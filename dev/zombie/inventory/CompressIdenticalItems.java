// 
// Decompiled by Procyon v0.5.36
// 

package zombie.inventory;

import java.util.ArrayDeque;
import java.util.HashMap;
import zombie.characters.IsoGameCharacter;
import java.util.ArrayList;
import zombie.inventory.types.InventoryContainer;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

public final class CompressIdenticalItems
{
    private static final int BLOCK_SIZE = 1024;
    private static final ThreadLocal<PerThreadData> perThreadVars;
    
    private static int bufferSize(final int n) {
        return (n + 1024 - 1) / 1024 * 1024;
    }
    
    private static ByteBuffer ensureCapacity(ByteBuffer allocate, final int n) {
        if (allocate == null || allocate.capacity() < n) {
            allocate = ByteBuffer.allocate(bufferSize(n));
        }
        return allocate;
    }
    
    private static ByteBuffer ensureCapacity(final ByteBuffer byteBuffer) {
        if (byteBuffer == null) {
            return ByteBuffer.allocate(1024);
        }
        if (byteBuffer.capacity() - byteBuffer.position() < 1024) {
            return ensureCapacity(null, byteBuffer.position() + 1024).put(byteBuffer.array(), 0, byteBuffer.position());
        }
        return ensureCapacity(null, byteBuffer.capacity() + 1024).put(byteBuffer.array(), 0, byteBuffer.position());
    }
    
    private static boolean setCompareItem(final PerThreadData perThreadData, final InventoryItem inventoryItem) throws IOException {
        ByteBuffer itemCompareBuffer = perThreadData.itemCompareBuffer;
        itemCompareBuffer.clear();
        final int id = inventoryItem.id;
        inventoryItem.id = 0;
        while (true) {
            try {
                itemCompareBuffer.putInt(0);
                inventoryItem.save(itemCompareBuffer, false);
                final int position = itemCompareBuffer.position();
                itemCompareBuffer.position(0);
                itemCompareBuffer.putInt(position);
                itemCompareBuffer.position(position);
            }
            catch (BufferOverflowException ex) {
                itemCompareBuffer = ensureCapacity(itemCompareBuffer);
                itemCompareBuffer.clear();
                perThreadData.itemCompareBuffer = itemCompareBuffer;
                continue;
            }
            finally {
                inventoryItem.id = id;
            }
            break;
        }
        return true;
    }
    
    private static boolean areItemsIdentical(final PerThreadData perThreadData, final InventoryItem inventoryItem, final InventoryItem inventoryItem2) throws IOException {
        if (inventoryItem instanceof InventoryContainer) {
            final ItemContainer inventory = ((InventoryContainer)inventoryItem).getInventory();
            final ItemContainer inventory2 = ((InventoryContainer)inventoryItem2).getInventory();
            if (!inventory.getItems().isEmpty() || !inventory2.getItems().isEmpty()) {
                return false;
            }
        }
        final ByteBuffer byteData = inventoryItem.getByteData();
        final ByteBuffer byteData2 = inventoryItem2.getByteData();
        if (byteData != null) {
            assert byteData.position() == 0;
            if (!byteData.equals(byteData2)) {
                return false;
            }
        }
        else if (byteData2 != null) {
            return false;
        }
        ByteBuffer itemCompareBuffer = null;
        final int id = inventoryItem2.id;
        inventoryItem2.id = 0;
        while (true) {
            try {
                itemCompareBuffer = perThreadData.itemCompareBuffer;
                itemCompareBuffer.position(0);
                final int int1 = itemCompareBuffer.getInt();
                final int position = itemCompareBuffer.position();
                itemCompareBuffer.position(int1);
                final int position2 = itemCompareBuffer.position();
                inventoryItem2.save(itemCompareBuffer, false);
                if (itemCompareBuffer.position() - position2 != int1 - position) {
                    return false;
                }
                for (int i = 0; i < int1 - position; ++i) {
                    if (itemCompareBuffer.get(position + i) != itemCompareBuffer.get(position2 + i)) {
                        return false;
                    }
                }
            }
            catch (BufferOverflowException ex) {
                itemCompareBuffer = ensureCapacity(itemCompareBuffer);
                itemCompareBuffer.clear();
                perThreadData.itemCompareBuffer = itemCompareBuffer;
                setCompareItem(perThreadData, inventoryItem);
                continue;
            }
            finally {
                inventoryItem2.id = id;
            }
            break;
        }
        return true;
    }
    
    public static ArrayList<InventoryItem> save(final ByteBuffer byteBuffer, final ArrayList<InventoryItem> list, final IsoGameCharacter isoGameCharacter) throws IOException {
        final PerThreadData perThreadData = CompressIdenticalItems.perThreadVars.get();
        final PerCallData allocSaveVars = perThreadData.allocSaveVars();
        final HashMap<String, ArrayList<InventoryItem>> typeToItems = allocSaveVars.typeToItems;
        final ArrayList<String> types = allocSaveVars.types;
        try {
            for (int i = 0; i < list.size(); ++i) {
                final String fullType = list.get(i).getFullType();
                if (!typeToItems.containsKey(fullType)) {
                    typeToItems.put(fullType, allocSaveVars.allocItemList());
                    types.add(fullType);
                }
                typeToItems.get(fullType).add(list.get(i));
            }
            final int position = byteBuffer.position();
            byteBuffer.putShort((short)0);
            int n = 0;
            for (int j = 0; j < types.size(); ++j) {
                final ArrayList<InventoryItem> list2 = typeToItems.get(types.get(j));
                for (int k = 0; k < list2.size(); ++k) {
                    final InventoryItem e = list2.get(k);
                    allocSaveVars.savedItems.add(e);
                    int n2 = 1;
                    final int n3 = k + 1;
                    if (isoGameCharacter == null || !isoGameCharacter.isEquipped(e)) {
                        setCompareItem(perThreadData, e);
                        while (k + 1 < list2.size() && areItemsIdentical(perThreadData, e, list2.get(k + 1))) {
                            allocSaveVars.savedItems.add(list2.get(k + 1));
                            ++k;
                            ++n2;
                        }
                    }
                    byteBuffer.putInt(n2);
                    e.saveWithSize(byteBuffer, false);
                    if (n2 > 1) {
                        for (int l = n3; l <= k; ++l) {
                            byteBuffer.putInt(list2.get(l).id);
                        }
                    }
                    ++n;
                }
            }
            final int position2 = byteBuffer.position();
            byteBuffer.position(position);
            byteBuffer.putShort((short)n);
            byteBuffer.position(position2);
        }
        finally {
            allocSaveVars.next = perThreadData.saveVars;
            perThreadData.saveVars = allocSaveVars;
        }
        return allocSaveVars.savedItems;
    }
    
    public static ArrayList<InventoryItem> load(final ByteBuffer byteBuffer, final int n, final ArrayList<InventoryItem> list, final ArrayList<InventoryItem> list2) throws IOException {
        final PerThreadData perThreadData = CompressIdenticalItems.perThreadVars.get();
        final PerCallData allocSaveVars = perThreadData.allocSaveVars();
        if (list != null) {
            list.clear();
        }
        if (list2 != null) {
            list2.clear();
        }
        try {
            for (short short1 = byteBuffer.getShort(), n2 = 0; n2 < short1; ++n2) {
                int n3 = 1;
                if (n >= 149) {
                    n3 = byteBuffer.getInt();
                }
                else if (n >= 128) {
                    n3 = byteBuffer.getShort();
                }
                final int position = byteBuffer.position();
                InventoryItem e = InventoryItem.loadItem(byteBuffer, n);
                if (e == null) {
                    byteBuffer.position(byteBuffer.position() + ((n3 > 1) ? ((n3 - 1) * 4) : 0));
                    for (int i = 0; i < n3; ++i) {
                        if (list2 != null) {
                            list2.add(null);
                        }
                        allocSaveVars.savedItems.add(null);
                    }
                }
                else {
                    for (int j = 0; j < n3; ++j) {
                        if (j > 0) {
                            byteBuffer.position(position);
                            e = InventoryItem.loadItem(byteBuffer, n);
                        }
                        if (list != null) {
                            list.add(e);
                        }
                        if (list2 != null) {
                            list2.add(e);
                        }
                        allocSaveVars.savedItems.add(e);
                    }
                    if (n >= 128) {
                        for (int k = 1; k < n3; ++k) {
                            final int int1 = byteBuffer.getInt();
                            final InventoryItem inventoryItem = allocSaveVars.savedItems.get(allocSaveVars.savedItems.size() - n3 + k);
                            if (inventoryItem != null) {
                                inventoryItem.id = int1;
                            }
                        }
                    }
                }
            }
        }
        finally {
            allocSaveVars.next = perThreadData.saveVars;
            perThreadData.saveVars = allocSaveVars;
        }
        return allocSaveVars.savedItems;
    }
    
    public static void save(final ByteBuffer byteBuffer, final InventoryItem inventoryItem) throws IOException {
        byteBuffer.putShort((short)1);
        byteBuffer.putInt(1);
        inventoryItem.saveWithSize(byteBuffer, false);
    }
    
    static {
        perThreadVars = new ThreadLocal<PerThreadData>() {
            @Override
            protected PerThreadData initialValue() {
                return new PerThreadData();
            }
        };
    }
    
    private static class PerCallData
    {
        final ArrayList<String> types;
        final HashMap<String, ArrayList<InventoryItem>> typeToItems;
        final ArrayDeque<ArrayList<InventoryItem>> itemLists;
        final ArrayList<InventoryItem> savedItems;
        PerCallData next;
        
        private PerCallData() {
            this.types = new ArrayList<String>();
            this.typeToItems = new HashMap<String, ArrayList<InventoryItem>>();
            this.itemLists = new ArrayDeque<ArrayList<InventoryItem>>();
            this.savedItems = new ArrayList<InventoryItem>();
        }
        
        void reset() {
            for (int i = 0; i < this.types.size(); ++i) {
                final ArrayList<InventoryItem> e = this.typeToItems.get(this.types.get(i));
                e.clear();
                this.itemLists.push(e);
            }
            this.types.clear();
            this.typeToItems.clear();
            this.savedItems.clear();
        }
        
        ArrayList<InventoryItem> allocItemList() {
            if (this.itemLists.isEmpty()) {
                return new ArrayList<InventoryItem>();
            }
            return this.itemLists.pop();
        }
    }
    
    private static class PerThreadData
    {
        PerCallData saveVars;
        ByteBuffer itemCompareBuffer;
        
        private PerThreadData() {
            this.itemCompareBuffer = ByteBuffer.allocate(1024);
        }
        
        PerCallData allocSaveVars() {
            if (this.saveVars == null) {
                return new PerCallData();
            }
            final PerCallData saveVars = this.saveVars;
            saveVars.reset();
            this.saveVars = this.saveVars.next;
            return saveVars;
        }
    }
}

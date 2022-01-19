// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network;

import zombie.Lua.LuaManager;
import se.krka.kahlua.j2se.KahluaTableImpl;
import zombie.iso.IsoDirections;
import zombie.inventory.InventoryItem;
import zombie.GameWindow;
import java.util.HashSet;
import java.io.IOException;
import se.krka.kahlua.vm.KahluaTableIterator;
import java.nio.ByteBuffer;
import se.krka.kahlua.vm.KahluaTable;

public final class TableNetworkUtils
{
    private static final byte SBYT_NO_SAVE = -1;
    private static final byte SBYT_STRING = 0;
    private static final byte SBYT_DOUBLE = 1;
    private static final byte SBYT_TABLE = 2;
    private static final byte SBYT_BOOLEAN = 3;
    private static final byte SBYT_ITEM = 4;
    private static final byte SBYT_DIRECTION = 5;
    
    public static void save(final KahluaTable kahluaTable, final ByteBuffer byteBuffer) throws IOException {
        final KahluaTableIterator iterator = kahluaTable.iterator();
        int n = 0;
        while (iterator.advance()) {
            if (canSave(iterator.getKey(), iterator.getValue())) {
                ++n;
            }
        }
        final KahluaTableIterator iterator2 = kahluaTable.iterator();
        byteBuffer.putInt(n);
        while (iterator2.advance()) {
            final byte keyByte = getKeyByte(iterator2.getKey());
            final byte valueByte = getValueByte(iterator2.getValue());
            if (keyByte != -1) {
                if (valueByte == -1) {
                    continue;
                }
                save(byteBuffer, keyByte, iterator2.getKey());
                save(byteBuffer, valueByte, iterator2.getValue());
            }
        }
    }
    
    public static void saveSome(final KahluaTable kahluaTable, final ByteBuffer byteBuffer, final HashSet<?> set) throws IOException {
        final KahluaTableIterator iterator = kahluaTable.iterator();
        int n = 0;
        while (iterator.advance()) {
            if (set.contains(iterator.getKey()) && canSave(iterator.getKey(), iterator.getValue())) {
                ++n;
            }
        }
        final KahluaTableIterator iterator2 = kahluaTable.iterator();
        byteBuffer.putInt(n);
        while (iterator2.advance()) {
            if (!set.contains(iterator2.getKey())) {
                continue;
            }
            final byte keyByte = getKeyByte(iterator2.getKey());
            final byte valueByte = getValueByte(iterator2.getValue());
            if (keyByte == -1) {
                continue;
            }
            if (valueByte == -1) {
                continue;
            }
            save(byteBuffer, keyByte, iterator2.getKey());
            save(byteBuffer, valueByte, iterator2.getValue());
        }
    }
    
    private static void save(final ByteBuffer byteBuffer, final byte b, final Object o) throws IOException, RuntimeException {
        byteBuffer.put(b);
        if (b == 0) {
            GameWindow.WriteString(byteBuffer, (String)o);
        }
        else if (b == 1) {
            byteBuffer.putDouble((double)o);
        }
        else if (b == 3) {
            byteBuffer.put((byte)(((boolean)o) ? 1 : 0));
        }
        else if (b == 2) {
            save((KahluaTable)o, byteBuffer);
        }
        else if (b == 4) {
            ((InventoryItem)o).saveWithSize(byteBuffer, false);
        }
        else {
            if (b != 5) {
                throw new RuntimeException(invokedynamic(makeConcatWithConstants:(B)Ljava/lang/String;, b));
            }
            byteBuffer.put((byte)((IsoDirections)o).index());
        }
    }
    
    public static void load(final KahluaTable kahluaTable, final ByteBuffer byteBuffer) throws IOException {
        final int int1 = byteBuffer.getInt();
        kahluaTable.wipe();
        for (int i = 0; i < int1; ++i) {
            kahluaTable.rawset(load(byteBuffer, byteBuffer.get()), load(byteBuffer, byteBuffer.get()));
        }
    }
    
    public static Object load(final ByteBuffer byteBuffer, final byte b) throws IOException, RuntimeException {
        if (b == 0) {
            return GameWindow.ReadString(byteBuffer);
        }
        if (b == 1) {
            return byteBuffer.getDouble();
        }
        if (b == 3) {
            return byteBuffer.get() == 1;
        }
        if (b == 2) {
            final KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)LuaManager.platform.newTable();
            load((KahluaTable)kahluaTableImpl, byteBuffer);
            return kahluaTableImpl;
        }
        if (b == 4) {
            Object loadItem = null;
            try {
                loadItem = InventoryItem.loadItem(byteBuffer, 186);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            return loadItem;
        }
        if (b == 5) {
            return IsoDirections.fromIndex(byteBuffer.get());
        }
        throw new RuntimeException(invokedynamic(makeConcatWithConstants:(B)Ljava/lang/String;, b));
    }
    
    private static byte getKeyByte(final Object o) {
        if (o instanceof String) {
            return 0;
        }
        if (o instanceof Double) {
            return 1;
        }
        return -1;
    }
    
    private static byte getValueByte(final Object o) {
        if (o instanceof String) {
            return 0;
        }
        if (o instanceof Double) {
            return 1;
        }
        if (o instanceof Boolean) {
            return 3;
        }
        if (o instanceof KahluaTableImpl) {
            return 2;
        }
        if (o instanceof InventoryItem) {
            return 4;
        }
        if (o instanceof IsoDirections) {
            return 5;
        }
        return -1;
    }
    
    public static boolean canSave(final Object o, final Object o2) {
        return getKeyByte(o) != -1 && getValueByte(o2) != -1;
    }
}

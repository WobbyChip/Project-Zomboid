// 
// Decompiled by Procyon v0.5.36
// 

package zombie.inventory;

import zombie.iso.IsoWorld;
import zombie.audio.BaseSoundEmitter;
import java.util.ArrayList;

public final class ItemSoundManager
{
    private static final ArrayList<InventoryItem> items;
    private static final ArrayList<BaseSoundEmitter> emitters;
    private static final ArrayList<InventoryItem> toAdd;
    private static final ArrayList<InventoryItem> toRemove;
    private static final ArrayList<InventoryItem> toStopItems;
    private static final ArrayList<BaseSoundEmitter> toStopEmitters;
    
    public static void addItem(final InventoryItem inventoryItem) {
        if (inventoryItem == null || ItemSoundManager.items.contains(inventoryItem)) {
            return;
        }
        ItemSoundManager.toRemove.remove(inventoryItem);
        final int index = ItemSoundManager.toStopItems.indexOf(inventoryItem);
        if (index != -1) {
            ItemSoundManager.toStopItems.remove(index);
            final BaseSoundEmitter e = ItemSoundManager.toStopEmitters.remove(index);
            ItemSoundManager.items.add(inventoryItem);
            ItemSoundManager.emitters.add(e);
            return;
        }
        if (ItemSoundManager.toAdd.contains(inventoryItem)) {
            return;
        }
        ItemSoundManager.toAdd.add(inventoryItem);
    }
    
    public static void removeItem(final InventoryItem inventoryItem) {
        ItemSoundManager.toAdd.remove(inventoryItem);
        final int index = ItemSoundManager.items.indexOf(inventoryItem);
        if (inventoryItem == null || index == -1) {
            return;
        }
        if (ItemSoundManager.toRemove.contains(inventoryItem)) {
            return;
        }
        ItemSoundManager.toRemove.add(inventoryItem);
    }
    
    public static void removeItems(final ArrayList<InventoryItem> list) {
        for (int i = 0; i < list.size(); ++i) {
            removeItem(list.get(i));
        }
    }
    
    public static void update() {
        if (!ItemSoundManager.toStopItems.isEmpty()) {
            for (int i = 0; i < ItemSoundManager.toStopItems.size(); ++i) {
                final BaseSoundEmitter baseSoundEmitter = ItemSoundManager.toStopEmitters.get(i);
                baseSoundEmitter.stopAll();
                IsoWorld.instance.returnOwnershipOfEmitter(baseSoundEmitter);
            }
            ItemSoundManager.toStopItems.clear();
            ItemSoundManager.toStopEmitters.clear();
        }
        if (!ItemSoundManager.toAdd.isEmpty()) {
            for (int j = 0; j < ItemSoundManager.toAdd.size(); ++j) {
                final InventoryItem inventoryItem = ItemSoundManager.toAdd.get(j);
                assert !ItemSoundManager.items.contains(inventoryItem);
                ItemSoundManager.items.add(inventoryItem);
                final BaseSoundEmitter freeEmitter = IsoWorld.instance.getFreeEmitter();
                IsoWorld.instance.takeOwnershipOfEmitter(freeEmitter);
                ItemSoundManager.emitters.add(freeEmitter);
            }
            ItemSoundManager.toAdd.clear();
        }
        if (!ItemSoundManager.toRemove.isEmpty()) {
            for (int k = 0; k < ItemSoundManager.toRemove.size(); ++k) {
                final InventoryItem e = ItemSoundManager.toRemove.get(k);
                assert ItemSoundManager.items.contains(e);
                final int index = ItemSoundManager.items.indexOf(e);
                ItemSoundManager.items.remove(index);
                final BaseSoundEmitter e2 = ItemSoundManager.emitters.get(index);
                ItemSoundManager.emitters.remove(index);
                ItemSoundManager.toStopItems.add(e);
                ItemSoundManager.toStopEmitters.add(e2);
            }
            ItemSoundManager.toRemove.clear();
        }
        for (int l = 0; l < ItemSoundManager.items.size(); ++l) {
            final InventoryItem inventoryItem2 = ItemSoundManager.items.get(l);
            final BaseSoundEmitter baseSoundEmitter2 = ItemSoundManager.emitters.get(l);
            ItemContainer outermostContainer = inventoryItem2.getOutermostContainer();
            if (outermostContainer != null) {
                if (outermostContainer.containingItem != null && outermostContainer.containingItem.getWorldItem() != null) {
                    if (outermostContainer.containingItem.getWorldItem().getWorldObjectIndex() == -1) {
                        outermostContainer = null;
                    }
                }
                else if (outermostContainer.parent != null) {
                    if (outermostContainer.parent.getObjectIndex() == -1 && outermostContainer.parent.getMovingObjectIndex() == -1 && outermostContainer.parent.getStaticMovingObjectIndex() == -1) {
                        outermostContainer = null;
                    }
                }
                else {
                    outermostContainer = null;
                }
            }
            if (outermostContainer == null && (inventoryItem2.getWorldItem() == null || inventoryItem2.getWorldItem().getWorldObjectIndex() == -1)) {
                removeItem(inventoryItem2);
            }
            else {
                inventoryItem2.updateSound(baseSoundEmitter2);
                baseSoundEmitter2.tick();
            }
        }
    }
    
    public static void Reset() {
        ItemSoundManager.items.clear();
        ItemSoundManager.emitters.clear();
        ItemSoundManager.toAdd.clear();
        ItemSoundManager.toRemove.clear();
        ItemSoundManager.toStopItems.clear();
        ItemSoundManager.toStopEmitters.clear();
    }
    
    static {
        items = new ArrayList<InventoryItem>();
        emitters = new ArrayList<BaseSoundEmitter>();
        toAdd = new ArrayList<InventoryItem>();
        toRemove = new ArrayList<InventoryItem>();
        toStopItems = new ArrayList<InventoryItem>();
        toStopEmitters = new ArrayList<BaseSoundEmitter>();
    }
}

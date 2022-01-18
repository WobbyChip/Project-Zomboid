// 
// Decompiled by Procyon v0.5.36
// 

package zombie.inventory;

import zombie.iso.objects.IsoMannequin;
import zombie.iso.objects.IsoDeadBody;
import zombie.inventory.types.Clothing;
import zombie.characters.IsoGameCharacter;
import zombie.iso.IsoObject;
import zombie.vehicles.VehiclePart;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.scripting.objects.Item;
import zombie.debug.DebugLog;
import zombie.scripting.ScriptManager;
import zombie.network.GameClient;
import zombie.util.Type;
import zombie.inventory.types.DrainableComboItem;
import java.util.ArrayList;

public final class ItemUser
{
    private static final ArrayList<InventoryItem> tempItems;
    
    public static void UseItem(final InventoryItem inventoryItem) {
        final DrainableComboItem drainableComboItem = Type.tryCastTo(inventoryItem, DrainableComboItem.class);
        if (drainableComboItem != null) {
            drainableComboItem.setDelta(drainableComboItem.getDelta() - drainableComboItem.getUseDelta());
            if (drainableComboItem.uses > 1) {
                final int uses = drainableComboItem.uses - 1;
                drainableComboItem.uses = 1;
                CreateItem(drainableComboItem.getFullType(), ItemUser.tempItems);
                final int index = 0;
                if (index < ItemUser.tempItems.size()) {
                    final InventoryItem inventoryItem2 = ItemUser.tempItems.get(index);
                    inventoryItem2.setUses(uses);
                    AddItem(drainableComboItem, inventoryItem2);
                }
            }
            if (drainableComboItem.getDelta() <= 1.0E-4f) {
                drainableComboItem.setDelta(0.0f);
                if (drainableComboItem.getReplaceOnDeplete() != null) {
                    CreateItem(drainableComboItem.getReplaceOnDepleteFullType(), ItemUser.tempItems);
                    for (int i = 0; i < ItemUser.tempItems.size(); ++i) {
                        final InventoryItem inventoryItem3 = ItemUser.tempItems.get(i);
                        inventoryItem3.setFavorite(drainableComboItem.isFavorite());
                        AddItem(drainableComboItem, inventoryItem3);
                    }
                    RemoveItem(drainableComboItem);
                }
                else {
                    UseItem(drainableComboItem, false, false);
                }
            }
            drainableComboItem.updateWeight();
        }
        else {
            UseItem(inventoryItem, false, false);
        }
    }
    
    public static void UseItem(final InventoryItem conditionFromModData, final boolean b, final boolean b2) {
        if (!conditionFromModData.isDisappearOnUse() && !b) {
            return;
        }
        --conditionFromModData.uses;
        if (conditionFromModData.replaceOnUse != null && !b2 && !b) {
            String replaceOnUse = conditionFromModData.replaceOnUse;
            if (!replaceOnUse.contains(".")) {
                replaceOnUse = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, conditionFromModData.module, replaceOnUse);
            }
            CreateItem(replaceOnUse, ItemUser.tempItems);
            for (int i = 0; i < ItemUser.tempItems.size(); ++i) {
                final InventoryItem inventoryItem = ItemUser.tempItems.get(i);
                inventoryItem.setConditionFromModData(conditionFromModData);
                AddItem(conditionFromModData, inventoryItem);
                inventoryItem.setFavorite(conditionFromModData.isFavorite());
            }
        }
        if (conditionFromModData.uses <= 0) {
            if (conditionFromModData.keepOnDeplete) {
                return;
            }
            RemoveItem(conditionFromModData);
        }
        else if (GameClient.bClient && !conditionFromModData.isInPlayerInventory()) {
            GameClient.instance.sendItemStats(conditionFromModData);
        }
    }
    
    public static void CreateItem(final String s, final ArrayList<InventoryItem> list) {
        list.clear();
        final Item findItem = ScriptManager.instance.FindItem(s);
        if (findItem == null) {
            DebugLog.General.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
            return;
        }
        for (int count = findItem.getCount(), i = 0; i < count; ++i) {
            final InventoryItem createItem = InventoryItemFactory.CreateItem(s);
            if (createItem == null) {
                return;
            }
            list.add(createItem);
        }
    }
    
    public static void AddItem(final InventoryItem inventoryItem, final InventoryItem inventoryItem2) {
        IsoWorldInventoryObject worldItem = inventoryItem.getWorldItem();
        if (worldItem != null && worldItem.getWorldObjectIndex() == -1) {
            worldItem = null;
        }
        if (worldItem != null) {
            worldItem.getSquare().AddWorldInventoryItem(inventoryItem2, 0.0f, 0.0f, 0.0f, true);
            return;
        }
        if (inventoryItem.container != null) {
            final VehiclePart vehiclePart = inventoryItem.container.vehiclePart;
            if (!inventoryItem.isInPlayerInventory()) {
                if (GameClient.bClient) {
                    inventoryItem.container.addItemOnServer(inventoryItem2);
                }
            }
            inventoryItem.container.AddItem(inventoryItem2);
            if (vehiclePart != null) {
                vehiclePart.setContainerContentAmount(vehiclePart.getItemContainer().getCapacityWeight());
            }
        }
    }
    
    public static void RemoveItem(final InventoryItem o) {
        IsoWorldInventoryObject worldItem = o.getWorldItem();
        if (worldItem != null && worldItem.getWorldObjectIndex() == -1) {
            worldItem = null;
        }
        if (worldItem != null) {
            worldItem.getSquare().transmitRemoveItemFromSquare(worldItem);
            return;
        }
        if (o.container != null) {
            final IsoObject parent = o.container.parent;
            final VehiclePart vehiclePart = o.container.vehiclePart;
            if (parent instanceof IsoGameCharacter) {
                final IsoGameCharacter isoGameCharacter = (IsoGameCharacter)parent;
                if (o instanceof Clothing) {
                    ((Clothing)o).Unwear();
                }
                isoGameCharacter.removeFromHands(o);
                if (isoGameCharacter.getClothingItem_Back() == o) {
                    isoGameCharacter.setClothingItem_Back(null);
                }
            }
            else if (!o.isInPlayerInventory()) {
                if (GameClient.bClient) {
                    o.container.removeItemOnServer(o);
                }
            }
            o.container.Items.remove(o);
            o.container.setDirty(true);
            o.container.setDrawDirty(true);
            o.container = null;
            if (parent instanceof IsoDeadBody) {
                ((IsoDeadBody)parent).checkClothing(o);
            }
            if (parent instanceof IsoMannequin) {
                ((IsoMannequin)parent).checkClothing(o);
            }
            if (vehiclePart != null) {
                vehiclePart.setContainerContentAmount(vehiclePart.getItemContainer().getCapacityWeight());
            }
        }
    }
    
    static {
        tempItems = new ArrayList<InventoryItem>();
    }
}

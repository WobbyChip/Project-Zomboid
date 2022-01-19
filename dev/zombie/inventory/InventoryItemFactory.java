// 
// Decompiled by Procyon v0.5.36
// 

package zombie.inventory;

import zombie.world.ItemInfo;
import zombie.world.WorldDictionary;
import zombie.scripting.objects.Item;
import zombie.inventory.types.Radio;
import zombie.inventory.types.Moveable;
import zombie.inventory.types.Drainable;
import zombie.core.Rand;
import zombie.core.Core;
import zombie.network.GameClient;
import zombie.debug.DebugLog;
import zombie.scripting.ScriptManager;
import zombie.util.Type;
import zombie.inventory.types.Food;

public final class InventoryItemFactory
{
    public static InventoryItem CreateItem(final String s) {
        return CreateItem(s, 1.0f);
    }
    
    public static InventoryItem CreateItem(final String s, final Food food) {
        final InventoryItem createItem = CreateItem(s, 1.0f);
        final Food food2 = Type.tryCastTo(createItem, Food.class);
        if (food2 == null) {
            return null;
        }
        food2.setBaseHunger(food.getBaseHunger());
        food2.setHungChange(food.getHungChange());
        food2.setBoredomChange(food.getBoredomChange());
        food2.setUnhappyChange(food.getUnhappyChange());
        food2.setCarbohydrates(food.getCarbohydrates());
        food2.setLipids(food.getLipids());
        food2.setProteins(food.getProteins());
        food2.setCalories(food.getCalories());
        return createItem;
    }
    
    public static InventoryItem CreateItem(String s, final float usedDelta) {
        Item findItem = null;
        boolean b = false;
        String type = null;
        try {
            if (s.startsWith("Moveables.") && !s.equalsIgnoreCase("Moveables.Moveable")) {
                type = s.split("\\.")[1];
                b = true;
                s = "Moveables.Moveable";
            }
            findItem = ScriptManager.instance.FindItem(s);
        }
        catch (Exception ex) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        }
        if (findItem == null) {
            return null;
        }
        final InventoryItem instanceItem = findItem.InstanceItem(null);
        if (GameClient.bClient && (Core.getInstance().getPoisonousBerry() == null || Core.getInstance().getPoisonousBerry().isEmpty())) {
            Core.getInstance().setPoisonousBerry(GameClient.poisonousBerry);
        }
        if (GameClient.bClient && (Core.getInstance().getPoisonousMushroom() == null || Core.getInstance().getPoisonousMushroom().isEmpty())) {
            Core.getInstance().setPoisonousMushroom(GameClient.poisonousMushroom);
        }
        if (s.equals(Core.getInstance().getPoisonousBerry())) {
            ((Food)instanceItem).Poison = true;
            ((Food)instanceItem).setPoisonLevelForRecipe(1);
            ((Food)instanceItem).setPoisonDetectionLevel(1);
            ((Food)instanceItem).setPoisonPower(5);
            ((Food)instanceItem).setUseForPoison(new Float(Math.abs(((Food)instanceItem).getHungChange()) * 100.0f).intValue());
        }
        if (s.equals(Core.getInstance().getPoisonousMushroom())) {
            ((Food)instanceItem).Poison = true;
            ((Food)instanceItem).setPoisonLevelForRecipe(2);
            ((Food)instanceItem).setPoisonDetectionLevel(2);
            ((Food)instanceItem).setPoisonPower(10);
            ((Food)instanceItem).setUseForPoison(new Float(Math.abs(((Food)instanceItem).getHungChange()) * 100.0f).intValue());
        }
        instanceItem.id = Rand.Next(2146250223) + 1233423;
        if (instanceItem instanceof Drainable) {
            ((Drainable)instanceItem).setUsedDelta(usedDelta);
        }
        if (b) {
            instanceItem.type = type;
            instanceItem.fullType = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, instanceItem.module, type);
            if (instanceItem instanceof Moveable && !((Moveable)instanceItem).ReadFromWorldSprite(type) && instanceItem instanceof Radio) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (s != null) ? s : "unknown"));
            }
        }
        return instanceItem;
    }
    
    public static InventoryItem CreateItem(final String s, final float usedDelta, final String s2) {
        final Item item = ScriptManager.instance.getItem(s);
        if (item == null) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
            return null;
        }
        final InventoryItem instanceItem = item.InstanceItem(s2);
        if (instanceItem == null) {}
        if (instanceItem instanceof Drainable) {
            ((Drainable)instanceItem).setUsedDelta(usedDelta);
        }
        return instanceItem;
    }
    
    public static InventoryItem CreateItem(final String s, final String s2, final String s3, final String s4) {
        final InventoryItem inventoryItem = new InventoryItem(s, s2, s3, s4);
        inventoryItem.id = Rand.Next(2146250223) + 1233423;
        return inventoryItem;
    }
    
    public static InventoryItem CreateItem(final short n) {
        final ItemInfo itemInfoFromID = WorldDictionary.getItemInfoFromID(n);
        if (itemInfoFromID != null && itemInfoFromID.isValid()) {
            final String fullType = itemInfoFromID.getFullType();
            if (fullType != null) {
                final InventoryItem createItem = CreateItem(fullType);
                if (createItem != null) {
                    return createItem;
                }
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;S)Ljava/lang/String;, (fullType != null) ? fullType : "unknown", n));
            }
            else {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, n));
            }
        }
        else if (itemInfoFromID == null) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, n));
        }
        else {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, itemInfoFromID.ToString()));
        }
        return null;
    }
}

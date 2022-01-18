// 
// Decompiled by Procyon v0.5.36
// 

package zombie.inventory;

import zombie.util.Type;
import java.util.ArrayDeque;
import zombie.inventory.types.Drainable;
import zombie.characters.skills.PerkFactory;
import java.util.Stack;
import zombie.scripting.objects.EvolvedRecipe;
import zombie.network.GameClient;
import zombie.inventory.types.Food;
import java.util.Collection;
import java.util.Iterator;
import zombie.inventory.types.DrainableComboItem;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.Lua.LuaEventManager;
import zombie.scripting.objects.MovableRecipe;
import zombie.inventory.types.Moveable;
import zombie.inventory.types.Clothing;
import zombie.characters.IsoGameCharacter;
import zombie.Lua.LuaManager;
import zombie.util.StringUtils;
import zombie.debug.DebugLog;
import zombie.scripting.objects.ScriptModule;
import zombie.scripting.objects.Item;
import java.util.Set;
import java.util.HashSet;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Recipe;
import java.util.ArrayList;

public final class RecipeManager
{
    private static final ArrayList<Recipe> RecipeList;
    
    public static void Loaded() {
        final ArrayList<Recipe> allRecipes = ScriptManager.instance.getAllRecipes();
        final HashSet<String> set = new HashSet<String>();
        for (int i = 0; i < allRecipes.size(); ++i) {
            final Recipe recipe = allRecipes.get(i);
            for (int j = 0; j < recipe.getSource().size(); ++j) {
                final Recipe.Source source = recipe.getSource().get(j);
                for (int k = 0; k < source.getItems().size(); ++k) {
                    final String anObject = source.getItems().get(k);
                    if (!"Water".equals(anObject) && !anObject.contains(".") && !anObject.startsWith("[")) {
                        final Item resolveItemModuleDotType = resolveItemModuleDotType(recipe, anObject, set, "recipe source");
                        if (resolveItemModuleDotType == null) {
                            source.getItems().set(k, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, anObject));
                        }
                        else {
                            source.getItems().set(k, resolveItemModuleDotType.getFullName());
                        }
                    }
                }
            }
            if (recipe.getResult() != null && recipe.getResult().getModule() == null) {
                final Item resolveItemModuleDotType2 = resolveItemModuleDotType(recipe, recipe.getResult().getType(), set, "recipe result");
                if (resolveItemModuleDotType2 == null) {
                    recipe.getResult().module = "???";
                }
                else {
                    recipe.getResult().module = resolveItemModuleDotType2.getModule().getName();
                }
            }
        }
    }
    
    private static Item resolveItemModuleDotType(final Recipe recipe, final String s, final Set<String> set, final String s2) {
        final Item item = recipe.getModule().getItem(s);
        if (item != null && !item.getObsolete()) {
            return item;
        }
        for (int i = 0; i < ScriptManager.instance.ModuleList.size(); ++i) {
            final Item item2 = ScriptManager.instance.ModuleList.get(i).getItem(s);
            if (item2 != null && !item2.getObsolete()) {
                final String name = recipe.getModule().getName();
                if (!set.contains(name)) {
                    set.add(name);
                    DebugLog.Recipe.warn("WARNING: module \"%s\" may have forgot to import module Base", name);
                }
                return item2;
            }
        }
        DebugLog.Recipe.warn("ERROR: can't find %s \"%s\" in recipe \"%s\"", s2, s, recipe.getOriginalname());
        return null;
    }
    
    public static void LoadedAfterLua() {
        final ArrayList<Item> list = new ArrayList<Item>();
        final ArrayList<Recipe> allRecipes = ScriptManager.instance.getAllRecipes();
        for (int i = 0; i < allRecipes.size(); ++i) {
            LoadedAfterLua(allRecipes.get(i), list);
        }
        list.clear();
    }
    
    private static void LoadedAfterLua(final Recipe recipe, final ArrayList<Item> list) {
        LoadedAfterLua(recipe, recipe.LuaCreate, "LuaCreate");
        LoadedAfterLua(recipe, recipe.LuaGiveXP, "LuaGiveXP");
        LoadedAfterLua(recipe, recipe.LuaTest, "LuaTest");
        for (int i = 0; i < recipe.getSource().size(); ++i) {
            LoadedAfterLua(recipe.getSource().get(i), list);
        }
    }
    
    private static void LoadedAfterLua(final Recipe recipe, final String s, final String s2) {
        if (StringUtils.isNullOrWhitespace(s)) {
            return;
        }
        if (LuaManager.getFunctionObject(s) == null) {
            DebugLog.General.error("no such function %s = \"%s\" in recipe \"%s\"", s2, s, recipe.name);
        }
    }
    
    private static void LoadedAfterLua(final Recipe.Source source, final ArrayList<Item> list) {
        for (int i = source.getItems().size() - 1; i >= 0; --i) {
            final String s = source.getItems().get(i);
            if (s.startsWith("[")) {
                source.getItems().remove(i);
                final Object functionObject = LuaManager.getFunctionObject(s.substring(1, s.indexOf("]")));
                if (functionObject != null) {
                    list.clear();
                    LuaManager.caller.protectedCallVoid(LuaManager.thread, functionObject, (Object)list);
                    for (int j = 0; j < list.size(); ++j) {
                        source.getItems().add(i + j, list.get(j).getFullName());
                    }
                }
            }
        }
    }
    
    public static boolean DoesWipeUseDelta(final String s, final String s2) {
        return true;
    }
    
    public static int getKnownRecipesNumber(final IsoGameCharacter isoGameCharacter) {
        int n = 0;
        final ArrayList<Recipe> allRecipes = ScriptManager.instance.getAllRecipes();
        for (int i = 0; i < allRecipes.size(); ++i) {
            if (isoGameCharacter.isRecipeKnown(allRecipes.get(i))) {
                ++n;
            }
        }
        return n;
    }
    
    public static boolean DoesUseItemUp(final String anObject, final Recipe recipe) {
        assert "Water".equals(anObject) || anObject.contains(".");
        for (int i = 0; i < recipe.Source.size(); ++i) {
            if (recipe.Source.get(i).keep) {
                final ArrayList<String> items = recipe.Source.get(i).getItems();
                for (int j = 0; j < items.size(); ++j) {
                    if (anObject.equals(items.get(j))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    public static boolean IsItemDestroyed(final String anObject, final Recipe recipe) {
        assert "Water".equals(anObject) || anObject.contains(".");
        for (int i = 0; i < recipe.Source.size(); ++i) {
            final Recipe.Source source = recipe.getSource().get(i);
            if (source.destroy) {
                for (int j = 0; j < source.getItems().size(); ++j) {
                    if (anObject.equals(source.getItems().get(j))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public static float UseAmount(final String s, final Recipe recipe, final IsoGameCharacter isoGameCharacter) {
        return recipe.findSource(s).getCount();
    }
    
    public static ArrayList<Recipe> getUniqueRecipeItems(final InventoryItem inventoryItem, final IsoGameCharacter isoGameCharacter, final ArrayList<ItemContainer> list) {
        RecipeManager.RecipeList.clear();
        final ArrayList<Recipe> allRecipes = ScriptManager.instance.getAllRecipes();
        for (int i = 0; i < allRecipes.size(); ++i) {
            final Recipe e = allRecipes.get(i);
            if (IsRecipeValid(e, isoGameCharacter, inventoryItem, list)) {
                if (!(inventoryItem instanceof Clothing) || inventoryItem.getCondition() > 0 || !e.getOriginalname().equalsIgnoreCase("rip clothing")) {
                    RecipeManager.RecipeList.add(e);
                }
            }
        }
        if (inventoryItem instanceof Moveable && RecipeManager.RecipeList.size() == 0 && ((Moveable)inventoryItem).getWorldSprite() != null) {
            if (inventoryItem.type != null && inventoryItem.type.equalsIgnoreCase(((Moveable)inventoryItem).getWorldSprite())) {
                final MovableRecipe e2 = new MovableRecipe();
                LuaEventManager.triggerEvent("OnDynamicMovableRecipe", ((Moveable)inventoryItem).getWorldSprite(), e2, inventoryItem, isoGameCharacter);
                if (e2.isValid() && IsRecipeValid(e2, isoGameCharacter, inventoryItem, list)) {
                    RecipeManager.RecipeList.add(e2);
                }
            }
            else {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, inventoryItem.getFullType()));
            }
        }
        return RecipeManager.RecipeList;
    }
    
    public static boolean IsRecipeValid(final Recipe recipe, final IsoGameCharacter isoGameCharacter, final InventoryItem inventoryItem, final ArrayList<ItemContainer> list) {
        return recipe.Result != null && isoGameCharacter.isRecipeKnown(recipe) && (inventoryItem == null || RecipeContainsItem(recipe, inventoryItem)) && HasAllRequiredItems(recipe, isoGameCharacter, inventoryItem, list) && HasRequiredSkill(recipe, isoGameCharacter) && isNearItem(recipe, isoGameCharacter) && hasHeat(recipe, inventoryItem, list, isoGameCharacter) && CanPerform(recipe, isoGameCharacter, inventoryItem);
    }
    
    private static boolean isNearItem(final Recipe recipe, final IsoGameCharacter isoGameCharacter) {
        if (recipe.getNearItem() == null || recipe.getNearItem().equals("")) {
            return true;
        }
        for (int i = isoGameCharacter.getSquare().getX() - 2; i < isoGameCharacter.getSquare().getX() + 2; ++i) {
            for (int j = isoGameCharacter.getSquare().getY() - 2; j < isoGameCharacter.getSquare().getY() + 2; ++j) {
                final IsoGridSquare gridSquare = isoGameCharacter.getCell().getGridSquare(i, j, 0);
                if (gridSquare != null) {
                    for (int k = 0; k < gridSquare.getObjects().size(); ++k) {
                        if (recipe.getNearItem().equals(gridSquare.getObjects().get(k).getName())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    private static boolean CanPerform(final Recipe recipe, final IsoGameCharacter isoGameCharacter, final InventoryItem inventoryItem) {
        if (StringUtils.isNullOrWhitespace(recipe.getCanPerform())) {
            return true;
        }
        final Object functionObject = LuaManager.getFunctionObject(recipe.getCanPerform());
        return functionObject != null && LuaManager.caller.protectedCallBoolean(LuaManager.thread, functionObject, (Object)recipe, (Object)isoGameCharacter, (Object)inventoryItem) == Boolean.TRUE;
    }
    
    private static boolean HasRequiredSkill(final Recipe recipe, final IsoGameCharacter isoGameCharacter) {
        if (recipe.getRequiredSkillCount() == 0) {
            return true;
        }
        for (int i = 0; i < recipe.getRequiredSkillCount(); ++i) {
            final Recipe.RequiredSkill requiredSkill = recipe.getRequiredSkill(i);
            if (isoGameCharacter.getPerkLevel(requiredSkill.getPerk()) < requiredSkill.getLevel()) {
                return false;
            }
        }
        return true;
    }
    
    private static boolean RecipeContainsItem(final Recipe recipe, final InventoryItem inventoryItem) {
        for (int i = 0; i < recipe.Source.size(); ++i) {
            final Recipe.Source source = recipe.getSource().get(i);
            for (int j = 0; j < source.getItems().size(); ++j) {
                final String anObject = source.getItems().get(j);
                if ("Water".equals(anObject) && inventoryItem.isWaterSource()) {
                    return true;
                }
                if (anObject.equals(inventoryItem.getFullType())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean HasAllRequiredItems(final Recipe recipe, final IsoGameCharacter isoGameCharacter, final InventoryItem inventoryItem, final ArrayList<ItemContainer> list) {
        return !getAvailableItemsNeeded(recipe, isoGameCharacter, list, inventoryItem, null).isEmpty();
    }
    
    public static boolean hasHeat(final Recipe recipe, final InventoryItem inventoryItem, final ArrayList<ItemContainer> list, final IsoGameCharacter isoGameCharacter) {
        if (recipe.getHeat() != 0.0f) {
            InventoryItem inventoryItem2 = null;
            for (final InventoryItem inventoryItem3 : getAvailableItemsNeeded(recipe, isoGameCharacter, list, inventoryItem, null)) {
                if (inventoryItem3 instanceof DrainableComboItem) {
                    inventoryItem2 = inventoryItem3;
                    break;
                }
            }
            if (inventoryItem2 != null) {
                final Iterator<ItemContainer> iterator2 = list.iterator();
                while (iterator2.hasNext()) {
                    for (final InventoryItem inventoryItem4 : iterator2.next().getItems()) {
                        if (inventoryItem4.getName().equals(inventoryItem2.getName())) {
                            if (recipe.getHeat() < 0.0f) {
                                if (inventoryItem4.getInvHeat() <= recipe.getHeat()) {
                                    return true;
                                }
                                continue;
                            }
                            else {
                                if (recipe.getHeat() > 0.0f && inventoryItem4.getInvHeat() + 1.0f >= recipe.getHeat()) {
                                    return true;
                                }
                                continue;
                            }
                        }
                    }
                }
            }
            return false;
        }
        return true;
    }
    
    public static ArrayList<InventoryItem> getAvailableItemsAll(final Recipe recipe, final IsoGameCharacter isoGameCharacter, final ArrayList<ItemContainer> list, final InventoryItem inventoryItem, final ArrayList<InventoryItem> list2) {
        return getAvailableItems(recipe, isoGameCharacter, list, inventoryItem, list2, true).allItems;
    }
    
    public static ArrayList<InventoryItem> getAvailableItemsNeeded(final Recipe recipe, final IsoGameCharacter isoGameCharacter, final ArrayList<ItemContainer> list, final InventoryItem inventoryItem, final ArrayList<InventoryItem> list2) {
        return getAvailableItems(recipe, isoGameCharacter, list, inventoryItem, list2, false).allItems;
    }
    
    private static SourceItems getAvailableItems(final Recipe recipe, final IsoGameCharacter isoGameCharacter, ArrayList<ItemContainer> list, InventoryItem inventoryItem, final ArrayList<InventoryItem> list2, final boolean b) {
        if (inventoryItem != null && (inventoryItem.getContainer() == null || !inventoryItem.getContainer().contains(inventoryItem))) {
            DebugLog.Recipe.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, inventoryItem.getFullType()));
            inventoryItem = null;
        }
        final SourceItems sourceItems = new SourceItems(recipe, isoGameCharacter, inventoryItem, list2);
        if (list == null) {
            list = new ArrayList<ItemContainer>();
            list.add(isoGameCharacter.getInventory());
        }
        if (inventoryItem != null && !RecipeContainsItem(recipe, inventoryItem)) {
            throw new RuntimeException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, inventoryItem.getFullType(), recipe.getOriginalname()));
        }
        final RMRecipe alloc = RMRecipe.alloc(recipe);
        alloc.getItemsFromContainers(isoGameCharacter, list, inventoryItem);
        if (b || alloc.hasItems()) {
            alloc.getAvailableItems(sourceItems, b);
        }
        RMRecipe.release(alloc);
        return sourceItems;
    }
    
    public static ArrayList<InventoryItem> getSourceItemsAll(final Recipe recipe, final int n, final IsoGameCharacter isoGameCharacter, final ArrayList<ItemContainer> list, final InventoryItem inventoryItem, final ArrayList<InventoryItem> list2) {
        if (n < 0 || n >= recipe.getSource().size()) {
            return null;
        }
        return getAvailableItems(recipe, isoGameCharacter, list, inventoryItem, list2, true).itemsPerSource[n];
    }
    
    public static ArrayList<InventoryItem> getSourceItemsNeeded(final Recipe recipe, final int n, final IsoGameCharacter isoGameCharacter, final ArrayList<ItemContainer> list, final InventoryItem inventoryItem, final ArrayList<InventoryItem> list2) {
        if (n < 0 || n >= recipe.getSource().size()) {
            return null;
        }
        return getAvailableItems(recipe, isoGameCharacter, list, inventoryItem, list2, false).itemsPerSource[n];
    }
    
    public static int getNumberOfTimesRecipeCanBeDone(final Recipe recipe, final IsoGameCharacter isoGameCharacter, ArrayList<ItemContainer> list, final InventoryItem inventoryItem) {
        int n = 0;
        final RMRecipe alloc = RMRecipe.alloc(recipe);
        if (list == null) {
            list = new ArrayList<ItemContainer>();
            list.add(isoGameCharacter.getInventory());
        }
        alloc.getItemsFromContainers(isoGameCharacter, list, inventoryItem);
        final ArrayList list2 = new ArrayList();
        final ArrayList<InventoryItem> list3 = new ArrayList<InventoryItem>();
        while (alloc.hasItems()) {
            list3.clear();
            alloc.Use(list3);
            if (list2.containsAll(list3)) {
                n = -1;
                break;
            }
            list2.addAll(list3);
            for (int i = 0; i < list3.size(); ++i) {
                final InventoryItem inventoryItem2 = list3.get(i);
                if (inventoryItem2 instanceof Food && ((Food)inventoryItem2).isFrozen()) {
                    --n;
                    break;
                }
            }
            ++n;
        }
        RMRecipe.release(alloc);
        return n;
    }
    
    public static InventoryItem GetMovableRecipeTool(final boolean b, final Recipe recipe, final InventoryItem inventoryItem, final IsoGameCharacter isoGameCharacter, final ArrayList<ItemContainer> list) {
        if (!(recipe instanceof MovableRecipe)) {
            return null;
        }
        final MovableRecipe movableRecipe = (MovableRecipe)recipe;
        final Recipe.Source source = b ? movableRecipe.getPrimaryTools() : movableRecipe.getSecondaryTools();
        if (source == null || source.getItems() == null || source.getItems().size() == 0) {
            return null;
        }
        final SourceItems availableItems = getAvailableItems(recipe, isoGameCharacter, list, inventoryItem, null, false);
        if (availableItems.allItems == null || availableItems.allItems.size() == 0) {
            return null;
        }
        for (int i = 0; i < availableItems.allItems.size(); ++i) {
            final InventoryItem inventoryItem2 = availableItems.allItems.get(i);
            for (int j = 0; j < source.getItems().size(); ++j) {
                if (inventoryItem2.getFullType().equalsIgnoreCase(source.getItems().get(j))) {
                    return inventoryItem2;
                }
            }
        }
        return null;
    }
    
    public static InventoryItem PerformMakeItem(final Recipe recipe, final InventoryItem inventoryItem, final IsoGameCharacter isoGameCharacter, final ArrayList<ItemContainer> list) {
        final boolean b = isoGameCharacter.getPrimaryHandItem() == inventoryItem;
        final boolean b2 = isoGameCharacter.getSecondaryHandItem() == inventoryItem;
        final SourceItems availableItems = getAvailableItems(recipe, isoGameCharacter, list, inventoryItem, null, false);
        final ArrayList<InventoryItem> allItems = availableItems.allItems;
        if (allItems.isEmpty()) {
            throw new RuntimeException("getAvailableItems() didn't return the required number of items");
        }
        isoGameCharacter.removeFromHands(inventoryItem);
        final InventoryItem createItem = InventoryItemFactory.CreateItem(recipe.getResult().getFullType());
        boolean cooked = false;
        boolean burnt = false;
        int poisonDetectionLevel = -1;
        int poisonPower = 0;
        boolean b3 = false;
        boolean b4 = false;
        float n = 0.0f;
        float n2 = 0.0f;
        int n3 = 0;
        int n4 = 0;
        for (int i = 0; i < recipe.getSource().size(); ++i) {
            final Recipe.Source source = recipe.getSource().get(i);
            if (!source.isKeep()) {
                final ArrayList<InventoryItem> list2 = availableItems.itemsPerSource[i];
                switch (availableItems.typePerSource[i]) {
                    case DRAINABLE: {
                        int n5 = (int)source.getCount();
                        for (int j = 0; j < list2.size(); ++j) {
                            final InventoryItem inventoryItem2 = list2.get(j);
                            final int availableUses = AvailableUses(inventoryItem2);
                            if (availableUses >= n5) {
                                ReduceUses(inventoryItem2, (float)n5, isoGameCharacter);
                                n5 = 0;
                            }
                            else {
                                ReduceUses(inventoryItem2, (float)availableUses, isoGameCharacter);
                                n5 -= availableUses;
                            }
                        }
                        if (n5 > 0) {
                            throw new RuntimeException(invokedynamic(makeConcatWithConstants:(Ljava/util/ArrayList;)Ljava/lang/String;, source.getItems()));
                        }
                        break;
                    }
                    case FOOD: {
                        int n6 = (int)source.use;
                        for (int k = 0; k < list2.size(); ++k) {
                            final InventoryItem inventoryItem3 = list2.get(k);
                            final int availableUses2 = AvailableUses(inventoryItem3);
                            if (availableUses2 >= n6) {
                                ReduceUses(inventoryItem3, (float)n6, isoGameCharacter);
                                n6 = 0;
                            }
                            else {
                                ReduceUses(inventoryItem3, (float)availableUses2, isoGameCharacter);
                                n6 -= availableUses2;
                            }
                        }
                        break;
                    }
                    case DESTROY: {
                        for (int l = 0; l < list2.size(); ++l) {
                            ItemUser.RemoveItem(list2.get(l));
                        }
                        break;
                    }
                    case OTHER: {
                        for (int index = 0; index < list2.size(); ++index) {
                            ItemUser.UseItem(list2.get(index), true, false);
                        }
                        break;
                    }
                    case WATER: {
                        int waterAmountNeeded = recipe.getWaterAmountNeeded();
                        for (int index2 = 0; index2 < list2.size(); ++index2) {
                            final InventoryItem inventoryItem4 = list2.get(index2);
                            final int availableUses3 = AvailableUses(inventoryItem4);
                            if (availableUses3 >= waterAmountNeeded) {
                                ReduceUses(inventoryItem4, (float)waterAmountNeeded, isoGameCharacter);
                                waterAmountNeeded = 0;
                            }
                            else {
                                ReduceUses(inventoryItem4, (float)availableUses3, isoGameCharacter);
                                waterAmountNeeded -= availableUses3;
                            }
                        }
                        if (waterAmountNeeded > 0) {
                            throw new RuntimeException("required amount of water wasn't available");
                        }
                        break;
                    }
                }
            }
        }
        for (int index3 = 0; index3 < allItems.size(); ++index3) {
            final InventoryItem inventoryItem5 = allItems.get(index3);
            if (inventoryItem5 instanceof Food) {
                if (((Food)inventoryItem5).isCooked()) {
                    cooked = true;
                }
                if (((Food)inventoryItem5).isBurnt()) {
                    burnt = true;
                }
                poisonDetectionLevel = ((Food)inventoryItem5).getPoisonDetectionLevel();
                poisonPower = ((Food)inventoryItem5).getPoisonPower();
                ++n4;
                if (inventoryItem5.getAge() > inventoryItem5.getOffAgeMax()) {
                    b3 = true;
                }
                else if (!b3 && inventoryItem5.getOffAgeMax() < 1000000000) {
                    if (inventoryItem5.getAge() < inventoryItem5.getOffAge()) {
                        n2 += 0.5f * inventoryItem5.getAge() / inventoryItem5.getOffAge();
                    }
                    else {
                        b4 = true;
                        n2 += 0.5f + 0.5f * (inventoryItem5.getAge() - inventoryItem5.getOffAge()) / (inventoryItem5.getOffAgeMax() - inventoryItem5.getOffAge());
                    }
                }
            }
            if (createItem instanceof Food && inventoryItem5.isTaintedWater()) {
                createItem.setTaintedWater(true);
            }
            if (createItem.getScriptItem() == inventoryItem5.getScriptItem() && inventoryItem5.isFavorite()) {
                createItem.setFavorite(true);
            }
            n += inventoryItem5.getCondition() / (float)inventoryItem5.getConditionMax();
            ++n3;
        }
        float n7 = n2 / n4;
        if (createItem instanceof Food && ((Food)createItem).IsCookable) {
            ((Food)createItem).setCooked(cooked);
            ((Food)createItem).setBurnt(burnt);
            ((Food)createItem).setPoisonDetectionLevel(poisonDetectionLevel);
            ((Food)createItem).setPoisonPower(poisonPower);
        }
        if (createItem.getOffAgeMax() != 1.0E9) {
            if (b3) {
                createItem.setAge((float)createItem.getOffAgeMax());
            }
            else {
                if (b4 && n7 < 0.5f) {
                    n7 = 0.5f;
                }
                if (n7 < 0.5f) {
                    createItem.setAge(2.0f * n7 * createItem.getOffAge());
                }
                else {
                    createItem.setAge(createItem.getOffAge() + 2.0f * (n7 - 0.5f) * (createItem.getOffAgeMax() - createItem.getOffAge()));
                }
            }
        }
        createItem.setCondition(Math.round(createItem.getConditionMax() * (n / n3)));
        for (int index4 = 0; index4 < allItems.size(); ++index4) {
            createItem.setConditionFromModData(allItems.get(index4));
        }
        GivePlayerExperience(recipe, allItems, createItem, isoGameCharacter);
        if (recipe.LuaCreate != null) {
            final Object functionObject = LuaManager.getFunctionObject(recipe.LuaCreate);
            if (functionObject != null) {
                LuaManager.caller.protectedCall(LuaManager.thread, functionObject, new Object[] { allItems, createItem, isoGameCharacter, inventoryItem, b, b2 });
            }
        }
        if (!recipe.isRemoveResultItem()) {
            return createItem;
        }
        return null;
    }
    
    private static boolean ReduceUses(final InventoryItem inventoryItem, final float b, final IsoGameCharacter isoGameCharacter) {
        if (inventoryItem instanceof DrainableComboItem) {
            final DrainableComboItem drainableComboItem = (DrainableComboItem)inventoryItem;
            drainableComboItem.setUsedDelta(drainableComboItem.getUsedDelta() - drainableComboItem.getUseDelta() * b);
            if (AvailableUses(inventoryItem) < 1) {
                drainableComboItem.setUsedDelta(0.0f);
                ItemUser.UseItem(drainableComboItem);
                return true;
            }
            if (GameClient.bClient && !inventoryItem.isInPlayerInventory()) {
                GameClient.instance.sendItemStats(inventoryItem);
            }
        }
        if (inventoryItem instanceof Food) {
            final Food food = (Food)inventoryItem;
            if (food.getHungerChange() < 0.0f) {
                float n = Math.min(-food.getHungerChange() * 100.0f, b) / (-food.getHungerChange() * 100.0f);
                if (n < 0.0f) {
                    n = 0.0f;
                }
                if (n > 1.0f) {
                    n = 1.0f;
                }
                food.setHungChange(food.getHungChange() - food.getHungChange() * n);
                food.setCalories(food.getCalories() - food.getCalories() * n);
                food.setCarbohydrates(food.getCarbohydrates() - food.getCarbohydrates() * n);
                food.setLipids(food.getLipids() - food.getLipids() * n);
                food.setProteins(food.getProteins() - food.getProteins() * n);
                food.setThirstChange(food.getThirstChange() - food.getThirstChange() * n);
                food.setFluReduction(food.getFluReduction() - (int)(food.getFluReduction() * n));
                food.setPainReduction(food.getPainReduction() - food.getPainReduction() * n);
                food.setEndChange(food.getEnduranceChange() - food.getEnduranceChange() * n);
                food.setReduceFoodSickness(food.getReduceFoodSickness() - (int)(food.getReduceFoodSickness() * n));
                food.setStressChange(food.getStressChange() - food.getStressChange() * n);
                food.setFatigueChange(food.getFatigueChange() - food.getFatigueChange() * n);
                if (food.getHungerChange() > -0.01) {
                    ItemUser.UseItem(food);
                    return true;
                }
                if (GameClient.bClient && !inventoryItem.isInPlayerInventory()) {
                    GameClient.instance.sendItemStats(inventoryItem);
                }
            }
        }
        return false;
    }
    
    private static int AvailableUses(final InventoryItem inventoryItem) {
        if (inventoryItem instanceof DrainableComboItem) {
            return ((DrainableComboItem)inventoryItem).getDrainableUsesInt();
        }
        if (inventoryItem instanceof Food) {
            return (int)(-((Food)inventoryItem).getHungerChange() * 100.0f);
        }
        return 0;
    }
    
    private static void GivePlayerExperience(final Recipe recipe, final ArrayList<InventoryItem> list, final InventoryItem inventoryItem, final IsoGameCharacter isoGameCharacter) {
        String luaGiveXP = recipe.LuaGiveXP;
        if (luaGiveXP == null) {
            luaGiveXP = "Recipe.OnGiveXP.Default";
        }
        final Object functionObject = LuaManager.getFunctionObject(luaGiveXP);
        if (functionObject == null) {
            DebugLog.Recipe.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, luaGiveXP));
            return;
        }
        LuaManager.caller.protectedCall(LuaManager.thread, functionObject, new Object[] { recipe, list, inventoryItem, isoGameCharacter });
    }
    
    public static ArrayList<EvolvedRecipe> getAllEvolvedRecipes() {
        final Stack<EvolvedRecipe> allEvolvedRecipes = ScriptManager.instance.getAllEvolvedRecipes();
        final ArrayList<EvolvedRecipe> list = new ArrayList<EvolvedRecipe>();
        for (int i = 0; i < allEvolvedRecipes.size(); ++i) {
            list.add((EvolvedRecipe)allEvolvedRecipes.get(i));
        }
        return list;
    }
    
    public static ArrayList<EvolvedRecipe> getEvolvedRecipe(final InventoryItem inventoryItem, final IsoGameCharacter isoGameCharacter, final ArrayList<ItemContainer> list, final boolean b) {
        final ArrayList<EvolvedRecipe> list2 = new ArrayList<EvolvedRecipe>();
        if (inventoryItem instanceof Food && ((Food)inventoryItem).isRotten() && isoGameCharacter.getPerkLevel(PerkFactory.Perks.Cooking) < 7) {
            return list2;
        }
        if (inventoryItem instanceof Food && ((Food)inventoryItem).isFrozen()) {
            return list2;
        }
        final Stack<EvolvedRecipe> allEvolvedRecipes = ScriptManager.instance.getAllEvolvedRecipes();
        for (int i = 0; i < allEvolvedRecipes.size(); ++i) {
            final EvolvedRecipe evolvedRecipe = allEvolvedRecipes.get(i);
            if (((inventoryItem.isCooked() && evolvedRecipe.addIngredientIfCooked) || !inventoryItem.isCooked()) && (inventoryItem.getType().equals(evolvedRecipe.baseItem) || inventoryItem.getType().equals(evolvedRecipe.getResultItem())) && (!inventoryItem.getType().equals("WaterPot") || ((Drainable)inventoryItem).getUsedDelta() >= 0.75)) {
                if (b) {
                    if (!evolvedRecipe.getItemsCanBeUse(isoGameCharacter, inventoryItem, list).isEmpty()) {
                        list2.add(evolvedRecipe);
                    }
                }
                else {
                    list2.add(evolvedRecipe);
                }
            }
        }
        return list2;
    }
    
    private static void DebugPrintAllRecipes() {
        final ArrayList<Recipe> allRecipes = ScriptManager.instance.getAllRecipes();
        for (int i = 0; i < allRecipes.size(); ++i) {
            final Recipe recipe = allRecipes.get(i);
            if (recipe == null) {
                DebugLog.Recipe.println("Null recipe.");
            }
            else if (recipe.Result == null) {
                DebugLog.Recipe.println("Null result.");
            }
            else {
                DebugLog.Recipe.println(recipe.Result.type);
                DebugLog.Recipe.println("-----");
                for (int j = 0; j < recipe.Source.size(); ++j) {
                    if (recipe.Source.get(j) == null) {
                        DebugLog.Recipe.println("Null ingredient.");
                    }
                    else if (recipe.Source.get(j).getItems().isEmpty()) {
                        DebugLog.Recipe.println(recipe.Source.get(j).getItems().toString());
                    }
                }
            }
        }
    }
    
    public static Recipe getDismantleRecipeFor(final String anotherString) {
        RecipeManager.RecipeList.clear();
        final ArrayList<Recipe> allRecipes = ScriptManager.instance.getAllRecipes();
        for (int i = 0; i < allRecipes.size(); ++i) {
            final Recipe recipe = allRecipes.get(i);
            final ArrayList<Recipe.Source> source = recipe.getSource();
            if (source.size() > 0) {
                for (int j = 0; j < source.size(); ++j) {
                    final Recipe.Source source2 = source.get(j);
                    for (int k = 0; k < source2.getItems().size(); ++k) {
                        if (source2.getItems().get(k).equalsIgnoreCase(anotherString) && recipe.name.toLowerCase().startsWith("dismantle ")) {
                            return recipe;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    static {
        RecipeList = new ArrayList<Recipe>();
    }
    
    private static final class SourceItems
    {
        InventoryItem selectedItem;
        final ArrayList<InventoryItem> allItems;
        final ArrayList<InventoryItem>[] itemsPerSource;
        final RMRecipeItemList.Type[] typePerSource;
        
        SourceItems(final Recipe recipe, final IsoGameCharacter isoGameCharacter, final InventoryItem selectedItem, final ArrayList<InventoryItem> list) {
            this.allItems = new ArrayList<InventoryItem>();
            this.itemsPerSource = (ArrayList<InventoryItem>[])new ArrayList[recipe.getSource().size()];
            for (int i = 0; i < this.itemsPerSource.length; ++i) {
                this.itemsPerSource[i] = new ArrayList<InventoryItem>();
            }
            this.typePerSource = new RMRecipeItemList.Type[recipe.getSource().size()];
            this.selectedItem = selectedItem;
        }
        
        public ArrayList<InventoryItem> getItems() {
            return this.allItems;
        }
    }
    
    private static final class RMRecipe
    {
        Recipe recipe;
        final ArrayList<RMRecipeSource> sources;
        final ArrayList<RMRecipeItem> allItems;
        boolean usesWater;
        final HashSet<String> allSourceTypes;
        static ArrayDeque<RMRecipe> pool;
        
        private RMRecipe() {
            this.sources = new ArrayList<RMRecipeSource>();
            this.allItems = new ArrayList<RMRecipeItem>();
            this.allSourceTypes = new HashSet<String>();
        }
        
        RMRecipe init(final Recipe recipe) {
            assert this.allItems.isEmpty();
            assert this.sources.isEmpty();
            assert this.allSourceTypes.isEmpty();
            this.recipe = recipe;
            this.usesWater = false;
            for (int i = 0; i < recipe.getSource().size(); ++i) {
                final RMRecipeSource alloc = RMRecipeSource.alloc(this, i);
                if (alloc.usesWater) {
                    this.usesWater = true;
                }
                this.allSourceTypes.addAll((Collection<?>)alloc.source.getItems());
                this.sources.add(alloc);
            }
            return this;
        }
        
        RMRecipe reset() {
            this.recipe = null;
            for (int i = 0; i < this.allItems.size(); ++i) {
                RMRecipeItem.release(this.allItems.get(i));
            }
            this.allItems.clear();
            for (int j = 0; j < this.sources.size(); ++j) {
                RMRecipeSource.release(this.sources.get(j));
            }
            this.sources.clear();
            this.allSourceTypes.clear();
            return this;
        }
        
        void getItemsFromContainers(final IsoGameCharacter isoGameCharacter, final ArrayList<ItemContainer> list, final InventoryItem inventoryItem) {
            for (int i = 0; i < list.size(); ++i) {
                this.getItemsFromContainer(isoGameCharacter, list.get(i), inventoryItem);
            }
            if (!this.Test(inventoryItem)) {
                return;
            }
            for (int j = 0; j < this.sources.size(); ++j) {
                this.sources.get(j).getItemsFrom(this.allItems, this);
            }
        }
        
        void getItemsFromContainer(final IsoGameCharacter isoGameCharacter, final ItemContainer itemContainer, final InventoryItem inventoryItem) {
            for (int i = 0; i < itemContainer.getItems().size(); ++i) {
                final InventoryItem inventoryItem2 = itemContainer.getItems().get(i);
                if ((inventoryItem != null && inventoryItem == inventoryItem2) || !isoGameCharacter.isEquippedClothing(inventoryItem2) || this.isKeep(inventoryItem2.getFullType())) {
                    if (this.usesWater && inventoryItem2 instanceof DrainableComboItem && inventoryItem2.isWaterSource()) {
                        this.allItems.add(RMRecipeItem.alloc(inventoryItem2));
                    }
                    else if (this.allSourceTypes.contains(inventoryItem2.getFullType())) {
                        this.allItems.add(RMRecipeItem.alloc(inventoryItem2));
                    }
                }
            }
        }
        
        boolean Test(final InventoryItem inventoryItem) {
            if (inventoryItem == null || this.recipe.LuaTest == null) {
                return true;
            }
            final Object functionObject = LuaManager.getFunctionObject(this.recipe.LuaTest);
            return functionObject != null && LuaManager.caller.protectedCallBoolean(LuaManager.thread, functionObject, (Object)inventoryItem, (Object)this.recipe.getResult()) == Boolean.TRUE;
        }
        
        boolean hasItems() {
            for (int i = 0; i < this.sources.size(); ++i) {
                if (!this.sources.get(i).hasItems()) {
                    return false;
                }
            }
            return true;
        }
        
        boolean isKeep(final String s) {
            for (int i = 0; i < this.sources.size(); ++i) {
                if (this.sources.get(i).isKeep(s)) {
                    return true;
                }
            }
            return false;
        }
        
        void getAvailableItems(final SourceItems sourceItems, final boolean b) {
            assert b || this.hasItems();
            for (int i = 0; i < this.sources.size(); ++i) {
                final RMRecipeSource rmRecipeSource = this.sources.get(i);
                assert b || rmRecipeSource.hasItems();
                rmRecipeSource.getAvailableItems(sourceItems, b);
            }
        }
        
        void Use(final ArrayList<InventoryItem> list) {
            assert this.hasItems();
            for (int i = 0; i < this.sources.size(); ++i) {
                final RMRecipeSource rmRecipeSource = this.sources.get(i);
                assert rmRecipeSource.hasItems();
                rmRecipeSource.Use(list);
            }
        }
        
        static RMRecipe alloc(final Recipe recipe) {
            return RMRecipe.pool.isEmpty() ? new RMRecipe().init(recipe) : RMRecipe.pool.pop().init(recipe);
        }
        
        static void release(final RMRecipe o) {
            assert !RMRecipe.pool.contains(o);
            RMRecipe.pool.push(o.reset());
        }
        
        static {
            RMRecipe.pool = new ArrayDeque<RMRecipe>();
        }
    }
    
    private static final class RMRecipeSource
    {
        RMRecipe recipe;
        Recipe.Source source;
        int index;
        final ArrayList<RMRecipeItemList> itemLists;
        boolean usesWater;
        static ArrayDeque<RMRecipeSource> pool;
        
        private RMRecipeSource() {
            this.itemLists = new ArrayList<RMRecipeItemList>();
        }
        
        RMRecipeSource init(final RMRecipe recipe, final int n) {
            this.recipe = recipe;
            this.source = recipe.recipe.getSource().get(n);
            this.index = n;
            assert this.itemLists.isEmpty();
            for (int i = 0; i < this.source.getItems().size(); ++i) {
                this.itemLists.add(RMRecipeItemList.alloc(this, i));
            }
            this.usesWater = this.source.getItems().contains("Water");
            return this;
        }
        
        RMRecipeSource reset() {
            for (int i = 0; i < this.itemLists.size(); ++i) {
                RMRecipeItemList.release(this.itemLists.get(i));
            }
            this.itemLists.clear();
            return this;
        }
        
        void getItemsFrom(final ArrayList<RMRecipeItem> list, final RMRecipe rmRecipe) {
            for (int i = 0; i < this.itemLists.size(); ++i) {
                this.itemLists.get(i).getItemsFrom(list, rmRecipe);
            }
        }
        
        boolean hasItems() {
            for (int i = 0; i < this.itemLists.size(); ++i) {
                if (this.itemLists.get(i).hasItems()) {
                    return true;
                }
            }
            return false;
        }
        
        boolean isKeep(final String o) {
            return this.source.getItems().contains(o) && this.source.keep;
        }
        
        void getAvailableItems(final SourceItems sourceItems, final boolean b) {
            if (b) {
                for (int i = 0; i < this.itemLists.size(); ++i) {
                    this.itemLists.get(i).getAvailableItems(sourceItems, b);
                }
                return;
            }
            int index = -1;
            for (int j = 0; j < this.itemLists.size(); ++j) {
                final RMRecipeItemList list = this.itemLists.get(j);
                if (list.hasItems()) {
                    if (sourceItems.selectedItem != null && list.indexOf(sourceItems.selectedItem) != -1) {
                        index = j;
                        break;
                    }
                    if (index == -1) {
                        index = j;
                    }
                }
            }
            this.itemLists.get(index).getAvailableItems(sourceItems, b);
        }
        
        void Use(final ArrayList<InventoryItem> list) {
            assert this.hasItems();
            for (int i = 0; i < this.itemLists.size(); ++i) {
                final RMRecipeItemList list2 = this.itemLists.get(i);
                if (list2.hasItems()) {
                    list2.Use(list);
                    return;
                }
            }
            assert false;
        }
        
        static RMRecipeSource alloc(final RMRecipe rmRecipe, final int n) {
            return RMRecipeSource.pool.isEmpty() ? new RMRecipeSource().init(rmRecipe, n) : RMRecipeSource.pool.pop().init(rmRecipe, n);
        }
        
        static void release(final RMRecipeSource o) {
            assert !RMRecipeSource.pool.contains(o);
            RMRecipeSource.pool.push(o.reset());
        }
        
        static {
            RMRecipeSource.pool = new ArrayDeque<RMRecipeSource>();
        }
    }
    
    private static final class RMRecipeItemList
    {
        RMRecipeSource source;
        final ArrayList<RMRecipeItem> items;
        int index;
        int usesNeeded;
        Type type;
        static ArrayDeque<RMRecipeItemList> pool;
        
        private RMRecipeItemList() {
            this.items = new ArrayList<RMRecipeItem>();
            this.type = Type.NONE;
        }
        
        RMRecipeItemList init(final RMRecipeSource source, final int n) {
            assert this.items.isEmpty();
            this.source = source;
            this.index = n;
            final String anObject = source.source.getItems().get(n);
            this.usesNeeded = (int)source.source.getCount();
            if ("Water".equals(anObject)) {
                this.type = Type.WATER;
            }
            else if (source.source.isDestroy()) {
                this.type = Type.DESTROY;
            }
            else if (ScriptManager.instance.isDrainableItemType(anObject)) {
                this.type = Type.DRAINABLE;
            }
            else if (source.source.use > 0.0f) {
                this.usesNeeded = (int)source.source.use;
                this.type = Type.FOOD;
            }
            else {
                this.type = Type.OTHER;
            }
            return this;
        }
        
        RMRecipeItemList reset() {
            this.source = null;
            this.items.clear();
            return this;
        }
        
        void getItemsFrom(final ArrayList<RMRecipeItem> list, final RMRecipe rmRecipe) {
            final String anObject = this.source.source.getItems().get(this.index);
            for (int i = 0; i < list.size(); ++i) {
                final RMRecipeItem e = list.get(i);
                final DrainableComboItem drainableComboItem = zombie.util.Type.tryCastTo(e.item, DrainableComboItem.class);
                final Food food = zombie.util.Type.tryCastTo(e.item, Food.class);
                if ("Water".equals(anObject)) {
                    if (rmRecipe.Test(e.item)) {
                        if (e.item instanceof DrainableComboItem && e.item.isWaterSource()) {
                            e.water = RecipeManager.AvailableUses(e.item);
                            this.items.add(e);
                        }
                    }
                }
                else if (anObject.equals(e.item.getFullType())) {
                    if (rmRecipe.recipe.getHeat() <= 0.0f || drainableComboItem == null || !e.item.IsCookable || e.item.getInvHeat() + 1.0f >= rmRecipe.recipe.getHeat()) {
                        if (rmRecipe.recipe.getHeat() >= 0.0f || drainableComboItem == null || !e.item.IsCookable || e.item.getInvHeat() <= rmRecipe.recipe.getHeat()) {
                            if (food == null || food.getFreezingTime() <= 0.0f) {
                                if (!rmRecipe.recipe.noBrokenItems() || !e.item.isBroken()) {
                                    if (!"Clothing".equals(e.item.getCategory()) || !e.item.isFavorite()) {
                                        if (rmRecipe.Test(e.item)) {
                                            if (this.source.source.isDestroy()) {
                                                e.uses = 1;
                                                this.items.add(e);
                                            }
                                            else if (drainableComboItem != null) {
                                                e.uses = RecipeManager.AvailableUses(e.item);
                                                this.items.add(e);
                                            }
                                            else if (this.source.source.use > 0.0f) {
                                                if (e.item instanceof Food) {
                                                    e.uses = RecipeManager.AvailableUses(e.item);
                                                    this.items.add(e);
                                                }
                                            }
                                            else {
                                                e.uses = e.item.getUses();
                                                this.items.add(e);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        boolean hasItems() {
            final String anObject = this.source.source.getItems().get(this.index);
            int n = 0;
            for (int i = 0; i < this.items.size(); ++i) {
                if ("Water".equals(anObject)) {
                    n += this.items.get(i).water;
                }
                else {
                    n += this.items.get(i).uses;
                }
            }
            return n >= this.usesNeeded;
        }
        
        int indexOf(final InventoryItem inventoryItem) {
            for (int i = 0; i < this.items.size(); ++i) {
                if (this.items.get(i).item == inventoryItem) {
                    return i;
                }
            }
            return -1;
        }
        
        void getAvailableItems(final SourceItems sourceItems, final boolean b) {
            if (b) {
                this.Use(sourceItems.itemsPerSource[this.source.index]);
                sourceItems.typePerSource[this.source.index] = this.type;
                sourceItems.allItems.addAll(sourceItems.itemsPerSource[this.source.index]);
                return;
            }
            assert this.hasItems();
            if (sourceItems.selectedItem != null) {
                final int index = this.indexOf(sourceItems.selectedItem);
                if (index != -1) {
                    this.items.add(0, this.items.remove(index));
                }
            }
            this.Use(sourceItems.itemsPerSource[this.source.index]);
            sourceItems.typePerSource[this.source.index] = this.type;
            sourceItems.allItems.addAll(sourceItems.itemsPerSource[this.source.index]);
        }
        
        void Use(final ArrayList<InventoryItem> list) {
            final String anObject = this.source.source.getItems().get(this.index);
            int usesNeeded = this.usesNeeded;
            for (int i = 0; i < this.items.size(); ++i) {
                final RMRecipeItem rmRecipeItem = this.items.get(i);
                if ("Water".equals(anObject) && rmRecipeItem.water > 0) {
                    usesNeeded -= rmRecipeItem.UseWater(usesNeeded);
                    list.add(rmRecipeItem.item);
                }
                else if (this.source.source.isKeep() && rmRecipeItem.uses > 0) {
                    usesNeeded -= Math.min(rmRecipeItem.uses, usesNeeded);
                    list.add(rmRecipeItem.item);
                }
                else if (rmRecipeItem.uses > 0) {
                    usesNeeded -= rmRecipeItem.Use(usesNeeded);
                    list.add(rmRecipeItem.item);
                }
                if (usesNeeded <= 0) {
                    break;
                }
            }
        }
        
        static RMRecipeItemList alloc(final RMRecipeSource rmRecipeSource, final int n) {
            return RMRecipeItemList.pool.isEmpty() ? new RMRecipeItemList().init(rmRecipeSource, n) : RMRecipeItemList.pool.pop().init(rmRecipeSource, n);
        }
        
        static void release(final RMRecipeItemList o) {
            assert !RMRecipeItemList.pool.contains(o);
            RMRecipeItemList.pool.push(o.reset());
        }
        
        static {
            RMRecipeItemList.pool = new ArrayDeque<RMRecipeItemList>();
        }
        
        enum Type
        {
            NONE, 
            WATER, 
            DRAINABLE, 
            FOOD, 
            OTHER, 
            DESTROY;
            
            private static /* synthetic */ Type[] $values() {
                return new Type[] { Type.NONE, Type.WATER, Type.DRAINABLE, Type.FOOD, Type.OTHER, Type.DESTROY };
            }
            
            static {
                $VALUES = $values();
            }
        }
    }
    
    private static final class RMRecipeItem
    {
        InventoryItem item;
        int uses;
        int water;
        static ArrayDeque<RMRecipeItem> pool;
        
        RMRecipeItem init(final InventoryItem item) {
            this.item = item;
            return this;
        }
        
        RMRecipeItem reset() {
            this.item = null;
            this.uses = 0;
            this.water = 0;
            return this;
        }
        
        int Use(final int b) {
            final int min = Math.min(this.uses, b);
            this.uses -= min;
            return min;
        }
        
        int UseWater(final int b) {
            final int min = Math.min(this.water, b);
            this.water -= min;
            return min;
        }
        
        static RMRecipeItem alloc(final InventoryItem inventoryItem) {
            return RMRecipeItem.pool.isEmpty() ? new RMRecipeItem().init(inventoryItem) : RMRecipeItem.pool.pop().init(inventoryItem);
        }
        
        static void release(final RMRecipeItem o) {
            assert !RMRecipeItem.pool.contains(o);
            RMRecipeItem.pool.push(o.reset());
        }
        
        static {
            RMRecipeItem.pool = new ArrayDeque<RMRecipeItem>();
        }
    }
}

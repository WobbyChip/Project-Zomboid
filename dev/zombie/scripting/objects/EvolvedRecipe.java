// 
// Decompiled by Procyon v0.5.36
// 

package zombie.scripting.objects;

import java.util.Stack;
import zombie.scripting.ScriptManager;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.InventoryItemFactory;
import java.util.Iterator;
import zombie.inventory.types.Food;
import zombie.characters.skills.PerkFactory;
import zombie.inventory.ItemContainer;
import java.util.ArrayList;
import zombie.characters.IsoGameCharacter;
import zombie.inventory.InventoryItem;
import zombie.util.StringUtils;
import zombie.core.Translator;
import java.util.HashMap;
import java.util.Map;

public final class EvolvedRecipe extends BaseScriptObject
{
    public String name;
    public String DisplayName;
    private String originalname;
    public int maxItems;
    public final Map<String, ItemRecipe> itemsList;
    public String resultItem;
    public String baseItem;
    public boolean cookable;
    public boolean addIngredientIfCooked;
    public boolean canAddSpicesEmpty;
    public String addIngredientSound;
    
    public EvolvedRecipe(final String name) {
        this.name = null;
        this.DisplayName = null;
        this.maxItems = 0;
        this.itemsList = new HashMap<String, ItemRecipe>();
        this.resultItem = null;
        this.baseItem = null;
        this.cookable = false;
        this.addIngredientIfCooked = false;
        this.canAddSpicesEmpty = false;
        this.addIngredientSound = null;
        this.name = name;
    }
    
    @Override
    public void Load(final String originalname, final String[] array) {
        this.DisplayName = Translator.getRecipeName(originalname);
        this.originalname = originalname;
        for (int i = 0; i < array.length; ++i) {
            if (!array[i].trim().isEmpty()) {
                if (array[i].contains(":")) {
                    final String[] split = array[i].split(":");
                    final String trim = split[0].trim();
                    final String trim2 = split[1].trim();
                    if (trim.equals("BaseItem")) {
                        this.baseItem = trim2;
                    }
                    else if (trim.equals("Name")) {
                        this.DisplayName = Translator.getRecipeName(trim2);
                        this.originalname = trim2;
                    }
                    else if (trim.equals("ResultItem")) {
                        this.resultItem = trim2;
                        if (!trim2.contains(".")) {
                            this.resultItem = trim2;
                        }
                    }
                    else if (trim.equals("Cookable")) {
                        this.cookable = true;
                    }
                    else if (trim.equals("MaxItems")) {
                        this.maxItems = Integer.parseInt(trim2);
                    }
                    else if (trim.equals("AddIngredientIfCooked")) {
                        this.addIngredientIfCooked = Boolean.parseBoolean(trim2);
                    }
                    else if (trim.equals("AddIngredientSound")) {
                        this.addIngredientSound = StringUtils.discardNullOrWhitespace(trim2);
                    }
                    else if (trim.equals("CanAddSpicesEmpty")) {
                        this.canAddSpicesEmpty = Boolean.parseBoolean(trim2);
                    }
                }
            }
        }
    }
    
    public boolean needToBeCooked(final InventoryItem inventoryItem) {
        final ItemRecipe itemRecipe = this.getItemRecipe(inventoryItem);
        return itemRecipe == null || itemRecipe.cooked == inventoryItem.isCooked() || !itemRecipe.cooked;
    }
    
    public ArrayList<InventoryItem> getItemsCanBeUse(final IsoGameCharacter isoGameCharacter, final InventoryItem inventoryItem, ArrayList<ItemContainer> list) {
        final int perkLevel = isoGameCharacter.getPerkLevel(PerkFactory.Perks.Cooking);
        if (list == null) {
            list = new ArrayList<ItemContainer>();
        }
        final ArrayList<Food> list2 = new ArrayList<Food>();
        final Iterator<String> iterator = this.itemsList.keySet().iterator();
        if (!list.contains(isoGameCharacter.getInventory())) {
            list.add(isoGameCharacter.getInventory());
        }
        while (iterator.hasNext()) {
            final String s = iterator.next();
            final Iterator<ItemContainer> iterator2 = list.iterator();
            while (iterator2.hasNext()) {
                this.checkItemCanBeUse(iterator2.next(), s, inventoryItem, perkLevel, (ArrayList<InventoryItem>)list2);
            }
        }
        if (inventoryItem.haveExtraItems() && inventoryItem.getExtraItems().size() >= 3) {
            for (int i = 0; i < list.size(); ++i) {
                final ItemContainer itemContainer = list.get(i);
                for (int j = 0; j < itemContainer.getItems().size(); ++j) {
                    final InventoryItem inventoryItem2 = itemContainer.getItems().get(j);
                    if (inventoryItem2 instanceof Food && ((Food)inventoryItem2).getPoisonLevelForRecipe() != null && isoGameCharacter.isKnownPoison(inventoryItem2) && !list2.contains(inventoryItem2)) {
                        list2.add((Food)inventoryItem2);
                    }
                }
            }
        }
        return (ArrayList<InventoryItem>)list2;
    }
    
    private void checkItemCanBeUse(final ItemContainer itemContainer, final String s, final InventoryItem inventoryItem, final int n, final ArrayList<InventoryItem> list) {
        final ArrayList<InventoryItem> itemsFromType = itemContainer.getItemsFromType(s);
        for (int i = 0; i < itemsFromType.size(); ++i) {
            final InventoryItem e = itemsFromType.get(i);
            int n2 = 0;
            if (e instanceof Food && this.itemsList.get(s).use != -1) {
                final Food food = (Food)e;
                if (food.isSpice()) {
                    if (this.isResultItem(inventoryItem)) {
                        n2 = (this.isSpiceAdded(inventoryItem, food) ? 0 : 1);
                    }
                    else if (this.canAddSpicesEmpty) {
                        n2 = 1;
                    }
                    if (food.isRotten() && n < 7) {
                        n2 = 0;
                    }
                }
                else if ((!inventoryItem.haveExtraItems() || inventoryItem.extraItems.size() < this.maxItems) && (!food.isRotten() || n >= 7)) {
                    n2 = 1;
                }
                if (food.isFrozen()) {
                    n2 = 0;
                }
            }
            else {
                n2 = 1;
            }
            this.getItemRecipe(e);
            if (n2 != 0) {
                list.add(e);
            }
        }
    }
    
    public InventoryItem addItem(InventoryItem inventoryItem, final InventoryItem inventoryItem2, final IsoGameCharacter isoGameCharacter) {
        final int perkLevel = isoGameCharacter.getPerkLevel(PerkFactory.Perks.Cooking);
        if (!this.isResultItem(inventoryItem)) {
            final InventoryItem inventoryItem3 = (inventoryItem instanceof Food) ? inventoryItem : null;
            final InventoryItem createItem = InventoryItemFactory.CreateItem(this.resultItem);
            if (createItem != null) {
                if (inventoryItem instanceof HandWeapon) {
                    createItem.getModData().rawset(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, inventoryItem.getType()), (Object)(inventoryItem.getCondition() / (double)inventoryItem.getConditionMax()));
                }
                isoGameCharacter.getInventory().Remove(inventoryItem);
                isoGameCharacter.getInventory().AddItem(createItem);
                final InventoryItem inventoryItem4 = inventoryItem;
                inventoryItem = createItem;
                if (inventoryItem instanceof Food) {
                    ((Food)inventoryItem).setCalories(0.0f);
                    ((Food)inventoryItem).setCarbohydrates(0.0f);
                    ((Food)inventoryItem).setProteins(0.0f);
                    ((Food)inventoryItem).setLipids(0.0f);
                    if (inventoryItem2 instanceof Food && ((Food)inventoryItem2).getPoisonLevelForRecipe() != null) {
                        this.addPoison(inventoryItem2, inventoryItem, isoGameCharacter);
                    }
                    ((Food)inventoryItem).setIsCookable(this.cookable);
                    if (inventoryItem3 != null) {
                        ((Food)inventoryItem).setHungChange(((Food)inventoryItem3).getHungChange());
                        ((Food)inventoryItem).setBaseHunger(((Food)inventoryItem3).getBaseHunger());
                    }
                    else {
                        ((Food)inventoryItem).setHungChange(0.0f);
                        ((Food)inventoryItem).setBaseHunger(0.0f);
                    }
                    if (inventoryItem4.isTaintedWater()) {
                        inventoryItem.setTaintedWater(true);
                    }
                    if (inventoryItem4 instanceof Food && inventoryItem4.getOffAgeMax() != 1000000000 && inventoryItem.getOffAgeMax() != 1000000000) {
                        inventoryItem.setAge(inventoryItem.getOffAgeMax() * (inventoryItem4.getAge() / inventoryItem4.getOffAgeMax()));
                    }
                    if (inventoryItem3 instanceof Food) {
                        ((Food)inventoryItem).setCalories(((Food)inventoryItem3).getCalories());
                        ((Food)inventoryItem).setProteins(((Food)inventoryItem3).getProteins());
                        ((Food)inventoryItem).setLipids(((Food)inventoryItem3).getLipids());
                        ((Food)inventoryItem).setCarbohydrates(((Food)inventoryItem3).getCarbohydrates());
                        ((Food)inventoryItem).setThirstChange(((Food)inventoryItem3).getThirstChange());
                    }
                }
                inventoryItem.setUnhappyChange(0.0f);
                inventoryItem.setBoredomChange(0.0f);
            }
        }
        if (this.itemsList.get(inventoryItem2.getType()) != null && this.itemsList.get(inventoryItem2.getType()).use > -1) {
            if (inventoryItem2 instanceof Food) {
                float n = this.itemsList.get(inventoryItem2.getType()).use / 100.0f;
                final Food food = (Food)inventoryItem2;
                if (food.isSpice() && inventoryItem instanceof Food) {
                    this.useSpice(food, (Food)inventoryItem, n, perkLevel);
                    return inventoryItem;
                }
                boolean b = false;
                if (food.isRotten()) {
                    final DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    if (perkLevel == 7 || perkLevel == 8) {
                        n = Float.parseFloat(decimalFormat.format(Math.abs(food.getBaseHunger() - (food.getBaseHunger() - 0.05f * food.getBaseHunger()))).replace(",", "."));
                    }
                    else if (perkLevel == 9 || perkLevel == 10) {
                        n = Float.parseFloat(decimalFormat.format(Math.abs(food.getBaseHunger() - (food.getBaseHunger() - 0.1f * food.getBaseHunger()))).replace(",", "."));
                    }
                    b = true;
                }
                if (Math.abs(food.getHungerChange()) < n) {
                    final DecimalFormat decimalFormat2 = new DecimalFormat("#.##");
                    decimalFormat2.setRoundingMode(RoundingMode.DOWN);
                    n = Math.abs(Float.parseFloat(decimalFormat2.format(food.getHungerChange()).replace(",", ".")));
                    b = true;
                }
                if (inventoryItem instanceof Food) {
                    final Food food2 = (Food)inventoryItem;
                    if (inventoryItem2 instanceof Food && ((Food)inventoryItem2).getPoisonLevelForRecipe() != null) {
                        this.addPoison(inventoryItem2, inventoryItem, isoGameCharacter);
                    }
                    food2.setHungChange(food2.getHungerChange() - n);
                    food2.setBaseHunger(food2.getBaseHunger() - n);
                    if (food.isbDangerousUncooked() && !food.isCooked()) {
                        food2.setbDangerousUncooked(true);
                    }
                    int n2 = 0;
                    if (inventoryItem.extraItems != null) {
                        for (int i = 0; i < inventoryItem.extraItems.size(); ++i) {
                            if (inventoryItem.extraItems.get(i).equals(inventoryItem2.getFullType())) {
                                ++n2;
                            }
                        }
                    }
                    if (inventoryItem.extraItems != null && inventoryItem.extraItems.size() - 2 > perkLevel) {
                        n2 += inventoryItem.extraItems.size() - 2 - perkLevel * 3;
                    }
                    float n3 = n - 3 * perkLevel / 100.0f * n;
                    float abs = Math.abs(n3 / food.getHungChange());
                    if (abs > 1.0f) {
                        abs = 1.0f;
                    }
                    inventoryItem.setUnhappyChange(inventoryItem.getUnhappyChange() - (5 - n2 * 5));
                    if (inventoryItem.getUnhappyChange() > 25.0f) {
                        inventoryItem.setUnhappyChange(25.0f);
                    }
                    final float n4 = perkLevel / 15.0f + 1.0f;
                    food2.setCalories(food2.getCalories() + food.getCalories() * n4 * abs);
                    food2.setProteins(food2.getProteins() + food.getProteins() * n4 * abs);
                    food2.setCarbohydrates(food2.getCarbohydrates() + food.getCarbohydrates() * n4 * abs);
                    food2.setLipids(food2.getLipids() + food.getLipids() * n4 * abs);
                    food2.setThirstChange(food2.getThirstChange() + food.getThirstChange() * n4 * abs);
                    if (food.isCooked()) {
                        n3 /= (float)1.3;
                    }
                    food.setHungChange(food.getHungChange() + n3);
                    food.setBaseHunger(food.getBaseHunger() + n3);
                    food.setCalories(food.getCalories() - food.getCalories() * abs);
                    food.setProteins(food.getProteins() - food.getProteins() * abs);
                    food.setCarbohydrates(food.getCarbohydrates() - food.getCarbohydrates() * abs);
                    food.setLipids(food.getLipids() - food.getLipids() * abs);
                    if (food.getHungerChange() >= -0.02 || b) {
                        inventoryItem2.Use();
                    }
                    if (food.getFatigueChange() < 0.0f) {
                        inventoryItem.setFatigueChange(food.getFatigueChange() * abs);
                        food.setFatigueChange(food.getFatigueChange() - food.getFatigueChange() * abs);
                    }
                }
            }
            else {
                inventoryItem2.Use();
            }
            inventoryItem.addExtraItem(inventoryItem2.getFullType());
        }
        else if (inventoryItem2 instanceof Food && ((Food)inventoryItem2).getPoisonLevelForRecipe() != null) {
            this.addPoison(inventoryItem2, inventoryItem, isoGameCharacter);
        }
        this.checkUniqueRecipe(inventoryItem);
        isoGameCharacter.getXp().AddXP(PerkFactory.Perks.Cooking, 3.0f);
        return inventoryItem;
    }
    
    private void checkUniqueRecipe(final InventoryItem inventoryItem) {
        if (inventoryItem instanceof Food) {
            final Food food = (Food)inventoryItem;
            final Stack<UniqueRecipe> allUniqueRecipes = ScriptManager.instance.getAllUniqueRecipes();
            for (int i = 0; i < allUniqueRecipes.size(); ++i) {
                final ArrayList<Integer> list = new ArrayList<Integer>();
                final UniqueRecipe uniqueRecipe = allUniqueRecipes.get(i);
                if (uniqueRecipe.getBaseRecipe().equals(inventoryItem.getType())) {
                    boolean b = true;
                    for (int j = 0; j < uniqueRecipe.getItems().size(); ++j) {
                        boolean b2 = false;
                        for (int k = 0; k < food.getExtraItems().size(); ++k) {
                            if (!list.contains(k) && food.getExtraItems().get(k).equals(uniqueRecipe.getItems().get(j))) {
                                b2 = true;
                                list.add(k);
                                break;
                            }
                        }
                        if (!b2) {
                            b = false;
                            break;
                        }
                    }
                    if (food.getExtraItems().size() == uniqueRecipe.getItems().size() && b) {
                        food.setName(uniqueRecipe.getName());
                        food.setBaseHunger(food.getBaseHunger() - uniqueRecipe.getHungerBonus() / 100.0f);
                        food.setHungChange(food.getBaseHunger());
                        food.setBoredomChange(food.getBoredomChange() - uniqueRecipe.getBoredomBonus());
                        food.setUnhappyChange(food.getUnhappyChange() - uniqueRecipe.getHapinessBonus());
                        food.setCustomName(true);
                    }
                }
            }
        }
    }
    
    private void addPoison(final InventoryItem inventoryItem, final InventoryItem inventoryItem2, final IsoGameCharacter isoGameCharacter) {
        final Food food = (Food)inventoryItem;
        if (inventoryItem2 instanceof Food) {
            final Food food2 = (Food)inventoryItem2;
            int n = food.getPoisonLevelForRecipe() - isoGameCharacter.getPerkLevel(PerkFactory.Perks.Cooking);
            if (n < 1) {
                n = 1;
            }
            Float value = 0.0f;
            if (food.getThirstChange() <= -0.01f) {
                float abs = food.getUseForPoison() / 100.0f;
                if (Math.abs(food.getThirstChange()) < abs) {
                    abs = Math.abs(food.getThirstChange());
                }
                value = new Float(Math.round(Math.abs(abs / food.getThirstChange()) * 100.0) / 100.0);
                food.setThirstChange(food.getThirstChange() + abs);
                if (food.getThirstChange() > -0.01) {
                    food.Use();
                }
            }
            else if (food.getBaseHunger() <= -0.01f) {
                float abs2 = food.getUseForPoison() / 100.0f;
                if (Math.abs(food.getBaseHunger()) < abs2) {
                    abs2 = Math.abs(food.getThirstChange());
                }
                value = new Float(Math.round(Math.abs(abs2 / food.getBaseHunger()) * 100.0) / 100.0);
            }
            if (food2.getPoisonDetectionLevel() == -1) {
                food2.setPoisonDetectionLevel(0);
            }
            food2.setPoisonDetectionLevel(food2.getPoisonDetectionLevel() + n);
            if (food2.getPoisonDetectionLevel() > 10) {
                food2.setPoisonDetectionLevel(10);
            }
            final int intValue = new Float(value * (food.getPoisonPower() / 100.0f) * 100.0f).intValue();
            food2.setPoisonPower(food2.getPoisonPower() + intValue);
            food.setPoisonPower(food.getPoisonPower() - intValue);
        }
    }
    
    private void useSpice(final Food food, final Food food2, float n, final int n2) {
        if (!this.isSpiceAdded(food2, food)) {
            if (food2.spices == null) {
                food2.spices = new ArrayList<String>();
            }
            food2.spices.add(food.getFullType());
            final float n3 = n;
            if (food.isRotten()) {
                final DecimalFormat decimalFormat = new DecimalFormat("#.##");
                if (n2 == 7 || n2 == 8) {
                    n = Float.parseFloat(decimalFormat.format(Math.abs(food.getBaseHunger() - (food.getBaseHunger() - 0.05f * food.getBaseHunger()))).replace(",", "."));
                }
                else if (n2 == 9 || n2 == 10) {
                    n = Float.parseFloat(decimalFormat.format(Math.abs(food.getBaseHunger() - (food.getBaseHunger() - 0.1f * food.getBaseHunger()))).replace(",", "."));
                }
            }
            float abs = Math.abs(n / food.getHungChange());
            if (abs > 1.0f) {
                abs = 1.0f;
            }
            final float n4 = n2 / 15.0f + 1.0f;
            food2.setUnhappyChange(food2.getUnhappyChange() - n * 200.0f);
            food2.setBoredomChange(food2.getBoredomChange() - n * 200.0f);
            food2.setCalories(food2.getCalories() + food.getCalories() * n4 * abs);
            food2.setProteins(food2.getProteins() + food.getProteins() * n4 * abs);
            food2.setCarbohydrates(food2.getCarbohydrates() + food.getCarbohydrates() * n4 * abs);
            food2.setLipids(food2.getLipids() + food.getLipids() * n4 * abs);
            float abs2 = Math.abs(n3 / food.getHungChange());
            if (abs2 > 1.0f) {
                abs2 = 1.0f;
            }
            food.setCalories(food.getCalories() - food.getCalories() * abs2);
            food.setProteins(food.getProteins() - food.getProteins() * abs2);
            food.setCarbohydrates(food.getCarbohydrates() - food.getCarbohydrates() * abs2);
            food.setLipids(food.getLipids() - food.getLipids() * abs2);
            food.setHungChange(food.getHungChange() + n3);
            if (food.getHungerChange() > -0.01) {
                food.Use();
            }
        }
    }
    
    public ItemRecipe getItemRecipe(final InventoryItem inventoryItem) {
        return this.itemsList.get(inventoryItem.getType());
    }
    
    public String getName() {
        return this.DisplayName;
    }
    
    public String getOriginalname() {
        return this.originalname;
    }
    
    public String getUntranslatedName() {
        return this.name;
    }
    
    public String getBaseItem() {
        return this.baseItem;
    }
    
    public Map<String, ItemRecipe> getItemsList() {
        return this.itemsList;
    }
    
    public ArrayList<ItemRecipe> getPossibleItems() {
        final ArrayList<ItemRecipe> list = new ArrayList<ItemRecipe>();
        final Iterator<ItemRecipe> iterator = this.itemsList.values().iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }
    
    public String getResultItem() {
        if (!this.resultItem.contains(".")) {
            return this.resultItem;
        }
        return this.resultItem.split("\\.")[1];
    }
    
    public String getFullResultItem() {
        return this.resultItem;
    }
    
    public boolean isCookable() {
        return this.cookable;
    }
    
    public int getMaxItems() {
        return this.maxItems;
    }
    
    public boolean isResultItem(final InventoryItem inventoryItem) {
        return inventoryItem != null && this.getResultItem().equals(inventoryItem.getType());
    }
    
    public boolean isSpiceAdded(final InventoryItem inventoryItem, final InventoryItem inventoryItem2) {
        if (!this.isResultItem(inventoryItem)) {
            return false;
        }
        if (!(inventoryItem instanceof Food) || !(inventoryItem2 instanceof Food)) {
            return false;
        }
        if (!((Food)inventoryItem2).isSpice()) {
            return false;
        }
        final ArrayList<String> spices = ((Food)inventoryItem).getSpices();
        return spices != null && spices.contains(inventoryItem2.getFullType());
    }
    
    public String getAddIngredientSound() {
        return this.addIngredientSound;
    }
}

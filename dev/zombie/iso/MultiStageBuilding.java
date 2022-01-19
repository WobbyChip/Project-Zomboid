// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import zombie.inventory.InventoryItem;
import zombie.inventory.ItemContainer;
import zombie.util.StringUtils;
import zombie.inventory.types.DrainableComboItem;
import zombie.scripting.objects.Item;
import zombie.scripting.ScriptManager;
import zombie.SandboxOptions;
import zombie.iso.objects.IsoThumpable;
import zombie.Lua.LuaManager;
import se.krka.kahlua.vm.KahluaTable;
import java.util.Iterator;
import zombie.characters.skills.PerkFactory;
import java.util.Map;
import zombie.core.Translator;
import java.util.HashMap;
import zombie.characters.IsoGameCharacter;
import java.util.ArrayList;

public final class MultiStageBuilding
{
    public static final ArrayList<Stage> stages;
    
    public static ArrayList<Stage> getStages(final IsoGameCharacter isoGameCharacter, final IsoObject isoObject, final boolean b) {
        final ArrayList<Stage> list = new ArrayList<Stage>();
        for (int i = 0; i < MultiStageBuilding.stages.size(); ++i) {
            final Stage stage = MultiStageBuilding.stages.get(i);
            if (stage.canBeDone(isoGameCharacter, isoObject, b) && !list.contains(stage)) {
                list.add(stage);
            }
        }
        return list;
    }
    
    public static void addStage(final Stage e) {
        for (int i = 0; i < MultiStageBuilding.stages.size(); ++i) {
            if (MultiStageBuilding.stages.get(i).ID.equals(e.ID)) {
                return;
            }
        }
        MultiStageBuilding.stages.add(e);
    }
    
    static {
        stages = new ArrayList<Stage>();
    }
    
    public class Stage
    {
        public String name;
        public ArrayList<String> previousStage;
        public String recipeName;
        public String sprite;
        public String northSprite;
        public int timeNeeded;
        public int bonusHealth;
        public boolean bonusHealthSkill;
        public HashMap<String, Integer> xp;
        public HashMap<String, Integer> perks;
        public HashMap<String, Integer> items;
        public ArrayList<String> itemsToKeep;
        public String knownRecipe;
        public String thumpSound;
        public String wallType;
        public boolean canBePlastered;
        public String craftingSound;
        public String completionSound;
        public String ID;
        public boolean canBarricade;
        
        public Stage() {
            this.previousStage = new ArrayList<String>();
            this.bonusHealthSkill = true;
            this.xp = new HashMap<String, Integer>();
            this.perks = new HashMap<String, Integer>();
            this.items = new HashMap<String, Integer>();
            this.itemsToKeep = new ArrayList<String>();
            this.thumpSound = "ZombieThumpGeneric";
            this.completionSound = "BuildWoodenStructureMedium";
            this.canBarricade = false;
        }
        
        public String getName() {
            return this.name;
        }
        
        public String getDisplayName() {
            return Translator.getMultiStageBuild(this.recipeName);
        }
        
        public String getSprite() {
            return this.sprite;
        }
        
        public String getNorthSprite() {
            return this.northSprite;
        }
        
        public String getThumpSound() {
            return this.thumpSound;
        }
        
        public String getRecipeName() {
            return this.recipeName;
        }
        
        public String getKnownRecipe() {
            return this.knownRecipe;
        }
        
        public int getTimeNeeded(final IsoGameCharacter isoGameCharacter) {
            int timeNeeded = this.timeNeeded;
            final Iterator<Map.Entry<String, Integer>> iterator = this.xp.entrySet().iterator();
            while (iterator.hasNext()) {
                timeNeeded -= isoGameCharacter.getPerkLevel(PerkFactory.Perks.FromString(iterator.next().getKey())) * 10;
            }
            return timeNeeded;
        }
        
        public ArrayList<String> getItemsToKeep() {
            return this.itemsToKeep;
        }
        
        public ArrayList<String> getPreviousStages() {
            return this.previousStage;
        }
        
        public String getCraftingSound() {
            return this.craftingSound;
        }
        
        public KahluaTable getItemsLua() {
            final KahluaTable table = LuaManager.platform.newTable();
            for (final Map.Entry<String, Integer> entry : this.items.entrySet()) {
                table.rawset((Object)entry.getKey(), (Object)entry.getValue().toString());
            }
            return table;
        }
        
        public KahluaTable getPerksLua() {
            final KahluaTable table = LuaManager.platform.newTable();
            for (final Map.Entry<String, Integer> entry : this.perks.entrySet()) {
                table.rawset((Object)PerkFactory.Perks.FromString(entry.getKey()), (Object)entry.getValue().toString());
            }
            return table;
        }
        
        public void doStage(final IsoGameCharacter isoGameCharacter, final IsoThumpable isoThumpable, final boolean b) {
            final int health = isoThumpable.getHealth();
            final int maxHealth = isoThumpable.getMaxHealth();
            String s = this.sprite;
            if (isoThumpable.north) {
                s = this.northSprite;
            }
            final IsoThumpable isoThumpable2 = new IsoThumpable(IsoWorld.instance.getCell(), isoThumpable.square, s, isoThumpable.north, isoThumpable.getTable());
            isoThumpable2.setCanBePlastered(this.canBePlastered);
            if ("doorframe".equals(this.wallType)) {
                isoThumpable2.setIsDoorFrame(true);
                isoThumpable2.setCanPassThrough(true);
                isoThumpable2.setIsThumpable(isoThumpable.isThumpable());
            }
            int bonusHealth = this.bonusHealth;
            switch (SandboxOptions.instance.ConstructionBonusPoints.getValue()) {
                case 1: {
                    bonusHealth *= (int)0.5;
                    break;
                }
                case 2: {
                    bonusHealth *= (int)0.7;
                    break;
                }
                case 4: {
                    bonusHealth *= (int)1.3;
                    break;
                }
                case 5: {
                    bonusHealth *= (int)1.5;
                    break;
                }
            }
            final Iterator<String> iterator = this.perks.keySet().iterator();
            int n = 20;
            switch (SandboxOptions.instance.ConstructionBonusPoints.getValue()) {
                case 1: {
                    n = 5;
                    break;
                }
                case 2: {
                    n = 10;
                    break;
                }
                case 4: {
                    n = 35;
                    break;
                }
                case 5: {
                    n = 60;
                    break;
                }
            }
            int n2 = 0;
            if (this.bonusHealthSkill) {
                while (iterator.hasNext()) {
                    n2 += isoGameCharacter.getPerkLevel(PerkFactory.Perks.FromString(iterator.next())) * n;
                }
            }
            isoThumpable2.setMaxHealth(maxHealth + bonusHealth + n2);
            isoThumpable2.setHealth(health + bonusHealth + n2);
            isoThumpable2.setName(this.name);
            isoThumpable2.setThumpSound(this.getThumpSound());
            isoThumpable2.setCanBarricade(this.canBarricade);
            isoThumpable2.setModData(isoThumpable.getModData());
            if (this.wallType != null) {
                isoThumpable2.getModData().rawset((Object)"wallType", (Object)this.wallType);
            }
            if (b) {
                final ItemContainer inventory = isoGameCharacter.getInventory();
                for (final String key : this.items.keySet()) {
                    final int intValue = this.items.get(key);
                    final Item item = ScriptManager.instance.getItem(key);
                    if (item == null) {
                        continue;
                    }
                    if (item.getType() == Item.Type.Drainable) {
                        final Item item2;
                        final int n3;
                        final InventoryItem firstRecurse = inventory.getFirstRecurse(drainableComboItem -> drainableComboItem.getFullType().equals(item2.getFullName()) && drainableComboItem.getDrainableUsesInt() >= n3);
                        if (firstRecurse == null) {
                            continue;
                        }
                        for (int i = 0; i < intValue; ++i) {
                            firstRecurse.Use();
                        }
                    }
                    else {
                        for (int j = 0; j < intValue; ++j) {
                            final InventoryItem firstTypeRecurse = inventory.getFirstTypeRecurse(key);
                            if (firstTypeRecurse != null) {
                                firstTypeRecurse.Use();
                            }
                        }
                    }
                }
            }
            for (final String key2 : this.xp.keySet()) {
                isoGameCharacter.getXp().AddXP(PerkFactory.Perks.FromString(key2), this.xp.get(key2));
            }
            isoThumpable2.getSquare().AddSpecialObject(isoThumpable2, isoThumpable.getSquare().transmitRemoveItemFromSquare(isoThumpable));
            isoThumpable2.getSquare().RecalcAllWithNeighbours(true);
            isoThumpable2.transmitCompleteItemToServer();
            if (isoGameCharacter != null && !StringUtils.isNullOrWhitespace(this.completionSound)) {
                isoGameCharacter.playSound(this.completionSound);
            }
        }
        
        public boolean canBeDone(final IsoGameCharacter isoGameCharacter, final IsoObject isoObject, final boolean b) {
            isoGameCharacter.getInventory();
            boolean b2 = false;
            for (int i = 0; i < this.previousStage.size(); ++i) {
                if (this.previousStage.get(i).equalsIgnoreCase(isoObject.getName())) {
                    b2 = true;
                    break;
                }
            }
            return b2;
        }
        
        public void Load(final String recipeName, final String[] array) {
            this.recipeName = recipeName;
            for (int i = 0; i < array.length; ++i) {
                if (!array[i].trim().isEmpty()) {
                    if (array[i].contains(":")) {
                        final String[] split = array[i].split(":");
                        final String trim = split[0].trim();
                        final String trim2 = split[1].trim();
                        if (trim.equalsIgnoreCase("Name")) {
                            this.name = trim2.trim();
                        }
                        if (trim.equalsIgnoreCase("TimeNeeded")) {
                            this.timeNeeded = Integer.parseInt(trim2.trim());
                        }
                        if (trim.equalsIgnoreCase("BonusHealth")) {
                            this.bonusHealth = Integer.parseInt(trim2.trim());
                        }
                        if (trim.equalsIgnoreCase("Sprite")) {
                            this.sprite = trim2.trim();
                        }
                        if (trim.equalsIgnoreCase("NorthSprite")) {
                            this.northSprite = trim2.trim();
                        }
                        if (trim.equalsIgnoreCase("KnownRecipe")) {
                            this.knownRecipe = trim2.trim();
                        }
                        if (trim.equalsIgnoreCase("ThumpSound")) {
                            this.thumpSound = trim2.trim();
                        }
                        if (trim.equalsIgnoreCase("WallType")) {
                            this.wallType = trim2.trim();
                        }
                        if (trim.equalsIgnoreCase("CraftingSound")) {
                            this.craftingSound = trim2.trim();
                        }
                        if (trim.equalsIgnoreCase("CompletionSound")) {
                            this.completionSound = trim2.trim();
                        }
                        if (trim.equalsIgnoreCase("ID")) {
                            this.ID = trim2.trim();
                        }
                        if (trim.equalsIgnoreCase("CanBePlastered")) {
                            this.canBePlastered = Boolean.parseBoolean(trim2.trim());
                        }
                        if (trim.equalsIgnoreCase("BonusSkill")) {
                            this.bonusHealthSkill = Boolean.parseBoolean(trim2.trim());
                        }
                        if (trim.equalsIgnoreCase("CanBarricade")) {
                            this.canBarricade = Boolean.parseBoolean(trim2.trim());
                        }
                        if (trim.equalsIgnoreCase("XP")) {
                            final String[] split2 = trim2.split(";");
                            for (int j = 0; j < split2.length; ++j) {
                                final String[] split3 = split2[j].split("=");
                                this.xp.put(split3[0], Integer.parseInt(split3[1]));
                            }
                        }
                        if (trim.equalsIgnoreCase("PreviousStage")) {
                            final String[] split4 = trim2.split(";");
                            for (int k = 0; k < split4.length; ++k) {
                                this.previousStage.add(split4[k]);
                            }
                        }
                        if (trim.equalsIgnoreCase("SkillRequired")) {
                            final String[] split5 = trim2.split(";");
                            for (int l = 0; l < split5.length; ++l) {
                                final String[] split6 = split5[l].split("=");
                                this.perks.put(split6[0], Integer.parseInt(split6[1]));
                            }
                        }
                        if (trim.equalsIgnoreCase("ItemsRequired")) {
                            final String[] split7 = trim2.split(";");
                            for (int n = 0; n < split7.length; ++n) {
                                final String[] split8 = split7[n].split("=");
                                this.items.put(split8[0], Integer.parseInt(split8[1]));
                            }
                        }
                        if (trim.equalsIgnoreCase("ItemsToKeep")) {
                            final String[] split9 = trim2.split(";");
                            for (int n2 = 0; n2 < split9.length; ++n2) {
                                this.itemsToKeep.add(split9[n2]);
                            }
                        }
                    }
                }
            }
        }
    }
}

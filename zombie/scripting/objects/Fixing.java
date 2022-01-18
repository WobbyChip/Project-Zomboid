// 
// Decompiled by Procyon v0.5.36
// 

package zombie.scripting.objects;

import java.util.function.Predicate;
import zombie.GameWindow;
import zombie.util.Type;
import zombie.inventory.types.DrainableComboItem;
import zombie.characters.IsoGameCharacter;
import java.util.List;
import java.util.Arrays;
import zombie.inventory.InventoryItem;
import java.util.LinkedList;
import java.util.ArrayList;

public final class Fixing extends BaseScriptObject
{
    private String name;
    private ArrayList<String> require;
    private final LinkedList<Fixer> fixers;
    private Fixer globalItem;
    private float conditionModifier;
    private static final PredicateRequired s_PredicateRequired;
    private static final ArrayList<InventoryItem> s_InventoryItems;
    
    public Fixing() {
        this.name = null;
        this.require = null;
        this.fixers = new LinkedList<Fixer>();
        this.globalItem = null;
        this.conditionModifier = 1.0f;
    }
    
    @Override
    public void Load(final String name, final String[] array) {
        this.setName(name);
        for (int i = 0; i < array.length; ++i) {
            if (!array[i].trim().isEmpty()) {
                if (array[i].contains(":")) {
                    final String[] split = array[i].split(":");
                    final String trim = split[0].trim();
                    final String trim2 = split[1].trim();
                    if (trim.equals("Require")) {
                        final List<String> list = Arrays.asList(trim2.split(";"));
                        for (int j = 0; j < list.size(); ++j) {
                            this.addRequiredItem(list.get(j).trim());
                        }
                    }
                    else if (trim.equals("Fixer")) {
                        if (trim2.contains(";")) {
                            final LinkedList<FixerSkill> list2 = new LinkedList<FixerSkill>();
                            final List<String> list3 = Arrays.asList(trim2.split(";"));
                            for (int k = 1; k < list3.size(); ++k) {
                                final String[] split2 = list3.get(k).trim().split("=");
                                list2.add(new FixerSkill(split2[0].trim(), Integer.parseInt(split2[1].trim())));
                            }
                            if (trim2.split(";")[0].trim().contains("=")) {
                                final String[] split3 = trim2.split(";")[0].trim().split("=");
                                this.fixers.add(new Fixer(split3[0], list2, Integer.parseInt(split3[1])));
                            }
                            else {
                                this.fixers.add(new Fixer(trim2.split(";")[0].trim(), list2, 1));
                            }
                        }
                        else if (trim2.contains("=")) {
                            this.fixers.add(new Fixer(trim2.split("=")[0], null, Integer.parseInt(trim2.split("=")[1])));
                        }
                        else {
                            this.fixers.add(new Fixer(trim2, null, 1));
                        }
                    }
                    else if (trim.equals("GlobalItem")) {
                        if (trim2.contains("=")) {
                            this.setGlobalItem(new Fixer(trim2.split("=")[0], null, Integer.parseInt(trim2.split("=")[1])));
                        }
                        else {
                            this.setGlobalItem(new Fixer(trim2, null, 1));
                        }
                    }
                    else if (trim.equals("ConditionModifier")) {
                        this.setConditionModifier(Float.parseFloat(trim2.trim()));
                    }
                }
            }
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public ArrayList<String> getRequiredItem() {
        return this.require;
    }
    
    public void addRequiredItem(final String e) {
        if (this.require == null) {
            this.require = new ArrayList<String>();
        }
        this.require.add(e);
    }
    
    public LinkedList<Fixer> getFixers() {
        return this.fixers;
    }
    
    public Fixer usedInFixer(final InventoryItem inventoryItem, final IsoGameCharacter isoGameCharacter) {
        for (int i = 0; i < this.getFixers().size(); ++i) {
            final Fixer fixer = this.getFixers().get(i);
            if (fixer.getFixerName().equals(inventoryItem.getType())) {
                if (inventoryItem instanceof DrainableComboItem) {
                    final DrainableComboItem drainableComboItem = (DrainableComboItem)inventoryItem;
                    if (drainableComboItem.getUsedDelta() >= 1.0f) {
                        return fixer;
                    }
                    if (drainableComboItem.getDrainableUsesInt() >= fixer.getNumberOfUse()) {
                        return fixer;
                    }
                }
                else if (isoGameCharacter.getInventory().getCountTypeRecurse(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getModule().getName(), fixer.getFixerName())) >= fixer.getNumberOfUse()) {
                    return fixer;
                }
            }
        }
        return null;
    }
    
    public InventoryItem haveGlobalItem(final IsoGameCharacter isoGameCharacter) {
        Fixing.s_InventoryItems.clear();
        final ArrayList<InventoryItem> requiredFixerItems = this.getRequiredFixerItems(isoGameCharacter, this.getGlobalItem(), null, Fixing.s_InventoryItems);
        return (requiredFixerItems == null) ? null : requiredFixerItems.get(0);
    }
    
    public InventoryItem haveThisFixer(final IsoGameCharacter isoGameCharacter, final Fixer fixer, final InventoryItem inventoryItem) {
        Fixing.s_InventoryItems.clear();
        final ArrayList<InventoryItem> requiredFixerItems = this.getRequiredFixerItems(isoGameCharacter, fixer, inventoryItem, Fixing.s_InventoryItems);
        return (requiredFixerItems == null) ? null : requiredFixerItems.get(0);
    }
    
    public int countUses(final IsoGameCharacter isoGameCharacter, final Fixer fixer, final InventoryItem inventoryItem) {
        Fixing.s_InventoryItems.clear();
        Fixing.s_PredicateRequired.uses = 0;
        this.getRequiredFixerItems(isoGameCharacter, fixer, inventoryItem, Fixing.s_InventoryItems);
        return Fixing.s_PredicateRequired.uses;
    }
    
    private static int countUses(final InventoryItem inventoryItem) {
        final DrainableComboItem drainableComboItem = Type.tryCastTo(inventoryItem, DrainableComboItem.class);
        if (drainableComboItem != null) {
            return drainableComboItem.getDrainableUsesInt();
        }
        return 1;
    }
    
    public ArrayList<InventoryItem> getRequiredFixerItems(final IsoGameCharacter isoGameCharacter, final Fixer fixer, final InventoryItem brokenItem, final ArrayList<InventoryItem> list) {
        if (fixer == null) {
            return null;
        }
        assert Thread.currentThread() == GameWindow.GameThread;
        final PredicateRequired s_PredicateRequired = Fixing.s_PredicateRequired;
        s_PredicateRequired.fixer = fixer;
        s_PredicateRequired.brokenItem = brokenItem;
        s_PredicateRequired.uses = 0;
        isoGameCharacter.getInventory().getAllRecurse(s_PredicateRequired, list);
        return (s_PredicateRequired.uses >= fixer.getNumberOfUse()) ? list : null;
    }
    
    public ArrayList<InventoryItem> getRequiredItems(final IsoGameCharacter isoGameCharacter, final Fixer fixer, final InventoryItem inventoryItem) {
        final ArrayList<InventoryItem> list = new ArrayList<InventoryItem>();
        if (this.getRequiredFixerItems(isoGameCharacter, fixer, inventoryItem, list) == null) {
            list.clear();
            return null;
        }
        if (this.getGlobalItem() != null && this.getRequiredFixerItems(isoGameCharacter, this.getGlobalItem(), inventoryItem, list) == null) {
            list.clear();
            return null;
        }
        return list;
    }
    
    public Fixer getGlobalItem() {
        return this.globalItem;
    }
    
    public void setGlobalItem(final Fixer globalItem) {
        this.globalItem = globalItem;
    }
    
    public float getConditionModifier() {
        return this.conditionModifier;
    }
    
    public void setConditionModifier(final float conditionModifier) {
        this.conditionModifier = conditionModifier;
    }
    
    static {
        s_PredicateRequired = new PredicateRequired();
        s_InventoryItems = new ArrayList<InventoryItem>();
    }
    
    public static final class Fixer
    {
        private String fixerName;
        private LinkedList<FixerSkill> skills;
        private int numberOfUse;
        
        public Fixer(final String fixerName, final LinkedList<FixerSkill> skills, final int numberOfUse) {
            this.fixerName = null;
            this.skills = null;
            this.numberOfUse = 1;
            this.fixerName = fixerName;
            this.skills = skills;
            this.numberOfUse = numberOfUse;
        }
        
        public String getFixerName() {
            return this.fixerName;
        }
        
        public LinkedList<FixerSkill> getFixerSkills() {
            return this.skills;
        }
        
        public int getNumberOfUse() {
            return this.numberOfUse;
        }
    }
    
    public static final class FixerSkill
    {
        private String skillName;
        private int skillLvl;
        
        public FixerSkill(final String skillName, final int skillLvl) {
            this.skillName = null;
            this.skillLvl = 0;
            this.skillName = skillName;
            this.skillLvl = skillLvl;
        }
        
        public String getSkillName() {
            return this.skillName;
        }
        
        public int getSkillLevel() {
            return this.skillLvl;
        }
    }
    
    private static final class PredicateRequired implements Predicate<InventoryItem>
    {
        Fixer fixer;
        InventoryItem brokenItem;
        int uses;
        
        @Override
        public boolean test(final InventoryItem inventoryItem) {
            if (this.uses >= this.fixer.getNumberOfUse()) {
                return false;
            }
            if (inventoryItem == this.brokenItem) {
                return false;
            }
            if (!this.fixer.getFixerName().equals(inventoryItem.getType())) {
                return false;
            }
            final int countUses = Fixing.countUses(inventoryItem);
            if (countUses > 0) {
                this.uses += countUses;
                return true;
            }
            return false;
        }
    }
}

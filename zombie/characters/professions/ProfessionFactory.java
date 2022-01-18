// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters.professions;

import zombie.debug.DebugLog;
import java.util.Collection;
import zombie.characters.skills.PerkFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import zombie.core.textures.Texture;
import zombie.interfaces.IListBoxItem;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

public final class ProfessionFactory
{
    public static LinkedHashMap<String, Profession> ProfessionMap;
    
    public static void init() {
    }
    
    public static Profession addProfession(final String key, final String s, final String s2, final int n) {
        final Profession value = new Profession(key, s, s2, n, "");
        ProfessionFactory.ProfessionMap.put(key, value);
        return value;
    }
    
    public static Profession getProfession(final String anObject) {
        for (final Profession profession : ProfessionFactory.ProfessionMap.values()) {
            if (profession.type.equals(anObject)) {
                return profession;
            }
        }
        return null;
    }
    
    public static ArrayList<Profession> getProfessions() {
        final ArrayList<Profession> list = new ArrayList<Profession>();
        final Iterator<Profession> iterator = ProfessionFactory.ProfessionMap.values().iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }
    
    public static void Reset() {
        ProfessionFactory.ProfessionMap.clear();
    }
    
    static {
        ProfessionFactory.ProfessionMap = new LinkedHashMap<String, Profession>();
    }
    
    public static class Profession implements IListBoxItem
    {
        public String type;
        public String name;
        public int cost;
        public String description;
        public String IconPath;
        public Texture texture;
        public Stack<String> FreeTraitStack;
        private List<String> freeRecipes;
        public HashMap<PerkFactory.Perk, Integer> XPBoostMap;
        
        public Profession(final String type, final String name, final String iconPath, final int cost, final String description) {
            this.texture = null;
            this.FreeTraitStack = new Stack<String>();
            this.freeRecipes = new ArrayList<String>();
            this.XPBoostMap = new HashMap<PerkFactory.Perk, Integer>();
            this.type = type;
            this.name = name;
            this.IconPath = iconPath;
            if (!iconPath.equals("")) {
                this.texture = Texture.trygetTexture(iconPath);
            }
            this.cost = cost;
            this.description = description;
        }
        
        public Texture getTexture() {
            return this.texture;
        }
        
        public void addFreeTrait(final String e) {
            this.FreeTraitStack.add(e);
        }
        
        public ArrayList<String> getFreeTraits() {
            final ArrayList<String> list = new ArrayList<String>();
            list.addAll(this.FreeTraitStack);
            return list;
        }
        
        @Override
        public String getLabel() {
            return this.getName();
        }
        
        public String getIconPath() {
            return this.IconPath;
        }
        
        @Override
        public String getLeftLabel() {
            return this.getName();
        }
        
        @Override
        public String getRightLabel() {
            int cost = this.getCost();
            if (cost == 0) {
                return "";
            }
            String s = "+";
            if (cost > 0) {
                s = "-";
            }
            else if (cost == 0) {
                s = "";
            }
            if (cost < 0) {
                cost = -cost;
            }
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s, cost);
        }
        
        public String getType() {
            return this.type;
        }
        
        public void setType(final String type) {
            this.type = type;
        }
        
        public String getName() {
            return this.name;
        }
        
        public void setName(final String name) {
            this.name = name;
        }
        
        public int getCost() {
            return this.cost;
        }
        
        public void setCost(final int cost) {
            this.cost = cost;
        }
        
        public String getDescription() {
            return this.description;
        }
        
        public void setDescription(final String description) {
            this.description = description;
        }
        
        public void setIconPath(final String iconPath) {
            this.IconPath = iconPath;
        }
        
        public Stack<String> getFreeTraitStack() {
            return this.FreeTraitStack;
        }
        
        public void addXPBoost(final PerkFactory.Perk key, final int i) {
            if (key == null || key == PerkFactory.Perks.None || key == PerkFactory.Perks.MAX) {
                DebugLog.General.warn("invalid perk passed to Profession.addXPBoost profession=%s perk=%s", this.name, key);
                return;
            }
            this.XPBoostMap.put(key, i);
        }
        
        public HashMap<PerkFactory.Perk, Integer> getXPBoostMap() {
            return this.XPBoostMap;
        }
        
        public void setFreeTraitStack(final Stack<String> freeTraitStack) {
            this.FreeTraitStack = freeTraitStack;
        }
        
        public List<String> getFreeRecipes() {
            return this.freeRecipes;
        }
        
        public void setFreeRecipes(final List<String> freeRecipes) {
            this.freeRecipes = freeRecipes;
        }
    }
}

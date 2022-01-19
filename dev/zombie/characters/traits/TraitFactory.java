// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters.traits;

import java.util.Locale;
import zombie.debug.DebugLog;
import zombie.characters.skills.PerkFactory;
import java.util.HashMap;
import zombie.core.textures.Texture;
import zombie.interfaces.IListBoxItem;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import java.util.Map;
import java.util.Comparator;
import java.util.Collection;
import java.util.LinkedList;
import java.util.LinkedHashMap;

public final class TraitFactory
{
    public static LinkedHashMap<String, Trait> TraitMap;
    
    public static void init() {
    }
    
    public static void setMutualExclusive(final String s, final String s2) {
        TraitFactory.TraitMap.get(s).MutuallyExclusive.add(s2);
        TraitFactory.TraitMap.get(s2).MutuallyExclusive.add(s);
    }
    
    public static void sortList() {
        final LinkedList<Object> list = new LinkedList<Object>(TraitFactory.TraitMap.entrySet());
        Collections.sort(list, (Comparator<? super Object>)new Comparator<Map.Entry<String, Trait>>() {
            @Override
            public int compare(final Map.Entry<String, Trait> entry, final Map.Entry<String, Trait> entry2) {
                return entry.getValue().name.compareTo(entry2.getValue().name);
            }
        });
        final LinkedHashMap<String, Trait> traitMap = new LinkedHashMap<String, Trait>();
        for (final Map.Entry<String, V> entry : list) {
            traitMap.put(entry.getKey(), (Trait)entry.getValue());
        }
        TraitFactory.TraitMap = traitMap;
    }
    
    public static Trait addTrait(final String key, final String s, final int n, final String s2, final boolean b) {
        final Trait value = new Trait(key, s, n, s2, b, false);
        TraitFactory.TraitMap.put(key, value);
        return value;
    }
    
    public static Trait addTrait(final String key, final String s, final int n, final String s2, final boolean b, final boolean b2) {
        final Trait value = new Trait(key, s, n, s2, b, b2);
        TraitFactory.TraitMap.put(key, value);
        return value;
    }
    
    public static ArrayList<Trait> getTraits() {
        final ArrayList<Trait> list = new ArrayList<Trait>();
        final Iterator<Trait> iterator = TraitFactory.TraitMap.values().iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }
    
    public static Trait getTrait(final String s) {
        if (TraitFactory.TraitMap.containsKey(s)) {
            return TraitFactory.TraitMap.get(s);
        }
        return null;
    }
    
    public static void Reset() {
        TraitFactory.TraitMap.clear();
    }
    
    static {
        TraitFactory.TraitMap = new LinkedHashMap<String, Trait>();
    }
    
    public static class Trait implements IListBoxItem
    {
        public String traitID;
        public String name;
        public int cost;
        public String description;
        public boolean prof;
        public Texture texture;
        private boolean removeInMP;
        private List<String> freeRecipes;
        public ArrayList<String> MutuallyExclusive;
        public HashMap<PerkFactory.Perk, Integer> XPBoostMap;
        
        public void addXPBoost(final PerkFactory.Perk key, final int i) {
            if (key == null || key == PerkFactory.Perks.None || key == PerkFactory.Perks.MAX) {
                DebugLog.General.warn("invalid perk passed to Trait.addXPBoost trait=%s perk=%s", this.name, key);
                return;
            }
            this.XPBoostMap.put(key, i);
        }
        
        public List<String> getFreeRecipes() {
            return this.freeRecipes;
        }
        
        public void setFreeRecipes(final List<String> freeRecipes) {
            this.freeRecipes = freeRecipes;
        }
        
        public Trait(final String traitID, final String name, final int cost, final String description, final boolean prof, final boolean removeInMP) {
            this.texture = null;
            this.removeInMP = false;
            this.freeRecipes = new ArrayList<String>();
            this.MutuallyExclusive = new ArrayList<String>(0);
            this.XPBoostMap = new HashMap<PerkFactory.Perk, Integer>();
            this.traitID = traitID;
            this.name = name;
            this.cost = cost;
            this.description = description;
            this.prof = prof;
            this.texture = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.traitID.toLowerCase(Locale.ENGLISH)));
            if (this.texture == null) {
                this.texture = Texture.getSharedTexture("media/ui/Traits/trait_generic.png");
            }
            this.removeInMP = removeInMP;
        }
        
        public String getType() {
            return this.traitID;
        }
        
        public Texture getTexture() {
            return this.texture;
        }
        
        @Override
        public String getLabel() {
            return this.name;
        }
        
        @Override
        public String getLeftLabel() {
            return this.name;
        }
        
        @Override
        public String getRightLabel() {
            int cost = this.cost;
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
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, new Integer(cost).toString());
        }
        
        public int getCost() {
            return this.cost;
        }
        
        public boolean isFree() {
            return this.prof;
        }
        
        public String getDescription() {
            return this.description;
        }
        
        public void setDescription(final String description) {
            this.description = description;
        }
        
        public ArrayList<String> getMutuallyExclusiveTraits() {
            return this.MutuallyExclusive;
        }
        
        public HashMap<PerkFactory.Perk, Integer> getXPBoostMap() {
            return this.XPBoostMap;
        }
        
        public boolean isRemoveInMP() {
            return this.removeInMP;
        }
        
        public void setRemoveInMP(final boolean removeInMP) {
            this.removeInMP = removeInMP;
        }
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters.skills;

import zombie.core.math.PZMath;
import java.util.Iterator;
import zombie.core.Translator;
import java.util.HashMap;
import java.util.ArrayList;

public final class PerkFactory
{
    public static final ArrayList<Perk> PerkList;
    private static final HashMap<String, Perk> PerkById;
    private static final HashMap<String, Perk> PerkByName;
    private static final Perk[] PerkByIndex;
    private static int NextPerkID;
    static float PerkXPReqMultiplier;
    
    public static String getPerkName(final Perk perk) {
        return perk.getName();
    }
    
    public static Perk getPerkFromName(final String key) {
        return PerkFactory.PerkByName.get(key);
    }
    
    public static Perk getPerk(final Perk perk) {
        return perk;
    }
    
    public static Perk AddPerk(final Perk perk, final String s, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8, final int n9, final int n10) {
        return AddPerk(perk, s, Perks.None, n, n2, n3, n4, n5, n6, n7, n8, n9, n10, false);
    }
    
    public static Perk AddPerk(final Perk perk, final String s, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8, final int n9, final int n10, final boolean b) {
        return AddPerk(perk, s, Perks.None, n, n2, n3, n4, n5, n6, n7, n8, n9, n10, b);
    }
    
    public static Perk AddPerk(final Perk perk, final String s, final Perk perk2, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8, final int n9, final int n10) {
        return AddPerk(perk, s, perk2, n, n2, n3, n4, n5, n6, n7, n8, n9, n10, false);
    }
    
    public static Perk AddPerk(final Perk perk, final String translation, final Perk parent, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8, final int n9, final int n10, final boolean passiv) {
        perk.translation = translation;
        perk.name = Translator.getText(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, translation));
        perk.parent = parent;
        perk.passiv = passiv;
        perk.xp1 = (int)(n * PerkFactory.PerkXPReqMultiplier);
        perk.xp2 = (int)(n2 * PerkFactory.PerkXPReqMultiplier);
        perk.xp3 = (int)(n3 * PerkFactory.PerkXPReqMultiplier);
        perk.xp4 = (int)(n4 * PerkFactory.PerkXPReqMultiplier);
        perk.xp5 = (int)(n5 * PerkFactory.PerkXPReqMultiplier);
        perk.xp6 = (int)(n6 * PerkFactory.PerkXPReqMultiplier);
        perk.xp7 = (int)(n7 * PerkFactory.PerkXPReqMultiplier);
        perk.xp8 = (int)(n8 * PerkFactory.PerkXPReqMultiplier);
        perk.xp9 = (int)(n9 * PerkFactory.PerkXPReqMultiplier);
        perk.xp10 = (int)(n10 * PerkFactory.PerkXPReqMultiplier);
        PerkFactory.PerkByName.put(perk.getName(), perk);
        PerkFactory.PerkList.add(perk);
        return perk;
    }
    
    public static void init() {
        Perks.None.parent = Perks.None;
        Perks.MAX.parent = Perks.None;
        AddPerk(Perks.Combat, "Combat", 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(Perks.Axe, "Axe", Perks.Combat, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(Perks.Blunt, "Blunt", Perks.Combat, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(Perks.SmallBlunt, "SmallBlunt", Perks.Combat, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(Perks.LongBlade, "LongBlade", Perks.Combat, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(Perks.SmallBlade, "SmallBlade", Perks.Combat, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(Perks.Spear, "Spear", Perks.Combat, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(Perks.Maintenance, "Maintenance", Perks.Combat, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(Perks.Firearm, "Firearm", 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(Perks.Aiming, "Aiming", Perks.Firearm, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(Perks.Reloading, "Reloading", Perks.Firearm, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(Perks.Crafting, "Crafting", 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(Perks.Woodwork, "Carpentry", Perks.Crafting, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(Perks.Cooking, "Cooking", Perks.Crafting, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(Perks.Farming, "Farming", Perks.Crafting, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(Perks.Doctor, "Doctor", Perks.Crafting, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(Perks.Electricity, "Electricity", Perks.Crafting, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(Perks.MetalWelding, "MetalWelding", Perks.Crafting, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(Perks.Mechanics, "Mechanics", Perks.Crafting, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(Perks.Tailoring, "Tailoring", Perks.Crafting, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(Perks.Survivalist, "Survivalist", 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(Perks.Fishing, "Fishing", Perks.Survivalist, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(Perks.Trapping, "Trapping", Perks.Survivalist, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(Perks.PlantScavenging, "Foraging", Perks.Survivalist, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(Perks.Passiv, "Passive", 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000, true);
        AddPerk(Perks.Fitness, "Fitness", Perks.Passiv, 1000, 2000, 4000, 6000, 12000, 20000, 40000, 60000, 80000, 100000, true);
        AddPerk(Perks.Strength, "Strength", Perks.Passiv, 1000, 2000, 4000, 6000, 12000, 20000, 40000, 60000, 80000, 100000, true);
        AddPerk(Perks.Agility, "Agility", 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(Perks.Sprinting, "Sprinting", Perks.Agility, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(Perks.Lightfoot, "Lightfooted", Perks.Agility, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(Perks.Nimble, "Nimble", Perks.Agility, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(Perks.Sneak, "Sneaking", Perks.Agility, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
    }
    
    public static void initTranslations() {
        PerkFactory.PerkByName.clear();
        for (final Perk value : PerkFactory.PerkList) {
            value.name = Translator.getText(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, value.translation));
            PerkFactory.PerkByName.put(value.name, value);
        }
    }
    
    public static void Reset() {
        PerkFactory.NextPerkID = 0;
        for (int i = PerkFactory.PerkByIndex.length - 1; i >= 0; --i) {
            final Perk o = PerkFactory.PerkByIndex[i];
            if (o != null) {
                if (o.isCustom()) {
                    PerkFactory.PerkList.remove(o);
                    PerkFactory.PerkById.remove(o.getId());
                    PerkFactory.PerkByName.remove(o.getName());
                    PerkFactory.PerkByIndex[o.index] = null;
                }
                else if (o != Perks.MAX) {
                    if (PerkFactory.NextPerkID == 0) {
                        PerkFactory.NextPerkID = i + 1;
                    }
                }
            }
        }
        Perks.MAX.index = PerkFactory.NextPerkID;
    }
    
    static {
        PerkList = new ArrayList<Perk>();
        PerkById = new HashMap<String, Perk>();
        PerkByName = new HashMap<String, Perk>();
        PerkByIndex = new Perk[256];
        PerkFactory.NextPerkID = 0;
        PerkFactory.PerkXPReqMultiplier = 1.5f;
    }
    
    public static final class Perks
    {
        public static final Perk None;
        public static final Perk Agility;
        public static final Perk Cooking;
        public static final Perk Melee;
        public static final Perk Crafting;
        public static final Perk Fitness;
        public static final Perk Strength;
        public static final Perk Blunt;
        public static final Perk Axe;
        public static final Perk Sprinting;
        public static final Perk Lightfoot;
        public static final Perk Nimble;
        public static final Perk Sneak;
        public static final Perk Woodwork;
        public static final Perk Aiming;
        public static final Perk Reloading;
        public static final Perk Farming;
        public static final Perk Survivalist;
        public static final Perk Fishing;
        public static final Perk Trapping;
        public static final Perk Passiv;
        public static final Perk Firearm;
        public static final Perk PlantScavenging;
        public static final Perk Doctor;
        public static final Perk Electricity;
        public static final Perk Blacksmith;
        public static final Perk MetalWelding;
        public static final Perk Melting;
        public static final Perk Mechanics;
        public static final Perk Spear;
        public static final Perk Maintenance;
        public static final Perk SmallBlade;
        public static final Perk LongBlade;
        public static final Perk SmallBlunt;
        public static final Perk Combat;
        public static final Perk Tailoring;
        public static final Perk MAX;
        
        public static int getMaxIndex() {
            return Perks.MAX.index();
        }
        
        public static Perk fromIndex(final int n) {
            if (n < 0 || n > PerkFactory.NextPerkID) {
                return null;
            }
            return PerkFactory.PerkByIndex[n];
        }
        
        public static Perk FromString(final String key) {
            return PerkFactory.PerkById.getOrDefault(key, Perks.MAX);
        }
        
        static {
            None = new Perk("None");
            Agility = new Perk("Agility");
            Cooking = new Perk("Cooking");
            Melee = new Perk("Melee");
            Crafting = new Perk("Crafting");
            Fitness = new Perk("Fitness");
            Strength = new Perk("Strength");
            Blunt = new Perk("Blunt");
            Axe = new Perk("Axe");
            Sprinting = new Perk("Sprinting");
            Lightfoot = new Perk("Lightfoot");
            Nimble = new Perk("Nimble");
            Sneak = new Perk("Sneak");
            Woodwork = new Perk("Woodwork");
            Aiming = new Perk("Aiming");
            Reloading = new Perk("Reloading");
            Farming = new Perk("Farming");
            Survivalist = new Perk("Survivalist");
            Fishing = new Perk("Fishing");
            Trapping = new Perk("Trapping");
            Passiv = new Perk("Passiv");
            Firearm = new Perk("Firearm");
            PlantScavenging = new Perk("PlantScavenging");
            Doctor = new Perk("Doctor");
            Electricity = new Perk("Electricity");
            Blacksmith = new Perk("Blacksmith");
            MetalWelding = new Perk("MetalWelding");
            Melting = new Perk("Melting");
            Mechanics = new Perk("Mechanics");
            Spear = new Perk("Spear");
            Maintenance = new Perk("Maintenance");
            SmallBlade = new Perk("SmallBlade");
            LongBlade = new Perk("LongBlade");
            SmallBlunt = new Perk("SmallBlunt");
            Combat = new Perk("Combat");
            Tailoring = new Perk("Tailoring");
            MAX = new Perk("MAX");
        }
    }
    
    public static final class Perk
    {
        private final String id;
        private int index;
        private boolean bCustom;
        public String translation;
        public String name;
        public boolean passiv;
        public int xp1;
        public int xp2;
        public int xp3;
        public int xp4;
        public int xp5;
        public int xp6;
        public int xp7;
        public int xp8;
        public int xp9;
        public int xp10;
        public Perk parent;
        
        public Perk(final String s) {
            this.bCustom = false;
            this.passiv = false;
            this.parent = Perks.None;
            this.id = s;
            this.index = PerkFactory.NextPerkID++;
            this.translation = s;
            this.name = s;
            PerkFactory.PerkById.put(s, this);
            PerkFactory.PerkByIndex[this.index] = this;
            if (Perks.MAX != null) {
                Perks.MAX.index = PZMath.max(Perks.MAX.index, this.index + 1);
            }
        }
        
        public Perk(final String s, final Perk parent) {
            this(s);
            this.parent = parent;
        }
        
        public String getId() {
            return this.id;
        }
        
        public int index() {
            return this.index;
        }
        
        public void setCustom() {
            this.bCustom = true;
        }
        
        public boolean isCustom() {
            return this.bCustom;
        }
        
        public boolean isPassiv() {
            return this.passiv;
        }
        
        public Perk getParent() {
            return this.parent;
        }
        
        public String getName() {
            return this.name;
        }
        
        public Perk getType() {
            return this;
        }
        
        public int getXp1() {
            return this.xp1;
        }
        
        public int getXp2() {
            return this.xp2;
        }
        
        public int getXp3() {
            return this.xp3;
        }
        
        public int getXp4() {
            return this.xp4;
        }
        
        public int getXp5() {
            return this.xp5;
        }
        
        public int getXp6() {
            return this.xp6;
        }
        
        public int getXp7() {
            return this.xp7;
        }
        
        public int getXp8() {
            return this.xp8;
        }
        
        public int getXp9() {
            return this.xp9;
        }
        
        public int getXp10() {
            return this.xp10;
        }
        
        public float getXpForLevel(final int n) {
            if (n == 1) {
                return (float)this.xp1;
            }
            if (n == 2) {
                return (float)this.xp2;
            }
            if (n == 3) {
                return (float)this.xp3;
            }
            if (n == 4) {
                return (float)this.xp4;
            }
            if (n == 5) {
                return (float)this.xp5;
            }
            if (n == 6) {
                return (float)this.xp6;
            }
            if (n == 7) {
                return (float)this.xp7;
            }
            if (n == 8) {
                return (float)this.xp8;
            }
            if (n == 9) {
                return (float)this.xp9;
            }
            if (n == 10) {
                return (float)this.xp10;
            }
            return -1.0f;
        }
        
        public float getTotalXpForLevel(final int n) {
            int n2 = 0;
            for (int i = 1; i <= n; ++i) {
                final float xpForLevel = this.getXpForLevel(i);
                if (xpForLevel != -1.0f) {
                    n2 += (int)xpForLevel;
                }
            }
            return (float)n2;
        }
        
        @Override
        public String toString() {
            return this.id;
        }
    }
}

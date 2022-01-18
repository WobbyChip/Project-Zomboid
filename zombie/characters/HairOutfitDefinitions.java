// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters;

import zombie.core.skinnedmodel.population.BeardStyle;
import zombie.core.ImmutableColor;
import java.util.List;
import zombie.core.skinnedmodel.population.OutfitRNG;
import zombie.iso.IsoWorld;
import zombie.util.StringUtils;
import se.krka.kahlua.vm.KahluaTableIterator;
import java.util.Collection;
import java.util.Arrays;
import zombie.util.Type;
import zombie.Lua.LuaManager;
import se.krka.kahlua.j2se.KahluaTableImpl;
import java.util.function.Supplier;
import zombie.core.skinnedmodel.population.HairStyle;
import java.util.ArrayList;

public final class HairOutfitDefinitions
{
    public static final HairOutfitDefinitions instance;
    public boolean m_dirty;
    public String hairStyle;
    public int minWorldAge;
    public final ArrayList<HaircutDefinition> m_haircutDefinition;
    public final ArrayList<HaircutOutfitDefinition> m_outfitDefinition;
    private final ThreadLocal<ArrayList<HairStyle>> m_tempHairStyles;
    
    public HairOutfitDefinitions() {
        this.m_dirty = true;
        this.m_haircutDefinition = new ArrayList<HaircutDefinition>();
        this.m_outfitDefinition = new ArrayList<HaircutOutfitDefinition>();
        this.m_tempHairStyles = ThreadLocal.withInitial((Supplier<? extends ArrayList<HairStyle>>)ArrayList::new);
    }
    
    public void checkDirty() {
        if (this.m_dirty) {
            this.m_dirty = false;
            this.init();
        }
    }
    
    private void init() {
        this.m_haircutDefinition.clear();
        this.m_outfitDefinition.clear();
        final KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)LuaManager.env.rawget((Object)"HairOutfitDefinitions");
        if (kahluaTableImpl == null) {
            return;
        }
        final KahluaTableImpl kahluaTableImpl2 = Type.tryCastTo(kahluaTableImpl.rawget((Object)"haircutDefinition"), KahluaTableImpl.class);
        if (kahluaTableImpl2 == null) {
            return;
        }
        final KahluaTableIterator iterator = kahluaTableImpl2.iterator();
        while (iterator.advance()) {
            final KahluaTableImpl kahluaTableImpl3 = Type.tryCastTo(iterator.getValue(), KahluaTableImpl.class);
            if (kahluaTableImpl3 == null) {
                continue;
            }
            this.m_haircutDefinition.add(new HaircutDefinition(kahluaTableImpl3.rawgetStr((Object)"name"), kahluaTableImpl3.rawgetInt((Object)"minWorldAge"), new ArrayList<String>(Arrays.asList(kahluaTableImpl3.rawgetStr((Object)"onlyFor").split(",")))));
        }
        final KahluaTableImpl kahluaTableImpl4 = Type.tryCastTo(kahluaTableImpl.rawget((Object)"haircutOutfitDefinition"), KahluaTableImpl.class);
        if (kahluaTableImpl4 == null) {
            return;
        }
        final KahluaTableIterator iterator2 = kahluaTableImpl4.iterator();
        while (iterator2.advance()) {
            final KahluaTableImpl kahluaTableImpl5 = Type.tryCastTo(iterator2.getValue(), KahluaTableImpl.class);
            if (kahluaTableImpl5 == null) {
                continue;
            }
            this.m_outfitDefinition.add(new HaircutOutfitDefinition(kahluaTableImpl5.rawgetStr((Object)"outfit"), initStringChance(kahluaTableImpl5.rawgetStr((Object)"haircut")), initStringChance(kahluaTableImpl5.rawgetStr((Object)"beard")), initStringChance(kahluaTableImpl5.rawgetStr((Object)"haircutColor"))));
        }
    }
    
    public boolean isHaircutValid(final String o, final String anObject) {
        HairOutfitDefinitions.instance.checkDirty();
        if (StringUtils.isNullOrEmpty(o)) {
            return true;
        }
        for (int i = 0; i < HairOutfitDefinitions.instance.m_haircutDefinition.size(); ++i) {
            final HaircutDefinition haircutDefinition = HairOutfitDefinitions.instance.m_haircutDefinition.get(i);
            if (haircutDefinition.hairStyle.equals(anObject)) {
                if (!haircutDefinition.onlyFor.contains(o)) {
                    return false;
                }
                if (IsoWorld.instance.getWorldAgeDays() < haircutDefinition.minWorldAge) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public void getValidHairStylesForOutfit(final String s, final ArrayList<HairStyle> list, final ArrayList<HairStyle> list2) {
        list2.clear();
        for (int i = 0; i < list.size(); ++i) {
            final HairStyle e = list.get(i);
            if (!e.isNoChoose()) {
                if (this.isHaircutValid(s, e.name)) {
                    list2.add(e);
                }
            }
        }
    }
    
    public String getRandomHaircut(final String anObject, final ArrayList<HairStyle> list) {
        final ArrayList<HairStyle> list2 = this.m_tempHairStyles.get();
        this.getValidHairStylesForOutfit(anObject, list, list2);
        if (list2.isEmpty()) {
            return "";
        }
        String s = OutfitRNG.pickRandom(list2).name;
        for (int n = 0, index = 0; index < HairOutfitDefinitions.instance.m_outfitDefinition.size() && n == 0; ++index) {
            final HaircutOutfitDefinition haircutOutfitDefinition = HairOutfitDefinitions.instance.m_outfitDefinition.get(index);
            if (haircutOutfitDefinition.outfit.equals(anObject) && haircutOutfitDefinition.haircutChance != null) {
                final float next = OutfitRNG.Next(0.0f, 100.0f);
                float n2 = 0.0f;
                for (int i = 0; i < haircutOutfitDefinition.haircutChance.size(); ++i) {
                    final StringChance stringChance = haircutOutfitDefinition.haircutChance.get(i);
                    n2 += stringChance.chance;
                    if (next < n2) {
                        s = stringChance.str;
                        if ("null".equalsIgnoreCase(stringChance.str)) {
                            s = "";
                        }
                        if ("random".equalsIgnoreCase(stringChance.str)) {
                            s = OutfitRNG.pickRandom(list2).name;
                        }
                        n = 1;
                        break;
                    }
                }
            }
        }
        return s;
    }
    
    public ImmutableColor getRandomHaircutColor(final String anObject) {
        ImmutableColor immutableColor = SurvivorDesc.HairCommonColors.get(OutfitRNG.Next(SurvivorDesc.HairCommonColors.size()));
        String str = null;
        for (int n = 0, index = 0; index < HairOutfitDefinitions.instance.m_outfitDefinition.size() && n == 0; ++index) {
            final HaircutOutfitDefinition haircutOutfitDefinition = HairOutfitDefinitions.instance.m_outfitDefinition.get(index);
            if (haircutOutfitDefinition.outfit.equals(anObject) && haircutOutfitDefinition.haircutColor != null) {
                final float next = OutfitRNG.Next(0.0f, 100.0f);
                float n2 = 0.0f;
                for (int i = 0; i < haircutOutfitDefinition.haircutColor.size(); ++i) {
                    final StringChance stringChance = haircutOutfitDefinition.haircutColor.get(i);
                    n2 += stringChance.chance;
                    if (next < n2) {
                        str = stringChance.str;
                        if ("random".equalsIgnoreCase(stringChance.str)) {
                            immutableColor = SurvivorDesc.HairCommonColors.get(OutfitRNG.Next(SurvivorDesc.HairCommonColors.size()));
                            str = null;
                        }
                        n = 1;
                        break;
                    }
                }
            }
        }
        if (!StringUtils.isNullOrEmpty(str)) {
            final String[] split = str.split(",");
            immutableColor = new ImmutableColor(Float.parseFloat(split[0]), Float.parseFloat(split[1]), Float.parseFloat(split[2]));
        }
        return immutableColor;
    }
    
    public String getRandomBeard(final String anObject, final ArrayList<BeardStyle> list) {
        String s = OutfitRNG.pickRandom(list).name;
        for (int n = 0, index = 0; index < HairOutfitDefinitions.instance.m_outfitDefinition.size() && n == 0; ++index) {
            final HaircutOutfitDefinition haircutOutfitDefinition = HairOutfitDefinitions.instance.m_outfitDefinition.get(index);
            if (haircutOutfitDefinition.outfit.equals(anObject) && haircutOutfitDefinition.beardChance != null) {
                final float next = OutfitRNG.Next(0.0f, 100.0f);
                float n2 = 0.0f;
                for (int i = 0; i < haircutOutfitDefinition.beardChance.size(); ++i) {
                    final StringChance stringChance = haircutOutfitDefinition.beardChance.get(i);
                    n2 += stringChance.chance;
                    if (next < n2) {
                        s = stringChance.str;
                        if ("null".equalsIgnoreCase(stringChance.str)) {
                            s = "";
                        }
                        if ("random".equalsIgnoreCase(stringChance.str)) {
                            s = OutfitRNG.pickRandom(list).name;
                        }
                        n = 1;
                        break;
                    }
                }
            }
        }
        return s;
    }
    
    private static ArrayList<StringChance> initStringChance(final String s) {
        if (StringUtils.isNullOrWhitespace(s)) {
            return null;
        }
        final ArrayList<StringChance> list = new ArrayList<StringChance>();
        final String[] split = s.split(";");
        int n = 0;
        final String[] array = split;
        for (int length = array.length, i = 0; i < length; ++i) {
            final String[] split2 = array[i].split(":");
            final StringChance e = new StringChance();
            e.str = split2[0];
            e.chance = Float.parseFloat(split2[1]);
            n += (int)e.chance;
            list.add(e);
        }
        if (n < 100) {
            final StringChance e2 = new StringChance();
            e2.str = "random";
            e2.chance = (float)(100 - n);
            list.add(e2);
        }
        return list;
    }
    
    static {
        instance = new HairOutfitDefinitions();
    }
    
    public static final class HaircutDefinition
    {
        public String hairStyle;
        public int minWorldAge;
        public ArrayList<String> onlyFor;
        
        public HaircutDefinition(final String hairStyle, final int minWorldAge, final ArrayList<String> onlyFor) {
            this.hairStyle = hairStyle;
            this.minWorldAge = minWorldAge;
            this.onlyFor = onlyFor;
        }
    }
    
    public static final class HaircutOutfitDefinition
    {
        public String outfit;
        public ArrayList<StringChance> haircutChance;
        public ArrayList<StringChance> beardChance;
        public ArrayList<StringChance> haircutColor;
        
        public HaircutOutfitDefinition(final String outfit, final ArrayList<StringChance> haircutChance, final ArrayList<StringChance> beardChance, final ArrayList<StringChance> haircutColor) {
            this.outfit = outfit;
            this.haircutChance = haircutChance;
            this.beardChance = beardChance;
            this.haircutColor = haircutColor;
        }
    }
    
    private static final class StringChance
    {
        String str;
        float chance;
    }
}

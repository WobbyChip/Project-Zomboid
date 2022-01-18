// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters;

import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.scripting.objects.Item;
import zombie.scripting.ScriptManager;
import zombie.core.skinnedmodel.population.OutfitRNG;
import zombie.core.Rand;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.util.Type;
import zombie.Lua.LuaManager;
import se.krka.kahlua.j2se.KahluaTableImpl;
import java.util.ArrayList;

public class UnderwearDefinition
{
    public static final UnderwearDefinition instance;
    public boolean m_dirty;
    private static final ArrayList<OutfitUnderwearDefinition> m_outfitDefinition;
    private static int baseChance;
    
    public UnderwearDefinition() {
        this.m_dirty = true;
    }
    
    public void checkDirty() {
        this.init();
    }
    
    private void init() {
        UnderwearDefinition.m_outfitDefinition.clear();
        final KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)LuaManager.env.rawget((Object)"UnderwearDefinition");
        if (kahluaTableImpl == null) {
            return;
        }
        UnderwearDefinition.baseChance = kahluaTableImpl.rawgetInt((Object)"baseChance");
        final KahluaTableIterator iterator = kahluaTableImpl.iterator();
        while (iterator.advance()) {
            ArrayList<StringChance> list = null;
            final KahluaTableImpl kahluaTableImpl2 = Type.tryCastTo(iterator.getValue(), KahluaTableImpl.class);
            if (kahluaTableImpl2 == null) {
                continue;
            }
            final KahluaTableImpl kahluaTableImpl3 = Type.tryCastTo(kahluaTableImpl2.rawget((Object)"top"), KahluaTableImpl.class);
            if (kahluaTableImpl3 != null) {
                list = new ArrayList<StringChance>();
                final KahluaTableIterator iterator2 = kahluaTableImpl3.iterator();
                while (iterator2.advance()) {
                    final KahluaTableImpl kahluaTableImpl4 = Type.tryCastTo(iterator2.getValue(), KahluaTableImpl.class);
                    if (kahluaTableImpl4 == null) {
                        continue;
                    }
                    list.add(new StringChance(kahluaTableImpl4.rawgetStr((Object)"name"), kahluaTableImpl4.rawgetFloat((Object)"chance")));
                }
            }
            UnderwearDefinition.m_outfitDefinition.add(new OutfitUnderwearDefinition(list, kahluaTableImpl2.rawgetStr((Object)"bottom"), kahluaTableImpl2.rawgetInt((Object)"chanceToSpawn"), kahluaTableImpl2.rawgetStr((Object)"gender")));
        }
    }
    
    public static void addRandomUnderwear(final IsoZombie isoZombie) {
        UnderwearDefinition.instance.checkDirty();
        if (Rand.Next(100) > UnderwearDefinition.baseChance) {
            return;
        }
        final ArrayList<OutfitUnderwearDefinition> list = new ArrayList<OutfitUnderwearDefinition>();
        int n = 0;
        for (int i = 0; i < UnderwearDefinition.m_outfitDefinition.size(); ++i) {
            final OutfitUnderwearDefinition e = UnderwearDefinition.m_outfitDefinition.get(i);
            if ((isoZombie.isFemale() && e.female) || (!isoZombie.isFemale() && !e.female)) {
                list.add(e);
                n += e.chanceToSpawn;
            }
        }
        final int next = OutfitRNG.Next(n);
        OutfitUnderwearDefinition outfitUnderwearDefinition = null;
        int n2 = 0;
        for (int j = 0; j < list.size(); ++j) {
            final OutfitUnderwearDefinition outfitUnderwearDefinition2 = list.get(j);
            n2 += outfitUnderwearDefinition2.chanceToSpawn;
            if (next < n2) {
                outfitUnderwearDefinition = outfitUnderwearDefinition2;
                break;
            }
        }
        if (outfitUnderwearDefinition != null) {
            final Item findItem = ScriptManager.instance.FindItem(outfitUnderwearDefinition.bottom);
            ItemVisual addClothingItem = null;
            if (findItem != null) {
                addClothingItem = isoZombie.getHumanVisual().addClothingItem(isoZombie.getItemVisuals(), findItem);
            }
            if (outfitUnderwearDefinition.top != null) {
                String str = null;
                final int next2 = OutfitRNG.Next(outfitUnderwearDefinition.topTotalChance);
                int n3 = 0;
                for (int k = 0; k < outfitUnderwearDefinition.top.size(); ++k) {
                    final StringChance stringChance = outfitUnderwearDefinition.top.get(k);
                    n3 += (int)stringChance.chance;
                    if (next2 < n3) {
                        str = stringChance.str;
                        break;
                    }
                }
                if (str != null) {
                    final Item findItem2 = ScriptManager.instance.FindItem(str);
                    if (findItem2 != null) {
                        final ItemVisual addClothingItem2 = isoZombie.getHumanVisual().addClothingItem(isoZombie.getItemVisuals(), findItem2);
                        if (Rand.Next(100) < 60 && addClothingItem2 != null && addClothingItem != null) {
                            addClothingItem2.setTint(addClothingItem.getTint());
                        }
                    }
                }
            }
        }
    }
    
    static {
        instance = new UnderwearDefinition();
        m_outfitDefinition = new ArrayList<OutfitUnderwearDefinition>();
        UnderwearDefinition.baseChance = 50;
    }
    
    public static final class OutfitUnderwearDefinition
    {
        public ArrayList<StringChance> top;
        public int topTotalChance;
        public String bottom;
        public int chanceToSpawn;
        public boolean female;
        
        public OutfitUnderwearDefinition(final ArrayList<StringChance> top, final String bottom, final int chanceToSpawn, final String anObject) {
            this.topTotalChance = 0;
            this.female = false;
            this.top = top;
            if (top != null) {
                for (int i = 0; i < top.size(); ++i) {
                    this.topTotalChance += (int)top.get(i).chance;
                }
            }
            this.bottom = bottom;
            this.chanceToSpawn = chanceToSpawn;
            if ("female".equals(anObject)) {
                this.female = true;
            }
        }
    }
    
    private static final class StringChance
    {
        String str;
        float chance;
        
        public StringChance(final String str, final float chance) {
            this.str = str;
            this.chance = chance;
        }
    }
}

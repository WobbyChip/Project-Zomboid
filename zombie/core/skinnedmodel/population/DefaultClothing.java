// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.population;

import java.util.List;
import se.krka.kahlua.j2se.KahluaTableImpl;
import java.util.ArrayList;
import zombie.util.Type;
import zombie.Lua.LuaManager;
import se.krka.kahlua.vm.KahluaTable;

public final class DefaultClothing
{
    public static final DefaultClothing instance;
    public final Clothing Pants;
    public final Clothing TShirt;
    public final Clothing TShirtDecal;
    public final Clothing Vest;
    public boolean m_dirty;
    
    public DefaultClothing() {
        this.Pants = new Clothing();
        this.TShirt = new Clothing();
        this.TShirtDecal = new Clothing();
        this.Vest = new Clothing();
        this.m_dirty = true;
    }
    
    private void checkDirty() {
        if (this.m_dirty) {
            this.m_dirty = false;
            this.init();
        }
    }
    
    private void init() {
        this.Pants.clear();
        this.TShirt.clear();
        this.TShirtDecal.clear();
        this.Vest.clear();
        final KahluaTable kahluaTable = Type.tryCastTo(LuaManager.env.rawget((Object)"DefaultClothing"), KahluaTable.class);
        if (kahluaTable == null) {
            return;
        }
        this.initClothing(kahluaTable, this.Pants, "Pants");
        this.initClothing(kahluaTable, this.TShirt, "TShirt");
        this.initClothing(kahluaTable, this.TShirtDecal, "TShirtDecal");
        this.initClothing(kahluaTable, this.Vest, "Vest");
    }
    
    private void initClothing(final KahluaTable kahluaTable, final Clothing clothing, final String s) {
        final KahluaTable kahluaTable2 = Type.tryCastTo(kahluaTable.rawget((Object)s), KahluaTable.class);
        if (kahluaTable2 == null) {
            return;
        }
        this.tableToArrayList(kahluaTable2, "hue", clothing.hue);
        this.tableToArrayList(kahluaTable2, "texture", clothing.texture);
        this.tableToArrayList(kahluaTable2, "tint", clothing.tint);
    }
    
    private void tableToArrayList(final KahluaTable kahluaTable, final String s, final ArrayList<String> list) {
        final KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)kahluaTable.rawget((Object)s);
        if (kahluaTableImpl == null) {
            return;
        }
        for (int i = 1; i <= kahluaTableImpl.len(); ++i) {
            final Object rawget = kahluaTableImpl.rawget(i);
            if (rawget != null) {
                list.add(rawget.toString());
            }
        }
    }
    
    public String pickPantsHue() {
        this.checkDirty();
        return OutfitRNG.pickRandom(this.Pants.hue);
    }
    
    public String pickPantsTexture() {
        this.checkDirty();
        return OutfitRNG.pickRandom(this.Pants.texture);
    }
    
    public String pickPantsTint() {
        this.checkDirty();
        return OutfitRNG.pickRandom(this.Pants.tint);
    }
    
    public String pickTShirtTexture() {
        this.checkDirty();
        return OutfitRNG.pickRandom(this.TShirt.texture);
    }
    
    public String pickTShirtTint() {
        this.checkDirty();
        return OutfitRNG.pickRandom(this.TShirt.tint);
    }
    
    public String pickTShirtDecalTexture() {
        this.checkDirty();
        return OutfitRNG.pickRandom(this.TShirtDecal.texture);
    }
    
    public String pickTShirtDecalTint() {
        this.checkDirty();
        return OutfitRNG.pickRandom(this.TShirtDecal.tint);
    }
    
    public String pickVestTexture() {
        this.checkDirty();
        return OutfitRNG.pickRandom(this.Vest.texture);
    }
    
    public String pickVestTint() {
        this.checkDirty();
        return OutfitRNG.pickRandom(this.Vest.tint);
    }
    
    static {
        instance = new DefaultClothing();
    }
    
    private static final class Clothing
    {
        final ArrayList<String> hue;
        final ArrayList<String> texture;
        final ArrayList<String> tint;
        
        private Clothing() {
            this.hue = new ArrayList<String>();
            this.texture = new ArrayList<String>();
            this.tint = new ArrayList<String>();
        }
        
        void clear() {
            this.hue.clear();
            this.texture.clear();
            this.tint.clear();
        }
    }
}

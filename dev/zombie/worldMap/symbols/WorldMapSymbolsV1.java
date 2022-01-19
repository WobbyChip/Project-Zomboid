// 
// Decompiled by Procyon v0.5.36
// 

package zombie.worldMap.symbols;

import zombie.util.StringUtils;
import zombie.util.PooledObject;
import zombie.Lua.LuaManager;
import zombie.util.Type;
import zombie.ui.UIFont;
import java.util.Objects;
import java.util.ArrayList;
import zombie.worldMap.UIWorldMap;
import zombie.util.Pool;

public class WorldMapSymbolsV1
{
    private static final Pool<WorldMapTextSymbolV1> s_textPool;
    private static final Pool<WorldMapTextureSymbolV1> s_texturePool;
    private final UIWorldMap m_ui;
    private final WorldMapSymbols m_uiSymbols;
    private final ArrayList<WorldMapBaseSymbolV1> m_symbols;
    
    public WorldMapSymbolsV1(final UIWorldMap uiWorldMap, final WorldMapSymbols uiSymbols) {
        this.m_symbols = new ArrayList<WorldMapBaseSymbolV1>();
        Objects.requireNonNull(uiWorldMap);
        this.m_ui = uiWorldMap;
        this.m_uiSymbols = uiSymbols;
        this.reinit();
    }
    
    public WorldMapTextSymbolV1 addTranslatedText(final String s, final UIFont uiFont, final float n, final float n2) {
        final WorldMapTextSymbolV1 init = WorldMapSymbolsV1.s_textPool.alloc().init(this, this.m_uiSymbols.addTranslatedText(s, uiFont, n, n2, 1.0f, 1.0f, 1.0f, 1.0f));
        this.m_symbols.add(init);
        return init;
    }
    
    public WorldMapTextSymbolV1 addUntranslatedText(final String s, final UIFont uiFont, final float n, final float n2) {
        final WorldMapTextSymbolV1 init = WorldMapSymbolsV1.s_textPool.alloc().init(this, this.m_uiSymbols.addUntranslatedText(s, uiFont, n, n2, 1.0f, 1.0f, 1.0f, 1.0f));
        this.m_symbols.add(init);
        return init;
    }
    
    public WorldMapTextureSymbolV1 addTexture(final String s, final float n, final float n2) {
        final WorldMapTextureSymbolV1 init = WorldMapSymbolsV1.s_texturePool.alloc().init(this, this.m_uiSymbols.addTexture(s, n, n2, 1.0f, 1.0f, 1.0f, 1.0f));
        this.m_symbols.add(init);
        return init;
    }
    
    public int hitTest(final float n, final float n2) {
        return this.m_uiSymbols.hitTest(this.m_ui, n, n2);
    }
    
    public int getSymbolCount() {
        return this.m_symbols.size();
    }
    
    public WorldMapBaseSymbolV1 getSymbolByIndex(final int index) {
        return this.m_symbols.get(index);
    }
    
    public void removeSymbolByIndex(final int index) {
        this.m_uiSymbols.removeSymbolByIndex(index);
        this.m_symbols.remove(index).release();
    }
    
    public void clear() {
        this.m_uiSymbols.clear();
        this.reinit();
    }
    
    void reinit() {
        for (int i = 0; i < this.m_symbols.size(); ++i) {
            this.m_symbols.get(i).release();
        }
        this.m_symbols.clear();
        for (int j = 0; j < this.m_uiSymbols.getSymbolCount(); ++j) {
            final WorldMapBaseSymbol symbolByIndex = this.m_uiSymbols.getSymbolByIndex(j);
            final WorldMapTextSymbol worldMapTextSymbol = Type.tryCastTo(symbolByIndex, WorldMapTextSymbol.class);
            if (worldMapTextSymbol != null) {
                this.m_symbols.add(WorldMapSymbolsV1.s_textPool.alloc().init(this, worldMapTextSymbol));
            }
            final WorldMapTextureSymbol worldMapTextureSymbol = Type.tryCastTo(symbolByIndex, WorldMapTextureSymbol.class);
            if (worldMapTextureSymbol != null) {
                this.m_symbols.add(WorldMapSymbolsV1.s_texturePool.alloc().init(this, worldMapTextureSymbol));
            }
        }
    }
    
    public static void setExposed(final LuaManager.Exposer exposer) {
        exposer.setExposed(WorldMapSymbolsV1.class);
        exposer.setExposed(WorldMapTextSymbolV1.class);
        exposer.setExposed(WorldMapTextureSymbolV1.class);
    }
    
    static {
        s_textPool = new Pool<WorldMapTextSymbolV1>(WorldMapTextSymbolV1::new);
        s_texturePool = new Pool<WorldMapTextureSymbolV1>(WorldMapTextureSymbolV1::new);
    }
    
    protected static class WorldMapBaseSymbolV1 extends PooledObject
    {
        WorldMapSymbolsV1 m_owner;
        WorldMapBaseSymbol m_symbol;
        
        WorldMapBaseSymbolV1 init(final WorldMapSymbolsV1 owner, final WorldMapBaseSymbol symbol) {
            this.m_owner = owner;
            this.m_symbol = symbol;
            return this;
        }
        
        public float getWorldX() {
            return this.m_symbol.m_x;
        }
        
        public float getWorldY() {
            return this.m_symbol.m_y;
        }
        
        public float getDisplayX() {
            return this.m_symbol.m_layoutX + this.m_owner.m_ui.getAPIv1().worldOriginX();
        }
        
        public float getDisplayY() {
            return this.m_symbol.m_layoutY + this.m_owner.m_ui.getAPIv1().worldOriginY();
        }
        
        public float getDisplayWidth() {
            return this.m_symbol.widthScaled(this.m_owner.m_ui);
        }
        
        public float getDisplayHeight() {
            return this.m_symbol.heightScaled(this.m_owner.m_ui);
        }
        
        public void setAnchor(final float n, final float n2) {
            this.m_symbol.setAnchor(n, n2);
        }
        
        public void setPosition(final float n, final float n2) {
            this.m_symbol.setPosition(n, n2);
            this.m_owner.m_uiSymbols.invalidateLayout();
        }
        
        public void setCollide(final boolean collide) {
            this.m_symbol.setCollide(collide);
        }
        
        public void setVisible(final boolean visible) {
            this.m_symbol.setVisible(visible);
        }
        
        public boolean isVisible() {
            return this.m_symbol.isVisible();
        }
        
        public void setRGBA(final float n, final float n2, final float n3, final float n4) {
            this.m_symbol.setRGBA(n, n2, n3, n4);
        }
        
        public float getRed() {
            return this.m_symbol.m_r;
        }
        
        public float getGreen() {
            return this.m_symbol.m_g;
        }
        
        public float getBlue() {
            return this.m_symbol.m_b;
        }
        
        public float getAlpha() {
            return this.m_symbol.m_a;
        }
        
        public void setScale(final float scale) {
            this.m_symbol.setScale(scale);
        }
        
        public boolean isText() {
            return false;
        }
        
        public boolean isTexture() {
            return false;
        }
    }
    
    public static class WorldMapTextSymbolV1 extends WorldMapBaseSymbolV1
    {
        WorldMapTextSymbol m_textSymbol;
        
        WorldMapTextSymbolV1 init(final WorldMapSymbolsV1 worldMapSymbolsV1, final WorldMapTextSymbol textSymbol) {
            super.init(worldMapSymbolsV1, textSymbol);
            this.m_textSymbol = textSymbol;
            return this;
        }
        
        public void setTranslatedText(final String translatedText) {
            if (StringUtils.isNullOrWhitespace(translatedText)) {
                return;
            }
            this.m_textSymbol.setTranslatedText(translatedText);
            this.m_owner.m_uiSymbols.invalidateLayout();
        }
        
        public void setUntranslatedText(final String untranslatedText) {
            if (StringUtils.isNullOrWhitespace(untranslatedText)) {
                return;
            }
            this.m_textSymbol.setUntranslatedText(untranslatedText);
            this.m_owner.m_uiSymbols.invalidateLayout();
        }
        
        public String getTranslatedText() {
            return this.m_textSymbol.getTranslatedText();
        }
        
        public String getUntranslatedText() {
            return this.m_textSymbol.getUntranslatedText();
        }
        
        @Override
        public boolean isText() {
            return true;
        }
    }
    
    public static class WorldMapTextureSymbolV1 extends WorldMapBaseSymbolV1
    {
        WorldMapTextureSymbol m_textureSymbol;
        
        WorldMapTextureSymbolV1 init(final WorldMapSymbolsV1 worldMapSymbolsV1, final WorldMapTextureSymbol textureSymbol) {
            super.init(worldMapSymbolsV1, textureSymbol);
            this.m_textureSymbol = textureSymbol;
            return this;
        }
        
        public String getSymbolID() {
            return this.m_textureSymbol.getSymbolID();
        }
        
        @Override
        public boolean isTexture() {
            return true;
        }
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.worldMap.symbols;

import zombie.core.textures.Texture;
import java.util.HashMap;
import java.util.ArrayList;

public final class MapSymbolDefinitions
{
    private static MapSymbolDefinitions instance;
    private final ArrayList<MapSymbolDefinition> m_symbolList;
    private final HashMap<String, MapSymbolDefinition> m_symbolByID;
    
    public MapSymbolDefinitions() {
        this.m_symbolList = new ArrayList<MapSymbolDefinition>();
        this.m_symbolByID = new HashMap<String, MapSymbolDefinition>();
    }
    
    public static MapSymbolDefinitions getInstance() {
        if (MapSymbolDefinitions.instance == null) {
            MapSymbolDefinitions.instance = new MapSymbolDefinitions();
        }
        return MapSymbolDefinitions.instance;
    }
    
    public void addTexture(final String s, final String texturePath, final int width, final int height) {
        final MapSymbolDefinition mapSymbolDefinition = new MapSymbolDefinition();
        mapSymbolDefinition.id = s;
        mapSymbolDefinition.texturePath = texturePath;
        mapSymbolDefinition.width = width;
        mapSymbolDefinition.height = height;
        this.m_symbolList.add(mapSymbolDefinition);
        this.m_symbolByID.put(s, mapSymbolDefinition);
    }
    
    public void addTexture(final String s, final String s2) {
        final Texture sharedTexture = Texture.getSharedTexture(s2);
        if (sharedTexture == null) {
            this.addTexture(s, s2, 18, 18);
            return;
        }
        this.addTexture(s, s2, sharedTexture.getWidth(), sharedTexture.getHeight());
    }
    
    public int getSymbolCount() {
        return this.m_symbolList.size();
    }
    
    public MapSymbolDefinition getSymbolByIndex(final int index) {
        return this.m_symbolList.get(index);
    }
    
    public MapSymbolDefinition getSymbolById(final String key) {
        return this.m_symbolByID.get(key);
    }
    
    public static void Reset() {
        if (MapSymbolDefinitions.instance == null) {
            return;
        }
        getInstance().m_symbolList.clear();
        getInstance().m_symbolByID.clear();
    }
    
    public static final class MapSymbolDefinition
    {
        private String id;
        private String texturePath;
        private int width;
        private int height;
        
        public String getId() {
            return this.id;
        }
        
        public String getTexturePath() {
            return this.texturePath;
        }
        
        public int getWidth() {
            return this.width;
        }
        
        public int getHeight() {
            return this.height;
        }
    }
}

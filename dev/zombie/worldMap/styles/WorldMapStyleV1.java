// 
// Decompiled by Procyon v0.5.36
// 

package zombie.worldMap.styles;

import java.util.HashMap;
import zombie.core.textures.Texture;
import zombie.core.math.PZMath;
import zombie.worldMap.WorldMapFeature;
import zombie.Lua.LuaManager;
import java.util.Objects;
import java.util.ArrayList;
import zombie.worldMap.UIWorldMapV1;
import zombie.worldMap.UIWorldMap;

public class WorldMapStyleV1
{
    public UIWorldMap m_ui;
    public UIWorldMapV1 m_api;
    public WorldMapStyle m_style;
    public final ArrayList<WorldMapStyleLayerV1> m_layers;
    
    public WorldMapStyleV1(final UIWorldMap uiWorldMap) {
        this.m_layers = new ArrayList<WorldMapStyleLayerV1>();
        Objects.requireNonNull(uiWorldMap);
        this.m_ui = uiWorldMap;
        this.m_api = uiWorldMap.getAPIv1();
        this.m_style = this.m_api.getStyle();
    }
    
    public WorldMapStyleLayerV1 newLineLayer(final String s) throws IllegalArgumentException {
        final WorldMapLineStyleLayerV1 e = new WorldMapLineStyleLayerV1(this, s);
        this.m_layers.add(e);
        return e;
    }
    
    public WorldMapStyleLayerV1 newPolygonLayer(final String s) throws IllegalArgumentException {
        final WorldMapPolygonStyleLayerV1 e = new WorldMapPolygonStyleLayerV1(this, s);
        this.m_layers.add(e);
        return e;
    }
    
    public WorldMapStyleLayerV1 newTextureLayer(final String s) throws IllegalArgumentException {
        final WorldMapTextureStyleLayerV1 e = new WorldMapTextureStyleLayerV1(this, s);
        this.m_layers.add(e);
        return e;
    }
    
    public int getLayerCount() {
        return this.m_layers.size();
    }
    
    public WorldMapStyleLayerV1 getLayerByIndex(final int index) {
        return this.m_layers.get(index);
    }
    
    public WorldMapStyleLayerV1 getLayerByName(final String s) {
        final int indexOfLayer = this.indexOfLayer(s);
        return (indexOfLayer == -1) ? null : this.m_layers.get(indexOfLayer);
    }
    
    public int indexOfLayer(final String anObject) {
        for (int i = 0; i < this.m_layers.size(); ++i) {
            if (this.m_layers.get(i).m_layer.m_id.equals(anObject)) {
                return i;
            }
        }
        return -1;
    }
    
    public void moveLayer(final int n, final int n2) {
        this.m_style.m_layers.add(n2, this.m_style.m_layers.remove(n));
        this.m_layers.add(n2, this.m_layers.remove(n));
    }
    
    public void removeLayerById(final String s) {
        final int indexOfLayer = this.indexOfLayer(s);
        if (indexOfLayer == -1) {
            return;
        }
        this.removeLayerByIndex(indexOfLayer);
    }
    
    public void removeLayerByIndex(final int n) {
        this.m_style.m_layers.remove(n);
        this.m_layers.remove(n);
    }
    
    public void clear() {
        this.m_style.m_layers.clear();
        this.m_layers.clear();
    }
    
    public static void setExposed(final LuaManager.Exposer exposer) {
        exposer.setExposed(WorldMapStyleV1.class);
        exposer.setExposed(WorldMapStyleLayerV1.class);
        exposer.setExposed(WorldMapLineStyleLayerV1.class);
        exposer.setExposed(WorldMapPolygonStyleLayerV1.class);
        exposer.setExposed(WorldMapTextureStyleLayerV1.class);
    }
    
    public static class WorldMapStyleLayerV1
    {
        WorldMapStyleV1 m_owner;
        WorldMapStyleLayer m_layer;
        
        WorldMapStyleLayerV1(final WorldMapStyleV1 owner, final WorldMapStyleLayer layer) {
            this.m_owner = owner;
            this.m_layer = layer;
            owner.m_style.m_layers.add(this.m_layer);
        }
        
        public String getTypeString() {
            return this.m_layer.getTypeString();
        }
        
        public void setId(final String id) {
            this.m_layer.m_id = id;
        }
        
        public String getId() {
            return this.m_layer.m_id;
        }
        
        public void setMinZoom(final float minZoom) {
            this.m_layer.m_minZoom = minZoom;
        }
        
        public float getMinZoom() {
            return this.m_layer.m_minZoom;
        }
    }
    
    public static class WorldMapLineStyleLayerV1 extends WorldMapStyleLayerV1
    {
        WorldMapLineStyleLayer m_lineStyle;
        
        WorldMapLineStyleLayerV1(final WorldMapStyleV1 worldMapStyleV1, final String s) {
            super(worldMapStyleV1, new WorldMapLineStyleLayer(s));
            this.m_lineStyle = (WorldMapLineStyleLayer)this.m_layer;
        }
        
        public void setFilter(final String s, final String filterValue) {
            this.m_lineStyle.m_filterKey = s;
            this.m_lineStyle.m_filterValue = filterValue;
            this.m_lineStyle.m_filter = ((worldMapFeature, p3) -> worldMapFeature.hasLineString() && filterValue.equals(worldMapFeature.m_properties.get(s)));
        }
        
        public void addFill(final float n, final int n2, final int n3, final int n4, final int n5) {
            this.m_lineStyle.m_fill.add(new WorldMapStyleLayer.ColorStop(n, n2, n3, n4, n5));
        }
        
        public void addLineWidth(final float n, final float n2) {
            this.m_lineStyle.m_lineWidth.add(new WorldMapStyleLayer.FloatStop(n, n2));
        }
    }
    
    public static class WorldMapPolygonStyleLayerV1 extends WorldMapStyleLayerV1
    {
        WorldMapPolygonStyleLayer m_polygonStyle;
        
        WorldMapPolygonStyleLayerV1(final WorldMapStyleV1 worldMapStyleV1, final String s) {
            super(worldMapStyleV1, new WorldMapPolygonStyleLayer(s));
            this.m_polygonStyle = (WorldMapPolygonStyleLayer)this.m_layer;
        }
        
        public void setFilter(final String s, final String filterValue) {
            this.m_polygonStyle.m_filterKey = s;
            this.m_polygonStyle.m_filterValue = filterValue;
            this.m_polygonStyle.m_filter = ((worldMapFeature, p3) -> worldMapFeature.hasPolygon() && filterValue.equals(worldMapFeature.m_properties.get(s)));
        }
        
        public String getFilterKey() {
            return this.m_polygonStyle.m_filterKey;
        }
        
        public String getFilterValue() {
            return this.m_polygonStyle.m_filterValue;
        }
        
        public void addFill(final float n, final int n2, final int n3, final int n4, final int n5) {
            this.m_polygonStyle.m_fill.add(new WorldMapStyleLayer.ColorStop(n, n2, n3, n4, n5));
        }
        
        public void addScale(final float n, final float n2) {
            this.m_polygonStyle.m_scale.add(new WorldMapStyleLayer.FloatStop(n, n2));
        }
        
        public void addTexture(final float n, final String s) {
            this.m_polygonStyle.m_texture.add(new WorldMapStyleLayer.TextureStop(n, s));
        }
        
        public void removeFill(final int index) {
            this.m_polygonStyle.m_fill.remove(index);
        }
        
        public void removeTexture(final int index) {
            this.m_polygonStyle.m_texture.remove(index);
        }
        
        public void moveFill(final int index, final int index2) {
            this.m_polygonStyle.m_fill.add(index2, this.m_polygonStyle.m_fill.remove(index));
        }
        
        public void moveTexture(final int index, final int index2) {
            this.m_polygonStyle.m_texture.add(index2, this.m_polygonStyle.m_texture.remove(index));
        }
        
        public int getFillStops() {
            return this.m_polygonStyle.m_fill.size();
        }
        
        public void setFillRGBA(final int n, final int r, final int g, final int b, final int a) {
            this.m_polygonStyle.m_fill.get(n).r = r;
            this.m_polygonStyle.m_fill.get(n).g = g;
            this.m_polygonStyle.m_fill.get(n).b = b;
            this.m_polygonStyle.m_fill.get(n).a = a;
        }
        
        public void setFillZoom(final int index, final float n) {
            this.m_polygonStyle.m_fill.get(index).m_zoom = PZMath.clamp(n, 0.0f, 24.0f);
        }
        
        public float getFillZoom(final int index) {
            return this.m_polygonStyle.m_fill.get(index).m_zoom;
        }
        
        public int getFillRed(final int index) {
            return this.m_polygonStyle.m_fill.get(index).r;
        }
        
        public int getFillGreen(final int index) {
            return this.m_polygonStyle.m_fill.get(index).g;
        }
        
        public int getFillBlue(final int index) {
            return this.m_polygonStyle.m_fill.get(index).b;
        }
        
        public int getFillAlpha(final int index) {
            return this.m_polygonStyle.m_fill.get(index).a;
        }
        
        public int getTextureStops() {
            return this.m_polygonStyle.m_texture.size();
        }
        
        public void setTextureZoom(final int index, final float n) {
            this.m_polygonStyle.m_texture.get(index).m_zoom = PZMath.clamp(n, 0.0f, 24.0f);
        }
        
        public float getTextureZoom(final int index) {
            return this.m_polygonStyle.m_texture.get(index).m_zoom;
        }
        
        public void setTexturePath(final int n, final String texturePath) {
            this.m_polygonStyle.m_texture.get(n).texturePath = texturePath;
            this.m_polygonStyle.m_texture.get(n).texture = Texture.getTexture(texturePath);
        }
        
        public String getTexturePath(final int index) {
            return this.m_polygonStyle.m_texture.get(index).texturePath;
        }
        
        public Texture getTexture(final int index) {
            return this.m_polygonStyle.m_texture.get(index).texture;
        }
    }
    
    public static class WorldMapTextureStyleLayerV1 extends WorldMapStyleLayerV1
    {
        WorldMapTextureStyleLayer m_textureStyle;
        
        WorldMapTextureStyleLayerV1(final WorldMapStyleV1 worldMapStyleV1, final String s) {
            super(worldMapStyleV1, new WorldMapTextureStyleLayer(s));
            this.m_textureStyle = (WorldMapTextureStyleLayer)this.m_layer;
        }
        
        public void addFill(final float n, final int n2, final int n3, final int n4, final int n5) {
            this.m_textureStyle.m_fill.add(new WorldMapStyleLayer.ColorStop(n, n2, n3, n4, n5));
        }
        
        public void addTexture(final float n, final String s) {
            this.m_textureStyle.m_texture.add(new WorldMapStyleLayer.TextureStop(n, s));
        }
        
        public void removeFill(final int index) {
            this.m_textureStyle.m_fill.remove(index);
        }
        
        public void removeAllFill() {
            this.m_textureStyle.m_fill.clear();
        }
        
        public void removeTexture(final int index) {
            this.m_textureStyle.m_texture.remove(index);
        }
        
        public void removeAllTexture() {
            this.m_textureStyle.m_texture.clear();
        }
        
        public void moveFill(final int index, final int index2) {
            this.m_textureStyle.m_fill.add(index2, this.m_textureStyle.m_fill.remove(index));
        }
        
        public void moveTexture(final int index, final int index2) {
            this.m_textureStyle.m_texture.add(index2, this.m_textureStyle.m_texture.remove(index));
        }
        
        public void setBoundsInSquares(final int worldX1, final int worldY1, final int worldX2, final int worldY2) {
            this.m_textureStyle.m_worldX1 = worldX1;
            this.m_textureStyle.m_worldY1 = worldY1;
            this.m_textureStyle.m_worldX2 = worldX2;
            this.m_textureStyle.m_worldY2 = worldY2;
        }
        
        public int getMinXInSquares() {
            return this.m_textureStyle.m_worldX1;
        }
        
        public int getMinYInSquares() {
            return this.m_textureStyle.m_worldY1;
        }
        
        public int getMaxXInSquares() {
            return this.m_textureStyle.m_worldX2;
        }
        
        public int getMaxYInSquares() {
            return this.m_textureStyle.m_worldY2;
        }
        
        public int getWidthInSquares() {
            return this.m_textureStyle.m_worldX2 - this.m_textureStyle.m_worldX1;
        }
        
        public int getHeightInSquares() {
            return this.m_textureStyle.m_worldY2 - this.m_textureStyle.m_worldY1;
        }
        
        public void setTile(final boolean tile) {
            this.m_textureStyle.m_tile = tile;
        }
        
        public boolean isTile() {
            return this.m_textureStyle.m_tile;
        }
        
        public void setUseWorldBounds(final boolean useWorldBounds) {
            this.m_textureStyle.m_useWorldBounds = useWorldBounds;
        }
        
        public boolean isUseWorldBounds() {
            return this.m_textureStyle.m_useWorldBounds;
        }
        
        public int getFillStops() {
            return this.m_textureStyle.m_fill.size();
        }
        
        public void setFillRGBA(final int n, final int r, final int g, final int b, final int a) {
            this.m_textureStyle.m_fill.get(n).r = r;
            this.m_textureStyle.m_fill.get(n).g = g;
            this.m_textureStyle.m_fill.get(n).b = b;
            this.m_textureStyle.m_fill.get(n).a = a;
        }
        
        public void setFillZoom(final int index, final float n) {
            this.m_textureStyle.m_fill.get(index).m_zoom = PZMath.clamp(n, 0.0f, 24.0f);
        }
        
        public float getFillZoom(final int index) {
            return this.m_textureStyle.m_fill.get(index).m_zoom;
        }
        
        public int getFillRed(final int index) {
            return this.m_textureStyle.m_fill.get(index).r;
        }
        
        public int getFillGreen(final int index) {
            return this.m_textureStyle.m_fill.get(index).g;
        }
        
        public int getFillBlue(final int index) {
            return this.m_textureStyle.m_fill.get(index).b;
        }
        
        public int getFillAlpha(final int index) {
            return this.m_textureStyle.m_fill.get(index).a;
        }
        
        public int getTextureStops() {
            return this.m_textureStyle.m_texture.size();
        }
        
        public void setTextureZoom(final int index, final float n) {
            this.m_textureStyle.m_texture.get(index).m_zoom = PZMath.clamp(n, 0.0f, 24.0f);
        }
        
        public float getTextureZoom(final int index) {
            return this.m_textureStyle.m_texture.get(index).m_zoom;
        }
        
        public void setTexturePath(final int n, final String texturePath) {
            this.m_textureStyle.m_texture.get(n).texturePath = texturePath;
            this.m_textureStyle.m_texture.get(n).texture = Texture.getTexture(texturePath);
        }
        
        public String getTexturePath(final int index) {
            return this.m_textureStyle.m_texture.get(index).texturePath;
        }
        
        public Texture getTexture(final int index) {
            return this.m_textureStyle.m_texture.get(index).texture;
        }
    }
}

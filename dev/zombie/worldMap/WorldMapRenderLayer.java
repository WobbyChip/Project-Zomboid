// 
// Decompiled by Procyon v0.5.36
// 

package zombie.worldMap;

import zombie.popman.ObjectPool;
import java.util.ArrayList;
import zombie.worldMap.styles.WorldMapStyleLayer;

public final class WorldMapRenderLayer
{
    WorldMapStyleLayer m_styleLayer;
    final ArrayList<WorldMapFeature> m_features;
    static ObjectPool<WorldMapRenderLayer> s_pool;
    
    public WorldMapRenderLayer() {
        this.m_features = new ArrayList<WorldMapFeature>();
    }
    
    static {
        WorldMapRenderLayer.s_pool = new ObjectPool<WorldMapRenderLayer>(WorldMapRenderLayer::new);
    }
}

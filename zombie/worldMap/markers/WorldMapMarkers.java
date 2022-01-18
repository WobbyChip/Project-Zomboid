// 
// Decompiled by Procyon v0.5.36
// 

package zombie.worldMap.markers;

import zombie.worldMap.UIWorldMap;
import java.util.ArrayList;
import zombie.util.Pool;

public final class WorldMapMarkers
{
    private static final Pool<WorldMapGridSquareMarker> s_gridSquareMarkerPool;
    private final ArrayList<WorldMapMarker> m_markers;
    
    public WorldMapMarkers() {
        this.m_markers = new ArrayList<WorldMapMarker>();
    }
    
    public WorldMapGridSquareMarker addGridSquareMarker(final int n, final int n2, final int n3, final float n4, final float n5, final float n6, final float n7) {
        final WorldMapGridSquareMarker init = WorldMapMarkers.s_gridSquareMarkerPool.alloc().init(n, n2, n3, n4, n5, n6, n7);
        this.m_markers.add(init);
        return init;
    }
    
    public void removeMarker(final WorldMapMarker worldMapMarker) {
        if (!this.m_markers.contains(worldMapMarker)) {
            return;
        }
        this.m_markers.remove(worldMapMarker);
        worldMapMarker.release();
    }
    
    public void clear() {
        for (int i = 0; i < this.m_markers.size(); ++i) {
            this.m_markers.get(i).release();
        }
        this.m_markers.clear();
    }
    
    public void render(final UIWorldMap uiWorldMap) {
        for (int i = 0; i < this.m_markers.size(); ++i) {
            this.m_markers.get(i).render(uiWorldMap);
        }
    }
    
    static {
        s_gridSquareMarkerPool = new Pool<WorldMapGridSquareMarker>(WorldMapGridSquareMarker::new);
    }
}

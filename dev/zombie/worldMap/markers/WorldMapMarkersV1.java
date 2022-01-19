// 
// Decompiled by Procyon v0.5.36
// 

package zombie.worldMap.markers;

import zombie.Lua.LuaManager;
import java.util.Objects;
import java.util.ArrayList;
import zombie.worldMap.UIWorldMap;

public class WorldMapMarkersV1
{
    private final UIWorldMap m_ui;
    private final ArrayList<WorldMapMarkerV1> m_markers;
    
    public WorldMapMarkersV1(final UIWorldMap uiWorldMap) {
        this.m_markers = new ArrayList<WorldMapMarkerV1>();
        Objects.requireNonNull(uiWorldMap);
        this.m_ui = uiWorldMap;
    }
    
    public WorldMapGridSquareMarkerV1 addGridSquareMarker(final int n, final int n2, final int n3, final float n4, final float n5, final float n6, final float n7) {
        final WorldMapGridSquareMarkerV1 e = new WorldMapGridSquareMarkerV1(this.m_ui.getAPIv1().getMarkers().addGridSquareMarker(n, n2, n3, n4, n5, n6, n7));
        this.m_markers.add(e);
        return e;
    }
    
    public void removeMarker(final WorldMapMarkerV1 o) {
        if (!this.m_markers.remove(o)) {
            return;
        }
        this.m_ui.getAPIv1().getMarkers().removeMarker(o.m_marker);
    }
    
    public void clear() {
        this.m_ui.getAPIv1().getMarkers().clear();
        this.m_markers.clear();
    }
    
    public static void setExposed(final LuaManager.Exposer exposer) {
        exposer.setExposed(WorldMapMarkersV1.class);
        exposer.setExposed(WorldMapMarkerV1.class);
        exposer.setExposed(WorldMapGridSquareMarkerV1.class);
    }
    
    public static class WorldMapMarkerV1
    {
        final WorldMapMarker m_marker;
        
        WorldMapMarkerV1(final WorldMapMarker marker) {
            this.m_marker = marker;
        }
    }
    
    public static final class WorldMapGridSquareMarkerV1 extends WorldMapMarkerV1
    {
        final WorldMapGridSquareMarker m_gridSquareMarker;
        
        WorldMapGridSquareMarkerV1(final WorldMapGridSquareMarker gridSquareMarker) {
            super(gridSquareMarker);
            this.m_gridSquareMarker = gridSquareMarker;
        }
        
        public void setBlink(final boolean blink) {
            this.m_gridSquareMarker.setBlink(blink);
        }
        
        public void setMinScreenRadius(final int minScreenRadius) {
            this.m_gridSquareMarker.setMinScreenRadius(minScreenRadius);
        }
    }
}

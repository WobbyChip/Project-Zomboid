// 
// Decompiled by Procyon v0.5.36
// 

package zombie.worldMap;

import java.util.Iterator;
import java.util.ArrayList;

public final class WorldMapFeature
{
    public final WorldMapCell m_cell;
    public final ArrayList<WorldMapGeometry> m_geometries;
    public WorldMapProperties m_properties;
    
    WorldMapFeature(final WorldMapCell cell) {
        this.m_geometries = new ArrayList<WorldMapGeometry>();
        this.m_properties = null;
        this.m_cell = cell;
    }
    
    public boolean hasLineString() {
        for (int i = 0; i < this.m_geometries.size(); ++i) {
            if (this.m_geometries.get(i).m_type == WorldMapGeometry.Type.LineString) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasPoint() {
        for (int i = 0; i < this.m_geometries.size(); ++i) {
            if (this.m_geometries.get(i).m_type == WorldMapGeometry.Type.Point) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasPolygon() {
        for (int i = 0; i < this.m_geometries.size(); ++i) {
            if (this.m_geometries.get(i).m_type == WorldMapGeometry.Type.Polygon) {
                return true;
            }
        }
        return false;
    }
    
    public boolean containsPoint(final float n, final float n2) {
        for (int i = 0; i < this.m_geometries.size(); ++i) {
            if (this.m_geometries.get(i).containsPoint(n, n2)) {
                return true;
            }
        }
        return false;
    }
    
    public void dispose() {
        final Iterator<WorldMapGeometry> iterator = this.m_geometries.iterator();
        while (iterator.hasNext()) {
            iterator.next().dispose();
        }
        this.m_properties.clear();
    }
}

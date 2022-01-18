// 
// Decompiled by Procyon v0.5.36
// 

package zombie.worldMap;

import java.util.Iterator;
import java.util.ArrayList;

public final class WorldMapCell
{
    public int m_x;
    public int m_y;
    public final ArrayList<WorldMapFeature> m_features;
    
    public WorldMapCell() {
        this.m_features = new ArrayList<WorldMapFeature>();
    }
    
    public void hitTest(float n, float n2, final ArrayList<WorldMapFeature> list) {
        n -= this.m_x * 300;
        n2 -= this.m_y * 300;
        for (int i = 0; i < this.m_features.size(); ++i) {
            final WorldMapFeature e = this.m_features.get(i);
            if (e.containsPoint(n, n2)) {
                list.add(e);
            }
        }
    }
    
    public void dispose() {
        final Iterator<WorldMapFeature> iterator = this.m_features.iterator();
        while (iterator.hasNext()) {
            iterator.next().dispose();
        }
        this.m_features.clear();
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.worldMap.styles;

import java.util.Collection;
import java.util.ArrayList;

public final class WorldMapStyle
{
    public final ArrayList<WorldMapStyleLayer> m_layers;
    
    public WorldMapStyle() {
        this.m_layers = new ArrayList<WorldMapStyleLayer>();
    }
    
    public void copyFrom(final WorldMapStyle worldMapStyle) {
        this.m_layers.clear();
        this.m_layers.addAll(worldMapStyle.m_layers);
    }
}

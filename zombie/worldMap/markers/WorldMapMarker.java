// 
// Decompiled by Procyon v0.5.36
// 

package zombie.worldMap.markers;

import zombie.worldMap.UIWorldMap;
import zombie.util.PooledObject;

public abstract class WorldMapMarker extends PooledObject
{
    abstract void render(final UIWorldMap p0);
}

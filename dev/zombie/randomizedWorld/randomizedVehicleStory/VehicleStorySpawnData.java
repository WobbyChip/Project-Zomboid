// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedVehicleStory;

import zombie.iso.IsoChunk;
import zombie.iso.IsoMetaGrid;

public final class VehicleStorySpawnData
{
    public RandomizedVehicleStoryBase m_story;
    public IsoMetaGrid.Zone m_zone;
    public float m_spawnX;
    public float m_spawnY;
    public float m_direction;
    public int m_x1;
    public int m_y1;
    public int m_x2;
    public int m_y2;
    
    public VehicleStorySpawnData(final RandomizedVehicleStoryBase story, final IsoMetaGrid.Zone zone, final float spawnX, final float spawnY, final float direction, final int x1, final int y1, final int x2, final int y2) {
        this.m_story = story;
        this.m_zone = zone;
        this.m_spawnX = spawnX;
        this.m_spawnY = spawnY;
        this.m_direction = direction;
        this.m_x1 = x1;
        this.m_y1 = y1;
        this.m_x2 = x2;
        this.m_y2 = y2;
    }
    
    public boolean isValid(final IsoMetaGrid.Zone zone, final IsoChunk isoChunk) {
        if (zone != this.m_zone) {
            return false;
        }
        if (!this.m_story.isFullyStreamedIn(this.m_x1, this.m_y1, this.m_x2, this.m_y2)) {
            return false;
        }
        isoChunk.setRandomVehicleStoryToSpawnLater(null);
        return this.m_story.isValid(zone, isoChunk, false);
    }
}

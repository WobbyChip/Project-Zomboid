// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import zombie.characters.IsoPlayer;

public class IsoHeatSource
{
    private int x;
    private int y;
    private int z;
    private int radius;
    private int temperature;
    
    public IsoHeatSource(final int x, final int y, final int z, final int radius, final int temperature) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
        this.temperature = temperature;
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public int getZ() {
        return this.z;
    }
    
    public int getRadius() {
        return this.radius;
    }
    
    public void setRadius(final int radius) {
        this.radius = radius;
    }
    
    public int getTemperature() {
        return this.temperature;
    }
    
    public void setTemperature(final int temperature) {
        this.temperature = temperature;
    }
    
    public boolean isInBounds(final int n, final int n2, final int n3, final int n4) {
        return this.x >= n && this.x < n3 && this.y >= n2 && this.y < n4;
    }
    
    public boolean isInBounds() {
        final IsoChunkMap[] chunkMap = IsoWorld.instance.CurrentCell.ChunkMap;
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            if (!chunkMap[i].ignore) {
                if (this.isInBounds(chunkMap[i].getWorldXMinTiles(), chunkMap[i].getWorldYMinTiles(), chunkMap[i].getWorldXMaxTiles(), chunkMap[i].getWorldYMaxTiles())) {
                    return true;
                }
            }
        }
        return false;
    }
}

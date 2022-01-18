// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.stash;

public final class StashBuilding
{
    public int buildingX;
    public int buildingY;
    public String stashName;
    
    public StashBuilding(final String stashName, final int buildingX, final int buildingY) {
        this.stashName = stashName;
        this.buildingX = buildingX;
        this.buildingY = buildingY;
    }
    
    public String getName() {
        return this.stashName;
    }
}

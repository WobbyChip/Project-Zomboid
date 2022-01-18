// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.areas.isoregion.jobs;

public abstract class RegionJob
{
    private final RegionJobType type;
    
    protected RegionJob(final RegionJobType type) {
        this.type = type;
    }
    
    protected void reset() {
    }
    
    public RegionJobType getJobType() {
        return this.type;
    }
}

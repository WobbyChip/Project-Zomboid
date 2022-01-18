// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.areas.isoregion.jobs;

public class JobApplyChanges extends RegionJob
{
    protected boolean saveToDisk;
    
    protected JobApplyChanges() {
        super(RegionJobType.ApplyChanges);
    }
    
    @Override
    protected void reset() {
        this.saveToDisk = false;
    }
    
    public boolean isSaveToDisk() {
        return this.saveToDisk;
    }
}

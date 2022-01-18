// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.areas.isoregion.jobs;

public enum RegionJobType
{
    SquareUpdate, 
    ChunkUpdate, 
    ApplyChanges, 
    ServerSendFullData, 
    DebugResetAllData;
    
    private static /* synthetic */ RegionJobType[] $values() {
        return new RegionJobType[] { RegionJobType.SquareUpdate, RegionJobType.ChunkUpdate, RegionJobType.ApplyChanges, RegionJobType.ServerSendFullData, RegionJobType.DebugResetAllData };
    }
    
    static {
        $VALUES = $values();
    }
}

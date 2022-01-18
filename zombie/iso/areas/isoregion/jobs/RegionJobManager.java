// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.areas.isoregion.jobs;

import zombie.core.Core;
import zombie.core.raknet.UdpConnection;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class RegionJobManager
{
    private static final ConcurrentLinkedQueue<JobSquareUpdate> poolSquareUpdate;
    private static final ConcurrentLinkedQueue<JobChunkUpdate> poolChunkUpdate;
    private static final ConcurrentLinkedQueue<JobApplyChanges> poolApplyChanges;
    private static final ConcurrentLinkedQueue<JobServerSendFullData> poolServerSendFullData;
    private static final ConcurrentLinkedQueue<JobDebugResetAllData> poolDebugResetAllData;
    
    public static JobSquareUpdate allocSquareUpdate(final int worldSquareX, final int worldSquareY, final int worldSquareZ, final byte newSquareFlags) {
        JobSquareUpdate jobSquareUpdate = RegionJobManager.poolSquareUpdate.poll();
        if (jobSquareUpdate == null) {
            jobSquareUpdate = new JobSquareUpdate();
        }
        jobSquareUpdate.worldSquareX = worldSquareX;
        jobSquareUpdate.worldSquareY = worldSquareY;
        jobSquareUpdate.worldSquareZ = worldSquareZ;
        jobSquareUpdate.newSquareFlags = newSquareFlags;
        return jobSquareUpdate;
    }
    
    public static JobChunkUpdate allocChunkUpdate() {
        JobChunkUpdate jobChunkUpdate = RegionJobManager.poolChunkUpdate.poll();
        if (jobChunkUpdate == null) {
            jobChunkUpdate = new JobChunkUpdate();
        }
        return jobChunkUpdate;
    }
    
    public static JobApplyChanges allocApplyChanges(final boolean saveToDisk) {
        JobApplyChanges jobApplyChanges = RegionJobManager.poolApplyChanges.poll();
        if (jobApplyChanges == null) {
            jobApplyChanges = new JobApplyChanges();
        }
        jobApplyChanges.saveToDisk = saveToDisk;
        return jobApplyChanges;
    }
    
    public static JobServerSendFullData allocServerSendFullData(final UdpConnection targetConn) {
        JobServerSendFullData jobServerSendFullData = RegionJobManager.poolServerSendFullData.poll();
        if (jobServerSendFullData == null) {
            jobServerSendFullData = new JobServerSendFullData();
        }
        jobServerSendFullData.targetConn = targetConn;
        return jobServerSendFullData;
    }
    
    public static JobDebugResetAllData allocDebugResetAllData() {
        JobDebugResetAllData jobDebugResetAllData = RegionJobManager.poolDebugResetAllData.poll();
        if (jobDebugResetAllData == null) {
            jobDebugResetAllData = new JobDebugResetAllData();
        }
        return jobDebugResetAllData;
    }
    
    public static void release(final RegionJob regionJob) {
        regionJob.reset();
        switch (regionJob.getJobType()) {
            case SquareUpdate: {
                RegionJobManager.poolSquareUpdate.add((JobSquareUpdate)regionJob);
                break;
            }
            case ApplyChanges: {
                RegionJobManager.poolApplyChanges.add((JobApplyChanges)regionJob);
                break;
            }
            case ChunkUpdate: {
                RegionJobManager.poolChunkUpdate.add((JobChunkUpdate)regionJob);
                break;
            }
            case ServerSendFullData: {
                RegionJobManager.poolServerSendFullData.add((JobServerSendFullData)regionJob);
                break;
            }
            case DebugResetAllData: {
                RegionJobManager.poolDebugResetAllData.add((JobDebugResetAllData)regionJob);
                break;
            }
            default: {
                if (Core.bDebug) {
                    throw new RuntimeException("No pooling for this job type?");
                }
                break;
            }
        }
    }
    
    static {
        poolSquareUpdate = new ConcurrentLinkedQueue<JobSquareUpdate>();
        poolChunkUpdate = new ConcurrentLinkedQueue<JobChunkUpdate>();
        poolApplyChanges = new ConcurrentLinkedQueue<JobApplyChanges>();
        poolServerSendFullData = new ConcurrentLinkedQueue<JobServerSendFullData>();
        poolDebugResetAllData = new ConcurrentLinkedQueue<JobDebugResetAllData>();
    }
}

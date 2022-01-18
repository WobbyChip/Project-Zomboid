// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.areas.isoregion;

import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import zombie.iso.IsoChunk;
import zombie.iso.IsoWorld;
import zombie.network.ServerMap;
import zombie.iso.IsoChunkMap;
import zombie.core.Colors;
import zombie.core.network.ByteBufferWriter;
import zombie.network.PacketTypes;
import java.io.File;
import java.util.Collection;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import zombie.network.GameClient;
import zombie.iso.areas.isoregion.jobs.JobApplyChanges;
import zombie.iso.areas.isoregion.jobs.JobSquareUpdate;
import java.util.Iterator;
import zombie.core.raknet.UdpConnection;
import zombie.iso.areas.isoregion.jobs.RegionJobType;
import zombie.core.Core;
import zombie.iso.areas.isoregion.data.DataChunk;
import zombie.iso.areas.isoregion.jobs.JobServerSendFullData;
import zombie.network.GameServer;
import zombie.debug.DebugType;
import zombie.iso.areas.isoregion.jobs.RegionJobManager;
import zombie.debug.DebugLog;
import zombie.GameWindow;
import zombie.core.ThreadGroups;
import java.util.ArrayList;
import java.nio.ByteBuffer;
import zombie.iso.areas.isoregion.jobs.JobChunkUpdate;
import zombie.iso.areas.isoregion.jobs.RegionJob;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.List;
import zombie.iso.areas.isoregion.data.DataRoot;
import java.util.concurrent.atomic.AtomicBoolean;

public final class IsoRegionWorker
{
    private Thread thread;
    private boolean bFinished;
    protected static final AtomicBoolean isRequestingBufferSwap;
    private static IsoRegionWorker instance;
    private DataRoot rootBuffer;
    private List<Integer> discoveredChunks;
    private final List<Integer> threadDiscoveredChunks;
    private int lastThreadDiscoveredChunksSize;
    private final ConcurrentLinkedQueue<RegionJob> jobQueue;
    private final ConcurrentLinkedQueue<JobChunkUpdate> jobOutgoingQueue;
    private final List<RegionJob> jobBatchedProcessing;
    private final ConcurrentLinkedQueue<RegionJob> finishedJobQueue;
    private static final ByteBuffer byteBuffer;
    
    protected IsoRegionWorker() {
        this.rootBuffer = new DataRoot();
        this.discoveredChunks = new ArrayList<Integer>();
        this.threadDiscoveredChunks = new ArrayList<Integer>();
        this.lastThreadDiscoveredChunksSize = 0;
        this.jobQueue = new ConcurrentLinkedQueue<RegionJob>();
        this.jobOutgoingQueue = new ConcurrentLinkedQueue<JobChunkUpdate>();
        this.jobBatchedProcessing = new ArrayList<RegionJob>();
        this.finishedJobQueue = new ConcurrentLinkedQueue<RegionJob>();
        IsoRegionWorker.instance = this;
    }
    
    protected void create() {
        if (this.thread != null) {
            return;
        }
        this.bFinished = false;
        (this.thread = new Thread(ThreadGroups.Workers, () -> {
            while (!this.bFinished) {
                try {
                    this.thread_main_loop();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return;
        })).setPriority(5);
        this.thread.setDaemon(true);
        this.thread.setName("IsoRegionWorker");
        this.thread.setUncaughtExceptionHandler(GameWindow::uncaughtException);
        this.thread.start();
    }
    
    protected void stop() {
        if (this.thread == null) {
            return;
        }
        if (this.thread != null) {
            this.bFinished = true;
            while (this.thread.isAlive()) {}
            this.thread = null;
        }
        if (this.jobQueue.size() > 0) {
            DebugLog.IsoRegion.warn((Object)"IsoRegionWorker -> JobQueue has items remaining");
        }
        if (this.jobBatchedProcessing.size() > 0) {
            DebugLog.IsoRegion.warn((Object)"IsoRegionWorker -> JobBatchedProcessing has items remaining");
        }
        this.jobQueue.clear();
        this.jobOutgoingQueue.clear();
        this.jobBatchedProcessing.clear();
        this.finishedJobQueue.clear();
        this.rootBuffer = null;
        this.discoveredChunks = null;
    }
    
    protected void EnqueueJob(final RegionJob e) {
        this.jobQueue.add(e);
    }
    
    protected void ApplyChunkChanges() {
        this.ApplyChunkChanges(true);
    }
    
    protected void ApplyChunkChanges(final boolean b) {
        this.jobQueue.add(RegionJobManager.allocApplyChanges(b));
    }
    
    private void thread_main_loop() throws InterruptedException, IsoRegionException {
        IsoRegions.PRINT_D = DebugLog.isEnabled(DebugType.IsoRegion);
        for (RegionJob e = this.jobQueue.poll(); e != null; e = this.jobQueue.poll()) {
            switch (e.getJobType()) {
                case ServerSendFullData: {
                    if (!GameServer.bServer) {
                        break;
                    }
                    final UdpConnection targetConn = ((JobServerSendFullData)e).getTargetConn();
                    if (targetConn != null) {
                        IsoRegions.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, targetConn.idStr));
                        final ArrayList<DataChunk> list = new ArrayList<DataChunk>();
                        this.rootBuffer.getAllChunks(list);
                        JobChunkUpdate jobChunkUpdate = RegionJobManager.allocChunkUpdate();
                        jobChunkUpdate.setTargetConn(targetConn);
                        for (final DataChunk dataChunk : list) {
                            if (!jobChunkUpdate.canAddChunk()) {
                                this.jobOutgoingQueue.add(jobChunkUpdate);
                                jobChunkUpdate = RegionJobManager.allocChunkUpdate();
                                jobChunkUpdate.setTargetConn(targetConn);
                            }
                            jobChunkUpdate.addChunkFromDataChunk(dataChunk);
                        }
                        if (jobChunkUpdate.getChunkCount() > 0) {
                            this.jobOutgoingQueue.add(jobChunkUpdate);
                        }
                        else {
                            RegionJobManager.release(jobChunkUpdate);
                        }
                        this.finishedJobQueue.add(e);
                        break;
                    }
                    if (Core.bDebug) {
                        throw new IsoRegionException("IsoRegion: Server send full data target connection == null");
                    }
                    IsoRegions.warn("IsoRegion: Server send full data target connection == null");
                    break;
                }
                case DebugResetAllData: {
                    IsoRegions.log("IsoRegion: Debug Reset All Data");
                    for (int i = 0; i < 2; ++i) {
                        this.rootBuffer.resetAllData();
                        if (i == 0) {
                            IsoRegionWorker.isRequestingBufferSwap.set(true);
                            while (IsoRegionWorker.isRequestingBufferSwap.get()) {
                                if (this.bFinished) {
                                    break;
                                }
                                Thread.sleep(5L);
                            }
                        }
                    }
                    this.finishedJobQueue.add(e);
                    break;
                }
                case SquareUpdate:
                case ChunkUpdate:
                case ApplyChanges: {
                    IsoRegions.log(invokedynamic(makeConcatWithConstants:(Lzombie/iso/areas/isoregion/jobs/RegionJobType;)Ljava/lang/String;, e.getJobType()));
                    this.jobBatchedProcessing.add(e);
                    if (e.getJobType() == RegionJobType.ApplyChanges) {
                        this.thread_run_batched_jobs();
                        this.jobBatchedProcessing.clear();
                        break;
                    }
                    break;
                }
                default: {
                    this.finishedJobQueue.add(e);
                    break;
                }
            }
        }
        Thread.sleep(20L);
    }
    
    private void thread_run_batched_jobs() throws InterruptedException {
        IsoRegions.log(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.jobBatchedProcessing.size()));
        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < this.jobBatchedProcessing.size(); ++j) {
                final RegionJob regionJob = this.jobBatchedProcessing.get(j);
                switch (regionJob.getJobType()) {
                    case SquareUpdate: {
                        final JobSquareUpdate jobSquareUpdate = (JobSquareUpdate)regionJob;
                        this.rootBuffer.updateExistingSquare(jobSquareUpdate.getWorldSquareX(), jobSquareUpdate.getWorldSquareY(), jobSquareUpdate.getWorldSquareZ(), jobSquareUpdate.getNewSquareFlags());
                        break;
                    }
                    case ChunkUpdate: {
                        ((JobChunkUpdate)regionJob).readChunksPacket(this.rootBuffer, this.threadDiscoveredChunks);
                        break;
                    }
                    case ApplyChanges: {
                        this.rootBuffer.processDirtyChunks();
                        if (i == 0) {
                            IsoRegionWorker.isRequestingBufferSwap.set(true);
                            while (IsoRegionWorker.isRequestingBufferSwap.get()) {
                                Thread.sleep(5L);
                            }
                            break;
                        }
                        final JobApplyChanges jobApplyChanges = (JobApplyChanges)regionJob;
                        if (!GameClient.bClient && jobApplyChanges.isSaveToDisk()) {
                            for (int k = this.jobBatchedProcessing.size() - 1; k >= 0; --k) {
                                final RegionJob regionJob2 = this.jobBatchedProcessing.get(k);
                                if (regionJob2.getJobType() == RegionJobType.ChunkUpdate || regionJob2.getJobType() == RegionJobType.SquareUpdate) {
                                    JobChunkUpdate allocChunkUpdate;
                                    if (regionJob2.getJobType() == RegionJobType.SquareUpdate) {
                                        final JobSquareUpdate jobSquareUpdate2 = (JobSquareUpdate)regionJob2;
                                        this.rootBuffer.select.reset(jobSquareUpdate2.getWorldSquareX(), jobSquareUpdate2.getWorldSquareY(), jobSquareUpdate2.getWorldSquareZ(), true, false);
                                        allocChunkUpdate = RegionJobManager.allocChunkUpdate();
                                        allocChunkUpdate.addChunkFromDataChunk(this.rootBuffer.select.chunk);
                                    }
                                    else {
                                        this.jobBatchedProcessing.remove(k);
                                        allocChunkUpdate = (JobChunkUpdate)regionJob2;
                                    }
                                    allocChunkUpdate.saveChunksToDisk();
                                    if (GameServer.bServer) {
                                        this.jobOutgoingQueue.add(allocChunkUpdate);
                                    }
                                }
                            }
                            if (this.threadDiscoveredChunks.size() > 0 && this.threadDiscoveredChunks.size() > this.lastThreadDiscoveredChunksSize && !Core.getInstance().isNoSave()) {
                                IsoRegions.log("IsoRegion: Apply changes -> Saving header file to disk.");
                                final File headerFile = IsoRegions.getHeaderFile();
                                try {
                                    final DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(headerFile));
                                    dataOutputStream.writeInt(186);
                                    dataOutputStream.writeInt(this.threadDiscoveredChunks.size());
                                    final Iterator<Integer> iterator = this.threadDiscoveredChunks.iterator();
                                    while (iterator.hasNext()) {
                                        dataOutputStream.writeInt(iterator.next());
                                    }
                                    dataOutputStream.flush();
                                    dataOutputStream.close();
                                    this.lastThreadDiscoveredChunksSize = this.threadDiscoveredChunks.size();
                                }
                                catch (Exception ex) {
                                    DebugLog.log(ex.getMessage());
                                    ex.printStackTrace();
                                }
                            }
                        }
                        this.finishedJobQueue.addAll(this.jobBatchedProcessing);
                        break;
                    }
                }
            }
        }
    }
    
    protected DataRoot getRootBuffer() {
        return this.rootBuffer;
    }
    
    protected void setRootBuffer(final DataRoot rootBuffer) {
        this.rootBuffer = rootBuffer;
    }
    
    protected void load() {
        IsoRegions.log("IsoRegion: Load save map.");
        if (!GameClient.bClient) {
            this.loadSaveMap();
        }
        else {
            GameClient.sendIsoRegionDataRequest();
        }
    }
    
    protected void update() {
        for (RegionJob regionJob = this.finishedJobQueue.poll(); regionJob != null; regionJob = this.finishedJobQueue.poll()) {
            RegionJobManager.release(regionJob);
        }
        for (JobChunkUpdate jobChunkUpdate = this.jobOutgoingQueue.poll(); jobChunkUpdate != null; jobChunkUpdate = this.jobOutgoingQueue.poll()) {
            if (GameServer.bServer) {
                IsoRegions.log("IsoRegion: sending changed datachunks packet.");
                try {
                    for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                        final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
                        if (jobChunkUpdate.getTargetConn() == null || jobChunkUpdate.getTargetConn() == udpConnection) {
                            final ByteBufferWriter startPacket = udpConnection.startPacket();
                            PacketTypes.PacketType.IsoRegionServerPacket.doPacket(startPacket);
                            final ByteBuffer bb = startPacket.bb;
                            bb.putLong(System.nanoTime());
                            jobChunkUpdate.saveChunksToNetBuffer(bb);
                            PacketTypes.PacketType.IsoRegionServerPacket.send(udpConnection);
                        }
                    }
                }
                catch (Exception ex) {
                    DebugLog.log(ex.getMessage());
                    ex.printStackTrace();
                }
            }
            RegionJobManager.release(jobChunkUpdate);
        }
    }
    
    protected void readServerUpdatePacket(final ByteBuffer byteBuffer) {
        if (GameClient.bClient) {
            IsoRegions.log("IsoRegion: Receiving changed datachunk packet from server");
            try {
                final JobChunkUpdate allocChunkUpdate = RegionJobManager.allocChunkUpdate();
                allocChunkUpdate.readChunksFromNetBuffer(byteBuffer, byteBuffer.getLong());
                this.EnqueueJob(allocChunkUpdate);
                this.ApplyChunkChanges();
            }
            catch (Exception ex) {
                DebugLog.log(ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
    
    protected void readClientRequestFullUpdatePacket(final ByteBuffer byteBuffer, final UdpConnection udpConnection) {
        if (GameServer.bServer && udpConnection != null) {
            IsoRegions.log("IsoRegion: Receiving request full data packet from client");
            try {
                this.EnqueueJob(RegionJobManager.allocServerSendFullData(udpConnection));
            }
            catch (Exception ex) {
                DebugLog.log(ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
    
    protected void addDebugResetJob() {
        if (!GameServer.bServer && !GameClient.bClient) {
            this.EnqueueJob(RegionJobManager.allocDebugResetAllData());
        }
    }
    
    protected void addSquareChangedJob(final int n, final int n2, final int n3, final boolean b, final byte b2) {
        final int n4 = n / 10;
        final int n5 = n2 / 10;
        if (this.discoveredChunks.contains(IsoRegions.hash(n4, n5))) {
            IsoRegions.log("Update square only, plus any unprocessed chunks in a 7x7 grid.", Colors.Magenta);
            this.EnqueueJob(RegionJobManager.allocSquareUpdate(n, n2, n3, b2));
            this.readSurroundingChunks(n4, n5, 7, false);
            this.ApplyChunkChanges();
        }
        else {
            if (b) {
                return;
            }
            IsoRegions.log("Adding new chunk, plus any unprocessed chunks in a 7x7 grid.", Colors.Magenta);
            this.readSurroundingChunks(n4, n5, 7, true);
        }
    }
    
    protected void readSurroundingChunks(final int n, final int n2, final int n3, final boolean b) {
        this.readSurroundingChunks(n, n2, n3, b, false);
    }
    
    protected void readSurroundingChunks(final int n, final int n2, final int n3, final boolean b, final boolean b2) {
        int n4 = 1;
        if (n3 > 0 && n3 <= IsoChunkMap.ChunkGridWidth) {
            n4 = n3 / 2;
            if (n4 + n4 >= IsoChunkMap.ChunkGridWidth) {
                --n4;
            }
        }
        final int n5 = n - n4;
        final int n6 = n2 - n4;
        final int n7 = n + n4;
        final int n8 = n2 + n4;
        JobChunkUpdate jobChunkUpdate = RegionJobManager.allocChunkUpdate();
        boolean b3 = false;
        for (int i = n5; i <= n7; ++i) {
            for (int j = n6; j <= n8; ++j) {
                final IsoChunk isoChunk = GameServer.bServer ? ServerMap.instance.getChunk(i, j) : IsoWorld.instance.getCell().getChunk(i, j);
                if (isoChunk != null) {
                    final int hash = IsoRegions.hash(isoChunk.wx, isoChunk.wy);
                    if (b2 || !this.discoveredChunks.contains(hash)) {
                        this.discoveredChunks.add(hash);
                        if (!jobChunkUpdate.canAddChunk()) {
                            this.EnqueueJob(jobChunkUpdate);
                            jobChunkUpdate = RegionJobManager.allocChunkUpdate();
                        }
                        jobChunkUpdate.addChunkFromIsoChunk(isoChunk);
                        b3 = true;
                    }
                }
            }
        }
        if (jobChunkUpdate.getChunkCount() > 0) {
            this.EnqueueJob(jobChunkUpdate);
        }
        else {
            RegionJobManager.release(jobChunkUpdate);
        }
        if (b3 && b) {
            this.ApplyChunkChanges();
        }
    }
    
    private void loadSaveMap() {
        try {
            boolean b = false;
            final ArrayList<Object> list = new ArrayList<Object>();
            final File headerFile = IsoRegions.getHeaderFile();
            if (headerFile.exists()) {
                final DataInputStream dataInputStream = new DataInputStream(new FileInputStream(headerFile));
                b = true;
                dataInputStream.readInt();
                for (int int1 = dataInputStream.readInt(), i = 0; i < int1; ++i) {
                    list.add(dataInputStream.readInt());
                }
                dataInputStream.close();
            }
            final File[] listFiles = IsoRegions.getDirectory().listFiles(new FilenameFilter() {
                @Override
                public boolean accept(final File file, final String s) {
                    return s.startsWith("datachunk_") && s.endsWith(".bin");
                }
            });
            JobChunkUpdate jobChunkUpdate = RegionJobManager.allocChunkUpdate();
            final ByteBuffer byteBuffer = IsoRegionWorker.byteBuffer;
            boolean b2 = false;
            if (listFiles != null) {
                final File[] array = listFiles;
                for (int length = array.length, j = 0; j < length; ++j) {
                    final FileInputStream fileInputStream = new FileInputStream(array[j]);
                    try {
                        byteBuffer.clear();
                        byteBuffer.limit(fileInputStream.read(byteBuffer.array()));
                        byteBuffer.mark();
                        byteBuffer.getInt();
                        byteBuffer.getInt();
                        final int int2 = byteBuffer.getInt();
                        final int int3 = byteBuffer.getInt();
                        byteBuffer.reset();
                        final int hash = IsoRegions.hash(int2, int3);
                        if (!this.discoveredChunks.contains(hash)) {
                            this.discoveredChunks.add(hash);
                        }
                        if (list.contains(hash)) {
                            list.remove(list.indexOf(hash));
                        }
                        else {
                            IsoRegions.warn("IsoRegion: A chunk save has been found that was not in header known chunks list.");
                        }
                        if (!jobChunkUpdate.canAddChunk()) {
                            this.EnqueueJob(jobChunkUpdate);
                            jobChunkUpdate = RegionJobManager.allocChunkUpdate();
                        }
                        jobChunkUpdate.addChunkFromFile(byteBuffer);
                        b2 = true;
                        fileInputStream.close();
                    }
                    catch (Throwable t) {
                        try {
                            fileInputStream.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                        throw t;
                    }
                }
            }
            if (jobChunkUpdate.getChunkCount() > 0) {
                this.EnqueueJob(jobChunkUpdate);
            }
            else {
                RegionJobManager.release(jobChunkUpdate);
            }
            if (b2) {
                this.ApplyChunkChanges(false);
            }
            if (b && list.size() > 0) {
                IsoRegions.warn(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, list.size()));
                throw new IsoRegionException(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, list.size()));
            }
        }
        catch (Exception ex) {
            DebugLog.log(ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    static {
        isRequestingBufferSwap = new AtomicBoolean(false);
        byteBuffer = ByteBuffer.allocate(1024);
    }
}

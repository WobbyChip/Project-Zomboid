// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import java.util.ArrayDeque;
import zombie.erosion.categories.ErosionCategory;
import java.io.ObjectOutputStream;
import zombie.gameStates.GameLoadingState;
import zombie.core.Translator;
import zombie.debug.DebugOptions;
import zombie.core.Core;
import zombie.core.ThreadGroups;
import zombie.network.GameServer;
import zombie.characters.IsoPlayer;
import zombie.vehicles.VehiclesDB2;
import zombie.savefile.PlayerDB;
import zombie.GameWindow;
import java.util.Comparator;
import java.util.List;
import java.util.Collections;
import zombie.SystemDisabler;
import java.io.File;
import java.util.zip.DataFormatException;
import zombie.ChunkMapFilenames;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.network.PacketTypes;
import zombie.network.GameClient;
import java.io.IOException;
import zombie.network.ChunkChecksum;
import java.util.zip.Inflater;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;
import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class WorldStreamer
{
    static final ChunkComparator comp;
    private static final int CRF_CANCEL = 1;
    private static final int CRF_CANCEL_SENT = 2;
    private static final int CRF_DELETE = 4;
    private static final int CRF_TIMEOUT = 8;
    private static final int CRF_RECEIVED = 16;
    private static final int BLOCK_SIZE = 1024;
    public static WorldStreamer instance;
    private final ConcurrentLinkedQueue<IsoChunk> jobQueue;
    private final Stack<IsoChunk> jobList;
    private final ConcurrentLinkedQueue<IsoChunk> chunkRequests0;
    private final ArrayList<IsoChunk> chunkRequests1;
    private final ArrayList<ChunkRequest> pendingRequests;
    private final ArrayList<ChunkRequest> pendingRequests1;
    private final ConcurrentLinkedQueue<ChunkRequest> sentRequests;
    private final CRC32 crc32;
    private final ConcurrentLinkedQueue<ByteBuffer> freeBuffers;
    private final ConcurrentLinkedQueue<ChunkRequest> waitingToSendQ;
    private final ArrayList<ChunkRequest> tempRequests;
    private final Inflater decompressor;
    private final byte[] readBuf;
    private final ConcurrentLinkedQueue<ChunkRequest> waitingToCancelQ;
    public Thread worldStreamer;
    public boolean bFinished;
    private IsoChunk chunkHeadMain;
    private int requestNumber;
    private boolean bCompare;
    private boolean NetworkFileDebug;
    private ByteBuffer inMemoryZip;
    private boolean requestingLargeArea;
    private volatile int largeAreaDownloads;
    private ByteBuffer bb1;
    private ByteBuffer bb2;
    
    public WorldStreamer() {
        this.jobQueue = new ConcurrentLinkedQueue<IsoChunk>();
        this.jobList = new Stack<IsoChunk>();
        this.chunkRequests0 = new ConcurrentLinkedQueue<IsoChunk>();
        this.chunkRequests1 = new ArrayList<IsoChunk>();
        this.pendingRequests = new ArrayList<ChunkRequest>();
        this.pendingRequests1 = new ArrayList<ChunkRequest>();
        this.sentRequests = new ConcurrentLinkedQueue<ChunkRequest>();
        this.crc32 = new CRC32();
        this.freeBuffers = new ConcurrentLinkedQueue<ByteBuffer>();
        this.waitingToSendQ = new ConcurrentLinkedQueue<ChunkRequest>();
        this.tempRequests = new ArrayList<ChunkRequest>();
        this.decompressor = new Inflater();
        this.readBuf = new byte[1024];
        this.waitingToCancelQ = new ConcurrentLinkedQueue<ChunkRequest>();
        this.bFinished = false;
        this.bCompare = false;
        this.requestingLargeArea = false;
        this.bb1 = ByteBuffer.allocate(5120);
        this.bb2 = ByteBuffer.allocate(5120);
    }
    
    private int bufferSize(final int n) {
        return (n + 1024 - 1) / 1024 * 1024;
    }
    
    private ByteBuffer ensureCapacity(final ByteBuffer byteBuffer, final int n) {
        if (byteBuffer == null) {
            return ByteBuffer.allocate(this.bufferSize(n));
        }
        if (byteBuffer.capacity() < n) {
            return ByteBuffer.allocate(this.bufferSize(n)).put(byteBuffer.array(), 0, byteBuffer.position());
        }
        return byteBuffer;
    }
    
    private ByteBuffer getByteBuffer(final int n) {
        final ByteBuffer byteBuffer = this.freeBuffers.poll();
        if (byteBuffer == null) {
            return ByteBuffer.allocate(this.bufferSize(n));
        }
        byteBuffer.clear();
        return this.ensureCapacity(byteBuffer, n);
    }
    
    private void releaseBuffer(final ByteBuffer e) {
        this.freeBuffers.add(e);
    }
    
    private void sendRequests() throws IOException {
        if (this.chunkRequests1.isEmpty()) {
            return;
        }
        if (this.requestingLargeArea && this.pendingRequests1.size() > 20) {
            return;
        }
        final long currentTimeMillis = System.currentTimeMillis();
        ChunkRequest e = null;
        ChunkRequest chunkRequest = null;
        for (int i = this.chunkRequests1.size() - 1; i >= 0; --i) {
            final IsoChunk chunk = this.chunkRequests1.get(i);
            final ChunkRequest alloc = ChunkRequest.alloc();
            alloc.chunk = chunk;
            alloc.requestNumber = this.requestNumber++;
            alloc.time = currentTimeMillis;
            alloc.crc = ChunkChecksum.getChecksum(chunk.wx, chunk.wy);
            if (e == null) {
                e = alloc;
            }
            else {
                chunkRequest.next = alloc;
            }
            alloc.next = null;
            chunkRequest = alloc;
            this.pendingRequests1.add(alloc);
            this.chunkRequests1.remove(i);
            if (this.requestingLargeArea && this.pendingRequests1.size() >= 40) {
                break;
            }
        }
        this.waitingToSendQ.add(e);
    }
    
    public void updateMain() {
        final UdpConnection connection = GameClient.connection;
        if (this.chunkHeadMain != null) {
            this.chunkRequests0.add(this.chunkHeadMain);
            this.chunkHeadMain = null;
        }
        this.tempRequests.clear();
        for (ChunkRequest e = this.waitingToSendQ.poll(); e != null; e = this.waitingToSendQ.poll()) {
            while (e != null) {
                final ChunkRequest next = e.next;
                if ((e.flagsWS & 0x1) != 0x0) {
                    final ChunkRequest chunkRequest = e;
                    chunkRequest.flagsUDP |= 0x10;
                }
                else {
                    this.tempRequests.add(e);
                }
                e = next;
            }
        }
        if (!this.tempRequests.isEmpty()) {
            final ByteBufferWriter startPacket = connection.startPacket();
            PacketTypes.PacketType.RequestZipList.doPacket(startPacket);
            startPacket.putInt(this.tempRequests.size());
            for (int i = 0; i < this.tempRequests.size(); ++i) {
                final ChunkRequest chunkRequest2 = this.tempRequests.get(i);
                startPacket.putInt(chunkRequest2.requestNumber);
                startPacket.putInt(chunkRequest2.chunk.wx);
                startPacket.putInt(chunkRequest2.chunk.wy);
                startPacket.putLong(chunkRequest2.crc);
                if (this.NetworkFileDebug) {
                    DebugLog.log(DebugType.NetworkFileDebug, invokedynamic(makeConcatWithConstants:(IIJ)Ljava/lang/String;, chunkRequest2.chunk.wx, chunkRequest2.chunk.wy, chunkRequest2.crc));
                }
            }
            PacketTypes.PacketType.RequestZipList.send(connection);
            for (int j = 0; j < this.tempRequests.size(); ++j) {
                this.sentRequests.add(this.tempRequests.get(j));
            }
        }
        this.tempRequests.clear();
        for (ChunkRequest e2 = this.waitingToCancelQ.poll(); e2 != null; e2 = this.waitingToCancelQ.poll()) {
            this.tempRequests.add(e2);
        }
        if (!this.tempRequests.isEmpty()) {
            final ByteBufferWriter startPacket2 = connection.startPacket();
            PacketTypes.PacketType.NotRequiredInZip.doPacket(startPacket2);
            try {
                startPacket2.putInt(this.tempRequests.size());
                for (int k = 0; k < this.tempRequests.size(); ++k) {
                    final ChunkRequest chunkRequest3 = this.tempRequests.get(k);
                    if (this.NetworkFileDebug) {
                        DebugLog.log(DebugType.NetworkFileDebug, invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, chunkRequest3.chunk.wx, chunkRequest3.chunk.wy));
                    }
                    startPacket2.putInt(chunkRequest3.requestNumber);
                    final ChunkRequest chunkRequest4 = chunkRequest3;
                    chunkRequest4.flagsMain |= 0x2;
                }
                PacketTypes.PacketType.NotRequiredInZip.send(connection);
            }
            catch (Exception ex) {
                ex.printStackTrace();
                connection.cancelPacket();
            }
        }
    }
    
    private void loadReceivedChunks() throws DataFormatException, IOException {
        final boolean b = false;
        int n = 0;
        int n2 = 0;
        for (int i = 0; i < this.pendingRequests1.size(); ++i) {
            final ChunkRequest chunkRequest = this.pendingRequests1.get(i);
            if ((chunkRequest.flagsUDP & 0x10) != 0x0) {
                if (b) {
                    ++n;
                    if ((chunkRequest.flagsWS & 0x1) != 0x0) {
                        ++n2;
                    }
                }
                if ((chunkRequest.flagsWS & 0x1) == 0x0 || (chunkRequest.flagsMain & 0x2) != 0x0) {
                    this.pendingRequests1.remove(i--);
                    ChunkSaveWorker.instance.Update(chunkRequest.chunk);
                    if ((chunkRequest.flagsUDP & 0x4) != 0x0) {
                        final File filename = ChunkMapFilenames.instance.getFilename(chunkRequest.chunk.wx, chunkRequest.chunk.wy);
                        if (filename.exists()) {
                            if (this.NetworkFileDebug) {
                                DebugLog.log(DebugType.NetworkFileDebug, invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, chunkRequest.chunk.wx, chunkRequest.chunk.wy));
                            }
                            filename.delete();
                            ChunkChecksum.setChecksum(chunkRequest.chunk.wx, chunkRequest.chunk.wy, 0L);
                        }
                    }
                    ByteBuffer decompress = ((chunkRequest.flagsWS & 0x1) != 0x0) ? null : chunkRequest.bb;
                    if (decompress != null) {
                        try {
                            decompress = this.decompress(decompress);
                        }
                        catch (DataFormatException ex) {
                            DebugLog.General.error(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, chunkRequest.chunk.wx, chunkRequest.chunk.wy));
                            this.chunkRequests1.add(chunkRequest.chunk);
                            continue;
                        }
                        if (this.bCompare) {
                            final File filename2 = ChunkMapFilenames.instance.getFilename(chunkRequest.chunk.wx, chunkRequest.chunk.wy);
                            if (filename2.exists()) {
                                this.compare(chunkRequest, decompress, filename2);
                            }
                        }
                    }
                    if ((chunkRequest.flagsWS & 0x8) == 0x0) {
                        if ((chunkRequest.flagsWS & 0x1) != 0x0 || chunkRequest.chunk.refs.isEmpty()) {
                            if (this.NetworkFileDebug) {
                                DebugLog.log(DebugType.NetworkFileDebug, invokedynamic(makeConcatWithConstants:(IIZ)Ljava/lang/String;, chunkRequest.chunk.wx, chunkRequest.chunk.wy, decompress != null));
                            }
                            if (decompress != null) {
                                final long checksumIfExists = ChunkChecksum.getChecksumIfExists(chunkRequest.chunk.wx, chunkRequest.chunk.wy);
                                this.crc32.reset();
                                this.crc32.update(decompress.array(), 0, decompress.position());
                                if (checksumIfExists != this.crc32.getValue()) {
                                    ChunkChecksum.setChecksum(chunkRequest.chunk.wx, chunkRequest.chunk.wy, this.crc32.getValue());
                                    IsoChunk.SafeWrite("map_", chunkRequest.chunk.wx, chunkRequest.chunk.wy, decompress);
                                }
                            }
                            chunkRequest.chunk.resetForStore();
                            assert !IsoChunkMap.chunkStore.contains(chunkRequest.chunk);
                            IsoChunkMap.chunkStore.add(chunkRequest.chunk);
                        }
                        else {
                            if (decompress != null) {
                                decompress.position(0);
                            }
                            this.DoChunk(chunkRequest.chunk, decompress);
                        }
                    }
                    if (chunkRequest.bb != null) {
                        this.releaseBuffer(chunkRequest.bb);
                    }
                    ChunkRequest.release(chunkRequest);
                }
            }
        }
        if (b && (n != 0 || n2 != 0 || !this.pendingRequests1.isEmpty())) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, n, n2, this.pendingRequests1.size()));
        }
    }
    
    private ByteBuffer decompress(final ByteBuffer byteBuffer) throws DataFormatException {
        this.decompressor.reset();
        this.decompressor.setInput(byteBuffer.array(), 0, byteBuffer.position());
        int n = 0;
        if (this.inMemoryZip != null) {
            this.inMemoryZip.clear();
        }
        while (!this.decompressor.finished()) {
            final int inflate = this.decompressor.inflate(this.readBuf);
            if (inflate != 0) {
                (this.inMemoryZip = this.ensureCapacity(this.inMemoryZip, n + inflate)).put(this.readBuf, 0, inflate);
                n += inflate;
            }
            else {
                if (!this.decompressor.finished()) {
                    throw new DataFormatException();
                }
                continue;
            }
        }
        this.inMemoryZip.limit(this.inMemoryZip.position());
        return this.inMemoryZip;
    }
    
    private void threadLoop() throws DataFormatException, InterruptedException, IOException {
        if (GameClient.bClient && !SystemDisabler.doWorldSyncEnable) {
            this.NetworkFileDebug = DebugType.Do(DebugType.NetworkFileDebug);
            for (IsoChunk e = this.chunkRequests0.poll(); e != null; e = this.chunkRequests0.poll()) {
                while (e != null) {
                    final IsoChunk next = e.next;
                    this.chunkRequests1.add(e);
                    e = next;
                }
            }
            if (!this.chunkRequests1.isEmpty()) {
                WorldStreamer.comp.init();
                Collections.sort(this.chunkRequests1, WorldStreamer.comp);
                this.sendRequests();
            }
            this.loadReceivedChunks();
            this.cancelOutOfBoundsRequests();
            this.resendTimedOutRequests();
        }
        for (IsoChunk isoChunk = this.jobQueue.poll(); isoChunk != null; isoChunk = this.jobQueue.poll()) {
            if (this.jobList.contains(isoChunk)) {
                DebugLog.log("Ignoring duplicate chunk added to WorldStreamer.jobList");
            }
            else {
                this.jobList.add(isoChunk);
            }
        }
        if (!this.jobList.isEmpty()) {
            for (int i = this.jobList.size() - 1; i >= 0; --i) {
                final IsoChunk isoChunk2 = this.jobList.get(i);
                if (isoChunk2.refs.isEmpty()) {
                    this.jobList.remove(i);
                    isoChunk2.resetForStore();
                    assert !IsoChunkMap.chunkStore.contains(isoChunk2);
                    IsoChunkMap.chunkStore.add(isoChunk2);
                }
            }
            final boolean b = !this.jobList.isEmpty();
            IsoChunk isoChunk3 = null;
            if (b) {
                WorldStreamer.comp.init();
                Collections.sort(this.jobList, WorldStreamer.comp);
                isoChunk3 = this.jobList.remove(this.jobList.size() - 1);
            }
            ChunkSaveWorker.instance.Update(isoChunk3);
            if (isoChunk3 != null) {
                if (isoChunk3.refs.isEmpty()) {
                    isoChunk3.resetForStore();
                    assert !IsoChunkMap.chunkStore.contains(isoChunk3);
                    IsoChunkMap.chunkStore.add(isoChunk3);
                }
                else {
                    this.DoChunk(isoChunk3, null);
                }
            }
            if (b || ChunkSaveWorker.instance.bSaving) {
                return;
            }
        }
        else {
            ChunkSaveWorker.instance.Update(null);
            if (ChunkSaveWorker.instance.bSaving) {
                return;
            }
            if (!this.pendingRequests1.isEmpty()) {
                Thread.sleep(20L);
                return;
            }
            Thread.sleep(140L);
        }
        if (!GameClient.bClient && !GameWindow.bLoadedAsClient && PlayerDB.isAvailable()) {
            PlayerDB.getInstance().updateWorldStreamer();
        }
        VehiclesDB2.instance.updateWorldStreamer();
        if (IsoPlayer.getInstance() != null) {
            Thread.sleep(140L);
        }
        else {
            Thread.sleep(0L);
        }
    }
    
    public void create() {
        if (this.worldStreamer != null) {
            return;
        }
        if (GameServer.bServer) {
            return;
        }
        this.bFinished = false;
        (this.worldStreamer = new Thread(ThreadGroups.Workers, () -> {
            while (!this.bFinished) {
                try {
                    this.threadLoop();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return;
        })).setPriority(5);
        this.worldStreamer.setDaemon(true);
        this.worldStreamer.setName("World Streamer");
        this.worldStreamer.setUncaughtExceptionHandler(GameWindow::uncaughtException);
        this.worldStreamer.start();
    }
    
    public void addJob(final IsoChunk isoChunk, final int wx, final int wy, final boolean b) {
        if (GameServer.bServer) {
            return;
        }
        isoChunk.wx = wx;
        isoChunk.wy = wy;
        if (GameClient.bClient && !SystemDisabler.doWorldSyncEnable && b) {
            isoChunk.next = this.chunkHeadMain;
            this.chunkHeadMain = isoChunk;
            return;
        }
        assert !this.jobQueue.contains(isoChunk);
        assert !this.jobList.contains(isoChunk);
        this.jobQueue.add(isoChunk);
    }
    
    public void DoChunk(final IsoChunk isoChunk, final ByteBuffer byteBuffer) {
        if (GameServer.bServer) {
            return;
        }
        this.DoChunkAlways(isoChunk, byteBuffer);
    }
    
    public void DoChunkAlways(final IsoChunk e, final ByteBuffer byteBuffer) {
        if (Core.bDebug && DebugOptions.instance.WorldStreamerSlowLoad.getValue()) {
            try {
                Thread.sleep(50L);
            }
            catch (InterruptedException ex3) {}
        }
        if (e == null) {
            return;
        }
        try {
            if (!e.LoadOrCreate(e.wx, e.wy, byteBuffer)) {
                if (GameClient.bClient) {
                    ChunkChecksum.setChecksum(e.wx, e.wy, 0L);
                }
                e.Blam(e.wx, e.wy);
                if (!e.LoadBrandNew(e.wx, e.wy)) {
                    return;
                }
            }
            if (byteBuffer == null) {
                VehiclesDB2.instance.loadChunk(e);
            }
        }
        catch (Exception ex) {
            DebugLog.General.error(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, e.wx, e.wy));
            ex.printStackTrace();
            if (GameClient.bClient) {
                ChunkChecksum.setChecksum(e.wx, e.wy, 0L);
            }
            e.Blam(e.wx, e.wy);
            if (!e.LoadBrandNew(e.wx, e.wy)) {
                return;
            }
        }
        if (e.jobType != IsoChunk.JobType.Convert && e.jobType != IsoChunk.JobType.SoftReset) {
            try {
                if (!e.refs.isEmpty()) {
                    e.loadInWorldStreamerThread();
                }
            }
            catch (Exception ex2) {
                ex2.printStackTrace();
            }
            IsoChunk.loadGridSquare.add(e);
        }
        else {
            e.doLoadGridsquare();
            e.bLoaded = true;
        }
    }
    
    public void addJobInstant(final IsoChunk isoChunk, final int n, final int n2, final int wx, final int wy) {
        if (GameServer.bServer) {
            return;
        }
        isoChunk.wx = wx;
        isoChunk.wy = wy;
        try {
            this.DoChunkAlways(isoChunk, null);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void addJobConvert(final IsoChunk isoChunk, final int n, final int n2, final int wx, final int wy) {
        if (GameServer.bServer) {
            return;
        }
        isoChunk.wx = wx;
        isoChunk.wy = wy;
        isoChunk.jobType = IsoChunk.JobType.Convert;
        try {
            this.DoChunk(isoChunk, null);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void addJobWipe(final IsoChunk isoChunk, final int n, final int n2, final int wx, final int wy) {
        isoChunk.wx = wx;
        isoChunk.wy = wy;
        isoChunk.jobType = IsoChunk.JobType.SoftReset;
        try {
            this.DoChunkAlways(isoChunk, null);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public boolean isBusy() {
        return (GameClient.bClient && (!this.chunkRequests0.isEmpty() || !this.chunkRequests1.isEmpty() || this.chunkHeadMain != null || !this.waitingToSendQ.isEmpty() || !this.waitingToCancelQ.isEmpty() || !this.sentRequests.isEmpty() || !this.pendingRequests.isEmpty() || !this.pendingRequests1.isEmpty())) || !this.jobQueue.isEmpty() || !this.jobList.isEmpty();
    }
    
    public void stop() {
        DebugLog.log("EXITDEBUG: WorldStreamer.stop 1");
        if (this.worldStreamer == null) {
            return;
        }
        this.bFinished = true;
        DebugLog.log("EXITDEBUG: WorldStreamer.stop 2");
        while (this.worldStreamer.isAlive()) {}
        DebugLog.log("EXITDEBUG: WorldStreamer.stop 3");
        this.worldStreamer = null;
        this.jobList.clear();
        this.jobQueue.clear();
        DebugLog.log("EXITDEBUG: WorldStreamer.stop 4");
        ChunkSaveWorker.instance.SaveNow();
        ChunkChecksum.Reset();
        DebugLog.log("EXITDEBUG: WorldStreamer.stop 5");
    }
    
    public void quit() {
        this.stop();
    }
    
    public void requestLargeAreaZip(final int n, final int n2, final int n3) throws IOException {
        final ByteBufferWriter startPacket = GameClient.connection.startPacket();
        PacketTypes.PacketType.RequestLargeAreaZip.doPacket(startPacket);
        startPacket.putInt(n);
        startPacket.putInt(n2);
        startPacket.putInt(IsoChunkMap.ChunkGridWidth);
        PacketTypes.PacketType.RequestLargeAreaZip.send(GameClient.connection);
        this.requestingLargeArea = true;
        this.largeAreaDownloads = 0;
        GameLoadingState.GameLoadingString = Translator.getText("IGUI_MP_RequestMapData");
        int i = 0;
        final int n4 = n - n3;
        final int n5 = n2 - n3;
        final int n6 = n + n3;
        for (int n7 = n2 + n3, j = n5; j <= n7; ++j) {
            for (int k = n4; k <= n6; ++k) {
                if (IsoWorld.instance.MetaGrid.isValidChunk(k, j)) {
                    IsoChunk isoChunk = IsoChunkMap.chunkStore.poll();
                    if (isoChunk == null) {
                        isoChunk = new IsoChunk(IsoWorld.instance.CurrentCell);
                    }
                    this.addJob(isoChunk, k, j, true);
                    ++i;
                }
            }
        }
        DebugLog.log(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, i));
        long currentTimeMillis = System.currentTimeMillis();
        int n8 = 0;
        int n9 = 0;
        while (this.isBusy()) {
            final long currentTimeMillis2 = System.currentTimeMillis();
            if (currentTimeMillis2 - currentTimeMillis > 60000L) {
                GameLoadingState.mapDownloadFailed = true;
                throw new IOException("map download from server timed out");
            }
            final int largeAreaDownloads = this.largeAreaDownloads;
            GameLoadingState.GameLoadingString = Translator.getText("IGUI_MP_DownloadedMapData", largeAreaDownloads, i);
            final long n10 = currentTimeMillis2 - currentTimeMillis;
            if (n10 / 1000L > n8) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, largeAreaDownloads, i));
                n8 = (int)(n10 / 1000L);
            }
            if (n9 < largeAreaDownloads) {
                currentTimeMillis = currentTimeMillis2;
                n9 = largeAreaDownloads;
            }
            try {
                Thread.sleep(100L);
            }
            catch (InterruptedException ex) {}
        }
        DebugLog.log(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, this.largeAreaDownloads, i));
        this.requestingLargeArea = false;
    }
    
    private void cancelOutOfBoundsRequests() {
        if (this.requestingLargeArea) {
            return;
        }
        for (int i = 0; i < this.pendingRequests1.size(); ++i) {
            final ChunkRequest e = this.pendingRequests1.get(i);
            if ((e.flagsWS & 0x1) == 0x0) {
                if (e.chunk.refs.isEmpty()) {
                    final ChunkRequest chunkRequest = e;
                    chunkRequest.flagsWS |= 0x1;
                    this.waitingToCancelQ.add(e);
                }
            }
        }
    }
    
    private void resendTimedOutRequests() {
        final long currentTimeMillis = System.currentTimeMillis();
        for (int i = 0; i < this.pendingRequests1.size(); ++i) {
            final ChunkRequest chunkRequest = this.pendingRequests1.get(i);
            if ((chunkRequest.flagsWS & 0x1) == 0x0) {
                if (chunkRequest.time + 8000L < currentTimeMillis) {
                    if (this.NetworkFileDebug) {
                        DebugLog.log(DebugType.NetworkFileDebug, invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, chunkRequest.chunk.wx, chunkRequest.chunk.wy));
                    }
                    this.chunkRequests1.add(chunkRequest.chunk);
                    final ChunkRequest chunkRequest2 = chunkRequest;
                    chunkRequest2.flagsWS |= 0x9;
                    final ChunkRequest chunkRequest3 = chunkRequest;
                    chunkRequest3.flagsMain |= 0x2;
                }
            }
        }
    }
    
    public void receiveChunkPart(final ByteBuffer byteBuffer) {
        for (ChunkRequest e = this.sentRequests.poll(); e != null; e = this.sentRequests.poll()) {
            this.pendingRequests.add(e);
        }
        final int int1 = byteBuffer.getInt();
        final int int2 = byteBuffer.getInt();
        final int int3 = byteBuffer.getInt();
        final int int4 = byteBuffer.getInt();
        final int int5 = byteBuffer.getInt();
        final int int6 = byteBuffer.getInt();
        for (int i = 0; i < this.pendingRequests.size(); ++i) {
            final ChunkRequest chunkRequest = this.pendingRequests.get(i);
            if ((chunkRequest.flagsWS & 0x1) != 0x0) {
                this.pendingRequests.remove(i--);
                final ChunkRequest chunkRequest2 = chunkRequest;
                chunkRequest2.flagsUDP |= 0x10;
            }
            else if (chunkRequest.requestNumber == int1) {
                if (chunkRequest.bb == null) {
                    chunkRequest.bb = this.getByteBuffer(int4);
                }
                System.arraycopy(byteBuffer.array(), byteBuffer.position(), chunkRequest.bb.array(), int5, int6);
                if (chunkRequest.partsReceived == null) {
                    chunkRequest.partsReceived = new boolean[int2];
                }
                chunkRequest.partsReceived[int3] = true;
                if (!chunkRequest.isReceived()) {
                    break;
                }
                if (this.NetworkFileDebug) {
                    DebugLog.log(DebugType.NetworkFileDebug, invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, chunkRequest.chunk.wx, chunkRequest.chunk.wy));
                }
                chunkRequest.bb.position(int4);
                this.pendingRequests.remove(i);
                final ChunkRequest chunkRequest3 = chunkRequest;
                chunkRequest3.flagsUDP |= 0x10;
                if (this.requestingLargeArea) {
                    ++this.largeAreaDownloads;
                    break;
                }
                break;
            }
        }
    }
    
    public void receiveNotRequired(final ByteBuffer byteBuffer) {
        for (ChunkRequest e = this.sentRequests.poll(); e != null; e = this.sentRequests.poll()) {
            this.pendingRequests.add(e);
        }
        for (int int1 = byteBuffer.getInt(), i = 0; i < int1; ++i) {
            final int int2 = byteBuffer.getInt();
            final boolean b = byteBuffer.get() == 1;
            for (int j = 0; j < this.pendingRequests.size(); ++j) {
                final ChunkRequest chunkRequest = this.pendingRequests.get(j);
                if ((chunkRequest.flagsWS & 0x1) != 0x0) {
                    this.pendingRequests.remove(j--);
                    final ChunkRequest chunkRequest2 = chunkRequest;
                    chunkRequest2.flagsUDP |= 0x10;
                }
                else if (chunkRequest.requestNumber == int2) {
                    if (this.NetworkFileDebug) {
                        DebugLog.log(DebugType.NetworkFileDebug, invokedynamic(makeConcatWithConstants:(IIZ)Ljava/lang/String;, chunkRequest.chunk.wx, chunkRequest.chunk.wy, !b));
                    }
                    if (!b) {
                        final ChunkRequest chunkRequest3 = chunkRequest;
                        chunkRequest3.flagsUDP |= 0x4;
                    }
                    this.pendingRequests.remove(j);
                    final ChunkRequest chunkRequest4 = chunkRequest;
                    chunkRequest4.flagsUDP |= 0x10;
                    if (this.requestingLargeArea) {
                        ++this.largeAreaDownloads;
                        break;
                    }
                    break;
                }
            }
        }
    }
    
    private void compare(final ChunkRequest chunkRequest, final ByteBuffer byteBuffer, final File file) throws IOException {
        IsoChunk isoChunk = IsoChunkMap.chunkStore.poll();
        if (isoChunk == null) {
            isoChunk = new IsoChunk(IsoWorld.instance.getCell());
        }
        isoChunk.wx = chunkRequest.chunk.wx;
        isoChunk.wy = chunkRequest.chunk.wy;
        IsoChunk isoChunk2 = IsoChunkMap.chunkStore.poll();
        if (isoChunk2 == null) {
            isoChunk2 = new IsoChunk(IsoWorld.instance.getCell());
        }
        isoChunk2.wx = chunkRequest.chunk.wx;
        isoChunk2.wy = chunkRequest.chunk.wy;
        final int position = byteBuffer.position();
        byteBuffer.position(0);
        isoChunk.LoadFromBuffer(chunkRequest.chunk.wx, chunkRequest.chunk.wy, byteBuffer);
        byteBuffer.position(position);
        this.crc32.reset();
        this.crc32.update(byteBuffer.array(), 0, position);
        DebugLog.log(invokedynamic(makeConcatWithConstants:(JJ)Ljava/lang/String;, this.crc32.getValue(), ChunkChecksum.getChecksumIfExists(chunkRequest.chunk.wx, chunkRequest.chunk.wy)));
        isoChunk2.LoadFromDisk();
        DebugLog.log(invokedynamic(makeConcatWithConstants:(IJ)Ljava/lang/String;, position, file.length()));
        this.compareChunks(isoChunk, isoChunk2);
        isoChunk.resetForStore();
        assert !IsoChunkMap.chunkStore.contains(isoChunk);
        IsoChunkMap.chunkStore.add(isoChunk);
        isoChunk2.resetForStore();
        assert !IsoChunkMap.chunkStore.contains(isoChunk2);
        IsoChunkMap.chunkStore.add(isoChunk2);
    }
    
    private void compareChunks(final IsoChunk isoChunk, final IsoChunk isoChunk2) {
        DebugLog.log(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, isoChunk.wx, isoChunk.wy));
        try {
            this.compareErosion(isoChunk, isoChunk2);
            if (isoChunk.lootRespawnHour != isoChunk2.lootRespawnHour) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, isoChunk.lootRespawnHour, isoChunk2.lootRespawnHour));
            }
            for (int i = 0; i < 10; ++i) {
                for (int j = 0; j < 10; ++j) {
                    this.compareSquares(isoChunk.getGridSquare(j, i, 0), isoChunk2.getGridSquare(j, i, 0));
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void compareErosion(final IsoChunk isoChunk, final IsoChunk isoChunk2) {
        if (isoChunk.getErosionData().init != isoChunk2.getErosionData().init) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(ZZ)Ljava/lang/String;, isoChunk.getErosionData().init, isoChunk2.getErosionData().init));
        }
        if (isoChunk.getErosionData().eTickStamp != isoChunk2.getErosionData().eTickStamp) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, isoChunk.getErosionData().eTickStamp, isoChunk2.getErosionData().eTickStamp));
        }
        if (isoChunk.getErosionData().moisture != isoChunk2.getErosionData().moisture) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(FF)Ljava/lang/String;, isoChunk.getErosionData().moisture, isoChunk2.getErosionData().moisture));
        }
        if (isoChunk.getErosionData().minerals != isoChunk2.getErosionData().minerals) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(FF)Ljava/lang/String;, isoChunk.getErosionData().minerals, isoChunk2.getErosionData().minerals));
        }
        if (isoChunk.getErosionData().epoch != isoChunk2.getErosionData().epoch) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, isoChunk.getErosionData().epoch, isoChunk2.getErosionData().epoch));
        }
        if (isoChunk.getErosionData().soil != isoChunk2.getErosionData().soil) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, isoChunk.getErosionData().soil, isoChunk2.getErosionData().soil));
        }
    }
    
    private void compareSquares(final IsoGridSquare isoGridSquare, final IsoGridSquare isoGridSquare2) {
        if (isoGridSquare == null || isoGridSquare2 == null) {
            if (isoGridSquare != null || isoGridSquare2 != null) {
                DebugLog.log("one square is null, the other isn't");
            }
            return;
        }
        try {
            this.bb1.clear();
            isoGridSquare.save(this.bb1, null);
            this.bb1.flip();
            this.bb2.clear();
            isoGridSquare2.save(this.bb2, null);
            this.bb2.flip();
            if (this.bb1.compareTo(this.bb2) != 0) {
                boolean b = true;
                int n = -1;
                if (this.bb1.limit() == this.bb2.limit()) {
                    for (int i = 0; i < this.bb1.limit(); ++i) {
                        if (this.bb1.get(i) != this.bb2.get(i)) {
                            n = i;
                            break;
                        }
                    }
                    for (int j = 0; j < isoGridSquare.getErosionData().regions.size(); ++j) {
                        if (isoGridSquare.getErosionData().regions.get(j).dispSeason != isoGridSquare2.getErosionData().regions.get(j).dispSeason) {
                            DebugLog.log(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, isoGridSquare.getErosionData().regions.get(j).dispSeason, isoGridSquare2.getErosionData().regions.get(j).dispSeason));
                            b = false;
                        }
                    }
                }
                DebugLog.log(invokedynamic(makeConcatWithConstants:(IIIZI)Ljava/lang/String;, isoGridSquare.x, isoGridSquare.y, n, b, isoGridSquare.getErosionData().regions.size()));
                if (isoGridSquare.getObjects().size() == isoGridSquare2.getObjects().size()) {
                    for (int k = 0; k < isoGridSquare.getObjects().size(); ++k) {
                        final IsoObject isoObject = isoGridSquare.getObjects().get(k);
                        final IsoObject isoObject2 = isoGridSquare2.getObjects().get(k);
                        this.bb1.clear();
                        isoObject.save(this.bb1);
                        this.bb1.flip();
                        this.bb2.clear();
                        isoObject2.save(this.bb2);
                        this.bb2.flip();
                        if (this.bb1.compareTo(this.bb2) != 0) {
                            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, isoObject.getClass().getName(), isoObject.getName(), (isoObject.sprite == null) ? "no sprite" : isoObject.sprite.name));
                            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, isoObject2.getClass().getName(), isoObject2.getName(), (isoObject2.sprite == null) ? "no sprite" : isoObject2.sprite.name));
                        }
                    }
                }
                else {
                    for (int l = 0; l < isoGridSquare.getObjects().size(); ++l) {
                        final IsoObject isoObject3 = isoGridSquare.getObjects().get(l);
                        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, isoObject3.getClass().getName(), isoObject3.getName(), (isoObject3.sprite == null) ? "no sprite" : isoObject3.sprite.name));
                    }
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    static {
        comp = new ChunkComparator();
        WorldStreamer.instance = new WorldStreamer();
    }
    
    private static final class ChunkRequest
    {
        static final ArrayDeque<ChunkRequest> pool;
        IsoChunk chunk;
        int requestNumber;
        boolean[] partsReceived;
        long crc;
        ByteBuffer bb;
        transient int flagsMain;
        transient int flagsUDP;
        transient int flagsWS;
        long time;
        ChunkRequest next;
        
        private ChunkRequest() {
            this.partsReceived = null;
        }
        
        boolean isReceived() {
            if (this.partsReceived == null) {
                return false;
            }
            for (int i = 0; i < this.partsReceived.length; ++i) {
                if (!this.partsReceived[i]) {
                    return false;
                }
            }
            return true;
        }
        
        static ChunkRequest alloc() {
            return ChunkRequest.pool.isEmpty() ? new ChunkRequest() : ChunkRequest.pool.pop();
        }
        
        static void release(final ChunkRequest e) {
            e.chunk = null;
            e.partsReceived = null;
            e.bb = null;
            e.flagsMain = 0;
            e.flagsUDP = 0;
            e.flagsWS = 0;
            ChunkRequest.pool.push(e);
        }
        
        static {
            pool = new ArrayDeque<ChunkRequest>();
        }
    }
    
    private static class ChunkComparator implements Comparator<IsoChunk>
    {
        private Vector2[] pos;
        
        public ChunkComparator() {
            this.pos = new Vector2[4];
            for (int i = 0; i < 4; ++i) {
                this.pos[i] = new Vector2();
            }
        }
        
        public void init() {
            for (int i = 0; i < 4; ++i) {
                final Vector2 vector4;
                final Vector2 vector3;
                final Vector2 vector2 = vector3 = (vector4 = this.pos[i]);
                final float n = -1.0f;
                vector3.y = n;
                vector4.x = n;
                final IsoPlayer isoPlayer = IsoPlayer.players[i];
                if (isoPlayer != null) {
                    if (isoPlayer.lx != isoPlayer.x || isoPlayer.ly != isoPlayer.y) {
                        vector2.x = isoPlayer.x - isoPlayer.lx;
                        vector2.y = isoPlayer.y - isoPlayer.ly;
                        vector2.normalize();
                        vector2.setLength(10.0f);
                        final Vector2 vector5 = vector2;
                        vector5.x += isoPlayer.x;
                        final Vector2 vector6 = vector2;
                        vector6.y += isoPlayer.y;
                    }
                    else {
                        vector2.x = isoPlayer.x;
                        vector2.y = isoPlayer.y;
                    }
                }
            }
        }
        
        @Override
        public int compare(final IsoChunk isoChunk, final IsoChunk isoChunk2) {
            float min = Float.MAX_VALUE;
            float min2 = Float.MAX_VALUE;
            for (int i = 0; i < 4; ++i) {
                if (this.pos[i].x != -1.0f || this.pos[i].y != -1.0f) {
                    final float x = this.pos[i].x;
                    final float y = this.pos[i].y;
                    min = Math.min(min, IsoUtils.DistanceTo(x, y, (float)(isoChunk.wx * 10 + 5), (float)(isoChunk.wy * 10 + 5)));
                    min2 = Math.min(min2, IsoUtils.DistanceTo(x, y, (float)(isoChunk2.wx * 10 + 5), (float)(isoChunk2.wy * 10 + 5)));
                }
            }
            if (min < min2) {
                return 1;
            }
            if (min > min2) {
                return -1;
            }
            return 0;
        }
    }
}

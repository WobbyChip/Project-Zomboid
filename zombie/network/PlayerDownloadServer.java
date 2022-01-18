// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network;

import zombie.debug.DebugLog;
import zombie.ChunkMapFilenames;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import zombie.debug.DebugType;
import zombie.iso.IsoChunk;
import zombie.core.logger.LoggerManager;
import org.lwjglx.BufferUtils;
import java.util.ArrayList;
import zombie.core.network.ByteBufferWriter;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;
import zombie.core.raknet.UdpConnection;

public class PlayerDownloadServer
{
    private WorkerThread workerThread;
    public int port;
    private UdpConnection connection;
    private boolean NetworkFileDebug;
    private final CRC32 crc32;
    private ByteBuffer bb;
    private ByteBuffer sb;
    private ByteBufferWriter bbw;
    private final ArrayList<ClientChunkRequest> ccrWaiting;
    
    public PlayerDownloadServer(final UdpConnection connection, final int port) {
        this.crc32 = new CRC32();
        this.bb = ByteBuffer.allocate(1000000);
        this.sb = BufferUtils.createByteBuffer(1000000);
        this.bbw = new ByteBufferWriter(this.bb);
        this.ccrWaiting = new ArrayList<ClientChunkRequest>();
        this.connection = connection;
        this.port = port;
        (this.workerThread = new WorkerThread()).setDaemon(true);
        this.workerThread.setName(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, port));
        this.workerThread.start();
    }
    
    public void destroy() {
        this.workerThread.putCommand(EThreadCommand.Quit, null);
        while (this.workerThread.isAlive()) {
            try {
                Thread.sleep(10L);
            }
            catch (InterruptedException ex) {}
        }
        this.workerThread = null;
    }
    
    public void startConnectionTest() {
    }
    
    public void receiveRequestArray(final ByteBuffer byteBuffer) throws Exception {
        ClientChunkRequest clientChunkRequest = this.workerThread.freeRequests.poll();
        if (clientChunkRequest == null) {
            clientChunkRequest = new ClientChunkRequest();
        }
        clientChunkRequest.largeArea = false;
        this.ccrWaiting.add(clientChunkRequest);
        for (int int1 = byteBuffer.getInt(), i = 0; i < int1; ++i) {
            if (clientChunkRequest.chunks.size() >= 20) {
                clientChunkRequest = this.workerThread.freeRequests.poll();
                if (clientChunkRequest == null) {
                    clientChunkRequest = new ClientChunkRequest();
                }
                clientChunkRequest.largeArea = false;
                this.ccrWaiting.add(clientChunkRequest);
            }
            final ClientChunkRequest.Chunk chunk = clientChunkRequest.getChunk();
            chunk.requestNumber = byteBuffer.getInt();
            chunk.wx = byteBuffer.getInt();
            chunk.wy = byteBuffer.getInt();
            chunk.crc = byteBuffer.getLong();
            clientChunkRequest.chunks.add(chunk);
        }
    }
    
    public void receiveRequestLargeArea(final ByteBuffer byteBuffer) {
        final ClientChunkRequest clientChunkRequest = new ClientChunkRequest();
        clientChunkRequest.unpackLargeArea(byteBuffer, this.connection);
        for (int i = 0; i < clientChunkRequest.chunks.size(); ++i) {
            final ClientChunkRequest.Chunk chunk = clientChunkRequest.chunks.get(i);
            final IsoChunk chunk2 = ServerMap.instance.getChunk(chunk.wx, chunk.wy);
            if (chunk2 != null) {
                clientChunkRequest.getByteBuffer(chunk);
                try {
                    chunk2.SaveLoadedChunk(chunk, this.crc32);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                    LoggerManager.getLogger("map").write(ex);
                    clientChunkRequest.releaseBuffer(chunk);
                }
            }
        }
        this.workerThread.putCommand(EThreadCommand.RequestLargeArea, clientChunkRequest);
    }
    
    public void receiveCancelRequest(final ByteBuffer byteBuffer) {
        for (int int1 = byteBuffer.getInt(), i = 0; i < int1; ++i) {
            this.workerThread.cancelQ.add(byteBuffer.getInt());
        }
    }
    
    public void update() {
        this.NetworkFileDebug = DebugType.Do(DebugType.NetworkFileDebug);
        if (!this.workerThread.bReady) {
            return;
        }
        this.removeOlderDuplicateRequests();
        if (this.ccrWaiting.isEmpty()) {
            if (this.workerThread.cancelQ.isEmpty() && !this.workerThread.cancelled.isEmpty()) {
                this.workerThread.cancelled.clear();
            }
            return;
        }
        final ClientChunkRequest e = this.ccrWaiting.remove(0);
        for (int i = 0; i < e.chunks.size(); ++i) {
            final ClientChunkRequest.Chunk chunk = e.chunks.get(i);
            if (this.workerThread.isRequestCancelled(chunk)) {
                e.chunks.remove(i--);
                e.releaseChunk(chunk);
            }
            else {
                final IsoChunk chunk2 = ServerMap.instance.getChunk(chunk.wx, chunk.wy);
                if (chunk2 != null) {
                    try {
                        e.getByteBuffer(chunk);
                        chunk2.SaveLoadedChunk(chunk, this.crc32);
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                        LoggerManager.getLogger("map").write(ex);
                        this.workerThread.sendNotRequired(chunk, false);
                        e.chunks.remove(i--);
                        e.releaseChunk(chunk);
                    }
                }
            }
        }
        if (e.chunks.isEmpty()) {
            this.workerThread.freeRequests.add(e);
            return;
        }
        this.workerThread.bReady = false;
        this.workerThread.putCommand(EThreadCommand.RequestZipArray, e);
    }
    
    private void removeOlderDuplicateRequests() {
        for (int i = this.ccrWaiting.size() - 1; i >= 0; --i) {
            final ClientChunkRequest e = this.ccrWaiting.get(i);
            for (int j = 0; j < e.chunks.size(); ++j) {
                final ClientChunkRequest.Chunk chunk = e.chunks.get(j);
                if (this.workerThread.isRequestCancelled(chunk)) {
                    e.chunks.remove(j--);
                    e.releaseChunk(chunk);
                }
                else {
                    for (int k = i - 1; k >= 0; --k) {
                        if (this.cancelDuplicateChunk(this.ccrWaiting.get(k), chunk.wx, chunk.wy)) {}
                    }
                }
            }
            if (e.chunks.isEmpty()) {
                this.ccrWaiting.remove(i);
                this.workerThread.freeRequests.add(e);
            }
        }
    }
    
    private boolean cancelDuplicateChunk(final ClientChunkRequest clientChunkRequest, final int n, final int n2) {
        for (int i = 0; i < clientChunkRequest.chunks.size(); ++i) {
            final ClientChunkRequest.Chunk chunk = clientChunkRequest.chunks.get(i);
            if (this.workerThread.isRequestCancelled(chunk)) {
                clientChunkRequest.chunks.remove(i--);
                clientChunkRequest.releaseChunk(chunk);
            }
            else if (chunk.wx == n && chunk.wy == n2) {
                this.workerThread.sendNotRequired(chunk, false);
                clientChunkRequest.chunks.remove(i);
                clientChunkRequest.releaseChunk(chunk);
                return true;
            }
        }
        return false;
    }
    
    private void sendPacket(final PacketTypes.PacketType packetType) {
        this.bb.flip();
        this.sb.put(this.bb);
        this.sb.flip();
        this.connection.getPeer().SendRaw(this.sb, packetType.PacketPriority, packetType.PacketReliability, (byte)0, this.connection.getConnectedGUID(), false);
        this.sb.clear();
    }
    
    private ByteBufferWriter startPacket() {
        this.bb.clear();
        return this.bbw;
    }
    
    private enum EThreadCommand
    {
        RequestLargeArea, 
        RequestZipArray, 
        Quit;
        
        private static /* synthetic */ EThreadCommand[] $values() {
            return new EThreadCommand[] { EThreadCommand.RequestLargeArea, EThreadCommand.RequestZipArray, EThreadCommand.Quit };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    private static final class WorkerThreadCommand
    {
        EThreadCommand e;
        ClientChunkRequest ccr;
    }
    
    private final class WorkerThread extends Thread
    {
        boolean bQuit;
        volatile boolean bReady;
        final LinkedBlockingQueue<WorkerThreadCommand> commandQ;
        final ConcurrentLinkedQueue<ClientChunkRequest> freeRequests;
        final ConcurrentLinkedQueue<Integer> cancelQ;
        final ArrayList<Integer> cancelled;
        final CRC32 crcMaker;
        static final int chunkSize = 1000;
        private byte[] inMemoryZip;
        private final Deflater compressor;
        
        private WorkerThread() {
            this.bReady = true;
            this.commandQ = new LinkedBlockingQueue<WorkerThreadCommand>();
            this.freeRequests = new ConcurrentLinkedQueue<ClientChunkRequest>();
            this.cancelQ = new ConcurrentLinkedQueue<Integer>();
            this.cancelled = new ArrayList<Integer>();
            this.crcMaker = new CRC32();
            this.inMemoryZip = new byte[20480];
            this.compressor = new Deflater();
        }
        
        @Override
        public void run() {
            while (!this.bQuit) {
                try {
                    this.runInner();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        
        private void runInner() throws InterruptedException, IOException {
            MPStatistic.getInstance().PlayerDownloadServer.End();
            final WorkerThreadCommand workerThreadCommand = this.commandQ.take();
            MPStatistic.getInstance().PlayerDownloadServer.Start();
            switch (workerThreadCommand.e) {
                case RequestLargeArea: {
                    try {
                        this.sendLargeArea(workerThreadCommand.ccr);
                    }
                    finally {
                        this.bReady = true;
                    }
                    break;
                }
                case RequestZipArray: {
                    try {
                        this.sendArray(workerThreadCommand.ccr);
                    }
                    finally {
                        this.bReady = true;
                    }
                    break;
                }
                case Quit: {
                    this.bQuit = true;
                    break;
                }
            }
        }
        
        void putCommand(final EThreadCommand e, final ClientChunkRequest ccr) {
            final WorkerThreadCommand e2 = new WorkerThreadCommand();
            e2.e = e;
            e2.ccr = ccr;
            while (true) {
                try {
                    this.commandQ.put(e2);
                }
                catch (InterruptedException ex) {
                    continue;
                }
                break;
            }
        }
        
        private int compressChunk(final ClientChunkRequest.Chunk chunk) {
            this.compressor.reset();
            this.compressor.setInput(chunk.bb.array(), 0, chunk.bb.limit());
            this.compressor.finish();
            if (this.inMemoryZip.length < chunk.bb.limit() * 1.5) {
                this.inMemoryZip = new byte[(int)(chunk.bb.limit() * 1.5)];
            }
            return this.compressor.deflate(this.inMemoryZip, 0, this.inMemoryZip.length, 3);
        }
        
        private void sendChunk(final ClientChunkRequest.Chunk chunk) {
            try {
                final long n = this.compressChunk(chunk);
                long n2 = n / 1000L;
                if (n % 1000L != 0L) {
                    ++n2;
                }
                long n3 = 0L;
                for (int n4 = 0; n4 < n2; ++n4) {
                    final long n5 = (n - n3 > 1000L) ? 1000L : (n - n3);
                    final ByteBufferWriter startPacket = PlayerDownloadServer.this.startPacket();
                    PacketTypes.PacketType.SentChunk.doPacket(startPacket);
                    startPacket.putInt(chunk.requestNumber);
                    startPacket.putInt((int)n2);
                    startPacket.putInt(n4);
                    startPacket.putInt((int)n);
                    startPacket.putInt((int)n3);
                    startPacket.putInt((int)n5);
                    startPacket.bb.put(this.inMemoryZip, (int)n3, (int)n5);
                    PlayerDownloadServer.this.sendPacket(PacketTypes.PacketType.SentChunk);
                    n3 += n5;
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
                this.sendNotRequired(chunk, false);
            }
        }
        
        private void sendNotRequired(final ClientChunkRequest.Chunk chunk, final boolean b) {
            final ByteBufferWriter startPacket = PlayerDownloadServer.this.startPacket();
            PacketTypes.PacketType.NotRequiredInZip.doPacket(startPacket);
            startPacket.putInt(1);
            startPacket.putInt(chunk.requestNumber);
            startPacket.putByte((byte)(b ? 1 : 0));
            PlayerDownloadServer.this.sendPacket(PacketTypes.PacketType.NotRequiredInZip);
        }
        
        private void sendLargeArea(final ClientChunkRequest clientChunkRequest) throws IOException {
            for (int i = 0; i < clientChunkRequest.chunks.size(); ++i) {
                final ClientChunkRequest.Chunk chunk = clientChunkRequest.chunks.get(i);
                final int wx = chunk.wx;
                final int wy = chunk.wy;
                if (chunk.bb != null) {
                    chunk.bb.limit(chunk.bb.position());
                    chunk.bb.position(0);
                    this.sendChunk(chunk);
                    clientChunkRequest.releaseBuffer(chunk);
                }
                else if (ChunkMapFilenames.instance.getFilename(wx, wy).exists()) {
                    clientChunkRequest.getByteBuffer(chunk);
                    chunk.bb = IsoChunk.SafeRead("map_", wx, wy, chunk.bb);
                    this.sendChunk(chunk);
                    clientChunkRequest.releaseBuffer(chunk);
                }
            }
            ClientChunkRequest.freeBuffers.clear();
            clientChunkRequest.chunks.clear();
        }
        
        private void sendArray(final ClientChunkRequest e) throws IOException {
            for (int i = 0; i < e.chunks.size(); ++i) {
                final ClientChunkRequest.Chunk chunk = e.chunks.get(i);
                if (!this.isRequestCancelled(chunk)) {
                    final int wx = chunk.wx;
                    final int wy = chunk.wy;
                    final long crc = chunk.crc;
                    if (chunk.bb != null) {
                        boolean b = true;
                        if (chunk.crc != 0L) {
                            this.crcMaker.reset();
                            this.crcMaker.update(chunk.bb.array(), 0, chunk.bb.position());
                            b = (chunk.crc != this.crcMaker.getValue());
                            if (b && PlayerDownloadServer.this.NetworkFileDebug) {
                                DebugLog.log(DebugType.NetworkFileDebug, invokedynamic(makeConcatWithConstants:(IIJJ)Ljava/lang/String;, wx, wy, this.crcMaker.getValue(), chunk.crc));
                            }
                        }
                        if (b) {
                            if (PlayerDownloadServer.this.NetworkFileDebug) {
                                DebugLog.log(DebugType.NetworkFileDebug, invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, wx, wy));
                            }
                            chunk.bb.limit(chunk.bb.position());
                            chunk.bb.position(0);
                            this.sendChunk(chunk);
                        }
                        else {
                            if (PlayerDownloadServer.this.NetworkFileDebug) {
                                DebugLog.log(DebugType.NetworkFileDebug, invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, wx, wy));
                            }
                            this.sendNotRequired(chunk, true);
                        }
                        e.releaseBuffer(chunk);
                    }
                    else if (ChunkMapFilenames.instance.getFilename(wx, wy).exists()) {
                        final long checksum = ChunkChecksum.getChecksum(wx, wy);
                        if (checksum != 0L && checksum == chunk.crc) {
                            if (PlayerDownloadServer.this.NetworkFileDebug) {
                                DebugLog.log(DebugType.NetworkFileDebug, invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, wx, wy));
                            }
                            this.sendNotRequired(chunk, true);
                        }
                        else {
                            e.getByteBuffer(chunk);
                            chunk.bb = IsoChunk.SafeRead("map_", wx, wy, chunk.bb);
                            int n = 1;
                            if (chunk.crc != 0L) {
                                this.crcMaker.reset();
                                this.crcMaker.update(chunk.bb.array(), 0, chunk.bb.limit());
                                n = ((chunk.crc != this.crcMaker.getValue()) ? 1 : 0);
                            }
                            if (n != 0) {
                                if (PlayerDownloadServer.this.NetworkFileDebug) {
                                    DebugLog.log(DebugType.NetworkFileDebug, invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, wx, wy));
                                }
                                this.sendChunk(chunk);
                            }
                            else {
                                if (PlayerDownloadServer.this.NetworkFileDebug) {
                                    DebugLog.log(DebugType.NetworkFileDebug, invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, wx, wy));
                                }
                                this.sendNotRequired(chunk, true);
                            }
                            e.releaseBuffer(chunk);
                        }
                    }
                    else {
                        if (PlayerDownloadServer.this.NetworkFileDebug) {
                            DebugLog.log(DebugType.NetworkFileDebug, invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, wx, wy));
                        }
                        this.sendNotRequired(chunk, crc == 0L);
                    }
                }
            }
            for (int j = 0; j < e.chunks.size(); ++j) {
                e.releaseChunk(e.chunks.get(j));
            }
            e.chunks.clear();
            this.freeRequests.add(e);
        }
        
        private boolean isRequestCancelled(final ClientChunkRequest.Chunk chunk) {
            for (Integer e = this.cancelQ.poll(); e != null; e = this.cancelQ.poll()) {
                this.cancelled.add(e);
            }
            for (int i = 0; i < this.cancelled.size(); ++i) {
                final Integer n = this.cancelled.get(i);
                if (n == chunk.requestNumber) {
                    if (PlayerDownloadServer.this.NetworkFileDebug) {
                        DebugLog.log(DebugType.NetworkFileDebug, invokedynamic(makeConcatWithConstants:(Ljava/lang/Integer;)Ljava/lang/String;, n));
                    }
                    this.cancelled.remove(i);
                    return true;
                }
            }
            return false;
        }
    }
}

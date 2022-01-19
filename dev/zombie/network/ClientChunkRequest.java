// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network;

import java.util.Collection;
import zombie.core.raknet.UdpConnection;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.ArrayList;

public class ClientChunkRequest
{
    public ArrayList<Chunk> chunks;
    private static final ConcurrentLinkedQueue<Chunk> freeChunks;
    public static final ConcurrentLinkedQueue<ByteBuffer> freeBuffers;
    public boolean largeArea;
    int minX;
    int maxX;
    int minY;
    int maxY;
    
    public ClientChunkRequest() {
        this.chunks = new ArrayList<Chunk>(20);
        this.largeArea = false;
    }
    
    public Chunk getChunk() {
        Chunk chunk = ClientChunkRequest.freeChunks.poll();
        if (chunk == null) {
            chunk = new Chunk();
        }
        return chunk;
    }
    
    public void releaseChunk(final Chunk e) {
        this.releaseBuffer(e);
        ClientChunkRequest.freeChunks.add(e);
    }
    
    public void getByteBuffer(final Chunk chunk) {
        chunk.bb = ClientChunkRequest.freeBuffers.poll();
        if (chunk.bb == null) {
            chunk.bb = ByteBuffer.allocate(16384);
        }
        else {
            chunk.bb.clear();
        }
    }
    
    public void releaseBuffer(final Chunk chunk) {
        if (chunk.bb != null) {
            ClientChunkRequest.freeBuffers.add(chunk.bb);
            chunk.bb = null;
        }
    }
    
    public void releaseBuffers() {
        for (int i = 0; i < this.chunks.size(); ++i) {
            this.chunks.get(i).bb = null;
        }
    }
    
    public void unpack(final ByteBuffer byteBuffer, final UdpConnection udpConnection) {
        for (int i = 0; i < this.chunks.size(); ++i) {
            this.releaseBuffer(this.chunks.get(i));
        }
        ClientChunkRequest.freeChunks.addAll(this.chunks);
        this.chunks.clear();
        for (int int1 = byteBuffer.getInt(), j = 0; j < int1; ++j) {
            final Chunk chunk = this.getChunk();
            chunk.requestNumber = byteBuffer.getInt();
            chunk.wx = byteBuffer.getInt();
            chunk.wy = byteBuffer.getInt();
            chunk.crc = byteBuffer.getLong();
            this.chunks.add(chunk);
        }
        this.largeArea = false;
    }
    
    public void unpackLargeArea(final ByteBuffer byteBuffer, final UdpConnection udpConnection) {
        for (int i = 0; i < this.chunks.size(); ++i) {
            this.releaseBuffer(this.chunks.get(i));
        }
        ClientChunkRequest.freeChunks.addAll(this.chunks);
        this.chunks.clear();
        this.minX = byteBuffer.getInt();
        this.minY = byteBuffer.getInt();
        this.maxX = byteBuffer.getInt();
        this.maxY = byteBuffer.getInt();
        for (int j = this.minX; j < this.maxX; ++j) {
            for (int k = this.minY; k < this.maxY; ++k) {
                final Chunk chunk = this.getChunk();
                chunk.requestNumber = byteBuffer.getInt();
                chunk.wx = j;
                chunk.wy = k;
                chunk.crc = 0L;
                this.releaseBuffer(chunk);
                this.chunks.add(chunk);
            }
        }
        this.largeArea = true;
    }
    
    static {
        freeChunks = new ConcurrentLinkedQueue<Chunk>();
        freeBuffers = new ConcurrentLinkedQueue<ByteBuffer>();
    }
    
    public static final class Chunk
    {
        public int requestNumber;
        public int wx;
        public int wy;
        public long crc;
        public ByteBuffer bb;
    }
}

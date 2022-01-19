// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.areas.isoregion.jobs;

import zombie.iso.areas.isoregion.ChunkUpdate;
import zombie.iso.IsoChunk;
import zombie.iso.areas.isoregion.data.DataChunk;
import java.io.File;
import zombie.debug.DebugLog;
import java.io.FileOutputStream;
import zombie.core.Core;
import zombie.iso.areas.isoregion.IsoRegions;
import zombie.network.GameClient;
import java.util.List;
import zombie.iso.areas.isoregion.data.DataRoot;
import zombie.core.raknet.UdpConnection;
import java.nio.ByteBuffer;

public class JobChunkUpdate extends RegionJob
{
    private ByteBuffer buffer;
    private int chunkCount;
    private int bufferMaxBytes;
    private long netTimeStamp;
    private UdpConnection targetConn;
    
    protected JobChunkUpdate() {
        super(RegionJobType.ChunkUpdate);
        this.buffer = ByteBuffer.allocate(65536);
        this.chunkCount = 0;
        this.bufferMaxBytes = 0;
        this.netTimeStamp = -1L;
    }
    
    @Override
    protected void reset() {
        this.chunkCount = 0;
        this.bufferMaxBytes = 0;
        this.netTimeStamp = -1L;
        this.targetConn = null;
        this.buffer.clear();
    }
    
    public UdpConnection getTargetConn() {
        return this.targetConn;
    }
    
    public void setTargetConn(final UdpConnection targetConn) {
        this.targetConn = targetConn;
    }
    
    public int getChunkCount() {
        return this.chunkCount;
    }
    
    public ByteBuffer getBuffer() {
        return this.buffer;
    }
    
    public long getNetTimeStamp() {
        return this.netTimeStamp;
    }
    
    public void setNetTimeStamp(final long netTimeStamp) {
        this.netTimeStamp = netTimeStamp;
    }
    
    public boolean readChunksPacket(final DataRoot dataRoot, final List<Integer> list) {
        this.buffer.position(0);
        this.buffer.getInt();
        for (int int1 = this.buffer.getInt(), i = 0; i < int1; ++i) {
            this.buffer.getInt();
            final int int2 = this.buffer.getInt();
            final int int3 = this.buffer.getInt();
            final int int4 = this.buffer.getInt();
            dataRoot.select.reset(int3 * 10, int4 * 10, 0, true, false);
            if (GameClient.bClient) {
                if (this.netTimeStamp != -1L && this.netTimeStamp < dataRoot.select.chunk.getLastUpdateStamp()) {
                    this.buffer.position(this.buffer.position() + this.buffer.getInt());
                    continue;
                }
                dataRoot.select.chunk.setLastUpdateStamp(this.netTimeStamp);
            }
            else {
                final int hash = IsoRegions.hash(int3, int4);
                if (!list.contains(hash)) {
                    list.add(hash);
                }
            }
            dataRoot.select.chunk.load(this.buffer, int2, true);
            dataRoot.select.chunk.setDirtyAllActive();
        }
        return true;
    }
    
    public boolean saveChunksToDisk() {
        if (Core.getInstance().isNoSave()) {
            return true;
        }
        if (this.chunkCount > 0) {
            this.buffer.position(0);
            this.buffer.getInt();
            for (int int1 = this.buffer.getInt(), i = 0; i < int1; ++i) {
                this.buffer.mark();
                final int int2 = this.buffer.getInt();
                this.buffer.getInt();
                final int int3 = this.buffer.getInt();
                final int int4 = this.buffer.getInt();
                this.buffer.reset();
                final File chunkFile = IsoRegions.getChunkFile(int3, int4);
                try {
                    final FileOutputStream fileOutputStream = new FileOutputStream(chunkFile);
                    fileOutputStream.getChannel().truncate(0L);
                    fileOutputStream.write(this.buffer.array(), this.buffer.position(), int2);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
                catch (Exception ex) {
                    DebugLog.log(ex.getMessage());
                    ex.printStackTrace();
                }
                this.buffer.position(this.buffer.position() + int2);
            }
            return true;
        }
        return false;
    }
    
    public boolean saveChunksToNetBuffer(final ByteBuffer byteBuffer) {
        IsoRegions.log(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, this.bufferMaxBytes, this.chunkCount));
        byteBuffer.put(this.buffer.array(), 0, this.bufferMaxBytes);
        return true;
    }
    
    public boolean readChunksFromNetBuffer(final ByteBuffer byteBuffer, final long netTimeStamp) {
        this.netTimeStamp = netTimeStamp;
        byteBuffer.mark();
        this.bufferMaxBytes = byteBuffer.getInt();
        this.chunkCount = byteBuffer.getInt();
        byteBuffer.reset();
        IsoRegions.log(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, this.bufferMaxBytes, this.chunkCount));
        this.buffer.position(0);
        this.buffer.put(byteBuffer.array(), byteBuffer.position(), this.bufferMaxBytes);
        return true;
    }
    
    public boolean canAddChunk() {
        return this.buffer.position() + 1024 < this.buffer.capacity();
    }
    
    private int startBufferBlock() {
        if (this.chunkCount == 0) {
            this.buffer.position(0);
            this.buffer.putInt(0);
            this.buffer.putInt(0);
        }
        final int position = this.buffer.position();
        this.buffer.putInt(0);
        return position;
    }
    
    private void endBufferBlock(final int newPosition) {
        this.bufferMaxBytes = this.buffer.position();
        this.buffer.position(newPosition);
        this.buffer.putInt(this.bufferMaxBytes - newPosition);
        ++this.chunkCount;
        this.buffer.position(0);
        this.buffer.putInt(this.bufferMaxBytes);
        this.buffer.putInt(this.chunkCount);
        this.buffer.position(this.bufferMaxBytes);
    }
    
    public boolean addChunkFromDataChunk(final DataChunk dataChunk) {
        if (this.buffer.position() + 1024 >= this.buffer.capacity()) {
            return false;
        }
        final int startBufferBlock = this.startBufferBlock();
        this.buffer.putInt(186);
        this.buffer.putInt(dataChunk.getChunkX());
        this.buffer.putInt(dataChunk.getChunkY());
        dataChunk.save(this.buffer);
        this.endBufferBlock(startBufferBlock);
        return true;
    }
    
    public boolean addChunkFromIsoChunk(final IsoChunk isoChunk) {
        if (this.buffer.position() + 1024 >= this.buffer.capacity()) {
            return false;
        }
        final int startBufferBlock = this.startBufferBlock();
        this.buffer.putInt(186);
        this.buffer.putInt(isoChunk.wx);
        this.buffer.putInt(isoChunk.wy);
        ChunkUpdate.writeIsoChunkIntoBuffer(isoChunk, this.buffer);
        this.endBufferBlock(startBufferBlock);
        return true;
    }
    
    public boolean addChunkFromFile(final ByteBuffer byteBuffer) {
        if (this.buffer.position() + byteBuffer.limit() >= this.buffer.capacity()) {
            return false;
        }
        byteBuffer.getInt();
        final int startBufferBlock = this.startBufferBlock();
        this.buffer.putInt(byteBuffer.getInt());
        this.buffer.putInt(byteBuffer.getInt());
        this.buffer.putInt(byteBuffer.getInt());
        byteBuffer.mark();
        final int int1 = byteBuffer.getInt();
        byteBuffer.reset();
        this.buffer.put(byteBuffer.array(), byteBuffer.position(), int1);
        this.endBufferBlock(startBufferBlock);
        return true;
    }
}

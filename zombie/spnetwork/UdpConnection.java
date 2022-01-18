// 
// Decompiled by Procyon v0.5.36
// 

package zombie.spnetwork;

import java.util.concurrent.locks.ReentrantLock;
import zombie.characters.IsoPlayer;
import zombie.core.network.ByteBufferWriter;
import java.nio.ByteBuffer;
import java.util.concurrent.locks.Lock;

public final class UdpConnection
{
    final UdpEngine engine;
    private final Lock bufferLock;
    private final ByteBuffer bb;
    private final ByteBufferWriter bbw;
    public final IsoPlayer[] players;
    
    public UdpConnection(final UdpEngine engine) {
        this.bufferLock = new ReentrantLock();
        this.bb = ByteBuffer.allocate(1000000);
        this.bbw = new ByteBufferWriter(this.bb);
        this.players = IsoPlayer.players;
        this.engine = engine;
    }
    
    public boolean ReleventTo(final float n, final float n2) {
        return true;
    }
    
    public ByteBufferWriter startPacket() {
        this.bufferLock.lock();
        this.bb.clear();
        return this.bbw;
    }
    
    public void endPacketImmediate() {
        this.bb.flip();
        this.engine.Send(this.bb);
        this.bufferLock.unlock();
    }
    
    public void cancelPacket() {
        this.bufferLock.unlock();
    }
}

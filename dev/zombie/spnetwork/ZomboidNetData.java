// 
// Decompiled by Procyon v0.5.36
// 

package zombie.spnetwork;

import java.nio.ByteBuffer;
import zombie.network.IZomboidPacket;

public final class ZomboidNetData implements IZomboidPacket
{
    public short type;
    public short length;
    public ByteBuffer buffer;
    public UdpConnection connection;
    
    public ZomboidNetData() {
        this.buffer = ByteBuffer.allocate(2048);
    }
    
    public ZomboidNetData(final int capacity) {
        this.buffer = ByteBuffer.allocate(capacity);
    }
    
    public void reset() {
        this.type = 0;
        this.length = 0;
        this.buffer.clear();
        this.connection = null;
    }
    
    public void read(final short type, final ByteBuffer src, final UdpConnection connection) {
        this.type = type;
        this.connection = connection;
        this.buffer.put(src);
        this.buffer.flip();
    }
    
    @Override
    public boolean isConnect() {
        return false;
    }
    
    @Override
    public boolean isDisconnect() {
        return false;
    }
}

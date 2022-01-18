// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network;

import zombie.core.raknet.UdpConnection;
import java.nio.ByteBuffer;

public class ZomboidNetData implements IZomboidPacket
{
    public short type;
    public short length;
    public ByteBuffer buffer;
    public long connection;
    public long time;
    
    public ZomboidNetData() {
        this.buffer = ByteBuffer.allocate(2048);
    }
    
    public ZomboidNetData(final int capacity) {
        this.buffer = ByteBuffer.allocate(capacity);
    }
    
    public void reset() {
        this.type = 0;
        this.length = 0;
        this.connection = 0L;
        this.buffer.clear();
    }
    
    public void read(final short type, final ByteBuffer src, final UdpConnection udpConnection) {
        this.type = type;
        this.connection = udpConnection.getConnectedGUID();
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

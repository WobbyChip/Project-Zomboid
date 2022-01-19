// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network.packets;

import zombie.core.network.ByteBufferWriter;
import java.nio.ByteBuffer;

public interface INetworkPacket
{
    void parse(final ByteBuffer p0);
    
    void write(final ByteBufferWriter p0);
    
    default int getPacketSizeBytes() {
        return 0;
    }
    
    default boolean isConsistent() {
        return true;
    }
    
    default String getDescription() {
        return this.getClass().getSimpleName();
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network.packets.hit;

import zombie.core.network.ByteBufferWriter;
import java.nio.ByteBuffer;
import zombie.network.packets.INetworkPacket;

public abstract class Instance implements INetworkPacket
{
    protected short ID;
    
    public void set(final short id) {
        this.ID = id;
    }
    
    @Override
    public void parse(final ByteBuffer byteBuffer) {
        this.ID = byteBuffer.getShort();
    }
    
    @Override
    public void write(final ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putShort(this.ID);
    }
    
    @Override
    public boolean isConsistent() {
        return this.ID != -1;
    }
    
    @Override
    public String getDescription() {
        return invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, this.ID);
    }
}

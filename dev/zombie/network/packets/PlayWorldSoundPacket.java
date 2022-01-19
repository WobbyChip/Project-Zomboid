// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network.packets;

import zombie.core.network.ByteBufferWriter;
import zombie.GameWindow;
import java.nio.ByteBuffer;
import zombie.SoundManager;

public class PlayWorldSoundPacket implements INetworkPacket
{
    String name;
    int x;
    int y;
    byte z;
    
    public void set(final String name, final int x, final int y, final byte z) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public void process() {
        SoundManager.instance.PlayWorldSoundImpl(this.name, false, this.x, this.y, this.z, 1.0f, 20.0f, 2.0f, false);
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
    
    @Override
    public void parse(final ByteBuffer byteBuffer) {
        this.x = byteBuffer.getInt();
        this.y = byteBuffer.getInt();
        this.z = byteBuffer.get();
        this.name = GameWindow.ReadString(byteBuffer);
    }
    
    @Override
    public void write(final ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putInt(this.x);
        byteBufferWriter.putInt(this.y);
        byteBufferWriter.putByte(this.z);
        byteBufferWriter.putUTF(this.name);
    }
    
    @Override
    public boolean isConsistent() {
        return this.name != null && !this.name.isEmpty();
    }
    
    @Override
    public int getPacketSizeBytes() {
        return 12 + this.name.length();
    }
    
    @Override
    public String getDescription() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;IIB)Ljava/lang/String;, this.name, this.x, this.y, this.z);
    }
}

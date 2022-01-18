// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network.packets.hit;

import zombie.core.network.ByteBufferWriter;
import java.nio.ByteBuffer;
import zombie.iso.objects.IsoWindow;
import zombie.iso.IsoObject;
import zombie.iso.IsoMovingObject;
import zombie.network.packets.INetworkPacket;

public class HitInfo implements INetworkPacket
{
    public MovingObject object;
    public NetObject window;
    public float x;
    public float y;
    public float z;
    public float dot;
    public float distSq;
    public int chance;
    
    public HitInfo() {
        this.chance = 0;
        this.object = new MovingObject();
        this.window = new NetObject();
    }
    
    public HitInfo init(final IsoMovingObject movingObject, final float dot, final float distSq, final float x, final float y, final float z) {
        this.object = new MovingObject();
        this.window = new NetObject();
        this.object.setMovingObject(movingObject);
        this.window.setObject(null);
        this.x = x;
        this.y = y;
        this.z = z;
        this.dot = dot;
        this.distSq = distSq;
        return this;
    }
    
    public HitInfo init(final IsoWindow object, final float dot, final float distSq) {
        this.object = new MovingObject();
        this.window = new NetObject();
        this.object.setMovingObject(null);
        this.window.setObject(object);
        this.z = object.getZ();
        this.dot = dot;
        this.distSq = distSq;
        return this;
    }
    
    public IsoMovingObject getObject() {
        return this.object.getMovingObject();
    }
    
    @Override
    public void parse(final ByteBuffer byteBuffer) {
        this.object.parse(byteBuffer);
        this.window.parse(byteBuffer);
        this.x = byteBuffer.getFloat();
        this.y = byteBuffer.getFloat();
        this.z = byteBuffer.getFloat();
        this.dot = byteBuffer.getFloat();
        this.distSq = byteBuffer.getFloat();
        this.chance = byteBuffer.getInt();
    }
    
    @Override
    public void write(final ByteBufferWriter byteBufferWriter) {
        this.object.write(byteBufferWriter);
        this.window.write(byteBufferWriter);
        byteBufferWriter.putFloat(this.x);
        byteBufferWriter.putFloat(this.y);
        byteBufferWriter.putFloat(this.z);
        byteBufferWriter.putFloat(this.dot);
        byteBufferWriter.putFloat(this.distSq);
        byteBufferWriter.putInt(this.chance);
    }
    
    @Override
    public int getPacketSizeBytes() {
        return 24 + this.object.getPacketSizeBytes() + this.window.getPacketSizeBytes();
    }
    
    @Override
    public String getDescription() {
        return invokedynamic(makeConcatWithConstants:(FFFFFILjava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.x, this.y, this.z, this.dot, this.distSq, this.chance, this.object.getDescription(), this.window.getDescription());
    }
}

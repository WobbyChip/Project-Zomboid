// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network.packets.hit;

import zombie.characters.IsoGameCharacter;
import zombie.core.network.ByteBufferWriter;
import java.nio.ByteBuffer;
import zombie.characters.HitReactionNetworkAI;
import zombie.network.packets.INetworkPacket;

public class Fall implements INetworkPacket
{
    protected float dropPositionX;
    protected float dropPositionY;
    protected byte dropPositionZ;
    protected float dropDirection;
    
    public void set(final HitReactionNetworkAI hitReactionNetworkAI) {
        this.dropPositionX = hitReactionNetworkAI.finalPosition.x;
        this.dropPositionY = hitReactionNetworkAI.finalPosition.y;
        this.dropPositionZ = hitReactionNetworkAI.finalPositionZ;
        this.dropDirection = hitReactionNetworkAI.finalDirection.getDirection();
    }
    
    public void set(final float dropPositionX, final float dropPositionY, final byte dropPositionZ, final float dropDirection) {
        this.dropPositionX = dropPositionX;
        this.dropPositionY = dropPositionY;
        this.dropPositionZ = dropPositionZ;
        this.dropDirection = dropDirection;
    }
    
    @Override
    public void parse(final ByteBuffer byteBuffer) {
        this.dropPositionX = byteBuffer.getFloat();
        this.dropPositionY = byteBuffer.getFloat();
        this.dropPositionZ = byteBuffer.get();
        this.dropDirection = byteBuffer.getFloat();
    }
    
    @Override
    public void write(final ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putFloat(this.dropPositionX);
        byteBufferWriter.putFloat(this.dropPositionY);
        byteBufferWriter.putByte(this.dropPositionZ);
        byteBufferWriter.putFloat(this.dropDirection);
    }
    
    @Override
    public String getDescription() {
        return invokedynamic(makeConcatWithConstants:(FFFB)Ljava/lang/String;, this.dropDirection, this.dropPositionX, this.dropPositionY, this.dropPositionZ);
    }
    
    public void process(final IsoGameCharacter isoGameCharacter) {
        if (this.isSetup() && isoGameCharacter.getHitReactionNetworkAI() != null) {
            isoGameCharacter.getHitReactionNetworkAI().process(this.dropPositionX, this.dropPositionY, this.dropPositionZ, this.dropDirection);
        }
    }
    
    boolean isSetup() {
        return this.dropPositionX != 0.0f && this.dropPositionY != 0.0f;
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network.packets.hit;

import zombie.core.network.ByteBufferWriter;
import java.nio.ByteBuffer;
import zombie.iso.IsoGridSquare;
import zombie.characters.IsoGameCharacter;
import zombie.network.packets.INetworkPacket;

public class Square implements INetworkPacket
{
    protected float positionX;
    protected float positionY;
    protected float positionZ;
    
    public void set(final IsoGameCharacter isoGameCharacter) {
        final IsoGridSquare attackTargetSquare = isoGameCharacter.getAttackTargetSquare();
        if (attackTargetSquare != null) {
            this.positionX = (float)attackTargetSquare.getX();
            this.positionY = (float)attackTargetSquare.getY();
            this.positionZ = (float)attackTargetSquare.getZ();
        }
        else {
            this.positionX = 0.0f;
            this.positionY = 0.0f;
            this.positionZ = 0.0f;
        }
    }
    
    @Override
    public void parse(final ByteBuffer byteBuffer) {
        this.positionX = byteBuffer.getFloat();
        this.positionY = byteBuffer.getFloat();
        this.positionZ = byteBuffer.getFloat();
    }
    
    @Override
    public void write(final ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putFloat(this.positionX);
        byteBufferWriter.putFloat(this.positionY);
        byteBufferWriter.putFloat(this.positionZ);
    }
    
    @Override
    public String getDescription() {
        return invokedynamic(makeConcatWithConstants:(FFF)Ljava/lang/String;, this.positionX, this.positionY, this.positionZ);
    }
    
    void process(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.setAttackTargetSquare(isoGameCharacter.getCell().getGridSquare(this.positionX, this.positionY, this.positionZ));
    }
}

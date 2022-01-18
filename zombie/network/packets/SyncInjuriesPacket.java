// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network.packets;

import zombie.core.network.ByteBufferWriter;
import java.nio.ByteBuffer;
import zombie.characters.IsoPlayer;

public class SyncInjuriesPacket implements INetworkPacket
{
    public short id;
    public float strafeSpeed;
    public float walkSpeed;
    public float walkInjury;
    
    public boolean set(final IsoPlayer isoPlayer) {
        this.id = isoPlayer.getOnlineID();
        this.strafeSpeed = isoPlayer.getVariableFloat("StrafeSpeed", 1.0f);
        this.walkSpeed = isoPlayer.getVariableFloat("WalkSpeed", 1.0f);
        this.walkInjury = isoPlayer.getVariableFloat("WalkInjury", 0.0f);
        return true;
    }
    
    public void process(final IsoPlayer isoPlayer) {
        isoPlayer.setVariable("StrafeSpeed", this.strafeSpeed);
        isoPlayer.setVariable("WalkSpeed", this.walkSpeed);
        isoPlayer.setVariable("WalkInjury", this.walkInjury);
    }
    
    @Override
    public void parse(final ByteBuffer byteBuffer) {
        this.id = byteBuffer.getShort();
        this.strafeSpeed = byteBuffer.getFloat();
        this.walkSpeed = byteBuffer.getFloat();
        this.walkInjury = byteBuffer.getFloat();
    }
    
    @Override
    public void write(final ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putShort(this.id);
        byteBufferWriter.putFloat(this.strafeSpeed);
        byteBufferWriter.putFloat(this.walkSpeed);
        byteBufferWriter.putFloat(this.walkInjury);
    }
    
    @Override
    public int getPacketSizeBytes() {
        return 14;
    }
    
    @Override
    public String getDescription() {
        return invokedynamic(makeConcatWithConstants:(SFFF)Ljava/lang/String;, this.id, this.strafeSpeed, this.walkSpeed, this.walkInjury);
    }
}

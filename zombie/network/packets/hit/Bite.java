// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network.packets.hit;

import zombie.network.GameServer;
import zombie.iso.IsoMovingObject;
import zombie.characters.IsoGameCharacter;
import zombie.core.network.ByteBufferWriter;
import java.nio.ByteBuffer;
import zombie.characters.IsoZombie;
import zombie.network.packets.INetworkPacket;

public class Bite implements INetworkPacket
{
    protected short flags;
    protected float hitDirection;
    
    public void set(final IsoZombie isoZombie) {
        this.flags = 0;
        this.flags |= (short)((isoZombie.getEatBodyTarget() != null) ? 1 : 0);
        this.flags |= (short)(isoZombie.getVariableBoolean("AttackDidDamage") ? 2 : 0);
        this.flags |= (short)("BiteDefended".equals(isoZombie.getHitReaction()) ? 4 : 0);
        this.flags |= (short)(isoZombie.scratch ? 8 : 0);
        this.flags |= (short)(isoZombie.laceration ? 16 : 0);
        this.hitDirection = isoZombie.getHitDir().getDirection();
    }
    
    @Override
    public void parse(final ByteBuffer byteBuffer) {
        this.flags = byteBuffer.getShort();
        this.hitDirection = byteBuffer.getFloat();
    }
    
    @Override
    public void write(final ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putShort(this.flags);
        byteBufferWriter.putFloat(this.hitDirection);
    }
    
    @Override
    public String getDescription() {
        return invokedynamic(makeConcatWithConstants:(ZZZZZF)Ljava/lang/String;, (this.flags & 0x1) != 0x0, (this.flags & 0x2) != 0x0, (this.flags & 0x4) != 0x0, (this.flags & 0x8) != 0x0, (this.flags & 0x10) != 0x0, this.hitDirection);
    }
    
    void process(final IsoZombie attackedBy, final IsoGameCharacter isoGameCharacter) {
        if ((this.flags & 0x4) == 0x0) {
            isoGameCharacter.setAttackedBy(attackedBy);
            if ((this.flags & 0x1) != 0x0 || isoGameCharacter.isDead()) {
                attackedBy.setEatBodyTarget(isoGameCharacter, true);
                attackedBy.setTarget(null);
            }
            if (isoGameCharacter.isAsleep()) {
                if (GameServer.bServer) {
                    isoGameCharacter.sendObjectChange("wakeUp");
                }
                else {
                    isoGameCharacter.forceAwake();
                }
            }
            if ((this.flags & 0x2) != 0x0) {
                isoGameCharacter.reportEvent("washit");
                isoGameCharacter.setVariable("hitpvp", false);
            }
            attackedBy.scratch = ((this.flags & 0x8) != 0x0);
            attackedBy.laceration = ((this.flags & 0x8) != 0x0);
        }
        attackedBy.getHitDir().setLengthAndDirection(this.hitDirection, 1.0f);
    }
}

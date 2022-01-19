// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network.packets.hit;

import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.core.Core;
import zombie.iso.IsoMovingObject;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.network.GameServer;
import zombie.characters.IsoGameCharacter;
import zombie.core.network.ByteBufferWriter;
import java.nio.ByteBuffer;
import zombie.network.packets.INetworkPacket;

public abstract class Hit implements INetworkPacket
{
    protected boolean ignore;
    protected float damage;
    protected float hitForce;
    protected float hitDirectionX;
    protected float hitDirectionY;
    
    public void set(final boolean ignore, final float damage, final float hitForce, final float hitDirectionX, final float hitDirectionY) {
        this.ignore = ignore;
        this.damage = damage;
        this.hitForce = hitForce;
        this.hitDirectionX = hitDirectionX;
        this.hitDirectionY = hitDirectionY;
    }
    
    @Override
    public void parse(final ByteBuffer byteBuffer) {
        this.ignore = (byteBuffer.get() != 0);
        this.damage = byteBuffer.getFloat();
        this.hitForce = byteBuffer.getFloat();
        this.hitDirectionX = byteBuffer.getFloat();
        this.hitDirectionY = byteBuffer.getFloat();
    }
    
    @Override
    public void write(final ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putBoolean(this.ignore);
        byteBufferWriter.putFloat(this.damage);
        byteBufferWriter.putFloat(this.hitForce);
        byteBufferWriter.putFloat(this.hitDirectionX);
        byteBufferWriter.putFloat(this.hitDirectionY);
    }
    
    @Override
    public String getDescription() {
        return invokedynamic(makeConcatWithConstants:(ZFFFF)Ljava/lang/String;, this.ignore, this.damage, this.hitForce, this.hitDirectionX, this.hitDirectionY);
    }
    
    void process(final IsoGameCharacter attackedBy, final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.getHitDir().set(this.hitDirectionX, this.hitDirectionY);
        isoGameCharacter.setHitForce(this.hitForce);
        if (GameServer.bServer && isoGameCharacter instanceof IsoZombie && attackedBy instanceof IsoPlayer) {
            ((IsoZombie)isoGameCharacter).addAggro(attackedBy, this.damage);
            if (Core.bDebug) {
                DebugLog.log(DebugType.Multiplayer, String.format("AddAggro zombie=%d player=%d ( \"%s\" ) damage=%f", isoGameCharacter.getOnlineID(), attackedBy.getOnlineID(), ((IsoPlayer)attackedBy).getUsername(), this.damage));
            }
        }
        isoGameCharacter.setAttackedBy(attackedBy);
    }
}

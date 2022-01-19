// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network.packets.hit;

import zombie.core.raknet.UdpConnection;
import zombie.core.network.ByteBufferWriter;
import zombie.characters.IsoLivingCharacter;
import java.nio.ByteBuffer;
import zombie.inventory.types.HandWeapon;
import zombie.characters.IsoPlayer;
import zombie.network.packets.INetworkPacket;

public abstract class PlayerHitPacket extends HitCharacterPacket implements INetworkPacket
{
    protected final Player wielder;
    protected final Weapon weapon;
    
    public PlayerHitPacket(final HitType hitType) {
        super(hitType);
        this.wielder = new Player();
        this.weapon = new Weapon();
    }
    
    public void set(final IsoPlayer isoPlayer, final HandWeapon handWeapon, final boolean b) {
        this.wielder.set(isoPlayer, b);
        this.weapon.set(handWeapon);
    }
    
    @Override
    public void parse(final ByteBuffer byteBuffer) {
        this.wielder.parse(byteBuffer);
        this.weapon.parse(byteBuffer, (IsoLivingCharacter)this.wielder.getCharacter());
    }
    
    @Override
    public void write(final ByteBufferWriter byteBufferWriter) {
        super.write(byteBufferWriter);
        this.wielder.write(byteBufferWriter);
        this.weapon.write(byteBufferWriter);
    }
    
    @Override
    public boolean isRelevant(final UdpConnection udpConnection) {
        return this.wielder.isRelevant(udpConnection);
    }
    
    @Override
    public boolean isConsistent() {
        return super.isConsistent() && this.weapon.isConsistent() && this.wielder.isConsistent();
    }
    
    @Override
    public String getDescription() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, super.getDescription(), this.wielder.getDescription(), this.weapon.getDescription());
    }
    
    @Override
    protected void preProcess() {
        this.wielder.process();
    }
    
    @Override
    protected void postProcess() {
        this.wielder.process();
    }
    
    @Override
    protected void attack() {
        this.wielder.attack(this.weapon.getWeapon());
    }
    
    @Override
    protected void react() {
    }
}

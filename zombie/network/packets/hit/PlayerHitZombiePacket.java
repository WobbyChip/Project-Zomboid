// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network.packets.hit;

import zombie.core.network.ByteBufferWriter;
import java.nio.ByteBuffer;
import zombie.inventory.types.HandWeapon;
import zombie.characters.IsoZombie;
import zombie.characters.IsoPlayer;
import zombie.network.packets.INetworkPacket;

public class PlayerHitZombiePacket extends PlayerHitPacket implements INetworkPacket
{
    protected final Zombie target;
    protected final WeaponHit hit;
    protected final Fall fall;
    
    public PlayerHitZombiePacket() {
        super(HitType.PlayerHitZombie);
        this.target = new Zombie();
        this.hit = new WeaponHit();
        this.fall = new Fall();
    }
    
    public void set(final IsoPlayer isoPlayer, final IsoZombie isoZombie, final HandWeapon handWeapon, final float n, final boolean b, final float n2, final boolean b2, final boolean b3, final boolean b4) {
        super.set(isoPlayer, handWeapon, b2);
        this.target.set(isoZombie, b3);
        this.hit.set(b, n, n2, isoZombie.getHitForce(), isoZombie.getHitDir().x, isoZombie.getHitDir().y, b4);
        this.fall.set(isoZombie.getHitReactionNetworkAI());
    }
    
    @Override
    public void parse(final ByteBuffer byteBuffer) {
        super.parse(byteBuffer);
        this.target.parse(byteBuffer);
        this.hit.parse(byteBuffer);
        this.fall.parse(byteBuffer);
    }
    
    @Override
    public void write(final ByteBufferWriter byteBufferWriter) {
        super.write(byteBufferWriter);
        this.target.write(byteBufferWriter);
        this.hit.write(byteBufferWriter);
        this.fall.write(byteBufferWriter);
    }
    
    @Override
    public boolean isConsistent() {
        return super.isConsistent() && this.target.isConsistent() && this.hit.isConsistent();
    }
    
    @Override
    public String getDescription() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, super.getDescription(), this.target.getDescription(), this.hit.getDescription(), this.fall.getDescription());
    }
    
    @Override
    public String getHitDescription() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName(), this.fall.getDescription(), this.target.getFlagsDescription());
    }
    
    @Override
    protected void preProcess() {
        super.preProcess();
        this.target.process();
    }
    
    @Override
    protected void process() {
        this.hit.process(this.wielder.getCharacter(), this.target.getCharacter(), this.weapon.getWeapon());
        this.fall.process(this.target.getCharacter());
    }
    
    @Override
    protected void postProcess() {
        super.postProcess();
        this.target.process();
    }
    
    @Override
    protected void attack() {
        this.wielder.attack(this.weapon.getWeapon());
    }
    
    @Override
    protected void react() {
        this.target.react(this.weapon.getWeapon());
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network.packets.hit;

import zombie.core.raknet.UdpConnection;
import zombie.core.network.ByteBufferWriter;
import java.nio.ByteBuffer;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.network.packets.INetworkPacket;

public class ZombieHitPlayerPacket extends HitCharacterPacket implements INetworkPacket
{
    protected final Zombie wielder;
    protected final Player target;
    protected final Bite bite;
    
    public ZombieHitPlayerPacket() {
        super(HitType.ZombieHitPlayer);
        this.wielder = new Zombie();
        this.target = new Player();
        this.bite = new Bite();
    }
    
    public void set(final IsoZombie isoZombie, final IsoPlayer isoPlayer) {
        this.wielder.set(isoZombie, false);
        this.target.set(isoPlayer, false);
        this.bite.set(isoZombie);
    }
    
    @Override
    public void parse(final ByteBuffer byteBuffer) {
        this.wielder.parse(byteBuffer);
        this.target.parse(byteBuffer);
        this.bite.parse(byteBuffer);
    }
    
    @Override
    public void write(final ByteBufferWriter byteBufferWriter) {
        super.write(byteBufferWriter);
        this.wielder.write(byteBufferWriter);
        this.target.write(byteBufferWriter);
        this.bite.write(byteBufferWriter);
    }
    
    @Override
    public boolean isRelevant(final UdpConnection udpConnection) {
        return this.target.isRelevant(udpConnection);
    }
    
    @Override
    public boolean isConsistent() {
        return super.isConsistent() && this.target.isConsistent() && this.wielder.isConsistent();
    }
    
    @Override
    public String getDescription() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, super.getDescription(), this.wielder.getDescription(), this.target.getDescription(), this.bite.getDescription());
    }
    
    @Override
    protected void preProcess() {
        this.wielder.process();
        this.target.process();
    }
    
    @Override
    protected void process() {
        this.bite.process((IsoZombie)this.wielder.getCharacter(), this.target.getCharacter());
    }
    
    @Override
    protected void postProcess() {
        this.wielder.process();
        this.target.process();
    }
    
    @Override
    protected void attack() {
    }
    
    @Override
    protected void react() {
        this.wielder.react();
        this.target.react();
    }
}

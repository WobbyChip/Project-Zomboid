// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network.packets.hit;

import zombie.core.raknet.UdpConnection;
import zombie.core.network.ByteBufferWriter;
import java.nio.ByteBuffer;
import zombie.characters.IsoGameCharacter;
import zombie.inventory.types.HandWeapon;
import zombie.characters.IsoPlayer;
import zombie.network.packets.INetworkPacket;

public class PlayerHitSquarePacket extends PlayerHitPacket implements INetworkPacket
{
    protected final Square square;
    
    public PlayerHitSquarePacket() {
        super(HitType.PlayerHitSquare);
        this.square = new Square();
    }
    
    @Override
    public void set(final IsoPlayer isoPlayer, final HandWeapon handWeapon, final boolean b) {
        super.set(isoPlayer, handWeapon, b);
        this.square.set(isoPlayer);
    }
    
    @Override
    public void parse(final ByteBuffer byteBuffer) {
        super.parse(byteBuffer);
        this.square.parse(byteBuffer);
    }
    
    @Override
    public void write(final ByteBufferWriter byteBufferWriter) {
        super.write(byteBufferWriter);
        this.square.write(byteBufferWriter);
    }
    
    @Override
    public boolean isRelevant(final UdpConnection udpConnection) {
        return this.wielder.isRelevant(udpConnection);
    }
    
    @Override
    public boolean isConsistent() {
        return super.isConsistent() && this.square.isConsistent();
    }
    
    @Override
    public String getDescription() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, super.getDescription(), this.square.getDescription());
    }
    
    @Override
    protected void process() {
        this.square.process(this.wielder.getCharacter());
    }
}

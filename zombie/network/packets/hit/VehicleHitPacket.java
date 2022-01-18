// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network.packets.hit;

import zombie.core.raknet.UdpConnection;
import zombie.core.network.ByteBufferWriter;
import java.nio.ByteBuffer;
import zombie.vehicles.BaseVehicle;
import zombie.characters.IsoPlayer;
import zombie.network.packets.INetworkPacket;

public abstract class VehicleHitPacket extends HitCharacterPacket implements INetworkPacket
{
    protected final Player wielder;
    protected final Vehicle vehicle;
    
    public VehicleHitPacket(final HitType hitType) {
        super(hitType);
        this.wielder = new Player();
        this.vehicle = new Vehicle();
    }
    
    public void set(final IsoPlayer isoPlayer, final BaseVehicle baseVehicle, final boolean b) {
        this.wielder.set(isoPlayer, b);
        this.vehicle.set(baseVehicle);
    }
    
    @Override
    public void parse(final ByteBuffer byteBuffer) {
        this.wielder.parse(byteBuffer);
        this.vehicle.parse(byteBuffer);
    }
    
    @Override
    public void write(final ByteBufferWriter byteBufferWriter) {
        super.write(byteBufferWriter);
        this.wielder.write(byteBufferWriter);
        this.vehicle.write(byteBufferWriter);
    }
    
    @Override
    public boolean isRelevant(final UdpConnection udpConnection) {
        return this.wielder.isRelevant(udpConnection);
    }
    
    @Override
    public boolean isConsistent() {
        return super.isConsistent() && this.wielder.isConsistent() && this.vehicle.isConsistent();
    }
    
    @Override
    public String getDescription() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, super.getDescription(), this.wielder.getDescription(), this.vehicle.getDescription());
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
    }
}

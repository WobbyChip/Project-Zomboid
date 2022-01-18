// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network.packets.hit;

import zombie.core.network.ByteBufferWriter;
import java.nio.ByteBuffer;
import zombie.vehicles.BaseVehicle;
import zombie.characters.IsoPlayer;
import zombie.network.packets.INetworkPacket;

public class VehicleHitPlayerPacket extends VehicleHitPacket implements INetworkPacket
{
    protected final Player target;
    protected final VehicleHit vehicleHit;
    protected final Fall fall;
    
    public VehicleHitPlayerPacket() {
        super(HitType.VehicleHitPlayer);
        this.target = new Player();
        this.vehicleHit = new VehicleHit();
        this.fall = new Fall();
    }
    
    public void set(final IsoPlayer isoPlayer, final IsoPlayer isoPlayer2, final BaseVehicle baseVehicle, final float n, final boolean b, final int n2, final float n3, final boolean b2) {
        super.set(isoPlayer, baseVehicle, false);
        this.target.set(isoPlayer2, false);
        this.vehicleHit.set(false, n, isoPlayer2.getHitForce(), isoPlayer2.getHitDir().x, isoPlayer2.getHitDir().y, n2, n3, b2, b);
        this.fall.set(isoPlayer2.getHitReactionNetworkAI());
    }
    
    @Override
    public void parse(final ByteBuffer byteBuffer) {
        super.parse(byteBuffer);
        this.target.parse(byteBuffer);
        this.vehicleHit.parse(byteBuffer);
        this.fall.parse(byteBuffer);
    }
    
    @Override
    public void write(final ByteBufferWriter byteBufferWriter) {
        super.write(byteBufferWriter);
        this.target.write(byteBufferWriter);
        this.vehicleHit.write(byteBufferWriter);
        this.fall.write(byteBufferWriter);
    }
    
    @Override
    public boolean isConsistent() {
        return super.isConsistent() && this.target.isConsistent() && this.vehicleHit.isConsistent();
    }
    
    @Override
    public String getDescription() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, super.getDescription(), this.target.getDescription(), this.vehicleHit.getDescription(), this.fall.getDescription());
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
        this.vehicleHit.process(this.wielder.getCharacter(), this.target.getCharacter(), this.vehicle.getVehicle());
        this.fall.process(this.target.getCharacter());
    }
    
    @Override
    protected void postProcess() {
        super.postProcess();
        this.target.process();
    }
    
    @Override
    protected void react() {
        this.target.react();
    }
    
    @Override
    protected void postpone() {
        this.target.getCharacter().getNetworkCharacterAI().setVehicleHit(this);
    }
}

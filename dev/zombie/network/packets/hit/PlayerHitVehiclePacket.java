// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network.packets.hit;

import zombie.core.network.ByteBufferWriter;
import java.nio.ByteBuffer;
import zombie.inventory.types.HandWeapon;
import zombie.vehicles.BaseVehicle;
import zombie.characters.IsoPlayer;
import zombie.network.packets.INetworkPacket;

public class PlayerHitVehiclePacket extends PlayerHitPacket implements INetworkPacket
{
    protected final Vehicle vehicle;
    
    public PlayerHitVehiclePacket() {
        super(HitType.PlayerHitVehicle);
        this.vehicle = new Vehicle();
    }
    
    public void set(final IsoPlayer isoPlayer, final BaseVehicle baseVehicle, final HandWeapon handWeapon, final boolean b) {
        super.set(isoPlayer, handWeapon, b);
        this.vehicle.set(baseVehicle);
    }
    
    @Override
    public void parse(final ByteBuffer byteBuffer) {
        super.parse(byteBuffer);
        this.vehicle.parse(byteBuffer);
    }
    
    @Override
    public void write(final ByteBufferWriter byteBufferWriter) {
        super.write(byteBufferWriter);
        this.vehicle.write(byteBufferWriter);
    }
    
    @Override
    public boolean isConsistent() {
        return super.isConsistent() && this.vehicle.isConsistent();
    }
    
    @Override
    public String getDescription() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, super.getDescription(), this.vehicle.getDescription());
    }
    
    @Override
    protected void process() {
        this.vehicle.process(this.wielder.getCharacter(), this.weapon.getWeapon());
    }
}

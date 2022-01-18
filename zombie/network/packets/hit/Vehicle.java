// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network.packets.hit;

import zombie.network.GameServer;
import zombie.inventory.types.HandWeapon;
import zombie.characters.IsoGameCharacter;
import zombie.core.network.ByteBufferWriter;
import zombie.vehicles.VehicleManager;
import java.nio.ByteBuffer;
import zombie.vehicles.BaseVehicle;
import zombie.network.packets.INetworkPacket;

public class Vehicle extends Instance implements INetworkPacket
{
    protected BaseVehicle vehicle;
    
    public void set(final BaseVehicle vehicle) {
        super.set(vehicle.getId());
        this.vehicle = vehicle;
    }
    
    @Override
    public void parse(final ByteBuffer byteBuffer) {
        super.parse(byteBuffer);
        this.vehicle = VehicleManager.instance.getVehicleByID(this.ID);
    }
    
    @Override
    public void write(final ByteBufferWriter byteBufferWriter) {
        super.write(byteBufferWriter);
    }
    
    @Override
    public boolean isConsistent() {
        return super.isConsistent() && this.vehicle != null;
    }
    
    @Override
    public String getDescription() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, super.getDescription(), (this.vehicle == null) ? "?" : invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.vehicle.getScriptName()));
    }
    
    void process(final IsoGameCharacter isoGameCharacter, final HandWeapon handWeapon) {
        if (GameServer.bServer) {
            this.vehicle.hitVehicle(isoGameCharacter, handWeapon);
        }
    }
    
    BaseVehicle getVehicle() {
        return this.vehicle;
    }
}

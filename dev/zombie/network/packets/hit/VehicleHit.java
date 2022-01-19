// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network.packets.hit;

import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.vehicles.BaseVehicle;
import zombie.characters.IsoGameCharacter;
import zombie.core.network.ByteBufferWriter;
import java.nio.ByteBuffer;
import zombie.network.packets.INetworkPacket;

public class VehicleHit extends Hit implements INetworkPacket
{
    public int vehicleDamage;
    public float vehicleSpeed;
    public boolean isVehicleHitFromBehind;
    public boolean isTargetHitFromBehind;
    
    public void set(final boolean b, final float n, final float n2, final float n3, final float n4, final int vehicleDamage, final float vehicleSpeed, final boolean isVehicleHitFromBehind, final boolean isTargetHitFromBehind) {
        super.set(b, n, n2, n3, n4);
        this.vehicleDamage = vehicleDamage;
        this.vehicleSpeed = vehicleSpeed;
        this.isVehicleHitFromBehind = isVehicleHitFromBehind;
        this.isTargetHitFromBehind = isTargetHitFromBehind;
    }
    
    @Override
    public void parse(final ByteBuffer byteBuffer) {
        super.parse(byteBuffer);
        this.vehicleDamage = byteBuffer.getInt();
        this.vehicleSpeed = byteBuffer.getFloat();
        this.isVehicleHitFromBehind = (byteBuffer.get() != 0);
        this.isTargetHitFromBehind = (byteBuffer.get() != 0);
    }
    
    @Override
    public void write(final ByteBufferWriter byteBufferWriter) {
        super.write(byteBufferWriter);
        byteBufferWriter.putInt(this.vehicleDamage);
        byteBufferWriter.putFloat(this.vehicleSpeed);
        byteBufferWriter.putBoolean(this.isVehicleHitFromBehind);
        byteBufferWriter.putBoolean(this.isTargetHitFromBehind);
    }
    
    @Override
    public String getDescription() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;FILjava/lang/String;Ljava/lang/String;)Ljava/lang/String;, super.getDescription(), this.vehicleSpeed, this.vehicleDamage, this.isTargetHitFromBehind ? "FRONT" : "BEHIND", this.isVehicleHitFromBehind ? "FRONT" : "REAR");
    }
    
    void process(final IsoGameCharacter isoGameCharacter, final IsoGameCharacter isoGameCharacter2, final BaseVehicle baseVehicle) {
        super.process(isoGameCharacter, isoGameCharacter2);
        if (GameServer.bServer) {
            if (this.vehicleDamage != 0) {
                if (this.isVehicleHitFromBehind) {
                    baseVehicle.addDamageFrontHitAChr(this.vehicleDamage);
                }
                else {
                    baseVehicle.addDamageRearHitAChr(this.vehicleDamage);
                }
                baseVehicle.transmitBlood();
            }
        }
        else if (GameClient.bClient) {
            if (isoGameCharacter2 instanceof IsoZombie) {
                ((IsoZombie)isoGameCharacter2).applyDamageFromVehicle(this.vehicleSpeed, this.damage);
            }
            else if (isoGameCharacter2 instanceof IsoPlayer) {
                ((IsoPlayer)isoGameCharacter2).getDamageFromHitByACar(this.vehicleSpeed);
                ((IsoPlayer)isoGameCharacter2).actionContext.reportEvent("washit");
                isoGameCharacter2.setVariable("hitpvp", false);
            }
        }
    }
}

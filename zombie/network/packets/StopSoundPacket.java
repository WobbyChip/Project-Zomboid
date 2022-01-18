// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network.packets;

import zombie.core.network.ByteBufferWriter;
import zombie.GameWindow;
import java.nio.ByteBuffer;
import zombie.vehicles.BaseVehicle;
import zombie.util.Type;
import zombie.characters.IsoGameCharacter;
import zombie.iso.IsoMovingObject;
import zombie.network.packets.hit.MovingObject;

public class StopSoundPacket implements INetworkPacket
{
    MovingObject object;
    String name;
    boolean trigger;
    
    public StopSoundPacket() {
        this.object = new MovingObject();
    }
    
    public void set(final IsoMovingObject movingObject, final String name, final boolean trigger) {
        this.object.setMovingObject(movingObject);
        this.name = name;
        this.trigger = trigger;
    }
    
    public void process() {
        final IsoMovingObject movingObject = this.object.getMovingObject();
        final IsoGameCharacter isoGameCharacter = Type.tryCastTo(movingObject, IsoGameCharacter.class);
        if (isoGameCharacter != null) {
            if (this.trigger) {
                isoGameCharacter.getEmitter().stopOrTriggerSoundByName(this.name);
            }
            else {
                isoGameCharacter.getEmitter().stopSoundByName(this.name);
            }
            return;
        }
        final BaseVehicle baseVehicle = Type.tryCastTo(movingObject, BaseVehicle.class);
        if (baseVehicle != null) {
            if (this.trigger) {
                baseVehicle.getEmitter().stopOrTriggerSoundByName(this.name);
            }
            else {
                baseVehicle.getEmitter().stopSoundByName(this.name);
            }
        }
    }
    
    @Override
    public void parse(final ByteBuffer byteBuffer) {
        this.trigger = (byteBuffer.get() == 1);
        this.object.parse(byteBuffer);
        this.name = GameWindow.ReadString(byteBuffer);
    }
    
    @Override
    public void write(final ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putByte((byte)(this.trigger ? 1 : 0));
        this.object.write(byteBufferWriter);
        byteBufferWriter.putUTF(this.name);
    }
    
    @Override
    public int getPacketSizeBytes() {
        return this.object.getPacketSizeBytes() + 2 + this.name.length();
    }
    
    @Override
    public String getDescription() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.name, this.object.getDescription());
    }
}

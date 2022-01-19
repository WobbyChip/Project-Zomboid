// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network.packets;

import zombie.core.network.ByteBufferWriter;
import zombie.GameWindow;
import java.nio.ByteBuffer;
import zombie.audio.BaseSoundEmitter;
import zombie.characters.BaseCharacterSoundEmitter;
import zombie.iso.IsoWorld;
import zombie.iso.IsoObject;
import zombie.characters.IsoGameCharacter;
import zombie.iso.IsoMovingObject;
import zombie.network.packets.hit.MovingObject;

public class PlaySoundPacket implements INetworkPacket
{
    String name;
    MovingObject object;
    boolean loop;
    
    public PlaySoundPacket() {
        this.object = new MovingObject();
    }
    
    public void set(final String name, final boolean loop, final IsoMovingObject movingObject) {
        this.name = name;
        this.loop = loop;
        this.object.setMovingObject(movingObject);
    }
    
    public void process() {
        final IsoMovingObject movingObject = this.object.getMovingObject();
        if (movingObject instanceof IsoGameCharacter) {
            final BaseCharacterSoundEmitter emitter = ((IsoGameCharacter)movingObject).getEmitter();
            if (!this.loop) {
                emitter.playSoundImpl(this.name, null);
            }
        }
        else {
            BaseSoundEmitter emitter2 = movingObject.emitter;
            if (emitter2 == null) {
                emitter2 = IsoWorld.instance.getFreeEmitter(movingObject.x, movingObject.y, movingObject.z);
                IsoWorld.instance.takeOwnershipOfEmitter(emitter2);
                movingObject.emitter = emitter2;
            }
            if (!this.loop) {
                emitter2.playSoundImpl(this.name, (IsoObject)null);
            }
            else {
                emitter2.playSoundLoopedImpl(this.name);
            }
            emitter2.tick();
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    public IsoMovingObject getMovingObject() {
        return this.object.getMovingObject();
    }
    
    @Override
    public void parse(final ByteBuffer byteBuffer) {
        this.object.parse(byteBuffer);
        this.name = GameWindow.ReadString(byteBuffer);
        this.loop = (byteBuffer.get() == 1);
    }
    
    @Override
    public void write(final ByteBufferWriter byteBufferWriter) {
        this.object.write(byteBufferWriter);
        byteBufferWriter.putUTF(this.name);
        byteBufferWriter.putByte((byte)(this.loop ? 1 : 0));
    }
    
    @Override
    public boolean isConsistent() {
        return this.name != null && !this.name.isEmpty();
    }
    
    @Override
    public int getPacketSizeBytes() {
        return 12 + this.name.length();
    }
    
    @Override
    public String getDescription() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Z)Ljava/lang/String;, this.name, this.object.getDescription(), this.loop);
    }
}

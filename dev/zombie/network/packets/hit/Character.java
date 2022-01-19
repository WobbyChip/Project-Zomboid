// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network.packets.hit;

import zombie.network.GameServer;
import zombie.core.network.ByteBufferWriter;
import zombie.GameWindow;
import java.nio.ByteBuffer;
import java.util.Optional;
import zombie.characters.IsoGameCharacter;
import zombie.network.packets.INetworkPacket;

public abstract class Character extends Instance implements INetworkPacket
{
    protected IsoGameCharacter character;
    protected short characterFlags;
    protected float positionX;
    protected float positionY;
    protected float positionZ;
    protected float directionX;
    protected float directionY;
    protected String characterReaction;
    protected String playerReaction;
    protected String zombieReaction;
    
    public void set(final IsoGameCharacter character) {
        super.set(character.getOnlineID());
        this.characterFlags = 0;
        this.characterFlags |= (short)(character.isDead() ? 1 : 0);
        this.characterFlags |= (short)(character.isCloseKilled() ? 2 : 0);
        this.characterFlags |= (short)(character.isHitFromBehind() ? 4 : 0);
        this.characterFlags |= (short)(character.isFallOnFront() ? 8 : 0);
        this.characterFlags |= (short)(character.isKnockedDown() ? 16 : 0);
        this.characterFlags |= (short)(character.isOnFloor() ? 32 : 0);
        this.character = character;
        this.positionX = character.getX();
        this.positionY = character.getY();
        this.positionZ = character.getZ();
        this.directionX = character.getForwardDirection().getX();
        this.directionY = character.getForwardDirection().getY();
        this.characterReaction = Optional.ofNullable(character.getHitReaction()).orElse("");
        this.playerReaction = Optional.ofNullable(character.getVariableString("PlayerHitReaction")).orElse("");
        this.zombieReaction = Optional.ofNullable(character.getVariableString("ZombieHitReaction")).orElse("");
    }
    
    @Override
    public void parse(final ByteBuffer byteBuffer) {
        super.parse(byteBuffer);
        this.characterFlags = byteBuffer.getShort();
        this.positionX = byteBuffer.getFloat();
        this.positionY = byteBuffer.getFloat();
        this.positionZ = byteBuffer.getFloat();
        this.directionX = byteBuffer.getFloat();
        this.directionY = byteBuffer.getFloat();
        this.characterReaction = GameWindow.ReadString(byteBuffer);
        this.playerReaction = GameWindow.ReadString(byteBuffer);
        this.zombieReaction = GameWindow.ReadString(byteBuffer);
    }
    
    @Override
    public void write(final ByteBufferWriter byteBufferWriter) {
        super.write(byteBufferWriter);
        byteBufferWriter.putShort(this.characterFlags);
        byteBufferWriter.putFloat(this.positionX);
        byteBufferWriter.putFloat(this.positionY);
        byteBufferWriter.putFloat(this.positionZ);
        byteBufferWriter.putFloat(this.directionX);
        byteBufferWriter.putFloat(this.directionY);
        byteBufferWriter.putUTF(this.characterReaction);
        byteBufferWriter.putUTF(this.playerReaction);
        byteBufferWriter.putUTF(this.zombieReaction);
    }
    
    @Override
    public boolean isConsistent() {
        return super.isConsistent() && this.character != null;
    }
    
    @Override
    public String getDescription() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;FFFFFLjava/io/Serializable;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, super.getDescription(), this.characterReaction, this.playerReaction, this.zombieReaction, this.getFlagsDescription(), this.positionX, this.positionY, this.positionZ, this.directionX, this.directionY, (this.character == null) ? "?" : Float.valueOf(this.character.getHealth()), (this.character == null) ? "?" : invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.character.getCurrentActionContextStateName()), (this.character == null) ? "?" : invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.character.getPreviousActionContextStateName()));
    }
    
    String getFlagsDescription() {
        return invokedynamic(makeConcatWithConstants:(ZZZZZZ)Ljava/lang/String;, (this.characterFlags & 0x1) != 0x0, (this.characterFlags & 0x10) != 0x0, (this.characterFlags & 0x2) != 0x0, (this.characterFlags & 0x4) != 0x0, (this.characterFlags & 0x8) != 0x0, (this.characterFlags & 0x20) != 0x0);
    }
    
    void process() {
        this.character.setHitReaction(this.characterReaction);
        this.character.setVariable("PlayerHitReaction", this.playerReaction);
        this.character.setVariable("ZombieHitReaction", this.zombieReaction);
        this.character.setCloseKilled((this.characterFlags & 0x2) != 0x0);
        this.character.setHitFromBehind((this.characterFlags & 0x4) != 0x0);
        this.character.setFallOnFront((this.characterFlags & 0x8) != 0x0);
        this.character.setKnockedDown((this.characterFlags & 0x10) != 0x0);
        this.character.setOnFloor((this.characterFlags & 0x20) != 0x0);
        if (GameServer.bServer && (this.characterFlags & 0x20) == 0x0 && (this.characterFlags & 0x4) != 0x0) {
            this.character.setFallOnFront(true);
        }
    }
    
    protected void react() {
    }
    
    abstract IsoGameCharacter getCharacter();
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network.packets;

import zombie.core.network.ByteBufferWriter;
import zombie.network.GameClient;
import zombie.network.GameServer;
import java.nio.ByteBuffer;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;

public class DeadPlayerPacket extends DeadCharacterPacket implements INetworkPacket
{
    private byte playerFlags;
    private float infectionLevel;
    private IsoPlayer player;
    
    @Override
    public void set(final IsoGameCharacter isoGameCharacter) {
        super.set(isoGameCharacter);
        this.player = (IsoPlayer)isoGameCharacter;
        this.infectionLevel = this.player.getBodyDamage().getInfectionLevel();
        this.playerFlags |= (byte)(this.player.getBodyDamage().isInfected() ? 1 : 0);
    }
    
    @Override
    public void process() {
        if (this.player != null) {
            this.player.getBodyDamage().setOverallBodyHealth(0.0f);
            this.player.getBodyDamage().setInfected((this.playerFlags & 0x1) != 0x0);
            this.player.getBodyDamage().setInfectionLevel(this.infectionLevel);
            super.process();
        }
    }
    
    @Override
    public void parse(final ByteBuffer byteBuffer) {
        super.parse(byteBuffer);
        this.infectionLevel = byteBuffer.getFloat();
        if (GameServer.bServer) {
            this.player = GameServer.IDToPlayerMap.get(this.id);
        }
        else if (GameClient.bClient) {
            this.player = GameClient.IDToPlayerMap.get(this.id);
        }
        if (this.player != null) {
            this.character = this.player;
            this.parseCharacterInventory(byteBuffer);
            this.character.setHealth(0.0f);
            this.character.getNetworkCharacterAI().setDeadBody(this);
        }
    }
    
    @Override
    public void write(final ByteBufferWriter byteBufferWriter) {
        super.write(byteBufferWriter);
        byteBufferWriter.putFloat(this.infectionLevel);
        this.writeCharacterInventory(byteBufferWriter);
    }
    
    @Override
    public String getDescription() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, super.getDescription(), String.format(" | isInfected=%b infectionLevel=%f", (this.playerFlags & 0x1) != 0x0, this.infectionLevel));
    }
    
    public IsoPlayer getPlayer() {
        return this.player;
    }
}

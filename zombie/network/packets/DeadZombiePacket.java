// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network.packets;

import zombie.core.network.ByteBufferWriter;
import zombie.network.GameClient;
import zombie.network.ServerMap;
import zombie.network.GameServer;
import java.nio.ByteBuffer;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;

public class DeadZombiePacket extends DeadCharacterPacket implements INetworkPacket
{
    private byte zombieFlags;
    private IsoZombie zombie;
    
    @Override
    public void set(final IsoGameCharacter isoGameCharacter) {
        super.set(isoGameCharacter);
        this.zombie = (IsoZombie)isoGameCharacter;
        this.zombieFlags |= (byte)(this.zombie.isCrawling() ? 1 : 0);
    }
    
    @Override
    public void process() {
        if (this.zombie != null) {
            this.zombie.setCrawler((this.zombieFlags & 0x1) != 0x0);
            super.process();
        }
    }
    
    @Override
    public void parse(final ByteBuffer byteBuffer) {
        super.parse(byteBuffer);
        if (GameServer.bServer) {
            this.zombie = ServerMap.instance.ZombieMap.get(this.id);
        }
        else if (GameClient.bClient) {
            this.zombie = (IsoZombie)GameClient.IDToZombieMap.get(this.id);
        }
        if (this.zombie != null) {
            this.character = this.zombie;
            if (!GameServer.bServer || !this.zombie.isReanimatedPlayer()) {
                this.parseCharacterInventory(byteBuffer);
            }
            this.character.setHealth(0.0f);
            this.character.getHitReactionNetworkAI().process(this.x, this.y, this.z, this.angle);
            this.character.getNetworkCharacterAI().setDeadBody(this);
        }
        else {
            this.parseDeadBodyInventory(byteBuffer);
        }
    }
    
    @Override
    public void write(final ByteBufferWriter byteBufferWriter) {
        super.write(byteBufferWriter);
        this.writeCharacterInventory(byteBufferWriter);
    }
    
    @Override
    public String getDescription() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, super.getDescription(), String.format(" | isCrawling=%b", (this.zombieFlags & 0x1) != 0x0));
    }
    
    public IsoZombie getZombie() {
        return this.zombie;
    }
}

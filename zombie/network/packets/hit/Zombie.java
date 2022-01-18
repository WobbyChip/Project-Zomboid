// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network.packets.hit;

import zombie.core.skinnedmodel.ModelManager;
import zombie.network.ServerGUI;
import zombie.PersistentOutfits;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.HandWeapon;
import zombie.core.network.ByteBufferWriter;
import zombie.network.GameClient;
import zombie.network.ServerMap;
import zombie.network.GameServer;
import zombie.GameWindow;
import java.nio.ByteBuffer;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.network.packets.INetworkPacket;

public class Zombie extends Character implements INetworkPacket
{
    protected IsoZombie zombie;
    protected short zombieFlags;
    protected String attackOutcome;
    protected String attackPosition;
    
    public void set(final IsoZombie zombie, final boolean b) {
        super.set(zombie);
        this.zombie = zombie;
        this.zombieFlags = 0;
        this.zombieFlags |= (short)(zombie.isStaggerBack() ? 1 : 0);
        this.zombieFlags |= (short)(zombie.isFakeDead() ? 2 : 0);
        this.zombieFlags |= (short)(zombie.isBecomeCrawler() ? 4 : 0);
        this.zombieFlags |= (short)(zombie.isCrawling() ? 8 : 0);
        this.zombieFlags |= (short)(zombie.isKnifeDeath() ? 16 : 0);
        this.zombieFlags |= (short)(zombie.isJawStabAttach() ? 32 : 0);
        this.zombieFlags |= (short)(b ? 64 : 0);
        this.zombieFlags |= (short)(zombie.getVariableBoolean("AttackDidDamage") ? 128 : 0);
        this.attackOutcome = zombie.getVariableString("AttackOutcome");
        this.attackPosition = zombie.getPlayerAttackPosition();
    }
    
    @Override
    public void parse(final ByteBuffer byteBuffer) {
        super.parse(byteBuffer);
        this.zombieFlags = byteBuffer.getShort();
        this.attackOutcome = GameWindow.ReadString(byteBuffer);
        this.attackPosition = GameWindow.ReadString(byteBuffer);
        if (GameServer.bServer) {
            this.zombie = ServerMap.instance.ZombieMap.get(this.ID);
            this.character = this.zombie;
        }
        else if (GameClient.bClient) {
            this.zombie = (IsoZombie)GameClient.IDToZombieMap.get(this.ID);
            this.character = this.zombie;
        }
    }
    
    @Override
    public void write(final ByteBufferWriter byteBufferWriter) {
        super.write(byteBufferWriter);
        byteBufferWriter.putShort(this.zombieFlags);
        byteBufferWriter.putUTF(this.attackOutcome);
        byteBufferWriter.putUTF(this.attackPosition);
    }
    
    @Override
    public boolean isConsistent() {
        return super.isConsistent() && this.zombie != null;
    }
    
    @Override
    public String getDescription() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;ZZZZZZZZLjava/lang/String;)Ljava/lang/String;, super.getDescription(), this.attackPosition, (this.zombieFlags & 0x1) != 0x0, (this.zombieFlags & 0x2) != 0x0, (this.zombieFlags & 0x4) != 0x0, (this.zombieFlags & 0x8) != 0x0, (this.zombieFlags & 0x10) != 0x0, (this.zombieFlags & 0x20) != 0x0, (this.zombieFlags & 0x40) != 0x0, (this.zombieFlags & 0x80) != 0x0, this.attackOutcome);
    }
    
    @Override
    void process() {
        super.process();
        this.zombie.setVariable("AttackOutcome", this.attackOutcome);
        this.zombie.setPlayerAttackPosition(this.attackPosition);
        this.zombie.setStaggerBack((this.zombieFlags & 0x1) != 0x0);
        this.zombie.setFakeDead((this.zombieFlags & 0x2) != 0x0);
        this.zombie.setBecomeCrawler((this.zombieFlags & 0x4) != 0x0);
        this.zombie.setCrawler((this.zombieFlags & 0x8) != 0x0);
        this.zombie.setKnifeDeath((this.zombieFlags & 0x10) != 0x0);
        this.zombie.setJawStabAttach((this.zombieFlags & 0x20) != 0x0);
        this.zombie.setVariable("AttackDidDamage", (this.zombieFlags & 0x80) != 0x0);
    }
    
    protected void react(final HandWeapon handWeapon) {
        if (this.zombie.isJawStabAttach()) {
            this.zombie.setAttachedItem("JawStab", handWeapon);
        }
        if (GameServer.bServer && (this.zombieFlags & 0x40) != 0x0 && !PersistentOutfits.instance.isHatFallen(this.zombie)) {
            PersistentOutfits.instance.setFallenHat(this.zombie, true);
            if (ServerGUI.isCreated()) {
                PersistentOutfits.instance.removeFallenHat(this.zombie.getPersistentOutfitID(), this.zombie);
                ModelManager.instance.ResetNextFrame(this.zombie);
            }
        }
        this.react();
    }
    
    @Override
    IsoGameCharacter getCharacter() {
        return this.zombie;
    }
}

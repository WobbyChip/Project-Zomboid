// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network.packets.hit;

import zombie.core.raknet.UdpConnection;
import zombie.inventory.types.HandWeapon;
import zombie.core.network.ByteBufferWriter;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.GameWindow;
import java.nio.ByteBuffer;
import java.util.Collection;
import zombie.characters.skills.PerkFactory;
import zombie.characters.IsoGameCharacter;
import java.util.ArrayList;
import zombie.characters.IsoPlayer;
import zombie.network.packets.INetworkPacket;

public class Player extends Character implements INetworkPacket
{
    protected IsoPlayer player;
    protected short playerFlags;
    protected float charge;
    protected float perkAiming;
    protected float combatSpeed;
    protected String attackType;
    protected AttackVars attackVars;
    ArrayList<HitInfo> hitList;
    
    public Player() {
        this.attackVars = new AttackVars();
        this.hitList = new ArrayList<HitInfo>();
    }
    
    public void set(final IsoPlayer player, final boolean b) {
        super.set(player);
        this.player = player;
        this.playerFlags = 0;
        this.playerFlags |= (short)(player.isAimAtFloor() ? 1 : 0);
        this.playerFlags |= (short)(player.isDoShove() ? 2 : 0);
        this.playerFlags |= (short)(player.isAttackFromBehind() ? 4 : 0);
        this.playerFlags |= (short)(b ? 8 : 0);
        this.charge = player.useChargeDelta;
        this.perkAiming = (float)player.getPerkLevel(PerkFactory.Perks.Aiming);
        this.combatSpeed = player.getVariableFloat("CombatSpeed", 1.0f);
        this.attackType = player.getAttackType();
        this.attackVars.copy(player.attackVars);
        this.hitList.clear();
        this.hitList.addAll(player.hitList);
    }
    
    @Override
    public void parse(final ByteBuffer byteBuffer) {
        super.parse(byteBuffer);
        this.playerFlags = byteBuffer.getShort();
        this.charge = byteBuffer.getFloat();
        this.perkAiming = byteBuffer.getFloat();
        this.combatSpeed = byteBuffer.getFloat();
        this.attackType = GameWindow.ReadString(byteBuffer);
        if (GameServer.bServer) {
            this.player = GameServer.IDToPlayerMap.get(this.ID);
            this.character = this.player;
        }
        else if (GameClient.bClient) {
            this.player = GameClient.IDToPlayerMap.get(this.ID);
            this.character = this.player;
        }
        this.attackVars.parse(byteBuffer);
        for (byte value = byteBuffer.get(), b = 0; b < value; ++b) {
            final HitInfo e = new HitInfo();
            e.parse(byteBuffer);
            this.hitList.add(e);
        }
    }
    
    @Override
    public void write(final ByteBufferWriter byteBufferWriter) {
        super.write(byteBufferWriter);
        byteBufferWriter.putShort(this.playerFlags);
        byteBufferWriter.putFloat(this.charge);
        byteBufferWriter.putFloat(this.perkAiming);
        byteBufferWriter.putFloat(this.combatSpeed);
        byteBufferWriter.putUTF(this.attackType);
        this.attackVars.write(byteBufferWriter);
        final byte b = (byte)this.hitList.size();
        byteBufferWriter.putByte(b);
        for (byte index = 0; index < b; ++index) {
            this.hitList.get(index).write(byteBufferWriter);
        }
    }
    
    @Override
    public boolean isConsistent() {
        return super.isConsistent() && this.player != null;
    }
    
    @Override
    public String getDescription() {
        String s = "";
        for (byte b = (byte)Math.min(100, this.hitList.size()), index = 0; index < b; ++index) {
            s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, this.hitList.get(index).getDescription());
        }
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;FFFLjava/lang/String;ZZZZLjava/io/Serializable;Ljava/lang/String;Ljava/lang/String;I)Ljava/lang/String;, super.getDescription(), (this.player == null) ? "?" : invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.player.getUsername()), this.charge, this.perkAiming, this.combatSpeed, this.attackType, (this.playerFlags & 0x1) != 0x0, (this.playerFlags & 0x2) != 0x0, (this.playerFlags & 0x4) != 0x0, (this.playerFlags & 0x8) != 0x0, (this.player == null) ? "?" : Float.valueOf(this.player.getBodyDamage().getHealth()), this.attackVars.getDescription(), s, this.hitList.size());
    }
    
    @Override
    void process() {
        super.process();
        this.player.useChargeDelta = this.charge;
        this.player.setVariable("recoilVarX", this.perkAiming / 10.0f);
        this.player.setAttackType(this.attackType);
        this.player.setVariable("CombatSpeed", this.combatSpeed);
        this.player.setVariable("AimFloorAnim", (this.playerFlags & 0x1) != 0x0);
        this.player.setAimAtFloor((this.playerFlags & 0x1) != 0x0);
        this.player.setDoShove((this.playerFlags & 0x2) != 0x0);
        this.player.setAttackFromBehind((this.playerFlags & 0x4) != 0x0);
        this.player.setCriticalHit((this.playerFlags & 0x8) != 0x0);
    }
    
    void attack(final HandWeapon handWeapon) {
        this.player.attackStarted = false;
        this.player.attackVars.copy(this.attackVars);
        this.player.hitList.clear();
        this.player.hitList.addAll(this.hitList);
        this.player.pressedAttack(false);
        if (this.player.isAttackStarted() && handWeapon.isRanged() && !this.player.isDoShove()) {
            this.player.startMuzzleFlash();
        }
        if (handWeapon.getPhysicsObject() != null) {
            this.player.Throw(handWeapon);
        }
    }
    
    @Override
    IsoGameCharacter getCharacter() {
        return this.player;
    }
    
    boolean isRelevant(final UdpConnection udpConnection) {
        return udpConnection.RelevantTo(this.positionX, this.positionY);
    }
}

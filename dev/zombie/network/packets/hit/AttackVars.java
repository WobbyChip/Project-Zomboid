// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network.packets.hit;

import zombie.iso.IsoMovingObject;
import java.util.Iterator;
import zombie.core.network.ByteBufferWriter;
import java.nio.ByteBuffer;
import zombie.characters.IsoLivingCharacter;
import zombie.inventory.types.HandWeapon;
import java.util.ArrayList;
import zombie.network.packets.INetworkPacket;

public class AttackVars implements INetworkPacket
{
    private boolean isBareHeadsWeapon;
    public MovingObject targetOnGround;
    public boolean bAimAtFloor;
    public boolean bCloseKill;
    public boolean bDoShove;
    public float useChargeDelta;
    public int recoilDelay;
    public final ArrayList<HitInfo> targetsStanding;
    public final ArrayList<HitInfo> targetsProne;
    
    public AttackVars() {
        this.targetOnGround = new MovingObject();
        this.targetsStanding = new ArrayList<HitInfo>();
        this.targetsProne = new ArrayList<HitInfo>();
    }
    
    public void setWeapon(final HandWeapon handWeapon) {
        this.isBareHeadsWeapon = "BareHands".equals(handWeapon.getType());
    }
    
    public HandWeapon getWeapon(final IsoLivingCharacter isoLivingCharacter) {
        if (this.isBareHeadsWeapon || isoLivingCharacter.getUseHandWeapon() == null) {
            return isoLivingCharacter.bareHands;
        }
        return isoLivingCharacter.getUseHandWeapon();
    }
    
    @Override
    public void parse(final ByteBuffer byteBuffer) {
        final byte value = byteBuffer.get();
        this.isBareHeadsWeapon = ((value & 0x1) != 0x0);
        this.bAimAtFloor = ((value & 0x2) != 0x0);
        this.bCloseKill = ((value & 0x4) != 0x0);
        this.bDoShove = ((value & 0x8) != 0x0);
        this.targetOnGround.parse(byteBuffer);
        this.useChargeDelta = byteBuffer.getFloat();
        this.recoilDelay = byteBuffer.getInt();
        final byte value2 = byteBuffer.get();
        this.targetsStanding.clear();
        for (byte b = 0; b < value2; ++b) {
            final HitInfo e = new HitInfo();
            e.parse(byteBuffer);
            this.targetsStanding.add(e);
        }
        final byte value3 = byteBuffer.get();
        this.targetsProne.clear();
        for (byte b2 = 0; b2 < value3; ++b2) {
            final HitInfo e2 = new HitInfo();
            e2.parse(byteBuffer);
            this.targetsProne.add(e2);
        }
    }
    
    @Override
    public void write(final ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putByte((byte)((byte)((byte)((byte)(0x0 | (byte)(this.isBareHeadsWeapon ? 1 : 0)) | (byte)(this.bAimAtFloor ? 2 : 0)) | (byte)(this.bCloseKill ? 4 : 0)) | (byte)(this.bDoShove ? 8 : 0)));
        this.targetOnGround.write(byteBufferWriter);
        byteBufferWriter.putFloat(this.useChargeDelta);
        byteBufferWriter.putInt(this.recoilDelay);
        final byte b = (byte)Math.min(100, this.targetsStanding.size());
        byteBufferWriter.putByte(b);
        for (byte index = 0; index < b; ++index) {
            this.targetsStanding.get(index).write(byteBufferWriter);
        }
        final byte b2 = (byte)Math.min(100, this.targetsProne.size());
        byteBufferWriter.putByte(b2);
        for (byte index2 = 0; index2 < b2; ++index2) {
            this.targetsProne.get(index2).write(byteBufferWriter);
        }
    }
    
    @Override
    public int getPacketSizeBytes() {
        int n = 11 + this.targetOnGround.getPacketSizeBytes();
        for (byte b = (byte)Math.min(100, this.targetsStanding.size()), index = 0; index < b; ++index) {
            n += this.targetsStanding.get(index).getPacketSizeBytes();
        }
        for (byte b2 = (byte)Math.min(100, this.targetsProne.size()), index2 = 0; index2 < b2; ++index2) {
            n += this.targetsProne.get(index2).getPacketSizeBytes();
        }
        return n;
    }
    
    @Override
    public String getDescription() {
        String s = "";
        for (byte b = (byte)Math.min(100, this.targetsStanding.size()), index = 0; index < b; ++index) {
            s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, this.targetsStanding.get(index).getDescription());
        }
        String s2 = "";
        for (byte b2 = (byte)Math.min(100, this.targetsProne.size()), index2 = 0; index2 < b2; ++index2) {
            s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, this.targetsProne.get(index2).getDescription());
        }
        return invokedynamic(makeConcatWithConstants:(ZZZZFILjava/lang/String;Ljava/lang/String;ILjava/lang/String;I)Ljava/lang/String;, this.isBareHeadsWeapon, this.bAimAtFloor, this.bCloseKill, this.bDoShove, this.useChargeDelta, this.recoilDelay, this.targetOnGround.getDescription(), s, this.targetsStanding.size(), s2, this.targetsProne.size());
    }
    
    public void copy(final AttackVars attackVars) {
        this.isBareHeadsWeapon = attackVars.isBareHeadsWeapon;
        this.targetOnGround = attackVars.targetOnGround;
        this.bAimAtFloor = attackVars.bAimAtFloor;
        this.bCloseKill = attackVars.bCloseKill;
        this.bDoShove = attackVars.bDoShove;
        this.useChargeDelta = attackVars.useChargeDelta;
        this.recoilDelay = attackVars.recoilDelay;
        this.targetsStanding.clear();
        final Iterator<HitInfo> iterator = attackVars.targetsStanding.iterator();
        while (iterator.hasNext()) {
            this.targetsStanding.add(iterator.next());
        }
        this.targetsProne.clear();
        final Iterator<HitInfo> iterator2 = attackVars.targetsProne.iterator();
        while (iterator2.hasNext()) {
            this.targetsProne.add(iterator2.next());
        }
    }
    
    public void clear() {
        this.targetOnGround.setMovingObject(null);
        this.targetsStanding.clear();
        this.targetsProne.clear();
    }
}

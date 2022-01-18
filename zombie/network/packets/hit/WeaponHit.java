// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network.packets.hit;

import zombie.characters.IsoPlayer;
import zombie.characters.IsoLivingCharacter;
import zombie.characterTextures.BloodBodyPartType;
import zombie.iso.IsoMovingObject;
import zombie.ai.states.SwipeStatePlayer;
import zombie.Lua.LuaEventManager;
import zombie.inventory.types.HandWeapon;
import zombie.characters.IsoGameCharacter;
import zombie.core.network.ByteBufferWriter;
import java.nio.ByteBuffer;
import zombie.network.packets.INetworkPacket;

public class WeaponHit extends Hit implements INetworkPacket
{
    protected float range;
    protected boolean hitHead;
    
    public void set(final boolean b, final float n, final float range, final float n2, final float n3, final float n4, final boolean hitHead) {
        super.set(b, n, n2, n3, n4);
        this.range = range;
        this.hitHead = hitHead;
    }
    
    @Override
    public void parse(final ByteBuffer byteBuffer) {
        super.parse(byteBuffer);
        this.range = byteBuffer.getFloat();
        this.hitHead = (byteBuffer.get() != 0);
    }
    
    @Override
    public void write(final ByteBufferWriter byteBufferWriter) {
        super.write(byteBufferWriter);
        byteBufferWriter.putFloat(this.range);
        byteBufferWriter.putBoolean(this.hitHead);
    }
    
    @Override
    public String getDescription() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;FZ)Ljava/lang/String;, super.getDescription(), this.range, this.hitHead);
    }
    
    void process(final IsoGameCharacter isoGameCharacter, final IsoGameCharacter isoGameCharacter2, final HandWeapon handWeapon) {
        isoGameCharacter2.Hit(handWeapon, isoGameCharacter, this.damage, this.ignore, this.range, true);
        super.process(isoGameCharacter, isoGameCharacter2);
        LuaEventManager.triggerEvent("OnWeaponHitXp", isoGameCharacter, handWeapon, isoGameCharacter2, this.damage);
        if (isoGameCharacter.isAimAtFloor() && !handWeapon.isRanged() && isoGameCharacter.isNPC()) {
            SwipeStatePlayer.splash(isoGameCharacter2, handWeapon, isoGameCharacter);
        }
        if (this.hitHead) {
            SwipeStatePlayer.splash(isoGameCharacter2, handWeapon, isoGameCharacter);
            SwipeStatePlayer.splash(isoGameCharacter2, handWeapon, isoGameCharacter);
            isoGameCharacter2.addBlood(BloodBodyPartType.Head, true, true, true);
            isoGameCharacter2.addBlood(BloodBodyPartType.Torso_Upper, true, false, false);
            isoGameCharacter2.addBlood(BloodBodyPartType.UpperArm_L, true, false, false);
            isoGameCharacter2.addBlood(BloodBodyPartType.UpperArm_R, true, false, false);
        }
        if ((!((IsoLivingCharacter)isoGameCharacter).bDoShove || isoGameCharacter.isAimAtFloor()) && isoGameCharacter.DistToSquared(isoGameCharacter2) < 2.0f && Math.abs(isoGameCharacter.z - isoGameCharacter2.z) < 0.5f) {
            isoGameCharacter.addBlood(null, false, false, false);
        }
        if (!isoGameCharacter2.isDead() && !(isoGameCharacter2 instanceof IsoPlayer) && (!((IsoLivingCharacter)isoGameCharacter).bDoShove || isoGameCharacter.isAimAtFloor())) {
            SwipeStatePlayer.splash(isoGameCharacter2, handWeapon, isoGameCharacter);
        }
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters;

import zombie.inventory.InventoryItem;
import zombie.ai.State;
import zombie.WorldSoundManager;
import zombie.iso.IsoObject;
import zombie.ui.UIManager;
import zombie.iso.Vector2;
import zombie.iso.IsoMovingObject;
import zombie.characters.skills.PerkFactory;
import zombie.characters.Moodles.MoodleType;
import zombie.Lua.LuaHookManager;
import zombie.ai.states.SwipeStatePlayer;
import zombie.network.packets.hit.AttackVars;
import zombie.inventory.InventoryItemFactory;
import zombie.iso.IsoCell;
import zombie.inventory.types.HandWeapon;

public class IsoLivingCharacter extends IsoGameCharacter
{
    public float useChargeDelta;
    public final HandWeapon bareHands;
    public boolean bDoShove;
    public boolean bCollidedWithPushable;
    public IsoGameCharacter targetOnGround;
    
    public IsoLivingCharacter(final IsoCell isoCell, final float n, final float n2, final float n3) {
        super(isoCell, n, n2, n3);
        this.useChargeDelta = 0.0f;
        this.bDoShove = false;
        this.bCollidedWithPushable = false;
        this.bareHands = (HandWeapon)InventoryItemFactory.CreateItem("Base.BareHands");
    }
    
    public boolean isCollidedWithPushableThisFrame() {
        return this.bCollidedWithPushable;
    }
    
    public boolean AttemptAttack(final float f) {
        HandWeapon bareHands;
        if (this.leftHandItem instanceof HandWeapon) {
            bareHands = (HandWeapon)this.leftHandItem;
        }
        else {
            bareHands = this.bareHands;
        }
        if (bareHands != this.bareHands && this instanceof IsoPlayer) {
            final AttackVars attackVars = new AttackVars();
            SwipeStatePlayer.instance().CalcAttackVars(this, attackVars);
            this.setDoShove(attackVars.bDoShove);
            if (LuaHookManager.TriggerHook("Attack", this, f, bareHands)) {
                return false;
            }
        }
        return this.DoAttack(f);
    }
    
    public boolean DoAttack(final float n) {
        if (this.isDead()) {
            return false;
        }
        if (this.leftHandItem != null) {
            final InventoryItem leftHandItem = this.leftHandItem;
            if (leftHandItem instanceof HandWeapon) {
                this.useHandWeapon = (HandWeapon)leftHandItem;
                if (this.useHandWeapon.getCondition() <= 0) {
                    return false;
                }
                final int moodleLevel = this.Moodles.getMoodleLevel(MoodleType.Endurance);
                if (this.useHandWeapon.isCantAttackWithLowestEndurance() && moodleLevel == 4) {
                    return false;
                }
                int n2 = 0;
                if (this.useHandWeapon.isRanged()) {
                    this.setRecoilDelay((float)(this.useHandWeapon.getRecoilDelay() * (1.0f - this.getPerkLevel(PerkFactory.Perks.Aiming) / 30.0f)).intValue());
                }
                if (this instanceof IsoSurvivor && this.useHandWeapon.isRanged() && n2 < this.useHandWeapon.getMaxHitCount()) {
                    for (int i = 0; i < this.getCell().getObjectList().size(); ++i) {
                        final IsoMovingObject isoMovingObject = this.getCell().getObjectList().get(i);
                        if (isoMovingObject != this) {
                            if (isoMovingObject.isShootable() && this.IsAttackRange(isoMovingObject.getX(), isoMovingObject.getY(), isoMovingObject.getZ()) && 1.0f > 0.0f) {
                                final Vector2 vector2 = new Vector2(this.getX(), this.getY());
                                final Vector2 vector4;
                                final Vector2 vector3 = vector4 = new Vector2(isoMovingObject.getX(), isoMovingObject.getY());
                                vector4.x -= vector2.x;
                                final Vector2 vector5 = vector3;
                                vector5.y -= vector2.y;
                                boolean b = false;
                                if (vector3.x == 0.0f && vector3.y == 0.0f) {
                                    b = true;
                                }
                                final Vector2 forwardDirection = this.getForwardDirection();
                                this.DirectionFromVector(forwardDirection);
                                vector3.normalize();
                                float dot = vector3.dot(forwardDirection);
                                if (b) {
                                    dot = 1.0f;
                                }
                                if (dot > 1.0f) {
                                    dot = 1.0f;
                                }
                                if (dot < -1.0f) {
                                    dot = -1.0f;
                                }
                                if (dot >= this.useHandWeapon.getMinAngle() && dot <= this.useHandWeapon.getMaxAngle()) {
                                    ++n2;
                                }
                                if (n2 >= this.useHandWeapon.getMaxHitCount()) {
                                    break;
                                }
                            }
                        }
                    }
                }
                if (UIManager.getPicked() != null) {
                    this.attackTargetSquare = UIManager.getPicked().square;
                    if (UIManager.getPicked().tile instanceof IsoMovingObject) {
                        this.attackTargetSquare = ((IsoMovingObject)UIManager.getPicked().tile).getCurrentSquare();
                    }
                }
                if (this.useHandWeapon.getAmmoType() != null && !this.inventory.contains(this.useHandWeapon.getAmmoType())) {
                    return false;
                }
                if (this.useHandWeapon.getOtherHandRequire() != null && (this.rightHandItem == null || !this.rightHandItem.getType().equals(this.useHandWeapon.getOtherHandRequire()))) {
                    return false;
                }
                if (!this.useHandWeapon.isRanged()) {
                    this.getEmitter().playSound(this.useHandWeapon.getSwingSound(), this);
                    WorldSoundManager.instance.addSound(this, (int)this.getX(), (int)this.getY(), (int)this.getZ(), this.useHandWeapon.getSoundRadius(), this.useHandWeapon.getSoundVolume());
                }
                this.AttackWasSuperAttack = this.superAttack;
                this.changeState(SwipeStatePlayer.instance());
                if (this.useHandWeapon.getAmmoType() != null) {
                    if (this instanceof IsoPlayer) {
                        IsoPlayer.getInstance().inventory.RemoveOneOf(this.useHandWeapon.getAmmoType());
                    }
                    else {
                        this.inventory.RemoveOneOf(this.useHandWeapon.getAmmoType());
                    }
                }
                if (this.useHandWeapon.isUseSelf() && this.leftHandItem != null) {
                    this.leftHandItem.Use();
                }
                if (this.useHandWeapon.isOtherHandUse() && this.rightHandItem != null) {
                    this.rightHandItem.Use();
                }
                return true;
            }
        }
        return false;
    }
    
    public boolean isDoShove() {
        return this.bDoShove;
    }
    
    public void setDoShove(final boolean bDoShove) {
        this.bDoShove = bDoShove;
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import fmod.fmod.FMODManager;
import java.util.Iterator;
import zombie.iso.IsoCell;
import zombie.iso.objects.IsoTree;
import zombie.iso.IsoDirections;
import zombie.iso.objects.interfaces.Thumpable;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoZombieGiblets;
import zombie.vehicles.VehicleWindow;
import zombie.vehicles.VehiclePart;
import zombie.characters.Stats;
import zombie.characters.HitReactionNetworkAI;
import zombie.characterTextures.BloodBodyPartType;
import zombie.ui.MoodlesUI;
import zombie.audio.parameters.ParameterMeleeHitSurface;
import zombie.util.StringUtils;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.network.GameServer;
import zombie.core.skinnedmodel.animation.AnimationPlayer;
import zombie.scripting.objects.VehicleScript;
import zombie.characters.Moodles.MoodleType;
import zombie.characters.BodyDamage.BodyPart;
import org.joml.Vector3f;
import java.util.Collection;
import java.util.Collections;
import zombie.iso.IsoGridSquare;
import java.awt.geom.Line2D;
import zombie.iso.objects.IsoWindow;
import zombie.iso.IsoObject;
import zombie.vehicles.BaseVehicle;
import zombie.characters.Faction;
import zombie.iso.areas.NonPvpZone;
import zombie.network.ServerOptions;
import zombie.iso.LosUtil;
import zombie.iso.IsoUtils;
import zombie.vehicles.PolygonalMap2;
import zombie.characters.IsoZombie;
import java.util.Comparator;
import java.util.List;
import org.lwjglx.input.Keyboard;
import zombie.inventory.types.WeaponType;
import zombie.core.Core;
import zombie.util.Type;
import zombie.debug.DebugOptions;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.inventory.types.Clothing;
import zombie.core.math.PZMath;
import zombie.SandboxOptions;
import zombie.characters.skills.PerkFactory;
import zombie.core.Rand;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.core.skinnedmodel.model.Model;
import java.util.HashMap;
import se.krka.kahlua.vm.KahluaTable;
import zombie.iso.IsoWorld;
import zombie.Lua.LuaHookManager;
import zombie.Lua.LuaEventManager;
import zombie.GameTime;
import zombie.network.GameClient;
import zombie.ui.UIManager;
import zombie.network.packets.hit.AttackVars;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoLivingCharacter;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.characters.IsoGameCharacter;
import zombie.inventory.types.HandWeapon;
import org.joml.Vector4f;
import zombie.iso.IsoMovingObject;
import zombie.iso.Vector3;
import zombie.popman.ObjectPool;
import zombie.iso.Vector2;
import zombie.network.packets.hit.HitInfo;
import java.util.ArrayList;
import zombie.ai.State;

public final class SwipeStatePlayer extends State
{
    private static final SwipeStatePlayer _instance;
    static final Integer PARAM_LOWER_CONDITION;
    static final Integer PARAM_ATTACKED;
    private static final ArrayList<HitInfo> HitList2;
    private static final Vector2 tempVector2_1;
    private static final Vector2 tempVector2_2;
    private final ArrayList<Float> dotList;
    private boolean bHitOnlyTree;
    public final ObjectPool<HitInfo> hitInfoPool;
    private static final CustomComparator Comparator;
    static final Vector3 tempVector3_1;
    static final Vector3 tempVector3_2;
    static final Vector3 tempVectorBonePos;
    static final ArrayList<IsoMovingObject> movingStatic;
    private final Vector4f tempVector4f;
    private final WindowVisitor windowVisitor;
    
    public SwipeStatePlayer() {
        this.dotList = new ArrayList<Float>();
        this.hitInfoPool = new ObjectPool<HitInfo>(HitInfo::new);
        this.tempVector4f = new Vector4f();
        this.windowVisitor = new WindowVisitor();
    }
    
    public static SwipeStatePlayer instance() {
        return SwipeStatePlayer._instance;
    }
    
    public static void WeaponLowerCondition(final HandWeapon handWeapon, final IsoGameCharacter isoGameCharacter) {
        if (handWeapon.getUses() > 1) {
            handWeapon.Use();
            final InventoryItem createItem = InventoryItemFactory.CreateItem(handWeapon.getFullType());
            createItem.setCondition(handWeapon.getCondition() - 1);
            handWeapon.getContainer().AddItem(createItem);
            isoGameCharacter.setPrimaryHandItem(createItem);
        }
        else {
            handWeapon.setCondition(handWeapon.getCondition() - 1);
        }
    }
    
    private static HandWeapon GetWeapon(final IsoGameCharacter isoGameCharacter) {
        HandWeapon handWeapon = isoGameCharacter.getUseHandWeapon();
        if (((IsoLivingCharacter)isoGameCharacter).bDoShove || isoGameCharacter.isForceShove()) {
            handWeapon = ((IsoLivingCharacter)isoGameCharacter).bareHands;
        }
        return handWeapon;
    }
    
    private void doAttack(final IsoPlayer isoPlayer, float useChargeDelta, final boolean forceShove, final String clickSound, final AttackVars attackVars) {
        isoPlayer.setForceShove(forceShove);
        isoPlayer.setClickSound(clickSound);
        if (forceShove) {
            useChargeDelta *= 2.0f;
        }
        if (useChargeDelta > 90.0f) {
            useChargeDelta = 90.0f;
        }
        useChargeDelta /= 25.0f;
        isoPlayer.useChargeDelta = useChargeDelta;
        InventoryItem inventoryItem = isoPlayer.getPrimaryHandItem();
        if (inventoryItem == null || !(inventoryItem instanceof HandWeapon) || forceShove || attackVars.bDoShove) {
            inventoryItem = isoPlayer.bareHands;
        }
        if (inventoryItem instanceof HandWeapon) {
            isoPlayer.setUseHandWeapon((HandWeapon)inventoryItem);
            if (isoPlayer.PlayerIndex == 0 && isoPlayer.JoypadBind == -1 && UIManager.getPicked() != null && (!GameClient.bClient || isoPlayer.isLocalPlayer())) {
                if (UIManager.getPicked().tile instanceof IsoMovingObject) {
                    isoPlayer.setAttackTargetSquare(((IsoMovingObject)UIManager.getPicked().tile).getCurrentSquare());
                }
                else {
                    isoPlayer.setAttackTargetSquare(UIManager.getPicked().square);
                }
            }
            isoPlayer.setRecoilDelay((float)attackVars.recoilDelay);
            if (forceShove) {
                isoPlayer.setRecoilDelay(10.0f);
            }
        }
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        if ("HitReaction".equals(isoGameCharacter.getHitReaction())) {
            isoGameCharacter.clearVariable("HitReaction");
        }
        UIManager.speedControls.SetCurrentGameSpeed(1);
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        stateMachineParams.put(SwipeStatePlayer.PARAM_LOWER_CONDITION, Boolean.FALSE);
        stateMachineParams.put(SwipeStatePlayer.PARAM_ATTACKED, Boolean.FALSE);
        if (!(isoGameCharacter instanceof IsoPlayer) || !((IsoPlayer)isoGameCharacter).bRemote) {
            isoGameCharacter.updateRecoilVar();
        }
        if ("Auto".equals(isoGameCharacter.getVariableString("FireMode"))) {
            isoGameCharacter.setVariable("autoShootSpeed", 4.0f * GameTime.getAnimSpeedFix());
            isoGameCharacter.setVariable("autoShootVarY", 0.0f);
            if (System.currentTimeMillis() - isoGameCharacter.lastAutomaticShoot < 600L) {
                ++isoGameCharacter.shootInARow;
                isoGameCharacter.setVariable("autoShootVarX", Math.max(0.0f, 1.0f - isoGameCharacter.shootInARow / 20.0f));
                isoGameCharacter.setVariable("autoShootSpeed", (4.0f - isoGameCharacter.shootInARow / 10.0f) * GameTime.getAnimSpeedFix());
            }
            else {
                isoGameCharacter.setVariable("autoShootVarX", 1.0f);
                isoGameCharacter.shootInARow = 0;
            }
            isoGameCharacter.lastAutomaticShoot = System.currentTimeMillis();
        }
        final IsoPlayer isoPlayer = (IsoPlayer)isoGameCharacter;
        isoGameCharacter.setVariable("ShotDone", false);
        isoGameCharacter.setVariable("ShoveAnim", false);
        this.CalcAttackVars((IsoLivingCharacter)isoGameCharacter, isoPlayer.attackVars);
        this.doAttack(isoPlayer, 2.0f, isoGameCharacter.isForceShove(), isoGameCharacter.getClickSound(), isoPlayer.attackVars);
        final HandWeapon useHandWeapon = isoGameCharacter.getUseHandWeapon();
        if (!GameClient.bClient || isoPlayer.isLocalPlayer()) {
            isoGameCharacter.setVariable("AimFloorAnim", isoPlayer.attackVars.bAimAtFloor);
        }
        LuaEventManager.triggerEvent("OnWeaponSwing", isoGameCharacter, useHandWeapon);
        if (LuaHookManager.TriggerHook("WeaponSwing", isoGameCharacter, useHandWeapon)) {
            isoGameCharacter.getStateMachine().revertToPreviousState(this);
        }
        isoGameCharacter.StopAllActionQueue();
        if (((IsoPlayer)isoGameCharacter).isLocalPlayer()) {
            IsoWorld.instance.CurrentCell.setDrag(null, ((IsoPlayer)isoGameCharacter).PlayerIndex);
        }
        final HandWeapon weapon = isoPlayer.attackVars.getWeapon(isoPlayer);
        isoPlayer.setAimAtFloor(isoPlayer.attackVars.bAimAtFloor);
        final boolean bDoShove = isoPlayer.bDoShove;
        isoPlayer.setDoShove(isoPlayer.attackVars.bDoShove);
        isoPlayer.useChargeDelta = isoPlayer.attackVars.useChargeDelta;
        isoPlayer.targetOnGround = (IsoGameCharacter)isoPlayer.attackVars.targetOnGround.getMovingObject();
        if (isoPlayer.bDoShove || bDoShove || isoPlayer.getClickSound() != null || weapon.getPhysicsObject() != null || !weapon.isRanged()) {}
        if (GameClient.bClient && isoGameCharacter == IsoPlayer.getInstance()) {
            GameClient.instance.sendPlayer((IsoPlayer)isoGameCharacter);
        }
        if (!isoPlayer.bDoShove && !bDoShove && !weapon.isRanged() && isoPlayer.isLocalPlayer()) {
            isoGameCharacter.playSound(weapon.getSwingSound());
        }
        else if ((isoPlayer.bDoShove || bDoShove) && isoPlayer.isLocalPlayer()) {
            if (isoPlayer.targetOnGround != null) {
                isoGameCharacter.playSound("AttackStomp");
            }
            else {
                isoGameCharacter.playSound("AttackShove");
            }
        }
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.StopAllActionQueue();
    }
    
    private int DoSwingCollisionBoneCheck(final IsoGameCharacter isoGameCharacter, final HandWeapon handWeapon, final IsoGameCharacter isoGameCharacter2, final int n, final float n2) {
        SwipeStatePlayer.movingStatic.clear();
        float n3 = handWeapon.WeaponLength + 0.5f;
        if (isoGameCharacter.isAimAtFloor() && ((IsoLivingCharacter)isoGameCharacter).bDoShove) {
            n3 = 0.3f;
        }
        Model.BoneToWorldCoords(isoGameCharacter2, n, SwipeStatePlayer.tempVectorBonePos);
        for (int i = 1; i <= 10; ++i) {
            final float n4 = i / 10.0f;
            SwipeStatePlayer.tempVector3_1.x = isoGameCharacter.x;
            SwipeStatePlayer.tempVector3_1.y = isoGameCharacter.y;
            SwipeStatePlayer.tempVector3_1.z = isoGameCharacter.z;
            final Vector3 tempVector3_1 = SwipeStatePlayer.tempVector3_1;
            tempVector3_1.x += isoGameCharacter.getForwardDirection().x * n3 * n4;
            final Vector3 tempVector3_2 = SwipeStatePlayer.tempVector3_1;
            tempVector3_2.y += isoGameCharacter.getForwardDirection().y * n3 * n4;
            SwipeStatePlayer.tempVector3_1.x = SwipeStatePlayer.tempVectorBonePos.x - SwipeStatePlayer.tempVector3_1.x;
            SwipeStatePlayer.tempVector3_1.y = SwipeStatePlayer.tempVectorBonePos.y - SwipeStatePlayer.tempVector3_1.y;
            SwipeStatePlayer.tempVector3_1.z = 0.0f;
            if (SwipeStatePlayer.tempVector3_1.getLength() < n2) {
                return n;
            }
        }
        return -1;
    }
    
    @Override
    public void animEvent(final IsoGameCharacter isoGameCharacter, final AnimEvent animEvent) {
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        if ((animEvent.m_EventName.equalsIgnoreCase("ActiveAnimFinishing") || animEvent.m_EventName.equalsIgnoreCase("NonLoopedAnimFadeOut")) && stateMachineParams.get(SwipeStatePlayer.PARAM_LOWER_CONDITION) == Boolean.TRUE && !isoGameCharacter.isRangedWeaponEmpty()) {
            stateMachineParams.put(SwipeStatePlayer.PARAM_LOWER_CONDITION, Boolean.FALSE);
            final HandWeapon getWeapon = GetWeapon(isoGameCharacter);
            int conditionLowerChance = getWeapon.getConditionLowerChance();
            if (isoGameCharacter instanceof IsoPlayer && "charge".equals(((IsoPlayer)isoGameCharacter).getAttackType())) {
                conditionLowerChance /= (int)1.5;
            }
            if (Rand.Next(conditionLowerChance + isoGameCharacter.getMaintenanceMod() * 2) == 0) {
                WeaponLowerCondition(getWeapon, isoGameCharacter);
            }
            else if (Rand.NextBool(2) && !getWeapon.isRanged() && !getWeapon.getName().contains("Bare Hands")) {
                if (getWeapon.isTwoHandWeapon() && (isoGameCharacter.getPrimaryHandItem() != getWeapon || isoGameCharacter.getSecondaryHandItem() != getWeapon) && Rand.NextBool(3)) {
                    return;
                }
                isoGameCharacter.getXp().AddXP(PerkFactory.Perks.Maintenance, 1.0f);
            }
        }
        if (animEvent.m_EventName.equalsIgnoreCase("AttackAnim")) {
            isoGameCharacter.setAttackAnim(Boolean.parseBoolean(animEvent.m_ParameterValue));
        }
        if (animEvent.m_EventName.equalsIgnoreCase("BlockTurn")) {
            isoGameCharacter.setIgnoreMovement(Boolean.parseBoolean(animEvent.m_ParameterValue));
        }
        if (animEvent.m_EventName.equalsIgnoreCase("ShoveAnim")) {
            isoGameCharacter.setVariable("ShoveAnim", Boolean.parseBoolean(animEvent.m_ParameterValue));
        }
        if (animEvent.m_EventName.equalsIgnoreCase("StompAnim")) {
            isoGameCharacter.setVariable("StompAnim", Boolean.parseBoolean(animEvent.m_ParameterValue));
        }
        final HandWeapon getWeapon2 = GetWeapon(isoGameCharacter);
        if (animEvent.m_EventName.equalsIgnoreCase("AttackCollisionCheck") && stateMachineParams.get(SwipeStatePlayer.PARAM_ATTACKED) == Boolean.FALSE && isoGameCharacter instanceof IsoPlayer && ((IsoPlayer)isoGameCharacter).isLocalPlayer()) {
            this.ConnectSwing(isoGameCharacter, getWeapon2);
        }
        if (animEvent.m_EventName.equalsIgnoreCase("BlockMovement") && SandboxOptions.instance.AttackBlockMovements.getValue()) {
            isoGameCharacter.setVariable("SlowingMovement", Boolean.parseBoolean(animEvent.m_ParameterValue));
        }
        if (animEvent.m_EventName.equalsIgnoreCase("WeaponEmptyCheck") && isoGameCharacter.getClickSound() != null) {
            if (isoGameCharacter instanceof IsoPlayer && !((IsoPlayer)isoGameCharacter).isLocalPlayer()) {
                return;
            }
            isoGameCharacter.playSound(isoGameCharacter.getClickSound());
            isoGameCharacter.setRecoilDelay(10.0f);
        }
        if (animEvent.m_EventName.equalsIgnoreCase("ShotDone") && getWeapon2 != null && getWeapon2.isRackAfterShoot()) {
            isoGameCharacter.setVariable("ShotDone", true);
        }
        if (animEvent.m_EventName.equalsIgnoreCase("SetVariable") && animEvent.m_ParameterValue.startsWith("ShotDone=")) {
            isoGameCharacter.setVariable("ShotDone", isoGameCharacter.getVariableBoolean("ShotDone") && getWeapon2 != null && getWeapon2.isRackAfterShoot());
        }
        if (animEvent.m_EventName.equalsIgnoreCase("playRackSound")) {
            if (isoGameCharacter instanceof IsoPlayer && !((IsoPlayer)isoGameCharacter).isLocalPlayer()) {
                return;
            }
            isoGameCharacter.playSound(getWeapon2.getRackSound());
        }
        if (animEvent.m_EventName.equalsIgnoreCase("playClickSound")) {
            if (isoGameCharacter instanceof IsoPlayer && !((IsoPlayer)isoGameCharacter).isLocalPlayer()) {
                return;
            }
            isoGameCharacter.playSound(getWeapon2.getClickSound());
        }
        if (animEvent.m_EventName.equalsIgnoreCase("SetMeleeDelay")) {
            isoGameCharacter.setMeleeDelay(PZMath.tryParseFloat(animEvent.m_ParameterValue, 0.0f));
        }
        if (animEvent.m_EventName.equalsIgnoreCase("SitGroundStarted")) {
            isoGameCharacter.setVariable("SitGroundAnim", "Idle");
        }
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        isoGameCharacter.setSprinting(false);
        ((IsoPlayer)isoGameCharacter).setForceSprint(false);
        isoGameCharacter.setIgnoreMovement(false);
        isoGameCharacter.setVariable("ShoveAnim", false);
        isoGameCharacter.setVariable("StompAnim", false);
        isoGameCharacter.setAttackAnim(false);
        isoGameCharacter.setVariable("AimFloorAnim", false);
        ((IsoPlayer)isoGameCharacter).setBlockMovement(false);
        if (isoGameCharacter.isAimAtFloor() && ((IsoLivingCharacter)isoGameCharacter).bDoShove) {
            final Clothing clothing = (Clothing)isoGameCharacter.getWornItem("Shoes");
            final int n = 10;
            int n2;
            if (clothing == null) {
                n2 = 3;
            }
            else {
                n2 = n + clothing.getConditionLowerChance() / 2;
                if (Rand.Next(clothing.getConditionLowerChance()) == 0) {
                    clothing.setCondition(clothing.getCondition() - 1);
                }
            }
            if (Rand.Next(n2) == 0) {
                if (clothing == null) {
                    isoGameCharacter.getBodyDamage().getBodyPart(BodyPartType.Foot_R).AddDamage((float)Rand.Next(5, 10));
                    isoGameCharacter.getBodyDamage().getBodyPart(BodyPartType.Foot_R).setAdditionalPain(isoGameCharacter.getBodyDamage().getBodyPart(BodyPartType.Foot_R).getAdditionalPain() + Rand.Next(5, 10));
                }
                else {
                    isoGameCharacter.getBodyDamage().getBodyPart(BodyPartType.Foot_R).AddDamage((float)Rand.Next(1, 5));
                    isoGameCharacter.getBodyDamage().getBodyPart(BodyPartType.Foot_R).setAdditionalPain(isoGameCharacter.getBodyDamage().getBodyPart(BodyPartType.Foot_R).getAdditionalPain() + Rand.Next(1, 5));
                }
            }
        }
        final HandWeapon getWeapon = GetWeapon(isoGameCharacter);
        isoGameCharacter.clearVariable("ZombieHitReaction");
        ((IsoPlayer)isoGameCharacter).attackStarted = false;
        ((IsoPlayer)isoGameCharacter).setAttackType(null);
        ((IsoLivingCharacter)isoGameCharacter).setDoShove(false);
        isoGameCharacter.clearVariable("RackWeapon");
        isoGameCharacter.clearVariable("bShoveAiming");
        final boolean b = stateMachineParams.get(SwipeStatePlayer.PARAM_ATTACKED) == Boolean.TRUE;
        if (getWeapon != null && (getWeapon.getCondition() <= 0 || (b && getWeapon.isUseSelf()))) {
            isoGameCharacter.removeFromHands(getWeapon);
            if (DebugOptions.instance.MultiplayerAutoEquip.getValue() && getWeapon.getPhysicsObject() != null) {
                isoGameCharacter.setPrimaryHandItem(isoGameCharacter.getInventory().getItemFromType(getWeapon.getType()));
            }
            isoGameCharacter.getInventory().setDrawDirty(true);
        }
        if (isoGameCharacter.isRangedWeaponEmpty()) {
            isoGameCharacter.setRecoilDelay(10.0f);
        }
        isoGameCharacter.setRangedWeaponEmpty(false);
        isoGameCharacter.setForceShove(false);
        isoGameCharacter.setClickSound(null);
        if (b) {
            LuaEventManager.triggerEvent("OnPlayerAttackFinished", isoGameCharacter, getWeapon);
        }
        isoGameCharacter.hitList.clear();
        isoGameCharacter.attackVars.clear();
    }
    
    public void CalcAttackVars(final IsoLivingCharacter isoLivingCharacter, final AttackVars attackVars) {
        HandWeapon handWeapon = Type.tryCastTo(isoLivingCharacter.getPrimaryHandItem(), HandWeapon.class);
        if (handWeapon != null && handWeapon.getOtherHandRequire() != null) {
            final InventoryItem secondaryHandItem = isoLivingCharacter.getSecondaryHandItem();
            if (secondaryHandItem == null || !secondaryHandItem.getType().equals(handWeapon.getOtherHandRequire())) {
                handWeapon = null;
            }
        }
        if (GameClient.bClient && !isoLivingCharacter.isLocal()) {
            return;
        }
        final boolean b = isoLivingCharacter.isAttackAnim() || isoLivingCharacter.getVariableBoolean("ShoveAnim") || isoLivingCharacter.getVariableBoolean("StompAnim");
        attackVars.setWeapon((handWeapon == null) ? isoLivingCharacter.bareHands : handWeapon);
        attackVars.targetOnGround.setMovingObject(null);
        attackVars.bAimAtFloor = false;
        attackVars.bCloseKill = false;
        attackVars.bDoShove = isoLivingCharacter.bDoShove;
        if (!b) {
            isoLivingCharacter.setVariable("ShoveAimX", 0.5f);
            isoLivingCharacter.setVariable("ShoveAimY", 1.0f);
            if (attackVars.bDoShove && isoLivingCharacter.getVariableBoolean("isMoving")) {
                isoLivingCharacter.setVariable("ShoveAim", true);
            }
            else {
                isoLivingCharacter.setVariable("ShoveAim", false);
            }
        }
        attackVars.useChargeDelta = isoLivingCharacter.useChargeDelta;
        attackVars.recoilDelay = 0;
        if (attackVars.getWeapon(isoLivingCharacter) == isoLivingCharacter.bareHands || attackVars.bDoShove || isoLivingCharacter.isForceShove()) {
            attackVars.bDoShove = true;
            attackVars.bAimAtFloor = false;
            attackVars.setWeapon(isoLivingCharacter.bareHands);
        }
        this.calcValidTargets(isoLivingCharacter, attackVars.getWeapon(isoLivingCharacter), true, attackVars.targetsProne, attackVars.targetsStanding);
        HitInfo hitInfo = attackVars.targetsStanding.isEmpty() ? null : attackVars.targetsStanding.get(0);
        final HitInfo hitInfo2 = attackVars.targetsProne.isEmpty() ? null : attackVars.targetsProne.get(0);
        if (this.isProneTargetBetter(isoLivingCharacter, hitInfo, hitInfo2)) {
            hitInfo = null;
        }
        if (!b) {
            isoLivingCharacter.setAimAtFloor(false);
        }
        float distSq = Float.MAX_VALUE;
        if (hitInfo != null) {
            if (!b) {
                isoLivingCharacter.setAimAtFloor(false);
            }
            attackVars.bAimAtFloor = false;
            attackVars.targetOnGround.setMovingObject(null);
            distSq = hitInfo.distSq;
        }
        else if (hitInfo2 != null && (Core.OptionAutoProneAtk || isoLivingCharacter.bDoShove)) {
            if (!b) {
                isoLivingCharacter.setAimAtFloor(true);
            }
            attackVars.bAimAtFloor = true;
            attackVars.targetOnGround.setMovingObject(hitInfo2.getObject());
        }
        if (distSq < attackVars.getWeapon(isoLivingCharacter).getMinRange() * attackVars.getWeapon(isoLivingCharacter).getMinRange()) {
            if (hitInfo == null || !this.isWindowBetween(isoLivingCharacter, hitInfo.getObject())) {
                if (isoLivingCharacter.getStats().NumChasingZombies <= 1 && WeaponType.getWeaponType(isoLivingCharacter) == WeaponType.knife) {
                    attackVars.bCloseKill = true;
                    return;
                }
                attackVars.bDoShove = true;
                final IsoPlayer isoPlayer = Type.tryCastTo(isoLivingCharacter, IsoPlayer.class);
                if (isoPlayer != null && !isoPlayer.isAuthorizeShoveStomp()) {
                    attackVars.bDoShove = false;
                }
                attackVars.bAimAtFloor = false;
                if (isoLivingCharacter.bareHands.getSwingAnim() != null) {
                    attackVars.useChargeDelta = 3.0f;
                }
            }
        }
        final int key = Core.getInstance().getKey("ManualFloorAtk");
        final int key2 = Core.getInstance().getKey("Sprint");
        final boolean variableBoolean = isoLivingCharacter.getVariableBoolean("StartedAttackWhileSprinting");
        if (Keyboard.isKeyDown(key) && (key != key2 || !variableBoolean)) {
            attackVars.bAimAtFloor = true;
            isoLivingCharacter.setDoShove(attackVars.bDoShove = false);
        }
        if (attackVars.getWeapon(isoLivingCharacter).isRanged()) {
            attackVars.recoilDelay = (attackVars.getWeapon(isoLivingCharacter).getRecoilDelay() * (1.0f - isoLivingCharacter.getPerkLevel(PerkFactory.Perks.Aiming) / 30.0f)).intValue();
            isoLivingCharacter.setVariable("singleShootSpeed", (0.8f + isoLivingCharacter.getPerkLevel(PerkFactory.Perks.Aiming) / 10.0f) * GameTime.getAnimSpeedFix());
        }
    }
    
    public void calcValidTargets(final IsoLivingCharacter isoLivingCharacter, final HandWeapon handWeapon, final boolean b, final ArrayList<HitInfo> list, final ArrayList<HitInfo> list2) {
        this.hitInfoPool.release(list);
        this.hitInfoPool.release(list2);
        list.clear();
        list2.clear();
        final float ignoreProneZombieRange = Core.getInstance().getIgnoreProneZombieRange();
        final float max = Math.max(ignoreProneZombieRange, handWeapon.getMaxRange() * handWeapon.getRangeMod(isoLivingCharacter) + (b ? 1.0f : 0.0f));
        final ArrayList<IsoMovingObject> objectList = IsoWorld.instance.CurrentCell.getObjectList();
        for (int i = 0; i < objectList.size(); ++i) {
            final IsoMovingObject isoMovingObject = objectList.get(i);
            final HitInfo calcValidTarget = this.calcValidTarget(isoLivingCharacter, handWeapon, isoMovingObject, max);
            if (calcValidTarget != null) {
                if (isStanding(isoMovingObject)) {
                    list2.add(calcValidTarget);
                }
                else {
                    list.add(calcValidTarget);
                }
            }
        }
        if (!list.isEmpty() && this.shouldIgnoreProneZombies(isoLivingCharacter, list2, ignoreProneZombieRange)) {
            this.hitInfoPool.release(list);
            list.clear();
        }
        float minAngle = handWeapon.getMinAngle();
        final float maxAngle = handWeapon.getMaxAngle();
        if (handWeapon.isRanged()) {
            minAngle -= handWeapon.getAimingPerkMinAngleModifier() * (isoLivingCharacter.getPerkLevel(PerkFactory.Perks.Aiming) / 2.0f);
        }
        this.removeUnhittableTargets(isoLivingCharacter, handWeapon, minAngle, maxAngle, b, list2);
        this.removeUnhittableTargets(isoLivingCharacter, handWeapon, (float)(handWeapon.getMinAngle() / 1.5), maxAngle, b, list);
        list2.sort(SwipeStatePlayer.Comparator);
        list.sort(SwipeStatePlayer.Comparator);
    }
    
    private boolean shouldIgnoreProneZombies(final IsoGameCharacter isoGameCharacter, final ArrayList<HitInfo> list, final float n) {
        if (n <= 0.0f) {
            return false;
        }
        final boolean b = isoGameCharacter.isInvisible() || (isoGameCharacter instanceof IsoPlayer && ((IsoPlayer)isoGameCharacter).isGhostMode());
        for (int i = 0; i < list.size(); ++i) {
            final HitInfo hitInfo = list.get(i);
            final IsoZombie isoZombie = Type.tryCastTo(hitInfo.getObject(), IsoZombie.class);
            if (isoZombie == null || isoZombie.target != null || b) {
                if (hitInfo.distSq <= n * n) {
                    if (!PolygonalMap2.instance.lineClearCollide(isoGameCharacter.x, isoGameCharacter.y, hitInfo.getObject().x, hitInfo.getObject().y, (int)isoGameCharacter.z, isoGameCharacter, false, true)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private boolean isUnhittableTarget(final IsoGameCharacter isoGameCharacter, final HandWeapon handWeapon, final float n, final float n2, final HitInfo hitInfo, final boolean b) {
        return hitInfo.dot < n || hitInfo.dot > n2 || !isoGameCharacter.IsAttackRange(handWeapon, hitInfo.getObject(), SwipeStatePlayer.tempVectorBonePos.set(hitInfo.x, hitInfo.y, hitInfo.z), b);
    }
    
    private void removeUnhittableTargets(final IsoGameCharacter isoGameCharacter, final HandWeapon handWeapon, final float n, final float n2, final boolean b, final ArrayList<HitInfo> list) {
        for (int i = list.size() - 1; i >= 0; --i) {
            final HitInfo hitInfo = list.get(i);
            if (this.isUnhittableTarget(isoGameCharacter, handWeapon, n, n2, hitInfo, b)) {
                this.hitInfoPool.release(hitInfo);
                list.remove(i);
            }
        }
    }
    
    private boolean getNearestTargetPosAndDot(final IsoGameCharacter isoGameCharacter, final HandWeapon handWeapon, final IsoMovingObject isoMovingObject, final boolean b, final Vector4f vector4f) {
        this.getNearestTargetPosAndDot(isoGameCharacter, isoMovingObject, vector4f);
        final float w = vector4f.w;
        float minAngle = handWeapon.getMinAngle();
        final float maxAngle = handWeapon.getMaxAngle();
        if (Type.tryCastTo(isoMovingObject, IsoGameCharacter.class) != null) {
            if (isStanding(isoMovingObject)) {
                if (handWeapon.isRanged()) {
                    minAngle -= handWeapon.getAimingPerkMinAngleModifier() * (isoGameCharacter.getPerkLevel(PerkFactory.Perks.Aiming) / 2.0f);
                }
            }
            else {
                minAngle /= 1.5f;
            }
        }
        return w >= minAngle && w <= maxAngle && isoGameCharacter.IsAttackRange(handWeapon, isoMovingObject, SwipeStatePlayer.tempVectorBonePos.set(vector4f.x, vector4f.y, vector4f.z), b);
    }
    
    private void getNearestTargetPosAndDot(final IsoGameCharacter isoGameCharacter, final Vector3 vector3, final Vector2 vector4, final Vector4f vector4f) {
        vector4f.w = Math.max(PZMath.clamp(isoGameCharacter.getDotWithForwardDirection(vector3), -1.0f, 1.0f), vector4f.w);
        final float distanceToSquared = IsoUtils.DistanceToSquared(isoGameCharacter.x, isoGameCharacter.y, (float)((int)isoGameCharacter.z * 3), vector3.x, vector3.y, (float)((int)Math.max(vector3.z, 0.0f) * 3));
        if (distanceToSquared < vector4.x) {
            vector4.x = distanceToSquared;
            vector4f.set(vector3.x, vector3.y, vector3.z, vector4f.w);
        }
    }
    
    private void getNearestTargetPosAndDot(final IsoGameCharacter isoGameCharacter, final IsoMovingObject isoMovingObject, final String s, final Vector2 vector2, final Vector4f vector4f) {
        this.getNearestTargetPosAndDot(isoGameCharacter, getBoneWorldPos(isoMovingObject, s, SwipeStatePlayer.tempVectorBonePos), vector2, vector4f);
    }
    
    private void getNearestTargetPosAndDot(final IsoGameCharacter isoGameCharacter, final IsoMovingObject isoMovingObject, final Vector4f vector4f) {
        final Vector2 set = SwipeStatePlayer.tempVector2_1.set(Float.MAX_VALUE, Float.NaN);
        vector4f.w = Float.NEGATIVE_INFINITY;
        if (Type.tryCastTo(isoMovingObject, IsoGameCharacter.class) == null) {
            this.getNearestTargetPosAndDot(isoGameCharacter, isoMovingObject, null, set, vector4f);
            return;
        }
        getBoneWorldPos(isoMovingObject, "Bip01_Head", SwipeStatePlayer.tempVector3_1);
        getBoneWorldPos(isoMovingObject, "Bip01_HeadNub", SwipeStatePlayer.tempVector3_2);
        SwipeStatePlayer.tempVector3_1.addToThis(SwipeStatePlayer.tempVector3_2);
        SwipeStatePlayer.tempVector3_1.div(2.0f);
        final Vector3 tempVector3_1 = SwipeStatePlayer.tempVector3_1;
        if (isStanding(isoMovingObject)) {
            this.getNearestTargetPosAndDot(isoGameCharacter, tempVector3_1, set, vector4f);
            this.getNearestTargetPosAndDot(isoGameCharacter, isoMovingObject, "Bip01_Pelvis", set, vector4f);
            this.getNearestTargetPosAndDot(isoGameCharacter, SwipeStatePlayer.tempVectorBonePos.set(isoMovingObject.getX(), isoMovingObject.getY(), isoMovingObject.getZ()), set, vector4f);
        }
        else {
            this.getNearestTargetPosAndDot(isoGameCharacter, tempVector3_1, set, vector4f);
            this.getNearestTargetPosAndDot(isoGameCharacter, isoMovingObject, "Bip01_Pelvis", set, vector4f);
            this.getNearestTargetPosAndDot(isoGameCharacter, isoMovingObject, "Bip01_DressFrontNub", set, vector4f);
        }
    }
    
    private HitInfo calcValidTarget(final IsoLivingCharacter isoLivingCharacter, final HandWeapon handWeapon, final IsoMovingObject isoMovingObject, final float n) {
        if (isoMovingObject == isoLivingCharacter) {
            return null;
        }
        final IsoGameCharacter isoGameCharacter = Type.tryCastTo(isoMovingObject, IsoGameCharacter.class);
        if (isoGameCharacter == null) {
            return null;
        }
        if (isoGameCharacter.isGodMod()) {
            return null;
        }
        if (!checkPVP(isoLivingCharacter, isoMovingObject)) {
            return null;
        }
        final float abs = Math.abs(isoGameCharacter.getZ() - isoLivingCharacter.getZ());
        if (!handWeapon.isRanged() && abs >= 0.5f) {
            return null;
        }
        if (abs > 3.3f) {
            return null;
        }
        if (!isoGameCharacter.isShootable()) {
            return null;
        }
        if (isoGameCharacter.isCurrentState(FakeDeadZombieState.instance())) {
            return null;
        }
        if (isoGameCharacter.isDead()) {
            return null;
        }
        if (isoGameCharacter.getHitReaction() != null && isoGameCharacter.getHitReaction().contains("Death")) {
            return null;
        }
        final Vector4f tempVector4f = this.tempVector4f;
        this.getNearestTargetPosAndDot(isoLivingCharacter, isoGameCharacter, tempVector4f);
        final float w = tempVector4f.w;
        final float distanceToSquared = IsoUtils.DistanceToSquared(isoLivingCharacter.x, isoLivingCharacter.y, (float)((int)isoLivingCharacter.z * 3), tempVector4f.x, tempVector4f.y, (float)((int)tempVector4f.z * 3));
        if (w < 0.0f) {
            return null;
        }
        if (distanceToSquared > n * n) {
            return null;
        }
        final LosUtil.TestResults lineClear = LosUtil.lineClear(isoLivingCharacter.getCell(), (int)isoLivingCharacter.getX(), (int)isoLivingCharacter.getY(), (int)isoLivingCharacter.getZ(), (int)isoGameCharacter.getX(), (int)isoGameCharacter.getY(), (int)isoGameCharacter.getZ(), false);
        if (lineClear == LosUtil.TestResults.Blocked || lineClear == LosUtil.TestResults.ClearThroughClosedDoor) {
            return null;
        }
        return this.hitInfoPool.alloc().init(isoGameCharacter, w, distanceToSquared, tempVector4f.x, tempVector4f.y, tempVector4f.z);
    }
    
    public static boolean isProne(final IsoMovingObject isoMovingObject) {
        final IsoZombie isoZombie = Type.tryCastTo(isoMovingObject, IsoZombie.class);
        if (isoZombie == null) {
            return isoMovingObject.isOnFloor();
        }
        return isoZombie.isOnFloor() || isoZombie.isCurrentState(ZombieEatBodyState.instance()) || isoZombie.isDead() || isoZombie.isSitAgainstWall() || isoZombie.isCrawling();
    }
    
    public static boolean isStanding(final IsoMovingObject isoMovingObject) {
        return !isProne(isoMovingObject);
    }
    
    public boolean isProneTargetBetter(final IsoGameCharacter isoGameCharacter, final HitInfo hitInfo, final HitInfo hitInfo2) {
        return hitInfo != null && hitInfo.getObject() != null && hitInfo2 != null && hitInfo2.getObject() != null && hitInfo.distSq > hitInfo2.distSq && PolygonalMap2.instance.lineClearCollide(isoGameCharacter.x, isoGameCharacter.y, hitInfo.getObject().x, hitInfo.getObject().y, (int)isoGameCharacter.z, null, false, true) && !PolygonalMap2.instance.lineClearCollide(isoGameCharacter.x, isoGameCharacter.y, hitInfo2.getObject().x, hitInfo2.getObject().y, (int)isoGameCharacter.z, null, false, true);
    }
    
    public static boolean checkPVP(final IsoGameCharacter isoGameCharacter, final IsoMovingObject isoMovingObject) {
        final IsoPlayer isoPlayer = Type.tryCastTo(isoGameCharacter, IsoPlayer.class);
        final IsoPlayer isoPlayer2 = Type.tryCastTo(isoMovingObject, IsoPlayer.class);
        if (GameClient.bClient && isoPlayer2 != null) {
            if (isoPlayer2.isGodMod() || !ServerOptions.instance.PVP.getValue() || (ServerOptions.instance.SafetySystem.getValue() && isoGameCharacter.isSafety() && ((IsoGameCharacter)isoMovingObject).isSafety())) {
                return false;
            }
            if (NonPvpZone.getNonPvpZone((int)isoMovingObject.getX(), (int)isoMovingObject.getY()) != null) {
                return false;
            }
            if (isoPlayer != null && NonPvpZone.getNonPvpZone((int)isoGameCharacter.getX(), (int)isoGameCharacter.getY()) != null) {
                return false;
            }
            if (isoPlayer != null && !isoPlayer.factionPvp && !isoPlayer2.factionPvp) {
                final Faction playerFaction = Faction.getPlayerFaction(isoPlayer);
                final Faction playerFaction2 = Faction.getPlayerFaction(isoPlayer2);
                if (playerFaction2 != null && playerFaction == playerFaction2) {
                    return false;
                }
            }
        }
        return GameClient.bClient || isoPlayer2 == null || IsoPlayer.getCoopPVP();
    }
    
    private void CalcHitListShove(final IsoGameCharacter isoGameCharacter, final boolean b, final AttackVars attackVars, final ArrayList<HitInfo> list) {
        final HandWeapon weapon = attackVars.getWeapon((IsoLivingCharacter)isoGameCharacter);
        final ArrayList<IsoMovingObject> objectList = IsoWorld.instance.CurrentCell.getObjectList();
        for (int i = 0; i < objectList.size(); ++i) {
            final IsoMovingObject isoMovingObject = objectList.get(i);
            if (isoMovingObject != isoGameCharacter) {
                if (!(isoMovingObject instanceof BaseVehicle)) {
                    final IsoGameCharacter isoGameCharacter2 = Type.tryCastTo(isoMovingObject, IsoGameCharacter.class);
                    if (isoGameCharacter2 != null) {
                        if (!isoGameCharacter2.isGodMod()) {
                            if (!isoGameCharacter2.isDead()) {
                                final IsoZombie isoZombie = Type.tryCastTo(isoMovingObject, IsoZombie.class);
                                if (isoZombie == null || !isoZombie.isCurrentState(FakeDeadZombieState.instance())) {
                                    if (checkPVP(isoGameCharacter, isoMovingObject)) {
                                        if (isoMovingObject == attackVars.targetOnGround.getMovingObject() || (isoMovingObject.isShootable() && isStanding(isoMovingObject) && !attackVars.bAimAtFloor) || (isoMovingObject.isShootable() && isProne(isoMovingObject) && attackVars.bAimAtFloor)) {
                                            final Vector4f tempVector4f = this.tempVector4f;
                                            if (this.getNearestTargetPosAndDot(isoGameCharacter, weapon, isoMovingObject, b, tempVector4f)) {
                                                final float w = tempVector4f.w;
                                                final float distanceToSquared = IsoUtils.DistanceToSquared(isoGameCharacter.x, isoGameCharacter.y, (float)((int)isoGameCharacter.z * 3), tempVector4f.x, tempVector4f.y, (float)((int)tempVector4f.z * 3));
                                                final LosUtil.TestResults lineClear = LosUtil.lineClear(isoGameCharacter.getCell(), (int)isoGameCharacter.getX(), (int)isoGameCharacter.getY(), (int)isoGameCharacter.getZ(), (int)isoMovingObject.getX(), (int)isoMovingObject.getY(), (int)isoMovingObject.getZ(), false);
                                                if (lineClear != LosUtil.TestResults.Blocked) {
                                                    if (lineClear != LosUtil.TestResults.ClearThroughClosedDoor) {
                                                        if (isoMovingObject.getCurrentSquare() == null || isoGameCharacter.getCurrentSquare() == null || isoMovingObject.getCurrentSquare() == isoGameCharacter.getCurrentSquare() || !isoMovingObject.getCurrentSquare().isWindowBlockedTo(isoGameCharacter.getCurrentSquare())) {
                                                            if (isoMovingObject.getSquare().getTransparentWallTo(isoGameCharacter.getSquare()) == null) {
                                                                final HitInfo init = this.hitInfoPool.alloc().init(isoMovingObject, w, distanceToSquared, tempVector4f.x, tempVector4f.y, tempVector4f.z);
                                                                if (attackVars.targetOnGround.getMovingObject() == isoMovingObject) {
                                                                    list.clear();
                                                                    list.add(init);
                                                                    break;
                                                                }
                                                                list.add(init);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void CalcHitListWeapon(final IsoGameCharacter isoGameCharacter, final boolean b, final AttackVars attackVars, final ArrayList<HitInfo> list) {
        final HandWeapon weapon = attackVars.getWeapon((IsoLivingCharacter)isoGameCharacter);
        final ArrayList<IsoMovingObject> objectList = IsoWorld.instance.CurrentCell.getObjectList();
        for (int i = 0; i < objectList.size(); ++i) {
            final IsoMovingObject isoMovingObject = objectList.get(i);
            if (isoMovingObject != isoGameCharacter) {
                final IsoGameCharacter isoGameCharacter2 = Type.tryCastTo(isoMovingObject, IsoGameCharacter.class);
                if (isoGameCharacter2 == null || !isoGameCharacter2.isGodMod()) {
                    if (isoGameCharacter2 == null || !isoGameCharacter2.isDead()) {
                        final IsoZombie isoZombie = Type.tryCastTo(isoMovingObject, IsoZombie.class);
                        if (isoZombie == null || !isoZombie.isCurrentState(FakeDeadZombieState.instance())) {
                            if (checkPVP(isoGameCharacter, isoMovingObject)) {
                                if (isoMovingObject == attackVars.targetOnGround.getMovingObject() || (isoMovingObject.isShootable() && isStanding(isoMovingObject) && !attackVars.bAimAtFloor) || (isoMovingObject.isShootable() && isProne(isoMovingObject) && attackVars.bAimAtFloor)) {
                                    final Vector4f tempVector4f = this.tempVector4f;
                                    if (isoMovingObject instanceof BaseVehicle) {
                                        if (((BaseVehicle)isoMovingObject).getNearestBodyworkPart(isoGameCharacter) == null) {
                                            continue;
                                        }
                                        final float dotWithForwardDirection = isoGameCharacter.getDotWithForwardDirection(isoMovingObject.x, isoMovingObject.y);
                                        if (dotWithForwardDirection < 0.8f) {
                                            continue;
                                        }
                                        tempVector4f.set(isoMovingObject.x, isoMovingObject.y, isoMovingObject.z, dotWithForwardDirection);
                                    }
                                    else {
                                        if (isoGameCharacter2 == null) {
                                            continue;
                                        }
                                        if (!this.getNearestTargetPosAndDot(isoGameCharacter, weapon, isoMovingObject, b, tempVector4f)) {
                                            continue;
                                        }
                                    }
                                    final LosUtil.TestResults lineClear = LosUtil.lineClear(isoGameCharacter.getCell(), (int)isoGameCharacter.getX(), (int)isoGameCharacter.getY(), (int)isoGameCharacter.getZ(), (int)isoMovingObject.getX(), (int)isoMovingObject.getY(), (int)isoMovingObject.getZ(), false);
                                    if (lineClear != LosUtil.TestResults.Blocked) {
                                        if (lineClear != LosUtil.TestResults.ClearThroughClosedDoor) {
                                            final float w = tempVector4f.w;
                                            final float distanceToSquared = IsoUtils.DistanceToSquared(isoGameCharacter.x, isoGameCharacter.y, (float)((int)isoGameCharacter.z * 3), tempVector4f.x, tempVector4f.y, (float)((int)tempVector4f.z * 3));
                                            if (isoMovingObject.getSquare().getTransparentWallTo(isoGameCharacter.getSquare()) != null && isoGameCharacter instanceof IsoPlayer) {
                                                if (WeaponType.getWeaponType(isoGameCharacter) == WeaponType.spear) {
                                                    ((IsoPlayer)isoGameCharacter).setAttackType("spearStab");
                                                }
                                                else if (WeaponType.getWeaponType(isoGameCharacter) != WeaponType.knife) {
                                                    continue;
                                                }
                                            }
                                            final IsoWindow windowBetween = this.getWindowBetween(isoGameCharacter, isoMovingObject);
                                            if (windowBetween == null || !windowBetween.isBarricaded()) {
                                                final HitInfo init = this.hitInfoPool.alloc().init(isoMovingObject, w, distanceToSquared, tempVector4f.x, tempVector4f.y, tempVector4f.z);
                                                init.window.setObject(windowBetween);
                                                list.add(init);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!list.isEmpty()) {
            return;
        }
        this.CalcHitListWindow(isoGameCharacter, weapon, list);
    }
    
    private void CalcHitListWindow(final IsoGameCharacter isoGameCharacter, final HandWeapon handWeapon, final ArrayList<HitInfo> list) {
        final Vector2 lookVector = isoGameCharacter.getLookVector(SwipeStatePlayer.tempVector2_1);
        lookVector.setLength(handWeapon.getMaxRange() * handWeapon.getRangeMod(isoGameCharacter));
        HitInfo e = null;
        final ArrayList<IsoWindow> windowList = IsoWorld.instance.CurrentCell.getWindowList();
        for (int i = 0; i < windowList.size(); ++i) {
            final IsoWindow isoWindow = windowList.get(i);
            if ((int)isoWindow.getZ() == (int)isoGameCharacter.z) {
                if (this.windowVisitor.isHittable(isoWindow)) {
                    final float x = isoWindow.getX();
                    final float y = isoWindow.getY();
                    final float n = x + (isoWindow.getNorth() ? 1.0f : 0.0f);
                    final float n2 = y + (isoWindow.getNorth() ? 0.0f : 1.0f);
                    if (Line2D.linesIntersect(isoGameCharacter.x, isoGameCharacter.y, isoGameCharacter.x + lookVector.x, isoGameCharacter.y + lookVector.y, x, y, n, n2)) {
                        final IsoGridSquare addSheetSquare = isoWindow.getAddSheetSquare(isoGameCharacter);
                        if (addSheetSquare != null) {
                            if (!LosUtil.lineClearCollide((int)isoGameCharacter.x, (int)isoGameCharacter.y, (int)isoGameCharacter.z, addSheetSquare.x, addSheetSquare.y, addSheetSquare.z, false)) {
                                final float distanceToSquared = IsoUtils.DistanceToSquared(isoGameCharacter.x, isoGameCharacter.y, x + (n - x) / 2.0f, y + (n2 - y) / 2.0f);
                                if (e == null || e.distSq >= distanceToSquared) {
                                    final float n3 = 1.0f;
                                    if (e == null) {
                                        e = this.hitInfoPool.alloc();
                                    }
                                    e.init(isoWindow, n3, distanceToSquared);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (e != null) {
            list.add(e);
        }
    }
    
    public void CalcHitList(final IsoGameCharacter isoGameCharacter, final boolean b, final AttackVars attackVars, final ArrayList<HitInfo> list) {
        if (GameClient.bClient && !isoGameCharacter.isLocal()) {
            return;
        }
        this.hitInfoPool.release(list);
        list.clear();
        final HandWeapon weapon = attackVars.getWeapon((IsoLivingCharacter)isoGameCharacter);
        int maxHitCount = weapon.getMaxHitCount();
        if (attackVars.bDoShove) {
            maxHitCount = ((WeaponType.getWeaponType(isoGameCharacter) != WeaponType.barehand) ? 3 : 1);
        }
        if (!weapon.isRanged() && !SandboxOptions.instance.MultiHitZombies.getValue()) {
            maxHitCount = 1;
        }
        if (weapon == ((IsoPlayer)isoGameCharacter).bareHands && !(isoGameCharacter.getPrimaryHandItem() instanceof HandWeapon)) {
            maxHitCount = 1;
        }
        if (weapon == ((IsoPlayer)isoGameCharacter).bareHands && attackVars.targetOnGround.getMovingObject() != null) {
            maxHitCount = 1;
        }
        if (0 < maxHitCount) {
            if (attackVars.bDoShove) {
                this.CalcHitListShove(isoGameCharacter, b, attackVars, list);
            }
            else {
                this.CalcHitListWeapon(isoGameCharacter, b, attackVars, list);
            }
            if (list.size() == 1 && ((HitInfo)list.get(0)).getObject() == null) {
                return;
            }
            this.filterTargetsByZ(isoGameCharacter);
            Collections.sort((List<Object>)list, (Comparator<? super Object>)SwipeStatePlayer.Comparator);
            if (weapon.isPiercingBullets()) {
                SwipeStatePlayer.HitList2.clear();
                double degrees = 0.0;
                for (int i = 0; i < list.size(); ++i) {
                    final HitInfo hitInfo = list.get(i);
                    final IsoMovingObject object = hitInfo.getObject();
                    if (object != null) {
                        final double atan2 = Math.atan2(-(isoGameCharacter.getY() - object.getY()), isoGameCharacter.getX() - object.getX());
                        double abs;
                        if (atan2 < 0.0) {
                            abs = Math.abs(atan2);
                        }
                        else {
                            abs = 6.283185307179586 - atan2;
                        }
                        if (i == 0) {
                            degrees = Math.toDegrees(abs);
                            SwipeStatePlayer.HitList2.add(hitInfo);
                        }
                        else if (Math.abs(degrees - Math.toDegrees(abs)) < 1.0) {
                            SwipeStatePlayer.HitList2.add(hitInfo);
                            break;
                        }
                    }
                }
                list.removeAll(SwipeStatePlayer.HitList2);
                this.hitInfoPool.release(list);
                list.clear();
                list.addAll(SwipeStatePlayer.HitList2);
            }
            else {
                while (list.size() > maxHitCount) {
                    this.hitInfoPool.release(list.remove(list.size() - 1));
                }
            }
        }
        for (int j = 0; j < list.size(); ++j) {
            final HitInfo hitInfo2 = list.get(j);
            hitInfo2.chance = this.CalcHitChance(isoGameCharacter, weapon, hitInfo2);
        }
    }
    
    private void filterTargetsByZ(final IsoGameCharacter isoGameCharacter) {
        float n = Float.MAX_VALUE;
        HitInfo hitInfo = null;
        for (int i = 0; i < isoGameCharacter.hitList.size(); ++i) {
            final HitInfo hitInfo2 = isoGameCharacter.hitList.get(i);
            final float abs = Math.abs(hitInfo2.z - isoGameCharacter.getZ());
            if (abs < n) {
                n = abs;
                hitInfo = hitInfo2;
            }
        }
        if (hitInfo == null) {
            return;
        }
        for (int j = isoGameCharacter.hitList.size() - 1; j >= 0; --j) {
            final HitInfo hitInfo3 = isoGameCharacter.hitList.get(j);
            if (hitInfo3 != hitInfo) {
                if (Math.abs(hitInfo3.z - hitInfo.z) > 0.5f) {
                    this.hitInfoPool.release(hitInfo3);
                    isoGameCharacter.hitList.remove(j);
                }
            }
        }
    }
    
    public int CalcHitChance(final IsoGameCharacter isoGameCharacter, final HandWeapon handWeapon, final HitInfo hitInfo) {
        final IsoMovingObject object = hitInfo.getObject();
        if (object == null) {
            return 0;
        }
        if (isoGameCharacter.getVehicle() != null) {
            final BaseVehicle vehicle = isoGameCharacter.getVehicle();
            final Vector3f forwardVector = vehicle.getForwardVector(BaseVehicle.TL_vector3f_pool.get().alloc());
            final Vector2 vector2 = BaseVehicle.TL_vector2_pool.get().alloc();
            vector2.x = forwardVector.x;
            vector2.y = forwardVector.z;
            vector2.normalize();
            final VehicleScript.Area areaById = vehicle.getScript().getAreaById(vehicle.getPassengerArea(vehicle.getSeat(isoGameCharacter)));
            int n = -90;
            if (areaById.x > 0.0f) {
                n = 90;
            }
            vector2.rotate((float)Math.toRadians(n));
            vector2.normalize();
            final Vector2 vector3 = BaseVehicle.TL_vector2_pool.get().alloc();
            vector3.x = object.x;
            vector3.y = object.y;
            final Vector2 vector4 = vector3;
            vector4.x -= isoGameCharacter.x;
            final Vector2 vector5 = vector3;
            vector5.y -= isoGameCharacter.y;
            vector3.normalize();
            if (vector3.dot(vector2) > -0.6) {
                return 0;
            }
            BaseVehicle.TL_vector2_pool.get().release(vector2);
            BaseVehicle.TL_vector2_pool.get().release(vector3);
            BaseVehicle.TL_vector3f_pool.get().release(forwardVector);
        }
        if (System.currentTimeMillis() - isoGameCharacter.lastAutomaticShoot > 600L) {
            isoGameCharacter.shootInARow = 0;
        }
        int n2 = (int)(handWeapon.getHitChance() + handWeapon.getAimingPerkHitChanceModifier() * isoGameCharacter.getPerkLevel(PerkFactory.Perks.Aiming));
        if (n2 > 95) {
            n2 = 95;
        }
        final int n3 = n2 - isoGameCharacter.shootInARow * 2;
        float sqrt = PZMath.sqrt(hitInfo.distSq);
        float n4 = 1.3f;
        if (object instanceof IsoPlayer) {
            sqrt *= 1.5;
            n4 = 1.0f;
        }
        int n5 = (int)(n3 + (handWeapon.getMaxRange() * handWeapon.getRangeMod(isoGameCharacter) - sqrt) * n4);
        if (handWeapon.getMinRangeRanged() > 0.0f) {
            if (sqrt < handWeapon.getMinRangeRanged()) {
                n5 -= 50;
            }
        }
        else if (sqrt < 1.7 && handWeapon.isRanged() && !(object instanceof IsoPlayer)) {
            n5 += 35;
        }
        if (handWeapon.isRanged() && isoGameCharacter.getBeenMovingFor() > handWeapon.getAimingTime() + isoGameCharacter.getPerkLevel(PerkFactory.Perks.Aiming)) {
            n5 -= (int)(isoGameCharacter.getBeenMovingFor() - (handWeapon.getAimingTime() + isoGameCharacter.getPerkLevel(PerkFactory.Perks.Aiming)));
        }
        if (hitInfo.getObject() instanceof IsoPlayer) {
            final IsoPlayer isoPlayer = (IsoPlayer)hitInfo.getObject();
            if (isoPlayer.isPlayerMoving()) {
                n5 -= 5;
            }
            if (isoPlayer.isRunning()) {
                n5 -= 10;
            }
            if (isoPlayer.isSprinting()) {
                n5 -= 15;
            }
        }
        if (handWeapon.isRanged() && isoGameCharacter.getVehicle() != null) {
            n5 -= (int)(Math.abs(isoGameCharacter.getVehicle().getCurrentSpeedKmHour()) * 2.0f);
        }
        if (isoGameCharacter.Traits.Marksman.isSet()) {
            n5 += 20;
        }
        float n6 = 0.0f;
        for (int i = BodyPartType.ToIndex(BodyPartType.Hand_L); i <= BodyPartType.ToIndex(BodyPartType.UpperArm_R); ++i) {
            n6 += isoGameCharacter.getBodyDamage().getBodyParts().get(i).getPain();
        }
        if (n6 > 0.0f) {
            n5 -= (int)(n6 / 10.0f);
        }
        int n7 = n5 - isoGameCharacter.getMoodles().getMoodleLevel(MoodleType.Tired) * 5;
        if (n7 <= 10) {
            n7 = 10;
        }
        if (n7 > 100 || !handWeapon.isRanged()) {
            n7 = 100;
        }
        return n7;
    }
    
    public static Vector3 getBoneWorldPos(final IsoMovingObject isoMovingObject, final String s, final Vector3 vector3) {
        final IsoGameCharacter isoGameCharacter = Type.tryCastTo(isoMovingObject, IsoGameCharacter.class);
        if (isoGameCharacter == null || s == null) {
            return vector3.set(isoMovingObject.x, isoMovingObject.y, isoMovingObject.z);
        }
        final AnimationPlayer animationPlayer = isoGameCharacter.getAnimationPlayer();
        if (animationPlayer == null || !animationPlayer.isReady()) {
            return vector3.set(isoMovingObject.x, isoMovingObject.y, isoMovingObject.z);
        }
        final int skinningBoneIndex = animationPlayer.getSkinningBoneIndex(s, -1);
        if (skinningBoneIndex == -1) {
            return vector3.set(isoMovingObject.x, isoMovingObject.y, isoMovingObject.z);
        }
        Model.BoneToWorldCoords(isoGameCharacter, skinningBoneIndex, vector3);
        return vector3;
    }
    
    public void ConnectSwing(final IsoGameCharacter isoGameCharacter, final HandWeapon weapon) {
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        final IsoLivingCharacter isoLivingCharacter = (IsoLivingCharacter)isoGameCharacter;
        final IsoPlayer isoPlayer = Type.tryCastTo(isoGameCharacter, IsoPlayer.class);
        if (isoGameCharacter.getVariableBoolean("ShoveAnim")) {
            isoLivingCharacter.setDoShove(true);
        }
        if (GameServer.bServer) {
            DebugLog.log(DebugType.Network, "Player swing connects.");
        }
        LuaEventManager.triggerEvent("OnWeaponSwingHitPoint", isoGameCharacter, weapon);
        if (weapon.getPhysicsObject() != null) {
            isoGameCharacter.Throw(weapon);
        }
        if (weapon.isUseSelf()) {
            weapon.Use();
        }
        if (weapon.isOtherHandUse() && isoGameCharacter.getSecondaryHandItem() != null) {
            isoGameCharacter.getSecondaryHandItem().Use();
        }
        boolean b = false;
        if (isoLivingCharacter.bDoShove && !isoGameCharacter.isAimAtFloor()) {
            b = true;
        }
        int n = 0;
        boolean helmetFall = false;
        isoGameCharacter.attackVars.setWeapon(weapon);
        isoGameCharacter.attackVars.targetOnGround.setMovingObject(isoLivingCharacter.targetOnGround);
        isoGameCharacter.attackVars.bAimAtFloor = isoGameCharacter.isAimAtFloor();
        isoGameCharacter.attackVars.bDoShove = isoLivingCharacter.bDoShove;
        if (isoGameCharacter.getVariableBoolean("ShoveAnim")) {
            isoGameCharacter.attackVars.bDoShove = true;
        }
        this.CalcHitList(isoGameCharacter, false, isoGameCharacter.attackVars, isoGameCharacter.hitList);
        final int size = isoGameCharacter.hitList.size();
        boolean checkObjectHit = false;
        if (size == 0) {
            checkObjectHit = this.CheckObjectHit(isoGameCharacter, weapon);
        }
        if (weapon.isUseEndurance()) {
            float n2 = 0.0f;
            if (weapon.isTwoHandWeapon() && (isoGameCharacter.getPrimaryHandItem() != weapon || isoGameCharacter.getSecondaryHandItem() != weapon)) {
                n2 = weapon.getWeight() / 1.5f / 10.0f;
            }
            if (size <= 0 && !isoGameCharacter.isForceShove()) {
                final float n3 = (weapon.getWeight() * 0.28f * weapon.getFatigueMod(isoGameCharacter) * isoGameCharacter.getFatigueMod() * weapon.getEnduranceMod() * 0.3f + n2) * 0.04f;
                float n4 = 1.0f;
                if (isoGameCharacter.Traits.Asthmatic.isSet()) {
                    n4 = 1.3f;
                }
                final Stats stats = isoGameCharacter.getStats();
                stats.endurance -= n3 * n4;
            }
        }
        isoGameCharacter.setLastHitCount(isoGameCharacter.hitList.size());
        if (!weapon.isMultipleHitConditionAffected()) {
            n = 1;
        }
        int n5 = 1;
        this.dotList.clear();
        if (isoGameCharacter.hitList.isEmpty() && isoGameCharacter.getClickSound() != null && !isoLivingCharacter.bDoShove) {
            if ((isoGameCharacter instanceof IsoPlayer && ((IsoPlayer)isoGameCharacter).isLocalPlayer()) || !(isoGameCharacter instanceof IsoPlayer)) {
                isoGameCharacter.getEmitter().playSound(isoGameCharacter.getClickSound());
            }
            isoGameCharacter.setRecoilDelay(10.0f);
        }
        boolean sendHitCharacter = false;
        for (int i = 0; i < isoGameCharacter.hitList.size(); ++i) {
            int hitHeadWhileOnFloor = 0;
            boolean hitLegsWhileOnFloor = false;
            final HitInfo hitInfo = isoGameCharacter.hitList.get(i);
            final IsoMovingObject object = hitInfo.getObject();
            final BaseVehicle vehicleHitLocation = Type.tryCastTo(object, BaseVehicle.class);
            final IsoZombie isoZombie = Type.tryCastTo(object, IsoZombie.class);
            if (hitInfo.getObject() == null && hitInfo.window.getObject() != null) {
                hitInfo.window.getObject().WeaponHit(isoGameCharacter, weapon);
            }
            else {
                this.smashWindowBetween(isoGameCharacter, object, weapon);
                if (!this.isWindowBetween(isoGameCharacter, object)) {
                    if (Rand.Next(100) <= hitInfo.chance) {
                        final Vector2 set = SwipeStatePlayer.tempVector2_1.set(isoGameCharacter.getX(), isoGameCharacter.getY());
                        final Vector2 set2;
                        final Vector2 vector2 = set2 = SwipeStatePlayer.tempVector2_2.set(object.getX(), object.getY());
                        set2.x -= set.x;
                        final Vector2 vector3 = vector2;
                        vector3.y -= set.y;
                        final Vector2 lookVector = isoGameCharacter.getLookVector(SwipeStatePlayer.tempVector2_1);
                        lookVector.tangent();
                        vector2.normalize();
                        boolean b2 = true;
                        final float dot = lookVector.dot(vector2);
                        for (int j = 0; j < this.dotList.size(); ++j) {
                            if (Math.abs(dot - this.dotList.get(j)) < 1.0E-4) {
                                b2 = false;
                            }
                        }
                        float minDamage = weapon.getMinDamage();
                        float maxDamage = weapon.getMaxDamage();
                        long n6 = 0L;
                        if (!b2) {
                            minDamage /= 5.0f;
                            maxDamage /= 5.0f;
                        }
                        if (isoGameCharacter.isAimAtFloor() && !weapon.isRanged() && isoGameCharacter.isNPC()) {
                            splash(object, weapon, isoGameCharacter);
                            hitHeadWhileOnFloor = (byte)Rand.Next(2);
                        }
                        else if (isoGameCharacter.isAimAtFloor() && !weapon.isRanged()) {
                            if (isoPlayer == null || isoPlayer.isLocalPlayer()) {
                                if (!StringUtils.isNullOrEmpty(weapon.getHitFloorSound())) {
                                    isoPlayer.setMeleeHitSurface(ParameterMeleeHitSurface.Material.Body);
                                    n6 = isoGameCharacter.playSound(weapon.getHitFloorSound());
                                }
                                else {
                                    if (isoPlayer != null) {
                                        isoPlayer.setMeleeHitSurface(ParameterMeleeHitSurface.Material.Body);
                                    }
                                    n6 = isoGameCharacter.playSound(weapon.getZombieHitSound());
                                }
                            }
                            if (this.DoSwingCollisionBoneCheck(isoGameCharacter, GetWeapon(isoGameCharacter), (IsoGameCharacter)object, ((IsoGameCharacter)object).getAnimationPlayer().getSkinningBoneIndex("Bip01_Head", -1), 0.28f) == -1) {
                                if (this.DoSwingCollisionBoneCheck(isoGameCharacter, GetWeapon(isoGameCharacter), (IsoGameCharacter)object, ((IsoGameCharacter)object).getAnimationPlayer().getSkinningBoneIndex("Bip01_Spine", -1), 0.28f) == -1) {
                                    int n7 = this.DoSwingCollisionBoneCheck(isoGameCharacter, GetWeapon(isoGameCharacter), (IsoGameCharacter)object, ((IsoGameCharacter)object).getAnimationPlayer().getSkinningBoneIndex("Bip01_L_Calf", -1), 0.13f);
                                    if (n7 == -1) {
                                        n7 = this.DoSwingCollisionBoneCheck(isoGameCharacter, GetWeapon(isoGameCharacter), (IsoGameCharacter)object, ((IsoGameCharacter)object).getAnimationPlayer().getSkinningBoneIndex("Bip01_R_Calf", -1), 0.13f);
                                    }
                                    if (n7 == -1) {
                                        n7 = this.DoSwingCollisionBoneCheck(isoGameCharacter, GetWeapon(isoGameCharacter), (IsoGameCharacter)object, ((IsoGameCharacter)object).getAnimationPlayer().getSkinningBoneIndex("Bip01_L_Foot", -1), 0.23f);
                                    }
                                    if (n7 == -1) {
                                        n7 = this.DoSwingCollisionBoneCheck(isoGameCharacter, GetWeapon(isoGameCharacter), (IsoGameCharacter)object, ((IsoGameCharacter)object).getAnimationPlayer().getSkinningBoneIndex("Bip01_R_Foot", -1), 0.23f);
                                    }
                                    if (n7 == -1) {
                                        continue;
                                    }
                                    hitLegsWhileOnFloor = true;
                                }
                            }
                            else {
                                splash(object, weapon, isoGameCharacter);
                                splash(object, weapon, isoGameCharacter);
                                hitHeadWhileOnFloor = (byte)(Rand.Next(0, 3) + 1);
                            }
                        }
                        if (!isoGameCharacter.attackVars.bAimAtFloor && (!isoGameCharacter.attackVars.bCloseKill || !isoGameCharacter.isCriticalHit()) && !isoLivingCharacter.bDoShove && object instanceof IsoGameCharacter && (isoPlayer == null || isoPlayer.isLocalPlayer())) {
                            if (isoPlayer != null) {
                                isoPlayer.setMeleeHitSurface(ParameterMeleeHitSurface.Material.Body);
                            }
                            if (weapon.isRanged()) {
                                n6 = ((IsoGameCharacter)object).playSound(weapon.getZombieHitSound());
                            }
                            else {
                                n6 = isoGameCharacter.playSound(weapon.getZombieHitSound());
                            }
                        }
                        if (weapon.isRanged() && isoZombie != null) {
                            final Vector2 set3 = SwipeStatePlayer.tempVector2_1.set(isoGameCharacter.getX(), isoGameCharacter.getY());
                            final Vector2 set4;
                            final Vector2 vector4 = set4 = SwipeStatePlayer.tempVector2_2.set(object.getX(), object.getY());
                            set4.x -= set3.x;
                            final Vector2 vector5 = vector4;
                            vector5.y -= set3.y;
                            final Vector2 forwardDirection = isoZombie.getForwardDirection();
                            vector4.normalize();
                            forwardDirection.normalize();
                            isoZombie.setHitFromBehind(vector4.dot(forwardDirection) > 0.5);
                        }
                        if (this.dotList.isEmpty()) {
                            this.dotList.add(dot);
                        }
                        if (isoZombie != null && isoZombie.isCurrentState(ZombieOnGroundState.instance())) {
                            isoZombie.setReanimateTimer(isoZombie.getReanimateTimer() + Rand.Next(10));
                        }
                        if (isoZombie != null && isoZombie.isCurrentState(ZombieGetUpState.instance())) {
                            isoZombie.setReanimateTimer((float)(Rand.Next(60) + 30));
                        }
                        boolean b3 = false;
                        if (!weapon.isTwoHandWeapon() || isoGameCharacter.isItemInBothHands(weapon)) {
                            b3 = true;
                        }
                        final float n8 = minDamage;
                        final float n9 = maxDamage - minDamage;
                        float n10;
                        if (n9 == 0.0f) {
                            n10 = n8 + 0.0f;
                        }
                        else {
                            n10 = n8 + Rand.Next((int)(n9 * 1000.0f)) / 1000.0f;
                        }
                        if (!weapon.isRanged()) {
                            n10 *= weapon.getDamageMod(isoGameCharacter) * isoGameCharacter.getHittingMod();
                        }
                        if (!b3 && !weapon.isRanged() && maxDamage > minDamage) {
                            n10 -= minDamage;
                        }
                        if (!isoGameCharacter.isAimAtFloor() || !isoLivingCharacter.bDoShove) {
                            float n11 = 0.0f;
                            for (int k = BodyPartType.ToIndex(BodyPartType.Hand_L); k <= BodyPartType.ToIndex(BodyPartType.UpperArm_R); ++k) {
                                n11 += isoGameCharacter.getBodyDamage().getBodyParts().get(k).getPain();
                            }
                            if (n11 > 10.0f) {
                                n10 /= PZMath.clamp(n11 / 10.0f, 1.0f, 30.0f);
                                MoodlesUI.getInstance().wiggle(MoodleType.Pain);
                                MoodlesUI.getInstance().wiggle(MoodleType.Injured);
                            }
                        }
                        else {
                            float n12 = 0.0f;
                            for (int l = BodyPartType.ToIndex(BodyPartType.UpperLeg_L); l <= BodyPartType.ToIndex(BodyPartType.Foot_R); ++l) {
                                n12 += isoGameCharacter.getBodyDamage().getBodyParts().get(l).getPain();
                            }
                            if (n12 > 10.0f) {
                                n10 /= PZMath.clamp(n12 / 10.0f, 1.0f, 30.0f);
                                MoodlesUI.getInstance().wiggle(MoodleType.Pain);
                                MoodlesUI.getInstance().wiggle(MoodleType.Injured);
                            }
                        }
                        if (isoGameCharacter.Traits.Underweight.isSet()) {
                            n10 *= 0.8f;
                        }
                        if (isoGameCharacter.Traits.VeryUnderweight.isSet()) {
                            n10 *= 0.6f;
                        }
                        if (isoGameCharacter.Traits.Emaciated.isSet()) {
                            n10 *= 0.4f;
                        }
                        float f = n10 / (n5 / 2.0f);
                        if (isoGameCharacter.isAttackWasSuperAttack()) {
                            f *= 5.0f;
                        }
                        ++n5;
                        if (weapon.isMultipleHitConditionAffected()) {
                            n = 1;
                        }
                        final Vector2 set5 = SwipeStatePlayer.tempVector2_1.set(isoGameCharacter.getX(), isoGameCharacter.getY());
                        final Vector2 set6;
                        final Vector2 vector6 = set6 = SwipeStatePlayer.tempVector2_2.set(object.getX(), object.getY());
                        set6.x -= set5.x;
                        final Vector2 vector7 = vector6;
                        vector7.y -= set5.y;
                        final float length = vector6.getLength();
                        float n13 = 1.0f;
                        if (!weapon.isRangeFalloff()) {
                            n13 = length / weapon.getMaxRange(isoGameCharacter);
                        }
                        float n14 = n13 * 2.0f;
                        if (n14 < 0.3f) {
                            n14 = 1.0f;
                        }
                        if (weapon.isRanged() && isoGameCharacter.getPerkLevel(PerkFactory.Perks.Aiming) < 6 && isoGameCharacter.getMoodles().getMoodleLevel(MoodleType.Panic) > 2) {
                            f -= isoGameCharacter.getMoodles().getMoodleLevel(MoodleType.Panic) * 0.2f;
                            MoodlesUI.getInstance().wiggle(MoodleType.Panic);
                        }
                        if (!weapon.isRanged() && isoGameCharacter.getMoodles().getMoodleLevel(MoodleType.Panic) > 1) {
                            f -= isoGameCharacter.getMoodles().getMoodleLevel(MoodleType.Panic) * 0.1f;
                            MoodlesUI.getInstance().wiggle(MoodleType.Panic);
                        }
                        if (isoGameCharacter.getMoodles().getMoodleLevel(MoodleType.Stress) > 1) {
                            f -= isoGameCharacter.getMoodles().getMoodleLevel(MoodleType.Stress) * 0.1f;
                            MoodlesUI.getInstance().wiggle(MoodleType.Stress);
                        }
                        if (f < 0.0f) {
                            f = 0.1f;
                        }
                        if (isoGameCharacter.isAimAtFloor() && isoLivingCharacter.bDoShove) {
                            final float n15 = Rand.Next(0.7f, 1.0f) + isoGameCharacter.getPerkLevel(PerkFactory.Perks.Strength) * 0.2f;
                            final Clothing clothing = (Clothing)isoGameCharacter.getWornItem("Shoes");
                            if (clothing == null) {
                                f = n15 * 0.5f;
                            }
                            else {
                                f = n15 * clothing.getStompPower();
                            }
                        }
                        if (!weapon.isRanged()) {
                            switch (isoGameCharacter.getMoodles().getMoodleLevel(MoodleType.Endurance)) {
                                case 1: {
                                    f *= 0.5f;
                                    MoodlesUI.getInstance().wiggle(MoodleType.Endurance);
                                    break;
                                }
                                case 2: {
                                    f *= 0.2f;
                                    MoodlesUI.getInstance().wiggle(MoodleType.Endurance);
                                    break;
                                }
                                case 3: {
                                    f *= 0.1f;
                                    MoodlesUI.getInstance().wiggle(MoodleType.Endurance);
                                    break;
                                }
                                case 4: {
                                    f *= 0.05f;
                                    MoodlesUI.getInstance().wiggle(MoodleType.Endurance);
                                    break;
                                }
                            }
                            switch (isoGameCharacter.getMoodles().getMoodleLevel(MoodleType.Tired)) {
                                case 1: {
                                    f *= 0.5f;
                                    MoodlesUI.getInstance().wiggle(MoodleType.Tired);
                                    break;
                                }
                                case 2: {
                                    f *= 0.2f;
                                    MoodlesUI.getInstance().wiggle(MoodleType.Tired);
                                    break;
                                }
                                case 3: {
                                    f *= 0.1f;
                                    MoodlesUI.getInstance().wiggle(MoodleType.Tired);
                                    break;
                                }
                                case 4: {
                                    f *= 0.05f;
                                    MoodlesUI.getInstance().wiggle(MoodleType.Tired);
                                    break;
                                }
                            }
                        }
                        isoGameCharacter.knockbackAttackMod = 1.0f;
                        if ("KnifeDeath".equals(isoGameCharacter.getVariableString("ZombieHitReaction"))) {
                            n14 *= 1000.0f;
                            isoGameCharacter.knockbackAttackMod = 0.0f;
                            isoGameCharacter.addWorldSoundUnlessInvisible(4, 4, false);
                            object.setCloseKilled(isoGameCharacter.attackVars.bCloseKill = true);
                        }
                        else {
                            object.setCloseKilled(isoGameCharacter.attackVars.bCloseKill = false);
                            isoGameCharacter.addWorldSoundUnlessInvisible(8, 8, false);
                            if (Rand.Next(3) == 0 || (isoGameCharacter.isAimAtFloor() && isoLivingCharacter.bDoShove)) {
                                isoGameCharacter.addWorldSoundUnlessInvisible(10, 10, false);
                            }
                            else if (Rand.Next(7) == 0) {
                                isoGameCharacter.addWorldSoundUnlessInvisible(16, 16, false);
                            }
                        }
                        object.setHitFromAngle(hitInfo.dot);
                        if (isoZombie != null) {
                            isoZombie.setHitFromBehind(isoGameCharacter.isBehind(isoZombie));
                            isoZombie.setHitAngle(isoGameCharacter.getForwardDirection());
                            isoZombie.setPlayerAttackPosition(isoZombie.testDotSide(isoGameCharacter));
                            isoZombie.setHitHeadWhileOnFloor(hitHeadWhileOnFloor);
                            isoZombie.setHitLegsWhileOnFloor(hitLegsWhileOnFloor);
                            if (hitHeadWhileOnFloor > 0) {
                                isoZombie.addBlood(BloodBodyPartType.Head, true, true, true);
                                isoZombie.addBlood(BloodBodyPartType.Torso_Upper, true, false, false);
                                isoZombie.addBlood(BloodBodyPartType.UpperArm_L, true, false, false);
                                isoZombie.addBlood(BloodBodyPartType.UpperArm_R, true, false, false);
                                f *= 3.0f;
                            }
                            if (hitLegsWhileOnFloor) {
                                f = 0.0f;
                            }
                            int n16;
                            if (hitHeadWhileOnFloor > 0) {
                                n16 = Rand.Next(BodyPartType.ToIndex(BodyPartType.Head), BodyPartType.ToIndex(BodyPartType.Neck) + 1);
                            }
                            else if (hitLegsWhileOnFloor) {
                                n16 = Rand.Next(BodyPartType.ToIndex(BodyPartType.Groin), BodyPartType.ToIndex(BodyPartType.Foot_R) + 1);
                            }
                            else {
                                n16 = Rand.Next(BodyPartType.ToIndex(BodyPartType.Hand_L), BodyPartType.ToIndex(BodyPartType.Neck) + 1);
                            }
                            float n17 = isoZombie.getBodyPartClothingDefense(n16, false, weapon.isRanged()) / 2.0f + isoZombie.getBodyPartClothingDefense(n16, true, weapon.isRanged());
                            if (n17 > 70.0f) {
                                n17 = 70.0f;
                            }
                            f *= Math.abs(1.0f - n17 / 100.0f);
                            if ((!GameClient.bClient && !GameServer.bServer) || (GameClient.bClient && isoGameCharacter instanceof IsoPlayer && ((IsoPlayer)isoGameCharacter).isLocalPlayer())) {
                                helmetFall = isoZombie.helmetFall(hitHeadWhileOnFloor > 0);
                            }
                            if ("KnifeDeath".equals(isoGameCharacter.getVariableString("ZombieHitReaction")) && !"Tutorial".equals(Core.GameMode)) {
                                int n18 = 8;
                                if (isoZombie.isCurrentState(AttackState.instance())) {
                                    n18 = 3;
                                }
                                if (Rand.NextBool(n18 + (isoGameCharacter.getPerkLevel(PerkFactory.Perks.SmallBlade) + 1) * 2)) {
                                    final InventoryItem primaryHandItem = isoGameCharacter.getPrimaryHandItem();
                                    isoGameCharacter.getInventory().Remove(primaryHandItem);
                                    isoGameCharacter.removeFromHands(primaryHandItem);
                                    isoZombie.setAttachedItem("JawStab", primaryHandItem);
                                    isoZombie.setJawStabAttach(true);
                                }
                                isoZombie.setKnifeDeath(true);
                            }
                        }
                        float hit = 0.0f;
                        final boolean criticalHit = isoGameCharacter.isCriticalHit();
                        if (vehicleHitLocation == null && object.getSquare() != null && isoGameCharacter.getSquare() != null) {
                            object.setCloseKilled(isoGameCharacter.attackVars.bCloseKill);
                            if (((IsoPlayer)isoGameCharacter).isLocalPlayer() || isoGameCharacter.isNPC()) {
                                hit = object.Hit(weapon, isoGameCharacter, f, b, n14);
                                this.setParameterCharacterHitResult(isoGameCharacter, isoZombie, n6);
                            }
                            LuaEventManager.triggerEvent("OnWeaponHitXp", isoGameCharacter, weapon, object, f);
                            if ((!isoLivingCharacter.bDoShove || isoGameCharacter.isAimAtFloor()) && isoGameCharacter.DistToSquared(object) < 2.0f && Math.abs(isoGameCharacter.z - object.z) < 0.5f) {
                                isoGameCharacter.addBlood(null, false, false, false);
                            }
                            if (object instanceof IsoGameCharacter) {
                                if (((IsoGameCharacter)object).isDead()) {
                                    final Stats stats2 = isoGameCharacter.getStats();
                                    stats2.stress -= 0.02f;
                                }
                                else if (!(object instanceof IsoPlayer) && (!isoLivingCharacter.bDoShove || isoGameCharacter.isAimAtFloor())) {
                                    splash(object, weapon, isoGameCharacter);
                                }
                            }
                        }
                        else if (vehicleHitLocation != null) {
                            final VehiclePart nearestBodyworkPart = vehicleHitLocation.getNearestBodyworkPart(isoGameCharacter);
                            if (nearestBodyworkPart != null) {
                                VehicleWindow vehicleWindow = nearestBodyworkPart.getWindow();
                                for (int n19 = 0; n19 < nearestBodyworkPart.getChildCount(); ++n19) {
                                    final VehiclePart child = nearestBodyworkPart.getChild(n19);
                                    if (child.getWindow() != null) {
                                        vehicleWindow = child.getWindow();
                                        break;
                                    }
                                }
                                if (vehicleWindow != null && vehicleWindow.isHittable()) {
                                    vehicleWindow.damage(this.calcDamageToVehicle((int)f * 10, weapon.getDoorDamage(), true));
                                    isoGameCharacter.playSound("HitVehicleWindowWithWeapon");
                                }
                                else {
                                    nearestBodyworkPart.setCondition(nearestBodyworkPart.getCondition() - this.calcDamageToVehicle((int)f * 10, weapon.getDoorDamage(), false));
                                    isoPlayer.setVehicleHitLocation(vehicleHitLocation);
                                    isoGameCharacter.playSound("HitVehiclePartWithWeapon");
                                }
                            }
                        }
                        if (GameClient.bClient && isoGameCharacter.isLocal()) {
                            if (object instanceof IsoPlayer) {
                                isoGameCharacter.setSafetyCooldown(isoGameCharacter.getSafetyCooldown() + ServerOptions.instance.SafetyCooldownTimer.getValue());
                            }
                            if (object instanceof IsoGameCharacter) {
                                HitReactionNetworkAI.CalcHitReactionWeapon(isoGameCharacter, (IsoGameCharacter)object, weapon);
                            }
                            sendHitCharacter = GameClient.sendHitCharacter(isoGameCharacter, object, weapon, hit, b, n14, criticalHit, helmetFall, hitHeadWhileOnFloor > 0);
                        }
                    }
                }
            }
        }
        if (GameClient.bClient && ((IsoPlayer)isoGameCharacter).isLocalPlayer() && !sendHitCharacter) {
            GameClient.sendHitCharacter(isoGameCharacter, null, weapon, 0.0f, b, 1.0f, isoGameCharacter.isCriticalHit(), false, false);
        }
        if (n == 0 && checkObjectHit) {
            if (Rand.Next(weapon.getConditionLowerChance() * ((this.bHitOnlyTree && weapon.getScriptItem().Categories.contains("Axe")) ? 2 : 1) + isoGameCharacter.getMaintenanceMod() * 2) == 0) {
                n = 1;
            }
            else if (Rand.NextBool(2) && !weapon.getName().contains("Bare Hands")) {
                if (!weapon.isTwoHandWeapon() || isoGameCharacter.getPrimaryHandItem() == weapon || isoGameCharacter.getSecondaryHandItem() == weapon || !Rand.NextBool(3)) {
                    isoGameCharacter.getXp().AddXP(PerkFactory.Perks.Maintenance, 1.0f);
                }
            }
        }
        stateMachineParams.put(SwipeStatePlayer.PARAM_LOWER_CONDITION, (n != 0) ? Boolean.TRUE : Boolean.FALSE);
        stateMachineParams.put(SwipeStatePlayer.PARAM_ATTACKED, Boolean.TRUE);
    }
    
    private int calcDamageToVehicle(final int n, final int n2, final boolean b) {
        if (n <= 0) {
            return 0;
        }
        return PZMath.clamp((int)(n2 * PZMath.clamp(n / (b ? 10.0f : 40.0f), 0.0f, 1.0f)), 1, n2);
    }
    
    public static void splash(final IsoMovingObject isoMovingObject, final HandWeapon handWeapon, final IsoGameCharacter isoGameCharacter) {
        final IsoGameCharacter isoGameCharacter2 = (IsoGameCharacter)isoMovingObject;
        if (handWeapon != null && SandboxOptions.instance.BloodLevel.getValue() > 1) {
            int splatNumber = handWeapon.getSplatNumber();
            if (splatNumber < 1) {
                splatNumber = 1;
            }
            if (Core.bLastStand) {
                splatNumber *= 3;
            }
            switch (SandboxOptions.instance.BloodLevel.getValue()) {
                case 2: {
                    splatNumber /= 2;
                    break;
                }
                case 4: {
                    splatNumber *= 2;
                    break;
                }
                case 5: {
                    splatNumber *= 5;
                    break;
                }
            }
            for (int i = 0; i < splatNumber; ++i) {
                isoGameCharacter2.splatBlood(3, 0.3f);
            }
        }
        int n = 3;
        int n2 = 7;
        switch (SandboxOptions.instance.BloodLevel.getValue()) {
            case 1: {
                n2 = 0;
                break;
            }
            case 2: {
                n2 = 4;
                n = 5;
                break;
            }
            case 4: {
                n2 = 10;
                n = 2;
                break;
            }
            case 5: {
                n2 = 15;
                n = 0;
                break;
            }
        }
        if (SandboxOptions.instance.BloodLevel.getValue() > 1) {
            isoGameCharacter2.splatBloodFloorBig();
        }
        float n3 = 0.5f;
        if (isoGameCharacter2 instanceof IsoZombie && (((IsoZombie)isoGameCharacter2).bCrawling || isoGameCharacter2.getCurrentState() == ZombieOnGroundState.instance())) {
            n3 = 0.2f;
        }
        float n4 = Rand.Next(1.5f, 5.0f);
        float n5 = Rand.Next(1.5f, 5.0f);
        if (isoGameCharacter instanceof IsoPlayer && ((IsoPlayer)isoGameCharacter).bDoShove) {
            n4 = Rand.Next(0.0f, 0.5f);
            n5 = Rand.Next(0.0f, 0.5f);
        }
        if (n2 > 0) {
            isoGameCharacter2.playBloodSplatterSound();
        }
        for (int j = 0; j < n2; ++j) {
            if (Rand.Next(n) == 0) {
                new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, isoGameCharacter2.getCell(), isoGameCharacter2.getX(), isoGameCharacter2.getY(), isoGameCharacter2.getZ() + n3, isoGameCharacter2.getHitDir().x * n4, isoGameCharacter2.getHitDir().y * n5);
            }
        }
    }
    
    private boolean checkObjectHit(final IsoGameCharacter isoGameCharacter, final HandWeapon handWeapon, final IsoGridSquare isoGridSquare, final boolean b, final boolean b2) {
        if (isoGridSquare == null) {
            return false;
        }
        for (int i = isoGridSquare.getSpecialObjects().size() - 1; i >= 0; --i) {
            final IsoObject isoObject = isoGridSquare.getSpecialObjects().get(i);
            final IsoDoor isoDoor = Type.tryCastTo(isoObject, IsoDoor.class);
            final IsoThumpable isoThumpable = Type.tryCastTo(isoObject, IsoThumpable.class);
            final IsoWindow isoWindow = Type.tryCastTo(isoObject, IsoWindow.class);
            if (isoDoor != null && ((b && isoDoor.north) || (b2 && !isoDoor.north))) {
                final Thumpable thumpable = isoDoor.getThumpableFor(isoGameCharacter);
                if (thumpable != null) {
                    thumpable.WeaponHit(isoGameCharacter, handWeapon);
                    return true;
                }
            }
            if (isoThumpable != null) {
                if (isoThumpable.isDoor() || isoThumpable.isWindow() || !isoThumpable.isBlockAllTheSquare()) {
                    if ((b && isoThumpable.north) || (b2 && !isoThumpable.north)) {
                        final Thumpable thumpable2 = isoThumpable.getThumpableFor(isoGameCharacter);
                        if (thumpable2 != null) {
                            thumpable2.WeaponHit(isoGameCharacter, handWeapon);
                            return true;
                        }
                    }
                }
                else {
                    final Thumpable thumpable3 = isoThumpable.getThumpableFor(isoGameCharacter);
                    if (thumpable3 != null) {
                        thumpable3.WeaponHit(isoGameCharacter, handWeapon);
                        return true;
                    }
                }
            }
            if (isoWindow != null && ((b && isoWindow.north) || (b2 && !isoWindow.north))) {
                final Thumpable thumpable4 = isoWindow.getThumpableFor(isoGameCharacter);
                if (thumpable4 != null) {
                    thumpable4.WeaponHit(isoGameCharacter, handWeapon);
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean CheckObjectHit(final IsoGameCharacter isoGameCharacter, final HandWeapon handWeapon) {
        if (isoGameCharacter.isAimAtFloor()) {
            return this.bHitOnlyTree = false;
        }
        boolean b = false;
        int n = 0;
        int n2 = 0;
        final IsoDirections fromAngle = IsoDirections.fromAngle(isoGameCharacter.getForwardDirection());
        int n3 = 0;
        int n4 = 0;
        if (fromAngle == IsoDirections.NE || fromAngle == IsoDirections.N || fromAngle == IsoDirections.NW) {
            --n4;
        }
        if (fromAngle == IsoDirections.SE || fromAngle == IsoDirections.S || fromAngle == IsoDirections.SW) {
            ++n4;
        }
        if (fromAngle == IsoDirections.NW || fromAngle == IsoDirections.W || fromAngle == IsoDirections.SW) {
            --n3;
        }
        if (fromAngle == IsoDirections.NE || fromAngle == IsoDirections.E || fromAngle == IsoDirections.SE) {
            ++n3;
        }
        final IsoCell currentCell = IsoWorld.instance.CurrentCell;
        final IsoGridSquare currentSquare = isoGameCharacter.getCurrentSquare();
        final IsoGridSquare gridSquare = currentCell.getGridSquare(currentSquare.getX() + n3, currentSquare.getY() + n4, currentSquare.getZ());
        if (gridSquare != null) {
            if (this.checkObjectHit(isoGameCharacter, handWeapon, gridSquare, false, false)) {
                b = true;
                ++n;
            }
            if (!gridSquare.isBlockedTo(currentSquare)) {
                for (int i = 0; i < gridSquare.getObjects().size(); ++i) {
                    final IsoObject isoObject = gridSquare.getObjects().get(i);
                    if (isoObject instanceof IsoTree) {
                        ((IsoTree)isoObject).WeaponHit(isoGameCharacter, handWeapon);
                        b = true;
                        ++n;
                        ++n2;
                        if (isoObject.getObjectIndex() == -1) {
                            --i;
                        }
                    }
                }
            }
        }
        if ((fromAngle == IsoDirections.NE || fromAngle == IsoDirections.N || fromAngle == IsoDirections.NW) && this.checkObjectHit(isoGameCharacter, handWeapon, currentSquare, true, false)) {
            b = true;
            ++n;
        }
        if ((fromAngle == IsoDirections.SE || fromAngle == IsoDirections.S || fromAngle == IsoDirections.SW) && this.checkObjectHit(isoGameCharacter, handWeapon, currentCell.getGridSquare(currentSquare.getX(), currentSquare.getY() + 1, currentSquare.getZ()), true, false)) {
            b = true;
            ++n;
        }
        if ((fromAngle == IsoDirections.SE || fromAngle == IsoDirections.E || fromAngle == IsoDirections.NE) && this.checkObjectHit(isoGameCharacter, handWeapon, currentCell.getGridSquare(currentSquare.getX() + 1, currentSquare.getY(), currentSquare.getZ()), false, true)) {
            b = true;
            ++n;
        }
        if ((fromAngle == IsoDirections.NW || fromAngle == IsoDirections.W || fromAngle == IsoDirections.SW) && this.checkObjectHit(isoGameCharacter, handWeapon, currentSquare, false, true)) {
            b = true;
            ++n;
        }
        this.bHitOnlyTree = (b && n == n2);
        return b;
    }
    
    private LosUtil.TestResults los(int i, int j, final int n, final int n2, final int n3, final LOSVisitor losVisitor) {
        final IsoCell currentCell = IsoWorld.instance.CurrentCell;
        final int a = n2 - j;
        final int a2 = n - i;
        final int n4 = n3 - n3;
        final float n5 = 0.5f;
        final float n6 = 0.5f;
        IsoGridSquare gridSquare = currentCell.getGridSquare(i, j, n3);
        if (Math.abs(a2) > Math.abs(a)) {
            final float n7 = a / (float)a2;
            final float n8 = n4 / (float)a2;
            float n9 = n5 + j;
            float n10 = n6 + n3;
            final int n11 = (a2 < 0) ? -1 : 1;
            final float n12 = n7 * n11;
            final float n13 = n8 * n11;
            while (i != n) {
                i += n11;
                n9 += n12;
                n10 += n13;
                final IsoGridSquare gridSquare2 = currentCell.getGridSquare(i, (int)n9, (int)n10);
                if (losVisitor.visit(gridSquare2, gridSquare)) {
                    return losVisitor.getResult();
                }
                gridSquare = gridSquare2;
            }
        }
        else {
            final float n14 = a2 / (float)a;
            final float n15 = n4 / (float)a;
            float n16 = n5 + i;
            float n17 = n6 + n3;
            final int n18 = (a < 0) ? -1 : 1;
            final float n19 = n14 * n18;
            final float n20 = n15 * n18;
            while (j != n2) {
                j += n18;
                n16 += n19;
                n17 += n20;
                final IsoGridSquare gridSquare3 = currentCell.getGridSquare((int)n16, j, (int)n17);
                if (losVisitor.visit(gridSquare3, gridSquare)) {
                    return losVisitor.getResult();
                }
                gridSquare = gridSquare3;
            }
        }
        return LosUtil.TestResults.Clear;
    }
    
    private IsoWindow getWindowBetween(final int n, final int n2, final int n3, final int n4, final int n5) {
        this.windowVisitor.init();
        this.los(n, n2, n3, n4, n5, this.windowVisitor);
        return this.windowVisitor.window;
    }
    
    private IsoWindow getWindowBetween(final IsoMovingObject isoMovingObject, final IsoMovingObject isoMovingObject2) {
        return this.getWindowBetween((int)isoMovingObject.x, (int)isoMovingObject.y, (int)isoMovingObject2.x, (int)isoMovingObject2.y, (int)isoMovingObject.z);
    }
    
    private boolean isWindowBetween(final IsoMovingObject isoMovingObject, final IsoMovingObject isoMovingObject2) {
        return this.getWindowBetween(isoMovingObject, isoMovingObject2) != null;
    }
    
    private void smashWindowBetween(final IsoGameCharacter isoGameCharacter, final IsoMovingObject isoMovingObject, final HandWeapon handWeapon) {
        final IsoWindow windowBetween = this.getWindowBetween(isoGameCharacter, isoMovingObject);
        if (windowBetween == null) {
            return;
        }
        windowBetween.WeaponHit(isoGameCharacter, handWeapon);
    }
    
    public void changeWeapon(final HandWeapon handWeapon, final IsoGameCharacter isoGameCharacter) {
        if (handWeapon != null && handWeapon.isUseSelf()) {
            isoGameCharacter.getInventory().setDrawDirty(true);
            for (final InventoryItem inventoryItem : isoGameCharacter.getInventory().getItems()) {
                if (inventoryItem != handWeapon && inventoryItem instanceof HandWeapon && inventoryItem.getType() == handWeapon.getType() && inventoryItem.getCondition() > 0) {
                    if (isoGameCharacter.getPrimaryHandItem() == handWeapon && isoGameCharacter.getSecondaryHandItem() == handWeapon) {
                        isoGameCharacter.setPrimaryHandItem(inventoryItem);
                        isoGameCharacter.setSecondaryHandItem(inventoryItem);
                    }
                    else if (isoGameCharacter.getPrimaryHandItem() == handWeapon) {
                        isoGameCharacter.setPrimaryHandItem(inventoryItem);
                    }
                    else if (isoGameCharacter.getSecondaryHandItem() == handWeapon) {
                        isoGameCharacter.setSecondaryHandItem(inventoryItem);
                    }
                    return;
                }
            }
        }
        if (handWeapon == null || handWeapon.getCondition() <= 0 || handWeapon.isUseSelf()) {
            final HandWeapon handWeapon2 = (HandWeapon)isoGameCharacter.getInventory().getBestWeapon(isoGameCharacter.getDescriptor());
            isoGameCharacter.setPrimaryHandItem(null);
            if (isoGameCharacter.getSecondaryHandItem() == handWeapon) {
                isoGameCharacter.setSecondaryHandItem(null);
            }
            if (handWeapon2 != null && handWeapon2 != isoGameCharacter.getPrimaryHandItem() && handWeapon2.getCondition() > 0) {
                isoGameCharacter.setPrimaryHandItem(handWeapon2);
                if (handWeapon2.isTwoHandWeapon() && isoGameCharacter.getSecondaryHandItem() == null) {
                    isoGameCharacter.setSecondaryHandItem(handWeapon2);
                }
            }
        }
    }
    
    private void setParameterCharacterHitResult(final IsoGameCharacter isoGameCharacter, final IsoZombie isoZombie, final long n) {
        if (n == 0L) {
            return;
        }
        int n2 = 0;
        if (isoZombie != null) {
            if (isoZombie.isDead()) {
                n2 = 2;
            }
            else if (isoZombie.isKnockedDown()) {
                n2 = 1;
            }
        }
        isoGameCharacter.getEmitter().setParameterValue(n, FMODManager.instance.getParameterDescription("CharacterHitResult"), (float)n2);
    }
    
    static {
        _instance = new SwipeStatePlayer();
        PARAM_LOWER_CONDITION = 0;
        PARAM_ATTACKED = 1;
        HitList2 = new ArrayList<HitInfo>();
        tempVector2_1 = new Vector2();
        tempVector2_2 = new Vector2();
        Comparator = new CustomComparator();
        tempVector3_1 = new Vector3();
        tempVector3_2 = new Vector3();
        tempVectorBonePos = new Vector3();
        movingStatic = new ArrayList<IsoMovingObject>();
    }
    
    public static class CustomComparator implements Comparator<HitInfo>
    {
        @Override
        public int compare(final HitInfo hitInfo, final HitInfo hitInfo2) {
            final float distSq = hitInfo.distSq;
            final float distSq2 = hitInfo2.distSq;
            final IsoZombie isoZombie = Type.tryCastTo(hitInfo.getObject(), IsoZombie.class);
            final IsoZombie isoZombie2 = Type.tryCastTo(hitInfo2.getObject(), IsoZombie.class);
            if (isoZombie != null && isoZombie2 != null) {
                final boolean prone = SwipeStatePlayer.isProne(isoZombie);
                final boolean prone2 = SwipeStatePlayer.isProne(isoZombie2);
                final boolean currentState = isoZombie.isCurrentState(ZombieGetUpState.instance());
                final boolean currentState2 = isoZombie2.isCurrentState(ZombieGetUpState.instance());
                if (currentState && !currentState2 && prone2) {
                    return -1;
                }
                if (!currentState && prone && currentState2) {
                    return 1;
                }
                if (prone && prone2) {
                    if (isoZombie.isCrawling() && !isoZombie2.isCrawling()) {
                        return -1;
                    }
                    if (!isoZombie.isCrawling() && isoZombie2.isCrawling()) {
                        return 1;
                    }
                }
            }
            if (distSq > distSq2) {
                return 1;
            }
            if (distSq2 > distSq) {
                return -1;
            }
            return 0;
        }
    }
    
    private static final class WindowVisitor implements LOSVisitor
    {
        LosUtil.TestResults test;
        IsoWindow window;
        
        void init() {
            this.test = LosUtil.TestResults.Clear;
            this.window = null;
        }
        
        @Override
        public boolean visit(final IsoGridSquare isoGridSquare, final IsoGridSquare isoGridSquare2) {
            if (isoGridSquare == null || isoGridSquare2 == null) {
                return false;
            }
            final LosUtil.TestResults testVisionAdjacent = isoGridSquare.testVisionAdjacent(isoGridSquare2.getX() - isoGridSquare.getX(), isoGridSquare2.getY() - isoGridSquare.getY(), isoGridSquare2.getZ() - isoGridSquare.getZ(), true, false);
            if (testVisionAdjacent == LosUtil.TestResults.ClearThroughWindow) {
                final IsoWindow windowTo = isoGridSquare.getWindowTo(isoGridSquare2);
                if (this.isHittable(windowTo) && windowTo.TestVision(isoGridSquare, isoGridSquare2) == IsoObject.VisionResult.Unblocked) {
                    this.window = windowTo;
                    return true;
                }
            }
            if (testVisionAdjacent == LosUtil.TestResults.Blocked || this.test == LosUtil.TestResults.Clear || (testVisionAdjacent == LosUtil.TestResults.ClearThroughWindow && this.test == LosUtil.TestResults.ClearThroughOpenDoor)) {
                this.test = testVisionAdjacent;
            }
            else if (testVisionAdjacent == LosUtil.TestResults.ClearThroughClosedDoor && this.test == LosUtil.TestResults.ClearThroughOpenDoor) {
                this.test = testVisionAdjacent;
            }
            return this.test == LosUtil.TestResults.Blocked;
        }
        
        @Override
        public LosUtil.TestResults getResult() {
            return this.test;
        }
        
        boolean isHittable(final IsoWindow isoWindow) {
            return isoWindow != null && (isoWindow.isBarricaded() || (!isoWindow.isDestroyed() && !isoWindow.IsOpen()));
        }
    }
    
    private interface LOSVisitor
    {
        boolean visit(final IsoGridSquare p0, final IsoGridSquare p1);
        
        LosUtil.TestResults getResult();
    }
}

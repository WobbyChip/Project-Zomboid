// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import zombie.iso.LosUtil;
import zombie.characters.IsoPlayer;
import zombie.iso.Vector2;
import zombie.audio.parameters.ParameterZombieState;
import zombie.inventory.types.HandWeapon;
import zombie.network.GameClient;
import zombie.core.Rand;
import zombie.characters.skills.PerkFactory;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.iso.IsoMovingObject;
import zombie.network.GameServer;
import zombie.GameTime;
import zombie.iso.IsoObject;
import zombie.util.StringUtils;
import java.util.HashMap;
import zombie.characters.IsoZombie;
import zombie.characters.IsoGameCharacter;
import zombie.ai.State;

public final class AttackState extends State
{
    private static final AttackState s_instance;
    private static final String frontStr = "FRONT";
    private static final String backStr = "BEHIND";
    private static final String rightStr = "LEFT";
    private static final String leftStr = "RIGHT";
    
    public static AttackState instance() {
        return AttackState.s_instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        final IsoZombie isoZombie = (IsoZombie)isoGameCharacter;
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        stateMachineParams.clear();
        stateMachineParams.put(0, Boolean.FALSE);
        isoGameCharacter.setVariable("AttackOutcome", "start");
        isoGameCharacter.clearVariable("AttackDidDamage");
        isoGameCharacter.clearVariable("ZombieBiteDone");
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        final IsoZombie lastTargettedBy = (IsoZombie)isoGameCharacter;
        final IsoGameCharacter isoGameCharacter2 = (IsoGameCharacter)lastTargettedBy.target;
        if (isoGameCharacter2 != null && "Chainsaw".equals(isoGameCharacter2.getVariableString("ZombieHitReaction"))) {
            return;
        }
        String variableString = isoGameCharacter.getVariableString("AttackOutcome");
        if ("success".equals(variableString) && isoGameCharacter.getVariableBoolean("bAttack") && isoGameCharacter.isVariable("targethitreaction", "EndDeath")) {
            variableString = "enddeath";
            isoGameCharacter.setVariable("AttackOutcome", variableString);
        }
        if ("success".equals(variableString) && !isoGameCharacter.getVariableBoolean("bAttack") && !isoGameCharacter.getVariableBoolean("AttackDidDamage") && isoGameCharacter.getVariableString("ZombieBiteDone") == null) {
            isoGameCharacter.setVariable("AttackOutcome", "interrupted");
        }
        if (isoGameCharacter2 == null || isoGameCharacter2.isDead()) {
            lastTargettedBy.setTargetSeenTime(10.0f);
        }
        if (isoGameCharacter2 != null && stateMachineParams.get(0) == Boolean.FALSE && !"started".equals(variableString) && !StringUtils.isNullOrEmpty(isoGameCharacter.getVariableString("PlayerHitReaction"))) {
            stateMachineParams.put(0, Boolean.TRUE);
            isoGameCharacter2.testDefense(lastTargettedBy);
        }
        lastTargettedBy.setShootable(true);
        if (lastTargettedBy.target != null && !lastTargettedBy.bCrawling) {
            if (!"fail".equals(variableString) && !"interrupted".equals(variableString)) {
                lastTargettedBy.faceThisObject(lastTargettedBy.target);
            }
            lastTargettedBy.setOnFloor(false);
        }
        final boolean b = lastTargettedBy.speedType == 1;
        if (lastTargettedBy.target != null && b && ("start".equals(variableString) || "success".equals(variableString))) {
            final IsoGameCharacter isoGameCharacter3 = (IsoGameCharacter)lastTargettedBy.target;
            final float slowFactor = isoGameCharacter3.getSlowFactor();
            if (isoGameCharacter3.getSlowFactor() <= 0.0f) {
                isoGameCharacter3.setSlowTimer(30.0f);
            }
            isoGameCharacter3.setSlowTimer(isoGameCharacter3.getSlowTimer() + GameTime.instance.getMultiplier());
            if (isoGameCharacter3.getSlowTimer() > 60.0f) {
                isoGameCharacter3.setSlowTimer(60.0f);
            }
            isoGameCharacter3.setSlowFactor(isoGameCharacter3.getSlowFactor() + 0.03f);
            if (isoGameCharacter3.getSlowFactor() >= 0.5f) {
                isoGameCharacter3.setSlowFactor(0.5f);
            }
            if (GameServer.bServer && slowFactor != isoGameCharacter3.getSlowFactor()) {
                GameServer.sendSlowFactor(isoGameCharacter3);
            }
        }
        if (lastTargettedBy.target != null) {
            lastTargettedBy.target.setTimeSinceZombieAttack(0);
            lastTargettedBy.target.setLastTargettedBy(lastTargettedBy);
        }
        if (!lastTargettedBy.bCrawling) {
            lastTargettedBy.setVariable("AttackType", "bite");
        }
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
        final IsoZombie isoZombie = (IsoZombie)isoGameCharacter;
        isoGameCharacter.clearVariable("AttackOutcome");
        isoGameCharacter.clearVariable("AttackType");
        isoGameCharacter.clearVariable("PlayerHitReaction");
        isoGameCharacter.setStateMachineLocked(false);
        if (isoZombie.target != null && isoZombie.target.isOnFloor()) {
            isoZombie.setEatBodyTarget(isoZombie.target, true);
            isoZombie.setTarget(null);
        }
        isoZombie.AllowRepathDelay = 0.0f;
    }
    
    @Override
    public void animEvent(final IsoGameCharacter isoGameCharacter, final AnimEvent animEvent) {
        final IsoZombie isoZombie = (IsoZombie)isoGameCharacter;
        if (animEvent.m_EventName.equalsIgnoreCase("SetAttackOutcome")) {
            if (isoZombie.getVariableBoolean("bAttack")) {
                isoZombie.setVariable("AttackOutcome", "success");
            }
            else {
                isoZombie.setVariable("AttackOutcome", "fail");
            }
        }
        if (animEvent.m_EventName.equalsIgnoreCase("AttackCollisionCheck") && !isoZombie.isNoTeeth()) {
            final IsoGameCharacter isoGameCharacter2 = (IsoGameCharacter)isoZombie.target;
            if (isoGameCharacter2 == null) {
                return;
            }
            isoGameCharacter2.setHitFromBehind(isoZombie.isBehind(isoGameCharacter2));
            if (isoGameCharacter2.testDotSide(isoZombie).equals("FRONT") && !isoGameCharacter2.isAimAtFloor() && !StringUtils.isNullOrEmpty(isoGameCharacter2.getVariableString("AttackType"))) {
                return;
            }
            if ("KnifeDeath".equals(isoGameCharacter2.getVariableString("ZombieHitReaction")) && Rand.NextBool(Math.max(0, 9 - (isoGameCharacter2.getPerkLevel(PerkFactory.Perks.SmallBlade) + 1) * 2))) {
                return;
            }
            this.triggerPlayerReaction(isoGameCharacter.getVariableString("PlayerHitReaction"), isoGameCharacter);
            final Vector2 hitDir = isoZombie.getHitDir();
            hitDir.x = isoZombie.getX();
            hitDir.y = isoZombie.getY();
            final Vector2 vector2 = hitDir;
            vector2.x -= isoGameCharacter2.getX();
            final Vector2 vector3 = hitDir;
            vector3.y -= isoGameCharacter2.getY();
            hitDir.normalize();
            if (GameClient.bClient && !isoZombie.isRemoteZombie()) {
                GameClient.sendHitCharacter(isoZombie, isoGameCharacter2, null, 0.0f, false, 1.0f, false, false, false);
            }
        }
        if (animEvent.m_EventName.equalsIgnoreCase("EatBody")) {
            isoGameCharacter.setVariable("EatingStarted", true);
            ((IsoZombie)isoGameCharacter).setEatBodyTarget(((IsoZombie)isoGameCharacter).target, true);
            ((IsoZombie)isoGameCharacter).setTarget(null);
        }
        if (animEvent.m_EventName.equalsIgnoreCase("SetState")) {
            isoZombie.parameterZombieState.setState(ParameterZombieState.State.Attack);
        }
    }
    
    @Override
    public boolean isAttacking(final IsoGameCharacter isoGameCharacter) {
        return true;
    }
    
    private void triggerPlayerReaction(String s, final IsoGameCharacter isoGameCharacter) {
        final IsoZombie attackedBy = (IsoZombie)isoGameCharacter;
        final IsoGameCharacter isoGameCharacter2 = (IsoGameCharacter)attackedBy.target;
        if (isoGameCharacter2 == null) {
            return;
        }
        if (attackedBy.DistTo(isoGameCharacter2) > 1.0f && !attackedBy.bCrawling) {
            return;
        }
        if ((attackedBy.isFakeDead() || attackedBy.bCrawling) && attackedBy.DistTo(isoGameCharacter2) > 1.3f) {
            return;
        }
        if ((isoGameCharacter2.isDead() && !isoGameCharacter2.getHitReaction().equals("EndDeath")) || isoGameCharacter2.isOnFloor()) {
            attackedBy.setEatBodyTarget(isoGameCharacter2, true);
            return;
        }
        if (isoGameCharacter2.isDead()) {
            return;
        }
        isoGameCharacter2.setHitFromBehind(attackedBy.isBehind(isoGameCharacter2));
        final String testDotSide = isoGameCharacter2.testDotSide(attackedBy);
        final boolean equals = testDotSide.equals("FRONT");
        final boolean equals2 = testDotSide.equals("BEHIND");
        if (testDotSide.equals("RIGHT")) {
            s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
        }
        if (testDotSide.equals("LEFT")) {
            s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
        }
        if (((IsoPlayer)isoGameCharacter2).bDoShove && equals && !isoGameCharacter2.isAimAtFloor()) {
            return;
        }
        if (((IsoPlayer)isoGameCharacter2).bDoShove && !equals && !equals2 && Rand.Next(100) > 75) {
            return;
        }
        if (Math.abs(attackedBy.z - isoGameCharacter2.z) >= 0.2f) {
            return;
        }
        final LosUtil.TestResults lineClear = LosUtil.lineClear(attackedBy.getCell(), (int)attackedBy.getX(), (int)attackedBy.getY(), (int)attackedBy.getZ(), (int)isoGameCharacter2.getX(), (int)isoGameCharacter2.getY(), (int)isoGameCharacter2.getZ(), false);
        if (lineClear == LosUtil.TestResults.Blocked || lineClear == LosUtil.TestResults.ClearThroughClosedDoor) {
            return;
        }
        if (isoGameCharacter2.getSquare().isSomethingTo(attackedBy.getCurrentSquare())) {
            return;
        }
        isoGameCharacter2.setAttackedBy(attackedBy);
        boolean addRandomDamageFromZombie = false;
        if ((!GameClient.bClient && !GameServer.bServer) || (GameClient.bClient && !attackedBy.isRemoteZombie())) {
            addRandomDamageFromZombie = isoGameCharacter2.getBodyDamage().AddRandomDamageFromZombie(attackedBy, s);
        }
        isoGameCharacter.setVariable("AttackDidDamage", addRandomDamageFromZombie);
        isoGameCharacter2.getBodyDamage().Update();
        if (isoGameCharacter2.isDead()) {
            isoGameCharacter2.setHealth(0.0f);
            attackedBy.setEatBodyTarget(isoGameCharacter2, true);
            attackedBy.setTarget(null);
        }
        else if (isoGameCharacter2.isAsleep()) {
            if (GameServer.bServer) {
                isoGameCharacter2.sendObjectChange("wakeUp");
            }
            else {
                isoGameCharacter2.forceAwake();
            }
        }
    }
    
    static {
        s_instance = new AttackState();
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import zombie.audio.parameters.ParameterZombieState;
import zombie.characters.IsoPlayer;
import zombie.network.GameClient;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.util.StringUtils;
import java.util.HashMap;
import zombie.characters.IsoZombie;
import zombie.characters.IsoGameCharacter;
import zombie.ai.State;

public class AttackNetworkState extends State
{
    private static final AttackNetworkState s_instance;
    private String attackOutcome;
    
    public static AttackNetworkState instance() {
        return AttackNetworkState.s_instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        final IsoZombie isoZombie = (IsoZombie)isoGameCharacter;
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        stateMachineParams.clear();
        stateMachineParams.put(0, Boolean.FALSE);
        this.attackOutcome = isoGameCharacter.getVariableString("AttackOutcome");
        isoGameCharacter.setVariable("AttackOutcome", "start");
        isoGameCharacter.clearVariable("AttackDidDamage");
        isoGameCharacter.clearVariable("ZombieBiteDone");
        isoZombie.setTargetSeenTime(1.0f);
        if (!isoZombie.bCrawling) {
            isoZombie.setVariable("AttackType", "bite");
        }
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
        final IsoZombie lastTargettedBy = (IsoZombie)isoGameCharacter;
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        final IsoGameCharacter isoGameCharacter2 = (IsoGameCharacter)lastTargettedBy.target;
        if (isoGameCharacter2 != null && "Chainsaw".equals(isoGameCharacter2.getVariableString("ZombieHitReaction"))) {
            return;
        }
        final String variableString = isoGameCharacter.getVariableString("AttackOutcome");
        if ("success".equals(variableString) && !isoGameCharacter.getVariableBoolean("bAttack") && (isoGameCharacter2 == null || !isoGameCharacter2.isGodMod()) && !isoGameCharacter.getVariableBoolean("AttackDidDamage") && isoGameCharacter.getVariableString("ZombieBiteDone") != "true") {
            isoGameCharacter.setVariable("AttackOutcome", "interrupted");
        }
        if (isoGameCharacter2 == null || isoGameCharacter2.isDead()) {
            lastTargettedBy.setTargetSeenTime(10.0f);
        }
        if (isoGameCharacter2 != null && stateMachineParams.get(0) == Boolean.FALSE && !"started".equals(variableString) && !StringUtils.isNullOrEmpty(isoGameCharacter.getVariableString("PlayerHitReaction"))) {
            stateMachineParams.put(0, Boolean.TRUE);
        }
        lastTargettedBy.setShootable(true);
        if (lastTargettedBy.target != null && !lastTargettedBy.bCrawling) {
            if (!"fail".equals(variableString) && !"interrupted".equals(variableString)) {
                lastTargettedBy.faceThisObject(lastTargettedBy.target);
            }
            lastTargettedBy.setOnFloor(false);
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
        if (GameClient.bClient && isoZombie.isRemoteZombie()) {
            if (animEvent.m_EventName.equalsIgnoreCase("SetAttackOutcome")) {
                isoZombie.setVariable("AttackOutcome", "fail".equals(this.attackOutcome) ? "fail" : "success");
            }
            if (animEvent.m_EventName.equalsIgnoreCase("AttackCollisionCheck") && isoZombie.target instanceof IsoPlayer) {
                final IsoPlayer isoPlayer = (IsoPlayer)isoZombie.target;
                if (isoZombie.scratch) {
                    isoZombie.getEmitter().playSoundImpl("ZombieScratch", isoZombie);
                }
                else if (isoZombie.laceration) {
                    isoZombie.getEmitter().playSoundImpl("ZombieScratch", isoZombie);
                }
                else {
                    isoZombie.getEmitter().playSoundImpl("ZombieBite", isoZombie);
                    isoPlayer.splatBloodFloorBig();
                    isoPlayer.splatBloodFloorBig();
                    isoPlayer.splatBloodFloorBig();
                }
            }
            if (animEvent.m_EventName.equalsIgnoreCase("EatBody")) {
                isoGameCharacter.setVariable("EatingStarted", true);
                ((IsoZombie)isoGameCharacter).setEatBodyTarget(((IsoZombie)isoGameCharacter).target, true);
                ((IsoZombie)isoGameCharacter).setTarget(null);
            }
        }
        if (animEvent.m_EventName.equalsIgnoreCase("SetState")) {
            isoZombie.parameterZombieState.setState(ParameterZombieState.State.Attack);
        }
    }
    
    @Override
    public boolean isAttacking(final IsoGameCharacter isoGameCharacter) {
        return true;
    }
    
    static {
        s_instance = new AttackNetworkState();
    }
}

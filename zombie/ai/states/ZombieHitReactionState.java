// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import zombie.audio.parameters.ParameterZombieState;
import zombie.iso.objects.IsoZombieGiblets;
import zombie.core.Rand;
import zombie.characterTextures.BloodBodyPartType;
import zombie.network.GameClient;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoDirections;
import zombie.GameTime;
import java.util.HashMap;
import zombie.characters.IsoZombie;
import zombie.characters.IsoGameCharacter;
import zombie.ai.State;

public final class ZombieHitReactionState extends State
{
    private static final ZombieHitReactionState _instance;
    private static final int TURN_TO_PLAYER = 1;
    private static final int HIT_REACTION_TIMER = 2;
    
    public static ZombieHitReactionState instance() {
        return ZombieHitReactionState._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        final IsoZombie isoZombie = (IsoZombie)isoGameCharacter;
        isoZombie.collideWhileHit = true;
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        stateMachineParams.put(1, Boolean.FALSE);
        stateMachineParams.put(2, 0.0f);
        isoGameCharacter.clearVariable("onknees");
        if (isoZombie.isSitAgainstWall()) {
            isoGameCharacter.setHitReaction(null);
        }
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        isoGameCharacter.setOnFloor(((IsoZombie)isoGameCharacter).isKnockedDown());
        stateMachineParams.put(2, (float)stateMachineParams.get(2) + GameTime.getInstance().getMultiplier());
        if (stateMachineParams.get(1) == Boolean.TRUE) {
            if (!isoGameCharacter.isHitFromBehind()) {
                isoGameCharacter.setDir(IsoDirections.reverse(IsoDirections.fromAngle(isoGameCharacter.getHitDir())));
            }
            else {
                isoGameCharacter.setDir(IsoDirections.fromAngle(isoGameCharacter.getHitDir()));
            }
        }
        else if (isoGameCharacter.hasAnimationPlayer()) {
            isoGameCharacter.getAnimationPlayer().setTargetToAngle();
        }
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
        final IsoZombie isoZombie = (IsoZombie)isoGameCharacter;
        isoZombie.collideWhileHit = true;
        if (isoZombie.target != null) {
            isoZombie.AllowRepathDelay = 0.0f;
            isoZombie.spotted(isoZombie.target, true);
        }
        isoZombie.setStaggerBack(false);
        isoZombie.setHitReaction("");
        isoZombie.setEatBodyTarget(null, false);
        isoZombie.setSitAgainstWall(false);
        isoZombie.setShootable(true);
    }
    
    @Override
    public void animEvent(final IsoGameCharacter isoGameCharacter, final AnimEvent animEvent) {
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        final IsoZombie isoZombie = (IsoZombie)isoGameCharacter;
        if (animEvent.m_EventName.equalsIgnoreCase("DoDeath") && Boolean.parseBoolean(animEvent.m_ParameterValue) && isoGameCharacter.isAlive()) {
            isoGameCharacter.Kill(isoGameCharacter.getAttackedBy());
            if (GameClient.bClient) {
                GameClient.sendKillZombie(isoZombie);
            }
        }
        if (animEvent.m_EventName.equalsIgnoreCase("PlayDeathSound")) {
            isoGameCharacter.setDoDeathSound(false);
            isoGameCharacter.playDeadSound();
        }
        if (animEvent.m_EventName.equalsIgnoreCase("FallOnFront")) {
            isoGameCharacter.setFallOnFront(Boolean.parseBoolean(animEvent.m_ParameterValue));
        }
        if (animEvent.m_EventName.equalsIgnoreCase("ActiveAnimFinishing")) {}
        if (animEvent.m_EventName.equalsIgnoreCase("Collide") && ((IsoZombie)isoGameCharacter).speedType == 1) {
            ((IsoZombie)isoGameCharacter).collideWhileHit = false;
        }
        if (animEvent.m_EventName.equalsIgnoreCase("ZombieTurnToPlayer")) {
            stateMachineParams.put(1, Boolean.parseBoolean(animEvent.m_ParameterValue) ? Boolean.TRUE : Boolean.FALSE);
        }
        if (animEvent.m_EventName.equalsIgnoreCase("CancelKnockDown") && Boolean.parseBoolean(animEvent.m_ParameterValue)) {
            ((IsoZombie)isoGameCharacter).setKnockedDown(false);
        }
        if (animEvent.m_EventName.equalsIgnoreCase("KnockDown")) {
            isoGameCharacter.setOnFloor(true);
            ((IsoZombie)isoGameCharacter).setKnockedDown(true);
        }
        if (animEvent.m_EventName.equalsIgnoreCase("SplatBlood")) {
            isoZombie.addBlood(null, true, false, false);
            isoZombie.addBlood(null, true, false, false);
            isoZombie.addBlood(null, true, false, false);
            isoZombie.playBloodSplatterSound();
            for (int i = 0; i < 10; ++i) {
                isoZombie.getCurrentSquare().getChunk().addBloodSplat(isoZombie.x + Rand.Next(-0.5f, 0.5f), isoZombie.y + Rand.Next(-0.5f, 0.5f), isoZombie.z, Rand.Next(8));
                if (Rand.Next(5) == 0) {
                    new IsoZombieGiblets(IsoZombieGiblets.GibletType.B, isoZombie.getCell(), isoZombie.getX(), isoZombie.getY(), isoZombie.getZ() + 0.3f, Rand.Next(-0.2f, 0.2f) * 1.5f, Rand.Next(-0.2f, 0.2f) * 1.5f);
                }
                else {
                    new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, isoZombie.getCell(), isoZombie.getX(), isoZombie.getY(), isoZombie.getZ() + 0.3f, Rand.Next(-0.2f, 0.2f) * 1.5f, Rand.Next(-0.2f, 0.2f) * 1.5f);
                }
            }
        }
        if (animEvent.m_EventName.equalsIgnoreCase("SetState") && !isoZombie.isDead()) {
            if (isoZombie.getAttackedBy() != null && isoZombie.getAttackedBy().getVehicle() != null && "Floor".equals(isoZombie.getHitReaction())) {
                isoZombie.parameterZombieState.setState(ParameterZombieState.State.RunOver);
                return;
            }
            isoZombie.parameterZombieState.setState(ParameterZombieState.State.Hit);
        }
    }
    
    static {
        _instance = new ZombieHitReactionState();
    }
}

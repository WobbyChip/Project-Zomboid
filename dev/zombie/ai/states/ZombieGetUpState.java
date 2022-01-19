// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.util.StringUtils;
import java.util.HashMap;
import zombie.network.GameClient;
import zombie.audio.parameters.ParameterZombieState;
import zombie.characters.IsoZombie;
import zombie.characters.IsoGameCharacter;
import zombie.ai.State;

public final class ZombieGetUpState extends State
{
    private static final ZombieGetUpState _instance;
    static final Integer PARAM_STANDING;
    static final Integer PARAM_PREV_STATE;
    
    public static ZombieGetUpState instance() {
        return ZombieGetUpState._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        final IsoZombie isoZombie = (IsoZombie)isoGameCharacter;
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        stateMachineParams.put(ZombieGetUpState.PARAM_STANDING, Boolean.FALSE);
        State previous = isoGameCharacter.getStateMachine().getPrevious();
        if (previous == ZombieGetUpFromCrawlState.instance()) {
            previous = isoGameCharacter.getStateMachineParams(ZombieGetUpFromCrawlState.instance()).get(1);
        }
        stateMachineParams.put(ZombieGetUpState.PARAM_PREV_STATE, previous);
        isoZombie.parameterZombieState.setState(ParameterZombieState.State.GettingUp);
        if (GameClient.bClient) {
            isoGameCharacter.setKnockedDown(false);
        }
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
        final boolean b = isoGameCharacter.getStateMachineParams(this).get(ZombieGetUpState.PARAM_STANDING) == Boolean.TRUE;
        isoGameCharacter.setOnFloor(!b);
        ((IsoZombie)isoGameCharacter).setKnockedDown(!b);
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        final IsoZombie isoZombie = (IsoZombie)isoGameCharacter;
        isoGameCharacter.setCollidable(true);
        isoGameCharacter.clearVariable("SprinterTripped");
        isoGameCharacter.clearVariable("ShouldStandUp");
        if (StringUtils.isNullOrEmpty(isoGameCharacter.getHitReaction())) {
            isoZombie.setSitAgainstWall(false);
        }
        isoZombie.setKnockedDown(false);
        isoZombie.AllowRepathDelay = 0.0f;
        if (stateMachineParams.get(ZombieGetUpState.PARAM_PREV_STATE) == PathFindState.instance()) {
            if (isoGameCharacter.getPathFindBehavior2().getTargetChar() == null) {
                isoGameCharacter.setVariable("bPathfind", true);
                isoGameCharacter.setVariable("bMoving", false);
            }
            else if (isoZombie.isTargetLocationKnown()) {
                isoGameCharacter.pathToCharacter(isoGameCharacter.getPathFindBehavior2().getTargetChar());
            }
            else if (isoZombie.LastTargetSeenX != -1) {
                isoGameCharacter.pathToLocation(isoZombie.LastTargetSeenX, isoZombie.LastTargetSeenY, isoZombie.LastTargetSeenZ);
            }
        }
        else if (stateMachineParams.get(ZombieGetUpState.PARAM_PREV_STATE) == WalkTowardState.instance()) {
            isoGameCharacter.setVariable("bPathFind", false);
            isoGameCharacter.setVariable("bMoving", true);
        }
    }
    
    @Override
    public void animEvent(final IsoGameCharacter isoGameCharacter, final AnimEvent animEvent) {
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        final IsoZombie isoZombie = (IsoZombie)isoGameCharacter;
        if (animEvent.m_EventName.equalsIgnoreCase("IsAlmostUp")) {
            stateMachineParams.put(ZombieGetUpState.PARAM_STANDING, Boolean.TRUE);
        }
    }
    
    static {
        _instance = new ZombieGetUpState();
        PARAM_STANDING = 1;
        PARAM_PREV_STATE = 2;
    }
}

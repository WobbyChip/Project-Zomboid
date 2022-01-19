// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import zombie.core.Rand;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.characters.IsoZombie;
import java.util.HashMap;
import zombie.characters.IsoGameCharacter;
import zombie.ai.State;

public final class ZombieGetDownState extends State
{
    private static final ZombieGetDownState _instance;
    static final Integer PARAM_PREV_STATE;
    static final Integer PARAM_WAIT_TIME;
    static final Integer PARAM_START_X;
    static final Integer PARAM_START_Y;
    
    public static ZombieGetDownState instance() {
        return ZombieGetDownState._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        stateMachineParams.put(ZombieGetDownState.PARAM_PREV_STATE, isoGameCharacter.getStateMachine().getPrevious());
        stateMachineParams.put(ZombieGetDownState.PARAM_START_X, isoGameCharacter.getX());
        stateMachineParams.put(ZombieGetDownState.PARAM_START_Y, isoGameCharacter.getY());
        isoGameCharacter.setStateEventDelayTimer((float)stateMachineParams.get(ZombieGetDownState.PARAM_WAIT_TIME));
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.getStateMachineParams(this);
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        final IsoZombie isoZombie = (IsoZombie)isoGameCharacter;
        isoZombie.setStateEventDelayTimer(0.0f);
        isoZombie.AllowRepathDelay = 0.0f;
        if (stateMachineParams.get(ZombieGetDownState.PARAM_PREV_STATE) == PathFindState.instance()) {
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
        else if (stateMachineParams.get(ZombieGetDownState.PARAM_PREV_STATE) == WalkTowardState.instance()) {
            isoGameCharacter.setVariable("bPathFind", false);
            isoGameCharacter.setVariable("bMoving", true);
        }
    }
    
    @Override
    public void animEvent(final IsoGameCharacter isoGameCharacter, final AnimEvent animEvent) {
        final IsoZombie isoZombie = (IsoZombie)isoGameCharacter;
        if (animEvent.m_EventName.equalsIgnoreCase("StartCrawling") && !isoZombie.isCrawling()) {
            isoZombie.toggleCrawling();
        }
    }
    
    public boolean isNearStartXY(final IsoGameCharacter isoGameCharacter) {
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        final Float n = stateMachineParams.get(ZombieGetDownState.PARAM_START_X);
        final Float n2 = stateMachineParams.get(ZombieGetDownState.PARAM_START_Y);
        return n != null && n2 != null && isoGameCharacter.DistToSquared(n, n2) <= 0.25f;
    }
    
    public void setParams(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.getStateMachineParams(this).put(ZombieGetDownState.PARAM_WAIT_TIME, Rand.Next(60.0f, 150.0f));
    }
    
    static {
        _instance = new ZombieGetDownState();
        PARAM_PREV_STATE = 1;
        PARAM_WAIT_TIME = 2;
        PARAM_START_X = 3;
        PARAM_START_Y = 4;
    }
}

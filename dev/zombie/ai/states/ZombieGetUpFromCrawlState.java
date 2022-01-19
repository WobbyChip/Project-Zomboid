// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import java.util.HashMap;
import zombie.characters.IsoZombie;
import zombie.characters.IsoGameCharacter;
import zombie.ai.State;

public final class ZombieGetUpFromCrawlState extends State
{
    private static final ZombieGetUpFromCrawlState _instance;
    
    public static ZombieGetUpFromCrawlState instance() {
        return ZombieGetUpFromCrawlState._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        final IsoZombie isoZombie = (IsoZombie)isoGameCharacter;
        stateMachineParams.put(1, isoGameCharacter.getStateMachine().getPrevious());
        if (isoZombie.isCrawling()) {
            isoZombie.toggleCrawling();
            isoZombie.setOnFloor(true);
        }
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        final IsoZombie isoZombie = (IsoZombie)isoGameCharacter;
        isoZombie.AllowRepathDelay = 0.0f;
        if (stateMachineParams.get(1) == PathFindState.instance()) {
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
    }
    
    @Override
    public void animEvent(final IsoGameCharacter isoGameCharacter, final AnimEvent animEvent) {
    }
    
    static {
        _instance = new ZombieGetUpFromCrawlState();
    }
}

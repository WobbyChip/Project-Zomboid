// 
// Decompiled by Procyon v0.5.36
// 

package zombie.vehicles;

import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.ai.astar.AStarPathFinder;
import zombie.ai.astar.Mover;
import zombie.iso.IsoWorld;
import zombie.network.ServerMap;
import zombie.network.GameServer;
import zombie.audio.parameters.ParameterZombieState;
import java.util.HashMap;
import zombie.gameStates.IngameState;
import zombie.characters.IsoZombie;
import zombie.characters.IsoGameCharacter;
import zombie.ai.State;

public final class PathFindState2 extends State
{
    private static final Integer PARAM_TICK_COUNT;
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        isoGameCharacter.setVariable("bPathfind", true);
        isoGameCharacter.setVariable("bMoving", false);
        ((IsoZombie)isoGameCharacter).networkAI.extraUpdate();
        stateMachineParams.put(PathFindState2.PARAM_TICK_COUNT, IngameState.instance.numberTicks);
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        final PathFindBehavior2.BehaviorResult update = isoGameCharacter.getPathFindBehavior2().update();
        if (update == PathFindBehavior2.BehaviorResult.Failed) {
            isoGameCharacter.setPathFindIndex(-1);
            isoGameCharacter.setVariable("bPathfind", false);
            isoGameCharacter.setVariable("bMoving", false);
            return;
        }
        if (update != PathFindBehavior2.BehaviorResult.Succeeded) {
            if (isoGameCharacter instanceof IsoZombie && IngameState.instance.numberTicks - (long)stateMachineParams.get(PathFindState2.PARAM_TICK_COUNT) == 2L) {
                ((IsoZombie)isoGameCharacter).parameterZombieState.setState(ParameterZombieState.State.Idle);
            }
            return;
        }
        final int n = (int)isoGameCharacter.getPathFindBehavior2().getTargetX();
        final int n2 = (int)isoGameCharacter.getPathFindBehavior2().getTargetY();
        if ((GameServer.bServer ? ServerMap.instance.getChunk(n / 10, n2 / 10) : IsoWorld.instance.CurrentCell.getChunkForGridSquare(n, n2, 0)) == null) {
            isoGameCharacter.setVariable("bPathfind", false);
            isoGameCharacter.setVariable("bMoving", true);
            return;
        }
        isoGameCharacter.setVariable("bPathfind", false);
        isoGameCharacter.setVariable("bMoving", false);
        isoGameCharacter.setPath2(null);
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
        if (isoGameCharacter instanceof IsoZombie) {
            ((IsoZombie)isoGameCharacter).networkAI.extraUpdate();
            ((IsoZombie)isoGameCharacter).AllowRepathDelay = 0.0f;
        }
        isoGameCharacter.setVariable("bPathfind", false);
        isoGameCharacter.setVariable("bMoving", false);
        isoGameCharacter.setVariable("ShouldBeCrawling", false);
        PolygonalMap2.instance.cancelRequest(isoGameCharacter);
        isoGameCharacter.getFinder().progress = AStarPathFinder.PathFindProgress.notrunning;
        isoGameCharacter.setPath2(null);
    }
    
    @Override
    public void animEvent(final IsoGameCharacter isoGameCharacter, final AnimEvent animEvent) {
    }
    
    @Override
    public boolean isMoving(final IsoGameCharacter isoGameCharacter) {
        return isoGameCharacter.isMoving();
    }
    
    static {
        PARAM_TICK_COUNT = 0;
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import zombie.audio.parameters.ParameterZombieState;
import zombie.iso.IsoWorld;
import zombie.network.ServerMap;
import zombie.network.GameServer;
import zombie.vehicles.PathFindBehavior2;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoUtils;
import zombie.vehicles.PolygonalMap2;
import zombie.network.NetworkVariables;
import zombie.characters.IsoZombie;
import zombie.gameStates.IngameState;
import zombie.characters.IsoGameCharacter;
import zombie.ai.State;

public class WalkTowardNetworkState extends State
{
    static WalkTowardNetworkState _instance;
    private static final Integer PARAM_TICK_COUNT;
    
    public static WalkTowardNetworkState instance() {
        return WalkTowardNetworkState._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.getStateMachineParams(this).put(WalkTowardNetworkState.PARAM_TICK_COUNT, IngameState.instance.numberTicks);
        isoGameCharacter.setVariable("bMoving", true);
        isoGameCharacter.setVariable("bPathfind", false);
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
        final IsoZombie isoZombie = (IsoZombie)isoGameCharacter;
        final PathFindBehavior2 pathFindBehavior2 = isoZombie.getPathFindBehavior2();
        isoZombie.vectorToTarget.x = isoZombie.networkAI.targetX - isoZombie.x;
        isoZombie.vectorToTarget.y = isoZombie.networkAI.targetY - isoZombie.y;
        pathFindBehavior2.walkingOnTheSpot.reset(isoZombie.x, isoZombie.y);
        if (isoZombie.z == isoZombie.networkAI.targetZ && (isoZombie.networkAI.predictionType == NetworkVariables.PredictionTypes.Thump || isoZombie.networkAI.predictionType == NetworkVariables.PredictionTypes.Climb)) {
            if (isoZombie.networkAI.usePathFind) {
                pathFindBehavior2.reset();
                isoZombie.setPath2(null);
                isoZombie.networkAI.usePathFind = false;
            }
            pathFindBehavior2.moveToPoint(isoZombie.networkAI.targetX, isoZombie.networkAI.targetY, 1.0f);
            isoZombie.setVariable("bMoving", IsoUtils.DistanceManhatten(isoZombie.networkAI.targetX, isoZombie.networkAI.targetY, isoZombie.nx, isoZombie.ny) > 0.5f);
        }
        else if (isoZombie.z == isoZombie.networkAI.targetZ && !PolygonalMap2.instance.lineClearCollide(isoZombie.x, isoZombie.y, isoZombie.networkAI.targetX, isoZombie.networkAI.targetY, isoZombie.networkAI.targetZ, null)) {
            if (isoZombie.networkAI.usePathFind) {
                pathFindBehavior2.reset();
                isoZombie.setPath2(null);
                isoZombie.networkAI.usePathFind = false;
            }
            pathFindBehavior2.moveToPoint(isoZombie.networkAI.targetX, isoZombie.networkAI.targetY, 1.0f);
            isoZombie.setVariable("bMoving", IsoUtils.DistanceManhatten(isoZombie.networkAI.targetX, isoZombie.networkAI.targetY, isoZombie.nx, isoZombie.ny) > 0.5f);
        }
        else {
            if (!isoZombie.networkAI.usePathFind) {
                pathFindBehavior2.pathToLocationF(isoZombie.networkAI.targetX, isoZombie.networkAI.targetY, (float)isoZombie.networkAI.targetZ);
                pathFindBehavior2.walkingOnTheSpot.reset(isoZombie.x, isoZombie.y);
                isoZombie.networkAI.usePathFind = true;
            }
            final PathFindBehavior2.BehaviorResult update = pathFindBehavior2.update();
            if (update == PathFindBehavior2.BehaviorResult.Failed) {
                isoZombie.setPathFindIndex(-1);
                return;
            }
            if (update == PathFindBehavior2.BehaviorResult.Succeeded) {
                final int n = (int)isoZombie.getPathFindBehavior2().getTargetX();
                final int n2 = (int)isoZombie.getPathFindBehavior2().getTargetY();
                if ((GameServer.bServer ? ServerMap.instance.getChunk(n / 10, n2 / 10) : IsoWorld.instance.CurrentCell.getChunkForGridSquare(n, n2, 0)) == null) {
                    isoZombie.setVariable("bMoving", true);
                    return;
                }
                isoZombie.setPath2(null);
                isoZombie.setVariable("bMoving", true);
                return;
            }
        }
        if (!((IsoZombie)isoGameCharacter).bCrawling) {
            isoGameCharacter.setOnFloor(false);
        }
        boolean collidedWithVehicle = isoGameCharacter.isCollidedWithVehicle();
        if (isoZombie.target instanceof IsoGameCharacter && ((IsoGameCharacter)isoZombie.target).getVehicle() != null && ((IsoGameCharacter)isoZombie.target).getVehicle().isCharacterAdjacentTo(isoGameCharacter)) {
            collidedWithVehicle = false;
        }
        if (isoGameCharacter.isCollidedThisFrame() || collidedWithVehicle) {
            isoZombie.AllowRepathDelay = 0.0f;
            isoZombie.pathToLocation(isoGameCharacter.getPathTargetX(), isoGameCharacter.getPathTargetY(), isoGameCharacter.getPathTargetZ());
            if (!"true".equals(isoZombie.getVariableString("bPathfind"))) {
                isoZombie.setVariable("bPathfind", true);
                isoZombie.setVariable("bMoving", false);
            }
        }
        if (IngameState.instance.numberTicks - isoGameCharacter.getStateMachineParams(this).get(WalkTowardNetworkState.PARAM_TICK_COUNT) == 2L) {
            isoZombie.parameterZombieState.setState(ParameterZombieState.State.Idle);
        }
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.setVariable("bMoving", false);
    }
    
    static {
        WalkTowardNetworkState._instance = new WalkTowardNetworkState();
        PARAM_TICK_COUNT = 2;
    }
}

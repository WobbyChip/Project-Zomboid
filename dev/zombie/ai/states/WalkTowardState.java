// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import zombie.iso.IsoMovingObject;
import zombie.vehicles.PolygonalMap2;
import zombie.iso.IsoWorld;
import zombie.network.ServerMap;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.audio.parameters.ParameterZombieState;
import zombie.iso.IsoDirections;
import zombie.network.GameServer;
import zombie.util.Type;
import java.util.HashMap;
import zombie.characters.IsoZombie;
import zombie.gameStates.IngameState;
import zombie.characters.IsoGameCharacter;
import org.joml.Vector3f;
import zombie.iso.Vector2;
import zombie.ai.State;

public final class WalkTowardState extends State
{
    private static final WalkTowardState _instance;
    private static final Integer PARAM_IGNORE_OFFSET;
    private static final Integer PARAM_IGNORE_TIME;
    private static final Integer PARAM_TICK_COUNT;
    private final Vector2 temp;
    private final Vector3f worldPos;
    
    public WalkTowardState() {
        this.temp = new Vector2();
        this.worldPos = new Vector3f();
    }
    
    public static WalkTowardState instance() {
        return WalkTowardState._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        if (stateMachineParams.get(WalkTowardState.PARAM_IGNORE_OFFSET) == null) {
            stateMachineParams.put(WalkTowardState.PARAM_IGNORE_OFFSET, Boolean.FALSE);
            stateMachineParams.put(WalkTowardState.PARAM_IGNORE_TIME, 0L);
        }
        if (stateMachineParams.get(WalkTowardState.PARAM_IGNORE_OFFSET) == Boolean.TRUE && System.currentTimeMillis() - (long)stateMachineParams.get(WalkTowardState.PARAM_IGNORE_TIME) > 3000L) {
            stateMachineParams.put(WalkTowardState.PARAM_IGNORE_OFFSET, Boolean.FALSE);
            stateMachineParams.put(WalkTowardState.PARAM_IGNORE_TIME, 0L);
        }
        stateMachineParams.put(WalkTowardState.PARAM_TICK_COUNT, IngameState.instance.numberTicks);
        if (((IsoZombie)isoGameCharacter).isUseless()) {
            isoGameCharacter.changeState(ZombieIdleState.instance());
        }
        isoGameCharacter.getPathFindBehavior2().walkingOnTheSpot.reset(isoGameCharacter.x, isoGameCharacter.y);
        ((IsoZombie)isoGameCharacter).networkAI.extraUpdate();
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        final IsoZombie isoZombie = (IsoZombie)isoGameCharacter;
        if (!isoZombie.bCrawling) {
            isoGameCharacter.setOnFloor(false);
        }
        final IsoGameCharacter isoGameCharacter2 = Type.tryCastTo(isoZombie.target, IsoGameCharacter.class);
        if (isoZombie.target != null) {
            if (isoZombie.isTargetLocationKnown()) {
                if (isoGameCharacter2 != null) {
                    isoZombie.getPathFindBehavior2().pathToCharacter(isoGameCharacter2);
                    if (isoGameCharacter2.getVehicle() != null && isoZombie.DistToSquared(isoZombie.target) < 16.0f) {
                        if (isoGameCharacter2.getVehicle().chooseBestAttackPosition(isoGameCharacter2, isoZombie, this.worldPos) == null) {
                            isoZombie.setVariable("bMoving", false);
                            return;
                        }
                        if (Math.abs(isoGameCharacter.x - isoZombie.getPathFindBehavior2().getTargetX()) > 0.1f || Math.abs(isoGameCharacter.y - isoZombie.getPathFindBehavior2().getTargetY()) > 0.1f) {
                            isoZombie.setVariable("bPathfind", true);
                            isoZombie.setVariable("bMoving", false);
                            return;
                        }
                    }
                }
            }
            else if (isoZombie.LastTargetSeenX != -1 && !isoGameCharacter.getPathFindBehavior2().isTargetLocation(isoZombie.LastTargetSeenX + 0.5f, isoZombie.LastTargetSeenY + 0.5f, (float)isoZombie.LastTargetSeenZ)) {
                isoGameCharacter.pathToLocation(isoZombie.LastTargetSeenX, isoZombie.LastTargetSeenY, isoZombie.LastTargetSeenZ);
            }
        }
        if (isoGameCharacter.getPathTargetX() == (int)isoGameCharacter.getX() && isoGameCharacter.getPathTargetY() == (int)isoGameCharacter.getY()) {
            if (isoZombie.target == null) {
                isoZombie.setVariable("bPathfind", false);
                isoZombie.setVariable("bMoving", false);
                return;
            }
            if ((int)isoZombie.target.getZ() != (int)isoGameCharacter.getZ()) {
                isoZombie.setVariable("bPathfind", true);
                isoZombie.setVariable("bMoving", false);
                return;
            }
        }
        boolean collidedWithVehicle = isoGameCharacter.isCollidedWithVehicle();
        if (isoGameCharacter2 != null && isoGameCharacter2.getVehicle() != null && isoGameCharacter2.getVehicle().isCharacterAdjacentTo(isoGameCharacter)) {
            collidedWithVehicle = false;
        }
        boolean collidedThisFrame = isoGameCharacter.isCollidedThisFrame();
        if (collidedThisFrame && stateMachineParams.get(WalkTowardState.PARAM_IGNORE_OFFSET) == Boolean.FALSE) {
            stateMachineParams.put(WalkTowardState.PARAM_IGNORE_OFFSET, Boolean.TRUE);
            stateMachineParams.put(WalkTowardState.PARAM_IGNORE_TIME, System.currentTimeMillis());
            collidedThisFrame = !this.isPathClear(isoGameCharacter, isoZombie.getPathFindBehavior2().getTargetX(), isoZombie.getPathFindBehavior2().getTargetY(), isoZombie.z);
        }
        if (collidedThisFrame || collidedWithVehicle) {
            isoZombie.AllowRepathDelay = 0.0f;
            isoZombie.pathToLocation(isoGameCharacter.getPathTargetX(), isoGameCharacter.getPathTargetY(), isoGameCharacter.getPathTargetZ());
            if (!isoZombie.getVariableBoolean("bPathfind")) {
                isoZombie.setVariable("bPathfind", true);
                isoZombie.setVariable("bMoving", false);
            }
            return;
        }
        this.temp.x = isoZombie.getPathFindBehavior2().getTargetX();
        this.temp.y = isoZombie.getPathFindBehavior2().getTargetY();
        final Vector2 temp = this.temp;
        temp.x -= isoZombie.getX();
        final Vector2 temp2 = this.temp;
        temp2.y -= isoZombie.getY();
        float length = this.temp.getLength();
        if (length < 0.25f) {
            isoGameCharacter.x = isoZombie.getPathFindBehavior2().getTargetX();
            isoGameCharacter.y = isoZombie.getPathFindBehavior2().getTargetY();
            isoGameCharacter.nx = isoGameCharacter.x;
            isoGameCharacter.ny = isoGameCharacter.y;
            length = 0.0f;
        }
        if (length < 0.025f) {
            isoZombie.setVariable("bPathfind", false);
            isoZombie.setVariable("bMoving", false);
            return;
        }
        if (!GameServer.bServer && !isoZombie.bCrawling && stateMachineParams.get(WalkTowardState.PARAM_IGNORE_OFFSET) == Boolean.FALSE) {
            final float min = Math.min(length / 2.0f, 4.0f);
            final float n = (isoGameCharacter.getID() + isoZombie.ZombieID) % 20 / 10.0f - 1.0f;
            final float n2 = (isoZombie.getID() + isoZombie.ZombieID) % 20 / 10.0f - 1.0f;
            final Vector2 temp3 = this.temp;
            temp3.x += isoZombie.getX();
            final Vector2 temp4 = this.temp;
            temp4.y += isoZombie.getY();
            final Vector2 temp5 = this.temp;
            temp5.x += n * min;
            final Vector2 temp6 = this.temp;
            temp6.y += n2 * min;
            final Vector2 temp7 = this.temp;
            temp7.x -= isoZombie.getX();
            final Vector2 temp8 = this.temp;
            temp8.y -= isoZombie.getY();
        }
        isoZombie.bRunning = false;
        this.temp.normalize();
        if (isoZombie.bCrawling) {
            if (isoZombie.getVariableString("TurnDirection").isEmpty()) {
                isoZombie.setForwardDirection(this.temp);
            }
        }
        else {
            isoZombie.setDir(IsoDirections.fromAngle(this.temp));
            isoZombie.setForwardDirection(this.temp);
        }
        if (isoGameCharacter.getPathFindBehavior2().walkingOnTheSpot.check(isoGameCharacter.x, isoGameCharacter.y)) {
            isoGameCharacter.setVariable("bMoving", false);
        }
        if (IngameState.instance.numberTicks - (long)stateMachineParams.get(WalkTowardState.PARAM_TICK_COUNT) == 2L) {
            isoZombie.parameterZombieState.setState(ParameterZombieState.State.Idle);
        }
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.setVariable("bMoving", false);
        ((IsoZombie)isoGameCharacter).networkAI.extraUpdate();
    }
    
    @Override
    public void animEvent(final IsoGameCharacter isoGameCharacter, final AnimEvent animEvent) {
    }
    
    @Override
    public boolean isMoving(final IsoGameCharacter isoGameCharacter) {
        return true;
    }
    
    private boolean isPathClear(final IsoGameCharacter isoGameCharacter, final float n, final float n2, final float n3) {
        final int n4 = (int)n / 10;
        final int n5 = (int)n2 / 10;
        return (GameServer.bServer ? ServerMap.instance.getChunk(n4, n5) : IsoWorld.instance.CurrentCell.getChunkForGridSquare((int)n, (int)n2, (int)n3)) != null && !PolygonalMap2.instance.lineClearCollide(isoGameCharacter.getX(), isoGameCharacter.getY(), n, n2, (int)n3, isoGameCharacter.getPathFindBehavior2().getTargetChar(), 0x1 | 0x2);
    }
    
    public boolean calculateTargetLocation(final IsoZombie isoZombie, final Vector2 vector2) {
        assert isoZombie.isCurrentState(this);
        final HashMap<Object, Object> stateMachineParams = isoZombie.getStateMachineParams(this);
        vector2.x = isoZombie.getPathFindBehavior2().getTargetX();
        vector2.y = isoZombie.getPathFindBehavior2().getTargetY();
        this.temp.set(vector2);
        final Vector2 temp = this.temp;
        temp.x -= isoZombie.getX();
        final Vector2 temp2 = this.temp;
        temp2.y -= isoZombie.getY();
        final float length = this.temp.getLength();
        if (length < 0.025f) {
            return false;
        }
        if (!GameServer.bServer && !isoZombie.bCrawling && stateMachineParams.get(WalkTowardState.PARAM_IGNORE_OFFSET) == Boolean.FALSE) {
            final float min = Math.min(length / 2.0f, 4.0f);
            final float n = (isoZombie.getID() + isoZombie.ZombieID) % 20 / 10.0f - 1.0f;
            final float n2 = (isoZombie.getID() + isoZombie.ZombieID) % 20 / 10.0f - 1.0f;
            vector2.x += n * min;
            vector2.y += n2 * min;
            return true;
        }
        return false;
    }
    
    static {
        _instance = new WalkTowardState();
        PARAM_IGNORE_OFFSET = 0;
        PARAM_IGNORE_TIME = 1;
        PARAM_TICK_COUNT = 2;
    }
}

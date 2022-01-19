// 
// Decompiled by Procyon v0.5.36
// 

package zombie.vehicles;

import zombie.debug.LineDrawer;
import zombie.ai.states.WalkTowardState;
import zombie.iso.IsoCell;
import zombie.ai.states.ZombieGetDownState;
import zombie.iso.objects.IsoWindow;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoDirections;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoDoor;
import zombie.iso.IsoWorld;
import zombie.ai.states.CollideWithWallState;
import zombie.ai.State;
import zombie.iso.IsoObject;
import zombie.SandboxOptions;
import zombie.iso.IsoUtils;
import zombie.network.GameClient;
import zombie.ai.states.ClimbThroughWindowState;
import zombie.ai.states.ClimbOverFenceState;
import zombie.debug.DebugOptions;
import zombie.core.Core;
import gnu.trove.TFloatCollection;
import java.util.List;
import zombie.ai.astar.Mover;
import zombie.characters.IsoPlayer;
import zombie.scripting.objects.VehicleScript;
import se.krka.kahlua.vm.KahluaTable;
import zombie.util.Type;
import zombie.characters.IsoZombie;
import zombie.iso.IsoMovingObject;
import zombie.ai.astar.AStarPathFinder;
import zombie.popman.ObjectPool;
import zombie.ai.WalkingOnTheSpot;
import gnu.trove.list.array.TFloatArrayList;
import zombie.characters.IsoGameCharacter;
import zombie.ai.astar.IPathfinder;
import java.util.ArrayList;
import org.joml.Vector3f;
import org.joml.Vector2f;
import zombie.iso.Vector2;

public final class PathFindBehavior2 implements PolygonalMap2.IPathfinder
{
    private static final Vector2 tempVector2;
    private static final Vector2f tempVector2f;
    private static final Vector2 tempVector2_2;
    private static final Vector3f tempVector3f_1;
    private static final PointOnPath pointOnPath;
    public boolean pathNextIsSet;
    public float pathNextX;
    public float pathNextY;
    public ArrayList<zombie.ai.astar.IPathfinder> Listeners;
    public NPCData NPCData;
    private IsoGameCharacter chr;
    private float startX;
    private float startY;
    private float startZ;
    private float targetX;
    private float targetY;
    private float targetZ;
    private final TFloatArrayList targetXYZ;
    private final PolygonalMap2.Path path;
    private int pathIndex;
    private boolean isCancel;
    public boolean bStopping;
    public final WalkingOnTheSpot walkingOnTheSpot;
    private final ArrayList<DebugPt> actualPos;
    private static final ObjectPool<DebugPt> actualPool;
    private Goal goal;
    private IsoGameCharacter goalCharacter;
    private BaseVehicle goalVehicle;
    private String goalVehicleArea;
    private int goalVehicleSeat;
    
    public PathFindBehavior2(final IsoGameCharacter chr) {
        this.pathNextIsSet = false;
        this.Listeners = new ArrayList<zombie.ai.astar.IPathfinder>();
        this.NPCData = new NPCData();
        this.targetXYZ = new TFloatArrayList();
        this.path = new PolygonalMap2.Path();
        this.isCancel = true;
        this.bStopping = false;
        this.walkingOnTheSpot = new WalkingOnTheSpot();
        this.actualPos = new ArrayList<DebugPt>();
        this.goal = Goal.None;
        this.chr = chr;
    }
    
    public boolean isGoalNone() {
        return this.goal == Goal.None;
    }
    
    public boolean isGoalCharacter() {
        return this.goal == Goal.Character;
    }
    
    public boolean isGoalLocation() {
        return this.goal == Goal.Location;
    }
    
    public boolean isGoalSound() {
        return this.goal == Goal.Sound;
    }
    
    public boolean isGoalVehicleAdjacent() {
        return this.goal == Goal.VehicleAdjacent;
    }
    
    public boolean isGoalVehicleArea() {
        return this.goal == Goal.VehicleArea;
    }
    
    public boolean isGoalVehicleSeat() {
        return this.goal == Goal.VehicleSeat;
    }
    
    public void reset() {
        this.startX = this.chr.getX();
        this.startY = this.chr.getY();
        this.startZ = this.chr.getZ();
        this.targetX = this.startX;
        this.targetY = this.startY;
        this.targetZ = this.startZ;
        this.targetXYZ.resetQuick();
        this.pathIndex = 0;
        this.chr.getFinder().progress = AStarPathFinder.PathFindProgress.notrunning;
        this.walkingOnTheSpot.reset(this.startX, this.startY);
    }
    
    public void pathToCharacter(final IsoGameCharacter goalCharacter) {
        this.isCancel = false;
        this.goal = Goal.Character;
        this.goalCharacter = goalCharacter;
        if (goalCharacter.getVehicle() != null) {
            final Vector3f chooseBestAttackPosition = goalCharacter.getVehicle().chooseBestAttackPosition(goalCharacter, this.chr, PathFindBehavior2.tempVector3f_1);
            if (chooseBestAttackPosition != null) {
                this.setData(chooseBestAttackPosition.x, chooseBestAttackPosition.y, (float)(int)goalCharacter.getVehicle().z);
                return;
            }
            this.setData(goalCharacter.getVehicle().x, goalCharacter.getVehicle().y, (float)(int)goalCharacter.getVehicle().z);
            if (this.chr.DistToSquared(goalCharacter.getVehicle()) < 100.0f) {
                final IsoZombie isoZombie = Type.tryCastTo(this.chr, IsoZombie.class);
                if (isoZombie != null) {
                    isoZombie.AllowRepathDelay = 100.0f;
                }
                this.chr.getFinder().progress = AStarPathFinder.PathFindProgress.failed;
            }
        }
        this.setData(goalCharacter.getX(), goalCharacter.getY(), goalCharacter.getZ());
    }
    
    public void pathToLocation(final int n, final int n2, final int n3) {
        this.isCancel = false;
        this.goal = Goal.Location;
        this.setData(n + 0.5f, n2 + 0.5f, (float)n3);
    }
    
    public void pathToLocationF(final float n, final float n2, final float n3) {
        this.isCancel = false;
        this.goal = Goal.Location;
        this.setData(n, n2, n3);
    }
    
    public void pathToSound(final int n, final int n2, final int n3) {
        this.isCancel = false;
        this.goal = Goal.Sound;
        this.setData(n + 0.5f, n2 + 0.5f, (float)n3);
    }
    
    public void pathToNearest(final TFloatArrayList list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("locations is null or empty");
        }
        if (list.size() % 3 != 0) {
            throw new IllegalArgumentException("locations should be multiples of x,y,z");
        }
        this.isCancel = false;
        this.goal = Goal.Location;
        this.setData(list.get(0), list.get(1), list.get(2));
        for (int i = 3; i < list.size(); i += 3) {
            this.targetXYZ.add(list.get(i));
            this.targetXYZ.add(list.get(i + 1));
            this.targetXYZ.add(list.get(i + 2));
        }
    }
    
    public void pathToNearestTable(final KahluaTable kahluaTable) {
        if (kahluaTable == null || kahluaTable.isEmpty()) {
            throw new IllegalArgumentException("locations table is null or empty");
        }
        if (kahluaTable.len() % 3 != 0) {
            throw new IllegalArgumentException("locations table should be multiples of x,y,z");
        }
        final TFloatArrayList list = new TFloatArrayList(kahluaTable.size());
        for (int i = 1; i <= kahluaTable.len(); i += 3) {
            final Double n = Type.tryCastTo(kahluaTable.rawget(i), Double.class);
            final Double n2 = Type.tryCastTo(kahluaTable.rawget(i + 1), Double.class);
            final Double n3 = Type.tryCastTo(kahluaTable.rawget(i + 2), Double.class);
            if (n == null || n2 == null || n3 == null) {
                throw new IllegalArgumentException("locations table should be multiples of x,y,z");
            }
            list.add(n.floatValue());
            list.add(n2.floatValue());
            list.add(n3.floatValue());
        }
        this.pathToNearest(list);
    }
    
    public void pathToVehicleAdjacent(final BaseVehicle goalVehicle) {
        this.isCancel = false;
        this.goal = Goal.VehicleAdjacent;
        this.goalVehicle = goalVehicle;
        final VehicleScript script = goalVehicle.getScript();
        final Vector3f extents = script.getExtents();
        final Vector3f centerOfMassOffset = script.getCenterOfMassOffset();
        final float x = extents.x;
        final float z = extents.z;
        final float n = 0.3f;
        final float n2 = centerOfMassOffset.x - x / 2.0f - n;
        final float n3 = centerOfMassOffset.z - z / 2.0f - n;
        final float n4 = centerOfMassOffset.x + x / 2.0f + n;
        final float n5 = centerOfMassOffset.z + z / 2.0f + n;
        final TFloatArrayList list = new TFloatArrayList();
        final Vector3f worldPos = goalVehicle.getWorldPos(n2, centerOfMassOffset.y, centerOfMassOffset.z, PathFindBehavior2.tempVector3f_1);
        if (PolygonalMap2.instance.canStandAt(worldPos.x, worldPos.y, (int)this.targetZ, goalVehicle, false, true)) {
            list.add(worldPos.x);
            list.add(worldPos.y);
            list.add(this.targetZ);
        }
        final Vector3f worldPos2 = goalVehicle.getWorldPos(n4, centerOfMassOffset.y, centerOfMassOffset.z, PathFindBehavior2.tempVector3f_1);
        if (PolygonalMap2.instance.canStandAt(worldPos2.x, worldPos2.y, (int)this.targetZ, goalVehicle, false, true)) {
            list.add(worldPos2.x);
            list.add(worldPos2.y);
            list.add(this.targetZ);
        }
        final Vector3f worldPos3 = goalVehicle.getWorldPos(centerOfMassOffset.x, centerOfMassOffset.y, n3, PathFindBehavior2.tempVector3f_1);
        if (PolygonalMap2.instance.canStandAt(worldPos3.x, worldPos3.y, (int)this.targetZ, goalVehicle, false, true)) {
            list.add(worldPos3.x);
            list.add(worldPos3.y);
            list.add(this.targetZ);
        }
        final Vector3f worldPos4 = goalVehicle.getWorldPos(centerOfMassOffset.x, centerOfMassOffset.y, n5, PathFindBehavior2.tempVector3f_1);
        if (PolygonalMap2.instance.canStandAt(worldPos4.x, worldPos4.y, (int)this.targetZ, goalVehicle, false, true)) {
            list.add(worldPos4.x);
            list.add(worldPos4.y);
            list.add(this.targetZ);
        }
        this.setData(list.get(0), list.get(1), list.get(2));
        for (int i = 3; i < list.size(); i += 3) {
            this.targetXYZ.add(list.get(i));
            this.targetXYZ.add(list.get(i + 1));
            this.targetXYZ.add(list.get(i + 2));
        }
    }
    
    public void pathToVehicleArea(final BaseVehicle goalVehicle, final String goalVehicleArea) {
        final Vector2 areaCenter = goalVehicle.getAreaCenter(goalVehicleArea);
        if (areaCenter == null) {
            this.targetX = this.chr.getX();
            this.targetY = this.chr.getY();
            this.targetZ = this.chr.getZ();
            this.chr.getFinder().progress = AStarPathFinder.PathFindProgress.failed;
            return;
        }
        this.isCancel = false;
        this.goal = Goal.VehicleArea;
        this.goalVehicle = goalVehicle;
        this.goalVehicleArea = goalVehicleArea;
        this.setData(areaCenter.getX(), areaCenter.getY(), (float)(int)goalVehicle.getZ());
        if (this.chr instanceof IsoPlayer && (int)this.chr.z == (int)this.targetZ && !PolygonalMap2.instance.lineClearCollide(this.chr.x, this.chr.y, this.targetX, this.targetY, (int)this.targetZ, null)) {
            this.path.clear();
            this.path.addNode(this.chr.x, this.chr.y, this.chr.z);
            this.path.addNode(this.targetX, this.targetY, this.targetZ);
            this.chr.getFinder().progress = AStarPathFinder.PathFindProgress.found;
        }
    }
    
    public void pathToVehicleSeat(final BaseVehicle goalVehicle, final int n) {
        final VehicleScript.Position passengerPosition = goalVehicle.getPassengerPosition(n, "outside2");
        if (passengerPosition != null) {
            final Vector3f vector3f = BaseVehicle.TL_vector3f_pool.get().alloc();
            if (passengerPosition.area == null) {
                goalVehicle.getPassengerPositionWorldPos(passengerPosition, vector3f);
            }
            else {
                final Vector2 vector2 = BaseVehicle.TL_vector2_pool.get().alloc();
                final Vector2 areaPositionWorld4PlayerInteract = goalVehicle.areaPositionWorld4PlayerInteract(goalVehicle.script.getAreaById(passengerPosition.area), vector2);
                vector3f.x = areaPositionWorld4PlayerInteract.x;
                vector3f.y = areaPositionWorld4PlayerInteract.y;
                vector3f.z = 0.0f;
                BaseVehicle.TL_vector2_pool.get().release(vector2);
            }
            vector3f.sub(this.chr.x, this.chr.y, this.chr.z);
            if (vector3f.length() < 2.0f) {
                goalVehicle.getPassengerPositionWorldPos(passengerPosition, vector3f);
                this.setData(vector3f.x(), vector3f.y(), (float)(int)vector3f.z());
                if (this.chr instanceof IsoPlayer && (int)this.chr.z == (int)this.targetZ) {
                    BaseVehicle.TL_vector3f_pool.get().release(vector3f);
                    this.path.clear();
                    this.path.addNode(this.chr.x, this.chr.y, this.chr.z);
                    this.path.addNode(this.targetX, this.targetY, this.targetZ);
                    this.chr.getFinder().progress = AStarPathFinder.PathFindProgress.found;
                    return;
                }
            }
            BaseVehicle.TL_vector3f_pool.get().release(vector3f);
        }
        final VehicleScript.Position passengerPosition2 = goalVehicle.getPassengerPosition(n, "outside");
        if (passengerPosition2 != null) {
            this.isCancel = false;
            this.goal = Goal.VehicleSeat;
            this.goalVehicle = goalVehicle;
            final Vector3f vector3f2 = BaseVehicle.TL_vector3f_pool.get().alloc();
            if (passengerPosition2.area == null) {
                goalVehicle.getPassengerPositionWorldPos(passengerPosition2, vector3f2);
            }
            else {
                final Vector2 vector3 = BaseVehicle.TL_vector2_pool.get().alloc();
                final Vector2 areaPositionWorld4PlayerInteract2 = goalVehicle.areaPositionWorld4PlayerInteract(goalVehicle.script.getAreaById(passengerPosition2.area), vector3);
                vector3f2.x = areaPositionWorld4PlayerInteract2.x;
                vector3f2.y = areaPositionWorld4PlayerInteract2.y;
                vector3f2.z = 0.0f;
                BaseVehicle.TL_vector2_pool.get().release(vector3);
            }
            this.setData(vector3f2.x(), vector3f2.y(), (float)(int)vector3f2.z());
            BaseVehicle.TL_vector3f_pool.get().release(vector3f2);
            if (this.chr instanceof IsoPlayer && (int)this.chr.z == (int)this.targetZ && !PolygonalMap2.instance.lineClearCollide(this.chr.x, this.chr.y, this.targetX, this.targetY, (int)this.targetZ, null)) {
                this.path.clear();
                this.path.addNode(this.chr.x, this.chr.y, this.chr.z);
                this.path.addNode(this.targetX, this.targetY, this.targetZ);
                this.chr.getFinder().progress = AStarPathFinder.PathFindProgress.found;
            }
            return;
        }
        final VehiclePart passengerDoor = goalVehicle.getPassengerDoor(n);
        if (passengerDoor == null) {
            this.targetX = this.chr.getX();
            this.targetY = this.chr.getY();
            this.targetZ = this.chr.getZ();
            this.chr.getFinder().progress = AStarPathFinder.PathFindProgress.failed;
            return;
        }
        this.pathToVehicleArea(goalVehicle, passengerDoor.getArea());
    }
    
    public void cancel() {
        this.isCancel = true;
    }
    
    public boolean getIsCancelled() {
        return this.isCancel;
    }
    
    public void setData(final float targetX, final float targetY, final float targetZ) {
        this.startX = this.chr.getX();
        this.startY = this.chr.getY();
        this.startZ = this.chr.getZ();
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
        this.targetXYZ.resetQuick();
        this.pathIndex = 0;
        PolygonalMap2.instance.cancelRequest(this.chr);
        this.chr.getFinder().progress = AStarPathFinder.PathFindProgress.notrunning;
        this.bStopping = false;
        PathFindBehavior2.actualPool.release(this.actualPos);
        this.actualPos.clear();
    }
    
    public float getTargetX() {
        return this.targetX;
    }
    
    public float getTargetY() {
        return this.targetY;
    }
    
    public float getTargetZ() {
        return this.targetZ;
    }
    
    public float getPathLength() {
        if (this.path == null || this.path.nodes.size() == 0) {
            return (float)Math.sqrt((this.chr.x - this.targetX) * (this.chr.x - this.targetX) + (this.chr.y - this.targetY) * (this.chr.y - this.targetY));
        }
        if (this.pathIndex + 1 >= this.path.nodes.size()) {
            return (float)Math.sqrt((this.chr.x - this.targetX) * (this.chr.x - this.targetX) + (this.chr.y - this.targetY) * (this.chr.y - this.targetY));
        }
        float n = (float)Math.sqrt((this.chr.x - this.path.nodes.get(this.pathIndex + 1).x) * (this.chr.x - this.path.nodes.get(this.pathIndex + 1).x) + (this.chr.y - this.path.nodes.get(this.pathIndex + 1).y) * (this.chr.y - this.path.nodes.get(this.pathIndex + 1).y));
        for (int i = this.pathIndex + 2; i < this.path.nodes.size(); ++i) {
            n += (float)Math.sqrt((this.path.nodes.get(i - 1).x - this.path.nodes.get(i).x) * (this.path.nodes.get(i - 1).x - this.path.nodes.get(i).x) + (this.path.nodes.get(i - 1).y - this.path.nodes.get(i).y) * (this.path.nodes.get(i - 1).y - this.path.nodes.get(i).y));
        }
        return n;
    }
    
    public IsoGameCharacter getTargetChar() {
        return (this.goal == Goal.Character) ? this.goalCharacter : null;
    }
    
    public boolean isTargetLocation(final float n, final float n2, final float n3) {
        return this.goal == Goal.Location && n == this.targetX && n2 == this.targetY && (int)n3 == (int)this.targetZ;
    }
    
    public BehaviorResult update() {
        if (this.chr.getFinder().progress == AStarPathFinder.PathFindProgress.notrunning) {
            final PolygonalMap2.PathFindRequest addRequest = PolygonalMap2.instance.addRequest(this, this.chr, this.startX, this.startY, this.startZ, this.targetX, this.targetY, this.targetZ);
            addRequest.targetXYZ.resetQuick();
            addRequest.targetXYZ.addAll((TFloatCollection)this.targetXYZ);
            this.chr.getFinder().progress = AStarPathFinder.PathFindProgress.notyetfound;
            this.walkingOnTheSpot.reset(this.chr.x, this.chr.y);
            this.updateWhileRunningPathfind();
            return BehaviorResult.Working;
        }
        if (this.chr.getFinder().progress == AStarPathFinder.PathFindProgress.notyetfound) {
            this.updateWhileRunningPathfind();
            return BehaviorResult.Working;
        }
        if (this.chr.getFinder().progress == AStarPathFinder.PathFindProgress.failed) {
            return BehaviorResult.Failed;
        }
        final State currentState = this.chr.getCurrentState();
        if (Core.bDebug && DebugOptions.instance.PathfindRenderPath.getValue() && this.chr instanceof IsoPlayer) {
            this.actualPos.add(PathFindBehavior2.actualPool.alloc().init(this.chr.x, this.chr.y, this.chr.z, currentState == ClimbOverFenceState.instance() || currentState == ClimbThroughWindowState.instance()));
        }
        if (currentState == ClimbOverFenceState.instance() || currentState == ClimbThroughWindowState.instance()) {
            if (GameClient.bClient && this.chr instanceof IsoPlayer && !((IsoPlayer)this.chr).isLocalPlayer()) {
                this.chr.getDeferredMovement(PathFindBehavior2.tempVector2_2);
                this.chr.MoveUnmodded(PathFindBehavior2.tempVector2_2);
            }
            return BehaviorResult.Working;
        }
        if (this.chr.getVehicle() != null) {
            return BehaviorResult.Failed;
        }
        if (this.walkingOnTheSpot.check(this.chr.x, this.chr.y)) {
            return BehaviorResult.Failed;
        }
        this.chr.setMoving(true);
        this.chr.setPath2(this.path);
        final IsoZombie isoZombie = Type.tryCastTo(this.chr, IsoZombie.class);
        if (this.goal == Goal.Character && isoZombie != null && this.goalCharacter != null && this.goalCharacter.getVehicle() != null && this.chr.DistToSquared(this.targetX, this.targetY) < 16.0f) {
            final Vector3f chooseBestAttackPosition = this.goalCharacter.getVehicle().chooseBestAttackPosition(this.goalCharacter, this.chr, PathFindBehavior2.tempVector3f_1);
            if (chooseBestAttackPosition == null) {
                return BehaviorResult.Failed;
            }
            if (Math.abs(chooseBestAttackPosition.x - this.targetX) > 0.1f || Math.abs(chooseBestAttackPosition.y - this.targetY) > 0.1f) {
                if (Math.abs(this.goalCharacter.getVehicle().getCurrentSpeedKmHour()) > 0.1f) {
                    if (!PolygonalMap2.instance.lineClearCollide(this.chr.x, this.chr.y, chooseBestAttackPosition.x, chooseBestAttackPosition.y, (int)this.targetZ, this.goalCharacter)) {
                        this.path.clear();
                        this.path.addNode(this.chr.x, this.chr.y, this.chr.z);
                        this.path.addNode(chooseBestAttackPosition.x, chooseBestAttackPosition.y, chooseBestAttackPosition.z);
                    }
                    else if (IsoUtils.DistanceToSquared(chooseBestAttackPosition.x, chooseBestAttackPosition.y, this.targetX, this.targetY) > IsoUtils.DistanceToSquared(this.chr.x, this.chr.y, chooseBestAttackPosition.x, chooseBestAttackPosition.y)) {
                        return BehaviorResult.Working;
                    }
                }
                else if (isoZombie.AllowRepathDelay <= 0.0f) {
                    isoZombie.AllowRepathDelay = 6.25f;
                    if (PolygonalMap2.instance.lineClearCollide(this.chr.x, this.chr.y, chooseBestAttackPosition.x, chooseBestAttackPosition.y, (int)this.targetZ, null)) {
                        this.setData(chooseBestAttackPosition.x, chooseBestAttackPosition.y, this.targetZ);
                        return BehaviorResult.Working;
                    }
                    this.path.clear();
                    this.path.addNode(this.chr.x, this.chr.y, this.chr.z);
                    this.path.addNode(chooseBestAttackPosition.x, chooseBestAttackPosition.y, chooseBestAttackPosition.z);
                }
            }
        }
        closestPointOnPath(this.chr.x, this.chr.y, this.chr.z, this.chr, this.path, PathFindBehavior2.pointOnPath);
        this.pathIndex = PathFindBehavior2.pointOnPath.pathIndex;
        if (this.pathIndex == this.path.nodes.size() - 2) {
            final PolygonalMap2.PathNode pathNode = this.path.nodes.get(this.path.nodes.size() - 1);
            if (IsoUtils.DistanceToSquared(this.chr.x, this.chr.y, pathNode.x, pathNode.y) <= 0.0025000002f) {
                this.chr.getDeferredMovement(PathFindBehavior2.tempVector2);
                if (PathFindBehavior2.tempVector2.getLength() > 0.0f) {
                    if (isoZombie != null || this.chr instanceof IsoPlayer) {
                        this.chr.setMoving(false);
                    }
                    this.bStopping = true;
                    return BehaviorResult.Working;
                }
                this.pathNextIsSet = false;
                return BehaviorResult.Succeeded;
            }
        }
        else if (this.pathIndex < this.path.nodes.size() - 2 && PathFindBehavior2.pointOnPath.dist > 0.999f) {
            ++this.pathIndex;
        }
        final PolygonalMap2.PathNode pathNode2 = this.path.nodes.get(this.pathIndex);
        final PolygonalMap2.PathNode pathNode3 = this.path.nodes.get(this.pathIndex + 1);
        this.pathNextX = pathNode3.x;
        this.pathNextY = pathNode3.y;
        this.pathNextIsSet = true;
        final Vector2 set = PathFindBehavior2.tempVector2.set(this.pathNextX - this.chr.x, this.pathNextY - this.chr.y);
        set.normalize();
        this.chr.getDeferredMovement(PathFindBehavior2.tempVector2_2);
        float length = PathFindBehavior2.tempVector2_2.getLength();
        if (isoZombie != null) {
            isoZombie.bRunning = false;
            if (SandboxOptions.instance.Lore.Speed.getValue() == 1) {
                isoZombie.bRunning = true;
            }
        }
        final float n = length * 1.0f;
        final float distanceTo = IsoUtils.DistanceTo(this.pathNextX, this.pathNextY, this.chr.x, this.chr.y);
        if (n >= distanceTo) {
            length *= distanceTo / n;
            ++this.pathIndex;
        }
        if (isoZombie != null) {
            this.checkCrawlingTransition(pathNode2, pathNode3, distanceTo);
        }
        if (isoZombie == null && distanceTo >= 0.5f) {
            if (this.checkDoorHoppableWindow(this.chr.x + set.x * Math.max(0.5f, length), this.chr.y + set.y * Math.max(0.5f, length), this.chr.z)) {
                return BehaviorResult.Failed;
            }
            if (currentState != this.chr.getCurrentState()) {
                return BehaviorResult.Working;
            }
        }
        if (length <= 0.0f) {
            this.walkingOnTheSpot.reset(this.chr.x, this.chr.y);
            return BehaviorResult.Working;
        }
        PathFindBehavior2.tempVector2_2.set(set);
        PathFindBehavior2.tempVector2_2.setLength(length);
        this.chr.MoveUnmodded(PathFindBehavior2.tempVector2_2);
        if (this.isStrafing()) {
            if ((this.goal == Goal.VehicleAdjacent || this.goal == Goal.VehicleArea || this.goal == Goal.VehicleSeat) && this.goalVehicle != null) {
                this.chr.faceThisObject(this.goalVehicle);
            }
        }
        else if (!this.chr.isAiming()) {
            this.chr.faceLocationF(this.pathNextX, this.pathNextY);
        }
        return BehaviorResult.Working;
    }
    
    private void updateWhileRunningPathfind() {
        if (!this.pathNextIsSet) {
            return;
        }
        this.moveToPoint(this.pathNextX, this.pathNextY, 1.0f);
    }
    
    public void moveToPoint(final float n, final float n2, final float n3) {
        if (this.chr instanceof IsoPlayer && this.chr.getCurrentState() == CollideWithWallState.instance()) {
            return;
        }
        final IsoZombie isoZombie = Type.tryCastTo(this.chr, IsoZombie.class);
        final Vector2 set = PathFindBehavior2.tempVector2.set(n - this.chr.x, n2 - this.chr.y);
        if ((int)n == (int)this.chr.x && (int)n2 == (int)this.chr.y && set.getLength() <= 0.1f) {
            return;
        }
        set.normalize();
        this.chr.getDeferredMovement(PathFindBehavior2.tempVector2_2);
        final float length = PathFindBehavior2.tempVector2_2.getLength() * n3;
        if (isoZombie != null) {
            isoZombie.bRunning = false;
            if (SandboxOptions.instance.Lore.Speed.getValue() == 1) {
                isoZombie.bRunning = true;
            }
        }
        if (length <= 0.0f) {
            return;
        }
        PathFindBehavior2.tempVector2_2.set(set);
        PathFindBehavior2.tempVector2_2.setLength(length);
        this.chr.MoveUnmodded(PathFindBehavior2.tempVector2_2);
        this.chr.faceLocation(n - 0.5f, n2 - 0.5f);
        this.chr.getForwardDirection().set(n - this.chr.x, n2 - this.chr.y);
        this.chr.getForwardDirection().normalize();
    }
    
    public void moveToDir(final IsoMovingObject isoMovingObject, final float n) {
        final Vector2 set = PathFindBehavior2.tempVector2.set(isoMovingObject.x - this.chr.x, isoMovingObject.y - this.chr.y);
        if (set.getLength() <= 0.1f) {
            return;
        }
        set.normalize();
        this.chr.getDeferredMovement(PathFindBehavior2.tempVector2_2);
        final float length = PathFindBehavior2.tempVector2_2.getLength() * n;
        if (this.chr instanceof IsoZombie) {
            ((IsoZombie)this.chr).bRunning = false;
            if (SandboxOptions.instance.Lore.Speed.getValue() == 1) {
                ((IsoZombie)this.chr).bRunning = true;
            }
        }
        if (length <= 0.0f) {
            return;
        }
        PathFindBehavior2.tempVector2_2.set(set);
        PathFindBehavior2.tempVector2_2.setLength(length);
        this.chr.MoveUnmodded(PathFindBehavior2.tempVector2_2);
        this.chr.faceLocation(isoMovingObject.x - 0.5f, isoMovingObject.y - 0.5f);
        this.chr.getForwardDirection().set(isoMovingObject.x - this.chr.x, isoMovingObject.y - this.chr.y);
        this.chr.getForwardDirection().normalize();
    }
    
    private boolean checkDoorHoppableWindow(final float n, final float n2, final float n3) {
        final IsoGridSquare currentSquare = this.chr.getCurrentSquare();
        if (currentSquare == null) {
            return false;
        }
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n, n2, n3);
        if (gridSquare == null || gridSquare == currentSquare) {
            return false;
        }
        final int n4 = gridSquare.x - currentSquare.x;
        final int n5 = gridSquare.y - currentSquare.y;
        if (n4 != 0 && n5 != 0) {
            return false;
        }
        final IsoObject doorTo = this.chr.getCurrentSquare().getDoorTo(gridSquare);
        if (doorTo instanceof IsoDoor) {
            final IsoDoor isoDoor = (IsoDoor)doorTo;
            if (!isoDoor.open) {
                isoDoor.ToggleDoor(this.chr);
                if (!isoDoor.open) {
                    return true;
                }
            }
        }
        else if (doorTo instanceof IsoThumpable) {
            final IsoThumpable isoThumpable = (IsoThumpable)doorTo;
            if (!isoThumpable.open) {
                isoThumpable.ToggleDoor(this.chr);
                if (!isoThumpable.open) {
                    return true;
                }
            }
        }
        final IsoWindow windowTo = currentSquare.getWindowTo(gridSquare);
        if (windowTo != null) {
            if (!windowTo.canClimbThrough(this.chr) || (windowTo.isSmashed() && !windowTo.isGlassRemoved())) {
                return true;
            }
            this.chr.climbThroughWindow(windowTo);
            return false;
        }
        else {
            final IsoThumpable windowThumpableTo = currentSquare.getWindowThumpableTo(gridSquare);
            if (windowThumpableTo != null) {
                if (windowThumpableTo.isBarricaded()) {
                    return true;
                }
                this.chr.climbThroughWindow(windowThumpableTo);
                return false;
            }
            else {
                final IsoObject windowFrameTo = currentSquare.getWindowFrameTo(gridSquare);
                if (windowFrameTo != null) {
                    this.chr.climbThroughWindowFrame(windowFrameTo);
                    return false;
                }
                if (n4 > 0 && gridSquare.Is(IsoFlagType.HoppableW)) {
                    this.chr.climbOverFence(IsoDirections.E);
                }
                else if (n4 < 0 && currentSquare.Is(IsoFlagType.HoppableW)) {
                    this.chr.climbOverFence(IsoDirections.W);
                }
                else if (n5 < 0 && currentSquare.Is(IsoFlagType.HoppableN)) {
                    this.chr.climbOverFence(IsoDirections.N);
                }
                else if (n5 > 0 && gridSquare.Is(IsoFlagType.HoppableN)) {
                    this.chr.climbOverFence(IsoDirections.S);
                }
                return false;
            }
        }
    }
    
    private void checkCrawlingTransition(PolygonalMap2.PathNode pathNode, PolygonalMap2.PathNode pathNode2, float distanceTo) {
        final IsoZombie isoZombie = (IsoZombie)this.chr;
        if (this.pathIndex < this.path.nodes.size() - 2) {
            pathNode = this.path.nodes.get(this.pathIndex);
            pathNode2 = this.path.nodes.get(this.pathIndex + 1);
            distanceTo = IsoUtils.DistanceTo(pathNode2.x, pathNode2.y, this.chr.x, this.chr.y);
        }
        if (isoZombie.isCrawling()) {
            if (!isoZombie.isCanWalk()) {
                return;
            }
            if (isoZombie.isBeingSteppedOn()) {}
            if (isoZombie.getStateMachine().getPrevious() == ZombieGetDownState.instance() && ZombieGetDownState.instance().isNearStartXY(isoZombie)) {
                return;
            }
            this.advanceAlongPath(this.chr.x, this.chr.y, this.chr.z, 0.5f, PathFindBehavior2.pointOnPath);
            if (!PolygonalMap2.instance.canStandAt(PathFindBehavior2.pointOnPath.x, PathFindBehavior2.pointOnPath.y, (int)isoZombie.z, null, false, true)) {
                return;
            }
            if (!pathNode2.hasFlag(1) && PolygonalMap2.instance.canStandAt(isoZombie.x, isoZombie.y, (int)isoZombie.z, null, false, true)) {
                isoZombie.setVariable("ShouldStandUp", true);
            }
        }
        else {
            if (pathNode.hasFlag(1) && pathNode2.hasFlag(1)) {
                isoZombie.setVariable("ShouldBeCrawling", true);
                ZombieGetDownState.instance().setParams(this.chr);
                return;
            }
            if (distanceTo < 0.4f && !pathNode.hasFlag(1) && pathNode2.hasFlag(1)) {
                isoZombie.setVariable("ShouldBeCrawling", true);
                ZombieGetDownState.instance().setParams(this.chr);
            }
        }
    }
    
    public boolean shouldGetUpFromCrawl() {
        return this.chr.getVariableBoolean("ShouldStandUp");
    }
    
    public boolean isStrafing() {
        return !this.chr.isZombie() && this.path.nodes.size() == 2 && IsoUtils.DistanceToSquared(this.startX, this.startY, this.startZ * 3.0f, this.targetX, this.targetY, this.targetZ * 3.0f) < 0.25f;
    }
    
    public static void closestPointOnPath(final float n, final float n2, final float n3, final IsoMovingObject isoMovingObject, final PolygonalMap2.Path path, final PointOnPath pointOnPath) {
        final IsoCell currentCell = IsoWorld.instance.CurrentCell;
        pointOnPath.pathIndex = 0;
        float n4 = Float.MAX_VALUE;
        for (int i = 0; i < path.nodes.size() - 1; ++i) {
            final PolygonalMap2.PathNode pathNode = path.nodes.get(i);
            final PolygonalMap2.PathNode pathNode2 = path.nodes.get(i + 1);
            if ((int)pathNode.z == (int)n3 || (int)pathNode2.z == (int)n3) {
                final float x = pathNode.x;
                final float y = pathNode.y;
                final float x2 = pathNode2.x;
                final float y2 = pathNode2.y;
                double n5 = ((n - x) * (x2 - x) + (n2 - y) * (y2 - y)) / (Math.pow(x2 - x, 2.0) + Math.pow(y2 - y, 2.0));
                double n6 = x + n5 * (x2 - x);
                double n7 = y + n5 * (y2 - y);
                if (n5 <= 0.0) {
                    n6 = x;
                    n7 = y;
                    n5 = 0.0;
                }
                else if (n5 >= 1.0) {
                    n6 = x2;
                    n7 = y2;
                    n5 = 1.0;
                }
                final int n8 = (int)n6 - (int)n;
                final int n9 = (int)n7 - (int)n2;
                if ((n8 != 0 || n9 != 0) && Math.abs(n8) <= 1 && Math.abs(n9) <= 1) {
                    final IsoGridSquare gridSquare = currentCell.getGridSquare((int)n, (int)n2, (int)n3);
                    final IsoGridSquare gridSquare2 = currentCell.getGridSquare((int)n6, (int)n7, (int)n3);
                    if (isoMovingObject instanceof IsoZombie) {
                        final boolean ghost = ((IsoZombie)isoMovingObject).Ghost;
                        ((IsoZombie)isoMovingObject).Ghost = true;
                        try {
                            if (gridSquare != null && gridSquare2 != null && gridSquare.testCollideAdjacent(isoMovingObject, n8, n9, 0)) {
                                continue;
                            }
                        }
                        finally {
                            ((IsoZombie)isoMovingObject).Ghost = ghost;
                        }
                    }
                    else if (gridSquare != null && gridSquare2 != null && gridSquare.testCollideAdjacent(isoMovingObject, n8, n9, 0)) {
                        continue;
                    }
                }
                float n10 = n3;
                if (Math.abs(n8) <= 1 && Math.abs(n9) <= 1) {
                    final IsoGridSquare gridSquare3 = currentCell.getGridSquare((int)pathNode.x, (int)pathNode.y, (int)pathNode.z);
                    final IsoGridSquare gridSquare4 = currentCell.getGridSquare((int)pathNode2.x, (int)pathNode2.y, (int)pathNode2.z);
                    final float n11 = (gridSquare3 == null) ? pathNode.z : PolygonalMap2.instance.getApparentZ(gridSquare3);
                    n10 = n11 + (((gridSquare4 == null) ? pathNode2.z : PolygonalMap2.instance.getApparentZ(gridSquare4)) - n11) * (float)n5;
                }
                final float distanceToSquared = IsoUtils.DistanceToSquared(n, n2, n3, (float)n6, (float)n7, n10);
                if (distanceToSquared < n4) {
                    n4 = distanceToSquared;
                    pointOnPath.pathIndex = i;
                    pointOnPath.dist = ((n5 == 1.0) ? 1.0f : ((float)n5));
                    pointOnPath.x = (float)n6;
                    pointOnPath.y = (float)n7;
                }
            }
        }
    }
    
    void advanceAlongPath(float x, float y, final float n, float n2, final PointOnPath pointOnPath) {
        closestPointOnPath(x, y, n, this.chr, this.path, pointOnPath);
        for (int i = pointOnPath.pathIndex; i < this.path.nodes.size() - 1; ++i) {
            final PolygonalMap2.PathNode pathNode = this.path.nodes.get(i);
            final PolygonalMap2.PathNode pathNode2 = this.path.nodes.get(i + 1);
            final double n3 = IsoUtils.DistanceTo2D(x, y, pathNode2.x, pathNode2.y);
            if (n2 <= n3) {
                pointOnPath.pathIndex = i;
                pointOnPath.dist += n2 / IsoUtils.DistanceTo2D(pathNode.x, pathNode.y, pathNode2.x, pathNode2.y);
                pointOnPath.x = pathNode.x + pointOnPath.dist * (pathNode2.x - pathNode.x);
                pointOnPath.y = pathNode.y + pointOnPath.dist * (pathNode2.y - pathNode.y);
                return;
            }
            x = pathNode2.x;
            y = pathNode2.y;
            n2 -= (float)n3;
            pointOnPath.dist = 0.0f;
        }
        pointOnPath.pathIndex = this.path.nodes.size() - 1;
        pointOnPath.dist = 1.0f;
        pointOnPath.x = this.path.nodes.get(pointOnPath.pathIndex).x;
        pointOnPath.y = this.path.nodes.get(pointOnPath.pathIndex).y;
    }
    
    public void render() {
        if (this.chr.getCurrentState() == WalkTowardState.instance()) {
            WalkTowardState.instance().calculateTargetLocation((IsoZombie)this.chr, PathFindBehavior2.tempVector2);
            final Vector2 tempVector2 = PathFindBehavior2.tempVector2;
            tempVector2.x -= this.chr.x;
            final Vector2 tempVector3 = PathFindBehavior2.tempVector2;
            tempVector3.y -= this.chr.y;
            PathFindBehavior2.tempVector2.setLength(Math.min(100.0f, PathFindBehavior2.tempVector2.getLength()));
            LineDrawer.addLine(this.chr.x, this.chr.y, this.chr.z, this.chr.x + PathFindBehavior2.tempVector2.x, this.chr.y + PathFindBehavior2.tempVector2.y, this.targetZ, 1.0f, 1.0f, 1.0f, null, true);
            return;
        }
        if (this.chr.getPath2() == null) {
            return;
        }
        for (int i = 0; i < this.path.nodes.size() - 1; ++i) {
            final PolygonalMap2.PathNode pathNode = this.path.nodes.get(i);
            final PolygonalMap2.PathNode pathNode2 = this.path.nodes.get(i + 1);
            final float n = 1.0f;
            float n2 = 1.0f;
            if ((int)pathNode.z != (int)pathNode2.z) {
                n2 = 0.0f;
            }
            LineDrawer.addLine(pathNode.x, pathNode.y, pathNode.z, pathNode2.x, pathNode2.y, pathNode2.z, n, n2, 0.0f, null, true);
        }
        for (int j = 0; j < this.path.nodes.size(); ++j) {
            final PolygonalMap2.PathNode pathNode3 = this.path.nodes.get(j);
            float n3 = 1.0f;
            final float n4 = 1.0f;
            float n5 = 0.0f;
            if (j == 0) {
                n3 = 0.0f;
                n5 = 1.0f;
            }
            LineDrawer.addLine(pathNode3.x - 0.05f, pathNode3.y - 0.05f, pathNode3.z, pathNode3.x + 0.05f, pathNode3.y + 0.05f, pathNode3.z, n3, n4, n5, null, false);
        }
        closestPointOnPath(this.chr.x, this.chr.y, this.chr.z, this.chr, this.path, PathFindBehavior2.pointOnPath);
        LineDrawer.addLine(PathFindBehavior2.pointOnPath.x - 0.05f, PathFindBehavior2.pointOnPath.y - 0.05f, this.chr.z, PathFindBehavior2.pointOnPath.x + 0.05f, PathFindBehavior2.pointOnPath.y + 0.05f, this.chr.z, 0.0f, 1.0f, 0.0f, null, false);
        for (int k = 0; k < this.actualPos.size() - 1; ++k) {
            final DebugPt debugPt = this.actualPos.get(k);
            final DebugPt debugPt2 = this.actualPos.get(k + 1);
            LineDrawer.addLine(debugPt.x, debugPt.y, this.chr.z, debugPt2.x, debugPt2.y, this.chr.z, 1.0f, 1.0f, 1.0f, null, true);
            LineDrawer.addLine(debugPt.x - 0.05f, debugPt.y - 0.05f, this.chr.z, debugPt.x + 0.05f, debugPt.y + 0.05f, this.chr.z, 1.0f, debugPt.climbing ? 1.0f : 0.0f, 0.0f, null, false);
        }
    }
    
    @Override
    public void Succeeded(final PolygonalMap2.Path path, final Mover mover) {
        this.path.copyFrom(path);
        if (!this.isCancel) {
            this.chr.setPath2(this.path);
        }
        if (!path.isEmpty()) {
            final PolygonalMap2.PathNode pathNode = path.nodes.get(path.nodes.size() - 1);
            this.targetX = pathNode.x;
            this.targetY = pathNode.y;
            this.targetZ = pathNode.z;
        }
        this.chr.getFinder().progress = AStarPathFinder.PathFindProgress.found;
    }
    
    @Override
    public void Failed(final Mover mover) {
        this.chr.getFinder().progress = AStarPathFinder.PathFindProgress.failed;
    }
    
    public boolean isMovingUsingPathFind() {
        return !this.bStopping && !this.isGoalNone() && !this.isCancel;
    }
    
    static {
        tempVector2 = new Vector2();
        tempVector2f = new Vector2f();
        tempVector2_2 = new Vector2();
        tempVector3f_1 = new Vector3f();
        pointOnPath = new PointOnPath();
        actualPool = new ObjectPool<DebugPt>(DebugPt::new);
    }
    
    public class NPCData
    {
        public boolean doDirectMovement;
        public int MaxSteps;
        public int nextTileX;
        public int nextTileY;
        public int nextTileZ;
    }
    
    private static final class DebugPt
    {
        float x;
        float y;
        float z;
        boolean climbing;
        
        DebugPt init(final float x, final float y, final float z, final boolean climbing) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.climbing = climbing;
            return this;
        }
    }
    
    public enum BehaviorResult
    {
        Working, 
        Failed, 
        Succeeded;
        
        private static /* synthetic */ BehaviorResult[] $values() {
            return new BehaviorResult[] { BehaviorResult.Working, BehaviorResult.Failed, BehaviorResult.Succeeded };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    private enum Goal
    {
        None, 
        Character, 
        Location, 
        Sound, 
        VehicleAdjacent, 
        VehicleArea, 
        VehicleSeat;
        
        private static /* synthetic */ Goal[] $values() {
            return new Goal[] { Goal.None, Goal.Character, Goal.Location, Goal.Sound, Goal.VehicleAdjacent, Goal.VehicleArea, Goal.VehicleSeat };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    public static final class PointOnPath
    {
        int pathIndex;
        float dist;
        float x;
        float y;
    }
}

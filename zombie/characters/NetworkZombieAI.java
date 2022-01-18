// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters;

import zombie.iso.IsoDirections;
import zombie.network.GameServer;
import zombie.network.GameClient;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.core.Core;
import zombie.debug.DebugOptions;
import zombie.ai.states.PathFindState;
import zombie.ai.states.LungeState;
import zombie.ai.states.WalkTowardState;
import zombie.ai.states.ClimbThroughWindowState;
import zombie.ai.states.ClimbOverWallState;
import zombie.ai.states.ClimbOverFenceState;
import zombie.ai.states.ThumpState;
import zombie.GameTime;
import zombie.iso.IsoUtils;
import zombie.iso.IsoObject;
import zombie.iso.Vector2;
import zombie.network.NetworkVariables;
import zombie.network.packets.ZombiePacket;
import zombie.core.math.PZMath;
import zombie.popman.NetworkZombieSimulator;
import zombie.vehicles.PathFindBehavior2;
import zombie.core.utils.UpdateTimer;

public class NetworkZombieAI extends NetworkCharacterAI
{
    private final UpdateTimer timer;
    private final PathFindBehavior2 pfb2;
    public final IsoZombie zombie;
    public boolean usePathFind;
    public float targetX;
    public float targetY;
    public int targetZ;
    public boolean isClimbing;
    private byte flags;
    private byte direction;
    public final NetworkZombieMind mindSync;
    public boolean DebugInterfaceActive;
    
    public NetworkZombieAI(final IsoGameCharacter isoGameCharacter) {
        super(isoGameCharacter);
        this.usePathFind = false;
        this.targetX = 0.0f;
        this.targetY = 0.0f;
        this.targetZ = 0;
        this.DebugInterfaceActive = false;
        this.zombie = (IsoZombie)isoGameCharacter;
        this.isClimbing = false;
        this.flags = 0;
        this.pfb2 = this.zombie.getPathFindBehavior2();
        this.timer = new UpdateTimer();
        this.mindSync = new NetworkZombieMind(this.zombie);
        isoGameCharacter.ulBeatenVehicle.Reset(400L);
    }
    
    @Override
    public void reset() {
        super.reset();
        this.usePathFind = true;
        this.targetX = this.zombie.getX();
        this.targetY = this.zombie.getY();
        this.targetZ = (byte)this.zombie.getZ();
        this.isClimbing = false;
        this.flags = 0;
        this.zombie.getHitDir().set(0.0f, 0.0f);
    }
    
    public void extraUpdate() {
        NetworkZombieSimulator.getInstance().addExtraUpdate(this.zombie);
    }
    
    private long getUpdateTime() {
        return this.timer.getTime();
    }
    
    public void setUpdateTimer(final float n) {
        this.timer.reset(PZMath.clamp((int)n, 200, 3800));
    }
    
    private void setUsingExtrapolation(final ZombiePacket zombiePacket, final int n) {
        if (this.zombie.isMoving()) {
            final Vector2 toVector = this.zombie.dir.ToVector();
            this.zombie.networkCharacter.checkReset(n);
            final NetworkCharacter.Transform predict = this.zombie.networkCharacter.predict(500, n, this.zombie.x, this.zombie.y, toVector.x, toVector.y);
            zombiePacket.x = predict.position.x;
            zombiePacket.y = predict.position.y;
            zombiePacket.z = (byte)this.zombie.z;
            zombiePacket.moveType = NetworkVariables.PredictionTypes.Moving;
            this.setUpdateTimer(300.0f);
        }
        else {
            zombiePacket.x = this.zombie.x;
            zombiePacket.y = this.zombie.y;
            zombiePacket.z = (byte)this.zombie.z;
            zombiePacket.moveType = NetworkVariables.PredictionTypes.Static;
            this.setUpdateTimer(2280.0f);
        }
    }
    
    private void setUsingThump(final ZombiePacket zombiePacket) {
        zombiePacket.x = ((IsoObject)this.zombie.getThumpTarget()).getX();
        zombiePacket.y = ((IsoObject)this.zombie.getThumpTarget()).getY();
        zombiePacket.z = (byte)((IsoObject)this.zombie.getThumpTarget()).getZ();
        zombiePacket.moveType = NetworkVariables.PredictionTypes.Thump;
        this.setUpdateTimer(2280.0f);
    }
    
    private void setUsingClimb(final ZombiePacket zombiePacket) {
        zombiePacket.x = this.zombie.getTarget().getX();
        zombiePacket.y = this.zombie.getTarget().getY();
        zombiePacket.z = (byte)this.zombie.getTarget().getZ();
        zombiePacket.moveType = NetworkVariables.PredictionTypes.Climb;
        this.setUpdateTimer(2280.0f);
    }
    
    private void setUsingLungeState(final ZombiePacket zombiePacket, final long n) {
        if (this.zombie.target == null) {
            this.setUsingExtrapolation(zombiePacket, (int)n);
            return;
        }
        final float distanceTo = IsoUtils.DistanceTo(this.zombie.target.x, this.zombie.target.y, this.zombie.x, this.zombie.y);
        if (distanceTo > 5.0f) {
            zombiePacket.x = (this.zombie.x + this.zombie.target.x) * 0.5f;
            zombiePacket.y = (this.zombie.y + this.zombie.target.y) * 0.5f;
            zombiePacket.z = (byte)this.zombie.target.z;
            final float n2 = distanceTo * 0.5f / 5.0E-4f * this.zombie.speedMod;
            zombiePacket.moveType = NetworkVariables.PredictionTypes.LungeHalf;
            this.setUpdateTimer(n2 * 0.6f);
        }
        else {
            zombiePacket.x = this.zombie.target.x;
            zombiePacket.y = this.zombie.target.y;
            zombiePacket.z = (byte)this.zombie.target.z;
            final float n3 = distanceTo / 5.0E-4f * this.zombie.speedMod;
            zombiePacket.moveType = NetworkVariables.PredictionTypes.Lunge;
            this.setUpdateTimer(n3 * 0.6f);
        }
    }
    
    private void setUsingWalkTowardState(final ZombiePacket zombiePacket) {
        float n;
        if (this.zombie.getPath2() == null) {
            final float pathLength = this.pfb2.getPathLength();
            if (pathLength > 5.0f) {
                zombiePacket.x = (this.zombie.x + this.pfb2.getTargetX()) * 0.5f;
                zombiePacket.y = (this.zombie.y + this.pfb2.getTargetY()) * 0.5f;
                zombiePacket.z = (byte)this.pfb2.getTargetZ();
                n = pathLength * 0.5f / 5.0E-4f * this.zombie.speedMod;
                zombiePacket.moveType = NetworkVariables.PredictionTypes.WalkHalf;
            }
            else {
                zombiePacket.x = this.pfb2.getTargetX();
                zombiePacket.y = this.pfb2.getTargetY();
                zombiePacket.z = (byte)this.pfb2.getTargetZ();
                n = pathLength / 5.0E-4f * this.zombie.speedMod;
                zombiePacket.moveType = NetworkVariables.PredictionTypes.Walk;
            }
        }
        else {
            zombiePacket.x = this.pfb2.pathNextX;
            zombiePacket.y = this.pfb2.pathNextY;
            zombiePacket.z = (byte)this.zombie.z;
            n = IsoUtils.DistanceTo(this.zombie.x, this.zombie.y, this.pfb2.pathNextX, this.pfb2.pathNextY) / 5.0E-4f * this.zombie.speedMod;
            zombiePacket.moveType = NetworkVariables.PredictionTypes.Walk;
        }
        this.setUpdateTimer(n * 0.6f);
    }
    
    private void setUsingPathFindState(final ZombiePacket zombiePacket) {
        zombiePacket.x = this.pfb2.pathNextX;
        zombiePacket.y = this.pfb2.pathNextY;
        zombiePacket.z = (byte)this.zombie.z;
        final float n = IsoUtils.DistanceTo(this.zombie.x, this.zombie.y, this.pfb2.pathNextX, this.pfb2.pathNextY) / 5.0E-4f * this.zombie.speedMod;
        zombiePacket.moveType = NetworkVariables.PredictionTypes.PathFind;
        this.setUpdateTimer(n * 0.6f);
    }
    
    public void set(final ZombiePacket zombiePacket) {
        final int n = (int)(GameTime.getServerTime() / 1000000L);
        zombiePacket.booleanVariables = NetworkZombieVariables.getBooleanVariables(this.zombie);
        zombiePacket.realHealth = (short)NetworkZombieVariables.getInt(this.zombie, (short)0);
        zombiePacket.target = (short)NetworkZombieVariables.getInt(this.zombie, (short)1);
        zombiePacket.speedMod = (short)NetworkZombieVariables.getInt(this.zombie, (short)2);
        zombiePacket.timeSinceSeenFlesh = NetworkZombieVariables.getInt(this.zombie, (short)3);
        zombiePacket.smParamTargetAngle = NetworkZombieVariables.getInt(this.zombie, (short)4);
        zombiePacket.walkType = NetworkVariables.WalkType.fromString(this.zombie.getVariableString("zombieWalkType"));
        zombiePacket.realX = this.zombie.x;
        zombiePacket.realY = this.zombie.y;
        zombiePacket.realZ = (byte)this.zombie.z;
        this.zombie.realState = NetworkVariables.ZombieState.fromString(this.zombie.getAdvancedAnimator().getCurrentStateName());
        zombiePacket.realState = this.zombie.realState;
        if (this.zombie.getThumpTarget() != null && this.zombie.getCurrentState() == ThumpState.instance()) {
            this.setUsingThump(zombiePacket);
        }
        else if (this.zombie.getTarget() != null && !this.isClimbing && (this.zombie.getCurrentState() == ClimbOverFenceState.instance() || this.zombie.getCurrentState() == ClimbOverWallState.instance() || this.zombie.getCurrentState() == ClimbThroughWindowState.instance())) {
            this.setUsingClimb(zombiePacket);
            this.isClimbing = true;
        }
        else if (this.zombie.getCurrentState() == WalkTowardState.instance()) {
            this.setUsingWalkTowardState(zombiePacket);
        }
        else if (this.zombie.getCurrentState() == LungeState.instance()) {
            this.setUsingLungeState(zombiePacket, n);
        }
        else if (this.zombie.getCurrentState() == PathFindState.instance() && this.zombie.isMoving()) {
            this.setUsingPathFindState(zombiePacket);
        }
        else {
            this.setUsingExtrapolation(zombiePacket, n);
        }
        final Vector2 toVector = this.zombie.dir.ToVector();
        this.zombie.networkCharacter.updateExtrapolationPoint(n, this.zombie.x, this.zombie.y, toVector.x, toVector.y);
        if (DebugOptions.instance.MultiplayerLogPrediction.getValue() && Core.bDebug) {
            DebugLog.log(DebugType.Multiplayer, getPredictionDebug(this.zombie, zombiePacket, n, this.getUpdateTime()));
        }
    }
    
    public void parse(final ZombiePacket zombiePacket) {
        final int n = (int)(GameTime.getServerTime() / 1000000L);
        if (DebugOptions.instance.MultiplayerLogPrediction.getValue()) {
            this.zombie.getNetworkCharacterAI().addTeleportData(n, getPredictionDebug(this.zombie, zombiePacket, n, n));
        }
        if (this.usePathFind) {
            this.pfb2.pathToLocationF(zombiePacket.x, zombiePacket.y, zombiePacket.z);
            this.pfb2.walkingOnTheSpot.reset(this.zombie.x, this.zombie.y);
        }
        this.targetX = zombiePacket.x;
        this.targetY = zombiePacket.y;
        this.targetZ = zombiePacket.z;
        this.predictionType = zombiePacket.moveType;
        NetworkZombieVariables.setInt(this.zombie, (short)1, zombiePacket.target);
        NetworkZombieVariables.setInt(this.zombie, (short)3, zombiePacket.timeSinceSeenFlesh);
        if (this.zombie.isRemoteZombie()) {
            NetworkZombieVariables.setInt(this.zombie, (short)2, zombiePacket.speedMod);
            NetworkZombieVariables.setInt(this.zombie, (short)4, zombiePacket.smParamTargetAngle);
            NetworkZombieVariables.setBooleanVariables(this.zombie, zombiePacket.booleanVariables);
            this.zombie.setWalkType(zombiePacket.walkType.toString());
            this.zombie.realState = zombiePacket.realState;
        }
        this.zombie.realx = zombiePacket.realX;
        this.zombie.realy = zombiePacket.realY;
        this.zombie.realz = zombiePacket.realZ;
        if ((IsoUtils.DistanceToSquared(this.zombie.x, this.zombie.y, this.zombie.realx, this.zombie.realy) > 9.0f || this.zombie.z != this.zombie.realz) && (this.zombie.isRemoteZombie() || (IsoPlayer.getInstance() != null && IsoUtils.DistanceToSquared(this.zombie.x, this.zombie.y, IsoPlayer.getInstance().x, IsoPlayer.getInstance().y) > 2.0f))) {
            NetworkTeleport.teleport(this.zombie, NetworkTeleport.Type.teleportation, this.zombie.realx, this.zombie.realy, this.zombie.realz, 1.0f);
        }
    }
    
    public void preupdate() {
        if (GameClient.bClient) {
            if (this.zombie.target != null) {
                this.zombie.setTargetSeenTime(this.zombie.getTargetSeenTime() + GameTime.getInstance().getRealworldSecondsSinceLastUpdate());
            }
        }
        else if (GameServer.bServer) {
            final byte flags = (byte)((this.zombie.getVariableBoolean("bMoving") ? 1 : 0) | (this.zombie.getVariableBoolean("bPathfind") ? 2 : 0));
            if (this.flags != flags) {
                this.flags = flags;
                this.extraUpdate();
            }
            final byte direction = (byte)IsoDirections.fromAngleActual(this.zombie.getForwardDirection()).index();
            if (this.direction != direction) {
                this.direction = direction;
                this.extraUpdate();
            }
        }
    }
    
    public static String getPredictionDebug(final IsoGameCharacter isoGameCharacter, final ZombiePacket zombiePacket, final int i, final long n) {
        return String.format("Prediction Z_%d [type=%s, distance=%f], time [current=%d, next=%d], states [current=%s, previous=%s]", zombiePacket.id, zombiePacket.moveType.toString(), IsoUtils.DistanceTo(isoGameCharacter.x, isoGameCharacter.y, zombiePacket.x, zombiePacket.y), i, n - i, isoGameCharacter.getCurrentStateName(), isoGameCharacter.getPreviousStateName());
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters;

import zombie.chat.ChatManager;
import zombie.core.Core;
import zombie.input.GameKeyboard;
import zombie.debug.DebugOptions;
import zombie.network.GameClient;
import zombie.vehicles.BaseVehicle;
import zombie.network.GameServer;
import zombie.debug.DebugLog;
import zombie.vehicles.VehicleManager;
import zombie.ai.states.CollideWithWallState;
import zombie.GameTime;
import zombie.iso.IsoDirections;
import zombie.iso.IsoMovingObject;
import zombie.vehicles.PolygonalMap2;
import zombie.network.NetworkVariables;
import zombie.SystemDisabler;
import zombie.network.packets.PlayerPacket;
import zombie.core.math.PZMath;
import zombie.iso.Vector2;
import zombie.core.utils.UpdateTimer;
import zombie.vehicles.PathFindBehavior2;
import zombie.network.packets.EventPacket;
import java.util.LinkedList;

public class NetworkPlayerAI extends NetworkCharacterAI
{
    public final LinkedList<EventPacket> events;
    IsoPlayer player;
    private PathFindBehavior2 pfb2;
    private final UpdateTimer timer;
    private byte lastDirection;
    private boolean needUpdate;
    private boolean blockUpdate;
    public boolean usePathFind;
    public float collidePointX;
    public float collidePointY;
    public float targetX;
    public float targetY;
    public int targetZ;
    public boolean needToMovingUsingPathFinder;
    public boolean forcePathFinder;
    public Vector2 direction;
    public Vector2 distance;
    public boolean moving;
    public byte footstepSoundRadius;
    public int lastBooleanVariables;
    public float lastForwardDirection;
    public float lastPlayerMoveDirLen;
    private boolean pressedMovement;
    private boolean pressedCancelAction;
    public boolean climbFenceOutcomeFall;
    private Vector2 tempo;
    private static final int predictInterval = 1000;
    
    public NetworkPlayerAI(final IsoGameCharacter isoGameCharacter) {
        super(isoGameCharacter);
        this.events = new LinkedList<EventPacket>();
        this.pfb2 = null;
        this.timer = new UpdateTimer();
        this.lastDirection = 0;
        this.needUpdate = false;
        this.blockUpdate = false;
        this.usePathFind = false;
        this.targetX = 0.0f;
        this.targetY = 0.0f;
        this.targetZ = 0;
        this.needToMovingUsingPathFinder = false;
        this.forcePathFinder = false;
        this.direction = new Vector2();
        this.distance = new Vector2();
        this.moving = false;
        this.footstepSoundRadius = 0;
        this.lastBooleanVariables = 0;
        this.lastForwardDirection = 0.0f;
        this.lastPlayerMoveDirLen = 0.0f;
        this.pressedMovement = false;
        this.pressedCancelAction = false;
        this.climbFenceOutcomeFall = false;
        this.tempo = new Vector2();
        this.player = (IsoPlayer)isoGameCharacter;
        this.pfb2 = this.player.getPathFindBehavior2();
        isoGameCharacter.ulBeatenVehicle.Reset(200L);
        this.collidePointX = -1.0f;
        this.collidePointY = -1.0f;
    }
    
    public void needToUpdate() {
        this.needUpdate = true;
    }
    
    public void setBlockUpdate(final boolean blockUpdate) {
        this.blockUpdate = blockUpdate;
    }
    
    public boolean isNeedToUpdate() {
        final int booleanVariables = NetworkPlayerVariables.getBooleanVariables(this.player);
        final byte lastDirection = (byte)(this.player.playerMoveDir.getDirection() * 10.0f);
        if (((this.timer.check() || booleanVariables != this.lastBooleanVariables || this.lastDirection != lastDirection) && !this.blockUpdate) || this.needUpdate) {
            this.lastDirection = lastDirection;
            this.needUpdate = false;
            return true;
        }
        return false;
    }
    
    public void setUpdateTimer(final float n) {
        this.timer.reset(PZMath.clamp((int)n, 200, 3800));
    }
    
    private void setUsingCollide(final PlayerPacket playerPacket, final int n) {
        if (SystemDisabler.useNetworkCharacter) {
            this.player.networkCharacter.checkResetPlayer(n);
        }
        playerPacket.x = (float)this.player.getCurrentSquare().getX();
        playerPacket.y = (float)this.player.getCurrentSquare().getY();
        playerPacket.z = (byte)this.player.getCurrentSquare().getZ();
        playerPacket.usePathFinder = false;
        playerPacket.moveType = NetworkVariables.PredictionTypes.Thump;
    }
    
    private void setUsingExtrapolation(final PlayerPacket playerPacket, final int n, final int n2) {
        final Vector2 toVector = this.player.dir.ToVector();
        if (SystemDisabler.useNetworkCharacter) {
            this.player.networkCharacter.checkResetPlayer(n);
        }
        if (!this.player.isPlayerMoving()) {
            playerPacket.x = this.player.x;
            playerPacket.y = this.player.y;
            playerPacket.z = (byte)this.player.z;
            playerPacket.usePathFinder = false;
            playerPacket.moveType = NetworkVariables.PredictionTypes.Static;
            return;
        }
        final Vector2 tempo = this.tempo;
        if (SystemDisabler.useNetworkCharacter) {
            final NetworkCharacter.Transform predict = this.player.networkCharacter.predict(n2, n, this.player.x, this.player.y, toVector.x, toVector.y);
            tempo.x = predict.position.x;
            tempo.y = predict.position.y;
        }
        else {
            this.player.getDeferredMovement(tempo);
            tempo.x = this.player.x + tempo.x * 0.03f * n2;
            tempo.y = this.player.y + tempo.y * 0.03f * n2;
        }
        if (this.player.z == this.pfb2.getTargetZ() && !PolygonalMap2.instance.lineClearCollide(this.player.x, this.player.y, tempo.x, tempo.y, (int)this.player.z, null)) {
            playerPacket.x = tempo.x;
            playerPacket.y = tempo.y;
            playerPacket.z = (byte)this.pfb2.getTargetZ();
        }
        else {
            final Vector2 collidepoint = PolygonalMap2.instance.getCollidepoint(this.player.x, this.player.y, tempo.x, tempo.y, (int)this.player.z, null, 2);
            playerPacket.collidePointX = collidepoint.x;
            playerPacket.collidePointY = collidepoint.y;
            playerPacket.x = collidepoint.x + ((this.player.dir == IsoDirections.N || this.player.dir == IsoDirections.S) ? 0.0f : ((this.player.dir.index() >= IsoDirections.NW.index() && this.player.dir.index() <= IsoDirections.SW.index()) ? -1.0f : 1.0f));
            playerPacket.y = collidepoint.y + ((this.player.dir == IsoDirections.W || this.player.dir == IsoDirections.E) ? 0.0f : ((this.player.dir.index() >= IsoDirections.SW.index() && this.player.dir.index() <= IsoDirections.SE.index()) ? 1.0f : -1.0f));
            playerPacket.z = (byte)this.player.z;
        }
        playerPacket.usePathFinder = false;
        playerPacket.moveType = NetworkVariables.PredictionTypes.Moving;
    }
    
    private void setUsingPathFindState(final PlayerPacket playerPacket, final int n) {
        if (SystemDisabler.useNetworkCharacter) {
            this.player.networkCharacter.checkResetPlayer(n);
        }
        playerPacket.x = this.pfb2.pathNextX;
        playerPacket.y = this.pfb2.pathNextY;
        playerPacket.z = (byte)this.player.z;
        playerPacket.usePathFinder = true;
        playerPacket.moveType = NetworkVariables.PredictionTypes.PathFind;
    }
    
    public boolean set(final PlayerPacket playerPacket) {
        final int realt = (int)(GameTime.getServerTime() / 1000000L);
        playerPacket.realx = this.player.x;
        playerPacket.realy = this.player.y;
        playerPacket.realz = (byte)this.player.z;
        playerPacket.realdir = (byte)this.player.dir.index();
        playerPacket.realt = realt;
        if (this.player.vehicle == null) {
            playerPacket.VehicleID = -1;
            playerPacket.VehicleSeat = -1;
        }
        else {
            playerPacket.VehicleID = this.player.vehicle.VehicleID;
            playerPacket.VehicleSeat = (short)this.player.vehicle.getSeat(this.player);
        }
        final boolean check = this.timer.check();
        playerPacket.collidePointX = -1.0f;
        playerPacket.collidePointY = -1.0f;
        if (check) {
            this.setUpdateTimer(600.0f);
        }
        if (this.player.getCurrentState() == CollideWithWallState.instance()) {
            this.setUsingCollide(playerPacket, realt);
        }
        else if (this.pfb2.isMovingUsingPathFind()) {
            this.setUsingPathFindState(playerPacket, realt);
        }
        else {
            this.setUsingExtrapolation(playerPacket, realt, 1000);
        }
        final boolean b = this.player.playerMoveDir.getLength() < 0.01 && this.lastPlayerMoveDirLen > 0.01f;
        this.lastPlayerMoveDirLen = this.player.playerMoveDir.getLength();
        playerPacket.booleanVariables = NetworkPlayerVariables.getBooleanVariables(this.player);
        final boolean b2 = this.lastBooleanVariables != playerPacket.booleanVariables;
        this.lastBooleanVariables = playerPacket.booleanVariables;
        playerPacket.direction = this.player.getForwardDirection().getDirection();
        final boolean b3 = Math.abs(this.lastForwardDirection - playerPacket.direction) > 0.2f;
        this.lastForwardDirection = playerPacket.direction;
        playerPacket.footstepSoundRadius = this.footstepSoundRadius;
        return check || b2 || b3 || this.player.isJustMoved() || b;
    }
    
    public void parse(final PlayerPacket playerPacket) {
        if (this.player.isTeleporting()) {
            return;
        }
        this.targetX = PZMath.roundFromEdges(playerPacket.x);
        this.targetY = PZMath.roundFromEdges(playerPacket.y);
        this.targetZ = playerPacket.z;
        this.predictionType = playerPacket.moveType;
        this.needToMovingUsingPathFinder = playerPacket.usePathFinder;
        this.direction.set((float)Math.cos(playerPacket.direction), (float)Math.sin(playerPacket.direction));
        this.distance.set(playerPacket.x - this.player.x, playerPacket.y - this.player.y);
        if (this.usePathFind) {
            this.pfb2.pathToLocationF(playerPacket.x, playerPacket.y, playerPacket.z);
            this.pfb2.walkingOnTheSpot.reset(this.player.x, this.player.y);
        }
        final BaseVehicle vehicleByID = VehicleManager.instance.getVehicleByID(playerPacket.VehicleID);
        NetworkPlayerVariables.setBooleanVariables(this.player, playerPacket.booleanVariables);
        this.player.setbSeenThisFrame(false);
        this.player.setbCouldBeSeenThisFrame(false);
        this.player.TimeSinceLastNetData = 0;
        this.player.ensureOnTile();
        this.player.realx = playerPacket.realx;
        this.player.realy = playerPacket.realy;
        this.player.realz = playerPacket.realz;
        this.player.realdir = IsoDirections.fromIndex(playerPacket.realdir);
        this.collidePointX = playerPacket.collidePointX;
        this.collidePointY = playerPacket.collidePointY;
        playerPacket.variables.apply(this.player);
        this.footstepSoundRadius = playerPacket.footstepSoundRadius;
        if (this.player.getVehicle() == null) {
            if (vehicleByID != null) {
                if (playerPacket.VehicleSeat < 0 || playerPacket.VehicleSeat >= vehicleByID.getMaxPassengers()) {
                    DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;SS)Ljava/lang/String;, this.player.getUsername(), vehicleByID.VehicleID, playerPacket.VehicleSeat));
                }
                else {
                    final IsoGameCharacter character = vehicleByID.getCharacter(playerPacket.VehicleSeat);
                    if (character == null) {
                        if (GameServer.bDebug) {
                            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;SS)Ljava/lang/String;, this.player.getUsername(), vehicleByID.VehicleID, playerPacket.VehicleSeat));
                        }
                        vehicleByID.enterRSync(playerPacket.VehicleSeat, this.player, vehicleByID);
                    }
                    else if (character != this.player) {
                        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.player.getUsername(), ((IsoPlayer)character).getUsername()));
                        this.player.sendObjectChange("exitVehicle");
                    }
                }
            }
        }
        else if (vehicleByID != null) {
            if (vehicleByID != this.player.getVehicle() || this.player.getVehicle().getSeat(this.player) == -1) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;SSSI)Ljava/lang/String;, this.player.getUsername(), vehicleByID.VehicleID, playerPacket.VehicleSeat, this.player.getVehicle().VehicleID, this.player.getVehicle().getSeat(this.player)));
                this.player.sendObjectChange("exitVehicle");
            }
            else {
                final IsoGameCharacter character2 = vehicleByID.getCharacter(playerPacket.VehicleSeat);
                if (character2 == null) {
                    if (vehicleByID.getSeat(this.player) != playerPacket.VehicleSeat) {
                        vehicleByID.switchSeatRSync(this.player, playerPacket.VehicleSeat);
                    }
                }
                else if (character2 != this.player) {
                    DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.player.getUsername(), ((IsoPlayer)character2).getUsername()));
                    this.player.sendObjectChange("exitVehicle");
                }
            }
        }
        else {
            this.player.getVehicle().exitRSync(this.player);
            this.player.setVehicle(null);
        }
        this.setPressedMovement(false);
        this.setPressedCancelAction(false);
    }
    
    public boolean isPressedMovement() {
        return this.pressedMovement;
    }
    
    public void setPressedMovement(final boolean pressedMovement) {
        final boolean b = !this.pressedMovement && pressedMovement;
        this.pressedMovement = pressedMovement;
        if (this.player.isLocal() && b) {
            GameClient.sendEvent(this.player, "Update");
        }
    }
    
    public boolean isPressedCancelAction() {
        return this.pressedCancelAction;
    }
    
    public void setPressedCancelAction(final boolean pressedCancelAction) {
        final boolean b = !this.pressedCancelAction && pressedCancelAction;
        this.pressedCancelAction = pressedCancelAction;
        if (this.player.isLocal() && b) {
            GameClient.sendEvent(this.player, "Update");
        }
    }
    
    public void update() {
        if (DebugOptions.instance.MultiplayerSpawnZombie.getValue() && GameKeyboard.isKeyPressed(44)) {
            if (Core.bDebug && GameKeyboard.isKeyDown(42)) {
                throw new NullPointerException("debug null pointer exception");
            }
            ChatManager.getInstance().showInfoMessage(this.player.getUsername(), "spawn zombie");
            GameClient.SendCommandToServer(String.format("/createhorde2 -x %d -y %d -z %d", (int)this.player.getX(), (int)this.player.getY(), (int)this.player.getZ()));
        }
    }
}

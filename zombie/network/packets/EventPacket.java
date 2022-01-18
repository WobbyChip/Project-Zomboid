// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network.packets;

import java.util.HashMap;
import zombie.characters.CharacterTimedActions.BaseAction;
import zombie.ai.states.ClimbSheetRopeState;
import zombie.ai.states.ClimbDownSheetRopeState;
import zombie.ai.State;
import zombie.ai.states.SmashWindowState;
import zombie.characters.IsoZombie;
import zombie.characters.NetworkPlayerVariables;
import zombie.vehicles.VehiclePart;
import zombie.vehicles.BaseVehicle;
import zombie.characters.IsoGameCharacter;
import zombie.vehicles.VehicleManager;
import zombie.inventory.InventoryItem;
import zombie.util.StringUtils;
import zombie.ai.states.FishingState;
import zombie.iso.IsoGridSquare;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.objects.IsoWindowFrame;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.IsoObject;
import zombie.iso.IsoDirections;
import zombie.iso.objects.IsoWindow;
import zombie.core.raknet.UdpConnection;
import zombie.core.network.ByteBufferWriter;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.GameWindow;
import java.nio.ByteBuffer;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.core.Core;
import zombie.characters.IsoPlayer;

public class EventPacket implements INetworkPacket
{
    public static final int MAX_PLAYER_EVENTS = 10;
    private static final long EVENT_TIMEOUT = 5000L;
    private static final short EVENT_FLAGS_VAULT_OVER_SPRINT = 1;
    private static final short EVENT_FLAGS_VAULT_OVER_RUN = 2;
    private static final short EVENT_FLAGS_BUMP_FALL = 4;
    private static final short EVENT_FLAGS_BUMP_STAGGERED = 8;
    private static final short EVENT_FLAGS_ACTIVATE_ITEM = 16;
    private static final short EVENT_FLAGS_CLIMB_SUCCESS = 32;
    private static final short EVENT_FLAGS_CLIMB_STRUGGLE = 64;
    private static final short EVENT_FLAGS_BUMP_FROM_BEHIND = 128;
    private static final short EVENT_FLAGS_BUMP_TARGET_TYPE = 256;
    private static final short EVENT_FLAGS_PRESSED_MOVEMENT = 512;
    private static final short EVENT_FLAGS_PRESSED_CANCEL_ACTION = 1024;
    private static final short EVENT_FLAGS_SMASH_CAR_WINDOW = 2048;
    private static final short EVENT_FLAGS_FITNESS_FINISHED = 4096;
    private short id;
    public float x;
    public float y;
    public float z;
    private byte eventID;
    private String type1;
    private String type2;
    private String type3;
    private String type4;
    private float strafeSpeed;
    private float walkSpeed;
    private float walkInjury;
    private int booleanVariables;
    private short flags;
    private IsoPlayer player;
    private EventType event;
    private long timestamp;
    
    @Override
    public String getDescription() {
        return invokedynamic(makeConcatWithConstants:(SLjava/lang/String;Ljava/lang/String;FFFLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;SI)Ljava/lang/String;, this.id, (this.player == null) ? "?" : this.player.getUsername(), (this.event == null) ? "?" : this.event.name(), this.x, this.y, this.z, this.type1, this.type2, this.type3, this.type4, this.flags, this.booleanVariables);
    }
    
    @Override
    public boolean isConsistent() {
        final boolean b = this.player != null && this.event != null;
        if (!b && Core.bDebug) {
            DebugLog.log(DebugType.Multiplayer, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getDescription()));
        }
        return b;
    }
    
    @Override
    public void parse(final ByteBuffer byteBuffer) {
        this.id = byteBuffer.getShort();
        this.x = byteBuffer.getFloat();
        this.y = byteBuffer.getFloat();
        this.z = byteBuffer.getFloat();
        this.eventID = byteBuffer.get();
        this.type1 = GameWindow.ReadString(byteBuffer);
        this.type2 = GameWindow.ReadString(byteBuffer);
        this.type3 = GameWindow.ReadString(byteBuffer);
        this.type4 = GameWindow.ReadString(byteBuffer);
        this.strafeSpeed = byteBuffer.getFloat();
        this.walkSpeed = byteBuffer.getFloat();
        this.walkInjury = byteBuffer.getFloat();
        this.booleanVariables = byteBuffer.getInt();
        this.flags = byteBuffer.getShort();
        if (this.eventID >= 0 && this.eventID < EventType.values().length) {
            this.event = EventType.values()[this.eventID];
        }
        else {
            DebugLog.Multiplayer.warn(invokedynamic(makeConcatWithConstants:(B)Ljava/lang/String;, this.eventID));
            this.event = null;
        }
        if (GameServer.bServer) {
            this.player = GameServer.IDToPlayerMap.get(this.id);
        }
        else if (GameClient.bClient) {
            this.player = GameClient.IDToPlayerMap.get(this.id);
        }
        else {
            this.player = null;
        }
        this.timestamp = System.currentTimeMillis() + 5000L;
    }
    
    @Override
    public void write(final ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putShort(this.id);
        byteBufferWriter.putFloat(this.x);
        byteBufferWriter.putFloat(this.y);
        byteBufferWriter.putFloat(this.z);
        byteBufferWriter.putByte(this.eventID);
        byteBufferWriter.putUTF(this.type1);
        byteBufferWriter.putUTF(this.type2);
        byteBufferWriter.putUTF(this.type3);
        byteBufferWriter.putUTF(this.type4);
        byteBufferWriter.putFloat(this.strafeSpeed);
        byteBufferWriter.putFloat(this.walkSpeed);
        byteBufferWriter.putFloat(this.walkInjury);
        byteBufferWriter.putInt(this.booleanVariables);
        byteBufferWriter.putShort(this.flags);
    }
    
    public boolean isRelevant(final UdpConnection udpConnection) {
        return udpConnection.RelevantTo(this.x, this.y);
    }
    
    public boolean isMovableEvent() {
        return this.isConsistent() && (EventType.EventClimbFence.equals(this.event) || EventType.EventFallClimb.equals(this.event));
    }
    
    private boolean requireNonMoving() {
        return this.isConsistent() && (EventType.EventClimbWindow.equals(this.event) || EventType.EventClimbFence.equals(this.event) || EventType.EventClimbDownRope.equals(this.event) || EventType.EventClimbRope.equals(this.event) || EventType.EventClimbWall.equals(this.event));
    }
    
    private IsoWindow getWindow(final IsoPlayer isoPlayer) {
        final IsoDirections[] values = IsoDirections.values();
        for (int length = values.length, i = 0; i < length; ++i) {
            final IsoObject contextDoorOrWindowOrWindowFrame = isoPlayer.getContextDoorOrWindowOrWindowFrame(values[i]);
            if (contextDoorOrWindowOrWindowFrame instanceof IsoWindow) {
                return (IsoWindow)contextDoorOrWindowOrWindowFrame;
            }
        }
        return null;
    }
    
    private IsoObject getObject(final IsoPlayer isoPlayer) {
        final IsoDirections[] values = IsoDirections.values();
        for (int length = values.length, i = 0; i < length; ++i) {
            final IsoObject contextDoorOrWindowOrWindowFrame = isoPlayer.getContextDoorOrWindowOrWindowFrame(values[i]);
            if (contextDoorOrWindowOrWindowFrame instanceof IsoWindow || contextDoorOrWindowOrWindowFrame instanceof IsoThumpable || IsoWindowFrame.isWindowFrame(contextDoorOrWindowOrWindowFrame)) {
                return contextDoorOrWindowOrWindowFrame;
            }
        }
        return null;
    }
    
    private IsoDirections checkCurrentIsEventGridSquareFence(final IsoPlayer isoPlayer) {
        final IsoGridSquare gridSquare = isoPlayer.getCell().getGridSquare(this.x, this.y, this.z);
        final IsoGridSquare gridSquare2 = isoPlayer.getCell().getGridSquare(this.x, this.y + 1.0f, this.z);
        final IsoGridSquare gridSquare3 = isoPlayer.getCell().getGridSquare(this.x + 1.0f, this.y, this.z);
        IsoDirections isoDirections;
        if (gridSquare.Is(IsoFlagType.HoppableN)) {
            isoDirections = IsoDirections.N;
        }
        else if (gridSquare.Is(IsoFlagType.HoppableW)) {
            isoDirections = IsoDirections.W;
        }
        else if (gridSquare2.Is(IsoFlagType.HoppableN)) {
            isoDirections = IsoDirections.S;
        }
        else if (gridSquare3.Is(IsoFlagType.HoppableW)) {
            isoDirections = IsoDirections.E;
        }
        else {
            isoDirections = IsoDirections.Max;
        }
        return isoDirections;
    }
    
    public boolean isTimeout() {
        return System.currentTimeMillis() > this.timestamp;
    }
    
    public void tryProcess() {
        if (this.isConsistent()) {
            if (this.player.networkAI.events.size() < 10) {
                this.player.networkAI.events.add(this);
            }
            else {
                DebugLog.Multiplayer.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getDescription()));
            }
        }
    }
    
    public boolean process(final IsoPlayer isoPlayer) {
        boolean b = false;
        if (this.isConsistent()) {
            isoPlayer.overridePrimaryHandModel = null;
            isoPlayer.overrideSecondaryHandModel = null;
            if ((isoPlayer.getCurrentSquare() == isoPlayer.getCell().getGridSquare(this.x, this.y, this.z) && !isoPlayer.isPlayerMoving()) || !this.requireNonMoving()) {
                switch (this.event) {
                    case EventSetActivatedPrimary: {
                        if (isoPlayer.getPrimaryHandItem() != null && isoPlayer.getPrimaryHandItem().canEmitLight()) {
                            isoPlayer.getPrimaryHandItem().setActivatedRemote((this.flags & 0x10) != 0x0);
                            b = true;
                            break;
                        }
                        break;
                    }
                    case EventSetActivatedSecondary: {
                        if (isoPlayer.getSecondaryHandItem() != null && isoPlayer.getSecondaryHandItem().canEmitLight()) {
                            isoPlayer.getSecondaryHandItem().setActivatedRemote((this.flags & 0x10) != 0x0);
                            b = true;
                            break;
                        }
                        break;
                    }
                    case EventFallClimb: {
                        isoPlayer.setVariable("ClimbFenceOutcome", "fall");
                        isoPlayer.setVariable("BumpDone", true);
                        isoPlayer.setFallOnFront(true);
                        b = true;
                        break;
                    }
                    case collideWithWall: {
                        isoPlayer.setCollideType(this.type1);
                        isoPlayer.actionContext.reportEvent("collideWithWall");
                        b = true;
                        break;
                    }
                    case EventFishing: {
                        isoPlayer.setVariable("FishingStage", this.type1);
                        if (!FishingState.instance().equals(isoPlayer.getCurrentState())) {
                            isoPlayer.setVariable("forceGetUp", true);
                            isoPlayer.actionContext.reportEvent("EventFishing");
                        }
                        b = true;
                        break;
                    }
                    case EventFitness: {
                        isoPlayer.setVariable("ExerciseType", this.type1);
                        isoPlayer.setVariable("FitnessFinished", false);
                        isoPlayer.actionContext.reportEvent("EventFitness");
                        b = true;
                        break;
                    }
                    case EventUpdateFitness: {
                        isoPlayer.clearVariable("ExerciseHand");
                        isoPlayer.setVariable("ExerciseType", this.type2);
                        if (!StringUtils.isNullOrEmpty(this.type1)) {
                            isoPlayer.setVariable("ExerciseHand", this.type1);
                        }
                        isoPlayer.setFitnessSpeed();
                        if ((this.flags & 0x1000) != 0x0) {
                            isoPlayer.setVariable("ExerciseStarted", false);
                            isoPlayer.setVariable("ExerciseEnded", true);
                        }
                        isoPlayer.setPrimaryHandItem(null);
                        isoPlayer.setSecondaryHandItem(null);
                        isoPlayer.overridePrimaryHandModel = null;
                        isoPlayer.overrideSecondaryHandModel = null;
                        isoPlayer.overridePrimaryHandModel = this.type3;
                        isoPlayer.overrideSecondaryHandModel = this.type4;
                        isoPlayer.resetModelNextFrame();
                        b = true;
                        break;
                    }
                    case EventEmote: {
                        isoPlayer.setVariable("emote", this.type1);
                        isoPlayer.actionContext.reportEvent("EventEmote");
                        b = true;
                        break;
                    }
                    case EventSitOnGround: {
                        isoPlayer.actionContext.reportEvent("EventSitOnGround");
                        b = true;
                        break;
                    }
                    case EventClimbRope: {
                        isoPlayer.climbSheetRope();
                        b = true;
                        break;
                    }
                    case EventClimbDownRope: {
                        isoPlayer.climbDownSheetRope();
                        b = true;
                        break;
                    }
                    case EventClimbFence: {
                        final IsoDirections checkCurrentIsEventGridSquareFence = this.checkCurrentIsEventGridSquareFence(isoPlayer);
                        if (checkCurrentIsEventGridSquareFence != IsoDirections.Max) {
                            isoPlayer.climbOverFence(checkCurrentIsEventGridSquareFence);
                            if (isoPlayer.isSprinting()) {
                                isoPlayer.setVariable("VaultOverSprint", true);
                            }
                            if (isoPlayer.isRunning()) {
                                isoPlayer.setVariable("VaultOverRun", true);
                            }
                            b = true;
                            break;
                        }
                        break;
                    }
                    case EventClimbWall: {
                        isoPlayer.setClimbOverWallStruggle((this.flags & 0x40) != 0x0);
                        isoPlayer.setClimbOverWallSuccess((this.flags & 0x20) != 0x0);
                        final IsoDirections[] values = IsoDirections.values();
                        for (int length = values.length, i = 0; i < length; ++i) {
                            if (isoPlayer.climbOverWall(values[i])) {
                                return true;
                            }
                        }
                        break;
                    }
                    case EventClimbWindow: {
                        final IsoObject object = this.getObject(isoPlayer);
                        if (object instanceof IsoWindow) {
                            isoPlayer.climbThroughWindow((IsoWindow)object);
                            b = true;
                        }
                        else if (object instanceof IsoThumpable) {
                            isoPlayer.climbThroughWindow((IsoThumpable)object);
                            b = true;
                        }
                        if (IsoWindowFrame.isWindowFrame(object)) {
                            isoPlayer.climbThroughWindowFrame(object);
                            b = true;
                            break;
                        }
                        break;
                    }
                    case EventOpenWindow: {
                        final IsoWindow window = this.getWindow(isoPlayer);
                        if (window != null) {
                            isoPlayer.openWindow(window);
                            b = true;
                            break;
                        }
                        break;
                    }
                    case EventCloseWindow: {
                        final IsoWindow window2 = this.getWindow(isoPlayer);
                        if (window2 != null) {
                            isoPlayer.closeWindow(window2);
                            b = true;
                            break;
                        }
                        break;
                    }
                    case EventSmashWindow: {
                        if ((this.flags & 0x800) != 0x0) {
                            final BaseVehicle vehicleByID = VehicleManager.instance.getVehicleByID(Short.parseShort(this.type1));
                            if (vehicleByID != null) {
                                final VehiclePart partById = vehicleByID.getPartById(this.type2);
                                if (partById != null && partById.getWindow() != null) {
                                    isoPlayer.smashCarWindow(partById);
                                    b = true;
                                }
                            }
                            break;
                        }
                        final IsoWindow window3 = this.getWindow(isoPlayer);
                        if (window3 != null) {
                            isoPlayer.smashWindow(window3);
                            b = true;
                        }
                        break;
                    }
                    case wasBumped: {
                        isoPlayer.setBumpDone(false);
                        isoPlayer.setVariable("BumpFallAnimFinished", false);
                        isoPlayer.setBumpType(this.type1);
                        isoPlayer.setBumpFallType(this.type2);
                        isoPlayer.setBumpFall((this.flags & 0x4) != 0x0);
                        isoPlayer.setBumpStaggered((this.flags & 0x8) != 0x0);
                        isoPlayer.reportEvent("wasBumped");
                        if (!StringUtils.isNullOrEmpty(this.type3) && !StringUtils.isNullOrEmpty(this.type4)) {
                            IsoGameCharacter isoGameCharacter;
                            if ((this.flags & 0x100) != 0x0) {
                                isoGameCharacter = (IsoGameCharacter)GameClient.IDToZombieMap.get(Short.parseShort(this.type3));
                            }
                            else {
                                isoGameCharacter = GameClient.IDToPlayerMap.get(Short.parseShort(this.type3));
                            }
                            if (isoGameCharacter != null) {
                                isoGameCharacter.setBumpType(this.type4);
                                isoGameCharacter.setHitFromBehind((this.flags & 0x80) != 0x0);
                            }
                        }
                        b = true;
                        break;
                    }
                    case EventOverrideItem: {
                        if (isoPlayer.getNetworkCharacterAI().getAction() != null) {
                            isoPlayer.getNetworkCharacterAI().setOverride(true, this.type1, this.type2);
                        }
                        b = true;
                        break;
                    }
                    case ChargeSpearConnect: {
                        b = true;
                        break;
                    }
                    case Update: {
                        isoPlayer.networkAI.setPressedMovement((this.flags & 0x200) != 0x0);
                        isoPlayer.networkAI.setPressedCancelAction((this.flags & 0x400) != 0x0);
                        b = true;
                        break;
                    }
                    default: {
                        DebugLog.Multiplayer.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getDescription()));
                        b = true;
                        break;
                    }
                }
            }
        }
        return b;
    }
    
    public boolean set(final IsoPlayer player, final String anObject) {
        boolean b = false;
        this.player = player;
        this.id = player.getOnlineID();
        this.x = player.getX();
        this.y = player.getY();
        this.z = player.getZ();
        this.type1 = null;
        this.type2 = null;
        this.type3 = null;
        this.type4 = null;
        this.booleanVariables = NetworkPlayerVariables.getBooleanVariables(player);
        this.strafeSpeed = player.getVariableFloat("StrafeSpeed", 1.0f);
        this.walkSpeed = player.getVariableFloat("WalkSpeed", 1.0f);
        this.walkInjury = player.getVariableFloat("WalkInjury", 0.0f);
        this.flags = 0;
        for (final EventType event : EventType.values()) {
            if (event.name().equals(anObject)) {
                this.event = event;
                this.eventID = (byte)event.ordinal();
                switch (event) {
                    case EventSetActivatedSecondary: {
                        this.flags |= (short)(player.getSecondaryHandItem().isActivated() ? 16 : 0);
                        break;
                    }
                    case EventSetActivatedPrimary: {
                        this.flags |= (short)(player.getPrimaryHandItem().isActivated() ? 16 : 0);
                        break;
                    }
                    case EventClimbFence: {
                        if (player.getVariableBoolean("VaultOverRun")) {
                            this.flags |= 0x2;
                        }
                        if (player.getVariableBoolean("VaultOverSprint")) {
                            this.flags |= 0x1;
                            break;
                        }
                        break;
                    }
                    case collideWithWall: {
                        this.type1 = player.getCollideType();
                        break;
                    }
                    case EventEmote: {
                        this.type1 = player.getVariableString("emote");
                        break;
                    }
                    case EventFishing: {
                        this.type1 = player.getVariableString("FishingStage");
                        break;
                    }
                    case EventFitness: {
                        this.type1 = player.getVariableString("ExerciseType");
                        break;
                    }
                    case EventUpdateFitness: {
                        this.type1 = player.getVariableString("ExerciseHand");
                        this.type2 = player.getVariableString("ExerciseType");
                        if (player.getPrimaryHandItem() != null) {
                            this.type3 = player.getPrimaryHandItem().getStaticModel();
                        }
                        if (player.getSecondaryHandItem() != null && player.getSecondaryHandItem() != player.getPrimaryHandItem()) {
                            this.type4 = player.getSecondaryHandItem().getStaticModel();
                        }
                        this.flags |= (short)(player.getVariableBoolean("FitnessFinished") ? 4096 : 0);
                        break;
                    }
                    case wasBumped: {
                        this.type1 = player.getBumpType();
                        this.type2 = player.getBumpFallType();
                        this.flags |= (short)(player.isBumpFall() ? 4 : 0);
                        this.flags |= (short)(player.isBumpStaggered() ? 8 : 0);
                        if (player.getBumpedChr() == null) {
                            break;
                        }
                        this.type3 = String.valueOf(player.getBumpedChr().getOnlineID());
                        this.type4 = player.getBumpedChr().getBumpType();
                        this.flags |= (short)(player.isHitFromBehind() ? 128 : 0);
                        if (player.getBumpedChr() instanceof IsoZombie) {
                            this.flags |= 0x100;
                            break;
                        }
                        break;
                    }
                    case EventClimbWall: {
                        this.flags |= (short)(player.isClimbOverWallSuccess() ? 32 : 0);
                        this.flags |= (short)(player.isClimbOverWallStruggle() ? 64 : 0);
                        break;
                    }
                    case EventOverrideItem: {
                        if (player.getNetworkCharacterAI().getAction() != null) {
                            final BaseAction action = player.getNetworkCharacterAI().getAction();
                            this.type1 = ((action.getPrimaryHandItem() == null) ? action.getPrimaryHandMdl() : action.getPrimaryHandItem().getStaticModel());
                            this.type2 = ((action.getSecondaryHandItem() == null) ? action.getSecondaryHandMdl() : action.getSecondaryHandItem().getStaticModel());
                            break;
                        }
                        return false;
                    }
                    case Update: {
                        this.flags |= (short)(player.networkAI.isPressedMovement() ? 512 : 0);
                        this.flags |= (short)(player.networkAI.isPressedCancelAction() ? 1024 : 0);
                        break;
                    }
                    case EventSmashWindow: {
                        final HashMap<Object, Object> stateMachineParams = player.getStateMachineParams(SmashWindowState.instance());
                        if (stateMachineParams.get(1) instanceof BaseVehicle && stateMachineParams.get(2) instanceof VehiclePart) {
                            final BaseVehicle baseVehicle = stateMachineParams.get(1);
                            final VehiclePart vehiclePart = stateMachineParams.get(2);
                            this.flags |= 0x800;
                            this.type1 = String.valueOf(baseVehicle.getId());
                            this.type2 = vehiclePart.getId();
                            break;
                        }
                        break;
                    }
                    case EventFallClimb:
                    case EventSitOnGround:
                    case EventClimbRope:
                    case EventClimbDownRope:
                    case EventClimbWindow:
                    case EventOpenWindow:
                    case EventCloseWindow:
                    case ChargeSpearConnect: {
                        break;
                    }
                    default: {
                        DebugLog.Multiplayer.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getDescription()));
                        return false;
                    }
                }
                b = (!ClimbDownSheetRopeState.instance().equals(player.getCurrentState()) && !ClimbSheetRopeState.instance().equals(player.getCurrentState()));
            }
        }
        return b;
    }
    
    public enum EventType
    {
        EventSetActivatedPrimary, 
        EventSetActivatedSecondary, 
        EventFishing, 
        EventFitness, 
        EventEmote, 
        EventClimbFence, 
        EventClimbDownRope, 
        EventClimbRope, 
        EventClimbWall, 
        EventClimbWindow, 
        EventOpenWindow, 
        EventCloseWindow, 
        EventSmashWindow, 
        EventSitOnGround, 
        wasBumped, 
        collideWithWall, 
        EventUpdateFitness, 
        EventFallClimb, 
        EventOverrideItem, 
        ChargeSpearConnect, 
        Update, 
        Unknown;
        
        private static /* synthetic */ EventType[] $values() {
            return new EventType[] { EventType.EventSetActivatedPrimary, EventType.EventSetActivatedSecondary, EventType.EventFishing, EventType.EventFitness, EventType.EventEmote, EventType.EventClimbFence, EventType.EventClimbDownRope, EventType.EventClimbRope, EventType.EventClimbWall, EventType.EventClimbWindow, EventType.EventOpenWindow, EventType.EventCloseWindow, EventType.EventSmashWindow, EventType.EventSitOnGround, EventType.wasBumped, EventType.collideWithWall, EventType.EventUpdateFitness, EventType.EventFallClimb, EventType.EventOverrideItem, EventType.ChargeSpearConnect, EventType.Update, EventType.Unknown };
        }
        
        static {
            $VALUES = $values();
        }
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters;

import zombie.GameTime;
import java.util.concurrent.TimeUnit;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.core.Core;
import java.util.Comparator;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.HashMap;
import zombie.characters.CharacterTimedActions.BaseAction;
import zombie.network.packets.hit.VehicleHitPacket;
import zombie.network.packets.DeadCharacterPacket;
import zombie.network.NetworkVariables;

public abstract class NetworkCharacterAI
{
    private static final short VEHICLE_HIT_DELAY_MS = 500;
    public NetworkVariables.PredictionTypes predictionType;
    protected DeadCharacterPacket deadBody;
    protected VehicleHitPacket vehicleHit;
    protected float timestamp;
    protected BaseAction action;
    protected long noCollisionTime;
    protected boolean wasLocal;
    protected final HitReactionNetworkAI hitReaction;
    private final IsoGameCharacter character;
    public NetworkTeleport.NetworkTeleportDebug teleportDebug;
    public final HashMap<Integer, String> debugData;
    
    public NetworkCharacterAI(final IsoGameCharacter character) {
        this.debugData = new LinkedHashMap<Integer, String>() {
            @Override
            protected boolean removeEldestEntry(final Map.Entry<Integer, String> entry) {
                return this.size() > 10;
            }
        };
        this.character = character;
        this.deadBody = null;
        this.wasLocal = false;
        this.vehicleHit = null;
        this.noCollisionTime = 0L;
        this.hitReaction = new HitReactionNetworkAI(character);
        this.predictionType = NetworkVariables.PredictionTypes.None;
        this.clearTeleportDebug();
    }
    
    public void reset() {
        this.deadBody = null;
        this.wasLocal = false;
        this.vehicleHit = null;
        this.noCollisionTime = 0L;
        this.hitReaction.finish();
        this.predictionType = NetworkVariables.PredictionTypes.None;
        this.clearTeleportDebug();
    }
    
    public void setLocal(final boolean wasLocal) {
        this.wasLocal = wasLocal;
    }
    
    public boolean wasLocal() {
        return this.wasLocal;
    }
    
    public NetworkTeleport.NetworkTeleportDebug getTeleportDebug() {
        return this.teleportDebug;
    }
    
    public void clearTeleportDebug() {
        this.teleportDebug = null;
        this.debugData.clear();
    }
    
    public void setTeleportDebug(final NetworkTeleport.NetworkTeleportDebug teleportDebug) {
        this.teleportDebug = teleportDebug;
        this.debugData.entrySet().stream().sorted((Comparator<? super Object>)Map.Entry.comparingByKey(Comparator.naturalOrder())).forEach(entry -> {
            if (Core.bDebug) {
                DebugLog.log(DebugType.Multiplayer, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (String)entry.getValue()));
            }
            return;
        });
        if (Core.bDebug) {
            DebugLog.log(DebugType.Multiplayer, String.format("NetworkTeleport %s id=%d distance=%.3f prediction=%s", this.character.getClass().getSimpleName(), this.character.getOnlineID(), teleportDebug.getDistance(), this.predictionType));
        }
    }
    
    public void addTeleportData(final int i, final String value) {
        this.debugData.put(i, value);
    }
    
    public void processDeadBody() {
        if (this.isSetDeadBody() && !this.hitReaction.isSetup() && !this.hitReaction.isStarted()) {
            this.deadBody.process();
            this.setDeadBody(null);
        }
    }
    
    public void setDeadBody(final DeadCharacterPacket deadBody) {
        this.deadBody = deadBody;
        if (Core.bDebug) {
            DebugLog.log(DebugType.Death, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (deadBody == null) ? "processed" : "postpone"));
        }
    }
    
    public boolean isSetDeadBody() {
        return this.deadBody != null && this.deadBody.isConsistent();
    }
    
    public void setAction(final BaseAction action) {
        this.action = action;
    }
    
    public BaseAction getAction() {
        return this.action;
    }
    
    public void startAction() {
        if (this.action != null) {
            this.action.start();
        }
    }
    
    public void stopAction() {
        if (this.action != null) {
            this.setOverride(false, null, null);
            this.action.stop();
        }
    }
    
    public void setOverride(final boolean forceNullOverride, final String overridePrimaryHandModel, final String overrideSecondaryHandModel) {
        if (this.action != null) {
            this.action.chr.forceNullOverride = forceNullOverride;
            this.action.chr.overridePrimaryHandModel = overridePrimaryHandModel;
            this.action.chr.overrideSecondaryHandModel = overrideSecondaryHandModel;
            this.action.chr.resetModelNextFrame();
        }
    }
    
    public void processVehicleHit() {
        this.vehicleHit.tryProcessInternal();
        this.setVehicleHit(null);
    }
    
    public void setVehicleHit(final VehicleHitPacket vehicleHit) {
        this.vehicleHit = vehicleHit;
        this.timestamp = (float)TimeUnit.NANOSECONDS.toMillis(GameTime.getServerTime());
        if (Core.bDebug) {
            DebugLog.log(DebugType.Damage, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (vehicleHit == null) ? "processed" : "postpone"));
        }
    }
    
    public boolean isSetVehicleHit() {
        return this.vehicleHit != null && this.vehicleHit.isConsistent();
    }
    
    public void resetVehicleHitTimeout() {
        this.timestamp = (float)(TimeUnit.NANOSECONDS.toMillis(GameTime.getServerTime()) - 500L);
        if (this.vehicleHit == null && Core.bDebug) {
            DebugLog.log(DebugType.Damage, "VehicleHit is not set");
        }
    }
    
    public boolean isVehicleHitTimeout() {
        final boolean b = TimeUnit.NANOSECONDS.toMillis(GameTime.getServerTime()) - this.timestamp >= 500.0f;
        if (b && Core.bDebug) {
            DebugLog.log(DebugType.Multiplayer, "VehicleHit timeout");
        }
        return b;
    }
    
    public void updateHitVehicle() {
        if (this.isSetVehicleHit() && this.isVehicleHitTimeout()) {
            this.processVehicleHit();
        }
    }
    
    public boolean isCollisionEnabled() {
        return this.noCollisionTime == 0L;
    }
    
    public boolean isNoCollisionTimeout() {
        final boolean b = GameTime.getServerTimeMills() > this.noCollisionTime;
        if (b) {
            this.setNoCollision(0L);
        }
        return b;
    }
    
    public void setNoCollision(final long n) {
        if (n == 0L) {
            this.noCollisionTime = 0L;
            if (Core.bDebug) {
                DebugLog.log(DebugType.Multiplayer, "SetNoCollision: disabled");
            }
        }
        else {
            this.noCollisionTime = GameTime.getServerTimeMills() + n;
            if (Core.bDebug) {
                DebugLog.log(DebugType.Multiplayer, invokedynamic(makeConcatWithConstants:(J)Ljava/lang/String;, n));
            }
        }
    }
}

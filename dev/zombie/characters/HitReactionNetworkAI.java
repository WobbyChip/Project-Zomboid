// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters;

import zombie.network.GameServer;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.PolygonalMap2;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoUtils;
import zombie.ai.states.PlayerOnGroundState;
import zombie.ai.states.PlayerKnockedDown;
import zombie.ai.states.PlayerFallDownState;
import zombie.ai.states.ZombieOnGroundState;
import zombie.ai.State;
import zombie.ai.states.ZombieFallDownState;
import zombie.iso.IsoWorld;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoDirections;
import zombie.core.math.PZMath;
import zombie.debug.DebugType;
import zombie.core.Core;
import zombie.debug.DebugLog;
import zombie.GameTime;
import zombie.iso.Vector2;

public class HitReactionNetworkAI
{
    private static final float G = 2.0f;
    private static final float DURATION = 600.0f;
    public final Vector2 startPosition;
    public final Vector2 finalPosition;
    public byte finalPositionZ;
    public final Vector2 startDirection;
    public final Vector2 finalDirection;
    private float startAngle;
    private float finalAngle;
    private final IsoGameCharacter character;
    private long startTime;
    
    public HitReactionNetworkAI(final IsoGameCharacter character) {
        this.startPosition = new Vector2();
        this.finalPosition = new Vector2();
        this.finalPositionZ = 0;
        this.startDirection = new Vector2();
        this.finalDirection = new Vector2();
        this.character = character;
        this.startTime = 0L;
    }
    
    public boolean isSetup() {
        return this.finalPosition.x != 0.0f && this.finalPosition.y != 0.0f;
    }
    
    public boolean isStarted() {
        return this.startTime > 0L;
    }
    
    public void start() {
        if (this.isSetup() && !this.isStarted()) {
            this.startTime = GameTime.getServerTimeMills();
            if (this.startPosition.x != this.character.x || this.startPosition.y != this.character.y) {
                DebugLog.Multiplayer.warn((Object)"HitReaction start shifted");
            }
            if (Core.bDebug) {
                DebugLog.log(DebugType.Damage, String.format("HitReaction start id=%d: %s / %s => %s ", this.character.getOnlineID(), this.getActualDescription(), this.getStartDescription(), this.getFinalDescription()));
            }
        }
    }
    
    public void finish() {
        if (this.startTime != 0L && Core.bDebug) {
            DebugLog.log(DebugType.Damage, String.format("HitReaction finish id=%d: %s / %s => %s ", this.character.getOnlineID(), this.getActualDescription(), this.getStartDescription(), this.getFinalDescription()));
        }
        this.startTime = 0L;
        this.setup(0.0f, 0.0f, (byte)0, 0.0f);
    }
    
    public void setup(final float n, final float n2, final byte finalPositionZ, Float value) {
        this.startPosition.set(this.character.x, this.character.y);
        this.finalPosition.set(n, n2);
        this.finalPositionZ = finalPositionZ;
        this.startDirection.set(this.character.getForwardDirection());
        this.startAngle = this.character.getAnimAngleRadians();
        final Vector2 set = new Vector2().set(this.finalPosition.x - this.startPosition.x, this.finalPosition.y - this.startPosition.y);
        if (value == null) {
            set.normalize();
            value = set.dot(this.character.getForwardDirection());
            PZMath.lerp(this.finalDirection, set, this.character.getForwardDirection(), Math.abs(value));
            IsoMovingObject.getVectorFromDirection(this.finalDirection, IsoDirections.fromAngle(this.finalDirection));
        }
        else {
            this.finalDirection.setLengthAndDirection(value, 1.0f);
        }
        this.finalAngle = value;
        if (this.isSetup() && Core.bDebug) {
            DebugLog.log(DebugType.Damage, String.format("HitReaction setup id=%d: %s / %s => %s ", this.character.getOnlineID(), this.getActualDescription(), this.getStartDescription(), this.getFinalDescription()));
        }
    }
    
    private void moveInternal(final float nx, final float ny, final float n, final float n2) {
        this.character.nx = nx;
        this.character.ny = ny;
        this.character.setDir(IsoDirections.fromAngle(n, n2));
        this.character.setForwardDirection(n, n2);
        this.character.getAnimationPlayer().SetForceDir(this.character.getForwardDirection());
    }
    
    public void moveFinal() {
        this.moveInternal(this.finalPosition.x, this.finalPosition.y, this.finalDirection.x, this.finalDirection.y);
        final IsoGameCharacter character = this.character;
        final IsoGameCharacter character2 = this.character;
        final IsoGameCharacter character3 = this.character;
        final float x = this.finalPosition.x;
        character3.x = x;
        character2.nx = x;
        character.lx = x;
        final IsoGameCharacter character4 = this.character;
        final IsoGameCharacter character5 = this.character;
        final IsoGameCharacter character6 = this.character;
        final float y = this.finalPosition.y;
        character6.y = y;
        character5.ny = y;
        character4.ly = y;
        this.character.setCurrent(IsoWorld.instance.CurrentCell.getGridSquare((int)this.finalPosition.x, (int)this.finalPosition.y, this.character.z));
        if (Core.bDebug) {
            DebugLog.log(DebugType.Damage, String.format("HitReaction final id=%d: %s / %s => %s ", this.character.getOnlineID(), this.getActualDescription(), this.getStartDescription(), this.getFinalDescription()));
            DebugLog.log(DebugType.Multiplayer, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getDescription()));
        }
    }
    
    public void move() {
        if (this.finalPositionZ != (byte)this.character.z) {
            DebugLog.log(String.format("HitReaction interrupt id=%d: z-final:%d z-current=%d", this.character.getOnlineID(), this.finalPositionZ, (byte)this.character.z));
            this.finish();
            return;
        }
        float min = Math.min(1.0f, Math.max(0.0f, (GameTime.getServerTimeMills() - this.startTime) / 600.0f));
        if (this.startPosition.x == this.finalPosition.x && this.startPosition.y == this.finalPosition.y) {
            min = 1.0f;
        }
        if (min < 1.0f) {
            final float n = (PZMath.gain(min * 0.5f + 0.5f, 2.0f) - 0.5f) * 2.0f;
            this.moveInternal(PZMath.lerp(this.startPosition.x, this.finalPosition.x, n), PZMath.lerp(this.startPosition.y, this.finalPosition.y, n), PZMath.lerp(this.startDirection.x, this.finalDirection.x, n), PZMath.lerp(this.startDirection.y, this.finalDirection.y, n));
        }
        else {
            this.moveFinal();
            this.finish();
        }
    }
    
    public boolean isDoSkipMovement() {
        if (this.character instanceof IsoZombie) {
            return this.character.isCurrentState(ZombieFallDownState.instance()) || this.character.isCurrentState(ZombieOnGroundState.instance());
        }
        return this.character instanceof IsoPlayer && (this.character.isCurrentState(PlayerFallDownState.instance()) || this.character.isCurrentState(PlayerKnockedDown.instance()) || this.character.isCurrentState(PlayerOnGroundState.instance()));
    }
    
    private String getStartDescription() {
        return String.format("start=[ pos=( %f ; %f ) dir=( %f ; %f ) angle=%f ]", this.startPosition.x, this.startPosition.y, this.startDirection.x, this.startDirection.y, this.startAngle);
    }
    
    private String getFinalDescription() {
        return String.format("final=[ pos=( %f ; %f ) dir=( %f ; %f ) angle=%f ]", this.finalPosition.x, this.finalPosition.y, this.finalDirection.x, this.finalDirection.y, this.finalAngle);
    }
    
    private String getActualDescription() {
        return String.format("actual=[ pos=( %f ; %f ) dir=( %f ; %f ) angle=%f ]", this.character.x, this.character.y, this.character.getForwardDirection().getX(), this.character.getForwardDirection().getY(), this.character.getAnimAngleRadians());
    }
    
    public String getDescription() {
        return String.format("start=%d | (x=%f,y=%f;a=%f;l=%f)", this.startTime, this.finalPosition.x, this.finalPosition.y, this.finalAngle, IsoUtils.DistanceTo(this.startPosition.x, this.startPosition.y, this.finalPosition.x, this.finalPosition.y));
    }
    
    public static void CalcHitReactionWeapon(final IsoGameCharacter isoGameCharacter, final IsoGameCharacter isoGameCharacter2, final HandWeapon handWeapon) {
        final HitReactionNetworkAI hitReactionNetworkAI = isoGameCharacter2.getHitReactionNetworkAI();
        if (isoGameCharacter2.isOnFloor()) {
            hitReactionNetworkAI.setup(isoGameCharacter2.x, isoGameCharacter2.y, (byte)isoGameCharacter2.z, isoGameCharacter2.getAnimAngleRadians());
        }
        else {
            final Vector2 vector2 = new Vector2();
            final Float calcHitDir = isoGameCharacter2.calcHitDir(isoGameCharacter, handWeapon, vector2);
            if (isoGameCharacter2 instanceof IsoPlayer) {
                vector2.x = (vector2.x + isoGameCharacter2.x + ((IsoPlayer)isoGameCharacter2).networkAI.targetX) * 0.5f;
                vector2.y = (vector2.y + isoGameCharacter2.y + ((IsoPlayer)isoGameCharacter2).networkAI.targetY) * 0.5f;
            }
            else {
                vector2.x += isoGameCharacter2.x;
                vector2.y += isoGameCharacter2.y;
            }
            vector2.x = PZMath.roundFromEdges(vector2.x);
            vector2.y = PZMath.roundFromEdges(vector2.y);
            if (PolygonalMap2.instance.lineClearCollide(isoGameCharacter2.x, isoGameCharacter2.y, vector2.x, vector2.y, (int)isoGameCharacter2.z, null, false, true)) {
                vector2.x = isoGameCharacter2.x;
                vector2.y = isoGameCharacter2.y;
            }
            hitReactionNetworkAI.setup(vector2.x, vector2.y, (byte)isoGameCharacter2.z, calcHitDir);
        }
        if (hitReactionNetworkAI.isSetup()) {
            hitReactionNetworkAI.start();
            if (Core.bDebug) {
                DebugLog.log(DebugType.Multiplayer, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, hitReactionNetworkAI.getDescription()));
            }
        }
    }
    
    public static void CalcHitReactionVehicle(final IsoGameCharacter isoGameCharacter, final BaseVehicle baseVehicle) {
        final HitReactionNetworkAI hitReactionNetworkAI = isoGameCharacter.getHitReactionNetworkAI();
        if (!hitReactionNetworkAI.isStarted()) {
            if (isoGameCharacter.isOnFloor()) {
                hitReactionNetworkAI.setup(isoGameCharacter.x, isoGameCharacter.y, (byte)isoGameCharacter.z, isoGameCharacter.getAnimAngleRadians());
            }
            else {
                final Vector2 vector2 = new Vector2();
                isoGameCharacter.calcHitDir(vector2);
                if (isoGameCharacter instanceof IsoPlayer) {
                    vector2.x = (vector2.x + isoGameCharacter.x + ((IsoPlayer)isoGameCharacter).networkAI.targetX) * 0.5f;
                    vector2.y = (vector2.y + isoGameCharacter.y + ((IsoPlayer)isoGameCharacter).networkAI.targetY) * 0.5f;
                }
                else {
                    vector2.x += isoGameCharacter.x;
                    vector2.y += isoGameCharacter.y;
                }
                vector2.x = PZMath.roundFromEdges(vector2.x);
                vector2.y = PZMath.roundFromEdges(vector2.y);
                if (PolygonalMap2.instance.lineClearCollide(isoGameCharacter.x, isoGameCharacter.y, vector2.x, vector2.y, (int)isoGameCharacter.z, baseVehicle, false, true)) {
                    vector2.x = isoGameCharacter.x;
                    vector2.y = isoGameCharacter.y;
                }
                hitReactionNetworkAI.setup(vector2.x, vector2.y, (byte)isoGameCharacter.z, null);
            }
            if (Core.bDebug) {
                DebugLog.log(DebugType.Multiplayer, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, hitReactionNetworkAI.getDescription()));
            }
        }
        if (hitReactionNetworkAI.isSetup()) {
            hitReactionNetworkAI.start();
            if (Core.bDebug) {
                DebugLog.log(DebugType.Multiplayer, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, hitReactionNetworkAI.getDescription()));
            }
        }
    }
    
    public void process(final float n, final float n2, final float n3, final float f) {
        this.setup(n, n2, (byte)n3, f);
        if (Core.bDebug) {
            DebugLog.log(DebugType.Damage, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getDescription()));
        }
        this.start();
        if (Core.bDebug) {
            DebugLog.log(DebugType.Damage, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getDescription()));
        }
        if (GameServer.bServer) {
            this.moveFinal();
            this.finish();
            if (Core.bDebug) {
                DebugLog.log(DebugType.Damage, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getDescription()));
            }
        }
    }
}

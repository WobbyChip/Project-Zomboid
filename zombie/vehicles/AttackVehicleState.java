// 
// Decompiled by Procyon v0.5.36
// 

package zombie.vehicles;

import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.iso.IsoObject;
import fmod.fmod.FMODSoundEmitter;
import zombie.core.Rand;
import zombie.core.Core;
import zombie.network.GameServer;
import zombie.GameTime;
import zombie.iso.IsoMovingObject;
import zombie.ai.states.ZombieIdleState;
import zombie.characters.IsoZombie;
import zombie.characters.IsoGameCharacter;
import org.joml.Vector3f;
import zombie.audio.BaseSoundEmitter;
import zombie.ai.State;

public final class AttackVehicleState extends State
{
    private static final AttackVehicleState _instance;
    private BaseSoundEmitter emitter;
    private final Vector3f worldPos;
    
    public AttackVehicleState() {
        this.worldPos = new Vector3f();
    }
    
    public static AttackVehicleState instance() {
        return AttackVehicleState._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
        final IsoZombie isoZombie = (IsoZombie)isoGameCharacter;
        if (!(isoZombie.target instanceof IsoGameCharacter)) {
            return;
        }
        final IsoGameCharacter isoGameCharacter2 = (IsoGameCharacter)isoZombie.target;
        if (isoGameCharacter2.isDead()) {
            if (isoGameCharacter2.getLeaveBodyTimedown() > 3600.0f) {
                isoZombie.changeState(ZombieIdleState.instance());
                isoZombie.setTarget(null);
            }
            else {
                isoGameCharacter2.setLeaveBodyTimedown(isoGameCharacter2.getLeaveBodyTimedown() + GameTime.getInstance().getMultiplier() / 1.6f);
                if (!GameServer.bServer && !Core.SoundDisabled && Rand.Next(Rand.AdjustForFramerate(15)) == 0) {
                    if (this.emitter == null) {
                        this.emitter = (BaseSoundEmitter)new FMODSoundEmitter();
                    }
                    final String s = isoZombie.isFemale() ? "FemaleZombieEating" : "MaleZombieEating";
                    if (!this.emitter.isPlaying(s)) {
                        this.emitter.playSound(s);
                    }
                }
            }
            isoZombie.TimeSinceSeenFlesh = 0.0f;
            return;
        }
        final BaseVehicle vehicle = isoGameCharacter2.getVehicle();
        if (vehicle == null || !vehicle.isCharacterAdjacentTo(isoGameCharacter)) {
            return;
        }
        final Vector3f chooseBestAttackPosition = vehicle.chooseBestAttackPosition(isoGameCharacter2, isoGameCharacter, this.worldPos);
        if (chooseBestAttackPosition == null) {
            if (isoZombie.AllowRepathDelay <= 0.0f) {
                isoGameCharacter.pathToCharacter(isoGameCharacter2);
                isoZombie.AllowRepathDelay = 6.25f;
            }
            return;
        }
        if (chooseBestAttackPosition == null || (Math.abs(chooseBestAttackPosition.x - isoGameCharacter.x) <= 0.1f && Math.abs(chooseBestAttackPosition.y - isoGameCharacter.y) <= 0.1f)) {
            isoGameCharacter.faceThisObject(isoGameCharacter2);
            return;
        }
        if (Math.abs(vehicle.getCurrentSpeedKmHour()) > 0.1f && (vehicle.isCharacterAdjacentTo(isoGameCharacter) || vehicle.DistToSquared(isoGameCharacter) < 16.0f)) {
            return;
        }
        if (isoZombie.AllowRepathDelay <= 0.0f) {
            isoGameCharacter.pathToCharacter(isoGameCharacter2);
            isoZombie.AllowRepathDelay = 6.25f;
        }
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
    }
    
    @Override
    public void animEvent(final IsoGameCharacter isoGameCharacter, final AnimEvent animEvent) {
        final IsoZombie isoZombie = (IsoZombie)isoGameCharacter;
        if (!(isoZombie.target instanceof IsoGameCharacter)) {
            return;
        }
        final IsoGameCharacter isoGameCharacter2 = (IsoGameCharacter)isoZombie.target;
        final BaseVehicle vehicle = isoGameCharacter2.getVehicle();
        if (vehicle == null) {
            return;
        }
        if (isoGameCharacter2.isDead()) {
            return;
        }
        if (animEvent.m_EventName.equalsIgnoreCase("AttackCollisionCheck")) {
            isoGameCharacter2.getBodyDamage().AddRandomDamageFromZombie(isoZombie, null);
            isoGameCharacter2.getBodyDamage().Update();
            if (isoGameCharacter2.isDead()) {
                if (isoGameCharacter2.isFemale()) {
                    isoZombie.getEmitter().playVocals("FemaleBeingEatenDeath");
                }
                else {
                    isoZombie.getEmitter().playVocals("MaleBeingEatenDeath");
                }
                isoGameCharacter2.setHealth(0.0f);
            }
            else if (isoGameCharacter2.isAsleep()) {
                if (GameServer.bServer) {
                    isoGameCharacter2.sendObjectChange("wakeUp");
                    isoGameCharacter2.setAsleep(false);
                }
                else {
                    isoGameCharacter2.forceAwake();
                }
            }
        }
        else if (animEvent.m_EventName.equalsIgnoreCase("ThumpFrame")) {
            VehicleWindow vehicleWindow = null;
            VehiclePart nearestBodyworkPart = null;
            final int seat = vehicle.getSeat(isoGameCharacter2);
            if (vehicle.isInArea(vehicle.getPassengerArea(seat), isoGameCharacter)) {
                final VehiclePart passengerDoor = vehicle.getPassengerDoor(seat);
                if (passengerDoor != null) {
                    if (passengerDoor.getDoor() != null) {
                        if (passengerDoor.getInventoryItem() != null) {
                            if (!passengerDoor.getDoor().isOpen()) {
                                vehicleWindow = passengerDoor.findWindow();
                                if (vehicleWindow != null && !vehicleWindow.isHittable()) {
                                    vehicleWindow = null;
                                }
                                if (vehicleWindow == null) {
                                    nearestBodyworkPart = passengerDoor;
                                }
                            }
                        }
                    }
                }
            }
            else {
                nearestBodyworkPart = vehicle.getNearestBodyworkPart(isoGameCharacter);
                if (nearestBodyworkPart != null) {
                    vehicleWindow = nearestBodyworkPart.getWindow();
                    if (vehicleWindow == null) {
                        vehicleWindow = nearestBodyworkPart.findWindow();
                    }
                    if (vehicleWindow != null && !vehicleWindow.isHittable()) {
                        vehicleWindow = null;
                    }
                    if (vehicleWindow != null) {
                        nearestBodyworkPart = null;
                    }
                }
            }
            if (vehicleWindow != null) {
                vehicleWindow.damage(isoZombie.strength);
                vehicle.setBloodIntensity(vehicleWindow.part.getId(), vehicle.getBloodIntensity(vehicleWindow.part.getId()) + 0.025f);
                if (!GameServer.bServer) {
                    isoZombie.setVehicleHitLocation(vehicle);
                    isoGameCharacter.getEmitter().playSound("ZombieThumpVehicleWindow", vehicle);
                }
                isoZombie.setThumpFlag(3);
            }
            else {
                if (!GameServer.bServer) {
                    isoZombie.setVehicleHitLocation(vehicle);
                    isoGameCharacter.getEmitter().playSound("ZombieThumpVehicle", vehicle);
                }
                isoZombie.setThumpFlag(1);
            }
            vehicle.setAddThumpWorldSound(true);
            if (nearestBodyworkPart != null && nearestBodyworkPart.getWindow() == null) {
                nearestBodyworkPart.setCondition(nearestBodyworkPart.getCondition() - isoZombie.strength);
            }
            if (isoGameCharacter2.isAsleep()) {
                if (GameServer.bServer) {
                    isoGameCharacter2.sendObjectChange("wakeUp");
                    isoGameCharacter2.setAsleep(false);
                }
                else {
                    isoGameCharacter2.forceAwake();
                }
            }
        }
    }
    
    @Override
    public boolean isAttacking(final IsoGameCharacter isoGameCharacter) {
        return true;
    }
    
    public boolean isPassengerExposed(final IsoGameCharacter isoGameCharacter) {
        if (!(isoGameCharacter instanceof IsoZombie)) {
            return false;
        }
        final IsoZombie isoZombie = (IsoZombie)isoGameCharacter;
        if (!(isoZombie.target instanceof IsoGameCharacter)) {
            return false;
        }
        final IsoGameCharacter isoGameCharacter2 = (IsoGameCharacter)isoZombie.target;
        final BaseVehicle vehicle = isoGameCharacter2.getVehicle();
        if (vehicle == null) {
            return false;
        }
        boolean b = false;
        final int seat = vehicle.getSeat(isoGameCharacter2);
        if (vehicle.isInArea(vehicle.getPassengerArea(seat), isoGameCharacter)) {
            final VehiclePart passengerDoor = vehicle.getPassengerDoor(seat);
            if (passengerDoor != null) {
                if (passengerDoor.getDoor() != null) {
                    if (passengerDoor.getInventoryItem() == null || passengerDoor.getDoor().isOpen()) {
                        b = true;
                    }
                    else {
                        VehicleWindow window = passengerDoor.findWindow();
                        if (window != null) {
                            if (!window.isHittable()) {
                                window = null;
                            }
                            b = (window == null);
                        }
                        else {
                            b = false;
                        }
                    }
                }
            }
        }
        else {
            final VehiclePart nearestBodyworkPart = vehicle.getNearestBodyworkPart(isoGameCharacter);
            if (nearestBodyworkPart != null) {
                final VehicleWindow window2 = nearestBodyworkPart.findWindow();
                if (window2 != null && !window2.isHittable()) {}
            }
        }
        return b;
    }
    
    static {
        _instance = new AttackVehicleState();
    }
}

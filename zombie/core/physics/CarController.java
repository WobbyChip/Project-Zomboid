// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.physics;

import zombie.vehicles.PolygonalMap2;
import zombie.debug.LineDrawer;
import zombie.debug.DebugOptions;
import zombie.input.JoypadManager;
import zombie.input.GameKeyboard;
import zombie.core.Core;
import zombie.iso.IsoObject;
import zombie.vehicles.EngineRPMData;
import zombie.characters.IsoGameCharacter;
import zombie.core.Rand;
import zombie.vehicles.TransmissionNumber;
import zombie.vehicles.VehicleManager;
import zombie.characters.IsoPlayer;
import zombie.network.GameServer;
import zombie.core.math.PZMath;
import zombie.ui.UIManager;
import zombie.GameTime;
import zombie.network.ServerOptions;
import zombie.network.GameClient;
import org.joml.Vector3fc;
import zombie.characters.Moodles.MoodleType;
import zombie.scripting.objects.VehicleScript;
import zombie.debug.DebugLog;
import org.joml.Vector3f;
import zombie.iso.Vector2;
import zombie.core.utils.OnceEvery;
import zombie.vehicles.BaseVehicle;

public final class CarController
{
    public final BaseVehicle vehicleObject;
    public float clientForce;
    public float EngineForce;
    public float BrakingForce;
    private float VehicleSteering;
    boolean isGas;
    boolean isGasR;
    boolean isBreak;
    private float atRestTimer;
    private float regulatorTimer;
    private final OnceEvery sendEvery;
    private double sentEngineSpeed;
    public boolean isEnable;
    private final Transform tempXfrm;
    private final Vector2 tempVec2;
    private final Vector3f tempVec3f;
    private final Vector3f tempVec3f_2;
    private final Vector3f tempVec3f_3;
    private static final Vector3f _UNIT_Y;
    public boolean acceleratorOn;
    public boolean brakeOn;
    public float speed;
    public static GearInfo[] gears;
    public final ClientControls clientControls;
    private boolean engineStartingFromKeyboard;
    private static final BulletVariables bulletVariables;
    float drunkDelayCommandTimer;
    boolean wasBreaking;
    boolean wasGas;
    boolean wasGasR;
    boolean wasSteering;
    
    public CarController(final BaseVehicle vehicleObject) {
        this.clientForce = 0.0f;
        this.EngineForce = 0.0f;
        this.BrakingForce = 0.0f;
        this.VehicleSteering = 0.0f;
        this.isGas = false;
        this.isGasR = false;
        this.isBreak = false;
        this.atRestTimer = -1.0f;
        this.regulatorTimer = 0.0f;
        this.sendEvery = new OnceEvery(0.1f);
        this.sentEngineSpeed = -1.0;
        this.isEnable = false;
        this.tempXfrm = new Transform();
        this.tempVec2 = new Vector2();
        this.tempVec3f = new Vector3f();
        this.tempVec3f_2 = new Vector3f();
        this.tempVec3f_3 = new Vector3f();
        this.acceleratorOn = false;
        this.brakeOn = false;
        this.speed = 0.0f;
        this.clientControls = new ClientControls();
        this.drunkDelayCommandTimer = 0.0f;
        this.wasBreaking = false;
        this.wasGas = false;
        this.wasGasR = false;
        this.wasSteering = false;
        this.vehicleObject = vehicleObject;
        this.engineStartingFromKeyboard = false;
        final VehicleScript script = vehicleObject.getScript();
        float savedPhysicsZ = vehicleObject.savedPhysicsZ;
        if (Float.isNaN(savedPhysicsZ)) {
            float max = Math.max((float)(int)vehicleObject.z, 0.0f);
            if (script.getWheelCount() > 0) {
                max = max + script.getModelOffset().y() + (script.getWheel(0).getOffset().y() - script.getWheel(0).radius);
            }
            savedPhysicsZ = 0.0f - Math.min(max, script.getCenterOfMassOffset().y() - script.getExtents().y() / 2.0f);
            vehicleObject.jniTransform.origin.y = savedPhysicsZ;
        }
        Bullet.addVehicle(vehicleObject.VehicleID, vehicleObject.x, vehicleObject.y, savedPhysicsZ, vehicleObject.savedRot.x, vehicleObject.savedRot.y, vehicleObject.savedRot.z, vehicleObject.savedRot.w, script.getFullName());
        Bullet.setVehicleStatic(vehicleObject.VehicleID, vehicleObject.netPlayerAuthorization == 4);
        DebugLog.Vehicle.debugln(invokedynamic(makeConcatWithConstants:(SLjava/lang/String;FFFBLjava/lang/String;)Ljava/lang/String;, vehicleObject.VehicleID, script.getFullName(), vehicleObject.x, vehicleObject.y, savedPhysicsZ, vehicleObject.netPlayerAuthorization, (vehicleObject.netPlayerAuthorization == 4) ? "static" : "dynamic"));
    }
    
    public GearInfo findGear(final float n) {
        for (int i = 0; i < CarController.gears.length; ++i) {
            if (n >= CarController.gears[i].minSpeed && n < CarController.gears[i].maxSpeed) {
                return CarController.gears[i];
            }
        }
        return null;
    }
    
    public void accelerator(final boolean acceleratorOn) {
        this.acceleratorOn = acceleratorOn;
    }
    
    public void brake(final boolean brakeOn) {
        this.brakeOn = brakeOn;
    }
    
    public ClientControls getClientControls() {
        return this.clientControls;
    }
    
    public void update() {
        if (this.vehicleObject.getVehicleTowedBy() != null) {
            return;
        }
        final VehicleScript script = this.vehicleObject.getScript();
        this.speed = this.vehicleObject.getCurrentSpeedKmHour();
        final boolean b = this.vehicleObject.getDriver() != null && this.vehicleObject.getDriver().getMoodles().getMoodleLevel(MoodleType.Drunk) > 1;
        float dot = 0.0f;
        final Vector3f linearVelocity = this.vehicleObject.getLinearVelocity(this.tempVec3f_2);
        linearVelocity.y = 0.0f;
        if (linearVelocity.length() > 0.5) {
            linearVelocity.normalize();
            final Vector3f tempVec3f = this.tempVec3f;
            this.vehicleObject.getForwardVector(tempVec3f);
            dot = linearVelocity.dot((Vector3fc)tempVec3f);
        }
        float lerp = 1.0f;
        if (GameClient.bClient) {
            final float n = this.vehicleObject.jniSpeed / Math.min(120.0f, (float)ServerOptions.instance.SpeedLimit.getValue());
            lerp = GameTime.getInstance().Lerp(1.0f, BaseVehicle.getFakeSpeedModifier(), n * n);
        }
        final float n2 = this.vehicleObject.getCurrentSpeedKmHour() * lerp;
        this.isGas = false;
        this.isGasR = false;
        this.isBreak = false;
        if (this.clientControls.forward) {
            if (dot < 0.0f) {
                this.isBreak = true;
            }
            if (dot >= 0.0f) {
                this.isGas = true;
            }
            this.isGasR = false;
        }
        if (this.clientControls.backward) {
            if (dot > 0.0f) {
                this.isBreak = true;
            }
            if (dot <= 0.0f) {
                this.isGasR = true;
            }
            this.isGas = false;
        }
        if (this.clientControls.brake) {
            this.isBreak = true;
            this.isGas = false;
            this.isGasR = false;
        }
        if (b && this.vehicleObject.engineState != BaseVehicle.engineStateTypes.Idle) {
            if (this.isBreak && !this.wasBreaking) {
                this.isBreak = this.delayCommandWhileDrunk(this.isBreak);
            }
            if (this.isGas && !this.wasGas) {
                this.isGas = this.delayCommandWhileDrunk(this.isGas);
            }
            if (this.isGasR && !this.wasGasR) {
                this.isGasR = this.delayCommandWhileDrunk(this.isGas);
            }
            if (this.clientControls.steering != 0.0f && !this.wasSteering) {
                this.clientControls.steering = this.delayCommandWhileDrunk(this.clientControls.steering);
            }
        }
        this.updateRegulator();
        this.wasBreaking = this.isBreak;
        this.wasGas = this.isGas;
        this.wasGasR = this.isGasR;
        this.wasSteering = (this.clientControls.steering != 0.0f);
        if (!this.isGasR && this.vehicleObject.isInvalidChunkAhead()) {
            this.isBreak = true;
            this.isGas = false;
            this.isGasR = false;
        }
        else if (!this.isGas && this.vehicleObject.isInvalidChunkBehind()) {
            this.isBreak = true;
            this.isGas = false;
            this.isGasR = false;
        }
        if (this.clientControls.shift) {
            this.isGas = false;
            this.isBreak = false;
            this.isGasR = false;
            this.clientControls.wasUsingParkingBrakes = false;
        }
        final float throttle = this.vehicleObject.throttle;
        float throttle2;
        if (this.isGas || this.isGasR) {
            throttle2 = throttle + GameTime.getInstance().getMultiplier() / 30.0f;
        }
        else {
            throttle2 = throttle - GameTime.getInstance().getMultiplier() / 30.0f;
        }
        if (throttle2 < 0.0f) {
            throttle2 = 0.0f;
        }
        if (throttle2 > 1.0f) {
            throttle2 = 1.0f;
        }
        if (this.vehicleObject.isRegulator() && !this.isGas && !this.isGasR) {
            throttle2 = 0.5f;
            if (n2 < this.vehicleObject.getRegulatorSpeed()) {
                this.isGas = true;
            }
        }
        this.vehicleObject.throttle = throttle2;
        final float n3 = GameTime.getInstance().getMultiplier() / 0.8f;
        ControlState controlState = ControlState.NoControl;
        if (this.isBreak) {
            controlState = ControlState.Braking;
        }
        else if (this.isGas && !this.isGasR) {
            controlState = ControlState.Forward;
        }
        else if (!this.isGas && this.isGasR) {
            controlState = ControlState.Reverse;
        }
        if (controlState != ControlState.NoControl) {
            UIManager.speedControls.SetCurrentGameSpeed(1);
        }
        if (controlState == ControlState.NoControl) {
            this.control_NoControl();
        }
        if (controlState == ControlState.Reverse) {
            this.control_Reverse(n2);
        }
        if (controlState == ControlState.Forward) {
            this.control_ForwardNew(n2);
        }
        this.updateBackSignal();
        if (controlState == ControlState.Braking) {
            this.control_Braking();
        }
        this.updateBrakeLights();
        final BaseVehicle vehicleTowedBy = this.vehicleObject.getVehicleTowedBy();
        if (vehicleTowedBy != null && vehicleTowedBy.getDriver() == null && this.vehicleObject.getDriver() != null) {
            this.vehicleObject.addPointConstraint(vehicleTowedBy, this.vehicleObject.getTowAttachmentSelf(), vehicleTowedBy.getTowAttachmentSelf());
        }
        if (this.vehicleObject.getVehicleTowing() != null) {
            this.vehicleObject.updateConstraint(this.vehicleObject.getVehicleTowing());
        }
        this.updateRammingSound(n2);
        if (Math.abs(this.clientControls.steering) > 0.1f) {
            float n4 = 1.0f - this.speed / this.vehicleObject.getMaxSpeed();
            if (n4 < 0.1f) {
                n4 = 0.1f;
            }
            this.VehicleSteering -= (this.clientControls.steering + this.VehicleSteering) * 0.06f * n3 * n4;
        }
        else if (Math.abs(this.VehicleSteering) <= 0.04) {
            this.VehicleSteering = 0.0f;
        }
        else if (this.VehicleSteering > 0.0f) {
            this.VehicleSteering -= 0.04f * n3;
            this.VehicleSteering = Math.max(this.VehicleSteering, 0.0f);
        }
        else {
            this.VehicleSteering += 0.04f * n3;
            this.VehicleSteering = Math.min(this.VehicleSteering, 0.0f);
        }
        final float steeringClamp = script.getSteeringClamp(this.speed);
        this.VehicleSteering = PZMath.clamp(this.VehicleSteering, -steeringClamp, steeringClamp);
        final BulletVariables set = CarController.bulletVariables.set(this.vehicleObject, this.EngineForce, this.BrakingForce, this.VehicleSteering);
        this.checkTire(set);
        this.EngineForce = set.engineForce;
        this.BrakingForce = set.brakingForce;
        this.VehicleSteering = set.vehicleSteering;
        if (this.vehicleObject.isDoingOffroad()) {
            int transmissionNumber = this.vehicleObject.getTransmissionNumber();
            if (transmissionNumber <= 0) {
                transmissionNumber = 1;
            }
            this.EngineForce /= (float)(transmissionNumber * 1.5);
        }
        this.vehicleObject.setCurrentSteering(this.VehicleSteering);
        this.vehicleObject.setBraking(this.isBreak);
        if (!GameServer.bServer) {
            this.checkShouldBeActive();
            Bullet.controlVehicle(this.vehicleObject.VehicleID, this.EngineForce, this.BrakingForce, this.VehicleSteering);
            if (this.EngineForce > 0.0f && this.vehicleObject.engineState == BaseVehicle.engineStateTypes.Idle && !this.engineStartingFromKeyboard) {
                this.engineStartingFromKeyboard = true;
                if (GameClient.bClient) {
                    GameClient.instance.sendClientCommandV((IsoPlayer)this.vehicleObject.getDriver(), "vehicle", "startEngine", "haveKey", (this.vehicleObject.getDriver().getInventory().haveThisKeyId(this.vehicleObject.getKeyId()) != null) ? Boolean.TRUE : Boolean.FALSE);
                }
                else {
                    this.vehicleObject.tryStartEngine();
                }
            }
            if (this.engineStartingFromKeyboard && this.EngineForce == 0.0f) {
                this.engineStartingFromKeyboard = false;
            }
        }
        if (this.vehicleObject.engineState != BaseVehicle.engineStateTypes.Running) {
            this.acceleratorOn = false;
            if (this.vehicleObject.jniSpeed > 5.0f && this.vehicleObject.getScript().getWheelCount() > 0) {
                Bullet.controlVehicle(this.vehicleObject.VehicleID, 0.0f, this.BrakingForce, this.VehicleSteering);
            }
            else {
                this.park();
            }
        }
        if (GameClient.bClient) {
            double sentEngineSpeed = this.vehicleObject.isEngineRunning() ? this.vehicleObject.engineSpeed : 0.0;
            if (!this.isGas && !this.isBreak && !this.isGasR && sentEngineSpeed >= 950.0 && sentEngineSpeed <= 1050.0) {
                sentEngineSpeed = 1000.0;
            }
            if (this.sendEvery.Check() && (this.sentEngineSpeed == -1.0 || Math.abs(this.sentEngineSpeed - sentEngineSpeed) > 10.0 || this.sentEngineSpeed != 0.0 != (sentEngineSpeed != 0.0))) {
                VehicleManager.instance.sendEngineSound(this.vehicleObject, (float)sentEngineSpeed, this.vehicleObject.throttle);
                this.sentEngineSpeed = sentEngineSpeed;
            }
        }
    }
    
    public void updateTrailer() {
        if (GameServer.bServer) {
            return;
        }
        final BaseVehicle vehicleTowedBy = this.vehicleObject.getVehicleTowedBy();
        if (vehicleTowedBy == null) {
            return;
        }
        this.speed = this.vehicleObject.getCurrentSpeedKmHour();
        this.isGas = false;
        this.isGasR = false;
        this.isBreak = false;
        this.wasGas = false;
        this.wasGasR = false;
        this.wasBreaking = false;
        this.vehicleObject.throttle = 0.0f;
        if (vehicleTowedBy.getDriver() == null && this.vehicleObject.getDriver() != null && !GameClient.bClient) {
            this.vehicleObject.addPointConstraint(vehicleTowedBy, this.vehicleObject.getTowAttachmentSelf(), vehicleTowedBy.getTowAttachmentSelf());
            return;
        }
        this.checkShouldBeActive();
        this.EngineForce = 0.0f;
        this.BrakingForce = 0.0f;
        this.VehicleSteering = 0.0f;
        if (!this.vehicleObject.getScriptName().contains("Trailer")) {
            this.BrakingForce = 10.0f;
        }
        Bullet.controlVehicle(this.vehicleObject.VehicleID, this.EngineForce, this.BrakingForce, this.VehicleSteering);
    }
    
    private void updateRegulator() {
        if (this.regulatorTimer > 0.0f) {
            this.regulatorTimer -= GameTime.getInstance().getMultiplier() / 1.6f;
        }
        if (this.clientControls.shift) {
            if (this.clientControls.forward && this.regulatorTimer <= 0.0f) {
                if (this.vehicleObject.getRegulatorSpeed() < this.vehicleObject.getMaxSpeed() + 20.0f && ((!this.vehicleObject.isRegulator() && this.vehicleObject.getRegulatorSpeed() == 0.0f) || this.vehicleObject.isRegulator())) {
                    this.vehicleObject.setRegulatorSpeed(this.vehicleObject.getRegulatorSpeed() + 5.0f);
                }
                this.vehicleObject.setRegulator(true);
                this.regulatorTimer = 20.0f;
            }
            else if (this.clientControls.backward && this.regulatorTimer <= 0.0f) {
                this.regulatorTimer = 20.0f;
                if (this.vehicleObject.getRegulatorSpeed() >= 5.0f && ((!this.vehicleObject.isRegulator() && this.vehicleObject.getRegulatorSpeed() == 0.0f) || this.vehicleObject.isRegulator())) {
                    this.vehicleObject.setRegulatorSpeed(this.vehicleObject.getRegulatorSpeed() - 5.0f);
                }
                this.vehicleObject.setRegulator(true);
                if (this.vehicleObject.getRegulatorSpeed() <= 0.0f) {
                    this.vehicleObject.setRegulator(false);
                }
            }
        }
        else if (this.isGasR || this.isBreak) {
            this.vehicleObject.setRegulator(false);
        }
    }
    
    private void control_NoControl() {
        final float n = GameTime.getInstance().getMultiplier() / 0.8f;
        if (!this.vehicleObject.isEngineRunning()) {
            if (this.vehicleObject.engineSpeed > 0.0) {
                this.vehicleObject.engineSpeed = Math.max(this.vehicleObject.engineSpeed - 50.0f * n, 0.0);
            }
        }
        else if (this.vehicleObject.engineSpeed > this.vehicleObject.getScript().getEngineIdleSpeed()) {
            if (!this.vehicleObject.isRegulator()) {
                final BaseVehicle vehicleObject = this.vehicleObject;
                vehicleObject.engineSpeed -= 20.0f * n;
            }
        }
        else {
            final BaseVehicle vehicleObject2 = this.vehicleObject;
            vehicleObject2.engineSpeed += 20.0f * n;
        }
        if (!this.vehicleObject.isRegulator()) {
            this.vehicleObject.transmissionNumber = TransmissionNumber.N;
        }
        this.EngineForce = 0.0f;
        if (this.vehicleObject.engineSpeed > 1000.0) {
            this.BrakingForce = 15.0f;
        }
        else {
            this.BrakingForce = 10.0f;
        }
    }
    
    private void control_Braking() {
        final float n = GameTime.getInstance().getMultiplier() / 0.8f;
        if (this.vehicleObject.engineSpeed > this.vehicleObject.getScript().getEngineIdleSpeed()) {
            final BaseVehicle vehicleObject = this.vehicleObject;
            vehicleObject.engineSpeed -= Rand.Next(10, 30) * n;
        }
        else {
            final BaseVehicle vehicleObject2 = this.vehicleObject;
            vehicleObject2.engineSpeed += Rand.Next(20) * n;
        }
        this.vehicleObject.transmissionNumber = TransmissionNumber.N;
        this.EngineForce = 0.0f;
        this.BrakingForce = this.vehicleObject.getBrakingForce();
        if (this.clientControls.brake) {
            this.BrakingForce *= 13.0f;
        }
    }
    
    private void control_Forward(final float n) {
        final float n2 = GameTime.getInstance().getMultiplier() / 0.8f;
        final IsoGameCharacter driver = this.vehicleObject.getDriver();
        final boolean b = driver != null && driver.Traits.SpeedDemon.isSet();
        final boolean b2 = driver != null && driver.Traits.SundayDriver.isSet();
        final int gearRatioCount = this.vehicleObject.getScript().gearRatioCount;
        float n3 = 0.0f;
        if (this.vehicleObject.transmissionNumber == TransmissionNumber.N) {
            this.vehicleObject.transmissionNumber = TransmissionNumber.Speed1;
            int n4 = 0;
            while (true) {
                if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed1) {
                    n3 = 3000.0f * n / 30.0f;
                }
                if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed2) {
                    n3 = 3000.0f * n / 40.0f;
                }
                if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed3) {
                    n3 = 3000.0f * n / 60.0f;
                }
                if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed4) {
                    n3 = 3000.0f * n / 85.0f;
                }
                if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed5) {
                    n3 = 3000.0f * n / 105.0f;
                }
                if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed6) {
                    n3 = 3000.0f * n / 130.0f;
                }
                if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed7) {
                    n3 = 3000.0f * n / 160.0f;
                }
                if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed8) {
                    n3 = 3000.0f * n / 200.0f;
                }
                if (b) {
                    if (n3 > 6000.0f) {
                        this.vehicleObject.changeTransmission(this.vehicleObject.transmissionNumber.getNext(gearRatioCount));
                        n4 = 1;
                    }
                }
                else if (n3 > 3000.0f) {
                    this.vehicleObject.changeTransmission(this.vehicleObject.transmissionNumber.getNext(gearRatioCount));
                    n4 = 1;
                }
                if (n4 == 0) {
                    break;
                }
                if (this.vehicleObject.transmissionNumber.getIndex() >= gearRatioCount) {
                    break;
                }
                n4 = 0;
            }
        }
        if (b) {
            if (this.vehicleObject.engineSpeed > 6000.0 && this.vehicleObject.transmissionChangeTime.Check()) {
                this.vehicleObject.changeTransmission(this.vehicleObject.transmissionNumber.getNext(gearRatioCount));
            }
        }
        else if (this.vehicleObject.engineSpeed > 3000.0 && this.vehicleObject.transmissionChangeTime.Check()) {
            this.vehicleObject.changeTransmission(this.vehicleObject.transmissionNumber.getNext(gearRatioCount));
        }
        if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed1) {
            n3 = 3000.0f * n / 30.0f;
        }
        if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed2) {
            n3 = 3000.0f * n / 40.0f;
        }
        if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed3) {
            n3 = 3000.0f * n / 60.0f;
        }
        if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed4) {
            n3 = 3000.0f * n / 85.0f;
        }
        if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed5) {
            n3 = 3000.0f * n / 105.0f;
        }
        if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed6) {
            n3 = 3000.0f * n / 130.0f;
        }
        if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed7) {
            n3 = 3000.0f * n / 160.0f;
        }
        if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed8) {
            n3 = 3000.0f * n / 200.0f;
        }
        final BaseVehicle vehicleObject = this.vehicleObject;
        vehicleObject.engineSpeed -= Math.min(0.5 * (this.vehicleObject.engineSpeed - n3), 100.0) * n2;
        if (b) {
            if (n < 50.0f) {
                final BaseVehicle vehicleObject2 = this.vehicleObject;
                vehicleObject2.engineSpeed -= Math.min(0.06 * (this.vehicleObject.engineSpeed - 7000.0), 30.0f - n) * n2;
            }
        }
        else if (n < 30.0f) {
            final BaseVehicle vehicleObject3 = this.vehicleObject;
            vehicleObject3.engineSpeed -= Math.min(0.02 * (this.vehicleObject.engineSpeed - 7000.0), 30.0f - n) * n2;
        }
        this.EngineForce = (float)(this.vehicleObject.getEnginePower() * (0.5 + this.vehicleObject.engineSpeed / 24000.0));
        this.EngineForce -= this.EngineForce * (n / 200.0f);
        boolean b3 = false;
        if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed1 && this.vehicleObject.getVehicleTowedBy() != null) {
            if (this.vehicleObject.getVehicleTowedBy().getScript().getPassengerCount() == 0 && this.vehicleObject.getVehicleTowedBy().getScript().getMass() > 200.0f) {
                b3 = true;
            }
            if (n < (b3 ? 20 : 5)) {
                this.EngineForce *= Math.min(1.2f, this.vehicleObject.getVehicleTowedBy().getMass() / 500.0f);
                if (b3) {
                    this.EngineForce *= 4.0f;
                }
            }
        }
        if (this.vehicleObject.engineSpeed > 6000.0) {
            this.EngineForce *= (float)((7000.0 - this.vehicleObject.engineSpeed) / 1000.0);
        }
        if (b2) {
            this.EngineForce *= 0.6f;
            if (n > 20.0f) {
                this.EngineForce *= (40.0f - n) / 20.0f;
            }
        }
        if (b) {
            if (n > this.vehicleObject.getMaxSpeed() * 1.15f) {
                this.EngineForce *= (this.vehicleObject.getMaxSpeed() * 1.15f + 20.0f - n) / 20.0f;
            }
        }
        else if (n > this.vehicleObject.getMaxSpeed()) {
            this.EngineForce *= (this.vehicleObject.getMaxSpeed() + 20.0f - n) / 20.0f;
        }
        this.BrakingForce = 0.0f;
        if (this.clientControls.wasUsingParkingBrakes) {
            this.clientControls.wasUsingParkingBrakes = false;
            this.EngineForce *= 8.0f;
        }
        if (GameClient.bClient && this.vehicleObject.jniSpeed >= ServerOptions.instance.SpeedLimit.getValue()) {
            this.EngineForce = 0.0f;
        }
    }
    
    private void control_ForwardNew(final float n) {
        final float n2 = GameTime.getInstance().getMultiplier() / 0.8f;
        final IsoGameCharacter driver = this.vehicleObject.getDriver();
        final boolean b = driver != null && driver.Traits.SpeedDemon.isSet();
        final boolean b2 = driver != null && driver.Traits.SundayDriver.isSet();
        final int gearRatioCount = this.vehicleObject.getScript().gearRatioCount;
        final EngineRPMData[] rpmData = this.vehicleObject.getVehicleEngineRPM().m_rpmData;
        final float n3 = this.vehicleObject.getMaxSpeed() / gearRatioCount;
        final int min = PZMath.min((int)PZMath.floor(PZMath.clamp(n, 0.0f, this.vehicleObject.getMaxSpeed()) / n3) + 1, gearRatioCount);
        final float gearChange = rpmData[min - 1].gearChange;
        TransmissionNumber transmissionNumber = TransmissionNumber.Speed1;
        switch (min) {
            case 1: {
                transmissionNumber = TransmissionNumber.Speed1;
                break;
            }
            case 2: {
                transmissionNumber = TransmissionNumber.Speed2;
                break;
            }
            case 3: {
                transmissionNumber = TransmissionNumber.Speed3;
                break;
            }
            case 4: {
                transmissionNumber = TransmissionNumber.Speed4;
                break;
            }
            case 5: {
                transmissionNumber = TransmissionNumber.Speed5;
                break;
            }
            case 6: {
                transmissionNumber = TransmissionNumber.Speed6;
                break;
            }
            case 7: {
                transmissionNumber = TransmissionNumber.Speed7;
                break;
            }
            case 8: {
                transmissionNumber = TransmissionNumber.Speed8;
                break;
            }
        }
        if (this.vehicleObject.transmissionNumber == TransmissionNumber.N) {
            this.vehicleObject.transmissionNumber = transmissionNumber;
        }
        else if (this.vehicleObject.transmissionNumber.getIndex() - 1 >= 0 && this.vehicleObject.transmissionNumber.getIndex() < transmissionNumber.getIndex() && this.vehicleObject.getEngineSpeed() >= rpmData[this.vehicleObject.transmissionNumber.getIndex() - 1].gearChange && n >= n3 * this.vehicleObject.transmissionNumber.getIndex()) {
            this.vehicleObject.transmissionNumber = transmissionNumber;
            this.vehicleObject.engineSpeed = rpmData[this.vehicleObject.transmissionNumber.getIndex() - 1].afterGearChange;
        }
        if (this.vehicleObject.transmissionNumber.getIndex() < gearRatioCount && this.vehicleObject.transmissionNumber.getIndex() - 1 >= 0) {
            this.vehicleObject.engineSpeed = Math.min(this.vehicleObject.engineSpeed, rpmData[this.vehicleObject.transmissionNumber.getIndex() - 1].gearChange + 100.0f);
        }
        if (this.vehicleObject.engineSpeed > gearChange) {
            final BaseVehicle vehicleObject = this.vehicleObject;
            vehicleObject.engineSpeed -= Math.min(0.5 * (this.vehicleObject.engineSpeed - gearChange), 10.0) * n2;
        }
        else {
            float n4 = 0.0f;
            switch (this.vehicleObject.transmissionNumber) {
                case Speed1: {
                    n4 = 10.0f;
                    break;
                }
                case Speed2: {
                    n4 = 8.0f;
                    break;
                }
                case Speed3: {
                    n4 = 7.0f;
                    break;
                }
                case Speed4: {
                    n4 = 6.0f;
                    break;
                }
                case Speed5: {
                    n4 = 5.0f;
                    break;
                }
                default: {
                    n4 = 4.0f;
                    break;
                }
            }
            final float n5 = n4;
            final BaseVehicle vehicleObject2 = this.vehicleObject;
            vehicleObject2.engineSpeed += n5 * n2;
        }
        final float n6 = (float)this.vehicleObject.getEnginePower();
        final float engineForce = this.vehicleObject.getScript().getEngineForce();
        float n7 = 0.0f;
        switch (this.vehicleObject.transmissionNumber) {
            case Speed1: {
                n7 = 1.5f;
                break;
            }
            default: {
                n7 = 1.0f;
                break;
            }
        }
        this.EngineForce = (float)(engineForce * n7 * (0.30000001192092896 + this.vehicleObject.engineSpeed / 30000.0));
        this.EngineForce -= this.EngineForce * (n / 200.0f);
        boolean b3 = false;
        if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed1 && this.vehicleObject.getVehicleTowedBy() != null) {
            if (this.vehicleObject.getVehicleTowedBy().getScript().getPassengerCount() == 0 && this.vehicleObject.getVehicleTowedBy().getScript().getMass() > 200.0f) {
                b3 = true;
            }
            if (n < (b3 ? 20 : 5)) {
                this.EngineForce *= Math.min(1.2f, this.vehicleObject.getVehicleTowedBy().getMass() / 500.0f);
                if (b3) {
                    this.EngineForce *= 4.0f;
                }
            }
        }
        if (this.vehicleObject.engineSpeed > 6000.0) {
            this.EngineForce *= (float)((7000.0 - this.vehicleObject.engineSpeed) / 1000.0);
        }
        if (b2) {
            this.EngineForce *= 0.6f;
            if (n > 20.0f) {
                this.EngineForce *= (40.0f - n) / 20.0f;
            }
        }
        if (b) {
            if (n > this.vehicleObject.getMaxSpeed() * 1.15f) {
                this.EngineForce *= (this.vehicleObject.getMaxSpeed() * 1.15f + 20.0f - n) / 20.0f;
            }
        }
        else if (n > this.vehicleObject.getMaxSpeed()) {
            this.EngineForce *= (this.vehicleObject.getMaxSpeed() + 20.0f - n) / 20.0f;
        }
        this.BrakingForce = 0.0f;
        if (this.clientControls.wasUsingParkingBrakes) {
            this.clientControls.wasUsingParkingBrakes = false;
            this.EngineForce *= 8.0f;
        }
        if (GameClient.bClient && this.vehicleObject.jniSpeed >= ServerOptions.instance.SpeedLimit.getValue()) {
            this.EngineForce = 0.0f;
        }
    }
    
    private void control_Reverse(float n) {
        final float n2 = GameTime.getInstance().getMultiplier() / 0.8f;
        n *= 1.5f;
        final IsoGameCharacter driver = this.vehicleObject.getDriver();
        final boolean b = driver != null && driver.Traits.SpeedDemon.isSet();
        final boolean b2 = driver != null && driver.Traits.SundayDriver.isSet();
        this.vehicleObject.transmissionNumber = TransmissionNumber.R;
        final float n3 = 1000.0f * n / 30.0f;
        final BaseVehicle vehicleObject = this.vehicleObject;
        vehicleObject.engineSpeed -= Math.min(0.5 * (this.vehicleObject.engineSpeed - n3), 100.0) * n2;
        if (b) {
            final BaseVehicle vehicleObject2 = this.vehicleObject;
            vehicleObject2.engineSpeed -= Math.min(0.06 * (this.vehicleObject.engineSpeed - 7000.0), 30.0f - n) * n2;
        }
        else {
            final BaseVehicle vehicleObject3 = this.vehicleObject;
            vehicleObject3.engineSpeed -= Math.min(0.02 * (this.vehicleObject.engineSpeed - 7000.0), 30.0f - n) * n2;
        }
        this.EngineForce = (float)(-1.0f * this.vehicleObject.getEnginePower() * (0.75 + this.vehicleObject.engineSpeed / 24000.0));
        if (this.vehicleObject.engineSpeed > 6000.0) {
            this.EngineForce *= (float)((7000.0 - this.vehicleObject.engineSpeed) / 1000.0);
        }
        if (b2) {
            this.EngineForce *= 0.7f;
            if (n < -5.0f) {
                this.EngineForce *= (15.0f + n) / 10.0f;
            }
        }
        if (n < -30.0f) {
            this.EngineForce *= (40.0f + n) / 10.0f;
        }
        this.BrakingForce = 0.0f;
    }
    
    private void updateRammingSound(final float n) {
        if (this.vehicleObject.isEngineRunning() && ((n < 1.0f && this.EngineForce > this.vehicleObject.getScript().getEngineIdleSpeed() * 2.0f) || (n > -0.5f && this.EngineForce < this.vehicleObject.getScript().getEngineIdleSpeed() * -2.0f))) {
            if (this.vehicleObject.ramSound == 0L) {
                this.vehicleObject.ramSound = this.vehicleObject.playSoundImpl("VehicleSkid", null);
                this.vehicleObject.ramSoundTime = System.currentTimeMillis() + 1000L + Rand.Next(2000);
            }
            if (this.vehicleObject.ramSound != 0L && this.vehicleObject.ramSoundTime < System.currentTimeMillis()) {
                this.vehicleObject.stopSound(this.vehicleObject.ramSound);
                this.vehicleObject.ramSound = 0L;
            }
        }
        else if (this.vehicleObject.ramSound != 0L) {
            this.vehicleObject.stopSound(this.vehicleObject.ramSound);
            this.vehicleObject.ramSound = 0L;
        }
    }
    
    private void updateBackSignal() {
        if (this.isGasR && this.vehicleObject.isEngineRunning() && this.vehicleObject.hasBackSignal() && !this.vehicleObject.isBackSignalEmitting()) {
            if (GameClient.bClient) {
                GameClient.instance.sendClientCommandV((IsoPlayer)this.vehicleObject.getDriver(), "vehicle", "onBackSignal", "state", "start");
            }
            else {
                this.vehicleObject.onBackMoveSignalStart();
            }
        }
        if (!this.isGasR && this.vehicleObject.isBackSignalEmitting()) {
            if (GameClient.bClient) {
                GameClient.instance.sendClientCommandV((IsoPlayer)this.vehicleObject.getDriver(), "vehicle", "onBackSignal", "state", "stop");
            }
            else {
                this.vehicleObject.onBackMoveSignalStop();
            }
        }
    }
    
    private void updateBrakeLights() {
        if (this.isBreak) {
            if (this.vehicleObject.getStoplightsOn()) {
                return;
            }
            if (GameClient.bClient) {
                GameClient.instance.sendClientCommandV((IsoPlayer)this.vehicleObject.getDriver(), "vehicle", "setStoplightsOn", "on", Boolean.TRUE);
            }
            if (!GameServer.bServer) {
                this.vehicleObject.setStoplightsOn(true);
            }
        }
        else {
            if (!this.vehicleObject.getStoplightsOn()) {
                return;
            }
            if (GameClient.bClient) {
                GameClient.instance.sendClientCommandV((IsoPlayer)this.vehicleObject.getDriver(), "vehicle", "setStoplightsOn", "on", Boolean.FALSE);
            }
            if (!GameServer.bServer) {
                this.vehicleObject.setStoplightsOn(false);
            }
        }
    }
    
    private boolean delayCommandWhileDrunk(final boolean b) {
        this.drunkDelayCommandTimer += GameTime.getInstance().getMultiplier();
        if (Rand.AdjustForFramerate(4 * this.vehicleObject.getDriver().getMoodles().getMoodleLevel(MoodleType.Drunk)) < this.drunkDelayCommandTimer) {
            this.drunkDelayCommandTimer = 0.0f;
            return true;
        }
        return false;
    }
    
    private float delayCommandWhileDrunk(final float n) {
        this.drunkDelayCommandTimer += GameTime.getInstance().getMultiplier();
        if (Rand.AdjustForFramerate(4 * this.vehicleObject.getDriver().getMoodles().getMoodleLevel(MoodleType.Drunk)) < this.drunkDelayCommandTimer) {
            this.drunkDelayCommandTimer = 0.0f;
            return n;
        }
        return 0.0f;
    }
    
    private void checkTire(final BulletVariables bulletVariables) {
        if (this.vehicleObject.getPartById("TireFrontLeft") == null || this.vehicleObject.getPartById("TireFrontLeft").getInventoryItem() == null) {
            bulletVariables.brakingForce /= (float)1.2;
            bulletVariables.engineForce /= (float)1.2;
        }
        if (this.vehicleObject.getPartById("TireFrontRight") == null || this.vehicleObject.getPartById("TireFrontRight").getInventoryItem() == null) {
            bulletVariables.brakingForce /= (float)1.2;
            bulletVariables.engineForce /= (float)1.2;
        }
        if (this.vehicleObject.getPartById("TireRearLeft") == null || this.vehicleObject.getPartById("TireRearLeft").getInventoryItem() == null) {
            bulletVariables.brakingForce /= (float)1.3;
            bulletVariables.engineForce /= (float)1.3;
        }
        if (this.vehicleObject.getPartById("TireRearRight") == null || this.vehicleObject.getPartById("TireRearRight").getInventoryItem() == null) {
            bulletVariables.brakingForce /= (float)1.3;
            bulletVariables.engineForce /= (float)1.3;
        }
    }
    
    public void updateControls() {
        if (!GameServer.bServer) {
            if (this.vehicleObject.isKeyboardControlled()) {
                final boolean keyDown = GameKeyboard.isKeyDown(Core.getInstance().getKey("Left"));
                final boolean keyDown2 = GameKeyboard.isKeyDown(Core.getInstance().getKey("Right"));
                final boolean keyDown3 = GameKeyboard.isKeyDown(Core.getInstance().getKey("Forward"));
                final boolean keyDown4 = GameKeyboard.isKeyDown(Core.getInstance().getKey("Backward"));
                final boolean keyDown5 = GameKeyboard.isKeyDown(57);
                final boolean keyDown6 = GameKeyboard.isKeyDown(42);
                this.clientControls.steering = 0.0f;
                if (keyDown) {
                    final ClientControls clientControls = this.clientControls;
                    --clientControls.steering;
                }
                if (keyDown2) {
                    final ClientControls clientControls2 = this.clientControls;
                    ++clientControls2.steering;
                }
                this.clientControls.forward = keyDown3;
                this.clientControls.backward = keyDown4;
                this.clientControls.brake = keyDown5;
                this.clientControls.shift = keyDown6;
                if (this.clientControls.brake) {
                    this.clientControls.wasUsingParkingBrakes = true;
                }
            }
            final int joypad = this.vehicleObject.getJoypad();
            if (joypad != -1) {
                JoypadManager.instance.isLeftPressed(joypad);
                JoypadManager.instance.isRightPressed(joypad);
                final boolean rtPressed = JoypadManager.instance.isRTPressed(joypad);
                final boolean ltPressed = JoypadManager.instance.isLTPressed(joypad);
                final boolean bPressed = JoypadManager.instance.isBPressed(joypad);
                this.clientControls.steering = JoypadManager.instance.getMovementAxisX(joypad);
                this.clientControls.forward = rtPressed;
                this.clientControls.backward = ltPressed;
                this.clientControls.brake = bPressed;
            }
        }
    }
    
    public void render() {
    }
    
    public void park() {
        if (this.vehicleObject.getScript().getWheelCount() > 0) {
            Bullet.controlVehicle(this.vehicleObject.VehicleID, 0.0f, this.vehicleObject.getBrakingForce(), 0.0f);
        }
        final boolean b = false;
        this.wasGas = b;
        this.isGas = b;
        final boolean b2 = false;
        this.wasGasR = b2;
        this.isGasR = b2;
        this.clientControls.reset();
        this.vehicleObject.transmissionNumber = TransmissionNumber.N;
        if (this.vehicleObject.getVehicleTowing() != null) {
            this.vehicleObject.getVehicleTowing().getController().park();
        }
    }
    
    protected boolean shouldBeActive() {
        if (this.vehicleObject.physicActiveCheck != -1L) {
            return true;
        }
        final BaseVehicle vehicleTowedBy = this.vehicleObject.getVehicleTowedBy();
        if (vehicleTowedBy == null) {
            return Math.abs(this.vehicleObject.isEngineRunning() ? this.EngineForce : 0.0f) > 0.01f;
        }
        return vehicleTowedBy.getController() != null && vehicleTowedBy.getController().shouldBeActive();
    }
    
    public void checkShouldBeActive() {
        if (this.shouldBeActive()) {
            if (!this.isEnable) {
                Bullet.setVehicleActive(this.vehicleObject.VehicleID, true);
                this.isEnable = true;
            }
            this.atRestTimer = 1.0f;
        }
        else if (this.isEnable && this.vehicleObject.isAtRest()) {
            if (this.atRestTimer > 0.0f) {
                this.atRestTimer -= GameTime.getInstance().getTimeDelta();
            }
            if (this.atRestTimer <= 0.0f) {
                Bullet.setVehicleActive(this.vehicleObject.VehicleID, false);
                this.isEnable = false;
            }
        }
    }
    
    public boolean isGasPedalPressed() {
        return this.isGas || this.isGasR;
    }
    
    public boolean isBrakePedalPressed() {
        return this.isBreak;
    }
    
    public void debug() {
        if (!Core.bDebug || !DebugOptions.instance.VehicleRenderOutline.getValue()) {
            return;
        }
        final VehicleScript script = this.vehicleObject.getScript();
        final Vector3f tempVec3f = this.tempVec3f;
        this.vehicleObject.getForwardVector(tempVec3f);
        this.vehicleObject.getWorldTransform(this.tempXfrm);
        final PolygonalMap2.VehiclePoly poly = this.vehicleObject.getPoly();
        LineDrawer.addLine(poly.x1, poly.y1, 0.0f, poly.x2, poly.y2, 0.0f, 1.0f, 1.0f, 1.0f, null, true);
        LineDrawer.addLine(poly.x2, poly.y2, 0.0f, poly.x3, poly.y3, 0.0f, 1.0f, 1.0f, 1.0f, null, true);
        LineDrawer.addLine(poly.x3, poly.y3, 0.0f, poly.x4, poly.y4, 0.0f, 1.0f, 1.0f, 1.0f, null, true);
        LineDrawer.addLine(poly.x4, poly.y4, 0.0f, poly.x1, poly.y1, 0.0f, 1.0f, 1.0f, 1.0f, null, true);
        CarController._UNIT_Y.set(0.0f, 1.0f, 0.0f);
        for (int i = 0; i < this.vehicleObject.getScript().getWheelCount(); ++i) {
            final VehicleScript.Wheel wheel = script.getWheel(i);
            this.tempVec3f.set((Vector3fc)wheel.getOffset());
            if (script.getModel() != null) {
                this.tempVec3f.add((Vector3fc)script.getModelOffset());
            }
            this.vehicleObject.getWorldPos(this.tempVec3f, this.tempVec3f);
            final float x = this.tempVec3f.x;
            final float y = this.tempVec3f.y;
            this.vehicleObject.getWheelForwardVector(i, this.tempVec3f);
            LineDrawer.addLine(x, y, 0.0f, x + this.tempVec3f.x, y + this.tempVec3f.z, 0.0f, 1.0f, 1.0f, 1.0f, null, true);
            this.drawRect(this.tempVec3f, x - WorldSimulation.instance.offsetX, y - WorldSimulation.instance.offsetY, wheel.width, wheel.radius);
        }
        if (this.vehicleObject.collideX != -1.0f) {
            this.vehicleObject.getForwardVector(tempVec3f);
            this.drawCircle(this.vehicleObject.collideX, this.vehicleObject.collideY, 0.3f);
            this.vehicleObject.collideX = -1.0f;
            this.vehicleObject.collideY = -1.0f;
        }
        final int joypad = this.vehicleObject.getJoypad();
        if (joypad != -1) {
            final float movementAxisX = JoypadManager.instance.getMovementAxisX(joypad);
            final float movementAxisY = JoypadManager.instance.getMovementAxisY(joypad);
            final float deadZone = JoypadManager.instance.getDeadZone(joypad, 0);
            if (Math.abs(movementAxisY) > deadZone || Math.abs(movementAxisX) > deadZone) {
                final Vector2 set = this.tempVec2.set(movementAxisX, movementAxisY);
                set.setLength(4.0f);
                set.rotate(-0.7853982f);
                LineDrawer.addLine(this.vehicleObject.getX(), this.vehicleObject.getY(), this.vehicleObject.z, this.vehicleObject.getX() + set.x, this.vehicleObject.getY() + set.y, this.vehicleObject.z, 1.0f, 1.0f, 1.0f, null, true);
            }
        }
        final float x2 = this.vehicleObject.x;
        final float y2 = this.vehicleObject.y;
        final float z = this.vehicleObject.z;
        LineDrawer.DrawIsoLine(x2 - 0.5f, y2, z, x2 + 0.5f, y2, z, 1.0f, 1.0f, 1.0f, 0.25f, 1);
        LineDrawer.DrawIsoLine(x2, y2 - 0.5f, z, x2, y2 + 0.5f, z, 1.0f, 1.0f, 1.0f, 0.25f, 1);
    }
    
    public void drawRect(final Vector3f vector3f, final float n, final float n2, final float n3, final float n4) {
        this.drawRect(vector3f, n, n2, n3, n4, 1.0f, 1.0f, 1.0f);
    }
    
    public void drawRect(final Vector3f vector3f, final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7) {
        final float x = vector3f.x;
        final float y = vector3f.y;
        final float z = vector3f.z;
        final Vector3f tempVec3f_3 = this.tempVec3f_3;
        vector3f.cross((Vector3fc)CarController._UNIT_Y, tempVec3f_3);
        final float n8 = 1.0f;
        vector3f.x *= n8 * n4;
        vector3f.z *= n8 * n4;
        final Vector3f vector3f2 = tempVec3f_3;
        vector3f2.x *= n8 * n3;
        final Vector3f vector3f3 = tempVec3f_3;
        vector3f3.z *= n8 * n3;
        final float n9 = n + vector3f.x;
        final float n10 = n2 + vector3f.z;
        final float n11 = n - vector3f.x;
        final float n12 = n2 - vector3f.z;
        final float n13 = n9 - tempVec3f_3.x / 2.0f;
        final float n14 = n9 + tempVec3f_3.x / 2.0f;
        final float n15 = n11 - tempVec3f_3.x / 2.0f;
        final float n16 = n11 + tempVec3f_3.x / 2.0f;
        final float n17 = n12 - tempVec3f_3.z / 2.0f;
        final float n18 = n12 + tempVec3f_3.z / 2.0f;
        final float n19 = n10 - tempVec3f_3.z / 2.0f;
        final float n20 = n10 + tempVec3f_3.z / 2.0f;
        final float n21 = n13 + WorldSimulation.instance.offsetX;
        final float n22 = n19 + WorldSimulation.instance.offsetY;
        final float n23 = n14 + WorldSimulation.instance.offsetX;
        final float n24 = n20 + WorldSimulation.instance.offsetY;
        final float n25 = n15 + WorldSimulation.instance.offsetX;
        final float n26 = n17 + WorldSimulation.instance.offsetY;
        final float n27 = n16 + WorldSimulation.instance.offsetX;
        final float n28 = n18 + WorldSimulation.instance.offsetY;
        LineDrawer.addLine(n21, n22, 0.0f, n23, n24, 0.0f, n5, n6, n7, null, true);
        LineDrawer.addLine(n21, n22, 0.0f, n25, n26, 0.0f, n5, n6, n7, null, true);
        LineDrawer.addLine(n23, n24, 0.0f, n27, n28, 0.0f, n5, n6, n7, null, true);
        LineDrawer.addLine(n25, n26, 0.0f, n27, n28, 0.0f, n5, n6, n7, null, true);
        vector3f.set(x, y, z);
    }
    
    public void drawCircle(final float n, final float n2, final float n3) {
        this.drawCircle(n, n2, n3, 1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    public void drawCircle(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7) {
        LineDrawer.DrawIsoCircle(n, n2, 0.0f, n3, 16, n4, n5, n6, n7);
    }
    
    static {
        _UNIT_Y = new Vector3f(0.0f, 1.0f, 0.0f);
        (CarController.gears = new GearInfo[3])[0] = new GearInfo(0, 25, 0.0f);
        CarController.gears[1] = new GearInfo(25, 50, 0.5f);
        CarController.gears[2] = new GearInfo(50, 1000, 0.5f);
        bulletVariables = new BulletVariables();
    }
    
    public static final class BulletVariables
    {
        float engineForce;
        float brakingForce;
        float vehicleSteering;
        BaseVehicle vehicle;
        
        BulletVariables set(final BaseVehicle vehicle, final float engineForce, final float brakingForce, final float vehicleSteering) {
            this.vehicle = vehicle;
            this.engineForce = engineForce;
            this.brakingForce = brakingForce;
            this.vehicleSteering = vehicleSteering;
            return this;
        }
    }
    
    public static final class GearInfo
    {
        int minSpeed;
        int maxSpeed;
        float minRPM;
        
        GearInfo(final int minSpeed, final int maxSpeed, final float minRPM) {
            this.minSpeed = minSpeed;
            this.maxSpeed = maxSpeed;
            this.minRPM = minRPM;
        }
    }
    
    public static final class ClientControls
    {
        public float steering;
        public boolean forward;
        public boolean backward;
        public boolean brake;
        public boolean shift;
        public boolean wasUsingParkingBrakes;
        
        public void reset() {
            this.steering = 0.0f;
            this.forward = false;
            this.backward = false;
            this.brake = false;
            this.shift = false;
            this.wasUsingParkingBrakes = false;
        }
    }
    
    enum ControlState
    {
        NoControl, 
        Braking, 
        Forward, 
        Reverse;
        
        private static /* synthetic */ ControlState[] $values() {
            return new ControlState[] { ControlState.NoControl, ControlState.Braking, ControlState.Forward, ControlState.Reverse };
        }
        
        static {
            $VALUES = $values();
        }
    }
}

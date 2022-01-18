// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio.parameters;

import zombie.scripting.objects.VehicleScript;
import zombie.core.math.PZMath;
import zombie.network.GameClient;
import zombie.vehicles.BaseVehicle;
import zombie.audio.FMODLocalParameter;

public class ParameterVehicleSkid extends FMODLocalParameter
{
    private final BaseVehicle vehicle;
    private final BaseVehicle.WheelInfo[] wheelInfo;
    
    public ParameterVehicleSkid(final BaseVehicle vehicle) {
        super("VehicleSkid");
        this.vehicle = vehicle;
        this.wheelInfo = vehicle.wheelInfo;
    }
    
    @Override
    public float calculateCurrentValue() {
        float min = 1.0f;
        if (GameClient.bClient && !this.vehicle.isLocalPhysicSim()) {
            return min;
        }
        final VehicleScript script = this.vehicle.getScript();
        if (script == null) {
            return min;
        }
        for (int i = 0; i < script.getWheelCount(); ++i) {
            min = PZMath.min(min, this.wheelInfo[i].skidInfo);
        }
        return (int)(100.0f - PZMath.clamp(min, 0.0f, 1.0f) * 100.0f) / 100.0f;
    }
}

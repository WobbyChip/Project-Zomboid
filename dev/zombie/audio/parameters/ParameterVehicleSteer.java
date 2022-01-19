// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio.parameters;

import zombie.scripting.objects.VehicleScript;
import zombie.core.math.PZMath;
import zombie.vehicles.BaseVehicle;
import zombie.audio.FMODLocalParameter;

public class ParameterVehicleSteer extends FMODLocalParameter
{
    private final BaseVehicle vehicle;
    
    public ParameterVehicleSteer(final BaseVehicle vehicle) {
        super("VehicleSteer");
        this.vehicle = vehicle;
    }
    
    @Override
    public float calculateCurrentValue() {
        float max = 0.0f;
        if (!this.vehicle.isEngineRunning()) {
            return max;
        }
        final VehicleScript script = this.vehicle.getScript();
        if (script == null) {
            return max;
        }
        final BaseVehicle.WheelInfo[] wheelInfo = this.vehicle.wheelInfo;
        for (int i = 0; i < script.getWheelCount(); ++i) {
            max = PZMath.max(max, Math.abs(wheelInfo[i].steering));
        }
        return (int)(PZMath.clamp(max, 0.0f, 1.0f) * 100.0f) / 100.0f;
    }
}

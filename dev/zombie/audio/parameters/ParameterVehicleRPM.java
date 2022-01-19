// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio.parameters;

import zombie.core.math.PZMath;
import zombie.vehicles.BaseVehicle;
import zombie.audio.FMODLocalParameter;

public class ParameterVehicleRPM extends FMODLocalParameter
{
    private final BaseVehicle vehicle;
    
    public ParameterVehicleRPM(final BaseVehicle vehicle) {
        super("VehicleRPM");
        this.vehicle = vehicle;
    }
    
    @Override
    public float calculateCurrentValue() {
        final float clamp = PZMath.clamp((float)this.vehicle.getEngineSpeed(), 0.0f, 7000.0f);
        final float n = this.vehicle.getScript().getEngineIdleSpeed() * 1.1f;
        final float n2 = 800.0f;
        final float n3 = 7000.0f;
        float n4;
        if (clamp < n) {
            n4 = clamp / n * n2;
        }
        else {
            n4 = n2 + (clamp - n) / (7000.0f - n) * (n3 - n2);
        }
        return (int)((n4 + 50.0f - 1.0f) / 50.0f) * 50.0f;
    }
}

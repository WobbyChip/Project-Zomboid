// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio.parameters;

import zombie.vehicles.BaseVehicle;
import zombie.audio.FMODLocalParameter;

public class ParameterVehicleSpeed extends FMODLocalParameter
{
    private final BaseVehicle vehicle;
    
    public ParameterVehicleSpeed(final BaseVehicle vehicle) {
        super("VehicleSpeed");
        this.vehicle = vehicle;
    }
    
    @Override
    public float calculateCurrentValue() {
        return (float)Math.round(Math.abs(this.vehicle.getCurrentSpeedKmHour()));
    }
}

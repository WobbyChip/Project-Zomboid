// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio.parameters;

import zombie.vehicles.BaseVehicle;
import zombie.audio.FMODLocalParameter;

public class ParameterVehicleBrake extends FMODLocalParameter
{
    private final BaseVehicle vehicle;
    
    public ParameterVehicleBrake(final BaseVehicle vehicle) {
        super("VehicleBrake");
        this.vehicle = vehicle;
    }
    
    @Override
    public float calculateCurrentValue() {
        return this.vehicle.getController().isBrakePedalPressed() ? 1.0f : 0.0f;
    }
}

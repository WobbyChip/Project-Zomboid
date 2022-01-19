// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio.parameters;

import zombie.vehicles.BaseVehicle;
import zombie.audio.FMODLocalParameter;

public class ParameterVehicleLoad extends FMODLocalParameter
{
    private final BaseVehicle vehicle;
    
    public ParameterVehicleLoad(final BaseVehicle vehicle) {
        super("VehicleLoad");
        this.vehicle = vehicle;
    }
    
    @Override
    public float calculateCurrentValue() {
        return this.vehicle.getController().isGasPedalPressed() ? 1.0f : 0.0f;
    }
}

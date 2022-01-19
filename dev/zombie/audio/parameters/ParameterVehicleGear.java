// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio.parameters;

import zombie.vehicles.BaseVehicle;
import zombie.audio.FMODLocalParameter;

public class ParameterVehicleGear extends FMODLocalParameter
{
    private final BaseVehicle vehicle;
    
    public ParameterVehicleGear(final BaseVehicle vehicle) {
        super("VehicleGear");
        this.vehicle = vehicle;
    }
    
    @Override
    public float calculateCurrentValue() {
        return (float)(this.vehicle.getTransmissionNumber() + 1);
    }
}

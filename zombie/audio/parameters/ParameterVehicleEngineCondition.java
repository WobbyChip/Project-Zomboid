// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio.parameters;

import zombie.vehicles.VehiclePart;
import zombie.core.math.PZMath;
import zombie.vehicles.BaseVehicle;
import zombie.audio.FMODLocalParameter;

public class ParameterVehicleEngineCondition extends FMODLocalParameter
{
    private final BaseVehicle vehicle;
    
    public ParameterVehicleEngineCondition(final BaseVehicle vehicle) {
        super("VehicleEngineCondition");
        this.vehicle = vehicle;
    }
    
    @Override
    public float calculateCurrentValue() {
        final VehiclePart partById = this.vehicle.getPartById("Engine");
        return (partById == null) ? 100.0f : ((float)PZMath.clamp(partById.getCondition(), 0, 100));
    }
}

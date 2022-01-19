// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio.parameters;

import zombie.vehicles.VehiclePart;
import zombie.scripting.objects.VehicleScript;
import zombie.vehicles.BaseVehicle;
import zombie.audio.FMODLocalParameter;

public class ParameterVehicleTireMissing extends FMODLocalParameter
{
    private final BaseVehicle vehicle;
    
    public ParameterVehicleTireMissing(final BaseVehicle vehicle) {
        super("VehicleTireMissing");
        this.vehicle = vehicle;
    }
    
    @Override
    public float calculateCurrentValue() {
        boolean b = false;
        final VehicleScript script = this.vehicle.getScript();
        if (script != null) {
            for (int i = 0; i < script.getWheelCount(); ++i) {
                final VehiclePart partById = this.vehicle.getPartById(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, script.getWheel(i).getId()));
                if (partById == null || partById.getInventoryItem() == null) {
                    b = true;
                    break;
                }
            }
        }
        return b ? 1.0f : 0.0f;
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio.parameters;

import zombie.iso.weather.ClimateManager;
import zombie.audio.FMODGlobalParameter;

public final class ParameterSnowIntensity extends FMODGlobalParameter
{
    public ParameterSnowIntensity() {
        super("SnowIntensity");
    }
    
    @Override
    public float calculateCurrentValue() {
        if (ClimateManager.getInstance().isSnowing()) {
            return ClimateManager.getInstance().getPrecipitationIntensity();
        }
        return 0.0f;
    }
}

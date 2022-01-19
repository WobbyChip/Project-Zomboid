// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio.parameters;

import zombie.iso.weather.ClimateManager;
import zombie.audio.FMODGlobalParameter;

public final class ParameterRainIntensity extends FMODGlobalParameter
{
    public ParameterRainIntensity() {
        super("RainIntensity");
    }
    
    @Override
    public float calculateCurrentValue() {
        if (ClimateManager.getInstance().isRaining()) {
            return ClimateManager.getInstance().getPrecipitationIntensity();
        }
        return 0.0f;
    }
}

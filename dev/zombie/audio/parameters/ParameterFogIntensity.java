// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio.parameters;

import zombie.iso.weather.ClimateManager;
import zombie.audio.FMODGlobalParameter;

public final class ParameterFogIntensity extends FMODGlobalParameter
{
    public ParameterFogIntensity() {
        super("FogIntensity");
    }
    
    @Override
    public float calculateCurrentValue() {
        return ClimateManager.getInstance().getFogIntensity();
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio.parameters;

import zombie.iso.weather.ClimateManager;
import zombie.audio.FMODGlobalParameter;

public final class ParameterTemperature extends FMODGlobalParameter
{
    public ParameterTemperature() {
        super("Temperature");
    }
    
    @Override
    public float calculateCurrentValue() {
        return (int)(ClimateManager.getInstance().getTemperature() * 100.0f) / 100.0f;
    }
}

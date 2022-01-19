// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio.parameters;

import zombie.iso.weather.ClimateManager;
import zombie.audio.FMODGlobalParameter;

public final class ParameterWindIntensity extends FMODGlobalParameter
{
    public ParameterWindIntensity() {
        super("WindIntensity");
    }
    
    @Override
    public float calculateCurrentValue() {
        return (int)(ClimateManager.getInstance().getWindIntensity() * 1000.0f) / 1000.0f;
    }
}

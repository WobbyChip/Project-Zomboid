// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio.parameters;

import zombie.iso.weather.WeatherPeriod;
import zombie.iso.weather.ClimateManager;
import zombie.audio.FMODGlobalParameter;

public final class ParameterStorm extends FMODGlobalParameter
{
    public ParameterStorm() {
        super("Storm");
    }
    
    @Override
    public float calculateCurrentValue() {
        final WeatherPeriod weatherPeriod = ClimateManager.getInstance().getWeatherPeriod();
        if (weatherPeriod.isRunning()) {
            if (weatherPeriod.isThunderStorm()) {
                return 1.0f;
            }
            if (weatherPeriod.isTropicalStorm()) {
                return 2.0f;
            }
            if (weatherPeriod.isBlizzard()) {
                return 3.0f;
            }
        }
        return 0.0f;
    }
}

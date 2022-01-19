// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio.parameters;

import zombie.iso.weather.ClimateManager;
import zombie.audio.FMODGlobalParameter;

public final class ParameterSeason extends FMODGlobalParameter
{
    public ParameterSeason() {
        super("Season");
    }
    
    @Override
    public float calculateCurrentValue() {
        final ClimateManager.DayInfo currentDay = ClimateManager.getInstance().getCurrentDay();
        if (currentDay == null) {
            return 0.0f;
        }
        float n = 0.0f;
        switch (currentDay.season.getSeason()) {
            case 1: {
                n = 0.0f;
                break;
            }
            case 2:
            case 3: {
                n = 1.0f;
                break;
            }
            case 4: {
                n = 2.0f;
                break;
            }
            case 5: {
                n = 3.0f;
                break;
            }
            default: {
                n = 1.0f;
                break;
            }
        }
        return n;
    }
}

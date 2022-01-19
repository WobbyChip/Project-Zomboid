// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio.parameters;

import zombie.GameTime;
import zombie.iso.weather.ClimateManager;
import zombie.audio.FMODGlobalParameter;

public final class ParameterTimeOfDay extends FMODGlobalParameter
{
    public ParameterTimeOfDay() {
        super("TimeOfDay");
    }
    
    @Override
    public float calculateCurrentValue() {
        final ClimateManager.DayInfo currentDay = ClimateManager.getInstance().getCurrentDay();
        if (currentDay == null) {
            return 1.0f;
        }
        final float dawn = currentDay.season.getDawn();
        final float dusk = currentDay.season.getDusk();
        currentDay.season.getDayHighNoon();
        final float timeOfDay = GameTime.instance.getTimeOfDay();
        if (timeOfDay >= dawn - 1.0f && timeOfDay < dawn + 1.0f) {
            return 0.0f;
        }
        if (timeOfDay >= dawn + 1.0f && timeOfDay < dawn + 2.0f) {
            return 1.0f;
        }
        if (timeOfDay >= dawn + 2.0f && timeOfDay < dusk - 2.0f) {
            return 2.0f;
        }
        if (timeOfDay >= dusk - 2.0f && timeOfDay < dusk - 1.0f) {
            return 3.0f;
        }
        if (timeOfDay >= dusk - 1.0f && timeOfDay < dusk + 1.0f) {
            return 4.0f;
        }
        return 5.0f;
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio.parameters;

import zombie.SandboxOptions;
import zombie.iso.weather.ClimateManager;
import zombie.audio.FMODGlobalParameter;

public final class ParameterWeatherEvent extends FMODGlobalParameter
{
    private Event event;
    
    public ParameterWeatherEvent() {
        super("WeatherEvent");
        this.event = Event.None;
    }
    
    @Override
    public float calculateCurrentValue() {
        ClimateManager.getInstance().getSnowFracNow();
        if (!SandboxOptions.instance.EnableSnowOnGround.getValue()) {}
        return (float)this.event.value;
    }
    
    public enum Event
    {
        None(0), 
        FreshSnow(1);
        
        final int value;
        
        private Event(final int value) {
            this.value = value;
        }
        
        private static /* synthetic */ Event[] $values() {
            return new Event[] { Event.None, Event.FreshSnow };
        }
        
        static {
            $VALUES = $values();
        }
    }
}

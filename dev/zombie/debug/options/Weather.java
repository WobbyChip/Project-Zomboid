// 
// Decompiled by Procyon v0.5.36
// 

package zombie.debug.options;

import zombie.debug.BooleanDebugOption;

public final class Weather extends OptionGroup
{
    public final BooleanDebugOption Fx;
    public final BooleanDebugOption Snow;
    public final BooleanDebugOption WaterPuddles;
    
    public Weather() {
        super("Weather");
        this.Fx = OptionGroup.newDebugOnlyOption(this.Group, "Fx", true);
        this.Snow = OptionGroup.newDebugOnlyOption(this.Group, "Snow", true);
        this.WaterPuddles = OptionGroup.newDebugOnlyOption(this.Group, "WaterPuddles", true);
    }
}

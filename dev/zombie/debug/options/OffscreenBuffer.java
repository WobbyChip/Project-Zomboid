// 
// Decompiled by Procyon v0.5.36
// 

package zombie.debug.options;

import zombie.debug.BooleanDebugOption;

public final class OffscreenBuffer extends OptionGroup
{
    public final BooleanDebugOption Render;
    
    public OffscreenBuffer() {
        super("OffscreenBuffer");
        this.Render = OptionGroup.newDebugOnlyOption(this.Group, "Render", true);
    }
}

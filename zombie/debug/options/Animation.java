// 
// Decompiled by Procyon v0.5.36
// 

package zombie.debug.options;

import zombie.debug.BooleanDebugOption;

public final class Animation extends OptionGroup
{
    public final BooleanDebugOption Debug;
    public final BooleanDebugOption AllowEarlyTransitionOut;
    public final AnimLayerOG AnimLayer;
    public final SharedSkelesOG SharedSkeles;
    public final BooleanDebugOption AnimRenderPicker;
    public final BooleanDebugOption BlendUseFbx;
    
    public Animation() {
        super("Animation");
        this.Debug = OptionGroup.newDebugOnlyOption(this.Group, "Debug", false);
        this.AllowEarlyTransitionOut = OptionGroup.newDebugOnlyOption(this.Group, "AllowEarlyTransitionOut", true);
        this.AnimLayer = new AnimLayerOG(this.Group);
        this.SharedSkeles = new SharedSkelesOG(this.Group);
        this.AnimRenderPicker = OptionGroup.newDebugOnlyOption(this.Group, "Render.Picker", false);
        this.BlendUseFbx = OptionGroup.newDebugOnlyOption(this.Group, "BlendUseFbx", false);
    }
    
    public static final class AnimLayerOG extends OptionGroup
    {
        public final BooleanDebugOption LogStateChanges;
        public final BooleanDebugOption AllowAnimNodeOverride;
        
        AnimLayerOG(final IDebugOptionGroup debugOptionGroup) {
            super(debugOptionGroup, "AnimLayer");
            this.LogStateChanges = OptionGroup.newDebugOnlyOption(this.Group, "Debug.LogStateChanges", false);
            this.AllowAnimNodeOverride = OptionGroup.newDebugOnlyOption(this.Group, "Debug.AllowAnimNodeOverride", false);
        }
    }
    
    public static final class SharedSkelesOG extends OptionGroup
    {
        public final BooleanDebugOption Enabled;
        public final BooleanDebugOption AllowLerping;
        
        SharedSkelesOG(final IDebugOptionGroup debugOptionGroup) {
            super(debugOptionGroup, "SharedSkeles");
            this.Enabled = OptionGroup.newDebugOnlyOption(this.Group, "Enabled", true);
            this.AllowLerping = OptionGroup.newDebugOnlyOption(this.Group, "AllowLerping", true);
        }
    }
}

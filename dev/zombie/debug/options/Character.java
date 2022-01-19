// 
// Decompiled by Procyon v0.5.36
// 

package zombie.debug.options;

import zombie.debug.BooleanDebugOption;

public class Character extends OptionGroup
{
    public final BooleanDebugOption CreateAllOutfits;
    public final DebugOG Debug;
    
    public Character() {
        super("Character");
        this.CreateAllOutfits = OptionGroup.newOption(this.Group, "Create.AllOutfits", false);
        this.Debug = new DebugOG(this.Group);
    }
    
    public static final class DebugOG extends OptionGroup
    {
        public final RenderOG Render;
        public final AnimateOG Animate;
        public final BooleanDebugOption RegisterDebugVariables;
        public final BooleanDebugOption AlwaysTripOverFence;
        public final BooleanDebugOption PlaySoundWhenInvisible;
        public final BooleanDebugOption UpdateAlpha;
        public final BooleanDebugOption UpdateAlphaEighthSpeed;
        
        public DebugOG(final IDebugOptionGroup debugOptionGroup) {
            super(debugOptionGroup, "Debug");
            this.Render = new RenderOG(this.Group);
            this.Animate = new AnimateOG(this.Group);
            this.RegisterDebugVariables = OptionGroup.newDebugOnlyOption(this.Group, "DebugVariables", false);
            this.AlwaysTripOverFence = OptionGroup.newDebugOnlyOption(this.Group, "AlwaysTripOverFence", false);
            this.PlaySoundWhenInvisible = OptionGroup.newDebugOnlyOption(this.Group, "PlaySoundWhenInvisible", true);
            this.UpdateAlpha = OptionGroup.newDebugOnlyOption(this.Group, "UpdateAlpha", true);
            this.UpdateAlphaEighthSpeed = OptionGroup.newDebugOnlyOption(this.Group, "UpdateAlphaEighthSpeed", false);
        }
        
        public static final class RenderOG extends OptionGroup
        {
            public final BooleanDebugOption AimCone;
            public final BooleanDebugOption Angle;
            public final BooleanDebugOption TestDotSide;
            public final BooleanDebugOption DeferredMovement;
            public final BooleanDebugOption DeferredAngles;
            public final BooleanDebugOption TranslationData;
            public final BooleanDebugOption Bip01;
            public final BooleanDebugOption PrimaryHandBone;
            public final BooleanDebugOption SecondaryHandBone;
            public final BooleanDebugOption SkipCharacters;
            public final BooleanDebugOption Vision;
            public final BooleanDebugOption DisplayRoomAndZombiesZone;
            
            public RenderOG(final IDebugOptionGroup debugOptionGroup) {
                super(debugOptionGroup, "Render");
                this.AimCone = OptionGroup.newDebugOnlyOption(this.Group, "AimCone", false);
                this.Angle = OptionGroup.newDebugOnlyOption(this.Group, "Angle", false);
                this.TestDotSide = OptionGroup.newDebugOnlyOption(this.Group, "TestDotSide", false);
                this.DeferredMovement = OptionGroup.newDebugOnlyOption(this.Group, "DeferredMovement", false);
                this.DeferredAngles = OptionGroup.newDebugOnlyOption(this.Group, "DeferredRotation", false);
                this.TranslationData = OptionGroup.newDebugOnlyOption(this.Group, "Translation_Data", false);
                this.Bip01 = OptionGroup.newDebugOnlyOption(this.Group, "Bip01", false);
                this.PrimaryHandBone = OptionGroup.newDebugOnlyOption(this.Group, "HandBones.Primary", false);
                this.SecondaryHandBone = OptionGroup.newDebugOnlyOption(this.Group, "HandBones.Secondary", false);
                this.SkipCharacters = OptionGroup.newDebugOnlyOption(this.Group, "SkipCharacters", false);
                this.Vision = OptionGroup.newDebugOnlyOption(this.Group, "Vision", false);
                this.DisplayRoomAndZombiesZone = OptionGroup.newDebugOnlyOption(this.Group, "DisplayRoomAndZombiesZone", false);
            }
        }
        
        public static final class AnimateOG extends OptionGroup
        {
            public final BooleanDebugOption DeferredRotationOnly;
            public final BooleanDebugOption NoBoneMasks;
            public final BooleanDebugOption NoBoneTwists;
            public final BooleanDebugOption ZeroCounterRotationBone;
            
            public AnimateOG(final IDebugOptionGroup debugOptionGroup) {
                super(debugOptionGroup, "Animate");
                this.DeferredRotationOnly = OptionGroup.newDebugOnlyOption(this.Group, "DeferredRotationsOnly", false);
                this.NoBoneMasks = OptionGroup.newDebugOnlyOption(this.Group, "NoBoneMasks", false);
                this.NoBoneTwists = OptionGroup.newDebugOnlyOption(this.Group, "NoBoneTwists", false);
                this.ZeroCounterRotationBone = OptionGroup.newDebugOnlyOption(this.Group, "ZeroCounterRotation", false);
            }
        }
    }
}

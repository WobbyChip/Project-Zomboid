// 
// Decompiled by Procyon v0.5.36
// 

package zombie.debug.options;

import zombie.debug.BooleanDebugOption;

public final class IsoSprite extends OptionGroup
{
    public final BooleanDebugOption RenderSprites;
    public final BooleanDebugOption RenderModels;
    public final BooleanDebugOption MovingObjectEdges;
    public final BooleanDebugOption DropShadowEdges;
    public final BooleanDebugOption NearestMagFilterAtMinZoom;
    public final BooleanDebugOption TextureWrapClampToEdge;
    public final BooleanDebugOption TextureWrapRepeat;
    public final BooleanDebugOption ForceLinearMagFilter;
    public final BooleanDebugOption ForceNearestMagFilter;
    public final BooleanDebugOption ForceNearestMipMapping;
    public final BooleanDebugOption CharacterMipmapColors;
    public final BooleanDebugOption WorldMipmapColors;
    
    public IsoSprite() {
        super("IsoSprite");
        this.RenderSprites = OptionGroup.newDebugOnlyOption(this.Group, "Render.Sprites", true);
        this.RenderModels = OptionGroup.newDebugOnlyOption(this.Group, "Render.Models", true);
        this.MovingObjectEdges = OptionGroup.newDebugOnlyOption(this.Group, "Render.MovingObjectEdges", false);
        this.DropShadowEdges = OptionGroup.newDebugOnlyOption(this.Group, "Render.DropShadowEdges", false);
        this.NearestMagFilterAtMinZoom = OptionGroup.newDebugOnlyOption(this.Group, "Render.NearestMagFilterAtMinZoom", true);
        this.TextureWrapClampToEdge = OptionGroup.newDebugOnlyOption(this.Group, "Render.TextureWrap.ClampToEdge", false);
        this.TextureWrapRepeat = OptionGroup.newDebugOnlyOption(this.Group, "Render.TextureWrap.Repeat", false);
        this.ForceLinearMagFilter = OptionGroup.newDebugOnlyOption(this.Group, "Render.ForceLinearMagFilter", false);
        this.ForceNearestMagFilter = OptionGroup.newDebugOnlyOption(this.Group, "Render.ForceNearestMagFilter", false);
        this.ForceNearestMipMapping = OptionGroup.newDebugOnlyOption(this.Group, "Render.ForceNearestMipMapping", false);
        this.CharacterMipmapColors = OptionGroup.newDebugOnlyOption(this.Group, "Render.CharacterMipmapColors", false);
        this.WorldMipmapColors = OptionGroup.newDebugOnlyOption(this.Group, "Render.WorldMipmapColors", false);
    }
}

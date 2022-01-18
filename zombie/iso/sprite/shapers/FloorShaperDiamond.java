// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.sprite.shapers;

import zombie.core.textures.TextureDraw;

public class FloorShaperDiamond extends FloorShaper
{
    public static final FloorShaperDiamond instance;
    
    @Override
    public void accept(final TextureDraw textureDraw) {
        super.accept(textureDraw);
        DiamondShaper.instance.accept(textureDraw);
    }
    
    static {
        instance = new FloorShaperDiamond();
    }
}

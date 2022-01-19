// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.sprite.shapers;

import zombie.core.textures.TextureDraw;

public class WallShaperWhole extends WallShaper
{
    public static final WallShaperWhole instance;
    
    @Override
    public void accept(final TextureDraw textureDraw) {
        super.accept(textureDraw);
        WallPaddingShaper.instance.accept(textureDraw);
    }
    
    static {
        instance = new WallShaperWhole();
    }
}

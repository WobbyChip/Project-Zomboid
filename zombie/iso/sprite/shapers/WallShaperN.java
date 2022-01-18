// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.sprite.shapers;

import zombie.core.textures.TextureDraw;

public class WallShaperN extends WallShaper
{
    public static final WallShaperN instance;
    
    @Override
    public void accept(final TextureDraw textureDraw) {
        super.accept(textureDraw);
        textureDraw.x0 = textureDraw.x0 * 0.5f + textureDraw.x1 * 0.5f;
        textureDraw.x3 = textureDraw.x2 * 0.5f + textureDraw.x3 * 0.5f;
        textureDraw.u0 = textureDraw.u0 * 0.5f + textureDraw.u1 * 0.5f;
        textureDraw.u3 = textureDraw.u2 * 0.5f + textureDraw.u3 * 0.5f;
        WallPaddingShaper.instance.accept(textureDraw);
    }
    
    static {
        instance = new WallShaperN();
    }
}

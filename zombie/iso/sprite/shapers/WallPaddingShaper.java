// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.sprite.shapers;

import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;

public class WallPaddingShaper implements Consumer<TextureDraw>
{
    public static final WallPaddingShaper instance;
    
    @Override
    public void accept(final TextureDraw textureDraw) {
        SpritePadding.applyIsoPadding(textureDraw, SpritePaddingSettings.getSettings().IsoPadding);
    }
    
    static {
        instance = new WallPaddingShaper();
    }
}

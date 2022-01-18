// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.sprite.shapers;

import zombie.core.Color;
import zombie.debug.DebugOptions;
import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;

public class WallShaper implements Consumer<TextureDraw>
{
    public final int[] col;
    protected int colTint;
    
    public WallShaper() {
        this.col = new int[4];
        this.colTint = 0;
    }
    
    public void setTintColor(final int colTint) {
        this.colTint = colTint;
    }
    
    @Override
    public void accept(final TextureDraw textureDraw) {
        if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Walls.Lighting.getValue()) {
            textureDraw.col0 = Color.blendBGR(textureDraw.col0, this.col[0]);
            textureDraw.col1 = Color.blendBGR(textureDraw.col1, this.col[1]);
            textureDraw.col2 = Color.blendBGR(textureDraw.col2, this.col[2]);
            textureDraw.col3 = Color.blendBGR(textureDraw.col3, this.col[3]);
        }
        if (this.colTint != 0) {
            textureDraw.col0 = Color.tintABGR(textureDraw.col0, this.colTint);
            textureDraw.col1 = Color.tintABGR(textureDraw.col1, this.colTint);
            textureDraw.col2 = Color.tintABGR(textureDraw.col2, this.colTint);
            textureDraw.col3 = Color.tintABGR(textureDraw.col3, this.colTint);
        }
    }
}

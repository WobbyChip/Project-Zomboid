// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.sprite.shapers;

import zombie.core.Color;
import zombie.debug.DebugOptions;
import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;

public class FloorShaper implements Consumer<TextureDraw>
{
    protected final int[] col;
    protected int colTint;
    protected boolean isShore;
    protected final float[] waterDepth;
    
    public FloorShaper() {
        this.col = new int[4];
        this.colTint = 0;
        this.isShore = false;
        this.waterDepth = new float[4];
    }
    
    public void setVertColors(final int n, final int n2, final int n3, final int n4) {
        this.col[0] = n;
        this.col[1] = n2;
        this.col[2] = n3;
        this.col[3] = n4;
    }
    
    public void setAlpha4(final float n) {
        final int n2 = (int)(n * 255.0f) & 0xFF;
        this.col[0] = ((this.col[0] & 0xFFFFFF) | n2 << 24);
        this.col[1] = ((this.col[1] & 0xFFFFFF) | n2 << 24);
        this.col[2] = ((this.col[2] & 0xFFFFFF) | n2 << 24);
        this.col[3] = ((this.col[3] & 0xFFFFFF) | n2 << 24);
    }
    
    public void setShore(final boolean isShore) {
        this.isShore = isShore;
    }
    
    public void setWaterDepth(final float n, final float n2, final float n3, final float n4) {
        this.waterDepth[0] = n;
        this.waterDepth[1] = n2;
        this.waterDepth[2] = n3;
        this.waterDepth[3] = n4;
    }
    
    public void setTintColor(final int colTint) {
        this.colTint = colTint;
    }
    
    @Override
    public void accept(final TextureDraw textureDraw) {
        if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Floor.Lighting.getValue()) {
            textureDraw.col0 = Color.blendBGR(textureDraw.col0, this.col[0]);
            textureDraw.col1 = Color.blendBGR(textureDraw.col1, this.col[1]);
            textureDraw.col2 = Color.blendBGR(textureDraw.col2, this.col[2]);
            textureDraw.col3 = Color.blendBGR(textureDraw.col3, this.col[3]);
        }
        if (this.isShore && DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.ShoreFade.getValue()) {
            textureDraw.col0 = Color.setAlphaChannelToABGR(textureDraw.col0, 1.0f - this.waterDepth[0]);
            textureDraw.col1 = Color.setAlphaChannelToABGR(textureDraw.col1, 1.0f - this.waterDepth[1]);
            textureDraw.col2 = Color.setAlphaChannelToABGR(textureDraw.col2, 1.0f - this.waterDepth[2]);
            textureDraw.col3 = Color.setAlphaChannelToABGR(textureDraw.col3, 1.0f - this.waterDepth[3]);
        }
        if (this.colTint != 0) {
            textureDraw.col0 = Color.tintABGR(textureDraw.col0, this.colTint);
            textureDraw.col1 = Color.tintABGR(textureDraw.col1, this.colTint);
            textureDraw.col2 = Color.tintABGR(textureDraw.col2, this.colTint);
            textureDraw.col3 = Color.tintABGR(textureDraw.col3, this.colTint);
        }
        SpritePadding.applyIsoPadding(textureDraw, this.getIsoPaddingSettings());
    }
    
    private SpritePadding.IsoPaddingSettings getIsoPaddingSettings() {
        return SpritePaddingSettings.getSettings().IsoPadding;
    }
}

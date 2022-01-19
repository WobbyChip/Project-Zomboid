// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.sprite.shapers;

import javax.xml.bind.annotation.XmlType;
import zombie.core.Color;
import zombie.debug.DebugOptions;
import zombie.core.textures.TextureDraw;

public class FloorShaperDeDiamond extends FloorShaper
{
    public static final FloorShaperDeDiamond instance;
    
    @Override
    public void accept(final TextureDraw textureDraw) {
        final int colTint = this.colTint;
        this.colTint = 0;
        super.accept(textureDraw);
        this.applyDeDiamondPadding(textureDraw);
        if (!DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Floor.Lighting.getValue()) {
            return;
        }
        final int n = this.col[0];
        final int n2 = this.col[1];
        final int n3 = this.col[2];
        final int n4 = this.col[3];
        final int lerpABGR = Color.lerpABGR(n, n4, 0.5f);
        final int lerpABGR2 = Color.lerpABGR(n2, n, 0.5f);
        final int lerpABGR3 = Color.lerpABGR(n3, n2, 0.5f);
        final int lerpABGR4 = Color.lerpABGR(n4, n3, 0.5f);
        textureDraw.col0 = Color.blendBGR(textureDraw.col0, lerpABGR);
        textureDraw.col1 = Color.blendBGR(textureDraw.col1, lerpABGR2);
        textureDraw.col2 = Color.blendBGR(textureDraw.col2, lerpABGR3);
        textureDraw.col3 = Color.blendBGR(textureDraw.col3, lerpABGR4);
        if (colTint != 0) {
            textureDraw.col0 = Color.tintABGR(textureDraw.col0, colTint);
            textureDraw.col1 = Color.tintABGR(textureDraw.col1, colTint);
            textureDraw.col2 = Color.tintABGR(textureDraw.col2, colTint);
            textureDraw.col3 = Color.tintABGR(textureDraw.col3, colTint);
        }
    }
    
    private void applyDeDiamondPadding(final TextureDraw textureDraw) {
        if (!DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.IsoPaddingDeDiamond.getValue()) {
            return;
        }
        final Settings.BorderSetting currentZoomSetting = this.getSettings().getCurrentZoomSetting();
        final float borderThicknessUp = currentZoomSetting.borderThicknessUp;
        final float borderThicknessDown = currentZoomSetting.borderThicknessDown;
        final float borderThicknessLR = currentZoomSetting.borderThicknessLR;
        final float uvFraction = currentZoomSetting.uvFraction;
        final float n = textureDraw.x1 - textureDraw.x0;
        final float n2 = textureDraw.y2 - textureDraw.y1;
        final float n3 = textureDraw.u1 - textureDraw.u0;
        final float n4 = textureDraw.v2 - textureDraw.v1;
        final float n5 = borderThicknessLR;
        final float n6 = borderThicknessUp;
        final float n7 = borderThicknessDown;
        final float n8 = n3 * n5 / n;
        final float n9 = n4 * n6 / n2;
        final float n10 = n4 * n7 / n2;
        final float n11 = uvFraction * n8;
        SpritePadding.applyPadding(textureDraw, n5, n6, n5, n7, n11, uvFraction * n9, n11, uvFraction * n10);
    }
    
    private Settings getSettings() {
        return SpritePaddingSettings.getSettings().FloorDeDiamond;
    }
    
    static {
        instance = new FloorShaperDeDiamond();
    }
    
    @XmlType(name = "FloorShaperDeDiamondSettings")
    public static class Settings extends SpritePaddingSettings.GenericZoomBasedSettingGroup
    {
        public BorderSetting ZoomedIn;
        public BorderSetting NotZoomed;
        public BorderSetting ZoomedOut;
        
        public Settings() {
            this.ZoomedIn = new BorderSetting(2.0f, 1.0f, 2.0f, 0.01f);
            this.NotZoomed = new BorderSetting(2.0f, 1.0f, 2.0f, 0.01f);
            this.ZoomedOut = new BorderSetting(2.0f, 0.0f, 2.5f, 0.0f);
        }
        
        @Override
        public BorderSetting getCurrentZoomSetting() {
            return SpritePaddingSettings.GenericZoomBasedSettingGroup.getCurrentZoomSetting(this.ZoomedIn, this.NotZoomed, this.ZoomedOut);
        }
        
        public static class BorderSetting
        {
            public float borderThicknessUp;
            public float borderThicknessDown;
            public float borderThicknessLR;
            public float uvFraction;
            
            public BorderSetting() {
                this.borderThicknessUp = 3.0f;
                this.borderThicknessDown = 3.0f;
                this.borderThicknessLR = 0.0f;
                this.uvFraction = 0.01f;
            }
            
            public BorderSetting(final float borderThicknessUp, final float borderThicknessDown, final float borderThicknessLR, final float uvFraction) {
                this.borderThicknessUp = 3.0f;
                this.borderThicknessDown = 3.0f;
                this.borderThicknessLR = 0.0f;
                this.uvFraction = 0.01f;
                this.borderThicknessUp = borderThicknessUp;
                this.borderThicknessDown = borderThicknessDown;
                this.borderThicknessLR = borderThicknessLR;
                this.uvFraction = uvFraction;
            }
        }
    }
}

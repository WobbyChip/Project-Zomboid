// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.sprite.shapers;

import zombie.debug.DebugOptions;
import zombie.core.textures.TextureDraw;

public class SpritePadding
{
    public static void applyPadding(final TextureDraw textureDraw, final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8) {
        final float x0 = textureDraw.x0;
        final float y0 = textureDraw.y0;
        final float x2 = textureDraw.x1;
        final float y2 = textureDraw.y1;
        final float x3 = textureDraw.x2;
        final float y3 = textureDraw.y2;
        final float x4 = textureDraw.x3;
        final float y4 = textureDraw.y3;
        final float u0 = textureDraw.u0;
        final float v0 = textureDraw.v0;
        final float u2 = textureDraw.u1;
        final float v2 = textureDraw.v1;
        final float u3 = textureDraw.u2;
        final float v3 = textureDraw.v2;
        final float u4 = textureDraw.u3;
        final float v4 = textureDraw.v3;
        textureDraw.x0 = x0 - n;
        textureDraw.y0 = y0 - n2;
        textureDraw.u0 = u0 - n5;
        textureDraw.v0 = v0 - n6;
        textureDraw.x1 = x2 + n3;
        textureDraw.y1 = y2 - n2;
        textureDraw.u1 = u2 + n7;
        textureDraw.v1 = v2 - n6;
        textureDraw.x2 = x3 + n3;
        textureDraw.y2 = y3 + n4;
        textureDraw.u2 = u3 + n7;
        textureDraw.v2 = v3 + n8;
        textureDraw.x3 = x4 - n;
        textureDraw.y3 = y4 + n4;
        textureDraw.u3 = u4 - n5;
        textureDraw.v3 = v4 + n8;
    }
    
    public static void applyPaddingBorder(final TextureDraw textureDraw, final float n, final float n2) {
        final float n3 = textureDraw.x1 - textureDraw.x0;
        final float n4 = textureDraw.y2 - textureDraw.y1;
        final float n5 = textureDraw.u1 - textureDraw.u0;
        final float n6 = textureDraw.v2 - textureDraw.v1;
        final float n7 = n5 * n / n3;
        final float n8 = n6 * n / n4;
        final float n9 = n2 * n7;
        final float n10 = n2 * n8;
        applyPadding(textureDraw, n, n, n, n, n9, n10, n9, n10);
    }
    
    public static void applyIsoPadding(final TextureDraw textureDraw, final IsoPaddingSettings isoPaddingSettings) {
        if (!DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.IsoPadding.getValue()) {
            return;
        }
        final IsoPaddingSettings.IsoBorderSetting currentZoomSetting = isoPaddingSettings.getCurrentZoomSetting();
        applyPaddingBorder(textureDraw, currentZoomSetting.borderThickness, currentZoomSetting.uvFraction);
    }
    
    public static class IsoPaddingSettings extends SpritePaddingSettings.GenericZoomBasedSettingGroup
    {
        public IsoBorderSetting ZoomedIn;
        public IsoBorderSetting NotZoomed;
        public IsoBorderSetting ZoomedOut;
        
        public IsoPaddingSettings() {
            this.ZoomedIn = new IsoBorderSetting(1.0f, 0.99f);
            this.NotZoomed = new IsoBorderSetting(1.0f, 0.99f);
            this.ZoomedOut = new IsoBorderSetting(2.0f, 0.01f);
        }
        
        @Override
        public IsoBorderSetting getCurrentZoomSetting() {
            return SpritePaddingSettings.GenericZoomBasedSettingGroup.getCurrentZoomSetting(this.ZoomedIn, this.NotZoomed, this.ZoomedOut);
        }
        
        public static class IsoBorderSetting
        {
            public float borderThickness;
            public float uvFraction;
            
            public IsoBorderSetting() {
            }
            
            public IsoBorderSetting(final float borderThickness, final float uvFraction) {
                this.borderThickness = borderThickness;
                this.uvFraction = uvFraction;
            }
        }
    }
}

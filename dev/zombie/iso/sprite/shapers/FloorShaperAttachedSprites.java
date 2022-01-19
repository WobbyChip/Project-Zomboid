// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.sprite.shapers;

import javax.xml.bind.annotation.XmlType;
import zombie.debug.DebugOptions;
import zombie.core.textures.TextureDraw;

public class FloorShaperAttachedSprites extends FloorShaper
{
    public static final FloorShaperAttachedSprites instance;
    
    @Override
    public void accept(final TextureDraw textureDraw) {
        super.accept(textureDraw);
        this.applyAttachedSpritesPadding(textureDraw);
    }
    
    private void applyAttachedSpritesPadding(final TextureDraw textureDraw) {
        if (!DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.IsoPaddingAttached.getValue()) {
            return;
        }
        final Settings.ASBorderSetting currentZoomSetting = this.getSettings().getCurrentZoomSetting();
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
        return SpritePaddingSettings.getSettings().AttachedSprites;
    }
    
    static {
        instance = new FloorShaperAttachedSprites();
    }
    
    @XmlType(name = "FloorShaperAttachedSpritesSettings")
    public static class Settings extends SpritePaddingSettings.GenericZoomBasedSettingGroup
    {
        public ASBorderSetting ZoomedIn;
        public ASBorderSetting NotZoomed;
        public ASBorderSetting ZoomedOut;
        
        public Settings() {
            this.ZoomedIn = new ASBorderSetting(2.0f, 1.0f, 3.0f, 0.01f);
            this.NotZoomed = new ASBorderSetting(2.0f, 1.0f, 3.0f, 0.01f);
            this.ZoomedOut = new ASBorderSetting(2.0f, 0.0f, 2.5f, 0.0f);
        }
        
        @Override
        public ASBorderSetting getCurrentZoomSetting() {
            return SpritePaddingSettings.GenericZoomBasedSettingGroup.getCurrentZoomSetting(this.ZoomedIn, this.NotZoomed, this.ZoomedOut);
        }
        
        public static class ASBorderSetting
        {
            public float borderThicknessUp;
            public float borderThicknessDown;
            public float borderThicknessLR;
            public float uvFraction;
            
            public ASBorderSetting() {
            }
            
            public ASBorderSetting(final float borderThicknessUp, final float borderThicknessDown, final float borderThicknessLR, final float uvFraction) {
                this.borderThicknessUp = borderThicknessUp;
                this.borderThicknessDown = borderThicknessDown;
                this.borderThicknessLR = borderThicknessLR;
                this.uvFraction = uvFraction;
            }
        }
    }
}

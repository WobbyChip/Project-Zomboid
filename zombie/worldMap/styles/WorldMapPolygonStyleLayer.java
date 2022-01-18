// 
// Decompiled by Procyon v0.5.36
// 

package zombie.worldMap.styles;

import zombie.core.textures.Texture;
import zombie.worldMap.WorldMapFeature;
import java.util.ArrayList;

public class WorldMapPolygonStyleLayer extends WorldMapStyleLayer
{
    public final ArrayList<ColorStop> m_fill;
    public final ArrayList<TextureStop> m_texture;
    public final ArrayList<FloatStop> m_scale;
    
    public WorldMapPolygonStyleLayer(final String s) {
        super(s);
        this.m_fill = new ArrayList<ColorStop>();
        this.m_texture = new ArrayList<TextureStop>();
        this.m_scale = new ArrayList<FloatStop>();
    }
    
    @Override
    public String getTypeString() {
        return "Polygon";
    }
    
    @Override
    public void render(final WorldMapFeature worldMapFeature, final RenderArgs renderArgs) {
        final RGBAf evalColor = this.evalColor(renderArgs, this.m_fill);
        if (evalColor.a < 0.01f) {
            RGBAf.s_pool.release(evalColor);
            return;
        }
        final float evalFloat = this.evalFloat(renderArgs, this.m_scale);
        final Texture evalTexture = this.evalTexture(renderArgs, this.m_texture);
        if (evalTexture == null || !evalTexture.isReady()) {
            renderArgs.drawer.fillPolygon(renderArgs, worldMapFeature, evalColor);
        }
        else {
            renderArgs.drawer.fillPolygon(renderArgs, worldMapFeature, evalColor, evalTexture, evalFloat);
        }
        RGBAf.s_pool.release(evalColor);
    }
}

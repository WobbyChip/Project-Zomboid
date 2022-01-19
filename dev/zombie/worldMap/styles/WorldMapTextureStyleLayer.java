// 
// Decompiled by Procyon v0.5.36
// 

package zombie.worldMap.styles;

import zombie.core.textures.Texture;
import zombie.worldMap.WorldMapFeature;
import java.util.ArrayList;

public class WorldMapTextureStyleLayer extends WorldMapStyleLayer
{
    public int m_worldX1;
    public int m_worldY1;
    public int m_worldX2;
    public int m_worldY2;
    public boolean m_useWorldBounds;
    public final ArrayList<ColorStop> m_fill;
    public final ArrayList<TextureStop> m_texture;
    public boolean m_tile;
    
    public WorldMapTextureStyleLayer(final String s) {
        super(s);
        this.m_useWorldBounds = false;
        this.m_fill = new ArrayList<ColorStop>();
        this.m_texture = new ArrayList<TextureStop>();
        this.m_tile = false;
    }
    
    @Override
    public String getTypeString() {
        return "Texture";
    }
    
    @Override
    public boolean filter(final WorldMapFeature worldMapFeature, final FilterArgs filterArgs) {
        return false;
    }
    
    @Override
    public void render(final WorldMapFeature worldMapFeature, final RenderArgs renderArgs) {
    }
    
    @Override
    public void renderCell(final RenderArgs renderArgs) {
        if (this.m_useWorldBounds) {
            this.m_worldX1 = renderArgs.renderer.getWorldMap().getMinXInSquares();
            this.m_worldY1 = renderArgs.renderer.getWorldMap().getMinYInSquares();
            this.m_worldX2 = renderArgs.renderer.getWorldMap().getMaxXInSquares() + 1;
            this.m_worldY2 = renderArgs.renderer.getWorldMap().getMaxYInSquares() + 1;
        }
        final RGBAf evalColor = this.evalColor(renderArgs, this.m_fill);
        if (evalColor.a < 0.01f) {
            RGBAf.s_pool.release(evalColor);
            return;
        }
        final Texture evalTexture = this.evalTexture(renderArgs, this.m_texture);
        if (evalTexture == null) {
            RGBAf.s_pool.release(evalColor);
            return;
        }
        if (this.m_tile) {
            renderArgs.drawer.drawTextureTiled(evalTexture, evalColor, this.m_worldX1, this.m_worldY1, this.m_worldX2, this.m_worldY2, renderArgs.cellX, renderArgs.cellY);
        }
        else {
            renderArgs.drawer.drawTexture(evalTexture, evalColor, this.m_worldX1, this.m_worldY1, this.m_worldX2, this.m_worldY2);
        }
        RGBAf.s_pool.release(evalColor);
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.worldMap.symbols;

import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;
import zombie.core.SpriteRenderer;
import java.io.IOException;
import zombie.GameWindow;
import java.nio.ByteBuffer;
import zombie.worldMap.UIWorldMap;
import zombie.core.textures.Texture;

public final class WorldMapTextureSymbol extends WorldMapBaseSymbol
{
    private String m_symbolID;
    Texture m_texture;
    
    public WorldMapTextureSymbol(final WorldMapSymbols worldMapSymbols) {
        super(worldMapSymbols);
    }
    
    public void setSymbolID(final String symbolID) {
        this.m_symbolID = symbolID;
    }
    
    public String getSymbolID() {
        return this.m_symbolID;
    }
    
    public void checkTexture() {
        if (this.m_texture != null) {
            return;
        }
        final MapSymbolDefinitions.MapSymbolDefinition symbolById = MapSymbolDefinitions.getInstance().getSymbolById(this.getSymbolID());
        if (symbolById == null) {
            this.m_width = 18.0f;
            this.m_height = 18.0f;
        }
        else {
            this.m_texture = Texture.getSharedTexture(symbolById.getTexturePath());
            this.m_width = (float)symbolById.getWidth();
            this.m_height = (float)symbolById.getHeight();
        }
        if (this.m_texture == null) {
            this.m_texture = Texture.getErrorTexture();
        }
    }
    
    @Override
    public WorldMapSymbols.WorldMapSymbolType getType() {
        return WorldMapSymbols.WorldMapSymbolType.Texture;
    }
    
    @Override
    public void layout(final UIWorldMap uiWorldMap, final WorldMapSymbolCollisions worldMapSymbolCollisions, final float n, final float n2) {
        this.checkTexture();
        super.layout(uiWorldMap, worldMapSymbolCollisions, n, n2);
    }
    
    @Override
    public void save(final ByteBuffer byteBuffer) throws IOException {
        super.save(byteBuffer);
        GameWindow.WriteString(byteBuffer, this.m_symbolID);
    }
    
    @Override
    public void load(final ByteBuffer byteBuffer, final int n, final int n2) throws IOException {
        super.load(byteBuffer, n, n2);
        this.m_symbolID = GameWindow.ReadString(byteBuffer);
    }
    
    @Override
    public void render(final UIWorldMap uiWorldMap, final float n, final float n2) {
        if (this.m_collided) {
            this.renderCollided(uiWorldMap, n, n2);
        }
        else {
            this.checkTexture();
            final float n3 = n + this.m_layoutX;
            final float n4 = n2 + this.m_layoutY;
            if (this.m_scale > 0.0f) {
                final float displayScale = this.getDisplayScale(uiWorldMap);
                SpriteRenderer.instance.m_states.getPopulatingActiveState().render(this.m_texture, uiWorldMap.getAbsoluteX().intValue() + n3, uiWorldMap.getAbsoluteY().intValue() + n4, this.m_texture.getWidth() * displayScale, this.m_texture.getHeight() * displayScale, this.m_r, this.m_g, this.m_b, this.m_a, null);
            }
            else {
                uiWorldMap.DrawTextureColor(this.m_texture, n3, n4, this.m_r, this.m_g, this.m_b, this.m_a);
            }
        }
    }
    
    @Override
    public void release() {
        this.m_symbolID = null;
        this.m_texture = null;
    }
}

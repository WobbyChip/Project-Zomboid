// 
// Decompiled by Procyon v0.5.36
// 

package zombie.worldMap.symbols;

import zombie.worldMap.UIWorldMap;
import java.io.IOException;
import zombie.GameWindow;
import java.nio.ByteBuffer;
import zombie.core.Translator;
import zombie.ui.TextManager;
import zombie.network.GameServer;
import zombie.ui.UIFont;

public final class WorldMapTextSymbol extends WorldMapBaseSymbol
{
    String m_text;
    boolean m_translated;
    UIFont m_font;
    
    public WorldMapTextSymbol(final WorldMapSymbols worldMapSymbols) {
        super(worldMapSymbols);
        this.m_translated = false;
        this.m_font = UIFont.Handwritten;
    }
    
    public void setTranslatedText(final String text) {
        this.m_text = text;
        this.m_translated = true;
        if (GameServer.bServer) {
            return;
        }
        this.m_width = (float)TextManager.instance.MeasureStringX(this.m_font, this.getTranslatedText());
        this.m_height = (float)TextManager.instance.getFontHeight(this.m_font);
    }
    
    public void setUntranslatedText(final String text) {
        this.m_text = text;
        this.m_translated = false;
        if (GameServer.bServer) {
            return;
        }
        this.m_width = (float)TextManager.instance.MeasureStringX(this.m_font, this.getTranslatedText());
        this.m_height = (float)TextManager.instance.getFontHeight(this.m_font);
    }
    
    public String getTranslatedText() {
        return this.m_translated ? this.m_text : Translator.getText(this.m_text);
    }
    
    public String getUntranslatedText() {
        return this.m_translated ? null : this.m_text;
    }
    
    @Override
    public WorldMapSymbols.WorldMapSymbolType getType() {
        return WorldMapSymbols.WorldMapSymbolType.Text;
    }
    
    @Override
    public boolean isVisible() {
        return !this.m_owner.getMiniMapSymbols() && super.isVisible();
    }
    
    @Override
    public void save(final ByteBuffer byteBuffer) throws IOException {
        super.save(byteBuffer);
        GameWindow.WriteString(byteBuffer, this.m_text);
        byteBuffer.put((byte)(this.m_translated ? 1 : 0));
    }
    
    @Override
    public void load(final ByteBuffer byteBuffer, final int n, final int n2) throws IOException {
        super.load(byteBuffer, n, n2);
        this.m_text = GameWindow.ReadString(byteBuffer);
        this.m_translated = (byteBuffer.get() == 1);
    }
    
    @Override
    public void render(final UIWorldMap uiWorldMap, final float n, final float n2) {
        if (this.m_width == 0.0f || this.m_height == 0.0f) {
            this.m_width = (float)TextManager.instance.MeasureStringX(this.m_font, this.getTranslatedText());
            this.m_height = (float)TextManager.instance.getFontHeight(this.m_font);
        }
        if (this.m_collided) {
            this.renderCollided(uiWorldMap, n, n2);
        }
        else {
            final float n3 = n + this.m_layoutX;
            final float n4 = n2 + this.m_layoutY;
            if (this.m_scale > 0.0f) {
                uiWorldMap.DrawText(this.m_font, this.getTranslatedText(), n3, n4, this.getDisplayScale(uiWorldMap), this.m_r, this.m_g, this.m_b, this.m_a);
            }
            else {
                uiWorldMap.DrawText(this.m_font, this.getTranslatedText(), n3, n4, this.m_r, this.m_g, this.m_b, this.m_a);
            }
        }
    }
    
    @Override
    public void release() {
        this.m_text = null;
    }
}

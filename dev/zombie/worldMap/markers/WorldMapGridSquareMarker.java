// 
// Decompiled by Procyon v0.5.36
// 

package zombie.worldMap.markers;

import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;
import zombie.core.SpriteRenderer;
import zombie.core.Core;
import zombie.core.math.PZMath;
import zombie.worldMap.UIWorldMap;
import zombie.core.textures.Texture;

public final class WorldMapGridSquareMarker extends WorldMapMarker
{
    Texture m_texture1;
    Texture m_texture2;
    float m_r;
    float m_g;
    float m_b;
    float m_a;
    int m_worldX;
    int m_worldY;
    int m_radius;
    int m_minScreenRadius;
    boolean m_blink;
    
    public WorldMapGridSquareMarker() {
        this.m_texture1 = Texture.getSharedTexture("media/textures/worldMap/circle_center.png");
        this.m_texture2 = Texture.getSharedTexture("media/textures/worldMap/circle_only_highlight.png");
        this.m_r = 1.0f;
        this.m_g = 1.0f;
        this.m_b = 1.0f;
        this.m_a = 1.0f;
        this.m_radius = 10;
        this.m_minScreenRadius = 64;
        this.m_blink = true;
    }
    
    WorldMapGridSquareMarker init(final int worldX, final int worldY, final int radius, final float r, final float g, final float b, final float a) {
        this.m_worldX = worldX;
        this.m_worldY = worldY;
        this.m_radius = radius;
        this.m_r = r;
        this.m_g = g;
        this.m_b = b;
        this.m_a = a;
        return this;
    }
    
    public void setBlink(final boolean blink) {
        this.m_blink = blink;
    }
    
    public void setMinScreenRadius(final int minScreenRadius) {
        this.m_minScreenRadius = minScreenRadius;
    }
    
    @Override
    void render(final UIWorldMap uiWorldMap) {
        final float max = PZMath.max((float)this.m_radius, this.m_minScreenRadius / uiWorldMap.getAPI().getWorldScale());
        final float worldToUIX = uiWorldMap.getAPI().worldToUIX(this.m_worldX - max, this.m_worldY - max);
        final float worldToUIY = uiWorldMap.getAPI().worldToUIY(this.m_worldX - max, this.m_worldY - max);
        final float worldToUIX2 = uiWorldMap.getAPI().worldToUIX(this.m_worldX + max, this.m_worldY - max);
        final float worldToUIY2 = uiWorldMap.getAPI().worldToUIY(this.m_worldX + max, this.m_worldY - max);
        final float worldToUIX3 = uiWorldMap.getAPI().worldToUIX(this.m_worldX + max, this.m_worldY + max);
        final float worldToUIY3 = uiWorldMap.getAPI().worldToUIY(this.m_worldX + max, this.m_worldY + max);
        final float worldToUIX4 = uiWorldMap.getAPI().worldToUIX(this.m_worldX - max, this.m_worldY + max);
        final float worldToUIY4 = uiWorldMap.getAPI().worldToUIY(this.m_worldX - max, this.m_worldY + max);
        final float n = (float)(worldToUIX + uiWorldMap.getAbsoluteX());
        final float n2 = (float)(worldToUIY + uiWorldMap.getAbsoluteY());
        final float n3 = (float)(worldToUIX2 + uiWorldMap.getAbsoluteX());
        final float n4 = (float)(worldToUIY2 + uiWorldMap.getAbsoluteY());
        final float n5 = (float)(worldToUIX3 + uiWorldMap.getAbsoluteX());
        final float n6 = (float)(worldToUIY3 + uiWorldMap.getAbsoluteY());
        final float n7 = (float)(worldToUIX4 + uiWorldMap.getAbsoluteX());
        final float n8 = (float)(worldToUIY4 + uiWorldMap.getAbsoluteY());
        final float n9 = this.m_a * (this.m_blink ? Core.blinkAlpha : 1.0f);
        if (this.m_texture1 != null && this.m_texture1.isReady()) {
            SpriteRenderer.instance.render(this.m_texture1, n, n2, n3, n4, n5, n6, n7, n8, this.m_r, this.m_g, this.m_b, n9, null);
        }
        if (this.m_texture2 != null && this.m_texture2.isReady()) {
            SpriteRenderer.instance.render(this.m_texture2, n, n2, n3, n4, n5, n6, n7, n8, this.m_r, this.m_g, this.m_b, n9, null);
        }
    }
}

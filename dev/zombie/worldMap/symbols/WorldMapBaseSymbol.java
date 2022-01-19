// 
// Decompiled by Procyon v0.5.36
// 

package zombie.worldMap.symbols;

import zombie.core.textures.Texture;
import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.worldMap.UIWorldMap;
import zombie.core.math.PZMath;

public abstract class WorldMapBaseSymbol
{
    public static float DEFAULT_SCALE;
    WorldMapSymbols m_owner;
    float m_x;
    float m_y;
    float m_width;
    float m_height;
    float m_anchorX;
    float m_anchorY;
    float m_scale;
    float m_r;
    float m_g;
    float m_b;
    float m_a;
    boolean m_collide;
    boolean m_collided;
    float m_layoutX;
    float m_layoutY;
    boolean m_visible;
    
    public WorldMapBaseSymbol(final WorldMapSymbols owner) {
        this.m_anchorX = 0.0f;
        this.m_anchorY = 0.0f;
        this.m_scale = WorldMapBaseSymbol.DEFAULT_SCALE;
        this.m_collide = false;
        this.m_collided = false;
        this.m_visible = true;
        this.m_owner = owner;
    }
    
    public abstract WorldMapSymbols.WorldMapSymbolType getType();
    
    public void setAnchor(final float n, final float n2) {
        this.m_anchorX = PZMath.clamp(n, 0.0f, 1.0f);
        this.m_anchorY = PZMath.clamp(n2, 0.0f, 1.0f);
    }
    
    public void setPosition(final float x, final float y) {
        this.m_x = x;
        this.m_y = y;
    }
    
    public void setCollide(final boolean collide) {
        this.m_collide = collide;
    }
    
    public void setRGBA(final float n, final float n2, final float n3, final float n4) {
        this.m_r = PZMath.clamp_01(n);
        this.m_g = PZMath.clamp_01(n2);
        this.m_b = PZMath.clamp_01(n3);
        this.m_a = PZMath.clamp_01(n4);
    }
    
    public void setScale(final float scale) {
        this.m_scale = scale;
    }
    
    public float getDisplayScale(final UIWorldMap uiWorldMap) {
        if (this.m_scale <= 0.0f) {
            return this.m_scale;
        }
        if (this.m_owner.getMiniMapSymbols()) {
            return PZMath.min(this.m_owner.getLayoutWorldScale(), 1.0f);
        }
        return this.m_owner.getLayoutWorldScale() * this.m_scale;
    }
    
    public void layout(final UIWorldMap uiWorldMap, final WorldMapSymbolCollisions worldMapSymbolCollisions, final float n, final float n2) {
        final float n3 = uiWorldMap.getAPI().worldToUIX(this.m_x, this.m_y) - n;
        final float n4 = uiWorldMap.getAPI().worldToUIY(this.m_x, this.m_y) - n2;
        this.m_layoutX = n3 - this.widthScaled(uiWorldMap) * this.m_anchorX;
        this.m_layoutY = n4 - this.heightScaled(uiWorldMap) * this.m_anchorY;
        this.m_collided = worldMapSymbolCollisions.addBox(this.m_layoutX, this.m_layoutY, this.widthScaled(uiWorldMap), this.heightScaled(uiWorldMap), this.m_collide);
        if (this.m_collided) {}
    }
    
    public float widthScaled(final UIWorldMap uiWorldMap) {
        return (this.m_scale <= 0.0f) ? this.m_width : (this.m_width * this.getDisplayScale(uiWorldMap));
    }
    
    public float heightScaled(final UIWorldMap uiWorldMap) {
        return (this.m_scale <= 0.0f) ? this.m_height : (this.m_height * this.getDisplayScale(uiWorldMap));
    }
    
    public void setVisible(final boolean visible) {
        this.m_visible = visible;
    }
    
    public boolean isVisible() {
        return this.m_visible;
    }
    
    public void save(final ByteBuffer byteBuffer) throws IOException {
        byteBuffer.putFloat(this.m_x);
        byteBuffer.putFloat(this.m_y);
        byteBuffer.putFloat(this.m_anchorX);
        byteBuffer.putFloat(this.m_anchorY);
        byteBuffer.putFloat(this.m_scale);
        byteBuffer.put((byte)(this.m_r * 255.0f));
        byteBuffer.put((byte)(this.m_g * 255.0f));
        byteBuffer.put((byte)(this.m_b * 255.0f));
        byteBuffer.put((byte)(this.m_a * 255.0f));
        byteBuffer.put((byte)(this.m_collide ? 1 : 0));
    }
    
    public void load(final ByteBuffer byteBuffer, final int n, final int n2) throws IOException {
        this.m_x = byteBuffer.getFloat();
        this.m_y = byteBuffer.getFloat();
        this.m_anchorX = byteBuffer.getFloat();
        this.m_anchorY = byteBuffer.getFloat();
        this.m_scale = byteBuffer.getFloat();
        this.m_r = (byteBuffer.get() & 0xFF) / 255.0f;
        this.m_g = (byteBuffer.get() & 0xFF) / 255.0f;
        this.m_b = (byteBuffer.get() & 0xFF) / 255.0f;
        this.m_a = (byteBuffer.get() & 0xFF) / 255.0f;
        this.m_collide = (byteBuffer.get() == 1);
    }
    
    public abstract void render(final UIWorldMap p0, final float p1, final float p2);
    
    void renderCollided(final UIWorldMap uiWorldMap, final float n, final float n2) {
        uiWorldMap.DrawTextureScaledCol(null, n + this.m_layoutX + this.widthScaled(uiWorldMap) / 2.0f - 3.0f, n2 + this.m_layoutY + this.heightScaled(uiWorldMap) / 2.0f - 3.0f, 6.0, 6.0, this.m_r, this.m_g, this.m_b, this.m_a);
    }
    
    public abstract void release();
    
    static {
        WorldMapBaseSymbol.DEFAULT_SCALE = 0.666f;
    }
}

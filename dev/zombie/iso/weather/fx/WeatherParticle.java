// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.weather.fx;

import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;
import zombie.core.SpriteRenderer;
import zombie.iso.Vector2;
import zombie.core.Color;
import zombie.core.textures.Texture;
import org.lwjgl.util.Rectangle;

public abstract class WeatherParticle
{
    protected ParticleRectangle parent;
    protected Rectangle bounds;
    protected Texture texture;
    protected Color color;
    protected Vector2 position;
    protected Vector2 velocity;
    protected float alpha;
    protected float speed;
    protected SteppedUpdateFloat alphaFadeMod;
    protected float renderAlpha;
    protected float oWidth;
    protected float oHeight;
    protected float zoomMultiW;
    protected float zoomMultiH;
    protected boolean recalcSizeOnZoom;
    protected float lastZoomMod;
    
    public WeatherParticle(final Texture texture) {
        this.color = Color.white;
        this.position = new Vector2(0.0f, 0.0f);
        this.velocity = new Vector2(0.0f, 0.0f);
        this.alpha = 1.0f;
        this.speed = 0.0f;
        this.alphaFadeMod = new SteppedUpdateFloat(0.0f, 0.1f, 0.0f, 1.0f);
        this.renderAlpha = 0.0f;
        this.zoomMultiW = 0.0f;
        this.zoomMultiH = 0.0f;
        this.recalcSizeOnZoom = false;
        this.lastZoomMod = -1.0f;
        this.texture = texture;
        this.bounds = new Rectangle(0, 0, texture.getWidth(), texture.getHeight());
        this.oWidth = (float)this.bounds.getWidth();
        this.oHeight = (float)this.bounds.getHeight();
    }
    
    public WeatherParticle(final Texture texture, final int n, final int n2) {
        this.color = Color.white;
        this.position = new Vector2(0.0f, 0.0f);
        this.velocity = new Vector2(0.0f, 0.0f);
        this.alpha = 1.0f;
        this.speed = 0.0f;
        this.alphaFadeMod = new SteppedUpdateFloat(0.0f, 0.1f, 0.0f, 1.0f);
        this.renderAlpha = 0.0f;
        this.zoomMultiW = 0.0f;
        this.zoomMultiH = 0.0f;
        this.recalcSizeOnZoom = false;
        this.lastZoomMod = -1.0f;
        this.texture = texture;
        this.bounds = new Rectangle(0, 0, n, n2);
        this.oWidth = (float)this.bounds.getWidth();
        this.oHeight = (float)this.bounds.getHeight();
    }
    
    protected void setParent(final ParticleRectangle parent) {
        this.parent = parent;
    }
    
    public void update(final float n) {
        this.update(n, true);
    }
    
    public void update(final float n, final boolean b) {
        this.alphaFadeMod.update(n);
        if (this.position.x > this.parent.getWidth()) {
            final Vector2 position = this.position;
            position.x -= (int)(this.position.x / this.parent.getWidth()) * this.parent.getWidth();
        }
        else if (this.position.x < 0.0f) {
            final Vector2 position2 = this.position;
            position2.x -= (int)((this.position.x - this.parent.getWidth()) / this.parent.getWidth()) * this.parent.getWidth();
        }
        if (this.position.y > this.parent.getHeight()) {
            final Vector2 position3 = this.position;
            position3.y -= (int)(this.position.y / this.parent.getHeight()) * this.parent.getHeight();
        }
        else if (this.position.y < 0.0f) {
            final Vector2 position4 = this.position;
            position4.y -= (int)((this.position.y - this.parent.getHeight()) / this.parent.getHeight()) * this.parent.getHeight();
        }
        if (b) {
            this.bounds.setLocation((int)this.position.x - this.bounds.getWidth() / 2, (int)this.position.y - this.bounds.getHeight() / 2);
        }
    }
    
    protected boolean updateZoomSize() {
        if (this.recalcSizeOnZoom && this.lastZoomMod != IsoWeatherFX.ZoomMod) {
            this.lastZoomMod = IsoWeatherFX.ZoomMod;
            this.oWidth = (float)this.bounds.getWidth();
            this.oHeight = (float)this.bounds.getHeight();
            if (this.lastZoomMod > 0.0f) {
                this.oWidth *= 1.0f + IsoWeatherFX.ZoomMod * this.zoomMultiW;
                this.oHeight *= 1.0f + IsoWeatherFX.ZoomMod * this.zoomMultiH;
            }
            return true;
        }
        return false;
    }
    
    public void render(final float n, final float n2) {
        SpriteRenderer.instance.render(this.texture, n + this.bounds.getX(), n2 + this.bounds.getY(), this.oWidth, this.oHeight, this.color.r, this.color.g, this.color.b, this.renderAlpha, null);
    }
}

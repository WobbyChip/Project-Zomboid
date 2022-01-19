// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.weather.fx;

import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;
import zombie.core.SpriteRenderer;
import zombie.iso.Vector2;
import zombie.core.Rand;
import zombie.core.textures.Texture;

public class RainParticle extends WeatherParticle
{
    private double angleRadians;
    private float lastAngle;
    private float lastIntensity;
    protected float angleOffset;
    private float alphaMod;
    private float incarnateAlpha;
    private float life;
    private RenderPoints rp;
    private boolean angleUpdate;
    private float tmpAngle;
    
    public RainParticle(final Texture texture, final int n) {
        super(texture);
        this.angleRadians = 0.0;
        this.lastAngle = -1.0f;
        this.lastIntensity = -1.0f;
        this.angleOffset = 0.0f;
        this.alphaMod = 0.0f;
        this.incarnateAlpha = 1.0f;
        this.life = 0.0f;
        this.angleUpdate = false;
        this.tmpAngle = 0.0f;
        if (n > 6) {
            this.bounds.setSize(Rand.Next(1, 2), n);
        }
        else {
            this.bounds.setSize(1, n);
        }
        this.oWidth = (float)this.bounds.getWidth();
        this.oHeight = (float)this.bounds.getHeight();
        this.recalcSizeOnZoom = true;
        this.zoomMultiW = 0.0f;
        this.zoomMultiH = 2.0f;
        this.setLife();
        (this.rp = new RenderPoints()).setDimensions(this.oWidth, this.oHeight);
    }
    
    protected void setLife() {
        this.life = (float)Rand.Next(20, 60);
    }
    
    @Override
    public void update(final float n) {
        this.angleUpdate = false;
        if (this.updateZoomSize()) {
            this.rp.setDimensions(this.oWidth, this.oHeight);
            this.angleUpdate = true;
        }
        if (this.angleUpdate || this.lastAngle != IsoWeatherFX.instance.windAngle || this.lastIntensity != IsoWeatherFX.instance.windPrecipIntensity.value()) {
            this.tmpAngle = IsoWeatherFX.instance.windAngle + (this.angleOffset - this.angleOffset * 0.5f * IsoWeatherFX.instance.windPrecipIntensity.value());
            if (this.tmpAngle > 360.0f) {
                this.tmpAngle -= 360.0f;
            }
            if (this.tmpAngle < 0.0f) {
                this.tmpAngle += 360.0f;
            }
            this.angleRadians = Math.toRadians(this.tmpAngle);
            this.velocity.set((float)Math.cos(this.angleRadians) * this.speed, (float)Math.sin(this.angleRadians) * this.speed);
            this.lastAngle = IsoWeatherFX.instance.windAngle;
            this.lastIntensity = IsoWeatherFX.instance.windPrecipIntensity.value();
            this.angleUpdate = true;
        }
        final Vector2 position = this.position;
        position.x += this.velocity.x * (1.0f + IsoWeatherFX.instance.windSpeed * 0.1f) * n;
        final Vector2 position2 = this.position;
        position2.y += this.velocity.y * (1.0f + IsoWeatherFX.instance.windSpeed * 0.1f) * n;
        --this.life;
        if (this.life < 0.0f) {
            this.setLife();
            this.incarnateAlpha = 0.0f;
            this.position.set((float)Rand.Next(0, this.parent.getWidth()), (float)Rand.Next(0, this.parent.getHeight()));
        }
        if (this.incarnateAlpha < 1.0f) {
            this.incarnateAlpha += 0.035f;
            if (this.incarnateAlpha > 1.0f) {
                this.incarnateAlpha = 1.0f;
            }
        }
        super.update(n, false);
        this.bounds.setLocation((int)this.position.x, (int)this.position.y);
        if (this.angleUpdate) {
            this.tmpAngle += 90.0f;
            if (this.tmpAngle > 360.0f) {
                this.tmpAngle -= 360.0f;
            }
            if (this.tmpAngle < 0.0f) {
                this.tmpAngle += 360.0f;
            }
            this.angleRadians = Math.toRadians(this.tmpAngle);
            this.rp.rotate(this.angleRadians);
        }
        this.alphaMod = 1.0f - 0.2f * IsoWeatherFX.instance.windIntensity.value();
        this.renderAlpha = this.alpha * this.alphaMod * this.alphaFadeMod.value() * IsoWeatherFX.instance.indoorsAlphaMod.value() * this.incarnateAlpha;
        this.renderAlpha *= 0.55f;
        if (IsoWeatherFX.instance.playerIndoors) {
            this.renderAlpha *= 0.5f;
        }
    }
    
    @Override
    public void render(final float n, final float n2) {
        final double n3 = n + this.bounds.getX();
        final double n4 = n2 + this.bounds.getY();
        SpriteRenderer.instance.render(this.texture, n3 + this.rp.getX(0), n4 + this.rp.getY(0), n3 + this.rp.getX(1), n4 + this.rp.getY(1), n3 + this.rp.getX(2), n4 + this.rp.getY(2), n3 + this.rp.getX(3), n4 + this.rp.getY(3), this.color.r, this.color.g, this.color.b, this.renderAlpha, null);
    }
    
    private class RenderPoints
    {
        Point[] points;
        Point center;
        Point dim;
        
        public RenderPoints() {
            this.points = new Point[4];
            this.center = new Point();
            this.dim = new Point();
            for (int i = 0; i < this.points.length; ++i) {
                this.points[i] = new Point();
            }
        }
        
        public double getX(final int n) {
            return this.points[n].x;
        }
        
        public double getY(final int n) {
            return this.points[n].y;
        }
        
        public void setCenter(final float n, final float n2) {
            this.center.set(n, n2);
        }
        
        public void setDimensions(final float n, final float n2) {
            this.dim.set(n, n2);
            this.points[0].setOrig(-n / 2.0f, -n2 / 2.0f);
            this.points[1].setOrig(n / 2.0f, -n2 / 2.0f);
            this.points[2].setOrig(n / 2.0f, n2 / 2.0f);
            this.points[3].setOrig(-n / 2.0f, n2 / 2.0f);
        }
        
        public void rotate(final double n) {
            final double cos = Math.cos(n);
            final double sin = Math.sin(n);
            for (int i = 0; i < this.points.length; ++i) {
                this.points[i].x = this.points[i].origx * cos - this.points[i].origy * sin;
                this.points[i].y = this.points[i].origx * sin + this.points[i].origy * cos;
            }
        }
    }
    
    private class Point
    {
        private double origx;
        private double origy;
        private double x;
        private double y;
        
        public void setOrig(final double n, final double n2) {
            this.origx = n;
            this.origy = n2;
            this.x = n;
            this.y = n2;
        }
        
        public void set(final double x, final double y) {
            this.x = x;
            this.y = y;
        }
    }
}

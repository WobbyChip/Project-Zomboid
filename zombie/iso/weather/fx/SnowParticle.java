// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.weather.fx;

import zombie.iso.Vector2;
import zombie.core.Rand;
import zombie.core.textures.Texture;

public class SnowParticle extends WeatherParticle
{
    private double angleRadians;
    private float lastAngle;
    private float lastIntensity;
    protected float angleOffset;
    private float alphaMod;
    private float incarnateAlpha;
    private float life;
    private float fadeTime;
    private float tmpAngle;
    
    public SnowParticle(final Texture texture) {
        super(texture);
        this.angleRadians = 0.0;
        this.lastAngle = -1.0f;
        this.lastIntensity = -1.0f;
        this.angleOffset = 0.0f;
        this.alphaMod = 0.0f;
        this.incarnateAlpha = 1.0f;
        this.life = 0.0f;
        this.fadeTime = 80.0f;
        this.tmpAngle = 0.0f;
        this.recalcSizeOnZoom = true;
        this.zoomMultiW = 1.0f;
        this.zoomMultiH = 1.0f;
    }
    
    protected void setLife() {
        this.life = this.fadeTime + Rand.Next(60, 500);
    }
    
    @Override
    public void update(final float n) {
        if (this.lastAngle != IsoWeatherFX.instance.windAngle || this.lastIntensity != IsoWeatherFX.instance.windPrecipIntensity.value()) {
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
        }
        if (this.life >= this.fadeTime) {
            final Vector2 position = this.position;
            position.x += this.velocity.x * IsoWeatherFX.instance.windSpeed * n;
            final Vector2 position2 = this.position;
            position2.y += this.velocity.y * IsoWeatherFX.instance.windSpeed * n;
        }
        else {
            this.incarnateAlpha = this.life / this.fadeTime;
        }
        --this.life;
        if (this.life < 0.0f) {
            this.setLife();
            this.incarnateAlpha = 0.0f;
            this.position.set((float)Rand.Next(0, this.parent.getWidth()), (float)Rand.Next(0, this.parent.getHeight()));
        }
        if (this.incarnateAlpha < 1.0f) {
            this.incarnateAlpha += 0.05f;
            if (this.incarnateAlpha > 1.0f) {
                this.incarnateAlpha = 1.0f;
            }
        }
        super.update(n);
        this.updateZoomSize();
        this.alphaMod = 1.0f - 0.2f * IsoWeatherFX.instance.windIntensity.value();
        this.renderAlpha = this.alpha * this.alphaMod * this.alphaFadeMod.value() * IsoWeatherFX.instance.indoorsAlphaMod.value() * this.incarnateAlpha;
        this.renderAlpha *= 0.7f;
    }
    
    @Override
    public void render(final float n, final float n2) {
        super.render(n, n2);
    }
}

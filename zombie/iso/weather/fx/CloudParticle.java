// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.weather.fx;

import zombie.iso.Vector2;
import zombie.core.textures.Texture;

public class CloudParticle extends WeatherParticle
{
    private double angleRadians;
    private float lastAngle;
    private float lastIntensity;
    protected float angleOffset;
    private float alphaMod;
    private float tmpAngle;
    
    public CloudParticle(final Texture texture) {
        super(texture);
        this.angleRadians = 0.0;
        this.lastAngle = -1.0f;
        this.lastIntensity = -1.0f;
        this.angleOffset = 0.0f;
        this.alphaMod = 0.0f;
        this.tmpAngle = 0.0f;
    }
    
    public CloudParticle(final Texture texture, final int n, final int n2) {
        super(texture, n, n2);
        this.angleRadians = 0.0;
        this.lastAngle = -1.0f;
        this.lastIntensity = -1.0f;
        this.angleOffset = 0.0f;
        this.alphaMod = 0.0f;
        this.tmpAngle = 0.0f;
    }
    
    @Override
    public void update(final float n) {
        if (this.lastAngle != IsoWeatherFX.instance.windAngleClouds || this.lastIntensity != IsoWeatherFX.instance.windIntensity.value()) {
            this.tmpAngle = IsoWeatherFX.instance.windAngleClouds;
            if (this.tmpAngle > 360.0f) {
                this.tmpAngle -= 360.0f;
            }
            if (this.tmpAngle < 0.0f) {
                this.tmpAngle += 360.0f;
            }
            this.angleRadians = Math.toRadians(this.tmpAngle);
            this.velocity.set((float)Math.cos(this.angleRadians) * this.speed, (float)Math.sin(this.angleRadians) * this.speed);
            this.lastAngle = IsoWeatherFX.instance.windAngleClouds;
        }
        final Vector2 position = this.position;
        position.x += this.velocity.x * IsoWeatherFX.instance.windSpeedFog * n;
        final Vector2 position2 = this.position;
        position2.y += this.velocity.y * IsoWeatherFX.instance.windSpeedFog * n;
        super.update(n);
        this.alphaMod = IsoWeatherFX.instance.cloudIntensity.value() * 0.3f;
        this.renderAlpha = this.alpha * this.alphaMod * this.alphaFadeMod.value() * IsoWeatherFX.instance.indoorsAlphaMod.value();
    }
}

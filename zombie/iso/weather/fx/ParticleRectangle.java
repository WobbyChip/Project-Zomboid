// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.weather.fx;

import zombie.debug.LineDrawer;
import zombie.iso.IsoCamera;

public class ParticleRectangle
{
    protected boolean DEBUG_BOUNDS;
    private int width;
    private int height;
    private WeatherParticle[] particles;
    private int particlesToRender;
    private int particlesReqUpdCnt;
    
    public ParticleRectangle(final int width, final int height) {
        this.DEBUG_BOUNDS = false;
        this.particlesReqUpdCnt = 0;
        this.width = width;
        this.height = height;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public void SetParticles(final WeatherParticle[] particles) {
        for (int i = 0; i < particles.length; ++i) {
            particles[i].setParent(this);
        }
        this.particles = particles;
        this.particlesToRender = particles.length;
    }
    
    public void SetParticlesStrength(final float n) {
        this.particlesToRender = (int)(this.particles.length * n);
    }
    
    public boolean requiresUpdate() {
        return this.particlesToRender > 0 || this.particlesReqUpdCnt > 0;
    }
    
    public void update(final float n) {
        this.particlesReqUpdCnt = 0;
        for (int i = 0; i < this.particles.length; ++i) {
            final WeatherParticle weatherParticle = this.particles[i];
            if (i < this.particlesToRender) {
                weatherParticle.alphaFadeMod.setTarget(1.0f);
            }
            else if (i >= this.particlesToRender) {
                weatherParticle.alphaFadeMod.setTarget(0.0f);
            }
            weatherParticle.update(n);
            if (weatherParticle.renderAlpha > 0.0f) {
                ++this.particlesReqUpdCnt;
            }
        }
    }
    
    public void render() {
        final int playerIndex = IsoCamera.frameState.playerIndex;
        final int offscreenWidth = IsoCamera.frameState.OffscreenWidth;
        final int offscreenHeight = IsoCamera.frameState.OffscreenHeight;
        final int n = (int)Math.ceil(offscreenWidth / this.width) + 2;
        final int n2 = (int)Math.ceil(offscreenHeight / this.height) + 2;
        int n3;
        if (IsoCamera.frameState.OffX >= 0.0f) {
            n3 = (int)IsoCamera.frameState.OffX % this.width;
        }
        else {
            n3 = this.width - (int)Math.abs(IsoCamera.frameState.OffX) % this.width;
        }
        int n4;
        if (IsoCamera.frameState.OffY >= 0.0f) {
            n4 = (int)IsoCamera.frameState.OffY % this.height;
        }
        else {
            n4 = this.height - (int)Math.abs(IsoCamera.frameState.OffY) % this.height;
        }
        final int n5 = -n3;
        final int n6 = -n4;
        for (int i = 0; i < n2; ++i) {
            for (int j = 0; j < n; ++j) {
                final int n7 = n5 + j * this.width;
                final int n8 = n6 + i * this.height;
                if (this.DEBUG_BOUNDS || IsoWeatherFX.DEBUG_BOUNDS) {
                    LineDrawer.drawRect((float)n7, (float)n8, (float)this.width, (float)this.height, 0.0f, 1.0f, 0.0f, 1.0f, 1);
                }
                for (int k = 0; k < this.particles.length; ++k) {
                    final WeatherParticle weatherParticle = this.particles[k];
                    if (weatherParticle.renderAlpha > 0.0f) {
                        weatherParticle.render((float)n7, (float)n8);
                        if (this.DEBUG_BOUNDS || IsoWeatherFX.DEBUG_BOUNDS) {
                            LineDrawer.drawRect((float)(n7 + weatherParticle.bounds.getX()), (float)(n8 + weatherParticle.bounds.getY()), (float)weatherParticle.bounds.getWidth(), (float)weatherParticle.bounds.getHeight(), 0.0f, 0.0f, 1.0f, 0.5f, 1);
                        }
                    }
                }
            }
        }
    }
}

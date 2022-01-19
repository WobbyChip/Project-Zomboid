// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ui;

public final class UITransition
{
    private float duration;
    private float elapsed;
    private float frac;
    private boolean fadeOut;
    private boolean bIgnoreUpdateTime;
    private long updateTimeMS;
    private static long currentTimeMS;
    private static long elapsedTimeMS;
    
    public static void UpdateAll() {
        final long currentTimeMillis = System.currentTimeMillis();
        UITransition.elapsedTimeMS = currentTimeMillis - UITransition.currentTimeMS;
        UITransition.currentTimeMS = currentTimeMillis;
    }
    
    public UITransition() {
        this.bIgnoreUpdateTime = false;
        this.duration = 100.0f;
    }
    
    public void init(final float a, final boolean fadeOut) {
        this.duration = Math.max(a, 1.0f);
        if (this.frac >= 1.0f) {
            this.elapsed = 0.0f;
        }
        else if (this.fadeOut != fadeOut) {
            this.elapsed = (1.0f - this.frac) * this.duration;
        }
        else {
            this.elapsed = this.frac * this.duration;
        }
        this.fadeOut = fadeOut;
    }
    
    public void update() {
        if (!this.bIgnoreUpdateTime && this.updateTimeMS != 0L && this.updateTimeMS + (long)this.duration < UITransition.currentTimeMS) {
            this.elapsed = this.duration;
        }
        this.updateTimeMS = UITransition.currentTimeMS;
        this.frac = this.elapsed / this.duration;
        this.elapsed = Math.min(this.elapsed + UITransition.elapsedTimeMS, this.duration);
    }
    
    public float fraction() {
        return this.fadeOut ? (1.0f - this.frac) : this.frac;
    }
    
    public void setFadeIn(final boolean b) {
        if (b) {
            if (this.fadeOut) {
                this.init(100.0f, false);
            }
        }
        else if (!this.fadeOut) {
            this.init(200.0f, true);
        }
    }
    
    public void reset() {
        this.elapsed = 0.0f;
    }
    
    public void setIgnoreUpdateTime(final boolean bIgnoreUpdateTime) {
        this.bIgnoreUpdateTime = bIgnoreUpdateTime;
    }
    
    public float getElapsed() {
        return this.elapsed;
    }
    
    public void setElapsed(final float elapsed) {
        this.elapsed = elapsed;
    }
}

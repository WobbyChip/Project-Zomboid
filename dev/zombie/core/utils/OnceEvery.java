// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.utils;

import zombie.GameTime;
import zombie.core.Rand;

public final class OnceEvery
{
    private long initialDelayMillis;
    private long triggerIntervalMillis;
    private static float milliFraction;
    private static long currentMillis;
    private static long prevMillis;
    
    public OnceEvery(final float n) {
        this(n, false);
    }
    
    public OnceEvery(final float n, final boolean b) {
        this.initialDelayMillis = 0L;
        this.triggerIntervalMillis = (long)(n * 1000.0f);
        this.initialDelayMillis = 0L;
        if (b) {
            this.initialDelayMillis = Rand.Next(this.triggerIntervalMillis);
        }
    }
    
    public static long getElapsedMillis() {
        return OnceEvery.currentMillis;
    }
    
    public boolean Check() {
        return OnceEvery.currentMillis >= this.initialDelayMillis && (this.triggerIntervalMillis == 0L || (OnceEvery.prevMillis - this.initialDelayMillis) % this.triggerIntervalMillis > (OnceEvery.currentMillis - this.initialDelayMillis) % this.triggerIntervalMillis || this.triggerIntervalMillis < OnceEvery.currentMillis - OnceEvery.prevMillis);
    }
    
    public static void update() {
        final long currentMillis = OnceEvery.currentMillis;
        final float n = GameTime.instance.getTimeDelta() * 1000.0f + OnceEvery.milliFraction;
        final long n2 = (long)n;
        final float milliFraction = n - n2;
        final long currentMillis2 = currentMillis + n2;
        OnceEvery.prevMillis = currentMillis;
        OnceEvery.currentMillis = currentMillis2;
        OnceEvery.milliFraction = milliFraction;
    }
    
    static {
        OnceEvery.milliFraction = 0.0f;
        OnceEvery.currentMillis = 0L;
        OnceEvery.prevMillis = 0L;
    }
}

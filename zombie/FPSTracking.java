// 
// Decompiled by Procyon v0.5.36
// 

package zombie;

import zombie.core.PerformanceSettings;

public final class FPSTracking
{
    private final double[] lastFPS;
    private int lastFPSCount;
    private long timeAtLastUpdate;
    private final long[] last10;
    private int last10index;
    
    public FPSTracking() {
        this.lastFPS = new double[20];
        this.lastFPSCount = 0;
        this.last10 = new long[10];
        this.last10index = 0;
    }
    
    public void init() {
        for (int i = 0; i < 20; ++i) {
            this.lastFPS[i] = PerformanceSettings.getLockFPS();
        }
        this.timeAtLastUpdate = System.nanoTime();
    }
    
    public long frameStep() {
        final long nanoTime = System.nanoTime();
        final long n = nanoTime - this.timeAtLastUpdate;
        if (n > 0L) {
            float n2 = 0.0f;
            final double n3 = 1.0 / (n / 1.0E9);
            this.lastFPS[this.lastFPSCount] = n3;
            ++this.lastFPSCount;
            if (this.lastFPSCount >= 5) {
                this.lastFPSCount = 0;
            }
            for (int i = 0; i < 5; ++i) {
                n2 += (float)this.lastFPS[i];
            }
            GameWindow.averageFPS = n2 / 5.0f;
            GameTime.instance.FPSMultiplier = (float)(60.0 / n3);
            if (GameTime.instance.FPSMultiplier > 5.0f) {
                GameTime.instance.FPSMultiplier = 5.0f;
            }
        }
        this.timeAtLastUpdate = nanoTime;
        this.updateFPS(n);
        return n;
    }
    
    public void updateFPS(final long n) {
        this.last10[this.last10index++] = n;
        if (this.last10index >= this.last10.length) {
            this.last10index = 0;
        }
        float n2 = 11110.0f;
        float n3 = -11110.0f;
        for (final long n4 : this.last10) {
            if (n4 != 0L) {
                if (n4 < n2) {
                    n2 = (float)n4;
                }
                if (n4 > n3) {
                    n3 = (float)n4;
                }
            }
        }
    }
}

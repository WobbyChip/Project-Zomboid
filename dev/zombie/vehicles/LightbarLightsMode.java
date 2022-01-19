// 
// Decompiled by Procyon v0.5.36
// 

package zombie.vehicles;

public final class LightbarLightsMode
{
    private long startTime;
    private int light;
    private final int modeMax = 3;
    private int mode;
    
    public LightbarLightsMode() {
        this.startTime = 0L;
        this.light = 0;
        this.mode = 0;
    }
    
    public int get() {
        return this.mode;
    }
    
    public void set(final int mode) {
        if (mode > 3) {
            this.mode = 3;
            return;
        }
        if (mode < 0) {
            this.mode = 0;
            return;
        }
        this.mode = mode;
        if (this.mode != 0) {
            this.start();
        }
    }
    
    public void start() {
        this.startTime = System.currentTimeMillis();
    }
    
    public void update() {
        final long n = System.currentTimeMillis() - this.startTime;
        switch (this.mode) {
            case 1: {
                final long n2 = n % 1000L;
                if (n2 < 50L) {
                    this.light = 0;
                    break;
                }
                if (n2 < 450L) {
                    this.light = 1;
                    break;
                }
                if (n2 < 550L) {
                    this.light = 0;
                    break;
                }
                if (n2 < 950L) {
                    this.light = 2;
                    break;
                }
                this.light = 0;
                break;
            }
            case 2: {
                final long n3 = n % 1000L;
                if (n3 < 50L) {
                    this.light = 0;
                    break;
                }
                if (n3 < 250L) {
                    this.light = 1;
                    break;
                }
                if (n3 < 300L) {
                    this.light = 0;
                    break;
                }
                if (n3 < 500L) {
                    this.light = 1;
                    break;
                }
                if (n3 < 550L) {
                    this.light = 0;
                    break;
                }
                if (n3 < 750L) {
                    this.light = 2;
                    break;
                }
                if (n3 < 800L) {
                    this.light = 0;
                    break;
                }
                this.light = 2;
                break;
            }
            case 3: {
                final long n4 = n % 300L;
                if (n4 < 25L) {
                    this.light = 0;
                    break;
                }
                if (n4 < 125L) {
                    this.light = 1;
                    break;
                }
                if (n4 < 175L) {
                    this.light = 0;
                    break;
                }
                if (n4 < 275L) {
                    this.light = 2;
                    break;
                }
                this.light = 0;
                break;
            }
            default: {
                this.light = 0;
                break;
            }
        }
    }
    
    public int getLightTexIndex() {
        return this.light;
    }
    
    public boolean isEnable() {
        return this.mode != 0;
    }
}

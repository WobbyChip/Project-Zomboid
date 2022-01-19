// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.utils;

public final class UpdateLimit
{
    private long delay;
    private long last;
    private long lastPeriod;
    
    public UpdateLimit(final long delay) {
        this.delay = delay;
        this.last = System.currentTimeMillis();
        this.lastPeriod = this.last;
    }
    
    public UpdateLimit(final long delay, final long n) {
        this.delay = delay;
        this.last = System.currentTimeMillis() - n;
        this.lastPeriod = this.last;
    }
    
    public void BlockCheck() {
        this.last = System.currentTimeMillis() + this.delay;
    }
    
    public void Reset(final long delay) {
        this.delay = delay;
        this.Reset();
    }
    
    public void Reset() {
        this.last = System.currentTimeMillis();
        this.lastPeriod = System.currentTimeMillis();
    }
    
    public void setUpdatePeriod(final long delay) {
        this.delay = delay;
    }
    
    public boolean Check() {
        final long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - this.last > this.delay) {
            if (currentTimeMillis - this.last > 3L * this.delay) {
                this.last = currentTimeMillis;
            }
            else {
                this.last += this.delay;
            }
            return true;
        }
        return false;
    }
    
    public long getLast() {
        return this.last;
    }
    
    public void updateTimePeriod() {
        final long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - this.last > this.delay) {
            if (currentTimeMillis - this.last > 3L * this.delay) {
                this.last = currentTimeMillis;
            }
            else {
                this.last += this.delay;
            }
        }
        this.lastPeriod = currentTimeMillis;
    }
    
    public double getTimePeriod() {
        return Math.min((System.currentTimeMillis() - (double)this.lastPeriod) / this.delay, 1.0);
    }
}

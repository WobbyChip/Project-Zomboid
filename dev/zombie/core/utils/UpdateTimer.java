// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.utils;

public class UpdateTimer
{
    private long time;
    
    public UpdateTimer() {
        this.time = 0L;
        this.time = System.currentTimeMillis() + 3800L;
    }
    
    public void reset(final long n) {
        this.time = System.currentTimeMillis() + n;
    }
    
    public boolean check() {
        return this.time != 0L && System.currentTimeMillis() + 200L >= this.time;
    }
    
    public long getTime() {
        return this.time;
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.spnetwork;

import java.util.ArrayDeque;

public final class ZomboidNetDataPool
{
    public static ZomboidNetDataPool instance;
    private final ArrayDeque<ZomboidNetData> Pool;
    
    public ZomboidNetDataPool() {
        this.Pool = new ArrayDeque<ZomboidNetData>();
    }
    
    public ZomboidNetData get() {
        synchronized (this.Pool) {
            if (this.Pool.isEmpty()) {
                return new ZomboidNetData();
            }
            return this.Pool.pop();
        }
    }
    
    public void discard(final ZomboidNetData e) {
        e.reset();
        if (e.buffer.capacity() == 2048) {
            synchronized (this.Pool) {
                this.Pool.add(e);
            }
        }
    }
    
    public ZomboidNetData getLong(final int n) {
        return new ZomboidNetData(n);
    }
    
    static {
        ZomboidNetDataPool.instance = new ZomboidNetDataPool();
    }
}

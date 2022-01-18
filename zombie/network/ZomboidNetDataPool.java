// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ZomboidNetDataPool
{
    public static final ZomboidNetDataPool instance;
    final ConcurrentLinkedQueue<ZomboidNetData> Pool;
    
    public ZomboidNetDataPool() {
        this.Pool = new ConcurrentLinkedQueue<ZomboidNetData>();
    }
    
    public ZomboidNetData get() {
        final ZomboidNetData zomboidNetData = this.Pool.poll();
        if (zomboidNetData == null) {
            return new ZomboidNetData();
        }
        return zomboidNetData;
    }
    
    public void discard(final ZomboidNetData e) {
        e.reset();
        if (e.buffer.capacity() == 2048) {
            this.Pool.add(e);
        }
    }
    
    public ZomboidNetData getLong(final int n) {
        return new ZomboidNetData(n);
    }
    
    static {
        instance = new ZomboidNetDataPool();
    }
}

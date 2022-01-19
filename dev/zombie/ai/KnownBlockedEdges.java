// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai;

import java.util.List;
import java.util.ArrayList;
import zombie.GameWindow;
import zombie.network.GameServer;
import zombie.popman.ObjectPool;

public final class KnownBlockedEdges
{
    public int x;
    public int y;
    public int z;
    public boolean w;
    public boolean n;
    static final ObjectPool<KnownBlockedEdges> pool;
    
    public KnownBlockedEdges init(final KnownBlockedEdges knownBlockedEdges) {
        return this.init(knownBlockedEdges.x, knownBlockedEdges.y, knownBlockedEdges.z, knownBlockedEdges.w, knownBlockedEdges.n);
    }
    
    public KnownBlockedEdges init(final int n, final int n2, final int n3) {
        return this.init(n, n2, n3, false, false);
    }
    
    public KnownBlockedEdges init(final int x, final int y, final int z, final boolean w, final boolean n) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        this.n = n;
        return this;
    }
    
    public boolean isBlocked(final int n, final int n2) {
        return (this.x > n && this.w) || (this.y > n2 && this.n);
    }
    
    public static KnownBlockedEdges alloc() {
        assert Thread.currentThread() == GameWindow.GameThread;
        return KnownBlockedEdges.pool.alloc();
    }
    
    public static void releaseAll(final ArrayList<KnownBlockedEdges> list) {
        assert Thread.currentThread() == GameWindow.GameThread;
        KnownBlockedEdges.pool.release(list);
    }
    
    public void release() {
        assert Thread.currentThread() == GameWindow.GameThread;
        KnownBlockedEdges.pool.release(this);
    }
    
    static {
        pool = new ObjectPool<KnownBlockedEdges>(KnownBlockedEdges::new);
    }
}

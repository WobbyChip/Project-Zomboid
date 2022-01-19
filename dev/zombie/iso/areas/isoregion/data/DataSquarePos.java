// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.areas.isoregion.data;

import zombie.iso.areas.isoregion.IsoRegions;
import java.util.ArrayDeque;

public final class DataSquarePos
{
    public static boolean DEBUG_POOL;
    private static final ArrayDeque<DataSquarePos> pool;
    public int x;
    public int y;
    public int z;
    
    static DataSquarePos alloc(final int n, final int n2, final int n3) {
        final DataSquarePos dataSquarePos = DataSquarePos.pool.isEmpty() ? new DataSquarePos() : DataSquarePos.pool.pop();
        dataSquarePos.set(n, n2, n3);
        return dataSquarePos;
    }
    
    static void release(final DataSquarePos dataSquarePos) {
        assert !DataSquarePos.pool.contains(dataSquarePos);
        if (DataSquarePos.DEBUG_POOL && DataSquarePos.pool.contains(dataSquarePos)) {
            IsoRegions.warn("DataSquarePos.release Trying to release a DataSquarePos twice.");
            return;
        }
        DataSquarePos.pool.push(dataSquarePos.reset());
    }
    
    private DataSquarePos() {
    }
    
    private DataSquarePos reset() {
        return this;
    }
    
    public void set(final int x, final int y, final int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    static {
        DataSquarePos.DEBUG_POOL = true;
        pool = new ArrayDeque<DataSquarePos>();
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util;

import zombie.util.list.PZArrayUtil;

public final class PooledFloatArrayObject extends PooledObject
{
    private static final Pool<PooledFloatArrayObject> s_pool;
    private float[] m_array;
    
    public PooledFloatArrayObject() {
        this.m_array = PZArrayUtil.emptyFloatArray;
    }
    
    public static PooledFloatArrayObject alloc(final int n) {
        final PooledFloatArrayObject pooledFloatArrayObject = PooledFloatArrayObject.s_pool.alloc();
        pooledFloatArrayObject.initCapacity(n);
        return pooledFloatArrayObject;
    }
    
    public static PooledFloatArrayObject toArray(final PooledFloatArrayObject pooledFloatArrayObject) {
        if (pooledFloatArrayObject == null) {
            return null;
        }
        final int length = pooledFloatArrayObject.length();
        final PooledFloatArrayObject alloc = alloc(length);
        if (length > 0) {
            System.arraycopy(pooledFloatArrayObject.array(), 0, alloc.array(), 0, length);
        }
        return alloc;
    }
    
    private void initCapacity(final int n) {
        if (this.m_array.length != n) {
            this.m_array = new float[n];
        }
    }
    
    public float[] array() {
        return this.m_array;
    }
    
    public float get(final int n) {
        return this.m_array[n];
    }
    
    public void set(final int n, final float n2) {
        this.m_array[n] = n2;
    }
    
    public int length() {
        return this.m_array.length;
    }
    
    static {
        s_pool = new Pool<PooledFloatArrayObject>(PooledFloatArrayObject::new);
    }
}

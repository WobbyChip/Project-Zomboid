// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util;

public abstract class PooledObject implements IPooledObject
{
    private boolean m_isFree;
    private Pool<IPooledObject> m_pool;
    
    public PooledObject() {
        this.m_isFree = true;
    }
    
    @Override
    public final Pool<IPooledObject> getPool() {
        return this.m_pool;
    }
    
    @Override
    public final void setPool(final Pool<IPooledObject> pool) {
        this.m_pool = pool;
    }
    
    @Override
    public final void release() {
        if (this.m_pool != null) {
            this.m_pool.release(this);
        }
        else {
            this.onReleased();
        }
    }
    
    @Override
    public final boolean isFree() {
        return this.m_isFree;
    }
    
    @Override
    public final void setFree(final boolean isFree) {
        this.m_isFree = isFree;
    }
}

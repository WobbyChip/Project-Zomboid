// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util;

import java.util.ArrayList;
import java.util.function.Consumer;
import zombie.util.list.PZArrayUtil;
import java.util.List;
import gnu.trove.set.hash.THashSet;
import java.util.function.Supplier;

public final class Pool<PO extends IPooledObject>
{
    private final Supplier<PO> m_allocator;
    private final ThreadLocal<PoolStacks> m_stacks;
    
    public Pool(final Supplier<PO> allocator) {
        this.m_stacks = ThreadLocal.withInitial((Supplier<? extends PoolStacks>)PoolStacks::new);
        this.m_allocator = allocator;
    }
    
    public final PO alloc() {
        final Supplier<PO> allocator = this.m_allocator;
        final PoolStacks poolStacks = this.m_stacks.get();
        final THashSet<IPooledObject> inUse = poolStacks.inUse;
        final List<IPooledObject> released = poolStacks.released;
        IPooledObject pooledObject;
        if (!released.isEmpty()) {
            pooledObject = released.remove(released.size() - 1);
        }
        else {
            pooledObject = allocator.get();
            if (pooledObject == null) {
                throw new NullPointerException("Allocator returned a nullPtr. This is not allowed.");
            }
            pooledObject.setPool((Pool<IPooledObject>)this);
        }
        pooledObject.setFree(false);
        inUse.add((Object)pooledObject);
        return (PO)pooledObject;
    }
    
    public final void release(final IPooledObject pooledObject) {
        final PoolStacks poolStacks = this.m_stacks.get();
        final THashSet<IPooledObject> inUse = poolStacks.inUse;
        final List<IPooledObject> released = poolStacks.released;
        if (pooledObject.getPool() != this) {
            throw new UnsupportedOperationException("Cannot release item. Not owned by this pool.");
        }
        if (pooledObject.isFree()) {
            throw new UnsupportedOperationException("Cannot release item. Already released.");
        }
        inUse.remove((Object)pooledObject);
        pooledObject.setFree(true);
        released.add(pooledObject);
        pooledObject.onReleased();
    }
    
    public static <E> E tryRelease(final E e) {
        final IPooledObject pooledObject = Type.tryCastTo(e, IPooledObject.class);
        if (pooledObject != null && !pooledObject.isFree()) {
            pooledObject.release();
        }
        return null;
    }
    
    public static <E extends IPooledObject> E tryRelease(final E e) {
        if (e != null && !e.isFree()) {
            e.release();
        }
        return null;
    }
    
    public static <E extends IPooledObject> E[] tryRelease(final E[] array) {
        PZArrayUtil.forEach(array, Pool::tryRelease);
        return null;
    }
    
    private static final class PoolStacks
    {
        final THashSet<IPooledObject> inUse;
        final List<IPooledObject> released;
        
        PoolStacks() {
            this.inUse = (THashSet<IPooledObject>)new THashSet();
            this.released = new ArrayList<IPooledObject>();
            this.inUse.setAutoCompactionFactor(0.0f);
        }
    }
}

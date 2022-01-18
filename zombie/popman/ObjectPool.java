// 
// Decompiled by Procyon v0.5.36
// 

package zombie.popman;

import java.util.function.Consumer;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class ObjectPool<T>
{
    private final Allocator<T> allocator;
    private final ArrayList<T> pool;
    
    public ObjectPool() {
        this(null);
    }
    
    public ObjectPool(final Allocator<T> allocator) {
        this.pool = new ArrayList<T>() {
            @Override
            public boolean contains(final Object o) {
                for (int i = 0; i < ObjectPool.this.pool.size(); ++i) {
                    if (ObjectPool.this.pool.get(i) == o) {
                        return true;
                    }
                }
                return false;
            }
        };
        this.allocator = allocator;
    }
    
    public T alloc() {
        return this.pool.isEmpty() ? this.makeObject() : this.pool.remove(this.pool.size() - 1);
    }
    
    public void release(final T t) {
        assert t != null;
        assert !this.pool.contains(t);
        this.pool.add(t);
    }
    
    public void release(final List<T> list) {
        for (int i = 0; i < list.size(); ++i) {
            if (list.get(i) != null) {
                this.release(list.get(i));
            }
        }
    }
    
    public void release(final Iterable<T> iterable) {
        for (final T next : iterable) {
            if (next != null) {
                this.release(next);
            }
        }
    }
    
    public void release(final T[] array) {
        if (array == null) {
            return;
        }
        for (int i = 0; i < array.length; ++i) {
            if (array[i] != null) {
                this.release(array[i]);
            }
        }
    }
    
    public void releaseAll(final List<T> list) {
        for (int i = 0; i < list.size(); ++i) {
            if (list.get(i) != null) {
                this.release(list.get(i));
            }
        }
    }
    
    public void clear() {
        this.pool.clear();
    }
    
    protected T makeObject() {
        if (this.allocator != null) {
            return this.allocator.allocate();
        }
        throw new UnsupportedOperationException("Allocator is null. The ObjectPool is intended to be used with an allocator, or with the function makeObject overridden in a subclass.");
    }
    
    public void forEach(final Consumer<T> consumer) {
        for (int i = 0; i < this.pool.size(); ++i) {
            consumer.accept(this.pool.get(i));
        }
    }
    
    public interface Allocator<T>
    {
        T allocate();
    }
}

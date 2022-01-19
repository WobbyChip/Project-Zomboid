// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util;

import java.util.function.Function;

public class PooledArrayObject<T> extends PooledObject
{
    private T[] m_array;
    
    public PooledArrayObject() {
        this.m_array = null;
    }
    
    public T[] array() {
        return this.m_array;
    }
    
    public int length() {
        return this.m_array.length;
    }
    
    public T get(final int n) {
        return this.m_array[n];
    }
    
    public void set(final int n, final T t) {
        this.m_array[n] = t;
    }
    
    protected void initCapacity(final int i, final Function<Integer, T[]> function) {
        if (this.m_array == null || this.m_array.length != i) {
            this.m_array = function.apply(i);
        }
    }
    
    public boolean isEmpty() {
        return this.m_array == null || this.m_array.length == 0;
    }
}

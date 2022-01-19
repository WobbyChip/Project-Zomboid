// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util.list;

import java.util.Iterator;

public final class PZEmptyIterable<T> implements Iterable<T>
{
    private static final PZEmptyIterable<Object> instance;
    private final Iterator<T> s_it;
    
    private PZEmptyIterable() {
        this.s_it = new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return false;
            }
            
            @Override
            public T next() {
                throw new ArrayIndexOutOfBoundsException("Empty Iterator. Has no data.");
            }
        };
    }
    
    public static <E> PZEmptyIterable<E> getInstance() {
        return (PZEmptyIterable<E>)PZEmptyIterable.instance;
    }
    
    @Override
    public Iterator<T> iterator() {
        return this.s_it;
    }
    
    static {
        instance = new PZEmptyIterable<Object>();
    }
}

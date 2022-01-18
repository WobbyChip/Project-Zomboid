// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util.list;

import java.util.Iterator;
import java.util.function.Function;

public final class PZConvertIterable<T, S> implements Iterable<T>
{
    private final Iterable<S> m_srcIterable;
    private final Function<S, T> m_converter;
    
    public PZConvertIterable(final Iterable<S> srcIterable, final Function<S, T> converter) {
        this.m_srcIterable = srcIterable;
        this.m_converter = converter;
    }
    
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Iterator<S> m_srcIterator = PZConvertIterable.this.m_srcIterable.iterator();
            
            @Override
            public boolean hasNext() {
                return this.m_srcIterator.hasNext();
            }
            
            @Override
            public T next() {
                return PZConvertIterable.this.m_converter.apply(this.m_srcIterator.next());
            }
        };
    }
}

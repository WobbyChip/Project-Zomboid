// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util.list;

import java.util.Comparator;
import java.util.function.UnaryOperator;
import java.util.function.Consumer;
import java.util.Objects;
import java.util.function.Function;
import java.util.List;
import java.util.RandomAccess;
import java.util.AbstractList;

public final class PZConvertList<S, T> extends AbstractList<T> implements RandomAccess
{
    private final List<S> m_list;
    private final Function<S, T> m_converterST;
    private final Function<T, S> m_converterTS;
    
    public PZConvertList(final List<S> list, final Function<S, T> function) {
        this(list, function, null);
    }
    
    public PZConvertList(final List<S> obj, final Function<S, T> converterST, final Function<T, S> converterTS) {
        this.m_list = Objects.requireNonNull(obj);
        this.m_converterST = converterST;
        this.m_converterTS = converterTS;
    }
    
    public boolean isReadonly() {
        return this.m_converterTS == null;
    }
    
    @Override
    public int size() {
        return this.m_list.size();
    }
    
    @Override
    public Object[] toArray() {
        return this.m_list.toArray();
    }
    
    @Override
    public <R> R[] toArray(final R[] array) {
        final int size = this.size();
        for (int n = 0; n < size && n < array.length; ++n) {
            array[n] = (R)this.get(n);
        }
        if (array.length > size) {
            array[size] = null;
        }
        return array;
    }
    
    @Override
    public T get(final int n) {
        return this.convertST(this.m_list.get(n));
    }
    
    @Override
    public T set(final int n, final T t) {
        final T value = this.get(n);
        this.setS(n, this.convertTS(t));
        return value;
    }
    
    public S setS(final int n, final S n2) {
        final S value = this.m_list.get(n);
        this.m_list.set(n, n2);
        return value;
    }
    
    @Override
    public int indexOf(final Object o) {
        int n = -1;
        for (int i = 0; i < this.size(); ++i) {
            if (objectsEqual(o, this.get(i))) {
                n = i;
                break;
            }
        }
        return n;
    }
    
    private static boolean objectsEqual(final Object o, final Object obj) {
        return o == obj || (o != null && o.equals(obj));
    }
    
    @Override
    public boolean contains(final Object o) {
        return this.indexOf(o) != -1;
    }
    
    @Override
    public void forEach(final Consumer<? super T> consumer) {
        for (int i = 0; i < this.size(); ++i) {
            consumer.accept((Object)this.get(i));
        }
    }
    
    @Override
    public void replaceAll(final UnaryOperator<T> obj) {
        Objects.requireNonNull(obj);
        for (int i = 0; i < this.size(); ++i) {
            this.set(i, (T)obj.apply(this.get(i)));
        }
    }
    
    @Override
    public void sort(final Comparator<? super T> comparator) {
        this.m_list.sort((n, n2) -> comparator.compare(this.convertST(n), (Object)this.convertST(n2)));
    }
    
    private T convertST(final S n) {
        return this.m_converterST.apply(n);
    }
    
    private S convertTS(final T t) {
        return this.m_converterTS.apply(t);
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util.list;

import java.util.Comparator;
import java.util.function.UnaryOperator;
import java.util.function.Consumer;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.RandomAccess;
import java.util.AbstractList;

public final class PZConvertArray<S, T> extends AbstractList<T> implements RandomAccess
{
    private final S[] m_array;
    private final Function<S, T> m_converterST;
    private final Function<T, S> m_converterTS;
    
    public PZConvertArray(final S[] array, final Function<S, T> function) {
        this(array, (Function<Object, T>)function, null);
    }
    
    public PZConvertArray(final S[] obj, final Function<S, T> converterST, final Function<T, S> converterTS) {
        this.m_array = Objects.requireNonNull(obj);
        this.m_converterST = converterST;
        this.m_converterTS = converterTS;
    }
    
    public boolean isReadonly() {
        return this.m_converterTS == null;
    }
    
    @Override
    public int size() {
        return this.m_array.length;
    }
    
    @Override
    public Object[] toArray() {
        return Arrays.asList(this.m_array).toArray();
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
        return this.convertST(this.m_array[n]);
    }
    
    @Override
    public T set(final int n, final T t) {
        final T value = this.get(n);
        this.setS(n, this.convertTS(t));
        return value;
    }
    
    public S setS(final int n, final S n2) {
        final S n3 = this.m_array[n];
        this.m_array[n] = n2;
        return n3;
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
        final S[] array = this.m_array;
        for (int i = 0; i < array.length; ++i) {
            array[i] = this.convertTS((T)obj.apply(this.get(i)));
        }
    }
    
    @Override
    public void sort(final Comparator<? super T> comparator) {
        Arrays.sort(this.m_array, (n, n2) -> comparator.compare(this.convertST(n), (Object)this.convertST(n2)));
    }
    
    private T convertST(final S n) {
        return this.m_converterST.apply(n);
    }
    
    private S convertTS(final T t) {
        return this.m_converterTS.apply(t);
    }
}

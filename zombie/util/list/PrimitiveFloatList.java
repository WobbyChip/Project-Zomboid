// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util.list;

import java.util.Comparator;
import java.util.function.UnaryOperator;
import java.util.function.Consumer;
import java.util.Arrays;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.AbstractList;

public class PrimitiveFloatList extends AbstractList<Float> implements RandomAccess
{
    private final float[] m_array;
    
    public PrimitiveFloatList(final float[] obj) {
        this.m_array = Objects.requireNonNull(obj);
    }
    
    @Override
    public int size() {
        return this.m_array.length;
    }
    
    @Override
    public Object[] toArray() {
        return Arrays.asList(new float[][] { this.m_array }).toArray();
    }
    
    @Override
    public <T> T[] toArray(final T[] array) {
        final int size = this.size();
        for (int n = 0; n < size && n < array.length; ++n) {
            array[n] = (T)Float.valueOf(this.m_array[n]);
        }
        if (array.length > size) {
            array[size] = null;
        }
        return array;
    }
    
    @Override
    public Float get(final int n) {
        return this.m_array[n];
    }
    
    @Override
    public Float set(final int n, final Float n2) {
        return this.set(n, (float)n2);
    }
    
    public float set(final int n, final float n2) {
        final float n3 = this.m_array[n];
        this.m_array[n] = n2;
        return n3;
    }
    
    @Override
    public int indexOf(final Object o) {
        if (o == null) {
            return -1;
        }
        if (o instanceof Number) {
            return this.indexOf(((Number)o).floatValue());
        }
        return -1;
    }
    
    public int indexOf(final float n) {
        int n2 = -1;
        for (int i = 0; i < this.size(); ++i) {
            if (this.m_array[i] == n) {
                n2 = i;
                break;
            }
        }
        return n2;
    }
    
    @Override
    public boolean contains(final Object o) {
        return this.indexOf(o) != -1;
    }
    
    public boolean contains(final float n) {
        return this.indexOf(n) != -1;
    }
    
    @Override
    public void forEach(final Consumer<? super Float> consumer) {
        Objects.requireNonNull(consumer);
        Objects.requireNonNull(consumer);
        this.forEach(consumer::accept);
    }
    
    public void forEach(final FloatConsumer floatConsumer) {
        for (int i = 0; i < this.size(); ++i) {
            floatConsumer.accept(this.m_array[i]);
        }
    }
    
    @Override
    public void replaceAll(final UnaryOperator<Float> obj) {
        Objects.requireNonNull(obj);
        final float[] array = this.m_array;
        for (int i = 0; i < array.length; ++i) {
            array[i] = (float)obj.apply(array[i]);
        }
    }
    
    @Override
    public void sort(final Comparator<? super Float> comparator) {
        this.sort();
    }
    
    public void sort() {
        Arrays.sort(this.m_array);
    }
}

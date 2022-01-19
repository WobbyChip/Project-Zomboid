// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util.list;

import java.util.Arrays;
import java.util.ListIterator;
import java.util.Iterator;
import java.lang.reflect.Array;
import java.util.RandomAccess;
import java.util.List;
import java.util.AbstractList;

public final class PZArrayList<E> extends AbstractList<E> implements List<E>, RandomAccess
{
    private E[] elements;
    private int numElements;
    private static final PZArrayList<Object> instance;
    
    public PZArrayList(final Class<E> componentType, final int length) {
        this.elements = (E[])Array.newInstance(componentType, length);
    }
    
    @Override
    public E get(final int n) {
        if (n < 0 || n >= this.numElements) {
            throw new IndexOutOfBoundsException(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, n, this.numElements));
        }
        return this.elements[n];
    }
    
    @Override
    public int size() {
        return this.numElements;
    }
    
    @Override
    public int indexOf(final Object o) {
        for (int i = 0; i < this.numElements; ++i) {
            if ((o == null && this.elements[i] == null) || (o != null && o.equals(this.elements[i]))) {
                return i;
            }
        }
        return -1;
    }
    
    @Override
    public boolean isEmpty() {
        return this.numElements == 0;
    }
    
    @Override
    public boolean contains(final Object o) {
        return this.indexOf(o) >= 0;
    }
    
    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public ListIterator<E> listIterator() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public ListIterator<E> listIterator(final int n) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean add(final E e) {
        if (this.numElements == this.elements.length) {
            int newLength = this.elements.length + (this.elements.length >> 1);
            if (newLength < this.numElements + 1) {
                newLength = this.numElements + 1;
            }
            this.elements = Arrays.copyOf(this.elements, newLength);
        }
        this.elements[this.numElements] = e;
        ++this.numElements;
        return true;
    }
    
    @Override
    public void add(final int n, final E e) {
        if (n < 0 || n > this.numElements) {
            throw new IndexOutOfBoundsException(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, n, this.numElements));
        }
        if (this.numElements == this.elements.length) {
            int newLength = this.elements.length + this.elements.length >> 1;
            if (newLength < this.numElements + 1) {
                newLength = this.numElements + 1;
            }
            this.elements = Arrays.copyOf(this.elements, newLength);
        }
        System.arraycopy(this.elements, n, this.elements, n + 1, this.numElements - n);
        this.elements[n] = e;
        ++this.numElements;
    }
    
    @Override
    public E remove(final int n) {
        if (n < 0 || n >= this.numElements) {
            throw new IndexOutOfBoundsException(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, n, this.numElements));
        }
        final E e = this.elements[n];
        final int n2 = this.numElements - n - 1;
        if (n2 > 0) {
            System.arraycopy(this.elements, n + 1, this.elements, n, n2);
        }
        this.elements[this.numElements - 1] = null;
        --this.numElements;
        return e;
    }
    
    @Override
    public boolean remove(final Object o) {
        for (int i = 0; i < this.numElements; ++i) {
            if ((o == null && this.elements[i] == null) || (o != null && o.equals(this.elements[i]))) {
                final int n = this.numElements - i - 1;
                if (n > 0) {
                    System.arraycopy(this.elements, i + 1, this.elements, i, n);
                }
                this.elements[this.numElements - 1] = null;
                --this.numElements;
                return true;
            }
        }
        return false;
    }
    
    @Override
    public E set(final int n, final E e) {
        if (n < 0 || n >= this.numElements) {
            throw new IndexOutOfBoundsException(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, n, this.numElements));
        }
        final E e2 = this.elements[n];
        this.elements[n] = e;
        return e2;
    }
    
    @Override
    public void clear() {
        for (int i = 0; i < this.numElements; ++i) {
            this.elements[i] = null;
        }
        this.numElements = 0;
    }
    
    @Override
    public String toString() {
        if (this.isEmpty()) {
            return "[]";
        }
        final StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < this.numElements; ++i) {
            final E e = this.elements[i];
            sb.append((e == this) ? "(self)" : e.toString());
            if (i == this.numElements - 1) {
                break;
            }
            sb.append(',');
            sb.append(' ');
        }
        return sb.append(']').toString();
    }
    
    public E[] getElements() {
        return this.elements;
    }
    
    public static <E> AbstractList<E> emptyList() {
        return (AbstractList<E>)PZArrayList.instance;
    }
    
    static {
        instance = new PZArrayList<Object>(Object.class, 0);
    }
}

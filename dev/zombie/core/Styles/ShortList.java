// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.Styles;

import java.io.Serializable;

public class ShortList implements Serializable
{
    private static final long serialVersionUID = 1L;
    private short[] value;
    private short count;
    private final boolean fastExpand;
    
    public ShortList() {
        this(0);
    }
    
    public ShortList(final int n) {
        this(true, n);
    }
    
    public ShortList(final boolean fastExpand, final int n) {
        this.count = 0;
        this.fastExpand = fastExpand;
        this.value = new short[n];
    }
    
    public short add(final short n) {
        if (this.count == this.value.length) {
            final short[] value = this.value;
            if (this.fastExpand) {
                this.value = new short[(value.length << 1) + 1];
            }
            else {
                this.value = new short[value.length + 1];
            }
            System.arraycopy(value, 0, this.value, 0, value.length);
        }
        this.value[this.count] = n;
        final short count = this.count;
        this.count = (short)(count + 1);
        return count;
    }
    
    public short remove(final int n) {
        if (n >= this.count || n < 0) {
            throw new IndexOutOfBoundsException(invokedynamic(makeConcatWithConstants:(IS)Ljava/lang/String;, n, this.count));
        }
        final short n2 = this.value[n];
        if (n < this.count - 1) {
            System.arraycopy(this.value, n + 1, this.value, n, this.count - n - 1);
        }
        --this.count;
        return n2;
    }
    
    public void addAll(final short[] array) {
        this.ensureCapacity(this.count + array.length);
        System.arraycopy(array, 0, this.value, this.count, array.length);
        this.count += (short)array.length;
    }
    
    public void addAll(final ShortList list) {
        this.ensureCapacity(this.count + list.count);
        System.arraycopy(list.value, 0, this.value, this.count, list.count);
        this.count += list.count;
    }
    
    public short[] array() {
        return this.value;
    }
    
    public int capacity() {
        return this.value.length;
    }
    
    public void clear() {
        this.count = 0;
    }
    
    public void ensureCapacity(final int n) {
        if (this.value.length >= n) {
            return;
        }
        final short[] value = this.value;
        System.arraycopy(value, 0, this.value = new short[n], 0, value.length);
    }
    
    public short get(final int n) {
        return this.value[n];
    }
    
    public boolean isEmpty() {
        return this.count == 0;
    }
    
    public int size() {
        return this.count;
    }
    
    public short[] toArray(short[] array) {
        if (array == null) {
            array = new short[this.count];
        }
        System.arraycopy(this.value, 0, array, 0, this.count);
        return array;
    }
    
    public void trimToSize() {
        if (this.count == this.value.length) {
            return;
        }
        System.arraycopy(this.value, 0, this.value = new short[this.count], 0, this.count);
    }
}

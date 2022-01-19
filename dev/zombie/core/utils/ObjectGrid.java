// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.utils;

import java.util.Arrays;
import java.io.Serializable;

public class ObjectGrid<T> implements Serializable, Cloneable
{
    private static final long serialVersionUID = 1L;
    private final int width;
    private final int height;
    private final Object[] value;
    
    public ObjectGrid(final int width, final int height) {
        this.width = width;
        this.height = height;
        this.value = new Object[width * height];
    }
    
    public ObjectGrid<T> clone() throws CloneNotSupportedException {
        final ObjectGrid objectGrid = new ObjectGrid(this.width, this.height);
        System.arraycopy(this.value, 0, objectGrid.value, 0, this.value.length);
        return objectGrid;
    }
    
    public void clear() {
        Arrays.fill(this.value, 0);
    }
    
    public void fill(final T val) {
        Arrays.fill(this.value, val);
    }
    
    private int getIndex(final int n, final int n2) {
        if (n < 0 || n2 < 0 || n >= this.width || n2 >= this.height) {
            return -1;
        }
        return n + n2 * this.width;
    }
    
    public T getValue(final int n, final int n2) {
        final int index = this.getIndex(n, n2);
        if (index == -1) {
            return null;
        }
        return (T)this.value[index];
    }
    
    public void setValue(final int n, final int n2, final T t) {
        final int index = this.getIndex(n, n2);
        if (index == -1) {
            return;
        }
        this.value[index] = t;
    }
    
    public final int getWidth() {
        return this.width;
    }
    
    public final int getHeight() {
        return this.height;
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.utils;

import java.util.Arrays;
import zombie.debug.DebugLog;

public class ObjectCube<T> implements Cloneable
{
    private final int width;
    private final int height;
    private final int depth;
    private final Object[] value;
    
    public ObjectCube(final int width, final int height, final int depth) {
        DebugLog.log(invokedynamic(makeConcatWithConstants:(IIII)Ljava/lang/String;, width, height, depth, width * height * depth * 4));
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.value = new Object[width * height * depth];
    }
    
    public ObjectCube<T> clone() throws CloneNotSupportedException {
        final ObjectCube objectCube = new ObjectCube(this.width, this.height, this.depth);
        System.arraycopy(this.value, 0, objectCube.value, 0, this.value.length);
        return objectCube;
    }
    
    public void clear() {
        Arrays.fill(this.value, null);
    }
    
    public void fill(final T val) {
        Arrays.fill(this.value, val);
    }
    
    private int getIndex(final int n, final int n2, final int n3) {
        if (n < 0 || n2 < 0 || n3 < 0 || n >= this.width || n2 >= this.height || n3 >= this.depth) {
            return -1;
        }
        return n + n2 * this.width + n3 * this.width * this.height;
    }
    
    public T getValue(final int n, final int n2, final int n3) {
        final int index = this.getIndex(n, n2, n3);
        if (index == -1) {
            return null;
        }
        return (T)this.value[index];
    }
    
    public void setValue(final int n, final int n2, final int n3, final T t) {
        final int index = this.getIndex(n, n2, n3);
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
    
    public int getDepth() {
        return this.depth;
    }
}

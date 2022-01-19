// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.utils;

import java.util.Arrays;
import java.io.Serializable;

public class IntHypercube implements Serializable, Cloneable
{
    private static final long serialVersionUID = 1L;
    private final int width;
    private final int height;
    private final int depth;
    private final int quanta;
    private final int wxh;
    private final int wxhxd;
    private final int[] value;
    
    public IntHypercube(final int width, final int height, final int depth, final int quanta) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.quanta = quanta;
        this.wxh = width * height;
        this.wxhxd = this.wxh * depth;
        this.value = new int[this.wxhxd * quanta];
    }
    
    public IntHypercube clone() throws CloneNotSupportedException {
        final IntHypercube intHypercube = new IntHypercube(this.width, this.height, this.depth, this.quanta);
        System.arraycopy(this.value, 0, intHypercube.value, 0, this.value.length);
        return intHypercube;
    }
    
    public void clear() {
        Arrays.fill(this.value, 0);
    }
    
    public void fill(final int val) {
        Arrays.fill(this.value, val);
    }
    
    private int getIndex(final int n, final int n2, final int n3, final int n4) {
        if (n < 0 || n2 < 0 || n3 < 0 || n4 < 0 || n >= this.width || n2 >= this.height || n3 >= this.depth || n4 >= this.quanta) {
            return -1;
        }
        return n + n2 * this.width + n3 * this.wxh + n4 * this.wxhxd;
    }
    
    public int getValue(final int n, final int n2, final int n3, final int n4) {
        final int index = this.getIndex(n, n2, n3, n4);
        if (index == -1) {
            return 0;
        }
        return this.value[index];
    }
    
    public void setValue(final int n, final int n2, final int n3, final int n4, final int n5) {
        final int index = this.getIndex(n, n2, n3, n4);
        if (index == -1) {
            return;
        }
        this.value[index] = n5;
    }
    
    public final int getWidth() {
        return this.width;
    }
    
    public final int getHeight() {
        return this.height;
    }
    
    public final int getDepth() {
        return this.depth;
    }
    
    public final int getQuanta() {
        return this.quanta;
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.utils;

import java.util.Arrays;
import java.io.Serializable;

public class ExpandableBooleanList implements Serializable, Cloneable
{
    private static final long serialVersionUID = 1L;
    private int width;
    private int bitWidth;
    private int[] value;
    
    public ExpandableBooleanList(final int bitWidth) {
        this.bitWidth = bitWidth;
        this.width = bitWidth / 32 + ((bitWidth % 32 != 0) ? 1 : 0);
        this.value = new int[this.width];
    }
    
    public ExpandableBooleanList clone() throws CloneNotSupportedException {
        final ExpandableBooleanList list = new ExpandableBooleanList(this.bitWidth);
        System.arraycopy(this.value, 0, list.value, 0, this.value.length);
        return list;
    }
    
    public void clear() {
        Arrays.fill(this.value, 0);
    }
    
    public void fill() {
        Arrays.fill(this.value, -1);
    }
    
    public boolean getValue(final int n) {
        return n >= 0 && n < this.bitWidth && (this.value[n >> 5] & 1 << (n & 0x1F)) != 0x0;
    }
    
    public void setValue(final int n, final boolean b) {
        if (n < 0) {
            return;
        }
        if (n >= this.bitWidth) {
            final int[] value = this.value;
            this.bitWidth = Math.max(this.bitWidth * 2, n + 1);
            this.width = this.bitWidth / 32 + ((this.width % 32 != 0) ? 1 : 0);
            System.arraycopy(value, 0, this.value = new int[this.width], 0, value.length);
        }
        final int n2 = n >> 5;
        final int n3 = 1 << (n & 0x1F);
        if (b) {
            final int[] value2 = this.value;
            final int n4 = n2;
            value2[n4] |= n3;
        }
        else {
            final int[] value3 = this.value;
            final int n5 = n2;
            value3[n5] &= ~n3;
        }
    }
    
    public final int getWidth() {
        return this.width;
    }
}

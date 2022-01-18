// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.utils;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.io.Serializable;

public class BooleanGrid implements Serializable, Cloneable
{
    private static final long serialVersionUID = 1L;
    private final int width;
    private final int height;
    private final int bitWidth;
    private final int[] value;
    
    public BooleanGrid(final int bitWidth, final int height) {
        this.bitWidth = bitWidth;
        this.width = bitWidth / 32 + ((bitWidth % 32 != 0) ? 1 : 0);
        this.height = height;
        this.value = new int[this.width * this.height];
    }
    
    public BooleanGrid clone() throws CloneNotSupportedException {
        final BooleanGrid booleanGrid = new BooleanGrid(this.bitWidth, this.height);
        System.arraycopy(this.value, 0, booleanGrid.value, 0, this.value.length);
        return booleanGrid;
    }
    
    public void copy(final BooleanGrid booleanGrid) {
        if (booleanGrid.bitWidth != this.bitWidth || booleanGrid.height != this.height) {
            throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Lzombie/core/utils/BooleanGrid;Lzombie/core/utils/BooleanGrid;)Ljava/lang/String;, booleanGrid, this));
        }
        System.arraycopy(booleanGrid.value, 0, this.value, 0, booleanGrid.value.length);
    }
    
    public void clear() {
        Arrays.fill(this.value, 0);
    }
    
    public void fill() {
        Arrays.fill(this.value, -1);
    }
    
    private int getIndex(final int n, final int n2) {
        if (n < 0 || n2 < 0 || n >= this.width || n2 >= this.height) {
            return -1;
        }
        return n + n2 * this.width;
    }
    
    public boolean getValue(final int n, final int n2) {
        if (n >= this.bitWidth || n < 0 || n2 >= this.height || n2 < 0) {
            return false;
        }
        final int n3 = n / 32;
        final int n4 = 1 << (n & 0x1F);
        final int index = this.getIndex(n3, n2);
        return index != -1 && (this.value[index] & n4) != 0x0;
    }
    
    public void setValue(final int n, final int n2, final boolean b) {
        if (n >= this.bitWidth || n < 0 || n2 >= this.height || n2 < 0) {
            return;
        }
        final int n3 = n / 32;
        final int n4 = 1 << (n & 0x1F);
        final int index = this.getIndex(n3, n2);
        if (index == -1) {
            return;
        }
        if (b) {
            final int[] value = this.value;
            final int n5 = index;
            value[n5] |= n4;
        }
        else {
            final int[] value2 = this.value;
            final int n6 = index;
            value2[n6] &= ~n4;
        }
    }
    
    public final int getWidth() {
        return this.width;
    }
    
    public final int getHeight() {
        return this.height;
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, this.width, this.height, this.bitWidth);
    }
    
    public void LoadFromByteBuffer(final ByteBuffer byteBuffer) {
        for (int n = this.width * this.height, i = 0; i < n; ++i) {
            this.value[i] = byteBuffer.getInt();
        }
    }
    
    public void PutToByteBuffer(final ByteBuffer byteBuffer) {
        for (int n = this.width * this.height, i = 0; i < n; ++i) {
            byteBuffer.putInt(this.value[i]);
        }
    }
}

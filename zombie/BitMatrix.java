// 
// Decompiled by Procyon v0.5.36
// 

package zombie;

public class BitMatrix
{
    public static boolean Is(final int n, final int n2, final int n3, final int n4) {
        return (1 << (n2 + 1) * 9 + ((n3 + 1) * 3 + (n4 + 1)) & n) == 1 << (n2 + 1) * 9 + ((n3 + 1) * 3 + (n4 + 1));
    }
    
    public static int Set(int n, final int n2, final int n3, final int n4, final boolean b) {
        if (b) {
            n |= 1 << (n2 + 1) * 9 + ((n3 + 1) * 3 + (n4 + 1));
        }
        else {
            n &= ~(1 << (n2 + 1) * 9 + ((n3 + 1) * 3 + (n4 + 1)));
        }
        return n;
    }
}

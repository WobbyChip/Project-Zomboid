// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core;

public class FastTrig
{
    public static double cos(final double n) {
        return sin(n + 1.5707963267948966);
    }
    
    public static double sin(double reduceSinAngle) {
        reduceSinAngle = reduceSinAngle(reduceSinAngle);
        if (Math.abs(reduceSinAngle) <= 0.7853981633974483) {
            return Math.sin(reduceSinAngle);
        }
        return Math.cos(1.5707963267948966 - reduceSinAngle);
    }
    
    private static double reduceSinAngle(double n) {
        n %= 6.283185307179586;
        if (Math.abs(n) > 3.141592653589793) {
            n -= 6.283185307179586;
        }
        if (Math.abs(n) > 1.5707963267948966) {
            n = 3.141592653589793 - n;
        }
        return n;
    }
}

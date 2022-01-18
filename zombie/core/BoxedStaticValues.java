// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core;

public class BoxedStaticValues
{
    static Double[] doubles;
    static Double[] negdoubles;
    static Double[] doublesh;
    static Double[] negdoublesh;
    
    public static Double toDouble(final double d) {
        if (d >= 10000.0) {
            return d;
        }
        if (d <= -10000.0) {
            return d;
        }
        if ((int)Math.abs(d) == Math.abs(d)) {
            if (d < 0.0) {
                return BoxedStaticValues.negdoubles[(int)(-d)];
            }
            return BoxedStaticValues.doubles[(int)d];
        }
        else {
            if ((int)Math.abs(d) != Math.abs(d) - 0.5) {
                return d;
            }
            if (d < 0.0) {
                return BoxedStaticValues.negdoublesh[(int)(-d)];
            }
            return BoxedStaticValues.doublesh[(int)d];
        }
    }
    
    static {
        BoxedStaticValues.doubles = new Double[10000];
        BoxedStaticValues.negdoubles = new Double[10000];
        BoxedStaticValues.doublesh = new Double[10000];
        BoxedStaticValues.negdoublesh = new Double[10000];
        for (int i = 0; i < 10000; ++i) {
            BoxedStaticValues.doubles[i] = (double)i;
            BoxedStaticValues.negdoubles[i] = -BoxedStaticValues.doubles[i];
            BoxedStaticValues.doublesh[i] = i + 0.5;
            BoxedStaticValues.negdoublesh[i] = -(BoxedStaticValues.doubles[i] + 0.5);
        }
    }
}

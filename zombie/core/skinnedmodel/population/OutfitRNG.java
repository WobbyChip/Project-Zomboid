// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.population;

import java.util.function.Supplier;
import zombie.core.Color;
import zombie.core.ImmutableColor;
import java.util.List;
import zombie.util.LocationRNG;

public final class OutfitRNG
{
    private static final ThreadLocal<LocationRNG> RNG;
    
    public static void setSeed(final long seed) {
        OutfitRNG.RNG.get().setSeed(seed);
    }
    
    public static long getSeed() {
        return OutfitRNG.RNG.get().getSeed();
    }
    
    public static int Next(final int n) {
        return OutfitRNG.RNG.get().nextInt(n);
    }
    
    public static int Next(int n, int n2) {
        if (n2 == n) {
            return n;
        }
        if (n > n2) {
            final int n3 = n;
            n = n2;
            n2 = n3;
        }
        return OutfitRNG.RNG.get().nextInt(n2 - n) + n;
    }
    
    public static float Next(float n, float n2) {
        if (n2 == n) {
            return n;
        }
        if (n > n2) {
            final float n3 = n;
            n = n2;
            n2 = n3;
        }
        return n + OutfitRNG.RNG.get().nextFloat() * (n2 - n);
    }
    
    public static boolean NextBool(final int n) {
        return Next(n) == 0;
    }
    
    public static <E> E pickRandom(final List<E> list) {
        if (list.isEmpty()) {
            return null;
        }
        if (list.size() == 1) {
            return list.get(0);
        }
        return list.get(Next(list.size()));
    }
    
    public static ImmutableColor randomImmutableColor() {
        return new ImmutableColor(Color.HSBtoRGB(Next(0.0f, 1.0f), Next(0.0f, 0.6f), Next(0.0f, 0.9f)));
    }
    
    static {
        RNG = ThreadLocal.withInitial((Supplier<? extends LocationRNG>)LocationRNG::new);
    }
}

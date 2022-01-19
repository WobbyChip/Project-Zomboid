// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core;

import org.uncommons.maths.random.SecureRandomSeedGenerator;
import zombie.network.GameServer;
import org.uncommons.maths.random.SeedException;
import org.uncommons.maths.random.SeedGenerator;
import org.uncommons.maths.random.CellularAutomatonRNG;

public final class Rand
{
    public static CellularAutomatonRNG rand;
    public static CellularAutomatonRNG randlua;
    public static int id;
    
    public static void init() {
        try {
            Rand.rand = new CellularAutomatonRNG((SeedGenerator)new PZSeedGenerator());
            Rand.randlua = new CellularAutomatonRNG((SeedGenerator)new PZSeedGenerator());
        }
        catch (SeedException ex) {
            ex.printStackTrace();
        }
    }
    
    private static int Next(final int n, final CellularAutomatonRNG cellularAutomatonRNG) {
        if (n <= 0) {
            return 0;
        }
        ++Rand.id;
        if (Rand.id >= 10000) {
            Rand.id = 0;
        }
        return cellularAutomatonRNG.nextInt(n);
    }
    
    public static int Next(final int n) {
        return Next(n, Rand.rand);
    }
    
    public static long Next(final long n, final CellularAutomatonRNG cellularAutomatonRNG) {
        return Next((int)n, cellularAutomatonRNG);
    }
    
    public static long Next(final long n) {
        return Next(n, Rand.rand);
    }
    
    public static int Next(int n, int n2, final CellularAutomatonRNG cellularAutomatonRNG) {
        if (n2 == n) {
            return n;
        }
        if (n > n2) {
            final int n3 = n;
            n = n2;
            n2 = n3;
        }
        ++Rand.id;
        if (Rand.id >= 10000) {
            Rand.id = 0;
        }
        return cellularAutomatonRNG.nextInt(n2 - n) + n;
    }
    
    public static int Next(final int n, final int n2) {
        return Next(n, n2, Rand.rand);
    }
    
    public static long Next(long n, long n2, final CellularAutomatonRNG cellularAutomatonRNG) {
        if (n2 == n) {
            return n;
        }
        if (n > n2) {
            final long n3 = n;
            n = n2;
            n2 = n3;
        }
        ++Rand.id;
        if (Rand.id >= 10000) {
            Rand.id = 0;
        }
        return cellularAutomatonRNG.nextInt((int)(n2 - n)) + n;
    }
    
    public static long Next(final long n, final long n2) {
        return Next(n, n2, Rand.rand);
    }
    
    public static float Next(float n, float n2, final CellularAutomatonRNG cellularAutomatonRNG) {
        if (n2 == n) {
            return n;
        }
        if (n > n2) {
            final float n3 = n;
            n = n2;
            n2 = n3;
        }
        ++Rand.id;
        if (Rand.id >= 10000) {
            Rand.id = 0;
        }
        return n + cellularAutomatonRNG.nextFloat() * (n2 - n);
    }
    
    public static float Next(final float n, final float n2) {
        return Next(n, n2, Rand.rand);
    }
    
    public static boolean NextBool(final int n) {
        return Next(n) == 0;
    }
    
    public static int AdjustForFramerate(int n) {
        if (GameServer.bServer) {
            n *= (int)0.33333334f;
        }
        else {
            n *= (int)(PerformanceSettings.getLockFPS() / 30.0f);
        }
        return n;
    }
    
    static {
        Rand.id = 0;
    }
    
    public static final class PZSeedGenerator implements SeedGenerator
    {
        private static final SeedGenerator[] GENERATORS;
        
        private PZSeedGenerator() {
        }
        
        public byte[] generateSeed(final int n) {
            final SeedGenerator[] generators = PZSeedGenerator.GENERATORS;
            final int length = generators.length;
            int i = 0;
            while (i < length) {
                final SeedGenerator seedGenerator = generators[i];
                try {
                    return seedGenerator.generateSeed(n);
                }
                catch (SeedException ex) {
                    ++i;
                    continue;
                }
                break;
            }
            throw new IllegalStateException("All available seed generation strategies failed.");
        }
        
        static {
            GENERATORS = new SeedGenerator[] { (SeedGenerator)new SecureRandomSeedGenerator() };
        }
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util;

public final class LocationRNG
{
    public static final LocationRNG instance;
    private static final float INT_TO_FLOAT;
    private long _s0;
    private long _s1;
    private long state;
    
    public void setSeed(final long state) {
        this.state = state;
        this._s0 = this.nextSplitMix64();
        this._s1 = this.nextSplitMix64();
    }
    
    public long getSeed() {
        return this.state;
    }
    
    private long nextSplitMix64() {
        final long state = this.state - 7046029254386353131L;
        this.state = state;
        final long n = state;
        final long n2 = (n ^ n >>> 30) * -4658895280553007687L;
        final long n3 = (n2 ^ n2 >>> 27) * -7723592293110705685L;
        return n3 ^ n3 >>> 31;
    }
    
    public float nextFloat() {
        return (this.nextInt() >>> 8) * LocationRNG.INT_TO_FLOAT;
    }
    
    private int nextInt() {
        final long s0 = this._s0;
        final long s2 = this._s1;
        final long n = s0 + s2;
        final long i = s2 ^ s0;
        this._s0 = (Long.rotateLeft(s0, 55) ^ i ^ i << 14);
        this._s1 = Long.rotateLeft(i, 36);
        return (int)(n & -1L);
    }
    
    public int nextInt(final int n) {
        return (int)((this.nextInt() >>> 1) * (long)n >> 31);
    }
    
    public int nextInt(final int n, final int n2, final int n3, final int n4) {
        this.setSeed((long)n4 << 16 | (long)n3 << 32 | (long)n2);
        return this.nextInt(n);
    }
    
    static {
        instance = new LocationRNG();
        INT_TO_FLOAT = Float.intBitsToFloat(864026624);
    }
}

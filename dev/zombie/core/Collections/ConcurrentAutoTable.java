// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.Collections;

import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import sun.misc.Unsafe;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.io.Serializable;

public class ConcurrentAutoTable implements Serializable
{
    private volatile CAT _cat;
    private static final AtomicReferenceFieldUpdater<ConcurrentAutoTable, CAT> _catUpdater;
    
    public ConcurrentAutoTable() {
        this._cat = new CAT(null, 4, 0L);
    }
    
    public void add(final long n) {
        this.add_if_mask(n, 0L);
    }
    
    public void decrement() {
        this.add_if_mask(-1L, 0L);
    }
    
    public void increment() {
        this.add_if_mask(1L, 0L);
    }
    
    public void set(final long n) {
        while (!this.CAS_cat(this._cat, new CAT(null, 4, n))) {}
    }
    
    public long get() {
        return this._cat.sum(0L);
    }
    
    public int intValue() {
        return (int)this._cat.sum(0L);
    }
    
    public long longValue() {
        return this._cat.sum(0L);
    }
    
    public long estimate_get() {
        return this._cat.estimate_sum(0L);
    }
    
    @Override
    public String toString() {
        return this._cat.toString(0L);
    }
    
    public void print() {
        this._cat.print();
    }
    
    public int internal_size() {
        return this._cat._t.length;
    }
    
    private long add_if_mask(final long n, final long n2) {
        return this._cat.add_if_mask(n, n2, hash(), this);
    }
    
    private boolean CAS_cat(final CAT cat, final CAT cat2) {
        return ConcurrentAutoTable._catUpdater.compareAndSet(this, cat, cat2);
    }
    
    private static final int hash() {
        final int identityHashCode = System.identityHashCode(Thread.currentThread());
        final int n = identityHashCode ^ (identityHashCode >>> 20 ^ identityHashCode >>> 12);
        return (n ^ (n >>> 7 ^ n >>> 4)) << 2;
    }
    
    static {
        _catUpdater = AtomicReferenceFieldUpdater.newUpdater(ConcurrentAutoTable.class, CAT.class, "_cat");
    }
    
    private static class CAT implements Serializable
    {
        private static final Unsafe _unsafe;
        private static final int _Lbase;
        private static final int _Lscale;
        volatile long _resizers;
        private static final AtomicLongFieldUpdater<CAT> _resizerUpdater;
        private final CAT _next;
        private volatile long _sum_cache;
        private volatile long _fuzzy_sum_cache;
        private volatile long _fuzzy_time;
        private static final int MAX_SPIN = 2;
        private long[] _t;
        
        private static long rawIndex(final long[] array, final int n) {
            assert n >= 0 && n < array.length;
            return CAT._Lbase + n * CAT._Lscale;
        }
        
        private static final boolean CAS(final long[] o, final int n, final long expected, final long x) {
            return CAT._unsafe.compareAndSwapLong(o, rawIndex(o, n), expected, x);
        }
        
        CAT(final CAT next, final int n, final long n2) {
            this._next = next;
            this._sum_cache = Long.MIN_VALUE;
            (this._t = new long[n])[0] = n2;
        }
        
        public long add_if_mask(final long n, final long n2, final int n3, final ConcurrentAutoTable concurrentAutoTable) {
            final long[] t = this._t;
            final int n4 = n3 & t.length - 1;
            final long n5 = t[n4];
            final boolean cas = CAS(t, n4, n5 & ~n2, n5 + n);
            if (this._sum_cache != Long.MIN_VALUE) {
                this._sum_cache = Long.MIN_VALUE;
            }
            if (cas) {
                return n5;
            }
            if ((n5 & n2) != 0x0L) {
                return n5;
            }
            int n6 = 0;
            while (true) {
                final long n7 = t[n4];
                if ((n7 & n2) != 0x0L) {
                    return n7;
                }
                if (CAS(t, n4, n7, n7 + n)) {
                    if (n6 < 2) {
                        return n7;
                    }
                    if (t.length >= 1048576) {
                        return n7;
                    }
                    long n8;
                    int n9;
                    for (n8 = this._resizers, n9 = t.length << 1 << 3; !CAT._resizerUpdater.compareAndSet(this, n8, n8 + n9); n8 = this._resizers) {}
                    final long n10 = n8 + n9;
                    if (concurrentAutoTable._cat != this) {
                        return n7;
                    }
                    if (n10 >> 17 != 0L) {
                        try {
                            Thread.sleep(n10 >> 17);
                        }
                        catch (InterruptedException ex) {}
                        if (concurrentAutoTable._cat != this) {
                            return n7;
                        }
                    }
                    concurrentAutoTable.CAS_cat(this, new CAT(this, t.length * 2, 0L));
                    return n7;
                }
                else {
                    ++n6;
                }
            }
        }
        
        public long sum(final long n) {
            final long sum_cache = this._sum_cache;
            if (sum_cache != Long.MIN_VALUE) {
                return sum_cache;
            }
            long sum_cache2 = (this._next == null) ? 0L : this._next.sum(n);
            final long[] t = this._t;
            for (int i = 0; i < t.length; ++i) {
                sum_cache2 += (t[i] & ~n);
            }
            return this._sum_cache = sum_cache2;
        }
        
        public long estimate_sum(final long n) {
            if (this._t.length <= 64) {
                return this.sum(n);
            }
            final long currentTimeMillis = System.currentTimeMillis();
            if (this._fuzzy_time != currentTimeMillis) {
                this._fuzzy_sum_cache = this.sum(n);
                this._fuzzy_time = currentTimeMillis;
            }
            return this._fuzzy_sum_cache;
        }
        
        public void all_or(final long n) {
            final long[] t = this._t;
            for (int i = 0; i < t.length; ++i) {
                long n2;
                for (boolean cas = false; !cas; cas = CAS(t, i, n2, n2 | n)) {
                    n2 = t[i];
                }
            }
            if (this._next != null) {
                this._next.all_or(n);
            }
            if (this._sum_cache != Long.MIN_VALUE) {
                this._sum_cache = Long.MIN_VALUE;
            }
        }
        
        public void all_and(final long n) {
            final long[] t = this._t;
            for (int i = 0; i < t.length; ++i) {
                long n2;
                for (boolean cas = false; !cas; cas = CAS(t, i, n2, n2 & n)) {
                    n2 = t[i];
                }
            }
            if (this._next != null) {
                this._next.all_and(n);
            }
            if (this._sum_cache != Long.MIN_VALUE) {
                this._sum_cache = Long.MIN_VALUE;
            }
        }
        
        public void all_set(final long n) {
            final long[] t = this._t;
            for (int i = 0; i < t.length; ++i) {
                t[i] = n;
            }
            if (this._next != null) {
                this._next.all_set(n);
            }
            if (this._sum_cache != Long.MIN_VALUE) {
                this._sum_cache = Long.MIN_VALUE;
            }
        }
        
        String toString(final long n) {
            return Long.toString(this.sum(n));
        }
        
        public void print() {
            final long[] t = this._t;
            System.out.print(invokedynamic(makeConcatWithConstants:(JJ)Ljava/lang/String;, this._sum_cache, t[0]));
            for (int i = 1; i < t.length; ++i) {
                System.out.print(invokedynamic(makeConcatWithConstants:(J)Ljava/lang/String;, t[i]));
            }
            System.out.print("]");
            if (this._next != null) {
                this._next.print();
            }
        }
        
        static {
            _unsafe = UtilUnsafe.getUnsafe();
            _Lbase = CAT._unsafe.arrayBaseOffset(long[].class);
            _Lscale = CAT._unsafe.arrayIndexScale(long[].class);
            _resizerUpdater = AtomicLongFieldUpdater.newUpdater(CAT.class, "_resizers");
        }
    }
}

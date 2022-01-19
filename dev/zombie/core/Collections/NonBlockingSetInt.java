// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.Collections;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.NoSuchElementException;
import java.lang.reflect.Field;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import sun.misc.Unsafe;
import java.io.Serializable;
import java.util.AbstractSet;

public class NonBlockingSetInt extends AbstractSet<Integer> implements Serializable
{
    private static final long serialVersionUID = 1234123412341234123L;
    private static final Unsafe _unsafe;
    private static final long _nbsi_offset;
    private transient NBSI _nbsi;
    
    private final boolean CAS_nbsi(final NBSI expected, final NBSI x) {
        return NonBlockingSetInt._unsafe.compareAndSwapObject(this, NonBlockingSetInt._nbsi_offset, expected, x);
    }
    
    public NonBlockingSetInt() {
        this._nbsi = new NBSI(63, new Counter(), this);
    }
    
    private NonBlockingSetInt(final NonBlockingSetInt nonBlockingSetInt, final NonBlockingSetInt nonBlockingSetInt2) {
        this._nbsi = new NBSI(nonBlockingSetInt._nbsi, nonBlockingSetInt2._nbsi, new Counter(), this);
    }
    
    @Override
    public boolean add(final Integer n) {
        return this.add((int)n);
    }
    
    @Override
    public boolean contains(final Object o) {
        return o instanceof Integer && this.contains((int)o);
    }
    
    @Override
    public boolean remove(final Object o) {
        return o instanceof Integer && this.remove((int)o);
    }
    
    public boolean add(final int n) {
        if (n < 0) {
            throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n));
        }
        return this._nbsi.add(n);
    }
    
    public boolean contains(final int n) {
        return n >= 0 && this._nbsi.contains(n);
    }
    
    public boolean remove(final int n) {
        return n >= 0 && this._nbsi.remove(n);
    }
    
    @Override
    public int size() {
        return this._nbsi.size();
    }
    
    @Override
    public void clear() {
        while (!this.CAS_nbsi(this._nbsi, new NBSI(63, new Counter(), this))) {}
    }
    
    public int sizeInBytes() {
        return this._nbsi.sizeInBytes();
    }
    
    public NonBlockingSetInt intersect(final NonBlockingSetInt nonBlockingSetInt) {
        final NonBlockingSetInt nonBlockingSetInt2 = new NonBlockingSetInt(this, nonBlockingSetInt);
        nonBlockingSetInt2._nbsi.intersect(nonBlockingSetInt2._nbsi, this._nbsi, nonBlockingSetInt._nbsi);
        return nonBlockingSetInt2;
    }
    
    public NonBlockingSetInt union(final NonBlockingSetInt nonBlockingSetInt) {
        final NonBlockingSetInt nonBlockingSetInt2 = new NonBlockingSetInt(this, nonBlockingSetInt);
        nonBlockingSetInt2._nbsi.union(nonBlockingSetInt2._nbsi, this._nbsi, nonBlockingSetInt._nbsi);
        return nonBlockingSetInt2;
    }
    
    public void print() {
        this._nbsi.print(0);
    }
    
    @Override
    public Iterator<Integer> iterator() {
        return new iter();
    }
    
    public IntIterator intIterator() {
        return new NBSIIntIterator();
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        final NBSI nbsi = this._nbsi;
        final int val = this._nbsi._bits.length << 6;
        objectOutputStream.writeInt(val);
        for (int i = 0; i < val; ++i) {
            objectOutputStream.writeBoolean(this._nbsi.contains(i));
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        final int int1 = objectInputStream.readInt();
        this._nbsi = new NBSI(int1, new Counter(), this);
        for (int i = 0; i < int1; ++i) {
            if (objectInputStream.readBoolean()) {
                this._nbsi.add(i);
            }
        }
    }
    
    static {
        _unsafe = UtilUnsafe.getUnsafe();
        Field declaredField = null;
        try {
            declaredField = NonBlockingSetInt.class.getDeclaredField("_nbsi");
        }
        catch (NoSuchFieldException ex) {}
        _nbsi_offset = NonBlockingSetInt._unsafe.objectFieldOffset(declaredField);
    }
    
    private class NBSIIntIterator implements IntIterator
    {
        NBSI nbsi;
        int index;
        int prev;
        
        NBSIIntIterator() {
            this.index = -1;
            this.prev = -1;
            this.nbsi = NonBlockingSetInt.this._nbsi;
            this.advance();
        }
        
        private void advance() {
            do {
                ++this.index;
                while (this.index >> 6 >= this.nbsi._bits.length) {
                    if (this.nbsi._new == null) {
                        this.index = -2;
                        return;
                    }
                    this.nbsi = this.nbsi._new;
                }
            } while (!this.nbsi.contains(this.index));
        }
        
        @Override
        public int next() {
            if (this.index == -1) {
                throw new NoSuchElementException();
            }
            this.prev = this.index;
            this.advance();
            return this.prev;
        }
        
        @Override
        public boolean hasNext() {
            return this.index != -2;
        }
        
        public void remove() {
            if (this.prev == -1) {
                throw new IllegalStateException();
            }
            this.nbsi.remove(this.prev);
            this.prev = -1;
        }
    }
    
    private class iter implements Iterator<Integer>
    {
        NBSIIntIterator intIterator;
        
        iter() {
            this.intIterator = new NBSIIntIterator();
        }
        
        @Override
        public boolean hasNext() {
            return this.intIterator.hasNext();
        }
        
        @Override
        public Integer next() {
            return this.intIterator.next();
        }
        
        @Override
        public void remove() {
            this.intIterator.remove();
        }
    }
    
    private static final class NBSI
    {
        private final transient NonBlockingSetInt _non_blocking_set_int;
        private final transient Counter _size;
        private final long[] _bits;
        private static final int _Lbase;
        private static final int _Lscale;
        private NBSI _new;
        private static final long _new_offset;
        private final transient AtomicInteger _copyIdx;
        private final transient AtomicInteger _copyDone;
        private final transient int _sum_bits_length;
        private final NBSI _nbsi64;
        
        private static long rawIndex(final long[] array, final int n) {
            assert n >= 0 && n < array.length;
            return NBSI._Lbase + n * NBSI._Lscale;
        }
        
        private final boolean CAS(final int n, final long expected, final long x) {
            return NonBlockingSetInt._unsafe.compareAndSwapLong(this._bits, rawIndex(this._bits, n), expected, x);
        }
        
        private final boolean CAS_new(final NBSI x) {
            return NonBlockingSetInt._unsafe.compareAndSwapObject(this, NBSI._new_offset, null, x);
        }
        
        private static final long mask(final int n) {
            return 1L << (n & 0x3F);
        }
        
        private NBSI(final int n, final Counter size, final NonBlockingSetInt non_blocking_set_int) {
            this._non_blocking_set_int = non_blocking_set_int;
            this._size = size;
            this._copyIdx = ((size == null) ? null : new AtomicInteger());
            this._copyDone = ((size == null) ? null : new AtomicInteger());
            this._bits = new long[(int)(n + 63L >>> 6)];
            this._nbsi64 = ((n + 1 >>> 6 == 0) ? null : new NBSI(n + 1 >>> 6, null, null));
            this._sum_bits_length = this._bits.length + ((this._nbsi64 == null) ? 0 : this._nbsi64._sum_bits_length);
        }
        
        private NBSI(final NBSI nbsi, final NBSI nbsi2, final Counter size, final NonBlockingSetInt non_blocking_set_int) {
            this._non_blocking_set_int = non_blocking_set_int;
            this._size = size;
            this._copyIdx = ((size == null) ? null : new AtomicInteger());
            this._copyDone = ((size == null) ? null : new AtomicInteger());
            if (!has_bits(nbsi) && !has_bits(nbsi2)) {
                this._bits = null;
                this._nbsi64 = null;
                this._sum_bits_length = 0;
                return;
            }
            if (!has_bits(nbsi)) {
                this._bits = new long[nbsi2._bits.length];
                this._nbsi64 = new NBSI(null, nbsi2._nbsi64, null, null);
            }
            else if (!has_bits(nbsi2)) {
                this._bits = new long[nbsi._bits.length];
                this._nbsi64 = new NBSI(null, nbsi._nbsi64, null, null);
            }
            else {
                this._bits = new long[(nbsi._bits.length > nbsi2._bits.length) ? nbsi._bits.length : nbsi2._bits.length];
                this._nbsi64 = new NBSI(nbsi._nbsi64, nbsi2._nbsi64, null, null);
            }
            this._sum_bits_length = this._bits.length + this._nbsi64._sum_bits_length;
        }
        
        private static boolean has_bits(final NBSI nbsi) {
            return nbsi != null && nbsi._bits != null;
        }
        
        public boolean add(final int n) {
            if (n >> 6 >= this._bits.length) {
                return this.install_larger_new_bits(n).help_copy().add(n);
            }
            NBSI nbsi64 = this;
            int n2;
            for (n2 = n; (n2 & 0x3F) == 0x3F; n2 >>= 6) {
                nbsi64 = nbsi64._nbsi64;
            }
            final long mask = mask(n2);
            long n3;
            do {
                n3 = nbsi64._bits[n2 >> 6];
                if (n3 < 0L) {
                    return this.help_copy_impl(n).help_copy().add(n);
                }
                if ((n3 & mask) != 0x0L) {
                    return false;
                }
            } while (!nbsi64.CAS(n2 >> 6, n3, n3 | mask));
            this._size.add(1L);
            return true;
        }
        
        public boolean remove(final int n) {
            if (n >> 6 >= this._bits.length) {
                return this._new != null && this.help_copy().remove(n);
            }
            NBSI nbsi64 = this;
            int n2;
            for (n2 = n; (n2 & 0x3F) == 0x3F; n2 >>= 6) {
                nbsi64 = nbsi64._nbsi64;
            }
            final long mask = mask(n2);
            long n3;
            do {
                n3 = nbsi64._bits[n2 >> 6];
                if (n3 < 0L) {
                    return this.help_copy_impl(n).help_copy().remove(n);
                }
                if ((n3 & mask) == 0x0L) {
                    return false;
                }
            } while (!nbsi64.CAS(n2 >> 6, n3, n3 & ~mask));
            this._size.add(-1L);
            return true;
        }
        
        public boolean contains(final int n) {
            if (n >> 6 >= this._bits.length) {
                return this._new != null && this.help_copy().contains(n);
            }
            NBSI nbsi64 = this;
            int n2;
            for (n2 = n; (n2 & 0x3F) == 0x3F; n2 >>= 6) {
                nbsi64 = nbsi64._nbsi64;
            }
            final long mask = mask(n2);
            final long n3 = nbsi64._bits[n2 >> 6];
            if (n3 < 0L) {
                return this.help_copy_impl(n).help_copy().contains(n);
            }
            return (n3 & mask) != 0x0L;
        }
        
        public boolean intersect(final NBSI nbsi, final NBSI nbsi2, final NBSI nbsi3) {
            if (!has_bits(nbsi2) || !has_bits(nbsi3)) {
                return true;
            }
            for (int i = 0; i < nbsi._bits.length; ++i) {
                nbsi._bits[i] = (nbsi2.safe_read_word(i, 0L) & nbsi3.safe_read_word(i, 0L) & Long.MAX_VALUE);
            }
            return this.intersect(nbsi._nbsi64, nbsi2._nbsi64, nbsi3._nbsi64);
        }
        
        public boolean union(final NBSI nbsi, final NBSI nbsi2, final NBSI nbsi3) {
            if (!has_bits(nbsi2) && !has_bits(nbsi3)) {
                return true;
            }
            if (has_bits(nbsi2) || has_bits(nbsi3)) {
                for (int i = 0; i < nbsi._bits.length; ++i) {
                    nbsi._bits[i] = ((nbsi2.safe_read_word(i, 0L) | nbsi3.safe_read_word(i, 0L)) & Long.MAX_VALUE);
                }
            }
            return this.union(nbsi._nbsi64, nbsi2._nbsi64, nbsi3._nbsi64);
        }
        
        private long safe_read_word(final int n, final long n2) {
            if (n >= this._bits.length) {
                return n2;
            }
            long n3 = this._bits[n];
            if (n3 < 0L) {
                n3 = this.help_copy_impl(n).help_copy()._bits[n];
            }
            return n3;
        }
        
        public int sizeInBytes() {
            return this._bits.length;
        }
        
        public int size() {
            return (int)this._size.get();
        }
        
        private NBSI install_larger_new_bits(final int n) {
            if (this._new == null) {
                this.CAS_new(new NBSI(this._bits.length << 6 << 1, this._size, this._non_blocking_set_int));
            }
            return this;
        }
        
        private NBSI help_copy() {
            final NBSI nbsi = this._non_blocking_set_int._nbsi;
            final int andAdd = nbsi._copyIdx.getAndAdd(512);
            for (int i = 0; i < 8; ++i) {
                final int n = (andAdd + i * 64) % (nbsi._bits.length << 6);
                nbsi.help_copy_impl(n);
                nbsi.help_copy_impl(n + 63);
            }
            if (nbsi._copyDone.get() != nbsi._sum_bits_length || this._non_blocking_set_int.CAS_nbsi(nbsi, nbsi._new)) {}
            return this._new;
        }
        
        private NBSI help_copy_impl(final int n) {
            NBSI nbsi64 = this;
            NBSI nbsi65 = this._new;
            if (nbsi65 == null) {
                return this;
            }
            int n2;
            for (n2 = n; (n2 & 0x3F) == 0x3F; n2 >>= 6) {
                nbsi64 = nbsi64._nbsi64;
                nbsi65 = nbsi65._nbsi64;
            }
            long n3 = nbsi64._bits[n2 >> 6];
            while (n3 >= 0L) {
                final long n4 = n3;
                n3 |= mask(63);
                if (nbsi64.CAS(n2 >> 6, n4, n3)) {
                    if (n4 == 0L) {
                        this._copyDone.addAndGet(1);
                        break;
                    }
                    break;
                }
                else {
                    n3 = nbsi64._bits[n2 >> 6];
                }
            }
            if (n3 != mask(63)) {
                if (nbsi65._bits[n2 >> 6] == 0L) {
                    long n5 = n3 & ~mask(63);
                    if (!nbsi65.CAS(n2 >> 6, 0L, n5)) {
                        n5 = nbsi65._bits[n2 >> 6];
                    }
                    assert n5 != 0L;
                }
                if (nbsi64.CAS(n2 >> 6, n3, mask(63))) {
                    this._copyDone.addAndGet(1);
                }
            }
            return this;
        }
        
        private void print(final int n, final String x) {
            for (int i = 0; i < n; ++i) {
                System.out.print("  ");
            }
            System.out.println(x);
        }
        
        private void print(final int n) {
            final StringBuffer sb = new StringBuffer();
            sb.append("NBSI - _bits.len=");
            for (NBSI nbsi64 = this; nbsi64 != null; nbsi64 = nbsi64._nbsi64) {
                sb.append(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, nbsi64._bits.length));
            }
            this.print(n, sb.toString());
            NBSI nbsi65 = this;
            while (nbsi65 != null) {
                for (int i = 0; i < nbsi65._bits.length; ++i) {
                    System.out.print(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Long.toHexString(nbsi65._bits[i])));
                }
                nbsi65 = nbsi65._nbsi64;
                System.out.println();
            }
            if (this._copyIdx.get() != 0 || this._copyDone.get() != 0) {
                this.print(n, invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, this._copyIdx.get(), this._copyDone.get(), this._sum_bits_length));
            }
            if (this._new != null) {
                this.print(n, "__has_new - ");
                this._new.print(n + 1);
            }
        }
        
        static {
            _Lbase = NonBlockingSetInt._unsafe.arrayBaseOffset(long[].class);
            _Lscale = NonBlockingSetInt._unsafe.arrayIndexScale(long[].class);
            Field declaredField = null;
            try {
                declaredField = NBSI.class.getDeclaredField("_new");
            }
            catch (NoSuchFieldException ex) {}
            _new_offset = NonBlockingSetInt._unsafe.objectFieldOffset(declaredField);
        }
    }
}

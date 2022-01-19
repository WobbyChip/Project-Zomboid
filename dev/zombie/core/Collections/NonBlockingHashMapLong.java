// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.Collections;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.lang.reflect.Field;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.AbstractSet;
import java.util.Set;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import sun.misc.Unsafe;
import java.io.Serializable;
import java.util.concurrent.ConcurrentMap;
import java.util.AbstractMap;

public class NonBlockingHashMapLong<TypeV> extends AbstractMap<Long, TypeV> implements ConcurrentMap<Long, TypeV>, Serializable
{
    private static final long serialVersionUID = 1234123412341234124L;
    private static final int REPROBE_LIMIT = 10;
    private static final Unsafe _unsafe;
    private static final int _Obase;
    private static final int _Oscale;
    private static final int _Lbase;
    private static final int _Lscale;
    private static final long _chm_offset;
    private static final long _val_1_offset;
    private transient CHM _chm;
    private transient Object _val_1;
    private transient long _last_resize_milli;
    private final boolean _opt_for_space;
    private static final int MIN_SIZE_LOG = 4;
    private static final int MIN_SIZE = 16;
    private static final Object NO_MATCH_OLD;
    private static final Object MATCH_ANY;
    private static final Object TOMBSTONE;
    private static final Prime TOMBPRIME;
    private static final long NO_KEY = 0L;
    private transient Counter _reprobes;
    
    private static long rawIndex(final Object[] array, final int n) {
        assert n >= 0 && n < array.length;
        return NonBlockingHashMapLong._Obase + n * NonBlockingHashMapLong._Oscale;
    }
    
    private static long rawIndex(final long[] array, final int n) {
        assert n >= 0 && n < array.length;
        return NonBlockingHashMapLong._Lbase + n * NonBlockingHashMapLong._Lscale;
    }
    
    private final boolean CAS(final long offset, final Object expected, final Object x) {
        return NonBlockingHashMapLong._unsafe.compareAndSwapObject(this, offset, expected, x);
    }
    
    public final void print() {
        System.out.println("=========");
        print_impl(-99, 0L, this._val_1);
        this._chm.print();
        System.out.println("=========");
    }
    
    private static final void print_impl(final int n, final long n2, final Object o) {
        final String s = (o instanceof Prime) ? "prime_" : "";
        final Object unbox = Prime.unbox(o);
        System.out.println(invokedynamic(makeConcatWithConstants:(IJLjava/lang/String;Ljava/lang/String;)Ljava/lang/String;, n, n2, s, (unbox == NonBlockingHashMapLong.TOMBSTONE) ? "tombstone" : unbox.toString()));
    }
    
    private final void print2() {
        System.out.println("=========");
        print2_impl(-99, 0L, this._val_1);
        this._chm.print();
        System.out.println("=========");
    }
    
    private static final void print2_impl(final int n, final long n2, final Object o) {
        if (o != null && Prime.unbox(o) != NonBlockingHashMapLong.TOMBSTONE) {
            print_impl(n, n2, o);
        }
    }
    
    public long reprobes() {
        final long value = this._reprobes.get();
        this._reprobes = new Counter();
        return value;
    }
    
    private static final int reprobe_limit(final int n) {
        return 10 + (n >> 2);
    }
    
    public NonBlockingHashMapLong() {
        this(16, true);
    }
    
    public NonBlockingHashMapLong(final int n) {
        this(n, true);
    }
    
    public NonBlockingHashMapLong(final boolean b) {
        this(1, b);
    }
    
    public NonBlockingHashMapLong(final int n, final boolean opt_for_space) {
        this._reprobes = new Counter();
        this._opt_for_space = opt_for_space;
        this.initialize(n);
    }
    
    private final void initialize(final int n) {
        if (n < 0) {
            throw new IllegalArgumentException();
        }
        int n2;
        for (n2 = 4; 1 << n2 < n; ++n2) {}
        this._chm = new CHM(this, new Counter(), n2);
        this._val_1 = NonBlockingHashMapLong.TOMBSTONE;
        this._last_resize_milli = System.currentTimeMillis();
    }
    
    @Override
    public int size() {
        return ((this._val_1 != NonBlockingHashMapLong.TOMBSTONE) ? 1 : 0) + this._chm.size();
    }
    
    public boolean containsKey(final long n) {
        return this.get(n) != null;
    }
    
    public boolean contains(final Object o) {
        return this.containsValue(o);
    }
    
    public TypeV put(final long n, final TypeV typeV) {
        return this.putIfMatch(n, typeV, NonBlockingHashMapLong.NO_MATCH_OLD);
    }
    
    public TypeV putIfAbsent(final long n, final TypeV typeV) {
        return this.putIfMatch(n, typeV, NonBlockingHashMapLong.TOMBSTONE);
    }
    
    public TypeV remove(final long n) {
        return this.putIfMatch(n, NonBlockingHashMapLong.TOMBSTONE, NonBlockingHashMapLong.NO_MATCH_OLD);
    }
    
    public boolean remove(final long n, final Object o) {
        return this.putIfMatch(n, NonBlockingHashMapLong.TOMBSTONE, o) == o;
    }
    
    public TypeV replace(final long n, final TypeV typeV) {
        return this.putIfMatch(n, typeV, NonBlockingHashMapLong.MATCH_ANY);
    }
    
    public boolean replace(final long n, final TypeV typeV, final TypeV typeV2) {
        return this.putIfMatch(n, typeV2, typeV) == typeV;
    }
    
    private final TypeV putIfMatch(final long n, final Object o, final Object o2) {
        if (o2 == null || o == null) {
            throw new NullPointerException();
        }
        if (n == 0L) {
            final Object val_1 = this._val_1;
            if (o2 == NonBlockingHashMapLong.NO_MATCH_OLD || val_1 == o2 || (o2 == NonBlockingHashMapLong.MATCH_ANY && val_1 != NonBlockingHashMapLong.TOMBSTONE) || o2.equals(val_1)) {
                this.CAS(NonBlockingHashMapLong._val_1_offset, val_1, o);
            }
            return (TypeV)((val_1 == NonBlockingHashMapLong.TOMBSTONE) ? null : val_1);
        }
        final Object putIfMatch = this._chm.putIfMatch(n, o, o2);
        assert !(putIfMatch instanceof Prime);
        assert putIfMatch != null;
        return (TypeV)((putIfMatch == NonBlockingHashMapLong.TOMBSTONE) ? null : putIfMatch);
    }
    
    @Override
    public void clear() {
        while (!this.CAS(NonBlockingHashMapLong._chm_offset, this._chm, new CHM(this, new Counter(), 4))) {}
        this.CAS(NonBlockingHashMapLong._val_1_offset, this._val_1, NonBlockingHashMapLong.TOMBSTONE);
    }
    
    @Override
    public boolean containsValue(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this._val_1) {
            return true;
        }
        for (final TypeV next : this.values()) {
            if (next == obj || next.equals(obj)) {
                return true;
            }
        }
        return false;
    }
    
    public final TypeV get(final long n) {
        if (n == 0L) {
            final Object val_1 = this._val_1;
            return (TypeV)((val_1 == NonBlockingHashMapLong.TOMBSTONE) ? null : val_1);
        }
        final Object get_impl = this._chm.get_impl(n);
        assert !(get_impl instanceof Prime);
        assert get_impl != NonBlockingHashMapLong.TOMBSTONE;
        return (TypeV)get_impl;
    }
    
    @Override
    public TypeV get(final Object o) {
        return (o instanceof Long) ? this.get((long)o) : null;
    }
    
    @Override
    public TypeV remove(final Object o) {
        return (o instanceof Long) ? this.remove((long)o) : null;
    }
    
    @Override
    public boolean remove(final Object o, final Object o2) {
        return o instanceof Long && this.remove((long)o, o2);
    }
    
    @Override
    public boolean containsKey(final Object o) {
        return o instanceof Long && this.containsKey((long)o);
    }
    
    @Override
    public TypeV putIfAbsent(final Long n, final TypeV typeV) {
        return this.putIfAbsent((long)n, typeV);
    }
    
    @Override
    public TypeV replace(final Long n, final TypeV typeV) {
        return this.replace((long)n, typeV);
    }
    
    @Override
    public TypeV put(final Long n, final TypeV typeV) {
        return this.put((long)n, typeV);
    }
    
    @Override
    public boolean replace(final Long n, final TypeV typeV, final TypeV typeV2) {
        return this.replace((long)n, typeV, typeV2);
    }
    
    private final void help_copy() {
        final CHM chm = this._chm;
        if (chm._newchm == null) {
            return;
        }
        chm.help_copy_impl(false);
    }
    
    public Enumeration<TypeV> elements() {
        return new SnapshotV();
    }
    
    @Override
    public Collection<TypeV> values() {
        return new AbstractCollection<TypeV>() {
            @Override
            public void clear() {
                NonBlockingHashMapLong.this.clear();
            }
            
            @Override
            public int size() {
                return NonBlockingHashMapLong.this.size();
            }
            
            @Override
            public boolean contains(final Object o) {
                return NonBlockingHashMapLong.this.containsValue(o);
            }
            
            @Override
            public Iterator<TypeV> iterator() {
                return new SnapshotV();
            }
        };
    }
    
    public Enumeration<Long> keys() {
        return new IteratorLong();
    }
    
    @Override
    public Set<Long> keySet() {
        return new AbstractSet<Long>() {
            @Override
            public void clear() {
                NonBlockingHashMapLong.this.clear();
            }
            
            @Override
            public int size() {
                return NonBlockingHashMapLong.this.size();
            }
            
            @Override
            public boolean contains(final Object o) {
                return NonBlockingHashMapLong.this.containsKey(o);
            }
            
            @Override
            public boolean remove(final Object o) {
                return NonBlockingHashMapLong.this.remove(o) != null;
            }
            
            @Override
            public IteratorLong iterator() {
                return new IteratorLong();
            }
        };
    }
    
    @Override
    public Set<Map.Entry<Long, TypeV>> entrySet() {
        return new AbstractSet<Map.Entry<Long, TypeV>>() {
            @Override
            public void clear() {
                NonBlockingHashMapLong.this.clear();
            }
            
            @Override
            public int size() {
                return NonBlockingHashMapLong.this.size();
            }
            
            @Override
            public boolean remove(final Object o) {
                if (!(o instanceof Map.Entry)) {
                    return false;
                }
                final Map.Entry entry = (Map.Entry)o;
                return NonBlockingHashMapLong.this.remove(entry.getKey(), entry.getValue());
            }
            
            @Override
            public boolean contains(final Object o) {
                if (!(o instanceof Map.Entry)) {
                    return false;
                }
                final Map.Entry entry = (Map.Entry)o;
                return NonBlockingHashMapLong.this.get(entry.getKey()).equals(entry.getValue());
            }
            
            @Override
            public Iterator<Map.Entry<Long, TypeV>> iterator() {
                return new SnapshotE();
            }
        };
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        for (final long longValue : this.keySet()) {
            final TypeV value = this.get(longValue);
            objectOutputStream.writeLong(longValue);
            objectOutputStream.writeObject(value);
        }
        objectOutputStream.writeLong(0L);
        objectOutputStream.writeObject(null);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.initialize(16);
        while (true) {
            final long long1 = objectInputStream.readLong();
            final Object object = objectInputStream.readObject();
            if (long1 == 0L && object == null) {
                break;
            }
            this.put(long1, (TypeV)object);
        }
    }
    
    static {
        _unsafe = UtilUnsafe.getUnsafe();
        _Obase = NonBlockingHashMapLong._unsafe.arrayBaseOffset(Object[].class);
        _Oscale = NonBlockingHashMapLong._unsafe.arrayIndexScale(Object[].class);
        _Lbase = NonBlockingHashMapLong._unsafe.arrayBaseOffset(long[].class);
        _Lscale = NonBlockingHashMapLong._unsafe.arrayIndexScale(long[].class);
        Field declaredField;
        try {
            declaredField = NonBlockingHashMapLong.class.getDeclaredField("_chm");
        }
        catch (NoSuchFieldException cause) {
            throw new RuntimeException(cause);
        }
        _chm_offset = NonBlockingHashMapLong._unsafe.objectFieldOffset(declaredField);
        Field declaredField2;
        try {
            declaredField2 = NonBlockingHashMapLong.class.getDeclaredField("_val_1");
        }
        catch (NoSuchFieldException cause2) {
            throw new RuntimeException(cause2);
        }
        _val_1_offset = NonBlockingHashMapLong._unsafe.objectFieldOffset(declaredField2);
        NO_MATCH_OLD = new Object();
        MATCH_ANY = new Object();
        TOMBSTONE = new Object();
        TOMBPRIME = new Prime(NonBlockingHashMapLong.TOMBSTONE);
    }
    
    private static final class Prime
    {
        final Object _V;
        
        Prime(final Object v) {
            this._V = v;
        }
        
        static Object unbox(final Object o) {
            return (o instanceof Prime) ? ((Prime)o)._V : o;
        }
    }
    
    private static final class CHM<TypeV> implements Serializable
    {
        final NonBlockingHashMapLong _nbhml;
        private final Counter _size;
        private final Counter _slots;
        volatile CHM _newchm;
        private static final AtomicReferenceFieldUpdater<CHM, CHM> _newchmUpdater;
        volatile long _resizers;
        private static final AtomicLongFieldUpdater<CHM> _resizerUpdater;
        final long[] _keys;
        final Object[] _vals;
        volatile long _copyIdx;
        private static final AtomicLongFieldUpdater<CHM> _copyIdxUpdater;
        volatile long _copyDone;
        private static final AtomicLongFieldUpdater<CHM> _copyDoneUpdater;
        static final /* synthetic */ boolean $assertionsDisabled;
        
        public int size() {
            return (int)this._size.get();
        }
        
        public int slots() {
            return (int)this._slots.get();
        }
        
        boolean CAS_newchm(final CHM chm) {
            return CHM._newchmUpdater.compareAndSet(this, null, chm);
        }
        
        private final boolean CAS_key(final int n, final long expected, final long x) {
            return NonBlockingHashMapLong._unsafe.compareAndSwapLong(this._keys, NonBlockingHashMapLong.rawIndex(this._keys, n), expected, x);
        }
        
        private final boolean CAS_val(final int n, final Object expected, final Object x) {
            return NonBlockingHashMapLong._unsafe.compareAndSwapObject(this._vals, NonBlockingHashMapLong.rawIndex(this._vals, n), expected, x);
        }
        
        CHM(final NonBlockingHashMapLong nbhml, final Counter size, final int n) {
            this._copyIdx = 0L;
            this._copyDone = 0L;
            this._nbhml = nbhml;
            this._size = size;
            this._slots = new Counter();
            this._keys = new long[1 << n];
            this._vals = new Object[1 << n];
        }
        
        private final void print() {
            for (int i = 0; i < this._keys.length; ++i) {
                final long n = this._keys[i];
                if (n != 0L) {
                    NonBlockingHashMapLong.print_impl(i, n, this._vals[i]);
                }
            }
            final CHM newchm = this._newchm;
            if (newchm != null) {
                System.out.println("----");
                newchm.print();
            }
        }
        
        private final void print2() {
            for (int i = 0; i < this._keys.length; ++i) {
                final long n = this._keys[i];
                if (n != 0L) {
                    NonBlockingHashMapLong.print2_impl(i, n, this._vals[i]);
                }
            }
            final CHM newchm = this._newchm;
            if (newchm != null) {
                System.out.println("----");
                newchm.print2();
            }
        }
        
        private final Object get_impl(final long n) {
            final int length = this._keys.length;
            int n2 = (int)(n & (long)(length - 1));
            int n3 = 0;
            while (true) {
                final long n4 = this._keys[n2];
                final Object o = this._vals[n2];
                if (n4 == 0L) {
                    return null;
                }
                if (n == n4) {
                    if (o instanceof Prime) {
                        return this.copy_slot_and_check(n2, n).get_impl(n);
                    }
                    if (o == NonBlockingHashMapLong.TOMBSTONE) {
                        return null;
                    }
                    final CHM newchm = this._newchm;
                    return o;
                }
                else {
                    if (++n3 >= NonBlockingHashMapLong.reprobe_limit(length)) {
                        return (this._newchm == null) ? null : this.copy_slot_and_check(n2, n).get_impl(n);
                    }
                    n2 = (n2 + 1 & length - 1);
                }
            }
        }
        
        private final Object putIfMatch(final long n, final Object o, final Object o2) {
            assert o != null;
            assert !(o instanceof Prime);
            assert !(o2 instanceof Prime);
            final int length = this._keys.length;
            int n2 = (int)(n & (long)(length - 1));
            int n3 = 0;
            Object obj;
            while (true) {
                obj = this._vals[n2];
                long n4 = this._keys[n2];
                if (n4 == 0L) {
                    if (o == NonBlockingHashMapLong.TOMBSTONE) {
                        return o;
                    }
                    if (this.CAS_key(n2, 0L, n)) {
                        this._slots.add(1L);
                        break;
                    }
                    n4 = this._keys[n2];
                    assert n4 != 0L;
                }
                if (n4 == n) {
                    break;
                }
                if (++n3 >= NonBlockingHashMapLong.reprobe_limit(length)) {
                    final CHM resize = this.resize();
                    if (o2 != null) {
                        this._nbhml.help_copy();
                    }
                    return resize.putIfMatch(n, o, o2);
                }
                n2 = (n2 + 1 & length - 1);
            }
            if (o == obj) {
                return obj;
            }
            if ((obj == null && this.tableFull(n3, length)) || obj instanceof Prime) {
                this.resize();
                return this.copy_slot_and_check(n2, o2).putIfMatch(n, o, o2);
            }
            while (CHM.$assertionsDisabled || !(obj instanceof Prime)) {
                if (o2 != NonBlockingHashMapLong.NO_MATCH_OLD && obj != o2 && (o2 != NonBlockingHashMapLong.MATCH_ANY || obj == NonBlockingHashMapLong.TOMBSTONE || obj == null) && (obj != null || o2 != NonBlockingHashMapLong.TOMBSTONE) && (o2 == null || !o2.equals(obj))) {
                    return obj;
                }
                if (this.CAS_val(n2, obj, o)) {
                    if (o2 != null) {
                        if ((obj == null || obj == NonBlockingHashMapLong.TOMBSTONE) && o != NonBlockingHashMapLong.TOMBSTONE) {
                            this._size.add(1L);
                        }
                        if (obj != null && obj != NonBlockingHashMapLong.TOMBSTONE && o == NonBlockingHashMapLong.TOMBSTONE) {
                            this._size.add(-1L);
                        }
                    }
                    return (obj == null && o2 != null) ? NonBlockingHashMapLong.TOMBSTONE : obj;
                }
                obj = this._vals[n2];
                if (obj instanceof Prime) {
                    return this.copy_slot_and_check(n2, o2).putIfMatch(n, o, o2);
                }
            }
            throw new AssertionError();
        }
        
        private final boolean tableFull(final int n, final int n2) {
            return n >= 10 && this._slots.estimate_get() >= NonBlockingHashMapLong.reprobe_limit(n2);
        }
        
        private final CHM resize() {
            final CHM newchm = this._newchm;
            if (newchm != null) {
                return newchm;
            }
            final int length = this._keys.length;
            int size;
            final int n = size = this.size();
            if (this._nbhml._opt_for_space) {
                if (n >= length >> 1) {
                    size = length << 1;
                }
            }
            else if (n >= length >> 2) {
                size = length << 1;
                if (n >= length >> 1) {
                    size = length << 2;
                }
            }
            final long currentTimeMillis = System.currentTimeMillis();
            if (size <= length && currentTimeMillis <= this._nbhml._last_resize_milli + 10000L) {
                size = length << 1;
            }
            if (size < length) {
                size = length;
            }
            int n2;
            for (n2 = 4; 1 << n2 < size; ++n2) {}
            long n3;
            for (n3 = this._resizers; !CHM._resizerUpdater.compareAndSet(this, n3, n3 + 1L); n3 = this._resizers) {}
            final int n4 = (1 << n2 << 1) + 4 << 3 >> 20;
            if (n3 >= 2L && n4 > 0) {
                final CHM newchm2 = this._newchm;
                if (newchm2 != null) {
                    return newchm2;
                }
                try {
                    Thread.sleep(8 * n4);
                }
                catch (Exception ex) {}
            }
            final CHM newchm3 = this._newchm;
            if (newchm3 != null) {
                return newchm3;
            }
            CHM newchm4 = new CHM(this._nbhml, this._size, n2);
            if (this._newchm != null) {
                return this._newchm;
            }
            if (!this.CAS_newchm(newchm4)) {
                newchm4 = this._newchm;
            }
            return newchm4;
        }
        
        private final void help_copy_impl(final boolean b) {
            final CHM newchm = this._newchm;
            assert newchm != null;
            final int length = this._keys.length;
            final int min = Math.min(length, 1024);
            int n = -1;
            int n2 = -9999;
            while (this._copyDone < length) {
                if (n == -1) {
                    for (n2 = (int)this._copyIdx; n2 < length << 1 && !CHM._copyIdxUpdater.compareAndSet(this, n2, n2 + min); n2 = (int)this._copyIdx) {}
                    if (n2 >= length << 1) {
                        n = n2;
                    }
                }
                int n3 = 0;
                for (int i = 0; i < min; ++i) {
                    if (this.copy_slot(n2 + i & length - 1)) {
                        ++n3;
                    }
                }
                if (n3 > 0) {
                    this.copy_check_and_promote(n3);
                }
                n2 += min;
                if (!b && n == -1) {
                    return;
                }
            }
            this.copy_check_and_promote(0);
        }
        
        private final CHM copy_slot_and_check(final int n, final Object o) {
            assert this._newchm != null;
            if (this.copy_slot(n)) {
                this.copy_check_and_promote(1);
            }
            if (o != null) {
                this._nbhml.help_copy();
            }
            return this._newchm;
        }
        
        private final void copy_check_and_promote(final int n) {
            final int length = this._keys.length;
            long n2 = this._copyDone;
            long n3 = n2 + n;
            assert n3 <= length;
            if (n > 0) {
                while (!CHM._copyDoneUpdater.compareAndSet(this, n2, n3)) {
                    n2 = this._copyDone;
                    n3 = n2 + n;
                    assert n3 <= length;
                }
            }
            if (n3 == length && this._nbhml._chm == this && this._nbhml.CAS(NonBlockingHashMapLong._chm_offset, this, this._newchm)) {
                this._nbhml._last_resize_milli = System.currentTimeMillis();
            }
        }
        
        private boolean copy_slot(final int n) {
            long n2;
            while ((n2 = this._keys[n]) == 0L) {
                this.CAS_key(n, 0L, n + this._keys.length);
            }
            Object o = this._vals[n];
            while (!(o instanceof Prime)) {
                final Prime prime = (o == null || o == NonBlockingHashMapLong.TOMBSTONE) ? NonBlockingHashMapLong.TOMBPRIME : new Prime(o);
                if (this.CAS_val(n, o, prime)) {
                    if (prime == NonBlockingHashMapLong.TOMBPRIME) {
                        return true;
                    }
                    o = prime;
                    break;
                }
                else {
                    o = this._vals[n];
                }
            }
            if (o == NonBlockingHashMapLong.TOMBPRIME) {
                return false;
            }
            final Object v = ((Prime)o)._V;
            assert v != NonBlockingHashMapLong.TOMBSTONE;
            final boolean b = this._newchm.putIfMatch(n2, v, null) == null;
            while (!this.CAS_val(n, o, NonBlockingHashMapLong.TOMBPRIME)) {
                o = this._vals[n];
            }
            return b;
        }
        
        static {
            _newchmUpdater = AtomicReferenceFieldUpdater.newUpdater(CHM.class, CHM.class, "_newchm");
            _resizerUpdater = AtomicLongFieldUpdater.newUpdater(CHM.class, "_resizers");
            _copyIdxUpdater = AtomicLongFieldUpdater.newUpdater(CHM.class, "_copyIdx");
            _copyDoneUpdater = AtomicLongFieldUpdater.newUpdater(CHM.class, "_copyDone");
        }
    }
    
    private class SnapshotV implements Iterator<TypeV>, Enumeration<TypeV>
    {
        final CHM _sschm;
        private int _idx;
        private long _nextK;
        private long _prevK;
        private TypeV _nextV;
        private TypeV _prevV;
        
        public SnapshotV() {
            CHM chm;
            while (true) {
                chm = NonBlockingHashMapLong.this._chm;
                if (chm._newchm == null) {
                    break;
                }
                chm.help_copy_impl(true);
            }
            this._sschm = chm;
            this._idx = -1;
            this.next();
        }
        
        int length() {
            return this._sschm._keys.length;
        }
        
        long key(final int n) {
            return this._sschm._keys[n];
        }
        
        @Override
        public boolean hasNext() {
            return this._nextV != null;
        }
        
        @Override
        public TypeV next() {
            if (this._idx != -1 && this._nextV == null) {
                throw new NoSuchElementException();
            }
            this._prevK = this._nextK;
            this._prevV = this._nextV;
            this._nextV = null;
            if (this._idx == -1) {
                this._idx = 0;
                this._nextK = 0L;
                if ((this._nextV = NonBlockingHashMapLong.this.get(this._nextK)) != null) {
                    return this._prevV;
                }
            }
            while (this._idx < this.length()) {
                this._nextK = this.key(this._idx++);
                if (this._nextK != 0L && (this._nextV = NonBlockingHashMapLong.this.get(this._nextK)) != null) {
                    break;
                }
            }
            return this._prevV;
        }
        
        @Override
        public void remove() {
            if (this._prevV == null) {
                throw new IllegalStateException();
            }
            this._sschm.putIfMatch(this._prevK, NonBlockingHashMapLong.TOMBSTONE, this._prevV);
            this._prevV = null;
        }
        
        @Override
        public TypeV nextElement() {
            return this.next();
        }
        
        @Override
        public boolean hasMoreElements() {
            return this.hasNext();
        }
    }
    
    public class IteratorLong implements Iterator<Long>, Enumeration<Long>
    {
        private final SnapshotV _ss;
        
        public IteratorLong() {
            this._ss = new SnapshotV();
        }
        
        @Override
        public void remove() {
            this._ss.remove();
        }
        
        @Override
        public Long next() {
            this._ss.next();
            return this._ss._prevK;
        }
        
        public long nextLong() {
            this._ss.next();
            return this._ss._prevK;
        }
        
        @Override
        public boolean hasNext() {
            return this._ss.hasNext();
        }
        
        @Override
        public Long nextElement() {
            return this.next();
        }
        
        @Override
        public boolean hasMoreElements() {
            return this.hasNext();
        }
    }
    
    private class NBHMLEntry extends AbstractEntry<Long, TypeV>
    {
        NBHMLEntry(final Long n, final TypeV typeV) {
            super(n, typeV);
        }
        
        @Override
        public TypeV setValue(final TypeV val) {
            if (val == null) {
                throw new NullPointerException();
            }
            this._val = (TypeV)val;
            return NonBlockingHashMapLong.this.put((Long)this._key, val);
        }
    }
    
    private class SnapshotE implements Iterator<Map.Entry<Long, TypeV>>
    {
        final SnapshotV _ss;
        
        public SnapshotE() {
            this._ss = new SnapshotV();
        }
        
        @Override
        public void remove() {
            this._ss.remove();
        }
        
        @Override
        public Map.Entry<Long, TypeV> next() {
            this._ss.next();
            return new NBHMLEntry(this._ss._prevK, this._ss._prevV);
        }
        
        @Override
        public boolean hasNext() {
            return this._ss.hasNext();
        }
    }
}

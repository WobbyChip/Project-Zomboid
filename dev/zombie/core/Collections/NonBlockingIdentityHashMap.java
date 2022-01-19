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
import java.util.AbstractSet;
import java.util.Set;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import sun.misc.Unsafe;
import java.io.Serializable;
import java.util.concurrent.ConcurrentMap;
import java.util.AbstractMap;

public class NonBlockingIdentityHashMap<TypeK, TypeV> extends AbstractMap<TypeK, TypeV> implements ConcurrentMap<TypeK, TypeV>, Cloneable, Serializable
{
    private static final long serialVersionUID = 1234123412341234123L;
    private static final int REPROBE_LIMIT = 10;
    private static final Unsafe _unsafe;
    private static final int _Obase;
    private static final int _Oscale;
    private static final long _kvs_offset;
    private transient Object[] _kvs;
    private transient long _last_resize_milli;
    private static final int MIN_SIZE_LOG = 3;
    private static final int MIN_SIZE = 8;
    private static final Object NO_MATCH_OLD;
    private static final Object MATCH_ANY;
    private static final Object TOMBSTONE;
    private static final Prime TOMBPRIME;
    private transient Counter _reprobes;
    static final /* synthetic */ boolean $assertionsDisabled;
    
    private static long rawIndex(final Object[] array, final int n) {
        assert n >= 0 && n < array.length;
        return NonBlockingIdentityHashMap._Obase + n * NonBlockingIdentityHashMap._Oscale;
    }
    
    private final boolean CAS_kvs(final Object[] expected, final Object[] x) {
        return NonBlockingIdentityHashMap._unsafe.compareAndSwapObject(this, NonBlockingIdentityHashMap._kvs_offset, expected, x);
    }
    
    private static final int hash(final Object o) {
        final int identityHashCode = System.identityHashCode(o);
        final int n = identityHashCode ^ (identityHashCode >>> 20 ^ identityHashCode >>> 12);
        return n ^ (n >>> 7 ^ n >>> 4);
    }
    
    private static final CHM chm(final Object[] array) {
        return (CHM)array[0];
    }
    
    private static final int[] hashes(final Object[] array) {
        return (int[])array[1];
    }
    
    private static final int len(final Object[] array) {
        return array.length - 2 >> 1;
    }
    
    private static final Object key(final Object[] array, final int n) {
        return array[(n << 1) + 2];
    }
    
    private static final Object val(final Object[] array, final int n) {
        return array[(n << 1) + 3];
    }
    
    private static final boolean CAS_key(final Object[] o, final int n, final Object expected, final Object x) {
        return NonBlockingIdentityHashMap._unsafe.compareAndSwapObject(o, rawIndex(o, (n << 1) + 2), expected, x);
    }
    
    private static final boolean CAS_val(final Object[] o, final int n, final Object expected, final Object x) {
        return NonBlockingIdentityHashMap._unsafe.compareAndSwapObject(o, rawIndex(o, (n << 1) + 3), expected, x);
    }
    
    public final void print() {
        System.out.println("=========");
        this.print2(this._kvs);
        System.out.println("=========");
    }
    
    private final void print(final Object[] array) {
        for (int i = 0; i < len(array); ++i) {
            final Object key = key(array, i);
            if (key != null) {
                final String s = (key == NonBlockingIdentityHashMap.TOMBSTONE) ? "XXX" : key.toString();
                final Object val = val(array, i);
                final Object unbox = Prime.unbox(val);
                System.out.println(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, i, s, (val == unbox) ? "" : "prime_", (unbox == NonBlockingIdentityHashMap.TOMBSTONE) ? "tombstone" : unbox.toString()));
            }
        }
        final Object[] newkvs = chm(array)._newkvs;
        if (newkvs != null) {
            System.out.println("----");
            this.print(newkvs);
        }
    }
    
    private final void print2(final Object[] array) {
        for (int i = 0; i < len(array); ++i) {
            final Object key = key(array, i);
            final Object val = val(array, i);
            final Object unbox = Prime.unbox(val);
            if (key != null && key != NonBlockingIdentityHashMap.TOMBSTONE && val != null && unbox != NonBlockingIdentityHashMap.TOMBSTONE) {
                System.out.println(invokedynamic(makeConcatWithConstants:(ILjava/lang/Object;Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/String;, i, key, (val == unbox) ? "" : "prime_", val));
            }
        }
        final Object[] newkvs = chm(array)._newkvs;
        if (newkvs != null) {
            System.out.println("----");
            this.print2(newkvs);
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
    
    public NonBlockingIdentityHashMap() {
        this(8);
    }
    
    public NonBlockingIdentityHashMap(final int n) {
        this._reprobes = new Counter();
        this.initialize(n);
    }
    
    private final void initialize(int n) {
        if (n < 0) {
            throw new IllegalArgumentException();
        }
        if (n > 1048576) {
            n = 1048576;
        }
        int n2;
        for (n2 = 3; 1 << n2 < n << 2; ++n2) {}
        (this._kvs = new Object[(1 << n2 << 1) + 2])[0] = new CHM(new Counter());
        this._kvs[1] = new int[1 << n2];
        this._last_resize_milli = System.currentTimeMillis();
    }
    
    protected final void initialize() {
        this.initialize(8);
    }
    
    @Override
    public int size() {
        return chm(this._kvs).size();
    }
    
    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }
    
    @Override
    public boolean containsKey(final Object o) {
        return this.get(o) != null;
    }
    
    public boolean contains(final Object o) {
        return this.containsValue(o);
    }
    
    @Override
    public TypeV put(final TypeK typeK, final TypeV typeV) {
        return this.putIfMatch(typeK, typeV, NonBlockingIdentityHashMap.NO_MATCH_OLD);
    }
    
    @Override
    public TypeV putIfAbsent(final TypeK typeK, final TypeV typeV) {
        return this.putIfMatch(typeK, typeV, NonBlockingIdentityHashMap.TOMBSTONE);
    }
    
    @Override
    public TypeV remove(final Object o) {
        return this.putIfMatch(o, NonBlockingIdentityHashMap.TOMBSTONE, NonBlockingIdentityHashMap.NO_MATCH_OLD);
    }
    
    @Override
    public boolean remove(final Object o, final Object o2) {
        return this.putIfMatch(o, NonBlockingIdentityHashMap.TOMBSTONE, o2) == o2;
    }
    
    @Override
    public TypeV replace(final TypeK typeK, final TypeV typeV) {
        return this.putIfMatch(typeK, typeV, NonBlockingIdentityHashMap.MATCH_ANY);
    }
    
    @Override
    public boolean replace(final TypeK typeK, final TypeV typeV, final TypeV typeV2) {
        return this.putIfMatch(typeK, typeV2, typeV) == typeV;
    }
    
    private final TypeV putIfMatch(final Object o, final Object o2, final Object o3) {
        if (o3 == null || o2 == null) {
            throw new NullPointerException();
        }
        final Object putIfMatch = putIfMatch(this, this._kvs, o, o2, o3);
        assert !(putIfMatch instanceof Prime);
        assert putIfMatch != null;
        return (TypeV)((putIfMatch == NonBlockingIdentityHashMap.TOMBSTONE) ? null : putIfMatch);
    }
    
    @Override
    public void putAll(final Map<? extends TypeK, ? extends TypeV> map) {
        for (final Map.Entry<? extends TypeK, ? extends TypeV> entry : map.entrySet()) {
            this.put(entry.getKey(), (V)entry.getValue());
        }
    }
    
    @Override
    public void clear() {
        while (!this.CAS_kvs(this._kvs, new NonBlockingIdentityHashMap(8)._kvs)) {}
    }
    
    @Override
    public boolean containsValue(final Object obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        for (final TypeV next : this.values()) {
            if (next == obj || next.equals(obj)) {
                return true;
            }
        }
        return false;
    }
    
    protected void rehash() {
    }
    
    public Object clone() {
        try {
            final NonBlockingIdentityHashMap nonBlockingIdentityHashMap = (NonBlockingIdentityHashMap)super.clone();
            nonBlockingIdentityHashMap.clear();
            for (final TypeK next : this.keySet()) {
                nonBlockingIdentityHashMap.put(next, this.get(next));
            }
            return nonBlockingIdentityHashMap;
        }
        catch (CloneNotSupportedException ex) {
            throw new InternalError();
        }
    }
    
    @Override
    public String toString() {
        final Iterator<Map.Entry<TypeK, TypeV>> iterator = this.entrySet().iterator();
        if (!iterator.hasNext()) {
            return "{}";
        }
        final StringBuilder sb = new StringBuilder();
        sb.append('{');
        while (true) {
            final Map.Entry<TypeK, TypeV> entry = iterator.next();
            final TypeK key = entry.getKey();
            final TypeV value = entry.getValue();
            sb.append((key == this) ? "(this Map)" : key);
            sb.append('=');
            sb.append((value == this) ? "(this Map)" : value);
            if (!iterator.hasNext()) {
                break;
            }
            sb.append(", ");
        }
        return sb.append('}').toString();
    }
    
    @Override
    public TypeV get(final Object o) {
        final Object get_impl = get_impl(this, this._kvs, o, hash(o));
        assert !(get_impl instanceof Prime);
        return (TypeV)get_impl;
    }
    
    private static final Object get_impl(final NonBlockingIdentityHashMap nonBlockingIdentityHashMap, final Object[] array, final Object o, final int n) {
        final int len = len(array);
        final CHM chm = chm(array);
        int n2 = n & len - 1;
        int n3 = 0;
        while (true) {
            final Object key = key(array, n2);
            final Object val = val(array, n2);
            if (key == null) {
                return null;
            }
            final Object[] newkvs = chm._newkvs;
            if (key == o) {
                if (!(val instanceof Prime)) {
                    return (val == NonBlockingIdentityHashMap.TOMBSTONE) ? null : val;
                }
                return get_impl(nonBlockingIdentityHashMap, chm.copy_slot_and_check(nonBlockingIdentityHashMap, array, n2, o), o, n);
            }
            else {
                if (++n3 >= reprobe_limit(len) || o == NonBlockingIdentityHashMap.TOMBSTONE) {
                    return (newkvs == null) ? null : get_impl(nonBlockingIdentityHashMap, nonBlockingIdentityHashMap.help_copy(newkvs), o, n);
                }
                n2 = (n2 + 1 & len - 1);
            }
        }
    }
    
    private static final Object putIfMatch(final NonBlockingIdentityHashMap nonBlockingIdentityHashMap, final Object[] array, final Object o, final Object o2, final Object o3) {
        assert o2 != null;
        assert !(o2 instanceof Prime);
        assert !(o3 instanceof Prime);
        final int hash = hash(o);
        final int len = len(array);
        final CHM chm = chm(array);
        int n = hash & len - 1;
        int n2 = 0;
        Object[] array2 = null;
        Object obj;
        while (true) {
            obj = val(array, n);
            Object o4 = key(array, n);
            if (o4 == null) {
                if (o2 == NonBlockingIdentityHashMap.TOMBSTONE) {
                    return o2;
                }
                if (CAS_key(array, n, null, o)) {
                    chm._slots.add(1L);
                    break;
                }
                o4 = key(array, n);
                assert o4 != null;
            }
            array2 = chm._newkvs;
            if (o4 == o) {
                break;
            }
            if (++n2 >= reprobe_limit(len) || o == NonBlockingIdentityHashMap.TOMBSTONE) {
                final Object[] resize = chm.resize(nonBlockingIdentityHashMap, array);
                if (o3 != null) {
                    nonBlockingIdentityHashMap.help_copy(resize);
                }
                return putIfMatch(nonBlockingIdentityHashMap, resize, o, o2, o3);
            }
            n = (n + 1 & len - 1);
        }
        if (o2 == obj) {
            return obj;
        }
        if (array2 == null && ((obj == null && chm.tableFull(n2, len)) || obj instanceof Prime)) {
            array2 = chm.resize(nonBlockingIdentityHashMap, array);
        }
        if (array2 != null) {
            return putIfMatch(nonBlockingIdentityHashMap, chm.copy_slot_and_check(nonBlockingIdentityHashMap, array, n, o3), o, o2, o3);
        }
        while (NonBlockingIdentityHashMap.$assertionsDisabled || !(obj instanceof Prime)) {
            if (o3 != NonBlockingIdentityHashMap.NO_MATCH_OLD && obj != o3 && (o3 != NonBlockingIdentityHashMap.MATCH_ANY || obj == NonBlockingIdentityHashMap.TOMBSTONE || obj == null) && (obj != null || o3 != NonBlockingIdentityHashMap.TOMBSTONE) && (o3 == null || !o3.equals(obj))) {
                return obj;
            }
            if (CAS_val(array, n, obj, o2)) {
                if (o3 != null) {
                    if ((obj == null || obj == NonBlockingIdentityHashMap.TOMBSTONE) && o2 != NonBlockingIdentityHashMap.TOMBSTONE) {
                        chm._size.add(1L);
                    }
                    if (obj != null && obj != NonBlockingIdentityHashMap.TOMBSTONE && o2 == NonBlockingIdentityHashMap.TOMBSTONE) {
                        chm._size.add(-1L);
                    }
                }
                return (obj == null && o3 != null) ? NonBlockingIdentityHashMap.TOMBSTONE : obj;
            }
            obj = val(array, n);
            if (obj instanceof Prime) {
                return putIfMatch(nonBlockingIdentityHashMap, chm.copy_slot_and_check(nonBlockingIdentityHashMap, array, n, o3), o, o2, o3);
            }
        }
        throw new AssertionError();
    }
    
    private final Object[] help_copy(final Object[] array) {
        final Object[] kvs = this._kvs;
        final CHM chm = chm(kvs);
        if (chm._newkvs == null) {
            return array;
        }
        chm.help_copy_impl(this, kvs, false);
        return array;
    }
    
    public Enumeration<TypeV> elements() {
        return new SnapshotV();
    }
    
    @Override
    public Collection<TypeV> values() {
        return new AbstractCollection<TypeV>() {
            @Override
            public void clear() {
                NonBlockingIdentityHashMap.this.clear();
            }
            
            @Override
            public int size() {
                return NonBlockingIdentityHashMap.this.size();
            }
            
            @Override
            public boolean contains(final Object o) {
                return NonBlockingIdentityHashMap.this.containsValue(o);
            }
            
            @Override
            public Iterator<TypeV> iterator() {
                return new SnapshotV();
            }
        };
    }
    
    public Enumeration<TypeK> keys() {
        return new SnapshotK();
    }
    
    @Override
    public Set<TypeK> keySet() {
        return new AbstractSet<TypeK>() {
            @Override
            public void clear() {
                NonBlockingIdentityHashMap.this.clear();
            }
            
            @Override
            public int size() {
                return NonBlockingIdentityHashMap.this.size();
            }
            
            @Override
            public boolean contains(final Object o) {
                return NonBlockingIdentityHashMap.this.containsKey(o);
            }
            
            @Override
            public boolean remove(final Object o) {
                return NonBlockingIdentityHashMap.this.remove(o) != null;
            }
            
            @Override
            public Iterator<TypeK> iterator() {
                return new SnapshotK();
            }
        };
    }
    
    @Override
    public Set<Map.Entry<TypeK, TypeV>> entrySet() {
        return new AbstractSet<Map.Entry<TypeK, TypeV>>() {
            @Override
            public void clear() {
                NonBlockingIdentityHashMap.this.clear();
            }
            
            @Override
            public int size() {
                return NonBlockingIdentityHashMap.this.size();
            }
            
            @Override
            public boolean remove(final Object o) {
                if (!(o instanceof Map.Entry)) {
                    return false;
                }
                final Map.Entry entry = (Map.Entry)o;
                return NonBlockingIdentityHashMap.this.remove(entry.getKey(), entry.getValue());
            }
            
            @Override
            public boolean contains(final Object o) {
                if (!(o instanceof Map.Entry)) {
                    return false;
                }
                final Map.Entry entry = (Map.Entry)o;
                return NonBlockingIdentityHashMap.this.get(entry.getKey()).equals(entry.getValue());
            }
            
            @Override
            public Iterator<Map.Entry<TypeK, TypeV>> iterator() {
                return new SnapshotE();
            }
        };
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        for (final TypeK next : this.keySet()) {
            final TypeV value = this.get(next);
            objectOutputStream.writeObject(next);
            objectOutputStream.writeObject(value);
        }
        objectOutputStream.writeObject(null);
        objectOutputStream.writeObject(null);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.initialize(8);
        while (true) {
            final Object object = objectInputStream.readObject();
            final Object object2 = objectInputStream.readObject();
            if (object == null) {
                break;
            }
            this.put((TypeK)object, (TypeV)object2);
        }
    }
    
    static {
        _unsafe = UtilUnsafe.getUnsafe();
        _Obase = NonBlockingIdentityHashMap._unsafe.arrayBaseOffset(Object[].class);
        _Oscale = NonBlockingIdentityHashMap._unsafe.arrayIndexScale(Object[].class);
        Field declaredField;
        try {
            declaredField = NonBlockingHashMap.class.getDeclaredField("_kvs");
        }
        catch (NoSuchFieldException cause) {
            throw new RuntimeException(cause);
        }
        _kvs_offset = NonBlockingIdentityHashMap._unsafe.objectFieldOffset(declaredField);
        NO_MATCH_OLD = new Object();
        MATCH_ANY = new Object();
        TOMBSTONE = new Object();
        TOMBPRIME = new Prime(NonBlockingIdentityHashMap.TOMBSTONE);
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
    
    private static final class CHM<TypeK, TypeV>
    {
        private final Counter _size;
        private final Counter _slots;
        volatile Object[] _newkvs;
        private final AtomicReferenceFieldUpdater<CHM, Object[]> _newkvsUpdater;
        volatile long _resizers;
        private static final AtomicLongFieldUpdater<CHM> _resizerUpdater;
        volatile long _copyIdx;
        private static final AtomicLongFieldUpdater<CHM> _copyIdxUpdater;
        volatile long _copyDone;
        private static final AtomicLongFieldUpdater<CHM> _copyDoneUpdater;
        
        public int size() {
            return (int)this._size.get();
        }
        
        public int slots() {
            return (int)this._slots.get();
        }
        
        boolean CAS_newkvs(final Object[] array) {
            while (this._newkvs == null) {
                if (this._newkvsUpdater.compareAndSet(this, null, array)) {
                    return true;
                }
            }
            return false;
        }
        
        CHM(final Counter size) {
            this._newkvsUpdater = (AtomicReferenceFieldUpdater<CHM, Object[]>)AtomicReferenceFieldUpdater.newUpdater(CHM.class, Object[].class, "_newkvs");
            this._copyIdx = 0L;
            this._copyDone = 0L;
            this._size = size;
            this._slots = new Counter();
        }
        
        private final boolean tableFull(final int n, final int n2) {
            return n >= 10 && this._slots.estimate_get() >= NonBlockingIdentityHashMap.reprobe_limit(n2);
        }
        
        private final Object[] resize(final NonBlockingIdentityHashMap nonBlockingIdentityHashMap, final Object[] array) {
            assert NonBlockingIdentityHashMap.chm(array) == this;
            final Object[] newkvs = this._newkvs;
            if (newkvs != null) {
                return newkvs;
            }
            final int len = NonBlockingIdentityHashMap.len(array);
            final int size = this.size();
            int n;
            if ((n = size) >= len >> 2) {
                n = len << 1;
                if (size >= len >> 1) {
                    n = len << 2;
                }
            }
            final long currentTimeMillis = System.currentTimeMillis();
            if (n <= len && currentTimeMillis <= nonBlockingIdentityHashMap._last_resize_milli + 10000L && this._slots.estimate_get() >= size << 1) {
                n = len << 1;
            }
            if (n < len) {
                n = len;
            }
            int n2;
            for (n2 = 3; 1 << n2 < n; ++n2) {}
            long n3;
            for (n3 = this._resizers; !CHM._resizerUpdater.compareAndSet(this, n3, n3 + 1L); n3 = this._resizers) {}
            final int n4 = (1 << n2 << 1) + 4 << 3 >> 20;
            if (n3 >= 2L && n4 > 0) {
                final Object[] newkvs2 = this._newkvs;
                if (newkvs2 != null) {
                    return newkvs2;
                }
                try {
                    Thread.sleep(8 * n4);
                }
                catch (Exception ex) {}
            }
            final Object[] newkvs3 = this._newkvs;
            if (newkvs3 != null) {
                return newkvs3;
            }
            Object[] newkvs4 = new Object[(1 << n2 << 1) + 2];
            newkvs4[0] = new CHM(this._size);
            newkvs4[1] = new int[1 << n2];
            if (this._newkvs != null) {
                return this._newkvs;
            }
            if (this.CAS_newkvs(newkvs4)) {
                nonBlockingIdentityHashMap.rehash();
            }
            else {
                newkvs4 = this._newkvs;
            }
            return newkvs4;
        }
        
        private final void help_copy_impl(final NonBlockingIdentityHashMap nonBlockingIdentityHashMap, final Object[] array, final boolean b) {
            assert NonBlockingIdentityHashMap.chm(array) == this;
            final Object[] newkvs = this._newkvs;
            assert newkvs != null;
            final int len = NonBlockingIdentityHashMap.len(array);
            final int min = Math.min(len, 1024);
            int n = -1;
            int n2 = -9999;
            while (this._copyDone < len) {
                if (n == -1) {
                    for (n2 = (int)this._copyIdx; n2 < len << 1 && !CHM._copyIdxUpdater.compareAndSet(this, n2, n2 + min); n2 = (int)this._copyIdx) {}
                    if (n2 >= len << 1) {
                        n = n2;
                    }
                }
                int n3 = 0;
                for (int i = 0; i < min; ++i) {
                    if (this.copy_slot(nonBlockingIdentityHashMap, n2 + i & len - 1, array, newkvs)) {
                        ++n3;
                    }
                }
                if (n3 > 0) {
                    this.copy_check_and_promote(nonBlockingIdentityHashMap, array, n3);
                }
                n2 += min;
                if (!b && n == -1) {
                    return;
                }
            }
            this.copy_check_and_promote(nonBlockingIdentityHashMap, array, 0);
        }
        
        private final Object[] copy_slot_and_check(final NonBlockingIdentityHashMap nonBlockingIdentityHashMap, final Object[] array, final int n, final Object o) {
            assert NonBlockingIdentityHashMap.chm(array) == this;
            final Object[] newkvs = this._newkvs;
            assert newkvs != null;
            if (this.copy_slot(nonBlockingIdentityHashMap, n, array, this._newkvs)) {
                this.copy_check_and_promote(nonBlockingIdentityHashMap, array, 1);
            }
            return (o == null) ? newkvs : nonBlockingIdentityHashMap.help_copy(newkvs);
        }
        
        private final void copy_check_and_promote(final NonBlockingIdentityHashMap nonBlockingIdentityHashMap, final Object[] array, final int n) {
            assert NonBlockingIdentityHashMap.chm(array) == this;
            final int len = NonBlockingIdentityHashMap.len(array);
            long n2 = this._copyDone;
            assert n2 + n <= len;
            if (n > 0) {
                while (!CHM._copyDoneUpdater.compareAndSet(this, n2, n2 + n)) {
                    n2 = this._copyDone;
                    assert n2 + n <= len;
                }
            }
            if (n2 + n == len && nonBlockingIdentityHashMap._kvs == array && nonBlockingIdentityHashMap.CAS_kvs(array, this._newkvs)) {
                nonBlockingIdentityHashMap._last_resize_milli = System.currentTimeMillis();
            }
        }
        
        private boolean copy_slot(final NonBlockingIdentityHashMap nonBlockingIdentityHashMap, final int n, final Object[] array, final Object[] array2) {
            Object key;
            while ((key = NonBlockingIdentityHashMap.key(array, n)) == null) {
                NonBlockingIdentityHashMap.CAS_key(array, n, null, NonBlockingIdentityHashMap.TOMBSTONE);
            }
            Object o = NonBlockingIdentityHashMap.val(array, n);
            while (!(o instanceof Prime)) {
                final Prime prime = (o == null || o == NonBlockingIdentityHashMap.TOMBSTONE) ? NonBlockingIdentityHashMap.TOMBPRIME : new Prime(o);
                if (NonBlockingIdentityHashMap.CAS_val(array, n, o, prime)) {
                    if (prime == NonBlockingIdentityHashMap.TOMBPRIME) {
                        return true;
                    }
                    o = prime;
                    break;
                }
                else {
                    o = NonBlockingIdentityHashMap.val(array, n);
                }
            }
            if (o == NonBlockingIdentityHashMap.TOMBPRIME) {
                return false;
            }
            final Object v = ((Prime)o)._V;
            assert v != NonBlockingIdentityHashMap.TOMBSTONE;
            final boolean b = NonBlockingIdentityHashMap.putIfMatch(nonBlockingIdentityHashMap, array2, key, v, null) == null;
            while (!NonBlockingIdentityHashMap.CAS_val(array, n, o, NonBlockingIdentityHashMap.TOMBPRIME)) {
                o = NonBlockingIdentityHashMap.val(array, n);
            }
            return b;
        }
        
        static {
            _resizerUpdater = AtomicLongFieldUpdater.newUpdater(CHM.class, "_resizers");
            _copyIdxUpdater = AtomicLongFieldUpdater.newUpdater(CHM.class, "_copyIdx");
            _copyDoneUpdater = AtomicLongFieldUpdater.newUpdater(CHM.class, "_copyDone");
        }
    }
    
    private class SnapshotV implements Iterator<TypeV>, Enumeration<TypeV>
    {
        final Object[] _sskvs;
        private int _idx;
        private Object _nextK;
        private Object _prevK;
        private TypeV _nextV;
        private TypeV _prevV;
        
        public SnapshotV() {
            Object[] kvs;
            while (true) {
                kvs = NonBlockingIdentityHashMap.this._kvs;
                final CHM chm = NonBlockingIdentityHashMap.chm(kvs);
                if (chm._newkvs == null) {
                    break;
                }
                chm.help_copy_impl(NonBlockingIdentityHashMap.this, kvs, true);
            }
            this._sskvs = kvs;
            this.next();
        }
        
        int length() {
            return NonBlockingIdentityHashMap.len(this._sskvs);
        }
        
        Object key(final int n) {
            return NonBlockingIdentityHashMap.key(this._sskvs, n);
        }
        
        @Override
        public boolean hasNext() {
            return this._nextV != null;
        }
        
        @Override
        public TypeV next() {
            if (this._idx != 0 && this._nextV == null) {
                throw new NoSuchElementException();
            }
            this._prevK = this._nextK;
            this._prevV = this._nextV;
            this._nextV = null;
            while (this._idx < this.length()) {
                this._nextK = this.key(this._idx++);
                if (this._nextK != null && this._nextK != NonBlockingIdentityHashMap.TOMBSTONE && (this._nextV = NonBlockingIdentityHashMap.this.get(this._nextK)) != null) {
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
            NonBlockingIdentityHashMap.putIfMatch(NonBlockingIdentityHashMap.this, this._sskvs, this._prevK, NonBlockingIdentityHashMap.TOMBSTONE, this._prevV);
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
    
    private class SnapshotK implements Iterator<TypeK>, Enumeration<TypeK>
    {
        final SnapshotV _ss;
        
        public SnapshotK() {
            this._ss = new SnapshotV();
        }
        
        @Override
        public void remove() {
            this._ss.remove();
        }
        
        @Override
        public TypeK next() {
            this._ss.next();
            return (TypeK)this._ss._prevK;
        }
        
        @Override
        public boolean hasNext() {
            return this._ss.hasNext();
        }
        
        @Override
        public TypeK nextElement() {
            return this.next();
        }
        
        @Override
        public boolean hasMoreElements() {
            return this.hasNext();
        }
    }
    
    private class NBHMEntry extends AbstractEntry<TypeK, TypeV>
    {
        NBHMEntry(final TypeK typeK, final TypeV typeV) {
            super(typeK, typeV);
        }
        
        @Override
        public TypeV setValue(final TypeV val) {
            if (val == null) {
                throw new NullPointerException();
            }
            this._val = (TypeV)val;
            return NonBlockingIdentityHashMap.this.put(this._key, val);
        }
    }
    
    private class SnapshotE implements Iterator<Map.Entry<TypeK, TypeV>>
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
        public Map.Entry<TypeK, TypeV> next() {
            this._ss.next();
            return new NBHMEntry((TypeK)this._ss._prevK, this._ss._prevV);
        }
        
        @Override
        public boolean hasNext() {
            return this._ss.hasNext();
        }
    }
}

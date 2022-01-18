// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util.map;

import java.util.Iterator;
import java.util.AbstractCollection;
import zombie.util.IntIterator;
import zombie.util.set.AbstractIntSet;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import zombie.util.hash.DefaultIntHashFunction;
import zombie.util.hash.Primes;
import zombie.util.util.Exceptions;
import java.util.Collection;
import zombie.util.set.IntSet;
import zombie.util.hash.IntHashFunction;
import java.io.Serializable;

public class IntKeyOpenHashMap<V> extends AbstractIntKeyMap<V> implements IntKeyMap<V>, Cloneable, Serializable
{
    private static final long serialVersionUID = 1L;
    private static final int GROWTH_POLICY_RELATIVE = 0;
    private static final int GROWTH_POLICY_ABSOLUTE = 1;
    private static final int DEFAULT_GROWTH_POLICY = 0;
    public static final double DEFAULT_GROWTH_FACTOR = 1.0;
    public static final int DEFAULT_GROWTH_CHUNK = 10;
    public static final int DEFAULT_CAPACITY = 11;
    public static final double DEFAULT_LOAD_FACTOR = 0.75;
    private IntHashFunction keyhash;
    private int size;
    private transient int[] keys;
    private transient Object[] values;
    private transient byte[] states;
    private static final byte EMPTY = 0;
    private static final byte OCCUPIED = 1;
    private static final byte REMOVED = 2;
    private transient int used;
    private int growthPolicy;
    private double growthFactor;
    private int growthChunk;
    private double loadFactor;
    private int expandAt;
    private transient IntSet ckeys;
    private transient Collection<V> cvalues;
    
    private IntKeyOpenHashMap(final IntHashFunction keyhash, int nextPrime, final int growthPolicy, final double n, final int n2, final double n3) {
        if (keyhash == null) {
            Exceptions.nullArgument("hash function");
        }
        if (nextPrime < 0) {
            Exceptions.negativeArgument("capacity", String.valueOf(nextPrime));
        }
        if (n <= 0.0) {
            Exceptions.negativeOrZeroArgument("growthFactor", String.valueOf(n));
        }
        if (n2 <= 0) {
            Exceptions.negativeOrZeroArgument("growthChunk", String.valueOf(n2));
        }
        if (n3 <= 0.0) {
            Exceptions.negativeOrZeroArgument("loadFactor", String.valueOf(n3));
        }
        this.keyhash = keyhash;
        nextPrime = Primes.nextPrime(nextPrime);
        this.keys = new int[nextPrime];
        this.values = new Object[nextPrime];
        this.states = new byte[nextPrime];
        this.size = 0;
        this.expandAt = (int)Math.round(n3 * nextPrime);
        this.used = 0;
        this.growthPolicy = growthPolicy;
        this.growthFactor = n;
        this.growthChunk = n2;
        this.loadFactor = n3;
    }
    
    private IntKeyOpenHashMap(final int n, final int n2, final double n3, final int n4, final double n5) {
        this(DefaultIntHashFunction.INSTANCE, n, n2, n3, n4, n5);
    }
    
    public IntKeyOpenHashMap() {
        this(11);
    }
    
    public IntKeyOpenHashMap(final IntKeyMap<V> intKeyMap) {
        this();
        this.putAll(intKeyMap);
    }
    
    public IntKeyOpenHashMap(final int n) {
        this(n, 0, 1.0, 10, 0.75);
    }
    
    public IntKeyOpenHashMap(final double n) {
        this(11, 0, 1.0, 10, n);
    }
    
    public IntKeyOpenHashMap(final int n, final double n2) {
        this(n, 0, 1.0, 10, n2);
    }
    
    public IntKeyOpenHashMap(final int n, final double n2, final double n3) {
        this(n, 0, n3, 10, n2);
    }
    
    public IntKeyOpenHashMap(final int n, final double n2, final int n3) {
        this(n, 1, 1.0, n3, n2);
    }
    
    public IntKeyOpenHashMap(final IntHashFunction intHashFunction) {
        this(intHashFunction, 11, 0, 1.0, 10, 0.75);
    }
    
    public IntKeyOpenHashMap(final IntHashFunction intHashFunction, final int n) {
        this(intHashFunction, n, 0, 1.0, 10, 0.75);
    }
    
    public IntKeyOpenHashMap(final IntHashFunction intHashFunction, final double n) {
        this(intHashFunction, 11, 0, 1.0, 10, n);
    }
    
    public IntKeyOpenHashMap(final IntHashFunction intHashFunction, final int n, final double n2) {
        this(intHashFunction, n, 0, 1.0, 10, n2);
    }
    
    public IntKeyOpenHashMap(final IntHashFunction intHashFunction, final int n, final double n2, final double n3) {
        this(intHashFunction, n, 0, n3, 10, n2);
    }
    
    public IntKeyOpenHashMap(final IntHashFunction intHashFunction, final int n, final double n2, final int n3) {
        this(intHashFunction, n, 1, 1.0, n3, n2);
    }
    
    private void ensureCapacity(final int n) {
        if (n >= this.expandAt) {
            int n2;
            if (this.growthPolicy == 0) {
                n2 = (int)(this.keys.length * (1.0 + this.growthFactor));
            }
            else {
                n2 = this.keys.length + this.growthChunk;
            }
            if (n2 * this.loadFactor < n) {
                n2 = (int)Math.round(n / this.loadFactor);
            }
            final int nextPrime = Primes.nextPrime(n2);
            this.expandAt = (int)Math.round(this.loadFactor * nextPrime);
            final int[] keys = new int[nextPrime];
            final Object[] values = new Object[nextPrime];
            final byte[] states = new byte[nextPrime];
            this.used = 0;
            for (int i = 0; i < this.keys.length; ++i) {
                if (this.states[i] == 1) {
                    ++this.used;
                    final int n3 = this.keys[i];
                    final Object o = this.values[i];
                    final int abs = Math.abs(this.keyhash.hash(n3));
                    int n4 = abs % nextPrime;
                    if (states[n4] == 1) {
                        final int n5 = 1 + abs % (nextPrime - 2);
                        do {
                            n4 -= n5;
                            if (n4 < 0) {
                                n4 += nextPrime;
                            }
                        } while (states[n4] != 0);
                    }
                    states[n4] = 1;
                    values[n4] = o;
                    keys[n4] = n3;
                }
            }
            this.keys = keys;
            this.values = values;
            this.states = states;
        }
    }
    
    @Override
    public IntSet keySet() {
        if (this.ckeys == null) {
            this.ckeys = new KeySet();
        }
        return this.ckeys;
    }
    
    @Override
    public V put(final int n, final V v) {
        final int abs = Math.abs(this.keyhash.hash(n));
        int n2 = abs % this.keys.length;
        Label_0166: {
            if (this.states[n2] == 1) {
                if (this.keys[n2] == n) {
                    final Object o = this.values[n2];
                    this.values[n2] = v;
                    return (V)o;
                }
                final int n3 = 1 + abs % (this.keys.length - 2);
                do {
                    n2 -= n3;
                    if (n2 < 0) {
                        n2 += this.keys.length;
                    }
                    if (this.states[n2] == 0) {
                        break Label_0166;
                    }
                    if (this.states[n2] == 2) {
                        break Label_0166;
                    }
                } while (this.states[n2] != 1 || this.keys[n2] != n);
                final Object o2 = this.values[n2];
                this.values[n2] = v;
                return (V)o2;
            }
        }
        if (this.states[n2] == 0) {
            ++this.used;
        }
        this.states[n2] = 1;
        this.keys[n2] = n;
        this.values[n2] = v;
        ++this.size;
        this.ensureCapacity(this.used);
        return null;
    }
    
    @Override
    public Collection<V> values() {
        if (this.cvalues == null) {
            this.cvalues = new ValueCollection();
        }
        return this.cvalues;
    }
    
    public Object clone() {
        try {
            final IntKeyOpenHashMap intKeyOpenHashMap = (IntKeyOpenHashMap)super.clone();
            intKeyOpenHashMap.keys = new int[this.keys.length];
            System.arraycopy(this.keys, 0, intKeyOpenHashMap.keys, 0, this.keys.length);
            intKeyOpenHashMap.values = new Object[this.values.length];
            System.arraycopy(this.values, 0, intKeyOpenHashMap.values, 0, this.values.length);
            intKeyOpenHashMap.states = new byte[this.states.length];
            System.arraycopy(this.states, 0, intKeyOpenHashMap.states, 0, this.states.length);
            intKeyOpenHashMap.cvalues = null;
            intKeyOpenHashMap.ckeys = null;
            return intKeyOpenHashMap;
        }
        catch (CloneNotSupportedException ex) {
            Exceptions.cloning();
            return null;
        }
    }
    
    @Override
    public IntKeyMapIterator<V> entries() {
        return new IntKeyMapIterator<V>() {
            int nextEntry = this.nextEntry(0);
            int lastEntry = -1;
            
            int nextEntry(int n) {
                while (n < IntKeyOpenHashMap.this.keys.length && IntKeyOpenHashMap.this.states[n] != 1) {
                    ++n;
                }
                return n;
            }
            
            @Override
            public boolean hasNext() {
                return this.nextEntry < IntKeyOpenHashMap.this.keys.length;
            }
            
            @Override
            public void next() {
                if (!this.hasNext()) {
                    Exceptions.endOfIterator();
                }
                this.lastEntry = this.nextEntry;
                this.nextEntry = this.nextEntry(this.nextEntry + 1);
            }
            
            @Override
            public void remove() {
                if (this.lastEntry == -1) {
                    Exceptions.noElementToRemove();
                }
                IntKeyOpenHashMap.this.states[this.lastEntry] = 2;
                IntKeyOpenHashMap.this.values[this.lastEntry] = null;
                final IntKeyOpenHashMap this$0 = IntKeyOpenHashMap.this;
                --this$0.size;
                this.lastEntry = -1;
            }
            
            @Override
            public int getKey() {
                if (this.lastEntry == -1) {
                    Exceptions.noElementToGet();
                }
                return IntKeyOpenHashMap.this.keys[this.lastEntry];
            }
            
            @Override
            public V getValue() {
                if (this.lastEntry == -1) {
                    Exceptions.noElementToGet();
                }
                return (V)IntKeyOpenHashMap.this.values[this.lastEntry];
            }
        };
    }
    
    @Override
    public void clear() {
        Arrays.fill(this.states, (byte)0);
        Arrays.fill(this.values, null);
        this.size = 0;
        this.used = 0;
    }
    
    @Override
    public boolean containsKey(final int n) {
        final int abs = Math.abs(this.keyhash.hash(n));
        int n2 = abs % this.keys.length;
        if (this.states[n2] == 0) {
            return false;
        }
        if (this.states[n2] == 1 && this.keys[n2] == n) {
            return true;
        }
        final int n3 = 1 + abs % (this.keys.length - 2);
        do {
            n2 -= n3;
            if (n2 < 0) {
                n2 += this.keys.length;
            }
            if (this.states[n2] == 0) {
                return false;
            }
        } while (this.states[n2] != 1 || this.keys[n2] != n);
        return true;
    }
    
    @Override
    public boolean containsValue(final Object o) {
        if (o == null) {
            for (int i = 0; i < this.states.length; ++i) {
                if (this.states[i] == 1 && this.values[i] == null) {
                    return true;
                }
            }
        }
        else {
            for (int j = 0; j < this.states.length; ++j) {
                if (this.states[j] == 1 && o.equals(this.values[j])) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public V get(final int n) {
        final int abs = Math.abs(this.keyhash.hash(n));
        int n2 = abs % this.keys.length;
        if (this.states[n2] == 0) {
            return null;
        }
        if (this.states[n2] == 1 && this.keys[n2] == n) {
            return (V)this.values[n2];
        }
        final int n3 = 1 + abs % (this.keys.length - 2);
        do {
            n2 -= n3;
            if (n2 < 0) {
                n2 += this.keys.length;
            }
            if (this.states[n2] == 0) {
                return null;
            }
        } while (this.states[n2] != 1 || this.keys[n2] != n);
        return (V)this.values[n2];
    }
    
    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }
    
    @Override
    public V remove(final int n) {
        final int abs = Math.abs(this.keyhash.hash(n));
        int n2 = abs % this.keys.length;
        if (this.states[n2] == 0) {
            return null;
        }
        if (this.states[n2] == 1 && this.keys[n2] == n) {
            final Object o = this.values[n2];
            this.values[n2] = null;
            this.states[n2] = 2;
            --this.size;
            return (V)o;
        }
        final int n3 = 1 + abs % (this.keys.length - 2);
        do {
            n2 -= n3;
            if (n2 < 0) {
                n2 += this.keys.length;
            }
            if (this.states[n2] == 0) {
                return null;
            }
        } while (this.states[n2] != 1 || this.keys[n2] != n);
        final Object o2 = this.values[n2];
        this.values[n2] = null;
        this.states[n2] = 2;
        --this.size;
        return (V)o2;
    }
    
    @Override
    public int size() {
        return this.size;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeInt(this.keys.length);
        final IntKeyMapIterator<Object> entries = (IntKeyMapIterator<Object>)this.entries();
        while (entries.hasNext()) {
            entries.next();
            objectOutputStream.writeInt(entries.getKey());
            objectOutputStream.writeObject(entries.getValue());
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.keys = new int[objectInputStream.readInt()];
        this.states = new byte[this.keys.length];
        this.values = new Object[this.keys.length];
        this.used = this.size;
        for (int i = 0; i < this.size; ++i) {
            final int int1 = objectInputStream.readInt();
            final Object object = objectInputStream.readObject();
            final int abs = Math.abs(this.keyhash.hash(int1));
            int n = abs % this.keys.length;
            if (this.states[n] != 0) {
                final int n2 = 1 + abs % (this.keys.length - 2);
                do {
                    n -= n2;
                    if (n < 0) {
                        n += this.keys.length;
                    }
                } while (this.states[n] != 0);
            }
            this.states[n] = 1;
            this.keys[n] = int1;
            this.values[n] = object;
        }
    }
    
    private class KeySet extends AbstractIntSet
    {
        @Override
        public void clear() {
            IntKeyOpenHashMap.this.clear();
        }
        
        @Override
        public boolean contains(final int n) {
            return IntKeyOpenHashMap.this.containsKey(n);
        }
        
        @Override
        public IntIterator iterator() {
            return new IntIterator() {
                int nextEntry = this.nextEntry(0);
                int lastEntry = -1;
                
                int nextEntry(int n) {
                    while (n < IntKeyOpenHashMap.this.keys.length && IntKeyOpenHashMap.this.states[n] != 1) {
                        ++n;
                    }
                    return n;
                }
                
                @Override
                public boolean hasNext() {
                    return this.nextEntry < IntKeyOpenHashMap.this.keys.length;
                }
                
                @Override
                public int next() {
                    if (!this.hasNext()) {
                        Exceptions.endOfIterator();
                    }
                    this.lastEntry = this.nextEntry;
                    this.nextEntry = this.nextEntry(this.nextEntry + 1);
                    return IntKeyOpenHashMap.this.keys[this.lastEntry];
                }
                
                @Override
                public void remove() {
                    if (this.lastEntry == -1) {
                        Exceptions.noElementToRemove();
                    }
                    IntKeyOpenHashMap.this.states[this.lastEntry] = 2;
                    IntKeyOpenHashMap.this.values[this.lastEntry] = null;
                    final IntKeyOpenHashMap this$0 = IntKeyOpenHashMap.this;
                    --this$0.size;
                    this.lastEntry = -1;
                }
            };
        }
        
        @Override
        public boolean remove(final int n) {
            final boolean containsKey = IntKeyOpenHashMap.this.containsKey(n);
            if (containsKey) {
                IntKeyOpenHashMap.this.remove(n);
            }
            return containsKey;
        }
        
        @Override
        public int size() {
            return IntKeyOpenHashMap.this.size;
        }
    }
    
    private class ValueCollection extends AbstractCollection<V>
    {
        @Override
        public void clear() {
            IntKeyOpenHashMap.this.clear();
        }
        
        @Override
        public boolean contains(final Object o) {
            return IntKeyOpenHashMap.this.containsValue(o);
        }
        
        @Override
        public Iterator<V> iterator() {
            return new Iterator<V>() {
                int nextEntry = this.nextEntry(0);
                int lastEntry = -1;
                
                int nextEntry(int n) {
                    while (n < IntKeyOpenHashMap.this.keys.length && IntKeyOpenHashMap.this.states[n] != 1) {
                        ++n;
                    }
                    return n;
                }
                
                @Override
                public boolean hasNext() {
                    return this.nextEntry < IntKeyOpenHashMap.this.keys.length;
                }
                
                @Override
                public V next() {
                    if (!this.hasNext()) {
                        Exceptions.endOfIterator();
                    }
                    this.lastEntry = this.nextEntry;
                    this.nextEntry = this.nextEntry(this.nextEntry + 1);
                    return (V)IntKeyOpenHashMap.this.values[this.lastEntry];
                }
                
                @Override
                public void remove() {
                    if (this.lastEntry == -1) {
                        Exceptions.noElementToRemove();
                    }
                    IntKeyOpenHashMap.this.states[this.lastEntry] = 2;
                    IntKeyOpenHashMap.this.values[this.lastEntry] = null;
                    final IntKeyOpenHashMap this$0 = IntKeyOpenHashMap.this;
                    --this$0.size;
                    this.lastEntry = -1;
                }
            };
        }
        
        @Override
        public int size() {
            return IntKeyOpenHashMap.this.size;
        }
    }
}

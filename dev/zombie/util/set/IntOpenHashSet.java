// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util.set;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import zombie.util.IntIterator;
import zombie.util.IntCollection;
import zombie.util.hash.DefaultIntHashFunction;
import zombie.util.hash.Primes;
import zombie.util.util.Exceptions;
import zombie.util.hash.IntHashFunction;
import java.io.Serializable;

public class IntOpenHashSet extends AbstractIntSet implements IntSet, Cloneable, Serializable
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
    private transient int[] data;
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
    
    private IntOpenHashSet(final IntHashFunction keyhash, int nextPrime, final int growthPolicy, final double n, final int n2, final double n3) {
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
        this.data = new int[nextPrime];
        this.states = new byte[nextPrime];
        this.size = 0;
        this.expandAt = (int)Math.round(n3 * nextPrime);
        this.used = 0;
        this.growthPolicy = growthPolicy;
        this.growthFactor = n;
        this.growthChunk = n2;
        this.loadFactor = n3;
    }
    
    private IntOpenHashSet(final int n, final int n2, final double n3, final int n4, final double n5) {
        this(DefaultIntHashFunction.INSTANCE, n, n2, n3, n4, n5);
    }
    
    public IntOpenHashSet() {
        this(11);
    }
    
    public IntOpenHashSet(final IntCollection collection) {
        this();
        this.addAll(collection);
    }
    
    public IntOpenHashSet(final int[] array) {
        this();
        for (int length = array.length, i = 0; i < length; ++i) {
            this.add(array[i]);
        }
    }
    
    public IntOpenHashSet(final int n) {
        this(n, 0, 1.0, 10, 0.75);
    }
    
    public IntOpenHashSet(final double n) {
        this(11, 0, 1.0, 10, n);
    }
    
    public IntOpenHashSet(final int n, final double n2) {
        this(n, 0, 1.0, 10, n2);
    }
    
    public IntOpenHashSet(final int n, final double n2, final double n3) {
        this(n, 0, n3, 10, n2);
    }
    
    public IntOpenHashSet(final int n, final double n2, final int n3) {
        this(n, 1, 1.0, n3, n2);
    }
    
    public IntOpenHashSet(final IntHashFunction intHashFunction) {
        this(intHashFunction, 11, 0, 1.0, 10, 0.75);
    }
    
    public IntOpenHashSet(final IntHashFunction intHashFunction, final int n) {
        this(intHashFunction, n, 0, 1.0, 10, 0.75);
    }
    
    public IntOpenHashSet(final IntHashFunction intHashFunction, final double n) {
        this(intHashFunction, 11, 0, 1.0, 10, n);
    }
    
    public IntOpenHashSet(final IntHashFunction intHashFunction, final int n, final double n2) {
        this(intHashFunction, n, 0, 1.0, 10, n2);
    }
    
    public IntOpenHashSet(final IntHashFunction intHashFunction, final int n, final double n2, final double n3) {
        this(intHashFunction, n, 0, n3, 10, n2);
    }
    
    public IntOpenHashSet(final IntHashFunction intHashFunction, final int n, final double n2, final int n3) {
        this(intHashFunction, n, 1, 1.0, n3, n2);
    }
    
    private void ensureCapacity(final int n) {
        if (n >= this.expandAt) {
            int n2;
            if (this.growthPolicy == 0) {
                n2 = (int)(this.data.length * (1.0 + this.growthFactor));
            }
            else {
                n2 = this.data.length + this.growthChunk;
            }
            if (n2 * this.loadFactor < n) {
                n2 = (int)Math.round(n / this.loadFactor);
            }
            final int nextPrime = Primes.nextPrime(n2);
            this.expandAt = (int)Math.round(this.loadFactor * nextPrime);
            final int[] data = new int[nextPrime];
            final byte[] states = new byte[nextPrime];
            this.used = 0;
            for (int i = 0; i < this.data.length; ++i) {
                if (this.states[i] == 1) {
                    ++this.used;
                    final int n3 = this.data[i];
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
                    data[n4] = n3;
                }
            }
            this.data = data;
            this.states = states;
        }
    }
    
    @Override
    public boolean add(final int n) {
        this.ensureCapacity(this.used + 1);
        final int abs = Math.abs(this.keyhash.hash(n));
        int n2 = abs % this.data.length;
        Label_0128: {
            if (this.states[n2] == 1) {
                if (this.data[n2] == n) {
                    return false;
                }
                final int n3 = 1 + abs % (this.data.length - 2);
                do {
                    n2 -= n3;
                    if (n2 < 0) {
                        n2 += this.data.length;
                    }
                    if (this.states[n2] == 0) {
                        break Label_0128;
                    }
                    if (this.states[n2] == 2) {
                        break Label_0128;
                    }
                } while (this.states[n2] != 1 || this.data[n2] != n);
                return false;
            }
        }
        if (this.states[n2] == 0) {
            ++this.used;
        }
        this.states[n2] = 1;
        this.data[n2] = n;
        ++this.size;
        return true;
    }
    
    @Override
    public IntIterator iterator() {
        return new IntIterator() {
            int nextEntry = this.nextEntry(0);
            int lastEntry = -1;
            
            int nextEntry(int n) {
                while (n < IntOpenHashSet.this.data.length && IntOpenHashSet.this.states[n] != 1) {
                    ++n;
                }
                return n;
            }
            
            @Override
            public boolean hasNext() {
                return this.nextEntry < IntOpenHashSet.this.data.length;
            }
            
            @Override
            public int next() {
                if (!this.hasNext()) {
                    Exceptions.endOfIterator();
                }
                this.lastEntry = this.nextEntry;
                this.nextEntry = this.nextEntry(this.nextEntry + 1);
                return IntOpenHashSet.this.data[this.lastEntry];
            }
            
            @Override
            public void remove() {
                if (this.lastEntry == -1) {
                    Exceptions.noElementToRemove();
                }
                IntOpenHashSet.this.states[this.lastEntry] = 2;
                final IntOpenHashSet this$0 = IntOpenHashSet.this;
                --this$0.size;
                this.lastEntry = -1;
            }
        };
    }
    
    @Override
    public void trimToSize() {
    }
    
    public Object clone() {
        try {
            final IntOpenHashSet set = (IntOpenHashSet)super.clone();
            set.data = new int[this.data.length];
            System.arraycopy(this.data, 0, set.data, 0, this.data.length);
            set.states = new byte[this.data.length];
            System.arraycopy(this.states, 0, set.states, 0, this.states.length);
            return set;
        }
        catch (CloneNotSupportedException ex) {
            Exceptions.cloning();
            throw new RuntimeException();
        }
    }
    
    @Override
    public int size() {
        return this.size;
    }
    
    @Override
    public void clear() {
        this.size = 0;
        this.used = 0;
        Arrays.fill(this.states, (byte)0);
    }
    
    @Override
    public boolean contains(final int n) {
        final int abs = Math.abs(this.keyhash.hash(n));
        int n2 = abs % this.data.length;
        if (this.states[n2] == 0) {
            return false;
        }
        if (this.states[n2] == 1 && this.data[n2] == n) {
            return true;
        }
        final int n3 = 1 + abs % (this.data.length - 2);
        do {
            n2 -= n3;
            if (n2 < 0) {
                n2 += this.data.length;
            }
            if (this.states[n2] == 0) {
                return false;
            }
        } while (this.states[n2] != 1 || this.data[n2] != n);
        return true;
    }
    
    @Override
    public int hashCode() {
        int n = 0;
        for (int i = 0; i < this.data.length; ++i) {
            if (this.states[i] == 1) {
                n += this.data[i];
            }
        }
        return n;
    }
    
    @Override
    public boolean remove(final int n) {
        final int abs = Math.abs(this.keyhash.hash(n));
        int n2 = abs % this.data.length;
        if (this.states[n2] == 0) {
            return false;
        }
        if (this.states[n2] == 1 && this.data[n2] == n) {
            this.states[n2] = 2;
            --this.size;
            return true;
        }
        final int n3 = 1 + abs % (this.data.length - 2);
        do {
            n2 -= n3;
            if (n2 < 0) {
                n2 += this.data.length;
            }
            if (this.states[n2] == 0) {
                return false;
            }
        } while (this.states[n2] != 1 || this.data[n2] != n);
        this.states[n2] = 2;
        --this.size;
        return true;
    }
    
    @Override
    public int[] toArray(int[] array) {
        if (array == null || array.length < this.size) {
            array = new int[this.size];
        }
        int n = 0;
        for (int i = 0; i < this.data.length; ++i) {
            if (this.states[i] == 1) {
                array[n++] = this.data[i];
            }
        }
        return array;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeInt(this.data.length);
        final IntIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            objectOutputStream.writeInt(iterator.next());
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.data = new int[objectInputStream.readInt()];
        this.states = new byte[this.data.length];
        this.used = this.size;
        for (int i = 0; i < this.size; ++i) {
            final int int1 = objectInputStream.readInt();
            final int abs = Math.abs(this.keyhash.hash(int1));
            int n = abs % this.data.length;
            if (this.states[n] == 1) {
                final int n2 = 1 + abs % (this.data.length - 2);
                do {
                    n -= n2;
                    if (n < 0) {
                        n += this.data.length;
                    }
                } while (this.states[n] != 0);
            }
            this.states[n] = 1;
            this.data[n] = int1;
        }
    }
}

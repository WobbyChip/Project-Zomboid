// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util.list;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import zombie.util.hash.DefaultIntHashFunction;
import zombie.util.IntCollection;
import zombie.util.util.Exceptions;
import java.io.Serializable;

public class IntArrayList extends AbstractIntList implements Cloneable, Serializable
{
    private static final long serialVersionUID = 1L;
    private static final int GROWTH_POLICY_RELATIVE = 0;
    private static final int GROWTH_POLICY_ABSOLUTE = 1;
    private static final int DEFAULT_GROWTH_POLICY = 0;
    public static final double DEFAULT_GROWTH_FACTOR = 1.0;
    public static final int DEFAULT_GROWTH_CHUNK = 10;
    public static final int DEFAULT_CAPACITY = 10;
    private transient int[] data;
    private int size;
    private int growthPolicy;
    private double growthFactor;
    private int growthChunk;
    
    private IntArrayList(final int i, final int growthPolicy, final double n, final int n2) {
        if (i < 0) {
            Exceptions.negativeArgument("capacity", String.valueOf(i));
        }
        if (n < 0.0) {
            Exceptions.negativeArgument("growthFactor", String.valueOf(n));
        }
        if (n2 < 0) {
            Exceptions.negativeArgument("growthChunk", String.valueOf(n2));
        }
        this.data = new int[i];
        this.size = 0;
        this.growthPolicy = growthPolicy;
        this.growthFactor = n;
        this.growthChunk = n2;
    }
    
    public IntArrayList() {
        this(10);
    }
    
    public IntArrayList(final IntCollection collection) {
        this(collection.size());
        this.addAll(collection);
    }
    
    public IntArrayList(final int[] array) {
        this(array.length);
        System.arraycopy(array, 0, this.data, 0, array.length);
        this.size = array.length;
    }
    
    public IntArrayList(final int n) {
        this(n, 1.0);
    }
    
    public IntArrayList(final int n, final double n2) {
        this(n, 0, n2, 10);
    }
    
    public IntArrayList(final int n, final int n2) {
        this(n, 1, 1.0, n2);
    }
    
    private int computeCapacity(final int n) {
        int n2;
        if (this.growthPolicy == 0) {
            n2 = (int)(this.data.length * (1.0 + this.growthFactor));
        }
        else {
            n2 = this.data.length + this.growthChunk;
        }
        if (n2 < n) {
            n2 = n;
        }
        return n2;
    }
    
    public int ensureCapacity(int computeCapacity) {
        if (computeCapacity > this.data.length) {
            final int[] data = new int[computeCapacity = this.computeCapacity(computeCapacity)];
            System.arraycopy(this.data, 0, data, 0, this.size);
            this.data = data;
        }
        return computeCapacity;
    }
    
    public int capacity() {
        return this.data.length;
    }
    
    @Override
    public void add(final int n, final int n2) {
        if (n < 0 || n > this.size) {
            Exceptions.indexOutOfBounds(n, 0, this.size);
        }
        this.ensureCapacity(this.size + 1);
        final int n3 = this.size - n;
        if (n3 > 0) {
            System.arraycopy(this.data, n, this.data, n + 1, n3);
        }
        this.data[n] = n2;
        ++this.size;
    }
    
    @Override
    public int get(final int n) {
        if (n < 0 || n >= this.size) {
            Exceptions.indexOutOfBounds(n, 0, this.size - 1);
        }
        return this.data[n];
    }
    
    @Override
    public int set(final int n, final int n2) {
        if (n < 0 || n >= this.size) {
            Exceptions.indexOutOfBounds(n, 0, this.size - 1);
        }
        final int n3 = this.data[n];
        this.data[n] = n2;
        return n3;
    }
    
    @Override
    public int removeElementAt(final int n) {
        if (n < 0 || n >= this.size) {
            Exceptions.indexOutOfBounds(n, 0, this.size - 1);
        }
        final int n2 = this.data[n];
        final int n3 = this.size - (n + 1);
        if (n3 > 0) {
            System.arraycopy(this.data, n + 1, this.data, n, n3);
        }
        --this.size;
        return n2;
    }
    
    @Override
    public void trimToSize() {
        if (this.data.length > this.size) {
            final int[] data = new int[this.size];
            System.arraycopy(this.data, 0, data, 0, this.size);
            this.data = data;
        }
    }
    
    public Object clone() {
        try {
            final IntArrayList list = (IntArrayList)super.clone();
            list.data = new int[this.data.length];
            System.arraycopy(this.data, 0, list.data, 0, this.size);
            return list;
        }
        catch (CloneNotSupportedException ex) {
            Exceptions.cloning();
            return null;
        }
    }
    
    @Override
    public int size() {
        return this.size;
    }
    
    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }
    
    @Override
    public void clear() {
        this.size = 0;
    }
    
    @Override
    public boolean contains(final int n) {
        for (int i = 0; i < this.size; ++i) {
            if (this.data[i] == n) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int indexOf(final int n) {
        for (int i = 0; i < this.size; ++i) {
            if (this.data[i] == n) {
                return i;
            }
        }
        return -1;
    }
    
    @Override
    public int indexOf(final int n, final int n2) {
        if (n < 0 || n > this.size) {
            Exceptions.indexOutOfBounds(n, 0, this.size);
        }
        for (int i = n; i < this.size; ++i) {
            if (this.data[i] == n2) {
                return i;
            }
        }
        return -1;
    }
    
    @Override
    public int lastIndexOf(final int n) {
        for (int i = this.size - 1; i >= 0; --i) {
            if (this.data[i] == n) {
                return i;
            }
        }
        return -1;
    }
    
    @Override
    public boolean remove(final int n) {
        final int index = this.indexOf(n);
        if (index != -1) {
            this.removeElementAt(index);
            return true;
        }
        return false;
    }
    
    @Override
    public int[] toArray() {
        final int[] array = new int[this.size];
        System.arraycopy(this.data, 0, array, 0, this.size);
        return array;
    }
    
    @Override
    public int[] toArray(int[] array) {
        if (array == null || array.length < this.size) {
            array = new int[this.size];
        }
        System.arraycopy(this.data, 0, array, 0, this.size);
        return array;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IntList)) {
            return false;
        }
        int n = 0;
        final IntListIterator listIterator = ((IntList)o).listIterator();
        while (n < this.size && listIterator.hasNext()) {
            if (this.data[n++] != listIterator.next()) {
                return false;
            }
        }
        return n >= this.size && !listIterator.hasNext();
    }
    
    @Override
    public int hashCode() {
        int n = 1;
        for (int i = 0; i < this.size; ++i) {
            n = 31 * n + DefaultIntHashFunction.INSTANCE.hash(this.data[i]);
        }
        return n;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeInt(this.data.length);
        for (int i = 0; i < this.size; ++i) {
            objectOutputStream.writeInt(this.data[i]);
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.data = new int[objectInputStream.readInt()];
        for (int i = 0; i < this.size; ++i) {
            this.data[i] = objectInputStream.readInt();
        }
    }
}

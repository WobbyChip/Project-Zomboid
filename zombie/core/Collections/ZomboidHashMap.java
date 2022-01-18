// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.Collections;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;
import java.io.Serializable;
import java.util.Map;

public class ZomboidHashMap<K, V> extends ZomboidAbstractMap<K, V> implements Map<K, V>, Cloneable, Serializable
{
    static final int DEFAULT_INITIAL_CAPACITY = 16;
    static final int MAXIMUM_CAPACITY = 1073741824;
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    transient Entry[] table;
    transient int size;
    int threshold;
    final float loadFactor;
    transient volatile int modCount;
    Stack<Entry<K, V>> entryStore;
    private transient Set<Map.Entry<K, V>> entrySet;
    private static final long serialVersionUID = 362498820763181265L;
    
    public ZomboidHashMap(int n, final float n2) {
        this.entryStore = new Stack<Entry<K, V>>();
        this.entrySet = null;
        if (n < 0) {
            throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n));
        }
        if (n > 1073741824) {
            n = 1073741824;
        }
        if (n2 <= 0.0f || Float.isNaN(n2)) {
            throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, n2));
        }
        int i;
        for (i = 1; i < n; i <<= 1) {}
        for (int j = 0; j < 100; ++j) {
            this.entryStore.add(new Entry<K, V>(0, null, null, null));
        }
        this.loadFactor = n2;
        this.threshold = (int)(i * n2);
        this.table = new Entry[i];
        this.init();
    }
    
    public ZomboidHashMap(final int n) {
        this(n, 0.75f);
    }
    
    public ZomboidHashMap() {
        this.entryStore = new Stack<Entry<K, V>>();
        this.entrySet = null;
        this.loadFactor = 0.75f;
        this.threshold = 12;
        this.table = new Entry[16];
        this.init();
    }
    
    public ZomboidHashMap(final Map<? extends K, ? extends V> map) {
        this(Math.max((int)(map.size() / 0.75f) + 1, 16), 0.75f);
        this.putAllForCreate(map);
    }
    
    void init() {
    }
    
    static int hash(int n) {
        n ^= (n >>> 20 ^ n >>> 12);
        return n ^ n >>> 7 ^ n >>> 4;
    }
    
    static int indexFor(final int n, final int n2) {
        return n & n2 - 1;
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
    public V get(final Object o) {
        if (o == null) {
            return this.getForNullKey();
        }
        final int hash = hash(o.hashCode());
        for (Entry next = this.table[indexFor(hash, this.table.length)]; next != null; next = next.next) {
            final K key;
            if (next.hash == hash && ((key = next.key) == o || o.equals(key))) {
                return (V)next.value;
            }
        }
        return null;
    }
    
    private V getForNullKey() {
        for (Entry next = this.table[0]; next != null; next = next.next) {
            if (next.key == null) {
                return (V)next.value;
            }
        }
        return null;
    }
    
    @Override
    public boolean containsKey(final Object o) {
        return this.getEntry(o) != null;
    }
    
    final Entry<K, V> getEntry(final Object o) {
        final int n = (o == null) ? 0 : hash(o.hashCode());
        for (Entry<K, V> next = (Entry<K, V>)this.table[indexFor(n, this.table.length)]; next != null; next = next.next) {
            final K key;
            if (next.hash == n && ((key = next.key) == o || (o != null && o.equals(key)))) {
                return next;
            }
        }
        return null;
    }
    
    @Override
    public V put(final K k, final V value) {
        if (k == null) {
            return this.putForNullKey(value);
        }
        final int hash = hash(k.hashCode());
        final int index = indexFor(hash, this.table.length);
        for (Entry next = this.table[index]; next != null; next = next.next) {
            final K key;
            if (next.hash == hash && ((key = next.key) == k || k.equals(key))) {
                final V value2 = next.value;
                next.value = (V)value;
                next.recordAccess((ZomboidHashMap)this);
                return (V)value2;
            }
        }
        ++this.modCount;
        this.addEntry(hash, k, value, index);
        return null;
    }
    
    private V putForNullKey(final V value) {
        for (Entry next = this.table[0]; next != null; next = next.next) {
            if (next.key == null) {
                final V value2 = next.value;
                next.value = (V)value;
                next.recordAccess((ZomboidHashMap)this);
                return (V)value2;
            }
        }
        ++this.modCount;
        this.addEntry(0, null, value, 0);
        return null;
    }
    
    private void putForCreate(final K k, final V value) {
        final int n = (k == null) ? 0 : hash(k.hashCode());
        final int index = indexFor(n, this.table.length);
        for (Entry next = this.table[index]; next != null; next = next.next) {
            final K key;
            if (next.hash == n && ((key = next.key) == k || (k != null && k.equals(key)))) {
                next.value = (V)value;
                return;
            }
        }
        this.createEntry(n, k, value, index);
    }
    
    private void putAllForCreate(final Map<? extends K, ? extends V> map) {
        for (final Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            this.putForCreate(entry.getKey(), (V)entry.getValue());
        }
    }
    
    void resize(final int n) {
        if (this.table.length == 1073741824) {
            this.threshold = Integer.MAX_VALUE;
            return;
        }
        final Entry[] table = new Entry[n];
        this.transfer(table);
        this.table = table;
        this.threshold = (int)(n * this.loadFactor);
    }
    
    void transfer(final Entry[] array) {
        final Entry[] table = this.table;
        final int length = array.length;
        for (int i = 0; i < table.length; ++i) {
            Entry entry = table[i];
            if (entry != null) {
                table[i] = null;
                do {
                    final Entry<K, V> next = entry.next;
                    final int index = indexFor(entry.hash, length);
                    entry.next = array[index];
                    array[index] = entry;
                    entry = next;
                } while (entry != null);
            }
        }
    }
    
    @Override
    public void putAll(final Map<? extends K, ? extends V> map) {
        final int size = map.size();
        if (size == 0) {
            return;
        }
        if (size > this.threshold) {
            int n = (int)(size / this.loadFactor + 1.0f);
            if (n > 1073741824) {
                n = 1073741824;
            }
            int i;
            for (i = this.table.length; i < n; i <<= 1) {}
            if (i > this.table.length) {
                this.resize(i);
            }
        }
        for (final Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            this.put(entry.getKey(), (V)entry.getValue());
        }
    }
    
    @Override
    public V remove(final Object o) {
        final Entry<K, V> removeEntryForKey = this.removeEntryForKey(o);
        return (removeEntryForKey == null) ? null : removeEntryForKey.value;
    }
    
    final Entry<K, V> removeEntryForKey(final Object o) {
        final int n = (o == null) ? 0 : hash(o.hashCode());
        final int index = indexFor(n, this.table.length);
        Entry<K, V> item;
        Entry<K, V> next;
        for (Entry<K, V> entry = item = (Entry<K, V>)this.table[index]; item != null; item = next) {
            next = item.next;
            final K key;
            if (item.hash == n && ((key = item.key) == o || (o != null && o.equals(key)))) {
                ++this.modCount;
                --this.size;
                if (entry == item) {
                    this.table[index] = next;
                }
                else {
                    entry.next = next;
                }
                item.recordRemoval(this);
                item.value = null;
                item.next = null;
                this.entryStore.push(item);
                return item;
            }
            entry = item;
        }
        return item;
    }
    
    final Entry<K, V> removeMapping(final Object o) {
        if (!(o instanceof Map.Entry)) {
            return null;
        }
        final Map.Entry entry = (Map.Entry)o;
        final Object key = entry.getKey();
        final int n = (key == null) ? 0 : hash(key.hashCode());
        final int index = indexFor(n, this.table.length);
        Entry<K, V> item;
        Entry<K, V> next;
        for (Entry<K, V> entry2 = item = (Entry<K, V>)this.table[index]; item != null; item = next) {
            next = item.next;
            if (item.hash == n && item.equals(entry)) {
                ++this.modCount;
                --this.size;
                if (entry2 == item) {
                    this.table[index] = next;
                }
                else {
                    entry2.next = next;
                }
                item.recordRemoval(this);
                item.value = null;
                item.next = null;
                this.entryStore.push(item);
                return item;
            }
            entry2 = item;
        }
        return item;
    }
    
    @Override
    public void clear() {
        ++this.modCount;
        final Entry[] table = this.table;
        for (int i = 0; i < table.length; ++i) {
            if (table[i] != null) {
                table[i].value = null;
                table[i].next = null;
                this.entryStore.push(table[i]);
            }
            table[i] = null;
        }
        this.size = 0;
    }
    
    @Override
    public boolean containsValue(final Object o) {
        if (o == null) {
            return this.containsNullValue();
        }
        final Entry[] table = this.table;
        for (int i = 0; i < table.length; ++i) {
            for (Entry next = table[i]; next != null; next = next.next) {
                if (o.equals(next.value)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean containsNullValue() {
        final Entry[] table = this.table;
        for (int i = 0; i < table.length; ++i) {
            for (Entry next = table[i]; next != null; next = next.next) {
                if (next.value == null) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public Object clone() {
        ZomboidHashMap zomboidHashMap = null;
        try {
            zomboidHashMap = (ZomboidHashMap)super.clone();
        }
        catch (CloneNotSupportedException ex) {}
        zomboidHashMap.table = new Entry[this.table.length];
        zomboidHashMap.entrySet = null;
        zomboidHashMap.modCount = 0;
        zomboidHashMap.size = 0;
        zomboidHashMap.init();
        zomboidHashMap.putAllForCreate(this);
        return zomboidHashMap;
    }
    
    void addEntry(final int hash, final K key, final V value, final int n) {
        final Entry next = this.table[n];
        if (this.entryStore.isEmpty()) {
            for (int i = 0; i < 100; ++i) {
                this.entryStore.add(new Entry<Object, Object>(0, null, null, null));
            }
        }
        final Entry<K, V> entry = this.entryStore.pop();
        entry.hash = hash;
        entry.key = key;
        entry.value = value;
        entry.next = (Entry<K, V>)next;
        this.table[n] = entry;
        if (this.size++ >= this.threshold) {
            this.resize(2 * this.table.length);
        }
    }
    
    void createEntry(final int hash, final K key, final V value, final int n) {
        final Entry next = this.table[n];
        if (this.entryStore.isEmpty()) {
            for (int i = 0; i < 100; ++i) {
                this.entryStore.add(new Entry<Object, Object>(0, null, null, null));
            }
        }
        final Entry<K, V> entry = this.entryStore.pop();
        entry.hash = hash;
        entry.key = key;
        entry.value = value;
        entry.next = (Entry<K, V>)next;
        this.table[n] = entry;
        ++this.size;
    }
    
    Iterator<K> newKeyIterator() {
        return new KeyIterator();
    }
    
    Iterator<V> newValueIterator() {
        return new ValueIterator();
    }
    
    Iterator<Map.Entry<K, V>> newEntryIterator() {
        return new EntryIterator();
    }
    
    @Override
    public Set<K> keySet() {
        final Set<K> keySet = this.keySet;
        return (keySet != null) ? keySet : (this.keySet = new KeySet());
    }
    
    @Override
    public Collection<V> values() {
        final Collection<V> values = this.values;
        return (values != null) ? values : (this.values = new Values());
    }
    
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return this.entrySet0();
    }
    
    private Set<Map.Entry<K, V>> entrySet0() {
        final Set<Map.Entry<K, V>> entrySet = this.entrySet;
        return (entrySet != null) ? entrySet : (this.entrySet = new EntrySet());
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        final Iterator<Map.Entry<K, V>> iterator = (this.size > 0) ? this.entrySet0().iterator() : null;
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeInt(this.table.length);
        objectOutputStream.writeInt(this.size);
        if (iterator != null) {
            while (iterator.hasNext()) {
                final Map.Entry<K, V> entry = iterator.next();
                objectOutputStream.writeObject(entry.getKey());
                objectOutputStream.writeObject(entry.getValue());
            }
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.table = new Entry[objectInputStream.readInt()];
        this.init();
        for (int int1 = objectInputStream.readInt(), i = 0; i < int1; ++i) {
            this.putForCreate(objectInputStream.readObject(), objectInputStream.readObject());
        }
    }
    
    int capacity() {
        return this.table.length;
    }
    
    float loadFactor() {
        return this.loadFactor;
    }
    
    static class Entry<K, V> implements Map.Entry<K, V>
    {
        K key;
        V value;
        Entry<K, V> next;
        int hash;
        
        Entry(final int hash, final K key, final V value, final Entry<K, V> next) {
            this.value = value;
            this.next = next;
            this.key = key;
            this.hash = hash;
        }
        
        @Override
        public final K getKey() {
            return this.key;
        }
        
        @Override
        public final V getValue() {
            return this.value;
        }
        
        @Override
        public final V setValue(final V value) {
            final V value2 = this.value;
            this.value = value;
            return value2;
        }
        
        @Override
        public final boolean equals(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry entry = (Map.Entry)o;
            final Object key = this.getKey();
            final Object key2 = entry.getKey();
            if (key == key2 || (key != null && key.equals(key2))) {
                final Object value = this.getValue();
                final V value2 = entry.getValue();
                if (value == value2 || (value != null && value.equals(value2))) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public final int hashCode() {
            return ((this.key == null) ? 0 : this.key.hashCode()) ^ ((this.value == null) ? 0 : this.value.hashCode());
        }
        
        @Override
        public final String toString() {
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/String;, this.getKey(), this.getValue());
        }
        
        void recordAccess(final ZomboidHashMap<K, V> zomboidHashMap) {
        }
        
        void recordRemoval(final ZomboidHashMap<K, V> zomboidHashMap) {
        }
    }
    
    private abstract class HashIterator<E> implements Iterator<E>
    {
        Entry<K, V> next;
        int expectedModCount;
        int index;
        Entry<K, V> current;
        
        HashIterator() {
            this.expectedModCount = ZomboidHashMap.this.modCount;
            if (ZomboidHashMap.this.size > 0) {
                final Entry[] table = ZomboidHashMap.this.table;
                while (this.index < table.length && (this.next = (Entry<K, V>)table[this.index++]) == null) {}
            }
        }
        
        @Override
        public final boolean hasNext() {
            return this.next != null;
        }
        
        final Entry<K, V> nextEntry() {
            if (ZomboidHashMap.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            final Entry<K, V> next = this.next;
            if (next == null) {
                throw new NoSuchElementException();
            }
            if ((this.next = next.next) == null) {
                final Entry[] table = ZomboidHashMap.this.table;
                while (this.index < table.length && (this.next = (Entry<K, V>)table[this.index++]) == null) {}
            }
            return this.current = next;
        }
        
        @Override
        public void remove() {
            if (this.current == null) {
                throw new IllegalStateException();
            }
            if (ZomboidHashMap.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            final K key = this.current.key;
            this.current = null;
            ZomboidHashMap.this.removeEntryForKey(key);
            this.expectedModCount = ZomboidHashMap.this.modCount;
        }
    }
    
    private final class ValueIterator extends HashIterator<V>
    {
        @Override
        public V next() {
            return this.nextEntry().value;
        }
    }
    
    private final class KeyIterator extends HashIterator<K>
    {
        @Override
        public K next() {
            return this.nextEntry().getKey();
        }
    }
    
    private final class EntryIterator extends HashIterator<Map.Entry<K, V>>
    {
        @Override
        public Map.Entry<K, V> next() {
            return this.nextEntry();
        }
    }
    
    private final class KeySet extends AbstractSet<K>
    {
        @Override
        public Iterator<K> iterator() {
            return ZomboidHashMap.this.newKeyIterator();
        }
        
        @Override
        public int size() {
            return ZomboidHashMap.this.size;
        }
        
        @Override
        public boolean contains(final Object o) {
            return ZomboidHashMap.this.containsKey(o);
        }
        
        @Override
        public boolean remove(final Object o) {
            return ZomboidHashMap.this.removeEntryForKey(o) != null;
        }
        
        @Override
        public void clear() {
            ZomboidHashMap.this.clear();
        }
    }
    
    private final class Values extends AbstractCollection<V>
    {
        @Override
        public Iterator<V> iterator() {
            return ZomboidHashMap.this.newValueIterator();
        }
        
        @Override
        public int size() {
            return ZomboidHashMap.this.size;
        }
        
        @Override
        public boolean contains(final Object o) {
            return ZomboidHashMap.this.containsValue(o);
        }
        
        @Override
        public void clear() {
            ZomboidHashMap.this.clear();
        }
    }
    
    private final class EntrySet extends AbstractSet<Map.Entry<K, V>>
    {
        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return ZomboidHashMap.this.newEntryIterator();
        }
        
        @Override
        public boolean contains(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry entry = (Map.Entry)o;
            final Entry<K, V> entry2 = ZomboidHashMap.this.getEntry(entry.getKey());
            return entry2 != null && entry2.equals(entry);
        }
        
        @Override
        public boolean remove(final Object o) {
            return ZomboidHashMap.this.removeMapping(o) != null;
        }
        
        @Override
        public int size() {
            return ZomboidHashMap.this.size;
        }
        
        @Override
        public void clear() {
            ZomboidHashMap.this.clear();
        }
    }
}

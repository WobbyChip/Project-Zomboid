// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.utils;

public class HashMap
{
    private int capacity;
    private int elements;
    private Bucket[] buckets;
    
    public HashMap() {
        this.capacity = 2;
        this.elements = 0;
        this.buckets = new Bucket[this.capacity];
        for (int i = 0; i < this.capacity; ++i) {
            this.buckets[i] = new Bucket();
        }
    }
    
    public void clear() {
        this.elements = 0;
        for (int i = 0; i < this.capacity; ++i) {
            this.buckets[i].clear();
        }
    }
    
    private void grow() {
        final Bucket[] buckets = this.buckets;
        this.capacity *= 2;
        this.elements = 0;
        this.buckets = new Bucket[this.capacity];
        for (int i = 0; i < this.capacity; ++i) {
            this.buckets[i] = new Bucket();
        }
        for (int j = 0; j < buckets.length; ++j) {
            final Bucket bucket = buckets[j];
            for (int k = 0; k < bucket.size(); ++k) {
                if (bucket.keys[k] != null) {
                    this.put(bucket.keys[k], bucket.values[k]);
                }
            }
        }
    }
    
    public Object get(final Object obj) {
        final Bucket bucket = this.buckets[Math.abs(obj.hashCode()) % this.capacity];
        for (int i = 0; i < bucket.size(); ++i) {
            if (bucket.keys[i] != null && bucket.keys[i].equals(obj)) {
                return bucket.values[i];
            }
        }
        return null;
    }
    
    public Object remove(final Object o) {
        final Object remove = this.buckets[Math.abs(o.hashCode()) % this.capacity].remove(o);
        if (remove != null) {
            --this.elements;
            return remove;
        }
        return null;
    }
    
    public Object put(final Object o, final Object o2) {
        if (this.elements + 1 >= this.buckets.length) {
            this.grow();
        }
        final Object remove = this.remove(o);
        this.buckets[Math.abs(o.hashCode()) % this.capacity].put(o, o2);
        ++this.elements;
        return remove;
    }
    
    public int size() {
        return this.elements;
    }
    
    public boolean isEmpty() {
        return this.size() == 0;
    }
    
    public Iterator iterator() {
        return new Iterator(this);
    }
    
    @Override
    public String toString() {
        String s = new String();
        for (int i = 0; i < this.buckets.length; ++i) {
            final Bucket bucket = this.buckets[i];
            for (int j = 0; j < bucket.size(); ++j) {
                if (bucket.keys[j] != null) {
                    if (s.length() > 0) {
                        s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
                    }
                    s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/String;, s, bucket.keys[j], bucket.values[j]);
                }
            }
        }
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
    }
    
    public static class Iterator
    {
        private HashMap hashMap;
        private int bucketIdx;
        private int keyValuePairIdx;
        private int elementIdx;
        private Object currentKey;
        private Object currentValue;
        
        public Iterator(final HashMap hashMap) {
            this.hashMap = hashMap;
            this.reset();
        }
        
        public Iterator reset() {
            this.bucketIdx = 0;
            this.keyValuePairIdx = 0;
            this.elementIdx = 0;
            this.currentKey = null;
            this.currentValue = null;
            return this;
        }
        
        public boolean hasNext() {
            return this.elementIdx < this.hashMap.elements;
        }
        
        public boolean advance() {
            while (this.bucketIdx < this.hashMap.buckets.length) {
                final Bucket bucket = this.hashMap.buckets[this.bucketIdx];
                if (this.keyValuePairIdx == bucket.size()) {
                    this.keyValuePairIdx = 0;
                    ++this.bucketIdx;
                }
                else {
                    while (this.keyValuePairIdx < bucket.size()) {
                        if (bucket.keys[this.keyValuePairIdx] != null) {
                            this.currentKey = bucket.keys[this.keyValuePairIdx];
                            this.currentValue = bucket.values[this.keyValuePairIdx];
                            ++this.keyValuePairIdx;
                            ++this.elementIdx;
                            return true;
                        }
                        ++this.keyValuePairIdx;
                    }
                    this.keyValuePairIdx = 0;
                    ++this.bucketIdx;
                }
            }
            return false;
        }
        
        public Object getKey() {
            return this.currentKey;
        }
        
        public Object getValue() {
            return this.currentValue;
        }
    }
    
    private static class Bucket
    {
        public Object[] keys;
        public Object[] values;
        public int count;
        public int nextIndex;
        
        public void put(final Object o, final Object o2) throws IllegalStateException {
            if (this.keys == null) {
                this.grow();
                this.keys[0] = o;
                this.values[0] = o2;
                this.nextIndex = 1;
                this.count = 1;
                return;
            }
            if (this.count == this.keys.length) {
                this.grow();
            }
            for (int i = 0; i < this.keys.length; ++i) {
                if (this.keys[i] == null) {
                    this.keys[i] = o;
                    this.values[i] = o2;
                    ++this.count;
                    this.nextIndex = Math.max(this.nextIndex, i + 1);
                    return;
                }
            }
            throw new IllegalStateException("bucket is full");
        }
        
        public Object remove(final Object obj) {
            for (int i = 0; i < this.nextIndex; ++i) {
                if (this.keys[i] != null && this.keys[i].equals(obj)) {
                    final Object o = this.values[i];
                    this.keys[i] = null;
                    this.values[i] = null;
                    --this.count;
                    return o;
                }
            }
            return null;
        }
        
        private void grow() {
            if (this.keys == null) {
                this.keys = new Object[2];
                this.values = new Object[2];
            }
            else {
                final Object[] keys = this.keys;
                final Object[] values = this.values;
                this.keys = new Object[keys.length * 2];
                this.values = new Object[values.length * 2];
                System.arraycopy(keys, 0, this.keys, 0, keys.length);
                System.arraycopy(values, 0, this.values, 0, values.length);
            }
        }
        
        public int size() {
            return this.nextIndex;
        }
        
        public void clear() {
            for (int i = 0; i < this.nextIndex; ++i) {
                this.keys[i] = null;
                this.values[i] = null;
            }
            this.count = 0;
            this.nextIndex = 0;
        }
    }
}

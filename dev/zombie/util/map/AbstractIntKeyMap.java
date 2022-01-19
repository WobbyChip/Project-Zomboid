// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util.map;

import zombie.util.hash.DefaultIntHashFunction;

public abstract class AbstractIntKeyMap<V> implements IntKeyMap<V>
{
    protected AbstractIntKeyMap() {
    }
    
    @Override
    public void clear() {
        final IntKeyMapIterator<V> entries = this.entries();
        while (entries.hasNext()) {
            entries.next();
            entries.remove();
        }
    }
    
    @Override
    public V remove(final int n) {
        final IntKeyMapIterator<V> entries = this.entries();
        while (entries.hasNext()) {
            entries.next();
            if (entries.getKey() == n) {
                final V value = entries.getValue();
                entries.remove();
                return value;
            }
        }
        return null;
    }
    
    @Override
    public void putAll(final IntKeyMap<V> intKeyMap) {
        final IntKeyMapIterator<V> entries = intKeyMap.entries();
        while (entries.hasNext()) {
            entries.next();
            this.put(entries.getKey(), entries.getValue());
        }
    }
    
    @Override
    public boolean containsKey(final int n) {
        final IntKeyMapIterator<V> entries = this.entries();
        while (entries.hasNext()) {
            entries.next();
            if (entries.getKey() == n) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public V get(final int n) {
        final IntKeyMapIterator<V> entries = this.entries();
        while (entries.hasNext()) {
            entries.next();
            if (entries.getKey() == n) {
                return entries.getValue();
            }
        }
        return null;
    }
    
    @Override
    public boolean containsValue(final Object o) {
        final IntKeyMapIterator<Object> entries = this.entries();
        if (o == null) {
            while (entries.hasNext()) {
                entries.next();
                if (o == null) {
                    return true;
                }
            }
        }
        else {
            while (entries.hasNext()) {
                entries.next();
                if (o.equals(entries.getValue())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof IntKeyMap)) {
            return false;
        }
        final IntKeyMap intKeyMap = (IntKeyMap)o;
        if (this.size() != intKeyMap.size()) {
            return false;
        }
        final IntKeyMapIterator<Object> entries = (IntKeyMapIterator<Object>)this.entries();
        while (entries.hasNext()) {
            entries.next();
            final int key = entries.getKey();
            final Object value = entries.getValue();
            if (value == null) {
                if (intKeyMap.get(key) != null) {
                    return false;
                }
                if (!intKeyMap.containsKey(key)) {
                    return false;
                }
                continue;
            }
            else {
                if (!value.equals(intKeyMap.get(key))) {
                    return false;
                }
                continue;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int n = 0;
        final IntKeyMapIterator<Object> entries = (IntKeyMapIterator<Object>)this.entries();
        while (entries.hasNext()) {
            entries.next();
            n += (DefaultIntHashFunction.INSTANCE.hash(entries.getKey()) ^ entries.getValue().hashCode());
        }
        return n;
    }
    
    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }
    
    @Override
    public int size() {
        int n = 0;
        final IntKeyMapIterator<V> entries = this.entries();
        while (entries.hasNext()) {
            entries.next();
            ++n;
        }
        return n;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append('[');
        final IntKeyMapIterator<Object> entries = (IntKeyMapIterator<Object>)this.entries();
        while (entries.hasNext()) {
            if (sb.length() > 1) {
                sb.append(',');
            }
            entries.next();
            sb.append(String.valueOf(entries.getKey()));
            sb.append("->");
            sb.append(String.valueOf(entries.getValue()));
        }
        sb.append(']');
        return sb.toString();
    }
    
    public void trimToSize() {
    }
}

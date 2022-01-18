// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.Collections;

import java.util.Iterator;
import java.io.Serializable;
import java.util.AbstractSet;

public class NonBlockingHashSet<E> extends AbstractSet<E> implements Serializable
{
    private static final Object V;
    private final NonBlockingHashMap<E, Object> _map;
    
    public NonBlockingHashSet() {
        this._map = new NonBlockingHashMap<E, Object>();
    }
    
    @Override
    public boolean add(final E e) {
        return this._map.putIfAbsent(e, NonBlockingHashSet.V) != NonBlockingHashSet.V;
    }
    
    @Override
    public boolean contains(final Object o) {
        return this._map.containsKey(o);
    }
    
    @Override
    public boolean remove(final Object o) {
        return this._map.remove(o) == NonBlockingHashSet.V;
    }
    
    @Override
    public int size() {
        return this._map.size();
    }
    
    @Override
    public void clear() {
        this._map.clear();
    }
    
    @Override
    public Iterator<E> iterator() {
        return this._map.keySet().iterator();
    }
    
    public void readOnly() {
        throw new RuntimeException("Unimplemented");
    }
    
    static {
        V = "";
    }
}

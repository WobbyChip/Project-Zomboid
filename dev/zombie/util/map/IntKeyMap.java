// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util.map;

import java.util.Collection;
import zombie.util.set.IntSet;

public interface IntKeyMap<V>
{
    void clear();
    
    boolean containsKey(final int p0);
    
    boolean containsValue(final Object p0);
    
    IntKeyMapIterator<V> entries();
    
    boolean equals(final Object p0);
    
    V get(final int p0);
    
    int hashCode();
    
    boolean isEmpty();
    
    IntSet keySet();
    
    V put(final int p0, final V p1);
    
    void putAll(final IntKeyMap<V> p0);
    
    V remove(final int p0);
    
    int size();
    
    Collection<V> values();
}

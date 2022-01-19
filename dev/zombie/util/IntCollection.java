// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util;

public interface IntCollection
{
    boolean add(final int p0);
    
    boolean addAll(final IntCollection p0);
    
    void clear();
    
    boolean contains(final int p0);
    
    boolean containsAll(final IntCollection p0);
    
    boolean equals(final Object p0);
    
    int hashCode();
    
    boolean isEmpty();
    
    IntIterator iterator();
    
    boolean remove(final int p0);
    
    boolean removeAll(final IntCollection p0);
    
    boolean retainAll(final IntCollection p0);
    
    int size();
    
    int[] toArray();
    
    int[] toArray(final int[] p0);
    
    void trimToSize();
}

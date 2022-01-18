// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util.list;

import zombie.util.IntCollection;

public interface IntList extends IntCollection
{
    void add(final int p0, final int p1);
    
    boolean addAll(final int p0, final IntCollection p1);
    
    int get(final int p0);
    
    int indexOf(final int p0);
    
    int indexOf(final int p0, final int p1);
    
    int lastIndexOf(final int p0);
    
    int lastIndexOf(final int p0, final int p1);
    
    IntListIterator listIterator();
    
    IntListIterator listIterator(final int p0);
    
    int removeElementAt(final int p0);
    
    int set(final int p0, final int p1);
}

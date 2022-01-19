// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util.list;

import zombie.util.IntIterator;

public interface IntListIterator extends IntIterator
{
    void add(final int p0);
    
    boolean hasPrevious();
    
    int nextIndex();
    
    int previous();
    
    int previousIndex();
    
    void set(final int p0);
}

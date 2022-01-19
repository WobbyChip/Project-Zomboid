// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util.set;

import zombie.util.IntIterator;
import zombie.util.hash.DefaultIntHashFunction;
import zombie.util.IntCollection;
import zombie.util.AbstractIntCollection;

public abstract class AbstractIntSet extends AbstractIntCollection implements IntSet
{
    protected AbstractIntSet() {
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof IntSet)) {
            return false;
        }
        final IntSet set = (IntSet)o;
        return set.size() == this.size() && this.containsAll(set);
    }
    
    @Override
    public int hashCode() {
        int n = 0;
        final IntIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            n += DefaultIntHashFunction.INSTANCE.hash(iterator.next());
        }
        return n;
    }
}

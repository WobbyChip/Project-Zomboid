// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util.list;

import zombie.util.hash.DefaultIntHashFunction;
import zombie.util.IntIterator;
import zombie.util.IntCollection;
import zombie.util.util.Exceptions;
import zombie.util.AbstractIntCollection;

public abstract class AbstractIntList extends AbstractIntCollection implements IntList
{
    protected AbstractIntList() {
    }
    
    @Override
    public boolean add(final int n) {
        this.add(this.size(), n);
        return true;
    }
    
    @Override
    public void add(final int n, final int n2) {
        Exceptions.unsupported("add");
    }
    
    @Override
    public boolean addAll(int n, final IntCollection collection) {
        if (n < 0 || n > this.size()) {
            Exceptions.indexOutOfBounds(n, 0, this.size());
        }
        final IntIterator iterator = collection.iterator();
        final boolean hasNext = iterator.hasNext();
        while (iterator.hasNext()) {
            this.add(n, iterator.next());
            ++n;
        }
        return hasNext;
    }
    
    @Override
    public int indexOf(final int n) {
        return this.indexOf(0, n);
    }
    
    @Override
    public int indexOf(final int n, final int n2) {
        final IntListIterator listIterator = this.listIterator(n);
        while (listIterator.hasNext()) {
            if (listIterator.next() == n2) {
                return listIterator.previousIndex();
            }
        }
        return -1;
    }
    
    @Override
    public IntIterator iterator() {
        return this.listIterator();
    }
    
    @Override
    public int lastIndexOf(final int n) {
        final IntListIterator listIterator = this.listIterator(this.size());
        while (listIterator.hasPrevious()) {
            if (listIterator.previous() == n) {
                return listIterator.nextIndex();
            }
        }
        return -1;
    }
    
    @Override
    public int lastIndexOf(final int n, final int n2) {
        final IntListIterator listIterator = this.listIterator(n);
        while (listIterator.hasPrevious()) {
            if (listIterator.previous() == n2) {
                return listIterator.nextIndex();
            }
        }
        return -1;
    }
    
    @Override
    public IntListIterator listIterator() {
        return this.listIterator(0);
    }
    
    @Override
    public IntListIterator listIterator(final int n) {
        if (n < 0 || n > this.size()) {
            Exceptions.indexOutOfBounds(n, 0, this.size());
        }
        return new IntListIterator() {
            private int ptr = n;
            private int lptr = -1;
            
            @Override
            public boolean hasNext() {
                return this.ptr < AbstractIntList.this.size();
            }
            
            @Override
            public int next() {
                if (this.ptr == AbstractIntList.this.size()) {
                    Exceptions.endOfIterator();
                }
                this.lptr = this.ptr++;
                return AbstractIntList.this.get(this.lptr);
            }
            
            @Override
            public void remove() {
                if (this.lptr == -1) {
                    Exceptions.noElementToRemove();
                }
                AbstractIntList.this.removeElementAt(this.lptr);
                if (this.lptr < this.ptr) {
                    --this.ptr;
                }
                this.lptr = -1;
            }
            
            @Override
            public void add(final int n) {
                AbstractIntList.this.add(this.ptr++, n);
                this.lptr = -1;
            }
            
            @Override
            public boolean hasPrevious() {
                return this.ptr > 0;
            }
            
            @Override
            public int nextIndex() {
                return this.ptr;
            }
            
            @Override
            public int previous() {
                if (this.ptr == 0) {
                    Exceptions.startOfIterator();
                }
                --this.ptr;
                this.lptr = this.ptr;
                return AbstractIntList.this.get(this.ptr);
            }
            
            @Override
            public int previousIndex() {
                return this.ptr - 1;
            }
            
            @Override
            public void set(final int n) {
                if (this.lptr == -1) {
                    Exceptions.noElementToSet();
                }
                AbstractIntList.this.set(this.lptr, n);
            }
        };
    }
    
    @Override
    public int removeElementAt(final int n) {
        Exceptions.unsupported("removeElementAt");
        throw new RuntimeException();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IntList)) {
            return false;
        }
        final IntListIterator listIterator = this.listIterator();
        final IntListIterator listIterator2 = ((IntList)o).listIterator();
        while (listIterator.hasNext() && listIterator2.hasNext()) {
            if (listIterator.next() != listIterator2.next()) {
                return false;
            }
        }
        return !listIterator.hasNext() && !listIterator2.hasNext();
    }
    
    @Override
    public int hashCode() {
        int n = 1;
        final IntIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            n = 31 * n + DefaultIntHashFunction.INSTANCE.hash(iterator.next());
        }
        return n;
    }
}

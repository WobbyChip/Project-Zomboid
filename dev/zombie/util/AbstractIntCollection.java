// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util;

import zombie.util.util.Display;
import zombie.util.util.Exceptions;

public abstract class AbstractIntCollection implements IntCollection
{
    protected AbstractIntCollection() {
    }
    
    @Override
    public boolean add(final int n) {
        Exceptions.unsupported("add");
        return false;
    }
    
    @Override
    public boolean addAll(final IntCollection collection) {
        final IntIterator iterator = collection.iterator();
        boolean b = false;
        while (iterator.hasNext()) {
            b |= this.add(iterator.next());
        }
        return b;
    }
    
    @Override
    public void clear() {
        final IntIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
    }
    
    @Override
    public boolean contains(final int n) {
        final IntIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            if (iterator.next() == n) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean containsAll(final IntCollection collection) {
        final IntIterator iterator = collection.iterator();
        while (iterator.hasNext()) {
            if (!this.contains(iterator.next())) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }
    
    @Override
    public boolean remove(final int n) {
        final IntIterator iterator = this.iterator();
        boolean b = false;
        while (iterator.hasNext()) {
            if (iterator.next() == n) {
                iterator.remove();
                b = true;
                break;
            }
        }
        return b;
    }
    
    @Override
    public boolean removeAll(final IntCollection collection) {
        if (collection == null) {
            Exceptions.nullArgument("collection");
        }
        final IntIterator iterator = this.iterator();
        boolean b = false;
        while (iterator.hasNext()) {
            if (collection.contains(iterator.next())) {
                iterator.remove();
                b = true;
            }
        }
        return b;
    }
    
    @Override
    public boolean retainAll(final IntCollection collection) {
        if (collection == null) {
            Exceptions.nullArgument("collection");
        }
        final IntIterator iterator = this.iterator();
        boolean b = false;
        while (iterator.hasNext()) {
            if (!collection.contains(iterator.next())) {
                iterator.remove();
                b = true;
            }
        }
        return b;
    }
    
    @Override
    public int size() {
        final IntIterator iterator = this.iterator();
        int n = 0;
        while (iterator.hasNext()) {
            iterator.next();
            ++n;
        }
        return n;
    }
    
    @Override
    public int[] toArray() {
        return this.toArray(null);
    }
    
    @Override
    public int[] toArray(int[] array) {
        final int size = this.size();
        if (array == null || array.length < size) {
            array = new int[size];
        }
        final IntIterator iterator = this.iterator();
        int n = 0;
        while (iterator.hasNext()) {
            array[n] = iterator.next();
            ++n;
        }
        return array;
    }
    
    @Override
    public void trimToSize() {
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append('[');
        final IntIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            if (sb.length() > 1) {
                sb.append(',');
            }
            sb.append(Display.display(iterator.next()));
        }
        sb.append(']');
        return sb.toString();
    }
}

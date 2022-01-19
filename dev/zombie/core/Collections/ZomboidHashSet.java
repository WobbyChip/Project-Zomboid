// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.Collections;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.Collection;
import java.io.Serializable;
import java.util.Set;
import java.util.AbstractSet;

public class ZomboidHashSet<E> extends AbstractSet<E> implements Set<E>, Cloneable, Serializable
{
    static final long serialVersionUID = -5024744406713321676L;
    private transient ZomboidHashMap<E, Object> map;
    private static final Object PRESENT;
    
    public ZomboidHashSet() {
        this.map = new ZomboidHashMap<E, Object>();
    }
    
    public ZomboidHashSet(final Collection<? extends E> c) {
        this.map = new ZomboidHashMap<E, Object>(Math.max((int)(c.size() / 0.75f) + 1, 16));
        this.addAll(c);
    }
    
    public ZomboidHashSet(final int n, final float n2) {
        this.map = new ZomboidHashMap<E, Object>(n);
    }
    
    public ZomboidHashSet(final int n) {
        this.map = new ZomboidHashMap<E, Object>(n);
    }
    
    @Override
    public Iterator<E> iterator() {
        return this.map.keySet().iterator();
    }
    
    @Override
    public int size() {
        return this.map.size();
    }
    
    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }
    
    @Override
    public boolean contains(final Object o) {
        return this.map.containsKey(o);
    }
    
    @Override
    public boolean add(final E e) {
        return this.map.put(e, ZomboidHashSet.PRESENT) == null;
    }
    
    @Override
    public boolean remove(final Object o) {
        return this.map.remove(o) == ZomboidHashSet.PRESENT;
    }
    
    @Override
    public void clear() {
        this.map.clear();
    }
    
    public Object clone() {
        try {
            final ZomboidHashSet set = (ZomboidHashSet)super.clone();
            set.map = (ZomboidHashMap<E, Object>)this.map.clone();
            return set;
        }
        catch (CloneNotSupportedException ex) {
            throw new InternalError();
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeInt(this.map.size());
        final Iterator<E> iterator = this.map.keySet().iterator();
        while (iterator.hasNext()) {
            objectOutputStream.writeObject(iterator.next());
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
    }
    
    static {
        PRESENT = new Object();
    }
}

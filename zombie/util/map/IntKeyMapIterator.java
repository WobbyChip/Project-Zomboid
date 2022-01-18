// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util.map;

public interface IntKeyMapIterator<V>
{
    boolean hasNext();
    
    void next();
    
    void remove();
    
    int getKey();
    
    V getValue();
}

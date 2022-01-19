// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util;

import java.util.List;

public interface IPooledObject
{
    Pool<IPooledObject> getPool();
    
    void setPool(final Pool<IPooledObject> p0);
    
    void release();
    
    boolean isFree();
    
    void setFree(final boolean p0);
    
    default void onReleased() {
    }
    
    default void release(final IPooledObject[] array) {
        for (int i = 0; i < array.length; ++i) {
            Pool.tryRelease(array[i]);
        }
    }
    
    default void tryReleaseAndBlank(final IPooledObject[] array) {
        if (array != null) {
            releaseAndBlank(array);
        }
    }
    
    default void releaseAndBlank(final IPooledObject[] array) {
        for (int i = 0; i < array.length; ++i) {
            array[i] = Pool.tryRelease(array[i]);
        }
    }
    
    default void release(final List<? extends IPooledObject> list) {
        for (int i = 0; i < list.size(); ++i) {
            Pool.tryRelease((IPooledObject)list.get(i));
        }
        list.clear();
    }
}

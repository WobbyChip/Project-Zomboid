// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util.lambda;

import zombie.util.Pool;
import zombie.util.PooledObject;

public final class ReturnValueContainer<T> extends PooledObject
{
    public T ReturnVal;
    private static final Pool<ReturnValueContainer<Object>> s_pool;
    
    @Override
    public void onReleased() {
        this.ReturnVal = null;
    }
    
    public static <E> ReturnValueContainer<E> alloc() {
        return (ReturnValueContainer<E>)ReturnValueContainer.s_pool.alloc();
    }
    
    static {
        s_pool = new Pool<ReturnValueContainer<Object>>(ReturnValueContainer::new);
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util.lambda;

import zombie.util.Pool;
import java.util.Comparator;
import zombie.util.PooledObject;

public final class Comparators
{
    public static final class Params1
    {
        private static class StackItem<T1> extends PooledObject
        {
            T1 val1;
        }
        
        public static final class CallbackStackItem<E, T1> extends StackItem<T1> implements Comparator<E>
        {
            private ICallback<E, T1> comparator;
            private static final Pool<CallbackStackItem<Object, Object>> s_pool;
            
            @Override
            public int compare(final E e, final E e2) {
                return this.comparator.compare(e, e2, (T1)this.val1);
            }
            
            public static <E, T1> CallbackStackItem<E, T1> alloc(final T1 val1, final ICallback<E, T1> comparator) {
                final CallbackStackItem<Object, Object> callbackStackItem = CallbackStackItem.s_pool.alloc();
                callbackStackItem.val1 = (T1)val1;
                callbackStackItem.comparator = (ICallback<Object, Object>)comparator;
                return (CallbackStackItem<E, T1>)callbackStackItem;
            }
            
            @Override
            public void onReleased() {
                this.val1 = null;
                this.comparator = null;
            }
            
            static {
                s_pool = new Pool<CallbackStackItem<Object, Object>>(CallbackStackItem::new);
            }
        }
        
        public interface ICallback<E, T1>
        {
            int compare(final E p0, final E p1, final T1 p2);
        }
    }
    
    public static final class Params2
    {
        private static class StackItem<T1, T2> extends PooledObject
        {
            T1 val1;
            T2 val2;
        }
        
        public static final class CallbackStackItem<E, T1, T2> extends StackItem<T1, T2> implements Comparator<E>
        {
            private ICallback<E, T1, T2> comparator;
            private static final Pool<CallbackStackItem<Object, Object, Object>> s_pool;
            
            @Override
            public int compare(final E e, final E e2) {
                return this.comparator.compare(e, e2, (T1)this.val1, (T2)this.val2);
            }
            
            public static <E, T1, T2> CallbackStackItem<E, T1, T2> alloc(final T1 val1, final T2 val2, final ICallback<E, T1, T2> comparator) {
                final CallbackStackItem<Object, Object, Object> callbackStackItem = CallbackStackItem.s_pool.alloc();
                callbackStackItem.val1 = (T1)val1;
                callbackStackItem.val2 = (T2)val2;
                callbackStackItem.comparator = (ICallback<Object, Object, Object>)comparator;
                return (CallbackStackItem<E, T1, T2>)callbackStackItem;
            }
            
            @Override
            public void onReleased() {
                this.val1 = null;
                this.val2 = null;
                this.comparator = null;
            }
            
            static {
                s_pool = new Pool<CallbackStackItem<Object, Object, Object>>(CallbackStackItem::new);
            }
        }
        
        public interface ICallback<E, T1, T2>
        {
            int compare(final E p0, final E p1, final T1 p2, final T2 p3);
        }
    }
}

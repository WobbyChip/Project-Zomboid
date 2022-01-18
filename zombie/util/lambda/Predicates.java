// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util.lambda;

import zombie.util.Pool;
import java.util.function.Predicate;
import zombie.util.PooledObject;

public final class Predicates
{
    public static final class Params1
    {
        private static class StackItem<T1> extends PooledObject
        {
            T1 val1;
        }
        
        public static final class CallbackStackItem<E, T1> extends StackItem<T1> implements Predicate<E>
        {
            private ICallback<E, T1> predicate;
            private static final Pool<CallbackStackItem<Object, Object>> s_pool;
            
            @Override
            public boolean test(final E e) {
                return this.predicate.test(e, (T1)this.val1);
            }
            
            public static <E, T1> CallbackStackItem<E, T1> alloc(final T1 val1, final ICallback<E, T1> predicate) {
                final CallbackStackItem<Object, Object> callbackStackItem = CallbackStackItem.s_pool.alloc();
                callbackStackItem.val1 = (T1)val1;
                callbackStackItem.predicate = (ICallback<Object, Object>)predicate;
                return (CallbackStackItem<E, T1>)callbackStackItem;
            }
            
            @Override
            public void onReleased() {
                this.val1 = null;
                this.predicate = null;
            }
            
            static {
                s_pool = new Pool<CallbackStackItem<Object, Object>>(CallbackStackItem::new);
            }
        }
        
        public interface ICallback<E, T1>
        {
            boolean test(final E p0, final T1 p1);
        }
    }
    
    public static final class Params2
    {
        private static class StackItem<T1, T2> extends PooledObject
        {
            T1 val1;
            T2 val2;
        }
        
        public static final class CallbackStackItem<E, T1, T2> extends StackItem<T1, T2> implements Predicate<E>
        {
            private ICallback<E, T1, T2> predicate;
            private static final Pool<CallbackStackItem<Object, Object, Object>> s_pool;
            
            @Override
            public boolean test(final E e) {
                return this.predicate.test(e, (T1)this.val1, (T2)this.val2);
            }
            
            public static <E, T1, T2> CallbackStackItem<E, T1, T2> alloc(final T1 val1, final T2 val2, final ICallback<E, T1, T2> predicate) {
                final CallbackStackItem<Object, Object, Object> callbackStackItem = CallbackStackItem.s_pool.alloc();
                callbackStackItem.val1 = (T1)val1;
                callbackStackItem.val2 = (T2)val2;
                callbackStackItem.predicate = (ICallback<Object, Object, Object>)predicate;
                return (CallbackStackItem<E, T1, T2>)callbackStackItem;
            }
            
            @Override
            public void onReleased() {
                this.val1 = null;
                this.val2 = null;
                this.predicate = null;
            }
            
            static {
                s_pool = new Pool<CallbackStackItem<Object, Object, Object>>(CallbackStackItem::new);
            }
        }
        
        public interface ICallback<E, T1, T2>
        {
            boolean test(final E p0, final T1 p1, final T2 p2);
        }
    }
    
    public static final class Params3
    {
        private static class StackItem<T1, T2, T3> extends PooledObject
        {
            T1 val1;
            T2 val2;
            T3 val3;
        }
        
        public static final class CallbackStackItem<E, T1, T2, T3> extends StackItem<T1, T2, T3> implements Predicate<E>
        {
            private ICallback<E, T1, T2, T3> predicate;
            private static final Pool<CallbackStackItem<Object, Object, Object, Object>> s_pool;
            
            @Override
            public boolean test(final E e) {
                return this.predicate.test(e, (T1)this.val1, (T2)this.val2, (T3)this.val3);
            }
            
            public static <E, T1, T2, T3> CallbackStackItem<E, T1, T2, T3> alloc(final T1 val1, final T2 val2, final T3 val3, final ICallback<E, T1, T2, T3> predicate) {
                final CallbackStackItem<Object, Object, Object, Object> callbackStackItem = CallbackStackItem.s_pool.alloc();
                callbackStackItem.val1 = (T1)val1;
                callbackStackItem.val2 = (T2)val2;
                callbackStackItem.val3 = (T3)val3;
                callbackStackItem.predicate = (ICallback<Object, Object, Object, Object>)predicate;
                return (CallbackStackItem<E, T1, T2, T3>)callbackStackItem;
            }
            
            @Override
            public void onReleased() {
                this.val1 = null;
                this.val2 = null;
                this.val3 = null;
                this.predicate = null;
            }
            
            static {
                s_pool = new Pool<CallbackStackItem<Object, Object, Object, Object>>(CallbackStackItem::new);
            }
        }
        
        public interface ICallback<E, T1, T2, T3>
        {
            boolean test(final E p0, final T1 p1, final T2 p2, final T3 p3);
        }
    }
}

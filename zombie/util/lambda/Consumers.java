// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util.lambda;

import zombie.util.Pool;
import java.util.function.Consumer;
import zombie.util.PooledObject;

public final class Consumers
{
    public static final class Params1
    {
        private static class StackItem<T1> extends PooledObject
        {
            T1 val1;
        }
        
        public static final class CallbackStackItem<E, T1> extends StackItem<T1> implements Consumer<E>
        {
            private ICallback<E, T1> consumer;
            private static final Pool<CallbackStackItem<Object, Object>> s_pool;
            
            @Override
            public void accept(final E e) {
                this.consumer.accept(e, (T1)this.val1);
            }
            
            public static <E, T1> CallbackStackItem<E, T1> alloc(final T1 val1, final ICallback<E, T1> consumer) {
                final CallbackStackItem<Object, Object> callbackStackItem = CallbackStackItem.s_pool.alloc();
                callbackStackItem.val1 = (T1)val1;
                callbackStackItem.consumer = (ICallback<Object, Object>)consumer;
                return (CallbackStackItem<E, T1>)callbackStackItem;
            }
            
            @Override
            public void onReleased() {
                this.val1 = null;
                this.consumer = null;
            }
            
            static {
                s_pool = new Pool<CallbackStackItem<Object, Object>>(CallbackStackItem::new);
            }
        }
        
        public interface ICallback<E, T1>
        {
            void accept(final E p0, final T1 p1);
        }
    }
    
    public static class Params2
    {
        private static class StackItem<T1, T2> extends PooledObject
        {
            T1 val1;
            T2 val2;
        }
        
        public static final class CallbackStackItem<E, T1, T2> extends StackItem<T1, T2> implements Consumer<E>
        {
            private ICallback<E, T1, T2> consumer;
            private static final Pool<CallbackStackItem<Object, Object, Object>> s_pool;
            
            @Override
            public void accept(final E e) {
                this.consumer.accept(e, (T1)this.val1, (T2)this.val2);
            }
            
            public static <E, T1, T2> CallbackStackItem<E, T1, T2> alloc(final T1 val1, final T2 val2, final ICallback<E, T1, T2> consumer) {
                final CallbackStackItem<Object, Object, Object> callbackStackItem = CallbackStackItem.s_pool.alloc();
                callbackStackItem.val1 = (T1)val1;
                callbackStackItem.val2 = (T2)val2;
                callbackStackItem.consumer = (ICallback<Object, Object, Object>)consumer;
                return (CallbackStackItem<E, T1, T2>)callbackStackItem;
            }
            
            @Override
            public void onReleased() {
                this.val1 = null;
                this.val2 = null;
                this.consumer = null;
            }
            
            static {
                s_pool = new Pool<CallbackStackItem<Object, Object, Object>>(CallbackStackItem::new);
            }
        }
        
        public interface ICallback<E, T1, T2>
        {
            void accept(final E p0, final T1 p1, final T2 p2);
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
        
        public static final class CallbackStackItem<E, T1, T2, T3> extends StackItem<T1, T2, T3> implements Consumer<E>
        {
            private ICallback<E, T1, T2, T3> consumer;
            private static final Pool<CallbackStackItem<Object, Object, Object, Object>> s_pool;
            
            @Override
            public void accept(final E e) {
                this.consumer.accept(e, (T1)this.val1, (T2)this.val2, (T3)this.val3);
            }
            
            public static <E, T1, T2, T3> CallbackStackItem<E, T1, T2, T3> alloc(final T1 val1, final T2 val2, final T3 val3, final ICallback<E, T1, T2, T3> consumer) {
                final CallbackStackItem<Object, Object, Object, Object> callbackStackItem = CallbackStackItem.s_pool.alloc();
                callbackStackItem.val1 = (T1)val1;
                callbackStackItem.val2 = (T2)val2;
                callbackStackItem.val3 = (T3)val3;
                callbackStackItem.consumer = (ICallback<Object, Object, Object, Object>)consumer;
                return (CallbackStackItem<E, T1, T2, T3>)callbackStackItem;
            }
            
            @Override
            public void onReleased() {
                this.val1 = null;
                this.val2 = null;
                this.val3 = null;
                this.consumer = null;
            }
            
            static {
                s_pool = new Pool<CallbackStackItem<Object, Object, Object, Object>>(CallbackStackItem::new);
            }
        }
        
        public interface ICallback<E, T1, T2, T3>
        {
            void accept(final E p0, final T1 p1, final T2 p2, final T3 p3);
        }
    }
    
    public static final class Params4
    {
        private static class StackItem<T1, T2, T3, T4> extends PooledObject
        {
            T1 val1;
            T2 val2;
            T3 val3;
            T4 val4;
        }
        
        public static final class CallbackStackItem<E, T1, T2, T3, T4> extends StackItem<T1, T2, T3, T4> implements Consumer<E>
        {
            private ICallback<E, T1, T2, T3, T4> consumer;
            private static final Pool<CallbackStackItem<Object, Object, Object, Object, Object>> s_pool;
            
            @Override
            public void accept(final E e) {
                this.consumer.accept(e, (T1)this.val1, (T2)this.val2, (T3)this.val3, (T4)this.val4);
            }
            
            public static <E, T1, T2, T3, T4> CallbackStackItem<E, T1, T2, T3, T4> alloc(final T1 val1, final T2 val2, final T3 val3, final T4 val4, final ICallback<E, T1, T2, T3, T4> consumer) {
                final CallbackStackItem<Object, Object, Object, Object, Object> callbackStackItem = CallbackStackItem.s_pool.alloc();
                callbackStackItem.val1 = (T1)val1;
                callbackStackItem.val2 = (T2)val2;
                callbackStackItem.val3 = (T3)val3;
                callbackStackItem.val4 = (T4)val4;
                callbackStackItem.consumer = (ICallback<Object, Object, Object, Object, Object>)consumer;
                return (CallbackStackItem<E, T1, T2, T3, T4>)callbackStackItem;
            }
            
            @Override
            public void onReleased() {
                this.val1 = null;
                this.val2 = null;
                this.val3 = null;
                this.val4 = null;
                this.consumer = null;
            }
            
            static {
                s_pool = new Pool<CallbackStackItem<Object, Object, Object, Object, Object>>(CallbackStackItem::new);
            }
        }
        
        public interface ICallback<E, T1, T2, T3, T4>
        {
            void accept(final E p0, final T1 p1, final T2 p2, final T3 p3, final T4 p4);
        }
    }
    
    public static final class Params5
    {
        private static class StackItem<T1, T2, T3, T4, T5> extends PooledObject
        {
            T1 val1;
            T2 val2;
            T3 val3;
            T4 val4;
            T5 val5;
        }
        
        public static final class CallbackStackItem<E, T1, T2, T3, T4, T5> extends StackItem<T1, T2, T3, T4, T5> implements Consumer<E>
        {
            private ICallback<E, T1, T2, T3, T4, T5> consumer;
            private static final Pool<CallbackStackItem<Object, Object, Object, Object, Object, Object>> s_pool;
            
            @Override
            public void accept(final E e) {
                this.consumer.accept(e, (T1)this.val1, (T2)this.val2, (T3)this.val3, (T4)this.val4, (T5)this.val5);
            }
            
            public static <E, T1, T2, T3, T4, T5> CallbackStackItem<E, T1, T2, T3, T4, T5> alloc(final T1 val1, final T2 val2, final T3 val3, final T4 val4, final T5 val5, final ICallback<E, T1, T2, T3, T4, T5> consumer) {
                final CallbackStackItem<Object, Object, Object, Object, Object, Object> callbackStackItem = CallbackStackItem.s_pool.alloc();
                callbackStackItem.val1 = (T1)val1;
                callbackStackItem.val2 = (T2)val2;
                callbackStackItem.val3 = (T3)val3;
                callbackStackItem.val4 = (T4)val4;
                callbackStackItem.val5 = (T5)val5;
                callbackStackItem.consumer = (ICallback<Object, Object, Object, Object, Object, Object>)consumer;
                return (CallbackStackItem<E, T1, T2, T3, T4, T5>)callbackStackItem;
            }
            
            @Override
            public void onReleased() {
                this.val1 = null;
                this.val2 = null;
                this.val3 = null;
                this.val4 = null;
                this.val5 = null;
                this.consumer = null;
            }
            
            static {
                s_pool = new Pool<CallbackStackItem<Object, Object, Object, Object, Object, Object>>(CallbackStackItem::new);
            }
        }
        
        public interface ICallback<E, T1, T2, T3, T4, T5>
        {
            void accept(final E p0, final T1 p1, final T2 p2, final T3 p3, final T4 p4, final T5 p5);
        }
    }
}

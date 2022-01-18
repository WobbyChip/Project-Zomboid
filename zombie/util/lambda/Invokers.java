// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util.lambda;

import zombie.util.Pool;
import zombie.util.PooledObject;

public class Invokers
{
    public static final class Params1
    {
        private static class StackItem<T1> extends PooledObject
        {
            T1 val1;
        }
        
        public static final class CallbackStackItem<T1> extends StackItem<T1> implements Runnable
        {
            private ICallback<T1> invoker;
            private static final Pool<CallbackStackItem<Object>> s_pool;
            
            @Override
            public void run() {
                this.invoker.accept(this.val1);
            }
            
            public static <T1> CallbackStackItem<T1> alloc(final T1 val1, final ICallback<T1> invoker) {
                final CallbackStackItem<Object> callbackStackItem = CallbackStackItem.s_pool.alloc();
                callbackStackItem.val1 = val1;
                callbackStackItem.invoker = (ICallback<Object>)invoker;
                return (CallbackStackItem<T1>)callbackStackItem;
            }
            
            @Override
            public void onReleased() {
                this.val1 = null;
                this.invoker = null;
            }
            
            static {
                s_pool = new Pool<CallbackStackItem<Object>>(CallbackStackItem::new);
            }
        }
        
        public interface ICallback<T1>
        {
            void accept(final T1 p0);
        }
    }
    
    public static final class Params2
    {
        private static class StackItem<T1, T2> extends PooledObject
        {
            T1 val1;
            T2 val2;
        }
        
        public static final class CallbackStackItem<T1, T2> extends StackItem<T1, T2> implements Runnable
        {
            private ICallback<T1, T2> invoker;
            private static final Pool<CallbackStackItem<Object, Object>> s_pool;
            
            @Override
            public void run() {
                this.invoker.accept(this.val1, this.val2);
            }
            
            public static <T1, T2> CallbackStackItem<T1, T2> alloc(final T1 val1, final T2 val2, final ICallback<T1, T2> invoker) {
                final CallbackStackItem<Object, Object> callbackStackItem = CallbackStackItem.s_pool.alloc();
                callbackStackItem.val1 = val1;
                callbackStackItem.val2 = val2;
                callbackStackItem.invoker = (ICallback<Object, Object>)invoker;
                return (CallbackStackItem<T1, T2>)callbackStackItem;
            }
            
            @Override
            public void onReleased() {
                this.val1 = null;
                this.val2 = null;
                this.invoker = null;
            }
            
            static {
                s_pool = new Pool<CallbackStackItem<Object, Object>>(CallbackStackItem::new);
            }
        }
        
        public interface ICallback<T1, T2>
        {
            void accept(final T1 p0, final T2 p1);
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
        
        public static final class CallbackStackItem<T1, T2, T3> extends StackItem<T1, T2, T3> implements Runnable
        {
            private ICallback<T1, T2, T3> invoker;
            private static final Pool<CallbackStackItem<Object, Object, Object>> s_pool;
            
            @Override
            public void run() {
                this.invoker.accept(this.val1, this.val2, this.val3);
            }
            
            public static <T1, T2, T3> CallbackStackItem<T1, T2, T3> alloc(final T1 val1, final T2 val2, final T3 val3, final ICallback<T1, T2, T3> invoker) {
                final CallbackStackItem<Object, Object, Object> callbackStackItem = CallbackStackItem.s_pool.alloc();
                callbackStackItem.val1 = val1;
                callbackStackItem.val2 = val2;
                callbackStackItem.val3 = val3;
                callbackStackItem.invoker = (ICallback<Object, Object, Object>)invoker;
                return (CallbackStackItem<T1, T2, T3>)callbackStackItem;
            }
            
            @Override
            public void onReleased() {
                this.val1 = null;
                this.val2 = null;
                this.val3 = null;
                this.invoker = null;
            }
            
            static {
                s_pool = new Pool<CallbackStackItem<Object, Object, Object>>(CallbackStackItem::new);
            }
        }
        
        public interface ICallback<T1, T2, T3>
        {
            void accept(final T1 p0, final T2 p1, final T3 p2);
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
        
        public static final class CallbackStackItem<T1, T2, T3, T4> extends StackItem<T1, T2, T3, T4> implements Runnable
        {
            private ICallback<T1, T2, T3, T4> invoker;
            private static final Pool<CallbackStackItem<Object, Object, Object, Object>> s_pool;
            
            @Override
            public void run() {
                this.invoker.accept(this.val1, this.val2, this.val3, this.val4);
            }
            
            public static <T1, T2, T3, T4> CallbackStackItem<T1, T2, T3, T4> alloc(final T1 val1, final T2 val2, final T3 val3, final T4 val4, final ICallback<T1, T2, T3, T4> invoker) {
                final CallbackStackItem<Object, Object, Object, Object> callbackStackItem = CallbackStackItem.s_pool.alloc();
                callbackStackItem.val1 = val1;
                callbackStackItem.val2 = val2;
                callbackStackItem.val3 = val3;
                callbackStackItem.val4 = val4;
                callbackStackItem.invoker = (ICallback<Object, Object, Object, Object>)invoker;
                return (CallbackStackItem<T1, T2, T3, T4>)callbackStackItem;
            }
            
            @Override
            public void onReleased() {
                this.val1 = null;
                this.val2 = null;
                this.val3 = null;
                this.val4 = null;
                this.invoker = null;
            }
            
            static {
                s_pool = new Pool<CallbackStackItem<Object, Object, Object, Object>>(CallbackStackItem::new);
            }
        }
        
        public interface ICallback<T1, T2, T3, T4>
        {
            void accept(final T1 p0, final T2 p1, final T3 p2, final T4 p3);
        }
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util.lambda;

import java.util.Comparator;
import zombie.util.Lambda;
import java.util.function.Predicate;
import java.util.function.Consumer;
import zombie.util.Pool;
import java.util.ArrayList;
import zombie.util.IPooledObject;
import java.util.List;
import zombie.util.PooledObject;

public final class Stacks
{
    public abstract static class GenericStack extends PooledObject
    {
        private final List<IPooledObject> m_stackItems;
        
        public GenericStack() {
            this.m_stackItems = new ArrayList<IPooledObject>();
        }
        
        public abstract void invoke();
        
        public void invokeAndRelease() {
            try {
                this.invoke();
            }
            finally {
                this.release();
            }
        }
        
        private <E> E push(final E e) {
            this.m_stackItems.add((IPooledObject)e);
            return e;
        }
        
        @Override
        public void onReleased() {
            this.m_stackItems.forEach(Pool::tryRelease);
            this.m_stackItems.clear();
        }
        
        public <E, T1> Predicate<E> predicate(final T1 t1, final Predicates.Params1.ICallback<E, T1> callback) {
            return this.push(Lambda.predicate(t1, callback));
        }
        
        public <E, T1, T2> Predicate<E> predicate(final T1 t1, final T2 t2, final Predicates.Params2.ICallback<E, T1, T2> callback) {
            return this.push(Lambda.predicate(t1, t2, callback));
        }
        
        public <E, T1, T2, T3> Predicate<E> predicate(final T1 t1, final T2 t2, final T3 t3, final Predicates.Params3.ICallback<E, T1, T2, T3> callback) {
            return this.push(Lambda.predicate(t1, t2, t3, callback));
        }
        
        public <E, T1> Comparator<E> comparator(final T1 t1, final Comparators.Params1.ICallback<E, T1> callback) {
            return this.push(Lambda.comparator(t1, callback));
        }
        
        public <E, T1, T2> Comparator<E> comparator(final T1 t1, final T2 t2, final Comparators.Params2.ICallback<E, T1, T2> callback) {
            return this.push(Lambda.comparator(t1, t2, callback));
        }
        
        public <E, T1> Consumer<E> consumer(final T1 t1, final Consumers.Params1.ICallback<E, T1> callback) {
            return this.push(Lambda.consumer(t1, callback));
        }
        
        public <E, T1, T2> Consumer<E> consumer(final T1 t1, final T2 t2, final Consumers.Params2.ICallback<E, T1, T2> callback) {
            return this.push(Lambda.consumer(t1, t2, callback));
        }
        
        public <T1> Runnable invoker(final T1 t1, final Invokers.Params1.ICallback<T1> callback) {
            return this.push(Lambda.invoker(t1, callback));
        }
        
        public <T1, T2> Runnable invoker(final T1 t1, final T2 t2, final Invokers.Params2.ICallback<T1, T2> callback) {
            return this.push(Lambda.invoker(t1, t2, callback));
        }
        
        public <T1, T2, T3> Runnable invoker(final T1 t1, final T2 t2, final T3 t3, final Invokers.Params3.ICallback<T1, T2, T3> callback) {
            return this.push(Lambda.invoker(t1, t2, t3, callback));
        }
        
        public <T1, T2, T3, T4> Runnable invoker(final T1 t1, final T2 t2, final T3 t3, final T4 t4, final Invokers.Params4.ICallback<T1, T2, T3, T4> callback) {
            return this.push(Lambda.invoker(t1, t2, t3, t4, callback));
        }
    }
    
    public static final class Params1
    {
        private abstract static class StackItem<T1> extends GenericStack
        {
            T1 val1;
        }
        
        public static final class CallbackStackItem<T1> extends StackItem<T1>
        {
            private ICallback<T1> callback;
            private static final Pool<CallbackStackItem<Object>> s_pool;
            
            @Override
            public void invoke() {
                this.callback.accept(this, this.val1);
            }
            
            public static <T1> CallbackStackItem<T1> alloc(final T1 val1, final ICallback<T1> callback) {
                final CallbackStackItem<Object> callbackStackItem = CallbackStackItem.s_pool.alloc();
                callbackStackItem.val1 = val1;
                callbackStackItem.callback = (ICallback<Object>)callback;
                return (CallbackStackItem<T1>)callbackStackItem;
            }
            
            @Override
            public void onReleased() {
                this.val1 = null;
                this.callback = null;
                super.onReleased();
            }
            
            static {
                s_pool = new Pool<CallbackStackItem<Object>>(CallbackStackItem::new);
            }
        }
        
        public interface ICallback<T1>
        {
            void accept(final GenericStack p0, final T1 p1);
        }
    }
    
    public static final class Params2
    {
        private abstract static class StackItem<T1, T2> extends GenericStack
        {
            T1 val1;
            T2 val2;
        }
        
        public static final class CallbackStackItem<T1, T2> extends StackItem<T1, T2>
        {
            private ICallback<T1, T2> callback;
            private static final Pool<CallbackStackItem<Object, Object>> s_pool;
            
            @Override
            public void invoke() {
                this.callback.accept(this, this.val1, this.val2);
            }
            
            public static <T1, T2> CallbackStackItem<T1, T2> alloc(final T1 val1, final T2 val2, final ICallback<T1, T2> callback) {
                final CallbackStackItem<Object, Object> callbackStackItem = CallbackStackItem.s_pool.alloc();
                callbackStackItem.val1 = val1;
                callbackStackItem.val2 = val2;
                callbackStackItem.callback = (ICallback<Object, Object>)callback;
                return (CallbackStackItem<T1, T2>)callbackStackItem;
            }
            
            @Override
            public void onReleased() {
                this.val1 = null;
                this.val2 = null;
                this.callback = null;
                super.onReleased();
            }
            
            static {
                s_pool = new Pool<CallbackStackItem<Object, Object>>(CallbackStackItem::new);
            }
        }
        
        public interface ICallback<T1, T2>
        {
            void accept(final GenericStack p0, final T1 p1, final T2 p2);
        }
    }
    
    public static final class Params3
    {
        private abstract static class StackItem<T1, T2, T3> extends GenericStack
        {
            T1 val1;
            T2 val2;
            T3 val3;
        }
        
        public static final class CallbackStackItem<T1, T2, T3> extends StackItem<T1, T2, T3>
        {
            private ICallback<T1, T2, T3> callback;
            private static final Pool<CallbackStackItem<Object, Object, Object>> s_pool;
            
            @Override
            public void invoke() {
                this.callback.accept(this, this.val1, this.val2, this.val3);
            }
            
            public static <T1, T2, T3> CallbackStackItem<T1, T2, T3> alloc(final T1 val1, final T2 val2, final T3 val3, final ICallback<T1, T2, T3> callback) {
                final CallbackStackItem<Object, Object, Object> callbackStackItem = CallbackStackItem.s_pool.alloc();
                callbackStackItem.val1 = val1;
                callbackStackItem.val2 = val2;
                callbackStackItem.val3 = val3;
                callbackStackItem.callback = (ICallback<Object, Object, Object>)callback;
                return (CallbackStackItem<T1, T2, T3>)callbackStackItem;
            }
            
            @Override
            public void onReleased() {
                this.val1 = null;
                this.val2 = null;
                this.val3 = null;
                this.callback = null;
                super.onReleased();
            }
            
            static {
                s_pool = new Pool<CallbackStackItem<Object, Object, Object>>(CallbackStackItem::new);
            }
        }
        
        public interface ICallback<T1, T2, T3>
        {
            void accept(final GenericStack p0, final T1 p1, final T2 p2, final T3 p3);
        }
    }
    
    public static final class Params4
    {
        private abstract static class StackItem<T1, T2, T3, T4> extends GenericStack
        {
            T1 val1;
            T2 val2;
            T3 val3;
            T4 val4;
        }
        
        public static final class CallbackStackItem<T1, T2, T3, T4> extends StackItem<T1, T2, T3, T4>
        {
            private ICallback<T1, T2, T3, T4> callback;
            private static final Pool<CallbackStackItem<Object, Object, Object, Object>> s_pool;
            
            @Override
            public void invoke() {
                this.callback.accept(this, this.val1, this.val2, this.val3, this.val4);
            }
            
            public static <T1, T2, T3, T4> CallbackStackItem<T1, T2, T3, T4> alloc(final T1 val1, final T2 val2, final T3 val3, final T4 val4, final ICallback<T1, T2, T3, T4> callback) {
                final CallbackStackItem<Object, Object, Object, Object> callbackStackItem = CallbackStackItem.s_pool.alloc();
                callbackStackItem.val1 = val1;
                callbackStackItem.val2 = val2;
                callbackStackItem.val3 = val3;
                callbackStackItem.val4 = val4;
                callbackStackItem.callback = (ICallback<Object, Object, Object, Object>)callback;
                return (CallbackStackItem<T1, T2, T3, T4>)callbackStackItem;
            }
            
            @Override
            public void onReleased() {
                this.val1 = null;
                this.val2 = null;
                this.val3 = null;
                this.val4 = null;
                this.callback = null;
                super.onReleased();
            }
            
            static {
                s_pool = new Pool<CallbackStackItem<Object, Object, Object, Object>>(CallbackStackItem::new);
            }
        }
        
        public interface ICallback<T1, T2, T3, T4>
        {
            void accept(final GenericStack p0, final T1 p1, final T2 p2, final T3 p3, final T4 p4);
        }
    }
    
    public static final class Params5
    {
        private abstract static class StackItem<T1, T2, T3, T4, T5> extends GenericStack
        {
            T1 val1;
            T2 val2;
            T3 val3;
            T4 val4;
            T5 val5;
        }
        
        public static final class CallbackStackItem<T1, T2, T3, T4, T5> extends StackItem<T1, T2, T3, T4, T5>
        {
            private ICallback<T1, T2, T3, T4, T5> callback;
            private static final Pool<CallbackStackItem<Object, Object, Object, Object, Object>> s_pool;
            
            @Override
            public void invoke() {
                this.callback.accept(this, this.val1, this.val2, this.val3, this.val4, this.val5);
            }
            
            public static <T1, T2, T3, T4, T5> CallbackStackItem<T1, T2, T3, T4, T5> alloc(final T1 val1, final T2 val2, final T3 val3, final T4 val4, final T5 val5, final ICallback<T1, T2, T3, T4, T5> callback) {
                final CallbackStackItem<Object, Object, Object, Object, Object> callbackStackItem = CallbackStackItem.s_pool.alloc();
                callbackStackItem.val1 = val1;
                callbackStackItem.val2 = val2;
                callbackStackItem.val3 = val3;
                callbackStackItem.val4 = val4;
                callbackStackItem.val5 = val5;
                callbackStackItem.callback = (ICallback<Object, Object, Object, Object, Object>)callback;
                return (CallbackStackItem<T1, T2, T3, T4, T5>)callbackStackItem;
            }
            
            @Override
            public void onReleased() {
                this.val1 = null;
                this.val2 = null;
                this.val3 = null;
                this.val4 = null;
                this.val5 = null;
                this.callback = null;
                super.onReleased();
            }
            
            static {
                s_pool = new Pool<CallbackStackItem<Object, Object, Object, Object, Object>>(CallbackStackItem::new);
            }
        }
        
        public interface ICallback<T1, T2, T3, T4, T5>
        {
            void accept(final GenericStack p0, final T1 p1, final T2 p2, final T3 p3, final T4 p4, final T5 p5);
        }
    }
    
    public static final class Params6
    {
        private abstract static class StackItem<T1, T2, T3, T4, T5, T6> extends GenericStack
        {
            T1 val1;
            T2 val2;
            T3 val3;
            T4 val4;
            T5 val5;
            T6 val6;
        }
        
        public static final class CallbackStackItem<T1, T2, T3, T4, T5, T6> extends StackItem<T1, T2, T3, T4, T5, T6>
        {
            private ICallback<T1, T2, T3, T4, T5, T6> callback;
            private static final Pool<CallbackStackItem<Object, Object, Object, Object, Object, Object>> s_pool;
            
            @Override
            public void invoke() {
                this.callback.accept(this, this.val1, this.val2, this.val3, this.val4, this.val5, this.val6);
            }
            
            public static <T1, T2, T3, T4, T5, T6> CallbackStackItem<T1, T2, T3, T4, T5, T6> alloc(final T1 val1, final T2 val2, final T3 val3, final T4 val4, final T5 val5, final T6 val6, final ICallback<T1, T2, T3, T4, T5, T6> callback) {
                final CallbackStackItem<Object, Object, Object, Object, Object, Object> callbackStackItem = CallbackStackItem.s_pool.alloc();
                callbackStackItem.val1 = val1;
                callbackStackItem.val2 = val2;
                callbackStackItem.val3 = val3;
                callbackStackItem.val4 = val4;
                callbackStackItem.val5 = val5;
                callbackStackItem.val6 = val6;
                callbackStackItem.callback = (ICallback<Object, Object, Object, Object, Object, Object>)callback;
                return (CallbackStackItem<T1, T2, T3, T4, T5, T6>)callbackStackItem;
            }
            
            @Override
            public void onReleased() {
                this.val1 = null;
                this.val2 = null;
                this.val3 = null;
                this.val4 = null;
                this.val5 = null;
                this.val6 = null;
                this.callback = null;
                super.onReleased();
            }
            
            static {
                s_pool = new Pool<CallbackStackItem<Object, Object, Object, Object, Object, Object>>(CallbackStackItem::new);
            }
        }
        
        public interface ICallback<T1, T2, T3, T4, T5, T6>
        {
            void accept(final GenericStack p0, final T1 p1, final T2 p2, final T3 p3, final T4 p4, final T5 p5, final T6 p6);
        }
    }
}

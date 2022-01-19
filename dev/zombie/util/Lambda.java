// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util;

import java.util.function.BiPredicate;
import zombie.util.lambda.ReturnValueContainerPrimitives;
import zombie.util.lambda.IntSupplierFunction;
import zombie.util.lambda.ReturnValueContainer;
import java.util.function.Function;
import java.util.List;
import java.util.function.BiConsumer;
import zombie.util.lambda.Stacks;
import zombie.util.lambda.Invokers;
import java.util.function.Consumer;
import zombie.util.lambda.Consumers;
import java.util.Comparator;
import zombie.util.lambda.Comparators;
import java.util.function.Predicate;
import zombie.util.lambda.Predicates;

public final class Lambda
{
    public static <E, T1> Predicate<E> predicate(final T1 t1, final Predicates.Params1.ICallback<E, T1> callback) {
        return Predicates.Params1.CallbackStackItem.alloc(t1, callback);
    }
    
    public static <E, T1, T2> Predicate<E> predicate(final T1 t1, final T2 t2, final Predicates.Params2.ICallback<E, T1, T2> callback) {
        return Predicates.Params2.CallbackStackItem.alloc(t1, t2, callback);
    }
    
    public static <E, T1, T2, T3> Predicate<E> predicate(final T1 t1, final T2 t2, final T3 t3, final Predicates.Params3.ICallback<E, T1, T2, T3> callback) {
        return Predicates.Params3.CallbackStackItem.alloc(t1, t2, t3, callback);
    }
    
    public static <E, T1> Comparator<E> comparator(final T1 t1, final Comparators.Params1.ICallback<E, T1> callback) {
        return Comparators.Params1.CallbackStackItem.alloc(t1, callback);
    }
    
    public static <E, T1, T2> Comparator<E> comparator(final T1 t1, final T2 t2, final Comparators.Params2.ICallback<E, T1, T2> callback) {
        return Comparators.Params2.CallbackStackItem.alloc(t1, t2, callback);
    }
    
    public static <E, T1> Consumer<E> consumer(final T1 t1, final Consumers.Params1.ICallback<E, T1> callback) {
        return Consumers.Params1.CallbackStackItem.alloc(t1, callback);
    }
    
    public static <E, T1, T2> Consumer<E> consumer(final T1 t1, final T2 t2, final Consumers.Params2.ICallback<E, T1, T2> callback) {
        return Consumers.Params2.CallbackStackItem.alloc(t1, t2, callback);
    }
    
    public static <E, T1, T2, T3> Consumer<E> consumer(final T1 t1, final T2 t2, final T3 t3, final Consumers.Params3.ICallback<E, T1, T2, T3> callback) {
        return Consumers.Params3.CallbackStackItem.alloc(t1, t2, t3, callback);
    }
    
    public static <E, T1, T2, T3, T4> Consumer<E> consumer(final T1 t1, final T2 t2, final T3 t3, final T4 t4, final Consumers.Params4.ICallback<E, T1, T2, T3, T4> callback) {
        return Consumers.Params4.CallbackStackItem.alloc(t1, t2, t3, t4, callback);
    }
    
    public static <E, T1, T2, T3, T4, T5> Consumer<E> consumer(final T1 t1, final T2 t2, final T3 t3, final T4 t4, final T5 t5, final Consumers.Params5.ICallback<E, T1, T2, T3, T4, T5> callback) {
        return Consumers.Params5.CallbackStackItem.alloc(t1, t2, t3, t4, t5, callback);
    }
    
    public static <T1> Runnable invoker(final T1 t1, final Invokers.Params1.ICallback<T1> callback) {
        return Invokers.Params1.CallbackStackItem.alloc(t1, callback);
    }
    
    public static <T1, T2> Runnable invoker(final T1 t1, final T2 t2, final Invokers.Params2.ICallback<T1, T2> callback) {
        return Invokers.Params2.CallbackStackItem.alloc(t1, t2, callback);
    }
    
    public static <T1, T2, T3> Runnable invoker(final T1 t1, final T2 t2, final T3 t3, final Invokers.Params3.ICallback<T1, T2, T3> callback) {
        return Invokers.Params3.CallbackStackItem.alloc(t1, t2, t3, callback);
    }
    
    public static <T1, T2, T3, T4> Runnable invoker(final T1 t1, final T2 t2, final T3 t3, final T4 t4, final Invokers.Params4.ICallback<T1, T2, T3, T4> callback) {
        return Invokers.Params4.CallbackStackItem.alloc(t1, t2, t3, t4, callback);
    }
    
    public static <T1> void capture(final T1 t1, final Stacks.Params1.ICallback<T1> callback) {
        Stacks.Params1.CallbackStackItem.alloc(t1, callback).invokeAndRelease();
    }
    
    public static <T1, T2> void capture(final T1 t1, final T2 t2, final Stacks.Params2.ICallback<T1, T2> callback) {
        Stacks.Params2.CallbackStackItem.alloc(t1, t2, callback).invokeAndRelease();
    }
    
    public static <T1, T2, T3> void capture(final T1 t1, final T2 t2, final T3 t3, final Stacks.Params3.ICallback<T1, T2, T3> callback) {
        Stacks.Params3.CallbackStackItem.alloc(t1, t2, t3, callback).invokeAndRelease();
    }
    
    public static <T1, T2, T3, T4> void capture(final T1 t1, final T2 t2, final T3 t3, final T4 t4, final Stacks.Params4.ICallback<T1, T2, T3, T4> callback) {
        Stacks.Params4.CallbackStackItem.alloc(t1, t2, t3, t4, callback).invokeAndRelease();
    }
    
    public static <T1, T2, T3, T4, T5> void capture(final T1 t1, final T2 t2, final T3 t3, final T4 t4, final T5 t5, final Stacks.Params5.ICallback<T1, T2, T3, T4, T5> callback) {
        Stacks.Params5.CallbackStackItem.alloc(t1, t2, t3, t4, t5, callback).invokeAndRelease();
    }
    
    public static <T1, T2, T3, T4, T5, T6> void capture(final T1 t1, final T2 t2, final T3 t3, final T4 t4, final T5 t5, final T6 t6, final Stacks.Params6.ICallback<T1, T2, T3, T4, T5, T6> callback) {
        Stacks.Params6.CallbackStackItem.alloc(t1, t2, t3, t4, t5, t6, callback).invokeAndRelease();
    }
    
    public static <E, T1> void forEach(final Consumer<Consumer<E>> consumer, final T1 t1, final Consumers.Params1.ICallback<E, T1> callback) {
        capture(consumer, t1, callback, (genericStack, consumer2, o, callback2) -> consumer2.accept(genericStack.consumer(o, callback2)));
    }
    
    public static <E, T1, T2> void forEach(final Consumer<Consumer<E>> consumer, final T1 t1, final T2 t2, final Consumers.Params2.ICallback<E, T1, T2> callback) {
        capture(consumer, t1, t2, callback, (genericStack, consumer2, o, o2, callback2) -> consumer2.accept(genericStack.consumer(o, o2, callback2)));
    }
    
    public static <E, T1> void forEachFrom(final BiConsumer<List<E>, Consumer<E>> biConsumer, final List<E> list, final T1 t1, final Consumers.Params1.ICallback<E, T1> callback) {
        capture(biConsumer, list, t1, callback, (genericStack, biConsumer2, list2, o, callback2) -> biConsumer2.accept(list2, genericStack.consumer(o, callback2)));
    }
    
    public static <E, T1, T2> void forEachFrom(final BiConsumer<List<E>, Consumer<E>> biConsumer, final List<E> list, final T1 t1, final T2 t2, final Consumers.Params2.ICallback<E, T1, T2> callback) {
        capture(biConsumer, list, t1, t2, callback, (genericStack, biConsumer2, list2, o, o2, callback2) -> biConsumer2.accept(list2, genericStack.consumer(o, o2, callback2)));
    }
    
    public static <E, F, T1> void forEachFrom(final BiConsumer<F, Consumer<E>> biConsumer, final F n, final T1 t1, final Consumers.Params1.ICallback<E, T1> callback) {
        capture(biConsumer, n, t1, callback, (genericStack, biConsumer2, o, o2, callback2) -> biConsumer2.accept(o, genericStack.consumer(o2, callback2)));
    }
    
    public static <E, F, T1, T2> void forEachFrom(final BiConsumer<F, Consumer<E>> biConsumer, final F n, final T1 t1, final T2 t2, final Consumers.Params2.ICallback<E, T1, T2> callback) {
        capture(biConsumer, n, t1, t2, callback, (genericStack, biConsumer2, o, o2, o3, callback2) -> biConsumer2.accept(o, genericStack.consumer(o2, o3, callback2)));
    }
    
    public static <E, T1, R> R find(final Function<Predicate<E>, R> function, final T1 t1, final Predicates.Params1.ICallback<E, T1> callback) {
        final ReturnValueContainer<Object> alloc = ReturnValueContainer.alloc();
        capture(function, t1, callback, alloc, (genericStack, function2, o, callback2, returnValueContainer) -> returnValueContainer.ReturnVal = function2.apply(genericStack.predicate(o, callback2)));
        final Object returnVal = alloc.ReturnVal;
        alloc.release();
        return (R)returnVal;
    }
    
    public static <E, T1> int indexOf(final IntSupplierFunction<Predicate<E>> intSupplierFunction, final T1 t1, final Predicates.Params1.ICallback<E, T1> callback) {
        final ReturnValueContainerPrimitives.RVInt alloc = ReturnValueContainerPrimitives.RVInt.alloc();
        capture(intSupplierFunction, t1, callback, alloc, (genericStack, intSupplierFunction2, o, callback2, rvInt) -> rvInt.ReturnVal = intSupplierFunction2.getInt(genericStack.predicate(o, callback2)));
        final int returnVal = alloc.ReturnVal;
        alloc.release();
        return returnVal;
    }
    
    public static <E, T1> boolean contains(final Predicate<Predicate<E>> predicate, final T1 t1, final Predicates.Params1.ICallback<E, T1> callback) {
        final ReturnValueContainerPrimitives.RVBoolean alloc = ReturnValueContainerPrimitives.RVBoolean.alloc();
        capture(predicate, t1, callback, alloc, (genericStack, predicate2, o, callback2, rvBoolean) -> rvBoolean.ReturnVal = predicate2.test(genericStack.predicate(o, callback2)));
        final Boolean value = alloc.ReturnVal;
        alloc.release();
        return value;
    }
    
    public static <E, F extends Iterable<E>, T1> boolean containsFrom(final BiPredicate<F, Predicate<E>> biPredicate, final F n, final T1 t1, final Predicates.Params1.ICallback<E, T1> callback) {
        final ReturnValueContainerPrimitives.RVBoolean alloc = ReturnValueContainerPrimitives.RVBoolean.alloc();
        capture(biPredicate, n, t1, callback, alloc, (genericStack, biPredicate2, iterable, o, callback2, rvBoolean) -> rvBoolean.ReturnVal = biPredicate2.test(iterable, genericStack.predicate(o, callback2)));
        final Boolean value = alloc.ReturnVal;
        alloc.release();
        return value;
    }
    
    public static <T1> void invoke(final Consumer<Runnable> consumer, final T1 t1, final Invokers.Params1.ICallback<T1> callback) {
        capture(consumer, t1, callback, (genericStack, consumer2, o, callback2) -> consumer2.accept(genericStack.invoker(o, callback2)));
    }
    
    public static <T1, T2> void invoke(final Consumer<Runnable> consumer, final T1 t1, final T2 t2, final Invokers.Params2.ICallback<T1, T2> callback) {
        capture(consumer, t1, t2, callback, (genericStack, consumer2, o, o2, callback2) -> consumer2.accept(genericStack.invoker(o, o2, callback2)));
    }
}

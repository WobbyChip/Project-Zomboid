// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util.list;

import zombie.util.StringUtils;
import java.util.Comparator;
import java.util.Stack;
import java.util.HashMap;
import java.util.function.Consumer;
import zombie.core.math.PZMath;
import java.util.function.Supplier;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.function.Function;
import zombie.util.Pool;
import java.util.function.Predicate;
import zombie.util.ICloner;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collection;
import java.util.List;
import zombie.core.Rand;

public class PZArrayUtil
{
    public static final int[] emptyIntArray;
    public static final float[] emptyFloatArray;
    
    public static <E> E pickRandom(final E[] array) {
        if (array.length == 0) {
            return null;
        }
        return array[Rand.Next(array.length)];
    }
    
    public static <E> E pickRandom(final List<E> list) {
        if (list.isEmpty()) {
            return null;
        }
        return list.get(Rand.Next(list.size()));
    }
    
    public static <E> E pickRandom(final Collection<E> collection) {
        if (collection.isEmpty()) {
            return null;
        }
        return getElementAt(collection, Rand.Next(collection.size()));
    }
    
    public static <E> E pickRandom(final Iterable<E> iterable) {
        final int size = getSize(iterable);
        if (size == 0) {
            return null;
        }
        return getElementAt(iterable, Rand.Next(size));
    }
    
    public static <E> int getSize(final Iterable<E> iterable) {
        int n = 0;
        final Iterator<E> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            ++n;
            iterator.next();
        }
        return n;
    }
    
    public static <E> E getElementAt(final Iterable<E> iterable, final int n) throws ArrayIndexOutOfBoundsException {
        E next = null;
        final Iterator<E> iterator = iterable.iterator();
        for (int i = 0; i <= n; ++i) {
            if (!iterator.hasNext()) {
                throw new ArrayIndexOutOfBoundsException(i);
            }
            if (i == n) {
                next = iterator.next();
            }
        }
        return next;
    }
    
    public static <E> void copy(final ArrayList<E> list, final ArrayList<E> list2) {
        copy(list, list2, o -> o);
    }
    
    public static <E> void copy(final ArrayList<E> list, final ArrayList<E> list2, final ICloner<E> cloner) {
        if (list == list2) {
            return;
        }
        list.clear();
        list.ensureCapacity(list2.size());
        for (int i = 0; i < list2.size(); ++i) {
            list.add(cloner.clone(list2.get(i)));
        }
    }
    
    public static <E> int indexOf(final E[] array, final Predicate<E> predicate) {
        try {
            for (int i = 0; i < array.length; ++i) {
                if (predicate.test(array[i])) {
                    return i;
                }
            }
            return -1;
        }
        finally {
            Pool.tryRelease(predicate);
        }
    }
    
    public static <E> int indexOf(final List<E> list, final Predicate<E> predicate) {
        try {
            int n = -1;
            for (int i = 0; i < list.size(); ++i) {
                if (predicate.test(list.get(i))) {
                    n = i;
                    break;
                }
            }
            return n;
        }
        finally {
            Pool.tryRelease(predicate);
        }
    }
    
    public static <E> boolean contains(final E[] array, final Predicate<E> predicate) {
        return indexOf(array, predicate) > -1;
    }
    
    public static <E> boolean contains(final List<E> list, final Predicate<E> predicate) {
        return indexOf(list, predicate) > -1;
    }
    
    public static <E> boolean contains(final Collection<E> collection, final Predicate<E> predicate) {
        if (collection instanceof List) {
            return contains((List<E>)collection, predicate);
        }
        try {
            boolean b = false;
            final Iterator<E> iterator = (Iterator<E>)collection.iterator();
            while (iterator.hasNext()) {
                if (predicate.test(iterator.next())) {
                    b = true;
                    break;
                }
            }
            return b;
        }
        finally {
            Pool.tryRelease(predicate);
        }
    }
    
    public static <E> boolean contains(final Iterable<E> iterable, final Predicate<E> predicate) {
        if (iterable instanceof List) {
            return indexOf((List<E>)iterable, predicate) > -1;
        }
        try {
            boolean b = false;
            final Iterator<E> iterator = (Iterator<E>)iterable.iterator();
            while (iterator.hasNext()) {
                if (predicate.test(iterator.next())) {
                    b = true;
                    break;
                }
            }
            return b;
        }
        finally {
            Pool.tryRelease(predicate);
        }
    }
    
    public static <E> E find(final List<E> list, final Predicate<E> predicate) {
        final int index = indexOf(list, predicate);
        if (index > -1) {
            return list.get(index);
        }
        return null;
    }
    
    public static <E> E find(final Iterable<E> iterable, final Predicate<E> predicate) {
        if (iterable instanceof List) {
            return find((List<E>)iterable, predicate);
        }
        try {
            for (final E next : iterable) {
                if (predicate.test(next)) {
                    return next;
                }
            }
            return null;
        }
        finally {
            Pool.tryRelease(predicate);
        }
    }
    
    public static <E, S> List<E> listConvert(final List<S> list, final Function<S, E> function) {
        if (list.isEmpty()) {
            return (List<E>)PZArrayList.emptyList();
        }
        return (List<E>)new PZConvertList((List<Object>)list, (Function<Object, Object>)function);
    }
    
    public static <E, S> Iterable<E> itConvert(final Iterable<S> iterable, final Function<S, E> function) {
        return new PZConvertIterable<E, Object>(iterable, function);
    }
    
    public static <E, S> List<E> listConvert(final List<S> list, final List<E> list2, final Function<S, E> function) {
        list2.clear();
        for (int i = 0; i < list.size(); ++i) {
            list2.add(function.apply(list.get(i)));
        }
        return list2;
    }
    
    public static <E, S, T1> List<E> listConvert(final List<S> list, final List<E> list2, final T1 t1, final IListConverter1Param<S, E, T1> listConverter1Param) {
        list2.clear();
        for (int i = 0; i < list.size(); ++i) {
            list2.add(listConverter1Param.convert(list.get(i), t1));
        }
        return list2;
    }
    
    private static <E> List<E> asList(final E[] a) {
        return Arrays.asList(a);
    }
    
    private static List<Float> asList(final float[] array) {
        return new PrimitiveFloatList(array);
    }
    
    private static <E> Iterable<E> asSafeIterable(final E[] array) {
        return (Iterable<E>)((array != null) ? asList(array) : PZEmptyIterable.getInstance());
    }
    
    private static Iterable<Float> asSafeIterable(final float[] array) {
        return (Iterable<Float>)((array != null) ? asList(array) : PZEmptyIterable.getInstance());
    }
    
    public static String arrayToString(final float[] array) {
        return arrayToString(asSafeIterable(array));
    }
    
    public static String arrayToString(final float[] array, final String s, final String s2, final String s3) {
        return arrayToString(asSafeIterable(array), s, s2, s3);
    }
    
    public static <E> String arrayToString(final E[] array) {
        return arrayToString((Iterable<Object>)asSafeIterable((E[])array));
    }
    
    public static <E> String arrayToString(final E[] array, final String s, final String s2, final String s3) {
        return arrayToString((Iterable<Object>)asSafeIterable((E[])array), s, s2, s3);
    }
    
    public static <E> String arrayToString(final Iterable<E> iterable, final Function<E, String> function) {
        return arrayToString(iterable, function, "{", "}", System.lineSeparator());
    }
    
    public static <E> String arrayToString(final Iterable<E> iterable) {
        return arrayToString(iterable, String::valueOf, "{", "}", System.lineSeparator());
    }
    
    public static <E> String arrayToString(final Iterable<E> iterable, final String s, final String s2, final String s3) {
        return arrayToString(iterable, String::valueOf, s, s2, s3);
    }
    
    public static <E> String arrayToString(final Iterable<E> iterable, final Function<E, String> function, final String str, final String str2, final String str3) {
        final StringBuilder sb = new StringBuilder(str);
        if (iterable != null) {
            int n = 1;
            for (final E next : iterable) {
                if (n == 0) {
                    sb.append(str3);
                }
                sb.append(function.apply(next));
                n = 0;
            }
        }
        sb.append(str2);
        Pool.tryRelease(function);
        return sb.toString();
    }
    
    public static <E> E[] newInstance(final Class<?> componentType, final int length) {
        return (E[])Array.newInstance(componentType, length);
    }
    
    public static <E> E[] newInstance(final Class<?> clazz, final int n, final Supplier<E> supplier) {
        final E[] instance = newInstance(clazz, n);
        for (int i = 0; i < instance.length; ++i) {
            instance[i] = supplier.get();
        }
        return instance;
    }
    
    public static <E> E[] newInstance(final Class<?> clazz, final E[] array, final int n) {
        return newInstance(clazz, array, n, false, () -> null);
    }
    
    public static <E> E[] newInstance(final Class<?> clazz, final E[] array, final int n, final boolean b) {
        return newInstance(clazz, array, n, b, () -> null);
    }
    
    public static <E> E[] newInstance(final Class<?> clazz, final E[] array, final int n, final Supplier<E> supplier) {
        return newInstance(clazz, array, n, false, supplier);
    }
    
    public static <E> E[] newInstance(final Class<?> clazz, final E[] array, final int n, final boolean b, final Supplier<E> supplier) {
        if (array == null) {
            return newInstance(clazz, n, supplier);
        }
        final int length = array.length;
        if (length == n) {
            return array;
        }
        if (b && length > n) {
            return array;
        }
        final E[] instance = newInstance(clazz, n);
        arrayCopy(instance, array, 0, PZMath.min(n, length));
        if (n > length) {
            for (int i = length; i < n; ++i) {
                instance[i] = supplier.get();
            }
        }
        if (n < length) {
            for (int j = n; j < length; ++j) {
                array[j] = Pool.tryRelease(array[j]);
            }
        }
        return instance;
    }
    
    public static float[] add(final float[] array, final float n) {
        final float[] array2 = new float[array.length + 1];
        arrayCopy(array2, array, 0, array.length);
        array2[array.length] = n;
        return array2;
    }
    
    public static <E> E[] add(final E[] array, final E e) {
        final E[] instance = newInstance(array.getClass().getComponentType(), array.length + 1);
        arrayCopy(instance, array, 0, array.length);
        instance[array.length] = e;
        return instance;
    }
    
    public static <E> E[] concat(final E[] array, final E[] array2) {
        final boolean b = array == null || array.length == 0;
        final boolean b2 = array2 == null || array2.length == 0;
        if (b && b2) {
            return null;
        }
        if (b) {
            return clone(array2);
        }
        if (b2) {
            return array;
        }
        final E[] instance = newInstance(array.getClass().getComponentType(), array.length + array2.length);
        arrayCopy(instance, array, 0, array.length);
        arrayCopy(instance, array2, array.length, instance.length);
        return instance;
    }
    
    public static <E, S extends E> E[] arrayCopy(final E[] array, final S[] array2, final int n, final int n2) {
        for (int i = n; i < n2; ++i) {
            array[i] = array2[i];
        }
        return array;
    }
    
    public static float[] arrayCopy(final float[] array, final float[] array2, final int n, final int n2) {
        for (int i = n; i < n2; ++i) {
            array[i] = array2[i];
        }
        return array;
    }
    
    public static int[] arrayCopy(final int[] array, final int[] array2, final int n, final int n2) {
        for (int i = n; i < n2; ++i) {
            array[i] = array2[i];
        }
        return array;
    }
    
    public static <L extends List<E>, E> L arrayCopy(final L l, final List<? extends E> list) {
        l.clear();
        l.addAll(list);
        return l;
    }
    
    public static <E> E[] arrayCopy(final E[] array, final List<? extends E> list) {
        for (int i = 0; i < list.size(); ++i) {
            array[i] = (E)list.get(i);
        }
        return array;
    }
    
    public static <E, S extends E> E[] arrayCopy(final E[] array, final S[] array2) {
        System.arraycopy(array2, 0, array, 0, array2.length);
        return array;
    }
    
    public static <L extends List<E>, E, S> L arrayConvert(final L l, final List<S> list, final Function<S, E> function) {
        l.clear();
        for (int i = 0; i < list.size(); ++i) {
            l.add(function.apply(list.get(i)));
        }
        return l;
    }
    
    public static float[] clone(final float[] array) {
        if (isNullOrEmpty(array)) {
            return array;
        }
        final float[] array2 = new float[array.length];
        arrayCopy(array2, array, 0, array.length);
        return array2;
    }
    
    public static <E> E[] clone(final E[] array) {
        if (isNullOrEmpty(array)) {
            return array;
        }
        final E[] instance = newInstance(array.getClass().getComponentType(), array.length);
        arrayCopy(instance, array, 0, array.length);
        return instance;
    }
    
    public static <E> boolean isNullOrEmpty(final E[] array) {
        return array == null || array.length == 0;
    }
    
    public static boolean isNullOrEmpty(final int[] array) {
        return array == null || array.length == 0;
    }
    
    public static boolean isNullOrEmpty(final float[] array) {
        return array == null || array.length == 0;
    }
    
    public static <E> boolean isNullOrEmpty(final List<E> list) {
        return list == null || list.isEmpty();
    }
    
    public static <E> boolean isNullOrEmpty(final Iterable<E> iterable) {
        if (iterable instanceof List) {
            return isNullOrEmpty((List<Object>)iterable);
        }
        boolean b = true;
        final Iterator<Object> iterator = iterable.iterator();
        if (iterator.hasNext()) {
            iterator.next();
            b = false;
        }
        return b;
    }
    
    public static <E> E getOrDefault(final List<E> list, final int n) {
        return getOrDefault(list, n, (E)null);
    }
    
    public static <E> E getOrDefault(final List<E> list, final int n, final E e) {
        if (n >= 0 && n < list.size()) {
            return list.get(n);
        }
        return e;
    }
    
    public static <E> E getOrDefault(final E[] array, final int n, final E e) {
        if (array != null && n >= 0 && n < array.length) {
            return array[n];
        }
        return e;
    }
    
    public static float getOrDefault(final float[] array, final int n, final float n2) {
        if (array != null && n >= 0 && n < array.length) {
            return array[n];
        }
        return n2;
    }
    
    public static int[] arraySet(final int[] array, final int n) {
        if (isNullOrEmpty(array)) {
            return array;
        }
        for (int i = 0; i < array.length; ++i) {
            array[i] = n;
        }
        return array;
    }
    
    public static float[] arraySet(final float[] array, final float n) {
        if (isNullOrEmpty(array)) {
            return array;
        }
        for (int i = 0; i < array.length; ++i) {
            array[i] = n;
        }
        return array;
    }
    
    public static <E> E[] arraySet(final E[] array, final E e) {
        if (isNullOrEmpty(array)) {
            return array;
        }
        for (int i = 0; i < array.length; ++i) {
            array[i] = e;
        }
        return array;
    }
    
    public static <E> E[] arrayPopulate(final E[] array, final Supplier<E> supplier) {
        if (isNullOrEmpty(array)) {
            return array;
        }
        for (int i = 0; i < array.length; ++i) {
            array[i] = supplier.get();
        }
        return array;
    }
    
    public static void insertAt(final int[] array, final int n, final int n2) {
        for (int i = array.length - 1; i > n; --i) {
            array[i] = array[i - 1];
        }
        array[n] = n2;
    }
    
    public static void insertAt(final float[] array, final int n, final float n2) {
        for (int i = array.length - 1; i > n; --i) {
            array[i] = array[i - 1];
        }
        array[n] = n2;
    }
    
    public static <E> E[] toArray(final List<E> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        final Object[] instance = newInstance(list.get(0).getClass(), list.size());
        arrayCopy(instance, list);
        return (E[])instance;
    }
    
    public static <E> int indexOf(final E[] array, final int n, final E e) {
        for (int i = 0; i < n; ++i) {
            if (array[i] == e) {
                return i;
            }
        }
        return -1;
    }
    
    public static int indexOf(final int[] array, final int n, final int n2) {
        for (int i = 0; i < n; ++i) {
            if (array[i] == n2) {
                return i;
            }
        }
        return -1;
    }
    
    public static boolean contains(final int[] array, final int n, final int n2) {
        return indexOf(array, n, n2) != -1;
    }
    
    public static <E> void forEach(final List<E> list, final Consumer<? super E> consumer) {
        try {
            if (list == null) {
                return;
            }
            for (int i = 0; i < list.size(); ++i) {
                consumer.accept(list.get(i));
            }
        }
        finally {
            Pool.tryRelease(consumer);
        }
    }
    
    public static <E> void forEach(final Iterable<E> iterable, final Consumer<? super E> consumer) {
        if (iterable == null) {
            Pool.tryRelease(consumer);
            return;
        }
        if (iterable instanceof List) {
            forEach((List<Object>)iterable, (Consumer<? super Object>)consumer);
            return;
        }
        try {
            final Iterator<Object> iterator = iterable.iterator();
            while (iterator.hasNext()) {
                consumer.accept(iterator.next());
            }
        }
        finally {
            Pool.tryRelease(consumer);
        }
    }
    
    public static <E> void forEach(final E[] array, final Consumer<? super E> consumer) {
        if (isNullOrEmpty(array)) {
            return;
        }
        for (int i = 0; i < array.length; ++i) {
            consumer.accept(array[i]);
        }
    }
    
    public static <K, V> V getOrCreate(final HashMap<K, V> hashMap, final K k, final Supplier<V> supplier) {
        V value = hashMap.get(k);
        if (value == null) {
            value = supplier.get();
            hashMap.put(k, value);
        }
        return value;
    }
    
    public static <E> void sort(final Stack<E> stack, final Comparator<E> c) {
        try {
            stack.sort(c);
        }
        finally {
            Pool.tryRelease(c);
        }
    }
    
    public static <E> boolean sequenceEqual(final E[] array, final List<? extends E> list) {
        return sequenceEqual(array, list, Comparators::objectsEqual);
    }
    
    public static <E> boolean sequenceEqual(final E[] array, final List<? extends E> list, final Comparator<E> comparator) {
        return array.length == list.size() && sequenceEqual((List<? extends E>)asList((E[])array), list, comparator);
    }
    
    public static <E> boolean sequenceEqual(final List<? extends E> list, final List<? extends E> list2) {
        return sequenceEqual(list, list2, Comparators::objectsEqual);
    }
    
    public static <E> boolean sequenceEqual(final List<? extends E> list, final List<? extends E> list2, final Comparator<E> comparator) {
        if (list.size() != list2.size()) {
            return false;
        }
        boolean b = true;
        for (int i = 0; i < list.size(); ++i) {
            if (comparator.compare((E)list.get(i), (E)list2.get(i)) != 0) {
                b = false;
                break;
            }
        }
        return b;
    }
    
    public static int[] arrayAdd(final int[] array, final int[] array2) {
        for (int i = 0; i < array.length; ++i) {
            final int n = i;
            array[n] += array2[i];
        }
        return array;
    }
    
    static {
        emptyIntArray = new int[0];
        emptyFloatArray = new float[0];
    }
    
    public static class Comparators
    {
        public static <E> int referencesEqual(final E e, final E e2) {
            return (e != e2) ? 1 : 0;
        }
        
        public static <E> int objectsEqual(final E e, final E obj) {
            return (e == null || !e.equals(obj)) ? 1 : 0;
        }
        
        public static int equalsIgnoreCase(final String s, final String s2) {
            return StringUtils.equals(s, s2) ? 0 : 1;
        }
    }
    
    public interface IListConverter1Param<S, E, T1>
    {
        E convert(final S p0, final T1 p1);
    }
}

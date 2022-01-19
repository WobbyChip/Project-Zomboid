// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util.util;

import zombie.util.map.NoSuchMappingException;
import java.util.NoSuchElementException;

public class Exceptions
{
    public static void indexOutOfBounds(final int n, final int n2, final int n3) throws IndexOutOfBoundsException {
        throw new IndexOutOfBoundsException(invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, n, n2, n3));
    }
    
    public static void nullArgument(final String s) throws NullPointerException {
        throw new NullPointerException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
    }
    
    public static void negativeArgument(final String s, final Object obj) throws IllegalArgumentException {
        throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, String.valueOf(obj)));
    }
    
    public static void negativeOrZeroArgument(final String s, final Object obj) throws IllegalArgumentException {
        throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, String.valueOf(obj)));
    }
    
    public static void endOfIterator() throws NoSuchElementException {
        throw new NoSuchElementException("Attempt to iterate past iterator's last element.");
    }
    
    public static void startOfIterator() throws NoSuchElementException {
        throw new NoSuchElementException("Attempt to iterate past iterator's first element.");
    }
    
    public static void noElementToRemove() throws IllegalStateException {
        throw new IllegalStateException("Attempt to remove element from iterator that has no current element.");
    }
    
    public static void noElementToGet() throws IllegalStateException {
        throw new IllegalStateException("Attempt to get element from iterator that has no current element. Call next() first.");
    }
    
    public static void noElementToSet() throws IllegalStateException {
        throw new IllegalStateException("Attempt to set element in iterator that has no current element.");
    }
    
    public static void noLastElement() throws IllegalStateException {
        throw new IllegalStateException("No value to return. Call containsKey() first.");
    }
    
    public static void noSuchMapping(final Object obj) throws NoSuchMappingException {
        throw new NoSuchMappingException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, String.valueOf(obj)));
    }
    
    public static void dequeNoFirst() throws IndexOutOfBoundsException {
        throw new IndexOutOfBoundsException("Attempt to get first element of empty deque");
    }
    
    public static void dequeNoLast() throws IndexOutOfBoundsException {
        throw new IndexOutOfBoundsException("Attempt to get last element of empty deque");
    }
    
    public static void dequeNoFirstToRemove() throws IndexOutOfBoundsException {
        throw new IndexOutOfBoundsException("Attempt to remove last element of empty deque");
    }
    
    public static void dequeNoLastToRemove() throws IndexOutOfBoundsException {
        throw new IndexOutOfBoundsException("Attempt to remove last element of empty deque");
    }
    
    public static void nullElementNotAllowed() throws IllegalArgumentException {
        throw new IllegalArgumentException("Attempt to add a null value to an adapted primitive set.");
    }
    
    public static void cannotAdapt(final String s) throws IllegalStateException {
        throw new IllegalStateException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, s));
    }
    
    public static void unsupported(final String s) throws UnsupportedOperationException {
        throw new UnsupportedOperationException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
    }
    
    public static void unmodifiable(final String s) throws UnsupportedOperationException {
        throw new UnsupportedOperationException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
    }
    
    public static void cloning() throws RuntimeException {
        throw new RuntimeException("Clone is not supported");
    }
    
    public static void invalidRangeBounds(final Object o, final Object o2) throws IllegalArgumentException {
        throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/String;, o, o2));
    }
    
    public static void cannotMergeRanges(final Object o, final Object o2) throws IllegalArgumentException {
        throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, o.toString(), o2.toString()));
    }
    
    public static void setNoFirst() throws NoSuchElementException {
        throw new NoSuchElementException("Attempt to get first element of empty set");
    }
    
    public static void setNoLast() throws NoSuchElementException {
        throw new NoSuchElementException("Attempt to get last element of empty set");
    }
    
    public static void invalidSetBounds(final Object o, final Object o2) throws IllegalArgumentException {
        throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/String;, o, o2));
    }
    
    public static void valueNotInSubRange(final Object o) throws IllegalArgumentException {
        throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;)Ljava/lang/String;, o));
    }
    
    public static void invalidUpperBound(final Object o) throws IllegalArgumentException {
        throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;)Ljava/lang/String;, o));
    }
    
    public static void invalidLowerBound(final Object o) throws IllegalArgumentException {
        throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;)Ljava/lang/String;, o));
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.utils;

import java.util.NoSuchElementException;

public class BoundedQueue<E>
{
    private int numElements;
    private int front;
    private int rear;
    private E[] elements;
    
    public BoundedQueue(final int n) {
        this.numElements = n;
        this.elements = (E[])new Object[Integer.highestOneBit(Math.max(n, 16) - 1) << 1];
    }
    
    public void add(final E e) {
        if (e == null) {
            throw new NullPointerException();
        }
        if (this.size() == this.numElements) {
            this.removeFirst();
        }
        this.elements[this.rear] = e;
        this.rear = (this.rear + 1 & this.elements.length - 1);
    }
    
    public E removeFirst() {
        final E e = this.elements[this.front];
        if (e == null) {
            throw new NoSuchElementException();
        }
        this.elements[this.front] = null;
        this.front = (this.front + 1 & this.elements.length - 1);
        return e;
    }
    
    public E remove(final int n) {
        final int n2 = this.front + n & this.elements.length - 1;
        final E e = this.elements[n2];
        if (e == null) {
            throw new NoSuchElementException();
        }
        int i;
        int n3;
        for (i = n2; i != this.front; i = n3) {
            n3 = (i - 1 & this.elements.length - 1);
            this.elements[i] = this.elements[n3];
        }
        this.front = (this.front + 1 & this.elements.length - 1);
        this.elements[i] = null;
        return e;
    }
    
    public E get(final int n) {
        final E e = this.elements[this.front + n & this.elements.length - 1];
        if (e == null) {
            throw new NoSuchElementException();
        }
        return e;
    }
    
    public void clear() {
        while (this.front != this.rear) {
            this.elements[this.front] = null;
            this.front = (this.front + 1 & this.elements.length - 1);
        }
        final int n = 0;
        this.rear = n;
        this.front = n;
    }
    
    public int capacity() {
        return this.numElements;
    }
    
    public int size() {
        return (this.front <= this.rear) ? (this.rear - this.front) : (this.rear + this.elements.length - this.front);
    }
    
    public boolean isEmpty() {
        return this.front == this.rear;
    }
    
    public boolean isFull() {
        return this.size() == this.capacity();
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.utils;

import zombie.iso.IsoGridSquare;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.util.List;

public final class FibonacciHeap<T>
{
    private Entry<T> mMin;
    private int mSize;
    List<Entry<T>> treeTable;
    List<Entry<T>> toVisit;
    
    public FibonacciHeap() {
        this.mMin = null;
        this.mSize = 0;
        this.treeTable = new ArrayList<Entry<T>>(300);
        this.toVisit = new ArrayList<Entry<T>>(300);
    }
    
    public void empty() {
        this.mMin = null;
        this.mSize = 0;
    }
    
    public Entry<T> enqueue(final T t, final double n) {
        this.checkPriority(n);
        final Entry<T> entry = new Entry<T>(t, n);
        this.mMin = mergeLists(this.mMin, entry);
        ++this.mSize;
        return entry;
    }
    
    public Entry<T> min() {
        if (this.isEmpty()) {
            throw new NoSuchElementException("Heap is empty.");
        }
        return this.mMin;
    }
    
    public boolean isEmpty() {
        return this.mMin == null;
    }
    
    public int size() {
        return this.mSize;
    }
    
    public static <T> FibonacciHeap<T> merge(final FibonacciHeap<T> fibonacciHeap, final FibonacciHeap<T> fibonacciHeap2) {
        final FibonacciHeap<T> fibonacciHeap3 = new FibonacciHeap<T>();
        fibonacciHeap3.mMin = (Entry<T>)mergeLists((Entry<Object>)fibonacciHeap.mMin, (Entry<Object>)fibonacciHeap2.mMin);
        fibonacciHeap3.mSize = fibonacciHeap.mSize + fibonacciHeap2.mSize;
        final int n = 0;
        fibonacciHeap2.mSize = n;
        fibonacciHeap.mSize = n;
        fibonacciHeap.mMin = null;
        fibonacciHeap2.mMin = null;
        return fibonacciHeap3;
    }
    
    public Entry<T> dequeueMin() {
        if (this.isEmpty()) {
            throw new NoSuchElementException("Heap is empty.");
        }
        --this.mSize;
        final Entry<T> mMin = this.mMin;
        if (this.mMin.mNext == this.mMin) {
            this.mMin = null;
        }
        else {
            this.mMin.mPrev.mNext = this.mMin.mNext;
            this.mMin.mNext.mPrev = this.mMin.mPrev;
            this.mMin = this.mMin.mNext;
        }
        if (mMin.mChild != null) {
            Entry<T> entry = mMin.mChild;
            do {
                entry.mParent = null;
                entry = entry.mNext;
            } while (entry != mMin.mChild);
        }
        this.mMin = mergeLists(this.mMin, mMin.mChild);
        if (this.mMin == null) {
            return mMin;
        }
        this.treeTable.clear();
        this.toVisit.clear();
        for (Entry<T> entry2 = this.mMin; this.toVisit.isEmpty() || this.toVisit.get(0) != entry2; entry2 = entry2.mNext) {
            this.toVisit.add(entry2);
        }
        for (Entry<T> mMin2 : this.toVisit) {
            while (true) {
                if (mMin2.mDegree >= this.treeTable.size()) {
                    this.treeTable.add(null);
                }
                else {
                    if (this.treeTable.get(mMin2.mDegree) == null) {
                        break;
                    }
                    final Entry<T> entry3 = this.treeTable.get(mMin2.mDegree);
                    this.treeTable.set(mMin2.mDegree, null);
                    final Entry<T> mParent = (entry3.mPriority < mMin2.mPriority) ? entry3 : mMin2;
                    final Entry<T> entry4 = (entry3.mPriority < mMin2.mPriority) ? mMin2 : entry3;
                    entry4.mNext.mPrev = entry4.mPrev;
                    entry4.mPrev.mNext = entry4.mNext;
                    final Entry<T> entry5 = entry4;
                    final Entry<T> entry6 = entry4;
                    final Entry<T> entry7 = entry4;
                    entry6.mPrev = entry7;
                    entry5.mNext = entry7;
                    mParent.mChild = (Entry<T>)mergeLists((Entry<T>)mParent.mChild, (Entry<T>)entry4);
                    entry4.mParent = mParent;
                    entry4.mIsMarked = false;
                    final Entry<T> entry8 = mParent;
                    ++entry8.mDegree;
                    mMin2 = mParent;
                }
            }
            this.treeTable.set(mMin2.mDegree, mMin2);
            if (mMin2.mPriority <= this.mMin.mPriority) {
                this.mMin = mMin2;
            }
        }
        return mMin;
    }
    
    public void decreaseKey(final Entry<T> entry, final double n) {
        this.checkPriority(n);
        if (n > entry.mPriority) {
            throw new IllegalArgumentException("New priority exceeds old.");
        }
        this.decreaseKeyUnchecked(entry, n);
    }
    
    public void delete(final Entry<T> entry) {
        this.decreaseKeyUnchecked(entry, Double.NEGATIVE_INFINITY);
        this.dequeueMin();
    }
    
    public void delete(final int n, final IsoGridSquare isoGridSquare) {
    }
    
    private void checkPriority(final double v) {
        if (Double.isNaN(v)) {
            throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(D)Ljava/lang/String;, v));
        }
    }
    
    private static <T> Entry<T> mergeLists(final Entry<T> mPrev, final Entry<T> mPrev2) {
        if (mPrev == null && mPrev2 == null) {
            return null;
        }
        if (mPrev != null && mPrev2 == null) {
            return mPrev;
        }
        if (mPrev == null && mPrev2 != null) {
            return mPrev2;
        }
        final Entry<T> mNext = mPrev.mNext;
        mPrev.mNext = mPrev2.mNext;
        mPrev.mNext.mPrev = mPrev;
        mPrev2.mNext = mNext;
        mPrev2.mNext.mPrev = mPrev2;
        return (mPrev.mPriority < mPrev2.mPriority) ? mPrev : mPrev2;
    }
    
    private void decreaseKeyUnchecked(final Entry<T> mMin, final double mPriority) {
        mMin.mPriority = mPriority;
        if (mMin.mParent != null && mMin.mPriority <= mMin.mParent.mPriority) {
            this.cutNode(mMin);
        }
        if (mMin.mPriority <= this.mMin.mPriority) {
            this.mMin = mMin;
        }
    }
    
    private void decreaseKeyUncheckedNode(final Entry<IsoGridSquare> mMin, final double mPriority) {
        mMin.mPriority = mPriority;
        if (mMin.mParent != null && mMin.mPriority <= mMin.mParent.mPriority) {
            this.cutNodeNode(mMin);
        }
        if (mMin.mPriority <= this.mMin.mPriority) {
            this.mMin = (Entry<T>)mMin;
        }
    }
    
    private void cutNode(final Entry<T> entry) {
        entry.mIsMarked = false;
        if (entry.mParent == null) {
            return;
        }
        if (entry.mNext != entry) {
            entry.mNext.mPrev = entry.mPrev;
            entry.mPrev.mNext = entry.mNext;
        }
        if (entry.mParent.mChild == entry) {
            if (entry.mNext != entry) {
                entry.mParent.mChild = entry.mNext;
            }
            else {
                entry.mParent.mChild = null;
            }
        }
        final Entry<T> mParent = entry.mParent;
        --mParent.mDegree;
        entry.mNext = entry;
        entry.mPrev = entry;
        this.mMin = mergeLists(this.mMin, entry);
        if (entry.mParent.mIsMarked) {
            this.cutNode(entry.mParent);
        }
        else {
            entry.mParent.mIsMarked = true;
        }
        entry.mParent = null;
    }
    
    private void cutNodeNode(final Entry<IsoGridSquare> entry) {
        entry.mIsMarked = false;
        if (entry.mParent == null) {
            return;
        }
        if (entry.mNext != entry) {
            entry.mNext.mPrev = entry.mPrev;
            entry.mPrev.mNext = entry.mNext;
        }
        if (entry.mParent.mChild == entry) {
            if (entry.mNext != entry) {
                entry.mParent.mChild = entry.mNext;
            }
            else {
                entry.mParent.mChild = null;
            }
        }
        final Entry<T> mParent = (Entry<T>)entry.mParent;
        --mParent.mDegree;
        entry.mNext = entry;
        entry.mPrev = entry;
        this.mMin = mergeLists(this.mMin, (Entry<T>)entry);
        if (entry.mParent.mIsMarked) {
            this.cutNode((Entry<T>)entry.mParent);
        }
        else {
            entry.mParent.mIsMarked = true;
        }
        entry.mParent = null;
    }
    
    public static final class Entry<T>
    {
        private int mDegree;
        private boolean mIsMarked;
        private Entry<T> mNext;
        private Entry<T> mPrev;
        private Entry<T> mParent;
        private Entry<T> mChild;
        private T mElem;
        private double mPriority;
        
        public T getValue() {
            return this.mElem;
        }
        
        public void setValue(final T mElem) {
            this.mElem = mElem;
        }
        
        public double getPriority() {
            return this.mPriority;
        }
        
        private Entry(final T mElem, final double mPriority) {
            this.mDegree = 0;
            this.mIsMarked = false;
            this.mPrev = this;
            this.mNext = this;
            this.mElem = mElem;
            this.mPriority = mPriority;
        }
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.utils;

import java.util.ArrayList;

public final class DirectBufferAllocator
{
    private static final Object LOCK;
    private static final ArrayList<WrappedBuffer> ALL;
    
    public static WrappedBuffer allocate(final int n) {
        synchronized (DirectBufferAllocator.LOCK) {
            destroyDisposed();
            final WrappedBuffer e = new WrappedBuffer(n);
            DirectBufferAllocator.ALL.add(e);
            return e;
        }
    }
    
    private static void destroyDisposed() {
        synchronized (DirectBufferAllocator.LOCK) {
            for (int i = DirectBufferAllocator.ALL.size() - 1; i >= 0; --i) {
                if (DirectBufferAllocator.ALL.get(i).isDisposed()) {
                    DirectBufferAllocator.ALL.remove(i);
                }
            }
        }
    }
    
    public static long getBytesAllocated() {
        synchronized (DirectBufferAllocator.LOCK) {
            destroyDisposed();
            long n = 0L;
            for (int i = 0; i < DirectBufferAllocator.ALL.size(); ++i) {
                final WrappedBuffer wrappedBuffer = DirectBufferAllocator.ALL.get(i);
                if (!wrappedBuffer.isDisposed()) {
                    n += wrappedBuffer.capacity();
                }
            }
            return n;
        }
    }
    
    static {
        LOCK = "DirectBufferAllocator.LOCK";
        ALL = new ArrayList<WrappedBuffer>();
    }
}

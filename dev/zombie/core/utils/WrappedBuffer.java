// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.utils;

import java.nio.Buffer;
import org.lwjgl.system.MemoryUtil;
import java.nio.ByteBuffer;

public final class WrappedBuffer
{
    private ByteBuffer buf;
    private final int capacity;
    private boolean disposed;
    
    public WrappedBuffer(final int n) {
        MemoryUtil.memSet(this.buf = MemoryUtil.memAlloc(n), 0);
        this.capacity = this.buf.capacity();
    }
    
    public ByteBuffer getBuffer() {
        if (this.disposed) {
            throw new IllegalStateException("Can't get buffer after disposal");
        }
        return this.buf;
    }
    
    public int capacity() {
        return this.capacity;
    }
    
    public void dispose() {
        if (this.disposed) {
            throw new IllegalStateException("WrappedBuffer was already disposed");
        }
        this.disposed = true;
        MemoryUtil.memFree((Buffer)this.buf);
        this.buf = null;
    }
    
    public boolean isDisposed() {
        return this.disposed;
    }
}

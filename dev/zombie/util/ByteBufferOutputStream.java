// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.io.OutputStream;

public class ByteBufferOutputStream extends OutputStream
{
    private ByteBuffer wrappedBuffer;
    private final boolean autoEnlarge;
    
    public ByteBufferOutputStream(final ByteBuffer wrappedBuffer, final boolean autoEnlarge) {
        this.wrappedBuffer = wrappedBuffer;
        this.autoEnlarge = autoEnlarge;
    }
    
    public ByteBuffer toByteBuffer() {
        final ByteBuffer duplicate = this.wrappedBuffer.duplicate();
        duplicate.flip();
        return duplicate.asReadOnlyBuffer();
    }
    
    public ByteBuffer getWrappedBuffer() {
        return this.wrappedBuffer;
    }
    
    public void clear() {
        this.wrappedBuffer.clear();
    }
    
    public void flip() {
        this.wrappedBuffer.flip();
    }
    
    private void growTo(final int n) {
        int n2 = this.wrappedBuffer.capacity() << 1;
        if (n2 - n < 0) {
            n2 = n;
        }
        if (n2 < 0) {
            if (n < 0) {
                throw new OutOfMemoryError();
            }
            n2 = Integer.MAX_VALUE;
        }
        final ByteBuffer wrappedBuffer = this.wrappedBuffer;
        if (this.wrappedBuffer.isDirect()) {
            this.wrappedBuffer = ByteBuffer.allocateDirect(n2);
        }
        else {
            this.wrappedBuffer = ByteBuffer.allocate(n2);
        }
        wrappedBuffer.flip();
        this.wrappedBuffer.put(wrappedBuffer);
    }
    
    @Override
    public void write(final int n) {
        try {
            this.wrappedBuffer.put((byte)n);
        }
        catch (BufferOverflowException ex) {
            if (!this.autoEnlarge) {
                throw ex;
            }
            this.growTo(this.wrappedBuffer.capacity() * 2);
            this.write(n);
        }
    }
    
    @Override
    public void write(final byte[] src) {
        int position = 0;
        try {
            position = this.wrappedBuffer.position();
            this.wrappedBuffer.put(src);
        }
        catch (BufferOverflowException ex) {
            if (!this.autoEnlarge) {
                throw ex;
            }
            this.growTo(Math.max(this.wrappedBuffer.capacity() * 2, position + src.length));
            this.write(src);
        }
    }
    
    @Override
    public void write(final byte[] src, final int offset, final int length) {
        int position = 0;
        try {
            position = this.wrappedBuffer.position();
            this.wrappedBuffer.put(src, offset, length);
        }
        catch (BufferOverflowException ex) {
            if (!this.autoEnlarge) {
                throw ex;
            }
            this.growTo(Math.max(this.wrappedBuffer.capacity() * 2, position + length));
            this.write(src, offset, length);
        }
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util;

import java.io.IOException;
import java.util.Objects;
import java.nio.ByteBuffer;
import java.io.InputStream;

public class ByteBufferBackedInputStream extends InputStream
{
    final ByteBuffer buf;
    
    public ByteBufferBackedInputStream(final ByteBuffer byteBuffer) {
        Objects.requireNonNull(byteBuffer);
        this.buf = byteBuffer;
    }
    
    @Override
    public int read() throws IOException {
        if (!this.buf.hasRemaining()) {
            return -1;
        }
        return this.buf.get() & 0xFF;
    }
    
    @Override
    public int read(final byte[] dst, final int offset, int min) throws IOException {
        if (!this.buf.hasRemaining()) {
            return -1;
        }
        min = Math.min(min, this.buf.remaining());
        this.buf.get(dst, offset, min);
        return min;
    }
}

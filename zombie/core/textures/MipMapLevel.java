// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.textures;

import java.nio.ByteBuffer;
import zombie.core.utils.DirectBufferAllocator;
import zombie.core.utils.WrappedBuffer;

public final class MipMapLevel
{
    public final int width;
    public final int height;
    public final WrappedBuffer data;
    
    public MipMapLevel(final int width, final int height) {
        this.width = width;
        this.height = height;
        this.data = DirectBufferAllocator.allocate(width * height * 4);
    }
    
    public MipMapLevel(final int width, final int height, final WrappedBuffer data) {
        this.width = width;
        this.height = height;
        this.data = data;
    }
    
    public void dispose() {
        if (this.data != null) {
            this.data.dispose();
        }
    }
    
    public boolean isDisposed() {
        return this.data != null && this.data.isDisposed();
    }
    
    public void rewind() {
        if (this.data != null) {
            this.data.getBuffer().rewind();
        }
    }
    
    public ByteBuffer getBuffer() {
        if (this.data == null) {
            return null;
        }
        return this.data.getBuffer();
    }
    
    public int getDataSize() {
        return this.width * this.height * 4;
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.VBO;

import java.nio.IntBuffer;
import java.nio.ByteBuffer;
import org.lwjgl.opengl.ARBVertexBufferObject;

public final class GLBufferObjectARB implements IGLBufferObject
{
    @Override
    public int GL_ARRAY_BUFFER() {
        return 34962;
    }
    
    @Override
    public int GL_ELEMENT_ARRAY_BUFFER() {
        return 34963;
    }
    
    @Override
    public int GL_STATIC_DRAW() {
        return 35044;
    }
    
    @Override
    public int GL_STREAM_DRAW() {
        return 35040;
    }
    
    @Override
    public int GL_BUFFER_SIZE() {
        return 34660;
    }
    
    @Override
    public int GL_WRITE_ONLY() {
        return 35001;
    }
    
    @Override
    public int glGenBuffers() {
        return ARBVertexBufferObject.glGenBuffersARB();
    }
    
    @Override
    public void glBindBuffer(final int n, final int n2) {
        ARBVertexBufferObject.glBindBufferARB(n, n2);
    }
    
    @Override
    public void glDeleteBuffers(final int n) {
        ARBVertexBufferObject.glDeleteBuffersARB(n);
    }
    
    @Override
    public void glBufferData(final int n, final ByteBuffer byteBuffer, final int n2) {
        ARBVertexBufferObject.glBufferDataARB(n, byteBuffer, n2);
    }
    
    @Override
    public void glBufferData(final int n, final long n2, final int n3) {
        ARBVertexBufferObject.glBufferDataARB(n, n2, n3);
    }
    
    @Override
    public ByteBuffer glMapBuffer(final int n, final int n2, final long n3, final ByteBuffer byteBuffer) {
        return ARBVertexBufferObject.glMapBufferARB(n, n2, n3, byteBuffer);
    }
    
    @Override
    public boolean glUnmapBuffer(final int n) {
        return ARBVertexBufferObject.glUnmapBufferARB(n);
    }
    
    @Override
    public void glGetBufferParameter(final int n, final int n2, final IntBuffer intBuffer) {
        ARBVertexBufferObject.glGetBufferParameterivARB(n, n2, intBuffer);
    }
}

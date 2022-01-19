// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.VBO;

import java.nio.IntBuffer;
import java.nio.ByteBuffer;

public interface IGLBufferObject
{
    int GL_ARRAY_BUFFER();
    
    int GL_ELEMENT_ARRAY_BUFFER();
    
    int GL_STATIC_DRAW();
    
    int GL_STREAM_DRAW();
    
    int GL_BUFFER_SIZE();
    
    int GL_WRITE_ONLY();
    
    int glGenBuffers();
    
    void glBindBuffer(final int p0, final int p1);
    
    void glDeleteBuffers(final int p0);
    
    void glBufferData(final int p0, final ByteBuffer p1, final int p2);
    
    void glBufferData(final int p0, final long p1, final int p2);
    
    ByteBuffer glMapBuffer(final int p0, final int p1, final long p2, final ByteBuffer p3);
    
    boolean glUnmapBuffer(final int p0);
    
    void glGetBufferParameter(final int p0, final int p1, final IntBuffer p2);
}

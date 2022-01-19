// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.VBO;

import org.lwjgl.opengl.GL20;
import java.nio.ByteOrder;
import org.lwjglx.opengl.OpenGLException;
import org.lwjgl.opengl.ARBMapBufferRange;
import org.lwjgl.opengl.GL30;
import zombie.core.skinnedmodel.model.VertexBufferObject;
import org.lwjgl.opengl.GL;
import java.nio.ByteBuffer;

public class GLVertexBufferObject
{
    public static IGLBufferObject funcs;
    private long size;
    private final int type;
    private final int usage;
    private transient int id;
    private transient boolean mapped;
    private transient boolean cleared;
    private transient ByteBuffer buffer;
    private int m_vertexAttribArray;
    
    public static void init() {
        if (GL.getCapabilities().OpenGL15) {
            System.out.println("OpenGL 1.5 buffer objects supported");
            GLVertexBufferObject.funcs = new GLBufferObject15();
        }
        else {
            if (!GL.getCapabilities().GL_ARB_vertex_buffer_object) {
                throw new RuntimeException("Neither OpenGL 1.5 nor GL_ARB_vertex_buffer_object supported");
            }
            System.out.println("GL_ARB_vertex_buffer_object supported");
            GLVertexBufferObject.funcs = new GLBufferObjectARB();
        }
        VertexBufferObject.funcs = GLVertexBufferObject.funcs;
    }
    
    public GLVertexBufferObject(final long size, final int type, final int usage) {
        this.m_vertexAttribArray = -1;
        this.size = size;
        this.type = type;
        this.usage = usage;
    }
    
    public GLVertexBufferObject(final int type, final int usage) {
        this.m_vertexAttribArray = -1;
        this.size = 0L;
        this.type = type;
        this.usage = usage;
    }
    
    public void create() {
        this.id = GLVertexBufferObject.funcs.glGenBuffers();
    }
    
    public void clear() {
        if (!this.cleared) {
            GLVertexBufferObject.funcs.glBufferData(this.type, this.size, this.usage);
            this.cleared = true;
        }
    }
    
    protected void doDestroy() {
        if (this.id != 0) {
            this.unmap();
            GLVertexBufferObject.funcs.glDeleteBuffers(this.id);
            this.id = 0;
        }
    }
    
    public ByteBuffer map(final int newLimit) {
        if (!this.mapped) {
            if (this.size != newLimit) {
                this.size = newLimit;
                this.clear();
            }
            if (this.buffer != null && this.buffer.capacity() < newLimit) {
                this.buffer = null;
            }
            final ByteBuffer buffer = this.buffer;
            if (GL.getCapabilities().OpenGL30) {
                this.buffer = GL30.glMapBufferRange(this.type, 0L, (long)newLimit, 34, this.buffer);
            }
            else if (GL.getCapabilities().GL_ARB_map_buffer_range) {
                this.buffer = ARBMapBufferRange.glMapBufferRange(this.type, 0L, (long)newLimit, 34, this.buffer);
            }
            else {
                this.buffer = GLVertexBufferObject.funcs.glMapBuffer(this.type, GLVertexBufferObject.funcs.GL_WRITE_ONLY(), newLimit, this.buffer);
            }
            if (this.buffer == null) {
                throw new OpenGLException(invokedynamic(makeConcatWithConstants:(Lzombie/core/VBO/GLVertexBufferObject;)Ljava/lang/String;, this));
            }
            if (this.buffer == buffer || buffer != null) {}
            this.buffer.order(ByteOrder.nativeOrder()).clear().limit(newLimit);
            this.mapped = true;
            this.cleared = false;
        }
        return this.buffer;
    }
    
    public ByteBuffer map() {
        if (!this.mapped) {
            assert this.size > 0L;
            this.clear();
            final ByteBuffer buffer = this.buffer;
            if (GL.getCapabilities().OpenGL30) {
                this.buffer = GL30.glMapBufferRange(this.type, 0L, this.size, 34, this.buffer);
            }
            else if (GL.getCapabilities().GL_ARB_map_buffer_range) {
                this.buffer = ARBMapBufferRange.glMapBufferRange(this.type, 0L, this.size, 34, this.buffer);
            }
            else {
                this.buffer = GLVertexBufferObject.funcs.glMapBuffer(this.type, GLVertexBufferObject.funcs.GL_WRITE_ONLY(), this.size, this.buffer);
            }
            if (this.buffer == null) {
                throw new OpenGLException(invokedynamic(makeConcatWithConstants:(J)Ljava/lang/String;, this.size));
            }
            if (this.buffer == buffer || buffer != null) {}
            this.buffer.order(ByteOrder.nativeOrder()).clear().limit((int)this.size);
            this.mapped = true;
            this.cleared = false;
        }
        return this.buffer;
    }
    
    public void orphan() {
        GLVertexBufferObject.funcs.glMapBuffer(this.type, this.usage, this.size, null);
    }
    
    public boolean unmap() {
        if (this.mapped) {
            this.mapped = false;
            return GLVertexBufferObject.funcs.glUnmapBuffer(this.type);
        }
        return true;
    }
    
    public boolean isMapped() {
        return this.mapped;
    }
    
    public void bufferData(final ByteBuffer byteBuffer) {
        GLVertexBufferObject.funcs.glBufferData(this.type, byteBuffer, this.usage);
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(IJ)Ljava/lang/String;, this.id, this.size);
    }
    
    public void bind() {
        GLVertexBufferObject.funcs.glBindBuffer(this.type, this.id);
    }
    
    public void bindNone() {
        GLVertexBufferObject.funcs.glBindBuffer(this.type, 0);
    }
    
    public int getID() {
        return this.id;
    }
    
    public void enableVertexAttribArray(final int n) {
        if (this.m_vertexAttribArray != n) {
            this.disableVertexAttribArray();
            if (n >= 0) {
                GL20.glEnableVertexAttribArray(n);
            }
            this.m_vertexAttribArray = ((n >= 0) ? n : -1);
        }
    }
    
    public void disableVertexAttribArray() {
        if (this.m_vertexAttribArray != -1) {
            GL20.glDisableVertexAttribArray(this.m_vertexAttribArray);
            this.m_vertexAttribArray = -1;
        }
    }
}

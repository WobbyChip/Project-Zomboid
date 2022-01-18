// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.opengl;

import zombie.debug.DebugLog;
import java.nio.ShortBuffer;
import java.nio.FloatBuffer;
import zombie.core.VBO.GLVertexBufferObject;

public final class SharedVertexBufferObjects
{
    private final int bufferSizeBytes = 65536;
    private final int indexBufferSizeBytes;
    public final int bufferSizeVertices;
    private final GLVertexBufferObject[] vbo;
    private final GLVertexBufferObject[] ibo;
    public FloatBuffer vertices;
    public ShortBuffer indices;
    private int sequence;
    private int mark;
    
    public SharedVertexBufferObjects(final int n) {
        this.vbo = new GLVertexBufferObject[48];
        this.ibo = new GLVertexBufferObject[48];
        this.sequence = -1;
        this.bufferSizeVertices = 65536 / n;
        this.indexBufferSizeBytes = this.bufferSizeVertices * 3;
    }
    
    public void startFrame() {
        if (true) {
            this.sequence = -1;
        }
        this.mark = this.sequence;
    }
    
    public void next() {
        ++this.sequence;
        if (this.sequence == this.vbo.length) {
            this.sequence = 0;
        }
        if (this.sequence == this.mark) {
            DebugLog.General.error((Object)"SharedVertexBufferObject overrun.");
        }
        if (this.vbo[this.sequence] == null) {
            (this.vbo[this.sequence] = new GLVertexBufferObject(65536L, GLVertexBufferObject.funcs.GL_ARRAY_BUFFER(), GLVertexBufferObject.funcs.GL_STREAM_DRAW())).create();
            (this.ibo[this.sequence] = new GLVertexBufferObject(this.indexBufferSizeBytes, GLVertexBufferObject.funcs.GL_ELEMENT_ARRAY_BUFFER(), GLVertexBufferObject.funcs.GL_STREAM_DRAW())).create();
        }
        this.vbo[this.sequence].bind();
        (this.vertices = this.vbo[this.sequence].map().asFloatBuffer()).clear();
        this.ibo[this.sequence].bind();
        (this.indices = this.ibo[this.sequence].map().asShortBuffer()).clear();
    }
    
    public void unmap() {
        this.vbo[this.sequence].unmap();
        this.ibo[this.sequence].unmap();
    }
}

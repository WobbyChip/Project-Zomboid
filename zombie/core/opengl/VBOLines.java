// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.opengl;

import zombie.core.math.PZMath;
import zombie.core.SpriteRenderer;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import zombie.core.VBO.IGLBufferObject;
import org.lwjgl.BufferUtils;
import java.nio.ByteBuffer;
import zombie.core.VBO.GLVertexBufferObject;

public final class VBOLines
{
    private final int VERTEX_SIZE = 12;
    private final int COLOR_SIZE = 16;
    private final int ELEMENT_SIZE = 28;
    private final int COLOR_OFFSET = 12;
    private final int NUM_LINES = 128;
    private final int NUM_ELEMENTS = 256;
    private final int INDEX_SIZE = 2;
    private GLVertexBufferObject m_vbo;
    private GLVertexBufferObject m_ibo;
    private ByteBuffer m_elements;
    private ByteBuffer m_indices;
    private float m_lineWidth;
    private float m_dx;
    private float m_dy;
    private float m_dz;
    private int m_mode;
    
    public VBOLines() {
        this.m_lineWidth = 1.0f;
        this.m_dx = 0.0f;
        this.m_dy = 0.0f;
        this.m_dz = 0.0f;
        this.m_mode = 1;
    }
    
    private void create() {
        this.m_elements = BufferUtils.createByteBuffer(7168);
        this.m_indices = BufferUtils.createByteBuffer(512);
        final IGLBufferObject funcs = GLVertexBufferObject.funcs;
        (this.m_vbo = new GLVertexBufferObject(7168L, funcs.GL_ARRAY_BUFFER(), funcs.GL_STREAM_DRAW())).create();
        (this.m_ibo = new GLVertexBufferObject(512L, funcs.GL_ELEMENT_ARRAY_BUFFER(), funcs.GL_STREAM_DRAW())).create();
    }
    
    public void setOffset(final float dx, final float dy, final float dz) {
        this.m_dx = dx;
        this.m_dy = dy;
        this.m_dz = dz;
    }
    
    public void addElement(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7) {
        if (this.isFull()) {
            this.flush();
        }
        if (this.m_elements == null) {
            this.create();
        }
        this.m_elements.putFloat(this.m_dx + n);
        this.m_elements.putFloat(this.m_dy + n2);
        this.m_elements.putFloat(this.m_dz + n3);
        this.m_elements.putFloat(n4);
        this.m_elements.putFloat(n5);
        this.m_elements.putFloat(n6);
        this.m_elements.putFloat(n7);
        this.m_indices.putShort((short)(this.m_indices.position() / 2));
    }
    
    public void addLine(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10) {
        this.reserve(2);
        this.addElement(n, n2, n3, n7, n8, n9, n10);
        this.addElement(n4, n5, n6, n7, n8, n9, n10);
    }
    
    public void addLine(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11, final float n12, final float n13, final float n14) {
        this.reserve(2);
        this.addElement(n, n2, n3, n7, n8, n9, n10);
        this.addElement(n4, n5, n6, n11, n12, n13, n14);
    }
    
    public void addTriangle(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11, final float n12, final float n13) {
        this.reserve(3);
        this.addElement(n, n2, n3, n10, n11, n12, n13);
        this.addElement(n4, n5, n6, n10, n11, n12, n13);
        this.addElement(n7, n8, n9, n10, n11, n12, n13);
    }
    
    public void addQuad(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9) {
        this.reserve(6);
        this.addTriangle(n, n2, n5, n3, n2, n5, n, n4, n5, n6, n7, n8, n9);
        this.addTriangle(n3, n2, n5, n3, n4, n5, n, n4, n5, n6, n7, n8, n9);
    }
    
    boolean isFull() {
        return this.m_elements != null && ((this.m_mode == 4 && this.m_elements.position() % 84 == 0 && this.m_elements.position() + 84 > 7168) || this.m_elements.position() == 7168);
    }
    
    public void reserve(final int n) {
        if (!this.hasRoomFor(n)) {
            this.flush();
        }
    }
    
    boolean hasRoomFor(final int n) {
        return this.m_elements == null || this.m_elements.position() / 28 + n <= 256;
    }
    
    public void flush() {
        if (this.m_elements == null || this.m_elements.position() == 0) {
            return;
        }
        this.m_elements.flip();
        this.m_indices.flip();
        GL13.glClientActiveTexture(33984);
        GL11.glDisableClientState(32888);
        this.m_vbo.bind();
        this.m_vbo.bufferData(this.m_elements);
        this.m_ibo.bind();
        this.m_ibo.bufferData(this.m_indices);
        GL11.glEnableClientState(32884);
        GL11.glEnableClientState(32886);
        GL11.glVertexPointer(3, 5126, 28, 0L);
        GL11.glColorPointer(4, 5126, 28, 12L);
        for (int i = 7; i >= 0; --i) {
            GL13.glActiveTexture(33984 + i);
            GL11.glDisable(3553);
        }
        GL11.glDisable(2929);
        GL11.glEnable(2848);
        GL11.glLineWidth(this.m_lineWidth);
        final int n = 0;
        final int n2 = this.m_elements.limit() / 28;
        final int n3 = 0;
        GL12.glDrawRangeElements(this.m_mode, n, n + n2, this.m_indices.limit() / 2 - n3, 5123, (long)(n3 * 2));
        this.m_vbo.bindNone();
        this.m_ibo.bindNone();
        this.m_elements.clear();
        this.m_indices.clear();
        GL11.glEnable(2929);
        GL11.glEnable(3553);
        GL11.glDisable(2848);
        GL13.glClientActiveTexture(33984);
        GL11.glEnableClientState(32888);
        SpriteRenderer.ringBuffer.restoreVBOs = true;
    }
    
    public void setLineWidth(final float lineWidth) {
        if (!PZMath.equal(this.m_lineWidth, lineWidth, 0.01f)) {
            this.flush();
            this.m_lineWidth = lineWidth;
        }
    }
    
    public void setMode(final int mode) {
        assert mode == 4;
        if (mode != this.m_mode) {
            this.flush();
            this.m_mode = mode;
        }
    }
}

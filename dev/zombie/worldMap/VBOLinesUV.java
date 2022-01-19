// 
// Decompiled by Procyon v0.5.36
// 

package zombie.worldMap;

import zombie.core.math.PZMath;
import zombie.core.SpriteRenderer;
import java.util.List;
import org.lwjgl.opengl.GL12;
import zombie.core.textures.Texture;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import zombie.core.textures.TextureID;
import zombie.core.VBO.IGLBufferObject;
import org.lwjgl.BufferUtils;
import java.util.ArrayList;
import zombie.popman.ObjectPool;
import java.nio.ByteBuffer;
import zombie.core.VBO.GLVertexBufferObject;

public final class VBOLinesUV
{
    private final int VERTEX_SIZE = 12;
    private final int COLOR_SIZE = 16;
    private final int UV_SIZE = 8;
    private final int ELEMENT_SIZE = 36;
    private final int COLOR_OFFSET = 12;
    private final int UV_OFFSET = 28;
    private final int NUM_ELEMENTS = 128;
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
    private final ObjectPool<Run> m_runPool;
    private final ArrayList<Run> m_runs;
    
    public VBOLinesUV() {
        this.m_lineWidth = 1.0f;
        this.m_dx = 0.0f;
        this.m_dy = 0.0f;
        this.m_dz = 0.0f;
        this.m_mode = 1;
        this.m_runPool = new ObjectPool<Run>(Run::new);
        this.m_runs = new ArrayList<Run>();
    }
    
    private Run currentRun() {
        return this.m_runs.isEmpty() ? null : this.m_runs.get(this.m_runs.size() - 1);
    }
    
    private void create() {
        this.m_elements = BufferUtils.createByteBuffer(4608);
        this.m_indices = BufferUtils.createByteBuffer(256);
        final IGLBufferObject funcs = GLVertexBufferObject.funcs;
        (this.m_vbo = new GLVertexBufferObject(4608L, funcs.GL_ARRAY_BUFFER(), funcs.GL_STREAM_DRAW())).create();
        (this.m_ibo = new GLVertexBufferObject(256L, funcs.GL_ELEMENT_ARRAY_BUFFER(), funcs.GL_STREAM_DRAW())).create();
    }
    
    public void setOffset(final float dx, final float dy, final float dz) {
        this.m_dx = dx;
        this.m_dy = dy;
        this.m_dz = dz;
    }
    
    public void addElement(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9) {
        if (this.isFull()) {
            final TextureID textureID = this.currentRun().textureID;
            this.flush();
            this.startRun(textureID);
        }
        if (this.m_elements == null) {
            this.create();
        }
        this.m_elements.putFloat(this.m_dx + n);
        this.m_elements.putFloat(this.m_dy + n2);
        this.m_elements.putFloat(this.m_dz + n3);
        this.m_elements.putFloat(n6);
        this.m_elements.putFloat(n7);
        this.m_elements.putFloat(n8);
        this.m_elements.putFloat(n9);
        this.m_elements.putFloat(n4);
        this.m_elements.putFloat(n5);
        this.m_indices.putShort((short)(this.m_indices.position() / 2));
        final Run currentRun = this.currentRun();
        ++currentRun.count;
    }
    
    public void addElement(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7) {
        this.addElement(n, n2, n3, 0.0f, 0.0f, n4, n5, n6, n7);
    }
    
    public void addLine(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10) {
        this.addElement(n, n2, n3, n7, n8, n9, n10);
        this.addElement(n4, n5, n6, n7, n8, n9, n10);
    }
    
    public void addLine(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11, final float n12, final float n13, final float n14) {
        this.addElement(n, n2, n3, n7, n8, n9, n10);
        this.addElement(n4, n5, n6, n11, n12, n13, n14);
    }
    
    public void addTriangle(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11, final float n12, final float n13, final float n14, final float n15, final float n16, final float n17, final float n18, final float n19) {
        this.reserve(3);
        this.addElement(n, n2, n3, n4, n5, n16, n17, n18, n19);
        this.addElement(n6, n7, n8, n9, n10, n16, n17, n18, n19);
        this.addElement(n11, n12, n13, n14, n15, n16, n17, n18, n19);
    }
    
    public void addQuad(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11, final float n12, final float n13) {
        this.reserve(4);
        this.addElement(n, n2, n9, n3, n4, n10, n11, n12, n13);
        this.addElement(n5, n2, n9, n7, n4, n10, n11, n12, n13);
        this.addElement(n5, n6, n9, n7, n8, n10, n11, n12, n13);
        this.addElement(n, n6, n9, n3, n8, n10, n11, n12, n13);
    }
    
    public void addQuad(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11, final float n12, final float n13, final float n14, final float n15, final float n16, final float n17, final float n18, final float n19, final float n20, final float n21) {
        this.reserve(4);
        this.addElement(n, n2, n17, n3, n4, n18, n19, n20, n21);
        this.addElement(n5, n6, n17, n7, n8, n18, n19, n20, n21);
        this.addElement(n9, n10, n17, n11, n12, n18, n19, n20, n21);
        this.addElement(n13, n14, n17, n15, n16, n18, n19, n20, n21);
    }
    
    boolean isFull() {
        return this.m_elements != null && ((this.m_mode == 4 && this.m_elements.position() % 108 == 0 && this.m_elements.position() + 108 > 4608) || this.m_elements.position() == 4608);
    }
    
    public void reserve(final int n) {
        if (!this.hasRoomFor(n)) {
            final TextureID textureID = (this.currentRun() == null) ? null : this.currentRun().textureID;
            this.flush();
            if (textureID != null) {
                this.startRun(textureID);
            }
        }
    }
    
    boolean hasRoomFor(final int n) {
        return this.m_elements == null || this.m_elements.position() / 36 + n <= 128;
    }
    
    public void flush() {
        if (this.m_elements == null || this.m_elements.position() == 0) {
            return;
        }
        this.m_elements.flip();
        this.m_indices.flip();
        GL13.glClientActiveTexture(33984);
        GL11.glEnableClientState(32888);
        this.m_vbo.bind();
        this.m_vbo.bufferData(this.m_elements);
        this.m_ibo.bind();
        this.m_ibo.bufferData(this.m_indices);
        GL11.glEnableClientState(32884);
        GL11.glEnableClientState(32886);
        GL11.glVertexPointer(3, 5126, 36, 0L);
        GL11.glColorPointer(4, 5126, 36, 12L);
        GL11.glTexCoordPointer(2, 5126, 36, 28L);
        GL11.glEnable(3553);
        GL11.glDisable(2929);
        GL11.glEnable(2848);
        GL11.glLineWidth(this.m_lineWidth);
        for (int i = 0; i < this.m_runs.size(); ++i) {
            final Run run = this.m_runs.get(i);
            final int start = run.start;
            final int count = run.count;
            final int start2 = run.start;
            final int n = start2 + run.count;
            if (run.textureID.getID() == -1) {
                run.textureID.bind();
            }
            else {
                GL11.glBindTexture(3553, Texture.lastTextureID = run.textureID.getID());
                GL11.glTexParameteri(3553, 10241, 9729);
                GL11.glTexParameteri(3553, 10240, 9728);
            }
            GL12.glDrawRangeElements(this.m_mode, start, start + count, n - start2, 5123, start2 * 2L);
        }
        this.m_vbo.bindNone();
        this.m_ibo.bindNone();
        this.m_elements.clear();
        this.m_indices.clear();
        this.m_runPool.releaseAll(this.m_runs);
        this.m_runs.clear();
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL13.glClientActiveTexture(33984);
        SpriteRenderer.ringBuffer.restoreVBOs = true;
        SpriteRenderer.ringBuffer.restoreBoundTextures = true;
    }
    
    public void setLineWidth(final float lineWidth) {
        if (!PZMath.equal(this.m_lineWidth, lineWidth, 0.01f)) {
            final TextureID textureID = (this.currentRun() == null) ? null : this.currentRun().textureID;
            this.flush();
            if (textureID != null) {
                this.startRun(textureID);
            }
            this.m_lineWidth = lineWidth;
        }
    }
    
    public void setMode(final int mode) {
        assert mode == 4;
        if (mode != this.m_mode) {
            final TextureID textureID = (this.currentRun() == null) ? null : this.currentRun().textureID;
            this.flush();
            if (textureID != null) {
                this.startRun(textureID);
            }
            this.m_mode = mode;
        }
    }
    
    public void startRun(final TextureID textureID) {
        final Run e = this.m_runPool.alloc();
        e.start = ((this.m_elements == null) ? 0 : (this.m_elements.position() / 36));
        e.count = 0;
        e.textureID = textureID;
        this.m_runs.add(e);
    }
    
    private static final class Run
    {
        int start;
        int count;
        TextureID textureID;
    }
}

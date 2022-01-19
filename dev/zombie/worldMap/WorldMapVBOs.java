// 
// Decompiled by Procyon v0.5.36
// 

package zombie.worldMap;

import zombie.core.VBO.IGLBufferObject;
import zombie.core.VBO.GLVertexBufferObject;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL11;
import org.lwjgl.BufferUtils;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public final class WorldMapVBOs
{
    private static final int VERTEX_SIZE = 12;
    private static final int COLOR_SIZE = 16;
    private static final int ELEMENT_SIZE = 28;
    private static final int COLOR_OFFSET = 12;
    public static final int NUM_ELEMENTS = 2340;
    private static final int INDEX_SIZE = 2;
    private static final WorldMapVBOs instance;
    private final ArrayList<WorldMapVBO> m_vbos;
    private ByteBuffer m_elements;
    private ByteBuffer m_indices;
    
    public WorldMapVBOs() {
        this.m_vbos = new ArrayList<WorldMapVBO>();
    }
    
    public static WorldMapVBOs getInstance() {
        return WorldMapVBOs.instance;
    }
    
    public void create() {
        this.m_elements = BufferUtils.createByteBuffer(65520);
        this.m_indices = BufferUtils.createByteBuffer(4680);
    }
    
    private void flush() {
        if (this.m_vbos.isEmpty()) {
            final WorldMapVBO e = new WorldMapVBO();
            e.create();
            this.m_vbos.add(e);
        }
        this.m_elements.flip();
        this.m_indices.flip();
        this.m_vbos.get(this.m_vbos.size() - 1).flush(this.m_elements, this.m_indices);
        this.m_elements.position(this.m_elements.limit());
        this.m_elements.limit(this.m_elements.capacity());
        this.m_indices.position(this.m_indices.limit());
        this.m_indices.limit(this.m_indices.capacity());
    }
    
    private void addVBO() {
        final WorldMapVBO e = new WorldMapVBO();
        e.create();
        this.m_vbos.add(e);
        this.m_elements.clear();
        this.m_indices.clear();
    }
    
    public void reserveVertices(final int n, final int[] array) {
        if (this.m_indices == null) {
            this.create();
        }
        if (this.m_indices.position() / 2 + n > 2340) {
            this.flush();
            this.addVBO();
        }
        array[0] = (this.m_vbos.isEmpty() ? 0 : (this.m_vbos.size() - 1));
        array[1] = this.m_indices.position() / 2;
    }
    
    public void addElement(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7) {
        this.m_elements.putFloat(n);
        this.m_elements.putFloat(n2);
        this.m_elements.putFloat(n3);
        this.m_elements.putFloat(n4);
        this.m_elements.putFloat(n5);
        this.m_elements.putFloat(n6);
        this.m_elements.putFloat(n7);
        this.m_indices.putShort((short)(this.m_indices.position() / 2));
    }
    
    public void drawElements(final int n, final int index, final int n2, final int n3) {
        if (index < 0 || index >= this.m_vbos.size()) {
            return;
        }
        final WorldMapVBO worldMapVBO = this.m_vbos.get(index);
        if (n2 < 0 || n2 + n3 > worldMapVBO.m_elementCount) {
            return;
        }
        worldMapVBO.m_vbo.bind();
        worldMapVBO.m_ibo.bind();
        GL11.glEnableClientState(32884);
        GL11.glDisableClientState(32886);
        GL11.glVertexPointer(3, 5126, 28, 0L);
        for (int i = 7; i >= 0; --i) {
            GL13.glActiveTexture(33984 + i);
            GL11.glDisable(3553);
        }
        GL11.glDisable(2929);
        GL12.glDrawRangeElements(n, n2, n2 + n3, n3, 5123, (long)(n2 * 2));
        worldMapVBO.m_vbo.bindNone();
        worldMapVBO.m_ibo.bindNone();
    }
    
    public void reset() {
    }
    
    static {
        instance = new WorldMapVBOs();
    }
    
    private static final class WorldMapVBO
    {
        GLVertexBufferObject m_vbo;
        GLVertexBufferObject m_ibo;
        int m_elementCount;
        
        private WorldMapVBO() {
            this.m_elementCount = 0;
        }
        
        void create() {
            final IGLBufferObject funcs = GLVertexBufferObject.funcs;
            (this.m_vbo = new GLVertexBufferObject(65520L, funcs.GL_ARRAY_BUFFER(), funcs.GL_STREAM_DRAW())).create();
            (this.m_ibo = new GLVertexBufferObject(4680L, funcs.GL_ELEMENT_ARRAY_BUFFER(), funcs.GL_STREAM_DRAW())).create();
        }
        
        void flush(final ByteBuffer byteBuffer, final ByteBuffer byteBuffer2) {
            this.m_vbo.bind();
            this.m_vbo.bufferData(byteBuffer);
            this.m_ibo.bind();
            this.m_ibo.bufferData(byteBuffer2);
            this.m_elementCount = byteBuffer2.limit() / 2;
        }
    }
}

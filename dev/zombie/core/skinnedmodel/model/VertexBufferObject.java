// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.model;

import zombie.util.list.PZArrayUtil;
import java.nio.IntBuffer;
import org.lwjgl.opengl.GL20;
import zombie.debug.DebugOptions;
import zombie.core.skinnedmodel.shader.Shader;
import java.nio.Buffer;
import org.lwjgl.system.MemoryUtil;
import java.nio.ByteBuffer;
import org.lwjglx.BufferUtils;
import zombie.core.opengl.RenderThread;
import zombie.core.VBO.IGLBufferObject;

public final class VertexBufferObject
{
    public static IGLBufferObject funcs;
    int[] elements;
    Vbo _handle;
    private final VertexFormat m_vertexFormat;
    private BeginMode _beginMode;
    public boolean bStatic;
    
    public VertexBufferObject() {
        this.bStatic = false;
        this.bStatic = false;
        (this.m_vertexFormat = new VertexFormat(4)).setElement(0, VertexType.VertexArray, 12);
        this.m_vertexFormat.setElement(1, VertexType.NormalArray, 12);
        this.m_vertexFormat.setElement(2, VertexType.ColorArray, 4);
        this.m_vertexFormat.setElement(3, VertexType.TextureCoordArray, 8);
        this.m_vertexFormat.calculate();
        this._beginMode = BeginMode.Triangles;
    }
    
    @Deprecated
    public VertexBufferObject(final VertexPositionNormalTangentTexture[] array, final int[] elements) {
        this.bStatic = false;
        this.elements = elements;
        this.bStatic = true;
        RenderThread.invokeOnRenderContext(this, array, elements, (vertexBufferObject, array2, array3) -> vertexBufferObject._handle = this.LoadVBO(array2, array3));
        (this.m_vertexFormat = new VertexFormat(4)).setElement(0, VertexType.VertexArray, 12);
        this.m_vertexFormat.setElement(1, VertexType.NormalArray, 12);
        this.m_vertexFormat.setElement(2, VertexType.TangentArray, 12);
        this.m_vertexFormat.setElement(3, VertexType.TextureCoordArray, 8);
        this.m_vertexFormat.calculate();
        this._beginMode = BeginMode.Triangles;
    }
    
    @Deprecated
    public VertexBufferObject(final VertexPositionNormalTangentTextureSkin[] array, int[] elements, final boolean b) {
        this.bStatic = false;
        this.elements = elements;
        if (b) {
            final int[] array2 = new int[elements.length];
            int n = 0;
            for (int i = elements.length - 1 - 2; i >= 0; i -= 3) {
                array2[n] = elements[i];
                array2[n + 1] = elements[i + 1];
                array2[n + 2] = elements[i + 2];
                n += 3;
            }
            elements = array2;
        }
        this.bStatic = false;
        this._handle = this.LoadVBO(array, elements);
        (this.m_vertexFormat = new VertexFormat(6)).setElement(0, VertexType.VertexArray, 12);
        this.m_vertexFormat.setElement(1, VertexType.NormalArray, 12);
        this.m_vertexFormat.setElement(2, VertexType.TangentArray, 12);
        this.m_vertexFormat.setElement(3, VertexType.TextureCoordArray, 8);
        this.m_vertexFormat.setElement(4, VertexType.BlendWeightArray, 16);
        this.m_vertexFormat.setElement(5, VertexType.BlendIndexArray, 16);
        this.m_vertexFormat.calculate();
        this._beginMode = BeginMode.Triangles;
    }
    
    public VertexBufferObject(final VertexArray vertexArray, final int[] elements) {
        this.bStatic = false;
        this.m_vertexFormat = vertexArray.m_format;
        this.elements = elements;
        this.bStatic = true;
        RenderThread.invokeOnRenderContext(this, vertexArray, elements, (vertexBufferObject, vertexArray2, array) -> vertexBufferObject._handle = this.LoadVBO(vertexArray2, array));
        this._beginMode = BeginMode.Triangles;
    }
    
    public VertexBufferObject(final VertexArray vertexArray, int[] elements, final boolean b) {
        this.bStatic = false;
        this.m_vertexFormat = vertexArray.m_format;
        if (b) {
            final int[] array = new int[elements.length];
            int n = 0;
            for (int i = elements.length - 1 - 2; i >= 0; i -= 3) {
                array[n] = elements[i];
                array[n + 1] = elements[i + 1];
                array[n + 2] = elements[i + 2];
                n += 3;
            }
            elements = array;
        }
        this.elements = elements;
        this.bStatic = false;
        this._handle = this.LoadVBO(vertexArray, elements);
        this._beginMode = BeginMode.Triangles;
    }
    
    @Deprecated
    private Vbo LoadVBO(final VertexPositionNormalTangentTextureSkin[] array, final int[] array2) {
        final Vbo vbo = new Vbo();
        final int vertexStride = 76;
        vbo.FaceDataOnly = false;
        final ByteBuffer byteBuffer = BufferUtils.createByteBuffer(array.length * vertexStride);
        final ByteBuffer byteBuffer2 = BufferUtils.createByteBuffer(array2.length * 4);
        for (int i = 0; i < array.length; ++i) {
            array[i].put(byteBuffer);
        }
        for (int j = 0; j < array2.length; ++j) {
            byteBuffer2.putInt(array2[j]);
        }
        byteBuffer.flip();
        byteBuffer2.flip();
        vbo.VboID = VertexBufferObject.funcs.glGenBuffers();
        VertexBufferObject.funcs.glBindBuffer(VertexBufferObject.funcs.GL_ARRAY_BUFFER(), vbo.VboID);
        VertexBufferObject.funcs.glBufferData(VertexBufferObject.funcs.GL_ARRAY_BUFFER(), byteBuffer, VertexBufferObject.funcs.GL_STATIC_DRAW());
        VertexBufferObject.funcs.glGetBufferParameter(VertexBufferObject.funcs.GL_ARRAY_BUFFER(), VertexBufferObject.funcs.GL_BUFFER_SIZE(), vbo.b);
        if (array.length * vertexStride != vbo.b.get()) {
            throw new RuntimeException("Vertex data not uploaded correctly");
        }
        vbo.EboID = VertexBufferObject.funcs.glGenBuffers();
        VertexBufferObject.funcs.glBindBuffer(VertexBufferObject.funcs.GL_ELEMENT_ARRAY_BUFFER(), vbo.EboID);
        VertexBufferObject.funcs.glBufferData(VertexBufferObject.funcs.GL_ELEMENT_ARRAY_BUFFER(), byteBuffer2, VertexBufferObject.funcs.GL_STATIC_DRAW());
        vbo.b.clear();
        VertexBufferObject.funcs.glGetBufferParameter(VertexBufferObject.funcs.GL_ELEMENT_ARRAY_BUFFER(), VertexBufferObject.funcs.GL_BUFFER_SIZE(), vbo.b);
        if (array2.length * 4 != vbo.b.get()) {
            throw new RuntimeException("Element data not uploaded correctly");
        }
        vbo.NumElements = array2.length;
        vbo.VertexStride = vertexStride;
        return vbo;
    }
    
    public Vbo LoadSoftwareVBO(final ByteBuffer byteBuffer, final Vbo vbo, final int[] array) {
        Vbo vbo2 = vbo;
        ByteBuffer byteBuffer2 = null;
        if (vbo2 == null) {
            vbo2 = new Vbo();
            vbo2.VboID = VertexBufferObject.funcs.glGenBuffers();
            final ByteBuffer byteBuffer3 = BufferUtils.createByteBuffer(array.length * 4);
            for (int i = 0; i < array.length; ++i) {
                byteBuffer3.putInt(array[i]);
            }
            byteBuffer3.flip();
            byteBuffer2 = byteBuffer3;
            vbo2.VertexStride = 36;
            vbo2.NumElements = array.length;
        }
        else {
            vbo2.b.clear();
        }
        vbo2.FaceDataOnly = false;
        VertexBufferObject.funcs.glBindBuffer(VertexBufferObject.funcs.GL_ARRAY_BUFFER(), vbo2.VboID);
        VertexBufferObject.funcs.glBufferData(VertexBufferObject.funcs.GL_ARRAY_BUFFER(), byteBuffer, VertexBufferObject.funcs.GL_STATIC_DRAW());
        VertexBufferObject.funcs.glGetBufferParameter(VertexBufferObject.funcs.GL_ARRAY_BUFFER(), VertexBufferObject.funcs.GL_BUFFER_SIZE(), vbo2.b);
        if (byteBuffer2 != null) {
            vbo2.EboID = VertexBufferObject.funcs.glGenBuffers();
            VertexBufferObject.funcs.glBindBuffer(VertexBufferObject.funcs.GL_ELEMENT_ARRAY_BUFFER(), vbo2.EboID);
            VertexBufferObject.funcs.glBufferData(VertexBufferObject.funcs.GL_ELEMENT_ARRAY_BUFFER(), byteBuffer2, VertexBufferObject.funcs.GL_STATIC_DRAW());
        }
        return vbo2;
    }
    
    @Deprecated
    private Vbo LoadVBO(final VertexPositionNormalTangentTexture[] array, final int[] array2) {
        final Vbo vbo = new Vbo();
        final int vertexStride = 44;
        vbo.FaceDataOnly = false;
        final ByteBuffer byteBuffer = BufferUtils.createByteBuffer(array.length * vertexStride);
        final ByteBuffer byteBuffer2 = BufferUtils.createByteBuffer(array2.length * 4);
        for (int i = 0; i < array.length; ++i) {
            array[i].put(byteBuffer);
        }
        for (int j = 0; j < array2.length; ++j) {
            byteBuffer2.putInt(array2[j]);
        }
        byteBuffer.flip();
        byteBuffer2.flip();
        vbo.VboID = VertexBufferObject.funcs.glGenBuffers();
        VertexBufferObject.funcs.glBindBuffer(VertexBufferObject.funcs.GL_ARRAY_BUFFER(), vbo.VboID);
        VertexBufferObject.funcs.glBufferData(VertexBufferObject.funcs.GL_ARRAY_BUFFER(), byteBuffer, VertexBufferObject.funcs.GL_STATIC_DRAW());
        VertexBufferObject.funcs.glGetBufferParameter(VertexBufferObject.funcs.GL_ARRAY_BUFFER(), VertexBufferObject.funcs.GL_BUFFER_SIZE(), vbo.b);
        if (array.length * vertexStride != vbo.b.get()) {
            throw new RuntimeException("Vertex data not uploaded correctly");
        }
        vbo.EboID = VertexBufferObject.funcs.glGenBuffers();
        VertexBufferObject.funcs.glBindBuffer(VertexBufferObject.funcs.GL_ELEMENT_ARRAY_BUFFER(), vbo.EboID);
        VertexBufferObject.funcs.glBufferData(VertexBufferObject.funcs.GL_ELEMENT_ARRAY_BUFFER(), byteBuffer2, VertexBufferObject.funcs.GL_STATIC_DRAW());
        vbo.b.clear();
        VertexBufferObject.funcs.glGetBufferParameter(VertexBufferObject.funcs.GL_ELEMENT_ARRAY_BUFFER(), VertexBufferObject.funcs.GL_BUFFER_SIZE(), vbo.b);
        if (array2.length * 4 != vbo.b.get()) {
            throw new RuntimeException("Element data not uploaded correctly");
        }
        vbo.NumElements = array2.length;
        vbo.VertexStride = vertexStride;
        return vbo;
    }
    
    private Vbo LoadVBO(final VertexArray vertexArray, final int[] array) {
        final Vbo vbo = new Vbo();
        vbo.FaceDataOnly = false;
        final ByteBuffer memAlloc = MemoryUtil.memAlloc(array.length * 4);
        for (int i = 0; i < array.length; ++i) {
            memAlloc.putInt(array[i]);
        }
        vertexArray.m_buffer.position(0);
        vertexArray.m_buffer.limit(vertexArray.m_numVertices * vertexArray.m_format.m_stride);
        memAlloc.flip();
        vbo.VboID = VertexBufferObject.funcs.glGenBuffers();
        VertexBufferObject.funcs.glBindBuffer(VertexBufferObject.funcs.GL_ARRAY_BUFFER(), vbo.VboID);
        VertexBufferObject.funcs.glBufferData(VertexBufferObject.funcs.GL_ARRAY_BUFFER(), vertexArray.m_buffer, VertexBufferObject.funcs.GL_STATIC_DRAW());
        VertexBufferObject.funcs.glGetBufferParameter(VertexBufferObject.funcs.GL_ARRAY_BUFFER(), VertexBufferObject.funcs.GL_BUFFER_SIZE(), vbo.b);
        if (vertexArray.m_numVertices * vertexArray.m_format.m_stride != vbo.b.get()) {
            throw new RuntimeException("Vertex data not uploaded correctly");
        }
        vbo.EboID = VertexBufferObject.funcs.glGenBuffers();
        VertexBufferObject.funcs.glBindBuffer(VertexBufferObject.funcs.GL_ELEMENT_ARRAY_BUFFER(), vbo.EboID);
        VertexBufferObject.funcs.glBufferData(VertexBufferObject.funcs.GL_ELEMENT_ARRAY_BUFFER(), memAlloc, VertexBufferObject.funcs.GL_STATIC_DRAW());
        MemoryUtil.memFree((Buffer)memAlloc);
        vbo.b.clear();
        VertexBufferObject.funcs.glGetBufferParameter(VertexBufferObject.funcs.GL_ELEMENT_ARRAY_BUFFER(), VertexBufferObject.funcs.GL_BUFFER_SIZE(), vbo.b);
        if (array.length * 4 != vbo.b.get()) {
            throw new RuntimeException("Element data not uploaded correctly");
        }
        vbo.NumElements = array.length;
        vbo.VertexStride = vertexArray.m_format.m_stride;
        return vbo;
    }
    
    public void clear() {
        if (this._handle == null) {
            return;
        }
        if (this._handle.VboID > 0) {
            VertexBufferObject.funcs.glDeleteBuffers(this._handle.VboID);
            this._handle.VboID = -1;
        }
        if (this._handle.EboID > 0) {
            VertexBufferObject.funcs.glDeleteBuffers(this._handle.EboID);
            this._handle.EboID = -1;
        }
        this._handle = null;
    }
    
    public void Draw(final Shader shader) {
        Draw(this._handle, this.m_vertexFormat, shader, 4);
    }
    
    public void DrawStrip(final Shader shader) {
        Draw(this._handle, this.m_vertexFormat, shader, 5);
    }
    
    private static void Draw(final Vbo vbo, final VertexFormat vertexFormat, final Shader shader, final int n) {
        if (vbo == null) {
            return;
        }
        if (DebugOptions.instance.DebugDraw_SkipVBODraw.getValue()) {
            return;
        }
        int n2 = 33984;
        boolean b = false;
        if (!vbo.FaceDataOnly) {
            VertexBufferObject.funcs.glBindBuffer(VertexBufferObject.funcs.GL_ARRAY_BUFFER(), vbo.VboID);
            for (int i = 0; i < vertexFormat.m_elements.length; ++i) {
                final VertexElement vertexElement = vertexFormat.m_elements[i];
                switch (vertexElement.m_type) {
                    case VertexArray: {
                        GL20.glVertexPointer(3, 5126, vbo.VertexStride, (long)vertexElement.m_byteOffset);
                        GL20.glEnableClientState(32884);
                        break;
                    }
                    case NormalArray: {
                        GL20.glNormalPointer(5126, vbo.VertexStride, (long)vertexElement.m_byteOffset);
                        GL20.glEnableClientState(32885);
                        break;
                    }
                    case ColorArray: {
                        GL20.glColorPointer(3, 5121, vbo.VertexStride, (long)vertexElement.m_byteOffset);
                        GL20.glEnableClientState(32886);
                        break;
                    }
                    case TextureCoordArray: {
                        GL20.glActiveTexture(n2);
                        GL20.glClientActiveTexture(n2);
                        GL20.glTexCoordPointer(2, 5126, vbo.VertexStride, (long)vertexElement.m_byteOffset);
                        ++n2;
                        GL20.glEnableClientState(32888);
                    }
                    case BlendWeightArray: {
                        final int boneWeightsAttrib = shader.BoneWeightsAttrib;
                        GL20.glVertexAttribPointer(boneWeightsAttrib, 4, 5126, false, vbo.VertexStride, (long)vertexElement.m_byteOffset);
                        GL20.glEnableVertexAttribArray(boneWeightsAttrib);
                        b = true;
                        break;
                    }
                    case BlendIndexArray: {
                        final int boneIndicesAttrib = shader.BoneIndicesAttrib;
                        GL20.glVertexAttribPointer(boneIndicesAttrib, 4, 5126, false, vbo.VertexStride, (long)vertexElement.m_byteOffset);
                        GL20.glEnableVertexAttribArray(boneIndicesAttrib);
                        break;
                    }
                }
            }
        }
        VertexBufferObject.funcs.glBindBuffer(VertexBufferObject.funcs.GL_ELEMENT_ARRAY_BUFFER(), vbo.EboID);
        GL20.glDrawElements(n, vbo.NumElements, 5125, 0L);
        GL20.glDisableClientState(32885);
        if (b && shader != null) {
            GL20.glDisableVertexAttribArray(shader.BoneWeightsAttrib);
            GL20.glDisableVertexAttribArray(shader.BoneIndicesAttrib);
        }
    }
    
    public enum VertexType
    {
        VertexArray, 
        NormalArray, 
        ColorArray, 
        IndexArray, 
        TextureCoordArray, 
        TangentArray, 
        BlendWeightArray, 
        BlendIndexArray;
        
        private static /* synthetic */ VertexType[] $values() {
            return new VertexType[] { VertexType.VertexArray, VertexType.NormalArray, VertexType.ColorArray, VertexType.IndexArray, VertexType.TextureCoordArray, VertexType.TangentArray, VertexType.BlendWeightArray, VertexType.BlendIndexArray };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    public enum BeginMode
    {
        Triangles;
        
        private static /* synthetic */ BeginMode[] $values() {
            return new BeginMode[] { BeginMode.Triangles };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    public static final class Vbo
    {
        public final IntBuffer b;
        public int VboID;
        public int EboID;
        public int NumElements;
        public int VertexStride;
        public boolean FaceDataOnly;
        
        public Vbo() {
            this.b = BufferUtils.createIntBuffer(4);
        }
    }
    
    public static final class VertexElement
    {
        public VertexType m_type;
        public int m_byteSize;
        public int m_byteOffset;
    }
    
    public static final class VertexFormat
    {
        final VertexElement[] m_elements;
        int m_stride;
        
        public VertexFormat(final int n) {
            this.m_elements = PZArrayUtil.newInstance(VertexElement.class, n, VertexElement::new);
        }
        
        public void setElement(final int n, final VertexType type, final int byteSize) {
            this.m_elements[n].m_type = type;
            this.m_elements[n].m_byteSize = byteSize;
        }
        
        public void calculate() {
            this.m_stride = 0;
            for (int i = 0; i < this.m_elements.length; ++i) {
                this.m_elements[i].m_byteOffset = this.m_stride;
                this.m_stride += this.m_elements[i].m_byteSize;
            }
        }
    }
    
    public static final class VertexArray
    {
        public final VertexFormat m_format;
        public final int m_numVertices;
        public final ByteBuffer m_buffer;
        
        public VertexArray(final VertexFormat format, final int numVertices) {
            this.m_format = format;
            this.m_numVertices = numVertices;
            this.m_buffer = BufferUtils.createByteBuffer(this.m_numVertices * this.m_format.m_stride);
        }
        
        public void setElement(final int n, final int n2, final float n3, final float n4) {
            int n5 = n * this.m_format.m_stride + this.m_format.m_elements[n2].m_byteOffset;
            this.m_buffer.putFloat(n5, n3);
            n5 += 4;
            this.m_buffer.putFloat(n5, n4);
        }
        
        public void setElement(final int n, final int n2, final float n3, final float n4, final float n5) {
            int n6 = n * this.m_format.m_stride + this.m_format.m_elements[n2].m_byteOffset;
            this.m_buffer.putFloat(n6, n3);
            n6 += 4;
            this.m_buffer.putFloat(n6, n4);
            n6 += 4;
            this.m_buffer.putFloat(n6, n5);
        }
        
        public void setElement(final int n, final int n2, final float n3, final float n4, final float n5, final float n6) {
            int n7 = n * this.m_format.m_stride + this.m_format.m_elements[n2].m_byteOffset;
            this.m_buffer.putFloat(n7, n3);
            n7 += 4;
            this.m_buffer.putFloat(n7, n4);
            n7 += 4;
            this.m_buffer.putFloat(n7, n5);
            n7 += 4;
            this.m_buffer.putFloat(n7, n6);
        }
        
        float getElementFloat(final int n, final int n2, final int n3) {
            return this.m_buffer.getFloat(n * this.m_format.m_stride + this.m_format.m_elements[n2].m_byteOffset + n3 * 4);
        }
    }
}

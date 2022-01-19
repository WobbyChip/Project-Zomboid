// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.model;

import org.lwjglx.BufferUtils;
import java.nio.IntBuffer;

public final class Vbo
{
    public IntBuffer b;
    public int VboID;
    public int EboID;
    public int NumElements;
    public int VertexStride;
    public boolean FaceDataOnly;
    
    public Vbo() {
        this.b = BufferUtils.createIntBuffer(4);
    }
}

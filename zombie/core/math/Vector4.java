// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.math;

import org.joml.Vector4f;

public class Vector4 extends Vector4f
{
    public Vector4() {
    }
    
    public Vector4(final float n, final float n2, final float n3, final float n4) {
        super(n, n2, n3, n4);
    }
    
    public Vector4(final org.lwjgl.util.vector.Vector4f vector4f) {
        super(vector4f.x, vector4f.y, vector4f.z, vector4f.w);
    }
    
    public org.lwjgl.util.vector.Vector4f Get() {
        final org.lwjgl.util.vector.Vector4f vector4f = new org.lwjgl.util.vector.Vector4f();
        vector4f.set(this.x, this.y, this.z, this.w);
        return vector4f;
    }
    
    public void Set(final org.lwjgl.util.vector.Vector4f vector4f) {
        this.x = vector4f.x;
        this.y = vector4f.y;
        this.z = vector4f.z;
        this.w = vector4f.w;
    }
}

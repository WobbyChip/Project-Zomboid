// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel;

public final class Vector4
{
    public float x;
    public float y;
    public float z;
    public float w;
    
    public Vector4() {
        this(0.0f, 0.0f, 0.0f, 0.0f);
    }
    
    public Vector4(final float x, final float y, final float z, final float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    
    public Vector4(final Vector4 vector4) {
        this.set(vector4);
    }
    
    public Vector4 set(final float x, final float y, final float z, final float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }
    
    public Vector4 set(final Vector4 vector4) {
        return this.set(vector4.x, vector4.y, vector4.z, vector4.w);
    }
}

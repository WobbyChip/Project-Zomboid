// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.math;

import org.joml.Vector3f;

public class Vector3 extends Vector3f
{
    public Vector3() {
    }
    
    public Vector3(final float n, final float n2, final float n3) {
        super(n, n2, n3);
    }
    
    public Vector3(final org.lwjgl.util.vector.Vector3f vector3f) {
        super(vector3f.x, vector3f.y, vector3f.z);
    }
    
    public Vector3(final Vector3 vector3) {
        super(vector3.x, vector3.y, vector3.z);
    }
    
    public static org.lwjgl.util.vector.Vector3f addScaled(final org.lwjgl.util.vector.Vector3f vector3f, final org.lwjgl.util.vector.Vector3f vector3f2, final float n, final org.lwjgl.util.vector.Vector3f vector3f3) {
        vector3f3.set(vector3f.x + vector3f2.x * n, vector3f.y + vector3f2.y * n, vector3f.z + vector3f2.z * n);
        return vector3f3;
    }
    
    public static org.lwjgl.util.vector.Vector3f setScaled(final org.lwjgl.util.vector.Vector3f vector3f, final float n, final org.lwjgl.util.vector.Vector3f vector3f2) {
        vector3f2.set(vector3f.x * n, vector3f.y * n, vector3f.z * n);
        return vector3f2;
    }
    
    public org.lwjgl.util.vector.Vector3f Get() {
        final org.lwjgl.util.vector.Vector3f vector3f = new org.lwjgl.util.vector.Vector3f();
        vector3f.set(this.x, this.y, this.z);
        return vector3f;
    }
    
    public void Set(final org.lwjgl.util.vector.Vector3f vector3f) {
        this.x = vector3f.x;
        this.y = vector3f.y;
        this.z = vector3f.z;
    }
    
    public Vector3 reset() {
        final float x = 0.0f;
        this.z = x;
        this.y = x;
        this.x = x;
        return this;
    }
    
    public float dot(final Vector3 vector3) {
        return this.x * vector3.x + this.y * vector3.y + this.z * vector3.z;
    }
    
    public Vector3 cross(final Vector3 vector3) {
        return new Vector3(this.y() * vector3.z() - vector3.y() * this.z(), vector3.z() * this.x() - this.z() * vector3.x(), this.x() * vector3.y() - vector3.x() * this.y());
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel;

public final class Vector3
{
    private float x;
    private float y;
    private float z;
    
    public Vector3() {
        this(0.0f, 0.0f, 0.0f);
    }
    
    public Vector3(final float x, final float y, final float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Vector3(final Vector3 vector3) {
        this.set(vector3);
    }
    
    public float x() {
        return this.x;
    }
    
    public Vector3 x(final float x) {
        this.x = x;
        return this;
    }
    
    public float y() {
        return this.y;
    }
    
    public Vector3 y(final float y) {
        this.y = y;
        return this;
    }
    
    public float z() {
        return this.z;
    }
    
    public Vector3 z(final float z) {
        this.z = z;
        return this;
    }
    
    public Vector3 set(final float x, final float y, final float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }
    
    public Vector3 set(final Vector3 vector3) {
        return this.set(vector3.x(), vector3.y(), vector3.z());
    }
    
    public Vector3 reset() {
        final float x = 0.0f;
        this.z = x;
        this.y = x;
        this.x = x;
        return this;
    }
    
    public float length() {
        return (float)Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }
    
    public Vector3 normalize() {
        final float length = this.length();
        this.x /= length;
        this.y /= length;
        this.z /= length;
        return this;
    }
    
    public float dot(final Vector3 vector3) {
        return this.x * vector3.x + this.y * vector3.y + this.z * vector3.z;
    }
    
    public Vector3 cross(final Vector3 vector3) {
        return new Vector3(this.y() * vector3.z() - vector3.y() * this.z(), vector3.z() * this.x() - this.z() * vector3.x(), this.x() * vector3.y() - vector3.x() * this.y());
    }
    
    public Vector3 add(final float n, final float n2, final float n3) {
        this.x += n;
        this.y += n2;
        this.z += n3;
        return this;
    }
    
    public Vector3 add(final Vector3 vector3) {
        return this.add(vector3.x(), vector3.y(), vector3.z());
    }
    
    public Vector3 sub(final float n, final float n2, final float n3) {
        this.x -= n;
        this.y -= n2;
        this.z -= n3;
        return this;
    }
    
    public Vector3 sub(final Vector3 vector3) {
        return this.sub(vector3.x(), vector3.y(), vector3.z());
    }
    
    public Vector3 mul(final float n) {
        return this.mul(n, n, n);
    }
    
    public Vector3 mul(final float n, final float n2, final float n3) {
        this.x *= n;
        this.y *= n2;
        this.z *= n3;
        return this;
    }
    
    public Vector3 mul(final Vector3 vector3) {
        return this.mul(vector3.x(), vector3.y(), vector3.z());
    }
}

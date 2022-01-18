// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.physics;

import org.joml.Quaternionfc;
import org.joml.Quaternionf;
import org.joml.Vector3fc;
import org.joml.Matrix4f;
import org.joml.Matrix3fc;
import org.joml.Vector3f;
import org.joml.Matrix3f;

public final class Transform
{
    public final Matrix3f basis;
    public final Vector3f origin;
    
    public Transform() {
        this.basis = new Matrix3f();
        this.origin = new Vector3f();
    }
    
    public Transform(final Matrix3f matrix3f) {
        this.basis = new Matrix3f();
        this.origin = new Vector3f();
        this.basis.set((Matrix3fc)matrix3f);
    }
    
    public Transform(final Matrix4f matrix4f) {
        this.basis = new Matrix3f();
        this.origin = new Vector3f();
        this.set(matrix4f);
    }
    
    public Transform(final Transform transform) {
        this.basis = new Matrix3f();
        this.origin = new Vector3f();
        this.set(transform);
    }
    
    public void set(final Transform transform) {
        this.basis.set((Matrix3fc)transform.basis);
        this.origin.set((Vector3fc)transform.origin);
    }
    
    public void set(final Matrix3f matrix3f) {
        this.basis.set((Matrix3fc)matrix3f);
        this.origin.set(0.0f, 0.0f, 0.0f);
    }
    
    public void set(final Matrix4f matrix4f) {
        matrix4f.get3x3(this.basis);
        matrix4f.getTranslation(this.origin);
    }
    
    public void transform(final Vector3f vector3f) {
        this.basis.transform(vector3f);
        vector3f.add((Vector3fc)this.origin);
    }
    
    public void setIdentity() {
        this.basis.identity();
        this.origin.set(0.0f, 0.0f, 0.0f);
    }
    
    public void inverse() {
        this.basis.transpose();
        this.origin.negate();
        this.basis.transform(this.origin);
    }
    
    public void inverse(final Transform transform) {
        this.set(transform);
        this.inverse();
    }
    
    public Quaternionf getRotation(final Quaternionf quaternionf) {
        this.basis.getUnnormalizedRotation(quaternionf);
        return quaternionf;
    }
    
    public void setRotation(final Quaternionf quaternionf) {
        this.basis.set((Quaternionfc)quaternionf);
    }
    
    public Matrix4f getMatrix(final Matrix4f matrix4f) {
        matrix4f.set((Matrix3fc)this.basis);
        matrix4f.setTranslation((Vector3fc)this.origin);
        return matrix4f;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof Transform)) {
            return false;
        }
        final Transform transform = (Transform)o;
        return this.basis.equals((Object)transform.basis) && this.origin.equals((Object)transform.origin);
    }
    
    @Override
    public int hashCode() {
        return 41 * (41 * 3 + this.basis.hashCode()) + this.origin.hashCode();
    }
}

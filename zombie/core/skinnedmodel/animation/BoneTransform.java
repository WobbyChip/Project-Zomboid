// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.animation;

import org.lwjgl.util.vector.ReadableVector4f;
import org.lwjgl.util.vector.ReadableVector3f;
import zombie.util.Pool;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import zombie.core.skinnedmodel.HelperFunctions;
import org.lwjgl.util.vector.Matrix4f;
import zombie.util.PooledObject;

public class BoneTransform extends PooledObject
{
    private boolean m_matrixValid;
    private final Matrix4f m_matrix;
    private final HelperFunctions.TransformResult_QPS m_transformResult;
    private boolean m_prsValid;
    private final Vector3f m_pos;
    private final Quaternion m_rot;
    private final Vector3f m_scale;
    private static final Pool<BoneTransform> s_pool;
    
    protected BoneTransform() {
        this.m_matrixValid = true;
        this.m_matrix = new Matrix4f();
        this.m_transformResult = new HelperFunctions.TransformResult_QPS(this.m_matrix);
        this.m_prsValid = true;
        this.m_pos = new Vector3f();
        this.m_rot = new Quaternion();
        this.m_scale = new Vector3f();
        this.setIdentity();
    }
    
    public void setIdentity() {
        this.m_matrixValid = true;
        this.m_matrix.setIdentity();
        this.m_prsValid = true;
        this.m_pos.set(0.0f, 0.0f, 0.0f);
        this.m_rot.setIdentity();
        this.m_scale.set(1.0f, 1.0f, 1.0f);
    }
    
    public void set(final BoneTransform boneTransform) {
        this.m_matrixValid = boneTransform.m_matrixValid;
        this.m_prsValid = boneTransform.m_prsValid;
        this.m_pos.set((ReadableVector3f)boneTransform.m_pos);
        this.m_rot.set((ReadableVector4f)boneTransform.m_rot);
        this.m_scale.set((ReadableVector3f)boneTransform.m_scale);
        this.m_matrix.load(boneTransform.m_matrix);
    }
    
    public void set(final Vector3f vector3f, final Quaternion obj, final Vector3f vector3f2) {
        if (!this.m_matrixValid && this.m_prsValid && this.m_pos.equals((Object)vector3f) && this.m_rot.equals(obj) && this.m_scale.equals((Object)vector3f2)) {
            return;
        }
        this.m_matrixValid = false;
        this.m_prsValid = true;
        this.m_pos.set((ReadableVector3f)vector3f);
        this.m_rot.set((ReadableVector4f)obj);
        this.m_scale.set((ReadableVector3f)vector3f2);
    }
    
    public void set(final Matrix4f matrix4f) {
        this.m_matrixValid = true;
        this.m_matrix.load(matrix4f);
        this.m_prsValid = false;
    }
    
    public void mul(final Matrix4f matrix4f, final Matrix4f matrix4f2) {
        this.m_matrixValid = true;
        this.m_prsValid = false;
        Matrix4f.mul(matrix4f, matrix4f2, this.m_matrix);
    }
    
    public void getMatrix(final Matrix4f matrix4f) {
        matrix4f.load(this.getValidMatrix_Internal());
    }
    
    public void getPRS(final Vector3f vector3f, final Quaternion quaternion, final Vector3f vector3f2) {
        this.validatePRS();
        vector3f.set((ReadableVector3f)this.m_pos);
        quaternion.set((ReadableVector4f)this.m_rot);
        vector3f2.set((ReadableVector3f)this.m_scale);
    }
    
    public void getPosition(final Vector3f vector3f) {
        this.validatePRS();
        vector3f.set((ReadableVector3f)this.m_pos);
    }
    
    private Matrix4f getValidMatrix_Internal() {
        this.validateMatrix();
        return this.m_matrix;
    }
    
    private void validateMatrix() {
        if (this.m_matrixValid) {
            return;
        }
        this.validateInternal();
        this.m_matrixValid = true;
        HelperFunctions.CreateFromQuaternionPositionScale(this.m_pos, this.m_rot, this.m_scale, this.m_transformResult);
    }
    
    protected void validatePRS() {
        if (this.m_prsValid) {
            return;
        }
        this.validateInternal();
        this.m_prsValid = true;
        HelperFunctions.getPosition(this.m_matrix, this.m_pos);
        HelperFunctions.getRotation(this.m_matrix, this.m_rot);
        this.m_scale.set(1.0f, 1.0f, 1.0f);
    }
    
    protected void validateInternal() {
        if (!this.m_prsValid && !this.m_matrixValid) {
            throw new RuntimeException("Neither the matrix nor the PosRotScale values in this object are listed as valid.");
        }
    }
    
    public static void mul(final BoneTransform boneTransform, final Matrix4f matrix4f, final Matrix4f matrix4f2) {
        Matrix4f.mul(boneTransform.getValidMatrix_Internal(), matrix4f, matrix4f2);
    }
    
    public static BoneTransform alloc() {
        return BoneTransform.s_pool.alloc();
    }
    
    static {
        s_pool = new Pool<BoneTransform>(BoneTransform::new);
    }
}

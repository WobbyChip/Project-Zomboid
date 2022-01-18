// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.math;

import org.joml.Matrix4fc;
import org.joml.Matrix4f;

public final class Matrix4 extends Matrix4f
{
    public Matrix4(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11, final float n12, final float n13, final float n14, final float n15, final float n16) {
        super(n, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, n15, n16);
    }
    
    public Matrix4(final Matrix4 matrix4) {
        super((Matrix4fc)matrix4);
    }
    
    public org.lwjgl.util.vector.Matrix4f Get() {
        final org.lwjgl.util.vector.Matrix4f matrix4f = new org.lwjgl.util.vector.Matrix4f();
        matrix4f.m00 = this.m00();
        matrix4f.m01 = this.m01();
        matrix4f.m02 = this.m02();
        matrix4f.m03 = this.m03();
        matrix4f.m10 = this.m10();
        matrix4f.m11 = this.m11();
        matrix4f.m12 = this.m12();
        matrix4f.m13 = this.m13();
        matrix4f.m20 = this.m20();
        matrix4f.m21 = this.m21();
        matrix4f.m22 = this.m22();
        matrix4f.m23 = this.m23();
        matrix4f.m30 = this.m30();
        matrix4f.m31 = this.m31();
        matrix4f.m32 = this.m32();
        matrix4f.m33 = this.m33();
        return matrix4f;
    }
    
    public void Set(final org.lwjgl.util.vector.Matrix4f matrix4f) {
        this.set(matrix4f.m00, matrix4f.m01, matrix4f.m02, matrix4f.m03, matrix4f.m10, matrix4f.m11, matrix4f.m12, matrix4f.m13, matrix4f.m20, matrix4f.m21, matrix4f.m22, matrix4f.m23, matrix4f.m30, matrix4f.m31, matrix4f.m32, matrix4f.m33);
    }
}

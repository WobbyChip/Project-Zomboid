// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel;

import zombie.debug.DebugLog;
import zombie.util.list.PZArrayUtil;
import zombie.core.math.PZMath;
import org.lwjgl.util.vector.Vector4f;
import java.util.List;
import zombie.core.skinnedmodel.model.VertexPositionNormalTangentTextureSkin;
import zombie.core.Color;
import zombie.popman.ObjectPool;
import java.util.concurrent.atomic.AtomicBoolean;
import org.lwjgl.util.vector.Matrix4f;
import java.util.Stack;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

public final class HelperFunctions
{
    private static final Vector3f s_zero3;
    private static final Quaternion s_identityQ;
    private static final Stack<Matrix4f> MatrixStack;
    private static final AtomicBoolean MatrixLock;
    private static final ObjectPool<Vector3f> VectorPool;
    
    public static int ToRgba(final Color color) {
        return (int)color.a << 24 | (int)color.b << 16 | (int)color.g << 8 | (int)color.r;
    }
    
    public static void returnMatrix(final Matrix4f matrix4f) {
        while (!HelperFunctions.MatrixLock.compareAndSet(false, true)) {
            Thread.onSpinWait();
        }
        assert !HelperFunctions.MatrixStack.contains(matrix4f);
        HelperFunctions.MatrixStack.push(matrix4f);
        HelperFunctions.MatrixLock.set(false);
    }
    
    public static Matrix4f getMatrix() {
        while (!HelperFunctions.MatrixLock.compareAndSet(false, true)) {
            Thread.onSpinWait();
        }
        Matrix4f matrix4f;
        if (HelperFunctions.MatrixStack.isEmpty()) {
            matrix4f = new Matrix4f();
        }
        else {
            matrix4f = HelperFunctions.MatrixStack.pop();
        }
        HelperFunctions.MatrixLock.set(false);
        return matrix4f;
    }
    
    public static Matrix4f getMatrix(final Matrix4f matrix4f) {
        final Matrix4f matrix = getMatrix();
        matrix.load(matrix4f);
        return matrix;
    }
    
    public static Vector3f getVector3f() {
        while (!HelperFunctions.MatrixLock.compareAndSet(false, true)) {
            Thread.onSpinWait();
        }
        final Vector3f vector3f = HelperFunctions.VectorPool.alloc();
        HelperFunctions.MatrixLock.set(false);
        return vector3f;
    }
    
    public static void returnVector3f(final Vector3f vector3f) {
        while (!HelperFunctions.MatrixLock.compareAndSet(false, true)) {
            Thread.onSpinWait();
        }
        HelperFunctions.VectorPool.release(vector3f);
        HelperFunctions.MatrixLock.set(false);
    }
    
    public static Matrix4f CreateFromQuaternion(final Quaternion quaternion) {
        final Matrix4f matrix = getMatrix();
        CreateFromQuaternion(quaternion, matrix);
        return matrix;
    }
    
    public static Matrix4f CreateFromQuaternion(final Quaternion quaternion, final Matrix4f matrix4f) {
        matrix4f.setIdentity();
        final float lengthSquared = quaternion.lengthSquared();
        if ((lengthSquared > 0.0f && lengthSquared < 0.99999f) || lengthSquared > 1.00001f) {
            quaternion.scale(1.0f / (float)Math.sqrt(lengthSquared));
        }
        final float n = quaternion.x * quaternion.x;
        final float n2 = quaternion.x * quaternion.y;
        final float n3 = quaternion.x * quaternion.z;
        final float n4 = quaternion.x * quaternion.w;
        final float n5 = quaternion.y * quaternion.y;
        final float n6 = quaternion.y * quaternion.z;
        final float n7 = quaternion.y * quaternion.w;
        final float n8 = quaternion.z * quaternion.z;
        final float n9 = quaternion.z * quaternion.w;
        matrix4f.m00 = 1.0f - 2.0f * (n5 + n8);
        matrix4f.m10 = 2.0f * (n2 - n9);
        matrix4f.m20 = 2.0f * (n3 + n7);
        matrix4f.m30 = 0.0f;
        matrix4f.m01 = 2.0f * (n2 + n9);
        matrix4f.m11 = 1.0f - 2.0f * (n + n8);
        matrix4f.m21 = 2.0f * (n6 - n4) * 1.0f;
        matrix4f.m31 = 0.0f;
        matrix4f.m02 = 2.0f * (n3 - n7);
        matrix4f.m12 = 2.0f * (n6 + n4);
        matrix4f.m22 = 1.0f - 2.0f * (n + n5);
        matrix4f.m32 = 0.0f;
        matrix4f.m03 = 0.0f;
        matrix4f.m13 = 0.0f;
        matrix4f.m23 = 0.0f;
        matrix4f.m33 = 1.0f;
        matrix4f.m30 = 0.0f;
        matrix4f.m31 = 0.0f;
        matrix4f.m32 = 0.0f;
        matrix4f.transpose();
        return matrix4f;
    }
    
    public static Matrix4f CreateFromQuaternionPositionScale(final Vector3f vector3f, final Quaternion quaternion, final Vector3f vector3f2, final Matrix4f matrix4f) {
        final Matrix4f matrix = getMatrix();
        final Matrix4f matrix2 = getMatrix();
        final Matrix4f matrix3 = getMatrix();
        CreateFromQuaternionPositionScale(vector3f, quaternion, vector3f2, matrix4f, matrix2, matrix3, matrix);
        returnMatrix(matrix);
        returnMatrix(matrix2);
        returnMatrix(matrix3);
        return matrix4f;
    }
    
    public static void CreateFromQuaternionPositionScale(final Vector3f vector3f, final Quaternion quaternion, final Vector3f vector3f2, final TransformResult_QPS transformResult_QPS) {
        CreateFromQuaternionPositionScale(vector3f, quaternion, vector3f2, transformResult_QPS.result, transformResult_QPS.trans, transformResult_QPS.rot, transformResult_QPS.scl);
    }
    
    private static void CreateFromQuaternionPositionScale(final Vector3f vector3f, final Quaternion quaternion, final Vector3f vector3f2, final Matrix4f matrix4f, final Matrix4f matrix4f2, final Matrix4f matrix4f3, final Matrix4f matrix4f4) {
        matrix4f4.setIdentity();
        matrix4f4.scale(vector3f2);
        matrix4f2.setIdentity();
        matrix4f2.translate(vector3f);
        matrix4f2.transpose();
        CreateFromQuaternion(quaternion, matrix4f3);
        Matrix4f.mul(matrix4f4, matrix4f3, matrix4f3);
        Matrix4f.mul(matrix4f3, matrix4f2, matrix4f);
    }
    
    public static void TransformVertices(final VertexPositionNormalTangentTextureSkin[] array, final List<Matrix4f> list) {
        final Vector3 vector3 = new Vector3();
        final Vector3 vector4 = new Vector3();
        final Vector4f vector4f = new Vector4f();
        for (final VertexPositionNormalTangentTextureSkin vertexPositionNormalTangentTextureSkin : array) {
            vector3.reset();
            vector4.reset();
            final Vector3 position = vertexPositionNormalTangentTextureSkin.Position;
            final Vector3 normal = vertexPositionNormalTangentTextureSkin.Normal;
            ApplyBlendBone(vertexPositionNormalTangentTextureSkin.BlendWeights.x, list.get(vertexPositionNormalTangentTextureSkin.BlendIndices.X), position, normal, vector4f, vector3, vector4);
            ApplyBlendBone(vertexPositionNormalTangentTextureSkin.BlendWeights.y, list.get(vertexPositionNormalTangentTextureSkin.BlendIndices.Y), position, normal, vector4f, vector3, vector4);
            ApplyBlendBone(vertexPositionNormalTangentTextureSkin.BlendWeights.z, list.get(vertexPositionNormalTangentTextureSkin.BlendIndices.Z), position, normal, vector4f, vector3, vector4);
            ApplyBlendBone(vertexPositionNormalTangentTextureSkin.BlendWeights.w, list.get(vertexPositionNormalTangentTextureSkin.BlendIndices.W), position, normal, vector4f, vector3, vector4);
            position.set(vector3);
            normal.set(vector4);
        }
    }
    
    public static void ApplyBlendBone(final float n, final Matrix4f matrix4f, final Vector3 vector3, final Vector3 vector4, final Vector4f vector4f, final Vector3 vector5, final Vector3 vector6) {
        if (n > 0.0f) {
            final float x = vector3.x();
            final float y = vector3.y();
            final float z = vector3.z();
            vector5.add((matrix4f.m00 * x + matrix4f.m01 * y + matrix4f.m02 * z + matrix4f.m03) * n, (matrix4f.m10 * x + matrix4f.m11 * y + matrix4f.m12 * z + matrix4f.m13) * n, (matrix4f.m20 * x + matrix4f.m21 * y + matrix4f.m22 * z + matrix4f.m23) * n);
            final float x2 = vector4.x();
            final float y2 = vector4.y();
            final float z2 = vector4.z();
            vector6.add((matrix4f.m00 * x2 + matrix4f.m01 * y2 + matrix4f.m02 * z2) * n, (matrix4f.m10 * x2 + matrix4f.m11 * y2 + matrix4f.m12 * z2) * n, (matrix4f.m20 * x2 + matrix4f.m21 * y2 + matrix4f.m22 * z2) * n);
        }
    }
    
    public static Vector3f getPosition(final Matrix4f matrix4f, final Vector3f vector3f) {
        vector3f.set(matrix4f.m03, matrix4f.m13, matrix4f.m23);
        return vector3f;
    }
    
    public static void setPosition(final Matrix4f matrix4f, final Vector3f vector3f) {
        matrix4f.m03 = vector3f.x;
        matrix4f.m13 = vector3f.y;
        matrix4f.m23 = vector3f.z;
    }
    
    public static Quaternion getRotation(final Matrix4f matrix4f, final Quaternion quaternion) {
        return Quaternion.setFromMatrix(matrix4f, quaternion);
    }
    
    public static void transform(final Quaternion quaternion, final Vector3f vector3f, final Vector3f vector3f2) {
        quaternion.normalise();
        final float w = quaternion.w;
        final float x = quaternion.x;
        final float y = quaternion.y;
        final float z = quaternion.z;
        final float n = w * w;
        final float n2 = x * x + y * y + z * z;
        final float x2 = vector3f.x;
        final float y2 = vector3f.y;
        final float z2 = vector3f.z;
        final float n3 = y * z2 - z * y2;
        final float n4 = z * x2 - x * z2;
        final float n5 = x * y2 - y * x2;
        final float n6 = x2 * x + y2 * y + z2 * z;
        vector3f2.set((n - n2) * x2 + 2.0f * w * n3 + 2.0f * x * n6, (n - n2) * y2 + 2.0f * w * n4 + 2.0f * y * n6, (n - n2) * z2 + 2.0f * w * n5 + 2.0f * z * n6);
    }
    
    private static Vector4f transform(final Matrix4f matrix4f, final Vector4f vector4f, final Vector4f vector4f2) {
        final float x = matrix4f.m00 * vector4f.x + matrix4f.m01 * vector4f.y + matrix4f.m02 * vector4f.z + matrix4f.m30 * vector4f.w;
        final float y = matrix4f.m10 * vector4f.x + matrix4f.m11 * vector4f.y + matrix4f.m12 * vector4f.z + matrix4f.m31 * vector4f.w;
        final float z = matrix4f.m20 * vector4f.x + matrix4f.m21 * vector4f.y + matrix4f.m22 * vector4f.z + matrix4f.m32 * vector4f.w;
        final float w = matrix4f.m03 * vector4f.x + matrix4f.m13 * vector4f.y + matrix4f.m23 * vector4f.z + matrix4f.m33 * vector4f.w;
        vector4f2.x = x;
        vector4f2.y = y;
        vector4f2.z = z;
        vector4f2.w = w;
        return vector4f2;
    }
    
    public static float getRotationY(final Quaternion quaternion) {
        quaternion.normalise();
        final float w = quaternion.w;
        final float x = quaternion.x;
        final float y = quaternion.y;
        final float z = quaternion.z;
        final float n = w * w;
        final float n2 = x * x + y * y + z * z;
        final float n3 = y * 0.0f - z * 0.0f;
        final float n4 = x * 0.0f - y * 1.0f;
        final float n5 = 1.0f * x + 0.0f * y + 0.0f * z;
        return PZMath.wrap((float)Math.atan2(-((n - n2) * 0.0f + 2.0f * w * n4 + 2.0f * z * n5), (n - n2) * 1.0f + 2.0f * w * n3 + 2.0f * x * n5), -3.1415927f, 3.1415927f);
    }
    
    public static float getRotationZ(final Quaternion quaternion) {
        final float w = quaternion.w;
        final float x = quaternion.x;
        final float y = quaternion.y;
        final float z = quaternion.z;
        final float n = w * w;
        final float n2 = x * x + y * y + z * z;
        final float n3 = z * 1.0f;
        final float n4 = 1.0f * x;
        return (float)Math.atan2(2.0f * w * n3 + 2.0f * y * n4, (n - n2) * 1.0f + 2.0f * x * n4);
    }
    
    public static Vector3f ToEulerAngles(final Quaternion quaternion, final Vector3f vector3f) {
        vector3f.x = (float)Math.atan2(2.0 * (quaternion.w * quaternion.x + quaternion.y * quaternion.z), 1.0 - 2.0 * (quaternion.x * quaternion.x + quaternion.y * quaternion.y));
        final double a = 2.0 * (quaternion.w * quaternion.y - quaternion.z * quaternion.x);
        if (Math.abs(a) >= 1.0) {
            vector3f.y = (float)Math.copySign(1.5707963705062866, a);
        }
        else {
            vector3f.y = (float)Math.asin(a);
        }
        vector3f.z = (float)Math.atan2(2.0 * (quaternion.w * quaternion.z + quaternion.x * quaternion.y), 1.0 - 2.0 * (quaternion.y * quaternion.y + quaternion.z * quaternion.z));
        return vector3f;
    }
    
    public static Quaternion ToQuaternion(final double n, final double n2, final double n3, final Quaternion quaternion) {
        final double cos = Math.cos(n3 * 0.5);
        final double sin = Math.sin(n3 * 0.5);
        final double cos2 = Math.cos(n2 * 0.5);
        final double sin2 = Math.sin(n2 * 0.5);
        final double cos3 = Math.cos(n * 0.5);
        final double sin3 = Math.sin(n * 0.5);
        quaternion.w = (float)(cos * cos2 * cos3 + sin * sin2 * sin3);
        quaternion.x = (float)(cos * cos2 * sin3 - sin * sin2 * cos3);
        quaternion.y = (float)(sin * cos2 * sin3 + cos * sin2 * cos3);
        quaternion.z = (float)(sin * cos2 * cos3 - cos * sin2 * sin3);
        return quaternion;
    }
    
    public static Vector3f getZero3() {
        HelperFunctions.s_zero3.set(0.0f, 0.0f, 0.0f);
        return HelperFunctions.s_zero3;
    }
    
    public static Quaternion getIdentityQ() {
        HelperFunctions.s_identityQ.setIdentity();
        return HelperFunctions.s_identityQ;
    }
    
    static {
        s_zero3 = new Vector3f(0.0f, 0.0f, 0.0f);
        s_identityQ = new Quaternion();
        MatrixStack = new Stack<Matrix4f>();
        MatrixLock = new AtomicBoolean(false);
        VectorPool = new ObjectPool<Vector3f>(Vector3f::new);
        UnitTests.runAll();
    }
    
    public static class TransformResult_QPS
    {
        public final Matrix4f result;
        final Matrix4f trans;
        final Matrix4f rot;
        final Matrix4f scl;
        
        public TransformResult_QPS() {
            this.result = new Matrix4f();
            this.trans = new Matrix4f();
            this.rot = new Matrix4f();
            this.scl = new Matrix4f();
        }
        
        public TransformResult_QPS(final Matrix4f result) {
            this.result = result;
            this.trans = new Matrix4f();
            this.rot = new Matrix4f();
            this.scl = new Matrix4f();
        }
    }
    
    private static final class UnitTests
    {
        private static final Runnable[] s_unitTests;
        
        private static void runAll() {
            PZArrayUtil.forEach(UnitTests.s_unitTests, Runnable::run);
        }
        
        static {
            s_unitTests = new Runnable[0];
        }
        
        private static final class getRotationZ
        {
            public static void run() {
                DebugLog.UnitTests.println("UnitTest_getRotationZ");
                DebugLog.UnitTests.println("in, out, result");
                final Quaternion quaternion = new Quaternion();
                for (int i = 0; i < 360; ++i) {
                    final float wrap = PZMath.wrap((float)i, -180.0f, 180.0f);
                    quaternion.setFromAxisAngle(new Vector4f(0.0f, 0.0f, 1.0f, wrap * 0.017453292f));
                    final float f = HelperFunctions.getRotationZ(quaternion) * 57.295776f;
                    DebugLog.UnitTests.printUnitTest("%f,%f", PZMath.equal(wrap, f, 0.001f), wrap, f);
                }
                DebugLog.UnitTests.println("UnitTest_getRotationZ. Complete");
            }
        }
        
        private static final class getRotationY
        {
            public static void run() {
                DebugLog.UnitTests.println("UnitTest_getRotationY");
                DebugLog.UnitTests.println("in, out, result");
                final Quaternion quaternion = new Quaternion();
                for (int i = 0; i < 360; ++i) {
                    final float wrap = PZMath.wrap((float)i, -180.0f, 180.0f);
                    quaternion.setFromAxisAngle(new Vector4f(0.0f, 1.0f, 0.0f, wrap * 0.017453292f));
                    final float f = HelperFunctions.getRotationY(quaternion) * 57.295776f;
                    DebugLog.UnitTests.printUnitTest("%f,%f", PZMath.equal(wrap, f, 0.001f), wrap, f);
                }
                DebugLog.UnitTests.println("UnitTest_getRotationY. Complete");
            }
        }
        
        private static final class getRotationMatrix
        {
            public static void run() {
                DebugLog.UnitTests.println("UnitTest_getRotationMatrix");
                DebugLog.UnitTests.println("q.x, q.y, q.z, q.w, q_out.x, q_out.y, q_out.z, q_out.w");
                final Quaternion quaternion = new Quaternion();
                final Vector4f fromAxisAngle = new Vector4f();
                final Matrix4f matrix4f = new Matrix4f();
                final Quaternion quaternion2 = new Quaternion();
                final Quaternion quaternion3 = new Quaternion();
                for (int i = 0; i < 360; i += 10) {
                    fromAxisAngle.set(1.0f, 0.0f, 0.0f, PZMath.wrap((float)i, -180.0f, 180.0f) * 0.017453292f);
                    quaternion.setFromAxisAngle(fromAxisAngle);
                    HelperFunctions.CreateFromQuaternion(quaternion, matrix4f);
                    HelperFunctions.getRotation(matrix4f, quaternion2);
                    quaternion3.set(-quaternion2.x, -quaternion2.y, -quaternion2.z, -quaternion2.w);
                    DebugLog.UnitTests.printUnitTest("%f,%f,%f,%f, %f,%f,%f,%f", (PZMath.equal(quaternion.x, quaternion2.x, 0.01f) && PZMath.equal(quaternion.y, quaternion2.y, 0.01f) && PZMath.equal(quaternion.z, quaternion2.z, 0.01f) && PZMath.equal(quaternion.w, quaternion2.w, 0.01f)) || (PZMath.equal(quaternion.x, quaternion3.x, 0.01f) && PZMath.equal(quaternion.y, quaternion3.y, 0.01f) && PZMath.equal(quaternion.z, quaternion3.z, 0.01f) && PZMath.equal(quaternion.w, quaternion3.w, 0.01f)), quaternion.x, quaternion.y, quaternion.z, quaternion.w, quaternion2.x, quaternion2.y, quaternion2.z, quaternion2.w);
                }
                DebugLog.UnitTests.println("UnitTest_getRotationMatrix. Complete");
            }
        }
        
        private static final class transformQuaternion
        {
            public static void run() {
                DebugLog.UnitTests.println("UnitTest_transformQuaternion");
                DebugLog.UnitTests.println("roll, pitch, yaw, out.x, out.y, out.z, cout.x, cout.y, cout.z, result");
                final Quaternion quaternion = new Quaternion();
                final Vector3f vector3f = new Vector3f(0.0f, 0.0f, 0.0f);
                final Vector3f vector3f2 = new Vector3f(1.0f, 1.0f, 1.0f);
                final Vector3f vector3f3 = new Vector3f();
                final Vector3f vector3f4 = new Vector3f();
                final Matrix4f matrix4f = new Matrix4f();
                final Vector4f vector4f = new Vector4f();
                final Vector4f vector4f2 = new Vector4f();
                final Vector3f vector3f5 = new Vector3f(1.0f, 0.0f, 0.0f);
                final Vector3f vector3f6 = new Vector3f(0.0f, 1.0f, 0.0f);
                final Vector3f vector3f7 = new Vector3f(0.0f, 0.0f, 1.0f);
                runTest(0.0f, 0.0f, 90.0f, quaternion, vector3f3, vector3f4, matrix4f, vector4f, vector4f2, vector3f5, vector3f6, vector3f7);
                runTest(0.0f, 0.0f, 5.0f, quaternion, vector3f3, vector3f4, matrix4f, vector4f, vector4f2, vector3f5, vector3f6, vector3f7);
                for (int i = 0; i < 10; ++i) {
                    final float wrap = PZMath.wrap(i / 10.0f * 360.0f, -180.0f, 180.0f);
                    for (int j = 0; j < 10; ++j) {
                        final float wrap2 = PZMath.wrap(j / 10.0f * 360.0f, -180.0f, 180.0f);
                        for (int k = 0; k < 10; ++k) {
                            runTest(wrap, wrap2, PZMath.wrap(k / 10.0f * 360.0f, -180.0f, 180.0f), quaternion, vector3f3, vector3f4, matrix4f, vector4f, vector4f2, vector3f5, vector3f6, vector3f7);
                        }
                    }
                }
                DebugLog.UnitTests.println("UnitTest_transformQuaternion. Complete");
            }
            
            public static void runTest(final float f, final float f2, final float f3, final Quaternion quaternion, final Vector3f vector3f, final Vector3f vector3f2, final Matrix4f matrix4f, final Vector4f vector4f, final Vector4f vector4f2, final Vector3f vector3f3, final Vector3f vector3f4, final Vector3f vector3f5) {
                final Vector3f vector3f6 = new Vector3f(15.0f, 0.0f, 0.0f);
                matrix4f.setIdentity();
                matrix4f.translate(vector3f6);
                matrix4f.rotate(f * 0.017453292f, vector3f3);
                matrix4f.rotate(f2 * 0.017453292f, vector3f4);
                matrix4f.rotate(f3 * 0.017453292f, vector3f5);
                HelperFunctions.getRotation(matrix4f, quaternion);
                vector3f.set(1.0f, 0.0f, 0.0f);
                vector4f.set(vector3f.x, vector3f.y, vector3f.z, 1.0f);
                HelperFunctions.transform(matrix4f, vector4f, vector4f2);
                HelperFunctions.transform(quaternion, vector3f, vector3f2);
                vector3f2.x += vector3f6.x;
                vector3f2.y += vector3f6.y;
                vector3f2.z += vector3f6.z;
                DebugLog.UnitTests.printUnitTest("%f,%f,%f,%f,%f,%f,%f,%f,%f", PZMath.equal(vector3f2.x, vector4f2.x, 0.01f) && PZMath.equal(vector3f2.y, vector4f2.y, 0.01f) && PZMath.equal(vector3f2.z, vector4f2.z, 0.01f), f, f2, f3, vector3f2.x, vector3f2.y, vector3f2.z, vector4f2.x, vector4f2.y, vector4f2.z);
            }
        }
    }
}

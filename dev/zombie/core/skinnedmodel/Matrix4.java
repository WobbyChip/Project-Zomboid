// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel;

import org.lwjglx.BufferUtils;
import java.nio.FloatBuffer;

public class Matrix4
{
    private FloatBuffer matrix;
    public static Matrix4 Identity;
    private FloatBuffer direct;
    
    public Matrix4() {
        this.matrix = FloatBuffer.allocate(16);
    }
    
    public Matrix4(final float[] array) {
        this();
        this.put(array);
    }
    
    public Matrix4(final Matrix4 matrix4) {
        this();
        this.put(matrix4);
    }
    
    public Matrix4 clear() {
        for (int i = 0; i < 16; ++i) {
            this.matrix.put(i, 0.0f);
        }
        return this;
    }
    
    public Matrix4 clearToIdentity() {
        return this.clear().put(0, 1.0f).put(5, 1.0f).put(10, 1.0f).put(15, 1.0f);
    }
    
    public Matrix4 clearToOrtho(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        return this.clear().put(0, 2.0f / (n2 - n)).put(5, 2.0f / (n4 - n3)).put(10, -2.0f / (n6 - n5)).put(12, -(n2 + n) / (n2 - n)).put(13, -(n4 + n3) / (n4 - n3)).put(14, -(n6 + n5) / (n6 - n5)).put(15, 1.0f);
    }
    
    public Matrix4 clearToPerspective(final float n, final float n2, final float n3, final float n4, final float n5) {
        final float n6 = 1.0f / (float)Math.tan(n / 2.0f);
        return this.clear().put(0, n6 / (n2 / n3)).put(5, n6).put(10, (n5 + n4) / (n4 - n5)).put(14, 2.0f * n5 * n4 / (n4 - n5)).put(11, -1.0f);
    }
    
    public float get(final int n) {
        return this.matrix.get(n);
    }
    
    public Matrix4 put(final int n, final float n2) {
        this.matrix.put(n, n2);
        return this;
    }
    
    public Matrix4 put(final int n, final Vector3 vector3, final float n2) {
        this.put(n * 4 + 0, vector3.x());
        this.put(n * 4 + 1, vector3.y());
        this.put(n * 4 + 2, vector3.z());
        this.put(n * 4 + 3, n2);
        return this;
    }
    
    public Matrix4 put(final float[] src) {
        if (src.length < 16) {
            throw new IllegalArgumentException("float array must have at least 16 values.");
        }
        this.matrix.position(0);
        this.matrix.put(src, 0, 16);
        return this;
    }
    
    public Matrix4 put(final Matrix4 matrix4) {
        final FloatBuffer buffer = matrix4.getBuffer();
        while (buffer.hasRemaining()) {
            this.matrix.put(buffer.get());
        }
        return this;
    }
    
    public Matrix4 mult(final float[] array) {
        final float[] array2 = new float[16];
        for (int i = 0; i < 16; i += 4) {
            array2[i + 0] = this.get(0) * array[i] + this.get(4) * array[i + 1] + this.get(8) * array[i + 2] + this.get(12) * array[i + 3];
            array2[i + 1] = this.get(1) * array[i] + this.get(5) * array[i + 1] + this.get(9) * array[i + 2] + this.get(13) * array[i + 3];
            array2[i + 2] = this.get(2) * array[i] + this.get(6) * array[i + 1] + this.get(10) * array[i + 2] + this.get(14) * array[i + 3];
            array2[i + 3] = this.get(3) * array[i] + this.get(7) * array[i + 1] + this.get(11) * array[i + 2] + this.get(15) * array[i + 3];
        }
        this.put(array2);
        return this;
    }
    
    public Matrix4 mult(final Matrix4 matrix4) {
        final float[] array = new float[16];
        for (int i = 0; i < 16; i += 4) {
            array[i + 0] = this.get(0) * matrix4.get(i) + this.get(4) * matrix4.get(i + 1) + this.get(8) * matrix4.get(i + 2) + this.get(12) * matrix4.get(i + 3);
            array[i + 1] = this.get(1) * matrix4.get(i) + this.get(5) * matrix4.get(i + 1) + this.get(9) * matrix4.get(i + 2) + this.get(13) * matrix4.get(i + 3);
            array[i + 2] = this.get(2) * matrix4.get(i) + this.get(6) * matrix4.get(i + 1) + this.get(10) * matrix4.get(i + 2) + this.get(14) * matrix4.get(i + 3);
            array[i + 3] = this.get(3) * matrix4.get(i) + this.get(7) * matrix4.get(i + 1) + this.get(11) * matrix4.get(i + 2) + this.get(15) * matrix4.get(i + 3);
        }
        this.put(array);
        return this;
    }
    
    public Matrix4 transpose() {
        final float value = this.get(1);
        this.put(1, this.get(4));
        this.put(4, value);
        final float value2 = this.get(2);
        this.put(2, this.get(8));
        this.put(8, value2);
        final float value3 = this.get(3);
        this.put(3, this.get(12));
        this.put(12, value3);
        final float value4 = this.get(7);
        this.put(7, this.get(13));
        this.put(13, value4);
        final float value5 = this.get(11);
        this.put(11, this.get(14));
        this.put(14, value5);
        final float value6 = this.get(6);
        this.put(6, this.get(9));
        this.put(9, value6);
        return this;
    }
    
    public Matrix4 translate(final float n, final float n2, final float n3) {
        return this.mult(new float[] { 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, n, n2, n3, 1.0f });
    }
    
    public Matrix4 translate(final Vector3 vector3) {
        return this.translate(vector3.x(), vector3.y(), vector3.z());
    }
    
    public Matrix4 scale(final float n, final float n2, final float n3) {
        return this.mult(new float[] { n, 0.0f, 0.0f, 0.0f, 0.0f, n2, 0.0f, 0.0f, 0.0f, 0.0f, n3, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f });
    }
    
    public Matrix4 scale(final Vector3 vector3) {
        return this.scale(vector3.x(), vector3.y(), vector3.z());
    }
    
    public Matrix4 rotate(final float n, final float n2, final float n3, final float n4) {
        final float n5 = (float)Math.cos(n);
        final float n6 = (float)Math.sin(n);
        final float n7 = 1.0f - n5;
        final Vector3 normalize = new Vector3(n2, n3, n4).normalize();
        return this.mult(new float[] { normalize.x() * normalize.x() + (1.0f - normalize.x() * normalize.x()) * n5, normalize.y() * normalize.x() * n7 + normalize.z() * n6, normalize.z() * normalize.x() * n7 - normalize.y() * n6, 0.0f, normalize.x() * normalize.y() * n7 - normalize.z() * n6, normalize.y() * normalize.y() + (1.0f - normalize.y() * normalize.y()) * n5, normalize.z() * normalize.y() * n7 + normalize.x() * n6, 0.0f, normalize.x() * normalize.z() * n7 + normalize.y() * n6, normalize.y() * normalize.z() * n7 - normalize.x() * n6, normalize.z() * normalize.z() + (1.0f - normalize.z() * normalize.z()) * n5, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f });
    }
    
    public Matrix4 rotate(final float n, final Vector3 vector3) {
        return this.rotate(n, vector3.x(), vector3.y(), vector3.z());
    }
    
    public FloatBuffer getBuffer() {
        if (this.direct == null) {
            this.direct = BufferUtils.createFloatBuffer(16);
        }
        this.direct.clear();
        this.direct.put(this.matrix.position(16).flip());
        this.direct.flip();
        return this.direct;
    }
    
    static {
        (Matrix4.Identity = new Matrix4()).clearToIdentity();
    }
}

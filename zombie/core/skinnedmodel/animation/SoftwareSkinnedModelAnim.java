// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.animation;

import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import zombie.core.skinnedmodel.shader.Shader;
import zombie.core.skinnedmodel.model.UInt4;
import zombie.iso.Vector2;
import zombie.core.skinnedmodel.Vector3;
import org.lwjglx.BufferUtils;
import zombie.core.skinnedmodel.model.VertexPositionNormalTangentTextureSkin;
import java.util.ArrayList;
import zombie.core.skinnedmodel.HelperFunctions;
import zombie.core.skinnedmodel.model.SoftwareModelMesh;
import zombie.core.skinnedmodel.model.SkinningData;
import javax.vecmath.Point3f;
import org.lwjgl.util.vector.Vector3f;
import java.util.HashMap;
import java.nio.ByteBuffer;
import org.lwjgl.util.vector.Matrix4f;
import zombie.core.skinnedmodel.model.Vbo;
import zombie.core.skinnedmodel.model.VertexStride;
import zombie.core.skinnedmodel.model.VertexBufferObject;

public final class SoftwareSkinnedModelAnim
{
    private long animOffset;
    private final VertexBufferObject.BeginMode _beginMode;
    private final VertexStride[] _vertexStride;
    private final Vbo _handle;
    public static Matrix4f[] boneTransforms;
    public static Matrix4f[] worldTransforms;
    public static Matrix4f[] skinTransforms;
    ByteBuffer softwareSkinBufferInt;
    public HashMap<String, Integer> AnimationOffset;
    public HashMap<String, Integer> AnimationLength;
    public int vertCount;
    private int elementCount;
    static Matrix4f Identity;
    private static Vector3f tempVec3f;
    static javax.vecmath.Matrix4f m;
    static Point3f tempop;
    static javax.vecmath.Vector3f temponor;
    static Vector3f tot;
    static Vector3f totn;
    static Vector3f vec;
    
    public void UpdateWorldTransforms(final Matrix4f matrix4f, final float n, final SkinningData skinningData) {
        SoftwareSkinnedModelAnim.Identity.setIdentity();
        SoftwareSkinnedModelAnim.tempVec3f.set(0.0f, 1.0f, 0.0f);
        Matrix4f.mul(SoftwareSkinnedModelAnim.boneTransforms[0], SoftwareSkinnedModelAnim.Identity, SoftwareSkinnedModelAnim.worldTransforms[0]);
        for (int i = 1; i < SoftwareSkinnedModelAnim.worldTransforms.length; ++i) {
            Matrix4f.mul(SoftwareSkinnedModelAnim.boneTransforms[i], SoftwareSkinnedModelAnim.worldTransforms[skinningData.SkeletonHierarchy.get(i)], SoftwareSkinnedModelAnim.worldTransforms[i]);
        }
    }
    
    public void UpdateSkinTransforms(final SkinningData skinningData) {
        for (int i = 0; i < SoftwareSkinnedModelAnim.worldTransforms.length; ++i) {
            Matrix4f.mul((Matrix4f)skinningData.BoneOffset.get(i), SoftwareSkinnedModelAnim.worldTransforms[i], SoftwareSkinnedModelAnim.skinTransforms[i]);
        }
    }
    
    public SoftwareSkinnedModelAnim(final StaticAnimation[] array, final SoftwareModelMesh softwareModelMesh, final SkinningData skinningData) {
        this.AnimationOffset = new HashMap<String, Integer>();
        this.AnimationLength = new HashMap<String, Integer>();
        this.vertCount = 0;
        this.vertCount = softwareModelMesh.verticesUnskinned.length;
        this.elementCount = softwareModelMesh.indicesUnskinned.length;
        final Vbo handle = new Vbo();
        if (SoftwareSkinnedModelAnim.boneTransforms == null) {
            SoftwareSkinnedModelAnim.boneTransforms = new Matrix4f[skinningData.BindPose.size()];
            SoftwareSkinnedModelAnim.worldTransforms = new Matrix4f[skinningData.BindPose.size()];
            SoftwareSkinnedModelAnim.skinTransforms = new Matrix4f[skinningData.BindPose.size()];
            for (int i = 0; i < skinningData.BindPose.size(); ++i) {
                (SoftwareSkinnedModelAnim.boneTransforms[i] = HelperFunctions.getMatrix()).setIdentity();
                (SoftwareSkinnedModelAnim.worldTransforms[i] = HelperFunctions.getMatrix()).setIdentity();
                (SoftwareSkinnedModelAnim.skinTransforms[i] = HelperFunctions.getMatrix()).setIdentity();
            }
        }
        int j = 0;
        final ArrayList<VertexPositionNormalTangentTextureSkin> list = new ArrayList<VertexPositionNormalTangentTextureSkin>();
        final ArrayList<Integer> list2 = new ArrayList<Integer>();
        int n = 0;
        for (int k = 0; k < array.length; ++k) {
            final StaticAnimation staticAnimation = array[k];
            this.AnimationOffset.put(staticAnimation.Clip.Name, j);
            this.AnimationLength.put(staticAnimation.Clip.Name, staticAnimation.Matrices.length);
            for (int l = 0; l < staticAnimation.Matrices.length; ++l) {
                final int[] indicesUnskinned = softwareModelMesh.indicesUnskinned;
                for (int n2 = 0; n2 < indicesUnskinned.length; ++n2) {
                    list2.add(indicesUnskinned[n2] + n);
                }
                n += this.vertCount;
                SoftwareSkinnedModelAnim.boneTransforms = staticAnimation.Matrices[l];
                this.UpdateWorldTransforms(null, 0.0f, skinningData);
                this.UpdateSkinTransforms(skinningData);
                for (int n3 = 0; n3 < softwareModelMesh.verticesUnskinned.length; ++n3) {
                    list.add(this.updateSkin(SoftwareSkinnedModelAnim.skinTransforms, softwareModelMesh.verticesUnskinned, n3));
                }
                j += softwareModelMesh.indicesUnskinned.length;
            }
        }
        this._vertexStride = new VertexStride[4];
        for (int n4 = 0; n4 < this._vertexStride.length; ++n4) {
            this._vertexStride[n4] = new VertexStride();
        }
        this._vertexStride[0].Type = VertexBufferObject.VertexType.VertexArray;
        this._vertexStride[0].Offset = 0;
        this._vertexStride[1].Type = VertexBufferObject.VertexType.NormalArray;
        this._vertexStride[1].Offset = 12;
        this._vertexStride[2].Type = VertexBufferObject.VertexType.ColorArray;
        this._vertexStride[2].Offset = 24;
        this._vertexStride[3].Type = VertexBufferObject.VertexType.TextureCoordArray;
        this._vertexStride[3].Offset = 28;
        this._beginMode = VertexBufferObject.BeginMode.Triangles;
        handle.VboID = VertexBufferObject.funcs.glGenBuffers();
        final ByteBuffer byteBuffer = BufferUtils.createByteBuffer(list.size() * 36);
        final ByteBuffer byteBuffer2 = BufferUtils.createByteBuffer(list2.size() * 4);
        for (int index = 0; index < list.size(); ++index) {
            final VertexPositionNormalTangentTextureSkin vertexPositionNormalTangentTextureSkin = list.get(index);
            byteBuffer.putFloat(vertexPositionNormalTangentTextureSkin.Position.x());
            byteBuffer.putFloat(vertexPositionNormalTangentTextureSkin.Position.y());
            byteBuffer.putFloat(vertexPositionNormalTangentTextureSkin.Position.z());
            byteBuffer.putFloat(vertexPositionNormalTangentTextureSkin.Normal.x());
            byteBuffer.putFloat(vertexPositionNormalTangentTextureSkin.Normal.y());
            byteBuffer.putFloat(vertexPositionNormalTangentTextureSkin.Normal.z());
            byteBuffer.putInt(-1);
            byteBuffer.putFloat(vertexPositionNormalTangentTextureSkin.TextureCoordinates.x);
            byteBuffer.putFloat(vertexPositionNormalTangentTextureSkin.TextureCoordinates.y);
        }
        for (int index2 = 0; index2 < list2.size(); ++index2) {
            byteBuffer2.putInt(list2.get(index2));
        }
        byteBuffer2.flip();
        byteBuffer.flip();
        final ByteBuffer byteBuffer3 = byteBuffer2;
        handle.VertexStride = 36;
        handle.NumElements = list2.size();
        handle.FaceDataOnly = false;
        VertexBufferObject.funcs.glBindBuffer(VertexBufferObject.funcs.GL_ARRAY_BUFFER(), handle.VboID);
        VertexBufferObject.funcs.glBufferData(VertexBufferObject.funcs.GL_ARRAY_BUFFER(), byteBuffer, VertexBufferObject.funcs.GL_STATIC_DRAW());
        VertexBufferObject.funcs.glGetBufferParameter(VertexBufferObject.funcs.GL_ARRAY_BUFFER(), VertexBufferObject.funcs.GL_BUFFER_SIZE(), handle.b);
        handle.EboID = VertexBufferObject.funcs.glGenBuffers();
        VertexBufferObject.funcs.glBindBuffer(VertexBufferObject.funcs.GL_ELEMENT_ARRAY_BUFFER(), handle.EboID);
        VertexBufferObject.funcs.glBufferData(VertexBufferObject.funcs.GL_ELEMENT_ARRAY_BUFFER(), byteBuffer3, VertexBufferObject.funcs.GL_STATIC_DRAW());
        this._handle = handle;
    }
    
    public VertexPositionNormalTangentTextureSkin updateSkin(final Matrix4f[] array, final VertexPositionNormalTangentTextureSkin[] array2, final int n) {
        SoftwareSkinnedModelAnim.tot.set(0.0f, 0.0f, 0.0f);
        SoftwareSkinnedModelAnim.totn.set(0.0f, 0.0f, 0.0f);
        final VertexPositionNormalTangentTextureSkin vertexPositionNormalTangentTextureSkin = array2[n];
        final Matrix4f matrix = HelperFunctions.getMatrix();
        final Matrix4f matrix2 = HelperFunctions.getMatrix();
        matrix.setIdentity();
        final Matrix4f matrix3 = HelperFunctions.getMatrix();
        final UInt4 blendIndices = vertexPositionNormalTangentTextureSkin.BlendIndices;
        if (vertexPositionNormalTangentTextureSkin.BlendWeights.x > 0.0f) {
            matrix2.load(array[blendIndices.X]);
            set(matrix2, SoftwareSkinnedModelAnim.m);
            final Point3f tempop = SoftwareSkinnedModelAnim.tempop;
            SoftwareSkinnedModelAnim.tempop.set(vertexPositionNormalTangentTextureSkin.Position.x(), vertexPositionNormalTangentTextureSkin.Position.y(), vertexPositionNormalTangentTextureSkin.Position.z());
            SoftwareSkinnedModelAnim.m.transform(tempop);
            final Point3f point3f = tempop;
            point3f.x *= vertexPositionNormalTangentTextureSkin.BlendWeights.x;
            final Point3f point3f2 = tempop;
            point3f2.y *= vertexPositionNormalTangentTextureSkin.BlendWeights.x;
            final Point3f point3f3 = tempop;
            point3f3.z *= vertexPositionNormalTangentTextureSkin.BlendWeights.x;
            final Vector3f tot = SoftwareSkinnedModelAnim.tot;
            tot.x += tempop.x;
            final Vector3f tot2 = SoftwareSkinnedModelAnim.tot;
            tot2.y += tempop.y;
            final Vector3f tot3 = SoftwareSkinnedModelAnim.tot;
            tot3.z += tempop.z;
            final javax.vecmath.Vector3f temponor = SoftwareSkinnedModelAnim.temponor;
            SoftwareSkinnedModelAnim.temponor.set(vertexPositionNormalTangentTextureSkin.Normal.x(), vertexPositionNormalTangentTextureSkin.Normal.y(), vertexPositionNormalTangentTextureSkin.Normal.z());
            SoftwareSkinnedModelAnim.m.transform(temponor);
            final javax.vecmath.Vector3f vector3f = temponor;
            vector3f.x *= vertexPositionNormalTangentTextureSkin.BlendWeights.x;
            final javax.vecmath.Vector3f vector3f2 = temponor;
            vector3f2.y *= vertexPositionNormalTangentTextureSkin.BlendWeights.x;
            final javax.vecmath.Vector3f vector3f3 = temponor;
            vector3f3.z *= vertexPositionNormalTangentTextureSkin.BlendWeights.x;
            final Vector3f totn = SoftwareSkinnedModelAnim.totn;
            totn.x += temponor.x;
            final Vector3f totn2 = SoftwareSkinnedModelAnim.totn;
            totn2.y += temponor.y;
            final Vector3f totn3 = SoftwareSkinnedModelAnim.totn;
            totn3.z += temponor.z;
        }
        if (vertexPositionNormalTangentTextureSkin.BlendWeights.y > 0.0f) {
            matrix2.load(array[blendIndices.Y]);
            set(matrix2, SoftwareSkinnedModelAnim.m);
            final Point3f tempop2 = SoftwareSkinnedModelAnim.tempop;
            SoftwareSkinnedModelAnim.tempop.set(vertexPositionNormalTangentTextureSkin.Position.x(), vertexPositionNormalTangentTextureSkin.Position.y(), vertexPositionNormalTangentTextureSkin.Position.z());
            SoftwareSkinnedModelAnim.m.transform(tempop2);
            final Point3f point3f4 = tempop2;
            point3f4.x *= vertexPositionNormalTangentTextureSkin.BlendWeights.y;
            final Point3f point3f5 = tempop2;
            point3f5.y *= vertexPositionNormalTangentTextureSkin.BlendWeights.y;
            final Point3f point3f6 = tempop2;
            point3f6.z *= vertexPositionNormalTangentTextureSkin.BlendWeights.y;
            final Vector3f tot4 = SoftwareSkinnedModelAnim.tot;
            tot4.x += tempop2.x;
            final Vector3f tot5 = SoftwareSkinnedModelAnim.tot;
            tot5.y += tempop2.y;
            final Vector3f tot6 = SoftwareSkinnedModelAnim.tot;
            tot6.z += tempop2.z;
            final javax.vecmath.Vector3f temponor2 = SoftwareSkinnedModelAnim.temponor;
            SoftwareSkinnedModelAnim.temponor.set(vertexPositionNormalTangentTextureSkin.Normal.x(), vertexPositionNormalTangentTextureSkin.Normal.y(), vertexPositionNormalTangentTextureSkin.Normal.z());
            SoftwareSkinnedModelAnim.m.transform(temponor2);
            final javax.vecmath.Vector3f vector3f4 = temponor2;
            vector3f4.x *= vertexPositionNormalTangentTextureSkin.BlendWeights.y;
            final javax.vecmath.Vector3f vector3f5 = temponor2;
            vector3f5.y *= vertexPositionNormalTangentTextureSkin.BlendWeights.y;
            final javax.vecmath.Vector3f vector3f6 = temponor2;
            vector3f6.z *= vertexPositionNormalTangentTextureSkin.BlendWeights.y;
            final Vector3f totn4 = SoftwareSkinnedModelAnim.totn;
            totn4.x += temponor2.x;
            final Vector3f totn5 = SoftwareSkinnedModelAnim.totn;
            totn5.y += temponor2.y;
            final Vector3f totn6 = SoftwareSkinnedModelAnim.totn;
            totn6.z += temponor2.z;
        }
        if (vertexPositionNormalTangentTextureSkin.BlendWeights.z > 0.0f) {
            matrix2.load(array[blendIndices.Z]);
            set(matrix2, SoftwareSkinnedModelAnim.m);
            final Point3f tempop3 = SoftwareSkinnedModelAnim.tempop;
            SoftwareSkinnedModelAnim.tempop.set(vertexPositionNormalTangentTextureSkin.Position.x(), vertexPositionNormalTangentTextureSkin.Position.y(), vertexPositionNormalTangentTextureSkin.Position.z());
            SoftwareSkinnedModelAnim.m.transform(tempop3);
            final Point3f point3f7 = tempop3;
            point3f7.x *= vertexPositionNormalTangentTextureSkin.BlendWeights.z;
            final Point3f point3f8 = tempop3;
            point3f8.y *= vertexPositionNormalTangentTextureSkin.BlendWeights.z;
            final Point3f point3f9 = tempop3;
            point3f9.z *= vertexPositionNormalTangentTextureSkin.BlendWeights.z;
            final Vector3f tot7 = SoftwareSkinnedModelAnim.tot;
            tot7.x += tempop3.x;
            final Vector3f tot8 = SoftwareSkinnedModelAnim.tot;
            tot8.y += tempop3.y;
            final Vector3f tot9 = SoftwareSkinnedModelAnim.tot;
            tot9.z += tempop3.z;
            final javax.vecmath.Vector3f temponor3 = SoftwareSkinnedModelAnim.temponor;
            SoftwareSkinnedModelAnim.temponor.set(vertexPositionNormalTangentTextureSkin.Normal.x(), vertexPositionNormalTangentTextureSkin.Normal.y(), vertexPositionNormalTangentTextureSkin.Normal.z());
            SoftwareSkinnedModelAnim.m.transform(temponor3);
            final javax.vecmath.Vector3f vector3f7 = temponor3;
            vector3f7.x *= vertexPositionNormalTangentTextureSkin.BlendWeights.z;
            final javax.vecmath.Vector3f vector3f8 = temponor3;
            vector3f8.y *= vertexPositionNormalTangentTextureSkin.BlendWeights.z;
            final javax.vecmath.Vector3f vector3f9 = temponor3;
            vector3f9.z *= vertexPositionNormalTangentTextureSkin.BlendWeights.z;
            final Vector3f totn7 = SoftwareSkinnedModelAnim.totn;
            totn7.x += temponor3.x;
            final Vector3f totn8 = SoftwareSkinnedModelAnim.totn;
            totn8.y += temponor3.y;
            final Vector3f totn9 = SoftwareSkinnedModelAnim.totn;
            totn9.z += temponor3.z;
        }
        if (vertexPositionNormalTangentTextureSkin.BlendWeights.w > 0.0f) {
            matrix2.load(array[blendIndices.W]);
            set(matrix2, SoftwareSkinnedModelAnim.m);
            final Point3f tempop4 = SoftwareSkinnedModelAnim.tempop;
            SoftwareSkinnedModelAnim.tempop.set(vertexPositionNormalTangentTextureSkin.Position.x(), vertexPositionNormalTangentTextureSkin.Position.y(), vertexPositionNormalTangentTextureSkin.Position.z());
            SoftwareSkinnedModelAnim.m.transform(tempop4);
            final Point3f point3f10 = tempop4;
            point3f10.x *= vertexPositionNormalTangentTextureSkin.BlendWeights.w;
            final Point3f point3f11 = tempop4;
            point3f11.y *= vertexPositionNormalTangentTextureSkin.BlendWeights.w;
            final Point3f point3f12 = tempop4;
            point3f12.z *= vertexPositionNormalTangentTextureSkin.BlendWeights.w;
            final Vector3f tot10 = SoftwareSkinnedModelAnim.tot;
            tot10.x += tempop4.x;
            final Vector3f tot11 = SoftwareSkinnedModelAnim.tot;
            tot11.y += tempop4.y;
            final Vector3f tot12 = SoftwareSkinnedModelAnim.tot;
            tot12.z += tempop4.z;
            final javax.vecmath.Vector3f temponor4 = SoftwareSkinnedModelAnim.temponor;
            SoftwareSkinnedModelAnim.temponor.set(vertexPositionNormalTangentTextureSkin.Normal.x(), vertexPositionNormalTangentTextureSkin.Normal.y(), vertexPositionNormalTangentTextureSkin.Normal.z());
            SoftwareSkinnedModelAnim.m.transform(temponor4);
            final javax.vecmath.Vector3f vector3f10 = temponor4;
            vector3f10.x *= vertexPositionNormalTangentTextureSkin.BlendWeights.w;
            final javax.vecmath.Vector3f vector3f11 = temponor4;
            vector3f11.y *= vertexPositionNormalTangentTextureSkin.BlendWeights.w;
            final javax.vecmath.Vector3f vector3f12 = temponor4;
            vector3f12.z *= vertexPositionNormalTangentTextureSkin.BlendWeights.w;
            final Vector3f totn10 = SoftwareSkinnedModelAnim.totn;
            totn10.x += temponor4.x;
            final Vector3f totn11 = SoftwareSkinnedModelAnim.totn;
            totn11.y += temponor4.y;
            final Vector3f totn12 = SoftwareSkinnedModelAnim.totn;
            totn12.z += temponor4.z;
        }
        matrix3.setIdentity();
        SoftwareSkinnedModelAnim.vec.x = SoftwareSkinnedModelAnim.tot.x;
        SoftwareSkinnedModelAnim.vec.y = SoftwareSkinnedModelAnim.tot.y;
        SoftwareSkinnedModelAnim.vec.z = SoftwareSkinnedModelAnim.tot.z;
        final VertexPositionNormalTangentTextureSkin vertexPositionNormalTangentTextureSkin2 = new VertexPositionNormalTangentTextureSkin();
        (vertexPositionNormalTangentTextureSkin2.Position = new Vector3()).set(SoftwareSkinnedModelAnim.vec.getX(), SoftwareSkinnedModelAnim.vec.getY(), SoftwareSkinnedModelAnim.vec.getZ());
        final javax.vecmath.Vector3f temponor5 = SoftwareSkinnedModelAnim.temponor;
        temponor5.x = SoftwareSkinnedModelAnim.totn.x;
        temponor5.y = SoftwareSkinnedModelAnim.totn.y;
        temponor5.z = SoftwareSkinnedModelAnim.totn.z;
        temponor5.normalize();
        (vertexPositionNormalTangentTextureSkin2.Normal = new Vector3()).set(temponor5.getX(), temponor5.getY(), temponor5.getZ());
        vertexPositionNormalTangentTextureSkin2.TextureCoordinates = new Vector2();
        vertexPositionNormalTangentTextureSkin2.TextureCoordinates.x = vertexPositionNormalTangentTextureSkin.TextureCoordinates.x;
        vertexPositionNormalTangentTextureSkin2.TextureCoordinates.y = vertexPositionNormalTangentTextureSkin.TextureCoordinates.y;
        HelperFunctions.returnMatrix(matrix);
        HelperFunctions.returnMatrix(matrix3);
        HelperFunctions.returnMatrix(matrix2);
        return vertexPositionNormalTangentTextureSkin2;
    }
    
    public void Draw(final int n, final int n2, final String s) {
        this.Draw(this._handle, this._vertexStride, this._beginMode, null, n, n2, s);
    }
    
    static void set(final Matrix4f matrix4f, final javax.vecmath.Matrix4f matrix4f2) {
        matrix4f2.m00 = matrix4f.m00;
        matrix4f2.m01 = matrix4f.m01;
        matrix4f2.m02 = matrix4f.m02;
        matrix4f2.m03 = matrix4f.m03;
        matrix4f2.m10 = matrix4f.m10;
        matrix4f2.m11 = matrix4f.m11;
        matrix4f2.m12 = matrix4f.m12;
        matrix4f2.m13 = matrix4f.m13;
        matrix4f2.m20 = matrix4f.m20;
        matrix4f2.m21 = matrix4f.m21;
        matrix4f2.m22 = matrix4f.m22;
        matrix4f2.m23 = matrix4f.m23;
        matrix4f2.m30 = matrix4f.m30;
        matrix4f2.m31 = matrix4f.m31;
        matrix4f2.m32 = matrix4f.m32;
        matrix4f2.m33 = matrix4f.m33;
    }
    
    private void Draw(final Vbo vbo, final VertexStride[] array, final VertexBufferObject.BeginMode beginMode, final Shader shader, final int n, final int n2, final String s) {
        this.animOffset = n2 + this.elementCount * n;
        final int elementCount = this.elementCount;
        int n3 = 33984;
        if (!vbo.FaceDataOnly) {
            VertexBufferObject.funcs.glBindBuffer(VertexBufferObject.funcs.GL_ARRAY_BUFFER(), vbo.VboID);
            for (int i = array.length - 1; i >= 0; --i) {
                switch (array[i].Type) {
                    case VertexArray: {
                        GL20.glVertexPointer(3, 5126, vbo.VertexStride, (long)array[i].Offset);
                        GL20.glEnableClientState(32884);
                        break;
                    }
                    case NormalArray: {
                        GL20.glNormalPointer(5126, vbo.VertexStride, (long)array[i].Offset);
                        GL20.glEnableClientState(32885);
                        break;
                    }
                    case ColorArray: {
                        GL20.glColorPointer(3, 5121, vbo.VertexStride, (long)array[i].Offset);
                        GL20.glEnableClientState(32886);
                        break;
                    }
                    case TextureCoordArray: {
                        GL13.glActiveTexture(n3);
                        GL13.glClientActiveTexture(n3);
                        GL20.glTexCoordPointer(2, 5126, vbo.VertexStride, (long)array[i].Offset);
                        ++n3;
                        GL20.glEnableClientState(32888);
                        break;
                    }
                    case TangentArray: {
                        GL20.glNormalPointer(5126, vbo.VertexStride, (long)array[i].Offset);
                        break;
                    }
                    case BlendWeightArray: {
                        final int glGetAttribLocation = GL20.glGetAttribLocation(shader.getID(), (CharSequence)"boneWeights");
                        GL20.glVertexAttribPointer(glGetAttribLocation, 4, 5126, false, vbo.VertexStride, (long)array[i].Offset);
                        GL20.glEnableVertexAttribArray(glGetAttribLocation);
                        break;
                    }
                    case BlendIndexArray: {
                        final int glGetAttribLocation2 = GL20.glGetAttribLocation(shader.getID(), (CharSequence)"boneIndices");
                        GL20.glVertexAttribPointer(glGetAttribLocation2, 4, 5126, false, vbo.VertexStride, (long)array[i].Offset);
                        GL20.glEnableVertexAttribArray(glGetAttribLocation2);
                        break;
                    }
                }
            }
        }
        VertexBufferObject.funcs.glBindBuffer(VertexBufferObject.funcs.GL_ELEMENT_ARRAY_BUFFER(), vbo.EboID);
        GL20.glDrawElements(4, elementCount, 5125, this.animOffset * 4L);
        GL20.glDisableClientState(32885);
    }
    
    static {
        SoftwareSkinnedModelAnim.Identity = new Matrix4f();
        SoftwareSkinnedModelAnim.tempVec3f = new Vector3f();
        SoftwareSkinnedModelAnim.m = new javax.vecmath.Matrix4f();
        SoftwareSkinnedModelAnim.tempop = new Point3f();
        SoftwareSkinnedModelAnim.temponor = new javax.vecmath.Vector3f();
        SoftwareSkinnedModelAnim.tot = new Vector3f();
        SoftwareSkinnedModelAnim.totn = new Vector3f();
        SoftwareSkinnedModelAnim.vec = new Vector3f();
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.model;

import org.lwjgl.util.vector.Matrix4f;
import zombie.core.opengl.RenderThread;
import java.io.IOException;
import java.util.Iterator;
import org.lwjgl.util.vector.Quaternion;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import zombie.core.skinnedmodel.animation.StaticAnimation;
import zombie.core.skinnedmodel.ModelManager;
import java.util.List;
import zombie.core.skinnedmodel.animation.AnimationClip;
import org.lwjgl.util.vector.ReadableVector3f;
import org.lwjgl.util.vector.Vector3f;
import zombie.core.skinnedmodel.animation.Keyframe;
import java.util.ArrayList;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.function.Supplier;
import zombie.util.SharedStrings;

public final class ModelLoader
{
    public static final ModelLoader instance;
    private final ThreadLocal<SharedStrings> sharedStrings;
    
    public ModelLoader() {
        this.sharedStrings = ThreadLocal.withInitial((Supplier<? extends SharedStrings>)SharedStrings::new);
    }
    
    protected ModelTxt loadTxt(final String fileName, final boolean bStatic, final boolean bReverse, final SkinningData skinningData) throws IOException {
        final ModelTxt modelTxt = new ModelTxt();
        modelTxt.bStatic = bStatic;
        modelTxt.bReverse = bReverse;
        final VertexBufferObject.VertexFormat vertexFormat = new VertexBufferObject.VertexFormat(bStatic ? 4 : 6);
        vertexFormat.setElement(0, VertexBufferObject.VertexType.VertexArray, 12);
        vertexFormat.setElement(1, VertexBufferObject.VertexType.NormalArray, 12);
        vertexFormat.setElement(2, VertexBufferObject.VertexType.TangentArray, 12);
        vertexFormat.setElement(3, VertexBufferObject.VertexType.TextureCoordArray, 8);
        if (!bStatic) {
            vertexFormat.setElement(4, VertexBufferObject.VertexType.BlendWeightArray, 16);
            vertexFormat.setElement(5, VertexBufferObject.VertexType.BlendIndexArray, 16);
        }
        vertexFormat.calculate();
        final FileReader in = new FileReader(fileName);
        try {
            final BufferedReader bufferedReader = new BufferedReader(in);
            try {
                final SharedStrings sharedStrings = this.sharedStrings.get();
                LoadMode loadMode = LoadMode.Version;
                int n2 = 0;
                int int1 = 0;
                int int2 = 0;
                int int3 = 0;
                boolean b = false;
                String s2;
                while ((s2 = bufferedReader.readLine()) != null) {
                    if (s2.indexOf(35) == 0) {
                        continue;
                    }
                    if (s2.contains("Tangent")) {
                        if (bStatic) {
                            n2 += 2;
                        }
                        b = true;
                    }
                    if (n2 > 0) {
                        --n2;
                    }
                    else {
                        switch (loadMode) {
                            case Version: {
                                loadMode = LoadMode.ModelName;
                                continue;
                            }
                            case ModelName: {
                                loadMode = LoadMode.VertexStrideElementCount;
                                continue;
                            }
                            case VertexStrideElementCount: {
                                loadMode = LoadMode.VertexCount;
                                if (bStatic) {
                                    n2 = 7;
                                    continue;
                                }
                                n2 = 13;
                                continue;
                            }
                            case VertexCount: {
                                int1 = Integer.parseInt(s2);
                                loadMode = LoadMode.VertexBuffer;
                                modelTxt.vertices = new VertexBufferObject.VertexArray(vertexFormat, int1);
                                continue;
                            }
                            case VertexBuffer: {
                                for (int i = 0; i < int1; ++i) {
                                    final String[] split = s2.split(",");
                                    final float float1 = Float.parseFloat(split[0].trim());
                                    final float float2 = Float.parseFloat(split[1].trim());
                                    final float float3 = Float.parseFloat(split[2].trim());
                                    final String[] split2 = bufferedReader.readLine().split(",");
                                    final float float4 = Float.parseFloat(split2[0].trim());
                                    final float float5 = Float.parseFloat(split2[1].trim());
                                    final float float6 = Float.parseFloat(split2[2].trim());
                                    float float7 = 0.0f;
                                    float float8 = 0.0f;
                                    float float9 = 0.0f;
                                    if (b) {
                                        final String[] split3 = bufferedReader.readLine().split(",");
                                        float7 = Float.parseFloat(split3[0].trim());
                                        float8 = Float.parseFloat(split3[1].trim());
                                        float9 = Float.parseFloat(split3[2].trim());
                                    }
                                    final String[] split4 = bufferedReader.readLine().split(",");
                                    final float float10 = Float.parseFloat(split4[0].trim());
                                    final float float11 = Float.parseFloat(split4[1].trim());
                                    float float12 = 0.0f;
                                    float float13 = 0.0f;
                                    float float14 = 0.0f;
                                    float float15 = 0.0f;
                                    int int4 = 0;
                                    int int5 = 0;
                                    int int6 = 0;
                                    int int7 = 0;
                                    if (!bStatic) {
                                        final String[] split5 = bufferedReader.readLine().split(",");
                                        float12 = Float.parseFloat(split5[0].trim());
                                        float13 = Float.parseFloat(split5[1].trim());
                                        float14 = Float.parseFloat(split5[2].trim());
                                        float15 = Float.parseFloat(split5[3].trim());
                                        final String[] split6 = bufferedReader.readLine().split(",");
                                        int4 = Integer.parseInt(split6[0].trim());
                                        int5 = Integer.parseInt(split6[1].trim());
                                        int6 = Integer.parseInt(split6[2].trim());
                                        int7 = Integer.parseInt(split6[3].trim());
                                    }
                                    s2 = bufferedReader.readLine();
                                    modelTxt.vertices.setElement(i, 0, float1, float2, float3);
                                    modelTxt.vertices.setElement(i, 1, float4, float5, float6);
                                    modelTxt.vertices.setElement(i, 2, float7, float8, float9);
                                    modelTxt.vertices.setElement(i, 3, float10, float11);
                                    if (!bStatic) {
                                        modelTxt.vertices.setElement(i, 4, float12, float13, float14, float15);
                                        modelTxt.vertices.setElement(i, 5, (float)int4, (float)int5, (float)int6, (float)int7);
                                    }
                                }
                                loadMode = LoadMode.NumberOfFaces;
                                continue;
                            }
                            case NumberOfFaces: {
                                int2 = Integer.parseInt(s2);
                                modelTxt.elements = new int[int2 * 3];
                                loadMode = LoadMode.FaceData;
                                continue;
                            }
                            case FaceData: {
                                for (int j = 0; j < int2; ++j) {
                                    final String[] split7 = s2.split(",");
                                    final int int8 = Integer.parseInt(split7[0].trim());
                                    final int int9 = Integer.parseInt(split7[1].trim());
                                    final int int10 = Integer.parseInt(split7[2].trim());
                                    if (bReverse) {
                                        modelTxt.elements[j * 3 + 2] = int8;
                                        modelTxt.elements[j * 3 + 1] = int9;
                                        modelTxt.elements[j * 3 + 0] = int10;
                                    }
                                    else {
                                        modelTxt.elements[j * 3 + 0] = int8;
                                        modelTxt.elements[j * 3 + 1] = int9;
                                        modelTxt.elements[j * 3 + 2] = int10;
                                    }
                                    s2 = bufferedReader.readLine();
                                }
                                loadMode = LoadMode.NumberOfBones;
                                continue;
                            }
                            case NumberOfBones: {
                                int3 = Integer.parseInt(s2);
                                loadMode = LoadMode.SkeletonHierarchy;
                                continue;
                            }
                            case SkeletonHierarchy: {
                                for (int k = 0; k < int3; ++k) {
                                    final int int11 = Integer.parseInt(s2);
                                    final int int12 = Integer.parseInt(bufferedReader.readLine());
                                    final String value = sharedStrings.get(bufferedReader.readLine());
                                    s2 = bufferedReader.readLine();
                                    modelTxt.SkeletonHierarchy.add(int12);
                                    modelTxt.boneIndices.put(value, int11);
                                }
                                loadMode = LoadMode.BindPose;
                                continue;
                            }
                            case BindPose: {
                                for (int l = 0; l < int3; ++l) {
                                    modelTxt.bindPose.add(l, this.getMatrix(bufferedReader.readLine(), bufferedReader.readLine(), bufferedReader.readLine(), bufferedReader.readLine()));
                                    bufferedReader.readLine();
                                }
                                loadMode = LoadMode.InvBindPose;
                                continue;
                            }
                            case InvBindPose: {
                                for (int index = 0; index < int3; ++index) {
                                    modelTxt.invBindPose.add(index, this.getMatrix(bufferedReader.readLine(), bufferedReader.readLine(), bufferedReader.readLine(), bufferedReader.readLine()));
                                    bufferedReader.readLine();
                                }
                                loadMode = LoadMode.SkinOffsetMatrices;
                                continue;
                            }
                            case SkinOffsetMatrices: {
                                for (int index2 = 0; index2 < int3; ++index2) {
                                    modelTxt.skinOffsetMatrices.add(index2, this.getMatrix(bufferedReader.readLine(), bufferedReader.readLine(), bufferedReader.readLine(), bufferedReader.readLine()));
                                    bufferedReader.readLine();
                                }
                                loadMode = LoadMode.NumberOfAnims;
                                continue;
                            }
                            case NumberOfAnims: {
                                Integer.parseInt(s2);
                                loadMode = LoadMode.Anim;
                                continue;
                            }
                            case Anim: {
                                final ArrayList<Keyframe> list = new ArrayList<Keyframe>();
                                final String key = s2;
                                final float float16 = Float.parseFloat(bufferedReader.readLine());
                                final int int13 = Integer.parseInt(bufferedReader.readLine());
                                String s3 = bufferedReader.readLine();
                                for (int n3 = 0; n3 < int13; ++n3) {
                                    final Keyframe e = new Keyframe();
                                    final int int14 = Integer.parseInt(s3);
                                    final String value2 = sharedStrings.get(bufferedReader.readLine());
                                    final float float17 = Float.parseFloat(bufferedReader.readLine());
                                    s3 = bufferedReader.readLine();
                                    final String line = bufferedReader.readLine();
                                    final Vector3f vector = this.getVector(s3);
                                    final Quaternion quaternion = this.getQuaternion(line);
                                    if (n3 < int13 - 1) {
                                        s3 = bufferedReader.readLine();
                                    }
                                    e.Bone = int14;
                                    e.BoneName = value2;
                                    e.Time = float17;
                                    e.Rotation = quaternion;
                                    e.Position = new Vector3f((ReadableVector3f)vector);
                                    list.add(e);
                                }
                                final AnimationClip value3 = new AnimationClip(float16, list, key, false);
                                list.clear();
                                if (ModelManager.instance.bCreateSoftwareMeshes) {
                                    value3.staticClip = new StaticAnimation(value3);
                                }
                                modelTxt.clips.put(key, value3);
                                continue;
                            }
                        }
                    }
                }
                if (!bStatic && skinningData != null) {
                    try {
                        final int[] array = new int[modelTxt.boneIndices.size()];
                        final ArrayList<Integer> skeletonHierarchy = modelTxt.SkeletonHierarchy;
                        final HashMap<String, Integer> boneIndices = modelTxt.boneIndices;
                        final HashMap<String, Integer> boneIndices2 = new HashMap<String, Integer>(skinningData.BoneIndices);
                        final ArrayList<Integer> skeletonHierarchy2 = new ArrayList<Integer>(skinningData.SkeletonHierarchy);
                        final HashMap<K, Integer> hashMap;
                        final int m;
                        final ArrayList<Integer> list2;
                        final int n4;
                        final ArrayList<Integer> list3;
                        final Object o;
                        boneIndices.forEach((s, n) -> {
                            hashMap.getOrDefault(s, -1);
                            if (m == -1) {
                                hashMap.size();
                                hashMap.put((K)s, m);
                                list2.get(n);
                                if (n4 >= 0) {
                                    list3.add(Integer.valueOf(o[n4]));
                                }
                            }
                            o[n] = m;
                            return;
                        });
                        modelTxt.boneIndices = boneIndices2;
                        modelTxt.SkeletonHierarchy = skeletonHierarchy2;
                        for (int n5 = 0; n5 < modelTxt.vertices.m_numVertices; ++n5) {
                            int n6 = (int)modelTxt.vertices.getElementFloat(n5, 5, 0);
                            int n7 = (int)modelTxt.vertices.getElementFloat(n5, 5, 1);
                            int n8 = (int)modelTxt.vertices.getElementFloat(n5, 5, 2);
                            int n9 = (int)modelTxt.vertices.getElementFloat(n5, 5, 3);
                            if (n6 >= 0) {
                                n6 = array[n6];
                            }
                            if (n7 >= 0) {
                                n7 = array[n7];
                            }
                            if (n8 >= 0) {
                                n8 = array[n8];
                            }
                            if (n9 >= 0) {
                                n9 = array[n9];
                            }
                            modelTxt.vertices.setElement(n5, 5, (float)n6, (float)n7, (float)n8, (float)n9);
                        }
                        final Iterator<AnimationClip> iterator = modelTxt.clips.values().iterator();
                        while (iterator.hasNext()) {
                            for (final Keyframe keyframe : iterator.next().getKeyframes()) {
                                keyframe.Bone = array[keyframe.Bone];
                            }
                        }
                        modelTxt.skinOffsetMatrices = this.RemapMatrices(array, modelTxt.skinOffsetMatrices, modelTxt.boneIndices.size());
                        modelTxt.bindPose = this.RemapMatrices(array, modelTxt.bindPose, modelTxt.boneIndices.size());
                        modelTxt.invBindPose = this.RemapMatrices(array, modelTxt.invBindPose, modelTxt.boneIndices.size());
                    }
                    catch (Exception ex) {
                        ex.toString();
                    }
                }
                bufferedReader.close();
            }
            catch (Throwable t) {
                try {
                    bufferedReader.close();
                }
                catch (Throwable exception) {
                    t.addSuppressed(exception);
                }
                throw t;
            }
            in.close();
        }
        catch (Throwable t2) {
            try {
                in.close();
            }
            catch (Throwable exception2) {
                t2.addSuppressed(exception2);
            }
            throw t2;
        }
        return modelTxt;
    }
    
    protected void applyToMesh(final ModelTxt modelTxt, final ModelMesh modelMesh, final SkinningData skinningData) {
        if (modelTxt.bStatic) {
            if (!ModelManager.NoOpenGL) {
                RenderThread.queueInvokeOnRenderContext(() -> {
                    modelMesh.SetVertexBuffer(new VertexBufferObject(modelTxt.vertices, modelTxt.elements));
                    if (ModelManager.instance.bCreateSoftwareMeshes) {
                        modelMesh.softwareMesh.vb = modelMesh.vb;
                    }
                    return;
                });
            }
        }
        else {
            modelMesh.skinningData = new SkinningData(modelTxt.clips, modelTxt.bindPose, modelTxt.invBindPose, modelTxt.skinOffsetMatrices, modelTxt.SkeletonHierarchy, modelTxt.boneIndices);
            if (!ModelManager.NoOpenGL) {
                RenderThread.queueInvokeOnRenderContext(() -> {
                    modelMesh.SetVertexBuffer(new VertexBufferObject(modelTxt.vertices, modelTxt.elements, modelTxt.bReverse));
                    if (ModelManager.instance.bCreateSoftwareMeshes) {}
                    return;
                });
            }
        }
        if (skinningData != null) {
            modelMesh.skinningData.AnimationClips = skinningData.AnimationClips;
        }
    }
    
    protected void applyToAnimation(final ModelTxt modelTxt, final AnimationAsset animationAsset) {
        animationAsset.AnimationClips = modelTxt.clips;
        animationAsset.assetParams.animationsMesh.skinningData.AnimationClips.putAll(modelTxt.clips);
    }
    
    private ArrayList<Matrix4f> RemapMatrices(final int[] array, final ArrayList<Matrix4f> list, final int initialCapacity) {
        final ArrayList<Matrix4f> list2 = new ArrayList<Matrix4f>(initialCapacity);
        final Matrix4f e = new Matrix4f();
        for (int i = 0; i < initialCapacity; ++i) {
            list2.add(e);
        }
        for (int j = 0; j < array.length; ++j) {
            list2.set(array[j], list.get(j));
        }
        return list2;
    }
    
    private Vector3f getVector(final String s) {
        final Vector3f vector3f = new Vector3f();
        final String[] split = s.split(",");
        vector3f.x = Float.parseFloat(split[0]);
        vector3f.y = Float.parseFloat(split[1]);
        vector3f.z = Float.parseFloat(split[2]);
        return vector3f;
    }
    
    private Quaternion getQuaternion(final String s) {
        final Quaternion quaternion = new Quaternion();
        final String[] split = s.split(",");
        quaternion.x = Float.parseFloat(split[0]);
        quaternion.y = Float.parseFloat(split[1]);
        quaternion.z = Float.parseFloat(split[2]);
        quaternion.w = Float.parseFloat(split[3]);
        return quaternion;
    }
    
    private Matrix4f getMatrix(final String s, final String s2, final String s3, final String s4) {
        final Matrix4f matrix4f = new Matrix4f();
        final String[] split = s.split(",");
        matrix4f.m00 = Float.parseFloat(split[0]);
        matrix4f.m01 = Float.parseFloat(split[1]);
        matrix4f.m02 = Float.parseFloat(split[2]);
        matrix4f.m03 = Float.parseFloat(split[3]);
        final String[] split2 = s2.split(",");
        matrix4f.m10 = Float.parseFloat(split2[0]);
        matrix4f.m11 = Float.parseFloat(split2[1]);
        matrix4f.m12 = Float.parseFloat(split2[2]);
        matrix4f.m13 = Float.parseFloat(split2[3]);
        final String[] split3 = s3.split(",");
        matrix4f.m20 = Float.parseFloat(split3[0]);
        matrix4f.m21 = Float.parseFloat(split3[1]);
        matrix4f.m22 = Float.parseFloat(split3[2]);
        matrix4f.m23 = Float.parseFloat(split3[3]);
        final String[] split4 = s4.split(",");
        matrix4f.m30 = Float.parseFloat(split4[0]);
        matrix4f.m31 = Float.parseFloat(split4[1]);
        matrix4f.m32 = Float.parseFloat(split4[2]);
        matrix4f.m33 = Float.parseFloat(split4[3]);
        return matrix4f;
    }
    
    static {
        instance = new ModelLoader();
    }
    
    public enum LoadMode
    {
        Version, 
        ModelName, 
        VertexStrideElementCount, 
        VertexStrideSize, 
        VertexStrideData, 
        VertexCount, 
        VertexBuffer, 
        NumberOfFaces, 
        FaceData, 
        NumberOfBones, 
        SkeletonHierarchy, 
        BindPose, 
        InvBindPose, 
        SkinOffsetMatrices, 
        NumberOfAnims, 
        Anim;
        
        private static /* synthetic */ LoadMode[] $values() {
            return new LoadMode[] { LoadMode.Version, LoadMode.ModelName, LoadMode.VertexStrideElementCount, LoadMode.VertexStrideSize, LoadMode.VertexStrideData, LoadMode.VertexCount, LoadMode.VertexBuffer, LoadMode.NumberOfFaces, LoadMode.FaceData, LoadMode.NumberOfBones, LoadMode.SkeletonHierarchy, LoadMode.BindPose, LoadMode.InvBindPose, LoadMode.SkinOffsetMatrices, LoadMode.NumberOfAnims, LoadMode.Anim };
        }
        
        static {
            $VALUES = $values();
        }
    }
}

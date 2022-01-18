// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.model.jassimp;

import jassimp.AiQuaternion;
import zombie.core.skinnedmodel.HelperFunctions;
import zombie.util.StringUtils;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.ReadableVector4f;
import java.util.Arrays;
import gnu.trove.list.array.TFloatArrayList;
import zombie.core.skinnedmodel.animation.StaticAnimation;
import zombie.core.skinnedmodel.ModelManager;
import zombie.debug.DebugLog;
import java.util.Collections;
import jassimp.AiNodeAnim;
import zombie.core.skinnedmodel.animation.Keyframe;
import jassimp.AiMesh;
import zombie.core.skinnedmodel.model.SkinningData;
import jassimp.AiScene;
import jassimp.AiAnimation;
import jassimp.AiMatrix4f;
import jassimp.AiBone;
import java.util.List;
import java.util.Collection;
import java.util.Map;
import jassimp.AiWrapperProvider;
import org.lwjgl.util.vector.Quaternion;
import jassimp.AiBuiltInWrapperProvider;
import zombie.core.skinnedmodel.animation.AnimationClip;
import jassimp.AiNode;
import org.lwjgl.util.vector.Matrix4f;
import java.util.ArrayList;
import java.util.HashMap;

public final class ImportedSkeleton
{
    final HashMap<String, Integer> boneIndices;
    final ArrayList<Integer> SkeletonHierarchy;
    final ArrayList<Matrix4f> bindPose;
    final ArrayList<Matrix4f> invBindPose;
    final ArrayList<Matrix4f> skinOffsetMatrices;
    AiNode rootBoneNode;
    final HashMap<String, AnimationClip> clips;
    final AiBuiltInWrapperProvider wrapper;
    final Quaternion end;
    
    private ImportedSkeleton() {
        this.boneIndices = new HashMap<String, Integer>();
        this.SkeletonHierarchy = new ArrayList<Integer>();
        this.bindPose = new ArrayList<Matrix4f>();
        this.invBindPose = new ArrayList<Matrix4f>();
        this.skinOffsetMatrices = new ArrayList<Matrix4f>();
        this.rootBoneNode = null;
        this.clips = new HashMap<String, AnimationClip>();
        this.wrapper = new AiBuiltInWrapperProvider();
        this.end = new Quaternion();
    }
    
    public static ImportedSkeleton process(final ImportedSkeletonParams importedSkeletonParams) {
        final ImportedSkeleton importedSkeleton = new ImportedSkeleton();
        importedSkeleton.processAiScene(importedSkeletonParams);
        return importedSkeleton;
    }
    
    private void processAiScene(final ImportedSkeletonParams importedSkeletonParams) {
        final AiScene scene = importedSkeletonParams.scene;
        final JAssImpImporter.LoadMode mode = importedSkeletonParams.mode;
        final SkinningData skinnedTo = importedSkeletonParams.skinnedTo;
        final float animBonesScaleModifier = importedSkeletonParams.animBonesScaleModifier;
        final Quaternion animBonesRotateModifier = importedSkeletonParams.animBonesRotateModifier;
        final AiMesh mesh = importedSkeletonParams.mesh;
        final AiNode rootBoneNode = (AiNode)scene.getSceneRoot((AiWrapperProvider)this.wrapper);
        this.rootBoneNode = JAssImpImporter.FindNode("Dummy01", rootBoneNode);
        boolean b;
        if (this.rootBoneNode == null) {
            this.rootBoneNode = JAssImpImporter.FindNode("VehicleSkeleton", rootBoneNode);
            b = true;
        }
        else {
            b = false;
        }
        while (this.rootBoneNode != null && this.rootBoneNode.getParent() != null && this.rootBoneNode.getParent() != rootBoneNode) {
            this.rootBoneNode = this.rootBoneNode.getParent();
        }
        if (this.rootBoneNode == null) {
            this.rootBoneNode = rootBoneNode;
        }
        final ArrayList list = new ArrayList<AiNode>();
        JAssImpImporter.CollectBoneNodes(list, this.rootBoneNode);
        final AiNode findNode = JAssImpImporter.FindNode("Translation_Data", rootBoneNode);
        if (findNode != null) {
            list.add(findNode);
            for (AiNode e = findNode.getParent(); e != null && e != rootBoneNode; e = e.getParent()) {
                list.add(e);
            }
        }
        if (skinnedTo != null) {
            this.boneIndices.putAll(skinnedTo.BoneIndices);
            this.SkeletonHierarchy.addAll(skinnedTo.SkeletonHierarchy);
        }
        for (int i = 0; i < list.size(); ++i) {
            final AiNode aiNode = list.get(i);
            final String name = aiNode.getName();
            if (!this.boneIndices.containsKey(name)) {
                this.boneIndices.put(name, this.boneIndices.size());
                if (aiNode == this.rootBoneNode) {
                    this.SkeletonHierarchy.add(-1);
                }
                else {
                    AiNode aiNode2;
                    for (aiNode2 = aiNode.getParent(); aiNode2 != null && !this.boneIndices.containsKey(aiNode2.getName()); aiNode2 = aiNode2.getParent()) {}
                    if (aiNode2 != null) {
                        this.SkeletonHierarchy.add(this.boneIndices.get(aiNode2.getName()));
                    }
                    else {
                        this.SkeletonHierarchy.add(0);
                    }
                }
            }
        }
        final Matrix4f matrix4f = new Matrix4f();
        for (int j = 0; j < this.boneIndices.size(); ++j) {
            this.bindPose.add(matrix4f);
            this.skinOffsetMatrices.add(matrix4f);
        }
        final List bones = mesh.getBones();
        for (int k = 0; k < list.size(); ++k) {
            final AiNode aiNode3 = list.get(k);
            final String name2 = aiNode3.getName();
            final AiBone findAiBone = JAssImpImporter.FindAiBone(name2, bones);
            if (findAiBone != null) {
                final AiMatrix4f aiMatrix4f = (AiMatrix4f)findAiBone.getOffsetMatrix((AiWrapperProvider)this.wrapper);
                if (aiMatrix4f != null) {
                    final Matrix4f matrixFromAiMatrix = JAssImpImporter.getMatrixFromAiMatrix(aiMatrix4f);
                    final Matrix4f matrix4f2 = new Matrix4f(matrixFromAiMatrix);
                    matrix4f2.invert();
                    final Matrix4f matrix4f3 = new Matrix4f();
                    matrix4f3.setIdentity();
                    final AiBone findAiBone2 = JAssImpImporter.FindAiBone(aiNode3.getParent().getName(), bones);
                    if (findAiBone2 != null) {
                        final AiMatrix4f aiMatrix4f2 = (AiMatrix4f)findAiBone2.getOffsetMatrix((AiWrapperProvider)this.wrapper);
                        if (aiMatrix4f2 != null) {
                            JAssImpImporter.getMatrixFromAiMatrix(aiMatrix4f2, matrix4f3);
                        }
                    }
                    final Matrix4f matrix4f4 = new Matrix4f(matrix4f3);
                    matrix4f4.invert();
                    final Matrix4f element = new Matrix4f();
                    Matrix4f.mul(matrix4f2, matrix4f4, element);
                    element.invert();
                    final int intValue = this.boneIndices.get(name2);
                    this.bindPose.set(intValue, element);
                    this.skinOffsetMatrices.set(intValue, matrixFromAiMatrix);
                }
            }
        }
        for (int size = this.bindPose.size(), l = 0; l < size; ++l) {
            final Matrix4f element2 = new Matrix4f((Matrix4f)this.bindPose.get(l));
            element2.invert();
            this.invBindPose.add(l, element2);
        }
        if (mode != JAssImpImporter.LoadMode.AnimationOnly && skinnedTo != null) {
            return;
        }
        final int numAnimations = scene.getNumAnimations();
        if (numAnimations <= 0) {
            return;
        }
        final List animations = scene.getAnimations();
        for (int n = 0; n < numAnimations; ++n) {
            final AiAnimation aiAnimation = animations.get(n);
            if (b) {
                this.processAnimation(aiAnimation, b, 1.0f, null);
            }
            else {
                this.processAnimation(aiAnimation, b, animBonesScaleModifier, animBonesRotateModifier);
            }
        }
    }
    
    @Deprecated
    void processAnimationOld(final AiAnimation aiAnimation, final boolean b) {
        final ArrayList<Keyframe> list = new ArrayList<Keyframe>();
        final float n = (float)aiAnimation.getDuration() / (float)aiAnimation.getTicksPerSecond();
        final ArrayList<Float> list2 = (ArrayList<Float>)new ArrayList<Comparable>();
        final List channels = aiAnimation.getChannels();
        for (int i = 0; i < channels.size(); ++i) {
            final AiNodeAnim aiNodeAnim = channels.get(i);
            for (int j = 0; j < aiNodeAnim.getNumPosKeys(); ++j) {
                final float n2 = (float)aiNodeAnim.getPosKeyTime(j);
                if (!list2.contains(n2)) {
                    list2.add(n2);
                }
            }
            for (int k = 0; k < aiNodeAnim.getNumRotKeys(); ++k) {
                final float n3 = (float)aiNodeAnim.getRotKeyTime(k);
                if (!list2.contains(n3)) {
                    list2.add(n3);
                }
            }
            for (int l = 0; l < aiNodeAnim.getNumScaleKeys(); ++l) {
                final float n4 = (float)aiNodeAnim.getScaleKeyTime(l);
                if (!list2.contains(n4)) {
                    list2.add(n4);
                }
            }
        }
        Collections.sort((List<Comparable>)list2);
        for (int index = 0; index < list2.size(); ++index) {
            for (int n5 = 0; n5 < channels.size(); ++n5) {
                final AiNodeAnim aiNodeAnim2 = channels.get(n5);
                final Keyframe e = new Keyframe();
                e.clear();
                e.BoneName = aiNodeAnim2.getNodeName();
                final Integer n6 = this.boneIndices.get(e.BoneName);
                if (n6 == null) {
                    DebugLog.General.error("Could not find bone index for node name: \"%s\"", e.BoneName);
                }
                else {
                    e.Bone = n6;
                    e.Time = list2.get(index) / (float)aiAnimation.getTicksPerSecond();
                    if (!b) {
                        e.Position = JAssImpImporter.GetKeyFramePosition(aiNodeAnim2, list2.get(index));
                        e.Rotation = JAssImpImporter.GetKeyFrameRotation(aiNodeAnim2, list2.get(index));
                        e.Scale = JAssImpImporter.GetKeyFrameScale(aiNodeAnim2, list2.get(index));
                    }
                    else {
                        e.Position = this.GetKeyFramePosition(aiNodeAnim2, list2.get(index), aiAnimation.getDuration());
                        e.Rotation = this.GetKeyFrameRotation(aiNodeAnim2, list2.get(index), aiAnimation.getDuration());
                        e.Scale = this.GetKeyFrameScale(aiNodeAnim2, list2.get(index), aiAnimation.getDuration());
                    }
                    if (e.Bone >= 0) {
                        list.add(e);
                    }
                }
            }
        }
        String key = aiAnimation.getName();
        final int index2 = key.indexOf(124);
        if (index2 > 0) {
            key = key.substring(index2 + 1);
        }
        final AnimationClip value = new AnimationClip(n, list, key, true);
        list.clear();
        if (ModelManager.instance.bCreateSoftwareMeshes) {
            value.staticClip = new StaticAnimation(value);
        }
        this.clips.put(key, value);
    }
    
    private void processAnimation(final AiAnimation aiAnimation, final boolean b, final float n, final Quaternion quaternion) {
        final ArrayList<Keyframe> list = new ArrayList<Keyframe>();
        final float n2 = (float)aiAnimation.getDuration();
        final float n3 = n2 / (float)aiAnimation.getTicksPerSecond();
        final TFloatArrayList[] a = new TFloatArrayList[this.boneIndices.size()];
        Arrays.fill(a, null);
        final ArrayList<ArrayList<AiNodeAnim>> list2 = new ArrayList<ArrayList<AiNodeAnim>>(this.boneIndices.size());
        for (int i = 0; i < this.boneIndices.size(); ++i) {
            list2.add(null);
        }
        this.collectBoneFrames(aiAnimation, a, list2);
        Quaternion quaternion2 = null;
        final boolean b2 = quaternion != null;
        if (b2) {
            quaternion2 = new Quaternion();
            Quaternion.mulInverse(quaternion2, quaternion, quaternion2);
        }
        for (int j = 0; j < this.boneIndices.size(); ++j) {
            final ArrayList<AiNodeAnim> list3 = list2.get(j);
            if (list3 == null) {
                if (j == 0 && quaternion != null) {
                    final Quaternion quaternion3 = new Quaternion();
                    quaternion3.set((ReadableVector4f)quaternion);
                    this.addDefaultAnimTrack("RootNode", j, quaternion3, new Vector3f(0.0f, 0.0f, 0.0f), list, n3);
                }
            }
            else {
                final TFloatArrayList list4 = a[j];
                if (list4 != null) {
                    list4.sort();
                    final int parentBoneIdx = this.getParentBoneIdx(j);
                    final boolean b3 = b2 && (parentBoneIdx == 0 || this.doesParentBoneHaveAnimFrames(a, list2, j));
                    for (int k = 0; k < list4.size(); ++k) {
                        final float value = list4.get(k);
                        final float time = value / (float)aiAnimation.getTicksPerSecond();
                        for (int l = 0; l < list3.size(); ++l) {
                            final AiNodeAnim aiNodeAnim = list3.get(l);
                            final Keyframe e = new Keyframe();
                            e.clear();
                            e.BoneName = aiNodeAnim.getNodeName();
                            e.Bone = j;
                            e.Time = time;
                            if (!b) {
                                e.Position = JAssImpImporter.GetKeyFramePosition(aiNodeAnim, value);
                                e.Rotation = JAssImpImporter.GetKeyFrameRotation(aiNodeAnim, value);
                                e.Scale = JAssImpImporter.GetKeyFrameScale(aiNodeAnim, value);
                            }
                            else {
                                e.Position = this.GetKeyFramePosition(aiNodeAnim, value, n2);
                                e.Rotation = this.GetKeyFrameRotation(aiNodeAnim, value, n2);
                                e.Scale = this.GetKeyFrameScale(aiNodeAnim, value, n2);
                            }
                            final Vector3f position = e.Position;
                            position.x *= n;
                            final Vector3f position2 = e.Position;
                            position2.y *= n;
                            final Vector3f position3 = e.Position;
                            position3.z *= n;
                            if (b2) {
                                if (b3) {
                                    Quaternion.mul(quaternion2, e.Rotation, e.Rotation);
                                    if (!StringUtils.startsWithIgnoreCase(e.BoneName, "Translation_Data")) {
                                        HelperFunctions.transform(quaternion2, e.Position, e.Position);
                                    }
                                }
                                Quaternion.mul(e.Rotation, quaternion, e.Rotation);
                            }
                            list.add(e);
                        }
                    }
                }
            }
        }
        String s = aiAnimation.getName();
        final int index = s.indexOf(124);
        if (index > 0) {
            s = s.substring(index + 1);
        }
        final String trim = s.trim();
        final AnimationClip value2 = new AnimationClip(n3, list, trim, true);
        list.clear();
        if (ModelManager.instance.bCreateSoftwareMeshes) {
            value2.staticClip = new StaticAnimation(value2);
        }
        this.clips.put(trim, value2);
    }
    
    private void addDefaultAnimTrack(final String s, final int n, final Quaternion quaternion, final Vector3f vector3f, final ArrayList<Keyframe> list, final float time) {
        final Vector3f vector3f2 = new Vector3f(1.0f, 1.0f, 1.0f);
        final Keyframe e = new Keyframe();
        e.clear();
        e.BoneName = s;
        e.Bone = n;
        e.Time = 0.0f;
        e.Position = vector3f;
        e.Rotation = quaternion;
        e.Scale = vector3f2;
        list.add(e);
        final Keyframe e2 = new Keyframe();
        e2.clear();
        e2.BoneName = s;
        e2.Bone = n;
        e2.Time = time;
        e2.Position = vector3f;
        e2.Rotation = quaternion;
        e2.Scale = vector3f2;
        list.add(e2);
    }
    
    private boolean doesParentBoneHaveAnimFrames(final TFloatArrayList[] array, final ArrayList<ArrayList<AiNodeAnim>> list, final int n) {
        final int parentBoneIdx = this.getParentBoneIdx(n);
        return parentBoneIdx >= 0 && this.doesBoneHaveAnimFrames(array, list, parentBoneIdx);
    }
    
    private boolean doesBoneHaveAnimFrames(final TFloatArrayList[] array, final ArrayList<ArrayList<AiNodeAnim>> list, final int index) {
        final TFloatArrayList list2 = array[index];
        return list2 != null && list2.size() > 0 && list.get(index).size() > 0;
    }
    
    private void collectBoneFrames(final AiAnimation aiAnimation, final TFloatArrayList[] array, final ArrayList<ArrayList<AiNodeAnim>> list) {
        final List channels = aiAnimation.getChannels();
        for (int i = 0; i < channels.size(); ++i) {
            final AiNodeAnim e = channels.get(i);
            final String nodeName = e.getNodeName();
            final Integer n = this.boneIndices.get(nodeName);
            if (n == null) {
                DebugLog.General.error("Could not find bone index for node name: \"%s\"", nodeName);
            }
            else {
                ArrayList<AiNodeAnim> element = list.get(n);
                if (element == null) {
                    element = new ArrayList<AiNodeAnim>();
                    list.set(n, element);
                }
                element.add(e);
                TFloatArrayList list2 = array[n];
                if (list2 == null) {
                    list2 = new TFloatArrayList();
                    array[n] = list2;
                }
                for (int j = 0; j < e.getNumPosKeys(); ++j) {
                    final float n2 = (float)e.getPosKeyTime(j);
                    if (!list2.contains(n2)) {
                        list2.add(n2);
                    }
                }
                for (int k = 0; k < e.getNumRotKeys(); ++k) {
                    final float n3 = (float)e.getRotKeyTime(k);
                    if (!list2.contains(n3)) {
                        list2.add(n3);
                    }
                }
                for (int l = 0; l < e.getNumScaleKeys(); ++l) {
                    final float n4 = (float)e.getScaleKeyTime(l);
                    if (!list2.contains(n4)) {
                        list2.add(n4);
                    }
                }
            }
        }
    }
    
    private int getParentBoneIdx(final int index) {
        if (index > -1) {
            return this.SkeletonHierarchy.get(index);
        }
        return -1;
    }
    
    public int getNumBoneAncestors(final int n) {
        int n2 = 0;
        for (int i = this.getParentBoneIdx(n); i > -1; i = this.getParentBoneIdx(i)) {
            ++n2;
        }
        return n2;
    }
    
    private Vector3f GetKeyFramePosition(final AiNodeAnim aiNodeAnim, final float n, final double n2) {
        final Vector3f vector3f = new Vector3f();
        if (aiNodeAnim.getNumPosKeys() == 0) {
            return vector3f;
        }
        int n3;
        for (n3 = 0; n3 < aiNodeAnim.getNumPosKeys() - 1 && n >= aiNodeAnim.getPosKeyTime(n3 + 1); ++n3) {}
        final int n4 = (n3 + 1) % aiNodeAnim.getNumPosKeys();
        final float n5 = (float)aiNodeAnim.getPosKeyTime(n3);
        final float n6 = (float)aiNodeAnim.getPosKeyTime(n4);
        float n7 = n6 - n5;
        if (n7 < 0.0f) {
            n7 += (float)n2;
        }
        if (n7 > 0.0f) {
            final float n8 = (n - n5) / (n6 - n5);
            final float posKeyX = aiNodeAnim.getPosKeyX(n3);
            final float n9 = posKeyX + n8 * (aiNodeAnim.getPosKeyX(n4) - posKeyX);
            final float posKeyY = aiNodeAnim.getPosKeyY(n3);
            final float n10 = posKeyY + n8 * (aiNodeAnim.getPosKeyY(n4) - posKeyY);
            final float posKeyZ = aiNodeAnim.getPosKeyZ(n3);
            vector3f.set(n9, n10, posKeyZ + n8 * (aiNodeAnim.getPosKeyZ(n4) - posKeyZ));
        }
        else {
            vector3f.set(aiNodeAnim.getPosKeyX(n3), aiNodeAnim.getPosKeyY(n3), aiNodeAnim.getPosKeyZ(n3));
        }
        return vector3f;
    }
    
    private Quaternion GetKeyFrameRotation(final AiNodeAnim aiNodeAnim, final float n, final double n2) {
        final Quaternion quaternion = new Quaternion();
        if (aiNodeAnim.getNumRotKeys() == 0) {
            return quaternion;
        }
        int n3;
        for (n3 = 0; n3 < aiNodeAnim.getNumRotKeys() - 1 && n >= aiNodeAnim.getRotKeyTime(n3 + 1); ++n3) {}
        final int n4 = (n3 + 1) % aiNodeAnim.getNumRotKeys();
        final float n5 = (float)aiNodeAnim.getRotKeyTime(n3);
        float n6 = (float)aiNodeAnim.getRotKeyTime(n4) - n5;
        if (n6 < 0.0f) {
            n6 += (float)n2;
        }
        if (n6 > 0.0f) {
            final float n7 = (n - n5) / n6;
            final AiQuaternion aiQuaternion = (AiQuaternion)aiNodeAnim.getRotKeyQuaternion(n3, (AiWrapperProvider)this.wrapper);
            final AiQuaternion aiQuaternion2 = (AiQuaternion)aiNodeAnim.getRotKeyQuaternion(n4, (AiWrapperProvider)this.wrapper);
            double a = aiQuaternion.getX() * aiQuaternion2.getX() + aiQuaternion.getY() * aiQuaternion2.getY() + aiQuaternion.getZ() * aiQuaternion2.getZ() + aiQuaternion.getW() * aiQuaternion2.getW();
            this.end.set(aiQuaternion2.getX(), aiQuaternion2.getY(), aiQuaternion2.getZ(), aiQuaternion2.getW());
            if (a < 0.0) {
                a *= -1.0;
                this.end.setX(-this.end.getX());
                this.end.setY(-this.end.getY());
                this.end.setZ(-this.end.getZ());
                this.end.setW(-this.end.getW());
            }
            double n8;
            double n9;
            if (1.0 - a > 1.0E-4) {
                final double acos = Math.acos(a);
                final double sin = Math.sin(acos);
                n8 = Math.sin((1.0 - n7) * acos) / sin;
                n9 = Math.sin(n7 * acos) / sin;
            }
            else {
                n8 = 1.0 - n7;
                n9 = n7;
            }
            quaternion.set((float)(n8 * aiQuaternion.getX() + n9 * this.end.getX()), (float)(n8 * aiQuaternion.getY() + n9 * this.end.getY()), (float)(n8 * aiQuaternion.getZ() + n9 * this.end.getZ()), (float)(n8 * aiQuaternion.getW() + n9 * this.end.getW()));
        }
        else {
            quaternion.set(aiNodeAnim.getRotKeyX(n3), aiNodeAnim.getRotKeyY(n3), aiNodeAnim.getRotKeyZ(n3), aiNodeAnim.getRotKeyW(n3));
        }
        return quaternion;
    }
    
    private Vector3f GetKeyFrameScale(final AiNodeAnim aiNodeAnim, final float n, final double n2) {
        final Vector3f vector3f = new Vector3f(1.0f, 1.0f, 1.0f);
        if (aiNodeAnim.getNumScaleKeys() == 0) {
            return vector3f;
        }
        int n3;
        for (n3 = 0; n3 < aiNodeAnim.getNumScaleKeys() - 1 && n >= aiNodeAnim.getScaleKeyTime(n3 + 1); ++n3) {}
        vector3f.set(aiNodeAnim.getScaleKeyX(n3), aiNodeAnim.getScaleKeyY(n3), aiNodeAnim.getScaleKeyZ(n3));
        return vector3f;
    }
}

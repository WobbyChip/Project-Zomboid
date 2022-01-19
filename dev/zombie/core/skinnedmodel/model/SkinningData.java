// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.model;

import java.util.ArrayList;
import org.lwjgl.util.vector.Matrix4f;
import java.util.List;
import zombie.core.skinnedmodel.animation.AnimationClip;
import java.util.HashMap;

public final class SkinningData
{
    public HashMap<String, AnimationClip> AnimationClips;
    public List<Matrix4f> BindPose;
    public List<Matrix4f> InverseBindPose;
    public List<Matrix4f> BoneOffset;
    public List<Integer> SkeletonHierarchy;
    public HashMap<String, Integer> BoneIndices;
    private SkinningBoneHierarchy m_boneHieararchy;
    
    public SkinningData(final HashMap<String, AnimationClip> animationClips, final List<Matrix4f> bindPose, final List<Matrix4f> inverseBindPose, final List<Matrix4f> list, final List<Integer> skeletonHierarchy, final HashMap<String, Integer> boneIndices) {
        this.BoneOffset = new ArrayList<Matrix4f>();
        this.m_boneHieararchy = null;
        this.AnimationClips = animationClips;
        this.BindPose = bindPose;
        this.InverseBindPose = inverseBindPose;
        this.SkeletonHierarchy = skeletonHierarchy;
        for (int i = 0; i < skeletonHierarchy.size(); ++i) {
            this.BoneOffset.add(list.get(i));
        }
        this.BoneIndices = boneIndices;
    }
    
    private void validateBoneHierarchy() {
        if (this.m_boneHieararchy == null) {
            (this.m_boneHieararchy = new SkinningBoneHierarchy()).buildBoneHiearchy(this);
        }
    }
    
    public int numBones() {
        return this.SkeletonHierarchy.size();
    }
    
    public int numRootBones() {
        return this.getBoneHieararchy().numRootBones();
    }
    
    public int getParentBoneIdx(final int n) {
        return this.SkeletonHierarchy.get(n);
    }
    
    public SkinningBone getBoneAt(final int n) {
        return this.getBoneHieararchy().getBoneAt(n);
    }
    
    public SkinningBone getBone(final String key) {
        final Integer n = this.BoneIndices.get(key);
        if (n == null) {
            return null;
        }
        return this.getBoneAt(n);
    }
    
    public SkinningBone getRootBoneAt(final int n) {
        return this.getBoneHieararchy().getRootBoneAt(n);
    }
    
    public SkinningBoneHierarchy getBoneHieararchy() {
        this.validateBoneHierarchy();
        return this.m_boneHieararchy;
    }
}

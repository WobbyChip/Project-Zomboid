// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.model;

import java.util.Iterator;
import java.util.Map;
import zombie.util.list.PZArrayUtil;

public final class SkinningBoneHierarchy
{
    private boolean m_boneHieararchyValid;
    private SkinningBone[] m_allBones;
    private SkinningBone[] m_rootBones;
    
    public SkinningBoneHierarchy() {
        this.m_boneHieararchyValid = false;
        this.m_allBones = null;
        this.m_rootBones = null;
    }
    
    public boolean isValid() {
        return this.m_boneHieararchyValid;
    }
    
    public void buildBoneHiearchy(final SkinningData skinningData) {
        this.m_rootBones = new SkinningBone[0];
        PZArrayUtil.arrayPopulate(this.m_allBones = new SkinningBone[skinningData.numBones()], SkinningBone::new);
        for (final Map.Entry<String, Integer> entry : skinningData.BoneIndices.entrySet()) {
            final int intValue = entry.getValue();
            final String name = entry.getKey();
            final SkinningBone skinningBone = this.m_allBones[intValue];
            skinningBone.Index = intValue;
            skinningBone.Name = name;
            skinningBone.Children = new SkinningBone[0];
        }
        for (int i = 0; i < skinningData.numBones(); ++i) {
            final SkinningBone skinningBone2 = this.m_allBones[i];
            final int parentBoneIdx = skinningData.getParentBoneIdx(i);
            if (parentBoneIdx > -1) {
                skinningBone2.Parent = this.m_allBones[parentBoneIdx];
                skinningBone2.Parent.Children = PZArrayUtil.add(skinningBone2.Parent.Children, skinningBone2);
            }
            else {
                this.m_rootBones = PZArrayUtil.add(this.m_rootBones, skinningBone2);
            }
        }
        this.m_boneHieararchyValid = true;
    }
    
    public int numRootBones() {
        return this.m_rootBones.length;
    }
    
    public SkinningBone getBoneAt(final int n) {
        return this.m_allBones[n];
    }
    
    public SkinningBone getRootBoneAt(final int n) {
        return this.m_rootBones[n];
    }
}

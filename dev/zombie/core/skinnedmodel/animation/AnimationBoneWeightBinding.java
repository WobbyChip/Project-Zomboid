// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.animation;

import zombie.core.skinnedmodel.model.SkinningBone;
import java.util.function.Consumer;
import zombie.core.skinnedmodel.advancedanimation.AnimBoneWeight;

public class AnimationBoneWeightBinding extends AnimationBoneBinding
{
    private float m_weight;
    private boolean m_includeDescendants;
    
    public AnimationBoneWeightBinding(final AnimBoneWeight animBoneWeight) {
        this(animBoneWeight.boneName, animBoneWeight.weight, animBoneWeight.includeDescendants);
    }
    
    public AnimationBoneWeightBinding(final String s, final float weight, final boolean includeDescendants) {
        super(s);
        this.m_weight = 1.0f;
        this.m_includeDescendants = true;
        this.m_weight = weight;
        this.m_includeDescendants = includeDescendants;
    }
    
    public float getWeight() {
        return this.m_weight;
    }
    
    public void setWeight(final float weight) {
        this.m_weight = weight;
    }
    
    public boolean getIncludeDescendants() {
        return this.m_includeDescendants;
    }
    
    public void setIncludeDescendants(final boolean includeDescendants) {
        this.m_includeDescendants = includeDescendants;
    }
    
    public void forEachDescendant(final Consumer<SkinningBone> consumer) {
        if (!this.m_includeDescendants) {
            return;
        }
        final SkinningBone bone = this.getBone();
        if (bone == null) {
            return;
        }
        bone.forEachDescendant(consumer);
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.advancedanimation;

public final class AnimBoneWeight
{
    public String boneName;
    public float weight;
    public boolean includeDescendants;
    
    public AnimBoneWeight() {
        this.weight = 1.0f;
        this.includeDescendants = true;
    }
    
    public AnimBoneWeight(final String boneName, final float weight) {
        this.weight = 1.0f;
        this.includeDescendants = true;
        this.boneName = boneName;
        this.weight = weight;
        this.includeDescendants = true;
    }
}

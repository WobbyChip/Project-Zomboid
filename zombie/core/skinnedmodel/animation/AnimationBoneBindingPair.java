// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.animation;

import zombie.util.StringUtils;
import zombie.core.skinnedmodel.model.SkinningBone;
import zombie.core.skinnedmodel.model.SkinningData;

public final class AnimationBoneBindingPair
{
    public final AnimationBoneBinding boneBindingA;
    public final AnimationBoneBinding boneBindingB;
    
    public AnimationBoneBindingPair(final String s, final String s2) {
        this.boneBindingA = new AnimationBoneBinding(s);
        this.boneBindingB = new AnimationBoneBinding(s2);
    }
    
    public void setSkinningData(final SkinningData skinningData) {
        this.boneBindingA.setSkinningData(skinningData);
        this.boneBindingB.setSkinningData(skinningData);
    }
    
    public SkinningBone getBoneA() {
        return this.boneBindingA.getBone();
    }
    
    public SkinningBone getBoneB() {
        return this.boneBindingB.getBone();
    }
    
    public boolean isValid() {
        return this.getBoneA() != null && this.getBoneB() != null;
    }
    
    public boolean matches(final String s, final String s2) {
        return StringUtils.equalsIgnoreCase(this.boneBindingA.boneName, s) && StringUtils.equalsIgnoreCase(this.boneBindingB.boneName, s2);
    }
    
    public int getBoneIdxA() {
        return getBoneIdx(this.getBoneA());
    }
    
    public int getBoneIdxB() {
        return getBoneIdx(this.getBoneB());
    }
    
    private static int getBoneIdx(final SkinningBone skinningBone) {
        return (skinningBone != null) ? skinningBone.Index : -1;
    }
    
    @Override
    public String toString() {
        final String lineSeparator = System.lineSeparator();
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getClass().getName(), lineSeparator, lineSeparator, StringUtils.indent(String.valueOf(this.boneBindingA)), lineSeparator, StringUtils.indent(String.valueOf(this.boneBindingB)), lineSeparator);
    }
}

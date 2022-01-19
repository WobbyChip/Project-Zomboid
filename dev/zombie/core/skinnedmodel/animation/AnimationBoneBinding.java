// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.animation;

import zombie.util.StringUtils;
import zombie.core.skinnedmodel.model.SkinningData;
import zombie.core.skinnedmodel.model.SkinningBone;

public class AnimationBoneBinding
{
    public final String boneName;
    private SkinningBone m_bone;
    private SkinningData m_skinningData;
    
    public AnimationBoneBinding(final String boneName) {
        this.m_bone = null;
        this.boneName = boneName;
    }
    
    public SkinningData getSkinningData() {
        return this.m_skinningData;
    }
    
    public void setSkinningData(final SkinningData skinningData) {
        if (this.m_skinningData != skinningData) {
            this.m_skinningData = skinningData;
            this.m_bone = null;
        }
    }
    
    public SkinningBone getBone() {
        if (this.m_bone == null) {
            this.initBone();
        }
        return this.m_bone;
    }
    
    private void initBone() {
        if (this.m_skinningData == null) {
            this.m_bone = null;
            return;
        }
        this.m_bone = this.m_skinningData.getBone(this.boneName);
    }
    
    @Override
    public String toString() {
        final String lineSeparator = System.lineSeparator();
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getClass().getName(), lineSeparator, lineSeparator, this.boneName, lineSeparator, StringUtils.indent(String.valueOf(this.m_bone)), lineSeparator);
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.advancedanimation;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public final class AnimTransition
{
    public String m_Target;
    public String m_AnimName;
    public float m_SyncAdjustTime;
    public float m_blendInTime;
    public float m_blendOutTime;
    public float m_speedScale;
    public List<AnimCondition> m_Conditions;
    
    public AnimTransition() {
        this.m_SyncAdjustTime = 0.0f;
        this.m_blendInTime = Float.POSITIVE_INFINITY;
        this.m_blendOutTime = Float.POSITIVE_INFINITY;
        this.m_speedScale = Float.POSITIVE_INFINITY;
        this.m_Conditions = new ArrayList<AnimCondition>();
    }
}

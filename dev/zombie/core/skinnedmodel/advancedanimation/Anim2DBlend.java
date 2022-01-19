// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.advancedanimation;

import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlAttribute;

public final class Anim2DBlend
{
    public String m_AnimName;
    public float m_XPos;
    public float m_YPos;
    public float m_SpeedScale;
    @XmlAttribute(name = "referenceID")
    @XmlID
    public String m_referenceID;
    
    public Anim2DBlend() {
        this.m_AnimName = "";
        this.m_XPos = 0.0f;
        this.m_YPos = 0.0f;
        this.m_SpeedScale = 1.0f;
    }
}

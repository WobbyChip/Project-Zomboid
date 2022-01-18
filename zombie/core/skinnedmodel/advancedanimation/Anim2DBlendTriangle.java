// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.advancedanimation;

import javax.xml.bind.annotation.XmlIDREF;

public final class Anim2DBlendTriangle
{
    @XmlIDREF
    public Anim2DBlend node1;
    @XmlIDREF
    public Anim2DBlend node2;
    @XmlIDREF
    public Anim2DBlend node3;
    
    public static double sign(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        return (n - n5) * (n4 - n6) - (n3 - n5) * (n2 - n6);
    }
    
    static boolean PointInTriangle(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8) {
        final boolean b = sign(n, n2, n3, n4, n5, n6) < 0.0;
        final boolean b2 = sign(n, n2, n5, n6, n7, n8) < 0.0;
        final boolean b3 = sign(n, n2, n7, n8, n3, n4) < 0.0;
        return b == b2 && b2 == b3;
    }
    
    public boolean Contains(final float n, final float n2) {
        return PointInTriangle(n, n2, this.node1.m_XPos, this.node1.m_YPos, this.node2.m_XPos, this.node2.m_YPos, this.node3.m_XPos, this.node3.m_YPos);
    }
}

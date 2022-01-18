// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.opengl;

import zombie.core.Core;
import zombie.core.skinnedmodel.ModelCamera;

public final class ParticleModelCamera extends ModelCamera
{
    @Override
    public void Begin() {
        Core.getInstance().DoPushIsoParticleStuff(this.m_x, this.m_y, this.m_z);
    }
    
    @Override
    public void End() {
        Core.getInstance().DoPopIsoStuff();
    }
}

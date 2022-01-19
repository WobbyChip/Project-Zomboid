// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.advancedanimation;

public abstract class AnimationVariableSlot implements IAnimationVariableSlot
{
    private final String m_key;
    
    protected AnimationVariableSlot(final String s) {
        this.m_key = s.toLowerCase().trim();
    }
    
    @Override
    public String getKey() {
        return this.m_key;
    }
}

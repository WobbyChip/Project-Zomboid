// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.advancedanimation;

public interface IAnimationVariableMap extends IAnimationVariableSource
{
    IAnimationVariableSlot getOrCreateVariable(final String p0);
    
    void setVariable(final IAnimationVariableSlot p0);
    
    void setVariable(final String p0, final String p1);
    
    void setVariable(final String p0, final boolean p1);
    
    void setVariable(final String p0, final float p1);
    
    void clearVariable(final String p0);
    
    void clearVariables();
}

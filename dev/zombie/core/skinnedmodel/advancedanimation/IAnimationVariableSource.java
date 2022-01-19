// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.advancedanimation;

public interface IAnimationVariableSource
{
    IAnimationVariableSlot getVariable(final AnimationVariableHandle p0);
    
    IAnimationVariableSlot getVariable(final String p0);
    
    String getVariableString(final String p0);
    
    float getVariableFloat(final String p0, final float p1);
    
    boolean getVariableBoolean(final String p0);
    
    Iterable<IAnimationVariableSlot> getGameVariables();
    
    boolean isVariable(final String p0, final String p1);
    
    boolean containsVariable(final String p0);
}

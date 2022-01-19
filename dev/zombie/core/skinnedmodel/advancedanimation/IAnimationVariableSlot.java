// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.advancedanimation;

public interface IAnimationVariableSlot
{
    String getKey();
    
    String getValueString();
    
    float getValueFloat();
    
    boolean getValueBool();
    
    void setValue(final String p0);
    
    void setValue(final float p0);
    
    void setValue(final boolean p0);
    
    AnimationVariableType getType();
    
    boolean canConvertFrom(final String p0);
    
    void clear();
    
    default boolean isReadOnly() {
        return false;
    }
}

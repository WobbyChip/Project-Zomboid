// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.advancedanimation;

import zombie.core.math.PZMath;

public final class AnimationVariableSlotCallbackFloat extends AnimationVariableSlotCallback<Float>
{
    private float m_defaultValue;
    
    public AnimationVariableSlotCallbackFloat(final String s, final CallbackGetStrongTyped callbackGetStrongTyped) {
        super(s, callbackGetStrongTyped);
        this.m_defaultValue = 0.0f;
    }
    
    public AnimationVariableSlotCallbackFloat(final String s, final CallbackGetStrongTyped callbackGetStrongTyped, final CallbackSetStrongTyped callbackSetStrongTyped) {
        super(s, callbackGetStrongTyped, callbackSetStrongTyped);
        this.m_defaultValue = 0.0f;
    }
    
    public AnimationVariableSlotCallbackFloat(final String s, final float defaultValue, final CallbackGetStrongTyped callbackGetStrongTyped) {
        super(s, callbackGetStrongTyped);
        this.m_defaultValue = 0.0f;
        this.m_defaultValue = defaultValue;
    }
    
    public AnimationVariableSlotCallbackFloat(final String s, final float defaultValue, final CallbackGetStrongTyped callbackGetStrongTyped, final CallbackSetStrongTyped callbackSetStrongTyped) {
        super(s, callbackGetStrongTyped, callbackSetStrongTyped);
        this.m_defaultValue = 0.0f;
        this.m_defaultValue = defaultValue;
    }
    
    @Override
    public Float getDefaultValue() {
        return this.m_defaultValue;
    }
    
    @Override
    public String getValueString() {
        return this.getValue().toString();
    }
    
    @Override
    public float getValueFloat() {
        return this.getValue();
    }
    
    @Override
    public boolean getValueBool() {
        return this.getValueFloat() != 0.0f;
    }
    
    @Override
    public void setValue(final String s) {
        this.trySetValue(PZMath.tryParseFloat(s, 0.0f));
    }
    
    @Override
    public void setValue(final float f) {
        this.trySetValue(f);
    }
    
    @Override
    public void setValue(final boolean b) {
        this.trySetValue(b ? 1.0f : 0.0f);
    }
    
    @Override
    public AnimationVariableType getType() {
        return AnimationVariableType.Float;
    }
    
    @Override
    public boolean canConvertFrom(final String s) {
        return true;
    }
    
    public interface CallbackSetStrongTyped extends CallbackSet<Float>
    {
    }
    
    public interface CallbackGetStrongTyped extends CallbackGet<Float>
    {
    }
}

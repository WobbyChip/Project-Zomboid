// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.advancedanimation;

import zombie.core.math.PZMath;

public final class AnimationVariableSlotCallbackInt extends AnimationVariableSlotCallback<Integer>
{
    private int m_defaultValue;
    
    public AnimationVariableSlotCallbackInt(final String s, final CallbackGetStrongTyped callbackGetStrongTyped) {
        super(s, callbackGetStrongTyped);
        this.m_defaultValue = 0;
    }
    
    public AnimationVariableSlotCallbackInt(final String s, final CallbackGetStrongTyped callbackGetStrongTyped, final CallbackSetStrongTyped callbackSetStrongTyped) {
        super(s, callbackGetStrongTyped, callbackSetStrongTyped);
        this.m_defaultValue = 0;
    }
    
    public AnimationVariableSlotCallbackInt(final String s, final int defaultValue, final CallbackGetStrongTyped callbackGetStrongTyped) {
        super(s, callbackGetStrongTyped);
        this.m_defaultValue = 0;
        this.m_defaultValue = defaultValue;
    }
    
    public AnimationVariableSlotCallbackInt(final String s, final int defaultValue, final CallbackGetStrongTyped callbackGetStrongTyped, final CallbackSetStrongTyped callbackSetStrongTyped) {
        super(s, callbackGetStrongTyped, callbackSetStrongTyped);
        this.m_defaultValue = 0;
        this.m_defaultValue = defaultValue;
    }
    
    @Override
    public Integer getDefaultValue() {
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
        this.trySetValue(PZMath.tryParseInt(s, 0));
    }
    
    @Override
    public void setValue(final float n) {
        this.trySetValue((int)n);
    }
    
    @Override
    public void setValue(final boolean i) {
        this.trySetValue(i ? 1 : 0);
    }
    
    @Override
    public AnimationVariableType getType() {
        return AnimationVariableType.Float;
    }
    
    @Override
    public boolean canConvertFrom(final String s) {
        return true;
    }
    
    public interface CallbackSetStrongTyped extends CallbackSet<Integer>
    {
    }
    
    public interface CallbackGetStrongTyped extends CallbackGet<Integer>
    {
    }
}

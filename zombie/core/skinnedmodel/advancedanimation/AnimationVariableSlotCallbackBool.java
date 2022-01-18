// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.advancedanimation;

import zombie.util.StringUtils;

public final class AnimationVariableSlotCallbackBool extends AnimationVariableSlotCallback<Boolean>
{
    private boolean m_defaultValue;
    
    public AnimationVariableSlotCallbackBool(final String s, final CallbackGetStrongTyped callbackGetStrongTyped) {
        super(s, callbackGetStrongTyped);
        this.m_defaultValue = false;
    }
    
    public AnimationVariableSlotCallbackBool(final String s, final CallbackGetStrongTyped callbackGetStrongTyped, final CallbackSetStrongTyped callbackSetStrongTyped) {
        super(s, callbackGetStrongTyped, callbackSetStrongTyped);
        this.m_defaultValue = false;
    }
    
    public AnimationVariableSlotCallbackBool(final String s, final boolean defaultValue, final CallbackGetStrongTyped callbackGetStrongTyped) {
        super(s, callbackGetStrongTyped);
        this.m_defaultValue = false;
        this.m_defaultValue = defaultValue;
    }
    
    public AnimationVariableSlotCallbackBool(final String s, final boolean defaultValue, final CallbackGetStrongTyped callbackGetStrongTyped, final CallbackSetStrongTyped callbackSetStrongTyped) {
        super(s, callbackGetStrongTyped, callbackSetStrongTyped);
        this.m_defaultValue = false;
        this.m_defaultValue = defaultValue;
    }
    
    @Override
    public Boolean getDefaultValue() {
        return this.m_defaultValue;
    }
    
    @Override
    public String getValueString() {
        return this.getValue() ? "true" : "false";
    }
    
    @Override
    public float getValueFloat() {
        return this.getValue() ? 1.0f : 0.0f;
    }
    
    @Override
    public boolean getValueBool() {
        return this.getValue();
    }
    
    @Override
    public void setValue(final String s) {
        this.trySetValue(StringUtils.tryParseBoolean(s));
    }
    
    @Override
    public void setValue(final float n) {
        this.trySetValue(n != 0.0);
    }
    
    @Override
    public void setValue(final boolean b) {
        this.trySetValue(b);
    }
    
    @Override
    public AnimationVariableType getType() {
        return AnimationVariableType.Boolean;
    }
    
    @Override
    public boolean canConvertFrom(final String s) {
        return StringUtils.tryParseBoolean(s);
    }
    
    public interface CallbackSetStrongTyped extends CallbackSet<Boolean>
    {
    }
    
    public interface CallbackGetStrongTyped extends CallbackGet<Boolean>
    {
    }
}

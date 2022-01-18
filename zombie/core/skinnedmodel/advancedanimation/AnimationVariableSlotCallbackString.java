// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.advancedanimation;

import zombie.util.StringUtils;
import zombie.core.math.PZMath;

public final class AnimationVariableSlotCallbackString extends AnimationVariableSlotCallback<String>
{
    private String m_defaultValue;
    
    public AnimationVariableSlotCallbackString(final String s, final CallbackGetStrongTyped callbackGetStrongTyped) {
        super(s, callbackGetStrongTyped);
        this.m_defaultValue = "";
    }
    
    public AnimationVariableSlotCallbackString(final String s, final CallbackGetStrongTyped callbackGetStrongTyped, final CallbackSetStrongTyped callbackSetStrongTyped) {
        super(s, callbackGetStrongTyped, callbackSetStrongTyped);
        this.m_defaultValue = "";
    }
    
    public AnimationVariableSlotCallbackString(final String s, final String defaultValue, final CallbackGetStrongTyped callbackGetStrongTyped) {
        super(s, callbackGetStrongTyped);
        this.m_defaultValue = "";
        this.m_defaultValue = defaultValue;
    }
    
    public AnimationVariableSlotCallbackString(final String s, final String defaultValue, final CallbackGetStrongTyped callbackGetStrongTyped, final CallbackSetStrongTyped callbackSetStrongTyped) {
        super(s, callbackGetStrongTyped, callbackSetStrongTyped);
        this.m_defaultValue = "";
        this.m_defaultValue = defaultValue;
    }
    
    @Override
    public String getDefaultValue() {
        return this.m_defaultValue;
    }
    
    @Override
    public String getValueString() {
        return this.getValue();
    }
    
    @Override
    public float getValueFloat() {
        return PZMath.tryParseFloat(this.getValue(), 0.0f);
    }
    
    @Override
    public boolean getValueBool() {
        return StringUtils.tryParseBoolean(this.getValue());
    }
    
    @Override
    public void setValue(final String s) {
        this.trySetValue(s);
    }
    
    @Override
    public void setValue(final float f) {
        this.trySetValue(String.valueOf(f));
    }
    
    @Override
    public void setValue(final boolean b) {
        this.trySetValue(b ? "true" : "false");
    }
    
    @Override
    public AnimationVariableType getType() {
        return AnimationVariableType.String;
    }
    
    @Override
    public boolean canConvertFrom(final String s) {
        return true;
    }
    
    public interface CallbackSetStrongTyped extends CallbackSet<String>
    {
    }
    
    public interface CallbackGetStrongTyped extends CallbackGet<String>
    {
    }
}

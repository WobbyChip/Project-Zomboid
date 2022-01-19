// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.advancedanimation;

import zombie.util.StringUtils;

public final class AnimationVariableSlotBool extends AnimationVariableSlot
{
    private boolean m_value;
    
    public AnimationVariableSlotBool(final String s) {
        super(s);
    }
    
    @Override
    public String getValueString() {
        return this.m_value ? "true" : "false";
    }
    
    @Override
    public float getValueFloat() {
        return this.m_value ? 1.0f : 0.0f;
    }
    
    @Override
    public boolean getValueBool() {
        return this.m_value;
    }
    
    @Override
    public void setValue(final String s) {
        this.m_value = StringUtils.tryParseBoolean(s);
    }
    
    @Override
    public void setValue(final float n) {
        this.m_value = (n != 0.0);
    }
    
    @Override
    public void setValue(final boolean value) {
        this.m_value = value;
    }
    
    @Override
    public AnimationVariableType getType() {
        return AnimationVariableType.Boolean;
    }
    
    @Override
    public boolean canConvertFrom(final String s) {
        return StringUtils.isBoolean(s);
    }
    
    @Override
    public void clear() {
        this.m_value = false;
    }
}

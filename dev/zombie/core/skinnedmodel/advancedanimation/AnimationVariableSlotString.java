// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.advancedanimation;

import zombie.util.StringUtils;
import zombie.core.math.PZMath;

public final class AnimationVariableSlotString extends AnimationVariableSlot
{
    private String m_value;
    
    public AnimationVariableSlotString(final String s) {
        super(s);
    }
    
    @Override
    public String getValueString() {
        return this.m_value;
    }
    
    @Override
    public float getValueFloat() {
        return PZMath.tryParseFloat(this.m_value, 0.0f);
    }
    
    @Override
    public boolean getValueBool() {
        return StringUtils.tryParseBoolean(this.m_value);
    }
    
    @Override
    public void setValue(final String value) {
        this.m_value = value;
    }
    
    @Override
    public void setValue(final float f) {
        this.m_value = String.valueOf(f);
    }
    
    @Override
    public void setValue(final boolean b) {
        this.m_value = (b ? "true" : "false");
    }
    
    @Override
    public AnimationVariableType getType() {
        return AnimationVariableType.String;
    }
    
    @Override
    public boolean canConvertFrom(final String s) {
        return true;
    }
    
    @Override
    public void clear() {
        this.m_value = "";
    }
}

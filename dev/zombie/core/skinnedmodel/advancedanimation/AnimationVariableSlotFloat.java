// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.advancedanimation;

import zombie.core.math.PZMath;

public final class AnimationVariableSlotFloat extends AnimationVariableSlot
{
    private float m_value;
    
    public AnimationVariableSlotFloat(final String s) {
        super(s);
        this.m_value = 0.0f;
    }
    
    @Override
    public String getValueString() {
        return String.valueOf(this.m_value);
    }
    
    @Override
    public float getValueFloat() {
        return this.m_value;
    }
    
    @Override
    public boolean getValueBool() {
        return this.m_value != 0.0f;
    }
    
    @Override
    public void setValue(final String s) {
        this.m_value = PZMath.tryParseFloat(s, 0.0f);
    }
    
    @Override
    public void setValue(final float value) {
        this.m_value = value;
    }
    
    @Override
    public void setValue(final boolean b) {
        this.m_value = (b ? 1.0f : 0.0f);
    }
    
    @Override
    public AnimationVariableType getType() {
        return AnimationVariableType.Float;
    }
    
    @Override
    public boolean canConvertFrom(final String s) {
        return PZMath.canParseFloat(s);
    }
    
    @Override
    public void clear() {
        this.m_value = 0.0f;
    }
}

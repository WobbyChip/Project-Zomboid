// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.advancedanimation;

import zombie.debug.DebugLog;

public final class AnimationVariableGenericSlot extends AnimationVariableSlot
{
    private AnimationVariableType m_type;
    private IAnimationVariableSlot m_valueSlot;
    
    public AnimationVariableGenericSlot(final String s) {
        super(s);
        this.m_type = AnimationVariableType.Void;
    }
    
    @Override
    public String getValueString() {
        return (this.m_valueSlot != null) ? this.m_valueSlot.getValueString() : null;
    }
    
    @Override
    public float getValueFloat() {
        return (this.m_valueSlot != null) ? this.m_valueSlot.getValueFloat() : 0.0f;
    }
    
    @Override
    public boolean getValueBool() {
        return this.m_valueSlot != null && this.m_valueSlot.getValueBool();
    }
    
    @Override
    public void setValue(final String value) {
        if (this.m_valueSlot == null || !this.m_valueSlot.canConvertFrom(value)) {
            this.m_valueSlot = new AnimationVariableSlotString(this.getKey());
            this.setType(this.m_valueSlot.getType());
        }
        this.m_valueSlot.setValue(value);
    }
    
    @Override
    public void setValue(final float value) {
        if (this.m_valueSlot == null || this.m_type != AnimationVariableType.Float) {
            this.m_valueSlot = new AnimationVariableSlotFloat(this.getKey());
            this.setType(this.m_valueSlot.getType());
        }
        this.m_valueSlot.setValue(value);
    }
    
    @Override
    public void setValue(final boolean value) {
        if (this.m_valueSlot == null || this.m_type != AnimationVariableType.Boolean) {
            this.m_valueSlot = new AnimationVariableSlotBool(this.getKey());
            this.setType(this.m_valueSlot.getType());
        }
        this.m_valueSlot.setValue(value);
    }
    
    @Override
    public AnimationVariableType getType() {
        return this.m_type;
    }
    
    private void setType(final AnimationVariableType type) {
        if (this.m_type == type) {
            return;
        }
        if (this.m_type != AnimationVariableType.Void) {
            DebugLog.General.printf("Variable %s converting from %s to %s\n", this.getKey(), this.m_type, type);
        }
        this.m_type = type;
    }
    
    @Override
    public boolean canConvertFrom(final String s) {
        return true;
    }
    
    @Override
    public void clear() {
        this.m_type = AnimationVariableType.Void;
        this.m_valueSlot = null;
    }
}

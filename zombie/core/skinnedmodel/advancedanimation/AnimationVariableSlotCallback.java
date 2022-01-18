// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.advancedanimation;

import zombie.debug.DebugLog;

public abstract class AnimationVariableSlotCallback<VariableType> extends AnimationVariableSlot
{
    private final CallbackGet<VariableType> m_callbackGet;
    private final CallbackSet<VariableType> m_callbackSet;
    
    protected AnimationVariableSlotCallback(final String s, final CallbackGet<VariableType> callbackGet) {
        this(s, callbackGet, null);
    }
    
    protected AnimationVariableSlotCallback(final String s, final CallbackGet<VariableType> callbackGet, final CallbackSet<VariableType> callbackSet) {
        super(s);
        this.m_callbackGet = callbackGet;
        this.m_callbackSet = callbackSet;
    }
    
    public VariableType getValue() {
        return this.m_callbackGet.call();
    }
    
    public abstract VariableType getDefaultValue();
    
    public boolean trySetValue(final VariableType variableType) {
        if (this.isReadOnly()) {
            DebugLog.General.warn("Trying to set read-only variable \"%s\"", super.getKey());
            return false;
        }
        this.m_callbackSet.call(variableType);
        return true;
    }
    
    @Override
    public boolean isReadOnly() {
        return this.m_callbackSet == null;
    }
    
    @Override
    public void clear() {
        if (!this.isReadOnly()) {
            this.trySetValue(this.getDefaultValue());
        }
    }
    
    public interface CallbackGet<VariableType>
    {
        VariableType call();
    }
    
    public interface CallbackSet<VariableType>
    {
        void call(final VariableType p0);
    }
}

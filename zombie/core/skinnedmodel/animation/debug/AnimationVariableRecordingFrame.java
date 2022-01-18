// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.animation.debug;

import zombie.debug.DebugLog;
import zombie.util.list.PZArrayUtil;
import java.util.Iterator;
import zombie.core.skinnedmodel.advancedanimation.IAnimationVariableSlot;
import zombie.core.skinnedmodel.advancedanimation.IAnimationVariableSource;

public final class AnimationVariableRecordingFrame extends GenericNameValueRecordingFrame
{
    private String[] m_variableValues;
    
    public AnimationVariableRecordingFrame(final String s) {
        super(s, "_values");
        this.m_variableValues = new String[0];
    }
    
    public void logVariables(final IAnimationVariableSource animationVariableSource) {
        for (final IAnimationVariableSlot animationVariableSlot : animationVariableSource.getGameVariables()) {
            this.logVariable(animationVariableSlot.getKey(), animationVariableSlot.getValueString());
        }
    }
    
    @Override
    protected void onColumnAdded() {
        this.m_variableValues = PZArrayUtil.add(this.m_variableValues, null);
    }
    
    public void logVariable(final String s, final String s2) {
        final int orCreateColumn = this.getOrCreateColumn(s);
        if (this.m_variableValues[orCreateColumn] != null) {
            DebugLog.General.error("Value for %s already set: %f, new value: %f", s, this.m_variableValues[orCreateColumn], s2);
        }
        this.m_variableValues[orCreateColumn] = s2;
    }
    
    @Override
    public String getValueAt(final int n) {
        return this.m_variableValues[n];
    }
    
    @Override
    public void reset() {
        for (int i = 0; i < this.m_variableValues.length; ++i) {
            this.m_variableValues[i] = null;
        }
    }
}

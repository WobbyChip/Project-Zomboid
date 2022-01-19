// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.advancedanimation;

public class AnimationVariableHandle
{
    private String m_name;
    private int m_varIndex;
    
    AnimationVariableHandle() {
        this.m_name = null;
        this.m_varIndex = -1;
    }
    
    public static AnimationVariableHandle alloc(final String s) {
        return AnimationVariableHandlePool.getOrCreate(s);
    }
    
    public String getVariableName() {
        return this.m_name;
    }
    
    public int getVariableIndex() {
        return this.m_varIndex;
    }
    
    void setVariableName(final String name) {
        this.m_name = name;
    }
    
    void setVariableIndex(final int varIndex) {
        this.m_varIndex = varIndex;
    }
}

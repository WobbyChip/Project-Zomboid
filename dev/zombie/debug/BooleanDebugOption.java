// 
// Decompiled by Procyon v0.5.36
// 

package zombie.debug;

import zombie.core.Core;
import zombie.debug.options.IDebugOptionGroup;
import zombie.debug.options.IDebugOption;
import zombie.config.BooleanConfigOption;

public class BooleanDebugOption extends BooleanConfigOption implements IDebugOption
{
    private IDebugOptionGroup m_parent;
    private final boolean m_debugOnly;
    
    public BooleanDebugOption(final String s, final boolean debugOnly, final boolean b) {
        super(s, b);
        this.m_debugOnly = debugOnly;
    }
    
    @Override
    public boolean getValue() {
        if (Core.bDebug || !this.isDebugOnly()) {
            return super.getValue();
        }
        return super.getDefaultValue();
    }
    
    public boolean isDebugOnly() {
        return this.m_debugOnly;
    }
    
    @Override
    public IDebugOptionGroup getParent() {
        return this.m_parent;
    }
    
    @Override
    public void setParent(final IDebugOptionGroup parent) {
        this.m_parent = parent;
    }
}

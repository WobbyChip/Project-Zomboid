// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.profiling;

import zombie.GameProfiler;
import java.util.Stack;

public class PerformanceProfileProbe extends AbstractPerformanceProfileProbe
{
    private final Stack<GameProfiler.ProfileArea> m_currentArea;
    
    public PerformanceProfileProbe(final String s) {
        super(s);
        this.m_currentArea = new Stack<GameProfiler.ProfileArea>();
    }
    
    public PerformanceProfileProbe(final String s, final boolean enabled) {
        super(s);
        this.m_currentArea = new Stack<GameProfiler.ProfileArea>();
        this.setEnabled(enabled);
    }
    
    @Override
    protected void onStart() {
        this.m_currentArea.push(GameProfiler.getInstance().start(this.Name));
    }
    
    @Override
    protected void onEnd() {
        GameProfiler.getInstance().end(this.m_currentArea.pop());
    }
}

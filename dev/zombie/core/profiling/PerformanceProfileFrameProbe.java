// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.profiling;

import zombie.GameProfiler;

public class PerformanceProfileFrameProbe extends PerformanceProfileProbe
{
    public PerformanceProfileFrameProbe(final String s) {
        super(s);
    }
    
    @Override
    protected void onStart() {
        GameProfiler.getInstance().startFrame(this.Name);
        super.onStart();
    }
    
    @Override
    protected void onEnd() {
        super.onEnd();
        GameProfiler.getInstance().endFrame();
    }
}

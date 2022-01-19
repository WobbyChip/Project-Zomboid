// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.profiling;

import zombie.util.lambda.Stacks;
import zombie.util.Lambda;
import zombie.util.lambda.Invokers;
import zombie.GameProfiler;

public abstract class AbstractPerformanceProfileProbe
{
    public final String Name;
    private boolean m_isEnabled;
    private boolean m_isRunning;
    private boolean m_isProfilerRunning;
    
    protected AbstractPerformanceProfileProbe(final String name) {
        this.m_isEnabled = true;
        this.m_isRunning = false;
        this.m_isProfilerRunning = false;
        this.Name = name;
    }
    
    protected abstract void onStart();
    
    protected abstract void onEnd();
    
    public void start() {
        if (this.m_isRunning) {
            throw new RuntimeException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()));
        }
        if (!(this.m_isProfilerRunning = (this.isEnabled() && GameProfiler.isRunning()))) {
            return;
        }
        this.m_isRunning = true;
        this.onStart();
    }
    
    public boolean isEnabled() {
        return this.m_isEnabled;
    }
    
    public void setEnabled(final boolean isEnabled) {
        this.m_isEnabled = isEnabled;
    }
    
    public void end() {
        if (!this.m_isProfilerRunning) {
            return;
        }
        if (!this.m_isRunning) {
            throw new RuntimeException("end() called without calling start().");
        }
        this.onEnd();
        this.m_isRunning = false;
    }
    
    public void invokeAndMeasure(final Runnable runnable) {
        try {
            this.start();
            runnable.run();
        }
        finally {
            this.end();
        }
    }
    
    public <T1> void invokeAndMeasure(final T1 t1, final Invokers.Params1.ICallback<T1> callback) {
        Lambda.capture(this, t1, callback, (genericStack, abstractPerformanceProfileProbe, o, callback2) -> abstractPerformanceProfileProbe.invokeAndMeasure(genericStack.invoker(o, callback2)));
    }
    
    public <T1, T2> void invokeAndMeasure(final T1 t1, final T2 t2, final Invokers.Params2.ICallback<T1, T2> callback) {
        Lambda.capture(this, t1, t2, callback, (genericStack, abstractPerformanceProfileProbe, o, o2, callback2) -> abstractPerformanceProfileProbe.invokeAndMeasure(genericStack.invoker(o, o2, callback2)));
    }
}

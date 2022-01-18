// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.profiling;

import zombie.util.list.PZArrayUtil;

public class PerformanceProfileProbeList<Probe extends PerformanceProfileProbe>
{
    final String m_prefix;
    final Probe[] layers;
    
    public static PerformanceProfileProbeList<PerformanceProfileProbe> construct(final String s, final int n) {
        return new PerformanceProfileProbeList<PerformanceProfileProbe>(s, n, PerformanceProfileProbe.class, PerformanceProfileProbe::new);
    }
    
    public static <Probe extends PerformanceProfileProbe> PerformanceProfileProbeList<Probe> construct(final String s, final int n, final Class<Probe> clazz, final Constructor<Probe> constructor) {
        return new PerformanceProfileProbeList<Probe>(s, n, clazz, constructor);
    }
    
    protected PerformanceProfileProbeList(final String prefix, final int n, final Class<Probe> clazz, final Constructor<Probe> constructor) {
        this.m_prefix = prefix;
        this.layers = PZArrayUtil.newInstance(clazz, n + 1);
        for (int i = 0; i < n; ++i) {
            this.layers[i] = constructor.get(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, prefix, i));
        }
        this.layers[n] = constructor.get(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, prefix));
    }
    
    public int count() {
        return this.layers.length;
    }
    
    public Probe at(final int n) {
        if (n < this.count()) {
            return this.layers[n];
        }
        return this.layers[this.count() - 1];
    }
    
    public Probe start(final int n) {
        final PerformanceProfileProbe at = this.at(n);
        at.start();
        return (Probe)at;
    }
    
    public interface Constructor<Probe extends PerformanceProfileProbe>
    {
        Probe get(final String p0);
    }
}

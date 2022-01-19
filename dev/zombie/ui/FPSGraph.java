// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ui;

import zombie.core.textures.Texture;
import zombie.core.SpriteRenderer;
import zombie.core.PerformanceSettings;
import java.util.ArrayList;
import zombie.input.Mouse;
import zombie.core.Core;

public final class FPSGraph extends UIElement
{
    public static FPSGraph instance;
    private static final int NUM_BARS = 30;
    private static final int BAR_WID = 8;
    private final Graph fpsGraph;
    private final Graph upsGraph;
    private final Graph lpsGraph;
    private final Graph uiGraph;
    
    public FPSGraph() {
        this.fpsGraph = new Graph();
        this.upsGraph = new Graph();
        this.lpsGraph = new Graph();
        this.uiGraph = new Graph();
        this.setVisible(false);
    }
    
    public void addRender(final long n) {
        synchronized (this.fpsGraph) {
            this.fpsGraph.add(n);
        }
    }
    
    public void addUpdate(final long n) {
        this.upsGraph.add(n);
    }
    
    public void addLighting(final long n) {
        synchronized (this.lpsGraph) {
            this.lpsGraph.add(n);
        }
    }
    
    public void addUI(final long n) {
        this.uiGraph.add(n);
    }
    
    @Override
    public void update() {
        if (!this.isVisible()) {
            return;
        }
        this.setHeight(108.0);
        this.setWidth(232.0);
        this.setX(20.0);
        this.setY(Core.getInstance().getScreenHeight() - 20 - this.getHeight());
        super.update();
    }
    
    @Override
    public void render() {
        if (!this.isVisible()) {
            return;
        }
        if (!UIManager.VisibleAllUI) {
            return;
        }
        final int n = this.getHeight().intValue() - 4;
        int n2 = -1;
        if (this.isMouseOver()) {
            this.DrawTextureScaledCol(UIElement.white, 0.0, 0.0, this.getWidth(), this.getHeight(), 0.0, 0.20000000298023224, 0.0, 0.5);
            n2 = (Mouse.getXA() - this.getAbsoluteX().intValue()) / 8;
        }
        synchronized (this.fpsGraph) {
            this.fpsGraph.render(0.0f, 1.0f, 0.0f);
            if (n2 >= 0 && n2 < this.fpsGraph.bars.size()) {
                this.DrawText(invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;)Ljava/lang/String;, this.fpsGraph.bars.get(n2)), 20.0, (double)(n / 2 - 10), 0.0, 1.0, 0.0, 1.0);
            }
        }
        synchronized (this.lpsGraph) {
            this.lpsGraph.render(1.0f, 1.0f, 0.0f);
            if (n2 >= 0 && n2 < this.lpsGraph.bars.size()) {
                this.DrawText(invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;)Ljava/lang/String;, this.lpsGraph.bars.get(n2)), 20.0, (double)(n / 2 + 20), 1.0, 1.0, 0.0, 1.0);
            }
        }
        this.upsGraph.render(0.0f, 1.0f, 1.0f);
        if (n2 >= 0 && n2 < this.upsGraph.bars.size()) {
            this.DrawText(invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;)Ljava/lang/String;, this.upsGraph.bars.get(n2)), 20.0, (double)(n / 2 + 5), 0.0, 1.0, 1.0, 1.0);
            this.DrawTextureScaledCol(UIElement.white, n2 * 8 + 4, 0.0, 1.0, this.getHeight(), 1.0, 1.0, 1.0, 0.5);
        }
        this.uiGraph.render(1.0f, 0.0f, 1.0f);
        if (n2 >= 0 && n2 < this.uiGraph.bars.size()) {
            this.DrawText(invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;)Ljava/lang/String;, this.uiGraph.bars.get(n2)), 20.0, (double)(n / 2 - 26), 1.0, 0.0, 1.0, 1.0);
        }
    }
    
    private final class Graph
    {
        private final ArrayList<Long> times;
        private final ArrayList<Integer> bars;
        
        private Graph() {
            this.times = new ArrayList<Long>();
            this.bars = new ArrayList<Integer>();
        }
        
        public void add(final long l) {
            this.times.add(l);
            this.bars.clear();
            long n = this.times.get(0);
            int i = 1;
            for (int j = 1; j < this.times.size(); ++j) {
                if (j == this.times.size() - 1 || this.times.get(j) - n > 1000L) {
                    final long n2 = (this.times.get(j) - n) / 1000L - 1L;
                    for (int n3 = 0; n3 < n2; ++n3) {
                        this.bars.add(0);
                    }
                    this.bars.add(i);
                    i = 1;
                    n = this.times.get(j);
                }
                else {
                    ++i;
                }
            }
            while (this.bars.size() > 30) {
                for (int intValue = this.bars.get(0), k = 0; k < intValue; ++k) {
                    this.times.remove(0);
                }
                this.bars.remove(0);
            }
        }
        
        public void render(final float n, final float n2, final float n3) {
            if (this.bars.isEmpty()) {
                return;
            }
            final float n4 = (float)(FPSGraph.this.getHeight().intValue() - 4);
            final float n5 = (float)(FPSGraph.this.getHeight().intValue() - 2);
            final int max = Math.max(PerformanceSettings.getLockFPS(), PerformanceSettings.LightingFPS);
            int n6 = 8;
            float n7 = n4 * (Math.min(max, this.bars.get(0)) / (float)max);
            for (int i = 1; i < this.bars.size() - 1; ++i) {
                final float n8 = n4 * (Math.min(max, this.bars.get(i)) / (float)max);
                SpriteRenderer.instance.renderline(null, FPSGraph.this.getAbsoluteX().intValue() + n6 - 8 + 4, FPSGraph.this.getAbsoluteY().intValue() + (int)(n5 - n7), FPSGraph.this.getAbsoluteX().intValue() + n6 + 4, FPSGraph.this.getAbsoluteY().intValue() + (int)(n5 - n8), n, n2, n3, 0.35f, 1);
                n6 += 8;
                n7 = n8;
            }
        }
    }
}

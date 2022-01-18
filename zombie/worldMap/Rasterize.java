// 
// Decompiled by Procyon v0.5.36
// 

package zombie.worldMap;

import java.util.function.BiConsumer;

public final class Rasterize
{
    final Edge edge1;
    final Edge edge2;
    final Edge edge3;
    
    public Rasterize() {
        this.edge1 = new Edge();
        this.edge2 = new Edge();
        this.edge3 = new Edge();
    }
    
    void scanLine(final int n, final int n2, final int i, final BiConsumer<Integer, Integer> biConsumer) {
        for (int j = n; j < n2; ++j) {
            biConsumer.accept(j, i);
        }
    }
    
    void scanSpan(Edge edge, Edge edge2, final int n, final int n2, final BiConsumer<Integer, Integer> biConsumer) {
        final int n3 = (int)Math.max(n, Math.floor(edge2.y0));
        final int n4 = (int)Math.min(n2, Math.ceil(edge2.y1));
        if (edge.x0 == edge2.x0 && edge.y0 == edge2.y0) {
            if (edge.x0 + edge2.dy / edge.dy * edge.dx < edge2.x1) {
                final Edge edge3 = edge;
                edge = edge2;
                edge2 = edge3;
            }
        }
        else if (edge.x1 - edge2.dy / edge.dy * edge.dx < edge2.x0) {
            final Edge edge4 = edge;
            edge = edge2;
            edge2 = edge4;
        }
        final double n5 = edge.dx / edge.dy;
        final double n6 = edge2.dx / edge2.dy;
        final double n7 = (edge.dx > 0.0f) ? 1.0 : 0.0;
        final double n8 = (edge2.dx < 0.0f) ? 1.0 : 0.0;
        for (int i = n3; i < n4; ++i) {
            this.scanLine((int)Math.floor(n6 * Math.max(0.0, Math.min(edge2.dy, i + n8 - edge2.y0)) + edge2.x0), (int)Math.ceil(n5 * Math.max(0.0, Math.min(edge.dy, i + n7 - edge.y0)) + edge.x0), i, biConsumer);
        }
    }
    
    void scanTriangle(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final int n7, final int n8, final BiConsumer<Integer, Integer> biConsumer) {
        Edge init = this.edge1.init(n, n2, n3, n4);
        Edge init2 = this.edge2.init(n3, n4, n5, n6);
        Edge init3 = this.edge3.init(n5, n6, n, n2);
        if (init.dy > init3.dy) {
            final Edge edge = init;
            init = init3;
            init3 = edge;
        }
        if (init2.dy > init3.dy) {
            final Edge edge2 = init2;
            init2 = init3;
            init3 = edge2;
        }
        if (init.dy > 0.0f) {
            this.scanSpan(init3, init, n7, n8, biConsumer);
        }
        if (init2.dy > 0.0f) {
            this.scanSpan(init3, init2, n7, n8, biConsumer);
        }
    }
    
    private static final class Edge
    {
        float x0;
        float y0;
        float x1;
        float y1;
        float dx;
        float dy;
        
        Edge init(final float n, final float n2, final float n3, final float n4) {
            if (n2 > n4) {
                this.x0 = n3;
                this.y0 = n4;
                this.x1 = n;
                this.y1 = n2;
            }
            else {
                this.x0 = n;
                this.y0 = n2;
                this.x1 = n3;
                this.y1 = n4;
            }
            this.dx = this.x1 - this.x0;
            this.dy = this.y1 - this.y0;
            return this;
        }
    }
}

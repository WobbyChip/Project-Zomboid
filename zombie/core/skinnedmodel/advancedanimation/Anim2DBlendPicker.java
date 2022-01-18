// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.advancedanimation;

import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;
import zombie.core.textures.Texture;
import zombie.core.SpriteRenderer;
import zombie.core.Core;
import java.util.Iterator;
import java.util.Comparator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;

public final class Anim2DBlendPicker
{
    private List<Anim2DBlendTriangle> m_tris;
    private List<Anim2DBlend> m_hull;
    private HullComparer m_hullComparer;
    
    public void SetPickTriangles(final List<Anim2DBlendTriangle> tris) {
        this.m_tris = tris;
        this.BuildHull();
    }
    
    private void BuildHull() {
        final HashMap<Edge, Counter> hashMap = new HashMap<Edge, Counter>();
        Counter value = new Counter();
        for (final Anim2DBlendTriangle anim2DBlendTriangle : this.m_tris) {
            Counter counter2 = hashMap.putIfAbsent(new Edge(anim2DBlendTriangle.node1, anim2DBlendTriangle.node2), value);
            if (counter2 == null) {
                counter2 = value;
                value = new Counter();
            }
            counter2.Increment();
            Counter counter3 = hashMap.putIfAbsent(new Edge(anim2DBlendTriangle.node2, anim2DBlendTriangle.node3), value);
            if (counter3 == null) {
                counter3 = value;
                value = new Counter();
            }
            counter3.Increment();
            Counter counter4 = hashMap.putIfAbsent(new Edge(anim2DBlendTriangle.node3, anim2DBlendTriangle.node1), value);
            if (counter4 == null) {
                counter4 = value;
                value = new Counter();
            }
            counter4.Increment();
        }
        final HashSet<Anim2DBlend> c = new HashSet<Anim2DBlend>();
        final HashSet<Anim2DBlend> set;
        hashMap.forEach((edge, counter) -> {
            if (counter.count == 1) {
                set.add(edge.a);
                set.add(edge.b);
            }
            return;
        });
        final ArrayList hull = new ArrayList<Anim2DBlend>(c);
        float n = 0.0f;
        float n2 = 0.0f;
        for (final Anim2DBlend anim2DBlend : hull) {
            n += anim2DBlend.m_XPos;
            n2 += anim2DBlend.m_YPos;
        }
        hull.sort(this.m_hullComparer = new HullComparer(n / hull.size(), n2 / hull.size()));
        this.m_hull = (List<Anim2DBlend>)hull;
    }
    
    static <T> int LowerBoundIdx(final List<T> list, final T t, final Comparator<? super T> comparator) {
        int i = 0;
        int size = list.size();
        while (i != size) {
            final int n = (i + size) / 2;
            if (comparator.compare(t, list.get(n)) < 0) {
                size = n;
            }
            else {
                i = n + 1;
            }
        }
        return i;
    }
    
    private static float ProjectPointToLine(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        final float n7 = n - n3;
        final float n8 = n2 - n4;
        final float n9 = n5 - n3;
        final float n10 = n6 - n4;
        return (n9 * n7 + n10 * n8) / (n9 * n9 + n10 * n10);
    }
    
    public PickResults Pick(float xPos, float yPos) {
        final PickResults pickResults = new PickResults();
        for (final Anim2DBlendTriangle anim2DBlendTriangle : this.m_tris) {
            if (anim2DBlendTriangle.Contains(xPos, yPos)) {
                pickResults.numNodes = 3;
                pickResults.node1 = anim2DBlendTriangle.node1;
                pickResults.node2 = anim2DBlendTriangle.node2;
                pickResults.node3 = anim2DBlendTriangle.node3;
                final float xPos2 = pickResults.node1.m_XPos;
                final float yPos2 = pickResults.node1.m_YPos;
                final float xPos3 = pickResults.node2.m_XPos;
                final float yPos3 = pickResults.node2.m_YPos;
                final float xPos4 = pickResults.node3.m_XPos;
                final float yPos4 = pickResults.node3.m_YPos;
                pickResults.scale1 = ((yPos3 - yPos4) * (xPos - xPos4) + (xPos4 - xPos3) * (yPos - yPos4)) / ((yPos3 - yPos4) * (xPos2 - xPos4) + (xPos4 - xPos3) * (yPos2 - yPos4));
                pickResults.scale2 = ((yPos4 - yPos2) * (xPos - xPos4) + (xPos2 - xPos4) * (yPos - yPos4)) / ((yPos3 - yPos4) * (xPos2 - xPos4) + (xPos4 - xPos3) * (yPos2 - yPos4));
                pickResults.scale3 = 1.0f - pickResults.scale1 - pickResults.scale2;
                return pickResults;
            }
        }
        xPos *= 1.1f;
        yPos *= 1.1f;
        final Anim2DBlend anim2DBlend = new Anim2DBlend();
        anim2DBlend.m_XPos = xPos;
        anim2DBlend.m_YPos = yPos;
        int lowerBoundIdx = LowerBoundIdx(this.m_hull, anim2DBlend, this.m_hullComparer);
        if (lowerBoundIdx == this.m_hull.size()) {
            lowerBoundIdx = 0;
        }
        final int n = (lowerBoundIdx > 0) ? (lowerBoundIdx - 1) : (this.m_hull.size() - 1);
        final Anim2DBlend anim2DBlend2 = this.m_hull.get(lowerBoundIdx);
        final Anim2DBlend anim2DBlend3 = this.m_hull.get(n);
        final float projectPointToLine = ProjectPointToLine(xPos, yPos, anim2DBlend2.m_XPos, anim2DBlend2.m_YPos, anim2DBlend3.m_XPos, anim2DBlend3.m_YPos);
        if (projectPointToLine < 0.0f) {
            pickResults.numNodes = 1;
            pickResults.node1 = anim2DBlend2;
            pickResults.scale1 = 1.0f;
        }
        else if (projectPointToLine > 1.0f) {
            pickResults.numNodes = 1;
            pickResults.node1 = anim2DBlend3;
            pickResults.scale1 = 1.0f;
        }
        else {
            pickResults.numNodes = 2;
            pickResults.node1 = anim2DBlend2;
            pickResults.node2 = anim2DBlend3;
            pickResults.scale1 = 1.0f - projectPointToLine;
            pickResults.scale2 = projectPointToLine;
        }
        return pickResults;
    }
    
    void render(final float n, final float n2) {
        final int n3 = 200;
        final int n4 = Core.getInstance().getScreenWidth() - n3 - 100;
        final int n5 = Core.getInstance().getScreenHeight() - n3 - 100;
        SpriteRenderer.instance.renderi(null, n4 - 20, n5 - 20, n3 + 40, n3 + 40, 1.0f, 1.0f, 1.0f, 1.0f, null);
        for (int i = 0; i < this.m_tris.size(); ++i) {
            final Anim2DBlendTriangle anim2DBlendTriangle = this.m_tris.get(i);
            SpriteRenderer.instance.renderline(null, (int)(n4 + n3 / 2 + anim2DBlendTriangle.node1.m_XPos * n3 / 2.0f), (int)(n5 + n3 / 2 - anim2DBlendTriangle.node1.m_YPos * n3 / 2.0f), (int)(n4 + n3 / 2 + anim2DBlendTriangle.node2.m_XPos * n3 / 2.0f), (int)(n5 + n3 / 2 - anim2DBlendTriangle.node2.m_YPos * n3 / 2.0f), 0.5f, 0.5f, 0.5f, 1.0f);
            SpriteRenderer.instance.renderline(null, (int)(n4 + n3 / 2 + anim2DBlendTriangle.node2.m_XPos * n3 / 2.0f), (int)(n5 + n3 / 2 - anim2DBlendTriangle.node2.m_YPos * n3 / 2.0f), (int)(n4 + n3 / 2 + anim2DBlendTriangle.node3.m_XPos * n3 / 2.0f), (int)(n5 + n3 / 2 - anim2DBlendTriangle.node3.m_YPos * n3 / 2.0f), 0.5f, 0.5f, 0.5f, 1.0f);
            SpriteRenderer.instance.renderline(null, (int)(n4 + n3 / 2 + anim2DBlendTriangle.node3.m_XPos * n3 / 2.0f), (int)(n5 + n3 / 2 - anim2DBlendTriangle.node3.m_YPos * n3 / 2.0f), (int)(n4 + n3 / 2 + anim2DBlendTriangle.node1.m_XPos * n3 / 2.0f), (int)(n5 + n3 / 2 - anim2DBlendTriangle.node1.m_YPos * n3 / 2.0f), 0.5f, 0.5f, 0.5f, 1.0f);
        }
        final float n6 = 8.0f;
        final PickResults pick = this.Pick(n, n2);
        if (pick.node1 != null) {
            SpriteRenderer.instance.render(null, n4 + n3 / 2 + pick.node1.m_XPos * n3 / 2.0f - n6 / 2.0f, n5 + n3 / 2 - pick.node1.m_YPos * n3 / 2.0f - n6 / 2.0f, n6, n6, 0.0f, 1.0f, 0.0f, 1.0f, null);
        }
        if (pick.node2 != null) {
            SpriteRenderer.instance.render(null, n4 + n3 / 2 + pick.node2.m_XPos * n3 / 2.0f - n6 / 2.0f, n5 + n3 / 2 - pick.node2.m_YPos * n3 / 2.0f - n6 / 2.0f, n6, n6, 0.0f, 1.0f, 0.0f, 1.0f, null);
        }
        if (pick.node3 != null) {
            SpriteRenderer.instance.render(null, n4 + n3 / 2 + pick.node3.m_XPos * n3 / 2.0f - n6 / 2.0f, n5 + n3 / 2 - pick.node3.m_YPos * n3 / 2.0f - n6 / 2.0f, n6, n6, 0.0f, 1.0f, 0.0f, 1.0f, null);
        }
        final float n7 = 4.0f;
        SpriteRenderer.instance.render(null, n4 + n3 / 2 + n * n3 / 2.0f - n7 / 2.0f, n5 + n3 / 2 - n2 * n3 / 2.0f - n7 / 2.0f, n7, n7, 0.0f, 0.0f, 1.0f, 1.0f, null);
    }
    
    public static class PickResults
    {
        public int numNodes;
        public Anim2DBlend node1;
        public Anim2DBlend node2;
        public Anim2DBlend node3;
        public float scale1;
        public float scale2;
        public float scale3;
    }
    
    static class Edge
    {
        public Anim2DBlend a;
        public Anim2DBlend b;
        
        public Edge(final Anim2DBlend anim2DBlend, final Anim2DBlend anim2DBlend2) {
            boolean b;
            if (anim2DBlend.m_XPos != anim2DBlend2.m_XPos) {
                b = (anim2DBlend.m_XPos > anim2DBlend2.m_XPos);
            }
            else {
                b = (anim2DBlend.m_YPos > anim2DBlend2.m_YPos);
            }
            if (b) {
                this.a = anim2DBlend2;
                this.b = anim2DBlend;
            }
            else {
                this.a = anim2DBlend;
                this.b = anim2DBlend2;
            }
        }
        
        @Override
        public int hashCode() {
            final int hashCode = this.a.hashCode();
            return (hashCode << 5) + hashCode ^ this.b.hashCode();
        }
        
        @Override
        public boolean equals(final Object o) {
            return o instanceof Edge && this.a == ((Edge)o).a && this.b == ((Edge)o).b;
        }
    }
    
    static class HullComparer implements Comparator<Anim2DBlend>
    {
        private int centerX;
        private int centerY;
        
        public HullComparer(final float n, final float n2) {
            this.centerX = (int)(n * 1000.0f);
            this.centerY = (int)(n2 * 1000.0f);
        }
        
        public boolean isLessThan(final Anim2DBlend anim2DBlend, final Anim2DBlend anim2DBlend2) {
            final int n = (int)(anim2DBlend.m_XPos * 1000.0f);
            final int n2 = (int)(anim2DBlend.m_YPos * 1000.0f);
            final int n3 = (int)(anim2DBlend2.m_XPos * 1000.0f);
            final int n4 = (int)(anim2DBlend2.m_YPos * 1000.0f);
            final int n5 = n - this.centerX;
            final int n6 = n2 - this.centerY;
            final int n7 = n3 - this.centerX;
            final int n8 = n4 - this.centerY;
            return (n6 == 0 && n5 > 0) || ((n8 != 0 || n7 <= 0) && ((n6 > 0 && n8 < 0) || ((n6 >= 0 || n8 <= 0) && n5 * n8 - n6 * n7 > 0)));
        }
        
        @Override
        public int compare(final Anim2DBlend anim2DBlend, final Anim2DBlend anim2DBlend2) {
            if (this.isLessThan(anim2DBlend, anim2DBlend2)) {
                return -1;
            }
            if (this.isLessThan(anim2DBlend2, anim2DBlend)) {
                return 1;
            }
            return 0;
        }
    }
    
    static class Counter
    {
        public int count;
        
        Counter() {
            this.count = 0;
        }
        
        public int Increment() {
            return ++this.count;
        }
    }
}

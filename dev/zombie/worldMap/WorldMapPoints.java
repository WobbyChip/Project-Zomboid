// 
// Decompiled by Procyon v0.5.36
// 

package zombie.worldMap;

import zombie.core.math.PZMath;
import gnu.trove.list.array.TIntArrayList;

public final class WorldMapPoints extends TIntArrayList
{
    int m_minX;
    int m_minY;
    int m_maxX;
    int m_maxY;
    
    public int numPoints() {
        return this.size() / 2;
    }
    
    public int getX(final int n) {
        return this.get(n * 2);
    }
    
    public int getY(final int n) {
        return this.get(n * 2 + 1);
    }
    
    public void calculateBounds() {
        final int n = Integer.MAX_VALUE;
        this.m_minY = n;
        this.m_minX = n;
        final int n2 = Integer.MIN_VALUE;
        this.m_maxY = n2;
        this.m_maxX = n2;
        for (int i = 0; i < this.numPoints(); ++i) {
            final int x = this.getX(i);
            final int y = this.getY(i);
            this.m_minX = PZMath.min(this.m_minX, x);
            this.m_minY = PZMath.min(this.m_minY, y);
            this.m_maxX = PZMath.max(this.m_maxX, x);
            this.m_maxY = PZMath.max(this.m_maxY, y);
        }
    }
    
    public boolean isClockwise() {
        float n = 0.0f;
        for (int i = 0; i < this.numPoints(); ++i) {
            n += (this.getX((i + 1) % this.numPoints()) - this.getX(i)) * (this.getY((i + 1) % this.numPoints()) + this.getY(i));
        }
        return n > 0.0;
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.worldMap.symbols;

import gnu.trove.list.array.TByteArrayList;
import gnu.trove.list.array.TFloatArrayList;

public final class WorldMapSymbolCollisions
{
    final TFloatArrayList m_boxes;
    final TByteArrayList m_collide;
    
    public WorldMapSymbolCollisions() {
        this.m_boxes = new TFloatArrayList();
        this.m_collide = new TByteArrayList();
    }
    
    boolean addBox(float n, float n2, final float n3, final float n4, final boolean b) {
        final int n5 = this.m_boxes.size() / 4 - 1;
        final int n6 = n5 + 1;
        this.m_boxes.add(n);
        this.m_boxes.add(n2);
        this.m_boxes.add(n + n3);
        this.m_boxes.add(n2 + n4);
        this.m_collide.add((byte)(byte)(b ? 1 : 0));
        if (!b) {
            return false;
        }
        for (int i = 0; i <= n5; ++i) {
            if (this.isCollision(i, n6)) {
                n += n3 / 2.0f;
                n2 += n4 / 2.0f;
                this.m_boxes.set(n6 * 4, n - 3.0f - 1.0f);
                this.m_boxes.set(n6 * 4 + 1, n2 - 3.0f - 1.0f);
                this.m_boxes.set(n6 * 4 + 2, n + 3.0f + 1.0f);
                this.m_boxes.set(n6 * 4 + 3, n2 - 3.0f + 1.0f);
                return true;
            }
        }
        return false;
    }
    
    boolean isCollision(int n, int n2) {
        if (this.m_collide.getQuick(n) == 0 || this.m_collide.getQuick(n2) == 0) {
            return false;
        }
        n *= 4;
        n2 *= 4;
        final float value = this.m_boxes.get(n);
        final float value2 = this.m_boxes.get(n + 1);
        final float value3 = this.m_boxes.get(n + 2);
        final float value4 = this.m_boxes.get(n + 3);
        final float value5 = this.m_boxes.get(n2);
        final float value6 = this.m_boxes.get(n2 + 1);
        final float value7 = this.m_boxes.get(n2 + 2);
        final float value8 = this.m_boxes.get(n2 + 3);
        return value < value7 && value3 > value5 && value2 < value8 && value4 > value6;
    }
    
    boolean isCollision(final int n) {
        for (int i = 0; i < this.m_boxes.size() / 4; ++i) {
            if (i != n && this.isCollision(n, i)) {
                return true;
            }
        }
        return false;
    }
}

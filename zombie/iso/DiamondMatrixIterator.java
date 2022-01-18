// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import org.joml.Vector2i;

public class DiamondMatrixIterator
{
    private int size;
    private int lineSize;
    private int line;
    private int column;
    
    public DiamondMatrixIterator(final int size) {
        this.size = size;
        this.lineSize = 1;
        this.line = 0;
        this.column = 0;
    }
    
    public DiamondMatrixIterator reset(final int size) {
        this.size = size;
        this.lineSize = 1;
        this.line = 0;
        this.column = 0;
        return this;
    }
    
    public void reset() {
        this.lineSize = 1;
        this.line = 0;
        this.column = 0;
    }
    
    public boolean next(final Vector2i vector2i) {
        if (this.lineSize == 0) {
            vector2i.x = 0;
            vector2i.y = 0;
            return false;
        }
        if (this.line == 0 && this.column == 0) {
            vector2i.set(0, 0);
            ++this.column;
            return true;
        }
        if (this.column < this.lineSize) {
            ++vector2i.x;
            --vector2i.y;
            ++this.column;
        }
        else {
            this.column = 1;
            ++this.line;
            if (this.line < this.size) {
                ++this.lineSize;
                vector2i.x = 0;
                vector2i.y = this.line;
            }
            else {
                --this.lineSize;
                vector2i.x = this.line - this.size + 1;
                vector2i.y = this.size - 1;
            }
        }
        if (this.lineSize == 0) {
            vector2i.x = 0;
            vector2i.y = 0;
            return false;
        }
        return true;
    }
    
    public Vector2i i2line(final int n) {
        int n2 = 0;
        for (int i = 1; i < this.size + 1; ++i) {
            n2 += i;
            if (n + 1 <= n2) {
                return new Vector2i(n - n2 + i, i - 1);
            }
        }
        for (int j = this.size + 1; j < this.size * 2; ++j) {
            n2 += this.size * 2 - j;
            if (n + 1 <= n2) {
                return new Vector2i(n - n2 + this.size * 2 - j, j - 1);
            }
        }
        return null;
    }
    
    public Vector2i line2coord(final Vector2i vector2i) {
        if (vector2i == null) {
            return null;
        }
        if (vector2i.y < this.size) {
            final Vector2i vector2i2 = new Vector2i(0, vector2i.y);
            for (int i = 0; i < vector2i.x; ++i) {
                final Vector2i vector2i3 = vector2i2;
                ++vector2i3.x;
                final Vector2i vector2i4 = vector2i2;
                --vector2i4.y;
            }
            return vector2i2;
        }
        final Vector2i vector2i5 = new Vector2i(vector2i.y - this.size + 1, this.size - 1);
        for (int j = 0; j < vector2i.x; ++j) {
            final Vector2i vector2i6 = vector2i5;
            ++vector2i6.x;
            final Vector2i vector2i7 = vector2i5;
            --vector2i7.y;
        }
        return vector2i5;
    }
}

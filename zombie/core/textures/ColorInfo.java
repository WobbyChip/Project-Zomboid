// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.textures;

import zombie.core.ImmutableColor;
import zombie.core.Color;

public final class ColorInfo
{
    public float a;
    public float b;
    public float g;
    public float r;
    
    public ColorInfo() {
        this.a = 1.0f;
        this.b = 1.0f;
        this.g = 1.0f;
        this.r = 1.0f;
        this.r = 1.0f;
        this.g = 1.0f;
        this.b = 1.0f;
        this.a = 1.0f;
    }
    
    public ColorInfo(final float r, final float g, final float b, final float a) {
        this.a = 1.0f;
        this.b = 1.0f;
        this.g = 1.0f;
        this.r = 1.0f;
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }
    
    public ColorInfo set(final ColorInfo colorInfo) {
        this.r = colorInfo.r;
        this.g = colorInfo.g;
        this.b = colorInfo.b;
        this.a = colorInfo.a;
        return this;
    }
    
    public ColorInfo set(final float r, final float g, final float b, final float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        return this;
    }
    
    public float getR() {
        return this.r;
    }
    
    public float getG() {
        return this.g;
    }
    
    public float getB() {
        return this.b;
    }
    
    public Color toColor() {
        return new Color(this.r, this.g, this.b, this.a);
    }
    
    public ImmutableColor toImmutableColor() {
        return new ImmutableColor(this.r, this.g, this.b, this.a);
    }
    
    public float getA() {
        return this.a;
    }
    
    public void desaturate(final float n) {
        final float n2 = this.r * 0.3086f + this.g * 0.6094f + this.b * 0.082f;
        this.r = n2 * n + this.r * (1.0f - n);
        this.g = n2 * n + this.g * (1.0f - n);
        this.b = n2 * n + this.b * (1.0f - n);
    }
    
    public void interp(final ColorInfo colorInfo, final float n, final ColorInfo colorInfo2) {
        final float n2 = colorInfo.r - this.r;
        final float n3 = colorInfo.g - this.g;
        final float n4 = colorInfo.b - this.b;
        final float n5 = colorInfo.a - this.a;
        final float n6 = n2 * n;
        final float n7 = n3 * n;
        final float n8 = n4 * n;
        final float n9 = n5 * n;
        colorInfo2.r = this.r + n6;
        colorInfo2.g = this.g + n7;
        colorInfo2.b = this.b + n8;
        colorInfo2.a = this.a + n9;
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(FFFF)Ljava/lang/String;, this.r, this.g, this.b, this.a);
    }
}

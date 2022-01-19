// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core;

import zombie.core.math.PZMath;

public final class ImmutableColor
{
    public static final ImmutableColor transparent;
    public static final ImmutableColor white;
    public static final ImmutableColor yellow;
    public static final ImmutableColor red;
    public static final ImmutableColor purple;
    public static final ImmutableColor blue;
    public static final ImmutableColor green;
    public static final ImmutableColor black;
    public static final ImmutableColor gray;
    public static final ImmutableColor cyan;
    public static final ImmutableColor darkGray;
    public static final ImmutableColor lightGray;
    public static final ImmutableColor pink;
    public static final ImmutableColor orange;
    public static final ImmutableColor magenta;
    public static final ImmutableColor darkGreen;
    public static final ImmutableColor lightGreen;
    public final float a;
    public final float b;
    public final float g;
    public final float r;
    
    public ImmutableColor(final ImmutableColor immutableColor) {
        if (immutableColor == null) {
            this.r = 0.0f;
            this.g = 0.0f;
            this.b = 0.0f;
            this.a = 1.0f;
            return;
        }
        this.r = immutableColor.r;
        this.g = immutableColor.g;
        this.b = immutableColor.b;
        this.a = immutableColor.a;
    }
    
    public ImmutableColor(final Color color) {
        if (color == null) {
            this.r = 0.0f;
            this.g = 0.0f;
            this.b = 0.0f;
            this.a = 1.0f;
            return;
        }
        this.r = color.r;
        this.g = color.g;
        this.b = color.b;
        this.a = color.a;
    }
    
    public Color toMutableColor() {
        return new Color(this.r, this.g, this.b, this.a);
    }
    
    public ImmutableColor(final float n, final float n2, final float n3) {
        this.r = PZMath.clamp(n, 0.0f, 1.0f);
        this.g = PZMath.clamp(n2, 0.0f, 1.0f);
        this.b = PZMath.clamp(n3, 0.0f, 1.0f);
        this.a = 1.0f;
    }
    
    public ImmutableColor(final float a, final float a2, final float a3, final float a4) {
        this.r = Math.min(a, 1.0f);
        this.g = Math.min(a2, 1.0f);
        this.b = Math.min(a3, 1.0f);
        this.a = Math.min(a4, 1.0f);
    }
    
    public ImmutableColor(final Color color, final Color color2, final float n) {
        final float n2 = (color2.r - color.r) * n;
        final float n3 = (color2.g - color.g) * n;
        final float n4 = (color2.b - color.b) * n;
        final float n5 = (color2.a - color.a) * n;
        this.r = color.r + n2;
        this.g = color.g + n3;
        this.b = color.b + n4;
        this.a = color.a + n5;
    }
    
    public ImmutableColor(final int n, final int n2, final int n3) {
        this.r = n / 255.0f;
        this.g = n2 / 255.0f;
        this.b = n3 / 255.0f;
        this.a = 1.0f;
    }
    
    public ImmutableColor(final int n, final int n2, final int n3, final int n4) {
        this.r = n / 255.0f;
        this.g = n2 / 255.0f;
        this.b = n3 / 255.0f;
        this.a = n4 / 255.0f;
    }
    
    public ImmutableColor(final int n) {
        final int n2 = (n & 0xFF0000) >> 16;
        final int n3 = (n & 0xFF00) >> 8;
        final int n4 = n & 0xFF;
        int n5 = (n & 0xFF000000) >> 24;
        if (n5 < 0) {
            n5 += 256;
        }
        if (n5 == 0) {
            n5 = 255;
        }
        this.r = n4 / 255.0f;
        this.g = n3 / 255.0f;
        this.b = n2 / 255.0f;
        this.a = n5 / 255.0f;
    }
    
    public static ImmutableColor random() {
        return new ImmutableColor(Color.HSBtoRGB(Rand.Next(0.0f, 1.0f), Rand.Next(0.0f, 0.6f), Rand.Next(0.0f, 0.9f)));
    }
    
    public static ImmutableColor decode(final String nm) {
        return new ImmutableColor(Integer.decode(nm));
    }
    
    public ImmutableColor add(final ImmutableColor immutableColor) {
        return new ImmutableColor(this.r + immutableColor.r, this.g + immutableColor.g, this.b + immutableColor.b, this.a + immutableColor.a);
    }
    
    public ImmutableColor brighter() {
        return this.brighter(0.2f);
    }
    
    public ImmutableColor brighter(final float n) {
        return new ImmutableColor(this.r + n, this.g + n, this.b + n);
    }
    
    public ImmutableColor darker() {
        return this.darker(0.5f);
    }
    
    public ImmutableColor darker(final float n) {
        return new ImmutableColor(this.r - n, this.g - n, this.b - n);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof ImmutableColor) {
            final ImmutableColor immutableColor = (ImmutableColor)o;
            return immutableColor.r == this.r && immutableColor.g == this.g && immutableColor.b == this.b && immutableColor.a == this.a;
        }
        return false;
    }
    
    public int getAlphaInt() {
        return (int)(this.a * 255.0f);
    }
    
    public float getAlphaFloat() {
        return this.a;
    }
    
    public float getRedFloat() {
        return this.r;
    }
    
    public float getGreenFloat() {
        return this.g;
    }
    
    public float getBlueFloat() {
        return this.b;
    }
    
    public byte getAlphaByte() {
        return (byte)((int)(this.a * 255.0f) & 0xFF);
    }
    
    public int getBlueInt() {
        return (int)(this.b * 255.0f);
    }
    
    public byte getBlueByte() {
        return (byte)((int)(this.b * 255.0f) & 0xFF);
    }
    
    public int getGreenInt() {
        return (int)(this.g * 255.0f);
    }
    
    public byte getGreenByte() {
        return (byte)((int)(this.g * 255.0f) & 0xFF);
    }
    
    public int getRedInt() {
        return (int)(this.r * 255.0f);
    }
    
    public byte getRedByte() {
        return (byte)((int)(this.r * 255.0f) & 0xFF);
    }
    
    @Override
    public int hashCode() {
        return (int)(this.r + this.g + this.b + this.a) * 255;
    }
    
    public ImmutableColor multiply(final Color color) {
        return new ImmutableColor(this.r * color.r, this.g * color.g, this.b * color.b, this.a * color.a);
    }
    
    public ImmutableColor scale(final float n) {
        return new ImmutableColor(this.r * n, this.g * n, this.b * n, this.a * n);
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(FFFF)Ljava/lang/String;, this.r, this.g, this.b, this.a);
    }
    
    public ImmutableColor interp(final ImmutableColor immutableColor, final float n) {
        return new ImmutableColor(this.r + (immutableColor.r - this.r) * n, this.g + (immutableColor.g - this.g) * n, this.b + (immutableColor.b - this.b) * n, this.a + (immutableColor.a - this.a) * n);
    }
    
    public static Integer[] HSBtoRGB(final float n, final float n2, final float n3) {
        int i = 0;
        int j = 0;
        int k = 0;
        if (n2 == 0.0f) {
            j = (i = (k = (int)(n3 * 255.0f + 0.5f)));
        }
        else {
            final float n4 = (n - (float)Math.floor(n)) * 6.0f;
            final float n5 = n4 - (float)Math.floor(n4);
            final float n6 = n3 * (1.0f - n2);
            final float n7 = n3 * (1.0f - n2 * n5);
            final float n8 = n3 * (1.0f - n2 * (1.0f - n5));
            switch ((int)n4) {
                case 0: {
                    i = (int)(n3 * 255.0f + 0.5f);
                    j = (int)(n8 * 255.0f + 0.5f);
                    k = (int)(n6 * 255.0f + 0.5f);
                    break;
                }
                case 1: {
                    i = (int)(n7 * 255.0f + 0.5f);
                    j = (int)(n3 * 255.0f + 0.5f);
                    k = (int)(n6 * 255.0f + 0.5f);
                    break;
                }
                case 2: {
                    i = (int)(n6 * 255.0f + 0.5f);
                    j = (int)(n3 * 255.0f + 0.5f);
                    k = (int)(n8 * 255.0f + 0.5f);
                    break;
                }
                case 3: {
                    i = (int)(n6 * 255.0f + 0.5f);
                    j = (int)(n7 * 255.0f + 0.5f);
                    k = (int)(n3 * 255.0f + 0.5f);
                    break;
                }
                case 4: {
                    i = (int)(n8 * 255.0f + 0.5f);
                    j = (int)(n6 * 255.0f + 0.5f);
                    k = (int)(n3 * 255.0f + 0.5f);
                    break;
                }
                case 5: {
                    i = (int)(n3 * 255.0f + 0.5f);
                    j = (int)(n6 * 255.0f + 0.5f);
                    k = (int)(n7 * 255.0f + 0.5f);
                    break;
                }
            }
        }
        return new Integer[] { i, j, k };
    }
    
    static {
        transparent = new ImmutableColor(0.0f, 0.0f, 0.0f, 0.0f);
        white = new ImmutableColor(1.0f, 1.0f, 1.0f, 1.0f);
        yellow = new ImmutableColor(1.0f, 1.0f, 0.0f, 1.0f);
        red = new ImmutableColor(1.0f, 0.0f, 0.0f, 1.0f);
        purple = new ImmutableColor(196.0f, 0.0f, 171.0f);
        blue = new ImmutableColor(0.0f, 0.0f, 1.0f, 1.0f);
        green = new ImmutableColor(0.0f, 1.0f, 0.0f, 1.0f);
        black = new ImmutableColor(0.0f, 0.0f, 0.0f, 1.0f);
        gray = new ImmutableColor(0.5f, 0.5f, 0.5f, 1.0f);
        cyan = new ImmutableColor(0.0f, 1.0f, 1.0f, 1.0f);
        darkGray = new ImmutableColor(0.3f, 0.3f, 0.3f, 1.0f);
        lightGray = new ImmutableColor(0.7f, 0.7f, 0.7f, 1.0f);
        pink = new ImmutableColor(255, 175, 175, 255);
        orange = new ImmutableColor(255, 200, 0, 255);
        magenta = new ImmutableColor(255, 0, 255, 255);
        darkGreen = new ImmutableColor(22, 113, 20, 255);
        lightGreen = new ImmutableColor(55, 148, 53, 255);
    }
}

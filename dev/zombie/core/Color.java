// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core;

import zombie.core.math.PZMath;
import java.io.Serializable;

public final class Color implements Serializable
{
    private static final long serialVersionUID = 1393939L;
    public static final Color transparent;
    public static final Color white;
    public static final Color yellow;
    public static final Color red;
    public static final Color purple;
    public static final Color blue;
    public static final Color green;
    public static final Color black;
    public static final Color gray;
    public static final Color cyan;
    public static final Color darkGray;
    public static final Color lightGray;
    public static final Color pink;
    public static final Color orange;
    public static final Color magenta;
    public static final Color darkGreen;
    public static final Color lightGreen;
    public float a;
    public float b;
    public float g;
    public float r;
    
    public Color() {
        this.a = 1.0f;
    }
    
    public Color(final Color color) {
        this.a = 1.0f;
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
    
    public Color(final float r, final float g, final float b) {
        this.a = 1.0f;
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = 1.0f;
    }
    
    public Color(final float n, final float n2, final float n3, final float n4) {
        this.a = 1.0f;
        this.r = PZMath.clamp(n, 0.0f, 1.0f);
        this.g = PZMath.clamp(n2, 0.0f, 1.0f);
        this.b = PZMath.clamp(n3, 0.0f, 1.0f);
        this.a = PZMath.clamp(n4, 0.0f, 1.0f);
    }
    
    public Color(final Color color, final Color color2, final float n) {
        this.a = 1.0f;
        final float n2 = (color2.r - color.r) * n;
        final float n3 = (color2.g - color.g) * n;
        final float n4 = (color2.b - color.b) * n;
        final float n5 = (color2.a - color.a) * n;
        this.r = color.r + n2;
        this.g = color.g + n3;
        this.b = color.b + n4;
        this.a = color.a + n5;
    }
    
    public void setColor(final Color color, final Color color2, final float n) {
        final float n2 = (color2.r - color.r) * n;
        final float n3 = (color2.g - color.g) * n;
        final float n4 = (color2.b - color.b) * n;
        final float n5 = (color2.a - color.a) * n;
        this.r = color.r + n2;
        this.g = color.g + n3;
        this.b = color.b + n4;
        this.a = color.a + n5;
    }
    
    public Color(final int n, final int n2, final int n3) {
        this.a = 1.0f;
        this.r = n / 255.0f;
        this.g = n2 / 255.0f;
        this.b = n3 / 255.0f;
        this.a = 1.0f;
    }
    
    public Color(final int n, final int n2, final int n3, final int n4) {
        this.a = 1.0f;
        this.r = n / 255.0f;
        this.g = n2 / 255.0f;
        this.b = n3 / 255.0f;
        this.a = n4 / 255.0f;
    }
    
    public Color(final int n) {
        this.a = 1.0f;
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
    
    @Deprecated
    public void fromColor(final int n) {
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
    
    public void setABGR(final int n) {
        abgrToColor(n, this);
    }
    
    public static Color abgrToColor(final int n, final Color color) {
        final int n2 = n >> 24 & 0xFF;
        final int n3 = n >> 16 & 0xFF;
        final int n4 = n >> 8 & 0xFF;
        final float r = 0.003921569f * (n & 0xFF);
        final float g = 0.003921569f * n4;
        final float b = 0.003921569f * n3;
        final float a = 0.003921569f * n2;
        color.r = r;
        color.g = g;
        color.b = b;
        color.a = a;
        return color;
    }
    
    public static int colorToABGR(final Color color) {
        return colorToABGR(color.r, color.g, color.b, color.a);
    }
    
    public static int colorToABGR(float clamp, float clamp2, float clamp3, float clamp4) {
        clamp = PZMath.clamp(clamp, 0.0f, 1.0f);
        clamp2 = PZMath.clamp(clamp2, 0.0f, 1.0f);
        clamp3 = PZMath.clamp(clamp3, 0.0f, 1.0f);
        clamp4 = PZMath.clamp(clamp4, 0.0f, 1.0f);
        return ((int)(clamp4 * 255.0f) & 0xFF) << 24 | ((int)(clamp3 * 255.0f) & 0xFF) << 16 | ((int)(clamp2 * 255.0f) & 0xFF) << 8 | ((int)(clamp * 255.0f) & 0xFF);
    }
    
    public static int multiplyABGR(final int n, final int n2) {
        return colorToABGR(getRedChannelFromABGR(n) * getRedChannelFromABGR(n2), getGreenChannelFromABGR(n) * getGreenChannelFromABGR(n2), getBlueChannelFromABGR(n) * getBlueChannelFromABGR(n2), getAlphaChannelFromABGR(n) * getAlphaChannelFromABGR(n2));
    }
    
    public static int multiplyBGR(final int n, final int n2) {
        return colorToABGR(getRedChannelFromABGR(n) * getRedChannelFromABGR(n2), getGreenChannelFromABGR(n) * getGreenChannelFromABGR(n2), getBlueChannelFromABGR(n) * getBlueChannelFromABGR(n2), getAlphaChannelFromABGR(n));
    }
    
    public static int blendBGR(final int n, final int n2) {
        final float redChannelFromABGR = getRedChannelFromABGR(n);
        final float greenChannelFromABGR = getGreenChannelFromABGR(n);
        final float blueChannelFromABGR = getBlueChannelFromABGR(n);
        final float alphaChannelFromABGR = getAlphaChannelFromABGR(n);
        final float redChannelFromABGR2 = getRedChannelFromABGR(n2);
        final float greenChannelFromABGR2 = getGreenChannelFromABGR(n2);
        final float blueChannelFromABGR2 = getBlueChannelFromABGR(n2);
        final float alphaChannelFromABGR2 = getAlphaChannelFromABGR(n2);
        return colorToABGR(redChannelFromABGR * (1.0f - alphaChannelFromABGR2) + redChannelFromABGR2 * alphaChannelFromABGR2, greenChannelFromABGR * (1.0f - alphaChannelFromABGR2) + greenChannelFromABGR2 * alphaChannelFromABGR2, blueChannelFromABGR * (1.0f - alphaChannelFromABGR2) + blueChannelFromABGR2 * alphaChannelFromABGR2, alphaChannelFromABGR);
    }
    
    public static int blendABGR(final int n, final int n2) {
        final float redChannelFromABGR = getRedChannelFromABGR(n);
        final float greenChannelFromABGR = getGreenChannelFromABGR(n);
        final float blueChannelFromABGR = getBlueChannelFromABGR(n);
        final float alphaChannelFromABGR = getAlphaChannelFromABGR(n);
        final float redChannelFromABGR2 = getRedChannelFromABGR(n2);
        final float greenChannelFromABGR2 = getGreenChannelFromABGR(n2);
        final float blueChannelFromABGR2 = getBlueChannelFromABGR(n2);
        final float alphaChannelFromABGR2 = getAlphaChannelFromABGR(n2);
        return colorToABGR(redChannelFromABGR * (1.0f - alphaChannelFromABGR2) + redChannelFromABGR2 * alphaChannelFromABGR2, greenChannelFromABGR * (1.0f - alphaChannelFromABGR2) + greenChannelFromABGR2 * alphaChannelFromABGR2, blueChannelFromABGR * (1.0f - alphaChannelFromABGR2) + blueChannelFromABGR2 * alphaChannelFromABGR2, alphaChannelFromABGR * (1.0f - alphaChannelFromABGR2) + alphaChannelFromABGR2 * alphaChannelFromABGR2);
    }
    
    public static int tintABGR(final int n, final int n2) {
        final float redChannelFromABGR = getRedChannelFromABGR(n2);
        final float greenChannelFromABGR = getGreenChannelFromABGR(n2);
        final float blueChannelFromABGR = getBlueChannelFromABGR(n2);
        final float alphaChannelFromABGR = getAlphaChannelFromABGR(n2);
        return colorToABGR(redChannelFromABGR * alphaChannelFromABGR + getRedChannelFromABGR(n) * (1.0f - alphaChannelFromABGR), greenChannelFromABGR * alphaChannelFromABGR + getGreenChannelFromABGR(n) * (1.0f - alphaChannelFromABGR), blueChannelFromABGR * alphaChannelFromABGR + getBlueChannelFromABGR(n) * (1.0f - alphaChannelFromABGR), getAlphaChannelFromABGR(n));
    }
    
    public static int lerpABGR(final int n, final int n2, final float n3) {
        return colorToABGR(getRedChannelFromABGR(n) * (1.0f - n3) + getRedChannelFromABGR(n2) * n3, getGreenChannelFromABGR(n) * (1.0f - n3) + getGreenChannelFromABGR(n2) * n3, getBlueChannelFromABGR(n) * (1.0f - n3) + getBlueChannelFromABGR(n2) * n3, getAlphaChannelFromABGR(n) * (1.0f - n3) + getAlphaChannelFromABGR(n2) * n3);
    }
    
    public static float getAlphaChannelFromABGR(final int n) {
        return 0.003921569f * (n >> 24 & 0xFF);
    }
    
    public static float getBlueChannelFromABGR(final int n) {
        return 0.003921569f * (n >> 16 & 0xFF);
    }
    
    public static float getGreenChannelFromABGR(final int n) {
        return 0.003921569f * (n >> 8 & 0xFF);
    }
    
    public static float getRedChannelFromABGR(final int n) {
        return 0.003921569f * (n & 0xFF);
    }
    
    public static int setAlphaChannelToABGR(final int n, float clamp) {
        clamp = PZMath.clamp(clamp, 0.0f, 1.0f);
        return ((int)(clamp * 255.0f) & 0xFF) << 24 | (n & 0xFFFFFF);
    }
    
    public static int setBlueChannelToABGR(final int n, float clamp) {
        clamp = PZMath.clamp(clamp, 0.0f, 1.0f);
        return ((int)(clamp * 255.0f) & 0xFF) << 16 | (n & 0xFF00FFFF);
    }
    
    public static int setGreenChannelToABGR(final int n, float clamp) {
        clamp = PZMath.clamp(clamp, 0.0f, 1.0f);
        return ((int)(clamp * 255.0f) & 0xFF) << 8 | (n & 0xFFFF00FF);
    }
    
    public static int setRedChannelToABGR(final int n, float clamp) {
        clamp = PZMath.clamp(clamp, 0.0f, 1.0f);
        return ((int)(clamp * 255.0f) & 0xFF) | (n & 0xFFFFFF00);
    }
    
    public static Color random() {
        return Colors.GetRandomColor();
    }
    
    public static Color decode(final String nm) {
        return new Color(Integer.decode(nm));
    }
    
    public void add(final Color color) {
        this.r += color.r;
        this.g += color.g;
        this.b += color.b;
        this.a += color.a;
    }
    
    public Color addToCopy(final Color color) {
        final Color color3;
        final Color color2 = color3 = new Color(this.r, this.g, this.b, this.a);
        color3.r += color.r;
        final Color color4 = color2;
        color4.g += color.g;
        final Color color5 = color2;
        color5.b += color.b;
        final Color color6 = color2;
        color6.a += color.a;
        return color2;
    }
    
    public Color brighter() {
        return this.brighter(0.2f);
    }
    
    public Color brighter(final float n) {
        final float n2 = this.r + n;
        this.r = n2;
        this.r = n2;
        final float n3 = this.g + n;
        this.g = n3;
        this.g = n3;
        final float n4 = this.b + n;
        this.b = n4;
        this.b = n4;
        return this;
    }
    
    public Color darker() {
        return this.darker(0.5f);
    }
    
    public Color darker(final float n) {
        final float n2 = this.r - n;
        this.r = n2;
        this.r = n2;
        final float n3 = this.g - n;
        this.g = n3;
        this.g = n3;
        final float n4 = this.b - n;
        this.b = n4;
        this.b = n4;
        return this;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof Color) {
            final Color color = (Color)o;
            return color.r == this.r && color.g == this.g && color.b == this.b && color.a == this.a;
        }
        return false;
    }
    
    public Color set(final Color color) {
        this.r = color.r;
        this.g = color.g;
        this.b = color.b;
        this.a = color.a;
        return this;
    }
    
    public Color set(final float r, final float g, final float b) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = 1.0f;
        return this;
    }
    
    public Color set(final float r, final float g, final float b, final float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        return this;
    }
    
    public int getAlpha() {
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
    
    public int getAlphaByte() {
        return (int)(this.a * 255.0f);
    }
    
    public int getBlue() {
        return (int)(this.b * 255.0f);
    }
    
    public int getBlueByte() {
        return (int)(this.b * 255.0f);
    }
    
    public int getGreen() {
        return (int)(this.g * 255.0f);
    }
    
    public int getGreenByte() {
        return (int)(this.g * 255.0f);
    }
    
    public int getRed() {
        return (int)(this.r * 255.0f);
    }
    
    public int getRedByte() {
        return (int)(this.r * 255.0f);
    }
    
    @Override
    public int hashCode() {
        return (int)(this.r + this.g + this.b + this.a) * 255;
    }
    
    public Color multiply(final Color color) {
        return new Color(this.r * color.r, this.g * color.g, this.b * color.b, this.a * color.a);
    }
    
    public Color scale(final float n) {
        this.r *= n;
        this.g *= n;
        this.b *= n;
        this.a *= n;
        return this;
    }
    
    public Color scaleCopy(final float n) {
        final Color color2;
        final Color color = color2 = new Color(this.r, this.g, this.b, this.a);
        color2.r *= n;
        final Color color3 = color;
        color3.g *= n;
        final Color color4 = color;
        color4.b *= n;
        final Color color5 = color;
        color5.a *= n;
        return color;
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(FFFF)Ljava/lang/String;, this.r, this.g, this.b, this.a);
    }
    
    public void interp(final Color color, final float n, final Color color2) {
        final float n2 = color.r - this.r;
        final float n3 = color.g - this.g;
        final float n4 = color.b - this.b;
        final float n5 = color.a - this.a;
        final float n6 = n2 * n;
        final float n7 = n3 * n;
        final float n8 = n4 * n;
        final float n9 = n5 * n;
        color2.r = this.r + n6;
        color2.g = this.g + n7;
        color2.b = this.b + n8;
        color2.a = this.a + n9;
    }
    
    public void changeHSBValue(final float n, final float n2, final float n3) {
        final float[] rgBtoHSB = java.awt.Color.RGBtoHSB(this.getRedByte(), this.getGreenByte(), this.getBlueByte(), null);
        final int hsBtoRGB = java.awt.Color.HSBtoRGB(rgBtoHSB[0] * n, rgBtoHSB[1] * n2, rgBtoHSB[2] * n3);
        this.r = (hsBtoRGB >> 16 & 0xFF) / 255.0f;
        this.g = (hsBtoRGB >> 8 & 0xFF) / 255.0f;
        this.b = (hsBtoRGB & 0xFF) / 255.0f;
    }
    
    public static Color HSBtoRGB(final float n, final float n2, final float n3, final Color color) {
        int n4 = 0;
        int n5 = 0;
        int n6 = 0;
        if (n2 == 0.0f) {
            n5 = (n4 = (n6 = (int)(n3 * 255.0f + 0.5f)));
        }
        else {
            final float n7 = (n - (float)Math.floor(n)) * 6.0f;
            final float n8 = n7 - (float)Math.floor(n7);
            final float n9 = n3 * (1.0f - n2);
            final float n10 = n3 * (1.0f - n2 * n8);
            final float n11 = n3 * (1.0f - n2 * (1.0f - n8));
            switch ((int)n7) {
                case 0: {
                    n4 = (int)(n3 * 255.0f + 0.5f);
                    n5 = (int)(n11 * 255.0f + 0.5f);
                    n6 = (int)(n9 * 255.0f + 0.5f);
                    break;
                }
                case 1: {
                    n4 = (int)(n10 * 255.0f + 0.5f);
                    n5 = (int)(n3 * 255.0f + 0.5f);
                    n6 = (int)(n9 * 255.0f + 0.5f);
                    break;
                }
                case 2: {
                    n4 = (int)(n9 * 255.0f + 0.5f);
                    n5 = (int)(n3 * 255.0f + 0.5f);
                    n6 = (int)(n11 * 255.0f + 0.5f);
                    break;
                }
                case 3: {
                    n4 = (int)(n9 * 255.0f + 0.5f);
                    n5 = (int)(n10 * 255.0f + 0.5f);
                    n6 = (int)(n3 * 255.0f + 0.5f);
                    break;
                }
                case 4: {
                    n4 = (int)(n11 * 255.0f + 0.5f);
                    n5 = (int)(n9 * 255.0f + 0.5f);
                    n6 = (int)(n3 * 255.0f + 0.5f);
                    break;
                }
                case 5: {
                    n4 = (int)(n3 * 255.0f + 0.5f);
                    n5 = (int)(n9 * 255.0f + 0.5f);
                    n6 = (int)(n10 * 255.0f + 0.5f);
                    break;
                }
            }
        }
        return color.set(n4 / 255.0f, n5 / 255.0f, n6 / 255.0f);
    }
    
    public static Color HSBtoRGB(final float n, final float n2, final float n3) {
        return HSBtoRGB(n, n2, n3, new Color());
    }
    
    static {
        transparent = new Color(0.0f, 0.0f, 0.0f, 0.0f);
        white = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        yellow = new Color(1.0f, 1.0f, 0.0f, 1.0f);
        red = new Color(1.0f, 0.0f, 0.0f, 1.0f);
        purple = new Color(196.0f, 0.0f, 171.0f);
        blue = new Color(0.0f, 0.0f, 1.0f, 1.0f);
        green = new Color(0.0f, 1.0f, 0.0f, 1.0f);
        black = new Color(0.0f, 0.0f, 0.0f, 1.0f);
        gray = new Color(0.5f, 0.5f, 0.5f, 1.0f);
        cyan = new Color(0.0f, 1.0f, 1.0f, 1.0f);
        darkGray = new Color(0.3f, 0.3f, 0.3f, 1.0f);
        lightGray = new Color(0.7f, 0.7f, 0.7f, 1.0f);
        pink = new Color(255, 175, 175, 255);
        orange = new Color(255, 200, 0, 255);
        magenta = new Color(255, 0, 255, 255);
        darkGreen = new Color(22, 113, 20, 255);
        lightGreen = new Color(55, 148, 53, 255);
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.fonts;

import zombie.core.Color;

public interface Font
{
    void drawString(final float p0, final float p1, final String p2);
    
    void drawString(final float p0, final float p1, final String p2, final Color p3);
    
    void drawString(final float p0, final float p1, final String p2, final Color p3, final int p4, final int p5);
    
    int getHeight(final String p0);
    
    int getWidth(final String p0);
    
    int getWidth(final String p0, final boolean p1);
    
    int getWidth(final String p0, final int p1, final int p2);
    
    int getWidth(final String p0, final int p1, final int p2, final boolean p3);
    
    int getLineHeight();
}

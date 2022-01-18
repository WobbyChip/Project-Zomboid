// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.Styles;

public interface Style
{
    void setupState();
    
    void resetState();
    
    int getStyleID();
    
    AlphaOp getAlphaOp();
    
    boolean getRenderSprite();
    
    GeometryData build();
    
    void render(final int p0, final int p1);
}

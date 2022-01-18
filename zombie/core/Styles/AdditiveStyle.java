// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.Styles;

import zombie.IndieGL;

public final class AdditiveStyle extends AbstractStyle
{
    private static final long serialVersionUID = 1L;
    public static final AdditiveStyle instance;
    
    @Override
    public void setupState() {
        IndieGL.glBlendFuncA(1, 771);
    }
    
    @Override
    public void resetState() {
        IndieGL.glBlendFuncA(770, 771);
    }
    
    @Override
    public AlphaOp getAlphaOp() {
        return AlphaOp.KEEP;
    }
    
    @Override
    public int getStyleID() {
        return 3;
    }
    
    @Override
    public boolean getRenderSprite() {
        return true;
    }
    
    static {
        instance = new AdditiveStyle();
    }
}

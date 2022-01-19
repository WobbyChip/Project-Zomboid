// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.Styles;

import zombie.IndieGL;

public final class TransparentStyle extends AbstractStyle
{
    private static final long serialVersionUID = 1L;
    public static final TransparentStyle instance;
    
    @Override
    public void setupState() {
        IndieGL.glBlendFuncA(770, 771);
    }
    
    @Override
    public void resetState() {
    }
    
    @Override
    public AlphaOp getAlphaOp() {
        return AlphaOp.KEEP;
    }
    
    @Override
    public int getStyleID() {
        return 2;
    }
    
    @Override
    public boolean getRenderSprite() {
        return true;
    }
    
    static {
        instance = new TransparentStyle();
    }
}

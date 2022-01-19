// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.Styles;

import org.lwjgl.opengl.GL14;

public final class UIFBOStyle extends AbstractStyle
{
    public static final UIFBOStyle instance;
    
    @Override
    public void setupState() {
        GL14.glBlendFuncSeparate(770, 771, 1, 771);
    }
    
    @Override
    public AlphaOp getAlphaOp() {
        return AlphaOp.KEEP;
    }
    
    @Override
    public int getStyleID() {
        return 1;
    }
    
    @Override
    public boolean getRenderSprite() {
        return true;
    }
    
    static {
        instance = new UIFBOStyle();
    }
}

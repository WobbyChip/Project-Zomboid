// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.Styles;

public abstract class AbstractStyle implements Style
{
    private static final long serialVersionUID = 1L;
    
    @Override
    public boolean getRenderSprite() {
        return false;
    }
    
    @Override
    public AlphaOp getAlphaOp() {
        return null;
    }
    
    @Override
    public int getStyleID() {
        return this.hashCode();
    }
    
    @Override
    public void resetState() {
    }
    
    @Override
    public void setupState() {
    }
    
    @Override
    public GeometryData build() {
        return null;
    }
    
    @Override
    public void render(final int n, final int n2) {
    }
}

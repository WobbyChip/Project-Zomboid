// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.sprite;

public final class SpriteRenderStateUI extends GenericSpriteRenderState
{
    public boolean bActive;
    
    public SpriteRenderStateUI(final int n) {
        super(n);
    }
    
    @Override
    public void clear() {
        try {
            this.bActive = true;
            super.clear();
        }
        finally {
            this.bActive = false;
        }
    }
}

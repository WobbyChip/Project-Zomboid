// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.sprite;

public final class IsoSpriteGrid
{
    private IsoSprite[] sprites;
    private int width;
    private int height;
    
    public IsoSpriteGrid(final int width, final int height) {
        this.sprites = new IsoSprite[width * height];
        this.width = width;
        this.height = height;
    }
    
    public IsoSprite getAnchorSprite() {
        return (this.sprites.length > 0) ? this.sprites[0] : null;
    }
    
    public IsoSprite getSprite(final int n, final int n2) {
        return this.getSpriteFromIndex(n2 * this.width + n);
    }
    
    public void setSprite(final int n, final int n2, final IsoSprite isoSprite) {
        this.sprites[n2 * this.width + n] = isoSprite;
    }
    
    public int getSpriteIndex(final IsoSprite isoSprite) {
        for (int i = 0; i < this.sprites.length; ++i) {
            final IsoSprite isoSprite2 = this.sprites[i];
            if (isoSprite2 != null && isoSprite2 == isoSprite) {
                return i;
            }
        }
        return -1;
    }
    
    public int getSpriteGridPosX(final IsoSprite isoSprite) {
        final int spriteIndex = this.getSpriteIndex(isoSprite);
        if (spriteIndex >= 0) {
            return spriteIndex % this.width;
        }
        return -1;
    }
    
    public int getSpriteGridPosY(final IsoSprite isoSprite) {
        final int spriteIndex = this.getSpriteIndex(isoSprite);
        if (spriteIndex >= 0) {
            return spriteIndex / this.width;
        }
        return -1;
    }
    
    public IsoSprite getSpriteFromIndex(final int n) {
        if (n >= 0 && n < this.sprites.length) {
            return this.sprites[n];
        }
        return null;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public boolean validate() {
        for (int i = 0; i < this.sprites.length; ++i) {
            if (this.sprites[i] == null) {
                return false;
            }
        }
        return true;
    }
    
    public int getSpriteCount() {
        return this.sprites.length;
    }
    
    public IsoSprite[] getSprites() {
        return this.sprites;
    }
}

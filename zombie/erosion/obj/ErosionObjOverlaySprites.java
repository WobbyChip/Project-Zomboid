// 
// Decompiled by Procyon v0.5.36
// 

package zombie.erosion.obj;

import zombie.erosion.ErosionMain;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.iso.sprite.IsoSprite;

public final class ErosionObjOverlaySprites
{
    public String name;
    public int stages;
    private Stage[] sprites;
    
    public ErosionObjOverlaySprites(final int stages, final String name) {
        this.name = name;
        this.stages = stages;
        this.sprites = new Stage[this.stages];
        for (int i = 0; i < this.stages; ++i) {
            this.sprites[i] = new Stage();
        }
    }
    
    public IsoSprite getSprite(final int n, final int n2) {
        return this.sprites[n].seasons[n2].getSprite();
    }
    
    public IsoSpriteInstance getSpriteInstance(final int n, final int n2) {
        return this.sprites[n].seasons[n2].getInstance();
    }
    
    public void setSprite(final int n, final String s, final int n2) {
        this.sprites[n].seasons[n2] = new Sprite(s);
    }
    
    private static final class Sprite
    {
        private final String sprite;
        
        public Sprite(final String sprite) {
            this.sprite = sprite;
        }
        
        public IsoSprite getSprite() {
            if (this.sprite != null) {
                return ErosionMain.getInstance().getSpriteManager().getSprite(this.sprite);
            }
            return null;
        }
        
        public IsoSpriteInstance getInstance() {
            if (this.sprite != null) {
                return ErosionMain.getInstance().getSpriteManager().getSprite(this.sprite).newInstance();
            }
            return null;
        }
    }
    
    private static class Stage
    {
        public Sprite[] seasons;
        
        private Stage() {
            this.seasons = new Sprite[6];
        }
    }
}

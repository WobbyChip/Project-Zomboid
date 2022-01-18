// 
// Decompiled by Procyon v0.5.36
// 

package zombie.erosion.obj;

import java.util.Collection;
import zombie.iso.sprite.IsoSprite;
import zombie.debug.DebugLog;
import zombie.iso.IsoDirections;
import zombie.iso.sprite.IsoDirectionFrame;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.network.GameServer;
import zombie.core.Core;
import java.util.ArrayList;

public final class ErosionObjSprites
{
    public static final int SECTION_BASE = 0;
    public static final int SECTION_SNOW = 1;
    public static final int SECTION_FLOWER = 2;
    public static final int SECTION_CHILD = 3;
    public static final int NUM_SECTIONS = 4;
    public String name;
    public int stages;
    public boolean hasSnow;
    public boolean hasFlower;
    public boolean hasChildSprite;
    public boolean noSeasonBase;
    public int cycleTime;
    private Stage[] sprites;
    
    public ErosionObjSprites(final int stages, final String name, final boolean hasSnow, final boolean hasFlower, final boolean hasChildSprite) {
        this.cycleTime = 1;
        this.name = name;
        this.stages = stages;
        this.hasSnow = hasSnow;
        this.hasFlower = hasFlower;
        this.hasChildSprite = hasChildSprite;
        this.sprites = new Stage[stages];
        for (int i = 0; i < stages; ++i) {
            this.sprites[i] = new Stage();
            this.sprites[i].sections[0] = new Section();
            if (this.hasSnow) {
                this.sprites[i].sections[1] = new Section();
            }
            if (this.hasFlower) {
                this.sprites[i].sections[2] = new Section();
            }
            if (this.hasChildSprite) {
                this.sprites[i].sections[3] = new Section();
            }
        }
    }
    
    private String getSprite(final int n, final int n2, final int n3) {
        if (this.sprites[n] != null && this.sprites[n].sections[n2] != null && this.sprites[n].sections[n2].seasons[n3] != null) {
            return this.sprites[n].sections[n2].seasons[n3].getNext();
        }
        return null;
    }
    
    public String getBase(final int n, final int n2) {
        return this.getSprite(n, 0, n2);
    }
    
    public String getFlower(final int n) {
        if (this.hasFlower) {
            return this.getSprite(n, 2, 0);
        }
        return null;
    }
    
    public String getChildSprite(final int n, final int n2) {
        if (this.hasChildSprite) {
            return this.getSprite(n, 3, n2);
        }
        return null;
    }
    
    private void setSprite(final int n, final int n2, final String s, final int n3) {
        if (this.sprites[n] != null && this.sprites[n].sections[n2] != null) {
            this.sprites[n].sections[n2].seasons[n3] = new Sprites(s);
        }
    }
    
    private void setSprite(final int n, final int n2, final ArrayList<String> list, final int n3) {
        assert !list.isEmpty();
        if (this.sprites[n] != null && this.sprites[n].sections[n2] != null) {
            this.sprites[n].sections[n2].seasons[n3] = new Sprites(list);
        }
    }
    
    public void setBase(final int n, final String s, final int n2) {
        this.setSprite(n, 0, s, n2);
    }
    
    public void setBase(final int n, final ArrayList<String> list, final int n2) {
        this.setSprite(n, 0, list, n2);
    }
    
    public void setFlower(final int n, final String s) {
        this.setSprite(n, 2, s, 0);
    }
    
    public void setFlower(final int n, final ArrayList<String> list) {
        this.setSprite(n, 2, list, 0);
    }
    
    public void setChildSprite(final int n, final String s, final int n2) {
        this.setSprite(n, 3, s, n2);
    }
    
    public void setChildSprite(final int n, final ArrayList<String> list, final int n2) {
        this.setSprite(n, 3, list, n2);
    }
    
    private static final class Sprites
    {
        public final ArrayList<String> sprites;
        private int index;
        
        public Sprites(final String e) {
            this.sprites = new ArrayList<String>();
            this.index = -1;
            if (Core.bDebug || (GameServer.bServer && GameServer.bDebug)) {
                final IsoSprite sprite = IsoSpriteManager.instance.getSprite(e);
                if (sprite.CurrentAnim.Frames.size() == 0 || (!GameServer.bServer && sprite.CurrentAnim.Frames.get(0).getTexture(IsoDirections.N) == null) || sprite.ID < 10000) {
                    DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e));
                }
            }
            this.sprites.add(e);
        }
        
        public Sprites(final ArrayList<String> c) {
            this.sprites = new ArrayList<String>();
            this.index = -1;
            if (Core.bDebug || (GameServer.bServer && GameServer.bDebug)) {
                for (int i = 0; i < c.size(); ++i) {
                    final IsoSprite sprite = IsoSpriteManager.instance.getSprite(c.get(i));
                    if (sprite.CurrentAnim.Frames.size() == 0 || (!GameServer.bServer && sprite.CurrentAnim.Frames.get(0).getTexture(IsoDirections.N) == null) || sprite.ID < 10000) {
                        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (String)c.get(i)));
                    }
                }
            }
            this.sprites.addAll(c);
        }
        
        public String getNext() {
            if (++this.index >= this.sprites.size()) {
                this.index = 0;
            }
            return this.sprites.get(this.index);
        }
    }
    
    private static class Section
    {
        public Sprites[] seasons;
        
        private Section() {
            this.seasons = new Sprites[6];
        }
    }
    
    private static class Stage
    {
        public Section[] sections;
        
        private Stage() {
            this.sections = new Section[4];
        }
    }
}

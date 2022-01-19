// 
// Decompiled by Procyon v0.5.36
// 

package zombie.erosion.season;

import zombie.core.Core;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import java.util.ArrayList;

public final class ErosionIceQueen
{
    public static ErosionIceQueen instance;
    private final ArrayList<Sprite> sprites;
    private final IsoSpriteManager SprMngr;
    private boolean snowState;
    
    public void addSprite(final String name, final String s) {
        final IsoSprite sprite = this.SprMngr.getSprite(name);
        final IsoSprite sprite2 = this.SprMngr.getSprite(s);
        if (sprite != null && sprite2 != null) {
            sprite.setName(name);
            this.sprites.add(new Sprite(sprite, name, s));
        }
    }
    
    public void setSnow(final boolean snowState) {
        if (this.snowState != snowState) {
            this.snowState = snowState;
            for (int i = 0; i < this.sprites.size(); ++i) {
                final Sprite sprite = this.sprites.get(i);
                sprite.sprite.ReplaceCurrentAnimFrames(this.snowState ? sprite.winter : sprite.normal);
            }
        }
    }
    
    public ErosionIceQueen(final IsoSpriteManager sprMngr) {
        this.sprites = new ArrayList<Sprite>();
        ErosionIceQueen.instance = this;
        this.SprMngr = sprMngr;
        if (Core.TileScale == 1) {
            this.setRoofSnowOneX();
            for (int i = 0; i < 10; ++i) {
                this.addSprite(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, i), invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, i + 48));
            }
        }
        else {
            this.setRoofSnow();
            for (int j = 0; j < 10; ++j) {
                this.addSprite(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, j), invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, j + 10));
                this.addSprite(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, j), invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, j + 10));
            }
        }
    }
    
    private void setRoofSnowA() {
        for (int i = 0; i < 128; ++i) {
            final String s = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, i);
            for (int j = 1; j <= 5; ++j) {
                this.addSprite(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, j, i), s);
            }
        }
    }
    
    private void setRoofSnow() {
        for (int i = 1; i <= 5; ++i) {
            for (int j = 0; j < 128; ++j) {
                int n = j;
                switch (i) {
                    case 1: {
                        if (j >= 72 && j <= 79) {
                            n = j - 8;
                        }
                        if (j == 112 || j == 114) {
                            n = 0;
                        }
                        if (j == 113 || j == 115) {
                            n = 1;
                        }
                        if (j == 116 || j == 118) {
                            n = 4;
                        }
                        if (j == 117 || j == 119) {
                            n = 5;
                            break;
                        }
                        break;
                    }
                    case 2: {
                        if (j == 50) {
                            n = 106;
                        }
                        if (j == 51) {
                            n = 107;
                        }
                        if (j >= 72 && j <= 79) {
                            n = j - 8;
                        }
                        if (j == 104 || j == 106) {
                            n = 0;
                        }
                        if (j == 105 || j == 107) {
                            n = 1;
                        }
                        if (j == 108 || j == 110) {
                            n = 4;
                        }
                        if (j == 109 || j == 111) {
                            n = 5;
                            break;
                        }
                        break;
                    }
                    case 3: {
                        if (j == 72 || j == 74) {
                            n = 0;
                        }
                        if (j == 73 || j == 75) {
                            n = 1;
                        }
                        if (j == 76 || j == 78) {
                            n = 4;
                        }
                        if (j == 77 || j == 79) {
                            n = 5;
                        }
                        if (j == 102) {
                            n = 70;
                        }
                        if (j == 103) {
                            n = 71;
                        }
                        if (j == 104 || j == 106) {
                            n = 0;
                        }
                        if (j == 105 || j == 107) {
                            n = 1;
                        }
                        if (j == 108 || j == 110) {
                            n = 4;
                        }
                        if (j == 109 || j == 111) {
                            n = 5;
                        }
                        if (j >= 120 && j <= 127) {
                            n = j - 16;
                            break;
                        }
                        break;
                    }
                    case 4: {
                        if (j == 48) {
                            n = 106;
                        }
                        if (j == 49) {
                            n = 107;
                        }
                        if (j == 50) {
                            n = 108;
                        }
                        if (j == 51) {
                            n = 109;
                        }
                        if (j == 72 || j == 74) {
                            n = 0;
                        }
                        if (j == 73 || j == 75) {
                            n = 1;
                        }
                        if (j == 76 || j == 78) {
                            n = 4;
                        }
                        if (j == 77 || j == 79) {
                            n = 5;
                        }
                        if (j == 102) {
                            n = 70;
                        }
                        if (j == 103) {
                            n = 71;
                        }
                        if (j == 104 || j == 106) {
                            n = 0;
                        }
                        if (j == 105 || j == 107) {
                            n = 1;
                        }
                        if (j == 108 || j == 110) {
                            n = 4;
                        }
                        if (j == 109 || j == 111) {
                            n = 5;
                            break;
                        }
                        break;
                    }
                    case 5: {
                        if (j == 104 || j == 106) {
                            n = 0;
                        }
                        if (j == 105 || j == 107) {
                            n = 1;
                        }
                        if (j == 108 || j == 110) {
                            n = 4;
                        }
                        if (j == 109 || j == 111) {
                            n = 5;
                            break;
                        }
                        break;
                    }
                }
                this.addSprite(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, i, j), invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n));
            }
        }
    }
    
    private void setRoofSnowOneX() {
        for (int i = 1; i <= 5; ++i) {
            for (int j = 0; j < 128; ++j) {
                int n = j;
                switch (i) {
                    case 1: {
                        if (j >= 96 && j <= 98) {
                            n = j - 16;
                        }
                        if (j == 99) {
                            n = j - 19;
                        }
                        if (j == 100) {
                            n = j - 13;
                        }
                        if (j >= 101 && j <= 103) {
                            n = j - 16;
                        }
                        if (j >= 112 && j <= 113) {
                            n = j - 112;
                        }
                        if (j >= 114 && j <= 115) {
                            n = j - 114;
                        }
                        if (j == 116 || j == 118) {
                            n = 5;
                        }
                        if (j == 117 || j == 119) {
                            n = 4;
                            break;
                        }
                        break;
                    }
                    case 2: {
                        if (j >= 96 && j <= 98) {
                            n = j - 16;
                        }
                        if (j == 99) {
                            n = j - 19;
                        }
                        if (j == 100) {
                            n = j - 13;
                        }
                        if (j >= 101 && j <= 103) {
                            n = j - 16;
                        }
                        if (j >= 104 && j <= 105) {
                            n = j - 104;
                        }
                        if (j >= 106 && j <= 107) {
                            n = j - 106;
                        }
                        if (j >= 108 && j <= 109) {
                            n = j - 104;
                        }
                        if (j >= 110 && j <= 111) {
                            n = j - 106;
                            break;
                        }
                        break;
                    }
                    case 3: {
                        if (j >= 18 && j <= 19) {
                            n = j - 12;
                        }
                        if (j >= 50 && j <= 51) {
                            n = j - 44;
                        }
                        if (j >= 72 && j <= 73) {
                            n = j - 72;
                        }
                        if (j >= 74 && j <= 75) {
                            n = j - 74;
                        }
                        if (j >= 76 && j <= 77) {
                            n = j - 72;
                        }
                        if (j >= 78 && j <= 79) {
                            n = j - 74;
                        }
                        if (j >= 102 && j <= 103) {
                            n = j - 88;
                        }
                        if (j >= 122 && j <= 125) {
                            n = j - 16;
                            break;
                        }
                        break;
                    }
                    case 4: {
                        if (j >= 18 && j <= 19) {
                            n = j - 12;
                            break;
                        }
                        break;
                    }
                    case 5: {
                        if (j >= 72 && j <= 74) {
                            n = j + 8;
                        }
                        if (j == 75) {
                            n = j + 7;
                        }
                        if (j == 76) {
                            n = j + 11;
                        }
                        if (j >= 77 && j <= 79) {
                            n = j + 8;
                        }
                        if (j >= 112 && j <= 113) {
                            n = j - 112;
                        }
                        if (j >= 114 && j <= 115) {
                            n = j - 114;
                        }
                        if (j == 116 || j == 118) {
                            n = 5;
                        }
                        if (j == 117 || j == 119) {
                            n = 4;
                            break;
                        }
                        break;
                    }
                }
                this.addSprite(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, i, j), invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n));
            }
        }
    }
    
    public static void Reset() {
        if (ErosionIceQueen.instance != null) {
            ErosionIceQueen.instance.sprites.clear();
            ErosionIceQueen.instance = null;
        }
    }
    
    private static class Sprite
    {
        public IsoSprite sprite;
        public String normal;
        public String winter;
        
        public Sprite(final IsoSprite sprite, final String normal, final String winter) {
            this.sprite = sprite;
            this.normal = normal;
            this.winter = winter;
        }
    }
}

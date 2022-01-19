// 
// Decompiled by Procyon v0.5.36
// 

package zombie.erosion;

import zombie.erosion.season.ErosionIceQueen;
import zombie.iso.sprite.IsoSpriteManager;

public class ErosionClient
{
    public static ErosionClient instance;
    
    public ErosionClient(final IsoSpriteManager isoSpriteManager, final boolean b) {
        ErosionClient.instance = this;
        new ErosionIceQueen(isoSpriteManager);
        ErosionRegions.init();
    }
    
    public static void Reset() {
        ErosionClient.instance = null;
    }
}

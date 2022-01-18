// 
// Decompiled by Procyon v0.5.36
// 

package zombie.erosion;

import zombie.erosion.season.ErosionSeason;
import zombie.erosion.season.ErosionIceQueen;
import zombie.iso.sprite.IsoSpriteManager;

public final class ErosionGlobals
{
    public static boolean EROSION_DEBUG;
    
    public static void Boot(final IsoSpriteManager isoSpriteManager) {
        final ErosionMain erosionMain = new ErosionMain(isoSpriteManager, ErosionGlobals.EROSION_DEBUG);
    }
    
    public static void Reset() {
        ErosionMain.Reset();
        ErosionClient.Reset();
        ErosionIceQueen.Reset();
        ErosionSeason.Reset();
        ErosionRegions.Reset();
    }
    
    static {
        ErosionGlobals.EROSION_DEBUG = true;
    }
}

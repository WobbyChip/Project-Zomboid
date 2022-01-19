// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedZoneStory;

import zombie.core.Rand;
import zombie.iso.IsoMetaGrid;

public class RZSMusicFest extends RandomizedZoneStoryBase
{
    public RZSMusicFest() {
        this.name = "Music Festival";
        this.chance = 100;
        this.zoneType.add(ZoneType.MusicFest.toString());
        this.alwaysDo = true;
    }
    
    @Override
    public void randomizeZoneStory(final IsoMetaGrid.Zone zone) {
        for (int next = Rand.Next(20, 50), i = 0; i < next; ++i) {
            switch (Rand.Next(0, 4)) {
                case 0: {
                    this.addItemOnGround(this.getRandomFreeSquareFullZone(this, zone), "Base.BeerCan");
                    break;
                }
                case 1: {
                    this.addItemOnGround(this.getRandomFreeSquareFullZone(this, zone), "Base.BeerBottle");
                    break;
                }
                case 2: {
                    this.addItemOnGround(this.getRandomFreeSquareFullZone(this, zone), "Base.BeerCanEmpty");
                    break;
                }
                case 3: {
                    this.addItemOnGround(this.getRandomFreeSquareFullZone(this, zone), "Base.BeerEmpty");
                    break;
                }
            }
        }
    }
}

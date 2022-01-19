// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedZoneStory;

import zombie.core.Rand;
import zombie.iso.IsoMetaGrid;

public class RZSBaseball extends RandomizedZoneStoryBase
{
    public RZSBaseball() {
        this.name = "Baseball";
        this.chance = 100;
        this.zoneType.add(ZoneType.Baseball.toString());
        this.minZoneWidth = 20;
        this.minZoneHeight = 20;
        this.alwaysDo = true;
    }
    
    @Override
    public void randomizeZoneStory(final IsoMetaGrid.Zone zone) {
        int i;
        int n;
        for (i = Rand.Next(0, 3), n = Rand.Next(0, 3); i == n; n = Rand.Next(0, 3)) {}
        String s = "BaseballPlayer_KY";
        if (i == 1) {
            s = "BaseballPlayer_Rangers";
        }
        if (i == 2) {
            s = "BaseballPlayer_Z";
        }
        String s2 = "BaseballPlayer_KY";
        if (n == 1) {
            s2 = "BaseballPlayer_Rangers";
        }
        if (n == 2) {
            s2 = "BaseballPlayer_Z";
        }
        for (int j = 0; j < 20; ++j) {
            if (Rand.NextBool(4)) {
                this.addItemOnGround(this.getRandomFreeSquare(this, zone), "Base.BaseballBat");
            }
            if (Rand.NextBool(6)) {
                this.addItemOnGround(this.getRandomFreeSquare(this, zone), "Base.Baseball");
            }
        }
        for (int k = 0; k <= 9; ++k) {
            this.addZombiesOnSquare(1, s, 0, this.getRandomFreeSquare(this, zone));
            this.addZombiesOnSquare(1, s2, 0, this.getRandomFreeSquare(this, zone));
        }
    }
}

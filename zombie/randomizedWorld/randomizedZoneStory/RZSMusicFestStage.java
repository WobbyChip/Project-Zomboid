// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedZoneStory;

import zombie.core.Rand;
import zombie.iso.IsoMetaGrid;

public class RZSMusicFestStage extends RandomizedZoneStoryBase
{
    public RZSMusicFestStage() {
        this.name = "Music Festival Stage";
        this.chance = 100;
        this.zoneType.add(ZoneType.MusicFestStage.toString());
        this.alwaysDo = true;
    }
    
    @Override
    public void randomizeZoneStory(final IsoMetaGrid.Zone zone) {
        for (int i = 0; i < 2; ++i) {
            switch (Rand.Next(0, 4)) {
                case 0: {
                    this.addItemOnGround(this.getRandomFreeSquareFullZone(this, zone), "Base.GuitarAcoustic");
                    break;
                }
                case 1: {
                    this.addItemOnGround(this.getRandomFreeSquareFullZone(this, zone), "Base.GuitarElectricBlack");
                    break;
                }
                case 2: {
                    this.addItemOnGround(this.getRandomFreeSquareFullZone(this, zone), "Base.GuitarElectricBlue");
                    break;
                }
                case 3: {
                    this.addItemOnGround(this.getRandomFreeSquareFullZone(this, zone), "Base.GuitarElectricRed");
                    break;
                }
            }
        }
        switch (Rand.Next(0, 3)) {
            case 0: {
                this.addItemOnGround(this.getRandomFreeSquareFullZone(this, zone), "Base.GuitarElectricBassBlack");
                break;
            }
            case 1: {
                this.addItemOnGround(this.getRandomFreeSquareFullZone(this, zone), "Base.GuitarElectricBassBlue");
                break;
            }
            case 2: {
                this.addItemOnGround(this.getRandomFreeSquareFullZone(this, zone), "Base.GuitarElectricBassRed");
                break;
            }
        }
        if (Rand.NextBool(6)) {
            this.addItemOnGround(this.getRandomFreeSquareFullZone(this, zone), "Base.Keytar");
        }
        this.addItemOnGround(this.getRandomFreeSquareFullZone(this, zone), "Base.Speaker");
        this.addItemOnGround(this.getRandomFreeSquareFullZone(this, zone), "Base.Speaker");
        this.addItemOnGround(this.getRandomFreeSquareFullZone(this, zone), "Base.Drumstick");
        this.addZombiesOnSquare(1, "Punk", 0, this.getRandomFreeSquareFullZone(this, zone));
        this.addZombiesOnSquare(1, "Punk", 0, this.getRandomFreeSquareFullZone(this, zone));
        this.addZombiesOnSquare(1, "Punk", 0, this.getRandomFreeSquareFullZone(this, zone));
        this.addZombiesOnSquare(1, "Punk", 0, this.getRandomFreeSquareFullZone(this, zone));
        this.addZombiesOnSquare(1, "Punk", 100, this.getRandomFreeSquareFullZone(this, zone));
    }
}

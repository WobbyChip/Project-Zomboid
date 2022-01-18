// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedZoneStory;

import zombie.core.Rand;
import zombie.iso.IsoDirections;
import zombie.iso.IsoChunk;
import zombie.iso.IsoMetaGrid;
import java.util.ArrayList;

public class RZSHunterCamp extends RandomizedZoneStoryBase
{
    public RZSHunterCamp() {
        this.name = "Hunter Forest Camp";
        this.chance = 5;
        this.minZoneHeight = 6;
        this.minZoneWidth = 6;
        this.zoneType.add(ZoneType.Forest.toString());
    }
    
    public static ArrayList<String> getForestClutter() {
        final ArrayList<String> list = new ArrayList<String>();
        list.add("Base.VarmintRifle");
        list.add("Base.223Box");
        list.add("Base.HuntingRifle");
        list.add("Base.308Box");
        list.add("Base.Shotgun");
        list.add("Base.ShotgunShellsBox");
        list.add("Base.DoubleBarrelShotgun");
        list.add("Base.AssaultRifle");
        list.add("Base.556Box");
        return list;
    }
    
    @Override
    public void randomizeZoneStory(final IsoMetaGrid.Zone zone) {
        final int pickedXForZoneStory = zone.pickedXForZoneStory;
        final int pickedYForZoneStory = zone.pickedYForZoneStory;
        final ArrayList<String> forestClutter = getForestClutter();
        this.cleanAreaForStory(this, zone);
        this.addVehicle(zone, this.getSq(zone.x, zone.y, zone.z), null, null, "Base.OffRoad", null, null, "Hunter");
        this.addTileObject(pickedXForZoneStory, pickedYForZoneStory, zone.z, "camping_01_6");
        final int next = Rand.Next(-1, 2);
        final int next2 = Rand.Next(-1, 2);
        this.addTentWestEast(pickedXForZoneStory + next - 2, pickedYForZoneStory + next2, zone.z);
        if (Rand.Next(100) < 70) {
            this.addTentNorthSouth(pickedXForZoneStory + next, pickedYForZoneStory + next2 - 2, zone.z);
        }
        if (Rand.Next(100) < 30) {
            this.addTentNorthSouth(pickedXForZoneStory + next + 1, pickedYForZoneStory + next2 - 2, zone.z);
        }
        for (int next3 = Rand.Next(2, 5), i = 0; i < next3; ++i) {
            this.addItemOnGround(this.getRandomFreeSquare(this, zone), forestClutter.get(Rand.Next(forestClutter.size())));
        }
        this.addZombiesOnSquare(Rand.Next(2, 5), "Hunter", 0, this.getRandomFreeSquare(this, zone));
    }
}

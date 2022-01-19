// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedZoneStory;

import zombie.iso.IsoGridSquare;
import zombie.Lua.MapObjects;
import zombie.core.Rand;
import zombie.iso.IsoMetaGrid;
import java.util.ArrayList;

public class RZSTrapperCamp extends RandomizedZoneStoryBase
{
    public RZSTrapperCamp() {
        this.name = "Trappers Forest Camp";
        this.chance = 7;
        this.minZoneHeight = 6;
        this.minZoneWidth = 6;
        this.zoneType.add(ZoneType.Forest.toString());
    }
    
    public static ArrayList<String> getTrapList() {
        final ArrayList<String> list = new ArrayList<String>();
        list.add("constructedobjects_01_3");
        list.add("constructedobjects_01_4");
        list.add("constructedobjects_01_7");
        list.add("constructedobjects_01_8");
        list.add("constructedobjects_01_11");
        list.add("constructedobjects_01_13");
        list.add("constructedobjects_01_16");
        return list;
    }
    
    @Override
    public void randomizeZoneStory(final IsoMetaGrid.Zone zone) {
        final int pickedXForZoneStory = zone.pickedXForZoneStory;
        final int pickedYForZoneStory = zone.pickedYForZoneStory;
        final ArrayList<String> trapList = getTrapList();
        this.cleanAreaForStory(this, zone);
        this.addTileObject(pickedXForZoneStory, pickedYForZoneStory, zone.z, "camping_01_6");
        final int next = Rand.Next(-1, 2);
        final int next2 = Rand.Next(-1, 2);
        this.addTentWestEast(pickedXForZoneStory + next - 2, pickedYForZoneStory + next2, zone.z);
        if (Rand.Next(100) < 70) {
            this.addTentNorthSouth(pickedXForZoneStory + next, pickedYForZoneStory + next2 - 2, zone.z);
        }
        for (int next3 = Rand.Next(2, 5), i = 0; i < next3; ++i) {
            final IsoGridSquare randomFreeSquare = this.getRandomFreeSquare(this, zone);
            this.addTileObject(randomFreeSquare, trapList.get(Rand.Next(trapList.size())));
            MapObjects.loadGridSquare(randomFreeSquare);
        }
        this.addZombiesOnSquare(Rand.Next(2, 5), "Hunter", 0, this.getRandomFreeSquare(this, zone));
    }
}

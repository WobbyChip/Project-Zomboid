// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedZoneStory;

import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.InventoryContainer;
import zombie.core.Rand;
import zombie.iso.IsoDirections;
import zombie.iso.IsoChunk;
import zombie.iso.IsoMetaGrid;
import java.util.ArrayList;

public class RZSFishingTrip extends RandomizedZoneStoryBase
{
    public RZSFishingTrip() {
        this.name = "Fishing Trip";
        this.chance = 10;
        this.minZoneHeight = 8;
        this.minZoneWidth = 8;
        this.zoneType.add(ZoneType.Beach.toString());
        this.zoneType.add(ZoneType.Lake.toString());
    }
    
    public static ArrayList<String> getFishes() {
        final ArrayList<String> list = new ArrayList<String>();
        list.add("Base.Catfish");
        list.add("Base.Bass");
        list.add("Base.Perch");
        list.add("Base.Crappie");
        list.add("Base.Panfish");
        list.add("Base.Pike");
        list.add("Base.Trout");
        list.add("Base.BaitFish");
        return list;
    }
    
    public static ArrayList<String> getFishingTools() {
        final ArrayList<String> list = new ArrayList<String>();
        list.add("Base.FishingTackle");
        list.add("Base.FishingTackle");
        list.add("Base.FishingTackle2");
        list.add("Base.FishingTackle2");
        list.add("Base.FishingLine");
        list.add("Base.FishingLine");
        list.add("Base.FishingNet");
        list.add("Base.Worm");
        list.add("Base.Worm");
        list.add("Base.Worm");
        list.add("Base.Worm");
        return list;
    }
    
    @Override
    public void randomizeZoneStory(final IsoMetaGrid.Zone zone) {
        final ArrayList<String> fishes = getFishes();
        final ArrayList<String> fishingTools = getFishingTools();
        this.cleanAreaForStory(this, zone);
        this.addVehicle(zone, this.getSq(zone.x, zone.y, zone.z), null, null, "Base.PickUpTruck", null, null, "Fisherman");
        for (int next = Rand.Next(1, 3), i = 0; i < next; ++i) {
            this.addTileObject(this.getRandomFreeSquare(this, zone), invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Rand.Next(16, 20)));
        }
        final InventoryContainer inventoryContainer = (InventoryContainer)InventoryItemFactory.CreateItem("Base.Cooler");
        for (int next2 = Rand.Next(4, 10), j = 0; j < next2; ++j) {
            inventoryContainer.getItemContainer().AddItem(fishes.get(Rand.Next(fishes.size())));
        }
        this.addItemOnGround(this.getRandomFreeSquare(this, zone), inventoryContainer);
        final InventoryContainer inventoryContainer2 = (InventoryContainer)InventoryItemFactory.CreateItem("Base.Toolbox");
        for (int next3 = Rand.Next(3, 8), k = 0; k < next3; ++k) {
            inventoryContainer2.getItemContainer().AddItem(fishingTools.get(Rand.Next(fishingTools.size())));
        }
        this.addItemOnGround(this.getRandomFreeSquare(this, zone), inventoryContainer2);
        for (int next4 = Rand.Next(2, 5), l = 0; l < next4; ++l) {
            this.addItemOnGround(this.getRandomFreeSquare(this, zone), "FishingRod");
        }
        this.addZombiesOnSquare(Rand.Next(2, 5), "Fisherman", 0, this.getRandomFreeSquare(this, zone));
    }
}

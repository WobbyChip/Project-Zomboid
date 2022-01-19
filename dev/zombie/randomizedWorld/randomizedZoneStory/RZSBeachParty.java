// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedZoneStory;

import zombie.iso.IsoGridSquare;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.InventoryContainer;
import zombie.core.Rand;
import zombie.iso.IsoMetaGrid;
import java.util.ArrayList;

public class RZSBeachParty extends RandomizedZoneStoryBase
{
    public RZSBeachParty() {
        this.name = "Beach Party";
        this.chance = 10;
        this.minZoneHeight = 13;
        this.minZoneWidth = 13;
        this.zoneType.add(ZoneType.Beach.toString());
        this.zoneType.add(ZoneType.Lake.toString());
    }
    
    public static ArrayList<String> getBeachClutter() {
        final ArrayList<String> list = new ArrayList<String>();
        list.add("Base.Crisps");
        list.add("Base.Crisps3");
        list.add("Base.Pop");
        list.add("Base.WhiskeyFull");
        list.add("Base.Cigarettes");
        list.add("Base.BeerBottle");
        list.add("Base.BeerBottle");
        list.add("Base.BeerCan");
        list.add("Base.BeerCan");
        list.add("Base.BeerCan");
        list.add("Base.BeerCan");
        list.add("Base.BeerCan");
        list.add("Base.BeerCan");
        return list;
    }
    
    @Override
    public void randomizeZoneStory(final IsoMetaGrid.Zone zone) {
        final int pickedXForZoneStory = zone.pickedXForZoneStory;
        final int pickedYForZoneStory = zone.pickedYForZoneStory;
        final ArrayList<String> beachClutter = getBeachClutter();
        final ArrayList<String> coolerClutter = RZSForestCamp.getCoolerClutter();
        if (Rand.NextBool(2)) {
            this.addTileObject(pickedXForZoneStory, pickedYForZoneStory, zone.z, "camping_01_6");
        }
        for (int next = Rand.Next(1, 4), i = 0; i < next; ++i) {
            int n = Rand.Next(4) + 1;
            switch (n) {
                case 1: {
                    n = 25;
                    break;
                }
                case 2: {
                    n = 26;
                    break;
                }
                case 3: {
                    n = 28;
                    break;
                }
                case 4: {
                    n = 31;
                    break;
                }
            }
            final IsoGridSquare randomFreeSquare = this.getRandomFreeSquare(this, zone);
            this.addTileObject(randomFreeSquare, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n));
            if (n == 25) {
                this.addTileObject(this.getSq(randomFreeSquare.x, randomFreeSquare.y + 1, randomFreeSquare.z), "furniture_seating_outdoor_01_24");
            }
            else if (n == 26) {
                this.addTileObject(this.getSq(randomFreeSquare.x + 1, randomFreeSquare.y, randomFreeSquare.z), "furniture_seating_outdoor_01_27");
            }
            else if (n == 28) {
                this.addTileObject(this.getSq(randomFreeSquare.x, randomFreeSquare.y - 1, randomFreeSquare.z), "furniture_seating_outdoor_01_29");
            }
            else {
                this.addTileObject(this.getSq(randomFreeSquare.x - 1, randomFreeSquare.y, randomFreeSquare.z), "furniture_seating_outdoor_01_30");
            }
        }
        for (int next2 = Rand.Next(1, 3), j = 0; j < next2; ++j) {
            this.addTileObject(this.getRandomFreeSquare(this, zone), invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Rand.Next(16, 20)));
        }
        final InventoryContainer inventoryContainer = (InventoryContainer)InventoryItemFactory.CreateItem("Base.Cooler");
        for (int next3 = Rand.Next(4, 8), k = 0; k < next3; ++k) {
            inventoryContainer.getItemContainer().AddItem(coolerClutter.get(Rand.Next(coolerClutter.size())));
        }
        this.addItemOnGround(this.getRandomFreeSquare(this, zone), inventoryContainer);
        for (int next4 = Rand.Next(3, 7), l = 0; l < next4; ++l) {
            this.addItemOnGround(this.getRandomFreeSquare(this, zone), beachClutter.get(Rand.Next(beachClutter.size())));
        }
        for (int next5 = Rand.Next(3, 8), n2 = 0; n2 < next5; ++n2) {
            this.addZombiesOnSquare(1, "Swimmer", null, this.getRandomFreeSquare(this, zone));
        }
        for (int next6 = Rand.Next(1, 3), n3 = 0; n3 < next6; ++n3) {
            this.addZombiesOnSquare(1, "Tourist", null, this.getRandomFreeSquare(this, zone));
        }
    }
}

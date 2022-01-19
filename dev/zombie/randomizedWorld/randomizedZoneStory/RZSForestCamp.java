// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedZoneStory;

import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.InventoryContainer;
import zombie.core.Rand;
import zombie.iso.IsoMetaGrid;
import java.util.ArrayList;

public class RZSForestCamp extends RandomizedZoneStoryBase
{
    public RZSForestCamp() {
        this.name = "Basic Forest Camp";
        this.chance = 10;
        this.minZoneHeight = 6;
        this.minZoneWidth = 6;
        this.zoneType.add(ZoneType.Forest.toString());
    }
    
    public static ArrayList<String> getForestClutter() {
        final ArrayList<String> list = new ArrayList<String>();
        list.add("Base.Crisps");
        list.add("Base.Crisps2");
        list.add("Base.Crisps3");
        list.add("Base.Crisps4");
        list.add("Base.Pop");
        list.add("Base.Pop2");
        list.add("Base.WaterBottleFull");
        list.add("Base.CannedSardines");
        list.add("Base.CannedChili");
        list.add("Base.CannedBolognese");
        list.add("Base.CannedCornedBeef");
        list.add("Base.TinnedSoup");
        list.add("Base.TinnedBeans");
        list.add("Base.TunaTin");
        list.add("Base.WhiskeyFull");
        list.add("Base.BeerBottle");
        list.add("Base.BeerCan");
        list.add("Base.BeerCan");
        return list;
    }
    
    public static ArrayList<String> getCoolerClutter() {
        final ArrayList<String> list = new ArrayList<String>();
        list.add("Base.Pop");
        list.add("Base.Pop2");
        list.add("Base.BeefJerky");
        list.add("Base.Ham");
        list.add("Base.WaterBottleFull");
        list.add("Base.BeerCan");
        list.add("Base.BeerCan");
        list.add("Base.BeerCan");
        list.add("Base.BeerCan");
        return list;
    }
    
    public static ArrayList<String> getFireClutter() {
        final ArrayList<String> list = new ArrayList<String>();
        list.add("Base.WaterPotRice");
        list.add("Base.WaterPot");
        list.add("Base.Pot");
        list.add("Base.WaterSaucepanRice");
        list.add("Base.WaterSaucepanPasta");
        list.add("Base.PotOfStew");
        return list;
    }
    
    @Override
    public void randomizeZoneStory(final IsoMetaGrid.Zone zone) {
        final int pickedXForZoneStory = zone.pickedXForZoneStory;
        final int pickedYForZoneStory = zone.pickedYForZoneStory;
        final ArrayList<String> forestClutter = getForestClutter();
        final ArrayList<String> coolerClutter = getCoolerClutter();
        final ArrayList<String> fireClutter = getFireClutter();
        this.cleanAreaForStory(this, zone);
        this.addTileObject(pickedXForZoneStory, pickedYForZoneStory, zone.z, "camping_01_6");
        this.addItemOnGround(this.getSq(pickedXForZoneStory, pickedYForZoneStory, zone.z), fireClutter.get(Rand.Next(fireClutter.size())));
        final int next = Rand.Next(-1, 2);
        final int next2 = Rand.Next(-1, 2);
        this.addTentWestEast(pickedXForZoneStory + next - 2, pickedYForZoneStory + next2, zone.z);
        if (Rand.Next(100) < 70) {
            this.addTentNorthSouth(pickedXForZoneStory + next, pickedYForZoneStory + next2 - 2, zone.z);
        }
        if (Rand.Next(100) < 30) {
            this.addTentNorthSouth(pickedXForZoneStory + next + 1, pickedYForZoneStory + next2 - 2, zone.z);
        }
        this.addTileObject(pickedXForZoneStory + 2, pickedYForZoneStory, zone.z, "furniture_seating_outdoor_01_19");
        final InventoryContainer inventoryContainer = (InventoryContainer)InventoryItemFactory.CreateItem("Base.Cooler");
        for (int next3 = Rand.Next(2, 5), i = 0; i < next3; ++i) {
            inventoryContainer.getItemContainer().AddItem(coolerClutter.get(Rand.Next(coolerClutter.size())));
        }
        this.addItemOnGround(this.getRandomFreeSquare(this, zone), inventoryContainer);
        for (int next4 = Rand.Next(3, 7), j = 0; j < next4; ++j) {
            this.addItemOnGround(this.getRandomFreeSquare(this, zone), forestClutter.get(Rand.Next(forestClutter.size())));
        }
        this.addZombiesOnSquare(Rand.Next(1, 3), "Camper", null, this.getRandomFreeSquare(this, zone));
    }
}

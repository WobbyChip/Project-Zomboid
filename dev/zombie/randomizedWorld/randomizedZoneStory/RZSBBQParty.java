// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedZoneStory;

import zombie.iso.IsoGridSquare;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.InventoryContainer;
import zombie.core.Rand;
import zombie.iso.objects.IsoBarbecue;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.IsoWorld;
import zombie.iso.IsoMetaGrid;
import java.util.ArrayList;

public class RZSBBQParty extends RandomizedZoneStoryBase
{
    public RZSBBQParty() {
        this.name = "BBQ Party";
        this.chance = 10;
        this.minZoneHeight = 12;
        this.minZoneWidth = 12;
        this.zoneType.add(ZoneType.Beach.toString());
        this.zoneType.add(ZoneType.Lake.toString());
    }
    
    public static ArrayList<String> getBeachClutter() {
        final ArrayList<String> list = new ArrayList<String>();
        list.add("Base.Crisps");
        list.add("Base.Crisps3");
        list.add("Base.MuttonChop");
        list.add("Base.PorkChop");
        list.add("Base.Steak");
        list.add("Base.Pop");
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
        final IsoGridSquare sq = this.getSq(pickedXForZoneStory, pickedYForZoneStory, zone.z);
        sq.getObjects().add(new IsoBarbecue(IsoWorld.instance.getCell(), sq, IsoSpriteManager.instance.NamedMap.get("appliances_cooking_01_35")));
        for (int next = Rand.Next(1, 4), i = 0; i < next; ++i) {
            this.addTileObject(this.getRandomFreeSquare(this, zone), invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Rand.Next(16, 20)));
        }
        final InventoryContainer inventoryContainer = (InventoryContainer)InventoryItemFactory.CreateItem("Base.Cooler");
        for (int next2 = Rand.Next(4, 8), j = 0; j < next2; ++j) {
            inventoryContainer.getItemContainer().AddItem(coolerClutter.get(Rand.Next(coolerClutter.size())));
        }
        this.addItemOnGround(this.getRandomFreeSquare(this, zone), inventoryContainer);
        for (int next3 = Rand.Next(3, 7), k = 0; k < next3; ++k) {
            this.addItemOnGround(this.getRandomFreeSquare(this, zone), beachClutter.get(Rand.Next(beachClutter.size())));
        }
        for (int next4 = Rand.Next(3, 8), l = 0; l < next4; ++l) {
            this.addZombiesOnSquare(1, "Tourist", null, this.getRandomFreeSquare(this, zone));
        }
    }
}

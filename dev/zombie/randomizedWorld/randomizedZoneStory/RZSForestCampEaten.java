// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedZoneStory;

import zombie.iso.objects.IsoDeadBody;
import java.util.ArrayList;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoDirections;
import zombie.randomizedWorld.RandomizedWorldBase;
import zombie.characters.IsoZombie;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.InventoryContainer;
import zombie.core.Rand;
import zombie.iso.IsoMetaGrid;

public class RZSForestCampEaten extends RandomizedZoneStoryBase
{
    public RZSForestCampEaten() {
        this.name = "Forest Camp Eaten";
        this.chance = 10;
        this.minZoneHeight = 6;
        this.minZoneWidth = 10;
        this.minimumDays = 30;
        this.zoneType.add(ZoneType.Forest.toString());
    }
    
    @Override
    public void randomizeZoneStory(final IsoMetaGrid.Zone zone) {
        final int pickedXForZoneStory = zone.pickedXForZoneStory;
        final int pickedYForZoneStory = zone.pickedYForZoneStory;
        final ArrayList<String> forestClutter = RZSForestCamp.getForestClutter();
        final ArrayList<String> coolerClutter = RZSForestCamp.getCoolerClutter();
        final ArrayList<String> fireClutter = RZSForestCamp.getFireClutter();
        this.cleanAreaForStory(this, zone);
        this.addTileObject(pickedXForZoneStory, pickedYForZoneStory, zone.z, "camping_01_6");
        this.addItemOnGround(this.getSq(pickedXForZoneStory, pickedYForZoneStory, zone.z), fireClutter.get(Rand.Next(fireClutter.size())));
        final int n = 0;
        final int n2 = 0;
        this.addTentNorthSouth(pickedXForZoneStory - 4, pickedYForZoneStory + n2 - 2, zone.z);
        final int n3 = n + Rand.Next(1, 3);
        this.addTentNorthSouth(pickedXForZoneStory - 3 + n3, pickedYForZoneStory + n2 - 2, zone.z);
        int n4 = n3 + Rand.Next(1, 3);
        this.addTentNorthSouth(pickedXForZoneStory - 2 + n4, pickedYForZoneStory + n2 - 2, zone.z);
        if (Rand.NextBool(1)) {
            n4 += Rand.Next(1, 3);
            this.addTentNorthSouth(pickedXForZoneStory - 1 + n4, pickedYForZoneStory + n2 - 2, zone.z);
        }
        if (Rand.NextBool(2)) {
            this.addTentNorthSouth(pickedXForZoneStory + (n4 + Rand.Next(1, 3)), pickedYForZoneStory + n2 - 2, zone.z);
        }
        final InventoryContainer inventoryContainer = (InventoryContainer)InventoryItemFactory.CreateItem("Base.Cooler");
        for (int next = Rand.Next(2, 5), i = 0; i < next; ++i) {
            inventoryContainer.getItemContainer().AddItem(coolerClutter.get(Rand.Next(coolerClutter.size())));
        }
        this.addItemOnGround(this.getRandomFreeSquare(this, zone), inventoryContainer);
        for (int next2 = Rand.Next(3, 7), j = 0; j < next2; ++j) {
            this.addItemOnGround(this.getRandomFreeSquare(this, zone), forestClutter.get(Rand.Next(forestClutter.size())));
        }
        final ArrayList<IsoZombie> addZombiesOnSquare = this.addZombiesOnSquare(1, "Camper", null, this.getRandomFreeSquare(this, zone));
        final IsoZombie isoZombie = addZombiesOnSquare.isEmpty() ? null : addZombiesOnSquare.get(0);
        for (int next3 = Rand.Next(3, 7), k = 0; k < next3; ++k) {
            final IsoDeadBody randomDeadBody = RandomizedWorldBase.createRandomDeadBody(this.getRandomFreeSquare(this, zone), null, Rand.Next(5, 10), 0, "Camper");
            if (randomDeadBody != null) {
                this.addBloodSplat(randomDeadBody.getSquare(), 10);
            }
        }
        final IsoDeadBody randomDeadBody2 = RandomizedWorldBase.createRandomDeadBody(this.getSq(pickedXForZoneStory, pickedYForZoneStory + 3, zone.z), null, Rand.Next(5, 10), 0, "Camper");
        if (randomDeadBody2 != null) {
            this.addBloodSplat(randomDeadBody2.getSquare(), 10);
            if (isoZombie != null) {
                isoZombie.faceLocationF(randomDeadBody2.x, randomDeadBody2.y);
                isoZombie.setX(randomDeadBody2.x + 1.0f);
                isoZombie.setY(randomDeadBody2.y);
                isoZombie.setEatBodyTarget(randomDeadBody2, true);
            }
        }
    }
}

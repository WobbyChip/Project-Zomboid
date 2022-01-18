// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedBuilding;

import zombie.iso.IsoGridSquare;
import zombie.inventory.InventoryItem;
import java.util.ArrayList;
import zombie.iso.IsoObject;
import zombie.inventory.InventoryItemFactory;
import zombie.characterTextures.BloodBodyPartType;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.core.ImmutableColor;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.characters.IsoZombie;
import zombie.iso.BuildingDef;

public class RBKateAndBaldspot extends RandomizedBuildingBase
{
    public RBKateAndBaldspot() {
        this.name = "K&B story";
        this.setChance(0);
        this.setUnique(true);
    }
    
    @Override
    public void randomizeBuilding(final BuildingDef buildingDef) {
        buildingDef.bAlarmed = false;
        buildingDef.setHasBeenVisited(true);
        buildingDef.setAllExplored(true);
        final ArrayList<IsoZombie> addZombiesOnSquare = this.addZombiesOnSquare(1, "Kate", 100, this.getSq(10746, 9412, 1));
        if (addZombiesOnSquare == null || addZombiesOnSquare.isEmpty()) {
            return;
        }
        final IsoZombie isoZombie = addZombiesOnSquare.get(0);
        final HumanVisual humanVisual = (HumanVisual)isoZombie.getVisual();
        humanVisual.setHairModel("Rachel");
        humanVisual.setHairColor(new ImmutableColor(0.83f, 0.67f, 0.27f));
        for (int i = 0; i < isoZombie.getItemVisuals().size(); ++i) {
            final ItemVisual itemVisual = isoZombie.getItemVisuals().get(i);
            if (itemVisual.getClothingItemName().equals("Skirt_Knees")) {
                itemVisual.setTint(new ImmutableColor(0.54f, 0.54f, 0.54f));
            }
        }
        isoZombie.getHumanVisual().setSkinTextureIndex(1);
        isoZombie.addBlood(BloodBodyPartType.LowerLeg_L, true, true, true);
        isoZombie.addBlood(BloodBodyPartType.LowerLeg_L, true, true, true);
        isoZombie.addBlood(BloodBodyPartType.UpperLeg_L, true, true, true);
        isoZombie.addBlood(BloodBodyPartType.UpperLeg_L, true, true, true);
        isoZombie.setCrawler(true);
        isoZombie.setCanWalk(false);
        isoZombie.setCrawlerType(1);
        isoZombie.resetModelNextFrame();
        final ArrayList<IsoZombie> addZombiesOnSquare2 = this.addZombiesOnSquare(1, "Bob", 0, this.getSq(10747, 9412, 1));
        if (addZombiesOnSquare2 == null || addZombiesOnSquare2.isEmpty()) {
            return;
        }
        final IsoZombie isoZombie2 = addZombiesOnSquare2.get(0);
        final HumanVisual humanVisual2 = (HumanVisual)isoZombie2.getVisual();
        humanVisual2.setHairModel("Baldspot");
        humanVisual2.setHairColor(new ImmutableColor(0.337f, 0.173f, 0.082f));
        humanVisual2.setBeardModel("");
        for (int j = 0; j < isoZombie2.getItemVisuals().size(); ++j) {
            final ItemVisual itemVisual2 = isoZombie2.getItemVisuals().get(j);
            if (itemVisual2.getClothingItemName().equals("Trousers_DefaultTEXTURE_TINT")) {
                itemVisual2.setTint(new ImmutableColor(0.54f, 0.54f, 0.54f));
            }
            if (itemVisual2.getClothingItemName().equals("Shirt_FormalTINT")) {
                itemVisual2.setTint(new ImmutableColor(0.63f, 0.71f, 0.82f));
            }
        }
        isoZombie2.getHumanVisual().setSkinTextureIndex(1);
        isoZombie2.resetModelNextFrame();
        isoZombie2.addItemToSpawnAtDeath(InventoryItemFactory.CreateItem("KatePic"));
        isoZombie2.addItemToSpawnAtDeath(InventoryItemFactory.CreateItem("RippedSheets"));
        isoZombie2.addItemToSpawnAtDeath(InventoryItemFactory.CreateItem("Pills"));
        final InventoryItem createItem = InventoryItemFactory.CreateItem("Hammer");
        createItem.setCondition(1);
        isoZombie2.addItemToSpawnAtDeath(createItem);
        isoZombie2.addItemToSpawnAtDeath(InventoryItemFactory.CreateItem("Nails"));
        isoZombie2.addItemToSpawnAtDeath(InventoryItemFactory.CreateItem("Plank"));
        final ArrayList<IsoZombie> addZombiesOnSquare3 = this.addZombiesOnSquare(1, "Raider", 0, this.getSq(10745, 9411, 0));
        if (addZombiesOnSquare3 == null || addZombiesOnSquare3.isEmpty()) {
            return;
        }
        final IsoZombie isoZombie3 = addZombiesOnSquare3.get(0);
        final HumanVisual humanVisual3 = (HumanVisual)isoZombie3.getVisual();
        humanVisual3.setHairModel("Crewcut");
        humanVisual3.setHairColor(new ImmutableColor(0.37f, 0.27f, 0.23f));
        humanVisual3.setBeardModel("Goatee");
        for (int k = 0; k < isoZombie3.getItemVisuals().size(); ++k) {
            final ItemVisual itemVisual3 = isoZombie3.getItemVisuals().get(k);
            if (itemVisual3.getClothingItemName().equals("Trousers_DefaultTEXTURE_TINT")) {
                itemVisual3.setTint(new ImmutableColor(0.54f, 0.54f, 0.54f));
            }
            if (itemVisual3.getClothingItemName().equals("Vest_DefaultTEXTURE_TINT")) {
                itemVisual3.setTint(new ImmutableColor(0.22f, 0.25f, 0.27f));
            }
        }
        isoZombie3.getHumanVisual().setSkinTextureIndex(1);
        final InventoryItem createItem2 = InventoryItemFactory.CreateItem("Shotgun");
        createItem2.setCondition(0);
        isoZombie3.setAttachedItem("Rifle On Back", createItem2);
        final InventoryItem createItem3 = InventoryItemFactory.CreateItem("BaseballBat");
        createItem3.setCondition(1);
        isoZombie3.addItemToSpawnAtDeath(createItem3);
        isoZombie3.addItemToSpawnAtDeath(InventoryItemFactory.CreateItem("ShotgunShells"));
        isoZombie3.resetModelNextFrame();
        this.addItemOnGround(this.getSq(10747, 9412, 1), InventoryItemFactory.CreateItem("Pillow"));
        this.getSq(10745, 9410, 0).Burn();
        this.getSq(10745, 9411, 0).Burn();
        this.getSq(10746, 9411, 0).Burn();
        this.getSq(10745, 9410, 0).Burn();
        this.getSq(10745, 9412, 0).Burn();
        this.getSq(10747, 9410, 0).Burn();
        this.getSq(10746, 9409, 0).Burn();
        this.getSq(10745, 9409, 0).Burn();
        this.getSq(10744, 9410, 0).Burn();
        this.getSq(10747, 9411, 0).Burn();
        this.getSq(10746, 9412, 0).Burn();
        final IsoGridSquare sq = this.getSq(10746, 9410, 0);
        for (int l = 0; l < sq.getObjects().size(); ++l) {
            final IsoObject isoObject = sq.getObjects().get(l);
            if (isoObject.getContainer() != null) {
                final InventoryItem createItem4 = InventoryItemFactory.CreateItem("PotOfSoup");
                createItem4.setCooked(true);
                createItem4.setBurnt(true);
                isoObject.getContainer().AddItem(createItem4);
                break;
            }
        }
        this.addBarricade(this.getSq(10747, 9417, 0), 3);
        this.addBarricade(this.getSq(10745, 9417, 0), 3);
        this.addBarricade(this.getSq(10744, 9413, 0), 3);
        this.addBarricade(this.getSq(10744, 9412, 0), 3);
        this.addBarricade(this.getSq(10752, 9413, 0), 3);
    }
    
    @Override
    public boolean isValid(final BuildingDef buildingDef, final boolean b) {
        this.debugLine = "";
        if (buildingDef.x == 10744 && buildingDef.y == 9409) {
            return true;
        }
        this.debugLine = "Need to be the K&B house";
        return false;
    }
}

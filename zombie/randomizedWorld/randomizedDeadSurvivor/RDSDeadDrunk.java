// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedDeadSurvivor;

import zombie.iso.objects.IsoDeadBody;
import zombie.inventory.InventoryItemFactory;
import zombie.core.Rand;
import zombie.randomizedWorld.RandomizedWorldBase;
import zombie.iso.BuildingDef;
import java.util.ArrayList;

public final class RDSDeadDrunk extends RandomizedDeadSurvivorBase
{
    final ArrayList<String> alcoholList;
    
    public RDSDeadDrunk() {
        this.alcoholList = new ArrayList<String>();
        this.name = "Dead Drunk";
        this.setChance(10);
        this.alcoholList.add("Base.WhiskeyFull");
        this.alcoholList.add("Base.WhiskeyEmpty");
        this.alcoholList.add("Base.Wine");
        this.alcoholList.add("Base.WineEmpty");
        this.alcoholList.add("Base.Wine2");
        this.alcoholList.add("Base.WineEmpty2");
    }
    
    @Override
    public void randomizeDeadSurvivor(final BuildingDef buildingDef) {
        final IsoDeadBody randomDeadBody = RandomizedWorldBase.createRandomDeadBody(this.getLivingRoomOrKitchen(buildingDef), 0);
        if (randomDeadBody == null) {
            return;
        }
        for (int next = Rand.Next(2, 4), i = 0; i < next; ++i) {
            randomDeadBody.getSquare().AddWorldInventoryItem(InventoryItemFactory.CreateItem(this.alcoholList.get(Rand.Next(0, this.alcoholList.size()))), Rand.Next(0.5f, 1.0f), Rand.Next(0.5f, 1.0f), 0.0f);
        }
        randomDeadBody.setPrimaryHandItem(InventoryItemFactory.CreateItem("Base.WhiskeyEmpty"));
    }
}

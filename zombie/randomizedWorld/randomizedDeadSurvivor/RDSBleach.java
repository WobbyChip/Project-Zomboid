// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedDeadSurvivor;

import zombie.iso.objects.IsoDeadBody;
import zombie.inventory.InventoryItemFactory;
import zombie.core.Rand;
import zombie.randomizedWorld.RandomizedWorldBase;
import zombie.iso.BuildingDef;

public final class RDSBleach extends RandomizedDeadSurvivorBase
{
    public RDSBleach() {
        this.name = "Suicide by Bleach";
        this.setChance(10);
        this.setMinimumDays(60);
    }
    
    @Override
    public void randomizeDeadSurvivor(final BuildingDef buildingDef) {
        final IsoDeadBody randomDeadBody = RandomizedWorldBase.createRandomDeadBody(this.getLivingRoomOrKitchen(buildingDef), 0);
        if (randomDeadBody == null) {
            return;
        }
        for (int next = Rand.Next(1, 3), i = 0; i < next; ++i) {
            randomDeadBody.getSquare().AddWorldInventoryItem(InventoryItemFactory.CreateItem("Base.BleachEmpty"), Rand.Next(0.5f, 1.0f), Rand.Next(0.5f, 1.0f), 0.0f);
        }
        randomDeadBody.setPrimaryHandItem(InventoryItemFactory.CreateItem("Base.BleachEmpty"));
    }
}

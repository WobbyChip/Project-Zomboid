// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedDeadSurvivor;

import zombie.iso.objects.IsoDeadBody;
import zombie.inventory.InventoryItem;
import zombie.randomizedWorld.RandomizedWorldBase;
import zombie.core.Rand;
import zombie.iso.BuildingDef;

public final class RDSGunmanInBathroom extends RandomizedDeadSurvivorBase
{
    @Override
    public void randomizeDeadSurvivor(final BuildingDef buildingDef) {
        final IsoDeadBody randomDeadBody = RandomizedWorldBase.createRandomDeadBody(super.getRoom(buildingDef, "bathroom"), Rand.Next(5, 10));
        if (randomDeadBody == null) {
            return;
        }
        randomDeadBody.setPrimaryHandItem(super.addRandomRangedWeapon(randomDeadBody.getContainer(), true, false, false));
        for (int next = Rand.Next(1, 4), i = 0; i < next; ++i) {
            randomDeadBody.getContainer().AddItem(super.addRandomRangedWeapon(randomDeadBody.getContainer(), true, true, true));
        }
    }
    
    public RDSGunmanInBathroom() {
        this.name = "Bathroom Gunman";
        this.setChance(5);
    }
}

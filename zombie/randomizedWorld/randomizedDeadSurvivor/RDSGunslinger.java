// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedDeadSurvivor;

import zombie.iso.objects.IsoDeadBody;
import zombie.iso.IsoGridSquare;
import zombie.core.Rand;
import zombie.inventory.InventoryItem;
import zombie.iso.IsoDirections;
import zombie.randomizedWorld.RandomizedWorldBase;
import zombie.iso.BuildingDef;

public final class RDSGunslinger extends RandomizedDeadSurvivorBase
{
    @Override
    public void randomizeDeadSurvivor(final BuildingDef buildingDef) {
        final IsoGridSquare freeSquareInRoom = buildingDef.getFreeSquareInRoom();
        if (freeSquareInRoom == null) {
            return;
        }
        final IsoDeadBody randomDeadBody = RandomizedWorldBase.createRandomDeadBody(freeSquareInRoom.getX(), freeSquareInRoom.getY(), freeSquareInRoom.getZ(), null, 0);
        if (randomDeadBody == null) {
            return;
        }
        randomDeadBody.setPrimaryHandItem(super.addRandomRangedWeapon(randomDeadBody.getContainer(), true, false, false));
        for (int next = Rand.Next(1, 4), i = 0; i < next; ++i) {
            randomDeadBody.getContainer().AddItem(super.addRandomRangedWeapon(randomDeadBody.getContainer(), true, true, true));
        }
    }
    
    public RDSGunslinger() {
        this.name = "Gunslinger";
        this.setChance(5);
    }
}

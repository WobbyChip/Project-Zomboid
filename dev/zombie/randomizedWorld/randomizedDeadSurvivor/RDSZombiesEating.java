// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedDeadSurvivor;

import zombie.iso.objects.IsoDeadBody;
import zombie.iso.RoomDef;
import zombie.VirtualZombieManager;
import zombie.randomizedWorld.RandomizedWorldBase;
import zombie.core.Rand;
import zombie.iso.IsoWorld;
import zombie.iso.BuildingDef;

public final class RDSZombiesEating extends RandomizedDeadSurvivorBase
{
    public RDSZombiesEating() {
        this.name = "Eating zombies";
        this.setChance(7);
        this.setMaximumDays(60);
    }
    
    @Override
    public boolean isValid(final BuildingDef buildingDef, final boolean b) {
        return IsoWorld.getZombiesEnabled() && super.isValid(buildingDef, b);
    }
    
    @Override
    public void randomizeDeadSurvivor(final BuildingDef buildingDef) {
        final RoomDef livingRoomOrKitchen = this.getLivingRoomOrKitchen(buildingDef);
        final IsoDeadBody randomDeadBody = RandomizedWorldBase.createRandomDeadBody(livingRoomOrKitchen, Rand.Next(5, 10));
        if (randomDeadBody == null) {
            return;
        }
        VirtualZombieManager.instance.createEatingZombies(randomDeadBody, Rand.Next(1, 3));
        final RoomDef room = this.getRoom(buildingDef, "kitchen");
        final RoomDef room2 = this.getRoom(buildingDef, "livingroom");
        if ("kitchen".equals(livingRoomOrKitchen.name) && room2 != null && Rand.Next(3) == 0) {
            final IsoDeadBody randomDeadBody2 = RandomizedWorldBase.createRandomDeadBody(room2, Rand.Next(5, 10));
            if (randomDeadBody2 == null) {
                return;
            }
            VirtualZombieManager.instance.createEatingZombies(randomDeadBody2, Rand.Next(1, 3));
        }
        if ("livingroom".equals(livingRoomOrKitchen.name) && room != null && Rand.Next(3) == 0) {
            final IsoDeadBody randomDeadBody3 = RandomizedWorldBase.createRandomDeadBody(room, Rand.Next(5, 10));
            if (randomDeadBody3 == null) {
                return;
            }
            VirtualZombieManager.instance.createEatingZombies(randomDeadBody3, Rand.Next(1, 3));
        }
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedDeadSurvivor;

import zombie.vehicles.BaseVehicle;
import java.util.ArrayList;
import zombie.iso.RoomDef;
import zombie.characters.IsoZombie;
import java.util.Collection;
import zombie.randomizedWorld.RandomizedWorldBase;
import zombie.core.Rand;
import zombie.iso.BuildingDef;

public final class RDSPoliceAtHouse extends RandomizedDeadSurvivorBase
{
    public RDSPoliceAtHouse() {
        this.name = "Police at House";
        this.setChance(4);
    }
    
    @Override
    public void randomizeDeadSurvivor(final BuildingDef buildingDef) {
        final RoomDef livingRoomOrKitchen = this.getLivingRoomOrKitchen(buildingDef);
        this.addZombies(buildingDef, Rand.Next(2, 4), null, 0, livingRoomOrKitchen);
        final ArrayList<IsoZombie> addZombies = this.addZombies(buildingDef, Rand.Next(1, 3), "Police", null, livingRoomOrKitchen);
        final BaseVehicle spawnCarOnNearestNav = this.spawnCarOnNearestNav("Base.CarLightsPolice", buildingDef);
        if (spawnCarOnNearestNav == null) {
            return;
        }
        final ArrayList<IsoZombie> addZombiesOnSquare = this.addZombiesOnSquare(2, "Police", null, spawnCarOnNearestNav.getSquare().getCell().getGridSquare(spawnCarOnNearestNav.getSquare().x - 2, spawnCarOnNearestNav.getSquare().y - 2, 0));
        RandomizedWorldBase.createRandomDeadBody(livingRoomOrKitchen, Rand.Next(7, 10));
        RandomizedWorldBase.createRandomDeadBody(livingRoomOrKitchen, Rand.Next(7, 10));
        if (addZombies.isEmpty()) {
            return;
        }
        addZombies.addAll(addZombiesOnSquare);
        addZombies.get(Rand.Next(addZombies.size())).addItemToSpawnAtDeath(spawnCarOnNearestNav.createVehicleKey());
    }
}

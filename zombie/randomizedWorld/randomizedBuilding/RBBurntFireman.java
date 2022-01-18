// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedBuilding;

import zombie.vehicles.BaseVehicle;
import java.util.ArrayList;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoCell;
import zombie.characters.IsoZombie;
import zombie.iso.IsoWorld;
import zombie.core.Rand;
import zombie.iso.BuildingDef;

public final class RBBurntFireman extends RandomizedBuildingBase
{
    @Override
    public void randomizeBuilding(final BuildingDef buildingDef) {
        buildingDef.bAlarmed = false;
        final int next = Rand.Next(1, 4);
        buildingDef.setHasBeenVisited(true);
        final IsoCell currentCell = IsoWorld.instance.CurrentCell;
        for (int i = buildingDef.x - 1; i < buildingDef.x2 + 1; ++i) {
            for (int j = buildingDef.y - 1; j < buildingDef.y2 + 1; ++j) {
                for (int k = 0; k < 8; ++k) {
                    final IsoGridSquare gridSquare = currentCell.getGridSquare(i, j, k);
                    if (gridSquare != null && Rand.Next(100) < 70) {
                        gridSquare.Burn(false);
                    }
                }
            }
        }
        buildingDef.setAllExplored(true);
        final ArrayList<IsoZombie> addZombies = this.addZombies(buildingDef, next, "FiremanFullSuit", 35, this.getLivingRoomOrKitchen(buildingDef));
        for (int l = 0; l < addZombies.size(); ++l) {
            addZombies.get(l).getInventory().setExplored(true);
        }
        BaseVehicle baseVehicle;
        if (Rand.NextBool(2)) {
            baseVehicle = this.spawnCarOnNearestNav("Base.PickUpVanLightsFire", buildingDef);
        }
        else {
            baseVehicle = this.spawnCarOnNearestNav("Base.PickUpTruckLightsFire", buildingDef);
        }
        if (baseVehicle != null && !addZombies.isEmpty()) {
            addZombies.get(Rand.Next(addZombies.size())).addItemToSpawnAtDeath(baseVehicle.createVehicleKey());
        }
    }
    
    public RBBurntFireman() {
        this.name = "Burnt Fireman";
        this.setChance(2);
    }
}

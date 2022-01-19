// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedDeadSurvivor;

import zombie.core.Rand;
import zombie.iso.BuildingDef;

public final class RDSTinFoilHat extends RandomizedDeadSurvivorBase
{
    public RDSTinFoilHat() {
        this.name = "Tin foil hat family";
        this.setUnique(true);
        this.setChance(2);
    }
    
    @Override
    public void randomizeDeadSurvivor(final BuildingDef buildingDef) {
        this.addZombies(buildingDef, Rand.Next(2, 5), "TinFoilHat", null, this.getLivingRoomOrKitchen(buildingDef));
    }
}

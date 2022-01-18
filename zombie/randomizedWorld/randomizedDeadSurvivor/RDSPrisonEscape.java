// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedDeadSurvivor;

import zombie.core.Rand;
import zombie.iso.BuildingDef;

public final class RDSPrisonEscape extends RandomizedDeadSurvivorBase
{
    public RDSPrisonEscape() {
        this.name = "Prison Escape";
        this.setChance(3);
        this.setMaximumDays(90);
        this.setUnique(true);
    }
    
    @Override
    public void randomizeDeadSurvivor(final BuildingDef buildingDef) {
        this.addZombies(buildingDef, Rand.Next(2, 4), "InmateEscaped", 0, this.getLivingRoomOrKitchen(buildingDef));
    }
}

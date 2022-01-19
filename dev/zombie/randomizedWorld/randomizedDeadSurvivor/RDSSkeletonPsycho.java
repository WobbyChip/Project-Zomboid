// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedDeadSurvivor;

import java.util.ArrayList;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.RoomDef;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.iso.BuildingDef;

public final class RDSSkeletonPsycho extends RandomizedDeadSurvivorBase
{
    public RDSSkeletonPsycho() {
        this.name = "Skeleton Psycho";
        this.setChance(1);
        this.setMinimumDays(120);
        this.setUnique(true);
    }
    
    @Override
    public void randomizeDeadSurvivor(final BuildingDef buildingDef) {
        final RoomDef room = this.getRoom(buildingDef, "bedroom");
        for (int next = Rand.Next(3, 7), i = 0; i < next; ++i) {
            final IsoDeadBody skeletonCorpse = super.createSkeletonCorpse(room);
            if (skeletonCorpse != null) {
                super.addBloodSplat(skeletonCorpse.getCurrentSquare(), Rand.Next(7, 12));
            }
        }
        final ArrayList<IsoZombie> addZombies = super.addZombies(buildingDef, 1, "Doctor", null, room);
        if (addZombies.isEmpty()) {
            return;
        }
        for (int j = 0; j < 8; ++j) {
            addZombies.get(0).addBlood(null, false, true, false);
        }
    }
}

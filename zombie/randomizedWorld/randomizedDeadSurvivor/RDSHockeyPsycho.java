// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedDeadSurvivor;

import zombie.iso.objects.IsoDeadBody;
import java.util.ArrayList;
import zombie.iso.IsoDirections;
import zombie.randomizedWorld.RandomizedWorldBase;
import zombie.core.Rand;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characters.IsoZombie;
import zombie.iso.BuildingDef;

public final class RDSHockeyPsycho extends RandomizedDeadSurvivorBase
{
    public RDSHockeyPsycho() {
        this.name = "Hockey Psycho (friday 13th!)";
        this.setUnique(true);
        this.setChance(1);
    }
    
    @Override
    public void randomizeDeadSurvivor(final BuildingDef buildingDef) {
        final ArrayList<IsoZombie> addZombies = this.addZombies(buildingDef, 1, "HockeyPsycho", 0, this.getLivingRoomOrKitchen(buildingDef));
        if (addZombies != null && !addZombies.isEmpty()) {
            final IsoZombie isoZombie = addZombies.get(0);
            isoZombie.addBlood(BloodBodyPartType.Head, true, true, true);
            for (int i = 0; i < 10; ++i) {
                isoZombie.addBlood(null, true, false, true);
                isoZombie.addDirt(null, Rand.Next(0, 3), true);
            }
        }
        for (int j = 0; j < 10; ++j) {
            final IsoDeadBody randomDeadBody = RandomizedWorldBase.createRandomDeadBody(this.getRandomRoom(buildingDef, 2), Rand.Next(5, 20));
            if (randomDeadBody != null) {
                this.addTraitOfBlood(IsoDirections.getRandom(), 15, (int)randomDeadBody.x, (int)randomDeadBody.y, (int)randomDeadBody.z);
                this.addTraitOfBlood(IsoDirections.getRandom(), 15, (int)randomDeadBody.x, (int)randomDeadBody.y, (int)randomDeadBody.z);
                this.addTraitOfBlood(IsoDirections.getRandom(), 15, (int)randomDeadBody.x, (int)randomDeadBody.y, (int)randomDeadBody.z);
            }
        }
    }
}

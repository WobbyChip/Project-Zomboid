// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedDeadSurvivor;

import java.util.ArrayList;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.RoomDef;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characters.IsoZombie;
import zombie.randomizedWorld.RandomizedWorldBase;
import zombie.core.Rand;
import zombie.iso.BuildingDef;

public final class RDSCorpsePsycho extends RandomizedDeadSurvivorBase
{
    public RDSCorpsePsycho() {
        this.name = "Corpse Psycho";
        this.setChance(1);
        this.setMinimumDays(120);
        this.setUnique(true);
    }
    
    @Override
    public void randomizeDeadSurvivor(final BuildingDef buildingDef) {
        final RoomDef room = this.getRoom(buildingDef, "kitchen");
        for (int next = Rand.Next(3, 7), i = 0; i < next; ++i) {
            final IsoDeadBody randomDeadBody = RandomizedWorldBase.createRandomDeadBody(room, Rand.Next(5, 10));
            if (randomDeadBody != null) {
                super.addBloodSplat(randomDeadBody.getCurrentSquare(), Rand.Next(7, 12));
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

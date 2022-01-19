// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedDeadSurvivor;

import zombie.iso.objects.IsoDeadBody;
import zombie.iso.IsoGridSquare;
import zombie.characters.IsoGameCharacter;
import java.util.List;
import zombie.util.list.PZArrayUtil;
import zombie.inventory.ItemPickerJava;
import zombie.iso.IsoDirections;
import zombie.randomizedWorld.RandomizedWorldBase;
import zombie.iso.BuildingDef;
import java.util.ArrayList;

public final class RDSSpecificProfession extends RandomizedDeadSurvivorBase
{
    private final ArrayList<String> specificProfessionDistribution;
    
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
        ItemPickerJava.rollItem((ItemPickerJava.ItemPickerContainer)((ItemPickerJava.ItemPickerRoom)ItemPickerJava.rooms.get((Object)PZArrayUtil.pickRandom(this.specificProfessionDistribution))).Containers.get((Object)"counter"), randomDeadBody.getContainer(), true, null, null);
    }
    
    public RDSSpecificProfession() {
        (this.specificProfessionDistribution = new ArrayList<String>()).add("Carpenter");
        this.specificProfessionDistribution.add("Electrician");
        this.specificProfessionDistribution.add("Farmer");
        this.specificProfessionDistribution.add("Nurse");
    }
}

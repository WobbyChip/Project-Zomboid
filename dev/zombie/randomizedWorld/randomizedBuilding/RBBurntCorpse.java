// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedBuilding;

import java.util.ArrayList;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoCell;
import zombie.characters.IsoGameCharacter;
import zombie.iso.objects.IsoDeadBody;
import zombie.characters.IsoZombie;
import zombie.iso.RoomDef;
import zombie.core.Rand;
import zombie.iso.IsoWorld;
import zombie.iso.BuildingDef;

public final class RBBurntCorpse extends RandomizedBuildingBase
{
    @Override
    public void randomizeBuilding(final BuildingDef buildingDef) {
        buildingDef.bAlarmed = false;
        buildingDef.setHasBeenVisited(true);
        final IsoCell currentCell = IsoWorld.instance.CurrentCell;
        for (int i = buildingDef.x - 1; i < buildingDef.x2 + 1; ++i) {
            for (int j = buildingDef.y - 1; j < buildingDef.y2 + 1; ++j) {
                for (int k = 0; k < 8; ++k) {
                    final IsoGridSquare gridSquare = currentCell.getGridSquare(i, j, k);
                    if (gridSquare != null && Rand.Next(100) < 60) {
                        gridSquare.Burn(false);
                    }
                }
            }
        }
        buildingDef.setAllExplored(true);
        buildingDef.bAlarmed = false;
        final ArrayList<IsoZombie> addZombies = this.addZombies(buildingDef, Rand.Next(3, 7), null, null, null);
        if (addZombies == null) {
            return;
        }
        for (int l = 0; l < addZombies.size(); ++l) {
            final IsoZombie isoZombie = addZombies.get(l);
            isoZombie.setSkeleton(true);
            isoZombie.getHumanVisual().setSkinTextureIndex(0);
            new IsoDeadBody(isoZombie, false);
        }
    }
    
    public RBBurntCorpse() {
        this.name = "Burnt with corpses";
        this.setChance(3);
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedBuilding;

import zombie.iso.IsoGridSquare;
import zombie.iso.IsoCell;
import zombie.core.Rand;
import zombie.iso.IsoWorld;
import zombie.iso.BuildingDef;

public final class RBBurnt extends RandomizedBuildingBase
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
                    if (gridSquare != null && Rand.Next(100) < 90) {
                        gridSquare.Burn(false);
                    }
                }
            }
        }
        buildingDef.setAllExplored(true);
        buildingDef.bAlarmed = false;
    }
    
    @Override
    public boolean isValid(final BuildingDef buildingDef, final boolean b) {
        return super.isValid(buildingDef, b) && buildingDef.getRooms().size() <= 10;
    }
    
    public RBBurnt() {
        this.name = "Burnt";
        this.setChance(3);
    }
}

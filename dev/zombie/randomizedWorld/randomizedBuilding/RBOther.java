// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedBuilding;

import zombie.iso.IsoGridSquare;
import zombie.iso.IsoCell;
import zombie.inventory.ItemPickerJava;
import zombie.core.Rand;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.BuildingDef;

public final class RBOther extends RandomizedBuildingBase
{
    @Override
    public void randomizeBuilding(final BuildingDef buildingDef) {
        buildingDef.bAlarmed = false;
        buildingDef.setHasBeenVisited(true);
        buildingDef.setAllExplored(true);
        final IsoCell currentCell = IsoWorld.instance.CurrentCell;
        for (int i = buildingDef.x - 1; i < buildingDef.x2 + 1; ++i) {
            for (int j = buildingDef.y - 1; j < buildingDef.y2 + 1; ++j) {
                for (int k = 0; k < 8; ++k) {
                    final IsoGridSquare gridSquare = currentCell.getGridSquare(i, j, k);
                    if (gridSquare != null) {
                        for (int l = 0; l < gridSquare.getObjects().size(); ++l) {
                            final IsoObject isoObject = gridSquare.getObjects().get(l);
                            if (isoObject.getContainer() != null) {
                                isoObject.getContainer().emptyIt();
                                isoObject.getContainer().AddItems("Base.ToiletPaper", Rand.Next(10, 30));
                                ItemPickerJava.updateOverlaySprite(isoObject);
                            }
                        }
                    }
                }
            }
        }
    }
    
    public RBOther() {
        this.name = "Other";
        this.setChance(1);
        this.setUnique(true);
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedBuilding;

import zombie.iso.IsoGridSquare;
import zombie.iso.IsoCell;
import zombie.core.Rand;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.BuildingDef;

public final class RBPizzaWhirled extends RandomizedBuildingBase
{
    @Override
    public void randomizeBuilding(final BuildingDef buildingDef) {
        final IsoCell currentCell = IsoWorld.instance.CurrentCell;
        for (int i = buildingDef.x - 1; i < buildingDef.x2 + 1; ++i) {
            for (int j = buildingDef.y - 1; j < buildingDef.y2 + 1; ++j) {
                for (int k = 0; k < 8; ++k) {
                    final IsoGridSquare gridSquare = currentCell.getGridSquare(i, j, k);
                    if (gridSquare != null && this.roomValid(gridSquare)) {
                        for (int l = 0; l < gridSquare.getObjects().size(); ++l) {
                            final IsoObject isoObject = gridSquare.getObjects().get(l);
                            if (Rand.NextBool(2) && this.isTableFor3DItems(isoObject, gridSquare)) {
                                this.addWorldItem("Pizza", gridSquare, isoObject);
                                if (Rand.NextBool(3)) {
                                    this.addWorldItem("Fork", gridSquare, isoObject);
                                }
                                if (Rand.NextBool(3)) {
                                    this.addWorldItem("ButterKnife", gridSquare, isoObject);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public boolean roomValid(final IsoGridSquare isoGridSquare) {
        return isoGridSquare.getRoom() != null && ("pizzawhirled".equals(isoGridSquare.getRoom().getName()) || "italianrestaurant".equals(isoGridSquare.getRoom().getName()));
    }
    
    @Override
    public boolean isValid(final BuildingDef buildingDef, final boolean b) {
        return buildingDef.getRoom("pizzawhirled") != null || buildingDef.getRoom("italianrestaurant") != null || b;
    }
    
    public RBPizzaWhirled() {
        this.name = "Pizza Whirled Restaurant";
        this.setAlwaysDo(true);
    }
}

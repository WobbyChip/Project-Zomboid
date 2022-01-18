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

public final class RBSpiffo extends RandomizedBuildingBase
{
    @Override
    public void randomizeBuilding(final BuildingDef buildingDef) {
        final IsoCell currentCell = IsoWorld.instance.CurrentCell;
        for (int i = buildingDef.x - 1; i < buildingDef.x2 + 1; ++i) {
            for (int j = buildingDef.y - 1; j < buildingDef.y2 + 1; ++j) {
                for (int k = 0; k < 8; ++k) {
                    final IsoGridSquare gridSquare = currentCell.getGridSquare(i, j, k);
                    if (gridSquare != null && this.roomValid(gridSquare)) {
                        int l = 0;
                        while (l < gridSquare.getObjects().size()) {
                            final IsoObject isoObject = gridSquare.getObjects().get(l);
                            if (Rand.NextBool(2) && this.isTableFor3DItems(isoObject, gridSquare)) {
                                System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;II)Ljava/lang/String;, isoObject.getSprite().getName(), gridSquare.x, gridSquare.y));
                                if (Rand.NextBool(2)) {
                                    this.addWorldItem("Burger", gridSquare, isoObject);
                                }
                                if (Rand.NextBool(2)) {
                                    this.addWorldItem("Fries", gridSquare, isoObject);
                                }
                                if (Rand.NextBool(2)) {
                                    this.addWorldItem("Ketchup", gridSquare, isoObject);
                                }
                                if (Rand.NextBool(3)) {
                                    this.addWorldItem("Fork", gridSquare, isoObject);
                                }
                                if (Rand.NextBool(3)) {
                                    this.addWorldItem("ButterKnife", gridSquare, isoObject);
                                }
                                if (Rand.NextBool(30)) {
                                    this.addWorldItem("MugSpiffo", gridSquare, isoObject);
                                    break;
                                }
                                break;
                            }
                            else {
                                ++l;
                            }
                        }
                    }
                }
            }
        }
    }
    
    public boolean roomValid(final IsoGridSquare isoGridSquare) {
        return isoGridSquare.getRoom() != null && ("spiffo_dining".equals(isoGridSquare.getRoom().getName()) || "burgerkitchen".equals(isoGridSquare.getRoom().getName()));
    }
    
    @Override
    public boolean isValid(final BuildingDef buildingDef, final boolean b) {
        return buildingDef.getRoom("spiffo_dining") != null || buildingDef.getRoom("burgerkitchen") != null || b;
    }
    
    public RBSpiffo() {
        this.name = "Spiffo Restaurant";
        this.setAlwaysDo(true);
    }
}

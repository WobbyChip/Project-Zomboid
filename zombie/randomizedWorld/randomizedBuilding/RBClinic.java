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

public final class RBClinic extends RandomizedBuildingBase
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
                            if (Rand.NextBool(2) && isoObject.getSurfaceOffsetNoTable() > 0.0f && isoObject.getContainer() == null && gridSquare.getProperties().Val("waterAmount") == null && !isoObject.hasWater()) {
                                for (int next = Rand.Next(1, 3), n = 0; n < next; ++n) {
                                    switch (Rand.Next(12)) {
                                        case 0: {
                                            this.addWorldItem("Scalpel", gridSquare, Rand.Next(0.4f, 0.6f), Rand.Next(0.4f, 0.6f), isoObject.getSurfaceOffsetNoTable() / 96.0f);
                                            break;
                                        }
                                        case 1: {
                                            this.addWorldItem("Bandage", gridSquare, Rand.Next(0.4f, 0.6f), Rand.Next(0.4f, 0.6f), isoObject.getSurfaceOffsetNoTable() / 96.0f);
                                            break;
                                        }
                                        case 2: {
                                            this.addWorldItem("Pills", gridSquare, Rand.Next(0.4f, 0.6f), Rand.Next(0.4f, 0.6f), isoObject.getSurfaceOffsetNoTable() / 96.0f);
                                            break;
                                        }
                                        case 3: {
                                            this.addWorldItem("AlcoholWipes", gridSquare, Rand.Next(0.4f, 0.6f), Rand.Next(0.4f, 0.6f), isoObject.getSurfaceOffsetNoTable() / 96.0f);
                                            break;
                                        }
                                        case 4: {
                                            this.addWorldItem("Bandaid", gridSquare, Rand.Next(0.4f, 0.6f), Rand.Next(0.4f, 0.6f), isoObject.getSurfaceOffsetNoTable() / 96.0f);
                                            break;
                                        }
                                        case 5: {
                                            this.addWorldItem("CottonBalls", gridSquare, Rand.Next(0.4f, 0.6f), Rand.Next(0.4f, 0.6f), isoObject.getSurfaceOffsetNoTable() / 96.0f);
                                            break;
                                        }
                                        case 6: {
                                            this.addWorldItem("Disinfectant", gridSquare, Rand.Next(0.4f, 0.6f), Rand.Next(0.4f, 0.6f), isoObject.getSurfaceOffsetNoTable() / 96.0f);
                                            break;
                                        }
                                        case 7: {
                                            this.addWorldItem("SutureNeedle", gridSquare, Rand.Next(0.4f, 0.6f), Rand.Next(0.4f, 0.6f), isoObject.getSurfaceOffsetNoTable() / 96.0f);
                                            break;
                                        }
                                        case 8: {
                                            this.addWorldItem("SutureNeedleHolder", gridSquare, Rand.Next(0.4f, 0.6f), Rand.Next(0.4f, 0.6f), isoObject.getSurfaceOffsetNoTable() / 96.0f);
                                            break;
                                        }
                                        case 9: {
                                            this.addWorldItem("Tweezers", gridSquare, Rand.Next(0.4f, 0.6f), Rand.Next(0.4f, 0.6f), isoObject.getSurfaceOffsetNoTable() / 96.0f);
                                            break;
                                        }
                                        case 10: {
                                            this.addWorldItem("Gloves_Surgical", gridSquare, Rand.Next(0.4f, 0.6f), Rand.Next(0.4f, 0.6f), isoObject.getSurfaceOffsetNoTable() / 96.0f);
                                            break;
                                        }
                                        case 11: {
                                            this.addWorldItem("Hat_SurgicalMask_Blue", gridSquare, Rand.Next(0.4f, 0.6f), Rand.Next(0.4f, 0.6f), isoObject.getSurfaceOffsetNoTable() / 96.0f);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public boolean roomValid(final IsoGridSquare isoGridSquare) {
        return isoGridSquare.getRoom() != null && ("hospitalroom".equals(isoGridSquare.getRoom().getName()) || "clinic".equals(isoGridSquare.getRoom().getName()) || "medical".equals(isoGridSquare.getRoom().getName()));
    }
    
    @Override
    public boolean isValid(final BuildingDef buildingDef, final boolean b) {
        return buildingDef.getRoom("medical") != null || buildingDef.getRoom("clinic") != null || b;
    }
    
    public RBClinic() {
        this.name = "Clinic (Vet, Doctor..)";
        this.setAlwaysDo(true);
    }
}

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

public final class RBSchool extends RandomizedBuildingBase
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
                            if (Rand.NextBool(3) && this.isTableFor3DItems(isoObject, gridSquare)) {
                                switch (Rand.Next(0, 8)) {
                                    case 0: {
                                        gridSquare.AddWorldInventoryItem("Pen", Rand.Next(0.4f, 0.8f), Rand.Next(0.4f, 0.8f), isoObject.getSurfaceOffsetNoTable() / 96.0f);
                                        break;
                                    }
                                    case 1: {
                                        gridSquare.AddWorldInventoryItem("Pencil", Rand.Next(0.4f, 0.8f), Rand.Next(0.4f, 0.8f), isoObject.getSurfaceOffsetNoTable() / 96.0f);
                                        break;
                                    }
                                    case 2: {
                                        gridSquare.AddWorldInventoryItem("Crayons", Rand.Next(0.4f, 0.8f), Rand.Next(0.4f, 0.8f), isoObject.getSurfaceOffsetNoTable() / 96.0f);
                                        break;
                                    }
                                    case 3: {
                                        gridSquare.AddWorldInventoryItem("RedPen", Rand.Next(0.4f, 0.8f), Rand.Next(0.4f, 0.8f), isoObject.getSurfaceOffsetNoTable() / 96.0f);
                                        break;
                                    }
                                    case 4: {
                                        gridSquare.AddWorldInventoryItem("BluePen", Rand.Next(0.4f, 0.8f), Rand.Next(0.4f, 0.8f), isoObject.getSurfaceOffsetNoTable() / 96.0f);
                                        break;
                                    }
                                    case 5: {
                                        gridSquare.AddWorldInventoryItem("Eraser", Rand.Next(0.4f, 0.8f), Rand.Next(0.4f, 0.8f), isoObject.getSurfaceOffsetNoTable() / 96.0f);
                                        break;
                                    }
                                }
                                switch (Rand.Next(0, 6)) {
                                    case 0: {
                                        gridSquare.AddWorldInventoryItem("Doodle", Rand.Next(0.4f, 0.8f), Rand.Next(0.4f, 0.8f), isoObject.getSurfaceOffsetNoTable() / 96.0f);
                                        break;
                                    }
                                    case 1: {
                                        gridSquare.AddWorldInventoryItem("Book", Rand.Next(0.4f, 0.8f), Rand.Next(0.4f, 0.8f), isoObject.getSurfaceOffsetNoTable() / 96.0f);
                                        break;
                                    }
                                    case 2: {
                                        gridSquare.AddWorldInventoryItem("Notebook", Rand.Next(0.4f, 0.8f), Rand.Next(0.4f, 0.8f), isoObject.getSurfaceOffsetNoTable() / 96.0f);
                                        break;
                                    }
                                    case 3: {
                                        gridSquare.AddWorldInventoryItem("SheetPaper2", Rand.Next(0.4f, 0.8f), Rand.Next(0.4f, 0.8f), isoObject.getSurfaceOffsetNoTable() / 96.0f);
                                        break;
                                    }
                                }
                            }
                        }
                        if (gridSquare.getRoom() != null && "classroom".equals(gridSquare.getRoom().getName())) {
                            if (Rand.NextBool(50)) {
                                switch (Rand.Next(0, 10)) {
                                    case 0: {
                                        gridSquare.AddWorldInventoryItem("Doodle", Rand.Next(0.4f, 0.8f), Rand.Next(0.4f, 0.8f), 0.0f);
                                        break;
                                    }
                                    case 1: {
                                        gridSquare.AddWorldInventoryItem("Book", Rand.Next(0.4f, 0.8f), Rand.Next(0.4f, 0.8f), 0.0f);
                                        break;
                                    }
                                    case 2: {
                                        gridSquare.AddWorldInventoryItem("Notebook", Rand.Next(0.4f, 0.8f), Rand.Next(0.4f, 0.8f), 0.0f);
                                        break;
                                    }
                                    case 3: {
                                        gridSquare.AddWorldInventoryItem("SheetPaper2", Rand.Next(0.4f, 0.8f), Rand.Next(0.4f, 0.8f), 0.0f);
                                        break;
                                    }
                                    case 4: {
                                        gridSquare.AddWorldInventoryItem("Pen", Rand.Next(0.4f, 0.8f), Rand.Next(0.4f, 0.8f), 0.0f);
                                        break;
                                    }
                                    case 5: {
                                        gridSquare.AddWorldInventoryItem("Pencil", Rand.Next(0.4f, 0.8f), Rand.Next(0.4f, 0.8f), 0.0f);
                                        break;
                                    }
                                    case 6: {
                                        gridSquare.AddWorldInventoryItem("Crayons", Rand.Next(0.4f, 0.8f), Rand.Next(0.4f, 0.8f), 0.0f);
                                        break;
                                    }
                                    case 7: {
                                        gridSquare.AddWorldInventoryItem("RedPen", Rand.Next(0.4f, 0.8f), Rand.Next(0.4f, 0.8f), 0.0f);
                                        break;
                                    }
                                    case 8: {
                                        gridSquare.AddWorldInventoryItem("BluePen", Rand.Next(0.4f, 0.8f), Rand.Next(0.4f, 0.8f), 0.0f);
                                        break;
                                    }
                                    case 9: {
                                        gridSquare.AddWorldInventoryItem("Eraser", Rand.Next(0.4f, 0.8f), Rand.Next(0.4f, 0.8f), 0.0f);
                                        break;
                                    }
                                }
                            }
                            if (Rand.NextBool(120)) {
                                gridSquare.AddWorldInventoryItem("Bag_Schoolbag", Rand.Next(0.4f, 0.8f), Rand.Next(0.4f, 0.8f), 0.0f);
                            }
                        }
                    }
                }
            }
        }
    }
    
    public boolean roomValid(final IsoGridSquare isoGridSquare) {
        return isoGridSquare.getRoom() != null && "classroom".equals(isoGridSquare.getRoom().getName());
    }
    
    @Override
    public boolean isValid(final BuildingDef buildingDef, final boolean b) {
        return buildingDef.getRoom("classroom") != null || b;
    }
    
    public RBSchool() {
        this.name = "School";
        this.setAlwaysDo(true);
    }
}

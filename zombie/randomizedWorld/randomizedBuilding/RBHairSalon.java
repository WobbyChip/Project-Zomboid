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

public final class RBHairSalon extends RandomizedBuildingBase
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
                            if (Rand.NextBool(3) && isoObject.getSurfaceOffsetNoTable() > 0.0f && gridSquare.getProperties().Val("waterAmount") == null && !isoObject.hasWater() && isoObject.getProperties().Val("BedType") == null) {
                                switch (Rand.Next(12)) {
                                    case 0: {
                                        this.addWorldItem("Comb", gridSquare, 0.5f, 0.5f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                                        break;
                                    }
                                    case 1: {
                                        this.addWorldItem("HairDyeBlonde", gridSquare, 0.5f, 0.5f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                                        break;
                                    }
                                    case 2: {
                                        this.addWorldItem("HairDyeBlack", gridSquare, 0.5f, 0.5f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                                        break;
                                    }
                                    case 3: {
                                        this.addWorldItem("HairDyeWhite", gridSquare, 0.5f, 0.5f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                                        break;
                                    }
                                    case 5: {
                                        this.addWorldItem("HairDyePink", gridSquare, 0.5f, 0.5f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                                        break;
                                    }
                                    case 6: {
                                        this.addWorldItem("HairDyeYellow", gridSquare, 0.5f, 0.5f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                                        break;
                                    }
                                    case 7: {
                                        this.addWorldItem("HairDyeRed", gridSquare, 0.5f, 0.5f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                                        break;
                                    }
                                    case 8: {
                                        this.addWorldItem("HairDyeGinger", gridSquare, 0.5f, 0.5f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                                        break;
                                    }
                                    case 9: {
                                        this.addWorldItem("Hairgel", gridSquare, 0.5f, 0.5f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                                        break;
                                    }
                                    case 10: {
                                        this.addWorldItem("Hairspray", gridSquare, 0.5f, 0.5f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                                        break;
                                    }
                                    case 11: {
                                        this.addWorldItem("Razor", gridSquare, 0.5f, 0.5f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
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
    
    public boolean roomValid(final IsoGridSquare isoGridSquare) {
        return isoGridSquare.getRoom() != null && "aesthetic".equals(isoGridSquare.getRoom().getName());
    }
    
    @Override
    public boolean isValid(final BuildingDef buildingDef, final boolean b) {
        return buildingDef.getRoom("aesthetic") != null || b;
    }
    
    public RBHairSalon() {
        this.name = "Hair Salon";
        this.setAlwaysDo(true);
    }
}

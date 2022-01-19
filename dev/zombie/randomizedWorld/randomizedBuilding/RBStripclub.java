// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedBuilding;

import zombie.iso.RoomDef;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoCell;
import zombie.iso.IsoObject;
import java.util.ArrayList;
import zombie.core.Rand;
import zombie.iso.IsoWorld;
import zombie.iso.BuildingDef;

public final class RBStripclub extends RandomizedBuildingBase
{
    @Override
    public void randomizeBuilding(final BuildingDef buildingDef) {
        buildingDef.bAlarmed = false;
        buildingDef.setHasBeenVisited(true);
        buildingDef.setAllExplored(true);
        final IsoCell currentCell = IsoWorld.instance.CurrentCell;
        final boolean nextBool = Rand.NextBool(20);
        final ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = buildingDef.x - 1; i < buildingDef.x2 + 1; ++i) {
            for (int j = buildingDef.y - 1; j < buildingDef.y2 + 1; ++j) {
                for (int k = 0; k < 8; ++k) {
                    final IsoGridSquare gridSquare = currentCell.getGridSquare(i, j, k);
                    if (gridSquare != null) {
                        for (int l = 0; l < gridSquare.getObjects().size(); ++l) {
                            final IsoObject isoObject = gridSquare.getObjects().get(l);
                            if (Rand.NextBool(2) && "location_restaurant_pizzawhirled_01_16".equals(isoObject.getSprite().getName())) {
                                for (int next = Rand.Next(1, 4), n = 0; n < next; ++n) {
                                    gridSquare.AddWorldInventoryItem("Money", Rand.Next(0.0f, 0.5f), Rand.Next(0.0f, 0.5f), 0.0f);
                                }
                                for (int next2 = Rand.Next(1, 4), n2 = 0; n2 < next2; ++n2) {
                                    int m;
                                    for (m = Rand.Next(1, 7); list.contains(m); m = Rand.Next(1, 7)) {}
                                    switch (m) {
                                        case 1: {
                                            gridSquare.AddWorldInventoryItem(nextBool ? "Trousers" : "TightsFishnet_Ground", Rand.Next(0.0f, 0.5f), Rand.Next(0.0f, 0.5f), 0.0f);
                                            list.add(1);
                                            break;
                                        }
                                        case 2: {
                                            gridSquare.AddWorldInventoryItem("Vest_DefaultTEXTURE_TINT", Rand.Next(0.0f, 0.5f), Rand.Next(0.0f, 0.5f), 0.0f);
                                            list.add(2);
                                            break;
                                        }
                                        case 3: {
                                            gridSquare.AddWorldInventoryItem(nextBool ? "Jacket_Fireman" : "BunnySuitBlack", Rand.Next(0.0f, 0.5f), Rand.Next(0.0f, 0.5f), 0.0f);
                                            list.add(3);
                                            break;
                                        }
                                        case 4: {
                                            gridSquare.AddWorldInventoryItem(nextBool ? "Hat_Cowboy" : "Garter", Rand.Next(0.0f, 0.5f), Rand.Next(0.0f, 0.5f), 0.0f);
                                            list.add(4);
                                            break;
                                        }
                                        case 5: {
                                            if (!nextBool) {
                                                gridSquare.AddWorldInventoryItem("StockingsBlack", Rand.Next(0.0f, 0.5f), Rand.Next(0.0f, 0.5f), 0.0f);
                                            }
                                            list.add(5);
                                            break;
                                        }
                                    }
                                }
                            }
                            if ("furniture_tables_high_01_16".equals(isoObject.getSprite().getName()) || "furniture_tables_high_01_17".equals(isoObject.getSprite().getName()) || "furniture_tables_high_01_18".equals(isoObject.getSprite().getName())) {
                                for (int next3 = Rand.Next(1, 4), n3 = 0; n3 < next3; ++n3) {
                                    gridSquare.AddWorldInventoryItem("Money", Rand.Next(0.5f, 1.0f), Rand.Next(0.5f, 1.0f), isoObject.getSurfaceOffsetNoTable() / 96.0f);
                                }
                                if (Rand.NextBool(3)) {
                                    this.addWorldItem("Cigarettes", gridSquare, isoObject);
                                    if (Rand.NextBool(2)) {
                                        this.addWorldItem("Lighter", gridSquare, isoObject);
                                    }
                                }
                                switch (Rand.Next(7)) {
                                    case 0: {
                                        gridSquare.AddWorldInventoryItem("WhiskeyFull", Rand.Next(0.5f, 1.0f), Rand.Next(0.5f, 1.0f), isoObject.getSurfaceOffsetNoTable() / 96.0f);
                                        break;
                                    }
                                    case 1: {
                                        gridSquare.AddWorldInventoryItem("Wine", Rand.Next(0.5f, 1.0f), Rand.Next(0.5f, 1.0f), isoObject.getSurfaceOffsetNoTable() / 96.0f);
                                        break;
                                    }
                                    case 2: {
                                        gridSquare.AddWorldInventoryItem("Wine2", Rand.Next(0.5f, 1.0f), Rand.Next(0.5f, 1.0f), isoObject.getSurfaceOffsetNoTable() / 96.0f);
                                        break;
                                    }
                                    case 3: {
                                        gridSquare.AddWorldInventoryItem("BeerCan", Rand.Next(0.5f, 1.0f), Rand.Next(0.5f, 1.0f), isoObject.getSurfaceOffsetNoTable() / 96.0f);
                                        break;
                                    }
                                    case 4: {
                                        gridSquare.AddWorldInventoryItem("BeerBottle", Rand.Next(0.5f, 1.0f), Rand.Next(0.5f, 1.0f), isoObject.getSurfaceOffsetNoTable() / 96.0f);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        final RoomDef room = buildingDef.getRoom("stripclub");
        if (nextBool) {
            this.addZombies(buildingDef, Rand.Next(2, 4), "WaiterStripper", 0, room);
            this.addZombies(buildingDef, 1, "PoliceStripper", 0, room);
            this.addZombies(buildingDef, 1, "FiremanStripper", 0, room);
            this.addZombies(buildingDef, 1, "CowboyStripper", 0, room);
            this.addZombies(buildingDef, Rand.Next(9, 15), null, 100, room);
        }
        else {
            this.addZombies(buildingDef, Rand.Next(2, 4), "WaiterStripper", 100, room);
            this.addZombies(buildingDef, Rand.Next(2, 5), "StripperNaked", 100, room);
            this.addZombies(buildingDef, Rand.Next(2, 5), "StripperBlack", 100, room);
            this.addZombies(buildingDef, Rand.Next(2, 5), "StripperWhite", 100, room);
            this.addZombies(buildingDef, Rand.Next(9, 15), null, 0, room);
        }
    }
    
    @Override
    public boolean isValid(final BuildingDef buildingDef, final boolean b) {
        return buildingDef.getRoom("stripclub") != null || b;
    }
    
    public RBStripclub() {
        this.name = "Stripclub";
        this.setAlwaysDo(true);
    }
}

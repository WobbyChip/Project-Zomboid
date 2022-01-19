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

public final class RBBar extends RandomizedBuildingBase
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
                            if (isoObject.getSprite() != null && isoObject.getSprite().getName() != null && (isoObject.getSprite().getName().equals("recreational_01_6") || isoObject.getSprite().getName().equals("recreational_01_7"))) {
                                if (Rand.NextBool(3)) {
                                    this.addWorldItem("PoolBall", gridSquare, isoObject);
                                }
                                if (Rand.NextBool(3)) {
                                    this.addWorldItem("Poolcue", gridSquare, isoObject);
                                }
                            }
                            else if (isoObject.isTableSurface() && Rand.NextBool(2)) {
                                if (Rand.NextBool(3)) {
                                    this.addWorldItem("Cigarettes", gridSquare, isoObject);
                                    if (Rand.NextBool(2)) {
                                        this.addWorldItem("Lighter", gridSquare, isoObject);
                                    }
                                }
                                switch (Rand.Next(7)) {
                                    case 0: {
                                        this.addWorldItem("WhiskeyFull", gridSquare, isoObject);
                                        break;
                                    }
                                    case 1: {
                                        this.addWorldItem("Wine", gridSquare, isoObject);
                                        break;
                                    }
                                    case 2: {
                                        this.addWorldItem("Wine2", gridSquare, isoObject);
                                        break;
                                    }
                                    case 3: {
                                        this.addWorldItem("BeerCan", gridSquare, isoObject);
                                        break;
                                    }
                                    case 4: {
                                        this.addWorldItem("BeerBottle", gridSquare, isoObject);
                                        break;
                                    }
                                }
                                if (Rand.NextBool(3)) {
                                    switch (Rand.Next(7)) {
                                        case 0: {
                                            this.addWorldItem("Crisps", gridSquare, isoObject);
                                            break;
                                        }
                                        case 1: {
                                            this.addWorldItem("Crisps2", gridSquare, isoObject);
                                            break;
                                        }
                                        case 2: {
                                            this.addWorldItem("Crisps3", gridSquare, isoObject);
                                            break;
                                        }
                                        case 3: {
                                            this.addWorldItem("Crisps4", gridSquare, isoObject);
                                            break;
                                        }
                                        case 4: {
                                            this.addWorldItem("Peanuts", gridSquare, isoObject);
                                            break;
                                        }
                                    }
                                }
                                if (Rand.NextBool(4)) {
                                    this.addWorldItem("CardDeck", gridSquare, isoObject);
                                }
                            }
                        }
                        if (Rand.NextBool(20) && gridSquare.getRoom() != null && gridSquare.getRoom().getName().equals("bar") && gridSquare.getObjects().size() == 1 && Rand.NextBool(8)) {
                            this.addWorldItem("Dart", gridSquare, null);
                        }
                    }
                }
            }
        }
    }
    
    public boolean roomValid(final IsoGridSquare isoGridSquare) {
        return isoGridSquare.getRoom() != null && "bar".equals(isoGridSquare.getRoom().getName());
    }
    
    @Override
    public boolean isValid(final BuildingDef buildingDef, final boolean b) {
        return (buildingDef.getRoom("bar") != null && buildingDef.getRoom("stripclub") == null) || b;
    }
    
    public RBBar() {
        this.name = "Bar";
        this.setAlwaysDo(true);
    }
}

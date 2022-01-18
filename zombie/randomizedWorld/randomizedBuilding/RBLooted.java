// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedBuilding;

import zombie.iso.IsoGridSquare;
import zombie.iso.IsoCell;
import zombie.inventory.ItemPickerJava;
import zombie.iso.objects.IsoWindow;
import zombie.characters.IsoGameCharacter;
import zombie.iso.objects.IsoDoor;
import zombie.core.Rand;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.BuildingDef;

public final class RBLooted extends RandomizedBuildingBase
{
    @Override
    public void randomizeBuilding(final BuildingDef buildingDef) {
        buildingDef.bAlarmed = false;
        final IsoCell currentCell = IsoWorld.instance.CurrentCell;
        for (int i = buildingDef.x - 1; i < buildingDef.x2 + 1; ++i) {
            for (int j = buildingDef.y - 1; j < buildingDef.y2 + 1; ++j) {
                for (int k = 0; k < 8; ++k) {
                    final IsoGridSquare gridSquare = currentCell.getGridSquare(i, j, k);
                    if (gridSquare != null) {
                        for (int l = 0; l < gridSquare.getObjects().size(); ++l) {
                            final IsoObject isoObject = gridSquare.getObjects().get(l);
                            if (Rand.Next(100) >= 85 && isoObject instanceof IsoDoor && ((IsoDoor)isoObject).isExteriorDoor(null)) {
                                ((IsoDoor)isoObject).destroy();
                            }
                            if (Rand.Next(100) >= 85 && isoObject instanceof IsoWindow) {
                                ((IsoWindow)isoObject).smashWindow(false, false);
                            }
                            if (isoObject.getContainer() != null && isoObject.getContainer().getItems() != null) {
                                for (int index = 0; index < isoObject.getContainer().getItems().size(); ++index) {
                                    if (Rand.Next(100) < 80) {
                                        isoObject.getContainer().getItems().remove(index);
                                        --index;
                                    }
                                }
                                ItemPickerJava.updateOverlaySprite(isoObject);
                                isoObject.getContainer().setExplored(true);
                            }
                        }
                    }
                }
            }
        }
        buildingDef.setAllExplored(true);
        buildingDef.bAlarmed = false;
    }
    
    public RBLooted() {
        this.name = "Looted";
        this.setChance(10);
    }
}

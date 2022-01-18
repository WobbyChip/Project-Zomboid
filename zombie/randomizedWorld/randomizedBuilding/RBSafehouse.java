// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedBuilding;

import zombie.randomizedWorld.RandomizedWorldBase;
import zombie.iso.RoomDef;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoCell;
import zombie.iso.objects.IsoWindow;
import zombie.network.GameServer;
import zombie.inventory.InventoryItem;
import zombie.characters.IsoGameCharacter;
import zombie.core.Rand;
import zombie.iso.objects.interfaces.BarricadeAble;
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoDoor;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.inventory.ItemPickerJava;
import zombie.iso.BuildingDef;

public final class RBSafehouse extends RandomizedBuildingBase
{
    @Override
    public void randomizeBuilding(final BuildingDef buildingDef) {
        buildingDef.bAlarmed = false;
        buildingDef.setHasBeenVisited(true);
        final ItemPickerJava.ItemPickerRoom itemPickerRoom = (ItemPickerJava.ItemPickerRoom)ItemPickerJava.rooms.get((Object)"SafehouseLoot");
        final IsoCell currentCell = IsoWorld.instance.CurrentCell;
        for (int i = buildingDef.x - 1; i < buildingDef.x2 + 1; ++i) {
            for (int j = buildingDef.y - 1; j < buildingDef.y2 + 1; ++j) {
                for (int k = 0; k < 8; ++k) {
                    final IsoGridSquare gridSquare = currentCell.getGridSquare(i, j, k);
                    if (gridSquare != null) {
                        for (int l = 0; l < gridSquare.getObjects().size(); ++l) {
                            final IsoObject isoObject = gridSquare.getObjects().get(l);
                            if (isoObject instanceof IsoDoor && ((IsoDoor)isoObject).isBarricadeAllowed()) {
                                final IsoGridSquare isoGridSquare = (gridSquare.getRoom() == null) ? gridSquare : ((IsoDoor)isoObject).getOppositeSquare();
                                if (isoGridSquare != null && isoGridSquare.getRoom() == null) {
                                    final IsoBarricade addBarricadeToObject = IsoBarricade.AddBarricadeToObject((BarricadeAble)isoObject, isoGridSquare != gridSquare);
                                    if (addBarricadeToObject != null) {
                                        for (int next = Rand.Next(1, 4), n = 0; n < next; ++n) {
                                            addBarricadeToObject.addPlank(null, null);
                                        }
                                        if (GameServer.bServer) {
                                            addBarricadeToObject.transmitCompleteItemToClients();
                                        }
                                    }
                                }
                            }
                            if (isoObject instanceof IsoWindow) {
                                final IsoGridSquare isoGridSquare2 = (gridSquare.getRoom() == null) ? gridSquare : ((IsoWindow)isoObject).getOppositeSquare();
                                if (((IsoWindow)isoObject).isBarricadeAllowed() && k == 0 && isoGridSquare2 != null && isoGridSquare2.getRoom() == null) {
                                    final IsoBarricade addBarricadeToObject2 = IsoBarricade.AddBarricadeToObject((BarricadeAble)isoObject, isoGridSquare2 != gridSquare);
                                    if (addBarricadeToObject2 != null) {
                                        for (int next2 = Rand.Next(1, 4), n2 = 0; n2 < next2; ++n2) {
                                            addBarricadeToObject2.addPlank(null, null);
                                        }
                                        if (GameServer.bServer) {
                                            addBarricadeToObject2.transmitCompleteItemToClients();
                                        }
                                    }
                                }
                                else {
                                    ((IsoWindow)isoObject).addSheet(null);
                                    ((IsoWindow)isoObject).HasCurtains().ToggleDoor(null);
                                }
                            }
                            if (isoObject.getContainer() != null && gridSquare.getRoom() != null && gridSquare.getRoom().getBuilding().getDef() == buildingDef && Rand.Next(100) <= 70 && gridSquare.getRoom().getName() != null && itemPickerRoom.Containers.containsKey((Object)isoObject.getContainer().getType())) {
                                isoObject.getContainer().clear();
                                ItemPickerJava.fillContainerType(itemPickerRoom, isoObject.getContainer(), "", null);
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
        this.addZombies(buildingDef);
    }
    
    private void addZombies(final BuildingDef buildingDef) {
        this.addZombies(buildingDef, 0, null, null, null);
        if (Rand.Next(5) == 0) {
            this.addZombies(buildingDef, 1, "Survivalist", null, null);
        }
        if (Rand.Next(100) <= 60) {
            RandomizedWorldBase.createRandomDeadBody(this.getLivingRoomOrKitchen(buildingDef), Rand.Next(3, 7));
        }
        if (Rand.Next(100) <= 60) {
            RandomizedWorldBase.createRandomDeadBody(this.getLivingRoomOrKitchen(buildingDef), Rand.Next(3, 7));
        }
    }
    
    public RBSafehouse() {
        this.name = "Safehouse";
        this.setChance(10);
    }
}

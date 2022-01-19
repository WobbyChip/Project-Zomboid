// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedDeadSurvivor;

import zombie.randomizedWorld.RandomizedWorldBase;
import zombie.iso.objects.IsoDoor;
import zombie.iso.IsoGridSquare;
import zombie.iso.objects.IsoDeadBody;
import zombie.network.GameServer;
import zombie.inventory.InventoryItem;
import zombie.iso.objects.interfaces.BarricadeAble;
import zombie.iso.objects.IsoBarricade;
import zombie.characters.IsoGameCharacter;
import zombie.iso.IsoDirections;
import zombie.core.Rand;
import zombie.ZombieSpawnRecorder;
import zombie.VirtualZombieManager;
import zombie.iso.IsoWorld;
import zombie.iso.RoomDef;
import zombie.iso.BuildingDef;

public final class RDSZombieLockedBathroom extends RandomizedDeadSurvivorBase
{
    @Override
    public void randomizeDeadSurvivor(final BuildingDef buildingDef) {
        IsoDeadBody addDeadBodyTheOtherSide = null;
        for (int i = 0; i < buildingDef.rooms.size(); ++i) {
            final RoomDef roomDef = buildingDef.rooms.get(i);
            if ("bathroom".equals(roomDef.name)) {
                if (IsoWorld.getZombiesEnabled()) {
                    final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(roomDef.getX(), roomDef.getY(), roomDef.getZ());
                    if (gridSquare != null && gridSquare.getRoom() != null) {
                        final IsoGridSquare randomFreeSquare = gridSquare.getRoom().getRandomFreeSquare();
                        if (randomFreeSquare != null) {
                            VirtualZombieManager.instance.choices.clear();
                            VirtualZombieManager.instance.choices.add(randomFreeSquare);
                            ZombieSpawnRecorder.instance.record(VirtualZombieManager.instance.createRealZombieAlways(IsoDirections.fromIndex(Rand.Next(8)).index(), false), this.getClass().getSimpleName());
                        }
                    }
                }
                for (int j = roomDef.x - 1; j < roomDef.x2 + 1; ++j) {
                    for (int k = roomDef.y - 1; k < roomDef.y2 + 1; ++k) {
                        final IsoGridSquare gridSquare2 = IsoWorld.instance.getCell().getGridSquare(j, k, roomDef.getZ());
                        if (gridSquare2 != null) {
                            final IsoDoor isoDoor = gridSquare2.getIsoDoor();
                            if (isoDoor != null) {
                                if (this.isDoorToRoom(isoDoor, roomDef)) {
                                    if (isoDoor.IsOpen()) {
                                        isoDoor.ToggleDoor(null);
                                    }
                                    final IsoBarricade addBarricadeToObject = IsoBarricade.AddBarricadeToObject(isoDoor, gridSquare2.getRoom().def == roomDef);
                                    if (addBarricadeToObject != null) {
                                        addBarricadeToObject.addPlank(null, null);
                                        if (GameServer.bServer) {
                                            addBarricadeToObject.transmitCompleteItemToClients();
                                        }
                                    }
                                    addDeadBodyTheOtherSide = this.addDeadBodyTheOtherSide(isoDoor);
                                    break;
                                }
                            }
                        }
                    }
                    if (addDeadBodyTheOtherSide != null) {
                        break;
                    }
                }
                if (addDeadBodyTheOtherSide != null) {
                    addDeadBodyTheOtherSide.setPrimaryHandItem(super.addWeapon("Base.Pistol", true));
                }
                return;
            }
        }
    }
    
    private boolean isDoorToRoom(final IsoDoor isoDoor, final RoomDef roomDef) {
        if (isoDoor == null || roomDef == null) {
            return false;
        }
        final IsoGridSquare square = isoDoor.getSquare();
        final IsoGridSquare oppositeSquare = isoDoor.getOppositeSquare();
        return square != null && oppositeSquare != null && square.getRoomID() == roomDef.ID != (oppositeSquare.getRoomID() == roomDef.ID);
    }
    
    private boolean checkIsBathroom(final IsoGridSquare isoGridSquare) {
        return isoGridSquare.getRoom() != null && "bathroom".equals(isoGridSquare.getRoom().getName());
    }
    
    private IsoDeadBody addDeadBodyTheOtherSide(final IsoDoor isoDoor) {
        IsoGridSquare isoGridSquare;
        if (isoDoor.north) {
            isoGridSquare = IsoWorld.instance.getCell().getGridSquare(isoDoor.getX(), isoDoor.getY(), isoDoor.getZ());
            if (this.checkIsBathroom(isoGridSquare)) {
                isoGridSquare = IsoWorld.instance.getCell().getGridSquare(isoDoor.getX(), isoDoor.getY() - 1.0f, isoDoor.getZ());
            }
        }
        else {
            isoGridSquare = IsoWorld.instance.getCell().getGridSquare(isoDoor.getX(), isoDoor.getY(), isoDoor.getZ());
            if (this.checkIsBathroom(isoGridSquare)) {
                isoGridSquare = IsoWorld.instance.getCell().getGridSquare(isoDoor.getX() - 1.0f, isoDoor.getY(), isoDoor.getZ());
            }
        }
        return RandomizedWorldBase.createRandomDeadBody(isoGridSquare.getX(), isoGridSquare.getY(), isoGridSquare.getZ(), null, Rand.Next(5, 10));
    }
    
    public RDSZombieLockedBathroom() {
        this.name = "Locked in Bathroom";
        this.setChance(5);
    }
}

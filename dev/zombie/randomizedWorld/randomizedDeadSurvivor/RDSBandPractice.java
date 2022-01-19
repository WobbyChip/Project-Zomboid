// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedDeadSurvivor;

import zombie.characters.IsoPlayer;
import zombie.network.GameServer;
import zombie.network.GameClient;
import zombie.iso.IsoGridSquare;
import zombie.iso.RoomDef;
import java.util.List;
import zombie.util.list.PZArrayUtil;
import zombie.randomizedWorld.RandomizedWorldBase;
import zombie.core.Rand;
import zombie.iso.BuildingDef;
import java.util.ArrayList;

public final class RDSBandPractice extends RandomizedDeadSurvivorBase
{
    private final ArrayList<String> instrumentsList;
    
    public RDSBandPractice() {
        this.instrumentsList = new ArrayList<String>();
        this.name = "Band Practice";
        this.setChance(10);
        this.setMaximumDays(60);
        this.instrumentsList.add("GuitarAcoustic");
        this.instrumentsList.add("GuitarElectricBlack");
        this.instrumentsList.add("GuitarElectricBlue");
        this.instrumentsList.add("GuitarElectricRed");
        this.instrumentsList.add("GuitarElectricBassBlue");
        this.instrumentsList.add("GuitarElectricBassBlack");
        this.instrumentsList.add("GuitarElectricBassRed");
    }
    
    @Override
    public void randomizeDeadSurvivor(final BuildingDef buildingDef) {
        this.spawnItemsInContainers(buildingDef, "BandPractice", 90);
        RoomDef roomDef = this.getRoom(buildingDef, "garagestorage");
        if (roomDef == null) {
            roomDef = this.getRoom(buildingDef, "shed");
        }
        if (roomDef == null) {
            roomDef = this.getRoom(buildingDef, "garage");
        }
        this.addZombies(buildingDef, Rand.Next(2, 4), "Rocker", 20, roomDef);
        final IsoGridSquare randomSpawnSquare = RandomizedWorldBase.getRandomSpawnSquare(roomDef);
        if (randomSpawnSquare == null) {
            return;
        }
        randomSpawnSquare.AddWorldInventoryItem(PZArrayUtil.pickRandom(this.instrumentsList), Rand.Next(0.0f, 0.5f), Rand.Next(0.0f, 0.5f), 0.0f);
        if (Rand.Next(4) == 0) {
            randomSpawnSquare.AddWorldInventoryItem(PZArrayUtil.pickRandom(this.instrumentsList), Rand.Next(0.0f, 0.5f), Rand.Next(0.0f, 0.5f), 0.0f);
        }
        if (Rand.Next(4) == 0) {
            randomSpawnSquare.AddWorldInventoryItem(PZArrayUtil.pickRandom(this.instrumentsList), Rand.Next(0.0f, 0.5f), Rand.Next(0.0f, 0.5f), 0.0f);
        }
    }
    
    @Override
    public boolean isValid(final BuildingDef buildingDef, final boolean b) {
        this.debugLine = "";
        if (GameClient.bClient) {
            return false;
        }
        if (buildingDef.isAllExplored() && !b) {
            return false;
        }
        if (!b) {
            for (int i = 0; i < GameServer.Players.size(); ++i) {
                final IsoPlayer isoPlayer = GameServer.Players.get(i);
                if (isoPlayer.getSquare() != null && isoPlayer.getSquare().getBuilding() != null && isoPlayer.getSquare().getBuilding().def == buildingDef) {
                    return false;
                }
            }
        }
        boolean b2 = false;
        for (int j = 0; j < buildingDef.rooms.size(); ++j) {
            final RoomDef roomDef = buildingDef.rooms.get(j);
            if (("garagestorage".equals(roomDef.name) || "shed".equals(roomDef.name) || "garage".equals(roomDef.name)) && roomDef.area >= 9) {
                b2 = true;
                break;
            }
        }
        if (!b2) {
            this.debugLine = "No shed/garage or is too small";
        }
        return b2;
    }
}

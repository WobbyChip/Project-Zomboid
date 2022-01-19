// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedDeadSurvivor;

import zombie.iso.RoomDef;
import zombie.core.Rand;
import zombie.characters.IsoPlayer;
import zombie.network.GameServer;
import zombie.network.GameClient;
import zombie.iso.BuildingDef;
import java.util.ArrayList;

public final class RDSHouseParty extends RandomizedDeadSurvivorBase
{
    final ArrayList<String> items;
    
    public RDSHouseParty() {
        this.items = new ArrayList<String>();
        this.name = "House Party";
        this.setChance(4);
        this.items.add("Base.Crisps");
        this.items.add("Base.Crisps2");
        this.items.add("Base.Crisps3");
        this.items.add("Base.Pop");
        this.items.add("Base.Pop2");
        this.items.add("Base.Pop3");
        this.items.add("Base.Cupcake");
        this.items.add("Base.Cupcake");
        this.items.add("Base.CakeSlice");
        this.items.add("Base.CakeSlice");
        this.items.add("Base.CakeSlice");
        this.items.add("Base.CakeSlice");
        this.items.add("Base.CakeSlice");
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
        if (this.getRoom(buildingDef, "livingroom") != null) {
            return true;
        }
        this.debugLine = "No living room";
        return false;
    }
    
    @Override
    public void randomizeDeadSurvivor(final BuildingDef buildingDef) {
        final RoomDef room = this.getRoom(buildingDef, "livingroom");
        this.addZombies(buildingDef, Rand.Next(5, 8), "Party", null, room);
        this.addRandomItemsOnGround(room, this.items, Rand.Next(4, 7));
    }
}

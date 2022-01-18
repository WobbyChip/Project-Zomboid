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

public final class RDSFootballNight extends RandomizedDeadSurvivorBase
{
    private final ArrayList<String> items;
    private final ArrayList<String> otherItems;
    
    public RDSFootballNight() {
        this.items = new ArrayList<String>();
        this.otherItems = new ArrayList<String>();
        this.name = "Football Night";
        this.setChance(5);
        this.setMaximumDays(60);
        this.otherItems.add("Base.Cigarettes");
        this.otherItems.add("Base.WhiskeyFull");
        this.otherItems.add("Base.Wine");
        this.otherItems.add("Base.Wine2");
        this.items.add("Base.Crisps");
        this.items.add("Base.Crisps2");
        this.items.add("Base.Crisps3");
        this.items.add("Base.Pop");
        this.items.add("Base.Pop2");
        this.items.add("Base.Pop3");
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
        this.addZombies(buildingDef, Rand.Next(3, 6), "SportsFan", 10, room);
        this.addRandomItemsOnGround(room, this.items, Rand.Next(3, 7));
        this.addRandomItemsOnGround(room, this.otherItems, Rand.Next(2, 6));
    }
}

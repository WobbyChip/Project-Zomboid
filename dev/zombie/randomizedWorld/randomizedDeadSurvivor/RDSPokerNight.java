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

public final class RDSPokerNight extends RandomizedDeadSurvivorBase
{
    private final ArrayList<String> items;
    private String money;
    private String card;
    
    public RDSPokerNight() {
        this.items = new ArrayList<String>();
        this.money = null;
        this.card = null;
        this.name = "Poker Night";
        this.setChance(4);
        this.setMaximumDays(60);
        this.items.add("Base.Cigarettes");
        this.items.add("Base.WhiskeyFull");
        this.items.add("Base.Wine");
        this.items.add("Base.Wine2");
        this.items.add("Base.Crisps");
        this.items.add("Base.Crisps2");
        this.items.add("Base.Crisps3");
        this.items.add("Base.Pop");
        this.items.add("Base.Pop2");
        this.items.add("Base.Pop3");
        this.money = "Base.Money";
        this.card = "Base.CardDeck";
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
        if (this.getRoom(buildingDef, "kitchen") != null) {
            return true;
        }
        this.debugLine = "No kitchen";
        return false;
    }
    
    @Override
    public void randomizeDeadSurvivor(final BuildingDef buildingDef) {
        final RoomDef room = this.getRoom(buildingDef, "kitchen");
        this.addZombies(buildingDef, Rand.Next(3, 5), null, 10, room);
        this.addZombies(buildingDef, 1, "PokerDealer", 0, room);
        this.addRandomItemsOnGround(room, this.items, Rand.Next(3, 7));
        this.addRandomItemsOnGround(room, this.money, Rand.Next(8, 13));
        this.addRandomItemsOnGround(room, this.card, 1);
    }
}

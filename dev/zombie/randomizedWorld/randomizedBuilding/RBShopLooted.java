// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedBuilding;

import zombie.characters.IsoPlayer;
import zombie.network.GameServer;
import zombie.network.GameClient;
import zombie.iso.IsoDirections;
import zombie.randomizedWorld.RandomizedWorldBase;
import zombie.core.Rand;
import zombie.iso.RoomDef;
import zombie.iso.BuildingDef;
import java.util.ArrayList;

public final class RBShopLooted extends RandomizedBuildingBase
{
    private final ArrayList<String> buildingList;
    
    @Override
    public void randomizeBuilding(final BuildingDef buildingDef) {
        buildingDef.bAlarmed = false;
        buildingDef.setAllExplored(true);
        RoomDef roomDef = null;
        for (int i = 0; i < buildingDef.rooms.size(); ++i) {
            final RoomDef roomDef2 = buildingDef.rooms.get(i);
            if (this.buildingList.contains(roomDef2.name)) {
                roomDef = roomDef2;
                break;
            }
        }
        if (roomDef == null) {
            return;
        }
        for (int next = Rand.Next(3, 8), j = 0; j < next; ++j) {
            this.addZombiesOnSquare(1, "Bandit", null, roomDef.getFreeSquare());
        }
        this.addZombiesOnSquare(2, "Police", null, roomDef.getFreeSquare());
        for (int next2 = Rand.Next(3, 8), k = 0; k < next2; ++k) {
            RandomizedWorldBase.createRandomDeadBody(RandomizedWorldBase.getRandomSquareForCorpse(roomDef), null, Rand.Next(5, 10), 5, null);
        }
    }
    
    @Override
    public boolean isValid(final BuildingDef buildingDef, final boolean b) {
        this.debugLine = "";
        if (GameClient.bClient) {
            return false;
        }
        if (!this.isTimeValid(b)) {
            return false;
        }
        if (buildingDef.isAllExplored() && !b) {
            return false;
        }
        if (!b) {
            if (Rand.Next(100) > this.getChance()) {
                return false;
            }
            for (int i = 0; i < GameServer.Players.size(); ++i) {
                final IsoPlayer isoPlayer = GameServer.Players.get(i);
                if (isoPlayer.getSquare() != null && isoPlayer.getSquare().getBuilding() != null && isoPlayer.getSquare().getBuilding().def == buildingDef) {
                    return false;
                }
            }
        }
        for (int j = 0; j < buildingDef.rooms.size(); ++j) {
            if (this.buildingList.contains(buildingDef.rooms.get(j).name)) {
                return true;
            }
        }
        this.debugLine = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.debugLine);
        return false;
    }
    
    public RBShopLooted() {
        this.buildingList = new ArrayList<String>();
        this.name = "Looted Shop";
        this.setChance(2);
        this.setAlwaysDo(true);
        this.setMaximumDays(30);
        this.buildingList.add("conveniencestore");
        this.buildingList.add("warehouse");
        this.buildingList.add("medclinic");
        this.buildingList.add("grocery");
        this.buildingList.add("zippeestore");
        this.buildingList.add("gigamart");
        this.buildingList.add("fossoil");
        this.buildingList.add("spiffo_dining");
        this.buildingList.add("pizzawhirled");
        this.buildingList.add("bookstore");
        this.buildingList.add("grocers");
        this.buildingList.add("library");
        this.buildingList.add("toolstore");
        this.buildingList.add("bar");
        this.buildingList.add("pharmacy");
        this.buildingList.add("gunstore");
        this.buildingList.add("mechanic");
        this.buildingList.add("bakery");
        this.buildingList.add("aesthetic");
        this.buildingList.add("clothesstore");
        this.buildingList.add("restaurant");
        this.buildingList.add("poststorage");
        this.buildingList.add("generalstore");
        this.buildingList.add("furniturestore");
        this.buildingList.add("fishingstorage");
        this.buildingList.add("cornerstore");
        this.buildingList.add("housewarestore");
        this.buildingList.add("shoestore");
        this.buildingList.add("sportstore");
        this.buildingList.add("giftstore");
        this.buildingList.add("candystore");
        this.buildingList.add("toystore");
        this.buildingList.add("electronicsstore");
        this.buildingList.add("sewingstore");
        this.buildingList.add("medical");
        this.buildingList.add("medicaloffice");
        this.buildingList.add("jewelrystore");
        this.buildingList.add("musicstore");
        this.buildingList.add("departmentstore");
        this.buildingList.add("gasstore");
        this.buildingList.add("gardenstore");
        this.buildingList.add("farmstorage");
        this.buildingList.add("hunting");
        this.buildingList.add("camping");
        this.buildingList.add("butcher");
        this.buildingList.add("optometrist");
        this.buildingList.add("knoxbutcher");
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedDeadSurvivor;

import zombie.iso.IsoGridSquare;
import zombie.randomizedWorld.RandomizedWorldBase;
import zombie.iso.RoomDef;
import zombie.core.Rand;
import zombie.iso.BuildingDef;
import java.util.ArrayList;

public final class RDSStudentNight extends RandomizedDeadSurvivorBase
{
    private final ArrayList<String> items;
    private final ArrayList<String> otherItems;
    private final ArrayList<String> pantsMaleItems;
    private final ArrayList<String> pantsFemaleItems;
    private final ArrayList<String> topItems;
    private final ArrayList<String> shoesItems;
    
    public RDSStudentNight() {
        this.items = new ArrayList<String>();
        this.otherItems = new ArrayList<String>();
        this.pantsMaleItems = new ArrayList<String>();
        this.pantsFemaleItems = new ArrayList<String>();
        this.topItems = new ArrayList<String>();
        this.shoesItems = new ArrayList<String>();
        this.name = "Student Night";
        this.setChance(4);
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
        this.shoesItems.add("Base.Shoes_Random");
        this.shoesItems.add("Base.Shoes_TrainerTINT");
        this.pantsMaleItems.add("Base.TrousersMesh_DenimLight");
        this.pantsMaleItems.add("Base.Trousers_DefaultTEXTURE_TINT");
        this.pantsMaleItems.add("Base.Trousers_Denim");
        this.pantsFemaleItems.add("Base.Skirt_Knees");
        this.pantsFemaleItems.add("Base.Skirt_Long");
        this.pantsFemaleItems.add("Base.Skirt_Short");
        this.pantsFemaleItems.add("Base.Skirt_Normal");
        this.topItems.add("Base.Shirt_FormalWhite");
        this.topItems.add("Base.Shirt_FormalWhite_ShortSleeve");
        this.topItems.add("Base.Tshirt_DefaultTEXTURE_TINT");
        this.topItems.add("Base.Tshirt_PoloTINT");
        this.topItems.add("Base.Tshirt_WhiteLongSleeveTINT");
        this.topItems.add("Base.Tshirt_WhiteTINT");
    }
    
    @Override
    public void randomizeDeadSurvivor(final BuildingDef buildingDef) {
        final RoomDef livingRoomOrKitchen = this.getLivingRoomOrKitchen(buildingDef);
        this.addZombies(buildingDef, Rand.Next(2, 5), null, null, livingRoomOrKitchen);
        final RoomDef room = this.getRoom(buildingDef, "bedroom");
        this.addZombies(buildingDef, 1, "Naked", 0, room);
        this.addItemsOnGround(room, true);
        this.addZombies(buildingDef, 1, "Naked", 100, room);
        this.addItemsOnGround(room, false);
        this.addRandomItemsOnGround(livingRoomOrKitchen, this.items, Rand.Next(3, 7));
        this.addRandomItemsOnGround(livingRoomOrKitchen, this.otherItems, Rand.Next(2, 6));
    }
    
    private void addItemsOnGround(final RoomDef roomDef, final boolean b) {
        final IsoGridSquare randomSpawnSquare = RandomizedWorldBase.getRandomSpawnSquare(roomDef);
        this.addRandomItemOnGround(randomSpawnSquare, this.shoesItems);
        this.addRandomItemOnGround(randomSpawnSquare, this.topItems);
        this.addRandomItemOnGround(randomSpawnSquare, b ? this.pantsMaleItems : this.pantsFemaleItems);
    }
}

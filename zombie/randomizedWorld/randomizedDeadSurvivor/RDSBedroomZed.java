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

public final class RDSBedroomZed extends RandomizedDeadSurvivorBase
{
    private final ArrayList<String> pantsMaleItems;
    private final ArrayList<String> pantsFemaleItems;
    private final ArrayList<String> topItems;
    private final ArrayList<String> shoesItems;
    
    public RDSBedroomZed() {
        this.pantsMaleItems = new ArrayList<String>();
        this.pantsFemaleItems = new ArrayList<String>();
        this.topItems = new ArrayList<String>();
        this.shoesItems = new ArrayList<String>();
        this.name = "Bedroom Zed";
        this.setChance(7);
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
        final RoomDef room = this.getRoom(buildingDef, "bedroom");
        final boolean b = Rand.Next(7) == 0;
        final boolean b2 = Rand.Next(7) == 0;
        if (b) {
            this.addZombies(buildingDef, 2, "Naked", 0, room);
            this.addItemsOnGround(room, true);
            this.addItemsOnGround(room, true);
        }
        else if (b2) {
            this.addZombies(buildingDef, 2, "Naked", 100, room);
            this.addItemsOnGround(room, false);
            this.addItemsOnGround(room, false);
        }
        else {
            this.addZombies(buildingDef, 1, "Naked", 0, room);
            this.addItemsOnGround(room, true);
            this.addZombies(buildingDef, 1, "Naked", 100, room);
            this.addItemsOnGround(room, false);
        }
    }
    
    private void addItemsOnGround(final RoomDef roomDef, final boolean b) {
        final IsoGridSquare randomSpawnSquare = RandomizedWorldBase.getRandomSpawnSquare(roomDef);
        this.addRandomItemOnGround(randomSpawnSquare, this.shoesItems);
        this.addRandomItemOnGround(randomSpawnSquare, this.topItems);
        this.addRandomItemOnGround(randomSpawnSquare, b ? this.pantsMaleItems : this.pantsFemaleItems);
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedDeadSurvivor;

import zombie.iso.RoomDef;
import zombie.core.Rand;
import zombie.iso.BuildingDef;
import java.util.ArrayList;

public final class RDSBathroomZed extends RandomizedDeadSurvivorBase
{
    private final ArrayList<String> items;
    
    public RDSBathroomZed() {
        this.items = new ArrayList<String>();
        this.name = "Bathroom Zed";
        this.setChance(12);
        this.items.add("Base.BathTowel");
        this.items.add("Base.Razor");
        this.items.add("Base.Lipstick");
        this.items.add("Base.Comb");
        this.items.add("Base.Hairspray");
        this.items.add("Base.Toothbrush");
        this.items.add("Base.Cologne");
        this.items.add("Base.Perfume");
    }
    
    @Override
    public void randomizeDeadSurvivor(final BuildingDef buildingDef) {
        final RoomDef room = this.getRoom(buildingDef, "bathroom");
        int next = 1;
        if (room.area > 6) {
            next = Rand.Next(1, 3);
        }
        this.addZombies(buildingDef, next, (Rand.Next(2) == 0) ? "Bathrobe" : "Naked", null, room);
        this.addRandomItemsOnGround(room, this.items, Rand.Next(2, 5));
    }
}

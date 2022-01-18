// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedZoneStory;

import zombie.characters.IsoZombie;
import zombie.vehicles.BaseVehicle;
import zombie.core.Rand;
import zombie.iso.IsoDirections;
import zombie.iso.IsoChunk;
import zombie.iso.IsoMetaGrid;
import java.util.ArrayList;

public class RZSSexyTime extends RandomizedZoneStoryBase
{
    private final ArrayList<String> pantsMaleItems;
    private final ArrayList<String> pantsFemaleItems;
    private final ArrayList<String> topItems;
    private final ArrayList<String> shoesItems;
    
    public RZSSexyTime() {
        this.pantsMaleItems = new ArrayList<String>();
        this.pantsFemaleItems = new ArrayList<String>();
        this.topItems = new ArrayList<String>();
        this.shoesItems = new ArrayList<String>();
        this.name = "Sexy Time";
        this.chance = 5;
        this.minZoneHeight = 5;
        this.minZoneWidth = 5;
        this.zoneType.add(ZoneType.Beach.toString());
        this.zoneType.add(ZoneType.Forest.toString());
        this.zoneType.add(ZoneType.Lake.toString());
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
    public void randomizeZoneStory(final IsoMetaGrid.Zone zone) {
        this.cleanAreaForStory(this, zone);
        final BaseVehicle addVehicle = this.addVehicle(zone, this.getSq(zone.pickedXForZoneStory, zone.pickedYForZoneStory, zone.z), null, null, "Base.VanAmbulance", null, null, null);
        final boolean b = Rand.Next(7) == 0;
        final boolean b2 = Rand.Next(7) == 0;
        if (b) {
            this.addItemsOnGround(zone, true, addVehicle);
            this.addItemsOnGround(zone, true, addVehicle);
        }
        else if (b2) {
            this.addItemsOnGround(zone, false, addVehicle);
            this.addItemsOnGround(zone, false, addVehicle);
        }
        else {
            this.addItemsOnGround(zone, true, addVehicle);
            this.addItemsOnGround(zone, false, addVehicle);
        }
    }
    
    private void addItemsOnGround(final IsoMetaGrid.Zone zone, final boolean b, final BaseVehicle baseVehicle) {
        int i = 100;
        if (!b) {
            i = 0;
        }
        final ArrayList<IsoZombie> addZombiesOnVehicle = this.addZombiesOnVehicle(1, "Naked", i, baseVehicle);
        if (addZombiesOnVehicle.isEmpty()) {
            return;
        }
        final IsoZombie isoZombie = addZombiesOnVehicle.get(0);
        this.addRandomItemOnGround(isoZombie.getSquare(), this.shoesItems);
        this.addRandomItemOnGround(isoZombie.getSquare(), this.topItems);
        this.addRandomItemOnGround(isoZombie.getSquare(), b ? this.pantsMaleItems : this.pantsFemaleItems);
    }
}

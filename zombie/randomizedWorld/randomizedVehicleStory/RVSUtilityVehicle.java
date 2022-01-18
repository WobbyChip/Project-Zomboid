// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedVehicleStory;

import zombie.iso.IsoGridSquare;
import zombie.core.math.PZMath;
import java.util.List;
import zombie.util.list.PZArrayUtil;
import zombie.vehicles.BaseVehicle;
import zombie.iso.Vector2;
import zombie.iso.IsoDirections;
import zombie.core.Rand;
import zombie.iso.IsoChunk;
import zombie.iso.IsoMetaGrid;
import java.util.ArrayList;

public final class RVSUtilityVehicle extends RandomizedVehicleStoryBase
{
    private ArrayList<String> tools;
    private ArrayList<String> carpenterTools;
    private Params params;
    
    public RVSUtilityVehicle() {
        this.tools = null;
        this.carpenterTools = null;
        this.params = new Params();
        this.name = "Utility Vehicle";
        this.minZoneWidth = 8;
        this.minZoneHeight = 9;
        this.setChance(7);
        (this.tools = new ArrayList<String>()).add("Base.PickAxe");
        this.tools.add("Base.Shovel");
        this.tools.add("Base.Shovel2");
        this.tools.add("Base.Hammer");
        this.tools.add("Base.LeadPipe");
        this.tools.add("Base.PipeWrench");
        this.tools.add("Base.Sledgehammer");
        this.tools.add("Base.Sledgehammer2");
        (this.carpenterTools = new ArrayList<String>()).add("Base.Hammer");
        this.carpenterTools.add("Base.NailsBox");
        this.carpenterTools.add("Base.Plank");
        this.carpenterTools.add("Base.Plank");
        this.carpenterTools.add("Base.Plank");
        this.carpenterTools.add("Base.Screwdriver");
        this.carpenterTools.add("Base.Saw");
        this.carpenterTools.add("Base.Saw");
        this.carpenterTools.add("Base.Woodglue");
    }
    
    @Override
    public void randomizeVehicleStory(final IsoMetaGrid.Zone zone, final IsoChunk isoChunk) {
        this.callVehicleStorySpawner(zone, isoChunk, 0.0f);
    }
    
    public void doUtilityVehicle(final IsoMetaGrid.Zone zone, final IsoChunk isoChunk, final String zoneName, final String scriptName, final String outfits, final Integer femaleChance, final String vehicleDistrib, final ArrayList<String> items, final int nbrOfItem, final boolean addTrailer) {
        this.params.zoneName = zoneName;
        this.params.scriptName = scriptName;
        this.params.outfits = outfits;
        this.params.femaleChance = femaleChance;
        this.params.vehicleDistrib = vehicleDistrib;
        this.params.items = items;
        this.params.nbrOfItem = nbrOfItem;
        this.params.addTrailer = addTrailer;
    }
    
    @Override
    public boolean initVehicleStorySpawner(final IsoMetaGrid.Zone zone, final IsoChunk isoChunk, final boolean b) {
        switch (Rand.Next(0, 7)) {
            case 0: {
                this.doUtilityVehicle(zone, isoChunk, null, "Base.PickUpTruck", "ConstructionWorker", 0, "ConstructionWorker", this.tools, Rand.Next(0, 3), true);
                break;
            }
            case 1: {
                this.doUtilityVehicle(zone, isoChunk, "police", null, "Police", null, null, null, 0, false);
                break;
            }
            case 2: {
                this.doUtilityVehicle(zone, isoChunk, "fire", null, "Fireman", null, null, null, 0, false);
                break;
            }
            case 3: {
                this.doUtilityVehicle(zone, isoChunk, "ranger", null, "Ranger", null, null, null, 0, true);
                break;
            }
            case 4: {
                this.doUtilityVehicle(zone, isoChunk, "mccoy", null, "McCoys", 0, "Carpenter", this.carpenterTools, Rand.Next(2, 6), true);
                break;
            }
            case 5: {
                this.doUtilityVehicle(zone, isoChunk, "postal", null, "Postal", null, null, null, 0, false);
                break;
            }
            case 6: {
                this.doUtilityVehicle(zone, isoChunk, "fossoil", null, "Fossoil", null, null, null, 0, false);
                break;
            }
        }
        final VehicleStorySpawner instance = VehicleStorySpawner.getInstance();
        instance.clear();
        final Vector2 toVector = IsoDirections.N.ToVector();
        float n = 0.5235988f;
        if (b) {
            n = 0.0f;
        }
        toVector.rotate(Rand.Next(-n, n));
        final float n2 = -2.0f;
        final int n3 = 5;
        instance.addElement("vehicle1", 0.0f, n2, toVector.getDirection(), 2.0f, (float)n3);
        if (this.params.addTrailer && Rand.NextBool(7)) {
            final int n4 = 3;
            instance.addElement("trailer", 0.0f, n2 + n3 / 2.0f + 1.0f + n4 / 2.0f, toVector.getDirection(), 2.0f, (float)n4);
        }
        if (this.params.items != null) {
            for (int i = 0; i < this.params.nbrOfItem; ++i) {
                instance.addElement("tool", Rand.Next(-3.5f, 3.5f), Rand.Next(-3.5f, 3.5f), 0.0f, 1.0f, 1.0f);
            }
        }
        instance.setParameter("zone", zone);
        return true;
    }
    
    @Override
    public void spawnElement(final VehicleStorySpawner vehicleStorySpawner, final VehicleStorySpawner.Element element) {
        final IsoGridSquare square = element.square;
        if (square == null) {
            return;
        }
        final float z = element.z;
        final IsoMetaGrid.Zone zone = vehicleStorySpawner.getParameter("zone", IsoMetaGrid.Zone.class);
        final BaseVehicle baseVehicle = vehicleStorySpawner.getParameter("vehicle1", BaseVehicle.class);
        final String id = element.id;
        switch (id) {
            case "tool": {
                if (baseVehicle == null) {
                    break;
                }
                square.AddWorldInventoryItem(PZArrayUtil.pickRandom(this.params.items), PZMath.max(element.position.x - square.x, 0.001f), PZMath.max(element.position.y - square.y, 0.001f), 0.0f);
                break;
            }
            case "trailer": {
                if (baseVehicle == null) {
                    break;
                }
                this.addTrailer(baseVehicle, zone, square.getChunk(), this.params.zoneName, this.params.vehicleDistrib, Rand.NextBool(1) ? "Base.Trailer" : "Base.TrailerCover");
                break;
            }
            case "vehicle1": {
                final BaseVehicle addVehicle = this.addVehicle(zone, element.position.x, element.position.y, z, element.direction, this.params.zoneName, this.params.scriptName, null, this.params.vehicleDistrib);
                if (addVehicle == null) {
                    break;
                }
                this.addZombiesOnVehicle(Rand.Next(2, 5), this.params.outfits, this.params.femaleChance, addVehicle);
                break;
            }
        }
    }
    
    private static final class Params
    {
        String zoneName;
        String scriptName;
        String outfits;
        Integer femaleChance;
        String vehicleDistrib;
        ArrayList<String> items;
        int nbrOfItem;
        boolean addTrailer;
    }
}

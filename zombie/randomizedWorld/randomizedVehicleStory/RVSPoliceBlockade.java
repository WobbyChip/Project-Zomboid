// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedVehicleStory;

import zombie.vehicles.BaseVehicle;
import zombie.iso.Vector2;
import zombie.iso.IsoDirections;
import zombie.core.Rand;
import zombie.iso.IsoChunk;
import zombie.iso.IsoMetaGrid;

public final class RVSPoliceBlockade extends RandomizedVehicleStoryBase
{
    public RVSPoliceBlockade() {
        this.name = "Police Blockade";
        this.minZoneWidth = 8;
        this.minZoneHeight = 8;
        this.setChance(3);
        this.setMaximumDays(30);
    }
    
    @Override
    public void randomizeVehicleStory(final IsoMetaGrid.Zone zone, final IsoChunk isoChunk) {
        this.callVehicleStorySpawner(zone, isoChunk, 0.0f);
    }
    
    @Override
    public boolean initVehicleStorySpawner(final IsoMetaGrid.Zone zone, final IsoChunk isoChunk, final boolean b) {
        final VehicleStorySpawner instance = VehicleStorySpawner.getInstance();
        instance.clear();
        float n = 0.17453292f;
        if (b) {
            n = 0.0f;
        }
        float n2 = 1.5f;
        float n3 = 1.0f;
        if (this.zoneWidth >= 10) {
            n2 = 2.5f;
            n3 = 0.0f;
        }
        final IsoDirections isoDirections = Rand.NextBool(2) ? IsoDirections.W : IsoDirections.E;
        final Vector2 toVector = isoDirections.ToVector();
        toVector.rotate(Rand.Next(-n, n));
        instance.addElement("vehicle1", -n2, n3, toVector.getDirection(), 2.0f, 5.0f);
        final Vector2 toVector2 = isoDirections.RotLeft(4).ToVector();
        toVector2.rotate(Rand.Next(-n, n));
        instance.addElement("vehicle2", n2, -n3, toVector2.getDirection(), 2.0f, 5.0f);
        String s = "Base.CarLightsPolice";
        if (Rand.NextBool(3)) {
            s = "Base.PickUpVanLightsPolice";
        }
        instance.setParameter("zone", zone);
        instance.setParameter("script", s);
        return true;
    }
    
    @Override
    public void spawnElement(final VehicleStorySpawner vehicleStorySpawner, final VehicleStorySpawner.Element element) {
        if (element.square == null) {
            return;
        }
        final float z = element.z;
        final IsoMetaGrid.Zone zone = vehicleStorySpawner.getParameter("zone", IsoMetaGrid.Zone.class);
        final String parameterString = vehicleStorySpawner.getParameterString("script");
        final String id = element.id;
        switch (id) {
            case "vehicle1":
            case "vehicle2": {
                final BaseVehicle addVehicle = this.addVehicle(zone, element.position.x, element.position.y, z, element.direction, null, parameterString, null, null);
                if (addVehicle == null) {
                    break;
                }
                if (Rand.NextBool(3)) {
                    addVehicle.setHeadlightsOn(true);
                    addVehicle.setLightbarLightsMode(2);
                }
                this.addZombiesOnVehicle(Rand.Next(2, 4), "police", null, addVehicle);
                break;
            }
        }
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedVehicleStory;

import zombie.vehicles.BaseVehicle;
import zombie.iso.Vector2;
import zombie.core.Rand;
import zombie.iso.IsoDirections;
import zombie.iso.IsoChunk;
import zombie.iso.IsoMetaGrid;

public final class RVSBurntCar extends RandomizedVehicleStoryBase
{
    public RVSBurntCar() {
        this.name = "Burnt Car";
        this.minZoneWidth = 2;
        this.minZoneHeight = 5;
        this.setChance(13);
    }
    
    @Override
    public void randomizeVehicleStory(final IsoMetaGrid.Zone zone, final IsoChunk isoChunk) {
        this.callVehicleStorySpawner(zone, isoChunk, 0.0f);
    }
    
    @Override
    public boolean initVehicleStorySpawner(final IsoMetaGrid.Zone zone, final IsoChunk isoChunk, final boolean b) {
        final VehicleStorySpawner instance = VehicleStorySpawner.getInstance();
        instance.clear();
        final Vector2 toVector = IsoDirections.N.ToVector();
        float n = 0.5235988f;
        if (b) {
            n = 0.0f;
        }
        toVector.rotate(Rand.Next(-n, n));
        instance.addElement("vehicle1", 0.0f, 0.0f, toVector.getDirection(), 2.0f, 5.0f);
        instance.setParameter("zone", zone);
        return true;
    }
    
    @Override
    public void spawnElement(final VehicleStorySpawner vehicleStorySpawner, final VehicleStorySpawner.Element element) {
        if (element.square == null) {
            return;
        }
        final float z = element.z;
        final IsoMetaGrid.Zone zone = vehicleStorySpawner.getParameter("zone", IsoMetaGrid.Zone.class);
        final String id = element.id;
        switch (id) {
            case "vehicle1": {
                final BaseVehicle addVehicle = this.addVehicle(zone, element.position.x, element.position.y, z, element.direction, null, "Base.CarNormal", null, null);
                if (addVehicle == null) {
                    break;
                }
                addVehicle.setSmashed("right");
                break;
            }
        }
    }
}

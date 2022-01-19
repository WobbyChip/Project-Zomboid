// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedVehicleStory;

import zombie.randomizedWorld.RandomizedWorldBase;
import zombie.vehicles.BaseVehicle;
import zombie.iso.Vector2;
import zombie.iso.IsoDirections;
import zombie.core.Rand;
import zombie.iso.IsoChunk;
import zombie.iso.IsoMetaGrid;

public final class RVSCarCrashCorpse extends RandomizedVehicleStoryBase
{
    public RVSCarCrashCorpse() {
        this.name = "Basic Car Crash Corpse";
        this.minZoneWidth = 6;
        this.minZoneHeight = 11;
        this.setChance(10);
    }
    
    @Override
    public void randomizeVehicleStory(final IsoMetaGrid.Zone zone, final IsoChunk isoChunk) {
        final float n = 0.5235988f;
        this.callVehicleStorySpawner(zone, isoChunk, Rand.Next(-n, n));
    }
    
    @Override
    public boolean initVehicleStorySpawner(final IsoMetaGrid.Zone zone, final IsoChunk isoChunk, final boolean b) {
        final VehicleStorySpawner instance = VehicleStorySpawner.getInstance();
        instance.clear();
        final Vector2 toVector = IsoDirections.N.ToVector();
        final float n = 2.5f;
        instance.addElement("vehicle1", 0.0f, n, toVector.getDirection(), 2.0f, 5.0f);
        instance.addElement("corpse", 0.0f, n - (b ? 7 : Rand.Next(4, 7)), toVector.getDirection() + 3.1415927f, 1.0f, 2.0f);
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
        final BaseVehicle baseVehicle = vehicleStorySpawner.getParameter("vehicle1", BaseVehicle.class);
        final String id = element.id;
        switch (id) {
            case "corpse": {
                if (baseVehicle == null) {
                    break;
                }
                RandomizedWorldBase.createRandomDeadBody(element.position.x, element.position.y, element.z, element.direction, false, 35, 30, null);
                this.addTrailOfBlood(element.position.x, element.position.y, element.z, element.direction, 15);
                break;
            }
            case "vehicle1": {
                final BaseVehicle addVehicle = this.addVehicle(zone, element.position.x, element.position.y, z, element.direction, "bad", null, null, null);
                if (addVehicle == null) {
                    break;
                }
                final BaseVehicle setSmashed = addVehicle.setSmashed("Front");
                setSmashed.setBloodIntensity("Front", 1.0f);
                vehicleStorySpawner.setParameter("vehicle1", setSmashed);
                break;
            }
        }
    }
}

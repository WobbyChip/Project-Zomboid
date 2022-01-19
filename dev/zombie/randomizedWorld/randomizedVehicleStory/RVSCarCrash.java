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

public final class RVSCarCrash extends RandomizedVehicleStoryBase
{
    public RVSCarCrash() {
        this.name = "Basic Car Crash";
        this.minZoneWidth = 5;
        this.minZoneHeight = 7;
        this.setChance(25);
    }
    
    @Override
    public void randomizeVehicleStory(final IsoMetaGrid.Zone zone, final IsoChunk isoChunk) {
        this.callVehicleStorySpawner(zone, isoChunk, 0.0f);
    }
    
    @Override
    public boolean initVehicleStorySpawner(final IsoMetaGrid.Zone zone, final IsoChunk isoChunk, final boolean b) {
        final VehicleStorySpawner instance = VehicleStorySpawner.getInstance();
        instance.clear();
        float n = 0.5235988f;
        if (b) {
            n = 0.0f;
        }
        final Vector2 toVector = IsoDirections.N.ToVector();
        toVector.rotate(Rand.Next(-n, n));
        instance.addElement("vehicle1", 0.0f, 1.0f, toVector.getDirection(), 2.0f, 5.0f);
        final boolean nextBool = Rand.NextBool(2);
        final Vector2 vector2 = nextBool ? IsoDirections.E.ToVector() : IsoDirections.W.ToVector();
        vector2.rotate(Rand.Next(-n, n));
        instance.addElement("vehicle2", 0.0f, -2.5f, vector2.getDirection(), 2.0f, 5.0f);
        instance.setParameter("zone", zone);
        instance.setParameter("smashed", Rand.NextBool(3));
        instance.setParameter("east", nextBool);
        return true;
    }
    
    @Override
    public void spawnElement(final VehicleStorySpawner vehicleStorySpawner, final VehicleStorySpawner.Element element) {
        if (element.square == null) {
            return;
        }
        final float z = element.z;
        final IsoMetaGrid.Zone zone = vehicleStorySpawner.getParameter("zone", IsoMetaGrid.Zone.class);
        final boolean parameterBoolean = vehicleStorySpawner.getParameterBoolean("smashed");
        final boolean parameterBoolean2 = vehicleStorySpawner.getParameterBoolean("east");
        final String id = element.id;
        switch (id) {
            case "vehicle1":
            case "vehicle2": {
                BaseVehicle baseVehicle = this.addVehicle(zone, element.position.x, element.position.y, z, element.direction, "bad", null, null, null);
                if (baseVehicle == null) {
                    break;
                }
                if (parameterBoolean) {
                    String smashed = "Front";
                    if ("vehicle2".equals(element.id)) {
                        smashed = (parameterBoolean2 ? "Right" : "Left");
                    }
                    baseVehicle = baseVehicle.setSmashed(smashed);
                    baseVehicle.setBloodIntensity(smashed, 1.0f);
                }
                if ("vehicle1".equals(element.id) && Rand.Next(10) < 4) {
                    this.addZombiesOnVehicle(Rand.Next(2, 5), null, null, baseVehicle);
                    break;
                }
                break;
            }
        }
    }
}

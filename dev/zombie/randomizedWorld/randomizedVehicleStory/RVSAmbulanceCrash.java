// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedVehicleStory;

import java.util.ArrayList;
import zombie.vehicles.BaseVehicle;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.characters.IsoZombie;
import zombie.iso.Vector2;
import zombie.core.Rand;
import zombie.iso.IsoDirections;
import zombie.iso.IsoChunk;
import zombie.iso.IsoMetaGrid;

public final class RVSAmbulanceCrash extends RandomizedVehicleStoryBase
{
    public RVSAmbulanceCrash() {
        this.name = "Ambulance Crash";
        this.minZoneWidth = 5;
        this.minZoneHeight = 7;
        this.setChance(5);
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
        final Vector2 vector2 = Rand.NextBool(2) ? IsoDirections.E.ToVector() : IsoDirections.W.ToVector();
        vector2.rotate(Rand.Next(-n, n));
        instance.addElement("vehicle2", 0.0f, -2.5f, vector2.getDirection(), 2.0f, 5.0f);
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
                final BaseVehicle addVehicle = this.addVehicle(zone, element.position.x, element.position.y, z, element.direction, null, "Base.VanAmbulance", null, null);
                if (addVehicle == null) {
                    break;
                }
                this.addZombiesOnVehicle(Rand.Next(1, 3), "AmbulanceDriver", null, addVehicle);
                final ArrayList<IsoZombie> addZombiesOnVehicle = this.addZombiesOnVehicle(Rand.Next(1, 3), "HospitalPatient", null, addVehicle);
                for (int i = 0; i < addZombiesOnVehicle.size(); ++i) {
                    for (int j = 0; j < 7; ++j) {
                        if (Rand.NextBool(2)) {
                            addZombiesOnVehicle.get(i).addVisualBandage(BodyPartType.getRandom(), true);
                        }
                    }
                }
                break;
            }
            case "vehicle2": {
                if (this.addVehicle(zone, element.position.x, element.position.y, z, element.direction, "bad", null, null, null) == null) {}
                break;
            }
        }
    }
}

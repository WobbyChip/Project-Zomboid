// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedVehicleStory;

import zombie.randomizedWorld.RandomizedWorldBase;
import zombie.vehicles.BaseVehicle;
import zombie.iso.Vector2;
import zombie.core.Rand;
import zombie.iso.IsoDirections;
import zombie.iso.IsoChunk;
import zombie.iso.IsoMetaGrid;

public final class RVSBanditRoad extends RandomizedVehicleStoryBase
{
    public RVSBanditRoad() {
        this.name = "Bandits on Road";
        this.minZoneWidth = 7;
        this.minZoneHeight = 9;
        this.setMinimumDays(30);
        this.setChance(3);
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
        instance.addElement("vehicle1", 0.0f, 2.0f, toVector.getDirection(), 2.0f, 5.0f);
        final Vector2 vector2 = Rand.NextBool(2) ? IsoDirections.E.ToVector() : IsoDirections.W.ToVector();
        vector2.rotate(Rand.Next(-n, n));
        final float n2 = 0.0f;
        final float n3 = -1.5f;
        instance.addElement("vehicle2", n2, n3, vector2.getDirection(), 2.0f, 5.0f);
        for (int next = Rand.Next(3, 6), i = 0; i < next; ++i) {
            instance.addElement("corpse", Rand.Next(n2 - 3.0f, n2 + 3.0f), Rand.Next(n3 - 3.0f, n3 + 3.0f), Rand.Next(0.0f, 6.2831855f), 1.0f, 2.0f);
        }
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
            case "corpse": {
                final BaseVehicle baseVehicle = vehicleStorySpawner.getParameter("vehicle1", BaseVehicle.class);
                if (baseVehicle == null) {
                    break;
                }
                RandomizedWorldBase.createRandomDeadBody(element.position.x, element.position.y, element.z, element.direction, false, 6, 0, null);
                this.addTrailOfBlood(element.position.x, element.position.y, element.z, Vector2.getDirection(element.position.x - baseVehicle.x, element.position.y - baseVehicle.y), 15);
                break;
            }
            case "vehicle1": {
                final BaseVehicle addVehicle = this.addVehicle(zone, element.position.x, element.position.y, z, element.direction, "bad", null, null, null);
                if (addVehicle == null) {
                    break;
                }
                final BaseVehicle setSmashed = addVehicle.setSmashed("Front");
                this.addZombiesOnVehicle(Rand.Next(3, 6), "Bandit", null, setSmashed);
                vehicleStorySpawner.setParameter("vehicle1", setSmashed);
                break;
            }
            case "vehicle2": {
                final BaseVehicle addVehicle2 = this.addVehicle(zone, element.position.x, element.position.y, z, element.direction, "bad", null, null, null);
                if (addVehicle2 == null) {
                    break;
                }
                this.addZombiesOnVehicle(Rand.Next(3, 5), null, null, addVehicle2);
                vehicleStorySpawner.setParameter("vehicle2", addVehicle2);
                break;
            }
        }
    }
}

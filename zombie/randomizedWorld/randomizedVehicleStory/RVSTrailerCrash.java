// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedVehicleStory;

import zombie.vehicles.BaseVehicle;
import zombie.iso.IsoGridSquare;
import zombie.iso.Vector2;
import zombie.core.Rand;
import zombie.iso.IsoDirections;
import zombie.iso.IsoChunk;
import zombie.iso.IsoMetaGrid;

public final class RVSTrailerCrash extends RandomizedVehicleStoryBase
{
    public RVSTrailerCrash() {
        this.name = "Trailer Crash";
        this.minZoneWidth = 5;
        this.minZoneHeight = 12;
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
        final float n2 = 0.0f;
        final float n3 = -1.5f;
        instance.addElement("vehicle1", n2, n3, toVector.getDirection(), 2.0f, 5.0f);
        final int n4 = 4;
        instance.addElement("trailer", n2, n3 + 2.5f + 1.0f + n4 / 2.0f, toVector.getDirection(), 2.0f, (float)n4);
        final boolean nextBool = Rand.NextBool(2);
        final Vector2 vector2 = nextBool ? IsoDirections.E.ToVector() : IsoDirections.W.ToVector();
        vector2.rotate(Rand.Next(-n, n));
        instance.addElement("vehicle2", 0.0f, n3 - 2.5f - 1.0f, vector2.getDirection(), 2.0f, 5.0f);
        instance.setParameter("zone", zone);
        instance.setParameter("east", nextBool);
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
        final boolean parameterBoolean = vehicleStorySpawner.getParameterBoolean("east");
        final String id = element.id;
        switch (id) {
            case "vehicle1": {
                final BaseVehicle addVehicle = this.addVehicle(zone, element.position.x, element.position.y, z, element.direction, null, "Base.PickUpVan", null, null);
                if (addVehicle == null) {
                    break;
                }
                final BaseVehicle setSmashed = addVehicle.setSmashed("Front");
                setSmashed.setBloodIntensity("Front", 1.0f);
                String s = Rand.NextBool(2) ? "Base.Trailer" : "Base.TrailerCover";
                if (Rand.NextBool(6)) {
                    s = "Base.TrailerAdvert";
                }
                final BaseVehicle addTrailer = this.addTrailer(setSmashed, zone, square.getChunk(), null, null, s);
                if (addTrailer != null && Rand.NextBool(3)) {
                    addTrailer.setAngles(addTrailer.getAngleX(), Rand.Next(90.0f, 110.0f), addTrailer.getAngleZ());
                }
                if (Rand.Next(10) < 4) {
                    this.addZombiesOnVehicle(Rand.Next(2, 5), null, null, setSmashed);
                }
                vehicleStorySpawner.setParameter("vehicle1", setSmashed);
                break;
            }
            case "vehicle2": {
                final BaseVehicle addVehicle2 = this.addVehicle(zone, element.position.x, element.position.y, z, element.direction, "bad", null, null, null);
                if (addVehicle2 == null) {
                    break;
                }
                final String smashed = parameterBoolean ? "Right" : "Left";
                final BaseVehicle setSmashed2 = addVehicle2.setSmashed(smashed);
                setSmashed2.setBloodIntensity(smashed, 1.0f);
                vehicleStorySpawner.setParameter("vehicle2", setSmashed2);
                break;
            }
        }
    }
}

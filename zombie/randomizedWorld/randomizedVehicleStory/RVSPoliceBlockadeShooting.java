// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedVehicleStory;

import zombie.iso.IsoGridSquare;
import zombie.randomizedWorld.RandomizedWorldBase;
import zombie.vehicles.BaseVehicle;
import zombie.iso.IsoObject;
import zombie.iso.IsoCell;
import zombie.iso.Vector2;
import zombie.iso.IsoDirections;
import zombie.core.Rand;
import zombie.iso.IsoChunk;
import zombie.iso.IsoMetaGrid;

public final class RVSPoliceBlockadeShooting extends RandomizedVehicleStoryBase
{
    public RVSPoliceBlockadeShooting() {
        this.name = "Police Blockade Shooting";
        this.minZoneWidth = 8;
        this.minZoneHeight = 8;
        this.setChance(1);
        this.setMaximumDays(30);
    }
    
    @Override
    public boolean isValid(final IsoMetaGrid.Zone zone, final IsoChunk isoChunk, final boolean b) {
        return super.isValid(zone, isoChunk, b) && zone.isRectangle();
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
        boolean nextBool = Rand.NextBool(2);
        if (b) {
            nextBool = true;
        }
        final IsoDirections isoDirections = Rand.NextBool(2) ? IsoDirections.W : IsoDirections.E;
        final Vector2 toVector = isoDirections.ToVector();
        toVector.rotate(Rand.Next(-n, n));
        instance.addElement("vehicle1", -n2, n3, toVector.getDirection(), 2.0f, 5.0f);
        final Vector2 toVector2 = isoDirections.RotLeft(4).ToVector();
        toVector2.rotate(Rand.Next(-n, n));
        instance.addElement("vehicle2", n2, -n3, toVector2.getDirection(), 2.0f, 5.0f);
        instance.addElement("barricade", 0.0f, nextBool ? (-n3 - 2.5f) : (n3 + 2.5f), IsoDirections.N.ToVector().getDirection(), (float)this.zoneWidth, 1.0f);
        for (int next = Rand.Next(7, 15), i = 0; i < next; ++i) {
            instance.addElement("corpse", Rand.Next(-this.zoneWidth / 2.0f + 1.0f, this.zoneWidth / 2.0f - 1.0f), nextBool ? (Rand.Next(-7, -4) - n3) : (Rand.Next(5, 8) + n3), IsoDirections.getRandom().ToVector().getDirection(), 1.0f, 2.0f);
        }
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
            case "barricade": {
                if (this.horizontalZone) {
                    final int n2 = (int)(element.position.y - element.width / 2.0f);
                    final int n3 = (int)(element.position.y + element.width / 2.0f) - 1;
                    final int n4 = (int)element.position.x;
                    for (int i = n2; i <= n3; ++i) {
                        final IsoGridSquare gridSquare = IsoCell.getInstance().getGridSquare(n4, i, zone.z);
                        if (gridSquare != null) {
                            if (i == n2 || i == n3) {
                                gridSquare.AddTileObject(IsoObject.getNew(gridSquare, "street_decoration_01_26", null, false));
                            }
                            else {
                                gridSquare.AddTileObject(IsoObject.getNew(gridSquare, "construction_01_9", null, false));
                            }
                        }
                    }
                    break;
                }
                final int n5 = (int)(element.position.x - element.width / 2.0f);
                final int n6 = (int)(element.position.x + element.width / 2.0f) - 1;
                final int n7 = (int)element.position.y;
                for (int j = n5; j <= n6; ++j) {
                    final IsoGridSquare gridSquare2 = IsoCell.getInstance().getGridSquare(j, n7, zone.z);
                    if (gridSquare2 != null) {
                        if (j == n5 || j == n6) {
                            gridSquare2.AddTileObject(IsoObject.getNew(gridSquare2, "street_decoration_01_26", null, false));
                        }
                        else {
                            gridSquare2.AddTileObject(IsoObject.getNew(gridSquare2, "construction_01_8", null, false));
                        }
                    }
                }
                break;
            }
            case "corpse": {
                final BaseVehicle baseVehicle = vehicleStorySpawner.getParameter("vehicle1", BaseVehicle.class);
                if (baseVehicle == null) {
                    break;
                }
                RandomizedWorldBase.createRandomDeadBody(element.position.x, element.position.y, (float)zone.z, element.direction, false, 10, 10, null);
                this.addTrailOfBlood(element.position.x, element.position.y, element.z, (this.horizontalZone ? ((element.position.x < baseVehicle.x) ? IsoDirections.W : IsoDirections.E) : ((element.position.y < baseVehicle.y) ? IsoDirections.N : IsoDirections.S)).ToVector().getDirection(), 5);
                break;
            }
            case "vehicle1":
            case "vehicle2": {
                final BaseVehicle addVehicle = this.addVehicle(zone, element.position.x, element.position.y, z, element.direction, null, parameterString, null, null);
                if (addVehicle == null) {
                    break;
                }
                vehicleStorySpawner.setParameter(element.id, addVehicle);
                if (Rand.NextBool(3)) {
                    addVehicle.setHeadlightsOn(true);
                    addVehicle.setLightbarLightsMode(2);
                }
                this.addZombiesOnVehicle(Rand.Next(2, 4), "PoliceRiot", 0, addVehicle);
                break;
            }
        }
    }
}

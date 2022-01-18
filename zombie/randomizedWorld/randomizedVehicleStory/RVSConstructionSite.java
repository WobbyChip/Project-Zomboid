// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedVehicleStory;

import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.vehicles.BaseVehicle;
import zombie.core.math.PZMath;
import zombie.iso.Vector2;
import zombie.iso.IsoDirections;
import zombie.core.Rand;
import zombie.iso.IsoChunk;
import zombie.iso.IsoMetaGrid;
import java.util.ArrayList;

public final class RVSConstructionSite extends RandomizedVehicleStoryBase
{
    private ArrayList<String> tools;
    
    public RVSConstructionSite() {
        this.tools = null;
        this.name = "Construction Site";
        this.minZoneWidth = 6;
        this.minZoneHeight = 6;
        this.setChance(3);
        (this.tools = new ArrayList<String>()).add("Base.PickAxe");
        this.tools.add("Base.Shovel");
        this.tools.add("Base.Shovel2");
        this.tools.add("Base.Hammer");
        this.tools.add("Base.LeadPipe");
        this.tools.add("Base.PipeWrench");
        this.tools.add("Base.Sledgehammer");
        this.tools.add("Base.Sledgehammer2");
    }
    
    @Override
    public void randomizeVehicleStory(final IsoMetaGrid.Zone zone, final IsoChunk isoChunk) {
        this.callVehicleStorySpawner(zone, isoChunk, 0.0f);
    }
    
    @Override
    public boolean initVehicleStorySpawner(final IsoMetaGrid.Zone zone, final IsoChunk isoChunk, final boolean b) {
        final VehicleStorySpawner instance = VehicleStorySpawner.getInstance();
        instance.clear();
        boolean nextBool = Rand.NextBool(2);
        if (b) {
            nextBool = true;
        }
        final int n = nextBool ? 1 : -1;
        final Vector2 toVector = IsoDirections.N.ToVector();
        float n2 = 0.5235988f;
        if (b) {
            n2 = 0.0f;
        }
        toVector.rotate(Rand.Next(-n2, n2));
        instance.addElement("vehicle1", -n * 2.0f, 0.0f, toVector.getDirection(), 2.0f, 5.0f);
        instance.addElement("manhole", n * 1.5f, 1.5f, 0.0f, 3.0f, 3.0f);
        for (int next = Rand.Next(0, 3), i = 0; i < next; ++i) {
            instance.addElement("tool", n * Rand.Next(0.0f, 3.0f), -Rand.Next(0.7f, 2.3f), 0.0f, 1.0f, 1.0f);
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
        final float max = PZMath.max(element.position.x - square.x, 0.001f);
        final float max2 = PZMath.max(element.position.y - square.y, 0.001f);
        final float n = 0.0f;
        final float z = element.z;
        final IsoMetaGrid.Zone zone = vehicleStorySpawner.getParameter("zone", IsoMetaGrid.Zone.class);
        final BaseVehicle baseVehicle = vehicleStorySpawner.getParameter("vehicle1", BaseVehicle.class);
        final String id = element.id;
        switch (id) {
            case "manhole": {
                square.AddTileObject(IsoObject.getNew(square, "street_decoration_01_15", null, false));
                final IsoGridSquare adjacentSquare = square.getAdjacentSquare(IsoDirections.E);
                if (adjacentSquare != null) {
                    adjacentSquare.AddTileObject(IsoObject.getNew(adjacentSquare, "street_decoration_01_26", null, false));
                }
                final IsoGridSquare adjacentSquare2 = square.getAdjacentSquare(IsoDirections.W);
                if (adjacentSquare2 != null) {
                    adjacentSquare2.AddTileObject(IsoObject.getNew(adjacentSquare2, "street_decoration_01_26", null, false));
                }
                final IsoGridSquare adjacentSquare3 = square.getAdjacentSquare(IsoDirections.S);
                if (adjacentSquare3 != null) {
                    adjacentSquare3.AddTileObject(IsoObject.getNew(adjacentSquare3, "street_decoration_01_26", null, false));
                }
                final IsoGridSquare adjacentSquare4 = square.getAdjacentSquare(IsoDirections.N);
                if (adjacentSquare4 != null) {
                    adjacentSquare4.AddTileObject(IsoObject.getNew(adjacentSquare4, "street_decoration_01_26", null, false));
                    break;
                }
                break;
            }
            case "tool": {
                square.AddWorldInventoryItem(this.tools.get(Rand.Next(this.tools.size())), max, max2, n);
                break;
            }
            case "vehicle1": {
                final BaseVehicle addVehicle = this.addVehicle(zone, element.position.x, element.position.y, z, element.direction, null, "Base.PickUpTruck", null, "ConstructionWorker");
                if (addVehicle == null) {
                    break;
                }
                this.addZombiesOnVehicle(Rand.Next(2, 5), "ConstructionWorker", 0, addVehicle);
                this.addZombiesOnVehicle(1, "Foreman", 0, addVehicle);
                vehicleStorySpawner.setParameter("vehicle1", addVehicle);
                break;
            }
        }
    }
}

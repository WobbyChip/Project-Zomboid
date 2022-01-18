// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedVehicleStory;

import zombie.vehicles.VehiclePart;
import zombie.iso.IsoGridSquare;
import zombie.inventory.InventoryItem;
import zombie.vehicles.BaseVehicle;
import zombie.core.math.PZMath;
import zombie.iso.IsoDirections;
import zombie.core.Rand;
import zombie.iso.IsoChunk;
import zombie.iso.IsoMetaGrid;

public final class RVSChangingTire extends RandomizedVehicleStoryBase
{
    public RVSChangingTire() {
        this.name = "Changing Tire";
        this.minZoneWidth = 5;
        this.minZoneHeight = 5;
        this.setChance(3);
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
        boolean nextBool = Rand.NextBool(2);
        if (b) {
            nextBool = true;
        }
        final int n = nextBool ? 1 : -1;
        instance.addElement("vehicle1", n * -1.5f, 0.0f, IsoDirections.N.ToVector().getDirection(), 2.0f, 5.0f);
        instance.addElement("tire1", n * 0.0f, 0.0f, 0.0f, 1.0f, 1.0f);
        instance.addElement("tool1", n * 0.8f, -0.2f, 0.0f, 1.0f, 1.0f);
        instance.addElement("tool2", n * 1.2f, 0.2f, 0.0f, 1.0f, 1.0f);
        instance.addElement("tire2", n * 2.0f, 0.0f, 0.0f, 1.0f, 1.0f);
        instance.setParameter("zone", zone);
        instance.setParameter("removeRightWheel", nextBool);
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
        final boolean parameterBoolean = vehicleStorySpawner.getParameterBoolean("removeRightWheel");
        final BaseVehicle baseVehicle = vehicleStorySpawner.getParameter("vehicle1", BaseVehicle.class);
        final String id = element.id;
        switch (id) {
            case "tire1": {
                if (baseVehicle == null) {
                    break;
                }
                final InventoryItem addWorldInventoryItem = square.AddWorldInventoryItem(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, baseVehicle.getScript().getMechanicType()), max, max2, n);
                if (addWorldInventoryItem != null) {
                    addWorldInventoryItem.setItemCapacity((float)addWorldInventoryItem.getMaxCapacity());
                }
                this.addBloodSplat(square, Rand.Next(10, 20));
                break;
            }
            case "tire2": {
                if (baseVehicle == null) {
                    break;
                }
                final InventoryItem addWorldInventoryItem2 = square.AddWorldInventoryItem(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, baseVehicle.getScript().getMechanicType()), max, max2, n);
                if (addWorldInventoryItem2 != null) {
                    addWorldInventoryItem2.setCondition(0);
                    break;
                }
                break;
            }
            case "tool1": {
                square.AddWorldInventoryItem("Base.LugWrench", max, max2, n);
                break;
            }
            case "tool2": {
                square.AddWorldInventoryItem("Base.Jack", max, max2, n);
                break;
            }
            case "vehicle1": {
                final BaseVehicle addVehicle = this.addVehicle(zone, element.position.x, element.position.y, z, element.direction, "good", null, null, null);
                if (addVehicle == null) {
                    break;
                }
                addVehicle.setGeneralPartCondition(0.7f, 40.0f);
                addVehicle.setRust(0.0f);
                final VehiclePart partById = addVehicle.getPartById(parameterBoolean ? "TireRearRight" : "TireRearLeft");
                addVehicle.setTireRemoved(partById.getWheelIndex(), true);
                partById.setModelVisible("InflatedTirePlusWheel", false);
                partById.setInventoryItem(null);
                this.addZombiesOnVehicle(2, null, null, addVehicle);
                vehicleStorySpawner.setParameter("vehicle1", addVehicle);
                break;
            }
        }
    }
}

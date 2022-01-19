// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio.parameters;

import zombie.iso.IsoObject;
import zombie.iso.IsoGridSquare;
import zombie.vehicles.BaseVehicle;
import zombie.audio.FMODLocalParameter;

public final class ParameterVehicleRoadMaterial extends FMODLocalParameter
{
    private final BaseVehicle vehicle;
    
    public ParameterVehicleRoadMaterial(final BaseVehicle vehicle) {
        super("VehicleRoadMaterial");
        this.vehicle = vehicle;
    }
    
    @Override
    public float calculateCurrentValue() {
        if (!this.vehicle.isEngineRunning()) {
            return Float.isNaN(this.getCurrentValue()) ? 0.0f : this.getCurrentValue();
        }
        return (float)this.getMaterial().label;
    }
    
    private Material getMaterial() {
        final IsoGridSquare currentSquare = this.vehicle.getCurrentSquare();
        if (currentSquare == null) {
            return Material.Concrete;
        }
        final IsoObject floor = this.vehicle.getCurrentSquare().getFloor();
        if (floor == null || floor.getSprite() == null || floor.getSprite().getName() == null) {
            return Material.Concrete;
        }
        final String name = floor.getSprite().getName();
        if (name.endsWith("blends_natural_01_5") || name.endsWith("blends_natural_01_6") || name.endsWith("blends_natural_01_7") || name.endsWith("blends_natural_01_0")) {
            return Material.Sand;
        }
        if (name.endsWith("blends_natural_01_64") || name.endsWith("blends_natural_01_69") || name.endsWith("blends_natural_01_70") || name.endsWith("blends_natural_01_71")) {
            return Material.Dirt;
        }
        if (name.startsWith("blends_natural_01")) {
            return Material.Grass;
        }
        if (name.endsWith("blends_street_01_48") || name.endsWith("blends_street_01_53") || name.endsWith("blends_street_01_54") || name.endsWith("blends_street_01_55")) {
            return Material.Gravel;
        }
        if (name.startsWith("floors_interior_tilesandwood_01_")) {
            final int int1 = Integer.parseInt(name.replaceFirst("floors_interior_tilesandwood_01_", ""));
            if (int1 > 40 && int1 < 48) {
                return Material.Wood;
            }
            return Material.Concrete;
        }
        else {
            if (name.startsWith("carpentry_02_")) {
                return Material.Wood;
            }
            if (name.contains("interior_carpet_")) {
                return Material.Carpet;
            }
            if (currentSquare.getPuddlesInGround() > 0.1) {
                return Material.Puddle;
            }
            return Material.Concrete;
        }
    }
    
    enum Material
    {
        Concrete(0), 
        Grass(1), 
        Gravel(2), 
        Puddle(3), 
        Snow(4), 
        Wood(5), 
        Carpet(6), 
        Dirt(7), 
        Sand(8);
        
        final int label;
        
        private Material(final int label) {
            this.label = label;
        }
        
        private static /* synthetic */ Material[] $values() {
            return new Material[] { Material.Concrete, Material.Grass, Material.Gravel, Material.Puddle, Material.Snow, Material.Wood, Material.Carpet, Material.Dirt, Material.Sand };
        }
        
        static {
            $VALUES = $values();
        }
    }
}

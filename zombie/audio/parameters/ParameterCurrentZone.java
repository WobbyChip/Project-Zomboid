// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio.parameters;

import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoObject;
import zombie.audio.FMODLocalParameter;

public final class ParameterCurrentZone extends FMODLocalParameter
{
    private final IsoObject object;
    private IsoMetaGrid.Zone metaZone;
    private Zone zone;
    
    public ParameterCurrentZone(final IsoObject object) {
        super("CurrentZone");
        this.zone = Zone.None;
        this.object = object;
    }
    
    @Override
    public float calculateCurrentValue() {
        final IsoGridSquare square = this.object.getSquare();
        if (square == null) {
            this.zone = Zone.None;
            return (float)this.zone.label;
        }
        if (square.zone == this.metaZone) {
            return (float)this.zone.label;
        }
        this.metaZone = square.zone;
        if (this.metaZone == null) {
            this.zone = Zone.None;
            return (float)this.zone.label;
        }
        final String type = this.metaZone.type;
        Zone zone = null;
        switch (type) {
            case "DeepForest": {
                zone = Zone.DeepForest;
                break;
            }
            case "Farm": {
                zone = Zone.Farm;
                break;
            }
            case "Forest": {
                zone = Zone.Forest;
                break;
            }
            case "Nav": {
                zone = Zone.Nav;
                break;
            }
            case "TownZone": {
                zone = Zone.Town;
                break;
            }
            case "TrailerPark": {
                zone = Zone.TrailerPark;
                break;
            }
            case "Vegitation": {
                zone = Zone.Vegetation;
                break;
            }
            default: {
                zone = Zone.None;
                break;
            }
        }
        this.zone = zone;
        return (float)this.zone.label;
    }
    
    enum Zone
    {
        None(0), 
        DeepForest(1), 
        Farm(2), 
        Forest(3), 
        Nav(4), 
        Town(5), 
        TrailerPark(6), 
        Vegetation(7);
        
        final int label;
        
        private Zone(final int label) {
            this.label = label;
        }
        
        private static /* synthetic */ Zone[] $values() {
            return new Zone[] { Zone.None, Zone.DeepForest, Zone.Farm, Zone.Forest, Zone.Nav, Zone.Town, Zone.TrailerPark, Zone.Vegetation };
        }
        
        static {
            $VALUES = $values();
        }
    }
}

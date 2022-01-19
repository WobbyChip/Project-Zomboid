// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio.parameters;

import zombie.scripting.objects.VehicleScript;
import org.joml.Vector3f;
import zombie.vehicles.BaseVehicle;
import zombie.audio.FMODLocalParameter;

public class ParameterVehicleHitLocation extends FMODLocalParameter
{
    private HitLocation location;
    
    public ParameterVehicleHitLocation() {
        super("VehicleHitLocation");
        this.location = HitLocation.Front;
    }
    
    @Override
    public float calculateCurrentValue() {
        return (float)this.location.label;
    }
    
    public static HitLocation calculateLocation(final BaseVehicle baseVehicle, final float n, final float n2, final float n3) {
        final VehicleScript script = baseVehicle.getScript();
        if (script == null) {
            return HitLocation.Front;
        }
        final Vector3f localPos = baseVehicle.getLocalPos(n, n2, n3, BaseVehicle.TL_vector3f_pool.get().alloc());
        final Vector3f extents = script.getExtents();
        final Vector3f centerOfMassOffset = script.getCenterOfMassOffset();
        final float n4 = centerOfMassOffset.z - extents.z / 2.0f;
        final float n5 = centerOfMassOffset.z + extents.z / 2.0f;
        final float n6 = n4 * 0.9f;
        final float n7 = n5 * 0.9f;
        HitLocation hitLocation;
        if (localPos.z >= n6 && localPos.z <= n7) {
            hitLocation = HitLocation.Side;
        }
        else if (localPos.z > 0.0f) {
            hitLocation = HitLocation.Front;
        }
        else {
            hitLocation = HitLocation.Rear;
        }
        BaseVehicle.TL_vector3f_pool.get().release(localPos);
        return hitLocation;
    }
    
    public void setLocation(final HitLocation location) {
        this.location = location;
    }
    
    public enum HitLocation
    {
        Front(0), 
        Rear(1), 
        Side(2);
        
        final int label;
        
        private HitLocation(final int label) {
            this.label = label;
        }
        
        private static /* synthetic */ HitLocation[] $values() {
            return new HitLocation[] { HitLocation.Front, HitLocation.Rear, HitLocation.Side };
        }
        
        static {
            $VALUES = $values();
        }
    }
}

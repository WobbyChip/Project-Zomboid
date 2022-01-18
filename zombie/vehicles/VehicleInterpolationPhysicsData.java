// 
// Decompiled by Procyon v0.5.36
// 

package zombie.vehicles;

public class VehicleInterpolationPhysicsData
{
    long time;
    float force;
    float[] data;
    
    VehicleInterpolationPhysicsData() {
        this.data = new float[23];
        this.time = 0L;
    }
    
    void copy(final VehicleInterpolationPhysicsData vehicleInterpolationPhysicsData) {
        this.time = vehicleInterpolationPhysicsData.time;
        for (int i = 0; i < this.data.length; ++i) {
            this.data[i] = vehicleInterpolationPhysicsData.data[i];
        }
    }
}

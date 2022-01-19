// 
// Decompiled by Procyon v0.5.36
// 

package zombie.vehicles;

public class VehicleInterpolationData
{
    long time;
    float x;
    float y;
    float z;
    float qx;
    float qy;
    float qz;
    float qw;
    float vx;
    float vy;
    float vz;
    short w_count;
    float[] w_st;
    float[] w_rt;
    float[] w_si;
    float[] w_sl;
    
    VehicleInterpolationData() {
        this.time = 0L;
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
        this.qx = 0.0f;
        this.qy = 0.0f;
        this.qz = 0.0f;
        this.qw = 0.0f;
        this.vx = 0.0f;
        this.vy = 0.0f;
        this.vz = 0.0f;
        this.w_count = 4;
        this.w_st = new float[4];
        this.w_rt = new float[4];
        this.w_si = new float[4];
        this.w_sl = new float[4];
    }
    
    void setNumWheels(final int n) {
        this.w_count = (short)n;
        if (n > this.w_st.length) {
            this.w_st = new float[n];
            this.w_rt = new float[n];
            this.w_si = new float[n];
            this.w_sl = new float[n];
        }
    }
    
    void copy(final VehicleInterpolationData vehicleInterpolationData) {
        this.time = vehicleInterpolationData.time;
        this.x = vehicleInterpolationData.x;
        this.y = vehicleInterpolationData.y;
        this.z = vehicleInterpolationData.z;
        this.qx = vehicleInterpolationData.qx;
        this.qy = vehicleInterpolationData.qy;
        this.qz = vehicleInterpolationData.qz;
        this.qw = vehicleInterpolationData.qw;
        this.vx = vehicleInterpolationData.vx;
        this.vy = vehicleInterpolationData.vy;
        this.vz = vehicleInterpolationData.vz;
        this.setNumWheels(vehicleInterpolationData.w_count);
        for (short n = 0; n < vehicleInterpolationData.w_count; ++n) {
            this.w_st[n] = vehicleInterpolationData.w_st[n];
            this.w_rt[n] = vehicleInterpolationData.w_rt[n];
            this.w_si[n] = vehicleInterpolationData.w_si[n];
            this.w_sl[n] = vehicleInterpolationData.w_sl[n];
        }
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.radio.devices;

import zombie.iso.IsoGridSquare;

public interface WaveSignalDevice
{
    DeviceData getDeviceData();
    
    void setDeviceData(final DeviceData p0);
    
    float getDelta();
    
    void setDelta(final float p0);
    
    IsoGridSquare getSquare();
    
    float getX();
    
    float getY();
    
    float getZ();
    
    void AddDeviceText(final String p0, final float p1, final float p2, final float p3, final String p4, final int p5);
    
    boolean HasPlayerInRange();
}

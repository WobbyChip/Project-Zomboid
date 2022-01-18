// 
// Decompiled by Procyon v0.5.36
// 

package zombie.vehicles;

public final class EngineRPMData
{
    public float gearChange;
    public float afterGearChange;
    
    public EngineRPMData() {
    }
    
    public EngineRPMData(final float gearChange, final float afterGearChange) {
        this.gearChange = gearChange;
        this.afterGearChange = afterGearChange;
    }
    
    public void reset() {
        this.gearChange = 0.0f;
        this.afterGearChange = 0.0f;
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.vehicles;

import zombie.scripting.objects.VehicleScript;

public final class LightbarSirenMode
{
    private int mode;
    private final int modeMax = 3;
    
    public LightbarSirenMode() {
        this.mode = 0;
    }
    
    public int get() {
        return this.mode;
    }
    
    public void set(final int mode) {
        if (mode > 3) {
            this.mode = 3;
            return;
        }
        if (mode < 0) {
            this.mode = 0;
            return;
        }
        this.mode = mode;
    }
    
    public boolean isEnable() {
        return this.mode != 0;
    }
    
    public String getSoundName(final VehicleScript.LightBar lightBar) {
        if (this.isEnable()) {
            if (this.mode == 1) {
                return lightBar.soundSiren0;
            }
            if (this.mode == 2) {
                return lightBar.soundSiren1;
            }
            if (this.mode == 3) {
                return lightBar.soundSiren2;
            }
        }
        return "";
    }
}

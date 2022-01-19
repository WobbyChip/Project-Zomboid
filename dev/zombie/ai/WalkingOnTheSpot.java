// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai;

import zombie.GameTime;
import zombie.iso.IsoUtils;

public final class WalkingOnTheSpot
{
    private float x;
    private float y;
    private float time;
    
    public boolean check(final float x, final float y) {
        if (IsoUtils.DistanceToSquared(this.x, this.y, x, y) < 0.010000001f) {
            this.time += GameTime.getInstance().getMultiplier();
        }
        else {
            this.x = x;
            this.y = y;
            this.time = 0.0f;
        }
        return this.time > 400.0f;
    }
    
    public void reset(final float x, final float y) {
        this.x = x;
        this.y = y;
        this.time = 0.0f;
    }
}

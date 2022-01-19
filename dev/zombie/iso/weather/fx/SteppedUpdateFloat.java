// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.weather.fx;

public class SteppedUpdateFloat
{
    private float current;
    private float step;
    private float target;
    private float min;
    private float max;
    
    public SteppedUpdateFloat(final float n, final float step, final float min, final float max) {
        this.current = n;
        this.step = step;
        this.target = n;
        this.min = min;
        this.max = max;
    }
    
    public float value() {
        return this.current;
    }
    
    public void setTarget(final float n) {
        this.target = this.clamp(this.min, this.max, n);
    }
    
    public float getTarget() {
        return this.target;
    }
    
    public void overrideCurrentValue(final float current) {
        this.current = current;
    }
    
    private float clamp(final float a, final float a2, float n) {
        n = Math.min(a2, n);
        n = Math.max(a, n);
        return n;
    }
    
    public void update(final float n) {
        if (this.current != this.target) {
            if (this.target > this.current) {
                this.current += this.step * n;
                if (this.current > this.target) {
                    this.current = this.target;
                }
            }
            else if (this.target < this.current) {
                this.current -= this.step * n;
                if (this.current < this.target) {
                    this.current = this.target;
                }
            }
        }
    }
}

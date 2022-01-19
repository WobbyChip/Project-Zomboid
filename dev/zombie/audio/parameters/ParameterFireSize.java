// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio.parameters;

import zombie.audio.FMODLocalParameter;

public final class ParameterFireSize extends FMODLocalParameter
{
    private int size;
    
    public ParameterFireSize() {
        super("FireSize");
        this.size = 0;
    }
    
    @Override
    public float calculateCurrentValue() {
        return (float)this.size;
    }
    
    public void setSize(final int size) {
        this.size = size;
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio.parameters;

import zombie.core.math.PZMath;
import zombie.iso.IsoObject;
import zombie.characters.IsoZombie;
import zombie.audio.FMODLocalParameter;

public final class ParameterPlayerDistance extends FMODLocalParameter
{
    private final IsoZombie zombie;
    
    public ParameterPlayerDistance(final IsoZombie zombie) {
        super("PlayerDistance");
        this.zombie = zombie;
    }
    
    @Override
    public float calculateCurrentValue() {
        if (this.zombie.target == null) {
            return 1000.0f;
        }
        return (float)(int)PZMath.ceil(this.zombie.DistToProper(this.zombie.target));
    }
}

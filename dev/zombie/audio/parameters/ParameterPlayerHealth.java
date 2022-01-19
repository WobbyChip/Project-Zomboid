// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio.parameters;

import zombie.core.math.PZMath;
import zombie.characters.IsoPlayer;
import zombie.audio.FMODLocalParameter;

public final class ParameterPlayerHealth extends FMODLocalParameter
{
    private final IsoPlayer player;
    
    public ParameterPlayerHealth(final IsoPlayer player) {
        super("PlayerHealth");
        this.player = player;
    }
    
    @Override
    public float calculateCurrentValue() {
        return PZMath.clamp(this.player.getHealth() / 100.0f, 0.0f, 1.0f);
    }
}

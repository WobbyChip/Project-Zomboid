// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio.parameters;

import zombie.characters.IsoPlayer;
import zombie.audio.FMODLocalParameter;

public final class ParameterLocalPlayer extends FMODLocalParameter
{
    private final IsoPlayer player;
    
    public ParameterLocalPlayer(final IsoPlayer player) {
        super("LocalPlayer");
        this.player = player;
    }
    
    @Override
    public float calculateCurrentValue() {
        return this.player.isLocalPlayer() ? 1.0f : 0.0f;
    }
}

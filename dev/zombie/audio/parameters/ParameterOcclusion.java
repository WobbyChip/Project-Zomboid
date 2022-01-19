// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio.parameters;

import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.characters.IsoPlayer;
import zombie.core.math.PZMath;
import fmod.fmod.FMODSoundEmitter;
import zombie.audio.FMODLocalParameter;

public final class ParameterOcclusion extends FMODLocalParameter
{
    private final FMODSoundEmitter emitter;
    private float currentValue;
    
    public ParameterOcclusion(final FMODSoundEmitter emitter) {
        super("Occlusion");
        this.currentValue = Float.NaN;
        this.emitter = emitter;
    }
    
    @Override
    public float calculateCurrentValue() {
        float min = 1.0f;
        for (int i = 0; i < 4; ++i) {
            min = PZMath.min(min, this.calculateValueForPlayer(i));
        }
        this.currentValue = min;
        return (int)(this.currentValue * 1000.0f) / 1000.0f;
    }
    
    @Override
    public void resetToDefault() {
        this.currentValue = Float.NaN;
    }
    
    private float calculateValueForPlayer(final int n) {
        final IsoPlayer isoPlayer = IsoPlayer.players[n];
        if (isoPlayer == null) {
            return 1.0f;
        }
        final IsoGridSquare currentSquare = isoPlayer.getCurrentSquare();
        final IsoGridSquare gridSquare = IsoWorld.instance.getCell().getGridSquare(this.emitter.x, this.emitter.y, this.emitter.z);
        if (gridSquare == null) {}
        float n2 = 0.0f;
        if (currentSquare != null && gridSquare != null && !gridSquare.isCouldSee(n)) {
            n2 = 1.0f;
        }
        return n2;
    }
}

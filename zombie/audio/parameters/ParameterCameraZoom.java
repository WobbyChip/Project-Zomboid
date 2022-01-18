// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio.parameters;

import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.audio.FMODGlobalParameter;

public final class ParameterCameraZoom extends FMODGlobalParameter
{
    public ParameterCameraZoom() {
        super("CameraZoom");
    }
    
    @Override
    public float calculateCurrentValue() {
        final IsoPlayer player = this.getPlayer();
        if (player == null) {
            return 0.0f;
        }
        return (Core.getInstance().getZoom(player.PlayerIndex) - Core.getInstance().OffscreenBuffer.getMinZoom()) / (Core.getInstance().OffscreenBuffer.getMaxZoom() - Core.getInstance().OffscreenBuffer.getMinZoom());
    }
    
    private IsoPlayer getPlayer() {
        IsoGameCharacter isoGameCharacter = null;
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            final IsoPlayer isoPlayer = IsoPlayer.players[i];
            if (isoPlayer != null && (isoGameCharacter == null || (isoGameCharacter.isDead() && isoPlayer.isAlive()))) {
                isoGameCharacter = isoPlayer;
            }
        }
        return (IsoPlayer)isoGameCharacter;
    }
}

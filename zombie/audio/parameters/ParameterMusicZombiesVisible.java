// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio.parameters;

import zombie.characters.IsoPlayer;
import zombie.core.math.PZMath;
import zombie.audio.FMODGlobalParameter;

public final class ParameterMusicZombiesVisible extends FMODGlobalParameter
{
    private int m_playerIndex;
    
    public ParameterMusicZombiesVisible() {
        super("MusicZombiesVisible");
        this.m_playerIndex = -1;
    }
    
    @Override
    public float calculateCurrentValue() {
        final IsoPlayer choosePlayer = this.choosePlayer();
        if (choosePlayer != null) {
            return (float)PZMath.clamp(choosePlayer.getStats().MusicZombiesVisible, 0, 50);
        }
        return 0.0f;
    }
    
    private IsoPlayer choosePlayer() {
        if (this.m_playerIndex != -1) {
            final IsoPlayer isoPlayer = IsoPlayer.players[this.m_playerIndex];
            if (isoPlayer == null || isoPlayer.isDead()) {
                this.m_playerIndex = -1;
            }
        }
        if (this.m_playerIndex != -1) {
            return IsoPlayer.players[this.m_playerIndex];
        }
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            final IsoPlayer isoPlayer2 = IsoPlayer.players[i];
            if (isoPlayer2 != null && !isoPlayer2.isDead()) {
                this.m_playerIndex = i;
                return isoPlayer2;
            }
        }
        return null;
    }
}

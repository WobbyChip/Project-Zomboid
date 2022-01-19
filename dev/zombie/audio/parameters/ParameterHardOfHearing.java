// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio.parameters;

import zombie.characters.IsoPlayer;
import zombie.audio.FMODGlobalParameter;

public final class ParameterHardOfHearing extends FMODGlobalParameter
{
    private int m_playerIndex;
    
    public ParameterHardOfHearing() {
        super("HardOfHearing");
        this.m_playerIndex = -1;
    }
    
    @Override
    public float calculateCurrentValue() {
        final IsoPlayer choosePlayer = this.choosePlayer();
        if (choosePlayer != null) {
            return choosePlayer.getCharacterTraits().HardOfHearing.isSet() ? 1.0f : 0.0f;
        }
        return 0.0f;
    }
    
    private IsoPlayer choosePlayer() {
        if (this.m_playerIndex != -1 && IsoPlayer.players[this.m_playerIndex] == null) {
            this.m_playerIndex = -1;
        }
        if (this.m_playerIndex != -1) {
            return IsoPlayer.players[this.m_playerIndex];
        }
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            final IsoPlayer isoPlayer = IsoPlayer.players[i];
            if (isoPlayer != null) {
                this.m_playerIndex = i;
                return isoPlayer;
            }
        }
        return null;
    }
}

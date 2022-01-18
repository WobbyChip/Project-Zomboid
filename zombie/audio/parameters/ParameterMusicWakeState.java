// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio.parameters;

import zombie.characters.IsoPlayer;
import zombie.audio.FMODGlobalParameter;

public final class ParameterMusicWakeState extends FMODGlobalParameter
{
    private int m_playerIndex;
    private State m_state;
    
    public ParameterMusicWakeState() {
        super("MusicWakeState");
        this.m_playerIndex = -1;
        this.m_state = State.Awake;
    }
    
    @Override
    public float calculateCurrentValue() {
        final IsoPlayer choosePlayer = this.choosePlayer();
        if (choosePlayer != null && this.m_state == State.Awake && choosePlayer.isAsleep()) {
            this.m_state = State.Sleeping;
        }
        return (float)this.m_state.label;
    }
    
    public void setState(final IsoPlayer isoPlayer, final State state) {
        if (isoPlayer == this.choosePlayer()) {
            this.m_state = state;
        }
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
                this.m_state = (isoPlayer2.isAsleep() ? State.Sleeping : State.Awake);
                return isoPlayer2;
            }
        }
        return null;
    }
    
    public enum State
    {
        Awake(0), 
        Sleeping(1), 
        WakeNormal(2), 
        WakeNightmare(3), 
        WakeZombies(4);
        
        final int label;
        
        private State(final int label) {
            this.label = label;
        }
        
        private static /* synthetic */ State[] $values() {
            return new State[] { State.Awake, State.Sleeping, State.WakeNormal, State.WakeNightmare, State.WakeZombies };
        }
        
        static {
            $VALUES = $values();
        }
    }
}

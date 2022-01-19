// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio.parameters;

import zombie.audio.FMODGlobalParameter;

public final class ParameterMusicState extends FMODGlobalParameter
{
    private State state;
    
    public ParameterMusicState() {
        super("MusicState");
        this.state = State.MainMenu;
    }
    
    @Override
    public float calculateCurrentValue() {
        return (float)this.state.label;
    }
    
    public void setState(final State state) {
        this.state = state;
    }
    
    public enum State
    {
        MainMenu(0), 
        Loading(1), 
        InGame(2), 
        PauseMenu(3), 
        Tutorial(4);
        
        final int label;
        
        private State(final int label) {
            this.label = label;
        }
        
        private static /* synthetic */ State[] $values() {
            return new State[] { State.MainMenu, State.Loading, State.InGame, State.PauseMenu, State.Tutorial };
        }
        
        static {
            $VALUES = $values();
        }
    }
}

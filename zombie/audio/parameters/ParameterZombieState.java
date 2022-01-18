// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio.parameters;

import zombie.characters.IsoZombie;
import zombie.audio.FMODLocalParameter;

public final class ParameterZombieState extends FMODLocalParameter
{
    private final IsoZombie zombie;
    private State state;
    
    public ParameterZombieState(final IsoZombie zombie) {
        super("ZombieState");
        this.state = State.Idle;
        this.zombie = zombie;
    }
    
    @Override
    public float calculateCurrentValue() {
        if (this.zombie.target == null) {
            if (this.state == State.SearchTarget) {
                this.setState(State.Idle);
            }
        }
        else if (this.state == State.Idle) {
            this.setState(State.SearchTarget);
        }
        return (float)this.state.index;
    }
    
    public void setState(final State state) {
        if (state == this.state) {
            return;
        }
        this.state = state;
    }
    
    public boolean isState(final State state) {
        return this.state == state;
    }
    
    public enum State
    {
        Idle(0), 
        Eating(1), 
        SearchTarget(2), 
        LockTarget(3), 
        AttackScratch(4), 
        AttackLacerate(5), 
        AttackBite(6), 
        Hit(7), 
        Death(8), 
        Reanimate(9), 
        Pushed(10), 
        GettingUp(11), 
        Attack(12), 
        RunOver(13);
        
        final int index;
        
        private State(final int index) {
            this.index = index;
        }
        
        private static /* synthetic */ State[] $values() {
            return new State[] { State.Idle, State.Eating, State.SearchTarget, State.LockTarget, State.AttackScratch, State.AttackLacerate, State.AttackBite, State.Hit, State.Death, State.Reanimate, State.Pushed, State.GettingUp, State.Attack, State.RunOver };
        }
        
        static {
            $VALUES = $values();
        }
    }
}

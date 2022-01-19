// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai;

import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.debug.DebugLog;
import java.util.function.Predicate;
import zombie.Lua.LuaEventManager;
import java.util.function.Consumer;
import zombie.util.Lambda;
import zombie.util.list.PZArrayUtil;
import java.util.ArrayList;
import java.util.List;
import zombie.characters.IsoGameCharacter;

public final class StateMachine
{
    private boolean m_isLocked;
    public int activeStateChanged;
    private State m_currentState;
    private State m_previousState;
    private final IsoGameCharacter m_owner;
    private final List<SubstateSlot> m_subStates;
    
    public StateMachine(final IsoGameCharacter owner) {
        this.m_isLocked = false;
        this.activeStateChanged = 0;
        this.m_subStates = new ArrayList<SubstateSlot>();
        this.m_owner = owner;
    }
    
    public void changeState(final State state, final Iterable<State> iterable) {
        this.changeState(state, iterable, false);
    }
    
    public void changeState(final State state2, final Iterable<State> iterable, final boolean b) {
        if (this.m_isLocked) {
            return;
        }
        this.changeRootState(state2, b);
        PZArrayUtil.forEach(this.m_subStates, substateSlot -> substateSlot.shouldBeActive = false);
        PZArrayUtil.forEach((Iterable<Object>)iterable, (Consumer<? super Object>)Lambda.consumer(this, (state, stateMachine) -> {
            if (state != null) {
                stateMachine.ensureSubstateActive(state);
            }
            return;
        }));
        Lambda.forEachFrom(PZArrayUtil::forEach, this.m_subStates, this, (substateSlot2, stateMachine2) -> {
            if (!substateSlot2.shouldBeActive && !substateSlot2.isEmpty()) {
                stateMachine2.removeSubstate(substateSlot2);
            }
        });
    }
    
    private void changeRootState(final State currentState, final boolean b) {
        if (this.m_currentState == currentState) {
            if (b) {
                this.stateEnter(this.m_currentState);
            }
            return;
        }
        final State currentState2 = this.m_currentState;
        if (currentState2 != null) {
            this.stateExit(currentState2);
        }
        this.m_previousState = currentState2;
        if ((this.m_currentState = currentState) != null) {
            this.stateEnter(currentState);
        }
        LuaEventManager.triggerEvent("OnAIStateChange", this.m_owner, this.m_currentState, this.m_previousState);
    }
    
    private void ensureSubstateActive(final State state) {
        final SubstateSlot existingSlot = this.getExistingSlot(state);
        if (existingSlot != null) {
            existingSlot.shouldBeActive = true;
            return;
        }
        final SubstateSlot substateSlot = PZArrayUtil.find(this.m_subStates, SubstateSlot::isEmpty);
        if (substateSlot != null) {
            substateSlot.setState(state);
            substateSlot.shouldBeActive = true;
        }
        else {
            this.m_subStates.add(new SubstateSlot(state));
        }
        this.stateEnter(state);
    }
    
    private SubstateSlot getExistingSlot(final State state) {
        return PZArrayUtil.find(this.m_subStates, (Predicate<SubstateSlot>)Lambda.predicate(state, (substateSlot, state2) -> substateSlot.getState() == state2));
    }
    
    private void removeSubstate(final State state) {
        final SubstateSlot existingSlot = this.getExistingSlot(state);
        if (existingSlot == null) {
            return;
        }
        this.removeSubstate(existingSlot);
    }
    
    private void removeSubstate(final SubstateSlot substateSlot) {
        final State state = substateSlot.getState();
        substateSlot.setState(null);
        this.stateExit(state);
    }
    
    public boolean isSubstate(final State state) {
        return PZArrayUtil.contains(this.m_subStates, (Predicate<SubstateSlot>)Lambda.predicate(state, (substateSlot, state2) -> substateSlot.getState() == state2));
    }
    
    public State getCurrent() {
        return this.m_currentState;
    }
    
    public State getPrevious() {
        return this.m_previousState;
    }
    
    public int getSubStateCount() {
        return this.m_subStates.size();
    }
    
    public State getSubStateAt(final int n) {
        return this.m_subStates.get(n).getState();
    }
    
    public void revertToPreviousState(final State obj) {
        if (this.isSubstate(obj)) {
            this.removeSubstate(obj);
            return;
        }
        if (this.m_currentState != obj) {
            DebugLog.ActionSystem.warn("The sender $s is not an active state in this state machine.", String.valueOf(obj));
            return;
        }
        this.changeRootState(this.m_previousState, false);
    }
    
    public void update() {
        if (this.m_currentState != null) {
            this.m_currentState.execute(this.m_owner);
        }
        Lambda.forEachFrom(PZArrayUtil::forEach, this.m_subStates, this.m_owner, (substateSlot, isoGameCharacter) -> {
            if (!substateSlot.isEmpty()) {
                substateSlot.state.execute(isoGameCharacter);
            }
            return;
        });
        this.logCurrentState();
    }
    
    private void logCurrentState() {
        if (this.m_owner.isAnimationRecorderActive()) {
            this.m_owner.getAnimationPlayerRecorder().logAIState(this.m_currentState, this.m_subStates);
        }
    }
    
    private void stateEnter(final State state) {
        state.enter(this.m_owner);
    }
    
    private void stateExit(final State state) {
        state.exit(this.m_owner);
    }
    
    public final void stateAnimEvent(final int n, final AnimEvent animEvent) {
        if (n == 0) {
            if (this.m_currentState != null) {
                this.m_currentState.animEvent(this.m_owner, animEvent);
            }
            return;
        }
        Lambda.forEachFrom(PZArrayUtil::forEach, this.m_subStates, this.m_owner, animEvent, (substateSlot, isoGameCharacter, animEvent2) -> {
            if (!substateSlot.isEmpty()) {
                substateSlot.state.animEvent(isoGameCharacter, animEvent2);
            }
        });
    }
    
    public boolean isLocked() {
        return this.m_isLocked;
    }
    
    public void setLocked(final boolean isLocked) {
        this.m_isLocked = isLocked;
    }
    
    public static class SubstateSlot
    {
        private State state;
        boolean shouldBeActive;
        
        SubstateSlot(final State state) {
            this.state = state;
            this.shouldBeActive = true;
        }
        
        public State getState() {
            return this.state;
        }
        
        void setState(final State state) {
            this.state = state;
        }
        
        public boolean isEmpty() {
            return this.state == null;
        }
    }
}

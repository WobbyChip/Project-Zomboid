// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters.action;

import zombie.core.profiling.PerformanceProfileProbe;
import zombie.characters.action.conditions.LuaCall;
import zombie.characters.action.conditions.EventNotOccurred;
import zombie.characters.action.conditions.EventOccurred;
import zombie.characters.action.conditions.CharacterVariableCondition;
import java.util.function.Predicate;
import java.util.function.Consumer;
import zombie.network.GameClient;
import zombie.characters.IsoZombie;
import java.util.List;
import zombie.util.list.PZArrayUtil;
import zombie.debug.DebugType;
import zombie.characters.IsoPlayer;
import zombie.debug.DebugLog;
import zombie.util.StringUtils;
import java.util.ArrayList;
import zombie.core.skinnedmodel.advancedanimation.IAnimatable;

public final class ActionContext
{
    private final IAnimatable m_owner;
    private ActionGroup m_stateGroup;
    private ActionState m_currentState;
    private final ArrayList<ActionState> m_childStates;
    private String m_previousStateName;
    private boolean m_statesChanged;
    public final ArrayList<IActionStateChanged> onStateChanged;
    private final ActionContextEvents occurredEvents;
    
    public ActionContext(final IAnimatable owner) {
        this.m_childStates = new ArrayList<ActionState>();
        this.m_previousStateName = null;
        this.m_statesChanged = false;
        this.onStateChanged = new ArrayList<IActionStateChanged>();
        this.occurredEvents = new ActionContextEvents();
        this.m_owner = owner;
    }
    
    public IAnimatable getOwner() {
        return this.m_owner;
    }
    
    public void update() {
        s_performance.update.invokeAndMeasure(this, ActionContext::updateInternal);
    }
    
    private void updateInternal() {
        if (this.m_currentState == null) {
            this.logCurrentState();
            return;
        }
        s_performance.evaluateCurrentStateTransitions.invokeAndMeasure(this, ActionContext::evaluateCurrentStateTransitions);
        s_performance.evaluateSubStateTransitions.invokeAndMeasure(this, ActionContext::evaluateSubStateTransitions);
        this.invokeAnyStateChangedEvents();
        this.logCurrentState();
    }
    
    private void evaluateCurrentStateTransitions() {
        for (int i = 0; i < this.m_currentState.transitions.size(); ++i) {
            final ActionTransition actionTransition = this.m_currentState.transitions.get(i);
            if (actionTransition.passes(this, 0)) {
                if (StringUtils.isNullOrWhitespace(actionTransition.transitionTo)) {
                    DebugLog.ActionSystem.warn("%s> Transition's target state not specified: \"%s\"", this.getOwner().getUID(), actionTransition.transitionTo);
                }
                else {
                    final ActionState value = this.m_stateGroup.get(actionTransition.transitionTo);
                    if (value == null) {
                        DebugLog.ActionSystem.warn("%s> Transition's target state not found: \"%s\"", this.getOwner().getUID(), actionTransition.transitionTo);
                    }
                    else if (!this.hasChildState(value)) {
                        if (!actionTransition.asSubstate || !this.currentStateSupportsChildState(value)) {
                            if (this.m_owner instanceof IsoPlayer) {
                                DebugLog.log(DebugType.ActionSystem, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ((IsoPlayer)this.m_owner).getUsername(), this.m_currentState.getName(), actionTransition.transitionTo));
                            }
                            this.setCurrentState(value);
                            break;
                        }
                        this.tryAddChildState(value);
                    }
                }
            }
        }
    }
    
    private void evaluateSubStateTransitions() {
        for (int i = 0; i < this.childStateCount(); ++i) {
            ActionState currentState = null;
            final ActionState childState = this.getChildStateAt(i);
            for (int j = 0; j < childState.transitions.size(); ++j) {
                final ActionTransition actionTransition = childState.transitions.get(j);
                if (actionTransition.passes(this, 1)) {
                    if (actionTransition.transitionOut) {
                        this.removeChildStateAt(i);
                        --i;
                        break;
                    }
                    if (!StringUtils.isNullOrWhitespace(actionTransition.transitionTo)) {
                        final ActionState value = this.m_stateGroup.get(actionTransition.transitionTo);
                        if (value == null) {
                            DebugLog.ActionSystem.warn("%s> Transition's target state not found: \"%s\"", this.getOwner().getUID(), actionTransition.transitionTo);
                        }
                        else if (!this.hasChildState(value)) {
                            if (this.currentStateSupportsChildState(value)) {
                                this.m_childStates.set(i, value);
                                this.onStatesChanged();
                                break;
                            }
                            if (actionTransition.forceParent) {
                                currentState = value;
                                break;
                            }
                        }
                    }
                }
            }
            if (currentState != this.m_currentState) {
                if (currentState != null) {
                    this.setCurrentState(currentState);
                }
            }
        }
    }
    
    protected boolean currentStateSupportsChildState(final ActionState actionState) {
        return this.m_currentState != null && this.m_currentState.canHaveSubState(actionState);
    }
    
    private boolean hasChildState(final ActionState actionState2) {
        return this.indexOfChildState(actionState -> actionState == actionState2) > -1;
    }
    
    public void setPlaybackStateSnapshot(final ActionStateSnapshot actionStateSnapshot) {
        if (this.m_stateGroup == null) {
            return;
        }
        if (actionStateSnapshot.stateName == null) {
            DebugLog.General.warn((Object)"Snapshot not valid. Missing root state name.");
            return;
        }
        this.setCurrentState(this.m_stateGroup.get(actionStateSnapshot.stateName));
        if (PZArrayUtil.isNullOrEmpty(actionStateSnapshot.childStateNames)) {
            while (this.childStateCount() > 0) {
                this.removeChildStateAt(0);
            }
            return;
        }
        for (int i = 0; i < this.childStateCount(); ++i) {
            if (!StringUtils.contains(actionStateSnapshot.childStateNames, this.getChildStateAt(i).name, StringUtils::equalsIgnoreCase)) {
                this.removeChildStateAt(i);
                --i;
            }
        }
        for (int j = 0; j < actionStateSnapshot.childStateNames.length; ++j) {
            this.tryAddChildState(this.m_stateGroup.get(actionStateSnapshot.childStateNames[j]));
        }
    }
    
    public ActionStateSnapshot getPlaybackStateSnapshot() {
        if (this.m_currentState == null) {
            return null;
        }
        final ActionStateSnapshot actionStateSnapshot = new ActionStateSnapshot();
        actionStateSnapshot.stateName = this.m_currentState.name;
        actionStateSnapshot.childStateNames = new String[this.m_childStates.size()];
        for (int i = 0; i < actionStateSnapshot.childStateNames.length; ++i) {
            actionStateSnapshot.childStateNames[i] = this.m_childStates.get(i).name;
        }
        return actionStateSnapshot;
    }
    
    protected boolean setCurrentState(final ActionState currentState) {
        if (currentState == this.m_currentState) {
            return false;
        }
        this.m_previousStateName = ((this.m_currentState == null) ? "" : this.m_currentState.getName());
        this.m_currentState = currentState;
        for (int i = 0; i < this.m_childStates.size(); ++i) {
            if (!this.m_currentState.canHaveSubState(this.m_childStates.get(i))) {
                this.removeChildStateAt(i);
                --i;
            }
        }
        this.onStatesChanged();
        return true;
    }
    
    protected boolean tryAddChildState(final ActionState e) {
        if (this.hasChildState(e)) {
            return false;
        }
        this.m_childStates.add(e);
        this.onStatesChanged();
        return true;
    }
    
    protected void removeChildStateAt(final int index) {
        this.m_childStates.remove(index);
        this.onStatesChanged();
    }
    
    private void onStatesChanged() {
        this.m_statesChanged = true;
    }
    
    public void logCurrentState() {
        if (this.m_owner.isAnimationRecorderActive()) {
            this.m_owner.getAnimationPlayerRecorder().logActionState(this.m_currentState, this.m_childStates);
        }
    }
    
    private void invokeAnyStateChangedEvents() {
        if (!this.m_statesChanged) {
            return;
        }
        this.m_statesChanged = false;
        this.occurredEvents.clear();
        for (int i = 0; i < this.onStateChanged.size(); ++i) {
            this.onStateChanged.get(i).actionStateChanged(this);
        }
        if (this.m_owner instanceof IsoZombie) {
            ((IsoZombie)this.m_owner).networkAI.extraUpdate();
        }
    }
    
    public ActionState getCurrentState() {
        return this.m_currentState;
    }
    
    public void setGroup(final ActionGroup stateGroup) {
        final String s = (this.m_currentState == null) ? null : this.m_currentState.name;
        this.m_stateGroup = stateGroup;
        final ActionState initialState = stateGroup.getInitialState();
        if (!StringUtils.equalsIgnoreCase(s, initialState.name)) {
            this.setCurrentState(initialState);
        }
        else {
            this.m_currentState = initialState;
        }
    }
    
    public ActionGroup getGroup() {
        return this.m_stateGroup;
    }
    
    public void reportEvent(final String s) {
        this.reportEvent(-1, s);
    }
    
    public void reportEvent(final int n, final String s) {
        this.occurredEvents.add(s, n);
        if (GameClient.bClient && n == -1 && this.m_owner instanceof IsoPlayer && ((IsoPlayer)this.m_owner).isLocalPlayer()) {
            GameClient.sendEvent((IsoPlayer)this.m_owner, s);
        }
    }
    
    public final boolean hasChildStates() {
        return this.childStateCount() > 0;
    }
    
    public final int childStateCount() {
        return (this.m_childStates != null) ? this.m_childStates.size() : 0;
    }
    
    public final void foreachChildState(final Consumer<ActionState> consumer) {
        for (int i = 0; i < this.childStateCount(); ++i) {
            consumer.accept(this.getChildStateAt(i));
        }
    }
    
    public final int indexOfChildState(final Predicate<ActionState> predicate) {
        int n = -1;
        for (int i = 0; i < this.childStateCount(); ++i) {
            if (predicate.test(this.getChildStateAt(i))) {
                n = i;
                break;
            }
        }
        return n;
    }
    
    public final ActionState getChildStateAt(final int n) {
        if (n < 0 || n >= this.childStateCount()) {
            throw new IndexOutOfBoundsException(String.format("Index %d out of bounds. childCount: %d", n, this.childStateCount()));
        }
        return this.m_childStates.get(n);
    }
    
    public List<ActionState> getChildStates() {
        return this.m_childStates;
    }
    
    public String getCurrentStateName() {
        return this.m_currentState.name;
    }
    
    public String getPreviousStateName() {
        return this.m_previousStateName;
    }
    
    public boolean hasEventOccurred(final String s) {
        return this.hasEventOccurred(s, -1);
    }
    
    public boolean hasEventOccurred(final String s, final int n) {
        return this.occurredEvents.contains(s, n);
    }
    
    public void clearEvent(final String s) {
        this.occurredEvents.clearEvent(s);
    }
    
    static {
        final CharacterVariableCondition.Factory factory = new CharacterVariableCondition.Factory();
        IActionCondition.registerFactory("isTrue", factory);
        IActionCondition.registerFactory("isFalse", factory);
        IActionCondition.registerFactory("compare", factory);
        IActionCondition.registerFactory("gtr", factory);
        IActionCondition.registerFactory("less", factory);
        IActionCondition.registerFactory("equals", factory);
        IActionCondition.registerFactory("lessEqual", factory);
        IActionCondition.registerFactory("gtrEqual", factory);
        IActionCondition.registerFactory("notEquals", factory);
        IActionCondition.registerFactory("eventOccurred", new EventOccurred.Factory());
        IActionCondition.registerFactory("eventNotOccurred", new EventNotOccurred.Factory());
        IActionCondition.registerFactory("lua", new LuaCall.Factory());
    }
    
    private static class s_performance
    {
        static final PerformanceProfileProbe update;
        static final PerformanceProfileProbe evaluateCurrentStateTransitions;
        static final PerformanceProfileProbe evaluateSubStateTransitions;
        
        static {
            update = new PerformanceProfileProbe("ActionContext.update");
            evaluateCurrentStateTransitions = new PerformanceProfileProbe("ActionContext.evaluateCurrentStateTransitions");
            evaluateSubStateTransitions = new PerformanceProfileProbe("ActionContext.evaluateSubStateTransitions");
        }
    }
}

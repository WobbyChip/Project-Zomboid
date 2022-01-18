// 
// Decompiled by Procyon v0.5.36
// 

package zombie.gameStates;

import java.util.Stack;
import java.util.ArrayList;

public final class GameStateMachine
{
    public boolean firstrun;
    public boolean Loop;
    public int StateIndex;
    public int LoopToState;
    public final ArrayList<GameState> States;
    public GameState current;
    private final Stack<GameState> yieldStack;
    public GameState forceNext;
    
    public GameStateMachine() {
        this.firstrun = true;
        this.Loop = true;
        this.StateIndex = 0;
        this.LoopToState = 0;
        this.States = new ArrayList<GameState>();
        this.current = null;
        this.yieldStack = new Stack<GameState>();
        this.forceNext = null;
    }
    
    public void render() {
        if (this.current != null) {
            this.current.render();
        }
    }
    
    public void update() {
        if (this.States.size() == 0) {
            if (this.forceNext == null) {
                return;
            }
            this.States.add(this.forceNext);
            this.forceNext = null;
        }
        if (this.firstrun) {
            if (this.current == null) {
                this.current = this.States.get(this.StateIndex);
            }
            System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.current.getClass().getName()));
            this.current.enter();
            this.firstrun = false;
        }
        if (this.current == null) {
            if (!this.Loop) {
                return;
            }
            this.StateIndex = this.LoopToState;
            if (this.States.isEmpty()) {
                return;
            }
            this.current = this.States.get(this.StateIndex);
            if (this.StateIndex < this.States.size()) {
                System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.current.getClass().getName()));
                this.current.enter();
            }
        }
        if (this.current != null) {
            GameState current;
            if (this.forceNext != null) {
                System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.current.getClass().getName()));
                this.current.exit();
                current = this.forceNext;
                this.forceNext = null;
            }
            else {
                final StateAction update = this.current.update();
                if (update == StateAction.Continue) {
                    System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.current.getClass().getName()));
                    this.current.exit();
                    if (!this.yieldStack.isEmpty()) {
                        this.current = this.yieldStack.pop();
                        System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.current.getClass().getName()));
                        this.current.reenter();
                        return;
                    }
                    current = this.current.redirectState();
                }
                else {
                    if (update != StateAction.Yield) {
                        return;
                    }
                    System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.current.getClass().getName()));
                    this.current.yield();
                    this.yieldStack.push(this.current);
                    current = this.current.redirectState();
                }
            }
            if (current == null) {
                ++this.StateIndex;
                if (this.StateIndex < this.States.size()) {
                    this.current = this.States.get(this.StateIndex);
                    System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.current.getClass().getName()));
                    this.current.enter();
                }
                else {
                    this.current = null;
                }
            }
            else {
                System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, current.getClass().getName()));
                current.enter();
                this.current = current;
            }
        }
    }
    
    public void forceNextState(final GameState forceNext) {
        this.forceNext = forceNext;
    }
    
    public enum StateAction
    {
        Continue, 
        Remain, 
        Yield;
        
        private static /* synthetic */ StateAction[] $values() {
            return new StateAction[] { StateAction.Continue, StateAction.Remain, StateAction.Yield };
        }
        
        static {
            $VALUES = $values();
        }
    }
}

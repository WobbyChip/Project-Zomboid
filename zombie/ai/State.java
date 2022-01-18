// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai;

import zombie.characters.MoveDeltaModifiers;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.characters.IsoGameCharacter;

public abstract class State
{
    public void enter(final IsoGameCharacter isoGameCharacter) {
    }
    
    public void execute(final IsoGameCharacter isoGameCharacter) {
    }
    
    public void exit(final IsoGameCharacter isoGameCharacter) {
    }
    
    public void animEvent(final IsoGameCharacter isoGameCharacter, final AnimEvent animEvent) {
    }
    
    public boolean isAttacking(final IsoGameCharacter isoGameCharacter) {
        return false;
    }
    
    public boolean isMoving(final IsoGameCharacter isoGameCharacter) {
        return false;
    }
    
    public boolean isDoingActionThatCanBeCancelled() {
        return false;
    }
    
    public void getDeltaModifiers(final IsoGameCharacter isoGameCharacter, final MoveDeltaModifiers moveDeltaModifiers) {
    }
    
    public boolean isIgnoreCollide(final IsoGameCharacter isoGameCharacter, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        return false;
    }
    
    public String getName() {
        return this.getClass().getSimpleName();
    }
}

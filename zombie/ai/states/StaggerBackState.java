// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import zombie.audio.parameters.ParameterZombieState;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.characters.IsoZombie;
import zombie.characters.IsoGameCharacter;
import zombie.ai.State;

public final class StaggerBackState extends State
{
    private static final StaggerBackState _instance;
    
    public static StaggerBackState instance() {
        return StaggerBackState._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.setStateEventDelayTimer(this.getMaxStaggerTime(isoGameCharacter));
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
        if (isoGameCharacter.hasAnimationPlayer()) {
            isoGameCharacter.getAnimationPlayer().setTargetToAngle();
        }
        isoGameCharacter.getVectorFromDirection(isoGameCharacter.getForwardDirection());
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
        if (isoGameCharacter.isZombie()) {
            ((IsoZombie)isoGameCharacter).setStaggerBack(false);
        }
        isoGameCharacter.setShootable(true);
    }
    
    private float getMaxStaggerTime(final IsoGameCharacter isoGameCharacter) {
        final float n = 35.0f * isoGameCharacter.getHitForce() * isoGameCharacter.getStaggerTimeMod();
        if (n < 20.0f) {
            return 20.0f;
        }
        if (n > 30.0f) {
            return 30.0f;
        }
        return n;
    }
    
    @Override
    public void animEvent(final IsoGameCharacter isoGameCharacter, final AnimEvent animEvent) {
        if (animEvent.m_EventName.equalsIgnoreCase("FallOnFront")) {
            isoGameCharacter.setFallOnFront(Boolean.parseBoolean(animEvent.m_ParameterValue));
        }
        if (animEvent.m_EventName.equalsIgnoreCase("SetState")) {
            ((IsoZombie)isoGameCharacter).parameterZombieState.setState(ParameterZombieState.State.Pushed);
        }
    }
    
    static {
        _instance = new StaggerBackState();
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.characters.IsoGameCharacter;
import zombie.ai.State;

public final class ZombieFallDownState extends State
{
    private static final ZombieFallDownState _instance;
    
    public static ZombieFallDownState instance() {
        return ZombieFallDownState._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.blockTurning = true;
        isoGameCharacter.setHitReaction("");
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.blockTurning = false;
        isoGameCharacter.setOnFloor(true);
    }
    
    @Override
    public void animEvent(final IsoGameCharacter isoGameCharacter, final AnimEvent animEvent) {
        if (animEvent.m_EventName.equalsIgnoreCase("FallOnFront")) {
            isoGameCharacter.setFallOnFront(Boolean.parseBoolean(animEvent.m_ParameterValue));
        }
        if (animEvent.m_EventName.equalsIgnoreCase("PlayDeathSound")) {
            isoGameCharacter.setDoDeathSound(false);
            isoGameCharacter.playDeadSound();
        }
    }
    
    static {
        _instance = new ZombieFallDownState();
    }
}

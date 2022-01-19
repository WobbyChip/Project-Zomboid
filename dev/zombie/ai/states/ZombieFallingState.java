// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.characters.IsoGameCharacter;
import zombie.ai.State;

public final class ZombieFallingState extends State
{
    private static final ZombieFallingState _instance;
    
    public static ZombieFallingState instance() {
        return ZombieFallingState._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.setVariable("bHardFall", false);
        isoGameCharacter.clearVariable("bLandAnimFinished");
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.clearVariable("bHardFall");
        isoGameCharacter.clearVariable("bLandAnimFinished");
    }
    
    @Override
    public void animEvent(final IsoGameCharacter isoGameCharacter, final AnimEvent animEvent) {
        if (animEvent.m_EventName.equalsIgnoreCase("FallOnFront")) {
            isoGameCharacter.setFallOnFront(Boolean.parseBoolean(animEvent.m_ParameterValue));
        }
    }
    
    static {
        _instance = new ZombieFallingState();
    }
}

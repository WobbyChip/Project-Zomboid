// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.characters.IsoZombie;
import zombie.characters.IsoGameCharacter;
import zombie.ai.State;

public final class ZombieReanimateState extends State
{
    private static final ZombieReanimateState _instance;
    
    public static ZombieReanimateState instance() {
        return ZombieReanimateState._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        ((IsoZombie)isoGameCharacter).clearVariable("ReanimateAnim");
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
        ((IsoZombie)isoGameCharacter).clearVariable("ReanimateAnim");
    }
    
    @Override
    public void animEvent(final IsoGameCharacter isoGameCharacter, final AnimEvent animEvent) {
        final IsoZombie isoZombie = (IsoZombie)isoGameCharacter;
        if (animEvent.m_EventName.equalsIgnoreCase("FallOnFront")) {
            isoGameCharacter.setFallOnFront(Boolean.parseBoolean(animEvent.m_ParameterValue));
        }
        if (animEvent.m_EventName.equalsIgnoreCase("ReanimateAnimFinishing")) {
            isoZombie.setReanimate(false);
            isoZombie.setFallOnFront(true);
        }
        if (animEvent.m_EventName.equalsIgnoreCase("FallOnFront")) {
            isoGameCharacter.setFallOnFront(Boolean.parseBoolean(animEvent.m_ParameterValue));
        }
    }
    
    static {
        _instance = new ZombieReanimateState();
    }
}

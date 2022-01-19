// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import zombie.util.StringUtils;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.characters.IsoGameCharacter;
import zombie.ai.State;

public final class IdleState extends State
{
    private static final IdleState _instance;
    
    public static IdleState instance() {
        return IdleState._instance;
    }
    
    @Override
    public void animEvent(final IsoGameCharacter isoGameCharacter, final AnimEvent animEvent) {
        if (animEvent.m_EventName.equalsIgnoreCase("PlaySound") && !StringUtils.isNullOrEmpty(animEvent.m_ParameterValue)) {
            isoGameCharacter.getSquare().playSound(animEvent.m_ParameterValue);
        }
    }
    
    static {
        _instance = new IdleState();
    }
}

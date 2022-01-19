// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoGameCharacter;
import zombie.ai.State;

public final class PlayerEmoteState extends State
{
    private static final PlayerEmoteState _instance;
    
    public static PlayerEmoteState instance() {
        return PlayerEmoteState._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.setVariable("EmotePlaying", true);
        isoGameCharacter.resetModelNextFrame();
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
        if (((IsoPlayer)isoGameCharacter).pressedCancelAction()) {
            isoGameCharacter.setVariable("EmotePlaying", false);
        }
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.clearVariable("EmotePlaying");
        isoGameCharacter.resetModelNextFrame();
    }
    
    @Override
    public void animEvent(final IsoGameCharacter isoGameCharacter, final AnimEvent animEvent) {
        if ("EmoteFinishing".equalsIgnoreCase(animEvent.m_EventName)) {
            isoGameCharacter.setVariable("EmotePlaying", false);
        }
        if ("EmoteLooped".equalsIgnoreCase(animEvent.m_EventName)) {}
    }
    
    @Override
    public boolean isDoingActionThatCanBeCancelled() {
        return true;
    }
    
    static {
        _instance = new PlayerEmoteState();
    }
}

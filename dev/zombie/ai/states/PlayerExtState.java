// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.characters.IsoGameCharacter;
import zombie.ai.State;

public final class PlayerExtState extends State
{
    private static final PlayerExtState _instance;
    
    public static PlayerExtState instance() {
        return PlayerExtState._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.setVariable("ExtPlaying", true);
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.clearVariable("ExtPlaying");
    }
    
    @Override
    public void animEvent(final IsoGameCharacter isoGameCharacter, final AnimEvent animEvent) {
        if ("ExtFinishing".equalsIgnoreCase(animEvent.m_EventName)) {
            isoGameCharacter.setVariable("ExtPlaying", false);
        }
    }
    
    static {
        _instance = new PlayerExtState();
    }
}

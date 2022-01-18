// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import zombie.characters.IsoGameCharacter;
import zombie.ai.State;

public final class PlayerFallingState extends State
{
    private static final PlayerFallingState _instance;
    
    public static PlayerFallingState instance() {
        return PlayerFallingState._instance;
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
    
    static {
        _instance = new PlayerFallingState();
    }
}

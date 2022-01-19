// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import zombie.characters.IsoGameCharacter;
import zombie.ai.State;

public final class ZombieSittingState extends State
{
    private static final ZombieSittingState _instance;
    
    public static ZombieSittingState instance() {
        return ZombieSittingState._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
    }
    
    static {
        _instance = new ZombieSittingState();
    }
}

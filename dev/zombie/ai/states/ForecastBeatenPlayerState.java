// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import zombie.GameTime;
import zombie.characters.IsoGameCharacter;
import zombie.ai.State;

public final class ForecastBeatenPlayerState extends State
{
    private static final ForecastBeatenPlayerState _instance;
    
    public static ForecastBeatenPlayerState instance() {
        return ForecastBeatenPlayerState._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.setIgnoreMovement(true);
        isoGameCharacter.setReanimateTimer(30.0f);
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
        if (isoGameCharacter.getCurrentSquare() == null) {
            return;
        }
        isoGameCharacter.setReanimateTimer(isoGameCharacter.getReanimateTimer() - GameTime.getInstance().getMultiplier() / 1.6f);
        if (isoGameCharacter.getReanimateTimer() <= 0.0f) {
            isoGameCharacter.setReanimateTimer(0.0f);
            isoGameCharacter.setVariable("bKnockedDown", true);
        }
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.setIgnoreMovement(false);
    }
    
    static {
        _instance = new ForecastBeatenPlayerState();
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.characters.IsoPlayer;
import zombie.network.GameClient;
import zombie.characters.IsoGameCharacter;
import zombie.ai.State;

public final class FitnessState extends State
{
    private static final FitnessState _instance;
    
    public static FitnessState instance() {
        return FitnessState._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.setIgnoreMovement(true);
        isoGameCharacter.setVariable("FitnessFinished", false);
        isoGameCharacter.clearVariable("ExerciseStarted");
        isoGameCharacter.clearVariable("ExerciseEnded");
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
        if (GameClient.bClient && isoGameCharacter instanceof IsoPlayer && ((IsoPlayer)isoGameCharacter).isLocalPlayer()) {
            GameClient.sendEvent((IsoPlayer)isoGameCharacter, "EventUpdateFitness");
        }
        isoGameCharacter.setIgnoreMovement(false);
        isoGameCharacter.clearVariable("FitnessFinished");
        isoGameCharacter.clearVariable("ExerciseStarted");
        isoGameCharacter.clearVariable("ExerciseHand");
        isoGameCharacter.clearVariable("FitnessStruggle");
        isoGameCharacter.setVariable("ExerciseEnded", true);
    }
    
    @Override
    public void animEvent(final IsoGameCharacter isoGameCharacter, final AnimEvent animEvent) {
    }
    
    static {
        _instance = new FitnessState();
    }
}

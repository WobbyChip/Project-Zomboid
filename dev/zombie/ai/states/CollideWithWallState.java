// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import fmod.fmod.FMODManager;
import zombie.audio.parameters.ParameterCharacterMovementSpeed;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.iso.IsoDirections;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoGameCharacter;
import zombie.ai.State;

public final class CollideWithWallState extends State
{
    private static final CollideWithWallState _instance;
    
    public static CollideWithWallState instance() {
        return CollideWithWallState._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.setIgnoreMovement(true);
        if (isoGameCharacter instanceof IsoPlayer) {
            ((IsoPlayer)isoGameCharacter).setIsAiming(false);
        }
        if (isoGameCharacter.isCollidedN()) {
            isoGameCharacter.setDir(IsoDirections.N);
        }
        if (isoGameCharacter.isCollidedS()) {
            isoGameCharacter.setDir(IsoDirections.S);
        }
        if (isoGameCharacter.isCollidedE()) {
            isoGameCharacter.setDir(IsoDirections.E);
        }
        if (isoGameCharacter.isCollidedW()) {
            isoGameCharacter.setDir(IsoDirections.W);
        }
        isoGameCharacter.setCollideType("wall");
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.setLastCollideTime(70.0f);
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.setCollideType(null);
        isoGameCharacter.setIgnoreMovement(false);
    }
    
    @Override
    public void animEvent(final IsoGameCharacter isoGameCharacter, final AnimEvent animEvent) {
        if ("PlayCollideSound".equalsIgnoreCase(animEvent.m_EventName)) {
            final long playSound = isoGameCharacter.playSound(animEvent.m_ParameterValue);
            isoGameCharacter.getEmitter().setParameterValue(playSound, ((IsoPlayer)isoGameCharacter).getParameterCharacterMovementSpeed().getParameterDescription(), (float)ParameterCharacterMovementSpeed.MovementType.Sprint.label);
            isoGameCharacter.getEmitter().setParameterValue(playSound, FMODManager.instance.getParameterDescription("TripObstacleType"), 7.0f);
        }
    }
    
    static {
        _instance = new CollideWithWallState();
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.characters.IsoZombie;
import zombie.util.Type;
import zombie.audio.parameters.ParameterCharacterMovementSpeed;
import fmod.fmod.FMODManager;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoGameCharacter;
import zombie.ai.State;

public final class BumpedState extends State
{
    private static final BumpedState _instance;
    
    public static BumpedState instance() {
        return BumpedState._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.setBumpDone(false);
        isoGameCharacter.setVariable("BumpFallAnimFinished", false);
        isoGameCharacter.getAnimationPlayer().setTargetToAngle();
        isoGameCharacter.getForwardDirection().setLengthAndDirection(isoGameCharacter.getAnimationPlayer().getAngle(), 1.0f);
        this.setCharacterBlockMovement(isoGameCharacter, true);
        if (isoGameCharacter.getVariableBoolean("BumpFall")) {
            final long playSound = isoGameCharacter.playSound("TripOverObstacle");
            final ParameterCharacterMovementSpeed parameterCharacterMovementSpeed = ((IsoPlayer)isoGameCharacter).getParameterCharacterMovementSpeed();
            isoGameCharacter.getEmitter().setParameterValue(playSound, parameterCharacterMovementSpeed.getParameterDescription(), parameterCharacterMovementSpeed.calculateCurrentValue());
            String variableString = isoGameCharacter.getVariableString("TripObstacleType");
            if (variableString == null) {
                variableString = "zombie";
            }
            isoGameCharacter.clearVariable("TripObstacleType");
            final String s = variableString;
            int n2 = 0;
            switch (s) {
                case "tree": {
                    n2 = 5;
                    break;
                }
                default: {
                    n2 = 6;
                    break;
                }
            }
            isoGameCharacter.getEmitter().setParameterValue(playSound, FMODManager.instance.getParameterDescription("TripObstacleType"), (float)n2);
        }
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
        this.setCharacterBlockMovement(isoGameCharacter, isoGameCharacter.isBumpFall() || isoGameCharacter.isBumpStaggered());
    }
    
    private void setCharacterBlockMovement(final IsoGameCharacter isoGameCharacter, final boolean blockMovement) {
        final IsoPlayer isoPlayer = Type.tryCastTo(isoGameCharacter, IsoPlayer.class);
        if (isoPlayer != null) {
            isoPlayer.setBlockMovement(blockMovement);
        }
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.clearVariable("BumpFallType");
        isoGameCharacter.clearVariable("BumpFallAnimFinished");
        isoGameCharacter.clearVariable("BumpAnimFinished");
        isoGameCharacter.setBumpType("");
        isoGameCharacter.setBumpedChr(null);
        final IsoPlayer isoPlayer = Type.tryCastTo(isoGameCharacter, IsoPlayer.class);
        if (isoPlayer != null) {
            isoPlayer.setInitiateAttack(false);
            isoPlayer.attackStarted = false;
            isoPlayer.setAttackType(null);
        }
        if (isoPlayer != null && isoGameCharacter.isBumpFall()) {
            isoGameCharacter.fallenOnKnees();
        }
        isoGameCharacter.setOnFloor(false);
        isoGameCharacter.setBumpFall(false);
        this.setCharacterBlockMovement(isoGameCharacter, false);
        if (isoGameCharacter instanceof IsoZombie && ((IsoZombie)isoGameCharacter).target != null) {
            isoGameCharacter.pathToLocation((int)((IsoZombie)isoGameCharacter).target.getX(), (int)((IsoZombie)isoGameCharacter).target.getY(), (int)((IsoZombie)isoGameCharacter).target.getZ());
        }
    }
    
    @Override
    public void animEvent(final IsoGameCharacter isoGameCharacter, final AnimEvent animEvent) {
        if (animEvent.m_EventName.equalsIgnoreCase("FallOnFront")) {
            isoGameCharacter.setFallOnFront(Boolean.parseBoolean(animEvent.m_ParameterValue));
            isoGameCharacter.setOnFloor(isoGameCharacter.isFallOnFront());
        }
        if (animEvent.m_EventName.equalsIgnoreCase("FallOnBack")) {
            isoGameCharacter.setOnFloor(Boolean.parseBoolean(animEvent.m_ParameterValue));
        }
    }
    
    static {
        _instance = new BumpedState();
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import zombie.network.GameClient;
import zombie.characters.Moodles.MoodleType;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoGameCharacter;
import zombie.ai.State;

public final class PlayerGetUpState extends State
{
    private static final PlayerGetUpState _instance;
    
    public static PlayerGetUpState instance() {
        return PlayerGetUpState._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.getStateMachineParams(this);
        isoGameCharacter.setIgnoreMovement(true);
        final IsoPlayer isoPlayer = (IsoPlayer)isoGameCharacter;
        isoPlayer.setInitiateAttack(false);
        isoPlayer.attackStarted = false;
        isoPlayer.setAttackType(null);
        isoPlayer.setBlockMovement(true);
        isoPlayer.setForceRun(false);
        isoPlayer.setForceSprint(false);
        isoGameCharacter.setVariable("getUpQuick", isoGameCharacter.getVariableBoolean("pressedRunButton"));
        if (isoGameCharacter.getMoodles().getMoodleLevel(MoodleType.Panic) > 1) {
            isoGameCharacter.setVariable("getUpQuick", true);
        }
        if (isoGameCharacter.getVariableBoolean("pressedMovement")) {
            isoGameCharacter.setVariable("getUpWalk", true);
        }
        if (GameClient.bClient) {
            isoGameCharacter.setKnockedDown(false);
        }
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.clearVariable("getUpWalk");
        if (isoGameCharacter.getVariableBoolean("sitonground")) {
            isoGameCharacter.setHideWeaponModel(false);
        }
        isoGameCharacter.setIgnoreMovement(false);
        isoGameCharacter.setFallOnFront(false);
        isoGameCharacter.setOnFloor(false);
        ((IsoPlayer)isoGameCharacter).setBlockMovement(false);
        isoGameCharacter.setSitOnGround(false);
    }
    
    static {
        _instance = new PlayerGetUpState();
    }
}

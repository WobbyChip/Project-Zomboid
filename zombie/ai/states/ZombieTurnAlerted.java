// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import java.util.HashMap;
import zombie.characters.IsoZombie;
import zombie.characters.IsoGameCharacter;
import zombie.ai.State;

public final class ZombieTurnAlerted extends State
{
    private static final ZombieTurnAlerted _instance;
    public static final Integer PARAM_TARGET_ANGLE;
    
    public static ZombieTurnAlerted instance() {
        return ZombieTurnAlerted._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.getAnimationPlayer().setTargetAngle(isoGameCharacter.getStateMachineParams(this).get(ZombieTurnAlerted.PARAM_TARGET_ANGLE));
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.pathToSound(isoGameCharacter.getPathTargetX(), isoGameCharacter.getPathTargetY(), isoGameCharacter.getPathTargetZ());
        ((IsoZombie)isoGameCharacter).alerted = false;
    }
    
    public void setParams(final IsoGameCharacter isoGameCharacter, final float f) {
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        stateMachineParams.clear();
        stateMachineParams.put(ZombieTurnAlerted.PARAM_TARGET_ANGLE, f);
    }
    
    static {
        _instance = new ZombieTurnAlerted();
        PARAM_TARGET_ANGLE = 0;
    }
}

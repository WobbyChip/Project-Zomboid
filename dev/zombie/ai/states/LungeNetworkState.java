// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import zombie.audio.parameters.ParameterZombieState;
import zombie.util.Type;
import zombie.characters.IsoPlayer;
import zombie.GameTime;
import java.util.HashMap;
import zombie.gameStates.IngameState;
import zombie.characters.IsoZombie;
import zombie.characters.IsoGameCharacter;
import org.joml.Vector3f;
import zombie.iso.Vector2;
import zombie.ai.State;

public class LungeNetworkState extends State
{
    static LungeNetworkState _instance;
    private Vector2 temp;
    private final Vector3f worldPos;
    private static final Integer PARAM_TICK_COUNT;
    
    public LungeNetworkState() {
        this.temp = new Vector2();
        this.worldPos = new Vector3f();
    }
    
    public static LungeNetworkState instance() {
        return LungeNetworkState._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        WalkTowardNetworkState.instance().enter(isoGameCharacter);
        ((IsoZombie)isoGameCharacter).LungeTimer = 180.0f;
        stateMachineParams.put(LungeNetworkState.PARAM_TICK_COUNT, IngameState.instance.numberTicks);
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
        WalkTowardNetworkState.instance().execute(isoGameCharacter);
        final IsoZombie isoZombie = (IsoZombie)isoGameCharacter;
        isoGameCharacter.setOnFloor(false);
        isoGameCharacter.setShootable(true);
        if (isoZombie.bLunger) {
            isoZombie.walkVariantUse = "ZombieWalk3";
        }
        final IsoZombie isoZombie2 = isoZombie;
        isoZombie2.LungeTimer -= GameTime.getInstance().getMultiplier() / 1.6f;
        final IsoPlayer isoPlayer = Type.tryCastTo(isoZombie.getTarget(), IsoPlayer.class);
        if (isoPlayer != null && isoPlayer.isGhostMode()) {
            isoZombie.LungeTimer = 0.0f;
        }
        if (isoZombie.LungeTimer < 0.0f) {
            isoZombie.LungeTimer = 0.0f;
        }
        if (isoZombie.LungeTimer <= 0.0f) {
            isoZombie.AllowRepathDelay = 0.0f;
        }
        if (IngameState.instance.numberTicks - isoGameCharacter.getStateMachineParams(this).get(LungeNetworkState.PARAM_TICK_COUNT) == 2L) {
            ((IsoZombie)isoGameCharacter).parameterZombieState.setState(ParameterZombieState.State.LockTarget);
        }
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
        WalkTowardNetworkState.instance().exit(isoGameCharacter);
    }
    
    @Override
    public boolean isMoving(final IsoGameCharacter isoGameCharacter) {
        return true;
    }
    
    static {
        LungeNetworkState._instance = new LungeNetworkState();
        PARAM_TICK_COUNT = 0;
    }
}

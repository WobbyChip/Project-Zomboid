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
import zombie.network.GameServer;
import zombie.characters.IsoZombie;
import zombie.characters.IsoGameCharacter;
import zombie.iso.Vector2;
import zombie.ai.State;

public final class LungeState extends State
{
    private static final LungeState _instance;
    private final Vector2 temp;
    private static final Integer PARAM_TICK_COUNT;
    
    public LungeState() {
        this.temp = new Vector2();
    }
    
    public static LungeState instance() {
        return LungeState._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        final IsoZombie isoZombie = (IsoZombie)isoGameCharacter;
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        if (System.currentTimeMillis() - isoZombie.LungeSoundTime > 5000L) {
            if (isoZombie.isFemale()) {}
            if (GameServer.bServer) {
                GameServer.sendZombieSound(IsoZombie.ZombieSound.Lunge, isoZombie);
            }
            isoZombie.LungeSoundTime = System.currentTimeMillis();
        }
        isoZombie.LungeTimer = 180.0f;
        stateMachineParams.put(LungeState.PARAM_TICK_COUNT, IngameState.instance.numberTicks);
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
        final IsoZombie isoZombie = (IsoZombie)isoGameCharacter;
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
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
        this.temp.x = isoZombie.vectorToTarget.x;
        this.temp.y = isoZombie.vectorToTarget.y;
        isoZombie.getZombieLungeSpeed();
        this.temp.normalize();
        isoZombie.setForwardDirection(this.temp);
        isoZombie.DirectionFromVector(this.temp);
        isoZombie.getVectorFromDirection(isoZombie.getForwardDirection());
        isoZombie.setForwardDirection(this.temp);
        if (!isoZombie.isTargetLocationKnown() && isoZombie.LastTargetSeenX != -1 && !isoGameCharacter.getPathFindBehavior2().isTargetLocation(isoZombie.LastTargetSeenX + 0.5f, isoZombie.LastTargetSeenY + 0.5f, (float)isoZombie.LastTargetSeenZ)) {
            isoZombie.LungeTimer = 0.0f;
            isoGameCharacter.pathToLocation(isoZombie.LastTargetSeenX, isoZombie.LastTargetSeenY, isoZombie.LastTargetSeenZ);
        }
        if (IngameState.instance.numberTicks - (long)stateMachineParams.get(LungeState.PARAM_TICK_COUNT) == 2L) {
            ((IsoZombie)isoGameCharacter).parameterZombieState.setState(ParameterZombieState.State.LockTarget);
        }
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
    }
    
    @Override
    public boolean isMoving(final IsoGameCharacter isoGameCharacter) {
        return true;
    }
    
    static {
        _instance = new LungeState();
        PARAM_TICK_COUNT = 0;
    }
}

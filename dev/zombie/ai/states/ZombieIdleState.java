// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import zombie.iso.objects.RainManager;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.core.Rand;
import zombie.audio.parameters.ParameterZombieState;
import zombie.iso.IsoMovingObject;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import java.util.HashMap;
import zombie.gameStates.IngameState;
import zombie.characters.IsoZombie;
import zombie.characters.IsoGameCharacter;
import zombie.ai.State;

public final class ZombieIdleState extends State
{
    private static final ZombieIdleState _instance;
    private static final Integer PARAM_TICK_COUNT;
    
    public static ZombieIdleState instance() {
        return ZombieIdleState._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        ((IsoZombie)isoGameCharacter).soundSourceTarget = null;
        ((IsoZombie)isoGameCharacter).soundAttract = 0.0f;
        ((IsoZombie)isoGameCharacter).movex = 0.0f;
        ((IsoZombie)isoGameCharacter).movey = 0.0f;
        isoGameCharacter.setStateEventDelayTimer(this.pickRandomWanderInterval());
        if (IngameState.instance == null) {
            stateMachineParams.put(ZombieIdleState.PARAM_TICK_COUNT, 0L);
        }
        else {
            stateMachineParams.put(ZombieIdleState.PARAM_TICK_COUNT, IngameState.instance.numberTicks);
        }
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
        final IsoZombie isoZombie = (IsoZombie)isoGameCharacter;
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        isoZombie.movex = 0.0f;
        isoZombie.movey = 0.0f;
        if (Core.bLastStand) {
            IsoGameCharacter isoGameCharacter2 = null;
            float distTo = 1000000.0f;
            for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
                if (IsoPlayer.players[i] != null && IsoPlayer.players[i].DistTo(isoGameCharacter) < distTo && !IsoPlayer.players[i].isDead()) {
                    distTo = IsoPlayer.players[i].DistTo(isoGameCharacter);
                    isoGameCharacter2 = IsoPlayer.players[i];
                }
            }
            if (isoGameCharacter2 != null) {
                isoZombie.pathToCharacter(isoGameCharacter2);
            }
            return;
        }
        if (((IsoZombie)isoGameCharacter).bCrawling) {
            isoGameCharacter.setOnFloor(true);
        }
        else {
            isoGameCharacter.setOnFloor(false);
        }
        if (IngameState.instance.numberTicks - (long)stateMachineParams.get(ZombieIdleState.PARAM_TICK_COUNT) == 2L) {
            ((IsoZombie)isoGameCharacter).parameterZombieState.setState(ParameterZombieState.State.Idle);
        }
        if (isoZombie.bIndoorZombie) {
            return;
        }
        if (isoZombie.isUseless()) {
            return;
        }
        if (isoZombie.getStateEventDelayTimer() <= 0.0f) {
            isoGameCharacter.setStateEventDelayTimer(this.pickRandomWanderInterval());
            final int n = (int)isoGameCharacter.getX() + (Rand.Next(8) - 4);
            final int n2 = (int)isoGameCharacter.getY() + (Rand.Next(8) - 4);
            if (isoGameCharacter.getCell().getGridSquare(n, n2, isoGameCharacter.getZ()) != null && isoGameCharacter.getCell().getGridSquare(n, n2, isoGameCharacter.getZ()).isFree(true)) {
                isoGameCharacter.pathToLocation(n, n2, (int)isoGameCharacter.getZ());
                isoZombie.AllowRepathDelay = 200.0f;
            }
        }
        isoZombie.networkAI.mindSync.zombieIdleUpdate();
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
    }
    
    @Override
    public void animEvent(final IsoGameCharacter isoGameCharacter, final AnimEvent animEvent) {
    }
    
    private float pickRandomWanderInterval() {
        float n = (float)Rand.Next(400, 1000);
        if (!RainManager.isRaining()) {
            n *= 1.5f;
        }
        return n;
    }
    
    static {
        _instance = new ZombieIdleState();
        PARAM_TICK_COUNT = 0;
    }
}

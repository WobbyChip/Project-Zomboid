// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import java.util.HashMap;
import zombie.characterTextures.BloodBodyPartType;
import zombie.iso.objects.IsoZombieGiblets;
import zombie.iso.IsoObject;
import zombie.audio.parameters.ParameterZombieState;
import zombie.core.Core;
import zombie.network.GameServer;
import zombie.characters.IsoPlayer;
import zombie.network.GameClient;
import zombie.iso.IsoMovingObject;
import zombie.iso.objects.IsoDeadBody;
import zombie.core.Rand;
import zombie.characters.IsoZombie;
import zombie.characters.IsoGameCharacter;
import zombie.ai.State;

public final class ZombieEatBodyState extends State
{
    private static final ZombieEatBodyState _instance;
    
    public static ZombieEatBodyState instance() {
        return ZombieEatBodyState._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        final IsoZombie e = (IsoZombie)isoGameCharacter;
        e.setStateEventDelayTimer(Rand.Next(1800.0f, 3600.0f));
        e.setVariable("onknees", Rand.Next(3) != 0);
        if (e.getEatBodyTarget() instanceof IsoDeadBody) {
            final IsoDeadBody value = (IsoDeadBody)e.eatBodyTarget;
            if (!e.isEatingOther(value)) {
                isoGameCharacter.getStateMachineParams(this).put(0, value);
                value.getEatingZombies().add(e);
            }
            if (GameClient.bClient && e.isLocal()) {
                GameClient.sendEatBody(e, e.getEatBodyTarget());
            }
        }
        else if (e.getEatBodyTarget() instanceof IsoPlayer && GameClient.bClient && e.isLocal()) {
            GameClient.sendEatBody(e, e.getEatBodyTarget());
        }
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
        final IsoZombie isoZombie = (IsoZombie)isoGameCharacter;
        final IsoMovingObject eatBodyTarget = isoZombie.getEatBodyTarget();
        if (isoZombie.getStateEventDelayTimer() <= 0.0f) {
            isoZombie.setEatBodyTarget(null, false);
        }
        else if (!GameServer.bServer && !Core.SoundDisabled && Rand.Next(Rand.AdjustForFramerate(15)) == 0) {
            isoZombie.parameterZombieState.setState(ParameterZombieState.State.Eating);
        }
        isoZombie.TimeSinceSeenFlesh = 0.0f;
        if (eatBodyTarget != null) {
            isoZombie.faceThisObject(eatBodyTarget);
        }
        if (Rand.Next(Rand.AdjustForFramerate(450)) == 0) {
            isoZombie.getCurrentSquare().getChunk().addBloodSplat(isoZombie.x + Rand.Next(-0.5f, 0.5f), isoZombie.y + Rand.Next(-0.5f, 0.5f), isoZombie.z, Rand.Next(8));
            if (Rand.Next(6) == 0) {
                new IsoZombieGiblets(IsoZombieGiblets.GibletType.B, isoZombie.getCell(), isoZombie.getX(), isoZombie.getY(), isoZombie.getZ() + 0.3f, Rand.Next(-0.2f, 0.2f) * 1.5f, Rand.Next(-0.2f, 0.2f) * 1.5f);
            }
            else {
                new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, isoZombie.getCell(), isoZombie.getX(), isoZombie.getY(), isoZombie.getZ() + 0.3f, Rand.Next(-0.2f, 0.2f) * 1.5f, Rand.Next(-0.2f, 0.2f) * 1.5f);
            }
            if (Rand.Next(4) == 0) {
                isoZombie.addBlood(null, true, false, false);
            }
        }
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
        final IsoZombie o = (IsoZombie)isoGameCharacter;
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        if (stateMachineParams.get(0) instanceof IsoDeadBody) {
            ((IsoDeadBody)stateMachineParams.get(0)).getEatingZombies().remove(o);
        }
        if (o.parameterZombieState.isState(ParameterZombieState.State.Eating)) {
            o.parameterZombieState.setState(ParameterZombieState.State.Idle);
        }
        if (GameClient.bClient && o.isLocal()) {
            GameClient.sendEatBody(o, null);
        }
    }
    
    static {
        _instance = new ZombieEatBodyState();
    }
}

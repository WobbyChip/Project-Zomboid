// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import zombie.network.ServerMap;
import zombie.vehicles.BaseVehicle;
import zombie.core.PerformanceSettings;
import zombie.network.ServerOptions;
import zombie.GameTime;
import zombie.iso.Vector2;
import zombie.iso.LosUtil;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.iso.objects.interfaces.Thumpable;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoDoor;
import zombie.util.Type;
import zombie.iso.objects.IsoBarricade;
import zombie.characters.ZombieThumpManager;
import zombie.iso.objects.IsoWindow;
import zombie.SoundManager;
import zombie.iso.IsoMovingObject;
import zombie.characters.IsoPlayer;
import zombie.network.GameServer;
import zombie.iso.IsoObject;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.network.GameClient;
import zombie.characters.IsoGameCharacter;
import zombie.ai.State;

public final class ThumpState extends State
{
    private static final ThumpState _instance;
    
    public static ThumpState instance() {
        return ThumpState._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        if (!GameClient.bClient || isoGameCharacter.isLocal()) {
            switch (Rand.Next(3)) {
                case 0: {
                    isoGameCharacter.setVariable("ThumpType", "DoorClaw");
                    break;
                }
                case 1: {
                    isoGameCharacter.setVariable("ThumpType", "Door");
                    break;
                }
                case 2: {
                    isoGameCharacter.setVariable("ThumpType", "DoorBang");
                    break;
                }
            }
        }
        if (GameClient.bClient && isoGameCharacter.isLocal()) {
            GameClient.sendThump(isoGameCharacter, isoGameCharacter.getThumpTarget());
        }
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
        final IsoZombie isoZombie = (IsoZombie)isoGameCharacter;
        final Thumpable thumpTarget = isoGameCharacter.getThumpTarget();
        if (thumpTarget instanceof IsoObject) {
            isoGameCharacter.faceThisObject((IsoObject)thumpTarget);
        }
        if ((GameServer.bServer && GameServer.bFastForward) || (!GameServer.bServer && IsoPlayer.allPlayersAsleep()) || isoGameCharacter.actionContext.hasEventOccurred("thumpframe")) {
            isoGameCharacter.actionContext.clearEvent("thumpframe");
            isoGameCharacter.setTimeThumping(isoGameCharacter.getTimeThumping() + 1);
            if (isoZombie.TimeSinceSeenFlesh < 5.0f) {
                isoGameCharacter.setTimeThumping(0);
            }
            int size = 1;
            if (isoGameCharacter.getCurrentSquare() != null) {
                size = isoGameCharacter.getCurrentSquare().getMovingObjects().size();
            }
            for (int n = 0; n < size && this.isThumpTargetValid(isoGameCharacter, isoGameCharacter.getThumpTarget()); ++n) {
                isoGameCharacter.getThumpTarget().Thump(isoGameCharacter);
            }
            final Thumpable thumpable = (isoGameCharacter.getThumpTarget() == null) ? null : isoGameCharacter.getThumpTarget().getThumpableFor(isoGameCharacter);
            if (GameServer.bServer || SoundManager.instance.isListenerInRange(isoGameCharacter.x, isoGameCharacter.y, 20.0f)) {
                if (!IsoPlayer.allPlayersAsleep()) {
                    if (thumpable instanceof IsoWindow) {
                        isoZombie.setThumpFlag((Rand.Next(3) == 0) ? 2 : 3);
                        isoZombie.setThumpCondition(thumpable.getThumpCondition());
                        if (!GameServer.bServer) {
                            ZombieThumpManager.instance.addCharacter(isoZombie);
                        }
                    }
                    else if (thumpable != null) {
                        String anObject = "ZombieThumpGeneric";
                        final IsoBarricade isoBarricade = Type.tryCastTo(thumpable, IsoBarricade.class);
                        if (isoBarricade != null && (isoBarricade.isMetal() || isoBarricade.isMetalBar())) {
                            anObject = "ZombieThumpMetal";
                        }
                        else if (thumpable instanceof IsoDoor) {
                            anObject = ((IsoDoor)thumpable).getThumpSound();
                        }
                        else if (thumpable instanceof IsoThumpable) {
                            anObject = ((IsoThumpable)thumpable).getThumpSound();
                        }
                        if ("ZombieThumpGeneric".equals(anObject)) {
                            isoZombie.setThumpFlag(1);
                        }
                        else if ("ZombieThumpWindow".equals(anObject)) {
                            isoZombie.setThumpFlag(3);
                        }
                        else if ("ZombieThumpMetal".equals(anObject)) {
                            isoZombie.setThumpFlag(4);
                        }
                        else {
                            isoZombie.setThumpFlag(1);
                        }
                        isoZombie.setThumpCondition(thumpable.getThumpCondition());
                        if (!GameServer.bServer) {
                            ZombieThumpManager.instance.addCharacter(isoZombie);
                        }
                    }
                }
            }
        }
        if (this.isThumpTargetValid(isoGameCharacter, isoGameCharacter.getThumpTarget())) {
            return;
        }
        isoGameCharacter.setThumpTarget(null);
        isoGameCharacter.setTimeThumping(0);
        if (thumpTarget instanceof IsoWindow && ((IsoWindow)thumpTarget).canClimbThrough(isoGameCharacter)) {
            isoGameCharacter.climbThroughWindow((IsoWindow)thumpTarget);
            return;
        }
        if (thumpTarget instanceof IsoDoor && (((IsoDoor)thumpTarget).open || thumpTarget.isDestroyed())) {
            final IsoDoor isoDoor = (IsoDoor)thumpTarget;
            if (this.lungeThroughDoor(isoZombie, isoDoor.getSquare(), isoDoor.getOppositeSquare())) {
                return;
            }
        }
        if (thumpTarget instanceof IsoThumpable && ((IsoThumpable)thumpTarget).isDoor && (((IsoThumpable)thumpTarget).open || thumpTarget.isDestroyed())) {
            final IsoThumpable isoThumpable = (IsoThumpable)thumpTarget;
            if (this.lungeThroughDoor(isoZombie, isoThumpable.getSquare(), isoThumpable.getInsideSquare())) {
                return;
            }
        }
        if (isoZombie.LastTargetSeenX != -1) {
            isoGameCharacter.pathToLocation(isoZombie.LastTargetSeenX, isoZombie.LastTargetSeenY, isoZombie.LastTargetSeenZ);
        }
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.setThumpTarget(null);
        ((IsoZombie)isoGameCharacter).setThumpTimer(200);
        if (GameClient.bClient && isoGameCharacter.isLocal()) {
            GameClient.sendThump(isoGameCharacter, isoGameCharacter.getThumpTarget());
        }
    }
    
    @Override
    public void animEvent(final IsoGameCharacter isoGameCharacter, final AnimEvent animEvent) {
        if (animEvent.m_EventName.equalsIgnoreCase("ThumpFrame")) {}
    }
    
    private IsoPlayer findPlayer(final int n, final int n2, final int n3, final int n4, final int n5) {
        for (int i = n3; i <= n4; ++i) {
            for (int j = n; j <= n2; ++j) {
                final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(j, i, n5);
                if (gridSquare != null) {
                    for (int k = 0; k < gridSquare.getMovingObjects().size(); ++k) {
                        final IsoMovingObject isoMovingObject = gridSquare.getMovingObjects().get(k);
                        if (isoMovingObject instanceof IsoPlayer && !((IsoPlayer)isoMovingObject).isGhostMode()) {
                            return (IsoPlayer)isoMovingObject;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    private boolean lungeThroughDoor(final IsoZombie isoZombie, final IsoGridSquare isoGridSquare, final IsoGridSquare isoGridSquare2) {
        if (isoGridSquare == null || isoGridSquare2 == null) {
            return false;
        }
        final boolean b = isoGridSquare.getY() > isoGridSquare2.getY();
        IsoGridSquare isoGridSquare3 = null;
        IsoMovingObject target = null;
        if (isoZombie.getCurrentSquare() == isoGridSquare) {
            isoGridSquare3 = isoGridSquare2;
            if (b) {
                target = this.findPlayer(isoGridSquare2.getX() - 1, isoGridSquare2.getX() + 1, isoGridSquare2.getY() - 1, isoGridSquare2.getY(), isoGridSquare2.getZ());
            }
            else {
                target = this.findPlayer(isoGridSquare2.getX() - 1, isoGridSquare2.getX(), isoGridSquare2.getY() - 1, isoGridSquare2.getY() + 1, isoGridSquare2.getZ());
            }
        }
        else if (isoZombie.getCurrentSquare() == isoGridSquare2) {
            isoGridSquare3 = isoGridSquare;
            if (b) {
                target = this.findPlayer(isoGridSquare.getX() - 1, isoGridSquare.getX() + 1, isoGridSquare.getY(), isoGridSquare.getY() + 1, isoGridSquare.getZ());
            }
            else {
                target = this.findPlayer(isoGridSquare.getX(), isoGridSquare.getX() + 1, isoGridSquare.getY() - 1, isoGridSquare.getY() + 1, isoGridSquare.getZ());
            }
        }
        if (target != null && !LosUtil.lineClearCollide(isoGridSquare3.getX(), isoGridSquare3.getY(), isoGridSquare3.getZ(), (int)target.getX(), (int)target.getY(), (int)target.getZ(), false)) {
            isoZombie.setTarget(target);
            isoZombie.vectorToTarget.x = target.getX();
            isoZombie.vectorToTarget.y = target.getY();
            final Vector2 vectorToTarget = isoZombie.vectorToTarget;
            vectorToTarget.x -= isoZombie.getX();
            final Vector2 vectorToTarget2 = isoZombie.vectorToTarget;
            vectorToTarget2.y -= isoZombie.getY();
            isoZombie.TimeSinceSeenFlesh = 0.0f;
            isoZombie.setThumpTarget(null);
            return true;
        }
        return false;
    }
    
    public static int getFastForwardDamageMultiplier() {
        final GameTime instance = GameTime.getInstance();
        if (GameServer.bServer) {
            return (int)(GameServer.bFastForward ? (ServerOptions.instance.FastForwardMultiplier.getValue() / instance.getDeltaMinutesPerDay()) : 1.0);
        }
        if (GameClient.bClient) {
            return (int)(GameClient.bFastForward ? (ServerOptions.instance.FastForwardMultiplier.getValue() / instance.getDeltaMinutesPerDay()) : 1.0);
        }
        if (IsoPlayer.allPlayersAsleep()) {
            return (int)(200.0f * (30.0f / PerformanceSettings.getLockFPS()) / 1.6f);
        }
        return (int)instance.getTrueMultiplier();
    }
    
    private boolean isThumpTargetValid(final IsoGameCharacter isoGameCharacter, final Thumpable thumpable) {
        if (thumpable == null) {
            return false;
        }
        if (thumpable.isDestroyed()) {
            return false;
        }
        final IsoObject isoObject = Type.tryCastTo(thumpable, IsoObject.class);
        if (isoObject == null) {
            return false;
        }
        if (thumpable instanceof BaseVehicle) {
            return isoObject.getMovingObjectIndex() != -1;
        }
        if (isoObject.getObjectIndex() == -1) {
            return false;
        }
        final int n = isoObject.getSquare().getX() / 10;
        final int n2 = isoObject.getSquare().getY() / 10;
        return (GameServer.bServer ? ServerMap.instance.getChunk(n, n2) : IsoWorld.instance.CurrentCell.getChunk(n, n2)) != null && thumpable.getThumpableFor(isoGameCharacter) != null;
    }
    
    static {
        _instance = new ThumpState();
    }
}

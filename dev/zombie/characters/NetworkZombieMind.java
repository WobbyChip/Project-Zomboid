// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters;

import zombie.iso.IsoMovingObject;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.vehicles.PathFindBehavior2;
import zombie.debug.DebugLog;
import zombie.network.NetworkVariables;
import zombie.network.packets.ZombiePacket;

public class NetworkZombieMind
{
    private final IsoZombie zombie;
    private byte pfbType;
    private float pfbTargetX;
    private float pfbTargetY;
    private float pfbTargetZ;
    private boolean pfbIsCanceled;
    private boolean shouldRestorePFBTarget;
    private IsoPlayer pfbTargetCharacter;
    
    public NetworkZombieMind(final IsoZombie zombie) {
        this.pfbType = 0;
        this.pfbIsCanceled = false;
        this.shouldRestorePFBTarget = false;
        this.pfbTargetCharacter = null;
        this.zombie = zombie;
    }
    
    public void set(final ZombiePacket zombiePacket) {
        final PathFindBehavior2 pathFindBehavior2 = this.zombie.getPathFindBehavior2();
        if (pathFindBehavior2.getIsCancelled() || pathFindBehavior2.isGoalNone() || pathFindBehavior2.bStopping || this.zombie.realState == null || NetworkVariables.ZombieState.Idle.equals(this.zombie.realState)) {
            zombiePacket.pfbType = 0;
        }
        else if (pathFindBehavior2.isGoalCharacter()) {
            final IsoGameCharacter targetChar = pathFindBehavior2.getTargetChar();
            if (targetChar instanceof IsoPlayer) {
                zombiePacket.pfbType = 1;
                zombiePacket.pfbTarget = targetChar.getOnlineID();
            }
            else {
                zombiePacket.pfbType = 0;
                DebugLog.Multiplayer.error((Object)"NetworkZombieMind: goal character is not set");
            }
        }
        else if (pathFindBehavior2.isGoalLocation()) {
            zombiePacket.pfbType = 2;
            zombiePacket.pfbTargetX = pathFindBehavior2.getTargetX();
            zombiePacket.pfbTargetY = pathFindBehavior2.getTargetY();
            zombiePacket.pfbTargetZ = (byte)pathFindBehavior2.getTargetZ();
        }
        else if (pathFindBehavior2.isGoalSound()) {
            zombiePacket.pfbType = 3;
            zombiePacket.pfbTargetX = pathFindBehavior2.getTargetX();
            zombiePacket.pfbTargetY = pathFindBehavior2.getTargetY();
            zombiePacket.pfbTargetZ = (byte)pathFindBehavior2.getTargetZ();
        }
    }
    
    public void parse(final ZombiePacket zombiePacket) {
        if (!(this.pfbIsCanceled = (zombiePacket.pfbType == 0))) {
            this.pfbType = zombiePacket.pfbType;
            if (this.pfbType == 1) {
                if (GameServer.bServer) {
                    this.pfbTargetCharacter = GameServer.IDToPlayerMap.get(zombiePacket.pfbTarget);
                }
                else if (GameClient.bClient) {
                    this.pfbTargetCharacter = GameClient.IDToPlayerMap.get(zombiePacket.pfbTarget);
                }
            }
            else if (this.pfbType > 1) {
                this.pfbTargetX = zombiePacket.pfbTargetX;
                this.pfbTargetY = zombiePacket.pfbTargetY;
                this.pfbTargetZ = zombiePacket.pfbTargetZ;
            }
        }
    }
    
    public void restorePFBTarget() {
        this.shouldRestorePFBTarget = true;
    }
    
    public void zombieIdleUpdate() {
        if (this.shouldRestorePFBTarget) {
            this.doRestorePFBTarget();
            this.shouldRestorePFBTarget = false;
        }
    }
    
    public void doRestorePFBTarget() {
        if (!this.pfbIsCanceled) {
            if (this.pfbType == 1 && this.pfbTargetCharacter != null) {
                this.zombie.pathToCharacter(this.pfbTargetCharacter);
                this.zombie.spotted(this.pfbTargetCharacter, true);
            }
            else if (this.pfbType == 2) {
                this.zombie.pathToLocationF(this.pfbTargetX, this.pfbTargetY, this.pfbTargetZ);
            }
            else if (this.pfbType == 3) {
                this.zombie.pathToSound((int)this.pfbTargetX, (int)this.pfbTargetY, (int)this.pfbTargetZ);
                this.zombie.alerted = false;
                this.zombie.setLastHeardSound((int)this.pfbTargetX, (int)this.pfbTargetY, (int)this.pfbTargetZ);
                this.zombie.AllowRepathDelay = 120.0f;
                this.zombie.timeSinceRespondToSound = 0.0f;
            }
        }
    }
}

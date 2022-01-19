// 
// Decompiled by Procyon v0.5.36
// 

package zombie.popman;

import zombie.iso.IsoGridSquare;
import zombie.util.Type;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoMovingObject;
import java.util.LinkedList;
import java.util.Iterator;
import java.nio.ByteBuffer;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.core.Core;
import zombie.characters.IsoPlayer;
import zombie.SystemDisabler;
import zombie.network.GameServer;
import zombie.ai.State;
import zombie.ai.states.ZombieTurnAlerted;
import zombie.ai.states.ZombieSittingState;
import zombie.ai.states.ZombieEatBodyState;
import zombie.ai.states.ZombieIdleState;
import zombie.iso.IsoUtils;
import zombie.characters.IsoZombie;
import zombie.iso.IsoWorld;
import zombie.core.raknet.UdpConnection;

public class NetworkZombieManager
{
    private static final NetworkZombieManager instance;
    private final NetworkZombieList owns;
    private static final float NospottedDistanceSquared = 16.0f;
    
    public NetworkZombieManager() {
        this.owns = new NetworkZombieList();
    }
    
    public static NetworkZombieManager getInstance() {
        return NetworkZombieManager.instance;
    }
    
    public int getAuthorizedZombieCount(final UdpConnection udpConnection) {
        return (int)IsoWorld.instance.CurrentCell.getZombieList().stream().filter(isoZombie -> isoZombie.authOwner == udpConnection).count();
    }
    
    public int getUnauthorizedZombieCount() {
        return (int)IsoWorld.instance.CurrentCell.getZombieList().stream().filter(isoZombie -> isoZombie.authOwner == null).count();
    }
    
    public static boolean canSpotted(final IsoZombie isoZombie) {
        if (isoZombie.isRemoteZombie()) {
            return false;
        }
        if (isoZombie.target != null && IsoUtils.DistanceToSquared(isoZombie.x, isoZombie.y, isoZombie.target.x, isoZombie.target.y) < 16.0f) {
            return false;
        }
        final State currentState = isoZombie.getCurrentState();
        return currentState == null || currentState == ZombieIdleState.instance() || currentState == ZombieEatBodyState.instance() || currentState == ZombieSittingState.instance() || currentState == ZombieTurnAlerted.instance();
    }
    
    public void updateAuth(final IsoZombie isoZombie) {
        if (!GameServer.bServer) {
            return;
        }
        if (System.currentTimeMillis() - isoZombie.lastChangeOwner < 2000L && isoZombie.authOwner != null) {
            return;
        }
        if (SystemDisabler.zombiesSwitchOwnershipEachUpdate && GameServer.getPlayerCount() > 1) {
            if (isoZombie.authOwner == null) {
                for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                    final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
                    if (udpConnection != null) {
                        this.moveZombie(isoZombie, udpConnection, null);
                        break;
                    }
                }
            }
            else {
                final int n = GameServer.udpEngine.connections.indexOf(isoZombie.authOwner) + 1;
                for (int j = 0; j < GameServer.udpEngine.connections.size(); ++j) {
                    final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get((j + n) % GameServer.udpEngine.connections.size());
                    if (udpConnection2 != null) {
                        this.moveZombie(isoZombie, udpConnection2, null);
                        break;
                    }
                }
            }
            return;
        }
        if (isoZombie.target instanceof IsoPlayer) {
            final UdpConnection connectionFromPlayer = GameServer.getConnectionFromPlayer((IsoPlayer)isoZombie.target);
            if (connectionFromPlayer != null && connectionFromPlayer.isFullyConnected() && !Float.isInfinite(((IsoPlayer)isoZombie.target).getRelevantAndDistance(isoZombie.x, isoZombie.y, (float)(connectionFromPlayer.ReleventRange - 2)))) {
                this.moveZombie(isoZombie, connectionFromPlayer, (IsoPlayer)isoZombie.target);
                if (Core.bDebug) {
                    DebugLog.log(DebugType.Ownership, String.format("Zombie (%d) owner (\"%s\"): zombie has target", isoZombie.getOnlineID(), ((IsoPlayer)isoZombie.target).getUsername()));
                }
                return;
            }
        }
        UdpConnection authOwner = isoZombie.authOwner;
        IsoPlayer authOwnerPlayer = isoZombie.authOwnerPlayer;
        float relevantAndDistance = Float.POSITIVE_INFINITY;
        if (authOwner != null) {
            relevantAndDistance = authOwner.getRelevantAndDistance(isoZombie.x, isoZombie.y, isoZombie.z);
        }
        for (int k = 0; k < GameServer.udpEngine.connections.size(); ++k) {
            final UdpConnection udpConnection3 = GameServer.udpEngine.connections.get(k);
            if (udpConnection3 != authOwner) {
                for (final IsoPlayer isoPlayer : udpConnection3.players) {
                    if (isoPlayer != null && isoPlayer.isAlive()) {
                        final float relevantAndDistance2 = isoPlayer.getRelevantAndDistance(isoZombie.x, isoZombie.y, (float)(udpConnection3.ReleventRange - 2));
                        if (!Float.isInfinite(relevantAndDistance2) && (authOwner == null || relevantAndDistance > relevantAndDistance2 * 1.618034f)) {
                            authOwner = udpConnection3;
                            relevantAndDistance = relevantAndDistance2;
                            authOwnerPlayer = isoPlayer;
                        }
                    }
                }
            }
        }
        if (Core.bDebug && authOwnerPlayer != null && authOwnerPlayer != isoZombie.authOwnerPlayer) {
            DebugLog.log(DebugType.Ownership, String.format("Zombie (%d) owner (\"%s\"): zombie is closer", isoZombie.getOnlineID(), authOwnerPlayer.getUsername()));
        }
        if (authOwner == null && isoZombie.isReanimatedPlayer()) {
            for (int n2 = 0; n2 < GameServer.udpEngine.connections.size(); ++n2) {
                final UdpConnection udpConnection4 = GameServer.udpEngine.connections.get(n2);
                if (udpConnection4 != authOwner) {
                    for (final IsoPlayer isoPlayer2 : udpConnection4.players) {
                        if (isoPlayer2 != null && isoPlayer2.isDead() && isoPlayer2.ReanimatedCorpse == isoZombie) {
                            authOwner = udpConnection4;
                            authOwnerPlayer = isoPlayer2;
                            if (Core.bDebug) {
                                DebugLog.log(DebugType.Ownership, String.format("Zombie (%d) owner (\"%s\"): zombie is reanimated", isoZombie.getOnlineID(), authOwnerPlayer.getUsername()));
                            }
                        }
                    }
                }
            }
        }
        if (authOwner != null && !authOwner.RelevantTo(isoZombie.x, isoZombie.y, (float)((authOwner.ReleventRange - 2) * 10))) {
            authOwner = null;
        }
        this.moveZombie(isoZombie, authOwner, authOwnerPlayer);
    }
    
    public void moveZombie(final IsoZombie isoZombie, UdpConnection connectionFromPlayer, IsoPlayer authOwnerPlayer) {
        if (isoZombie.isDead()) {
            if (isoZombie.authOwner == null && isoZombie.authOwnerPlayer == null) {
                isoZombie.becomeCorpse();
            }
            else {
                synchronized (this.owns.lock) {
                    isoZombie.authOwner = null;
                    isoZombie.authOwnerPlayer = null;
                }
                NetworkZombiePacker.getInstance().setExtraUpdate();
            }
            if (Core.bDebug) {
                DebugLog.log(DebugType.Ownership, String.format("Zombie (%d) owner (\"%s\" / null): zombie is dead", isoZombie.getOnlineID(), (authOwnerPlayer == null) ? "" : authOwnerPlayer.getUsername()));
            }
            return;
        }
        if (authOwnerPlayer != null && authOwnerPlayer.getVehicle() != null && authOwnerPlayer.getVehicle().getSpeed2D() > 2.0f && authOwnerPlayer.getVehicle().getDriver() != authOwnerPlayer && authOwnerPlayer.getVehicle().getDriver() instanceof IsoPlayer) {
            authOwnerPlayer = (IsoPlayer)authOwnerPlayer.getVehicle().getDriver();
            connectionFromPlayer = GameServer.getConnectionFromPlayer(authOwnerPlayer);
            if (Core.bDebug) {
                DebugLog.log(DebugType.Ownership, String.format("Zombie (%d) owner (\"%s\"): zombie owner is driver", isoZombie.getOnlineID(), (authOwnerPlayer == null) ? "" : authOwnerPlayer.getUsername()));
            }
        }
        if (isoZombie.authOwner == connectionFromPlayer) {
            return;
        }
        synchronized (this.owns.lock) {
            if (isoZombie.authOwner != null) {
                final NetworkZombieList.NetworkZombie networkZombie = this.owns.getNetworkZombie(isoZombie.authOwner);
                if (networkZombie != null && !networkZombie.zombies.remove(isoZombie)) {
                    DebugLog.log("moveZombie: There are no zombies in nz.zombies.");
                }
            }
            if (connectionFromPlayer != null) {
                final NetworkZombieList.NetworkZombie networkZombie2 = this.owns.getNetworkZombie(connectionFromPlayer);
                if (networkZombie2 != null) {
                    networkZombie2.zombies.add(isoZombie);
                    isoZombie.authOwner = connectionFromPlayer;
                    isoZombie.authOwnerPlayer = authOwnerPlayer;
                    connectionFromPlayer.timerSendZombie.reset(0L);
                }
            }
            else {
                isoZombie.authOwner = null;
                isoZombie.authOwnerPlayer = null;
            }
        }
        isoZombie.lastChangeOwner = System.currentTimeMillis();
        NetworkZombiePacker.getInstance().setExtraUpdate();
    }
    
    public void getZombieAuth(final UdpConnection udpConnection, final ByteBuffer byteBuffer) {
        final NetworkZombieList.NetworkZombie networkZombie = this.owns.getNetworkZombie(udpConnection);
        final int size = networkZombie.zombies.size();
        int n = 0;
        final int position = byteBuffer.position();
        byteBuffer.putShort((short)size);
        synchronized (this.owns.lock) {
            networkZombie.zombies.removeIf(isoZombie -> isoZombie.OnlineID == -1);
            for (final IsoZombie isoZombie2 : networkZombie.zombies) {
                if (isoZombie2.OnlineID != -1) {
                    byteBuffer.putShort(isoZombie2.OnlineID);
                    ++n;
                }
                else {
                    DebugLog.General.error((Object)"getZombieAuth: zombie.OnlineID == -1");
                }
            }
        }
        if (n < size) {
            final int position2 = byteBuffer.position();
            byteBuffer.position(position);
            byteBuffer.putShort((short)n);
            byteBuffer.position(position2);
        }
    }
    
    public LinkedList<IsoZombie> getZombieList(final UdpConnection udpConnection) {
        return this.owns.getNetworkZombie(udpConnection).zombies;
    }
    
    public void clearTargetAuth(final UdpConnection udpConnection, final IsoPlayer isoPlayer) {
        if (Core.bDebug) {
            DebugLog.log(DebugType.Multiplayer, invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, isoPlayer.getOnlineID()));
        }
        if (GameServer.bServer) {
            for (final IsoZombie isoZombie : IsoWorld.instance.CurrentCell.getZombieList()) {
                if (isoZombie.target == isoPlayer) {
                    isoZombie.setTarget(null);
                }
                if (isoZombie.authOwner == udpConnection) {
                    isoZombie.authOwner = null;
                    isoZombie.authOwnerPlayer = null;
                    getInstance().updateAuth(isoZombie);
                }
            }
        }
    }
    
    public static void removeZombies(final UdpConnection udpConnection) {
        final int n = (IsoChunkMap.ChunkGridWidth / 2 + 2) * 10;
        for (final IsoPlayer isoPlayer : udpConnection.players) {
            if (isoPlayer != null) {
                final int n2 = (int)isoPlayer.getX();
                final int n3 = (int)isoPlayer.getY();
                for (int j = 0; j < 8; ++j) {
                    for (int k = n3 - n; k <= n3 + n; ++k) {
                        for (int l = n2 - n; l <= n2 + n; ++l) {
                            final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(l, k, j);
                            if (gridSquare != null && !gridSquare.getMovingObjects().isEmpty()) {
                                for (int index = gridSquare.getMovingObjects().size() - 1; index >= 0; --index) {
                                    final IsoZombie isoZombie = Type.tryCastTo(gridSquare.getMovingObjects().get(index), IsoZombie.class);
                                    if (isoZombie != null) {
                                        NetworkZombiePacker.getInstance().deleteZombie(isoZombie);
                                        isoZombie.removeFromWorld();
                                        isoZombie.removeFromSquare();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void recheck(final UdpConnection udpConnection) {
        synchronized (this.owns.lock) {
            final NetworkZombieList.NetworkZombie networkZombie = this.owns.getNetworkZombie(udpConnection);
            if (networkZombie != null) {
                networkZombie.zombies.removeIf(isoZombie -> isoZombie.authOwner != udpConnection);
            }
        }
    }
    
    static {
        instance = new NetworkZombieManager();
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.popman;

import zombie.iso.IsoMovingObject;
import zombie.core.Core;
import zombie.core.network.ByteBufferWriter;
import zombie.characters.NetworkZombieAI;
import java.util.Iterator;
import zombie.network.PacketTypes;
import zombie.iso.IsoGridSquare;
import zombie.ai.states.ZombieHitReactionState;
import zombie.ai.State;
import zombie.ai.states.ZombieOnGroundState;
import zombie.characters.NetworkZombieVariables;
import zombie.iso.IsoDirections;
import zombie.VirtualZombieManager;
import zombie.debug.DebugType;
import zombie.iso.objects.IsoDeadBody;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.characters.IsoPlayer;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Collection;
import java.util.Set;
import com.google.common.collect.Sets;
import zombie.network.GameClient;
import zombie.iso.IsoWorld;
import zombie.core.utils.UpdateLimit;
import zombie.characters.IsoZombie;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.ArrayList;
import java.nio.ByteBuffer;
import zombie.network.packets.ZombiePacket;

public class NetworkZombieSimulator
{
    public static final int MAX_ZOMBIES_PER_UPDATE = 300;
    private static final NetworkZombieSimulator instance;
    private static final ZombiePacket zombiePacket;
    private final ByteBuffer bb;
    private final ArrayList<Short> unknownZombies;
    private final HashSet<Short> authoriseZombies;
    private final ArrayDeque<IsoZombie> SendQueue;
    private final ArrayDeque<IsoZombie> ExtraSendQueue;
    private HashSet<Short> authoriseZombiesCurrent;
    private HashSet<Short> authoriseZombiesLast;
    UpdateLimit ZombieSimulationReliableLimit;
    
    public NetworkZombieSimulator() {
        this.bb = ByteBuffer.allocate(1000000);
        this.unknownZombies = new ArrayList<Short>();
        this.authoriseZombies = new HashSet<Short>();
        this.SendQueue = new ArrayDeque<IsoZombie>();
        this.ExtraSendQueue = new ArrayDeque<IsoZombie>();
        this.authoriseZombiesCurrent = new HashSet<Short>();
        this.authoriseZombiesLast = new HashSet<Short>();
        this.ZombieSimulationReliableLimit = new UpdateLimit(1000L);
    }
    
    public static NetworkZombieSimulator getInstance() {
        return NetworkZombieSimulator.instance;
    }
    
    public int getAuthorizedZombieCount() {
        return (int)IsoWorld.instance.CurrentCell.getZombieList().stream().filter(isoZombie -> isoZombie.authOwner == GameClient.connection).count();
    }
    
    public int getUnauthorizedZombieCount() {
        return (int)IsoWorld.instance.CurrentCell.getZombieList().stream().filter(isoZombie -> isoZombie.authOwner == null).count();
    }
    
    public void clear() {
        final HashSet<Short> authoriseZombiesCurrent = this.authoriseZombiesCurrent;
        this.authoriseZombiesCurrent = this.authoriseZombiesLast;
        (this.authoriseZombiesLast = authoriseZombiesCurrent).removeIf(n -> GameClient.getZombie(n) == null);
        this.authoriseZombiesCurrent.clear();
    }
    
    public void addExtraUpdate(final IsoZombie isoZombie) {
        if (isoZombie.authOwner == GameClient.connection && !this.ExtraSendQueue.contains(isoZombie)) {
            this.ExtraSendQueue.add(isoZombie);
        }
    }
    
    public void add(final short s) {
        this.authoriseZombiesCurrent.add(s);
    }
    
    public void added() {
        for (final Short n : Sets.difference((Set)this.authoriseZombiesCurrent, (Set)this.authoriseZombiesLast)) {
            final IsoZombie zombie = GameClient.getZombie(n);
            if (zombie != null && zombie.OnlineID == n) {
                this.becomeLocal(zombie);
            }
            else {
                if (this.unknownZombies.contains(n)) {
                    continue;
                }
                this.unknownZombies.add(n);
            }
        }
        final UnmodifiableIterator iterator2 = Sets.difference((Set)this.authoriseZombiesLast, (Set)this.authoriseZombiesCurrent).iterator();
        while (((Iterator)iterator2).hasNext()) {
            final IsoZombie zombie2 = GameClient.getZombie(((Iterator<Short>)iterator2).next());
            if (zombie2 != null) {
                this.becomeRemote(zombie2);
            }
        }
        synchronized (this.authoriseZombies) {
            this.authoriseZombies.clear();
            this.authoriseZombies.addAll((Collection<?>)this.authoriseZombiesCurrent);
        }
    }
    
    public void becomeLocal(final IsoZombie isoZombie) {
        isoZombie.lastRemoteUpdate = 0;
        isoZombie.authOwner = GameClient.connection;
        isoZombie.authOwnerPlayer = IsoPlayer.getInstance();
        isoZombie.networkAI.setUpdateTimer(0.0f);
        isoZombie.AllowRepathDelay = 0.0f;
        isoZombie.networkAI.mindSync.restorePFBTarget();
    }
    
    public void becomeRemote(final IsoZombie isoZombie) {
        if (isoZombie.isDead() && isoZombie.authOwner == GameClient.connection) {
            isoZombie.getNetworkCharacterAI().setLocal(true);
        }
        isoZombie.lastRemoteUpdate = 0;
        isoZombie.authOwner = null;
        isoZombie.authOwnerPlayer = null;
        if (isoZombie.group != null) {
            isoZombie.group.remove(isoZombie);
        }
    }
    
    public boolean isZombieSimulated(final Short o) {
        synchronized (this.authoriseZombies) {
            return this.authoriseZombies.contains(o);
        }
    }
    
    public void receivePacket(final ByteBuffer byteBuffer) {
        if (!DebugOptions.instance.Network.Client.UpdateZombiesFromPacket.getValue()) {
            return;
        }
        for (short short1 = byteBuffer.getShort(), n = 0; n < short1; ++n) {
            this.parseZombie(byteBuffer);
        }
    }
    
    private void parseZombie(final ByteBuffer byteBuffer) {
        final ZombiePacket zombiePacket = NetworkZombieSimulator.zombiePacket;
        zombiePacket.parse(byteBuffer);
        if (zombiePacket.id == -1) {
            DebugLog.General.error(invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, zombiePacket.id));
            return;
        }
        try {
            IsoZombie realZombieAlways = (IsoZombie)GameClient.IDToZombieMap.get(zombiePacket.id);
            if (realZombieAlways == null) {
                if (IsoDeadBody.isDead(zombiePacket.id)) {
                    DebugLog.log(DebugType.Multiplayer, invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, zombiePacket.id));
                    return;
                }
                final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(zombiePacket.realX, zombiePacket.realY, zombiePacket.realZ);
                if (gridSquare != null) {
                    VirtualZombieManager.instance.choices.clear();
                    VirtualZombieManager.instance.choices.add(gridSquare);
                    realZombieAlways = VirtualZombieManager.instance.createRealZombieAlways(zombiePacket.descriptorID, IsoDirections.getRandom().index(), false);
                    DebugLog.log(DebugType.ActionSystem, invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, zombiePacket.id));
                    if (realZombieAlways != null) {
                        realZombieAlways.setFakeDead(false);
                        realZombieAlways.OnlineID = zombiePacket.id;
                        GameClient.IDToZombieMap.put(zombiePacket.id, (Object)realZombieAlways);
                        final IsoZombie isoZombie = realZombieAlways;
                        final IsoZombie isoZombie2 = realZombieAlways;
                        final IsoZombie isoZombie3 = realZombieAlways;
                        final float realX = zombiePacket.realX;
                        isoZombie3.x = realX;
                        isoZombie2.nx = realX;
                        isoZombie.lx = realX;
                        final IsoZombie isoZombie4 = realZombieAlways;
                        final IsoZombie isoZombie5 = realZombieAlways;
                        final IsoZombie isoZombie6 = realZombieAlways;
                        final float realY = zombiePacket.realY;
                        isoZombie6.y = realY;
                        isoZombie5.ny = realY;
                        isoZombie4.ly = realY;
                        final IsoZombie isoZombie7 = realZombieAlways;
                        final IsoZombie isoZombie8 = realZombieAlways;
                        final float n = zombiePacket.realZ;
                        isoZombie8.z = n;
                        isoZombie7.lz = n;
                        realZombieAlways.setForwardDirection(realZombieAlways.dir.ToVector());
                        realZombieAlways.setCurrent(gridSquare);
                        realZombieAlways.networkAI.targetX = zombiePacket.x;
                        realZombieAlways.networkAI.targetY = zombiePacket.y;
                        realZombieAlways.networkAI.targetZ = zombiePacket.z;
                        realZombieAlways.networkAI.predictionType = zombiePacket.moveType;
                        NetworkZombieVariables.setInt(realZombieAlways, (short)0, zombiePacket.realHealth);
                        NetworkZombieVariables.setInt(realZombieAlways, (short)2, zombiePacket.speedMod);
                        NetworkZombieVariables.setInt(realZombieAlways, (short)1, zombiePacket.target);
                        NetworkZombieVariables.setInt(realZombieAlways, (short)3, zombiePacket.timeSinceSeenFlesh);
                        NetworkZombieVariables.setInt(realZombieAlways, (short)4, zombiePacket.smParamTargetAngle);
                        NetworkZombieVariables.setBooleanVariables(realZombieAlways, zombiePacket.booleanVariables);
                        if (realZombieAlways.isKnockedDown()) {
                            realZombieAlways.setOnFloor(true);
                            realZombieAlways.changeState(ZombieOnGroundState.instance());
                        }
                        realZombieAlways.setWalkType(zombiePacket.walkType.toString());
                        realZombieAlways.realState = zombiePacket.realState;
                        if (realZombieAlways.isReanimatedPlayer()) {
                            realZombieAlways.getStateMachine().changeState(null, null);
                        }
                        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
                            final IsoPlayer isoPlayer = IsoPlayer.players[i];
                            if (gridSquare.isCanSee(i)) {
                                realZombieAlways.setAlphaAndTarget(i, 1.0f);
                            }
                            if (isoPlayer != null && isoPlayer.ReanimatedCorpseID == zombiePacket.id && zombiePacket.id != -1) {
                                isoPlayer.ReanimatedCorpseID = -1;
                                isoPlayer.ReanimatedCorpse = realZombieAlways;
                            }
                        }
                        realZombieAlways.networkAI.mindSync.parse(zombiePacket);
                    }
                    else {
                        DebugLog.log("Error: VirtualZombieManager can't create zombie");
                    }
                }
                if (realZombieAlways == null) {
                    return;
                }
            }
            if (getInstance().isZombieSimulated(realZombieAlways.OnlineID)) {
                realZombieAlways.authOwner = GameClient.connection;
                realZombieAlways.authOwnerPlayer = IsoPlayer.getInstance();
                return;
            }
            realZombieAlways.authOwner = null;
            realZombieAlways.authOwnerPlayer = null;
            if (!realZombieAlways.networkAI.isSetVehicleHit() || !realZombieAlways.isCurrentState(ZombieHitReactionState.instance())) {
                realZombieAlways.networkAI.parse(zombiePacket);
                realZombieAlways.networkAI.mindSync.parse(zombiePacket);
            }
            realZombieAlways.lastRemoteUpdate = 0;
            if (!IsoWorld.instance.CurrentCell.getZombieList().contains(realZombieAlways)) {
                IsoWorld.instance.CurrentCell.getZombieList().add(realZombieAlways);
            }
            if (!IsoWorld.instance.CurrentCell.getObjectList().contains(realZombieAlways)) {
                IsoWorld.instance.CurrentCell.getObjectList().add(realZombieAlways);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public boolean anyUnknownZombies() {
        return this.unknownZombies.size() > 0;
    }
    
    public void send() {
        if (this.authoriseZombies.size() == 0 && this.unknownZombies.size() == 0) {
            return;
        }
        if (this.SendQueue.isEmpty()) {
            synchronized (this.authoriseZombies) {
                final Iterator<Short> iterator = this.authoriseZombies.iterator();
                while (iterator.hasNext()) {
                    final IsoZombie zombie = GameClient.getZombie(iterator.next());
                    if (zombie != null && zombie.OnlineID != -1) {
                        this.SendQueue.add(zombie);
                    }
                }
            }
        }
        this.bb.clear();
        synchronized (ZombieCountOptimiser.zombiesForDelete) {
            final int size = ZombieCountOptimiser.zombiesForDelete.size();
            this.bb.putShort((short)size);
            for (int i = 0; i < size; ++i) {
                this.bb.putShort(ZombieCountOptimiser.zombiesForDelete.get(i).OnlineID);
            }
            ZombieCountOptimiser.zombiesForDelete.clear();
        }
        final int size2 = this.unknownZombies.size();
        this.bb.putShort((short)size2);
        for (int j = 0; j < size2; ++j) {
            this.bb.putShort(this.unknownZombies.get(j));
        }
        this.unknownZombies.clear();
        final int position = this.bb.position();
        this.bb.putShort((short)300);
        int n = 0;
        while (!this.SendQueue.isEmpty()) {
            final IsoZombie o = this.SendQueue.poll();
            this.ExtraSendQueue.remove(o);
            o.zombiePacket.set(o);
            if (o.OnlineID == -1) {
                continue;
            }
            o.zombiePacket.write(this.bb);
            final NetworkZombieAI networkAI = o.networkAI;
            final IsoZombie isoZombie = o;
            final float x = o.x;
            isoZombie.realx = x;
            networkAI.targetX = x;
            final NetworkZombieAI networkAI2 = o.networkAI;
            final IsoZombie isoZombie2 = o;
            final float y = o.y;
            isoZombie2.realy = y;
            networkAI2.targetY = y;
            final NetworkZombieAI networkAI3 = o.networkAI;
            final IsoZombie isoZombie3 = o;
            final byte b = (byte)o.z;
            isoZombie3.realz = b;
            networkAI3.targetZ = b;
            o.realdir = o.getDir();
            if (++n >= 300) {
                break;
            }
        }
        if (n < 300) {
            final int position2 = this.bb.position();
            this.bb.position(position);
            this.bb.putShort((short)n);
            this.bb.position(position2);
        }
        if (n > 0 || size2 > 0) {
            final ByteBufferWriter startPacket = GameClient.connection.startPacket();
            PacketTypes.PacketType packetType;
            if (size2 > 0 && this.ZombieSimulationReliableLimit.Check()) {
                packetType = PacketTypes.PacketType.ZombieSimulationReliable;
            }
            else {
                packetType = PacketTypes.PacketType.ZombieSimulation;
            }
            packetType.doPacket(startPacket);
            startPacket.bb.put(this.bb.array(), 0, this.bb.position());
            packetType.send(GameClient.connection);
        }
        if (!this.ExtraSendQueue.isEmpty()) {
            this.bb.clear();
            this.bb.putShort((short)0);
            this.bb.putShort((short)0);
            final int position3 = this.bb.position();
            this.bb.putShort((short)0);
            int n2 = 0;
            while (!this.ExtraSendQueue.isEmpty()) {
                final IsoZombie isoZombie4 = this.ExtraSendQueue.poll();
                isoZombie4.zombiePacket.set(isoZombie4);
                if (isoZombie4.OnlineID == -1) {
                    continue;
                }
                isoZombie4.zombiePacket.write(this.bb);
                final NetworkZombieAI networkAI4 = isoZombie4.networkAI;
                final IsoZombie isoZombie5 = isoZombie4;
                final float x2 = isoZombie4.x;
                isoZombie5.realx = x2;
                networkAI4.targetX = x2;
                final NetworkZombieAI networkAI5 = isoZombie4.networkAI;
                final IsoZombie isoZombie6 = isoZombie4;
                final float y2 = isoZombie4.y;
                isoZombie6.realy = y2;
                networkAI5.targetY = y2;
                final NetworkZombieAI networkAI6 = isoZombie4.networkAI;
                final IsoZombie isoZombie7 = isoZombie4;
                final byte b2 = (byte)isoZombie4.z;
                isoZombie7.realz = b2;
                networkAI6.targetZ = b2;
                isoZombie4.realdir = isoZombie4.getDir();
                ++n2;
            }
            final int position4 = this.bb.position();
            this.bb.position(position3);
            this.bb.putShort((short)n2);
            this.bb.position(position4);
            if (n2 > 0) {
                final ByteBufferWriter startPacket2 = GameClient.connection.startPacket();
                PacketTypes.PacketType.ZombieSimulation.doPacket(startPacket2);
                startPacket2.bb.put(this.bb.array(), 0, this.bb.position());
                PacketTypes.PacketType.ZombieSimulation.send(GameClient.connection);
            }
        }
    }
    
    public void remove(final IsoZombie isoZombie) {
        if (isoZombie == null || isoZombie.OnlineID == -1) {
            return;
        }
        GameClient.IDToZombieMap.remove(isoZombie.OnlineID);
    }
    
    public void clearTargetAuth(final IsoPlayer isoPlayer) {
        if (Core.bDebug) {
            DebugLog.log(DebugType.Multiplayer, invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, isoPlayer.getOnlineID()));
        }
        if (GameClient.bClient) {
            for (final IsoZombie isoZombie : GameClient.IDToZombieMap.valueCollection()) {
                if (isoZombie.target == isoPlayer) {
                    isoZombie.setTarget(null);
                }
            }
        }
    }
    
    static {
        instance = new NetworkZombieSimulator();
        zombiePacket = new ZombiePacket();
    }
}

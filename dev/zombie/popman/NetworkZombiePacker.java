// 
// Decompiled by Procyon v0.5.36
// 

package zombie.popman;

import zombie.iso.IsoGridSquare;
import zombie.characters.NetworkZombieVariables;
import zombie.core.network.ByteBufferWriter;
import zombie.network.PacketTypes;
import java.nio.BufferOverflowException;
import java.util.Iterator;
import java.util.Collection;
import zombie.iso.IsoWorld;
import zombie.network.MPStatistics;
import zombie.VirtualZombieManager;
import zombie.debug.DebugLog;
import zombie.network.ServerMap;
import zombie.network.GameServer;
import zombie.core.utils.UpdateLimit;
import java.nio.ByteBuffer;
import zombie.core.raknet.UdpConnection;
import zombie.network.packets.ZombiePacket;
import zombie.characters.IsoZombie;
import java.util.HashSet;
import java.util.ArrayList;

public class NetworkZombiePacker
{
    private static final NetworkZombiePacker instance;
    private final ArrayList<DeletedZombie> zombiesDeleted;
    private final ArrayList<DeletedZombie> zombiesDeletedForSending;
    private final HashSet<IsoZombie> zombiesReceived;
    private final ArrayList<IsoZombie> zombiesProcessing;
    private final NetworkZombieList zombiesRequest;
    private final ZombiePacket packet;
    private HashSet<UdpConnection> extraUpdate;
    private final ByteBuffer bb;
    UpdateLimit ZombieSimulationReliableLimit;
    
    public NetworkZombiePacker() {
        this.zombiesDeleted = new ArrayList<DeletedZombie>();
        this.zombiesDeletedForSending = new ArrayList<DeletedZombie>();
        this.zombiesReceived = new HashSet<IsoZombie>();
        this.zombiesProcessing = new ArrayList<IsoZombie>();
        this.zombiesRequest = new NetworkZombieList();
        this.packet = new ZombiePacket();
        this.extraUpdate = new HashSet<UdpConnection>();
        this.bb = ByteBuffer.allocate(1000000);
        this.ZombieSimulationReliableLimit = new UpdateLimit(5000L);
    }
    
    public static NetworkZombiePacker getInstance() {
        return NetworkZombiePacker.instance;
    }
    
    public void setExtraUpdate() {
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection e = GameServer.udpEngine.connections.get(i);
            if (e.isFullyConnected()) {
                this.extraUpdate.add(e);
            }
        }
    }
    
    public void deleteZombie(final IsoZombie isoZombie) {
        synchronized (this.zombiesDeleted) {
            this.zombiesDeleted.add(new DeletedZombie(isoZombie.OnlineID, isoZombie.x, isoZombie.y));
        }
    }
    
    public void receivePacket(final ByteBuffer byteBuffer, final UdpConnection udpConnection) {
        for (short short1 = byteBuffer.getShort(), n = 0; n < short1; ++n) {
            final IsoZombie value = ServerMap.instance.ZombieMap.get(byteBuffer.getShort());
            if (value != null) {
                this.deleteZombie(value);
                DebugLog.Multiplayer.debugln(invokedynamic(makeConcatWithConstants:(SFF)Ljava/lang/String;, value.OnlineID, value.x, value.y));
                VirtualZombieManager.instance.removeZombieFromWorld(value);
                MPStatistics.serverZombieCulled();
            }
        }
        for (short short2 = byteBuffer.getShort(), n2 = 0; n2 < short2; ++n2) {
            final IsoZombie value2 = ServerMap.instance.ZombieMap.get(byteBuffer.getShort());
            if (value2 != null) {
                this.zombiesRequest.getNetworkZombie(udpConnection).zombies.add(value2);
            }
        }
        for (short short3 = byteBuffer.getShort(), n3 = 0; n3 < short3; ++n3) {
            this.parseZombie(byteBuffer, udpConnection);
        }
    }
    
    public void parseZombie(final ByteBuffer byteBuffer, final UdpConnection e) {
        this.packet.parse(byteBuffer);
        if (this.packet.id == -1) {
            DebugLog.General.error(invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, this.packet.id));
            return;
        }
        try {
            final IsoZombie value = ServerMap.instance.ZombieMap.get(this.packet.id);
            if (value == null) {
                return;
            }
            if (value.authOwner != e) {
                NetworkZombieManager.getInstance().recheck(e);
                this.extraUpdate.add(e);
                return;
            }
            this.applyZombie(value);
            value.lastRemoteUpdate = 0;
            if (!IsoWorld.instance.CurrentCell.getZombieList().contains(value)) {
                IsoWorld.instance.CurrentCell.getZombieList().add(value);
            }
            if (!IsoWorld.instance.CurrentCell.getObjectList().contains(value)) {
                IsoWorld.instance.CurrentCell.getObjectList().add(value);
            }
            value.zombiePacket.copy(this.packet);
            value.zombiePacketUpdated = true;
            synchronized (this.zombiesReceived) {
                this.zombiesReceived.add(value);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void postupdate() {
        this.updateAuth();
        synchronized (this.zombiesReceived) {
            this.zombiesProcessing.clear();
            this.zombiesProcessing.addAll(this.zombiesReceived);
            this.zombiesReceived.clear();
        }
        synchronized (this.zombiesDeleted) {
            this.zombiesDeletedForSending.clear();
            this.zombiesDeletedForSending.addAll(this.zombiesDeleted);
            this.zombiesDeleted.clear();
        }
        for (final UdpConnection udpConnection : GameServer.udpEngine.connections) {
            if (udpConnection != null && udpConnection.isFullyConnected()) {
                this.send(udpConnection);
            }
        }
    }
    
    private void updateAuth() {
        final ArrayList<IsoZombie> zombieList = IsoWorld.instance.CurrentCell.getZombieList();
        for (int i = 0; i < zombieList.size(); ++i) {
            NetworkZombieManager.getInstance().updateAuth(zombieList.get(i));
        }
    }
    
    public int getZombieData(final UdpConnection udpConnection, final ByteBuffer byteBuffer) {
        final int position = byteBuffer.position();
        byteBuffer.putShort((short)300);
        int n = 0;
        try {
            final NetworkZombieList.NetworkZombie networkZombie = this.zombiesRequest.getNetworkZombie(udpConnection);
            while (!networkZombie.zombies.isEmpty()) {
                final IsoZombie isoZombie = networkZombie.zombies.poll();
                isoZombie.zombiePacket.set(isoZombie);
                if (isoZombie.OnlineID == -1) {
                    continue;
                }
                isoZombie.zombiePacket.write(byteBuffer);
                isoZombie.zombiePacketUpdated = false;
                if (++n >= 300) {
                    break;
                }
            }
            for (int i = 0; i < this.zombiesProcessing.size(); ++i) {
                final IsoZombie isoZombie2 = this.zombiesProcessing.get(i);
                if (isoZombie2.authOwner != null && isoZombie2.authOwner != udpConnection && udpConnection.RelevantTo(isoZombie2.x, isoZombie2.y, (float)((udpConnection.ReleventRange - 2) * 10))) {
                    if (isoZombie2.OnlineID != -1) {
                        isoZombie2.zombiePacket.write(byteBuffer);
                        isoZombie2.zombiePacketUpdated = false;
                        ++n;
                    }
                }
            }
            final int position2 = byteBuffer.position();
            byteBuffer.position(position);
            byteBuffer.putShort((short)n);
            byteBuffer.position(position2);
        }
        catch (BufferOverflowException ex) {
            ex.printStackTrace();
        }
        return n;
    }
    
    public void send(final UdpConnection udpConnection) {
        this.bb.clear();
        this.bb.put((byte)(udpConnection.isNeighborPlayer ? 1 : 0));
        final int position = this.bb.position();
        short n = 0;
        this.bb.putShort((short)0);
        for (final DeletedZombie deletedZombie : this.zombiesDeletedForSending) {
            if (udpConnection.RelevantTo(deletedZombie.x, deletedZombie.y)) {
                ++n;
                this.bb.putShort(deletedZombie.OnlineID);
            }
        }
        final int position2 = this.bb.position();
        this.bb.position(position);
        this.bb.putShort(n);
        this.bb.position(position2);
        NetworkZombieManager.getInstance().getZombieAuth(udpConnection, this.bb);
        if (this.getZombieData(udpConnection, this.bb) <= 0 && !udpConnection.timerSendZombie.check() && !this.extraUpdate.contains(udpConnection)) {
            return;
        }
        this.extraUpdate.remove(udpConnection);
        udpConnection.timerSendZombie.reset(3800L);
        final ByteBufferWriter startPacket = udpConnection.startPacket();
        PacketTypes.PacketType packetType;
        if (this.ZombieSimulationReliableLimit.Check()) {
            packetType = PacketTypes.PacketType.ZombieSimulationReliable;
        }
        else {
            packetType = PacketTypes.PacketType.ZombieSimulation;
        }
        packetType.doPacket(startPacket);
        startPacket.bb.put(this.bb.array(), 0, this.bb.position());
        packetType.send(udpConnection);
    }
    
    private void applyZombie(final IsoZombie isoZombie) {
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare((int)this.packet.x, (int)this.packet.y, this.packet.z);
        final float realX = this.packet.realX;
        isoZombie.x = realX;
        isoZombie.nx = realX;
        isoZombie.lx = realX;
        final float realY = this.packet.realY;
        isoZombie.y = realY;
        isoZombie.ny = realY;
        isoZombie.ly = realY;
        final float n = this.packet.realZ;
        isoZombie.z = n;
        isoZombie.lz = n;
        isoZombie.setForwardDirection(isoZombie.dir.ToVector());
        isoZombie.setCurrent(gridSquare);
        isoZombie.networkAI.targetX = this.packet.x;
        isoZombie.networkAI.targetY = this.packet.y;
        isoZombie.networkAI.targetZ = this.packet.z;
        isoZombie.networkAI.predictionType = this.packet.moveType;
        NetworkZombieVariables.setInt(isoZombie, (short)0, this.packet.realHealth);
        NetworkZombieVariables.setInt(isoZombie, (short)2, this.packet.speedMod);
        NetworkZombieVariables.setInt(isoZombie, (short)1, this.packet.target);
        NetworkZombieVariables.setInt(isoZombie, (short)3, this.packet.timeSinceSeenFlesh);
        NetworkZombieVariables.setInt(isoZombie, (short)4, this.packet.smParamTargetAngle);
        NetworkZombieVariables.setBooleanVariables(isoZombie, this.packet.booleanVariables);
        isoZombie.setWalkType(this.packet.walkType.toString());
        isoZombie.realState = this.packet.realState;
    }
    
    static {
        instance = new NetworkZombiePacker();
    }
    
    class DeletedZombie
    {
        short OnlineID;
        float x;
        float y;
        
        public DeletedZombie(final short onlineID, final float x, final float y) {
            this.OnlineID = onlineID;
            this.x = x;
            this.y = y;
        }
    }
}

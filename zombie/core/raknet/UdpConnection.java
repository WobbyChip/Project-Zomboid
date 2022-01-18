// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.raknet;

import java.net.InetSocketAddress;
import zombie.network.MPStatistic;
import zombie.network.GameServer;
import zombie.iso.IsoUtils;
import java.util.concurrent.locks.ReentrantLock;
import zombie.core.utils.UpdateTimer;
import zombie.core.znet.ZNetStatistics;
import gnu.trove.list.array.TShortArrayList;
import gnu.trove.set.hash.TShortHashSet;
import zombie.network.PlayerDownloadServer;
import zombie.network.ClientServerMap;
import zombie.characters.IsoPlayer;
import zombie.iso.Vector3;
import zombie.core.network.ByteBufferWriter;
import java.nio.ByteBuffer;
import java.util.concurrent.locks.Lock;

public class UdpConnection
{
    Lock bufferLock;
    private ByteBuffer bb;
    private ByteBufferWriter bbw;
    Lock bufferLockPing;
    private ByteBuffer bbPing;
    private ByteBufferWriter bbwPing;
    long connectedGUID;
    UdpEngine engine;
    public int index;
    public boolean allChatMuted;
    public String username;
    public String[] usernames;
    public byte ReleventRange;
    public String accessLevel;
    public String ip;
    public String password;
    public boolean ping;
    public Vector3[] ReleventPos;
    public short[] playerIDs;
    public IsoPlayer[] players;
    public Vector3[] connectArea;
    public int ChunkGridWidth;
    public ClientServerMap[] loadedCells;
    public PlayerDownloadServer playerDownloadServer;
    public ChecksumState checksumState;
    public long checksumTime;
    public boolean sendPulse;
    public boolean awaitingCoopApprove;
    public long steamID;
    public long ownerID;
    public String idStr;
    public boolean isCoopHost;
    public final TShortHashSet vehicles;
    public final TShortArrayList chunkObjectState;
    public final long[] packetCounts;
    public MPClientStatistic statistic;
    public ZNetStatistics netStatistics;
    public UpdateTimer timerSendZombie;
    private boolean bFullyConnected;
    public boolean isNeighborPlayer;
    
    public UdpConnection(final UdpEngine engine, final long connectedGUID, final int index) {
        this.bufferLock = new ReentrantLock();
        this.bb = ByteBuffer.allocate(1000000);
        this.bbw = new ByteBufferWriter(this.bb);
        this.bufferLockPing = new ReentrantLock();
        this.bbPing = ByteBuffer.allocate(50);
        this.bbwPing = new ByteBufferWriter(this.bbPing);
        this.connectedGUID = 0L;
        this.allChatMuted = false;
        this.usernames = new String[4];
        this.accessLevel = "";
        this.ping = false;
        this.ReleventPos = new Vector3[4];
        this.playerIDs = new short[4];
        this.players = new IsoPlayer[4];
        this.connectArea = new Vector3[4];
        this.loadedCells = new ClientServerMap[4];
        this.checksumState = ChecksumState.Init;
        this.sendPulse = false;
        this.awaitingCoopApprove = false;
        this.vehicles = new TShortHashSet();
        this.chunkObjectState = new TShortArrayList();
        this.packetCounts = new long[256];
        this.statistic = new MPClientStatistic();
        this.timerSendZombie = new UpdateTimer();
        this.bFullyConnected = false;
        this.isNeighborPlayer = false;
        this.engine = engine;
        this.connectedGUID = connectedGUID;
        this.index = index;
        this.ReleventPos[0] = new Vector3();
        for (int i = 0; i < 4; ++i) {
            this.playerIDs[i] = -1;
        }
        this.vehicles.setAutoCompactionFactor(0.0f);
    }
    
    public RakNetPeerInterface getPeer() {
        return this.engine.peer;
    }
    
    public long getConnectedGUID() {
        return this.connectedGUID;
    }
    
    public String getServerIP() {
        return this.engine.getServerIP();
    }
    
    public ByteBufferWriter startPacket() {
        this.bufferLock.lock();
        this.bb.clear();
        return this.bbw;
    }
    
    public ByteBufferWriter startPingPacket() {
        this.bufferLockPing.lock();
        this.bbPing.clear();
        return this.bbwPing;
    }
    
    public boolean RelevantTo(final float n, final float n2) {
        for (int i = 0; i < 4; ++i) {
            if (this.connectArea[i] != null) {
                final int n3 = (int)this.connectArea[i].z;
                final int n4 = (int)(this.connectArea[i].x - n3 / 2) * 10;
                final int n5 = (int)(this.connectArea[i].y - n3 / 2) * 10;
                final int n6 = n4 + n3 * 10;
                final int n7 = n5 + n3 * 10;
                if (n >= n4 && n < n6 && n2 >= n5 && n2 < n7) {
                    return true;
                }
            }
            if (this.ReleventPos[i] != null) {
                if (Math.abs(this.ReleventPos[i].x - n) <= this.ReleventRange * 10 && Math.abs(this.ReleventPos[i].y - n2) <= this.ReleventRange * 10) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public float getRelevantAndDistance(final float n, final float n2, final float n3) {
        for (int i = 0; i < 4; ++i) {
            if (this.ReleventPos[i] != null) {
                if (Math.abs(this.ReleventPos[i].x - n) <= this.ReleventRange * 10 && Math.abs(this.ReleventPos[i].y - n2) <= this.ReleventRange * 10) {
                    return IsoUtils.DistanceTo(this.ReleventPos[i].x, this.ReleventPos[i].y, n, n2);
                }
            }
        }
        return Float.POSITIVE_INFINITY;
    }
    
    public boolean RelevantToPlayerIndex(final int n, final float n2, final float n3) {
        if (this.connectArea[n] != null) {
            final int n4 = (int)this.connectArea[n].z;
            final int n5 = (int)(this.connectArea[n].x - n4 / 2) * 10;
            final int n6 = (int)(this.connectArea[n].y - n4 / 2) * 10;
            final int n7 = n5 + n4 * 10;
            final int n8 = n6 + n4 * 10;
            if (n2 >= n5 && n2 < n7 && n3 >= n6 && n3 < n8) {
                return true;
            }
        }
        return this.ReleventPos[n] != null && Math.abs(this.ReleventPos[n].x - n2) <= this.ReleventRange * 10 && Math.abs(this.ReleventPos[n].y - n3) <= this.ReleventRange * 10;
    }
    
    public boolean RelevantTo(final float n, final float n2, final float n3) {
        for (int i = 0; i < 4; ++i) {
            if (this.connectArea[i] != null) {
                final int n4 = (int)this.connectArea[i].z;
                final int n5 = (int)(this.connectArea[i].x - n4 / 2) * 10;
                final int n6 = (int)(this.connectArea[i].y - n4 / 2) * 10;
                final int n7 = n5 + n4 * 10;
                final int n8 = n6 + n4 * 10;
                if (n >= n5 && n < n7 && n2 >= n6 && n2 < n8) {
                    return true;
                }
            }
            if (this.ReleventPos[i] != null) {
                if (Math.abs(this.ReleventPos[i].x - n) <= n3 && Math.abs(this.ReleventPos[i].y - n2) <= n3) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void cancelPacket() {
        this.bufferLock.unlock();
    }
    
    public void endPacket(final int n, final int n2, final byte b) {
        if (GameServer.bServer) {
            final int position = this.bb.position();
            this.bb.position(1);
            MPStatistic.getInstance().addOutcomePacket(this.bb.getShort(), position);
            this.bb.position(position);
        }
        this.bb.flip();
        this.engine.peer.Send(this.bb, n, n2, b, this.connectedGUID, false);
        this.bufferLock.unlock();
    }
    
    public void endPacket() {
        if (GameServer.bServer) {
            final int position = this.bb.position();
            this.bb.position(1);
            MPStatistic.getInstance().addOutcomePacket(this.bb.getShort(), position);
            this.bb.position(position);
        }
        this.bb.flip();
        this.engine.peer.Send(this.bb, 1, 3, (byte)0, this.connectedGUID, false);
        this.bufferLock.unlock();
    }
    
    public void endPacketImmediate() {
        if (GameServer.bServer) {
            final int position = this.bb.position();
            this.bb.position(1);
            MPStatistic.getInstance().addOutcomePacket(this.bb.getShort(), position);
            this.bb.position(position);
        }
        this.bb.flip();
        this.engine.peer.Send(this.bb, 0, 3, (byte)0, this.connectedGUID, false);
        this.bufferLock.unlock();
    }
    
    public void endPacketUnordered() {
        if (GameServer.bServer) {
            final int position = this.bb.position();
            this.bb.position(1);
            MPStatistic.getInstance().addOutcomePacket(this.bb.getShort(), position);
            this.bb.position(position);
        }
        this.bb.flip();
        this.engine.peer.Send(this.bb, 2, 2, (byte)0, this.connectedGUID, false);
        this.bufferLock.unlock();
    }
    
    public void endPacketUnreliable() {
        this.bb.flip();
        this.engine.peer.Send(this.bb, 2, 1, (byte)0, this.connectedGUID, false);
        this.bufferLock.unlock();
    }
    
    public void endPacketSuperHighUnreliable() {
        if (GameServer.bServer) {
            final int position = this.bb.position();
            this.bb.position(1);
            MPStatistic.getInstance().addOutcomePacket(this.bb.getShort(), position);
            this.bb.position(position);
        }
        this.bb.flip();
        this.engine.peer.Send(this.bb, 0, 1, (byte)0, this.connectedGUID, false);
        this.bufferLock.unlock();
    }
    
    public void endPingPacket() {
        if (GameServer.bServer) {
            final int position = this.bb.position();
            this.bb.position(1);
            MPStatistic.getInstance().addOutcomePacket(this.bb.getShort(), position);
            this.bb.position(position);
        }
        this.bbPing.flip();
        this.engine.peer.Send(this.bbPing, 0, 1, (byte)0, this.connectedGUID, false);
        this.bufferLockPing.unlock();
    }
    
    public void close() {
    }
    
    public void disconnect(final String s) {
    }
    
    public InetSocketAddress getInetSocketAddress() {
        final String ipFromGUID = this.engine.peer.getIPFromGUID(this.connectedGUID);
        if ("UNASSIGNED_SYSTEM_ADDRESS".equals(ipFromGUID)) {
            return null;
        }
        final String[] split = ipFromGUID.replace("|", "\u00c2£").split("\u00c2£");
        return new InetSocketAddress(split[0], Integer.parseInt(split[1]));
    }
    
    public void forceDisconnect() {
        this.engine.forceDisconnect(this.getConnectedGUID());
    }
    
    public void setFullyConnected() {
        this.bFullyConnected = true;
    }
    
    public boolean isFullyConnected() {
        return this.bFullyConnected;
    }
    
    public void calcCountPlayersInRelevantPosition() {
        if (!this.isFullyConnected()) {
            return;
        }
        boolean isNeighborPlayer = false;
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            if (udpConnection.isFullyConnected()) {
                if (udpConnection != this) {
                    for (int j = 0; j < udpConnection.players.length; ++j) {
                        final IsoPlayer isoPlayer = udpConnection.players[j];
                        if (isoPlayer != null && this.RelevantTo(isoPlayer.x, isoPlayer.y, 120.0f)) {
                            isNeighborPlayer = true;
                        }
                    }
                    if (isNeighborPlayer) {
                        break;
                    }
                }
            }
        }
        this.isNeighborPlayer = isNeighborPlayer;
    }
    
    public ZNetStatistics getStatistics() {
        try {
            this.netStatistics = this.engine.peer.GetNetStatistics(this.connectedGUID);
        }
        catch (Exception ex) {
            this.netStatistics = null;
        }
        finally {
            return this.netStatistics;
        }
    }
    
    public int getAveragePing() {
        return this.engine.peer.GetAveragePing(this.connectedGUID);
    }
    
    public int getLastPing() {
        return this.engine.peer.GetLastPing(this.connectedGUID);
    }
    
    public int getLowestPing() {
        return this.engine.peer.GetLowestPing(this.connectedGUID);
    }
    
    public int getMTUSize() {
        return this.engine.peer.GetMTUSize(this.connectedGUID);
    }
    
    public enum ChecksumState
    {
        Init, 
        Different, 
        Done;
        
        private static /* synthetic */ ChecksumState[] $values() {
            return new ChecksumState[] { ChecksumState.Init, ChecksumState.Different, ChecksumState.Done };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    public class MPClientStatistic
    {
        public byte enable;
        public int diff;
        public float pingAVG;
        public int zombiesCount;
        public int zombiesLocalOwnership;
        public float zombiesDesyncAVG;
        public float zombiesDesyncMax;
        public int zombiesTeleports;
        public int remotePlayersCount;
        public float remotePlayersDesyncAVG;
        public float remotePlayersDesyncMax;
        public int remotePlayersTeleports;
        public float FPS;
        public float FPSMin;
        public float FPSAvg;
        public float FPSMax;
        public short[] FPSHistogramm;
        
        public MPClientStatistic() {
            this.enable = 0;
            this.diff = 0;
            this.pingAVG = 0.0f;
            this.zombiesCount = 0;
            this.zombiesLocalOwnership = 0;
            this.zombiesDesyncAVG = 0.0f;
            this.zombiesDesyncMax = 0.0f;
            this.zombiesTeleports = 0;
            this.remotePlayersCount = 0;
            this.remotePlayersDesyncAVG = 0.0f;
            this.remotePlayersDesyncMax = 0.0f;
            this.remotePlayersTeleports = 0;
            this.FPS = 0.0f;
            this.FPSMin = 0.0f;
            this.FPSAvg = 0.0f;
            this.FPSMax = 0.0f;
            this.FPSHistogramm = new short[32];
        }
        
        public void parse(final ByteBuffer byteBuffer) {
            this.diff = (int)(System.currentTimeMillis() - byteBuffer.getLong());
            this.pingAVG += (this.diff * 0.5f - this.pingAVG) * 0.1f;
            this.zombiesCount = byteBuffer.getInt();
            this.zombiesLocalOwnership = byteBuffer.getInt();
            this.zombiesDesyncAVG = byteBuffer.getFloat();
            this.zombiesDesyncMax = byteBuffer.getFloat();
            this.zombiesTeleports = byteBuffer.getInt();
            this.remotePlayersCount = byteBuffer.getInt();
            this.remotePlayersDesyncAVG = byteBuffer.getFloat();
            this.remotePlayersDesyncMax = byteBuffer.getFloat();
            this.remotePlayersTeleports = byteBuffer.getInt();
            this.FPS = byteBuffer.getFloat();
            this.FPSMin = byteBuffer.getFloat();
            this.FPSAvg = byteBuffer.getFloat();
            this.FPSMax = byteBuffer.getFloat();
            for (int i = 0; i < 32; ++i) {
                this.FPSHistogramm[i] = byteBuffer.getShort();
            }
        }
    }
}

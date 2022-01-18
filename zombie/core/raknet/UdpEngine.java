// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.raknet;

import java.net.UnknownHostException;
import java.net.InetAddress;
import zombie.Lua.LuaEventManager;
import zombie.core.Translator;
import zombie.core.Core;
import zombie.network.ServerWorldDatabase;
import java.util.Calendar;
import zombie.network.GameClient;
import zombie.network.PacketTypes;
import zombie.core.secure.PZcrypt;
import zombie.debug.LogSeverity;
import zombie.core.ThreadGroups;
import java.net.ConnectException;
import zombie.core.Rand;
import zombie.network.GameServer;
import zombie.core.znet.SteamUtils;
import zombie.debug.DebugLog;
import java.util.concurrent.locks.ReentrantLock;
import java.util.ArrayList;
import java.util.HashMap;
import zombie.core.network.ByteBufferWriter;
import java.nio.ByteBuffer;
import java.util.concurrent.locks.Lock;
import java.util.List;
import java.util.Map;

public class UdpEngine
{
    private int maxConnections;
    private final Map<Long, UdpConnection> connectionMap;
    public final List<UdpConnection> connections;
    protected final RakNetPeerInterface peer;
    final boolean bServer;
    Lock bufferLock;
    private ByteBuffer bb;
    private ByteBufferWriter bbw;
    public int port;
    private final Thread thread;
    private boolean bQuit;
    UdpConnection[] connectionArray;
    ByteBuffer buf;
    
    public UdpEngine(final int port, final int maxConnections, final String s, final boolean bServer) throws ConnectException {
        this.maxConnections = 0;
        this.connectionMap = new HashMap<Long, UdpConnection>();
        this.connections = new ArrayList<UdpConnection>();
        this.bufferLock = new ReentrantLock();
        this.bb = ByteBuffer.allocate(500000);
        this.bbw = new ByteBufferWriter(this.bb);
        this.port = 0;
        this.connectionArray = new UdpConnection[256];
        this.buf = ByteBuffer.allocate(1000000);
        this.port = port;
        this.peer = new RakNetPeerInterface();
        DebugLog.Network.println("Initialising RakNet...");
        this.peer.Init(SteamUtils.isSteamModeEnabled());
        this.peer.SetMaximumIncomingConnections(maxConnections);
        this.bServer = bServer;
        if (this.bServer) {
            if (GameServer.IPCommandline != null) {
                this.peer.SetServerIP(GameServer.IPCommandline);
            }
            this.peer.SetServerPort(port);
            this.peer.SetIncomingPassword(this.hashServerPassword(s));
        }
        else {
            this.peer.SetClientPort(GameServer.DEFAULT_PORT + Rand.Next(10000) + 1234);
        }
        this.peer.SetOccasionalPing(true);
        this.maxConnections = maxConnections;
        final int startup = this.peer.Startup(maxConnections);
        DebugLog.Network.println("RakNet.Startup() return code: %s (0 means success)", startup);
        if (startup != 0) {
            throw new ConnectException(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, startup));
        }
        if (bServer) {
            VoiceManager.instance.InitVMServer();
        }
        (this.thread = new Thread(ThreadGroups.Network, this::threadRun, "UdpEngine")).setDaemon(true);
        this.thread.start();
    }
    
    private void threadRun() {
        while (!this.bQuit) {
            final ByteBuffer receive = this.Receive();
            if (this.bQuit) {
                break;
            }
            try {
                this.decode(receive);
            }
            catch (Exception ex) {
                DebugLog.Network.printException(ex, "Exception thrown during decode.", LogSeverity.Error);
            }
        }
    }
    
    public void Shutdown() {
        DebugLog.log("waiting for UdpEngine thread termination");
        this.bQuit = true;
        while (this.thread.isAlive()) {
            try {
                Thread.sleep(10L);
            }
            catch (InterruptedException ex) {}
        }
        this.peer.Shutdown();
    }
    
    public void SetServerPassword(final String s) {
        if (this.peer != null) {
            this.peer.SetIncomingPassword(s);
        }
    }
    
    public String hashServerPassword(final String s) {
        return PZcrypt.hash(s, true);
    }
    
    public String getServerIP() {
        return this.peer.GetServerIP();
    }
    
    public long getClientSteamID(final long n) {
        return this.peer.GetClientSteamID(n);
    }
    
    public long getClientOwnerSteamID(final long n) {
        return this.peer.GetClientOwnerSteamID(n);
    }
    
    public ByteBufferWriter startPacket() {
        this.bufferLock.lock();
        this.bb.clear();
        return this.bbw;
    }
    
    public void endPacketBroadcast(final PacketTypes.PacketType packetType) {
        this.bb.flip();
        this.peer.Send(this.bb, packetType.PacketPriority, packetType.PacketPriority, (byte)0, -1L, true);
        this.bufferLock.unlock();
    }
    
    public void endPacketBroadcastExcept(final int n, final int n2, final UdpConnection udpConnection) {
        this.bb.flip();
        this.peer.Send(this.bb, n, n2, (byte)0, udpConnection.connectedGUID, true);
        this.bufferLock.unlock();
    }
    
    private void decode(final ByteBuffer byteBuffer) {
        final int i = byteBuffer.get() & 0xFF;
        switch (i) {
            case 21: {
                final int n = byteBuffer.get() & 0xFF;
                VoiceManager.instance.VoiceConnectClose(this.peer.getGuidOfPacket());
                this.removeConnection(n);
                if (GameClient.bClient) {
                    GameClient.instance.addDisconnectPacket(i);
                }
                break;
            }
            case 19: {
                this.addConnection(byteBuffer.get() & 0xFF, this.peer.getGuidOfPacket());
                break;
            }
            case 44: {
                VoiceManager.instance.VoiceConnectAccept(this.peer.getGuidOfPacket());
                break;
            }
            case 45: {
                VoiceManager.instance.VoiceOpenChannelReply(this.peer.getGuidOfPacket());
                break;
            }
            case 16: {
                DebugLog.Network.println("Connection Request Accepted");
                final int n2 = byteBuffer.get() & 0xFF;
                final long guidOfPacket = this.peer.getGuidOfPacket();
                final UdpConnection addConnection = this.addConnection(n2, guidOfPacket);
                VoiceManager.instance.VoiceConnectReq(guidOfPacket);
                if (GameClient.bClient && !GameClient.askPing) {
                    GameClient.startAuth = Calendar.getInstance();
                    GameClient.connection = addConnection;
                    final ByteBufferWriter startPacket = addConnection.startPacket();
                    PacketTypes.PacketType.Login.doPacket(startPacket);
                    startPacket.putUTF(GameClient.username);
                    startPacket.putUTF(PZcrypt.hash(ServerWorldDatabase.encrypt(GameClient.password)));
                    startPacket.putUTF(Core.getInstance().getVersionNumber());
                    PacketTypes.PacketType.Login.send(addConnection);
                }
                else if (GameClient.bClient && GameClient.askPing) {
                    GameClient.connection = addConnection;
                    final ByteBufferWriter startPacket2 = addConnection.startPacket();
                    PacketTypes.PacketType.Ping.doPacket(startPacket2);
                    startPacket2.putUTF(GameClient.ip);
                    PacketTypes.PacketType.Ping.send(addConnection);
                }
                break;
            }
            case 25: {
                break;
            }
            case 0:
            case 1: {
                break;
            }
            case 22: {
                final int n3 = byteBuffer.get() & 0xFF;
                if (GameServer.bServer && this.connectionArray[n3] != null) {
                    DebugLog.log(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, n3, this.connectionArray[n3].username));
                }
                else {
                    DebugLog.log("Connection Lost");
                }
                this.removeConnection(n3);
                break;
            }
            case 18: {
                DebugLog.Network.println("User Already Connected");
                if (GameClient.bClient) {
                    GameClient.instance.addDisconnectPacket(i);
                    break;
                }
                break;
            }
            case 31: {
                break;
            }
            case 32: {
                if (GameClient.bClient) {
                    GameClient.instance.addDisconnectPacket(i);
                    break;
                }
                break;
            }
            case 33: {
                break;
            }
            case 23: {
                DebugLog.Network.println("User Banned");
                if (GameClient.bClient) {
                    GameClient.instance.addDisconnectPacket(i);
                    break;
                }
                break;
            }
            case 17: {
                if (GameClient.bClient) {
                    GameClient.instance.addDisconnectPacket(i);
                    break;
                }
                break;
            }
            case 20: {
                break;
            }
            case 24: {
                if (GameClient.bClient) {
                    GameClient.instance.addDisconnectPacket(i);
                    break;
                }
                break;
            }
            case 134: {
                final short short1 = byteBuffer.getShort();
                if (GameServer.bServer) {
                    final long guidOfPacket2 = this.peer.getGuidOfPacket();
                    final UdpConnection udpConnection = this.connectionMap.get(guidOfPacket2);
                    if (udpConnection == null) {
                        DebugLog.Network.warn("GOT PACKET FROM UNKNOWN CONNECTION guid=%s packetId=%s", guidOfPacket2, short1);
                        return;
                    }
                    GameServer.addIncoming(short1, byteBuffer, udpConnection);
                }
                else {
                    GameClient.instance.addIncoming(short1, byteBuffer);
                }
                break;
            }
            default: {
                DebugLog.Network.warn("Received unknown packet: %s", i);
                break;
            }
        }
    }
    
    private void removeConnection(final int n) {
        final UdpConnection udpConnection = this.connectionArray[n];
        if (udpConnection != null) {
            this.connectionArray[n] = null;
            this.connectionMap.remove(udpConnection.getConnectedGUID());
            if (GameClient.bClient) {
                GameClient.instance.connectionLost();
            }
            if (GameServer.bServer) {
                GameServer.addDisconnect(udpConnection);
            }
        }
    }
    
    private UdpConnection addConnection(final int n, final long l) {
        final UdpConnection udpConnection = new UdpConnection(this, l, n);
        this.connectionMap.put(l, udpConnection);
        this.connectionArray[n] = udpConnection;
        if (GameServer.bServer) {
            GameServer.addConnection(udpConnection);
        }
        return udpConnection;
    }
    
    public ByteBuffer Receive() {
        boolean receive;
        do {
            receive = this.peer.Receive(this.buf);
            if (receive) {
                return this.buf;
            }
            try {
                Thread.sleep(1L);
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        } while (!this.bQuit && !receive);
        return this.buf;
    }
    
    public UdpConnection getActiveConnection(final long n) {
        if (!this.connectionMap.containsKey(n)) {
            return null;
        }
        return this.connectionMap.get(n);
    }
    
    public void Connect(final String host, final int n, final String s) {
        if (n == 0 && SteamUtils.isSteamModeEnabled()) {
            long convertStringToSteamID;
            try {
                convertStringToSteamID = SteamUtils.convertStringToSteamID(host);
            }
            catch (NumberFormatException ex) {
                ex.printStackTrace();
                LuaEventManager.triggerEvent("OnConnectFailed", Translator.getText("UI_OnConnectFailed_UnknownHost"));
                return;
            }
            this.peer.ConnectToSteamServer(convertStringToSteamID, this.hashServerPassword(s));
        }
        else {
            String hostAddress;
            try {
                hostAddress = InetAddress.getByName(host).getHostAddress();
            }
            catch (UnknownHostException ex2) {
                ex2.printStackTrace();
                LuaEventManager.triggerEvent("OnConnectFailed", Translator.getText("UI_OnConnectFailed_UnknownHost"));
                return;
            }
            this.peer.Connect(hostAddress, n, this.hashServerPassword(s));
        }
    }
    
    public void Connect(final long n, final String s) {
        this.peer.ConnectToSteamServer(n, s);
    }
    
    public void forceDisconnect(final long n) {
        this.peer.disconnect(n);
        this.removeConnection(n);
    }
    
    private void removeConnection(final long l) {
        final UdpConnection udpConnection = this.connectionMap.remove(l);
        if (udpConnection != null) {
            this.removeConnection(udpConnection.index);
        }
    }
    
    public RakNetPeerInterface getPeer() {
        return this.peer;
    }
    
    public int getMaxConnections() {
        return this.maxConnections;
    }
}

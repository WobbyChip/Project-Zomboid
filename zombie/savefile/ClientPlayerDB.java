// 
// Decompiled by Procyon v0.5.36
// 

package zombie.savefile;

import zombie.iso.IsoChunkMap;
import zombie.iso.IsoCell;
import java.nio.BufferOverflowException;
import zombie.debug.DebugLog;
import java.io.IOException;
import zombie.core.network.ByteBufferWriter;
import zombie.network.PacketTypes;
import zombie.network.GameClient;
import zombie.core.raknet.UdpConnection;
import zombie.core.logger.ExceptionLogger;
import zombie.iso.IsoWorld;
import java.util.ArrayList;
import zombie.characters.IsoPlayer;
import java.nio.ByteBuffer;
import zombie.core.utils.UpdateLimit;

public final class ClientPlayerDB
{
    private static ClientPlayerDB instance;
    private static boolean allow;
    private NetworkCharacterProfile networkProfile;
    private UpdateLimit saveToDBPeriod4Network;
    private static ByteBuffer SliceBuffer4NetworkPlayer;
    private boolean forceSavePlayers;
    public boolean canSavePlayers;
    private int serverPlayerIndex;
    
    public ClientPlayerDB() {
        this.networkProfile = null;
        this.saveToDBPeriod4Network = new UpdateLimit(30000L);
        this.canSavePlayers = false;
        this.serverPlayerIndex = 1;
    }
    
    public static void setAllow(final boolean allow) {
        ClientPlayerDB.allow = allow;
    }
    
    public static boolean isAllow() {
        return ClientPlayerDB.allow;
    }
    
    public static synchronized ClientPlayerDB getInstance() {
        if (ClientPlayerDB.instance == null && ClientPlayerDB.allow) {
            ClientPlayerDB.instance = new ClientPlayerDB();
        }
        return ClientPlayerDB.instance;
    }
    
    public static boolean isAvailable() {
        return ClientPlayerDB.instance != null;
    }
    
    public void updateMain() {
        this.saveNetworkPlayersToDB();
    }
    
    public void close() {
        ClientPlayerDB.instance = null;
        ClientPlayerDB.allow = false;
    }
    
    private void saveNetworkPlayersToDB() {
        if (this.canSavePlayers && (this.forceSavePlayers || this.saveToDBPeriod4Network.Check())) {
            this.forceSavePlayers = false;
            for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
                final IsoPlayer isoPlayer = IsoPlayer.players[i];
                if (isoPlayer != null) {
                    this.clientSendNetworkPlayerInt(isoPlayer);
                }
            }
        }
    }
    
    public ArrayList<IsoPlayer> getAllNetworkPlayers() {
        final ArrayList<IsoPlayer> list = new ArrayList<IsoPlayer>();
        for (int i = 1; i < this.networkProfile.playerCount; ++i) {
            final byte[] clientLoadNetworkPlayerData = this.getClientLoadNetworkPlayerData(i + 1);
            final ByteBuffer allocate = ByteBuffer.allocate(clientLoadNetworkPlayerData.length);
            allocate.rewind();
            allocate.put(clientLoadNetworkPlayerData);
            allocate.rewind();
            try {
                final IsoPlayer e = new IsoPlayer(IsoWorld.instance.CurrentCell);
                e.serverPlayerIndex = i + 1;
                e.load(allocate, this.networkProfile.worldVersion);
                list.add(e);
            }
            catch (Exception ex) {
                ExceptionLogger.logException(ex);
            }
        }
        return list;
    }
    
    public void clientLoadNetworkCharacter(final ByteBuffer byteBuffer, final UdpConnection udpConnection) {
        final boolean b = byteBuffer.get() == 1;
        final int int1 = byteBuffer.getInt();
        if (b) {
            final float float1 = byteBuffer.getFloat();
            final float float2 = byteBuffer.getFloat();
            final float float3 = byteBuffer.getFloat();
            final int int2 = byteBuffer.getInt();
            final boolean b2 = byteBuffer.get() == 1;
            final byte[] character1 = new byte[byteBuffer.getInt()];
            byteBuffer.get(character1);
            if (this.networkProfile != null) {
                final NetworkCharacterProfile networkProfile = this.networkProfile;
                ++networkProfile.playerCount;
                switch (this.networkProfile.playerCount) {
                    case 2: {
                        this.networkProfile.character2 = character1;
                        this.networkProfile.x[1] = float1;
                        this.networkProfile.y[1] = float2;
                        this.networkProfile.z[1] = float3;
                        this.networkProfile.isDead[1] = b2;
                        break;
                    }
                    case 3: {
                        this.networkProfile.character3 = character1;
                        this.networkProfile.x[2] = float1;
                        this.networkProfile.y[2] = float2;
                        this.networkProfile.z[2] = float3;
                        this.networkProfile.isDead[2] = b2;
                        break;
                    }
                    case 4: {
                        this.networkProfile.character4 = character1;
                        this.networkProfile.x[3] = float1;
                        this.networkProfile.y[3] = float2;
                        this.networkProfile.z[3] = float3;
                        this.networkProfile.isDead[3] = b2;
                        break;
                    }
                }
            }
            else {
                this.networkProfile = new NetworkCharacterProfile();
                this.networkProfile.playerCount = 1;
                this.networkProfile.username = GameClient.username;
                this.networkProfile.server = GameClient.ip;
                this.networkProfile.character1 = character1;
                this.networkProfile.worldVersion = int2;
                this.networkProfile.x[0] = float1;
                this.networkProfile.y[0] = float2;
                this.networkProfile.z[0] = float3;
                this.networkProfile.isDead[0] = b2;
            }
            final ByteBufferWriter startPacket = GameClient.connection.startPacket();
            PacketTypes.PacketType.LoadPlayerProfile.doPacket(startPacket);
            startPacket.putByte((byte)(int1 + 1));
            PacketTypes.PacketType.LoadPlayerProfile.send(GameClient.connection);
        }
        else if (this.networkProfile != null) {
            this.networkProfile.isLoaded = true;
            this.serverPlayerIndex = this.networkProfile.playerCount;
        }
        else {
            this.networkProfile = new NetworkCharacterProfile();
            this.networkProfile.isLoaded = true;
            this.networkProfile.playerCount = 0;
            this.networkProfile.username = GameClient.username;
            this.networkProfile.server = GameClient.ip;
            this.networkProfile.character1 = null;
            this.networkProfile.worldVersion = IsoWorld.getWorldVersion();
        }
    }
    
    private boolean isClientLoadNetworkCharacterCompleted() {
        return this.networkProfile != null && this.networkProfile.isLoaded;
    }
    
    public void clientSendNetworkPlayerInt(final IsoPlayer isoPlayer) {
        if (GameClient.connection == null) {
            return;
        }
        try {
            final ByteBufferWriter startPacket = GameClient.connection.startPacket();
            PacketTypes.PacketType.SendPlayerProfile.doPacket(startPacket);
            startPacket.putByte((byte)(isoPlayer.serverPlayerIndex - 1));
            startPacket.putFloat(isoPlayer.x);
            startPacket.putFloat(isoPlayer.y);
            startPacket.putFloat(isoPlayer.z);
            startPacket.putByte((byte)(isoPlayer.isDead() ? 1 : 0));
            ClientPlayerDB.SliceBuffer4NetworkPlayer.rewind();
            isoPlayer.save(ClientPlayerDB.SliceBuffer4NetworkPlayer);
            final byte[] array = new byte[ClientPlayerDB.SliceBuffer4NetworkPlayer.position()];
            ClientPlayerDB.SliceBuffer4NetworkPlayer.rewind();
            ClientPlayerDB.SliceBuffer4NetworkPlayer.get(array);
            startPacket.putInt(IsoWorld.getWorldVersion());
            startPacket.putInt(ClientPlayerDB.SliceBuffer4NetworkPlayer.position());
            startPacket.bb.put(array);
            PacketTypes.PacketType.SendPlayerProfile.send(GameClient.connection);
        }
        catch (IOException ex) {
            GameClient.connection.cancelPacket();
            ExceptionLogger.logException(ex);
        }
        catch (BufferOverflowException ex2) {
            GameClient.connection.cancelPacket();
            final int capacity = ClientPlayerDB.SliceBuffer4NetworkPlayer.capacity();
            if (capacity > 2097152) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, isoPlayer.getUsername()));
                ExceptionLogger.logException(ex2);
                return;
            }
            ClientPlayerDB.SliceBuffer4NetworkPlayer = ByteBuffer.allocate(capacity * 2);
            this.clientSendNetworkPlayerInt(isoPlayer);
        }
    }
    
    public boolean isAliveMainNetworkPlayer() {
        return !this.networkProfile.isDead[0];
    }
    
    public boolean clientLoadNetworkPlayer() {
        if (this.networkProfile != null && this.networkProfile.isLoaded && this.networkProfile.username.equals(GameClient.username) && this.networkProfile.server.equals(GameClient.ip)) {
            return this.networkProfile.playerCount > 0;
        }
        if (GameClient.connection == null) {
            return false;
        }
        if (this.networkProfile != null) {
            this.networkProfile = null;
        }
        final ByteBufferWriter startPacket = GameClient.connection.startPacket();
        PacketTypes.PacketType.LoadPlayerProfile.doPacket(startPacket);
        startPacket.putByte((byte)0);
        PacketTypes.PacketType.LoadPlayerProfile.send(GameClient.connection);
        int n = 200;
        while (n-- > 0) {
            if (this.isClientLoadNetworkCharacterCompleted()) {
                return this.networkProfile.playerCount > 0;
            }
            try {
                Thread.sleep(50L);
            }
            catch (InterruptedException ex) {
                ExceptionLogger.logException(ex);
            }
        }
        return false;
    }
    
    public byte[] getClientLoadNetworkPlayerData(final int n) {
        if (this.networkProfile != null && this.networkProfile.isLoaded && this.networkProfile.username.equals(GameClient.username) && this.networkProfile.server.equals(GameClient.ip)) {
            switch (n) {
                case 1: {
                    return this.networkProfile.character1;
                }
                case 2: {
                    return this.networkProfile.character2;
                }
                case 3: {
                    return this.networkProfile.character3;
                }
                case 4: {
                    return this.networkProfile.character4;
                }
                default: {
                    return null;
                }
            }
        }
        else {
            if (!this.clientLoadNetworkPlayer()) {
                return null;
            }
            switch (n) {
                case 1: {
                    return this.networkProfile.character1;
                }
                case 2: {
                    return this.networkProfile.character2;
                }
                case 3: {
                    return this.networkProfile.character3;
                }
                case 4: {
                    return this.networkProfile.character4;
                }
                default: {
                    return null;
                }
            }
        }
    }
    
    public boolean loadNetworkPlayer(final int serverPlayerIndex) {
        try {
            final byte[] clientLoadNetworkPlayerData = this.getClientLoadNetworkPlayerData(serverPlayerIndex);
            if (clientLoadNetworkPlayerData != null) {
                final ByteBuffer allocate = ByteBuffer.allocate(clientLoadNetworkPlayerData.length);
                allocate.rewind();
                allocate.put(clientLoadNetworkPlayerData);
                allocate.rewind();
                if (serverPlayerIndex == 1) {
                    if (IsoPlayer.getInstance() == null) {
                        IsoPlayer.setInstance(new IsoPlayer(IsoCell.getInstance()));
                        IsoPlayer.players[0] = IsoPlayer.getInstance();
                    }
                    IsoPlayer.getInstance().serverPlayerIndex = 1;
                    IsoPlayer.getInstance().load(allocate, this.networkProfile.worldVersion);
                }
                else {
                    IsoPlayer.players[serverPlayerIndex - 1] = new IsoPlayer(IsoCell.getInstance());
                    IsoPlayer.getInstance().serverPlayerIndex = serverPlayerIndex;
                    IsoPlayer.getInstance().load(allocate, this.networkProfile.worldVersion);
                }
                return true;
            }
        }
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
        }
        return false;
    }
    
    public boolean loadNetworkPlayerInfo(final int n) {
        if (this.networkProfile != null && this.networkProfile.isLoaded && this.networkProfile.username.equals(GameClient.username) && this.networkProfile.server.equals(GameClient.ip) && n >= 1 && n <= 4 && n <= this.networkProfile.playerCount) {
            final int n2 = (int)(this.networkProfile.x[n - 1] / 10.0f) + IsoWorld.saveoffsetx * 30;
            final int n3 = (int)(this.networkProfile.y[n - 1] / 10.0f) + IsoWorld.saveoffsety * 30;
            IsoChunkMap.WorldXA = (int)this.networkProfile.x[n - 1];
            IsoChunkMap.WorldYA = (int)this.networkProfile.y[n - 1];
            IsoChunkMap.WorldZA = (int)this.networkProfile.z[n - 1];
            IsoChunkMap.WorldXA += 300 * IsoWorld.saveoffsetx;
            IsoChunkMap.WorldYA += 300 * IsoWorld.saveoffsety;
            IsoChunkMap.SWorldX[0] = n2;
            IsoChunkMap.SWorldY[0] = n3;
            final int[] sWorldX = IsoChunkMap.SWorldX;
            final int n4 = 0;
            sWorldX[n4] += 30 * IsoWorld.saveoffsetx;
            final int[] sWorldY = IsoChunkMap.SWorldY;
            final int n5 = 0;
            sWorldY[n5] += 30 * IsoWorld.saveoffsety;
            return true;
        }
        return false;
    }
    
    public int getNextServerPlayerIndex() {
        return ++this.serverPlayerIndex;
    }
    
    static {
        ClientPlayerDB.instance = null;
        ClientPlayerDB.allow = false;
        ClientPlayerDB.SliceBuffer4NetworkPlayer = ByteBuffer.allocate(65536);
    }
    
    private final class NetworkCharacterProfile
    {
        boolean isLoaded;
        byte[] character1;
        byte[] character2;
        byte[] character3;
        byte[] character4;
        String username;
        String server;
        int playerCount;
        int worldVersion;
        float[] x;
        float[] y;
        float[] z;
        boolean[] isDead;
        
        public NetworkCharacterProfile() {
            this.isLoaded = false;
            this.playerCount = 0;
            this.x = new float[4];
            this.y = new float[4];
            this.z = new float[4];
            this.isDead = new boolean[4];
        }
    }
}

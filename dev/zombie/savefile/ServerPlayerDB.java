// 
// Decompiled by Procyon v0.5.36
// 

package zombie.savefile;

import zombie.core.network.ByteBufferWriter;
import java.sql.SQLException;
import zombie.network.PacketTypes;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import zombie.core.logger.ExceptionLogger;
import zombie.characters.IsoPlayer;
import zombie.iso.IsoCell;
import zombie.core.raknet.UdpConnection;
import java.nio.ByteBuffer;
import zombie.core.Core;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.sql.Connection;

public final class ServerPlayerDB
{
    private static ServerPlayerDB instance;
    private static boolean allow;
    private Connection conn;
    private ConcurrentLinkedQueue<NetworkCharacterData> CharactersToSave;
    
    public static void setAllow(final boolean allow) {
        ServerPlayerDB.allow = allow;
    }
    
    public static boolean isAllow() {
        return ServerPlayerDB.allow;
    }
    
    public static synchronized ServerPlayerDB getInstance() {
        if (ServerPlayerDB.instance == null && ServerPlayerDB.allow) {
            ServerPlayerDB.instance = new ServerPlayerDB();
        }
        return ServerPlayerDB.instance;
    }
    
    public static boolean isAvailable() {
        return ServerPlayerDB.instance != null;
    }
    
    public ServerPlayerDB() {
        this.conn = null;
        if (Core.getInstance().isNoSave()) {
            return;
        }
        this.create();
    }
    
    public void close() {
        ServerPlayerDB.instance = null;
        ServerPlayerDB.allow = false;
    }
    
    private void create() {
        this.conn = PlayerDBHelper.create();
        this.CharactersToSave = new ConcurrentLinkedQueue<NetworkCharacterData>();
    }
    
    public void process() {
        if (!this.CharactersToSave.isEmpty()) {
            for (NetworkCharacterData networkCharacterData = this.CharactersToSave.poll(); networkCharacterData != null; networkCharacterData = this.CharactersToSave.poll()) {
                this.serverUpdateNetworkCharacterInt(networkCharacterData);
            }
        }
    }
    
    public void serverUpdateNetworkCharacter(final ByteBuffer byteBuffer, final UdpConnection udpConnection) {
        this.CharactersToSave.add(new NetworkCharacterData(byteBuffer, udpConnection));
    }
    
    private void serverUpdateNetworkCharacterInt(final NetworkCharacterData networkCharacterData) {
        if (networkCharacterData.playerIndex < 0 || networkCharacterData.playerIndex >= 4) {
            return;
        }
        if (this.conn == null) {
            return;
        }
        final String s = "SELECT id FROM networkPlayers WHERE username=? AND world=? AND playerIndex=?";
        final String s2 = "INSERT INTO networkPlayers(world,username,playerIndex,name,x,y,z,worldversion,isDead,data) VALUES(?,?,?,?,?,?,?,?,?,?)";
        final String s3 = "UPDATE networkPlayers SET x=?, y=?, z=?, worldversion = ?, isDead = ?, data = ? WHERE id=?";
        try {
            final PreparedStatement prepareStatement = this.conn.prepareStatement(s);
            try {
                prepareStatement.setString(1, networkCharacterData.username);
                prepareStatement.setString(2, Core.GameSaveWorld);
                prepareStatement.setInt(3, networkCharacterData.playerIndex);
                final ResultSet executeQuery = prepareStatement.executeQuery();
                if (executeQuery.next()) {
                    final int int1 = executeQuery.getInt(1);
                    final PreparedStatement prepareStatement2 = this.conn.prepareStatement(s3);
                    try {
                        prepareStatement2.setFloat(1, networkCharacterData.x);
                        prepareStatement2.setFloat(2, networkCharacterData.y);
                        prepareStatement2.setFloat(3, networkCharacterData.z);
                        prepareStatement2.setInt(4, networkCharacterData.worldVersion);
                        prepareStatement2.setBoolean(5, networkCharacterData.isDead);
                        prepareStatement2.setBytes(6, networkCharacterData.buffer);
                        prepareStatement2.setInt(7, int1);
                        prepareStatement2.executeUpdate();
                        this.conn.commit();
                        if (prepareStatement2 != null) {
                            prepareStatement2.close();
                        }
                    }
                    catch (Throwable t) {
                        if (prepareStatement2 != null) {
                            try {
                                prepareStatement2.close();
                            }
                            catch (Throwable exception) {
                                t.addSuppressed(exception);
                            }
                        }
                        throw t;
                    }
                    if (prepareStatement != null) {
                        prepareStatement.close();
                    }
                    return;
                }
                if (prepareStatement != null) {
                    prepareStatement.close();
                }
            }
            catch (Throwable t2) {
                if (prepareStatement != null) {
                    try {
                        prepareStatement.close();
                    }
                    catch (Throwable exception2) {
                        t2.addSuppressed(exception2);
                    }
                }
                throw t2;
            }
            final ByteBuffer allocate = ByteBuffer.allocate(networkCharacterData.buffer.length);
            allocate.rewind();
            allocate.put(networkCharacterData.buffer);
            allocate.rewind();
            final IsoPlayer isoPlayer = new IsoPlayer(IsoCell.getInstance());
            isoPlayer.load(allocate, networkCharacterData.worldVersion);
            final String s4 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, isoPlayer.getDescriptor().getForename(), isoPlayer.getDescriptor().getSurname());
            final PreparedStatement prepareStatement3 = this.conn.prepareStatement(s2);
            try {
                prepareStatement3.setString(1, Core.GameSaveWorld);
                prepareStatement3.setString(2, networkCharacterData.username);
                prepareStatement3.setInt(3, networkCharacterData.playerIndex);
                prepareStatement3.setString(4, s4);
                prepareStatement3.setFloat(5, networkCharacterData.x);
                prepareStatement3.setFloat(6, networkCharacterData.y);
                prepareStatement3.setFloat(7, networkCharacterData.z);
                prepareStatement3.setInt(8, networkCharacterData.worldVersion);
                prepareStatement3.setBoolean(9, networkCharacterData.isDead);
                prepareStatement3.setBytes(10, networkCharacterData.buffer);
                prepareStatement3.executeUpdate();
                this.conn.commit();
                if (prepareStatement3 != null) {
                    prepareStatement3.close();
                }
            }
            catch (Throwable t3) {
                if (prepareStatement3 != null) {
                    try {
                        prepareStatement3.close();
                    }
                    catch (Throwable exception3) {
                        t3.addSuppressed(exception3);
                    }
                }
                throw t3;
            }
        }
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
            PlayerDBHelper.rollback(this.conn);
        }
    }
    
    public void serverLoadNetworkCharacter(final ByteBuffer byteBuffer, final UdpConnection udpConnection) {
        final byte value = byteBuffer.get();
        if (value < 0 || value >= 4) {
            return;
        }
        if (this.conn == null) {
            return;
        }
        final String s = "SELECT id, x, y, z, data, worldversion, isDead FROM networkPlayers WHERE username=? AND world=? AND playerIndex=?";
        try {
            final PreparedStatement prepareStatement = this.conn.prepareStatement(s);
            try {
                prepareStatement.setString(1, udpConnection.username);
                prepareStatement.setString(2, Core.GameSaveWorld);
                prepareStatement.setInt(3, value);
                final ResultSet executeQuery = prepareStatement.executeQuery();
                if (executeQuery.next()) {
                    executeQuery.getInt(1);
                    final float float1 = executeQuery.getFloat(2);
                    final float float2 = executeQuery.getFloat(3);
                    final float float3 = executeQuery.getFloat(4);
                    final byte[] bytes = executeQuery.getBytes(5);
                    final int int1 = executeQuery.getInt(6);
                    final boolean boolean1 = executeQuery.getBoolean(7);
                    final ByteBufferWriter startPacket = udpConnection.startPacket();
                    PacketTypes.PacketType.LoadPlayerProfile.doPacket(startPacket);
                    startPacket.putByte((byte)1);
                    startPacket.putInt(value);
                    startPacket.putFloat(float1);
                    startPacket.putFloat(float2);
                    startPacket.putFloat(float3);
                    startPacket.putInt(int1);
                    startPacket.putByte((byte)(boolean1 ? 1 : 0));
                    startPacket.putInt(bytes.length);
                    startPacket.bb.put(bytes);
                    PacketTypes.PacketType.LoadPlayerProfile.send(udpConnection);
                }
                else {
                    final ByteBufferWriter startPacket2 = udpConnection.startPacket();
                    PacketTypes.PacketType.LoadPlayerProfile.doPacket(startPacket2);
                    startPacket2.putByte((byte)0);
                    startPacket2.putInt(value);
                    PacketTypes.PacketType.LoadPlayerProfile.send(udpConnection);
                }
                if (prepareStatement != null) {
                    prepareStatement.close();
                }
            }
            catch (Throwable t) {
                if (prepareStatement != null) {
                    try {
                        prepareStatement.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                }
                throw t;
            }
        }
        catch (SQLException ex) {
            ExceptionLogger.logException(ex);
        }
    }
    
    static {
        ServerPlayerDB.instance = null;
        ServerPlayerDB.allow = false;
    }
    
    private static final class NetworkCharacterData
    {
        byte[] buffer;
        String username;
        int playerIndex;
        float x;
        float y;
        float z;
        boolean isDead;
        int worldVersion;
        
        public NetworkCharacterData(final ByteBuffer byteBuffer, final UdpConnection udpConnection) {
            this.playerIndex = byteBuffer.get();
            this.x = byteBuffer.getFloat();
            this.y = byteBuffer.getFloat();
            this.z = byteBuffer.getFloat();
            this.isDead = (byteBuffer.get() == 1);
            this.worldVersion = byteBuffer.getInt();
            byteBuffer.get(this.buffer = new byte[byteBuffer.getInt()]);
            this.username = udpConnection.username;
        }
    }
}

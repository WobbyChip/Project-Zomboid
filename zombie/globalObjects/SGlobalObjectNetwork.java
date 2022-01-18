// 
// Decompiled by Procyon v0.5.36
// 

package zombie.globalObjects;

import zombie.Lua.LuaManager;
import zombie.GameWindow;
import java.util.HashSet;
import se.krka.kahlua.vm.KahluaTableIterator;
import java.io.IOException;
import zombie.core.logger.ExceptionLogger;
import zombie.debug.DebugLog;
import zombie.network.TableNetworkUtils;
import zombie.network.PacketTypes;
import se.krka.kahlua.vm.KahluaTable;
import zombie.spnetwork.SinglePlayerServer;
import zombie.network.GameClient;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameServer;
import zombie.characters.IsoPlayer;
import zombie.core.network.ByteBufferWriter;
import java.nio.ByteBuffer;

public final class SGlobalObjectNetwork
{
    public static final byte PACKET_ServerCommand = 1;
    public static final byte PACKET_ClientCommand = 2;
    public static final byte PACKET_NewLuaObjectAt = 3;
    public static final byte PACKET_RemoveLuaObjectAt = 4;
    public static final byte PACKET_UpdateLuaObjectAt = 5;
    private static final ByteBuffer BYTE_BUFFER;
    private static final ByteBufferWriter BYTE_BUFFER_WRITER;
    
    public static void receive(final ByteBuffer byteBuffer, final IsoPlayer isoPlayer) {
        switch (byteBuffer.get()) {
            case 2: {
                receiveClientCommand(byteBuffer, isoPlayer);
                break;
            }
        }
    }
    
    private static void sendPacket(final ByteBuffer byteBuffer) {
        if (GameServer.bServer) {
            for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
                final ByteBufferWriter startPacket = udpConnection.startPacket();
                byteBuffer.flip();
                startPacket.bb.put(byteBuffer);
                udpConnection.endPacketImmediate();
            }
        }
        else {
            if (GameClient.bClient) {
                throw new IllegalStateException("can't call this method on the client");
            }
            for (int j = 0; j < SinglePlayerServer.udpEngine.connections.size(); ++j) {
                final zombie.spnetwork.UdpConnection udpConnection2 = SinglePlayerServer.udpEngine.connections.get(j);
                final ByteBufferWriter startPacket2 = udpConnection2.startPacket();
                byteBuffer.flip();
                startPacket2.bb.put(byteBuffer);
                udpConnection2.endPacketImmediate();
            }
        }
    }
    
    private static void writeServerCommand(final String s, final String s2, final KahluaTable kahluaTable, final ByteBufferWriter byteBufferWriter) {
        PacketTypes.PacketType.GlobalObjects.doPacket(byteBufferWriter);
        byteBufferWriter.putByte((byte)1);
        byteBufferWriter.putUTF(s);
        byteBufferWriter.putUTF(s2);
        if (kahluaTable == null || kahluaTable.isEmpty()) {
            byteBufferWriter.putByte((byte)0);
        }
        else {
            byteBufferWriter.putByte((byte)1);
            try {
                final KahluaTableIterator iterator = kahluaTable.iterator();
                while (iterator.advance()) {
                    if (!TableNetworkUtils.canSave(iterator.getKey(), iterator.getValue())) {
                        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/String;, iterator.getKey(), iterator.getValue()));
                    }
                }
                TableNetworkUtils.save(kahluaTable, byteBufferWriter.bb);
            }
            catch (IOException ex) {
                ExceptionLogger.logException(ex);
            }
        }
    }
    
    public static void sendServerCommand(final String s, final String s2, final KahluaTable kahluaTable) {
        SGlobalObjectNetwork.BYTE_BUFFER.clear();
        writeServerCommand(s, s2, kahluaTable, SGlobalObjectNetwork.BYTE_BUFFER_WRITER);
        sendPacket(SGlobalObjectNetwork.BYTE_BUFFER);
    }
    
    public static void addGlobalObjectOnClient(final SGlobalObject sGlobalObject) throws IOException {
        SGlobalObjectNetwork.BYTE_BUFFER.clear();
        final ByteBufferWriter byte_BUFFER_WRITER = SGlobalObjectNetwork.BYTE_BUFFER_WRITER;
        PacketTypes.PacketType.GlobalObjects.doPacket(byte_BUFFER_WRITER);
        byte_BUFFER_WRITER.putByte((byte)3);
        byte_BUFFER_WRITER.putUTF(sGlobalObject.system.name);
        byte_BUFFER_WRITER.putInt(sGlobalObject.getX());
        byte_BUFFER_WRITER.putInt(sGlobalObject.getY());
        byte_BUFFER_WRITER.putByte((byte)sGlobalObject.getZ());
        TableNetworkUtils.saveSome(sGlobalObject.getModData(), byte_BUFFER_WRITER.bb, ((SGlobalObjectSystem)sGlobalObject.system).objectSyncKeys);
        sendPacket(SGlobalObjectNetwork.BYTE_BUFFER);
    }
    
    public static void removeGlobalObjectOnClient(final GlobalObject globalObject) {
        SGlobalObjectNetwork.BYTE_BUFFER.clear();
        final ByteBufferWriter byte_BUFFER_WRITER = SGlobalObjectNetwork.BYTE_BUFFER_WRITER;
        PacketTypes.PacketType.GlobalObjects.doPacket(byte_BUFFER_WRITER);
        byte_BUFFER_WRITER.putByte((byte)4);
        byte_BUFFER_WRITER.putUTF(globalObject.system.name);
        byte_BUFFER_WRITER.putInt(globalObject.getX());
        byte_BUFFER_WRITER.putInt(globalObject.getY());
        byte_BUFFER_WRITER.putByte((byte)globalObject.getZ());
        sendPacket(SGlobalObjectNetwork.BYTE_BUFFER);
    }
    
    public static void updateGlobalObjectOnClient(final SGlobalObject sGlobalObject) throws IOException {
        SGlobalObjectNetwork.BYTE_BUFFER.clear();
        final ByteBufferWriter byte_BUFFER_WRITER = SGlobalObjectNetwork.BYTE_BUFFER_WRITER;
        PacketTypes.PacketType.GlobalObjects.doPacket(byte_BUFFER_WRITER);
        byte_BUFFER_WRITER.putByte((byte)5);
        byte_BUFFER_WRITER.putUTF(sGlobalObject.system.name);
        byte_BUFFER_WRITER.putInt(sGlobalObject.getX());
        byte_BUFFER_WRITER.putInt(sGlobalObject.getY());
        byte_BUFFER_WRITER.putByte((byte)sGlobalObject.getZ());
        TableNetworkUtils.saveSome(sGlobalObject.getModData(), byte_BUFFER_WRITER.bb, ((SGlobalObjectSystem)sGlobalObject.system).objectSyncKeys);
        sendPacket(SGlobalObjectNetwork.BYTE_BUFFER);
    }
    
    private static void receiveClientCommand(final ByteBuffer byteBuffer, final IsoPlayer isoPlayer) {
        final String readString = GameWindow.ReadString(byteBuffer);
        final String readString2 = GameWindow.ReadString(byteBuffer);
        final boolean b = byteBuffer.get() == 1;
        KahluaTable table = null;
        if (b) {
            table = LuaManager.platform.newTable();
            try {
                TableNetworkUtils.load(table, byteBuffer);
            }
            catch (Exception ex) {
                ex.printStackTrace();
                return;
            }
        }
        SGlobalObjects.receiveClientCommand(readString, readString2, isoPlayer, table);
    }
    
    static {
        BYTE_BUFFER = ByteBuffer.allocate(1048576);
        BYTE_BUFFER_WRITER = new ByteBufferWriter(SGlobalObjectNetwork.BYTE_BUFFER);
    }
}

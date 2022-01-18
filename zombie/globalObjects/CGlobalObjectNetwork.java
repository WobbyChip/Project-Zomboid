// 
// Decompiled by Procyon v0.5.36
// 

package zombie.globalObjects;

import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.debug.DebugLog;
import zombie.characters.IsoPlayer;
import zombie.spnetwork.SinglePlayerClient;
import zombie.network.PacketTypes;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.core.logger.ExceptionLogger;
import zombie.network.TableNetworkUtils;
import zombie.Lua.LuaManager;
import zombie.GameWindow;
import java.io.IOException;
import se.krka.kahlua.vm.KahluaTable;
import zombie.core.network.ByteBufferWriter;
import java.nio.ByteBuffer;

public final class CGlobalObjectNetwork
{
    private static final ByteBuffer BYTE_BUFFER;
    private static final ByteBufferWriter BYTE_BUFFER_WRITER;
    private static KahluaTable tempTable;
    
    public static void receive(final ByteBuffer byteBuffer) throws IOException {
        switch (byteBuffer.get()) {
            case 1: {
                receiveServerCommand(byteBuffer);
                break;
            }
            case 3: {
                receiveNewLuaObjectAt(byteBuffer);
                break;
            }
            case 4: {
                receiveRemoveLuaObjectAt(byteBuffer);
                break;
            }
            case 5: {
                receiveUpdateLuaObjectAt(byteBuffer);
                break;
            }
        }
    }
    
    private static void receiveServerCommand(final ByteBuffer byteBuffer) {
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
                ExceptionLogger.logException(ex);
                return;
            }
        }
        CGlobalObjects.receiveServerCommand(readString, readString2, table);
    }
    
    private static void receiveNewLuaObjectAt(final ByteBuffer byteBuffer) throws IOException {
        final String readStringUTF = GameWindow.ReadStringUTF(byteBuffer);
        final int int1 = byteBuffer.getInt();
        final int int2 = byteBuffer.getInt();
        final byte value = byteBuffer.get();
        if (CGlobalObjectNetwork.tempTable == null) {
            CGlobalObjectNetwork.tempTable = LuaManager.platform.newTable();
        }
        TableNetworkUtils.load(CGlobalObjectNetwork.tempTable, byteBuffer);
        final CGlobalObjectSystem systemByName = CGlobalObjects.getSystemByName(readStringUTF);
        if (systemByName == null) {
            return;
        }
        systemByName.receiveNewLuaObjectAt(int1, int2, value, CGlobalObjectNetwork.tempTable);
    }
    
    private static void receiveRemoveLuaObjectAt(final ByteBuffer byteBuffer) {
        final String readStringUTF = GameWindow.ReadStringUTF(byteBuffer);
        final int int1 = byteBuffer.getInt();
        final int int2 = byteBuffer.getInt();
        final byte value = byteBuffer.get();
        final CGlobalObjectSystem systemByName = CGlobalObjects.getSystemByName(readStringUTF);
        if (systemByName == null) {
            return;
        }
        systemByName.receiveRemoveLuaObjectAt(int1, int2, value);
    }
    
    private static void receiveUpdateLuaObjectAt(final ByteBuffer byteBuffer) throws IOException {
        final String readStringUTF = GameWindow.ReadStringUTF(byteBuffer);
        final int int1 = byteBuffer.getInt();
        final int int2 = byteBuffer.getInt();
        final byte value = byteBuffer.get();
        if (CGlobalObjectNetwork.tempTable == null) {
            CGlobalObjectNetwork.tempTable = LuaManager.platform.newTable();
        }
        TableNetworkUtils.load(CGlobalObjectNetwork.tempTable, byteBuffer);
        final CGlobalObjectSystem systemByName = CGlobalObjects.getSystemByName(readStringUTF);
        if (systemByName == null) {
            return;
        }
        systemByName.receiveUpdateLuaObjectAt(int1, int2, value, CGlobalObjectNetwork.tempTable);
    }
    
    private static void sendPacket(final ByteBuffer byteBuffer) {
        if (GameServer.bServer) {
            throw new IllegalStateException("can't call this method on the server");
        }
        if (GameClient.bClient) {
            final ByteBufferWriter startPacket = GameClient.connection.startPacket();
            byteBuffer.flip();
            startPacket.bb.put(byteBuffer);
            PacketTypes.PacketType.GlobalObjects.send(GameClient.connection);
        }
        else {
            final ByteBufferWriter startPacket2 = SinglePlayerClient.connection.startPacket();
            byteBuffer.flip();
            startPacket2.bb.put(byteBuffer);
            SinglePlayerClient.connection.endPacketImmediate();
        }
    }
    
    public static void sendClientCommand(final IsoPlayer isoPlayer, final String s, final String s2, final KahluaTable kahluaTable) {
        CGlobalObjectNetwork.BYTE_BUFFER.clear();
        writeClientCommand(isoPlayer, s, s2, kahluaTable, CGlobalObjectNetwork.BYTE_BUFFER_WRITER);
        sendPacket(CGlobalObjectNetwork.BYTE_BUFFER);
    }
    
    private static void writeClientCommand(final IsoPlayer isoPlayer, final String s, final String s2, final KahluaTable kahluaTable, final ByteBufferWriter byteBufferWriter) {
        PacketTypes.PacketType.GlobalObjects.doPacket(byteBufferWriter);
        byteBufferWriter.putByte((byte)((isoPlayer != null) ? isoPlayer.PlayerIndex : -1));
        byteBufferWriter.putByte((byte)2);
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
    
    public static void Reset() {
        if (CGlobalObjectNetwork.tempTable != null) {
            CGlobalObjectNetwork.tempTable.wipe();
            CGlobalObjectNetwork.tempTable = null;
        }
    }
    
    static {
        BYTE_BUFFER = ByteBuffer.allocate(1048576);
        BYTE_BUFFER_WRITER = new ByteBufferWriter(CGlobalObjectNetwork.BYTE_BUFFER);
    }
}

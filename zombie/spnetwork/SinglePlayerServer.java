// 
// Decompiled by Procyon v0.5.36
// 

package zombie.spnetwork;

import java.util.Iterator;
import zombie.globalObjects.SGlobalObjectNetwork;
import zombie.Lua.LuaEventManager;
import zombie.GameWindow;
import zombie.network.GameClient;
import se.krka.kahlua.vm.KahluaTableIterator;
import java.io.IOException;
import zombie.debug.DebugLog;
import zombie.network.TableNetworkUtils;
import zombie.Lua.LuaManager;
import zombie.core.network.ByteBufferWriter;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.vehicles.BaseVehicle;
import zombie.characters.IsoPlayer;
import zombie.network.PacketTypes;
import se.krka.kahlua.vm.KahluaTable;
import zombie.iso.IsoObject;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public final class SinglePlayerServer
{
    private static final ArrayList<ZomboidNetData> MainLoopNetData;
    public static final UdpEngineServer udpEngine;
    
    public static void addIncoming(final short n, final ByteBuffer byteBuffer, final UdpConnection udpConnection) {
        ZomboidNetData e;
        if (byteBuffer.remaining() > 2048) {
            e = ZomboidNetDataPool.instance.getLong(byteBuffer.remaining());
        }
        else {
            e = ZomboidNetDataPool.instance.get();
        }
        e.read(n, byteBuffer, udpConnection);
        synchronized (SinglePlayerServer.MainLoopNetData) {
            SinglePlayerServer.MainLoopNetData.add(e);
        }
    }
    
    private static void sendObjectChange(final IsoObject isoObject, final String s, final KahluaTable kahluaTable, final UdpConnection udpConnection) {
        if (isoObject.getSquare() == null) {
            return;
        }
        final ByteBufferWriter startPacket = udpConnection.startPacket();
        PacketTypes.PacketType.ObjectChange.doPacket(startPacket);
        if (isoObject instanceof IsoPlayer) {
            startPacket.putByte((byte)1);
            startPacket.putShort(((IsoPlayer)isoObject).OnlineID);
        }
        else if (isoObject instanceof BaseVehicle) {
            startPacket.putByte((byte)2);
            startPacket.putShort(((BaseVehicle)isoObject).getId());
        }
        else if (isoObject instanceof IsoWorldInventoryObject) {
            startPacket.putByte((byte)3);
            startPacket.putInt(isoObject.getSquare().getX());
            startPacket.putInt(isoObject.getSquare().getY());
            startPacket.putInt(isoObject.getSquare().getZ());
            startPacket.putInt(((IsoWorldInventoryObject)isoObject).getItem().getID());
        }
        else {
            startPacket.putByte((byte)0);
            startPacket.putInt(isoObject.getSquare().getX());
            startPacket.putInt(isoObject.getSquare().getY());
            startPacket.putInt(isoObject.getSquare().getZ());
            startPacket.putInt(isoObject.getSquare().getObjects().indexOf(isoObject));
        }
        startPacket.putUTF(s);
        isoObject.saveChange(s, kahluaTable, startPacket.bb);
        udpConnection.endPacketImmediate();
    }
    
    public static void sendObjectChange(final IsoObject isoObject, final String s, final KahluaTable kahluaTable) {
        if (isoObject == null) {
            return;
        }
        for (int i = 0; i < SinglePlayerServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection = SinglePlayerServer.udpEngine.connections.get(i);
            if (udpConnection.ReleventTo(isoObject.getX(), isoObject.getY())) {
                sendObjectChange(isoObject, s, kahluaTable, udpConnection);
            }
        }
    }
    
    public static void sendObjectChange(final IsoObject isoObject, final String s, final Object... array) {
        if (array.length == 0) {
            sendObjectChange(isoObject, s, null);
            return;
        }
        if (array.length % 2 != 0) {
            return;
        }
        final KahluaTable table = LuaManager.platform.newTable();
        for (int i = 0; i < array.length; i += 2) {
            final Object o = array[i + 1];
            if (o instanceof Float) {
                table.rawset(array[i], (Object)(double)o);
            }
            else if (o instanceof Integer) {
                table.rawset(array[i], (Object)(double)o);
            }
            else if (o instanceof Short) {
                table.rawset(array[i], (Object)(double)o);
            }
            else {
                table.rawset(array[i], o);
            }
        }
        sendObjectChange(isoObject, s, table);
    }
    
    public static void sendServerCommand(final String s, final String s2, final KahluaTable kahluaTable, final UdpConnection udpConnection) {
        final ByteBufferWriter startPacket = udpConnection.startPacket();
        PacketTypes.PacketType.ClientCommand.doPacket(startPacket);
        startPacket.putUTF(s);
        startPacket.putUTF(s2);
        if (kahluaTable == null || kahluaTable.isEmpty()) {
            startPacket.putByte((byte)0);
        }
        else {
            startPacket.putByte((byte)1);
            try {
                final KahluaTableIterator iterator = kahluaTable.iterator();
                while (iterator.advance()) {
                    if (!TableNetworkUtils.canSave(iterator.getKey(), iterator.getValue())) {
                        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/String;, iterator.getKey(), iterator.getValue()));
                    }
                }
                TableNetworkUtils.save(kahluaTable, startPacket.bb);
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        udpConnection.endPacketImmediate();
    }
    
    public static void sendServerCommand(final String s, final String s2, final KahluaTable kahluaTable) {
        for (int i = 0; i < SinglePlayerServer.udpEngine.connections.size(); ++i) {
            sendServerCommand(s, s2, kahluaTable, SinglePlayerServer.udpEngine.connections.get(i));
        }
    }
    
    public static void update() {
        if (GameClient.bClient) {
            return;
        }
        for (short onlineID = 0; onlineID < IsoPlayer.numPlayers; ++onlineID) {
            if (IsoPlayer.players[onlineID] != null) {
                IsoPlayer.players[onlineID].setOnlineID(onlineID);
            }
        }
        synchronized (SinglePlayerServer.MainLoopNetData) {
            for (int i = 0; i < SinglePlayerServer.MainLoopNetData.size(); ++i) {
                mainLoopDealWithNetData(SinglePlayerServer.MainLoopNetData.get(i));
                SinglePlayerServer.MainLoopNetData.remove(i--);
            }
        }
    }
    
    private static void mainLoopDealWithNetData(final ZomboidNetData zomboidNetData) {
        final ByteBuffer buffer = zomboidNetData.buffer;
        try {
            switch (PacketTypes.packetTypes.get(zomboidNetData.type)) {
                case ClientCommand: {
                    receiveClientCommand(buffer, zomboidNetData.connection);
                    break;
                }
                case GlobalObjects: {
                    receiveGlobalObjects(buffer, zomboidNetData.connection);
                    break;
                }
            }
        }
        finally {
            ZomboidNetDataPool.instance.discard(zomboidNetData);
        }
    }
    
    private static IsoPlayer getAnyPlayerFromConnection(final UdpConnection udpConnection) {
        for (int i = 0; i < 4; ++i) {
            if (udpConnection.players[i] != null) {
                return udpConnection.players[i];
            }
        }
        return null;
    }
    
    private static IsoPlayer getPlayerFromConnection(final UdpConnection udpConnection, final int n) {
        if (n >= 0 && n < 4) {
            return udpConnection.players[n];
        }
        return null;
    }
    
    private static void receiveClientCommand(final ByteBuffer byteBuffer, final UdpConnection udpConnection) {
        final byte value = byteBuffer.get();
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
        IsoPlayer isoPlayer = getPlayerFromConnection(udpConnection, value);
        if (value == -1) {
            isoPlayer = getAnyPlayerFromConnection(udpConnection);
        }
        if (isoPlayer == null) {
            DebugLog.log("receiveClientCommand: player is null");
            return;
        }
        LuaEventManager.triggerEvent("OnClientCommand", readString, readString2, isoPlayer, table);
    }
    
    private static void receiveGlobalObjects(final ByteBuffer byteBuffer, final UdpConnection udpConnection) {
        final byte value = byteBuffer.get();
        IsoPlayer isoPlayer = getPlayerFromConnection(udpConnection, value);
        if (value == -1) {
            isoPlayer = getAnyPlayerFromConnection(udpConnection);
        }
        if (isoPlayer == null) {
            DebugLog.log("receiveGlobalObjects: player is null");
            return;
        }
        SGlobalObjectNetwork.receive(byteBuffer, isoPlayer);
    }
    
    public static void Reset() {
        final Iterator<ZomboidNetData> iterator = SinglePlayerServer.MainLoopNetData.iterator();
        while (iterator.hasNext()) {
            ZomboidNetDataPool.instance.discard(iterator.next());
        }
        SinglePlayerServer.MainLoopNetData.clear();
    }
    
    static {
        MainLoopNetData = new ArrayList<ZomboidNetData>();
        udpEngine = new UdpEngineServer();
    }
    
    public static final class UdpEngineServer extends UdpEngine
    {
        public final ArrayList<UdpConnection> connections;
        
        UdpEngineServer() {
            (this.connections = new ArrayList<UdpConnection>()).add(new UdpConnection(this));
        }
        
        @Override
        public void Send(final ByteBuffer byteBuffer) {
            SinglePlayerClient.udpEngine.Receive(byteBuffer);
        }
        
        @Override
        public void Receive(final ByteBuffer byteBuffer) {
            final int n = byteBuffer.get() & 0xFF;
            SinglePlayerServer.addIncoming(byteBuffer.getShort(), byteBuffer, SinglePlayerServer.udpEngine.connections.get(0));
        }
    }
}

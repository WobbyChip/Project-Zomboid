// 
// Decompiled by Procyon v0.5.36
// 

package zombie.spnetwork;

import java.util.Iterator;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.core.network.ByteBufferWriter;
import java.io.IOException;
import zombie.network.TableNetworkUtils;
import se.krka.kahlua.vm.KahluaTable;
import zombie.iso.IsoGridSquare;
import zombie.vehicles.BaseVehicle;
import zombie.iso.IsoObject;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.iso.IsoWorld;
import zombie.vehicles.VehicleManager;
import zombie.debug.DebugLog;
import zombie.core.Core;
import zombie.GameWindow;
import zombie.globalObjects.CGlobalObjectNetwork;
import zombie.network.PacketTypes;
import zombie.characters.IsoPlayer;
import zombie.network.GameClient;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public final class SinglePlayerClient
{
    private static final ArrayList<ZomboidNetData> MainLoopNetData;
    public static final UdpEngine udpEngine;
    public static final UdpConnection connection;
    
    public static void addIncoming(final short n, final ByteBuffer byteBuffer) {
        ZomboidNetData e;
        if (byteBuffer.remaining() > 2048) {
            e = ZomboidNetDataPool.instance.getLong(byteBuffer.remaining());
        }
        else {
            e = ZomboidNetDataPool.instance.get();
        }
        e.read(n, byteBuffer, SinglePlayerClient.connection);
        synchronized (SinglePlayerClient.MainLoopNetData) {
            SinglePlayerClient.MainLoopNetData.add(e);
        }
    }
    
    public static void update() throws Exception {
        if (GameClient.bClient) {
            return;
        }
        for (short onlineID = 0; onlineID < IsoPlayer.numPlayers; ++onlineID) {
            if (IsoPlayer.players[onlineID] != null) {
                IsoPlayer.players[onlineID].setOnlineID(onlineID);
            }
        }
        synchronized (SinglePlayerClient.MainLoopNetData) {
            for (int i = 0; i < SinglePlayerClient.MainLoopNetData.size(); ++i) {
                final ZomboidNetData zomboidNetData = SinglePlayerClient.MainLoopNetData.get(i);
                try {
                    mainLoopDealWithNetData(zomboidNetData);
                }
                finally {
                    SinglePlayerClient.MainLoopNetData.remove(i--);
                }
            }
        }
    }
    
    private static void mainLoopDealWithNetData(final ZomboidNetData zomboidNetData) throws Exception {
        final ByteBuffer buffer = zomboidNetData.buffer;
        try {
            final PacketTypes.PacketType packetType = PacketTypes.packetTypes.get(zomboidNetData.type);
            switch (packetType) {
                case ClientCommand: {
                    receiveServerCommand(buffer);
                    break;
                }
                case GlobalObjects: {
                    CGlobalObjectNetwork.receive(buffer);
                    break;
                }
                case ObjectChange: {
                    receiveObjectChange(buffer);
                    break;
                }
                default: {
                    throw new IllegalStateException(invokedynamic(makeConcatWithConstants:(Lzombie/network/PacketTypes$PacketType;)Ljava/lang/String;, packetType));
                }
            }
        }
        finally {
            ZomboidNetDataPool.instance.discard(zomboidNetData);
        }
    }
    
    private static void delayPacket(final int n, final int n2, final int n3) {
    }
    
    private static IsoPlayer getPlayerByID(final int n) {
        return IsoPlayer.players[n];
    }
    
    private static void receiveObjectChange(final ByteBuffer byteBuffer) {
        final byte value = byteBuffer.get();
        if (value == 1) {
            final short short1 = byteBuffer.getShort();
            final String readString = GameWindow.ReadString(byteBuffer);
            if (Core.bDebug) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, readString));
            }
            final IsoPlayer playerByID = getPlayerByID(short1);
            if (playerByID != null) {
                playerByID.loadChange(readString, byteBuffer);
            }
        }
        else if (value == 2) {
            final short short2 = byteBuffer.getShort();
            final String readString2 = GameWindow.ReadString(byteBuffer);
            if (Core.bDebug) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, readString2));
            }
            final BaseVehicle vehicleByID = VehicleManager.instance.getVehicleByID(short2);
            if (vehicleByID != null) {
                vehicleByID.loadChange(readString2, byteBuffer);
            }
            else if (Core.bDebug) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, short2));
            }
        }
        else if (value == 3) {
            final int int1 = byteBuffer.getInt();
            final int int2 = byteBuffer.getInt();
            final int int3 = byteBuffer.getInt();
            final int int4 = byteBuffer.getInt();
            final String readString3 = GameWindow.ReadString(byteBuffer);
            if (Core.bDebug) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, readString3));
            }
            final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
            if (gridSquare == null) {
                delayPacket(int1, int2, int3);
                return;
            }
            for (int i = 0; i < gridSquare.getWorldObjects().size(); ++i) {
                final IsoWorldInventoryObject isoWorldInventoryObject = gridSquare.getWorldObjects().get(i);
                if (isoWorldInventoryObject.getItem() != null && isoWorldInventoryObject.getItem().getID() == int4) {
                    isoWorldInventoryObject.loadChange(readString3, byteBuffer);
                    return;
                }
            }
            if (Core.bDebug) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(IIII)Ljava/lang/String;, int4, int1, int2, int3));
            }
        }
        else {
            final int int5 = byteBuffer.getInt();
            final int int6 = byteBuffer.getInt();
            final int int7 = byteBuffer.getInt();
            final int int8 = byteBuffer.getInt();
            final String readString4 = GameWindow.ReadString(byteBuffer);
            if (Core.bDebug) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, readString4));
            }
            final IsoGridSquare gridSquare2 = IsoWorld.instance.CurrentCell.getGridSquare(int5, int6, int7);
            if (gridSquare2 == null) {
                delayPacket(int5, int6, int7);
                return;
            }
            if (int8 >= 0 && int8 < gridSquare2.getObjects().size()) {
                gridSquare2.getObjects().get(int8).loadChange(readString4, byteBuffer);
            }
            else if (Core.bDebug) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(IIII)Ljava/lang/String;, int8, int5, int6, int7));
            }
        }
    }
    
    public static void sendClientCommand(final IsoPlayer isoPlayer, final String s, final String s2, final KahluaTable kahluaTable) {
        final ByteBufferWriter startPacket = SinglePlayerClient.connection.startPacket();
        PacketTypes.PacketType.ClientCommand.doPacket(startPacket);
        startPacket.putByte((byte)((isoPlayer != null) ? isoPlayer.PlayerIndex : -1));
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
        SinglePlayerClient.connection.endPacketImmediate();
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
                ex.printStackTrace();
                return;
            }
        }
        LuaEventManager.triggerEvent("OnServerCommand", readString, readString2, table);
    }
    
    public static void Reset() {
        final Iterator<ZomboidNetData> iterator = SinglePlayerClient.MainLoopNetData.iterator();
        while (iterator.hasNext()) {
            ZomboidNetDataPool.instance.discard(iterator.next());
        }
        SinglePlayerClient.MainLoopNetData.clear();
    }
    
    static {
        MainLoopNetData = new ArrayList<ZomboidNetData>();
        udpEngine = new UdpEngineClient();
        connection = new UdpConnection(SinglePlayerClient.udpEngine);
    }
    
    private static final class UdpEngineClient extends UdpEngine
    {
        @Override
        public void Send(final ByteBuffer byteBuffer) {
            SinglePlayerServer.udpEngine.Receive(byteBuffer);
        }
        
        @Override
        public void Receive(final ByteBuffer byteBuffer) {
            final int n = byteBuffer.get() & 0xFF;
            SinglePlayerClient.addIncoming(byteBuffer.getShort(), byteBuffer);
        }
    }
}

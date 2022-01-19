// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import zombie.network.ServerMap;
import zombie.core.raknet.UdpEngine;
import zombie.iso.objects.IsoLightSwitch;
import zombie.network.WorldItemTypes;
import zombie.Lua.LuaEventManager;
import java.nio.ByteBuffer;
import zombie.core.network.ByteBufferWriter;
import zombie.network.PacketTypes;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.inventory.ItemContainer;
import zombie.SystemDisabler;
import zombie.network.GameClient;
import java.util.ArrayList;

public final class ObjectsSyncRequests
{
    public static final short ClientSendChunkHashes = 1;
    public static final short ServerSendGridSquareHashes = 2;
    public static final short ClientSendGridSquareRequest = 3;
    public static final short ServerSendGridSquareObjectsHashes = 4;
    public static final short ClientSendObjectRequests = 5;
    public static final short ServerSendObject = 6;
    public ArrayList<SyncIsoChunk> requestsSyncIsoChunk;
    public ArrayList<SyncIsoGridSquare> requestsSyncIsoGridSquare;
    public ArrayList<SyncIsoObject> requestsSyncIsoObject;
    public long timeout;
    
    public ObjectsSyncRequests(final boolean b) {
        this.timeout = 1000L;
        if (b) {
            this.requestsSyncIsoChunk = new ArrayList<SyncIsoChunk>();
            this.requestsSyncIsoGridSquare = new ArrayList<SyncIsoGridSquare>();
            this.requestsSyncIsoObject = new ArrayList<SyncIsoObject>();
        }
        else {
            this.requestsSyncIsoGridSquare = new ArrayList<SyncIsoGridSquare>();
        }
    }
    
    static int getObjectInsertIndex(final long[] array, final long[] array2, final long n) {
        if (n == array2[0]) {
            return 0;
        }
        for (int i = 0; i < array.length; ++i) {
            if (array[i] == n) {
                return -1;
            }
        }
        int n2 = 0;
        for (int j = 0; j < array2.length; ++j) {
            if (n2 < array.length && array2[j] == array[n2]) {
                ++n2;
            }
            if (array2[j] == n) {
                return n2;
            }
        }
        return -1;
    }
    
    public void putRequestSyncIsoChunk(final IsoChunk isoChunk) {
        if (GameClient.bClient && !SystemDisabler.doWorldSyncEnable) {
            return;
        }
        final SyncIsoChunk e = new SyncIsoChunk();
        e.x = isoChunk.wx;
        e.y = isoChunk.wy;
        e.hashCodeObjects = isoChunk.getHashCodeObjects();
        e.reqTime = 0L;
        e.reqCount = 0;
        synchronized (this.requestsSyncIsoChunk) {
            this.requestsSyncIsoChunk.add(e);
        }
    }
    
    public void putRequestSyncItemContainer(final ItemContainer itemContainer) {
        if (itemContainer == null || itemContainer.parent == null || itemContainer.parent.square == null) {
            return;
        }
        this.putRequestSyncIsoGridSquare(itemContainer.parent.square);
    }
    
    public void putRequestSyncIsoGridSquare(final IsoGridSquare o) {
        if (o == null) {
            return;
        }
        final SyncIsoGridSquare e = new SyncIsoGridSquare();
        e.x = o.x;
        e.y = o.y;
        e.z = o.z;
        e.reqTime = 0L;
        e.reqCount = 0;
        synchronized (this.requestsSyncIsoGridSquare) {
            if (!this.requestsSyncIsoGridSquare.contains(o)) {
                this.requestsSyncIsoGridSquare.add(e);
            }
            else {
                DebugLog.log("Warning: [putRequestSyncIsoGridSquare] Tryed to add dublicate object.");
            }
        }
    }
    
    public void sendRequests(final UdpConnection udpConnection) {
        if (!SystemDisabler.doWorldSyncEnable) {
            return;
        }
        if (this.requestsSyncIsoChunk != null && this.requestsSyncIsoChunk.size() != 0) {
            final ByteBufferWriter startPacket = udpConnection.startPacket();
            PacketTypes.PacketType.SyncObjects.doPacket(startPacket);
            startPacket.putShort((short)1);
            final ByteBuffer bb = startPacket.bb;
            final int position = bb.position();
            startPacket.putShort((short)0);
            int n = 0;
            synchronized (this.requestsSyncIsoChunk) {
                for (int i = this.requestsSyncIsoChunk.size() - 1; i >= 0; --i) {
                    final SyncIsoChunk syncIsoChunk = this.requestsSyncIsoChunk.get(i);
                    if (syncIsoChunk.reqCount > 3) {
                        this.requestsSyncIsoChunk.remove(i);
                    }
                    else {
                        if (syncIsoChunk.reqTime == 0L) {
                            syncIsoChunk.reqTime = System.currentTimeMillis();
                            ++n;
                            bb.putInt(syncIsoChunk.x);
                            bb.putInt(syncIsoChunk.y);
                            bb.putLong(syncIsoChunk.hashCodeObjects);
                            final SyncIsoChunk syncIsoChunk2 = syncIsoChunk;
                            ++syncIsoChunk2.reqCount;
                        }
                        if (System.currentTimeMillis() - syncIsoChunk.reqTime >= this.timeout) {
                            syncIsoChunk.reqTime = System.currentTimeMillis();
                            ++n;
                            bb.putInt(syncIsoChunk.x);
                            bb.putInt(syncIsoChunk.y);
                            bb.putLong(syncIsoChunk.hashCodeObjects);
                            final SyncIsoChunk syncIsoChunk3 = syncIsoChunk;
                            ++syncIsoChunk3.reqCount;
                        }
                        if (n >= 5) {
                            break;
                        }
                    }
                }
            }
            if (n == 0) {
                GameClient.connection.cancelPacket();
                return;
            }
            final int position2 = bb.position();
            bb.position(position);
            bb.putShort((short)n);
            bb.position(position2);
            PacketTypes.PacketType.SyncObjects.send(GameClient.connection);
        }
        if (this.requestsSyncIsoGridSquare != null && this.requestsSyncIsoGridSquare.size() != 0) {
            final ByteBufferWriter startPacket2 = udpConnection.startPacket();
            PacketTypes.PacketType.SyncObjects.doPacket(startPacket2);
            startPacket2.putShort((short)3);
            final ByteBuffer bb2 = startPacket2.bb;
            final int position3 = bb2.position();
            startPacket2.putShort((short)0);
            int n2 = 0;
            synchronized (this.requestsSyncIsoGridSquare) {
                for (int j = 0; j < this.requestsSyncIsoGridSquare.size(); ++j) {
                    final SyncIsoGridSquare syncIsoGridSquare = this.requestsSyncIsoGridSquare.get(j);
                    if (syncIsoGridSquare.reqCount > 3) {
                        this.requestsSyncIsoGridSquare.remove(j);
                        --j;
                    }
                    else {
                        if (syncIsoGridSquare.reqTime == 0L) {
                            syncIsoGridSquare.reqTime = System.currentTimeMillis();
                            ++n2;
                            bb2.putInt(syncIsoGridSquare.x);
                            bb2.putInt(syncIsoGridSquare.y);
                            bb2.put((byte)syncIsoGridSquare.z);
                            final SyncIsoGridSquare syncIsoGridSquare2 = syncIsoGridSquare;
                            ++syncIsoGridSquare2.reqCount;
                        }
                        if (System.currentTimeMillis() - syncIsoGridSquare.reqTime >= this.timeout) {
                            syncIsoGridSquare.reqTime = System.currentTimeMillis();
                            ++n2;
                            bb2.putInt(syncIsoGridSquare.x);
                            bb2.putInt(syncIsoGridSquare.y);
                            bb2.put((byte)syncIsoGridSquare.z);
                            final SyncIsoGridSquare syncIsoGridSquare3 = syncIsoGridSquare;
                            ++syncIsoGridSquare3.reqCount;
                        }
                        if (n2 >= 100) {
                            break;
                        }
                    }
                }
            }
            if (n2 == 0) {
                GameClient.connection.cancelPacket();
                return;
            }
            final int position4 = bb2.position();
            bb2.position(position3);
            bb2.putShort((short)n2);
            bb2.position(position4);
            PacketTypes.PacketType.SyncObjects.send(GameClient.connection);
        }
        if (this.requestsSyncIsoObject != null && this.requestsSyncIsoObject.size() != 0) {
            final ByteBufferWriter startPacket3 = udpConnection.startPacket();
            PacketTypes.PacketType.SyncObjects.doPacket(startPacket3);
            startPacket3.putShort((short)5);
            final ByteBuffer bb3 = startPacket3.bb;
            final int position5 = bb3.position();
            startPacket3.putShort((short)0);
            int n3 = 0;
            synchronized (this.requestsSyncIsoObject) {
                for (int k = 0; k < this.requestsSyncIsoObject.size(); ++k) {
                    final SyncIsoObject syncIsoObject = this.requestsSyncIsoObject.get(k);
                    if (syncIsoObject.reqCount > 3) {
                        this.requestsSyncIsoObject.remove(k);
                        --k;
                    }
                    else {
                        if (syncIsoObject.reqTime == 0L) {
                            syncIsoObject.reqTime = System.currentTimeMillis();
                            ++n3;
                            bb3.putInt(syncIsoObject.x);
                            bb3.putInt(syncIsoObject.y);
                            bb3.put((byte)syncIsoObject.z);
                            bb3.putLong(syncIsoObject.hash);
                            final SyncIsoObject syncIsoObject2 = syncIsoObject;
                            ++syncIsoObject2.reqCount;
                        }
                        if (System.currentTimeMillis() - syncIsoObject.reqTime >= this.timeout) {
                            syncIsoObject.reqTime = System.currentTimeMillis();
                            ++n3;
                            bb3.putInt(syncIsoObject.x);
                            bb3.putInt(syncIsoObject.y);
                            bb3.put((byte)syncIsoObject.z);
                            bb3.putLong(syncIsoObject.hash);
                            final SyncIsoObject syncIsoObject3 = syncIsoObject;
                            ++syncIsoObject3.reqCount;
                        }
                        if (n3 >= 100) {
                            break;
                        }
                    }
                }
            }
            if (n3 == 0) {
                GameClient.connection.cancelPacket();
                return;
            }
            final int position6 = bb3.position();
            bb3.position(position5);
            bb3.putShort((short)n3);
            bb3.position(position6);
            PacketTypes.PacketType.SyncObjects.send(GameClient.connection);
        }
    }
    
    public void receiveSyncIsoChunk(final int n, final int n2) {
        synchronized (this.requestsSyncIsoChunk) {
            for (int i = 0; i < this.requestsSyncIsoChunk.size(); ++i) {
                final SyncIsoChunk syncIsoChunk = this.requestsSyncIsoChunk.get(i);
                if (syncIsoChunk.x == n && syncIsoChunk.y == n2) {
                    this.requestsSyncIsoChunk.remove(i);
                    return;
                }
            }
        }
    }
    
    public void receiveSyncIsoGridSquare(final int n, final int n2, final int n3) {
        synchronized (this.requestsSyncIsoGridSquare) {
            for (int i = 0; i < this.requestsSyncIsoGridSquare.size(); ++i) {
                final SyncIsoGridSquare syncIsoGridSquare = this.requestsSyncIsoGridSquare.get(i);
                if (syncIsoGridSquare.x == n && syncIsoGridSquare.y == n2 && syncIsoGridSquare.z == n3) {
                    this.requestsSyncIsoGridSquare.remove(i);
                    return;
                }
            }
        }
    }
    
    public void receiveSyncIsoObject(final int n, final int n2, final int n3, final long n4) {
        synchronized (this.requestsSyncIsoObject) {
            for (int i = 0; i < this.requestsSyncIsoObject.size(); ++i) {
                final SyncIsoObject syncIsoObject = this.requestsSyncIsoObject.get(i);
                if (syncIsoObject.x == n && syncIsoObject.y == n2 && syncIsoObject.z == n3 && syncIsoObject.hash == n4) {
                    this.requestsSyncIsoObject.remove(i);
                    return;
                }
            }
        }
    }
    
    public void receiveGridSquareHashes(final ByteBuffer byteBuffer) {
        for (short short1 = byteBuffer.getShort(), n = 0; n < short1; ++n) {
            final short short2 = byteBuffer.getShort();
            final short short3 = byteBuffer.getShort();
            byteBuffer.getLong();
            for (short short4 = byteBuffer.getShort(), n2 = 0; n2 < short4; ++n2) {
                final int x = byteBuffer.get() + short2 * 10;
                final int y = byteBuffer.get() + short3 * 10;
                final byte value = byteBuffer.get();
                final int int1 = byteBuffer.getInt();
                final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(x, y, value);
                if (gridSquare != null && gridSquare.getHashCodeObjectsInt() != int1) {
                    final SyncIsoGridSquare e = new SyncIsoGridSquare();
                    e.x = x;
                    e.y = y;
                    e.z = value;
                    e.reqTime = 0L;
                    e.reqCount = 0;
                    synchronized (this.requestsSyncIsoGridSquare) {
                        this.requestsSyncIsoGridSquare.add(e);
                    }
                }
            }
            this.receiveSyncIsoChunk(short2, short3);
        }
    }
    
    public void receiveGridSquareObjectHashes(final ByteBuffer byteBuffer) {
        for (short short1 = byteBuffer.getShort(), n = 0; n < short1; ++n) {
            final int int1 = byteBuffer.getInt();
            final int int2 = byteBuffer.getInt();
            final byte value = byteBuffer.get();
            this.receiveSyncIsoGridSquare(int1, int2, value);
            final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, value);
            if (gridSquare == null) {
                return;
            }
            final byte value2 = byteBuffer.get();
            final int newPosition = byteBuffer.getInt() - 3;
            final long[] array = new long[value2];
            for (byte b = 0; b < value2; ++b) {
                array[b] = byteBuffer.getLong();
            }
            try {
                final boolean[] array2 = new boolean[gridSquare.getObjects().size()];
                final boolean[] array3 = new boolean[value2];
                for (byte b2 = 0; b2 < value2; ++b2) {
                    array3[b2] = true;
                }
                for (int i = 0; i < gridSquare.getObjects().size(); ++i) {
                    array2[i] = false;
                    final long customHashCode = gridSquare.getObjects().get(i).customHashCode();
                    boolean b3 = false;
                    for (byte b4 = 0; b4 < value2; ++b4) {
                        if (array[b4] == customHashCode) {
                            b3 = true;
                            array3[b4] = false;
                            break;
                        }
                    }
                    if (!b3) {
                        array2[i] = true;
                    }
                }
                for (int j = gridSquare.getObjects().size() - 1; j >= 0; --j) {
                    if (array2[j]) {
                        gridSquare.getObjects().get(j).removeFromWorld();
                        gridSquare.getObjects().get(j).removeFromSquare();
                    }
                }
                for (byte b5 = 0; b5 < value2; ++b5) {
                    if (array3[b5]) {
                        final SyncIsoObject e = new SyncIsoObject();
                        e.x = int1;
                        e.y = int2;
                        e.z = value;
                        e.hash = array[b5];
                        e.reqTime = 0L;
                        e.reqCount = 0;
                        synchronized (this.requestsSyncIsoObject) {
                            this.requestsSyncIsoObject.add(e);
                        }
                    }
                }
            }
            catch (Throwable t) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, t.getMessage()));
            }
            gridSquare.RecalcAllWithNeighbours(true);
            IsoWorld.instance.CurrentCell.checkHaveRoof(gridSquare.getX(), gridSquare.getY());
            byteBuffer.position(newPosition);
        }
        LuaEventManager.triggerEvent("OnContainerUpdate");
    }
    
    public void receiveObject(final ByteBuffer byteBuffer) {
        final int int1 = byteBuffer.getInt();
        final int int2 = byteBuffer.getInt();
        final byte value = byteBuffer.get();
        final long long1 = byteBuffer.getLong();
        this.receiveSyncIsoObject(int1, int2, value, long1);
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, value);
        if (gridSquare == null) {
            return;
        }
        final byte value2 = byteBuffer.get();
        final long[] array = new long[value2];
        for (byte b = 0; b < value2; ++b) {
            array[b] = byteBuffer.getLong();
        }
        final long[] array2 = new long[gridSquare.getObjects().size()];
        for (int i = 0; i < gridSquare.getObjects().size(); ++i) {
            array2[i] = gridSquare.getObjects().get(i).customHashCode();
        }
        gridSquare.getObjects().size();
        final int objectInsertIndex = getObjectInsertIndex(array2, array, long1);
        if (objectInsertIndex == -1) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(IIIJ)Ljava/lang/String;, int1, int2, value, long1));
            return;
        }
        final IsoObject fromBuffer = WorldItemTypes.createFromBuffer(byteBuffer);
        if (fromBuffer != null) {
            fromBuffer.loadFromRemoteBuffer(byteBuffer, false);
            gridSquare.getObjects().add(objectInsertIndex, fromBuffer);
            if (fromBuffer instanceof IsoLightSwitch) {
                ((IsoLightSwitch)fromBuffer).addLightSourceFromSprite();
            }
            fromBuffer.addToWorld();
        }
        gridSquare.RecalcAllWithNeighbours(true);
        IsoWorld.instance.CurrentCell.checkHaveRoof(gridSquare.getX(), gridSquare.getY());
        LuaEventManager.triggerEvent("OnContainerUpdate");
    }
    
    public void serverSendRequests(final UdpEngine udpEngine) {
        for (int i = 0; i < udpEngine.connections.size(); ++i) {
            this.serverSendRequests(udpEngine.connections.get(i));
        }
        synchronized (this.requestsSyncIsoGridSquare) {
            for (int j = 0; j < this.requestsSyncIsoGridSquare.size(); ++j) {
                this.requestsSyncIsoGridSquare.remove(0);
            }
        }
    }
    
    public void serverSendRequests(final UdpConnection udpConnection) {
        if (this.requestsSyncIsoGridSquare.size() == 0) {
            return;
        }
        final ByteBufferWriter startPacket = udpConnection.startPacket();
        PacketTypes.PacketType.SyncObjects.doPacket(startPacket);
        startPacket.putShort((short)4);
        final int position = startPacket.bb.position();
        startPacket.putShort((short)0);
        int n = 0;
        for (int i = 0; i < this.requestsSyncIsoGridSquare.size(); ++i) {
            final SyncIsoGridSquare syncIsoGridSquare = this.requestsSyncIsoGridSquare.get(i);
            if (udpConnection.RelevantTo((float)syncIsoGridSquare.x, (float)syncIsoGridSquare.y, 100.0f)) {
                final IsoGridSquare gridSquare = ServerMap.instance.getGridSquare(syncIsoGridSquare.x, syncIsoGridSquare.y, syncIsoGridSquare.z);
                if (gridSquare != null) {
                    ++n;
                    startPacket.putInt(gridSquare.x);
                    startPacket.putInt(gridSquare.y);
                    startPacket.putByte((byte)gridSquare.z);
                    startPacket.putByte((byte)gridSquare.getObjects().size());
                    startPacket.putInt(0);
                    final int position2 = startPacket.bb.position();
                    for (int j = 0; j < gridSquare.getObjects().size(); ++j) {
                        startPacket.putLong(gridSquare.getObjects().get(j).customHashCode());
                    }
                    final int position3 = startPacket.bb.position();
                    startPacket.bb.position(position2 - 4);
                    startPacket.putInt(position3);
                    startPacket.bb.position(position3);
                }
            }
        }
        final int position4 = startPacket.bb.position();
        startPacket.bb.position(position);
        startPacket.putShort((short)n);
        startPacket.bb.position(position4);
        PacketTypes.PacketType.SyncObjects.send(GameClient.connection);
    }
    
    private class SyncIsoChunk
    {
        int x;
        int y;
        long hashCodeObjects;
        long reqTime;
        int reqCount;
    }
    
    private class SyncIsoGridSquare
    {
        int x;
        int y;
        int z;
        long reqTime;
        int reqCount;
        
        @Override
        public int hashCode() {
            return this.x + this.y + this.z;
        }
    }
    
    private class SyncIsoObject
    {
        int x;
        int y;
        int z;
        long hash;
        long reqTime;
        int reqCount;
        
        @Override
        public int hashCode() {
            return (int)(this.x + this.y + this.z + this.hash);
        }
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import java.nio.ByteBuffer;
import zombie.core.network.ByteBufferWriter;
import zombie.network.GameClient;
import zombie.network.PacketTypes;
import zombie.core.raknet.UdpConnection;
import java.util.ArrayList;

public final class WorldObjectsSyncRequests
{
    public final ArrayList<SyncData> requests;
    public long timeout;
    
    public WorldObjectsSyncRequests() {
        this.requests = new ArrayList<SyncData>();
        this.timeout = 1000L;
    }
    
    public void putRequest(final IsoChunk isoChunk) {
        final SyncData e = new SyncData();
        e.x = isoChunk.wx;
        e.y = isoChunk.wy;
        e.hashCodeWorldObjects = isoChunk.getHashCodeObjects();
        e.reqTime = 0L;
        e.reqCount = 0;
        synchronized (this.requests) {
            this.requests.add(e);
        }
    }
    
    public void sendRequests(final UdpConnection udpConnection) {
        if (this.requests.size() == 0) {
            return;
        }
        final ByteBufferWriter startPacket = udpConnection.startPacket();
        PacketTypes.PacketType.SyncWorldObjectsReq.doPacket(startPacket);
        final ByteBuffer bb = startPacket.bb;
        final int position = bb.position();
        startPacket.putShort((short)0);
        int n = 0;
        synchronized (this.requests) {
            for (int i = 0; i < this.requests.size(); ++i) {
                final SyncData syncData = this.requests.get(i);
                if (syncData.reqCount > 2) {
                    this.requests.remove(i);
                    --i;
                }
                else {
                    if (syncData.reqTime == 0L) {
                        syncData.reqTime = System.currentTimeMillis();
                        ++n;
                        bb.putInt(syncData.x);
                        bb.putInt(syncData.y);
                        bb.putLong(syncData.hashCodeWorldObjects);
                        final SyncData syncData2 = syncData;
                        ++syncData2.reqCount;
                    }
                    if (System.currentTimeMillis() - syncData.reqTime >= this.timeout) {
                        syncData.reqTime = System.currentTimeMillis();
                        ++n;
                        bb.putInt(syncData.x);
                        bb.putInt(syncData.y);
                        bb.putLong(syncData.hashCodeWorldObjects);
                        final SyncData syncData3 = syncData;
                        ++syncData3.reqCount;
                    }
                    if (n >= 50) {
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
        PacketTypes.PacketType.SyncWorldObjectsReq.send(GameClient.connection);
    }
    
    public void receiveIsoSync(final int n, final int n2) {
        synchronized (this.requests) {
            for (int i = 0; i < this.requests.size(); ++i) {
                final SyncData syncData = this.requests.get(i);
                if (syncData.x == n && syncData.y == n2) {
                    this.requests.remove(i);
                }
            }
        }
    }
    
    private class SyncData
    {
        int x;
        int y;
        long hashCodeWorldObjects;
        long reqTime;
        int reqCount;
    }
}

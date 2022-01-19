// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import java.nio.ByteBuffer;
import zombie.core.network.ByteBufferWriter;
import zombie.network.PacketTypes;
import zombie.core.raknet.UdpConnection;
import zombie.SystemDisabler;
import zombie.network.GameClient;
import java.util.ArrayList;

public final class IsoObjectSyncRequests
{
    public final ArrayList<SyncData> requests;
    public long timeout;
    
    public IsoObjectSyncRequests() {
        this.requests = new ArrayList<SyncData>();
        this.timeout = 1000L;
    }
    
    public void putRequest(final IsoGridSquare isoGridSquare, final IsoObject isoObject) {
        if (GameClient.bClient) {
            this.putRequest(isoGridSquare.x, isoGridSquare.y, isoGridSquare.z, (byte)isoGridSquare.getObjects().indexOf(isoObject));
        }
    }
    
    public void putRequestLoad(final IsoGridSquare isoGridSquare) {
        if (GameClient.bClient) {
            this.putRequest(isoGridSquare.x, isoGridSquare.y, isoGridSquare.z, (byte)isoGridSquare.getObjects().size());
        }
    }
    
    public void putRequest(final int x, final int y, final int z, final byte objIndex) {
        if (!SystemDisabler.doObjectStateSyncEnable) {
            return;
        }
        final SyncData e = new SyncData();
        e.x = x;
        e.y = y;
        e.z = z;
        e.objIndex = objIndex;
        e.reqTime = 0L;
        e.reqCount = 0;
        synchronized (this.requests) {
            this.requests.add(e);
        }
    }
    
    public void sendRequests(final UdpConnection udpConnection) {
        if (!SystemDisabler.doObjectStateSyncEnable) {
            return;
        }
        if (this.requests.size() == 0) {
            return;
        }
        final ByteBufferWriter startPacket = udpConnection.startPacket();
        PacketTypes.PacketType.SyncIsoObjectReq.doPacket(startPacket);
        final ByteBuffer bb = startPacket.bb;
        final int position = bb.position();
        startPacket.putShort((short)0);
        int n = 0;
        synchronized (this.requests) {
            for (int i = 0; i < this.requests.size(); ++i) {
                final SyncData syncData = this.requests.get(i);
                if (syncData.reqCount > 4) {
                    this.requests.remove(i);
                    --i;
                }
                else {
                    if (syncData.reqTime == 0L) {
                        syncData.reqTime = System.currentTimeMillis();
                        ++n;
                        bb.putInt(syncData.x);
                        bb.putInt(syncData.y);
                        bb.putInt(syncData.z);
                        bb.put(syncData.objIndex);
                        final SyncData syncData2 = syncData;
                        ++syncData2.reqCount;
                    }
                    if (System.currentTimeMillis() - syncData.reqTime >= this.timeout) {
                        syncData.reqTime = System.currentTimeMillis();
                        ++n;
                        bb.putInt(syncData.x);
                        bb.putInt(syncData.y);
                        bb.putInt(syncData.z);
                        bb.put(syncData.objIndex);
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
        PacketTypes.PacketType.SyncIsoObjectReq.send(GameClient.connection);
    }
    
    public void receiveIsoSync(final int n, final int n2, final int n3, final byte b) {
        synchronized (this.requests) {
            for (int i = 0; i < this.requests.size(); ++i) {
                final SyncData syncData = this.requests.get(i);
                if (syncData.x == n && syncData.y == n2 && syncData.z == n3 && syncData.objIndex == b) {
                    this.requests.remove(i);
                }
            }
        }
    }
    
    private class SyncData
    {
        int x;
        int y;
        int z;
        byte objIndex;
        long reqTime;
        int reqCount;
    }
}

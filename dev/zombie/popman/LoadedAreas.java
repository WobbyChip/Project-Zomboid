// 
// Decompiled by Procyon v0.5.36
// 

package zombie.popman;

import zombie.iso.Vector3;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoWorld;
import zombie.core.raknet.UdpConnection;
import zombie.characters.IsoPlayer;
import zombie.network.ServerMap;
import zombie.network.GameServer;

final class LoadedAreas
{
    public static final int MAX_AREAS = 64;
    public int[] areas;
    public int count;
    public boolean changed;
    public int[] prevAreas;
    public int prevCount;
    private boolean serverCells;
    
    public LoadedAreas(final boolean serverCells) {
        this.areas = new int[256];
        this.prevAreas = new int[256];
        this.serverCells = serverCells;
    }
    
    public boolean set() {
        this.setPrev();
        this.clear();
        if (GameServer.bServer) {
            if (this.serverCells) {
                for (int i = 0; i < ServerMap.instance.LoadedCells.size(); ++i) {
                    final ServerMap.ServerCell serverCell = ServerMap.instance.LoadedCells.get(i);
                    this.add(serverCell.WX * 5, serverCell.WY * 5, 5, 5);
                }
            }
            else {
                for (int j = 0; j < GameServer.Players.size(); ++j) {
                    final IsoPlayer isoPlayer = GameServer.Players.get(j);
                    this.add((int)isoPlayer.x / 10 - isoPlayer.OnlineChunkGridWidth / 2, (int)isoPlayer.y / 10 - isoPlayer.OnlineChunkGridWidth / 2, isoPlayer.OnlineChunkGridWidth, isoPlayer.OnlineChunkGridWidth);
                }
                for (int k = 0; k < GameServer.udpEngine.connections.size(); ++k) {
                    final UdpConnection udpConnection = GameServer.udpEngine.connections.get(k);
                    for (int l = 0; l < 4; ++l) {
                        final Vector3 vector3 = udpConnection.connectArea[l];
                        if (vector3 != null) {
                            final int n = (int)vector3.z;
                            this.add((int)vector3.x - n / 2, (int)vector3.y - n / 2, n, n);
                        }
                    }
                }
            }
        }
        else {
            for (int n2 = 0; n2 < IsoPlayer.numPlayers; ++n2) {
                final IsoChunkMap isoChunkMap = IsoWorld.instance.CurrentCell.ChunkMap[n2];
                if (!isoChunkMap.ignore) {
                    this.add(isoChunkMap.getWorldXMin(), isoChunkMap.getWorldYMin(), IsoChunkMap.ChunkGridWidth, IsoChunkMap.ChunkGridWidth);
                }
            }
        }
        return this.changed = this.compareWithPrev();
    }
    
    public void add(final int n, final int n2, final int n3, final int n4) {
        if (this.count >= 64) {
            return;
        }
        int n5 = this.count * 4;
        this.areas[n5++] = n;
        this.areas[n5++] = n2;
        this.areas[n5++] = n3;
        this.areas[n5++] = n4;
        ++this.count;
    }
    
    public void clear() {
        this.count = 0;
        this.changed = false;
    }
    
    public void copy(final LoadedAreas loadedAreas) {
        this.count = loadedAreas.count;
        for (int i = 0; i < this.count; ++i) {
            int n = i * 4;
            this.areas[n] = loadedAreas.areas[n++];
            this.areas[n] = loadedAreas.areas[n++];
            this.areas[n] = loadedAreas.areas[n++];
            this.areas[n] = loadedAreas.areas[n++];
        }
    }
    
    private void setPrev() {
        this.prevCount = this.count;
        for (int i = 0; i < this.count; ++i) {
            int n = i * 4;
            this.prevAreas[n] = this.areas[n++];
            this.prevAreas[n] = this.areas[n++];
            this.prevAreas[n] = this.areas[n++];
            this.prevAreas[n] = this.areas[n++];
        }
    }
    
    private boolean compareWithPrev() {
        if (this.prevCount != this.count) {
            return true;
        }
        for (int i = 0; i < this.count; ++i) {
            int n = i * 4;
            if (this.prevAreas[n] != this.areas[n++]) {
                return true;
            }
            if (this.prevAreas[n] != this.areas[n++]) {
                return true;
            }
            if (this.prevAreas[n] != this.areas[n++]) {
                return true;
            }
            if (this.prevAreas[n] != this.areas[n++]) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isOnEdge(final int n, final int n2) {
        if (n % 10 != 0 && (n + 1) % 10 != 0 && n2 % 10 != 0 && (n2 + 1) % 10 != 0) {
            return false;
        }
        for (int i = 0; i < this.count; ++i) {
            int n3 = i * 4;
            final int n4 = this.areas[n3++] * 10;
            final int n5 = this.areas[n3++] * 10;
            final int n6 = n4 + this.areas[n3++] * 10;
            final int n7 = n5 + this.areas[n3++] * 10;
            final boolean b = n >= n4 && n < n6;
            final boolean b2 = n2 >= n5 && n2 < n7;
            if (b && (n2 == n5 || n2 == n7 - 1)) {
                return true;
            }
            if (b2 && (n == n4 || n == n6 - 1)) {
                return true;
            }
        }
        return false;
    }
}

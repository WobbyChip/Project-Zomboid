// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network;

import java.util.Arrays;
import zombie.iso.IsoChunk;
import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;
import zombie.iso.IsoUtils;
import zombie.core.SpriteRenderer;
import zombie.core.Core;
import zombie.iso.IsoWorld;
import zombie.iso.IsoChunkMap;
import java.nio.ByteBuffer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.characters.IsoPlayer;
import zombie.core.textures.Texture;

public final class ClientServerMap
{
    private static final int ChunksPerServerCell = 5;
    private static final int SquaresPerServerCell = 50;
    int playerIndex;
    int centerX;
    int centerY;
    int chunkGridWidth;
    int width;
    boolean[] loaded;
    private static boolean[] isLoaded;
    private static Texture trafficCone;
    
    public ClientServerMap(final int playerIndex, final int centerX, final int centerY, final int chunkGridWidth) {
        this.playerIndex = playerIndex;
        this.centerX = centerX;
        this.centerY = centerY;
        this.chunkGridWidth = chunkGridWidth;
        this.width = (chunkGridWidth - 1) * 10 / 50;
        if ((chunkGridWidth - 1) * 10 % 50 != 0) {
            ++this.width;
        }
        ++this.width;
        this.loaded = new boolean[this.width * this.width];
    }
    
    public int getMinX() {
        return (this.centerX / 10 - this.chunkGridWidth / 2) / 5;
    }
    
    public int getMinY() {
        return (this.centerY / 10 - this.chunkGridWidth / 2) / 5;
    }
    
    public int getMaxX() {
        return this.getMinX() + this.width - 1;
    }
    
    public int getMaxY() {
        return this.getMinY() + this.width - 1;
    }
    
    public boolean isValidCell(final int n, final int n2) {
        return n >= 0 && n2 >= 0 && n < this.width && n2 < this.width;
    }
    
    public boolean setLoaded() {
        if (!GameServer.bServer) {
            return false;
        }
        final int minX = ServerMap.instance.getMinX();
        final int minY = ServerMap.instance.getMinY();
        final int minX2 = this.getMinX();
        final int minY2 = this.getMinY();
        boolean b = false;
        for (int i = 0; i < this.width; ++i) {
            for (int j = 0; j < this.width; ++j) {
                final ServerMap.ServerCell cell = ServerMap.instance.getCell(minX2 + j - minX, minY2 + i - minY);
                final boolean b2 = cell != null && cell.bLoaded;
                b |= (this.loaded[j + i * this.width] != b2);
                this.loaded[j + i * this.width] = b2;
            }
        }
        return b;
    }
    
    public boolean setPlayerPosition(final int centerX, final int centerY) {
        if (!GameServer.bServer) {
            return false;
        }
        final int minX = this.getMinX();
        final int minY = this.getMinY();
        this.centerX = centerX;
        this.centerY = centerY;
        return this.setLoaded() || minX != this.getMinX() || minY != this.getMinY();
    }
    
    public static boolean isChunkLoaded(final int n, final int n2) {
        if (!GameClient.bClient) {
            return false;
        }
        if (n < 0 || n2 < 0) {
            return false;
        }
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            final ClientServerMap clientServerMap = GameClient.loadedCells[i];
            if (clientServerMap != null) {
                final int n3 = n / 5 - clientServerMap.getMinX();
                final int n4 = n2 / 5 - clientServerMap.getMinY();
                if (clientServerMap.isValidCell(n3, n4) && clientServerMap.loaded[n3 + n4 * clientServerMap.width]) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static void characterIn(final UdpConnection udpConnection, final int n) {
        if (!GameServer.bServer) {
            return;
        }
        final ClientServerMap clientServerMap = udpConnection.loadedCells[n];
        if (clientServerMap == null) {
            return;
        }
        final IsoPlayer isoPlayer = udpConnection.players[n];
        if (isoPlayer == null) {
            return;
        }
        if (clientServerMap.setPlayerPosition((int)isoPlayer.x, (int)isoPlayer.y)) {
            clientServerMap.sendPacket(udpConnection);
        }
    }
    
    public void sendPacket(final UdpConnection udpConnection) {
        if (!GameServer.bServer) {
            return;
        }
        final ByteBufferWriter startPacket = udpConnection.startPacket();
        PacketTypes.PacketType.ServerMap.doPacket(startPacket);
        startPacket.putByte((byte)this.playerIndex);
        startPacket.putInt(this.centerX);
        startPacket.putInt(this.centerY);
        for (int i = 0; i < this.width; ++i) {
            for (int j = 0; j < this.width; ++j) {
                startPacket.putBoolean(this.loaded[j + i * this.width]);
            }
        }
        PacketTypes.PacketType.ServerMap.send(udpConnection);
    }
    
    public static void receivePacket(final ByteBuffer byteBuffer) {
        if (!GameClient.bClient) {
            return;
        }
        final byte value = byteBuffer.get();
        final int int1 = byteBuffer.getInt();
        final int int2 = byteBuffer.getInt();
        ClientServerMap clientServerMap = GameClient.loadedCells[value];
        if (clientServerMap == null) {
            final ClientServerMap[] loadedCells = GameClient.loadedCells;
            final byte b = value;
            final ClientServerMap clientServerMap2 = new ClientServerMap(value, int1, int2, IsoChunkMap.ChunkGridWidth);
            loadedCells[b] = clientServerMap2;
            clientServerMap = clientServerMap2;
        }
        clientServerMap.centerX = int1;
        clientServerMap.centerY = int2;
        for (int i = 0; i < clientServerMap.width; ++i) {
            for (int j = 0; j < clientServerMap.width; ++j) {
                clientServerMap.loaded[j + i * clientServerMap.width] = (byteBuffer.get() == 1);
            }
        }
    }
    
    public static void render(final int n) {
        if (!GameClient.bClient) {
            return;
        }
        final IsoChunkMap chunkMap = IsoWorld.instance.CurrentCell.getChunkMap(n);
        if (chunkMap == null || chunkMap.ignore) {
            return;
        }
        final int tileScale = Core.TileScale;
        final int n2 = 10;
        final float n3 = 0.1f;
        final float n4 = 0.1f;
        final float n5 = 0.1f;
        final float n6 = 0.75f;
        final float n7 = 0.0f;
        if (ClientServerMap.trafficCone == null) {
            ClientServerMap.trafficCone = Texture.getSharedTexture("street_decoration_01_26");
        }
        final Texture trafficCone = ClientServerMap.trafficCone;
        if (ClientServerMap.isLoaded == null || ClientServerMap.isLoaded.length < IsoChunkMap.ChunkGridWidth * IsoChunkMap.ChunkGridWidth) {
            ClientServerMap.isLoaded = new boolean[IsoChunkMap.ChunkGridWidth * IsoChunkMap.ChunkGridWidth];
        }
        for (int i = 0; i < IsoChunkMap.ChunkGridWidth; ++i) {
            for (int j = 0; j < IsoChunkMap.ChunkGridWidth; ++j) {
                final IsoChunk chunk = chunkMap.getChunk(j, i);
                if (chunk != null) {
                    ClientServerMap.isLoaded[j + i * IsoChunkMap.ChunkGridWidth] = isChunkLoaded(chunk.wx, chunk.wy);
                }
            }
        }
        for (int k = 0; k < IsoChunkMap.ChunkGridWidth; ++k) {
            for (int l = 0; l < IsoChunkMap.ChunkGridWidth; ++l) {
                final IsoChunk chunk2 = chunkMap.getChunk(l, k);
                if (chunk2 != null) {
                    final boolean b = ClientServerMap.isLoaded[l + k * IsoChunkMap.ChunkGridWidth];
                    if (b && trafficCone != null) {
                        if (chunkMap.getChunk(l, k - 1) != null && !ClientServerMap.isLoaded[l + (k - 1) * IsoChunkMap.ChunkGridWidth]) {
                            for (int n8 = 0; n8 < n2; ++n8) {
                                SpriteRenderer.instance.render(trafficCone, IsoUtils.XToScreenExact((float)(chunk2.wx * n2 + n8), (float)(chunk2.wy * n2), n7, 0) - trafficCone.getWidth() / 2, IsoUtils.YToScreenExact((float)(chunk2.wx * n2 + n8), (float)(chunk2.wy * n2), n7, 0), (float)trafficCone.getWidth(), (float)trafficCone.getHeight(), 1.0f, 1.0f, 1.0f, 1.0f, null);
                            }
                        }
                        if (chunkMap.getChunk(l, k + 1) != null && !ClientServerMap.isLoaded[l + (k + 1) * IsoChunkMap.ChunkGridWidth]) {
                            for (int n9 = 0; n9 < n2; ++n9) {
                                SpriteRenderer.instance.render(trafficCone, IsoUtils.XToScreenExact((float)(chunk2.wx * n2 + n9), (float)(chunk2.wy * n2 + (n2 - 1)), n7, 0) - trafficCone.getWidth() / 2, IsoUtils.YToScreenExact((float)(chunk2.wx * n2 + n9), (float)(chunk2.wy * n2 + (n2 - 1)), n7, 0), (float)trafficCone.getWidth(), (float)trafficCone.getHeight(), 1.0f, 1.0f, 1.0f, 1.0f, null);
                            }
                        }
                        if (chunkMap.getChunk(l - 1, k) != null && !ClientServerMap.isLoaded[l - 1 + k * IsoChunkMap.ChunkGridWidth]) {
                            for (int n10 = 0; n10 < n2; ++n10) {
                                SpriteRenderer.instance.render(trafficCone, IsoUtils.XToScreenExact((float)(chunk2.wx * n2), (float)(chunk2.wy * n2 + n10), n7, 0) - trafficCone.getWidth() / 2, IsoUtils.YToScreenExact((float)(chunk2.wx * n2), (float)(chunk2.wy * n2 + n10), n7, 0), (float)trafficCone.getWidth(), (float)trafficCone.getHeight(), 1.0f, 1.0f, 1.0f, 1.0f, null);
                            }
                        }
                        if (chunkMap.getChunk(l + 1, k) != null && !ClientServerMap.isLoaded[l + 1 + k * IsoChunkMap.ChunkGridWidth]) {
                            for (int n11 = 0; n11 < n2; ++n11) {
                                SpriteRenderer.instance.render(trafficCone, IsoUtils.XToScreenExact((float)(chunk2.wx * n2 + (n2 - 1)), (float)(chunk2.wy * n2 + n11), n7, 0) - trafficCone.getWidth() / 2, IsoUtils.YToScreenExact((float)(chunk2.wx * n2 + (n2 - 1)), (float)(chunk2.wy * n2 + n11), n7, 0), (float)trafficCone.getWidth(), (float)trafficCone.getHeight(), 1.0f, 1.0f, 1.0f, 1.0f, null);
                            }
                        }
                    }
                    if (!b) {
                        final float n12 = (float)(chunk2.wx * n2);
                        final float n13 = (float)(chunk2.wy * n2);
                        final float xToScreenExact = IsoUtils.XToScreenExact(n12, n13 + n2, n7, 0);
                        final float yToScreenExact = IsoUtils.YToScreenExact(n12, n13 + n2, n7, 0);
                        SpriteRenderer.instance.renderPoly((float)(int)xToScreenExact, (float)(int)yToScreenExact, (float)(int)(xToScreenExact + n2 * 64 / 2 * tileScale), (float)(int)(yToScreenExact - n2 * 32 / 2 * tileScale), (float)(int)(xToScreenExact + n2 * 64 * tileScale), (float)(int)yToScreenExact, (float)(int)(xToScreenExact + n2 * 64 / 2 * tileScale), (float)(int)(yToScreenExact + n2 * 32 / 2 * tileScale), n3, n4, n5, n6);
                    }
                }
            }
        }
    }
    
    public static void Reset() {
        Arrays.fill(GameClient.loadedCells, null);
        ClientServerMap.trafficCone = null;
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network;

import zombie.iso.IsoChunk;
import org.joml.Vector3f;
import zombie.iso.IsoChunkMap;
import zombie.core.SpriteRenderer;
import zombie.iso.IsoUtils;
import zombie.core.Core;
import zombie.vehicles.BaseVehicle;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import java.nio.ByteBuffer;
import zombie.iso.IsoWorld;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;

public final class PassengerMap
{
    private static final int CHUNKS = 7;
    private static final int MAX_PASSENGERS = 16;
    private static final PassengerLocal[] perPlayerPngr;
    private static final DriverLocal[] perPlayerDriver;
    
    public static void updatePassenger(final IsoPlayer isoPlayer) {
        if (isoPlayer == null || isoPlayer.getVehicle() == null || isoPlayer.getVehicle().isDriver(isoPlayer)) {
            return;
        }
        final IsoGameCharacter driver = isoPlayer.getVehicle().getDriver();
        if (!(driver instanceof IsoPlayer) || ((IsoPlayer)driver).isLocalPlayer()) {
            return;
        }
        final PassengerLocal passengerLocal = PassengerMap.perPlayerPngr[isoPlayer.PlayerIndex];
        passengerLocal.chunkMap = IsoWorld.instance.CurrentCell.ChunkMap[isoPlayer.PlayerIndex];
        passengerLocal.updateLoaded();
    }
    
    public static void serverReceivePacket(final ByteBuffer byteBuffer, final UdpConnection udpConnection) {
        final byte value = byteBuffer.get();
        final int int1 = byteBuffer.getInt();
        final int int2 = byteBuffer.getInt();
        final long long1 = byteBuffer.getLong();
        final IsoPlayer isoPlayer = udpConnection.players[value];
        if (isoPlayer == null || isoPlayer.getVehicle() == null) {
            return;
        }
        final IsoGameCharacter driver = isoPlayer.getVehicle().getDriver();
        if (!(driver instanceof IsoPlayer) || driver == isoPlayer) {
            return;
        }
        final UdpConnection connectionFromPlayer = GameServer.getConnectionFromPlayer((IsoPlayer)driver);
        if (connectionFromPlayer == null) {
            return;
        }
        final ByteBufferWriter startPacket = connectionFromPlayer.startPacket();
        PacketTypes.PacketType.PassengerMap.doPacket(startPacket);
        startPacket.putShort(isoPlayer.getVehicle().VehicleID);
        startPacket.putByte((byte)isoPlayer.getVehicle().getSeat(isoPlayer));
        startPacket.putInt(int1);
        startPacket.putInt(int2);
        startPacket.putLong(long1);
        PacketTypes.PacketType.PassengerMap.send(connectionFromPlayer);
    }
    
    public static void clientReceivePacket(final ByteBuffer byteBuffer) {
        final short short1 = byteBuffer.getShort();
        final byte value = byteBuffer.get();
        final int int1 = byteBuffer.getInt();
        final int int2 = byteBuffer.getInt();
        final long long1 = byteBuffer.getLong();
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            final IsoPlayer isoPlayer = IsoPlayer.players[i];
            if (isoPlayer != null) {
                if (isoPlayer.getVehicle() != null) {
                    final BaseVehicle vehicle = isoPlayer.getVehicle();
                    if (vehicle.VehicleID == short1) {
                        if (vehicle.isDriver(isoPlayer)) {
                            final DriverLocal driverLocal = PassengerMap.perPlayerDriver[i];
                            PassengerRemote passengerRemote = driverLocal.passengers[value];
                            if (passengerRemote == null) {
                                final PassengerRemote[] passengers = driverLocal.passengers;
                                final byte b = value;
                                final PassengerRemote passengerRemote2 = new PassengerRemote();
                                passengers[b] = passengerRemote2;
                                passengerRemote = passengerRemote2;
                            }
                            passengerRemote.setLoaded(int1, int2, long1);
                        }
                    }
                }
            }
        }
    }
    
    public static boolean isChunkLoaded(final BaseVehicle baseVehicle, final int n, final int n2) {
        if (!GameClient.bClient) {
            return false;
        }
        if (baseVehicle == null || n < 0 || n2 < 0) {
            return false;
        }
        final IsoGameCharacter driver = baseVehicle.getDriver();
        if (!(driver instanceof IsoPlayer) || !((IsoPlayer)driver).isLocalPlayer()) {
            return false;
        }
        final DriverLocal driverLocal = PassengerMap.perPlayerDriver[((IsoPlayer)driver).PlayerIndex];
        for (int i = 1; i < baseVehicle.getMaxPassengers(); ++i) {
            final PassengerRemote passengerRemote = driverLocal.passengers[i];
            if (passengerRemote != null) {
                if (passengerRemote.wx != -1) {
                    final IsoGameCharacter character = baseVehicle.getCharacter(i);
                    if (!(character instanceof IsoPlayer) || ((IsoPlayer)character).isLocalPlayer()) {
                        passengerRemote.wx = -1;
                    }
                    else {
                        final int n3 = passengerRemote.wx - 3;
                        final int n4 = passengerRemote.wy - 3;
                        if (n >= n3 && n2 >= n4 && n < n3 + 7) {
                            if (n2 < n4 + 7) {
                                if ((passengerRemote.loaded & 1L << n - n3 + (n2 - n4) * 7) == 0x0L) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
    
    public static void render(final int n) {
        if (!GameClient.bClient) {
            return;
        }
        final IsoPlayer isoPlayer = IsoPlayer.players[n];
        if (isoPlayer == null || isoPlayer.getVehicle() == null) {
            return;
        }
        final BaseVehicle vehicle = isoPlayer.getVehicle();
        final int tileScale = Core.TileScale;
        final int n2 = 10;
        final float n3 = 0.1f;
        final float n4 = 0.1f;
        final float n5 = 0.1f;
        final float n6 = 0.75f;
        final float n7 = 0.0f;
        final DriverLocal driverLocal = PassengerMap.perPlayerDriver[n];
        for (int i = 1; i < vehicle.getMaxPassengers(); ++i) {
            final PassengerRemote passengerRemote = driverLocal.passengers[i];
            if (passengerRemote != null) {
                if (passengerRemote.wx != -1) {
                    final IsoGameCharacter character = vehicle.getCharacter(i);
                    if (!(character instanceof IsoPlayer) || ((IsoPlayer)character).isLocalPlayer()) {
                        passengerRemote.wx = -1;
                    }
                    else {
                        for (int j = 0; j < 7; ++j) {
                            for (int k = 0; k < 7; ++k) {
                                if ((passengerRemote.loaded & 1L << k + j * 7) == 0x0L) {
                                    final float n8 = (float)((passengerRemote.wx - 3 + k) * n2);
                                    final float n9 = (float)((passengerRemote.wy - 3 + j) * n2);
                                    final float xToScreenExact = IsoUtils.XToScreenExact(n8, n9 + n2, n7, 0);
                                    final float yToScreenExact = IsoUtils.YToScreenExact(n8, n9 + n2, n7, 0);
                                    SpriteRenderer.instance.renderPoly((float)(int)xToScreenExact, (float)(int)yToScreenExact, (float)(int)(xToScreenExact + n2 * 64 / 2 * tileScale), (float)(int)(yToScreenExact - n2 * 32 / 2 * tileScale), (float)(int)(xToScreenExact + n2 * 64 * tileScale), (float)(int)yToScreenExact, (float)(int)(xToScreenExact + n2 * 64 / 2 * tileScale), (float)(int)(yToScreenExact + n2 * 32 / 2 * tileScale), n3, n4, n5, n6);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public static void Reset() {
        for (int i = 0; i < 4; ++i) {
            PassengerMap.perPlayerPngr[i].wx = -1;
            final DriverLocal driverLocal = PassengerMap.perPlayerDriver[i];
            for (int j = 0; j < 16; ++j) {
                final PassengerRemote passengerRemote = driverLocal.passengers[j];
                if (passengerRemote != null) {
                    passengerRemote.wx = -1;
                }
            }
        }
    }
    
    static {
        perPlayerPngr = new PassengerLocal[4];
        perPlayerDriver = new DriverLocal[4];
        for (int i = 0; i < 4; ++i) {
            PassengerMap.perPlayerPngr[i] = new PassengerLocal(i);
            PassengerMap.perPlayerDriver[i] = new DriverLocal();
        }
    }
    
    private static final class PassengerLocal
    {
        final int playerIndex;
        IsoChunkMap chunkMap;
        int wx;
        int wy;
        long loaded;
        
        PassengerLocal(final int playerIndex) {
            this.wx = -1;
            this.wy = -1;
            this.loaded = 0L;
            this.playerIndex = playerIndex;
        }
        
        boolean setLoaded() {
            int worldX = this.chunkMap.WorldX;
            int worldY = this.chunkMap.WorldY;
            final Vector3f jniLinearVelocity = IsoPlayer.players[this.playerIndex].getVehicle().jniLinearVelocity;
            final float abs = Math.abs(jniLinearVelocity.x);
            final float abs2 = Math.abs(jniLinearVelocity.z);
            final boolean b = jniLinearVelocity.x < 0.0f && abs > abs2;
            final boolean b2 = jniLinearVelocity.x > 0.0f && abs > abs2;
            final boolean b3 = jniLinearVelocity.z < 0.0f && abs2 > abs;
            final boolean b4 = jniLinearVelocity.z > 0.0f && abs2 > abs;
            if (b2) {
                ++worldX;
            }
            else if (b) {
                --worldX;
            }
            else if (b3) {
                --worldY;
            }
            else if (b4) {
                ++worldY;
            }
            long loaded = 0L;
            for (int i = 0; i < 7; ++i) {
                for (int j = 0; j < 7; ++j) {
                    final IsoChunk chunk = this.chunkMap.getChunk(IsoChunkMap.ChunkGridWidth / 2 - 3 + j, IsoChunkMap.ChunkGridWidth / 2 - 3 + i);
                    if (chunk != null && chunk.bLoaded) {
                        loaded |= 1L << j + i * 7;
                    }
                }
            }
            final boolean b5 = worldX != this.wx || worldY != this.wy || loaded != this.loaded;
            if (b5) {
                this.wx = worldX;
                this.wy = worldY;
                this.loaded = loaded;
            }
            return b5;
        }
        
        void updateLoaded() {
            if (this.setLoaded()) {
                this.clientSendPacket(GameClient.connection);
            }
        }
        
        void clientSendPacket(final UdpConnection udpConnection) {
            final ByteBufferWriter startPacket = udpConnection.startPacket();
            PacketTypes.PacketType.PassengerMap.doPacket(startPacket);
            startPacket.putByte((byte)this.playerIndex);
            startPacket.putInt(this.wx);
            startPacket.putInt(this.wy);
            startPacket.putLong(this.loaded);
            PacketTypes.PacketType.PassengerMap.send(udpConnection);
        }
    }
    
    private static final class PassengerRemote
    {
        int wx;
        int wy;
        long loaded;
        
        private PassengerRemote() {
            this.wx = -1;
            this.wy = -1;
            this.loaded = 0L;
        }
        
        void setLoaded(final int wx, final int wy, final long loaded) {
            this.wx = wx;
            this.wy = wy;
            this.loaded = loaded;
        }
    }
    
    private static final class DriverLocal
    {
        final PassengerRemote[] passengers;
        
        private DriverLocal() {
            this.passengers = new PassengerRemote[16];
        }
    }
}

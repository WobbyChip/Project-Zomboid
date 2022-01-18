// 
// Decompiled by Procyon v0.5.36
// 

package zombie.popman;

import zombie.core.network.ByteBufferWriter;
import zombie.network.PacketTypes;
import zombie.network.GameClient;

final class DebugCommands
{
    protected static final byte PKT_LOADED = 1;
    protected static final byte PKT_REPOP = 2;
    protected static final byte PKT_SPAWN_TIME_TO_ZERO = 3;
    protected static final byte PKT_CLEAR_ZOMBIES = 4;
    protected static final byte PKT_SPAWN_NOW = 5;
    
    private static native void n_debugCommand(final int p0, final int p1, final int p2);
    
    public void SpawnTimeToZero(final int n, final int n2) {
        if (ZombiePopulationManager.instance.bClient) {
            final ByteBufferWriter startPacket = GameClient.connection.startPacket();
            PacketTypes.PacketType.KeepAlive.doPacket(startPacket);
            startPacket.bb.put((byte)3);
            startPacket.bb.putShort((short)n);
            startPacket.bb.putShort((short)n2);
            PacketTypes.PacketType.KeepAlive.send(GameClient.connection);
            return;
        }
        n_debugCommand(3, n, n2);
    }
    
    public void ClearZombies(final int n, final int n2) {
        if (ZombiePopulationManager.instance.bClient) {
            final ByteBufferWriter startPacket = GameClient.connection.startPacket();
            PacketTypes.PacketType.KeepAlive.doPacket(startPacket);
            startPacket.bb.put((byte)4);
            startPacket.bb.putShort((short)n);
            startPacket.bb.putShort((short)n2);
            PacketTypes.PacketType.KeepAlive.send(GameClient.connection);
            return;
        }
        n_debugCommand(4, n, n2);
    }
    
    public void SpawnNow(final int n, final int n2) {
        if (ZombiePopulationManager.instance.bClient) {
            final ByteBufferWriter startPacket = GameClient.connection.startPacket();
            PacketTypes.PacketType.KeepAlive.doPacket(startPacket);
            startPacket.bb.put((byte)5);
            startPacket.bb.putShort((short)n);
            startPacket.bb.putShort((short)n2);
            PacketTypes.PacketType.KeepAlive.send(GameClient.connection);
            return;
        }
        n_debugCommand(5, n, n2);
    }
}

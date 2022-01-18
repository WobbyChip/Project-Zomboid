// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network;

import zombie.core.network.ByteBufferWriter;

public class PacketTypesShort
{
    public static final short WaveSignal = 1000;
    public static final short PlayerListensChannel = 1001;
    public static final short RadioServerData = 1002;
    public static final short RadioDeviceTurnedOnState = 1003;
    public static final short RadioDeviceDataState = 1004;
    public static final short RadioDeviceState = 1020;
    public static final short SyncCustomLightSettings = 1200;
    
    public static void doPacket(final short n, final ByteBufferWriter byteBufferWriter) {
        PacketTypes.PacketType.PacketTypeShort.doPacket(byteBufferWriter);
        byteBufferWriter.putShort(n);
    }
}

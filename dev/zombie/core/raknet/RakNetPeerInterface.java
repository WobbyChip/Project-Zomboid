// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.raknet;

import zombie.core.znet.ZNetStatistics;
import zombie.debug.DebugLog;
import zombie.Lua.LuaEventManager;
import zombie.core.znet.ZNetFileChunk;
import zombie.core.znet.ZNetFileAnnounce;
import java.util.concurrent.locks.ReentrantLock;
import org.lwjglx.BufferUtils;
import java.util.concurrent.locks.Lock;
import java.nio.ByteBuffer;

public class RakNetPeerInterface
{
    private static Thread mainThread;
    public static final int ID_NEW_INCOMING_CONNECTION = 19;
    public static final int ID_DISCONNECTION_NOTIFICATION = 21;
    public static final int ID_INCOMPATIBLE_PROTOCOL_VERSION = 25;
    public static final int ID_CONNECTED_PING = 0;
    public static final int ID_UNCONNECTED_PING = 1;
    public static final int ID_CONNECTION_LOST = 22;
    public static final int ID_ALREADY_CONNECTED = 18;
    public static final int ID_REMOTE_DISCONNECTION_NOTIFICATION = 31;
    public static final int ID_REMOTE_CONNECTION_LOST = 32;
    public static final int ID_REMOTE_NEW_INCOMING_CONNECTION = 33;
    public static final int ID_CONNECTION_BANNED = 23;
    public static final int ID_CONNECTION_ATTEMPT_FAILED = 17;
    public static final int ID_NO_FREE_INCOMING_CONNECTIONS = 20;
    public static final int ID_CONNECTION_REQUEST_ACCEPTED = 16;
    public static final int ID_INVALID_PASSWORD = 24;
    public static final int ID_TIMESTAMP = 27;
    public static final int ID_PING = 28;
    public static final int ID_RAKVOICE_OPEN_CHANNEL_REQUEST = 44;
    public static final int ID_RAKVOICE_OPEN_CHANNEL_REPLY = 45;
    public static final int ID_RAKVOICE_CLOSE_CHANNEL = 46;
    public static final int ID_RAKVOICE_DATA = 47;
    public static final int ID_USER_PACKET_ENUM = 134;
    public static final int PacketPriority_IMMEDIATE = 0;
    public static final int PacketPriority_HIGH = 1;
    public static final int PacketPriority_MEDIUM = 2;
    public static final int PacketPriority_LOW = 3;
    public static final int PacketReliability_UNRELIABLE = 0;
    public static final int PacketReliability_UNRELIABLE_SEQUENCED = 1;
    public static final int PacketReliability_RELIABLE = 2;
    public static final int PacketReliability_RELIABLE_ORDERED = 3;
    public static final int PacketReliability_RELIABLE_SEQUENCED = 4;
    public static final int PacketReliability_UNRELIABLE_WITH_ACK_RECEIPT = 5;
    public static final int PacketReliability_RELIABLE_WITH_ACK_RECEIPT = 6;
    public static final int PacketReliability_RELIABLE_ORDERED_WITH_ACK_RECEIPT = 7;
    ByteBuffer receiveBuf;
    ByteBuffer sendBuf;
    Lock sendLock;
    
    public RakNetPeerInterface() {
        this.receiveBuf = BufferUtils.createByteBuffer(1000000);
        this.sendBuf = BufferUtils.createByteBuffer(1000000);
        this.sendLock = new ReentrantLock();
    }
    
    public static void init() {
        RakNetPeerInterface.mainThread = Thread.currentThread();
    }
    
    public native void Init(final boolean p0);
    
    public native int Startup(final int p0);
    
    public native void Shutdown();
    
    public native void SetServerIP(final String p0);
    
    public native void SetServerPort(final int p0);
    
    public native void SetClientPort(final int p0);
    
    public native int Connect(final String p0, final int p1, final String p2);
    
    public native int ConnectToSteamServer(final long p0, final String p1);
    
    public native String GetServerIP();
    
    public native long GetClientSteamID(final long p0);
    
    public native long GetClientOwnerSteamID(final long p0);
    
    public native void SetIncomingPassword(final String p0);
    
    public native void SetTimeoutTime(final int p0);
    
    public native void SetMaximumIncomingConnections(final int p0);
    
    public native void SetOccasionalPing(final boolean p0);
    
    public native void SetUnreliableTimeout(final int p0);
    
    public native void ApplyNetworkSimulator(final float p0, final short p1, final short p2);
    
    private native boolean TryReceive();
    
    private native int nativeGetData(final ByteBuffer p0);
    
    public boolean Receive(final ByteBuffer byteBuffer) {
        if (this.TryReceive()) {
            try {
                byteBuffer.clear();
                this.receiveBuf.clear();
                this.nativeGetData(this.receiveBuf);
                byteBuffer.put(this.receiveBuf);
                byteBuffer.flip();
            }
            catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
            return true;
        }
        return false;
    }
    
    public int Send(final ByteBuffer src, final int n, final int n2, final byte b, final long n3, final boolean b2) {
        this.sendLock.lock();
        this.sendBuf.clear();
        if (src.remaining() > this.sendBuf.remaining()) {
            System.out.println("Packet data too big.");
            this.sendLock.unlock();
            return 0;
        }
        try {
            this.sendBuf.put(src);
            this.sendBuf.flip();
            final int sendNative = this.sendNative(this.sendBuf, this.sendBuf.remaining(), n, n2, b, n3, b2);
            this.sendLock.unlock();
            return sendNative;
        }
        catch (Exception ex) {
            System.out.println("Other weird packet data error.");
            ex.printStackTrace();
            this.sendLock.unlock();
            return 0;
        }
    }
    
    public int SendRaw(final ByteBuffer byteBuffer, final int n, final int n2, final byte b, final long n3, final boolean b2) {
        try {
            return this.sendNative(byteBuffer, byteBuffer.remaining(), n, n2, b, n3, b2);
        }
        catch (Exception ex) {
            System.out.println("Other weird packet data error.");
            ex.printStackTrace();
            return 0;
        }
    }
    
    private native int sendNative(final ByteBuffer p0, final int p1, final int p2, final int p3, final byte p4, final long p5, final boolean p6);
    
    public native long getGuidFromIndex(final int p0);
    
    public native long getGuidOfPacket();
    
    public native String getIPFromGUID(final long p0);
    
    public native int SendFileAnnounce(final long p0, final long p1, final long p2, final long p3, final String p4);
    
    public native int SendFileChunk(final long p0, final long p1, final long p2, final byte[] p3, final long p4);
    
    public native ZNetFileAnnounce ReceiveFileAnnounce();
    
    public native ZNetFileChunk ReceiveFileChunk();
    
    public native void disconnect(final long p0);
    
    private void connectionStateChangedCallback(final String s, final String s2) {
        final Thread currentThread = Thread.currentThread();
        if (currentThread == RakNetPeerInterface.mainThread) {
            LuaEventManager.triggerEvent("OnConnectionStateChanged", s, s2);
        }
        else {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Thread;)Ljava/lang/String;, s, s2, currentThread));
        }
    }
    
    public native ZNetStatistics GetNetStatistics(final long p0);
    
    public native int GetAveragePing(final long p0);
    
    public native int GetLastPing(final long p0);
    
    public native int GetLowestPing(final long p0);
    
    public native int GetMTUSize(final long p0);
}

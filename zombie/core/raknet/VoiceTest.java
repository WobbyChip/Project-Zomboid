// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.raknet;

import zombie.core.znet.SteamUtils;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.network.GameServer;
import zombie.debug.DebugLog;
import java.nio.ByteBuffer;

public class VoiceTest
{
    protected static boolean bQuit;
    protected static ByteBuffer serverBuf;
    protected static ByteBuffer clientBuf;
    protected static RakNetPeerInterface rnclientPeer;
    protected static RakNetPeerInterface rnserverPeer;
    
    protected static void rakNetServer(final int n) {
        final int n2 = 2;
        final String s = "test";
        VoiceTest.rnserverPeer = new RakNetPeerInterface();
        DebugLog.log("Initialising RakNet...");
        VoiceTest.rnserverPeer.Init(false);
        VoiceTest.rnserverPeer.SetMaximumIncomingConnections(n2);
        if (GameServer.IPCommandline != null) {
            VoiceTest.rnserverPeer.SetServerIP(GameServer.IPCommandline);
        }
        VoiceTest.rnserverPeer.SetServerPort(n);
        VoiceTest.rnserverPeer.SetIncomingPassword(s);
        VoiceTest.rnserverPeer.SetOccasionalPing(true);
        System.out.println(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, VoiceTest.rnserverPeer.Startup(n2)));
    }
    
    public static ByteBuffer rakNetServerReceive() {
        boolean receive;
        do {
            try {
                Thread.sleep(1L);
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            receive = VoiceTest.rnserverPeer.Receive(VoiceTest.serverBuf);
        } while (!VoiceTest.bQuit && !receive);
        return VoiceTest.serverBuf;
    }
    
    private static void rakNetServerDecode(final ByteBuffer byteBuffer) {
        final int n = byteBuffer.get() & 0xFF;
        switch (n) {
            case 19: {
                System.out.println("ID_NEW_INCOMING_CONNECTION");
                final int n2 = byteBuffer.get() & 0xFF;
                final long guidOfPacket = VoiceTest.rnserverPeer.getGuidOfPacket();
                System.out.println(invokedynamic(makeConcatWithConstants:(IJ)Ljava/lang/String;, n2, guidOfPacket));
                VoiceManager.instance.VoiceConnectReq(guidOfPacket);
                break;
            }
            case 16: {
                System.out.println("Connection Request Accepted");
                final int n3 = byteBuffer.get() & 0xFF;
                VoiceManager.instance.VoiceConnectReq(VoiceTest.rnserverPeer.getGuidOfPacket());
                break;
            }
            case 0:
            case 1: {
                System.out.println("PING");
                break;
            }
            default: {
                System.out.println(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n));
                break;
            }
        }
    }
    
    protected static void rakNetClient() {
        final int n = 2;
        VoiceTest.rnclientPeer = new RakNetPeerInterface();
        DebugLog.log("Initialising RakNet...");
        VoiceTest.rnclientPeer.Init(false);
        VoiceTest.rnclientPeer.SetMaximumIncomingConnections(n);
        VoiceTest.rnclientPeer.SetClientPort(GameServer.DEFAULT_PORT + Rand.Next(10000) + 1234);
        VoiceTest.rnclientPeer.SetOccasionalPing(true);
        System.out.println(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, VoiceTest.rnclientPeer.Startup(n)));
    }
    
    public static ByteBuffer rakNetClientReceive() {
        boolean receive;
        do {
            try {
                Thread.sleep(1L);
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            receive = VoiceTest.rnclientPeer.Receive(VoiceTest.clientBuf);
        } while (!VoiceTest.bQuit && !receive);
        return VoiceTest.clientBuf;
    }
    
    private static void rakNetClientDecode(final ByteBuffer byteBuffer) {
        final int n = byteBuffer.get() & 0xFF;
        switch (n) {
            case 19: {
                System.out.println("ID_NEW_INCOMING_CONNECTION");
                final int n2 = byteBuffer.get() & 0xFF;
                final long guidOfPacket = VoiceTest.rnclientPeer.getGuidOfPacket();
                System.out.println(invokedynamic(makeConcatWithConstants:(IJ)Ljava/lang/String;, n2, guidOfPacket));
                VoiceManager.instance.VoiceConnectReq(guidOfPacket);
                break;
            }
            case 16: {
                System.out.println("Connection Request Accepted");
                final int n3 = byteBuffer.get() & 0xFF;
                VoiceManager.instance.VoiceConnectReq(VoiceTest.rnclientPeer.getGuidOfPacket());
                break;
            }
            case 0:
            case 1: {
                System.out.println("PING");
                break;
            }
            default: {
                System.out.println(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n));
                break;
            }
        }
    }
    
    public static void main(final String[] array) {
        DebugLog.log("VoiceTest: START");
        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Core.getInstance().getVersionNumber()));
        DebugLog.log("VoiceTest: SteamUtils.init - EXEC");
        SteamUtils.init();
        DebugLog.log("VoiceTest: SteamUtils.init - OK");
        DebugLog.log("VoiceTest: RakNetPeerInterface - EXEC");
        RakNetPeerInterface.init();
        DebugLog.log("VoiceTest: RakNetPeerInterface - OK");
        DebugLog.log("VoiceTest: VoiceManager.InitVMServer - EXEC");
        VoiceManager.instance.InitVMServer();
        DebugLog.log("VoiceTest: VoiceManager.InitVMServer - OK");
        DebugLog.log("VoiceTest: rakNetServer - EXEC");
        rakNetServer(16000);
        DebugLog.log("VoiceTest: rakNetServer - OK");
        DebugLog.log("VoiceTest: rakNetClient - EXEC");
        rakNetClient();
        DebugLog.log("VoiceTest: rakNetClient - OK");
        DebugLog.log("VoiceTest: rnclientPeer.Connect - EXEC");
        VoiceTest.rnclientPeer.Connect("127.0.0.1", 16000, "test");
        DebugLog.log("VoiceTest: rnclientPeer.Connect - OK");
        final Thread thread = new Thread() {
            @Override
            public void run() {
                while (!VoiceTest.bQuit && !VoiceTest.bQuit) {
                    final ByteBuffer rakNetServerReceive = VoiceTest.rakNetServerReceive();
                    try {
                        VoiceTest.rakNetServerDecode(rakNetServerReceive);
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        };
        thread.setName("serverThread");
        thread.start();
        final Thread thread2 = new Thread() {
            @Override
            public void run() {
                while (!VoiceTest.bQuit && !VoiceTest.bQuit) {
                    final ByteBuffer rakNetClientReceive = VoiceTest.rakNetClientReceive();
                    try {
                        VoiceTest.rakNetClientDecode(rakNetClientReceive);
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        };
        thread2.setName("clientThread");
        thread2.start();
        DebugLog.log("VoiceTest: sleep 10 sec");
        try {
            Thread.sleep(10000L);
        }
        catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    
    static {
        VoiceTest.bQuit = false;
        VoiceTest.serverBuf = ByteBuffer.allocate(500000);
        VoiceTest.clientBuf = ByteBuffer.allocate(500000);
    }
}

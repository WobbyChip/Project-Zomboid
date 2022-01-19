// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.raknet;

import java.nio.ByteBuffer;

public class TestServer
{
    static RakNetPeerInterface server;
    static ByteBuffer buf;
    
    public static void main(final String[] array) {
        (TestServer.server = new RakNetPeerInterface()).SetServerPort(12203);
        TestServer.server.Init(false);
        System.out.println(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, TestServer.server.Startup(32)));
        TestServer.server.SetMaximumIncomingConnections(32);
        TestServer.server.SetOccasionalPing(true);
        TestServer.server.SetIncomingPassword("spiffo");
        while (!false) {
            decode(Receive());
        }
    }
    
    private static void decode(final ByteBuffer byteBuffer) {
        final int n = byteBuffer.get() & 0xFF;
        switch (n) {
            case 21: {
                System.out.println("ID_DISCONNECTION_NOTIFICATION");
                break;
            }
            case 19: {
                TestServer.server.getGuidFromIndex(byteBuffer.get() & 0xFF);
                break;
            }
            case 25: {
                System.out.println("ID_INCOMPATIBLE_PROTOCOL_VERSION");
                break;
            }
            case 0:
            case 1: {
                System.out.println("PING");
                break;
            }
            case 22: {
                System.out.println("ID_CONNECTION_LOST");
                break;
            }
            default: {
                System.out.println(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n));
                break;
            }
        }
    }
    
    public static ByteBuffer Receive() {
        TestServer.buf.position();
        do {
            try {
                Thread.sleep(1L);
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        } while (!TestServer.server.Receive(TestServer.buf));
        return TestServer.buf;
    }
    
    static {
        TestServer.buf = ByteBuffer.allocate(2048);
    }
}

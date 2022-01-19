// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.raknet;

import java.nio.ByteBuffer;

public class TestClient
{
    static RakNetPeerInterface client;
    private static boolean bConnected;
    
    public static void main(final String[] array) {
        (TestClient.client = new RakNetPeerInterface()).Init(false);
        System.out.println(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, TestClient.client.Startup(1)));
        TestClient.client.SetOccasionalPing(true);
        System.out.println(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, TestClient.client.Connect("127.0.0.1", 12203, "spiffo")));
        final boolean b = false;
        final ByteBuffer allocate = ByteBuffer.allocate(500000);
        int n = 0;
        while (!b) {
            ++n;
            while (TestClient.client.Receive(allocate)) {
                decode(allocate);
            }
            try {
                Thread.sleep(33L);
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private static void decode(final ByteBuffer byteBuffer) {
        switch (byteBuffer.get()) {
            case 21: {
                System.out.println("ID_DISCONNECTION_NOTIFICATION");
                break;
            }
            case 18: {
                System.out.println("ID_ALREADY_CONNECTED");
                break;
            }
            case 25: {
                System.out.println("ID_INCOMPATIBLE_PROTOCOL_VERSION");
                break;
            }
            case 31: {
                System.out.println("ID_REMOTE_DISCONNECTION_NOTIFICATION");
                break;
            }
            case 32: {
                System.out.println("ID_REMOTE_CONNECTION_LOST");
                break;
            }
            case 33: {
                System.out.println("ID_REMOTE_NEW_INCOMING_CONNECTION");
                break;
            }
            case 23: {
                System.out.println("ID_CONNECTION_BANNED");
                break;
            }
            case 17: {
                System.out.println("ID_CONNECTION_ATTEMPT_FAILED");
                break;
            }
            case 20: {
                System.out.println("ID_NO_FREE_INCOMING_CONNECTIONS");
                break;
            }
            case 24: {
                System.out.println("ID_INVALID_PASSWORD");
                break;
            }
            case 22: {
                System.out.println("ID_CONNECTION_LOST");
                break;
            }
            case 16: {
                System.out.println("ID_CONNECTION_REQUEST_ACCEPTED");
                TestClient.bConnected = true;
                byteBuffer.clear();
                byteBuffer.put((byte)(-122));
                for (int i = 0; i < 1000; ++i) {
                    byteBuffer.put((byte)(-1));
                }
                System.out.println(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, TestClient.client.Send(byteBuffer, 1, 3, (byte)0, 0L, false)));
                break;
            }
            case 0:
            case 1: {
                System.out.println("PING");
                break;
            }
        }
    }
    
    static {
        TestClient.bConnected = false;
    }
}

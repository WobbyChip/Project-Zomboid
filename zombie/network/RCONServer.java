// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network;

import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.net.SocketException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.io.IOException;
import zombie.debug.DebugLog;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.net.ServerSocket;

public class RCONServer
{
    public static final int SERVERDATA_RESPONSE_VALUE = 0;
    public static final int SERVERDATA_AUTH_RESPONSE = 2;
    public static final int SERVERDATA_EXECCOMMAND = 2;
    public static final int SERVERDATA_AUTH = 3;
    private static RCONServer instance;
    private ServerSocket welcomeSocket;
    private ServerThread thread;
    private String password;
    private ConcurrentLinkedQueue<ExecCommand> toMain;
    
    private RCONServer(final int n, final String password) {
        this.toMain = new ConcurrentLinkedQueue<ExecCommand>();
        this.password = password;
        try {
            this.welcomeSocket = new ServerSocket();
            if (GameServer.IPCommandline != null) {
                this.welcomeSocket.bind(new InetSocketAddress(GameServer.IPCommandline, n));
            }
            else {
                this.welcomeSocket.bind(new InetSocketAddress(n));
            }
            DebugLog.log(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n));
        }
        catch (IOException ex) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n));
            ex.printStackTrace();
            try {
                this.welcomeSocket.close();
                this.welcomeSocket = null;
            }
            catch (IOException ex2) {
                ex2.printStackTrace();
            }
            return;
        }
        (this.thread = new ServerThread()).start();
    }
    
    private void updateMain() {
        for (ExecCommand execCommand = this.toMain.poll(); execCommand != null; execCommand = this.toMain.poll()) {
            execCommand.update();
        }
    }
    
    public void quit() {
        if (this.welcomeSocket != null) {
            try {
                this.welcomeSocket.close();
            }
            catch (IOException ex) {}
            this.welcomeSocket = null;
            this.thread.quit();
            this.thread = null;
        }
    }
    
    public static void init(final int n, final String s) {
        RCONServer.instance = new RCONServer(n, s);
    }
    
    public static void update() {
        if (RCONServer.instance != null) {
            RCONServer.instance.updateMain();
        }
    }
    
    public static void shutdown() {
        if (RCONServer.instance != null) {
            RCONServer.instance.quit();
        }
    }
    
    private class ServerThread extends Thread
    {
        private ArrayList<ClientThread> connections;
        public boolean bQuit;
        
        public ServerThread() {
            this.connections = new ArrayList<ClientThread>();
            this.setName("RCONServer");
        }
        
        @Override
        public void run() {
            while (!this.bQuit) {
                this.runInner();
            }
        }
        
        private void runInner() {
            try {
                final Socket accept = RCONServer.this.welcomeSocket.accept();
                for (int i = 0; i < this.connections.size(); ++i) {
                    if (!this.connections.get(i).isAlive()) {
                        this.connections.remove(i--);
                    }
                }
                if (this.connections.size() >= 5) {
                    accept.close();
                    return;
                }
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, accept.toString()));
                final ClientThread e = new ClientThread(accept, RCONServer.this.password);
                this.connections.add(e);
                e.start();
            }
            catch (IOException ex) {
                if (!this.bQuit) {
                    ex.printStackTrace();
                }
            }
        }
        
        public void quit() {
            this.bQuit = true;
            while (this.isAlive()) {
                try {
                    Thread.sleep(50L);
                }
                catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            for (int i = 0; i < this.connections.size(); ++i) {
                this.connections.get(i).quit();
            }
        }
    }
    
    private static class ClientThread extends Thread
    {
        public Socket socket;
        public boolean bAuth;
        public boolean bQuit;
        private String password;
        private InputStream in;
        private OutputStream out;
        private ConcurrentLinkedQueue<ExecCommand> toThread;
        private int pendingCommands;
        
        public ClientThread(final Socket socket, final String password) {
            this.toThread = new ConcurrentLinkedQueue<ExecCommand>();
            this.socket = socket;
            this.password = password;
            try {
                this.in = socket.getInputStream();
                this.out = socket.getOutputStream();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
            this.setName(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, socket.getLocalPort()));
        }
        
        @Override
        public void run() {
            if (this.in == null) {
                return;
            }
            if (this.out == null) {
                return;
            }
            while (!this.bQuit) {
                try {
                    this.runInner();
                }
                catch (SocketException ex3) {
                    this.bQuit = true;
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            try {
                this.socket.close();
            }
            catch (IOException ex2) {
                ex2.printStackTrace();
            }
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.socket.toString()));
        }
        
        private void runInner() throws IOException {
            final byte[] array = new byte[4];
            if (this.in.read(array, 0, 4) < 0) {
                this.bQuit = true;
                return;
            }
            final ByteBuffer wrap = ByteBuffer.wrap(array);
            wrap.order(ByteOrder.LITTLE_ENDIAN);
            int i;
            final int n = i = wrap.getInt();
            final byte[] array2 = new byte[n];
            do {
                final int read = this.in.read(array2, n - i, i);
                if (read < 0) {
                    this.bQuit = true;
                    return;
                }
                i -= read;
            } while (i > 0);
            final ByteBuffer wrap2 = ByteBuffer.wrap(array2);
            wrap2.order(ByteOrder.LITTLE_ENDIAN);
            this.handlePacket(wrap2.getInt(), wrap2.getInt(), new String(wrap2.array(), wrap2.position(), wrap2.limit() - wrap2.position() - 2));
        }
        
        private void handlePacket(final int n, final int n2, final String s) throws IOException {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(IILjava/lang/String;Ljava/lang/String;)Ljava/lang/String;, n, n2, s, this.socket.toString()));
            switch (n2) {
                case 3: {
                    if (!(this.bAuth = s.equals(this.password))) {
                        DebugLog.log("RCON: password doesn't match");
                        this.bQuit = true;
                    }
                    final ByteBuffer allocate = ByteBuffer.allocate(14);
                    allocate.order(ByteOrder.LITTLE_ENDIAN);
                    allocate.putInt(allocate.capacity() - 4);
                    allocate.putInt(n);
                    allocate.putInt(0);
                    allocate.putShort((short)0);
                    this.out.write(allocate.array());
                    allocate.clear();
                    allocate.putInt(allocate.capacity() - 4);
                    allocate.putInt(this.bAuth ? n : -1);
                    allocate.putInt(2);
                    allocate.putShort((short)0);
                    this.out.write(allocate.array());
                    break;
                }
                case 2: {
                    if (!this.checkAuth()) {
                        break;
                    }
                    final ExecCommand e = new ExecCommand(n, s, this);
                    ++this.pendingCommands;
                    RCONServer.instance.toMain.add(e);
                    while (this.pendingCommands > 0) {
                        final ExecCommand execCommand = this.toThread.poll();
                        if (execCommand != null) {
                            --this.pendingCommands;
                            this.handleResponse(execCommand);
                        }
                        else {
                            try {
                                Thread.sleep(50L);
                            }
                            catch (InterruptedException ex) {}
                        }
                    }
                    break;
                }
                case 0: {
                    if (!this.checkAuth()) {
                        break;
                    }
                    final ByteBuffer allocate2 = ByteBuffer.allocate(14);
                    allocate2.order(ByteOrder.LITTLE_ENDIAN);
                    allocate2.putInt(allocate2.capacity() - 4);
                    allocate2.putInt(n);
                    allocate2.putInt(0);
                    allocate2.putShort((short)0);
                    this.out.write(allocate2.array());
                    this.out.write(allocate2.array());
                    break;
                }
                default: {
                    DebugLog.log(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n2));
                    break;
                }
            }
        }
        
        public void handleResponse(final ExecCommand execCommand) {
            String response = execCommand.response;
            if (response == null) {
                response = "";
            }
            final ByteBuffer allocate = ByteBuffer.allocate(12 + response.length() + 2);
            allocate.order(ByteOrder.LITTLE_ENDIAN);
            allocate.putInt(allocate.capacity() - 4);
            allocate.putInt(execCommand.ID);
            allocate.putInt(0);
            allocate.put(response.getBytes());
            allocate.putShort((short)0);
            try {
                this.out.write(allocate.array());
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        private boolean checkAuth() throws IOException {
            if (this.bAuth) {
                return true;
            }
            this.bQuit = true;
            final ByteBuffer allocate = ByteBuffer.allocate(14);
            allocate.order(ByteOrder.LITTLE_ENDIAN);
            allocate.putInt(allocate.capacity() - 4);
            allocate.putInt(-1);
            allocate.putInt(2);
            allocate.putShort((short)0);
            this.out.write(allocate.array());
            return false;
        }
        
        public void quit() {
            if (this.socket != null) {
                try {
                    this.socket.close();
                }
                catch (IOException ex2) {}
            }
            this.bQuit = true;
            while (this.isAlive()) {
                try {
                    Thread.sleep(50L);
                }
                catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    
    private static class ExecCommand
    {
        public int ID;
        public String command;
        public String response;
        public ClientThread thread;
        
        public ExecCommand(final int id, final String command, final ClientThread thread) {
            this.ID = id;
            this.command = command;
            this.thread = thread;
        }
        
        public void update() {
            this.response = GameServer.rcon(this.command);
            if (this.thread.isAlive()) {
                this.thread.toThread.add(this);
            }
        }
    }
}

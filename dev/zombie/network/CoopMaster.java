// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network;

import zombie.Lua.LuaManager;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.Platform;
import java.util.Objects;
import java.util.regex.Matcher;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.Lua.LuaEventManager;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.lang.management.ManagementFactory;
import zombie.GameWindow;
import zombie.core.ThreadGroups;
import zombie.ZomboidFileSystem;
import zombie.core.Core;
import java.util.ArrayList;
import zombie.core.znet.SteamUtils;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.UUID;
import java.util.LinkedList;
import java.util.regex.Pattern;
import java.util.List;
import java.io.PrintStream;

public class CoopMaster
{
    private Process serverProcess;
    private Thread serverThread;
    private PrintStream serverCommandStream;
    private final List<String> incomingMessages;
    private Pattern serverMessageParser;
    private TerminationReason serverTerminationReason;
    private Thread timeoutWatchThread;
    private boolean serverResponded;
    public static final CoopMaster instance;
    private String adminUsername;
    private String adminPassword;
    private String serverName;
    private Long serverSteamID;
    private String serverIP;
    private Integer serverPort;
    private int autoCookie;
    private static final int autoCookieOffset = 1000000;
    private static final int maxAutoCookie = 1000000;
    private final List<Pair<ICoopServerMessageListener, ListenerOptions>> listeners;
    
    private CoopMaster() {
        this.adminUsername = null;
        this.adminPassword = null;
        this.serverName = null;
        this.serverSteamID = null;
        this.serverIP = null;
        this.serverPort = null;
        this.autoCookie = 0;
        this.incomingMessages = new LinkedList<String>();
        this.listeners = new LinkedList<Pair<ICoopServerMessageListener, ListenerOptions>>();
        this.serverMessageParser = Pattern.compile("^([\\-\\w]+)(\\[(\\d+)\\])?@(.*)$");
        this.adminPassword = UUID.randomUUID().toString();
    }
    
    public void launchServer(final String s, final String s2, final int n) throws IOException {
        this.launchServer(s, s2, n, false);
    }
    
    public void softreset(final String s, final String s2, final int n) throws IOException {
        this.launchServer(s, s2, n, true);
    }
    
    private void launchServer(final String serverName, String adminUsername, final int n, final boolean b) throws IOException {
        final String string = Paths.get(System.getProperty("java.home"), "bin", "java").toAbsolutePath().toString();
        if (SteamUtils.isSteamModeEnabled()) {
            adminUsername = "admin";
        }
        final ArrayList<String> command = new ArrayList<String>();
        command.add(string);
        command.add(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n));
        command.add(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n));
        command.add(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.getProperty("java.library.path")));
        command.add(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.getProperty("java.class.path")));
        command.add(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.getProperty("user.dir")));
        command.add(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.getProperty("user.home")));
        command.add("-Dzomboid.znetlog=1");
        command.add(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, SteamUtils.isSteamModeEnabled() ? "1" : "0"));
        command.add("-Djava.awt.headless=true");
        command.add("-XX:-OmitStackTraceInFastThrow");
        final String garbageCollector = this.getGarbageCollector();
        if (garbageCollector != null) {
            command.add(garbageCollector);
        }
        if (b) {
            command.add("-Dsoftreset");
        }
        if (Core.bDebug) {
            command.add("-Ddebug");
        }
        command.add("zombie.network.GameServer");
        command.add("-coop");
        command.add("-servername");
        command.add(this.serverName = serverName);
        command.add("-adminusername");
        command.add(this.adminUsername = adminUsername);
        command.add("-adminpassword");
        command.add(this.adminPassword);
        command.add(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir()));
        final ProcessBuilder processBuilder = new ProcessBuilder(command);
        this.serverTerminationReason = TerminationReason.NormalTermination;
        this.serverResponded = false;
        this.serverProcess = processBuilder.start();
        this.serverCommandStream = new PrintStream(this.serverProcess.getOutputStream());
        (this.serverThread = new Thread(ThreadGroups.Workers, this::readServer)).setUncaughtExceptionHandler(GameWindow::uncaughtException);
        this.serverThread.start();
        (this.timeoutWatchThread = new Thread(ThreadGroups.Workers, this::watchServer)).setUncaughtExceptionHandler(GameWindow::uncaughtException);
        this.timeoutWatchThread.start();
    }
    
    private String getGarbageCollector() {
        try {
            final List<String> inputArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
            boolean b = false;
            boolean b2 = false;
            for (final String s : inputArguments) {
                if ("-XX:+UseZGC".equals(s)) {
                    b = true;
                }
                if ("-XX:-UseZGC".equals(s)) {
                    b = false;
                }
                if ("-XX:+UseG1GC".equals(s)) {
                    b2 = true;
                }
                if ("-XX:-UseG1GC".equals(s)) {
                    b2 = false;
                }
            }
            if (b) {
                return "-XX:+UseZGC";
            }
            if (b2) {
                return "-XX:+UseG1GC";
            }
        }
        catch (Throwable t) {}
        return null;
    }
    
    private void readServer() {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.serverProcess.getInputStream()));
        while (true) {
            try {
                this.serverProcess.exitValue();
            }
            catch (IllegalThreadStateException ex2) {
                String line = null;
                try {
                    line = bufferedReader.readLine();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
                if (line != null) {
                    this.storeMessage(line);
                    this.serverResponded = true;
                }
                continue;
            }
            break;
        }
        this.storeMessage("process-status@terminated");
    }
    
    public void abortServer() {
        this.serverProcess.destroy();
    }
    
    private void watchServer() {
        final int max = Math.max(ServerOptions.instance.CoopServerLaunchTimeout.getValue(), 5);
        try {
            Thread.sleep(1000 * max);
            if (!this.serverResponded) {
                this.serverTerminationReason = TerminationReason.Timeout;
                this.abortServer();
            }
        }
        catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    
    public boolean isRunning() {
        return this.serverThread != null && this.serverThread.isAlive();
    }
    
    public TerminationReason terminationReason() {
        return this.serverTerminationReason;
    }
    
    private void storeMessage(final String s) {
        synchronized (this.incomingMessages) {
            this.incomingMessages.add(s);
        }
    }
    
    public synchronized void sendMessage(final String str, final String str2, final String str3) {
        final StringBuilder sb = new StringBuilder();
        sb.append(str);
        if (str2 == null) {
            sb.append("@");
        }
        else {
            sb.append("[");
            sb.append(str2);
            sb.append("]@");
        }
        sb.append(str3);
        final String string = sb.toString();
        if (this.serverCommandStream != null) {
            this.serverCommandStream.println(string);
            this.serverCommandStream.flush();
        }
    }
    
    public void sendMessage(final String s, final String s2) {
        this.sendMessage(s, null, s2);
    }
    
    public synchronized void invokeServer(final String s, final String s2, final ICoopServerMessageListener coopServerMessageListener) {
        this.autoCookie = (this.autoCookie + 1) % 1000000;
        final String string = Integer.toString(1000000 + this.autoCookie);
        this.addListener(coopServerMessageListener, new ListenerOptions(s, string, true));
        this.sendMessage(s, string, s2);
    }
    
    public String getMessage() {
        Object anObject = null;
        synchronized (this.incomingMessages) {
            if (this.incomingMessages.size() != 0) {
                anObject = this.incomingMessages.get(0);
                this.incomingMessages.remove(0);
                if (!"ping@ping".equals(anObject)) {
                    System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, anObject));
                }
            }
        }
        return (String)anObject;
    }
    
    public void update() {
        String message;
        while ((message = this.getMessage()) != null) {
            final Matcher matcher = this.serverMessageParser.matcher(message);
            if (matcher.find()) {
                final String group = matcher.group(1);
                final String group2 = matcher.group(3);
                final String group3 = matcher.group(4);
                LuaEventManager.triggerEvent("OnCoopServerMessage", group, group2, group3);
                this.handleMessage(group, group2, group3);
            }
            else {
                DebugLog.log(DebugType.Network, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, message));
            }
        }
    }
    
    private void handleMessage(final String a, final String s, final String s2) {
        if (Objects.equals(a, "ping")) {
            this.sendMessage("ping", s, "pong");
        }
        else if (Objects.equals(a, "steam-id")) {
            if (Objects.equals(s2, "null")) {
                this.serverSteamID = null;
            }
            else {
                this.serverSteamID = SteamUtils.convertStringToSteamID(s2);
            }
        }
        else if (Objects.equals(a, "server-address")) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s2));
            final Matcher matcher = Pattern.compile("^(\\d+\\.\\d+\\.\\d+\\.\\d+):(\\d+)$").matcher(s2);
            if (matcher.find()) {
                final String group = matcher.group(1);
                final String group2 = matcher.group(2);
                this.serverIP = group;
                this.serverPort = Integer.valueOf(group2);
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/Integer;)Ljava/lang/String;, this.serverIP, this.serverPort));
            }
            else {
                DebugLog.log("Failed to parse server address");
            }
        }
        this.invokeListeners(a, s, s2);
    }
    
    public void register(final Platform platform, final KahluaTable kahluaTable) {
        final KahluaTable table = platform.newTable();
        table.rawset((Object)"launch", (Object)new JavaFunction() {
            public int call(final LuaCallFrame luaCallFrame, final int n) {
                boolean b = false;
                if (n == 4) {
                    final Object value = luaCallFrame.get(1);
                    final Object value2 = luaCallFrame.get(2);
                    final Object value3 = luaCallFrame.get(3);
                    if (!(value instanceof String) || !(value2 instanceof String) || !(value3 instanceof Double)) {
                        return 0;
                    }
                    try {
                        CoopMaster.this.launchServer((String)value, (String)value2, ((Double)value3).intValue());
                        b = true;
                    }
                    catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                else {
                    DebugLog.log(DebugType.Network, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n));
                }
                luaCallFrame.push((Object)b);
                return 1;
            }
        });
        table.rawset((Object)"softreset", (Object)new JavaFunction() {
            public int call(final LuaCallFrame luaCallFrame, final int n) {
                boolean b = false;
                if (n == 4) {
                    final Object value = luaCallFrame.get(1);
                    final Object value2 = luaCallFrame.get(2);
                    final Object value3 = luaCallFrame.get(3);
                    if (!(value instanceof String) || !(value2 instanceof String) || !(value3 instanceof Double)) {
                        return 0;
                    }
                    try {
                        CoopMaster.this.softreset((String)value, (String)value2, ((Double)value3).intValue());
                        b = true;
                    }
                    catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                else {
                    DebugLog.log(DebugType.Network, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n));
                }
                luaCallFrame.push((Object)b);
                return 1;
            }
        });
        table.rawset((Object)"isRunning", (Object)new JavaFunction() {
            public int call(final LuaCallFrame luaCallFrame, final int n) {
                luaCallFrame.push((Object)CoopMaster.this.isRunning());
                return 1;
            }
        });
        table.rawset((Object)"sendMessage", (Object)new JavaFunction() {
            public int call(final LuaCallFrame luaCallFrame, final int n) {
                if (n == 4) {
                    final Object value = luaCallFrame.get(1);
                    final Object value2 = luaCallFrame.get(2);
                    final Object value3 = luaCallFrame.get(3);
                    if (value instanceof String && value2 instanceof String && value3 instanceof String) {
                        CoopMaster.this.sendMessage((String)value, (String)value2, (String)value3);
                    }
                }
                else if (n == 3) {
                    final Object value4 = luaCallFrame.get(1);
                    final Object value5 = luaCallFrame.get(2);
                    if (value4 instanceof String && value5 instanceof String) {
                        CoopMaster.this.sendMessage((String)value4, (String)value5);
                    }
                }
                return 0;
            }
        });
        table.rawset((Object)"getAdminPassword", (Object)new JavaFunction() {
            public int call(final LuaCallFrame luaCallFrame, final int n) {
                luaCallFrame.push((Object)CoopMaster.this.adminPassword);
                return 1;
            }
        });
        table.rawset((Object)"getTerminationReason", (Object)new JavaFunction() {
            public int call(final LuaCallFrame luaCallFrame, final int n) {
                luaCallFrame.push((Object)CoopMaster.this.serverTerminationReason.toString());
                return 1;
            }
        });
        table.rawset((Object)"getSteamID", (Object)new JavaFunction() {
            public int call(final LuaCallFrame luaCallFrame, final int n) {
                if (CoopMaster.this.serverSteamID != null) {
                    luaCallFrame.push((Object)SteamUtils.convertSteamIDToString(CoopMaster.this.serverSteamID));
                    return 1;
                }
                return 0;
            }
        });
        table.rawset((Object)"getAddress", (Object)new JavaFunction() {
            public int call(final LuaCallFrame luaCallFrame, final int n) {
                luaCallFrame.push((Object)CoopMaster.this.serverIP);
                return 1;
            }
        });
        table.rawset((Object)"getPort", (Object)new JavaFunction() {
            public int call(final LuaCallFrame luaCallFrame, final int n) {
                luaCallFrame.push((Object)CoopMaster.this.serverPort);
                return 1;
            }
        });
        table.rawset((Object)"abort", (Object)new JavaFunction() {
            public int call(final LuaCallFrame luaCallFrame, final int n) {
                CoopMaster.this.abortServer();
                return 0;
            }
        });
        table.rawset((Object)"getServerSaveFolder", (Object)new JavaFunction() {
            public int call(final LuaCallFrame luaCallFrame, final int n) {
                luaCallFrame.push((Object)CoopMaster.this.getServerSaveFolder((String)luaCallFrame.get(1)));
                return 1;
            }
        });
        table.rawset((Object)"getPlayerSaveFolder", (Object)new JavaFunction() {
            public int call(final LuaCallFrame luaCallFrame, final int n) {
                luaCallFrame.push((Object)CoopMaster.this.getPlayerSaveFolder((String)luaCallFrame.get(1)));
                return 1;
            }
        });
        kahluaTable.rawset((Object)"CoopServer", (Object)table);
    }
    
    public void addListener(final ICoopServerMessageListener coopServerMessageListener, final ListenerOptions listenerOptions) {
        synchronized (this.listeners) {
            this.listeners.add(new Pair<ICoopServerMessageListener, ListenerOptions>(coopServerMessageListener, listenerOptions));
        }
    }
    
    public void addListener(final ICoopServerMessageListener coopServerMessageListener) {
        this.addListener(coopServerMessageListener, null);
    }
    
    public void removeListener(final ICoopServerMessageListener coopServerMessageListener) {
        synchronized (this.listeners) {
            int n;
            for (n = 0; n < this.listeners.size() && this.listeners.get(n).first != coopServerMessageListener; ++n) {}
            if (n < this.listeners.size()) {
                this.listeners.remove(n);
            }
        }
    }
    
    private void invokeListeners(final String anObject, final String anObject2, final String s) {
        synchronized (this.listeners) {
            final Iterator<Pair<ICoopServerMessageListener, ListenerOptions>> iterator = this.listeners.iterator();
            while (iterator.hasNext()) {
                final Pair<ICoopServerMessageListener, ListenerOptions> pair = iterator.next();
                final ICoopServerMessageListener coopServerMessageListener = pair.first;
                final ListenerOptions listenerOptions = pair.second;
                if (coopServerMessageListener != null) {
                    if (listenerOptions == null) {
                        coopServerMessageListener.OnCoopServerMessage(anObject, anObject2, s);
                    }
                    else {
                        if ((listenerOptions.tag != null && !listenerOptions.tag.equals(anObject)) || (listenerOptions.cookie != null && !listenerOptions.cookie.equals(anObject2))) {
                            continue;
                        }
                        if (listenerOptions.autoRemove) {
                            iterator.remove();
                        }
                        coopServerMessageListener.OnCoopServerMessage(anObject, anObject2, s);
                    }
                }
            }
        }
    }
    
    public String getServerName() {
        return this.serverName;
    }
    
    public String getServerSaveFolder(final String s) {
        return LuaManager.GlobalObject.sanitizeWorldName(s);
    }
    
    public String getPlayerSaveFolder(final String s) {
        return LuaManager.GlobalObject.sanitizeWorldName(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
    }
    
    static {
        instance = new CoopMaster();
    }
    
    public enum TerminationReason
    {
        NormalTermination, 
        Timeout;
        
        private static /* synthetic */ TerminationReason[] $values() {
            return new TerminationReason[] { TerminationReason.NormalTermination, TerminationReason.Timeout };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    private class Pair<K, V>
    {
        private final K first;
        private final V second;
        
        public Pair(final K first, final V second) {
            this.first = first;
            this.second = second;
        }
        
        public K getFirst() {
            return this.first;
        }
        
        public V getSecond() {
            return this.second;
        }
    }
    
    public class ListenerOptions
    {
        public String tag;
        public String cookie;
        public boolean autoRemove;
        
        public ListenerOptions(final String tag, final String cookie, final boolean autoRemove) {
            this.tag = null;
            this.cookie = null;
            this.autoRemove = false;
            this.tag = tag;
            this.cookie = cookie;
            this.autoRemove = autoRemove;
        }
        
        public ListenerOptions(final CoopMaster coopMaster, final String s, final String s2) {
            this(coopMaster, s, s2, false);
        }
        
        public ListenerOptions(final CoopMaster coopMaster, final String s) {
            this(coopMaster, s, null, false);
        }
    }
}

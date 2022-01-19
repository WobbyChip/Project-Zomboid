// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network;

import java.util.regex.Matcher;
import zombie.debug.DebugLog;
import java.util.Objects;
import zombie.core.znet.SteamGameServer;
import zombie.core.znet.SteamUtils;
import zombie.core.znet.PortMapper;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.File;
import zombie.ZomboidFileSystem;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.regex.Pattern;
import java.io.PrintStream;

public class CoopSlave
{
    private static PrintStream stdout;
    private static PrintStream stderr;
    private Pattern serverMessageParser;
    private long nextPing;
    private long lastPong;
    public static CoopSlave instance;
    public String hostUser;
    public long hostSteamID;
    private boolean masterLost;
    private HashSet<Long> invites;
    private Long serverSteamID;
    
    public static void init() throws FileNotFoundException {
        CoopSlave.instance = new CoopSlave();
    }
    
    public static void initStreams() throws FileNotFoundException {
        final FileOutputStream out = new FileOutputStream(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator));
        CoopSlave.stdout = System.out;
        CoopSlave.stderr = System.err;
        System.setOut(new PrintStream(out));
        System.setErr(System.out);
    }
    
    private CoopSlave() {
        this.nextPing = -1L;
        this.lastPong = -1L;
        this.hostUser = null;
        this.hostSteamID = 0L;
        this.masterLost = false;
        this.invites = new HashSet<Long>();
        this.serverSteamID = null;
        this.serverMessageParser = Pattern.compile("^([\\-\\w]+)(\\[(\\d+)\\])?@(.*)$");
        this.notify("coop mode enabled");
        if (System.getProperty("hostUser") != null) {
            this.hostUser = System.getProperty("zomboid.hostUser").trim();
        }
    }
    
    public synchronized void notify(final String s) {
        this.sendMessage("info", null, s);
    }
    
    public synchronized void sendStatus(final String s) {
        this.sendMessage("status", null, s);
    }
    
    public static void status(final String s) {
        if (CoopSlave.instance != null) {
            CoopSlave.instance.sendStatus(s);
        }
    }
    
    public synchronized void sendMessage(final String s) {
        this.sendMessage("message", null, s);
    }
    
    public synchronized void sendMessage(final String s, final String s2, final String s3) {
        if (s2 != null) {
            CoopSlave.stdout.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, s2, s3));
        }
        else {
            CoopSlave.stdout.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, s3));
        }
    }
    
    public void sendExternalIPAddress(final String s) {
        this.sendMessage("get-parameter", s, PortMapper.getExternalAddress());
    }
    
    public synchronized void sendSteamID(final String s) {
        if (this.serverSteamID == null && SteamUtils.isSteamModeEnabled()) {
            this.serverSteamID = SteamGameServer.GetSteamID();
        }
        this.sendMessage("get-parameter", s, this.serverSteamID.toString());
    }
    
    public boolean handleCommand(final String input) {
        final Matcher matcher = this.serverMessageParser.matcher(input);
        if (matcher.find()) {
            final String group = matcher.group(1);
            final String group2 = matcher.group(3);
            final String group3 = matcher.group(4);
            if (Objects.equals(group, "set-host-user")) {
                System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, group3));
                this.hostUser = group3;
            }
            if (Objects.equals(group, "set-host-steamid")) {
                this.hostSteamID = SteamUtils.convertStringToSteamID(group3);
            }
            if (Objects.equals(group, "invite-add")) {
                final Long value = SteamUtils.convertStringToSteamID(group3);
                if (value != -1L) {
                    this.invites.add(value);
                }
            }
            if (Objects.equals(group, "invite-remove")) {
                final Long value2 = SteamUtils.convertStringToSteamID(group3);
                if (value2 != -1L) {
                    this.invites.remove(value2);
                }
            }
            if (Objects.equals(group, "get-parameter")) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, group, group3));
                if (Objects.equals(group3, "external-ip")) {
                    this.sendExternalIPAddress(group2);
                }
                else if (Objects.equals(group3, "steam-id")) {
                    this.sendSteamID(group2);
                }
            }
            if (Objects.equals(group, "ping")) {
                this.lastPong = System.currentTimeMillis();
            }
            if (Objects.equals(group, "process-status") && Objects.equals(group3, "eof")) {
                DebugLog.log("Master connection lost: EOF");
                this.masterLost = true;
            }
            return true;
        }
        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, input));
        return false;
    }
    
    public String getHostUser() {
        return this.hostUser;
    }
    
    public void update() {
        final long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis >= this.nextPing) {
            this.sendMessage("ping", null, "ping");
            this.nextPing = currentTimeMillis + 5000L;
        }
        final long n = Math.max(ServerOptions.instance.CoopMasterPingTimeout.getValue(), 30) * 1000;
        if (this.lastPong == -1L) {
            this.lastPong = currentTimeMillis;
        }
        this.masterLost = (this.masterLost || currentTimeMillis - this.lastPong > n);
    }
    
    public boolean masterLost() {
        return this.masterLost;
    }
    
    public boolean isHost(final long n) {
        return n == this.hostSteamID;
    }
    
    public boolean isInvited(final long l) {
        return this.invites.contains(l);
    }
}

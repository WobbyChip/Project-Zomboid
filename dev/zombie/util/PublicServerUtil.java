// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util;

import zombie.core.ThreadGroups;
import java.net.NetworkInterface;
import java.net.InetAddress;
import java.util.Iterator;
import zombie.core.Core;
import java.net.URLConnection;
import java.net.SocketTimeoutException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import zombie.debug.DebugLog;
import zombie.network.ServerOptions;
import zombie.network.GameServer;
import zombie.debug.DebugOptions;

public final class PublicServerUtil
{
    public static String webSite;
    private static long timestampForUpdate;
    private static long timestampForPlayerUpdate;
    private static long updateTick;
    private static long updatePlayerTick;
    private static int sentPlayerCount;
    private static boolean isEnabled;
    
    public static void init() {
        PublicServerUtil.isEnabled = false;
        if (!DebugOptions.instance.Network.PublicServerUtil.Enabled.getValue()) {
            return;
        }
        try {
            if (GameServer.bServer) {
                ServerOptions.instance.changeOption("PublicName", checkHacking(ServerOptions.instance.getOption("PublicName")));
                ServerOptions.instance.changeOption("PublicDescription", checkHacking(ServerOptions.instance.getOption("PublicDescription")));
            }
            if (GameServer.bServer && !isPublic()) {
                return;
            }
            DebugLog.log("connecting to public server list");
            final URLConnection openConnection = new URL(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, PublicServerUtil.webSite)).openConnection();
            openConnection.setConnectTimeout(3000);
            openConnection.connect();
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(openConnection.getInputStream()));
            final StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            bufferedReader.close();
            for (final String s : sb.toString().split("<br>")) {
                if (s.contains("allowed") && s.contains("true")) {
                    PublicServerUtil.isEnabled = true;
                }
                if (s.contains("updateTick")) {
                    PublicServerUtil.updateTick = Long.parseLong(s.split("=")[1].trim());
                }
                if (s.contains("updatePlayerTick")) {
                    PublicServerUtil.updatePlayerTick = Long.parseLong(s.split("=")[1].trim());
                }
                if (s.contains("ip")) {
                    GameServer.ip = s.split("=")[1].trim();
                }
            }
        }
        catch (SocketTimeoutException ex2) {
            PublicServerUtil.isEnabled = false;
            DebugLog.log("timeout trying to connect to public server list");
        }
        catch (Exception ex) {
            PublicServerUtil.isEnabled = false;
            ex.printStackTrace();
        }
    }
    
    private static String checkHacking(final String s) {
        if (s == null) {
            return "";
        }
        return s.replaceAll("--", "").replaceAll("->", "").replaceAll("(?i)select union", "").replaceAll("(?i)select join", "").replaceAll("1=1", "").replaceAll("(?i)delete from", "");
    }
    
    public static void insertOrUpdate() {
        if (!PublicServerUtil.isEnabled) {
            return;
        }
        if (isPublic()) {
            try {
                insertDatas();
            }
            catch (Exception ex) {
                System.out.println("Can't reach PZ.com");
            }
        }
    }
    
    private static boolean isPublic() {
        final String checkHacking = checkHacking(ServerOptions.instance.PublicName.getValue());
        return ServerOptions.instance.Public.getValue() && !checkHacking.isEmpty();
    }
    
    public static void update() {
        if (System.currentTimeMillis() - PublicServerUtil.timestampForUpdate > PublicServerUtil.updateTick) {
            PublicServerUtil.timestampForUpdate = System.currentTimeMillis();
            init();
            if (!PublicServerUtil.isEnabled) {
                return;
            }
            if (isPublic()) {
                try {
                    insertDatas();
                }
                catch (Exception ex) {
                    System.out.println("Can't reach PZ.com");
                }
            }
        }
    }
    
    private static void insertDatas() throws Exception {
        if (!PublicServerUtil.isEnabled) {
            return;
        }
        String s = "";
        if (!ServerOptions.instance.PublicDescription.getValue().isEmpty()) {
            s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, ServerOptions.instance.PublicDescription.getValue().replaceAll(" ", "%20"));
        }
        String anObject = "";
        final Iterator<String> iterator = GameServer.ServerMods.iterator();
        while (iterator.hasNext()) {
            anObject = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, anObject, (String)iterator.next());
        }
        if (!"".equals(anObject)) {
            anObject = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, anObject.substring(0, anObject.length() - 1).replaceAll(" ", "%20"));
        }
        String s2 = GameServer.ip;
        if (!ServerOptions.instance.server_browser_announced_ip.getValue().isEmpty()) {
            s2 = ServerOptions.instance.server_browser_announced_ip.getValue();
        }
        PublicServerUtil.timestampForUpdate = System.currentTimeMillis();
        final int playerCount = GameServer.getPlayerCount();
        callUrl(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IILjava/lang/String;Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, PublicServerUtil.webSite, ServerOptions.instance.PublicName.getValue().replaceAll(" ", "%20"), s, ServerOptions.instance.DefaultPort.getValue(), playerCount, s2, ServerOptions.instance.Open.getValue() ? "1" : "0", "".equals(ServerOptions.instance.Password.getValue()) ? "0" : "1", ServerOptions.getInstance().getMaxPlayers(), Core.getInstance().getVersionNumber().replaceAll(" ", "%20"), anObject, getMacAddress()));
        PublicServerUtil.sentPlayerCount = playerCount;
    }
    
    public static void updatePlayers() {
        if (System.currentTimeMillis() - PublicServerUtil.timestampForPlayerUpdate > PublicServerUtil.updatePlayerTick) {
            PublicServerUtil.timestampForPlayerUpdate = System.currentTimeMillis();
            if (!PublicServerUtil.isEnabled) {
                return;
            }
            try {
                String s = GameServer.ip;
                if (!ServerOptions.instance.server_browser_announced_ip.getValue().isEmpty()) {
                    s = ServerOptions.instance.server_browser_announced_ip.getValue();
                }
                callUrl(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;IILjava/lang/String;)Ljava/lang/String;, PublicServerUtil.webSite, ServerOptions.instance.DefaultPort.getValue(), GameServer.getPlayerCount(), s));
                PublicServerUtil.sentPlayerCount = GameServer.getPlayerCount();
            }
            catch (Exception ex) {
                System.out.println("Can't reach PZ.com");
            }
        }
    }
    
    public static void updatePlayerCountIfChanged() {
        if (PublicServerUtil.isEnabled && PublicServerUtil.sentPlayerCount != GameServer.getPlayerCount()) {
            updatePlayers();
        }
    }
    
    public static boolean isEnabled() {
        return PublicServerUtil.isEnabled;
    }
    
    private static String getMacAddress() {
        try {
            final NetworkInterface byInetAddress = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
            if (byInetAddress != null) {
                final byte[] hardwareAddress = byInetAddress.getHardwareAddress();
                final StringBuilder sb = new StringBuilder();
                for (int i = 0; i < hardwareAddress.length; ++i) {
                    sb.append(String.format("%02X%s", hardwareAddress[i], (i < hardwareAddress.length - 1) ? "-" : ""));
                }
                return sb.toString();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }
    
    private static void callUrl(final String s) {
        new Thread(ThreadGroups.Workers, Lambda.invoker(s, spec -> {
            try {
                new URL(spec).openConnection().getInputStream();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }), "openUrl").start();
    }
    
    static {
        PublicServerUtil.webSite = "https://www.projectzomboid.com/server_browser/";
        PublicServerUtil.timestampForUpdate = 0L;
        PublicServerUtil.timestampForPlayerUpdate = 0L;
        PublicServerUtil.updateTick = 600000L;
        PublicServerUtil.updatePlayerTick = 300000L;
        PublicServerUtil.sentPlayerCount = 0;
        PublicServerUtil.isEnabled = false;
    }
}

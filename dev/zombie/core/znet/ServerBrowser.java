// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.znet;

import se.krka.kahlua.vm.KahluaTable;
import zombie.Lua.LuaManager;
import zombie.network.Server;
import zombie.Lua.LuaEventManager;
import java.util.ArrayList;
import java.util.List;

public class ServerBrowser
{
    private static IServerBrowserCallback m_callbackInterface;
    
    public static boolean init() {
        boolean n_Init = false;
        if (SteamUtils.isSteamModeEnabled()) {
            n_Init = n_Init();
        }
        return n_Init;
    }
    
    public static void shutdown() {
        if (SteamUtils.isSteamModeEnabled()) {
            n_Shutdown();
        }
    }
    
    public static void RefreshInternetServers() {
        if (SteamUtils.isSteamModeEnabled()) {
            n_RefreshInternetServers();
        }
    }
    
    public static int GetServerCount() {
        int n_GetServerCount = 0;
        if (SteamUtils.isSteamModeEnabled()) {
            n_GetServerCount = n_GetServerCount();
        }
        return n_GetServerCount;
    }
    
    public static GameServerDetails GetServerDetails(final int n) {
        GameServerDetails n_GetServerDetails = null;
        if (SteamUtils.isSteamModeEnabled()) {
            n_GetServerDetails = n_GetServerDetails(n);
        }
        return n_GetServerDetails;
    }
    
    public static void Release() {
        if (SteamUtils.isSteamModeEnabled()) {
            n_Release();
        }
    }
    
    public static boolean IsRefreshing() {
        boolean n_IsRefreshing = false;
        if (SteamUtils.isSteamModeEnabled()) {
            n_IsRefreshing = n_IsRefreshing();
        }
        return n_IsRefreshing;
    }
    
    public static boolean QueryServer(final String s, final int n) {
        boolean n_QueryServer = false;
        if (SteamUtils.isSteamModeEnabled()) {
            n_QueryServer = n_QueryServer(s, n);
        }
        return n_QueryServer;
    }
    
    public static GameServerDetails GetServerDetails(final String s, final int n) {
        GameServerDetails n_GetServerDetails = null;
        if (SteamUtils.isSteamModeEnabled()) {
            n_GetServerDetails = n_GetServerDetails(s, n);
        }
        return n_GetServerDetails;
    }
    
    public static void ReleaseServerQuery(final String s, final int n) {
        if (SteamUtils.isSteamModeEnabled()) {
            n_ReleaseServerQuery(s, n);
        }
    }
    
    public static List<GameServerDetails> GetServerList() {
        final ArrayList<GameServerDetails> list = new ArrayList<GameServerDetails>();
        if (SteamUtils.isSteamModeEnabled()) {
            try {
                while (IsRefreshing()) {
                    Thread.sleep(100L);
                    SteamUtils.runLoop();
                }
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            for (int i = 0; i < GetServerCount(); ++i) {
                final GameServerDetails getServerDetails = GetServerDetails(i);
                if (getServerDetails.steamId != 0L) {
                    list.add(getServerDetails);
                }
            }
        }
        return list;
    }
    
    public static GameServerDetails GetServerDetailsSync(final String s, final int n) {
        GameServerDetails gameServerDetails = null;
        if (SteamUtils.isSteamModeEnabled()) {
            gameServerDetails = GetServerDetails(s, n);
            if (gameServerDetails == null) {
                QueryServer(s, n);
                try {
                    while (gameServerDetails == null) {
                        Thread.sleep(100L);
                        SteamUtils.runLoop();
                        gameServerDetails = GetServerDetails(s, n);
                    }
                }
                catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return gameServerDetails;
    }
    
    public static boolean RequestServerRules(final String s, final int n) {
        return n_RequestServerRules(s, n);
    }
    
    public static void setCallbackInterface(final IServerBrowserCallback callbackInterface) {
        ServerBrowser.m_callbackInterface = callbackInterface;
    }
    
    private static native boolean n_Init();
    
    private static native void n_Shutdown();
    
    private static native void n_RefreshInternetServers();
    
    private static native int n_GetServerCount();
    
    private static native GameServerDetails n_GetServerDetails(final int p0);
    
    private static native void n_Release();
    
    private static native boolean n_IsRefreshing();
    
    private static native boolean n_QueryServer(final String p0, final int p1);
    
    private static native GameServerDetails n_GetServerDetails(final String p0, final int p1);
    
    private static native void n_ReleaseServerQuery(final String p0, final int p1);
    
    private static native boolean n_RequestServerRules(final String p0, final int p1);
    
    private static void onServerRespondedCallback(final int i) {
        if (ServerBrowser.m_callbackInterface != null) {
            ServerBrowser.m_callbackInterface.OnServerResponded(i);
        }
        LuaEventManager.triggerEvent("OnSteamServerResponded", i);
    }
    
    private static void onServerFailedToRespondCallback(final int n) {
        if (ServerBrowser.m_callbackInterface != null) {
            ServerBrowser.m_callbackInterface.OnServerFailedToRespond(n);
        }
    }
    
    private static void onRefreshCompleteCallback() {
        if (ServerBrowser.m_callbackInterface != null) {
            ServerBrowser.m_callbackInterface.OnRefreshComplete();
        }
        LuaEventManager.triggerEvent("OnSteamRefreshInternetServers");
    }
    
    private static void onServerRespondedCallback(final String s, final int n) {
        if (ServerBrowser.m_callbackInterface != null) {
            ServerBrowser.m_callbackInterface.OnServerResponded(s, n);
        }
        final GameServerDetails getServerDetails = GetServerDetails(s, n);
        if (getServerDetails == null) {
            return;
        }
        final Server server = new Server();
        server.setName(getServerDetails.name);
        server.setDescription("");
        server.setSteamId(Long.toString(getServerDetails.steamId));
        server.setPing(Integer.toString(getServerDetails.ping));
        server.setPlayers(Integer.toString(getServerDetails.numPlayers));
        server.setMaxPlayers(Integer.toString(getServerDetails.maxPlayers));
        server.setOpen(true);
        if (getServerDetails.tags.contains("hidden")) {
            server.setOpen(false);
        }
        server.setIp(getServerDetails.address);
        server.setPort(Integer.toString(getServerDetails.port));
        server.setMods("");
        if (!getServerDetails.tags.replace("hidden", "").replace("hosted", "").replace(";", "").isEmpty()) {
            server.setMods(getServerDetails.tags.replace(";hosted", "").replace("hidden", ""));
        }
        server.setHosted(getServerDetails.tags.endsWith(";hosted"));
        server.setVersion("");
        server.setLastUpdate(1);
        server.setPasswordProtected(getServerDetails.passwordProtected);
        ReleaseServerQuery(s, n);
        LuaEventManager.triggerEvent("OnSteamServerResponded2", s, (double)n, server);
    }
    
    private static void onServerFailedToRespondCallback(final String s, final int n) {
        if (ServerBrowser.m_callbackInterface != null) {
            ServerBrowser.m_callbackInterface.OnServerFailedToRespond(s, n);
        }
        LuaEventManager.triggerEvent("OnSteamServerFailedToRespond2", s, (double)n);
    }
    
    private static void onRulesRefreshComplete(final String s, final int n, final String[] array) {
        if (ServerBrowser.m_callbackInterface != null) {
            ServerBrowser.m_callbackInterface.OnSteamRulesRefreshComplete(s, n);
        }
        final KahluaTable table = LuaManager.platform.newTable();
        for (int i = 0; i < array.length; i += 2) {
            table.rawset((Object)array[i], (Object)array[i + 1]);
        }
        LuaEventManager.triggerEvent("OnSteamRulesRefreshComplete", s, (double)n, table);
    }
    
    static {
        ServerBrowser.m_callbackInterface = null;
    }
}

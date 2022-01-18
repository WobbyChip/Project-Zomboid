// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.znet;

public class SteamGameServer
{
    public static int STEAM_SERVERS_DISCONNECTED;
    public static int STEAM_SERVERS_CONNECTED;
    public static int STEAM_SERVERS_CONNECTFAILURE;
    
    public static native boolean Init(final String p0, final int p1, final int p2, final int p3, final int p4, final String p5);
    
    public static native void SetProduct(final String p0);
    
    public static native void SetGameDescription(final String p0);
    
    public static native void SetModDir(final String p0);
    
    public static native void SetDedicatedServer(final boolean p0);
    
    public static native void LogOnAnonymous();
    
    public static native void EnableHeartBeats(final boolean p0);
    
    public static native void SetMaxPlayerCount(final int p0);
    
    public static native void SetServerName(final String p0);
    
    public static native void SetMapName(final String p0);
    
    public static native void SetKeyValue(final String p0, final String p1);
    
    public static native void SetGameTags(final String p0);
    
    public static native void SetRegion(final String p0);
    
    public static native boolean BUpdateUserData(final long p0, final String p1, final int p2);
    
    public static native int GetSteamServersConnectState();
    
    public static native long GetSteamID();
    
    static {
        SteamGameServer.STEAM_SERVERS_DISCONNECTED = 0;
        SteamGameServer.STEAM_SERVERS_CONNECTED = 1;
        SteamGameServer.STEAM_SERVERS_CONNECTFAILURE = 2;
    }
}

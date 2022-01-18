// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.znet;

public class SteamUser
{
    public static long GetSteamID() {
        if (SteamUtils.isSteamModeEnabled()) {
            return n_GetSteamID();
        }
        return 0L;
    }
    
    public static String GetSteamIDString() {
        if (SteamUtils.isSteamModeEnabled()) {
            return SteamUtils.convertSteamIDToString(n_GetSteamID());
        }
        return null;
    }
    
    private static native long n_GetSteamID();
}

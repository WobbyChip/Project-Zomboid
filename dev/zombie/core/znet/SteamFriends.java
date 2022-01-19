// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.znet;

import zombie.core.textures.Texture;
import zombie.Lua.LuaEventManager;
import zombie.network.GameServer;
import zombie.network.GameClient;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class SteamFriends
{
    public static final int k_EPersonaStateOffline = 0;
    public static final int k_EPersonaStateOnline = 1;
    public static final int k_EPersonaStateBusy = 2;
    public static final int k_EPersonaStateAway = 3;
    public static final int k_EPersonaStateSnooze = 4;
    public static final int k_EPersonaStateLookingToTrade = 5;
    public static final int k_EPersonaStateLookingToPlay = 6;
    
    public static void init() {
        if (SteamUtils.isSteamModeEnabled()) {
            n_Init();
        }
    }
    
    public static void shutdown() {
        if (SteamUtils.isSteamModeEnabled()) {
            n_Shutdown();
        }
    }
    
    public static native void n_Init();
    
    public static native void n_Shutdown();
    
    public static native String GetPersonaName();
    
    public static native int GetFriendCount();
    
    public static native long GetFriendByIndex(final int p0);
    
    public static native String GetFriendPersonaName(final long p0);
    
    public static native int GetFriendPersonaState(final long p0);
    
    public static native boolean InviteUserToGame(final long p0, final String p1);
    
    public static native void ActivateGameOverlay(final String p0);
    
    public static native void ActivateGameOverlayToUser(final String p0, final long p1);
    
    public static native void ActivateGameOverlayToWebPage(final String p0);
    
    public static native void SetPlayedWith(final long p0);
    
    public static native void UpdateRichPresenceConnectionInfo(final String p0, final String p1);
    
    public static List<SteamFriend> GetFriendList() {
        final ArrayList<SteamFriend> list = new ArrayList<SteamFriend>();
        for (int getFriendCount = GetFriendCount(), i = 0; i < getFriendCount; ++i) {
            final long getFriendByIndex = GetFriendByIndex(i);
            list.add(new SteamFriend(GetFriendPersonaName(getFriendByIndex), getFriendByIndex));
        }
        return list;
    }
    
    public static native int CreateSteamAvatar(final long p0, final ByteBuffer p1);
    
    private static void onStatusChangedCallback(final long i) {
        if (GameClient.bClient || GameServer.bServer) {
            LuaEventManager.triggerEvent("OnSteamFriendStatusChanged", Long.toString(i));
        }
    }
    
    private static void onAvatarChangedCallback(final long n) {
        Texture.steamAvatarChanged(n);
    }
    
    private static void onProfileNameChanged(final long n) {
        if (GameClient.bClient) {
            GameClient.instance.sendSteamProfileName(n);
        }
    }
}

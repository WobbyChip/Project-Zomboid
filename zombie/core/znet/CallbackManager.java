// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.znet;

import zombie.Lua.LuaEventManager;

public class CallbackManager implements IJoinRequestCallback
{
    public CallbackManager() {
        SteamUtils.addJoinRequestCallback(this);
    }
    
    @Override
    public void onJoinRequest(final long n, final String s) {
        LuaEventManager.triggerEvent("OnAcceptInvite", s);
    }
}

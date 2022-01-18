// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.znet;

public interface IServerBrowserCallback
{
    void OnServerResponded(final int p0);
    
    void OnServerFailedToRespond(final int p0);
    
    void OnRefreshComplete();
    
    void OnServerResponded(final String p0, final int p1);
    
    void OnServerFailedToRespond(final String p0, final int p1);
    
    void OnSteamRulesRefreshComplete(final String p0, final int p1);
}

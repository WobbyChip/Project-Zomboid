// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.znet;

public interface ISteamWorkshopCallback
{
    void onItemCreated(final long p0, final boolean p1);
    
    void onItemNotCreated(final int p0);
    
    void onItemUpdated(final boolean p0);
    
    void onItemNotUpdated(final int p0);
    
    void onItemSubscribed(final long p0);
    
    void onItemNotSubscribed(final long p0, final int p1);
    
    void onItemDownloaded(final long p0);
    
    void onItemNotDownloaded(final long p0, final int p1);
    
    void onItemQueryCompleted(final long p0, final int p1);
    
    void onItemQueryNotCompleted(final long p0, final int p1);
}

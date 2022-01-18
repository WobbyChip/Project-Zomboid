// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.znet;

import zombie.debug.DebugLog;

public class SteamUGCDetails
{
    private long ID;
    private String title;
    private long timeCreated;
    private long timeUpdated;
    private int fileSize;
    private long[] childIDs;
    
    public SteamUGCDetails(final long id, final String title, final long timeCreated, final long timeUpdated, final int fileSize, final long[] childIDs) {
        this.ID = id;
        this.title = title;
        this.timeCreated = timeCreated;
        this.timeUpdated = timeUpdated;
        this.fileSize = fileSize;
        this.childIDs = childIDs;
    }
    
    public long getID() {
        return this.ID;
    }
    
    public String getIDString() {
        return SteamUtils.convertSteamIDToString(this.ID);
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public long getTimeCreated() {
        return this.timeCreated;
    }
    
    public long getTimeUpdated() {
        return this.timeUpdated;
    }
    
    public int getFileSize() {
        return this.fileSize;
    }
    
    public long[] getChildren() {
        return this.childIDs;
    }
    
    public int getNumChildren() {
        return (this.childIDs == null) ? 0 : this.childIDs.length;
    }
    
    public long getChildID(final int n) {
        if (n < 0 || n >= this.getNumChildren()) {
            throw new IndexOutOfBoundsException("invalid child index");
        }
        return this.childIDs[n];
    }
    
    public String getState() {
        final long getItemState = SteamWorkshop.instance.GetItemState(this.ID);
        if (!SteamWorkshopItem.ItemState.Subscribed.and(getItemState)) {
            return "NotSubscribed";
        }
        if (SteamWorkshopItem.ItemState.DownloadPending.and(getItemState)) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;J)Ljava/lang/String;, SteamWorkshopItem.ItemState.toString(getItemState), this.ID));
            return "Downloading";
        }
        if (SteamWorkshopItem.ItemState.NeedsUpdate.and(getItemState)) {
            return "NeedsUpdate";
        }
        if (SteamWorkshopItem.ItemState.Installed.and(getItemState)) {
            return "Installed";
        }
        return "Error";
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network;

import zombie.core.logger.ExceptionLogger;
import java.nio.file.FileVisitor;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import zombie.core.znet.SteamWorkshopItem;
import zombie.core.znet.SteamUGCDetails;
import zombie.core.znet.ISteamWorkshopCallback;
import java.util.Iterator;
import zombie.core.znet.SteamWorkshop;
import zombie.core.znet.SteamUtils;
import java.util.ArrayList;
import zombie.debug.DebugLog;

public class GameServerWorkshopItems
{
    private static void noise(final String s) {
        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
    }
    
    public static boolean Install(final ArrayList<Long> list) {
        if (!GameServer.bServer) {
            return false;
        }
        if (list.isEmpty()) {
            return true;
        }
        final ArrayList<WorkshopItem> list2 = new ArrayList<WorkshopItem>();
        final Iterator<Long> iterator = list.iterator();
        while (iterator.hasNext()) {
            list2.add(new WorkshopItem(iterator.next()));
        }
        if (!QueryItemDetails(list2)) {
            return false;
        }
        while (true) {
            SteamUtils.runLoop();
            boolean b = false;
            for (int i = 0; i < list2.size(); ++i) {
                final WorkshopItem workshopItem = list2.get(i);
                workshopItem.update();
                if (workshopItem.state == WorkshopInstallState.Fail) {
                    return false;
                }
                if (workshopItem.state != WorkshopInstallState.Ready) {
                    b = true;
                    break;
                }
            }
            if (!b) {
                GameServer.WorkshopInstallFolders = new String[list.size()];
                GameServer.WorkshopTimeStamps = new long[list.size()];
                for (int j = 0; j < list.size(); ++j) {
                    final long longValue = list.get(j);
                    final String getItemInstallFolder = SteamWorkshop.instance.GetItemInstallFolder(longValue);
                    if (getItemInstallFolder == null) {
                        noise(invokedynamic(makeConcatWithConstants:(J)Ljava/lang/String;, longValue));
                        return false;
                    }
                    noise(invokedynamic(makeConcatWithConstants:(JLjava/lang/String;)Ljava/lang/String;, longValue, getItemInstallFolder));
                    GameServer.WorkshopInstallFolders[j] = getItemInstallFolder;
                    GameServer.WorkshopTimeStamps[j] = SteamWorkshop.instance.GetItemInstallTimeStamp(longValue);
                }
                return true;
            }
            try {
                Thread.sleep(33L);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private static boolean QueryItemDetails(final ArrayList<WorkshopItem> list) {
        final long[] array = new long[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            array[i] = list.get(i).ID;
        }
        final ItemQuery itemQuery = new ItemQuery();
        itemQuery.handle = SteamWorkshop.instance.CreateQueryUGCDetailsRequest(array, itemQuery);
        if (itemQuery.handle == 0L) {
            return false;
        }
        while (true) {
            SteamUtils.runLoop();
            if (itemQuery.isCompleted()) {
                for (final SteamUGCDetails details : itemQuery.details) {
                    for (final WorkshopItem workshopItem : list) {
                        if (workshopItem.ID == details.getID()) {
                            workshopItem.details = details;
                            break;
                        }
                    }
                }
                return true;
            }
            if (itemQuery.isNotCompleted()) {
                return false;
            }
            try {
                Thread.sleep(33L);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private enum WorkshopInstallState
    {
        CheckItemState, 
        DownloadPending, 
        Ready, 
        Fail;
        
        private static /* synthetic */ WorkshopInstallState[] $values() {
            return new WorkshopInstallState[] { WorkshopInstallState.CheckItemState, WorkshopInstallState.DownloadPending, WorkshopInstallState.Ready, WorkshopInstallState.Fail };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    private static class WorkshopItem implements ISteamWorkshopCallback
    {
        long ID;
        WorkshopInstallState state;
        long downloadStartTime;
        long downloadQueryTime;
        String error;
        SteamUGCDetails details;
        
        WorkshopItem(final long id) {
            this.state = WorkshopInstallState.CheckItemState;
            this.ID = id;
        }
        
        void update() {
            switch (this.state) {
                case CheckItemState: {
                    this.CheckItemState();
                    break;
                }
                case DownloadPending: {
                    this.DownloadPending();
                    break;
                }
            }
        }
        
        void setState(final WorkshopInstallState state) {
            GameServerWorkshopItems.noise(invokedynamic(makeConcatWithConstants:(Lzombie/network/GameServerWorkshopItems$WorkshopInstallState;Lzombie/network/GameServerWorkshopItems$WorkshopInstallState;J)Ljava/lang/String;, this.state, state, this.ID));
            this.state = state;
        }
        
        void CheckItemState() {
            long getItemState = SteamWorkshop.instance.GetItemState(this.ID);
            GameServerWorkshopItems.noise(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;J)Ljava/lang/String;, SteamWorkshopItem.ItemState.toString(getItemState), this.ID));
            if (SteamWorkshopItem.ItemState.Installed.and(getItemState) && this.details != null && this.details.getTimeCreated() != 0L && this.details.getTimeUpdated() != SteamWorkshop.instance.GetItemInstallTimeStamp(this.ID)) {
                GameServerWorkshopItems.noise("Installed status but timeUpdated doesn't match!!!");
                this.RemoveFolderForReinstall();
                getItemState |= SteamWorkshopItem.ItemState.NeedsUpdate.getValue();
            }
            if (getItemState == SteamWorkshopItem.ItemState.None.getValue() || SteamWorkshopItem.ItemState.NeedsUpdate.and(getItemState)) {
                if (SteamWorkshop.instance.DownloadItem(this.ID, true, this)) {
                    this.setState(WorkshopInstallState.DownloadPending);
                    this.downloadStartTime = System.currentTimeMillis();
                    return;
                }
                this.error = "DownloadItemFalse";
                this.setState(WorkshopInstallState.Fail);
            }
            else {
                if (SteamWorkshopItem.ItemState.Installed.and(getItemState)) {
                    this.setState(WorkshopInstallState.Ready);
                    return;
                }
                this.error = "UnknownItemState";
                this.setState(WorkshopInstallState.Fail);
            }
        }
        
        void RemoveFolderForReinstall() {
            final String getItemInstallFolder = SteamWorkshop.instance.GetItemInstallFolder(this.ID);
            if (getItemInstallFolder == null) {
                GameServerWorkshopItems.noise(invokedynamic(makeConcatWithConstants:(J)Ljava/lang/String;, this.ID));
                return;
            }
            final Path value = Paths.get(getItemInstallFolder, new String[0]);
            if (!Files.exists(value, new LinkOption[0])) {
                GameServerWorkshopItems.noise(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, getItemInstallFolder));
                return;
            }
            try {
                Files.walkFileTree(value, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(final Path path, final BasicFileAttributes basicFileAttributes) throws IOException {
                        Files.delete(path);
                        return FileVisitResult.CONTINUE;
                    }
                    
                    @Override
                    public FileVisitResult postVisitDirectory(final Path path, final IOException ex) throws IOException {
                        Files.delete(path);
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
            catch (Exception ex) {
                ExceptionLogger.logException(ex);
            }
        }
        
        void DownloadPending() {
            final long currentTimeMillis = System.currentTimeMillis();
            if (this.downloadQueryTime + 100L > currentTimeMillis) {
                return;
            }
            this.downloadQueryTime = currentTimeMillis;
            final long getItemState = SteamWorkshop.instance.GetItemState(this.ID);
            GameServerWorkshopItems.noise(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;J)Ljava/lang/String;, SteamWorkshopItem.ItemState.toString(getItemState), this.ID));
            if (SteamWorkshopItem.ItemState.NeedsUpdate.and(getItemState)) {
                final long[] array = new long[2];
                if (SteamWorkshop.instance.GetItemDownloadInfo(this.ID, array)) {
                    GameServerWorkshopItems.noise(invokedynamic(makeConcatWithConstants:(JJJ)Ljava/lang/String;, array[0], array[1], this.ID));
                }
            }
        }
        
        @Override
        public void onItemCreated(final long n, final boolean b) {
        }
        
        @Override
        public void onItemNotCreated(final int n) {
        }
        
        @Override
        public void onItemUpdated(final boolean b) {
        }
        
        @Override
        public void onItemNotUpdated(final int n) {
        }
        
        @Override
        public void onItemSubscribed(final long n) {
            GameServerWorkshopItems.noise(invokedynamic(makeConcatWithConstants:(J)Ljava/lang/String;, n));
        }
        
        @Override
        public void onItemNotSubscribed(final long n, final int n2) {
            GameServerWorkshopItems.noise(invokedynamic(makeConcatWithConstants:(JI)Ljava/lang/String;, n, n2));
        }
        
        @Override
        public void onItemDownloaded(final long n) {
            GameServerWorkshopItems.noise(invokedynamic(makeConcatWithConstants:(JJ)Ljava/lang/String;, n, System.currentTimeMillis() - this.downloadStartTime));
            if (n != this.ID) {
                return;
            }
            SteamWorkshop.instance.RemoveCallback(this);
            this.setState(WorkshopInstallState.CheckItemState);
        }
        
        @Override
        public void onItemNotDownloaded(final long n, final int n2) {
            GameServerWorkshopItems.noise(invokedynamic(makeConcatWithConstants:(JI)Ljava/lang/String;, n, n2));
            if (n != this.ID) {
                return;
            }
            SteamWorkshop.instance.RemoveCallback(this);
            this.error = "ItemNotDownloaded";
            this.setState(WorkshopInstallState.Fail);
        }
        
        @Override
        public void onItemQueryCompleted(final long n, final int n2) {
            GameServerWorkshopItems.noise(invokedynamic(makeConcatWithConstants:(JI)Ljava/lang/String;, n, n2));
        }
        
        @Override
        public void onItemQueryNotCompleted(final long n, final int n2) {
            GameServerWorkshopItems.noise(invokedynamic(makeConcatWithConstants:(JI)Ljava/lang/String;, n, n2));
        }
    }
    
    private static final class ItemQuery implements ISteamWorkshopCallback
    {
        long handle;
        ArrayList<SteamUGCDetails> details;
        boolean bCompleted;
        boolean bNotCompleted;
        
        public boolean isCompleted() {
            return this.bCompleted;
        }
        
        public boolean isNotCompleted() {
            return this.bNotCompleted;
        }
        
        @Override
        public void onItemCreated(final long n, final boolean b) {
        }
        
        @Override
        public void onItemNotCreated(final int n) {
        }
        
        @Override
        public void onItemUpdated(final boolean b) {
        }
        
        @Override
        public void onItemNotUpdated(final int n) {
        }
        
        @Override
        public void onItemSubscribed(final long n) {
        }
        
        @Override
        public void onItemNotSubscribed(final long n, final int n2) {
        }
        
        @Override
        public void onItemDownloaded(final long n) {
        }
        
        @Override
        public void onItemNotDownloaded(final long n, final int n2) {
        }
        
        @Override
        public void onItemQueryCompleted(final long n, final int n2) {
            GameServerWorkshopItems.noise(invokedynamic(makeConcatWithConstants:(JI)Ljava/lang/String;, n, n2));
            if (n != this.handle) {
                return;
            }
            SteamWorkshop.instance.RemoveCallback(this);
            final ArrayList<SteamUGCDetails> details = new ArrayList<SteamUGCDetails>();
            for (int i = 0; i < n2; ++i) {
                final SteamUGCDetails getQueryUGCResult = SteamWorkshop.instance.GetQueryUGCResult(n, i);
                if (getQueryUGCResult != null) {
                    details.add(getQueryUGCResult);
                }
            }
            this.details = details;
            SteamWorkshop.instance.ReleaseQueryUGCRequest(n);
            this.bCompleted = true;
        }
        
        @Override
        public void onItemQueryNotCompleted(final long n, final int n2) {
            GameServerWorkshopItems.noise(invokedynamic(makeConcatWithConstants:(JI)Ljava/lang/String;, n, n2));
            if (n != this.handle) {
                return;
            }
            SteamWorkshop.instance.RemoveCallback(this);
            SteamWorkshop.instance.ReleaseQueryUGCRequest(n);
            this.bNotCompleted = true;
        }
    }
}

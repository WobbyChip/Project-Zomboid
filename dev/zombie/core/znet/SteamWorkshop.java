// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.znet;

import zombie.Lua.LuaEventManager;
import zombie.debug.DebugLog;
import java.nio.file.Path;
import java.nio.file.DirectoryStream;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.FileSystems;
import zombie.ZomboidFileSystem;
import java.util.Iterator;
import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.File;
import zombie.network.GameServer;
import java.util.ArrayList;

public class SteamWorkshop implements ISteamWorkshopCallback
{
    public static final SteamWorkshop instance;
    private ArrayList<SteamWorkshopItem> stagedItems;
    private ArrayList<ISteamWorkshopCallback> callbacks;
    
    public SteamWorkshop() {
        this.stagedItems = new ArrayList<SteamWorkshopItem>();
        this.callbacks = new ArrayList<ISteamWorkshopCallback>();
    }
    
    public static void init() {
        if (SteamUtils.isSteamModeEnabled()) {
            SteamWorkshop.instance.n_Init();
        }
        if (!GameServer.bServer) {
            SteamWorkshop.instance.initWorkshopFolder();
        }
    }
    
    public static void shutdown() {
        if (SteamUtils.isSteamModeEnabled()) {
            SteamWorkshop.instance.n_Shutdown();
        }
    }
    
    private void copyFile(final File file, final File file2) {
        try {
            final FileInputStream fileInputStream = new FileInputStream(file);
            try {
                final FileOutputStream fileOutputStream = new FileOutputStream(file2);
                try {
                    fileOutputStream.getChannel().transferFrom(fileInputStream.getChannel(), 0L, file.length());
                    fileOutputStream.close();
                }
                catch (Throwable t) {
                    try {
                        fileOutputStream.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                    throw t;
                }
                fileInputStream.close();
            }
            catch (Throwable t2) {
                try {
                    fileInputStream.close();
                }
                catch (Throwable exception2) {
                    t2.addSuppressed(exception2);
                }
                throw t2;
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private void copyFileOrFolder(final File parent, final File parent2) {
        if (parent.isDirectory()) {
            if (!parent2.mkdirs()) {
                return;
            }
            final String[] list = parent.list();
            for (int i = 0; i < list.length; ++i) {
                this.copyFileOrFolder(new File(parent, list[i]), new File(parent2, list[i]));
            }
        }
        else {
            this.copyFile(parent, parent2);
        }
    }
    
    private void initWorkshopFolder() {
        final File file = new File(this.getWorkshopFolder());
        if (!file.exists() && !file.mkdirs()) {
            return;
        }
        final File file2 = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, File.separator));
        final File file3 = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getWorkshopFolder(), File.separator));
        if (file2.exists() && !file3.exists()) {
            this.copyFileOrFolder(file2, file3);
        }
    }
    
    public ArrayList<SteamWorkshopItem> loadStagedItems() {
        this.stagedItems.clear();
        final Iterator<String> iterator = this.getStageFolders().iterator();
        while (iterator.hasNext()) {
            final SteamWorkshopItem e = new SteamWorkshopItem(iterator.next());
            e.readWorkshopTxt();
            this.stagedItems.add(e);
        }
        return this.stagedItems;
    }
    
    public String getWorkshopFolder() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator);
    }
    
    public ArrayList<String> getStageFolders() {
        final ArrayList<String> list = new ArrayList<String>();
        final Path path = FileSystems.getDefault().getPath(this.getWorkshopFolder(), new String[0]);
        try {
            if (!Files.isDirectory(path, new LinkOption[0])) {
                Files.createDirectories(path, (FileAttribute<?>[])new FileAttribute[0]);
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return list;
        }
        final DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept(final Path path) throws IOException {
                return Files.isDirectory(path, new LinkOption[0]);
            }
        };
        try {
            final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path, filter);
            try {
                final Iterator<Path> iterator = directoryStream.iterator();
                while (iterator.hasNext()) {
                    list.add(iterator.next().toAbsolutePath().toString());
                }
                if (directoryStream != null) {
                    directoryStream.close();
                }
            }
            catch (Throwable t) {
                if (directoryStream != null) {
                    try {
                        directoryStream.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                }
                throw t;
            }
        }
        catch (Exception ex2) {
            ex2.printStackTrace();
        }
        return list;
    }
    
    public boolean CreateWorkshopItem(final SteamWorkshopItem steamWorkshopItem) {
        if (steamWorkshopItem.getID() != null) {
            throw new RuntimeException("can't recreate an existing item");
        }
        return this.n_CreateItem();
    }
    
    public boolean SubmitWorkshopItem(final SteamWorkshopItem steamWorkshopItem) {
        if (steamWorkshopItem.getID() == null || !SteamUtils.isValidSteamID(steamWorkshopItem.getID())) {
            throw new RuntimeException("workshop ID is required");
        }
        if (!this.n_StartItemUpdate(SteamUtils.convertStringToSteamID(steamWorkshopItem.getID()))) {
            return false;
        }
        if (!this.n_SetItemTitle(steamWorkshopItem.getTitle())) {
            return false;
        }
        if (!this.n_SetItemDescription(steamWorkshopItem.getSubmitDescription())) {
            return false;
        }
        int visibilityInteger = steamWorkshopItem.getVisibilityInteger();
        if ("Mod Template".equals(steamWorkshopItem.getTitle())) {
            visibilityInteger = 2;
        }
        if (!this.n_SetItemVisibility(visibilityInteger)) {
            return false;
        }
        if (!this.n_SetItemTags(steamWorkshopItem.getSubmitTags())) {}
        return this.n_SetItemContent(steamWorkshopItem.getContentFolder()) && this.n_SetItemPreview(steamWorkshopItem.getPreviewImage()) && this.n_SubmitItemUpdate(steamWorkshopItem.getChangeNote());
    }
    
    public boolean GetItemUpdateProgress(final long[] array) {
        return this.n_GetItemUpdateProgress(array);
    }
    
    public String[] GetInstalledItemFolders() {
        if (GameServer.bServer) {
            return GameServer.WorkshopInstallFolders;
        }
        return this.n_GetInstalledItemFolders();
    }
    
    public long GetItemState(final long n) {
        return this.n_GetItemState(n);
    }
    
    public String GetItemInstallFolder(final long n) {
        return this.n_GetItemInstallFolder(n);
    }
    
    public long GetItemInstallTimeStamp(final long n) {
        return this.n_GetItemInstallTimeStamp(n);
    }
    
    public boolean SubscribeItem(final long n, final ISteamWorkshopCallback steamWorkshopCallback) {
        if (!this.callbacks.contains(steamWorkshopCallback)) {
            this.callbacks.add(steamWorkshopCallback);
        }
        return this.n_SubscribeItem(n);
    }
    
    public boolean DownloadItem(final long n, final boolean b, final ISteamWorkshopCallback steamWorkshopCallback) {
        if (!this.callbacks.contains(steamWorkshopCallback)) {
            this.callbacks.add(steamWorkshopCallback);
        }
        return this.n_DownloadItem(n, b);
    }
    
    public boolean GetItemDownloadInfo(final long n, final long[] array) {
        return this.n_GetItemDownloadInfo(n, array);
    }
    
    public long CreateQueryUGCDetailsRequest(final long[] array, final ISteamWorkshopCallback steamWorkshopCallback) {
        if (!this.callbacks.contains(steamWorkshopCallback)) {
            this.callbacks.add(steamWorkshopCallback);
        }
        return this.n_CreateQueryUGCDetailsRequest(array);
    }
    
    public SteamUGCDetails GetQueryUGCResult(final long n, final int n2) {
        return this.n_GetQueryUGCResult(n, n2);
    }
    
    public long[] GetQueryUGCChildren(final long n, final int n2) {
        return this.n_GetQueryUGCChildren(n, n2);
    }
    
    public boolean ReleaseQueryUGCRequest(final long n) {
        return this.n_ReleaseQueryUGCRequest(n);
    }
    
    public void RemoveCallback(final ISteamWorkshopCallback o) {
        this.callbacks.remove(o);
    }
    
    public String getIDFromItemInstallFolder(final String pathname) {
        if (pathname != null && pathname.replace("\\", "/").contains("/workshop/content/108600/")) {
            final String name = new File(pathname).getName();
            if (SteamUtils.isValidSteamID(name)) {
                return name;
            }
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, name));
        }
        return null;
    }
    
    private native void n_Init();
    
    private native void n_Shutdown();
    
    private native boolean n_CreateItem();
    
    private native boolean n_StartItemUpdate(final long p0);
    
    private native boolean n_SetItemTitle(final String p0);
    
    private native boolean n_SetItemDescription(final String p0);
    
    private native boolean n_SetItemVisibility(final int p0);
    
    private native boolean n_SetItemTags(final String[] p0);
    
    private native boolean n_SetItemContent(final String p0);
    
    private native boolean n_SetItemPreview(final String p0);
    
    private native boolean n_SubmitItemUpdate(final String p0);
    
    private native boolean n_GetItemUpdateProgress(final long[] p0);
    
    private native String[] n_GetInstalledItemFolders();
    
    private native long n_GetItemState(final long p0);
    
    private native boolean n_SubscribeItem(final long p0);
    
    private native boolean n_DownloadItem(final long p0, final boolean p1);
    
    private native String n_GetItemInstallFolder(final long p0);
    
    private native long n_GetItemInstallTimeStamp(final long p0);
    
    private native boolean n_GetItemDownloadInfo(final long p0, final long[] p1);
    
    private native long n_CreateQueryUGCDetailsRequest(final long[] p0);
    
    private native SteamUGCDetails n_GetQueryUGCResult(final long p0, final int p1);
    
    private native long[] n_GetQueryUGCChildren(final long p0, final int p1);
    
    private native boolean n_ReleaseQueryUGCRequest(final long p0);
    
    @Override
    public void onItemCreated(final long n, final boolean b) {
        LuaEventManager.triggerEvent("OnSteamWorkshopItemCreated", SteamUtils.convertSteamIDToString(n), b);
    }
    
    @Override
    public void onItemNotCreated(final int i) {
        LuaEventManager.triggerEvent("OnSteamWorkshopItemNotCreated", i);
    }
    
    @Override
    public void onItemUpdated(final boolean b) {
        LuaEventManager.triggerEvent("OnSteamWorkshopItemUpdated", b);
    }
    
    @Override
    public void onItemNotUpdated(final int i) {
        LuaEventManager.triggerEvent("OnSteamWorkshopItemNotUpdated", i);
    }
    
    @Override
    public void onItemSubscribed(final long n) {
        for (int i = 0; i < this.callbacks.size(); ++i) {
            this.callbacks.get(i).onItemSubscribed(n);
        }
    }
    
    @Override
    public void onItemNotSubscribed(final long n, final int n2) {
        for (int i = 0; i < this.callbacks.size(); ++i) {
            this.callbacks.get(i).onItemNotSubscribed(n, n2);
        }
    }
    
    @Override
    public void onItemDownloaded(final long n) {
        for (int i = 0; i < this.callbacks.size(); ++i) {
            this.callbacks.get(i).onItemDownloaded(n);
        }
    }
    
    @Override
    public void onItemNotDownloaded(final long n, final int n2) {
        for (int i = 0; i < this.callbacks.size(); ++i) {
            this.callbacks.get(i).onItemNotDownloaded(n, n2);
        }
    }
    
    @Override
    public void onItemQueryCompleted(final long n, final int n2) {
        for (int i = 0; i < this.callbacks.size(); ++i) {
            this.callbacks.get(i).onItemQueryCompleted(n, n2);
        }
    }
    
    @Override
    public void onItemQueryNotCompleted(final long n, final int n2) {
        for (int i = 0; i < this.callbacks.size(); ++i) {
            this.callbacks.get(i).onItemQueryNotCompleted(n, n2);
        }
    }
    
    static {
        instance = new SteamWorkshop();
    }
}

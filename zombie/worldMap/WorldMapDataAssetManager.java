// 
// Decompiled by Procyon v0.5.36
// 

package zombie.worldMap;

import zombie.PredicatedFileWatcher;
import zombie.DebugFileWatcher;
import zombie.asset.AssetPath;
import zombie.debug.DebugLog;
import zombie.fileSystem.FileTask;
import zombie.fileSystem.FileSystem;
import zombie.asset.AssetTask;
import zombie.asset.AssetTask_RunFileTask;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import zombie.asset.Asset;
import zombie.asset.AssetManager;

public final class WorldMapDataAssetManager extends AssetManager
{
    public static final WorldMapDataAssetManager instance;
    
    @Override
    protected void startLoading(final Asset asset) {
        final WorldMapData worldMapData = (WorldMapData)asset;
        final FileSystem fileSystem = this.getOwner().getFileSystem();
        final String path = asset.getPath().getPath();
        FileTask fileTask;
        if (Files.exists(Paths.get(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, path), new String[0]), new LinkOption[0])) {
            fileTask = new FileTask_LoadWorldMapBinary(worldMapData, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, path), fileSystem, o -> this.loadCallback(worldMapData, o));
        }
        else {
            fileTask = new FileTask_LoadWorldMapXML(worldMapData, path, fileSystem, o2 -> this.loadCallback(worldMapData, o2));
        }
        fileTask.setPriority(4);
        final AssetTask_RunFileTask assetTask_RunFileTask = new AssetTask_RunFileTask(fileTask, asset);
        this.setTask(asset, assetTask_RunFileTask);
        assetTask_RunFileTask.execute();
    }
    
    private void loadCallback(final WorldMapData worldMapData, final Object o) {
        if (o == Boolean.TRUE) {
            worldMapData.onLoaded();
            this.onLoadingSucceeded(worldMapData);
        }
        else {
            DebugLog.General.warn(invokedynamic(makeConcatWithConstants:(Lzombie/asset/AssetPath;)Ljava/lang/String;, worldMapData.getPath()));
            this.onLoadingFailed(worldMapData);
        }
    }
    
    @Override
    protected Asset createAsset(final AssetPath assetPath, final AssetParams assetParams) {
        final WorldMapData worldMapData = new WorldMapData(assetPath, this, assetParams);
        DebugFileWatcher.instance.add(new PredicatedFileWatcher(assetPath.getPath(), p2 -> this.reload(worldMapData, assetParams)));
        return worldMapData;
    }
    
    @Override
    protected void destroyAsset(final Asset asset) {
    }
    
    static {
        instance = new WorldMapDataAssetManager();
    }
}

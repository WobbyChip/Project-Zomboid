// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.model;

import zombie.ZomboidFileSystem;
import zombie.util.StringUtils;
import zombie.asset.AssetPath;
import zombie.debug.DebugLog;
import zombie.core.skinnedmodel.model.jassimp.ProcessedAiScene;
import zombie.asset.AssetTask;
import zombie.fileSystem.FileTask;
import zombie.asset.AssetTask_RunFileTask;
import zombie.asset.Asset;
import zombie.DebugFileWatcher;
import zombie.PredicatedFileWatcher;
import java.util.HashSet;
import zombie.asset.AssetManager;

public final class MeshAssetManager extends AssetManager
{
    public static final MeshAssetManager instance;
    private final HashSet<String> m_watchedFiles;
    private final PredicatedFileWatcher m_watcher;
    
    private MeshAssetManager() {
        this.m_watchedFiles = new HashSet<String>();
        this.m_watcher = new PredicatedFileWatcher(MeshAssetManager::isWatched, MeshAssetManager::watchedFileChanged);
        DebugFileWatcher.instance.add(this.m_watcher);
    }
    
    @Override
    protected void startLoading(final Asset asset) {
        final ModelMesh modelMesh;
        final FileTask_LoadMesh fileTask_LoadMesh = new FileTask_LoadMesh((ModelMesh)asset, this.getOwner().getFileSystem(), o -> this.loadCallback(modelMesh, o));
        fileTask_LoadMesh.setPriority(6);
        final AssetTask_RunFileTask assetTask_RunFileTask = new AssetTask_RunFileTask(fileTask_LoadMesh, asset);
        this.setTask(asset, assetTask_RunFileTask);
        assetTask_RunFileTask.execute();
    }
    
    private void loadCallback(final ModelMesh modelMesh, final Object o) {
        if (o instanceof ProcessedAiScene) {
            modelMesh.onLoadedX((ProcessedAiScene)o);
            this.onLoadingSucceeded(modelMesh);
        }
        else if (o instanceof ModelTxt) {
            modelMesh.onLoadedTxt((ModelTxt)o);
            this.onLoadingSucceeded(modelMesh);
        }
        else {
            DebugLog.General.warn(invokedynamic(makeConcatWithConstants:(Lzombie/asset/AssetPath;)Ljava/lang/String;, modelMesh.getPath()));
            this.onLoadingFailed(modelMesh);
        }
    }
    
    @Override
    protected Asset createAsset(final AssetPath assetPath, final AssetParams assetParams) {
        return new ModelMesh(assetPath, this, (ModelMesh.MeshAssetParams)assetParams);
    }
    
    @Override
    protected void destroyAsset(final Asset asset) {
    }
    
    private static boolean isWatched(final String s) {
        return (StringUtils.endsWithIgnoreCase(s, ".fbx") || StringUtils.endsWithIgnoreCase(s, ".x")) && MeshAssetManager.instance.m_watchedFiles.contains(ZomboidFileSystem.instance.getString(s));
    }
    
    private static void watchedFileChanged(final String s) {
        DebugLog.Asset.printf("%s changed\n", s);
        MeshAssetManager.instance.getAssetTable().forEachValue(asset -> {
            final ModelMesh modelMesh = (ModelMesh)asset;
            if (!modelMesh.isEmpty() && s.equalsIgnoreCase(modelMesh.m_fullPath)) {
                final ModelMesh.MeshAssetParams meshAssetParams = new ModelMesh.MeshAssetParams();
                meshAssetParams.animationsMesh = modelMesh.m_animationsMesh;
                meshAssetParams.bStatic = modelMesh.bStatic;
                MeshAssetManager.instance.reload(asset, meshAssetParams);
            }
            return true;
        });
    }
    
    public void addWatchedFile(final String e) {
        this.m_watchedFiles.add(e);
    }
    
    static {
        instance = new MeshAssetManager();
    }
}

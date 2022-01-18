// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.model;

import java.util.Set;
import jassimp.Jassimp;
import zombie.fileSystem.FileSystem;
import zombie.fileSystem.IFileTaskCallback;
import jassimp.AiPostProcessSteps;
import java.util.EnumSet;
import zombie.asset.AssetPath;
import jassimp.AiScene;
import zombie.asset.AssetTask;
import zombie.fileSystem.FileTask;
import zombie.asset.AssetTask_RunFileTask;
import zombie.asset.Asset;
import zombie.asset.AssetManager;

@Deprecated
public final class AiSceneAssetManager extends AssetManager
{
    public static final AiSceneAssetManager instance;
    
    @Override
    protected void startLoading(final Asset asset) {
        final AssetTask_RunFileTask assetTask_RunFileTask = new AssetTask_RunFileTask(new FileTask_LoadAiScene(asset.getPath().getPath(), ((AiSceneAsset)asset).m_post_process_step_set, o -> this.onFileTaskFinished((AiSceneAsset)asset, o), asset.getAssetManager().getOwner().getFileSystem()), asset);
        this.setTask(asset, assetTask_RunFileTask);
        assetTask_RunFileTask.execute();
    }
    
    public void onFileTaskFinished(final AiSceneAsset aiSceneAsset, final Object o) {
        if (o instanceof AiScene) {
            aiSceneAsset.m_scene = (AiScene)o;
            this.onLoadingSucceeded(aiSceneAsset);
        }
        else {
            this.onLoadingFailed(aiSceneAsset);
        }
    }
    
    @Override
    protected Asset createAsset(final AssetPath assetPath, final AssetParams assetParams) {
        return new AiSceneAsset(assetPath, this, (AiSceneAsset.AiSceneAssetParams)assetParams);
    }
    
    @Override
    protected void destroyAsset(final Asset asset) {
    }
    
    static {
        instance = new AiSceneAssetManager();
    }
    
    static class FileTask_LoadAiScene extends FileTask
    {
        String m_filename;
        EnumSet<AiPostProcessSteps> m_post_process_step_set;
        
        public FileTask_LoadAiScene(final String filename, final EnumSet<AiPostProcessSteps> post_process_step_set, final IFileTaskCallback fileTaskCallback, final FileSystem fileSystem) {
            super(fileSystem, fileTaskCallback);
            this.m_filename = filename;
            this.m_post_process_step_set = post_process_step_set;
        }
        
        @Override
        public String getErrorMessage() {
            return this.m_filename;
        }
        
        @Override
        public void done() {
            this.m_filename = null;
            this.m_post_process_step_set = null;
        }
        
        @Override
        public Object call() throws Exception {
            return Jassimp.importFile(this.m_filename, (Set)this.m_post_process_step_set);
        }
    }
}

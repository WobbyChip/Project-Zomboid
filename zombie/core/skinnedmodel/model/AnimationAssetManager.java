// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.model;

import zombie.asset.AssetPath;
import zombie.debug.DebugLog;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.model.jassimp.ProcessedAiScene;
import zombie.asset.AssetTask;
import zombie.fileSystem.FileTask;
import zombie.asset.AssetTask_RunFileTask;
import zombie.asset.Asset;
import zombie.asset.AssetManager;

public final class AnimationAssetManager extends AssetManager
{
    public static final AnimationAssetManager instance;
    
    @Override
    protected void startLoading(final Asset asset) {
        final AnimationAsset animationAsset;
        final FileTask_LoadAnimation fileTask_LoadAnimation = new FileTask_LoadAnimation((AnimationAsset)asset, this.getOwner().getFileSystem(), o -> this.loadCallback(animationAsset, o));
        fileTask_LoadAnimation.setPriority(4);
        final String lowerCase = asset.getPath().getPath().toLowerCase();
        if (lowerCase.endsWith("bob_idle") || lowerCase.endsWith("bob_walk") || lowerCase.endsWith("bob_run")) {
            fileTask_LoadAnimation.setPriority(6);
        }
        final AssetTask_RunFileTask assetTask_RunFileTask = new AssetTask_RunFileTask(fileTask_LoadAnimation, asset);
        this.setTask(asset, assetTask_RunFileTask);
        assetTask_RunFileTask.execute();
    }
    
    private void loadCallback(final AnimationAsset animationAsset, final Object o) {
        if (o instanceof ProcessedAiScene) {
            animationAsset.onLoadedX((ProcessedAiScene)o);
            this.onLoadingSucceeded(animationAsset);
            ModelManager.instance.animationAssetLoaded(animationAsset);
        }
        else if (o instanceof ModelTxt) {
            animationAsset.onLoadedTxt((ModelTxt)o);
            this.onLoadingSucceeded(animationAsset);
            ModelManager.instance.animationAssetLoaded(animationAsset);
        }
        else {
            DebugLog.General.warn(invokedynamic(makeConcatWithConstants:(Lzombie/asset/AssetPath;)Ljava/lang/String;, animationAsset.getPath()));
            this.onLoadingFailed(animationAsset);
        }
    }
    
    @Override
    protected Asset createAsset(final AssetPath assetPath, final AssetParams assetParams) {
        return new AnimationAsset(assetPath, this, (AnimationAsset.AnimationAssetParams)assetParams);
    }
    
    @Override
    protected void destroyAsset(final Asset asset) {
    }
    
    static {
        instance = new AnimationAssetManager();
    }
}

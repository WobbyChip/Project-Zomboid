// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.model;

import zombie.core.skinnedmodel.model.jassimp.ProcessedAiScene;
import zombie.asset.AssetManager;
import zombie.asset.AssetPath;
import zombie.asset.AssetType;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.animation.AnimationClip;
import java.util.HashMap;
import zombie.asset.Asset;

public final class AnimationAsset extends Asset
{
    public HashMap<String, AnimationClip> AnimationClips;
    public AnimationAssetParams assetParams;
    public SkinningData skinningData;
    public String modelManagerKey;
    public ModelManager.ModAnimations modAnimations;
    public static final AssetType ASSET_TYPE;
    
    public AnimationAsset(final AssetPath assetPath, final AssetManager assetManager, final AnimationAssetParams assetParams) {
        super(assetPath, assetManager);
        this.assetParams = assetParams;
    }
    
    protected void onLoadedX(final ProcessedAiScene processedAiScene) {
        processedAiScene.applyToAnimation(this);
    }
    
    protected void onLoadedTxt(final ModelTxt modelTxt) {
        ModelLoader.instance.applyToAnimation(modelTxt, this);
    }
    
    public void onBeforeReady() {
        super.onBeforeReady();
        if (this.assetParams != null) {
            this.assetParams.animationsMesh = null;
            this.assetParams = null;
        }
    }
    
    @Override
    public void setAssetParams(final AssetManager.AssetParams assetParams) {
        this.assetParams = (AnimationAssetParams)assetParams;
    }
    
    @Override
    public AssetType getType() {
        return AnimationAsset.ASSET_TYPE;
    }
    
    static {
        ASSET_TYPE = new AssetType("Animation");
    }
    
    public static final class AnimationAssetParams extends AssetManager.AssetParams
    {
        public ModelMesh animationsMesh;
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.model;

import zombie.asset.AssetManager;
import zombie.asset.AssetPath;
import zombie.asset.AssetType;
import jassimp.AiPostProcessSteps;
import java.util.EnumSet;
import jassimp.AiScene;
import zombie.asset.Asset;

@Deprecated
public final class AiSceneAsset extends Asset
{
    AiScene m_scene;
    EnumSet<AiPostProcessSteps> m_post_process_step_set;
    AiSceneAssetParams assetParams;
    public static final AssetType ASSET_TYPE;
    
    protected AiSceneAsset(final AssetPath assetPath, final AssetManager assetManager, final AiSceneAssetParams assetParams) {
        super(assetPath, assetManager);
        this.assetParams = assetParams;
        this.m_scene = null;
        this.m_post_process_step_set = assetParams.post_process_step_set;
    }
    
    @Override
    public AssetType getType() {
        return AiSceneAsset.ASSET_TYPE;
    }
    
    static {
        ASSET_TYPE = new AssetType("AiScene");
    }
    
    public static final class AiSceneAssetParams extends AssetManager.AssetParams
    {
        EnumSet<AiPostProcessSteps> post_process_step_set;
    }
}

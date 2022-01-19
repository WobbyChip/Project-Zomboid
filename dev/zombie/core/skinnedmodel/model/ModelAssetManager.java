// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.model;

import zombie.asset.AssetPath;
import zombie.asset.Asset;
import zombie.asset.AssetManager;

public final class ModelAssetManager extends AssetManager
{
    public static final ModelAssetManager instance;
    
    @Override
    protected void startLoading(final Asset asset) {
    }
    
    @Override
    protected Asset createAsset(final AssetPath assetPath, final AssetParams assetParams) {
        return new Model(assetPath, this, (Model.ModelAssetParams)assetParams);
    }
    
    @Override
    protected void destroyAsset(final Asset asset) {
    }
    
    static {
        instance = new ModelAssetManager();
    }
}

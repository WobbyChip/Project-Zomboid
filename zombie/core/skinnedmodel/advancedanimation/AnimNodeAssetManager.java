// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.advancedanimation;

import zombie.asset.AssetPath;
import zombie.asset.Asset;
import zombie.asset.AssetManager;

public class AnimNodeAssetManager extends AssetManager
{
    public static final AnimNodeAssetManager instance;
    
    @Override
    protected void startLoading(final Asset asset) {
        final AnimNodeAsset animNodeAsset = (AnimNodeAsset)asset;
        animNodeAsset.m_animNode = AnimNode.Parse(asset.getPath().getPath());
        if (animNodeAsset.m_animNode == null) {
            this.onLoadingFailed(asset);
        }
        else {
            this.onLoadingSucceeded(asset);
        }
    }
    
    @Override
    public void onStateChanged(final Asset.State state, final Asset.State state2, final Asset asset) {
        super.onStateChanged(state, state2, asset);
        if (state2 == Asset.State.READY) {}
    }
    
    @Override
    protected Asset createAsset(final AssetPath assetPath, final AssetParams assetParams) {
        return new AnimNodeAsset(assetPath, this);
    }
    
    @Override
    protected void destroyAsset(final Asset asset) {
    }
    
    static {
        instance = new AnimNodeAssetManager();
    }
}

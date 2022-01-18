// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.advancedanimation;

import zombie.asset.AssetManager;
import zombie.asset.AssetPath;
import zombie.asset.AssetType;
import zombie.asset.Asset;

public class AnimNodeAsset extends Asset
{
    public static final AssetType ASSET_TYPE;
    public AnimNode m_animNode;
    
    protected AnimNodeAsset(final AssetPath assetPath, final AssetManager assetManager) {
        super(assetPath, assetManager);
    }
    
    @Override
    public AssetType getType() {
        return AnimNodeAsset.ASSET_TYPE;
    }
    
    static {
        ASSET_TYPE = new AssetType("AnimNode");
    }
}

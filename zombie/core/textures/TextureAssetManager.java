// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.textures;

import zombie.asset.AssetPath;
import zombie.asset.Asset;
import zombie.asset.AssetManager;

public final class TextureAssetManager extends AssetManager
{
    public static final TextureAssetManager instance;
    
    @Override
    protected void startLoading(final Asset asset) {
    }
    
    @Override
    protected Asset createAsset(final AssetPath assetPath, final AssetParams assetParams) {
        return new Texture(assetPath, this, (Texture.TextureAssetParams)assetParams);
    }
    
    @Override
    protected void destroyAsset(final Asset asset) {
    }
    
    static {
        instance = new TextureAssetManager();
    }
}

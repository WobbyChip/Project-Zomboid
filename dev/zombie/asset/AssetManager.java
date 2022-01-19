// 
// Decompiled by Procyon v0.5.36
// 

package zombie.asset;

import gnu.trove.map.hash.THashMap;
import zombie.debug.DebugLog;
import zombie.fileSystem.IFile;
import java.util.Iterator;
import java.util.ArrayList;

public abstract class AssetManager implements AssetStateObserver
{
    private final AssetTable m_assets;
    private AssetManagers m_owner;
    private boolean m_is_unload_enabled;
    
    public AssetManager() {
        this.m_assets = new AssetTable();
        this.m_is_unload_enabled = false;
    }
    
    public void create(final AssetType assetType, final AssetManagers owner) {
        owner.add(assetType, this);
        this.m_owner = owner;
    }
    
    public void destroy() {
        this.m_assets.forEachValue(asset -> {
            if (!asset.isEmpty()) {
                DebugLog.Asset.println(invokedynamic(makeConcatWithConstants:(Lzombie/asset/AssetPath;)Ljava/lang/String;, asset.getPath()));
            }
            this.destroyAsset(asset);
            return true;
        });
    }
    
    public void removeUnreferenced() {
        if (!this.m_is_unload_enabled) {
            return;
        }
        final ArrayList<Asset> list = new ArrayList<Asset>();
        this.m_assets.forEachValue(e -> {
            if (e.getRefCount() == 0) {
                list.add(e);
            }
            return true;
        });
        for (final Asset asset : list) {
            this.m_assets.remove((Object)asset.getPath());
            this.destroyAsset(asset);
        }
    }
    
    public Asset load(final AssetPath assetPath) {
        return this.load(assetPath, null);
    }
    
    public Asset load(final AssetPath assetPath, final AssetParams assetParams) {
        if (!assetPath.isValid()) {
            return null;
        }
        Asset asset = this.get(assetPath);
        if (asset == null) {
            asset = this.createAsset(assetPath, assetParams);
            this.m_assets.put((Object)assetPath.getPath(), (Object)asset);
        }
        if (asset.isEmpty() && asset.m_priv.m_desired_state == Asset.State.EMPTY) {
            this.doLoad(asset, assetParams);
        }
        asset.addRef();
        return asset;
    }
    
    public void load(final Asset asset) {
        if (asset.isEmpty() && asset.m_priv.m_desired_state == Asset.State.EMPTY) {
            this.doLoad(asset, null);
        }
        asset.addRef();
    }
    
    public void unload(final AssetPath assetPath) {
        final Asset value = this.get(assetPath);
        if (value != null) {
            this.unload(value);
        }
    }
    
    public void unload(final Asset asset) {
        final int rmRef = asset.rmRef();
        assert rmRef >= 0;
        if (rmRef == 0 && this.m_is_unload_enabled) {
            this.doUnload(asset);
        }
    }
    
    public void reload(final AssetPath assetPath) {
        final Asset value = this.get(assetPath);
        if (value != null) {
            this.reload(value);
        }
    }
    
    public void reload(final Asset asset) {
        this.reload(asset, null);
    }
    
    public void reload(final Asset asset, final AssetParams assetParams) {
        this.doUnload(asset);
        this.doLoad(asset, assetParams);
    }
    
    public void enableUnload(final boolean is_unload_enabled) {
        if (!(this.m_is_unload_enabled = is_unload_enabled)) {
            return;
        }
        this.m_assets.forEachValue(asset -> {
            if (asset.getRefCount() == 0) {
                this.doUnload(asset);
            }
            return true;
        });
    }
    
    private void doLoad(final Asset asset, final AssetParams assetParams) {
        if (asset.m_priv.m_desired_state == Asset.State.READY) {
            return;
        }
        asset.m_priv.m_desired_state = Asset.State.READY;
        asset.setAssetParams(assetParams);
        this.startLoading(asset);
    }
    
    private void doUnload(final Asset asset) {
        if (asset.m_priv.m_task != null) {
            asset.m_priv.m_task.cancel();
            asset.m_priv.m_task = null;
        }
        asset.m_priv.m_desired_state = Asset.State.EMPTY;
        this.unloadData(asset);
        assert asset.m_priv.m_empty_dep_count <= 1;
        asset.m_priv.m_empty_dep_count = 1;
        asset.m_priv.m_failed_dep_count = 0;
        asset.m_priv.checkState();
    }
    
    @Override
    public void onStateChanged(final Asset.State state, final Asset.State state2, final Asset asset) {
    }
    
    protected void startLoading(final Asset asset) {
        if (asset.m_priv.m_task != null) {
            return;
        }
        (asset.m_priv.m_task = new AssetTask_LoadFromFileAsync(asset, false)).execute();
    }
    
    protected final void onLoadingSucceeded(final Asset asset) {
        asset.m_priv.onLoadingSucceeded();
    }
    
    protected final void onLoadingFailed(final Asset asset) {
        asset.m_priv.onLoadingFailed();
    }
    
    protected final void setTask(final Asset asset, final AssetTask task) {
        if (asset.m_priv.m_task != null) {
            if (task == null) {
                asset.m_priv.m_task = null;
            }
            return;
        }
        asset.m_priv.m_task = task;
    }
    
    protected boolean loadDataFromFile(final Asset asset, final IFile file) {
        throw new RuntimeException("not implemented");
    }
    
    protected void unloadData(final Asset asset) {
    }
    
    public AssetTable getAssetTable() {
        return this.m_assets;
    }
    
    public AssetManagers getOwner() {
        return this.m_owner;
    }
    
    protected abstract Asset createAsset(final AssetPath p0, final AssetParams p1);
    
    protected abstract void destroyAsset(final Asset p0);
    
    protected Asset get(final AssetPath assetPath) {
        return (Asset)this.m_assets.get((Object)assetPath.getPath());
    }
    
    public static final class AssetTable extends THashMap<String, Asset>
    {
    }
    
    public static class AssetParams
    {
    }
}

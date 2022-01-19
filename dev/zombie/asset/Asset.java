// 
// Decompiled by Procyon v0.5.36
// 

package zombie.asset;

import java.util.ArrayList;

public abstract class Asset
{
    protected final AssetManager m_asset_manager;
    private AssetPath m_path;
    private int m_ref_count;
    final PRIVATE m_priv;
    
    protected Asset(final AssetPath path, final AssetManager asset_manager) {
        this.m_priv = new PRIVATE();
        this.m_ref_count = 0;
        this.m_path = path;
        this.m_asset_manager = asset_manager;
    }
    
    public abstract AssetType getType();
    
    public State getState() {
        return this.m_priv.m_current_state;
    }
    
    public boolean isEmpty() {
        return this.m_priv.m_current_state == State.EMPTY;
    }
    
    public boolean isReady() {
        return this.m_priv.m_current_state == State.READY;
    }
    
    public boolean isFailure() {
        return this.m_priv.m_current_state == State.FAILURE;
    }
    
    public void onCreated(final State state) {
        this.m_priv.onCreated(state);
    }
    
    public int getRefCount() {
        return this.m_ref_count;
    }
    
    public ObserverCallback getObserverCb() {
        if (this.m_priv.m_cb == null) {
            this.m_priv.m_cb = new ObserverCallback();
        }
        return this.m_priv.m_cb;
    }
    
    public AssetPath getPath() {
        return this.m_path;
    }
    
    public AssetManager getAssetManager() {
        return this.m_asset_manager;
    }
    
    protected void onBeforeReady() {
    }
    
    protected void onBeforeEmpty() {
    }
    
    public void addDependency(final Asset asset) {
        this.m_priv.addDependency(asset);
    }
    
    public void removeDependency(final Asset asset) {
        this.m_priv.removeDependency(asset);
    }
    
    int addRef() {
        return ++this.m_ref_count;
    }
    
    int rmRef() {
        return --this.m_ref_count;
    }
    
    public void setAssetParams(final AssetManager.AssetParams assetParams) {
    }
    
    final class PRIVATE implements AssetStateObserver
    {
        State m_current_state;
        State m_desired_state;
        int m_empty_dep_count;
        int m_failed_dep_count;
        ObserverCallback m_cb;
        AssetTask m_task;
        
        PRIVATE() {
            this.m_current_state = State.EMPTY;
            this.m_desired_state = State.EMPTY;
            this.m_empty_dep_count = 1;
            this.m_failed_dep_count = 0;
            this.m_task = null;
        }
        
        void onCreated(final State current_state) {
            assert this.m_empty_dep_count == 1;
            assert this.m_failed_dep_count == 0;
            this.m_current_state = current_state;
            this.m_desired_state = State.READY;
            this.m_failed_dep_count = ((current_state == State.FAILURE) ? 1 : 0);
            this.m_empty_dep_count = 0;
        }
        
        void addDependency(final Asset asset) {
            assert this.m_desired_state != State.EMPTY;
            ((ArrayList<PRIVATE>)asset.getObserverCb()).add(this);
            if (asset.isEmpty()) {
                ++this.m_empty_dep_count;
            }
            if (asset.isFailure()) {
                ++this.m_failed_dep_count;
            }
            this.checkState();
        }
        
        void removeDependency(final Asset asset) {
            asset.getObserverCb().remove(this);
            if (asset.isEmpty()) {
                assert this.m_empty_dep_count > 0;
                --this.m_empty_dep_count;
            }
            if (asset.isFailure()) {
                assert this.m_failed_dep_count > 0;
                --this.m_failed_dep_count;
            }
            this.checkState();
        }
        
        @Override
        public void onStateChanged(final State state, final State state2, final Asset asset) {
            assert state != state2;
            assert this.m_desired_state != State.EMPTY;
            if (state == State.EMPTY) {
                assert this.m_empty_dep_count > 0;
                --this.m_empty_dep_count;
            }
            if (state == State.FAILURE) {
                assert this.m_failed_dep_count > 0;
                --this.m_failed_dep_count;
            }
            if (state2 == State.EMPTY) {
                ++this.m_empty_dep_count;
            }
            if (state2 == State.FAILURE) {
                ++this.m_failed_dep_count;
            }
            this.checkState();
        }
        
        void onLoadingSucceeded() {
            assert this.m_current_state != State.READY;
            assert this.m_empty_dep_count == 1;
            --this.m_empty_dep_count;
            this.m_task = null;
            this.checkState();
        }
        
        void onLoadingFailed() {
            assert this.m_current_state != State.READY;
            assert this.m_empty_dep_count == 1;
            ++this.m_failed_dep_count;
            --this.m_empty_dep_count;
            this.m_task = null;
            this.checkState();
        }
        
        void checkState() {
            final State current_state = this.m_current_state;
            if (this.m_failed_dep_count > 0 && this.m_current_state != State.FAILURE) {
                this.m_current_state = State.FAILURE;
                Asset.this.getAssetManager().onStateChanged(current_state, this.m_current_state, Asset.this);
                if (this.m_cb != null) {
                    this.m_cb.invoke(current_state, this.m_current_state, Asset.this);
                }
            }
            if (this.m_failed_dep_count == 0) {
                if (this.m_empty_dep_count == 0 && this.m_current_state != State.READY && this.m_desired_state != State.EMPTY) {
                    Asset.this.onBeforeReady();
                    this.m_current_state = State.READY;
                    Asset.this.getAssetManager().onStateChanged(current_state, this.m_current_state, Asset.this);
                    if (this.m_cb != null) {
                        this.m_cb.invoke(current_state, this.m_current_state, Asset.this);
                    }
                }
                if (this.m_empty_dep_count > 0 && this.m_current_state != State.EMPTY) {
                    Asset.this.onBeforeEmpty();
                    this.m_current_state = State.EMPTY;
                    Asset.this.getAssetManager().onStateChanged(current_state, this.m_current_state, Asset.this);
                    if (this.m_cb != null) {
                        this.m_cb.invoke(current_state, this.m_current_state, Asset.this);
                    }
                }
            }
        }
    }
    
    public enum State
    {
        EMPTY, 
        READY, 
        FAILURE;
        
        private static /* synthetic */ State[] $values() {
            return new State[] { State.EMPTY, State.READY, State.FAILURE };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    public static final class ObserverCallback extends ArrayList<AssetStateObserver>
    {
        public void invoke(final State state, final State state2, final Asset asset) {
            for (int size = this.size(), i = 0; i < size; ++i) {
                this.get(i).onStateChanged(state, state2, asset);
            }
        }
    }
}

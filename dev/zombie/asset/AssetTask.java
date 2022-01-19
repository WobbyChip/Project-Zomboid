// 
// Decompiled by Procyon v0.5.36
// 

package zombie.asset;

public abstract class AssetTask
{
    public Asset m_asset;
    
    public AssetTask(final Asset asset) {
        this.m_asset = asset;
    }
    
    public abstract void execute();
    
    public abstract void cancel();
}

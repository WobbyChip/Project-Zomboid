// 
// Decompiled by Procyon v0.5.36
// 

package zombie.asset;

import zombie.util.StringUtils;

public final class AssetPath
{
    protected String m_path;
    
    public AssetPath(final String path) {
        this.m_path = path;
    }
    
    public boolean isValid() {
        return !StringUtils.isNullOrEmpty(this.m_path);
    }
    
    public int getHash() {
        return this.m_path.hashCode();
    }
    
    public String getPath() {
        return this.m_path;
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName(), this.getPath());
    }
}

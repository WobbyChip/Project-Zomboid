// 
// Decompiled by Procyon v0.5.36
// 

package zombie.debug.options;

public interface IDebugOption
{
    String getName();
    
    IDebugOptionGroup getParent();
    
    void setParent(final IDebugOptionGroup p0);
}

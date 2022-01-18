// 
// Decompiled by Procyon v0.5.36
// 

package zombie.debug.options;

public interface IDebugOptionGroup extends IDebugOption
{
    Iterable<IDebugOption> getChildren();
    
    void addChild(final IDebugOption p0);
    
    void onChildAdded(final IDebugOption p0);
    
    void onDescendantAdded(final IDebugOption p0);
}

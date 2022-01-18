// 
// Decompiled by Procyon v0.5.36
// 

package zombie.radio.globals;

public enum EditGlobalOps
{
    set, 
    add, 
    sub;
    
    private static /* synthetic */ EditGlobalOps[] $values() {
        return new EditGlobalOps[] { EditGlobalOps.set, EditGlobalOps.add, EditGlobalOps.sub };
    }
    
    static {
        $VALUES = $values();
    }
}

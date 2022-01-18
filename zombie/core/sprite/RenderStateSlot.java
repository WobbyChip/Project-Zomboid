// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.sprite;

public enum RenderStateSlot
{
    Populating(0), 
    Ready(1), 
    Rendering(2);
    
    private final int m_index;
    
    private RenderStateSlot(final int index) {
        this.m_index = index;
    }
    
    public int index() {
        return this.m_index;
    }
    
    public int count() {
        return 3;
    }
    
    private static /* synthetic */ RenderStateSlot[] $values() {
        return new RenderStateSlot[] { RenderStateSlot.Populating, RenderStateSlot.Ready, RenderStateSlot.Rendering };
    }
    
    static {
        $VALUES = $values();
    }
}

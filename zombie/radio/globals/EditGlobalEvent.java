// 
// Decompiled by Procyon v0.5.36
// 

package zombie.radio.globals;

public enum EditGlobalEvent
{
    OnSetActive, 
    OnPostDelay, 
    OnPlayerListens, 
    OnPlayerListensOnce, 
    OnBroadcastSetActive, 
    OnBroadcastRemove, 
    OnExit;
    
    private static /* synthetic */ EditGlobalEvent[] $values() {
        return new EditGlobalEvent[] { EditGlobalEvent.OnSetActive, EditGlobalEvent.OnPostDelay, EditGlobalEvent.OnPlayerListens, EditGlobalEvent.OnPlayerListensOnce, EditGlobalEvent.OnBroadcastSetActive, EditGlobalEvent.OnBroadcastRemove, EditGlobalEvent.OnExit };
    }
    
    static {
        $VALUES = $values();
    }
}

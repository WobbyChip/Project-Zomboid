// 
// Decompiled by Procyon v0.5.36
// 

package zombie.debug;

public enum LogSeverity
{
    Trace, 
    General, 
    Warning, 
    Error;
    
    private static /* synthetic */ LogSeverity[] $values() {
        return new LogSeverity[] { LogSeverity.Trace, LogSeverity.General, LogSeverity.Warning, LogSeverity.Error };
    }
    
    static {
        $VALUES = $values();
    }
}

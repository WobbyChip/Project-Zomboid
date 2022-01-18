// 
// Decompiled by Procyon v0.5.36
// 

package zombie.fileSystem;

public enum FileSeekMode
{
    BEGIN, 
    END, 
    CURRENT;
    
    private static /* synthetic */ FileSeekMode[] $values() {
        return new FileSeekMode[] { FileSeekMode.BEGIN, FileSeekMode.END, FileSeekMode.CURRENT };
    }
    
    static {
        $VALUES = $values();
    }
}

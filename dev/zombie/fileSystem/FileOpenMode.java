// 
// Decompiled by Procyon v0.5.36
// 

package zombie.fileSystem;

public final class FileOpenMode
{
    public static final int NONE = 0;
    public static final int READ = 1;
    public static final int WRITE = 2;
    public static final int OPEN = 4;
    public static final int CREATE = 8;
    public static final int STREAM = 16;
    public static final int CREATE_AND_WRITE = 10;
    public static final int OPEN_AND_READ = 5;
    
    public static String toStringMode(final int n) {
        final StringBuilder sb = new StringBuilder();
        if ((n & 0x1) != 0x0) {
            sb.append('r');
        }
        if ((n & 0x2) != 0x0) {
            sb.append('w');
        }
        return sb.toString();
    }
}

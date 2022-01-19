// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network;

import java.util.zip.CRC32;
import java.io.FileInputStream;
import java.io.File;

public class MD5Checksum
{
    public static long createChecksum(final String s) throws Exception {
        if (!new File(s).exists()) {
            return 0L;
        }
        final FileInputStream fileInputStream = new FileInputStream(s);
        final CRC32 crc32 = new CRC32();
        final byte[] array = new byte[1024];
        int read;
        while ((read = fileInputStream.read(array)) != -1) {
            crc32.update(array, 0, read);
        }
        final long value = crc32.getValue();
        fileInputStream.close();
        return value;
    }
    
    public static void main(final String[] array) {
    }
}

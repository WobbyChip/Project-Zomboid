// 
// Decompiled by Procyon v0.5.36
// 

package zombie.asset;

import java.util.zip.CRC32;

public final class AssetType
{
    public static final AssetType INVALID_ASSET_TYPE;
    public long type;
    
    public AssetType(final String s) {
        final CRC32 crc32 = new CRC32();
        crc32.update(s.getBytes());
        this.type = crc32.getValue();
    }
    
    static {
        INVALID_ASSET_TYPE = new AssetType("");
    }
}

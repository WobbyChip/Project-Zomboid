// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network;

import zombie.iso.IsoCell;
import zombie.iso.IsoObject;
import java.nio.ByteBuffer;

public class WorldItemTypes
{
    public static IsoObject createFromBuffer(final ByteBuffer byteBuffer) {
        return IsoObject.factoryFromFileInput(null, byteBuffer);
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.areas.isoregion;

import java.nio.ByteBuffer;
import zombie.iso.IsoChunk;

public class ChunkUpdate
{
    public static void writeIsoChunkIntoBuffer(final IsoChunk isoChunk, final ByteBuffer byteBuffer) {
        if (isoChunk != null) {
            final int position = byteBuffer.position();
            byteBuffer.putInt(0);
            byteBuffer.putInt(isoChunk.maxLevel);
            byteBuffer.putInt((isoChunk.maxLevel + 1) * 100);
            for (int i = 0; i <= isoChunk.maxLevel; ++i) {
                for (int j = 0; j < isoChunk.squares[0].length; ++j) {
                    byteBuffer.put(IsoRegions.calculateSquareFlags(isoChunk.squares[i][j]));
                }
            }
            final int position2 = byteBuffer.position();
            byteBuffer.position(position);
            byteBuffer.putInt(position2 - position);
            byteBuffer.position(position2);
        }
        else {
            byteBuffer.putInt(-1);
        }
    }
}

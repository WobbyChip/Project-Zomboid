// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.textures;

import java.io.EOFException;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.zip.CRC32;
import java.io.InputStream;

public final class PNGSize
{
    private static final byte[] SIGNATURE;
    private static final int IHDR = 1229472850;
    public int width;
    public int height;
    private int bitdepth;
    private int colorType;
    private int bytesPerPixel;
    private InputStream input;
    private final CRC32 crc;
    private final byte[] buffer;
    private int chunkLength;
    private int chunkType;
    private int chunkRemaining;
    
    public PNGSize() {
        this.crc = new CRC32();
        this.buffer = new byte[4096];
    }
    
    public void readSize(final String name) {
        try {
            final FileInputStream in = new FileInputStream(name);
            try {
                final BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
                try {
                    this.readSize(bufferedInputStream);
                    bufferedInputStream.close();
                }
                catch (Throwable t) {
                    try {
                        bufferedInputStream.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                    throw t;
                }
                in.close();
            }
            catch (Throwable t2) {
                try {
                    in.close();
                }
                catch (Throwable exception2) {
                    t2.addSuppressed(exception2);
                }
                throw t2;
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void readSize(final InputStream input) throws IOException {
        this.input = input;
        this.readFully(this.buffer, 0, PNGSize.SIGNATURE.length);
        if (!this.checkSignature(this.buffer)) {
            throw new IOException("Not a valid PNG file");
        }
        this.openChunk(1229472850);
        this.readIHDR();
        this.closeChunk();
    }
    
    private void readIHDR() throws IOException {
        this.checkChunkLength(13);
        this.readChunk(this.buffer, 0, 13);
        this.width = this.readInt(this.buffer, 0);
        this.height = this.readInt(this.buffer, 4);
        this.bitdepth = (this.buffer[8] & 0xFF);
        this.colorType = (this.buffer[9] & 0xFF);
    }
    
    private void openChunk() throws IOException {
        this.readFully(this.buffer, 0, 8);
        this.chunkLength = this.readInt(this.buffer, 0);
        this.chunkType = this.readInt(this.buffer, 4);
        this.chunkRemaining = this.chunkLength;
        this.crc.reset();
        this.crc.update(this.buffer, 4, 4);
    }
    
    private void openChunk(final int i) throws IOException {
        this.openChunk();
        if (this.chunkType != i) {
            throw new IOException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Integer.toHexString(i)));
        }
    }
    
    private void closeChunk() throws IOException {
        if (this.chunkRemaining > 0) {
            this.skip(this.chunkRemaining + 4);
        }
        else {
            this.readFully(this.buffer, 0, 4);
            if ((int)this.crc.getValue() != this.readInt(this.buffer, 0)) {
                throw new IOException("Invalid CRC");
            }
        }
        this.chunkRemaining = 0;
        this.chunkLength = 0;
        this.chunkType = 0;
    }
    
    private void checkChunkLength(final int n) throws IOException {
        if (this.chunkLength != n) {
            throw new IOException("Chunk has wrong size");
        }
    }
    
    private int readChunk(final byte[] b, final int off, int chunkRemaining) throws IOException {
        if (chunkRemaining > this.chunkRemaining) {
            chunkRemaining = this.chunkRemaining;
        }
        this.readFully(b, off, chunkRemaining);
        this.crc.update(b, off, chunkRemaining);
        this.chunkRemaining -= chunkRemaining;
        return chunkRemaining;
    }
    
    private void readFully(final byte[] b, int off, int i) throws IOException {
        do {
            final int read = this.input.read(b, off, i);
            if (read < 0) {
                throw new EOFException();
            }
            off += read;
            i -= read;
        } while (i > 0);
    }
    
    private int readInt(final byte[] array, final int n) {
        return array[n] << 24 | (array[n + 1] & 0xFF) << 16 | (array[n + 2] & 0xFF) << 8 | (array[n + 3] & 0xFF);
    }
    
    private void skip(long n) throws IOException {
        while (n > 0L) {
            final long skip = this.input.skip(n);
            if (skip < 0L) {
                throw new EOFException();
            }
            n -= skip;
        }
    }
    
    private boolean checkSignature(final byte[] array) {
        for (int i = 0; i < PNGSize.SIGNATURE.length; ++i) {
            if (array[i] != PNGSize.SIGNATURE[i]) {
                return false;
            }
        }
        return true;
    }
    
    static {
        SIGNATURE = new byte[] { -119, 80, 78, 71, 13, 10, 26, 10 };
    }
}

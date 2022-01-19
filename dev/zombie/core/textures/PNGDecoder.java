// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.textures;

import java.util.zip.DataFormatException;
import java.io.EOFException;
import java.util.Arrays;
import java.util.zip.Inflater;
import java.nio.ByteBuffer;
import java.io.IOException;
import zombie.core.utils.BooleanGrid;
import java.util.zip.CRC32;
import java.io.InputStream;

public final class PNGDecoder
{
    private static final byte[] SIGNATURE;
    private static final int IHDR = 1229472850;
    private static final int PLTE = 1347179589;
    private static final int tRNS = 1951551059;
    private static final int IDAT = 1229209940;
    private static final int IEND = 1229278788;
    private static final byte COLOR_GREYSCALE = 0;
    private static final byte COLOR_TRUECOLOR = 2;
    private static final byte COLOR_INDEXED = 3;
    private static final byte COLOR_GREYALPHA = 4;
    private static final byte COLOR_TRUEALPHA = 6;
    private final InputStream input;
    private final CRC32 crc;
    private final byte[] buffer;
    private int chunkLength;
    private int chunkType;
    private int chunkRemaining;
    private int width;
    private int height;
    private int bitdepth;
    private int colorType;
    private int bytesPerPixel;
    private byte[] palette;
    private byte[] paletteA;
    private byte[] transPixel;
    int maskM;
    public int maskID;
    public BooleanGrid mask;
    public boolean bDoMask;
    public long readTotal;
    
    public PNGDecoder(final InputStream input, final boolean bDoMask) throws IOException {
        this.maskM = 0;
        this.maskID = 0;
        this.bDoMask = false;
        this.readTotal = 0L;
        this.input = input;
        this.crc = new CRC32();
        this.buffer = new byte[4096];
        this.bDoMask = bDoMask;
        this.readFully(this.buffer, 0, PNGDecoder.SIGNATURE.length);
        if (!checkSignature(this.buffer)) {
            throw new IOException("Not a valid PNG file");
        }
        this.openChunk(1229472850);
        this.readIHDR();
        this.closeChunk();
    Label_0144:
        while (true) {
            this.openChunk();
            switch (this.chunkType) {
                case 1229209940: {
                    break Label_0144;
                }
                case 1347179589: {
                    this.readPLTE();
                    break;
                }
                case 1951551059: {
                    this.readtRNS();
                    break;
                }
            }
            this.closeChunk();
        }
        if (this.colorType == 3 && this.palette == null) {
            throw new IOException("Missing PLTE chunk");
        }
        if (bDoMask) {
            this.mask = new BooleanGrid(this.width, this.height);
        }
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public boolean hasAlphaChannel() {
        return this.colorType == 6 || this.colorType == 4;
    }
    
    public boolean hasAlpha() {
        return this.hasAlphaChannel() || this.paletteA != null || this.transPixel != null;
    }
    
    public boolean isRGB() {
        return this.colorType == 6 || this.colorType == 2 || this.colorType == 3;
    }
    
    public void overwriteTRNS(final byte b, final byte b2, final byte b3) {
        if (this.hasAlphaChannel()) {
            throw new UnsupportedOperationException("image has an alpha channel");
        }
        final byte[] palette = this.palette;
        if (palette == null) {
            this.transPixel = new byte[] { 0, b, 0, b2, 0, b3 };
        }
        else {
            this.paletteA = new byte[palette.length / 3];
            for (int i = 0, n = 0; i < palette.length; i += 3, ++n) {
                if (palette[i] != b || palette[i + 1] != b2 || palette[i + 2] != b3) {
                    this.paletteA[n] = -1;
                }
            }
        }
    }
    
    public Format decideTextureFormat(final Format format) {
        switch (this.colorType) {
            case 2: {
                switch (format) {
                    case ABGR:
                    case RGBA:
                    case BGRA:
                    case RGB: {
                        return format;
                    }
                    default: {
                        return Format.RGB;
                    }
                }
                break;
            }
            case 6: {
                switch (format) {
                    case ABGR:
                    case RGBA:
                    case BGRA:
                    case RGB: {
                        return format;
                    }
                    default: {
                        return Format.RGBA;
                    }
                }
                break;
            }
            case 0: {
                switch (format) {
                    case LUMINANCE:
                    case ALPHA: {
                        return format;
                    }
                    default: {
                        return Format.LUMINANCE;
                    }
                }
                break;
            }
            case 4: {
                return Format.LUMINANCE_ALPHA;
            }
            case 3: {
                switch (format) {
                    case ABGR:
                    case RGBA:
                    case BGRA: {
                        return format;
                    }
                    default: {
                        return Format.RGBA;
                    }
                }
                break;
            }
            default: {
                throw new UnsupportedOperationException("Not yet implemented");
            }
        }
    }
    
    public void decode(final ByteBuffer byteBuffer, final int n, final Format format) throws IOException {
        final int position = byteBuffer.position();
        final int n2 = (this.width * this.bitdepth + 7) / 8 * this.bytesPerPixel;
        byte[] array = new byte[n2 + 1];
        byte[] array2 = new byte[n2 + 1];
        byte[] array3 = (byte[])((this.bitdepth < 8) ? new byte[this.width + 1] : null);
        this.maskM = 0;
        final Inflater inflater = new Inflater();
        try {
            for (int i = 0; i < this.height; ++i) {
                this.readChunkUnzip(inflater, array, 0, array.length);
                this.unfilter(array, array2);
                byteBuffer.position(position + i * n);
                Label_0670: {
                    switch (this.colorType) {
                        case 2: {
                            switch (format) {
                                case ABGR: {
                                    this.copyRGBtoABGR(byteBuffer, array);
                                    break Label_0670;
                                }
                                case RGBA: {
                                    this.copyRGBtoRGBA(byteBuffer, array);
                                    break Label_0670;
                                }
                                case BGRA: {
                                    this.copyRGBtoBGRA(byteBuffer, array);
                                    break Label_0670;
                                }
                                case RGB: {
                                    this.copy(byteBuffer, array);
                                    break Label_0670;
                                }
                                default: {
                                    throw new UnsupportedOperationException("Unsupported format for this image");
                                }
                            }
                            break;
                        }
                        case 6: {
                            switch (format) {
                                case ABGR: {
                                    this.copyRGBAtoABGR(byteBuffer, array);
                                    break Label_0670;
                                }
                                case RGBA: {
                                    this.copy(byteBuffer, array);
                                    break Label_0670;
                                }
                                case BGRA: {
                                    this.copyRGBAtoBGRA(byteBuffer, array);
                                    break Label_0670;
                                }
                                case RGB: {
                                    this.copyRGBAtoRGB(byteBuffer, array);
                                    break Label_0670;
                                }
                                default: {
                                    throw new UnsupportedOperationException("Unsupported format for this image");
                                }
                            }
                            break;
                        }
                        case 0: {
                            switch (format) {
                                case LUMINANCE:
                                case ALPHA: {
                                    this.copy(byteBuffer, array);
                                    break Label_0670;
                                }
                                case RGBA: {
                                    this.copyGREYtoRGBA(byteBuffer, array);
                                    break Label_0670;
                                }
                                default: {
                                    throw new UnsupportedOperationException("Unsupported format for this image");
                                }
                            }
                            break;
                        }
                        case 4: {
                            switch (format) {
                                case LUMINANCE_ALPHA: {
                                    this.copy(byteBuffer, array);
                                    break Label_0670;
                                }
                                case RGBA: {
                                    this.copyGREYALPHAtoRGBA(byteBuffer, array);
                                    break Label_0670;
                                }
                                default: {
                                    throw new UnsupportedOperationException("Unsupported format for this image");
                                }
                            }
                            break;
                        }
                        case 3: {
                            switch (this.bitdepth) {
                                case 8: {
                                    array3 = array;
                                    break;
                                }
                                case 4: {
                                    this.expand4(array, array3);
                                    break;
                                }
                                case 2: {
                                    this.expand2(array, array3);
                                    break;
                                }
                                case 1: {
                                    this.expand1(array, array3);
                                    break;
                                }
                                default: {
                                    throw new UnsupportedOperationException("Unsupported bitdepth for this image");
                                }
                            }
                            switch (format) {
                                case ABGR: {
                                    this.copyPALtoABGR(byteBuffer, array3);
                                    break Label_0670;
                                }
                                case RGBA: {
                                    this.copyPALtoRGBA(byteBuffer, array3);
                                    break Label_0670;
                                }
                                case BGRA: {
                                    this.copyPALtoBGRA(byteBuffer, array3);
                                    break Label_0670;
                                }
                                default: {
                                    throw new UnsupportedOperationException("Unsupported format for this image");
                                }
                            }
                            break;
                        }
                        default: {
                            throw new UnsupportedOperationException("Not yet implemented");
                        }
                    }
                }
                final byte[] array4 = array;
                array = array2;
                array2 = array4;
            }
        }
        finally {
            inflater.end();
        }
    }
    
    public void decodeFlipped(final ByteBuffer byteBuffer, final int n, final Format format) throws IOException {
        if (n <= 0) {
            throw new IllegalArgumentException("stride");
        }
        final int position = byteBuffer.position();
        final int n2 = (this.height - 1) * n;
        byteBuffer.position(position + n2);
        this.decode(byteBuffer, -n, format);
        byteBuffer.position(byteBuffer.position() + n2);
    }
    
    private void copy(final ByteBuffer byteBuffer, final byte[] src) {
        if (this.bDoMask) {
            for (int i = 1; i < src.length; i += 4) {
                if (src[i + 3] % 255 != 0) {
                    this.mask.setValue(this.maskM % this.width, this.maskM / this.width, true);
                }
                ++this.maskM;
            }
        }
        byteBuffer.put(src, 1, src.length - 1);
    }
    
    private void copyRGBtoABGR(final ByteBuffer byteBuffer, final byte[] array) {
        if (this.transPixel != null) {
            final byte b = this.transPixel[1];
            final byte b2 = this.transPixel[3];
            final byte b3 = this.transPixel[5];
            for (int i = 1; i < array.length; i += 3) {
                final byte b4 = array[i];
                final byte b5 = array[i + 1];
                final byte b6 = array[i + 2];
                byte b7 = -1;
                if (b4 == b && b5 == b2 && b6 == b3) {
                    b7 = 0;
                }
                byteBuffer.put(b7).put(b6).put(b5).put(b4);
            }
        }
        else {
            for (int j = 1; j < array.length; j += 3) {
                byteBuffer.put((byte)(-1)).put(array[j + 2]).put(array[j + 1]).put(array[j]);
            }
        }
    }
    
    private void copyRGBtoRGBA(final ByteBuffer byteBuffer, final byte[] array) {
        if (this.transPixel != null) {
            final byte b = this.transPixel[1];
            final byte b2 = this.transPixel[3];
            final byte b3 = this.transPixel[5];
            for (int i = 1; i < array.length; i += 3) {
                final byte b4 = array[i];
                final byte b5 = array[i + 1];
                final byte b6 = array[i + 2];
                byte b7 = -1;
                if (b4 == b && b5 == b2 && b6 == b3) {
                    b7 = 0;
                }
                if (this.bDoMask && b7 == 0) {
                    this.mask.setValue(this.maskID % this.width, this.maskID / this.width, true);
                    ++this.maskID;
                }
                byteBuffer.put(b4).put(b5).put(b6).put(b7);
            }
        }
        else {
            for (int j = 1; j < array.length; j += 3) {
                byteBuffer.put(array[j]).put(array[j + 1]).put(array[j + 2]).put((byte)(-1));
            }
        }
    }
    
    private void copyRGBtoBGRA(final ByteBuffer byteBuffer, final byte[] array) {
        if (this.transPixel != null) {
            final byte b = this.transPixel[1];
            final byte b2 = this.transPixel[3];
            final byte b3 = this.transPixel[5];
            for (int i = 1; i < array.length; i += 3) {
                final byte b4 = array[i];
                final byte b5 = array[i + 1];
                final byte b6 = array[i + 2];
                byte b7 = -1;
                if (b4 == b && b5 == b2 && b6 == b3) {
                    b7 = 0;
                }
                byteBuffer.put(b6).put(b5).put(b4).put(b7);
            }
        }
        else {
            for (int j = 1; j < array.length; j += 3) {
                byteBuffer.put(array[j + 2]).put(array[j + 1]).put(array[j]).put((byte)(-1));
            }
        }
    }
    
    private void copyRGBAtoABGR(final ByteBuffer byteBuffer, final byte[] array) {
        for (int i = 1; i < array.length; i += 4) {
            byteBuffer.put(array[i + 3]).put(array[i + 2]).put(array[i + 1]).put(array[i]);
        }
    }
    
    private void copyRGBAtoBGRA(final ByteBuffer byteBuffer, final byte[] array) {
        for (int i = 1; i < array.length; i += 4) {
            byteBuffer.put(array[i + 2]).put(array[i + 1]).put(array[i]).put(array[i + 3]);
        }
    }
    
    private void copyRGBAtoRGB(final ByteBuffer byteBuffer, final byte[] array) {
        for (int i = 1; i < array.length; i += 4) {
            byteBuffer.put(array[i]).put(array[i + 1]).put(array[i + 2]);
        }
    }
    
    private void copyPALtoABGR(final ByteBuffer byteBuffer, final byte[] array) {
        if (this.paletteA != null) {
            for (int i = 1; i < array.length; ++i) {
                final int n = array[i] & 0xFF;
                byteBuffer.put(this.paletteA[n]).put(this.palette[n * 3 + 2]).put(this.palette[n * 3 + 1]).put(this.palette[n * 3 + 0]);
            }
        }
        else {
            for (int j = 1; j < array.length; ++j) {
                final int n2 = array[j] & 0xFF;
                byteBuffer.put((byte)(-1)).put(this.palette[n2 * 3 + 2]).put(this.palette[n2 * 3 + 1]).put(this.palette[n2 * 3 + 0]);
            }
        }
    }
    
    private void copyPALtoRGBA(final ByteBuffer byteBuffer, final byte[] array) {
        if (this.paletteA != null) {
            for (int i = 1; i < array.length; ++i) {
                final int n = array[i] & 0xFF;
                byteBuffer.put(this.palette[n * 3 + 0]).put(this.palette[n * 3 + 1]).put(this.palette[n * 3 + 2]).put(this.paletteA[n]);
            }
        }
        else {
            for (int j = 1; j < array.length; ++j) {
                final int n2 = array[j] & 0xFF;
                byteBuffer.put(this.palette[n2 * 3 + 0]).put(this.palette[n2 * 3 + 1]).put(this.palette[n2 * 3 + 2]).put((byte)(-1));
            }
        }
    }
    
    private void copyPALtoBGRA(final ByteBuffer byteBuffer, final byte[] array) {
        if (this.paletteA != null) {
            for (int i = 1; i < array.length; ++i) {
                final int n = array[i] & 0xFF;
                byteBuffer.put(this.palette[n * 3 + 2]).put(this.palette[n * 3 + 1]).put(this.palette[n * 3 + 0]).put(this.paletteA[n]);
            }
        }
        else {
            for (int j = 1; j < array.length; ++j) {
                final int n2 = array[j] & 0xFF;
                byteBuffer.put(this.palette[n2 * 3 + 2]).put(this.palette[n2 * 3 + 1]).put(this.palette[n2 * 3 + 0]).put((byte)(-1));
            }
        }
    }
    
    private void copyGREYtoRGBA(final ByteBuffer byteBuffer, final byte[] array) {
        for (int i = 1; i < array.length; ++i) {
            final byte b = array[i];
            byteBuffer.put(b).put(b).put(b).put((byte)(-1));
        }
    }
    
    private void copyGREYALPHAtoRGBA(final ByteBuffer byteBuffer, final byte[] array) {
        for (int i = 1; i < array.length; i += 2) {
            final byte b = array[i];
            byteBuffer.put(b).put(b).put(b).put(array[i + 1]);
        }
    }
    
    private void expand4(final byte[] array, final byte[] array2) {
        int i = 1;
        final int length = array2.length;
        while (i < length) {
            final int n = array[1 + (i >> 1)] & 0xFF;
            switch (length - i) {
                default: {
                    array2[i + 1] = (byte)(n & 0xF);
                }
                case 1: {
                    array2[i] = (byte)(n >> 4);
                    i += 2;
                    continue;
                }
            }
        }
    }
    
    private void expand2(final byte[] array, final byte[] array2) {
        int i = 1;
        final int length = array2.length;
        while (i < length) {
            final int n = array[1 + (i >> 2)] & 0xFF;
            switch (length - i) {
                default: {
                    array2[i + 3] = (byte)(n & 0x3);
                }
                case 3: {
                    array2[i + 2] = (byte)(n >> 2 & 0x3);
                }
                case 2: {
                    array2[i + 1] = (byte)(n >> 4 & 0x3);
                }
                case 1: {
                    array2[i] = (byte)(n >> 6);
                    i += 4;
                    continue;
                }
            }
        }
    }
    
    private void expand1(final byte[] array, final byte[] array2) {
        int i = 1;
        final int length = array2.length;
        while (i < length) {
            final int n = array[1 + (i >> 3)] & 0xFF;
            switch (length - i) {
                default: {
                    array2[i + 7] = (byte)(n & 0x1);
                }
                case 7: {
                    array2[i + 6] = (byte)(n >> 1 & 0x1);
                }
                case 6: {
                    array2[i + 5] = (byte)(n >> 2 & 0x1);
                }
                case 5: {
                    array2[i + 4] = (byte)(n >> 3 & 0x1);
                }
                case 4: {
                    array2[i + 3] = (byte)(n >> 4 & 0x1);
                }
                case 3: {
                    array2[i + 2] = (byte)(n >> 5 & 0x1);
                }
                case 2: {
                    array2[i + 1] = (byte)(n >> 6 & 0x1);
                }
                case 1: {
                    array2[i] = (byte)(n >> 7);
                    i += 8;
                    continue;
                }
            }
        }
    }
    
    private void unfilter(final byte[] array, final byte[] array2) throws IOException {
        switch (array[0]) {
            case 0: {
                break;
            }
            case 1: {
                this.unfilterSub(array);
                break;
            }
            case 2: {
                this.unfilterUp(array, array2);
                break;
            }
            case 3: {
                this.unfilterAverage(array, array2);
                break;
            }
            case 4: {
                this.unfilterPaeth(array, array2);
                break;
            }
            default: {
                throw new IOException(invokedynamic(makeConcatWithConstants:(B)Ljava/lang/String;, array[0]));
            }
        }
    }
    
    private void unfilterSub(final byte[] array) {
        final int bytesPerPixel = this.bytesPerPixel;
        for (int i = bytesPerPixel + 1; i < array.length; ++i) {
            final int n = i;
            array[n] += array[i - bytesPerPixel];
        }
    }
    
    private void unfilterUp(final byte[] array, final byte[] array2) {
        final int bytesPerPixel = this.bytesPerPixel;
        for (int i = 1; i < array.length; ++i) {
            final int n = i;
            array[n] += array2[i];
        }
    }
    
    private void unfilterAverage(final byte[] array, final byte[] array2) {
        int bytesPerPixel;
        int i;
        for (bytesPerPixel = this.bytesPerPixel, i = 1; i <= bytesPerPixel; ++i) {
            final int n = i;
            array[n] += (byte)((array2[i] & 0xFF) >>> 1);
        }
        while (i < array.length) {
            final int n2 = i;
            array[n2] += (byte)((array2[i] & 0xFF) + (array[i - bytesPerPixel] & 0xFF) >>> 1);
            ++i;
        }
    }
    
    private void unfilterPaeth(final byte[] array, final byte[] array2) {
        int bytesPerPixel;
        int i;
        for (bytesPerPixel = this.bytesPerPixel, i = 1; i <= bytesPerPixel; ++i) {
            final int n = i;
            array[n] += array2[i];
        }
        while (i < array.length) {
            final int n2 = array[i - bytesPerPixel] & 0xFF;
            final int n3 = array2[i] & 0xFF;
            int n4 = array2[i - bytesPerPixel] & 0xFF;
            final int n5 = n2 + n3 - n4;
            int n6 = n5 - n2;
            if (n6 < 0) {
                n6 = -n6;
            }
            int n7 = n5 - n3;
            if (n7 < 0) {
                n7 = -n7;
            }
            int n8 = n5 - n4;
            if (n8 < 0) {
                n8 = -n8;
            }
            if (n6 <= n7 && n6 <= n8) {
                n4 = n2;
            }
            else if (n7 <= n8) {
                n4 = n3;
            }
            final int n9 = i;
            array[n9] += (byte)n4;
            ++i;
        }
    }
    
    private void readIHDR() throws IOException {
        this.checkChunkLength(13);
        this.readChunk(this.buffer, 0, 13);
        this.width = this.readInt(this.buffer, 0);
        this.height = this.readInt(this.buffer, 4);
        this.bitdepth = (this.buffer[8] & 0xFF);
        Label_0350: {
            switch (this.colorType = (this.buffer[9] & 0xFF)) {
                case 0: {
                    if (this.bitdepth != 8) {
                        throw new IOException(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.bitdepth));
                    }
                    this.bytesPerPixel = 1;
                    break;
                }
                case 4: {
                    if (this.bitdepth != 8) {
                        throw new IOException(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.bitdepth));
                    }
                    this.bytesPerPixel = 2;
                    break;
                }
                case 2: {
                    if (this.bitdepth != 8) {
                        throw new IOException(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.bitdepth));
                    }
                    this.bytesPerPixel = 3;
                    break;
                }
                case 6: {
                    if (this.bitdepth != 8) {
                        throw new IOException(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.bitdepth));
                    }
                    this.bytesPerPixel = 4;
                    break;
                }
                case 3: {
                    switch (this.bitdepth) {
                        case 1:
                        case 2:
                        case 4:
                        case 8: {
                            this.bytesPerPixel = 1;
                            break Label_0350;
                        }
                        default: {
                            throw new IOException(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.bitdepth));
                        }
                    }
                    break;
                }
                default: {
                    throw new IOException(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.colorType));
                }
            }
        }
        if (this.buffer[10] != 0) {
            throw new IOException("unsupported compression method");
        }
        if (this.buffer[11] != 0) {
            throw new IOException("unsupported filtering method");
        }
        if (this.buffer[12] != 0) {
            throw new IOException("unsupported interlace method");
        }
    }
    
    private void readPLTE() throws IOException {
        final int n = this.chunkLength / 3;
        if (n < 1 || n > 256 || this.chunkLength % 3 != 0) {
            throw new IOException("PLTE chunk has wrong length");
        }
        this.readChunk(this.palette = new byte[n * 3], 0, this.palette.length);
    }
    
    private void readtRNS() throws IOException {
        switch (this.colorType) {
            case 0: {
                this.checkChunkLength(2);
                this.readChunk(this.transPixel = new byte[2], 0, 2);
                break;
            }
            case 2: {
                this.checkChunkLength(6);
                this.readChunk(this.transPixel = new byte[6], 0, 6);
                break;
            }
            case 3: {
                if (this.palette == null) {
                    throw new IOException("tRNS chunk without PLTE chunk");
                }
                Arrays.fill(this.paletteA = new byte[this.palette.length / 3], (byte)(-1));
                this.readChunk(this.paletteA, 0, this.paletteA.length);
                break;
            }
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
    
    private void refillInflater(final Inflater inflater) throws IOException {
        while (this.chunkRemaining == 0) {
            this.closeChunk();
            this.openChunk(1229209940);
        }
        inflater.setInput(this.buffer, 0, this.readChunk(this.buffer, 0, this.buffer.length));
    }
    
    private void readChunkUnzip(final Inflater inflater, final byte[] output, int off, int i) throws IOException {
        assert output != this.buffer;
        Label_0022: {
            break Label_0022;
            try {
                do {
                    final int inflate = inflater.inflate(output, off, i);
                    if (inflate <= 0) {
                        if (inflater.finished()) {
                            throw new EOFException();
                        }
                        if (!inflater.needsInput()) {
                            throw new IOException(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, i));
                        }
                        this.refillInflater(inflater);
                    }
                    else {
                        off += inflate;
                        i -= inflate;
                    }
                } while (i > 0);
            }
            catch (DataFormatException cause) {
                throw (IOException)new IOException("inflate error").initCause(cause);
            }
        }
    }
    
    private void readFully(final byte[] b, int off, int i) throws IOException {
        do {
            final int read = this.input.read(b, off, i);
            if (read < 0) {
                throw new EOFException();
            }
            off += read;
            i -= read;
            this.readTotal += read;
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
    
    private static boolean checkSignature(final byte[] array) {
        for (int i = 0; i < PNGDecoder.SIGNATURE.length; ++i) {
            if (array[i] != PNGDecoder.SIGNATURE[i]) {
                return false;
            }
        }
        return true;
    }
    
    static {
        SIGNATURE = new byte[] { -119, 80, 78, 71, 13, 10, 26, 10 };
    }
    
    public enum Format
    {
        ALPHA(1, true), 
        LUMINANCE(1, false), 
        LUMINANCE_ALPHA(2, true), 
        RGB(3, false), 
        RGBA(4, true), 
        BGRA(4, true), 
        ABGR(4, true);
        
        final int numComponents;
        final boolean hasAlpha;
        
        private Format(final int numComponents, final boolean hasAlpha) {
            this.numComponents = numComponents;
            this.hasAlpha = hasAlpha;
        }
        
        public int getNumComponents() {
            return this.numComponents;
        }
        
        public boolean isHasAlpha() {
            return this.hasAlpha;
        }
        
        private static /* synthetic */ Format[] $values() {
            return new Format[] { Format.ALPHA, Format.LUMINANCE, Format.LUMINANCE_ALPHA, Format.RGB, Format.RGBA, Format.BGRA, Format.ABGR };
        }
        
        static {
            $VALUES = $values();
        }
    }
}

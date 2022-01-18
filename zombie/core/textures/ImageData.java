// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.textures;

import java.util.function.Supplier;
import zombie.debug.DebugOptions;
import zombie.util.list.PZArrayUtil;
import zombie.core.math.PZMath;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.awt.image.Raster;
import java.awt.image.BufferedImage;
import zombie.core.znet.SteamFriends;
import zombie.core.utils.DirectBufferAllocator;
import zombie.core.opengl.RenderThread;
import java.nio.ByteBuffer;
import zombie.core.utils.ImageUtils;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import zombie.ZomboidFileSystem;
import zombie.core.utils.WrappedBuffer;
import com.evildevil.engines.bubble.texture.DDSLoader;
import zombie.core.utils.BooleanGrid;
import java.io.Serializable;

public final class ImageData implements Serializable
{
    private static final long serialVersionUID = -7893392091273534932L;
    public MipMapLevel data;
    private MipMapLevel[] mipMaps;
    private int height;
    private int heightHW;
    private boolean solid;
    private int width;
    private int widthHW;
    private int mipMapCount;
    private boolean alphaPaddingDone;
    public BooleanGrid mask;
    private static final int BufferSize = 67108864;
    static final DDSLoader dds;
    public int id;
    public static final int MIP_LEVEL_IDX_OFFSET = 0;
    private static final ThreadLocal<L_generateMipMaps> TL_generateMipMaps;
    private static final ThreadLocal<L_performAlphaPadding> TL_performAlphaPadding;
    
    public ImageData(final TextureID textureID, final WrappedBuffer wrappedBuffer) {
        this.solid = true;
        this.mipMapCount = -1;
        this.alphaPaddingDone = false;
        this.id = -1;
        this.data = new MipMapLevel(textureID.widthHW, textureID.heightHW, wrappedBuffer);
        this.width = textureID.width;
        this.widthHW = textureID.widthHW;
        this.height = textureID.height;
        this.heightHW = textureID.heightHW;
        this.solid = textureID.solid;
    }
    
    public ImageData(String name) throws Exception {
        this.solid = true;
        this.mipMapCount = -1;
        this.alphaPaddingDone = false;
        this.id = -1;
        if (name.contains(".txt")) {
            name = name.replace(".txt", ".png");
        }
        name = Texture.processFilePath(name);
        name = ZomboidFileSystem.instance.getString(name);
        try {
            final FileInputStream in = new FileInputStream(name);
            try {
                final BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
                try {
                    final PNGDecoder pngDecoder = new PNGDecoder(bufferedInputStream, false);
                    this.width = pngDecoder.getWidth();
                    this.height = pngDecoder.getHeight();
                    this.widthHW = ImageUtils.getNextPowerOfTwoHW(this.width);
                    this.heightHW = ImageUtils.getNextPowerOfTwoHW(this.height);
                    this.data = new MipMapLevel(this.widthHW, this.heightHW);
                    final ByteBuffer buffer = this.data.getBuffer();
                    buffer.rewind();
                    final int n = this.widthHW * 4;
                    if (this.width != this.widthHW) {
                        for (int i = this.width * 4; i < this.widthHW * 4; ++i) {
                            for (int j = 0; j < this.heightHW; ++j) {
                                buffer.put(i + j * n, (byte)0);
                            }
                        }
                    }
                    if (this.height != this.heightHW) {
                        for (int k = this.height; k < this.heightHW; ++k) {
                            for (int l = 0; l < this.width * 4; ++l) {
                                buffer.put(l + k * n, (byte)0);
                            }
                        }
                    }
                    pngDecoder.decode(this.data.getBuffer(), n, PNGDecoder.Format.RGBA);
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
            this.dispose();
            final int n2 = -1;
            this.height = n2;
            this.width = n2;
        }
    }
    
    public ImageData(final int width, final int height) {
        this.solid = true;
        this.mipMapCount = -1;
        this.alphaPaddingDone = false;
        this.id = -1;
        this.width = width;
        this.height = height;
        this.widthHW = ImageUtils.getNextPowerOfTwoHW(width);
        this.heightHW = ImageUtils.getNextPowerOfTwoHW(height);
        this.data = new MipMapLevel(this.widthHW, this.heightHW);
    }
    
    public ImageData(final int width, final int height, final WrappedBuffer wrappedBuffer) {
        this.solid = true;
        this.mipMapCount = -1;
        this.alphaPaddingDone = false;
        this.id = -1;
        this.width = width;
        this.height = height;
        this.widthHW = ImageUtils.getNextPowerOfTwoHW(width);
        this.heightHW = ImageUtils.getNextPowerOfTwoHW(height);
        this.data = new MipMapLevel(this.widthHW, this.heightHW, wrappedBuffer);
    }
    
    ImageData(final String s, final String s2) {
        this.solid = true;
        this.mipMapCount = -1;
        this.alphaPaddingDone = false;
        this.id = -1;
        final Pcx data = new Pcx(s, s2);
        this.width = data.imageWidth;
        this.height = data.imageHeight;
        this.widthHW = ImageUtils.getNextPowerOfTwoHW(this.width);
        this.heightHW = ImageUtils.getNextPowerOfTwoHW(this.height);
        this.data = new MipMapLevel(this.widthHW, this.heightHW);
        this.setData(data);
        this.makeTransp((byte)data.palette[762], (byte)data.palette[763], (byte)data.palette[764], (byte)0);
    }
    
    ImageData(final String s, final int[] array) {
        this.solid = true;
        this.mipMapCount = -1;
        this.alphaPaddingDone = false;
        this.id = -1;
        final Pcx data = new Pcx(s, array);
        this.width = data.imageWidth;
        this.height = data.imageHeight;
        this.widthHW = ImageUtils.getNextPowerOfTwoHW(this.width);
        this.heightHW = ImageUtils.getNextPowerOfTwoHW(this.height);
        this.data = new MipMapLevel(this.widthHW, this.heightHW);
        this.setData(data);
        this.makeTransp((byte)data.palette[762], (byte)data.palette[763], (byte)data.palette[764], (byte)0);
    }
    
    public ImageData(final BufferedInputStream bufferedInputStream, final boolean b, final Texture.PZFileformat pzFileformat) {
        this.solid = true;
        this.mipMapCount = -1;
        this.alphaPaddingDone = false;
        this.id = -1;
        if (pzFileformat == Texture.PZFileformat.DDS) {
            RenderThread.invokeOnRenderContext(() -> this.id = ImageData.dds.loadDDSFile(bufferedInputStream));
            this.width = DDSLoader.lastWid;
            this.height = DDSLoader.lastHei;
            this.widthHW = ImageUtils.getNextPowerOfTwoHW(this.width);
            this.heightHW = ImageUtils.getNextPowerOfTwoHW(this.height);
        }
    }
    
    public ImageData(final InputStream inputStream, final boolean b) throws Exception {
        this.solid = true;
        this.mipMapCount = -1;
        this.alphaPaddingDone = false;
        this.id = -1;
        final PNGDecoder pngDecoder = new PNGDecoder(inputStream, b);
        this.width = pngDecoder.getWidth();
        this.height = pngDecoder.getHeight();
        this.widthHW = ImageUtils.getNextPowerOfTwoHW(this.width);
        this.heightHW = ImageUtils.getNextPowerOfTwoHW(this.height);
        (this.data = new MipMapLevel(this.widthHW, this.heightHW)).rewind();
        pngDecoder.decode(this.data.getBuffer(), 4 * this.widthHW, PNGDecoder.Format.RGBA);
        if (b) {
            this.mask = pngDecoder.mask;
        }
    }
    
    public static ImageData createSteamAvatar(final long n) {
        final WrappedBuffer allocate = DirectBufferAllocator.allocate(65536);
        final int createSteamAvatar = SteamFriends.CreateSteamAvatar(n, allocate.getBuffer());
        if (createSteamAvatar <= 0) {
            return null;
        }
        final int n2 = allocate.getBuffer().position() / (createSteamAvatar * 4);
        allocate.getBuffer().flip();
        return new ImageData(createSteamAvatar, n2, allocate);
    }
    
    public MipMapLevel getData() {
        if (this.data == null) {
            this.data = new MipMapLevel(this.widthHW, this.heightHW, DirectBufferAllocator.allocate(67108864));
        }
        this.data.rewind();
        return this.data;
    }
    
    public void makeTransp(final byte b, final byte b2, final byte b3) {
        this.makeTransp(b, b2, b3, (byte)0);
    }
    
    public void makeTransp(final byte b, final byte b2, final byte b3, final byte b4) {
        this.solid = false;
        final ByteBuffer buffer = this.data.getBuffer();
        buffer.rewind();
        final int n = this.widthHW * 4;
        for (int i = 0; i < this.heightHW; ++i) {
            final int position = buffer.position();
            for (int j = 0; j < this.widthHW; ++j) {
                final byte value = buffer.get();
                final byte value2 = buffer.get();
                final byte value3 = buffer.get();
                if (value == b && value2 == b2 && value3 == b3) {
                    buffer.put(b4);
                }
                else {
                    buffer.get();
                }
                if (j == this.width) {
                    buffer.position(position + n);
                    break;
                }
            }
            if (i == this.height) {
                break;
            }
        }
        buffer.rewind();
    }
    
    public void setData(final BufferedImage bufferedImage) {
        if (bufferedImage != null) {
            this.setData(bufferedImage.getData());
        }
    }
    
    public void setData(final Raster raster) {
        if (raster == null) {
            new Exception().printStackTrace();
            return;
        }
        this.width = raster.getWidth();
        this.height = raster.getHeight();
        if (this.width > this.widthHW || this.height > this.heightHW) {
            new Exception().printStackTrace();
            return;
        }
        final int[] pixels = raster.getPixels(0, 0, this.width, this.height, (int[])null);
        final ByteBuffer buffer = this.data.getBuffer();
        buffer.rewind();
        int n = 0;
        int n2 = buffer.position();
        final int n3 = this.widthHW * 4;
        for (int i = 0; i < pixels.length; ++i) {
            if (++n > this.width) {
                buffer.position(n2 + n3);
                n2 = buffer.position();
                n = 1;
            }
            buffer.put((byte)pixels[i]);
            buffer.put((byte)pixels[++i]);
            buffer.put((byte)pixels[++i]);
            buffer.put((byte)pixels[++i]);
        }
        buffer.rewind();
        this.solid = false;
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.data = new MipMapLevel(this.widthHW, this.heightHW);
        final ByteBuffer buffer = this.data.getBuffer();
        for (int i = 0; i < this.widthHW * this.heightHW; ++i) {
            buffer.put(objectInputStream.readByte()).put(objectInputStream.readByte()).put(objectInputStream.readByte()).put(objectInputStream.readByte());
        }
        buffer.flip();
    }
    
    private void setData(final Pcx pcx) {
        this.width = pcx.imageWidth;
        this.height = pcx.imageHeight;
        if (this.width > this.widthHW || this.height > this.heightHW) {
            new Exception().printStackTrace();
            return;
        }
        final ByteBuffer buffer = this.data.getBuffer();
        buffer.rewind();
        int n = 0;
        buffer.position();
        final int n2 = this.widthHW * 4;
        for (int i = 0; i < this.heightHW * this.widthHW * 3; ++i) {
            if (++n > this.width) {
                buffer.position();
                n = 1;
            }
            buffer.put(pcx.imageData[i]);
            buffer.put(pcx.imageData[++i]);
            buffer.put(pcx.imageData[++i]);
            buffer.put((byte)(-1));
        }
        buffer.rewind();
        this.solid = false;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        final ByteBuffer buffer = this.data.getBuffer();
        buffer.rewind();
        for (int i = 0; i < this.widthHW * this.heightHW; ++i) {
            objectOutputStream.writeByte(buffer.get());
            objectOutputStream.writeByte(buffer.get());
            objectOutputStream.writeByte(buffer.get());
            objectOutputStream.writeByte(buffer.get());
        }
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public int getHeightHW() {
        return this.heightHW;
    }
    
    public boolean isSolid() {
        return this.solid;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getWidthHW() {
        return this.widthHW;
    }
    
    public int getMipMapCount() {
        if (this.data == null) {
            return 0;
        }
        if (this.mipMapCount < 0) {
            this.mipMapCount = calculateNumMips(this.widthHW, this.heightHW);
        }
        return this.mipMapCount;
    }
    
    public MipMapLevel getMipMapData(final int n) {
        if (this.data != null && !this.alphaPaddingDone) {
            this.performAlphaPadding();
        }
        if (n == 0) {
            return this.getData();
        }
        if (this.mipMaps == null) {
            this.generateMipMaps();
        }
        final MipMapLevel mipMapLevel = this.mipMaps[n - 1];
        mipMapLevel.rewind();
        return mipMapLevel;
    }
    
    public void initMipMaps() {
        final int mipMapCount = this.getMipMapCount();
        final int min = PZMath.min(0, mipMapCount - 1);
        for (int n = mipMapCount, i = min; i < n; ++i) {
            this.getMipMapData(i);
        }
    }
    
    public void dispose() {
        if (this.data != null) {
            this.data.dispose();
            this.data = null;
        }
        if (this.mipMaps != null) {
            for (int i = 0; i < this.mipMaps.length; ++i) {
                this.mipMaps[i].dispose();
                this.mipMaps[i] = null;
            }
            this.mipMaps = null;
        }
    }
    
    private void generateMipMaps() {
        this.mipMapCount = calculateNumMips(this.widthHW, this.heightHW);
        final int n = this.mipMapCount - 1;
        this.mipMaps = new MipMapLevel[n];
        final MipMapLevel data = this.getData();
        final int widthHW = this.widthHW;
        final int heightHW = this.heightHW;
        MipMapLevel mipMapLevel = data;
        final int n2 = widthHW;
        final int n3 = heightHW;
        int n4 = getNextMipDimension(n2);
        int n5 = getNextMipDimension(n3);
        for (int i = 0; i < n; ++i) {
            final MipMapLevel mipMapLevel2 = new MipMapLevel(n4, n5);
            if (i < 2) {
                this.scaleMipLevelMaxAlpha(mipMapLevel, mipMapLevel2, i);
            }
            else {
                this.scaleMipLevelAverage(mipMapLevel, mipMapLevel2, i);
            }
            this.performAlphaPadding(mipMapLevel2);
            this.mipMaps[i] = mipMapLevel2;
            mipMapLevel = mipMapLevel2;
            n4 = getNextMipDimension(n4);
            n5 = getNextMipDimension(n5);
        }
    }
    
    private void scaleMipLevelMaxAlpha(final MipMapLevel mipMapLevel, final MipMapLevel mipMapLevel2, final int n) {
        final L_generateMipMaps l_generateMipMaps = ImageData.TL_generateMipMaps.get();
        final ByteBuffer buffer = mipMapLevel2.getBuffer();
        buffer.rewind();
        final int width = mipMapLevel.width;
        final int height = mipMapLevel.height;
        final ByteBuffer buffer2 = mipMapLevel.getBuffer();
        final int width2 = mipMapLevel2.width;
        for (int height2 = mipMapLevel2.height, i = 0; i < height2; ++i) {
            for (int j = 0; j < width2; ++j) {
                final int[] pixelBytes = l_generateMipMaps.pixelBytes;
                final int[] originalPixel = l_generateMipMaps.originalPixel;
                final int[] resultPixelBytes = l_generateMipMaps.resultPixelBytes;
                getPixelClamped(buffer2, width, height, j * 2, i * 2, originalPixel);
                int n2;
                if (originalPixel[3] > 0) {
                    PZArrayUtil.arrayCopy(resultPixelBytes, originalPixel, 0, 4);
                    n2 = 1;
                }
                else {
                    PZArrayUtil.arraySet(resultPixelBytes, 0);
                    n2 = 0;
                }
                final int n3 = n2 + this.sampleNeighborPixelDiscard(buffer2, width, height, j * 2 + 1, i * 2, pixelBytes, resultPixelBytes) + this.sampleNeighborPixelDiscard(buffer2, width, height, j * 2, i * 2 + 1, pixelBytes, resultPixelBytes) + this.sampleNeighborPixelDiscard(buffer2, width, height, j * 2 + 1, i * 2 + 1, pixelBytes, resultPixelBytes);
                if (n3 > 0) {
                    final int[] array = resultPixelBytes;
                    final int n4 = 0;
                    array[n4] /= n3;
                    final int[] array2 = resultPixelBytes;
                    final int n5 = 1;
                    array2[n5] /= n3;
                    final int[] array3 = resultPixelBytes;
                    final int n6 = 2;
                    array3[n6] /= n3;
                    final int[] array4 = resultPixelBytes;
                    final int n7 = 3;
                    array4[n7] /= n3;
                    if (DebugOptions.instance.IsoSprite.WorldMipmapColors.getValue()) {
                        setMipmapDebugColors(n, resultPixelBytes);
                    }
                }
                setPixel(buffer, width2, height2, j, i, resultPixelBytes);
            }
        }
    }
    
    private void scaleMipLevelAverage(final MipMapLevel mipMapLevel, final MipMapLevel mipMapLevel2, final int n) {
        final L_generateMipMaps l_generateMipMaps = ImageData.TL_generateMipMaps.get();
        final ByteBuffer buffer = mipMapLevel2.getBuffer();
        buffer.rewind();
        final int width = mipMapLevel.width;
        final int height = mipMapLevel.height;
        final ByteBuffer buffer2 = mipMapLevel.getBuffer();
        final int width2 = mipMapLevel2.width;
        for (int height2 = mipMapLevel2.height, i = 0; i < height2; ++i) {
            for (int j = 0; j < width2; ++j) {
                final int[] resultPixelBytes = l_generateMipMaps.resultPixelBytes;
                final int n2 = 1;
                getPixelClamped(buffer2, width, height, j * 2, i * 2, resultPixelBytes);
                final int n3 = n2 + getPixelDiscard(buffer2, width, height, j * 2 + 1, i * 2, resultPixelBytes) + getPixelDiscard(buffer2, width, height, j * 2, i * 2 + 1, resultPixelBytes) + getPixelDiscard(buffer2, width, height, j * 2 + 1, i * 2 + 1, resultPixelBytes);
                final int[] array = resultPixelBytes;
                final int n4 = 0;
                array[n4] /= n3;
                final int[] array2 = resultPixelBytes;
                final int n5 = 1;
                array2[n5] /= n3;
                final int[] array3 = resultPixelBytes;
                final int n6 = 2;
                array3[n6] /= n3;
                final int[] array4 = resultPixelBytes;
                final int n7 = 3;
                array4[n7] /= n3;
                if (resultPixelBytes[3] != 0 && DebugOptions.instance.IsoSprite.WorldMipmapColors.getValue()) {
                    setMipmapDebugColors(n, resultPixelBytes);
                }
                setPixel(buffer, width2, height2, j, i, resultPixelBytes);
            }
        }
    }
    
    public static int calculateNumMips(final int n, final int n2) {
        return PZMath.max(calculateNumMips(n), calculateNumMips(n2));
    }
    
    private static int calculateNumMips(final int n) {
        int n2 = 0;
        for (int i = n; i > 0; i >>= 1, ++n2) {}
        return n2;
    }
    
    private void performAlphaPadding() {
        final MipMapLevel data = this.data;
        if (data == null || data.data == null) {
            return;
        }
        this.performAlphaPadding(data);
        this.alphaPaddingDone = true;
    }
    
    private void performAlphaPadding(final MipMapLevel mipMapLevel) {
        final L_performAlphaPadding l_performAlphaPadding = ImageData.TL_performAlphaPadding.get();
        final ByteBuffer buffer = mipMapLevel.getBuffer();
        final int width = mipMapLevel.width;
        for (int height = mipMapLevel.height, i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                final int n = buffer.get((i * width + j) * 4 + 3) & 0xFF;
                if (n != 255) {
                    if (n == 0) {
                        final int[] pixelClamped = getPixelClamped(buffer, width, height, j, i, l_performAlphaPadding.pixelRGBA);
                        final int[] newPixelRGBA = l_performAlphaPadding.newPixelRGBA;
                        PZArrayUtil.arraySet(newPixelRGBA, 0);
                        newPixelRGBA[3] = pixelClamped[3];
                        final int n2 = 0 + this.sampleNeighborPixelDiscard(buffer, width, height, j - 1, i, l_performAlphaPadding.pixelRGBA_neighbor, newPixelRGBA) + this.sampleNeighborPixelDiscard(buffer, width, height, j, i - 1, l_performAlphaPadding.pixelRGBA_neighbor, newPixelRGBA) + this.sampleNeighborPixelDiscard(buffer, width, height, j - 1, i - 1, l_performAlphaPadding.pixelRGBA_neighbor, newPixelRGBA) + this.sampleNeighborPixelDiscard(buffer, width, height, j + 1, i, l_performAlphaPadding.pixelRGBA_neighbor, newPixelRGBA) + this.sampleNeighborPixelDiscard(buffer, width, height, j, i + 1, l_performAlphaPadding.pixelRGBA_neighbor, newPixelRGBA) + this.sampleNeighborPixelDiscard(buffer, width, height, j + 1, i + 1, l_performAlphaPadding.pixelRGBA_neighbor, newPixelRGBA);
                        if (n2 > 0) {
                            final int[] array = newPixelRGBA;
                            final int n3 = 0;
                            array[n3] /= n2;
                            final int[] array2 = newPixelRGBA;
                            final int n4 = 1;
                            array2[n4] /= n2;
                            final int[] array3 = newPixelRGBA;
                            final int n5 = 2;
                            array3[n5] /= n2;
                            newPixelRGBA[3] = pixelClamped[3];
                            setPixel(buffer, width, height, j, i, newPixelRGBA);
                        }
                    }
                }
            }
        }
    }
    
    private int sampleNeighborPixelDiscard(final ByteBuffer byteBuffer, final int n, final int n2, final int n3, final int n4, final int[] array, final int[] array2) {
        if (n3 < 0 || n3 >= n || n4 < 0 || n4 >= n2) {
            return 0;
        }
        getPixelClamped(byteBuffer, n, n2, n3, n4, array);
        if (array[3] > 0) {
            final int n5 = 0;
            array2[n5] += array[0];
            final int n6 = 1;
            array2[n6] += array[1];
            final int n7 = 2;
            array2[n7] += array[2];
            final int n8 = 3;
            array2[n8] += array[3];
            return 1;
        }
        return 0;
    }
    
    public static int getPixelDiscard(final ByteBuffer byteBuffer, final int n, final int n2, final int n3, final int n4, final int[] array) {
        if (n3 < 0 || n3 >= n || n4 < 0 || n4 >= n2) {
            return 0;
        }
        final int n5 = (n3 + n4 * n) * 4;
        final int n6 = 0;
        array[n6] += (byteBuffer.get(n5) & 0xFF);
        final int n7 = 1;
        array[n7] += (byteBuffer.get(n5 + 1) & 0xFF);
        final int n8 = 2;
        array[n8] += (byteBuffer.get(n5 + 2) & 0xFF);
        final int n9 = 3;
        array[n9] += (byteBuffer.get(n5 + 3) & 0xFF);
        return 1;
    }
    
    public static int[] getPixelClamped(final ByteBuffer byteBuffer, final int n, final int n2, int clamp, int clamp2, final int[] array) {
        clamp = PZMath.clamp(clamp, 0, n - 1);
        clamp2 = PZMath.clamp(clamp2, 0, n2 - 1);
        final int n3 = (clamp + clamp2 * n) * 4;
        array[0] = (byteBuffer.get(n3) & 0xFF);
        array[1] = (byteBuffer.get(n3 + 1) & 0xFF);
        array[2] = (byteBuffer.get(n3 + 2) & 0xFF);
        array[3] = (byteBuffer.get(n3 + 3) & 0xFF);
        return array;
    }
    
    public static void setPixel(final ByteBuffer byteBuffer, final int n, final int n2, final int n3, final int n4, final int[] array) {
        final int n5 = (n3 + n4 * n) * 4;
        byteBuffer.put(n5, (byte)(array[0] & 0xFF));
        byteBuffer.put(n5 + 1, (byte)(array[1] & 0xFF));
        byteBuffer.put(n5 + 2, (byte)(array[2] & 0xFF));
        byteBuffer.put(n5 + 3, (byte)(array[3] & 0xFF));
    }
    
    public static int getNextMipDimension(int n) {
        if (n > 1) {
            n >>= 1;
        }
        return n;
    }
    
    private static void setMipmapDebugColors(final int n, final int[] array) {
        switch (n) {
            case 0: {
                array[0] = 255;
                array[2] = (array[1] = 0);
                break;
            }
            case 1: {
                array[0] = 0;
                array[1] = 255;
                array[2] = 0;
                break;
            }
            case 2: {
                array[1] = (array[0] = 0);
                array[2] = 255;
                break;
            }
            case 3: {
                array[1] = (array[0] = 255);
                array[2] = 0;
                break;
            }
            case 4: {
                array[0] = 255;
                array[1] = 0;
                array[2] = 255;
                break;
            }
            case 5: {
                array[0] = 0;
                array[2] = (array[1] = 0);
                break;
            }
            case 6: {
                array[0] = 255;
                array[2] = (array[1] = 255);
                break;
            }
            case 7: {
                array[0] = 128;
                array[2] = (array[1] = 128);
                break;
            }
        }
    }
    
    static {
        dds = new DDSLoader();
        TL_generateMipMaps = ThreadLocal.withInitial((Supplier<? extends L_generateMipMaps>)L_generateMipMaps::new);
        TL_performAlphaPadding = ThreadLocal.withInitial((Supplier<? extends L_performAlphaPadding>)L_performAlphaPadding::new);
    }
    
    private static final class L_generateMipMaps
    {
        final int[] pixelBytes;
        final int[] originalPixel;
        final int[] resultPixelBytes;
        
        private L_generateMipMaps() {
            this.pixelBytes = new int[4];
            this.originalPixel = new int[4];
            this.resultPixelBytes = new int[4];
        }
    }
    
    static final class L_performAlphaPadding
    {
        final int[] pixelRGBA;
        final int[] newPixelRGBA;
        final int[] pixelRGBA_neighbor;
        
        L_performAlphaPadding() {
            this.pixelRGBA = new int[4];
            this.newPixelRGBA = new int[4];
            this.pixelRGBA_neighbor = new int[4];
        }
    }
}

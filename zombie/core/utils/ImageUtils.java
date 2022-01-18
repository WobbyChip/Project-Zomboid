// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.utils;

import java.awt.image.WritableRaster;
import java.io.IOException;
import java.awt.image.RenderedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.awt.image.BufferedImage;
import java.nio.IntBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjglx.BufferUtils;
import zombie.core.Core;
import java.nio.ByteBuffer;
import zombie.core.textures.Texture;

public class ImageUtils
{
    public static boolean USE_MIPMAP;
    
    private ImageUtils() {
    }
    
    public static void depureTexture(final Texture texture, final float n) {
        final WrappedBuffer data = texture.getData();
        final ByteBuffer buffer = data.getBuffer();
        buffer.rewind();
        final int n2 = (int)(n * 255.0f);
        final long n3 = texture.getWidthHW() * texture.getHeightHW();
        for (int n4 = 0; n4 < n3; ++n4) {
            buffer.mark();
            buffer.get();
            buffer.get();
            buffer.get();
            final byte value = buffer.get();
            int n5;
            if (value < 0) {
                n5 = 256 + value;
            }
            else {
                n5 = value;
            }
            if (n5 < n2) {
                buffer.reset();
                buffer.put((byte)0);
                buffer.put((byte)0);
                buffer.put((byte)0);
                buffer.put((byte)0);
            }
        }
        buffer.flip();
        texture.setData(buffer);
        data.dispose();
    }
    
    public static int getNextPowerOfTwo(final int n) {
        int i;
        for (i = 2; i < n; i += i) {}
        return i;
    }
    
    public static int getNextPowerOfTwoHW(final int n) {
        int i;
        for (i = 2; i < n; i += i) {}
        return i;
    }
    
    public static Texture getScreenShot() {
        final Texture texture = new Texture(Core.getInstance().getScreenWidth(), Core.getInstance().getScreenHeight(), 0);
        final IntBuffer intBuffer = BufferUtils.createIntBuffer(4);
        texture.bind();
        intBuffer.rewind();
        GL11.glTexParameteri(3553, 10241, 9729);
        GL11.glTexParameteri(3553, 10240, 9729);
        GL11.glCopyTexImage2D(3553, 0, 6408, 0, 0, texture.getWidthHW(), texture.getHeightHW(), 0);
        return texture;
    }
    
    public static ByteBuffer makeTransp(final ByteBuffer byteBuffer, final int n, final int n2, final int n3, final int n4, final int n5) {
        return makeTransp(byteBuffer, n, n2, n3, 0, n4, n5);
    }
    
    public static ByteBuffer makeTransp(final ByteBuffer byteBuffer, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        byteBuffer.rewind();
        for (int i = 0; i < n6; ++i) {
            for (int j = 0; j < n5; ++j) {
                final byte value = byteBuffer.get();
                final byte value2 = byteBuffer.get();
                final byte value3 = byteBuffer.get();
                if (value == (byte)n && value2 == (byte)n2 && value3 == (byte)n3) {
                    byteBuffer.put((byte)n4);
                }
                else {
                    byteBuffer.get();
                }
            }
        }
        byteBuffer.rewind();
        return byteBuffer;
    }
    
    public static void saveBmpImage(final Texture texture, final String s) {
        saveImage(texture, s, "bmp");
    }
    
    public static void saveImage(final Texture texture, final String pathname, final String s) {
        final BufferedImage im = new BufferedImage(texture.getWidth(), texture.getHeight(), 1);
        final WritableRaster raster = im.getRaster();
        final WrappedBuffer data = texture.getData();
        final ByteBuffer buffer = data.getBuffer();
        buffer.rewind();
        for (int n = 0; n < texture.getHeightHW() && n < texture.getHeight(); ++n) {
            for (int i = 0; i < texture.getWidthHW(); ++i) {
                if (i >= texture.getWidth()) {
                    buffer.get();
                    buffer.get();
                    buffer.get();
                    buffer.get();
                }
                else {
                    raster.setPixel(i, texture.getHeight() - 1 - n, new int[] { buffer.get(), buffer.get(), buffer.get() });
                    buffer.get();
                }
            }
        }
        data.dispose();
        try {
            ImageIO.write(im, "png", new File(pathname));
        }
        catch (IOException ex) {}
    }
    
    public static void saveJpgImage(final Texture texture, final String s) {
        saveImage(texture, s, "jpg");
    }
    
    public static void savePngImage(final Texture texture, final String s) {
        saveImage(texture, s, "png");
    }
    
    static {
        ImageUtils.USE_MIPMAP = true;
    }
}

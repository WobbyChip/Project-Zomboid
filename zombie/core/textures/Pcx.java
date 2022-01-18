// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.textures;

import zombie.GameWindow;
import java.io.InputStream;
import zombie.debug.DebugLog;
import java.awt.image.ImageProducer;
import java.awt.image.MemoryImageSource;
import java.awt.Toolkit;
import java.awt.Image;
import java.net.URL;
import java.util.HashMap;

public class Pcx
{
    public static HashMap<String, Pcx> Cache;
    public byte[] imageData;
    public int imageWidth;
    public int imageHeight;
    public int[] palette;
    public int[] pic;
    
    public Pcx(final String s) {
    }
    
    public Pcx(final URL url) {
    }
    
    public Pcx(final String s, final int[] array) {
    }
    
    public Pcx(final String s, final String s2) {
    }
    
    public Image getImage() {
        final int[] pix = new int[this.imageWidth * this.imageHeight];
        int n = 0;
        int n2 = 0;
        for (int i = 0; i < this.imageWidth; ++i) {
            for (int j = 0; j < this.imageHeight; ++j) {
                pix[n++] = (0xFF000000 | (this.imageData[n2++] & 0xFF) << 16 | (this.imageData[n2++] & 0xFF) << 8 | (this.imageData[n2++] & 0xFF));
            }
        }
        return Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(this.imageWidth, this.imageHeight, pix, 0, this.imageWidth));
    }
    
    int loadPCX(final URL url) {
        try {
            final InputStream openStream = url.openStream();
            final int available = openStream.available();
            final byte[] array = new byte[available + 1];
            array[available] = 0;
            for (int i = 0; i < available; ++i) {
                array[i] = (byte)openStream.read();
            }
            openStream.close();
            final byte[] array2 = array;
            if (available == -1) {
                return -1;
            }
            final pcx_t pcx_t = new pcx_t(array2);
            final byte[] data = pcx_t.data;
            if (pcx_t.manufacturer != '\n' || pcx_t.version != '\u0005' || pcx_t.encoding != '\u0001' || pcx_t.bits_per_pixel != '\b' || pcx_t.xmax >= 640 || pcx_t.ymax >= 480) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/net/URL;)Ljava/lang/String;, url));
                return -1;
            }
            this.palette = new int[768];
            for (int j = 0; j < 768; ++j) {
                if (available - 128 - 768 + j < pcx_t.data.length) {
                    this.palette[j] = (pcx_t.data[available - 128 - 768 + j] & 0xFF);
                }
            }
            this.imageWidth = pcx_t.xmax + 1;
            this.imageHeight = pcx_t.ymax + 1;
            final int[] pic = new int[(pcx_t.ymax + 1) * (pcx_t.xmax + 1)];
            this.pic = pic;
            final int[] array3 = pic;
            int n = 0;
            int n2 = 0;
            for (short n3 = 0; n3 <= pcx_t.ymax; ++n3, n += pcx_t.xmax + 1) {
                short n4 = 0;
                while (n4 <= pcx_t.xmax) {
                    byte b = data[n2++];
                    int n5;
                    if ((b & 0xC0) == 0xC0) {
                        n5 = (b & 0x3F);
                        b = data[n2++];
                    }
                    else {
                        n5 = 1;
                    }
                    while (n5-- > 0) {
                        array3[n + n4++] = (b & 0xFF);
                    }
                }
            }
            if (this.pic == null || this.palette == null) {
                return -1;
            }
            this.imageData = new byte[(this.imageWidth + 1) * (this.imageHeight + 1) * 3];
            for (int k = 0; k < this.imageWidth * this.imageHeight; ++k) {
                this.imageData[k * 3] = (byte)this.palette[this.pic[k] * 3];
                this.imageData[k * 3 + 1] = (byte)this.palette[this.pic[k] * 3 + 1];
                this.imageData[k * 3 + 2] = (byte)this.palette[this.pic[k] * 3 + 2];
            }
            return 1;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return 1;
        }
    }
    
    int loadPCXminusPal(final String s) {
        try {
            if (Pcx.Cache.containsKey(s)) {
                final Pcx pcx = Pcx.Cache.get(s);
                this.imageWidth = pcx.imageWidth;
                this.imageHeight = pcx.imageHeight;
                this.imageData = new byte[(pcx.imageWidth + 1) * (pcx.imageHeight + 1) * 3];
                for (int i = 0; i < pcx.imageWidth * pcx.imageHeight; ++i) {
                    this.imageData[i * 3] = (byte)this.palette[pcx.pic[i] * 3];
                    this.imageData[i * 3 + 1] = (byte)this.palette[pcx.pic[i] * 3 + 1];
                    this.imageData[i * 3 + 2] = (byte)this.palette[pcx.pic[i] * 3 + 2];
                }
                return 1;
            }
            final InputStream resourceAsStream = GameWindow.class.getClassLoader().getResourceAsStream(s);
            if (resourceAsStream == null) {
                return 0;
            }
            final int available = resourceAsStream.available();
            final byte[] array = new byte[available + 1];
            array[available] = 0;
            for (int j = 0; j < available; ++j) {
                array[j] = (byte)resourceAsStream.read();
            }
            resourceAsStream.close();
            final byte[] array2 = array;
            if (available == -1) {
                return -1;
            }
            final pcx_t pcx_t = new pcx_t(array2);
            final byte[] data = pcx_t.data;
            if (pcx_t.manufacturer != '\n' || pcx_t.version != '\u0005' || pcx_t.encoding != '\u0001' || pcx_t.bits_per_pixel != '\b' || pcx_t.xmax >= 640 || pcx_t.ymax >= 480) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
                return -1;
            }
            this.imageWidth = pcx_t.xmax + 1;
            this.imageHeight = pcx_t.ymax + 1;
            final int[] pic = new int[(pcx_t.ymax + 1) * (pcx_t.xmax + 1)];
            this.pic = pic;
            final int[] array3 = pic;
            int n = 0;
            int n2 = 0;
            for (short n3 = 0; n3 <= pcx_t.ymax; ++n3, n += pcx_t.xmax + 1) {
                short n4 = 0;
                while (n4 <= pcx_t.xmax) {
                    byte b = data[n2++];
                    int n5;
                    if ((b & 0xC0) == 0xC0) {
                        n5 = (b & 0x3F);
                        b = data[n2++];
                    }
                    else {
                        n5 = 1;
                    }
                    while (n5-- > 0) {
                        array3[n + n4++] = (b & 0xFF);
                    }
                }
            }
            if (this.pic == null || this.palette == null) {
                return -1;
            }
            this.imageData = new byte[(this.imageWidth + 1) * (this.imageHeight + 1) * 3];
            for (int k = 0; k < this.imageWidth * this.imageHeight; ++k) {
                this.imageData[k * 3] = (byte)this.palette[this.pic[k] * 3];
                this.imageData[k * 3 + 1] = (byte)this.palette[this.pic[k] * 3 + 1];
                this.imageData[k * 3 + 2] = (byte)this.palette[this.pic[k] * 3 + 2];
            }
            Pcx.Cache.put(s, this);
            return 1;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return 1;
        }
    }
    
    int loadPCXpal(final String name) {
        try {
            final InputStream resourceAsStream = GameWindow.class.getClassLoader().getResourceAsStream(name);
            if (resourceAsStream == null) {
                return 1;
            }
            final int available = resourceAsStream.available();
            final byte[] array = new byte[available + 1];
            array[available] = 0;
            for (int i = 0; i < available; ++i) {
                array[i] = (byte)resourceAsStream.read();
            }
            resourceAsStream.close();
            final byte[] array2 = array;
            if (available == -1) {
                return -1;
            }
            final pcx_t pcx_t = new pcx_t(array2);
            final byte[] data = pcx_t.data;
            if (pcx_t.manufacturer != '\n' || pcx_t.version != '\u0005' || pcx_t.encoding != '\u0001' || pcx_t.bits_per_pixel != '\b' || pcx_t.xmax >= 640 || pcx_t.ymax >= 480) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, name));
                return -1;
            }
            this.palette = new int[768];
            for (int j = 0; j < 768; ++j) {
                if (available - 128 - 768 + j < pcx_t.data.length) {
                    this.palette[j] = (pcx_t.data[available - 128 - 768 + j] & 0xFF);
                }
            }
            this.imageWidth = pcx_t.xmax + 1;
            this.imageHeight = pcx_t.ymax + 1;
            this.pic = new int[(pcx_t.ymax + 1) * (pcx_t.xmax + 1)];
            if (this.pic == null || this.palette == null) {
                return -1;
            }
            return 1;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return 1;
        }
    }
    
    private void loadPCXpal(final int[] palette) {
        this.palette = palette;
    }
    
    static {
        Pcx.Cache = new HashMap<String, Pcx>();
    }
    
    class pcx_t
    {
        char bits_per_pixel;
        short bytes_per_line;
        char color_planes;
        byte[] data;
        char encoding;
        byte[] filler;
        short hres;
        short vres;
        char manufacturer;
        int[] palette;
        short palette_type;
        char reserved;
        char version;
        short xmin;
        short ymin;
        short xmax;
        short ymax;
        
        pcx_t(final byte[] array) {
            this.filler = new byte[58];
            this.palette = new int[48];
            this.manufacturer = (char)array[0];
            this.version = (char)array[1];
            this.encoding = (char)array[2];
            this.bits_per_pixel = (char)array[3];
            this.xmin = (short)(array[4] + (array[5] << 8) & 0xFF);
            this.ymin = (short)(array[6] + (array[7] << 8) & 0xFF);
            this.xmax = (short)(array[8] + (array[9] << 8) & 0xFF);
            this.ymax = (short)(array[10] + (array[11] << 8) & 0xFF);
            this.hres = (short)(array[12] + (array[13] << 8) & 0xFF);
            this.vres = (short)(array[14] + (array[15] << 8) & 0xFF);
            for (int i = 0; i < 48; ++i) {
                this.palette[i] = (array[16 + i] & 0xFF);
            }
            this.reserved = (char)array[64];
            this.color_planes = (char)array[65];
            this.bytes_per_line = (short)(array[66] + (array[67] << 8) & 0xFF);
            this.palette_type = (short)(array[68] + (array[69] << 8) & 0xFF);
            for (int j = 0; j < 58; ++j) {
                this.filler[j] = array[70 + j];
            }
            this.data = new byte[array.length - 128];
            for (int k = 0; k < array.length - 128; ++k) {
                this.data[k] = array[128 + k];
            }
        }
    }
}

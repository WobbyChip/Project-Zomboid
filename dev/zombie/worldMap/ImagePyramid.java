// 
// Decompiled by Procyon v0.5.36
// 

package zombie.worldMap;

import zombie.core.opengl.RenderThread;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.awt.image.RenderedImage;
import javax.imageio.ImageIO;
import java.nio.IntBuffer;
import java.awt.image.BufferedImage;
import java.util.zip.ZipOutputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import zombie.core.textures.MipMapLevel;
import zombie.core.SpriteRenderer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL;
import java.io.InputStream;
import java.nio.file.NoSuchFileException;
import zombie.core.textures.ImageData;
import java.nio.file.OpenOption;
import zombie.core.textures.TextureID;
import java.io.File;
import zombie.core.textures.Texture;
import java.util.Iterator;
import java.nio.file.DirectoryStream;
import zombie.core.logger.ExceptionLogger;
import zombie.core.math.PZMath;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;
import zombie.core.Core;
import java.util.HashSet;
import java.util.HashMap;
import java.nio.file.FileSystem;

public final class ImagePyramid
{
    String m_directory;
    String m_zipFile;
    FileSystem m_zipFS;
    final HashMap<String, PyramidTexture> m_textures;
    final HashSet<String> m_missing;
    int m_requestNumber;
    int m_minX;
    int m_minY;
    int m_maxX;
    int m_maxY;
    float m_resolution;
    int m_minZ;
    int m_maxZ;
    int MAX_TEXTURES;
    int MAX_REQUEST_NUMBER;
    
    public ImagePyramid() {
        this.m_textures = new HashMap<String, PyramidTexture>();
        this.m_missing = new HashSet<String>();
        this.m_requestNumber = 0;
        this.m_resolution = 1.0f;
        this.MAX_TEXTURES = 100;
        this.MAX_REQUEST_NUMBER = (Core.bDebug ? 10000 : Integer.MAX_VALUE);
    }
    
    public void setDirectory(final String directory) {
        if (this.m_zipFile != null) {
            this.m_zipFile = null;
            if (this.m_zipFS != null) {
                try {
                    this.m_zipFS.close();
                }
                catch (IOException ex) {}
                this.m_zipFS = null;
            }
        }
        this.m_directory = directory;
    }
    
    public void setZipFile(final String zipFile) {
        this.m_directory = null;
        this.m_zipFile = zipFile;
        this.m_zipFS = this.openZipFile();
        this.readInfoFile();
        this.m_minZ = Integer.MAX_VALUE;
        this.m_maxZ = Integer.MIN_VALUE;
        if (this.m_zipFS != null) {
            try {
                final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(this.m_zipFS.getPath("/", new String[0]));
                try {
                    for (final Path path : directoryStream) {
                        if (Files.isDirectory(path, new LinkOption[0])) {
                            final int tryParseInt = PZMath.tryParseInt(path.getFileName().toString(), -1);
                            this.m_minZ = PZMath.min(this.m_minZ, tryParseInt);
                            this.m_maxZ = PZMath.max(this.m_maxZ, tryParseInt);
                        }
                    }
                    if (directoryStream != null) {
                        directoryStream.close();
                    }
                }
                catch (Throwable t) {
                    if (directoryStream != null) {
                        try {
                            directoryStream.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                    }
                    throw t;
                }
            }
            catch (IOException ex) {
                ExceptionLogger.logException(ex);
            }
        }
    }
    
    public Texture getImage(final int n, final int n2, final int n3) {
        final String format = String.format("%dx%dx%d", n, n2, n3);
        if (this.m_missing.contains(format)) {
            return null;
        }
        final File file = new File(this.m_directory, String.format("%s%d%stile%dx%d.png", File.separator, n3, File.separator, n, n2));
        if (!file.exists()) {
            this.m_missing.add(format);
            return null;
        }
        return Texture.getSharedTexture(file.getAbsolutePath());
    }
    
    public TextureID getTexture(final int i, final int j, final int k) {
        final String format = String.format("%dx%dx%d", i, j, k);
        if (this.m_textures.containsKey(format)) {
            final PyramidTexture pyramidTexture = this.m_textures.get(format);
            pyramidTexture.m_requestNumber = this.m_requestNumber++;
            if (this.m_requestNumber >= this.MAX_REQUEST_NUMBER) {
                this.resetRequestNumbers();
            }
            return pyramidTexture.m_textureID;
        }
        if (this.m_missing.contains(format)) {
            return null;
        }
        if (this.m_zipFile != null) {
            if (this.m_zipFS == null || !this.m_zipFS.isOpen()) {
                return null;
            }
            try {
                final Path path = this.m_zipFS.getPath(String.valueOf(k), String.format("tile%dx%d.png", i, j));
                try {
                    final InputStream inputStream = Files.newInputStream(path, new OpenOption[0]);
                    try {
                        final ImageData imageData = new ImageData(inputStream, false);
                        final PyramidTexture checkTextureCache = this.checkTextureCache(format);
                        if (checkTextureCache.m_textureID == null) {
                            checkTextureCache.m_textureID = new TextureID(imageData);
                        }
                        else {
                            this.replaceTextureData(checkTextureCache, imageData);
                        }
                        final TextureID textureID = checkTextureCache.m_textureID;
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        return textureID;
                    }
                    catch (Throwable t) {
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            }
                            catch (Throwable exception) {
                                t.addSuppressed(exception);
                            }
                        }
                        throw t;
                    }
                }
                catch (NoSuchFileException ex2) {
                    this.m_missing.add(format);
                }
            }
            catch (Exception ex) {
                this.m_missing.add(format);
                ex.printStackTrace();
            }
            return null;
        }
        else {
            final File file = new File(this.m_directory, String.format("%s%d%stile%dx%d.png", File.separator, k, File.separator, i, j));
            if (!file.exists()) {
                this.m_missing.add(format);
                return null;
            }
            final Texture sharedTexture = Texture.getSharedTexture(file.getAbsolutePath());
            return (sharedTexture == null) ? null : sharedTexture.getTextureId();
        }
    }
    
    private void replaceTextureData(final PyramidTexture pyramidTexture, final ImageData imageData) {
        int n;
        if (GL.getCapabilities().GL_ARB_texture_compression) {
            n = 34030;
        }
        else {
            n = 6408;
        }
        GL11.glBindTexture(3553, Texture.lastTextureID = pyramidTexture.m_textureID.getID());
        SpriteRenderer.ringBuffer.restoreBoundTextures = true;
        GL11.glTexImage2D(3553, 0, n, imageData.getWidthHW(), imageData.getHeightHW(), 0, 6408, 5121, imageData.getData().getBuffer());
        imageData.dispose();
    }
    
    public void generateFiles(final String s, final String s2) throws Exception {
        final ImageData imageData = new ImageData(s);
        if (imageData == null) {
            return;
        }
        final int n = 256;
        for (int n2 = 5, i = 0; i < n2; ++i) {
            final MipMapLevel mipMapData = imageData.getMipMapData(i);
            final float n3 = imageData.getWidth() / (float)(1 << i);
            final float n4 = imageData.getHeight() / (float)(1 << i);
            final int n5 = (int)Math.ceil(n3 / n);
            for (int n6 = (int)Math.ceil(n4 / n), j = 0; j < n6; ++j) {
                for (int k = 0; k < n5; ++k) {
                    this.writeImageToFile(this.getBufferedImage(mipMapData, k, j, n), s2, k, j, i);
                }
            }
        }
    }
    
    public FileSystem openZipFile() {
        try {
            return FileSystems.newFileSystem(Paths.get(this.m_zipFile, new String[0]));
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public void generateZip(final String s, final String name) throws Exception {
        final ImageData imageData = new ImageData(s);
        if (imageData == null) {
            return;
        }
        final int n = 256;
        final FileOutputStream out = new FileOutputStream(name);
        try {
            final BufferedOutputStream out2 = new BufferedOutputStream(out);
            try {
                final ZipOutputStream zipOutputStream = new ZipOutputStream(out2);
                try {
                    for (int n2 = 5, i = 0; i < n2; ++i) {
                        final MipMapLevel mipMapData = imageData.getMipMapData(i);
                        final float n3 = imageData.getWidth() / (float)(1 << i);
                        final float n4 = imageData.getHeight() / (float)(1 << i);
                        final int n5 = (int)Math.ceil(n3 / n);
                        for (int n6 = (int)Math.ceil(n4 / n), j = 0; j < n6; ++j) {
                            for (int k = 0; k < n5; ++k) {
                                this.writeImageToZip(this.getBufferedImage(mipMapData, k, j, n), zipOutputStream, k, j, i);
                            }
                        }
                        if (n3 <= n && n4 <= n) {
                            break;
                        }
                    }
                    zipOutputStream.close();
                }
                catch (Throwable t) {
                    try {
                        zipOutputStream.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                    throw t;
                }
                out2.close();
            }
            catch (Throwable t2) {
                try {
                    out2.close();
                }
                catch (Throwable exception2) {
                    t2.addSuppressed(exception2);
                }
                throw t2;
            }
            out.close();
        }
        catch (Throwable t3) {
            try {
                out.close();
            }
            catch (Throwable exception3) {
                t3.addSuppressed(exception3);
            }
            throw t3;
        }
    }
    
    BufferedImage getBufferedImage(final MipMapLevel mipMapLevel, final int n, final int n2, final int n3) {
        final BufferedImage bufferedImage = new BufferedImage(n3, n3, 2);
        final int[] rgbArray = new int[n3];
        final IntBuffer intBuffer = mipMapLevel.getBuffer().asIntBuffer();
        for (int i = 0; i < n3; ++i) {
            intBuffer.get(n * n3 + (n2 * n3 + i) * mipMapLevel.width, rgbArray);
            for (int j = 0; j < n3; ++j) {
                final int n4 = rgbArray[j];
                rgbArray[j] = ((n4 >> 24 & 0xFF) << 24 | (n4 & 0xFF) << 16 | (n4 >> 8 & 0xFF) << 8 | (n4 >> 16 & 0xFF));
            }
            bufferedImage.setRGB(0, i, n3, 1, rgbArray, 0, n3);
        }
        return bufferedImage;
    }
    
    void writeImageToFile(final BufferedImage im, final String s, final int i, final int j, final int n) throws Exception {
        final File parent = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;I)Ljava/lang/String;, s, File.separator, n));
        if (!parent.exists() && !parent.mkdirs()) {
            return;
        }
        ImageIO.write(im, "png", new File(parent, String.format("tile%dx%d.png", i, j)));
    }
    
    void writeImageToZip(final BufferedImage im, final ZipOutputStream output, final int i, final int j, final int k) throws Exception {
        output.putNextEntry(new ZipEntry(String.format("%d/tile%dx%d.png", k, i, j)));
        ImageIO.write(im, "PNG", output);
        output.closeEntry();
    }
    
    PyramidTexture checkTextureCache(final String key) {
        if (this.m_textures.size() < this.MAX_TEXTURES) {
            final PyramidTexture value = new PyramidTexture();
            value.m_key = key;
            value.m_requestNumber = this.m_requestNumber++;
            this.m_textures.put(key, value);
            if (this.m_requestNumber >= this.MAX_REQUEST_NUMBER) {
                this.resetRequestNumbers();
            }
            return value;
        }
        PyramidTexture value2 = null;
        for (final PyramidTexture pyramidTexture : this.m_textures.values()) {
            if (value2 == null || value2.m_requestNumber > pyramidTexture.m_requestNumber) {
                value2 = pyramidTexture;
            }
        }
        this.m_textures.remove(value2.m_key);
        value2.m_key = key;
        value2.m_requestNumber = this.m_requestNumber++;
        this.m_textures.put(value2.m_key, value2);
        if (this.m_requestNumber >= this.MAX_REQUEST_NUMBER) {
            this.resetRequestNumbers();
        }
        return value2;
    }
    
    void resetRequestNumbers() {
        final ArrayList<PyramidTexture> list = new ArrayList<PyramidTexture>(this.m_textures.values());
        list.sort(Comparator.comparingInt(pyramidTexture -> pyramidTexture.m_requestNumber));
        this.m_requestNumber = 1;
        final Iterator<PyramidTexture> iterator = list.iterator();
        while (iterator.hasNext()) {
            iterator.next().m_requestNumber = this.m_requestNumber++;
        }
        list.clear();
    }
    
    private void readInfoFile() {
        if (this.m_zipFS == null || !this.m_zipFS.isOpen()) {
            return;
        }
        final Path path = this.m_zipFS.getPath("pyramid.txt", new String[0]);
        try {
            final InputStream inputStream = Files.newInputStream(path, new OpenOption[0]);
            try {
                final InputStreamReader in = new InputStreamReader(inputStream);
                try {
                    final BufferedReader bufferedReader = new BufferedReader(in);
                    try {
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            if (line.startsWith("VERSION=")) {
                                line.substring("VERSION=".length());
                            }
                            else if (line.startsWith("bounds=")) {
                                final String[] split = line.substring("bounds=".length()).split(" ");
                                if (split.length != 4) {
                                    continue;
                                }
                                this.m_minX = PZMath.tryParseInt(split[0], -1);
                                this.m_minY = PZMath.tryParseInt(split[1], -1);
                                this.m_maxX = PZMath.tryParseInt(split[2], -1);
                                this.m_maxY = PZMath.tryParseInt(split[3], -1);
                            }
                            else {
                                if (!line.startsWith("resolution=")) {
                                    continue;
                                }
                                this.m_resolution = PZMath.tryParseFloat(line.substring("resolution=".length()), 1.0f);
                            }
                        }
                        bufferedReader.close();
                    }
                    catch (Throwable t) {
                        try {
                            bufferedReader.close();
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
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            catch (Throwable t3) {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    }
                    catch (Throwable exception3) {
                        t3.addSuppressed(exception3);
                    }
                }
                throw t3;
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void destroy() {
        if (this.m_zipFS != null) {
            try {
                this.m_zipFS.close();
            }
            catch (IOException ex) {}
            this.m_zipFS = null;
        }
        final Iterator<PyramidTexture> iterator;
        RenderThread.invokeOnRenderContext(() -> {
            this.m_textures.values().iterator();
            while (iterator.hasNext()) {
                iterator.next().m_textureID.destroy();
            }
            return;
        });
        this.m_missing.clear();
        this.m_textures.clear();
    }
    
    public static final class PyramidTexture
    {
        String m_key;
        int m_requestNumber;
        TextureID m_textureID;
    }
}

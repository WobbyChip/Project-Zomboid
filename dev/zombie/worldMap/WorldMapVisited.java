// 
// Decompiled by Procyon v0.5.36
// 

package zombie.worldMap;

import zombie.characters.IsoPlayer;
import zombie.iso.IsoMetaGrid;
import zombie.core.logger.ExceptionLogger;
import zombie.iso.IsoWorld;
import zombie.core.math.PZMath;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import zombie.core.Core;
import java.io.File;
import zombie.ZomboidFileSystem;
import zombie.iso.SliceY;
import zombie.core.opengl.RenderThread;
import java.util.Objects;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL11;
import zombie.core.utils.ImageUtils;
import zombie.SandboxOptions;
import org.lwjgl.BufferUtils;
import java.util.Arrays;
import zombie.iso.Vector2;
import zombie.core.opengl.ShaderProgram;
import zombie.worldMap.styles.WorldMapStyleLayer;
import java.nio.ByteBuffer;
import zombie.core.textures.TextureID;

public class WorldMapVisited
{
    private static WorldMapVisited instance;
    private int m_minX;
    private int m_minY;
    private int m_maxX;
    private int m_maxY;
    byte[] m_visited;
    boolean m_changed;
    int m_changeX1;
    int m_changeY1;
    int m_changeX2;
    int m_changeY2;
    private final int[] m_updateMinX;
    private final int[] m_updateMinY;
    private final int[] m_updateMaxX;
    private final int[] m_updateMaxY;
    private static final int TEXTURE_BPP = 4;
    private TextureID m_textureID;
    private int m_textureW;
    private int m_textureH;
    private ByteBuffer m_textureBuffer;
    private boolean m_textureChanged;
    private final WorldMapStyleLayer.RGBAf m_color;
    private final WorldMapStyleLayer.RGBAf m_gridColor;
    private boolean m_mainMenu;
    private static ShaderProgram m_shaderProgram;
    private static ShaderProgram m_gridShaderProgram;
    static final int UNITS_PER_CELL = 10;
    static final int SQUARES_PER_CELL = 300;
    static final int SQUARES_PER_UNIT = 30;
    static final int TEXTURE_PAD = 1;
    static final int BIT_VISITED = 1;
    static final int BIT_KNOWN = 2;
    Vector2 m_vector2;
    
    public WorldMapVisited() {
        this.m_changed = false;
        this.m_changeX1 = 0;
        this.m_changeY1 = 0;
        this.m_changeX2 = 0;
        this.m_changeY2 = 0;
        this.m_updateMinX = new int[4];
        this.m_updateMinY = new int[4];
        this.m_updateMaxX = new int[4];
        this.m_updateMaxY = new int[4];
        this.m_textureW = 0;
        this.m_textureH = 0;
        this.m_textureChanged = false;
        this.m_color = new WorldMapStyleLayer.RGBAf().init(0.85882354f, 0.84313726f, 0.7529412f, 1.0f);
        this.m_gridColor = new WorldMapStyleLayer.RGBAf().init(this.m_color.r * 0.85f, this.m_color.g * 0.85f, this.m_color.b * 0.85f, 1.0f);
        this.m_mainMenu = false;
        this.m_vector2 = new Vector2();
        Arrays.fill(this.m_updateMinX, -1);
        Arrays.fill(this.m_updateMinY, -1);
        Arrays.fill(this.m_updateMaxX, -1);
        Arrays.fill(this.m_updateMaxY, -1);
    }
    
    public void setBounds(int minX, int minY, int maxX, int maxY) {
        if (minX > maxX || minY > maxY) {
            maxX = (minX = (minY = (maxY = 0)));
            this.m_mainMenu = true;
        }
        this.m_minX = minX;
        this.m_minY = minY;
        this.m_maxX = maxX;
        this.m_maxY = maxY;
        this.m_changed = true;
        this.m_changeX1 = 0;
        this.m_changeY1 = 0;
        this.m_changeX2 = this.getWidthInCells() * 10 - 1;
        this.m_changeY2 = this.getHeightInCells() * 10 - 1;
        this.m_visited = new byte[this.getWidthInCells() * 10 * this.getHeightInCells() * 10];
        this.m_textureW = this.calcTextureWidth();
        this.m_textureH = this.calcTextureHeight();
        (this.m_textureBuffer = BufferUtils.createByteBuffer(this.m_textureW * this.m_textureH * 4)).limit(this.m_textureBuffer.capacity());
        final byte b = (byte)(SandboxOptions.getInstance().Map.MapAllKnown.getValue() ? 0 : -1);
        final byte b2 = -1;
        final byte b3 = -1;
        final byte b4 = -1;
        for (int i = 0; i < this.m_textureBuffer.limit(); i += 4) {
            this.m_textureBuffer.put(i, b);
            this.m_textureBuffer.put(i + 1, b2);
            this.m_textureBuffer.put(i + 2, b3);
            this.m_textureBuffer.put(i + 3, b4);
        }
        this.m_textureID = new TextureID(this.m_textureW, this.m_textureH, 0);
    }
    
    public int getMinX() {
        return this.m_minX;
    }
    
    public int getMinY() {
        return this.m_minY;
    }
    
    private int getWidthInCells() {
        return this.m_maxX - this.m_minX + 1;
    }
    
    private int getHeightInCells() {
        return this.m_maxY - this.m_minY + 1;
    }
    
    private int calcTextureWidth() {
        return ImageUtils.getNextPowerOfTwo(this.getWidthInCells() * 10 + 2);
    }
    
    private int calcTextureHeight() {
        return ImageUtils.getNextPowerOfTwo(this.getHeightInCells() * 10 + 2);
    }
    
    public void setKnownInCells(final int n, final int n2, final int n3, final int n4) {
        this.setFlags(n * 300, n2 * 300, (n3 + 1) * 300, (n4 + 1) * 300, 2);
    }
    
    public void clearKnownInCells(final int n, final int n2, final int n3, final int n4) {
        this.clearFlags(n * 300, n2 * 300, (n3 + 1) * 300, (n4 + 1) * 300, 2);
    }
    
    public void setVisitedInCells(final int n, final int n2, final int n3, final int n4) {
        this.setFlags(n * 300, n2 * 300, n3 * 300, n4 * 300, 1);
    }
    
    public void clearVisitedInCells(final int n, final int n2, final int n3, final int n4) {
        this.clearFlags(n * 300, n2 * 300, n3 * 300, n4 * 300, 1);
    }
    
    public void setKnownInSquares(final int n, final int n2, final int n3, final int n4) {
        this.setFlags(n, n2, n3, n4, 2);
    }
    
    public void clearKnownInSquares(final int n, final int n2, final int n3, final int n4) {
        this.clearFlags(n, n2, n3, n4, 2);
    }
    
    public void setVisitedInSquares(final int n, final int n2, final int n3, final int n4) {
        this.setFlags(n, n2, n3, n4, 1);
    }
    
    public void clearVisitedInSquares(final int n, final int n2, final int n3, final int n4) {
        this.clearFlags(n, n2, n3, n4, 1);
    }
    
    private void updateVisitedTexture() {
        this.m_textureID.bind();
        GL11.glTexImage2D(3553, 0, 6408, this.m_textureW, this.m_textureH, 0, 6408, 5121, this.m_textureBuffer);
    }
    
    public void renderMain() {
        this.m_textureChanged |= this.updateTextureData(this.m_textureBuffer, this.m_textureW);
    }
    
    private void initShader() {
        WorldMapVisited.m_shaderProgram = ShaderProgram.createShaderProgram("worldMapVisited", false, true);
        if (WorldMapVisited.m_shaderProgram.isCompiled()) {}
    }
    
    public void render(final float n, final float n2, final int n3, final int n4, final int n5, final int n6, final float n7, final boolean b) {
        if (this.m_mainMenu) {
            return;
        }
        GL13.glActiveTexture(33984);
        GL13.glClientActiveTexture(33984);
        GL11.glEnable(3553);
        if (this.m_textureChanged) {
            this.m_textureChanged = false;
            this.updateVisitedTexture();
        }
        this.m_textureID.bind();
        final int n8 = b ? 9729 : 9728;
        GL11.glTexParameteri(3553, 10241, n8);
        GL11.glTexParameteri(3553, 10240, n8);
        GL11.glEnable(3042);
        GL11.glTexEnvi(8960, 8704, 8448);
        GL11.glColor4f(this.m_color.r, this.m_color.g, this.m_color.b, this.m_color.a);
        if (WorldMapVisited.m_shaderProgram == null) {
            this.initShader();
        }
        if (!WorldMapVisited.m_shaderProgram.isCompiled()) {
            return;
        }
        WorldMapVisited.m_shaderProgram.Start();
        final float n9 = (1 + (n3 - this.m_minX) * 10) / (float)this.m_textureW;
        final float n10 = (1 + (n4 - this.m_minY) * 10) / (float)this.m_textureH;
        final float n11 = (1 + (n5 + 1 - this.m_minX) * 10) / (float)this.m_textureW;
        final float n12 = (1 + (n6 + 1 - this.m_minY) * 10) / (float)this.m_textureH;
        final float n13 = (n3 - this.m_minX) * 300 * n7;
        final float n14 = (n4 - this.m_minY) * 300 * n7;
        final float n15 = (n5 + 1 - this.m_minX) * 300 * n7;
        final float n16 = (n6 + 1 - this.m_minY) * 300 * n7;
        GL11.glBegin(7);
        GL11.glTexCoord2f(n9, n10);
        GL11.glVertex2f(n + n13, n2 + n14);
        GL11.glTexCoord2f(n11, n10);
        GL11.glVertex2f(n + n15, n2 + n14);
        GL11.glTexCoord2f(n11, n12);
        GL11.glVertex2f(n + n15, n2 + n16);
        GL11.glTexCoord2f(n9, n12);
        GL11.glVertex2f(n + n13, n2 + n16);
        GL11.glEnd();
        WorldMapVisited.m_shaderProgram.End();
    }
    
    public void renderGrid(final float n, final float n2, final int n3, final int n4, final int n5, final int n6, final float n7, final float n8) {
        if (n8 < 11.0f) {
            return;
        }
        if (WorldMapVisited.m_gridShaderProgram == null) {
            WorldMapVisited.m_gridShaderProgram = ShaderProgram.createShaderProgram("worldMapGrid", false, true);
        }
        if (!WorldMapVisited.m_gridShaderProgram.isCompiled()) {
            return;
        }
        WorldMapVisited.m_gridShaderProgram.Start();
        final float n9 = n + (n3 * 300 - this.m_minX * 300) * n7;
        final float n10 = n2 + (n4 * 300 - this.m_minY * 300) * n7;
        final float n11 = n9 + (n5 - n3 + 1) * 300 * n7;
        final float n12 = n10 + (n6 - n4 + 1) * 300 * n7;
        final VBOLinesUV vboLinesUV = WorldMapRenderer.m_vboLinesUV;
        vboLinesUV.setMode(1);
        vboLinesUV.setLineWidth(0.5f);
        vboLinesUV.startRun(this.m_textureID);
        final float r = this.m_gridColor.r;
        final float g = this.m_gridColor.g;
        final float b = this.m_gridColor.b;
        final float a = this.m_gridColor.a;
        int n13 = 1;
        if (n8 < 13.0f) {
            n13 = 8;
        }
        else if (n8 < 14.0f) {
            n13 = 4;
        }
        else if (n8 < 15.0f) {
            n13 = 2;
        }
        WorldMapVisited.m_gridShaderProgram.setValue("UVOffset", this.m_vector2.set(0.5f / this.m_textureW, 0.0f));
        for (int i = n3 * 10; i <= (n5 + 1) * 10; i += n13) {
            vboLinesUV.reserve(2);
            vboLinesUV.addElement(n + (i * 30 - this.m_minX * 300) * n7, n10, 0.0f, (1 + i - this.m_minX * 10) / (float)this.m_textureW, 1.0f / this.m_textureH, r, g, b, a);
            vboLinesUV.addElement(n + (i * 30 - this.m_minX * 300) * n7, n12, 0.0f, (1 + i - this.m_minX * 10) / (float)this.m_textureW, (1 + this.getHeightInCells() * 10) / (float)this.m_textureH, r, g, b, a);
        }
        WorldMapVisited.m_gridShaderProgram.setValue("UVOffset", this.m_vector2.set(0.0f, 0.5f / this.m_textureH));
        for (int j = n4 * 10; j <= (n6 + 1) * 10; j += n13) {
            vboLinesUV.reserve(2);
            vboLinesUV.addElement(n9, n2 + (j * 30 - this.m_minY * 300) * n7, 0.0f, 1.0f / this.m_textureW, (1 + j - this.m_minY * 10) / (float)this.m_textureH, r, g, b, a);
            vboLinesUV.addElement(n11, n2 + (j * 30 - this.m_minY * 300) * n7, 0.0f, (1 + this.getWidthInCells() * 10) / (float)this.m_textureW, (1 + j - this.m_minY * 10) / (float)this.m_textureH, r, g, b, a);
        }
        vboLinesUV.flush();
        WorldMapVisited.m_gridShaderProgram.End();
    }
    
    private void destroy() {
        if (this.m_textureID != null) {
            final TextureID textureID = this.m_textureID;
            Objects.requireNonNull(textureID);
            RenderThread.invokeOnRenderContext(textureID::destroy);
        }
        this.m_textureBuffer = null;
        this.m_visited = null;
    }
    
    public void save(final ByteBuffer byteBuffer) {
        byteBuffer.putInt(this.m_minX);
        byteBuffer.putInt(this.m_minY);
        byteBuffer.putInt(this.m_maxX);
        byteBuffer.putInt(this.m_maxY);
        byteBuffer.putInt(10);
        byteBuffer.put(this.m_visited);
    }
    
    public void load(final ByteBuffer byteBuffer, final int n) {
        final int int1 = byteBuffer.getInt();
        final int int2 = byteBuffer.getInt();
        final int int3 = byteBuffer.getInt();
        final int int4 = byteBuffer.getInt();
        final int int5 = byteBuffer.getInt();
        if (int1 != this.m_minX || int2 != this.m_minY || int3 != this.m_maxX || int4 != this.m_maxY || int5 != 10) {
            return;
        }
        byteBuffer.get(this.m_visited);
    }
    
    public void save() throws IOException {
        final ByteBuffer sliceBuffer = SliceY.SliceBuffer;
        sliceBuffer.clear();
        sliceBuffer.putInt(186);
        this.save(sliceBuffer);
        final FileOutputStream out = new FileOutputStream(new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getGameModeCacheDir(), File.separator, Core.GameSaveWorld, File.separator)));
        try {
            final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(out);
            try {
                bufferedOutputStream.write(sliceBuffer.array(), 0, sliceBuffer.position());
                bufferedOutputStream.close();
            }
            catch (Throwable t) {
                try {
                    bufferedOutputStream.close();
                }
                catch (Throwable exception) {
                    t.addSuppressed(exception);
                }
                throw t;
            }
            out.close();
        }
        catch (Throwable t2) {
            try {
                out.close();
            }
            catch (Throwable exception2) {
                t2.addSuppressed(exception2);
            }
            throw t2;
        }
    }
    
    public void load() throws IOException {
        final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getGameModeCacheDir(), File.separator, Core.GameSaveWorld, File.separator));
        try {
            final FileInputStream in = new FileInputStream(file);
            try {
                final BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
                try {
                    final ByteBuffer sliceBuffer = SliceY.SliceBuffer;
                    sliceBuffer.clear();
                    sliceBuffer.limit(bufferedInputStream.read(sliceBuffer.array()));
                    this.load(sliceBuffer, sliceBuffer.getInt());
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
        catch (FileNotFoundException ex) {}
    }
    
    private void setFlags(int clamp, int clamp2, int clamp3, int clamp4, final int n) {
        clamp -= this.m_minX * 300;
        clamp2 -= this.m_minY * 300;
        clamp3 -= this.m_minX * 300;
        clamp4 -= this.m_minY * 300;
        final int widthInCells = this.getWidthInCells();
        final int heightInCells = this.getHeightInCells();
        clamp = PZMath.clamp(clamp, 0, widthInCells * 300 - 1);
        clamp2 = PZMath.clamp(clamp2, 0, heightInCells * 300 - 1);
        clamp3 = PZMath.clamp(clamp3, 0, widthInCells * 300 - 1);
        clamp4 = PZMath.clamp(clamp4, 0, heightInCells * 300 - 1);
        if (clamp == clamp3 || clamp2 == clamp4) {
            return;
        }
        final int n2 = clamp / 30;
        int n3 = clamp3 / 30;
        final int n4 = clamp2 / 30;
        int n5 = clamp4 / 30;
        if (clamp3 % 30 == 0) {
            --n3;
        }
        if (clamp4 % 30 == 0) {
            --n5;
        }
        boolean b = false;
        final int n6 = widthInCells * 10;
        for (int i = n4; i <= n5; ++i) {
            for (int j = n2; j <= n3; ++j) {
                final byte b2 = this.m_visited[j + i * n6];
                if ((b2 & n) != n) {
                    this.m_visited[j + i * n6] = (byte)(b2 | n);
                    b = true;
                }
            }
        }
        if (b) {
            this.m_changed = true;
            this.m_changeX1 = PZMath.min(this.m_changeX1, n2);
            this.m_changeY1 = PZMath.min(this.m_changeY1, n4);
            this.m_changeX2 = PZMath.max(this.m_changeX2, n3);
            this.m_changeY2 = PZMath.max(this.m_changeY2, n5);
        }
    }
    
    private void clearFlags(int clamp, int clamp2, int clamp3, int clamp4, final int n) {
        clamp -= this.m_minX * 300;
        clamp2 -= this.m_minY * 300;
        clamp3 -= this.m_minX * 300;
        clamp4 -= this.m_minY * 300;
        final int widthInCells = this.getWidthInCells();
        final int heightInCells = this.getHeightInCells();
        clamp = PZMath.clamp(clamp, 0, widthInCells * 300 - 1);
        clamp2 = PZMath.clamp(clamp2, 0, heightInCells * 300 - 1);
        clamp3 = PZMath.clamp(clamp3, 0, widthInCells * 300 - 1);
        clamp4 = PZMath.clamp(clamp4, 0, heightInCells * 300 - 1);
        if (clamp == clamp3 || clamp2 == clamp4) {
            return;
        }
        final int n2 = clamp / 30;
        int n3 = clamp3 / 30;
        final int n4 = clamp2 / 30;
        int n5 = clamp4 / 30;
        if (clamp3 % 30 == 0) {
            --n3;
        }
        if (clamp4 % 30 == 0) {
            --n5;
        }
        boolean b = false;
        final int n6 = widthInCells * 10;
        for (int i = n4; i <= n5; ++i) {
            for (int j = n2; j <= n3; ++j) {
                final byte b2 = this.m_visited[j + i * n6];
                if ((b2 & n) != n) {
                    this.m_visited[j + i * n6] = (byte)(b2 & ~n);
                    b = true;
                }
            }
        }
        if (b) {
            this.m_changed = true;
            this.m_changeX1 = PZMath.min(this.m_changeX1, n2);
            this.m_changeY1 = PZMath.min(this.m_changeY1, n4);
            this.m_changeX2 = PZMath.max(this.m_changeX2, n3);
            this.m_changeY2 = PZMath.max(this.m_changeY2, n5);
        }
    }
    
    private boolean updateTextureData(final ByteBuffer byteBuffer, final int n) {
        if (!this.m_changed) {
            return false;
        }
        this.m_changed = false;
        final int n2 = 4;
        final int n3 = this.getWidthInCells() * 10;
        for (int i = this.m_changeY1; i <= this.m_changeY2; ++i) {
            byteBuffer.position((1 + this.m_changeX1) * n2 + (1 + i) * n * n2);
            for (int j = this.m_changeX1; j <= this.m_changeX2; ++j) {
                final byte b = this.m_visited[j + i * n3];
                byteBuffer.put((byte)(((b & 0x2) != 0x0) ? 0 : -1));
                byteBuffer.put((byte)(((b & 0x1) != 0x0) ? 0 : -1));
                byteBuffer.put((byte)(-1));
                byteBuffer.put((byte)(-1));
            }
        }
        byteBuffer.position(0);
        this.m_changeX1 = Integer.MAX_VALUE;
        this.m_changeY1 = Integer.MAX_VALUE;
        this.m_changeX2 = Integer.MIN_VALUE;
        this.m_changeY2 = Integer.MIN_VALUE;
        return true;
    }
    
    void setUnvisitedRGBA(final float n, final float n2, final float n3, final float n4) {
        this.m_color.init(n, n2, n3, n4);
    }
    
    void setUnvisitedGridRGBA(final float n, final float n2, final float n3, final float n4) {
        this.m_gridColor.init(n, n2, n3, n4);
    }
    
    boolean hasFlags(int clamp, int clamp2, int clamp3, int clamp4, final int n, final boolean b) {
        clamp -= this.m_minX * 300;
        clamp2 -= this.m_minY * 300;
        clamp3 -= this.m_minX * 300;
        clamp4 -= this.m_minY * 300;
        final int widthInCells = this.getWidthInCells();
        final int heightInCells = this.getHeightInCells();
        clamp = PZMath.clamp(clamp, 0, widthInCells * 300 - 1);
        clamp2 = PZMath.clamp(clamp2, 0, heightInCells * 300 - 1);
        clamp3 = PZMath.clamp(clamp3, 0, widthInCells * 300 - 1);
        clamp4 = PZMath.clamp(clamp4, 0, heightInCells * 300 - 1);
        if (clamp == clamp3 || clamp2 == clamp4) {
            return false;
        }
        final int n2 = clamp / 30;
        int n3 = clamp3 / 30;
        final int n4 = clamp2 / 30;
        int n5 = clamp4 / 30;
        if (clamp3 % 30 == 0) {
            --n3;
        }
        if (clamp4 % 30 == 0) {
            --n5;
        }
        final int n6 = widthInCells * 10;
        for (int i = n4; i <= n5; ++i) {
            for (int j = n2; j <= n3; ++j) {
                final byte b2 = this.m_visited[j + i * n6];
                if (b) {
                    if ((b2 & n) != 0x0) {
                        return true;
                    }
                }
                else if ((b2 & n) != n) {
                    return false;
                }
            }
        }
        return !b;
    }
    
    boolean isCellVisible(final int n, final int n2) {
        return this.hasFlags(n * 300, n2 * 300, (n + 1) * 300, (n2 + 1) * 300, 3, true);
    }
    
    public static WorldMapVisited getInstance() {
        final IsoMetaGrid metaGrid = IsoWorld.instance.getMetaGrid();
        if (metaGrid == null) {
            throw new NullPointerException("IsoWorld.instance.MetaGrid is null");
        }
        if (WorldMapVisited.instance == null) {
            (WorldMapVisited.instance = new WorldMapVisited()).setBounds(metaGrid.getMinX(), metaGrid.getMinY(), metaGrid.getMaxX(), metaGrid.getMaxY());
            try {
                WorldMapVisited.instance.load();
                if (SandboxOptions.getInstance().Map.MapAllKnown.getValue()) {
                    WorldMapVisited.instance.setKnownInCells(metaGrid.getMinX(), metaGrid.getMinY(), metaGrid.getMaxX(), metaGrid.getMaxY());
                }
            }
            catch (Throwable t) {
                ExceptionLogger.logException(t);
            }
        }
        return WorldMapVisited.instance;
    }
    
    public static void update() {
        if (IsoWorld.instance == null) {
            return;
        }
        final WorldMapVisited instance = getInstance();
        if (instance == null) {
            return;
        }
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            final IsoPlayer isoPlayer = IsoPlayer.players[i];
            if (isoPlayer != null) {
                if (!isoPlayer.isDead()) {
                    final int n = 25;
                    final int n2 = ((int)isoPlayer.x - n) / 30;
                    final int n3 = ((int)isoPlayer.y - n) / 30;
                    int n4 = ((int)isoPlayer.x + n) / 30;
                    int n5 = ((int)isoPlayer.y + n) / 30;
                    if (((int)isoPlayer.x + n) % 30 == 0) {
                        --n4;
                    }
                    if (((int)isoPlayer.y + n) % 30 == 0) {
                        --n5;
                    }
                    if (n2 != instance.m_updateMinX[i] || n3 != instance.m_updateMinY[i] || n4 != instance.m_updateMaxX[i] || n5 != instance.m_updateMaxY[i]) {
                        instance.m_updateMinX[i] = n2;
                        instance.m_updateMinY[i] = n3;
                        instance.m_updateMaxX[i] = n4;
                        instance.m_updateMaxY[i] = n5;
                        instance.setFlags((int)isoPlayer.x - n, (int)isoPlayer.y - n, (int)isoPlayer.x + n, (int)isoPlayer.y + n, 3);
                    }
                }
            }
        }
    }
    
    public static void SaveAll() {
        final WorldMapVisited instance = WorldMapVisited.instance;
        if (instance != null) {
            try {
                instance.save();
            }
            catch (Exception ex) {
                ExceptionLogger.logException(ex);
            }
        }
    }
    
    public static void Reset() {
        final WorldMapVisited instance = WorldMapVisited.instance;
        if (instance != null) {
            instance.destroy();
            WorldMapVisited.instance = null;
        }
    }
}

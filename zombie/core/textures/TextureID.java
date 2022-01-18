// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.textures;

import zombie.fileSystem.FileSystem;
import org.lwjglx.BufferUtils;
import zombie.IndieGL;
import zombie.SystemDisabler;
import zombie.core.math.PZMath;
import zombie.core.opengl.PZGLUtil;
import org.lwjgl.opengl.GL;
import zombie.core.SpriteRenderer;
import java.nio.ByteBuffer;
import zombie.core.utils.DirectBufferAllocator;
import java.util.Iterator;
import org.lwjgl.opengl.GL11;
import zombie.debug.DebugOptions;
import java.io.InputStream;
import java.io.BufferedInputStream;
import zombie.core.opengl.RenderThread;
import zombie.core.utils.WrappedBuffer;
import zombie.debug.DebugLog;
import zombie.asset.AssetManager;
import zombie.asset.AssetPath;
import zombie.asset.AssetType;
import java.nio.IntBuffer;
import zombie.core.utils.BooleanGrid;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.Serializable;
import zombie.interfaces.IDestroyable;
import zombie.asset.Asset;

public final class TextureID extends Asset implements IDestroyable, Serializable
{
    private static final long serialVersionUID = 4409253583065563738L;
    public static long totalGraphicMemory;
    public static boolean UseFiltering;
    public static boolean bUseCompression;
    public static boolean bUseCompressionOption;
    public static float totalMemUsed;
    private static boolean FREE_MEMORY;
    private static final HashMap<Integer, String> TextureIDMap;
    protected String pathFileName;
    protected boolean solid;
    protected int width;
    protected int widthHW;
    protected int height;
    protected int heightHW;
    protected transient ImageData data;
    protected transient int id;
    private int m_glMagFilter;
    private int m_glMinFilter;
    ArrayList<AlphaColorIndex> alphaList;
    int referenceCount;
    BooleanGrid mask;
    protected int flags;
    public TextureIDAssetParams assetParams;
    public static final IntBuffer deleteTextureIDS;
    public static final AssetType ASSET_TYPE;
    
    public TextureID(final AssetPath assetPath, final AssetManager assetManager, final TextureIDAssetParams assetParams) {
        super(assetPath, assetManager);
        this.id = -1;
        this.m_glMagFilter = -1;
        this.m_glMinFilter = -1;
        this.referenceCount = 0;
        this.flags = 0;
        this.assetParams = assetParams;
        this.flags = ((assetParams == null) ? 0 : this.assetParams.flags);
    }
    
    protected TextureID() {
        super(null, TextureIDAssetManager.instance);
        this.id = -1;
        this.m_glMagFilter = -1;
        this.m_glMinFilter = -1;
        this.referenceCount = 0;
        this.flags = 0;
        this.assetParams = null;
        this.onCreated(State.READY);
    }
    
    public TextureID(final int n, final int n2, final int flags) {
        super(null, TextureIDAssetManager.instance);
        this.id = -1;
        this.m_glMagFilter = -1;
        this.m_glMinFilter = -1;
        this.referenceCount = 0;
        this.flags = 0;
        this.assetParams = new TextureIDAssetParams();
        this.assetParams.flags = flags;
        if ((flags & 0x10) != 0x0) {
            if ((flags & 0x4) != 0x0) {
                DebugLog.General.warn((Object)"FBO incompatible with COMPRESS");
                final TextureIDAssetParams assetParams = this.assetParams;
                assetParams.flags &= 0xFFFFFFFB;
            }
            this.data = new ImageData(n, n2, null);
        }
        else {
            this.data = new ImageData(n, n2);
        }
        this.width = this.data.getWidth();
        this.height = this.data.getHeight();
        this.widthHW = this.data.getWidthHW();
        this.heightHW = this.data.getHeightHW();
        this.solid = this.data.isSolid();
        RenderThread.queueInvokeOnRenderContext(() -> this.createTexture(false));
        this.onCreated(State.READY);
    }
    
    public TextureID(final ImageData data) {
        super(null, TextureIDAssetManager.instance);
        this.id = -1;
        this.m_glMagFilter = -1;
        this.m_glMinFilter = -1;
        this.referenceCount = 0;
        this.flags = 0;
        this.assetParams = null;
        this.data = data;
        RenderThread.invokeOnRenderContext(this::createTexture);
        this.onCreated(State.READY);
    }
    
    public TextureID(final String pathFileName, final String s) {
        super(null, TextureIDAssetManager.instance);
        this.id = -1;
        this.m_glMagFilter = -1;
        this.m_glMinFilter = -1;
        this.referenceCount = 0;
        this.flags = 0;
        this.assetParams = null;
        this.data = new ImageData(pathFileName, s);
        this.pathFileName = pathFileName;
        RenderThread.invokeOnRenderContext(this::createTexture);
        this.onCreated(State.READY);
    }
    
    public TextureID(final String pathFileName, final int[] array) {
        super(null, TextureIDAssetManager.instance);
        this.id = -1;
        this.m_glMagFilter = -1;
        this.m_glMinFilter = -1;
        this.referenceCount = 0;
        this.flags = 0;
        this.assetParams = null;
        this.data = new ImageData(pathFileName, array);
        this.pathFileName = pathFileName;
        RenderThread.invokeOnRenderContext(this::createTexture);
        this.onCreated(State.READY);
    }
    
    public TextureID(String substring, final int n, final int n2, final int n3) throws Exception {
        super(null, TextureIDAssetManager.instance);
        this.id = -1;
        this.m_glMagFilter = -1;
        this.m_glMinFilter = -1;
        this.referenceCount = 0;
        this.flags = 0;
        this.assetParams = null;
        if (substring.startsWith("/")) {
            substring = substring.substring(1);
        }
        int index;
        while ((index = substring.indexOf("\\")) != -1) {
            substring = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, substring.substring(0, index), substring.substring(index + 1));
        }
        (this.data = new ImageData(substring)).makeTransp((byte)n, (byte)n2, (byte)n3);
        if (this.alphaList == null) {
            this.alphaList = new ArrayList<AlphaColorIndex>();
        }
        this.alphaList.add(new AlphaColorIndex(n, n2, n3, 0));
        this.pathFileName = substring;
        RenderThread.invokeOnRenderContext(this::createTexture);
        this.onCreated(State.READY);
    }
    
    public TextureID(final String pathFileName) throws Exception {
        super(null, TextureIDAssetManager.instance);
        this.id = -1;
        this.m_glMagFilter = -1;
        this.m_glMinFilter = -1;
        this.referenceCount = 0;
        this.flags = 0;
        this.assetParams = null;
        if (pathFileName.toLowerCase().contains(".pcx")) {
            this.data = new ImageData(pathFileName, pathFileName);
        }
        else {
            this.data = new ImageData(pathFileName);
        }
        if (this.data.getHeight() == -1) {
            return;
        }
        this.pathFileName = pathFileName;
        RenderThread.invokeOnRenderContext(this::createTexture);
        this.onCreated(State.READY);
    }
    
    public TextureID(final BufferedInputStream bufferedInputStream, final String pathFileName, final boolean b, final Texture.PZFileformat pzFileformat) {
        super(null, TextureIDAssetManager.instance);
        this.id = -1;
        this.m_glMagFilter = -1;
        this.m_glMinFilter = -1;
        this.referenceCount = 0;
        this.flags = 0;
        this.assetParams = null;
        this.data = new ImageData(bufferedInputStream, b, pzFileformat);
        if (this.data.id != -1) {
            this.id = this.data.id;
            this.width = this.data.getWidth();
            this.height = this.data.getHeight();
            this.widthHW = this.data.getWidthHW();
            this.heightHW = this.data.getHeightHW();
            this.solid = this.data.isSolid();
        }
        else {
            if (b) {
                this.mask = this.data.mask;
                this.data.mask = null;
            }
            this.createTexture();
        }
        this.pathFileName = pathFileName;
        this.onCreated(State.READY);
    }
    
    public TextureID(final BufferedInputStream bufferedInputStream, final String pathFileName, final boolean b) throws Exception {
        super(null, TextureIDAssetManager.instance);
        this.id = -1;
        this.m_glMagFilter = -1;
        this.m_glMinFilter = -1;
        this.referenceCount = 0;
        this.flags = 0;
        this.assetParams = null;
        this.data = new ImageData(bufferedInputStream, b);
        if (b) {
            this.mask = this.data.mask;
            this.data.mask = null;
        }
        this.pathFileName = pathFileName;
        RenderThread.invokeOnRenderContext(this::createTexture);
        this.onCreated(State.READY);
    }
    
    public static TextureID createSteamAvatar(final long n) {
        final ImageData steamAvatar = ImageData.createSteamAvatar(n);
        if (steamAvatar == null) {
            return null;
        }
        return new TextureID(steamAvatar);
    }
    
    public int getID() {
        return this.id;
    }
    
    public boolean bind() {
        if (this.id == -1 && this.data == null) {
            Texture.getErrorTexture().bind();
            return true;
        }
        this.debugBoundTexture();
        return (this.id == -1 || this.id != Texture.lastTextureID) && this.bindalways();
    }
    
    public boolean bindalways() {
        this.bindInternal();
        return true;
    }
    
    private void bindInternal() {
        if (this.id == -1) {
            this.generateHwId(this.data != null && this.data.data != null);
        }
        this.assignFilteringFlags();
        Texture.lastlastTextureID = Texture.lastTextureID;
        Texture.lastTextureID = this.id;
        ++Texture.BindCount;
    }
    
    private void debugBoundTexture() {
        if (DebugOptions.instance.Checks.BoundTextures.getValue() && Texture.lastTextureID != -1 && GL11.glGetInteger(34016) == 33984) {
            final int glGetInteger = GL11.glGetInteger(32873);
            if (glGetInteger != Texture.lastTextureID) {
                Object path = null;
                for (final TextureID textureID : TextureIDAssetManager.instance.getAssetTable().values()) {
                    if (textureID.id == Texture.lastTextureID) {
                        path = textureID.getPath().getPath();
                        break;
                    }
                }
                DebugLog.General.error("Texture.lastTextureID %d != GL_TEXTURE_BINDING_2D %d name=%s", Texture.lastTextureID, glGetInteger, path);
            }
        }
    }
    
    @Override
    public void destroy() {
        assert Thread.currentThread() == RenderThread.RenderThread;
        if (this.id == -1) {
            return;
        }
        if (TextureID.deleteTextureIDS.position() == TextureID.deleteTextureIDS.capacity()) {
            TextureID.deleteTextureIDS.flip();
            GL11.glDeleteTextures(TextureID.deleteTextureIDS);
            TextureID.deleteTextureIDS.clear();
        }
        TextureID.deleteTextureIDS.put(this.id);
        this.id = -1;
    }
    
    public void freeMemory() {
        this.data = null;
    }
    
    public WrappedBuffer getData() {
        this.bind();
        final WrappedBuffer allocate = DirectBufferAllocator.allocate(this.heightHW * this.widthHW * 4);
        GL11.glGetTexImage(3553, 0, 6408, 5121, allocate.getBuffer());
        GL11.glBindTexture(3553, Texture.lastTextureID = 0);
        return allocate;
    }
    
    public void setData(final ByteBuffer src) {
        if (src == null) {
            this.freeMemory();
            return;
        }
        this.bind();
        GL11.glTexSubImage2D(3553, 0, 0, 0, this.widthHW, this.heightHW, 6408, 5121, src);
        if (this.data != null) {
            final ByteBuffer buffer = this.data.getData().getBuffer();
            src.flip();
            buffer.clear();
            buffer.put(src);
            buffer.flip();
        }
    }
    
    public ImageData getImageData() {
        return this.data;
    }
    
    public void setImageData(final ImageData data) {
        this.data = data;
        this.width = data.getWidth();
        this.height = data.getHeight();
        this.widthHW = data.getWidthHW();
        this.heightHW = data.getHeightHW();
        if (data.mask != null) {
            this.mask = data.mask;
            data.mask = null;
        }
        RenderThread.queueInvokeOnRenderContext(this::createTexture);
    }
    
    public String getPathFileName() {
        return this.pathFileName;
    }
    
    @Override
    public boolean isDestroyed() {
        return this.id == -1;
    }
    
    public boolean isSolid() {
        return this.solid;
    }
    
    private void createTexture() {
        if (this.data == null) {
            return;
        }
        this.createTexture(true);
    }
    
    private void createTexture(final boolean b) {
        if (this.id != -1) {
            return;
        }
        this.width = this.data.getWidth();
        this.height = this.data.getHeight();
        this.widthHW = this.data.getWidthHW();
        this.heightHW = this.data.getHeightHW();
        this.solid = this.data.isSolid();
        this.generateHwId(b);
    }
    
    private void generateHwId(final boolean b) {
        this.id = GL11.glGenTextures();
        ++Texture.totalTextureID;
        GL11.glBindTexture(3553, Texture.lastTextureID = this.id);
        SpriteRenderer.ringBuffer.restoreBoundTextures = true;
        int flags;
        if (this.assetParams == null) {
            flags = (TextureID.bUseCompressionOption ? 4 : 0);
        }
        else {
            flags = this.assetParams.flags;
        }
        final boolean b2 = (flags & 0x1) != 0x0;
        final boolean b3 = (flags & 0x2) != 0x0;
        final boolean b4 = (flags & 0x10) != 0x0;
        final boolean b5 = (flags & 0x40) != 0x0 && !b4 && b;
        int n;
        if ((flags & 0x4) != 0x0 && GL.getCapabilities().GL_ARB_texture_compression) {
            n = 34030;
        }
        else {
            n = 6408;
        }
        this.m_glMagFilter = (b3 ? 9728 : 9729);
        GL11.glTexParameteri(3553, 10241, this.m_glMinFilter = (b5 ? 9987 : (b2 ? 9728 : 9729)));
        GL11.glTexParameteri(3553, 10240, this.m_glMagFilter);
        if ((flags & 0x20) != 0x0) {
            GL11.glTexParameteri(3553, 10242, 33071);
            GL11.glTexParameteri(3553, 10243, 33071);
        }
        else {
            GL11.glTexParameteri(3553, 10242, 10497);
            GL11.glTexParameteri(3553, 10243, 10497);
        }
        if (b) {
            if (b5) {
                PZGLUtil.checkGLErrorThrow("TextureID.mipMaps.start", new Object[0]);
                final int mipMapCount = this.data.getMipMapCount();
                final int min = PZMath.min(0, mipMapCount - 1);
                for (int n2 = mipMapCount, i = min; i < n2; ++i) {
                    final MipMapLevel mipMapData = this.data.getMipMapData(i);
                    final int width = mipMapData.width;
                    final int height = mipMapData.height;
                    TextureID.totalMemUsed += mipMapData.getDataSize();
                    GL11.glTexImage2D(3553, i - min, n, width, height, 0, 6408, 5121, mipMapData.getBuffer());
                    PZGLUtil.checkGLErrorThrow("TextureID.mipMaps[%d].end", i);
                }
                PZGLUtil.checkGLErrorThrow("TextureID.mipMaps.end", new Object[0]);
            }
            else {
                PZGLUtil.checkGLErrorThrow("TextureID.noMips.start", new Object[0]);
                TextureID.totalMemUsed += this.widthHW * this.heightHW * 4;
                GL11.glTexImage2D(3553, 0, n, this.widthHW, this.heightHW, 0, 6408, 5121, this.data.getData().getBuffer());
                PZGLUtil.checkGLErrorThrow("TextureID.noMips.end", new Object[0]);
            }
        }
        else {
            GL11.glTexImage2D(3553, 0, n, this.widthHW, this.heightHW, 0, 6408, 5121, (ByteBuffer)null);
            TextureID.totalMemUsed += this.widthHW * this.heightHW * 4;
        }
        if (TextureID.FREE_MEMORY) {
            if (this.data != null) {
                this.data.dispose();
            }
            this.data = null;
            if (this.assetParams != null) {
                this.assetParams.subTexture = null;
                this.assetParams = null;
            }
        }
        TextureID.TextureIDMap.put(this.id, this.pathFileName);
        if (SystemDisabler.doEnableDetectOpenGLErrorsInTexture) {
            PZGLUtil.checkGLErrorThrow("generateHwId id:%d pathFileName:%s", this.id, this.pathFileName);
        }
    }
    
    private void assignFilteringFlags() {
        GL11.glBindTexture(3553, this.id);
        if (this.width == 1 && this.height == 1) {
            GL11.glTexParameteri(3553, 10241, 9728);
            GL11.glTexParameteri(3553, 10240, 9728);
            return;
        }
        GL11.glTexParameteri(3553, 10241, this.m_glMinFilter);
        GL11.glTexParameteri(3553, 10240, this.m_glMagFilter);
        if ((this.flags & 0x40) != 0x0 && DebugOptions.instance.IsoSprite.NearestMagFilterAtMinZoom.getValue() && this.isMinZoomLevel() && this.m_glMagFilter != 9728) {
            GL11.glTexParameteri(3553, 10240, 9728);
        }
        if (DebugOptions.instance.IsoSprite.ForceLinearMagFilter.getValue() && this.m_glMagFilter != 9729) {
            GL11.glTexParameteri(3553, 10240, 9729);
        }
        if (DebugOptions.instance.IsoSprite.ForceNearestMagFilter.getValue() && this.m_glMagFilter != 9728) {
            GL11.glTexParameteri(3553, 10240, 9728);
        }
        if (DebugOptions.instance.IsoSprite.ForceNearestMipMapping.getValue() && this.m_glMinFilter == 9987) {
            GL11.glTexParameteri(3553, 10241, 9986);
        }
        if (DebugOptions.instance.IsoSprite.TextureWrapClampToEdge.getValue()) {
            GL11.glTexParameteri(3553, 10242, 33071);
            GL11.glTexParameteri(3553, 10243, 33071);
        }
        if (DebugOptions.instance.IsoSprite.TextureWrapRepeat.getValue()) {
            GL11.glTexParameteri(3553, 10242, 10497);
            GL11.glTexParameteri(3553, 10243, 10497);
        }
        if (SystemDisabler.doEnableDetectOpenGLErrorsInTexture) {
            PZGLUtil.checkGLErrorThrow("assignFilteringFlags id:%d pathFileName:%s", this.id, this.pathFileName);
        }
    }
    
    public void setMagFilter(final int glMagFilter) {
        this.m_glMagFilter = glMagFilter;
    }
    
    public void setMinFilter(final int glMinFilter) {
        this.m_glMinFilter = glMinFilter;
    }
    
    public boolean hasMipMaps() {
        return this.m_glMinFilter == 9987;
    }
    
    private boolean isMaxZoomLevel() {
        return IndieGL.isMaxZoomLevel();
    }
    
    private boolean isMinZoomLevel() {
        return IndieGL.isMinZoomLevel();
    }
    
    @Override
    public void setAssetParams(final AssetManager.AssetParams assetParams) {
        this.assetParams = (TextureIDAssetParams)assetParams;
        this.flags = ((this.assetParams == null) ? 0 : this.assetParams.flags);
    }
    
    @Override
    public AssetType getType() {
        return TextureID.ASSET_TYPE;
    }
    
    static {
        TextureID.totalGraphicMemory = 0L;
        TextureID.UseFiltering = false;
        TextureID.bUseCompression = true;
        TextureID.bUseCompressionOption = true;
        TextureID.totalMemUsed = 0.0f;
        TextureID.FREE_MEMORY = true;
        TextureIDMap = new HashMap<Integer, String>();
        deleteTextureIDS = BufferUtils.createIntBuffer(20);
        ASSET_TYPE = new AssetType("TextureID");
    }
    
    public static final class TextureIDAssetParams extends AssetManager.AssetParams
    {
        FileSystem.SubTexture subTexture;
        int flags;
        
        public TextureIDAssetParams() {
            this.flags = 0;
        }
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.textures;

import java.util.function.Supplier;
import java.util.Iterator;
import java.util.Map;
import zombie.iso.Vector2;
import org.lwjgl.opengl.GL13;
import java.io.IOException;
import java.awt.image.RenderedImage;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import zombie.core.utils.ImageUtils;
import java.util.logging.Level;
import java.util.logging.Logger;
import zombie.debug.DebugOptions;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import java.util.function.Consumer;
import zombie.core.utils.WrappedBuffer;
import zombie.core.utils.BooleanGrid;
import zombie.core.logger.ExceptionLogger;
import zombie.util.Type;
import zombie.core.znet.SteamUtils;
import java.io.File;
import zombie.util.StringUtils;
import zombie.GameWindow;
import zombie.debug.DebugLog;
import zombie.core.bucket.BucketManager;
import zombie.ZomboidFileSystem;
import zombie.network.ServerGUI;
import zombie.network.GameServer;
import java.nio.ByteBuffer;
import zombie.core.opengl.RenderThread;
import java.nio.Buffer;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.opengl.GL11;
import zombie.IndieGL;
import java.io.BufferedInputStream;
import zombie.asset.AssetManager;
import zombie.asset.AssetPath;
import zombie.asset.AssetType;
import zombie.fileSystem.FileSystem;
import java.util.HashMap;
import zombie.iso.objects.ObjectRenderEffects;
import java.util.HashSet;
import java.io.Serializable;
import zombie.interfaces.ITexture;
import zombie.interfaces.IDestroyable;
import zombie.asset.Asset;

public class Texture extends Asset implements IDestroyable, ITexture, Serializable
{
    public static final HashSet<String> nullTextures;
    private static final long serialVersionUID = 7472363451408935314L;
    private static final ObjectRenderEffects objRen;
    public static int BindCount;
    public static boolean bDoingQuad;
    public static float lr;
    public static float lg;
    public static float lb;
    public static float la;
    public static int lastlastTextureID;
    public static int totalTextureID;
    private static Texture white;
    private static Texture errorTexture;
    private static Texture mipmap;
    public static int lastTextureID;
    public static boolean WarnFailFindTexture;
    private static final HashMap<String, Texture> textures;
    private static final HashMap<String, Texture> s_sharedTextureTable;
    private static final HashMap<Long, Texture> steamAvatarMap;
    public boolean flip;
    public float offsetX;
    public float offsetY;
    public boolean bindAlways;
    public float xEnd;
    public float yEnd;
    public float xStart;
    public float yStart;
    protected TextureID dataid;
    protected Mask mask;
    protected String name;
    protected boolean solid;
    protected int width;
    protected int height;
    protected int heightOrig;
    protected int widthOrig;
    private int realWidth;
    private int realHeight;
    private boolean destroyed;
    private Texture splitIconTex;
    private int splitX;
    private int splitY;
    private int splitW;
    private int splitH;
    protected FileSystem.SubTexture subTexture;
    public TextureAssetParams assetParams;
    private static final ThreadLocal<PNGSize> pngSize;
    public static final AssetType ASSET_TYPE;
    
    public Texture(final AssetPath assetPath, final AssetManager assetManager, final TextureAssetParams assetParams) {
        super(assetPath, assetManager);
        this.flip = false;
        this.offsetX = 0.0f;
        this.offsetY = 0.0f;
        this.bindAlways = false;
        this.xEnd = 1.0f;
        this.yEnd = 1.0f;
        this.xStart = 0.0f;
        this.yStart = 0.0f;
        this.realWidth = 0;
        this.realHeight = 0;
        this.destroyed = false;
        this.splitX = -1;
        this.assetParams = assetParams;
        this.name = ((assetPath == null) ? null : assetPath.getPath());
        if (assetParams != null && assetParams.subTexture != null) {
            final FileSystem.SubTexture subTexture = assetParams.subTexture;
            this.splitX = subTexture.m_info.x;
            this.splitY = subTexture.m_info.y;
            this.splitW = subTexture.m_info.w;
            this.splitH = subTexture.m_info.h;
            this.width = this.splitW;
            this.height = this.splitH;
            this.offsetX = (float)subTexture.m_info.ox;
            this.offsetY = (float)subTexture.m_info.oy;
            this.widthOrig = subTexture.m_info.fx;
            this.heightOrig = subTexture.m_info.fy;
            this.name = subTexture.m_info.name;
            this.subTexture = subTexture;
        }
        final TextureID.TextureIDAssetParams textureIDAssetParams = new TextureID.TextureIDAssetParams();
        if (this.assetParams == null || this.assetParams.subTexture == null) {
            if (this.assetParams == null) {
                final TextureID.TextureIDAssetParams textureIDAssetParams2 = textureIDAssetParams;
                textureIDAssetParams2.flags |= (TextureID.bUseCompressionOption ? 4 : 0);
            }
            else {
                textureIDAssetParams.flags = this.assetParams.flags;
            }
            this.dataid = (TextureID)this.getAssetManager().getOwner().get(TextureID.ASSET_TYPE).load(this.getPath(), textureIDAssetParams);
        }
        else {
            textureIDAssetParams.subTexture = this.assetParams.subTexture;
            final String pack_name = textureIDAssetParams.subTexture.m_pack_name;
            final String page_name = textureIDAssetParams.subTexture.m_page_name;
            final FileSystem fileSystem = this.getAssetManager().getOwner().getFileSystem();
            textureIDAssetParams.flags = fileSystem.getTexturePackFlags(pack_name);
            final TextureID.TextureIDAssetParams textureIDAssetParams3 = textureIDAssetParams;
            textureIDAssetParams3.flags |= (fileSystem.getTexturePackAlpha(pack_name, page_name) ? 8 : 0);
            this.dataid = (TextureID)TextureIDAssetManager.instance.load(new AssetPath(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, pack_name, page_name)), textureIDAssetParams);
        }
        this.onCreated(State.EMPTY);
        if (this.dataid != null) {
            this.addDependency(this.dataid);
        }
    }
    
    public Texture(final TextureID dataid, final String name) {
        super(null, TextureAssetManager.instance);
        this.flip = false;
        this.offsetX = 0.0f;
        this.offsetY = 0.0f;
        this.bindAlways = false;
        this.xEnd = 1.0f;
        this.yEnd = 1.0f;
        this.xStart = 0.0f;
        this.yStart = 0.0f;
        this.realWidth = 0;
        this.realHeight = 0;
        this.destroyed = false;
        this.splitX = -1;
        this.dataid = dataid;
        final TextureID dataid2 = this.dataid;
        ++dataid2.referenceCount;
        if (dataid.isReady()) {
            this.solid = this.dataid.solid;
            this.width = dataid.width;
            this.height = dataid.height;
            this.xEnd = this.width / (float)dataid.widthHW;
            this.yEnd = this.height / (float)dataid.heightHW;
        }
        else {
            assert false;
        }
        this.name = name;
        this.assetParams = null;
        this.onCreated(dataid.getState());
        this.addDependency(dataid);
    }
    
    public Texture(final String s) throws Exception {
        this(new TextureID(s), s);
        this.setUseAlphaChannel(true);
    }
    
    public Texture(final String s, final BufferedInputStream bufferedInputStream, final boolean b, final PZFileformat pzFileformat) {
        this(new TextureID(bufferedInputStream, s, b, pzFileformat), s);
        if (b && this.dataid.mask != null) {
            this.createMask(this.dataid.mask);
            this.dataid.mask = null;
            this.dataid.data = null;
        }
    }
    
    public Texture(final String s, final BufferedInputStream bufferedInputStream, final boolean b) throws Exception {
        this(new TextureID(bufferedInputStream, s, b), s);
        if (b) {
            this.createMask(this.dataid.mask);
            this.dataid.mask = null;
            this.dataid.data = null;
        }
    }
    
    public Texture(final String s, final boolean b, final boolean useAlphaChannel) throws Exception {
        this(new TextureID(s), s);
        this.setUseAlphaChannel(useAlphaChannel);
        if (b) {
            this.dataid.data = null;
        }
    }
    
    public Texture(final String s, final String s2) {
        this(new TextureID(s, s2), s);
        this.setUseAlphaChannel(true);
    }
    
    public Texture(final String s, final int[] array) {
        this(new TextureID(s, array), s);
        if (s.contains("drag")) {}
        this.setUseAlphaChannel(true);
    }
    
    public Texture(final String s, final boolean useAlphaChannel) throws Exception {
        this(new TextureID(s), s);
        this.setUseAlphaChannel(useAlphaChannel);
    }
    
    public Texture(final int n, final int n2, final String s, final int n3) {
        this(new TextureID(n, n2, n3), s);
    }
    
    public Texture(final int n, final int n2, final int n3) {
        this(new TextureID(n, n2, n3), null);
    }
    
    public Texture(final String s, final int n, final int n2, final int n3) throws Exception {
        this(new TextureID(s, n, n2, n3), s);
    }
    
    public Texture(final Texture texture) {
        this(texture.dataid, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, texture.name));
        this.width = texture.width;
        this.height = texture.height;
        this.name = texture.name;
        this.xStart = texture.xStart;
        this.yStart = texture.yStart;
        this.xEnd = texture.xEnd;
        this.yEnd = texture.yEnd;
        this.solid = texture.solid;
    }
    
    public Texture() {
        super(null, TextureAssetManager.instance);
        this.flip = false;
        this.offsetX = 0.0f;
        this.offsetY = 0.0f;
        this.bindAlways = false;
        this.xEnd = 1.0f;
        this.yEnd = 1.0f;
        this.xStart = 0.0f;
        this.yStart = 0.0f;
        this.realWidth = 0;
        this.realHeight = 0;
        this.destroyed = false;
        this.splitX = -1;
        this.assetParams = null;
        this.onCreated(State.EMPTY);
    }
    
    public static String processFilePath(String replaceAll) {
        replaceAll = replaceAll.replaceAll("\\\\", "/");
        return replaceAll;
    }
    
    public static void bindNone() {
        IndieGL.glDisable(3553);
        Texture.lastTextureID = -1;
        --Texture.BindCount;
    }
    
    public static Texture getWhite() {
        if (Texture.white == null) {
            Texture.white = new Texture(32, 32, "white", 0);
            final Object o;
            final int lastTextureID;
            int i = 0;
            final ByteBuffer byteBuffer;
            RenderThread.invokeOnRenderContext(() -> {
                Texture.white.getID();
                GL11.glBindTexture((int)o, Texture.lastTextureID = lastTextureID);
                GL11.glTexParameteri(3553, 10241, 9728);
                GL11.glTexParameteri(3553, 10240, 9728);
                MemoryUtil.memAlloc(Texture.white.width * Texture.white.height * 4);
                while (i < Texture.white.width * Texture.white.height * 4) {
                    byteBuffer.put((byte)(-1));
                    ++i;
                }
                byteBuffer.flip();
                GL11.glTexImage2D(3553, 0, 6408, Texture.white.width, Texture.white.height, 0, 6408, 5121, byteBuffer);
                MemoryUtil.memFree((Buffer)byteBuffer);
                return;
            });
            Texture.s_sharedTextureTable.put("white.png", Texture.white);
            Texture.s_sharedTextureTable.put("media/white.png", Texture.white);
            Texture.s_sharedTextureTable.put("media/ui/white.png", Texture.white);
        }
        return Texture.white;
    }
    
    public static Texture getErrorTexture() {
        if (Texture.errorTexture == null) {
            Texture.errorTexture = new Texture(32, 32, "EngineErrorTexture", 0);
            final Object o;
            final int lastTextureID;
            final int n;
            final ByteBuffer byteBuffer;
            final int n2;
            final int n4;
            final int n3;
            int i = 0;
            final int n5;
            final int n6;
            boolean b = false;
            boolean b2 = false;
            int n7;
            int j = 0;
            int k = 0;
            RenderThread.invokeOnRenderContext(() -> {
                Texture.errorTexture.getID();
                GL11.glBindTexture((int)o, Texture.lastTextureID = lastTextureID);
                GL11.glTexParameteri(3553, 10241, 9728);
                GL11.glTexParameteri(3553, 10240, 9728);
                MemoryUtil.memAlloc(Texture.errorTexture.width * Texture.errorTexture.height * n);
                byteBuffer.position(Texture.errorTexture.width * Texture.errorTexture.height * n);
                n2 = Texture.errorTexture.width * n;
                n3 = Texture.errorTexture.width / n4;
                while (i < n4 * n4) {
                    if (n5 > 0 && n6 == 0) {
                        b = (b2 = !b);
                    }
                    n7 = (b2 ? -16776961 : -1);
                    b2 = !b2;
                    while (j < n3) {
                        while (k < n3) {
                            byteBuffer.putInt((n5 * n3 + j) * n2 + (n6 * n3 + k) * n, n7);
                            ++k;
                        }
                        ++j;
                    }
                    ++i;
                }
                byteBuffer.flip();
                GL11.glTexImage2D(3553, 0, 6408, Texture.errorTexture.width, Texture.errorTexture.height, 0, 6408, 5121, byteBuffer);
                MemoryUtil.memFree((Buffer)byteBuffer);
                return;
            });
            Texture.s_sharedTextureTable.put("EngineErrorTexture.png", Texture.errorTexture);
        }
        return Texture.errorTexture;
    }
    
    private static void initEngineMipmapTextureLevel(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7) {
        final ByteBuffer memAlloc = MemoryUtil.memAlloc(n2 * n3 * 4);
        MemoryUtil.memSet(memAlloc, 255);
        for (int i = 0; i < n2 * n3; ++i) {
            memAlloc.put((byte)(n4 & 0xFF));
            memAlloc.put((byte)(n5 & 0xFF));
            memAlloc.put((byte)(n6 & 0xFF));
            memAlloc.put((byte)(n7 & 0xFF));
        }
        memAlloc.flip();
        GL11.glTexImage2D(3553, n, 6408, n2, n3, 0, 6408, 5121, memAlloc);
        MemoryUtil.memFree((Buffer)memAlloc);
    }
    
    public static Texture getEngineMipmapTexture() {
        if (Texture.mipmap == null) {
            Texture.mipmap = new Texture(256, 256, "EngineMipmapTexture", 0);
            Texture.mipmap.dataid.setMinFilter(9984);
            final Object o;
            final int lastTextureID;
            RenderThread.invokeOnRenderContext(() -> {
                Texture.mipmap.getID();
                GL11.glBindTexture((int)o, Texture.lastTextureID = lastTextureID);
                GL11.glTexParameteri(3553, 10241, 9984);
                GL11.glTexParameteri(3553, 10240, 9728);
                GL11.glTexParameteri(3553, 33085, 6);
                initEngineMipmapTextureLevel(0, Texture.mipmap.width, Texture.mipmap.height, 255, 0, 0, 255);
                initEngineMipmapTextureLevel(1, Texture.mipmap.width / 2, Texture.mipmap.height / 2, 0, 255, 0, 255);
                initEngineMipmapTextureLevel(2, Texture.mipmap.width / 4, Texture.mipmap.height / 4, 0, 0, 255, 255);
                initEngineMipmapTextureLevel(3, Texture.mipmap.width / 8, Texture.mipmap.height / 8, 255, 255, 0, 255);
                initEngineMipmapTextureLevel(4, Texture.mipmap.width / 16, Texture.mipmap.height / 16, 255, 0, 255, 255);
                initEngineMipmapTextureLevel(5, Texture.mipmap.width / 32, Texture.mipmap.height / 32, 0, 0, 0, 255);
                initEngineMipmapTextureLevel(6, Texture.mipmap.width / 64, Texture.mipmap.height / 64, 255, 255, 255, 255);
                return;
            });
        }
        return Texture.mipmap;
    }
    
    public static void clearTextures() {
        Texture.textures.clear();
    }
    
    public static Texture getSharedTexture(final String s) {
        return getSharedTexture(s, 0x0 | (TextureID.bUseCompression ? 4 : 0));
    }
    
    public static Texture getSharedTexture(final String s, final int n) {
        if (GameServer.bServer && !ServerGUI.isCreated()) {
            return null;
        }
        try {
            return getSharedTextureInternal(s, n);
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    public static Texture trygetTexture(final String s) {
        if (GameServer.bServer && !ServerGUI.isCreated()) {
            return null;
        }
        Texture sharedTexture = getSharedTexture(s);
        if (sharedTexture == null) {
            String s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            if (!s.endsWith(".png")) {
                s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s2);
            }
            sharedTexture = Texture.s_sharedTextureTable.get(s2);
            if (sharedTexture != null) {
                return sharedTexture;
            }
            final String string = ZomboidFileSystem.instance.getString(s2);
            if (!string.equals(s2)) {
                final int flags = 0x0 | (TextureID.bUseCompression ? 4 : 0);
                final TextureAssetParams textureAssetParams = new TextureAssetParams();
                textureAssetParams.flags = flags;
                sharedTexture = (Texture)TextureAssetManager.instance.load(new AssetPath(string), textureAssetParams);
                BucketManager.Shared().AddTexture(s2, sharedTexture);
                setSharedTextureInternal(s2, sharedTexture);
            }
        }
        return sharedTexture;
    }
    
    private static void onTextureFileChanged(final String s) {
        DebugLog.General.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
    }
    
    public static void onTexturePacksChanged() {
        Texture.nullTextures.clear();
        Texture.s_sharedTextureTable.clear();
    }
    
    private static void setSharedTextureInternal(final String key, final Texture value) {
        Texture.s_sharedTextureTable.put(key, value);
    }
    
    private static Texture getSharedTextureInternal(final String s, final int flags) {
        if (GameServer.bServer && !ServerGUI.isCreated()) {
            return null;
        }
        if (Texture.nullTextures.contains(s)) {
            return null;
        }
        final Texture texture = Texture.s_sharedTextureTable.get(s);
        if (texture != null) {
            return texture;
        }
        if (!s.endsWith(".txt")) {
            String substring = s;
            if (substring.endsWith(".pcx") || substring.endsWith(".png")) {
                substring = substring.substring(0, s.lastIndexOf("."));
            }
            final String substring2 = substring.substring(s.lastIndexOf("/") + 1);
            final Texture texture2 = TexturePackPage.getTexture(substring2);
            if (texture2 != null) {
                setSharedTextureInternal(s, texture2);
                return texture2;
            }
            final FileSystem.SubTexture subTexture = ((HashMap<K, FileSystem.SubTexture>)GameWindow.texturePackTextures).get(substring2);
            if (subTexture != null) {
                final TextureAssetParams textureAssetParams = new TextureAssetParams();
                textureAssetParams.subTexture = subTexture;
                final Texture texture3 = (Texture)TextureAssetManager.instance.load(new AssetPath(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, subTexture.m_pack_name, subTexture.m_page_name, subTexture.m_info.name)), textureAssetParams);
                if (texture3 == null) {
                    Texture.nullTextures.add(s);
                }
                else {
                    setSharedTextureInternal(s, texture3);
                }
                return texture3;
            }
        }
        if (TexturePackPage.subTextureMap.containsKey(s)) {
            return TexturePackPage.subTextureMap.get(s);
        }
        final FileSystem.SubTexture subTexture2 = ((HashMap<K, FileSystem.SubTexture>)GameWindow.texturePackTextures).get(s);
        if (subTexture2 != null) {
            final TextureAssetParams textureAssetParams2 = new TextureAssetParams();
            textureAssetParams2.subTexture = subTexture2;
            final Texture texture4 = (Texture)TextureAssetManager.instance.load(new AssetPath(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, subTexture2.m_pack_name, subTexture2.m_page_name, subTexture2.m_info.name)), textureAssetParams2);
            if (texture4 == null) {
                Texture.nullTextures.add(s);
            }
            else {
                setSharedTextureInternal(s, texture4);
            }
            return texture4;
        }
        if (BucketManager.Shared().HasTexture(s)) {
            final Texture texture5 = BucketManager.Shared().getTexture(s);
            setSharedTextureInternal(s, texture5);
            return texture5;
        }
        if (StringUtils.endsWithIgnoreCase(s, ".pcx")) {
            Texture.nullTextures.add(s);
            return null;
        }
        if (s.lastIndexOf(46) == -1) {
            Texture.nullTextures.add(s);
            return null;
        }
        final String string = ZomboidFileSystem.instance.getString(s);
        if (string == s && !new File(string).exists()) {
            Texture.nullTextures.add(s);
            return null;
        }
        final TextureAssetParams textureAssetParams3 = new TextureAssetParams();
        textureAssetParams3.flags = flags;
        final Texture texture6 = (Texture)TextureAssetManager.instance.load(new AssetPath(string), textureAssetParams3);
        BucketManager.Shared().AddTexture(s, texture6);
        setSharedTextureInternal(s, texture6);
        return texture6;
    }
    
    public static Texture getSharedTexture(final String s, final String s2) {
        if (BucketManager.Shared().HasTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, s2))) {
            return BucketManager.Shared().getTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, s2));
        }
        final Texture texture = new Texture(s, s2);
        BucketManager.Shared().AddTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, s2), texture);
        return texture;
    }
    
    public static Texture getSharedTexture(final String s, final int[] array, final String s2) {
        if (BucketManager.Shared().HasTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, s2))) {
            return BucketManager.Shared().getTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, s2));
        }
        final Texture texture = new Texture(s, array);
        BucketManager.Shared().AddTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, s2), texture);
        return texture;
    }
    
    public static Texture getTexture(final String s) {
        if (!s.contains(".txt")) {
            final Texture texture = TexturePackPage.getTexture(s.replace(".png", "").replace(".pcx", "").substring(s.lastIndexOf("/") + 1));
            if (texture != null) {
                return texture;
            }
        }
        if (BucketManager.Active().HasTexture(s)) {
            return BucketManager.Active().getTexture(s);
        }
        try {
            final Texture texture2 = new Texture(s);
            BucketManager.Active().AddTexture(s, texture2);
            return texture2;
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    public static Texture getSteamAvatar(final long l) {
        if (Texture.steamAvatarMap.containsKey(l)) {
            return Texture.steamAvatarMap.get(l);
        }
        final TextureID steamAvatar = TextureID.createSteamAvatar(l);
        if (steamAvatar == null) {
            return null;
        }
        final Texture value = new Texture(steamAvatar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, SteamUtils.convertSteamIDToString(l)));
        Texture.steamAvatarMap.put(l, value);
        return value;
    }
    
    public static void steamAvatarChanged(final long n) {
        if (Texture.steamAvatarMap.get(n) != null) {
            Texture.steamAvatarMap.remove(n);
        }
    }
    
    public static void forgetTexture(final String key) {
        BucketManager.Shared().forgetTexture(key);
        Texture.s_sharedTextureTable.remove(key);
    }
    
    public static void reload(final String key) {
        if (key == null || key.isEmpty()) {
            return;
        }
        Texture texture = Texture.s_sharedTextureTable.get(key);
        if (texture == null) {
            texture = Type.tryCastTo(TextureAssetManager.instance.getAssetTable().get((Object)key), Texture.class);
            if (texture == null) {
                return;
            }
        }
        texture.reloadFromFile(key);
    }
    
    public static int[] flipPixels(final int[] array, final int n, final int n2) {
        int[] array2 = null;
        if (array != null) {
            array2 = new int[n * n2];
            for (int i = 0; i < n2; ++i) {
                for (int j = 0; j < n; ++j) {
                    array2[(n2 - i - 1) * n + j] = array[i * n + j];
                }
            }
        }
        return array2;
    }
    
    public void reloadFromFile(final String pathname) {
        if (this.dataid != null) {
            final TextureID.TextureIDAssetParams textureIDAssetParams = new TextureID.TextureIDAssetParams();
            textureIDAssetParams.flags = this.dataid.flags;
            this.dataid.getAssetManager().reload(this.dataid, textureIDAssetParams);
            return;
        }
        if (pathname == null || pathname.isEmpty()) {
            return;
        }
        final File file = new File(pathname);
        if (!file.exists()) {
            return;
        }
        try {
            final ImageData imageData2 = new ImageData(file.getAbsolutePath());
            if (imageData2.getWidthHW() != this.getWidthHW() || imageData2.getHeightHW() != this.getHeightHW()) {
                return;
            }
            final int id;
            final Object o;
            RenderThread.invokeOnRenderContext(imageData2, imageData -> {
                id = this.dataid.id;
                GL11.glBindTexture((int)o, Texture.lastTextureID = id);
                GL11.glTexImage2D(3553, 0, 6408, this.getWidthHW(), this.getHeightHW(), 0, 6408, 5121, imageData.getData().getBuffer());
            });
        }
        catch (Throwable t) {
            ExceptionLogger.logException(t, pathname);
        }
    }
    
    @Override
    public void bind() {
        this.bind(3553);
    }
    
    @Override
    public void bind(final int n) {
        if (this.isDestroyed() || !this.isValid() || !this.isReady()) {
            getErrorTexture().bind(n);
            return;
        }
        if (this.bindAlways) {
            this.dataid.bindalways();
        }
        else {
            this.dataid.bind();
        }
    }
    
    public void copyMaskRegion(final Texture texture, final int n, final int n2, final int n3, final int n4) {
        if (texture.getMask() == null) {
            return;
        }
        new Mask(texture, this, n, n2, n3, n4);
    }
    
    public void createMask() {
        new Mask(this);
    }
    
    public void createMask(final boolean[] array) {
        new Mask(this, array);
    }
    
    public void createMask(final BooleanGrid booleanGrid) {
        new Mask(this, booleanGrid);
    }
    
    public void createMask(final WrappedBuffer wrappedBuffer) {
        new Mask(this, wrappedBuffer);
    }
    
    @Override
    public void destroy() {
        if (this.destroyed) {
            return;
        }
        if (this.dataid != null) {
            final TextureID dataid = this.dataid;
            if (--dataid.referenceCount == 0) {
                if (Texture.lastTextureID == this.dataid.id) {
                    Texture.lastTextureID = -1;
                }
                this.dataid.destroy();
            }
        }
        this.destroyed = true;
    }
    
    public boolean equals(final Texture texture) {
        return texture.xStart == this.xStart && texture.xEnd == this.xEnd && texture.yStart == this.yStart && texture.yEnd == this.yEnd && texture.width == this.width && texture.height == this.height && texture.solid == this.solid && (this.dataid == null || texture.dataid == null || texture.dataid.pathFileName == null || this.dataid.pathFileName == null || texture.dataid.pathFileName.equals(this.dataid.pathFileName));
    }
    
    @Override
    public WrappedBuffer getData() {
        return this.dataid.getData();
    }
    
    @Override
    public void setData(final ByteBuffer data) {
        this.dataid.setData(data);
    }
    
    @Override
    public int getHeight() {
        if (!this.isReady() && this.height <= 0 && !(this instanceof SmartTexture)) {
            this.syncReadSize();
        }
        return this.height;
    }
    
    public void setHeight(final int height) {
        this.height = height;
    }
    
    @Override
    public int getHeightHW() {
        if (!this.isReady() && this.height <= 0 && !(this instanceof SmartTexture)) {
            this.syncReadSize();
        }
        return this.dataid.heightHW;
    }
    
    public int getHeightOrig() {
        if (this.heightOrig == 0) {
            return this.getHeight();
        }
        return this.heightOrig;
    }
    
    @Override
    public int getID() {
        return this.dataid.id;
    }
    
    @Override
    public Mask getMask() {
        return this.mask;
    }
    
    @Override
    public void setMask(final Mask mask) {
        this.mask = mask;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String key) {
        if (key == null) {
            return;
        }
        if (key.equals(this.name)) {
            if (!Texture.textures.containsKey(key)) {
                Texture.textures.put(key, this);
            }
            return;
        }
        if (Texture.textures.containsKey(key)) {}
        if (Texture.textures.containsKey(this.name)) {
            Texture.textures.remove(this.name);
        }
        this.name = key;
        Texture.textures.put(key, this);
    }
    
    public TextureID getTextureId() {
        return this.dataid;
    }
    
    public boolean getUseAlphaChannel() {
        return !this.solid;
    }
    
    public void setUseAlphaChannel(final boolean b) {
        final TextureID dataid = this.dataid;
        final boolean b2 = !b;
        this.solid = b2;
        dataid.solid = b2;
    }
    
    @Override
    public int getWidth() {
        if (!this.isReady() && this.width <= 0 && !(this instanceof SmartTexture)) {
            this.syncReadSize();
        }
        return this.width;
    }
    
    public void setWidth(final int width) {
        this.width = width;
    }
    
    @Override
    public int getWidthHW() {
        if (!this.isReady() && this.width <= 0 && !(this instanceof SmartTexture)) {
            this.syncReadSize();
        }
        return this.dataid.widthHW;
    }
    
    public int getWidthOrig() {
        if (this.widthOrig == 0) {
            return this.getWidth();
        }
        return this.widthOrig;
    }
    
    @Override
    public float getXEnd() {
        return this.xEnd;
    }
    
    @Override
    public float getXStart() {
        return this.xStart;
    }
    
    @Override
    public float getYEnd() {
        return this.yEnd;
    }
    
    @Override
    public float getYStart() {
        return this.yStart;
    }
    
    public float getOffsetX() {
        return this.offsetX;
    }
    
    public void setOffsetX(final int n) {
        this.offsetX = (float)n;
    }
    
    public float getOffsetY() {
        return this.offsetY;
    }
    
    public void setOffsetY(final int n) {
        this.offsetY = (float)n;
    }
    
    public boolean isCollisionable() {
        return this.mask != null;
    }
    
    @Override
    public boolean isDestroyed() {
        return this.destroyed;
    }
    
    @Override
    public boolean isSolid() {
        return this.solid;
    }
    
    public boolean isValid() {
        return this.dataid != null;
    }
    
    @Override
    public void makeTransp(final int n, final int n2, final int n3) {
        this.setAlphaForeach(n, n2, n3, 0);
    }
    
    public void render(final float n, final float n2, final float n3, final float n4) {
        this.render(n, n2, n3, n4, 1.0f, 1.0f, 1.0f, 1.0f, null);
    }
    
    public void render(final float n, final float n2) {
        this.render(n, n2, (float)this.width, (float)this.height, 1.0f, 1.0f, 1.0f, 1.0f, null);
    }
    
    public void render(float n, float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final Consumer<TextureDraw> consumer) {
        n += this.offsetX;
        n2 += this.offsetY;
        SpriteRenderer.instance.render(this, n, n2, n3, n4, n5, n6, n7, n8, consumer);
    }
    
    public void render(final ObjectRenderEffects objectRenderEffects, final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final Consumer<TextureDraw> consumer) {
        final float n9 = this.offsetX + n;
        final float n10 = this.offsetY + n2;
        Texture.objRen.x1 = n9 + objectRenderEffects.x1 * n3;
        Texture.objRen.y1 = n10 + objectRenderEffects.y1 * n4;
        Texture.objRen.x2 = n9 + n3 + objectRenderEffects.x2 * n3;
        Texture.objRen.y2 = n10 + objectRenderEffects.y2 * n4;
        Texture.objRen.x3 = n9 + n3 + objectRenderEffects.x3 * n3;
        Texture.objRen.y3 = n10 + n4 + objectRenderEffects.y3 * n4;
        Texture.objRen.x4 = n9 + objectRenderEffects.x4 * n3;
        Texture.objRen.y4 = n10 + n4 + objectRenderEffects.y4 * n4;
        SpriteRenderer.instance.render(this, Texture.objRen.x1, Texture.objRen.y1, Texture.objRen.x2, Texture.objRen.y2, Texture.objRen.x3, Texture.objRen.y3, Texture.objRen.x4, Texture.objRen.y4, n5, n6, n7, n8, consumer);
    }
    
    public void rendershader2(float n, float n2, final float n3, final float n4, final int n5, final int n6, final int n7, final int n8, float lr, float lg, float lb, float la) {
        if (la == 0.0f) {
            return;
        }
        float n9 = n5 / (float)this.getWidthHW();
        final float n10 = n6 / (float)this.getHeightHW();
        float n11 = (n5 + n7) / (float)this.getWidthHW();
        final float n12 = (n6 + n8) / (float)this.getHeightHW();
        if (this.flip) {
            final float n13 = n11;
            n11 = n9;
            n9 = n13;
            n += this.widthOrig - this.offsetX - this.width;
            n2 += this.offsetY;
        }
        else {
            n += this.offsetX;
            n2 += this.offsetY;
        }
        if (lr > 1.0f) {
            lr = 1.0f;
        }
        if (lg > 1.0f) {
            lg = 1.0f;
        }
        if (lb > 1.0f) {
            lb = 1.0f;
        }
        if (la > 1.0f) {
            la = 1.0f;
        }
        if (lr < 0.0f) {
            lr = 0.0f;
        }
        if (lg < 0.0f) {
            lg = 0.0f;
        }
        if (lb < 0.0f) {
            lb = 0.0f;
        }
        if (la < 0.0f) {
            la = 0.0f;
        }
        if (n + n3 <= 0.0f) {
            return;
        }
        if (n2 + n4 <= 0.0f) {
            return;
        }
        if (n >= Core.getInstance().getScreenWidth()) {
            return;
        }
        if (n2 >= Core.getInstance().getScreenHeight()) {
            return;
        }
        Texture.lr = lr;
        Texture.lg = lg;
        Texture.lb = lb;
        Texture.la = la;
        SpriteRenderer.instance.render(this, n, n2, n3, n4, lr, lg, lb, la, n9, n12, n11, n12, n11, n10, n9, n10);
    }
    
    public void renderdiamond(final float n, final float n2, final float n3, final float n4, final int n5, final int n6, final int n7, final int n8) {
        SpriteRenderer.instance.render(null, n, n2, n + n3 / 2.0f, n2 - n4 / 2.0f, n + n3, n2, n + n3 / 2.0f, n2 + n4 / 2.0f, n5, n6, n7, n8);
    }
    
    public void renderwallnw(float n, float n2, final float n3, final float n4, int n5, int n6, int n7, int n8, int n9, int n10) {
        Texture.lr = -1.0f;
        Texture.lg = -1.0f;
        Texture.lb = -1.0f;
        Texture.la = -1.0f;
        if (this.flip) {
            n += this.widthOrig - this.offsetX - this.width;
            n2 += this.offsetY;
        }
        else {
            n += this.offsetX;
            n2 += this.offsetY;
        }
        final int tileScale = Core.TileScale;
        if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Walls.LightingOldDebug.getValue()) {
            n7 = (n5 = (n6 = (n8 = -65536)));
        }
        SpriteRenderer.instance.render(this, n - n3 / 2.0f - 0.0f, n2 - 96 * tileScale + n4 / 2.0f - 1.0f - 0.0f, n + 0.0f, n2 - 96 * tileScale - 2.0f - 0.0f, n + 0.0f, n2 + 4.0f + 0.0f, n - n3 / 2.0f - 0.0f, n2 + n4 / 2.0f + 4.0f + 0.0f, n8, n7, n5, n6);
        if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Walls.LightingOldDebug.getValue()) {
            n7 = (n5 = (n9 = (n10 = -256)));
        }
        SpriteRenderer.instance.render(this, n - 0.0f, n2 - 96 * tileScale - 0.0f, n + n3 / 2.0f + 0.0f, n2 - 96 * tileScale + n4 / 2.0f - 0.0f, n + n3 / 2.0f + 0.0f, n2 + n4 / 2.0f + 5.0f + 0.0f, n - 0.0f, n2 + 5.0f + 0.0f, n7, n10, n9, n5);
    }
    
    public void renderwallw(float n, float n2, final float n3, final float n4, int n5, int n6, int n7, int n8) {
        Texture.lr = -1.0f;
        Texture.lg = -1.0f;
        Texture.lb = -1.0f;
        Texture.la = -1.0f;
        if (this.flip) {
            n += this.widthOrig - this.offsetX - this.width;
            n2 += this.offsetY;
        }
        else {
            n += this.offsetX;
            n2 += this.offsetY;
        }
        if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Walls.LightingOldDebug.getValue()) {
            n6 = (n5 = -16711936);
            n8 = (n7 = -16728064);
        }
        final int tileScale = Core.TileScale;
        SpriteRenderer.instance.render(this, n - n3 / 2.0f - 0.0f, n2 - 96 * tileScale + n4 / 2.0f - 1.0f - 0.0f, n + tileScale + 0.0f, n2 - 96 * tileScale - 3.0f - 0.0f, n + tileScale + 0.0f, n2 + 3.0f + 0.0f, n - n3 / 2.0f - 0.0f, n2 + n4 / 2.0f + 4.0f + 0.0f, n8, n7, n5, n6);
    }
    
    public void renderwalln(float n, float n2, final float n3, final float n4, int n5, int n6, int n7, int n8) {
        Texture.lr = -1.0f;
        Texture.lg = -1.0f;
        Texture.lb = -1.0f;
        Texture.la = -1.0f;
        if (this.flip) {
            n += this.widthOrig - this.offsetX - this.width;
            n2 += this.offsetY;
        }
        else {
            n += this.offsetX;
            n2 += this.offsetY;
        }
        if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Walls.LightingOldDebug.getValue()) {
            n6 = (n5 = -16776961);
            n8 = (n7 = -16777024);
        }
        final int tileScale = Core.TileScale;
        SpriteRenderer.instance.render(this, n - 6.0f - 0.0f, n2 - 96 * tileScale - 3.0f - 0.0f, n + n3 / 2.0f + 0.0f, n2 - 96 * tileScale + n4 / 2.0f - 0.0f, n + n3 / 2.0f + 0.0f, n2 + n4 / 2.0f + 5.0f + 0.0f, n - 6.0f - 0.0f, n2 + 2.0f + 0.0f, n7, n8, n6, n5);
    }
    
    public void renderstrip(int n, int n2, final int n3, final int n4, float n5, float n6, float n7, float n8, final Consumer<TextureDraw> consumer) {
        try {
            if (n8 <= 0.0f) {
                return;
            }
            if (n5 > 1.0f) {
                n5 = 1.0f;
            }
            if (n6 > 1.0f) {
                n6 = 1.0f;
            }
            if (n7 > 1.0f) {
                n7 = 1.0f;
            }
            if (n8 > 1.0f) {
                n8 = 1.0f;
            }
            if (n5 < 0.0f) {
                n5 = 0.0f;
            }
            if (n6 < 0.0f) {
                n6 = 0.0f;
            }
            if (n7 < 0.0f) {
                n7 = 0.0f;
            }
            if (n8 < 0.0f) {
                n8 = 0.0f;
            }
            this.getXStart();
            this.getYStart();
            this.getXEnd();
            this.getYEnd();
            if (this.flip) {
                n += (int)(this.widthOrig - this.offsetX - this.width);
                n2 += (int)this.offsetY;
            }
            else {
                n += (int)this.offsetX;
                n2 += (int)this.offsetY;
            }
            SpriteRenderer.instance.renderi(this, n, n2, n3, n4, n5, n6, n7, n8, consumer);
        }
        catch (Exception thrown) {
            Texture.bDoingQuad = false;
            Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, null, thrown);
        }
    }
    
    @Override
    public void setAlphaForeach(final int n, final int n2, final int n3, final int n4) {
        final ImageData imageData = this.getTextureId().getImageData();
        if (imageData != null) {
            imageData.makeTransp((byte)n, (byte)n2, (byte)n3, (byte)n4);
        }
        else {
            final WrappedBuffer data = this.getData();
            this.setData(ImageUtils.makeTransp(data.getBuffer(), n, n2, n3, n4, this.getWidthHW(), this.getHeightHW()));
            data.dispose();
        }
        final AlphaColorIndex alphaColorIndex = new AlphaColorIndex(n, n2, n3, n4);
        if (this.dataid.alphaList == null) {
            this.dataid.alphaList = new ArrayList<AlphaColorIndex>();
        }
        if (!this.dataid.alphaList.contains(alphaColorIndex)) {
            this.dataid.alphaList.add(alphaColorIndex);
        }
    }
    
    public void setCustomizedTexture() {
        this.dataid.pathFileName = null;
    }
    
    public void setNameOnly(final String name) {
        this.name = name;
    }
    
    @Override
    public void setRegion(final int n, final int n2, int width, int height) {
        if (n < 0 || n > this.getWidthHW()) {
            return;
        }
        if (n2 < 0 || n2 > this.getHeightHW()) {
            return;
        }
        if (width <= 0) {
            return;
        }
        if (height <= 0) {
            return;
        }
        if (width + n > this.getWidthHW()) {
            width = this.getWidthHW() - n;
        }
        if (height > this.getHeightHW()) {
            height = this.getHeightHW() - n2;
        }
        this.xStart = n / (float)this.getWidthHW();
        this.yStart = n2 / (float)this.getHeightHW();
        this.xEnd = (n + width) / (float)this.getWidthHW();
        this.yEnd = (n2 + height) / (float)this.getHeightHW();
        this.width = width;
        this.height = height;
    }
    
    public Texture splitIcon() {
        if (this.splitIconTex == null) {
            if (!this.dataid.isReady()) {
                this.splitIconTex = new Texture();
                this.splitIconTex.name = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.name);
                this.splitIconTex.dataid = this.dataid;
                final TextureID dataid = this.splitIconTex.dataid;
                ++dataid.referenceCount;
                this.splitIconTex.splitX = this.splitX;
                this.splitIconTex.splitY = this.splitY;
                this.splitIconTex.splitW = this.splitW;
                this.splitIconTex.splitH = this.splitH;
                this.splitIconTex.width = this.width;
                this.splitIconTex.height = this.height;
                this.splitIconTex.offsetX = 0.0f;
                this.splitIconTex.offsetY = 0.0f;
                this.splitIconTex.widthOrig = 0;
                this.splitIconTex.heightOrig = 0;
                this.splitIconTex.addDependency(this.dataid);
                setSharedTextureInternal(this.splitIconTex.name, this.splitIconTex);
                return this.splitIconTex;
            }
            this.splitIconTex = new Texture(this.getTextureId(), invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.name));
            final float n = this.xStart * this.getWidthHW();
            final float n2 = this.yStart * this.getHeightHW();
            this.splitIconTex.setRegion((int)n, (int)n2, (int)(this.xEnd * this.getWidthHW() - n), (int)(this.yEnd * this.getHeightHW() - n2));
            this.splitIconTex.offsetX = 0.0f;
            this.splitIconTex.offsetY = 0.0f;
            setSharedTextureInternal(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.name), this.splitIconTex);
        }
        return this.splitIconTex;
    }
    
    public Texture split(final int splitX, final int splitY, final int splitW, final int splitH) {
        final Texture texture = new Texture(this.getTextureId(), invokedynamic(makeConcatWithConstants:(Ljava/lang/String;II)Ljava/lang/String;, this.name, splitX, splitY));
        this.splitX = splitX;
        this.splitY = splitY;
        this.splitW = splitW;
        this.splitH = splitH;
        if (this.getTextureId().isReady()) {
            texture.setRegion(splitX, splitY, splitW, splitH);
        }
        else {
            assert false;
        }
        return texture;
    }
    
    public Texture split(final String s, final int n, final int n2, final int n3, final int n4) {
        final Texture texture = new Texture(this.getTextureId(), s);
        texture.setRegion(n, n2, n3, n4);
        return texture;
    }
    
    public Texture[] split(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8) {
        final Texture[] array = new Texture[n3 * n4];
        for (int i = 0; i < n3; ++i) {
            for (int j = 0; j < n4; ++j) {
                (array[j + i * n4] = new Texture(this.getTextureId(), invokedynamic(makeConcatWithConstants:(Ljava/lang/String;II)Ljava/lang/String;, this.name, n3, n4))).setRegion(n + j * n5 + n7 * j, n2 + i * n6 + n8 * i, n5, n6);
                array[j + i * n4].copyMaskRegion(this, n + j * n5 + n7 * j, n2 + i * n6 + n8 * i, n5, n6);
            }
        }
        return array;
    }
    
    public Texture[][] split2D(final int[] array, final int[] array2) {
        if (array == null || array2 == null) {
            return null;
        }
        final Texture[][] array3 = new Texture[array.length][array2.length];
        float yStart;
        float n = yStart = 0.0f;
        for (int i = 0; i < array2.length; ++i) {
            yStart += n;
            n = array2[i] / (float)this.getHeightHW();
            float xStart = 0.0f;
            for (int j = 0; j < array.length; ++j) {
                final float n2 = array[j] / (float)this.getWidthHW();
                final Texture[] array4 = array3[j];
                final int n3 = i;
                final Texture texture = new Texture(this);
                array4[n3] = texture;
                final Texture texture2 = texture;
                texture2.width = array[j];
                texture2.height = array2[i];
                texture2.xStart = xStart;
                xStart = (texture2.xEnd = xStart + n2);
                texture2.yStart = yStart;
                texture2.yEnd = yStart + n;
            }
        }
        return array3;
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;II)Ljava/lang/String;, this.getClass().getSimpleName(), this.name, this.getWidth(), this.getHeight());
    }
    
    public void saveMask(final String s) {
        this.mask.save(s);
    }
    
    public void save(final String pathname) {
        GL11.glPixelStorei(3333, 1);
        final int width = this.getWidth();
        final int height = this.getHeight();
        final ByteBuffer buffer = this.getData().getBuffer();
        final int[] rgbArray = new int[width * height];
        final File output = new File(pathname);
        for (int i = 0; i < rgbArray.length; ++i) {
            final int n = i * 4;
            rgbArray[i] = ((buffer.get(n + 3) & 0xFF) << 24 | (buffer.get(n) & 0xFF) << 16 | (buffer.get(n + 1) & 0xFF) << 8 | (buffer.get(n + 2) & 0xFF) << 0);
        }
        final BufferedImage im = new BufferedImage(width, height, 2);
        im.setRGB(0, 0, width, height, rgbArray, 0, width);
        try {
            ImageIO.write(im, "png", output);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        GL13.glActiveTexture(33984);
    }
    
    public void loadMaskRegion(final ByteBuffer byteBuffer) {
        if (byteBuffer == null) {
            return;
        }
        this.mask = new Mask();
        (this.mask.mask = new BooleanGrid(this.width, this.height)).LoadFromByteBuffer(byteBuffer);
    }
    
    public void saveMaskRegion(final ByteBuffer byteBuffer) {
        if (byteBuffer == null) {
            return;
        }
        this.mask.mask.PutToByteBuffer(byteBuffer);
    }
    
    public int getRealWidth() {
        return this.realWidth;
    }
    
    public void setRealWidth(final int realWidth) {
        this.realWidth = realWidth;
    }
    
    public int getRealHeight() {
        return this.realHeight;
    }
    
    public void setRealHeight(final int realHeight) {
        this.realHeight = realHeight;
    }
    
    public Vector2 getUVScale(final Vector2 vector2) {
        vector2.set(1.0f, 1.0f);
        if (this.dataid == null) {
            return vector2;
        }
        if (this.dataid.heightHW != this.dataid.height || this.dataid.widthHW != this.dataid.width) {
            vector2.x = this.dataid.width / (float)this.dataid.widthHW;
            vector2.y = this.dataid.height / (float)this.dataid.heightHW;
        }
        return vector2;
    }
    
    private void syncReadSize() {
        final PNGSize pngSize = Texture.pngSize.get();
        pngSize.readSize(this.name);
        this.width = pngSize.width;
        this.height = pngSize.height;
    }
    
    @Override
    public AssetType getType() {
        return Texture.ASSET_TYPE;
    }
    
    public void onBeforeReady() {
        if (this.assetParams != null) {
            this.assetParams.subTexture = null;
            this.assetParams = null;
        }
        this.solid = this.dataid.solid;
        if (this.splitX == -1) {
            this.width = this.dataid.width;
            this.height = this.dataid.height;
            this.xEnd = this.width / (float)this.dataid.widthHW;
            this.yEnd = this.height / (float)this.dataid.heightHW;
            if (this.dataid.mask != null) {
                this.createMask(this.dataid.mask);
            }
        }
        else {
            this.setRegion(this.splitX, this.splitY, this.splitW, this.splitH);
            if (this.dataid.mask != null) {
                this.mask = new Mask(this.dataid.mask, this.dataid.width, this.dataid.height, this.splitX, this.splitY, this.splitW, this.splitH);
            }
        }
    }
    
    public static void collectAllIcons(final HashMap<String, String> hashMap, final HashMap<String, String> hashMap2) {
        for (final Map.Entry<String, Texture> entry : Texture.s_sharedTextureTable.entrySet()) {
            if (entry.getKey().startsWith("media/ui/Container_") || entry.getKey().startsWith("Item_")) {
                String value = "";
                if (entry.getKey().startsWith("Item_")) {
                    value = entry.getKey().replaceFirst("Item_", "");
                }
                else if (entry.getKey().startsWith("media/ui/Container_")) {
                    value = entry.getKey().replaceFirst("media/ui/Container_", "").replaceAll("\\.png", "");
                    DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, value.toLowerCase(), (String)entry.getKey()));
                }
                hashMap.put(value.toLowerCase(), value);
                hashMap2.put(value.toLowerCase(), entry.getKey());
            }
        }
    }
    
    static {
        nullTextures = new HashSet<String>();
        objRen = ObjectRenderEffects.alloc();
        Texture.BindCount = 0;
        Texture.bDoingQuad = false;
        Texture.lastlastTextureID = -2;
        Texture.totalTextureID = 0;
        Texture.white = null;
        Texture.errorTexture = null;
        Texture.mipmap = null;
        Texture.lastTextureID = -1;
        Texture.WarnFailFindTexture = true;
        textures = new HashMap<String, Texture>();
        s_sharedTextureTable = new HashMap<String, Texture>();
        steamAvatarMap = new HashMap<Long, Texture>();
        pngSize = ThreadLocal.withInitial((Supplier<? extends PNGSize>)PNGSize::new);
        ASSET_TYPE = new AssetType("Texture");
    }
    
    public enum PZFileformat
    {
        PNG, 
        DDS;
        
        private static /* synthetic */ PZFileformat[] $values() {
            return new PZFileformat[] { PZFileformat.PNG, PZFileformat.DDS };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    public static final class TextureAssetParams extends AssetManager.AssetParams
    {
        int flags;
        FileSystem.SubTexture subTexture;
        
        public TextureAssetParams() {
            this.flags = 0;
        }
    }
}

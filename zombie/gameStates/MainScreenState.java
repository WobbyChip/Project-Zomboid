// 
// Decompiled by Procyon v0.5.36
// 

package zombie.gameStates;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import org.lwjgl.system.Struct;
import javax.imageio.ImageIO;
import java.util.Locale;
import zombie.core.logger.ExceptionLogger;
import zombie.DebugFileWatcher;
import zombie.core.BoxedStaticValues;
import zombie.input.JoypadManager;
import zombie.modding.ActiveMods;
import zombie.ui.UIFont;
import zombie.ui.TextManager;
import zombie.IndieGL;
import zombie.ui.UIManager;
import zombie.GameTime;
import zombie.Lua.LuaEventManager;
import zombie.SoundManager;
import zombie.core.Color;
import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;
import zombie.core.SpriteRenderer;
import zombie.asset.AssetManagers;
import org.lwjglx.opengl.OpenGLException;
import zombie.worldMap.WorldMapData;
import zombie.worldMap.WorldMapDataAssetManager;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureAssetManager;
import zombie.core.textures.TextureID;
import zombie.core.textures.TextureIDAssetManager;
import zombie.core.skinnedmodel.model.Model;
import zombie.core.skinnedmodel.model.ModelAssetManager;
import zombie.core.skinnedmodel.model.ModelMesh;
import zombie.core.skinnedmodel.model.MeshAssetManager;
import zombie.core.skinnedmodel.population.ClothingItem;
import zombie.core.skinnedmodel.population.ClothingItemAssetManager;
import zombie.core.skinnedmodel.advancedanimation.AnimNodeAsset;
import zombie.core.skinnedmodel.advancedanimation.AnimNodeAssetManager;
import zombie.core.skinnedmodel.model.AnimationAsset;
import zombie.core.skinnedmodel.model.AnimationAssetManager;
import zombie.core.skinnedmodel.model.AiSceneAsset;
import zombie.core.skinnedmodel.model.AiSceneAssetManager;
import zombie.GameWindow;
import zombie.core.opengl.RenderThread;
import zombie.core.Translator;
import zombie.debug.DebugType;
import zombie.core.raknet.VoiceManager;
import zombie.characters.IsoPlayer;
import zombie.core.Rand;
import org.lwjglx.opengl.Display;
import zombie.core.Core;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import zombie.core.skinnedmodel.model.jassimp.JAssImpImporter;
import zombie.core.logger.LoggerManager;
import zombie.debug.DebugLog;
import java.io.FileNotFoundException;
import zombie.core.ProxyPrintStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.File;
import zombie.core.logger.ZipLogs;
import zombie.ZomboidFileSystem;
import java.nio.ByteBuffer;
import org.lwjgl.glfw.GLFWImage;
import zombie.worldMap.UIWorldMap;
import java.util.ArrayList;
import fmod.fmod.Audio;

public final class MainScreenState extends GameState
{
    public static String Version;
    public static Audio ambient;
    public static float totalScale;
    public float alpha;
    public float alphaStep;
    private int RestartDebounceClickTimer;
    public final ArrayList<ScreenElement> Elements;
    public float targetAlpha;
    int lastH;
    int lastW;
    ScreenElement Logo;
    public static MainScreenState instance;
    public boolean showLogo;
    private float FadeAlpha;
    public boolean lightningTimelineMarker;
    float lightningTime;
    public UIWorldMap m_worldMap;
    public float lightningDelta;
    public float lightningTargetDelta;
    public float lightningFullTimer;
    public float lightningCount;
    public float lightOffCount;
    private ConnectToServerState connectToServerState;
    private static GLFWImage windowIcon1;
    private static GLFWImage windowIcon2;
    private static ByteBuffer windowIconBB1;
    private static ByteBuffer windowIconBB2;
    
    public MainScreenState() {
        this.alpha = 1.0f;
        this.alphaStep = 0.03f;
        this.RestartDebounceClickTimer = 10;
        this.Elements = new ArrayList<ScreenElement>(16);
        this.targetAlpha = 1.0f;
        this.showLogo = false;
        this.FadeAlpha = 0.0f;
        this.lightningTimelineMarker = false;
        this.lightningTime = 0.0f;
        this.lightningDelta = 0.0f;
        this.lightningTargetDelta = 0.0f;
        this.lightningFullTimer = 0.0f;
        this.lightningCount = 0.0f;
        this.lightOffCount = 0.0f;
    }
    
    public static void main(final String[] array) {
        for (int i = 0; i < array.length; ++i) {
            if (array[i] != null) {
                if (array[i].startsWith("-cachedir=")) {
                    ZomboidFileSystem.instance.setCacheDir(array[i].replace("-cachedir=", "").trim());
                }
            }
        }
        ZipLogs.addZipFile(false);
        try {
            final PrintStream printStream = new PrintStream(new FileOutputStream(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator)), true);
            System.setOut(new ProxyPrintStream(System.out, printStream));
            System.setErr(new ProxyPrintStream(System.err, printStream));
        }
        catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        DebugLog.init();
        LoggerManager.init();
        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir()));
        JAssImpImporter.Init();
        System.out.println(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Calendar.getInstance().getTime()));
        System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir()));
        System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, LoggerManager.getLogsDir()));
        printSpecs();
        System.getProperties().list(System.out);
        System.out.println("-----");
        System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Core.getInstance().getVersionNumber()));
        DebugLog.General.println("svnRevision=%s date=%s time=%s", "", "", "");
        Display.setIcon(loadIcons());
        Rand.init();
        for (int j = 0; j < array.length; ++j) {
            if (array[j] != null) {
                if (array[j].contains("safemode")) {
                    Core.SafeMode = true;
                    Core.SafeModeForced = true;
                }
                else if (array[j].equals("-nosound")) {
                    Core.SoundDisabled = true;
                }
                else if (array[j].equals("-aitest")) {
                    IsoPlayer.isTestAIMode = true;
                }
                else if (array[j].equals("-novoip")) {
                    VoiceManager.VoipDisabled = true;
                }
                else if (array[j].equals("-debug")) {
                    Core.bDebug = true;
                }
                else if (array[j].startsWith("-debuglog=")) {
                    for (final String s : array[j].replace("-debuglog=", "").split(",")) {
                        try {
                            final char char1 = s.charAt(0);
                            DebugLog.setLogEnabled(DebugType.valueOf((char1 == '+' || char1 == '-') ? s.substring(1) : s), char1 != '-');
                        }
                        catch (IllegalArgumentException ex4) {}
                    }
                }
                else if (!array[j].startsWith("-cachedir=")) {
                    if (array[j].equals("+connect")) {
                        if (j + 1 < array.length) {
                            System.setProperty("args.server.connect", array[j + 1]);
                        }
                        ++j;
                    }
                    else if (array[j].equals("+password")) {
                        if (j + 1 < array.length) {
                            System.setProperty("args.server.password", array[j + 1]);
                        }
                        ++j;
                    }
                    else if (array[j].contains("-debugtranslation")) {
                        Translator.debug = true;
                    }
                    else if ("-modfolders".equals(array[j])) {
                        if (j + 1 < array.length) {
                            ZomboidFileSystem.instance.setModFoldersOrder(array[j + 1]);
                        }
                        ++j;
                    }
                    else if (array[j].equals("-nosteam")) {
                        System.setProperty("zomboid.steam", "0");
                    }
                    else {
                        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, array[j]));
                    }
                }
            }
        }
        try {
            RenderThread.init();
            final AssetManagers assetManagers = GameWindow.assetManagers;
            AiSceneAssetManager.instance.create(AiSceneAsset.ASSET_TYPE, assetManagers);
            AnimationAssetManager.instance.create(AnimationAsset.ASSET_TYPE, assetManagers);
            AnimNodeAssetManager.instance.create(AnimNodeAsset.ASSET_TYPE, assetManagers);
            ClothingItemAssetManager.instance.create(ClothingItem.ASSET_TYPE, assetManagers);
            MeshAssetManager.instance.create(ModelMesh.ASSET_TYPE, assetManagers);
            ModelAssetManager.instance.create(Model.ASSET_TYPE, assetManagers);
            TextureIDAssetManager.instance.create(TextureID.ASSET_TYPE, assetManagers);
            TextureAssetManager.instance.create(Texture.ASSET_TYPE, assetManagers);
            WorldMapDataAssetManager.instance.create(WorldMapData.ASSET_TYPE, assetManagers);
            GameWindow.InitGameThread();
            RenderThread.renderLoop();
        }
        catch (OpenGLException ex2) {
            new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator)).delete();
            ex2.printStackTrace();
        }
        catch (Exception ex3) {
            ex3.printStackTrace();
        }
    }
    
    public static void DrawTexture(final Texture texture, final int n, final int n2, final int n3, final int n4, final float n5) {
        SpriteRenderer.instance.renderi(texture, n, n2, n3, n4, 1.0f, 1.0f, 1.0f, n5, null);
    }
    
    public static void DrawTexture(final Texture texture, final int n, final int n2, final int n3, final int n4, final Color color) {
        SpriteRenderer.instance.renderi(texture, n, n2, n3, n4, color.r, color.g, color.b, color.a, null);
    }
    
    @Override
    public void enter() {
        DebugLog.log("EXITDEBUG: MainScreenState.enter 1");
        this.Elements.clear();
        this.targetAlpha = 1.0f;
        TextureID.UseFiltering = true;
        this.RestartDebounceClickTimer = 100;
        MainScreenState.totalScale = Core.getInstance().getOffscreenHeight(0) / 1080.0f;
        this.lastW = Core.getInstance().getOffscreenWidth(0);
        this.lastH = Core.getInstance().getOffscreenHeight(0);
        this.alpha = 1.0f;
        this.showLogo = false;
        SoundManager.instance.setMusicState("MainMenu");
        final ScreenElement screenElement = new ScreenElement(Texture.getSharedTexture("media/ui/PZ_Logo.png"), Core.getInstance().getOffscreenWidth(0) / 2 - (int)(Texture.getSharedTexture("media/ui/PZ_Logo.png").getWidth() * MainScreenState.totalScale) / 2, (int)(Core.getInstance().getOffscreenHeight(0) * 0.7f) - (int)(350.0f * MainScreenState.totalScale), 0.0f, 0.0f, 1);
        screenElement.targetAlpha = 1.0f;
        final ScreenElement screenElement2 = screenElement;
        screenElement2.alphaStep *= 0.9f;
        this.Logo = screenElement;
        this.Elements.add(screenElement);
        TextureID.UseFiltering = false;
        LuaEventManager.triggerEvent("OnMainMenuEnter");
        MainScreenState.instance = this;
        final float n = TextureID.totalMemUsed / 1024.0f / 1024.0f;
        DebugLog.log("EXITDEBUG: MainScreenState.enter 2");
    }
    
    public static MainScreenState getInstance() {
        return MainScreenState.instance;
    }
    
    public boolean ShouldShowLogo() {
        return this.showLogo;
    }
    
    @Override
    public void exit() {
        DebugLog.log("EXITDEBUG: MainScreenState.exit 1");
        DebugLog.log(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Texture.totalTextureID));
        final float musicVolume = Core.getInstance().getOptionMusicVolume() / 10.0f;
        final long timeInMillis = Calendar.getInstance().getTimeInMillis();
        while (true) {
            this.FadeAlpha = Math.min(1.0f, (Calendar.getInstance().getTimeInMillis() - timeInMillis) / 250.0f);
            this.render();
            if (this.FadeAlpha >= 1.0f) {
                break;
            }
            try {
                Thread.sleep(33L);
            }
            catch (Exception ex) {}
            SoundManager.instance.Update();
        }
        SoundManager.instance.stopMusic("");
        SoundManager.instance.setMusicVolume(musicVolume);
        DebugLog.log("EXITDEBUG: MainScreenState.exit 2");
    }
    
    @Override
    public void render() {
        this.lightningTime += 1.0f * GameTime.instance.getMultipliedSecondsSinceLastUpdate();
        Core.getInstance().StartFrame();
        Core.getInstance().EndFrame();
        final boolean useUIFBO = UIManager.useUIFBO;
        UIManager.useUIFBO = false;
        Core.getInstance().StartFrameUI();
        IndieGL.glBlendFunc(770, 771);
        SpriteRenderer.instance.renderi(null, 0, 0, Core.getInstance().getScreenWidth(), Core.getInstance().getScreenHeight(), 0.0f, 0.0f, 0.0f, 1.0f, null);
        IndieGL.glBlendFunc(770, 770);
        if (this.lightningTargetDelta == 0.0f && this.lightningDelta != 0.0f && this.lightningDelta < 0.6f && this.lightningCount == 0.0f) {
            this.lightningTargetDelta = 1.0f;
            this.lightningCount = 1.0f;
        }
        if (this.lightningTimelineMarker) {
            this.lightningTimelineMarker = false;
            this.lightningTargetDelta = 1.0f;
        }
        if (this.lightningTargetDelta == 1.0f && this.lightningDelta == 1.0f && ((this.lightningFullTimer > 1.0f && this.lightningCount == 0.0f) || this.lightningFullTimer > 10.0f)) {
            this.lightningTargetDelta = 0.0f;
            this.lightningFullTimer = 0.0f;
        }
        if (this.lightningTargetDelta == 1.0f && this.lightningDelta == 1.0f) {
            this.lightningFullTimer += GameTime.getInstance().getMultiplier();
        }
        if (this.lightningDelta != this.lightningTargetDelta) {
            if (this.lightningDelta < this.lightningTargetDelta) {
                this.lightningDelta += 0.17f * GameTime.getInstance().getMultiplier();
                if (this.lightningDelta > this.lightningTargetDelta) {
                    this.lightningDelta = this.lightningTargetDelta;
                    if (this.lightningDelta == 1.0f) {
                        this.showLogo = true;
                    }
                }
            }
            if (this.lightningDelta > this.lightningTargetDelta) {
                this.lightningDelta -= 0.025f * GameTime.getInstance().getMultiplier();
                if (this.lightningCount == 0.0f) {
                    this.lightningDelta -= 0.1f;
                }
                if (this.lightningDelta < this.lightningTargetDelta) {
                    this.lightningDelta = this.lightningTargetDelta;
                    this.lightningCount = 0.0f;
                }
            }
        }
        final Texture sharedTexture = Texture.getSharedTexture("media/ui/Title.png");
        final Texture sharedTexture2 = Texture.getSharedTexture("media/ui/Title2.png");
        final Texture sharedTexture3 = Texture.getSharedTexture("media/ui/Title3.png");
        final Texture sharedTexture4 = Texture.getSharedTexture("media/ui/Title4.png");
        if (Rand.Next(150) == 0) {
            this.lightOffCount = 10.0f;
        }
        final Texture sharedTexture5 = Texture.getSharedTexture("media/ui/Title_lightning.png");
        final Texture sharedTexture6 = Texture.getSharedTexture("media/ui/Title_lightning2.png");
        final Texture sharedTexture7 = Texture.getSharedTexture("media/ui/Title_lightning3.png");
        final Texture sharedTexture8 = Texture.getSharedTexture("media/ui/Title_lightning4.png");
        final float n = Core.getInstance().getScreenHeight() / 1080.0f;
        final float n2 = sharedTexture.getWidth() * n;
        float n3 = Core.getInstance().getScreenWidth() - (n2 + sharedTexture2.getWidth() * n);
        if (n3 >= 0.0f) {
            n3 = 0.0f;
        }
        final float n4 = 1.0f - this.lightningDelta * 0.6f;
        final float n5 = 1024.0f * n;
        DrawTexture(sharedTexture, (int)n3, 0, (int)n2, (int)n5, n4);
        DrawTexture(sharedTexture2, (int)n3 + (int)n2, 0, (int)n2, (int)n5, n4);
        DrawTexture(sharedTexture3, (int)n3, (int)n5, (int)n2, (int)(sharedTexture3.getHeight() * n), n4);
        DrawTexture(sharedTexture4, (int)n3 + (int)n2, (int)n5, (int)n2, (int)(sharedTexture3.getHeight() * n), n4);
        IndieGL.glBlendFunc(770, 1);
        DrawTexture(sharedTexture5, (int)n3, 0, (int)n2, (int)n5, this.lightningDelta);
        DrawTexture(sharedTexture6, (int)n3 + (int)n2, 0, (int)n2, (int)n5, this.lightningDelta);
        DrawTexture(sharedTexture7, (int)n3, (int)n5, (int)n2, (int)n5, this.lightningDelta);
        DrawTexture(sharedTexture8, (int)n3 + (int)n2, (int)n5, (int)n2, (int)n5, this.lightningDelta);
        IndieGL.glBlendFunc(770, 771);
        UIManager.render();
        if (GameWindow.DrawReloadingLua) {
            final int n6 = TextManager.instance.MeasureStringX(UIFont.Small, "Reloading Lua") + 32;
            final int lineHeight = TextManager.instance.font.getLineHeight();
            final int n7 = (int)Math.ceil(lineHeight * 1.5);
            SpriteRenderer.instance.renderi(null, Core.getInstance().getScreenWidth() - n6 - 12, 12, n6, n7, 0.0f, 0.5f, 0.75f, 1.0f, null);
            TextManager.instance.DrawStringCentre(Core.getInstance().getScreenWidth() - n6 / 2 - 12, 12 + (n7 - lineHeight) / 2, "Reloading Lua", 1.0, 1.0, 1.0, 1.0);
        }
        if (this.FadeAlpha > 0.0f) {
            UIManager.DrawTexture(UIManager.getBlack(), 0.0, 0.0, Core.getInstance().getScreenWidth(), Core.getInstance().getScreenHeight(), this.FadeAlpha);
        }
        ActiveMods.renderUI();
        JoypadManager.instance.renderUI();
        Core.getInstance().EndFrameUI();
        UIManager.useUIFBO = useUIFBO;
    }
    
    @Override
    public GameStateMachine.StateAction update() {
        if (this.connectToServerState != null && this.connectToServerState.update() == GameStateMachine.StateAction.Continue) {
            this.connectToServerState.exit();
            this.connectToServerState = null;
            return GameStateMachine.StateAction.Remain;
        }
        LuaEventManager.triggerEvent("OnFETick", BoxedStaticValues.toDouble(0.0));
        if (this.RestartDebounceClickTimer > 0) {
            --this.RestartDebounceClickTimer;
        }
        for (int i = 0; i < this.Elements.size(); ++i) {
            this.Elements.get(i).update();
        }
        this.lastW = Core.getInstance().getOffscreenWidth(0);
        this.lastH = Core.getInstance().getOffscreenHeight(0);
        DebugFileWatcher.instance.update();
        ZomboidFileSystem.instance.update();
        try {
            Core.getInstance().CheckDelayResetLua();
        }
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
        }
        return GameStateMachine.StateAction.Remain;
    }
    
    public void setConnectToServerState(final ConnectToServerState connectToServerState) {
        this.connectToServerState = connectToServerState;
    }
    
    @Override
    public GameState redirectState() {
        return null;
    }
    
    public static GLFWImage.Buffer loadIcons() {
        GLFWImage.Buffer buffer = null;
        final String upperCase = System.getProperty("os.name").toUpperCase(Locale.ENGLISH);
        if (upperCase.contains("WIN")) {
            try {
                buffer = GLFWImage.create(2);
                buffer.put(0, (Struct)(MainScreenState.windowIcon1 = GLFWImage.create().set(16, 16, MainScreenState.windowIconBB1 = loadInstance(ImageIO.read(new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, File.separator, File.separator)).getAbsoluteFile()), 16))));
                buffer.put(1, (Struct)(MainScreenState.windowIcon2 = GLFWImage.create().set(32, 32, MainScreenState.windowIconBB2 = loadInstance(ImageIO.read(new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, File.separator, File.separator)).getAbsoluteFile()), 32))));
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        else if (upperCase.contains("MAC")) {
            try {
                buffer = GLFWImage.create(1);
                buffer.put(0, (Struct)(MainScreenState.windowIcon1 = GLFWImage.create().set(128, 128, MainScreenState.windowIconBB1 = loadInstance(ImageIO.read(new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, File.separator, File.separator)).getAbsoluteFile()), 128))));
            }
            catch (IOException ex2) {
                ex2.printStackTrace();
            }
        }
        else {
            try {
                buffer = GLFWImage.create(1);
                buffer.put(0, (Struct)(MainScreenState.windowIcon1 = GLFWImage.create().set(32, 32, MainScreenState.windowIconBB1 = loadInstance(ImageIO.read(new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, File.separator, File.separator)).getAbsoluteFile()), 32))));
            }
            catch (IOException ex3) {
                ex3.printStackTrace();
            }
        }
        return buffer;
    }
    
    private static ByteBuffer loadInstance(final BufferedImage bufferedImage, final int n) {
        final BufferedImage bufferedImage2 = new BufferedImage(n, n, 3);
        final Graphics2D graphics = bufferedImage2.createGraphics();
        final double iconRatio = getIconRatio(bufferedImage, bufferedImage2);
        final double n2 = bufferedImage.getWidth() * iconRatio;
        final double n3 = bufferedImage.getHeight() * iconRatio;
        graphics.drawImage(bufferedImage, (int)((bufferedImage2.getWidth() - n2) / 2.0), (int)((bufferedImage2.getHeight() - n3) / 2.0), (int)n2, (int)n3, null);
        graphics.dispose();
        return convertToByteBuffer(bufferedImage2);
    }
    
    private static double getIconRatio(final BufferedImage bufferedImage, final BufferedImage bufferedImage2) {
        double n;
        if (bufferedImage.getWidth() > bufferedImage2.getWidth()) {
            n = bufferedImage2.getWidth() / (double)bufferedImage.getWidth();
        }
        else {
            n = bufferedImage2.getWidth() / bufferedImage.getWidth();
        }
        if (bufferedImage.getHeight() > bufferedImage2.getHeight()) {
            final double n2 = bufferedImage2.getHeight() / (double)bufferedImage.getHeight();
            if (n2 < n) {
                n = n2;
            }
        }
        else {
            final double n3 = bufferedImage2.getHeight() / bufferedImage.getHeight();
            if (n3 < n) {
                n = n3;
            }
        }
        return n;
    }
    
    public static ByteBuffer convertToByteBuffer(final BufferedImage bufferedImage) {
        final byte[] src = new byte[bufferedImage.getWidth() * bufferedImage.getHeight() * 4];
        int n = 0;
        for (int i = 0; i < bufferedImage.getHeight(); ++i) {
            for (int j = 0; j < bufferedImage.getWidth(); ++j) {
                final int rgb = bufferedImage.getRGB(j, i);
                src[n + 0] = (byte)(rgb << 8 >> 24);
                src[n + 1] = (byte)(rgb << 16 >> 24);
                src[n + 2] = (byte)(rgb << 24 >> 24);
                src[n + 3] = (byte)(rgb >> 24);
                n += 4;
            }
        }
        final ByteBuffer allocateDirect = ByteBuffer.allocateDirect(src.length);
        allocateDirect.put(src);
        allocateDirect.flip();
        return allocateDirect;
    }
    
    private static void printSpecs() {
        try {
            System.out.println("===== System specs =====");
            final long n = 1024L * 1024L;
            final long n2 = n * 1024L;
            final Map<String, String> getenv = System.getenv();
            System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, System.getProperty("os.name"), System.getProperty("os.version"), System.getProperty("os.arch")));
            if (getenv.containsKey("PROCESSOR_IDENTIFIER")) {
                System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (String)getenv.get("PROCESSOR_IDENTIFIER")));
            }
            if (getenv.containsKey("NUMBER_OF_PROCESSORS")) {
                System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (String)getenv.get("NUMBER_OF_PROCESSORS")));
            }
            System.out.println(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Runtime.getRuntime().availableProcessors()));
            System.out.println(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, Runtime.getRuntime().freeMemory() / (float)n));
            final long maxMemory = Runtime.getRuntime().maxMemory();
            System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/io/Serializable;)Ljava/lang/String;, (maxMemory == Long.MAX_VALUE) ? "no limit" : Float.valueOf(maxMemory / (float)n)));
            System.out.println(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, Runtime.getRuntime().totalMemory() / (float)n));
            for (final File file : File.listRoots()) {
                System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;FF)Ljava/lang/String;, file.getAbsolutePath(), file.getTotalSpace() / (float)n2, file.getFreeSpace() / (float)n2));
            }
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, wmic("baseboard", new String[] { "Product" })));
                System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, wmic("cpu", new String[] { "Manufacturer", "MaxClockSpeed", "Name" })));
                System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, wmic("path Win32_videocontroller", new String[] { "AdapterRAM", "DriverVersion", "Name" })));
                System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, wmic("path Win32_videocontroller", new String[] { "VideoModeDescription" })));
                System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, wmic("path Win32_sounddevice", new String[] { "Manufacturer", "Name" })));
                System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, wmic("memorychip", new String[] { "Capacity", "Manufacturer" })));
            }
            System.out.println("========================");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private static String wmic(final String s, final String[] array) {
        try {
            String s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            for (int i = 0; i < array.length; ++i) {
                s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, array[i]);
                if (i < array.length - 1) {
                    s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s2);
                }
            }
            final Process exec = Runtime.getRuntime().exec(new String[] { "CMD", "/C", s2 });
            exec.getOutputStream().close();
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
            String replaceAll = "";
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                replaceAll = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, replaceAll, line);
            }
            for (int length = array.length, j = 0; j < length; ++j) {
                replaceAll = replaceAll.replaceAll(array[j], "");
            }
            final String[] split = replaceAll.trim().replaceAll(" ( )+", "=").split("=");
            String s5;
            if (split.length > array.length) {
                String s3 = "{ ";
                for (int n = split.length / array.length, k = 0; k < n; ++k) {
                    String s4 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s3);
                    for (int l = 0; l < array.length; ++l) {
                        s4 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s4, array[l], split[k * array.length + l]);
                        if (l < array.length - 1) {
                            s4 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s4);
                        }
                    }
                    s3 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s4);
                    if (k < n - 1) {
                        s3 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s3);
                    }
                }
                s5 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s3);
            }
            else {
                String s6 = "[";
                for (int n2 = 0; n2 < split.length; ++n2) {
                    s6 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s6, array[n2], split[n2]);
                    if (n2 < split.length - 1) {
                        s6 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s6);
                    }
                }
                s5 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s6);
            }
            return s5;
        }
        catch (Exception ex) {
            return "Couldnt get info...";
        }
    }
    
    static {
        MainScreenState.Version = "RC 3";
        MainScreenState.totalScale = 1.0f;
    }
    
    public class Credit
    {
        public int disappearDelay;
        public Texture name;
        public float nameAlpha;
        public float nameAppearDelay;
        public float nameTargetAlpha;
        public Texture title;
        public float titleAlpha;
        public float titleTargetAlpha;
        
        public Credit(final Texture title, final Texture name) {
            this.disappearDelay = 200;
            this.nameAppearDelay = 40.0f;
            this.titleTargetAlpha = 1.0f;
            this.titleAlpha = 0.0f;
            this.nameTargetAlpha = 0.0f;
            this.nameAlpha = 0.0f;
            this.title = title;
            this.name = name;
        }
    }
    
    public static class ScreenElement
    {
        public float alpha;
        public float alphaStep;
        public boolean jumpBack;
        public float sx;
        public float sy;
        public float targetAlpha;
        public Texture tex;
        public int TicksTillTargetAlpha;
        public float x;
        public int xCount;
        public float xVel;
        public float xVelO;
        public float y;
        public float yVel;
        public float yVelO;
        
        public ScreenElement(final Texture tex, final int n, final int n2, final float xVel, final float yVel, final int xCount) {
            this.alpha = 0.0f;
            this.alphaStep = 0.2f;
            this.jumpBack = true;
            this.sx = 0.0f;
            this.sy = 0.0f;
            this.targetAlpha = 0.0f;
            this.TicksTillTargetAlpha = 0;
            this.x = 0.0f;
            this.xCount = 1;
            this.xVel = 0.0f;
            this.xVelO = 0.0f;
            this.y = 0.0f;
            this.yVel = 0.0f;
            this.yVelO = 0.0f;
            final float n3 = (float)n;
            this.sx = n3;
            this.x = n3;
            final float n4 = n2 - tex.getHeight() * MainScreenState.totalScale;
            this.sy = n4;
            this.y = n4;
            this.xVel = xVel;
            this.yVel = yVel;
            this.tex = tex;
            this.xCount = xCount;
        }
        
        public void render() {
            int n = (int)this.x;
            final int n2 = (int)this.y;
            for (int i = 0; i < this.xCount; ++i) {
                MainScreenState.DrawTexture(this.tex, n, n2, (int)(this.tex.getWidth() * MainScreenState.totalScale), (int)(this.tex.getHeight() * MainScreenState.totalScale), this.alpha);
                n += (int)(this.tex.getWidth() * MainScreenState.totalScale);
            }
            TextManager.instance.DrawStringRight((double)(Core.getInstance().getOffscreenWidth(0) - 5), (double)(Core.getInstance().getOffscreenHeight(0) - 15), invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, MainScreenState.Version), 1.0, 1.0, 1.0, 1.0);
        }
        
        public void setY(final float n) {
            final float n2 = n - this.tex.getHeight() * MainScreenState.totalScale;
            this.sy = n2;
            this.y = n2;
        }
        
        public void update() {
            this.x += this.xVel * MainScreenState.totalScale;
            this.y += this.yVel * MainScreenState.totalScale;
            --this.TicksTillTargetAlpha;
            if (this.TicksTillTargetAlpha <= 0) {
                this.targetAlpha = 1.0f;
            }
            if (this.jumpBack && this.sx - this.x > this.tex.getWidth() * MainScreenState.totalScale) {
                this.x += this.tex.getWidth() * MainScreenState.totalScale;
            }
            if (this.alpha < this.targetAlpha) {
                this.alpha += this.alphaStep;
                if (this.alpha > this.targetAlpha) {
                    this.alpha = this.targetAlpha;
                }
            }
            else if (this.alpha > this.targetAlpha) {
                this.alpha -= this.alphaStep;
                if (this.alpha < this.targetAlpha) {
                    this.alpha = this.targetAlpha;
                }
            }
        }
    }
}

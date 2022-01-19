// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core;

import zombie.scripting.objects.Item;
import zombie.ZomboidGlobals;
import zombie.ui.TextManager;
import zombie.core.skinnedmodel.ModelManager;
import zombie.worldMap.WorldMap;
import zombie.SandboxOptions;
import zombie.sandbox.CustomSandboxOptions;
import zombie.characters.skills.PerkFactory;
import zombie.characters.skills.CustomPerks;
import zombie.Lua.LuaHookManager;
import zombie.iso.TileOverlays;
import zombie.iso.BrokenFences;
import zombie.iso.BentFences;
import zombie.iso.ContainerOverlays;
import zombie.characters.WornItems.BodyLocations;
import zombie.characters.AttachedItems.AttachedLocations;
import zombie.gameStates.ChooseGameInfo;
import zombie.characters.traits.TraitFactory;
import zombie.characters.professions.ProfessionFactory;
import zombie.characters.SurvivorFactory;
import zombie.Lua.MapObjects;
import zombie.vehicles.VehicleType;
import zombie.core.skinnedmodel.advancedanimation.AnimationSet;
import zombie.core.skinnedmodel.population.OutfitManager;
import zombie.core.skinnedmodel.population.HairStyles;
import zombie.core.skinnedmodel.population.BeardStyles;
import zombie.core.skinnedmodel.population.ClothingDecals;
import zombie.scripting.ScriptManager;
import zombie.util.StringUtils;
import java.util.Arrays;
import zombie.gameStates.IngameState;
import org.lwjglx.input.Controller;
import zombie.ui.ObjectTooltip;
import fmod.FMOD_DriverInfo;
import fmod.javafmod;
import zombie.core.raknet.VoiceManager;
import zombie.SoundManager;
import zombie.GameSounds;
import zombie.iso.PlayerCamera;
import zombie.core.sprite.SpriteRenderState;
import zombie.core.opengl.PZGLUtil;
import org.lwjgl.util.glu.GLU;
import org.lwjglx.opengl.OpenGLException;
import zombie.debug.DebugOptions;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL32;
import zombie.core.VBO.GLVertexBufferObject;
import org.lwjglx.opengl.PixelFormat;
import zombie.GameWindow;
import zombie.core.znet.SteamUtils;
import zombie.Lua.LuaEventManager;
import zombie.input.GameKeyboard;
import zombie.network.GameClient;
import org.lwjglx.input.Keyboard;
import org.lwjgl.glfw.GLFWVidMode;
import zombie.debug.DebugLog;
import org.lwjgl.glfw.GLFW;
import se.krka.kahlua.vm.KahluaTable;
import java.io.FileWriter;
import java.nio.channels.ReadableByteChannel;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Iterator;
import org.lwjglx.opengl.DisplayMode;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.CopyOption;
import java.util.ArrayList;
import zombie.modding.ActiveMods;
import zombie.input.JoypadManager;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import org.lwjglx.LWJGLException;
import zombie.network.GameServer;
import zombie.characters.IsoPlayer;
import zombie.input.Mouse;
import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;
import zombie.ui.FPSGraph;
import zombie.core.logger.ExceptionLogger;
import zombie.ui.UIManager;
import zombie.GameTime;
import zombie.IndieGL;
import zombie.Lua.LuaManager;
import zombie.core.textures.TextureFBO;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import org.lwjglx.opengl.Display;
import java.nio.ByteBuffer;
import zombie.core.textures.Texture;
import java.io.File;
import java.awt.image.RenderedImage;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.Buffer;
import zombie.ZomboidFileSystem;
import org.lwjgl.system.MemoryUtil;
import zombie.core.math.PZMath;
import zombie.iso.IsoCamera;
import org.lwjgl.opengl.GL11;
import zombie.iso.IsoWater;
import zombie.iso.IsoPuddles;
import zombie.core.opengl.RenderThread;
import zombie.iso.weather.WeatherShader;
import java.io.IOException;
import zombie.MovingObjectUpdateScheduler;
import org.joml.Matrix4f;
import java.util.Map;
import zombie.core.opengl.Shader;
import zombie.ui.UITextBox2;
import java.util.HashMap;
import zombie.core.textures.MultiTextureFBO2;
import zombie.core.textures.ColorInfo;

public final class Core
{
    public static final boolean bDemo = false;
    public static boolean bTutorial;
    private static boolean fakefullscreen;
    private static final GameVersion gameVersion;
    public String steamServerVersion;
    public static boolean bMultithreadedRendering;
    public static boolean bAltMoveMethod;
    private boolean rosewoodSpawnDone;
    private final ColorInfo objectHighlitedColor;
    private boolean flashIsoCursor;
    private int isoCursorVisibility;
    public static boolean OptionShowCursorWhileAiming;
    private boolean collideZombies;
    public final MultiTextureFBO2 OffscreenBuffer;
    private String saveFolder;
    public static boolean OptionZoom;
    public static boolean OptionModsEnabled;
    public static int OptionFontSize;
    public static String OptionContextMenuFont;
    public static String OptionInventoryFont;
    public static String OptionTooltipFont;
    public static String OptionMeasurementFormat;
    public static int OptionClockFormat;
    public static int OptionClockSize;
    public static boolean OptionClock24Hour;
    public static boolean OptionVSync;
    public static int OptionSoundVolume;
    public static int OptionMusicVolume;
    public static int OptionAmbientVolume;
    public static int OptionMusicLibrary;
    public static boolean OptionVoiceEnable;
    public static int OptionVoiceMode;
    public static int OptionVoiceVADMode;
    public static String OptionVoiceRecordDeviceName;
    public static int OptionVoiceVolumeMic;
    public static int OptionVoiceVolumePlayers;
    public static int OptionVehicleEngineVolume;
    public static int OptionReloadDifficulty;
    public static boolean OptionRackProgress;
    public static int OptionBloodDecals;
    public static boolean OptionBorderlessWindow;
    public static boolean OptionLockCursorToWindow;
    public static boolean OptionTextureCompression;
    public static boolean OptionModelTextureMipmaps;
    public static boolean OptionTexture2x;
    private static String OptionZoomLevels1x;
    private static String OptionZoomLevels2x;
    public static boolean OptionEnableContentTranslations;
    public static boolean OptionUIFBO;
    public static int OptionUIRenderFPS;
    public static boolean OptionRadialMenuKeyToggle;
    public static boolean OptionReloadRadialInstant;
    public static boolean OptionPanCameraWhileAiming;
    public static boolean OptionPanCameraWhileDriving;
    public static boolean OptionShowChatTimestamp;
    public static boolean OptionShowChatTitle;
    public static String OptionChatFontSize;
    public static float OptionMinChatOpaque;
    public static float OptionMaxChatOpaque;
    public static float OptionChatFadeTime;
    public static boolean OptionChatOpaqueOnFocus;
    public static boolean OptionTemperatureDisplayCelsius;
    public static boolean OptionDoWindSpriteEffects;
    public static boolean OptionDoDoorSpriteEffects;
    public static boolean OptionRenderPrecipIndoors;
    public static boolean OptionAutoProneAtk;
    public static boolean Option3DGroundItem;
    public static int OptionRenderPrecipitation;
    public static boolean OptionUpdateSneakButton;
    public static boolean OptiondblTapJogToSprint;
    private static int OptionAimOutline;
    private static String OptionCycleContainerKey;
    private static boolean OptionDropItemsOnSquareCenter;
    private static boolean OptionTimedActionGameSpeedReset;
    private static int OptionShoulderButtonContainerSwitch;
    private static boolean OptionProgressBar;
    private static String OptionLanguageName;
    private static final boolean[] OptionSingleContextMenu;
    private static boolean OptionCorpseShadows;
    private static int OptionSimpleClothingTextures;
    private static boolean OptionSimpleWeaponTextures;
    private static boolean OptionAutoDrink;
    private static boolean OptionLeaveKeyInIgnition;
    private static int OptionSearchModeOverlayEffect;
    private static int OptionIgnoreProneZombieRange;
    private boolean showPing;
    private boolean forceSnow;
    private boolean zombieGroupSound;
    private String blinkingMoodle;
    private boolean tutorialDone;
    private boolean vehiclesWarningShow;
    private String poisonousBerry;
    private String poisonousMushroom;
    private boolean doneNewSaveFolder;
    private static String difficulty;
    public static int TileScale;
    private boolean isSelectingAll;
    private boolean showYourUsername;
    private ColorInfo mpTextColor;
    private boolean isAzerty;
    private String seenUpdateText;
    private boolean toggleToAim;
    private boolean toggleToRun;
    private boolean toggleToSprint;
    private boolean celsius;
    private boolean riversideDone;
    private boolean noSave;
    private boolean showFirstTimeVehicleTutorial;
    private boolean showFirstTimeWeatherTutorial;
    private boolean showFirstTimeSneakTutorial;
    private boolean newReloading;
    private boolean gotNewBelt;
    private boolean bAnimPopupDone;
    private boolean bModsPopupDone;
    public static float blinkAlpha;
    public static boolean blinkAlphaIncrease;
    private static HashMap<String, Object> optionsOnStartup;
    private boolean bChallenge;
    public static int width;
    public static int height;
    public static int MaxJukeBoxesActive;
    public static int NumJukeBoxesActive;
    public static String GameMode;
    private static String glVersion;
    private static int glMajorVersion;
    private static Core core;
    public static boolean bDebug;
    public static UITextBox2 CurrentTextEntryBox;
    public Shader RenderShader;
    private Map<String, Integer> keyMaps;
    public final boolean bUseShaders = true;
    private int iPerfSkybox;
    private int iPerfSkybox_new;
    public static final int iPerfSkybox_High = 0;
    public static final int iPerfSkybox_Medium = 1;
    public static final int iPerfSkybox_Static = 2;
    private int iPerfPuddles;
    private int iPerfPuddles_new;
    public static final int iPerfPuddles_None = 3;
    public static final int iPerfPuddles_GroundOnly = 2;
    public static final int iPerfPuddles_GroundWithRuts = 1;
    public static final int iPerfPuddles_All = 0;
    private boolean bPerfReflections;
    private boolean bPerfReflections_new;
    public int vidMem;
    private boolean bSupportsFBO;
    public float UIRenderAccumulator;
    public boolean UIRenderThisFrame;
    public int version;
    public int fileversion;
    private static boolean fullScreen;
    private static final boolean[] bAutoZoom;
    public static String GameMap;
    public static String GameSaveWorld;
    public static boolean SafeMode;
    public static boolean SafeModeForced;
    public static boolean SoundDisabled;
    public int frameStage;
    private int stack;
    public static int xx;
    public static int yy;
    public static int zz;
    public final HashMap<Integer, Float> FloatParamMap;
    private final Matrix4f tempMatrix4f;
    private static final float isoAngle = 62.65607f;
    private static final float scale = 0.047085002f;
    public static boolean bLastStand;
    public static String ChallengeID;
    public static boolean bLoadedWithMultithreaded;
    public static boolean bExiting;
    private String m_delayResetLua_activeMods;
    private String m_delayResetLua_reason;
    
    public Core() {
        this.steamServerVersion = "1.0.0.0";
        this.rosewoodSpawnDone = false;
        this.objectHighlitedColor = new ColorInfo(0.98f, 0.56f, 0.11f, 1.0f);
        this.flashIsoCursor = false;
        this.isoCursorVisibility = 5;
        this.collideZombies = true;
        this.OffscreenBuffer = new MultiTextureFBO2();
        this.saveFolder = null;
        this.showPing = true;
        this.forceSnow = false;
        this.zombieGroupSound = true;
        this.blinkingMoodle = null;
        this.tutorialDone = false;
        this.vehiclesWarningShow = false;
        this.poisonousBerry = null;
        this.poisonousMushroom = null;
        this.doneNewSaveFolder = false;
        this.isSelectingAll = false;
        this.showYourUsername = true;
        this.mpTextColor = null;
        this.isAzerty = false;
        this.seenUpdateText = "";
        this.toggleToAim = false;
        this.toggleToRun = false;
        this.toggleToSprint = true;
        this.celsius = false;
        this.riversideDone = false;
        this.noSave = false;
        this.showFirstTimeVehicleTutorial = false;
        this.showFirstTimeWeatherTutorial = false;
        this.showFirstTimeSneakTutorial = true;
        this.newReloading = true;
        this.gotNewBelt = false;
        this.bAnimPopupDone = false;
        this.bModsPopupDone = false;
        this.keyMaps = null;
        this.iPerfSkybox = 1;
        this.iPerfSkybox_new = 1;
        this.iPerfPuddles = 0;
        this.iPerfPuddles_new = 0;
        this.bPerfReflections = true;
        this.bPerfReflections_new = true;
        this.vidMem = 3;
        this.bSupportsFBO = true;
        this.UIRenderAccumulator = 0.0f;
        this.UIRenderThisFrame = true;
        this.version = 1;
        this.fileversion = 7;
        this.frameStage = 0;
        this.stack = 0;
        this.FloatParamMap = new HashMap<Integer, Float>();
        this.tempMatrix4f = new Matrix4f();
        this.m_delayResetLua_activeMods = null;
        this.m_delayResetLua_reason = null;
    }
    
    public boolean isMultiThread() {
        return Core.bMultithreadedRendering;
    }
    
    public void setChallenge(final boolean bChallenge) {
        this.bChallenge = bChallenge;
    }
    
    public boolean isChallenge() {
        return this.bChallenge;
    }
    
    public String getChallengeID() {
        return Core.ChallengeID;
    }
    
    public boolean getOptionTieredZombieUpdates() {
        return MovingObjectUpdateScheduler.instance.isEnabled();
    }
    
    public void setOptionTieredZombieUpdates(final boolean enabled) {
        MovingObjectUpdateScheduler.instance.setEnabled(enabled);
    }
    
    public void setFramerate(final int n) {
        PerformanceSettings.setUncappedFPS(n == 1);
        switch (n) {
            case 1: {
                PerformanceSettings.setLockFPS(60);
                break;
            }
            case 2: {
                PerformanceSettings.setLockFPS(244);
                break;
            }
            case 3: {
                PerformanceSettings.setLockFPS(240);
                break;
            }
            case 4: {
                PerformanceSettings.setLockFPS(165);
                break;
            }
            case 5: {
                PerformanceSettings.setLockFPS(120);
                break;
            }
            case 6: {
                PerformanceSettings.setLockFPS(95);
                break;
            }
            case 7: {
                PerformanceSettings.setLockFPS(90);
                break;
            }
            case 8: {
                PerformanceSettings.setLockFPS(75);
                break;
            }
            case 9: {
                PerformanceSettings.setLockFPS(60);
                break;
            }
            case 10: {
                PerformanceSettings.setLockFPS(55);
                break;
            }
            case 11: {
                PerformanceSettings.setLockFPS(45);
                break;
            }
            case 12: {
                PerformanceSettings.setLockFPS(30);
                break;
            }
            case 13: {
                PerformanceSettings.setLockFPS(24);
                break;
            }
        }
    }
    
    public void setMultiThread(final boolean bMultithreadedRendering) {
        Core.bMultithreadedRendering = bMultithreadedRendering;
        try {
            this.saveOptions();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public boolean loadedShader() {
        return this.RenderShader != null;
    }
    
    public static int getGLMajorVersion() {
        if (Core.glMajorVersion == -1) {
            getOpenGLVersions();
        }
        return Core.glMajorVersion;
    }
    
    public boolean getUseShaders() {
        return true;
    }
    
    public int getPerfSkybox() {
        return this.iPerfSkybox_new;
    }
    
    public int getPerfSkyboxOnLoad() {
        return this.iPerfSkybox;
    }
    
    public void setPerfSkybox(final int iPerfSkybox_new) {
        this.iPerfSkybox_new = iPerfSkybox_new;
    }
    
    public boolean getPerfReflections() {
        return this.bPerfReflections_new;
    }
    
    public boolean getPerfReflectionsOnLoad() {
        return this.bPerfReflections;
    }
    
    public void setPerfReflections(final boolean bPerfReflections_new) {
        this.bPerfReflections_new = bPerfReflections_new;
    }
    
    public int getPerfPuddles() {
        return this.iPerfPuddles_new;
    }
    
    public int getPerfPuddlesOnLoad() {
        return this.iPerfPuddles;
    }
    
    public void setPerfPuddles(final int iPerfPuddles_new) {
        this.iPerfPuddles_new = iPerfPuddles_new;
    }
    
    public int getVidMem() {
        if (Core.SafeMode) {
            return 5;
        }
        return this.vidMem;
    }
    
    public void setVidMem(final int vidMem) {
        if (Core.SafeMode) {
            this.vidMem = 5;
        }
        this.vidMem = vidMem;
        try {
            this.saveOptions();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void setUseShaders(final boolean b) {
    }
    
    public void shadersOptionChanged() {
        RenderThread.invokeOnRenderContext(() -> {
            if (!Core.SafeModeForced) {
                try {
                    if (this.RenderShader == null) {
                        this.RenderShader = new WeatherShader("screen");
                    }
                    if (this.RenderShader != null && !this.RenderShader.isCompiled()) {
                        this.RenderShader = null;
                    }
                }
                catch (Exception ex2) {
                    this.RenderShader = null;
                }
            }
            else if (this.RenderShader != null) {
                try {
                    this.RenderShader.destroy();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
                this.RenderShader = null;
            }
        });
    }
    
    public void initShaders() {
        try {
            if (this.RenderShader == null && !Core.SafeMode && !Core.SafeModeForced) {
                RenderThread.invokeOnRenderContext(() -> this.RenderShader = new WeatherShader("screen"));
            }
            if (this.RenderShader == null || !this.RenderShader.isCompiled()) {
                this.RenderShader = null;
            }
        }
        catch (Exception ex) {
            this.RenderShader = null;
            ex.printStackTrace();
        }
        IsoPuddles.getInstance();
        IsoWater.getInstance();
    }
    
    public static String getGLVersion() {
        if (Core.glVersion == null) {
            getOpenGLVersions();
        }
        return Core.glVersion;
    }
    
    public String getGameMode() {
        return Core.GameMode;
    }
    
    public static Core getInstance() {
        return Core.core;
    }
    
    public static void getOpenGLVersions() {
        Core.glVersion = GL11.glGetString(7938);
        Core.glMajorVersion = Core.glVersion.charAt(0) - '0';
    }
    
    public boolean getDebug() {
        return Core.bDebug;
    }
    
    public static void setFullScreen(final boolean fullScreen) {
        Core.fullScreen = fullScreen;
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
    
    public void TakeScreenshot() {
        this.TakeScreenshot(256, 256, 1028);
    }
    
    public void TakeScreenshot(int min, int min2, final int n) {
        final int n2 = 0;
        final int screenWidth = IsoCamera.getScreenWidth(n2);
        final int screenHeight = IsoCamera.getScreenHeight(n2);
        min = PZMath.min(min, screenWidth);
        min2 = PZMath.min(min2, screenHeight);
        this.TakeScreenshot(IsoCamera.getScreenLeft(n2) + screenWidth / 2 - min / 2, IsoCamera.getScreenTop(n2) + screenHeight / 2 - min2 / 2, min, min2, n);
    }
    
    public void TakeScreenshot(final int n, final int n2, final int n3, final int n4, final int n5) {
        GL11.glPixelStorei(3333, 1);
        GL11.glReadBuffer(n5);
        final ByteBuffer memAlloc = MemoryUtil.memAlloc(n3 * n4 * 3);
        GL11.glReadPixels(n, n2, n3, n4, 6407, 5121, memAlloc);
        final int[] array = new int[n3 * n4];
        final File fileInCurrentSave = ZomboidFileSystem.instance.getFileInCurrentSave("thumb.png");
        for (int i = 0; i < array.length; ++i) {
            final int n6 = i * 3;
            array[i] = (0xFF000000 | (memAlloc.get(n6) & 0xFF) << 16 | (memAlloc.get(n6 + 1) & 0xFF) << 8 | (memAlloc.get(n6 + 2) & 0xFF) << 0);
        }
        MemoryUtil.memFree((Buffer)memAlloc);
        final int[] flipPixels = flipPixels(array, n3, n4);
        final BufferedImage im = new BufferedImage(n3, n4, 2);
        im.setRGB(0, 0, n3, n4, flipPixels, 0, n3);
        try {
            ImageIO.write(im, "png", fileInCurrentSave);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        Texture.reload(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getGameModeCacheDir(), Core.GameSaveWorld, File.separator));
    }
    
    public void TakeFullScreenshot(final String s2) {
        final int n;
        final int n2;
        final int n3;
        final int n4;
        final ByteBuffer byteBuffer;
        final int[] array;
        final File output;
        int i = 0;
        final int n5;
        final BufferedImage im;
        final int[] rgbArray;
        RenderThread.invokeOnRenderContext(s2, s -> {
            GL11.glPixelStorei(3333, 1);
            GL11.glReadBuffer(1028);
            Display.getDisplayMode().getWidth();
            Display.getDisplayMode().getHeight();
            MemoryUtil.memAlloc(n * n2 * 3);
            GL11.glReadPixels(n3, n4, n, n2, 6407, 5121, byteBuffer);
            array = new int[n * n2];
            if (s == null) {
                s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss").format(Calendar.getInstance().getTime()));
            }
            output = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getScreenshotDir(), File.separator, s));
            while (i < array.length) {
                array[i] = (0xFF000000 | (byteBuffer.get(n5) & 0xFF) << 16 | (byteBuffer.get(n5 + 1) & 0xFF) << 8 | (byteBuffer.get(n5 + 2) & 0xFF) << 0);
                ++i;
            }
            MemoryUtil.memFree((Buffer)byteBuffer);
            flipPixels(array, n, n2);
            im = new BufferedImage(n, n2, 2);
            im.setRGB(0, 0, n, n2, rgbArray, 0, n);
            try {
                ImageIO.write(im, "png", output);
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }
    
    public static boolean supportNPTTexture() {
        return false;
    }
    
    public boolean supportsFBO() {
        if (Core.SafeMode) {
            return this.OffscreenBuffer.bZoomEnabled = false;
        }
        if (!this.bSupportsFBO) {
            return false;
        }
        if (this.OffscreenBuffer.Current != null) {
            return true;
        }
        try {
            if (TextureFBO.checkFBOSupport() && this.setupMultiFBO()) {
                return true;
            }
            this.bSupportsFBO = false;
            Core.SafeMode = true;
            return this.OffscreenBuffer.bZoomEnabled = false;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            this.bSupportsFBO = false;
            Core.SafeMode = true;
            return this.OffscreenBuffer.bZoomEnabled = false;
        }
    }
    
    private void sharedInit() {
        this.supportsFBO();
    }
    
    public void MoveMethodToggle() {
        Core.bAltMoveMethod = !Core.bAltMoveMethod;
    }
    
    public void EndFrameText(final int n) {
        if (LuaManager.thread.bStep) {
            return;
        }
        if (this.OffscreenBuffer.Current != null) {}
        IndieGL.glDoEndFrame();
        this.frameStage = 2;
    }
    
    public void EndFrame(final int n) {
        if (LuaManager.thread.bStep) {
            return;
        }
        if (this.OffscreenBuffer.Current != null) {
            SpriteRenderer.instance.glBuffer(0, n);
        }
        IndieGL.glDoEndFrame();
        this.frameStage = 2;
    }
    
    public void EndFrame() {
        IndieGL.glDoEndFrame();
        if (this.OffscreenBuffer.Current != null) {
            SpriteRenderer.instance.glBuffer(0, 0);
        }
    }
    
    public void EndFrameUI() {
        if (!Core.blinkAlphaIncrease) {
            Core.blinkAlpha -= 0.07f * (GameTime.getInstance().getMultiplier() / 1.6f);
            if (Core.blinkAlpha < 0.15f) {
                Core.blinkAlpha = 0.15f;
                Core.blinkAlphaIncrease = true;
            }
        }
        else {
            Core.blinkAlpha += 0.07f * (GameTime.getInstance().getMultiplier() / 1.6f);
            if (Core.blinkAlpha > 1.0f) {
                Core.blinkAlpha = 1.0f;
                Core.blinkAlphaIncrease = false;
            }
        }
        if (UIManager.useUIFBO && UIManager.UIFBO == null) {
            UIManager.CreateFBO(Core.width, Core.height);
        }
        if (LuaManager.thread != null && LuaManager.thread.bStep) {
            SpriteRenderer.instance.clearSprites();
            return;
        }
        ExceptionLogger.render();
        if (UIManager.useUIFBO && this.UIRenderThisFrame) {
            SpriteRenderer.instance.glBuffer(3, 0);
            IndieGL.glDoEndFrame();
            SpriteRenderer.instance.stopOffscreenUI();
            IndieGL.glDoStartFrame(Core.width, Core.height, 1.0f, -1);
            final float n = (int)(1.0f / Core.OptionUIRenderFPS * 100.0f) / 100.0f;
            this.UIRenderAccumulator -= (int)(this.UIRenderAccumulator / n) * n;
            if (FPSGraph.instance != null) {
                FPSGraph.instance.addUI(System.currentTimeMillis());
            }
        }
        if (UIManager.useUIFBO) {
            SpriteRenderer.instance.setDoAdditive(true);
            SpriteRenderer.instance.renderi((Texture)UIManager.UIFBO.getTexture(), 0, Core.height, Core.width, -Core.height, 1.0f, 1.0f, 1.0f, 1.0f, null);
            SpriteRenderer.instance.setDoAdditive(false);
        }
        if (getInstance().getOptionLockCursorToWindow()) {
            Mouse.renderCursorTexture();
        }
        IndieGL.glDoEndFrame();
        RenderThread.Ready();
        this.frameStage = 0;
    }
    
    public static void UnfocusActiveTextEntryBox() {
        if (Core.CurrentTextEntryBox != null && !Core.CurrentTextEntryBox.getUIName().contains("chat text entry")) {
            Core.CurrentTextEntryBox.DoingTextEntry = false;
            if (Core.CurrentTextEntryBox.Frame != null) {
                Core.CurrentTextEntryBox.Frame.Colour = Core.CurrentTextEntryBox.StandardFrameColour;
            }
            Core.CurrentTextEntryBox = null;
        }
    }
    
    public int getOffscreenWidth(final int n) {
        if (this.OffscreenBuffer != null) {
            return this.OffscreenBuffer.getWidth(n);
        }
        if (IsoPlayer.numPlayers > 1) {
            return this.getScreenWidth() / 2;
        }
        return this.getScreenWidth();
    }
    
    public int getOffscreenHeight(final int n) {
        if (this.OffscreenBuffer != null) {
            return this.OffscreenBuffer.getHeight(n);
        }
        if (IsoPlayer.numPlayers > 2) {
            return this.getScreenHeight() / 2;
        }
        return this.getScreenHeight();
    }
    
    public int getOffscreenTrueWidth() {
        if (this.OffscreenBuffer == null || this.OffscreenBuffer.Current == null) {
            return this.getScreenWidth();
        }
        return this.OffscreenBuffer.getTexture(0).getWidth();
    }
    
    public int getOffscreenTrueHeight() {
        if (this.OffscreenBuffer == null || this.OffscreenBuffer.Current == null) {
            return this.getScreenHeight();
        }
        return this.OffscreenBuffer.getTexture(0).getHeight();
    }
    
    public int getScreenHeight() {
        return Core.height;
    }
    
    public int getScreenWidth() {
        return Core.width;
    }
    
    public void setResolutionAndFullScreen(final int n, final int n2, final boolean b) {
        setDisplayMode(n, n2, b);
        this.setScreenSize(Display.getWidth(), Display.getHeight());
    }
    
    public void setResolution(final String s) {
        final String[] split = s.split("x");
        final int int1 = Integer.parseInt(split[0].trim());
        final int int2 = Integer.parseInt(split[1].trim());
        if (Core.fullScreen) {
            setDisplayMode(int1, int2, true);
        }
        else {
            setDisplayMode(int1, int2, false);
        }
        this.setScreenSize(Display.getWidth(), Display.getHeight());
        try {
            this.saveOptions();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public boolean loadOptions() throws IOException {
        final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator));
        if (!file.exists()) {
            this.saveFolder = getMyDocumentFolder();
            new File(this.saveFolder).mkdir();
            this.copyPasteFolders("mods");
            this.setOptionLanguageName(System.getProperty("user.language").toUpperCase());
            if (Translator.getAzertyMap().contains(Translator.getLanguage().name())) {
                this.setAzerty(true);
            }
            if (!GameServer.bServer) {
                try {
                    int width = 0;
                    int height = 0;
                    final DisplayMode[] availableDisplayModes = Display.getAvailableDisplayModes();
                    for (int i = 0; i < availableDisplayModes.length; ++i) {
                        if (availableDisplayModes[i].getWidth() > width && availableDisplayModes[i].getWidth() <= 1920) {
                            width = availableDisplayModes[i].getWidth();
                            height = availableDisplayModes[i].getHeight();
                        }
                    }
                    Core.width = width;
                    Core.height = height;
                }
                catch (LWJGLException ex) {
                    ex.printStackTrace();
                }
            }
            this.setOptionZoomLevels2x("50;75;125;150;175;200");
            this.setOptionZoomLevels1x("50;75;125;150;175;200");
            this.saveOptions();
            return false;
        }
        for (int j = 0; j < 4; ++j) {
            this.setAutoZoom(j, false);
        }
        Core.OptionLanguageName = null;
        final BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("version=")) {
                    this.version = new Integer(line.replaceFirst("version=", ""));
                }
                else if (line.startsWith("width=")) {
                    Core.width = new Integer(line.replaceFirst("width=", ""));
                }
                else if (line.startsWith("height=")) {
                    Core.height = new Integer(line.replaceFirst("height=", ""));
                }
                else if (line.startsWith("fullScreen=")) {
                    Core.fullScreen = Boolean.parseBoolean(line.replaceFirst("fullScreen=", ""));
                }
                else if (line.startsWith("frameRate=")) {
                    PerformanceSettings.setLockFPS(Integer.parseInt(line.replaceFirst("frameRate=", "")));
                }
                else if (line.startsWith("uncappedFPS=")) {
                    PerformanceSettings.setUncappedFPS(Boolean.parseBoolean(line.replaceFirst("uncappedFPS=", "")));
                }
                else if (line.startsWith("iso_cursor=")) {
                    getInstance().setIsoCursorVisibility(Integer.parseInt(line.replaceFirst("iso_cursor=", "")));
                }
                else if (line.startsWith("showCursorWhileAiming=")) {
                    Core.OptionShowCursorWhileAiming = Boolean.parseBoolean(line.replaceFirst("showCursorWhileAiming=", ""));
                }
                else if (line.startsWith("water=")) {
                    PerformanceSettings.WaterQuality = Integer.parseInt(line.replaceFirst("water=", ""));
                }
                else if (line.startsWith("puddles=")) {
                    PerformanceSettings.PuddlesQuality = Integer.parseInt(line.replaceFirst("puddles=", ""));
                }
                else if (line.startsWith("lighting=")) {
                    PerformanceSettings.LightingFrameSkip = Integer.parseInt(line.replaceFirst("lighting=", ""));
                }
                else if (line.startsWith("lightFPS=")) {
                    PerformanceSettings.instance.setLightingFPS(Integer.parseInt(line.replaceFirst("lightFPS=", "")));
                }
                else if (line.startsWith("perfSkybox=")) {
                    this.iPerfSkybox = Integer.parseInt(line.replaceFirst("perfSkybox=", ""));
                    this.iPerfSkybox_new = this.iPerfSkybox;
                }
                else if (line.startsWith("perfPuddles=")) {
                    this.iPerfPuddles = Integer.parseInt(line.replaceFirst("perfPuddles=", ""));
                    this.iPerfPuddles_new = this.iPerfPuddles;
                }
                else if (line.startsWith("bPerfReflections=")) {
                    this.bPerfReflections = Boolean.parseBoolean(line.replaceFirst("bPerfReflections=", ""));
                    this.bPerfReflections_new = this.bPerfReflections;
                }
                else if (line.startsWith("bMultithreadedRendering=")) {
                    Core.bMultithreadedRendering = Boolean.parseBoolean(line.replaceFirst("bMultithreadedRendering=", ""));
                }
                else if (line.startsWith("language=")) {
                    Core.OptionLanguageName = line.replaceFirst("language=", "").trim();
                }
                else if (line.startsWith("zoom=")) {
                    Core.OptionZoom = Boolean.parseBoolean(line.replaceFirst("zoom=", ""));
                }
                else if (line.startsWith("autozoom=")) {
                    final String[] split = line.replaceFirst("autozoom=", "").split(",");
                    for (int k = 0; k < split.length; ++k) {
                        if (!split[k].isEmpty()) {
                            final int int1 = Integer.parseInt(split[k]);
                            if (int1 >= 1 && int1 <= 4) {
                                this.setAutoZoom(int1 - 1, true);
                            }
                        }
                    }
                }
                else if (line.startsWith("fontSize=")) {
                    this.setOptionFontSize(Integer.parseInt(line.replaceFirst("fontSize=", "").trim()));
                }
                else if (line.startsWith("contextMenuFont=")) {
                    Core.OptionContextMenuFont = line.replaceFirst("contextMenuFont=", "").trim();
                }
                else if (line.startsWith("inventoryFont=")) {
                    Core.OptionInventoryFont = line.replaceFirst("inventoryFont=", "").trim();
                }
                else if (line.startsWith("tooltipFont=")) {
                    Core.OptionTooltipFont = line.replaceFirst("tooltipFont=", "").trim();
                }
                else if (line.startsWith("measurementsFormat=")) {
                    Core.OptionMeasurementFormat = line.replaceFirst("measurementsFormat=", "").trim();
                }
                else if (line.startsWith("clockFormat=")) {
                    Core.OptionClockFormat = Integer.parseInt(line.replaceFirst("clockFormat=", ""));
                }
                else if (line.startsWith("clockSize=")) {
                    Core.OptionClockSize = Integer.parseInt(line.replaceFirst("clockSize=", ""));
                }
                else if (line.startsWith("clock24Hour=")) {
                    Core.OptionClock24Hour = Boolean.parseBoolean(line.replaceFirst("clock24Hour=", ""));
                }
                else if (line.startsWith("vsync=")) {
                    Core.OptionVSync = Boolean.parseBoolean(line.replaceFirst("vsync=", ""));
                }
                else if (line.startsWith("voiceEnable=")) {
                    Core.OptionVoiceEnable = Boolean.parseBoolean(line.replaceFirst("voiceEnable=", ""));
                }
                else if (line.startsWith("voiceMode=")) {
                    Core.OptionVoiceMode = Integer.parseInt(line.replaceFirst("voiceMode=", ""));
                }
                else if (line.startsWith("voiceVADMode=")) {
                    Core.OptionVoiceVADMode = Integer.parseInt(line.replaceFirst("voiceVADMode=", ""));
                }
                else if (line.startsWith("voiceVolumeMic=")) {
                    Core.OptionVoiceVolumeMic = Integer.parseInt(line.replaceFirst("voiceVolumeMic=", ""));
                }
                else if (line.startsWith("voiceVolumePlayers=")) {
                    Core.OptionVoiceVolumePlayers = Integer.parseInt(line.replaceFirst("voiceVolumePlayers=", ""));
                }
                else if (line.startsWith("voiceRecordDeviceName=")) {
                    Core.OptionVoiceRecordDeviceName = line.replaceFirst("voiceRecordDeviceName=", "");
                }
                else if (line.startsWith("soundVolume=")) {
                    Core.OptionSoundVolume = Integer.parseInt(line.replaceFirst("soundVolume=", ""));
                }
                else if (line.startsWith("musicVolume=")) {
                    Core.OptionMusicVolume = Integer.parseInt(line.replaceFirst("musicVolume=", ""));
                }
                else if (line.startsWith("ambientVolume=")) {
                    Core.OptionAmbientVolume = Integer.parseInt(line.replaceFirst("ambientVolume=", ""));
                }
                else if (line.startsWith("musicLibrary=")) {
                    Core.OptionMusicLibrary = Integer.parseInt(line.replaceFirst("musicLibrary=", ""));
                }
                else if (line.startsWith("vehicleEngineVolume=")) {
                    Core.OptionVehicleEngineVolume = Integer.parseInt(line.replaceFirst("vehicleEngineVolume=", ""));
                }
                else if (line.startsWith("reloadDifficulty=")) {
                    Core.OptionReloadDifficulty = Integer.parseInt(line.replaceFirst("reloadDifficulty=", ""));
                }
                else if (line.startsWith("rackProgress=")) {
                    Core.OptionRackProgress = Boolean.parseBoolean(line.replaceFirst("rackProgress=", ""));
                }
                else if (line.startsWith("controller=")) {
                    final String replaceFirst = line.replaceFirst("controller=", "");
                    if (replaceFirst.isEmpty()) {
                        continue;
                    }
                    JoypadManager.instance.setControllerActive(replaceFirst, true);
                }
                else if (line.startsWith("tutorialDone=")) {
                    this.tutorialDone = Boolean.parseBoolean(line.replaceFirst("tutorialDone=", ""));
                }
                else if (line.startsWith("vehiclesWarningShow=")) {
                    this.vehiclesWarningShow = Boolean.parseBoolean(line.replaceFirst("vehiclesWarningShow=", ""));
                }
                else if (line.startsWith("bloodDecals=")) {
                    this.setOptionBloodDecals(Integer.parseInt(line.replaceFirst("bloodDecals=", "")));
                }
                else if (line.startsWith("borderless=")) {
                    Core.OptionBorderlessWindow = Boolean.parseBoolean(line.replaceFirst("borderless=", ""));
                }
                else if (line.startsWith("lockCursorToWindow=")) {
                    Core.OptionLockCursorToWindow = Boolean.parseBoolean(line.replaceFirst("lockCursorToWindow=", ""));
                }
                else if (line.startsWith("textureCompression=")) {
                    Core.OptionTextureCompression = Boolean.parseBoolean(line.replaceFirst("textureCompression=", ""));
                }
                else if (line.startsWith("modelTextureMipmaps=")) {
                    Core.OptionModelTextureMipmaps = Boolean.parseBoolean(line.replaceFirst("modelTextureMipmaps=", ""));
                }
                else if (line.startsWith("texture2x=")) {
                    Core.OptionTexture2x = Boolean.parseBoolean(line.replaceFirst("texture2x=", ""));
                }
                else if (line.startsWith("zoomLevels1x=")) {
                    Core.OptionZoomLevels1x = line.replaceFirst("zoomLevels1x=", "");
                }
                else if (line.startsWith("zoomLevels2x=")) {
                    Core.OptionZoomLevels2x = line.replaceFirst("zoomLevels2x=", "");
                }
                else if (line.startsWith("showChatTimestamp=")) {
                    Core.OptionShowChatTimestamp = Boolean.parseBoolean(line.replaceFirst("showChatTimestamp=", ""));
                }
                else if (line.startsWith("showChatTitle=")) {
                    Core.OptionShowChatTitle = Boolean.parseBoolean(line.replaceFirst("showChatTitle=", ""));
                }
                else if (line.startsWith("chatFontSize=")) {
                    Core.OptionChatFontSize = line.replaceFirst("chatFontSize=", "");
                }
                else if (line.startsWith("minChatOpaque=")) {
                    Core.OptionMinChatOpaque = Float.parseFloat(line.replaceFirst("minChatOpaque=", ""));
                }
                else if (line.startsWith("maxChatOpaque=")) {
                    Core.OptionMaxChatOpaque = Float.parseFloat(line.replaceFirst("maxChatOpaque=", ""));
                }
                else if (line.startsWith("chatFadeTime=")) {
                    Core.OptionChatFadeTime = Float.parseFloat(line.replaceFirst("chatFadeTime=", ""));
                }
                else if (line.startsWith("chatOpaqueOnFocus=")) {
                    Core.OptionChatOpaqueOnFocus = Boolean.parseBoolean(line.replaceFirst("chatOpaqueOnFocus=", ""));
                }
                else if (line.startsWith("doneNewSaveFolder=")) {
                    this.doneNewSaveFolder = Boolean.parseBoolean(line.replaceFirst("doneNewSaveFolder=", ""));
                }
                else if (line.startsWith("contentTranslationsEnabled=")) {
                    Core.OptionEnableContentTranslations = Boolean.parseBoolean(line.replaceFirst("contentTranslationsEnabled=", ""));
                }
                else if (line.startsWith("showYourUsername=")) {
                    this.showYourUsername = Boolean.parseBoolean(line.replaceFirst("showYourUsername=", ""));
                }
                else if (line.startsWith("riversideDone=")) {
                    this.riversideDone = Boolean.parseBoolean(line.replaceFirst("riversideDone=", ""));
                }
                else if (line.startsWith("rosewoodSpawnDone=")) {
                    this.rosewoodSpawnDone = Boolean.parseBoolean(line.replaceFirst("rosewoodSpawnDone=", ""));
                }
                else if (line.startsWith("gotNewBelt=")) {
                    this.gotNewBelt = Boolean.parseBoolean(line.replaceFirst("gotNewBelt=", ""));
                }
                else if (line.startsWith("mpTextColor=")) {
                    final String[] split2 = line.replaceFirst("mpTextColor=", "").split(",");
                    float float1 = Float.parseFloat(split2[0]);
                    float float2 = Float.parseFloat(split2[1]);
                    float float3 = Float.parseFloat(split2[2]);
                    if (float1 < 0.19f) {
                        float1 = 0.19f;
                    }
                    if (float2 < 0.19f) {
                        float2 = 0.19f;
                    }
                    if (float3 < 0.19f) {
                        float3 = 0.19f;
                    }
                    this.mpTextColor = new ColorInfo(float1, float2, float3, 1.0f);
                }
                else if (line.startsWith("objHighlightColor=")) {
                    final String[] split3 = line.replaceFirst("objHighlightColor=", "").split(",");
                    float float4 = Float.parseFloat(split3[0]);
                    float float5 = Float.parseFloat(split3[1]);
                    float float6 = Float.parseFloat(split3[2]);
                    if (float4 < 0.19f) {
                        float4 = 0.19f;
                    }
                    if (float5 < 0.19f) {
                        float5 = 0.19f;
                    }
                    if (float6 < 0.19f) {
                        float6 = 0.19f;
                    }
                    this.objectHighlitedColor.set(float4, float5, float6, 1.0f);
                }
                else if (line.startsWith("seenNews=")) {
                    this.setSeenUpdateText(line.replaceFirst("seenNews=", ""));
                }
                else if (line.startsWith("toggleToAim=")) {
                    this.setToggleToAim(Boolean.parseBoolean(line.replaceFirst("toggleToAim=", "")));
                }
                else if (line.startsWith("toggleToRun=")) {
                    this.setToggleToRun(Boolean.parseBoolean(line.replaceFirst("toggleToRun=", "")));
                }
                else if (line.startsWith("toggleToSprint=")) {
                    this.setToggleToSprint(Boolean.parseBoolean(line.replaceFirst("toggleToSprint=", "")));
                }
                else if (line.startsWith("celsius=")) {
                    this.setCelsius(Boolean.parseBoolean(line.replaceFirst("celsius=", "")));
                }
                else if (line.startsWith("mapOrder=")) {
                    if (this.version < 7) {
                        line = "mapOrder=";
                    }
                    final String[] split4 = line.replaceFirst("mapOrder=", "").split(";");
                    for (int length = split4.length, l = 0; l < length; ++l) {
                        final String trim = split4[l].trim();
                        if (!trim.isEmpty()) {
                            ActiveMods.getById("default").getMapOrder().add(trim);
                        }
                    }
                    ZomboidFileSystem.instance.saveModsFile();
                }
                else if (line.startsWith("showFirstTimeSneakTutorial=")) {
                    this.setShowFirstTimeSneakTutorial(Boolean.parseBoolean(line.replaceFirst("showFirstTimeSneakTutorial=", "")));
                }
                else if (line.startsWith("uiRenderOffscreen=")) {
                    Core.OptionUIFBO = Boolean.parseBoolean(line.replaceFirst("uiRenderOffscreen=", ""));
                }
                else if (line.startsWith("uiRenderFPS=")) {
                    Core.OptionUIRenderFPS = Integer.parseInt(line.replaceFirst("uiRenderFPS=", ""));
                }
                else if (line.startsWith("radialMenuKeyToggle=")) {
                    Core.OptionRadialMenuKeyToggle = Boolean.parseBoolean(line.replaceFirst("radialMenuKeyToggle=", ""));
                }
                else if (line.startsWith("reloadRadialInstant=")) {
                    Core.OptionReloadRadialInstant = Boolean.parseBoolean(line.replaceFirst("reloadRadialInstant=", ""));
                }
                else if (line.startsWith("panCameraWhileAiming=")) {
                    Core.OptionPanCameraWhileAiming = Boolean.parseBoolean(line.replaceFirst("panCameraWhileAiming=", ""));
                }
                else if (line.startsWith("panCameraWhileDriving=")) {
                    Core.OptionPanCameraWhileDriving = Boolean.parseBoolean(line.replaceFirst("panCameraWhileDriving=", ""));
                }
                else if (line.startsWith("temperatureDisplayCelsius=")) {
                    Core.OptionTemperatureDisplayCelsius = Boolean.parseBoolean(line.replaceFirst("temperatureDisplayCelsius=", ""));
                }
                else if (line.startsWith("doWindSpriteEffects=")) {
                    Core.OptionDoWindSpriteEffects = Boolean.parseBoolean(line.replaceFirst("doWindSpriteEffects=", ""));
                }
                else if (line.startsWith("doDoorSpriteEffects=")) {
                    Core.OptionDoDoorSpriteEffects = Boolean.parseBoolean(line.replaceFirst("doDoorSpriteEffects=", ""));
                }
                else if (line.startsWith("updateSneakButton2=")) {
                    Core.OptionUpdateSneakButton = true;
                }
                else if (line.startsWith("updateSneakButton=")) {
                    Core.OptionUpdateSneakButton = Boolean.parseBoolean(line.replaceFirst("updateSneakButton=", ""));
                }
                else if (line.startsWith("dblTapJogToSprint=")) {
                    Core.OptiondblTapJogToSprint = Boolean.parseBoolean(line.replaceFirst("dblTapJogToSprint=", ""));
                }
                else if (line.startsWith("aimOutline=")) {
                    this.setOptionAimOutline(PZMath.tryParseInt(line.replaceFirst("aimOutline=", ""), 2));
                }
                else if (line.startsWith("cycleContainerKey=")) {
                    Core.OptionCycleContainerKey = line.replaceFirst("cycleContainerKey=", "");
                }
                else if (line.startsWith("dropItemsOnSquareCenter=")) {
                    Core.OptionDropItemsOnSquareCenter = Boolean.parseBoolean(line.replaceFirst("dropItemsOnSquareCenter=", ""));
                }
                else if (line.startsWith("timedActionGameSpeedReset=")) {
                    Core.OptionTimedActionGameSpeedReset = Boolean.parseBoolean(line.replaceFirst("timedActionGameSpeedReset=", ""));
                }
                else if (line.startsWith("shoulderButtonContainerSwitch=")) {
                    Core.OptionShoulderButtonContainerSwitch = Integer.parseInt(line.replaceFirst("shoulderButtonContainerSwitch=", ""));
                }
                else if (line.startsWith("singleContextMenu=")) {
                    this.readPerPlayerBoolean(line.replaceFirst("singleContextMenu=", ""), Core.OptionSingleContextMenu);
                }
                else if (line.startsWith("renderPrecipIndoors=")) {
                    Core.OptionRenderPrecipIndoors = Boolean.parseBoolean(line.replaceFirst("renderPrecipIndoors=", ""));
                }
                else if (line.startsWith("autoProneAtk=")) {
                    Core.OptionAutoProneAtk = Boolean.parseBoolean(line.replaceFirst("autoProneAtk=", ""));
                }
                else if (line.startsWith("3DGroundItem=")) {
                    Core.Option3DGroundItem = Boolean.parseBoolean(line.replaceFirst("3DGroundItem=", ""));
                }
                else if (line.startsWith("tieredZombieUpdates=")) {
                    this.setOptionTieredZombieUpdates(Boolean.parseBoolean(line.replaceFirst("tieredZombieUpdates=", "")));
                }
                else if (line.startsWith("progressBar=")) {
                    this.setOptionProgressBar(Boolean.parseBoolean(line.replaceFirst("progressBar=", "")));
                }
                else if (line.startsWith("corpseShadows=")) {
                    Core.OptionCorpseShadows = Boolean.parseBoolean(line.replaceFirst("corpseShadows=", ""));
                }
                else if (line.startsWith("simpleClothingTextures=")) {
                    this.setOptionSimpleClothingTextures(PZMath.tryParseInt(line.replaceFirst("simpleClothingTextures=", ""), 1));
                }
                else if (line.startsWith("simpleWeaponTextures=")) {
                    Core.OptionSimpleWeaponTextures = Boolean.parseBoolean(line.replaceFirst("simpleWeaponTextures=", ""));
                }
                else if (line.startsWith("autoDrink=")) {
                    Core.OptionAutoDrink = Boolean.parseBoolean(line.replaceFirst("autoDrink=", ""));
                }
                else if (line.startsWith("leaveKeyInIgnition=")) {
                    Core.OptionLeaveKeyInIgnition = Boolean.parseBoolean(line.replaceFirst("leaveKeyInIgnition=", ""));
                }
                else if (line.startsWith("searchModeOverlayEffect=")) {
                    Core.OptionSearchModeOverlayEffect = Integer.parseInt(line.replaceFirst("searchModeOverlayEffect=", ""));
                }
                else if (line.startsWith("ignoreProneZombieRange=")) {
                    this.setOptionIgnoreProneZombieRange(PZMath.tryParseInt(line.replaceFirst("ignoreProneZombieRange=", ""), 1));
                }
                else if (line.startsWith("fogQuality=")) {
                    PerformanceSettings.FogQuality = Integer.parseInt(line.replaceFirst("fogQuality=", ""));
                }
                else {
                    if (!line.startsWith("renderPrecipitation=")) {
                        continue;
                    }
                    Core.OptionRenderPrecipitation = Integer.parseInt(line.replaceFirst("renderPrecipitation=", ""));
                }
            }
            if (Core.OptionLanguageName == null) {
                Core.OptionLanguageName = System.getProperty("user.language").toUpperCase();
            }
            if (!this.doneNewSaveFolder) {
                new File(ZomboidFileSystem.instance.getSaveDir()).mkdir();
                final ArrayList<String> list = new ArrayList<String>();
                list.add("Beginner");
                list.add("Survival");
                list.add("A Really CD DA");
                list.add("LastStand");
                list.add("Opening Hours");
                list.add("Sandbox");
                list.add("Tutorial");
                list.add("Winter is Coming");
                list.add("You Have One Day");
                try {
                    for (final String s : list) {
                        final File file2 = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator, s));
                        final File file3 = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getSaveDir(), File.separator, s));
                        if (file2.exists()) {
                            file3.mkdir();
                            Files.move(file2.toPath(), file3.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                }
                catch (Exception ex3) {}
                this.doneNewSaveFolder = true;
            }
        }
        catch (Exception ex2) {
            ex2.printStackTrace();
        }
        finally {
            bufferedReader.close();
        }
        this.saveOptions();
        return true;
    }
    
    public boolean isDedicated() {
        return GameServer.bServer;
    }
    
    private void copyPasteFolders(final String pathname) {
        final File absoluteFile = new File(pathname).getAbsoluteFile();
        if (absoluteFile.exists()) {
            this.searchFolders(absoluteFile, pathname);
        }
    }
    
    private void searchFolders(final File file, final String s) {
        if (file.isDirectory()) {
            new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.saveFolder, File.separator, s)).mkdir();
            final String[] list = file.list();
            for (int i = 0; i < list.length; ++i) {
                this.searchFolders(new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, file.getAbsolutePath(), File.separator, list[i])), invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, File.separator, list[i]));
            }
        }
        else {
            this.copyPasteFile(file, s);
        }
    }
    
    private void copyPasteFile(final File file, final String s) {
        FileOutputStream fileOutputStream = null;
        FileInputStream fileInputStream = null;
        try {
            final File file2 = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.saveFolder, File.separator, s));
            file2.createNewFile();
            fileOutputStream = new FileOutputStream(file2);
            fileInputStream = new FileInputStream(file);
            fileOutputStream.getChannel().transferFrom(fileInputStream.getChannel(), 0L, file.length());
        }
        catch (Exception ex) {
            ex.printStackTrace();
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            }
            catch (IOException ex2) {
                ex2.printStackTrace();
            }
        }
        finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            }
            catch (IOException ex3) {
                ex3.printStackTrace();
            }
        }
    }
    
    public static String getMyDocumentFolder() {
        return ZomboidFileSystem.instance.getCacheDir();
    }
    
    public void saveOptions() throws IOException {
        final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator));
        if (!file.exists()) {
            file.createNewFile();
        }
        final FileWriter fileWriter = new FileWriter(file);
        try {
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.fileversion));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.getScreenWidth()));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.getScreenHeight()));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, Core.fullScreen));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, PerformanceSettings.getLockFPS()));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, PerformanceSettings.isUncappedFPS()));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, getInstance().getIsoCursorVisibility()));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, Core.OptionShowCursorWhileAiming));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, PerformanceSettings.WaterQuality));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, PerformanceSettings.PuddlesQuality));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, PerformanceSettings.LightingFrameSkip));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, PerformanceSettings.LightingFPS));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.iPerfSkybox_new));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.iPerfPuddles_new));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, this.bPerfReflections_new));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.vidMem));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, Core.bMultithreadedRendering));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getOptionLanguageName()));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, Core.OptionZoom));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Core.OptionFontSize));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Core.OptionContextMenuFont));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Core.OptionInventoryFont));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Core.OptionTooltipFont));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Core.OptionClockFormat));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Core.OptionClockSize));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, Core.OptionClock24Hour));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Core.OptionMeasurementFormat));
            String s = "";
            for (int i = 0; i < 4; ++i) {
                if (Core.bAutoZoom[i]) {
                    if (!s.isEmpty()) {
                        s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
                    }
                    s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s, i + 1);
                }
            }
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, Core.OptionVSync));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Core.OptionSoundVolume));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Core.OptionAmbientVolume));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Core.OptionMusicVolume));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Core.OptionMusicLibrary));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Core.OptionVehicleEngineVolume));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, Core.OptionVoiceEnable));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Core.OptionVoiceMode));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Core.OptionVoiceVADMode));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Core.OptionVoiceVolumeMic));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Core.OptionVoiceVolumePlayers));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Core.OptionVoiceRecordDeviceName));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Core.OptionReloadDifficulty));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, Core.OptionRackProgress));
            final Iterator<String> iterator = JoypadManager.instance.ActiveControllerGUIDs.iterator();
            while (iterator.hasNext()) {
                fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (String)iterator.next()));
            }
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, this.isTutorialDone()));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, this.isVehiclesWarningShow()));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Core.OptionBloodDecals));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, Core.OptionBorderlessWindow));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, Core.OptionLockCursorToWindow));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, Core.OptionTextureCompression));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, Core.OptionModelTextureMipmaps));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, Core.OptionTexture2x));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Core.OptionZoomLevels1x));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Core.OptionZoomLevels2x));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, Core.OptionShowChatTimestamp));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, Core.OptionShowChatTitle));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Core.OptionChatFontSize));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, Core.OptionMinChatOpaque));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, Core.OptionMaxChatOpaque));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, Core.OptionChatFadeTime));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, Core.OptionChatOpaqueOnFocus));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, this.doneNewSaveFolder));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, Core.OptionEnableContentTranslations));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, this.showYourUsername));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, this.rosewoodSpawnDone));
            if (this.mpTextColor != null) {
                fileWriter.write(invokedynamic(makeConcatWithConstants:(FFF)Ljava/lang/String;, this.mpTextColor.r, this.mpTextColor.g, this.mpTextColor.b));
            }
            fileWriter.write(invokedynamic(makeConcatWithConstants:(FFF)Ljava/lang/String;, this.objectHighlitedColor.r, this.objectHighlitedColor.g, this.objectHighlitedColor.b));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getSeenUpdateText()));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, this.isToggleToAim()));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, this.isToggleToRun()));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, this.isToggleToSprint()));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, this.isCelsius()));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, this.isRiversideDone()));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, this.isShowFirstTimeSneakTutorial()));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, Core.OptionUIFBO));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Core.OptionUIRenderFPS));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, Core.OptionRadialMenuKeyToggle));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, Core.OptionReloadRadialInstant));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, Core.OptionPanCameraWhileAiming));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, Core.OptionPanCameraWhileDriving));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, Core.OptionTemperatureDisplayCelsius));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, Core.OptionDoWindSpriteEffects));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, Core.OptionDoDoorSpriteEffects));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, Core.OptionUpdateSneakButton));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, Core.OptiondblTapJogToSprint));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, this.gotNewBelt));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Core.OptionAimOutline));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Core.OptionCycleContainerKey));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, Core.OptionDropItemsOnSquareCenter));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, Core.OptionTimedActionGameSpeedReset));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Core.OptionShoulderButtonContainerSwitch));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getPerPlayerBooleanString(Core.OptionSingleContextMenu)));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, Core.OptionRenderPrecipIndoors));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, Core.OptionAutoProneAtk));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, Core.Option3DGroundItem));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, this.getOptionTieredZombieUpdates()));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, this.isOptionProgressBar()));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, this.getOptionCorpseShadows()));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.getOptionSimpleClothingTextures()));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, this.getOptionSimpleWeaponTextures()));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, this.getOptionAutoDrink()));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, this.getOptionLeaveKeyInIgnition()));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.getOptionSearchModeOverlayEffect()));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.getOptionIgnoreProneZombieRange()));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, PerformanceSettings.FogQuality));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Core.OptionRenderPrecipitation));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            fileWriter.close();
        }
    }
    
    public void setWindowed(final boolean fullScreen) {
        RenderThread.invokeOnRenderContext(() -> {
            if (fullScreen != Core.fullScreen) {
                setDisplayMode(this.getScreenWidth(), this.getScreenHeight(), fullScreen);
            }
            Core.fullScreen = fullScreen;
            if (Core.fakefullscreen) {
                Display.setResizable(false);
            }
            else {
                Display.setResizable(!fullScreen);
            }
            try {
                this.saveOptions();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }
    
    public boolean isFullScreen() {
        return Core.fullScreen;
    }
    
    public KahluaTable getScreenModes() {
        final ArrayList<String> list = new ArrayList<String>();
        final KahluaTable table = LuaManager.platform.newTable();
        final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, LuaManager.getLuaCacheDir(), File.separator));
        int n = 1;
        try {
            if (!file.exists()) {
                file.createNewFile();
                final FileWriter fileWriter = new FileWriter(file);
                0;
                0;
                final DisplayMode[] availableDisplayModes = Display.getAvailableDisplayModes();
                for (int i = 0; i < availableDisplayModes.length; ++i) {
                    final Integer value = availableDisplayModes[i].getWidth();
                    final Integer value2 = availableDisplayModes[i].getHeight();
                    if (!list.contains(invokedynamic(makeConcatWithConstants:(Ljava/lang/Integer;Ljava/lang/Integer;)Ljava/lang/String;, value, value2))) {
                        table.rawset(n, invokedynamic(makeConcatWithConstants:(Ljava/lang/Integer;Ljava/lang/Integer;)Ljava/lang/String;, value, value2));
                        fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/Integer;Ljava/lang/Integer;)Ljava/lang/String;, value, value2));
                        list.add(invokedynamic(makeConcatWithConstants:(Ljava/lang/Integer;Ljava/lang/Integer;)Ljava/lang/String;, value, value2));
                        ++n;
                    }
                }
                fileWriter.close();
            }
            else {
                final BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    table.rawset(n, (Object)line.trim());
                    ++n;
                }
                bufferedReader.close();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return table;
    }
    
    public static void setDisplayMode(final int n, final int n2, final boolean fullScreen) {
        final DisplayMode[] array;
        int length;
        int i = 0;
        DisplayMode displayMode;
        DisplayMode displayMode2 = null;
        int frequency = 0;
        final DisplayMode displayMode3;
        final GLFWVidMode glfwVidMode;
        RenderThread.invokeOnRenderContext(() -> {
            if (Display.getWidth() != n || Display.getHeight() != n2 || Display.isFullscreen() != fullScreen || Display.isBorderlessWindow() != Core.OptionBorderlessWindow) {
                Core.fullScreen = fullScreen;
                try {
                    if (fullScreen) {
                        Display.getAvailableDisplayModes();
                        for (length = array.length; i < length; ++i) {
                            displayMode = array[i];
                            if (displayMode.getWidth() == n && displayMode.getHeight() == n2 && displayMode.isFullscreenCapable()) {
                                if ((displayMode2 == null || displayMode.getFrequency() >= frequency) && (displayMode2 == null || displayMode.getBitsPerPixel() > displayMode2.getBitsPerPixel())) {
                                    frequency = displayMode2.getFrequency();
                                }
                                if (displayMode.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel() && displayMode.getFrequency() == Display.getDesktopDisplayMode().getFrequency()) {
                                    break;
                                }
                            }
                            if (displayMode.isFullscreenCapable() && (displayMode3 == null || Math.abs(displayMode.getWidth() - n) < Math.abs(displayMode3.getWidth() - n) || (displayMode.getWidth() == displayMode3.getWidth() && displayMode.getFrequency() > frequency))) {
                                displayMode.getFrequency();
                                System.out.println(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, displayMode3.getWidth(), displayMode3.getFrequency()));
                            }
                        }
                        if (displayMode2 == null && displayMode3 != null) {
                            displayMode2 = displayMode3;
                        }
                    }
                    else if (Core.OptionBorderlessWindow) {
                        if (Display.getWindow() != 0L && Display.isFullscreen()) {
                            Display.setFullscreen(false);
                        }
                        GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
                        displayMode2 = new DisplayMode(glfwVidMode.width(), glfwVidMode.height());
                    }
                    else {
                        displayMode2 = new DisplayMode(n, n2);
                    }
                    if (displayMode2 == null) {
                        DebugLog.log(invokedynamic(makeConcatWithConstants:(IIZ)Ljava/lang/String;, n, n2, fullScreen));
                    }
                    else {
                        Display.setBorderlessWindow(Core.OptionBorderlessWindow);
                        if (fullScreen) {
                            Display.setDisplayModeAndFullscreen(displayMode2);
                        }
                        else {
                            Display.setDisplayMode(displayMode2);
                            Display.setFullscreen(false);
                        }
                        if (!fullScreen && Core.OptionBorderlessWindow) {
                            Display.setResizable(false);
                        }
                        else if (!fullScreen && !Core.fakefullscreen) {
                            Display.setResizable(false);
                            Display.setResizable(true);
                        }
                        if (Display.isCreated()) {
                            DebugLog.log(invokedynamic(makeConcatWithConstants:(IIIZ)Ljava/lang/String;, Display.getWidth(), Display.getHeight(), Display.getDisplayMode().getFrequency(), Display.isFullscreen()));
                        }
                    }
                }
                catch (LWJGLException ex) {
                    DebugLog.log(invokedynamic(makeConcatWithConstants:(IIZLorg/lwjglx/LWJGLException;)Ljava/lang/String;, n, n2, fullScreen, ex));
                }
            }
        });
    }
    
    private boolean isFunctionKey(final int n) {
        return (n >= 59 && n <= 68) || (n >= 87 && n <= 105) || n == 113;
    }
    
    public boolean isDoingTextEntry() {
        return Core.CurrentTextEntryBox != null && Core.CurrentTextEntryBox.IsEditable && Core.CurrentTextEntryBox.DoingTextEntry;
    }
    
    private void updateKeyboardAux(final UITextBox2 uiTextBox2, final int n) {
        final boolean b = Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157);
        final boolean b2 = Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
        if (n == 28 || n == 156) {
            boolean b3 = false;
            if (UIManager.getDebugConsole() != null && uiTextBox2 == UIManager.getDebugConsole().CommandLine) {
                b3 = true;
            }
            if (uiTextBox2.multipleLine) {
                if (uiTextBox2.Lines.size() < uiTextBox2.getMaxLines()) {
                    if (uiTextBox2.TextEntryCursorPos != uiTextBox2.ToSelectionIndex) {
                        final int min = Math.min(uiTextBox2.TextEntryCursorPos, uiTextBox2.ToSelectionIndex);
                        final int max = Math.max(uiTextBox2.TextEntryCursorPos, uiTextBox2.ToSelectionIndex);
                        if (uiTextBox2.internalText.length() > 0) {
                            uiTextBox2.internalText = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, uiTextBox2.internalText.substring(0, min), uiTextBox2.internalText.substring(max));
                        }
                        else {
                            uiTextBox2.internalText = "\n";
                        }
                        uiTextBox2.TextEntryCursorPos = min + 1;
                    }
                    else {
                        final int textEntryCursorPos = uiTextBox2.TextEntryCursorPos;
                        uiTextBox2.SetText(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, uiTextBox2.internalText.substring(0, textEntryCursorPos), uiTextBox2.internalText.substring(textEntryCursorPos)));
                        uiTextBox2.TextEntryCursorPos = textEntryCursorPos + 1;
                    }
                    uiTextBox2.ToSelectionIndex = uiTextBox2.TextEntryCursorPos;
                    uiTextBox2.CursorLine = uiTextBox2.toDisplayLine(uiTextBox2.TextEntryCursorPos);
                }
            }
            else {
                uiTextBox2.onCommandEntered();
            }
            if (b3 && (!GameClient.bClient || !GameClient.accessLevel.equals("") || (GameClient.connection != null && GameClient.connection.isCoopHost))) {
                UIManager.getDebugConsole().ProcessCommand();
            }
            return;
        }
        if (n == 1) {
            uiTextBox2.onOtherKey(1);
            GameKeyboard.eatKeyPress(1);
            return;
        }
        if (n == 15) {
            uiTextBox2.onOtherKey(15);
            LuaEventManager.triggerEvent("SwitchChatStream");
            return;
        }
        if (n == 58) {
            return;
        }
        if (n == 199) {
            uiTextBox2.TextEntryCursorPos = 0;
            if (!uiTextBox2.Lines.isEmpty()) {
                uiTextBox2.TextEntryCursorPos = uiTextBox2.TextOffsetOfLineStart.get(uiTextBox2.CursorLine);
            }
            if (!b2) {
                uiTextBox2.ToSelectionIndex = uiTextBox2.TextEntryCursorPos;
            }
            uiTextBox2.resetBlink();
            return;
        }
        if (n == 207) {
            uiTextBox2.TextEntryCursorPos = uiTextBox2.internalText.length();
            if (!uiTextBox2.Lines.isEmpty()) {
                uiTextBox2.TextEntryCursorPos = uiTextBox2.TextOffsetOfLineStart.get(uiTextBox2.CursorLine) + uiTextBox2.Lines.get(uiTextBox2.CursorLine).length();
            }
            if (!b2) {
                uiTextBox2.ToSelectionIndex = uiTextBox2.TextEntryCursorPos;
            }
            uiTextBox2.resetBlink();
            return;
        }
        if (n == 200) {
            if (uiTextBox2.CursorLine > 0) {
                int length = uiTextBox2.TextEntryCursorPos - uiTextBox2.TextOffsetOfLineStart.get(uiTextBox2.CursorLine);
                --uiTextBox2.CursorLine;
                if (length > uiTextBox2.Lines.get(uiTextBox2.CursorLine).length()) {
                    length = uiTextBox2.Lines.get(uiTextBox2.CursorLine).length();
                }
                uiTextBox2.TextEntryCursorPos = uiTextBox2.TextOffsetOfLineStart.get(uiTextBox2.CursorLine) + length;
                if (!b2) {
                    uiTextBox2.ToSelectionIndex = uiTextBox2.TextEntryCursorPos;
                }
            }
            uiTextBox2.onPressUp();
            return;
        }
        if (n == 208) {
            if (uiTextBox2.Lines.size() - 1 > uiTextBox2.CursorLine && uiTextBox2.CursorLine + 1 < uiTextBox2.getMaxLines()) {
                int length2 = uiTextBox2.TextEntryCursorPos - uiTextBox2.TextOffsetOfLineStart.get(uiTextBox2.CursorLine);
                ++uiTextBox2.CursorLine;
                if (length2 > uiTextBox2.Lines.get(uiTextBox2.CursorLine).length()) {
                    length2 = uiTextBox2.Lines.get(uiTextBox2.CursorLine).length();
                }
                uiTextBox2.TextEntryCursorPos = uiTextBox2.TextOffsetOfLineStart.get(uiTextBox2.CursorLine) + length2;
                if (!b2) {
                    uiTextBox2.ToSelectionIndex = uiTextBox2.TextEntryCursorPos;
                }
            }
            uiTextBox2.onPressDown();
            return;
        }
        if (n == 29) {
            return;
        }
        if (n == 157) {
            return;
        }
        if (n == 42) {
            return;
        }
        if (n == 54) {
            return;
        }
        if (n == 56) {
            return;
        }
        if (n == 184) {
            return;
        }
        if (n == 203) {
            --uiTextBox2.TextEntryCursorPos;
            if (uiTextBox2.TextEntryCursorPos < 0) {
                uiTextBox2.TextEntryCursorPos = 0;
            }
            if (!b2) {
                uiTextBox2.ToSelectionIndex = uiTextBox2.TextEntryCursorPos;
            }
            uiTextBox2.resetBlink();
            return;
        }
        if (n == 205) {
            ++uiTextBox2.TextEntryCursorPos;
            if (uiTextBox2.TextEntryCursorPos > uiTextBox2.internalText.length()) {
                uiTextBox2.TextEntryCursorPos = uiTextBox2.internalText.length();
            }
            if (!b2) {
                uiTextBox2.ToSelectionIndex = uiTextBox2.TextEntryCursorPos;
            }
            uiTextBox2.resetBlink();
            return;
        }
        if (this.isFunctionKey(n)) {
            return;
        }
        if ((n == 211 || n == 14) && uiTextBox2.TextEntryCursorPos != uiTextBox2.ToSelectionIndex) {
            final int min2 = Math.min(uiTextBox2.TextEntryCursorPos, uiTextBox2.ToSelectionIndex);
            uiTextBox2.internalText = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, uiTextBox2.internalText.substring(0, min2), uiTextBox2.internalText.substring(Math.max(uiTextBox2.TextEntryCursorPos, uiTextBox2.ToSelectionIndex)));
            uiTextBox2.CursorLine = uiTextBox2.toDisplayLine(min2);
            uiTextBox2.ToSelectionIndex = min2;
            uiTextBox2.TextEntryCursorPos = min2;
            uiTextBox2.onTextChange();
            return;
        }
        if (n == 211) {
            if (uiTextBox2.internalText.length() == 0 || uiTextBox2.TextEntryCursorPos >= uiTextBox2.internalText.length()) {
                return;
            }
            if (uiTextBox2.TextEntryCursorPos > 0) {
                uiTextBox2.internalText = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, uiTextBox2.internalText.substring(0, uiTextBox2.TextEntryCursorPos), uiTextBox2.internalText.substring(uiTextBox2.TextEntryCursorPos + 1));
            }
            else {
                uiTextBox2.internalText = uiTextBox2.internalText.substring(1);
            }
            uiTextBox2.onTextChange();
        }
        else if (n == 14) {
            if (uiTextBox2.internalText.length() == 0 || uiTextBox2.TextEntryCursorPos <= 0) {
                return;
            }
            if (uiTextBox2.TextEntryCursorPos > uiTextBox2.internalText.length()) {
                uiTextBox2.internalText = uiTextBox2.internalText.substring(0, uiTextBox2.internalText.length() - 1);
            }
            else {
                final int textEntryCursorPos2 = uiTextBox2.TextEntryCursorPos;
                uiTextBox2.internalText = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, uiTextBox2.internalText.substring(0, textEntryCursorPos2 - 1), uiTextBox2.internalText.substring(textEntryCursorPos2));
            }
            --uiTextBox2.TextEntryCursorPos;
            uiTextBox2.ToSelectionIndex = uiTextBox2.TextEntryCursorPos;
            uiTextBox2.onTextChange();
        }
        else if (b && n == 47) {
            final String clipboard = Clipboard.getClipboard();
            if (clipboard == null) {
                return;
            }
            if (uiTextBox2.TextEntryCursorPos != uiTextBox2.ToSelectionIndex) {
                final int min3 = Math.min(uiTextBox2.TextEntryCursorPos, uiTextBox2.ToSelectionIndex);
                uiTextBox2.internalText = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, uiTextBox2.internalText.substring(0, min3), clipboard, uiTextBox2.internalText.substring(Math.max(uiTextBox2.TextEntryCursorPos, uiTextBox2.ToSelectionIndex)));
                uiTextBox2.ToSelectionIndex = min3 + clipboard.length();
                uiTextBox2.TextEntryCursorPos = min3 + clipboard.length();
            }
            else {
                if (uiTextBox2.TextEntryCursorPos < uiTextBox2.internalText.length()) {
                    uiTextBox2.internalText = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, uiTextBox2.internalText.substring(0, uiTextBox2.TextEntryCursorPos), clipboard, uiTextBox2.internalText.substring(uiTextBox2.TextEntryCursorPos));
                }
                else {
                    uiTextBox2.internalText = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, uiTextBox2.internalText, clipboard);
                }
                uiTextBox2.TextEntryCursorPos += clipboard.length();
                uiTextBox2.ToSelectionIndex += clipboard.length();
            }
            uiTextBox2.onTextChange();
        }
        else if (b && n == 46) {
            if (uiTextBox2.TextEntryCursorPos == uiTextBox2.ToSelectionIndex) {
                return;
            }
            uiTextBox2.updateText();
            final String substring = uiTextBox2.Text.substring(Math.min(uiTextBox2.TextEntryCursorPos, uiTextBox2.ToSelectionIndex), Math.max(uiTextBox2.TextEntryCursorPos, uiTextBox2.ToSelectionIndex));
            if (substring != null && substring.length() > 0) {
                Clipboard.setClipboard(substring);
            }
        }
        else if (b && n == 45) {
            if (uiTextBox2.TextEntryCursorPos == uiTextBox2.ToSelectionIndex) {
                return;
            }
            uiTextBox2.updateText();
            final int min4 = Math.min(uiTextBox2.TextEntryCursorPos, uiTextBox2.ToSelectionIndex);
            final int max2 = Math.max(uiTextBox2.TextEntryCursorPos, uiTextBox2.ToSelectionIndex);
            final String substring2 = uiTextBox2.Text.substring(min4, max2);
            if (substring2 != null && substring2.length() > 0) {
                Clipboard.setClipboard(substring2);
            }
            uiTextBox2.internalText = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, uiTextBox2.internalText.substring(0, min4), uiTextBox2.internalText.substring(max2));
            uiTextBox2.ToSelectionIndex = min4;
            uiTextBox2.TextEntryCursorPos = min4;
        }
        else {
            if (b && n == 30) {
                uiTextBox2.selectAll();
                return;
            }
            if (uiTextBox2.ignoreFirst) {
                return;
            }
            if (uiTextBox2.internalText.length() >= uiTextBox2.TextEntryMaxLength) {
                return;
            }
            final char eventCharacter = Keyboard.getEventCharacter();
            if (eventCharacter == '\0') {
                return;
            }
            if (uiTextBox2.isOnlyNumbers() && eventCharacter != '.' && eventCharacter != '-') {
                try {
                    Double.parseDouble(String.valueOf(eventCharacter));
                }
                catch (Exception ex) {
                    return;
                }
            }
            if (uiTextBox2.TextEntryCursorPos == uiTextBox2.ToSelectionIndex) {
                final int textEntryCursorPos3 = uiTextBox2.TextEntryCursorPos;
                if (textEntryCursorPos3 < uiTextBox2.internalText.length()) {
                    uiTextBox2.internalText = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;CLjava/lang/String;)Ljava/lang/String;, uiTextBox2.internalText.substring(0, textEntryCursorPos3), eventCharacter, uiTextBox2.internalText.substring(textEntryCursorPos3));
                }
                else {
                    uiTextBox2.internalText = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;C)Ljava/lang/String;, uiTextBox2.internalText, eventCharacter);
                }
                ++uiTextBox2.TextEntryCursorPos;
                ++uiTextBox2.ToSelectionIndex;
                uiTextBox2.onTextChange();
            }
            else {
                final int min5 = Math.min(uiTextBox2.TextEntryCursorPos, uiTextBox2.ToSelectionIndex);
                final int max3 = Math.max(uiTextBox2.TextEntryCursorPos, uiTextBox2.ToSelectionIndex);
                if (uiTextBox2.internalText.length() > 0) {
                    uiTextBox2.internalText = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;CLjava/lang/String;)Ljava/lang/String;, uiTextBox2.internalText.substring(0, min5), eventCharacter, uiTextBox2.internalText.substring(max3));
                }
                else {
                    uiTextBox2.internalText = invokedynamic(makeConcatWithConstants:(C)Ljava/lang/String;, eventCharacter);
                }
                uiTextBox2.ToSelectionIndex = min5 + 1;
                uiTextBox2.TextEntryCursorPos = min5 + 1;
                uiTextBox2.onTextChange();
            }
        }
    }
    
    public void updateKeyboard() {
        if (!this.isDoingTextEntry()) {
            return;
        }
        while (Keyboard.next()) {
            if (!this.isDoingTextEntry()) {
                continue;
            }
            if (!Keyboard.getEventKeyState()) {
                continue;
            }
            this.updateKeyboardAux(Core.CurrentTextEntryBox, Keyboard.getEventKey());
        }
        if (Core.CurrentTextEntryBox != null && Core.CurrentTextEntryBox.ignoreFirst) {
            Core.CurrentTextEntryBox.ignoreFirst = false;
        }
    }
    
    public void quit() {
        DebugLog.log("EXITDEBUG: Core.quit 1");
        if (IsoPlayer.getInstance() != null) {
            DebugLog.log("EXITDEBUG: Core.quit 2");
            Core.bExiting = true;
        }
        else {
            DebugLog.log("EXITDEBUG: Core.quit 3");
            try {
                this.saveOptions();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
            GameClient.instance.Shutdown();
            SteamUtils.shutdown();
            DebugLog.log("EXITDEBUG: Core.quit 4");
            System.exit(0);
        }
    }
    
    public void exitToMenu() {
        DebugLog.log("EXITDEBUG: Core.exitToMenu");
        Core.bExiting = true;
    }
    
    public void quitToDesktop() {
        DebugLog.log("EXITDEBUG: Core.quitToDesktop");
        GameWindow.closeRequested = true;
    }
    
    public boolean supportRes(final int n, final int n2) throws LWJGLException {
        final DisplayMode[] availableDisplayModes = Display.getAvailableDisplayModes();
        for (int i = 0; i < availableDisplayModes.length; ++i) {
            if (availableDisplayModes[i].getWidth() == n && availableDisplayModes[i].getHeight() == n2 && availableDisplayModes[i].isFullscreenCapable()) {
                return true;
            }
        }
        return false;
    }
    
    public void init(final int n, final int n2) throws LWJGLException {
        System.setProperty("org.lwjgl.opengl.Window.undecorated", Core.OptionBorderlessWindow ? "true" : "false");
        if (!System.getProperty("os.name").contains("OS X") && !System.getProperty("os.name").startsWith("Win")) {
            DebugLog.log("Creating display. If this fails, you may need to install xrandr.");
        }
        setDisplayMode(n, n2, Core.fullScreen);
        try {
            Display.create(new PixelFormat(32, 0, 24, 8, 0));
        }
        catch (LWJGLException ex) {
            Display.destroy();
            Display.setDisplayModeAndFullscreen(Display.getDesktopDisplayMode());
            Display.create(new PixelFormat(32, 0, 24, 8, 0));
        }
        Core.fullScreen = Display.isFullscreen();
        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, GL11.glGetString(7936), GL11.glGetString(7937)));
        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, GL11.glGetString(7938)));
        DebugLog.log(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, Display.getDesktopDisplayMode().getWidth(), Display.getDesktopDisplayMode().getHeight()));
        DebugLog.log(invokedynamic(makeConcatWithConstants:(IIZ)Ljava/lang/String;, Core.width, Core.height, Core.fullScreen));
        GLVertexBufferObject.init();
        DebugLog.General.println("VSync: %s", Core.OptionVSync ? "ON" : "OFF");
        Display.setVSyncEnabled(Core.OptionVSync);
        GL11.glEnable(3553);
        IndieGL.glBlendFunc(770, 771);
        GL32.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    }
    
    private boolean setupMultiFBO() {
        try {
            if (!this.OffscreenBuffer.test()) {
                return false;
            }
            this.OffscreenBuffer.setZoomLevelsFromOption((Core.TileScale == 2) ? Core.OptionZoomLevels2x : Core.OptionZoomLevels1x);
            this.OffscreenBuffer.create(Display.getWidth(), Display.getHeight());
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }
    
    public void setScreenSize(final int n, final int n2) {
        if (Core.width != n || n2 != Core.height) {
            final int width = Core.width;
            final int height = Core.height;
            DebugLog.log(invokedynamic(makeConcatWithConstants:(IIIIZ)Ljava/lang/String;, width, height, n, n2, Core.fullScreen));
            Core.width = n;
            Core.height = n2;
            if (this.OffscreenBuffer != null && this.OffscreenBuffer.Current != null) {
                this.OffscreenBuffer.destroy();
                try {
                    this.OffscreenBuffer.setZoomLevelsFromOption((Core.TileScale == 2) ? Core.OptionZoomLevels2x : Core.OptionZoomLevels1x);
                    this.OffscreenBuffer.create(n, n2);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            try {
                LuaEventManager.triggerEvent("OnResolutionChange", width, height, n, n2);
            }
            catch (Exception ex2) {
                ex2.printStackTrace();
            }
            for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
                final IsoPlayer isoPlayer = IsoPlayer.players[i];
                if (isoPlayer != null) {
                    isoPlayer.dirtyRecalcGridStackTime = 2.0f;
                }
            }
        }
    }
    
    public static boolean supportCompressedTextures() {
        return GL.getCapabilities().GL_EXT_texture_compression_latc;
    }
    
    public void StartFrame() {
        if (LuaManager.thread != null && LuaManager.thread.bStep) {
            return;
        }
        if (this.RenderShader != null && this.OffscreenBuffer.Current != null) {
            this.RenderShader.setTexture(this.OffscreenBuffer.getTexture(0));
        }
        SpriteRenderer.instance.prePopulating();
        UIManager.resize();
        final boolean b = false;
        Texture.BindCount = 0;
        if (!b) {
            IndieGL.glClear(18176);
            if (DebugOptions.instance.Terrain.RenderTiles.HighContrastBg.getValue()) {
                SpriteRenderer.instance.glClearColor(255, 0, 255, 255);
                SpriteRenderer.instance.glClear(16384);
            }
        }
        if (this.OffscreenBuffer.Current != null) {
            SpriteRenderer.instance.glBuffer(1, 0);
        }
        IndieGL.glDoStartFrame(this.getScreenWidth(), this.getScreenWidth(), this.getCurrentPlayerZoom(), 0);
        this.frameStage = 1;
    }
    
    public void StartFrame(final int n, final boolean b) {
        if (LuaManager.thread.bStep) {
            return;
        }
        this.OffscreenBuffer.update();
        if (this.RenderShader != null && this.OffscreenBuffer.Current != null) {
            this.RenderShader.setTexture(this.OffscreenBuffer.getTexture(n));
        }
        if (b) {
            SpriteRenderer.instance.prePopulating();
        }
        if (!b) {
            SpriteRenderer.instance.initFromIsoCamera(n);
        }
        Texture.BindCount = 0;
        IndieGL.glLoadIdentity();
        if (this.OffscreenBuffer.Current != null) {
            SpriteRenderer.instance.glBuffer(1, n);
        }
        IndieGL.glDoStartFrame(this.getScreenWidth(), this.getScreenHeight(), this.getZoom(n), n);
        IndieGL.glClear(17664);
        if (DebugOptions.instance.Terrain.RenderTiles.HighContrastBg.getValue()) {
            SpriteRenderer.instance.glClearColor(255, 0, 255, 255);
            SpriteRenderer.instance.glClear(16384);
        }
        this.frameStage = 1;
    }
    
    public TextureFBO getOffscreenBuffer() {
        return this.OffscreenBuffer.getCurrent(0);
    }
    
    public TextureFBO getOffscreenBuffer(final int n) {
        return this.OffscreenBuffer.getCurrent(n);
    }
    
    public void setLastRenderedFBO(final TextureFBO fbOrendered) {
        this.OffscreenBuffer.FBOrendered = fbOrendered;
    }
    
    public void DoStartFrameStuff(final int n, final int n2, final float n3, final int n4) {
        this.DoStartFrameStuff(n, n2, n3, n4, false);
    }
    
    public void DoStartFrameStuff(final int n, final int n2, final float n3, final int n4, final boolean b) {
        this.DoStartFrameStuffInternal(n, n2, n3, n4, b, false, false);
    }
    
    public void DoEndFrameStuffFx(final int n, final int n2, final int n3) {
        GL11.glPopAttrib();
        --this.stack;
        GL11.glMatrixMode(5889);
        GL11.glPopMatrix();
        --this.stack;
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
    }
    
    public void DoStartFrameStuffSmartTextureFx(final int n, final int n2, final int n3) {
        this.DoStartFrameStuffInternal(n, n2, 1.0f, n3, false, true, true);
    }
    
    private void DoStartFrameStuffInternal(int n, int n2, final float n3, final int renderingPlayerIndex, final boolean b, final boolean b2, final boolean b3) {
        GL32.glEnable(3042);
        GL32.glDepthFunc(519);
        final int screenWidth = this.getScreenWidth();
        final int screenHeight = this.getScreenHeight();
        if (!b3 && !b2) {
            n = screenWidth;
        }
        if (!b3 && !b2) {
            n2 = screenHeight;
        }
        if (!b3 && renderingPlayerIndex != -1) {
            n /= ((IsoPlayer.numPlayers > 1) ? 2 : 1);
            n2 /= ((IsoPlayer.numPlayers > 2) ? 2 : 1);
        }
        GL32.glMatrixMode(5889);
        if (!b2) {
            while (this.stack > 0) {
                try {
                    GL11.glPopMatrix();
                    GL11.glPopAttrib();
                    this.stack -= 2;
                }
                catch (OpenGLException ex) {
                    int glGetInteger = GL11.glGetInteger(2992);
                    while (glGetInteger-- > 0) {
                        GL11.glPopAttrib();
                    }
                    int glGetInteger2 = GL11.glGetInteger(2980);
                    while (glGetInteger2-- > 1) {
                        GL11.glPopMatrix();
                    }
                    this.stack = 0;
                }
            }
        }
        GL11.glAlphaFunc(516, 0.0f);
        GL11.glPushAttrib(2048);
        ++this.stack;
        GL11.glPushMatrix();
        ++this.stack;
        GL11.glLoadIdentity();
        if (!b3 && !b) {
            GLU.gluOrtho2D(0.0f, n * n3, n2 * n3, 0.0f);
        }
        else {
            GLU.gluOrtho2D(0.0f, (float)n, (float)n2, 0.0f);
        }
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        if (renderingPlayerIndex != -1) {
            int n4 = n;
            int n5 = n2;
            int n6;
            int n7;
            if (b) {
                n6 = n;
                n7 = n2;
            }
            else {
                n6 = screenWidth;
                n7 = screenHeight;
                if (IsoPlayer.numPlayers > 1) {
                    n6 /= 2;
                }
                if (IsoPlayer.numPlayers > 2) {
                    n7 /= 2;
                }
            }
            if (b2) {
                n4 = n6;
                n5 = n7;
            }
            float n8 = 0.0f;
            final float n9 = (float)(n6 * (renderingPlayerIndex % 2));
            if (renderingPlayerIndex >= 2) {
                n8 += n7;
            }
            if (b) {
                n8 = getInstance().getScreenHeight() - n5 - n8;
            }
            GL11.glViewport((int)n9, (int)n8, n4, n5);
            GL11.glEnable(3089);
            GL11.glScissor((int)n9, (int)n8, n4, n5);
            SpriteRenderer.instance.setRenderingPlayerIndex(renderingPlayerIndex);
        }
        else {
            GL11.glViewport(0, 0, n, n2);
        }
    }
    
    public void DoPushIsoStuff(final float n, final float n2, final float n3, final float n4, final boolean b) {
        final float floatValue = getInstance().FloatParamMap.get(0);
        final float floatValue2 = getInstance().FloatParamMap.get(1);
        final float floatValue3 = getInstance().FloatParamMap.get(2);
        final double n5 = floatValue;
        final double n6 = floatValue2;
        final double n7 = floatValue3;
        final SpriteRenderState renderingState = SpriteRenderer.instance.getRenderingState();
        final PlayerCamera playerCamera = renderingState.playerCamera[renderingState.playerIndex];
        final float rightClickX = playerCamera.RightClickX;
        final float rightClickY = playerCamera.RightClickY;
        final float tOffX = playerCamera.getTOffX();
        final float tOffY = playerCamera.getTOffY();
        final float deferedX = playerCamera.DeferedX;
        final float deferedY = playerCamera.DeferedY;
        final double n8 = n5 - playerCamera.XToIso(-tOffX - rightClickX, -tOffY - rightClickY, 0.0f);
        final double n9 = n6 - playerCamera.YToIso(-tOffX - rightClickX, -tOffY - rightClickY, 0.0f);
        final double n10 = n8 + deferedX;
        final double n11 = n9 + deferedY;
        final double n12 = playerCamera.OffscreenWidth / 1920.0f;
        final double n13 = playerCamera.OffscreenHeight / 1920.0f;
        final Matrix4f tempMatrix4f = this.tempMatrix4f;
        tempMatrix4f.setOrtho(-(float)n12 / 2.0f, (float)n12 / 2.0f, -(float)n13 / 2.0f, (float)n13 / 2.0f, -10.0f, 10.0f);
        PZGLUtil.pushAndLoadMatrix(5889, tempMatrix4f);
        final Matrix4f tempMatrix4f2 = this.tempMatrix4f;
        final float n14 = (float)(2.0 / Math.sqrt(2048.0));
        tempMatrix4f2.scaling(0.047085002f);
        tempMatrix4f2.scale(Core.TileScale / 2.0f);
        tempMatrix4f2.rotate(0.5235988f, 1.0f, 0.0f, 0.0f);
        tempMatrix4f2.rotate(2.3561945f, 0.0f, 1.0f, 0.0f);
        tempMatrix4f2.translate(-(float)(n - n10), (float)(n3 - n7) * 2.5f, -(float)(n2 - n11));
        if (b) {
            tempMatrix4f2.scale(-1.0f, 1.0f, 1.0f);
        }
        else {
            tempMatrix4f2.scale(-1.5f, 1.5f, 1.5f);
        }
        tempMatrix4f2.rotate(n4 + 3.1415927f, 0.0f, 1.0f, 0.0f);
        if (!b) {
            tempMatrix4f2.translate(0.0f, -0.48f, 0.0f);
        }
        PZGLUtil.pushAndLoadMatrix(5888, tempMatrix4f2);
        GL11.glDepthRange(0.0, 1.0);
    }
    
    public void DoPushIsoParticleStuff(final float n, final float n2, final float n3) {
        GL11.glMatrixMode(5889);
        GL11.glPushMatrix();
        final float floatValue = getInstance().FloatParamMap.get(0);
        final float floatValue2 = getInstance().FloatParamMap.get(1);
        final float floatValue3 = getInstance().FloatParamMap.get(2);
        GL11.glLoadIdentity();
        final double n4 = floatValue;
        final double n5 = floatValue2;
        final double n6 = floatValue3;
        final double n7 = Math.abs(getInstance().getOffscreenWidth(0)) / 1920.0f;
        final double n8 = Math.abs(getInstance().getOffscreenHeight(0)) / 1080.0f;
        GL11.glLoadIdentity();
        GL11.glOrtho(-n7 / 2.0, n7 / 2.0, -n8 / 2.0, n8 / 2.0, -10.0, 10.0);
        GL11.glMatrixMode(5888);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glScaled(0.047085002064704895, 0.047085002064704895, 0.047085002064704895);
        GL11.glRotatef(62.65607f, 1.0f, 0.0f, 0.0f);
        GL11.glTranslated(0.0, -2.7200000286102295, 0.0);
        GL11.glRotatef(135.0f, 0.0f, 1.0f, 0.0f);
        GL11.glScalef(1.7099999f, 14.193f, 1.7099999f);
        GL11.glScalef(0.59f, 0.59f, 0.59f);
        GL11.glTranslated(-(n - n4), n3 - n6, -(n2 - n5));
        GL11.glDepthRange(0.0, 1.0);
    }
    
    public void DoPopIsoStuff() {
        GL11.glEnable(3008);
        GL11.glDepthFunc(519);
        GL11.glDepthMask(false);
        GL11.glMatrixMode(5889);
        GL11.glPopMatrix();
        GL11.glMatrixMode(5888);
        GL11.glPopMatrix();
    }
    
    public void DoEndFrameStuff(final int n, final int n2) {
        try {
            GL11.glPopAttrib();
            --this.stack;
            GL11.glMatrixMode(5889);
            GL11.glPopMatrix();
            --this.stack;
        }
        catch (Exception ex) {
            int glGetInteger = GL11.glGetInteger(2992);
            while (glGetInteger-- > 0) {
                GL11.glPopAttrib();
            }
            GL11.glMatrixMode(5889);
            int glGetInteger2 = GL11.glGetInteger(2980);
            while (glGetInteger2-- > 1) {
                GL11.glPopMatrix();
            }
            this.stack = 0;
        }
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        GL11.glDisable(3089);
    }
    
    public void RenderOffScreenBuffer() {
        if (LuaManager.thread != null && LuaManager.thread.bStep) {
            return;
        }
        if (this.OffscreenBuffer.Current == null) {
            return;
        }
        IndieGL.disableStencilTest();
        IndieGL.glDoStartFrame(Core.width, Core.height, 1.0f, -1);
        IndieGL.glDisable(3042);
        this.OffscreenBuffer.render();
        IndieGL.glDoEndFrame();
    }
    
    public void StartFrameText(final int n) {
        if (LuaManager.thread != null && LuaManager.thread.bStep) {
            return;
        }
        IndieGL.glDoStartFrame(IsoCamera.getScreenWidth(n), IsoCamera.getScreenHeight(n), 1.0f, n, true);
        this.frameStage = 2;
    }
    
    public boolean StartFrameUI() {
        if (LuaManager.thread != null && LuaManager.thread.bStep) {
            return false;
        }
        boolean b = true;
        if (UIManager.useUIFBO) {
            if (UIManager.defaultthread == LuaManager.debugthread) {
                this.UIRenderThisFrame = true;
            }
            else {
                this.UIRenderAccumulator += GameTime.getInstance().getMultiplier() / 1.6f;
                this.UIRenderThisFrame = (this.UIRenderAccumulator >= 30.0f / Core.OptionUIRenderFPS);
            }
            if (this.UIRenderThisFrame) {
                SpriteRenderer.instance.startOffscreenUI();
                SpriteRenderer.instance.glBuffer(2, 0);
            }
            else {
                b = false;
            }
        }
        IndieGL.glDoStartFrame(Core.width, Core.height, 1.0f, -1);
        IndieGL.glClear(1024);
        UIManager.resize();
        this.frameStage = 3;
        return b;
    }
    
    public Map<String, Integer> getKeyMaps() {
        return this.keyMaps;
    }
    
    public void setKeyMaps(final Map<String, Integer> keyMaps) {
        this.keyMaps = keyMaps;
    }
    
    public void reinitKeyMaps() {
        this.keyMaps = new HashMap<String, Integer>();
    }
    
    public int getKey(final String s) {
        if (this.keyMaps == null) {
            return 0;
        }
        if (this.keyMaps.get(s) != null) {
            return this.keyMaps.get(s);
        }
        return 0;
    }
    
    public void addKeyBinding(final String s, final Integer n) {
        if (this.keyMaps == null) {
            this.keyMaps = new HashMap<String, Integer>();
        }
        this.keyMaps.put(s, n);
    }
    
    public static boolean isLastStand() {
        return Core.bLastStand;
    }
    
    public String getVersionNumber() {
        return Core.gameVersion.toString();
    }
    
    public GameVersion getGameVersion() {
        return Core.gameVersion;
    }
    
    public String getSteamServerVersion() {
        return this.steamServerVersion;
    }
    
    public void DoFrameReady() {
        this.updateKeyboard();
    }
    
    public float getCurrentPlayerZoom() {
        return this.getZoom(IsoCamera.frameState.playerIndex);
    }
    
    public float getZoom(final int n) {
        if (this.OffscreenBuffer != null) {
            return this.OffscreenBuffer.zoom[n] * (Core.TileScale / 2.0f);
        }
        return 1.0f;
    }
    
    public float getNextZoom(final int n, final int n2) {
        if (this.OffscreenBuffer != null) {
            return this.OffscreenBuffer.getNextZoom(n, n2);
        }
        return 1.0f;
    }
    
    public float getMinZoom() {
        if (this.OffscreenBuffer != null) {
            return this.OffscreenBuffer.getMinZoom() * (Core.TileScale / 2.0f);
        }
        return 1.0f;
    }
    
    public float getMaxZoom() {
        if (this.OffscreenBuffer != null) {
            return this.OffscreenBuffer.getMaxZoom() * (Core.TileScale / 2.0f);
        }
        return 1.0f;
    }
    
    public void doZoomScroll(final int n, final int n2) {
        if (this.OffscreenBuffer != null) {
            this.OffscreenBuffer.doZoomScroll(n, n2);
        }
    }
    
    public String getSaveFolder() {
        return this.saveFolder;
    }
    
    public void setSaveFolder(final String saveFolder) {
        if (!this.saveFolder.equals(saveFolder)) {
            final File absoluteFile = new File(saveFolder).getAbsoluteFile();
            if (!absoluteFile.exists()) {
                absoluteFile.mkdir();
            }
            final File file = new File(absoluteFile, "mods");
            if (!file.exists()) {
                file.mkdir();
            }
            final String s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.saveFolder, File.separator);
            this.saveFolder = saveFolder;
            this.copyPasteFolders(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
            this.deleteDirectoryRecusrively(s);
        }
    }
    
    public void deleteDirectoryRecusrively(final String pathname) {
        final File file = new File(pathname);
        final String[] list = file.list();
        for (int i = 0; i < list.length; ++i) {
            final File file2 = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, pathname, File.separator, list[i]));
            if (file2.isDirectory()) {
                this.deleteDirectoryRecusrively(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, pathname, File.separator, list[i]));
            }
            else {
                file2.delete();
            }
        }
        file.delete();
    }
    
    public boolean getOptionZoom() {
        return Core.OptionZoom;
    }
    
    public void setOptionZoom(final boolean optionZoom) {
        Core.OptionZoom = optionZoom;
    }
    
    public void zoomOptionChanged(final boolean b) {
        if (!b) {
            Core.SafeMode = Core.SafeModeForced;
            this.OffscreenBuffer.bZoomEnabled = (Core.OptionZoom && !Core.SafeModeForced);
            return;
        }
        RenderThread.invokeOnRenderContext(() -> {
            if (!Core.OptionZoom || Core.SafeModeForced) {
                this.OffscreenBuffer.destroy();
                Core.SafeMode = true;
                this.bSupportsFBO = false;
                this.OffscreenBuffer.bZoomEnabled = false;
            }
            else {
                Core.SafeMode = false;
                this.bSupportsFBO = true;
                this.OffscreenBuffer.bZoomEnabled = true;
                this.supportsFBO();
            }
            return;
        });
        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Core.SafeMode ? "on" : "off"));
    }
    
    public void zoomLevelsChanged() {
        if (this.OffscreenBuffer.Current != null) {
            RenderThread.invokeOnRenderContext(() -> {
                this.OffscreenBuffer.destroy();
                this.zoomOptionChanged(true);
            });
        }
    }
    
    public boolean isZoomEnabled() {
        return this.OffscreenBuffer.bZoomEnabled;
    }
    
    public void initFBOs() {
        if (!Core.OptionZoom || Core.SafeModeForced) {
            Core.SafeMode = true;
            this.OffscreenBuffer.bZoomEnabled = false;
        }
        else {
            RenderThread.invokeOnRenderContext(this::supportsFBO);
        }
        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Core.SafeMode ? "on" : "off"));
    }
    
    public boolean getAutoZoom(final int n) {
        return Core.bAutoZoom[n];
    }
    
    public void setAutoZoom(final int n, final boolean b) {
        Core.bAutoZoom[n] = b;
        if (this.OffscreenBuffer != null) {
            this.OffscreenBuffer.bAutoZoom[n] = b;
        }
    }
    
    public boolean getOptionVSync() {
        return Core.OptionVSync;
    }
    
    public void setOptionVSync(final boolean b) {
        Core.OptionVSync = b;
        RenderThread.invokeOnRenderContext(() -> Display.setVSyncEnabled(b));
    }
    
    public int getOptionSoundVolume() {
        return Core.OptionSoundVolume;
    }
    
    public float getRealOptionSoundVolume() {
        return Core.OptionSoundVolume / 10.0f;
    }
    
    public void setOptionSoundVolume(final int b) {
        Core.OptionSoundVolume = Math.max(0, Math.min(10, b));
        if (GameClient.bClient && GameSounds.soundIsPaused) {
            return;
        }
        if (SoundManager.instance != null) {
            SoundManager.instance.setSoundVolume(b / 10.0f);
        }
    }
    
    public int getOptionMusicVolume() {
        return Core.OptionMusicVolume;
    }
    
    public void setOptionMusicVolume(final int b) {
        Core.OptionMusicVolume = Math.max(0, Math.min(10, b));
        if (GameClient.bClient && GameSounds.soundIsPaused) {
            return;
        }
        if (SoundManager.instance != null) {
            SoundManager.instance.setMusicVolume(b / 10.0f);
        }
    }
    
    public int getOptionAmbientVolume() {
        return Core.OptionAmbientVolume;
    }
    
    public void setOptionAmbientVolume(final int b) {
        Core.OptionAmbientVolume = Math.max(0, Math.min(10, b));
        if (GameClient.bClient && GameSounds.soundIsPaused) {
            return;
        }
        if (SoundManager.instance != null) {
            SoundManager.instance.setAmbientVolume(b / 10.0f);
        }
    }
    
    public int getOptionMusicLibrary() {
        return Core.OptionMusicLibrary;
    }
    
    public void setOptionMusicLibrary(int optionMusicLibrary) {
        if (optionMusicLibrary < 1) {
            optionMusicLibrary = 1;
        }
        if (optionMusicLibrary > 3) {
            optionMusicLibrary = 3;
        }
        Core.OptionMusicLibrary = optionMusicLibrary;
    }
    
    public int getOptionVehicleEngineVolume() {
        return Core.OptionVehicleEngineVolume;
    }
    
    public void setOptionVehicleEngineVolume(final int b) {
        Core.OptionVehicleEngineVolume = Math.max(0, Math.min(10, b));
        if (GameClient.bClient && GameSounds.soundIsPaused) {
            return;
        }
        if (SoundManager.instance != null) {
            SoundManager.instance.setVehicleEngineVolume(Core.OptionVehicleEngineVolume / 10.0f);
        }
    }
    
    public boolean getOptionVoiceEnable() {
        return Core.OptionVoiceEnable;
    }
    
    public void setOptionVoiceEnable(final boolean optionVoiceEnable) {
        Core.OptionVoiceEnable = optionVoiceEnable;
    }
    
    public int getOptionVoiceMode() {
        return Core.OptionVoiceMode;
    }
    
    public void setOptionVoiceMode(final int n) {
        Core.OptionVoiceMode = n;
        VoiceManager.instance.setMode(n);
    }
    
    public int getOptionVoiceVADMode() {
        return Core.OptionVoiceVADMode;
    }
    
    public void setOptionVoiceVADMode(final int n) {
        Core.OptionVoiceVADMode = n;
        VoiceManager.instance.setVADMode(n);
    }
    
    public int getOptionVoiceVolumeMic() {
        return Core.OptionVoiceVolumeMic;
    }
    
    public void setOptionVoiceVolumeMic(final int n) {
        Core.OptionVoiceVolumeMic = n;
        VoiceManager.instance.setVolumeMic(n);
    }
    
    public int getOptionVoiceVolumePlayers() {
        return Core.OptionVoiceVolumePlayers;
    }
    
    public void setOptionVoiceVolumePlayers(final int n) {
        Core.OptionVoiceVolumePlayers = n;
        VoiceManager.instance.setVolumePlayers(n);
    }
    
    public String getOptionVoiceRecordDeviceName() {
        return Core.OptionVoiceRecordDeviceName;
    }
    
    public void setOptionVoiceRecordDeviceName(final String optionVoiceRecordDeviceName) {
        Core.OptionVoiceRecordDeviceName = optionVoiceRecordDeviceName;
        VoiceManager.instance.UpdateRecordDevice();
    }
    
    public int getOptionVoiceRecordDevice() {
        if (Core.SoundDisabled || VoiceManager.VoipDisabled) {
            return 0;
        }
        for (int fmod_System_GetRecordNumDrivers = javafmod.FMOD_System_GetRecordNumDrivers(), i = 0; i < fmod_System_GetRecordNumDrivers; ++i) {
            final FMOD_DriverInfo fmod_DriverInfo = new FMOD_DriverInfo();
            javafmod.FMOD_System_GetRecordDriverInfo(i, fmod_DriverInfo);
            if (fmod_DriverInfo.name.equals(Core.OptionVoiceRecordDeviceName)) {
                return i + 1;
            }
        }
        return 0;
    }
    
    public void setOptionVoiceRecordDevice(final int n) {
        if (Core.SoundDisabled || VoiceManager.VoipDisabled) {
            return;
        }
        if (n < 1) {
            return;
        }
        final FMOD_DriverInfo fmod_DriverInfo = new FMOD_DriverInfo();
        javafmod.FMOD_System_GetRecordDriverInfo(n - 1, fmod_DriverInfo);
        Core.OptionVoiceRecordDeviceName = fmod_DriverInfo.name;
        VoiceManager.instance.UpdateRecordDevice();
    }
    
    public int getMicVolumeIndicator() {
        return VoiceManager.instance.getMicVolumeIndicator();
    }
    
    public boolean getMicVolumeError() {
        return VoiceManager.instance.getMicVolumeError();
    }
    
    public boolean getServerVOIPEnable() {
        return VoiceManager.instance.getServerVOIPEnable();
    }
    
    public void setTestingMicrophone(final boolean testingMicrophone) {
        VoiceManager.instance.setTestingMicrophone(testingMicrophone);
    }
    
    public int getOptionReloadDifficulty() {
        return 2;
    }
    
    public void setOptionReloadDifficulty(final int b) {
        Core.OptionReloadDifficulty = Math.max(1, Math.min(3, b));
    }
    
    public boolean getOptionRackProgress() {
        return Core.OptionRackProgress;
    }
    
    public void setOptionRackProgress(final boolean optionRackProgress) {
        Core.OptionRackProgress = optionRackProgress;
    }
    
    public int getOptionFontSize() {
        return Core.OptionFontSize;
    }
    
    public void setOptionFontSize(final int n) {
        Core.OptionFontSize = PZMath.clamp(n, 1, 5);
    }
    
    public String getOptionContextMenuFont() {
        return Core.OptionContextMenuFont;
    }
    
    public void setOptionContextMenuFont(final String optionContextMenuFont) {
        Core.OptionContextMenuFont = optionContextMenuFont;
    }
    
    public String getOptionInventoryFont() {
        return Core.OptionInventoryFont;
    }
    
    public void setOptionInventoryFont(final String optionInventoryFont) {
        Core.OptionInventoryFont = optionInventoryFont;
    }
    
    public String getOptionTooltipFont() {
        return Core.OptionTooltipFont;
    }
    
    public void setOptionTooltipFont(final String optionTooltipFont) {
        Core.OptionTooltipFont = optionTooltipFont;
        ObjectTooltip.checkFont();
    }
    
    public String getOptionMeasurementFormat() {
        return Core.OptionMeasurementFormat;
    }
    
    public void setOptionMeasurementFormat(final String optionMeasurementFormat) {
        Core.OptionMeasurementFormat = optionMeasurementFormat;
    }
    
    public int getOptionClockFormat() {
        return Core.OptionClockFormat;
    }
    
    public int getOptionClockSize() {
        return Core.OptionClockSize;
    }
    
    public void setOptionClockFormat(int optionClockFormat) {
        if (optionClockFormat < 1) {
            optionClockFormat = 1;
        }
        if (optionClockFormat > 2) {
            optionClockFormat = 2;
        }
        Core.OptionClockFormat = optionClockFormat;
    }
    
    public void setOptionClockSize(int optionClockSize) {
        if (optionClockSize < 1) {
            optionClockSize = 1;
        }
        if (optionClockSize > 2) {
            optionClockSize = 2;
        }
        Core.OptionClockSize = optionClockSize;
    }
    
    public boolean getOptionClock24Hour() {
        return Core.OptionClock24Hour;
    }
    
    public void setOptionClock24Hour(final boolean optionClock24Hour) {
        Core.OptionClock24Hour = optionClock24Hour;
    }
    
    public boolean getOptionModsEnabled() {
        return Core.OptionModsEnabled;
    }
    
    public void setOptionModsEnabled(final boolean optionModsEnabled) {
        Core.OptionModsEnabled = optionModsEnabled;
    }
    
    public int getOptionBloodDecals() {
        return Core.OptionBloodDecals;
    }
    
    public void setOptionBloodDecals(int optionBloodDecals) {
        if (optionBloodDecals < 0) {
            optionBloodDecals = 0;
        }
        if (optionBloodDecals > 10) {
            optionBloodDecals = 10;
        }
        Core.OptionBloodDecals = optionBloodDecals;
    }
    
    public boolean getOptionBorderlessWindow() {
        return Core.OptionBorderlessWindow;
    }
    
    public void setOptionBorderlessWindow(final boolean optionBorderlessWindow) {
        Core.OptionBorderlessWindow = optionBorderlessWindow;
    }
    
    public boolean getOptionLockCursorToWindow() {
        return Core.OptionLockCursorToWindow;
    }
    
    public void setOptionLockCursorToWindow(final boolean optionLockCursorToWindow) {
        Core.OptionLockCursorToWindow = optionLockCursorToWindow;
    }
    
    public boolean getOptionTextureCompression() {
        return Core.OptionTextureCompression;
    }
    
    public void setOptionTextureCompression(final boolean optionTextureCompression) {
        Core.OptionTextureCompression = optionTextureCompression;
    }
    
    public boolean getOptionTexture2x() {
        return Core.OptionTexture2x;
    }
    
    public void setOptionTexture2x(final boolean optionTexture2x) {
        Core.OptionTexture2x = optionTexture2x;
    }
    
    public boolean getOptionModelTextureMipmaps() {
        return Core.OptionModelTextureMipmaps;
    }
    
    public void setOptionModelTextureMipmaps(final boolean optionModelTextureMipmaps) {
        Core.OptionModelTextureMipmaps = optionModelTextureMipmaps;
    }
    
    public String getOptionZoomLevels1x() {
        return Core.OptionZoomLevels1x;
    }
    
    public void setOptionZoomLevels1x(final String s) {
        Core.OptionZoomLevels1x = ((s == null) ? "" : s);
    }
    
    public String getOptionZoomLevels2x() {
        return Core.OptionZoomLevels2x;
    }
    
    public void setOptionZoomLevels2x(final String s) {
        Core.OptionZoomLevels2x = ((s == null) ? "" : s);
    }
    
    public ArrayList<Integer> getDefaultZoomLevels() {
        return this.OffscreenBuffer.getDefaultZoomLevels();
    }
    
    public void setOptionActiveController(final int n, final boolean b) {
        if (n < 0 || n >= GameWindow.GameInput.getControllerCount()) {
            return;
        }
        final Controller controller = GameWindow.GameInput.getController(n);
        if (controller != null) {
            JoypadManager.instance.setControllerActive(controller.getGUID(), b);
        }
    }
    
    public boolean getOptionActiveController(final String o) {
        return JoypadManager.instance.ActiveControllerGUIDs.contains(o);
    }
    
    public boolean isOptionShowChatTimestamp() {
        return Core.OptionShowChatTimestamp;
    }
    
    public void setOptionShowChatTimestamp(final boolean optionShowChatTimestamp) {
        Core.OptionShowChatTimestamp = optionShowChatTimestamp;
    }
    
    public boolean isOptionShowChatTitle() {
        return Core.OptionShowChatTitle;
    }
    
    public String getOptionChatFontSize() {
        return Core.OptionChatFontSize;
    }
    
    public void setOptionChatFontSize(final String optionChatFontSize) {
        Core.OptionChatFontSize = optionChatFontSize;
    }
    
    public void setOptionShowChatTitle(final boolean optionShowChatTitle) {
        Core.OptionShowChatTitle = optionShowChatTitle;
    }
    
    public float getOptionMinChatOpaque() {
        return Core.OptionMinChatOpaque;
    }
    
    public void setOptionMinChatOpaque(final float optionMinChatOpaque) {
        Core.OptionMinChatOpaque = optionMinChatOpaque;
    }
    
    public float getOptionMaxChatOpaque() {
        return Core.OptionMaxChatOpaque;
    }
    
    public void setOptionMaxChatOpaque(final float optionMaxChatOpaque) {
        Core.OptionMaxChatOpaque = optionMaxChatOpaque;
    }
    
    public float getOptionChatFadeTime() {
        return Core.OptionChatFadeTime;
    }
    
    public void setOptionChatFadeTime(final float optionChatFadeTime) {
        Core.OptionChatFadeTime = optionChatFadeTime;
    }
    
    public boolean getOptionChatOpaqueOnFocus() {
        return Core.OptionChatOpaqueOnFocus;
    }
    
    public void setOptionChatOpaqueOnFocus(final boolean optionChatOpaqueOnFocus) {
        Core.OptionChatOpaqueOnFocus = optionChatOpaqueOnFocus;
    }
    
    public boolean getOptionUIFBO() {
        return Core.OptionUIFBO;
    }
    
    public void setOptionUIFBO(final boolean optionUIFBO) {
        Core.OptionUIFBO = optionUIFBO;
        if (GameWindow.states.current == IngameState.instance) {
            UIManager.useUIFBO = (getInstance().supportsFBO() && Core.OptionUIFBO);
        }
    }
    
    public int getOptionAimOutline() {
        return Core.OptionAimOutline;
    }
    
    public void setOptionAimOutline(final int n) {
        Core.OptionAimOutline = PZMath.clamp(n, 1, 3);
    }
    
    public int getOptionUIRenderFPS() {
        return Core.OptionUIRenderFPS;
    }
    
    public void setOptionUIRenderFPS(final int optionUIRenderFPS) {
        Core.OptionUIRenderFPS = optionUIRenderFPS;
    }
    
    public void setOptionRadialMenuKeyToggle(final boolean optionRadialMenuKeyToggle) {
        Core.OptionRadialMenuKeyToggle = optionRadialMenuKeyToggle;
    }
    
    public boolean getOptionRadialMenuKeyToggle() {
        return Core.OptionRadialMenuKeyToggle;
    }
    
    public void setOptionReloadRadialInstant(final boolean optionReloadRadialInstant) {
        Core.OptionReloadRadialInstant = optionReloadRadialInstant;
    }
    
    public boolean getOptionReloadRadialInstant() {
        return Core.OptionReloadRadialInstant;
    }
    
    public void setOptionPanCameraWhileAiming(final boolean optionPanCameraWhileAiming) {
        Core.OptionPanCameraWhileAiming = optionPanCameraWhileAiming;
    }
    
    public boolean getOptionPanCameraWhileAiming() {
        return Core.OptionPanCameraWhileAiming;
    }
    
    public void setOptionPanCameraWhileDriving(final boolean optionPanCameraWhileDriving) {
        Core.OptionPanCameraWhileDriving = optionPanCameraWhileDriving;
    }
    
    public boolean getOptionPanCameraWhileDriving() {
        return Core.OptionPanCameraWhileDriving;
    }
    
    public String getOptionCycleContainerKey() {
        return Core.OptionCycleContainerKey;
    }
    
    public void setOptionCycleContainerKey(final String optionCycleContainerKey) {
        Core.OptionCycleContainerKey = optionCycleContainerKey;
    }
    
    public boolean getOptionDropItemsOnSquareCenter() {
        return Core.OptionDropItemsOnSquareCenter;
    }
    
    public void setOptionDropItemsOnSquareCenter(final boolean optionDropItemsOnSquareCenter) {
        Core.OptionDropItemsOnSquareCenter = optionDropItemsOnSquareCenter;
    }
    
    public boolean getOptionTimedActionGameSpeedReset() {
        return Core.OptionTimedActionGameSpeedReset;
    }
    
    public void setOptionTimedActionGameSpeedReset(final boolean optionTimedActionGameSpeedReset) {
        Core.OptionTimedActionGameSpeedReset = optionTimedActionGameSpeedReset;
    }
    
    public int getOptionShoulderButtonContainerSwitch() {
        return Core.OptionShoulderButtonContainerSwitch;
    }
    
    public void setOptionShoulderButtonContainerSwitch(final int optionShoulderButtonContainerSwitch) {
        Core.OptionShoulderButtonContainerSwitch = optionShoulderButtonContainerSwitch;
    }
    
    public boolean getOptionSingleContextMenu(final int n) {
        return Core.OptionSingleContextMenu[n];
    }
    
    public void setOptionSingleContextMenu(final int n, final boolean b) {
        Core.OptionSingleContextMenu[n] = b;
    }
    
    public boolean getOptionAutoDrink() {
        return Core.OptionAutoDrink;
    }
    
    public void setOptionAutoDrink(final boolean optionAutoDrink) {
        Core.OptionAutoDrink = optionAutoDrink;
    }
    
    public boolean getOptionCorpseShadows() {
        return Core.OptionCorpseShadows;
    }
    
    public void setOptionCorpseShadows(final boolean optionCorpseShadows) {
        Core.OptionCorpseShadows = optionCorpseShadows;
    }
    
    public boolean getOptionLeaveKeyInIgnition() {
        return Core.OptionLeaveKeyInIgnition;
    }
    
    public void setOptionLeaveKeyInIgnition(final boolean optionLeaveKeyInIgnition) {
        Core.OptionLeaveKeyInIgnition = optionLeaveKeyInIgnition;
    }
    
    public int getOptionSearchModeOverlayEffect() {
        return Core.OptionSearchModeOverlayEffect;
    }
    
    public void setOptionSearchModeOverlayEffect(final int optionSearchModeOverlayEffect) {
        Core.OptionSearchModeOverlayEffect = optionSearchModeOverlayEffect;
    }
    
    public int getOptionSimpleClothingTextures() {
        return Core.OptionSimpleClothingTextures;
    }
    
    public void setOptionSimpleClothingTextures(final int n) {
        Core.OptionSimpleClothingTextures = PZMath.clamp(n, 1, 3);
    }
    
    public boolean isOptionSimpleClothingTextures(final boolean b) {
        switch (Core.OptionSimpleClothingTextures) {
            case 1: {
                return false;
            }
            case 2: {
                return b;
            }
            default: {
                return true;
            }
        }
    }
    
    public boolean getOptionSimpleWeaponTextures() {
        return Core.OptionSimpleWeaponTextures;
    }
    
    public void setOptionSimpleWeaponTextures(final boolean optionSimpleWeaponTextures) {
        Core.OptionSimpleWeaponTextures = optionSimpleWeaponTextures;
    }
    
    public int getOptionIgnoreProneZombieRange() {
        return Core.OptionIgnoreProneZombieRange;
    }
    
    public void setOptionIgnoreProneZombieRange(final int n) {
        Core.OptionIgnoreProneZombieRange = PZMath.clamp(n, 1, 5);
    }
    
    public float getIgnoreProneZombieRange() {
        switch (Core.OptionIgnoreProneZombieRange) {
            case 1: {
                return -1.0f;
            }
            case 2: {
                return 1.5f;
            }
            case 3: {
                return 2.0f;
            }
            case 4: {
                return 2.5f;
            }
            case 5: {
                return 3.0f;
            }
            default: {
                return -1.0f;
            }
        }
    }
    
    private void readPerPlayerBoolean(final String s, final boolean[] a) {
        Arrays.fill(a, false);
        final String[] split = s.split(",");
        for (int n = 0; n < split.length && n != 4; ++n) {
            a[n] = StringUtils.tryParseBoolean(split[n]);
        }
    }
    
    private String getPerPlayerBooleanString(final boolean[] array) {
        return String.format("%b,%b,%b,%b", array[0], array[1], array[2], array[3]);
    }
    
    @Deprecated
    public void ResetLua(final boolean b, final String s) throws IOException {
        this.ResetLua("default", s);
    }
    
    public void ResetLua(final String s, final String s2) throws IOException {
        if (SpriteRenderer.instance != null) {
            GameWindow.DrawReloadingLua = true;
            GameWindow.render();
            GameWindow.DrawReloadingLua = false;
        }
        RenderThread.setWaitForRenderState(false);
        SpriteRenderer.instance.notifyRenderStateQueue();
        ScriptManager.instance.Reset();
        ClothingDecals.Reset();
        BeardStyles.Reset();
        HairStyles.Reset();
        OutfitManager.Reset();
        AnimationSet.Reset();
        GameSounds.Reset();
        VehicleType.Reset();
        LuaEventManager.Reset();
        MapObjects.Reset();
        UIManager.init();
        SurvivorFactory.Reset();
        ProfessionFactory.Reset();
        TraitFactory.Reset();
        ChooseGameInfo.Reset();
        AttachedLocations.Reset();
        BodyLocations.Reset();
        ContainerOverlays.instance.Reset();
        BentFences.getInstance().Reset();
        BrokenFences.getInstance().Reset();
        TileOverlays.instance.Reset();
        LuaHookManager.Reset();
        CustomPerks.Reset();
        PerkFactory.Reset();
        CustomSandboxOptions.Reset();
        SandboxOptions.Reset();
        WorldMap.Reset();
        LuaManager.init();
        JoypadManager.instance.Reset();
        GameKeyboard.doLuaKeyPressed = true;
        Texture.nullTextures.clear();
        ZomboidFileSystem.instance.Reset();
        ZomboidFileSystem.instance.init();
        ZomboidFileSystem.instance.loadMods(s);
        ZomboidFileSystem.instance.loadModPackFiles();
        ModelManager.instance.loadModAnimations();
        Languages.instance.init();
        Translator.loadFiles();
        CustomPerks.instance.init();
        CustomPerks.instance.initLua();
        CustomSandboxOptions.instance.init();
        CustomSandboxOptions.instance.initInstance(SandboxOptions.instance);
        ScriptManager.instance.Load();
        ClothingDecals.init();
        BeardStyles.init();
        HairStyles.init();
        OutfitManager.init();
        try {
            TextManager.instance.Init();
            LuaManager.LoadDirBase();
        }
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
            GameWindow.DoLoadingText("Reloading Lua - ERRORS!");
            try {
                Thread.sleep(2000L);
            }
            catch (InterruptedException ex2) {}
        }
        ZomboidGlobals.Load();
        RenderThread.setWaitForRenderState(true);
        LuaEventManager.triggerEvent("OnGameBoot");
        LuaEventManager.triggerEvent("OnMainMenuEnter");
        LuaEventManager.triggerEvent("OnResetLua", s2);
    }
    
    public void DelayResetLua(final String delayResetLua_activeMods, final String delayResetLua_reason) {
        this.m_delayResetLua_activeMods = delayResetLua_activeMods;
        this.m_delayResetLua_reason = delayResetLua_reason;
    }
    
    public void CheckDelayResetLua() throws IOException {
        if (this.m_delayResetLua_activeMods != null) {
            final String delayResetLua_activeMods = this.m_delayResetLua_activeMods;
            final String delayResetLua_reason = this.m_delayResetLua_reason;
            this.m_delayResetLua_activeMods = null;
            this.m_delayResetLua_reason = null;
            this.ResetLua(delayResetLua_activeMods, delayResetLua_reason);
        }
    }
    
    public boolean isShowPing() {
        return this.showPing;
    }
    
    public void setShowPing(final boolean showPing) {
        this.showPing = showPing;
    }
    
    public boolean isForceSnow() {
        return this.forceSnow;
    }
    
    public void setForceSnow(final boolean forceSnow) {
        this.forceSnow = forceSnow;
    }
    
    public boolean isZombieGroupSound() {
        return this.zombieGroupSound;
    }
    
    public void setZombieGroupSound(final boolean zombieGroupSound) {
        this.zombieGroupSound = zombieGroupSound;
    }
    
    public String getBlinkingMoodle() {
        return this.blinkingMoodle;
    }
    
    public void setBlinkingMoodle(final String blinkingMoodle) {
        this.blinkingMoodle = blinkingMoodle;
    }
    
    public boolean isTutorialDone() {
        return this.tutorialDone;
    }
    
    public void setTutorialDone(final boolean tutorialDone) {
        this.tutorialDone = tutorialDone;
    }
    
    public boolean isVehiclesWarningShow() {
        return this.vehiclesWarningShow;
    }
    
    public void setVehiclesWarningShow(final boolean vehiclesWarningShow) {
        this.vehiclesWarningShow = vehiclesWarningShow;
    }
    
    public void initPoisonousBerry() {
        final ArrayList<String> list = new ArrayList<String>();
        list.add("Base.BerryGeneric1");
        list.add("Base.BerryGeneric2");
        list.add("Base.BerryGeneric3");
        list.add("Base.BerryGeneric4");
        list.add("Base.BerryGeneric5");
        list.add("Base.BerryPoisonIvy");
        this.setPoisonousBerry(list.get(Rand.Next(0, list.size() - 1)));
    }
    
    public void initPoisonousMushroom() {
        final ArrayList<String> list = new ArrayList<String>();
        list.add("Base.MushroomGeneric1");
        list.add("Base.MushroomGeneric2");
        list.add("Base.MushroomGeneric3");
        list.add("Base.MushroomGeneric4");
        list.add("Base.MushroomGeneric5");
        list.add("Base.MushroomGeneric6");
        list.add("Base.MushroomGeneric7");
        this.setPoisonousMushroom(list.get(Rand.Next(0, list.size() - 1)));
    }
    
    public String getPoisonousBerry() {
        return this.poisonousBerry;
    }
    
    public void setPoisonousBerry(final String poisonousBerry) {
        this.poisonousBerry = poisonousBerry;
    }
    
    public String getPoisonousMushroom() {
        return this.poisonousMushroom;
    }
    
    public void setPoisonousMushroom(final String poisonousMushroom) {
        this.poisonousMushroom = poisonousMushroom;
    }
    
    public static String getDifficulty() {
        return Core.difficulty;
    }
    
    public static void setDifficulty(final String difficulty) {
        Core.difficulty = difficulty;
    }
    
    public boolean isDoneNewSaveFolder() {
        return this.doneNewSaveFolder;
    }
    
    public void setDoneNewSaveFolder(final boolean doneNewSaveFolder) {
        this.doneNewSaveFolder = doneNewSaveFolder;
    }
    
    public static int getTileScale() {
        return Core.TileScale;
    }
    
    public boolean isSelectingAll() {
        return this.isSelectingAll;
    }
    
    public void setIsSelectingAll(final boolean isSelectingAll) {
        this.isSelectingAll = isSelectingAll;
    }
    
    public boolean getContentTranslationsEnabled() {
        return Core.OptionEnableContentTranslations;
    }
    
    public void setContentTranslationsEnabled(final boolean optionEnableContentTranslations) {
        Core.OptionEnableContentTranslations = optionEnableContentTranslations;
    }
    
    public boolean isShowYourUsername() {
        return this.showYourUsername;
    }
    
    public void setShowYourUsername(final boolean showYourUsername) {
        this.showYourUsername = showYourUsername;
    }
    
    public ColorInfo getMpTextColor() {
        if (this.mpTextColor == null) {
            this.mpTextColor = new ColorInfo((Rand.Next(135) + 120) / 255.0f, (Rand.Next(135) + 120) / 255.0f, (Rand.Next(135) + 120) / 255.0f, 1.0f);
        }
        return this.mpTextColor;
    }
    
    public void setMpTextColor(final ColorInfo mpTextColor) {
        if (mpTextColor.r < 0.19f) {
            mpTextColor.r = 0.19f;
        }
        if (mpTextColor.g < 0.19f) {
            mpTextColor.g = 0.19f;
        }
        if (mpTextColor.b < 0.19f) {
            mpTextColor.b = 0.19f;
        }
        this.mpTextColor = mpTextColor;
    }
    
    public boolean isAzerty() {
        return this.isAzerty;
    }
    
    public void setAzerty(final boolean isAzerty) {
        this.isAzerty = isAzerty;
    }
    
    public ColorInfo getObjectHighlitedColor() {
        return this.objectHighlitedColor;
    }
    
    public void setObjectHighlitedColor(final ColorInfo colorInfo) {
        this.objectHighlitedColor.set(colorInfo);
    }
    
    public String getSeenUpdateText() {
        return this.seenUpdateText;
    }
    
    public void setSeenUpdateText(final String seenUpdateText) {
        this.seenUpdateText = seenUpdateText;
    }
    
    public boolean isToggleToAim() {
        return this.toggleToAim;
    }
    
    public void setToggleToAim(final boolean toggleToAim) {
        this.toggleToAim = toggleToAim;
    }
    
    public boolean isToggleToRun() {
        return this.toggleToRun;
    }
    
    public void setToggleToRun(final boolean toggleToRun) {
        this.toggleToRun = toggleToRun;
    }
    
    public int getXAngle(final int n, final float n2) {
        return new Long(Math.round((Math.sqrt(2.0) * Math.cos(Math.toRadians(225.0f + n2)) + 1.0) * (n / 2))).intValue();
    }
    
    public int getYAngle(final int n, final float n2) {
        return new Long(Math.round((Math.sqrt(2.0) * Math.sin(Math.toRadians(225.0f + n2)) + 1.0) * (n / 2))).intValue();
    }
    
    public boolean isCelsius() {
        return this.celsius;
    }
    
    public void setCelsius(final boolean celsius) {
        this.celsius = celsius;
    }
    
    public boolean isInDebug() {
        return Core.bDebug;
    }
    
    public boolean isRiversideDone() {
        return this.riversideDone;
    }
    
    public void setRiversideDone(final boolean riversideDone) {
        this.riversideDone = riversideDone;
    }
    
    public boolean isNoSave() {
        return this.noSave;
    }
    
    public void setNoSave(final boolean noSave) {
        this.noSave = noSave;
    }
    
    public boolean isShowFirstTimeVehicleTutorial() {
        return this.showFirstTimeVehicleTutorial;
    }
    
    public void setShowFirstTimeVehicleTutorial(final boolean showFirstTimeVehicleTutorial) {
        this.showFirstTimeVehicleTutorial = showFirstTimeVehicleTutorial;
    }
    
    public boolean getOptionDisplayAsCelsius() {
        return Core.OptionTemperatureDisplayCelsius;
    }
    
    public void setOptionDisplayAsCelsius(final boolean optionTemperatureDisplayCelsius) {
        Core.OptionTemperatureDisplayCelsius = optionTemperatureDisplayCelsius;
    }
    
    public boolean isShowFirstTimeWeatherTutorial() {
        return this.showFirstTimeWeatherTutorial;
    }
    
    public void setShowFirstTimeWeatherTutorial(final boolean showFirstTimeWeatherTutorial) {
        this.showFirstTimeWeatherTutorial = showFirstTimeWeatherTutorial;
    }
    
    public boolean getOptionDoWindSpriteEffects() {
        return Core.OptionDoWindSpriteEffects;
    }
    
    public void setOptionDoWindSpriteEffects(final boolean optionDoWindSpriteEffects) {
        Core.OptionDoWindSpriteEffects = optionDoWindSpriteEffects;
    }
    
    public boolean getOptionDoDoorSpriteEffects() {
        return Core.OptionDoDoorSpriteEffects;
    }
    
    public void setOptionDoDoorSpriteEffects(final boolean optionDoDoorSpriteEffects) {
        Core.OptionDoDoorSpriteEffects = optionDoDoorSpriteEffects;
    }
    
    public void setOptionUpdateSneakButton(final boolean optionUpdateSneakButton) {
        Core.OptionUpdateSneakButton = optionUpdateSneakButton;
    }
    
    public boolean getOptionUpdateSneakButton() {
        return Core.OptionUpdateSneakButton;
    }
    
    public boolean isNewReloading() {
        return this.newReloading;
    }
    
    public void setNewReloading(final boolean newReloading) {
        this.newReloading = newReloading;
    }
    
    public boolean isShowFirstTimeSneakTutorial() {
        return this.showFirstTimeSneakTutorial;
    }
    
    public void setShowFirstTimeSneakTutorial(final boolean showFirstTimeSneakTutorial) {
        this.showFirstTimeSneakTutorial = showFirstTimeSneakTutorial;
    }
    
    public void setOptiondblTapJogToSprint(final boolean optiondblTapJogToSprint) {
        Core.OptiondblTapJogToSprint = optiondblTapJogToSprint;
    }
    
    public boolean isOptiondblTapJogToSprint() {
        return Core.OptiondblTapJogToSprint;
    }
    
    public boolean isToggleToSprint() {
        return this.toggleToSprint;
    }
    
    public void setToggleToSprint(final boolean toggleToSprint) {
        this.toggleToSprint = toggleToSprint;
    }
    
    public int getIsoCursorVisibility() {
        return this.isoCursorVisibility;
    }
    
    public void setIsoCursorVisibility(final int isoCursorVisibility) {
        this.isoCursorVisibility = isoCursorVisibility;
    }
    
    public boolean getOptionShowCursorWhileAiming() {
        return Core.OptionShowCursorWhileAiming;
    }
    
    public void setOptionShowCursorWhileAiming(final boolean optionShowCursorWhileAiming) {
        Core.OptionShowCursorWhileAiming = optionShowCursorWhileAiming;
    }
    
    public boolean gotNewBelt() {
        return this.gotNewBelt;
    }
    
    public void setGotNewBelt(final boolean gotNewBelt) {
        this.gotNewBelt = gotNewBelt;
    }
    
    public void setAnimPopupDone(final boolean bAnimPopupDone) {
        this.bAnimPopupDone = bAnimPopupDone;
    }
    
    public boolean isAnimPopupDone() {
        return this.bAnimPopupDone;
    }
    
    public void setModsPopupDone(final boolean bModsPopupDone) {
        this.bModsPopupDone = bModsPopupDone;
    }
    
    public boolean isModsPopupDone() {
        return this.bModsPopupDone;
    }
    
    public boolean isRenderPrecipIndoors() {
        return Core.OptionRenderPrecipIndoors;
    }
    
    public void setRenderPrecipIndoors(final boolean optionRenderPrecipIndoors) {
        Core.OptionRenderPrecipIndoors = optionRenderPrecipIndoors;
    }
    
    public boolean isCollideZombies() {
        return this.collideZombies;
    }
    
    public void setCollideZombies(final boolean collideZombies) {
        this.collideZombies = collideZombies;
    }
    
    public boolean isFlashIsoCursor() {
        return this.flashIsoCursor;
    }
    
    public void setFlashIsoCursor(final boolean flashIsoCursor) {
        this.flashIsoCursor = flashIsoCursor;
    }
    
    public boolean isOptionProgressBar() {
        return true;
    }
    
    public void setOptionProgressBar(final boolean optionProgressBar) {
        Core.OptionProgressBar = optionProgressBar;
    }
    
    public void setOptionLanguageName(final String optionLanguageName) {
        Core.OptionLanguageName = optionLanguageName;
    }
    
    public String getOptionLanguageName() {
        return Core.OptionLanguageName;
    }
    
    public int getOptionRenderPrecipitation() {
        return Core.OptionRenderPrecipitation;
    }
    
    public void setOptionRenderPrecipitation(final int optionRenderPrecipitation) {
        Core.OptionRenderPrecipitation = optionRenderPrecipitation;
    }
    
    public void setOptionAutoProneAtk(final boolean optionAutoProneAtk) {
        Core.OptionAutoProneAtk = optionAutoProneAtk;
    }
    
    public boolean isOptionAutoProneAtk() {
        return Core.OptionAutoProneAtk;
    }
    
    public void setOption3DGroundItem(final boolean option3DGroundItem) {
        Core.Option3DGroundItem = option3DGroundItem;
    }
    
    public boolean isOption3DGroundItem() {
        return Core.Option3DGroundItem;
    }
    
    public Object getOptionOnStartup(final String key) {
        return Core.optionsOnStartup.get(key);
    }
    
    public void setOptionOnStartup(final String key, final Object value) {
        Core.optionsOnStartup.put(key, value);
    }
    
    public void countMissing3DItems() {
        final ArrayList<Item> allItems = ScriptManager.instance.getAllItems();
        int n = 0;
        for (final Item item : allItems) {
            if (item.type != Item.Type.Weapon && item.type != Item.Type.Moveable && !item.name.contains("ZedDmg") && !item.name.contains("Wound") && !item.name.contains("MakeUp") && !item.name.contains("Bandage") && !item.name.contains("Hat") && !item.getObsolete() && StringUtils.isNullOrEmpty(item.worldObjectSprite) && StringUtils.isNullOrEmpty(item.worldStaticModel)) {
                System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, item.name));
                ++n;
            }
        }
        System.out.println(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, n, allItems.size()));
    }
    
    static {
        Core.fakefullscreen = false;
        gameVersion = new GameVersion(41, 65, "");
        Core.bMultithreadedRendering = true;
        Core.bAltMoveMethod = false;
        Core.OptionShowCursorWhileAiming = false;
        Core.OptionZoom = true;
        Core.OptionModsEnabled = true;
        Core.OptionFontSize = 1;
        Core.OptionContextMenuFont = "Medium";
        Core.OptionInventoryFont = "Medium";
        Core.OptionTooltipFont = "Small";
        Core.OptionMeasurementFormat = "Metric";
        Core.OptionClockFormat = 1;
        Core.OptionClockSize = 2;
        Core.OptionClock24Hour = true;
        Core.OptionVSync = false;
        Core.OptionSoundVolume = 8;
        Core.OptionMusicVolume = 6;
        Core.OptionAmbientVolume = 5;
        Core.OptionMusicLibrary = 1;
        Core.OptionVoiceEnable = true;
        Core.OptionVoiceMode = 3;
        Core.OptionVoiceVADMode = 3;
        Core.OptionVoiceRecordDeviceName = "";
        Core.OptionVoiceVolumeMic = 10;
        Core.OptionVoiceVolumePlayers = 5;
        Core.OptionVehicleEngineVolume = 5;
        Core.OptionReloadDifficulty = 2;
        Core.OptionRackProgress = true;
        Core.OptionBloodDecals = 10;
        Core.OptionBorderlessWindow = false;
        Core.OptionLockCursorToWindow = false;
        Core.OptionTextureCompression = false;
        Core.OptionModelTextureMipmaps = false;
        Core.OptionTexture2x = true;
        Core.OptionZoomLevels1x = "";
        Core.OptionZoomLevels2x = "";
        Core.OptionEnableContentTranslations = true;
        Core.OptionUIFBO = true;
        Core.OptionUIRenderFPS = 20;
        Core.OptionRadialMenuKeyToggle = true;
        Core.OptionReloadRadialInstant = false;
        Core.OptionPanCameraWhileAiming = true;
        Core.OptionPanCameraWhileDriving = false;
        Core.OptionShowChatTimestamp = false;
        Core.OptionShowChatTitle = false;
        Core.OptionChatFontSize = "medium";
        Core.OptionMinChatOpaque = 1.0f;
        Core.OptionMaxChatOpaque = 1.0f;
        Core.OptionChatFadeTime = 0.0f;
        Core.OptionChatOpaqueOnFocus = true;
        Core.OptionTemperatureDisplayCelsius = false;
        Core.OptionDoWindSpriteEffects = true;
        Core.OptionDoDoorSpriteEffects = true;
        Core.OptionRenderPrecipIndoors = true;
        Core.OptionAutoProneAtk = true;
        Core.Option3DGroundItem = true;
        Core.OptionRenderPrecipitation = 1;
        Core.OptionUpdateSneakButton = true;
        Core.OptiondblTapJogToSprint = false;
        Core.OptionAimOutline = 1;
        Core.OptionCycleContainerKey = "shift";
        Core.OptionDropItemsOnSquareCenter = false;
        Core.OptionTimedActionGameSpeedReset = false;
        Core.OptionShoulderButtonContainerSwitch = 1;
        Core.OptionProgressBar = false;
        Core.OptionLanguageName = null;
        OptionSingleContextMenu = new boolean[4];
        Core.OptionCorpseShadows = true;
        Core.OptionSimpleClothingTextures = 1;
        Core.OptionSimpleWeaponTextures = false;
        Core.OptionAutoDrink = true;
        Core.OptionLeaveKeyInIgnition = false;
        Core.OptionSearchModeOverlayEffect = 1;
        Core.OptionIgnoreProneZombieRange = 2;
        Core.difficulty = "Hardcore";
        Core.TileScale = 2;
        Core.blinkAlpha = 1.0f;
        Core.blinkAlphaIncrease = false;
        Core.optionsOnStartup = new HashMap<String, Object>();
        Core.width = 1280;
        Core.height = 720;
        Core.MaxJukeBoxesActive = 10;
        Core.NumJukeBoxesActive = 0;
        Core.GameMode = "Sandbox";
        Core.glMajorVersion = -1;
        Core.core = new Core();
        Core.bDebug = false;
        Core.CurrentTextEntryBox = null;
        Core.fullScreen = false;
        bAutoZoom = new boolean[4];
        Core.GameMap = "DEFAULT";
        Core.GameSaveWorld = "";
        Core.SafeMode = false;
        Core.SafeModeForced = false;
        Core.SoundDisabled = false;
        Core.xx = 0;
        Core.yy = 0;
        Core.zz = 0;
        Core.bLastStand = false;
        Core.ChallengeID = null;
        Core.bLoadedWithMultithreaded = false;
        Core.bExiting = false;
    }
}

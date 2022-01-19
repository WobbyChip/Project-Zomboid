// 
// Decompiled by Procyon v0.5.36
// 

package zombie;

import zombie.core.profiling.PerformanceProfileProbe;
import zombie.core.profiling.PerformanceProfileFrameProbe;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.CharBuffer;
import zombie.fileSystem.FileSystemImpl;
import java.util.function.Supplier;
import java.nio.ByteBuffer;
import zombie.worldMap.WorldMapVisited;
import zombie.inventory.types.MapItem;
import zombie.world.moddata.GlobalModData;
import zombie.radio.ZomboidRadio;
import zombie.globalObjects.SGlobalObjects;
import zombie.savefile.SavefileThumbnail;
import zombie.iso.SliceY;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import zombie.sandbox.CustomSandboxOptions;
import zombie.core.Languages;
import zombie.core.physics.Bullet;
import zombie.worldMap.WorldMapJNI;
import zombie.vehicles.Clipper;
import zombie.util.PZSQLUtils;
import zombie.core.raknet.RakNetPeerInterface;
import zombie.core.znet.SteamWorkshop;
import zombie.core.znet.SteamFriends;
import zombie.core.znet.ServerBrowser;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import zombie.gameStates.GameLoadingState;
import java.io.FileInputStream;
import zombie.ui.TextManager;
import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;
import java.util.Map;
import zombie.core.logger.ZipLogs;
import zombie.vehicles.PolygonalMap2;
import zombie.popman.ZombiePopulationManager;
import zombie.savefile.PlayerDB;
import zombie.iso.WorldStreamer;
import zombie.savefile.ClientPlayerDB;
import zombie.ui.UIDebugConsole;
import org.lwjglx.opengl.OpenGLException;
import zombie.Lua.LuaManager;
import zombie.iso.LightingThread;
import zombie.util.PublicServerUtil;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.textures.Texture;
import zombie.core.math.PZMath;
import java.io.EOFException;
import java.io.DataInputStream;
import zombie.network.GameServer;
import zombie.iso.IsoWorld;
import zombie.audio.BaseSoundBank;
import fmod.fmod.FMODSoundBank;
import zombie.audio.DummySoundBank;
import zombie.debug.DebugOptions;
import fmod.fmod.FMODManager;
import zombie.core.opengl.RenderThread;
import zombie.core.ThreadGroups;
import org.lwjglx.LWJGLException;
import java.io.IOException;
import zombie.core.SpriteRenderer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL;
import org.lwjglx.opengl.Display;
import zombie.iso.IsoObjectPicker;
import zombie.iso.LightingJNI;
import zombie.core.PerformanceSettings;
import zombie.gameStates.IngameState;
import zombie.Lua.LuaEventManager;
import zombie.debug.LineDrawer;
import zombie.core.raknet.VoiceManager;
import zombie.ui.UIManager;
import zombie.iso.IsoCamera;
import zombie.characters.IsoPlayer;
import zombie.network.CoopMaster;
import zombie.input.GameKeyboard;
import zombie.core.znet.SteamUtils;
import zombie.spnetwork.SinglePlayerClient;
import zombie.spnetwork.SinglePlayerServer;
import zombie.core.logger.ExceptionLogger;
import zombie.network.GameClient;
import org.lwjglx.input.Controller;
import zombie.debug.DebugLog;
import zombie.gameStates.MainScreenState;
import zombie.gameStates.TISLogoState;
import zombie.core.Core;
import zombie.input.Mouse;
import zombie.core.particle.MuzzleFlash;
import zombie.core.textures.TextureID;
import zombie.core.Rand;
import zombie.characters.professions.ProfessionFactory;
import zombie.characters.traits.TraitFactory;
import zombie.core.skinnedmodel.population.OutfitManager;
import zombie.core.skinnedmodel.population.HairStyles;
import zombie.core.skinnedmodel.population.BeardStyles;
import zombie.core.skinnedmodel.population.ClothingDecals;
import zombie.scripting.ScriptManager;
import zombie.core.Translator;
import zombie.characters.skills.CustomPerks;
import zombie.characters.skills.PerkFactory;
import zombie.core.textures.TexturePackPage;
import java.io.File;
import java.util.ArrayList;
import zombie.asset.AssetManagers;
import zombie.fileSystem.FileSystem;
import zombie.input.JoypadManager;
import zombie.gameStates.GameStateMachine;
import zombie.core.input.Input;

public final class GameWindow
{
    private static final String GAME_TITLE = "Project Zomboid";
    private static final FPSTracking s_fpsTracking;
    private static final ThreadLocal<StringUTF> stringUTF;
    public static final Input GameInput;
    public static boolean DEBUG_SAVE;
    public static boolean OkToSaveOnExit;
    public static String lastP;
    public static GameStateMachine states;
    public static boolean bServerDisconnected;
    public static boolean bLoadedAsClient;
    public static String kickReason;
    public static boolean DrawReloadingLua;
    public static JoypadManager.Joypad ActivatedJoyPad;
    public static String version;
    public static volatile boolean closeRequested;
    public static float averageFPS;
    private static boolean doRenderEvent;
    public static boolean bLuaDebuggerKeyDown;
    public static FileSystem fileSystem;
    public static AssetManagers assetManagers;
    public static boolean bGameThreadExited;
    public static Thread GameThread;
    public static final ArrayList<TexturePack> texturePacks;
    public static final FileSystem.TexturePackTextures texturePackTextures;
    
    private static void initShared() throws Exception {
        final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator));
        if (!file.exists()) {
            file.mkdirs();
        }
        TexturePackPage.bIgnoreWorldItemTextures = true;
        final int n = 2;
        LoadTexturePack("UI", n);
        LoadTexturePack("UI2", n);
        LoadTexturePack("IconsMoveables", n);
        LoadTexturePack("RadioIcons", n);
        LoadTexturePack("ApComUI", n);
        LoadTexturePack("Mechanics", n);
        LoadTexturePack("WeatherFx", n);
        setTexturePackLookup();
        PerkFactory.init();
        CustomPerks.instance.init();
        DoLoadingText(Translator.getText("UI_Loading_Scripts"));
        ScriptManager.instance.Load();
        DoLoadingText(Translator.getText("UI_Loading_Clothing"));
        ClothingDecals.init();
        BeardStyles.init();
        HairStyles.init();
        OutfitManager.init();
        DoLoadingText("");
        TraitFactory.init();
        ProfessionFactory.init();
        Rand.init();
        TexturePackPage.bIgnoreWorldItemTextures = false;
        TextureID.bUseCompression = TextureID.bUseCompressionOption;
        MuzzleFlash.init();
        Mouse.initCustomCursor();
        if (!Core.bDebug) {
            GameWindow.states.States.add(new TISLogoState());
        }
        GameWindow.states.States.add(new MainScreenState());
        if (!Core.bDebug) {
            GameWindow.states.LoopToState = 1;
        }
        GameWindow.GameInput.initControllers();
        final int controllerCount = GameWindow.GameInput.getControllerCount();
        DebugLog.Input.println("----------------------------------------------");
        DebugLog.Input.println("--    Controller setup - use this info to     ");
        DebugLog.Input.println("--    edit joypad.ini in save directory       ");
        DebugLog.Input.println("----------------------------------------------");
        for (int i = 0; i < controllerCount; ++i) {
            final Controller controller = GameWindow.GameInput.getController(i);
            if (controller != null) {
                DebugLog.Input.println("----------------------------------------------");
                DebugLog.Input.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, controller.getGamepadName()));
                DebugLog.Input.println("----------------------------------------------");
                final int axisCount = controller.getAxisCount();
                if (axisCount > 1) {
                    DebugLog.Input.println("----------------------------------------------");
                    DebugLog.Input.println(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, i));
                    DebugLog.Input.println("----------------------------------------------");
                    for (int j = 0; j < axisCount; ++j) {
                        DebugLog.Input.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, controller.getAxisName(j)));
                    }
                }
                final int buttonCount = controller.getButtonCount();
                if (buttonCount > 1) {
                    DebugLog.Input.println("----------------------------------------------");
                    DebugLog.Input.println(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, i));
                    DebugLog.Input.println("----------------------------------------------");
                    for (int k = 0; k < buttonCount; ++k) {
                        DebugLog.Input.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, controller.getButtonName(k)));
                    }
                }
            }
        }
    }
    
    private static void logic() {
        if (GameClient.bClient) {
            try {
                GameClient.instance.update();
            }
            catch (Exception ex) {
                ExceptionLogger.logException(ex);
            }
        }
        try {
            SinglePlayerServer.update();
            SinglePlayerClient.update();
        }
        catch (Throwable t) {
            ExceptionLogger.logException(t);
        }
        SteamUtils.runLoop();
        Mouse.update();
        GameKeyboard.update();
        GameWindow.GameInput.updateGameThread();
        if (CoopMaster.instance != null) {
            CoopMaster.instance.update();
        }
        if (IsoPlayer.players[0] != null) {
            IsoPlayer.setInstance(IsoPlayer.players[0]);
            IsoCamera.CamCharacter = IsoPlayer.players[0];
        }
        UIManager.update();
        VoiceManager.instance.update();
        LineDrawer.clear();
        if (JoypadManager.instance.isAPressed(-1)) {
            int i = 0;
            while (i < JoypadManager.instance.JoypadList.size()) {
                final JoypadManager.Joypad activatedJoyPad = JoypadManager.instance.JoypadList.get(i);
                if (activatedJoyPad.isAPressed()) {
                    if (GameWindow.ActivatedJoyPad == null) {
                        GameWindow.ActivatedJoyPad = activatedJoyPad;
                    }
                    if (IsoPlayer.getInstance() != null) {
                        LuaEventManager.triggerEvent("OnJoypadActivate", activatedJoyPad.getID());
                        break;
                    }
                    LuaEventManager.triggerEvent("OnJoypadActivateUI", activatedJoyPad.getID());
                    break;
                }
                else {
                    ++i;
                }
            }
        }
        SoundManager.instance.Update();
        boolean b = true;
        if (GameTime.isGamePaused()) {
            b = false;
        }
        MapCollisionData.instance.updateGameState();
        Mouse.setCursorVisible(true);
        if (b) {
            GameWindow.states.update();
        }
        else {
            IsoCamera.updateAll();
            if (IngameState.instance != null && (GameWindow.states.current == IngameState.instance || GameWindow.states.States.contains(IngameState.instance))) {
                LuaEventManager.triggerEvent("OnTickEvenPaused", 0.0);
            }
        }
        UIManager.resize();
        GameWindow.fileSystem.updateAsyncTransactions();
        if (GameKeyboard.isKeyPressed(Core.getInstance().getKey("Take screenshot"))) {
            Core.getInstance().TakeFullScreenshot(null);
        }
    }
    
    public static void render() {
        final IsoCamera.FrameState frameState = IsoCamera.frameState;
        ++frameState.frameCount;
        renderInternal();
    }
    
    protected static void renderInternal() {
        if (!PerformanceSettings.LightingThread && LightingJNI.init && !LightingJNI.WaitingForMain()) {
            LightingJNI.DoLightingUpdateNew(System.nanoTime());
        }
        IsoObjectPicker.Instance.StartRender();
        s_performance.statesRender.invokeAndMeasure(GameWindow.states, GameStateMachine::render);
    }
    
    public static void InitDisplay() throws IOException, LWJGLException {
        Display.setTitle("Project Zomboid");
        if (!Core.getInstance().loadOptions()) {
            final int availableProcessors = Runtime.getRuntime().availableProcessors();
            if (availableProcessors == 1) {
                PerformanceSettings.LightingFrameSkip = 3;
            }
            else if (availableProcessors == 2) {
                PerformanceSettings.LightingFrameSkip = 2;
            }
            else if (availableProcessors <= 4) {
                PerformanceSettings.LightingFrameSkip = 1;
            }
            Display.setFullscreen(false);
            Display.setResizable(false);
            if (Display.getDesktopDisplayMode().getWidth() > 1280 && Display.getDesktopDisplayMode().getHeight() > 1080) {
                Core.getInstance().init(1280, 720);
                Core.getInstance().saveOptions();
            }
            else {
                Core.getInstance().init(Core.width, Core.height);
            }
            if (!GL.getCapabilities().GL_ATI_meminfo && !GL.getCapabilities().GL_NVX_gpu_memory_info) {
                DebugLog.General.warn((Object)"Unable to determine available GPU memory, texture compression defaults to on");
                TextureID.bUseCompression = (TextureID.bUseCompressionOption = true);
            }
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.getProperty("user.language")));
            Core.getInstance().setOptionLanguageName(System.getProperty("user.language").toUpperCase());
        }
        else {
            Core.getInstance().init(Core.getInstance().getScreenWidth(), Core.getInstance().getScreenHeight());
        }
        if (GL.getCapabilities().GL_ATI_meminfo) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, GL11.glGetInteger(34812) / 1024));
        }
        if (GL.getCapabilities().GL_NVX_gpu_memory_info) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, GL11.glGetInteger(36937) / 1024));
            DebugLog.log(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, GL11.glGetInteger(36935) / 1024));
            DebugLog.log(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, GL11.glGetInteger(36936) / 1024));
        }
        SpriteRenderer.instance.create();
    }
    
    public static void InitGameThread() {
        Thread.setDefaultUncaughtExceptionHandler(GameWindow::uncaughtGlobalException);
        final Thread gameThread = new Thread(ThreadGroups.Main, GameWindow::mainThread, "MainThread");
        gameThread.setUncaughtExceptionHandler(GameWindow::uncaughtExceptionMainThread);
        (GameWindow.GameThread = gameThread).start();
    }
    
    private static void uncaughtExceptionMainThread(final Thread thread, final Throwable t) {
        if (t instanceof ThreadDeath) {
            DebugLog.General.println("Game Thread exited: ", thread.getName());
            return;
        }
        try {
            uncaughtException(thread, t);
        }
        finally {
            onGameThreadExited();
        }
    }
    
    private static void uncaughtGlobalException(final Thread thread, final Throwable t) {
        if (t instanceof ThreadDeath) {
            DebugLog.General.println("External Thread exited: ", thread.getName());
            return;
        }
        uncaughtException(thread, t);
    }
    
    public static void uncaughtException(final Thread thread, final Throwable t) {
        if (t instanceof ThreadDeath) {
            DebugLog.General.println("Internal Thread exited: ", thread.getName());
            return;
        }
        final String format = String.format("Unhandled %s thrown by thread %s.", t.getClass().getName(), thread.getName());
        DebugLog.General.error((Object)format);
        ExceptionLogger.logException(t, format);
    }
    
    private static void mainThread() {
        mainThreadInit();
        enter();
        RenderThread.setWaitForRenderState(true);
        run_ez();
    }
    
    private static void mainThreadInit() {
        final String property = System.getProperty("debug");
        if (System.getProperty("nosave") != null) {
            Core.getInstance().setNoSave(true);
        }
        if (property != null) {
            Core.bDebug = true;
        }
        if (!Core.SoundDisabled) {
            FMODManager.instance.init();
        }
        DebugOptions.instance.init();
        GameProfiler.init();
        SoundManager.instance = (Core.SoundDisabled ? new DummySoundManager() : new SoundManager());
        AmbientStreamManager.instance = (Core.SoundDisabled ? new DummyAmbientStreamManager() : new AmbientStreamManager());
        BaseSoundBank.instance = (BaseSoundBank)(Core.SoundDisabled ? new DummySoundBank() : new FMODSoundBank());
        VoiceManager.instance.loadConfig();
        TextureID.bUseCompressionOption = (Core.SafeModeForced || Core.getInstance().getOptionTextureCompression());
        TextureID.bUseCompression = TextureID.bUseCompressionOption;
        SoundManager.instance.setSoundVolume(Core.getInstance().getOptionSoundVolume() / 10.0f);
        SoundManager.instance.setMusicVolume(Core.getInstance().getOptionMusicVolume() / 10.0f);
        SoundManager.instance.setAmbientVolume(Core.getInstance().getOptionAmbientVolume() / 10.0f);
        SoundManager.instance.setVehicleEngineVolume(Core.getInstance().getOptionVehicleEngineVolume() / 10.0f);
        try {
            ZomboidFileSystem.instance.init();
        }
        catch (Exception cause) {
            throw new RuntimeException(cause);
        }
        DebugFileWatcher.instance.init();
        final String property2 = System.getProperty("server");
        System.getProperty("client");
        if (System.getProperty("nozombies") != null) {
            IsoWorld.NoZombies = true;
        }
        if (property2 != null && property2.equals("true")) {
            GameServer.bServer = true;
        }
        try {
            renameSaveFolders();
            init();
        }
        catch (Exception cause2) {
            throw new RuntimeException(cause2);
        }
    }
    
    private static void renameSaveFolders() {
        final File file = new File(ZomboidFileSystem.instance.getSaveDir());
        if (!file.exists() || !file.isDirectory()) {
            return;
        }
        final File file2 = new File(file, "Fighter");
        final File file3 = new File(file, "Survivor");
        if (!file2.exists() || !file2.isDirectory() || !file3.exists() || !file3.isDirectory()) {
            return;
        }
        DebugLog.log("RENAMING Saves/Survivor to Saves/Apocalypse");
        DebugLog.log("RENAMING Saves/Fighter to Saves/Survivor");
        file3.renameTo(new File(file, "Apocalypse"));
        file2.renameTo(new File(file, "Survivor"));
        final File file4 = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator));
        if (file4.exists()) {
            file4.delete();
        }
    }
    
    public static long readLong(final DataInputStream dataInputStream) throws IOException {
        final int read = dataInputStream.read();
        final int read2 = dataInputStream.read();
        final int read3 = dataInputStream.read();
        final int read4 = dataInputStream.read();
        final int read5 = dataInputStream.read();
        final int read6 = dataInputStream.read();
        final int read7 = dataInputStream.read();
        final int read8 = dataInputStream.read();
        if ((read | read2 | read3 | read4 | read5 | read6 | read7 | read8) < 0) {
            throw new EOFException();
        }
        return read + (read2 << 8) + (read3 << 16) + (read4 << 24) + (read5 << 32) + (read6 << 40) + (read7 << 48) + (read8 << 56);
    }
    
    public static int readInt(final DataInputStream dataInputStream) throws IOException {
        final int read = dataInputStream.read();
        final int read2 = dataInputStream.read();
        final int read3 = dataInputStream.read();
        final int read4 = dataInputStream.read();
        if ((read | read2 | read3 | read4) < 0) {
            throw new EOFException();
        }
        return read + (read2 << 8) + (read3 << 16) + (read4 << 24);
    }
    
    private static void run_ez() {
        long nanoTime = System.nanoTime();
        long n = 0L;
        while (!RenderThread.isCloseRequested() && !GameWindow.closeRequested) {
            final long nanoTime2 = System.nanoTime();
            if (nanoTime2 < nanoTime) {
                nanoTime = nanoTime2;
            }
            else {
                final long n2 = nanoTime2 - nanoTime;
                nanoTime = nanoTime2;
                if (PerformanceSettings.isUncappedFPS()) {
                    frameStep();
                }
                else {
                    n += n2;
                    final long n3 = PZMath.secondsToNanos / PerformanceSettings.getLockFPS();
                    if (n >= n3) {
                        frameStep();
                        n %= n3;
                    }
                }
                if (Core.bDebug && DebugOptions.instance.ThreadCrash_Enabled.getValue()) {
                    DebugOptions.testThreadCrash(0);
                    RenderThread.invokeOnRenderContext(() -> DebugOptions.testThreadCrash(1));
                }
                Thread.yield();
            }
        }
        exit();
    }
    
    private static void enter() {
        Core.TileScale = (Core.getInstance().getOptionTexture2x() ? 2 : 1);
        if (Core.SafeModeForced) {
            Core.TileScale = 1;
        }
        IsoCamera.init();
        final int n = (TextureID.bUseCompression ? 4 : 0) | 0x40;
        if (Core.TileScale == 1) {
            LoadTexturePack("Tiles1x", n);
            LoadTexturePack("Overlays1x", n);
            LoadTexturePack("JumboTrees1x", n);
            LoadTexturePack("Tiles1x.floor", n & 0xFFFFFFFB);
        }
        if (Core.TileScale == 2) {
            LoadTexturePack("Tiles2x", n);
            LoadTexturePack("Overlays2x", n);
            LoadTexturePack("JumboTrees2x", n);
            LoadTexturePack("Tiles2x.floor", n & 0xFFFFFFFB);
        }
        setTexturePackLookup();
        if (Texture.getSharedTexture("TileIndieStoneTentFrontLeft") == null) {
            throw new RuntimeException("Rebuild Tiles.pack with \"1 Include This in .pack\" as individual images not tilesheets");
        }
        DebugLog.log(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Texture.totalTextureID));
        GameWindow.s_fpsTracking.init();
        DoLoadingText(Translator.getText("UI_Loading_ModelsAnimations"));
        ModelManager.instance.create();
        if (!SteamUtils.isSteamModeEnabled()) {
            DoLoadingText(Translator.getText("UI_Loading_InitPublicServers"));
            PublicServerUtil.init();
        }
        VoiceManager.instance.InitVMClient();
        DoLoadingText(Translator.getText("UI_Loading_OnGameBoot"));
        LuaEventManager.triggerEvent("OnGameBoot");
    }
    
    private static void frameStep() {
        try {
            final IsoCamera.FrameState frameState = IsoCamera.frameState;
            ++frameState.frameCount;
            s_performance.frameStep.start();
            GameWindow.s_fpsTracking.frameStep();
            s_performance.logic.invokeAndMeasure(GameWindow::logic);
            Core.getInstance().setScreenSize(RenderThread.getDisplayWidth(), RenderThread.getDisplayHeight());
            renderInternal();
            if (GameWindow.doRenderEvent) {
                LuaEventManager.triggerEvent("OnRenderTick");
            }
            Core.getInstance().DoFrameReady();
            LightingThread.instance.update();
            if (Core.bDebug) {
                if (GameKeyboard.isKeyDown(Core.getInstance().getKey("Toggle Lua Debugger"))) {
                    if (!GameWindow.bLuaDebuggerKeyDown) {
                        UIManager.setShowLuaDebuggerOnError(true);
                        LuaManager.thread.bStep = true;
                        LuaManager.thread.bStepInto = true;
                        GameWindow.bLuaDebuggerKeyDown = true;
                    }
                }
                else {
                    GameWindow.bLuaDebuggerKeyDown = false;
                }
                if (GameKeyboard.isKeyPressed(Core.getInstance().getKey("ToggleLuaConsole"))) {
                    final UIDebugConsole debugConsole = UIManager.getDebugConsole();
                    if (debugConsole != null) {
                        debugConsole.setVisible(!debugConsole.isVisible());
                    }
                }
            }
        }
        catch (OpenGLException ex) {
            RenderThread.logGLException(ex);
        }
        catch (Exception ex2) {
            ExceptionLogger.logException(ex2);
        }
        finally {
            s_performance.frameStep.end();
        }
    }
    
    private static void exit() {
        DebugLog.log("EXITDEBUG: GameWindow.exit 1");
        if (GameClient.bClient) {
            for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
                final IsoPlayer isoPlayer = IsoPlayer.players[i];
                if (isoPlayer != null) {
                    ClientPlayerDB.getInstance().clientSendNetworkPlayerInt(isoPlayer);
                }
            }
            WorldStreamer.instance.stop();
            GameClient.instance.doDisconnect("Quitting");
            VoiceManager.instance.DeinitVMClient();
        }
        if (GameWindow.OkToSaveOnExit) {
            try {
                WorldStreamer.instance.quit();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            if (PlayerDB.isAllow()) {
                PlayerDB.getInstance().saveLocalPlayersForce();
                PlayerDB.getInstance().m_canSavePlayers = false;
            }
            if (ClientPlayerDB.isAllow()) {
                ClientPlayerDB.getInstance().canSavePlayers = false;
            }
            try {
                if (GameClient.bClient && GameClient.connection != null) {
                    GameClient.connection.username = null;
                }
                save(true);
            }
            catch (Throwable t) {
                t.printStackTrace();
            }
            try {
                if (IsoWorld.instance.CurrentCell != null) {
                    LuaEventManager.triggerEvent("OnPostSave");
                }
            }
            catch (Exception ex2) {
                ex2.printStackTrace();
            }
            try {
                if (IsoWorld.instance.CurrentCell != null) {
                    LuaEventManager.triggerEvent("OnPostSave");
                }
            }
            catch (Exception ex3) {
                ex3.printStackTrace();
            }
            try {
                LightingThread.instance.stop();
                MapCollisionData.instance.stop();
                ZombiePopulationManager.instance.stop();
                PolygonalMap2.instance.stop();
                ZombieSpawnRecorder.instance.quit();
            }
            catch (Exception ex4) {
                ex4.printStackTrace();
            }
        }
        DebugLog.log("EXITDEBUG: GameWindow.exit 2");
        if (GameClient.bClient) {
            WorldStreamer.instance.stop();
            GameClient.instance.doDisconnect("Quitting");
            try {
                Thread.sleep(500L);
            }
            catch (InterruptedException ex5) {
                ex5.printStackTrace();
            }
        }
        DebugLog.log("EXITDEBUG: GameWindow.exit 3");
        if (PlayerDB.isAvailable()) {
            PlayerDB.getInstance().close();
        }
        if (ClientPlayerDB.isAvailable()) {
            ClientPlayerDB.getInstance().close();
        }
        DebugLog.log("EXITDEBUG: GameWindow.exit 4");
        GameClient.instance.Shutdown();
        SteamUtils.shutdown();
        ZipLogs.addZipFile(true);
        onGameThreadExited();
        DebugLog.log("EXITDEBUG: GameWindow.exit 5");
    }
    
    private static void onGameThreadExited() {
        GameWindow.bGameThreadExited = true;
        RenderThread.onGameThreadExited();
    }
    
    public static void setTexturePackLookup() {
        GameWindow.texturePackTextures.clear();
        for (int i = GameWindow.texturePacks.size() - 1; i >= 0; --i) {
            final TexturePack texturePack = GameWindow.texturePacks.get(i);
            if (texturePack.modID == null) {
                GameWindow.texturePackTextures.putAll(texturePack.textures);
            }
        }
        final ArrayList<String> modIDs = ZomboidFileSystem.instance.getModIDs();
        for (int j = GameWindow.texturePacks.size() - 1; j >= 0; --j) {
            final TexturePack texturePack2 = GameWindow.texturePacks.get(j);
            if (texturePack2.modID != null) {
                if (modIDs.contains(texturePack2.modID)) {
                    GameWindow.texturePackTextures.putAll(texturePack2.textures);
                }
            }
        }
        Texture.onTexturePacksChanged();
    }
    
    public static void LoadTexturePack(final String s, final int n) {
        LoadTexturePack(s, n, null);
    }
    
    public static void LoadTexturePack(final String packName, final int n, final String modID) {
        DebugLog.General.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, packName));
        DoLoadingText(Translator.getText("UI_Loading_Texturepack", packName));
        final String string = ZomboidFileSystem.instance.getString(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, packName));
        final TexturePack e = new TexturePack();
        e.packName = packName;
        e.fileName = string;
        e.modID = modID;
        GameWindow.fileSystem.mountTexturePack(packName, e.textures, n);
        GameWindow.texturePacks.add(e);
    }
    
    @Deprecated
    public static void LoadTexturePackDDS(final String s) {
        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        if (SpriteRenderer.instance != null) {
            Core.getInstance().StartFrame();
            Core.getInstance().EndFrame(0);
            Core.getInstance().StartFrameUI();
            SpriteRenderer.instance.renderi(null, 0, 0, Core.getInstance().getScreenWidth(), Core.getInstance().getScreenHeight(), 0.0f, 0.0f, 0.0f, 1.0f, null);
            TextManager.instance.DrawStringCentre(Core.getInstance().getScreenWidth() / 2, Core.getInstance().getScreenHeight() / 2, Translator.getText("UI_Loading_Texturepack", s), 1.0, 1.0, 1.0, 1.0);
            Core.getInstance().EndFrameUI();
        }
        InputStream in = null;
        try {
            in = new FileInputStream(ZomboidFileSystem.instance.getString(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s)));
        }
        catch (FileNotFoundException thrown) {
            Logger.getLogger(GameLoadingState.class.getName()).log(Level.SEVERE, null, thrown);
        }
        try {
            final BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
            try {
                for (int int1 = TexturePackPage.readInt(bufferedInputStream), i = 0; i < int1; ++i) {
                    final TexturePackPage texturePackPage = new TexturePackPage();
                    if (i % 100 == 0 && SpriteRenderer.instance != null) {
                        Core.getInstance().StartFrame();
                        Core.getInstance().EndFrame();
                        Core.getInstance().StartFrameUI();
                        TextManager.instance.DrawStringCentre(Core.getInstance().getScreenWidth() / 2, Core.getInstance().getScreenHeight() / 2, Translator.getText("UI_Loading_Texturepack", s), 1.0, 1.0, 1.0, 1.0);
                        Core.getInstance().EndFrameUI();
                        RenderThread.invokeOnRenderContext(Display::update);
                    }
                    texturePackPage.loadFromPackFileDDS(bufferedInputStream);
                }
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
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
        }
        catch (Exception ex) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
            ex.printStackTrace();
        }
        Texture.nullTextures.clear();
    }
    
    private static void installRequiredLibrary(final String pathname, final String s) {
        if (new File(pathname).exists()) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, pathname));
            final ProcessBuilder processBuilder = new ProcessBuilder(new String[] { pathname, "/quiet", "/norestart" });
            try {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, processBuilder.start().waitFor()));
                return;
            }
            catch (IOException | InterruptedException ex) {
                final Throwable t;
                t.printStackTrace();
            }
        }
        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
    }
    
    private static void checkRequiredLibraries() {
        if (System.getProperty("os.name").startsWith("Win")) {
            String libname;
            String s;
            String s2;
            String s3;
            if (System.getProperty("sun.arch.data.model").equals("64")) {
                libname = "Lighting64";
                s = "_CommonRedist\\vcredist\\2010\\vcredist_x64.exe";
                s2 = "_CommonRedist\\vcredist\\2012\\vcredist_x64.exe";
                s3 = "_CommonRedist\\vcredist\\2013\\vcredist_x64.exe";
            }
            else {
                libname = "Lighting32";
                s = "_CommonRedist\\vcredist\\2010\\vcredist_x86.exe";
                s2 = "_CommonRedist\\vcredist\\2012\\vcredist_x86.exe";
                s3 = "_CommonRedist\\vcredist\\2013\\vcredist_x86.exe";
            }
            if ("1".equals(System.getProperty("zomboid.debuglibs.lighting"))) {
                DebugLog.log("***** Loading debug version of Lighting");
                libname = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, libname);
            }
            try {
                System.loadLibrary(libname);
            }
            catch (UnsatisfiedLinkError unsatisfiedLinkError) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, libname));
                installRequiredLibrary(s, "the Microsoft Visual C++ 2010 Redistributable.");
                installRequiredLibrary(s2, "the Microsoft Visual C++ 2012 Redistributable.");
                installRequiredLibrary(s3, "the Microsoft Visual C++ 2013 Redistributable.");
            }
        }
    }
    
    private static void init() throws Exception {
        initFonts();
        checkRequiredLibraries();
        SteamUtils.init();
        ServerBrowser.init();
        SteamFriends.init();
        SteamWorkshop.init();
        RakNetPeerInterface.init();
        LightingJNI.init();
        ZombiePopulationManager.init();
        PZSQLUtils.init();
        Clipper.init();
        WorldMapJNI.init();
        Bullet.init();
        final int availableProcessors = Runtime.getRuntime().availableProcessors();
        if (Core.bMultithreadedRendering) {
            Core.bMultithreadedRendering = (availableProcessors > 1);
        }
        final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator));
        if (!file.exists()) {
            file.mkdirs();
        }
        DoLoadingText("Loading Mods");
        ZomboidFileSystem.instance.resetDefaultModsForNewRelease("41_51");
        ZomboidFileSystem.instance.loadMods("default");
        ZomboidFileSystem.instance.loadModPackFiles();
        DoLoadingText("Loading Translations");
        Languages.instance.init();
        Translator.language = null;
        initFonts();
        Translator.loadFiles();
        initShared();
        DoLoadingText(Translator.getText("UI_Loading_Lua"));
        LuaManager.init();
        CustomPerks.instance.initLua();
        CustomSandboxOptions.instance.init();
        CustomSandboxOptions.instance.initInstance(SandboxOptions.instance);
        LuaManager.LoadDirBase();
        ZomboidGlobals.Load();
        LuaEventManager.triggerEvent("OnLoadSoundBanks");
    }
    
    private static void initFonts() throws FileNotFoundException {
        TextManager.instance.Init();
        while (TextManager.instance.font.isEmpty()) {
            GameWindow.fileSystem.updateAsyncTransactions();
            try {
                Thread.sleep(10L);
            }
            catch (InterruptedException ex) {}
        }
    }
    
    public static void savePlayer() {
    }
    
    public static void save(final boolean b) throws IOException {
        if (Core.getInstance().isNoSave()) {
            return;
        }
        if (IsoWorld.instance.CurrentCell == null || "LastStand".equals(Core.getInstance().getGameMode()) || "Tutorial".equals(Core.getInstance().getGameMode())) {
            return;
        }
        final FileOutputStream out = new FileOutputStream(ZomboidFileSystem.instance.getFileInCurrentSave("map_ver.bin"));
        try {
            final DataOutputStream dataOutputStream = new DataOutputStream(out);
            try {
                dataOutputStream.writeInt(186);
                WriteString(dataOutputStream, Core.GameMap);
                WriteString(dataOutputStream, IsoWorld.instance.getDifficulty());
                dataOutputStream.close();
            }
            catch (Throwable t) {
                try {
                    dataOutputStream.close();
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
        final FileOutputStream out2 = new FileOutputStream(ZomboidFileSystem.instance.getFileInCurrentSave("map_sand.bin"));
        try {
            final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(out2);
            try {
                SliceY.SliceBuffer.clear();
                SandboxOptions.instance.save(SliceY.SliceBuffer);
                bufferedOutputStream.write(SliceY.SliceBuffer.array(), 0, SliceY.SliceBuffer.position());
                bufferedOutputStream.close();
            }
            catch (Throwable t3) {
                try {
                    bufferedOutputStream.close();
                }
                catch (Throwable exception3) {
                    t3.addSuppressed(exception3);
                }
                throw t3;
            }
            out2.close();
        }
        catch (Throwable t4) {
            try {
                out2.close();
            }
            catch (Throwable exception4) {
                t4.addSuppressed(exception4);
            }
            throw t4;
        }
        LuaEventManager.triggerEvent("OnSave");
        try {
            try {
                try {
                    if (Thread.currentThread() == GameWindow.GameThread) {
                        SavefileThumbnail.create();
                    }
                }
                catch (Exception ex) {
                    ExceptionLogger.logException(ex);
                }
                final File fileInCurrentSave = ZomboidFileSystem.instance.getFileInCurrentSave("map.bin");
                try {
                    final FileOutputStream out3 = new FileOutputStream(fileInCurrentSave);
                    try {
                        IsoWorld.instance.CurrentCell.save(new DataOutputStream(out3), b);
                        out3.close();
                    }
                    catch (Throwable t5) {
                        try {
                            out3.close();
                        }
                        catch (Throwable exception5) {
                            t5.addSuppressed(exception5);
                        }
                        throw t5;
                    }
                }
                catch (Exception ex2) {
                    ExceptionLogger.logException(ex2);
                }
                try {
                    MapCollisionData.instance.save();
                    if (!GameWindow.bLoadedAsClient) {
                        SGlobalObjects.save();
                    }
                }
                catch (Exception ex3) {
                    ExceptionLogger.logException(ex3);
                }
                ZomboidRadio.getInstance().Save();
                GlobalModData.instance.save();
                MapItem.SaveWorldMap();
                WorldMapVisited.SaveAll();
            }
            catch (IOException cause) {
                throw new RuntimeException(cause);
            }
        }
        catch (RuntimeException ex4) {
            final Throwable cause2 = ex4.getCause();
            if (cause2 instanceof IOException) {
                throw (IOException)cause2;
            }
            throw ex4;
        }
    }
    
    public static String getCoopServerHome() {
        return new File(ZomboidFileSystem.instance.getCacheDir()).getParent();
    }
    
    public static void WriteString(final ByteBuffer byteBuffer, final String s) {
        WriteStringUTF(byteBuffer, s);
    }
    
    public static void WriteStringUTF(final ByteBuffer byteBuffer, final String s) {
        GameWindow.stringUTF.get().save(byteBuffer, s);
    }
    
    public static void WriteString(final DataOutputStream dataOutputStream, final String s) throws IOException {
        if (s == null) {
            dataOutputStream.writeInt(0);
            return;
        }
        dataOutputStream.writeInt(s.length());
        if (s != null && s.length() >= 0) {
            dataOutputStream.writeChars(s);
        }
    }
    
    public static String ReadStringUTF(final ByteBuffer byteBuffer) {
        return GameWindow.stringUTF.get().load(byteBuffer);
    }
    
    public static String ReadString(final ByteBuffer byteBuffer) {
        return ReadStringUTF(byteBuffer);
    }
    
    public static String ReadString(final DataInputStream dataInputStream) throws IOException {
        final int int1 = dataInputStream.readInt();
        if (int1 == 0) {
            return "";
        }
        if (int1 > 65536) {
            throw new RuntimeException("GameWindow.ReadString: string is too long, corrupted save?");
        }
        final StringBuilder sb = new StringBuilder(int1);
        for (int i = 0; i < int1; ++i) {
            sb.append(dataInputStream.readChar());
        }
        return sb.toString();
    }
    
    public static void doRenderEvent(final boolean doRenderEvent) {
        GameWindow.doRenderEvent = doRenderEvent;
    }
    
    public static void DoLoadingText(final String s) {
        if (SpriteRenderer.instance != null && TextManager.instance.font != null) {
            Core.getInstance().StartFrame();
            Core.getInstance().EndFrame();
            Core.getInstance().StartFrameUI();
            SpriteRenderer.instance.renderi(null, 0, 0, Core.getInstance().getScreenWidth(), Core.getInstance().getScreenHeight(), 0.0f, 0.0f, 0.0f, 1.0f, null);
            TextManager.instance.DrawStringCentre(Core.getInstance().getScreenWidth() / 2, Core.getInstance().getScreenHeight() / 2, s, 1.0, 1.0, 1.0, 1.0);
            Core.getInstance().EndFrameUI();
        }
    }
    
    static {
        s_fpsTracking = new FPSTracking();
        stringUTF = ThreadLocal.withInitial((Supplier<? extends StringUTF>)StringUTF::new);
        GameInput = new Input();
        GameWindow.DEBUG_SAVE = false;
        GameWindow.OkToSaveOnExit = false;
        GameWindow.lastP = null;
        GameWindow.states = new GameStateMachine();
        GameWindow.bLoadedAsClient = false;
        GameWindow.DrawReloadingLua = false;
        GameWindow.ActivatedJoyPad = null;
        GameWindow.version = "RC3";
        GameWindow.averageFPS = (float)PerformanceSettings.getLockFPS();
        GameWindow.doRenderEvent = false;
        GameWindow.bLuaDebuggerKeyDown = false;
        GameWindow.fileSystem = new FileSystemImpl();
        GameWindow.assetManagers = new AssetManagers(GameWindow.fileSystem);
        GameWindow.bGameThreadExited = false;
        texturePacks = new ArrayList<TexturePack>();
        texturePackTextures = new FileSystem.TexturePackTextures();
    }
    
    private static final class TexturePack
    {
        String packName;
        String fileName;
        String modID;
        final FileSystem.TexturePackTextures textures;
        
        private TexturePack() {
            this.textures = new FileSystem.TexturePackTextures();
        }
    }
    
    public static class OSValidator
    {
        private static String OS;
        
        public static boolean isWindows() {
            return OSValidator.OS.indexOf("win") >= 0;
        }
        
        public static boolean isMac() {
            return OSValidator.OS.indexOf("mac") >= 0;
        }
        
        public static boolean isUnix() {
            return OSValidator.OS.indexOf("nix") >= 0 || OSValidator.OS.indexOf("nux") >= 0 || OSValidator.OS.indexOf("aix") > 0;
        }
        
        public static boolean isSolaris() {
            return OSValidator.OS.indexOf("sunos") >= 0;
        }
        
        static {
            OSValidator.OS = System.getProperty("os.name").toLowerCase();
        }
    }
    
    private static class StringUTF
    {
        private char[] chars;
        private ByteBuffer byteBuffer;
        private CharBuffer charBuffer;
        private CharsetEncoder ce;
        private CharsetDecoder cd;
        
        private int encode(final String s) {
            if (this.chars == null || this.chars.length < s.length()) {
                this.chars = new char[(s.length() + 128 - 1) / 128 * 128];
                this.charBuffer = CharBuffer.wrap(this.chars);
            }
            s.getChars(0, s.length(), this.chars, 0);
            this.charBuffer.limit(s.length());
            this.charBuffer.position(0);
            if (this.ce == null) {
                this.ce = StandardCharsets.UTF_8.newEncoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
            }
            this.ce.reset();
            final int capacity = ((int)(s.length() * (double)this.ce.maxBytesPerChar()) + 128 - 1) / 128 * 128;
            if (this.byteBuffer == null || this.byteBuffer.capacity() < capacity) {
                this.byteBuffer = ByteBuffer.allocate(capacity);
            }
            this.byteBuffer.clear();
            this.ce.encode(this.charBuffer, this.byteBuffer, true);
            return this.byteBuffer.position();
        }
        
        private String decode(final int n) {
            if (this.cd == null) {
                this.cd = StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
            }
            this.cd.reset();
            final int n2 = (int)(n * (double)this.cd.maxCharsPerByte());
            if (this.chars == null || this.chars.length < n2) {
                this.chars = new char[(n2 + 128 - 1) / 128 * 128];
                this.charBuffer = CharBuffer.wrap(this.chars);
            }
            this.charBuffer.clear();
            this.cd.decode(this.byteBuffer, this.charBuffer, true);
            return new String(this.chars, 0, this.charBuffer.position());
        }
        
        void save(final ByteBuffer byteBuffer, final String s) {
            if (s == null || s.isEmpty()) {
                byteBuffer.putShort((short)0);
                return;
            }
            byteBuffer.putShort((short)this.encode(s));
            this.byteBuffer.flip();
            byteBuffer.put(this.byteBuffer);
        }
        
        String load(final ByteBuffer src) {
            final short short1 = src.getShort();
            if (short1 <= 0) {
                return "";
            }
            final int capacity = (short1 + 128 - 1) / 128 * 128;
            if (this.byteBuffer == null || this.byteBuffer.capacity() < capacity) {
                this.byteBuffer = ByteBuffer.allocate(capacity);
            }
            this.byteBuffer.clear();
            if (src.remaining() < short1) {
                DebugLog.General.error(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, short1, src.remaining()));
            }
            final int limit = src.limit();
            src.limit(src.position() + short1);
            this.byteBuffer.put(src);
            src.limit(limit);
            this.byteBuffer.flip();
            return this.decode(short1);
        }
    }
    
    private static class s_performance
    {
        static final PerformanceProfileFrameProbe frameStep;
        static final PerformanceProfileProbe statesRender;
        static final PerformanceProfileProbe logic;
        
        static {
            frameStep = new PerformanceProfileFrameProbe("GameWindow.frameStep");
            statesRender = new PerformanceProfileProbe("GameWindow.states.render");
            logic = new PerformanceProfileProbe("GameWindow.logic");
        }
    }
}

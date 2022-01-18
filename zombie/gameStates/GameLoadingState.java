// 
// Decompiled by Procyon v0.5.36
// 

package zombie.gameStates;

import java.util.Iterator;
import zombie.input.Mouse;
import zombie.iso.WorldStreamer;
import zombie.core.znet.SteamUtils;
import org.lwjglx.input.Keyboard;
import zombie.core.skinnedmodel.runtime.RuntimeAnimationScript;
import zombie.scripting.ScriptManager;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.population.OutfitManager;
import zombie.input.JoypadManager;
import zombie.core.Translator;
import zombie.ui.UIFont;
import zombie.ui.TextManager;
import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;
import zombie.core.textures.Texture;
import zombie.core.SpriteRenderer;
import zombie.modding.ActiveModsFile;
import zombie.modding.ActiveMods;
import zombie.AmbientStreamManager;
import zombie.iso.areas.SafeHouse;
import zombie.network.ServerOptions;
import zombie.characters.IsoGameCharacter;
import zombie.iso.IsoCamera;
import zombie.network.NetworkAIParams;
import zombie.ui.UIManager;
import zombie.characters.IsoPlayer;
import zombie.chat.ChatManager;
import zombie.chat.ChatUtility;
import zombie.iso.IsoObjectPicker;
import zombie.globalObjects.CGlobalObjects;
import zombie.globalObjects.SGlobalObjects;
import zombie.debug.DebugOptions;
import zombie.iso.weather.ClimateManager;
import zombie.GameTime;
import zombie.ui.TutorialManager;
import zombie.vehicles.BaseVehicle;
import zombie.core.ThreadGroups;
import zombie.input.GameKeyboard;
import zombie.world.WorldDictionary;
import zombie.Lua.LuaEventManager;
import zombie.SoundManager;
import zombie.iso.LosUtil;
import zombie.iso.IsoChunkMap;
import java.io.IOException;
import zombie.iso.IsoWorld;
import java.io.FileWriter;
import zombie.network.GameServer;
import java.io.File;
import zombie.ZomboidFileSystem;
import zombie.iso.IsoWater;
import zombie.iso.IsoPuddles;
import zombie.iso.sprite.SkyBox;
import zombie.inventory.RecipeManager;
import zombie.core.logger.ExceptionLogger;
import zombie.Lua.LuaManager;
import zombie.debug.DebugLog;
import zombie.core.Core;
import zombie.ChunkMapFilenames;
import zombie.GameWindow;
import zombie.network.GameClient;

public final class GameLoadingState extends GameState
{
    public static Thread loader;
    public static boolean newGame;
    private static long startTime;
    public static boolean build23Stop;
    public static boolean unexpectedError;
    public static String GameLoadingString;
    public static boolean playerWrongIP;
    private static boolean bShowedUI;
    public static boolean mapDownloadFailed;
    private volatile boolean bWaitForAssetLoadingToFinish1;
    private volatile boolean bWaitForAssetLoadingToFinish2;
    private final Object assetLock1;
    private final Object assetLock2;
    public static boolean playerCreated;
    public static boolean bDone;
    public static boolean convertingWorld;
    public static int convertingFileCount;
    public static int convertingFileMax;
    public int Stage;
    float TotalTime;
    float loadingDotTick;
    String loadingDot;
    private float clickToSkipAlpha;
    private boolean clickToSkipFadeIn;
    public float Time;
    public boolean bForceDone;
    
    public GameLoadingState() {
        this.bWaitForAssetLoadingToFinish1 = false;
        this.bWaitForAssetLoadingToFinish2 = false;
        this.assetLock1 = "Asset Lock 1";
        this.assetLock2 = "Asset Lock 2";
        this.Stage = 0;
        this.TotalTime = 33.0f;
        this.loadingDotTick = 0.0f;
        this.loadingDot = "";
        this.clickToSkipAlpha = 1.0f;
        this.clickToSkipFadeIn = false;
        this.Time = 0.0f;
        this.bForceDone = false;
    }
    
    @Override
    public void enter() {
        GameWindow.bLoadedAsClient = GameClient.bClient;
        GameWindow.OkToSaveOnExit = false;
        GameLoadingState.bShowedUI = false;
        ChunkMapFilenames.instance.clear();
        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Core.GameSaveWorld));
        GameLoadingState.GameLoadingString = "";
        try {
            LuaManager.LoadDirBase("server");
            LuaManager.finishChecksum();
        }
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
        }
        RecipeManager.LoadedAfterLua();
        Core.getInstance().initFBOs();
        Core.getInstance().initShaders();
        SkyBox.getInstance();
        IsoPuddles.getInstance();
        IsoWater.getInstance();
        GameWindow.bServerDisconnected = false;
        if (GameClient.bClient && !GameClient.instance.bConnected) {
            GameClient.instance.init();
            Core.GameMode = "Multiplayer";
            while (GameClient.instance.ID == -1) {
                try {
                    Thread.sleep(10L);
                }
                catch (InterruptedException ex2) {
                    ex2.printStackTrace();
                }
                GameClient.instance.update();
            }
            Core.GameSaveWorld = invokedynamic(makeConcatWithConstants:(B)Ljava/lang/String;, GameClient.instance.ID);
            LuaManager.GlobalObject.deleteSave(invokedynamic(makeConcatWithConstants:(B)Ljava/lang/String;, GameClient.instance.ID));
            LuaManager.GlobalObject.createWorld(invokedynamic(makeConcatWithConstants:(B)Ljava/lang/String;, GameClient.instance.ID));
        }
        if (Core.GameSaveWorld.isEmpty()) {
            DebugLog.log("No savefile directory was specified.  It's a bug.");
            GameWindow.DoLoadingText("No savefile directory was specified.  The game will now close.  Sorry!");
            try {
                Thread.sleep(4000L);
            }
            catch (Exception ex4) {}
            System.exit(-1);
        }
        if (!new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getGameModeCacheDir(), File.separator, Core.GameSaveWorld)).exists() && !Core.getInstance().isNoSave()) {
            DebugLog.log("The savefile directory doesn't exist.  It's a bug.");
            GameWindow.DoLoadingText("The savefile directory doesn't exist.  The game will now close.  Sorry!");
            try {
                Thread.sleep(4000L);
            }
            catch (Exception ex5) {}
            System.exit(-1);
        }
        try {
            if (!GameClient.bClient && !GameServer.bServer && !Core.bTutorial && !Core.isLastStand() && !"Multiplayer".equals(Core.GameMode)) {
                final FileWriter fileWriter = new FileWriter(new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator)));
                fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, IsoWorld.instance.getWorld()));
                fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Core.getInstance().getGameMode()));
                fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, IsoWorld.instance.getDifficulty()));
                fileWriter.flush();
                fileWriter.close();
            }
        }
        catch (IOException ex3) {
            ex3.printStackTrace();
        }
        GameLoadingState.bDone = false;
        this.bForceDone = false;
        IsoChunkMap.CalcChunkWidth();
        LosUtil.init(IsoChunkMap.ChunkGridWidth * 10, IsoChunkMap.ChunkGridWidth * 10);
        this.Time = 0.0f;
        this.Stage = 0;
        this.clickToSkipAlpha = 1.0f;
        this.clickToSkipFadeIn = false;
        GameLoadingState.startTime = System.currentTimeMillis();
        SoundManager.instance.Purge();
        SoundManager.instance.setMusicState("Loading");
        LuaEventManager.triggerEvent("OnPreMapLoad");
        GameLoadingState.newGame = true;
        GameLoadingState.build23Stop = false;
        GameLoadingState.unexpectedError = false;
        GameLoadingState.mapDownloadFailed = false;
        GameLoadingState.playerCreated = false;
        GameLoadingState.convertingWorld = false;
        GameLoadingState.convertingFileCount = 0;
        GameLoadingState.convertingFileMax = -1;
        if (ZomboidFileSystem.instance.getFileInCurrentSave("map_ver.bin").exists()) {
            GameLoadingState.newGame = false;
        }
        if (GameClient.bClient) {
            GameLoadingState.newGame = false;
        }
        WorldDictionary.setIsNewGame(GameLoadingState.newGame);
        GameKeyboard.bNoEventsWhileLoading = true;
        GameLoadingState.loader = new Thread(ThreadGroups.Workers, new Runnable() {
            @Override
            public void run() {
                try {
                    this.runInner();
                }
                catch (Throwable t) {
                    GameLoadingState.unexpectedError = true;
                    ExceptionLogger.logException(t);
                }
            }
            
            private void runInner() throws Exception {
                GameLoadingState.this.bWaitForAssetLoadingToFinish1 = true;
                synchronized (GameLoadingState.this.assetLock1) {
                    while (GameLoadingState.this.bWaitForAssetLoadingToFinish1) {
                        try {
                            GameLoadingState.this.assetLock1.wait();
                        }
                        catch (InterruptedException ex) {}
                    }
                }
                new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getGameModeCacheDir(), File.separator)).mkdir();
                BaseVehicle.LoadAllVehicleTextures();
                if (GameClient.bClient) {
                    GameClient.instance.GameLoadingRequestData();
                }
                TutorialManager.instance = new TutorialManager();
                GameTime.setInstance(new GameTime());
                ClimateManager.setInstance(new ClimateManager());
                IsoWorld.instance = new IsoWorld();
                DebugOptions.testThreadCrash(0);
                IsoWorld.instance.init();
                if (GameWindow.bServerDisconnected) {
                    GameLoadingState.bDone = true;
                    return;
                }
                if (GameLoadingState.playerWrongIP) {
                    return;
                }
                if (GameLoadingState.build23Stop) {
                    return;
                }
                LuaEventManager.triggerEvent("OnGameTimeLoaded");
                SGlobalObjects.initSystems();
                CGlobalObjects.initSystems();
                IsoObjectPicker.Instance.Init();
                TutorialManager.instance.init();
                TutorialManager.instance.CreateQuests();
                if (ZomboidFileSystem.instance.getFileInCurrentSave("map_t.bin").exists()) {}
                if (!GameServer.bServer) {
                    final boolean b = !ZomboidFileSystem.instance.getFileInCurrentSave("map_ver.bin").exists();
                    if (b || IsoWorld.SavedWorldVersion != 186) {
                        if (!b && IsoWorld.SavedWorldVersion != 186) {
                            GameLoadingState.GameLoadingString = "Saving converted world.";
                        }
                        try {
                            GameWindow.save(true);
                        }
                        catch (Throwable t) {
                            ExceptionLogger.logException(t);
                        }
                    }
                }
                ChatUtility.InitAllowedChatIcons();
                ChatManager.getInstance().init(true, IsoPlayer.getInstance());
                GameLoadingState.this.bWaitForAssetLoadingToFinish2 = true;
                synchronized (GameLoadingState.this.assetLock2) {
                    while (GameLoadingState.this.bWaitForAssetLoadingToFinish2) {
                        try {
                            GameLoadingState.this.assetLock2.wait();
                        }
                        catch (InterruptedException ex2) {}
                    }
                }
                UIManager.bSuspend = false;
                GameLoadingState.playerCreated = true;
                GameLoadingState.GameLoadingString = "";
                GameLoadingState.Done();
            }
        });
        UIManager.bSuspend = true;
        GameLoadingState.loader.setName("GameLoadingThread");
        GameLoadingState.loader.setUncaughtExceptionHandler(GameWindow::uncaughtException);
        GameLoadingState.loader.start();
    }
    
    public static void Done() {
        GameLoadingState.bDone = true;
        GameKeyboard.bNoEventsWhileLoading = false;
        DebugLog.log(invokedynamic(makeConcatWithConstants:(J)Ljava/lang/String;, (System.currentTimeMillis() - GameLoadingState.startTime + 999L) / 1000L));
    }
    
    @Override
    public GameState redirectState() {
        return new IngameState();
    }
    
    @Override
    public void exit() {
        if (GameClient.bClient) {
            NetworkAIParams.Init();
        }
        UIManager.init();
        LuaEventManager.triggerEvent("OnCreatePlayer", 0, IsoPlayer.players[0]);
        GameLoadingState.loader = null;
        GameLoadingState.bDone = false;
        this.Stage = 0;
        IsoCamera.SetCharacterToFollow(IsoPlayer.getInstance());
        if (GameClient.bClient && !ServerOptions.instance.SafehouseAllowTrepass.getValue()) {
            final SafeHouse safeHouse = SafeHouse.isSafeHouse(IsoPlayer.getInstance().getCurrentSquare(), GameClient.username, true);
            if (safeHouse != null) {
                IsoPlayer.getInstance().setX((float)(safeHouse.getX() - 1));
                IsoPlayer.getInstance().setY((float)(safeHouse.getY() - 1));
            }
        }
        SoundManager.instance.stopMusic("");
        AmbientStreamManager.instance.init();
        if (IsoPlayer.getInstance() != null && IsoPlayer.getInstance().isAsleep()) {
            UIManager.setFadeBeforeUI(IsoPlayer.getInstance().getPlayerNum(), true);
            UIManager.FadeOut(IsoPlayer.getInstance().getPlayerNum(), 2.0);
            UIManager.setFadeTime(IsoPlayer.getInstance().getPlayerNum(), 0.0);
            UIManager.getSpeedControls().SetCurrentGameSpeed(3);
        }
        if (!GameClient.bClient) {
            final ActiveMods byId = ActiveMods.getById("currentGame");
            byId.checkMissingMods();
            byId.checkMissingMaps();
            ActiveMods.setLoadedMods(byId);
            new ActiveModsFile().write(ZomboidFileSystem.instance.getFileNameInCurrentSave("mods.txt"), byId);
        }
        GameWindow.OkToSaveOnExit = true;
    }
    
    @Override
    public void render() {
        this.loadingDotTick += GameTime.getInstance().getMultiplier();
        if (this.loadingDotTick > 20.0f) {
            this.loadingDot = ".";
        }
        if (this.loadingDotTick > 40.0f) {
            this.loadingDot = "..";
        }
        if (this.loadingDotTick > 60.0f) {
            this.loadingDot = "...";
        }
        if (this.loadingDotTick > 80.0f) {
            this.loadingDot = "";
            this.loadingDotTick = 0.0f;
        }
        this.Time += GameTime.instance.getTimeDelta();
        float n = 0.0f;
        float n2 = 0.0f;
        float n3 = 0.0f;
        if (this.Stage == 0) {
            final float time = this.Time;
            final float n4 = 0.0f;
            final float n5 = 1.0f;
            final float n6 = 5.0f;
            final float n7 = 7.0f;
            float n8 = 0.0f;
            if (time > n4 && time < n5) {
                n8 = (time - n4) / (n5 - n4);
            }
            if (time >= n5 && time <= n6) {
                n8 = 1.0f;
            }
            if (time > n6 && time < n7) {
                n8 = 1.0f - (time - n6) / (n7 - n6);
            }
            if (time >= n7) {
                ++this.Stage;
            }
            n = n8;
        }
        if (this.Stage == 1) {
            final float time2 = this.Time;
            final float n9 = 7.0f;
            final float n10 = 8.0f;
            final float n11 = 13.0f;
            final float n12 = 15.0f;
            float n13 = 0.0f;
            if (time2 > n9 && time2 < n10) {
                n13 = (time2 - n9) / (n10 - n9);
            }
            if (time2 >= n10 && time2 <= n11) {
                n13 = 1.0f;
            }
            if (time2 > n11 && time2 < n12) {
                n13 = 1.0f - (time2 - n11) / (n12 - n11);
            }
            if (time2 >= n12) {
                ++this.Stage;
            }
            n2 = n13;
        }
        if (this.Stage == 2) {
            final float time3 = this.Time;
            final float n14 = 15.0f;
            final float n15 = 16.0f;
            final float n16 = 31.0f;
            final float totalTime = this.TotalTime;
            float n17 = 0.0f;
            if (time3 > n14 && time3 < n15) {
                n17 = (time3 - n14) / (n15 - n14);
            }
            if (time3 >= n15 && time3 <= n16) {
                n17 = 1.0f;
            }
            if (time3 > n16 && time3 < totalTime) {
                n17 = 1.0f - (time3 - n16) / (totalTime - n16);
            }
            if (time3 >= totalTime) {
                ++this.Stage;
            }
            n3 = n17;
        }
        Core.getInstance().StartFrame();
        Core.getInstance().EndFrame();
        final boolean useUIFBO = UIManager.useUIFBO;
        UIManager.useUIFBO = false;
        Core.getInstance().StartFrameUI();
        SpriteRenderer.instance.renderi(null, 0, 0, Core.getInstance().getScreenWidth(), Core.getInstance().getScreenHeight(), 0.0f, 0.0f, 0.0f, 1.0f, null);
        if (GameLoadingState.mapDownloadFailed) {
            TextManager.instance.DrawStringCentre(UIFont.Medium, Core.getInstance().getScreenWidth() / 2, Core.getInstance().getScreenHeight() / 2 - TextManager.instance.getFontFromEnum(UIFont.Medium).getLineHeight() / 2, Translator.getText("UI_GameLoad_MapDownloadFailed"), 0.8, 0.1, 0.1, 1.0);
            UIManager.render();
            Core.getInstance().EndFrameUI();
            return;
        }
        if (GameLoadingState.unexpectedError) {
            final int lineHeight = TextManager.instance.getFontFromEnum(UIFont.Medium).getLineHeight();
            final int lineHeight2 = TextManager.instance.getFontFromEnum(UIFont.Small).getLineHeight();
            final int n18 = 8;
            final int n19 = 2;
            final int n20 = lineHeight + n18 + lineHeight2 + n19 + lineHeight2;
            final int n21 = Core.getInstance().getScreenWidth() / 2;
            final int n22 = Core.getInstance().getScreenHeight() / 2 - n20 / 2;
            TextManager.instance.DrawStringCentre(UIFont.Medium, n21, n22, Translator.getText("UI_GameLoad_UnexpectedError1"), 0.8, 0.1, 0.1, 1.0);
            TextManager.instance.DrawStringCentre(UIFont.Small, n21, n22 + lineHeight + n18, Translator.getText("UI_GameLoad_UnexpectedError2"), 1.0, 1.0, 1.0, 1.0);
            TextManager.instance.DrawStringCentre(UIFont.Small, (double)n21, (double)(n22 + lineHeight + n18 + lineHeight2 + n19), invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator), 1.0, 1.0, 1.0, 1.0);
            UIManager.render();
            Core.getInstance().EndFrameUI();
            return;
        }
        if (GameWindow.bServerDisconnected) {
            final int n23 = Core.getInstance().getScreenWidth() / 2;
            final int n24 = Core.getInstance().getScreenHeight() / 2;
            final int lineHeight3 = TextManager.instance.getFontFromEnum(UIFont.Medium).getLineHeight();
            final int n25 = n24 - (lineHeight3 + 2 + lineHeight3) / 2;
            String s = GameWindow.kickReason;
            if (s == null) {
                s = Translator.getText("UI_OnConnectFailed_ConnectionLost");
            }
            TextManager.instance.DrawStringCentre(UIFont.Medium, n23, n25, s, 0.8, 0.1, 0.1, 1.0);
            UIManager.render();
            Core.getInstance().EndFrameUI();
            return;
        }
        if (GameLoadingState.build23Stop) {
            TextManager.instance.DrawStringCentre(UIFont.Small, Core.getInstance().getScreenWidth() / 2, Core.getInstance().getScreenHeight() - 100, "This save is incompatible. Please switch to Steam branch \"build23\" to continue this save.", 0.8, 0.1, 0.1, 1.0);
        }
        else if (GameLoadingState.convertingWorld) {
            TextManager.instance.DrawStringCentre(UIFont.Small, Core.getInstance().getScreenWidth() / 2, Core.getInstance().getScreenHeight() - 100, Translator.getText("UI_ConvertWorld"), 0.5, 0.5, 0.5, 1.0);
            if (GameLoadingState.convertingFileMax != -1) {
                TextManager.instance.DrawStringCentre(UIFont.Small, (double)(Core.getInstance().getScreenWidth() / 2), (double)(Core.getInstance().getScreenHeight() - 100 + TextManager.instance.getFontFromEnum(UIFont.Small).getLineHeight() + 8), invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, GameLoadingState.convertingFileCount, GameLoadingState.convertingFileMax), 0.5, 0.5, 0.5, 1.0);
            }
        }
        if (GameLoadingState.playerWrongIP) {
            final int n26 = Core.getInstance().getScreenWidth() / 2;
            final int n27 = Core.getInstance().getScreenHeight() / 2;
            final int lineHeight4 = TextManager.instance.getFontFromEnum(UIFont.Medium).getLineHeight();
            final int n28 = n27 - (lineHeight4 + 2 + lineHeight4) / 2;
            String gameLoadingString = GameLoadingState.GameLoadingString;
            if (GameLoadingState.GameLoadingString == null) {
                gameLoadingString = "";
            }
            TextManager.instance.DrawStringCentre(UIFont.Medium, n26, n28, gameLoadingString, 0.8, 0.1, 0.1, 1.0);
            UIManager.render();
            Core.getInstance().EndFrameUI();
            return;
        }
        if (GameClient.bClient) {
            String gameLoadingString2 = GameLoadingState.GameLoadingString;
            if (GameLoadingState.GameLoadingString == null) {
                gameLoadingString2 = "";
            }
            TextManager.instance.DrawStringCentre(UIFont.Small, Core.getInstance().getScreenWidth() / 2, Core.getInstance().getScreenHeight() - 60, gameLoadingString2, 0.5, 0.5, 0.5, 1.0);
        }
        else if (!GameLoadingState.playerCreated && GameLoadingState.newGame && !Core.isLastStand()) {
            TextManager.instance.DrawStringCentre(UIFont.Small, Core.getInstance().getScreenWidth() / 2, Core.getInstance().getScreenHeight() - 60, Translator.getText("UI_Loading").replace(".", ""), 0.5, 0.5, 0.5, 1.0);
            TextManager.instance.DrawString(UIFont.Small, Core.getInstance().getScreenWidth() / 2 + TextManager.instance.MeasureStringX(UIFont.Small, Translator.getText("UI_Loading").replace(".", "")) / 2 + 1, Core.getInstance().getScreenHeight() - 60, this.loadingDot, 0.5, 0.5, 0.5, 1.0);
        }
        if (this.Stage == 0) {
            TextManager.instance.DrawStringCentre(UIFont.Intro, Core.getInstance().getScreenWidth() / 2, Core.getInstance().getScreenHeight() / 2 - TextManager.instance.getFontFromEnum(UIFont.Intro).getLineHeight() / 2, Translator.getText("UI_Intro1"), 1.0, 1.0, 1.0, n);
        }
        if (this.Stage == 1) {
            TextManager.instance.DrawStringCentre(UIFont.Intro, Core.getInstance().getScreenWidth() / 2, Core.getInstance().getScreenHeight() / 2 - TextManager.instance.getFontFromEnum(UIFont.Intro).getLineHeight() / 2, Translator.getText("UI_Intro2"), 1.0, 1.0, 1.0, n2);
        }
        if (this.Stage == 2) {
            TextManager.instance.DrawStringCentre(UIFont.Intro, Core.getInstance().getScreenWidth() / 2, Core.getInstance().getScreenHeight() / 2 - TextManager.instance.getFontFromEnum(UIFont.Intro).getLineHeight() / 2, Translator.getText("UI_Intro3"), 1.0, 1.0, 1.0, n3);
        }
        if (GameLoadingState.playerCreated && (!GameLoadingState.newGame || this.Time >= this.TotalTime || Core.isLastStand() || "Tutorial".equals(Core.GameMode))) {
            if (this.clickToSkipFadeIn) {
                this.clickToSkipAlpha += GameTime.getInstance().getMultiplier() / 1.6f / 30.0f;
                if (this.clickToSkipAlpha > 1.0f) {
                    this.clickToSkipAlpha = 1.0f;
                    this.clickToSkipFadeIn = false;
                }
            }
            else {
                this.clickToSkipAlpha -= GameTime.getInstance().getMultiplier() / 1.6f / 30.0f;
                if (this.clickToSkipAlpha < 0.25f) {
                    this.clickToSkipFadeIn = true;
                }
            }
            if (GameWindow.ActivatedJoyPad == null || JoypadManager.instance.JoypadList.isEmpty()) {
                TextManager.instance.DrawStringCentre(UIFont.NewLarge, Core.getInstance().getScreenWidth() / 2, Core.getInstance().getScreenHeight() - 60, Translator.getText("UI_ClickToSkip"), 1.0, 1.0, 1.0, this.clickToSkipAlpha);
            }
            else {
                final Texture sharedTexture = Texture.getSharedTexture("media/ui/xbox/XBOX_A.png");
                if (sharedTexture != null) {
                    SpriteRenderer.instance.renderi(sharedTexture, Core.getInstance().getScreenWidth() / 2 - TextManager.instance.MeasureStringX(UIFont.Small, Translator.getText("UI_PressAToStart")) / 2 - 8 - sharedTexture.getWidth(), Core.getInstance().getScreenHeight() - 60 + TextManager.instance.getFontFromEnum(UIFont.Small).getLineHeight() / 2 - sharedTexture.getHeight() / 2, sharedTexture.getWidth(), sharedTexture.getHeight(), 1.0f, 1.0f, 1.0f, this.clickToSkipAlpha, null);
                }
                TextManager.instance.DrawStringCentre(UIFont.Small, Core.getInstance().getScreenWidth() / 2, Core.getInstance().getScreenHeight() - 60, Translator.getText("UI_PressAToStart"), 1.0, 1.0, 1.0, this.clickToSkipAlpha);
            }
        }
        ActiveMods.renderUI();
        Core.getInstance().EndFrameUI();
        UIManager.useUIFBO = useUIFBO;
    }
    
    @Override
    public GameStateMachine.StateAction update() {
        if (this.bWaitForAssetLoadingToFinish1 && !OutfitManager.instance.isLoadingClothingItems()) {
            if (Core.bDebug) {
                OutfitManager.instance.debugOutfits();
            }
            synchronized (this.assetLock1) {
                this.bWaitForAssetLoadingToFinish1 = false;
                this.assetLock1.notifyAll();
            }
        }
        if (this.bWaitForAssetLoadingToFinish2 && !ModelManager.instance.isLoadingAnimations() && !GameWindow.fileSystem.hasWork()) {
            synchronized (this.assetLock2) {
                this.bWaitForAssetLoadingToFinish2 = false;
                this.assetLock2.notifyAll();
                final Iterator<RuntimeAnimationScript> iterator = ScriptManager.instance.getAllRuntimeAnimationScripts().iterator();
                while (iterator.hasNext()) {
                    iterator.next().exec();
                }
            }
        }
        if (GameLoadingState.unexpectedError || GameWindow.bServerDisconnected || GameLoadingState.playerWrongIP) {
            if (!GameLoadingState.bShowedUI) {
                GameLoadingState.bShowedUI = true;
                IsoPlayer.setInstance(null);
                IsoPlayer.players[0] = null;
                UIManager.UI.clear();
                LuaEventManager.Reset();
                LuaManager.call("ISGameLoadingUI_OnGameLoadingUI", "");
                UIManager.bSuspend = false;
            }
            if (Keyboard.isKeyDown(1)) {
                GameClient.instance.Shutdown();
                SteamUtils.shutdown();
                System.exit(1);
            }
            return GameStateMachine.StateAction.Remain;
        }
        if (!GameLoadingState.bDone) {
            return GameStateMachine.StateAction.Remain;
        }
        if (WorldStreamer.instance.isBusy()) {
            return GameStateMachine.StateAction.Remain;
        }
        if (ModelManager.instance.isLoadingAnimations()) {
            return GameStateMachine.StateAction.Remain;
        }
        if (Mouse.isButtonDown(0)) {
            this.bForceDone = true;
        }
        if (GameWindow.ActivatedJoyPad != null && GameWindow.ActivatedJoyPad.isAPressed()) {
            this.bForceDone = true;
        }
        if (this.bForceDone) {
            SoundManager.instance.playUISound("UIClickToStart");
            this.bForceDone = false;
            return GameStateMachine.StateAction.Continue;
        }
        return GameStateMachine.StateAction.Remain;
    }
    
    static {
        GameLoadingState.loader = null;
        GameLoadingState.newGame = true;
        GameLoadingState.build23Stop = false;
        GameLoadingState.unexpectedError = false;
        GameLoadingState.GameLoadingString = "";
        GameLoadingState.playerWrongIP = false;
        GameLoadingState.bShowedUI = false;
        GameLoadingState.mapDownloadFailed = false;
        GameLoadingState.playerCreated = false;
        GameLoadingState.bDone = false;
        GameLoadingState.convertingWorld = false;
        GameLoadingState.convertingFileCount = -1;
        GameLoadingState.convertingFileMax = -1;
    }
}

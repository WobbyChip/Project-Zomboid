// 
// Decompiled by Procyon v0.5.36
// 

package zombie.gameStates;

import zombie.core.profiling.PerformanceProfileProbe;
import zombie.network.MPStatistics;
import zombie.network.ServerGUI;
import zombie.network.ServerOptions;
import zombie.worldMap.editor.WorldMapEditorState;
import zombie.util.StringUtils;
import zombie.vehicles.EditVehicleState;
import zombie.savefile.ClientPlayerDB;
import zombie.core.skinnedmodel.advancedanimation.AdvancedAnimator;
import zombie.DebugFileWatcher;
import zombie.core.BoxedStaticValues;
import zombie.modding.ActiveMods;
import zombie.iso.sprite.SkyBox;
import zombie.debug.DebugOptions;
import zombie.debug.LineDrawer;
import zombie.core.skinnedmodel.model.ModelOutlines;
import zombie.iso.IsoUtils;
import zombie.chat.ChatElement;
import zombie.ui.TextDrawObject;
import zombie.ui.ActionProgressBar;
import zombie.ui.UIElement;
import zombie.IndieGL;
import zombie.ZomboidGlobals;
import zombie.core.Translator;
import zombie.core.Languages;
import zombie.SystemDisabler;
import zombie.GameWindow;
import zombie.input.GameKeyboard;
import zombie.input.JoypadManager;
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
import zombie.characters.traits.TraitFactory;
import zombie.characters.professions.ProfessionFactory;
import zombie.characters.SurvivorFactory;
import zombie.core.skinnedmodel.advancedanimation.AnimationSet;
import zombie.core.skinnedmodel.population.OutfitManager;
import zombie.core.skinnedmodel.population.HairStyles;
import zombie.core.skinnedmodel.population.BeardStyles;
import zombie.core.skinnedmodel.population.ClothingDecals;
import zombie.ui.TutorialManager;
import zombie.globalObjects.SGlobalObjects;
import zombie.globalObjects.CGlobalObjects;
import zombie.Lua.MapObjects;
import zombie.vehicles.VehicleType;
import zombie.ReanimatedPlayers;
import zombie.core.physics.WorldSimulation;
import zombie.worldMap.WorldMap;
import zombie.vehicles.VehiclesDB2;
import zombie.savefile.PlayerDB;
import zombie.core.skinnedmodel.DeadBodyAtlas;
import zombie.network.PassengerMap;
import zombie.spnetwork.SinglePlayerServer;
import zombie.spnetwork.SinglePlayerClient;
import zombie.network.ClientServerMap;
import zombie.network.ChunkChecksum;
import zombie.iso.IsoChunk;
import zombie.vehicles.VehicleIDMap;
import zombie.vehicles.VehicleCache;
import zombie.core.stash.StashSystem;
import zombie.iso.objects.IsoGenerator;
import zombie.erosion.ErosionGlobals;
import zombie.radio.ZomboidRadio;
import zombie.iso.IsoMarkers;
import zombie.iso.WorldMarkers;
import zombie.iso.weather.Temperature;
import zombie.iso.areas.isoregion.IsoRegions;
import zombie.iso.weather.fx.WeatherFxMask;
import zombie.core.skinnedmodel.ModelManager;
import zombie.iso.IsoChunkMap;
import zombie.iso.WorldStreamer;
import zombie.core.opengl.RenderThread;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.IsoCamera;
import zombie.ui.FPSGraph;
import zombie.ui.ServerPulseGraph;
import zombie.core.znet.SteamFriends;
import zombie.core.znet.SteamUtils;
import zombie.debug.DebugLog;
import zombie.Lua.LuaEventManager;
import zombie.iso.weather.ClimateManager;
import zombie.SoundManager;
import zombie.characters.IsoGameCharacter;
import zombie.iso.LightingThread;
import zombie.ZombieSpawnRecorder;
import zombie.core.Rand;
import zombie.GameSounds;
import zombie.core.opengl.RenderSettings;
import zombie.iso.SearchMode;
import zombie.worldMap.WorldMapVisited;
import zombie.Lua.LuaManager;
import zombie.iso.sprite.CorpseFlies;
import zombie.FliesSound;
import zombie.inventory.ItemSoundManager;
import zombie.network.BodyDamageSync;
import zombie.AmbientStreamManager;
import zombie.LootRespawn;
import zombie.vehicles.PolygonalMap2;
import zombie.popman.ZombiePopulationManager;
import zombie.MapCollisionData;
import zombie.VirtualZombieManager;
import zombie.meta.Meta;
import zombie.iso.objects.RainManager;
import zombie.iso.objects.IsoFireManager;
import zombie.core.logger.ExceptionLogger;
import zombie.WorldSoundManager;
import zombie.scripting.ScriptManager;
import zombie.ui.UIManager;
import zombie.characters.IsoPlayer;
import zombie.network.GameServer;
import zombie.GameTime;
import zombie.network.GameClient;
import java.nio.channels.ReadableByteChannel;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import zombie.ZomboidFileSystem;
import zombie.iso.LotHeader;
import zombie.iso.IsoMetaCell;
import zombie.iso.IsoMetaGrid;
import zombie.iso.BuildingDef;
import zombie.iso.IsoWorld;
import zombie.core.Core;
import zombie.iso.IsoGridSquare;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.ui.TextManager;
import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;
import zombie.core.textures.Texture;
import zombie.core.SpriteRenderer;
import zombie.input.Mouse;
import zombie.iso.IsoCell;
import java.util.ArrayList;
import java.util.HashMap;

public final class IngameState extends GameState
{
    public static int WaitMul;
    public static IngameState instance;
    public static float draww;
    public static float drawh;
    public static Long GameID;
    static int last;
    static float xPos;
    static float yPos;
    static float offx;
    static float offy;
    static float zoom;
    static HashMap<String, Integer> ContainerTypes;
    static int nSaveCycle;
    static boolean bDoChars;
    static boolean keySpacePreviousFrame;
    public long numberTicks;
    public boolean Paused;
    public float SaveDelay;
    boolean alt;
    int insanityScareCount;
    boolean MDebounce;
    int insanitypic;
    int timesincelastinsanity;
    GameState RedirectState;
    boolean bDidServerDisconnectState;
    boolean fpsKeyDown;
    private final ArrayList<Long> debugTimes;
    private int tickCount;
    private float SadisticMusicDirectorTime;
    public boolean showAnimationViewer;
    public boolean showAttachmentEditor;
    public boolean showChunkDebugger;
    public boolean showGlobalObjectDebugger;
    public String showVehicleEditor;
    public String showWorldMapEditor;
    
    public IngameState() {
        this.numberTicks = 0L;
        this.Paused = false;
        this.SaveDelay = 0.0f;
        this.alt = false;
        this.insanityScareCount = 5;
        this.MDebounce = false;
        this.insanitypic = -1;
        this.timesincelastinsanity = 10000000;
        this.RedirectState = null;
        this.bDidServerDisconnectState = false;
        this.fpsKeyDown = false;
        this.debugTimes = new ArrayList<Long>();
        this.tickCount = 0;
        this.showAnimationViewer = false;
        this.showAttachmentEditor = false;
        this.showChunkDebugger = false;
        this.showGlobalObjectDebugger = false;
        this.showVehicleEditor = null;
        this.showWorldMapEditor = null;
        IngameState.instance = this;
    }
    
    public static void renderDebugOverhead(final IsoCell isoCell, final int n, final int n2, final int n3, final int n4) {
        Mouse.update();
        final int x = Mouse.getX();
        final int y = Mouse.getY();
        final int n5 = x - n3;
        final int n6 = y - n4;
        final int n7 = n5 / n2;
        final int n8 = n6 / n2;
        SpriteRenderer.instance.renderi(null, n3, n4, n2 * isoCell.getWidthInTiles(), n2 * isoCell.getHeightInTiles(), 0.7f, 0.7f, 0.7f, 1.0f, null);
        final IsoGridSquare gridSquare = isoCell.getGridSquare(n7 + isoCell.ChunkMap[0].getWorldXMinTiles(), n8 + isoCell.ChunkMap[0].getWorldYMinTiles(), 0);
        if (gridSquare != null) {
            int n9 = 48;
            int n10 = 48;
            TextManager.instance.DrawString(n10, n9, "SQUARE FLAGS", 1.0, 1.0, 1.0, 1.0);
            n9 += 20;
            n10 += 8;
            for (int i = 0; i < IsoFlagType.MAX.index(); ++i) {
                if (gridSquare.Is(IsoFlagType.fromIndex(i))) {
                    TextManager.instance.DrawString(n10, n9, IsoFlagType.fromIndex(i).toString(), 0.6, 0.6, 0.8, 1.0);
                    n9 += 18;
                }
            }
            int n11 = 48;
            n9 += 16;
            TextManager.instance.DrawString(n11, n9, "SQUARE OBJECT TYPES", 1.0, 1.0, 1.0, 1.0);
            n9 += 20;
            n11 += 8;
            for (int j = 0; j < 64; ++j) {
                if (gridSquare.getHasTypes().isSet(j)) {
                    TextManager.instance.DrawString(n11, n9, IsoObjectType.fromIndex(j).toString(), 0.6, 0.6, 0.8, 1.0);
                    n9 += 18;
                }
            }
        }
        for (int k = 0; k < isoCell.getWidthInTiles(); ++k) {
            for (int l = 0; l < isoCell.getHeightInTiles(); ++l) {
                final IsoGridSquare gridSquare2 = isoCell.getGridSquare(k + isoCell.ChunkMap[0].getWorldXMinTiles(), l + isoCell.ChunkMap[0].getWorldYMinTiles(), n);
                if (gridSquare2 != null) {
                    if (gridSquare2.getProperties().Is(IsoFlagType.solid) || gridSquare2.getProperties().Is(IsoFlagType.solidtrans)) {
                        SpriteRenderer.instance.renderi(null, n3 + k * n2, n4 + l * n2, n2, n2, 0.5f, 0.5f, 0.5f, 255.0f, null);
                    }
                    else if (!gridSquare2.getProperties().Is(IsoFlagType.exterior)) {
                        SpriteRenderer.instance.renderi(null, n3 + k * n2, n4 + l * n2, n2, n2, 0.8f, 0.8f, 0.8f, 1.0f, null);
                    }
                    if (gridSquare2.Has(IsoObjectType.tree)) {
                        SpriteRenderer.instance.renderi(null, n3 + k * n2, n4 + l * n2, n2, n2, 0.4f, 0.8f, 0.4f, 1.0f, null);
                    }
                    if (gridSquare2.getProperties().Is(IsoFlagType.collideN)) {
                        SpriteRenderer.instance.renderi(null, n3 + k * n2, n4 + l * n2, n2, 1, 0.2f, 0.2f, 0.2f, 1.0f, null);
                    }
                    if (gridSquare2.getProperties().Is(IsoFlagType.collideW)) {
                        SpriteRenderer.instance.renderi(null, n3 + k * n2, n4 + l * n2, 1, n2, 0.2f, 0.2f, 0.2f, 1.0f, null);
                    }
                }
            }
        }
    }
    
    public static float translatePointX(float n, final float n2, final float n3, final float n4) {
        n -= n2;
        n *= n3;
        n += n4;
        n += IngameState.draww / 2.0f;
        return n;
    }
    
    public static float invTranslatePointX(float n, final float n2, final float n3, final float n4) {
        n -= IngameState.draww / 2.0f;
        n -= n4;
        n /= n3;
        n += n2;
        return n;
    }
    
    public static float invTranslatePointY(float n, final float n2, final float n3, final float n4) {
        n -= IngameState.drawh / 2.0f;
        n -= n4;
        n /= n3;
        n += n2;
        return n;
    }
    
    public static float translatePointY(float n, final float n2, final float n3, final float n4) {
        n -= n2;
        n *= n3;
        n += n4;
        n += IngameState.drawh / 2.0f;
        return n;
    }
    
    public static void renderRect(final float n, final float n2, float n3, float n4, final float n5, final float n6, final float n7, final float n8) {
        final float translatePointX = translatePointX(n, IngameState.xPos, IngameState.zoom, IngameState.offx);
        final float translatePointY = translatePointY(n2, IngameState.yPos, IngameState.zoom, IngameState.offy);
        final float translatePointX2 = translatePointX(n + n3, IngameState.xPos, IngameState.zoom, IngameState.offx);
        final float translatePointY2 = translatePointY(n2 + n4, IngameState.yPos, IngameState.zoom, IngameState.offy);
        n3 = translatePointX2 - translatePointX;
        n4 = translatePointY2 - translatePointY;
        if (translatePointX >= Core.getInstance().getScreenWidth() || translatePointX2 < 0.0f || translatePointY >= Core.getInstance().getScreenHeight() || translatePointY2 < 0.0f) {
            return;
        }
        SpriteRenderer.instance.render(null, translatePointX, translatePointY, n3, n4, n5, n6, n7, n8, null);
    }
    
    public static void renderLine(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8) {
        final float translatePointX = translatePointX(n, IngameState.xPos, IngameState.zoom, IngameState.offx);
        final float translatePointY = translatePointY(n2, IngameState.yPos, IngameState.zoom, IngameState.offy);
        final float translatePointX2 = translatePointX(n3, IngameState.xPos, IngameState.zoom, IngameState.offx);
        final float translatePointY2 = translatePointY(n4, IngameState.yPos, IngameState.zoom, IngameState.offy);
        if ((translatePointX >= Core.getInstance().getScreenWidth() && translatePointX2 >= Core.getInstance().getScreenWidth()) || (translatePointY >= Core.getInstance().getScreenHeight() && translatePointY2 >= Core.getInstance().getScreenHeight()) || (translatePointX < 0.0f && translatePointX2 < 0.0f) || (translatePointY < 0.0f && translatePointY2 < 0.0f)) {
            return;
        }
        SpriteRenderer.instance.renderline(null, (int)translatePointX, (int)translatePointY, (int)translatePointX2, (int)translatePointY2, n5, n6, n7, n8);
    }
    
    public static void renderDebugOverhead2(final IsoCell isoCell, final int n, final float zoom, final int n2, final int n3, final float xPos, final float yPos, final int n4, final int n5) {
        IngameState.draww = (float)n4;
        IngameState.drawh = (float)n5;
        IngameState.xPos = xPos;
        IngameState.yPos = yPos;
        IngameState.offx = (float)n2;
        IngameState.offy = (float)n3;
        IngameState.zoom = zoom;
        final float n6 = (float)isoCell.ChunkMap[0].getWorldXMinTiles();
        final float n7 = (float)isoCell.ChunkMap[0].getWorldYMinTiles();
        final float n8 = (float)isoCell.ChunkMap[0].getWorldXMaxTiles();
        final float n9 = (float)isoCell.ChunkMap[0].getWorldYMaxTiles();
        renderRect(n6, n7, (float)isoCell.getWidthInTiles(), (float)isoCell.getWidthInTiles(), 0.7f, 0.7f, 0.7f, 1.0f);
        for (int i = 0; i < isoCell.getWidthInTiles(); ++i) {
            for (int j = 0; j < isoCell.getHeightInTiles(); ++j) {
                final IsoGridSquare gridSquare = isoCell.getGridSquare(i + isoCell.ChunkMap[0].getWorldXMinTiles(), j + isoCell.ChunkMap[0].getWorldYMinTiles(), n);
                final float n10 = i + n6;
                final float n11 = j + n7;
                if (gridSquare != null) {
                    if (gridSquare.getProperties().Is(IsoFlagType.solid) || gridSquare.getProperties().Is(IsoFlagType.solidtrans)) {
                        renderRect(n10, n11, 1.0f, 1.0f, 0.5f, 0.5f, 0.5f, 1.0f);
                    }
                    else if (!gridSquare.getProperties().Is(IsoFlagType.exterior)) {
                        renderRect(n10, n11, 1.0f, 1.0f, 0.8f, 0.8f, 0.8f, 1.0f);
                    }
                    if (gridSquare.Has(IsoObjectType.tree)) {
                        renderRect(n10, n11, 1.0f, 1.0f, 0.4f, 0.8f, 0.4f, 1.0f);
                    }
                    if (gridSquare.getProperties().Is(IsoFlagType.collideN)) {
                        renderRect(n10, n11, 1.0f, 0.2f, 0.2f, 0.2f, 0.2f, 1.0f);
                    }
                    if (gridSquare.getProperties().Is(IsoFlagType.collideW)) {
                        renderRect(n10, n11, 0.2f, 1.0f, 0.2f, 0.2f, 0.2f, 1.0f);
                    }
                }
            }
        }
        final IsoMetaGrid metaGrid = IsoWorld.instance.MetaGrid;
        renderRect((float)(metaGrid.minX * 300), (float)(metaGrid.minY * 300), (float)(metaGrid.getWidth() * 300), (float)(metaGrid.getHeight() * 300), 1.0f, 1.0f, 1.0f, 0.05f);
        if (zoom > 0.1) {
            for (int k = metaGrid.minY; k <= metaGrid.maxY; ++k) {
                renderLine((float)(metaGrid.minX * 300), (float)(k * 300), (float)((metaGrid.maxX + 1) * 300), (float)(k * 300), 1.0f, 1.0f, 1.0f, 0.15f);
            }
            for (int l = metaGrid.minX; l <= metaGrid.maxX; ++l) {
                renderLine((float)(l * 300), (float)(metaGrid.minY * 300), (float)(l * 300), (float)((metaGrid.maxY + 1) * 300), 1.0f, 1.0f, 1.0f, 0.15f);
            }
        }
        final IsoMetaCell[][] grid = IsoWorld.instance.MetaGrid.Grid;
        for (int n12 = 0; n12 < grid.length; ++n12) {
            for (int n13 = 0; n13 < grid[0].length; ++n13) {
                final LotHeader info = grid[n12][n13].info;
                if (info == null) {
                    renderRect((float)((metaGrid.minX + n12) * 300 + 1), (float)((metaGrid.minY + n13) * 300 + 1), 298.0f, 298.0f, 0.2f, 0.0f, 0.0f, 0.3f);
                }
                else {
                    for (int index = 0; index < info.Buildings.size(); ++index) {
                        final BuildingDef buildingDef = info.Buildings.get(index);
                        if (buildingDef.bAlarmed) {
                            renderRect((float)buildingDef.getX(), (float)buildingDef.getY(), (float)buildingDef.getW(), (float)buildingDef.getH(), 0.8f, 0.8f, 0.5f, 0.3f);
                        }
                        else {
                            renderRect((float)buildingDef.getX(), (float)buildingDef.getY(), (float)buildingDef.getW(), (float)buildingDef.getH(), 0.5f, 0.5f, 0.8f, 0.3f);
                        }
                    }
                }
            }
        }
    }
    
    public static void copyWorld(final String s, final String s2) {
        final String replace = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getGameModeCacheDir(), File.separator, s, File.separator).replace("/", File.separator).replace("\\", File.separator);
        final File file = new File(replace.substring(0, replace.lastIndexOf(File.separator)).replace("\\", "/"));
        final String replace2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getGameModeCacheDir(), File.separator, s2, File.separator).replace("/", File.separator).replace("\\", File.separator);
        final File file2 = new File(replace2.substring(0, replace2.lastIndexOf(File.separator)).replace("\\", "/"));
        try {
            copyDirectory(file, file2);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public static void copyDirectory(final File file, final File file2) throws IOException {
        if (file.isDirectory()) {
            if (!file2.exists()) {
                file2.mkdir();
            }
            final String[] list = file.list();
            final boolean b = GameLoadingState.convertingFileMax == -1;
            if (b) {
                GameLoadingState.convertingFileMax = list.length;
            }
            for (int i = 0; i < list.length; ++i) {
                if (b) {
                    ++GameLoadingState.convertingFileCount;
                }
                copyDirectory(new File(file, list[i]), new File(file2, list[i]));
            }
        }
        else {
            final FileInputStream fileInputStream = new FileInputStream(file);
            final FileOutputStream fileOutputStream = new FileOutputStream(file2);
            fileOutputStream.getChannel().transferFrom(fileInputStream.getChannel(), 0L, file.length());
            fileInputStream.close();
            fileOutputStream.close();
        }
    }
    
    public static void createWorld(String trim) {
        trim = trim.replace(" ", "_").trim();
        final String replace = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getGameModeCacheDir(), File.separator, trim, File.separator).replace("/", File.separator).replace("\\", File.separator);
        final File file = new File(replace.substring(0, replace.lastIndexOf(File.separator)).replace("\\", "/"));
        if (!file.exists()) {
            file.mkdirs();
        }
        Core.GameSaveWorld = trim;
    }
    
    public void debugFullyStreamedIn(final int n, final int n2) {
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n, n2, 0);
        if (gridSquare == null) {
            return;
        }
        if (gridSquare.getBuilding() == null) {
            return;
        }
        final BuildingDef def = gridSquare.getBuilding().getDef();
        if (def == null) {
            return;
        }
        final boolean fullyStreamedIn = def.isFullyStreamedIn();
        for (int i = 0; i < def.overlappedChunks.size(); i += 2) {
            final short value = def.overlappedChunks.get(i);
            final short value2 = def.overlappedChunks.get(i + 1);
            if (fullyStreamedIn) {
                renderRect((float)(value * 10), (float)(value2 * 10), 10.0f, 10.0f, 0.0f, 1.0f, 0.0f, 0.5f);
            }
            else {
                renderRect((float)(value * 10), (float)(value2 * 10), 10.0f, 10.0f, 1.0f, 0.0f, 0.0f, 0.5f);
            }
        }
    }
    
    public void UpdateStuff() {
        GameClient.bIngame = true;
        this.SaveDelay += GameTime.instance.getMultiplier();
        if (this.SaveDelay / 60.0f > 30.0f) {
            this.SaveDelay = 0.0f;
        }
        GameTime.instance.LastLastTimeOfDay = GameTime.instance.getLastTimeOfDay();
        GameTime.instance.setLastTimeOfDay(GameTime.getInstance().getTimeOfDay());
        boolean allPlayersAsleep = false;
        if (!GameServer.bServer && IsoPlayer.getInstance() != null) {
            allPlayersAsleep = IsoPlayer.allPlayersAsleep();
        }
        GameTime.getInstance().update(allPlayersAsleep && UIManager.getFadeAlpha() == 1.0);
        if (!this.Paused) {
            ScriptManager.instance.update();
        }
        if (!this.Paused) {
            System.nanoTime();
            try {
                WorldSoundManager.instance.update();
            }
            catch (Exception ex) {
                ExceptionLogger.logException(ex);
            }
            try {
                IsoFireManager.Update();
            }
            catch (Exception ex2) {
                ExceptionLogger.logException(ex2);
            }
            try {
                RainManager.Update();
            }
            catch (Exception ex3) {
                ExceptionLogger.logException(ex3);
            }
            Meta.instance.update();
            try {
                VirtualZombieManager.instance.update();
                MapCollisionData.instance.updateMain();
                ZombiePopulationManager.instance.updateMain();
                PolygonalMap2.instance.updateMain();
            }
            catch (Exception ex4) {
                ExceptionLogger.logException(ex4);
            }
            catch (Error error) {
                error.printStackTrace();
            }
            try {
                LootRespawn.update();
            }
            catch (Exception ex5) {
                ExceptionLogger.logException(ex5);
            }
            if (GameServer.bServer) {
                try {
                    AmbientStreamManager.instance.update();
                }
                catch (Exception ex6) {
                    ExceptionLogger.logException(ex6);
                }
            }
            if (GameClient.bClient) {
                try {
                    BodyDamageSync.instance.update();
                }
                catch (Exception ex7) {
                    ExceptionLogger.logException(ex7);
                }
            }
            if (!GameServer.bServer) {
                try {
                    ItemSoundManager.update();
                    FliesSound.instance.update();
                    CorpseFlies.update();
                    LuaManager.call("SadisticMusicDirectorTick", null);
                    WorldMapVisited.update();
                }
                catch (Exception ex8) {
                    ExceptionLogger.logException(ex8);
                }
            }
            SearchMode.getInstance().update();
            RenderSettings.getInstance().update();
            System.nanoTime();
        }
    }
    
    @Override
    public void enter() {
        UIManager.useUIFBO = (Core.getInstance().supportsFBO() && Core.OptionUIFBO);
        if (!Core.getInstance().getUseShaders()) {
            Core.getInstance().RenderShader = null;
        }
        GameSounds.fix3DListenerPosition(false);
        IsoPlayer.getInstance().updateUsername();
        IsoPlayer.getInstance().setSceneCulled(false);
        IsoPlayer.getInstance().getInventory().addItemsToProcessItems();
        IngameState.GameID = (long)Rand.Next(10000000);
        IngameState.GameID += (Long)Rand.Next(10000000);
        IngameState.GameID += (Long)Rand.Next(10000000);
        IngameState.GameID += (Long)Rand.Next(10000000);
        IngameState.GameID += (Long)Rand.Next(10000000);
        IngameState.GameID += (Long)Rand.Next(10000000);
        IngameState.GameID += (Long)Rand.Next(10000000);
        IngameState.GameID += (Long)Rand.Next(10000000);
        IngameState.GameID += (Long)Rand.Next(10000000);
        IngameState.GameID += (Long)Rand.Next(10000000);
        IngameState.GameID += (Long)Rand.Next(10000000);
        IngameState.GameID += (Long)Rand.Next(10000000);
        IngameState.GameID += (Long)Rand.Next(10000000);
        IngameState.GameID += (Long)Rand.Next(10000000);
        IngameState.GameID += (Long)Rand.Next(10000000);
        IngameState.GameID += (Long)Rand.Next(10000000);
        IngameState.GameID += (Long)Rand.Next(10000000);
        IngameState.GameID += (Long)Rand.Next(10000000);
        IngameState.GameID += (Long)Rand.Next(10000000);
        IngameState.GameID += (Long)Rand.Next(10000000);
        IngameState.GameID += (Long)Rand.Next(10000000);
        IngameState.GameID += (Long)Rand.Next(10000000);
        ZombieSpawnRecorder.instance.init();
        if (!GameServer.bServer) {
            IsoWorld.instance.CurrentCell.ChunkMap[0].processAllLoadGridSquare();
            IsoWorld.instance.CurrentCell.ChunkMap[0].update();
            if (!GameClient.bClient) {
                LightingThread.instance.GameLoadingUpdate();
            }
        }
        try {
            MapCollisionData.instance.startGame();
        }
        catch (Throwable t) {
            ExceptionLogger.logException(t);
        }
        IsoWorld.instance.CurrentCell.putInVehicle(IsoPlayer.getInstance());
        SoundManager.instance.setMusicState("Tutorial".equals(Core.GameMode) ? "Tutorial" : "InGame");
        ClimateManager.getInstance().update();
        LuaEventManager.triggerEvent("OnGameStart");
        LuaEventManager.triggerEvent("OnLoad");
        if (GameClient.bClient) {
            GameClient.instance.sendPlayerConnect(IsoPlayer.getInstance());
            DebugLog.log("Waiting for player-connect response from server");
            while (IsoPlayer.getInstance().OnlineID == -1) {
                try {
                    Thread.sleep(10L);
                }
                catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                GameClient.instance.update();
            }
            ClimateManager.getInstance().update();
            LightingThread.instance.GameLoadingUpdate();
        }
        if (GameClient.bClient && SteamUtils.isSteamModeEnabled()) {
            SteamFriends.UpdateRichPresenceConnectionInfo("In game", invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, GameClient.ip, GameClient.port));
        }
    }
    
    @Override
    public void exit() {
        DebugLog.log("EXITDEBUG: IngameState.exit 1");
        if (SteamUtils.isSteamModeEnabled()) {
            SteamFriends.UpdateRichPresenceConnectionInfo("", "");
        }
        UIManager.useUIFBO = false;
        if (ServerPulseGraph.instance != null) {
            ServerPulseGraph.instance.setVisible(false);
        }
        if (FPSGraph.instance != null) {
            FPSGraph.instance.setVisible(false);
        }
        UIManager.updateBeforeFadeOut();
        SoundManager.instance.setMusicState("MainMenu");
        final long currentTimeMillis = System.currentTimeMillis();
        final boolean useUIFBO = UIManager.useUIFBO;
        UIManager.useUIFBO = false;
        DebugLog.log("EXITDEBUG: IngameState.exit 2");
        while (true) {
            final float min = Math.min(1.0f, (System.currentTimeMillis() - currentTimeMillis) / 500.0f);
            boolean b = true;
            for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
                if (IsoPlayer.players[i] != null) {
                    IsoPlayer.setInstance(IsoPlayer.players[i]);
                    IsoCamera.CamCharacter = IsoPlayer.players[i];
                    IsoSprite.globalOffsetX = -1.0f;
                    Core.getInstance().StartFrame(i, b);
                    IsoCamera.frameState.set(i);
                    IsoWorld.instance.render();
                    Core.getInstance().EndFrame(i);
                    b = false;
                }
            }
            Core.getInstance().RenderOffScreenBuffer();
            Core.getInstance().StartFrameUI();
            UIManager.render();
            UIManager.DrawTexture(UIManager.getBlack(), 0.0, 0.0, Core.getInstance().getScreenWidth(), Core.getInstance().getScreenHeight(), min);
            Core.getInstance().EndFrameUI();
            DebugLog.log(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, min));
            if (min >= 1.0f) {
                break;
            }
            try {
                Thread.sleep(33L);
            }
            catch (Exception ex4) {}
        }
        UIManager.useUIFBO = useUIFBO;
        DebugLog.log("EXITDEBUG: IngameState.exit 4");
        RenderThread.setWaitForRenderState(false);
        SpriteRenderer.instance.notifyRenderStateQueue();
        while (WorldStreamer.instance.isBusy()) {
            try {
                Thread.sleep(1L);
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        DebugLog.log("EXITDEBUG: IngameState.exit 5");
        WorldStreamer.instance.stop();
        LightingThread.instance.stop();
        MapCollisionData.instance.stop();
        ZombiePopulationManager.instance.stop();
        PolygonalMap2.instance.stop();
        DebugLog.log("EXITDEBUG: IngameState.exit 6");
        for (int j = 0; j < IsoWorld.instance.CurrentCell.ChunkMap.length; ++j) {
            final IsoChunkMap isoChunkMap = IsoWorld.instance.CurrentCell.ChunkMap[j];
            for (int k = 0; k < IsoChunkMap.ChunkGridWidth * IsoChunkMap.ChunkGridWidth; ++k) {
                final IsoChunk chunk = isoChunkMap.getChunk(k % IsoChunkMap.ChunkGridWidth, k / IsoChunkMap.ChunkGridWidth);
                if (chunk != null) {
                    if (chunk.refs.contains(isoChunkMap)) {
                        chunk.refs.remove(isoChunkMap);
                        if (chunk.refs.isEmpty()) {
                            chunk.removeFromWorld();
                            chunk.doReuseGridsquares();
                        }
                    }
                }
            }
        }
        ModelManager.instance.Reset();
        for (int l = 0; l < 4; ++l) {
            IsoPlayer.players[l] = null;
        }
        ZombieSpawnRecorder.instance.quit();
        DebugLog.log("EXITDEBUG: IngameState.exit 7");
        IsoPlayer.numPlayers = 1;
        Core.getInstance().OffscreenBuffer.destroy();
        WeatherFxMask.destroy();
        IsoRegions.reset();
        Temperature.reset();
        WorldMarkers.instance.reset();
        IsoMarkers.instance.reset();
        SearchMode.reset();
        ZomboidRadio.getInstance().Reset();
        ErosionGlobals.Reset();
        IsoGenerator.Reset();
        StashSystem.Reset();
        LootRespawn.Reset();
        VehicleCache.Reset();
        VehicleIDMap.instance.Reset();
        IsoWorld.instance.KillCell();
        ItemSoundManager.Reset();
        IsoChunk.Reset();
        ChunkChecksum.Reset();
        ClientServerMap.Reset();
        SinglePlayerClient.Reset();
        SinglePlayerServer.Reset();
        PassengerMap.Reset();
        DeadBodyAtlas.instance.Reset();
        CorpseFlies.Reset();
        if (PlayerDB.isAvailable()) {
            PlayerDB.getInstance().close();
        }
        VehiclesDB2.instance.Reset();
        WorldMap.Reset();
        WorldStreamer.instance = new WorldStreamer();
        WorldSimulation.instance.destroy();
        WorldSimulation.instance = new WorldSimulation();
        DebugLog.log("EXITDEBUG: IngameState.exit 8");
        VirtualZombieManager.instance.Reset();
        VirtualZombieManager.instance = new VirtualZombieManager();
        ReanimatedPlayers.instance = new ReanimatedPlayers();
        ScriptManager.instance.Reset();
        GameSounds.Reset();
        VehicleType.Reset();
        LuaEventManager.Reset();
        MapObjects.Reset();
        CGlobalObjects.Reset();
        SGlobalObjects.Reset();
        AmbientStreamManager.instance.stop();
        SoundManager.instance.stop();
        IsoPlayer.setInstance(null);
        IsoCamera.CamCharacter = null;
        TutorialManager.instance.StealControl = false;
        UIManager.init();
        ScriptManager.instance.Reset();
        ClothingDecals.Reset();
        BeardStyles.Reset();
        HairStyles.Reset();
        OutfitManager.Reset();
        AnimationSet.Reset();
        GameSounds.Reset();
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
        LuaManager.init();
        JoypadManager.instance.Reset();
        GameKeyboard.doLuaKeyPressed = true;
        GameWindow.ActivatedJoyPad = null;
        GameWindow.OkToSaveOnExit = false;
        GameWindow.bLoadedAsClient = false;
        Core.bLastStand = false;
        Core.ChallengeID = null;
        Core.bTutorial = false;
        Core.getInstance().setChallenge(false);
        Core.getInstance().setForceSnow(false);
        Core.getInstance().setZombieGroupSound(true);
        Core.getInstance().setFlashIsoCursor(false);
        SystemDisabler.Reset();
        Texture.nullTextures.clear();
        DebugLog.log("EXITDEBUG: IngameState.exit 9");
        ZomboidFileSystem.instance.Reset();
        try {
            ZomboidFileSystem.instance.init();
        }
        catch (IOException ex2) {
            ExceptionLogger.logException(ex2);
        }
        Core.OptionModsEnabled = true;
        DebugLog.log("EXITDEBUG: IngameState.exit 10");
        ZomboidFileSystem.instance.loadMods("default");
        ZomboidFileSystem.instance.loadModPackFiles();
        ModelManager.instance.loadModAnimations();
        Languages.instance.init();
        Translator.loadFiles();
        DebugLog.log("EXITDEBUG: IngameState.exit 11");
        CustomPerks.instance.init();
        CustomPerks.instance.initLua();
        CustomSandboxOptions.instance.init();
        CustomSandboxOptions.instance.initInstance(SandboxOptions.instance);
        ScriptManager.instance.Load();
        ClothingDecals.init();
        BeardStyles.init();
        HairStyles.init();
        OutfitManager.init();
        DebugLog.log("EXITDEBUG: IngameState.exit 12");
        try {
            TextManager.instance.Init();
            LuaManager.LoadDirBase();
        }
        catch (Exception ex3) {
            ExceptionLogger.logException(ex3);
        }
        ZomboidGlobals.Load();
        DebugLog.log("EXITDEBUG: IngameState.exit 13");
        LuaEventManager.triggerEvent("OnGameBoot");
        SoundManager.instance.resumeSoundAndMusic();
        for (final IsoPlayer isoPlayer : IsoPlayer.players) {
            if (isoPlayer != null) {
                isoPlayer.dirtyRecalcGridStack = true;
            }
        }
        RenderThread.setWaitForRenderState(true);
        DebugLog.log("EXITDEBUG: IngameState.exit 14");
    }
    
    @Override
    public void yield() {
        SoundManager.instance.setMusicState("PauseMenu");
    }
    
    @Override
    public GameState redirectState() {
        if (this.RedirectState != null) {
            final GameState redirectState = this.RedirectState;
            this.RedirectState = null;
            return redirectState;
        }
        return new MainScreenState();
    }
    
    @Override
    public void reenter() {
        SoundManager.instance.setMusicState("InGame");
    }
    
    public void renderframetext(final int i) {
        s_performance.renderFrameText.invokeAndMeasure(this, i, IngameState::renderFrameTextInternal);
    }
    
    private void renderFrameTextInternal(final int n) {
        IndieGL.disableAlphaTest();
        IndieGL.glDisable(2929);
        final ArrayList<UIElement> ui = UIManager.getUI();
        for (int i = 0; i < ui.size(); ++i) {
            final UIElement uiElement = ui.get(i);
            if (!(uiElement instanceof ActionProgressBar)) {
                if (uiElement.isVisible()) {
                    if (uiElement.isFollowGameWorld()) {
                        if (uiElement.getRenderThisPlayerOnly() == -1 || uiElement.getRenderThisPlayerOnly() == n) {
                            uiElement.render();
                        }
                    }
                }
            }
        }
        final ActionProgressBar progressBar = UIManager.getProgressBar(n);
        if (progressBar != null && progressBar.isVisible()) {
            progressBar.render();
        }
        WorldMarkers.instance.render();
        IsoMarkers.instance.render();
        TextDrawObject.RenderBatch(n);
        ChatElement.RenderBatch(n);
        try {
            Core.getInstance().EndFrameText(n);
        }
        catch (Exception ex) {}
    }
    
    public void renderframe(final int i) {
        s_performance.renderFrame.invokeAndMeasure(this, i, IngameState::renderFrameInternal);
    }
    
    private void renderFrameInternal(final int n) {
        if (IsoPlayer.getInstance() == null) {
            IsoPlayer.setInstance(IsoPlayer.players[0]);
            IsoCamera.CamCharacter = IsoPlayer.getInstance();
        }
        RenderSettings.getInstance().applyRenderSettings(n);
        final ActionProgressBar progressBar = UIManager.getProgressBar(n);
        if (progressBar != null) {
            if (progressBar.getValue() > 0.0f && progressBar.getValue() < 1.0f) {
                progressBar.setVisible(true);
                progressBar.delayHide = 2;
            }
            else if (progressBar.isVisible() && progressBar.delayHide > 0) {
                final ActionProgressBar actionProgressBar = progressBar;
                if (--actionProgressBar.delayHide == 0) {
                    progressBar.setVisible(false);
                }
            }
            if (progressBar.isVisible()) {
                final float xToScreen = IsoUtils.XToScreen(IsoPlayer.getInstance().getX(), IsoPlayer.getInstance().getY(), IsoPlayer.getInstance().getZ(), 0);
                final float yToScreen = IsoUtils.YToScreen(IsoPlayer.getInstance().getX(), IsoPlayer.getInstance().getY(), IsoPlayer.getInstance().getZ(), 0);
                final float n2 = xToScreen - IsoCamera.getOffX() - IsoPlayer.getInstance().offsetX;
                final float n3 = yToScreen - IsoCamera.getOffY() - IsoPlayer.getInstance().offsetY - 128 / (2 / Core.TileScale);
                final float n4 = n2 / Core.getInstance().getZoom(n);
                final float n5 = n3 / Core.getInstance().getZoom(n);
                final float n6 = n4 - progressBar.width / 2.0f;
                float n7 = n5 - progressBar.height;
                final IsoPlayer isoPlayer = IsoPlayer.players[n];
                if (isoPlayer != null && isoPlayer.getUserNameHeight() > 0) {
                    n7 -= isoPlayer.getUserNameHeight() + 2;
                }
                progressBar.setX(n6);
                progressBar.setY(n7);
            }
            if (!UIManager.VisibleAllUI) {
                progressBar.setVisible(false);
            }
        }
        IndieGL.disableAlphaTest();
        IndieGL.glDisable(2929);
        if ((IsoPlayer.getInstance() != null && !IsoPlayer.getInstance().isAsleep()) || UIManager.getFadeAlpha(n) < 1.0f) {
            ModelOutlines.instance.startFrameMain(n);
            IsoWorld.instance.render();
            ModelOutlines.instance.endFrameMain(n);
            RenderSettings.getInstance().legacyPostRender(n);
            LuaEventManager.triggerEvent("OnPostRender");
        }
        LineDrawer.clear();
        if (Core.bDebug && GameKeyboard.isKeyPressed(Core.getInstance().getKey("ToggleAnimationText"))) {
            DebugOptions.instance.Animation.Debug.setValue(!DebugOptions.instance.Animation.Debug.getValue());
        }
        try {
            Core.getInstance().EndFrame(n);
        }
        catch (Exception ex) {}
    }
    
    public void renderframeui() {
        s_performance.renderFrameUI.invokeAndMeasure(this, IngameState::renderFrameUI);
    }
    
    private void renderFrameUI() {
        if (Core.getInstance().StartFrameUI()) {
            TextManager.instance.DrawTextFromGameWorld();
            SkyBox.getInstance().draw();
            UIManager.render();
            ZomboidRadio.getInstance().render();
            if (Core.bDebug && IsoPlayer.getInstance() != null && IsoPlayer.getInstance().isGhostMode()) {
                IsoWorld.instance.CurrentCell.ChunkMap[0].drawDebugChunkMap();
            }
            DeadBodyAtlas.instance.renderUI();
            if (GameClient.bClient && GameClient.accessLevel.equals("admin")) {
                if (ServerPulseGraph.instance == null) {
                    ServerPulseGraph.instance = new ServerPulseGraph();
                }
                ServerPulseGraph.instance.update();
                ServerPulseGraph.instance.render();
            }
            if (Core.bDebug) {
                if (GameKeyboard.isKeyDown(Core.getInstance().getKey("Display FPS"))) {
                    if (!this.fpsKeyDown) {
                        this.fpsKeyDown = true;
                        if (FPSGraph.instance == null) {
                            FPSGraph.instance = new FPSGraph();
                        }
                        FPSGraph.instance.setVisible(!FPSGraph.instance.isVisible());
                    }
                }
                else {
                    this.fpsKeyDown = false;
                }
                if (FPSGraph.instance != null) {
                    FPSGraph.instance.render();
                }
            }
            if (!GameServer.bServer) {
                for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
                    final IsoPlayer isoPlayer = IsoPlayer.players[i];
                    if (isoPlayer != null && !isoPlayer.isDead()) {
                        if (isoPlayer.isAsleep()) {
                            final float n = GameClient.bFastForward ? GameTime.getInstance().ServerTimeOfDay : GameTime.getInstance().getTimeOfDay();
                            final Texture sharedTexture = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, (int)((n - (int)n) * 60.0f) / 10));
                            if (sharedTexture == null) {
                                break;
                            }
                            SpriteRenderer.instance.renderi(sharedTexture, IsoCamera.getScreenLeft(i) + IsoCamera.getScreenWidth(i) / 2 - sharedTexture.getWidth() / 2, IsoCamera.getScreenTop(i) + IsoCamera.getScreenHeight(i) / 2 - sharedTexture.getHeight() / 2, sharedTexture.getWidth(), sharedTexture.getHeight(), 1.0f, 1.0f, 1.0f, 1.0f, null);
                        }
                    }
                }
            }
            ActiveMods.renderUI();
            JoypadManager.instance.renderUI();
        }
        if (Core.bDebug && DebugOptions.instance.Animation.AnimRenderPicker.getValue() && IsoPlayer.players[0] != null) {
            IsoPlayer.players[0].advancedAnimator.render();
        }
        if (Core.bDebug) {
            ModelOutlines.instance.renderDebug();
        }
        Core.getInstance().EndFrameUI();
    }
    
    @Override
    public void render() {
        s_performance.render.invokeAndMeasure(this, IngameState::renderInternal);
    }
    
    private void renderInternal() {
        boolean b = true;
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            if (IsoPlayer.players[i] == null) {
                if (i == 0) {
                    SpriteRenderer.instance.prePopulating();
                }
            }
            else {
                IsoPlayer.setInstance(IsoPlayer.players[i]);
                IsoCamera.CamCharacter = IsoPlayer.players[i];
                Core.getInstance().StartFrame(i, b);
                IsoCamera.frameState.set(i);
                b = false;
                IsoSprite.globalOffsetX = -1.0f;
                this.renderframe(i);
            }
        }
        if (DebugOptions.instance.OffscreenBuffer.Render.getValue()) {
            Core.getInstance().RenderOffScreenBuffer();
        }
        for (int j = 0; j < IsoPlayer.numPlayers; ++j) {
            if (IsoPlayer.players[j] != null) {
                IsoPlayer.setInstance(IsoPlayer.players[j]);
                IsoCamera.CamCharacter = IsoPlayer.players[j];
                IsoCamera.frameState.set(j);
                Core.getInstance().StartFrameText(j);
                this.renderframetext(j);
            }
        }
        UIManager.resize();
        this.renderframeui();
    }
    
    @Override
    public GameStateMachine.StateAction update() {
        try {
            s_performance.update.start();
            return this.updateInternal();
        }
        finally {
            s_performance.update.end();
        }
    }
    
    private GameStateMachine.StateAction updateInternal() {
        ++this.tickCount;
        if (this.tickCount < 60) {
            for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
                if (IsoPlayer.players[i] != null) {
                    IsoPlayer.players[i].dirtyRecalcGridStackTime = 20.0f;
                }
            }
        }
        LuaEventManager.triggerEvent("OnTickEvenPaused", BoxedStaticValues.toDouble((double)this.numberTicks));
        DebugFileWatcher.instance.update();
        AdvancedAnimator.checkModifiedFiles();
        if (Core.bDebug) {
            this.debugTimes.clear();
            this.debugTimes.add(System.nanoTime());
        }
        if (Core.bExiting) {
            DebugLog.log("EXITDEBUG: IngameState.updateInternal 1");
            Core.bExiting = false;
            if (GameClient.bClient) {
                for (int j = 0; j < IsoPlayer.numPlayers; ++j) {
                    final IsoPlayer isoPlayer = IsoPlayer.players[j];
                    if (isoPlayer != null) {
                        ClientPlayerDB.getInstance().clientSendNetworkPlayerInt(isoPlayer);
                    }
                }
                try {
                    Thread.sleep(500L);
                }
                catch (InterruptedException ex5) {}
                WorldStreamer.instance.stop();
                GameClient.instance.doDisconnect("Quitting");
            }
            DebugLog.log("EXITDEBUG: IngameState.updateInternal 2");
            if (PlayerDB.isAllow()) {
                PlayerDB.getInstance().saveLocalPlayersForce();
                PlayerDB.getInstance().m_canSavePlayers = false;
            }
            if (ClientPlayerDB.isAllow()) {
                ClientPlayerDB.getInstance().canSavePlayers = false;
            }
            try {
                GameWindow.save(true);
            }
            catch (Throwable t) {
                ExceptionLogger.logException(t);
            }
            DebugLog.log("EXITDEBUG: IngameState.updateInternal 3");
            try {
                LuaEventManager.triggerEvent("OnPostSave");
            }
            catch (Exception ex) {
                ExceptionLogger.logException(ex);
            }
            if (ClientPlayerDB.isAllow()) {
                ClientPlayerDB.getInstance().close();
            }
            return GameStateMachine.StateAction.Continue;
        }
        if (!GameWindow.bServerDisconnected) {
            if (Core.bDebug) {
                if (this.showGlobalObjectDebugger || (GameKeyboard.isKeyPressed(60) && GameKeyboard.isKeyDown(29))) {
                    this.showGlobalObjectDebugger = false;
                    DebugLog.General.debugln("Activating DebugGlobalObjectState.");
                    this.RedirectState = new DebugGlobalObjectState();
                    return GameStateMachine.StateAction.Yield;
                }
                if (this.showChunkDebugger || GameKeyboard.isKeyPressed(60)) {
                    this.showChunkDebugger = false;
                    DebugLog.General.debugln("Activating DebugChunkState.");
                    this.RedirectState = DebugChunkState.checkInstance();
                    return GameStateMachine.StateAction.Yield;
                }
                if (this.showAnimationViewer || (GameKeyboard.isKeyPressed(65) && GameKeyboard.isKeyDown(29))) {
                    this.showAnimationViewer = false;
                    DebugLog.General.debugln("Activating AnimationViewerState.");
                    this.RedirectState = AnimationViewerState.checkInstance();
                    return GameStateMachine.StateAction.Yield;
                }
                if (this.showAttachmentEditor || (GameKeyboard.isKeyPressed(65) && GameKeyboard.isKeyDown(42))) {
                    this.showAttachmentEditor = false;
                    DebugLog.General.debugln("Activating AttachmentEditorState.");
                    this.RedirectState = AttachmentEditorState.checkInstance();
                    return GameStateMachine.StateAction.Yield;
                }
                if (this.showVehicleEditor != null || GameKeyboard.isKeyPressed(65)) {
                    DebugLog.General.debugln("Activating EditVehicleState.");
                    final EditVehicleState checkInstance = EditVehicleState.checkInstance();
                    if (!StringUtils.isNullOrWhitespace(this.showVehicleEditor)) {
                        checkInstance.setScript(this.showVehicleEditor);
                    }
                    this.showVehicleEditor = null;
                    this.RedirectState = checkInstance;
                    return GameStateMachine.StateAction.Yield;
                }
                if (this.showWorldMapEditor != null || GameKeyboard.isKeyPressed(66)) {
                    final WorldMapEditorState checkInstance2 = WorldMapEditorState.checkInstance();
                    this.showWorldMapEditor = null;
                    this.RedirectState = checkInstance2;
                    return GameStateMachine.StateAction.Yield;
                }
            }
            if (Core.bDebug) {
                this.debugTimes.add(System.nanoTime());
            }
            ++this.timesincelastinsanity;
            if (!GameServer.bServer && GameKeyboard.isKeyDown(Core.getInstance().getKey("Toggle Music")) && !this.MDebounce) {
                this.MDebounce = true;
                if (!(SoundManager.instance.AllowMusic = !SoundManager.instance.AllowMusic)) {
                    SoundManager.instance.StopMusic();
                    TutorialManager.instance.PrefMusic = null;
                }
            }
            else if (!GameServer.bServer && !GameKeyboard.isKeyDown(Core.getInstance().getKey("Toggle Music"))) {
                this.MDebounce = false;
            }
            if (Core.bDebug) {
                this.debugTimes.add(System.nanoTime());
            }
            try {
                if (!GameServer.bServer && IsoPlayer.getInstance() != null && IsoPlayer.allPlayersDead()) {
                    if (IsoPlayer.getInstance() != null) {
                        UIManager.getSpeedControls().SetCurrentGameSpeed(1);
                    }
                    IsoCamera.update();
                }
                this.alt = !this.alt;
                if (!GameServer.bServer) {
                    IngameState.WaitMul = 1;
                    if (UIManager.getSpeedControls() != null) {
                        if (UIManager.getSpeedControls().getCurrentGameSpeed() == 2) {
                            IngameState.WaitMul = 15;
                        }
                        if (UIManager.getSpeedControls().getCurrentGameSpeed() == 3) {
                            IngameState.WaitMul = 30;
                        }
                    }
                }
                if (Core.bDebug) {
                    this.debugTimes.add(System.nanoTime());
                }
                if (GameServer.bServer) {
                    if (GameServer.Players.isEmpty() && ServerOptions.instance.PauseEmpty.getValue()) {
                        this.Paused = true;
                    }
                    else {
                        this.Paused = false;
                    }
                }
                Label_1322: {
                    if (this.Paused) {
                        if (!GameClient.bClient) {
                            break Label_1322;
                        }
                    }
                    try {
                        if (IsoCamera.CamCharacter != null && IsoWorld.instance.bDoChunkMapUpdate) {
                            for (int k = 0; k < IsoPlayer.numPlayers; ++k) {
                                if (IsoPlayer.players[k] != null) {
                                    if (!IsoWorld.instance.CurrentCell.ChunkMap[k].ignore) {
                                        if (!GameServer.bServer) {
                                            IsoCamera.CamCharacter = IsoPlayer.players[k];
                                            IsoPlayer.setInstance(IsoPlayer.players[k]);
                                        }
                                        if (!GameServer.bServer) {
                                            IsoWorld.instance.CurrentCell.ChunkMap[k].ProcessChunkPos(IsoCamera.CamCharacter);
                                        }
                                    }
                                }
                            }
                        }
                        if (Core.bDebug) {
                            this.debugTimes.add(System.nanoTime());
                        }
                        IsoWorld.instance.update();
                        if (Core.bDebug) {
                            this.debugTimes.add(System.nanoTime());
                        }
                        ZomboidRadio.getInstance().update();
                        this.UpdateStuff();
                        LuaEventManager.triggerEvent("OnTick", this.numberTicks);
                        this.numberTicks = Math.max(this.numberTicks + 1L, 0L);
                    }
                    catch (Exception ex2) {
                        ExceptionLogger.logException(ex2);
                        if (!GameServer.bServer) {
                            if (GameClient.bClient) {
                                for (int l = 0; l < IsoPlayer.numPlayers; ++l) {
                                    final IsoPlayer isoPlayer2 = IsoPlayer.players[l];
                                    if (isoPlayer2 != null) {
                                        ClientPlayerDB.getInstance().clientSendNetworkPlayerInt(isoPlayer2);
                                    }
                                }
                                WorldStreamer.instance.stop();
                            }
                            final String gameSaveWorld = Core.GameSaveWorld;
                            createWorld(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Core.GameSaveWorld));
                            copyWorld(gameSaveWorld, Core.GameSaveWorld);
                            if (GameClient.bClient) {
                                if (PlayerDB.isAllow()) {
                                    PlayerDB.getInstance().saveLocalPlayersForce();
                                    PlayerDB.getInstance().m_canSavePlayers = false;
                                }
                                if (ClientPlayerDB.isAllow()) {
                                    ClientPlayerDB.getInstance().canSavePlayers = false;
                                }
                            }
                            try {
                                GameWindow.save(true);
                            }
                            catch (Throwable t2) {
                                ExceptionLogger.logException(t2);
                            }
                            if (GameClient.bClient) {
                                try {
                                    LuaEventManager.triggerEvent("OnPostSave");
                                }
                                catch (Exception ex3) {
                                    ExceptionLogger.logException(ex3);
                                }
                                if (ClientPlayerDB.isAllow()) {
                                    ClientPlayerDB.getInstance().close();
                                }
                            }
                        }
                        if (GameClient.bClient) {
                            GameClient.instance.doDisconnect("Quitting");
                        }
                        return GameStateMachine.StateAction.Continue;
                    }
                }
            }
            catch (Exception ex4) {
                System.err.println("IngameState.update caught an exception.");
                ExceptionLogger.logException(ex4);
            }
            if (Core.bDebug) {
                this.debugTimes.add(System.nanoTime());
            }
            if (!GameServer.bServer || ServerGUI.isCreated()) {
                ModelManager.instance.update();
            }
            if (Core.bDebug && FPSGraph.instance != null) {
                FPSGraph.instance.addUpdate(System.currentTimeMillis());
                FPSGraph.instance.update();
            }
            if (GameClient.bClient || GameServer.bServer) {
                MPStatistics.Update();
            }
            return GameStateMachine.StateAction.Remain;
        }
        TutorialManager.instance.StealControl = true;
        if (!this.bDidServerDisconnectState) {
            this.bDidServerDisconnectState = true;
            this.RedirectState = new ServerDisconnectState();
            return GameStateMachine.StateAction.Yield;
        }
        GameClient.connection = null;
        GameClient.instance.bConnected = false;
        GameClient.bClient = false;
        GameWindow.bServerDisconnected = false;
        return GameStateMachine.StateAction.Continue;
    }
    
    static {
        IngameState.WaitMul = 20;
        IngameState.GameID = 0L;
        IngameState.last = -1;
        IngameState.ContainerTypes = new HashMap<String, Integer>();
        IngameState.nSaveCycle = 1800;
        IngameState.bDoChars = false;
        IngameState.keySpacePreviousFrame = false;
    }
    
    private static class s_performance
    {
        static final PerformanceProfileProbe render;
        static final PerformanceProfileProbe renderFrame;
        static final PerformanceProfileProbe renderFrameText;
        static final PerformanceProfileProbe renderFrameUI;
        static final PerformanceProfileProbe update;
        
        static {
            render = new PerformanceProfileProbe("IngameState.render");
            renderFrame = new PerformanceProfileProbe("IngameState.renderFrame");
            renderFrameText = new PerformanceProfileProbe("IngameState.renderFrameText");
            renderFrameUI = new PerformanceProfileProbe("IngameState.renderFrameUI");
            update = new PerformanceProfileProbe("IngameState.update");
        }
    }
}

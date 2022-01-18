// 
// Decompiled by Procyon v0.5.36
// 

package zombie.debug;

import zombie.gameStates.GameLoadingState;
import zombie.GameWindow;
import zombie.core.opengl.RenderThread;
import zombie.config.ConfigOption;
import zombie.config.ConfigFile;
import zombie.debug.options.OptionGroup;
import zombie.util.Type;
import java.util.List;
import zombie.util.list.PZArrayUtil;
import zombie.util.PZXmlParserException;
import zombie.core.logger.ExceptionLogger;
import java.util.Iterator;
import zombie.util.PZXmlUtil;
import java.io.File;
import zombie.core.Core;
import zombie.DebugFileWatcher;
import zombie.ZomboidFileSystem;
import zombie.PredicatedFileWatcher;
import zombie.debug.options.Character;
import zombie.debug.options.Animation;
import zombie.debug.options.Weather;
import zombie.debug.options.Terrain;
import zombie.debug.options.OffscreenBuffer;
import zombie.debug.options.Network;
import zombie.debug.options.IsoSprite;
import zombie.debug.options.IDebugOption;
import java.util.ArrayList;
import zombie.debug.options.IDebugOptionGroup;

public final class DebugOptions implements IDebugOptionGroup
{
    public static final int VERSION = 1;
    public static final DebugOptions instance;
    private final ArrayList<BooleanDebugOption> options;
    private final ArrayList<IDebugOption> m_options;
    public final BooleanDebugOption AssetSlowLoad;
    public final BooleanDebugOption MultiplayerShowZombieMultiplier;
    public final BooleanDebugOption MultiplayerShowZombieOwner;
    public final BooleanDebugOption MultiplayerShowPosition;
    public final BooleanDebugOption MultiplayerShowTeleport;
    public final BooleanDebugOption MultiplayerShowHit;
    public final BooleanDebugOption MultiplayerLogPrediction;
    public final BooleanDebugOption MultiplayerShowPlayerPrediction;
    public final BooleanDebugOption MultiplayerShowPlayerStatus;
    public final BooleanDebugOption MultiplayerShowZombiePrediction;
    public final BooleanDebugOption MultiplayerShowZombieDesync;
    public final BooleanDebugOption MultiplayerShowZombieStatus;
    public final BooleanDebugOption MultiplayerLightAmbient;
    public final BooleanDebugOption MultiplayerCriticalHit;
    public final BooleanDebugOption MultiplayerTorsoHit;
    public final BooleanDebugOption MultiplayerZombieCrawler;
    public final BooleanDebugOption MultiplayerSpawnZombie;
    public final BooleanDebugOption MultiplayerPlayerZombie;
    public final BooleanDebugOption MultiplayerAttackPlayer;
    public final BooleanDebugOption MultiplayerFollowPlayer;
    public final BooleanDebugOption MultiplayerAutoEquip;
    public final BooleanDebugOption MultiplayerPing;
    public final BooleanDebugOption CheatClockVisible;
    public final BooleanDebugOption CheatDoorUnlock;
    public final BooleanDebugOption CheatPlayerStartInvisible;
    public final BooleanDebugOption CheatPlayerInvisibleSprint;
    public final BooleanDebugOption CheatPlayerSeeEveryone;
    public final BooleanDebugOption CheatUnlimitedAmmo;
    public final BooleanDebugOption CheatRecipeKnowAll;
    public final BooleanDebugOption CheatTimedActionInstant;
    public final BooleanDebugOption CheatVehicleMechanicsAnywhere;
    public final BooleanDebugOption CheatVehicleStartWithoutKey;
    public final BooleanDebugOption CheatWindowUnlock;
    public final BooleanDebugOption CollideWithObstaclesRenderRadius;
    public final BooleanDebugOption CollideWithObstaclesRenderObstacles;
    public final BooleanDebugOption CollideWithObstaclesRenderNormals;
    public final BooleanDebugOption DeadBodyAtlasRender;
    public final BooleanDebugOption DebugScenarioForceLaunch;
    public final BooleanDebugOption MechanicsRenderHitbox;
    public final BooleanDebugOption JoypadRenderUI;
    public final BooleanDebugOption ModelRenderAttachments;
    public final BooleanDebugOption ModelRenderAxis;
    public final BooleanDebugOption ModelRenderBones;
    public final BooleanDebugOption ModelRenderBounds;
    public final BooleanDebugOption ModelRenderLights;
    public final BooleanDebugOption ModelRenderMuzzleflash;
    public final BooleanDebugOption ModelRenderSkipVehicles;
    public final BooleanDebugOption ModelRenderWeaponHitPoint;
    public final BooleanDebugOption ModelRenderWireframe;
    public final BooleanDebugOption ModelSkeleton;
    public final BooleanDebugOption ModRenderLoaded;
    public final BooleanDebugOption PathfindPathToMouseAllowCrawl;
    public final BooleanDebugOption PathfindPathToMouseAllowThump;
    public final BooleanDebugOption PathfindPathToMouseEnable;
    public final BooleanDebugOption PathfindPathToMouseIgnoreCrawlCost;
    public final BooleanDebugOption PathfindRenderPath;
    public final BooleanDebugOption PathfindRenderWaiting;
    public final BooleanDebugOption PhysicsRender;
    public final BooleanDebugOption PolymapRenderClusters;
    public final BooleanDebugOption PolymapRenderConnections;
    public final BooleanDebugOption PolymapRenderCrawling;
    public final BooleanDebugOption PolymapRenderLineClearCollide;
    public final BooleanDebugOption PolymapRenderNodes;
    public final BooleanDebugOption TooltipInfo;
    public final BooleanDebugOption TooltipModName;
    public final BooleanDebugOption TranslationPrefix;
    public final BooleanDebugOption UIRenderOutline;
    public final BooleanDebugOption UIDebugConsoleStartVisible;
    public final BooleanDebugOption UIDebugConsoleDebugLog;
    public final BooleanDebugOption UIDebugConsoleEchoCommand;
    public final BooleanDebugOption VehicleCycleColor;
    public final BooleanDebugOption VehicleRenderBlood0;
    public final BooleanDebugOption VehicleRenderBlood50;
    public final BooleanDebugOption VehicleRenderBlood100;
    public final BooleanDebugOption VehicleRenderDamage0;
    public final BooleanDebugOption VehicleRenderDamage1;
    public final BooleanDebugOption VehicleRenderDamage2;
    public final BooleanDebugOption VehicleRenderRust0;
    public final BooleanDebugOption VehicleRenderRust50;
    public final BooleanDebugOption VehicleRenderRust100;
    public final BooleanDebugOption VehicleRenderOutline;
    public final BooleanDebugOption VehicleRenderArea;
    public final BooleanDebugOption VehicleRenderAuthorizations;
    public final BooleanDebugOption VehicleRenderAttackPositions;
    public final BooleanDebugOption VehicleRenderExit;
    public final BooleanDebugOption VehicleRenderIntersectedSquares;
    public final BooleanDebugOption VehicleRenderTrailerPositions;
    public final BooleanDebugOption VehicleSpawnEverywhere;
    public final BooleanDebugOption WorldSoundRender;
    public final BooleanDebugOption LightingRender;
    public final BooleanDebugOption SkyboxShow;
    public final BooleanDebugOption WorldStreamerSlowLoad;
    public final BooleanDebugOption DebugDraw_SkipVBODraw;
    public final BooleanDebugOption DebugDraw_SkipDrawNonSkinnedModel;
    public final BooleanDebugOption DebugDraw_SkipWorldShading;
    public final BooleanDebugOption GameProfilerEnabled;
    public final BooleanDebugOption GameTimeSpeedHalf;
    public final BooleanDebugOption GameTimeSpeedQuarter;
    public final BooleanDebugOption ThreadCrash_Enabled;
    public final BooleanDebugOption[] ThreadCrash_GameThread;
    public final BooleanDebugOption[] ThreadCrash_GameLoadingThread;
    public final BooleanDebugOption[] ThreadCrash_RenderThread;
    public final BooleanDebugOption WorldChunkMap5x5;
    public final BooleanDebugOption ZombieRenderCanCrawlUnderVehicle;
    public final BooleanDebugOption ZombieRenderFakeDead;
    public final BooleanDebugOption ZombieRenderMemory;
    public final BooleanDebugOption ZombieOutfitRandom;
    public final Checks Checks;
    public final IsoSprite IsoSprite;
    public final Network Network;
    public final OffscreenBuffer OffscreenBuffer;
    public final Terrain Terrain;
    public final Weather Weather;
    public final Animation Animation;
    public final Character Character;
    private static PredicatedFileWatcher m_triggerWatcher;
    
    public DebugOptions() {
        this.options = new ArrayList<BooleanDebugOption>();
        this.m_options = new ArrayList<IDebugOption>();
        this.AssetSlowLoad = this.newOption("Asset.SlowLoad", false);
        this.MultiplayerShowZombieMultiplier = this.newDebugOnlyOption("Multiplayer.Debug.ZombieMultiplier", false);
        this.MultiplayerShowZombieOwner = this.newDebugOnlyOption("Multiplayer.Debug.ZombieOwner", false);
        this.MultiplayerShowPosition = this.newDebugOnlyOption("Multiplayer.Debug.Position", false);
        this.MultiplayerShowTeleport = this.newDebugOnlyOption("Multiplayer.Debug.Teleport", false);
        this.MultiplayerShowHit = this.newDebugOnlyOption("Multiplayer.Debug.Hit", false);
        this.MultiplayerLogPrediction = this.newDebugOnlyOption("Multiplayer.Debug.LogPrediction", false);
        this.MultiplayerShowPlayerPrediction = this.newDebugOnlyOption("Multiplayer.Debug.PlayerPrediction", false);
        this.MultiplayerShowPlayerStatus = this.newDebugOnlyOption("Multiplayer.Debug.PlayerStatus", false);
        this.MultiplayerShowZombiePrediction = this.newDebugOnlyOption("Multiplayer.Debug.ZombiePrediction", false);
        this.MultiplayerShowZombieDesync = this.newDebugOnlyOption("Multiplayer.Debug.ZombieDesync", false);
        this.MultiplayerShowZombieStatus = this.newDebugOnlyOption("Multiplayer.Debug.ZombieStatus", false);
        this.MultiplayerLightAmbient = this.newDebugOnlyOption("Multiplayer.Debug.LightAmbient", false);
        this.MultiplayerCriticalHit = this.newDebugOnlyOption("Multiplayer.Debug.CriticalHit", false);
        this.MultiplayerTorsoHit = this.newOption("Multiplayer.Debug.TorsoHit", false);
        this.MultiplayerZombieCrawler = this.newDebugOnlyOption("Multiplayer.Debug.ZombieCrawler", false);
        this.MultiplayerSpawnZombie = this.newDebugOnlyOption("Multiplayer.Debug.SpawnZombie", false);
        this.MultiplayerPlayerZombie = this.newDebugOnlyOption("Multiplayer.Debug.PlayerZombie", false);
        this.MultiplayerAttackPlayer = this.newDebugOnlyOption("Multiplayer.Debug.Attack.Player", false);
        this.MultiplayerFollowPlayer = this.newDebugOnlyOption("Multiplayer.Debug.Follow.Player", false);
        this.MultiplayerAutoEquip = this.newDebugOnlyOption("Multiplayer.Debug.AutoEquip", false);
        this.MultiplayerPing = this.newOption("Multiplayer.Debug.Ping", false);
        this.CheatClockVisible = this.newDebugOnlyOption("Cheat.Clock.Visible", false);
        this.CheatDoorUnlock = this.newDebugOnlyOption("Cheat.Door.Unlock", false);
        this.CheatPlayerStartInvisible = this.newDebugOnlyOption("Cheat.Player.StartInvisible", false);
        this.CheatPlayerInvisibleSprint = this.newDebugOnlyOption("Cheat.Player.InvisibleSprint", false);
        this.CheatPlayerSeeEveryone = this.newDebugOnlyOption("Cheat.Player.SeeEveryone", false);
        this.CheatUnlimitedAmmo = this.newDebugOnlyOption("Cheat.Player.UnlimitedAmmo", false);
        this.CheatRecipeKnowAll = this.newDebugOnlyOption("Cheat.Recipe.KnowAll", false);
        this.CheatTimedActionInstant = this.newDebugOnlyOption("Cheat.TimedAction.Instant", false);
        this.CheatVehicleMechanicsAnywhere = this.newDebugOnlyOption("Cheat.Vehicle.MechanicsAnywhere", false);
        this.CheatVehicleStartWithoutKey = this.newDebugOnlyOption("Cheat.Vehicle.StartWithoutKey", false);
        this.CheatWindowUnlock = this.newDebugOnlyOption("Cheat.Window.Unlock", false);
        this.CollideWithObstaclesRenderRadius = this.newOption("CollideWithObstacles.Render.Radius", false);
        this.CollideWithObstaclesRenderObstacles = this.newOption("CollideWithObstacles.Render.Obstacles", false);
        this.CollideWithObstaclesRenderNormals = this.newOption("CollideWithObstacles.Render.Normals", false);
        this.DeadBodyAtlasRender = this.newOption("DeadBodyAtlas.Render", false);
        this.DebugScenarioForceLaunch = this.newOption("DebugScenario.ForceLaunch", false);
        this.MechanicsRenderHitbox = this.newOption("Mechanics.Render.Hitbox", false);
        this.JoypadRenderUI = this.newDebugOnlyOption("Joypad.Render.UI", false);
        this.ModelRenderAttachments = this.newOption("Model.Render.Attachments", false);
        this.ModelRenderAxis = this.newOption("Model.Render.Axis", false);
        this.ModelRenderBones = this.newOption("Model.Render.Bones", false);
        this.ModelRenderBounds = this.newOption("Model.Render.Bounds", false);
        this.ModelRenderLights = this.newOption("Model.Render.Lights", false);
        this.ModelRenderMuzzleflash = this.newOption("Model.Render.Muzzleflash", false);
        this.ModelRenderSkipVehicles = this.newOption("Model.Render.SkipVehicles", false);
        this.ModelRenderWeaponHitPoint = this.newOption("Model.Render.WeaponHitPoint", false);
        this.ModelRenderWireframe = this.newOption("Model.Render.Wireframe", false);
        this.ModelSkeleton = this.newOption("Model.Force.Skeleton", false);
        this.ModRenderLoaded = this.newDebugOnlyOption("Mod.Render.Loaded", false);
        this.PathfindPathToMouseAllowCrawl = this.newOption("Pathfind.PathToMouse.AllowCrawl", false);
        this.PathfindPathToMouseAllowThump = this.newOption("Pathfind.PathToMouse.AllowThump", false);
        this.PathfindPathToMouseEnable = this.newOption("Pathfind.PathToMouse.Enable", false);
        this.PathfindPathToMouseIgnoreCrawlCost = this.newOption("Pathfind.PathToMouse.IgnoreCrawlCost", false);
        this.PathfindRenderPath = this.newOption("Pathfind.Render.Path", false);
        this.PathfindRenderWaiting = this.newOption("Pathfind.Render.Waiting", false);
        this.PhysicsRender = this.newOption("Physics.Render", false);
        this.PolymapRenderClusters = this.newOption("Pathfind.Render.Clusters", false);
        this.PolymapRenderConnections = this.newOption("Pathfind.Render.Connections", false);
        this.PolymapRenderCrawling = this.newOption("Pathfind.Render.Crawling", false);
        this.PolymapRenderLineClearCollide = this.newOption("Pathfind.Render.LineClearCollide", false);
        this.PolymapRenderNodes = this.newOption("Pathfind.Render.Nodes", false);
        this.TooltipInfo = this.newOption("Tooltip.Info", false);
        this.TooltipModName = this.newDebugOnlyOption("Tooltip.ModName", false);
        this.TranslationPrefix = this.newOption("Translation.Prefix", false);
        this.UIRenderOutline = this.newOption("UI.Render.Outline", false);
        this.UIDebugConsoleStartVisible = this.newOption("UI.DebugConsole.StartVisible", true);
        this.UIDebugConsoleDebugLog = this.newOption("UI.DebugConsole.DebugLog", true);
        this.UIDebugConsoleEchoCommand = this.newOption("UI.DebugConsole.EchoCommand", true);
        this.VehicleCycleColor = this.newDebugOnlyOption("Vehicle.CycleColor", false);
        this.VehicleRenderBlood0 = this.newDebugOnlyOption("Vehicle.Render.Blood0", false);
        this.VehicleRenderBlood50 = this.newDebugOnlyOption("Vehicle.Render.Blood50", false);
        this.VehicleRenderBlood100 = this.newDebugOnlyOption("Vehicle.Render.Blood100", false);
        this.VehicleRenderDamage0 = this.newDebugOnlyOption("Vehicle.Render.Damage0", false);
        this.VehicleRenderDamage1 = this.newDebugOnlyOption("Vehicle.Render.Damage1", false);
        this.VehicleRenderDamage2 = this.newDebugOnlyOption("Vehicle.Render.Damage2", false);
        this.VehicleRenderRust0 = this.newDebugOnlyOption("Vehicle.Render.Rust0", false);
        this.VehicleRenderRust50 = this.newDebugOnlyOption("Vehicle.Render.Rust50", false);
        this.VehicleRenderRust100 = this.newDebugOnlyOption("Vehicle.Render.Rust100", false);
        this.VehicleRenderOutline = this.newOption("Vehicle.Render.Outline", false);
        this.VehicleRenderArea = this.newOption("Vehicle.Render.Area", false);
        this.VehicleRenderAuthorizations = this.newOption("Vehicle.Render.Authorizations", false);
        this.VehicleRenderAttackPositions = this.newOption("Vehicle.Render.AttackPositions", false);
        this.VehicleRenderExit = this.newOption("Vehicle.Render.Exit", false);
        this.VehicleRenderIntersectedSquares = this.newOption("Vehicle.Render.IntersectedSquares", false);
        this.VehicleRenderTrailerPositions = this.newDebugOnlyOption("Vehicle.Render.TrailerPositions", false);
        this.VehicleSpawnEverywhere = this.newDebugOnlyOption("Vehicle.Spawn.Everywhere", false);
        this.WorldSoundRender = this.newOption("WorldSound.Render", false);
        this.LightingRender = this.newOption("Lighting.Render", false);
        this.SkyboxShow = this.newOption("Skybox.Show", false);
        this.WorldStreamerSlowLoad = this.newOption("WorldStreamer.SlowLoad", false);
        this.DebugDraw_SkipVBODraw = this.newOption("DebugDraw.SkipVBODraw", false);
        this.DebugDraw_SkipDrawNonSkinnedModel = this.newOption("DebugDraw.SkipDrawNonSkinnedModel", false);
        this.DebugDraw_SkipWorldShading = this.newOption("DebugDraw.SkipWorldShading", false);
        this.GameProfilerEnabled = this.newOption("GameProfiler.Enabled", false);
        this.GameTimeSpeedHalf = this.newOption("GameTime.Speed.Half", false);
        this.GameTimeSpeedQuarter = this.newOption("GameTime.Speed.Quarter", false);
        this.ThreadCrash_Enabled = this.newDebugOnlyOption("ThreadCrash.Enable", false);
        this.ThreadCrash_GameThread = new BooleanDebugOption[] { this.newDebugOnlyOption("ThreadCrash.MainThread.0", false), this.newDebugOnlyOption("ThreadCrash.MainThread.1", false), this.newDebugOnlyOption("ThreadCrash.MainThread.2", false) };
        this.ThreadCrash_GameLoadingThread = new BooleanDebugOption[] { this.newDebugOnlyOption("ThreadCrash.GameLoadingThread.0", false) };
        this.ThreadCrash_RenderThread = new BooleanDebugOption[] { this.newDebugOnlyOption("ThreadCrash.RenderThread.0", false), this.newDebugOnlyOption("ThreadCrash.RenderThread.1", false), this.newDebugOnlyOption("ThreadCrash.RenderThread.2", false) };
        this.WorldChunkMap5x5 = this.newDebugOnlyOption("World.ChunkMap.5x5", false);
        this.ZombieRenderCanCrawlUnderVehicle = this.newDebugOnlyOption("Zombie.Render.CanCrawlUnderVehicle", false);
        this.ZombieRenderFakeDead = this.newDebugOnlyOption("Zombie.Render.FakeDead", false);
        this.ZombieRenderMemory = this.newDebugOnlyOption("Zombie.Render.Memory", false);
        this.ZombieOutfitRandom = this.newDebugOnlyOption("Zombie.Outfit.Random", false);
        this.Checks = this.newOptionGroup(new Checks());
        this.IsoSprite = this.newOptionGroup(new IsoSprite());
        this.Network = this.newOptionGroup(new Network());
        this.OffscreenBuffer = this.newOptionGroup(new OffscreenBuffer());
        this.Terrain = this.newOptionGroup(new Terrain());
        this.Weather = this.newOptionGroup(new Weather());
        this.Animation = this.newOptionGroup(new Animation());
        this.Character = this.newOptionGroup(new Character());
    }
    
    public void init() {
        this.load();
        this.initMessaging();
    }
    
    private void initMessaging() {
        if (DebugOptions.m_triggerWatcher == null) {
            DebugOptions.m_triggerWatcher = new PredicatedFileWatcher(ZomboidFileSystem.instance.getMessagingDirSub("Trigger_SetDebugOptions.xml"), this::onTrigger_SetDebugOptions);
            DebugFileWatcher.instance.add(DebugOptions.m_triggerWatcher);
        }
        final DebugOptionsXml debugOptionsXml = new DebugOptionsXml();
        debugOptionsXml.setDebugMode = true;
        debugOptionsXml.debugMode = Core.bDebug;
        for (final BooleanDebugOption booleanDebugOption : this.options) {
            debugOptionsXml.options.add(new DebugOptionsXml.OptionNode(booleanDebugOption.getName(), booleanDebugOption.getValue()));
        }
        PZXmlUtil.tryWrite(debugOptionsXml, new File(ZomboidFileSystem.instance.getMessagingDirSub("DebugOptions_list.xml")));
    }
    
    private void onTrigger_SetDebugOptions(final String s) {
        try {
            final DebugOptionsXml debugOptionsXml = PZXmlUtil.parse(DebugOptionsXml.class, ZomboidFileSystem.instance.getMessagingDirSub("Trigger_SetDebugOptions.xml"));
            for (final DebugOptionsXml.OptionNode optionNode : debugOptionsXml.options) {
                this.setBoolean(optionNode.name, optionNode.value);
            }
            if (debugOptionsXml.setDebugMode) {
                DebugLog.General.println("DebugMode: %s", debugOptionsXml.debugMode ? "ON" : "OFF");
                Core.bDebug = debugOptionsXml.debugMode;
            }
        }
        catch (PZXmlParserException ex) {
            ExceptionLogger.logException(ex, "Exception thrown parsing Trigger_SetDebugOptions.xml");
        }
    }
    
    @Override
    public Iterable<IDebugOption> getChildren() {
        return (Iterable<IDebugOption>)PZArrayUtil.listConvert((List<BooleanDebugOption>)this.options, booleanDebugOption -> booleanDebugOption);
    }
    
    @Override
    public void addChild(final IDebugOption e) {
        this.m_options.add(e);
        e.setParent(this);
        this.onChildAdded(e);
    }
    
    @Override
    public void onChildAdded(final IDebugOption debugOption) {
        this.onDescendantAdded(debugOption);
    }
    
    @Override
    public void onDescendantAdded(final IDebugOption debugOption) {
        this.addOption(debugOption);
    }
    
    private void addOption(final IDebugOption debugOption) {
        final BooleanDebugOption e = Type.tryCastTo(debugOption, BooleanDebugOption.class);
        if (e != null) {
            this.options.add(e);
        }
        final IDebugOptionGroup debugOptionGroup = Type.tryCastTo(debugOption, IDebugOptionGroup.class);
        if (debugOptionGroup != null) {
            this.addDescendantOptions(debugOptionGroup);
        }
    }
    
    private void addDescendantOptions(final IDebugOptionGroup debugOptionGroup) {
        final Iterator<IDebugOption> iterator = debugOptionGroup.getChildren().iterator();
        while (iterator.hasNext()) {
            this.addOption(iterator.next());
        }
    }
    
    @Override
    public String getName() {
        return "DebugOptions";
    }
    
    @Override
    public IDebugOptionGroup getParent() {
        return null;
    }
    
    @Override
    public void setParent(final IDebugOptionGroup debugOptionGroup) {
        throw new UnsupportedOperationException("DebugOptions is a root not. Cannot have a parent.");
    }
    
    private BooleanDebugOption newOption(final String s, final boolean b) {
        final BooleanDebugOption option = OptionGroup.newOption(s, b);
        this.addChild(option);
        return option;
    }
    
    private BooleanDebugOption newDebugOnlyOption(final String s, final boolean b) {
        final BooleanDebugOption debugOnlyOption = OptionGroup.newDebugOnlyOption(s, b);
        this.addChild(debugOnlyOption);
        return debugOnlyOption;
    }
    
    private <E extends IDebugOptionGroup> E newOptionGroup(final E e) {
        this.addChild(e);
        return e;
    }
    
    public BooleanDebugOption getOptionByName(final String anObject) {
        for (int i = 0; i < this.options.size(); ++i) {
            final BooleanDebugOption booleanDebugOption = this.options.get(i);
            if (booleanDebugOption.getName().equals(anObject)) {
                return booleanDebugOption;
            }
        }
        return null;
    }
    
    public int getOptionCount() {
        return this.options.size();
    }
    
    public BooleanDebugOption getOptionByIndex(final int index) {
        return this.options.get(index);
    }
    
    public void setBoolean(final String s, final boolean value) {
        final BooleanDebugOption optionByName = this.getOptionByName(s);
        if (optionByName != null) {
            optionByName.setValue(value);
        }
    }
    
    public boolean getBoolean(final String s) {
        final BooleanDebugOption optionByName = this.getOptionByName(s);
        return optionByName != null && optionByName.getValue();
    }
    
    public void save() {
        new ConfigFile().write(ZomboidFileSystem.instance.getCacheDirSub("debug-options.ini"), 1, this.options);
    }
    
    public void load() {
        final String cacheDirSub = ZomboidFileSystem.instance.getCacheDirSub("debug-options.ini");
        final ConfigFile configFile = new ConfigFile();
        if (configFile.read(cacheDirSub)) {
            for (int i = 0; i < configFile.getOptions().size(); ++i) {
                final ConfigOption configOption = configFile.getOptions().get(i);
                final BooleanDebugOption optionByName = this.getOptionByName(configOption.getName());
                if (optionByName != null) {
                    optionByName.parse(configOption.getValueAsString());
                }
            }
        }
    }
    
    public static void testThreadCrash(final int n) {
        DebugOptions.instance.testThreadCrashInternal(n);
    }
    
    private void testThreadCrashInternal(final int n) {
        if (!Core.bDebug) {
            return;
        }
        if (!this.ThreadCrash_Enabled.getValue()) {
            return;
        }
        final Thread currentThread = Thread.currentThread();
        BooleanDebugOption[] array;
        if (currentThread == RenderThread.RenderThread) {
            array = this.ThreadCrash_RenderThread;
        }
        else if (currentThread == GameWindow.GameThread) {
            array = this.ThreadCrash_GameThread;
        }
        else {
            if (currentThread != GameLoadingState.loader) {
                return;
            }
            array = this.ThreadCrash_GameLoadingThread;
        }
        if (array[n].getValue()) {
            throw new Error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, currentThread.getName()));
        }
    }
    
    static {
        instance = new DebugOptions();
    }
    
    public static final class Checks extends OptionGroup
    {
        public final BooleanDebugOption BoundTextures;
        public final BooleanDebugOption SlowLuaEvents;
        
        public Checks() {
            super("Checks");
            this.BoundTextures = OptionGroup.newDebugOnlyOption(this.Group, "BoundTextures", false);
            this.SlowLuaEvents = OptionGroup.newDebugOnlyOption(this.Group, "SlowLuaEvents", false);
        }
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import zombie.core.profiling.PerformanceProfileProbe;
import zombie.characters.HaloTextHelper;
import zombie.iso.areas.IsoBuilding;
import zombie.core.utils.OnceEvery;
import zombie.iso.weather.fog.ImprovedFog;
import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;
import zombie.input.Mouse;
import zombie.core.textures.Texture;
import zombie.network.PassengerMap;
import zombie.network.ClientServerMap;
import zombie.debug.LineDrawer;
import zombie.iso.weather.WorldFlares;
import zombie.core.skinnedmodel.DeadBodyAtlas;
import zombie.core.SpriteRenderer;
import zombie.core.PerformanceSettings;
import java.util.Comparator;
import zombie.ai.State;
import zombie.ai.states.FakeDeadZombieState;
import zombie.iso.objects.IsoFireManager;
import zombie.iso.objects.RainManager;
import zombie.ai.ZombieGroupManager;
import zombie.WorldSoundManager;
import zombie.SoundManager;
import zombie.FliesSound;
import zombie.iso.objects.IsoDeadBody;
import zombie.CollisionManager;
import zombie.world.WorldDictionaryException;
import zombie.ReanimatedPlayers;
import zombie.ui.TutorialManager;
import zombie.characters.traits.TraitFactory;
import zombie.characters.professions.ProfessionFactory;
import zombie.core.physics.WorldSimulation;
import zombie.SystemDisabler;
import zombie.inventory.types.MapItem;
import zombie.globalObjects.GlobalObjectLookup;
import zombie.vehicles.PolygonalMap2;
import zombie.MapCollisionData;
import zombie.savefile.ServerPlayerDB;
import zombie.savefile.PlayerDBHelper;
import zombie.network.ServerMap;
import zombie.core.stash.StashSystem;
import zombie.iso.areas.SafeHouse;
import zombie.iso.weather.ClimateManager;
import zombie.vehicles.VehicleManager;
import zombie.vehicles.VehicleIDMap;
import zombie.VirtualZombieManager;
import zombie.PersistentOutfits;
import zombie.SharedDescriptors;
import zombie.world.WorldDictionary;
import zombie.erosion.ErosionGlobals;
import zombie.world.moddata.GlobalModData;
import zombie.radio.ZomboidRadio;
import zombie.iso.objects.ObjectRenderEffects;
import zombie.iso.areas.isoregion.IsoRegions;
import zombie.iso.weather.fx.WeatherFxMask;
import zombie.network.NetChecksum;
import zombie.ZomboidGlobals;
import zombie.SandboxOptions;
import zombie.vehicles.VehiclesDB2;
import zombie.inventory.ItemPickerJava;
import zombie.Lua.LuaEventManager;
import java.io.DataInputStream;
import zombie.Lua.LuaManager;
import zombie.network.BodyDamageSync;
import zombie.randomizedWorld.randomizedZoneStory.RZSMusicFest;
import zombie.randomizedWorld.randomizedZoneStory.RZSMusicFestStage;
import zombie.randomizedWorld.randomizedZoneStory.RZSBaseball;
import zombie.randomizedWorld.randomizedZoneStory.RZSTrapperCamp;
import zombie.randomizedWorld.randomizedZoneStory.RZSSexyTime;
import zombie.randomizedWorld.randomizedZoneStory.RZSHunterCamp;
import zombie.randomizedWorld.randomizedZoneStory.RZSBBQParty;
import zombie.randomizedWorld.randomizedZoneStory.RZSFishingTrip;
import zombie.randomizedWorld.randomizedZoneStory.RZSBeachParty;
import zombie.randomizedWorld.randomizedZoneStory.RZSBuryingCamp;
import zombie.randomizedWorld.randomizedZoneStory.RZSForestCampEaten;
import zombie.randomizedWorld.randomizedZoneStory.RZSForestCamp;
import zombie.randomizedWorld.randomizedVehicleStory.RVSCrashHorde;
import zombie.randomizedWorld.randomizedVehicleStory.RVSTrailerCrash;
import zombie.randomizedWorld.randomizedVehicleStory.RVSBanditRoad;
import zombie.randomizedWorld.randomizedVehicleStory.RVSFlippedCrash;
import zombie.randomizedWorld.randomizedVehicleStory.RVSChangingTire;
import zombie.randomizedWorld.randomizedVehicleStory.RVSCarCrashCorpse;
import zombie.randomizedWorld.randomizedVehicleStory.RVSAmbulanceCrash;
import zombie.randomizedWorld.randomizedVehicleStory.RVSCarCrash;
import zombie.randomizedWorld.randomizedVehicleStory.RVSPoliceBlockade;
import zombie.randomizedWorld.randomizedVehicleStory.RVSPoliceBlockadeShooting;
import zombie.randomizedWorld.randomizedVehicleStory.RVSBurntCar;
import zombie.randomizedWorld.randomizedVehicleStory.RVSConstructionSite;
import zombie.randomizedWorld.randomizedVehicleStory.RVSUtilityVehicle;
import zombie.randomizedWorld.randomizedBuilding.RBClinic;
import zombie.randomizedWorld.randomizedBuilding.RBHairSalon;
import zombie.randomizedWorld.randomizedBuilding.RBOffice;
import zombie.randomizedWorld.randomizedBuilding.RBBar;
import zombie.randomizedWorld.randomizedBuilding.RBCafe;
import zombie.randomizedWorld.randomizedBuilding.RBPileOCrepe;
import zombie.randomizedWorld.randomizedBuilding.RBPizzaWhirled;
import zombie.randomizedWorld.randomizedBuilding.RBSpiffo;
import zombie.randomizedWorld.randomizedBuilding.RBSchool;
import zombie.randomizedWorld.randomizedBuilding.RBStripclub;
import zombie.randomizedWorld.randomizedBuilding.RBKateAndBaldspot;
import zombie.randomizedWorld.randomizedBuilding.RBShopLooted;
import zombie.randomizedWorld.randomizedBuilding.RBBurntCorpse;
import zombie.randomizedWorld.randomizedBuilding.RBBurntFireman;
import zombie.randomizedWorld.randomizedBuilding.RBLooted;
import zombie.randomizedWorld.randomizedBuilding.RBOther;
import zombie.randomizedWorld.randomizedBuilding.RBBurnt;
import zombie.randomizedWorld.randomizedBuilding.RBSafehouse;
import zombie.gameStates.GameLoadingState;
import zombie.core.Translator;
import zombie.network.ServerOptions;
import zombie.savefile.PlayerDB;
import zombie.savefile.ClientPlayerDB;
import zombie.network.GameClient;
import zombie.iso.sprite.IsoDirectionFrame;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import zombie.GameWindow;
import zombie.core.TilePropertyAliasMap;
import java.util.Iterator;
import zombie.core.logger.ExceptionLogger;
import java.util.List;
import java.util.Collections;
import java.util.Collection;
import zombie.iso.sprite.IsoSpriteGrid;
import java.util.Map;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import java.util.HashSet;
import zombie.iso.sprite.IsoSprite;
import zombie.util.SharedStrings;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import zombie.debug.DebugLog;
import zombie.iso.sprite.IsoSpriteManager;
import java.io.InputStream;
import java.io.IOException;
import java.io.EOFException;
import java.io.RandomAccessFile;
import zombie.GameTime;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoPlayer;
import zombie.DebugFileWatcher;
import zombie.characters.TriggerSetAnimationRecorderFile;
import zombie.ZomboidFileSystem;
import zombie.network.GameServer;
import zombie.randomizedWorld.randomizedBuilding.RBBasic;
import java.nio.ByteBuffer;
import zombie.popman.ZombiePopulationManager;
import se.krka.kahlua.vm.KahluaTable;
import zombie.util.Type;
import fmod.fmod.FMODSoundEmitter;
import zombie.audio.DummySoundEmitter;
import zombie.core.Core;
import zombie.characters.IsoZombie;
import zombie.util.AddCoopPlayer;
import zombie.characters.SurvivorDesc;
import zombie.audio.BaseSoundEmitter;
import java.util.ArrayDeque;
import zombie.characters.IsoGameCharacter;
import zombie.PredicatedFileWatcher;
import zombie.iso.sprite.SkyBox;
import java.util.HashMap;
import zombie.randomizedWorld.randomizedVehicleStory.RandomizedVehicleStoryBase;
import zombie.randomizedWorld.randomizedZoneStory.RandomizedZoneStoryBase;
import zombie.randomizedWorld.randomizedBuilding.RandomizedBuildingBase;
import java.util.ArrayList;

public final class IsoWorld
{
    private String weather;
    public final IsoMetaGrid MetaGrid;
    private final ArrayList<RandomizedBuildingBase> randomizedBuildingList;
    private final ArrayList<RandomizedZoneStoryBase> randomizedZoneList;
    private final ArrayList<RandomizedVehicleStoryBase> randomizedVehicleStoryList;
    private final RandomizedBuildingBase RBBasic;
    private final HashMap<String, ArrayList<Double>> spawnedZombieZone;
    private final HashMap<String, ArrayList<String>> allTiles;
    private float flashIsoCursorA;
    private boolean flashIsoCursorInc;
    public SkyBox sky;
    private static PredicatedFileWatcher m_setAnimationRecordingTriggerWatcher;
    private static boolean m_animationRecorderActive;
    private static boolean m_animationRecorderDiscard;
    private int timeSinceLastSurvivorInHorde;
    private int m_frameNo;
    public final Helicopter helicopter;
    public final ArrayList<IsoGameCharacter> Characters;
    private final ArrayDeque<BaseSoundEmitter> freeEmitters;
    private final ArrayList<BaseSoundEmitter> currentEmitters;
    private final HashMap<BaseSoundEmitter, IsoObject> emitterOwners;
    public int x;
    public int y;
    public IsoCell CurrentCell;
    public static IsoWorld instance;
    public int TotalSurvivorsDead;
    public int TotalSurvivorNights;
    public int SurvivorSurvivalRecord;
    public HashMap<Integer, SurvivorDesc> SurvivorDescriptors;
    public ArrayList<AddCoopPlayer> AddCoopPlayers;
    private static final CompScoreToPlayer compScoreToPlayer;
    static CompDistToPlayer compDistToPlayer;
    public static String mapPath;
    public static boolean mapUseJar;
    boolean bLoaded;
    public static final HashMap<String, ArrayList<String>> PropertyValueMap;
    private static int WorldX;
    private static int WorldY;
    private SurvivorDesc luaDesc;
    private ArrayList<String> luatraits;
    private int luaSpawnCellX;
    private int luaSpawnCellY;
    private int luaPosX;
    private int luaPosY;
    private int luaPosZ;
    public static final int WorldVersion = 186;
    public static final int WorldVersion_Barricade = 87;
    public static final int WorldVersion_SandboxOptions = 88;
    public static final int WorldVersion_FliesSound = 121;
    public static final int WorldVersion_LootRespawn = 125;
    public static final int WorldVersion_OverlappingGenerators = 127;
    public static final int WorldVersion_ItemContainerIdenticalItems = 128;
    public static final int WorldVersion_VehicleSirenStartTime = 129;
    public static final int WorldVersion_CompostLastUpdated = 130;
    public static final int WorldVersion_DayLengthHours = 131;
    public static final int WorldVersion_LampOnPillar = 132;
    public static final int WorldVersion_AlarmClockRingSince = 134;
    public static final int WorldVersion_ClimateAdded = 135;
    public static final int WorldVersion_VehicleLightFocusing = 135;
    public static final int WorldVersion_GeneratorFuelFloat = 138;
    public static final int WorldVersion_InfectionTime = 142;
    public static final int WorldVersion_ClimateColors = 143;
    public static final int WorldVersion_BodyLocation = 144;
    public static final int WorldVersion_CharacterModelData = 145;
    public static final int WorldVersion_CharacterModelData2 = 146;
    public static final int WorldVersion_CharacterModelData3 = 147;
    public static final int WorldVersion_HumanVisualBlood = 148;
    public static final int WorldVersion_ItemContainerIdenticalItemsInt = 149;
    public static final int WorldVersion_PerkName = 152;
    public static final int WorldVersion_Thermos = 153;
    public static final int WorldVersion_AllPatches = 155;
    public static final int WorldVersion_ZombieRotStage = 156;
    public static final int WorldVersion_NewSandboxLootModifier = 157;
    public static final int WorldVersion_KateBobStorm = 158;
    public static final int WorldVersion_DeadBodyAngle = 159;
    public static final int WorldVersion_ChunkSpawnedRooms = 160;
    public static final int WorldVersion_DeathDragDown = 161;
    public static final int WorldVersion_CanUpgradePerk = 162;
    public static final int WorldVersion_ItemVisualFullType = 164;
    public static final int WorldVersion_VehicleBlood = 165;
    public static final int WorldVersion_DeadBodyZombieRotStage = 166;
    public static final int WorldVersion_Fitness = 167;
    public static final int WorldVersion_DeadBodyFakeDead = 168;
    public static final int WorldVersion_Fitness2 = 169;
    public static final int WorldVersion_NewFog = 170;
    public static final int WorldVersion_DeadBodyPersistentOutfitID = 171;
    public static final int WorldVersion_VehicleTowingID = 172;
    public static final int WorldVersion_VehicleJNITransform = 173;
    public static final int WorldVersion_VehicleTowAttachment = 174;
    public static final int WorldVersion_ContainerMaxCapacity = 175;
    public static final int WorldVersion_TimedActionInstantCheat = 176;
    public static final int WorldVersion_ClothingPatchSaveLoad = 178;
    public static final int WorldVersion_AttachedSlotType = 179;
    public static final int WorldVersion_NoiseMakerDuration = 180;
    public static final int WorldVersion_ChunkVehicles = 91;
    public static final int WorldVersion_PlayerVehicleSeat = 91;
    public static final int WorldVersion_MediaDisksAndTapes = 181;
    public static final int WorldVersion_AlreadyReadBooks1 = 182;
    public static final int WorldVersion_LampOnPillar2 = 183;
    public static final int WorldVersion_AlreadyReadBooks2 = 184;
    public static final int WorldVersion_PolygonZone = 185;
    public static final int WorldVersion_PolylineZone = 186;
    public static int SavedWorldVersion;
    private boolean bDrawWorld;
    private final ArrayList<IsoZombie> zombieWithModel;
    private final ArrayList<IsoZombie> zombieWithoutModel;
    public static boolean NoZombies;
    public static int TotalWorldVersion;
    public static int saveoffsetx;
    public static int saveoffsety;
    public boolean bDoChunkMapUpdate;
    private long emitterUpdateMS;
    public boolean emitterUpdate;
    static final /* synthetic */ boolean $assertionsDisabled;
    
    public IsoMetaGrid getMetaGrid() {
        return this.MetaGrid;
    }
    
    public IsoMetaGrid.Zone registerZone(final String s, final String s2, final int n, final int n2, final int n3, final int n4, final int n5) {
        return this.MetaGrid.registerZone(s, s2, n, n2, n3, n4, n5);
    }
    
    public IsoMetaGrid.Zone registerZoneNoOverlap(final String s, final String s2, final int n, final int n2, final int n3, final int n4, final int n5) {
        return this.MetaGrid.registerZoneNoOverlap(s, s2, n, n2, n3, n4, n5);
    }
    
    public void removeZonesForLotDirectory(final String s) {
        this.MetaGrid.removeZonesForLotDirectory(s);
    }
    
    public BaseSoundEmitter getFreeEmitter() {
        Object e;
        if (this.freeEmitters.isEmpty()) {
            e = (Core.SoundDisabled ? new DummySoundEmitter() : new FMODSoundEmitter());
        }
        else {
            e = this.freeEmitters.pop();
        }
        this.currentEmitters.add((BaseSoundEmitter)e);
        return (BaseSoundEmitter)e;
    }
    
    public BaseSoundEmitter getFreeEmitter(final float n, final float n2, final float n3) {
        final BaseSoundEmitter freeEmitter = this.getFreeEmitter();
        freeEmitter.setPos(n, n2, n3);
        return freeEmitter;
    }
    
    public void takeOwnershipOfEmitter(final BaseSoundEmitter o) {
        this.currentEmitters.remove(o);
    }
    
    public void setEmitterOwner(final BaseSoundEmitter baseSoundEmitter, final IsoObject value) {
        if (baseSoundEmitter == null || value == null) {
            return;
        }
        if (this.emitterOwners.containsKey(baseSoundEmitter)) {
            return;
        }
        this.emitterOwners.put(baseSoundEmitter, value);
    }
    
    public void returnOwnershipOfEmitter(final BaseSoundEmitter baseSoundEmitter) {
        if (baseSoundEmitter == null) {
            return;
        }
        if (this.currentEmitters.contains(baseSoundEmitter) || this.freeEmitters.contains(baseSoundEmitter)) {
            return;
        }
        if (baseSoundEmitter.isEmpty()) {
            final FMODSoundEmitter fmodSoundEmitter = Type.tryCastTo(baseSoundEmitter, FMODSoundEmitter.class);
            if (fmodSoundEmitter != null) {
                fmodSoundEmitter.clearParameters();
            }
            this.freeEmitters.add(baseSoundEmitter);
        }
        else {
            this.currentEmitters.add(baseSoundEmitter);
        }
    }
    
    public IsoMetaGrid.Zone registerVehiclesZone(final String s, final String s2, final int n, final int n2, final int n3, final int n4, final int n5, final KahluaTable kahluaTable) {
        return this.MetaGrid.registerVehiclesZone(s, s2, n, n2, n3, n4, n5, kahluaTable);
    }
    
    public IsoMetaGrid.Zone registerMannequinZone(final String s, final String s2, final int n, final int n2, final int n3, final int n4, final int n5, final KahluaTable kahluaTable) {
        return this.MetaGrid.registerMannequinZone(s, s2, n, n2, n3, n4, n5, kahluaTable);
    }
    
    public void registerSpawnOrigin(final int n, final int n2, final int n3, final int n4, final KahluaTable kahluaTable) {
        ZombiePopulationManager.instance.registerSpawnOrigin(n, n2, n3, n4, kahluaTable);
    }
    
    public void registerWaterFlow(final float n, final float n2, final float n3, final float n4) {
        IsoWaterFlow.addFlow(n, n2, n3, n4);
    }
    
    public void registerWaterZone(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        IsoWaterFlow.addZone(n, n2, n3, n4, n5, n6);
    }
    
    public void checkVehiclesZones() {
        this.MetaGrid.checkVehiclesZones();
    }
    
    public void setGameMode(final String s) {
        Core.GameMode = s;
        Core.bLastStand = "LastStand".equals(s);
        Core.getInstance().setChallenge(false);
        Core.ChallengeID = null;
    }
    
    public String getGameMode() {
        return Core.GameMode;
    }
    
    public void setWorld(final String s) {
        Core.GameSaveWorld = s.trim();
    }
    
    public void setMap(final String gameMap) {
        Core.GameMap = gameMap;
    }
    
    public String getMap() {
        return Core.GameMap;
    }
    
    public void renderTerrain() {
    }
    
    public int getFrameNo() {
        return this.m_frameNo;
    }
    
    public IsoObject getItemFromXYZIndexBuffer(final ByteBuffer byteBuffer) {
        final IsoGridSquare gridSquare = this.CurrentCell.getGridSquare(byteBuffer.getInt(), byteBuffer.getInt(), byteBuffer.getInt());
        if (gridSquare == null) {
            return null;
        }
        final byte value = byteBuffer.get();
        if (value >= 0 && value < gridSquare.getObjects().size()) {
            return gridSquare.getObjects().get(value);
        }
        return null;
    }
    
    public IsoWorld() {
        this.weather = "sunny";
        this.MetaGrid = new IsoMetaGrid();
        this.randomizedBuildingList = new ArrayList<RandomizedBuildingBase>();
        this.randomizedZoneList = new ArrayList<RandomizedZoneStoryBase>();
        this.randomizedVehicleStoryList = new ArrayList<RandomizedVehicleStoryBase>();
        this.RBBasic = new RBBasic();
        this.spawnedZombieZone = new HashMap<String, ArrayList<Double>>();
        this.allTiles = new HashMap<String, ArrayList<String>>();
        this.flashIsoCursorA = 1.0f;
        this.flashIsoCursorInc = false;
        this.sky = null;
        this.timeSinceLastSurvivorInHorde = 4000;
        this.m_frameNo = 0;
        this.helicopter = new Helicopter();
        this.Characters = new ArrayList<IsoGameCharacter>();
        this.freeEmitters = new ArrayDeque<BaseSoundEmitter>();
        this.currentEmitters = new ArrayList<BaseSoundEmitter>();
        this.emitterOwners = new HashMap<BaseSoundEmitter, IsoObject>();
        this.x = 50;
        this.y = 50;
        this.TotalSurvivorsDead = 0;
        this.TotalSurvivorNights = 0;
        this.SurvivorSurvivalRecord = 0;
        this.SurvivorDescriptors = new HashMap<Integer, SurvivorDesc>();
        this.AddCoopPlayers = new ArrayList<AddCoopPlayer>();
        this.bLoaded = false;
        this.luaSpawnCellX = -1;
        this.luaSpawnCellY = -1;
        this.luaPosX = -1;
        this.luaPosY = -1;
        this.luaPosZ = -1;
        this.bDrawWorld = true;
        this.zombieWithModel = new ArrayList<IsoZombie>();
        this.zombieWithoutModel = new ArrayList<IsoZombie>();
        this.bDoChunkMapUpdate = true;
        if (!GameServer.bServer) {}
    }
    
    private static void initMessaging() {
        if (IsoWorld.m_setAnimationRecordingTriggerWatcher == null) {
            IsoWorld.m_setAnimationRecordingTriggerWatcher = new PredicatedFileWatcher(ZomboidFileSystem.instance.getMessagingDirSub("Trigger_AnimationRecorder.xml"), (Class<T>)TriggerSetAnimationRecorderFile.class, (PredicatedFileWatcher.IPredicatedDataPacketFileWatcherCallback<T>)IsoWorld::onTrigger_setAnimationRecorderTriggerFile);
            DebugFileWatcher.instance.add(IsoWorld.m_setAnimationRecordingTriggerWatcher);
        }
    }
    
    private static void onTrigger_setAnimationRecorderTriggerFile(final TriggerSetAnimationRecorderFile triggerSetAnimationRecorderFile) {
        IsoWorld.m_animationRecorderActive = triggerSetAnimationRecorderFile.isRecording;
        IsoWorld.m_animationRecorderDiscard = triggerSetAnimationRecorderFile.discard;
    }
    
    public static boolean isAnimRecorderActive() {
        return IsoWorld.m_animationRecorderActive;
    }
    
    public static boolean isAnimRecorderDiscardTriggered() {
        return IsoWorld.m_animationRecorderDiscard;
    }
    
    public IsoSurvivor CreateRandomSurvivor(final SurvivorDesc survivorDesc, final IsoGridSquare isoGridSquare, final IsoPlayer isoPlayer) {
        return null;
    }
    
    public void CreateSwarm(final int n, final int n2, final int n3, final int n4, final int n5) {
    }
    
    public void ForceKillAllZombies() {
        GameTime.getInstance().RemoveZombiesIndiscriminate(1000);
    }
    
    public static int readInt(final RandomAccessFile randomAccessFile) throws EOFException, IOException {
        final int read = randomAccessFile.read();
        final int read2 = randomAccessFile.read();
        final int read3 = randomAccessFile.read();
        final int read4 = randomAccessFile.read();
        if ((read | read2 | read3 | read4) < 0) {
            throw new EOFException();
        }
        return (read << 0) + (read2 << 8) + (read3 << 16) + (read4 << 24);
    }
    
    public static String readString(final RandomAccessFile randomAccessFile) throws EOFException, IOException {
        return randomAccessFile.readLine();
    }
    
    public static int readInt(final InputStream inputStream) throws EOFException, IOException {
        final int read = inputStream.read();
        final int read2 = inputStream.read();
        final int read3 = inputStream.read();
        final int read4 = inputStream.read();
        if ((read | read2 | read3 | read4) < 0) {
            throw new EOFException();
        }
        return (read << 0) + (read2 << 8) + (read3 << 16) + (read4 << 24);
    }
    
    public static String readString(final InputStream inputStream) throws IOException {
        final StringBuilder sb = new StringBuilder();
        int read = -1;
        int i = 0;
        while (i == 0) {
            switch (read = inputStream.read()) {
                case -1:
                case 10: {
                    i = 1;
                    continue;
                }
                case 13: {
                    throw new IllegalStateException("\r\n unsupported");
                }
                default: {
                    sb.append((char)read);
                    continue;
                }
            }
        }
        if (read == -1 && sb.length() == 0) {
            return null;
        }
        return sb.toString();
    }
    
    public void LoadTileDefinitions(final IsoSpriteManager isoSpriteManager, final String name, final int n) {
        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, name));
        final boolean endsWith = name.endsWith(".patch.tiles");
        try {
            final FileInputStream in = new FileInputStream(name);
            try {
                final BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
                try {
                    readInt(bufferedInputStream);
                    readInt(bufferedInputStream);
                    final int int1 = readInt(bufferedInputStream);
                    final SharedStrings sharedStrings = new SharedStrings();
                    final boolean b = false;
                    final boolean b2 = false;
                    final ArrayList<IsoSprite> list = new ArrayList<IsoSprite>();
                    final HashMap<Object, ArrayList<IsoSprite>> hashMap = new HashMap<Object, ArrayList<IsoSprite>>();
                    final HashMap<Object, ArrayList<Object>> hashMap2 = new HashMap<Object, ArrayList<Object>>();
                    final String[] array = { "N", "E", "S", "W" };
                    for (int i = 0; i < array.length; ++i) {
                        hashMap2.put(array[i], new ArrayList<Object>());
                    }
                    final ArrayList<String> list2 = new ArrayList<String>();
                    final HashMap hashMap3 = new HashMap<Object, Object>();
                    int n2 = 0;
                    int n3 = 0;
                    int n4 = 0;
                    int n5 = 0;
                    final HashSet<String> c = new HashSet<String>();
                    for (int j = 0; j < int1; ++j) {
                        final String trim = readString(bufferedInputStream).trim();
                        readString(bufferedInputStream);
                        readInt(bufferedInputStream);
                        readInt(bufferedInputStream);
                        final int int2 = readInt(bufferedInputStream);
                        for (int int3 = readInt(bufferedInputStream), k = 0; k < int3; ++k) {
                            IsoSprite e;
                            if (endsWith) {
                                e = isoSpriteManager.NamedMap.get(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, trim, k));
                                if (e == null) {
                                    continue;
                                }
                            }
                            else if (n < 2) {
                                e = isoSpriteManager.AddSprite(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, trim, k), n * 100 * 1000 + 10000 + int2 * 1000 + k);
                            }
                            else {
                                e = isoSpriteManager.AddSprite(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, trim, k), n * 512 * 512 + int2 * 512 + k);
                            }
                            if (Core.bDebug) {
                                if (this.allTiles.containsKey(trim)) {
                                    if (!endsWith) {
                                        this.allTiles.get(trim).add(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, trim, k));
                                    }
                                }
                                else {
                                    final ArrayList<String> value = new ArrayList<String>();
                                    value.add(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, trim, k));
                                    this.allTiles.put(trim, value);
                                }
                            }
                            list.add(e);
                            if (!endsWith) {
                                e.setName(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, trim, k));
                                e.tileSheetIndex = k;
                            }
                            if (e.name.contains("damaged") || e.name.contains("trash_")) {
                                e.attachedFloor = true;
                                e.getProperties().Set("attachedFloor", "true");
                            }
                            if (e.name.startsWith("f_bushes") && k <= 31) {
                                e.isBush = true;
                                e.attachedFloor = true;
                            }
                            for (int int4 = readInt(bufferedInputStream), l = 0; l < int4; ++l) {
                                String anObject = readString(bufferedInputStream).trim();
                                String trim2 = readString(bufferedInputStream).trim();
                                final IsoObjectType fromString = IsoObjectType.FromString(anObject);
                                if (fromString != IsoObjectType.MAX) {
                                    if ((e.getType() != IsoObjectType.doorW && e.getType() != IsoObjectType.doorN) || fromString != IsoObjectType.wall) {
                                        e.setType(fromString);
                                    }
                                    if (fromString == IsoObjectType.doorW) {
                                        e.getProperties().Set(IsoFlagType.doorW);
                                    }
                                    else if (fromString == IsoObjectType.doorN) {
                                        e.getProperties().Set(IsoFlagType.doorN);
                                    }
                                }
                                else {
                                    anObject = sharedStrings.get(anObject);
                                    if (anObject.equals("firerequirement")) {
                                        e.firerequirement = Integer.parseInt(trim2);
                                    }
                                    else if (anObject.equals("fireRequirement")) {
                                        e.firerequirement = Integer.parseInt(trim2);
                                    }
                                    else if (anObject.equals("BurntTile")) {
                                        e.burntTile = trim2;
                                    }
                                    else if (anObject.equals("ForceAmbient")) {
                                        e.forceAmbient = true;
                                        e.getProperties().Set(anObject, trim2);
                                    }
                                    else if (anObject.equals("solidfloor")) {
                                        e.solidfloor = true;
                                        e.getProperties().Set(anObject, trim2);
                                    }
                                    else if (anObject.equals("canBeRemoved")) {
                                        e.canBeRemoved = true;
                                        e.getProperties().Set(anObject, trim2);
                                    }
                                    else if (anObject.equals("attachedFloor")) {
                                        e.attachedFloor = true;
                                        e.getProperties().Set(anObject, trim2);
                                    }
                                    else if (anObject.equals("cutW")) {
                                        e.cutW = true;
                                        e.getProperties().Set(anObject, trim2);
                                    }
                                    else if (anObject.equals("cutN")) {
                                        e.cutN = true;
                                        e.getProperties().Set(anObject, trim2);
                                    }
                                    else if (anObject.equals("solid")) {
                                        e.solid = true;
                                        e.getProperties().Set(anObject, trim2);
                                    }
                                    else if (anObject.equals("solidTrans")) {
                                        e.solidTrans = true;
                                        e.getProperties().Set(anObject, trim2);
                                    }
                                    else if (anObject.equals("invisible")) {
                                        e.invisible = true;
                                        e.getProperties().Set(anObject, trim2);
                                    }
                                    else if (anObject.equals("alwaysDraw")) {
                                        e.alwaysDraw = true;
                                        e.getProperties().Set(anObject, trim2);
                                    }
                                    else if (anObject.equals("forceRender")) {
                                        e.forceRender = true;
                                        e.getProperties().Set(anObject, trim2);
                                    }
                                    else if ("FloorHeight".equals(anObject)) {
                                        if ("OneThird".equals(trim2)) {
                                            e.getProperties().Set(IsoFlagType.FloorHeightOneThird);
                                        }
                                        else if ("TwoThirds".equals(trim2)) {
                                            e.getProperties().Set(IsoFlagType.FloorHeightTwoThirds);
                                        }
                                    }
                                    else if (anObject.equals("MoveWithWind")) {
                                        e.moveWithWind = true;
                                        e.getProperties().Set(anObject, trim2);
                                    }
                                    else if (anObject.equals("WindType")) {
                                        e.windType = Integer.parseInt(trim2);
                                        e.getProperties().Set(anObject, trim2);
                                    }
                                    else if (anObject.equals("RenderLayer")) {
                                        e.getProperties().Set(anObject, trim2);
                                        if ("Default".equals(trim2)) {
                                            e.renderLayer = 0;
                                        }
                                        else if ("Floor".equals(trim2)) {
                                            e.renderLayer = 1;
                                        }
                                    }
                                    else if (anObject.equals("TreatAsWallOrder")) {
                                        e.treatAsWallOrder = true;
                                        e.getProperties().Set(anObject, trim2);
                                    }
                                    else {
                                        e.getProperties().Set(anObject, trim2);
                                        if ("WindowN".equals(anObject) || "WindowW".equals(anObject)) {
                                            e.getProperties().Set(anObject, trim2, false);
                                        }
                                    }
                                }
                                if (fromString == IsoObjectType.tree) {
                                    if (e.name.equals("e_riverbirch_1_1")) {
                                        trim2 = "1";
                                    }
                                    e.getProperties().Set("tree", trim2);
                                    e.getProperties().UnSet(IsoFlagType.solid);
                                    e.getProperties().Set(IsoFlagType.blocksight);
                                    int int5 = Integer.parseInt(trim2);
                                    if (trim.startsWith("vegetation_trees")) {
                                        int5 = 4;
                                    }
                                    if (int5 < 1) {
                                        int5 = 1;
                                    }
                                    if (int5 > 4) {
                                        int5 = 4;
                                    }
                                    if (int5 == 1 || int5 == 2) {
                                        e.getProperties().UnSet(IsoFlagType.blocksight);
                                    }
                                }
                                if (anObject.equals("interior") && trim2.equals("false")) {
                                    e.getProperties().Set(IsoFlagType.exterior);
                                }
                                if (anObject.equals("HoppableN")) {
                                    e.getProperties().Set(IsoFlagType.collideN);
                                    e.getProperties().Set(IsoFlagType.canPathN);
                                    e.getProperties().Set(IsoFlagType.transparentN);
                                }
                                if (anObject.equals("HoppableW")) {
                                    e.getProperties().Set(IsoFlagType.collideW);
                                    e.getProperties().Set(IsoFlagType.canPathW);
                                    e.getProperties().Set(IsoFlagType.transparentW);
                                }
                                if (anObject.equals("WallN")) {
                                    e.getProperties().Set(IsoFlagType.collideN);
                                    e.getProperties().Set(IsoFlagType.cutN);
                                    e.setType(IsoObjectType.wall);
                                    e.cutN = true;
                                    e.getProperties().Set("WallN", "", false);
                                }
                                if (anObject.equals("CantClimb")) {
                                    e.getProperties().Set(IsoFlagType.CantClimb);
                                }
                                else if (anObject.equals("container")) {
                                    e.getProperties().Set(anObject, trim2, false);
                                }
                                else if (anObject.equals("WallNTrans")) {
                                    e.getProperties().Set(IsoFlagType.collideN);
                                    e.getProperties().Set(IsoFlagType.cutN);
                                    e.getProperties().Set(IsoFlagType.transparentN);
                                    e.setType(IsoObjectType.wall);
                                    e.cutN = true;
                                    e.getProperties().Set("WallNTrans", "", false);
                                }
                                else if (anObject.equals("WallW")) {
                                    e.getProperties().Set(IsoFlagType.collideW);
                                    e.getProperties().Set(IsoFlagType.cutW);
                                    e.setType(IsoObjectType.wall);
                                    e.cutW = true;
                                    e.getProperties().Set("WallW", "", false);
                                }
                                else if (anObject.equals("windowN")) {
                                    e.getProperties().Set("WindowN", "WindowN");
                                    e.getProperties().Set(IsoFlagType.transparentN);
                                    e.getProperties().Set("WindowN", "WindowN", false);
                                }
                                else if (anObject.equals("windowW")) {
                                    e.getProperties().Set("WindowW", "WindowW");
                                    e.getProperties().Set(IsoFlagType.transparentW);
                                    e.getProperties().Set("WindowW", "WindowW", false);
                                }
                                else if (anObject.equals("cutW")) {
                                    e.getProperties().Set(IsoFlagType.cutW);
                                    e.cutW = true;
                                }
                                else if (anObject.equals("cutN")) {
                                    e.getProperties().Set(IsoFlagType.cutN);
                                    e.cutN = true;
                                }
                                else if (anObject.equals("WallWTrans")) {
                                    e.getProperties().Set(IsoFlagType.collideW);
                                    e.getProperties().Set(IsoFlagType.transparentW);
                                    e.getProperties().Set(IsoFlagType.cutW);
                                    e.setType(IsoObjectType.wall);
                                    e.cutW = true;
                                    e.getProperties().Set("WallWTrans", "", false);
                                }
                                else if (anObject.equals("DoorWallN")) {
                                    e.getProperties().Set(IsoFlagType.cutN);
                                    e.cutN = true;
                                    e.getProperties().Set("DoorWallN", "", false);
                                }
                                else if (anObject.equals("DoorWallW")) {
                                    e.getProperties().Set(IsoFlagType.cutW);
                                    e.cutW = true;
                                    e.getProperties().Set("DoorWallW", "", false);
                                }
                                else if (anObject.equals("WallNW")) {
                                    e.getProperties().Set(IsoFlagType.collideN);
                                    e.getProperties().Set(IsoFlagType.cutN);
                                    e.getProperties().Set(IsoFlagType.collideW);
                                    e.getProperties().Set(IsoFlagType.cutW);
                                    e.setType(IsoObjectType.wall);
                                    e.cutW = true;
                                    e.cutN = true;
                                    e.getProperties().Set("WallNW", "", false);
                                }
                                else if (anObject.equals("WallNWTrans")) {
                                    e.getProperties().Set(IsoFlagType.collideN);
                                    e.getProperties().Set(IsoFlagType.cutN);
                                    e.getProperties().Set(IsoFlagType.collideW);
                                    e.getProperties().Set(IsoFlagType.transparentN);
                                    e.getProperties().Set(IsoFlagType.transparentW);
                                    e.getProperties().Set(IsoFlagType.cutW);
                                    e.setType(IsoObjectType.wall);
                                    e.cutW = true;
                                    e.cutN = true;
                                    e.getProperties().Set("WallNWTrans", "", false);
                                }
                                else if (anObject.equals("WallSE")) {
                                    e.getProperties().Set(IsoFlagType.cutW);
                                    e.getProperties().Set(IsoFlagType.WallSE);
                                    e.getProperties().Set("WallSE", "WallSE");
                                    e.cutW = true;
                                }
                                else if (anObject.equals("WindowW")) {
                                    e.getProperties().Set(IsoFlagType.canPathW);
                                    e.getProperties().Set(IsoFlagType.collideW);
                                    e.getProperties().Set(IsoFlagType.cutW);
                                    e.getProperties().Set(IsoFlagType.transparentW);
                                    e.setType(IsoObjectType.windowFW);
                                    if (e.getProperties().Is(IsoFlagType.HoppableW)) {
                                        if (Core.bDebug) {
                                            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getName()));
                                        }
                                        e.getProperties().UnSet(IsoFlagType.HoppableW);
                                    }
                                    e.cutW = true;
                                }
                                else if (anObject.equals("WindowN")) {
                                    e.getProperties().Set(IsoFlagType.canPathN);
                                    e.getProperties().Set(IsoFlagType.collideN);
                                    e.getProperties().Set(IsoFlagType.cutN);
                                    e.getProperties().Set(IsoFlagType.transparentN);
                                    e.setType(IsoObjectType.windowFN);
                                    if (e.getProperties().Is(IsoFlagType.HoppableN)) {
                                        if (Core.bDebug) {
                                            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getName()));
                                        }
                                        e.getProperties().UnSet(IsoFlagType.HoppableN);
                                    }
                                    e.cutN = true;
                                }
                                else if (anObject.equals("UnbreakableWindowW")) {
                                    e.getProperties().Set(IsoFlagType.canPathW);
                                    e.getProperties().Set(IsoFlagType.collideW);
                                    e.getProperties().Set(IsoFlagType.cutW);
                                    e.getProperties().Set(IsoFlagType.transparentW);
                                    e.getProperties().Set(IsoFlagType.collideW);
                                    e.setType(IsoObjectType.wall);
                                    e.cutW = true;
                                }
                                else if (anObject.equals("UnbreakableWindowN")) {
                                    e.getProperties().Set(IsoFlagType.canPathN);
                                    e.getProperties().Set(IsoFlagType.collideN);
                                    e.getProperties().Set(IsoFlagType.cutN);
                                    e.getProperties().Set(IsoFlagType.transparentN);
                                    e.getProperties().Set(IsoFlagType.collideN);
                                    e.setType(IsoObjectType.wall);
                                    e.cutN = true;
                                }
                                else if (anObject.equals("UnbreakableWindowNW")) {
                                    e.getProperties().Set(IsoFlagType.cutN);
                                    e.getProperties().Set(IsoFlagType.transparentN);
                                    e.getProperties().Set(IsoFlagType.collideN);
                                    e.getProperties().Set(IsoFlagType.cutN);
                                    e.getProperties().Set(IsoFlagType.collideW);
                                    e.getProperties().Set(IsoFlagType.cutW);
                                    e.setType(IsoObjectType.wall);
                                    e.cutW = true;
                                    e.cutN = true;
                                }
                                else if ("NoWallLighting".equals(anObject)) {
                                    e.getProperties().Set(IsoFlagType.NoWallLighting);
                                }
                                else if ("ForceAmbient".equals(anObject)) {
                                    e.getProperties().Set(IsoFlagType.ForceAmbient);
                                }
                                if (anObject.equals("name")) {
                                    e.setParentObjectName(trim2);
                                }
                            }
                            if (e.getProperties().Is("lightR") || e.getProperties().Is("lightG") || e.getProperties().Is("lightB")) {
                                if (!e.getProperties().Is("lightR")) {
                                    e.getProperties().Set("lightR", "0");
                                }
                                if (!e.getProperties().Is("lightG")) {
                                    e.getProperties().Set("lightG", "0");
                                }
                                if (!e.getProperties().Is("lightB")) {
                                    e.getProperties().Set("lightB", "0");
                                }
                            }
                            e.getProperties().CreateKeySet();
                            if (Core.bDebug && e.getProperties().Is("SmashedTileOffset") && !e.getProperties().Is("GlassRemovedOffset")) {
                                DebugLog.General.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getName()));
                            }
                        }
                        hashMap.clear();
                        for (final IsoSprite e2 : list) {
                            if (e2.getProperties().Is("StopCar")) {
                                e2.setType(IsoObjectType.isMoveAbleObject);
                            }
                            if (e2.getProperties().Is("IsMoveAble")) {
                                if (!e2.getProperties().Is("CustomName") || e2.getProperties().Val("CustomName").equals("")) {
                                    DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, trim));
                                }
                                else {
                                    ++n2;
                                    if (e2.getProperties().Is("GroupName")) {
                                        final String e3 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, e2.getProperties().Val("GroupName"), e2.getProperties().Val("CustomName"));
                                        if (!hashMap.containsKey(e3)) {
                                            hashMap.put(e3, new ArrayList<IsoSprite>());
                                        }
                                        hashMap.get(e3).add(e2);
                                        c.add(e3);
                                    }
                                    else {
                                        if (!hashMap3.containsKey(trim)) {
                                            hashMap3.put(trim, new ArrayList<String>());
                                        }
                                        if (!hashMap3.get(trim).contains(e2.getProperties().Val("CustomName"))) {
                                            hashMap3.get(trim).add(e2.getProperties().Val("CustomName"));
                                        }
                                        ++n3;
                                        c.add(e2.getProperties().Val("CustomName"));
                                    }
                                }
                            }
                        }
                        for (final Map.Entry<String, ArrayList<IsoSprite>> entry : hashMap.entrySet()) {
                            final String s = entry.getKey();
                            if (!hashMap3.containsKey(trim)) {
                                hashMap3.put(trim, new ArrayList<String>());
                            }
                            if (!hashMap3.get(trim).contains(s)) {
                                hashMap3.get(trim).add(s);
                            }
                            final ArrayList<IsoSprite> list3 = entry.getValue();
                            if (list3.size() == 1) {
                                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, trim));
                            }
                            if (list3.size() == 3) {
                                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, trim));
                            }
                            final String[] array2 = array;
                            for (int length = array2.length, n6 = 0; n6 < length; ++n6) {
                                ((ArrayList<Object>)hashMap2.get(array2[n6])).clear();
                            }
                            final boolean b3 = list3.get(0).getProperties().Is("SpriteGridPos") && !list3.get(0).getProperties().Val("SpriteGridPos").equals("None");
                            int n7 = 1;
                            for (final IsoSprite isoSprite : list3) {
                                if (b3 != (isoSprite.getProperties().Is("SpriteGridPos") && !isoSprite.getProperties().Val("SpriteGridPos").equals("None"))) {
                                    n7 = 0;
                                    DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, trim));
                                    break;
                                }
                                if (!isoSprite.getProperties().Is("Facing")) {
                                    n7 = 0;
                                }
                                else {
                                    final String val = isoSprite.getProperties().Val("Facing");
                                    switch (val) {
                                        case "N": {
                                            hashMap2.get("N").add(isoSprite);
                                            break;
                                        }
                                        case "E": {
                                            hashMap2.get("E").add(isoSprite);
                                            break;
                                        }
                                        case "S": {
                                            hashMap2.get("S").add(isoSprite);
                                            break;
                                        }
                                        case "W": {
                                            hashMap2.get("W").add(isoSprite);
                                            break;
                                        }
                                        default: {
                                            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, isoSprite.getProperties().Val("Facing"), s, trim));
                                            n7 = 0;
                                            break;
                                        }
                                    }
                                }
                                if (n7 == 0) {
                                    DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, trim));
                                    break;
                                }
                            }
                            if (n7 == 0) {
                                continue;
                            }
                            if (!b3) {
                                if (list3.size() > 4) {
                                    DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, trim));
                                }
                                else {
                                    for (final String s2 : array) {
                                        if (hashMap2.get(s2).size() > 1) {
                                            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, s, trim));
                                            n7 = 0;
                                        }
                                    }
                                    if (n7 == 0) {
                                        continue;
                                    }
                                    ++n4;
                                    for (final IsoSprite o : list3) {
                                        for (final String s3 : array) {
                                            final ArrayList<Object> list4 = hashMap2.get(s3);
                                            if (list4.size() > 0 && list4.get(0) != o) {
                                                o.getProperties().Set(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s3), Integer.toString(list.indexOf(list4.get(0)) - list.indexOf(o)));
                                            }
                                        }
                                    }
                                }
                            }
                            else {
                                int size = 0;
                                final IsoSpriteGrid[] array5 = new IsoSpriteGrid[array.length];
                                for (int n11 = 0; n11 < array.length; ++n11) {
                                    final ArrayList<Object> list5 = hashMap2.get(array[n11]);
                                    if (list5.size() > 0) {
                                        if (size == 0) {
                                            size = list5.size();
                                        }
                                        if (size != list5.size()) {
                                            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, trim));
                                            n7 = 0;
                                            break;
                                        }
                                        list2.clear();
                                        int n12 = -1;
                                        int n13 = -1;
                                        final Iterator<IsoSprite> iterator5 = list5.iterator();
                                        while (iterator5.hasNext()) {
                                            final String val2 = iterator5.next().getProperties().Val("SpriteGridPos");
                                            if (list2.contains(val2)) {
                                                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, val2, s, trim));
                                                n7 = 0;
                                                break;
                                            }
                                            list2.add(val2);
                                            final String[] split = val2.split(",");
                                            if (split.length != 2) {
                                                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, trim));
                                                n7 = 0;
                                                break;
                                            }
                                            final int int6 = Integer.parseInt(split[0]);
                                            final int int7 = Integer.parseInt(split[1]);
                                            if (int6 > n12) {
                                                n12 = int6;
                                            }
                                            if (int7 <= n13) {
                                                continue;
                                            }
                                            n13 = int7;
                                        }
                                        if (n12 == -1 || n13 == -1 || (n12 + 1) * (n13 + 1) != list5.size()) {
                                            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, trim));
                                            n7 = 0;
                                            break;
                                        }
                                        if (n7 == 0) {
                                            break;
                                        }
                                        array5[n11] = new IsoSpriteGrid(n12 + 1, n13 + 1);
                                        for (final IsoSprite isoSprite2 : list5) {
                                            final String[] split2 = isoSprite2.getProperties().Val("SpriteGridPos").split(",");
                                            array5[n11].setSprite(Integer.parseInt(split2[0]), Integer.parseInt(split2[1]), isoSprite2);
                                        }
                                        if (!array5[n11].validate()) {
                                            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, trim));
                                            n7 = 0;
                                            break;
                                        }
                                    }
                                }
                                if (n7 == 0 || size == 0) {
                                    DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, trim));
                                }
                                else {
                                    ++n5;
                                    for (int n14 = 0; n14 < array.length; ++n14) {
                                        final IsoSpriteGrid spriteGrid = array5[n14];
                                        if (spriteGrid != null) {
                                            for (final IsoSprite o2 : spriteGrid.getSprites()) {
                                                o2.setSpriteGrid(spriteGrid);
                                                for (int n16 = 0; n16 < array.length; ++n16) {
                                                    if (n16 != n14 && array5[n16] != null) {
                                                        o2.getProperties().Set(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, array[n16]), Integer.toString(list.indexOf(array5[n16].getAnchorSprite()) - list.indexOf(o2)));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        list.clear();
                    }
                    if (b2) {
                        final ArrayList list6 = new ArrayList<String>(c);
                        Collections.sort((List<Comparable>)list6);
                        for (final String s4 : list6) {
                            System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s4.replaceAll(" ", "_").replaceAll("-", "_").replaceAll("'", "").replaceAll("\\.", ""), s4));
                        }
                    }
                    if (b) {
                        try {
                            this.saveMovableStats(hashMap3, n, n3, n4, n5, n2);
                        }
                        catch (Exception ex2) {}
                    }
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
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
        }
    }
    
    private void GenerateTilePropertyLookupTables() {
        TilePropertyAliasMap.instance.Generate(IsoWorld.PropertyValueMap);
        IsoWorld.PropertyValueMap.clear();
    }
    
    public void LoadTileDefinitionsPropertyStrings(final IsoSpriteManager isoSpriteManager, final String name, final int n) {
        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, name));
        if (!GameServer.bServer) {
            Thread.yield();
            Core.getInstance().DoFrameReady();
        }
        try {
            final FileInputStream in = new FileInputStream(name);
            try {
                final BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
                try {
                    readInt(bufferedInputStream);
                    readInt(bufferedInputStream);
                    final int int1 = readInt(bufferedInputStream);
                    final SharedStrings sharedStrings = new SharedStrings();
                    for (int i = 0; i < int1; ++i) {
                        readString(bufferedInputStream).trim();
                        readString(bufferedInputStream);
                        readInt(bufferedInputStream);
                        readInt(bufferedInputStream);
                        readInt(bufferedInputStream);
                        for (int int2 = readInt(bufferedInputStream), j = 0; j < int2; ++j) {
                            for (int int3 = readInt(bufferedInputStream), k = 0; k < int3; ++k) {
                                final String trim = readString(bufferedInputStream).trim();
                                final String trim2 = readString(bufferedInputStream).trim();
                                IsoObjectType.FromString(trim);
                                final String value = sharedStrings.get(trim);
                                ArrayList<String> value2;
                                if (IsoWorld.PropertyValueMap.containsKey(value)) {
                                    value2 = IsoWorld.PropertyValueMap.get(value);
                                }
                                else {
                                    value2 = new ArrayList<String>();
                                    IsoWorld.PropertyValueMap.put(value, value2);
                                }
                                if (!value2.contains(trim2)) {
                                    value2.add(trim2);
                                }
                            }
                        }
                    }
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
        catch (Exception thrown) {
            Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, null, thrown);
        }
    }
    
    private void SetCustomPropertyValues() {
        IsoWorld.PropertyValueMap.get("WindowN").add("WindowN");
        IsoWorld.PropertyValueMap.get("WindowW").add("WindowW");
        IsoWorld.PropertyValueMap.get("DoorWallN").add("DoorWallN");
        IsoWorld.PropertyValueMap.get("DoorWallW").add("DoorWallW");
        IsoWorld.PropertyValueMap.get("WallSE").add("WallSE");
        final ArrayList<String> list = new ArrayList<String>();
        for (int i = -96; i <= 96; ++i) {
            list.add(Integer.toString(i));
        }
        IsoWorld.PropertyValueMap.put("Noffset", list);
        IsoWorld.PropertyValueMap.put("Soffset", list);
        IsoWorld.PropertyValueMap.put("Woffset", list);
        IsoWorld.PropertyValueMap.put("Eoffset", list);
        IsoWorld.PropertyValueMap.get("tree").add("5");
        IsoWorld.PropertyValueMap.get("tree").add("6");
        IsoWorld.PropertyValueMap.get("lightR").add("0");
        IsoWorld.PropertyValueMap.get("lightG").add("0");
        IsoWorld.PropertyValueMap.get("lightB").add("0");
    }
    
    private void saveMovableStats(final Map<String, ArrayList<String>> map, final int n, final int n2, final int n3, final int n4, final int n5) throws FileNotFoundException, IOException {
        final File file = new File(ZomboidFileSystem.instance.getCacheDir());
        if (file.exists() && file.isDirectory()) {
            final File file2 = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;I)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator, n));
            try {
                final FileWriter fileWriter = new FileWriter(file2, false);
                try {
                    fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.lineSeparator()));
                    fileWriter.write(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, n2, System.lineSeparator()));
                    fileWriter.write(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, n3, System.lineSeparator()));
                    fileWriter.write(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, n4, System.lineSeparator()));
                    fileWriter.write(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, n2 + n3 + n4, System.lineSeparator()));
                    fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.lineSeparator()));
                    fileWriter.write(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, n5, System.lineSeparator()));
                    fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.lineSeparator()));
                    for (final Map.Entry<String, ArrayList<String>> entry : map.entrySet()) {
                        fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, (String)entry.getKey(), System.lineSeparator()));
                        final Iterator<String> iterator2 = entry.getValue().iterator();
                        while (iterator2.hasNext()) {
                            fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, (String)iterator2.next(), System.lineSeparator()));
                        }
                    }
                    fileWriter.close();
                }
                catch (Throwable t) {
                    try {
                        fileWriter.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                    throw t;
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private void addJumboTreeTileset(final IsoSpriteManager isoSpriteManager, final int n, final String s, final int n2, final int n3, final int windType) {
        final int n4 = 2;
        for (int i = 0; i < n3; ++i) {
            for (int j = 0; j < n4; ++j) {
                final String s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
                final int n5 = i * n4 + j;
                final IsoSprite addSprite = isoSpriteManager.AddSprite(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s2, n5), n * 512 * 512 + n2 * 512 + n5);
                if (!IsoWorld.$assertionsDisabled && !GameServer.bServer && (addSprite.CurrentAnim.Frames.isEmpty() || addSprite.CurrentAnim.Frames.get(0).getTexture(IsoDirections.N) == null)) {
                    throw new AssertionError();
                }
                addSprite.setName(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s2, n5));
                addSprite.setType(IsoObjectType.tree);
                addSprite.getProperties().Set("tree", (j == 0) ? "5" : "6");
                addSprite.getProperties().UnSet(IsoFlagType.solid);
                addSprite.getProperties().Set(IsoFlagType.blocksight);
                addSprite.getProperties().CreateKeySet();
                addSprite.moveWithWind = true;
                addSprite.windType = windType;
            }
        }
    }
    
    private void JumboTreeDefinitions(final IsoSpriteManager isoSpriteManager, final int n) {
        this.addJumboTreeTileset(isoSpriteManager, n, "americanholly", 1, 2, 3);
        this.addJumboTreeTileset(isoSpriteManager, n, "americanlinden", 2, 6, 2);
        this.addJumboTreeTileset(isoSpriteManager, n, "canadianhemlock", 3, 2, 3);
        this.addJumboTreeTileset(isoSpriteManager, n, "carolinasilverbell", 4, 6, 1);
        this.addJumboTreeTileset(isoSpriteManager, n, "cockspurhawthorn", 5, 6, 2);
        this.addJumboTreeTileset(isoSpriteManager, n, "dogwood", 6, 6, 2);
        this.addJumboTreeTileset(isoSpriteManager, n, "easternredbud", 7, 6, 2);
        this.addJumboTreeTileset(isoSpriteManager, n, "redmaple", 8, 6, 2);
        this.addJumboTreeTileset(isoSpriteManager, n, "riverbirch", 9, 6, 1);
        this.addJumboTreeTileset(isoSpriteManager, n, "virginiapine", 10, 2, 1);
        this.addJumboTreeTileset(isoSpriteManager, n, "yellowwood", 11, 6, 2);
        final int n2 = 12;
        final int n3 = 0;
        final IsoSprite addSprite = isoSpriteManager.AddSprite(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n3), n * 512 * 512 + n2 * 512 + n3);
        addSprite.setName(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n3));
        addSprite.setType(IsoObjectType.tree);
        addSprite.getProperties().Set("tree", "4");
        addSprite.getProperties().UnSet(IsoFlagType.solid);
        addSprite.getProperties().Set(IsoFlagType.blocksight);
    }
    
    public boolean LoadPlayerForInfo() throws FileNotFoundException, IOException {
        if (GameClient.bClient) {
            return ClientPlayerDB.getInstance().loadNetworkPlayerInfo(1);
        }
        final File fileInCurrentSave = ZomboidFileSystem.instance.getFileInCurrentSave("map_p.bin");
        if (!fileInCurrentSave.exists()) {
            PlayerDB.getInstance().importPlayersFromVehiclesDB();
            return PlayerDB.getInstance().loadLocalPlayerInfo(1);
        }
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(fileInCurrentSave));
        synchronized (SliceY.SliceBufferLock) {
            SliceY.SliceBuffer.clear();
            SliceY.SliceBuffer.limit(bufferedInputStream.read(SliceY.SliceBuffer.array()));
            bufferedInputStream.close();
            final byte value = SliceY.SliceBuffer.get();
            final byte value2 = SliceY.SliceBuffer.get();
            final byte value3 = SliceY.SliceBuffer.get();
            final byte value4 = SliceY.SliceBuffer.get();
            int int1 = -1;
            if (value == 80 && value2 == 76 && value3 == 89 && value4 == 82) {
                int1 = SliceY.SliceBuffer.getInt();
            }
            else {
                SliceY.SliceBuffer.rewind();
            }
            if (int1 >= 69) {
                String s = GameWindow.ReadString(SliceY.SliceBuffer);
                if (GameClient.bClient && int1 < 71) {
                    s = ServerOptions.instance.ServerPlayerID.getValue();
                }
                if (GameClient.bClient && !IsoPlayer.isServerPlayerIDValid(s)) {
                    GameLoadingState.GameLoadingString = Translator.getText("IGUI_MP_ServerPlayerIDMismatch");
                    GameLoadingState.playerWrongIP = true;
                    return false;
                }
            }
            else if (GameClient.bClient && ServerOptions.instance.ServerPlayerID.getValue().isEmpty()) {
                GameLoadingState.GameLoadingString = Translator.getText("IGUI_MP_ServerPlayerIDMissing");
                GameLoadingState.playerWrongIP = true;
                return false;
            }
            IsoWorld.WorldX = SliceY.SliceBuffer.getInt();
            IsoWorld.WorldY = SliceY.SliceBuffer.getInt();
            IsoChunkMap.WorldXA = SliceY.SliceBuffer.getInt();
            IsoChunkMap.WorldYA = SliceY.SliceBuffer.getInt();
            IsoChunkMap.WorldZA = SliceY.SliceBuffer.getInt();
            IsoChunkMap.WorldXA += 300 * IsoWorld.saveoffsetx;
            IsoChunkMap.WorldYA += 300 * IsoWorld.saveoffsety;
            IsoChunkMap.SWorldX[0] = IsoWorld.WorldX;
            IsoChunkMap.SWorldY[0] = IsoWorld.WorldY;
            final int[] sWorldX = IsoChunkMap.SWorldX;
            final int n = 0;
            sWorldX[n] += 30 * IsoWorld.saveoffsetx;
            final int[] sWorldY = IsoChunkMap.SWorldY;
            final int n2 = 0;
            sWorldY[n2] += 30 * IsoWorld.saveoffsety;
        }
        return true;
    }
    
    public void init() throws FileNotFoundException, IOException, WorldDictionaryException {
        if (!Core.bTutorial) {
            this.randomizedBuildingList.add(new RBSafehouse());
            this.randomizedBuildingList.add(new RBBurnt());
            this.randomizedBuildingList.add(new RBOther());
            this.randomizedBuildingList.add(new RBLooted());
            this.randomizedBuildingList.add(new RBBurntFireman());
            this.randomizedBuildingList.add(new RBBurntCorpse());
            this.randomizedBuildingList.add(new RBShopLooted());
            this.randomizedBuildingList.add(new RBKateAndBaldspot());
            this.randomizedBuildingList.add(new RBStripclub());
            this.randomizedBuildingList.add(new RBSchool());
            this.randomizedBuildingList.add(new RBSpiffo());
            this.randomizedBuildingList.add(new RBPizzaWhirled());
            this.randomizedBuildingList.add(new RBPileOCrepe());
            this.randomizedBuildingList.add(new RBCafe());
            this.randomizedBuildingList.add(new RBBar());
            this.randomizedBuildingList.add(new RBOffice());
            this.randomizedBuildingList.add(new RBHairSalon());
            this.randomizedBuildingList.add(new RBClinic());
            this.randomizedVehicleStoryList.add(new RVSUtilityVehicle());
            this.randomizedVehicleStoryList.add(new RVSConstructionSite());
            this.randomizedVehicleStoryList.add(new RVSBurntCar());
            this.randomizedVehicleStoryList.add(new RVSPoliceBlockadeShooting());
            this.randomizedVehicleStoryList.add(new RVSPoliceBlockade());
            this.randomizedVehicleStoryList.add(new RVSCarCrash());
            this.randomizedVehicleStoryList.add(new RVSAmbulanceCrash());
            this.randomizedVehicleStoryList.add(new RVSCarCrashCorpse());
            this.randomizedVehicleStoryList.add(new RVSChangingTire());
            this.randomizedVehicleStoryList.add(new RVSFlippedCrash());
            this.randomizedVehicleStoryList.add(new RVSBanditRoad());
            this.randomizedVehicleStoryList.add(new RVSTrailerCrash());
            this.randomizedVehicleStoryList.add(new RVSCrashHorde());
            this.randomizedZoneList.add(new RZSForestCamp());
            this.randomizedZoneList.add(new RZSForestCampEaten());
            this.randomizedZoneList.add(new RZSBuryingCamp());
            this.randomizedZoneList.add(new RZSBeachParty());
            this.randomizedZoneList.add(new RZSFishingTrip());
            this.randomizedZoneList.add(new RZSBBQParty());
            this.randomizedZoneList.add(new RZSHunterCamp());
            this.randomizedZoneList.add(new RZSSexyTime());
            this.randomizedZoneList.add(new RZSTrapperCamp());
            this.randomizedZoneList.add(new RZSBaseball());
            this.randomizedZoneList.add(new RZSMusicFestStage());
            this.randomizedZoneList.add(new RZSMusicFest());
        }
        zombie.randomizedWorld.randomizedBuilding.RBBasic.getUniqueRDSSpawned().clear();
        if (GameClient.bClient || GameServer.bServer) {
            BodyDamageSync.instance = new BodyDamageSync();
        }
        else {
            BodyDamageSync.instance = null;
        }
        if (GameServer.bServer) {
            LuaManager.GlobalObject.createWorld(Core.GameSaveWorld = GameServer.ServerName);
        }
        IsoWorld.SavedWorldVersion = this.readWorldVersion();
        if (!GameServer.bServer) {
            final File fileInCurrentSave = ZomboidFileSystem.instance.getFileInCurrentSave("map_ver.bin");
            try {
                final FileInputStream in = new FileInputStream(fileInCurrentSave);
                try {
                    final DataInputStream dataInputStream = new DataInputStream(in);
                    try {
                        final int int1 = dataInputStream.readInt();
                        if (int1 >= 25) {
                            final String readString = GameWindow.ReadString(dataInputStream);
                            if (!GameClient.bClient) {
                                Core.GameMap = readString;
                            }
                        }
                        if (int1 >= 74) {
                            this.setDifficulty(GameWindow.ReadString(dataInputStream));
                        }
                        dataInputStream.close();
                    }
                    catch (Throwable t) {
                        try {
                            dataInputStream.close();
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
            catch (FileNotFoundException ex4) {}
        }
        if (!GameServer.bServer || System.getProperty("softreset") == null) {
            this.MetaGrid.CreateStep1();
        }
        LuaEventManager.triggerEvent("OnPreDistributionMerge");
        LuaEventManager.triggerEvent("OnDistributionMerge");
        LuaEventManager.triggerEvent("OnPostDistributionMerge");
        ItemPickerJava.Parse();
        VehiclesDB2.instance.init();
        LuaEventManager.triggerEvent("OnInitWorld");
        if (!GameClient.bClient) {
            SandboxOptions.instance.load();
        }
        ZomboidGlobals.toLua();
        ItemPickerJava.InitSandboxLootSettings();
        this.SurvivorDescriptors.clear();
        IsoSpriteManager.instance.Dispose();
        if (GameClient.bClient && ServerOptions.instance.DoLuaChecksum.getValue()) {
            try {
                NetChecksum.comparer.beginCompare();
                GameLoadingState.GameLoadingString = Translator.getText("IGUI_MP_Checksum");
                long currentTimeMillis;
                final long n = currentTimeMillis = System.currentTimeMillis();
                while (!GameClient.checksumValid) {
                    if (GameWindow.bServerDisconnected) {
                        return;
                    }
                    if (System.currentTimeMillis() > n + 8000L) {
                        DebugLog.log("checksum: timed out waiting for the server to respond");
                        GameClient.connection.forceDisconnect();
                        GameWindow.bServerDisconnected = true;
                        GameWindow.kickReason = Translator.getText("UI_GameLoad_TimedOut");
                        return;
                    }
                    if (System.currentTimeMillis() > currentTimeMillis + 1000L) {
                        DebugLog.log("checksum: waited one second");
                        currentTimeMillis += 1000L;
                    }
                    NetChecksum.comparer.update();
                    if (GameClient.checksumValid) {
                        break;
                    }
                    Thread.sleep(100L);
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        GameLoadingState.GameLoadingString = Translator.getText("IGUI_MP_LoadTileDef");
        final IsoSpriteManager instance = IsoSpriteManager.instance;
        final ZomboidFileSystem instance2 = ZomboidFileSystem.instance;
        this.LoadTileDefinitionsPropertyStrings(instance, instance2.getMediaPath("tiledefinitions.tiles"), 0);
        this.LoadTileDefinitionsPropertyStrings(instance, instance2.getMediaPath("newtiledefinitions.tiles"), 1);
        this.LoadTileDefinitionsPropertyStrings(instance, instance2.getMediaPath("tiledefinitions_erosion.tiles"), 2);
        this.LoadTileDefinitionsPropertyStrings(instance, instance2.getMediaPath("tiledefinitions_apcom.tiles"), 3);
        this.LoadTileDefinitionsPropertyStrings(instance, instance2.getMediaPath("tiledefinitions_overlays.tiles"), 4);
        this.LoadTileDefinitionsPropertyStrings(instance, instance2.getMediaPath("tiledefinitions_noiseworks.patch.tiles"), -1);
        ZomboidFileSystem.instance.loadModTileDefPropertyStrings();
        this.SetCustomPropertyValues();
        this.GenerateTilePropertyLookupTables();
        this.LoadTileDefinitions(instance, instance2.getMediaPath("tiledefinitions.tiles"), 0);
        this.LoadTileDefinitions(instance, instance2.getMediaPath("newtiledefinitions.tiles"), 1);
        this.LoadTileDefinitions(instance, instance2.getMediaPath("tiledefinitions_erosion.tiles"), 2);
        this.LoadTileDefinitions(instance, instance2.getMediaPath("tiledefinitions_apcom.tiles"), 3);
        this.LoadTileDefinitions(instance, instance2.getMediaPath("tiledefinitions_overlays.tiles"), 4);
        this.LoadTileDefinitions(instance, instance2.getMediaPath("tiledefinitions_noiseworks.patch.tiles"), -1);
        this.JumboTreeDefinitions(instance, 5);
        ZomboidFileSystem.instance.loadModTileDefs();
        GameLoadingState.GameLoadingString = "";
        instance.AddSprite("media/ui/missing-tile.png");
        LuaEventManager.triggerEvent("OnLoadedTileDefinitions", instance);
        if (GameServer.bServer && System.getProperty("softreset") != null) {
            WorldConverter.instance.softreset();
        }
        try {
            WeatherFxMask.init();
        }
        catch (Exception ex2) {
            System.out.print(ex2.getStackTrace());
        }
        IsoRegions.init();
        ObjectRenderEffects.init();
        WorldConverter.instance.convert(Core.GameSaveWorld, instance);
        if (GameLoadingState.build23Stop) {
            return;
        }
        SandboxOptions.instance.handleOldZombiesFile2();
        GameTime.getInstance().init();
        GameTime.getInstance().load();
        ZomboidRadio.getInstance().Init(IsoWorld.SavedWorldVersion);
        GlobalModData.instance.init();
        if (GameServer.bServer && Core.getInstance().getPoisonousBerry() == null) {
            Core.getInstance().initPoisonousBerry();
        }
        if (GameServer.bServer && Core.getInstance().getPoisonousMushroom() == null) {
            Core.getInstance().initPoisonousMushroom();
        }
        ErosionGlobals.Boot(instance);
        WorldDictionary.init();
        WorldMarkers.instance.init();
        if (GameServer.bServer) {
            SharedDescriptors.initSharedDescriptors();
        }
        PersistentOutfits.instance.init();
        VirtualZombieManager.instance.init();
        VehicleIDMap.instance.Reset();
        VehicleManager.instance = new VehicleManager();
        GameLoadingState.GameLoadingString = Translator.getText("IGUI_MP_InitMap");
        this.MetaGrid.CreateStep2();
        ClimateManager.getInstance().init(this.MetaGrid);
        SafeHouse.init();
        if (!GameClient.bClient) {
            StashSystem.init();
        }
        LuaEventManager.triggerEvent("OnLoadMapZones");
        this.MetaGrid.load();
        this.MetaGrid.loadZones();
        this.MetaGrid.processZones();
        if (GameServer.bServer) {
            ServerMap.instance.init(this.MetaGrid);
        }
        boolean playerAlive = false;
        if (GameClient.bClient) {
            if (ClientPlayerDB.getInstance().clientLoadNetworkPlayer() && ClientPlayerDB.getInstance().isAliveMainNetworkPlayer()) {
                playerAlive = true;
            }
        }
        else {
            playerAlive = PlayerDBHelper.isPlayerAlive(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getGameModeCacheDir(), Core.GameSaveWorld), 1);
        }
        if (GameServer.bServer) {
            ServerPlayerDB.setAllow(true);
        }
        if (!GameClient.bClient && !GameServer.bServer) {
            PlayerDB.setAllow(true);
        }
        boolean b;
        if (playerAlive) {
            b = true;
            if (!this.LoadPlayerForInfo()) {
                return;
            }
            IsoWorld.WorldX = IsoChunkMap.SWorldX[IsoPlayer.getPlayerIndex()];
            IsoWorld.WorldY = IsoChunkMap.SWorldY[IsoPlayer.getPlayerIndex()];
            final int worldXA = IsoChunkMap.WorldXA;
            final int worldYA = IsoChunkMap.WorldYA;
            final int worldZA = IsoChunkMap.WorldZA;
        }
        else {
            b = false;
            if (GameClient.bClient && !ServerOptions.instance.SpawnPoint.getValue().isEmpty()) {
                final String[] split = ServerOptions.instance.SpawnPoint.getValue().split(",");
                if (split.length == 3) {
                    try {
                        IsoChunkMap.MPWorldXA = new Integer(split[0].trim());
                        IsoChunkMap.MPWorldYA = new Integer(split[1].trim());
                        IsoChunkMap.MPWorldZA = new Integer(split[2].trim());
                    }
                    catch (NumberFormatException ex5) {
                        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, ServerOptions.instance.SpawnPoint.getValue()));
                        IsoChunkMap.MPWorldXA = 0;
                        IsoChunkMap.MPWorldYA = 0;
                        IsoChunkMap.MPWorldZA = 0;
                    }
                }
                else {
                    DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, ServerOptions.instance.SpawnPoint.getValue()));
                }
            }
            if (this.getLuaSpawnCellX() >= 0 && (!GameClient.bClient || (IsoChunkMap.MPWorldXA == 0 && IsoChunkMap.MPWorldYA == 0))) {
                IsoChunkMap.WorldXA = this.getLuaPosX() + 300 * this.getLuaSpawnCellX();
                IsoChunkMap.WorldYA = this.getLuaPosY() + 300 * this.getLuaSpawnCellY();
                IsoChunkMap.WorldZA = this.getLuaPosZ();
                if (GameClient.bClient && ServerOptions.instance.SafehouseAllowRespawn.getValue()) {
                    for (int i = 0; i < SafeHouse.getSafehouseList().size(); ++i) {
                        final SafeHouse safeHouse = SafeHouse.getSafehouseList().get(i);
                        if (safeHouse.getPlayers().contains(GameClient.username) && safeHouse.isRespawnInSafehouse(GameClient.username)) {
                            IsoChunkMap.WorldXA = safeHouse.getX() + safeHouse.getH() / 2;
                            IsoChunkMap.WorldYA = safeHouse.getY() + safeHouse.getW() / 2;
                            IsoChunkMap.WorldZA = 0;
                        }
                    }
                }
                IsoWorld.WorldX = IsoChunkMap.WorldXA / 10;
                IsoWorld.WorldY = IsoChunkMap.WorldYA / 10;
            }
            else if (GameClient.bClient) {
                IsoChunkMap.WorldXA = IsoChunkMap.MPWorldXA;
                IsoChunkMap.WorldYA = IsoChunkMap.MPWorldYA;
                IsoChunkMap.WorldZA = IsoChunkMap.MPWorldZA;
                IsoWorld.WorldX = IsoChunkMap.WorldXA / 10;
                IsoWorld.WorldY = IsoChunkMap.WorldYA / 10;
            }
        }
        Core.getInstance();
        final KahluaTable kahluaTable = (KahluaTable)LuaManager.env.rawget((Object)"selectedDebugScenario");
        if (kahluaTable != null) {
            final KahluaTable kahluaTable2 = (KahluaTable)kahluaTable.rawget((Object)"startLoc");
            final int intValue = ((Double)kahluaTable2.rawget((Object)"x")).intValue();
            final int intValue2 = ((Double)kahluaTable2.rawget((Object)"y")).intValue();
            final int intValue3 = ((Double)kahluaTable2.rawget((Object)"z")).intValue();
            IsoChunkMap.WorldXA = intValue;
            IsoChunkMap.WorldYA = intValue2;
            IsoChunkMap.WorldZA = intValue3;
            IsoWorld.WorldX = IsoChunkMap.WorldXA / 10;
            IsoWorld.WorldY = IsoChunkMap.WorldYA / 10;
        }
        MapCollisionData.instance.init(IsoWorld.instance.getMetaGrid());
        ZombiePopulationManager.instance.init(IsoWorld.instance.getMetaGrid());
        PolygonalMap2.instance.init(IsoWorld.instance.getMetaGrid());
        GlobalObjectLookup.init(IsoWorld.instance.getMetaGrid());
        if (!GameServer.bServer) {
            SpawnPoints.instance.initSinglePlayer();
        }
        WorldStreamer.instance.create();
        this.CurrentCell = CellLoader.LoadCellBinaryChunk(instance, IsoWorld.WorldX, IsoWorld.WorldY);
        ClimateManager.getInstance().postCellLoadSetSnow();
        GameLoadingState.GameLoadingString = Translator.getText("IGUI_MP_LoadWorld");
        MapCollisionData.instance.start();
        MapItem.LoadWorldMap();
        while (WorldStreamer.instance.isBusy()) {
            try {
                Thread.sleep(100L);
            }
            catch (InterruptedException ex3) {
                ex3.printStackTrace();
            }
        }
        final ArrayList<IsoChunk> list = new ArrayList<IsoChunk>();
        list.addAll(IsoChunk.loadGridSquare);
        final Iterator<IsoChunk> iterator = list.iterator();
        while (iterator.hasNext()) {
            this.CurrentCell.ChunkMap[0].setChunkDirect(iterator.next(), false);
        }
        IsoChunk.bDoServerRequests = true;
        if (b && SystemDisabler.doPlayerCreation) {
            this.CurrentCell.LoadPlayer(IsoWorld.SavedWorldVersion);
            if (GameClient.bClient) {
                IsoPlayer.getInstance().setUsername(GameClient.username);
            }
        }
        else {
            if (IsoPlayer.numPlayers == 0) {
                IsoPlayer.numPlayers = 1;
            }
            int worldXA2 = IsoChunkMap.WorldXA;
            int worldYA2 = IsoChunkMap.WorldYA;
            int worldZA2 = IsoChunkMap.WorldZA;
            if (GameClient.bClient && !ServerOptions.instance.SpawnPoint.getValue().isEmpty()) {
                final String[] split2 = ServerOptions.instance.SpawnPoint.getValue().split(",");
                if (split2.length == 3) {
                    try {
                        int intValue4 = new Integer(split2[0].trim());
                        int intValue5 = new Integer(split2[1].trim());
                        int intValue6 = new Integer(split2[2].trim());
                        if (GameClient.bClient && ServerOptions.instance.SafehouseAllowRespawn.getValue()) {
                            for (int j = 0; j < SafeHouse.getSafehouseList().size(); ++j) {
                                final SafeHouse safeHouse2 = SafeHouse.getSafehouseList().get(j);
                                if (safeHouse2.getPlayers().contains(GameClient.username) && safeHouse2.isRespawnInSafehouse(GameClient.username)) {
                                    intValue4 = safeHouse2.getX() + safeHouse2.getH() / 2;
                                    intValue5 = safeHouse2.getY() + safeHouse2.getW() / 2;
                                    intValue6 = 0;
                                }
                            }
                        }
                        if (this.CurrentCell.getGridSquare(intValue4, intValue5, intValue6) != null) {
                            worldXA2 = intValue4;
                            worldYA2 = intValue5;
                            worldZA2 = intValue6;
                        }
                    }
                    catch (NumberFormatException ex6) {
                        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, ServerOptions.instance.SpawnPoint.getValue()));
                    }
                }
                else {
                    DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, ServerOptions.instance.SpawnPoint.getValue()));
                }
            }
            IsoGridSquare isoGridSquare = this.CurrentCell.getGridSquare(worldXA2, worldYA2, worldZA2);
            if (SystemDisabler.doPlayerCreation && !GameServer.bServer) {
                if (isoGridSquare != null && isoGridSquare.isFree(false) && isoGridSquare.getRoom() != null) {
                    final IsoGridSquare isoGridSquare2 = isoGridSquare;
                    isoGridSquare = isoGridSquare.getRoom().getFreeTile();
                    if (isoGridSquare == null) {
                        isoGridSquare = isoGridSquare2;
                    }
                }
                IsoPlayer camCharacter = null;
                if (this.getLuaPlayerDesc() != null) {
                    if (GameClient.bClient && ServerOptions.instance.SafehouseAllowRespawn.getValue()) {
                        isoGridSquare = this.CurrentCell.getGridSquare(IsoChunkMap.WorldXA, IsoChunkMap.WorldYA, IsoChunkMap.WorldZA);
                        if (isoGridSquare != null && isoGridSquare.isFree(false) && isoGridSquare.getRoom() != null) {
                            final IsoGridSquare isoGridSquare3 = isoGridSquare;
                            isoGridSquare = isoGridSquare.getRoom().getFreeTile();
                            if (isoGridSquare == null) {
                                isoGridSquare = isoGridSquare3;
                            }
                        }
                    }
                    if (isoGridSquare == null) {
                        throw new RuntimeException(invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, worldXA2, worldYA2, worldZA2));
                    }
                    WorldSimulation.instance.create();
                    camCharacter = new IsoPlayer(IsoWorld.instance.CurrentCell, this.getLuaPlayerDesc(), isoGridSquare.getX(), isoGridSquare.getY(), isoGridSquare.getZ());
                    if (GameClient.bClient) {
                        camCharacter.setUsername(GameClient.username);
                    }
                    camCharacter.setDir(IsoDirections.SE);
                    camCharacter.sqlID = 1;
                    IsoPlayer.setInstance(IsoPlayer.players[0] = camCharacter);
                    IsoCamera.CamCharacter = camCharacter;
                }
                final IsoPlayer instance3 = IsoPlayer.getInstance();
                instance3.applyTraits(this.getLuaTraits());
                final ProfessionFactory.Profession profession = ProfessionFactory.getProfession(instance3.getDescriptor().getProfession());
                if (profession != null && !profession.getFreeRecipes().isEmpty()) {
                    final Iterator<String> iterator2 = profession.getFreeRecipes().iterator();
                    while (iterator2.hasNext()) {
                        instance3.getKnownRecipes().add(iterator2.next());
                    }
                }
                final Iterator<String> iterator3 = this.getLuaTraits().iterator();
                while (iterator3.hasNext()) {
                    final TraitFactory.Trait trait = TraitFactory.getTrait(iterator3.next());
                    if (trait != null && !trait.getFreeRecipes().isEmpty()) {
                        final Iterator<String> iterator4 = trait.getFreeRecipes().iterator();
                        while (iterator4.hasNext()) {
                            instance3.getKnownRecipes().add(iterator4.next());
                        }
                    }
                }
                if (isoGridSquare != null && isoGridSquare.getRoom() != null) {
                    isoGridSquare.getRoom().def.setExplored(true);
                    isoGridSquare.getRoom().building.setAllExplored(true);
                    if (!GameServer.bServer && !GameClient.bClient) {
                        ZombiePopulationManager.instance.playerSpawnedAt(isoGridSquare.getX(), isoGridSquare.getY(), isoGridSquare.getZ());
                    }
                }
                instance3.createKeyRing();
                if (!GameClient.bClient) {
                    Core.getInstance().initPoisonousBerry();
                    Core.getInstance().initPoisonousMushroom();
                }
                LuaEventManager.triggerEvent("OnNewGame", camCharacter, isoGridSquare);
            }
        }
        if (PlayerDB.isAllow()) {
            PlayerDB.getInstance().m_canSavePlayers = true;
        }
        if (ClientPlayerDB.isAllow()) {
            ClientPlayerDB.getInstance().canSavePlayers = true;
        }
        TutorialManager.instance.ActiveControlZombies = false;
        ReanimatedPlayers.instance.loadReanimatedPlayers();
        if (IsoPlayer.getInstance() != null) {
            if (GameClient.bClient) {
                final int n2 = (int)IsoPlayer.getInstance().getX();
                final int n3 = (int)IsoPlayer.getInstance().getY();
                int k = (int)IsoPlayer.getInstance().getZ();
                while (k > 0) {
                    final IsoGridSquare gridSquare = this.CurrentCell.getGridSquare(n2, n3, k);
                    if (gridSquare != null && gridSquare.TreatAsSolidFloor()) {
                        break;
                    }
                    --k;
                    IsoPlayer.getInstance().setZ((float)k);
                }
            }
            IsoPlayer.getInstance().setCurrent(this.CurrentCell.getGridSquare((int)IsoPlayer.getInstance().getX(), (int)IsoPlayer.getInstance().getY(), (int)IsoPlayer.getInstance().getZ()));
        }
        if (!this.bLoaded) {
            if (!this.CurrentCell.getBuildingList().isEmpty()) {}
            if (!this.bLoaded) {
                this.PopulateCellWithSurvivors();
            }
        }
        if (IsoPlayer.players[0] != null && !this.CurrentCell.getObjectList().contains(IsoPlayer.players[0])) {
            this.CurrentCell.getObjectList().add(IsoPlayer.players[0]);
        }
        LightingThread.instance.create();
        GameLoadingState.GameLoadingString = "";
        initMessaging();
        WorldDictionary.onWorldLoaded();
    }
    
    int readWorldVersion() {
        if (GameServer.bServer) {
            final File fileInCurrentSave = ZomboidFileSystem.instance.getFileInCurrentSave("map_t.bin");
            try {
                final FileInputStream in = new FileInputStream(fileInCurrentSave);
                try {
                    final DataInputStream dataInputStream = new DataInputStream(in);
                    try {
                        final byte byte1 = dataInputStream.readByte();
                        final byte byte2 = dataInputStream.readByte();
                        final byte byte3 = dataInputStream.readByte();
                        final byte byte4 = dataInputStream.readByte();
                        if (byte1 == 71 && byte2 == 77 && byte3 == 84 && byte4 == 77) {
                            final int int1 = dataInputStream.readInt();
                            dataInputStream.close();
                            in.close();
                            return int1;
                        }
                        dataInputStream.close();
                    }
                    catch (Throwable t) {
                        try {
                            dataInputStream.close();
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
            catch (FileNotFoundException ex3) {}
            catch (IOException ex) {
                ExceptionLogger.logException(ex);
            }
            return -1;
        }
        final File fileInCurrentSave2 = ZomboidFileSystem.instance.getFileInCurrentSave("map_ver.bin");
        try {
            final FileInputStream in2 = new FileInputStream(fileInCurrentSave2);
            try {
                final DataInputStream dataInputStream2 = new DataInputStream(in2);
                try {
                    final int int2 = dataInputStream2.readInt();
                    dataInputStream2.close();
                    in2.close();
                    return int2;
                }
                catch (Throwable t3) {
                    try {
                        dataInputStream2.close();
                    }
                    catch (Throwable exception3) {
                        t3.addSuppressed(exception3);
                    }
                    throw t3;
                }
            }
            catch (Throwable t4) {
                try {
                    in2.close();
                }
                catch (Throwable exception4) {
                    t4.addSuppressed(exception4);
                }
                throw t4;
            }
        }
        catch (FileNotFoundException ex4) {}
        catch (IOException ex2) {
            ExceptionLogger.logException(ex2);
        }
        return -1;
    }
    
    public ArrayList<String> getLuaTraits() {
        if (this.luatraits == null) {
            this.luatraits = new ArrayList<String>();
        }
        return this.luatraits;
    }
    
    public void addLuaTrait(final String e) {
        this.getLuaTraits().add(e);
    }
    
    public SurvivorDesc getLuaPlayerDesc() {
        return this.luaDesc;
    }
    
    public void setLuaPlayerDesc(final SurvivorDesc luaDesc) {
        this.luaDesc = luaDesc;
    }
    
    public void KillCell() {
        this.helicopter.deactivate();
        CollisionManager.instance.ContactMap.clear();
        IsoDeadBody.Reset();
        FliesSound.instance.Reset();
        IsoObjectPicker.Instance.Init();
        IsoChunkMap.SharedChunks.clear();
        SoundManager.instance.StopMusic();
        WorldSoundManager.instance.KillCell();
        ZombieGroupManager.instance.Reset();
        this.CurrentCell.Dispose();
        IsoSpriteManager.instance.Dispose();
        this.CurrentCell = null;
        CellLoader.wanderRoom = null;
        IsoLot.Dispose();
        IsoGameCharacter.getSurvivorMap().clear();
        IsoPlayer.getInstance().setCurrent(null);
        IsoPlayer.getInstance().setLast(null);
        IsoPlayer.getInstance().square = null;
        RainManager.reset();
        IsoFireManager.Reset();
        IsoWaterFlow.Reset();
        this.MetaGrid.Dispose();
        IsoWorld.instance = new IsoWorld();
    }
    
    public void setDrawWorld(final boolean bDrawWorld) {
        this.bDrawWorld = bDrawWorld;
    }
    
    public void sceneCullZombies() {
        this.zombieWithModel.clear();
        this.zombieWithoutModel.clear();
        for (int i = 0; i < this.CurrentCell.getZombieList().size(); ++i) {
            final IsoZombie isoZombie = this.CurrentCell.getZombieList().get(i);
            int n = 0;
            for (int j = 0; j < IsoPlayer.numPlayers; ++j) {
                if (IsoPlayer.players[j] != null) {
                    if (isoZombie.current != null) {
                        final float n2 = (float)isoZombie.getScreenProperX(j);
                        final float n3 = (float)isoZombie.getScreenProperY(j);
                        if (n2 >= -100.0f && n3 >= -100.0f && n2 <= Core.getInstance().getOffscreenWidth(j) + 100) {
                            if (n3 <= Core.getInstance().getOffscreenHeight(j) + 100) {
                                if ((isoZombie.getAlpha(j) != 0.0f && isoZombie.legsSprite.def.alpha != 0.0f) || isoZombie.current.isCouldSee(j)) {
                                    n = 1;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            if (n != 0 && isoZombie.isCurrentState(FakeDeadZombieState.instance())) {
                n = 0;
            }
            if (n != 0) {
                this.zombieWithModel.add(isoZombie);
            }
            else {
                this.zombieWithoutModel.add(isoZombie);
            }
        }
        Collections.sort(this.zombieWithModel, IsoWorld.compScoreToPlayer);
        int n4 = 0;
        int n5 = 0;
        int n6 = 0;
        final int n7 = 510;
        PerformanceSettings.AnimationSkip = 0;
        for (int k = 0; k < this.zombieWithModel.size(); ++k) {
            final IsoZombie isoZombie2 = this.zombieWithModel.get(k);
            if (n6 < n7) {
                if (!isoZombie2.Ghost) {
                    ++n5;
                    ++n6;
                    isoZombie2.setSceneCulled(false);
                    if (isoZombie2.legsSprite != null) {
                        if (isoZombie2.legsSprite.modelSlot != null) {
                            if (n5 > PerformanceSettings.ZombieAnimationSpeedFalloffCount) {
                                ++n4;
                                n5 = 0;
                            }
                            if (n6 < PerformanceSettings.ZombieBonusFullspeedFalloff) {
                                isoZombie2.legsSprite.modelSlot.model.setInstanceSkip(n5 / PerformanceSettings.ZombieBonusFullspeedFalloff);
                                n5 = 0;
                            }
                            else {
                                isoZombie2.legsSprite.modelSlot.model.setInstanceSkip(n4 + PerformanceSettings.AnimationSkip);
                            }
                            if (isoZombie2.legsSprite.modelSlot.model.AnimPlayer != null) {
                                if (n6 < PerformanceSettings.numberZombiesBlended) {
                                    isoZombie2.legsSprite.modelSlot.model.AnimPlayer.bDoBlending = (!isoZombie2.isAlphaAndTargetZero(0) || !isoZombie2.isAlphaAndTargetZero(1) || !isoZombie2.isAlphaAndTargetZero(2) || !isoZombie2.isAlphaAndTargetZero(3));
                                }
                                else {
                                    isoZombie2.legsSprite.modelSlot.model.AnimPlayer.bDoBlending = false;
                                }
                            }
                        }
                    }
                }
            }
            else {
                isoZombie2.setSceneCulled(true);
                if (isoZombie2.hasAnimationPlayer()) {
                    isoZombie2.getAnimationPlayer().bDoBlending = false;
                }
            }
        }
        for (int l = 0; l < this.zombieWithoutModel.size(); ++l) {
            final IsoZombie isoZombie3 = this.zombieWithoutModel.get(l);
            if (isoZombie3.hasActiveModel()) {
                isoZombie3.setSceneCulled(true);
            }
            if (isoZombie3.hasAnimationPlayer()) {
                isoZombie3.getAnimationPlayer().bDoBlending = false;
            }
        }
    }
    
    public void render() {
        s_performance.isoWorldRender.invokeAndMeasure(this, IsoWorld::renderInternal);
    }
    
    private void renderInternal() {
        if (!this.bDrawWorld) {
            return;
        }
        if (IsoCamera.CamCharacter == null) {
            return;
        }
        SpriteRenderer.instance.doCoreIntParam(0, IsoCamera.CamCharacter.x);
        SpriteRenderer.instance.doCoreIntParam(1, IsoCamera.CamCharacter.y);
        SpriteRenderer.instance.doCoreIntParam(2, IsoCamera.CamCharacter.z);
        try {
            this.sceneCullZombies();
        }
        catch (Throwable t) {
            ExceptionLogger.logException(t);
        }
        try {
            WeatherFxMask.initMask();
            DeadBodyAtlas.instance.render();
            this.CurrentCell.render();
            this.DrawIsoCursorHelper();
            DeadBodyAtlas.instance.renderDebug();
            PolygonalMap2.instance.render();
            WorldSoundManager.instance.render();
            WorldFlares.debugRender();
            WorldMarkers.instance.debugRender();
            LineDrawer.render();
            WeatherFxMask.renderFxMask(IsoCamera.frameState.playerIndex);
            if (GameClient.bClient) {
                ClientServerMap.render(IsoCamera.frameState.playerIndex);
                PassengerMap.render(IsoCamera.frameState.playerIndex);
            }
            SkyBox.getInstance().render();
        }
        catch (Throwable t2) {
            ExceptionLogger.logException(t2);
        }
    }
    
    private void DrawIsoCursorHelper() {
        if (Core.getInstance().getOffscreenBuffer() != null) {
            return;
        }
        final IsoPlayer instance = IsoPlayer.getInstance();
        if (instance == null || instance.isDead() || !instance.isAiming() || instance.PlayerIndex != 0 || instance.JoypadBind != -1) {
            return;
        }
        if (GameTime.isGamePaused()) {
            return;
        }
        float flashIsoCursorA = 0.05f;
        switch (Core.getInstance().getIsoCursorVisibility()) {
            case 0: {
                return;
            }
            case 1: {
                flashIsoCursorA = 0.05f;
                break;
            }
            case 2: {
                flashIsoCursorA = 0.1f;
                break;
            }
            case 3: {
                flashIsoCursorA = 0.15f;
                break;
            }
            case 4: {
                flashIsoCursorA = 0.3f;
                break;
            }
            case 5: {
                flashIsoCursorA = 0.5f;
                break;
            }
            case 6: {
                flashIsoCursorA = 0.75f;
                break;
            }
        }
        if (Core.getInstance().isFlashIsoCursor()) {
            if (this.flashIsoCursorInc) {
                this.flashIsoCursorA += 0.1f;
                if (this.flashIsoCursorA >= 1.0f) {
                    this.flashIsoCursorInc = false;
                }
            }
            else {
                this.flashIsoCursorA -= 0.1f;
                if (this.flashIsoCursorA <= 0.0f) {
                    this.flashIsoCursorInc = true;
                }
            }
            flashIsoCursorA = this.flashIsoCursorA;
        }
        final Texture sharedTexture = Texture.getSharedTexture("media/ui/isocursor.png");
        final int n = (int)(sharedTexture.getWidth() * Core.TileScale / 2.0f);
        final int n2 = (int)(sharedTexture.getHeight() * Core.TileScale / 2.0f);
        SpriteRenderer.instance.setDoAdditive(true);
        SpriteRenderer.instance.renderi(sharedTexture, Mouse.getX() - n / 2, Mouse.getY() - n2 / 2, n, n2, flashIsoCursorA, flashIsoCursorA, flashIsoCursorA, flashIsoCursorA, null);
        SpriteRenderer.instance.setDoAdditive(false);
    }
    
    public void update() {
        s_performance.isoWorldUpdate.invokeAndMeasure(this, IsoWorld::updateInternal);
    }
    
    private void updateInternal() {
        ++this.m_frameNo;
        try {
            if (GameServer.bServer) {
                VehicleManager.instance.serverUpdate();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        WorldSimulation.instance.update();
        ImprovedFog.update();
        this.helicopter.update();
        final long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - this.emitterUpdateMS >= 30L) {
            this.emitterUpdateMS = currentTimeMillis;
            this.emitterUpdate = true;
        }
        else {
            this.emitterUpdate = false;
        }
        for (int i = 0; i < this.currentEmitters.size(); ++i) {
            final BaseSoundEmitter baseSoundEmitter = this.currentEmitters.get(i);
            if (this.emitterUpdate || baseSoundEmitter.hasSoundsToStart()) {
                baseSoundEmitter.tick();
            }
            if (baseSoundEmitter.isEmpty()) {
                final FMODSoundEmitter fmodSoundEmitter = Type.tryCastTo(baseSoundEmitter, FMODSoundEmitter.class);
                if (fmodSoundEmitter != null) {
                    fmodSoundEmitter.clearParameters();
                }
                this.currentEmitters.remove(i);
                this.freeEmitters.push(baseSoundEmitter);
                final IsoObject isoObject = this.emitterOwners.remove(baseSoundEmitter);
                if (isoObject != null && isoObject.emitter == baseSoundEmitter) {
                    isoObject.emitter = null;
                }
                --i;
            }
        }
        if (!GameClient.bClient && !GameServer.bServer) {
            final IsoMetaCell currentCellData = this.MetaGrid.getCurrentCellData();
            if (currentCellData != null) {
                currentCellData.checkTriggers();
            }
        }
        WorldSoundManager.instance.initFrame();
        ZombieGroupManager.instance.preupdate();
        OnceEvery.update();
        CollisionManager.instance.initUpdate();
        for (int j = 0; j < this.CurrentCell.getBuildingList().size(); ++j) {
            this.CurrentCell.getBuildingList().get(j).update();
        }
        ClimateManager.getInstance().update();
        ObjectRenderEffects.updateStatic();
        this.CurrentCell.update();
        IsoRegions.update();
        HaloTextHelper.update();
        CollisionManager.instance.ResolveContacts();
        for (int k = 0; k < this.AddCoopPlayers.size(); ++k) {
            final AddCoopPlayer addCoopPlayer = this.AddCoopPlayers.get(k);
            addCoopPlayer.update();
            if (addCoopPlayer.isFinished()) {
                this.AddCoopPlayers.remove(k--);
            }
        }
        try {
            if (PlayerDB.isAvailable()) {
                PlayerDB.getInstance().updateMain();
            }
            if (ClientPlayerDB.isAvailable()) {
                ClientPlayerDB.getInstance().updateMain();
            }
            VehiclesDB2.instance.updateMain();
        }
        catch (Exception ex2) {
            ExceptionLogger.logException(ex2);
        }
        IsoWorld.m_animationRecorderDiscard = false;
    }
    
    public IsoCell getCell() {
        return this.CurrentCell;
    }
    
    private void PopulateCellWithSurvivors() {
    }
    
    public int getWorldSquareY() {
        return this.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].WorldY * 10;
    }
    
    public int getWorldSquareX() {
        return this.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].WorldX * 10;
    }
    
    public IsoMetaChunk getMetaChunk(final int n, final int n2) {
        return this.MetaGrid.getChunkData(n, n2);
    }
    
    public IsoMetaChunk getMetaChunkFromTile(final int n, final int n2) {
        return this.MetaGrid.getChunkDataFromTile(n, n2);
    }
    
    public float getGlobalTemperature() {
        return ClimateManager.getInstance().getTemperature();
    }
    
    @Deprecated
    public void setGlobalTemperature(final float n) {
    }
    
    public String getWeather() {
        return this.weather;
    }
    
    public void setWeather(final String weather) {
        this.weather = weather;
    }
    
    public int getLuaSpawnCellX() {
        return this.luaSpawnCellX;
    }
    
    public void setLuaSpawnCellX(final int luaSpawnCellX) {
        this.luaSpawnCellX = luaSpawnCellX;
    }
    
    public int getLuaSpawnCellY() {
        return this.luaSpawnCellY;
    }
    
    public void setLuaSpawnCellY(final int luaSpawnCellY) {
        this.luaSpawnCellY = luaSpawnCellY;
    }
    
    public int getLuaPosX() {
        return this.luaPosX;
    }
    
    public void setLuaPosX(final int luaPosX) {
        this.luaPosX = luaPosX;
    }
    
    public int getLuaPosY() {
        return this.luaPosY;
    }
    
    public void setLuaPosY(final int luaPosY) {
        this.luaPosY = luaPosY;
    }
    
    public int getLuaPosZ() {
        return this.luaPosZ;
    }
    
    public void setLuaPosZ(final int luaPosZ) {
        this.luaPosZ = luaPosZ;
    }
    
    public String getWorld() {
        return Core.GameSaveWorld;
    }
    
    public void transmitWeather() {
        if (!GameServer.bServer) {
            return;
        }
        GameServer.sendWeather();
    }
    
    public boolean isValidSquare(final int n, final int n2, final int n3) {
        return n3 >= 0 && n3 < 8 && this.MetaGrid.isValidSquare(n, n2);
    }
    
    public ArrayList<RandomizedZoneStoryBase> getRandomizedZoneList() {
        return this.randomizedZoneList;
    }
    
    public ArrayList<RandomizedBuildingBase> getRandomizedBuildingList() {
        return this.randomizedBuildingList;
    }
    
    public ArrayList<RandomizedVehicleStoryBase> getRandomizedVehicleStoryList() {
        return this.randomizedVehicleStoryList;
    }
    
    public RandomizedVehicleStoryBase getRandomizedVehicleStoryByName(final String anotherString) {
        for (int i = 0; i < this.randomizedVehicleStoryList.size(); ++i) {
            final RandomizedVehicleStoryBase randomizedVehicleStoryBase = this.randomizedVehicleStoryList.get(i);
            if (randomizedVehicleStoryBase.getName().equalsIgnoreCase(anotherString)) {
                return randomizedVehicleStoryBase;
            }
        }
        return null;
    }
    
    public RandomizedBuildingBase getRBBasic() {
        return this.RBBasic;
    }
    
    public String getDifficulty() {
        return Core.getDifficulty();
    }
    
    public void setDifficulty(final String difficulty) {
        Core.setDifficulty(difficulty);
    }
    
    public static boolean getZombiesDisabled() {
        return IsoWorld.NoZombies || !SystemDisabler.doZombieCreation || SandboxOptions.instance.Zombies.getValue() == 6;
    }
    
    public static boolean getZombiesEnabled() {
        return !getZombiesDisabled();
    }
    
    public ClimateManager getClimateManager() {
        return ClimateManager.getInstance();
    }
    
    public IsoPuddles getPuddlesManager() {
        return IsoPuddles.getInstance();
    }
    
    public static int getWorldVersion() {
        return 186;
    }
    
    public HashMap<String, ArrayList<Double>> getSpawnedZombieZone() {
        return this.spawnedZombieZone;
    }
    
    public int getTimeSinceLastSurvivorInHorde() {
        return this.timeSinceLastSurvivorInHorde;
    }
    
    public void setTimeSinceLastSurvivorInHorde(final int timeSinceLastSurvivorInHorde) {
        this.timeSinceLastSurvivorInHorde = timeSinceLastSurvivorInHorde;
    }
    
    public float getWorldAgeDays() {
        return (float)GameTime.getInstance().getWorldAgeHours() / 24.0f + (SandboxOptions.instance.TimeSinceApo.getValue() - 1) * 30;
    }
    
    public HashMap<String, ArrayList<String>> getAllTiles() {
        return this.allTiles;
    }
    
    public ArrayList<String> getAllTilesName() {
        final ArrayList<String> list = (ArrayList<String>)new ArrayList<Comparable>();
        final Iterator<String> iterator = this.allTiles.keySet().iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        Collections.sort((List<Comparable>)list);
        return list;
    }
    
    public ArrayList<String> getAllTiles(final String key) {
        return this.allTiles.get(key);
    }
    
    static {
        IsoWorld.m_animationRecorderActive = false;
        IsoWorld.m_animationRecorderDiscard = false;
        IsoWorld.instance = new IsoWorld();
        compScoreToPlayer = new CompScoreToPlayer();
        IsoWorld.compDistToPlayer = new CompDistToPlayer();
        IsoWorld.mapPath = "media/";
        IsoWorld.mapUseJar = true;
        PropertyValueMap = new HashMap<String, ArrayList<String>>();
        IsoWorld.WorldX = 0;
        IsoWorld.WorldY = 0;
        IsoWorld.SavedWorldVersion = -1;
        IsoWorld.NoZombies = false;
        IsoWorld.TotalWorldVersion = -1;
    }
    
    private static class CompDistToPlayer implements Comparator<IsoZombie>
    {
        public float px;
        public float py;
        
        @Override
        public int compare(final IsoZombie isoZombie, final IsoZombie isoZombie2) {
            final float distanceManhatten = IsoUtils.DistanceManhatten((float)(int)isoZombie.x, (float)(int)isoZombie.y, this.px, this.py);
            final float distanceManhatten2 = IsoUtils.DistanceManhatten((float)(int)isoZombie2.x, (float)(int)isoZombie2.y, this.px, this.py);
            if (distanceManhatten < distanceManhatten2) {
                return -1;
            }
            if (distanceManhatten > distanceManhatten2) {
                return 1;
            }
            return 0;
        }
    }
    
    private static class CompScoreToPlayer implements Comparator<IsoZombie>
    {
        @Override
        public int compare(final IsoZombie isoZombie, final IsoZombie isoZombie2) {
            final float score = this.getScore(isoZombie);
            final float score2 = this.getScore(isoZombie2);
            if (score < score2) {
                return 1;
            }
            if (score > score2) {
                return -1;
            }
            return 0;
        }
        
        public float getScore(final IsoZombie isoZombie) {
            float max = Float.MIN_VALUE;
            for (int i = 0; i < 4; ++i) {
                final IsoPlayer isoPlayer = IsoPlayer.players[i];
                if (isoPlayer != null) {
                    if (isoPlayer.current != null) {
                        max = Math.max(max, isoPlayer.getZombieRelevenceScore(isoZombie));
                    }
                }
            }
            return max;
        }
    }
    
    public static class MetaCell
    {
        public int x;
        public int y;
        public int zombieCount;
        public IsoDirections zombieMigrateDirection;
        public int[][] from;
        
        public MetaCell() {
            this.from = new int[3][3];
        }
    }
    
    public class Frame
    {
        public ArrayList<Integer> xPos;
        public ArrayList<Integer> yPos;
        public ArrayList<Integer> Type;
        
        public Frame() {
            this.xPos = new ArrayList<Integer>();
            this.yPos = new ArrayList<Integer>();
            this.Type = new ArrayList<Integer>();
            final Iterator<IsoMovingObject> iterator = IsoWorld.instance.CurrentCell.getObjectList().iterator();
            while (iterator != null && iterator.hasNext()) {
                final IsoMovingObject isoMovingObject = iterator.next();
                int i;
                if (isoMovingObject instanceof IsoPlayer) {
                    i = 0;
                }
                else if (isoMovingObject instanceof IsoSurvivor) {
                    i = 1;
                }
                else {
                    if (!(isoMovingObject instanceof IsoZombie)) {
                        continue;
                    }
                    if (((IsoZombie)isoMovingObject).Ghost) {
                        continue;
                    }
                    i = 2;
                }
                this.xPos.add((int)isoMovingObject.getX());
                this.yPos.add((int)isoMovingObject.getY());
                this.Type.add(i);
            }
        }
    }
    
    private static class s_performance
    {
        static final PerformanceProfileProbe isoWorldUpdate;
        static final PerformanceProfileProbe isoWorldRender;
        
        static {
            isoWorldUpdate = new PerformanceProfileProbe("IsoWorld.update");
            isoWorldRender = new PerformanceProfileProbe("IsoWorld.render");
        }
    }
}

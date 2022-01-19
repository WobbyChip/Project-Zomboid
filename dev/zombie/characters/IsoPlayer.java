// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters;

import zombie.core.profiling.PerformanceProfileProbe;
import zombie.characters.WornItems.WornItem;
import zombie.iso.objects.IsoDeadBody;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.core.skinnedmodel.advancedanimation.AnimLayer;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.core.skinnedmodel.visual.BaseVisual;
import zombie.core.logger.LoggerManager;
import java.sql.SQLException;
import zombie.network.ServerWorldDatabase;
import zombie.network.PacketTypes;
import zombie.core.network.ByteBufferWriter;
import zombie.scripting.objects.VehicleScript;
import zombie.audio.GameSound;
import zombie.GameSounds;
import zombie.audio.FMODParameterList;
import zombie.audio.FMODParameter;
import zombie.network.packets.EventPacket;
import zombie.network.BodyDamageSync;
import se.krka.kahlua.vm.KahluaTable;
import zombie.characters.AttachedItems.AttachedItems;
import zombie.inventory.types.Clothing;
import zombie.gameStates.MainScreenState;
import zombie.network.ServerLOS;
import zombie.iso.IsoPhysicsObject;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoCurtain;
import zombie.iso.objects.IsoWindowFrame;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoWindow;
import zombie.iso.IsoObject;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.inventory.types.DrainableComboItem;
import zombie.vehicles.VehicleWindow;
import zombie.vehicles.VehiclePart;
import zombie.network.PassengerMap;
import zombie.vehicles.PolygonalMap2;
import zombie.iso.IsoChunk;
import zombie.network.ServerMap;
import zombie.inventory.InventoryItemFactory;
import java.util.Collections;
import java.util.Comparator;
import zombie.util.list.PZArrayUtil;
import fmod.fmod.FMODSoundEmitter;
import zombie.audio.DummySoundEmitter;
import fmod.fmod.SoundListener;
import fmod.fmod.DummySoundListener;
import zombie.characters.BodyDamage.BodyDamage;
import zombie.characters.Moodles.Moodles;
import zombie.network.packets.hit.AttackVars;
import zombie.iso.IsoGridSquare;
import zombie.core.skinnedmodel.ModelManager;
import zombie.ai.sadisticAIDirector.SleepingEvent;
import zombie.SoundManager;
import zombie.SandboxOptions;
import zombie.ZomboidGlobals;
import java.util.List;
import zombie.util.Type;
import zombie.inventory.types.HandWeapon;
import zombie.ai.states.PathFindState;
import zombie.vehicles.PathFindBehavior2;
import zombie.ui.TutorialManager;
import zombie.iso.weather.ClimateManager;
import zombie.characters.CharacterTimedActions.BaseAction;
import zombie.core.math.PZMath;
import zombie.core.Translator;
import zombie.ai.states.FakeDeadZombieState;
import zombie.ai.states.StaggerBackState;
import zombie.iso.areas.SafeHouse;
import zombie.GameTime;
import zombie.ai.states.ForecastBeatenPlayerState;
import zombie.iso.IsoMetaGrid;
import zombie.core.opengl.Shader;
import zombie.iso.IsoCamera;
import zombie.characters.skills.PerkFactory;
import zombie.characters.BodyDamage.BodyPart;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.iso.IsoUtils;
import zombie.input.Mouse;
import zombie.input.JoypadManager;
import zombie.core.logger.ExceptionLogger;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import zombie.vehicles.VehiclesDB2;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import zombie.iso.SliceY;
import java.util.Iterator;
import zombie.world.WorldDictionary;
import zombie.inventory.types.WeaponType;
import zombie.inventory.InventoryItem;
import zombie.GameWindow;
import java.nio.ByteBuffer;
import zombie.core.skinnedmodel.animation.AnimationPlayer;
import zombie.characters.action.ActionContext;
import zombie.ai.states.PlayerSitOnGroundState;
import zombie.ai.states.BumpedState;
import zombie.ai.states.CollideWithWallState;
import zombie.ai.states.PlayerHitReactionPVPState;
import zombie.ai.states.PlayerHitReactionState;
import zombie.ai.states.FitnessState;
import zombie.ai.states.FishingState;
import zombie.ai.states.SmashWindowState;
import zombie.ai.states.CloseWindowState;
import zombie.ai.states.OpenWindowState;
import zombie.ai.states.PlayerKnockedDown;
import zombie.ai.states.PlayerOnGroundState;
import zombie.ai.states.SwipeStatePlayer;
import zombie.ai.states.IdleState;
import zombie.ai.states.PlayerGetUpState;
import zombie.ai.states.PlayerFallingState;
import zombie.ai.states.PlayerFallDownState;
import zombie.ai.states.PlayerExtState;
import zombie.ai.states.PlayerEmoteState;
import zombie.ai.states.ClimbOverWallState;
import zombie.ai.states.ClimbSheetRopeState;
import zombie.ai.states.ClimbDownSheetRopeState;
import zombie.ai.states.PlayerAimState;
import zombie.ai.states.PlayerStrafeState;
import zombie.ai.states.PlayerActionsState;
import zombie.ui.UIManager;
import zombie.input.GameKeyboard;
import zombie.SystemDisabler;
import zombie.characters.Moodles.MoodleType;
import java.util.Arrays;
import zombie.iso.IsoWorld;
import zombie.network.ServerOptions;
import zombie.savefile.PlayerDB;
import zombie.savefile.ClientPlayerDB;
import java.io.File;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.util.StringUtils;
import zombie.ai.states.ClimbThroughWindowState;
import zombie.ai.State;
import zombie.ai.states.ClimbOverFenceState;
import zombie.ZomboidFileSystem;
import zombie.DebugFileWatcher;
import zombie.characters.action.ActionGroup;
import zombie.debug.DebugOptions;
import java.io.IOException;
import zombie.core.Core;
import zombie.core.Color;
import zombie.core.Rand;
import zombie.network.GameServer;
import zombie.network.GameClient;
import zombie.Lua.LuaEventManager;
import zombie.iso.IsoDirections;
import java.util.Collection;
import zombie.iso.IsoCell;
import zombie.audio.parameters.ParameterShoeType;
import zombie.audio.parameters.ParameterVehicleHitLocation;
import zombie.audio.parameters.ParameterPlayerHealth;
import zombie.audio.parameters.ParameterMeleeHitSurface;
import zombie.audio.parameters.ParameterLocalPlayer;
import zombie.audio.parameters.ParameterFootstepMaterial2;
import zombie.audio.parameters.ParameterFootstepMaterial;
import zombie.audio.parameters.ParameterCharacterMovementSpeed;
import zombie.network.packets.hit.HitInfo;
import zombie.network.ReplayManager;
import java.util.ArrayList;
import zombie.vehicles.BaseVehicle;
import zombie.core.textures.ColorInfo;
import zombie.characters.BodyDamage.Fitness;
import zombie.characters.BodyDamage.Nutrition;
import org.joml.Vector3f;
import zombie.audio.BaseSoundEmitter;
import zombie.iso.IsoMovingObject;
import fmod.fmod.BaseSoundListener;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.PredicatedFileWatcher;
import java.util.HashMap;
import java.util.Stack;
import zombie.iso.Vector2;
import zombie.core.skinnedmodel.visual.IHumanVisual;

public final class IsoPlayer extends IsoLivingCharacter implements IHumanVisual
{
    private String attackType;
    public static String DEATH_MUSIC_NAME;
    private boolean allowSprint;
    private boolean allowRun;
    public static boolean isTestAIMode;
    public static final boolean NoSound = false;
    private static final float TIME_RIGHT_PRESSED_SECONDS = 0.15f;
    public static int assumedPlayer;
    public static int numPlayers;
    public static final short MAX = 4;
    public static final IsoPlayer[] players;
    private static IsoPlayer instance;
    private static final Object instanceLock;
    private static final Vector2 testHitPosition;
    private static int FollowDeadCount;
    private static final Stack<String> StaticTraits;
    private boolean ignoreAutoVault;
    public int remoteSneakLvl;
    public int remoteStrLvl;
    public int remoteFitLvl;
    public boolean canSeeAll;
    public boolean canHearAll;
    public boolean MoodleCantSprint;
    private static final Vector2 tempo;
    private static final Vector2 tempVector2;
    private static final String forwardStr = "Forward";
    private static final String backwardStr = "Backward";
    private static final String leftStr = "Left";
    private static final String rightStr = "Right";
    private static boolean CoopPVP;
    private boolean ignoreContextKey;
    private boolean ignoreInputsForDirection;
    private boolean showMPInfos;
    public boolean spottedByPlayer;
    private HashMap<Integer, Integer> spottedPlayerTimer;
    private float extUpdateCount;
    private static final int s_randomIdleFidgetInterval = 5000;
    public boolean attackStarted;
    private static final PredicatedFileWatcher m_isoPlayerTriggerWatcher;
    private final PredicatedFileWatcher m_setClothingTriggerWatcher;
    private static Vector2 tempVector2_1;
    private static Vector2 tempVector2_2;
    protected final HumanVisual humanVisual;
    protected final ItemVisuals itemVisuals;
    public boolean targetedByZombie;
    public float lastTargeted;
    public float TimeSinceOpenDoor;
    public boolean bRemote;
    public int TimeSinceLastNetData;
    public String accessLevel;
    public String tagPrefix;
    public boolean showTag;
    public boolean factionPvp;
    public short OnlineID;
    public int OnlineChunkGridWidth;
    public boolean bJoypadMovementActive;
    public boolean bJoypadIgnoreAimUntilCentered;
    public boolean bJoypadIgnoreChargingRT;
    protected boolean bJoypadBDown;
    protected boolean bJoypadSprint;
    public boolean mpTorchCone;
    public float mpTorchDist;
    public float mpTorchStrength;
    public int PlayerIndex;
    public int serverPlayerIndex;
    public float useChargeDelta;
    public int JoypadBind;
    public float ContextPanic;
    public float numNearbyBuildingsRooms;
    public boolean isCharging;
    public boolean isChargingLT;
    private boolean bLookingWhileInVehicle;
    private boolean climbOverWallSuccess;
    private boolean climbOverWallStruggle;
    private boolean JustMoved;
    public boolean L3Pressed;
    public float maxWeightDelta;
    public float CurrentSpeed;
    public float MaxSpeed;
    public boolean bDeathFinished;
    public boolean isSpeek;
    public boolean isVoiceMute;
    public final Vector2 playerMoveDir;
    public BaseSoundListener soundListener;
    public String username;
    public boolean dirtyRecalcGridStack;
    public float dirtyRecalcGridStackTime;
    public float runningTime;
    public float timePressedContext;
    public float chargeTime;
    public float useChargeTime;
    public boolean bPressContext;
    public float closestZombie;
    public final Vector2 lastAngle;
    public String SaveFileName;
    public boolean bBannedAttacking;
    public int sqlID;
    protected int ClearSpottedTimer;
    protected float timeSinceLastStab;
    protected Stack<IsoMovingObject> LastSpotted;
    protected boolean bChangeCharacterDebounce;
    protected int followID;
    protected final Stack<IsoGameCharacter> FollowCamStack;
    protected boolean bSeenThisFrame;
    protected boolean bCouldBeSeenThisFrame;
    protected float AsleepTime;
    protected final Stack<IsoMovingObject> spottedList;
    protected int TicksSinceSeenZombie;
    protected boolean Waiting;
    protected IsoSurvivor DragCharacter;
    protected float heartDelay;
    protected float heartDelayMax;
    protected long heartEventInstance;
    protected long worldAmbianceInstance;
    protected String Forname;
    protected String Surname;
    protected int DialogMood;
    protected int ping;
    protected IsoMovingObject DragObject;
    private double lastSeenZombieTime;
    private BaseSoundEmitter testemitter;
    private int checkSafehouse;
    private boolean attackFromBehind;
    private float TimeRightPressed;
    private long aimKeyDownMS;
    private long runKeyDownMS;
    private long sprintKeyDownMS;
    private int hypothermiaCache;
    private int hyperthermiaCache;
    private float ticksSincePressedMovement;
    private boolean flickTorch;
    private float checkNearbyRooms;
    private boolean bUseVehicle;
    private boolean bUsedVehicle;
    private float useVehicleDuration;
    private static final Vector3f tempVector3f;
    private final InputState inputState;
    private boolean isWearingNightVisionGoggles;
    @Deprecated
    private Integer transactionID;
    private float MoveSpeed;
    private int offSetXUI;
    private int offSetYUI;
    private float combatSpeed;
    private double HoursSurvived;
    private boolean bSentDeath;
    private boolean noClip;
    private boolean authorizeMeleeAction;
    private boolean authorizeShoveStomp;
    private boolean blockMovement;
    private Nutrition nutrition;
    private Fitness fitness;
    private boolean forceOverrideAnim;
    private boolean initiateAttack;
    private final ColorInfo tagColor;
    private String displayName;
    private boolean seeNonPvpZone;
    private final HashMap<Long, Long> mechanicsItem;
    private int sleepingPillsTaken;
    private long lastPillsTaken;
    private long heavyBreathInstance;
    private String heavyBreathSoundName;
    private boolean allChatMuted;
    private boolean forceAim;
    private boolean forceRun;
    private boolean forceSprint;
    private boolean bMultiplayer;
    private String SaveFileIP;
    private BaseVehicle vehicle4testCollision;
    private long steamID;
    private final VehicleContainerData vehicleContainerData;
    private boolean isWalking;
    private int footInjuryTimer;
    private boolean bSneakDebounce;
    private float m_turnDelta;
    protected boolean m_isPlayerMoving;
    private float m_walkSpeed;
    private float m_walkInjury;
    private float m_runSpeed;
    private float m_idleSpeed;
    private float m_deltaX;
    private float m_deltaY;
    private float m_windspeed;
    private float m_windForce;
    private float m_IPX;
    private float m_IPY;
    private float pressedRunTimer;
    private boolean pressedRun;
    private boolean m_meleePressed;
    private boolean m_lastAttackWasShove;
    private boolean m_isPerformingAnAction;
    private ArrayList<String> alreadyReadBook;
    public byte bleedingLevel;
    public final NetworkPlayerAI networkAI;
    public ReplayManager replay;
    private boolean pathfindRun;
    private static final MoveVars s_moveVars;
    int atkTimer;
    private static final ArrayList<HitInfo> s_targetsProne;
    private static final ArrayList<HitInfo> s_targetsStanding;
    private boolean bReloadButtonDown;
    private boolean bRackButtonDown;
    private boolean bReloadKeyDown;
    private boolean bRackKeyDown;
    private long AttackAnimThrowTimer;
    String WeaponT;
    private final ParameterCharacterMovementSpeed parameterCharacterMovementSpeed;
    private final ParameterFootstepMaterial parameterFootstepMaterial;
    private final ParameterFootstepMaterial2 parameterFootstepMaterial2;
    private final ParameterLocalPlayer parameterLocalPlayer;
    private final ParameterMeleeHitSurface parameterMeleeHitSurface;
    private final ParameterPlayerHealth parameterPlayerHealth;
    private final ParameterVehicleHitLocation parameterVehicleHitLocation;
    private final ParameterShoeType parameterShoeType;
    
    public IsoPlayer(final IsoCell isoCell) {
        this(isoCell, null, 0, 0, 0);
    }
    
    public IsoPlayer(final IsoCell isoCell, final SurvivorDesc descriptor, final int n, final int n2, final int n3) {
        super(isoCell, (float)n, (float)n2, (float)n3);
        this.attackType = null;
        this.allowSprint = true;
        this.allowRun = true;
        this.ignoreAutoVault = false;
        this.remoteSneakLvl = 0;
        this.remoteStrLvl = 0;
        this.remoteFitLvl = 0;
        this.canSeeAll = false;
        this.canHearAll = false;
        this.MoodleCantSprint = false;
        this.ignoreContextKey = false;
        this.ignoreInputsForDirection = false;
        this.showMPInfos = false;
        this.spottedByPlayer = false;
        this.spottedPlayerTimer = new HashMap<Integer, Integer>();
        this.extUpdateCount = 0.0f;
        this.attackStarted = false;
        this.humanVisual = new HumanVisual(this);
        this.itemVisuals = new ItemVisuals();
        this.targetedByZombie = false;
        this.lastTargeted = 1.0E8f;
        this.TimeSinceLastNetData = 0;
        this.accessLevel = "";
        this.tagPrefix = "";
        this.showTag = true;
        this.factionPvp = false;
        this.OnlineID = 1;
        this.bJoypadMovementActive = true;
        this.bJoypadIgnoreChargingRT = false;
        this.bJoypadBDown = false;
        this.bJoypadSprint = false;
        this.mpTorchCone = false;
        this.mpTorchDist = 0.0f;
        this.mpTorchStrength = 0.0f;
        this.PlayerIndex = 0;
        this.serverPlayerIndex = 1;
        this.useChargeDelta = 0.0f;
        this.JoypadBind = -1;
        this.ContextPanic = 0.0f;
        this.numNearbyBuildingsRooms = 0.0f;
        this.isCharging = false;
        this.isChargingLT = false;
        this.bLookingWhileInVehicle = false;
        this.JustMoved = false;
        this.L3Pressed = false;
        this.maxWeightDelta = 1.0f;
        this.CurrentSpeed = 0.0f;
        this.MaxSpeed = 0.09f;
        this.bDeathFinished = false;
        this.playerMoveDir = new Vector2(0.0f, 0.0f);
        this.username = "Bob";
        this.dirtyRecalcGridStack = true;
        this.dirtyRecalcGridStackTime = 10.0f;
        this.runningTime = 0.0f;
        this.timePressedContext = 0.0f;
        this.chargeTime = 0.0f;
        this.useChargeTime = 0.0f;
        this.bPressContext = false;
        this.closestZombie = 1000000.0f;
        this.lastAngle = new Vector2();
        this.bBannedAttacking = false;
        this.sqlID = -1;
        this.ClearSpottedTimer = -1;
        this.timeSinceLastStab = 0.0f;
        this.LastSpotted = new Stack<IsoMovingObject>();
        this.bChangeCharacterDebounce = false;
        this.followID = 0;
        this.FollowCamStack = new Stack<IsoGameCharacter>();
        this.bSeenThisFrame = false;
        this.bCouldBeSeenThisFrame = false;
        this.AsleepTime = 0.0f;
        this.spottedList = new Stack<IsoMovingObject>();
        this.TicksSinceSeenZombie = 9999999;
        this.Waiting = true;
        this.DragCharacter = null;
        this.heartDelay = 30.0f;
        this.heartDelayMax = 30.0f;
        this.Forname = "Bob";
        this.Surname = "Smith";
        this.DialogMood = 1;
        this.ping = 0;
        this.DragObject = null;
        this.lastSeenZombieTime = 2.0;
        this.checkSafehouse = 200;
        this.attackFromBehind = false;
        this.TimeRightPressed = 0.0f;
        this.aimKeyDownMS = 0L;
        this.runKeyDownMS = 0L;
        this.sprintKeyDownMS = 0L;
        this.hypothermiaCache = -1;
        this.hyperthermiaCache = -1;
        this.ticksSincePressedMovement = 0.0f;
        this.flickTorch = false;
        this.checkNearbyRooms = 0.0f;
        this.bUseVehicle = false;
        this.inputState = new InputState();
        this.isWearingNightVisionGoggles = false;
        this.transactionID = 0;
        this.MoveSpeed = 0.06f;
        this.offSetXUI = 0;
        this.offSetYUI = 0;
        this.combatSpeed = 1.0f;
        this.HoursSurvived = 0.0;
        this.noClip = false;
        this.authorizeMeleeAction = true;
        this.authorizeShoveStomp = true;
        this.blockMovement = false;
        this.forceOverrideAnim = false;
        this.initiateAttack = false;
        this.tagColor = new ColorInfo(1.0f, 1.0f, 1.0f, 1.0f);
        this.displayName = null;
        this.seeNonPvpZone = false;
        this.mechanicsItem = new HashMap<Long, Long>();
        this.sleepingPillsTaken = 0;
        this.lastPillsTaken = 0L;
        this.heavyBreathInstance = 0L;
        this.heavyBreathSoundName = null;
        this.allChatMuted = false;
        this.forceAim = false;
        this.forceRun = false;
        this.forceSprint = false;
        this.vehicle4testCollision = null;
        this.vehicleContainerData = new VehicleContainerData();
        this.isWalking = false;
        this.footInjuryTimer = 0;
        this.m_turnDelta = 0.0f;
        this.m_isPlayerMoving = false;
        this.m_walkSpeed = 0.0f;
        this.m_walkInjury = 0.0f;
        this.m_runSpeed = 0.0f;
        this.m_idleSpeed = 0.0f;
        this.m_deltaX = 0.0f;
        this.m_deltaY = 0.0f;
        this.m_windspeed = 0.0f;
        this.m_windForce = 0.0f;
        this.m_IPX = 0.0f;
        this.m_IPY = 0.0f;
        this.pressedRunTimer = 0.0f;
        this.pressedRun = false;
        this.m_meleePressed = false;
        this.m_lastAttackWasShove = false;
        this.m_isPerformingAnAction = false;
        this.alreadyReadBook = new ArrayList<String>();
        this.bleedingLevel = 0;
        this.replay = null;
        this.pathfindRun = false;
        this.atkTimer = 0;
        this.bReloadButtonDown = false;
        this.bRackButtonDown = false;
        this.bReloadKeyDown = false;
        this.bRackKeyDown = false;
        this.AttackAnimThrowTimer = System.currentTimeMillis();
        this.WeaponT = null;
        this.parameterCharacterMovementSpeed = new ParameterCharacterMovementSpeed(this);
        this.parameterFootstepMaterial = new ParameterFootstepMaterial(this);
        this.parameterFootstepMaterial2 = new ParameterFootstepMaterial2(this);
        this.parameterLocalPlayer = new ParameterLocalPlayer(this);
        this.parameterMeleeHitSurface = new ParameterMeleeHitSurface(this);
        this.parameterPlayerHealth = new ParameterPlayerHealth(this);
        this.parameterVehicleHitLocation = new ParameterVehicleHitLocation();
        this.parameterShoeType = new ParameterShoeType(this);
        this.registerVariableCallbacks();
        this.Traits.addAll(IsoPlayer.StaticTraits);
        IsoPlayer.StaticTraits.clear();
        this.dir = IsoDirections.W;
        this.nutrition = new Nutrition(this);
        this.fitness = new Fitness(this);
        this.initWornItems("Human");
        this.initAttachedItems("Human");
        this.clothingWetness = new ClothingWetness(this);
        if (descriptor != null) {
            this.descriptor = descriptor;
        }
        else {
            this.descriptor = new SurvivorDesc();
        }
        this.setFemale(this.descriptor.isFemale());
        this.Dressup(this.descriptor);
        this.getHumanVisual().copyFrom(this.descriptor.humanVisual);
        this.InitSpriteParts(this.descriptor);
        LuaEventManager.triggerEvent("OnCreateLivingCharacter", this, this.descriptor);
        if (GameClient.bClient || !GameServer.bServer) {}
        this.descriptor.Instance = this;
        this.SpeakColour = new Color(Rand.Next(135) + 120, Rand.Next(135) + 120, Rand.Next(135) + 120, 255);
        if (GameClient.bClient) {
            if (Core.getInstance().getMpTextColor() != null) {
                this.SpeakColour = new Color(Core.getInstance().getMpTextColor().r, Core.getInstance().getMpTextColor().g, Core.getInstance().getMpTextColor().b, 1.0f);
            }
            else {
                Core.getInstance().setMpTextColor(new ColorInfo(this.SpeakColour.r, this.SpeakColour.g, this.SpeakColour.b, 1.0f));
                try {
                    Core.getInstance().saveOptions();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        if (Core.GameMode.equals("LastStand")) {
            this.Traits.add("Strong");
        }
        if (this.Traits.Strong.isSet()) {
            this.maxWeightDelta = 1.5f;
        }
        if (this.Traits.Weak.isSet()) {
            this.maxWeightDelta = 0.75f;
        }
        if (this.Traits.Feeble.isSet()) {
            this.maxWeightDelta = 0.9f;
        }
        if (this.Traits.Stout.isSet()) {
            this.maxWeightDelta = 1.25f;
        }
        this.descriptor.temper = 5.0f;
        if (this.Traits.ShortTemper.isSet()) {
            this.descriptor.temper = 7.5f;
        }
        else if (this.Traits.Patient.isSet()) {
            this.descriptor.temper = 2.5f;
        }
        if (this.Traits.Injured.isSet()) {
            this.getBodyDamage().AddRandomDamage();
        }
        this.bMultiplayer = (GameServer.bServer || GameClient.bClient);
        this.vehicle4testCollision = null;
        if (Core.bDebug && DebugOptions.instance.CheatPlayerStartInvisible.getValue()) {
            this.setGhostMode(true);
            this.setGodMod(true);
        }
        this.actionContext.setGroup(ActionGroup.getActionGroup("player"));
        this.initializeStates();
        DebugFileWatcher.instance.add(IsoPlayer.m_isoPlayerTriggerWatcher);
        this.m_setClothingTriggerWatcher = new PredicatedFileWatcher(ZomboidFileSystem.instance.getMessagingDirSub("Trigger_SetClothing.xml"), (Class<T>)TriggerXmlFile.class, (PredicatedFileWatcher.IPredicatedDataPacketFileWatcherCallback<T>)this::onTrigger_setClothingToXmlTriggerFile);
        this.networkAI = new NetworkPlayerAI(this);
        this.initFMODParameters();
    }
    
    public void setOnlineID(final short onlineID) {
        this.OnlineID = onlineID;
    }
    
    private void registerVariableCallbacks() {
        this.setVariable("CombatSpeed", () -> this.combatSpeed, n -> this.combatSpeed = n);
        this.setVariable("TurnDelta", () -> this.m_turnDelta, n2 -> this.m_turnDelta = n2);
        this.setVariable("sneaking", this::isSneaking, this::setSneaking);
        this.setVariable("initiateAttack", () -> this.initiateAttack, this::setInitiateAttack);
        this.setVariable("isMoving", this::isPlayerMoving);
        this.setVariable("isRunning", this::isRunning, this::setRunning);
        this.setVariable("isSprinting", this::isSprinting, this::setSprinting);
        this.setVariable("run", this::isRunning, this::setRunning);
        this.setVariable("sprint", this::isSprinting, this::setSprinting);
        this.setVariable("isStrafing", this::isStrafing);
        this.setVariable("WalkSpeed", () -> this.m_walkSpeed, n3 -> this.m_walkSpeed = n3);
        this.setVariable("WalkInjury", () -> this.m_walkInjury, n4 -> this.m_walkInjury = n4);
        this.setVariable("RunSpeed", () -> this.m_runSpeed, n5 -> this.m_runSpeed = n5);
        this.setVariable("IdleSpeed", () -> this.m_idleSpeed, n6 -> this.m_idleSpeed = n6);
        this.setVariable("DeltaX", () -> this.m_deltaX, n7 -> this.m_deltaX = n7);
        this.setVariable("DeltaY", () -> this.m_deltaY, n8 -> this.m_deltaY = n8);
        this.setVariable("Windspeed", () -> this.m_windspeed, n9 -> this.m_windspeed = n9);
        this.setVariable("WindForce", () -> this.m_windForce, n10 -> this.m_windForce = n10);
        this.setVariable("IPX", () -> this.m_IPX, n11 -> this.m_IPX = n11);
        this.setVariable("IPY", () -> this.m_IPY, n12 -> this.m_IPY = n12);
        this.setVariable("attacktype", () -> this.attackType);
        this.setVariable("aim", this::isAiming);
        this.setVariable("bdead", () -> ((!GameClient.bClient || this.bSentDeath) && this.isDead()) || (GameClient.bClient && this.bRemote && this.isDead()));
        this.setVariable("bdoshove", () -> this.bDoShove);
        this.setVariable("bfalling", () -> this.z > 0.0f && this.fallTime > 2.0f);
        this.setVariable("baimatfloor", this::isAimAtFloor);
        this.setVariable("attackfrombehind", () -> this.attackFromBehind);
        this.setVariable("bundervehicle", this::isUnderVehicle);
        this.setVariable("reanimatetimer", this::getReanimateTimer);
        this.setVariable("isattacking", this::isAttacking);
        this.setVariable("beensprintingfor", this::getBeenSprintingFor);
        this.setVariable("bannedAttacking", () -> this.bBannedAttacking);
        this.setVariable("meleePressed", () -> this.m_meleePressed);
        this.setVariable("AttackAnim", this::isAttackAnim, this::setAttackAnim);
        this.setVariable("Weapon", this::getWeaponType, this::setWeaponType);
        this.setVariable("BumpFall", false);
        this.setVariable("bClient", () -> GameClient.bClient);
        this.setVariable("IsPerformingAnAction", this::isPerformingAnAction, this::setPerformingAnAction);
    }
    
    @Override
    public Vector2 getDeferredMovement(final Vector2 vector2) {
        super.getDeferredMovement(vector2);
        if (DebugOptions.instance.CheatPlayerInvisibleSprint.getValue() && this.isGhostMode() && (this.IsRunning() || this.isSprinting()) && !this.isCurrentState(ClimbOverFenceState.instance()) && !this.isCurrentState(ClimbThroughWindowState.instance())) {
            if (this.getPath2() == null && !this.pressedMovement(false)) {
                return vector2.set(0.0f, 0.0f);
            }
            if (this.getCurrentBuilding() != null) {
                vector2.scale(2.5f);
                return vector2;
            }
            vector2.scale(7.5f);
        }
        return vector2;
    }
    
    @Override
    public float getTurnDelta() {
        if (DebugOptions.instance.CheatPlayerInvisibleSprint.getValue() && this.isGhostMode() && (this.isRunning() || this.isSprinting())) {
            return 10.0f;
        }
        return super.getTurnDelta();
    }
    
    public void setPerformingAnAction(final boolean isPerformingAnAction) {
        this.m_isPerformingAnAction = isPerformingAnAction;
    }
    
    public boolean isPerformingAnAction() {
        return this.m_isPerformingAnAction;
    }
    
    @Override
    public boolean isAttacking() {
        return !StringUtils.isNullOrWhitespace(this.getAttackType());
    }
    
    @Override
    public boolean shouldBeTurning() {
        if (this.isPerformingAnAction()) {}
        return super.shouldBeTurning();
    }
    
    public static void invokeOnPlayerInstance(final Runnable runnable) {
        synchronized (IsoPlayer.instanceLock) {
            if (IsoPlayer.instance != null) {
                runnable.run();
            }
        }
    }
    
    public static IsoPlayer getInstance() {
        return IsoPlayer.instance;
    }
    
    public static void setInstance(final IsoPlayer instance) {
        synchronized (IsoPlayer.instanceLock) {
            IsoPlayer.instance = instance;
        }
    }
    
    public static boolean hasInstance() {
        return IsoPlayer.instance != null;
    }
    
    private static void onTrigger_ResetIsoPlayerModel(final String s) {
        if (IsoPlayer.instance != null) {
            DebugLog.log(DebugType.General, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
            IsoPlayer.instance.resetModel();
        }
        else {
            DebugLog.log(DebugType.General, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        }
    }
    
    public static Stack<String> getStaticTraits() {
        return IsoPlayer.StaticTraits;
    }
    
    public static int getFollowDeadCount() {
        return IsoPlayer.FollowDeadCount;
    }
    
    public static void setFollowDeadCount(final int followDeadCount) {
        IsoPlayer.FollowDeadCount = followDeadCount;
    }
    
    public static ArrayList<String> getAllFileNames() {
        final ArrayList<String> list = new ArrayList<String>();
        final String s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getGameModeCacheDir(), File.separator, Core.GameSaveWorld);
        for (int i = 1; i < 100; ++i) {
            if (new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;I)Ljava/lang/String;, s, File.separator, i)).exists()) {
                list.add(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, i));
            }
        }
        return list;
    }
    
    public static String getUniqueFileName() {
        int n = 0;
        final String s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getGameModeCacheDir(), File.separator, Core.GameSaveWorld);
        for (int i = 1; i < 100; ++i) {
            if (new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;I)Ljava/lang/String;, s, File.separator, i)).exists()) {
                n = i;
            }
        }
        ++n;
        return ZomboidFileSystem.instance.getFileNameInCurrentSave(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n));
    }
    
    public static ArrayList<IsoPlayer> getAllSavedPlayers() {
        ArrayList<IsoPlayer> list;
        if (GameClient.bClient) {
            list = ClientPlayerDB.getInstance().getAllNetworkPlayers();
        }
        else {
            list = PlayerDB.getInstance().getAllLocalPlayers();
        }
        for (int i = list.size() - 1; i >= 0; --i) {
            if (list.get(i).isDead()) {
                list.remove(i);
            }
        }
        return list;
    }
    
    public static boolean isServerPlayerIDValid(final String anObject) {
        if (GameClient.bClient) {
            final String value = ServerOptions.instance.ServerPlayerID.getValue();
            return value == null || value.isEmpty() || value.equals(anObject);
        }
        return true;
    }
    
    public static int getPlayerIndex() {
        if (IsoPlayer.instance == null) {
            return IsoPlayer.assumedPlayer;
        }
        return IsoPlayer.instance.PlayerIndex;
    }
    
    public static boolean allPlayersDead() {
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            if (IsoPlayer.players[i] != null && !IsoPlayer.players[i].isDead()) {
                return false;
            }
        }
        return IsoWorld.instance == null || IsoWorld.instance.AddCoopPlayers.isEmpty();
    }
    
    public static ArrayList<IsoPlayer> getPlayers() {
        return new ArrayList<IsoPlayer>(Arrays.asList(IsoPlayer.players));
    }
    
    public static boolean allPlayersAsleep() {
        int n = 0;
        int n2 = 0;
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            if (IsoPlayer.players[i] != null && !IsoPlayer.players[i].isDead()) {
                ++n;
                if (IsoPlayer.players[i] != null && IsoPlayer.players[i].isAsleep()) {
                    ++n2;
                }
            }
        }
        return n > 0 && n == n2;
    }
    
    public static boolean getCoopPVP() {
        return IsoPlayer.CoopPVP;
    }
    
    public static void setCoopPVP(final boolean coopPVP) {
        IsoPlayer.CoopPVP = coopPVP;
    }
    
    public void TestZombieSpotPlayer(final IsoMovingObject isoMovingObject) {
        if (GameServer.bServer && isoMovingObject instanceof IsoZombie && ((IsoZombie)isoMovingObject).target != this && ((IsoZombie)isoMovingObject).isLeadAggro(this)) {
            GameServer.updateZombieControl((IsoZombie)isoMovingObject, (short)1, this.OnlineID);
            return;
        }
        isoMovingObject.spotted(this, false);
        if (isoMovingObject instanceof IsoZombie) {
            final float distTo = isoMovingObject.DistTo(this);
            if (distTo < this.closestZombie && !isoMovingObject.isOnFloor()) {
                this.closestZombie = distTo;
            }
        }
    }
    
    public float getPathSpeed() {
        float n = this.getMoveSpeed() * 0.9f;
        switch (this.Moodles.getMoodleLevel(MoodleType.Endurance)) {
            case 1: {
                n *= 0.95f;
                break;
            }
            case 2: {
                n *= 0.9f;
                break;
            }
            case 3: {
                n *= 0.8f;
                break;
            }
            case 4: {
                n *= 0.6f;
                break;
            }
        }
        if (this.stats.enduranceRecharging) {
            n *= 0.85f;
        }
        if (this.getMoodles().getMoodleLevel(MoodleType.HeavyLoad) > 0) {
            n *= 0.65f + 0.35f * (1.0f - (Math.min(2.0f, this.getInventory().getCapacityWeight() / this.getMaxWeight()) - 1.0f));
        }
        return n;
    }
    
    public boolean isGhostMode() {
        return this.isInvisible();
    }
    
    public void setGhostMode(final boolean invisible) {
        this.setInvisible(invisible);
    }
    
    public boolean isSeeEveryone() {
        return Core.bDebug && DebugOptions.instance.CheatPlayerSeeEveryone.getValue();
    }
    
    public boolean zombiesSwitchOwnershipEachUpdate() {
        return SystemDisabler.zombiesSwitchOwnershipEachUpdate;
    }
    
    public Vector2 getPlayerMoveDir() {
        return this.playerMoveDir;
    }
    
    public void setPlayerMoveDir(final Vector2 vector2) {
        this.playerMoveDir.set(vector2);
    }
    
    @Override
    public void MoveUnmodded(final Vector2 vector2) {
        if (this.getSlowFactor() > 0.0f) {
            vector2.x *= 1.0f - this.getSlowFactor();
            vector2.y *= 1.0f - this.getSlowFactor();
        }
        super.MoveUnmodded(vector2);
    }
    
    public void nullifyAiming() {
        this.setIsAiming(this.isCharging = false);
    }
    
    public boolean isAimKeyDown() {
        if (this.PlayerIndex != 0) {
            return false;
        }
        final int key = Core.getInstance().getKey("Aim");
        return GameKeyboard.isKeyDown(key) && ((key != 29 && key != 157) || !UIManager.isMouseOverInventory());
    }
    
    private void initializeStates() {
        final HashMap<String, State> stateUpdateLookup = this.getStateUpdateLookup();
        stateUpdateLookup.clear();
        if (this.getVehicle() == null) {
            stateUpdateLookup.put("actions", PlayerActionsState.instance());
            stateUpdateLookup.put("aim", PlayerAimState.instance());
            stateUpdateLookup.put("climbfence", ClimbOverFenceState.instance());
            stateUpdateLookup.put("climbdownrope", ClimbDownSheetRopeState.instance());
            stateUpdateLookup.put("climbrope", ClimbSheetRopeState.instance());
            stateUpdateLookup.put("climbwall", ClimbOverWallState.instance());
            stateUpdateLookup.put("climbwindow", ClimbThroughWindowState.instance());
            stateUpdateLookup.put("emote", PlayerEmoteState.instance());
            stateUpdateLookup.put("ext", PlayerExtState.instance());
            stateUpdateLookup.put("sitext", PlayerExtState.instance());
            stateUpdateLookup.put("falldown", PlayerFallDownState.instance());
            stateUpdateLookup.put("falling", PlayerFallingState.instance());
            stateUpdateLookup.put("getup", PlayerGetUpState.instance());
            stateUpdateLookup.put("idle", IdleState.instance());
            stateUpdateLookup.put("melee", SwipeStatePlayer.instance());
            stateUpdateLookup.put("shove", SwipeStatePlayer.instance());
            stateUpdateLookup.put("ranged", SwipeStatePlayer.instance());
            stateUpdateLookup.put("onground", PlayerOnGroundState.instance());
            stateUpdateLookup.put("knockeddown", PlayerKnockedDown.instance());
            stateUpdateLookup.put("openwindow", OpenWindowState.instance());
            stateUpdateLookup.put("closewindow", CloseWindowState.instance());
            stateUpdateLookup.put("smashwindow", SmashWindowState.instance());
            stateUpdateLookup.put("fishing", FishingState.instance());
            stateUpdateLookup.put("fitness", FitnessState.instance());
            stateUpdateLookup.put("hitreaction", PlayerHitReactionState.instance());
            stateUpdateLookup.put("hitreactionpvp", PlayerHitReactionPVPState.instance());
            stateUpdateLookup.put("hitreaction-hit", PlayerHitReactionPVPState.instance());
            stateUpdateLookup.put("collide", CollideWithWallState.instance());
            stateUpdateLookup.put("bumped", BumpedState.instance());
            stateUpdateLookup.put("bumped-bump", BumpedState.instance());
            stateUpdateLookup.put("sitonground", PlayerSitOnGroundState.instance());
            stateUpdateLookup.put("strafe", PlayerStrafeState.instance());
        }
        else {
            stateUpdateLookup.put("aim", PlayerAimState.instance());
            stateUpdateLookup.put("idle", IdleState.instance());
            stateUpdateLookup.put("melee", SwipeStatePlayer.instance());
            stateUpdateLookup.put("shove", SwipeStatePlayer.instance());
            stateUpdateLookup.put("ranged", SwipeStatePlayer.instance());
        }
    }
    
    @Override
    public ActionContext getActionContext() {
        return this.actionContext;
    }
    
    @Override
    protected void onAnimPlayerCreated(final AnimationPlayer animationPlayer) {
        super.onAnimPlayerCreated(animationPlayer);
        animationPlayer.addBoneReparent("Bip01_L_Thigh", "Bip01");
        animationPlayer.addBoneReparent("Bip01_R_Thigh", "Bip01");
        animationPlayer.addBoneReparent("Bip01_L_Clavicle", "Bip01_Spine1");
        animationPlayer.addBoneReparent("Bip01_R_Clavicle", "Bip01_Spine1");
        animationPlayer.addBoneReparent("Bip01_Prop1", "Bip01_R_Hand");
        animationPlayer.addBoneReparent("Bip01_Prop2", "Bip01_L_Hand");
    }
    
    @Override
    public String GetAnimSetName() {
        return (this.getVehicle() == null) ? "player" : "player-vehicle";
    }
    
    public boolean IsInMeleeAttack() {
        return this.isCurrentState(SwipeStatePlayer.instance());
    }
    
    @Override
    public void load(final ByteBuffer byteBuffer, final int n, final boolean b) throws IOException {
        byteBuffer.get();
        byteBuffer.get();
        super.load(byteBuffer, n, b);
        this.setHoursSurvived(byteBuffer.getDouble());
        final SurvivorDesc descriptor = this.descriptor;
        this.setFemale(descriptor.isFemale());
        this.InitSpriteParts(descriptor);
        this.SpeakColour = new Color(Rand.Next(135) + 120, Rand.Next(135) + 120, Rand.Next(135) + 120, 255);
        if (GameClient.bClient) {
            if (Core.getInstance().getMpTextColor() != null) {
                this.SpeakColour = new Color(Core.getInstance().getMpTextColor().r, Core.getInstance().getMpTextColor().g, Core.getInstance().getMpTextColor().b, 1.0f);
            }
            else {
                Core.getInstance().setMpTextColor(new ColorInfo(this.SpeakColour.r, this.SpeakColour.g, this.SpeakColour.b, 1.0f));
                try {
                    Core.getInstance().saveOptions();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        this.setZombieKills(byteBuffer.getInt());
        final ArrayList<InventoryItem> savedInventoryItems = this.savedInventoryItems;
        for (byte value = byteBuffer.get(), b2 = 0; b2 < value; ++b2) {
            final String readString = GameWindow.ReadString(byteBuffer);
            final short short1 = byteBuffer.getShort();
            if (short1 >= 0 && short1 < savedInventoryItems.size() && this.wornItems.getBodyLocationGroup().getLocation(readString) != null) {
                this.wornItems.setItem(readString, savedInventoryItems.get(short1));
            }
        }
        final short short2 = byteBuffer.getShort();
        if (short2 >= 0 && short2 < savedInventoryItems.size()) {
            this.leftHandItem = savedInventoryItems.get(short2);
        }
        final short short3 = byteBuffer.getShort();
        if (short3 >= 0 && short3 < savedInventoryItems.size()) {
            this.rightHandItem = savedInventoryItems.get(short3);
        }
        this.setVariable("Weapon", WeaponType.getWeaponType(this).type);
        this.setSurvivorKills(byteBuffer.getInt());
        this.initSpritePartsEmpty();
        this.nutrition.load(byteBuffer);
        this.setAllChatMuted(byteBuffer.get() == 1);
        this.tagPrefix = GameWindow.ReadString(byteBuffer);
        this.setTagColor(new ColorInfo(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat(), 1.0f));
        this.setDisplayName(GameWindow.ReadString(byteBuffer));
        this.showTag = (byteBuffer.get() == 1);
        this.factionPvp = (byteBuffer.get() == 1);
        if (n >= 176) {
            this.noClip = (byteBuffer.get() == 1);
        }
        if (byteBuffer.get() == 1) {
            this.savedVehicleX = byteBuffer.getFloat();
            this.savedVehicleY = byteBuffer.getFloat();
            this.savedVehicleSeat = byteBuffer.get();
            this.savedVehicleRunning = (byteBuffer.get() == 1);
            this.z = 0.0f;
        }
        for (int int1 = byteBuffer.getInt(), i = 0; i < int1; ++i) {
            this.mechanicsItem.put(byteBuffer.getLong(), byteBuffer.getLong());
        }
        this.fitness.load(byteBuffer, n);
        if (n >= 184) {
            for (short short4 = byteBuffer.getShort(), n2 = 0; n2 < short4; ++n2) {
                final String itemTypeFromID = WorldDictionary.getItemTypeFromID(byteBuffer.getShort());
                if (itemTypeFromID != null) {
                    this.alreadyReadBook.add(itemTypeFromID);
                }
            }
        }
        else if (n >= 182) {
            for (int int2 = byteBuffer.getInt(), j = 0; j < int2; ++j) {
                this.alreadyReadBook.add(GameWindow.ReadString(byteBuffer));
            }
        }
    }
    
    @Override
    public void save(final ByteBuffer byteBuffer, final boolean b) throws IOException {
        final IsoPlayer instance = IsoPlayer.instance;
        IsoPlayer.instance = this;
        try {
            super.save(byteBuffer, b);
        }
        finally {
            IsoPlayer.instance = instance;
        }
        byteBuffer.putDouble(this.getHoursSurvived());
        byteBuffer.putInt(this.getZombieKills());
        if (this.wornItems.size() > 127) {
            throw new RuntimeException("too many worn items");
        }
        byteBuffer.put((byte)this.wornItems.size());
        this.wornItems.forEach(wornItem -> {
            GameWindow.WriteString(byteBuffer, wornItem.getLocation());
            byteBuffer.putShort((short)this.savedInventoryItems.indexOf(wornItem.getItem()));
            return;
        });
        byteBuffer.putShort((short)this.savedInventoryItems.indexOf(this.getPrimaryHandItem()));
        byteBuffer.putShort((short)this.savedInventoryItems.indexOf(this.getSecondaryHandItem()));
        byteBuffer.putInt(this.getSurvivorKills());
        this.nutrition.save(byteBuffer);
        byteBuffer.put((byte)(this.isAllChatMuted() ? 1 : 0));
        GameWindow.WriteString(byteBuffer, this.tagPrefix);
        byteBuffer.putFloat(this.getTagColor().r);
        byteBuffer.putFloat(this.getTagColor().g);
        byteBuffer.putFloat(this.getTagColor().b);
        GameWindow.WriteString(byteBuffer, this.displayName);
        byteBuffer.put((byte)(this.showTag ? 1 : 0));
        byteBuffer.put((byte)(this.factionPvp ? 1 : 0));
        byteBuffer.put((byte)(this.isNoClip() ? 1 : 0));
        if (this.vehicle != null) {
            byteBuffer.put((byte)1);
            byteBuffer.putFloat(this.vehicle.x);
            byteBuffer.putFloat(this.vehicle.y);
            byteBuffer.put((byte)this.vehicle.getSeat(this));
            byteBuffer.put((byte)(this.vehicle.isEngineRunning() ? 1 : 0));
        }
        else {
            byteBuffer.put((byte)0);
        }
        byteBuffer.putInt(this.mechanicsItem.size());
        for (final Long key : this.mechanicsItem.keySet()) {
            byteBuffer.putLong(key);
            byteBuffer.putLong(this.mechanicsItem.get(key));
        }
        this.fitness.save(byteBuffer);
        byteBuffer.putShort((short)this.alreadyReadBook.size());
        for (int i = 0; i < this.alreadyReadBook.size(); ++i) {
            byteBuffer.putShort(WorldDictionary.getItemRegistryID(this.alreadyReadBook.get(i)));
        }
    }
    
    public void save() throws IOException {
        synchronized (SliceY.SliceBufferLock) {
            final ByteBuffer sliceBuffer = SliceY.SliceBuffer;
            sliceBuffer.clear();
            sliceBuffer.put((byte)80);
            sliceBuffer.put((byte)76);
            sliceBuffer.put((byte)89);
            sliceBuffer.put((byte)82);
            sliceBuffer.putInt(186);
            GameWindow.WriteString(sliceBuffer, this.bMultiplayer ? ServerOptions.instance.ServerPlayerID.getValue() : "");
            sliceBuffer.putInt((int)(this.x / 10.0f));
            sliceBuffer.putInt((int)(this.y / 10.0f));
            sliceBuffer.putInt((int)this.x);
            sliceBuffer.putInt((int)this.y);
            sliceBuffer.putInt((int)this.z);
            this.save(sliceBuffer);
            final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getGameModeCacheDir(), Core.GameSaveWorld, File.separator));
            if (!Core.getInstance().isNoSave()) {
                final FileOutputStream out = new FileOutputStream(file);
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
            if (this.getVehicle() != null && !GameClient.bClient) {
                VehiclesDB2.instance.updateVehicleAndTrailer(this.getVehicle());
            }
        }
    }
    
    public void save(final String s) throws IOException {
        this.SaveFileName = s;
        synchronized (SliceY.SliceBufferLock) {
            SliceY.SliceBuffer.clear();
            SliceY.SliceBuffer.putInt(186);
            GameWindow.WriteString(SliceY.SliceBuffer, this.bMultiplayer ? ServerOptions.instance.ServerPlayerID.getValue() : "");
            this.save(SliceY.SliceBuffer);
            final FileOutputStream out = new FileOutputStream(new File(s).getAbsoluteFile());
            try {
                final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(out);
                try {
                    bufferedOutputStream.write(SliceY.SliceBuffer.array(), 0, SliceY.SliceBuffer.position());
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
    }
    
    public void load(final String s) throws IOException {
        final File absoluteFile = new File(s).getAbsoluteFile();
        if (!absoluteFile.exists()) {
            return;
        }
        this.SaveFileName = s;
        final FileInputStream in = new FileInputStream(absoluteFile);
        try {
            final BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
            try {
                synchronized (SliceY.SliceBufferLock) {
                    SliceY.SliceBuffer.clear();
                    SliceY.SliceBuffer.limit(bufferedInputStream.read(SliceY.SliceBuffer.array()));
                    final int int1 = SliceY.SliceBuffer.getInt();
                    if (int1 >= 69) {
                        this.SaveFileIP = GameWindow.ReadStringUTF(SliceY.SliceBuffer);
                        if (int1 < 71) {
                            this.SaveFileIP = ServerOptions.instance.ServerPlayerID.getValue();
                        }
                    }
                    else if (GameClient.bClient) {
                        this.SaveFileIP = ServerOptions.instance.ServerPlayerID.getValue();
                    }
                    this.load(SliceY.SliceBuffer, int1);
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
    
    public void setVehicle4TestCollision(final BaseVehicle vehicle4testCollision) {
        this.vehicle4testCollision = vehicle4testCollision;
    }
    
    public boolean isSaveFileInUse() {
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            final IsoPlayer isoPlayer = IsoPlayer.players[i];
            if (isoPlayer != null) {
                if (this.sqlID != -1 && this.sqlID == isoPlayer.sqlID) {
                    return true;
                }
                if (this.SaveFileName != null && this.SaveFileName.equals(isoPlayer.SaveFileName)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void removeSaveFile() {
        try {
            if (PlayerDB.isAvailable()) {
                PlayerDB.getInstance().saveLocalPlayersForce();
            }
            if (this.isNPC() && this.SaveFileName != null) {
                final File absoluteFile = new File(this.SaveFileName).getAbsoluteFile();
                if (absoluteFile.exists()) {
                    absoluteFile.delete();
                }
            }
        }
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
        }
    }
    
    public boolean isSaveFileIPValid() {
        return isServerPlayerIDValid(this.SaveFileIP);
    }
    
    @Override
    public String getObjectName() {
        return "Player";
    }
    
    public int getJoypadBind() {
        return this.JoypadBind;
    }
    
    public boolean isLBPressed() {
        return this.JoypadBind != -1 && JoypadManager.instance.isLBPressed(this.JoypadBind);
    }
    
    public Vector2 getControllerAimDir(final Vector2 vector2) {
        if (GameWindow.ActivatedJoyPad != null && this.JoypadBind != -1 && this.bJoypadMovementActive) {
            float aimingAxisX = JoypadManager.instance.getAimingAxisX(this.JoypadBind);
            float aimingAxisY = JoypadManager.instance.getAimingAxisY(this.JoypadBind);
            if (this.bJoypadIgnoreAimUntilCentered) {
                if (vector2.set(aimingAxisX, aimingAxisY).getLengthSquared() > 0.0f) {
                    return vector2.set(0.0f, 0.0f);
                }
                this.bJoypadIgnoreAimUntilCentered = false;
            }
            if (vector2.set(aimingAxisX, aimingAxisY).getLength() < 0.3f) {
                aimingAxisY = (aimingAxisX = 0.0f);
            }
            if (aimingAxisX == 0.0f && aimingAxisY == 0.0f) {
                return vector2.set(0.0f, 0.0f);
            }
            vector2.set(aimingAxisX, aimingAxisY);
            vector2.normalize();
            vector2.rotate(-0.7853982f);
        }
        return vector2;
    }
    
    public Vector2 getMouseAimVector(final Vector2 vector2) {
        final int x = Mouse.getX();
        final int y = Mouse.getY();
        vector2.x = IsoUtils.XToIso((float)x, y + 55.0f * this.def.getScaleY(), this.getZ()) - this.getX();
        vector2.y = IsoUtils.YToIso((float)x, y + 55.0f * this.def.getScaleY(), this.getZ()) - this.getY();
        vector2.normalize();
        return vector2;
    }
    
    public Vector2 getAimVector(final Vector2 vector2) {
        if (this.JoypadBind == -1) {
            return this.getMouseAimVector(vector2);
        }
        return this.getControllerAimDir(vector2);
    }
    
    @Override
    public float getGlobalMovementMod(final boolean b) {
        if (this.isGhostMode() || this.isNoClip()) {
            return 1.0f;
        }
        return super.getGlobalMovementMod(b);
    }
    
    @Override
    public boolean isInTrees2(final boolean b) {
        return !this.isGhostMode() && !this.isNoClip() && super.isInTrees2(b);
    }
    
    @Override
    public float getMoveSpeed() {
        float n = 1.0f;
        for (int i = BodyPartType.ToIndex(BodyPartType.UpperLeg_L); i <= BodyPartType.ToIndex(BodyPartType.Foot_R); ++i) {
            final BodyPart bodyPart = this.getBodyDamage().getBodyPart(BodyPartType.FromIndex(i));
            float n2 = 1.0f;
            if (bodyPart.getFractureTime() > 20.0f) {
                n2 = 0.4f;
                if (bodyPart.getFractureTime() > 50.0f) {
                    n2 = 0.3f;
                }
                if (bodyPart.getSplintFactor() > 0.0f) {
                    n2 += bodyPart.getSplintFactor() / 10.0f;
                }
            }
            if (bodyPart.getFractureTime() < 20.0f && bodyPart.getSplintFactor() > 0.0f) {
                n2 = 0.8f;
            }
            if (n2 > 0.7f && bodyPart.getDeepWoundTime() > 0.0f) {
                n2 = 0.7f;
                if (bodyPart.bandaged()) {
                    n2 += 0.2f;
                }
            }
            if (n2 < n) {
                n = n2;
            }
        }
        if (n != 1.0f) {
            return this.MoveSpeed * n;
        }
        if (this.getMoodles().getMoodleLevel(MoodleType.Panic) >= 4 && this.Traits.AdrenalineJunkie.isSet()) {
            return this.MoveSpeed * (1.0f + (this.getMoodles().getMoodleLevel(MoodleType.Panic) + 1) / 50.0f);
        }
        return this.MoveSpeed;
    }
    
    public void setMoveSpeed(final float moveSpeed) {
        this.MoveSpeed = moveSpeed;
    }
    
    @Override
    public float getTorchStrength() {
        if (this.bRemote) {
            return this.mpTorchStrength;
        }
        final InventoryItem activeLightItem = this.getActiveLightItem();
        if (activeLightItem != null) {
            return activeLightItem.getLightStrength();
        }
        return 0.0f;
    }
    
    public float getInvAimingMod() {
        final int perkLevel = this.getPerkLevel(PerkFactory.Perks.Aiming);
        if (perkLevel == 1) {
            return 0.9f;
        }
        if (perkLevel == 2) {
            return 0.86f;
        }
        if (perkLevel == 3) {
            return 0.82f;
        }
        if (perkLevel == 4) {
            return 0.74f;
        }
        if (perkLevel == 5) {
            return 0.7f;
        }
        if (perkLevel == 6) {
            return 0.66f;
        }
        if (perkLevel == 7) {
            return 0.62f;
        }
        if (perkLevel == 8) {
            return 0.58f;
        }
        if (perkLevel == 9) {
            return 0.54f;
        }
        if (perkLevel == 10) {
            return 0.5f;
        }
        return 0.9f;
    }
    
    public float getAimingMod() {
        final int perkLevel = this.getPerkLevel(PerkFactory.Perks.Aiming);
        if (perkLevel == 1) {
            return 1.1f;
        }
        if (perkLevel == 2) {
            return 1.14f;
        }
        if (perkLevel == 3) {
            return 1.18f;
        }
        if (perkLevel == 4) {
            return 1.22f;
        }
        if (perkLevel == 5) {
            return 1.26f;
        }
        if (perkLevel == 6) {
            return 1.3f;
        }
        if (perkLevel == 7) {
            return 1.34f;
        }
        if (perkLevel == 8) {
            return 1.36f;
        }
        if (perkLevel == 9) {
            return 1.4f;
        }
        if (perkLevel == 10) {
            return 1.5f;
        }
        return 1.0f;
    }
    
    public float getReloadingMod() {
        return 3.5f - this.getPerkLevel(PerkFactory.Perks.Reloading) * 0.25f;
    }
    
    public float getAimingRangeMod() {
        final int perkLevel = this.getPerkLevel(PerkFactory.Perks.Aiming);
        if (perkLevel == 1) {
            return 1.2f;
        }
        if (perkLevel == 2) {
            return 1.28f;
        }
        if (perkLevel == 3) {
            return 1.36f;
        }
        if (perkLevel == 4) {
            return 1.42f;
        }
        if (perkLevel == 5) {
            return 1.5f;
        }
        if (perkLevel == 6) {
            return 1.58f;
        }
        if (perkLevel == 7) {
            return 1.66f;
        }
        if (perkLevel == 8) {
            return 1.72f;
        }
        if (perkLevel == 9) {
            return 1.8f;
        }
        if (perkLevel == 10) {
            return 2.0f;
        }
        return 1.1f;
    }
    
    public boolean isPathfindRunning() {
        return this.pathfindRun;
    }
    
    public void setPathfindRunning(final boolean pathfindRun) {
        this.pathfindRun = pathfindRun;
    }
    
    public boolean isBannedAttacking() {
        return this.bBannedAttacking;
    }
    
    public void setBannedAttacking(final boolean bBannedAttacking) {
        this.bBannedAttacking = bBannedAttacking;
    }
    
    public float getInvAimingRangeMod() {
        final int perkLevel = this.getPerkLevel(PerkFactory.Perks.Aiming);
        if (perkLevel == 1) {
            return 0.8f;
        }
        if (perkLevel == 2) {
            return 0.7f;
        }
        if (perkLevel == 3) {
            return 0.62f;
        }
        if (perkLevel == 4) {
            return 0.56f;
        }
        if (perkLevel == 5) {
            return 0.45f;
        }
        if (perkLevel == 6) {
            return 0.38f;
        }
        if (perkLevel == 7) {
            return 0.31f;
        }
        if (perkLevel == 8) {
            return 0.24f;
        }
        if (perkLevel == 9) {
            return 0.17f;
        }
        if (perkLevel == 10) {
            return 0.1f;
        }
        return 0.8f;
    }
    
    private void updateCursorVisibility() {
        if (!this.isAiming()) {
            return;
        }
        if (this.PlayerIndex != 0 || this.JoypadBind != -1 || this.isDead()) {
            return;
        }
        if (Core.getInstance().getOptionShowCursorWhileAiming()) {
            return;
        }
        if (Core.getInstance().getIsoCursorVisibility() == 0) {
            return;
        }
        if (UIManager.isForceCursorVisible()) {
            return;
        }
        final int xa = Mouse.getXA();
        final int ya = Mouse.getYA();
        if (xa < IsoCamera.getScreenLeft(0) || xa > IsoCamera.getScreenLeft(0) + IsoCamera.getScreenWidth(0)) {
            return;
        }
        if (ya < IsoCamera.getScreenTop(0) || ya > IsoCamera.getScreenTop(0) + IsoCamera.getScreenHeight(0)) {
            return;
        }
        Mouse.setCursorVisible(false);
    }
    
    @Override
    public void render(final float n, final float n2, final float n3, final ColorInfo colorInfo, final boolean b, final boolean b2, final Shader shader) {
        if (DebugOptions.instance.Character.Debug.Render.DisplayRoomAndZombiesZone.getValue()) {
            String name = "";
            if (this.getCurrentRoomDef() != null) {
                name = this.getCurrentRoomDef().name;
            }
            final IsoMetaGrid.Zone definitionZone = ZombiesZoneDefinition.getDefinitionZoneAt((int)n, (int)n2, (int)n3);
            if (definitionZone != null) {
                name = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, name, definitionZone.name, definitionZone.type);
            }
            this.Say(name);
        }
        if (!getInstance().checkCanSeeClient(this)) {
            this.setTargetAlpha(0.0f);
            getInstance().spottedPlayerTimer.remove(this.getRemoteID());
        }
        else {
            this.setTargetAlpha(1.0f);
        }
        super.render(n, n2, n3, colorInfo, b, b2, shader);
    }
    
    @Override
    public void renderlast() {
        super.renderlast();
    }
    
    public float doBeatenVehicle(final float n) {
        if (GameClient.bClient && this.isLocalPlayer()) {
            this.changeState(ForecastBeatenPlayerState.instance());
            return 0.0f;
        }
        if (!GameClient.bClient && !this.isLocalPlayer()) {
            return 0.0f;
        }
        final float damageFromHitByACar = this.getDamageFromHitByACar(n);
        if (this.isAlive()) {
            if (GameClient.bClient) {
                if (this.isCurrentState(PlayerSitOnGroundState.instance())) {
                    this.setKnockedDown(true);
                    this.setReanimateTimer(20.0f);
                }
                else if (this.isOnFloor() || n > 15.0f || this.isCurrentState(PlayerHitReactionState.instance()) || this.isCurrentState(PlayerGetUpState.instance()) || this.isCurrentState(PlayerOnGroundState.instance())) {
                    this.setHitReaction("HitReaction");
                    this.actionContext.reportEvent("washit");
                    this.setVariable("hitpvp", false);
                    this.setKnockedDown(true);
                    this.setReanimateTimer(20.0f);
                }
                else {
                    this.setHitReaction("HitReaction");
                    this.actionContext.reportEvent("washit");
                    this.setVariable("hitpvp", false);
                }
            }
            else if (this.getCurrentState() != PlayerHitReactionState.instance() && this.getCurrentState() != PlayerFallDownState.instance() && this.getCurrentState() != PlayerOnGroundState.instance() && !this.isKnockedDown()) {
                if (damageFromHitByACar > 15.0f) {
                    this.setKnockedDown(true);
                    this.setReanimateTimer((float)(20 + Rand.Next(60)));
                }
                this.setHitReaction("HitReaction");
                this.actionContext.reportEvent("washit");
            }
        }
        return damageFromHitByACar;
    }
    
    @Override
    public void update() {
        s_performance.update.invokeAndMeasure(this, IsoPlayer::updateInternal1);
    }
    
    private void updateInternal1() {
        if (this.replay != null) {
            this.replay.update();
        }
        final boolean updateInternal2 = this.updateInternal2();
        GameClient.instance.sendPlayer2(this);
        if (updateInternal2) {
            if (!this.bRemote) {
                this.updateLOS();
            }
            super.update();
        }
    }
    
    private void setBeenMovingSprinting() {
        if (this.isJustMoved()) {
            this.setBeenMovingFor(this.getBeenMovingFor() + 1.25f * GameTime.getInstance().getMultiplier());
        }
        else {
            this.setBeenMovingFor(this.getBeenMovingFor() - 0.625f * GameTime.getInstance().getMultiplier());
        }
        if (this.isJustMoved() && this.isSprinting()) {
            this.setBeenSprintingFor(this.getBeenSprintingFor() + 1.25f * GameTime.getInstance().getMultiplier());
        }
        else {
            this.setBeenSprintingFor(0.0f);
        }
    }
    
    private boolean updateInternal2() {
        if (IsoPlayer.isTestAIMode) {
            this.isNPC = true;
        }
        if (!this.attackStarted) {
            this.setInitiateAttack(false);
            this.setAttackType(null);
        }
        if ((this.isRunning() || this.isSprinting()) && this.getDeferredMovement(IsoPlayer.tempo).getLengthSquared() > 0.0f) {
            this.runningTime += GameTime.getInstance().getMultiplier() / 1.6f;
        }
        else {
            this.runningTime = 0.0f;
        }
        if (this.getLastCollideTime() > 0.0f) {
            this.setLastCollideTime(this.getLastCollideTime() - GameTime.getInstance().getMultiplier() / 1.6f);
        }
        this.updateDeathDragDown();
        this.updateGodModeKey();
        if (GameClient.bClient) {
            this.networkAI.update();
        }
        this.doDeferredMovement();
        if (GameServer.bServer) {
            this.vehicle4testCollision = null;
        }
        else if (GameClient.bClient) {
            if (this.vehicle4testCollision != null) {
                if (!this.isLocal()) {
                    this.vehicle4testCollision.updateHitByVehicle(this);
                }
                this.vehicle4testCollision = null;
            }
        }
        else {
            this.updateHitByVehicle();
            this.vehicle4testCollision = null;
        }
        this.updateEmitter();
        this.updateMechanicsItems();
        this.updateHeavyBreathing();
        this.updateTemperatureCheck();
        this.updateAimingStance();
        if (SystemDisabler.doCharacterStats) {
            this.nutrition.update();
        }
        this.fitness.update();
        this.updateSoundListener();
        if (GameClient.bClient && this.isLocalPlayer() && this.getSafetyCooldown() > 0.0f) {
            this.setSafetyCooldown(this.getSafetyCooldown() - GameTime.instance.getRealworldSecondsSinceLastUpdate());
        }
        if (!GameClient.bClient && !GameServer.bServer && this.bDeathFinished) {
            return false;
        }
        if (!GameClient.bClient && this.getCurrentBuildingDef() != null && !this.isInvisible()) {
            this.getCurrentBuildingDef().setHasBeenVisited(true);
        }
        if (this.checkSafehouse > 0 && GameServer.bServer) {
            --this.checkSafehouse;
            if (this.checkSafehouse == 0) {
                this.checkSafehouse = 200;
                final SafeHouse safeHouse = SafeHouse.isSafeHouse(this.getCurrentSquare(), null, false);
                if (safeHouse != null) {
                    safeHouse.updateSafehouse(this);
                }
            }
        }
        if (this.bRemote && this.TimeSinceLastNetData > 600) {
            IsoWorld.instance.CurrentCell.getObjectList().remove(this);
            if (this.movingSq != null) {
                this.movingSq.getMovingObjects().remove(this);
            }
        }
        this.TimeSinceLastNetData += (int)GameTime.instance.getMultiplier();
        this.TimeSinceOpenDoor += GameTime.instance.getMultiplier();
        this.lastTargeted += GameTime.instance.getMultiplier();
        this.targetedByZombie = false;
        this.checkActionGroup();
        if (this.updateRemotePlayer()) {
            if (this.updateWhileDead()) {
                return true;
            }
            this.updateHeartSound();
            this.checkIsNearWall();
            this.updateExt();
            this.setBeenMovingSprinting();
            return true;
        }
        else {
            assert !GameServer.bServer;
            assert !this.bRemote;
            assert !(!this.isLocalPlayer());
            IsoCamera.CamCharacter = this;
            IsoPlayer.instance = this;
            if (this.isLocalPlayer()) {
                IsoCamera.cameras[this.PlayerIndex].update();
                if (UIManager.getMoodleUI(this.PlayerIndex) != null) {
                    UIManager.getMoodleUI(this.PlayerIndex).setCharacter(this);
                }
            }
            if (this.closestZombie > 1.2f) {
                this.slowTimer = -1.0f;
                this.slowFactor = 0.0f;
            }
            this.ContextPanic -= 1.5f * GameTime.instance.getTimeDelta();
            if (this.ContextPanic < 0.0f) {
                this.ContextPanic = 0.0f;
            }
            this.lastSeenZombieTime += GameTime.instance.getGameWorldSecondsSinceLastUpdate() / 60.0f / 60.0f;
            LuaEventManager.triggerEvent("OnPlayerUpdate", this);
            if (this.pressedMovement(false)) {
                this.ContextPanic = 0.0f;
                this.ticksSincePressedMovement = 0.0f;
            }
            else {
                this.ticksSincePressedMovement += GameTime.getInstance().getMultiplier() / 1.6f;
            }
            this.setVariable("pressedMovement", this.pressedMovement(true));
            if (this.updateWhileDead()) {
                return true;
            }
            this.updateHeartSound();
            this.updateWorldAmbiance();
            this.updateSneakKey();
            this.checkIsNearWall();
            this.updateExt();
            this.updateInteractKeyPanic();
            if (this.isAsleep()) {
                this.m_isPlayerMoving = false;
            }
            if (this.getVehicle() == null || !this.getVehicle().isDriver(this) || !this.getVehicle().hasHorn() || Core.getInstance().getKey("Shout") != Core.getInstance().getKey("VehicleHorn")) {
                if (this.isAsleep() || this.PlayerIndex != 0 || this.Speaking || !GameKeyboard.isKeyDown(Core.getInstance().getKey("Shout")) || !this.isNPC) {}
            }
            if (this.getIgnoreMovement() || this.isAsleep()) {
                return true;
            }
            if (this.checkActionsBlockingMovement()) {
                if (this.getVehicle() != null && this.getVehicle().getDriver() == this && this.getVehicle().getController() != null) {
                    this.getVehicle().getController().clientControls.reset();
                    this.getVehicle().updatePhysics();
                }
                return true;
            }
            this.enterExitVehicle();
            this.checkActionGroup();
            this.checkReloading();
            if (this.checkActionsBlockingMovement()) {
                return true;
            }
            if (this.getVehicle() != null) {
                this.updateWhileInVehicle();
                return true;
            }
            this.checkVehicleContainers();
            this.setCollidable(true);
            this.updateCursorVisibility();
            this.bSeenThisFrame = false;
            this.bCouldBeSeenThisFrame = false;
            if (IsoCamera.CamCharacter == null && GameClient.bClient) {
                IsoCamera.CamCharacter = IsoPlayer.instance;
            }
            if (this.updateUseKey()) {
                return true;
            }
            this.updateEnableModelsKey();
            this.updateChangeCharacterKey();
            boolean isAttacking = false;
            boolean meleePressed = false;
            this.setRunning(false);
            this.setSprinting(false);
            this.useChargeTime = this.chargeTime;
            if (!this.isBlockMovement() && !this.isNPC) {
                if (this.isCharging || this.isChargingLT) {
                    this.chargeTime += 1.0f * GameTime.instance.getMultiplier();
                }
                else {
                    this.chargeTime = 0.0f;
                }
                this.UpdateInputState(this.inputState);
                meleePressed = this.inputState.bMelee;
                isAttacking = this.inputState.isAttacking;
                this.setRunning(this.inputState.bRunning);
                this.setSprinting(this.inputState.bSprinting);
                if (this.isSprinting() && !this.isJustMoved()) {
                    this.setSprinting(false);
                }
                if (this.isSprinting()) {
                    this.setRunning(false);
                }
                this.setIsAiming(this.inputState.isAiming);
                this.isCharging = this.inputState.isCharging;
                this.isChargingLT = this.inputState.isChargingLT;
                this.updateMovementRates();
                if (this.isAiming()) {
                    this.StopAllActionQueueAiming();
                }
                if (isAttacking) {
                    this.setIsAiming(true);
                }
                this.Waiting = false;
                if (this.isAiming()) {
                    this.setMoving(false);
                    this.setRunning(false);
                    this.setSprinting(false);
                }
                ++this.TicksSinceSeenZombie;
            }
            if (this.playerMoveDir.x == 0.0 && this.playerMoveDir.y == 0.0) {
                this.setForceRun(false);
                this.setForceSprint(false);
            }
            this.movementLastFrame.x = this.playerMoveDir.x;
            this.movementLastFrame.y = this.playerMoveDir.y;
            if (this.stateMachine.getCurrent() == StaggerBackState.instance() || this.stateMachine.getCurrent() == FakeDeadZombieState.instance() || UIManager.speedControls == null) {
                return true;
            }
            if (GameKeyboard.isKeyDown(88) && Translator.debug) {
                Translator.loadFiles();
            }
            this.setJustMoved(false);
            final MoveVars s_moveVars = IsoPlayer.s_moveVars;
            this.updateMovementFromInput(s_moveVars);
            if (!this.JustMoved && this.hasPath() && !this.getPathFindBehavior2().bStopping) {
                this.JustMoved = true;
            }
            float deltaX = s_moveVars.strafeX;
            float deltaY = s_moveVars.strafeY;
            if (this.isJustMoved() && !this.isNPC && !this.hasPath()) {
                if (UIManager.getSpeedControls().getCurrentGameSpeed() > 1) {
                    UIManager.getSpeedControls().SetCurrentGameSpeed(1);
                }
            }
            else if (this.stats.endurance < this.stats.endurancedanger && Rand.Next((int)(300.0f * GameTime.instance.getInvMultiplier())) == 0) {
                this.xp.AddXP(PerkFactory.Perks.Fitness, 1.0f);
            }
            this.setBeenMovingSprinting();
            final float n = 1.0f;
            float n2 = 0.0f;
            if (this.isJustMoved() && !this.isNPC) {
                if (this.isRunning() || this.isSprinting()) {
                    n2 = 1.5f;
                }
                else {
                    n2 = 1.0f;
                }
            }
            float n3 = n * n2;
            if (n3 > 1.0f) {
                n3 *= this.getSprintMod();
            }
            if (n3 > 1.0f && this.Traits.Athletic.isSet()) {
                n3 *= 1.2f;
            }
            if (n3 > 1.0f) {
                if (this.Traits.Overweight.isSet()) {
                    n3 *= 0.99f;
                }
                if (this.Traits.Obese.isSet()) {
                    n3 *= 0.85f;
                }
                if (this.getNutrition().getWeight() > 120.0f) {
                    n3 *= 0.97f;
                }
                if (this.Traits.OutOfShape.isSet()) {
                    n3 *= 0.99f;
                }
                if (this.Traits.Unfit.isSet()) {
                    n3 *= 0.8f;
                }
            }
            this.updateEndurance(n3);
            if (this.isAiming() && this.isJustMoved()) {
                n3 *= 0.7f;
            }
            if (this.isAiming()) {
                n3 *= this.getNimbleMod();
            }
            this.isWalking = false;
            if (n3 > 0.0f && !this.isNPC) {
                this.isWalking = true;
                LuaEventManager.triggerEvent("OnPlayerMove", this);
            }
            if (this.isJustMoved()) {
                this.sprite.Animate = true;
            }
            if (this.isNPC && this.GameCharacterAIBrain != null) {
                meleePressed = this.GameCharacterAIBrain.HumanControlVars.bMelee;
                this.bBannedAttacking = this.GameCharacterAIBrain.HumanControlVars.bBannedAttacking;
            }
            if (this.m_meleePressed = meleePressed) {
                if (!this.m_lastAttackWasShove) {
                    this.setMeleeDelay(Math.min(this.getMeleeDelay(), 2.0f));
                }
                if (!this.bBannedAttacking && this.isAuthorizeShoveStomp() && this.CanAttack() && this.getMeleeDelay() <= 0.0f) {
                    this.setDoShove(true);
                    if (!this.isCharging && !this.isChargingLT) {
                        this.setIsAiming(false);
                    }
                    this.AttemptAttack(this.useChargeTime);
                    this.useChargeTime = 0.0f;
                    this.chargeTime = 0.0f;
                }
            }
            else if (this.isAiming() && this.CanAttack()) {
                if (this.DragCharacter != null) {
                    this.DragObject = null;
                    this.DragCharacter.Dragging = false;
                    this.DragCharacter = null;
                }
                if (isAttacking && !this.bBannedAttacking) {
                    this.sprite.Animate = true;
                    if (this.getRecoilDelay() <= 0.0f && this.getMeleeDelay() <= 0.0f) {
                        this.AttemptAttack(this.useChargeTime);
                    }
                    this.useChargeTime = 0.0f;
                    this.chargeTime = 0.0f;
                }
            }
            if (this.isAiming() && !this.isNPC) {
                if (this.JoypadBind == -1 || this.bJoypadMovementActive) {
                    final Vector2 set = IsoPlayer.tempVector2.set(0.0f, 0.0f);
                    if (GameWindow.ActivatedJoyPad != null && this.JoypadBind != -1) {
                        this.getControllerAimDir(set);
                    }
                    else {
                        this.getMouseAimVector(set);
                    }
                    if (set.getLengthSquared() > 0.0f) {
                        this.DirectionFromVector(set);
                        this.setForwardDirection(set);
                    }
                }
                else if (this.getForwardDirection().getLengthSquared() > 0.0f) {
                    this.DirectionFromVector(this.getForwardDirection());
                }
                s_moveVars.NewFacing = this.dir;
            }
            if (this.getForwardDirection().x == 0.0f && this.getForwardDirection().y == 0.0f) {
                this.setForwardDirection(this.dir.ToVector());
            }
            if (this.lastAngle.x != this.getForwardDirection().x || this.lastAngle.y != this.getForwardDirection().y) {
                this.lastAngle.x = this.getForwardDirection().x;
                this.lastAngle.y = this.getForwardDirection().y;
                this.dirtyRecalcGridStackTime = 2.0f;
            }
            this.stats.endurance = PZMath.clamp(this.stats.endurance, 0.0f, 1.0f);
            final AnimationPlayer animationPlayer = this.getAnimationPlayer();
            if (animationPlayer != null && animationPlayer.isReady()) {
                this.dir = IsoDirections.fromAngle(IsoPlayer.tempVector2.setLengthAndDirection(animationPlayer.getAngle() + animationPlayer.getTwistAngle(), 1.0f));
            }
            else if (!this.bFalling && !this.isAiming() && !isAttacking) {
                this.dir = s_moveVars.NewFacing;
            }
            if (this.isAiming() && (GameWindow.ActivatedJoyPad == null || this.JoypadBind == -1)) {
                this.playerMoveDir.x = s_moveVars.moveX;
                this.playerMoveDir.y = s_moveVars.moveY;
            }
            if (!this.isAiming() && this.isJustMoved()) {
                this.playerMoveDir.x = this.getForwardDirection().x;
                this.playerMoveDir.y = this.getForwardDirection().y;
            }
            if (this.isJustMoved()) {
                if (this.isSprinting()) {
                    this.CurrentSpeed = 1.5f;
                }
                else if (this.isRunning()) {
                    this.CurrentSpeed = 1.0f;
                }
                else {
                    this.CurrentSpeed = 0.5f;
                }
            }
            else {
                this.CurrentSpeed = 0.0f;
            }
            boolean isInMeleeAttack = this.IsInMeleeAttack();
            if (!this.CharacterActions.isEmpty() && this.CharacterActions.get(0).overrideAnimation) {
                isInMeleeAttack = true;
            }
            if (!isInMeleeAttack && !this.isForceOverrideAnim()) {
                if (this.getPath2() == null) {
                    if (this.CurrentSpeed > 0.0f && (!this.bClimbing || this.lastFallSpeed > 0.0f)) {
                        if (this.isRunning() || this.isSprinting()) {
                            this.StopAllActionQueueRunning();
                        }
                        else {
                            this.StopAllActionQueueWalking();
                        }
                    }
                }
                else {
                    this.StopAllActionQueueWalking();
                }
            }
            if (this.slowTimer > 0.0f) {
                this.slowTimer -= GameTime.instance.getRealworldSecondsSinceLastUpdate();
                this.CurrentSpeed *= 1.0f - this.slowFactor;
                this.slowFactor -= GameTime.instance.getMultiplier() / 100.0f;
                if (this.slowFactor < 0.0f) {
                    this.slowFactor = 0.0f;
                }
            }
            else {
                this.slowFactor = 0.0f;
            }
            this.playerMoveDir.setLength(this.CurrentSpeed);
            if (this.playerMoveDir.x != 0.0f || this.playerMoveDir.y != 0.0f) {
                this.dirtyRecalcGridStackTime = 10.0f;
            }
            if (this.getPath2() != null && this.current != this.last) {
                this.dirtyRecalcGridStackTime = 10.0f;
            }
            this.closestZombie = 1000000.0f;
            this.weight = 0.3f;
            this.separate();
            this.updateSleepingPillsTaken();
            this.updateTorchStrength();
            if (this.isNPC && this.GameCharacterAIBrain != null) {
                this.GameCharacterAIBrain.postUpdateHuman(this);
                this.setInitiateAttack(this.GameCharacterAIBrain.HumanControlVars.initiateAttack);
                this.setRunning(this.GameCharacterAIBrain.HumanControlVars.bRunning);
                deltaX = this.GameCharacterAIBrain.HumanControlVars.strafeX;
                deltaY = this.GameCharacterAIBrain.HumanControlVars.strafeY;
                this.setJustMoved(this.GameCharacterAIBrain.HumanControlVars.JustMoved);
                this.updateMovementRates();
            }
            this.m_isPlayerMoving = (this.isJustMoved() || (this.getPath2() != null && !this.getPathFindBehavior2().bStopping));
            final boolean inTrees = this.isInTrees();
            if (inTrees) {
                final float n4 = "parkranger".equals(this.getDescriptor().getProfession()) ? 1.3f : 1.0f;
                float n5 = "lumberjack".equals(this.getDescriptor().getProfession()) ? 1.15f : n4;
                if (this.isRunning()) {
                    n5 *= 1.1f;
                }
                this.setVariable("WalkSpeedTrees", n5);
            }
            if ((inTrees || this.m_walkSpeed < 0.4f || this.m_walkInjury > 0.5f) && this.isSprinting() && !this.isGhostMode()) {
                if (this.runSpeedModifier < 1.0) {
                    this.setMoodleCantSprint(true);
                }
                this.setSprinting(false);
                this.setForceSprint(false);
                if (this.isInTreesNoBush()) {
                    this.setBumpType("left");
                    this.setVariable("BumpDone", false);
                    this.setVariable("BumpFall", true);
                    this.setVariable("TripObstacleType", "tree");
                    this.actionContext.reportEvent("wasBumped");
                }
            }
            this.m_deltaX = deltaX;
            this.m_deltaY = deltaY;
            this.m_windspeed = ClimateManager.getInstance().getWindSpeedMovement();
            this.m_windForce = ClimateManager.getInstance().getWindForceMovement(this, this.getForwardDirection().getDirectionNeg());
            return true;
        }
    }
    
    private void updateMovementFromInput(final MoveVars moveVars) {
        moveVars.moveX = 0.0f;
        moveVars.moveY = 0.0f;
        moveVars.strafeX = 0.0f;
        moveVars.strafeY = 0.0f;
        moveVars.NewFacing = this.dir;
        if (TutorialManager.instance.StealControl) {
            return;
        }
        if (this.isBlockMovement()) {
            return;
        }
        if (this.isNPC) {
            return;
        }
        if (MPDebugAI.updateMovementFromInput(this, moveVars)) {
            return;
        }
        if (this.fallTime > 2.0f) {
            return;
        }
        if (GameWindow.ActivatedJoyPad != null && this.JoypadBind != -1) {
            this.updateMovementFromJoypad(moveVars);
        }
        if (this.PlayerIndex == 0 && this.JoypadBind == -1) {
            this.updateMovementFromKeyboardMouse(moveVars);
        }
        if (this.isJustMoved()) {
            this.getForwardDirection().normalize();
            UIManager.speedControls.SetCurrentGameSpeed(1);
        }
    }
    
    private void updateMovementFromJoypad(final MoveVars moveVars) {
        this.playerMoveDir.x = 0.0f;
        this.playerMoveDir.y = 0.0f;
        this.getJoypadAimVector(IsoPlayer.tempVector2);
        final float x = IsoPlayer.tempVector2.x;
        final float y = IsoPlayer.tempVector2.y;
        final Vector2 joypadMoveVector = this.getJoypadMoveVector(IsoPlayer.tempVector2);
        if (joypadMoveVector.getLength() > 1.0f) {
            joypadMoveVector.setLength(1.0f);
        }
        final float x2 = joypadMoveVector.x;
        final float y2 = joypadMoveVector.y;
        if (Math.abs(x2) > 0.0f) {
            final Vector2 playerMoveDir = this.playerMoveDir;
            playerMoveDir.x += 0.04f * x2;
            final Vector2 playerMoveDir2 = this.playerMoveDir;
            playerMoveDir2.y -= 0.04f * x2;
            this.setJustMoved(true);
        }
        if (Math.abs(y2) > 0.0f) {
            final Vector2 playerMoveDir3 = this.playerMoveDir;
            playerMoveDir3.y += 0.04f * y2;
            final Vector2 playerMoveDir4 = this.playerMoveDir;
            playerMoveDir4.x += 0.04f * y2;
            this.setJustMoved(true);
        }
        if (JoypadManager.instance.isL3Pressed(this.JoypadBind)) {
            if (!this.L3Pressed) {
                this.setSneaking(!this.isSneaking());
                this.L3Pressed = true;
            }
        }
        else {
            this.L3Pressed = false;
        }
        this.playerMoveDir.setLength(0.05f * (float)Math.pow(joypadMoveVector.getLength(), 9.0));
        if (x != 0.0f || y != 0.0f) {
            final Vector2 set = IsoPlayer.tempVector2.set(x, y);
            set.normalize();
            moveVars.NewFacing = IsoDirections.fromAngle(set);
        }
        else if ((x2 != 0.0f || y2 != 0.0f) && this.playerMoveDir.getLengthSquared() > 0.0f) {
            final Vector2 set2 = IsoPlayer.tempVector2.set(this.playerMoveDir);
            set2.normalize();
            moveVars.NewFacing = IsoDirections.fromAngle(set2);
        }
        final PathFindBehavior2 pathFindBehavior2 = this.getPathFindBehavior2();
        if (this.playerMoveDir.x == 0.0f && this.playerMoveDir.y == 0.0f && this.getPath2() != null && pathFindBehavior2.isStrafing() && !pathFindBehavior2.bStopping) {
            this.playerMoveDir.set(pathFindBehavior2.getTargetX() - this.x, pathFindBehavior2.getTargetY() - this.y);
            this.playerMoveDir.normalize();
        }
        if (this.playerMoveDir.x != 0.0f || this.playerMoveDir.y != 0.0f) {
            if (this.isStrafing()) {
                IsoPlayer.tempo.set(this.playerMoveDir.x, -this.playerMoveDir.y);
                IsoPlayer.tempo.normalize();
                float renderedAngle = this.legsSprite.modelSlot.model.AnimPlayer.getRenderedAngle();
                if (renderedAngle > 6.283185307179586) {
                    renderedAngle -= (float)6.283185307179586;
                }
                if (renderedAngle < 0.0f) {
                    renderedAngle += (float)6.283185307179586;
                }
                IsoPlayer.tempo.rotate(renderedAngle);
                moveVars.strafeX = IsoPlayer.tempo.x;
                moveVars.strafeY = IsoPlayer.tempo.y;
                this.m_IPX = this.playerMoveDir.x;
                this.m_IPY = this.playerMoveDir.y;
            }
            else {
                moveVars.moveX = this.playerMoveDir.x;
                moveVars.moveY = this.playerMoveDir.y;
                IsoPlayer.tempo.set(this.playerMoveDir);
                IsoPlayer.tempo.normalize();
                this.setForwardDirection(IsoPlayer.tempo);
            }
        }
    }
    
    private void updateMovementFromKeyboardMouse(final MoveVars moveVars) {
        final int key = Core.getInstance().getKey("Left");
        final int key2 = Core.getInstance().getKey("Right");
        final int key3 = Core.getInstance().getKey("Forward");
        final int key4 = Core.getInstance().getKey("Backward");
        final boolean keyDown = GameKeyboard.isKeyDown(key);
        final boolean keyDown2 = GameKeyboard.isKeyDown(key2);
        final boolean keyDown3 = GameKeyboard.isKeyDown(key3);
        final boolean keyDown4 = GameKeyboard.isKeyDown(key4);
        if ((keyDown || keyDown2 || keyDown3 || keyDown4) && (key == 30 || key2 == 30 || key3 == 30 || key4 == 30) && (GameKeyboard.isKeyDown(29) || GameKeyboard.isKeyDown(157)) && UIManager.isMouseOverInventory() && Core.getInstance().isSelectingAll()) {
            return;
        }
        if (!this.isIgnoreInputsForDirection()) {
            if (Core.bAltMoveMethod) {
                if (keyDown && !keyDown2) {
                    moveVars.moveX -= 0.04f;
                    moveVars.NewFacing = IsoDirections.W;
                }
                if (keyDown2 && !keyDown) {
                    moveVars.moveX += 0.04f;
                    moveVars.NewFacing = IsoDirections.E;
                }
                if (keyDown3 && !keyDown4) {
                    moveVars.moveY -= 0.04f;
                    if (moveVars.NewFacing == IsoDirections.W) {
                        moveVars.NewFacing = IsoDirections.NW;
                    }
                    else if (moveVars.NewFacing == IsoDirections.E) {
                        moveVars.NewFacing = IsoDirections.NE;
                    }
                    else {
                        moveVars.NewFacing = IsoDirections.N;
                    }
                }
                if (keyDown4 && !keyDown3) {
                    moveVars.moveY += 0.04f;
                    if (moveVars.NewFacing == IsoDirections.W) {
                        moveVars.NewFacing = IsoDirections.SW;
                    }
                    else if (moveVars.NewFacing == IsoDirections.E) {
                        moveVars.NewFacing = IsoDirections.SE;
                    }
                    else {
                        moveVars.NewFacing = IsoDirections.S;
                    }
                }
            }
            else {
                if (keyDown) {
                    moveVars.moveX = -1.0f;
                }
                else if (keyDown2) {
                    moveVars.moveX = 1.0f;
                }
                if (keyDown3) {
                    moveVars.moveY = 1.0f;
                }
                else if (keyDown4) {
                    moveVars.moveY = -1.0f;
                }
                if (moveVars.moveX != 0.0f || moveVars.moveY != 0.0f) {
                    IsoPlayer.tempo.set(moveVars.moveX, moveVars.moveY);
                    IsoPlayer.tempo.normalize();
                    moveVars.NewFacing = IsoDirections.fromAngle(IsoPlayer.tempo);
                }
            }
        }
        final PathFindBehavior2 pathFindBehavior2 = this.getPathFindBehavior2();
        if (moveVars.moveX == 0.0f && moveVars.moveY == 0.0f && this.getPath2() != null && (pathFindBehavior2.isStrafing() || this.isAiming()) && !pathFindBehavior2.bStopping) {
            final Vector2 set = IsoPlayer.tempo.set(pathFindBehavior2.getTargetX() - this.x, pathFindBehavior2.getTargetY() - this.y);
            final Vector2 set2 = IsoPlayer.tempo2.set(-1.0f, 0.0f);
            final float n = 1.0f;
            IsoPlayer.tempo.set(set.dot(IsoPlayer.tempo2.set(0.0f, -1.0f)) / n, set.dot(set2) / n);
            IsoPlayer.tempo.normalize();
            IsoPlayer.tempo.rotate(0.7853982f);
            moveVars.moveX = IsoPlayer.tempo.x;
            moveVars.moveY = IsoPlayer.tempo.y;
        }
        if (moveVars.moveX != 0.0f || moveVars.moveY != 0.0f) {
            if (this.stateMachine.getCurrent() == PathFindState.instance()) {
                this.setDefaultState();
            }
            this.setJustMoved(true);
            this.setMoveDelta(1.0f);
            if (this.isStrafing()) {
                IsoPlayer.tempo.set(moveVars.moveX, moveVars.moveY);
                IsoPlayer.tempo.normalize();
                float n2 = (float)(this.legsSprite.modelSlot.model.AnimPlayer.getRenderedAngle() + 0.7853981633974483);
                if (n2 > 6.283185307179586) {
                    n2 -= (float)6.283185307179586;
                }
                if (n2 < 0.0f) {
                    n2 += (float)6.283185307179586;
                }
                IsoPlayer.tempo.rotate(n2);
                moveVars.strafeX = IsoPlayer.tempo.x;
                moveVars.strafeY = IsoPlayer.tempo.y;
                this.m_IPX = moveVars.moveX;
                this.m_IPY = moveVars.moveY;
            }
            else {
                IsoPlayer.tempo.set(moveVars.moveX, -moveVars.moveY);
                IsoPlayer.tempo.normalize();
                IsoPlayer.tempo.rotate(-0.7853982f);
                this.setForwardDirection(IsoPlayer.tempo);
            }
        }
    }
    
    private void updateAimingStance() {
        if (this.isVariable("LeftHandMask", "RaiseHand")) {
            this.clearVariable("LeftHandMask");
        }
        if (!this.isAiming() || this.isCurrentState(SwipeStatePlayer.instance())) {
            return;
        }
        final HandWeapon handWeapon = Type.tryCastTo(this.getPrimaryHandItem(), HandWeapon.class);
        final HandWeapon handWeapon2 = (handWeapon == null) ? this.bareHands : handWeapon;
        SwipeStatePlayer.instance().calcValidTargets(this, handWeapon2, true, IsoPlayer.s_targetsProne, IsoPlayer.s_targetsStanding);
        HitInfo hitInfo = IsoPlayer.s_targetsStanding.isEmpty() ? null : IsoPlayer.s_targetsStanding.get(0);
        final HitInfo hitInfo2 = IsoPlayer.s_targetsProne.isEmpty() ? null : IsoPlayer.s_targetsProne.get(0);
        if (SwipeStatePlayer.instance().isProneTargetBetter(this, hitInfo, hitInfo2)) {
            hitInfo = null;
        }
        final boolean b = this.isAttackAnim() || this.getVariableBoolean("ShoveAnim") || this.getVariableBoolean("StompAnim");
        if (!b) {
            this.setAimAtFloor(false);
        }
        if (hitInfo != null) {
            if (!b) {
                this.setAimAtFloor(false);
            }
        }
        else if (hitInfo2 != null && !b) {
            this.setAimAtFloor(true);
        }
        if (hitInfo != null && (!this.isAttackAnim() && handWeapon2.getSwingAnim() != null && handWeapon2.CloseKillMove != null && hitInfo.distSq < handWeapon2.getMinRange() * handWeapon2.getMinRange()) && (this.getSecondaryHandItem() == null || this.getSecondaryHandItem().getItemReplacementSecondHand() == null)) {
            this.setVariable("LeftHandMask", "RaiseHand");
        }
        SwipeStatePlayer.instance().hitInfoPool.release(IsoPlayer.s_targetsStanding);
        SwipeStatePlayer.instance().hitInfoPool.release(IsoPlayer.s_targetsProne);
        IsoPlayer.s_targetsStanding.clear();
        IsoPlayer.s_targetsProne.clear();
    }
    
    @Override
    protected void calculateStats() {
        if (this.bRemote) {
            return;
        }
        super.calculateStats();
    }
    
    @Override
    protected void updateStats_Sleeping() {
        float n = 2.0f;
        if (allPlayersAsleep()) {
            n *= GameTime.instance.getDeltaMinutesPerDay();
        }
        final Stats stats = this.stats;
        stats.endurance += (float)(ZomboidGlobals.ImobileEnduranceReduce * SandboxOptions.instance.getEnduranceRegenMultiplier() * this.getRecoveryMod() * GameTime.instance.getMultiplier() * n);
        if (this.stats.endurance > 1.0f) {
            this.stats.endurance = 1.0f;
        }
        if (this.stats.fatigue > 0.0f) {
            float n2 = 1.0f;
            if (this.Traits.Insomniac.isSet()) {
                n2 *= 0.5f;
            }
            if (this.Traits.NightOwl.isSet()) {
                n2 *= 1.4f;
            }
            float n3 = 1.0f;
            if ("goodBed".equals(this.getBedType())) {
                n3 = 1.1f;
            }
            if ("badBed".equals(this.getBedType())) {
                n3 = 0.9f;
            }
            if ("floor".equals(this.getBedType())) {
                n3 = 0.6f;
            }
            final float n4 = 1.0f / GameTime.instance.getMinutesPerDay() / 60.0f * GameTime.instance.getMultiplier() / 2.0f;
            this.timeOfSleep += n4;
            if (this.timeOfSleep > this.delayToActuallySleep) {
                float n5 = 1.0f;
                if (this.Traits.NeedsLessSleep.isSet()) {
                    n5 *= 0.75f;
                }
                else if (this.Traits.NeedsMoreSleep.isSet()) {
                    n5 *= 1.18f;
                }
                if (this.stats.fatigue <= 0.3f) {
                    final float n6 = 7.0f * n5;
                    final Stats stats2 = this.stats;
                    stats2.fatigue -= n4 / n6 * 0.3f * n2 * n3;
                }
                else {
                    final float n7 = 5.0f * n5;
                    final Stats stats3 = this.stats;
                    stats3.fatigue -= n4 / n7 * 0.7f * n2 * n3;
                }
            }
            if (this.stats.fatigue < 0.0f) {
                this.stats.fatigue = 0.0f;
            }
        }
        if (this.Moodles.getMoodleLevel(MoodleType.FoodEaten) == 0) {
            final float appetiteMultiplier = this.getAppetiteMultiplier();
            final Stats stats4 = this.stats;
            stats4.hunger += (float)(ZomboidGlobals.HungerIncreaseWhileAsleep * SandboxOptions.instance.getStatsDecreaseMultiplier() * appetiteMultiplier * GameTime.instance.getMultiplier() * GameTime.instance.getDeltaMinutesPerDay() * this.getHungerMultiplier());
        }
        else {
            final Stats stats5 = this.stats;
            stats5.hunger += (float)(ZomboidGlobals.HungerIncreaseWhenWellFed * SandboxOptions.instance.getStatsDecreaseMultiplier() * ZomboidGlobals.HungerIncreaseWhileAsleep * SandboxOptions.instance.getStatsDecreaseMultiplier() * GameTime.instance.getMultiplier() * this.getHungerMultiplier() * GameTime.instance.getDeltaMinutesPerDay());
        }
        if (this.ForceWakeUpTime == 0.0f) {
            this.ForceWakeUpTime = 9.0f;
        }
        float timeOfDay = GameTime.getInstance().getTimeOfDay();
        float lastTimeOfDay = GameTime.getInstance().getLastTimeOfDay();
        if (lastTimeOfDay > timeOfDay) {
            if (lastTimeOfDay < this.ForceWakeUpTime) {
                timeOfDay += 24.0f;
            }
            else {
                lastTimeOfDay -= 24.0f;
            }
        }
        int n8 = (timeOfDay >= this.ForceWakeUpTime && lastTimeOfDay < this.ForceWakeUpTime) ? 1 : 0;
        if (this.getAsleepTime() > 16.0f) {
            n8 = 1;
        }
        if (GameClient.bClient || IsoPlayer.numPlayers > 1) {
            n8 = ((n8 != 0 || this.pressedAim() || this.pressedMovement(false)) ? 1 : 0);
        }
        if (this.ForceWakeUp) {
            n8 = 1;
        }
        if (this.Asleep && n8 != 0) {
            this.ForceWakeUp = false;
            SoundManager.instance.setMusicWakeState(this, "WakeNormal");
            SleepingEvent.instance.wakeUp(this);
            this.ForceWakeUpTime = -1.0f;
            if (GameClient.bClient) {
                GameClient.instance.sendPlayer(this);
            }
            this.dirtyRecalcGridStackTime = 20.0f;
        }
    }
    
    private void updateEndurance(float n) {
        if (this.isSitOnGround()) {
            final float n2 = (float)ZomboidGlobals.SittingEnduranceMultiplier * (1.0f - this.stats.fatigue) * GameTime.instance.getMultiplier();
            final Stats stats = this.stats;
            stats.endurance += (float)(ZomboidGlobals.ImobileEnduranceReduce * SandboxOptions.instance.getEnduranceRegenMultiplier() * this.getRecoveryMod() * n2);
            this.stats.endurance = PZMath.clamp(this.stats.endurance, 0.0f, 1.0f);
            return;
        }
        float n3 = 1.0f;
        if (this.isSneaking()) {
            n3 = 1.5f;
        }
        if (this.CurrentSpeed > 0.0f && (this.isRunning() || this.isSprinting())) {
            double n4 = ZomboidGlobals.RunningEnduranceReduce;
            if (this.isSprinting()) {
                n4 = ZomboidGlobals.SprintingEnduranceReduce;
            }
            float n5 = 1.4f;
            if (this.Traits.Overweight.isSet()) {
                n5 = 2.9f;
            }
            if (this.Traits.Athletic.isSet()) {
                n5 = 0.8f;
            }
            final float n6 = n5 * 2.3f * this.getPacingMod() * this.getHyperthermiaMod();
            float n7 = 0.7f;
            if (this.Traits.Asthmatic.isSet()) {
                n7 = 1.4f;
            }
            if (this.Moodles.getMoodleLevel(MoodleType.HeavyLoad) == 0) {
                final Stats stats2 = this.stats;
                stats2.endurance -= (float)(n4 * n6 * 0.5 * n7 * GameTime.instance.getMultiplier() * n3);
            }
            else {
                float n8 = 2.8f;
                switch (this.Moodles.getMoodleLevel(MoodleType.HeavyLoad)) {
                    case 1: {
                        n8 = 1.5f;
                        break;
                    }
                    case 2: {
                        n8 = 1.9f;
                        break;
                    }
                    case 3: {
                        n8 = 2.3f;
                        break;
                    }
                }
                final Stats stats3 = this.stats;
                stats3.endurance -= (float)(n4 * n6 * 0.5 * n7 * GameTime.instance.getMultiplier() * n8 * n3);
            }
        }
        else if (this.CurrentSpeed > 0.0f && this.Moodles.getMoodleLevel(MoodleType.HeavyLoad) > 2) {
            float n9 = 0.7f;
            if (this.Traits.Asthmatic.isSet()) {
                n9 = 1.4f;
            }
            float n10 = 1.4f;
            if (this.Traits.Overweight.isSet()) {
                n10 = 2.9f;
            }
            if (this.Traits.Athletic.isSet()) {
                n10 = 0.8f;
            }
            final float n11 = n10 * 3.0f * this.getPacingMod() * this.getHyperthermiaMod();
            float n12 = 2.8f;
            switch (this.Moodles.getMoodleLevel(MoodleType.HeavyLoad)) {
                case 2: {
                    n12 = 1.5f;
                    break;
                }
                case 3: {
                    n12 = 1.9f;
                    break;
                }
                case 4: {
                    n12 = 2.3f;
                    break;
                }
            }
            final Stats stats4 = this.stats;
            stats4.endurance -= (float)(ZomboidGlobals.RunningEnduranceReduce * n11 * 0.5 * n9 * n3 * GameTime.instance.getMultiplier() * n12 / 2.0);
        }
        switch (this.Moodles.getMoodleLevel(MoodleType.Endurance)) {
            case 1: {
                n *= 0.95f;
                break;
            }
            case 2: {
                n *= 0.9f;
                break;
            }
            case 3: {
                n *= 0.8f;
                break;
            }
            case 4: {
                n *= 0.6f;
                break;
            }
        }
        if (this.stats.enduranceRecharging) {
            n *= 0.85f;
        }
        if (!this.isPlayerMoving()) {
            final float n13 = 1.0f * (1.0f - this.stats.fatigue) * GameTime.instance.getMultiplier();
            if (this.Moodles.getMoodleLevel(MoodleType.HeavyLoad) <= 1) {
                final Stats stats5 = this.stats;
                stats5.endurance += (float)(ZomboidGlobals.ImobileEnduranceReduce * SandboxOptions.instance.getEnduranceRegenMultiplier() * this.getRecoveryMod() * n13);
            }
        }
        if (!this.isSprinting() && !this.isRunning() && this.CurrentSpeed > 0.0f) {
            final float n14 = 1.0f * (1.0f - this.stats.fatigue) * GameTime.instance.getMultiplier();
            if (this.getMoodles().getMoodleLevel(MoodleType.Endurance) < 2) {
                if (this.Moodles.getMoodleLevel(MoodleType.HeavyLoad) <= 1) {
                    final Stats stats6 = this.stats;
                    stats6.endurance += (float)(ZomboidGlobals.ImobileEnduranceReduce / 4.0 * SandboxOptions.instance.getEnduranceRegenMultiplier() * this.getRecoveryMod() * n14);
                }
            }
            else {
                final Stats stats7 = this.stats;
                stats7.endurance -= (float)(ZomboidGlobals.RunningEnduranceReduce / 7.0 * n3);
            }
        }
    }
    
    private boolean checkActionsBlockingMovement() {
        return !this.CharacterActions.isEmpty() && this.CharacterActions.get(0).blockMovementEtc;
    }
    
    private void updateInteractKeyPanic() {
        if (this.PlayerIndex != 0) {
            return;
        }
        if (GameKeyboard.isKeyPressed(Core.getInstance().getKey("Interact"))) {
            this.ContextPanic += 0.6f;
        }
    }
    
    private void updateSneakKey() {
        if (this.PlayerIndex != 0) {
            this.bSneakDebounce = false;
            return;
        }
        if (!this.isBlockMovement() && GameKeyboard.isKeyDown(Core.getInstance().getKey("Crouch"))) {
            if (!this.bSneakDebounce) {
                this.setSneaking(!this.isSneaking());
                this.bSneakDebounce = true;
            }
        }
        else {
            this.bSneakDebounce = false;
        }
    }
    
    private void updateChangeCharacterKey() {
        if (!Core.bDebug) {
            return;
        }
        if (this.PlayerIndex != 0 || !GameKeyboard.isKeyDown(22)) {
            this.bChangeCharacterDebounce = false;
            return;
        }
        if (this.bChangeCharacterDebounce) {
            return;
        }
        this.FollowCamStack.clear();
        this.bChangeCharacterDebounce = true;
        for (int i = 0; i < this.getCell().getObjectList().size(); ++i) {
            final IsoMovingObject isoMovingObject = this.getCell().getObjectList().get(i);
            if (isoMovingObject instanceof IsoSurvivor) {
                this.FollowCamStack.add((IsoSurvivor)isoMovingObject);
            }
        }
        if (!this.FollowCamStack.isEmpty()) {
            if (this.followID >= this.FollowCamStack.size()) {
                this.followID = 0;
            }
            IsoCamera.SetCharacterToFollow(this.FollowCamStack.get(this.followID));
            ++this.followID;
        }
    }
    
    private void updateEnableModelsKey() {
        if (!Core.bDebug) {
            return;
        }
        if (this.PlayerIndex == 0 && GameKeyboard.isKeyPressed(Core.getInstance().getKey("ToggleModelsEnabled"))) {
            ModelManager.instance.bDebugEnableModels = !ModelManager.instance.bDebugEnableModels;
        }
    }
    
    private void updateDeathDragDown() {
        if (this.isDead()) {
            return;
        }
        if (!this.isDeathDragDown()) {
            return;
        }
        if (this.isGodMod()) {
            this.setDeathDragDown(false);
            return;
        }
        if ("EndDeath".equals(this.getHitReaction())) {
            return;
        }
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                final IsoGridSquare gridSquare = this.getCell().getGridSquare((int)this.x + j, (int)this.y + i, (int)this.z);
                if (gridSquare != null) {
                    for (int k = 0; k < gridSquare.getMovingObjects().size(); ++k) {
                        final IsoZombie attackedBy = Type.tryCastTo(gridSquare.getMovingObjects().get(k), IsoZombie.class);
                        if (attackedBy != null && attackedBy.isAlive() && !attackedBy.isOnFloor()) {
                            this.setAttackedBy(attackedBy);
                            this.setHitReaction("EndDeath");
                            this.setBlockMovement(true);
                            return;
                        }
                    }
                }
            }
        }
        this.setDeathDragDown(false);
    }
    
    private void updateGodModeKey() {
        if (!Core.bDebug) {
            return;
        }
        if (!GameKeyboard.isKeyPressed(Core.getInstance().getKey("ToggleGodModeInvisible"))) {
            return;
        }
        IsoPlayer isoPlayer = null;
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            if (IsoPlayer.players[i] != null && !IsoPlayer.players[i].isDead()) {
                isoPlayer = IsoPlayer.players[i];
                break;
            }
        }
        if (this == isoPlayer) {
            final boolean b = !isoPlayer.isGodMod();
            DebugLog.General.println("Toggle GodMode: %s", b ? "ON" : "OFF");
            isoPlayer.setInvisible(b);
            isoPlayer.setGhostMode(b);
            isoPlayer.setGodMod(b);
            for (int j = 0; j < IsoPlayer.numPlayers; ++j) {
                if (IsoPlayer.players[j] != null && IsoPlayer.players[j] != isoPlayer) {
                    IsoPlayer.players[j].setInvisible(b);
                    IsoPlayer.players[j].setGhostMode(b);
                    IsoPlayer.players[j].setGodMod(b);
                }
            }
            if (GameClient.bClient) {
                GameClient.sendPlayerExtraInfo(isoPlayer);
            }
        }
    }
    
    private void checkReloading() {
        final HandWeapon handWeapon = Type.tryCastTo(this.getPrimaryHandItem(), HandWeapon.class);
        if (handWeapon == null || !handWeapon.isReloadable(this)) {
            return;
        }
        int n = 0;
        int n2 = 0;
        if (this.JoypadBind != -1 && this.bJoypadMovementActive) {
            final boolean rbPressed = JoypadManager.instance.isRBPressed(this.JoypadBind);
            if (rbPressed) {
                n = (this.bReloadButtonDown ? 0 : 1);
            }
            this.bReloadButtonDown = rbPressed;
            final boolean lbPressed = JoypadManager.instance.isLBPressed(this.JoypadBind);
            if (lbPressed) {
                n2 = (this.bRackButtonDown ? 0 : 1);
            }
            this.bRackButtonDown = lbPressed;
        }
        if (this.PlayerIndex == 0) {
            final boolean keyDown = GameKeyboard.isKeyDown(Core.getInstance().getKey("ReloadWeapon"));
            if (keyDown) {
                n = (this.bReloadKeyDown ? 0 : 1);
            }
            this.bReloadKeyDown = keyDown;
            final boolean keyDown2 = GameKeyboard.isKeyDown(Core.getInstance().getKey("Rack Firearm"));
            if (keyDown2) {
                n2 = (this.bRackKeyDown ? 0 : 1);
            }
            this.bRackKeyDown = keyDown2;
        }
        if (n != 0) {
            this.setVariable("WeaponReloadType", handWeapon.getWeaponReloadType());
            LuaEventManager.triggerEvent("OnPressReloadButton", this, handWeapon);
        }
        else if (n2 != 0) {
            this.setVariable("WeaponReloadType", handWeapon.getWeaponReloadType());
            LuaEventManager.triggerEvent("OnPressRackButton", this, handWeapon);
        }
    }
    
    @Override
    public void postupdate() {
        s_performance.postUpdate.invokeAndMeasure(this, IsoPlayer::postupdateInternal);
    }
    
    private void postupdateInternal() {
        final boolean hasHitReaction = this.hasHitReaction();
        super.postupdate();
        if (hasHitReaction && this.hasHitReaction() && !this.isCurrentState(PlayerHitReactionState.instance()) && !this.isCurrentState(PlayerHitReactionPVPState.instance())) {
            this.setHitReaction("");
        }
        this.highlightRangedTargets();
        if (this.isNPC) {
            final GameTime instance = GameTime.getInstance();
            float n = 1.0f / instance.getMinutesPerDay() / 60.0f * instance.getMultiplier() / 2.0f;
            if (Core.bLastStand) {
                n = 1.0f / instance.getMinutesPerDay() / 60.0f * instance.getUnmoddedMultiplier() / 2.0f;
            }
            this.setHoursSurvived(this.getHoursSurvived() + n);
        }
        this.getBodyDamage().setBodyPartsLastState();
    }
    
    private void highlightRangedTargets() {
        if (!this.isLocalPlayer() || this.isNPC) {
            return;
        }
        if (!this.isAiming()) {
            return;
        }
        if (Core.getInstance().getOptionAimOutline() == 1) {
            return;
        }
        s_performance.highlightRangedTargets.invokeAndMeasure(this, IsoPlayer::highlightRangedTargetsInternal);
    }
    
    private void highlightRangedTargetsInternal() {
        HandWeapon bareHands = Type.tryCastTo(this.getPrimaryHandItem(), HandWeapon.class);
        if (bareHands == null || bareHands.getSwingAnim() == null || bareHands.getCondition() <= 0) {
            bareHands = this.bareHands;
        }
        if (Core.getInstance().getOptionAimOutline() == 2 && !bareHands.isRanged()) {
            return;
        }
        final AttackVars attackVars = new AttackVars();
        final ArrayList list = new ArrayList<HitInfo>();
        final boolean bDoShove = this.bDoShove;
        final HandWeapon useHandWeapon = this.getUseHandWeapon();
        this.setDoShove(false);
        this.setUseHandWeapon(bareHands);
        SwipeStatePlayer.instance().CalcAttackVars(this, attackVars);
        SwipeStatePlayer.instance().CalcHitList(this, false, attackVars, list);
        for (int i = 0; i < list.size(); ++i) {
            final HitInfo hitInfo = list.get(i);
            final IsoMovingObject object = hitInfo.getObject();
            if (object instanceof IsoZombie || object instanceof IsoPlayer) {
                final float n = 1.0f - hitInfo.chance / 100.0f;
                final float n2 = hitInfo.chance / 100.0f;
                float n3 = 0.4f;
                if (n2 < 0.7) {
                    n3 = 0.36f;
                }
                object.bOutline[this.PlayerIndex] = true;
                if (object.outlineColor[this.PlayerIndex] == null) {
                    object.outlineColor[this.PlayerIndex] = new ColorInfo();
                }
                object.outlineColor[this.PlayerIndex].set(n * 0.75f, n2 * n3, 0.0f, 1.0f);
            }
            if (hitInfo.window.getObject() != null) {
                hitInfo.window.getObject().setHighlightColor(0.8f, 0.1f, 0.1f, 0.5f);
                hitInfo.window.getObject().setHighlighted(true);
            }
        }
        this.setDoShove(bDoShove);
        this.setUseHandWeapon(useHandWeapon);
    }
    
    @Override
    public boolean isSolidForSeparate() {
        return !this.isGhostMode() && super.isSolidForSeparate();
    }
    
    @Override
    public boolean isPushableForSeparate() {
        return !this.isCurrentState(PlayerHitReactionState.instance()) && !this.isCurrentState(SwipeStatePlayer.instance()) && super.isPushableForSeparate();
    }
    
    @Override
    public boolean isPushedByForSeparate(final IsoMovingObject isoMovingObject) {
        return (this.isPlayerMoving() || !isoMovingObject.isZombie() || !((IsoZombie)isoMovingObject).isAttacking()) && (!GameClient.bClient || (this.isLocalPlayer() && this.isJustMoved())) && super.isPushedByForSeparate(isoMovingObject);
    }
    
    private void updateExt() {
        if (this.isSneaking()) {
            return;
        }
        this.extUpdateCount += GameTime.getInstance().getMultiplier() / 0.8f;
        if (!this.getAdvancedAnimator().containsAnyIdleNodes() && !this.isSitOnGround()) {
            this.extUpdateCount = 0.0f;
        }
        if (this.extUpdateCount <= 5000.0f) {
            return;
        }
        this.extUpdateCount = 0.0f;
        if (this.stats.NumVisibleZombies != 0 || this.stats.NumChasingZombies != 0) {
            return;
        }
        if (!Rand.NextBool(3)) {
            return;
        }
        if (!this.getAdvancedAnimator().containsAnyIdleNodes() && !this.isSitOnGround()) {
            return;
        }
        this.onIdlePerformFidgets();
        this.reportEvent("EventDoExt");
    }
    
    private void onIdlePerformFidgets() {
        final Moodles moodles = this.getMoodles();
        final BodyDamage bodyDamage = this.getBodyDamage();
        if (moodles.getMoodleLevel(MoodleType.Hypothermia) > 0 && Rand.NextBool(7)) {
            this.setVariable("Ext", "Shiver");
            return;
        }
        if (moodles.getMoodleLevel(MoodleType.Hyperthermia) > 0 && Rand.NextBool(7)) {
            this.setVariable("Ext", "WipeBrow");
            return;
        }
        if (moodles.getMoodleLevel(MoodleType.Sick) > 0 && Rand.NextBool(7)) {
            if (Rand.NextBool(4)) {
                this.setVariable("Ext", "Cough");
            }
            else {
                this.setVariable("Ext", invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Rand.Next(2) + 1));
            }
            return;
        }
        if (moodles.getMoodleLevel(MoodleType.Endurance) > 2 && Rand.NextBool(10)) {
            if (Rand.NextBool(5) && !this.isSitOnGround()) {
                this.setVariable("Ext", "BentDouble");
            }
            else {
                this.setVariable("Ext", "WipeBrow");
            }
            return;
        }
        if (moodles.getMoodleLevel(MoodleType.Tired) > 2 && Rand.NextBool(10)) {
            if (Rand.NextBool(7)) {
                this.setVariable("Ext", "TiredStretch");
            }
            else if (Rand.NextBool(7)) {
                this.setVariable("Ext", "Sway");
            }
            else {
                this.setVariable("Ext", "Yawn");
            }
            return;
        }
        if (bodyDamage.doBodyPartsHaveInjuries(BodyPartType.Head, BodyPartType.Neck) && Rand.NextBool(7)) {
            if (bodyDamage.areBodyPartsBleeding(BodyPartType.Head, BodyPartType.Neck) && Rand.NextBool(2)) {
                this.setVariable("Ext", "WipeHead");
            }
            else {
                this.setVariable("Ext", invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Rand.Next(2) + 1));
            }
            return;
        }
        if (bodyDamage.doBodyPartsHaveInjuries(BodyPartType.UpperArm_L, BodyPartType.ForeArm_L) && Rand.NextBool(7)) {
            if (bodyDamage.areBodyPartsBleeding(BodyPartType.UpperArm_L, BodyPartType.ForeArm_L) && Rand.NextBool(2)) {
                this.setVariable("Ext", "WipeArmL");
            }
            else {
                this.setVariable("Ext", "PainArmL");
            }
            return;
        }
        if (bodyDamage.doBodyPartsHaveInjuries(BodyPartType.UpperArm_R, BodyPartType.ForeArm_R) && Rand.NextBool(7)) {
            if (bodyDamage.areBodyPartsBleeding(BodyPartType.UpperArm_R, BodyPartType.ForeArm_R) && Rand.NextBool(2)) {
                this.setVariable("Ext", "WipeArmR");
            }
            else {
                this.setVariable("Ext", "PainArmR");
            }
            return;
        }
        if (bodyDamage.doesBodyPartHaveInjury(BodyPartType.Hand_L) && Rand.NextBool(7)) {
            this.setVariable("Ext", "PainHandL");
            return;
        }
        if (bodyDamage.doesBodyPartHaveInjury(BodyPartType.Hand_R) && Rand.NextBool(7)) {
            this.setVariable("Ext", "PainHandR");
            return;
        }
        if (!this.isSitOnGround() && bodyDamage.doBodyPartsHaveInjuries(BodyPartType.UpperLeg_L, BodyPartType.LowerLeg_L) && Rand.NextBool(7)) {
            if (bodyDamage.areBodyPartsBleeding(BodyPartType.UpperLeg_L, BodyPartType.LowerLeg_L) && Rand.NextBool(2)) {
                this.setVariable("Ext", "WipeLegL");
            }
            else {
                this.setVariable("Ext", "PainLegL");
            }
            return;
        }
        if (!this.isSitOnGround() && bodyDamage.doBodyPartsHaveInjuries(BodyPartType.UpperLeg_R, BodyPartType.LowerLeg_R) && Rand.NextBool(7)) {
            if (bodyDamage.areBodyPartsBleeding(BodyPartType.UpperLeg_R, BodyPartType.LowerLeg_R) && Rand.NextBool(2)) {
                this.setVariable("Ext", "WipeLegR");
            }
            else {
                this.setVariable("Ext", "PainLegR");
            }
            return;
        }
        if (bodyDamage.doBodyPartsHaveInjuries(BodyPartType.Torso_Upper, BodyPartType.Torso_Lower) && Rand.NextBool(7)) {
            if (bodyDamage.areBodyPartsBleeding(BodyPartType.Torso_Upper, BodyPartType.Torso_Lower) && Rand.NextBool(2)) {
                this.setVariable("Ext", invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Rand.Next(2) + 1));
            }
            else {
                this.setVariable("Ext", "PainTorso");
            }
            return;
        }
        if (WeaponType.getWeaponType(this) != WeaponType.barehand) {
            this.setVariable("Ext", invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Rand.Next(5) + 1));
            return;
        }
        if (Rand.NextBool(10)) {
            this.setVariable("Ext", "ChewNails");
            return;
        }
        if (Rand.NextBool(10)) {
            this.setVariable("Ext", "ShiftWeight");
            return;
        }
        if (Rand.NextBool(10)) {
            this.setVariable("Ext", "PullAtColar");
            return;
        }
        if (Rand.NextBool(10)) {
            this.setVariable("Ext", "BridgeNose");
            return;
        }
        this.setVariable("Ext", invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Rand.Next(5) + 1));
    }
    
    private boolean updateUseKey() {
        if (GameServer.bServer) {
            return false;
        }
        if (!this.isLocalPlayer()) {
            return false;
        }
        if (this.PlayerIndex != 0) {
            return false;
        }
        this.timePressedContext += GameTime.instance.getRealworldSecondsSinceLastUpdate();
        final boolean keyDown = GameKeyboard.isKeyDown(Core.getInstance().getKey("Interact"));
        if (keyDown && this.timePressedContext < 0.5f) {
            this.bPressContext = true;
        }
        else {
            if (this.bPressContext && ((Core.CurrentTextEntryBox != null && Core.CurrentTextEntryBox.DoingTextEntry) || !GameKeyboard.doLuaKeyPressed)) {
                this.bPressContext = false;
            }
            if (this.bPressContext && this.doContext(this.dir)) {
                this.timePressedContext = 0.0f;
                this.bPressContext = false;
                return true;
            }
            if (!keyDown) {
                this.bPressContext = false;
                this.timePressedContext = 0.0f;
            }
        }
        return false;
    }
    
    private void updateHitByVehicle() {
        if (GameServer.bServer) {
            return;
        }
        if (!this.isLocalPlayer()) {
            return;
        }
        if (this.vehicle4testCollision == null || !this.ulBeatenVehicle.Check() || SandboxOptions.instance.DamageToPlayerFromHitByACar.getValue() <= 1) {
            return;
        }
        final BaseVehicle vehicle4testCollision = this.vehicle4testCollision;
        this.vehicle4testCollision = null;
        if (!vehicle4testCollision.isEngineRunning() || this.getVehicle() == vehicle4testCollision) {
            return;
        }
        float n = vehicle4testCollision.jniLinearVelocity.x;
        float n2 = vehicle4testCollision.jniLinearVelocity.z;
        if (GameClient.bClient && this.isLocalPlayer()) {
            n = vehicle4testCollision.netLinearVelocity.x;
            n2 = vehicle4testCollision.netLinearVelocity.z;
        }
        final float n3 = (float)Math.sqrt(n * n + n2 * n2);
        final Vector2 vector2 = BaseVehicle.TL_vector2_pool.get().alloc();
        final Vector2 testCollisionWithCharacter = vehicle4testCollision.testCollisionWithCharacter(this, 0.20000002f, vector2);
        if (testCollisionWithCharacter != null && testCollisionWithCharacter.x != -1.0f) {
            testCollisionWithCharacter.x = (testCollisionWithCharacter.x - vehicle4testCollision.x) * n3 * 1.0f + this.x;
            testCollisionWithCharacter.y = (testCollisionWithCharacter.y - vehicle4testCollision.y) * n3 * 1.0f + this.x;
            if (this.isOnFloor()) {
                if (vehicle4testCollision.testCollisionWithProneCharacter(this, false) > 0) {
                    this.doBeatenVehicle(Math.max(n3 * 6.0f, 5.0f));
                }
                this.doBeatenVehicle(0.0f);
            }
            else if (this.getCurrentState() != PlayerFallDownState.instance() && n3 > 0.1f) {
                this.doBeatenVehicle(Math.max(n3 * 2.0f, 5.0f));
            }
        }
        BaseVehicle.TL_vector2_pool.get().release(vector2);
    }
    
    private void updateSoundListener() {
        if (GameServer.bServer) {
            return;
        }
        if (!this.isLocalPlayer()) {
            return;
        }
        if (this.soundListener == null) {
            this.soundListener = (BaseSoundListener)(Core.SoundDisabled ? new DummySoundListener(this.PlayerIndex) : new SoundListener(this.PlayerIndex));
        }
        this.soundListener.setPos(this.x, this.y, this.z);
        this.checkNearbyRooms -= GameTime.getInstance().getMultiplier() / 1.6f;
        if (this.checkNearbyRooms <= 0.0f) {
            this.checkNearbyRooms = 30.0f;
            this.numNearbyBuildingsRooms = (float)IsoWorld.instance.MetaGrid.countNearbyBuildingsRooms(this);
        }
        if (this.testemitter == null) {
            (this.testemitter = (BaseSoundEmitter)(Core.SoundDisabled ? new DummySoundEmitter() : new FMODSoundEmitter())).setPos(this.x, this.y, this.z);
        }
        this.soundListener.tick();
        this.testemitter.tick();
    }
    
    public void updateMovementRates() {
        this.calculateWalkSpeed();
        this.m_idleSpeed = this.calculateIdleSpeed();
        this.updateFootInjuries();
    }
    
    public void pressedAttack(final boolean b) {
        final boolean b2 = GameClient.bClient && !this.isLocalPlayer();
        final boolean sprinting = this.isSprinting();
        this.setSprinting(false);
        this.setForceSprint(false);
        if (this.attackStarted || this.isCurrentState(PlayerHitReactionState.instance())) {
            return;
        }
        if (GameClient.bClient && this.isCurrentState(PlayerHitReactionPVPState.instance()) && !ServerOptions.instance.PVPMeleeWhileHitReaction.getValue()) {
            return;
        }
        if (this.primaryHandModel != null && !StringUtils.isNullOrEmpty(this.primaryHandModel.maskVariableValue) && this.secondaryHandModel != null && !StringUtils.isNullOrEmpty(this.secondaryHandModel.maskVariableValue)) {
            this.setDoShove(false);
            this.setForceShove(false);
            this.setInitiateAttack(false);
            this.attackStarted = false;
            this.setAttackType(null);
            return;
        }
        if (this.getPrimaryHandItem() != null && this.getPrimaryHandItem().getItemReplacementPrimaryHand() != null && this.getSecondaryHandItem() != null && this.getSecondaryHandItem().getItemReplacementSecondHand() != null) {
            this.setDoShove(false);
            this.setForceShove(false);
            this.setInitiateAttack(false);
            this.attackStarted = false;
            this.setAttackType(null);
            return;
        }
        if (!this.attackStarted) {
            this.setVariable("StartedAttackWhileSprinting", sprinting);
        }
        this.setInitiateAttack(true);
        this.attackStarted = true;
        if (!b2) {
            this.setCriticalHit(false);
        }
        this.setAttackFromBehind(false);
        final WeaponType weaponType = WeaponType.getWeaponType(this);
        if (!GameClient.bClient || this.isLocalPlayer()) {
            this.setAttackType(PZArrayUtil.pickRandom(weaponType.possibleAttack));
        }
        if (!GameClient.bClient || this.isLocalPlayer()) {
            this.combatSpeed = this.calculateCombatSpeed();
        }
        if (b) {
            SwipeStatePlayer.instance().CalcAttackVars(this, this.attackVars);
        }
        final String variableString = this.getVariableString("Weapon");
        if (variableString != null && variableString.equals("throwing") && !this.attackVars.bDoShove) {
            this.setAttackAnimThrowTimer(2000L);
            this.setIsAiming(true);
        }
        if (b2) {
            this.attackVars.bDoShove = this.isDoShove();
            this.attackVars.bAimAtFloor = this.isAimAtFloor();
        }
        if (this.attackVars.bDoShove && !this.isAuthorizeShoveStomp()) {
            this.setDoShove(false);
            this.setForceShove(false);
            this.setInitiateAttack(false);
            this.attackStarted = false;
            this.setAttackType(null);
            return;
        }
        this.useHandWeapon = this.attackVars.getWeapon(this);
        this.setAimAtFloor(this.attackVars.bAimAtFloor);
        this.setDoShove(this.attackVars.bDoShove);
        this.targetOnGround = (IsoGameCharacter)this.attackVars.targetOnGround.getMovingObject();
        if (this.attackVars.getWeapon(this) != null && !StringUtils.isNullOrEmpty(this.attackVars.getWeapon(this).getFireMode())) {
            this.setVariable("FireMode", this.attackVars.getWeapon(this).getFireMode());
        }
        else {
            this.clearVariable("FireMode");
        }
        if (this.useHandWeapon != null && weaponType.isRanged && !this.bDoShove) {
            this.setRecoilDelay((float)(this.useHandWeapon.getRecoilDelay() * (1.0f - this.getPerkLevel(PerkFactory.Perks.Aiming) / 30.0f)).intValue());
        }
        final int next = Rand.Next(0, 3);
        if (next == 0) {
            this.setVariable("AttackVariationX", Rand.Next(-1.0f, -0.5f));
        }
        if (next == 1) {
            this.setVariable("AttackVariationX", 0.0f);
        }
        if (next == 2) {
            this.setVariable("AttackVariationX", Rand.Next(0.5f, 1.0f));
        }
        this.setVariable("AttackVariationY", 0.0f);
        if (b) {
            SwipeStatePlayer.instance().CalcHitList(this, true, this.attackVars, this.hitList);
        }
        IsoGameCharacter isoGameCharacter = null;
        if (!this.hitList.isEmpty()) {
            isoGameCharacter = Type.tryCastTo(this.hitList.get(0).getObject(), IsoGameCharacter.class);
        }
        if (isoGameCharacter == null) {
            if (this.isAiming() && !this.m_meleePressed && this.useHandWeapon != this.bareHands) {
                this.setDoShove(false);
                this.setForceShove(false);
            }
            this.m_lastAttackWasShove = this.bDoShove;
            if (weaponType.canMiss && !this.isAimAtFloor() && (!GameClient.bClient || this.isLocalPlayer())) {
                this.setAttackType("miss");
            }
            if (this.isAiming() && this.bDoShove) {
                this.setVariable("bShoveAiming", true);
            }
            else {
                this.clearVariable("bShoveAiming");
            }
            return;
        }
        if (!GameClient.bClient || this.isLocalPlayer()) {
            this.setAttackFromBehind(this.isBehind(isoGameCharacter));
        }
        final float distanceTo = IsoUtils.DistanceTo(isoGameCharacter.x, isoGameCharacter.y, this.x, this.y);
        this.setVariable("TargetDist", distanceTo);
        int calculateCritChance = this.calculateCritChance(isoGameCharacter);
        if (isoGameCharacter instanceof IsoZombie) {
            final IsoZombie closestZombieToOtherZombie = this.getClosestZombieToOtherZombie((IsoZombie)isoGameCharacter);
            if (!this.attackVars.bAimAtFloor && distanceTo > 1.25 && weaponType == WeaponType.spear && (closestZombieToOtherZombie == null || IsoUtils.DistanceTo(isoGameCharacter.x, isoGameCharacter.y, closestZombieToOtherZombie.x, closestZombieToOtherZombie.y) > 1.7)) {
                if (!GameClient.bClient || this.isLocalPlayer()) {
                    this.setAttackType("overhead");
                }
                calculateCritChance += 30;
            }
        }
        if (this.isLocalPlayer() && !isoGameCharacter.isOnFloor()) {
            isoGameCharacter.setHitFromBehind(this.isAttackFromBehind());
        }
        if (this.isAttackFromBehind()) {
            if (isoGameCharacter instanceof IsoZombie && ((IsoZombie)isoGameCharacter).target == null) {
                calculateCritChance += 30;
            }
            else {
                calculateCritChance += 5;
            }
        }
        if (isoGameCharacter instanceof IsoPlayer && weaponType.isRanged && !this.bDoShove) {
            calculateCritChance = (int)(this.attackVars.getWeapon(this).getStopPower() * (1.0f + this.getPerkLevel(PerkFactory.Perks.Aiming) / 15.0f));
        }
        if (!GameClient.bClient || this.isLocalPlayer()) {
            this.setCriticalHit(Rand.Next(100) < calculateCritChance);
            if (DebugOptions.instance.MultiplayerCriticalHit.getValue()) {
                this.setCriticalHit(true);
            }
            if (this.isAttackFromBehind() && this.attackVars.bCloseKill && isoGameCharacter instanceof IsoZombie && ((IsoZombie)isoGameCharacter).target == null) {
                this.setCriticalHit(true);
            }
            if (this.isCriticalHit() && !this.attackVars.bCloseKill && !this.bDoShove && weaponType == WeaponType.knife) {
                this.setCriticalHit(false);
            }
            this.setAttackWasSuperAttack(false);
            if (this.stats.NumChasingZombies > 1 && this.attackVars.bCloseKill && !this.bDoShove && weaponType == WeaponType.knife) {
                this.setCriticalHit(false);
            }
        }
        if (this.isCriticalHit()) {
            this.combatSpeed *= 1.1f;
        }
        if (DebugLog.isEnabled(DebugType.Combat)) {
            DebugLog.Combat.debugln(invokedynamic(makeConcatWithConstants:(FZIZ)Ljava/lang/String;, distanceTo, this.isCriticalHit(), calculateCritChance, this.isAttackFromBehind()));
        }
        if (this.isAiming() && this.bDoShove) {
            this.setVariable("bShoveAiming", true);
        }
        else {
            this.clearVariable("bShoveAiming");
        }
        if (this.useHandWeapon != null && weaponType.isRanged) {
            this.setRecoilDelay((float)(this.useHandWeapon.getRecoilDelay() - this.getPerkLevel(PerkFactory.Perks.Aiming) * 2));
        }
        this.m_lastAttackWasShove = this.bDoShove;
    }
    
    public void setAttackAnimThrowTimer(final long n) {
        this.AttackAnimThrowTimer = System.currentTimeMillis() + n;
    }
    
    public boolean isAttackAnimThrowTimeOut() {
        return this.AttackAnimThrowTimer <= System.currentTimeMillis();
    }
    
    private boolean getAttackAnim() {
        return false;
    }
    
    private String getWeaponType() {
        if (!this.isAttackAnimThrowTimeOut()) {
            return "throwing";
        }
        return this.WeaponT;
    }
    
    private void setWeaponType(final String weaponT) {
        this.WeaponT = weaponT;
    }
    
    public int calculateCritChance(final IsoGameCharacter isoGameCharacter) {
        if (this.bDoShove) {
            int n = 35;
            if (isoGameCharacter instanceof IsoPlayer) {
                final IsoPlayer isoPlayer = (IsoPlayer)isoGameCharacter;
                n = 20;
                if (GameClient.bClient && !isoPlayer.isLocalPlayer()) {
                    final int n2 = (int)(n - isoPlayer.remoteStrLvl * 1.5);
                    if (isoPlayer.getNutrition().getWeight() < 80.0f) {
                        n = (int)(n2 + Math.abs((isoPlayer.getNutrition().getWeight() - 80.0f) / 2.0f));
                    }
                    else {
                        n = (int)(n2 - (isoPlayer.getNutrition().getWeight() - 80.0f) / 2.0f);
                    }
                }
            }
            return (int)(n - this.getMoodles().getMoodleLevel(MoodleType.Endurance) * 5 - this.getMoodles().getMoodleLevel(MoodleType.HeavyLoad) * 5 - this.getMoodles().getMoodleLevel(MoodleType.Panic) * 1.3) + this.getPerkLevel(PerkFactory.Perks.Strength) * 2;
        }
        if (this.bDoShove && isoGameCharacter.getStateMachine().getCurrent() == StaggerBackState.instance() && isoGameCharacter instanceof IsoZombie) {
            return 100;
        }
        if (this.getPrimaryHandItem() == null || !(this.getPrimaryHandItem() instanceof HandWeapon)) {
            return 0;
        }
        final HandWeapon handWeapon = (HandWeapon)this.getPrimaryHandItem();
        int n3 = (int)handWeapon.getCriticalChance();
        if (handWeapon.isAlwaysKnockdown()) {
            return 100;
        }
        int n5;
        if (WeaponType.getWeaponType(this).isRanged) {
            int n4 = (int)(n3 + handWeapon.getAimingPerkCritModifier() * (this.getPerkLevel(PerkFactory.Perks.Aiming) / 2.0f));
            if (this.getBeenMovingFor() > handWeapon.getAimingTime() + this.getPerkLevel(PerkFactory.Perks.Aiming) * 2) {
                n4 -= (int)(this.getBeenMovingFor() - (handWeapon.getAimingTime() + this.getPerkLevel(PerkFactory.Perks.Aiming) * 2));
            }
            n5 = n4 + this.getPerkLevel(PerkFactory.Perks.Aiming) * 3;
            if (this.DistTo(isoGameCharacter) < 4.0f) {
                n5 += (int)((3.0f - this.DistTo(isoGameCharacter)) * 7.0f);
            }
            else if (this.DistTo(isoGameCharacter) >= 4.0f) {
                n5 -= (int)((4.0f - this.DistTo(isoGameCharacter)) * 7.0f);
            }
        }
        else {
            if (handWeapon.isTwoHandWeapon() && (this.getPrimaryHandItem() != handWeapon || this.getSecondaryHandItem() != handWeapon)) {
                n3 -= n3 / 3;
            }
            if (this.chargeTime < 2.0f) {
                n3 -= n3 / 5;
            }
            int n6 = this.getPerkLevel(PerkFactory.Perks.Blunt);
            if (handWeapon.getCategories().contains("Axe")) {
                n6 = this.getPerkLevel(PerkFactory.Perks.Axe);
            }
            if (handWeapon.getCategories().contains("LongBlade")) {
                n6 = this.getPerkLevel(PerkFactory.Perks.LongBlade);
            }
            if (handWeapon.getCategories().contains("Spear")) {
                n6 = this.getPerkLevel(PerkFactory.Perks.Spear);
            }
            if (handWeapon.getCategories().contains("SmallBlade")) {
                n6 = this.getPerkLevel(PerkFactory.Perks.SmallBlade);
            }
            if (handWeapon.getCategories().contains("SmallBlunt")) {
                n6 = this.getPerkLevel(PerkFactory.Perks.SmallBlunt);
            }
            n5 = n3 + n6 * 3;
            if (isoGameCharacter instanceof IsoPlayer) {
                final IsoPlayer isoPlayer2 = (IsoPlayer)isoGameCharacter;
                if (GameClient.bClient && !isoPlayer2.isLocalPlayer()) {
                    final int n7 = (int)(n5 - isoPlayer2.remoteStrLvl * 1.5);
                    if (isoPlayer2.getNutrition().getWeight() < 80.0f) {
                        n5 = (int)(n7 + Math.abs((isoPlayer2.getNutrition().getWeight() - 80.0f) / 2.0f));
                    }
                    else {
                        n5 = (int)(n7 - (isoPlayer2.getNutrition().getWeight() - 80.0f) / 2.0f);
                    }
                }
            }
        }
        int n8 = (int)(n5 - this.getMoodles().getMoodleLevel(MoodleType.Endurance) * 5 - this.getMoodles().getMoodleLevel(MoodleType.HeavyLoad) * 5 - this.getMoodles().getMoodleLevel(MoodleType.Panic) * 1.3);
        if (SandboxOptions.instance.Lore.Toughness.getValue() == 1) {
            n8 -= 6;
        }
        if (SandboxOptions.instance.Lore.Toughness.getValue() == 3) {
            n8 += 6;
        }
        if (n8 < 10) {
            n8 = 10;
        }
        if (n8 > 90) {
            n8 = 90;
        }
        return n8;
    }
    
    private void checkJoypadIgnoreAimUntilCentered() {
        if (!this.bJoypadIgnoreAimUntilCentered) {
            return;
        }
        if (GameWindow.ActivatedJoyPad != null && this.JoypadBind != -1 && this.bJoypadMovementActive) {
            final float aimingAxisX = JoypadManager.instance.getAimingAxisX(this.JoypadBind);
            final float aimingAxisY = JoypadManager.instance.getAimingAxisY(this.JoypadBind);
            if (aimingAxisX * aimingAxisX + aimingAxisY + aimingAxisY <= 0.0f) {
                this.bJoypadIgnoreAimUntilCentered = false;
            }
        }
    }
    
    public boolean isAimControlActive() {
        return this.isForceAim() || this.isAimKeyDown() || (GameWindow.ActivatedJoyPad != null && this.JoypadBind != -1 && this.bJoypadMovementActive && this.getJoypadAimVector(IsoPlayer.tempo).getLengthSquared() > 0.0f);
    }
    
    private Vector2 getJoypadAimVector(final Vector2 vector2) {
        if (this.bJoypadIgnoreAimUntilCentered) {
            return vector2.set(0.0f, 0.0f);
        }
        float aimingAxisY = JoypadManager.instance.getAimingAxisY(this.JoypadBind);
        float aimingAxisX = JoypadManager.instance.getAimingAxisX(this.JoypadBind);
        final float deadZone = JoypadManager.instance.getDeadZone(this.JoypadBind, 0);
        if (aimingAxisX * aimingAxisX + aimingAxisY * aimingAxisY < deadZone * deadZone) {
            aimingAxisY = (aimingAxisX = 0.0f);
        }
        return vector2.set(aimingAxisX, aimingAxisY);
    }
    
    private Vector2 getJoypadMoveVector(final Vector2 vector2) {
        float movementAxisY = JoypadManager.instance.getMovementAxisY(this.JoypadBind);
        float movementAxisX = JoypadManager.instance.getMovementAxisX(this.JoypadBind);
        final float deadZone = JoypadManager.instance.getDeadZone(this.JoypadBind, 0);
        if (movementAxisX * movementAxisX + movementAxisY * movementAxisY < deadZone * deadZone) {
            movementAxisY = (movementAxisX = 0.0f);
        }
        vector2.set(movementAxisX, movementAxisY);
        if (this.isIgnoreInputsForDirection()) {
            vector2.set(0.0f, 0.0f);
        }
        return vector2;
    }
    
    private void updateToggleToAim() {
        if (this.PlayerIndex != 0) {
            return;
        }
        if (!Core.getInstance().isToggleToAim()) {
            this.setForceAim(false);
            return;
        }
        final boolean aimKeyDown = this.isAimKeyDown();
        final long currentTimeMillis = System.currentTimeMillis();
        if (aimKeyDown) {
            if (this.aimKeyDownMS == 0L) {
                this.aimKeyDownMS = currentTimeMillis;
            }
        }
        else {
            if (this.aimKeyDownMS != 0L && currentTimeMillis - this.aimKeyDownMS < 500L) {
                this.toggleForceAim();
            }
            else if (this.isForceAim()) {
                if (this.aimKeyDownMS != 0L) {
                    this.toggleForceAim();
                }
                else {
                    final int key = Core.getInstance().getKey("Aim");
                    if ((key == 29 || key == 157) && UIManager.isMouseOverInventory()) {
                        this.toggleForceAim();
                    }
                }
            }
            this.aimKeyDownMS = 0L;
        }
    }
    
    private void UpdateInputState(final InputState inputState) {
        inputState.bMelee = false;
        if (MPDebugAI.updateInputState(this, inputState)) {
            return;
        }
        if (GameWindow.ActivatedJoyPad != null && this.JoypadBind != -1) {
            if (this.bJoypadMovementActive) {
                inputState.isAttacking = this.isCharging;
                if (this.bJoypadIgnoreChargingRT) {
                    inputState.isAttacking = false;
                }
                if (this.bJoypadIgnoreAimUntilCentered) {
                    final float aimingAxisX = JoypadManager.instance.getAimingAxisX(this.JoypadBind);
                    final float aimingAxisY = JoypadManager.instance.getAimingAxisY(this.JoypadBind);
                    if (aimingAxisX == 0.0f && aimingAxisY == 0.0f) {
                        this.bJoypadIgnoreAimUntilCentered = false;
                    }
                }
            }
            if (this.isChargingLT) {
                inputState.bMelee = true;
                inputState.isAttacking = false;
            }
        }
        else {
            inputState.isAttacking = (this.isCharging && Mouse.isButtonDownUICheck(0));
            if (GameKeyboard.isKeyDown(Core.getInstance().getKey("Melee")) && this.authorizeMeleeAction) {
                inputState.bMelee = true;
                inputState.isAttacking = false;
            }
        }
        if (GameWindow.ActivatedJoyPad != null && this.JoypadBind != -1) {
            if (this.bJoypadMovementActive) {
                inputState.isCharging = JoypadManager.instance.isRTPressed(this.JoypadBind);
                inputState.isChargingLT = JoypadManager.instance.isLTPressed(this.JoypadBind);
                if (this.bJoypadIgnoreChargingRT && !inputState.isCharging) {
                    this.bJoypadIgnoreChargingRT = false;
                }
            }
            inputState.isAiming = false;
            inputState.bRunning = false;
            inputState.bSprinting = false;
            final Vector2 joypadAimVector = this.getJoypadAimVector(IsoPlayer.tempVector2);
            if (joypadAimVector.x != 0.0f || joypadAimVector.y != 0.0f) {
                inputState.isAiming = true;
            }
            else {
                inputState.isCharging = false;
                final Vector2 joypadMoveVector = this.getJoypadMoveVector(IsoPlayer.tempVector2);
                if (joypadMoveVector.x != 0.0f || joypadMoveVector.y != 0.0f) {
                    if (this.isAllowRun()) {
                        inputState.bRunning = JoypadManager.instance.isRTPressed(this.JoypadBind);
                    }
                    inputState.isAttacking = false;
                    inputState.bMelee = false;
                    this.bJoypadIgnoreChargingRT = true;
                    inputState.isCharging = false;
                    final boolean bPressed = JoypadManager.instance.isBPressed(this.JoypadBind);
                    if (inputState.bRunning && bPressed && !this.bJoypadBDown) {
                        this.bJoypadSprint = !this.bJoypadSprint;
                    }
                    this.bJoypadBDown = bPressed;
                    inputState.bSprinting = this.bJoypadSprint;
                }
            }
            if (!inputState.bRunning) {
                this.bJoypadBDown = false;
                this.bJoypadSprint = false;
            }
        }
        else {
            inputState.isAiming = ((this.isAimKeyDown() || (Mouse.isButtonDownUICheck(1) && this.TimeRightPressed >= 0.15f)) && this.getPlayerNum() == 0 && StringUtils.isNullOrEmpty(this.getVariableString("BumpFallType")));
            if (Mouse.isButtonDown(1)) {
                this.TimeRightPressed += GameTime.getInstance().getRealworldSecondsSinceLastUpdate();
            }
            else {
                this.TimeRightPressed = 0.0f;
            }
            if (!this.isCharging) {
                inputState.isCharging = ((Mouse.isButtonDownUICheck(1) && this.TimeRightPressed >= 0.15f) || this.isAimKeyDown());
            }
            else {
                inputState.isCharging = (Mouse.isButtonDown(1) || this.isAimKeyDown());
            }
            final int key = Core.getInstance().getKey("Run");
            final int key2 = Core.getInstance().getKey("Sprint");
            if (this.isAllowRun()) {
                inputState.bRunning = GameKeyboard.isKeyDown(key);
            }
            if (this.isAllowSprint()) {
                if (!Core.OptiondblTapJogToSprint) {
                    if (GameKeyboard.isKeyDown(key2)) {
                        inputState.bSprinting = true;
                        this.pressedRunTimer = 1.0f;
                    }
                    else {
                        inputState.bSprinting = false;
                    }
                }
                else {
                    if (!GameKeyboard.wasKeyDown(key) && GameKeyboard.isKeyDown(key) && this.pressedRunTimer < 30.0f && this.pressedRun) {
                        inputState.bSprinting = true;
                    }
                    if (GameKeyboard.wasKeyDown(key) && !GameKeyboard.isKeyDown(key)) {
                        inputState.bSprinting = false;
                        this.pressedRun = true;
                    }
                    if (!inputState.bRunning) {
                        inputState.bSprinting = false;
                    }
                    if (this.pressedRun) {
                        ++this.pressedRunTimer;
                    }
                    if (this.pressedRunTimer > 30.0f) {
                        this.pressedRunTimer = 0.0f;
                        this.pressedRun = false;
                    }
                }
            }
            this.updateToggleToAim();
            if (inputState.bRunning || inputState.bSprinting) {
                this.setForceAim(false);
            }
            if (this.PlayerIndex == 0 && Core.getInstance().isToggleToRun()) {
                final boolean keyDown = GameKeyboard.isKeyDown(key);
                final boolean wasKeyDown = GameKeyboard.wasKeyDown(key);
                final long currentTimeMillis = System.currentTimeMillis();
                if (keyDown && !wasKeyDown) {
                    this.runKeyDownMS = currentTimeMillis;
                }
                else if (!keyDown && wasKeyDown && currentTimeMillis - this.runKeyDownMS < 500L) {
                    this.toggleForceRun();
                }
            }
            if (this.PlayerIndex == 0 && Core.getInstance().isToggleToSprint()) {
                final boolean keyDown2 = GameKeyboard.isKeyDown(key2);
                final boolean wasKeyDown2 = GameKeyboard.wasKeyDown(key2);
                final long currentTimeMillis2 = System.currentTimeMillis();
                if (keyDown2 && !wasKeyDown2) {
                    this.sprintKeyDownMS = currentTimeMillis2;
                }
                else if (!keyDown2 && wasKeyDown2 && currentTimeMillis2 - this.sprintKeyDownMS < 500L) {
                    this.toggleForceSprint();
                }
            }
            if (this.isForceAim()) {
                inputState.isAiming = true;
                inputState.isCharging = true;
            }
            if (this.isForceRun()) {
                inputState.bRunning = true;
            }
            if (this.isForceSprint()) {
                inputState.bSprinting = true;
            }
        }
    }
    
    public IsoZombie getClosestZombieToOtherZombie(final IsoZombie isoZombie) {
        IsoZombie isoZombie2 = null;
        final ArrayList<IsoZombie> list = new ArrayList<IsoZombie>();
        final ArrayList<IsoMovingObject> objectList = IsoWorld.instance.CurrentCell.getObjectList();
        for (int i = 0; i < objectList.size(); ++i) {
            final IsoMovingObject isoMovingObject = objectList.get(i);
            if (isoMovingObject != isoZombie) {
                if (isoMovingObject instanceof IsoZombie) {
                    list.add((IsoZombie)isoMovingObject);
                }
            }
        }
        float n = 0.0f;
        for (int j = 0; j < list.size(); ++j) {
            final IsoZombie isoZombie3 = list.get(j);
            final float distanceTo = IsoUtils.DistanceTo(isoZombie3.x, isoZombie3.y, isoZombie.x, isoZombie.y);
            if (isoZombie2 == null || distanceTo < n) {
                isoZombie2 = isoZombie3;
                n = distanceTo;
            }
        }
        return isoZombie2;
    }
    
    @Deprecated
    public IsoGameCharacter getClosestZombieDist() {
        final float n = 0.4f;
        boolean b = false;
        IsoPlayer.testHitPosition.x = this.x + this.getForwardDirection().x * n;
        IsoPlayer.testHitPosition.y = this.y + this.getForwardDirection().y * n;
        final HandWeapon weapon = this.getWeapon();
        final ArrayList<Object> list = new ArrayList<Object>();
        for (int i = (int)IsoPlayer.testHitPosition.x - (int)weapon.getMaxRange(); i <= (int)IsoPlayer.testHitPosition.x + (int)weapon.getMaxRange(); ++i) {
            for (int j = (int)IsoPlayer.testHitPosition.y - (int)weapon.getMaxRange(); j <= (int)IsoPlayer.testHitPosition.y + (int)weapon.getMaxRange(); ++j) {
                final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(i, j, this.z);
                if (gridSquare != null && gridSquare.getMovingObjects().size() > 0) {
                    for (int k = 0; k < gridSquare.getMovingObjects().size(); ++k) {
                        final IsoMovingObject isoMovingObject = gridSquare.getMovingObjects().get(k);
                        if (isoMovingObject instanceof IsoZombie) {
                            final Vector2 set = IsoPlayer.tempVector2_1.set(this.getX(), this.getY());
                            final Vector2 set2;
                            final Vector2 vector2 = set2 = IsoPlayer.tempVector2_2.set(isoMovingObject.getX(), isoMovingObject.getY());
                            set2.x -= set.x;
                            final Vector2 vector3 = vector2;
                            vector3.y -= set.y;
                            final Vector2 forwardDirection = this.getForwardDirection();
                            vector2.normalize();
                            forwardDirection.normalize();
                            if (vector2.dot(forwardDirection) >= weapon.getMinAngle() || isoMovingObject.isOnFloor()) {
                                b = true;
                            }
                            if (b && ((IsoZombie)isoMovingObject).Health > 0.0f) {
                                ((IsoZombie)isoMovingObject).setHitFromBehind(this.isBehind((IsoGameCharacter)isoMovingObject));
                                ((IsoZombie)isoMovingObject).setHitAngle(((IsoZombie)isoMovingObject).getForwardDirection());
                                ((IsoZombie)isoMovingObject).setPlayerAttackPosition(((IsoZombie)isoMovingObject).testDotSide(this));
                                if (IsoUtils.DistanceTo(isoMovingObject.x, isoMovingObject.y, this.x, this.y) < weapon.getMaxRange()) {
                                    list.add(isoMovingObject);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!list.isEmpty()) {
            Collections.sort(list, (Comparator<? super Object>)new Comparator<IsoGameCharacter>() {
                @Override
                public int compare(final IsoGameCharacter isoGameCharacter, final IsoGameCharacter isoGameCharacter2) {
                    final float distanceTo = IsoUtils.DistanceTo(isoGameCharacter.x, isoGameCharacter.y, IsoPlayer.testHitPosition.x, IsoPlayer.testHitPosition.y);
                    final float distanceTo2 = IsoUtils.DistanceTo(isoGameCharacter2.x, isoGameCharacter2.y, IsoPlayer.testHitPosition.x, IsoPlayer.testHitPosition.y);
                    if (distanceTo > distanceTo2) {
                        return 1;
                    }
                    if (distanceTo2 > distanceTo) {
                        return -1;
                    }
                    return 0;
                }
            });
            return (IsoZombie)list.get(0);
        }
        return null;
    }
    
    @Override
    public void hitConsequences(final HandWeapon handWeapon, final IsoGameCharacter isoGameCharacter, final boolean b, final float n, final boolean b2) {
        String variableString = isoGameCharacter.getVariableString("ZombieHitReaction");
        if ("Shot".equals(variableString)) {
            isoGameCharacter.setCriticalHit(Rand.Next(100) < ((IsoPlayer)isoGameCharacter).calculateCritChance(this));
        }
        this.setKnockedDown(isoGameCharacter.isCriticalHit());
        if (isoGameCharacter instanceof IsoPlayer) {
            if (!StringUtils.isNullOrEmpty(this.getHitReaction())) {
                this.actionContext.reportEvent("washitpvpagain");
            }
            this.actionContext.reportEvent("washitpvp");
            this.setVariable("hitpvp", true);
        }
        else {
            this.actionContext.reportEvent("washit");
        }
        if (!b) {
            if (!GameServer.bServer && (!GameClient.bClient || this.isLocalPlayer())) {
                this.BodyDamage.DamageFromWeapon(handWeapon);
            }
            else if (!GameServer.bServer && !this.isLocalPlayer()) {
                this.BodyDamage.splatBloodFloorBig();
            }
            if ("Bite".equals(variableString)) {
                final String testDotSide = this.testDotSide(isoGameCharacter);
                testDotSide.equals("FRONT");
                testDotSide.equals("BEHIND");
                if (testDotSide.equals("RIGHT")) {
                    variableString = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, variableString);
                }
                if (testDotSide.equals("LEFT")) {
                    variableString = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, variableString);
                }
                if (variableString != null && !"".equals(variableString)) {
                    this.setHitReaction(variableString);
                }
            }
            else if (!this.isKnockedDown()) {
                this.setHitReaction("HitReaction");
            }
            return;
        }
        if (GameServer.bServer) {
            return;
        }
        isoGameCharacter.xp.AddXP(PerkFactory.Perks.Strength, 2.0f);
        this.setHitForce(Math.min(0.5f, this.getHitForce()));
        this.setHitReaction("HitReaction");
        this.setHitFromBehind("BEHIND".equals(this.testDotSide(isoGameCharacter)));
    }
    
    private HandWeapon getWeapon() {
        if (this.getPrimaryHandItem() instanceof HandWeapon) {
            return (HandWeapon)this.getPrimaryHandItem();
        }
        if (this.getSecondaryHandItem() instanceof HandWeapon) {
            return (HandWeapon)this.getSecondaryHandItem();
        }
        return (HandWeapon)InventoryItemFactory.CreateItem("BareHands");
    }
    
    private void updateMechanicsItems() {
        if (GameServer.bServer || this.mechanicsItem.isEmpty()) {
            return;
        }
        final Iterator<Long> iterator = this.mechanicsItem.keySet().iterator();
        final ArrayList<Long> list = new ArrayList<Long>();
        while (iterator.hasNext()) {
            final Long n = iterator.next();
            if (GameTime.getInstance().getCalender().getTimeInMillis() > this.mechanicsItem.get(n) + 86400000L) {
                list.add(n);
            }
        }
        for (int i = 0; i < list.size(); ++i) {
            this.mechanicsItem.remove(list.get(i));
        }
    }
    
    private void enterExitVehicle() {
        final boolean b = this.PlayerIndex == 0 && GameKeyboard.isKeyDown(Core.getInstance().getKey("Interact"));
        if (b) {
            this.bUseVehicle = true;
            this.useVehicleDuration += GameTime.instance.getRealworldSecondsSinceLastUpdate();
        }
        if (!this.bUsedVehicle && this.bUseVehicle && (!b || this.useVehicleDuration > 0.5f)) {
            this.bUsedVehicle = true;
            if (this.getVehicle() != null) {
                LuaEventManager.triggerEvent("OnUseVehicle", this, this.getVehicle(), this.useVehicleDuration > 0.5f);
            }
            else {
                for (int i = 0; i < this.getCell().vehicles.size(); ++i) {
                    final BaseVehicle baseVehicle = this.getCell().vehicles.get(i);
                    if (baseVehicle.getUseablePart(this) != null) {
                        LuaEventManager.triggerEvent("OnUseVehicle", this, baseVehicle, this.useVehicleDuration > 0.5f);
                        break;
                    }
                }
            }
        }
        if (!b) {
            this.bUseVehicle = false;
            this.bUsedVehicle = false;
            this.useVehicleDuration = 0.0f;
        }
    }
    
    private void checkActionGroup() {
        final ActionGroup group = this.actionContext.getGroup();
        if (this.getVehicle() == null) {
            final ActionGroup actionGroup = ActionGroup.getActionGroup("player");
            if (group != actionGroup) {
                this.advancedAnimator.OnAnimDataChanged(false);
                this.initializeStates();
                this.actionContext.setGroup(actionGroup);
                this.clearVariable("bEnteringVehicle");
                this.clearVariable("EnterAnimationFinished");
                this.clearVariable("bExitingVehicle");
                this.clearVariable("ExitAnimationFinished");
                this.clearVariable("bSwitchingSeat");
                this.clearVariable("SwitchSeatAnimationFinished");
                this.setHitReaction("");
            }
        }
        else {
            final ActionGroup actionGroup2 = ActionGroup.getActionGroup("player-vehicle");
            if (group != actionGroup2) {
                this.advancedAnimator.OnAnimDataChanged(false);
                this.initializeStates();
                this.actionContext.setGroup(actionGroup2);
            }
        }
    }
    
    public BaseVehicle getUseableVehicle() {
        if (this.getVehicle() != null) {
            return null;
        }
        final int n = ((int)this.x - 4) / 10 - 1;
        final int n2 = ((int)this.y - 4) / 10 - 1;
        final int n3 = (int)Math.ceil((this.x + 4.0f) / 10.0f) + 1;
        for (int n4 = (int)Math.ceil((this.y + 4.0f) / 10.0f) + 1, i = n2; i < n4; ++i) {
            for (int j = n; j < n3; ++j) {
                final IsoChunk isoChunk = GameServer.bServer ? ServerMap.instance.getChunk(j, i) : IsoWorld.instance.CurrentCell.getChunkForGridSquare(j * 10, i * 10, 0);
                if (isoChunk != null) {
                    for (int k = 0; k < isoChunk.vehicles.size(); ++k) {
                        final BaseVehicle baseVehicle = isoChunk.vehicles.get(k);
                        if (baseVehicle.getUseablePart(this) != null || baseVehicle.getBestSeat(this) != -1) {
                            return baseVehicle;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    public Boolean isNearVehicle() {
        if (this.getVehicle() != null) {
            return false;
        }
        final int n = ((int)this.x - 4) / 10 - 1;
        final int n2 = ((int)this.y - 4) / 10 - 1;
        final int n3 = (int)Math.ceil((this.x + 4.0f) / 10.0f) + 1;
        for (int n4 = (int)Math.ceil((this.y + 4.0f) / 10.0f) + 1, i = n2; i < n4; ++i) {
            for (int j = n; j < n3; ++j) {
                final IsoChunk isoChunk = GameServer.bServer ? ServerMap.instance.getChunk(j, i) : IsoWorld.instance.CurrentCell.getChunkForGridSquare(j * 10, i * 10, 0);
                if (isoChunk != null) {
                    for (int k = 0; k < isoChunk.vehicles.size(); ++k) {
                        if (isoChunk.vehicles.get(k).DistTo(this) < 3.5) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public BaseVehicle getNearVehicle() {
        if (this.getVehicle() != null) {
            return null;
        }
        final int n = ((int)this.x - 4) / 10 - 1;
        final int n2 = ((int)this.y - 4) / 10 - 1;
        final int n3 = (int)Math.ceil((this.x + 4.0f) / 10.0f) + 1;
        for (int n4 = (int)Math.ceil((this.y + 4.0f) / 10.0f) + 1, i = n2; i < n4; ++i) {
            for (int j = n; j < n3; ++j) {
                final IsoChunk isoChunk = GameServer.bServer ? ServerMap.instance.getChunk(j, i) : IsoWorld.instance.CurrentCell.getChunk(j, i);
                if (isoChunk != null) {
                    for (int k = 0; k < isoChunk.vehicles.size(); ++k) {
                        final BaseVehicle baseVehicle = isoChunk.vehicles.get(k);
                        if ((int)this.getZ() == (int)baseVehicle.getZ()) {
                            if (!this.isLocalPlayer() || baseVehicle.getTargetAlpha(this.PlayerIndex) != 0.0f) {
                                if (this.DistToSquared((float)(int)baseVehicle.x, (float)(int)baseVehicle.y) < 16.0f) {
                                    if (PolygonalMap2.instance.intersectLineWithVehicle(this.x, this.y, this.x + this.getForwardDirection().x * 4.0f, this.y + this.getForwardDirection().y * 4.0f, baseVehicle, IsoPlayer.tempVector2)) {
                                        if (!PolygonalMap2.instance.lineClearCollide(this.x, this.y, IsoPlayer.tempVector2.x, IsoPlayer.tempVector2.y, (int)this.z, baseVehicle, false, true)) {
                                            return baseVehicle;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    private void updateWhileInVehicle() {
        this.bLookingWhileInVehicle = false;
        final ActionGroup group = this.actionContext.getGroup();
        final ActionGroup actionGroup = ActionGroup.getActionGroup("player-vehicle");
        if (group != actionGroup) {
            this.advancedAnimator.OnAnimDataChanged(false);
            this.initializeStates();
            this.actionContext.setGroup(actionGroup);
        }
        if (GameClient.bClient && this.getVehicle().getSeat(this) == -1) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getUsername()));
            this.setVehicle(null);
            return;
        }
        this.dirtyRecalcGridStackTime = 10.0f;
        if (this.getVehicle().isDriver(this)) {
            this.getVehicle().updatePhysics();
            boolean b = true;
            if (this.isAiming() && WeaponType.getWeaponType(this).equals(WeaponType.firearm)) {
                b = false;
            }
            if (this.getVariableBoolean("isLoading")) {
                b = false;
            }
            if (b) {
                this.getVehicle().updateControls();
            }
        }
        else if (GameClient.connection != null) {
            PassengerMap.updatePassenger(this);
        }
        this.fallTime = 0.0f;
        this.bSeenThisFrame = false;
        this.bCouldBeSeenThisFrame = false;
        this.closestZombie = 1000000.0f;
        this.setBeenMovingFor(this.getBeenMovingFor() - 0.625f * GameTime.getInstance().getMultiplier());
        if (!this.Asleep) {
            final float n = (float)ZomboidGlobals.SittingEnduranceMultiplier * (1.0f - this.stats.fatigue) * GameTime.instance.getMultiplier();
            final Stats stats = this.stats;
            stats.endurance += (float)(ZomboidGlobals.ImobileEnduranceReduce * SandboxOptions.instance.getEnduranceRegenMultiplier() * this.getRecoveryMod() * n);
            this.stats.endurance = PZMath.clamp(this.stats.endurance, 0.0f, 1.0f);
        }
        this.updateToggleToAim();
        if (this.vehicle != null) {
            final Vector3f forwardVector = this.vehicle.getForwardVector(IsoPlayer.tempVector3f);
            boolean aimControlActive = this.isAimControlActive();
            if (this.PlayerIndex == 0) {
                if (Mouse.isButtonDown(1)) {
                    this.TimeRightPressed += GameTime.getInstance().getRealworldSecondsSinceLastUpdate();
                }
                else {
                    this.TimeRightPressed = 0.0f;
                }
                aimControlActive |= (Mouse.isButtonDownUICheck(1) && this.TimeRightPressed >= 0.15f);
            }
            if (!aimControlActive && this.isCurrentState(IdleState.instance())) {
                this.setForwardDirection(forwardVector.x, forwardVector.z);
                this.getForwardDirection().normalize();
            }
            if (this.lastAngle.x != this.getForwardDirection().x || this.lastAngle.y != this.getForwardDirection().y) {
                this.dirtyRecalcGridStackTime = 10.0f;
            }
            this.DirectionFromVector(this.getForwardDirection());
            final AnimationPlayer animationPlayer = this.getAnimationPlayer();
            if (animationPlayer != null && animationPlayer.isReady()) {
                animationPlayer.SetForceDir(this.getForwardDirection());
                this.dir = IsoDirections.fromAngle(IsoPlayer.tempVector2.setLengthAndDirection(animationPlayer.getAngle() + animationPlayer.getTwistAngle(), 1.0f));
            }
            boolean b2 = false;
            final VehiclePart passengerDoor = this.vehicle.getPassengerDoor(this.vehicle.getSeat(this));
            if (passengerDoor != null) {
                final VehicleWindow window = passengerDoor.findWindow();
                if (window != null && !window.isHittable()) {
                    b2 = true;
                }
            }
            if (b2) {
                this.attackWhileInVehicle();
            }
            else if (aimControlActive) {
                this.bLookingWhileInVehicle = true;
                this.setAngleFromAim();
            }
            else {
                this.checkJoypadIgnoreAimUntilCentered();
                this.setIsAiming(false);
            }
        }
        this.updateCursorVisibility();
    }
    
    private void attackWhileInVehicle() {
        this.setIsAiming(false);
        boolean b = false;
        boolean meleePressed = false;
        if (GameWindow.ActivatedJoyPad != null && this.JoypadBind != -1) {
            if (!this.bJoypadMovementActive) {
                return;
            }
            if (this.isChargingLT && !JoypadManager.instance.isLTPressed(this.JoypadBind)) {
                meleePressed = true;
            }
            else {
                b = (this.isCharging && !JoypadManager.instance.isRTPressed(this.JoypadBind));
            }
            float aimingAxisX = JoypadManager.instance.getAimingAxisX(this.JoypadBind);
            float aimingAxisY = JoypadManager.instance.getAimingAxisY(this.JoypadBind);
            if (this.bJoypadIgnoreAimUntilCentered) {
                if (aimingAxisX == 0.0f && aimingAxisY == 0.0f) {
                    this.bJoypadIgnoreAimUntilCentered = false;
                }
                else {
                    aimingAxisY = (aimingAxisX = 0.0f);
                }
            }
            this.setIsAiming(aimingAxisX * aimingAxisX + aimingAxisY * aimingAxisY >= 0.09f);
            this.isCharging = (this.isAiming() && JoypadManager.instance.isRTPressed(this.JoypadBind));
            this.isChargingLT = (this.isAiming() && JoypadManager.instance.isLTPressed(this.JoypadBind));
        }
        else {
            final boolean aimKeyDown = this.isAimKeyDown();
            this.setIsAiming(aimKeyDown || (Mouse.isButtonDownUICheck(1) && this.TimeRightPressed >= 0.15f));
            if (this.isCharging) {
                this.isCharging = (aimKeyDown || Mouse.isButtonDown(1));
            }
            else {
                this.isCharging = (aimKeyDown || (Mouse.isButtonDownUICheck(1) && this.TimeRightPressed >= 0.15f));
            }
            if (this.isForceAim()) {
                this.setIsAiming(true);
                this.isCharging = true;
            }
            if (GameKeyboard.isKeyDown(Core.getInstance().getKey("Melee")) && this.authorizeMeleeAction) {
                meleePressed = true;
            }
            else {
                b = (this.isCharging && Mouse.isButtonDownUICheck(0));
                if (b) {
                    this.setIsAiming(true);
                }
            }
        }
        if (!this.isCharging && !this.isChargingLT) {
            this.chargeTime = 0.0f;
        }
        if (!this.isAiming() || this.bBannedAttacking || !this.CanAttack()) {
            return;
        }
        this.chargeTime += GameTime.instance.getMultiplier();
        this.useChargeTime = this.chargeTime;
        this.m_meleePressed = meleePressed;
        this.setAngleFromAim();
        if (meleePressed) {
            this.setDoShove(this.sprite.Animate = true);
            this.AttemptAttack(this.useChargeTime);
            this.useChargeTime = 0.0f;
            this.chargeTime = 0.0f;
        }
        else if (b) {
            this.sprite.Animate = true;
            if (this.getRecoilDelay() <= 0.0f) {
                this.AttemptAttack(this.useChargeTime);
            }
            this.useChargeTime = 0.0f;
            this.chargeTime = 0.0f;
        }
    }
    
    private void setAngleFromAim() {
        final Vector2 tempVector2 = IsoPlayer.tempVector2;
        if (GameWindow.ActivatedJoyPad != null && this.JoypadBind != -1) {
            this.getControllerAimDir(tempVector2);
        }
        else {
            tempVector2.set(this.getX(), this.getY());
            final int x = Mouse.getX();
            final int y = Mouse.getY();
            final Vector2 vector2 = tempVector2;
            vector2.x -= IsoUtils.XToIso((float)x, y + 55.0f * this.def.getScaleY(), this.getZ());
            final Vector2 vector3 = tempVector2;
            vector3.y -= IsoUtils.YToIso((float)x, y + 55.0f * this.def.getScaleY(), this.getZ());
            tempVector2.x = -tempVector2.x;
            tempVector2.y = -tempVector2.y;
        }
        if (tempVector2.getLengthSquared() > 0.0f) {
            tempVector2.normalize();
            this.DirectionFromVector(tempVector2);
            this.setForwardDirection(tempVector2);
            if (this.lastAngle.x != tempVector2.x || this.lastAngle.y != tempVector2.y) {
                this.lastAngle.x = tempVector2.x;
                this.lastAngle.y = tempVector2.y;
                this.dirtyRecalcGridStackTime = 10.0f;
            }
        }
    }
    
    private void updateTorchStrength() {
        if (this.getTorchStrength() > 0.0f || this.flickTorch) {
            final DrainableComboItem drainableComboItem = Type.tryCastTo(this.getActiveLightItem(), DrainableComboItem.class);
            if (drainableComboItem == null) {
                return;
            }
            if (Rand.Next(600 - (int)(0.4 / drainableComboItem.getUsedDelta() * 100.0)) == 0) {
                this.flickTorch = true;
            }
            this.flickTorch = false;
            if (this.flickTorch) {
                if (Rand.Next(6) == 0) {
                    drainableComboItem.setActivated(false);
                }
                else {
                    drainableComboItem.setActivated(true);
                }
                if (Rand.Next(40) == 0) {
                    this.flickTorch = false;
                    drainableComboItem.setActivated(true);
                }
            }
        }
    }
    
    public IsoCell getCell() {
        return IsoWorld.instance.CurrentCell;
    }
    
    public void calculateContext() {
        final float x = this.x;
        final float y = this.y;
        final float x2 = this.x;
        final IsoGridSquare[] array = new IsoGridSquare[4];
        if (this.dir == IsoDirections.N) {
            array[2] = this.getCell().getGridSquare(x - 1.0f, y - 1.0f, x2);
            array[1] = this.getCell().getGridSquare(x, y - 1.0f, x2);
            array[3] = this.getCell().getGridSquare(x + 1.0f, y - 1.0f, x2);
        }
        else if (this.dir == IsoDirections.NE) {
            array[2] = this.getCell().getGridSquare(x, y - 1.0f, x2);
            array[1] = this.getCell().getGridSquare(x + 1.0f, y - 1.0f, x2);
            array[3] = this.getCell().getGridSquare(x + 1.0f, y, x2);
        }
        else if (this.dir == IsoDirections.E) {
            array[2] = this.getCell().getGridSquare(x + 1.0f, y - 1.0f, x2);
            array[1] = this.getCell().getGridSquare(x + 1.0f, y, x2);
            array[3] = this.getCell().getGridSquare(x + 1.0f, y + 1.0f, x2);
        }
        else if (this.dir == IsoDirections.SE) {
            array[2] = this.getCell().getGridSquare(x + 1.0f, y, x2);
            array[1] = this.getCell().getGridSquare(x + 1.0f, y + 1.0f, x2);
            array[3] = this.getCell().getGridSquare(x, y + 1.0f, x2);
        }
        else if (this.dir == IsoDirections.S) {
            array[2] = this.getCell().getGridSquare(x + 1.0f, y + 1.0f, x2);
            array[1] = this.getCell().getGridSquare(x, y + 1.0f, x2);
            array[3] = this.getCell().getGridSquare(x - 1.0f, y + 1.0f, x2);
        }
        else if (this.dir == IsoDirections.SW) {
            array[2] = this.getCell().getGridSquare(x, y + 1.0f, x2);
            array[1] = this.getCell().getGridSquare(x - 1.0f, y + 1.0f, x2);
            array[3] = this.getCell().getGridSquare(x - 1.0f, y, x2);
        }
        else if (this.dir == IsoDirections.W) {
            array[2] = this.getCell().getGridSquare(x - 1.0f, y + 1.0f, x2);
            array[1] = this.getCell().getGridSquare(x - 1.0f, y, x2);
            array[3] = this.getCell().getGridSquare(x - 1.0f, y - 1.0f, x2);
        }
        else if (this.dir == IsoDirections.NW) {
            array[2] = this.getCell().getGridSquare(x - 1.0f, y, x2);
            array[1] = this.getCell().getGridSquare(x - 1.0f, y - 1.0f, x2);
            array[3] = this.getCell().getGridSquare(x, y - 1.0f, x2);
        }
        array[0] = this.current;
        for (int i = 0; i < 4; ++i) {
            if (array[i] == null) {}
        }
    }
    
    public boolean isSafeToClimbOver(final IsoDirections isoDirections) {
        IsoGridSquare isoGridSquare = null;
        switch (isoDirections) {
            case N: {
                isoGridSquare = this.getCell().getGridSquare(this.x, this.y - 1.0f, this.z);
                break;
            }
            case S: {
                isoGridSquare = this.getCell().getGridSquare(this.x, this.y + 1.0f, this.z);
                break;
            }
            case W: {
                isoGridSquare = this.getCell().getGridSquare(this.x - 1.0f, this.y, this.z);
                break;
            }
            case E: {
                isoGridSquare = this.getCell().getGridSquare(this.x + 1.0f, this.y, this.z);
                break;
            }
            default: {
                return false;
            }
        }
        return isoGridSquare != null && !isoGridSquare.Is(IsoFlagType.water) && (isoGridSquare.TreatAsSolidFloor() || isoGridSquare.HasStairsBelow());
    }
    
    public boolean doContext(final IsoDirections isoDirections) {
        if (this.isIgnoreContextKey()) {
            return false;
        }
        if (this.isBlockMovement()) {
            return false;
        }
        for (int i = 0; i < this.getCell().vehicles.size(); ++i) {
            if (this.getCell().vehicles.get(i).getUseablePart(this) != null) {
                return false;
            }
        }
        final float n = this.x - (int)this.x;
        final float n2 = this.y - (int)this.y;
        final IsoDirections max = IsoDirections.Max;
        IsoDirections isoDirections2 = IsoDirections.Max;
        IsoDirections isoDirections3;
        if (isoDirections == IsoDirections.NW) {
            if (n2 < n) {
                if (this.doContextNSWE(IsoDirections.N)) {
                    return true;
                }
                if (this.doContextNSWE(IsoDirections.W)) {
                    return true;
                }
                isoDirections3 = IsoDirections.S;
                isoDirections2 = IsoDirections.E;
            }
            else {
                if (this.doContextNSWE(IsoDirections.W)) {
                    return true;
                }
                if (this.doContextNSWE(IsoDirections.N)) {
                    return true;
                }
                isoDirections3 = IsoDirections.E;
                isoDirections2 = IsoDirections.S;
            }
        }
        else if (isoDirections == IsoDirections.NE) {
            if (n2 < 1.0f - n) {
                if (this.doContextNSWE(IsoDirections.N)) {
                    return true;
                }
                if (this.doContextNSWE(IsoDirections.E)) {
                    return true;
                }
                isoDirections3 = IsoDirections.S;
                isoDirections2 = IsoDirections.W;
            }
            else {
                if (this.doContextNSWE(IsoDirections.E)) {
                    return true;
                }
                if (this.doContextNSWE(IsoDirections.N)) {
                    return true;
                }
                isoDirections3 = IsoDirections.W;
                isoDirections2 = IsoDirections.S;
            }
        }
        else if (isoDirections == IsoDirections.SE) {
            if (1.0f - n2 < 1.0f - n) {
                if (this.doContextNSWE(IsoDirections.S)) {
                    return true;
                }
                if (this.doContextNSWE(IsoDirections.E)) {
                    return true;
                }
                isoDirections3 = IsoDirections.N;
                isoDirections2 = IsoDirections.W;
            }
            else {
                if (this.doContextNSWE(IsoDirections.E)) {
                    return true;
                }
                if (this.doContextNSWE(IsoDirections.S)) {
                    return true;
                }
                isoDirections3 = IsoDirections.W;
                isoDirections2 = IsoDirections.N;
            }
        }
        else if (isoDirections == IsoDirections.SW) {
            if (1.0f - n2 < n) {
                if (this.doContextNSWE(IsoDirections.S)) {
                    return true;
                }
                if (this.doContextNSWE(IsoDirections.W)) {
                    return true;
                }
                isoDirections3 = IsoDirections.N;
                isoDirections2 = IsoDirections.E;
            }
            else {
                if (this.doContextNSWE(IsoDirections.W)) {
                    return true;
                }
                if (this.doContextNSWE(IsoDirections.S)) {
                    return true;
                }
                isoDirections3 = IsoDirections.E;
                isoDirections2 = IsoDirections.N;
            }
        }
        else {
            if (this.doContextNSWE(isoDirections)) {
                return true;
            }
            isoDirections3 = isoDirections.RotLeft(4);
        }
        if (isoDirections3 != IsoDirections.Max) {
            final IsoObject contextDoorOrWindowOrWindowFrame = this.getContextDoorOrWindowOrWindowFrame(isoDirections3);
            if (contextDoorOrWindowOrWindowFrame != null) {
                this.doContextDoorOrWindowOrWindowFrame(isoDirections3, contextDoorOrWindowOrWindowFrame);
                return true;
            }
        }
        if (isoDirections2 != IsoDirections.Max) {
            final IsoObject contextDoorOrWindowOrWindowFrame2 = this.getContextDoorOrWindowOrWindowFrame(isoDirections2);
            if (contextDoorOrWindowOrWindowFrame2 != null) {
                this.doContextDoorOrWindowOrWindowFrame(isoDirections2, contextDoorOrWindowOrWindowFrame2);
                return true;
            }
        }
        return false;
    }
    
    private boolean doContextNSWE(final IsoDirections isoDirections) {
        assert isoDirections == IsoDirections.E;
        if (this.current == null) {
            return false;
        }
        if (isoDirections == IsoDirections.N && this.current.Is(IsoFlagType.climbSheetN) && this.canClimbSheetRope(this.current)) {
            this.climbSheetRope();
            return true;
        }
        if (isoDirections == IsoDirections.S && this.current.Is(IsoFlagType.climbSheetS) && this.canClimbSheetRope(this.current)) {
            this.climbSheetRope();
            return true;
        }
        if (isoDirections == IsoDirections.W && this.current.Is(IsoFlagType.climbSheetW) && this.canClimbSheetRope(this.current)) {
            this.climbSheetRope();
            return true;
        }
        if (isoDirections == IsoDirections.E && this.current.Is(IsoFlagType.climbSheetE) && this.canClimbSheetRope(this.current)) {
            this.climbSheetRope();
            return true;
        }
        final IsoGridSquare isoGridSquare = this.current.nav[isoDirections.index()];
        final boolean b = IsoWindow.isTopOfSheetRopeHere(isoGridSquare) && this.canClimbDownSheetRope(isoGridSquare);
        final IsoObject contextDoorOrWindowOrWindowFrame = this.getContextDoorOrWindowOrWindowFrame(isoDirections);
        if (contextDoorOrWindowOrWindowFrame != null) {
            this.doContextDoorOrWindowOrWindowFrame(isoDirections, contextDoorOrWindowOrWindowFrame);
            return true;
        }
        if (GameKeyboard.isKeyDown(42) && this.current != null && this.ticksSincePressedMovement > 15.0f) {
            final IsoObject door = this.current.getDoor(true);
            if (door instanceof IsoDoor && ((IsoDoor)door).isFacingSheet(this)) {
                ((IsoDoor)door).toggleCurtain();
                return true;
            }
            final IsoObject door2 = this.current.getDoor(false);
            if (door2 instanceof IsoDoor && ((IsoDoor)door2).isFacingSheet(this)) {
                ((IsoDoor)door2).toggleCurtain();
                return true;
            }
            if (isoDirections == IsoDirections.E) {
                final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(this.x + 1.0f, this.y, this.z);
                final IsoObject isoObject = (gridSquare != null) ? gridSquare.getDoor(true) : null;
                if (isoObject instanceof IsoDoor && ((IsoDoor)isoObject).isFacingSheet(this)) {
                    ((IsoDoor)isoObject).toggleCurtain();
                    return true;
                }
            }
            if (isoDirections == IsoDirections.S) {
                final IsoGridSquare gridSquare2 = IsoWorld.instance.CurrentCell.getGridSquare(this.x, this.y + 1.0f, this.z);
                final IsoObject isoObject2 = (gridSquare2 != null) ? gridSquare2.getDoor(false) : null;
                if (isoObject2 instanceof IsoDoor && ((IsoDoor)isoObject2).isFacingSheet(this)) {
                    ((IsoDoor)isoObject2).toggleCurtain();
                    return true;
                }
            }
        }
        boolean safeToClimbOver = this.isSafeToClimbOver(isoDirections);
        if (this.z > 0.0f && b) {
            safeToClimbOver = true;
        }
        if (this.timePressedContext < 0.5f && !safeToClimbOver) {
            return false;
        }
        if (this.ignoreAutoVault) {
            return false;
        }
        if (isoDirections == IsoDirections.N && this.getCurrentSquare().Is(IsoFlagType.HoppableN)) {
            this.climbOverFence(isoDirections);
            return true;
        }
        if (isoDirections == IsoDirections.W && this.getCurrentSquare().Is(IsoFlagType.HoppableW)) {
            this.climbOverFence(isoDirections);
            return true;
        }
        if (isoDirections == IsoDirections.S && IsoWorld.instance.CurrentCell.getGridSquare(this.x, this.y + 1.0f, this.z) != null && IsoWorld.instance.CurrentCell.getGridSquare(this.x, this.y + 1.0f, this.z).Is(IsoFlagType.HoppableN)) {
            this.climbOverFence(isoDirections);
            return true;
        }
        if (isoDirections == IsoDirections.E && IsoWorld.instance.CurrentCell.getGridSquare(this.x + 1.0f, this.y, this.z) != null && IsoWorld.instance.CurrentCell.getGridSquare(this.x + 1.0f, this.y, this.z).Is(IsoFlagType.HoppableW)) {
            this.climbOverFence(isoDirections);
            return true;
        }
        return this.climbOverWall(isoDirections);
    }
    
    public IsoObject getContextDoorOrWindowOrWindowFrame(final IsoDirections isoDirections) {
        if (this.current == null || isoDirections == null) {
            return null;
        }
        final IsoGridSquare isoGridSquare = this.current.nav[isoDirections.index()];
        IsoObject isoObject = null;
        switch (isoDirections) {
            case N: {
                final IsoObject openDoor = this.current.getOpenDoor(isoDirections);
                if (openDoor != null) {
                    return openDoor;
                }
                final IsoObject doorOrWindowOrWindowFrame = this.current.getDoorOrWindowOrWindowFrame(isoDirections, true);
                if (doorOrWindowOrWindowFrame != null) {
                    return doorOrWindowOrWindowFrame;
                }
                isoObject = this.current.getDoor(true);
                if (isoObject != null) {
                    return isoObject;
                }
                if (isoGridSquare == null) {
                    break;
                }
                if (this.current.isBlockedTo(isoGridSquare)) {
                    break;
                }
                isoObject = isoGridSquare.getOpenDoor(IsoDirections.S);
                break;
            }
            case S: {
                isoObject = this.current.getOpenDoor(isoDirections);
                if (isoObject != null) {
                    return isoObject;
                }
                if (isoGridSquare == null) {
                    break;
                }
                final IsoObject doorOrWindowOrWindowFrame2 = isoGridSquare.getDoorOrWindowOrWindowFrame(IsoDirections.N, this.current.isBlockedTo(isoGridSquare));
                if (doorOrWindowOrWindowFrame2 != null) {
                    return doorOrWindowOrWindowFrame2;
                }
                isoObject = isoGridSquare.getDoor(true);
                break;
            }
            case W: {
                final IsoObject openDoor2 = this.current.getOpenDoor(isoDirections);
                if (openDoor2 != null) {
                    return openDoor2;
                }
                final IsoObject doorOrWindowOrWindowFrame3 = this.current.getDoorOrWindowOrWindowFrame(isoDirections, true);
                if (doorOrWindowOrWindowFrame3 != null) {
                    return doorOrWindowOrWindowFrame3;
                }
                isoObject = this.current.getDoor(false);
                if (isoObject != null) {
                    return isoObject;
                }
                if (isoGridSquare == null) {
                    break;
                }
                if (this.current.isBlockedTo(isoGridSquare)) {
                    break;
                }
                isoObject = isoGridSquare.getOpenDoor(IsoDirections.E);
                break;
            }
            case E: {
                isoObject = this.current.getOpenDoor(isoDirections);
                if (isoObject != null) {
                    return isoObject;
                }
                if (isoGridSquare == null) {
                    break;
                }
                final IsoObject doorOrWindowOrWindowFrame4 = isoGridSquare.getDoorOrWindowOrWindowFrame(IsoDirections.W, this.current.isBlockedTo(isoGridSquare));
                if (doorOrWindowOrWindowFrame4 != null) {
                    return doorOrWindowOrWindowFrame4;
                }
                isoObject = isoGridSquare.getDoor(false);
                break;
            }
        }
        return isoObject;
    }
    
    private void doContextDoorOrWindowOrWindowFrame(final IsoDirections isoDirections, final IsoObject isoObject) {
        final IsoGridSquare isoGridSquare = this.current.nav[isoDirections.index()];
        final boolean b = IsoWindow.isTopOfSheetRopeHere(isoGridSquare) && this.canClimbDownSheetRope(isoGridSquare);
        if (isoObject instanceof IsoDoor) {
            final IsoDoor isoDoor = (IsoDoor)isoObject;
            if (GameKeyboard.isKeyDown(42) && isoDoor.HasCurtains() != null && isoDoor.isFacingSheet(this) && this.ticksSincePressedMovement > 15.0f) {
                isoDoor.toggleCurtain();
            }
            else if (this.timePressedContext >= 0.5f) {
                if (isoDoor.isHoppable() && !this.isIgnoreAutoVault()) {
                    this.climbOverFence(isoDirections);
                }
                else {
                    isoDoor.ToggleDoor(this);
                }
            }
            else {
                isoDoor.ToggleDoor(this);
            }
        }
        else if (isoObject instanceof IsoThumpable && ((IsoThumpable)isoObject).isDoor()) {
            final IsoThumpable isoThumpable = (IsoThumpable)isoObject;
            if (this.timePressedContext >= 0.5f) {
                if (isoThumpable.isHoppable() && !this.isIgnoreAutoVault()) {
                    this.climbOverFence(isoDirections);
                }
                else {
                    isoThumpable.ToggleDoor(this);
                }
            }
            else {
                isoThumpable.ToggleDoor(this);
            }
        }
        else if (isoObject instanceof IsoWindow && !isoObject.getSquare().getProperties().Is(IsoFlagType.makeWindowInvincible)) {
            final IsoWindow isoWindow = (IsoWindow)isoObject;
            if (GameKeyboard.isKeyDown(42)) {
                final IsoCurtain hasCurtains = isoWindow.HasCurtains();
                if (hasCurtains != null && this.current != null && !hasCurtains.getSquare().isBlockedTo(this.current)) {
                    hasCurtains.ToggleDoor(this);
                }
            }
            else if (this.timePressedContext >= 0.5f) {
                if (isoWindow.canClimbThrough(this)) {
                    this.climbThroughWindow(isoWindow);
                }
                else if (!isoWindow.PermaLocked && !isoWindow.isBarricaded() && !isoWindow.IsOpen()) {
                    this.openWindow(isoWindow);
                }
            }
            else if (isoWindow.Health > 0 && !isoWindow.isDestroyed()) {
                final IsoBarricade barricadeForCharacter = isoWindow.getBarricadeForCharacter(this);
                if (!isoWindow.open && barricadeForCharacter == null) {
                    this.openWindow(isoWindow);
                }
                else if (barricadeForCharacter == null) {
                    this.closeWindow(isoWindow);
                }
            }
            else if (isoWindow.isGlassRemoved()) {
                if (!this.isSafeToClimbOver(isoDirections) && !isoObject.getSquare().haveSheetRope && !b) {
                    return;
                }
                if (!isoWindow.isBarricaded()) {
                    this.climbThroughWindow(isoWindow);
                }
            }
        }
        else if (isoObject instanceof IsoThumpable && !isoObject.getSquare().getProperties().Is(IsoFlagType.makeWindowInvincible)) {
            final IsoThumpable isoThumpable2 = (IsoThumpable)isoObject;
            if (GameKeyboard.isKeyDown(42)) {
                final IsoCurtain hasCurtains2 = isoThumpable2.HasCurtains();
                if (hasCurtains2 != null && this.current != null && !hasCurtains2.getSquare().isBlockedTo(this.current)) {
                    hasCurtains2.ToggleDoor(this);
                }
            }
            else if (this.timePressedContext >= 0.5f) {
                if (isoThumpable2.canClimbThrough(this)) {
                    this.climbThroughWindow(isoThumpable2);
                }
            }
            else {
                if (!this.isSafeToClimbOver(isoDirections) && !isoObject.getSquare().haveSheetRope && !b) {
                    return;
                }
                if (isoThumpable2.canClimbThrough(this)) {
                    this.climbThroughWindow(isoThumpable2);
                }
            }
        }
        else if (IsoWindowFrame.isWindowFrame(isoObject)) {
            if (GameKeyboard.isKeyDown(42)) {
                final IsoCurtain curtain = IsoWindowFrame.getCurtain(isoObject);
                if (curtain != null && this.current != null && !curtain.getSquare().isBlockedTo(this.current)) {
                    curtain.ToggleDoor(this);
                }
            }
            else if ((this.timePressedContext >= 0.5f || this.isSafeToClimbOver(isoDirections) || b) && IsoWindowFrame.canClimbThrough(isoObject, this)) {
                this.climbThroughWindowFrame(isoObject);
            }
        }
    }
    
    public boolean hopFence(final IsoDirections isoDirections, final boolean b) {
        final float n = this.x - (int)this.x;
        final float n2 = this.y - (int)this.y;
        if (isoDirections == IsoDirections.NW) {
            if (n2 < n) {
                return this.hopFence(IsoDirections.N, b) || this.hopFence(IsoDirections.W, b);
            }
            return this.hopFence(IsoDirections.W, b) || this.hopFence(IsoDirections.N, b);
        }
        else if (isoDirections == IsoDirections.NE) {
            if (n2 < 1.0f - n) {
                return this.hopFence(IsoDirections.N, b) || this.hopFence(IsoDirections.E, b);
            }
            return this.hopFence(IsoDirections.E, b) || this.hopFence(IsoDirections.N, b);
        }
        else if (isoDirections == IsoDirections.SE) {
            if (1.0f - n2 < 1.0f - n) {
                return this.hopFence(IsoDirections.S, b) || this.hopFence(IsoDirections.E, b);
            }
            return this.hopFence(IsoDirections.E, b) || this.hopFence(IsoDirections.S, b);
        }
        else if (isoDirections == IsoDirections.SW) {
            if (1.0f - n2 < n) {
                return this.hopFence(IsoDirections.S, b) || this.hopFence(IsoDirections.W, b);
            }
            return this.hopFence(IsoDirections.W, b) || this.hopFence(IsoDirections.S, b);
        }
        else {
            if (this.current == null) {
                return false;
            }
            final IsoGridSquare isoGridSquare = this.current.nav[isoDirections.index()];
            if (isoGridSquare == null || isoGridSquare.Is(IsoFlagType.water)) {
                return false;
            }
            if (isoDirections == IsoDirections.N && this.getCurrentSquare().Is(IsoFlagType.HoppableN)) {
                if (b) {
                    return true;
                }
                this.climbOverFence(isoDirections);
                return true;
            }
            else if (isoDirections == IsoDirections.W && this.getCurrentSquare().Is(IsoFlagType.HoppableW)) {
                if (b) {
                    return true;
                }
                this.climbOverFence(isoDirections);
                return true;
            }
            else if (isoDirections == IsoDirections.S && IsoWorld.instance.CurrentCell.getGridSquare(this.x, this.y + 1.0f, this.z) != null && IsoWorld.instance.CurrentCell.getGridSquare(this.x, this.y + 1.0f, this.z).Is(IsoFlagType.HoppableN)) {
                if (b) {
                    return true;
                }
                this.climbOverFence(isoDirections);
                return true;
            }
            else {
                if (isoDirections != IsoDirections.E || IsoWorld.instance.CurrentCell.getGridSquare(this.x + 1.0f, this.y, this.z) == null || !IsoWorld.instance.CurrentCell.getGridSquare(this.x + 1.0f, this.y, this.z).Is(IsoFlagType.HoppableW)) {
                    return false;
                }
                if (b) {
                    return true;
                }
                this.climbOverFence(isoDirections);
                return true;
            }
        }
    }
    
    public boolean canClimbOverWall(final IsoDirections isoDirections) {
        if (this.isSprinting()) {
            return false;
        }
        if (!this.isSafeToClimbOver(isoDirections) || this.current == null) {
            return false;
        }
        if (this.current.haveRoof) {
            return false;
        }
        if (this.current.getBuilding() != null) {
            return false;
        }
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(this.current.x, this.current.y, this.current.z + 1);
        if (gridSquare != null && gridSquare.HasSlopedRoof()) {
            return false;
        }
        final IsoGridSquare isoGridSquare = this.current.nav[isoDirections.index()];
        if (isoGridSquare.haveRoof) {
            return false;
        }
        if (isoGridSquare.isSolid() || isoGridSquare.isSolidTrans()) {
            return false;
        }
        if (isoGridSquare.getBuilding() != null) {
            return false;
        }
        final IsoGridSquare gridSquare2 = IsoWorld.instance.CurrentCell.getGridSquare(isoGridSquare.x, isoGridSquare.y, isoGridSquare.z + 1);
        if (gridSquare2 != null && gridSquare2.HasSlopedRoof()) {
            return false;
        }
        switch (isoDirections) {
            case N: {
                if (this.current.Is(IsoFlagType.CantClimb)) {
                    return false;
                }
                if (!this.current.Has(IsoObjectType.wall)) {
                    return false;
                }
                if (!this.current.Is(IsoFlagType.collideN)) {
                    return false;
                }
                if (this.current.Is(IsoFlagType.HoppableN)) {
                    return false;
                }
                if (gridSquare != null && gridSquare.Is(IsoFlagType.collideN)) {
                    return false;
                }
                break;
            }
            case S: {
                if (isoGridSquare.Is(IsoFlagType.CantClimb)) {
                    return false;
                }
                if (!isoGridSquare.Has(IsoObjectType.wall)) {
                    return false;
                }
                if (!isoGridSquare.Is(IsoFlagType.collideN)) {
                    return false;
                }
                if (isoGridSquare.Is(IsoFlagType.HoppableN)) {
                    return false;
                }
                if (gridSquare2 != null && gridSquare2.Is(IsoFlagType.collideN)) {
                    return false;
                }
                break;
            }
            case W: {
                if (this.current.Is(IsoFlagType.CantClimb)) {
                    return false;
                }
                if (!this.current.Has(IsoObjectType.wall)) {
                    return false;
                }
                if (!this.current.Is(IsoFlagType.collideW)) {
                    return false;
                }
                if (this.current.Is(IsoFlagType.HoppableW)) {
                    return false;
                }
                if (gridSquare != null && gridSquare.Is(IsoFlagType.collideW)) {
                    return false;
                }
                break;
            }
            case E: {
                if (isoGridSquare.Is(IsoFlagType.CantClimb)) {
                    return false;
                }
                if (!isoGridSquare.Has(IsoObjectType.wall)) {
                    return false;
                }
                if (!isoGridSquare.Is(IsoFlagType.collideW)) {
                    return false;
                }
                if (isoGridSquare.Is(IsoFlagType.HoppableW)) {
                    return false;
                }
                if (gridSquare2 != null && gridSquare2.Is(IsoFlagType.collideW)) {
                    return false;
                }
                break;
            }
            default: {
                return false;
            }
        }
        return IsoWindow.canClimbThroughHelper(this, this.current, isoGridSquare, isoDirections == IsoDirections.N || isoDirections == IsoDirections.S);
    }
    
    public boolean climbOverWall(final IsoDirections isoDirections) {
        if (!this.canClimbOverWall(isoDirections)) {
            return false;
        }
        this.dropHeavyItems();
        ClimbOverWallState.instance().setParams(this, isoDirections);
        this.actionContext.reportEvent("EventClimbWall");
        return true;
    }
    
    private void updateSleepingPillsTaken() {
        if (this.getSleepingPillsTaken() > 0 && this.lastPillsTaken > 0L && GameTime.instance.Calender.getTimeInMillis() - this.lastPillsTaken > 7200000L) {
            this.setSleepingPillsTaken(this.getSleepingPillsTaken() - 1);
        }
    }
    
    public boolean AttemptAttack() {
        return this.DoAttack(this.useChargeTime);
    }
    
    @Override
    public boolean DoAttack(final float n) {
        return this.DoAttack(n, false, null);
    }
    
    public boolean DoAttack(final float n, final boolean forceShove, final String clickSound) {
        if (!this.authorizeMeleeAction) {
            return false;
        }
        this.setForceShove(forceShove);
        this.setClickSound(clickSound);
        this.pressedAttack(true);
        return false;
    }
    
    public int getPlayerNum() {
        return this.PlayerIndex;
    }
    
    public void updateLOS() {
        this.spottedList.clear();
        this.stats.NumVisibleZombies = 0;
        this.stats.LastNumChasingZombies = this.stats.NumChasingZombies;
        this.stats.NumChasingZombies = 0;
        this.stats.MusicZombiesTargeting = 0;
        this.stats.MusicZombiesVisible = 0;
        this.NumSurvivorsInVicinity = 0;
        if (this.getCurrentSquare() == null) {
            return;
        }
        final boolean bServer = GameServer.bServer;
        final boolean bClient = GameClient.bClient;
        final int playerIndex = this.PlayerIndex;
        final IsoPlayer instance = getInstance();
        final float x = this.getX();
        final float y = this.getY();
        final float z = this.getZ();
        int n = 0;
        int lastVeryCloseZombies = 0;
        for (int size = this.getCell().getObjectList().size(), i = 0; i < size; ++i) {
            final IsoMovingObject e = this.getCell().getObjectList().get(i);
            if (!(e instanceof IsoPhysicsObject)) {
                if (!(e instanceof BaseVehicle)) {
                    if (e == this) {
                        this.spottedList.add(e);
                    }
                    else {
                        final float x2 = e.getX();
                        final float y2 = e.getY();
                        final float z2 = e.getZ();
                        final float distanceTo = IsoUtils.DistanceTo(x2, y2, x, y);
                        if (distanceTo < 20.0f) {
                            ++n;
                        }
                        final IsoGridSquare currentSquare = e.getCurrentSquare();
                        if (currentSquare != null) {
                            if (this.isSeeEveryone()) {
                                e.setAlphaAndTarget(playerIndex, 1.0f);
                            }
                            else {
                                final IsoGameCharacter isoGameCharacter = Type.tryCastTo(e, IsoGameCharacter.class);
                                final IsoPlayer isoPlayer = Type.tryCastTo(isoGameCharacter, IsoPlayer.class);
                                final IsoZombie isoZombie = Type.tryCastTo(isoGameCharacter, IsoZombie.class);
                                if (instance != null && e != instance && isoGameCharacter != null && isoGameCharacter.isInvisible() && instance.accessLevel.isEmpty()) {
                                    isoGameCharacter.setAlphaAndTarget(playerIndex, 0.0f);
                                }
                                else {
                                    final float seeNearbyCharacterDistance = this.getSeeNearbyCharacterDistance();
                                    boolean b;
                                    if (bServer) {
                                        b = ServerLOS.instance.isCouldSee(this, currentSquare);
                                    }
                                    else {
                                        b = currentSquare.isCouldSee(playerIndex);
                                    }
                                    boolean canSee;
                                    if (bClient && isoPlayer != null) {
                                        canSee = true;
                                    }
                                    else if (!bServer) {
                                        canSee = currentSquare.isCanSee(playerIndex);
                                    }
                                    else {
                                        canSee = b;
                                    }
                                    if (!this.isAsleep() && (canSee || (distanceTo < seeNearbyCharacterDistance && b))) {
                                        this.TestZombieSpotPlayer(e);
                                        if (isoGameCharacter != null && isoGameCharacter.IsVisibleToPlayer[playerIndex]) {
                                            if (isoGameCharacter instanceof IsoSurvivor) {
                                                ++this.NumSurvivorsInVicinity;
                                            }
                                            if (isoZombie != null) {
                                                this.lastSeenZombieTime = 0.0;
                                                if (z2 >= z - 1.0f && distanceTo < 7.0f && !isoZombie.Ghost && !isoZombie.isFakeDead() && currentSquare.getRoom() == this.getCurrentSquare().getRoom()) {
                                                    this.TicksSinceSeenZombie = 0;
                                                    final Stats stats = this.stats;
                                                    ++stats.NumVisibleZombies;
                                                }
                                                if (distanceTo < 3.0f) {
                                                    ++lastVeryCloseZombies;
                                                }
                                                if (!isoZombie.isSceneCulled()) {
                                                    final Stats stats2 = this.stats;
                                                    ++stats2.MusicZombiesVisible;
                                                    if (isoZombie.target == this) {
                                                        final Stats stats3 = this.stats;
                                                        ++stats3.MusicZombiesTargeting;
                                                    }
                                                }
                                            }
                                            this.spottedList.add(isoGameCharacter);
                                            if (!(isoPlayer instanceof IsoPlayer) && !this.bRemote) {
                                                if (isoPlayer != null && isoPlayer != instance) {
                                                    isoPlayer.setTargetAlpha(playerIndex, 1.0f);
                                                }
                                                else {
                                                    isoGameCharacter.setTargetAlpha(playerIndex, 1.0f);
                                                }
                                            }
                                            float n2 = 4.0f;
                                            if (this.stats.NumVisibleZombies > 4) {
                                                n2 = 7.0f;
                                            }
                                            if (distanceTo < n2 && isoGameCharacter instanceof IsoZombie && (int)z2 == (int)z && !this.isGhostMode() && !bClient) {
                                                GameTime.instance.setMultiplier(1.0f);
                                                if (!bServer) {
                                                    UIManager.getSpeedControls().SetCurrentGameSpeed(1);
                                                }
                                            }
                                            if (distanceTo < n2 && isoGameCharacter instanceof IsoZombie && (int)z2 == (int)z && !this.LastSpotted.contains(isoGameCharacter)) {
                                                final Stats stats4 = this.stats;
                                                stats4.NumVisibleZombies += 2;
                                            }
                                        }
                                    }
                                    else {
                                        if (e != IsoPlayer.instance) {
                                            e.setTargetAlpha(playerIndex, 0.0f);
                                        }
                                        if (b) {
                                            this.TestZombieSpotPlayer(e);
                                        }
                                    }
                                    if (distanceTo < 2.0f && e.getTargetAlpha(playerIndex) == 1.0f && !this.bRemote) {
                                        e.setAlpha(playerIndex, 1.0f);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (this.isAlive() && lastVeryCloseZombies > 0 && this.stats.LastVeryCloseZombies == 0 && this.stats.NumVisibleZombies > 0 && this.stats.LastNumVisibleZombies == 0 && this.timeSinceLastStab >= 600.0f) {
            this.timeSinceLastStab = 0.0f;
            this.getEmitter().playSoundImpl("ZombieSurprisedPlayer", null);
        }
        if (this.stats.NumVisibleZombies > 0) {
            this.timeSinceLastStab = 0.0f;
        }
        if (this.timeSinceLastStab < 600.0f) {
            this.timeSinceLastStab += GameTime.getInstance().getMultiplier() / 1.6f;
        }
        float n3 = n / 20.0f;
        if (n3 > 1.0f) {
            n3 = 1.0f;
        }
        SoundManager.instance.BlendVolume(MainScreenState.ambient, n3 * 0.6f);
        int n4 = 0;
        for (int j = 0; j < this.spottedList.size(); ++j) {
            if (!this.LastSpotted.contains(this.spottedList.get(j))) {
                this.LastSpotted.add((IsoMovingObject)this.spottedList.get(j));
            }
            if (this.spottedList.get(j) instanceof IsoZombie) {
                ++n4;
            }
        }
        if (this.ClearSpottedTimer <= 0 && n4 == 0) {
            this.LastSpotted.clear();
            this.ClearSpottedTimer = 1000;
        }
        else {
            --this.ClearSpottedTimer;
        }
        this.stats.LastNumVisibleZombies = this.stats.NumVisibleZombies;
        this.stats.LastVeryCloseZombies = lastVeryCloseZombies;
    }
    
    public float getSeeNearbyCharacterDistance() {
        return 3.5f - this.stats.getFatigue();
    }
    
    private boolean checkSpottedPLayerTimer(final IsoPlayer isoPlayer) {
        if (!isoPlayer.spottedByPlayer) {
            return false;
        }
        if (this.spottedPlayerTimer.containsKey(isoPlayer.getRemoteID())) {
            this.spottedPlayerTimer.put(isoPlayer.getRemoteID(), this.spottedPlayerTimer.get(isoPlayer.getRemoteID()) + 1);
        }
        else {
            this.spottedPlayerTimer.put(isoPlayer.getRemoteID(), 1);
        }
        if (this.spottedPlayerTimer.get(isoPlayer.getRemoteID()) > 100) {
            isoPlayer.spottedByPlayer = false;
            return isoPlayer.doRenderShadow = false;
        }
        return true;
    }
    
    public boolean checkCanSeeClient(final IsoPlayer isoPlayer) {
        isoPlayer.doRenderShadow = true;
        final Vector2 set = IsoPlayer.tempVector2_1.set(this.getX(), this.getY());
        final Vector2 set2;
        final Vector2 vector2 = set2 = IsoPlayer.tempVector2_2.set(isoPlayer.getX(), isoPlayer.getY());
        set2.x -= set.x;
        final Vector2 vector3 = vector2;
        vector3.y -= set.y;
        final Vector2 forwardDirection = this.getForwardDirection();
        vector2.normalize();
        forwardDirection.normalize();
        forwardDirection.normalize();
        final float dot = vector2.dot(forwardDirection);
        if (!GameClient.bClient || isoPlayer == this || !this.isLocalPlayer()) {
            return true;
        }
        if (!this.getAccessLevel().equals("None") && this.canSeeAll) {
            return isoPlayer.spottedByPlayer = true;
        }
        final float distTo = isoPlayer.getCurrentSquare().DistTo(this.getCurrentSquare());
        if (distTo <= 2.0f) {
            return isoPlayer.spottedByPlayer = true;
        }
        if (ServerOptions.getInstance().HidePlayersBehindYou.getValue() && dot < -0.5) {
            return this.checkSpottedPLayerTimer(isoPlayer);
        }
        if (isoPlayer.isGhostMode() && this.getAccessLevel().equals("None")) {
            isoPlayer.doRenderShadow = false;
            return isoPlayer.spottedByPlayer = false;
        }
        final IsoGridSquare.ILighting lighting = isoPlayer.getCurrentSquare().lighting[this.getPlayerNum()];
        if (!lighting.bCouldSee()) {
            return this.checkSpottedPLayerTimer(isoPlayer);
        }
        if (!isoPlayer.isSneaking() || isoPlayer.isSprinting()) {
            return isoPlayer.spottedByPlayer = true;
        }
        if (distTo > 30.0f) {
            isoPlayer.spottedByPlayer = false;
        }
        if (isoPlayer.spottedByPlayer) {
            return true;
        }
        isoPlayer.doRenderShadow = true;
        final float n = (float)(Math.pow(Math.max(40.0f - distTo, 0.0f), 3.0) / 12000.0);
        final float n2 = (float)(1.0 - isoPlayer.remoteSneakLvl / 10.0f * 0.9 + 0.3);
        float n3 = 1.0f;
        if (dot < 0.8) {
            n3 = 0.3f;
        }
        if (dot < 0.6) {
            n3 = 0.05f;
        }
        final float n4 = (lighting.lightInfo().getR() + lighting.lightInfo().getG() + lighting.lightInfo().getB()) / 3.0f;
        final float n5 = (float)((1.0 - this.getMoodles().getMoodleLevel(MoodleType.Tired) / 5.0f) * 0.7 + 0.3);
        float n6 = 0.1f;
        if (isoPlayer.isPlayerMoving()) {
            n6 = 0.35f;
        }
        if (isoPlayer.isRunning()) {
            n6 = 1.0f;
        }
        final ArrayList<PolygonalMap2.Point> pointInLine = PolygonalMap2.instance.getPointInLine(isoPlayer.getX(), isoPlayer.getY(), this.getX(), this.getY(), (int)this.getZ());
        IsoGridSquare gridSquare = null;
        float distTo2 = 0.0f;
        float distTo3 = 0.0f;
        boolean b = false;
        for (int i = 0; i < pointInLine.size(); ++i) {
            final PolygonalMap2.Point point = pointInLine.get(i);
            gridSquare = IsoCell.getInstance().getGridSquare(point.x, point.y, this.getZ());
            if (gridSquare.getGridSneakModifier(false) > 1.0f) {
                b = true;
                break;
            }
            for (int j = 0; j < gridSquare.getObjects().size(); ++j) {
                final IsoObject isoObject = gridSquare.getObjects().get(j);
                if (isoObject.getSprite().getProperties().Is(IsoFlagType.solidtrans) || isoObject.getSprite().getProperties().Is(IsoFlagType.solid) || isoObject.getSprite().getProperties().Is(IsoFlagType.windowW) || isoObject.getSprite().getProperties().Is(IsoFlagType.windowN)) {
                    b = true;
                    break;
                }
            }
            if (b) {
                break;
            }
        }
        if (b) {
            distTo2 = gridSquare.DistTo(isoPlayer.getCurrentSquare());
            distTo3 = gridSquare.DistTo(this.getCurrentSquare());
        }
        final float n7 = n3 * n * n4 * n2 * n5 * n6 * (float)(Math.max(0.0f, ((distTo3 < 2.0f) ? 5.0f : Math.min(distTo2, 5.0f)) - 1.0f) / 5.0 * 0.9 + 0.1) * Math.max(0.1f, 1.0f - ClimateManager.getInstance().getFogIntensity());
        if (n7 >= 1.0f) {
            return isoPlayer.spottedByPlayer = true;
        }
        final boolean spottedByPlayer = Rand.Next(0.0f, 1.0f) < (float)(1.0 - Math.pow(1.0f - n7, GameTime.getInstance().getMultiplier())) * 0.5f;
        if (!(isoPlayer.spottedByPlayer = spottedByPlayer)) {
            isoPlayer.doRenderShadow = false;
        }
        return spottedByPlayer;
    }
    
    public String getTimeSurvived() {
        String s = "";
        final int n = (int)this.getHoursSurvived();
        final int n2 = n / 24;
        final int n3 = n % 24;
        final int n4 = n2 / 30;
        final int n5 = n2 % 30;
        final int n6 = n4 / 12;
        final int n7 = n4 % 12;
        String s2 = Translator.getText("IGUI_Gametime_day");
        String s3 = Translator.getText("IGUI_Gametime_year");
        String s4 = Translator.getText("IGUI_Gametime_hour");
        String s5 = Translator.getText("IGUI_Gametime_month");
        if (n6 != 0) {
            if (n6 > 1) {
                s3 = Translator.getText("IGUI_Gametime_years");
            }
            if (s.length() > 0) {
                s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            }
            s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;ILjava/lang/String;)Ljava/lang/String;, s, n6, s3);
        }
        if (n7 != 0) {
            if (n7 > 1) {
                s5 = Translator.getText("IGUI_Gametime_months");
            }
            if (s.length() > 0) {
                s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            }
            s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;ILjava/lang/String;)Ljava/lang/String;, s, n7, s5);
        }
        if (n5 != 0) {
            if (n5 > 1) {
                s2 = Translator.getText("IGUI_Gametime_days");
            }
            if (s.length() > 0) {
                s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            }
            s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;ILjava/lang/String;)Ljava/lang/String;, s, n5, s2);
        }
        if (n3 != 0) {
            if (n3 > 1) {
                s4 = Translator.getText("IGUI_Gametime_hours");
            }
            if (s.length() > 0) {
                s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            }
            s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;ILjava/lang/String;)Ljava/lang/String;, s, n3, s4);
        }
        if (s.isEmpty()) {
            s = invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, (int)(this.HoursSurvived * 60.0), Translator.getText("IGUI_Gametime_minutes"));
        }
        return s;
    }
    
    public boolean IsUsingAimWeapon() {
        return this.leftHandItem != null && this.leftHandItem instanceof HandWeapon && this.isAiming() && ((HandWeapon)this.leftHandItem).bIsAimedFirearm;
    }
    
    private boolean IsUsingAimHandWeapon() {
        return this.leftHandItem != null && this.leftHandItem instanceof HandWeapon && this.isAiming() && ((HandWeapon)this.leftHandItem).bIsAimedHandWeapon;
    }
    
    private boolean DoAimAnimOnAiming() {
        return this.IsUsingAimWeapon();
    }
    
    public int getSleepingPillsTaken() {
        return this.sleepingPillsTaken;
    }
    
    public void setSleepingPillsTaken(final int sleepingPillsTaken) {
        this.sleepingPillsTaken = sleepingPillsTaken;
        if (this.getStats().Drunkenness > 10.0f) {
            ++this.sleepingPillsTaken;
        }
        this.lastPillsTaken = GameTime.instance.Calender.getTimeInMillis();
    }
    
    @Override
    public boolean isOutside() {
        return this.getCurrentSquare() != null && this.getCurrentSquare().getRoom() == null && !this.isInARoom();
    }
    
    public double getLastSeenZomboidTime() {
        return this.lastSeenZombieTime;
    }
    
    public float getPlayerClothingTemperature() {
        float n = 0.0f;
        if (this.getClothingItem_Feet() != null) {
            n += ((Clothing)this.getClothingItem_Feet()).getTemperature();
        }
        if (this.getClothingItem_Hands() != null) {
            n += ((Clothing)this.getClothingItem_Hands()).getTemperature();
        }
        if (this.getClothingItem_Head() != null) {
            n += ((Clothing)this.getClothingItem_Head()).getTemperature();
        }
        if (this.getClothingItem_Legs() != null) {
            n += ((Clothing)this.getClothingItem_Legs()).getTemperature();
        }
        if (this.getClothingItem_Torso() != null) {
            n += ((Clothing)this.getClothingItem_Torso()).getTemperature();
        }
        return n;
    }
    
    public float getPlayerClothingInsulation() {
        float n = 0.0f;
        if (this.getClothingItem_Feet() != null) {
            n += ((Clothing)this.getClothingItem_Feet()).getInsulation() * 0.1f;
        }
        if (this.getClothingItem_Hands() != null) {
            n += ((Clothing)this.getClothingItem_Hands()).getInsulation() * 0.0f;
        }
        if (this.getClothingItem_Head() != null) {
            n += ((Clothing)this.getClothingItem_Head()).getInsulation() * 0.0f;
        }
        if (this.getClothingItem_Legs() != null) {
            n += ((Clothing)this.getClothingItem_Legs()).getInsulation() * 0.3f;
        }
        if (this.getClothingItem_Torso() != null) {
            n += ((Clothing)this.getClothingItem_Torso()).getInsulation() * 0.6f;
        }
        return n;
    }
    
    public InventoryItem getActiveLightItem() {
        if (this.rightHandItem != null && this.rightHandItem.isEmittingLight()) {
            return this.rightHandItem;
        }
        if (this.leftHandItem != null && this.leftHandItem.isEmittingLight()) {
            return this.leftHandItem;
        }
        final AttachedItems attachedItems = this.getAttachedItems();
        for (int i = 0; i < attachedItems.size(); ++i) {
            final InventoryItem itemByIndex = attachedItems.getItemByIndex(i);
            if (itemByIndex.isEmittingLight()) {
                return itemByIndex;
            }
        }
        return null;
    }
    
    public boolean isTorchCone() {
        if (this.bRemote) {
            return this.mpTorchCone;
        }
        final InventoryItem activeLightItem = this.getActiveLightItem();
        return activeLightItem != null && activeLightItem.isTorchCone();
    }
    
    public float getTorchDot() {
        if (this.bRemote) {}
        final InventoryItem activeLightItem = this.getActiveLightItem();
        if (activeLightItem != null) {
            return activeLightItem.getTorchDot();
        }
        return 0.0f;
    }
    
    public float getLightDistance() {
        if (this.bRemote) {
            return this.mpTorchDist;
        }
        final InventoryItem activeLightItem = this.getActiveLightItem();
        if (activeLightItem != null) {
            return (float)activeLightItem.getLightDistance();
        }
        return 0.0f;
    }
    
    public boolean pressedMovement(final boolean b) {
        if (this.isNPC) {
            return false;
        }
        if (GameClient.bClient && !this.isLocal()) {
            return this.networkAI.isPressedMovement();
        }
        this.setVariable("pressedRunButton", GameKeyboard.isKeyDown(Core.getInstance().getKey("Run")));
        if (!b && (this.isBlockMovement() || this.isIgnoreInputsForDirection())) {
            if (GameClient.bClient && this.isLocal()) {
                this.networkAI.setPressedMovement(false);
            }
            return false;
        }
        if (this.PlayerIndex == 0 && (GameKeyboard.isKeyDown(Core.getInstance().getKey("Left")) || GameKeyboard.isKeyDown(Core.getInstance().getKey("Right")) || GameKeyboard.isKeyDown(Core.getInstance().getKey("Forward")) || GameKeyboard.isKeyDown(Core.getInstance().getKey("Backward")))) {
            if (GameClient.bClient && this.isLocal()) {
                this.networkAI.setPressedMovement(true);
            }
            return true;
        }
        if (this.JoypadBind != -1) {
            final float movementAxisY = JoypadManager.instance.getMovementAxisY(this.JoypadBind);
            final float movementAxisX = JoypadManager.instance.getMovementAxisX(this.JoypadBind);
            final float deadZone = JoypadManager.instance.getDeadZone(this.JoypadBind, 0);
            if (Math.abs(movementAxisY) > deadZone || Math.abs(movementAxisX) > deadZone) {
                if (GameClient.bClient && this.isLocal()) {
                    this.networkAI.setPressedMovement(true);
                }
                return true;
            }
        }
        if (GameClient.bClient && this.isLocal()) {
            this.networkAI.setPressedMovement(false);
        }
        return false;
    }
    
    public boolean pressedCancelAction() {
        if (this.isNPC) {
            return false;
        }
        if (GameClient.bClient && !this.isLocal()) {
            return this.networkAI.isPressedCancelAction();
        }
        if (this.PlayerIndex == 0 && GameKeyboard.isKeyDown(Core.getInstance().getKey("CancelAction"))) {
            if (GameClient.bClient && this.isLocal()) {
                this.networkAI.setPressedCancelAction(true);
            }
            return true;
        }
        if (this.JoypadBind != -1) {
            final boolean bButtonStartPress = JoypadManager.instance.isBButtonStartPress(this.JoypadBind);
            if (GameClient.bClient && this.isLocal()) {
                this.networkAI.setPressedCancelAction(bButtonStartPress);
            }
            return bButtonStartPress;
        }
        if (GameClient.bClient && this.isLocal()) {
            this.networkAI.setPressedCancelAction(false);
        }
        return false;
    }
    
    public boolean pressedAim() {
        if (this.isNPC) {
            return false;
        }
        if (this.PlayerIndex == 0) {
            if (this.isAimKeyDown()) {
                return true;
            }
            if (Mouse.isButtonDownUICheck(1)) {
                return true;
            }
        }
        if (this.JoypadBind != -1) {
            final float aimingAxisY = JoypadManager.instance.getAimingAxisY(this.JoypadBind);
            final float aimingAxisX = JoypadManager.instance.getAimingAxisX(this.JoypadBind);
            return Math.abs(aimingAxisY) > 0.1f || Math.abs(aimingAxisX) > 0.1f;
        }
        return false;
    }
    
    @Override
    public boolean isDoingActionThatCanBeCancelled() {
        if (this.isDead()) {
            return false;
        }
        if (!this.getCharacterActions().isEmpty()) {
            return true;
        }
        final State currentState = this.getCurrentState();
        if (currentState != null && currentState.isDoingActionThatCanBeCancelled()) {
            return true;
        }
        for (int i = 0; i < this.stateMachine.getSubStateCount(); ++i) {
            final State subState = this.stateMachine.getSubStateAt(i);
            if (subState != null && subState.isDoingActionThatCanBeCancelled()) {
                return true;
            }
        }
        return false;
    }
    
    public long getSteamID() {
        return this.steamID;
    }
    
    public void setSteamID(final long steamID) {
        this.steamID = steamID;
    }
    
    public boolean isTargetedByZombie() {
        return this.targetedByZombie;
    }
    
    @Override
    public boolean isMaskClicked(final int n, final int n2, final boolean b) {
        return this.sprite != null && this.sprite.isMaskClicked(this.dir, n, n2, b);
    }
    
    public int getOffSetXUI() {
        return this.offSetXUI;
    }
    
    public void setOffSetXUI(final int offSetXUI) {
        this.offSetXUI = offSetXUI;
    }
    
    public int getOffSetYUI() {
        return this.offSetYUI;
    }
    
    public void setOffSetYUI(final int offSetYUI) {
        this.offSetYUI = offSetYUI;
    }
    
    public String getUsername() {
        return this.getUsername(false);
    }
    
    public String getUsername(final Boolean b) {
        String username = this.username;
        if (b && GameClient.bClient && ServerOptions.instance.ShowFirstAndLastName.getValue() && "None".equals(this.getAccessLevel())) {
            username = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getDescriptor().getForename(), this.getDescriptor().getSurname()), this.username);
        }
        return username;
    }
    
    public void setUsername(final String username) {
        this.username = username;
    }
    
    public void updateUsername() {
        if (GameClient.bClient || GameServer.bServer) {
            return;
        }
        this.username = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getDescriptor().getForename(), this.getDescriptor().getSurname());
    }
    
    @Override
    public short getOnlineID() {
        return this.OnlineID;
    }
    
    public boolean isLocalPlayer() {
        if (GameServer.bServer) {
            return false;
        }
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            if (IsoPlayer.players[i] == this) {
                return true;
            }
        }
        return false;
    }
    
    public static void setLocalPlayer(final int n, final IsoPlayer isoPlayer) {
        IsoPlayer.players[n] = isoPlayer;
    }
    
    public boolean isOnlyPlayerAsleep() {
        if (!this.isAsleep()) {
            return false;
        }
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            if (IsoPlayer.players[i] != null && !IsoPlayer.players[i].isDead() && IsoPlayer.players[i] != this && IsoPlayer.players[i].isAsleep()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void OnDeath() {
        super.OnDeath();
        this.advancedAnimator.SetState("death");
        if (GameServer.bServer) {
            return;
        }
        this.StopAllActionQueue();
        if (!GameClient.bClient) {
            this.dropHandItems();
        }
        if (allPlayersDead()) {
            SoundManager.instance.playMusic(IsoPlayer.DEATH_MUSIC_NAME);
        }
        if (this.isLocalPlayer()) {
            LuaEventManager.triggerEvent("OnPlayerDeath", this);
        }
        if (this.isLocalPlayer() && this.getVehicle() != null) {
            this.getVehicle().exit(this);
        }
        this.removeSaveFile();
        if (this.shouldBecomeZombieAfterDeath()) {
            this.forceAwake();
        }
        this.getMoodles().Update();
        this.getCell().setDrag(null, this.getPlayerNum());
    }
    
    public boolean isNoClip() {
        return this.noClip;
    }
    
    public void setNoClip(final boolean noClip) {
        this.noClip = noClip;
    }
    
    public void setAuthorizeMeleeAction(final boolean authorizeMeleeAction) {
        this.authorizeMeleeAction = authorizeMeleeAction;
    }
    
    public boolean isAuthorizeMeleeAction() {
        return this.authorizeMeleeAction;
    }
    
    public void setAuthorizeShoveStomp(final boolean authorizeShoveStomp) {
        this.authorizeShoveStomp = authorizeShoveStomp;
    }
    
    public boolean isAuthorizeShoveStomp() {
        return this.authorizeShoveStomp;
    }
    
    public boolean isBlockMovement() {
        return this.blockMovement;
    }
    
    public void setBlockMovement(final boolean blockMovement) {
        this.blockMovement = blockMovement;
    }
    
    public void startReceivingBodyDamageUpdates(final IsoPlayer isoPlayer) {
        if (GameClient.bClient && isoPlayer != null && isoPlayer != this && this.isLocalPlayer() && !isoPlayer.isLocalPlayer()) {
            isoPlayer.resetBodyDamageRemote();
            BodyDamageSync.instance.startReceivingUpdates(isoPlayer.getOnlineID());
        }
    }
    
    public void stopReceivingBodyDamageUpdates(final IsoPlayer isoPlayer) {
        if (GameClient.bClient && isoPlayer != null && isoPlayer != this && !isoPlayer.isLocalPlayer()) {
            BodyDamageSync.instance.stopReceivingUpdates(isoPlayer.getOnlineID());
        }
    }
    
    public Nutrition getNutrition() {
        return this.nutrition;
    }
    
    public Fitness getFitness() {
        return this.fitness;
    }
    
    private boolean updateRemotePlayer() {
        if (!this.bRemote) {
            return false;
        }
        if (GameServer.bServer) {
            ServerLOS.instance.doServerZombieLOS(this);
            ServerLOS.instance.updateLOS(this);
            if (this.isDead()) {
                return true;
            }
            this.removeFromSquare();
            this.setX(this.realx);
            this.setY(this.realy);
            this.setZ(this.realz);
            this.setLx(this.realx);
            this.setLy(this.realy);
            this.setLz(this.realz);
            this.ensureOnTile();
            if (this.slowTimer > 0.0f) {
                this.slowTimer -= GameTime.instance.getRealworldSecondsSinceLastUpdate();
                this.slowFactor -= GameTime.instance.getMultiplier() / 100.0f;
                if (this.slowFactor < 0.0f) {
                    this.slowFactor = 0.0f;
                }
            }
            else {
                this.slowFactor = 0.0f;
            }
        }
        if (GameClient.bClient) {
            if (this.isCurrentState(BumpedState.instance())) {
                return true;
            }
            float n;
            float n2;
            float z;
            if (this.networkAI.isCollisionEnabled() || this.networkAI.isNoCollisionTimeout()) {
                this.setCollidable(true);
                n = this.networkAI.targetX;
                n2 = this.networkAI.targetY;
                z = (float)this.networkAI.targetZ;
            }
            else {
                this.setCollidable(false);
                n = this.realx;
                n2 = this.realy;
                z = this.realz;
            }
            this.updateMovementRates();
            final PathFindBehavior2 pathFindBehavior2 = this.getPathFindBehavior2();
            boolean b = false;
            if (!this.networkAI.events.isEmpty()) {
                final Iterator<Object> iterator = this.networkAI.events.iterator();
                while (iterator.hasNext()) {
                    final EventPacket eventPacket = iterator.next();
                    if (eventPacket.process(this)) {
                        final NetworkPlayerAI networkAI = this.networkAI;
                        final boolean b2 = false;
                        networkAI.moving = b2;
                        this.m_isPlayerMoving = b2;
                        this.setJustMoved(false);
                        if (this.networkAI.usePathFind) {
                            pathFindBehavior2.reset();
                            this.setPath2(null);
                            this.networkAI.usePathFind = false;
                        }
                        if (Core.bDebug) {
                            DebugLog.log(DebugType.Multiplayer, String.format("Event processed (%d) : %s", this.networkAI.events.size(), eventPacket.getDescription()));
                        }
                        iterator.remove();
                        return true;
                    }
                    if (!eventPacket.isMovableEvent()) {
                        IsoPlayer.tempo.set(eventPacket.x - this.x, eventPacket.y - this.y);
                        n = eventPacket.x;
                        n2 = eventPacket.y;
                        z = eventPacket.z;
                        b = true;
                    }
                    if (eventPacket.isTimeout()) {
                        final NetworkPlayerAI networkAI2 = this.networkAI;
                        final boolean b3 = false;
                        networkAI2.moving = b3;
                        this.m_isPlayerMoving = b3;
                        this.setJustMoved(false);
                        if (this.networkAI.usePathFind) {
                            pathFindBehavior2.reset();
                            this.setPath2(null);
                            this.networkAI.usePathFind = false;
                        }
                        if (Core.bDebug) {
                            DebugLog.log(DebugType.Multiplayer, String.format("Event timeout (%d) : %s", this.networkAI.events.size(), eventPacket.getDescription()));
                        }
                        iterator.remove();
                        return true;
                    }
                }
            }
            if (!b && this.networkAI.collidePointX > -1.0f && this.networkAI.collidePointY > -1.0f && ((int)this.x != (int)this.networkAI.collidePointX || (int)this.y != (int)this.networkAI.collidePointY)) {
                n = this.networkAI.collidePointX;
                n2 = this.networkAI.collidePointY;
                DebugLog.log(DebugType.ActionSystem, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;FF)Ljava/lang/String;, this.username, n, n2));
            }
            if (DebugOptions.instance.MultiplayerShowPlayerPrediction.getValue()) {
                this.networkAI.targetX = n;
                this.networkAI.targetY = n2;
            }
            if (!this.networkAI.forcePathFinder && this.isCollidedThisFrame() && IsoUtils.DistanceManhatten(n, n2, this.x, this.y) > 3.0f) {
                this.networkAI.forcePathFinder = true;
            }
            if ((this.networkAI.forcePathFinder && !PolygonalMap2.instance.lineClearCollide(this.x, this.y, n, n2, (int)this.z, this.vehicle, false, true) && IsoUtils.DistanceManhatten(n, n2, this.x, this.y) < 2.0f) || this.getCurrentState() == ClimbOverFenceState.instance() || this.getCurrentState() == ClimbThroughWindowState.instance() || this.getCurrentState() == ClimbOverWallState.instance()) {
                this.networkAI.forcePathFinder = false;
            }
            if (!this.networkAI.needToMovingUsingPathFinder && !this.networkAI.forcePathFinder) {
                if (this.networkAI.usePathFind) {
                    pathFindBehavior2.reset();
                    this.setPath2(null);
                    this.networkAI.usePathFind = false;
                }
                pathFindBehavior2.walkingOnTheSpot.reset(this.x, this.y);
                this.getDeferredMovement(IsoPlayer.tempVector2_2);
                if (this.getCurrentState() == ClimbOverWallState.instance() || this.getCurrentState() == ClimbOverFenceState.instance()) {
                    this.MoveUnmodded(IsoPlayer.tempVector2_2);
                }
                else {
                    pathFindBehavior2.moveToPoint(n, n2, 0.8f + 0.4f * IsoUtils.smoothstep(0.8f, 1.2f, IsoUtils.DistanceTo(this.x, this.y, this.networkAI.targetX, this.networkAI.targetY) / IsoUtils.DistanceTo(this.realx, this.realy, this.networkAI.targetX, this.networkAI.targetY)));
                }
                if (!(this.m_isPlayerMoving = ((!b && IsoUtils.DistanceManhatten(n, n2, this.x, this.y) > 0.2f) || (int)n != (int)this.x || (int)n2 != (int)this.y || (int)this.z != (int)z))) {
                    this.DirectionFromVector(this.networkAI.direction);
                    this.setForwardDirection(this.networkAI.direction);
                    this.networkAI.forcePathFinder = false;
                    if (this.networkAI.usePathFind) {
                        pathFindBehavior2.reset();
                        this.setPath2(null);
                        this.networkAI.usePathFind = false;
                    }
                }
                this.setJustMoved(this.m_isPlayerMoving);
                this.m_deltaX = 0.0f;
                this.m_deltaY = 0.0f;
            }
            else {
                if (!this.networkAI.usePathFind || n != pathFindBehavior2.getTargetX() || n2 != pathFindBehavior2.getTargetY()) {
                    pathFindBehavior2.pathToLocationF(n, n2, z);
                    pathFindBehavior2.walkingOnTheSpot.reset(this.x, this.y);
                    this.networkAI.usePathFind = true;
                }
                final PathFindBehavior2.BehaviorResult update = pathFindBehavior2.update();
                if (update == PathFindBehavior2.BehaviorResult.Failed) {
                    this.setPathFindIndex(-1);
                    if (this.networkAI.forcePathFinder) {
                        this.networkAI.forcePathFinder = false;
                    }
                    else if (NetworkTeleport.teleport(this, NetworkTeleport.Type.teleportation, n, n2, (byte)z, 1.0f)) {
                        DebugLog.Multiplayer.warn((Object)String.format("Player %d teleport from (%.2f, %.2f, %.2f) to (%.2f, %.2f %.2f)", this.getOnlineID(), this.x, this.y, this.z, n, n2, z));
                    }
                }
                else if (update == PathFindBehavior2.BehaviorResult.Succeeded) {
                    final int n3 = (int)pathFindBehavior2.getTargetX();
                    final int n4 = (int)pathFindBehavior2.getTargetY();
                    final IsoChunk isoChunk = GameServer.bServer ? ServerMap.instance.getChunk(n3 / 10, n4 / 10) : IsoWorld.instance.CurrentCell.getChunkForGridSquare(n3, n4, 0);
                    this.setJustMoved(this.m_isPlayerMoving = true);
                }
                this.m_deltaX = 0.0f;
                this.m_deltaY = 0.0f;
            }
            if (!this.m_isPlayerMoving || this.isAiming()) {
                this.DirectionFromVector(this.networkAI.direction);
                this.setForwardDirection(this.networkAI.direction);
                IsoPlayer.tempo.set(n - this.nx, -(n2 - this.ny));
                IsoPlayer.tempo.normalize();
                float renderedAngle = this.legsSprite.modelSlot.model.AnimPlayer.getRenderedAngle();
                if (renderedAngle > 6.283185307179586) {
                    renderedAngle -= (float)6.283185307179586;
                }
                if (renderedAngle < 0.0f) {
                    renderedAngle += (float)6.283185307179586;
                }
                IsoPlayer.tempo.rotate(renderedAngle);
                IsoPlayer.tempo.setLength(Math.min(IsoUtils.DistanceTo(n, n2, this.x, this.y), 1.0f));
                this.m_deltaX = IsoPlayer.tempo.x;
                this.m_deltaY = IsoPlayer.tempo.y;
            }
        }
        return true;
    }
    
    private boolean updateWhileDead() {
        if (GameServer.bServer) {
            return false;
        }
        if (!this.isLocalPlayer()) {
            return false;
        }
        if (!this.isDead()) {
            return false;
        }
        this.setVariable("bPathfind", false);
        this.setMoving(false);
        this.m_isPlayerMoving = false;
        if (this.getVehicle() != null) {
            this.getVehicle().exit(this);
        }
        if (this.heartEventInstance != 0L) {
            this.getEmitter().stopSound(this.heartEventInstance);
            this.heartEventInstance = 0L;
        }
        if (GameClient.bClient && !this.bRemote && !this.bSentDeath) {
            this.dropHandItems();
            if (DebugOptions.instance.MultiplayerPlayerZombie.getValue()) {
                this.getBodyDamage().setInfectionLevel(100.0f);
            }
            GameClient.instance.sendPlayerDeath(this);
            ClientPlayerDB.getInstance().clientSendNetworkPlayerInt(this);
            this.bSentDeath = true;
        }
        return true;
    }
    
    private void initFMODParameters() {
        final FMODParameterList fmodParameters = this.getFMODParameters();
        fmodParameters.add(this.parameterCharacterMovementSpeed);
        fmodParameters.add(this.parameterFootstepMaterial);
        fmodParameters.add(this.parameterFootstepMaterial2);
        fmodParameters.add(this.parameterLocalPlayer);
        fmodParameters.add(this.parameterMeleeHitSurface);
        fmodParameters.add(this.parameterPlayerHealth);
        fmodParameters.add(this.parameterShoeType);
        fmodParameters.add(this.parameterVehicleHitLocation);
    }
    
    public ParameterCharacterMovementSpeed getParameterCharacterMovementSpeed() {
        return this.parameterCharacterMovementSpeed;
    }
    
    public void setMeleeHitSurface(final ParameterMeleeHitSurface.Material material) {
        this.parameterMeleeHitSurface.setMaterial(material);
    }
    
    public void setMeleeHitSurface(final String s) {
        try {
            this.parameterMeleeHitSurface.setMaterial(ParameterMeleeHitSurface.Material.valueOf(s));
        }
        catch (IllegalArgumentException ex) {
            this.parameterMeleeHitSurface.setMaterial(ParameterMeleeHitSurface.Material.Default);
        }
    }
    
    public void setVehicleHitLocation(final BaseVehicle baseVehicle) {
        this.parameterVehicleHitLocation.setLocation(ParameterVehicleHitLocation.calculateLocation(baseVehicle, this.getX(), this.getY(), this.getZ()));
    }
    
    private void updateHeartSound() {
        if (GameServer.bServer) {
            return;
        }
        if (!this.isLocalPlayer()) {
            return;
        }
        final GameSound sound = GameSounds.getSound("HeartBeat");
        final boolean b = sound != null && sound.getUserVolume() > 0.0f && this.stats.Panic > 0.0f;
        if (!this.Asleep && b && GameTime.getInstance().getTrueMultiplier() == 1.0f) {
            this.heartDelay -= GameTime.getInstance().getMultiplier() / 1.6f;
            if (this.heartEventInstance == 0L || !this.getEmitter().isPlaying(this.heartEventInstance)) {
                this.heartEventInstance = this.getEmitter().playSoundImpl("HeartBeat", null);
                this.getEmitter().setVolume(this.heartEventInstance, 0.0f);
            }
            if (this.heartDelay <= 0.0f) {
                this.heartDelayMax = (float)((int)((1.0f - this.stats.Panic / 100.0f * 0.7f) * 25.0f) * 2);
                this.heartDelay = this.heartDelayMax;
                if (this.heartEventInstance != 0L) {
                    this.getEmitter().setVolume(this.heartEventInstance, this.stats.Panic / 100.0f);
                }
            }
        }
        else if (this.heartEventInstance != 0L) {
            this.getEmitter().setVolume(this.heartEventInstance, 0.0f);
        }
    }
    
    private void updateWorldAmbiance() {
        if (GameServer.bServer) {
            return;
        }
        if (!this.isLocalPlayer()) {
            return;
        }
        if (this.getPlayerNum() == 0 && (this.worldAmbianceInstance == 0L || !this.getEmitter().isPlaying(this.worldAmbianceInstance))) {
            this.worldAmbianceInstance = this.getEmitter().playSoundImpl("WorldAmbiance", null);
            this.getEmitter().setVolume(this.worldAmbianceInstance, 1.0f);
        }
    }
    
    @Override
    public void DoFootstepSound(final String s) {
        ParameterCharacterMovementSpeed.MovementType movementType = ParameterCharacterMovementSpeed.MovementType.Walk;
        float n = 0.5f;
        switch (s) {
            case "sneak_walk": {
                n = 0.25f;
                movementType = ParameterCharacterMovementSpeed.MovementType.SneakWalk;
                break;
            }
            case "sneak_run": {
                n = 0.25f;
                movementType = ParameterCharacterMovementSpeed.MovementType.SneakRun;
                break;
            }
            case "strafe": {
                n = 0.5f;
                movementType = ParameterCharacterMovementSpeed.MovementType.Strafe;
                break;
            }
            case "walk": {
                n = 0.5f;
                movementType = ParameterCharacterMovementSpeed.MovementType.Walk;
                break;
            }
            case "run": {
                n = 0.75f;
                movementType = ParameterCharacterMovementSpeed.MovementType.Run;
                break;
            }
            case "sprint": {
                n = 1.0f;
                movementType = ParameterCharacterMovementSpeed.MovementType.Sprint;
                break;
            }
        }
        this.parameterCharacterMovementSpeed.setMovementType(movementType);
        super.DoFootstepSound(n);
    }
    
    private void updateHeavyBreathing() {
    }
    
    private void checkVehicleContainers() {
        final ArrayList<VehicleContainer> tempContainers = this.vehicleContainerData.tempContainers;
        tempContainers.clear();
        final int n = (int)this.getX() - 4;
        final int n2 = (int)this.getY() - 4;
        final int n3 = (int)this.getX() + 4;
        final int n4 = (int)this.getY() + 4;
        final int n5 = n / 10;
        final int n6 = n2 / 10;
        final int n7 = (int)Math.ceil(n3 / 10.0f);
        for (int n8 = (int)Math.ceil(n4 / 10.0f), i = n6; i < n8; ++i) {
            for (int j = n5; j < n7; ++j) {
                final IsoChunk isoChunk = GameServer.bServer ? ServerMap.instance.getChunk(j, i) : IsoWorld.instance.CurrentCell.getChunkForGridSquare(j * 10, i * 10, 0);
                if (isoChunk != null) {
                    for (int k = 0; k < isoChunk.vehicles.size(); ++k) {
                        final BaseVehicle baseVehicle = isoChunk.vehicles.get(k);
                        final VehicleScript script = baseVehicle.getScript();
                        if (script != null) {
                            for (int l = 0; l < script.getPartCount(); ++l) {
                                final VehicleScript.Part part = script.getPart(l);
                                if (part.container != null && part.area != null && baseVehicle.isInArea(part.area, this)) {
                                    tempContainers.add((this.vehicleContainerData.freeContainers.isEmpty() ? new VehicleContainer() : this.vehicleContainerData.freeContainers.pop()).set(baseVehicle, l));
                                }
                            }
                        }
                    }
                }
            }
        }
        if (tempContainers.size() != this.vehicleContainerData.containers.size()) {
            this.vehicleContainerData.freeContainers.addAll((Collection<?>)this.vehicleContainerData.containers);
            this.vehicleContainerData.containers.clear();
            this.vehicleContainerData.containers.addAll(tempContainers);
            LuaEventManager.triggerEvent("OnContainerUpdate");
        }
        else {
            for (int n9 = 0; n9 < tempContainers.size(); ++n9) {
                if (!tempContainers.get(n9).equals(this.vehicleContainerData.containers.get(n9))) {
                    this.vehicleContainerData.freeContainers.addAll((Collection<?>)this.vehicleContainerData.containers);
                    this.vehicleContainerData.containers.clear();
                    this.vehicleContainerData.containers.addAll(tempContainers);
                    LuaEventManager.triggerEvent("OnContainerUpdate");
                    break;
                }
            }
        }
    }
    
    public void setJoypadIgnoreAimUntilCentered(final boolean bJoypadIgnoreAimUntilCentered) {
        this.bJoypadIgnoreAimUntilCentered = bJoypadIgnoreAimUntilCentered;
    }
    
    public boolean canSeePlayerStats() {
        return this.accessLevel != "";
    }
    
    public ByteBufferWriter createPlayerStats(final ByteBufferWriter byteBufferWriter, final String s) {
        PacketTypes.PacketType.ChangePlayerStats.doPacket(byteBufferWriter);
        byteBufferWriter.putShort(this.getOnlineID());
        byteBufferWriter.putUTF(s);
        byteBufferWriter.putUTF(this.getDisplayName());
        byteBufferWriter.putUTF(this.getDescriptor().getForename());
        byteBufferWriter.putUTF(this.getDescriptor().getSurname());
        byteBufferWriter.putUTF(this.getDescriptor().getProfession());
        byteBufferWriter.putUTF(this.accessLevel);
        if (!StringUtils.isNullOrEmpty(this.getTagPrefix())) {
            byteBufferWriter.putByte((byte)1);
            byteBufferWriter.putUTF(this.getTagPrefix());
        }
        else {
            byteBufferWriter.putByte((byte)0);
        }
        if (this.accessLevel.equals("")) {
            this.setGhostMode(false);
            this.setInvisible(false);
            this.setGodMod(false);
        }
        byteBufferWriter.putBoolean(this.isAllChatMuted());
        byteBufferWriter.putFloat(this.getTagColor().r);
        byteBufferWriter.putFloat(this.getTagColor().g);
        byteBufferWriter.putFloat(this.getTagColor().b);
        byteBufferWriter.putByte((byte)(this.showTag ? 1 : 0));
        byteBufferWriter.putByte((byte)(this.factionPvp ? 1 : 0));
        return byteBufferWriter;
    }
    
    public String setPlayerStats(final ByteBuffer byteBuffer, final String s) {
        final String readString = GameWindow.ReadString(byteBuffer);
        final String readString2 = GameWindow.ReadString(byteBuffer);
        final String readString3 = GameWindow.ReadString(byteBuffer);
        final String readString4 = GameWindow.ReadString(byteBuffer);
        final String readString5 = GameWindow.ReadString(byteBuffer);
        String readString6 = "";
        if (byteBuffer.get() == 1) {
            readString6 = GameWindow.ReadString(byteBuffer);
        }
        final boolean allChatMuted = byteBuffer.get() == 1;
        final float float1 = byteBuffer.getFloat();
        final float float2 = byteBuffer.getFloat();
        final float float3 = byteBuffer.getFloat();
        String anObject = "";
        this.setTagColor(new ColorInfo(float1, float2, float3, 1.0f));
        this.setTagPrefix(readString6);
        this.showTag = (byteBuffer.get() == 1);
        this.factionPvp = (byteBuffer.get() == 1);
        if (!readString2.equals(this.getDescriptor().getForename())) {
            if (GameServer.bServer) {
                anObject = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, readString, readString2);
            }
            else {
                anObject = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, readString2);
            }
        }
        this.getDescriptor().setForename(readString2);
        if (!readString3.equals(this.getDescriptor().getSurname())) {
            if (GameServer.bServer) {
                anObject = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, readString, readString3);
            }
            else {
                anObject = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, readString3);
            }
        }
        this.getDescriptor().setSurname(readString3);
        if (!readString4.equals(this.getDescriptor().getProfession())) {
            if (GameServer.bServer) {
                anObject = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, readString, readString4);
            }
            else {
                anObject = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, readString4);
            }
        }
        this.getDescriptor().setProfession(readString4);
        if (!this.accessLevel.equals(readString5)) {
            if (GameServer.bServer) {
                // invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, this.getDisplayName(), readString5)
                try {
                    ServerWorldDatabase.instance.setAccessLevel(this.username, readString5);
                }
                catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            else if (GameClient.bClient && GameClient.username.equals(this.username)) {
                GameClient.accessLevel = readString5;
                GameClient.connection.accessLevel = readString5;
            }
            if (readString5.equals("")) {
                this.setGhostMode(false);
                this.setInvisible(false);
                this.setGodMod(false);
            }
            anObject = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, readString5);
            this.accessLevel = readString5;
        }
        if (!this.getDisplayName().equals(readString)) {
            if (GameServer.bServer) {
                anObject = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, this.getDisplayName(), readString);
                ServerWorldDatabase.instance.updateDisplayName(this.username, readString);
            }
            else {
                anObject = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, readString);
            }
            this.setDisplayName(readString);
        }
        if (allChatMuted != this.isAllChatMuted()) {
            if (allChatMuted) {
                if (GameServer.bServer) {
                    anObject = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, readString);
                }
                else {
                    anObject = "Banned you from using /all chat";
                }
            }
            else if (GameServer.bServer) {
                anObject = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, readString);
            }
            else {
                anObject = "Now allowed you to use /all chat";
            }
        }
        this.setAllChatMuted(allChatMuted);
        if (GameServer.bServer && !"".equals(anObject)) {
            LoggerManager.getLogger("admin").write(anObject);
        }
        if (GameClient.bClient) {
            LuaEventManager.triggerEvent("OnMiniScoreboardUpdate");
        }
        return anObject;
    }
    
    public boolean isAllChatMuted() {
        return this.allChatMuted;
    }
    
    public void setAllChatMuted(final boolean allChatMuted) {
        this.allChatMuted = allChatMuted;
    }
    
    public String getAccessLevel() {
        final String accessLevel = this.accessLevel;
        switch (accessLevel) {
            case "admin": {
                return "Admin";
            }
            case "moderator": {
                return "Moderator";
            }
            case "overseer": {
                return "Overseer";
            }
            case "gm": {
                return "GM";
            }
            case "observer": {
                return "Observer";
            }
            default: {
                return "None";
            }
        }
    }
    
    public void setAccessLevel(final String accessLevel) {
        this.accessLevel = accessLevel;
    }
    
    public void addMechanicsItem(final String s, final VehiclePart vehiclePart, final Long value) {
        int n = 1;
        int n2 = 1;
        if (this.mechanicsItem.get(Long.parseLong(s)) == null) {
            if (vehiclePart.getTable("uninstall") != null && vehiclePart.getTable("uninstall").rawget((Object)"skills") != null) {
                for (final String s2 : ((String)vehiclePart.getTable("uninstall").rawget((Object)"skills")).split(";")) {
                    if (s2.contains("Mechanics")) {
                        final int int1 = Integer.parseInt(s2.split(":")[1]);
                        if (int1 >= 6) {
                            n = 3;
                            n2 = 7;
                        }
                        else if (int1 >= 4) {
                            n = 3;
                            n2 = 5;
                        }
                        else if (int1 >= 2) {
                            n = 2;
                            n2 = 4;
                        }
                        else if (Rand.Next(3) == 0) {
                            n = 2;
                            n2 = 2;
                        }
                    }
                }
            }
            this.getXp().AddXP(PerkFactory.Perks.Mechanics, (float)Rand.Next(n, n2));
        }
        this.mechanicsItem.put(Long.parseLong(s), value);
    }
    
    public void setPosition(final float x, final float y, final float z) {
        this.setX(x);
        this.setY(y);
        this.setZ(z);
    }
    
    private void updateTemperatureCheck() {
        final int moodleLevel = this.Moodles.getMoodleLevel(MoodleType.Hypothermia);
        if (this.hypothermiaCache == -1 || this.hypothermiaCache != moodleLevel) {
            if (moodleLevel >= 3 && moodleLevel > this.hypothermiaCache && this.isAsleep() && !this.ForceWakeUp) {
                this.forceAwake();
            }
            this.hypothermiaCache = moodleLevel;
        }
        final int moodleLevel2 = this.Moodles.getMoodleLevel(MoodleType.Hyperthermia);
        if (this.hyperthermiaCache == -1 || this.hyperthermiaCache != moodleLevel2) {
            if (moodleLevel2 >= 3 && moodleLevel2 > this.hyperthermiaCache && this.isAsleep() && !this.ForceWakeUp) {
                this.forceAwake();
            }
            this.hyperthermiaCache = moodleLevel2;
        }
    }
    
    public float getZombieRelevenceScore(final IsoZombie isoZombie) {
        if (isoZombie.getCurrentSquare() == null) {
            return -10000.0f;
        }
        float n = 0.0f;
        if (isoZombie.getCurrentSquare().getCanSee(this.PlayerIndex)) {
            n += 100.0f;
        }
        else if (isoZombie.getCurrentSquare().isCouldSee(this.PlayerIndex)) {
            n += 10.0f;
        }
        if (isoZombie.getCurrentSquare().getRoom() != null && this.current.getRoom() == null) {
            n -= 20.0f;
        }
        if (isoZombie.getCurrentSquare().getRoom() == null && this.current.getRoom() != null) {
            n -= 20.0f;
        }
        if (isoZombie.getCurrentSquare().getRoom() != this.current.getRoom()) {
            n -= 20.0f;
        }
        final float distTo = isoZombie.DistTo(this);
        float n2 = n - distTo;
        if (distTo < 20.0f) {
            n2 += 300.0f;
        }
        if (distTo < 15.0f) {
            n2 += 300.0f;
        }
        if (distTo < 10.0f) {
            n2 += 1000.0f;
        }
        if (isoZombie.getTargetAlpha() < 1.0f && n2 > 0.0f) {
            n2 *= isoZombie.getTargetAlpha();
        }
        return n2;
    }
    
    @Override
    public BaseVisual getVisual() {
        return this.humanVisual;
    }
    
    @Override
    public HumanVisual getHumanVisual() {
        return this.humanVisual;
    }
    
    @Override
    public ItemVisuals getItemVisuals() {
        return this.itemVisuals;
    }
    
    @Override
    public void getItemVisuals(final ItemVisuals itemVisuals) {
        if (!this.bRemote) {
            this.getWornItems().getItemVisuals(itemVisuals);
        }
        else {
            itemVisuals.clear();
            itemVisuals.addAll(this.itemVisuals);
        }
    }
    
    @Override
    public void dressInNamedOutfit(final String s) {
        this.getHumanVisual().dressInNamedOutfit(s, this.itemVisuals);
        this.onClothingOutfitPreviewChanged();
    }
    
    @Override
    public void dressInClothingItem(final String s) {
        this.getHumanVisual().dressInClothingItem(s, this.itemVisuals);
        this.onClothingOutfitPreviewChanged();
    }
    
    private void onClothingOutfitPreviewChanged() {
        if (!this.isLocalPlayer()) {
            return;
        }
        this.getInventory().clear();
        this.wornItems.setFromItemVisuals(this.itemVisuals);
        this.wornItems.addItemsToItemContainer(this.getInventory());
        this.itemVisuals.clear();
        this.resetModel();
        this.onWornItemsChanged();
    }
    
    @Override
    public void onWornItemsChanged() {
        this.parameterShoeType.setShoeType(null);
    }
    
    @Override
    public void actionStateChanged(final ActionContext actionContext) {
        super.actionStateChanged(actionContext);
    }
    
    public Vector2 getLastAngle() {
        return this.lastAngle;
    }
    
    public void setLastAngle(final Vector2 vector2) {
        this.lastAngle.set(vector2);
    }
    
    public int getDialogMood() {
        return this.DialogMood;
    }
    
    public void setDialogMood(final int dialogMood) {
        this.DialogMood = dialogMood;
    }
    
    public int getPing() {
        return this.ping;
    }
    
    public void setPing(final int ping) {
        this.ping = ping;
    }
    
    public IsoMovingObject getDragObject() {
        return this.DragObject;
    }
    
    public void setDragObject(final IsoMovingObject dragObject) {
        this.DragObject = dragObject;
    }
    
    public float getAsleepTime() {
        return this.AsleepTime;
    }
    
    public void setAsleepTime(final float asleepTime) {
        this.AsleepTime = asleepTime;
    }
    
    public Stack<IsoMovingObject> getSpottedList() {
        return this.spottedList;
    }
    
    public int getTicksSinceSeenZombie() {
        return this.TicksSinceSeenZombie;
    }
    
    public void setTicksSinceSeenZombie(final int ticksSinceSeenZombie) {
        this.TicksSinceSeenZombie = ticksSinceSeenZombie;
    }
    
    public boolean isWaiting() {
        return this.Waiting;
    }
    
    public void setWaiting(final boolean waiting) {
        this.Waiting = waiting;
    }
    
    public IsoSurvivor getDragCharacter() {
        return this.DragCharacter;
    }
    
    public void setDragCharacter(final IsoSurvivor dragCharacter) {
        this.DragCharacter = dragCharacter;
    }
    
    public float getHeartDelay() {
        return this.heartDelay;
    }
    
    public void setHeartDelay(final float heartDelay) {
        this.heartDelay = heartDelay;
    }
    
    public float getHeartDelayMax() {
        return this.heartDelayMax;
    }
    
    public void setHeartDelayMax(final int n) {
        this.heartDelayMax = (float)n;
    }
    
    @Override
    public double getHoursSurvived() {
        return this.HoursSurvived;
    }
    
    public void setHoursSurvived(final double hoursSurvived) {
        this.HoursSurvived = hoursSurvived;
    }
    
    public float getMaxWeightDelta() {
        return this.maxWeightDelta;
    }
    
    public void setMaxWeightDelta(final float maxWeightDelta) {
        this.maxWeightDelta = maxWeightDelta;
    }
    
    public String getForname() {
        return this.Forname;
    }
    
    public void setForname(final String forname) {
        this.Forname = forname;
    }
    
    public String getSurname() {
        return this.Surname;
    }
    
    public void setSurname(final String surname) {
        this.Surname = surname;
    }
    
    public boolean isbChangeCharacterDebounce() {
        return this.bChangeCharacterDebounce;
    }
    
    public void setbChangeCharacterDebounce(final boolean bChangeCharacterDebounce) {
        this.bChangeCharacterDebounce = bChangeCharacterDebounce;
    }
    
    public int getFollowID() {
        return this.followID;
    }
    
    public void setFollowID(final int followID) {
        this.followID = followID;
    }
    
    public boolean isbSeenThisFrame() {
        return this.bSeenThisFrame;
    }
    
    public void setbSeenThisFrame(final boolean bSeenThisFrame) {
        this.bSeenThisFrame = bSeenThisFrame;
    }
    
    public boolean isbCouldBeSeenThisFrame() {
        return this.bCouldBeSeenThisFrame;
    }
    
    public void setbCouldBeSeenThisFrame(final boolean bCouldBeSeenThisFrame) {
        this.bCouldBeSeenThisFrame = bCouldBeSeenThisFrame;
    }
    
    public float getTimeSinceLastStab() {
        return this.timeSinceLastStab;
    }
    
    public void setTimeSinceLastStab(final float timeSinceLastStab) {
        this.timeSinceLastStab = timeSinceLastStab;
    }
    
    public Stack<IsoMovingObject> getLastSpotted() {
        return this.LastSpotted;
    }
    
    public void setLastSpotted(final Stack<IsoMovingObject> lastSpotted) {
        this.LastSpotted = lastSpotted;
    }
    
    public int getClearSpottedTimer() {
        return this.ClearSpottedTimer;
    }
    
    public void setClearSpottedTimer(final int clearSpottedTimer) {
        this.ClearSpottedTimer = clearSpottedTimer;
    }
    
    public boolean IsRunning() {
        return this.isRunning();
    }
    
    public void InitSpriteParts() {
    }
    
    public boolean IsAiming() {
        return this.isAiming();
    }
    
    public String getTagPrefix() {
        return this.tagPrefix;
    }
    
    public void setTagPrefix(final String tagPrefix) {
        this.tagPrefix = tagPrefix;
    }
    
    public ColorInfo getTagColor() {
        return this.tagColor;
    }
    
    public void setTagColor(final ColorInfo colorInfo) {
        this.tagColor.set(colorInfo);
    }
    
    @Deprecated
    public Integer getTransactionID() {
        return this.transactionID;
    }
    
    @Deprecated
    public void setTransactionID(final Integer transactionID) {
        this.transactionID = transactionID;
    }
    
    public String getDisplayName() {
        if (GameClient.bClient) {
            if (this.displayName == null || this.displayName.equals("")) {
                this.displayName = this.getUsername();
            }
        }
        else if (!GameServer.bServer) {
            this.displayName = this.getUsername();
        }
        return this.displayName;
    }
    
    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }
    
    public boolean isSeeNonPvpZone() {
        return this.seeNonPvpZone;
    }
    
    public void setSeeNonPvpZone(final boolean seeNonPvpZone) {
        this.seeNonPvpZone = seeNonPvpZone;
    }
    
    public boolean isShowTag() {
        return this.showTag;
    }
    
    public void setShowTag(final boolean showTag) {
        this.showTag = showTag;
    }
    
    public boolean isFactionPvp() {
        return this.factionPvp;
    }
    
    public void setFactionPvp(final boolean factionPvp) {
        this.factionPvp = factionPvp;
    }
    
    public boolean isForceAim() {
        return this.forceAim;
    }
    
    public void setForceAim(final boolean forceAim) {
        this.forceAim = forceAim;
    }
    
    public boolean toggleForceAim() {
        return this.forceAim = !this.forceAim;
    }
    
    public boolean isForceSprint() {
        return this.forceSprint;
    }
    
    public void setForceSprint(final boolean forceSprint) {
        this.forceSprint = forceSprint;
    }
    
    public boolean toggleForceSprint() {
        return this.forceSprint = !this.forceSprint;
    }
    
    public boolean isForceRun() {
        return this.forceRun;
    }
    
    public void setForceRun(final boolean forceRun) {
        this.forceRun = forceRun;
    }
    
    public boolean toggleForceRun() {
        return this.forceRun = !this.forceRun;
    }
    
    public boolean isDeaf() {
        return this.Traits.Deaf.isSet();
    }
    
    public boolean isForceOverrideAnim() {
        return this.forceOverrideAnim;
    }
    
    public void setForceOverrideAnim(final boolean forceOverrideAnim) {
        this.forceOverrideAnim = forceOverrideAnim;
    }
    
    public Long getMechanicsItem(final String s) {
        return this.mechanicsItem.get(Long.parseLong(s));
    }
    
    public boolean isWearingNightVisionGoggles() {
        return this.isWearingNightVisionGoggles;
    }
    
    public void setWearingNightVisionGoggles(final boolean isWearingNightVisionGoggles) {
        this.isWearingNightVisionGoggles = isWearingNightVisionGoggles;
    }
    
    @Override
    public void OnAnimEvent(final AnimLayer animLayer, final AnimEvent animEvent) {
        super.OnAnimEvent(animLayer, animEvent);
        if (this.CharacterActions.isEmpty()) {
            return;
        }
        this.CharacterActions.get(0).OnAnimEvent(animEvent);
    }
    
    @Override
    public void onCullStateChanged(final ModelManager modelManager, final boolean b) {
        super.onCullStateChanged(modelManager, b);
        if (!b) {
            DebugFileWatcher.instance.add(this.m_setClothingTriggerWatcher);
        }
        else {
            DebugFileWatcher.instance.remove(this.m_setClothingTriggerWatcher);
        }
    }
    
    @Override
    public boolean isTimedActionInstant() {
        return ((!GameClient.bClient && !GameServer.bServer) || !"None".equals(this.getAccessLevel())) && super.isTimedActionInstant();
    }
    
    @Override
    public boolean isSkeleton() {
        return false;
    }
    
    @Override
    public void addWorldSoundUnlessInvisible(final int n, final int n2, final boolean b) {
        if (this.isGhostMode()) {
            return;
        }
        super.addWorldSoundUnlessInvisible(n, n2, b);
    }
    
    private void updateFootInjuries() {
        final InventoryItem item = this.getWornItems().getItem("Shoes");
        if (item != null && item.getCondition() > 0) {
            return;
        }
        if (this.getCurrentSquare() == null) {
            return;
        }
        if (this.getCurrentSquare().getBrokenGlass() != null) {
            this.getBodyDamage().getBodyPart(BodyPartType.FromIndex(Rand.Next(BodyPartType.ToIndex(BodyPartType.Foot_L), BodyPartType.ToIndex(BodyPartType.Foot_R) + 1))).generateDeepShardWound();
        }
        int n = 0;
        boolean b = false;
        if (this.getCurrentSquare().getZone() != null && (this.getCurrentSquare().getZone().getType().equals("Forest") || this.getCurrentSquare().getZone().getType().equals("DeepForest"))) {
            b = true;
        }
        final IsoObject floor = this.getCurrentSquare().getFloor();
        if (floor != null && floor.getSprite() != null && floor.getSprite().getName() != null) {
            final String name = floor.getSprite().getName();
            if (name.contains("blends_natural_01") && b) {
                n = 2;
            }
            else if (!name.contains("blends_natural_01") && this.getCurrentSquare().getBuilding() == null) {
                n = 1;
            }
        }
        if (n == 0) {
            return;
        }
        if (this.isWalking && !this.isRunning() && !this.isSprinting()) {
            this.footInjuryTimer += n;
        }
        else if (this.isRunning() && !this.isSprinting()) {
            this.footInjuryTimer += n + 2;
        }
        else {
            if (!this.isSprinting()) {
                if (this.footInjuryTimer > 0 && Rand.Next(3) == 0) {
                    --this.footInjuryTimer;
                }
                return;
            }
            this.footInjuryTimer += n + 5;
        }
        if (Rand.Next(Rand.AdjustForFramerate(8500 - this.footInjuryTimer)) <= 0) {
            this.footInjuryTimer = 0;
            final BodyPart bodyPart = this.getBodyDamage().getBodyPart(BodyPartType.FromIndex(Rand.Next(BodyPartType.ToIndex(BodyPartType.Foot_L), BodyPartType.ToIndex(BodyPartType.Foot_R) + 1)));
            if (bodyPart.getScratchTime() > 30.0f) {
                if (!bodyPart.isCut()) {
                    bodyPart.setCut(true);
                    bodyPart.setCutTime(Rand.Next(1.0f, 3.0f));
                }
                else {
                    bodyPart.setCutTime(bodyPart.getCutTime() + Rand.Next(1.0f, 3.0f));
                }
            }
            else {
                if (!bodyPart.scratched()) {
                    bodyPart.setScratched(true, true);
                    bodyPart.setScratchTime(Rand.Next(1.0f, 3.0f));
                }
                else {
                    bodyPart.setScratchTime(bodyPart.getScratchTime() + Rand.Next(1.0f, 3.0f));
                }
                if (bodyPart.getScratchTime() > 20.0f && bodyPart.getBleedingTime() == 0.0f) {
                    bodyPart.setBleedingTime(Rand.Next(3.0f, 10.0f));
                }
            }
        }
    }
    
    public int getMoodleLevel(final MoodleType moodleType) {
        return this.getMoodles().getMoodleLevel(moodleType);
    }
    
    public boolean isAttackStarted() {
        return this.attackStarted;
    }
    
    @Override
    public boolean isBehaviourMoving() {
        return this.hasPath() || super.isBehaviourMoving();
    }
    
    public boolean isJustMoved() {
        return this.JustMoved;
    }
    
    public void setJustMoved(final boolean justMoved) {
        this.JustMoved = justMoved;
    }
    
    @Override
    public boolean isPlayerMoving() {
        return this.m_isPlayerMoving;
    }
    
    @Override
    public float getTimedActionTimeModifier() {
        if (this.getBodyDamage().getThermoregulator() != null) {
            return this.getBodyDamage().getThermoregulator().getTimedActionTimeModifier();
        }
        return 1.0f;
    }
    
    public boolean isLookingWhileInVehicle() {
        return this.getVehicle() != null && this.bLookingWhileInVehicle;
    }
    
    public void setInitiateAttack(final boolean initiateAttack) {
        this.initiateAttack = initiateAttack;
    }
    
    public boolean isIgnoreInputsForDirection() {
        return this.ignoreInputsForDirection;
    }
    
    public void setIgnoreInputsForDirection(final boolean ignoreInputsForDirection) {
        this.ignoreInputsForDirection = ignoreInputsForDirection;
    }
    
    public boolean isIgnoreContextKey() {
        return this.ignoreContextKey;
    }
    
    public void setIgnoreContextKey(final boolean ignoreContextKey) {
        this.ignoreContextKey = ignoreContextKey;
    }
    
    public boolean isIgnoreAutoVault() {
        return this.ignoreAutoVault;
    }
    
    public void setIgnoreAutoVault(final boolean ignoreAutoVault) {
        this.ignoreAutoVault = ignoreAutoVault;
    }
    
    public boolean isAllowSprint() {
        return this.allowSprint;
    }
    
    public void setAllowSprint(final boolean allowSprint) {
        this.allowSprint = allowSprint;
    }
    
    public boolean isAllowRun() {
        return this.allowRun;
    }
    
    public void setAllowRun(final boolean allowRun) {
        this.allowRun = allowRun;
    }
    
    public String getAttackType() {
        return this.attackType;
    }
    
    public void setAttackType(final String attackType) {
        this.attackType = attackType;
    }
    
    public void clearNetworkEvents() {
        this.networkAI.events.clear();
        this.clearVariable("PerformingAction");
        this.clearVariable("IsPerformingAnAction");
        this.overridePrimaryHandModel = null;
        this.overrideSecondaryHandModel = null;
        this.resetModelNextFrame();
    }
    
    public boolean isCanSeeAll() {
        return this.canSeeAll;
    }
    
    public void setCanSeeAll(final boolean canSeeAll) {
        this.canSeeAll = canSeeAll;
    }
    
    public boolean isNetworkTeleportEnabled() {
        return NetworkTeleport.enable;
    }
    
    public void setNetworkTeleportEnabled(final boolean enable) {
        NetworkTeleport.enable = enable;
    }
    
    public boolean isCheatPlayerSeeEveryone() {
        return DebugOptions.instance.CheatPlayerSeeEveryone.getValue();
    }
    
    public float getRelevantAndDistance(final float n, final float n2, final float n3) {
        if (Math.abs(this.x - n) <= n3 * 10.0f && Math.abs(this.y - n2) <= n3 * 10.0f) {
            return IsoUtils.DistanceTo(this.x, this.y, n, n2);
        }
        return Float.POSITIVE_INFINITY;
    }
    
    public boolean isCanHearAll() {
        return this.canHearAll;
    }
    
    public void setCanHearAll(final boolean canHearAll) {
        this.canHearAll = canHearAll;
    }
    
    public ArrayList<String> getAlreadyReadBook() {
        return this.alreadyReadBook;
    }
    
    public void setMoodleCantSprint(final boolean moodleCantSprint) {
        this.MoodleCantSprint = moodleCantSprint;
    }
    
    public void setAttackFromBehind(final boolean attackFromBehind) {
        this.attackFromBehind = attackFromBehind;
    }
    
    public boolean isAttackFromBehind() {
        return this.attackFromBehind;
    }
    
    public float getDamageFromHitByACar(final float n) {
        float n2 = 1.0f;
        switch (SandboxOptions.instance.DamageToPlayerFromHitByACar.getValue()) {
            case 1: {
                n2 = 0.0f;
                break;
            }
            case 2: {
                n2 = 0.5f;
                break;
            }
            case 4: {
                n2 = 2.0f;
                break;
            }
            case 5: {
                n2 = 5.0f;
                break;
            }
        }
        float n3 = n * n2;
        if (DebugOptions.instance.MultiplayerCriticalHit.getValue()) {
            n3 += 10.0f;
        }
        if (n3 > 0.0f) {
            for (int n4 = (int)(2.0f + n3 * 0.07f), i = 0; i < n4; ++i) {
                final int next = Rand.Next(BodyPartType.ToIndex(BodyPartType.Hand_L), BodyPartType.ToIndex(BodyPartType.MAX));
                final BodyPart bodyPart = this.getBodyDamage().getBodyPart(BodyPartType.FromIndex(next));
                float max = Math.max(Rand.Next(n3 - 15.0f, n3), 5.0f);
                if (this.Traits.FastHealer.isSet()) {
                    max *= (float)0.8;
                }
                else if (this.Traits.SlowHealer.isSet()) {
                    max *= (float)1.2;
                }
                switch (SandboxOptions.instance.InjurySeverity.getValue()) {
                    case 1: {
                        max *= 0.5f;
                        break;
                    }
                    case 3: {
                        max *= 1.5f;
                        break;
                    }
                }
                final float n5 = (float)(max * 0.9);
                bodyPart.AddDamage(n5);
                if (n5 > 40.0f && Rand.Next(12) == 0) {
                    bodyPart.generateDeepWound();
                }
                if (n5 > 10.0f && Rand.Next(100) <= 10 && SandboxOptions.instance.BoneFracture.getValue()) {
                    bodyPart.setFractureTime(Rand.Next(Rand.Next(10.0f, n5 + 10.0f), Rand.Next(n5 + 20.0f, n5 + 30.0f)));
                }
                if (n5 > 30.0f && Rand.Next(100) <= 80 && SandboxOptions.instance.BoneFracture.getValue() && next == BodyPartType.ToIndex(BodyPartType.Head)) {
                    bodyPart.setFractureTime(Rand.Next(Rand.Next(10.0f, n5 + 10.0f), Rand.Next(n5 + 20.0f, n5 + 30.0f)));
                }
                if (n5 > 10.0f && Rand.Next(100) <= 60 && SandboxOptions.instance.BoneFracture.getValue() && next > BodyPartType.ToIndex(BodyPartType.Groin)) {
                    bodyPart.setFractureTime(Rand.Next(Rand.Next(10.0f, n5 + 20.0f), Rand.Next(n5 + 30.0f, n5 + 40.0f)));
                }
            }
            this.getBodyDamage().Update();
        }
        this.addBlood(n);
        if (GameClient.bClient && this.isLocal()) {
            this.updateMovementRates();
            GameClient.sendPlayerInjuries(this);
            GameClient.sendPlayerDamage(this);
        }
        return n3;
    }
    
    @Override
    public float Hit(final BaseVehicle baseVehicle, final float n, final boolean b, final float n2, final float n3) {
        final float doBeatenVehicle = this.doBeatenVehicle(n);
        super.Hit(baseVehicle, n, b, n2, n3);
        return doBeatenVehicle;
    }
    
    @Override
    public void Kill(final IsoGameCharacter isoGameCharacter) {
        if (this.isOnKillDone()) {
            return;
        }
        super.Kill(isoGameCharacter);
        this.getBodyDamage().setOverallBodyHealth(0.0f);
        if (isoGameCharacter == null) {
            this.DoDeath(null, null);
        }
        else {
            this.DoDeath(isoGameCharacter.getUseHandWeapon(), isoGameCharacter);
        }
    }
    
    @Override
    public void becomeCorpse() {
        if (this.isOnDeathDone()) {
            return;
        }
        if (!this.shouldBecomeCorpse()) {
            return;
        }
        super.becomeCorpse();
        final IsoDeadBody isoDeadBody = new IsoDeadBody(this);
        if (this.shouldBecomeZombieAfterDeath()) {
            isoDeadBody.reanimateLater();
        }
    }
    
    @Override
    public void preupdate() {
        if (GameClient.bClient) {
            this.networkAI.updateHitVehicle();
            if (!this.isLocal()) {
                if (this.isKnockedDown() && !this.isOnFloor()) {
                    final HitReactionNetworkAI hitReactionNetworkAI = this.getHitReactionNetworkAI();
                    if (hitReactionNetworkAI.isSetup() && !hitReactionNetworkAI.isStarted()) {
                        hitReactionNetworkAI.start();
                        if (Core.bDebug) {
                            DebugLog.log(DebugType.Multiplayer, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, hitReactionNetworkAI.getDescription()));
                        }
                    }
                }
            }
        }
        super.preupdate();
    }
    
    @Override
    public HitReactionNetworkAI getHitReactionNetworkAI() {
        return this.networkAI.hitReaction;
    }
    
    @Override
    public NetworkCharacterAI getNetworkCharacterAI() {
        return this.networkAI;
    }
    
    public void setFitnessSpeed() {
        this.clearVariable("FitnessStruggle");
        float n = this.getPerkLevel(PerkFactory.Perks.Fitness) / 5.0f / 1.1f - this.getMoodleLevel(MoodleType.Endurance) / 20.0f;
        if (n > 1.5f) {
            n = 1.5f;
        }
        if (n < 0.85f) {
            n = 1.0f;
            this.setVariable("FitnessStruggle", true);
        }
        this.setVariable("FitnessSpeed", n);
    }
    
    @Override
    public boolean isLocal() {
        return super.isLocal() || this.isLocalPlayer();
    }
    
    public boolean isClimbOverWallSuccess() {
        return this.climbOverWallSuccess;
    }
    
    public void setClimbOverWallSuccess(final boolean climbOverWallSuccess) {
        this.climbOverWallSuccess = climbOverWallSuccess;
    }
    
    public boolean isClimbOverWallStruggle() {
        return this.climbOverWallStruggle;
    }
    
    public void setClimbOverWallStruggle(final boolean climbOverWallStruggle) {
        this.climbOverWallStruggle = climbOverWallStruggle;
    }
    
    @Override
    public boolean isVehicleCollisionActive(final BaseVehicle baseVehicle) {
        return super.isVehicleCollisionActive(baseVehicle) && !this.isGodMod() && SwipeStatePlayer.checkPVP(this.vehicle4testCollision.getDriver(), this) && SandboxOptions.instance.DamageToPlayerFromHitByACar.getValue() >= 1 && this.getVehicle() != baseVehicle && !this.isCurrentState(PlayerFallDownState.instance()) && !this.isCurrentState(PlayerFallingState.instance()) && !this.isCurrentState(PlayerKnockedDown.instance());
    }
    
    public boolean isShowMPInfos() {
        return this.showMPInfos;
    }
    
    public void setShowMPInfos(final boolean showMPInfos) {
        this.showMPInfos = showMPInfos;
    }
    
    static {
        IsoPlayer.DEATH_MUSIC_NAME = "PlayerDied";
        IsoPlayer.isTestAIMode = false;
        IsoPlayer.assumedPlayer = 0;
        IsoPlayer.numPlayers = 1;
        players = new IsoPlayer[4];
        instanceLock = "IsoPlayer.instance Lock";
        testHitPosition = new Vector2();
        IsoPlayer.FollowDeadCount = 240;
        StaticTraits = new Stack<String>();
        tempo = new Vector2();
        tempVector2 = new Vector2();
        IsoPlayer.CoopPVP = false;
        m_isoPlayerTriggerWatcher = new PredicatedFileWatcher(ZomboidFileSystem.instance.getMessagingDirSub("Trigger_ResetIsoPlayerModel.xml"), IsoPlayer::onTrigger_ResetIsoPlayerModel);
        IsoPlayer.tempVector2_1 = new Vector2();
        IsoPlayer.tempVector2_2 = new Vector2();
        tempVector3f = new Vector3f();
        s_moveVars = new MoveVars();
        s_targetsProne = new ArrayList<HitInfo>();
        s_targetsStanding = new ArrayList<HitInfo>();
    }
    
    static final class MoveVars
    {
        float moveX;
        float moveY;
        float strafeX;
        float strafeY;
        IsoDirections NewFacing;
    }
    
    static class InputState
    {
        public boolean bMelee;
        public boolean isAttacking;
        public boolean bRunning;
        public boolean bSprinting;
        boolean isAiming;
        boolean isCharging;
        boolean isChargingLT;
    }
    
    private static class VehicleContainer
    {
        BaseVehicle vehicle;
        int containerIndex;
        
        public VehicleContainer set(final BaseVehicle vehicle, final int containerIndex) {
            this.vehicle = vehicle;
            this.containerIndex = containerIndex;
            return this;
        }
        
        @Override
        public boolean equals(final Object o) {
            return o instanceof VehicleContainer && this.vehicle == ((VehicleContainer)o).vehicle && this.containerIndex == ((VehicleContainer)o).containerIndex;
        }
    }
    
    private static class VehicleContainerData
    {
        ArrayList<VehicleContainer> tempContainers;
        ArrayList<VehicleContainer> containers;
        Stack<VehicleContainer> freeContainers;
        
        private VehicleContainerData() {
            this.tempContainers = new ArrayList<VehicleContainer>();
            this.containers = new ArrayList<VehicleContainer>();
            this.freeContainers = new Stack<VehicleContainer>();
        }
    }
    
    private static class s_performance
    {
        static final PerformanceProfileProbe postUpdate;
        static final PerformanceProfileProbe highlightRangedTargets;
        static final PerformanceProfileProbe update;
        
        static {
            postUpdate = new PerformanceProfileProbe("IsoPlayer.postUpdate");
            highlightRangedTargets = new PerformanceProfileProbe("IsoPlayer.highlightRangedTargets");
            update = new PerformanceProfileProbe("IsoPlayer.update");
        }
    }
}

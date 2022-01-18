// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters;

import zombie.characters.BodyDamage.BodyPartLast;
import gnu.trove.map.hash.THashMap;
import zombie.core.profiling.PerformanceProfileProbe;
import zombie.scripting.objects.VehicleScript;
import zombie.vehicles.VehicleLight;
import zombie.popman.ObjectPool;
import zombie.characters.action.ActionState;
import zombie.core.skinnedmodel.advancedanimation.AnimNode;
import zombie.ai.states.PlayerKnockedDown;
import zombie.ai.states.PlayerFallDownState;
import zombie.audio.BaseSoundEmitter;
import fmod.fmod.FMODManager;
import zombie.SoundManager;
import zombie.ai.states.PlayerOnGroundState;
import zombie.characters.AttachedItems.AttachedItem;
import zombie.debug.LogSeverity;
import zombie.audio.FMODParameter;
import fmod.fmod.FMOD_STUDIO_PARAMETER_DESCRIPTION;
import java.util.BitSet;
import zombie.audio.GameSoundClip;
import zombie.core.skinnedmodel.advancedanimation.LiveAnimNode;
import zombie.characters.BodyDamage.Metabolics;
import zombie.iso.IsoLightSource;
import zombie.ai.states.AttackState;
import zombie.core.skinnedmodel.population.OutfitRNG;
import zombie.characterTextures.BloodClothingType;
import zombie.core.skinnedmodel.advancedanimation.debug.AnimatorDebugMonitor;
import zombie.network.ServerGUI;
import zombie.characters.action.ActionStateSnapshot;
import zombie.core.skinnedmodel.advancedanimation.AnimationVariableType;
import zombie.core.skinnedmodel.advancedanimation.AnimationVariableSlotCallbackInt;
import zombie.core.skinnedmodel.advancedanimation.AnimationVariableSlotCallbackFloat;
import zombie.core.skinnedmodel.advancedanimation.AnimationVariableSlotCallbackString;
import zombie.core.skinnedmodel.advancedanimation.AnimationVariableSlotCallbackBool;
import zombie.core.skinnedmodel.advancedanimation.IAnimationVariableSlot;
import zombie.core.skinnedmodel.advancedanimation.AnimationVariableHandle;
import zombie.ai.states.ThumpState;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Recipe;
import zombie.inventory.types.Drainable;
import java.util.Map;
import zombie.characters.traits.TraitFactory;
import zombie.iso.IsoChunk;
import zombie.core.skinnedmodel.advancedanimation.IAnimationVariableSource;
import se.krka.kahlua.vm.KahluaTable;
import zombie.audio.parameters.ParameterZombieState;
import zombie.iso.objects.interfaces.BarricadeAble;
import zombie.ZomboidGlobals;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.iso.IsoRoofFixer;
import zombie.core.skinnedmodel.population.HairStyle;
import zombie.core.skinnedmodel.population.HairStyles;
import zombie.core.skinnedmodel.population.BeardStyle;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.core.skinnedmodel.population.BeardStyles;
import zombie.iso.objects.RainManager;
import zombie.SystemDisabler;
import zombie.AmbientStreamManager;
import zombie.iso.objects.IsoThumpable;
import zombie.ai.states.ClimbDownSheetRopeState;
import zombie.ai.states.ClimbSheetRopeState;
import zombie.iso.objects.IsoWindowFrame;
import zombie.ai.states.CloseWindowState;
import zombie.ai.states.OpenWindowState;
import zombie.iso.objects.IsoWindow;
import zombie.ai.states.SmashWindowState;
import zombie.vehicles.VehiclePart;
import zombie.characters.WornItems.WornItem;
import zombie.inventory.types.Clothing;
import zombie.iso.objects.IsoFallingClothing;
import zombie.inventory.InventoryItemFactory;
import zombie.scripting.objects.Item;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.iso.objects.IsoMolotovCocktail;
import zombie.iso.objects.IsoBall;
import zombie.iso.LightingJNI;
import java.util.Iterator;
import zombie.ui.ActionProgressBar;
import zombie.characters.CharacterTimedActions.LuaTimedActionNew;
import zombie.debug.LineDrawer;
import zombie.core.Colors;
import zombie.input.Mouse;
import zombie.iso.IsoObjectPicker;
import zombie.radio.ZomboidRadio;
import zombie.network.chat.ChatType;
import zombie.chat.ChatManager;
import zombie.profanity.ProfanityFilter;
import zombie.ui.TutorialManager;
import org.joml.Vector3f;
import zombie.iso.objects.IsoDeadBody;
import zombie.ai.states.FakeDeadZombieState;
import zombie.gameStates.IngameState;
import org.joml.Vector3fc;
import zombie.interfaces.IUpdater;
import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;
import zombie.ai.astar.AStarPathFinder;
import zombie.ai.states.PathFindState;
import zombie.core.PerformanceSettings;
import zombie.core.SpriteRenderer;
import zombie.core.opengl.Shader;
import zombie.ui.UIFont;
import zombie.ui.TextManager;
import zombie.iso.IsoCamera;
import zombie.ai.states.PlayerHitReactionPVPState;
import zombie.ai.states.PlayerHitReactionState;
import zombie.network.ServerMap;
import zombie.ai.states.SwipeStatePlayer;
import zombie.ai.states.LungeNetworkState;
import zombie.ai.states.LungeState;
import zombie.iso.IsoUtils;
import zombie.Lua.LuaHookManager;
import zombie.iso.objects.IsoFireManager;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.VirtualZombieManager;
import zombie.characters.BodyDamage.Nutrition;
import zombie.core.BoxedStaticValues;
import zombie.Lua.LuaManager;
import zombie.core.math.PZMath;
import zombie.inventory.types.Food;
import zombie.ui.UIManager;
import java.io.IOException;
import zombie.GameWindow;
import java.nio.ByteBuffer;
import zombie.WorldSoundManager;
import zombie.core.Translator;
import zombie.iso.LosUtil;
import java.util.Collection;
import zombie.inventory.types.Literature;
import java.util.Objects;
import zombie.characters.Moodles.MoodleType;
import zombie.characters.BodyDamage.BodyPart;
import zombie.characterTextures.BloodBodyPartType;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.core.skinnedmodel.advancedanimation.AnimLayer;
import zombie.iso.RoomDef;
import zombie.iso.BuildingDef;
import zombie.characters.traits.TraitCollection;
import zombie.characters.AttachedItems.AttachedLocationGroup;
import zombie.util.Type;
import zombie.characters.AttachedItems.AttachedLocations;
import zombie.characters.WornItems.BodyLocationGroup;
import zombie.characters.WornItems.BodyLocations;
import zombie.inventory.types.WeaponType;
import zombie.core.skinnedmodel.visual.BaseVisual;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.iso.objects.IsoZombieGiblets;
import zombie.SandboxOptions;
import zombie.network.chat.ChatServer;
import zombie.network.ServerOptions;
import zombie.core.logger.LoggerManager;
import zombie.core.znet.SteamUtils;
import zombie.iso.areas.IsoRoom;
import zombie.PersistentOutfits;
import zombie.debug.DebugType;
import zombie.DebugFileWatcher;
import zombie.core.logger.ExceptionLogger;
import zombie.core.skinnedmodel.population.ClothingItem;
import zombie.core.skinnedmodel.population.ClothingItemReference;
import zombie.util.list.PZArrayUtil;
import zombie.core.skinnedmodel.population.Outfit;
import zombie.Lua.LuaEventManager;
import zombie.core.skinnedmodel.visual.IHumanVisual;
import zombie.core.skinnedmodel.population.OutfitManager;
import zombie.core.skinnedmodel.model.Model;
import zombie.util.Pool;
import zombie.core.skinnedmodel.ModelManager;
import zombie.ai.states.PlayerGetUpState;
import zombie.ai.states.CollideWithWallState;
import zombie.ai.states.AttackNetworkState;
import zombie.ai.states.ZombieOnGroundState;
import zombie.ai.states.ZombieFallingState;
import zombie.ai.states.ZombieFallDownState;
import zombie.ai.states.ZombieHitReactionState;
import zombie.ai.states.StaggerBackState;
import zombie.ai.states.ClimbOverWallState;
import zombie.debug.DebugLog;
import zombie.ai.states.WalkTowardState;
import zombie.ai.states.ClimbThroughWindowState;
import zombie.ai.states.ClimbOverFenceState;
import zombie.iso.IsoWorld;
import zombie.iso.objects.IsoTree;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.ai.states.BumpedState;
import zombie.util.StringUtils;
import zombie.core.skinnedmodel.animation.AnimationTrack;
import zombie.GameTime;
import zombie.characters.skills.PerkFactory;
import zombie.debug.DebugOptions;
import zombie.ZomboidFileSystem;
import zombie.core.skinnedmodel.advancedanimation.AnimationSet;
import zombie.network.GameClient;
import zombie.ai.states.IdleState;
import zombie.core.Rand;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.network.GameServer;
import zombie.core.Core;
import java.util.UUID;
import zombie.iso.IsoCell;
import zombie.iso.Vector3;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.core.skinnedmodel.animation.debug.AnimationPlayerRecorder;
import zombie.PredicatedFileWatcher;
import zombie.core.utils.UpdateLimit;
import zombie.iso.IsoDirections;
import zombie.network.NetworkVariables;
import zombie.ai.sadisticAIDirector.SleepingEventData;
import zombie.core.skinnedmodel.model.ModelInstanceTextureCreator;
import zombie.vehicles.PathFindBehavior2;
import zombie.network.packets.hit.HitInfo;
import zombie.network.packets.hit.AttackVars;
import zombie.ai.MapKnowledge;
import zombie.vehicles.PolygonalMap2;
import zombie.inventory.types.Radio;
import zombie.chat.ChatMessage;
import zombie.iso.IsoObject;
import java.util.List;
import zombie.vehicles.BaseVehicle;
import zombie.ui.TextDrawObject;
import zombie.chat.ChatElement;
import zombie.iso.IsoGridSquare;
import zombie.inventory.types.HandWeapon;
import zombie.characters.Moodles.Moodles;
import zombie.ai.StateMachine;
import zombie.core.Color;
import zombie.inventory.ItemContainer;
import zombie.ai.astar.AStarPathFinderResult;
import zombie.iso.areas.IsoBuilding;
import zombie.characters.AttachedItems.AttachedItems;
import zombie.characters.WornItems.WornItems;
import zombie.characters.BodyDamage.BodyDamage;
import zombie.inventory.types.InventoryContainer;
import zombie.characters.CharacterTimedActions.BaseAction;
import java.util.Stack;
import zombie.iso.sprite.IsoSprite;
import zombie.ai.State;
import zombie.core.skinnedmodel.advancedanimation.AdvancedAnimator;
import zombie.core.skinnedmodel.animation.AnimationPlayer;
import zombie.core.skinnedmodel.advancedanimation.AnimationVariableSource;
import zombie.audio.FMODParameterList;
import zombie.characters.action.ActionContext;
import zombie.core.skinnedmodel.model.ModelInstance;
import zombie.ai.GameCharacterAIBrain;
import zombie.inventory.InventoryItem;
import java.util.ArrayList;
import zombie.core.textures.ColorInfo;
import zombie.iso.Vector2;
import java.util.HashMap;
import fmod.fmod.IFMODParameterUpdater;
import zombie.core.skinnedmodel.advancedanimation.IAnimEventCallback;
import zombie.characters.action.IActionStateChanged;
import zombie.core.skinnedmodel.population.IClothingItemListener;
import zombie.core.skinnedmodel.advancedanimation.IAnimationVariableMap;
import zombie.core.skinnedmodel.advancedanimation.IAnimatable;
import zombie.chat.ChatElementOwner;
import zombie.iso.IsoMovingObject;

public abstract class IsoGameCharacter extends IsoMovingObject implements Talker, ChatElementOwner, IAnimatable, IAnimationVariableMap, IClothingItemListener, IActionStateChanged, IAnimEventCallback, IFMODParameterUpdater, ILuaVariableSource, ILuaGameCharacter
{
    private boolean ignoreAimingInput;
    public boolean doRenderShadow;
    private boolean doDeathSound;
    private boolean canShout;
    public boolean doDirtBloodEtc;
    private static int IID;
    public static final int RENDER_OFFSET_X = 1;
    public static final int RENDER_OFFSET_Y = -89;
    public static final float s_maxPossibleTwist = 70.0f;
    private static final HashMap<Integer, SurvivorDesc> SurvivorMap;
    private static final int[] LevelUpLevels;
    protected static final Vector2 tempo;
    protected static final ColorInfo inf;
    public long vocalEvent;
    public long removedFromWorldMS;
    private boolean bSneaking;
    protected static final Vector2 tempo2;
    private static final Vector2 tempVector2_1;
    private static final Vector2 tempVector2_2;
    private static String sleepText;
    protected final ArrayList<InventoryItem> savedInventoryItems;
    private final String instancename;
    protected GameCharacterAIBrain GameCharacterAIBrain;
    public final ArrayList<String> amputations;
    public ModelInstance hair;
    public ModelInstance beard;
    public ModelInstance primaryHandModel;
    public ModelInstance secondaryHandModel;
    public final ActionContext actionContext;
    public final BaseCharacterSoundEmitter emitter;
    private final FMODParameterList fmodParameters;
    private final AnimationVariableSource m_GameVariables;
    private AnimationVariableSource m_PlaybackGameVariables;
    private boolean bRunning;
    private boolean bSprinting;
    private boolean m_godMod;
    private boolean m_invisible;
    private boolean m_avoidDamage;
    public boolean callOut;
    public IsoGameCharacter ReanimatedCorpse;
    public int ReanimatedCorpseID;
    private AnimationPlayer m_animPlayer;
    public final AdvancedAnimator advancedAnimator;
    public final HashMap<State, HashMap<Object, Object>> StateMachineParams;
    public long clientIgnoreCollision;
    private boolean isCrit;
    private boolean bKnockedDown;
    public int bumpNbr;
    public boolean upKillCount;
    private final ArrayList<PerkInfo> PerkList;
    private final Vector2 m_forwardDirection;
    public boolean Asleep;
    public boolean blockTurning;
    public float speedMod;
    public IsoSprite legsSprite;
    private boolean bFemale;
    public float knockbackAttackMod;
    public final boolean[] IsVisibleToPlayer;
    public float savedVehicleX;
    public float savedVehicleY;
    public short savedVehicleSeat;
    public boolean savedVehicleRunning;
    private static final float RecoilDelayDecrease = 0.625f;
    protected static final float BeenMovingForIncrease = 1.25f;
    protected static final float BeenMovingForDecrease = 0.625f;
    private IsoGameCharacter FollowingTarget;
    private final ArrayList<IsoMovingObject> LocalList;
    private final ArrayList<IsoMovingObject> LocalNeutralList;
    private final ArrayList<IsoMovingObject> LocalGroupList;
    private final ArrayList<IsoMovingObject> LocalRelevantEnemyList;
    private float dangerLevels;
    private static final Vector2 tempVector2;
    private float leaveBodyTimedown;
    protected boolean AllowConversation;
    private float ReanimateTimer;
    private int ReanimAnimFrame;
    private int ReanimAnimDelay;
    private boolean Reanim;
    private boolean VisibleToNPCs;
    private int DieCount;
    private float llx;
    private float lly;
    private float llz;
    protected int RemoteID;
    protected int NumSurvivorsInVicinity;
    private float LevelUpMultiplier;
    protected XP xp;
    private int LastLocalEnemies;
    private final ArrayList<IsoMovingObject> VeryCloseEnemyList;
    private final HashMap<String, Location> LastKnownLocation;
    protected IsoGameCharacter AttackedBy;
    protected boolean IgnoreStaggerBack;
    protected boolean AttackWasSuperAttack;
    private int TimeThumping;
    private int PatienceMax;
    private int PatienceMin;
    private int Patience;
    protected final Stack<BaseAction> CharacterActions;
    private int ZombieKills;
    private int SurvivorKills;
    private int LastZombieKills;
    protected boolean superAttack;
    protected float ForceWakeUpTime;
    private float fullSpeedMod;
    protected float runSpeedModifier;
    private float walkSpeedModifier;
    private float combatSpeedModifier;
    private boolean bRangedWeaponEmpty;
    public ArrayList<InventoryContainer> bagsWorn;
    protected boolean ForceWakeUp;
    protected final BodyDamage BodyDamage;
    private BodyDamage BodyDamageRemote;
    private State defaultState;
    protected WornItems wornItems;
    protected AttachedItems attachedItems;
    protected ClothingWetness clothingWetness;
    protected SurvivorDesc descriptor;
    private final Stack<IsoBuilding> FamiliarBuildings;
    protected final AStarPathFinderResult finder;
    private float FireKillRate;
    private int FireSpreadProbability;
    protected float Health;
    protected boolean bDead;
    protected boolean bKill;
    protected boolean bPlayingDeathSound;
    private boolean bDeathDragDown;
    protected String hurtSound;
    protected ItemContainer inventory;
    protected InventoryItem leftHandItem;
    private int NextWander;
    private boolean OnFire;
    private int pathIndex;
    protected InventoryItem rightHandItem;
    protected Color SpeakColour;
    protected float slowFactor;
    protected float slowTimer;
    protected boolean bUseParts;
    protected boolean Speaking;
    private float SpeakTime;
    private float staggerTimeMod;
    protected final StateMachine stateMachine;
    protected final Moodles Moodles;
    protected final Stats stats;
    private final Stack<String> UsedItemsOn;
    protected HandWeapon useHandWeapon;
    protected IsoGridSquare attackTargetSquare;
    private float BloodImpactX;
    private float BloodImpactY;
    private float BloodImpactZ;
    private IsoSprite bloodSplat;
    private boolean bOnBed;
    private final Vector2 moveForwardVec;
    protected boolean pathing;
    protected ChatElement chatElement;
    private final Stack<IsoGameCharacter> LocalEnemyList;
    protected final Stack<IsoGameCharacter> EnemyList;
    public final CharacterTraits Traits;
    private int maxWeight;
    private int maxWeightBase;
    private float SleepingTabletDelta;
    private float BetaEffect;
    private float DepressEffect;
    private float SleepingTabletEffect;
    private float BetaDelta;
    private float DepressDelta;
    private float DepressFirstTakeTime;
    private float PainEffect;
    private float PainDelta;
    private boolean bDoDefer;
    private float haloDispTime;
    protected TextDrawObject userName;
    private TextDrawObject haloNote;
    private final HashMap<String, String> namesPrefix;
    private static final String namePvpSuffix = " [img=media/ui/Skull.png]";
    private static final String nameCarKeySuffix = " [img=media/ui/CarKey.png";
    private static final String voiceSuffix = "[img=media/ui/voiceon.png] ";
    private static final String voiceMuteSuffix = "[img=media/ui/voicemuted.png] ";
    protected IsoPlayer isoPlayer;
    private boolean hasInitTextObjects;
    private boolean canSeeCurrent;
    private boolean drawUserName;
    private final Location LastHeardSound;
    private float lrx;
    private float lry;
    protected boolean bClimbing;
    private boolean lastCollidedW;
    private boolean lastCollidedN;
    protected float fallTime;
    protected float lastFallSpeed;
    protected boolean bFalling;
    protected BaseVehicle vehicle;
    boolean isNPC;
    private long lastBump;
    private IsoGameCharacter bumpedChr;
    private boolean m_isCulled;
    private int age;
    private int lastHitCount;
    private boolean safety;
    private float safetyCooldown;
    private float meleeDelay;
    private float RecoilDelay;
    private float BeenMovingFor;
    private float BeenSprintingFor;
    private boolean forceShove;
    private String clickSound;
    private float reduceInfectionPower;
    private final List<String> knownRecipes;
    private int lastHourSleeped;
    protected float timeOfSleep;
    protected float delayToActuallySleep;
    private String bedType;
    private IsoObject bed;
    private boolean isReading;
    private float timeSinceLastSmoke;
    private boolean wasOnStairs;
    private ChatMessage lastChatMessage;
    private String lastSpokenLine;
    private boolean unlimitedEndurance;
    private boolean unlimitedCarry;
    private boolean buildCheat;
    private boolean farmingCheat;
    private boolean healthCheat;
    private boolean mechanicsCheat;
    private boolean movablesCheat;
    private boolean timedActionInstantCheat;
    private boolean showAdminTag;
    private long isAnimForecasted;
    private boolean fallOnFront;
    private boolean hitFromBehind;
    private String hitReaction;
    private String bumpType;
    private boolean m_isBumpDone;
    private boolean m_bumpFall;
    private boolean m_bumpStaggered;
    private String m_bumpFallType;
    private int sleepSpeechCnt;
    private Radio equipedRadio;
    private InventoryItem leftHandCache;
    private InventoryItem rightHandCache;
    private final ArrayList<ReadBook> ReadBooks;
    private final LightInfo lightInfo;
    private final LightInfo lightInfo2;
    private PolygonalMap2.Path path2;
    private final MapKnowledge mapKnowledge;
    public final AttackVars attackVars;
    public final ArrayList<HitInfo> hitList;
    private final PathFindBehavior2 pfb2;
    private final InventoryItem[] cacheEquiped;
    private boolean bAimAtFloor;
    protected int m_persistentOutfitId;
    protected boolean m_bPersistentOutfitInit;
    private boolean bUpdateModelTextures;
    private ModelInstanceTextureCreator textureCreator;
    public boolean bUpdateEquippedTextures;
    private final ArrayList<ModelInstance> readyModelData;
    private boolean sitOnGround;
    private boolean ignoreMovement;
    private boolean hideWeaponModel;
    private boolean isAiming;
    private float beardGrowTiming;
    private float hairGrowTiming;
    private float m_moveDelta;
    protected float m_turnDeltaNormal;
    protected float m_turnDeltaRunning;
    protected float m_turnDeltaSprinting;
    private float m_maxTwist;
    private boolean m_isMoving;
    private boolean m_isTurning;
    private boolean m_isTurningAround;
    private boolean m_isTurning90;
    public long lastAutomaticShoot;
    public int shootInARow;
    private boolean invincible;
    private float lungeFallTimer;
    private SleepingEventData m_sleepingEventData;
    private final int HAIR_GROW_TIME = 20;
    private final int BEARD_GROW_TIME = 5;
    public float realx;
    public float realy;
    public byte realz;
    public NetworkVariables.ZombieState realState;
    public IsoDirections realdir;
    public String overridePrimaryHandModel;
    public String overrideSecondaryHandModel;
    public boolean forceNullOverride;
    protected final UpdateLimit ulBeatenVehicle;
    private float m_momentumScalar;
    private final HashMap<String, State> m_stateUpdateLookup;
    private boolean attackAnim;
    private NetworkTeleport teleport;
    public ArrayList<Integer> invRadioFreq;
    private final PredicatedFileWatcher m_animStateTriggerWatcher;
    private final AnimationPlayerRecorder m_animationRecorder;
    private final String m_UID;
    private boolean m_bDebugVariablesRegistered;
    private float effectiveEdibleBuffTimer;
    private float m_shadowFM;
    private float m_shadowBM;
    private long shadowTick;
    private static final ItemVisuals tempItemVisuals;
    private static final ArrayList<IsoMovingObject> movingStatic;
    private long m_muzzleFlash;
    private static final Bandages s_bandages;
    private static final Vector3 tempVector;
    private static final Vector3 tempVectorBonePos;
    public final NetworkCharacter networkCharacter;
    
    public IsoGameCharacter(final IsoCell isoCell, final float scriptnx, final float scriptny, final float z) {
        super(isoCell, false);
        this.ignoreAimingInput = false;
        this.doRenderShadow = true;
        this.doDeathSound = true;
        this.canShout = true;
        this.doDirtBloodEtc = true;
        this.removedFromWorldMS = 0L;
        this.bSneaking = false;
        this.savedInventoryItems = new ArrayList<InventoryItem>();
        this.amputations = new ArrayList<String>();
        this.actionContext = new ActionContext(this);
        this.fmodParameters = new FMODParameterList();
        this.m_GameVariables = new AnimationVariableSource();
        this.m_PlaybackGameVariables = null;
        this.bRunning = false;
        this.bSprinting = false;
        this.m_godMod = false;
        this.m_invisible = false;
        this.m_avoidDamage = false;
        this.callOut = false;
        this.ReanimatedCorpseID = -1;
        this.m_animPlayer = null;
        this.StateMachineParams = new HashMap<State, HashMap<Object, Object>>();
        this.clientIgnoreCollision = 0L;
        this.isCrit = false;
        this.bKnockedDown = false;
        this.bumpNbr = 0;
        this.upKillCount = true;
        this.PerkList = new ArrayList<PerkInfo>();
        this.m_forwardDirection = new Vector2();
        this.Asleep = false;
        this.blockTurning = false;
        this.speedMod = 1.0f;
        this.bFemale = true;
        this.knockbackAttackMod = 1.0f;
        this.IsVisibleToPlayer = new boolean[4];
        this.savedVehicleSeat = -1;
        this.FollowingTarget = null;
        this.LocalList = new ArrayList<IsoMovingObject>();
        this.LocalNeutralList = new ArrayList<IsoMovingObject>();
        this.LocalGroupList = new ArrayList<IsoMovingObject>();
        this.LocalRelevantEnemyList = new ArrayList<IsoMovingObject>();
        this.dangerLevels = 0.0f;
        this.leaveBodyTimedown = 0.0f;
        this.AllowConversation = true;
        this.Reanim = false;
        this.VisibleToNPCs = true;
        this.DieCount = 0;
        this.llx = 0.0f;
        this.lly = 0.0f;
        this.llz = 0.0f;
        this.RemoteID = -1;
        this.NumSurvivorsInVicinity = 0;
        this.LevelUpMultiplier = 2.5f;
        this.xp = null;
        this.LastLocalEnemies = 0;
        this.VeryCloseEnemyList = new ArrayList<IsoMovingObject>();
        this.LastKnownLocation = new HashMap<String, Location>();
        this.AttackedBy = null;
        this.IgnoreStaggerBack = false;
        this.AttackWasSuperAttack = false;
        this.TimeThumping = 0;
        this.PatienceMax = 150;
        this.PatienceMin = 20;
        this.Patience = 20;
        this.CharacterActions = new Stack<BaseAction>();
        this.ZombieKills = 0;
        this.SurvivorKills = 0;
        this.LastZombieKills = 0;
        this.superAttack = false;
        this.ForceWakeUpTime = -1.0f;
        this.fullSpeedMod = 1.0f;
        this.runSpeedModifier = 1.0f;
        this.walkSpeedModifier = 1.0f;
        this.combatSpeedModifier = 1.0f;
        this.bRangedWeaponEmpty = false;
        this.BodyDamageRemote = null;
        this.wornItems = null;
        this.attachedItems = null;
        this.clothingWetness = null;
        this.FamiliarBuildings = new Stack<IsoBuilding>();
        this.finder = new AStarPathFinderResult();
        this.FireKillRate = 0.0038f;
        this.FireSpreadProbability = 6;
        this.Health = 1.0f;
        this.bDead = false;
        this.bKill = false;
        this.bPlayingDeathSound = false;
        this.bDeathDragDown = false;
        this.hurtSound = "MaleZombieHurt";
        this.inventory = new ItemContainer();
        this.NextWander = 200;
        this.OnFire = false;
        this.pathIndex = 0;
        this.SpeakColour = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        this.slowFactor = 0.0f;
        this.slowTimer = 0.0f;
        this.bUseParts = false;
        this.Speaking = false;
        this.SpeakTime = 0.0f;
        this.staggerTimeMod = 1.0f;
        this.stats = new Stats();
        this.UsedItemsOn = new Stack<String>();
        this.useHandWeapon = null;
        this.BloodImpactX = 0.0f;
        this.BloodImpactY = 0.0f;
        this.BloodImpactZ = 0.0f;
        this.bOnBed = false;
        this.moveForwardVec = new Vector2();
        this.pathing = false;
        this.LocalEnemyList = new Stack<IsoGameCharacter>();
        this.EnemyList = new Stack<IsoGameCharacter>();
        this.Traits = new CharacterTraits();
        this.maxWeight = 8;
        this.maxWeightBase = 8;
        this.SleepingTabletDelta = 1.0f;
        this.BetaEffect = 0.0f;
        this.DepressEffect = 0.0f;
        this.SleepingTabletEffect = 0.0f;
        this.BetaDelta = 0.0f;
        this.DepressDelta = 0.0f;
        this.DepressFirstTakeTime = -1.0f;
        this.PainEffect = 0.0f;
        this.PainDelta = 0.0f;
        this.bDoDefer = true;
        this.haloDispTime = 128.0f;
        this.namesPrefix = new HashMap<String, String>();
        this.isoPlayer = null;
        this.hasInitTextObjects = false;
        this.canSeeCurrent = false;
        this.drawUserName = false;
        this.LastHeardSound = new Location(-1, -1, -1);
        this.lrx = 0.0f;
        this.lry = 0.0f;
        this.bClimbing = false;
        this.lastCollidedW = false;
        this.lastCollidedN = false;
        this.fallTime = 0.0f;
        this.lastFallSpeed = 0.0f;
        this.bFalling = false;
        this.vehicle = null;
        this.isNPC = false;
        this.lastBump = 0L;
        this.bumpedChr = null;
        this.m_isCulled = true;
        this.age = 25;
        this.lastHitCount = 0;
        this.safety = true;
        this.safetyCooldown = 0.0f;
        this.meleeDelay = 0.0f;
        this.RecoilDelay = 0.0f;
        this.BeenMovingFor = 0.0f;
        this.BeenSprintingFor = 0.0f;
        this.forceShove = false;
        this.clickSound = null;
        this.reduceInfectionPower = 0.0f;
        this.knownRecipes = new ArrayList<String>();
        this.lastHourSleeped = 0;
        this.timeOfSleep = 0.0f;
        this.delayToActuallySleep = 0.0f;
        this.bedType = "averageBed";
        this.bed = null;
        this.isReading = false;
        this.timeSinceLastSmoke = 0.0f;
        this.wasOnStairs = false;
        this.unlimitedEndurance = false;
        this.unlimitedCarry = false;
        this.buildCheat = false;
        this.farmingCheat = false;
        this.healthCheat = false;
        this.mechanicsCheat = false;
        this.movablesCheat = false;
        this.timedActionInstantCheat = false;
        this.showAdminTag = true;
        this.isAnimForecasted = 0L;
        this.fallOnFront = false;
        this.hitFromBehind = false;
        this.hitReaction = "";
        this.bumpType = "";
        this.m_isBumpDone = false;
        this.m_bumpFall = false;
        this.m_bumpStaggered = false;
        this.m_bumpFallType = "";
        this.sleepSpeechCnt = 0;
        this.ReadBooks = new ArrayList<ReadBook>();
        this.lightInfo = new LightInfo();
        this.lightInfo2 = new LightInfo();
        this.mapKnowledge = new MapKnowledge();
        this.attackVars = new AttackVars();
        this.hitList = new ArrayList<HitInfo>();
        this.pfb2 = new PathFindBehavior2(this);
        this.cacheEquiped = new InventoryItem[2];
        this.bAimAtFloor = false;
        this.m_persistentOutfitId = 0;
        this.m_bPersistentOutfitInit = false;
        this.bUpdateModelTextures = false;
        this.textureCreator = null;
        this.bUpdateEquippedTextures = false;
        this.readyModelData = new ArrayList<ModelInstance>();
        this.sitOnGround = false;
        this.ignoreMovement = false;
        this.hideWeaponModel = false;
        this.isAiming = false;
        this.beardGrowTiming = -1.0f;
        this.hairGrowTiming = -1.0f;
        this.m_moveDelta = 1.0f;
        this.m_turnDeltaNormal = 1.0f;
        this.m_turnDeltaRunning = 0.8f;
        this.m_turnDeltaSprinting = 0.75f;
        this.m_maxTwist = 15.0f;
        this.m_isMoving = false;
        this.m_isTurning = false;
        this.m_isTurningAround = false;
        this.m_isTurning90 = false;
        this.lastAutomaticShoot = 0L;
        this.shootInARow = 0;
        this.invincible = false;
        this.lungeFallTimer = 0.0f;
        this.realx = 0.0f;
        this.realy = 0.0f;
        this.realz = 0;
        this.realState = NetworkVariables.ZombieState.Idle;
        this.realdir = IsoDirections.fromIndex(0);
        this.overridePrimaryHandModel = null;
        this.overrideSecondaryHandModel = null;
        this.forceNullOverride = false;
        this.ulBeatenVehicle = new UpdateLimit(200L);
        this.m_momentumScalar = 0.0f;
        this.m_stateUpdateLookup = new HashMap<String, State>();
        this.attackAnim = false;
        this.teleport = null;
        this.invRadioFreq = new ArrayList<Integer>();
        this.m_bDebugVariablesRegistered = false;
        this.effectiveEdibleBuffTimer = 0.0f;
        this.m_shadowFM = 0.0f;
        this.m_shadowBM = 0.0f;
        this.shadowTick = -1L;
        this.m_muzzleFlash = -1L;
        this.networkCharacter = new NetworkCharacter();
        this.m_UID = String.format("%s-%s", this.getClass().getSimpleName(), UUID.randomUUID().toString());
        this.registerVariableCallbacks();
        this.instancename = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, IsoGameCharacter.IID);
        ++IsoGameCharacter.IID;
        if (!(this instanceof IsoSurvivor)) {
            this.emitter = ((Core.SoundDisabled || GameServer.bServer) ? new DummyCharacterSoundEmitter(this) : new CharacterSoundEmitter(this));
        }
        else {
            this.emitter = null;
        }
        if (scriptnx != 0.0f || scriptny != 0.0f || z != 0.0f) {
            if (this.getCell().isSafeToAdd()) {
                this.getCell().getObjectList().add(this);
            }
            else {
                this.getCell().getAddList().add(this);
            }
        }
        if (this.def == null) {
            this.def = IsoSpriteInstance.get(this.sprite);
        }
        if (this instanceof IsoPlayer) {
            this.BodyDamage = new BodyDamage(this);
            this.Moodles = new Moodles(this);
            this.xp = new XP(this);
        }
        else {
            this.BodyDamage = null;
            this.Moodles = null;
            this.xp = null;
        }
        this.Patience = Rand.Next(this.PatienceMin, this.PatienceMax);
        this.x = scriptnx + 0.5f;
        this.y = scriptny + 0.5f;
        this.z = z;
        this.nx = scriptnx;
        this.lx = scriptnx;
        this.scriptnx = scriptnx;
        this.ny = scriptny;
        this.ly = scriptny;
        this.scriptny = scriptny;
        if (isoCell != null) {
            this.current = this.getCell().getGridSquare((int)scriptnx, (int)scriptny, (int)z);
        }
        this.offsetY = 0.0f;
        this.offsetX = 0.0f;
        this.stateMachine = new StateMachine(this);
        this.setDefaultState(IdleState.instance());
        this.inventory.parent = this;
        this.inventory.setExplored(true);
        this.chatElement = new ChatElement(this, 1, "character");
        if (GameClient.bClient || GameServer.bServer) {
            this.namesPrefix.put("admin", "[col=255,0,0]Admin[/] ");
            this.namesPrefix.put("moderator", "[col=0,128,47]Moderator[/] ");
            this.namesPrefix.put("overseer", "[col=26,26,191]Overseer[/] ");
            this.namesPrefix.put("gm", "[col=213,123,23]GM[/] ");
            this.namesPrefix.put("observer", "[col=128,128,128]Observer[/] ");
        }
        this.m_animationRecorder = new AnimationPlayerRecorder(this);
        (this.advancedAnimator = new AdvancedAnimator()).init(this);
        this.advancedAnimator.animCallbackHandlers.add(this);
        this.advancedAnimator.SetAnimSet(AnimationSet.GetAnimationSet(this.GetAnimSetName(), false));
        this.advancedAnimator.setRecorder(this.m_animationRecorder);
        this.actionContext.onStateChanged.add(this);
        this.m_animStateTriggerWatcher = new PredicatedFileWatcher(ZomboidFileSystem.instance.getMessagingDirSub("Trigger_SetAnimState.xml"), (Class<T>)AnimStateTriggerXmlFile.class, (PredicatedFileWatcher.IPredicatedDataPacketFileWatcherCallback<T>)this::onTrigger_setAnimStateToTriggerFile);
    }
    
    private void registerVariableCallbacks() {
        this.setVariable("hitreaction", this::getHitReaction, this::setHitReaction);
        this.setVariable("collidetype", this::getCollideType, this::setCollideType);
        this.setVariable("footInjuryType", this::getFootInjuryType);
        this.setVariable("bumptype", this::getBumpType, this::setBumpType);
        this.setVariable("sitonground", this::isSitOnGround, this::setSitOnGround);
        this.setVariable("canclimbdownrope", this::canClimbDownSheetRopeInCurrentSquare);
        this.setVariable("frombehind", this::isHitFromBehind, this::setHitFromBehind);
        this.setVariable("fallonfront", this::isFallOnFront, this::setFallOnFront);
        this.setVariable("hashitreaction", this::hasHitReaction);
        this.setVariable("intrees", this::isInTreesNoBush);
        this.setVariable("bumped", this::isBumped);
        this.setVariable("BumpDone", false, this::isBumpDone, this::setBumpDone);
        this.setVariable("BumpFall", false, this::isBumpFall, this::setBumpFall);
        this.setVariable("BumpFallType", "", this::getBumpFallType, this::setBumpFallType);
        this.setVariable("BumpStaggered", false, this::isBumpStaggered, this::setBumpStaggered);
        this.setVariable("bonfloor", this::isOnFloor, this::setOnFloor);
        this.setVariable("rangedweaponempty", this::isRangedWeaponEmpty, this::setRangedWeaponEmpty);
        this.setVariable("footInjury", this::hasFootInjury);
        this.setVariable("ChopTreeSpeed", 1.0f, this::getChopTreeSpeed);
        this.setVariable("MoveDelta", 1.0f, this::getMoveDelta, this::setMoveDelta);
        this.setVariable("TurnDelta", 1.0f, this::getTurnDelta, this::setTurnDelta);
        this.setVariable("angle", this::getDirectionAngle, this::setDirectionAngle);
        this.setVariable("animAngle", this::getAnimAngle);
        this.setVariable("twist", this::getTwist);
        this.setVariable("targetTwist", this::getTargetTwist);
        this.setVariable("maxTwist", this.m_maxTwist, this::getMaxTwist, this::setMaxTwist);
        this.setVariable("shoulderTwist", this::getShoulderTwist);
        this.setVariable("excessTwist", this::getExcessTwist);
        this.setVariable("angleStepDelta", this::getAnimAngleStepDelta);
        this.setVariable("angleTwistDelta", this::getAnimAngleTwistDelta);
        this.setVariable("isTurning", false, this::isTurning, this::setTurning);
        this.setVariable("isTurning90", false, this::isTurning90, this::setTurning90);
        this.setVariable("isTurningAround", false, this::isTurningAround, this::setTurningAround);
        this.setVariable("bMoving", false, this::isMoving, this::setMoving);
        this.setVariable("beenMovingFor", this::getBeenMovingFor);
        this.setVariable("previousState", this::getPreviousActionContextStateName);
        this.setVariable("momentumScalar", this::getMomentumScalar, this::setMomentumScalar);
        this.setVariable("hasTimedActions", this::hasTimedActions);
        if (DebugOptions.instance.Character.Debug.RegisterDebugVariables.getValue()) {
            this.registerDebugGameVariables();
        }
        this.setVariable("CriticalHit", this::isCriticalHit, this::setCriticalHit);
        this.setVariable("bKnockedDown", this::isKnockedDown, this::setKnockedDown);
    }
    
    public void updateRecoilVar() {
        this.setVariable("recoilVarY", 0.0f);
        this.setVariable("recoilVarX", 0.0f + this.getPerkLevel(PerkFactory.Perks.Aiming) / 10.0f);
    }
    
    private void registerDebugGameVariables() {
        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.dbgRegisterAnimTrackVariable(i, j);
            }
        }
        this.setVariable("dbg.anm.dx", () -> this.getDeferredMovement(IsoGameCharacter.tempo).x / GameTime.instance.getMultiplier());
        this.setVariable("dbg.anm.dy", () -> this.getDeferredMovement(IsoGameCharacter.tempo).y / GameTime.instance.getMultiplier());
        this.setVariable("dbg.anm.da", () -> this.getDeferredAngleDelta() / GameTime.instance.getMultiplier());
        this.setVariable("dbg.anm.daw", this::getDeferredRotationWeight);
        this.setVariable("dbg.forward", () -> invokedynamic(makeConcatWithConstants:(FF)Ljava/lang/String;, this.getForwardDirection().x, this.getForwardDirection().y));
        this.setVariable("dbg.anm.blend.fbx_x", () -> DebugOptions.instance.Animation.BlendUseFbx.getValue() ? 1.0f : 0.0f);
        this.m_bDebugVariablesRegistered = true;
    }
    
    private void dbgRegisterAnimTrackVariable(final int i, final int j) {
        this.setVariable(String.format("dbg.anm.track%d%d", i, j), () -> this.dbgGetAnimTrackName(i, j));
        this.setVariable(String.format("dbg.anm.t.track%d%d", i, j), () -> this.dbgGetAnimTrackTime(i, j));
        this.setVariable(String.format("dbg.anm.w.track%d%d", i, j), () -> this.dbgGetAnimTrackWeight(i, j));
    }
    
    public float getMomentumScalar() {
        return this.m_momentumScalar;
    }
    
    public void setMomentumScalar(final float momentumScalar) {
        this.m_momentumScalar = momentumScalar;
    }
    
    public Vector2 getDeferredMovement(final Vector2 vector2) {
        if (this.m_animPlayer == null) {
            vector2.set(0.0f, 0.0f);
            return vector2;
        }
        this.m_animPlayer.getDeferredMovement(vector2);
        return vector2;
    }
    
    public float getDeferredAngleDelta() {
        if (this.m_animPlayer == null) {
            return 0.0f;
        }
        return this.m_animPlayer.getDeferredAngleDelta() * 57.295776f;
    }
    
    public float getDeferredRotationWeight() {
        if (this.m_animPlayer == null) {
            return 0.0f;
        }
        return this.m_animPlayer.getDeferredRotationWeight();
    }
    
    public boolean isStrafing() {
        return (this.getPath2() != null && this.pfb2.isStrafing()) || this.isAiming();
    }
    
    public AnimationTrack dbgGetAnimTrack(final int n, final int n2) {
        if (this.m_animPlayer == null) {
            return null;
        }
        final List<AnimationTrack> tracks = this.m_animPlayer.getMultiTrack().getTracks();
        AnimationTrack animationTrack = null;
        int i = 0;
        int n3 = 0;
        while (i < tracks.size()) {
            final AnimationTrack animationTrack2 = tracks.get(i);
            if (animationTrack2.getLayerIdx() == n) {
                if (n3 == n2) {
                    animationTrack = animationTrack2;
                    break;
                }
                ++n3;
            }
            ++i;
        }
        return animationTrack;
    }
    
    public String dbgGetAnimTrackName(final int n, final int n2) {
        final AnimationTrack dbgGetAnimTrack = this.dbgGetAnimTrack(n, n2);
        return (dbgGetAnimTrack != null) ? dbgGetAnimTrack.name : "";
    }
    
    public float dbgGetAnimTrackTime(final int n, final int n2) {
        final AnimationTrack dbgGetAnimTrack = this.dbgGetAnimTrack(n, n2);
        return (dbgGetAnimTrack != null) ? dbgGetAnimTrack.getCurrentTime() : 0.0f;
    }
    
    public float dbgGetAnimTrackWeight(final int n, final int n2) {
        final AnimationTrack dbgGetAnimTrack = this.dbgGetAnimTrack(n, n2);
        return (dbgGetAnimTrack != null) ? dbgGetAnimTrack.BlendDelta : 0.0f;
    }
    
    public float getTwist() {
        if (this.m_animPlayer != null) {
            return 57.295776f * this.m_animPlayer.getTwistAngle();
        }
        return 0.0f;
    }
    
    public float getShoulderTwist() {
        if (this.m_animPlayer != null) {
            return 57.295776f * this.m_animPlayer.getShoulderTwistAngle();
        }
        return 0.0f;
    }
    
    public float getMaxTwist() {
        return this.m_maxTwist;
    }
    
    public void setMaxTwist(final float maxTwist) {
        this.m_maxTwist = maxTwist;
    }
    
    public float getExcessTwist() {
        if (this.m_animPlayer != null) {
            return 57.295776f * this.m_animPlayer.getExcessTwistAngle();
        }
        return 0.0f;
    }
    
    public float getAbsoluteExcessTwist() {
        return Math.abs(this.getExcessTwist());
    }
    
    public float getAnimAngleTwistDelta() {
        return (this.m_animPlayer != null) ? this.m_animPlayer.angleTwistDelta : 0.0f;
    }
    
    public float getAnimAngleStepDelta() {
        return (this.m_animPlayer != null) ? this.m_animPlayer.angleStepDelta : 0.0f;
    }
    
    public float getTargetTwist() {
        return (this.m_animPlayer != null) ? (57.295776f * this.m_animPlayer.getTargetTwistAngle()) : 0.0f;
    }
    
    public boolean isRangedWeaponEmpty() {
        return this.bRangedWeaponEmpty;
    }
    
    public void setRangedWeaponEmpty(final boolean bRangedWeaponEmpty) {
        this.bRangedWeaponEmpty = bRangedWeaponEmpty;
    }
    
    public boolean hasFootInjury() {
        return !StringUtils.isNullOrWhitespace(this.getFootInjuryType());
    }
    
    public boolean isInTrees2(final boolean b) {
        if (this.isCurrentState(BumpedState.instance())) {
            return false;
        }
        final IsoGridSquare currentSquare = this.getCurrentSquare();
        if (currentSquare != null) {
            if (currentSquare.Has(IsoObjectType.tree)) {
                final IsoTree tree = currentSquare.getTree();
                if (tree == null || (b && tree.getSize() > 2) || !b) {
                    return true;
                }
            }
            final String val = currentSquare.getProperties().Val("Movement");
            return "HedgeLow".equalsIgnoreCase(val) || "HedgeHigh".equalsIgnoreCase(val) || (!b && currentSquare.getProperties().Is("Bush"));
        }
        return false;
    }
    
    public boolean isInTreesNoBush() {
        return this.isInTrees2(true);
    }
    
    public boolean isInTrees() {
        return this.isInTrees2(false);
    }
    
    public static HashMap<Integer, SurvivorDesc> getSurvivorMap() {
        return IsoGameCharacter.SurvivorMap;
    }
    
    public static int[] getLevelUpLevels() {
        return IsoGameCharacter.LevelUpLevels;
    }
    
    public static Vector2 getTempo() {
        return IsoGameCharacter.tempo;
    }
    
    public static ColorInfo getInf() {
        return IsoGameCharacter.inf;
    }
    
    public GameCharacterAIBrain getBrain() {
        return this.GameCharacterAIBrain;
    }
    
    public boolean getIsNPC() {
        return this.isNPC;
    }
    
    public void setIsNPC(final boolean isNPC) {
        this.isNPC = isNPC;
    }
    
    public BaseCharacterSoundEmitter getEmitter() {
        return this.emitter;
    }
    
    public void updateEmitter() {
        this.getFMODParameters().update();
        if (!IsoWorld.instance.emitterUpdate && !this.emitter.hasSoundsToStart()) {
            return;
        }
        this.emitter.set(this.x, this.y, this.z);
        this.emitter.tick();
    }
    
    protected void doDeferredMovement() {
        if (GameClient.bClient && this.getHitReactionNetworkAI() != null) {
            if (this.getHitReactionNetworkAI().isStarted()) {
                this.getHitReactionNetworkAI().move();
                return;
            }
            if (this.isDead() && this.getHitReactionNetworkAI().isDoSkipMovement()) {
                return;
            }
        }
        if (this.getAnimationPlayer() == null) {
            return;
        }
        if (this.getPath2() != null && !this.isCurrentState(ClimbOverFenceState.instance()) && !this.isCurrentState(ClimbThroughWindowState.instance())) {
            if (this.isCurrentState(WalkTowardState.instance())) {
                DebugLog.General.warn((Object)"WalkTowardState but path2 != null");
                this.setPath2(null);
            }
            return;
        }
        if (GameClient.bClient) {
            if (this instanceof IsoZombie && ((IsoZombie)this).isRemoteZombie()) {
                if (this.getCurrentState() != ClimbOverFenceState.instance() && this.getCurrentState() != ClimbThroughWindowState.instance() && this.getCurrentState() != ClimbOverWallState.instance() && this.getCurrentState() != StaggerBackState.instance() && this.getCurrentState() != ZombieHitReactionState.instance() && this.getCurrentState() != ZombieFallDownState.instance() && this.getCurrentState() != ZombieFallingState.instance() && this.getCurrentState() != ZombieOnGroundState.instance() && this.getCurrentState() != AttackNetworkState.instance()) {
                    return;
                }
            }
            else if (this instanceof IsoPlayer && !((IsoPlayer)this).isLocalPlayer() && !this.isCurrentState(CollideWithWallState.instance()) && !this.isCurrentState(PlayerGetUpState.instance()) && !this.isCurrentState(BumpedState.instance())) {
                return;
            }
        }
        final Vector2 tempo = IsoGameCharacter.tempo;
        this.getDeferredMovement(tempo);
        if (GameClient.bClient && this instanceof IsoZombie && this.isCurrentState(StaggerBackState.instance())) {
            final float length = tempo.getLength();
            tempo.set(this.getHitDir());
            tempo.setLength(length);
        }
        this.MoveUnmodded(tempo);
    }
    
    @Override
    public ActionContext getActionContext() {
        return null;
    }
    
    public String getPreviousActionContextStateName() {
        final ActionContext actionContext = this.getActionContext();
        return (actionContext == null) ? "" : actionContext.getPreviousStateName();
    }
    
    public String getCurrentActionContextStateName() {
        final ActionContext actionContext = this.getActionContext();
        return (actionContext == null) ? "" : actionContext.getCurrentStateName();
    }
    
    public boolean hasAnimationPlayer() {
        return this.m_animPlayer != null;
    }
    
    @Override
    public AnimationPlayer getAnimationPlayer() {
        final Model bodyModel = ModelManager.instance.getBodyModel(this);
        int n = 0;
        if (this.m_animPlayer != null && this.m_animPlayer.getModel() != bodyModel) {
            n = ((this.m_animPlayer.getMultiTrack().getTrackCount() > 0) ? 1 : 0);
            this.m_animPlayer = Pool.tryRelease(this.m_animPlayer);
        }
        if (this.m_animPlayer == null) {
            this.onAnimPlayerCreated(this.m_animPlayer = AnimationPlayer.alloc(bodyModel));
            if (n != 0) {
                this.getAdvancedAnimator().OnAnimDataChanged(false);
            }
        }
        return this.m_animPlayer;
    }
    
    public void releaseAnimationPlayer() {
        this.m_animPlayer = Pool.tryRelease(this.m_animPlayer);
    }
    
    protected void onAnimPlayerCreated(final AnimationPlayer animationPlayer) {
        animationPlayer.setRecorder(this.m_animationRecorder);
        animationPlayer.setTwistBones("Bip01_Pelvis", "Bip01_Spine", "Bip01_Spine1", "Bip01_Neck", "Bip01_Head");
        animationPlayer.setCounterRotationBone("Bip01");
    }
    
    protected void updateAnimationRecorderState() {
        if (this.m_animPlayer == null) {
            return;
        }
        if (IsoWorld.isAnimRecorderDiscardTriggered()) {
            this.m_animPlayer.discardRecording();
        }
        final boolean recording = IsoWorld.isAnimRecorderActive() && !this.isSceneCulled();
        if (recording) {
            this.getAnimationPlayerRecorder().logCharacterPos();
        }
        this.m_animPlayer.setRecording(recording);
    }
    
    @Override
    public AdvancedAnimator getAdvancedAnimator() {
        return this.advancedAnimator;
    }
    
    @Override
    public ModelInstance getModelInstance() {
        if (this.legsSprite == null) {
            return null;
        }
        if (this.legsSprite.modelSlot == null) {
            return null;
        }
        return this.legsSprite.modelSlot.model;
    }
    
    public String getCurrentStateName() {
        return (this.stateMachine.getCurrent() == null) ? null : this.stateMachine.getCurrent().getName();
    }
    
    public String getPreviousStateName() {
        return (this.stateMachine.getPrevious() == null) ? null : this.stateMachine.getPrevious().getName();
    }
    
    public String getAnimationDebug() {
        if (this.advancedAnimator != null) {
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.instancename, this.advancedAnimator.GetDebug());
        }
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.instancename);
    }
    
    @Override
    public String getTalkerType() {
        return this.chatElement.getTalkerType();
    }
    
    public boolean isAnimForecasted() {
        return System.currentTimeMillis() < this.isAnimForecasted;
    }
    
    public void setAnimForecasted(final int n) {
        this.isAnimForecasted = System.currentTimeMillis() + n;
    }
    
    public void resetModel() {
        ModelManager.instance.Reset(this);
    }
    
    public void resetModelNextFrame() {
        ModelManager.instance.ResetNextFrame(this);
    }
    
    protected void onTrigger_setClothingToXmlTriggerFile(final TriggerXmlFile triggerXmlFile) {
        OutfitManager.Reload();
        if (!StringUtils.isNullOrWhitespace(triggerXmlFile.outfitName)) {
            final String outfitName = triggerXmlFile.outfitName;
            DebugLog.Clothing.debugln(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, outfitName));
            Outfit outfit;
            if (triggerXmlFile.isMale) {
                outfit = OutfitManager.instance.FindMaleOutfit(outfitName);
            }
            else {
                outfit = OutfitManager.instance.FindFemaleOutfit(outfitName);
            }
            if (outfit == null) {
                DebugLog.Clothing.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, outfitName));
                return;
            }
            if (this.bFemale == triggerXmlFile.isMale && this instanceof IHumanVisual) {
                ((IHumanVisual)this).getHumanVisual().clear();
            }
            this.bFemale = !triggerXmlFile.isMale;
            if (this.descriptor != null) {
                this.descriptor.setFemale(this.bFemale);
            }
            this.dressInNamedOutfit(outfit.m_Name);
            this.advancedAnimator.OnAnimDataChanged(false);
            if (this instanceof IsoPlayer) {
                LuaEventManager.triggerEvent("OnClothingUpdated", this);
            }
        }
        else if (!StringUtils.isNullOrWhitespace(triggerXmlFile.clothingItemGUID)) {
            this.dressInClothingItem(triggerXmlFile.clothingItemGUID);
            if (this instanceof IsoPlayer) {
                LuaEventManager.triggerEvent("OnClothingUpdated", this);
            }
        }
        ModelManager.instance.Reset(this);
    }
    
    protected void onTrigger_setAnimStateToTriggerFile(final AnimStateTriggerXmlFile animStateTriggerXmlFile) {
        if (!StringUtils.equalsIgnoreCase(this.GetAnimSetName(), animStateTriggerXmlFile.animSet)) {
            this.setVariable("dbgForceAnim", false);
            this.restoreAnimatorStateToActionContext();
            return;
        }
        DebugOptions.instance.Animation.AnimLayer.AllowAnimNodeOverride.setValue(animStateTriggerXmlFile.forceAnim);
        if (this.advancedAnimator.containsState(animStateTriggerXmlFile.stateName)) {
            this.setVariable("dbgForceAnim", animStateTriggerXmlFile.forceAnim);
            this.setVariable("dbgForceAnimStateName", animStateTriggerXmlFile.stateName);
            this.setVariable("dbgForceAnimNodeName", animStateTriggerXmlFile.nodeName);
            this.setVariable("dbgForceAnimScalars", animStateTriggerXmlFile.setScalarValues);
            this.setVariable("dbgForceScalar", animStateTriggerXmlFile.scalarValue);
            this.setVariable("dbgForceScalar2", animStateTriggerXmlFile.scalarValue2);
            this.advancedAnimator.SetState(animStateTriggerXmlFile.stateName);
        }
        else {
            DebugLog.Animation.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, animStateTriggerXmlFile.stateName));
            this.restoreAnimatorStateToActionContext();
        }
    }
    
    private void restoreAnimatorStateToActionContext() {
        if (this.actionContext.getCurrentState() != null) {
            this.advancedAnimator.SetState(this.actionContext.getCurrentStateName(), PZArrayUtil.listConvert(this.actionContext.getChildStates(), actionState -> actionState.name));
        }
    }
    
    @Override
    public void clothingItemChanged(final String s) {
        if (this.wornItems != null) {
            for (int i = 0; i < this.wornItems.size(); ++i) {
                final InventoryItem itemByIndex = this.wornItems.getItemByIndex(i);
                final ClothingItem clothingItem = itemByIndex.getClothingItem();
                if (clothingItem != null) {
                    if (clothingItem.isReady()) {
                        if (clothingItem.m_GUID.equals(s)) {
                            final ClothingItemReference clothingItemReference = new ClothingItemReference();
                            clothingItemReference.itemGUID = s;
                            clothingItemReference.randomize();
                            itemByIndex.getVisual().synchWithOutfit(clothingItemReference);
                            itemByIndex.synchWithVisual();
                            this.resetModelNextFrame();
                        }
                    }
                }
            }
        }
    }
    
    public void reloadOutfit() {
        ModelManager.instance.Reset(this);
    }
    
    public boolean isSceneCulled() {
        return this.m_isCulled;
    }
    
    public void setSceneCulled(final boolean b) {
        if (this.isSceneCulled() == b) {
            return;
        }
        try {
            if (b) {
                ModelManager.instance.Remove(this);
            }
            else {
                ModelManager.instance.Add(this);
            }
        }
        catch (Exception ex) {
            System.err.println(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, b));
            ExceptionLogger.logException(ex);
            ModelManager.instance.Remove(this);
            this.legsSprite.modelSlot = null;
        }
    }
    
    public void onCullStateChanged(final ModelManager modelManager, final boolean isCulled) {
        if (!(this.m_isCulled = isCulled)) {
            this.restoreAnimatorStateToActionContext();
            DebugFileWatcher.instance.add(this.m_animStateTriggerWatcher);
            OutfitManager.instance.addClothingItemListener(this);
        }
        else {
            DebugFileWatcher.instance.remove(this.m_animStateTriggerWatcher);
            OutfitManager.instance.removeClothingItemListener(this);
        }
    }
    
    public void dressInRandomOutfit() {
        if (DebugLog.isEnabled(DebugType.Clothing)) {
            DebugLog.Clothing.println("IsoGameCharacter.dressInRandomOutfit>");
        }
        final Outfit getRandomOutfit = OutfitManager.instance.GetRandomOutfit(this.isFemale());
        if (getRandomOutfit != null) {
            this.dressInNamedOutfit(getRandomOutfit.m_Name);
        }
    }
    
    public void dressInNamedOutfit(final String s) {
    }
    
    public void dressInPersistentOutfit(final String s) {
        this.dressInPersistentOutfitID(PersistentOutfits.instance.pickOutfit(s, this.isFemale()));
    }
    
    public void dressInPersistentOutfitID(final int n) {
    }
    
    public String getOutfitName() {
        if (this instanceof IHumanVisual) {
            final Outfit outfit = ((IHumanVisual)this).getHumanVisual().getOutfit();
            return (outfit == null) ? null : outfit.m_Name;
        }
        return null;
    }
    
    public void dressInClothingItem(final String s) {
    }
    
    public Outfit getRandomDefaultOutfit() {
        final IsoGridSquare currentSquare = this.getCurrentSquare();
        final IsoRoom isoRoom = (currentSquare == null) ? null : currentSquare.getRoom();
        return ZombiesZoneDefinition.getRandomDefaultOutfit(this.isFemale(), (isoRoom == null) ? null : isoRoom.getName());
    }
    
    public ModelInstance getModel() {
        if (this.legsSprite != null && this.legsSprite.modelSlot != null) {
            return this.legsSprite.modelSlot.model;
        }
        return null;
    }
    
    public boolean hasActiveModel() {
        return this.legsSprite != null && this.legsSprite.hasActiveModel();
    }
    
    public boolean hasItems(final String s, final int n) {
        return n <= this.inventory.getItemCount(s);
    }
    
    public int getLevelUpLevels(final int n) {
        if (IsoGameCharacter.LevelUpLevels.length <= n) {
            return IsoGameCharacter.LevelUpLevels[IsoGameCharacter.LevelUpLevels.length - 1];
        }
        return IsoGameCharacter.LevelUpLevels[n];
    }
    
    public int getLevelMaxForXp() {
        return IsoGameCharacter.LevelUpLevels.length;
    }
    
    public int getXpForLevel(final int n) {
        if (n < IsoGameCharacter.LevelUpLevels.length) {
            return (int)(IsoGameCharacter.LevelUpLevels[n] * this.LevelUpMultiplier);
        }
        return (int)((IsoGameCharacter.LevelUpLevels[IsoGameCharacter.LevelUpLevels.length - 1] + (n - IsoGameCharacter.LevelUpLevels.length + 1) * 400) * this.LevelUpMultiplier);
    }
    
    public void DoDeath(final HandWeapon handWeapon, final IsoGameCharacter isoGameCharacter) {
        this.DoDeath(handWeapon, isoGameCharacter, true);
    }
    
    public void DoDeath(final HandWeapon handWeapon, final IsoGameCharacter isoGameCharacter, final boolean b) {
        this.OnDeath();
        if (this.getAttackedBy() instanceof IsoPlayer && GameServer.bServer && this instanceof IsoPlayer) {
            String s = "";
            String s2 = "";
            if (SteamUtils.isSteamModeEnabled()) {
                s = invokedynamic(makeConcatWithConstants:(J)Ljava/lang/String;, ((IsoPlayer)this.getAttackedBy()).getSteamID());
                s2 = invokedynamic(makeConcatWithConstants:(J)Ljava/lang/String;, ((IsoPlayer)this).getSteamID());
            }
            LoggerManager.getLogger("pvp").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ((IsoPlayer)this.getAttackedBy()).username, s, ((IsoPlayer)this).username, s2, LoggerManager.getPlayerCoords((IsoPlayer)this)), "IMPORTANT");
            if (ServerOptions.instance.AnnounceDeath.getValue()) {
                ChatServer.getInstance().sendMessageToServerChat(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ((IsoPlayer)this.getAttackedBy()).username, ((IsoPlayer)this).username));
            }
            ChatServer.getInstance().sendMessageToAdminChat(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ((IsoPlayer)this.getAttackedBy()).username, ((IsoPlayer)this).username));
        }
        else {
            if (GameServer.bServer && this instanceof IsoPlayer) {
                LoggerManager.getLogger("user").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ((IsoPlayer)this).username, LoggerManager.getPlayerCoords((IsoPlayer)this)));
            }
            if (ServerOptions.instance.AnnounceDeath.getValue() && this instanceof IsoPlayer && GameServer.bServer) {
                ChatServer.getInstance().sendMessageToServerChat(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, ((IsoPlayer)this).username));
            }
        }
        if (this.isDead()) {
            float n = 0.5f;
            if (this.isZombie() && (((IsoZombie)this).bCrawling || this.getCurrentState() == ZombieOnGroundState.instance())) {
                n = 0.2f;
            }
            if (GameServer.bServer && b) {
                GameServer.sendBloodSplatter(handWeapon, this.getX(), this.getY(), this.getZ() + n, this.getHitDir(), this.isCloseKilled(), this.isOnFloor() && isoGameCharacter instanceof IsoPlayer && handWeapon != null && "BareHands".equals(handWeapon.getType()));
            }
            if (handWeapon != null && SandboxOptions.instance.BloodLevel.getValue() > 1 && b) {
                int splatNumber = handWeapon.getSplatNumber();
                if (splatNumber < 1) {
                    splatNumber = 1;
                }
                if (Core.bLastStand) {
                    splatNumber *= 3;
                }
                switch (SandboxOptions.instance.BloodLevel.getValue()) {
                    case 2: {
                        splatNumber /= 2;
                        break;
                    }
                    case 4: {
                        splatNumber *= 2;
                        break;
                    }
                    case 5: {
                        splatNumber *= 5;
                        break;
                    }
                }
                for (int i = 0; i < splatNumber; ++i) {
                    this.splatBlood(3, 0.3f);
                }
            }
            if (handWeapon != null && SandboxOptions.instance.BloodLevel.getValue() > 1 && b) {
                this.splatBloodFloorBig();
            }
            if (isoGameCharacter != null && isoGameCharacter.xp != null) {
                isoGameCharacter.xp.AddXP(handWeapon, 3);
            }
            if (SandboxOptions.instance.BloodLevel.getValue() > 1 && this.isOnFloor() && isoGameCharacter instanceof IsoPlayer && handWeapon == ((IsoPlayer)isoGameCharacter).bareHands && b) {
                this.playBloodSplatterSound();
                for (int j = -1; j <= 1; ++j) {
                    for (int k = -1; k <= 1; ++k) {
                        if (j != 0 || k != 0) {
                            new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + n, j * Rand.Next(0.25f, 0.5f), k * Rand.Next(0.25f, 0.5f));
                        }
                    }
                }
                new IsoZombieGiblets(IsoZombieGiblets.GibletType.Eye, this.getCell(), this.getX(), this.getY(), this.getZ() + n, this.getHitDir().x * 0.8f, this.getHitDir().y * 0.8f);
            }
            else if (SandboxOptions.instance.BloodLevel.getValue() > 1 && b) {
                this.playBloodSplatterSound();
                new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + n, this.getHitDir().x * 1.5f, this.getHitDir().y * 1.5f);
                IsoGameCharacter.tempo.x = this.getHitDir().x;
                IsoGameCharacter.tempo.y = this.getHitDir().y;
                int n2 = 3;
                int n3 = 0;
                int n4 = 1;
                switch (SandboxOptions.instance.BloodLevel.getValue()) {
                    case 1: {
                        n4 = 0;
                        break;
                    }
                    case 2: {
                        n4 = 1;
                        n2 = 5;
                        n3 = 2;
                        break;
                    }
                    case 4: {
                        n4 = 3;
                        n2 = 2;
                        break;
                    }
                    case 5: {
                        n4 = 10;
                        n2 = 0;
                        break;
                    }
                }
                for (int l = 0; l < n4; ++l) {
                    if (Rand.Next(this.isCloseKilled() ? 8 : n2) == 0) {
                        new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + n, this.getHitDir().x * 1.5f, this.getHitDir().y * 1.5f);
                    }
                    if (Rand.Next(this.isCloseKilled() ? 8 : n2) == 0) {
                        new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + n, this.getHitDir().x * 1.5f, this.getHitDir().y * 1.5f);
                    }
                    if (Rand.Next(this.isCloseKilled() ? 8 : n2) == 0) {
                        new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + n, this.getHitDir().x * 1.8f, this.getHitDir().y * 1.8f);
                    }
                    if (Rand.Next(this.isCloseKilled() ? 8 : n2) == 0) {
                        new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + n, this.getHitDir().x * 1.9f, this.getHitDir().y * 1.9f);
                    }
                    if (Rand.Next(this.isCloseKilled() ? 4 : n3) == 0) {
                        new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + n, this.getHitDir().x * 3.5f, this.getHitDir().y * 3.5f);
                    }
                    if (Rand.Next(this.isCloseKilled() ? 4 : n3) == 0) {
                        new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + n, this.getHitDir().x * 3.8f, this.getHitDir().y * 3.8f);
                    }
                    if (Rand.Next(this.isCloseKilled() ? 4 : n3) == 0) {
                        new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + n, this.getHitDir().x * 3.9f, this.getHitDir().y * 3.9f);
                    }
                    if (Rand.Next(this.isCloseKilled() ? 4 : n3) == 0) {
                        new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + n, this.getHitDir().x * 1.5f, this.getHitDir().y * 1.5f);
                    }
                    if (Rand.Next(this.isCloseKilled() ? 4 : n3) == 0) {
                        new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + n, this.getHitDir().x * 3.8f, this.getHitDir().y * 3.8f);
                    }
                    if (Rand.Next(this.isCloseKilled() ? 4 : n3) == 0) {
                        new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + n, this.getHitDir().x * 3.9f, this.getHitDir().y * 3.9f);
                    }
                    if (Rand.Next(this.isCloseKilled() ? 9 : 6) == 0) {
                        new IsoZombieGiblets(IsoZombieGiblets.GibletType.Eye, this.getCell(), this.getX(), this.getY(), this.getZ() + n, this.getHitDir().x * 0.8f, this.getHitDir().y * 0.8f);
                    }
                }
            }
        }
        if (this.isDoDeathSound()) {
            this.playDeadSound();
        }
        this.setDoDeathSound(false);
    }
    
    private boolean TestIfSeen(final int n) {
        final IsoPlayer isoPlayer = IsoPlayer.players[n];
        if (isoPlayer == null || this == isoPlayer || GameServer.bServer) {
            return false;
        }
        final float distToProper = this.DistToProper(isoPlayer);
        if (distToProper > GameTime.getInstance().getViewDist()) {
            return false;
        }
        boolean canSee = this.current.isCanSee(n);
        if (!canSee && this.current.isCouldSee(n)) {
            canSee = (distToProper < isoPlayer.getSeeNearbyCharacterDistance());
        }
        if (!canSee) {
            return false;
        }
        final ColorInfo lightInfo = this.getCurrentSquare().lighting[n].lightInfo();
        float n2 = (lightInfo.r + lightInfo.g + lightInfo.b) / 3.0f;
        if (n2 > 0.6f) {
            n2 = 1.0f;
        }
        float n3 = 1.0f - distToProper / GameTime.getInstance().getViewDist();
        if (n2 == 1.0f && n3 > 0.3f) {
            n3 = 1.0f;
        }
        float dotWithForwardDirection = isoPlayer.getDotWithForwardDirection(this.getX(), this.getY());
        if (dotWithForwardDirection < 0.5f) {
            dotWithForwardDirection = 0.5f;
        }
        float n4 = n2 * dotWithForwardDirection;
        if (n4 < 0.0f) {
            n4 = 0.0f;
        }
        if (distToProper <= 1.0f) {
            n3 = 1.0f;
            n4 *= 2.0f;
        }
        return n4 * n3 * 100.0f > 0.025f;
    }
    
    private void DoLand() {
        if (this.fallTime < 20.0f || this.isClimbing()) {
            return;
        }
        if (this instanceof IsoPlayer) {
            if (GameServer.bServer) {
                return;
            }
            if (GameClient.bClient && ((IsoPlayer)this).bRemote) {
                return;
            }
            if (((IsoPlayer)this).isGhostMode()) {
                return;
            }
        }
        if (this.isZombie()) {
            if (this.fallTime > 50.0f) {
                final Vector2 hitDir = this.hitDir;
                final Vector2 hitDir2 = this.hitDir;
                final float n = 0.0f;
                hitDir2.y = n;
                hitDir.x = n;
                if (!((IsoZombie)this).bCrawling && (Rand.Next(100) < 80 || this.fallTime > 80.0f)) {
                    this.setVariable("bHardFall", true);
                }
                this.playHurtSound();
                this.Health -= 0.075f * this.fallTime / 50.0f;
                this.setAttackedBy(null);
            }
            return;
        }
        boolean b = Rand.Next(80) == 0;
        float n2 = this.fallTime * Math.min(1.8f, this.getInventory().getCapacityWeight() / this.getInventory().getMaxWeight());
        if (this.getCurrentSquare().getFloor() != null && this.getCurrentSquare().getFloor().getSprite().getName() != null && this.getCurrentSquare().getFloor().getSprite().getName().startsWith("blends_natural")) {
            n2 *= 0.8f;
            if (!b) {
                b = (Rand.Next(65) == 0);
            }
        }
        if (b) {
            return;
        }
        if (this.Traits.Obese.isSet() || this.Traits.Emaciated.isSet()) {
            n2 *= 1.4f;
        }
        if (this.Traits.Overweight.isSet() || this.Traits.VeryUnderweight.isSet()) {
            n2 *= 1.2f;
        }
        float n3 = n2 * Math.max(0.1f, 1.0f - this.getPerkLevel(PerkFactory.Perks.Fitness) * 0.1f);
        if (this.fallTime > 135.0f) {
            n3 = 1000.0f;
        }
        this.BodyDamage.ReduceGeneralHealth(n3);
        if (this.fallTime > 70.0f) {
            int n4 = 100 - (int)(this.fallTime * 0.6);
            if (this.getInventory().getMaxWeight() - this.getInventory().getCapacityWeight() < 2.0f) {
                n4 -= (int)(this.getInventory().getCapacityWeight() / this.getInventory().getMaxWeight() * 100.0f / 5.0f);
            }
            if (this.Traits.Obese.isSet() || this.Traits.Emaciated.isSet()) {
                n4 -= 20;
            }
            if (this.Traits.Overweight.isSet() || this.Traits.VeryUnderweight.isSet()) {
                n4 -= 10;
            }
            if (this.getPerkLevel(PerkFactory.Perks.Fitness) > 4) {
                n4 += (this.getPerkLevel(PerkFactory.Perks.Fitness) - 4) * 3;
            }
            if (Rand.Next(100) >= n4) {
                if (!SandboxOptions.instance.BoneFracture.getValue()) {
                    return;
                }
                float fractureTime = (float)Rand.Next(50, 80);
                if (this.Traits.FastHealer.isSet()) {
                    fractureTime = (float)Rand.Next(30, 50);
                }
                else if (this.Traits.SlowHealer.isSet()) {
                    fractureTime = (float)Rand.Next(80, 150);
                }
                switch (SandboxOptions.instance.InjurySeverity.getValue()) {
                    case 1: {
                        fractureTime *= 0.5f;
                        break;
                    }
                    case 3: {
                        fractureTime *= 1.5f;
                        break;
                    }
                }
                this.getBodyDamage().getBodyPart(BodyPartType.FromIndex(Rand.Next(BodyPartType.ToIndex(BodyPartType.UpperLeg_L), BodyPartType.ToIndex(BodyPartType.Foot_R) + 1))).setFractureTime(fractureTime);
            }
            else if (Rand.Next(100) >= n4 - 10) {
                this.getBodyDamage().getBodyPart(BodyPartType.FromIndex(Rand.Next(BodyPartType.ToIndex(BodyPartType.UpperLeg_L), BodyPartType.ToIndex(BodyPartType.Foot_R) + 1))).generateDeepWound();
            }
        }
    }
    
    public IsoGameCharacter getFollowingTarget() {
        return this.FollowingTarget;
    }
    
    public void setFollowingTarget(final IsoGameCharacter followingTarget) {
        this.FollowingTarget = followingTarget;
    }
    
    public ArrayList<IsoMovingObject> getLocalList() {
        return this.LocalList;
    }
    
    public ArrayList<IsoMovingObject> getLocalNeutralList() {
        return this.LocalNeutralList;
    }
    
    public ArrayList<IsoMovingObject> getLocalGroupList() {
        return this.LocalGroupList;
    }
    
    public ArrayList<IsoMovingObject> getLocalRelevantEnemyList() {
        return this.LocalRelevantEnemyList;
    }
    
    public float getDangerLevels() {
        return this.dangerLevels;
    }
    
    public void setDangerLevels(final float dangerLevels) {
        this.dangerLevels = dangerLevels;
    }
    
    public ArrayList<PerkInfo> getPerkList() {
        return this.PerkList;
    }
    
    public float getLeaveBodyTimedown() {
        return this.leaveBodyTimedown;
    }
    
    public void setLeaveBodyTimedown(final float leaveBodyTimedown) {
        this.leaveBodyTimedown = leaveBodyTimedown;
    }
    
    public boolean isAllowConversation() {
        return this.AllowConversation;
    }
    
    public void setAllowConversation(final boolean allowConversation) {
        this.AllowConversation = allowConversation;
    }
    
    public float getReanimateTimer() {
        return this.ReanimateTimer;
    }
    
    public void setReanimateTimer(final float reanimateTimer) {
        this.ReanimateTimer = reanimateTimer;
    }
    
    public int getReanimAnimFrame() {
        return this.ReanimAnimFrame;
    }
    
    public void setReanimAnimFrame(final int reanimAnimFrame) {
        this.ReanimAnimFrame = reanimAnimFrame;
    }
    
    public int getReanimAnimDelay() {
        return this.ReanimAnimDelay;
    }
    
    public void setReanimAnimDelay(final int reanimAnimDelay) {
        this.ReanimAnimDelay = reanimAnimDelay;
    }
    
    public boolean isReanim() {
        return this.Reanim;
    }
    
    public void setReanim(final boolean reanim) {
        this.Reanim = reanim;
    }
    
    public boolean isVisibleToNPCs() {
        return this.VisibleToNPCs;
    }
    
    public void setVisibleToNPCs(final boolean visibleToNPCs) {
        this.VisibleToNPCs = visibleToNPCs;
    }
    
    public int getDieCount() {
        return this.DieCount;
    }
    
    public void setDieCount(final int dieCount) {
        this.DieCount = dieCount;
    }
    
    public float getLlx() {
        return this.llx;
    }
    
    public void setLlx(final float llx) {
        this.llx = llx;
    }
    
    public float getLly() {
        return this.lly;
    }
    
    public void setLly(final float lly) {
        this.lly = lly;
    }
    
    public float getLlz() {
        return this.llz;
    }
    
    public void setLlz(final float llz) {
        this.llz = llz;
    }
    
    public int getRemoteID() {
        return this.RemoteID;
    }
    
    public void setRemoteID(final int remoteID) {
        this.RemoteID = remoteID;
    }
    
    public int getNumSurvivorsInVicinity() {
        return this.NumSurvivorsInVicinity;
    }
    
    public void setNumSurvivorsInVicinity(final int numSurvivorsInVicinity) {
        this.NumSurvivorsInVicinity = numSurvivorsInVicinity;
    }
    
    public float getLevelUpMultiplier() {
        return this.LevelUpMultiplier;
    }
    
    public void setLevelUpMultiplier(final float levelUpMultiplier) {
        this.LevelUpMultiplier = levelUpMultiplier;
    }
    
    public XP getXp() {
        return this.xp;
    }
    
    public void setXp(final XP xp) {
        this.xp = xp;
    }
    
    public int getLastLocalEnemies() {
        return this.LastLocalEnemies;
    }
    
    public void setLastLocalEnemies(final int lastLocalEnemies) {
        this.LastLocalEnemies = lastLocalEnemies;
    }
    
    public ArrayList<IsoMovingObject> getVeryCloseEnemyList() {
        return this.VeryCloseEnemyList;
    }
    
    public HashMap<String, Location> getLastKnownLocation() {
        return this.LastKnownLocation;
    }
    
    public IsoGameCharacter getAttackedBy() {
        return this.AttackedBy;
    }
    
    public void setAttackedBy(final IsoGameCharacter attackedBy) {
        this.AttackedBy = attackedBy;
    }
    
    public boolean isIgnoreStaggerBack() {
        return this.IgnoreStaggerBack;
    }
    
    public void setIgnoreStaggerBack(final boolean ignoreStaggerBack) {
        this.IgnoreStaggerBack = ignoreStaggerBack;
    }
    
    public boolean isAttackWasSuperAttack() {
        return this.AttackWasSuperAttack;
    }
    
    public void setAttackWasSuperAttack(final boolean attackWasSuperAttack) {
        this.AttackWasSuperAttack = attackWasSuperAttack;
    }
    
    public int getTimeThumping() {
        return this.TimeThumping;
    }
    
    public void setTimeThumping(final int timeThumping) {
        this.TimeThumping = timeThumping;
    }
    
    public int getPatienceMax() {
        return this.PatienceMax;
    }
    
    public void setPatienceMax(final int patienceMax) {
        this.PatienceMax = patienceMax;
    }
    
    public int getPatienceMin() {
        return this.PatienceMin;
    }
    
    public void setPatienceMin(final int patienceMin) {
        this.PatienceMin = patienceMin;
    }
    
    public int getPatience() {
        return this.Patience;
    }
    
    public void setPatience(final int patience) {
        this.Patience = patience;
    }
    
    public Stack<BaseAction> getCharacterActions() {
        return this.CharacterActions;
    }
    
    public boolean hasTimedActions() {
        return !this.CharacterActions.isEmpty() || this.getVariableBoolean("IsPerformingAnAction");
    }
    
    public Vector2 getForwardDirection() {
        return this.m_forwardDirection;
    }
    
    public void setForwardDirection(final Vector2 vector2) {
        if (vector2 == null) {
            return;
        }
        this.setForwardDirection(vector2.x, vector2.y);
    }
    
    public void setForwardDirection(final float x, final float y) {
        this.m_forwardDirection.x = x;
        this.m_forwardDirection.y = y;
    }
    
    public void zeroForwardDirectionX() {
        this.setForwardDirection(0.0f, 1.0f);
    }
    
    public void zeroForwardDirectionY() {
        this.setForwardDirection(1.0f, 0.0f);
    }
    
    public float getDirectionAngle() {
        return 57.295776f * this.getForwardDirection().getDirection();
    }
    
    public void setDirectionAngle(final float n) {
        this.getForwardDirection().setDirection(0.017453292f * n);
    }
    
    public float getAnimAngle() {
        if (this.m_animPlayer == null || !this.m_animPlayer.isReady() || this.m_animPlayer.isBoneTransformsNeedFirstFrame()) {
            return this.getDirectionAngle();
        }
        return 57.295776f * this.m_animPlayer.getAngle();
    }
    
    public float getAnimAngleRadians() {
        if (this.m_animPlayer == null || !this.m_animPlayer.isReady() || this.m_animPlayer.isBoneTransformsNeedFirstFrame()) {
            return this.m_forwardDirection.getDirection();
        }
        return this.m_animPlayer.getAngle();
    }
    
    public Vector2 getAnimVector(final Vector2 vector2) {
        return vector2.setLengthAndDirection(this.getAnimAngleRadians(), 1.0f);
    }
    
    public float getLookAngleRadians() {
        if (this.m_animPlayer == null || !this.m_animPlayer.isReady()) {
            return this.getForwardDirection().getDirection();
        }
        return this.m_animPlayer.getAngle() + this.m_animPlayer.getTwistAngle();
    }
    
    public Vector2 getLookVector(final Vector2 vector2) {
        return vector2.setLengthAndDirection(this.getLookAngleRadians(), 1.0f);
    }
    
    public float getDotWithForwardDirection(final Vector3 vector3) {
        return this.getDotWithForwardDirection(vector3.x, vector3.y);
    }
    
    public float getDotWithForwardDirection(final float n, final float n2) {
        final Vector2 set = L_getDotWithForwardDirection.v1.set(n - this.getX(), n2 - this.getY());
        set.normalize();
        final Vector2 lookVector = this.getLookVector(L_getDotWithForwardDirection.v2);
        lookVector.normalize();
        return set.dot(lookVector);
    }
    
    public boolean isAsleep() {
        return this.Asleep;
    }
    
    public void setAsleep(final boolean asleep) {
        this.Asleep = asleep;
    }
    
    public int getZombieKills() {
        return this.ZombieKills;
    }
    
    public void setZombieKills(final int zombieKills) {
        this.ZombieKills = zombieKills;
    }
    
    public int getLastZombieKills() {
        return this.LastZombieKills;
    }
    
    public void setLastZombieKills(final int lastZombieKills) {
        this.LastZombieKills = lastZombieKills;
    }
    
    public boolean isSuperAttack() {
        return this.superAttack;
    }
    
    public void setSuperAttack(final boolean superAttack) {
        this.superAttack = superAttack;
    }
    
    public float getForceWakeUpTime() {
        return this.ForceWakeUpTime;
    }
    
    public void setForceWakeUpTime(final float forceWakeUpTime) {
        this.ForceWakeUpTime = forceWakeUpTime;
    }
    
    public void forceAwake() {
        if (this.isAsleep()) {
            this.ForceWakeUp = true;
        }
    }
    
    public BodyDamage getBodyDamage() {
        return this.BodyDamage;
    }
    
    public BodyDamage getBodyDamageRemote() {
        if (this.BodyDamageRemote == null) {
            this.BodyDamageRemote = new BodyDamage(null);
        }
        return this.BodyDamageRemote;
    }
    
    public void resetBodyDamageRemote() {
        this.BodyDamageRemote = null;
    }
    
    public State getDefaultState() {
        return this.defaultState;
    }
    
    public void setDefaultState(final State defaultState) {
        this.defaultState = defaultState;
    }
    
    public SurvivorDesc getDescriptor() {
        return this.descriptor;
    }
    
    public void setDescriptor(final SurvivorDesc descriptor) {
        this.descriptor = descriptor;
    }
    
    public String getFullName() {
        if (this.descriptor != null) {
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.descriptor.forename, this.descriptor.surname);
        }
        return "Bob Smith";
    }
    
    public BaseVisual getVisual() {
        throw new RuntimeException("subclasses must implement this");
    }
    
    public ItemVisuals getItemVisuals() {
        throw new RuntimeException("subclasses must implement this");
    }
    
    public void getItemVisuals(final ItemVisuals itemVisuals) {
        this.getWornItems().getItemVisuals(itemVisuals);
    }
    
    public boolean isUsingWornItems() {
        return this.wornItems != null;
    }
    
    public Stack<IsoBuilding> getFamiliarBuildings() {
        return this.FamiliarBuildings;
    }
    
    public AStarPathFinderResult getFinder() {
        return this.finder;
    }
    
    public float getFireKillRate() {
        return this.FireKillRate;
    }
    
    public void setFireKillRate(final float fireKillRate) {
        this.FireKillRate = fireKillRate;
    }
    
    public int getFireSpreadProbability() {
        return this.FireSpreadProbability;
    }
    
    public void setFireSpreadProbability(final int fireSpreadProbability) {
        this.FireSpreadProbability = fireSpreadProbability;
    }
    
    public float getHealth() {
        return this.Health;
    }
    
    public void setHealth(final float health) {
        this.Health = health;
    }
    
    public boolean isOnDeathDone() {
        return this.bDead;
    }
    
    public void setOnDeathDone(final boolean bDead) {
        this.bDead = bDead;
    }
    
    public boolean isOnKillDone() {
        return this.bKill;
    }
    
    public void setOnKillDone(final boolean bKill) {
        this.bKill = bKill;
    }
    
    public boolean isDeathDragDown() {
        return this.bDeathDragDown;
    }
    
    public void setDeathDragDown(final boolean bDeathDragDown) {
        this.bDeathDragDown = bDeathDragDown;
    }
    
    public boolean isPlayingDeathSound() {
        return this.bPlayingDeathSound;
    }
    
    public void setPlayingDeathSound(final boolean bPlayingDeathSound) {
        this.bPlayingDeathSound = bPlayingDeathSound;
    }
    
    public String getHurtSound() {
        return this.hurtSound;
    }
    
    public void setHurtSound(final String hurtSound) {
        this.hurtSound = hurtSound;
    }
    
    @Deprecated
    public boolean isIgnoreMovementForDirection() {
        return false;
    }
    
    public ItemContainer getInventory() {
        return this.inventory;
    }
    
    public void setInventory(final ItemContainer inventory) {
        inventory.parent = this;
        (this.inventory = inventory).setExplored(true);
    }
    
    public boolean isPrimaryEquipped(final String s) {
        return this.leftHandItem != null && (this.leftHandItem.getFullType().equals(s) || this.leftHandItem.getType().equals(s));
    }
    
    public InventoryItem getPrimaryHandItem() {
        return this.leftHandItem;
    }
    
    public void setPrimaryHandItem(final InventoryItem leftHandItem) {
        this.setEquipParent(this.leftHandItem, leftHandItem);
        this.leftHandItem = leftHandItem;
        if (GameClient.bClient && this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
            GameClient.instance.equip((IsoPlayer)this, 0);
        }
        LuaEventManager.triggerEvent("OnEquipPrimary", this, leftHandItem);
        this.resetEquippedHandsModels();
        this.setVariable("Weapon", WeaponType.getWeaponType(this).type);
        if (leftHandItem != null && leftHandItem instanceof HandWeapon && !StringUtils.isNullOrEmpty(((HandWeapon)leftHandItem).getFireMode())) {
            this.setVariable("FireMode", ((HandWeapon)leftHandItem).getFireMode());
        }
        else {
            this.clearVariable("FireMode");
        }
    }
    
    protected void setEquipParent(final InventoryItem inventoryItem, final InventoryItem inventoryItem2) {
        if (inventoryItem != null) {
            inventoryItem.setEquipParent(null);
        }
        if (inventoryItem2 != null) {
            inventoryItem2.setEquipParent(this);
        }
    }
    
    public void initWornItems(final String s) {
        this.wornItems = new WornItems(BodyLocations.getGroup(s));
    }
    
    public WornItems getWornItems() {
        return this.wornItems;
    }
    
    public void setWornItems(final WornItems wornItems) {
        this.wornItems = new WornItems(wornItems);
    }
    
    public InventoryItem getWornItem(final String s) {
        return this.wornItems.getItem(s);
    }
    
    public void setWornItem(final String s, final InventoryItem inventoryItem) {
        this.setWornItem(s, inventoryItem, true);
    }
    
    public void setWornItem(final String s, final InventoryItem inventoryItem, final boolean b) {
        final InventoryItem item = this.wornItems.getItem(s);
        final IsoCell currentCell = IsoWorld.instance.CurrentCell;
        if (item != null && currentCell != null) {
            currentCell.addToProcessItemsRemove(item);
        }
        this.wornItems.setItem(s, inventoryItem);
        if (inventoryItem != null && currentCell != null) {
            if (inventoryItem.getContainer() != null) {
                inventoryItem.getContainer().parent = this;
            }
            currentCell.addToProcessItems(inventoryItem);
        }
        if (b && item != null && this instanceof IsoPlayer && !this.getInventory().hasRoomFor(this, item)) {
            final IsoGridSquare currentSquare = this.getCurrentSquare();
            final IsoGridSquare solidFloor = this.getSolidFloorAt(currentSquare.x, currentSquare.y, currentSquare.z);
            if (solidFloor != null) {
                final float next = Rand.Next(0.1f, 0.9f);
                final float next2 = Rand.Next(0.1f, 0.9f);
                solidFloor.AddWorldInventoryItem(item, next, next2, solidFloor.getApparentZ(next, next2) - solidFloor.getZ());
                this.getInventory().Remove(item);
            }
        }
        this.resetModelNextFrame();
        if (this.clothingWetness != null) {
            this.clothingWetness.changed = true;
        }
        if (GameClient.bClient && this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
            GameClient.instance.sendClothing((IsoPlayer)this, s, inventoryItem);
        }
        this.onWornItemsChanged();
    }
    
    public void removeWornItem(final InventoryItem inventoryItem) {
        this.removeWornItem(inventoryItem, true);
    }
    
    public void removeWornItem(final InventoryItem inventoryItem, final boolean b) {
        final String location = this.wornItems.getLocation(inventoryItem);
        if (location == null) {
            return;
        }
        this.setWornItem(location, null, b);
    }
    
    public void clearWornItems() {
        if (this.wornItems == null) {
            return;
        }
        this.wornItems.clear();
        if (this.clothingWetness != null) {
            this.clothingWetness.changed = true;
        }
        this.onWornItemsChanged();
    }
    
    public BodyLocationGroup getBodyLocationGroup() {
        if (this.wornItems == null) {
            return null;
        }
        return this.wornItems.getBodyLocationGroup();
    }
    
    public void onWornItemsChanged() {
    }
    
    public void initAttachedItems(final String s) {
        this.attachedItems = new AttachedItems(AttachedLocations.getGroup(s));
    }
    
    public AttachedItems getAttachedItems() {
        return this.attachedItems;
    }
    
    public void setAttachedItems(final AttachedItems attachedItems) {
        this.attachedItems = new AttachedItems(attachedItems);
    }
    
    public InventoryItem getAttachedItem(final String s) {
        return this.attachedItems.getItem(s);
    }
    
    public void setAttachedItem(final String s, final InventoryItem inventoryItem) {
        final InventoryItem item = this.attachedItems.getItem(s);
        final IsoCell currentCell = IsoWorld.instance.CurrentCell;
        if (item != null && currentCell != null) {
            currentCell.addToProcessItemsRemove(item);
        }
        this.attachedItems.setItem(s, inventoryItem);
        if (inventoryItem != null && currentCell != null) {
            final InventoryContainer inventoryContainer = Type.tryCastTo(inventoryItem, InventoryContainer.class);
            if (inventoryContainer != null && inventoryContainer.getInventory() != null) {
                inventoryContainer.getInventory().parent = this;
            }
            currentCell.addToProcessItems(inventoryItem);
        }
        this.resetEquippedHandsModels();
        final IsoPlayer isoPlayer = Type.tryCastTo(this, IsoPlayer.class);
        if (GameClient.bClient && isoPlayer != null && isoPlayer.isLocalPlayer()) {
            GameClient.instance.sendAttachedItem(isoPlayer, s, inventoryItem);
        }
        if (!GameServer.bServer && isoPlayer != null && isoPlayer.isLocalPlayer()) {
            LuaEventManager.triggerEvent("OnClothingUpdated", this);
        }
    }
    
    public void removeAttachedItem(final InventoryItem inventoryItem) {
        final String location = this.attachedItems.getLocation(inventoryItem);
        if (location == null) {
            return;
        }
        this.setAttachedItem(location, null);
    }
    
    public void clearAttachedItems() {
        if (this.attachedItems == null) {
            return;
        }
        this.attachedItems.clear();
    }
    
    public AttachedLocationGroup getAttachedLocationGroup() {
        if (this.attachedItems == null) {
            return null;
        }
        return this.attachedItems.getGroup();
    }
    
    public ClothingWetness getClothingWetness() {
        return this.clothingWetness;
    }
    
    public InventoryItem getClothingItem_Head() {
        return this.getWornItem("Hat");
    }
    
    public void setClothingItem_Head(final InventoryItem inventoryItem) {
        this.setWornItem("Hat", inventoryItem);
    }
    
    public InventoryItem getClothingItem_Torso() {
        return this.getWornItem("Tshirt");
    }
    
    public void setClothingItem_Torso(final InventoryItem inventoryItem) {
        this.setWornItem("Tshirt", inventoryItem);
    }
    
    public InventoryItem getClothingItem_Back() {
        return this.getWornItem("Back");
    }
    
    public void setClothingItem_Back(final InventoryItem inventoryItem) {
        this.setWornItem("Back", inventoryItem);
    }
    
    public InventoryItem getClothingItem_Hands() {
        return this.getWornItem("Hands");
    }
    
    public void setClothingItem_Hands(final InventoryItem inventoryItem) {
        this.setWornItem("Hands", inventoryItem);
    }
    
    public InventoryItem getClothingItem_Legs() {
        return this.getWornItem("Pants");
    }
    
    public void setClothingItem_Legs(final InventoryItem inventoryItem) {
        this.setWornItem("Pants", inventoryItem);
    }
    
    public InventoryItem getClothingItem_Feet() {
        return this.getWornItem("Shoes");
    }
    
    public void setClothingItem_Feet(final InventoryItem inventoryItem) {
        this.setWornItem("Shoes", inventoryItem);
    }
    
    public int getNextWander() {
        return this.NextWander;
    }
    
    public void setNextWander(final int nextWander) {
        this.NextWander = nextWander;
    }
    
    public boolean isOnFire() {
        return this.OnFire;
    }
    
    public void setOnFire(final boolean onFire) {
        this.OnFire = onFire;
    }
    
    public int getPathIndex() {
        return this.pathIndex;
    }
    
    public void setPathIndex(final int pathIndex) {
        this.pathIndex = pathIndex;
    }
    
    public int getPathTargetX() {
        return (int)this.getPathFindBehavior2().getTargetX();
    }
    
    public int getPathTargetY() {
        return (int)this.getPathFindBehavior2().getTargetY();
    }
    
    public int getPathTargetZ() {
        return (int)this.getPathFindBehavior2().getTargetZ();
    }
    
    public InventoryItem getSecondaryHandItem() {
        return this.rightHandItem;
    }
    
    public void setSecondaryHandItem(final InventoryItem rightHandItem) {
        this.setEquipParent(this.rightHandItem, rightHandItem);
        this.rightHandItem = rightHandItem;
        if (GameClient.bClient && this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
            GameClient.instance.equip((IsoPlayer)this, 1);
        }
        LuaEventManager.triggerEvent("OnEquipSecondary", this, rightHandItem);
        this.resetEquippedHandsModels();
        this.setVariable("Weapon", WeaponType.getWeaponType(this).type);
    }
    
    public boolean isHandItem(final InventoryItem inventoryItem) {
        return this.isPrimaryHandItem(inventoryItem) || this.isSecondaryHandItem(inventoryItem);
    }
    
    public boolean isPrimaryHandItem(final InventoryItem inventoryItem) {
        return inventoryItem != null && this.getPrimaryHandItem() == inventoryItem;
    }
    
    public boolean isSecondaryHandItem(final InventoryItem inventoryItem) {
        return inventoryItem != null && this.getSecondaryHandItem() == inventoryItem;
    }
    
    public boolean isItemInBothHands(final InventoryItem inventoryItem) {
        return this.isPrimaryHandItem(inventoryItem) && this.isSecondaryHandItem(inventoryItem);
    }
    
    public boolean removeFromHands(final InventoryItem inventoryItem) {
        final boolean b = true;
        if (this.isPrimaryHandItem(inventoryItem)) {
            this.setPrimaryHandItem(null);
        }
        if (this.isSecondaryHandItem(inventoryItem)) {
            this.setSecondaryHandItem(null);
        }
        return b;
    }
    
    public Color getSpeakColour() {
        return this.SpeakColour;
    }
    
    public void setSpeakColour(final Color speakColour) {
        this.SpeakColour = speakColour;
    }
    
    public void setSpeakColourInfo(final ColorInfo colorInfo) {
        this.SpeakColour = new Color(colorInfo.r, colorInfo.g, colorInfo.b, 1.0f);
    }
    
    public float getSlowFactor() {
        return this.slowFactor;
    }
    
    public void setSlowFactor(final float slowFactor) {
        this.slowFactor = slowFactor;
    }
    
    public float getSlowTimer() {
        return this.slowTimer;
    }
    
    public void setSlowTimer(final float slowTimer) {
        this.slowTimer = slowTimer;
    }
    
    public boolean isbUseParts() {
        return this.bUseParts;
    }
    
    public void setbUseParts(final boolean bUseParts) {
        this.bUseParts = bUseParts;
    }
    
    public boolean isSpeaking() {
        return this.IsSpeaking();
    }
    
    public void setSpeaking(final boolean speaking) {
        this.Speaking = speaking;
    }
    
    public float getSpeakTime() {
        return this.SpeakTime;
    }
    
    public void setSpeakTime(final int n) {
        this.SpeakTime = (float)n;
    }
    
    public float getSpeedMod() {
        return this.speedMod;
    }
    
    public void setSpeedMod(final float speedMod) {
        this.speedMod = speedMod;
    }
    
    public float getStaggerTimeMod() {
        return this.staggerTimeMod;
    }
    
    public void setStaggerTimeMod(final float staggerTimeMod) {
        this.staggerTimeMod = staggerTimeMod;
    }
    
    public StateMachine getStateMachine() {
        return this.stateMachine;
    }
    
    public Moodles getMoodles() {
        return this.Moodles;
    }
    
    public Stats getStats() {
        return this.stats;
    }
    
    public Stack<String> getUsedItemsOn() {
        return this.UsedItemsOn;
    }
    
    public HandWeapon getUseHandWeapon() {
        return this.useHandWeapon;
    }
    
    public void setUseHandWeapon(final HandWeapon useHandWeapon) {
        this.useHandWeapon = useHandWeapon;
    }
    
    public IsoSprite getLegsSprite() {
        return this.legsSprite;
    }
    
    public void setLegsSprite(final IsoSprite legsSprite) {
        this.legsSprite = legsSprite;
    }
    
    public IsoGridSquare getAttackTargetSquare() {
        return this.attackTargetSquare;
    }
    
    public void setAttackTargetSquare(final IsoGridSquare attackTargetSquare) {
        this.attackTargetSquare = attackTargetSquare;
    }
    
    public float getBloodImpactX() {
        return this.BloodImpactX;
    }
    
    public void setBloodImpactX(final float bloodImpactX) {
        this.BloodImpactX = bloodImpactX;
    }
    
    public float getBloodImpactY() {
        return this.BloodImpactY;
    }
    
    public void setBloodImpactY(final float bloodImpactY) {
        this.BloodImpactY = bloodImpactY;
    }
    
    public float getBloodImpactZ() {
        return this.BloodImpactZ;
    }
    
    public void setBloodImpactZ(final float bloodImpactZ) {
        this.BloodImpactZ = bloodImpactZ;
    }
    
    public IsoSprite getBloodSplat() {
        return this.bloodSplat;
    }
    
    public void setBloodSplat(final IsoSprite bloodSplat) {
        this.bloodSplat = bloodSplat;
    }
    
    public boolean isbOnBed() {
        return this.bOnBed;
    }
    
    public void setbOnBed(final boolean bOnBed) {
        this.bOnBed = bOnBed;
    }
    
    public Vector2 getMoveForwardVec() {
        return this.moveForwardVec;
    }
    
    public void setMoveForwardVec(final Vector2 vector2) {
        this.moveForwardVec.set(vector2);
    }
    
    public boolean isPathing() {
        return this.pathing;
    }
    
    public void setPathing(final boolean pathing) {
        this.pathing = pathing;
    }
    
    public Stack<IsoGameCharacter> getLocalEnemyList() {
        return this.LocalEnemyList;
    }
    
    public Stack<IsoGameCharacter> getEnemyList() {
        return this.EnemyList;
    }
    
    public TraitCollection getTraits() {
        return this.getCharacterTraits();
    }
    
    public CharacterTraits getCharacterTraits() {
        return this.Traits;
    }
    
    public int getMaxWeight() {
        return this.maxWeight;
    }
    
    public void setMaxWeight(final int maxWeight) {
        this.maxWeight = maxWeight;
    }
    
    public int getMaxWeightBase() {
        return this.maxWeightBase;
    }
    
    public void setMaxWeightBase(final int maxWeightBase) {
        this.maxWeightBase = maxWeightBase;
    }
    
    public float getSleepingTabletDelta() {
        return this.SleepingTabletDelta;
    }
    
    public void setSleepingTabletDelta(final float sleepingTabletDelta) {
        this.SleepingTabletDelta = sleepingTabletDelta;
    }
    
    public float getBetaEffect() {
        return this.BetaEffect;
    }
    
    public void setBetaEffect(final float betaEffect) {
        this.BetaEffect = betaEffect;
    }
    
    public float getDepressEffect() {
        return this.DepressEffect;
    }
    
    public void setDepressEffect(final float depressEffect) {
        this.DepressEffect = depressEffect;
    }
    
    public float getSleepingTabletEffect() {
        return this.SleepingTabletEffect;
    }
    
    public void setSleepingTabletEffect(final float sleepingTabletEffect) {
        this.SleepingTabletEffect = sleepingTabletEffect;
    }
    
    public float getBetaDelta() {
        return this.BetaDelta;
    }
    
    public void setBetaDelta(final float betaDelta) {
        this.BetaDelta = betaDelta;
    }
    
    public float getDepressDelta() {
        return this.DepressDelta;
    }
    
    public void setDepressDelta(final float depressDelta) {
        this.DepressDelta = depressDelta;
    }
    
    public float getPainEffect() {
        return this.PainEffect;
    }
    
    public void setPainEffect(final float painEffect) {
        this.PainEffect = painEffect;
    }
    
    public float getPainDelta() {
        return this.PainDelta;
    }
    
    public void setPainDelta(final float painDelta) {
        this.PainDelta = painDelta;
    }
    
    public boolean isbDoDefer() {
        return this.bDoDefer;
    }
    
    public void setbDoDefer(final boolean bDoDefer) {
        this.bDoDefer = bDoDefer;
    }
    
    public Location getLastHeardSound() {
        return this.LastHeardSound;
    }
    
    public void setLastHeardSound(final int x, final int y, final int z) {
        this.LastHeardSound.x = x;
        this.LastHeardSound.y = y;
        this.LastHeardSound.z = z;
    }
    
    public float getLrx() {
        return this.lrx;
    }
    
    public void setLrx(final float lrx) {
        this.lrx = lrx;
    }
    
    public float getLry() {
        return this.lry;
    }
    
    public void setLry(final float lry) {
        this.lry = lry;
    }
    
    public boolean isClimbing() {
        return this.bClimbing;
    }
    
    public void setbClimbing(final boolean bClimbing) {
        this.bClimbing = bClimbing;
    }
    
    public boolean isLastCollidedW() {
        return this.lastCollidedW;
    }
    
    public void setLastCollidedW(final boolean lastCollidedW) {
        this.lastCollidedW = lastCollidedW;
    }
    
    public boolean isLastCollidedN() {
        return this.lastCollidedN;
    }
    
    public void setLastCollidedN(final boolean lastCollidedN) {
        this.lastCollidedN = lastCollidedN;
    }
    
    public float getFallTime() {
        return this.fallTime;
    }
    
    public void setFallTime(final float fallTime) {
        this.fallTime = fallTime;
    }
    
    public float getLastFallSpeed() {
        return this.lastFallSpeed;
    }
    
    public void setLastFallSpeed(final float lastFallSpeed) {
        this.lastFallSpeed = lastFallSpeed;
    }
    
    public boolean isbFalling() {
        return this.bFalling;
    }
    
    public void setbFalling(final boolean bFalling) {
        this.bFalling = bFalling;
    }
    
    @Override
    public IsoBuilding getCurrentBuilding() {
        if (this.current == null) {
            return null;
        }
        if (this.current.getRoom() == null) {
            return null;
        }
        return this.current.getRoom().building;
    }
    
    public BuildingDef getCurrentBuildingDef() {
        if (this.current == null) {
            return null;
        }
        if (this.current.getRoom() == null) {
            return null;
        }
        if (this.current.getRoom().building != null) {
            return this.current.getRoom().building.def;
        }
        return null;
    }
    
    public RoomDef getCurrentRoomDef() {
        if (this.current == null) {
            return null;
        }
        if (this.current.getRoom() != null) {
            return this.current.getRoom().def;
        }
        return null;
    }
    
    public float getTorchStrength() {
        return 0.0f;
    }
    
    @Override
    public void OnAnimEvent(final AnimLayer animLayer, final AnimEvent animEvent) {
        if (animEvent.m_EventName == null) {
            return;
        }
        if (animEvent.m_EventName.equalsIgnoreCase("SetVariable") && animEvent.m_SetVariable1 != null) {
            this.setVariable(animEvent.m_SetVariable1, animEvent.m_SetVariable2);
        }
        if (animEvent.m_EventName.equalsIgnoreCase("ClearVariable")) {
            this.clearVariable(animEvent.m_ParameterValue);
        }
        if (animEvent.m_EventName.equalsIgnoreCase("PlaySound")) {
            this.getEmitter().playSoundImpl(animEvent.m_ParameterValue, this);
        }
        if (animEvent.m_EventName.equalsIgnoreCase("Footstep")) {
            this.DoFootstepSound(animEvent.m_ParameterValue);
        }
        if (animEvent.m_EventName.equalsIgnoreCase("DamageWhileInTrees")) {
            this.damageWhileInTrees();
        }
        final int depth = animLayer.getDepth();
        this.actionContext.reportEvent(depth, animEvent.m_EventName);
        this.stateMachine.stateAnimEvent(depth, animEvent);
    }
    
    private void damageWhileInTrees() {
        if (this.isZombie() || "Tutorial".equals(Core.GameMode)) {
            return;
        }
        int n = 50;
        final int next = Rand.Next(0, BodyPartType.ToIndex(BodyPartType.MAX));
        if (this.isRunning()) {
            n = 30;
        }
        if (this.Traits.Outdoorsman.isSet()) {
            n += 50;
        }
        if (Rand.NextBool(n + (int)this.getBodyPartClothingDefense(next, false, false))) {
            this.addHole(BloodBodyPartType.FromIndex(next));
            int n2 = 6;
            if (this.Traits.ThickSkinned.isSet()) {
                n2 += 7;
            }
            if (this.Traits.ThinSkinned.isSet()) {
                n2 -= 3;
            }
            if (Rand.NextBool(n2) && (int)this.getBodyPartClothingDefense(next, false, false) < 100) {
                final BodyPart bodyPart = this.getBodyDamage().getBodyParts().get(next);
                if (Rand.NextBool(n2 + 10)) {
                    bodyPart.setCut(true, true);
                }
                else {
                    bodyPart.setScratched(true, true);
                }
            }
        }
    }
    
    public float getHammerSoundMod() {
        final int perkLevel = this.getPerkLevel(PerkFactory.Perks.Woodwork);
        if (perkLevel == 2) {
            return 0.8f;
        }
        if (perkLevel == 3) {
            return 0.6f;
        }
        if (perkLevel == 4) {
            return 0.4f;
        }
        if (perkLevel >= 5) {
            return 0.4f;
        }
        return 1.0f;
    }
    
    public float getWeldingSoundMod() {
        final int perkLevel = this.getPerkLevel(PerkFactory.Perks.MetalWelding);
        if (perkLevel == 2) {
            return 0.8f;
        }
        if (perkLevel == 3) {
            return 0.6f;
        }
        if (perkLevel == 4) {
            return 0.4f;
        }
        if (perkLevel >= 5) {
            return 0.4f;
        }
        return 1.0f;
    }
    
    public float getBarricadeTimeMod() {
        final int perkLevel = this.getPerkLevel(PerkFactory.Perks.Woodwork);
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
            return 0.5f;
        }
        if (perkLevel == 6) {
            return 0.42f;
        }
        if (perkLevel == 7) {
            return 0.36f;
        }
        if (perkLevel == 8) {
            return 0.3f;
        }
        if (perkLevel == 9) {
            return 0.26f;
        }
        if (perkLevel == 10) {
            return 0.2f;
        }
        return 0.7f;
    }
    
    public float getMetalBarricadeStrengthMod() {
        switch (this.getPerkLevel(PerkFactory.Perks.MetalWelding)) {
            case 2: {
                return 1.1f;
            }
            case 3: {
                return 1.14f;
            }
            case 4: {
                return 1.18f;
            }
            case 5: {
                return 1.22f;
            }
            case 6: {
                return 1.16f;
            }
            case 7: {
                return 1.3f;
            }
            case 8: {
                return 1.34f;
            }
            case 9: {
                return 1.4f;
            }
            case 10: {
                return 1.5f;
            }
            default: {
                final int perkLevel = this.getPerkLevel(PerkFactory.Perks.Woodwork);
                if (perkLevel == 2) {
                    return 1.1f;
                }
                if (perkLevel == 3) {
                    return 1.14f;
                }
                if (perkLevel == 4) {
                    return 1.18f;
                }
                if (perkLevel == 5) {
                    return 1.22f;
                }
                if (perkLevel == 6) {
                    return 1.26f;
                }
                if (perkLevel == 7) {
                    return 1.3f;
                }
                if (perkLevel == 8) {
                    return 1.34f;
                }
                if (perkLevel == 9) {
                    return 1.4f;
                }
                if (perkLevel == 10) {
                    return 1.5f;
                }
                return 1.0f;
            }
        }
    }
    
    public float getBarricadeStrengthMod() {
        final int perkLevel = this.getPerkLevel(PerkFactory.Perks.Woodwork);
        if (perkLevel == 2) {
            return 1.1f;
        }
        if (perkLevel == 3) {
            return 1.14f;
        }
        if (perkLevel == 4) {
            return 1.18f;
        }
        if (perkLevel == 5) {
            return 1.22f;
        }
        if (perkLevel == 6) {
            return 1.26f;
        }
        if (perkLevel == 7) {
            return 1.3f;
        }
        if (perkLevel == 8) {
            return 1.34f;
        }
        if (perkLevel == 9) {
            return 1.4f;
        }
        if (perkLevel == 10) {
            return 1.5f;
        }
        return 1.0f;
    }
    
    public float getSneakSpotMod() {
        final int perkLevel = this.getPerkLevel(PerkFactory.Perks.Sneak);
        float n = 0.95f;
        if (perkLevel == 1) {
            n = 0.9f;
        }
        if (perkLevel == 2) {
            n = 0.8f;
        }
        if (perkLevel == 3) {
            n = 0.75f;
        }
        if (perkLevel == 4) {
            n = 0.7f;
        }
        if (perkLevel == 5) {
            n = 0.65f;
        }
        if (perkLevel == 6) {
            n = 0.6f;
        }
        if (perkLevel == 7) {
            n = 0.55f;
        }
        if (perkLevel == 8) {
            n = 0.5f;
        }
        if (perkLevel == 9) {
            n = 0.45f;
        }
        if (perkLevel == 10) {
            n = 0.4f;
        }
        return n * 1.2f;
    }
    
    public float getNimbleMod() {
        final int perkLevel = this.getPerkLevel(PerkFactory.Perks.Nimble);
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
            return 1.38f;
        }
        if (perkLevel == 9) {
            return 1.42f;
        }
        if (perkLevel == 10) {
            return 1.5f;
        }
        return 1.0f;
    }
    
    public float getFatigueMod() {
        final int perkLevel = this.getPerkLevel(PerkFactory.Perks.Fitness);
        if (perkLevel == 1) {
            return 0.95f;
        }
        if (perkLevel == 2) {
            return 0.92f;
        }
        if (perkLevel == 3) {
            return 0.89f;
        }
        if (perkLevel == 4) {
            return 0.87f;
        }
        if (perkLevel == 5) {
            return 0.85f;
        }
        if (perkLevel == 6) {
            return 0.83f;
        }
        if (perkLevel == 7) {
            return 0.81f;
        }
        if (perkLevel == 8) {
            return 0.79f;
        }
        if (perkLevel == 9) {
            return 0.77f;
        }
        if (perkLevel == 10) {
            return 0.75f;
        }
        return 1.0f;
    }
    
    public float getLightfootMod() {
        final int perkLevel = this.getPerkLevel(PerkFactory.Perks.Lightfoot);
        if (perkLevel == 1) {
            return 0.9f;
        }
        if (perkLevel == 2) {
            return 0.79f;
        }
        if (perkLevel == 3) {
            return 0.71f;
        }
        if (perkLevel == 4) {
            return 0.65f;
        }
        if (perkLevel == 5) {
            return 0.59f;
        }
        if (perkLevel == 6) {
            return 0.52f;
        }
        if (perkLevel == 7) {
            return 0.45f;
        }
        if (perkLevel == 8) {
            return 0.37f;
        }
        if (perkLevel == 9) {
            return 0.3f;
        }
        if (perkLevel == 10) {
            return 0.2f;
        }
        return 0.99f;
    }
    
    public float getPacingMod() {
        final int perkLevel = this.getPerkLevel(PerkFactory.Perks.Fitness);
        if (perkLevel == 1) {
            return 0.8f;
        }
        if (perkLevel == 2) {
            return 0.75f;
        }
        if (perkLevel == 3) {
            return 0.7f;
        }
        if (perkLevel == 4) {
            return 0.65f;
        }
        if (perkLevel == 5) {
            return 0.6f;
        }
        if (perkLevel == 6) {
            return 0.57f;
        }
        if (perkLevel == 7) {
            return 0.53f;
        }
        if (perkLevel == 8) {
            return 0.49f;
        }
        if (perkLevel == 9) {
            return 0.46f;
        }
        if (perkLevel == 10) {
            return 0.43f;
        }
        return 0.9f;
    }
    
    public float getHyperthermiaMod() {
        float n = 1.0f;
        if (this.getMoodles().getMoodleLevel(MoodleType.Hyperthermia) > 1) {
            n = 1.0f;
            if (this.getMoodles().getMoodleLevel(MoodleType.Hyperthermia) == 4) {
                n = 2.0f;
            }
        }
        return n;
    }
    
    public float getHittingMod() {
        final int perkLevel = this.getPerkLevel(PerkFactory.Perks.Strength);
        if (perkLevel == 1) {
            return 0.8f;
        }
        if (perkLevel == 2) {
            return 0.85f;
        }
        if (perkLevel == 3) {
            return 0.9f;
        }
        if (perkLevel == 4) {
            return 0.95f;
        }
        if (perkLevel == 5) {
            return 1.0f;
        }
        if (perkLevel == 6) {
            return 1.05f;
        }
        if (perkLevel == 7) {
            return 1.1f;
        }
        if (perkLevel == 8) {
            return 1.15f;
        }
        if (perkLevel == 9) {
            return 1.2f;
        }
        if (perkLevel == 10) {
            return 1.25f;
        }
        return 0.75f;
    }
    
    public float getShovingMod() {
        final int perkLevel = this.getPerkLevel(PerkFactory.Perks.Strength);
        if (perkLevel == 1) {
            return 0.8f;
        }
        if (perkLevel == 2) {
            return 0.85f;
        }
        if (perkLevel == 3) {
            return 0.9f;
        }
        if (perkLevel == 4) {
            return 0.95f;
        }
        if (perkLevel == 5) {
            return 1.0f;
        }
        if (perkLevel == 6) {
            return 1.05f;
        }
        if (perkLevel == 7) {
            return 1.1f;
        }
        if (perkLevel == 8) {
            return 1.15f;
        }
        if (perkLevel == 9) {
            return 1.2f;
        }
        if (perkLevel == 10) {
            return 1.25f;
        }
        return 0.75f;
    }
    
    public float getRecoveryMod() {
        final int perkLevel = this.getPerkLevel(PerkFactory.Perks.Fitness);
        float n = 0.0f;
        if (perkLevel == 0) {
            n = 0.7f;
        }
        if (perkLevel == 1) {
            n = 0.8f;
        }
        if (perkLevel == 2) {
            n = 0.9f;
        }
        if (perkLevel == 3) {
            n = 1.0f;
        }
        if (perkLevel == 4) {
            n = 1.1f;
        }
        if (perkLevel == 5) {
            n = 1.2f;
        }
        if (perkLevel == 6) {
            n = 1.3f;
        }
        if (perkLevel == 7) {
            n = 1.4f;
        }
        if (perkLevel == 8) {
            n = 1.5f;
        }
        if (perkLevel == 9) {
            n = 1.55f;
        }
        if (perkLevel == 10) {
            n = 1.6f;
        }
        if (this.Traits.Obese.isSet()) {
            n *= (float)0.4;
        }
        if (this.Traits.Overweight.isSet()) {
            n *= (float)0.7;
        }
        if (this.Traits.VeryUnderweight.isSet()) {
            n *= (float)0.7;
        }
        if (this.Traits.Emaciated.isSet()) {
            n *= (float)0.3;
        }
        if (this instanceof IsoPlayer) {
            if (((IsoPlayer)this).getNutrition().getLipids() < -1500.0f) {
                n *= (float)0.2;
            }
            else if (((IsoPlayer)this).getNutrition().getLipids() < -1000.0f) {
                n *= 0.5;
            }
            if (((IsoPlayer)this).getNutrition().getProteins() < -1500.0f) {
                n *= (float)0.2;
            }
            else if (((IsoPlayer)this).getNutrition().getProteins() < -1000.0f) {
                n *= 0.5;
            }
        }
        return n;
    }
    
    public float getWeightMod() {
        final int perkLevel = this.getPerkLevel(PerkFactory.Perks.Strength);
        if (perkLevel == 1) {
            return 0.9f;
        }
        if (perkLevel == 2) {
            return 1.07f;
        }
        if (perkLevel == 3) {
            return 1.24f;
        }
        if (perkLevel == 4) {
            return 1.41f;
        }
        if (perkLevel == 5) {
            return 1.58f;
        }
        if (perkLevel == 6) {
            return 1.75f;
        }
        if (perkLevel == 7) {
            return 1.92f;
        }
        if (perkLevel == 8) {
            return 2.09f;
        }
        if (perkLevel == 9) {
            return 2.26f;
        }
        if (perkLevel == 10) {
            return 2.5f;
        }
        return 0.8f;
    }
    
    public int getHitChancesMod() {
        final int perkLevel = this.getPerkLevel(PerkFactory.Perks.Aiming);
        if (perkLevel == 1) {
            return 1;
        }
        if (perkLevel == 2) {
            return 1;
        }
        if (perkLevel == 3) {
            return 2;
        }
        if (perkLevel == 4) {
            return 2;
        }
        if (perkLevel == 5) {
            return 3;
        }
        if (perkLevel == 6) {
            return 3;
        }
        if (perkLevel == 7) {
            return 4;
        }
        if (perkLevel == 8) {
            return 4;
        }
        if (perkLevel == 9) {
            return 5;
        }
        if (perkLevel == 10) {
            return 5;
        }
        return 1;
    }
    
    public float getSprintMod() {
        final int perkLevel = this.getPerkLevel(PerkFactory.Perks.Sprinting);
        if (perkLevel == 1) {
            return 1.1f;
        }
        if (perkLevel == 2) {
            return 1.15f;
        }
        if (perkLevel == 3) {
            return 1.2f;
        }
        if (perkLevel == 4) {
            return 1.25f;
        }
        if (perkLevel == 5) {
            return 1.3f;
        }
        if (perkLevel == 6) {
            return 1.35f;
        }
        if (perkLevel == 7) {
            return 1.4f;
        }
        if (perkLevel == 8) {
            return 1.45f;
        }
        if (perkLevel == 9) {
            return 1.5f;
        }
        if (perkLevel == 10) {
            return 1.6f;
        }
        return 0.9f;
    }
    
    public int getPerkLevel(final PerkFactory.Perk perk) {
        final PerkInfo perkInfo = this.getPerkInfo(perk);
        if (perkInfo != null) {
            return perkInfo.level;
        }
        return 0;
    }
    
    public void setPerkLevelDebug(final PerkFactory.Perk perk, final int level) {
        final PerkInfo perkInfo = this.getPerkInfo(perk);
        if (perkInfo != null) {
            perkInfo.level = level;
        }
        if (GameClient.bClient && this instanceof IsoPlayer) {
            GameClient.sendPerks((IsoPlayer)this);
        }
    }
    
    public void LoseLevel(final PerkFactory.Perk perk) {
        final PerkInfo perkInfo = this.getPerkInfo(perk);
        if (perkInfo != null) {
            final PerkInfo perkInfo2 = perkInfo;
            --perkInfo2.level;
            if (perkInfo.level < 0) {
                perkInfo.level = 0;
            }
            LuaEventManager.triggerEvent("LevelPerk", this, perk, perkInfo.level, false);
            if (perk == PerkFactory.Perks.Sneak && GameClient.bClient && this instanceof IsoPlayer) {
                GameClient.sendPerks((IsoPlayer)this);
            }
            return;
        }
        LuaEventManager.triggerEvent("LevelPerk", this, perk, 0, false);
    }
    
    public void LevelPerk(final PerkFactory.Perk perk, final boolean b) {
        Objects.requireNonNull(perk, "perk is null");
        if (perk == PerkFactory.Perks.MAX) {
            throw new IllegalArgumentException("perk == Perks.MAX");
        }
        final PerkInfo perkInfo = this.getPerkInfo(perk);
        if (perkInfo != null) {
            final PerkInfo perkInfo2 = perkInfo;
            ++perkInfo2.level;
            if (this instanceof IsoPlayer && !perk.isPassiv() && !"Tutorial".equals(Core.GameMode) && this.getHoursSurvived() > 1.0) {
                HaloTextHelper.addTextWithArrow((IsoPlayer)this, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, perk.getName()), true, HaloTextHelper.getColorGreen());
            }
            if (perkInfo.level > 10) {
                perkInfo.level = 10;
            }
            if (GameClient.bClient && this instanceof IsoPlayer) {
                GameClient.instance.sendSyncXp((IsoPlayer)this);
            }
            LuaEventManager.triggerEventGarbage("LevelPerk", this, perk, perkInfo.level, true);
            if (GameClient.bClient && this instanceof IsoPlayer) {
                GameClient.sendPerks((IsoPlayer)this);
            }
            return;
        }
        final PerkInfo e = new PerkInfo();
        e.perk = perk;
        e.level = 1;
        this.PerkList.add(e);
        if (GameClient.bClient && this instanceof IsoPlayer) {
            GameClient.instance.sendSyncXp((IsoPlayer)this);
        }
        LuaEventManager.triggerEvent("LevelPerk", this, perk, e.level, true);
    }
    
    public void LevelPerk(final PerkFactory.Perk perk) {
        this.LevelPerk(perk, true);
    }
    
    public void level0(final PerkFactory.Perk perk) {
        final PerkInfo perkInfo = this.getPerkInfo(perk);
        if (perkInfo != null) {
            perkInfo.level = 0;
        }
    }
    
    public Location getLastKnownLocationOf(final String s) {
        if (this.LastKnownLocation.containsKey(s)) {
            return this.LastKnownLocation.get(s);
        }
        return null;
    }
    
    public void ReadLiterature(final Literature literature) {
        final Stats stats = this.stats;
        stats.stress += literature.getStressChange();
        this.getBodyDamage().JustReadSomething(literature);
        if (literature.getTeachedRecipes() != null) {
            for (int i = 0; i < literature.getTeachedRecipes().size(); ++i) {
                if (!this.getKnownRecipes().contains(literature.getTeachedRecipes().get(i))) {
                    this.getKnownRecipes().add(literature.getTeachedRecipes().get(i));
                }
            }
        }
        literature.Use();
    }
    
    public void OnDeath() {
        LuaEventManager.triggerEvent("OnCharacterDeath", this);
    }
    
    public void splatBloodFloorBig() {
        if (this.getCurrentSquare() != null && this.getCurrentSquare().getChunk() != null) {
            this.getCurrentSquare().getChunk().addBloodSplat(this.x, this.y, this.z, Rand.Next(20));
        }
    }
    
    public void splatBloodFloor() {
        if (this.getCurrentSquare() == null) {
            return;
        }
        if (this.getCurrentSquare().getChunk() == null) {
            return;
        }
        if (this.isDead() && Rand.Next(10) == 0) {
            this.getCurrentSquare().getChunk().addBloodSplat(this.x, this.y, this.z, Rand.Next(20));
        }
        if (Rand.Next(14) == 0) {
            this.getCurrentSquare().getChunk().addBloodSplat(this.x, this.y, this.z, Rand.Next(8));
        }
        if (Rand.Next(50) == 0) {
            this.getCurrentSquare().getChunk().addBloodSplat(this.x, this.y, this.z, Rand.Next(20));
        }
    }
    
    public int getThreatLevel() {
        final int n = this.LocalRelevantEnemyList.size() + this.VeryCloseEnemyList.size() * 10;
        if (n > 20) {
            return 3;
        }
        if (n > 10) {
            return 2;
        }
        if (n > 0) {
            return 1;
        }
        return 0;
    }
    
    public boolean isDead() {
        return this.Health <= 0.0f || (this.BodyDamage != null && this.BodyDamage.getHealth() <= 0.0f);
    }
    
    public boolean isAlive() {
        return !this.isDead();
    }
    
    public void Seen(final Stack<IsoMovingObject> c) {
        synchronized (this.LocalList) {
            this.LocalList.clear();
            this.LocalList.addAll(c);
        }
    }
    
    public boolean CanSee(final IsoMovingObject isoMovingObject) {
        return LosUtil.lineClear(this.getCell(), (int)this.getX(), (int)this.getY(), (int)this.getZ(), (int)isoMovingObject.getX(), (int)isoMovingObject.getY(), (int)isoMovingObject.getZ(), false) != LosUtil.TestResults.Blocked;
    }
    
    public IsoGridSquare getLowDangerInVicinity(final int n, final int n2) {
        float n3 = -1000000.0f;
        IsoGridSquare isoGridSquare = null;
        for (int i = 0; i < n; ++i) {
            final float n4 = 0.0f;
            final IsoGridSquare gridSquare = this.getCell().getGridSquare((int)this.getX() + Rand.Next(-n2, n2), (int)this.getY() + Rand.Next(-n2, n2), (int)this.getZ());
            if (gridSquare != null && gridSquare.isFree(true)) {
                float n5 = (float)gridSquare.getMovingObjects().size();
                if (gridSquare.getE() != null) {
                    n5 += gridSquare.getE().getMovingObjects().size();
                }
                if (gridSquare.getS() != null) {
                    n5 += gridSquare.getS().getMovingObjects().size();
                }
                if (gridSquare.getW() != null) {
                    n5 += gridSquare.getW().getMovingObjects().size();
                }
                if (gridSquare.getN() != null) {
                    n5 += gridSquare.getN().getMovingObjects().size();
                }
                final float n6 = n4 - n5 * 1000.0f;
                if (n6 > n3) {
                    n3 = n6;
                    isoGridSquare = gridSquare;
                }
            }
        }
        return isoGridSquare;
    }
    
    public void Anger(int n) {
        if (Rand.Next(100) < 10.0f) {
            n *= 2;
        }
        n *= (int)(this.stats.getStress() + 1.0f);
        n *= (int)(this.BodyDamage.getUnhappynessLevel() / 100.0f + 1.0f);
        final Stats stats = this.stats;
        stats.Anger += n / 100.0f;
    }
    
    public boolean hasEquipped(String s) {
        if (s.contains(".")) {
            s = s.split("\\.")[1];
        }
        return (this.leftHandItem != null && this.leftHandItem.getType().equals(s)) || (this.rightHandItem != null && this.rightHandItem.getType().equals(s));
    }
    
    public boolean hasEquippedTag(final String s) {
        return (this.leftHandItem != null && this.leftHandItem.hasTag(s)) || (this.rightHandItem != null && this.rightHandItem.hasTag(s));
    }
    
    public void setDir(final IsoDirections dir) {
        this.dir = dir;
        this.getVectorFromDirection(this.m_forwardDirection);
    }
    
    public void Callout(final boolean b) {
        if (!this.isCanShout()) {
            return;
        }
        this.Callout();
        if (b) {
            this.playEmote("shout");
        }
    }
    
    public void Callout() {
        String s = "";
        int n = 30;
        if (Core.getInstance().getGameMode().equals("Tutorial")) {
            s = Translator.getText("IGUI_PlayerText_CalloutTutorial");
        }
        else if (this.isSneaking()) {
            n = 6;
            switch (Rand.Next(3)) {
                case 0: {
                    s = Translator.getText("IGUI_PlayerText_Callout1Sneak");
                    break;
                }
                case 1: {
                    s = Translator.getText("IGUI_PlayerText_Callout2Sneak");
                    break;
                }
                case 2: {
                    s = Translator.getText("IGUI_PlayerText_Callout3Sneak");
                    break;
                }
            }
        }
        else {
            switch (Rand.Next(3)) {
                case 0: {
                    s = Translator.getText("IGUI_PlayerText_Callout1New");
                    break;
                }
                case 1: {
                    s = Translator.getText("IGUI_PlayerText_Callout2New");
                    break;
                }
                case 2: {
                    s = Translator.getText("IGUI_PlayerText_Callout3New");
                    break;
                }
            }
        }
        WorldSoundManager.instance.addSound(this, (int)this.x, (int)this.y, (int)this.z, n, n);
        this.SayShout(s);
        this.callOut = true;
    }
    
    @Override
    public void load(final ByteBuffer byteBuffer, final int n, final boolean b) throws IOException {
        super.load(byteBuffer, n, b);
        this.getVectorFromDirection(this.m_forwardDirection);
        if (byteBuffer.get() == 1) {
            (this.descriptor = new SurvivorDesc(true)).load(byteBuffer, n, this);
            this.bFemale = this.descriptor.isFemale();
        }
        this.getVisual().load(byteBuffer, n);
        final ArrayList<InventoryItem> load = this.inventory.load(byteBuffer, n);
        this.savedInventoryItems.clear();
        for (int i = 0; i < load.size(); ++i) {
            this.savedInventoryItems.add(load.get(i));
        }
        this.Asleep = (byteBuffer.get() == 1);
        this.ForceWakeUpTime = byteBuffer.getFloat();
        if (!this.isZombie()) {
            this.stats.load(byteBuffer, n);
            this.BodyDamage.load(byteBuffer, n);
            this.xp.load(byteBuffer, n);
            final ArrayList<InventoryItem> includingObsoleteItems = this.inventory.IncludingObsoleteItems;
            final int int1 = byteBuffer.getInt();
            if (int1 >= 0 && int1 < includingObsoleteItems.size()) {
                this.leftHandItem = includingObsoleteItems.get(int1);
            }
            final int int2 = byteBuffer.getInt();
            if (int2 >= 0 && int2 < includingObsoleteItems.size()) {
                this.rightHandItem = includingObsoleteItems.get(int2);
            }
            this.setEquipParent(null, this.leftHandItem);
            this.setEquipParent(null, this.rightHandItem);
        }
        if (byteBuffer.get() == 1) {
            this.SetOnFire();
        }
        this.DepressEffect = byteBuffer.getFloat();
        this.DepressFirstTakeTime = byteBuffer.getFloat();
        this.BetaEffect = byteBuffer.getFloat();
        this.BetaDelta = byteBuffer.getFloat();
        this.PainEffect = byteBuffer.getFloat();
        this.PainDelta = byteBuffer.getFloat();
        this.SleepingTabletEffect = byteBuffer.getFloat();
        this.SleepingTabletDelta = byteBuffer.getFloat();
        for (int int3 = byteBuffer.getInt(), j = 0; j < int3; ++j) {
            final ReadBook e = new ReadBook();
            e.fullType = GameWindow.ReadString(byteBuffer);
            e.alreadyReadPages = byteBuffer.getInt();
            this.ReadBooks.add(e);
        }
        this.reduceInfectionPower = byteBuffer.getFloat();
        for (int int4 = byteBuffer.getInt(), k = 0; k < int4; ++k) {
            this.knownRecipes.add(GameWindow.ReadString(byteBuffer));
        }
        this.lastHourSleeped = byteBuffer.getInt();
        this.timeSinceLastSmoke = byteBuffer.getFloat();
        this.beardGrowTiming = byteBuffer.getFloat();
        this.hairGrowTiming = byteBuffer.getFloat();
        this.setUnlimitedCarry(byteBuffer.get() == 1);
        this.setBuildCheat(byteBuffer.get() == 1);
        this.setHealthCheat(byteBuffer.get() == 1);
        this.setMechanicsCheat(byteBuffer.get() == 1);
        if (n >= 176) {
            this.setMovablesCheat(byteBuffer.get() == 1);
            this.setFarmingCheat(byteBuffer.get() == 1);
            this.setTimedActionInstantCheat(byteBuffer.get() == 1);
            this.setUnlimitedEndurance(byteBuffer.get() == 1);
        }
        if (n >= 161) {
            this.setSneaking(byteBuffer.get() == 1);
            this.setDeathDragDown(byteBuffer.get() == 1);
        }
    }
    
    @Override
    public void save(final ByteBuffer byteBuffer, final boolean b) throws IOException {
        super.save(byteBuffer, b);
        if (this.descriptor == null) {
            byteBuffer.put((byte)0);
        }
        else {
            byteBuffer.put((byte)1);
            this.descriptor.save(byteBuffer);
        }
        this.getVisual().save(byteBuffer);
        final ArrayList<InventoryItem> save = this.inventory.save(byteBuffer, this);
        this.savedInventoryItems.clear();
        for (int i = 0; i < save.size(); ++i) {
            this.savedInventoryItems.add(save.get(i));
        }
        byteBuffer.put((byte)(this.Asleep ? 1 : 0));
        byteBuffer.putFloat(this.ForceWakeUpTime);
        if (!this.isZombie()) {
            this.stats.save(byteBuffer);
            this.BodyDamage.save(byteBuffer);
            this.xp.save(byteBuffer);
            if (this.leftHandItem != null) {
                byteBuffer.putInt(this.inventory.getItems().indexOf(this.leftHandItem));
            }
            else {
                byteBuffer.putInt(-1);
            }
            if (this.rightHandItem != null) {
                byteBuffer.putInt(this.inventory.getItems().indexOf(this.rightHandItem));
            }
            else {
                byteBuffer.putInt(-1);
            }
        }
        byteBuffer.put((byte)(this.OnFire ? 1 : 0));
        byteBuffer.putFloat(this.DepressEffect);
        byteBuffer.putFloat(this.DepressFirstTakeTime);
        byteBuffer.putFloat(this.BetaEffect);
        byteBuffer.putFloat(this.BetaDelta);
        byteBuffer.putFloat(this.PainEffect);
        byteBuffer.putFloat(this.PainDelta);
        byteBuffer.putFloat(this.SleepingTabletEffect);
        byteBuffer.putFloat(this.SleepingTabletDelta);
        byteBuffer.putInt(this.ReadBooks.size());
        for (int j = 0; j < this.ReadBooks.size(); ++j) {
            final ReadBook readBook = this.ReadBooks.get(j);
            GameWindow.WriteString(byteBuffer, readBook.fullType);
            byteBuffer.putInt(readBook.alreadyReadPages);
        }
        byteBuffer.putFloat(this.reduceInfectionPower);
        byteBuffer.putInt(this.knownRecipes.size());
        for (int k = 0; k < this.knownRecipes.size(); ++k) {
            GameWindow.WriteString(byteBuffer, this.knownRecipes.get(k));
        }
        byteBuffer.putInt(this.lastHourSleeped);
        byteBuffer.putFloat(this.timeSinceLastSmoke);
        byteBuffer.putFloat(this.beardGrowTiming);
        byteBuffer.putFloat(this.hairGrowTiming);
        byteBuffer.put((byte)(this.isUnlimitedCarry() ? 1 : 0));
        byteBuffer.put((byte)(this.isBuildCheat() ? 1 : 0));
        byteBuffer.put((byte)(this.isHealthCheat() ? 1 : 0));
        byteBuffer.put((byte)(this.isMechanicsCheat() ? 1 : 0));
        byteBuffer.put((byte)(this.isMovablesCheat() ? 1 : 0));
        byteBuffer.put((byte)(this.isFarmingCheat() ? 1 : 0));
        byteBuffer.put((byte)(this.isTimedActionInstantCheat() ? 1 : 0));
        byteBuffer.put((byte)(this.isUnlimitedEndurance() ? 1 : 0));
        byteBuffer.put((byte)(this.isSneaking() ? 1 : 0));
        byteBuffer.put((byte)(this.isDeathDragDown() ? 1 : 0));
    }
    
    public ChatElement getChatElement() {
        return this.chatElement;
    }
    
    public void StartAction(final BaseAction item) {
        this.CharacterActions.clear();
        this.CharacterActions.push(item);
        if (item.valid()) {
            item.waitToStart();
        }
    }
    
    public void QueueAction(final BaseAction baseAction) {
    }
    
    public void StopAllActionQueue() {
        if (this.CharacterActions.isEmpty()) {
            return;
        }
        final BaseAction baseAction = this.CharacterActions.get(0);
        if (baseAction.bStarted) {
            baseAction.stop();
        }
        this.CharacterActions.clear();
        if (this == IsoPlayer.players[0] || this == IsoPlayer.players[1] || this == IsoPlayer.players[2] || this == IsoPlayer.players[3]) {
            UIManager.getProgressBar(((IsoPlayer)this).getPlayerNum()).setValue(0.0f);
        }
    }
    
    public void StopAllActionQueueRunning() {
        if (this.CharacterActions.isEmpty()) {
            return;
        }
        final BaseAction baseAction = this.CharacterActions.get(0);
        if (!baseAction.StopOnRun) {
            return;
        }
        if (baseAction.bStarted) {
            baseAction.stop();
        }
        this.CharacterActions.clear();
        if (this == IsoPlayer.players[0] || this == IsoPlayer.players[1] || this == IsoPlayer.players[2] || this == IsoPlayer.players[3]) {
            UIManager.getProgressBar(((IsoPlayer)this).getPlayerNum()).setValue(0.0f);
        }
    }
    
    public void StopAllActionQueueAiming() {
        if (this.CharacterActions.size() == 0) {
            return;
        }
        final BaseAction baseAction = this.CharacterActions.get(0);
        if (!baseAction.StopOnAim) {
            return;
        }
        if (baseAction.bStarted) {
            baseAction.stop();
        }
        this.CharacterActions.clear();
        if (this == IsoPlayer.players[0] || this == IsoPlayer.players[1] || this == IsoPlayer.players[2] || this == IsoPlayer.players[3]) {
            UIManager.getProgressBar(((IsoPlayer)this).getPlayerNum()).setValue(0.0f);
        }
    }
    
    public void StopAllActionQueueWalking() {
        if (this.CharacterActions.size() == 0) {
            return;
        }
        final BaseAction baseAction = this.CharacterActions.get(0);
        if (!baseAction.StopOnWalk) {
            return;
        }
        if (baseAction.bStarted) {
            baseAction.stop();
        }
        this.CharacterActions.clear();
        if (this == IsoPlayer.players[0] || this == IsoPlayer.players[1] || this == IsoPlayer.players[2] || this == IsoPlayer.players[3]) {
            UIManager.getProgressBar(((IsoPlayer)this).getPlayerNum()).setValue(0.0f);
        }
    }
    
    @Override
    public String GetAnimSetName() {
        return "Base";
    }
    
    public void SleepingTablet(final float n) {
        this.SleepingTabletEffect = 6600.0f;
        this.SleepingTabletDelta += n;
    }
    
    public void BetaBlockers(final float n) {
        this.BetaEffect = 6600.0f;
        this.BetaDelta += n;
    }
    
    public void BetaAntiDepress(final float n) {
        if (this.DepressEffect == 0.0f) {
            this.DepressFirstTakeTime = 10000.0f;
        }
        this.DepressEffect = 6600.0f;
        this.DepressDelta += n;
    }
    
    public void PainMeds(final float n) {
        this.PainEffect = 5400.0f;
        this.PainDelta += n;
    }
    
    public void initSpritePartsEmpty() {
        this.InitSpriteParts(this.descriptor);
    }
    
    public void InitSpriteParts(final SurvivorDesc survivorDesc) {
        this.sprite.AnimMap.clear();
        this.sprite.AnimStack.clear();
        this.sprite.CurrentAnim = null;
        this.legsSprite = this.sprite;
        this.legsSprite.name = survivorDesc.torso;
        this.bUseParts = true;
    }
    
    public boolean HasTrait(final String s) {
        return this.Traits.contains(s);
    }
    
    public void ApplyInBedOffset(final boolean b) {
        if (b) {
            if (!this.bOnBed) {
                this.offsetX -= 20.0f;
                this.offsetY += 21.0f;
                this.bOnBed = true;
            }
        }
        else if (this.bOnBed) {
            this.offsetX += 20.0f;
            this.offsetY -= 21.0f;
            this.bOnBed = false;
        }
    }
    
    public void Dressup(final SurvivorDesc survivorDesc) {
        if (this.isZombie()) {
            return;
        }
        if (this.wornItems == null) {
            return;
        }
        final ItemVisuals fromItemVisuals = new ItemVisuals();
        survivorDesc.getItemVisuals(fromItemVisuals);
        this.wornItems.setFromItemVisuals(fromItemVisuals);
        this.wornItems.addItemsToItemContainer(this.inventory);
        survivorDesc.wornItems.clear();
        this.onWornItemsChanged();
    }
    
    public void PlayAnim(final String s) {
    }
    
    public void PlayAnimWithSpeed(final String s, final float n) {
    }
    
    public void PlayAnimUnlooped(final String s) {
    }
    
    public void DirectionFromVector(final Vector2 vector2) {
        this.dir = IsoDirections.fromAngle(vector2);
    }
    
    public void DoFootstepSound(final String s) {
        float n = 1.0f;
        switch (s) {
            case "sneak_walk": {
                n = 0.2f;
                break;
            }
            case "sneak_run": {
                n = 0.5f;
                break;
            }
            case "strafe": {
                n = (this.bSneaking ? 0.2f : 0.3f);
                break;
            }
            case "walk": {
                n = 0.5f;
                break;
            }
            case "run": {
                n = 1.3f;
                break;
            }
            case "sprint": {
                n = 1.8f;
                break;
            }
        }
        this.DoFootstepSound(n);
    }
    
    public void DoFootstepSound(float n) {
        final IsoPlayer isoPlayer = Type.tryCastTo(this, IsoPlayer.class);
        if (GameClient.bClient && isoPlayer != null && isoPlayer.networkAI != null) {
            isoPlayer.networkAI.footstepSoundRadius = 0;
        }
        if (isoPlayer != null && isoPlayer.isGhostMode() && !DebugOptions.instance.Character.Debug.PlaySoundWhenInvisible.getValue()) {
            return;
        }
        if (this.getCurrentSquare() == null) {
            return;
        }
        if (n <= 0.0f) {
            return;
        }
        final float n2 = n;
        n *= 1.4f;
        if (this.Traits.Graceful.isSet()) {
            n *= 0.6f;
        }
        if (this.Traits.Clumsy.isSet()) {
            n *= 1.2f;
        }
        if (this.getWornItem("Shoes") == null) {
            n *= 0.5f;
        }
        n *= this.getLightfootMod();
        n *= 2.0f - this.getNimbleMod();
        if (this.bSneaking) {
            n *= this.getSneakSpotMod();
        }
        if (n > 0.0f) {
            this.emitter.playFootsteps("HumanFootstepsCombined", n2);
            if (isoPlayer != null && isoPlayer.isGhostMode()) {
                return;
            }
            int max = (int)Math.ceil(n * 10.0f);
            if (this.bSneaking) {
                max = Math.max(1, max);
            }
            if (this.getCurrentSquare().getRoom() != null) {
                max *= (int)0.5f;
            }
            int min = 2;
            if (this.bSneaking) {
                min = Math.min(12, 4 + this.getPerkLevel(PerkFactory.Perks.Lightfoot));
            }
            if (GameClient.bClient && isoPlayer != null && isoPlayer.networkAI != null) {
                isoPlayer.networkAI.footstepSoundRadius = (byte)max;
            }
            if (Rand.Next(min) == 0) {
                WorldSoundManager.instance.addSound(this, (int)this.getX(), (int)this.getY(), (int)this.getZ(), max, max, false, 0.0f, 1.0f, false, false, false);
            }
        }
    }
    
    public boolean Eat(final InventoryItem inventoryItem, float n) {
        final Food food = Type.tryCastTo(inventoryItem, Food.class);
        if (food == null) {
            return false;
        }
        n = PZMath.clamp(n, 0.0f, 1.0f);
        if (food.getRequireInHandOrInventory() != null) {
            for (int i = 0; i < food.getRequireInHandOrInventory().size(); ++i) {
                final InventoryItem findAndReturn = this.getInventory().FindAndReturn(food.getRequireInHandOrInventory().get(i));
                if (findAndReturn != null) {
                    findAndReturn.Use();
                    break;
                }
            }
        }
        if (food.getBaseHunger() != 0.0f && food.getHungChange() != 0.0f) {
            n = PZMath.clamp(food.getBaseHunger() * n / food.getHungChange(), 0.0f, 1.0f);
        }
        if (food.getHungChange() < 0.0f && food.getHungChange() * (1.0f - n) > -0.01f) {
            n = 1.0f;
        }
        if (food.getHungChange() == 0.0f && food.getThirstChange() < 0.0f && food.getThirstChange() * (1.0f - n) > -0.01f) {
            n = 1.0f;
        }
        final Stats stats = this.stats;
        stats.thirst += food.getThirstChange() * n;
        if (this.stats.thirst < 0.0f) {
            this.stats.thirst = 0.0f;
        }
        final Stats stats2 = this.stats;
        stats2.hunger += food.getHungerChange() * n;
        final Stats stats3 = this.stats;
        stats3.endurance += food.getEnduranceChange() * n;
        final Stats stats4 = this.stats;
        stats4.stress += food.getStressChange() * n;
        final Stats stats5 = this.stats;
        stats5.fatigue += food.getFatigueChange() * n;
        final IsoPlayer isoPlayer = Type.tryCastTo(this, IsoPlayer.class);
        if (isoPlayer != null) {
            final Nutrition nutrition = isoPlayer.getNutrition();
            nutrition.setCalories(nutrition.getCalories() + food.getCalories() * n);
            nutrition.setCarbohydrates(nutrition.getCarbohydrates() + food.getCarbohydrates() * n);
            nutrition.setProteins(nutrition.getProteins() + food.getProteins() * n);
            nutrition.setLipids(nutrition.getLipids() + food.getLipids() * n);
        }
        this.BodyDamage.setPainReduction(this.BodyDamage.getPainReduction() + food.getPainReduction() * n);
        this.BodyDamage.setColdReduction(this.BodyDamage.getColdReduction() + food.getFluReduction() * n);
        if (this.BodyDamage.getFoodSicknessLevel() > 0.0f && food.getReduceFoodSickness() > 0.0f && this.effectiveEdibleBuffTimer <= 0.0f) {
            this.BodyDamage.getFoodSicknessLevel();
            this.BodyDamage.setFoodSicknessLevel(this.BodyDamage.getFoodSicknessLevel() - food.getReduceFoodSickness() * n);
            if (this.BodyDamage.getFoodSicknessLevel() < 0.0f) {
                this.BodyDamage.setFoodSicknessLevel(0.0f);
            }
            this.BodyDamage.getPoisonLevel();
            this.BodyDamage.setPoisonLevel(this.BodyDamage.getPoisonLevel() - food.getReduceFoodSickness() * n);
            if (this.BodyDamage.getPoisonLevel() < 0.0f) {
                this.BodyDamage.setPoisonLevel(0.0f);
            }
            if (this.Traits.IronGut.isSet()) {
                this.effectiveEdibleBuffTimer = Rand.Next(80.0f, 150.0f);
            }
            else if (this.Traits.WeakStomach.isSet()) {
                this.effectiveEdibleBuffTimer = Rand.Next(120.0f, 230.0f);
            }
            else {
                this.effectiveEdibleBuffTimer = Rand.Next(200.0f, 280.0f);
            }
        }
        this.BodyDamage.JustAteFood(food, n);
        if (GameClient.bClient && this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
            GameClient.instance.eatFood((IsoPlayer)this, food, n);
        }
        if (food.getOnEat() != null) {
            final Object functionObject = LuaManager.getFunctionObject(food.getOnEat());
            if (functionObject != null) {
                LuaManager.caller.pcallvoid(LuaManager.thread, functionObject, (Object)inventoryItem, (Object)this, (Object)BoxedStaticValues.toDouble(n));
            }
        }
        if (n == 1.0f) {
            food.setHungChange(0.0f);
            food.UseItem();
        }
        else {
            final float hungChange = food.getHungChange();
            final float thirstChange = food.getThirstChange();
            food.multiplyFoodValues(1.0f - n);
            if (hungChange < 0.0f && food.getHungerChange() > -0.01) {
                food.setHungChange(0.0f);
                food.UseItem();
                return true;
            }
            if (hungChange == 0.0f && thirstChange < 0.0f && food.getThirstChange() > -0.01f) {
                food.setHungChange(0.0f);
                food.UseItem();
                return true;
            }
        }
        return true;
    }
    
    public boolean Eat(final InventoryItem inventoryItem) {
        return this.Eat(inventoryItem, 1.0f);
    }
    
    public void FireCheck() {
        if (this.OnFire) {
            return;
        }
        if (GameServer.bServer && this instanceof IsoPlayer) {
            return;
        }
        if (GameClient.bClient && this.isZombie() && this instanceof IsoZombie && ((IsoZombie)this).isRemoteZombie()) {
            return;
        }
        if (this.isZombie() && VirtualZombieManager.instance.isReused((IsoZombie)this)) {
            DebugLog.log(DebugType.Zombie, invokedynamic(makeConcatWithConstants:(Lzombie/characters/IsoGameCharacter;)Ljava/lang/String;, this));
            return;
        }
        if (this.getVehicle() != null) {
            return;
        }
        if (this.square != null && !GameServer.bServer && (!GameClient.bClient || (this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) || (this instanceof IsoZombie && !((IsoZombie)this).isRemoteZombie())) && this.square.getProperties().Is(IsoFlagType.burning)) {
            if ((this instanceof IsoPlayer && Rand.Next(Rand.AdjustForFramerate(70)) == 0) || this.isZombie()) {
                this.SetOnFire();
            }
            else {
                if (!(this instanceof IsoPlayer)) {
                    this.Health -= this.FireKillRate * GameTime.instance.getMultiplier() / 2.0f;
                    this.setAttackedBy(null);
                }
                else {
                    this.BodyDamage.ReduceGeneralHealth(this.FireKillRate * GameTime.instance.getMultiplier() * GameTime.instance.getMinutesPerDay() / 1.6f / 2.0f);
                    this.BodyDamage.OnFire(true);
                    this.forceAwake();
                }
                if (this.isDead()) {
                    IsoFireManager.RemoveBurningCharacter(this);
                }
            }
        }
    }
    
    public String getPrimaryHandType() {
        if (this.leftHandItem == null) {
            return null;
        }
        return this.leftHandItem.getType();
    }
    
    @Override
    public float getGlobalMovementMod(final boolean b) {
        if (this.getCurrentState() == ClimbOverFenceState.instance() || this.getCurrentState() == ClimbThroughWindowState.instance() || this.getCurrentState() == ClimbOverWallState.instance()) {
            return 1.0f;
        }
        return super.getGlobalMovementMod(b);
    }
    
    public float getMoveSpeed() {
        IsoGameCharacter.tempo2.x = this.getX() - this.getLx();
        IsoGameCharacter.tempo2.y = this.getY() - this.getLy();
        return IsoGameCharacter.tempo2.getLength();
    }
    
    public String getSecondaryHandType() {
        if (this.rightHandItem == null) {
            return null;
        }
        return this.rightHandItem.getType();
    }
    
    public boolean HasItem(final String s) {
        return s == null || s.equals(this.getSecondaryHandType()) || s.equals(this.getPrimaryHandType()) || this.inventory.contains(s);
    }
    
    public void changeState(final State state) {
        this.stateMachine.changeState(state, null);
    }
    
    public State getCurrentState() {
        return this.stateMachine.getCurrent();
    }
    
    public boolean isCurrentState(final State state) {
        return this.stateMachine.isSubstate(state) || this.stateMachine.getCurrent() == state;
    }
    
    public HashMap<Object, Object> getStateMachineParams(final State key) {
        return this.StateMachineParams.computeIfAbsent(key, p0 -> new HashMap());
    }
    
    public void setStateMachineLocked(final boolean locked) {
        this.stateMachine.setLocked(locked);
    }
    
    @Override
    public float Hit(final HandWeapon handWeapon, final IsoGameCharacter isoGameCharacter, final float n, final boolean b, final float n2) {
        return this.Hit(handWeapon, isoGameCharacter, n, b, n2, false);
    }
    
    public float Hit(final HandWeapon handWeapon, final IsoGameCharacter attackedBy, float n, boolean b, float n2, final boolean b2) {
        if (attackedBy == null || handWeapon == null) {
            return 0.0f;
        }
        if (!b && this.isZombie()) {
            final IsoZombie isoZombie = (IsoZombie)this;
            isoZombie.setHitTime(isoZombie.getHitTime() + 1);
            if (isoZombie.getHitTime() >= 4 && !b2) {
                n *= (float)((isoZombie.getHitTime() - 2) * 1.5);
            }
        }
        if (attackedBy instanceof IsoPlayer && ((IsoPlayer)attackedBy).bDoShove && !((IsoPlayer)attackedBy).isAimAtFloor()) {
            b = true;
            n2 *= 1.5f;
        }
        LuaEventManager.triggerEvent("OnWeaponHitCharacter", attackedBy, this, handWeapon, n);
        if (LuaHookManager.TriggerHook("WeaponHitCharacter", attackedBy, this, handWeapon, n)) {
            return 0.0f;
        }
        if (this.m_avoidDamage) {
            this.m_avoidDamage = false;
            return 0.0f;
        }
        if (this.noDamage) {
            b = true;
            this.noDamage = false;
        }
        if (this instanceof IsoSurvivor && !this.EnemyList.contains(attackedBy)) {
            this.EnemyList.add(attackedBy);
        }
        this.staggerTimeMod = handWeapon.getPushBackMod() * handWeapon.getKnockbackMod(attackedBy) * attackedBy.getShovingMod();
        if (!this.isZombie() || Rand.Next(3) != 0 || GameServer.bServer) {}
        attackedBy.addWorldSoundUnlessInvisible(5, 1, false);
        this.hitDir.x = this.getX();
        this.hitDir.y = this.getY();
        final Vector2 hitDir = this.hitDir;
        hitDir.x -= attackedBy.getX();
        final Vector2 hitDir2 = this.hitDir;
        hitDir2.y -= attackedBy.getY();
        this.getHitDir().normalize();
        final Vector2 hitDir3 = this.hitDir;
        hitDir3.x *= handWeapon.getPushBackMod();
        final Vector2 hitDir4 = this.hitDir;
        hitDir4.y *= handWeapon.getPushBackMod();
        this.hitDir.rotate(handWeapon.HitAngleMod);
        this.setAttackedBy(attackedBy);
        float processHitDamage = n;
        if (!b2) {
            processHitDamage = this.processHitDamage(handWeapon, attackedBy, n, b, n2);
        }
        float n3 = 0.0f;
        if (handWeapon.isTwoHandWeapon() && (attackedBy.getPrimaryHandItem() != handWeapon || attackedBy.getSecondaryHandItem() != handWeapon)) {
            n3 = handWeapon.getWeight() / 1.5f / 10.0f;
        }
        float n4 = (handWeapon.getWeight() * 0.28f * handWeapon.getFatigueMod(attackedBy) * this.getFatigueMod() * handWeapon.getEnduranceMod() * 0.3f + n3) * 0.04f;
        if (attackedBy instanceof IsoPlayer && attackedBy.isAimAtFloor() && ((IsoPlayer)attackedBy).bDoShove) {
            n4 *= 2.0f;
        }
        float health;
        if (handWeapon.isAimedFirearm()) {
            health = processHitDamage * 0.7f;
        }
        else {
            health = processHitDamage * 0.15f;
        }
        if (this.getHealth() < processHitDamage) {
            health = this.getHealth();
        }
        float n5 = health / handWeapon.getMaxDamage();
        if (n5 > 1.0f) {
            n5 = 1.0f;
        }
        if (this.isCloseKilled()) {
            n5 = 0.2f;
        }
        if (handWeapon.isUseEndurance()) {
            final Stats stats = attackedBy.getStats();
            stats.endurance -= n4 * n5;
        }
        this.hitConsequences(handWeapon, attackedBy, b, processHitDamage, b2);
        return processHitDamage;
    }
    
    public float processHitDamage(final HandWeapon handWeapon, final IsoGameCharacter isoGameCharacter, float n, final boolean b, final float n2) {
        float n4;
        float n3 = n4 = n * n2;
        if (b) {
            n4 /= 2.7f;
        }
        float hitForce = n4 * isoGameCharacter.getShovingMod();
        if (hitForce > 1.0f) {
            hitForce = 1.0f;
        }
        this.setHitForce(hitForce);
        if (isoGameCharacter.Traits.Strong.isSet() && !handWeapon.isRanged()) {
            this.setHitForce(this.getHitForce() * 1.4f);
        }
        if (isoGameCharacter.Traits.Weak.isSet() && !handWeapon.isRanged()) {
            this.setHitForce(this.getHitForce() * 0.6f);
        }
        if (1.0f - (IsoUtils.DistanceTo(isoGameCharacter.getX(), isoGameCharacter.getY(), this.getX(), this.getY()) - handWeapon.getMinRange()) / handWeapon.getMaxRange(isoGameCharacter) > 1.0f) {}
        final float n5 = isoGameCharacter.stats.endurance * isoGameCharacter.knockbackAttackMod;
        if (n5 < 0.5f) {
            float n6 = n5 * 1.3f;
            if (n6 < 0.4f) {
                n6 = 0.4f;
            }
            this.setHitForce(this.getHitForce() * n6);
        }
        if (!handWeapon.isRangeFalloff()) {}
        if (!handWeapon.isShareDamage()) {
            n = 1.0f;
        }
        if (isoGameCharacter instanceof IsoPlayer && !b) {
            this.setHitForce(this.getHitForce() * 2.0f);
        }
        if (isoGameCharacter instanceof IsoPlayer && !((IsoPlayer)isoGameCharacter).bDoShove) {
            final Vector2 set = IsoGameCharacter.tempVector2_1.set(this.getX(), this.getY());
            final Vector2 set2 = IsoGameCharacter.tempVector2_2.set(isoGameCharacter.getX(), isoGameCharacter.getY());
            final Vector2 vector2 = set;
            vector2.x -= set2.x;
            final Vector2 vector3 = set;
            vector3.y -= set2.y;
            final Vector2 vectorFromDirection = this.getVectorFromDirection(IsoGameCharacter.tempVector2_2);
            set.normalize();
            if (set.dot(vectorFromDirection) > -0.3f) {
                n3 *= 1.5f;
            }
        }
        float n7;
        if (this instanceof IsoPlayer) {
            n7 = n3 * 0.4f;
        }
        else {
            n7 = n3 * 1.5f;
        }
        switch (isoGameCharacter.getWeaponLevel()) {
            case -1: {
                n7 *= 0.3f;
                break;
            }
            case 0: {
                n7 *= 0.3f;
                break;
            }
            case 1: {
                n7 *= 0.4f;
                break;
            }
            case 2: {
                n7 *= 0.5f;
                break;
            }
            case 3: {
                n7 *= 0.6f;
                break;
            }
            case 4: {
                n7 *= 0.7f;
                break;
            }
            case 5: {
                n7 *= 0.8f;
                break;
            }
            case 6: {
                n7 *= 0.9f;
                break;
            }
            case 7: {
                n7 *= 1.0f;
                break;
            }
            case 8: {
                n7 *= 1.1f;
                break;
            }
            case 9: {
                n7 *= 1.2f;
                break;
            }
            case 10: {
                n7 *= 1.3f;
                break;
            }
        }
        if (isoGameCharacter instanceof IsoPlayer && isoGameCharacter.isAimAtFloor() && !b && !((IsoPlayer)isoGameCharacter).bDoShove) {
            n7 *= Math.max(5.0f, handWeapon.getCritDmgMultiplier());
        }
        if (isoGameCharacter.isCriticalHit() && !b) {
            n7 *= Math.max(2.0f, handWeapon.getCritDmgMultiplier());
        }
        if (handWeapon.isTwoHandWeapon() && !isoGameCharacter.isItemInBothHands(handWeapon)) {
            n7 *= 0.5f;
        }
        return n7;
    }
    
    public void hitConsequences(final HandWeapon handWeapon, final IsoGameCharacter isoGameCharacter, final boolean b, final float n, final boolean b2) {
        if (!b) {
            if (handWeapon.isAimedFirearm()) {
                this.Health -= n * 0.7f;
            }
            else {
                this.Health -= n * 0.15f;
            }
        }
        if (this.isDead()) {
            if (!this.isOnKillDone() && this.shouldDoInventory()) {
                this.Kill(isoGameCharacter);
            }
            if (this instanceof IsoZombie && ((IsoZombie)this).upKillCount) {
                isoGameCharacter.setZombieKills(isoGameCharacter.getZombieKills() + 1);
            }
            return;
        }
        if (handWeapon.isSplatBloodOnNoDeath()) {
            this.splatBlood(2, 0.2f);
        }
        if (handWeapon.isKnockBackOnNoDeath() && isoGameCharacter.xp != null) {
            isoGameCharacter.xp.AddXP(PerkFactory.Perks.Strength, 2.0f);
        }
    }
    
    public boolean IsAttackRange(final float n, final float n2, final float n3) {
        float n4 = 1.0f;
        float minRange = 0.0f;
        if (this.leftHandItem != null) {
            final InventoryItem leftHandItem = this.leftHandItem;
            if (leftHandItem instanceof HandWeapon) {
                final float maxRange = ((HandWeapon)leftHandItem).getMaxRange(this);
                minRange = ((HandWeapon)leftHandItem).getMinRange();
                n4 = maxRange * ((HandWeapon)this.leftHandItem).getRangeMod(this);
            }
        }
        if (Math.abs(n3 - this.getZ()) > 0.3f) {
            return false;
        }
        final float distanceTo = IsoUtils.DistanceTo(n, n2, this.getX(), this.getY());
        return distanceTo < n4 && distanceTo > minRange;
    }
    
    public boolean IsAttackRange(final HandWeapon handWeapon, final IsoMovingObject isoMovingObject, final Vector3 vector3, final boolean b) {
        if (handWeapon == null) {
            return false;
        }
        final float abs = Math.abs(isoMovingObject.getZ() - this.getZ());
        if (!handWeapon.isRanged() && abs >= 0.5f) {
            return false;
        }
        if (abs > 3.3f) {
            return false;
        }
        float n = handWeapon.getMaxRange(this) * handWeapon.getRangeMod(this);
        final float distanceToSquared = IsoUtils.DistanceToSquared(this.x, this.y, vector3.x, vector3.y);
        if (b) {
            final IsoZombie isoZombie = Type.tryCastTo(isoMovingObject, IsoZombie.class);
            if (isoZombie != null && distanceToSquared < 4.0f && isoZombie.target == this && (isoZombie.isCurrentState(LungeState.instance()) || isoZombie.isCurrentState(LungeNetworkState.instance()))) {
                ++n;
            }
        }
        return distanceToSquared < n * n;
    }
    
    @Override
    public boolean IsSpeaking() {
        return this.chatElement.IsSpeaking();
    }
    
    public void MoveForward(final float n, final float n2, final float n3, final float n4) {
        if (this.isCurrentState(SwipeStatePlayer.instance())) {
            return;
        }
        final float multiplier = GameTime.instance.getMultiplier();
        this.setNx(this.getNx() + n2 * n * multiplier);
        this.setNy(this.getNy() + n3 * n * multiplier);
        this.DoFootstepSound(n);
        if (!this.isZombie()) {}
    }
    
    private void pathToAux(final float n, final float n2, final float n3) {
        int n4 = 1;
        if ((int)n3 == (int)this.getZ() && IsoUtils.DistanceManhatten(n, n2, this.x, this.y) <= 30.0f) {
            final int n5 = (int)n / 10;
            final int n6 = (int)n2 / 10;
            if ((GameServer.bServer ? ServerMap.instance.getChunk(n5, n6) : IsoWorld.instance.CurrentCell.getChunkForGridSquare((int)n, (int)n2, (int)n3)) != null) {
                int n7 = 0x1 | 0x2;
                if (!this.isZombie()) {
                    n7 |= 0x4;
                }
                n4 = (PolygonalMap2.instance.lineClearCollide(this.getX(), this.getY(), n, n2, (int)n3, this.getPathFindBehavior2().getTargetChar(), n7) ? 0 : 1);
            }
        }
        if (n4 != 0 && this.current != null && this.current.HasStairs() && !this.current.isSameStaircase((int)n, (int)n2, (int)n3)) {
            n4 = 0;
        }
        if (n4 != 0) {
            this.setVariable("bPathfind", false);
            this.setMoving(true);
        }
        else {
            this.setVariable("bPathfind", true);
            this.setMoving(false);
        }
    }
    
    public void pathToCharacter(final IsoGameCharacter isoGameCharacter) {
        this.getPathFindBehavior2().pathToCharacter(isoGameCharacter);
        this.pathToAux(isoGameCharacter.getX(), isoGameCharacter.getY(), isoGameCharacter.getZ());
    }
    
    public void pathToLocation(final int n, final int n2, final int n3) {
        this.getPathFindBehavior2().pathToLocation(n, n2, n3);
        this.pathToAux(n + 0.5f, n2 + 0.5f, (float)n3);
    }
    
    public void pathToLocationF(final float n, final float n2, final float n3) {
        this.getPathFindBehavior2().pathToLocationF(n, n2, n3);
        this.pathToAux(n, n2, n3);
    }
    
    public void pathToSound(final int n, final int n2, final int n3) {
        this.getPathFindBehavior2().pathToSound(n, n2, n3);
        this.pathToAux(n + 0.5f, n2 + 0.5f, (float)n3);
    }
    
    public boolean CanAttack() {
        if (this.isAttackAnim() || this.getVariableBoolean("IsRacking") || this.getVariableBoolean("IsUnloading") || !StringUtils.isNullOrEmpty(this.getVariableString("RackWeapon"))) {
            return false;
        }
        if (GameClient.bClient && this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer() && (this.isCurrentState(PlayerHitReactionState.instance()) || this.isCurrentState(PlayerHitReactionPVPState.instance()))) {
            return false;
        }
        if (this.isSitOnGround()) {
            return false;
        }
        final InventoryItem leftHandItem = this.leftHandItem;
        if (leftHandItem instanceof HandWeapon && leftHandItem.getSwingAnim() != null) {
            this.useHandWeapon = (HandWeapon)leftHandItem;
        }
        if (this.useHandWeapon == null) {
            return true;
        }
        if (this.useHandWeapon.getCondition() <= 0) {
            this.useHandWeapon = null;
            if (this.rightHandItem == this.leftHandItem) {
                this.setSecondaryHandItem(null);
            }
            this.setPrimaryHandItem(null);
            if (this.getInventory() != null) {
                this.getInventory().setDrawDirty(true);
            }
            return false;
        }
        final int moodleLevel = this.Moodles.getMoodleLevel(MoodleType.Endurance);
        return !this.useHandWeapon.isCantAttackWithLowestEndurance() || moodleLevel != 4;
    }
    
    public void ReduceHealthWhenBurning() {
        if (!this.OnFire) {
            return;
        }
        if (this.isGodMod()) {
            this.StopBurning();
            return;
        }
        if (GameClient.bClient && this.isZombie() && this instanceof IsoZombie && ((IsoZombie)this).isRemoteZombie()) {
            return;
        }
        if (GameClient.bClient && this instanceof IsoPlayer && ((IsoPlayer)this).bRemote) {
            return;
        }
        if (this.isAlive()) {
            if (!(this instanceof IsoPlayer)) {
                if (this.isZombie()) {
                    this.Health -= this.FireKillRate / 20.0f * GameTime.instance.getMultiplier();
                    this.setAttackedBy(null);
                }
                else {
                    this.Health -= this.FireKillRate * GameTime.instance.getMultiplier();
                }
            }
            else {
                this.BodyDamage.ReduceGeneralHealth(this.FireKillRate * GameTime.instance.getMultiplier() * GameTime.instance.getMinutesPerDay() / 1.6f);
                this.BodyDamage.OnFire(true);
            }
            if (this.isDead()) {
                IsoFireManager.RemoveBurningCharacter(this);
                if (this.isZombie()) {
                    LuaEventManager.triggerEvent("OnZombieDead", this);
                    if (GameClient.bClient) {
                        this.setAttackedBy(IsoWorld.instance.CurrentCell.getFakeZombieForHit());
                    }
                }
            }
        }
        if (this instanceof IsoPlayer && Rand.Next(Rand.AdjustForFramerate(((IsoPlayer)this).IsRunning() ? 150 : 400)) == 0) {
            this.StopBurning();
        }
    }
    
    public void DrawSneezeText() {
        if (this.BodyDamage.IsSneezingCoughing() > 0) {
            String s = null;
            if (this.BodyDamage.IsSneezingCoughing() == 1) {
                s = Translator.getText("IGUI_PlayerText_Sneeze");
            }
            if (this.BodyDamage.IsSneezingCoughing() == 2) {
                s = Translator.getText("IGUI_PlayerText_Cough");
            }
            if (this.BodyDamage.IsSneezingCoughing() == 3) {
                s = Translator.getText("IGUI_PlayerText_SneezeMuffled");
            }
            if (this.BodyDamage.IsSneezingCoughing() == 4) {
                s = Translator.getText("IGUI_PlayerText_CoughMuffled");
            }
            final float sx = this.sx;
            final float sy = this.sy;
            final float n = (float)(int)sx;
            final float n2 = (float)(int)sy;
            final float n3 = n - (int)IsoCamera.getOffX();
            final float n4 = n2 - (int)IsoCamera.getOffY() - 48.0f;
            if (s != null) {
                TextManager.instance.DrawStringCentre(UIFont.Dialogue, (int)n3, (int)n4, s, this.SpeakColour.r, this.SpeakColour.g, this.SpeakColour.b, this.SpeakColour.a);
            }
        }
    }
    
    public IsoSpriteInstance getSpriteDef() {
        if (this.def == null) {
            this.def = new IsoSpriteInstance();
        }
        return this.def;
    }
    
    public void render(final float n, final float n2, final float n3, final ColorInfo colorInfo, final boolean b, final boolean b2, final Shader shader) {
        if (this.isAlphaAndTargetZero()) {
            return;
        }
        if (this.isSeatedInVehicle() && !this.getVehicle().showPassenger(this)) {
            return;
        }
        if (this.isSpriteInvisible()) {
            return;
        }
        if (this.isAlphaZero()) {
            return;
        }
        if (!this.bUseParts && this.def == null) {
            this.def = new IsoSpriteInstance(this.sprite);
        }
        SpriteRenderer.instance.glDepthMask(true);
        if (this.bDoDefer && n3 - (int)n3 > 0.2f) {
            final IsoGridSquare gridSquare = this.getCell().getGridSquare((int)n, (int)n2, (int)n3 + 1);
            if (gridSquare != null) {
                gridSquare.addDeferredCharacter(this);
            }
        }
        final IsoGridSquare currentSquare = this.getCurrentSquare();
        if (PerformanceSettings.LightingFrameSkip < 3 && currentSquare != null) {
            currentSquare.interpolateLight(IsoGameCharacter.inf, n - currentSquare.getX(), n2 - currentSquare.getY());
        }
        else {
            IsoGameCharacter.inf.r = colorInfo.r;
            IsoGameCharacter.inf.g = colorInfo.g;
            IsoGameCharacter.inf.b = colorInfo.b;
            IsoGameCharacter.inf.a = colorInfo.a;
        }
        if (Core.bDebug && DebugOptions.instance.PathfindRenderWaiting.getValue() && this.hasActiveModel()) {
            if (this.getCurrentState() == PathFindState.instance() && this.finder.progress == AStarPathFinder.PathFindProgress.notyetfound) {
                this.legsSprite.modelSlot.model.tintR = 1.0f;
                this.legsSprite.modelSlot.model.tintG = 0.0f;
                this.legsSprite.modelSlot.model.tintB = 0.0f;
            }
            else {
                this.legsSprite.modelSlot.model.tintR = 1.0f;
                this.legsSprite.modelSlot.model.tintG = 1.0f;
                this.legsSprite.modelSlot.model.tintB = 1.0f;
            }
        }
        if (this.dir == IsoDirections.Max) {
            this.dir = IsoDirections.N;
        }
        if (this.sprite != null && !this.legsSprite.hasActiveModel()) {
            this.checkDrawWeaponPre(n, n2, n3, colorInfo);
        }
        IsoGameCharacter.lastRenderedRendered = IsoGameCharacter.lastRendered;
        IsoGameCharacter.lastRendered = this;
        if (this.bUpdateModelTextures && this.hasActiveModel()) {
            this.bUpdateModelTextures = false;
            (this.textureCreator = ModelInstanceTextureCreator.alloc()).init(this);
        }
        if (this.bUpdateEquippedTextures && this.hasActiveModel()) {
            this.bUpdateEquippedTextures = false;
            if (this.primaryHandModel != null && this.primaryHandModel.getTextureInitializer() != null) {
                this.primaryHandModel.getTextureInitializer().setDirty();
            }
            if (this.secondaryHandModel != null && this.secondaryHandModel.getTextureInitializer() != null) {
                this.secondaryHandModel.getTextureInitializer().setDirty();
            }
        }
        final float n4 = (float)Core.TileScale;
        final float n5 = this.offsetX + 1.0f * n4;
        final float n6 = this.offsetY + -89.0f * n4;
        if (this.sprite != null) {
            this.def.setScale(n4, n4);
            if (!this.bUseParts) {
                this.sprite.render(this.def, this, n, n2, n3, this.dir, n5, n6, IsoGameCharacter.inf, true);
            }
            else if (this.legsSprite.hasActiveModel()) {
                this.legsSprite.renderActiveModel();
            }
            else if (!this.renderTextureInsteadOfModel(n, n2)) {
                this.def.Flip = false;
                IsoGameCharacter.inf.r = 1.0f;
                IsoGameCharacter.inf.g = 1.0f;
                IsoGameCharacter.inf.b = 1.0f;
                IsoGameCharacter.inf.a = this.def.alpha * 0.4f;
                this.legsSprite.renderCurrentAnim(this.def, this, n, n2, n3, this.dir, n5, n6, IsoGameCharacter.inf, false, null);
            }
        }
        if (this.AttachedAnimSprite != null) {
            for (int i = 0; i < this.AttachedAnimSprite.size(); ++i) {
                final IsoSpriteInstance isoSpriteInstance = this.AttachedAnimSprite.get(i);
                isoSpriteInstance.update();
                final float a = IsoGameCharacter.inf.a;
                IsoGameCharacter.inf.a = isoSpriteInstance.alpha;
                isoSpriteInstance.SetTargetAlpha(this.getTargetAlpha());
                isoSpriteInstance.render(this, n, n2, n3, this.dir, n5, n6, IsoGameCharacter.inf);
                IsoGameCharacter.inf.a = a;
            }
        }
        for (int j = 0; j < this.inventory.Items.size(); ++j) {
            final InventoryItem inventoryItem = this.inventory.Items.get(j);
            if (inventoryItem instanceof IUpdater) {
                ((IUpdater)inventoryItem).render();
            }
        }
    }
    
    public void renderServerGUI() {
        if (this instanceof IsoPlayer) {
            this.setSceneCulled(false);
        }
        if (this.bUpdateModelTextures && this.hasActiveModel()) {
            this.bUpdateModelTextures = false;
            (this.textureCreator = ModelInstanceTextureCreator.alloc()).init(this);
        }
        final float n = (float)Core.TileScale;
        final float n2 = this.offsetX + 1.0f * n;
        final float n3 = this.offsetY + -89.0f * n;
        if (this.sprite != null) {
            this.def.setScale(n, n);
            IsoGameCharacter.inf.r = 1.0f;
            IsoGameCharacter.inf.g = 1.0f;
            IsoGameCharacter.inf.b = 1.0f;
            IsoGameCharacter.inf.a = this.def.alpha * 0.4f;
            if (!this.isbUseParts()) {
                this.sprite.render(this.def, this, this.x, this.y, this.z, this.dir, n2, n3, IsoGameCharacter.inf, true);
            }
            else {
                this.def.Flip = false;
                this.legsSprite.render(this.def, this, this.x, this.y, this.z, this.dir, n2, n3, IsoGameCharacter.inf, true);
            }
        }
        if (Core.bDebug && this.hasActiveModel()) {
            if (this instanceof IsoZombie) {
                final int n4 = (int)IsoUtils.XToScreenExact(this.x, this.y, this.z, 0);
                final int n5 = (int)IsoUtils.YToScreenExact(this.x, this.y, this.z, 0);
                TextManager.instance.DrawString((double)n4, (double)n5, invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, this.getOnlineID()));
                TextManager.instance.DrawString((double)n4, (double)(n5 + 10), invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getCurrentStateName()));
                TextManager.instance.DrawString((double)n4, (double)(n5 + 20), invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.getHealth()));
            }
            final Vector2 tempo = IsoGameCharacter.tempo;
            this.getDeferredMovement(tempo);
            this.drawDirectionLine(tempo, 1000.0f * tempo.getLength() / GameTime.instance.getMultiplier() * 2.0f, 1.0f, 0.5f, 0.5f);
        }
    }
    
    protected float getAlphaUpdateRateMul() {
        float alphaUpdateRateMul = super.getAlphaUpdateRateMul();
        if (IsoCamera.CamCharacter.Traits.ShortSighted.isSet()) {
            alphaUpdateRateMul /= 2.0f;
        }
        if (IsoCamera.CamCharacter.Traits.EagleEyed.isSet()) {
            alphaUpdateRateMul *= 1.5f;
        }
        return alphaUpdateRateMul;
    }
    
    protected boolean isUpdateAlphaEnabled() {
        return !this.isTeleporting();
    }
    
    protected boolean isUpdateAlphaDuringRender() {
        return false;
    }
    
    public boolean isSeatedInVehicle() {
        return this.vehicle != null && this.vehicle.getSeat(this) != -1;
    }
    
    public void renderObjectPicker(final float n, final float n2, final float n3, final ColorInfo colorInfo) {
        if (!this.bUseParts) {
            this.sprite.renderObjectPicker(this.def, this, this.dir);
        }
        else {
            this.legsSprite.renderObjectPicker(this.def, this, this.dir);
        }
    }
    
    static Vector2 closestpointonline(final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final Vector2 vector2) {
        final double n7 = n4 - n2;
        final double n8 = n - n3;
        final double n9 = (n4 - n2) * n + (n - n3) * n2;
        final double n10 = -n8 * n5 + n7 * n6;
        final double n11 = n7 * n7 - -n8 * n8;
        double n12;
        double n13;
        if (n11 != 0.0) {
            n12 = (n7 * n9 - n8 * n10) / n11;
            n13 = (n7 * n10 - -n8 * n9) / n11;
        }
        else {
            n12 = n5;
            n13 = n6;
        }
        return vector2.set((float)n12, (float)n13);
    }
    
    public void renderShadow(final float n, final float n2, final float n3) {
        if (!this.doRenderShadow) {
            return;
        }
        if (this.isAlphaAndTargetZero()) {
            return;
        }
        if (this.isSeatedInVehicle()) {
            return;
        }
        final IsoGridSquare currentSquare = this.getCurrentSquare();
        if (currentSquare == null) {
            return;
        }
        final int playerIndex = IsoCamera.frameState.playerIndex;
        final Vector3f forward = L_renderShadow.forward;
        final Vector2 animVector = this.getAnimVector(IsoGameCharacter.tempo2);
        forward.set(animVector.x, animVector.y, 0.0f);
        final float n4 = 0.45f;
        float shadowFM = 1.4f;
        float shadowBM = 1.125f;
        float alpha = this.getAlpha(playerIndex);
        if (this.hasActiveModel() && this.hasAnimationPlayer() && this.getAnimationPlayer().isReady()) {
            final AnimationPlayer animationPlayer = this.getAnimationPlayer();
            final Vector3 v1 = L_renderShadow.v1;
            Model.BoneToWorldCoords(this, animationPlayer.getSkinningBoneIndex("Bip01_Head", -1), v1);
            final float x = v1.x;
            final float y = v1.y;
            Model.BoneToWorldCoords(this, animationPlayer.getSkinningBoneIndex("Bip01_L_Foot", -1), v1);
            final float x2 = v1.x;
            final float y2 = v1.y;
            Model.BoneToWorldCoords(this, animationPlayer.getSkinningBoneIndex("Bip01_R_Foot", -1), v1);
            final float x3 = v1.x;
            final float y3 = v1.y;
            final Vector3f v2 = L_renderShadow.v3;
            float a = 0.0f;
            float a2 = 0.0f;
            final Vector2 closestpointonline = closestpointonline(n, n2, n + forward.x, n2 + forward.y, x, y, IsoGameCharacter.tempo);
            final float x4 = closestpointonline.x;
            final float y4 = closestpointonline.y;
            final float length = closestpointonline.set(x4 - n, y4 - n2).getLength();
            if (length > 0.001f) {
                v2.set(x4 - n, y4 - n2, 0.0f).normalize();
                if (forward.dot((Vector3fc)v2) > 0.0f) {
                    a = Math.max(a, length);
                }
                else {
                    a2 = Math.max(a2, length);
                }
            }
            final Vector2 closestpointonline2 = closestpointonline(n, n2, n + forward.x, n2 + forward.y, x2, y2, IsoGameCharacter.tempo);
            final float x5 = closestpointonline2.x;
            final float y5 = closestpointonline2.y;
            final float length2 = closestpointonline2.set(x5 - n, y5 - n2).getLength();
            if (length2 > 0.001f) {
                v2.set(x5 - n, y5 - n2, 0.0f).normalize();
                if (forward.dot((Vector3fc)v2) > 0.0f) {
                    a = Math.max(a, length2);
                }
                else {
                    a2 = Math.max(a2, length2);
                }
            }
            final Vector2 closestpointonline3 = closestpointonline(n, n2, n + forward.x, n2 + forward.y, x3, y3, IsoGameCharacter.tempo);
            final float x6 = closestpointonline3.x;
            final float y6 = closestpointonline3.y;
            final float length3 = closestpointonline3.set(x6 - n, y6 - n2).getLength();
            if (length3 > 0.001f) {
                v2.set(x6 - n, y6 - n2, 0.0f).normalize();
                if (forward.dot((Vector3fc)v2) > 0.0f) {
                    a = Math.max(a, length3);
                }
                else {
                    a2 = Math.max(a2, length3);
                }
            }
            final float shadowFM2 = (a + 0.35f) * 1.35f;
            final float shadowBM2 = (a2 + 0.35f) * 1.35f;
            final float clamp = PZMath.clamp(0.1f * (GameTime.getInstance().getMultiplier() / 1.6f), 0.0f, 1.0f);
            if (this.shadowTick != IngameState.instance.numberTicks - 1L) {
                this.m_shadowFM = shadowFM2;
                this.m_shadowBM = shadowBM2;
            }
            this.shadowTick = IngameState.instance.numberTicks;
            this.m_shadowFM = PZMath.lerp(this.m_shadowFM, shadowFM2, clamp);
            shadowFM = this.m_shadowFM;
            this.m_shadowBM = PZMath.lerp(this.m_shadowBM, shadowBM2, clamp);
            shadowBM = this.m_shadowBM;
        }
        else if (this.isZombie() && this.isCurrentState(FakeDeadZombieState.instance())) {
            alpha = 1.0f;
        }
        else if (this.isSceneCulled()) {
            return;
        }
        IsoDeadBody.renderShadow(n, n2, n3, forward, n4, shadowFM, shadowBM, currentSquare.lighting[playerIndex].lightInfo(), alpha);
    }
    
    public boolean isMaskClicked(final int n, final int n2, final boolean b) {
        if (this.sprite == null) {
            return false;
        }
        if (!this.bUseParts) {
            return super.isMaskClicked(n, n2, b);
        }
        return this.legsSprite.isMaskClicked(this.dir, n, n2, b);
    }
    
    public void setHaloNote(final String s) {
        this.setHaloNote(s, this.haloDispTime);
    }
    
    public void setHaloNote(final String s, final float n) {
        this.setHaloNote(s, 0, 255, 0, n);
    }
    
    public void setHaloNote(final String s, final int n, final int n2, final int n3, final float haloDispTime) {
        if (this.haloNote != null && s != null) {
            this.haloDispTime = haloDispTime;
            this.haloNote.setDefaultColors(n, n2, n3);
            this.haloNote.ReadString(s);
            this.haloNote.setInternalTickClock(this.haloDispTime);
        }
    }
    
    public float getHaloTimerCount() {
        if (this.haloNote != null) {
            return this.haloNote.getInternalClock();
        }
        return 0.0f;
    }
    
    public void DoSneezeText() {
        if (this.BodyDamage == null) {
            return;
        }
        if (this.BodyDamage.IsSneezingCoughing() > 0) {
            String s = null;
            if (this.BodyDamage.IsSneezingCoughing() == 1) {
                s = Translator.getText("IGUI_PlayerText_Sneeze");
                this.setVariable("Ext", invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Rand.Next(2) + 1));
            }
            if (this.BodyDamage.IsSneezingCoughing() == 2) {
                s = Translator.getText("IGUI_PlayerText_Cough");
                this.setVariable("Ext", "Cough");
            }
            if (this.BodyDamage.IsSneezingCoughing() == 3) {
                s = Translator.getText("IGUI_PlayerText_SneezeMuffled");
                this.setVariable("Ext", invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Rand.Next(2) + 1));
            }
            if (this.BodyDamage.IsSneezingCoughing() == 4) {
                s = Translator.getText("IGUI_PlayerText_CoughMuffled");
                this.setVariable("Ext", "Cough");
            }
            if (s != null) {
                this.Say(s);
                this.reportEvent("EventDoExt");
            }
        }
    }
    
    @Override
    public String getSayLine() {
        return this.chatElement.getSayLine();
    }
    
    public void setSayLine(final String s) {
        this.Say(s);
    }
    
    public ChatMessage getLastChatMessage() {
        return this.lastChatMessage;
    }
    
    public void setLastChatMessage(final ChatMessage lastChatMessage) {
        this.lastChatMessage = lastChatMessage;
    }
    
    public String getLastSpokenLine() {
        return this.lastSpokenLine;
    }
    
    public void setLastSpokenLine(final String lastSpokenLine) {
        this.lastSpokenLine = lastSpokenLine;
    }
    
    protected void doSleepSpeech() {
        ++this.sleepSpeechCnt;
        if (this.sleepSpeechCnt > 250 * PerformanceSettings.getLockFPS() / 30.0f) {
            this.sleepSpeechCnt = 0;
            if (IsoGameCharacter.sleepText == null) {
                ChatElement.addNoLogText(IsoGameCharacter.sleepText = "ZzzZZZzzzz");
            }
            this.SayWhisper(IsoGameCharacter.sleepText);
        }
    }
    
    public void SayDebug(final String s) {
        this.chatElement.SayDebug(0, s);
    }
    
    public void SayDebug(final int n, final String s) {
        this.chatElement.SayDebug(n, s);
    }
    
    public int getMaxChatLines() {
        return this.chatElement.getMaxChatLines();
    }
    
    @Override
    public void Say(final String s) {
        if (this.isZombie()) {
            return;
        }
        this.ProcessSay(s, this.SpeakColour.r, this.SpeakColour.g, this.SpeakColour.b, 30.0f, "default");
    }
    
    public void Say(final String s, final float n, final float n2, final float n3, final UIFont uiFont, final float n4, final String s2) {
        this.ProcessSay(s, n, n2, n3, n4, s2);
    }
    
    public void SayWhisper(final String s) {
        this.ProcessSay(s, this.SpeakColour.r, this.SpeakColour.g, this.SpeakColour.b, 10.0f, "whisper");
    }
    
    public void SayShout(final String s) {
        this.ProcessSay(s, this.SpeakColour.r, this.SpeakColour.g, this.SpeakColour.b, 60.0f, "shout");
    }
    
    private void ProcessSay(String filterString, final float n, final float n2, final float n3, final float n4, final String s) {
        if (!this.AllowConversation) {
            return;
        }
        if (TutorialManager.instance.ProfanityFilter) {
            filterString = ProfanityFilter.getInstance().filterString(filterString);
        }
        if (s.equals("default")) {
            ChatManager.getInstance().showInfoMessage(((IsoPlayer)this).getUsername(), filterString);
            this.lastSpokenLine = filterString;
        }
        else if (s.equals("whisper")) {
            this.lastSpokenLine = filterString;
        }
        else if (s.equals("shout")) {
            ChatManager.getInstance().sendMessageToChat(((IsoPlayer)this).getUsername(), ChatType.shout, filterString);
            this.lastSpokenLine = filterString;
        }
        else if (s.equals("radio")) {
            this.chatElement.addChatLine(filterString, n, n2, n3, UIFont.Medium, n4, s, true, true, true, false, false, true);
            if (ZomboidRadio.isStaticSound(filterString)) {
                ChatManager.getInstance().showStaticRadioSound(filterString);
            }
            else {
                ChatManager.getInstance().showRadioMessage(filterString);
            }
        }
    }
    
    public void addLineChatElement(final String s) {
        this.addLineChatElement(s, 1.0f, 1.0f, 1.0f);
    }
    
    public void addLineChatElement(final String s, final float n, final float n2, final float n3) {
        this.addLineChatElement(s, n, n2, n3, UIFont.Dialogue, 30.0f, "default");
    }
    
    public void addLineChatElement(final String s, final float n, final float n2, final float n3, final UIFont uiFont, final float n4, final String s2) {
        this.addLineChatElement(s, n, n2, n3, uiFont, n4, s2, false, false, false, false, false, true);
    }
    
    public void addLineChatElement(final String s, final float n, final float n2, final float n3, final UIFont uiFont, final float n4, final String s2, final boolean b, final boolean b2, final boolean b3, final boolean b4, final boolean b5, final boolean b6) {
        this.chatElement.addChatLine(s, n, n2, n3, uiFont, n4, s2, b, b2, b3, b4, b5, b6);
    }
    
    protected boolean playerIsSelf() {
        return IsoPlayer.getInstance() == this;
    }
    
    public int getUserNameHeight() {
        if (!GameClient.bClient) {
            return 0;
        }
        if (this.userName != null) {
            return this.userName.getHeight();
        }
        return 0;
    }
    
    protected void initTextObjects() {
        this.hasInitTextObjects = true;
        if (this instanceof IsoPlayer) {
            this.chatElement.setMaxChatLines(5);
            if (IsoPlayer.getInstance() != null) {
                System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, IsoPlayer.getInstance().username));
            }
            this.isoPlayer = (IsoPlayer)this;
            if (this.isoPlayer.username != null) {
                (this.userName = new TextDrawObject()).setAllowAnyImage(true);
                this.userName.setDefaultFont(UIFont.Small);
                this.userName.setDefaultColors(255, 255, 255, 255);
                this.updateUserName();
            }
            if (this.haloNote == null) {
                (this.haloNote = new TextDrawObject()).setDefaultFont(UIFont.Small);
                this.haloNote.setDefaultColors(0, 255, 0);
                this.haloNote.setDrawBackground(true);
                this.haloNote.setAllowImages(true);
                this.haloNote.setAllowAnyImage(true);
                this.haloNote.setOutlineColors(0.0f, 0.0f, 0.0f, 0.33f);
            }
        }
    }
    
    protected void updateUserName() {
        if (this.userName != null && this.isoPlayer != null) {
            String username = this.isoPlayer.getUsername(true);
            if (this != IsoPlayer.getInstance() && this.isInvisible() && IsoPlayer.getInstance() != null && IsoPlayer.getInstance().accessLevel.equals("") && (!Core.bDebug || !DebugOptions.instance.CheatPlayerSeeEveryone.getValue())) {
                this.userName.ReadString("");
                return;
            }
            final Faction playerFaction = Faction.getPlayerFaction(this.isoPlayer);
            if (playerFaction != null) {
                if (this.isoPlayer.showTag || this.isoPlayer == IsoPlayer.getInstance() || Faction.getPlayerFaction(IsoPlayer.getInstance()) == playerFaction) {
                    this.isoPlayer.tagPrefix = playerFaction.getTag();
                    if (playerFaction.getTagColor() != null) {
                        this.isoPlayer.setTagColor(playerFaction.getTagColor());
                    }
                }
                else {
                    this.isoPlayer.tagPrefix = "";
                }
            }
            else {
                this.isoPlayer.tagPrefix = "";
            }
            boolean b = (this.isoPlayer != null && this.isoPlayer.bRemote) || Core.getInstance().isShowYourUsername();
            final boolean b2 = IsoCamera.CamCharacter instanceof IsoPlayer && !((IsoPlayer)IsoCamera.CamCharacter).accessLevel.equals("");
            final boolean b3 = IsoCamera.CamCharacter instanceof IsoPlayer && ((IsoPlayer)IsoCamera.CamCharacter).canSeeAll;
            if (!ServerOptions.instance.DisplayUserName.getValue() && !b3) {
                b = false;
            }
            if (!b) {
                username = "";
            }
            if (b && this.isoPlayer.tagPrefix != null && !this.isoPlayer.tagPrefix.equals("")) {
                username = invokedynamic(makeConcatWithConstants:(IIILjava/lang/String;Ljava/lang/String;)Ljava/lang/String;, new Float(this.isoPlayer.getTagColor().r * 255.0f).intValue(), new Float(this.isoPlayer.getTagColor().g * 255.0f).intValue(), new Float(this.isoPlayer.getTagColor().b * 255.0f).intValue(), this.isoPlayer.tagPrefix, username);
            }
            if (b && !this.isoPlayer.accessLevel.equals("") && this.isoPlayer.isShowAdminTag()) {
                username = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, (String)this.namesPrefix.get(this.isoPlayer.accessLevel), username);
            }
            if (b && !this.isoPlayer.isSafety() && ServerOptions.instance.ShowSafety.getValue()) {
                username = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, username);
            }
            if (this.isoPlayer.isSpeek && !this.isoPlayer.isVoiceMute) {
                username = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, username);
            }
            if (this.isoPlayer.isVoiceMute) {
                username = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, username);
            }
            final BaseVehicle baseVehicle = (IsoCamera.CamCharacter == this.isoPlayer) ? this.isoPlayer.getNearVehicle() : null;
            if (this.getVehicle() == null && baseVehicle != null && (this.isoPlayer.getInventory().haveThisKeyId(baseVehicle.getKeyId()) != null || baseVehicle.isHotwired() || SandboxOptions.getInstance().VehicleEasyUse.getValue())) {
                final Color hsBtoRGB = Color.HSBtoRGB(baseVehicle.colorHue, baseVehicle.colorSaturation * 0.5f, baseVehicle.colorValue);
                username = invokedynamic(makeConcatWithConstants:(IIILjava/lang/String;)Ljava/lang/String;, hsBtoRGB.getRedByte(), hsBtoRGB.getGreenByte(), hsBtoRGB.getBlueByte(), username);
            }
            if (!username.equals(this.userName.getOriginal())) {
                this.userName.ReadString(username);
            }
        }
    }
    
    public void updateTextObjects() {
        if (GameServer.bServer) {
            return;
        }
        if (!this.hasInitTextObjects) {
            this.initTextObjects();
        }
        if (!this.Speaking) {
            this.DoSneezeText();
            if (this.isAsleep() && this.getCurrentSquare() != null && this.getCurrentSquare().getCanSee(0)) {
                this.doSleepSpeech();
            }
        }
        if (this.isoPlayer != null) {
            this.radioEquipedCheck();
        }
        this.Speaking = false;
        this.drawUserName = false;
        this.canSeeCurrent = false;
        if (this.haloNote != null && this.haloNote.getInternalClock() > 0.0f) {
            this.haloNote.updateInternalTickClock();
        }
        this.legsSprite.PlayAnim("ZombieWalk1");
        this.chatElement.update();
        this.Speaking = this.chatElement.IsSpeaking();
        if (!this.Speaking || this.isDead()) {
            this.Speaking = false;
            this.callOut = false;
        }
    }
    
    @Override
    public void renderlast() {
        super.renderlast();
        final int playerIndex = IsoCamera.frameState.playerIndex;
        final float x = this.x;
        final float y = this.y;
        if (this.sx == 0.0f && this.def != null) {
            this.sx = IsoUtils.XToScreen(x + this.def.offX, y + this.def.offY, this.z + this.def.offZ, 0);
            this.sy = IsoUtils.YToScreen(x + this.def.offX, y + this.def.offY, this.z + this.def.offZ, 0);
            this.sx -= this.offsetX - 8.0f;
            this.sy -= this.offsetY - 60.0f;
        }
        if ((this.hasInitTextObjects && this.isoPlayer != null) || this.chatElement.getHasChatToDisplay()) {
            final float xToScreen = IsoUtils.XToScreen(x, y, this.getZ(), 0);
            final float yToScreen = IsoUtils.YToScreen(x, y, this.getZ(), 0);
            final float n = xToScreen - IsoCamera.getOffX() - this.offsetX;
            final float n2 = yToScreen - IsoCamera.getOffY() - this.offsetY - 128 / (2 / Core.TileScale);
            final float zoom = Core.getInstance().getZoom(playerIndex);
            final float n3 = n / zoom;
            float n4 = n2 / zoom;
            this.canSeeCurrent = true;
            this.drawUserName = false;
            if ((this.isoPlayer != null && (this == IsoCamera.frameState.CamCharacter || (this.getCurrentSquare() != null && this.getCurrentSquare().getCanSee(playerIndex)))) || IsoPlayer.getInstance().isCanSeeAll()) {
                if (this == IsoPlayer.getInstance()) {
                    this.canSeeCurrent = true;
                }
                if (GameClient.bClient && this.userName != null && this.doRenderShadow) {
                    this.drawUserName = false;
                    if (ServerOptions.getInstance().MouseOverToSeeDisplayName.getValue() && this != IsoPlayer.getInstance() && !IsoPlayer.getInstance().isCanSeeAll()) {
                        final IsoObjectPicker.ClickObject contextPick = IsoObjectPicker.Instance.ContextPick(Mouse.getXA(), Mouse.getYA());
                        if (contextPick != null && contextPick.tile != null) {
                            for (int i = contextPick.tile.square.getX() - 1; i < contextPick.tile.square.getX() + 2; ++i) {
                                for (int j = contextPick.tile.square.getY() - 1; j < contextPick.tile.square.getY() + 2; ++j) {
                                    final IsoGridSquare gridSquare = IsoCell.getInstance().getGridSquare(i, j, contextPick.tile.square.getZ());
                                    if (gridSquare != null) {
                                        for (int k = 0; k < gridSquare.getMovingObjects().size(); ++k) {
                                            final IsoMovingObject isoMovingObject = gridSquare.getMovingObjects().get(k);
                                            if (isoMovingObject instanceof IsoPlayer && this == isoMovingObject) {
                                                this.drawUserName = true;
                                                break;
                                            }
                                        }
                                        if (this.drawUserName) {
                                            break;
                                        }
                                    }
                                    if (this.drawUserName) {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    else {
                        this.drawUserName = true;
                    }
                    if (this.drawUserName) {
                        this.updateUserName();
                    }
                }
                if (!GameClient.bClient && this.isoPlayer != null && this.isoPlayer.getVehicle() == null) {
                    String s = "";
                    final BaseVehicle nearVehicle = this.isoPlayer.getNearVehicle();
                    if (this.getVehicle() == null && nearVehicle != null && nearVehicle.getPartById("Engine") != null && (this.isoPlayer.getInventory().haveThisKeyId(nearVehicle.getKeyId()) != null || nearVehicle.isHotwired() || SandboxOptions.getInstance().VehicleEasyUse.getValue()) && UIManager.VisibleAllUI) {
                        final Color hsBtoRGB = Color.HSBtoRGB(nearVehicle.colorHue, nearVehicle.colorSaturation * 0.5f, nearVehicle.colorValue, L_renderLast.color);
                        s = invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, hsBtoRGB.getRedByte(), hsBtoRGB.getGreenByte(), hsBtoRGB.getBlueByte());
                    }
                    if (!s.equals("")) {
                        this.userName.ReadString(s);
                        this.drawUserName = true;
                    }
                }
            }
            if (this.isoPlayer != null && this.hasInitTextObjects && (this.playerIsSelf() || this.canSeeCurrent)) {
                if (this.canSeeCurrent && this.drawUserName) {
                    n4 -= this.userName.getHeight();
                    this.userName.AddBatchedDraw((int)n3, (int)n4, true);
                }
                if (this.playerIsSelf()) {
                    final ActionProgressBar progressBar = UIManager.getProgressBar(playerIndex);
                    if (progressBar != null && progressBar.isVisible()) {
                        n4 -= progressBar.getHeight().intValue() + 2;
                    }
                }
                if (this.playerIsSelf() && this.haloNote != null && this.haloNote.getInternalClock() > 0.0f) {
                    final float min = PZMath.min(this.haloNote.getInternalClock() / (this.haloDispTime / 4.0f), 1.0f);
                    n4 -= this.haloNote.getHeight() + 2;
                    this.haloNote.AddBatchedDraw((int)n3, (int)n4, true, min);
                }
            }
            boolean b = false;
            if (IsoPlayer.getInstance() != this && this.equipedRadio != null && this.equipedRadio.getDeviceData() != null && this.equipedRadio.getDeviceData().getHeadphoneType() >= 0) {
                b = true;
            }
            if (this.equipedRadio != null && this.equipedRadio.getDeviceData() != null && !this.equipedRadio.getDeviceData().getIsTurnedOn()) {
                b = true;
            }
            final boolean b2 = GameClient.bClient && IsoCamera.CamCharacter instanceof IsoPlayer && !((IsoPlayer)IsoCamera.CamCharacter).accessLevel.equals("");
            if (!this.m_invisible || this == IsoCamera.frameState.CamCharacter || b2) {
                this.chatElement.renderBatched(IsoPlayer.getPlayerIndex(), (int)n3, (int)n4, b);
            }
        }
        if (Core.bDebug && DebugOptions.instance.Character.Debug.Render.Angle.getValue() && this.hasActiveModel()) {
            final Vector2 tempo = IsoGameCharacter.tempo;
            this.getAnimationPlayer();
            tempo.set(this.dir.ToVector());
            this.drawDirectionLine(tempo, 2.4f, 0.0f, 1.0f, 0.0f);
            tempo.setLengthAndDirection(this.getLookAngleRadians(), 1.0f);
            this.drawDirectionLine(tempo, 2.0f, 1.0f, 1.0f, 1.0f);
            tempo.setLengthAndDirection(this.getAnimAngleRadians(), 1.0f);
            this.drawDirectionLine(tempo, 2.0f, 1.0f, 1.0f, 0.0f);
            tempo.setLengthAndDirection(this.getForwardDirection().getDirection(), 1.0f);
            this.drawDirectionLine(tempo, 2.0f, 0.0f, 0.0f, 1.0f);
        }
        if (Core.bDebug && DebugOptions.instance.Character.Debug.Render.DeferredMovement.getValue() && this.hasActiveModel()) {
            final Vector2 tempo2 = IsoGameCharacter.tempo;
            this.getAnimationPlayer();
            this.getDeferredMovement(tempo2);
            this.drawDirectionLine(tempo2, 1000.0f * tempo2.getLength() / GameTime.instance.getMultiplier() * 2.0f, 1.0f, 0.5f, 0.5f);
        }
        if (Core.bDebug && DebugOptions.instance.Character.Debug.Render.DeferredAngles.getValue() && this.hasActiveModel()) {
            final Vector2 tempo3 = IsoGameCharacter.tempo;
            this.getAnimationPlayer();
            this.getDeferredMovement(tempo3);
            this.drawDirectionLine(tempo3, 1000.0f * tempo3.getLength() / GameTime.instance.getMultiplier() * 2.0f, 1.0f, 0.5f, 0.5f);
        }
        if (Core.bDebug && DebugOptions.instance.Character.Debug.Render.AimCone.getValue()) {
            this.debugAim();
        }
        if (Core.bDebug && DebugOptions.instance.Character.Debug.Render.TestDotSide.getValue()) {
            this.debugTestDotSide();
        }
        if (Core.bDebug && DebugOptions.instance.Character.Debug.Render.Vision.getValue()) {
            this.debugVision();
        }
        if (Core.bDebug) {
            if (DebugOptions.instance.MultiplayerShowZombieMultiplier.getValue() && this instanceof IsoZombie) {
                final IsoZombie isoZombie = (IsoZombie)this;
                final byte canHaveMultipleHits = isoZombie.canHaveMultipleHits();
                Color color;
                if (canHaveMultipleHits == 0) {
                    color = Colors.Green;
                }
                else if (canHaveMultipleHits == 1) {
                    color = Colors.Yellow;
                }
                else {
                    color = Colors.Red;
                }
                LineDrawer.DrawIsoCircle(this.x, this.y, this.z, 0.45f, 4, color.r, color.g, color.b, 0.5f);
                TextManager.instance.DrawStringCentre(UIFont.DebugConsole, IsoUtils.XToScreenExact(this.x + 0.4f, this.y + 0.4f, this.z, 0), IsoUtils.YToScreenExact(this.x + 0.4f, this.y - 1.4f, this.z, 0), String.valueOf(isoZombie.OnlineID), color.r, color.g, color.b, color.a);
            }
            if (DebugOptions.instance.MultiplayerShowZombieOwner.getValue() && this instanceof IsoZombie) {
                final IsoZombie isoZombie2 = (IsoZombie)this;
                Color color2;
                if (isoZombie2.isDead()) {
                    color2 = Colors.Yellow;
                }
                else if (isoZombie2.isRemoteZombie()) {
                    color2 = Colors.OrangeRed;
                }
                else {
                    color2 = Colors.Chartreuse;
                }
                LineDrawer.DrawIsoCircle(this.x, this.y, this.z, 0.45f, 4, color2.r, color2.g, color2.b, 0.5f);
                TextManager.instance.DrawStringCentre(UIFont.DebugConsole, IsoUtils.XToScreenExact(this.x + 0.4f, this.y + 0.4f, this.z, 0), IsoUtils.YToScreenExact(this.x + 0.4f, this.y - 1.4f, this.z, 0), String.valueOf(isoZombie2.OnlineID), color2.r, color2.g, color2.b, color2.a);
            }
            if (DebugOptions.instance.MultiplayerShowZombiePrediction.getValue() && this instanceof IsoZombie) {
                final IsoZombie isoZombie3 = (IsoZombie)this;
                LineDrawer.DrawIsoTransform(this.realx, this.realy, this.z, this.realdir.ToVector().x, this.realdir.ToVector().y, 0.35f, 16, Colors.Blue.r, Colors.Blue.g, Colors.Blue.b, 0.35f, 1);
                if (isoZombie3.networkAI.DebugInterfaceActive) {
                    LineDrawer.DrawIsoCircle(this.x, this.y, this.z, 0.4f, 4, 1.0f, 0.1f, 0.1f, 0.35f);
                }
                else if (!isoZombie3.isRemoteZombie()) {
                    LineDrawer.DrawIsoCircle(this.x, this.y, this.z, 0.3f, 3, Colors.Magenta.r, Colors.Magenta.g, Colors.Magenta.b, 0.35f);
                }
                else {
                    LineDrawer.DrawIsoCircle(this.x, this.y, this.z, 0.3f, 5, Colors.Magenta.r, Colors.Magenta.g, Colors.Magenta.b, 0.35f);
                }
                LineDrawer.DrawIsoTransform(isoZombie3.networkAI.targetX, isoZombie3.networkAI.targetY, this.z, 1.0f, 0.0f, 0.4f, 16, Colors.LimeGreen.r, Colors.LimeGreen.g, Colors.LimeGreen.b, 0.35f, 1);
                LineDrawer.DrawIsoLine(this.x, this.y, this.z, isoZombie3.networkAI.targetX, isoZombie3.networkAI.targetY, this.z, Colors.LimeGreen.r, Colors.LimeGreen.g, Colors.LimeGreen.b, 0.35f, 1);
                if (IsoUtils.DistanceToSquared(this.x, this.y, this.realx, this.realy) > 4.5f) {
                    LineDrawer.DrawIsoLine(this.realx, this.realy, this.z, this.x, this.y, this.z, Colors.Magenta.r, Colors.Magenta.g, Colors.Magenta.b, 0.35f, 1);
                }
                else {
                    LineDrawer.DrawIsoLine(this.realx, this.realy, this.z, this.x, this.y, this.z, Colors.Blue.r, Colors.Blue.g, Colors.Blue.b, 0.35f, 1);
                }
            }
            if (DebugOptions.instance.MultiplayerShowZombieDesync.getValue() && this instanceof IsoZombie) {
                final IsoZombie isoZombie4 = (IsoZombie)this;
                final float distanceTo = IsoUtils.DistanceTo(this.getX(), this.getY(), this.realx, this.realy);
                if (isoZombie4.isRemoteZombie() && distanceTo > 1.0f) {
                    LineDrawer.DrawIsoLine(this.realx, this.realy, this.z, this.x, this.y, this.z, Colors.Blue.r, Colors.Blue.g, Colors.Blue.b, 0.9f, 1);
                    LineDrawer.DrawIsoTransform(this.realx, this.realy, this.z, this.realdir.ToVector().x, this.realdir.ToVector().y, 0.35f, 16, Colors.Blue.r, Colors.Blue.g, Colors.Blue.b, 0.9f, 1);
                    LineDrawer.DrawIsoCircle(this.x, this.y, this.z, 0.4f, 4, 1.0f, 1.0f, 1.0f, 0.9f);
                    TextManager.instance.DrawStringCentre(UIFont.DebugConsole, IsoUtils.XToScreenExact(this.x, this.y, this.z, 0), IsoUtils.YToScreenExact(this.x, this.y, this.z, 0), String.format("dist:%f scale1:%f", distanceTo, IsoUtils.DistanceTo(this.x, this.y, isoZombie4.networkAI.targetX, isoZombie4.networkAI.targetY) / IsoUtils.DistanceTo(this.realx, this.realy, isoZombie4.networkAI.targetX, isoZombie4.networkAI.targetY)), Colors.NavajoWhite.r, Colors.NavajoWhite.g, Colors.NavajoWhite.b, 0.8999999761581421);
                }
            }
            if (DebugOptions.instance.MultiplayerShowHit.getValue() && this.getHitReactionNetworkAI() != null && this.getHitReactionNetworkAI().isSetup()) {
                LineDrawer.DrawIsoLine(this.x, this.y, this.z, this.x + this.getHitDir().getX(), this.y + this.getHitDir().getY(), this.z, Colors.BlueViolet.r, Colors.BlueViolet.g, Colors.BlueViolet.b, 0.8f, 1);
                LineDrawer.DrawIsoLine(this.getHitReactionNetworkAI().startPosition.x, this.getHitReactionNetworkAI().startPosition.y, this.z, this.getHitReactionNetworkAI().finalPosition.x, this.getHitReactionNetworkAI().finalPosition.y, this.z, Colors.Salmon.r, Colors.Salmon.g, Colors.Salmon.b, 0.8f, 1);
                LineDrawer.DrawIsoTransform(this.getHitReactionNetworkAI().startPosition.x, this.getHitReactionNetworkAI().startPosition.y, this.z, this.getHitReactionNetworkAI().startDirection.x, this.getHitReactionNetworkAI().startDirection.y, 0.4f, 16, Colors.Salmon.r - 0.2f, Colors.Salmon.g + 0.2f, Colors.Salmon.b, 0.8f, 1);
                LineDrawer.DrawIsoTransform(this.getHitReactionNetworkAI().finalPosition.x, this.getHitReactionNetworkAI().finalPosition.y, this.z, this.getHitReactionNetworkAI().finalDirection.x, this.getHitReactionNetworkAI().finalDirection.y, 0.4f, 16, Colors.Salmon.r, Colors.Salmon.g - 0.2f, Colors.Salmon.b, 0.8f, 1);
            }
            if (DebugOptions.instance.MultiplayerShowPlayerPrediction.getValue() && this instanceof IsoPlayer) {
                if (this.isoPlayer != null && this.isoPlayer.networkAI != null && this.isoPlayer.networkAI.footstepSoundRadius != 0) {
                    LineDrawer.DrawIsoCircle(this.x, this.y, this.z, this.isoPlayer.networkAI.footstepSoundRadius, 32, Colors.Violet.r, Colors.Violet.g, Colors.Violet.b, 0.5f);
                }
                if (this.isoPlayer != null && this.isoPlayer.bRemote) {
                    LineDrawer.DrawIsoCircle(this.x, this.y, this.z, 0.3f, 16, Colors.OrangeRed.r, Colors.OrangeRed.g, Colors.OrangeRed.b, 0.5f);
                    IsoGameCharacter.tempo.set(this.realdir.ToVector());
                    LineDrawer.DrawIsoTransform(this.realx, this.realy, this.z, IsoGameCharacter.tempo.x, IsoGameCharacter.tempo.y, 0.35f, 16, Colors.Blue.r, Colors.Blue.g, Colors.Blue.b, 0.5f, 1);
                    LineDrawer.DrawIsoLine(this.realx, this.realy, this.z, this.x, this.y, this.z, Colors.Blue.r, Colors.Blue.g, Colors.Blue.b, 0.5f, 1);
                    IsoGameCharacter.tempo.set(((IsoPlayer)this).networkAI.targetX, ((IsoPlayer)this).networkAI.targetY);
                    LineDrawer.DrawIsoTransform(IsoGameCharacter.tempo.x, IsoGameCharacter.tempo.y, this.z, 1.0f, 0.0f, 0.4f, 16, Colors.LimeGreen.r, Colors.LimeGreen.g, Colors.LimeGreen.b, 0.5f, 1);
                    LineDrawer.DrawIsoLine(this.x, this.y, this.z, IsoGameCharacter.tempo.x, IsoGameCharacter.tempo.y, this.z, Colors.LimeGreen.r, Colors.LimeGreen.g, Colors.LimeGreen.b, 0.5f, 1);
                }
            }
            if (DebugOptions.instance.MultiplayerShowTeleport.getValue() && this.getNetworkCharacterAI() != null) {
                final NetworkTeleport.NetworkTeleportDebug teleportDebug = this.getNetworkCharacterAI().getTeleportDebug();
                if (teleportDebug != null) {
                    LineDrawer.DrawIsoLine(teleportDebug.lx, teleportDebug.ly, teleportDebug.lz, teleportDebug.nx, teleportDebug.ny, teleportDebug.nz, Colors.NavajoWhite.r, Colors.NavajoWhite.g, Colors.NavajoWhite.b, 0.7f, 3);
                    LineDrawer.DrawIsoCircle(teleportDebug.nx, teleportDebug.ny, teleportDebug.nz, 0.2f, 16, Colors.NavajoWhite.r, Colors.NavajoWhite.g, Colors.NavajoWhite.b, 0.7f);
                    final float xToScreenExact = IsoUtils.XToScreenExact(teleportDebug.lx, teleportDebug.ly, teleportDebug.lz, 0);
                    final float yToScreenExact = IsoUtils.YToScreenExact(teleportDebug.lx, teleportDebug.ly, teleportDebug.lz, 0);
                    TextManager.instance.DrawStringCentre(UIFont.DebugConsole, xToScreenExact, yToScreenExact, String.format("%s id=%d", (this instanceof IsoPlayer) ? ((IsoPlayer)this).getUsername() : this.getClass().getSimpleName(), teleportDebug.id), Colors.NavajoWhite.r, Colors.NavajoWhite.g, Colors.NavajoWhite.b, 0.699999988079071);
                    TextManager.instance.DrawStringCentre(UIFont.DebugConsole, xToScreenExact, yToScreenExact + 10.0f, teleportDebug.type.name(), Colors.NavajoWhite.r, Colors.NavajoWhite.g, Colors.NavajoWhite.b, 0.699999988079071);
                }
            }
            else if (this.getNetworkCharacterAI() != null) {
                this.getNetworkCharacterAI().clearTeleportDebug();
            }
            if ((DebugOptions.instance.MultiplayerShowZombieStatus.getValue() && this instanceof IsoZombie) || (DebugOptions.instance.MultiplayerShowPlayerStatus.getValue() && this instanceof IsoPlayer && !this.isGodMod())) {
                final float xToScreenExact2 = IsoUtils.XToScreenExact(this.x, this.y, this.z, 0);
                final float yToScreenExact2 = IsoUtils.YToScreenExact(this.x, this.y, this.z, 0);
                final float n5 = 10.0f;
                final Color greenYellow = Colors.GreenYellow;
                final float n6;
                TextManager.instance.DrawStringCentre(UIFont.DebugConsole, xToScreenExact2, yToScreenExact2 + (n6 = n5 + 11.0f), String.format("%d: %.03f / %.03f", this.getOnlineID(), this.getHealth(), (this instanceof IsoZombie) ? 0.0f : this.getBodyDamage().getOverallBodyHealth()), greenYellow.r, greenYellow.g, greenYellow.b, greenYellow.a);
                float n7;
                TextManager.instance.DrawStringCentre(UIFont.DebugConsole, (double)xToScreenExact2, (double)(yToScreenExact2 + (n7 = n6 + 11.0f)), invokedynamic(makeConcatWithConstants:(FFF)Ljava/lang/String;, this.x, this.y, this.z), (double)greenYellow.r, (double)greenYellow.g, (double)greenYellow.b, (double)greenYellow.a);
                if (this instanceof IsoPlayer) {
                    final IsoPlayer isoPlayer = (IsoPlayer)this;
                    final Color navajoWhite = Colors.NavajoWhite;
                    final float n8;
                    TextManager.instance.DrawStringCentre(UIFont.DebugConsole, xToScreenExact2, yToScreenExact2 + (n8 = n7 + 18.0f), String.format("IdleSpeed: %s , targetDist: %s ", isoPlayer.getVariableString("IdleSpeed"), isoPlayer.getVariableString("targetDist")), navajoWhite.r, navajoWhite.g, navajoWhite.b, 1.0);
                    final float n9;
                    TextManager.instance.DrawStringCentre(UIFont.DebugConsole, xToScreenExact2, yToScreenExact2 + (n9 = n8 + 11.0f), String.format("WalkInjury: %s , WalkSpeed: %s", isoPlayer.getVariableString("WalkInjury"), isoPlayer.getVariableString("WalkSpeed")), navajoWhite.r, navajoWhite.g, navajoWhite.b, 1.0);
                    final float n10;
                    TextManager.instance.DrawStringCentre(UIFont.DebugConsole, xToScreenExact2, yToScreenExact2 + (n10 = n9 + 11.0f), String.format("DeltaX: %s , DeltaY: %s", isoPlayer.getVariableString("DeltaX"), isoPlayer.getVariableString("DeltaY")), navajoWhite.r, navajoWhite.g, navajoWhite.b, 1.0);
                    final float n11;
                    TextManager.instance.DrawStringCentre(UIFont.DebugConsole, xToScreenExact2, yToScreenExact2 + (n11 = n10 + 11.0f), String.format("AttackVariationX: %s , AttackVariationY: %s", isoPlayer.getVariableString("AttackVariationX"), isoPlayer.getVariableString("AttackVariationY")), navajoWhite.r, navajoWhite.g, navajoWhite.b, 1.0);
                    final float n12;
                    TextManager.instance.DrawStringCentre(UIFont.DebugConsole, xToScreenExact2, yToScreenExact2 + (n12 = n11 + 11.0f), String.format("autoShootVarX: %s , autoShootVarY: %s", isoPlayer.getVariableString("autoShootVarX"), isoPlayer.getVariableString("autoShootVarY")), navajoWhite.r, navajoWhite.g, navajoWhite.b, 1.0);
                    final float n13;
                    TextManager.instance.DrawStringCentre(UIFont.DebugConsole, xToScreenExact2, yToScreenExact2 + (n13 = n12 + 11.0f), String.format("recoilVarX: %s , recoilVarY: %s", isoPlayer.getVariableString("recoilVarX"), isoPlayer.getVariableString("recoilVarY")), navajoWhite.r, navajoWhite.g, navajoWhite.b, 1.0);
                    TextManager.instance.DrawStringCentre(UIFont.DebugConsole, xToScreenExact2, yToScreenExact2 + (n7 = n13 + 11.0f), String.format("ShoveAimX: %s , ShoveAimY: %s", isoPlayer.getVariableString("ShoveAimX"), isoPlayer.getVariableString("ShoveAimY")), navajoWhite.r, navajoWhite.g, navajoWhite.b, 1.0);
                }
                final Color yellow = Colors.Yellow;
                final float n14;
                TextManager.instance.DrawStringCentre(UIFont.DebugConsole, xToScreenExact2, yToScreenExact2 + (n14 = n7 + 18.0f), String.format("isHitFromBehind=%b/%b", this.isHitFromBehind(), this.getVariableBoolean("frombehind")), yellow.r, yellow.g, yellow.b, 1.0);
                final float n15;
                TextManager.instance.DrawStringCentre(UIFont.DebugConsole, xToScreenExact2, yToScreenExact2 + (n15 = n14 + 11.0f), String.format("bKnockedDown=%b/%b", this.isKnockedDown(), this.getVariableBoolean("bknockeddown")), yellow.r, yellow.g, yellow.b, 1.0);
                final float n16;
                TextManager.instance.DrawStringCentre(UIFont.DebugConsole, xToScreenExact2, yToScreenExact2 + (n16 = n15 + 11.0f), String.format("isFallOnFront=%b/%b", this.isFallOnFront(), this.getVariableBoolean("fallonfront")), yellow.r, yellow.g, yellow.b, 1.0);
                final float n17;
                TextManager.instance.DrawStringCentre(UIFont.DebugConsole, xToScreenExact2, yToScreenExact2 + (n17 = n16 + 11.0f), String.format("isOnFloor=%b/%b", this.isOnFloor(), this.getVariableBoolean("bonfloor")), yellow.r, yellow.g, yellow.b, 1.0);
                float n18;
                TextManager.instance.DrawStringCentre(UIFont.DebugConsole, xToScreenExact2, yToScreenExact2 + (n18 = n17 + 11.0f), String.format("isDead=%b/%b", this.isDead(), this.getVariableBoolean("bdead")), yellow.r, yellow.g, yellow.b, 1.0);
                if (this.advancedAnimator.getRootLayer() != null) {
                    final Color pink = Colors.Pink;
                    final float n19;
                    TextManager.instance.DrawStringCentre(UIFont.DebugConsole, (double)xToScreenExact2, (double)(yToScreenExact2 + (n19 = n18 + 18.0f)), invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.advancedAnimator.animSet.m_Name), (double)pink.r, (double)pink.g, (double)pink.b, 1.0);
                    final float n20;
                    TextManager.instance.DrawStringCentre(UIFont.DebugConsole, (double)xToScreenExact2, (double)(yToScreenExact2 + (n20 = n19 + 11.0f)), invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.advancedAnimator.getCurrentStateName()), (double)pink.r, (double)pink.g, (double)pink.b, 1.0);
                    TextManager.instance.DrawStringCentre(UIFont.DebugConsole, (double)xToScreenExact2, (double)(yToScreenExact2 + (n18 = n20 + 11.0f)), invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.advancedAnimator.getRootLayer().getDebugNodeName()), (double)pink.r, (double)pink.g, (double)pink.b, 1.0);
                }
                final Color lightBlue = Colors.LightBlue;
                final float n21;
                TextManager.instance.DrawStringCentre(UIFont.DebugConsole, xToScreenExact2, yToScreenExact2 + (n21 = n18 + 11.0f), String.format("Previous state: %s ( %s )", this.getPreviousStateName(), this.getPreviousActionContextStateName()), lightBlue.r, lightBlue.g, lightBlue.b, 1.0);
                final float n22;
                TextManager.instance.DrawStringCentre(UIFont.DebugConsole, xToScreenExact2, yToScreenExact2 + (n22 = n21 + 11.0f), String.format("Current state: %s ( %s )", this.getCurrentStateName(), this.getCurrentActionContextStateName()), lightBlue.r, lightBlue.g, lightBlue.b, 1.0);
                float n23;
                TextManager.instance.DrawStringCentre(UIFont.DebugConsole, xToScreenExact2, yToScreenExact2 + (n23 = n22 + 11.0f), String.format("Child state: %s", (this.getActionContext() != null && this.getActionContext().getChildStates() != null && this.getActionContext().getChildStates().size() > 0 && this.getActionContext().getChildStateAt(0) != null) ? this.getActionContext().getChildStateAt(0).getName() : "\"\""), lightBlue.r, lightBlue.g, lightBlue.b, 1.0);
                if (this.CharacterActions != null) {
                    TextManager.instance.DrawStringCentre(UIFont.DebugConsole, xToScreenExact2, yToScreenExact2 + (n23 += 11.0f), String.format("Character actions: %d", this.CharacterActions.size()), lightBlue.r, lightBlue.g, lightBlue.b, 1.0);
                    for (final BaseAction baseAction : this.CharacterActions) {
                        if (baseAction instanceof LuaTimedActionNew) {
                            TextManager.instance.DrawStringCentre(UIFont.DebugConsole, xToScreenExact2, yToScreenExact2 + (n23 += 11.0f), String.format("Action: %s", ((LuaTimedActionNew)baseAction).getMetaType()), lightBlue.r, lightBlue.g, lightBlue.b, 1.0);
                        }
                    }
                }
                if (this instanceof IsoZombie) {
                    final Color greenYellow2 = Colors.GreenYellow;
                    final IsoZombie isoZombie5 = (IsoZombie)this;
                    final float n24;
                    TextManager.instance.DrawStringCentre(UIFont.DebugConsole, (double)xToScreenExact2, (double)(yToScreenExact2 + (n24 = n23 + 18.0f)), invokedynamic(makeConcatWithConstants:(Lzombie/network/NetworkVariables$PredictionTypes;)Ljava/lang/String;, this.getNetworkCharacterAI().predictionType), (double)greenYellow2.r, (double)greenYellow2.g, (double)greenYellow2.b, 1.0);
                    final float n25;
                    TextManager.instance.DrawStringCentre(UIFont.DebugConsole, xToScreenExact2, yToScreenExact2 + (n25 = n24 + 11.0f), String.format("Real state: %s", isoZombie5.realState), greenYellow2.r, greenYellow2.g, greenYellow2.b, 1.0);
                    if (isoZombie5.target instanceof IsoPlayer) {
                        TextManager.instance.DrawStringCentre(UIFont.DebugConsole, (double)xToScreenExact2, (double)(yToScreenExact2 + (n25 + 11.0f)), invokedynamic(makeConcatWithConstants:(Ljava/lang/String;F)Ljava/lang/String;, ((IsoPlayer)isoZombie5.target).username, isoZombie5.vectorToTarget.getLength()), (double)greenYellow2.r, (double)greenYellow2.g, (double)greenYellow2.b, 1.0);
                    }
                    else {
                        TextManager.instance.DrawStringCentre(UIFont.DebugConsole, (double)xToScreenExact2, (double)(yToScreenExact2 + (n25 + 11.0f)), invokedynamic(makeConcatWithConstants:(Lzombie/iso/IsoMovingObject;F)Ljava/lang/String;, isoZombie5.target, isoZombie5.vectorToTarget.getLength()), (double)greenYellow2.r, (double)greenYellow2.g, (double)greenYellow2.b, 1.0);
                    }
                }
            }
        }
        if (this.inventory == null) {
            return;
        }
        for (int l = 0; l < this.inventory.Items.size(); ++l) {
            final InventoryItem inventoryItem = this.inventory.Items.get(l);
            if (inventoryItem instanceof IUpdater) {
                ((IUpdater)inventoryItem).renderlast();
            }
        }
        if (Core.bDebug && DebugOptions.instance.PathfindRenderPath.getValue() && this.pfb2 != null) {
            this.pfb2.render();
        }
        if (Core.bDebug && DebugOptions.instance.CollideWithObstaclesRenderRadius.getValue()) {
            final float n26 = 0.3f;
            float n27 = 1.0f;
            float n28 = 1.0f;
            float n29 = 1.0f;
            if (!this.isCollidable()) {
                n29 = 0.0f;
            }
            if ((int)this.z != (int)IsoCamera.frameState.CamCharacterZ) {
                n28 = (n27 = (n29 = 0.5f));
            }
            LineDrawer.DrawIsoCircle(this.x, this.y, this.z, n26, 16, n27, n28, n29, 1.0f);
        }
        if (DebugOptions.instance.Animation.Debug.getValue() && this.hasActiveModel()) {
            TextManager.instance.DrawString((int)IsoUtils.XToScreenExact(this.x, this.y, this.z, 0), (int)IsoUtils.YToScreenExact(this.x, this.y, this.z, 0), this.getAnimationDebug());
        }
        if (this.getIsNPC() && this.GameCharacterAIBrain != null) {
            this.GameCharacterAIBrain.renderlast();
        }
    }
    
    protected boolean renderTextureInsteadOfModel(final float n, final float n2) {
        return false;
    }
    
    public void drawDirectionLine(final Vector2 vector2, final float n, final float n2, final float n3, final float n4) {
        final float n5 = this.x + vector2.x * n;
        final float n6 = this.y + vector2.y * n;
        LineDrawer.drawLine(IsoUtils.XToScreenExact(this.x, this.y, this.z, 0), IsoUtils.YToScreenExact(this.x, this.y, this.z, 0), IsoUtils.XToScreenExact(n5, n6, this.z, 0), IsoUtils.YToScreenExact(n5, n6, this.z, 0), n2, n3, n4, 0.5f, 1);
    }
    
    public Radio getEquipedRadio() {
        return this.equipedRadio;
    }
    
    private void radioEquipedCheck() {
        if (this.leftHandItem != this.leftHandCache) {
            this.leftHandCache = this.leftHandItem;
            if (this.leftHandItem != null && (this.equipedRadio == null || this.equipedRadio != this.rightHandItem) && this.leftHandItem instanceof Radio) {
                this.equipedRadio = (Radio)this.leftHandItem;
            }
            else if (this.equipedRadio != null && this.equipedRadio != this.rightHandItem) {
                if (this.equipedRadio.getDeviceData() != null) {
                    this.equipedRadio.getDeviceData().cleanSoundsAndEmitter();
                }
                this.equipedRadio = null;
            }
        }
        if (this.rightHandItem != this.rightHandCache) {
            this.rightHandCache = this.rightHandItem;
            if (this.rightHandItem != null && this.rightHandItem instanceof Radio) {
                this.equipedRadio = (Radio)this.rightHandItem;
            }
            else if (this.equipedRadio != null && this.equipedRadio != this.leftHandItem) {
                if (this.equipedRadio.getDeviceData() != null) {
                    this.equipedRadio.getDeviceData().cleanSoundsAndEmitter();
                }
                this.equipedRadio = null;
            }
        }
    }
    
    private void debugAim() {
        if (this != IsoPlayer.getInstance()) {
            return;
        }
        final IsoPlayer isoPlayer = (IsoPlayer)this;
        if (!isoPlayer.IsAiming()) {
            return;
        }
        HandWeapon bareHands = Type.tryCastTo(this.getPrimaryHandItem(), HandWeapon.class);
        if (bareHands == null) {
            bareHands = isoPlayer.bareHands;
        }
        final float n = bareHands.getMaxRange(isoPlayer) * bareHands.getRangeMod(isoPlayer);
        final float lookAngleRadians = this.getLookAngleRadians();
        LineDrawer.drawDirectionLine(this.x, this.y, this.z, n, lookAngleRadians, 1.0f, 1.0f, 1.0f, 0.5f, 1);
        final float n2 = bareHands.getMinAngle() - bareHands.getAimingPerkMinAngleModifier() * (this.getPerkLevel(PerkFactory.Perks.Aiming) / 2.0f);
        LineDrawer.drawDotLines(this.x, this.y, this.z, n, lookAngleRadians, n2, 1.0f, 1.0f, 1.0f, 0.5f, 1);
        final float minRange = bareHands.getMinRange();
        LineDrawer.drawArc(this.x, this.y, this.z, minRange, lookAngleRadians, n2, 6, 1.0f, 1.0f, 1.0f, 0.5f);
        if (minRange != n) {
            LineDrawer.drawArc(this.x, this.y, this.z, n, lookAngleRadians, n2, 6, 1.0f, 1.0f, 1.0f, 0.5f);
        }
        LineDrawer.drawArc(this.x, this.y, this.z, PZMath.min(n + 1.0f, 2.0f), lookAngleRadians, n2, 6, 0.75f, 0.75f, 0.75f, 0.5f);
        final float ignoreProneZombieRange = Core.getInstance().getIgnoreProneZombieRange();
        if (ignoreProneZombieRange > 0.0f) {
            LineDrawer.drawArc(this.x, this.y, this.z, ignoreProneZombieRange, lookAngleRadians, 0.0f, 12, 0.0f, 0.0f, 1.0f, 0.25f);
            LineDrawer.drawDotLines(this.x, this.y, this.z, ignoreProneZombieRange, lookAngleRadians, 0.0f, 0.0f, 0.0f, 1.0f, 0.25f, 1);
        }
        final AttackVars attackVars = new AttackVars();
        final ArrayList list = new ArrayList<HitInfo>();
        SwipeStatePlayer.instance().CalcAttackVars((IsoLivingCharacter)this, attackVars);
        SwipeStatePlayer.instance().CalcHitList(this, false, attackVars, list);
        if (attackVars.targetOnGround.getMovingObject() != null) {
            final HitInfo hitInfo = attackVars.targetsProne.get(0);
            LineDrawer.DrawIsoCircle(hitInfo.x, hitInfo.y, hitInfo.z, 0.1f, 8, 1.0f, 1.0f, 0.0f, 1.0f);
        }
        else if (attackVars.targetsStanding.size() > 0) {
            final HitInfo hitInfo2 = attackVars.targetsStanding.get(0);
            LineDrawer.DrawIsoCircle(hitInfo2.x, hitInfo2.y, hitInfo2.z, 0.1f, 8, 1.0f, 1.0f, 0.0f, 1.0f);
        }
        for (int i = 0; i < list.size(); ++i) {
            final HitInfo hitInfo3 = list.get(i);
            final IsoMovingObject object = hitInfo3.getObject();
            if (object != null) {
                final int chance = hitInfo3.chance;
                final float n3 = 1.0f - chance / 100.0f;
                final float n4 = 1.0f - n3;
                final float n5 = Math.max(0.2f, chance / 100.0f) / 2.0f;
                final float xToScreenExact = IsoUtils.XToScreenExact(object.x - n5, object.y + n5, object.z, 0);
                final float yToScreenExact = IsoUtils.YToScreenExact(object.x - n5, object.y + n5, object.z, 0);
                final float xToScreenExact2 = IsoUtils.XToScreenExact(object.x - n5, object.y - n5, object.z, 0);
                final float yToScreenExact2 = IsoUtils.YToScreenExact(object.x - n5, object.y - n5, object.z, 0);
                final float xToScreenExact3 = IsoUtils.XToScreenExact(object.x + n5, object.y - n5, object.z, 0);
                final float yToScreenExact3 = IsoUtils.YToScreenExact(object.x + n5, object.y - n5, object.z, 0);
                final float xToScreenExact4 = IsoUtils.XToScreenExact(object.x + n5, object.y + n5, object.z, 0);
                final float yToScreenExact4 = IsoUtils.YToScreenExact(object.x + n5, object.y + n5, object.z, 0);
                SpriteRenderer.instance.renderPoly(xToScreenExact, yToScreenExact, xToScreenExact2, yToScreenExact2, xToScreenExact3, yToScreenExact3, xToScreenExact4, yToScreenExact4, n3, n4, 0.0f, 0.5f);
                final UIFont debugConsole = UIFont.DebugConsole;
                TextManager.instance.DrawStringCentre(debugConsole, xToScreenExact4, yToScreenExact4, String.valueOf(hitInfo3.dot), 1.0, 1.0, 1.0, 1.0);
                TextManager.instance.DrawStringCentre(debugConsole, (double)xToScreenExact4, (double)(yToScreenExact4 + TextManager.instance.getFontHeight(debugConsole)), invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, hitInfo3.chance), 1.0, 1.0, 1.0, 1.0);
                float n6 = 1.0f;
                final float n7 = 1.0f;
                float n8 = 1.0f;
                final float sqrt = PZMath.sqrt(hitInfo3.distSq);
                if (sqrt < bareHands.getMinRange()) {
                    n8 = (n6 = 0.0f);
                }
                TextManager.instance.DrawStringCentre(debugConsole, (double)xToScreenExact4, (double)(yToScreenExact4 + TextManager.instance.getFontHeight(debugConsole) * 2), invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, sqrt), (double)n6, (double)n7, (double)n8, 1.0);
            }
            if (hitInfo3.window.getObject() != null) {
                hitInfo3.window.getObject().setHighlighted(true);
            }
        }
    }
    
    private void debugTestDotSide() {
        if (this != IsoPlayer.getInstance()) {
            return;
        }
        final float lookAngleRadians = this.getLookAngleRadians();
        final float n = 2.0f;
        LineDrawer.drawDotLines(this.x, this.y, this.z, n, lookAngleRadians, 0.7f, 1.0f, 1.0f, 1.0f, 0.5f, 1);
        LineDrawer.drawDotLines(this.x, this.y, this.z, n, lookAngleRadians, -0.5f, 1.0f, 1.0f, 1.0f, 0.5f, 1);
        LineDrawer.drawArc(this.x, this.y, this.z, n, lookAngleRadians, -1.0f, 16, 1.0f, 1.0f, 1.0f, 0.5f);
        final ArrayList<IsoZombie> zombieList = this.getCell().getZombieList();
        for (int i = 0; i < zombieList.size(); ++i) {
            final IsoZombie isoZombie = zombieList.get(i);
            if (this.DistToSquared(isoZombie) < n * n) {
                LineDrawer.DrawIsoCircle(isoZombie.x, isoZombie.y, isoZombie.z, 0.3f, 1.0f, 1.0f, 1.0f, 1.0f);
                final float n2 = 0.2f;
                final float xToScreenExact = IsoUtils.XToScreenExact(isoZombie.x + n2, isoZombie.y + n2, isoZombie.z, 0);
                final float yToScreenExact = IsoUtils.YToScreenExact(isoZombie.x + n2, isoZombie.y + n2, isoZombie.z, 0);
                final UIFont debugConsole = UIFont.DebugConsole;
                final int fontHeight = TextManager.instance.getFontHeight(debugConsole);
                TextManager.instance.DrawStringCentre(debugConsole, (double)xToScreenExact, (double)(yToScreenExact + fontHeight), invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.testDotSide(isoZombie)), 1.0, 1.0, 1.0, 1.0);
                final Vector2 lookVector = this.getLookVector(IsoGameCharacter.tempo2);
                final Vector2 set = IsoGameCharacter.tempo.set(isoZombie.x - this.x, isoZombie.y - this.y);
                set.normalize();
                TextManager.instance.DrawStringCentre(debugConsole, (double)xToScreenExact, (double)(yToScreenExact + fontHeight * 2), invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, PZMath.radToDeg(PZMath.wrap(set.getDirection() - lookVector.getDirection(), 0.0f, 6.2831855f))), 1.0, 1.0, 1.0, 1.0);
                TextManager.instance.DrawStringCentre(debugConsole, (double)xToScreenExact, (double)(yToScreenExact + fontHeight * 3), invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, PZMath.radToDeg((float)Math.acos(this.getDotWithForwardDirection(isoZombie.x, isoZombie.y)))), 1.0, 1.0, 1.0, 1.0);
            }
        }
    }
    
    private void debugVision() {
        if (this != IsoPlayer.getInstance()) {
            return;
        }
        final float calculateVisionCone = LightingJNI.calculateVisionCone(this);
        LineDrawer.drawDotLines(this.x, this.y, this.z, GameTime.getInstance().getViewDist(), this.getLookAngleRadians(), -calculateVisionCone, 1.0f, 1.0f, 1.0f, 0.5f, 1);
        LineDrawer.drawArc(this.x, this.y, this.z, GameTime.getInstance().getViewDist(), this.getLookAngleRadians(), -calculateVisionCone, 16, 1.0f, 1.0f, 1.0f, 0.5f);
        LineDrawer.drawArc(this.x, this.y, this.z, 3.5f - this.stats.getFatigue(), this.getLookAngleRadians(), -1.0f, 32, 1.0f, 1.0f, 1.0f, 0.5f);
    }
    
    public void setDefaultState() {
        this.stateMachine.changeState(this.defaultState, null);
    }
    
    public void SetOnFire() {
        if (this.OnFire) {
            return;
        }
        this.OnFire = true;
        final float n = (float)Core.TileScale;
        this.AttachAnim("Fire", "01", 4, IsoFireManager.FireAnimDelay, (int)(-(this.offsetX + 1.0f * n)) + (8 - Rand.Next(16)), (int)(-(this.offsetY + -89.0f * n)) + (int)((10 + Rand.Next(20)) * n), true, 0, false, 0.7f, IsoFireManager.FireTintMod);
        IsoFireManager.AddBurningCharacter(this);
        final int next = Rand.Next(BodyPartType.ToIndex(BodyPartType.Hand_L), BodyPartType.ToIndex(BodyPartType.MAX));
        if (this instanceof IsoPlayer) {
            this.getBodyDamage().getBodyParts().get(next).setBurned();
        }
        if (n == 2.0f) {
            this.AttachedAnimSprite.get(this.AttachedAnimSprite.size() - 1).setScale(n, n);
        }
        if (!this.getEmitter().isPlaying("BurningFlesh")) {
            this.getEmitter().playSound("BurningFlesh");
        }
    }
    
    public void StopBurning() {
        if (!this.OnFire) {
            return;
        }
        IsoFireManager.RemoveBurningCharacter(this);
        this.setOnFire(false);
        if (this.AttachedAnimSprite != null) {
            this.AttachedAnimSprite.clear();
        }
        this.getEmitter().stopOrTriggerSoundByName("BurningFlesh");
    }
    
    public void sendStopBurning() {
        if (GameClient.bClient) {
            if (this instanceof IsoPlayer) {
                final IsoPlayer isoPlayer = (IsoPlayer)this;
                if (isoPlayer.isLocalPlayer()) {
                    this.StopBurning();
                }
                else {
                    GameClient.sendStopFire(isoPlayer);
                }
            }
            if (this.isZombie()) {
                GameClient.sendStopFire(this);
            }
        }
    }
    
    public void SpreadFire() {
        if (!this.OnFire) {
            return;
        }
        if (GameServer.bServer && this instanceof IsoPlayer) {
            return;
        }
        if (GameClient.bClient && this.isZombie() && this instanceof IsoZombie && ((IsoZombie)this).isRemoteZombie()) {
            return;
        }
        if (GameClient.bClient && this instanceof IsoPlayer && ((IsoPlayer)this).bRemote) {
            return;
        }
        if (!SandboxOptions.instance.FireSpread.getValue()) {
            return;
        }
        if (this.square != null && !this.square.getProperties().Is(IsoFlagType.burning) && Rand.Next(Rand.AdjustForFramerate(3000)) < this.FireSpreadProbability) {
            IsoFireManager.StartFire(this.getCell(), this.square, false, 80);
        }
    }
    
    public void Throw(final HandWeapon handWeapon) {
        if (this instanceof IsoPlayer && ((IsoPlayer)this).getJoypadBind() != -1) {
            final Vector2 set = IsoGameCharacter.tempo.set(this.m_forwardDirection);
            set.setLength(handWeapon.getMaxRange());
            this.attackTargetSquare = this.getCell().getGridSquare(this.getX() + set.getX(), this.getY() + set.getY(), this.getZ());
        }
        float maxRange = this.attackTargetSquare.getX() - this.getX();
        if (maxRange > 0.0f) {
            if (this.attackTargetSquare.getX() - this.getX() > handWeapon.getMaxRange()) {
                maxRange = handWeapon.getMaxRange();
            }
        }
        else if (this.attackTargetSquare.getX() - this.getX() < -handWeapon.getMaxRange()) {
            maxRange = -handWeapon.getMaxRange();
        }
        float maxRange2 = this.attackTargetSquare.getY() - this.getY();
        if (maxRange2 > 0.0f) {
            if (this.attackTargetSquare.getY() - this.getY() > handWeapon.getMaxRange()) {
                maxRange2 = handWeapon.getMaxRange();
            }
        }
        else if (this.attackTargetSquare.getY() - this.getY() < -handWeapon.getMaxRange()) {
            maxRange2 = -handWeapon.getMaxRange();
        }
        if (handWeapon.getPhysicsObject().equals("Ball")) {
            final IsoBall isoBall = new IsoBall(this.getCell(), this.getX(), this.getY(), this.getZ() + 0.6f, maxRange * 0.4f, maxRange2 * 0.4f, handWeapon, this);
        }
        else {
            final IsoMolotovCocktail isoMolotovCocktail = new IsoMolotovCocktail(this.getCell(), this.getX(), this.getY(), this.getZ() + 0.6f, maxRange * 0.4f, maxRange2 * 0.4f, handWeapon, this);
        }
        if (this instanceof IsoPlayer) {
            ((IsoPlayer)this).setAttackAnimThrowTimer(0L);
        }
    }
    
    public void serverRemoveItemFromZombie(final String anObject) {
        if (!GameServer.bServer) {
            return;
        }
        final IsoZombie isoZombie = Type.tryCastTo(this, IsoZombie.class);
        this.getItemVisuals(IsoGameCharacter.tempItemVisuals);
        for (int i = 0; i < IsoGameCharacter.tempItemVisuals.size(); ++i) {
            final Item scriptItem = IsoGameCharacter.tempItemVisuals.get(i).getScriptItem();
            if (scriptItem != null) {
                if (scriptItem.name.equals(anObject)) {
                    IsoGameCharacter.tempItemVisuals.remove(i--);
                    isoZombie.itemVisuals.clear();
                    isoZombie.itemVisuals.addAll(IsoGameCharacter.tempItemVisuals);
                }
            }
        }
    }
    
    public boolean helmetFall(final boolean b) {
        return this.helmetFall(b, null);
    }
    
    public boolean helmetFall(final boolean b, final String s) {
        final IsoPlayer isoPlayer = Type.tryCastTo(this, IsoPlayer.class);
        boolean b2 = false;
        InventoryItem inventoryItem = null;
        final IsoZombie isoZombie = Type.tryCastTo(this, IsoZombie.class);
        if (isoZombie != null && !isoZombie.isUsingWornItems()) {
            this.getItemVisuals(IsoGameCharacter.tempItemVisuals);
            for (int i = 0; i < IsoGameCharacter.tempItemVisuals.size(); ++i) {
                final ItemVisual itemVisual = IsoGameCharacter.tempItemVisuals.get(i);
                final Item scriptItem = itemVisual.getScriptItem();
                if (scriptItem != null) {
                    if (scriptItem.getType() == Item.Type.Clothing) {
                        if (scriptItem.getChanceToFall() > 0) {
                            int chanceToFall = scriptItem.getChanceToFall();
                            if (b) {
                                chanceToFall += 40;
                            }
                            if (scriptItem.name.equals(s)) {
                                chanceToFall = 100;
                            }
                            if (Rand.Next(100) > chanceToFall) {
                                final InventoryItem createItem = InventoryItemFactory.CreateItem(scriptItem.getFullName());
                                if (createItem != null) {
                                    if (createItem.getVisual() != null) {
                                        createItem.getVisual().copyFrom(itemVisual);
                                        createItem.synchWithVisual();
                                    }
                                    final IsoFallingClothing isoFallingClothing = new IsoFallingClothing(this.getCell(), this.getX(), this.getY(), PZMath.min(this.getZ() + 0.4f, (int)this.getZ() + 0.95f), 0.2f, 0.2f, createItem);
                                    if (!StringUtils.isNullOrEmpty(s)) {
                                        isoFallingClothing.addWorldItem = false;
                                    }
                                    IsoGameCharacter.tempItemVisuals.remove(i--);
                                    isoZombie.itemVisuals.clear();
                                    isoZombie.itemVisuals.addAll(IsoGameCharacter.tempItemVisuals);
                                    this.resetModelNextFrame();
                                    this.onWornItemsChanged();
                                    b2 = true;
                                    inventoryItem = createItem;
                                }
                            }
                        }
                    }
                }
            }
        }
        else if (this.getWornItems() != null && !this.getWornItems().isEmpty()) {
            for (int j = 0; j < this.getWornItems().size(); ++j) {
                final WornItem value = this.getWornItems().get(j);
                final InventoryItem item = value.getItem();
                final String location = value.getLocation();
                if (item instanceof Clothing) {
                    int chanceToFall2 = ((Clothing)item).getChanceToFall();
                    if (b) {
                        chanceToFall2 += 40;
                    }
                    if (item.getType().equals(s)) {
                        chanceToFall2 = 100;
                    }
                    if (((Clothing)item).getChanceToFall() > 0 && Rand.Next(100) <= chanceToFall2) {
                        final IsoFallingClothing isoFallingClothing2 = new IsoFallingClothing(this.getCell(), this.getX(), this.getY(), PZMath.min(this.getZ() + 0.4f, (int)this.getZ() + 0.95f), Rand.Next(-0.2f, 0.2f), Rand.Next(-0.2f, 0.2f), item);
                        if (!StringUtils.isNullOrEmpty(s)) {
                            isoFallingClothing2.addWorldItem = false;
                        }
                        this.getInventory().Remove(item);
                        this.getWornItems().remove(item);
                        inventoryItem = item;
                        this.resetModelNextFrame();
                        this.onWornItemsChanged();
                        b2 = true;
                        if (GameClient.bClient && isoPlayer != null && isoPlayer.isLocalPlayer() && StringUtils.isNullOrEmpty(s)) {
                            GameClient.instance.sendClothing(isoPlayer, location, null);
                        }
                    }
                }
            }
        }
        if (b2 && GameClient.bClient && StringUtils.isNullOrEmpty(s) && IsoPlayer.getInstance().isLocalPlayer()) {
            GameClient.sendZombieHelmetFall(IsoPlayer.getInstance(), this, inventoryItem);
        }
        if (b2 && isoPlayer != null && isoPlayer.isLocalPlayer()) {
            LuaEventManager.triggerEvent("OnClothingUpdated", this);
        }
        if (b2 && this.isZombie()) {
            PersistentOutfits.instance.setFallenHat(this, true);
        }
        return b2;
    }
    
    public void smashCarWindow(final VehiclePart value) {
        final HashMap<Object, Object> stateMachineParams = this.getStateMachineParams(SmashWindowState.instance());
        stateMachineParams.clear();
        stateMachineParams.put(0, value.getWindow());
        stateMachineParams.put(1, value.getVehicle());
        stateMachineParams.put(2, value);
        this.actionContext.reportEvent("EventSmashWindow");
    }
    
    public void smashWindow(final IsoWindow value) {
        if (!value.isInvincible()) {
            final HashMap<Object, Object> stateMachineParams = this.getStateMachineParams(SmashWindowState.instance());
            stateMachineParams.clear();
            stateMachineParams.put(0, value);
            this.actionContext.reportEvent("EventSmashWindow");
        }
    }
    
    public void openWindow(final IsoWindow isoWindow) {
        if (!isoWindow.isInvincible()) {
            OpenWindowState.instance().setParams(this, isoWindow);
            this.actionContext.reportEvent("EventOpenWindow");
        }
    }
    
    public void closeWindow(final IsoWindow value) {
        if (!value.isInvincible()) {
            final HashMap<Object, Object> stateMachineParams = this.getStateMachineParams(CloseWindowState.instance());
            stateMachineParams.clear();
            stateMachineParams.put(0, value);
            this.actionContext.reportEvent("EventCloseWindow");
        }
    }
    
    public void climbThroughWindow(final IsoWindow isoWindow) {
        if (isoWindow.canClimbThrough(this)) {
            final float n = this.x - (int)this.x;
            final float n2 = this.y - (int)this.y;
            int n3 = 0;
            int n4 = 0;
            if (isoWindow.getX() > this.x && !isoWindow.north) {
                n3 = -1;
            }
            if (isoWindow.getY() > this.y && isoWindow.north) {
                n4 = -1;
            }
            this.x = isoWindow.getX() + n + n3;
            this.y = isoWindow.getY() + n2 + n4;
            ClimbThroughWindowState.instance().setParams(this, isoWindow);
            this.actionContext.reportEvent("EventClimbWindow");
        }
    }
    
    public void climbThroughWindow(final IsoWindow isoWindow, final Integer n) {
        if (isoWindow.canClimbThrough(this)) {
            ClimbThroughWindowState.instance().setParams(this, isoWindow);
            this.actionContext.reportEvent("EventClimbWindow");
        }
    }
    
    public boolean isClosingWindow(final IsoWindow isoWindow) {
        return isoWindow != null && this.isCurrentState(CloseWindowState.instance()) && CloseWindowState.instance().getWindow(this) == isoWindow;
    }
    
    public boolean isClimbingThroughWindow(final IsoWindow isoWindow) {
        return isoWindow != null && this.isCurrentState(ClimbThroughWindowState.instance()) && this.getVariableBoolean("BlockWindow") && ClimbThroughWindowState.instance().getWindow(this) == isoWindow;
    }
    
    public void climbThroughWindowFrame(final IsoObject isoObject) {
        if (IsoWindowFrame.canClimbThrough(isoObject, this)) {
            ClimbThroughWindowState.instance().setParams(this, isoObject);
            this.actionContext.reportEvent("EventClimbWindow");
        }
    }
    
    public void climbSheetRope() {
        if (!this.canClimbSheetRope(this.current)) {
            return;
        }
        this.getStateMachineParams(ClimbSheetRopeState.instance()).clear();
        this.actionContext.reportEvent("EventClimbRope");
    }
    
    public void climbDownSheetRope() {
        if (!this.canClimbDownSheetRope(this.current)) {
            return;
        }
        this.dropHeavyItems();
        this.getStateMachineParams(ClimbDownSheetRopeState.instance()).clear();
        this.actionContext.reportEvent("EventClimbDownRope");
    }
    
    public boolean canClimbSheetRope(IsoGridSquare gridSquare) {
        if (gridSquare == null) {
            return false;
        }
        final int z = gridSquare.getZ();
        while (gridSquare != null) {
            if (!IsoWindow.isSheetRopeHere(gridSquare)) {
                return false;
            }
            if (!IsoWindow.canClimbHere(gridSquare)) {
                return false;
            }
            if (gridSquare.TreatAsSolidFloor() && gridSquare.getZ() > z) {
                return false;
            }
            if (IsoWindow.isTopOfSheetRopeHere(gridSquare)) {
                return true;
            }
            gridSquare = this.getCell().getGridSquare(gridSquare.getX(), gridSquare.getY(), gridSquare.getZ() + 1);
        }
        return false;
    }
    
    public boolean canClimbDownSheetRopeInCurrentSquare() {
        return this.canClimbDownSheetRope(this.current);
    }
    
    public boolean canClimbDownSheetRope(IsoGridSquare gridSquare) {
        if (gridSquare == null) {
            return false;
        }
        final int z = gridSquare.getZ();
        while (gridSquare != null) {
            if (!IsoWindow.isSheetRopeHere(gridSquare)) {
                return false;
            }
            if (!IsoWindow.canClimbHere(gridSquare)) {
                return false;
            }
            if (gridSquare.TreatAsSolidFloor()) {
                return gridSquare.getZ() < z;
            }
            gridSquare = this.getCell().getGridSquare(gridSquare.getX(), gridSquare.getY(), gridSquare.getZ() - 1);
        }
        return false;
    }
    
    public void climbThroughWindow(final IsoThumpable isoThumpable) {
        if (isoThumpable.canClimbThrough(this)) {
            final float n = this.x - (int)this.x;
            final float n2 = this.y - (int)this.y;
            int n3 = 0;
            int n4 = 0;
            if (isoThumpable.getX() > this.x && !isoThumpable.north) {
                n3 = -1;
            }
            if (isoThumpable.getY() > this.y && isoThumpable.north) {
                n4 = -1;
            }
            this.x = isoThumpable.getX() + n + n3;
            this.y = isoThumpable.getY() + n2 + n4;
            ClimbThroughWindowState.instance().setParams(this, isoThumpable);
            this.actionContext.reportEvent("EventClimbWindow");
        }
    }
    
    public void climbThroughWindow(final IsoThumpable isoThumpable, final Integer n) {
        if (isoThumpable.canClimbThrough(this)) {
            ClimbThroughWindowState.instance().setParams(this, isoThumpable);
            this.actionContext.reportEvent("EventClimbWindow");
        }
    }
    
    public void climbOverFence(final IsoDirections isoDirections) {
        if (this.current == null) {
            return;
        }
        if (!IsoWindow.canClimbThroughHelper(this, this.current, this.current.nav[isoDirections.index()], isoDirections == IsoDirections.N || isoDirections == IsoDirections.S)) {
            return;
        }
        ClimbOverFenceState.instance().setParams(this, isoDirections);
        this.actionContext.reportEvent("EventClimbFence");
    }
    
    public boolean isAboveTopOfStairs() {
        if (this.z == 0.0f || this.z - (int)this.z > 0.01 || (this.current != null && this.current.TreatAsSolidFloor())) {
            return false;
        }
        final IsoGridSquare gridSquare = this.getCell().getGridSquare(this.x, this.y, this.z - 1.0f);
        return gridSquare != null && (gridSquare.Has(IsoObjectType.stairsTN) || gridSquare.Has(IsoObjectType.stairsTW));
    }
    
    @Override
    public void preupdate() {
        super.preupdate();
        if (!this.m_bDebugVariablesRegistered && DebugOptions.instance.Character.Debug.RegisterDebugVariables.getValue()) {
            this.registerDebugGameVariables();
        }
        this.updateAnimationRecorderState();
        if (this.isAnimationRecorderActive()) {
            this.m_animationRecorder.beginLine(IsoWorld.instance.getFrameNo());
        }
    }
    
    public void setTeleport(final NetworkTeleport teleport) {
        this.teleport = teleport;
    }
    
    public NetworkTeleport getTeleport() {
        return this.teleport;
    }
    
    public boolean isTeleporting() {
        return this.teleport != null;
    }
    
    @Override
    public void update() {
        s_performance.update.invokeAndMeasure(this, IsoGameCharacter::updateInternal);
    }
    
    private void updateInternal() {
        if (this.current == null) {
            return;
        }
        if (this.teleport != null) {
            this.teleport.process(IsoPlayer.getPlayerIndex());
        }
        this.updateAlpha();
        if (this.isNPC) {
            if (this.GameCharacterAIBrain == null) {
                this.GameCharacterAIBrain = new GameCharacterAIBrain(this);
            }
            this.GameCharacterAIBrain.update();
        }
        if (this.sprite != null) {
            this.legsSprite = this.sprite;
        }
        if (this.isDead() && (this.current == null || !this.current.getMovingObjects().contains(this))) {
            return;
        }
        if (!GameClient.bClient && !this.m_invisible && this.getCurrentSquare().getTrapPositionX() > -1 && this.getCurrentSquare().getTrapPositionY() > -1 && this.getCurrentSquare().getTrapPositionZ() > -1) {
            this.getCurrentSquare().explodeTrap();
        }
        if (this.getBodyDamage() != null && this.getCurrentBuilding() != null && this.getCurrentBuilding().isToxic()) {
            final float n = GameTime.getInstance().getMultiplier() / 1.6f;
            if (this.getStats().getFatigue() < 1.0f) {
                this.getStats().setFatigue(this.getStats().getFatigue() + 1.0E-4f * n);
            }
            if (this.getStats().getFatigue() > 0.8) {
                this.getBodyDamage().getBodyPart(BodyPartType.Head).ReduceHealth(0.1f * n);
            }
            this.getBodyDamage().getBodyPart(BodyPartType.Torso_Upper).ReduceHealth(0.1f * n);
        }
        if (this.lungeFallTimer > 0.0f) {
            this.lungeFallTimer -= GameTime.getInstance().getMultiplier() / 1.6f;
        }
        if (this.getMeleeDelay() > 0.0f) {
            this.setMeleeDelay(this.getMeleeDelay() - 0.625f * GameTime.getInstance().getMultiplier());
        }
        if (this.getRecoilDelay() > 0.0f) {
            this.setRecoilDelay(this.getRecoilDelay() - 0.625f * GameTime.getInstance().getMultiplier());
        }
        this.sx = 0.0f;
        this.sy = 0.0f;
        if (this.current.getRoom() != null && this.current.getRoom().building.def.bAlarmed && (!this.isZombie() || Core.bTutorial) && !GameClient.bClient) {
            boolean b = false;
            if (this instanceof IsoPlayer && (((IsoPlayer)this).isInvisible() || ((IsoPlayer)this).isGhostMode())) {
                b = true;
            }
            if (!b) {
                AmbientStreamManager.instance.doAlarm(this.current.getRoom().def);
            }
        }
        this.updateSeenVisibility();
        this.llx = this.getLx();
        this.lly = this.getLy();
        this.setLx(this.getX());
        this.setLy(this.getY());
        this.setLz(this.getZ());
        this.updateBeardAndHair();
        this.updateFalling();
        if (this.descriptor != null) {
            this.descriptor.Instance = this;
        }
        if (!this.isZombie()) {
            if (this.Traits.Agoraphobic.isSet() && this.getCurrentSquare().getRoom() == null) {
                final Stats stats = this.stats;
                stats.Panic += 0.5f * (GameTime.getInstance().getMultiplier() / 1.6f);
            }
            if (this.Traits.Claustophobic.isSet() && this.getCurrentSquare().getRoom() != null) {
                float n2 = 1.0f - this.getCurrentSquare().getRoom().def.getH() * this.getCurrentSquare().getRoom().def.getW() / 70.0f;
                if (n2 < 0.0f) {
                    n2 = 0.0f;
                }
                float n3 = 0.6f * n2 * (GameTime.getInstance().getMultiplier() / 1.6f);
                if (n3 > 0.6f) {
                    n3 = 0.6f;
                }
                final Stats stats2 = this.stats;
                stats2.Panic += n3;
            }
            if (this.Moodles != null) {
                this.Moodles.Update();
            }
            if (this.Asleep) {
                this.BetaEffect = 0.0f;
                this.SleepingTabletEffect = 0.0f;
                this.StopAllActionQueue();
            }
            if (this.BetaEffect > 0.0f) {
                this.BetaEffect -= GameTime.getInstance().getMultiplier() / 1.6f;
                final Stats stats3 = this.stats;
                stats3.Panic -= 0.6f * (GameTime.getInstance().getMultiplier() / 1.6f);
                if (this.stats.Panic < 0.0f) {
                    this.stats.Panic = 0.0f;
                }
            }
            else {
                this.BetaDelta = 0.0f;
            }
            if (this.DepressFirstTakeTime > 0.0f || this.DepressEffect > 0.0f) {
                this.DepressFirstTakeTime -= GameTime.getInstance().getMultiplier() / 1.6f;
                if (this.DepressFirstTakeTime < 0.0f) {
                    this.DepressFirstTakeTime = -1.0f;
                    this.DepressEffect -= GameTime.getInstance().getMultiplier() / 1.6f;
                    this.getBodyDamage().setUnhappynessLevel(this.getBodyDamage().getUnhappynessLevel() - 0.03f * (GameTime.getInstance().getMultiplier() / 1.6f));
                    if (this.getBodyDamage().getUnhappynessLevel() < 0.0f) {
                        this.getBodyDamage().setUnhappynessLevel(0.0f);
                    }
                }
            }
            if (this.DepressEffect < 0.0f) {
                this.DepressEffect = 0.0f;
            }
            if (this.SleepingTabletEffect > 0.0f) {
                this.SleepingTabletEffect -= GameTime.getInstance().getMultiplier() / 1.6f;
                final Stats stats4 = this.stats;
                stats4.fatigue += 0.0016666667f * this.SleepingTabletDelta * (GameTime.getInstance().getMultiplier() / 1.6f);
            }
            else {
                this.SleepingTabletDelta = 0.0f;
            }
            final int moodleLevel = this.Moodles.getMoodleLevel(MoodleType.Panic);
            if (moodleLevel == 2) {
                final Stats stats5 = this.stats;
                stats5.Sanity -= 3.2E-7f;
            }
            else if (moodleLevel == 3) {
                final Stats stats6 = this.stats;
                stats6.Sanity -= 4.8000004E-7f;
            }
            else if (moodleLevel == 4) {
                final Stats stats7 = this.stats;
                stats7.Sanity -= 8.0E-7f;
            }
            else if (moodleLevel == 0) {
                final Stats stats8 = this.stats;
                stats8.Sanity += 1.0E-7f;
            }
            if (this.Moodles.getMoodleLevel(MoodleType.Tired) == 4) {
                final Stats stats9 = this.stats;
                stats9.Sanity -= 2.0E-6f;
            }
            if (this.stats.Sanity < 0.0f) {
                this.stats.Sanity = 0.0f;
            }
            if (this.stats.Sanity > 1.0f) {
                this.stats.Sanity = 1.0f;
            }
        }
        if (!this.CharacterActions.isEmpty()) {
            final BaseAction obj = this.CharacterActions.get(0);
            int valid = obj.valid() ? 1 : 0;
            if (valid != 0 && !obj.bStarted) {
                obj.waitToStart();
            }
            else if (valid != 0 && !obj.finished() && !obj.forceComplete && !obj.forceStop) {
                obj.update();
            }
            if (valid == 0 || obj.finished() || obj.forceComplete || obj.forceStop) {
                if (obj.finished() || obj.forceComplete) {
                    obj.perform();
                    valid = 1;
                }
                if ((obj.finished() && !obj.loopAction) || obj.forceComplete || obj.forceStop || valid == 0) {
                    if (obj.bStarted && (obj.forceStop || valid == 0)) {
                        obj.stop();
                    }
                    this.CharacterActions.removeElement(obj);
                    if (this == IsoPlayer.players[0] || this == IsoPlayer.players[1] || this == IsoPlayer.players[2] || this == IsoPlayer.players[3]) {
                        UIManager.getProgressBar(((IsoPlayer)this).getPlayerNum()).setValue(0.0f);
                    }
                }
            }
            for (int i = 0; i < this.EnemyList.size(); ++i) {
                final IsoGameCharacter o = this.EnemyList.get(i);
                if (o.isDead()) {
                    this.EnemyList.remove(o);
                    --i;
                }
            }
        }
        if (SystemDisabler.doCharacterStats && this.BodyDamage != null) {
            this.BodyDamage.Update();
            this.updateBandages();
        }
        if (this == IsoPlayer.getInstance()) {
            if (this.leftHandItem != null && this.leftHandItem.getUses() <= 0) {
                this.leftHandItem = null;
            }
            if (this.rightHandItem != null && this.rightHandItem.getUses() <= 0) {
                this.rightHandItem = null;
            }
        }
        if (SystemDisabler.doCharacterStats) {
            this.calculateStats();
        }
        this.moveForwardVec.x = 0.0f;
        this.moveForwardVec.y = 0.0f;
        if (!this.Asleep || !(this instanceof IsoPlayer)) {
            this.setLx(this.getX());
            this.setLy(this.getY());
            this.setLz(this.getZ());
            this.square = this.getCurrentSquare();
            if (this.sprite != null) {
                if (!this.bUseParts) {
                    this.sprite.update(this.def);
                }
                else {
                    this.legsSprite.update(this.def);
                }
            }
            this.setStateEventDelayTimer(this.getStateEventDelayTimer() - GameTime.getInstance().getMultiplier() / 1.6f);
        }
        this.stateMachine.update();
        if (this.isZombie() && VirtualZombieManager.instance.isReused((IsoZombie)this)) {
            DebugLog.log(DebugType.Zombie, invokedynamic(makeConcatWithConstants:(Lzombie/characters/IsoGameCharacter;)Ljava/lang/String;, this));
            return;
        }
        if (this instanceof IsoPlayer) {
            this.ensureOnTile();
        }
        if ((this instanceof IsoPlayer || this instanceof IsoSurvivor) && this.RemoteID == -1 && this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
            RainManager.SetPlayerLocation(((IsoPlayer)this).getPlayerNum(), this.getCurrentSquare());
        }
        this.FireCheck();
        this.SpreadFire();
        this.ReduceHealthWhenBurning();
        this.updateTextObjects();
        if (this.stateMachine.getCurrent() == StaggerBackState.instance()) {
            if (this.getStateEventDelayTimer() > 20.0f) {
                this.BloodImpactX = this.getX();
                this.BloodImpactY = this.getY();
                this.BloodImpactZ = this.getZ();
            }
        }
        else {
            this.BloodImpactX = this.getX();
            this.BloodImpactY = this.getY();
            this.BloodImpactZ = this.getZ();
        }
        if (!this.isZombie()) {
            this.recursiveItemUpdater(this.inventory);
        }
        this.LastZombieKills = this.ZombieKills;
        if (this.AttachedAnimSprite != null) {
            for (int size = this.AttachedAnimSprite.size(), j = 0; j < size; ++j) {
                final IsoSpriteInstance isoSpriteInstance = this.AttachedAnimSprite.get(j);
                final IsoSprite parentSprite = isoSpriteInstance.parentSprite;
                isoSpriteInstance.update();
                final IsoSpriteInstance isoSpriteInstance2 = isoSpriteInstance;
                isoSpriteInstance2.Frame += isoSpriteInstance.AnimFrameIncrease * (GameTime.instance.getMultipliedSecondsSinceLastUpdate() * 60.0f);
                if ((int)isoSpriteInstance.Frame >= parentSprite.CurrentAnim.Frames.size() && parentSprite.Loop && isoSpriteInstance.Looped) {
                    isoSpriteInstance.Frame = 0.0f;
                }
            }
        }
        if (this.isGodMod()) {
            this.getStats().setFatigue(0.0f);
            this.getStats().setEndurance(1.0f);
            this.getBodyDamage().setTemperature(37.0f);
            this.getStats().setHunger(0.0f);
        }
        this.updateMovementMomentum();
        if (this.effectiveEdibleBuffTimer > 0.0f) {
            this.effectiveEdibleBuffTimer -= GameTime.getInstance().getMultiplier() * 0.015f;
            if (this.effectiveEdibleBuffTimer < 0.0f) {
                this.effectiveEdibleBuffTimer = 0.0f;
            }
        }
        if (!GameServer.bServer || GameClient.bClient) {
            this.updateDirt();
        }
    }
    
    private void updateSeenVisibility() {
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            this.updateSeenVisibility(i);
        }
    }
    
    private void updateSeenVisibility(final int n) {
        final IsoPlayer isoPlayer = IsoPlayer.players[n];
        if (isoPlayer == null) {
            return;
        }
        this.IsVisibleToPlayer[n] = this.TestIfSeen(n);
        if (this.IsVisibleToPlayer[n]) {
            return;
        }
        if (this instanceof IsoPlayer) {
            return;
        }
        if (isoPlayer.isSeeEveryone()) {
            return;
        }
        this.setTargetAlpha(n, 0.0f);
    }
    
    private void recursiveItemUpdater(final ItemContainer itemContainer) {
        for (int i = 0; i < itemContainer.Items.size(); ++i) {
            final InventoryItem inventoryItem = itemContainer.Items.get(i);
            if (inventoryItem instanceof InventoryContainer) {
                this.recursiveItemUpdater((InventoryContainer)inventoryItem);
            }
            if (inventoryItem instanceof IUpdater) {
                inventoryItem.update();
            }
        }
    }
    
    private void recursiveItemUpdater(final InventoryContainer inventoryContainer) {
        for (int i = 0; i < inventoryContainer.getInventory().getItems().size(); ++i) {
            final InventoryItem inventoryItem = inventoryContainer.getInventory().getItems().get(i);
            if (inventoryItem instanceof InventoryContainer) {
                this.recursiveItemUpdater((InventoryContainer)inventoryItem);
            }
            if (inventoryItem instanceof IUpdater) {
                inventoryItem.update();
            }
        }
    }
    
    private void updateDirt() {
        if (this.isZombie() || this.getBodyDamage() == null) {
            return;
        }
        int i = 0;
        if (this.isRunning() && Rand.NextBool(Rand.AdjustForFramerate(3500))) {
            i = 1;
        }
        if (this.isSprinting() && Rand.NextBool(Rand.AdjustForFramerate(2500))) {
            i += Rand.Next(1, 3);
        }
        if (this.getBodyDamage().getTemperature() > 37.0f && Rand.NextBool(Rand.AdjustForFramerate(5000))) {
            ++i;
        }
        if (this.getBodyDamage().getTemperature() > 38.0f && Rand.NextBool(Rand.AdjustForFramerate(3000))) {
            ++i;
        }
        final float n = (this.square == null) ? 0.0f : this.square.getPuddlesInGround();
        if (this.isMoving() && n > 0.09f && Rand.NextBool(Rand.AdjustForFramerate(1500))) {
            ++i;
        }
        if (i > 0) {
            this.addDirt(null, i, true);
        }
        final IsoPlayer isoPlayer = Type.tryCastTo(this, IsoPlayer.class);
        if ((isoPlayer != null && isoPlayer.isPlayerMoving()) || (isoPlayer == null && this.isMoving())) {
            int j = 0;
            if (n > 0.09f && Rand.NextBool(Rand.AdjustForFramerate(1500))) {
                ++j;
            }
            if (this.isInTrees() && Rand.NextBool(Rand.AdjustForFramerate(1500))) {
                ++j;
            }
            if (j > 0) {
                this.addDirt(null, j, false);
            }
        }
    }
    
    protected void updateMovementMomentum() {
        final float timeDelta = GameTime.instance.getTimeDelta();
        if (this.isPlayerMoving() && !this.isAiming()) {
            final float n = this.m_momentumScalar * 0.55f;
            if (n >= 0.55f) {
                this.m_momentumScalar = 1.0f;
                return;
            }
            this.m_momentumScalar = PZMath.clamp((n + timeDelta) / 0.55f, 0.0f, 1.0f);
        }
        else {
            final float n2 = (1.0f - this.m_momentumScalar) * 0.25f;
            if (n2 >= 0.25f) {
                this.m_momentumScalar = 0.0f;
                return;
            }
            this.m_momentumScalar = 1.0f - PZMath.clamp((n2 + timeDelta) / 0.25f, 0.0f, 1.0f);
        }
    }
    
    public double getHoursSurvived() {
        return GameTime.instance.getWorldAgeHours();
    }
    
    private void updateBeardAndHair() {
        if (this.isZombie()) {
            return;
        }
        final float n = (float)this.getHoursSurvived();
        if (this.beardGrowTiming < 0.0f || this.beardGrowTiming > n) {
            this.beardGrowTiming = n;
        }
        if (this.hairGrowTiming < 0.0f || this.hairGrowTiming > n) {
            this.hairGrowTiming = n;
        }
        final boolean b = (!GameClient.bClient && !GameServer.bServer) || (ServerOptions.instance.SleepAllowed.getValue() && ServerOptions.instance.SleepNeeded.getValue());
        boolean b2 = false;
        if ((this.isAsleep() || !b) && n - this.beardGrowTiming > 120.0f) {
            this.beardGrowTiming = n;
            final BeardStyle findStyle = BeardStyles.instance.FindStyle(((HumanVisual)this.getVisual()).getBeardModel());
            int level = 1;
            if (findStyle != null) {
                level = findStyle.level;
            }
            final ArrayList<BeardStyle> allStyles = BeardStyles.instance.getAllStyles();
            for (int i = 0; i < allStyles.size(); ++i) {
                if (allStyles.get(i).growReference && allStyles.get(i).level == level + 1) {
                    ((HumanVisual)this.getVisual()).setBeardModel(allStyles.get(i).name);
                    b2 = true;
                    break;
                }
            }
        }
        if ((this.isAsleep() || !b) && n - this.hairGrowTiming > 480.0f) {
            this.hairGrowTiming = n;
            HairStyle hairStyle = HairStyles.instance.FindMaleStyle(((HumanVisual)this.getVisual()).getHairModel());
            if (this.isFemale()) {
                hairStyle = HairStyles.instance.FindFemaleStyle(((HumanVisual)this.getVisual()).getHairModel());
            }
            int level2 = 1;
            if (hairStyle != null) {
                level2 = hairStyle.level;
            }
            ArrayList<HairStyle> list = HairStyles.instance.m_MaleStyles;
            if (this.isFemale()) {
                list = HairStyles.instance.m_FemaleStyles;
            }
            for (int j = 0; j < list.size(); ++j) {
                final HairStyle hairStyle2 = list.get(j);
                if (hairStyle2.growReference && hairStyle2.level == level2 + 1) {
                    ((HumanVisual)this.getVisual()).setHairModel(hairStyle2.name);
                    ((HumanVisual)this.getVisual()).setNonAttachedHair(null);
                    b2 = true;
                    break;
                }
            }
        }
        if (b2) {
            this.resetModelNextFrame();
            LuaEventManager.triggerEvent("OnClothingUpdated", this);
            if (GameClient.bClient) {
                GameClient.instance.sendVisual((IsoPlayer)this);
            }
        }
    }
    
    private void updateFalling() {
        if (this instanceof IsoPlayer && !this.isClimbing()) {
            IsoRoofFixer.FixRoofsAt(this.current);
        }
        if (this.isSeatedInVehicle()) {
            this.fallTime = 0.0f;
            this.lastFallSpeed = 0.0f;
            this.bFalling = false;
            this.wasOnStairs = false;
            return;
        }
        if (this.z > 0.0f) {
            IsoDirections isoDirections = IsoDirections.Max;
            if (!this.isZombie() && this.isClimbing()) {
                if (this.current.Is(IsoFlagType.climbSheetW) || this.current.Is(IsoFlagType.climbSheetTopW)) {
                    isoDirections = IsoDirections.W;
                }
                if (this.current.Is(IsoFlagType.climbSheetE) || this.current.Is(IsoFlagType.climbSheetTopE)) {
                    isoDirections = IsoDirections.E;
                }
                if (this.current.Is(IsoFlagType.climbSheetN) || this.current.Is(IsoFlagType.climbSheetTopN)) {
                    isoDirections = IsoDirections.N;
                }
                if (this.current.Is(IsoFlagType.climbSheetS) || this.current.Is(IsoFlagType.climbSheetTopS)) {
                    isoDirections = IsoDirections.S;
                }
            }
            float lastFallSpeed = 0.125f * (GameTime.getInstance().getMultiplier() / 1.6f);
            if (this.bClimbing) {
                lastFallSpeed = 0.0f;
            }
            if (this.getCurrentState() == ClimbOverFenceState.instance() || this.getCurrentState() == ClimbThroughWindowState.instance()) {
                this.fallTime = 0.0f;
                lastFallSpeed = 0.0f;
            }
            this.lastFallSpeed = lastFallSpeed;
            if (!this.current.TreatAsSolidFloor()) {
                if (isoDirections != IsoDirections.Max) {
                    this.dir = isoDirections;
                }
                this.fallTime += 6.0f * (GameTime.getInstance().getMultiplier() / 1.6f);
                if (isoDirections != IsoDirections.Max) {
                    this.fallTime = 0.0f;
                }
                if (this.fallTime < 20.0f && this.isAboveTopOfStairs()) {
                    this.fallTime = 0.0f;
                }
                this.setZ(this.getZ() - lastFallSpeed);
            }
            else if (this.getZ() > (int)this.getZ() || lastFallSpeed < 0.0f) {
                if (isoDirections != IsoDirections.Max) {
                    this.dir = isoDirections;
                }
                if (!this.current.HasStairs()) {
                    if (!this.wasOnStairs) {
                        this.fallTime += 6.0f * (GameTime.getInstance().getMultiplier() / 1.6f);
                        if (isoDirections != IsoDirections.Max) {
                            this.fallTime = 0.0f;
                        }
                        this.setZ(this.getZ() - lastFallSpeed);
                        if (this.z < (int)this.llz) {
                            this.z = (float)(int)this.llz;
                            this.DoLand();
                            this.fallTime = 0.0f;
                            this.bFalling = false;
                        }
                    }
                    else {
                        this.wasOnStairs = false;
                    }
                }
                else {
                    this.fallTime = 0.0f;
                    this.bFalling = false;
                    this.wasOnStairs = true;
                }
            }
            else {
                this.DoLand();
                this.fallTime = 0.0f;
                this.bFalling = false;
            }
        }
        else {
            this.DoLand();
            this.fallTime = 0.0f;
            this.bFalling = false;
        }
        this.llz = this.lz;
    }
    
    protected void updateMovementRates() {
    }
    
    protected float calculateIdleSpeed() {
        return (float)(0.01f + this.getMoodles().getMoodleLevel(MoodleType.Endurance) * 2.5 / 10.0) * GameTime.getAnimSpeedFix();
    }
    
    public float calculateBaseSpeed() {
        float n = 0.8f;
        float n2 = 1.0f;
        if (this.getMoodles() != null) {
            n = n - this.getMoodles().getMoodleLevel(MoodleType.Endurance) * 0.15f - this.getMoodles().getMoodleLevel(MoodleType.HeavyLoad) * 0.15f;
        }
        if (this.getMoodles().getMoodleLevel(MoodleType.Panic) >= 3 && this.Traits.AdrenalineJunkie.isSet()) {
            n += (this.getMoodles().getMoodleLevel(MoodleType.Panic) + 1) / 20.0f;
        }
        for (int i = BodyPartType.ToIndex(BodyPartType.Torso_Upper); i < BodyPartType.ToIndex(BodyPartType.Neck) + 1; ++i) {
            final BodyPart bodyPart = this.getBodyDamage().getBodyPart(BodyPartType.FromIndex(i));
            if (bodyPart.HasInjury()) {
                n -= 0.1f;
            }
            if (bodyPart.bandaged()) {
                n += 0.05f;
            }
        }
        final BodyPart bodyPart2 = this.getBodyDamage().getBodyPart(BodyPartType.UpperLeg_L);
        if (bodyPart2.getAdditionalPain(true) > 20.0f) {
            n -= (bodyPart2.getAdditionalPain(true) - 20.0f) / 100.0f;
        }
        for (int j = 0; j < this.bagsWorn.size(); ++j) {
            n2 += this.calcRunSpeedModByBag(this.bagsWorn.get(j));
        }
        if (this.getPrimaryHandItem() != null && this.getPrimaryHandItem() instanceof InventoryContainer) {
            n2 += this.calcRunSpeedModByBag((InventoryContainer)this.getPrimaryHandItem());
        }
        if (this.getSecondaryHandItem() != null && this.getSecondaryHandItem() instanceof InventoryContainer) {
            n2 += this.calcRunSpeedModByBag((InventoryContainer)this.getSecondaryHandItem());
        }
        this.fullSpeedMod = this.runSpeedModifier + (n2 - 1.0f);
        return n * (1.0f - Math.abs(1.0f - this.fullSpeedMod) / 2.0f);
    }
    
    private float calcRunSpeedModByBag(final InventoryContainer inventoryContainer) {
        return (inventoryContainer.getScriptItem().runSpeedModifier - 1.0f) * (1.0f + inventoryContainer.getContentsWeight() / inventoryContainer.getEffectiveCapacity(this) / 2.0f);
    }
    
    protected float calculateCombatSpeed() {
        float n = 1.0f;
        HandWeapon handWeapon = null;
        if (this.getPrimaryHandItem() != null && this.getPrimaryHandItem() instanceof HandWeapon) {
            handWeapon = (HandWeapon)this.getPrimaryHandItem();
            n *= ((HandWeapon)this.getPrimaryHandItem()).getBaseSpeed();
        }
        final WeaponType weaponType = WeaponType.getWeaponType(this);
        if (handWeapon != null && handWeapon.isTwoHandWeapon() && this.getSecondaryHandItem() != handWeapon) {
            n *= 0.77f;
        }
        if (handWeapon != null && this.Traits.Axeman.isSet() && handWeapon.getCategories().contains("Axe")) {
            n *= this.getChopTreeSpeed();
        }
        float n2 = n - this.getMoodles().getMoodleLevel(MoodleType.Endurance) * 0.07f - this.getMoodles().getMoodleLevel(MoodleType.HeavyLoad) * 0.07f + this.getWeaponLevel() * 0.03f + this.getPerkLevel(PerkFactory.Perks.Fitness) * 0.02f;
        if (this.getSecondaryHandItem() != null && this.getSecondaryHandItem() instanceof InventoryContainer) {
            n2 *= 0.95f;
        }
        float b = n2 * Rand.Next(1.1f, 1.2f) * this.combatSpeedModifier * this.getArmsInjurySpeedModifier();
        if (this.getBodyDamage() != null && this.getBodyDamage().getThermoregulator() != null) {
            b *= this.getBodyDamage().getThermoregulator().getCombatModifier();
        }
        float max = Math.max(0.8f, Math.min(1.6f, b));
        if (handWeapon != null && handWeapon.isTwoHandWeapon() && weaponType.type.equalsIgnoreCase("heavy")) {
            max *= 1.2f;
        }
        return max * GameTime.getAnimSpeedFix();
    }
    
    private float getArmsInjurySpeedModifier() {
        float n = 1.0f;
        final float calculateInjurySpeed = this.calculateInjurySpeed(this.getBodyDamage().getBodyPart(BodyPartType.Hand_R), true);
        if (calculateInjurySpeed > 0.0f) {
            n -= calculateInjurySpeed;
        }
        final float calculateInjurySpeed2 = this.calculateInjurySpeed(this.getBodyDamage().getBodyPart(BodyPartType.ForeArm_R), true);
        if (calculateInjurySpeed2 > 0.0f) {
            n -= calculateInjurySpeed2;
        }
        final float calculateInjurySpeed3 = this.calculateInjurySpeed(this.getBodyDamage().getBodyPart(BodyPartType.UpperArm_R), true);
        if (calculateInjurySpeed3 > 0.0f) {
            n -= calculateInjurySpeed3;
        }
        return n;
    }
    
    private float getFootInjurySpeedModifier() {
        boolean b = true;
        float n = 0.0f;
        float n2 = 0.0f;
        for (int i = BodyPartType.ToIndex(BodyPartType.UpperLeg_L); i < BodyPartType.ToIndex(BodyPartType.MAX); ++i) {
            final float calculateInjurySpeed = this.calculateInjurySpeed(this.getBodyDamage().getBodyPart(BodyPartType.FromIndex(i)), false);
            if (b) {
                n += calculateInjurySpeed;
            }
            else {
                n2 += calculateInjurySpeed;
            }
            b = !b;
        }
        if (n > n2) {
            return -(n + n2);
        }
        return n + n2;
    }
    
    private float calculateInjurySpeed(final BodyPart bodyPart, final boolean b) {
        final float scratchSpeedModifier = bodyPart.getScratchSpeedModifier();
        final float cutSpeedModifier = bodyPart.getCutSpeedModifier();
        final float burnSpeedModifier = bodyPart.getBurnSpeedModifier();
        final float deepWoundSpeedModifier = bodyPart.getDeepWoundSpeedModifier();
        float n = 0.0f;
        if ((bodyPart.getType() == BodyPartType.Foot_L || bodyPart.getType() == BodyPartType.Foot_R) && (bodyPart.getBurnTime() > 5.0f || bodyPart.getBiteTime() > 0.0f || bodyPart.deepWounded() || bodyPart.isSplint() || bodyPart.getFractureTime() > 0.0f || bodyPart.haveGlass())) {
            n = 1.0f;
            if (bodyPart.bandaged()) {
                n = 0.7f;
            }
            if (bodyPart.getFractureTime() > 0.0f) {
                n = this.calcFractureInjurySpeed(bodyPart);
            }
        }
        if (bodyPart.haveBullet()) {
            return 1.0f;
        }
        if (bodyPart.getScratchTime() > 2.0f || bodyPart.getCutTime() > 5.0f || bodyPart.getBurnTime() > 0.0f || bodyPart.getDeepWoundTime() > 0.0f || bodyPart.isSplint() || bodyPart.getFractureTime() > 0.0f || bodyPart.getBiteTime() > 0.0f) {
            n = n + (bodyPart.getScratchTime() / scratchSpeedModifier + bodyPart.getCutTime() / cutSpeedModifier + bodyPart.getBurnTime() / burnSpeedModifier + bodyPart.getDeepWoundTime() / deepWoundSpeedModifier) + bodyPart.getBiteTime() / 20.0f;
            if (bodyPart.bandaged()) {
                n /= 2.0f;
            }
            if (bodyPart.getFractureTime() > 0.0f) {
                n = this.calcFractureInjurySpeed(bodyPart);
            }
        }
        if (b && bodyPart.getPain() > 20.0f) {
            n += bodyPart.getPain() / 10.0f;
        }
        return n;
    }
    
    private float calcFractureInjurySpeed(final BodyPart bodyPart) {
        float b = 0.4f;
        if (bodyPart.getFractureTime() > 10.0f) {
            b = 0.7f;
        }
        if (bodyPart.getFractureTime() > 20.0f) {
            b = 1.0f;
        }
        if (bodyPart.getSplintFactor() > 0.0f) {
            b = b - 0.2f - Math.min(bodyPart.getSplintFactor() / 10.0f, 0.8f);
        }
        return Math.max(0.0f, b);
    }
    
    protected void calculateWalkSpeed() {
        if (this instanceof IsoPlayer && !((IsoPlayer)this).isLocalPlayer()) {
            return;
        }
        final float footInjurySpeedModifier = this.getFootInjurySpeedModifier();
        this.setVariable("WalkInjury", footInjurySpeedModifier);
        final float calculateBaseSpeed = this.calculateBaseSpeed();
        float max;
        if (this.bRunning || this.bSprinting) {
            max = (float)((calculateBaseSpeed - 0.15f) * this.fullSpeedMod + this.getPerkLevel(PerkFactory.Perks.Sprinting) / 20.0f - Math.abs(footInjurySpeedModifier / 1.5));
            if ("Tutorial".equals(Core.GameMode)) {
                max = Math.max(1.0f, max);
            }
        }
        else {
            max = calculateBaseSpeed * this.walkSpeedModifier;
        }
        if (this.getSlowFactor() > 0.0f) {
            max *= 0.05f;
        }
        float min = Math.min(1.0f, max);
        if (this.getBodyDamage() != null && this.getBodyDamage().getThermoregulator() != null) {
            min *= this.getBodyDamage().getThermoregulator().getMovementModifier();
        }
        if (this.isAiming()) {
            this.setVariable("StrafeSpeed", Math.max(Math.min(0.9f + this.getPerkLevel(PerkFactory.Perks.Nimble) / 10.0f, 1.5f) * Math.min(min * 2.5f, 1.0f), 0.6f) * GameTime.getAnimSpeedFix());
        }
        if (this.isInTreesNoBush()) {
            final IsoGridSquare currentSquare = this.getCurrentSquare();
            if (currentSquare != null && currentSquare.Has(IsoObjectType.tree)) {
                final IsoTree tree = currentSquare.getTree();
                if (tree != null) {
                    min *= tree.getSlowFactor(this);
                }
            }
        }
        this.setVariable("WalkSpeed", min * GameTime.getAnimSpeedFix());
    }
    
    public void updateSpeedModifiers() {
        this.runSpeedModifier = 1.0f;
        this.walkSpeedModifier = 1.0f;
        this.combatSpeedModifier = 1.0f;
        this.bagsWorn = new ArrayList<InventoryContainer>();
        for (int i = 0; i < this.getWornItems().size(); ++i) {
            final InventoryItem itemByIndex = this.getWornItems().getItemByIndex(i);
            if (itemByIndex instanceof Clothing) {
                this.combatSpeedModifier += ((Clothing)itemByIndex).getCombatSpeedModifier() - 1.0f;
            }
            if (itemByIndex instanceof InventoryContainer) {
                final InventoryContainer e = (InventoryContainer)itemByIndex;
                this.combatSpeedModifier += e.getScriptItem().combatSpeedModifier - 1.0f;
                this.bagsWorn.add(e);
            }
        }
        final InventoryItem item = this.getWornItems().getItem("Shoes");
        if (item == null || item.getCondition() == 0) {
            this.runSpeedModifier *= 0.85f;
            this.walkSpeedModifier *= 0.85f;
        }
    }
    
    public void DoFloorSplat(final IsoGridSquare isoGridSquare, final String s, final boolean flip, final float n, final float n2) {
        if (isoGridSquare == null) {
            return;
        }
        isoGridSquare.DirtySlice();
        IsoObject isoObject = null;
        for (int i = 0; i < isoGridSquare.getObjects().size(); ++i) {
            final IsoObject isoObject2 = isoGridSquare.getObjects().get(i);
            if (isoObject2.sprite != null && isoObject2.sprite.getProperties().Is(IsoFlagType.solidfloor) && isoObject == null) {
                isoObject = isoObject2;
            }
        }
        if (isoObject != null && isoObject.sprite != null && (isoObject.sprite.getProperties().Is(IsoFlagType.vegitation) || isoObject.sprite.getProperties().Is(IsoFlagType.solidfloor))) {
            final IsoSprite sprite = IsoSprite.getSprite(IsoSpriteManager.instance, s, 0);
            if (sprite == null) {
                return;
            }
            if (isoObject.AttachedAnimSprite.size() > 7) {
                return;
            }
            isoObject.AttachedAnimSprite.add(IsoSpriteInstance.get(sprite));
            isoObject.AttachedAnimSprite.get(isoObject.AttachedAnimSprite.size() - 1).Flip = flip;
            isoObject.AttachedAnimSprite.get(isoObject.AttachedAnimSprite.size() - 1).tintr = 0.5f + Rand.Next(100) / 2000.0f;
            isoObject.AttachedAnimSprite.get(isoObject.AttachedAnimSprite.size() - 1).tintg = 0.7f + Rand.Next(300) / 1000.0f;
            isoObject.AttachedAnimSprite.get(isoObject.AttachedAnimSprite.size() - 1).tintb = 0.7f + Rand.Next(300) / 1000.0f;
            isoObject.AttachedAnimSprite.get(isoObject.AttachedAnimSprite.size() - 1).SetAlpha(0.4f * n2 * 0.6f);
            isoObject.AttachedAnimSprite.get(isoObject.AttachedAnimSprite.size() - 1).SetTargetAlpha(0.4f * n2 * 0.6f);
            isoObject.AttachedAnimSprite.get(isoObject.AttachedAnimSprite.size() - 1).offZ = -n;
            isoObject.AttachedAnimSprite.get(isoObject.AttachedAnimSprite.size() - 1).offX = 0.0f;
        }
    }
    
    void DoSplat(final IsoGridSquare isoGridSquare, final String s, final boolean b, final IsoFlagType isoFlagType, final float n, final float n2, final float n3) {
        if (isoGridSquare == null) {
            return;
        }
        isoGridSquare.DoSplat(s, b, isoFlagType, n, n2, n3);
    }
    
    public boolean onMouseLeftClick(final int n, final int n2) {
        if (IsoCamera.CamCharacter != IsoPlayer.getInstance() && Core.bDebug) {
            IsoCamera.CamCharacter = this;
        }
        return super.onMouseLeftClick(n, n2);
    }
    
    protected void calculateStats() {
        if (GameServer.bServer) {
            this.stats.fatigue = 0.0f;
        }
        else if (GameClient.bClient && (!ServerOptions.instance.SleepAllowed.getValue() || !ServerOptions.instance.SleepNeeded.getValue())) {
            this.stats.fatigue = 0.0f;
        }
        if (LuaHookManager.TriggerHook("CalculateStats", this)) {
            return;
        }
        this.updateEndurance();
        this.updateTripping();
        this.updateThirst();
        this.updateStress();
        this.updateStats_WakeState();
        this.stats.endurance = PZMath.clamp(this.stats.endurance, 0.0f, 1.0f);
        this.stats.hunger = PZMath.clamp(this.stats.hunger, 0.0f, 1.0f);
        this.stats.stress = PZMath.clamp(this.stats.stress, 0.0f, 1.0f);
        this.stats.fatigue = PZMath.clamp(this.stats.fatigue, 0.0f, 1.0f);
        this.updateMorale();
        this.updateFitness();
    }
    
    protected void updateStats_WakeState() {
        if (IsoPlayer.getInstance() == this && this.Asleep) {
            this.updateStats_Sleeping();
        }
        else {
            this.updateStats_Awake();
        }
    }
    
    protected void updateStats_Sleeping() {
    }
    
    protected void updateStats_Awake() {
        final Stats stats = this.stats;
        stats.stress -= (float)(ZomboidGlobals.StressReduction * GameTime.instance.getMultiplier() * GameTime.instance.getDeltaMinutesPerDay());
        float n = 1.0f - this.stats.endurance;
        if (n < 0.3f) {
            n = 0.3f;
        }
        float n2 = 1.0f;
        if (this.Traits.NeedsLessSleep.isSet()) {
            n2 = 0.7f;
        }
        if (this.Traits.NeedsMoreSleep.isSet()) {
            n2 = 1.3f;
        }
        if (SandboxOptions.instance.getStatsDecreaseMultiplier() < 1.0) {}
        final Stats stats2 = this.stats;
        stats2.fatigue += (float)(ZomboidGlobals.FatigueIncrease * SandboxOptions.instance.getStatsDecreaseMultiplier() * n * GameTime.instance.getMultiplier() * GameTime.instance.getDeltaMinutesPerDay() * n2 * this.getFatiqueMultiplier());
        final float appetiteMultiplier = this.getAppetiteMultiplier();
        if ((this instanceof IsoPlayer && ((IsoPlayer)this).IsRunning()) || this.isCurrentState(SwipeStatePlayer.instance())) {
            if (this.Moodles.getMoodleLevel(MoodleType.FoodEaten) == 0) {
                final Stats stats3 = this.stats;
                stats3.hunger += (float)(ZomboidGlobals.HungerIncreaseWhenExercise / 3.0 * SandboxOptions.instance.getStatsDecreaseMultiplier() * appetiteMultiplier * GameTime.instance.getMultiplier() * GameTime.instance.getDeltaMinutesPerDay() * this.getHungerMultiplier());
            }
            else {
                final Stats stats4 = this.stats;
                stats4.hunger += (float)(ZomboidGlobals.HungerIncreaseWhenExercise * SandboxOptions.instance.getStatsDecreaseMultiplier() * appetiteMultiplier * GameTime.instance.getMultiplier() * GameTime.instance.getDeltaMinutesPerDay() * this.getHungerMultiplier());
            }
        }
        else if (this.Moodles.getMoodleLevel(MoodleType.FoodEaten) == 0) {
            final Stats stats5 = this.stats;
            stats5.hunger += (float)(ZomboidGlobals.HungerIncrease * SandboxOptions.instance.getStatsDecreaseMultiplier() * appetiteMultiplier * GameTime.instance.getMultiplier() * GameTime.instance.getDeltaMinutesPerDay() * this.getHungerMultiplier());
        }
        else {
            final Stats stats6 = this.stats;
            stats6.hunger += (float)((float)ZomboidGlobals.HungerIncreaseWhenWellFed * SandboxOptions.instance.getStatsDecreaseMultiplier() * GameTime.instance.getMultiplier() * GameTime.instance.getDeltaMinutesPerDay() * this.getHungerMultiplier());
        }
        if (this.getCurrentSquare() == this.getLastSquare() && !this.isReading()) {
            final Stats stats7 = this.stats;
            stats7.idleboredom += 5.0E-5f * GameTime.instance.getMultiplier() * GameTime.instance.getDeltaMinutesPerDay();
            final Stats stats8 = this.stats;
            stats8.idleboredom += 0.00125f * GameTime.instance.getMultiplier() * GameTime.instance.getDeltaMinutesPerDay();
        }
        if (this.getCurrentSquare() != null && this.getLastSquare() != null && this.getCurrentSquare().getRoom() == this.getLastSquare().getRoom() && this.getCurrentSquare().getRoom() != null && !this.isReading()) {
            final Stats stats9 = this.stats;
            stats9.idleboredom += 1.0E-4f * GameTime.instance.getMultiplier() * GameTime.instance.getDeltaMinutesPerDay();
            final Stats stats10 = this.stats;
            stats10.idleboredom += 0.00125f * GameTime.instance.getMultiplier() * GameTime.instance.getDeltaMinutesPerDay();
        }
    }
    
    private void updateMorale() {
        float n = (1.0f - this.stats.getStress() - 0.5f) * 1.0E-4f;
        if (n > 0.0f) {
            n += 0.5f;
        }
        final Stats stats = this.stats;
        stats.morale += n;
        this.stats.morale = PZMath.clamp(this.stats.morale, 0.0f, 1.0f);
    }
    
    private void updateFitness() {
        this.stats.fitness = this.getPerkLevel(PerkFactory.Perks.Fitness) / 5.0f - 1.0f;
        if (this.stats.fitness > 1.0f) {
            this.stats.fitness = 1.0f;
        }
        if (this.stats.fitness < -1.0f) {
            this.stats.fitness = -1.0f;
        }
    }
    
    private void updateTripping() {
        if (this.stats.Tripping) {
            final Stats stats = this.stats;
            stats.TrippingRotAngle += 0.06f;
        }
        else {
            final Stats stats2 = this.stats;
            stats2.TrippingRotAngle += 0.0f;
        }
    }
    
    protected float getAppetiteMultiplier() {
        float n = 1.0f - this.stats.hunger;
        if (this.Traits.HeartyAppitite.isSet()) {
            n *= 1.5f;
        }
        if (this.Traits.LightEater.isSet()) {
            n *= 0.75f;
        }
        return n;
    }
    
    private void updateStress() {
        if (this.Traits.Cowardly.isSet()) {}
        if (this.Traits.Brave.isSet()) {}
        if (this.stats.Panic > 100.0f) {
            this.stats.Panic = 100.0f;
        }
        final Stats stats = this.stats;
        stats.stress += (float)(WorldSoundManager.instance.getStressFromSounds((int)this.getX(), (int)this.getY(), (int)this.getZ()) * ZomboidGlobals.StressFromSoundsMultiplier);
        if (this.BodyDamage.getNumPartsBitten() > 0) {
            final Stats stats2 = this.stats;
            stats2.stress += (float)(ZomboidGlobals.StressFromBiteOrScratch * GameTime.instance.getMultiplier() * GameTime.instance.getDeltaMinutesPerDay());
        }
        if (this.BodyDamage.getNumPartsScratched() > 0) {
            final Stats stats3 = this.stats;
            stats3.stress += (float)(ZomboidGlobals.StressFromBiteOrScratch * GameTime.instance.getMultiplier() * GameTime.instance.getDeltaMinutesPerDay());
        }
        if (this.BodyDamage.IsInfected() || this.BodyDamage.IsFakeInfected()) {
            final Stats stats4 = this.stats;
            stats4.stress += (float)(ZomboidGlobals.StressFromBiteOrScratch * GameTime.instance.getMultiplier() * GameTime.instance.getDeltaMinutesPerDay());
        }
        if (this.Traits.Hemophobic.isSet()) {
            final Stats stats5 = this.stats;
            stats5.stress += (float)(this.getTotalBlood() * ZomboidGlobals.StressFromHemophobic * (GameTime.instance.getMultiplier() / 0.8f) * GameTime.instance.getDeltaMinutesPerDay());
        }
        if (this.Traits.Brooding.isSet()) {
            final Stats stats6 = this.stats;
            stats6.Anger -= (float)(ZomboidGlobals.AngerDecrease * ZomboidGlobals.BroodingAngerDecreaseMultiplier * GameTime.instance.getMultiplier() * GameTime.instance.getDeltaMinutesPerDay());
        }
        else {
            final Stats stats7 = this.stats;
            stats7.Anger -= (float)(ZomboidGlobals.AngerDecrease * GameTime.instance.getMultiplier() * GameTime.instance.getDeltaMinutesPerDay());
        }
        this.stats.Anger = PZMath.clamp(this.stats.Anger, 0.0f, 1.0f);
    }
    
    private void updateEndurance() {
        this.stats.endurance = PZMath.clamp(this.stats.endurance, 0.0f, 1.0f);
        this.stats.endurancelast = this.stats.endurance;
        if (this.isUnlimitedEndurance()) {
            this.stats.endurance = 1.0f;
        }
    }
    
    private void updateThirst() {
        float n = 1.0f;
        if (this.Traits.HighThirst.isSet()) {
            n *= 2.0;
        }
        if (this.Traits.LowThirst.isSet()) {
            n *= 0.5;
        }
        if (IsoPlayer.getInstance() == this && !IsoPlayer.getInstance().isGhostMode()) {
            if (this.Asleep) {
                final Stats stats = this.stats;
                stats.thirst += (float)(ZomboidGlobals.ThirstSleepingIncrease * SandboxOptions.instance.getStatsDecreaseMultiplier() * GameTime.instance.getMultiplier() * GameTime.instance.getDeltaMinutesPerDay() * n);
            }
            else {
                final Stats stats2 = this.stats;
                stats2.thirst += (float)(ZomboidGlobals.ThirstIncrease * SandboxOptions.instance.getStatsDecreaseMultiplier() * GameTime.instance.getMultiplier() * this.getRunningThirstReduction() * GameTime.instance.getDeltaMinutesPerDay() * n * this.getThirstMultiplier());
            }
            if (this.stats.thirst > 1.0f) {
                this.stats.thirst = 1.0f;
            }
        }
        this.autoDrink();
    }
    
    private double getRunningThirstReduction() {
        if (this == IsoPlayer.getInstance() && IsoPlayer.getInstance().IsRunning()) {
            return 1.2;
        }
        return 1.0;
    }
    
    public void faceLocation(final float n, final float n2) {
        IsoGameCharacter.tempo.x = n + 0.5f;
        IsoGameCharacter.tempo.y = n2 + 0.5f;
        final Vector2 tempo = IsoGameCharacter.tempo;
        tempo.x -= this.getX();
        final Vector2 tempo2 = IsoGameCharacter.tempo;
        tempo2.y -= this.getY();
        this.DirectionFromVector(IsoGameCharacter.tempo);
        this.getVectorFromDirection(this.m_forwardDirection);
        final AnimationPlayer animationPlayer = this.getAnimationPlayer();
        if (animationPlayer != null && animationPlayer.isReady()) {
            animationPlayer.UpdateDir(this);
        }
    }
    
    public void faceLocationF(final float x, final float y) {
        IsoGameCharacter.tempo.x = x;
        IsoGameCharacter.tempo.y = y;
        final Vector2 tempo = IsoGameCharacter.tempo;
        tempo.x -= this.getX();
        final Vector2 tempo2 = IsoGameCharacter.tempo;
        tempo2.y -= this.getY();
        if (IsoGameCharacter.tempo.getLengthSquared() == 0.0f) {
            return;
        }
        this.DirectionFromVector(IsoGameCharacter.tempo);
        IsoGameCharacter.tempo.normalize();
        this.m_forwardDirection.set(IsoGameCharacter.tempo.x, IsoGameCharacter.tempo.y);
        final AnimationPlayer animationPlayer = this.getAnimationPlayer();
        if (animationPlayer != null && animationPlayer.isReady()) {
            animationPlayer.UpdateDir(this);
        }
    }
    
    public boolean isFacingLocation(final float n, final float n2, final float n3) {
        final Vector2 set = BaseVehicle.allocVector2().set(n - this.getX(), n2 - this.getY());
        set.normalize();
        final Vector2 lookVector = this.getLookVector(BaseVehicle.allocVector2());
        final float dot = set.dot(lookVector);
        BaseVehicle.releaseVector2(set);
        BaseVehicle.releaseVector2(lookVector);
        return dot >= n3;
    }
    
    public boolean isFacingObject(final IsoObject isoObject, final float n) {
        final Vector2 allocVector2 = BaseVehicle.allocVector2();
        isoObject.getFacingPosition(allocVector2);
        final boolean facingLocation = this.isFacingLocation(allocVector2.x, allocVector2.y, n);
        BaseVehicle.releaseVector2(allocVector2);
        return facingLocation;
    }
    
    private void checkDrawWeaponPre(final float n, final float n2, final float n3, final ColorInfo colorInfo) {
        if (this.sprite == null) {
            return;
        }
        if (this.sprite.CurrentAnim == null) {
            return;
        }
        if (this.sprite.CurrentAnim.name == null) {
            return;
        }
        if (this.dir == IsoDirections.S || this.dir == IsoDirections.SE || this.dir == IsoDirections.E || this.dir == IsoDirections.NE || this.dir == IsoDirections.SW) {
            return;
        }
        if (!this.sprite.CurrentAnim.name.contains("Attack_")) {
            return;
        }
    }
    
    public void splatBlood(final int n, final float n2) {
        if (this.getCurrentSquare() == null) {
            return;
        }
        this.getCurrentSquare().splatBlood(n, n2);
    }
    
    public boolean isOutside() {
        return this.getCurrentSquare() != null && this.getCurrentSquare().isOutside();
    }
    
    public boolean isFemale() {
        return this.bFemale;
    }
    
    public void setFemale(final boolean bFemale) {
        this.bFemale = bFemale;
    }
    
    public boolean isZombie() {
        return false;
    }
    
    public int getLastHitCount() {
        return this.lastHitCount;
    }
    
    public void setLastHitCount(final int lastHitCount) {
        this.lastHitCount = lastHitCount;
    }
    
    public int getSurvivorKills() {
        return this.SurvivorKills;
    }
    
    public void setSurvivorKills(final int survivorKills) {
        this.SurvivorKills = survivorKills;
    }
    
    public int getAge() {
        return this.age;
    }
    
    public void setAge(final int age) {
        this.age = age;
    }
    
    public void exert(float n) {
        if (this.Traits.PlaysFootball.isSet()) {
            n *= 0.9f;
        }
        if (this.Traits.Jogger.isSet()) {
            n *= 0.9f;
        }
        final Stats stats = this.stats;
        stats.endurance -= n;
    }
    
    public PerkInfo getPerkInfo(final PerkFactory.Perk perk) {
        for (int i = 0; i < this.PerkList.size(); ++i) {
            final PerkInfo perkInfo = this.PerkList.get(i);
            if (perkInfo.perk == perk) {
                return perkInfo;
            }
        }
        return null;
    }
    
    public void HitSilence(final HandWeapon handWeapon, final IsoGameCharacter attackedBy, boolean b, float n) {
        if (attackedBy == null || handWeapon == null) {
            return;
        }
        if (this.getStateMachine().getCurrent() == StaggerBackState.instance()) {
            return;
        }
        if (this.isOnFloor()) {
            n = 2.0f;
            b = false;
            this.setReanimateTimer(this.getReanimateTimer() + 38.0f);
        }
        if (handWeapon.getName().contains("Bare Hands") || (attackedBy instanceof IsoPlayer && ((IsoPlayer)attackedBy).bDoShove)) {
            b = true;
            this.noDamage = true;
        }
        this.staggerTimeMod = handWeapon.getPushBackMod() * handWeapon.getKnockbackMod(attackedBy) * attackedBy.getShovingMod();
        float n2;
        if (attackedBy instanceof IsoPlayer && !handWeapon.bIsAimedFirearm) {
            float useChargeDelta = ((IsoPlayer)attackedBy).useChargeDelta;
            if (useChargeDelta > 1.0f) {
                useChargeDelta = 1.0f;
            }
            if (useChargeDelta < 0.0f) {
                useChargeDelta = 0.0f;
            }
            n2 = handWeapon.getMinDamage() + (handWeapon.getMaxDamage() - handWeapon.getMinDamage()) * useChargeDelta;
        }
        else {
            n2 = Rand.Next((int)((handWeapon.getMaxDamage() - handWeapon.getMinDamage()) * 1000.0f)) / 1000.0f + handWeapon.getMinDamage();
        }
        float n3 = n2 * n;
        float hitForce = n3 * handWeapon.getKnockbackMod(attackedBy) * attackedBy.getShovingMod();
        if (hitForce > 1.0f) {
            hitForce = 1.0f;
        }
        this.setHitForce(hitForce);
        this.setAttackedBy(attackedBy);
        if (1.0f - (IsoUtils.DistanceTo(attackedBy.getX(), attackedBy.getY(), this.getX(), this.getY()) - handWeapon.getMinRange()) / handWeapon.getMaxRange(attackedBy) > 1.0f) {}
        this.hitDir.x = this.getX();
        this.hitDir.y = this.getY();
        final Vector2 hitDir = this.hitDir;
        hitDir.x -= attackedBy.getX();
        final Vector2 hitDir2 = this.hitDir;
        hitDir2.y -= attackedBy.getY();
        this.getHitDir().normalize();
        final Vector2 hitDir3 = this.hitDir;
        hitDir3.x *= handWeapon.getPushBackMod();
        final Vector2 hitDir4 = this.hitDir;
        hitDir4.y *= handWeapon.getPushBackMod();
        this.hitDir.rotate(handWeapon.HitAngleMod);
        final float n4 = attackedBy.stats.endurance * attackedBy.knockbackAttackMod;
        if (n4 < 0.5f) {
            float n5 = n4 * 1.3f;
            if (n5 < 0.4f) {
                n5 = 0.4f;
            }
            this.setHitForce(this.getHitForce() * n5);
        }
        if (!handWeapon.isRangeFalloff()) {}
        if (attackedBy instanceof IsoPlayer) {
            this.setHitForce(this.getHitForce() * 2.0f);
        }
        final Vector2 set = IsoGameCharacter.tempVector2_1.set(this.getX(), this.getY());
        final Vector2 set2 = IsoGameCharacter.tempVector2_2.set(attackedBy.getX(), attackedBy.getY());
        final Vector2 vector2 = set;
        vector2.x -= set2.x;
        final Vector2 vector3 = set;
        vector3.y -= set2.y;
        final Vector2 vectorFromDirection = this.getVectorFromDirection(IsoGameCharacter.tempVector2_2);
        set.normalize();
        if (set.dot(vectorFromDirection) > -0.3f) {
            n3 *= 1.5f;
        }
        if (attackedBy.isCriticalHit()) {
            n3 *= 10.0f;
        }
        if (!this.isOnFloor() && handWeapon.getScriptItem().Categories.contains("Axe")) {
            n3 *= 2.0f;
        }
        if (!b) {
            if (handWeapon.isAimedFirearm()) {
                this.Health -= n3 * 0.7f;
            }
            else {
                this.Health -= n3 * 0.15f;
            }
        }
        float n6 = 12.0f;
        if (attackedBy instanceof IsoPlayer) {
            final int moodleLevel = ((IsoPlayer)attackedBy).Moodles.getMoodleLevel(MoodleType.Endurance);
            if (moodleLevel == 4) {
                n6 = 50.0f;
            }
            else if (moodleLevel == 3) {
                n6 = 35.0f;
            }
            else if (moodleLevel == 2) {
                n6 = 24.0f;
            }
            else if (moodleLevel == 1) {
                n6 = 16.0f;
            }
        }
        if (handWeapon.getKnockdownMod() <= 0.0f) {
            handWeapon.setKnockdownMod(1.0f);
        }
        float n7 = n6 / handWeapon.getKnockdownMod();
        if (attackedBy instanceof IsoPlayer && !handWeapon.isAimedHandWeapon()) {
            n7 *= 2.0f - ((IsoPlayer)attackedBy).useChargeDelta;
        }
        if (n7 < 1.0f) {
            n7 = 1.0f;
        }
        final boolean b2 = Rand.Next((int)n7) == 0;
        if (this.isDead() || ((handWeapon.isAlwaysKnockdown() || b2) && this.isZombie())) {
            this.DoDeathSilence(handWeapon, attackedBy);
        }
    }
    
    protected void DoDeathSilence(final HandWeapon handWeapon, final IsoGameCharacter isoGameCharacter) {
        if (this.isDead()) {
            if (handWeapon != null) {
                int splatNumber = handWeapon.getSplatNumber();
                if (splatNumber < 1) {
                    splatNumber = 1;
                }
                if (Core.bLastStand) {
                    splatNumber *= 3;
                }
                for (int i = 0; i < splatNumber; ++i) {
                    this.splatBlood(3, 0.3f);
                }
            }
            this.splatBloodFloorBig();
            if (isoGameCharacter != null && isoGameCharacter.xp != null) {
                isoGameCharacter.xp.AddXP(handWeapon, 3);
            }
            IsoGameCharacter.tempo.x = this.getHitDir().x;
            IsoGameCharacter.tempo.y = this.getHitDir().y;
        }
        if (this.isDead() && this.getCurrentSquare() != null) {
            if (GameServer.bServer && this.isZombie()) {
                GameServer.sendZombieDeath((IsoZombie)this);
            }
            new IsoDeadBody(this);
        }
        this.setStateMachineLocked(true);
    }
    
    public boolean isEquipped(final InventoryItem inventoryItem) {
        return this.isEquippedClothing(inventoryItem) || this.isHandItem(inventoryItem);
    }
    
    public boolean isEquippedClothing(final InventoryItem inventoryItem) {
        return this.wornItems.contains(inventoryItem);
    }
    
    public boolean isAttachedItem(final InventoryItem inventoryItem) {
        return this.getAttachedItems().contains(inventoryItem);
    }
    
    public void faceThisObject(final IsoObject isoObject) {
        if (isoObject == null) {
            return;
        }
        final Vector2 tempo = IsoGameCharacter.tempo;
        final BaseVehicle baseVehicle = Type.tryCastTo(isoObject, BaseVehicle.class);
        final BarricadeAble barricadeAble = Type.tryCastTo(isoObject, BarricadeAble.class);
        if (baseVehicle != null) {
            baseVehicle.getFacingPosition(this, tempo);
            final Vector2 vector2 = tempo;
            vector2.x -= this.getX();
            final Vector2 vector3 = tempo;
            vector3.y -= this.getY();
            this.DirectionFromVector(tempo);
            tempo.normalize();
            this.m_forwardDirection.set(tempo.x, tempo.y);
        }
        else if (barricadeAble != null && this.current == barricadeAble.getSquare()) {
            this.dir = (barricadeAble.getNorth() ? IsoDirections.N : IsoDirections.W);
            this.getVectorFromDirection(this.m_forwardDirection);
        }
        else if (barricadeAble != null && this.current == barricadeAble.getOppositeSquare()) {
            this.dir = (barricadeAble.getNorth() ? IsoDirections.S : IsoDirections.E);
            this.getVectorFromDirection(this.m_forwardDirection);
        }
        else {
            isoObject.getFacingPosition(tempo);
            final Vector2 vector4 = tempo;
            vector4.x -= this.getX();
            final Vector2 vector5 = tempo;
            vector5.y -= this.getY();
            this.DirectionFromVector(tempo);
            this.getVectorFromDirection(this.m_forwardDirection);
        }
        final AnimationPlayer animationPlayer = this.getAnimationPlayer();
        if (animationPlayer != null && animationPlayer.isReady()) {
            animationPlayer.UpdateDir(this);
        }
    }
    
    public void facePosition(final int n, final int n2) {
        IsoGameCharacter.tempo.x = (float)n;
        IsoGameCharacter.tempo.y = (float)n2;
        final Vector2 tempo = IsoGameCharacter.tempo;
        tempo.x -= this.getX();
        final Vector2 tempo2 = IsoGameCharacter.tempo;
        tempo2.y -= this.getY();
        this.DirectionFromVector(IsoGameCharacter.tempo);
        this.getVectorFromDirection(this.m_forwardDirection);
        final AnimationPlayer animationPlayer = this.getAnimationPlayer();
        if (animationPlayer != null && animationPlayer.isReady()) {
            animationPlayer.UpdateDir(this);
        }
    }
    
    public void faceThisObjectAlt(final IsoObject isoObject) {
        if (isoObject == null) {
            return;
        }
        isoObject.getFacingPositionAlt(IsoGameCharacter.tempo);
        final Vector2 tempo = IsoGameCharacter.tempo;
        tempo.x -= this.getX();
        final Vector2 tempo2 = IsoGameCharacter.tempo;
        tempo2.y -= this.getY();
        this.DirectionFromVector(IsoGameCharacter.tempo);
        this.getVectorFromDirection(this.m_forwardDirection);
        final AnimationPlayer animationPlayer = this.getAnimationPlayer();
        if (animationPlayer != null && animationPlayer.isReady()) {
            animationPlayer.UpdateDir(this);
        }
    }
    
    public void setAnimated(final boolean b) {
        this.legsSprite.Animate = true;
    }
    
    public void playHurtSound() {
        this.getEmitter().playVocals(this.getHurtSound());
    }
    
    public void playDeadSound() {
        if (this.isCloseKilled()) {
            this.getEmitter().playSoundImpl("HeadStab", this);
        }
        else {
            this.getEmitter().playSoundImpl("HeadSmash", this);
        }
        if (this.isZombie()) {
            ((IsoZombie)this).parameterZombieState.setState(ParameterZombieState.State.Death);
        }
    }
    
    public void saveChange(final String s, final KahluaTable kahluaTable, final ByteBuffer byteBuffer) {
        super.saveChange(s, kahluaTable, byteBuffer);
        if ("addItem".equals(s)) {
            if (kahluaTable != null && kahluaTable.rawget((Object)"item") instanceof InventoryItem) {
                final InventoryItem inventoryItem = (InventoryItem)kahluaTable.rawget((Object)"item");
                try {
                    inventoryItem.saveWithSize(byteBuffer, false);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        else if ("addItemOfType".equals(s)) {
            if (kahluaTable != null && kahluaTable.rawget((Object)"type") instanceof String) {
                GameWindow.WriteStringUTF(byteBuffer, (String)kahluaTable.rawget((Object)"type"));
                if (kahluaTable.rawget((Object)"count") instanceof Double) {
                    byteBuffer.putShort(((Double)kahluaTable.rawget((Object)"count")).shortValue());
                }
                else {
                    byteBuffer.putShort((short)1);
                }
            }
        }
        else if ("AddRandomDamageFromZombie".equals(s)) {
            if (kahluaTable != null && kahluaTable.rawget((Object)"zombie") instanceof Double) {
                byteBuffer.putShort(((Double)kahluaTable.rawget((Object)"zombie")).shortValue());
            }
        }
        else if (!"AddZombieKill".equals(s)) {
            if ("DamageFromWeapon".equals(s)) {
                if (kahluaTable != null && kahluaTable.rawget((Object)"weapon") instanceof String) {
                    GameWindow.WriteStringUTF(byteBuffer, (String)kahluaTable.rawget((Object)"weapon"));
                }
            }
            else if ("removeItem".equals(s)) {
                if (kahluaTable != null && kahluaTable.rawget((Object)"item") instanceof Double) {
                    byteBuffer.putInt(((Double)kahluaTable.rawget((Object)"item")).intValue());
                }
            }
            else if ("removeItemID".equals(s)) {
                if (kahluaTable != null && kahluaTable.rawget((Object)"id") instanceof Double) {
                    byteBuffer.putInt(((Double)kahluaTable.rawget((Object)"id")).intValue());
                }
                if (kahluaTable != null && kahluaTable.rawget((Object)"type") instanceof String) {
                    GameWindow.WriteStringUTF(byteBuffer, (String)kahluaTable.rawget((Object)"type"));
                }
                else {
                    GameWindow.WriteStringUTF(byteBuffer, null);
                }
            }
            else if ("removeItemType".equals(s)) {
                if (kahluaTable != null && kahluaTable.rawget((Object)"type") instanceof String) {
                    GameWindow.WriteStringUTF(byteBuffer, (String)kahluaTable.rawget((Object)"type"));
                    if (kahluaTable.rawget((Object)"count") instanceof Double) {
                        byteBuffer.putShort(((Double)kahluaTable.rawget((Object)"count")).shortValue());
                    }
                    else {
                        byteBuffer.putShort((short)1);
                    }
                }
            }
            else if ("removeOneOf".equals(s)) {
                if (kahluaTable != null && kahluaTable.rawget((Object)"type") instanceof String) {
                    GameWindow.WriteStringUTF(byteBuffer, (String)kahluaTable.rawget((Object)"type"));
                }
            }
            else if ("reanimatedID".equals(s)) {
                if (kahluaTable != null && kahluaTable.rawget((Object)"ID") instanceof Double) {
                    byteBuffer.putInt(((Double)kahluaTable.rawget((Object)"ID")).intValue());
                }
            }
            else if ("Shove".equals(s)) {
                if (kahluaTable != null && kahluaTable.rawget((Object)"hitDirX") instanceof Double && kahluaTable.rawget((Object)"hitDirY") instanceof Double && kahluaTable.rawget((Object)"force") instanceof Double) {
                    byteBuffer.putFloat(((Double)kahluaTable.rawget((Object)"hitDirX")).floatValue());
                    byteBuffer.putFloat(((Double)kahluaTable.rawget((Object)"hitDirY")).floatValue());
                    byteBuffer.putFloat(((Double)kahluaTable.rawget((Object)"force")).floatValue());
                }
            }
            else if ("addXp".equals(s)) {
                if (kahluaTable != null && kahluaTable.rawget((Object)"perk") instanceof Double && kahluaTable.rawget((Object)"xp") instanceof Double) {
                    byteBuffer.putInt(((Double)kahluaTable.rawget((Object)"perk")).intValue());
                    byteBuffer.putInt(((Double)kahluaTable.rawget((Object)"xp")).intValue());
                    byteBuffer.put((byte)(Boolean.TRUE.equals(kahluaTable.rawget((Object)"noMultiplier")) ? 1 : 0));
                }
            }
            else if (!"wakeUp".equals(s)) {
                if ("mechanicActionDone".equals(s) && kahluaTable != null) {
                    byteBuffer.put((byte)(((boolean)kahluaTable.rawget((Object)"success")) ? 1 : 0));
                    byteBuffer.putInt(((Double)kahluaTable.rawget((Object)"vehicleId")).intValue());
                    GameWindow.WriteString(byteBuffer, (String)kahluaTable.rawget((Object)"partId"));
                    byteBuffer.put((byte)(((boolean)kahluaTable.rawget((Object)"installing")) ? 1 : 0));
                    byteBuffer.putLong(((Double)kahluaTable.rawget((Object)"itemId")).longValue());
                }
            }
        }
    }
    
    public void loadChange(final String anObject, final ByteBuffer byteBuffer) {
        super.loadChange(anObject, byteBuffer);
        if ("addItem".equals(anObject)) {
            try {
                final InventoryItem loadItem = InventoryItem.loadItem(byteBuffer, 186);
                if (loadItem != null) {
                    this.getInventory().AddItem(loadItem);
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        else if ("addItemOfType".equals(anObject)) {
            final String readStringUTF = GameWindow.ReadStringUTF(byteBuffer);
            for (short short1 = byteBuffer.getShort(), n = 0; n < short1; ++n) {
                this.getInventory().AddItem(readStringUTF);
            }
        }
        else if ("AddRandomDamageFromZombie".equals(anObject)) {
            final IsoZombie zombie = GameClient.getZombie(byteBuffer.getShort());
            if (zombie != null && !this.isDead()) {
                this.getBodyDamage().AddRandomDamageFromZombie(zombie, null);
                this.getBodyDamage().Update();
                if (this.isDead()) {
                    if (this.isFemale()) {
                        zombie.getEmitter().playSound("FemaleBeingEatenDeath");
                    }
                    else {
                        zombie.getEmitter().playSound("MaleBeingEatenDeath");
                    }
                }
            }
        }
        else if ("AddZombieKill".equals(anObject)) {
            this.setZombieKills(this.getZombieKills() + 1);
        }
        else if ("DamageFromWeapon".equals(anObject)) {
            final InventoryItem createItem = InventoryItemFactory.CreateItem(GameWindow.ReadStringUTF(byteBuffer));
            if (createItem instanceof HandWeapon) {
                this.getBodyDamage().DamageFromWeapon((HandWeapon)createItem);
            }
        }
        else if ("exitVehicle".equals(anObject)) {
            final BaseVehicle vehicle = this.getVehicle();
            if (vehicle != null) {
                vehicle.exit(this);
                this.setVehicle(null);
            }
        }
        else if ("removeItem".equals(anObject)) {
            final int int1 = byteBuffer.getInt();
            if (int1 >= 0 && int1 < this.getInventory().getItems().size()) {
                final InventoryItem inventoryItem = this.getInventory().getItems().get(int1);
                this.removeFromHands(inventoryItem);
                this.getInventory().Remove(inventoryItem);
            }
        }
        else if ("removeItemID".equals(anObject)) {
            final int int2 = byteBuffer.getInt();
            final String readStringUTF2 = GameWindow.ReadStringUTF(byteBuffer);
            final InventoryItem itemWithID = this.getInventory().getItemWithID(int2);
            if (itemWithID != null && itemWithID.getFullType().equals(readStringUTF2)) {
                this.removeFromHands(itemWithID);
                this.getInventory().Remove(itemWithID);
            }
        }
        else if ("removeItemType".equals(anObject)) {
            final String readStringUTF3 = GameWindow.ReadStringUTF(byteBuffer);
            for (short short2 = byteBuffer.getShort(), n2 = 0; n2 < short2; ++n2) {
                this.getInventory().RemoveOneOf(readStringUTF3);
            }
        }
        else if ("removeOneOf".equals(anObject)) {
            this.getInventory().RemoveOneOf(GameWindow.ReadStringUTF(byteBuffer));
        }
        else if ("reanimatedID".equals(anObject)) {
            this.ReanimatedCorpseID = byteBuffer.getInt();
        }
        else if (!"Shove".equals(anObject)) {
            if ("StopBurning".equals(anObject)) {
                this.StopBurning();
            }
            else if ("addXp".equals(anObject)) {
                final PerkFactory.Perk fromIndex = PerkFactory.Perks.fromIndex(byteBuffer.getInt());
                final int int3 = byteBuffer.getInt();
                if (byteBuffer.get() == 1) {
                    this.getXp().AddXPNoMultiplier(fromIndex, (float)int3);
                }
                else {
                    this.getXp().AddXP(fromIndex, (float)int3);
                }
            }
            else if ("wakeUp".equals(anObject)) {
                if (this.isAsleep()) {
                    this.Asleep = false;
                    this.ForceWakeUpTime = -1.0f;
                    TutorialManager.instance.StealControl = false;
                    if (this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
                        UIManager.setFadeBeforeUI(((IsoPlayer)this).getPlayerNum(), true);
                        UIManager.FadeIn(((IsoPlayer)this).getPlayerNum(), 2.0);
                        GameClient.instance.sendPlayer((IsoPlayer)this);
                    }
                }
            }
            else if ("mechanicActionDone".equals(anObject)) {
                LuaEventManager.triggerEvent("OnMechanicActionDone", this, byteBuffer.get() == 1, byteBuffer.getInt(), GameWindow.ReadString(byteBuffer), byteBuffer.getLong(), byteBuffer.get() == 1);
            }
            else if ("vehicleNoKey".equals(anObject)) {
                this.SayDebug(" [img=media/ui/CarKey_none.png]");
            }
        }
    }
    
    public int getAlreadyReadPages(final String anObject) {
        for (int i = 0; i < this.ReadBooks.size(); ++i) {
            final ReadBook readBook = this.ReadBooks.get(i);
            if (readBook.fullType.equals(anObject)) {
                return readBook.alreadyReadPages;
            }
        }
        return 0;
    }
    
    public void setAlreadyReadPages(final String s, final int n) {
        for (int i = 0; i < this.ReadBooks.size(); ++i) {
            final ReadBook readBook = this.ReadBooks.get(i);
            if (readBook.fullType.equals(s)) {
                readBook.alreadyReadPages = n;
                return;
            }
        }
        final ReadBook e = new ReadBook();
        e.fullType = s;
        e.alreadyReadPages = n;
        this.ReadBooks.add(e);
    }
    
    public void updateLightInfo() {
        if (!GameServer.bServer) {
            return;
        }
        if (this.isZombie()) {
            return;
        }
        synchronized (this.lightInfo) {
            this.lightInfo.square = this.movingSq;
            if (this.lightInfo.square == null) {
                this.lightInfo.square = this.getCell().getGridSquare((int)this.x, (int)this.y, (int)this.z);
            }
            if (this.ReanimatedCorpse != null) {
                this.lightInfo.square = this.getCell().getGridSquare((int)this.x, (int)this.y, (int)this.z);
            }
            this.lightInfo.x = this.getX();
            this.lightInfo.y = this.getY();
            this.lightInfo.z = this.getZ();
            this.lightInfo.angleX = this.getForwardDirection().getX();
            this.lightInfo.angleY = this.getForwardDirection().getY();
            this.lightInfo.torches.clear();
            this.lightInfo.night = GameTime.getInstance().getNight();
        }
    }
    
    public LightInfo initLightInfo2() {
        synchronized (this.lightInfo) {
            for (int i = 0; i < this.lightInfo2.torches.size(); ++i) {
                TorchInfo.release(this.lightInfo2.torches.get(i));
            }
            this.lightInfo2.initFrom(this.lightInfo);
        }
        return this.lightInfo2;
    }
    
    public LightInfo getLightInfo2() {
        return this.lightInfo2;
    }
    
    @Override
    public void postupdate() {
        s_performance.postUpdate.invokeAndMeasure(this, IsoGameCharacter::postUpdateInternal);
    }
    
    private void postUpdateInternal() {
        super.postupdate();
        this.getAnimationPlayer().UpdateDir(this);
        this.setTurning(this.shouldBeTurning());
        this.setTurning90(this.shouldBeTurning90());
        this.setTurningAround(this.shouldBeTurningAround());
        this.actionContext.update();
        if (this.getCurrentSquare() != null) {
            this.advancedAnimator.update();
        }
        this.actionContext.clearEvent("ActiveAnimFinished");
        this.actionContext.clearEvent("ActiveAnimFinishing");
        this.actionContext.clearEvent("ActiveAnimLooped");
        final AnimationPlayer animationPlayer = this.getAnimationPlayer();
        if (animationPlayer != null) {
            final MoveDeltaModifiers moveDeltas = L_postUpdate.moveDeltas;
            moveDeltas.moveDelta = this.getMoveDelta();
            moveDeltas.turnDelta = this.getTurnDelta();
            final boolean hasPath = this.hasPath();
            if (this instanceof IsoPlayer && hasPath && this.isRunning()) {
                moveDeltas.turnDelta = Math.max(moveDeltas.turnDelta, 2.0f);
            }
            final State currentState = this.getCurrentState();
            if (currentState != null) {
                currentState.getDeltaModifiers(this, moveDeltas);
            }
            if (moveDeltas.twistDelta == -1.0f) {
                moveDeltas.twistDelta = moveDeltas.turnDelta * 1.8f;
            }
            if (!this.isTurning()) {
                moveDeltas.turnDelta = 0.0f;
            }
            final float max = Math.max(1.0f - moveDeltas.moveDelta / 2.0f, 0.0f);
            animationPlayer.angleStepDelta = max * moveDeltas.turnDelta;
            animationPlayer.angleTwistDelta = max * moveDeltas.twistDelta;
            animationPlayer.setMaxTwistAngle(0.017453292f * this.getMaxTwist());
        }
        if (this.hasActiveModel()) {
            try {
                this.legsSprite.modelSlot.Update();
            }
            catch (Throwable t) {
                ExceptionLogger.logException(t);
            }
        }
        else {
            final AnimationPlayer animationPlayer2 = this.getAnimationPlayer();
            animationPlayer2.bUpdateBones = false;
            final boolean interpolateAnims = PerformanceSettings.InterpolateAnims;
            PerformanceSettings.InterpolateAnims = false;
            try {
                animationPlayer2.UpdateDir(this);
                animationPlayer2.Update();
            }
            catch (Throwable t2) {
                ExceptionLogger.logException(t2);
            }
            finally {
                animationPlayer2.bUpdateBones = true;
                PerformanceSettings.InterpolateAnims = interpolateAnims;
            }
        }
        this.updateLightInfo();
        if (this.isAnimationRecorderActive()) {
            this.m_animationRecorder.logVariables(this);
            this.m_animationRecorder.endLine();
        }
    }
    
    public boolean shouldBeTurning() {
        final boolean b = PZMath.abs(this.getTargetTwist()) > 1.0f;
        if (this.isZombie() && this.getCurrentState() == ZombieFallDownState.instance()) {
            return false;
        }
        if (this.blockTurning) {
            return false;
        }
        if (this.isBehaviourMoving()) {
            return b;
        }
        if (this.isPlayerMoving()) {
            return b;
        }
        if (this.isAttacking()) {
            return !this.bAimAtFloor;
        }
        return this.getAbsoluteExcessTwist() > 1.0f || (this.isTurning() && b);
    }
    
    public boolean shouldBeTurning90() {
        return this.isTurning() && (this.isTurning90() || Math.abs(this.getTargetTwist()) > 65.0f);
    }
    
    public boolean shouldBeTurningAround() {
        return this.isTurning() && (this.isTurningAround() || Math.abs(this.getTargetTwist()) > 110.0f);
    }
    
    private boolean isTurning() {
        return this.m_isTurning;
    }
    
    private void setTurning(final boolean isTurning) {
        this.m_isTurning = isTurning;
    }
    
    private boolean isTurningAround() {
        return this.m_isTurningAround;
    }
    
    private void setTurningAround(final boolean isTurningAround) {
        this.m_isTurningAround = isTurningAround;
    }
    
    private boolean isTurning90() {
        return this.m_isTurning90;
    }
    
    private void setTurning90(final boolean isTurning90) {
        this.m_isTurning90 = isTurning90;
    }
    
    public boolean hasPath() {
        return this.getPath2() != null;
    }
    
    @Override
    public boolean isAnimationRecorderActive() {
        return this.m_animationRecorder != null && this.m_animationRecorder.isRecording();
    }
    
    @Override
    public AnimationPlayerRecorder getAnimationPlayerRecorder() {
        return this.m_animationRecorder;
    }
    
    public boolean isSafety() {
        return this.safety;
    }
    
    public void setSafety(final boolean safety) {
        this.safety = safety;
    }
    
    public float getSafetyCooldown() {
        return this.safetyCooldown;
    }
    
    public void setSafetyCooldown(final float a) {
        this.safetyCooldown = Math.max(a, 0.0f);
    }
    
    public float getMeleeDelay() {
        return this.meleeDelay;
    }
    
    public void setMeleeDelay(final float a) {
        this.meleeDelay = Math.max(a, 0.0f);
    }
    
    public float getRecoilDelay() {
        return this.RecoilDelay;
    }
    
    public void setRecoilDelay(float recoilDelay) {
        if (recoilDelay < 0.0f) {
            recoilDelay = 0.0f;
        }
        this.RecoilDelay = recoilDelay;
    }
    
    public float getBeenMovingFor() {
        return this.BeenMovingFor;
    }
    
    public void setBeenMovingFor(float beenMovingFor) {
        if (beenMovingFor < 0.0f) {
            beenMovingFor = 0.0f;
        }
        if (beenMovingFor > 70.0f) {
            beenMovingFor = 70.0f;
        }
        this.BeenMovingFor = beenMovingFor;
    }
    
    public boolean isForceShove() {
        return this.forceShove;
    }
    
    public void setForceShove(final boolean forceShove) {
        this.forceShove = forceShove;
    }
    
    public String getClickSound() {
        return this.clickSound;
    }
    
    public void setClickSound(final String clickSound) {
        this.clickSound = clickSound;
    }
    
    public int getMeleeCombatMod() {
        final int weaponLevel = this.getWeaponLevel();
        if (weaponLevel == 1) {
            return -2;
        }
        if (weaponLevel == 2) {
            return 0;
        }
        if (weaponLevel == 3) {
            return 1;
        }
        if (weaponLevel == 4) {
            return 2;
        }
        if (weaponLevel == 5) {
            return 3;
        }
        if (weaponLevel == 6) {
            return 4;
        }
        if (weaponLevel == 7) {
            return 5;
        }
        if (weaponLevel == 8) {
            return 5;
        }
        if (weaponLevel == 9) {
            return 6;
        }
        if (weaponLevel == 10) {
            return 7;
        }
        return -5;
    }
    
    public int getWeaponLevel() {
        final WeaponType weaponType = WeaponType.getWeaponType(this);
        int perkLevel = -1;
        if (weaponType != null && weaponType != WeaponType.barehand) {
            if (((HandWeapon)this.getPrimaryHandItem()).getCategories().contains("Axe")) {
                perkLevel = this.getPerkLevel(PerkFactory.Perks.Axe);
            }
            if (((HandWeapon)this.getPrimaryHandItem()).getCategories().contains("Spear")) {
                perkLevel += this.getPerkLevel(PerkFactory.Perks.Spear);
            }
            if (((HandWeapon)this.getPrimaryHandItem()).getCategories().contains("SmallBlade")) {
                perkLevel += this.getPerkLevel(PerkFactory.Perks.SmallBlade);
            }
            if (((HandWeapon)this.getPrimaryHandItem()).getCategories().contains("LongBlade")) {
                perkLevel += this.getPerkLevel(PerkFactory.Perks.LongBlade);
            }
            if (((HandWeapon)this.getPrimaryHandItem()).getCategories().contains("Blunt")) {
                perkLevel += this.getPerkLevel(PerkFactory.Perks.Blunt);
            }
            if (((HandWeapon)this.getPrimaryHandItem()).getCategories().contains("SmallBlunt")) {
                perkLevel += this.getPerkLevel(PerkFactory.Perks.SmallBlunt);
            }
        }
        if (perkLevel == -1) {
            return 0;
        }
        return perkLevel;
    }
    
    public int getMaintenanceMod() {
        return (this.getPerkLevel(PerkFactory.Perks.Maintenance) + this.getWeaponLevel() / 2) / 2;
    }
    
    public BaseVehicle getVehicle() {
        return this.vehicle;
    }
    
    public void setVehicle(final BaseVehicle vehicle) {
        this.vehicle = vehicle;
    }
    
    public boolean isUnderVehicle() {
        final int n = ((int)this.x - 4) / 10;
        final int n2 = ((int)this.y - 4) / 10;
        final int n3 = (int)Math.ceil((this.x + 4.0f) / 10.0f);
        final int n4 = (int)Math.ceil((this.y + 4.0f) / 10.0f);
        final Vector2 vector2 = BaseVehicle.TL_vector2_pool.get().alloc();
        for (int i = n2; i < n4; ++i) {
            for (int j = n; j < n3; ++j) {
                final IsoChunk isoChunk = GameServer.bServer ? ServerMap.instance.getChunk(j, i) : IsoWorld.instance.CurrentCell.getChunkForGridSquare(j * 10, i * 10, 0);
                if (isoChunk != null) {
                    for (int k = 0; k < isoChunk.vehicles.size(); ++k) {
                        final Vector2 testCollisionWithCharacter = isoChunk.vehicles.get(k).testCollisionWithCharacter(this, 0.3f, vector2);
                        if (testCollisionWithCharacter != null && testCollisionWithCharacter.x != -1.0f) {
                            BaseVehicle.TL_vector2_pool.get().release(vector2);
                            return true;
                        }
                    }
                }
            }
        }
        BaseVehicle.TL_vector2_pool.get().release(vector2);
        return false;
    }
    
    public boolean isProne() {
        return this.isOnFloor();
    }
    
    public boolean isBeingSteppedOn() {
        if (!this.isOnFloor()) {
            return false;
        }
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                final IsoGridSquare gridSquare = this.getCell().getGridSquare((int)this.x + j, (int)this.y + i, (int)this.z);
                if (gridSquare != null) {
                    final ArrayList<IsoMovingObject> movingObjects = gridSquare.getMovingObjects();
                    for (int k = 0; k < movingObjects.size(); ++k) {
                        final IsoMovingObject isoMovingObject = movingObjects.get(k);
                        if (isoMovingObject != this) {
                            final IsoGameCharacter isoGameCharacter = Type.tryCastTo(isoMovingObject, IsoGameCharacter.class);
                            if (isoGameCharacter != null) {
                                if (isoGameCharacter.getVehicle() == null) {
                                    if (!isoMovingObject.isOnFloor()) {
                                        if (ZombieOnGroundState.isCharacterStandingOnOther(isoGameCharacter, this)) {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public float getTemperature() {
        return this.getBodyDamage().getTemperature();
    }
    
    public void setTemperature(final float temperature) {
        this.getBodyDamage().setTemperature(temperature);
    }
    
    public float getReduceInfectionPower() {
        return this.reduceInfectionPower;
    }
    
    public void setReduceInfectionPower(final float reduceInfectionPower) {
        this.reduceInfectionPower = reduceInfectionPower;
    }
    
    public float getInventoryWeight() {
        if (this.getInventory() == null) {
            return 0.0f;
        }
        float n = 0.0f;
        final ArrayList<InventoryItem> items = this.getInventory().getItems();
        for (int i = 0; i < items.size(); ++i) {
            final InventoryItem inventoryItem = items.get(i);
            if (inventoryItem.getAttachedSlot() > -1 && !this.isEquipped(inventoryItem)) {
                n += inventoryItem.getHotbarEquippedWeight();
            }
            else if (this.isEquipped(inventoryItem)) {
                n += inventoryItem.getEquippedWeight();
            }
            else {
                n += inventoryItem.getUnequippedWeight();
            }
        }
        return n;
    }
    
    public void dropHandItems() {
        if ("Tutorial".equals(Core.GameMode)) {
            return;
        }
        if (this instanceof IsoPlayer && !((IsoPlayer)this).isLocalPlayer()) {
            return;
        }
        this.dropHeavyItems();
        final IsoGridSquare currentSquare = this.getCurrentSquare();
        if (currentSquare == null) {
            return;
        }
        final InventoryItem primaryHandItem = this.getPrimaryHandItem();
        final InventoryItem secondaryHandItem = this.getSecondaryHandItem();
        if (primaryHandItem == null && secondaryHandItem == null) {
            return;
        }
        final IsoGridSquare solidFloor = this.getSolidFloorAt(currentSquare.x, currentSquare.y, currentSquare.z);
        if (solidFloor == null) {
            return;
        }
        final float next = Rand.Next(0.1f, 0.9f);
        final float next2 = Rand.Next(0.1f, 0.9f);
        final float n = solidFloor.getApparentZ(next, next2) - solidFloor.getZ();
        boolean b = false;
        if (secondaryHandItem == primaryHandItem) {
            b = true;
        }
        if (primaryHandItem != null) {
            this.setPrimaryHandItem(null);
            this.getInventory().DoRemoveItem(primaryHandItem);
            solidFloor.AddWorldInventoryItem(primaryHandItem, next, next2, n);
            LuaEventManager.triggerEvent("OnContainerUpdate");
            LuaEventManager.triggerEvent("onItemFall", primaryHandItem);
        }
        if (secondaryHandItem != null) {
            this.setSecondaryHandItem(null);
            if (!b) {
                this.getInventory().DoRemoveItem(secondaryHandItem);
                solidFloor.AddWorldInventoryItem(secondaryHandItem, next, next2, n);
                LuaEventManager.triggerEvent("OnContainerUpdate");
                LuaEventManager.triggerEvent("onItemFall", secondaryHandItem);
            }
        }
        this.resetEquippedHandsModels();
    }
    
    public boolean shouldBecomeZombieAfterDeath() {
        switch (SandboxOptions.instance.Lore.Transmission.getValue()) {
            case 1: {
                if (!this.getBodyDamage().IsFakeInfected()) {
                    final float infectionLevel = this.getBodyDamage().getInfectionLevel();
                    final BodyDamage bodyDamage = this.BodyDamage;
                    if (infectionLevel >= 0.001f) {
                        return true;
                    }
                }
                return false;
            }
            case 2: {
                if (!this.getBodyDamage().IsFakeInfected()) {
                    final float infectionLevel2 = this.getBodyDamage().getInfectionLevel();
                    final BodyDamage bodyDamage2 = this.BodyDamage;
                    if (infectionLevel2 >= 0.001f) {
                        return true;
                    }
                }
                return false;
            }
            case 3: {
                return true;
            }
            case 4: {
                return false;
            }
            default: {
                return false;
            }
        }
    }
    
    public void applyTraits(final ArrayList<String> list) {
        if (list == null) {
            return;
        }
        final HashMap<PerkFactory.Perk, Integer> hashMap = new HashMap<PerkFactory.Perk, Integer>();
        hashMap.put(PerkFactory.Perks.Fitness, 5);
        hashMap.put(PerkFactory.Perks.Strength, 5);
        for (int i = 0; i < list.size(); ++i) {
            final String s = list.get(i);
            if (s != null) {
                if (!s.isEmpty()) {
                    final TraitFactory.Trait trait = TraitFactory.getTrait(s);
                    if (trait != null) {
                        if (!this.HasTrait(s)) {
                            this.getTraits().add(s);
                        }
                        final HashMap<PerkFactory.Perk, Integer> xpBoostMap = trait.getXPBoostMap();
                        if (xpBoostMap != null) {
                            for (final Map.Entry<PerkFactory.Perk, Integer> entry : xpBoostMap.entrySet()) {
                                final PerkFactory.Perk key = entry.getKey();
                                int intValue = entry.getValue();
                                if (hashMap.containsKey(key)) {
                                    intValue += hashMap.get(key);
                                }
                                hashMap.put(key, intValue);
                            }
                        }
                    }
                }
            }
        }
        if (this instanceof IsoPlayer) {
            ((IsoPlayer)this).getNutrition().applyWeightFromTraits();
        }
        for (final Map.Entry<PerkFactory.Perk, Integer> entry2 : this.getDescriptor().getXPBoostMap().entrySet()) {
            final PerkFactory.Perk key2 = entry2.getKey();
            int intValue2 = entry2.getValue();
            if (hashMap.containsKey(key2)) {
                intValue2 += hashMap.get(key2);
            }
            hashMap.put(key2, intValue2);
        }
        for (final Map.Entry<PerkFactory.Perk, Integer> entry3 : hashMap.entrySet()) {
            final PerkFactory.Perk key3 = entry3.getKey();
            final int min = Math.min(10, Math.max(0, entry3.getValue()));
            this.getDescriptor().getXPBoostMap().put(key3, Math.min(3, min));
            for (int j = 0; j < min; ++j) {
                this.LevelPerk(key3);
            }
            this.getXp().setXPToLevel(key3, this.getPerkLevel(key3));
        }
    }
    
    public void createKeyRing() {
        final InventoryItem addItem = this.getInventory().AddItem("Base.KeyRing");
        if (addItem == null || !(addItem instanceof InventoryContainer)) {
            return;
        }
        final InventoryContainer inventoryContainer = (InventoryContainer)addItem;
        inventoryContainer.setName(Translator.getText("IGUI_KeyRingName", this.getDescriptor().getForename(), this.getDescriptor().getSurname()));
        if (Rand.Next(100) < 40) {
            final RoomDef room = IsoWorld.instance.MetaGrid.getRoomAt((int)this.getX(), (int)this.getY(), (int)this.getZ());
            if (room != null && room.getBuilding() != null) {
                inventoryContainer.getInventory().AddItem(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Rand.Next(5) + 1)).setKeyId(room.getBuilding().getKeyId());
            }
        }
    }
    
    public void autoDrink() {
        if (GameServer.bServer) {
            return;
        }
        if (GameClient.bClient && !((IsoPlayer)this).isLocalPlayer()) {
            return;
        }
        if (!Core.getInstance().getOptionAutoDrink()) {
            return;
        }
        if (LuaHookManager.TriggerHook("AutoDrink", this)) {
            return;
        }
        if (this.stats.thirst <= 0.1f) {
            return;
        }
        final InventoryItem waterSource = this.getWaterSource(this.getInventory().getItems());
        if (waterSource != null) {
            final Stats stats = this.stats;
            stats.thirst -= 0.1f;
            if (GameClient.bClient) {
                GameClient.instance.drink((IsoPlayer)this, 0.1f);
            }
            waterSource.Use();
        }
    }
    
    public InventoryItem getWaterSource(final ArrayList<InventoryItem> list) {
        InventoryItem inventoryItem = null;
        final ArrayList list2 = new ArrayList();
        for (int i = 0; i < list.size(); ++i) {
            final InventoryItem inventoryItem2 = list.get(i);
            if (inventoryItem2.isWaterSource() && !inventoryItem2.isBeingFilled()) {
                if (!inventoryItem2.isTaintedWater()) {
                    if (inventoryItem2 instanceof Drainable) {
                        if (((Drainable)inventoryItem2).getUsedDelta() > 0.0f) {
                            inventoryItem = inventoryItem2;
                            break;
                        }
                    }
                    else if (!(inventoryItem2 instanceof InventoryContainer)) {
                        inventoryItem = inventoryItem2;
                        break;
                    }
                }
            }
        }
        return inventoryItem;
    }
    
    public List<String> getKnownRecipes() {
        return this.knownRecipes;
    }
    
    public boolean isRecipeKnown(final Recipe recipe) {
        return DebugOptions.instance.CheatRecipeKnowAll.getValue() || !recipe.needToBeLearn() || this.getKnownRecipes().contains(recipe.getOriginalname());
    }
    
    public boolean isRecipeKnown(final String s) {
        final Recipe recipe = ScriptManager.instance.getRecipe(s);
        if (recipe == null) {
            return DebugOptions.instance.CheatRecipeKnowAll.getValue() || this.getKnownRecipes().contains(s);
        }
        return this.isRecipeKnown(recipe);
    }
    
    public boolean learnRecipe(final String s) {
        if (!this.isRecipeKnown(s)) {
            this.getKnownRecipes().add(s);
            return true;
        }
        return false;
    }
    
    public boolean isMoving() {
        return (!(this instanceof IsoPlayer) || ((IsoPlayer)this).isAttackAnimThrowTimeOut()) && this.m_isMoving;
    }
    
    public boolean isBehaviourMoving() {
        final State currentState = this.getCurrentState();
        return currentState != null && currentState.isMoving(this);
    }
    
    public boolean isPlayerMoving() {
        return false;
    }
    
    public void setMoving(final boolean justMoved) {
        this.m_isMoving = justMoved;
        if (GameClient.bClient && this instanceof IsoPlayer && ((IsoPlayer)this).bRemote) {
            ((IsoPlayer)this).m_isPlayerMoving = justMoved;
            ((IsoPlayer)this).setJustMoved(justMoved);
        }
    }
    
    private boolean isFacingNorthWesterly() {
        return this.dir == IsoDirections.W || this.dir == IsoDirections.NW || this.dir == IsoDirections.N || this.dir == IsoDirections.NE;
    }
    
    public boolean isAttacking() {
        return false;
    }
    
    public boolean isZombieAttacking() {
        return false;
    }
    
    public boolean isZombieAttacking(final IsoMovingObject isoMovingObject) {
        return false;
    }
    
    private boolean isZombieThumping() {
        return this.isZombie() && this.getCurrentState() == ThumpState.instance();
    }
    
    public int compareMovePriority(final IsoGameCharacter isoGameCharacter) {
        if (isoGameCharacter == null) {
            return 1;
        }
        if (this.isZombieThumping() && !isoGameCharacter.isZombieThumping()) {
            return 1;
        }
        if (!this.isZombieThumping() && isoGameCharacter.isZombieThumping()) {
            return -1;
        }
        if (isoGameCharacter instanceof IsoPlayer) {
            if (GameClient.bClient && this.isZombieAttacking(isoGameCharacter)) {
                return -1;
            }
            return 0;
        }
        else {
            if (this.isZombieAttacking() && !isoGameCharacter.isZombieAttacking()) {
                return 1;
            }
            if (!this.isZombieAttacking() && isoGameCharacter.isZombieAttacking()) {
                return -1;
            }
            if (this.isBehaviourMoving() && !isoGameCharacter.isBehaviourMoving()) {
                return 1;
            }
            if (!this.isBehaviourMoving() && isoGameCharacter.isBehaviourMoving()) {
                return -1;
            }
            if (this.isFacingNorthWesterly() && !isoGameCharacter.isFacingNorthWesterly()) {
                return 1;
            }
            if (!this.isFacingNorthWesterly() && isoGameCharacter.isFacingNorthWesterly()) {
                return -1;
            }
            return 0;
        }
    }
    
    public long playSound(final String s) {
        return this.getEmitter().playSound(s);
    }
    
    public void stopOrTriggerSound(final long n) {
        this.getEmitter().stopOrTriggerSound(n);
    }
    
    public void addWorldSoundUnlessInvisible(final int n, final int n2, final boolean b) {
        if (this.isInvisible()) {
            return;
        }
        WorldSoundManager.instance.addSound(this, (int)this.getX(), (int)this.getY(), (int)this.getZ(), n, n2, b);
    }
    
    public boolean isKnownPoison(final InventoryItem inventoryItem) {
        if (!(inventoryItem instanceof Food)) {
            return false;
        }
        final Food food = (Food)inventoryItem;
        if (food.getPoisonPower() <= 0) {
            return false;
        }
        if (food.getHerbalistType() != null && !food.getHerbalistType().isEmpty()) {
            return this.isRecipeKnown("Herbalist");
        }
        return (food.getPoisonDetectionLevel() >= 0 && this.getPerkLevel(PerkFactory.Perks.Cooking) >= 10 - food.getPoisonDetectionLevel()) || food.getPoisonLevelForRecipe() != null;
    }
    
    public int getLastHourSleeped() {
        return this.lastHourSleeped;
    }
    
    public void setLastHourSleeped(final int lastHourSleeped) {
        this.lastHourSleeped = lastHourSleeped;
    }
    
    public void setTimeOfSleep(final float timeOfSleep) {
        this.timeOfSleep = timeOfSleep;
    }
    
    public void setDelayToSleep(final float delayToActuallySleep) {
        this.delayToActuallySleep = delayToActuallySleep;
    }
    
    public String getBedType() {
        return this.bedType;
    }
    
    public void setBedType(final String bedType) {
        this.bedType = bedType;
    }
    
    public void enterVehicle(final BaseVehicle baseVehicle, final int n, final Vector3f vector3f) {
        if (this.vehicle != null) {
            this.vehicle.exit(this);
        }
        if (baseVehicle != null) {
            baseVehicle.enter(n, this, vector3f);
        }
    }
    
    public float Hit(final BaseVehicle baseVehicle, final float f, final boolean hitFromBehind, final float n, final float n2) {
        this.setHitFromBehind(hitFromBehind);
        this.setAttackedBy(baseVehicle.getDriver());
        this.getHitDir().set(n, n2);
        if (!this.isKnockedDown()) {
            this.setHitForce(Math.max(0.5f, f * 0.15f));
        }
        else {
            this.setHitForce(Math.min(2.5f, f * 0.15f));
        }
        if (GameClient.bClient) {
            HitReactionNetworkAI.CalcHitReactionVehicle(this, baseVehicle);
        }
        if (Core.bDebug) {
            DebugLog.log(DebugType.Multiplayer, String.format("Vehicle id=%d hit %s id=%d: speed=%f force=%f hitDir=%s", baseVehicle.getId(), this.getClass().getSimpleName(), this.getOnlineID(), f, this.getHitForce(), this.getHitDir()));
        }
        return this.getHealth();
    }
    
    public PolygonalMap2.Path getPath2() {
        return this.path2;
    }
    
    public void setPath2(final PolygonalMap2.Path path2) {
        this.path2 = path2;
    }
    
    public PathFindBehavior2 getPathFindBehavior2() {
        return this.pfb2;
    }
    
    public MapKnowledge getMapKnowledge() {
        return this.mapKnowledge;
    }
    
    public IsoObject getBed() {
        if (this.isAsleep()) {
            return this.bed;
        }
        return null;
    }
    
    public void setBed(final IsoObject bed) {
        this.bed = bed;
    }
    
    public boolean avoidDamage() {
        return this.m_avoidDamage;
    }
    
    public void setAvoidDamage(final boolean avoidDamage) {
        this.m_avoidDamage = avoidDamage;
    }
    
    public boolean isReading() {
        return this.isReading;
    }
    
    public void setReading(final boolean isReading) {
        this.isReading = isReading;
    }
    
    public float getTimeSinceLastSmoke() {
        return this.timeSinceLastSmoke;
    }
    
    public void setTimeSinceLastSmoke(final float n) {
        this.timeSinceLastSmoke = PZMath.clamp(n, 0.0f, 10.0f);
    }
    
    public boolean isInvisible() {
        return this.m_invisible;
    }
    
    public void setInvisible(final boolean invisible) {
        this.m_invisible = invisible;
    }
    
    public boolean isDriving() {
        return this.getVehicle() != null && this.getVehicle().getDriver() == this && Math.abs(this.getVehicle().getCurrentSpeedKmHour()) > 1.0f;
    }
    
    public boolean isInARoom() {
        return this.square != null && this.square.isInARoom();
    }
    
    public boolean isGodMod() {
        return this.m_godMod;
    }
    
    public void setGodMod(final boolean godMod) {
        this.m_godMod = godMod;
        if (this instanceof IsoPlayer && GameClient.bClient && ((IsoPlayer)this).isLocalPlayer()) {
            this.updateMovementRates();
            GameClient.sendPlayerInjuries((IsoPlayer)this);
            GameClient.sendPlayerDamage((IsoPlayer)this);
        }
    }
    
    public boolean isUnlimitedCarry() {
        return this.unlimitedCarry;
    }
    
    public void setUnlimitedCarry(final boolean unlimitedCarry) {
        this.unlimitedCarry = unlimitedCarry;
    }
    
    public boolean isBuildCheat() {
        return this.buildCheat;
    }
    
    public void setBuildCheat(final boolean buildCheat) {
        this.buildCheat = buildCheat;
    }
    
    public boolean isFarmingCheat() {
        return this.farmingCheat;
    }
    
    public void setFarmingCheat(final boolean farmingCheat) {
        this.farmingCheat = farmingCheat;
    }
    
    public boolean isHealthCheat() {
        return this.healthCheat;
    }
    
    public void setHealthCheat(final boolean healthCheat) {
        this.healthCheat = healthCheat;
    }
    
    public boolean isMechanicsCheat() {
        return this.mechanicsCheat;
    }
    
    public void setMechanicsCheat(final boolean mechanicsCheat) {
        this.mechanicsCheat = mechanicsCheat;
    }
    
    public boolean isMovablesCheat() {
        return this.movablesCheat;
    }
    
    public void setMovablesCheat(final boolean movablesCheat) {
        this.movablesCheat = movablesCheat;
    }
    
    public boolean isTimedActionInstantCheat() {
        return this.timedActionInstantCheat;
    }
    
    public void setTimedActionInstantCheat(final boolean timedActionInstantCheat) {
        this.timedActionInstantCheat = timedActionInstantCheat;
    }
    
    public boolean isTimedActionInstant() {
        return (Core.bDebug && DebugOptions.instance.CheatTimedActionInstant.getValue()) || this.isTimedActionInstantCheat();
    }
    
    public boolean isShowAdminTag() {
        return this.showAdminTag;
    }
    
    public void setShowAdminTag(final boolean showAdminTag) {
        this.showAdminTag = showAdminTag;
    }
    
    public IAnimationVariableSlot getVariable(final AnimationVariableHandle animationVariableHandle) {
        return this.getGameVariablesInternal().getVariable(animationVariableHandle);
    }
    
    public IAnimationVariableSlot getVariable(final String s) {
        return this.getGameVariablesInternal().getVariable(s);
    }
    
    @Override
    public IAnimationVariableSlot getOrCreateVariable(final String s) {
        return this.getGameVariablesInternal().getOrCreateVariable(s);
    }
    
    @Override
    public void setVariable(final IAnimationVariableSlot variable) {
        this.getGameVariablesInternal().setVariable(variable);
    }
    
    @Override
    public void setVariable(final String s, final String s2) {
        this.getGameVariablesInternal().setVariable(s, s2);
    }
    
    @Override
    public void setVariable(final String s, final boolean b) {
        this.getGameVariablesInternal().setVariable(s, b);
    }
    
    @Override
    public void setVariable(final String s, final float n) {
        this.getGameVariablesInternal().setVariable(s, n);
    }
    
    protected void setVariable(final String s, final AnimationVariableSlotCallbackBool.CallbackGetStrongTyped callbackGetStrongTyped) {
        this.getGameVariablesInternal().setVariable(s, callbackGetStrongTyped);
    }
    
    protected void setVariable(final String s, final AnimationVariableSlotCallbackBool.CallbackGetStrongTyped callbackGetStrongTyped, final AnimationVariableSlotCallbackBool.CallbackSetStrongTyped callbackSetStrongTyped) {
        this.getGameVariablesInternal().setVariable(s, callbackGetStrongTyped, callbackSetStrongTyped);
    }
    
    protected void setVariable(final String s, final AnimationVariableSlotCallbackString.CallbackGetStrongTyped callbackGetStrongTyped) {
        this.getGameVariablesInternal().setVariable(s, callbackGetStrongTyped);
    }
    
    protected void setVariable(final String s, final AnimationVariableSlotCallbackString.CallbackGetStrongTyped callbackGetStrongTyped, final AnimationVariableSlotCallbackString.CallbackSetStrongTyped callbackSetStrongTyped) {
        this.getGameVariablesInternal().setVariable(s, callbackGetStrongTyped, callbackSetStrongTyped);
    }
    
    protected void setVariable(final String s, final AnimationVariableSlotCallbackFloat.CallbackGetStrongTyped callbackGetStrongTyped) {
        this.getGameVariablesInternal().setVariable(s, callbackGetStrongTyped);
    }
    
    protected void setVariable(final String s, final AnimationVariableSlotCallbackFloat.CallbackGetStrongTyped callbackGetStrongTyped, final AnimationVariableSlotCallbackFloat.CallbackSetStrongTyped callbackSetStrongTyped) {
        this.getGameVariablesInternal().setVariable(s, callbackGetStrongTyped, callbackSetStrongTyped);
    }
    
    protected void setVariable(final String s, final AnimationVariableSlotCallbackInt.CallbackGetStrongTyped callbackGetStrongTyped) {
        this.getGameVariablesInternal().setVariable(s, callbackGetStrongTyped);
    }
    
    protected void setVariable(final String s, final AnimationVariableSlotCallbackInt.CallbackGetStrongTyped callbackGetStrongTyped, final AnimationVariableSlotCallbackInt.CallbackSetStrongTyped callbackSetStrongTyped) {
        this.getGameVariablesInternal().setVariable(s, callbackGetStrongTyped, callbackSetStrongTyped);
    }
    
    public void setVariable(final String s, final boolean b, final AnimationVariableSlotCallbackBool.CallbackGetStrongTyped callbackGetStrongTyped) {
        this.getGameVariablesInternal().setVariable(s, b, callbackGetStrongTyped);
    }
    
    public void setVariable(final String s, final boolean b, final AnimationVariableSlotCallbackBool.CallbackGetStrongTyped callbackGetStrongTyped, final AnimationVariableSlotCallbackBool.CallbackSetStrongTyped callbackSetStrongTyped) {
        this.getGameVariablesInternal().setVariable(s, b, callbackGetStrongTyped, callbackSetStrongTyped);
    }
    
    public void setVariable(final String s, final String s2, final AnimationVariableSlotCallbackString.CallbackGetStrongTyped callbackGetStrongTyped) {
        this.getGameVariablesInternal().setVariable(s, s2, callbackGetStrongTyped);
    }
    
    public void setVariable(final String s, final String s2, final AnimationVariableSlotCallbackString.CallbackGetStrongTyped callbackGetStrongTyped, final AnimationVariableSlotCallbackString.CallbackSetStrongTyped callbackSetStrongTyped) {
        this.getGameVariablesInternal().setVariable(s, s2, callbackGetStrongTyped, callbackSetStrongTyped);
    }
    
    public void setVariable(final String s, final float n, final AnimationVariableSlotCallbackFloat.CallbackGetStrongTyped callbackGetStrongTyped) {
        this.getGameVariablesInternal().setVariable(s, n, callbackGetStrongTyped);
    }
    
    public void setVariable(final String s, final float n, final AnimationVariableSlotCallbackFloat.CallbackGetStrongTyped callbackGetStrongTyped, final AnimationVariableSlotCallbackFloat.CallbackSetStrongTyped callbackSetStrongTyped) {
        this.getGameVariablesInternal().setVariable(s, n, callbackGetStrongTyped, callbackSetStrongTyped);
    }
    
    public void setVariable(final String s, final int n, final AnimationVariableSlotCallbackInt.CallbackGetStrongTyped callbackGetStrongTyped) {
        this.getGameVariablesInternal().setVariable(s, n, callbackGetStrongTyped);
    }
    
    public void setVariable(final String s, final int n, final AnimationVariableSlotCallbackInt.CallbackGetStrongTyped callbackGetStrongTyped, final AnimationVariableSlotCallbackInt.CallbackSetStrongTyped callbackSetStrongTyped) {
        this.getGameVariablesInternal().setVariable(s, n, callbackGetStrongTyped, callbackSetStrongTyped);
    }
    
    @Override
    public void clearVariable(final String s) {
        this.getGameVariablesInternal().clearVariable(s);
    }
    
    @Override
    public void clearVariables() {
        this.getGameVariablesInternal().clearVariables();
    }
    
    public String getVariableString(final String s) {
        return this.getGameVariablesInternal().getVariableString(s);
    }
    
    private String getFootInjuryType() {
        if (!(this instanceof IsoPlayer)) {
            return "";
        }
        final BodyPart bodyPart = this.getBodyDamage().getBodyPart(BodyPartType.Foot_L);
        final BodyPart bodyPart2 = this.getBodyDamage().getBodyPart(BodyPartType.Foot_R);
        if (!this.bRunning) {
            if (bodyPart.haveBullet() || bodyPart.getBurnTime() > 5.0f || bodyPart.bitten() || bodyPart.deepWounded() || bodyPart.isSplint() || bodyPart.getFractureTime() > 0.0f || bodyPart.haveGlass()) {
                return "leftheavy";
            }
            if (bodyPart2.haveBullet() || bodyPart2.getBurnTime() > 5.0f || bodyPart2.bitten() || bodyPart2.deepWounded() || bodyPart2.isSplint() || bodyPart2.getFractureTime() > 0.0f || bodyPart2.haveGlass()) {
                return "rightheavy";
            }
        }
        if (bodyPart.getScratchTime() > 5.0f || bodyPart.getCutTime() > 7.0f || bodyPart.getBurnTime() > 0.0f) {
            return "leftlight";
        }
        if (bodyPart2.getScratchTime() > 5.0f || bodyPart2.getCutTime() > 7.0f || bodyPart2.getBurnTime() > 0.0f) {
            return "rightlight";
        }
        return "";
    }
    
    public float getVariableFloat(final String s, final float n) {
        return this.getGameVariablesInternal().getVariableFloat(s, n);
    }
    
    public boolean getVariableBoolean(final String s) {
        return this.getGameVariablesInternal().getVariableBoolean(s);
    }
    
    public boolean isVariable(final String s, final String s2) {
        return this.getGameVariablesInternal().isVariable(s, s2);
    }
    
    public boolean containsVariable(final String s) {
        return this.getGameVariablesInternal().containsVariable(s);
    }
    
    public Iterable<IAnimationVariableSlot> getGameVariables() {
        return this.getGameVariablesInternal().getGameVariables();
    }
    
    private AnimationVariableSource getGameVariablesInternal() {
        if (this.m_PlaybackGameVariables != null) {
            return this.m_PlaybackGameVariables;
        }
        return this.m_GameVariables;
    }
    
    public AnimationVariableSource startPlaybackGameVariables() {
        if (this.m_PlaybackGameVariables != null) {
            DebugLog.General.error((Object)"Error! PlaybackGameVariables is already active.");
            return this.m_PlaybackGameVariables;
        }
        final AnimationVariableSource playbackGameVariables = new AnimationVariableSource();
        for (final IAnimationVariableSlot animationVariableSlot : this.getGameVariables()) {
            final AnimationVariableType type = animationVariableSlot.getType();
            switch (type) {
                case String: {
                    playbackGameVariables.setVariable(animationVariableSlot.getKey(), animationVariableSlot.getValueString());
                    continue;
                }
                case Float: {
                    playbackGameVariables.setVariable(animationVariableSlot.getKey(), animationVariableSlot.getValueFloat());
                    continue;
                }
                case Boolean: {
                    playbackGameVariables.setVariable(animationVariableSlot.getKey(), animationVariableSlot.getValueBool());
                    continue;
                }
                case Void: {
                    continue;
                }
                default: {
                    DebugLog.General.error("Error! Variable type not handled: %s", type.toString());
                    continue;
                }
            }
        }
        return this.m_PlaybackGameVariables = playbackGameVariables;
    }
    
    public void endPlaybackGameVariables(final AnimationVariableSource animationVariableSource) {
        if (this.m_PlaybackGameVariables != animationVariableSource) {
            DebugLog.General.error((Object)"Error! Playback GameVariables do not match.");
        }
        this.m_PlaybackGameVariables = null;
    }
    
    public void playbackSetCurrentStateSnapshot(final ActionStateSnapshot playbackStateSnapshot) {
        if (this.actionContext == null) {
            return;
        }
        this.actionContext.setPlaybackStateSnapshot(playbackStateSnapshot);
    }
    
    public ActionStateSnapshot playbackRecordCurrentStateSnapshot() {
        if (this.actionContext == null) {
            return null;
        }
        return this.actionContext.getPlaybackStateSnapshot();
    }
    
    public String GetVariable(final String s) {
        return this.getVariableString(s);
    }
    
    public void SetVariable(final String s, final String s2) {
        this.setVariable(s, s2);
    }
    
    public void ClearVariable(final String s) {
        this.clearVariable(s);
    }
    
    @Override
    public void actionStateChanged(final ActionContext actionContext) {
        final ArrayList<String> stateNames = L_actionStateChanged.stateNames;
        PZArrayUtil.listConvert(actionContext.getChildStates(), (List<String>)stateNames, actionState -> actionState.name);
        this.advancedAnimator.SetState(actionContext.getCurrentStateName(), stateNames);
        try {
            final StateMachine stateMachine = this.stateMachine;
            ++stateMachine.activeStateChanged;
            State defaultState = this.m_stateUpdateLookup.get(actionContext.getCurrentStateName().toLowerCase());
            if (defaultState == null) {
                defaultState = this.defaultState;
            }
            final ArrayList<State> states = L_actionStateChanged.states;
            PZArrayUtil.listConvert(actionContext.getChildStates(), (List<State>)states, this.m_stateUpdateLookup, (actionState2, hashMap) -> hashMap.get(actionState2.name.toLowerCase()));
            this.stateMachine.changeState(defaultState, states);
        }
        finally {
            final StateMachine stateMachine2 = this.stateMachine;
            --stateMachine2.activeStateChanged;
        }
    }
    
    public boolean isFallOnFront() {
        return this.fallOnFront;
    }
    
    public void setFallOnFront(final boolean fallOnFront) {
        this.fallOnFront = fallOnFront;
    }
    
    public boolean isHitFromBehind() {
        return this.hitFromBehind;
    }
    
    public void setHitFromBehind(final boolean hitFromBehind) {
        this.hitFromBehind = hitFromBehind;
    }
    
    public void reportEvent(final String s) {
        this.actionContext.reportEvent(s);
    }
    
    public void StartTimedActionAnim(final String s) {
        this.StartTimedActionAnim(s, null);
    }
    
    public void StartTimedActionAnim(final String s, final String s2) {
        this.reportEvent(s);
        if (s2 != null) {
            this.setVariable("TimedActionType", s2);
        }
        this.resetModelNextFrame();
    }
    
    public void StopTimedActionAnim() {
        this.clearVariable("TimedActionType");
        this.reportEvent("Event_TA_Exit");
        this.resetModelNextFrame();
    }
    
    public boolean hasHitReaction() {
        return !StringUtils.isNullOrEmpty(this.getHitReaction());
    }
    
    public String getHitReaction() {
        return this.hitReaction;
    }
    
    public void setHitReaction(final String hitReaction) {
        this.hitReaction = hitReaction;
    }
    
    public void CacheEquipped() {
        this.cacheEquiped[0] = this.getPrimaryHandItem();
        this.cacheEquiped[1] = this.getSecondaryHandItem();
    }
    
    public InventoryItem GetPrimaryEquippedCache() {
        return (this.cacheEquiped[0] != null && this.inventory.contains(this.cacheEquiped[0])) ? this.cacheEquiped[0] : null;
    }
    
    public InventoryItem GetSecondaryEquippedCache() {
        return (this.cacheEquiped[1] != null && this.inventory.contains(this.cacheEquiped[1])) ? this.cacheEquiped[1] : null;
    }
    
    public void ClearEquippedCache() {
        this.cacheEquiped[0] = null;
        this.cacheEquiped[1] = null;
    }
    
    public boolean isBehind(final IsoGameCharacter isoGameCharacter) {
        final Vector2 set = IsoGameCharacter.tempVector2_1.set(this.getX(), this.getY());
        final Vector2 set2;
        final Vector2 vector2 = set2 = IsoGameCharacter.tempVector2_2.set(isoGameCharacter.getX(), isoGameCharacter.getY());
        set2.x -= set.x;
        final Vector2 vector3 = vector2;
        vector3.y -= set.y;
        final Vector2 forwardDirection = isoGameCharacter.getForwardDirection();
        vector2.normalize();
        forwardDirection.normalize();
        return vector2.dot(forwardDirection) > 0.6;
    }
    
    public void resetEquippedHandsModels() {
        if (GameServer.bServer && !ServerGUI.isCreated()) {
            return;
        }
        if (!this.hasActiveModel()) {
            return;
        }
        ModelManager.instance.ResetEquippedNextFrame(this);
    }
    
    public AnimatorDebugMonitor getDebugMonitor() {
        return this.advancedAnimator.getDebugMonitor();
    }
    
    public void setDebugMonitor(final AnimatorDebugMonitor debugMonitor) {
        this.advancedAnimator.setDebugMonitor(debugMonitor);
    }
    
    public boolean isAimAtFloor() {
        return this.bAimAtFloor;
    }
    
    public void setAimAtFloor(final boolean bAimAtFloor) {
        this.bAimAtFloor = bAimAtFloor;
    }
    
    public String testDotSide(final IsoMovingObject isoMovingObject) {
        final Vector2 lookVector = this.getLookVector(l_testDotSide.v1);
        final Vector2 set = l_testDotSide.v2.set(this.getX(), this.getY());
        final Vector2 set2 = l_testDotSide.v3.set(isoMovingObject.x - set.x, isoMovingObject.y - set.y);
        set2.normalize();
        final float dot = Vector2.dot(set2.x, set2.y, lookVector.x, lookVector.y);
        if (dot > 0.7) {
            return "FRONT";
        }
        if (dot < 0.0f && dot < -0.5) {
            return "BEHIND";
        }
        final float x = isoMovingObject.x;
        final float y = isoMovingObject.y;
        final float x2 = set.x;
        final float y2 = set.y;
        if ((x - x2) * (set.y + lookVector.y - y2) - (y - y2) * (set.x + lookVector.x - x2) > 0.0f) {
            return "RIGHT";
        }
        return "LEFT";
    }
    
    public void addBasicPatch(BloodBodyPartType fromIndex) {
        if (!(this instanceof IHumanVisual)) {
            return;
        }
        if (fromIndex == null) {
            fromIndex = BloodBodyPartType.FromIndex(Rand.Next(0, BloodBodyPartType.MAX.index()));
        }
        final HumanVisual humanVisual = ((IHumanVisual)this).getHumanVisual();
        this.getItemVisuals(IsoGameCharacter.tempItemVisuals);
        BloodClothingType.addBasicPatch(fromIndex, humanVisual, IsoGameCharacter.tempItemVisuals);
        this.bUpdateModelTextures = true;
        this.bUpdateEquippedTextures = true;
        if (!GameServer.bServer && this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
            LuaEventManager.triggerEvent("OnClothingUpdated", this);
        }
    }
    
    public void addHole(final BloodBodyPartType bloodBodyPartType) {
        this.addHole(bloodBodyPartType, false);
    }
    
    public void addHole(BloodBodyPartType fromIndex, final boolean b) {
        if (!(this instanceof IHumanVisual)) {
            return;
        }
        if (fromIndex == null) {
            fromIndex = BloodBodyPartType.FromIndex(OutfitRNG.Next(0, BloodBodyPartType.MAX.index()));
        }
        final HumanVisual humanVisual = ((IHumanVisual)this).getHumanVisual();
        this.getItemVisuals(IsoGameCharacter.tempItemVisuals);
        BloodClothingType.addHole(fromIndex, humanVisual, IsoGameCharacter.tempItemVisuals, b);
        this.bUpdateModelTextures = true;
        if (!GameServer.bServer && this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
            LuaEventManager.triggerEvent("OnClothingUpdated", this);
            if (GameClient.bClient) {
                GameClient.instance.sendClothing((IsoPlayer)this, "", null);
            }
        }
    }
    
    public void addDirt(BloodBodyPartType fromIndex, Integer value, final boolean b) {
        final HumanVisual humanVisual = ((IHumanVisual)this).getHumanVisual();
        if (value == null) {
            value = OutfitRNG.Next(5, 10);
        }
        boolean b2 = false;
        if (fromIndex == null) {
            b2 = true;
        }
        this.getItemVisuals(IsoGameCharacter.tempItemVisuals);
        for (int i = 0; i < value; ++i) {
            if (b2) {
                fromIndex = BloodBodyPartType.FromIndex(OutfitRNG.Next(0, BloodBodyPartType.MAX.index()));
            }
            BloodClothingType.addDirt(fromIndex, humanVisual, IsoGameCharacter.tempItemVisuals, b);
        }
        this.bUpdateModelTextures = true;
        if (!GameServer.bServer && this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
            LuaEventManager.triggerEvent("OnClothingUpdated", this);
        }
    }
    
    public void addBlood(BloodBodyPartType fromIndex, final boolean b, final boolean b2, final boolean b3) {
        final HumanVisual humanVisual = ((IHumanVisual)this).getHumanVisual();
        int splatNumber = 1;
        boolean b4 = false;
        if (fromIndex == null) {
            b4 = true;
        }
        if (this.getPrimaryHandItem() instanceof HandWeapon) {
            splatNumber = ((HandWeapon)this.getPrimaryHandItem()).getSplatNumber();
            if (OutfitRNG.Next(15) < this.getWeaponLevel()) {
                --splatNumber;
            }
        }
        if (b2) {
            splatNumber = 20;
        }
        if (b) {
            splatNumber = 5;
        }
        if (this.isZombie()) {
            splatNumber += 8;
        }
        this.getItemVisuals(IsoGameCharacter.tempItemVisuals);
        for (int i = 0; i < splatNumber; ++i) {
            if (b4) {
                fromIndex = BloodBodyPartType.FromIndex(OutfitRNG.Next(0, BloodBodyPartType.MAX.index()));
                if (this.getPrimaryHandItem() != null && this.getPrimaryHandItem() instanceof HandWeapon) {
                    final HandWeapon handWeapon = (HandWeapon)this.getPrimaryHandItem();
                    if (handWeapon.getBloodLevel() < 1.0f) {
                        handWeapon.setBloodLevel(handWeapon.getBloodLevel() + 0.02f);
                        this.bUpdateEquippedTextures = true;
                    }
                }
            }
            BloodClothingType.addBlood(fromIndex, humanVisual, IsoGameCharacter.tempItemVisuals, b3);
        }
        this.bUpdateModelTextures = true;
        if (!GameServer.bServer && this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
            LuaEventManager.triggerEvent("OnClothingUpdated", this);
        }
    }
    
    public float getBodyPartClothingDefense(final Integer n, final boolean b, final boolean b2) {
        float b3 = 0.0f;
        this.getItemVisuals(IsoGameCharacter.tempItemVisuals);
        for (int i = IsoGameCharacter.tempItemVisuals.size() - 1; i >= 0; --i) {
            final ItemVisual itemVisual = IsoGameCharacter.tempItemVisuals.get(i);
            final Item scriptItem = itemVisual.getScriptItem();
            if (scriptItem != null) {
                final ArrayList<BloodClothingType> bloodClothingType = scriptItem.getBloodClothingType();
                if (bloodClothingType != null) {
                    final ArrayList<BloodBodyPartType> coveredParts = BloodClothingType.getCoveredParts(bloodClothingType);
                    if (coveredParts != null) {
                        InventoryItem inventoryItem = itemVisual.getInventoryItem();
                        if (inventoryItem == null) {
                            inventoryItem = InventoryItemFactory.CreateItem(itemVisual.getItemType());
                            if (inventoryItem == null) {
                                continue;
                            }
                        }
                        for (int j = 0; j < coveredParts.size(); ++j) {
                            if (inventoryItem instanceof Clothing && coveredParts.get(j).index() == n && itemVisual.getHole(coveredParts.get(j)) == 0.0f) {
                                b3 += ((Clothing)inventoryItem).getDefForPart(coveredParts.get(j), b, b2);
                                break;
                            }
                        }
                    }
                }
            }
        }
        return Math.min(100.0f, b3);
    }
    
    public boolean isBumped() {
        return !StringUtils.isNullOrWhitespace(this.getBumpType());
    }
    
    public boolean isBumpDone() {
        return this.m_isBumpDone;
    }
    
    public void setBumpDone(final boolean isBumpDone) {
        this.m_isBumpDone = isBumpDone;
    }
    
    public boolean isBumpFall() {
        return this.m_bumpFall;
    }
    
    public void setBumpFall(final boolean bumpFall) {
        this.m_bumpFall = bumpFall;
    }
    
    public boolean isBumpStaggered() {
        return this.m_bumpStaggered;
    }
    
    public void setBumpStaggered(final boolean bumpStaggered) {
        this.m_bumpStaggered = bumpStaggered;
    }
    
    public String getBumpType() {
        return this.bumpType;
    }
    
    public void setBumpType(final String s) {
        if (StringUtils.equalsIgnoreCase(this.bumpType, s)) {
            this.bumpType = s;
            return;
        }
        final boolean bumped = this.isBumped();
        this.bumpType = s;
        final boolean bumped2 = this.isBumped();
        if (bumped2 != bumped) {
            this.setBumpStaggered(bumped2);
        }
    }
    
    public String getBumpFallType() {
        return this.m_bumpFallType;
    }
    
    public void setBumpFallType(final String bumpFallType) {
        this.m_bumpFallType = bumpFallType;
    }
    
    public IsoGameCharacter getBumpedChr() {
        return this.bumpedChr;
    }
    
    public void setBumpedChr(final IsoGameCharacter bumpedChr) {
        this.bumpedChr = bumpedChr;
    }
    
    public long getLastBump() {
        return this.lastBump;
    }
    
    public void setLastBump(final long lastBump) {
        this.lastBump = lastBump;
    }
    
    public boolean isSitOnGround() {
        return this.sitOnGround;
    }
    
    public void setSitOnGround(final boolean sitOnGround) {
        this.sitOnGround = sitOnGround;
    }
    
    @Override
    public String getUID() {
        return this.m_UID;
    }
    
    protected HashMap<String, State> getStateUpdateLookup() {
        return this.m_stateUpdateLookup;
    }
    
    public boolean isRunning() {
        return (this.getMoodles() == null || this.getMoodles().getMoodleLevel(MoodleType.Endurance) < 3) && this.bRunning;
    }
    
    public void setRunning(final boolean bRunning) {
        this.bRunning = bRunning;
    }
    
    public boolean isSprinting() {
        return (!this.bSprinting || this.canSprint()) && this.bSprinting;
    }
    
    public void setSprinting(final boolean bSprinting) {
        this.bSprinting = bSprinting;
    }
    
    public boolean canSprint() {
        if (this instanceof IsoPlayer && !((IsoPlayer)this).isAllowSprint()) {
            return false;
        }
        if ("Tutorial".equals(Core.GameMode)) {
            return true;
        }
        final InventoryItem primaryHandItem = this.getPrimaryHandItem();
        if (primaryHandItem != null && primaryHandItem.isEquippedNoSprint()) {
            return false;
        }
        final InventoryItem secondaryHandItem = this.getSecondaryHandItem();
        return (secondaryHandItem == null || !secondaryHandItem.isEquippedNoSprint()) && (this.getMoodles() == null || this.getMoodles().getMoodleLevel(MoodleType.Endurance) < 2);
    }
    
    public void postUpdateModelTextures() {
        this.bUpdateModelTextures = true;
    }
    
    public ModelInstanceTextureCreator getTextureCreator() {
        return this.textureCreator;
    }
    
    public void setTextureCreator(final ModelInstanceTextureCreator textureCreator) {
        this.textureCreator = textureCreator;
    }
    
    public void postUpdateEquippedTextures() {
        this.bUpdateEquippedTextures = true;
    }
    
    public ArrayList<ModelInstance> getReadyModelData() {
        return this.readyModelData;
    }
    
    public boolean getIgnoreMovement() {
        return this.ignoreMovement;
    }
    
    public void setIgnoreMovement(final boolean ignoreMovement) {
        if (this instanceof IsoPlayer && ignoreMovement) {
            ((IsoPlayer)this).networkAI.needToUpdate();
        }
        this.ignoreMovement = ignoreMovement;
    }
    
    public boolean isSneaking() {
        return this.getVariableFloat("WalkInjury", 0.0f) <= 0.5f && this.bSneaking;
    }
    
    public void setSneaking(final boolean bSneaking) {
        this.bSneaking = bSneaking;
    }
    
    public GameCharacterAIBrain getGameCharacterAIBrain() {
        return this.GameCharacterAIBrain;
    }
    
    public float getMoveDelta() {
        return this.m_moveDelta;
    }
    
    public void setMoveDelta(final float moveDelta) {
        this.m_moveDelta = moveDelta;
    }
    
    public float getTurnDelta() {
        if (this.isSprinting()) {
            return this.m_turnDeltaSprinting;
        }
        if (this.isRunning()) {
            return this.m_turnDeltaRunning;
        }
        return this.m_turnDeltaNormal;
    }
    
    public void setTurnDelta(final float turnDeltaNormal) {
        this.m_turnDeltaNormal = turnDeltaNormal;
    }
    
    public float getChopTreeSpeed() {
        return (this.Traits.Axeman.isSet() ? 1.25f : 1.0f) * GameTime.getAnimSpeedFix();
    }
    
    public boolean testDefense(final IsoZombie attackedBy) {
        if (!this.testDotSide(attackedBy).equals("FRONT") || attackedBy.bCrawling || this.getSurroundingAttackingZombies() > 3) {
            return false;
        }
        int n = 0;
        if ("KnifeDeath".equals(this.getVariableString("ZombieHitReaction"))) {
            n += 30;
        }
        int n2 = n + this.getWeaponLevel() * 3 + this.getPerkLevel(PerkFactory.Perks.Fitness) * 2 + this.getPerkLevel(PerkFactory.Perks.Strength) * 2 - this.getSurroundingAttackingZombies() * 5 - this.getMoodles().getMoodleLevel(MoodleType.Endurance) * 2 - this.getMoodles().getMoodleLevel(MoodleType.HeavyLoad) * 2 - this.getMoodles().getMoodleLevel(MoodleType.Tired) * 3;
        if (SandboxOptions.instance.Lore.Strength.getValue() == 1) {
            n2 -= 7;
        }
        if (SandboxOptions.instance.Lore.Strength.getValue() == 3) {
            n2 += 7;
        }
        if (Rand.Next(100) < n2) {
            this.setAttackedBy(attackedBy);
            this.setHitReaction(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, attackedBy.getVariableString("PlayerHitReaction")));
            return true;
        }
        return false;
    }
    
    public int getSurroundingAttackingZombies() {
        IsoGameCharacter.movingStatic.clear();
        final IsoGridSquare currentSquare = this.getCurrentSquare();
        if (currentSquare == null) {
            return 0;
        }
        IsoGameCharacter.movingStatic.addAll(currentSquare.getMovingObjects());
        if (currentSquare.n != null) {
            IsoGameCharacter.movingStatic.addAll(currentSquare.n.getMovingObjects());
        }
        if (currentSquare.s != null) {
            IsoGameCharacter.movingStatic.addAll(currentSquare.s.getMovingObjects());
        }
        if (currentSquare.e != null) {
            IsoGameCharacter.movingStatic.addAll(currentSquare.e.getMovingObjects());
        }
        if (currentSquare.w != null) {
            IsoGameCharacter.movingStatic.addAll(currentSquare.w.getMovingObjects());
        }
        if (currentSquare.nw != null) {
            IsoGameCharacter.movingStatic.addAll(currentSquare.nw.getMovingObjects());
        }
        if (currentSquare.sw != null) {
            IsoGameCharacter.movingStatic.addAll(currentSquare.sw.getMovingObjects());
        }
        if (currentSquare.se != null) {
            IsoGameCharacter.movingStatic.addAll(currentSquare.se.getMovingObjects());
        }
        if (currentSquare.ne != null) {
            IsoGameCharacter.movingStatic.addAll(currentSquare.ne.getMovingObjects());
        }
        int n = 0;
        for (int i = 0; i < IsoGameCharacter.movingStatic.size(); ++i) {
            final IsoZombie isoZombie = Type.tryCastTo(IsoGameCharacter.movingStatic.get(i), IsoZombie.class);
            if (isoZombie != null) {
                if (isoZombie.target == this) {
                    if (this.DistToSquared(isoZombie) < 0.80999994f) {
                        if (isoZombie.isCurrentState(AttackState.instance()) || isoZombie.isCurrentState(AttackNetworkState.instance()) || isoZombie.isCurrentState(LungeState.instance()) || isoZombie.isCurrentState(LungeNetworkState.instance())) {
                            ++n;
                        }
                    }
                }
            }
        }
        return n;
    }
    
    public float checkIsNearWall() {
        if (!this.bSneaking || this.getCurrentSquare() == null) {
            this.setVariable("nearWallCrouching", false);
            return 0.0f;
        }
        final IsoGridSquare isoGridSquare = this.getCurrentSquare().nav[IsoDirections.N.index()];
        final IsoGridSquare isoGridSquare2 = this.getCurrentSquare().nav[IsoDirections.S.index()];
        final IsoGridSquare isoGridSquare3 = this.getCurrentSquare().nav[IsoDirections.E.index()];
        final IsoGridSquare isoGridSquare4 = this.getCurrentSquare().nav[IsoDirections.W.index()];
        if (isoGridSquare != null) {
            final float gridSneakModifier = isoGridSquare.getGridSneakModifier(true);
            if (gridSneakModifier > 1.0f) {
                this.setVariable("nearWallCrouching", true);
                return gridSneakModifier;
            }
        }
        if (isoGridSquare2 != null) {
            final float gridSneakModifier2 = isoGridSquare2.getGridSneakModifier(false);
            final float gridSneakModifier3 = isoGridSquare2.getGridSneakModifier(true);
            if (gridSneakModifier2 > 1.0f || gridSneakModifier3 > 1.0f) {
                this.setVariable("nearWallCrouching", true);
                return (gridSneakModifier2 > 1.0f) ? gridSneakModifier2 : gridSneakModifier3;
            }
        }
        if (isoGridSquare3 != null) {
            final float gridSneakModifier4 = isoGridSquare3.getGridSneakModifier(false);
            final float gridSneakModifier5 = isoGridSquare3.getGridSneakModifier(true);
            if (gridSneakModifier4 > 1.0f || gridSneakModifier5 > 1.0f) {
                this.setVariable("nearWallCrouching", true);
                return (gridSneakModifier4 > 1.0f) ? gridSneakModifier4 : gridSneakModifier5;
            }
        }
        if (isoGridSquare4 != null) {
            final float gridSneakModifier6 = isoGridSquare4.getGridSneakModifier(false);
            final float gridSneakModifier7 = isoGridSquare4.getGridSneakModifier(true);
            if (gridSneakModifier6 > 1.0f || gridSneakModifier7 > 1.0f) {
                this.setVariable("nearWallCrouching", true);
                return (gridSneakModifier6 > 1.0f) ? gridSneakModifier6 : gridSneakModifier7;
            }
        }
        final float gridSneakModifier8 = this.getCurrentSquare().getGridSneakModifier(false);
        if (gridSneakModifier8 > 1.0f) {
            this.setVariable("nearWallCrouching", true);
            return gridSneakModifier8;
        }
        if (this instanceof IsoPlayer && ((IsoPlayer)this).isNearVehicle()) {
            this.setVariable("nearWallCrouching", true);
            return 6.0f;
        }
        this.setVariable("nearWallCrouching", false);
        return 0.0f;
    }
    
    public float getBeenSprintingFor() {
        return this.BeenSprintingFor;
    }
    
    public void setBeenSprintingFor(float beenSprintingFor) {
        if (beenSprintingFor < 0.0f) {
            beenSprintingFor = 0.0f;
        }
        if (beenSprintingFor > 100.0f) {
            beenSprintingFor = 100.0f;
        }
        this.BeenSprintingFor = beenSprintingFor;
    }
    
    public boolean isHideWeaponModel() {
        return this.hideWeaponModel;
    }
    
    public void setHideWeaponModel(final boolean hideWeaponModel) {
        if (this.hideWeaponModel != hideWeaponModel) {
            this.hideWeaponModel = hideWeaponModel;
            this.resetEquippedHandsModels();
        }
    }
    
    public void setIsAiming(boolean isAiming) {
        if (this.ignoreAimingInput) {
            isAiming = false;
        }
        if ((this instanceof IsoPlayer && !((IsoPlayer)this).isAttackAnimThrowTimeOut()) || this.isAttackAnim() || this.getVariableBoolean("ShoveAnim")) {
            isAiming = true;
        }
        this.isAiming = isAiming;
    }
    
    public boolean isAiming() {
        if (GameClient.bClient && this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer() && DebugOptions.instance.MultiplayerAttackPlayer.getValue()) {
            return false;
        }
        if (this.isNPC) {
            return this.NPCGetAiming();
        }
        return this.isAiming;
    }
    
    public void resetBeardGrowingTime() {
        this.beardGrowTiming = (float)this.getHoursSurvived();
        if (GameClient.bClient && this instanceof IsoPlayer) {
            GameClient.instance.sendVisual((IsoPlayer)this);
        }
    }
    
    public void resetHairGrowingTime() {
        this.hairGrowTiming = (float)this.getHoursSurvived();
        if (GameClient.bClient && this instanceof IsoPlayer) {
            GameClient.instance.sendVisual((IsoPlayer)this);
        }
    }
    
    public void fallenOnKnees() {
        if (this instanceof IsoPlayer && !((IsoPlayer)this).isLocalPlayer()) {
            return;
        }
        if (this.isInvincible()) {
            return;
        }
        this.helmetFall(false);
        BloodBodyPartType bloodBodyPartType = BloodBodyPartType.FromIndex(Rand.Next(BloodBodyPartType.Hand_L.index(), BloodBodyPartType.Torso_Upper.index()));
        if (Rand.NextBool(2)) {
            bloodBodyPartType = BloodBodyPartType.FromIndex(Rand.Next(BloodBodyPartType.UpperLeg_L.index(), BloodBodyPartType.Back.index()));
        }
        for (int i = 0; i < 4; ++i) {
            BloodBodyPartType bloodBodyPartType2 = BloodBodyPartType.FromIndex(Rand.Next(BloodBodyPartType.Hand_L.index(), BloodBodyPartType.Torso_Upper.index()));
            if (Rand.NextBool(2)) {
                bloodBodyPartType2 = BloodBodyPartType.FromIndex(Rand.Next(BloodBodyPartType.UpperLeg_L.index(), BloodBodyPartType.Back.index()));
            }
            this.addDirt(bloodBodyPartType2, Rand.Next(2, 6), false);
        }
        if (!Rand.NextBool(2)) {
            return;
        }
        if (Rand.NextBool(4)) {
            this.dropHandItems();
        }
        this.addHole(bloodBodyPartType);
        this.addBlood(bloodBodyPartType, true, false, false);
        final BodyPart bodyPart = this.getBodyDamage().getBodyPart(BodyPartType.FromIndex(bloodBodyPartType.index()));
        if (bodyPart.scratched()) {
            bodyPart.generateDeepWound();
        }
        else {
            bodyPart.setScratched(true, true);
        }
    }
    
    public void addVisualDamage(final String s) {
        this.addBodyVisualFromItemType(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
    }
    
    protected ItemVisual addBodyVisualFromItemType(final String s) {
        final Item item = ScriptManager.instance.getItem(s);
        if (item == null || StringUtils.isNullOrWhitespace(item.getClothingItem())) {
            return null;
        }
        return this.addBodyVisualFromClothingItemName(item.getClothingItem());
    }
    
    protected ItemVisual addBodyVisualFromClothingItemName(final String s) {
        final IHumanVisual humanVisual = Type.tryCastTo(this, IHumanVisual.class);
        if (humanVisual == null) {
            return null;
        }
        final String itemTypeForClothingItem = ScriptManager.instance.getItemTypeForClothingItem(s);
        if (itemTypeForClothingItem == null) {
            return null;
        }
        final Item item = ScriptManager.instance.getItem(itemTypeForClothingItem);
        if (item == null) {
            return null;
        }
        final ClothingItem clothingItemAsset = item.getClothingItemAsset();
        if (clothingItemAsset == null) {
            return null;
        }
        final ClothingItemReference clothingItemReference = new ClothingItemReference();
        clothingItemReference.itemGUID = clothingItemAsset.m_GUID;
        clothingItemReference.randomize();
        final ItemVisual e = new ItemVisual();
        e.setItemType(itemTypeForClothingItem);
        e.synchWithOutfit(clothingItemReference);
        if (!this.isDuplicateBodyVisual(e)) {
            humanVisual.getHumanVisual().getBodyVisuals().add(e);
            return e;
        }
        return null;
    }
    
    protected boolean isDuplicateBodyVisual(final ItemVisual itemVisual) {
        final IHumanVisual humanVisual = Type.tryCastTo(this, IHumanVisual.class);
        if (humanVisual == null) {
            return false;
        }
        final ItemVisuals bodyVisuals = humanVisual.getHumanVisual().getBodyVisuals();
        for (int i = 0; i < bodyVisuals.size(); ++i) {
            final ItemVisual itemVisual2 = bodyVisuals.get(i);
            if (itemVisual.getClothingItemName().equals(itemVisual2.getClothingItemName()) && itemVisual.getTextureChoice() == itemVisual2.getTextureChoice() && itemVisual.getBaseTexture() == itemVisual2.getBaseTexture()) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isCriticalHit() {
        return this.isCrit;
    }
    
    public void setCriticalHit(final boolean isCrit) {
        this.isCrit = isCrit;
    }
    
    public float getRunSpeedModifier() {
        return this.runSpeedModifier;
    }
    
    public void startMuzzleFlash() {
        final float max = Math.max(GameTime.getInstance().getNight() * 0.8f, 0.2f);
        IsoWorld.instance.CurrentCell.getLamppostPositions().add(new IsoLightSource((int)this.getX(), (int)this.getY(), (int)this.getZ(), 0.8f * max, 0.8f * max, 0.6f * max, 18, 6));
        this.m_muzzleFlash = System.currentTimeMillis();
    }
    
    public boolean isMuzzleFlash() {
        return (Core.bDebug && DebugOptions.instance.ModelRenderMuzzleflash.getValue()) || this.m_muzzleFlash > System.currentTimeMillis() - 50L;
    }
    
    public boolean isNPC() {
        return this.isNPC;
    }
    
    public void setNPC(final boolean isNPC) {
        if (isNPC && this.GameCharacterAIBrain == null) {
            this.GameCharacterAIBrain = new GameCharacterAIBrain(this);
        }
        this.isNPC = isNPC;
    }
    
    public void NPCSetRunning(final boolean bRunning) {
        this.GameCharacterAIBrain.HumanControlVars.bRunning = bRunning;
    }
    
    public boolean NPCGetRunning() {
        return this.GameCharacterAIBrain.HumanControlVars.bRunning;
    }
    
    public void NPCSetJustMoved(final boolean justMoved) {
        this.GameCharacterAIBrain.HumanControlVars.JustMoved = justMoved;
    }
    
    public void NPCSetAiming(final boolean bAiming) {
        this.GameCharacterAIBrain.HumanControlVars.bAiming = bAiming;
    }
    
    public boolean NPCGetAiming() {
        return this.GameCharacterAIBrain.HumanControlVars.bAiming;
    }
    
    public void NPCSetAttack(final boolean initiateAttack) {
        this.GameCharacterAIBrain.HumanControlVars.initiateAttack = initiateAttack;
    }
    
    public void NPCSetMelee(final boolean bMelee) {
        this.GameCharacterAIBrain.HumanControlVars.bMelee = bMelee;
    }
    
    public void setMetabolicTarget(final Metabolics metabolics) {
        if (metabolics != null) {
            this.setMetabolicTarget(metabolics.getMet());
        }
    }
    
    public void setMetabolicTarget(final float metabolicTarget) {
        if (this.getBodyDamage() != null && this.getBodyDamage().getThermoregulator() != null) {
            this.getBodyDamage().getThermoregulator().setMetabolicTarget(metabolicTarget);
        }
    }
    
    public double getThirstMultiplier() {
        if (this.getBodyDamage() != null && this.getBodyDamage().getThermoregulator() != null) {
            return this.getBodyDamage().getThermoregulator().getFluidsMultiplier();
        }
        return 1.0;
    }
    
    public double getHungerMultiplier() {
        return 1.0;
    }
    
    public double getFatiqueMultiplier() {
        if (this.getBodyDamage() != null && this.getBodyDamage().getThermoregulator() != null) {
            return this.getBodyDamage().getThermoregulator().getFatigueMultiplier();
        }
        return 1.0;
    }
    
    public float getTimedActionTimeModifier() {
        return 1.0f;
    }
    
    public boolean addHoleFromZombieAttacks(final BloodBodyPartType bloodBodyPartType, final boolean b) {
        this.getItemVisuals(IsoGameCharacter.tempItemVisuals);
        ItemVisual itemVisual = null;
        for (int i = IsoGameCharacter.tempItemVisuals.size() - 1; i >= 0; --i) {
            final ItemVisual itemVisual2 = IsoGameCharacter.tempItemVisuals.get(i);
            final Item scriptItem = itemVisual2.getScriptItem();
            if (scriptItem != null) {
                final ArrayList<BloodClothingType> bloodClothingType = scriptItem.getBloodClothingType();
                if (bloodClothingType != null) {
                    final ArrayList<BloodBodyPartType> coveredParts = BloodClothingType.getCoveredParts(bloodClothingType);
                    for (int j = 0; j < coveredParts.size(); ++j) {
                        if (bloodBodyPartType == coveredParts.get(j)) {
                            itemVisual = itemVisual2;
                            break;
                        }
                    }
                    if (itemVisual != null) {
                        break;
                    }
                }
            }
        }
        float max = 0.0f;
        boolean b2 = false;
        if (itemVisual != null && itemVisual.getInventoryItem() != null && itemVisual.getInventoryItem() instanceof Clothing) {
            final Clothing clothing = (Clothing)itemVisual.getInventoryItem();
            clothing.getPatchType(bloodBodyPartType);
            max = Math.max(30.0f, 100.0f - clothing.getDefForPart(bloodBodyPartType, !b, false) / 1.5f);
        }
        if (Rand.Next(100) < max) {
            this.addHole(bloodBodyPartType);
            b2 = true;
        }
        return b2;
    }
    
    protected void updateBandages() {
        IsoGameCharacter.s_bandages.update(this);
    }
    
    public float getTotalBlood() {
        float n = 0.0f;
        if (this.getWornItems() == null) {
            return n;
        }
        for (int i = 0; i < this.getWornItems().size(); ++i) {
            final InventoryItem item = this.getWornItems().get(i).getItem();
            if (item instanceof Clothing) {
                n += ((Clothing)item).getBloodlevel();
            }
        }
        return n + ((HumanVisual)this.getVisual()).getTotalBlood();
    }
    
    public void attackFromWindowsLunge(final IsoZombie isoZombie) {
        if (this.lungeFallTimer > 0.0f || (int)this.getZ() != (int)isoZombie.getZ() || isoZombie.isDead() || this.getCurrentSquare().isDoorBlockedTo(isoZombie.getCurrentSquare()) || this.getCurrentSquare().isWallTo(isoZombie.getCurrentSquare()) || this.getCurrentSquare().isWindowTo(isoZombie.getCurrentSquare())) {
            return;
        }
        if (this.getVehicle() != null) {
            return;
        }
        if (!this.DoSwingCollisionBoneCheck(isoZombie, isoZombie.getAnimationPlayer().getSkinningBoneIndex("Bip01_R_Hand", -1), 1.0f)) {
            return;
        }
        this.lungeFallTimer = 200.0f;
        this.setIsAiming(false);
        boolean bumpFall = false;
        int b = 30 + this.getMoodles().getMoodleLevel(MoodleType.Endurance) * 3 + this.getMoodles().getMoodleLevel(MoodleType.HeavyLoad) * 5 - this.getPerkLevel(PerkFactory.Perks.Fitness) * 2;
        final BodyPart bodyPart = this.getBodyDamage().getBodyPart(BodyPartType.Torso_Lower);
        if (bodyPart.getAdditionalPain(true) > 20.0f) {
            b += (int)((bodyPart.getAdditionalPain(true) - 20.0f) / 10.0f);
        }
        if (this.Traits.Clumsy.isSet()) {
            b += 10;
        }
        if (this.Traits.Graceful.isSet()) {
            b -= 10;
        }
        if (this.Traits.VeryUnderweight.isSet()) {
            b += 20;
        }
        if (this.Traits.Underweight.isSet()) {
            b += 10;
        }
        if (this.Traits.Obese.isSet()) {
            b -= 10;
        }
        if (this.Traits.Overweight.isSet()) {
            b -= 5;
        }
        final int max = Math.max(5, b);
        this.clearVariable("BumpFallType");
        this.setBumpType("stagger");
        if (Rand.Next(100) < max) {
            bumpFall = true;
        }
        this.setBumpDone(false);
        this.setBumpFall(bumpFall);
        if (isoZombie.isBehind(this)) {
            this.setBumpFallType("pushedBehind");
        }
        else {
            this.setBumpFallType("pushedFront");
        }
        this.actionContext.reportEvent("wasBumped");
    }
    
    public boolean DoSwingCollisionBoneCheck(final IsoGameCharacter isoGameCharacter, final int n, final float n2) {
        Model.BoneToWorldCoords(isoGameCharacter, n, IsoGameCharacter.tempVectorBonePos);
        return IsoUtils.DistanceToSquared(IsoGameCharacter.tempVectorBonePos.x, IsoGameCharacter.tempVectorBonePos.y, this.x, this.y) < n2 * n2;
    }
    
    public boolean isInvincible() {
        return this.invincible;
    }
    
    public void setInvincible(final boolean invincible) {
        this.invincible = invincible;
    }
    
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
                            if (!(this instanceof IsoPlayer) || !((IsoPlayer)this).isLocalPlayer() || baseVehicle.getTargetAlpha(((IsoPlayer)this).PlayerIndex) != 0.0f) {
                                if (this.DistToSquared((float)(int)baseVehicle.x, (float)(int)baseVehicle.y) < 16.0f) {
                                    return baseVehicle;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    private IsoGridSquare getSolidFloorAt(final int n, final int n2, int i) {
        while (i >= 0) {
            final IsoGridSquare gridSquare = this.getCell().getGridSquare(n, n2, i);
            if (gridSquare != null) {
                if (gridSquare.TreatAsSolidFloor()) {
                    return gridSquare;
                }
            }
            --i;
        }
        return null;
    }
    
    public void dropHeavyItems() {
        final IsoGridSquare currentSquare = this.getCurrentSquare();
        if (currentSquare == null) {
            return;
        }
        final InventoryItem primaryHandItem = this.getPrimaryHandItem();
        final InventoryItem secondaryHandItem = this.getSecondaryHandItem();
        if (primaryHandItem == null && secondaryHandItem == null) {
            return;
        }
        final IsoGridSquare solidFloor = this.getSolidFloorAt(currentSquare.x, currentSquare.y, currentSquare.z);
        if (solidFloor == null) {
            return;
        }
        final boolean b = primaryHandItem == secondaryHandItem;
        if (this.isHeavyItem(primaryHandItem)) {
            final float next = Rand.Next(0.1f, 0.9f);
            final float next2 = Rand.Next(0.1f, 0.9f);
            final float n = solidFloor.getApparentZ(next, next2) - solidFloor.getZ();
            this.setPrimaryHandItem(null);
            this.getInventory().DoRemoveItem(primaryHandItem);
            solidFloor.AddWorldInventoryItem(primaryHandItem, next, next2, n);
            LuaEventManager.triggerEvent("OnContainerUpdate");
            LuaEventManager.triggerEvent("onItemFall", primaryHandItem);
        }
        if (this.isHeavyItem(secondaryHandItem)) {
            this.setSecondaryHandItem(null);
            if (!b) {
                final float next3 = Rand.Next(0.1f, 0.9f);
                final float next4 = Rand.Next(0.1f, 0.9f);
                final float n2 = solidFloor.getApparentZ(next3, next4) - solidFloor.getZ();
                this.getInventory().DoRemoveItem(secondaryHandItem);
                solidFloor.AddWorldInventoryItem(secondaryHandItem, next3, next4, n2);
                LuaEventManager.triggerEvent("OnContainerUpdate");
                LuaEventManager.triggerEvent("onItemFall", secondaryHandItem);
            }
        }
    }
    
    public boolean isHeavyItem(final InventoryItem inventoryItem) {
        return inventoryItem != null && (inventoryItem.getItemReplacementSecondHand() != null || (inventoryItem.getType().equals("CorpseMale") || inventoryItem.getType().equals("CorpseFemale")) || inventoryItem.getType().equals("Generator"));
    }
    
    public boolean isCanShout() {
        return this.canShout;
    }
    
    public void setCanShout(final boolean canShout) {
        this.canShout = canShout;
    }
    
    public boolean isUnlimitedEndurance() {
        return this.unlimitedEndurance;
    }
    
    public void setUnlimitedEndurance(final boolean unlimitedEndurance) {
        this.unlimitedEndurance = unlimitedEndurance;
    }
    
    private void addActiveLightItem(final InventoryItem inventoryItem, final ArrayList<InventoryItem> list) {
        if (inventoryItem != null && inventoryItem.isEmittingLight() && !list.contains(inventoryItem)) {
            list.add(inventoryItem);
        }
    }
    
    public ArrayList<InventoryItem> getActiveLightItems(final ArrayList<InventoryItem> list) {
        this.addActiveLightItem(this.getSecondaryHandItem(), list);
        this.addActiveLightItem(this.getPrimaryHandItem(), list);
        final AttachedItems attachedItems = this.getAttachedItems();
        for (int i = 0; i < attachedItems.size(); ++i) {
            this.addActiveLightItem(attachedItems.getItemByIndex(i), list);
        }
        return list;
    }
    
    public SleepingEventData getOrCreateSleepingEventData() {
        if (this.m_sleepingEventData == null) {
            this.m_sleepingEventData = new SleepingEventData();
        }
        return this.m_sleepingEventData;
    }
    
    public void playEmote(final String s) {
        this.setVariable("emote", s);
        this.actionContext.reportEvent("EventEmote");
    }
    
    public String getAnimationStateName() {
        return this.advancedAnimator.getCurrentStateName();
    }
    
    public String getActionStateName() {
        return this.actionContext.getCurrentStateName();
    }
    
    public boolean shouldWaitToStartTimedAction() {
        if (!this.isSitOnGround()) {
            return false;
        }
        final AdvancedAnimator advancedAnimator = this.getAdvancedAnimator();
        if (advancedAnimator.getRootLayer() == null) {
            return false;
        }
        if (advancedAnimator.animSet == null || !advancedAnimator.animSet.containsState("sitonground")) {
            return false;
        }
        if (!PZArrayUtil.contains(advancedAnimator.animSet.GetState("sitonground").m_Nodes, animNode -> "sit_action".equalsIgnoreCase(animNode.m_Name))) {
            return false;
        }
        final LiveAnimNode liveAnimNode2 = PZArrayUtil.find(advancedAnimator.getRootLayer().getLiveAnimNodes(), liveAnimNode -> liveAnimNode.isActive() && "sit_action".equalsIgnoreCase(liveAnimNode.getName()));
        return liveAnimNode2 == null || !liveAnimNode2.isMainAnimActive();
    }
    
    public void setPersistentOutfitID(final int n) {
        this.setPersistentOutfitID(n, false);
    }
    
    public void setPersistentOutfitID(final int persistentOutfitId, final boolean bPersistentOutfitInit) {
        this.m_persistentOutfitId = persistentOutfitId;
        this.m_bPersistentOutfitInit = bPersistentOutfitInit;
    }
    
    public int getPersistentOutfitID() {
        return this.m_persistentOutfitId;
    }
    
    public boolean isPersistentOutfitInit() {
        return this.m_bPersistentOutfitInit;
    }
    
    public boolean isDoingActionThatCanBeCancelled() {
        return false;
    }
    
    public boolean isDoDeathSound() {
        return this.doDeathSound;
    }
    
    public void setDoDeathSound(final boolean doDeathSound) {
        this.doDeathSound = doDeathSound;
    }
    
    public void updateEquippedRadioFreq() {
        this.invRadioFreq.clear();
        for (int i = 0; i < this.getInventory().getItems().size(); ++i) {
            final InventoryItem inventoryItem = this.getInventory().getItems().get(i);
            if (inventoryItem instanceof Radio) {
                final Radio radio = (Radio)inventoryItem;
                if (radio.getDeviceData() != null && radio.getDeviceData().getIsTurnedOn() && !radio.getDeviceData().getMicIsMuted() && !this.invRadioFreq.contains(radio.getDeviceData().getChannel())) {
                    this.invRadioFreq.add(radio.getDeviceData().getChannel());
                }
            }
        }
        for (int j = 0; j < this.invRadioFreq.size(); ++j) {
            System.out.println(this.invRadioFreq.get(j));
        }
        if (this instanceof IsoPlayer && GameClient.bClient) {
            GameClient.sendEquippedRadioFreq((IsoPlayer)this);
        }
    }
    
    public FMODParameterList getFMODParameters() {
        return this.fmodParameters;
    }
    
    public void startEvent(final long n, final GameSoundClip gameSoundClip, final BitSet set) {
        final FMODParameterList fmodParameters = this.getFMODParameters();
        final ArrayList parameters = gameSoundClip.eventDescription.parameters;
        for (int i = 0; i < parameters.size(); ++i) {
            final FMOD_STUDIO_PARAMETER_DESCRIPTION fmod_STUDIO_PARAMETER_DESCRIPTION = parameters.get(i);
            if (!set.get(fmod_STUDIO_PARAMETER_DESCRIPTION.globalIndex)) {
                final FMODParameter value = fmodParameters.get(fmod_STUDIO_PARAMETER_DESCRIPTION);
                if (value != null) {
                    value.startEventInstance(n);
                }
            }
        }
    }
    
    public void updateEvent(final long n, final GameSoundClip gameSoundClip) {
    }
    
    public void stopEvent(final long n, final GameSoundClip gameSoundClip, final BitSet set) {
        final FMODParameterList fmodParameters = this.getFMODParameters();
        final ArrayList parameters = gameSoundClip.eventDescription.parameters;
        for (int i = 0; i < parameters.size(); ++i) {
            final FMOD_STUDIO_PARAMETER_DESCRIPTION fmod_STUDIO_PARAMETER_DESCRIPTION = parameters.get(i);
            if (!set.get(fmod_STUDIO_PARAMETER_DESCRIPTION.globalIndex)) {
                final FMODParameter value = fmodParameters.get(fmod_STUDIO_PARAMETER_DESCRIPTION);
                if (value != null) {
                    value.stopEventInstance(n);
                }
            }
        }
    }
    
    public void playBloodSplatterSound() {
        if (this.getEmitter().isPlaying("BloodSplatter")) {}
        this.getEmitter().playSoundImpl("BloodSplatter", this);
    }
    
    public void setIgnoreAimingInput(final boolean ignoreAimingInput) {
        this.ignoreAimingInput = ignoreAimingInput;
    }
    
    public void addBlood(final float n) {
        if (Rand.Next(10) > n) {
            return;
        }
        if (SandboxOptions.instance.BloodLevel.getValue() > 1) {
            int next = Rand.Next(4, 10);
            if (next < 1) {
                next = 1;
            }
            if (Core.bLastStand) {
                next *= 3;
            }
            switch (SandboxOptions.instance.BloodLevel.getValue()) {
                case 2: {
                    next /= 2;
                    break;
                }
                case 4: {
                    next *= 2;
                    break;
                }
                case 5: {
                    next *= 5;
                    break;
                }
            }
            for (int i = 0; i < next; ++i) {
                this.splatBlood(2, 0.3f);
            }
        }
        if (SandboxOptions.instance.BloodLevel.getValue() > 1) {
            this.splatBloodFloorBig();
            this.playBloodSplatterSound();
        }
    }
    
    public boolean isKnockedDown() {
        return this.bKnockedDown;
    }
    
    public void setKnockedDown(final boolean bKnockedDown) {
        this.bKnockedDown = bKnockedDown;
    }
    
    public void writeInventory(final ByteBuffer byteBuffer) {
        GameWindow.WriteString(byteBuffer, this.isFemale() ? "inventoryfemale" : "inventorymale");
        if (this.getInventory() != null) {
            byteBuffer.put((byte)1);
            try {
                final ArrayList<InventoryItem> save = this.getInventory().save(byteBuffer);
                final WornItems wornItems = this.getWornItems();
                if (wornItems == null) {
                    byteBuffer.put((byte)0);
                }
                else {
                    int size = wornItems.size();
                    if (size > 127) {
                        DebugLog.Multiplayer.warn((Object)"Too many worn items");
                        size = 127;
                    }
                    byteBuffer.put((byte)size);
                    for (int i = 0; i < size; ++i) {
                        final WornItem value = wornItems.get(i);
                        GameWindow.WriteString(byteBuffer, value.getLocation());
                        byteBuffer.putShort((short)save.indexOf(value.getItem()));
                    }
                }
                final AttachedItems attachedItems = this.getAttachedItems();
                if (attachedItems == null) {
                    byteBuffer.put((byte)0);
                }
                else {
                    int size2 = attachedItems.size();
                    if (size2 > 127) {
                        DebugLog.Multiplayer.warn((Object)"Too many attached items");
                        size2 = 127;
                    }
                    byteBuffer.put((byte)size2);
                    for (int j = 0; j < size2; ++j) {
                        final AttachedItem value2 = attachedItems.get(j);
                        GameWindow.WriteString(byteBuffer, value2.getLocation());
                        byteBuffer.putShort((short)save.indexOf(value2.getItem()));
                    }
                }
            }
            catch (IOException ex) {
                DebugLog.Multiplayer.printException((Throwable)ex, invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, this.getOnlineID()), LogSeverity.Error);
            }
        }
        else {
            byteBuffer.put((byte)0);
        }
    }
    
    public String readInventory(final ByteBuffer byteBuffer) {
        final String readString = GameWindow.ReadString(byteBuffer);
        if (byteBuffer.get() == 1) {
            try {
                final ArrayList<InventoryItem> load = this.getInventory().load(byteBuffer, IsoWorld.getWorldVersion());
                for (byte value = byteBuffer.get(), b = 0; b < value; ++b) {
                    final String readStringUTF = GameWindow.ReadStringUTF(byteBuffer);
                    final short short1 = byteBuffer.getShort();
                    if (short1 >= 0 && short1 < load.size() && this.getBodyLocationGroup().getLocation(readStringUTF) != null) {
                        this.getWornItems().setItem(readStringUTF, load.get(short1));
                    }
                }
                for (byte value2 = byteBuffer.get(), b2 = 0; b2 < value2; ++b2) {
                    final String readStringUTF2 = GameWindow.ReadStringUTF(byteBuffer);
                    final short short2 = byteBuffer.getShort();
                    if (short2 >= 0 && short2 < load.size() && this.getAttachedLocationGroup().getLocation(readStringUTF2) != null) {
                        this.getAttachedItems().setItem(readStringUTF2, load.get(short2));
                    }
                }
            }
            catch (IOException ex) {
                DebugLog.Multiplayer.printException((Throwable)ex, invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, this.getOnlineID()), LogSeverity.Error);
            }
        }
        return readString;
    }
    
    public void Kill(final IsoGameCharacter attackedBy) {
        if (Core.bDebug) {
            DebugLog.log(DebugType.Death, String.format("%s.Kill id=%d", this.getClass().getSimpleName(), this.getOnlineID()));
        }
        this.setAttackedBy(attackedBy);
        this.setHealth(0.0f);
        this.setOnKillDone(true);
    }
    
    public boolean shouldDoInventory() {
        return true;
    }
    
    public void becomeCorpse() {
        if (Core.bDebug) {
            DebugLog.log(DebugType.Death, String.format("%s.BecomeCorpse id=%d", this.getClass().getSimpleName(), this.getOnlineID()));
        }
        this.Kill(this.getAttackedBy());
        this.setOnDeathDone(true);
    }
    
    public boolean shouldBecomeCorpse() {
        if (GameClient.bClient || GameServer.bServer) {
            if (this.getHitReactionNetworkAI().isSetup() || this.getHitReactionNetworkAI().isStarted()) {
                return false;
            }
            if (GameServer.bServer) {
                return this.getNetworkCharacterAI().isSetDeadBody();
            }
            if (GameClient.bClient) {
                return this.isCurrentState(ZombieOnGroundState.instance()) || this.isCurrentState(PlayerOnGroundState.instance());
            }
        }
        return true;
    }
    
    public HitReactionNetworkAI getHitReactionNetworkAI() {
        return null;
    }
    
    public NetworkCharacterAI getNetworkCharacterAI() {
        return null;
    }
    
    public boolean isLocal() {
        return !GameClient.bClient && !GameServer.bServer;
    }
    
    public boolean isVehicleCollisionActive(final BaseVehicle baseVehicle) {
        return GameClient.bClient && this.isAlive() && baseVehicle != null && baseVehicle.shouldCollideWithCharacters() && baseVehicle.isEngineRunning() && baseVehicle.getDriver() != null && Math.abs(baseVehicle.x - this.x) >= 0.01f && Math.abs(baseVehicle.y - this.y) >= 0.01f && (!this.isKnockedDown() || this.isOnFloor()) && (this.getHitReactionNetworkAI() == null || !this.getHitReactionNetworkAI().isStarted());
    }
    
    public void doHitByVehicle(final BaseVehicle baseVehicle, final BaseVehicle.HitVars hitVars) {
        if (GameClient.bClient) {
            if (baseVehicle.getDriver() instanceof IsoPlayer) {
                if (baseVehicle.getDriver().isLocal()) {
                    SoundManager.instance.PlayWorldSound("VehicleHitCharacter", this.getCurrentSquare(), 0.0f, 20.0f, 0.9f, true);
                    GameClient.sendHitVehicle((IsoPlayer)baseVehicle.getDriver(), this, baseVehicle, this.Hit(baseVehicle, hitVars.hitSpeed, hitVars.isTargetHitFromBehind, -hitVars.targetImpulse.x, -hitVars.targetImpulse.z), hitVars.isTargetHitFromBehind, hitVars.vehicleDamage, hitVars.hitSpeed, hitVars.isVehicleHitFromFront);
                }
                else {
                    this.getNetworkCharacterAI().resetVehicleHitTimeout();
                }
            }
        }
        else if (!GameServer.bServer) {
            final BaseSoundEmitter freeEmitter = IsoWorld.instance.getFreeEmitter(this.x, this.y, this.z);
            freeEmitter.setParameterValue(freeEmitter.playSound("VehicleHitCharacter"), FMODManager.instance.getParameterDescription("VehicleSpeed"), baseVehicle.getCurrentSpeedKmHour());
            this.Hit(baseVehicle, hitVars.hitSpeed, hitVars.isTargetHitFromBehind, -hitVars.targetImpulse.x, -hitVars.targetImpulse.z);
        }
    }
    
    public boolean isSkipResolveCollision() {
        return (this instanceof IsoZombie && (this.isCurrentState(ZombieHitReactionState.instance()) || this.isCurrentState(ZombieFallDownState.instance()) || this.isCurrentState(ZombieOnGroundState.instance()) || this.isCurrentState(StaggerBackState.instance()))) || (this instanceof IsoPlayer && !this.isLocal() && (this.isCurrentState(PlayerFallDownState.instance()) || this.isCurrentState(BumpedState.instance()) || this.isCurrentState(PlayerKnockedDown.instance()) || this.isCurrentState(PlayerHitReactionState.instance()) || this.isCurrentState(PlayerHitReactionPVPState.instance()) || this.isCurrentState(PlayerOnGroundState.instance())));
    }
    
    public boolean isAttackAnim() {
        return this.attackAnim;
    }
    
    public void setAttackAnim(final boolean attackAnim) {
        this.attackAnim = attackAnim;
    }
    
    public Float calcHitDir(final IsoGameCharacter isoGameCharacter, final HandWeapon handWeapon, final Vector2 vector2) {
        vector2.x = this.getX();
        vector2.y = this.getY();
        vector2.x -= isoGameCharacter.getX();
        vector2.y -= isoGameCharacter.getY();
        vector2.normalize();
        vector2.x *= handWeapon.getPushBackMod();
        vector2.y *= handWeapon.getPushBackMod();
        vector2.rotate(handWeapon.HitAngleMod);
        vector2.setLength(this.getHitForce() * 0.1f);
        return null;
    }
    
    public void calcHitDir(final Vector2 vector2) {
        vector2.set(this.getHitDir());
        vector2.setLength(this.getHitForce());
    }
    
    static {
        IsoGameCharacter.IID = 0;
        SurvivorMap = new HashMap<Integer, SurvivorDesc>();
        LevelUpLevels = new int[] { 25, 75, 150, 225, 300, 400, 500, 600, 700, 800, 900, 1000, 1200, 1400, 1600, 1800, 2000, 2200, 2400, 2600, 2800, 3000, 3200, 3400, 3600, 3800, 4000, 4400, 4800, 5200, 5600, 6000 };
        tempo = new Vector2();
        inf = new ColorInfo();
        tempo2 = new Vector2();
        tempVector2_1 = new Vector2();
        tempVector2_2 = new Vector2();
        IsoGameCharacter.sleepText = null;
        tempVector2 = new Vector2();
        tempItemVisuals = new ItemVisuals();
        movingStatic = new ArrayList<IsoMovingObject>();
        s_bandages = new Bandages();
        tempVector = new Vector3();
        tempVectorBonePos = new Vector3();
    }
    
    private static final class L_getDotWithForwardDirection
    {
        static final Vector2 v1;
        static final Vector2 v2;
        
        static {
            v1 = new Vector2();
            v2 = new Vector2();
        }
    }
    
    private static final class L_renderShadow
    {
        static final Vector3f forward;
        static final Vector3 v1;
        static final Vector3f v3;
        
        static {
            forward = new Vector3f();
            v1 = new Vector3();
            v3 = new Vector3f();
        }
    }
    
    private static final class L_renderLast
    {
        static final Color color;
        
        static {
            color = new Color();
        }
    }
    
    private static final class L_actionStateChanged
    {
        static final ArrayList<String> stateNames;
        static final ArrayList<State> states;
        
        static {
            stateNames = new ArrayList<String>();
            states = new ArrayList<State>();
        }
    }
    
    protected static final class l_testDotSide
    {
        static final Vector2 v1;
        static final Vector2 v2;
        static final Vector2 v3;
        
        static {
            v1 = new Vector2();
            v2 = new Vector2();
            v3 = new Vector2();
        }
    }
    
    public enum BodyLocation
    {
        Head, 
        Leg, 
        Arm, 
        Chest, 
        Stomach, 
        Foot, 
        Hand;
        
        private static /* synthetic */ BodyLocation[] $values() {
            return new BodyLocation[] { BodyLocation.Head, BodyLocation.Leg, BodyLocation.Arm, BodyLocation.Chest, BodyLocation.Stomach, BodyLocation.Foot, BodyLocation.Hand };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    public static class XPMultiplier
    {
        public float multiplier;
        public int minLevel;
        public int maxLevel;
    }
    
    public static class Location
    {
        public int x;
        public int y;
        public int z;
        
        public Location(final int x, final int y, final int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o instanceof Location) {
                final Location location = (Location)o;
                return this.x == location.x && this.y == location.y && this.z == location.z;
            }
            return false;
        }
    }
    
    private static class ReadBook
    {
        String fullType;
        int alreadyReadPages;
    }
    
    public static class LightInfo
    {
        public IsoGridSquare square;
        public float x;
        public float y;
        public float z;
        public float angleX;
        public float angleY;
        public ArrayList<TorchInfo> torches;
        public long time;
        public float night;
        public float rmod;
        public float gmod;
        public float bmod;
        
        public LightInfo() {
            this.torches = new ArrayList<TorchInfo>();
        }
        
        public void initFrom(final LightInfo lightInfo) {
            this.square = lightInfo.square;
            this.x = lightInfo.x;
            this.y = lightInfo.y;
            this.z = lightInfo.z;
            this.angleX = lightInfo.angleX;
            this.angleY = lightInfo.angleY;
            this.torches.clear();
            this.torches.addAll(lightInfo.torches);
            this.time = (long)(System.nanoTime() / 1000000.0);
            this.night = lightInfo.night;
            this.rmod = lightInfo.rmod;
            this.gmod = lightInfo.gmod;
            this.bmod = lightInfo.bmod;
        }
    }
    
    public static class TorchInfo
    {
        private static final ObjectPool<TorchInfo> TorchInfoPool;
        private static final Vector3f tempVector3f;
        public int id;
        public float x;
        public float y;
        public float z;
        public float angleX;
        public float angleY;
        public float dist;
        public float strength;
        public boolean bCone;
        public float dot;
        public int focusing;
        
        public static TorchInfo alloc() {
            return TorchInfo.TorchInfoPool.alloc();
        }
        
        public static void release(final TorchInfo torchInfo) {
            TorchInfo.TorchInfoPool.release(torchInfo);
        }
        
        public TorchInfo set(final IsoPlayer isoPlayer, final InventoryItem inventoryItem) {
            this.x = isoPlayer.getX();
            this.y = isoPlayer.getY();
            this.z = isoPlayer.getZ();
            final Vector2 lookVector = isoPlayer.getLookVector(IsoGameCharacter.tempVector2);
            this.angleX = lookVector.x;
            this.angleY = lookVector.y;
            this.dist = (float)inventoryItem.getLightDistance();
            this.strength = inventoryItem.getLightStrength();
            this.bCone = inventoryItem.isTorchCone();
            this.dot = inventoryItem.getTorchDot();
            this.focusing = 0;
            return this;
        }
        
        public TorchInfo set(final VehiclePart vehiclePart) {
            final BaseVehicle vehicle = vehiclePart.getVehicle();
            final VehicleLight light = vehiclePart.getLight();
            final VehicleScript script = vehicle.getScript();
            final Vector3f tempVector3f = TorchInfo.tempVector3f;
            tempVector3f.set(light.offset.x * script.getExtents().x / 2.0f, 0.0f, light.offset.y * script.getExtents().z / 2.0f);
            vehicle.getWorldPos(tempVector3f, tempVector3f);
            this.x = tempVector3f.x;
            this.y = tempVector3f.y;
            this.z = tempVector3f.z;
            final Vector3f forwardVector = vehicle.getForwardVector(tempVector3f);
            this.angleX = forwardVector.x;
            this.angleY = forwardVector.z;
            this.dist = vehiclePart.getLightDistance();
            this.strength = vehiclePart.getLightIntensity();
            this.bCone = true;
            this.dot = light.dot;
            this.focusing = (int)vehiclePart.getLightFocusing();
            return this;
        }
        
        static {
            TorchInfoPool = new ObjectPool<TorchInfo>(TorchInfo::new);
            tempVector3f = new Vector3f();
        }
    }
    
    public class PerkInfo
    {
        public int level;
        public PerkFactory.Perk perk;
        
        public PerkInfo() {
            this.level = 0;
        }
        
        public int getLevel() {
            return this.level;
        }
    }
    
    public class XP
    {
        public int level;
        public int lastlevel;
        public float TotalXP;
        public HashMap<PerkFactory.Perk, Float> XPMap;
        public HashMap<PerkFactory.Perk, XPMultiplier> XPMapMultiplier;
        IsoGameCharacter chr;
        
        public XP(final IsoGameCharacter chr) {
            this.level = 0;
            this.lastlevel = 0;
            this.TotalXP = 0.0f;
            this.XPMap = new HashMap<PerkFactory.Perk, Float>();
            this.XPMapMultiplier = new HashMap<PerkFactory.Perk, XPMultiplier>();
            this.chr = null;
            this.chr = chr;
        }
        
        public void addXpMultiplier(final PerkFactory.Perk perk, final float multiplier, final int minLevel, final int maxLevel) {
            XPMultiplier value = this.XPMapMultiplier.get(perk);
            if (value == null) {
                value = new XPMultiplier();
            }
            value.multiplier = multiplier;
            value.minLevel = minLevel;
            value.maxLevel = maxLevel;
            this.XPMapMultiplier.put(perk, value);
        }
        
        public HashMap<PerkFactory.Perk, XPMultiplier> getMultiplierMap() {
            return this.XPMapMultiplier;
        }
        
        public float getMultiplier(final PerkFactory.Perk key) {
            final XPMultiplier xpMultiplier = this.XPMapMultiplier.get(key);
            if (xpMultiplier == null) {
                return 0.0f;
            }
            return xpMultiplier.multiplier;
        }
        
        public int getPerkBoost(final PerkFactory.Perk perk) {
            if (IsoGameCharacter.this.getDescriptor().getXPBoostMap().get(perk) != null) {
                return IsoGameCharacter.this.getDescriptor().getXPBoostMap().get(perk);
            }
            return 0;
        }
        
        public int getLevel() {
            return this.level;
        }
        
        public void setLevel(final int level) {
            this.level = level;
        }
        
        public float getTotalXp() {
            return this.TotalXP;
        }
        
        public void AddXP(final PerkFactory.Perk perk, final float n) {
            this.AddXP(perk, n, true);
        }
        
        public void AddXPNoMultiplier(final PerkFactory.Perk perk, final float n) {
            final XPMultiplier value = this.getMultiplierMap().remove(perk);
            try {
                this.AddXP(perk, n);
            }
            finally {
                if (value != null) {
                    this.getMultiplierMap().put(perk, value);
                }
            }
        }
        
        public void AddXP(final PerkFactory.Perk perk, final float n, final boolean b) {
            this.AddXP(perk, n, b, true);
        }
        
        public void AddXP(final PerkFactory.Perk perk, final float n, final boolean b, final boolean b2) {
            this.AddXP(perk, n, b, b2, true, false);
        }
        
        public void AddXP(final PerkFactory.Perk key, float f, final boolean b, final boolean b2, final boolean b3, final boolean b4) {
            if (!b4 && GameClient.bClient && this.chr instanceof IsoPlayer) {
                GameClient.instance.sendAddXpFromPlayerStatsUI((IsoPlayer)this.chr, key, (int)f, b2, false);
            }
            PerkFactory.Perk perk = null;
            for (int i = 0; i < PerkFactory.PerkList.size(); ++i) {
                final PerkFactory.Perk perk2 = PerkFactory.PerkList.get(i);
                if (perk2.getType() == key) {
                    perk = perk2;
                    break;
                }
            }
            if (perk.getType() == PerkFactory.Perks.Fitness && this.chr instanceof IsoPlayer && !((IsoPlayer)this.chr).getNutrition().canAddFitnessXp()) {
                return;
            }
            if (perk.getType() == PerkFactory.Perks.Strength && this.chr instanceof IsoPlayer) {
                if (((IsoPlayer)this.chr).getNutrition().getProteins() > 50.0f && ((IsoPlayer)this.chr).getNutrition().getProteins() < 300.0f) {
                    f *= 1.5;
                }
                if (((IsoPlayer)this.chr).getNutrition().getProteins() < -300.0f) {
                    f *= (float)0.7;
                }
            }
            final float xp = this.getXP(key);
            final float totalXpForLevel = perk.getTotalXpForLevel(10);
            if (f >= 0.0f && xp >= totalXpForLevel) {
                return;
            }
            float n = 1.0f;
            if (b3) {
                boolean b5 = false;
                for (final Map.Entry<PerkFactory.Perk, Integer> entry : IsoGameCharacter.this.getDescriptor().getXPBoostMap().entrySet()) {
                    if (entry.getKey() == perk.getType()) {
                        b5 = true;
                        if (entry.getValue() == 0 && !this.isSkillExcludedFromSpeedReduction(entry.getKey())) {
                            n *= 0.25f;
                        }
                        else if (entry.getValue() == 1 && entry.getKey() == PerkFactory.Perks.Sprinting) {
                            n *= 1.25;
                        }
                        else if (entry.getValue() == 1) {
                            n *= 1.0;
                        }
                        else if (entry.getValue() == 2 && !this.isSkillExcludedFromSpeedIncrease(entry.getKey())) {
                            n *= (float)1.33;
                        }
                        else {
                            if (entry.getValue() < 3 || this.isSkillExcludedFromSpeedIncrease(entry.getKey())) {
                                continue;
                            }
                            n *= (float)1.66;
                        }
                    }
                }
                if (!b5 && !this.isSkillExcludedFromSpeedReduction(perk.getType())) {
                    n = 0.25f;
                }
                if (IsoGameCharacter.this.Traits.FastLearner.isSet() && !this.isSkillExcludedFromSpeedIncrease(perk.getType())) {
                    n *= 1.3f;
                }
                if (IsoGameCharacter.this.Traits.SlowLearner.isSet() && !this.isSkillExcludedFromSpeedReduction(perk.getType())) {
                    n *= 0.7f;
                }
                if (IsoGameCharacter.this.Traits.Pacifist.isSet()) {
                    if (perk.getType() == PerkFactory.Perks.SmallBlade || perk.getType() == PerkFactory.Perks.SmallBlunt || perk.getType() == PerkFactory.Perks.Spear || perk.getType() == PerkFactory.Perks.Maintenance || perk.getType() == PerkFactory.Perks.Blunt || perk.getType() == PerkFactory.Perks.Axe) {
                        n *= 0.75f;
                    }
                    else if (perk.getType() == PerkFactory.Perks.Aiming) {
                        n *= 0.75f;
                    }
                }
                f *= n;
                final float multiplier = this.getMultiplier(key);
                if (multiplier > 1.0f) {
                    f *= multiplier;
                }
                if (!perk.isPassiv()) {
                    f *= (float)SandboxOptions.instance.XpMultiplier.getValue();
                }
            }
            float f2 = xp + f;
            if (f2 < 0.0f) {
                f2 = 0.0f;
                f = -xp;
            }
            if (f2 > totalXpForLevel) {
                f2 = totalXpForLevel;
                f = f2 - xp;
            }
            this.XPMap.put(key, f2);
            for (float n2 = perk.getTotalXpForLevel(this.chr.getPerkLevel(perk) + 1); xp < n2 && f2 >= n2; n2 = perk.getTotalXpForLevel(this.chr.getPerkLevel(perk) + 1)) {
                IsoGameCharacter.this.LevelPerk(key);
                if (this.chr instanceof IsoPlayer && ((IsoPlayer)this.chr).isLocalPlayer() && !this.chr.getEmitter().isPlaying("GainExperienceLevel")) {
                    this.chr.getEmitter().playSoundImpl("GainExperienceLevel", null);
                }
                if (this.chr.getPerkLevel(perk) >= 10) {
                    break;
                }
            }
            final XPMultiplier xpMultiplier = this.getMultiplierMap().get(perk);
            if (xpMultiplier != null) {
                final float totalXpForLevel2 = perk.getTotalXpForLevel(xpMultiplier.minLevel - 1);
                final float totalXpForLevel3 = perk.getTotalXpForLevel(xpMultiplier.maxLevel);
                if ((xp >= totalXpForLevel2 && f2 < totalXpForLevel2) || (xp < totalXpForLevel3 && f2 >= totalXpForLevel3)) {
                    this.getMultiplierMap().remove(perk);
                }
            }
            if (b) {
                LuaEventManager.triggerEventGarbage("AddXP", this.chr, key, f);
            }
        }
        
        private boolean isSkillExcludedFromSpeedReduction(final PerkFactory.Perk perk) {
            return perk == PerkFactory.Perks.Sprinting || perk == PerkFactory.Perks.Fitness || perk == PerkFactory.Perks.Strength;
        }
        
        private boolean isSkillExcludedFromSpeedIncrease(final PerkFactory.Perk perk) {
            return perk == PerkFactory.Perks.Fitness || perk == PerkFactory.Perks.Strength;
        }
        
        public float getXP(final PerkFactory.Perk perk) {
            if (this.XPMap.containsKey(perk)) {
                return this.XPMap.get(perk);
            }
            return 0.0f;
        }
        
        public void AddXP(final HandWeapon handWeapon, final int n) {
        }
        
        public void setTotalXP(final float totalXP) {
            this.TotalXP = totalXP;
        }
        
        private void savePerk(final ByteBuffer byteBuffer, final PerkFactory.Perk perk) throws IOException {
            GameWindow.WriteStringUTF(byteBuffer, (perk == null) ? "" : perk.getId());
        }
        
        private PerkFactory.Perk loadPerk(final ByteBuffer byteBuffer, final int n) throws IOException {
            if (n >= 152) {
                final PerkFactory.Perk fromString = PerkFactory.Perks.FromString(GameWindow.ReadStringUTF(byteBuffer));
                return (fromString == PerkFactory.Perks.MAX) ? null : fromString;
            }
            final int int1 = byteBuffer.getInt();
            if (int1 < 0 || int1 >= PerkFactory.Perks.MAX.index()) {
                return null;
            }
            final PerkFactory.Perk fromIndex = PerkFactory.Perks.fromIndex(int1);
            return (fromIndex == PerkFactory.Perks.MAX) ? null : fromIndex;
        }
        
        public void load(final ByteBuffer byteBuffer, final int n) throws IOException {
            final int int1 = byteBuffer.getInt();
            this.chr.Traits.clear();
            for (int i = 0; i < int1; ++i) {
                final String readString = GameWindow.ReadString(byteBuffer);
                if (TraitFactory.getTrait(readString) != null) {
                    if (!this.chr.Traits.contains(readString)) {
                        this.chr.Traits.add(readString);
                    }
                }
                else {
                    DebugLog.General.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, readString));
                }
            }
            this.TotalXP = byteBuffer.getFloat();
            this.level = byteBuffer.getInt();
            this.lastlevel = byteBuffer.getInt();
            this.XPMap.clear();
            for (int int2 = byteBuffer.getInt(), j = 0; j < int2; ++j) {
                final PerkFactory.Perk loadPerk = this.loadPerk(byteBuffer, n);
                final float float1 = byteBuffer.getFloat();
                if (loadPerk != null) {
                    this.XPMap.put(loadPerk, float1);
                }
            }
            if (n < 162) {
                for (int int3 = byteBuffer.getInt(), k = 0; k < int3; ++k) {
                    this.loadPerk(byteBuffer, n);
                }
            }
            IsoGameCharacter.this.PerkList.clear();
            for (int int4 = byteBuffer.getInt(), l = 0; l < int4; ++l) {
                final PerkFactory.Perk loadPerk2 = this.loadPerk(byteBuffer, n);
                final int int5 = byteBuffer.getInt();
                if (loadPerk2 != null) {
                    final PerkInfo e = new PerkInfo();
                    e.perk = loadPerk2;
                    e.level = int5;
                    IsoGameCharacter.this.PerkList.add(e);
                }
            }
            for (int int6 = byteBuffer.getInt(), n2 = 0; n2 < int6; ++n2) {
                final PerkFactory.Perk loadPerk3 = this.loadPerk(byteBuffer, n);
                final float float2 = byteBuffer.getFloat();
                final byte value = byteBuffer.get();
                final byte value2 = byteBuffer.get();
                if (loadPerk3 != null) {
                    this.addXpMultiplier(loadPerk3, float2, value, value2);
                }
            }
            if (this.TotalXP > IsoGameCharacter.this.getXpForLevel(this.getLevel() + 1)) {
                this.setTotalXP((float)this.chr.getXpForLevel(this.getLevel()));
            }
        }
        
        public void save(final ByteBuffer byteBuffer) throws IOException {
            byteBuffer.putInt(this.chr.Traits.size());
            for (int i = 0; i < this.chr.Traits.size(); ++i) {
                GameWindow.WriteString(byteBuffer, this.chr.Traits.get(i));
            }
            byteBuffer.putFloat(this.TotalXP);
            byteBuffer.putInt(this.level);
            byteBuffer.putInt(this.lastlevel);
            byteBuffer.putInt(this.XPMap.size());
            final Iterator<Map.Entry<PerkFactory.Perk, Float>> iterator = this.XPMap.entrySet().iterator();
            while (iterator != null && iterator.hasNext()) {
                final Map.Entry<PerkFactory.Perk, Float> entry = iterator.next();
                this.savePerk(byteBuffer, entry.getKey());
                byteBuffer.putFloat(entry.getValue());
            }
            byteBuffer.putInt(IsoGameCharacter.this.PerkList.size());
            for (int j = 0; j < IsoGameCharacter.this.PerkList.size(); ++j) {
                final PerkInfo perkInfo = IsoGameCharacter.this.PerkList.get(j);
                this.savePerk(byteBuffer, perkInfo.perk);
                byteBuffer.putInt(perkInfo.level);
            }
            byteBuffer.putInt(this.XPMapMultiplier.size());
            final Iterator<Map.Entry<PerkFactory.Perk, XPMultiplier>> iterator2 = this.XPMapMultiplier.entrySet().iterator();
            while (iterator2 != null && iterator2.hasNext()) {
                final Map.Entry<PerkFactory.Perk, XPMultiplier> entry2 = iterator2.next();
                this.savePerk(byteBuffer, entry2.getKey());
                byteBuffer.putFloat(entry2.getValue().multiplier);
                byteBuffer.put((byte)entry2.getValue().minLevel);
                byteBuffer.put((byte)entry2.getValue().maxLevel);
            }
        }
        
        public void setXPToLevel(final PerkFactory.Perk key, final int n) {
            PerkFactory.Perk perk = null;
            for (int i = 0; i < PerkFactory.PerkList.size(); ++i) {
                final PerkFactory.Perk perk2 = PerkFactory.PerkList.get(i);
                if (perk2.getType() == key) {
                    perk = perk2;
                    break;
                }
            }
            if (perk != null) {
                this.XPMap.put(key, perk.getTotalXpForLevel(n));
            }
        }
    }
    
    public class CharacterTraits extends TraitCollection
    {
        public final TraitSlot Obese;
        public final TraitSlot Athletic;
        public final TraitSlot Overweight;
        public final TraitSlot Unfit;
        public final TraitSlot Emaciated;
        public final TraitSlot Graceful;
        public final TraitSlot Clumsy;
        public final TraitSlot Strong;
        public final TraitSlot Weak;
        public final TraitSlot VeryUnderweight;
        public final TraitSlot Underweight;
        public final TraitSlot FastHealer;
        public final TraitSlot SlowHealer;
        public final TraitSlot ShortSighted;
        public final TraitSlot EagleEyed;
        public final TraitSlot Agoraphobic;
        public final TraitSlot Claustophobic;
        public final TraitSlot AdrenalineJunkie;
        public final TraitSlot OutOfShape;
        public final TraitSlot HighThirst;
        public final TraitSlot LowThirst;
        public final TraitSlot HeartyAppitite;
        public final TraitSlot LightEater;
        public final TraitSlot Cowardly;
        public final TraitSlot Brave;
        public final TraitSlot Brooding;
        public final TraitSlot Insomniac;
        public final TraitSlot NeedsLessSleep;
        public final TraitSlot NeedsMoreSleep;
        public final TraitSlot Asthmatic;
        public final TraitSlot PlaysFootball;
        public final TraitSlot Jogger;
        public final TraitSlot NightVision;
        public final TraitSlot FastLearner;
        public final TraitSlot SlowLearner;
        public final TraitSlot Pacifist;
        public final TraitSlot Feeble;
        public final TraitSlot Stout;
        public final TraitSlot ShortTemper;
        public final TraitSlot Patient;
        public final TraitSlot Injured;
        public final TraitSlot Inconspicuous;
        public final TraitSlot Conspicuous;
        public final TraitSlot Desensitized;
        public final TraitSlot NightOwl;
        public final TraitSlot Hemophobic;
        public final TraitSlot Burglar;
        public final TraitSlot KeenHearing;
        public final TraitSlot Deaf;
        public final TraitSlot HardOfHearing;
        public final TraitSlot ThinSkinned;
        public final TraitSlot ThickSkinned;
        public final TraitSlot Marksman;
        public final TraitSlot Outdoorsman;
        public final TraitSlot Lucky;
        public final TraitSlot Unlucky;
        public final TraitSlot Nutritionist;
        public final TraitSlot Nutritionist2;
        public final TraitSlot Organized;
        public final TraitSlot Disorganized;
        public final TraitSlot Axeman;
        public final TraitSlot IronGut;
        public final TraitSlot WeakStomach;
        public final TraitSlot HeavyDrinker;
        public final TraitSlot LightDrinker;
        public final TraitSlot Resilient;
        public final TraitSlot ProneToIllness;
        public final TraitSlot SpeedDemon;
        public final TraitSlot SundayDriver;
        public final TraitSlot Smoker;
        public final TraitSlot Hypercondriac;
        public final TraitSlot Illiterate;
        
        public CharacterTraits() {
            this.Obese = this.getTraitSlot("Obese");
            this.Athletic = this.getTraitSlot("Athletic");
            this.Overweight = this.getTraitSlot("Overweight");
            this.Unfit = this.getTraitSlot("Unfit");
            this.Emaciated = this.getTraitSlot("Emaciated");
            this.Graceful = this.getTraitSlot("Graceful");
            this.Clumsy = this.getTraitSlot("Clumsy");
            this.Strong = this.getTraitSlot("Strong");
            this.Weak = this.getTraitSlot("Weak");
            this.VeryUnderweight = this.getTraitSlot("Very Underweight");
            this.Underweight = this.getTraitSlot("Underweight");
            this.FastHealer = this.getTraitSlot("FastHealer");
            this.SlowHealer = this.getTraitSlot("SlowHealer");
            this.ShortSighted = this.getTraitSlot("ShortSighted");
            this.EagleEyed = this.getTraitSlot("EagleEyed");
            this.Agoraphobic = this.getTraitSlot("Agoraphobic");
            this.Claustophobic = this.getTraitSlot("Claustophobic");
            this.AdrenalineJunkie = this.getTraitSlot("AdrenalineJunkie");
            this.OutOfShape = this.getTraitSlot("Out of Shape");
            this.HighThirst = this.getTraitSlot("HighThirst");
            this.LowThirst = this.getTraitSlot("LowThirst");
            this.HeartyAppitite = this.getTraitSlot("HeartyAppitite");
            this.LightEater = this.getTraitSlot("LightEater");
            this.Cowardly = this.getTraitSlot("Cowardly");
            this.Brave = this.getTraitSlot("Brave");
            this.Brooding = this.getTraitSlot("Brooding");
            this.Insomniac = this.getTraitSlot("Insomniac");
            this.NeedsLessSleep = this.getTraitSlot("NeedsLessSleep");
            this.NeedsMoreSleep = this.getTraitSlot("NeedsMoreSleep");
            this.Asthmatic = this.getTraitSlot("Asthmatic");
            this.PlaysFootball = this.getTraitSlot("PlaysFootball");
            this.Jogger = this.getTraitSlot("Jogger");
            this.NightVision = this.getTraitSlot("NightVision");
            this.FastLearner = this.getTraitSlot("FastLearner");
            this.SlowLearner = this.getTraitSlot("SlowLearner");
            this.Pacifist = this.getTraitSlot("Pacifist");
            this.Feeble = this.getTraitSlot("Feeble");
            this.Stout = this.getTraitSlot("Stout");
            this.ShortTemper = this.getTraitSlot("ShortTemper");
            this.Patient = this.getTraitSlot("Patient");
            this.Injured = this.getTraitSlot("Injured");
            this.Inconspicuous = this.getTraitSlot("Inconspicuous");
            this.Conspicuous = this.getTraitSlot("Conspicuous");
            this.Desensitized = this.getTraitSlot("Desensitized");
            this.NightOwl = this.getTraitSlot("NightOwl");
            this.Hemophobic = this.getTraitSlot("Hemophobic");
            this.Burglar = this.getTraitSlot("Burglar");
            this.KeenHearing = this.getTraitSlot("KeenHearing");
            this.Deaf = this.getTraitSlot("Deaf");
            this.HardOfHearing = this.getTraitSlot("HardOfHearing");
            this.ThinSkinned = this.getTraitSlot("ThinSkinned");
            this.ThickSkinned = this.getTraitSlot("ThickSkinned");
            this.Marksman = this.getTraitSlot("Marksman");
            this.Outdoorsman = this.getTraitSlot("Outdoorsman");
            this.Lucky = this.getTraitSlot("Lucky");
            this.Unlucky = this.getTraitSlot("Unlucky");
            this.Nutritionist = this.getTraitSlot("Nutritionist");
            this.Nutritionist2 = this.getTraitSlot("Nutritionist2");
            this.Organized = this.getTraitSlot("Organized");
            this.Disorganized = this.getTraitSlot("Disorganized");
            this.Axeman = this.getTraitSlot("Axeman");
            this.IronGut = this.getTraitSlot("IronGut");
            this.WeakStomach = this.getTraitSlot("WeakStomach");
            this.HeavyDrinker = this.getTraitSlot("HeavyDrinker");
            this.LightDrinker = this.getTraitSlot("LightDrinker");
            this.Resilient = this.getTraitSlot("Resilient");
            this.ProneToIllness = this.getTraitSlot("ProneToIllness");
            this.SpeedDemon = this.getTraitSlot("SpeedDemon");
            this.SundayDriver = this.getTraitSlot("SundayDriver");
            this.Smoker = this.getTraitSlot("Smoker");
            this.Hypercondriac = this.getTraitSlot("Hypercondriac");
            this.Illiterate = this.getTraitSlot("Illiterate");
        }
        
        public boolean isIlliterate() {
            return this.Illiterate.isSet();
        }
    }
    
    private static class L_postUpdate
    {
        static final MoveDeltaModifiers moveDeltas;
        
        static {
            moveDeltas = new MoveDeltaModifiers();
        }
    }
    
    private static class s_performance
    {
        static final PerformanceProfileProbe postUpdate;
        public static PerformanceProfileProbe update;
        
        static {
            postUpdate = new PerformanceProfileProbe("IsoGameCharacter.postUpdate");
            s_performance.update = new PerformanceProfileProbe("IsoGameCharacter.update");
        }
    }
    
    private static final class Bandages
    {
        final HashMap<String, String> bandageTypeMap;
        final THashMap<String, InventoryItem> itemMap;
        
        private Bandages() {
            this.bandageTypeMap = new HashMap<String, String>();
            this.itemMap = (THashMap<String, InventoryItem>)new THashMap();
        }
        
        String getBloodBandageType(final String s) {
            String s2 = this.bandageTypeMap.get(s);
            if (s2 == null) {
                this.bandageTypeMap.put(s, s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
            }
            return s2;
        }
        
        void update(final IsoGameCharacter isoGameCharacter) {
            if (GameServer.bServer) {
                return;
            }
            final BodyDamage bodyDamage = isoGameCharacter.getBodyDamage();
            final WornItems wornItems = isoGameCharacter.getWornItems();
            if (bodyDamage == null || wornItems == null) {
                return;
            }
            assert !(isoGameCharacter instanceof IsoZombie);
            this.itemMap.clear();
            for (int i = 0; i < wornItems.size(); ++i) {
                final InventoryItem itemByIndex = wornItems.getItemByIndex(i);
                if (itemByIndex != null) {
                    this.itemMap.put((Object)itemByIndex.getFullType(), (Object)itemByIndex);
                }
            }
            for (int j = 0; j < BodyPartType.ToIndex(BodyPartType.MAX); ++j) {
                final BodyPart bodyPart = bodyDamage.getBodyPart(BodyPartType.FromIndex(j));
                final BodyPartLast bodyPartsLastState = bodyDamage.getBodyPartsLastState(BodyPartType.FromIndex(j));
                final String bandageModel = bodyPart.getType().getBandageModel();
                if (!StringUtils.isNullOrWhitespace(bandageModel)) {
                    final String bloodBandageType = this.getBloodBandageType(bandageModel);
                    if (bodyPart.bandaged() != bodyPartsLastState.bandaged()) {
                        if (bodyPart.bandaged()) {
                            if (bodyPart.isBandageDirty()) {
                                this.removeBandageModel(isoGameCharacter, bandageModel);
                                this.addBandageModel(isoGameCharacter, bloodBandageType);
                            }
                            else {
                                this.removeBandageModel(isoGameCharacter, bloodBandageType);
                                this.addBandageModel(isoGameCharacter, bandageModel);
                            }
                        }
                        else {
                            this.removeBandageModel(isoGameCharacter, bandageModel);
                            this.removeBandageModel(isoGameCharacter, bloodBandageType);
                        }
                    }
                    if (bodyPart.bitten() != bodyPartsLastState.bitten()) {
                        if (bodyPart.bitten()) {
                            final String biteWoundModel = bodyPart.getType().getBiteWoundModel(isoGameCharacter.isFemale());
                            if (StringUtils.isNullOrWhitespace(biteWoundModel)) {
                                continue;
                            }
                            this.addBandageModel(isoGameCharacter, biteWoundModel);
                        }
                        else {
                            this.removeBandageModel(isoGameCharacter, bodyPart.getType().getBiteWoundModel(isoGameCharacter.isFemale()));
                        }
                    }
                    if (bodyPart.scratched() != bodyPartsLastState.scratched()) {
                        if (bodyPart.scratched()) {
                            final String scratchWoundModel = bodyPart.getType().getScratchWoundModel(isoGameCharacter.isFemale());
                            if (StringUtils.isNullOrWhitespace(scratchWoundModel)) {
                                continue;
                            }
                            this.addBandageModel(isoGameCharacter, scratchWoundModel);
                        }
                        else {
                            this.removeBandageModel(isoGameCharacter, bodyPart.getType().getScratchWoundModel(isoGameCharacter.isFemale()));
                        }
                    }
                    if (bodyPart.isCut() != bodyPartsLastState.isCut()) {
                        if (bodyPart.isCut()) {
                            final String cutWoundModel = bodyPart.getType().getCutWoundModel(isoGameCharacter.isFemale());
                            if (!StringUtils.isNullOrWhitespace(cutWoundModel)) {
                                this.addBandageModel(isoGameCharacter, cutWoundModel);
                            }
                        }
                        else {
                            this.removeBandageModel(isoGameCharacter, bodyPart.getType().getCutWoundModel(isoGameCharacter.isFemale()));
                        }
                    }
                }
            }
        }
        
        protected void addBandageModel(final IsoGameCharacter isoGameCharacter, final String s) {
            if (this.itemMap.containsKey((Object)s)) {
                return;
            }
            final InventoryItem createItem = InventoryItemFactory.CreateItem(s);
            if (!(createItem instanceof Clothing)) {
                return;
            }
            final Clothing clothing = (Clothing)createItem;
            isoGameCharacter.getInventory().addItem(clothing);
            isoGameCharacter.setWornItem(clothing.getBodyLocation(), clothing);
            isoGameCharacter.resetModelNextFrame();
        }
        
        protected void removeBandageModel(final IsoGameCharacter isoGameCharacter, final String s) {
            final InventoryItem inventoryItem = (InventoryItem)this.itemMap.get((Object)s);
            if (inventoryItem == null) {
                return;
            }
            isoGameCharacter.getWornItems().remove(inventoryItem);
            isoGameCharacter.getInventory().Remove(inventoryItem);
            isoGameCharacter.resetModelNextFrame();
            isoGameCharacter.onWornItemsChanged();
            if (GameClient.bClient && isoGameCharacter instanceof IsoPlayer && ((IsoPlayer)isoGameCharacter).isLocalPlayer()) {
                GameClient.instance.sendClothing((IsoPlayer)isoGameCharacter, inventoryItem.getBodyLocation(), inventoryItem);
            }
        }
    }
}

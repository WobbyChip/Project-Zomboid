// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters;

import zombie.core.profiling.PerformanceProfileProbe;
import zombie.iso.SpriteDetails.IsoObjectType;
import java.util.Stack;
import zombie.core.utils.BooleanGrid;
import zombie.characters.AttachedItems.AttachedItem;
import zombie.characters.AttachedItems.AttachedItems;
import zombie.characters.WornItems.WornItem;
import zombie.characters.WornItems.WornItems;
import java.util.Iterator;
import zombie.debug.LogSeverity;
import zombie.network.ServerLOS;
import zombie.scripting.ScriptManager;
import zombie.ai.states.PlayerHitReactionState;
import zombie.PersistentOutfits;
import zombie.core.skinnedmodel.population.Outfit;
import zombie.core.skinnedmodel.population.OutfitManager;
import zombie.core.skinnedmodel.visual.ItemVisual;
import java.util.Collection;
import zombie.core.skinnedmodel.visual.BaseVisual;
import zombie.iso.IsoWorld;
import zombie.core.skinnedmodel.population.OutfitRNG;
import zombie.iso.objects.IsoFireManager;
import zombie.ai.astar.Mover;
import zombie.characterTextures.BloodBodyPartType;
import zombie.iso.objects.IsoZombieGiblets;
import zombie.iso.areas.IsoBuilding;
import java.util.List;
import zombie.util.list.PZArrayUtil;
import zombie.util.StringUtils;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.vehicles.VehiclePart;
import zombie.ai.ZombieGroupManager;
import zombie.ai.astar.AStarPathFinder;
import zombie.network.ServerMap;
import zombie.ai.states.BurntToDeath;
import zombie.VirtualZombieManager;
import zombie.network.MPStatistics;
import zombie.popman.ZombieCountOptimiser;
import zombie.popman.NetworkZombieSimulator;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.SoundManager;
import zombie.audio.FMODParameter;
import zombie.iso.LightingJNI;
import zombie.characters.skills.PerkFactory;
import zombie.popman.NetworkZombieManager;
import zombie.core.math.PZMath;
import zombie.core.opengl.RenderSettings;
import zombie.iso.SpriteDetails.IsoFlagType;
import java.util.Arrays;
import zombie.WorldSoundManager;
import zombie.GameTime;
import zombie.iso.LosUtil;
import zombie.ui.UIFont;
import zombie.ui.TextManager;
import zombie.debug.DebugOptions;
import zombie.core.skinnedmodel.DeadBodyAtlas;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.opengl.Shader;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;
import zombie.core.PerformanceSettings;
import zombie.iso.IsoCamera;
import zombie.iso.sprite.IsoSprite;
import zombie.Lua.LuaEventManager;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.core.Core;
import zombie.inventory.types.HandWeapon;
import zombie.iso.objects.IsoWindowFrame;
import zombie.SandboxOptions;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoThumpable;
import zombie.util.Type;
import zombie.iso.objects.IsoWindow;
import java.io.IOException;
import zombie.GameWindow;
import java.nio.ByteBuffer;
import zombie.iso.sprite.IsoAnim;
import zombie.core.skinnedmodel.animation.AnimationPlayer;
import zombie.characters.action.ActionContext;
import zombie.iso.IsoGridSquare;
import zombie.network.NetworkVariables;
import zombie.iso.objects.interfaces.Thumpable;
import zombie.iso.IsoObject;
import zombie.vehicles.PolygonalMap2;
import zombie.network.GameServer;
import zombie.ai.states.ClimbOverWallState;
import zombie.SystemDisabler;
import zombie.iso.IsoUtils;
import zombie.network.GameClient;
import java.util.HashMap;
import zombie.ai.states.ZombieTurnAlerted;
import zombie.ai.states.LungeState;
import zombie.ai.states.ZombieGetUpState;
import zombie.ai.states.ZombieGetDownState;
import zombie.ai.states.ZombieFaceTargetState;
import zombie.ai.states.ZombieFallingState;
import zombie.ai.states.ZombieFallDownState;
import zombie.ai.states.ZombieEatBodyState;
import zombie.ai.states.ClimbThroughWindowState;
import zombie.ai.states.ClimbOverFenceState;
import zombie.ai.states.BumpedState;
import zombie.vehicles.AttackVehicleState;
import zombie.ai.states.WalkTowardState;
import zombie.ai.states.CrawlingZombieTurnState;
import zombie.ai.states.ThumpState;
import zombie.ai.states.StaggerBackState;
import zombie.ai.states.ZombieReanimateState;
import zombie.ai.states.PathFindState;
import zombie.ai.states.ZombieOnGroundState;
import zombie.ai.states.ZombieHitReactionState;
import zombie.ai.states.ZombieGetUpFromCrawlState;
import zombie.ai.states.FakeDeadAttackState;
import zombie.ai.states.FakeDeadZombieState;
import zombie.ai.states.AttackState;
import zombie.ai.states.WalkTowardNetworkState;
import zombie.ai.states.LungeNetworkState;
import zombie.ai.states.IdleState;
import zombie.ai.states.ZombieSittingState;
import zombie.ai.states.AttackNetworkState;
import zombie.characters.action.ActionGroup;
import zombie.ai.State;
import zombie.ai.states.ZombieIdleState;
import zombie.iso.IsoDirections;
import zombie.core.Rand;
import zombie.iso.IsoCell;
import zombie.core.utils.OnceEvery;
import zombie.core.skinnedmodel.animation.sharedskele.SharedSkeleAnimationRepository;
import zombie.network.packets.ZombiePacket;
import zombie.core.raknet.UdpConnection;
import zombie.audio.parameters.ParameterZombieState;
import zombie.audio.parameters.ParameterVehicleHitLocation;
import zombie.audio.parameters.ParameterShoeType;
import zombie.audio.parameters.ParameterPlayerDistance;
import zombie.audio.parameters.ParameterFootstepMaterial2;
import zombie.audio.parameters.ParameterFootstepMaterial;
import zombie.audio.parameters.ParameterCharacterMovementSpeed;
import zombie.audio.parameters.ParameterCharacterInside;
import zombie.vehicles.BaseVehicle;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.iso.objects.IsoDeadBody;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.SharedDescriptors;
import zombie.iso.Vector2;
import zombie.inventory.InventoryItem;
import java.util.ArrayList;
import zombie.core.textures.Texture;
import zombie.iso.IsoMovingObject;
import zombie.core.skinnedmodel.visual.IHumanVisual;

public final class IsoZombie extends IsoGameCharacter implements IHumanVisual
{
    public static final byte SPEED_SPRINTER = 1;
    public static final byte SPEED_FAST_SHAMBLER = 2;
    public static final byte SPEED_SHAMBLER = 3;
    public static final byte SPEED_RANDOM = 4;
    private boolean alwaysKnockedDown;
    private boolean onlyJawStab;
    private boolean forceEatingAnimation;
    private boolean noTeeth;
    public static final int AllowRepathDelayMax = 120;
    public static final boolean SPRINTER_FIXES = true;
    public int LastTargetSeenX;
    public int LastTargetSeenY;
    public int LastTargetSeenZ;
    public boolean Ghost;
    public float LungeTimer;
    public long LungeSoundTime;
    public IsoMovingObject target;
    public float TimeSinceSeenFlesh;
    private float targetSeenTime;
    public int FollowCount;
    public int ZombieID;
    private float BonusSpotTime;
    public boolean bStaggerBack;
    private boolean bKnifeDeath;
    private boolean bJawStabAttach;
    private boolean bBecomeCrawler;
    private boolean bFakeDead;
    private boolean bForceFakeDead;
    private boolean bWasFakeDead;
    private boolean bReanimate;
    public Texture atlasTex;
    private boolean bReanimatedPlayer;
    public boolean bIndoorZombie;
    public int thumpFlag;
    public boolean thumpSent;
    private float thumpCondition;
    public boolean mpIdleSound;
    public float nextIdleSound;
    public static final float EAT_BODY_DIST = 1.0f;
    public static final float EAT_BODY_TIME = 3600.0f;
    public static final float LUNGE_TIME = 180.0f;
    public static final float CRAWLER_DAMAGE_DOT = 0.9f;
    public static final float CRAWLER_DAMAGE_RANGE = 1.5f;
    private boolean useless;
    public int speedType;
    public ZombieGroup group;
    public boolean inactive;
    public int strength;
    public int cognition;
    private ArrayList<InventoryItem> itemsToSpawnAtDeath;
    private float soundReactDelay;
    private final Location delayedSound;
    private boolean bSoundSourceRepeating;
    public Object soundSourceTarget;
    public float soundAttract;
    public float soundAttractTimeout;
    private final Vector2 hitAngle;
    public boolean alerted;
    private String walkType;
    private float footstepVolume;
    protected SharedDescriptors.Descriptor sharedDesc;
    public boolean bDressInRandomOutfit;
    public String pendingOutfitName;
    protected final HumanVisual humanVisual;
    private int crawlerType;
    private String playerAttackPosition;
    private float eatSpeed;
    private boolean sitAgainstWall;
    private static final int CHECK_FOR_CORPSE_TIMER_MAX = 10000;
    private float checkForCorpseTimer;
    public IsoDeadBody bodyToEat;
    public IsoMovingObject eatBodyTarget;
    private int hitTime;
    private int thumpTimer;
    private boolean hitLegsWhileOnFloor;
    public boolean collideWhileHit;
    private float m_characterTextureAnimTime;
    private float m_characterTextureAnimDuration;
    public int lastPlayerHit;
    protected final ItemVisuals itemVisuals;
    private int hitHeadWhileOnFloor;
    private BaseVehicle vehicle4testCollision;
    public String SpriteName;
    public static final int PALETTE_COUNT = 3;
    public final Vector2 vectorToTarget;
    public float AllowRepathDelay;
    public boolean KeepItReal;
    private boolean isSkeleton;
    private final ParameterCharacterInside parameterCharacterInside;
    private final ParameterCharacterMovementSpeed parameterCharacterMovementSpeed;
    private final ParameterFootstepMaterial parameterFootstepMaterial;
    private final ParameterFootstepMaterial2 parameterFootstepMaterial2;
    private final ParameterPlayerDistance parameterPlayerDistance;
    private final ParameterShoeType parameterShoeType;
    private final ParameterVehicleHitLocation parameterVehicleHitLocation;
    public final ParameterZombieState parameterZombieState;
    public boolean scratch;
    public boolean laceration;
    public final NetworkZombieAI networkAI;
    public UdpConnection authOwner;
    public IsoPlayer authOwnerPlayer;
    public ZombiePacket zombiePacket;
    public boolean zombiePacketUpdated;
    public long lastChangeOwner;
    private static final SharedSkeleAnimationRepository m_sharedSkeleRepo;
    public int palette;
    public int AttackAnimTime;
    public static int AttackAnimTimeMax;
    public IsoMovingObject spottedLast;
    Aggro[] aggroList;
    public int spotSoundDelay;
    public float movex;
    public float movey;
    private int stepFrameLast;
    private OnceEvery networkUpdate;
    public short lastRemoteUpdate;
    public short OnlineID;
    private static final ArrayList<IsoDeadBody> tempBodies;
    float timeSinceRespondToSound;
    public String walkVariantUse;
    public String walkVariant;
    public boolean bLunger;
    public boolean bRunning;
    public boolean bCrawling;
    private boolean bCanCrawlUnderVehicle;
    private boolean bCanWalk;
    public boolean bRemote;
    private static final FloodFill floodFill;
    public boolean ImmortalTutorialZombie;
    
    @Override
    public String getObjectName() {
        return "Zombie";
    }
    
    @Override
    public short getOnlineID() {
        return this.OnlineID;
    }
    
    public boolean isRemoteZombie() {
        return this.authOwner == null;
    }
    
    public void setVehicle4TestCollision(final BaseVehicle vehicle4testCollision) {
        this.vehicle4testCollision = vehicle4testCollision;
    }
    
    IsoZombie(final short onlineID) {
        super(null, 0.0f, 0.0f, 0.0f);
        this.alwaysKnockedDown = false;
        this.onlyJawStab = false;
        this.forceEatingAnimation = false;
        this.noTeeth = false;
        this.LastTargetSeenX = -1;
        this.LastTargetSeenY = -1;
        this.LastTargetSeenZ = -1;
        this.Ghost = false;
        this.LungeTimer = 0.0f;
        this.LungeSoundTime = 0L;
        this.TimeSinceSeenFlesh = 100000.0f;
        this.targetSeenTime = 0.0f;
        this.FollowCount = 0;
        this.ZombieID = 0;
        this.BonusSpotTime = 0.0f;
        this.bStaggerBack = false;
        this.bKnifeDeath = false;
        this.bJawStabAttach = false;
        this.bBecomeCrawler = false;
        this.bFakeDead = false;
        this.bForceFakeDead = false;
        this.bWasFakeDead = false;
        this.bReanimate = false;
        this.atlasTex = null;
        this.bReanimatedPlayer = false;
        this.bIndoorZombie = false;
        this.thumpFlag = 0;
        this.thumpSent = false;
        this.thumpCondition = 1.0f;
        this.mpIdleSound = false;
        this.nextIdleSound = 0.0f;
        this.useless = false;
        this.speedType = -1;
        this.inactive = false;
        this.strength = -1;
        this.cognition = -1;
        this.itemsToSpawnAtDeath = null;
        this.soundReactDelay = 0.0f;
        this.delayedSound = new Location(-1, -1, -1);
        this.bSoundSourceRepeating = false;
        this.soundSourceTarget = null;
        this.soundAttract = 0.0f;
        this.soundAttractTimeout = 0.0f;
        this.hitAngle = new Vector2();
        this.alerted = false;
        this.walkType = null;
        this.footstepVolume = 1.0f;
        this.bDressInRandomOutfit = false;
        this.humanVisual = new HumanVisual(this);
        this.crawlerType = 0;
        this.playerAttackPosition = null;
        this.eatSpeed = 1.0f;
        this.sitAgainstWall = false;
        this.checkForCorpseTimer = 10000.0f;
        this.bodyToEat = null;
        this.hitTime = 0;
        this.thumpTimer = 0;
        this.hitLegsWhileOnFloor = false;
        this.collideWhileHit = true;
        this.m_characterTextureAnimTime = 0.0f;
        this.m_characterTextureAnimDuration = 1.0f;
        this.lastPlayerHit = -1;
        this.itemVisuals = new ItemVisuals();
        this.hitHeadWhileOnFloor = 0;
        this.vehicle4testCollision = null;
        this.SpriteName = "BobZ";
        this.vectorToTarget = new Vector2();
        this.AllowRepathDelay = 0.0f;
        this.KeepItReal = false;
        this.isSkeleton = false;
        this.parameterCharacterInside = new ParameterCharacterInside(this);
        this.parameterCharacterMovementSpeed = new ParameterCharacterMovementSpeed(this);
        this.parameterFootstepMaterial = new ParameterFootstepMaterial(this);
        this.parameterFootstepMaterial2 = new ParameterFootstepMaterial2(this);
        this.parameterPlayerDistance = new ParameterPlayerDistance(this);
        this.parameterShoeType = new ParameterShoeType(this);
        this.parameterVehicleHitLocation = new ParameterVehicleHitLocation();
        this.parameterZombieState = new ParameterZombieState(this);
        this.scratch = false;
        this.laceration = false;
        this.authOwner = null;
        this.authOwnerPlayer = null;
        this.zombiePacket = new ZombiePacket();
        this.zombiePacketUpdated = false;
        this.lastChangeOwner = -1L;
        this.palette = 0;
        this.AttackAnimTime = 50;
        this.spottedLast = null;
        this.aggroList = new Aggro[4];
        this.spotSoundDelay = 0;
        this.stepFrameLast = -1;
        this.networkUpdate = new OnceEvery(1.0f);
        this.lastRemoteUpdate = 0;
        this.OnlineID = -1;
        this.timeSinceRespondToSound = 1000000.0f;
        this.walkVariantUse = null;
        this.walkVariant = "ZombieWalk";
        this.bCanCrawlUnderVehicle = true;
        this.bCanWalk = true;
        this.networkAI = null;
        this.OnlineID = onlineID;
    }
    
    public IsoZombie(final IsoCell isoCell) {
        this(isoCell, null, -1);
    }
    
    public IsoZombie(final IsoCell isoCell, final SurvivorDesc descriptor, final int palette) {
        super(isoCell, 0.0f, 0.0f, 0.0f);
        this.alwaysKnockedDown = false;
        this.onlyJawStab = false;
        this.forceEatingAnimation = false;
        this.noTeeth = false;
        this.LastTargetSeenX = -1;
        this.LastTargetSeenY = -1;
        this.LastTargetSeenZ = -1;
        this.Ghost = false;
        this.LungeTimer = 0.0f;
        this.LungeSoundTime = 0L;
        this.TimeSinceSeenFlesh = 100000.0f;
        this.targetSeenTime = 0.0f;
        this.FollowCount = 0;
        this.ZombieID = 0;
        this.BonusSpotTime = 0.0f;
        this.bStaggerBack = false;
        this.bKnifeDeath = false;
        this.bJawStabAttach = false;
        this.bBecomeCrawler = false;
        this.bFakeDead = false;
        this.bForceFakeDead = false;
        this.bWasFakeDead = false;
        this.bReanimate = false;
        this.atlasTex = null;
        this.bReanimatedPlayer = false;
        this.bIndoorZombie = false;
        this.thumpFlag = 0;
        this.thumpSent = false;
        this.thumpCondition = 1.0f;
        this.mpIdleSound = false;
        this.nextIdleSound = 0.0f;
        this.useless = false;
        this.speedType = -1;
        this.inactive = false;
        this.strength = -1;
        this.cognition = -1;
        this.itemsToSpawnAtDeath = null;
        this.soundReactDelay = 0.0f;
        this.delayedSound = new Location(-1, -1, -1);
        this.bSoundSourceRepeating = false;
        this.soundSourceTarget = null;
        this.soundAttract = 0.0f;
        this.soundAttractTimeout = 0.0f;
        this.hitAngle = new Vector2();
        this.alerted = false;
        this.walkType = null;
        this.footstepVolume = 1.0f;
        this.bDressInRandomOutfit = false;
        this.humanVisual = new HumanVisual(this);
        this.crawlerType = 0;
        this.playerAttackPosition = null;
        this.eatSpeed = 1.0f;
        this.sitAgainstWall = false;
        this.checkForCorpseTimer = 10000.0f;
        this.bodyToEat = null;
        this.hitTime = 0;
        this.thumpTimer = 0;
        this.hitLegsWhileOnFloor = false;
        this.collideWhileHit = true;
        this.m_characterTextureAnimTime = 0.0f;
        this.m_characterTextureAnimDuration = 1.0f;
        this.lastPlayerHit = -1;
        this.itemVisuals = new ItemVisuals();
        this.hitHeadWhileOnFloor = 0;
        this.vehicle4testCollision = null;
        this.SpriteName = "BobZ";
        this.vectorToTarget = new Vector2();
        this.AllowRepathDelay = 0.0f;
        this.KeepItReal = false;
        this.isSkeleton = false;
        this.parameterCharacterInside = new ParameterCharacterInside(this);
        this.parameterCharacterMovementSpeed = new ParameterCharacterMovementSpeed(this);
        this.parameterFootstepMaterial = new ParameterFootstepMaterial(this);
        this.parameterFootstepMaterial2 = new ParameterFootstepMaterial2(this);
        this.parameterPlayerDistance = new ParameterPlayerDistance(this);
        this.parameterShoeType = new ParameterShoeType(this);
        this.parameterVehicleHitLocation = new ParameterVehicleHitLocation();
        this.parameterZombieState = new ParameterZombieState(this);
        this.scratch = false;
        this.laceration = false;
        this.authOwner = null;
        this.authOwnerPlayer = null;
        this.zombiePacket = new ZombiePacket();
        this.zombiePacketUpdated = false;
        this.lastChangeOwner = -1L;
        this.palette = 0;
        this.AttackAnimTime = 50;
        this.spottedLast = null;
        this.aggroList = new Aggro[4];
        this.spotSoundDelay = 0;
        this.stepFrameLast = -1;
        this.networkUpdate = new OnceEvery(1.0f);
        this.lastRemoteUpdate = 0;
        this.OnlineID = -1;
        this.timeSinceRespondToSound = 1000000.0f;
        this.walkVariantUse = null;
        this.walkVariant = "ZombieWalk";
        this.bCanCrawlUnderVehicle = true;
        this.bCanWalk = true;
        this.registerVariableCallbacks();
        this.Health = 1.8f + Rand.Next(0.0f, 0.3f);
        this.weight = 0.7f;
        this.dir = IsoDirections.fromIndex(Rand.Next(8));
        this.humanVisual.randomBlood();
        if (descriptor != null) {
            this.descriptor = descriptor;
            this.palette = palette;
        }
        else {
            this.descriptor = SurvivorFactory.CreateSurvivor();
            this.palette = Rand.Next(3) + 1;
        }
        this.setFemale(this.descriptor.isFemale());
        this.SpriteName = (this.isFemale() ? "KateZ" : "BobZ");
        if (this.palette != 1) {
            this.SpriteName = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this.SpriteName, this.palette);
        }
        this.InitSpritePartsZombie();
        this.sprite.def.tintr = 0.95f + Rand.Next(5) / 100.0f;
        this.sprite.def.tintg = 0.95f + Rand.Next(5) / 100.0f;
        this.sprite.def.tintb = 0.95f + Rand.Next(5) / 100.0f;
        this.setDefaultState(ZombieIdleState.instance());
        this.setFakeDead(false);
        this.DoZombieStats();
        this.width = 0.3f;
        this.setAlphaAndTarget(0.0f);
        this.finder.maxSearchDistance = 20;
        if (this.isFemale()) {
            this.hurtSound = "FemaleZombieHurt";
        }
        this.initializeStates();
        this.actionContext.setGroup(ActionGroup.getActionGroup("zombie"));
        this.initWornItems("Human");
        this.initAttachedItems("Human");
        this.networkAI = new NetworkZombieAI(this);
        this.clearAggroList();
    }
    
    public void initializeStates() {
        final HashMap<String, State> stateUpdateLookup = this.getStateUpdateLookup();
        stateUpdateLookup.clear();
        stateUpdateLookup.put("attack-network", AttackNetworkState.instance());
        stateUpdateLookup.put("attackvehicle-network", IdleState.instance());
        stateUpdateLookup.put("fakedead-attack-network", IdleState.instance());
        stateUpdateLookup.put("lunge-network", LungeNetworkState.instance());
        stateUpdateLookup.put("walktoward-network", WalkTowardNetworkState.instance());
        if (this.bCrawling) {
            stateUpdateLookup.put("attack", AttackState.instance());
            stateUpdateLookup.put("fakedead", FakeDeadZombieState.instance());
            stateUpdateLookup.put("fakedead-attack", FakeDeadAttackState.instance());
            stateUpdateLookup.put("getup", ZombieGetUpFromCrawlState.instance());
            stateUpdateLookup.put("hitreaction", ZombieHitReactionState.instance());
            stateUpdateLookup.put("hitreaction-hit", ZombieHitReactionState.instance());
            stateUpdateLookup.put("idle", ZombieIdleState.instance());
            stateUpdateLookup.put("onground", ZombieOnGroundState.instance());
            stateUpdateLookup.put("pathfind", PathFindState.instance());
            stateUpdateLookup.put("reanimate", ZombieReanimateState.instance());
            stateUpdateLookup.put("staggerback", StaggerBackState.instance());
            stateUpdateLookup.put("thump", ThumpState.instance());
            stateUpdateLookup.put("turn", CrawlingZombieTurnState.instance());
            stateUpdateLookup.put("walktoward", WalkTowardState.instance());
        }
        else {
            stateUpdateLookup.put("attack", AttackState.instance());
            stateUpdateLookup.put("attackvehicle", AttackVehicleState.instance());
            stateUpdateLookup.put("bumped", BumpedState.instance());
            stateUpdateLookup.put("climbfence", ClimbOverFenceState.instance());
            stateUpdateLookup.put("climbwindow", ClimbThroughWindowState.instance());
            stateUpdateLookup.put("eatbody", ZombieEatBodyState.instance());
            stateUpdateLookup.put("falldown", ZombieFallDownState.instance());
            stateUpdateLookup.put("falling", ZombieFallingState.instance());
            stateUpdateLookup.put("face-target", ZombieFaceTargetState.instance());
            stateUpdateLookup.put("fakedead", FakeDeadZombieState.instance());
            stateUpdateLookup.put("fakedead-attack", FakeDeadAttackState.instance());
            stateUpdateLookup.put("getdown", ZombieGetDownState.instance());
            stateUpdateLookup.put("getup", ZombieGetUpState.instance());
            stateUpdateLookup.put("hitreaction", ZombieHitReactionState.instance());
            stateUpdateLookup.put("hitreaction-hit", ZombieHitReactionState.instance());
            stateUpdateLookup.put("idle", ZombieIdleState.instance());
            stateUpdateLookup.put("lunge", LungeState.instance());
            stateUpdateLookup.put("onground", ZombieOnGroundState.instance());
            stateUpdateLookup.put("pathfind", PathFindState.instance());
            stateUpdateLookup.put("sitting", ZombieSittingState.instance());
            stateUpdateLookup.put("staggerback", StaggerBackState.instance());
            stateUpdateLookup.put("thump", ThumpState.instance());
            stateUpdateLookup.put("turnalerted", ZombieTurnAlerted.instance());
            stateUpdateLookup.put("walktoward", WalkTowardState.instance());
        }
    }
    
    private void registerVariableCallbacks() {
        this.setVariable("bClient", () -> GameClient.bClient && this.isRemoteZombie());
        this.setVariable("bMovingNetwork", () -> (this.isLocal() || !this.isBumped()) && (IsoUtils.DistanceManhatten(this.networkAI.targetX, this.networkAI.targetY, this.x, this.y) > 0.5f || this.z != this.networkAI.targetZ));
        this.setVariable("hitHeadType", this::getHitHeadWhileOnFloor);
        this.setVariable("realState", this::getRealState);
        final IsoGridSquare isoGridSquare;
        final IsoGridSquare isoGridSquare2;
        this.setVariable("battack", () -> {
            if (SystemDisabler.zombiesDontAttack) {
                return Boolean.valueOf(false);
            }
            else if (this.target == null || this.target.isZombiesDontAttack()) {
                return Boolean.valueOf(false);
            }
            else {
                if (this.target instanceof IsoGameCharacter) {
                    if (this.target.isOnFloor() && ((IsoGameCharacter)this.target).getCurrentState() != BumpedState.instance()) {
                        this.setTarget(null);
                        return Boolean.valueOf(false);
                    }
                    else if (((IsoGameCharacter)this.target).getVehicle() != null) {
                        return Boolean.valueOf(false);
                    }
                    else if (((IsoGameCharacter)this.target).ReanimatedCorpse != null) {
                        return Boolean.valueOf(false);
                    }
                    else if (((IsoGameCharacter)this.target).getStateMachine().getCurrent() == ClimbOverWallState.instance()) {
                        return Boolean.valueOf(false);
                    }
                }
                if (this.bReanimate) {
                    return Boolean.valueOf(false);
                }
                else if (Math.abs(this.target.z - this.z) >= 0.2f) {
                    return Boolean.valueOf(false);
                }
                else if (this.target instanceof IsoPlayer && ((IsoPlayer)this.target).isGhostMode()) {
                    return Boolean.valueOf(false);
                }
                else if (this.bFakeDead) {
                    return Boolean.valueOf(!this.isUnderVehicle() && this.DistTo(this.target) < 1.3f);
                }
                else if (this.bCrawling) {
                    return Boolean.valueOf(!this.isUnderVehicle() && this.DistTo(this.target) < 1.3f);
                }
                else {
                    this.getCurrentSquare();
                    this.target.getCurrentSquare();
                    if (isoGridSquare != null && isoGridSquare.isSomethingTo(isoGridSquare2)) {
                        return Boolean.valueOf(false);
                    }
                    else {
                        return Boolean.valueOf(this.vectorToTarget.getLength() <= (this.bCrawling ? 1.4f : 0.72f));
                    }
                }
            }
        });
        this.setVariable("isFacingTarget", this::isFacingTarget);
        this.setVariable("targetSeenTime", this::getTargetSeenTime);
        final BaseVehicle baseVehicle;
        this.setVariable("battackvehicle", () -> {
            if (this.getVariableBoolean("bPathfind")) {
                return Boolean.valueOf(false);
            }
            else if (this.isMoving()) {
                return Boolean.valueOf(false);
            }
            else if (this.target == null) {
                return Boolean.valueOf(false);
            }
            else if (Math.abs(this.target.z - this.z) >= 0.8f) {
                return Boolean.valueOf(false);
            }
            else if (this.target instanceof IsoPlayer && ((IsoPlayer)this.target).isGhostMode()) {
                return Boolean.valueOf(false);
            }
            else if (this.target instanceof IsoGameCharacter) {
                ((IsoGameCharacter)this.target).getVehicle();
                return Boolean.valueOf(baseVehicle != null && baseVehicle.isCharacterAdjacentTo(this));
            }
            else {
                return Boolean.valueOf(false);
            }
        });
        this.setVariable("bdead", this::isDead);
        this.setVariable("beatbodytarget", () -> {
            if (this.isForceEatingAnimation()) {
                return Boolean.valueOf(true);
            }
            else {
                if (!GameServer.bServer) {
                    this.updateEatBodyTarget();
                }
                return Boolean.valueOf(this.getEatBodyTarget() != null);
            }
        });
        this.setVariable("bbecomecrawler", this::isBecomeCrawler, this::setBecomeCrawler);
        this.setVariable("bfakedead", () -> this.bFakeDead);
        this.setVariable("bfalling", () -> this.z > 0.0f && this.fallTime > 2.0f);
        this.setVariable("bhastarget", () -> {
            if (this.target instanceof IsoGameCharacter && ((IsoGameCharacter)this.target).ReanimatedCorpse != null) {
                this.setTarget(null);
            }
            return Boolean.valueOf(this.target != null);
        });
        this.setVariable("shouldSprint", () -> {
            if (this.target instanceof IsoGameCharacter && ((IsoGameCharacter)this.target).ReanimatedCorpse != null) {
                this.setTarget(null);
            }
            return Boolean.valueOf(this.target != null || (this.soundSourceTarget != null && !(this.soundSourceTarget instanceof IsoZombie)));
        });
        this.setVariable("bknockeddown", this::isKnockedDown);
        final IsoGridSquare isoGridSquare3;
        final IsoGridSquare isoGridSquare4;
        final float n;
        this.setVariable("blunge", () -> {
            if (this.target == null) {
                return Boolean.valueOf(false);
            }
            else if ((int)this.getZ() != (int)this.target.getZ()) {
                return Boolean.valueOf(false);
            }
            else {
                if (this.target instanceof IsoGameCharacter) {
                    if (((IsoGameCharacter)this.target).getVehicle() != null) {
                        return Boolean.valueOf(false);
                    }
                    else if (((IsoGameCharacter)this.target).ReanimatedCorpse != null) {
                        return Boolean.valueOf(false);
                    }
                }
                if (this.target instanceof IsoPlayer && ((IsoPlayer)this.target).isGhostMode()) {
                    this.setTarget(null);
                    return Boolean.valueOf(false);
                }
                else {
                    this.getCurrentSquare();
                    this.target.getCurrentSquare();
                    if (isoGridSquare3 != null && isoGridSquare3.isSomethingTo(isoGridSquare4) && this.getThumpTarget() != null) {
                        return Boolean.valueOf(false);
                    }
                    else if (this.isCurrentState(ZombieTurnAlerted.instance()) && !this.isFacingTarget()) {
                        return Boolean.valueOf(false);
                    }
                    else {
                        this.vectorToTarget.getLength();
                        if (n > 3.5f && (n > 4.0f || !(this.target instanceof IsoGameCharacter) || ((IsoGameCharacter)this.target).getVehicle() == null)) {
                            return Boolean.valueOf(false);
                        }
                        else {
                            return Boolean.valueOf(!PolygonalMap2.instance.lineClearCollide(this.getX(), this.getY(), this.target.x, this.target.y, (int)this.getZ(), this.target, false, true));
                        }
                    }
                }
            }
        });
        this.setVariable("bpassengerexposed", () -> AttackVehicleState.instance().isPassengerExposed(this));
        this.setVariable("bistargetissmallvehicle", () -> {
            if (this.target != null && this.target instanceof IsoPlayer && ((IsoPlayer)this.target).getVehicle() != null) {
                return Boolean.valueOf(((IsoPlayer)this.target).getVehicle().getScript().isSmallVehicle);
            }
            else {
                return Boolean.valueOf(true);
            }
        });
        this.setVariable("breanimate", this::isReanimate, this::setReanimate);
        this.setVariable("bstaggerback", this::isStaggerBack);
        IsoObject isoObject;
        this.setVariable("bthump", () -> {
            if (this.getThumpTarget() instanceof IsoObject && !(this.getThumpTarget() instanceof BaseVehicle)) {
                isoObject = (IsoObject)this.getThumpTarget();
                if (isoObject != null && this.DistToSquared(isoObject.getX() + 0.5f, isoObject.getY() + 0.5f) > 9.0f) {
                    this.setThumpTarget(null);
                }
            }
            if (this.getThumpTimer() > 0) {
                this.setThumpTarget(null);
            }
            return Boolean.valueOf(this.getThumpTarget() != null);
        });
        this.setVariable("bundervehicle", this::isUnderVehicle);
        this.setVariable("bBeingSteppedOn", this::isBeingSteppedOn);
        this.setVariable("distancetotarget", () -> {
            if (this.target == null) {
                return "";
            }
            else {
                return String.valueOf(this.vectorToTarget.getLength() - this.getWidth() + this.target.getWidth());
            }
        });
        this.setVariable("lasttargetseen", () -> this.LastTargetSeenX != -1);
        this.setVariable("lungetimer", () -> this.LungeTimer);
        this.setVariable("reanimatetimer", this::getReanimateTimer);
        this.setVariable("stateeventdelaytimer", this::getStateEventDelayTimer);
        Vector2 tempo;
        Vector2 tempo2;
        final IsoDirections isoDirections;
        final IsoDirections isoDirections2;
        this.setVariable("turndirection", () -> {
            if (this.getPath2() != null) {
                return "";
            }
            else if (this.target == null || this.vectorToTarget.getLength() == 0.0f) {
                if (this.isCurrentState(WalkTowardState.instance())) {
                    WalkTowardState.instance().calculateTargetLocation(this, IsoZombie.tempo);
                    tempo = IsoZombie.tempo;
                    tempo.x -= this.getX();
                    tempo2 = IsoZombie.tempo;
                    tempo2.y -= this.getY();
                    IsoDirections.fromAngle(IsoZombie.tempo);
                    if (this.dir == isoDirections) {
                        return "";
                    }
                    else {
                        return CrawlingZombieTurnState.calculateDir(this, isoDirections) ? "left" : "right";
                    }
                }
                else {
                    if (this.isCurrentState(PathFindState.instance())) {}
                    return "";
                }
            }
            else {
                IsoDirections.fromAngle(this.vectorToTarget);
                if (this.dir == isoDirections2) {
                    return "";
                }
                else {
                    return CrawlingZombieTurnState.calculateDir(this, isoDirections2) ? "left" : "right";
                }
            }
        });
        this.setVariable("hitforce", this::getHitForce);
        this.setVariable("alerted", () -> this.alerted);
        this.setVariable("zombiewalktype", () -> this.walkType);
        this.setVariable("crawlertype", () -> this.crawlerType);
        this.setVariable("bGetUpFromCrawl", this::shouldGetUpFromCrawl);
        this.setVariable("playerattackposition", this::getPlayerAttackPosition);
        this.setVariable("eatspeed", () -> this.eatSpeed);
        this.setVariable("issitting", this::isSitAgainstWall);
        this.setVariable("bKnifeDeath", this::isKnifeDeath, this::setKnifeDeath);
        this.setVariable("bJawStabAttach", this::isJawStabAttach, this::setJawStabAttach);
        this.setVariable("bPathFindPrediction", () -> NetworkVariables.PredictionTypes.PathFind.equals(this.networkAI.predictionType));
        this.setVariable("bCrawling", this::isCrawling, this::setCrawler);
    }
    
    @Override
    public void actionStateChanged(final ActionContext actionContext) {
        super.actionStateChanged(actionContext);
        if (this.networkAI != null && GameServer.bServer) {
            this.networkAI.extraUpdate();
        }
    }
    
    @Override
    public ActionContext getActionContext() {
        return this.actionContext;
    }
    
    @Override
    protected void onAnimPlayerCreated(final AnimationPlayer animationPlayer) {
        super.onAnimPlayerCreated(animationPlayer);
        animationPlayer.setSharedAnimRepo(IsoZombie.m_sharedSkeleRepo);
    }
    
    @Override
    public String GetAnimSetName() {
        return this.bCrawling ? "zombie-crawler" : "zombie";
    }
    
    public void InitSpritePartsZombie() {
        this.InitSpritePartsZombie(this.descriptor);
    }
    
    public void InitSpritePartsZombie(final SurvivorDesc survivorDesc) {
        this.sprite.AnimMap.clear();
        this.sprite.AnimStack.clear();
        this.sprite.CurrentAnim = new IsoAnim();
        this.sprite.CurrentAnim.name = "REMOVE";
        this.legsSprite = this.sprite;
        this.legsSprite.name = survivorDesc.torso;
        this.ZombieID = Rand.Next(10000);
        this.bUseParts = true;
    }
    
    @Override
    public void pathToCharacter(final IsoGameCharacter isoGameCharacter) {
        if (this.AllowRepathDelay > 0.0f && (this.isCurrentState(PathFindState.instance()) || this.isCurrentState(WalkTowardState.instance()) || this.isCurrentState(WalkTowardNetworkState.instance()))) {
            return;
        }
        super.pathToCharacter(isoGameCharacter);
    }
    
    @Override
    public void pathToLocationF(final float n, final float n2, final float n3) {
        if (this.AllowRepathDelay > 0.0f && (this.isCurrentState(PathFindState.instance()) || this.isCurrentState(WalkTowardState.instance()) || this.isCurrentState(WalkTowardNetworkState.instance()))) {
            return;
        }
        super.pathToLocationF(n, n2, n3);
    }
    
    @Override
    public void load(final ByteBuffer byteBuffer, final int n, final boolean b) throws IOException {
        super.load(byteBuffer, n, b);
        this.walkVariant = "ZombieWalk";
        this.SpriteName = "BobZ";
        if (this.palette != 1) {
            this.SpriteName = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this.SpriteName, this.palette);
        }
        final SurvivorDesc descriptor = this.descriptor;
        this.setFemale(descriptor.isFemale());
        if (this.isFemale()) {
            if (this.palette == 1) {
                this.SpriteName = "KateZ";
            }
            else {
                this.SpriteName = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.palette);
            }
        }
        if (this.isFemale()) {
            this.hurtSound = "FemaleZombieHurt";
        }
        else {
            this.hurtSound = "MaleZombieHurt";
        }
        this.InitSpritePartsZombie(descriptor);
        this.sprite.def.tintr = 0.95f + Rand.Next(5) / 100.0f;
        this.sprite.def.tintg = 0.95f + Rand.Next(5) / 100.0f;
        this.sprite.def.tintb = 0.95f + Rand.Next(5) / 100.0f;
        this.setDefaultState(ZombieIdleState.instance());
        this.DoZombieStats();
        byteBuffer.getFloat();
        this.setWidth(0.3f);
        this.TimeSinceSeenFlesh = (float)byteBuffer.getInt();
        this.setAlpha(0.0f);
        this.setFakeDead(byteBuffer.getInt() == 1);
        final ArrayList<InventoryItem> savedInventoryItems = this.savedInventoryItems;
        for (byte value = byteBuffer.get(), b2 = 0; b2 < value; ++b2) {
            final String readString = GameWindow.ReadString(byteBuffer);
            final short short1 = byteBuffer.getShort();
            if (short1 >= 0 && short1 < savedInventoryItems.size() && this.wornItems.getBodyLocationGroup().getLocation(readString) != null) {
                this.wornItems.setItem(readString, savedInventoryItems.get(short1));
            }
        }
        this.setStateMachineLocked(false);
        this.setDefaultState();
        this.getCell().getZombieList().add(this);
    }
    
    @Override
    public void save(final ByteBuffer byteBuffer, final boolean b) throws IOException {
        super.save(byteBuffer, b);
        byteBuffer.putFloat(0.0f);
        byteBuffer.putInt((int)this.TimeSinceSeenFlesh);
        byteBuffer.putInt(this.isFakeDead() ? 1 : 0);
        if (this.wornItems.size() > 127) {
            throw new RuntimeException("too many worn items");
        }
        byteBuffer.put((byte)this.wornItems.size());
        this.wornItems.forEach(wornItem -> {
            GameWindow.WriteString(byteBuffer, wornItem.getLocation());
            byteBuffer.putShort((short)this.savedInventoryItems.indexOf(wornItem.getItem()));
        });
    }
    
    @Override
    public void collideWith(IsoObject rerouteCollide) {
        if (this.Ghost || rerouteCollide == null) {
            return;
        }
        if (rerouteCollide.rerouteCollide != null) {
            rerouteCollide = this.rerouteCollide;
        }
        final State currentState = this.getCurrentState();
        final boolean b = this.isCurrentState(PathFindState.instance()) || this.isCurrentState(LungeState.instance()) || this.isCurrentState(LungeNetworkState.instance()) || this.isCurrentState(WalkTowardState.instance()) || this.isCurrentState(WalkTowardNetworkState.instance());
        final IsoWindow isoWindow = Type.tryCastTo(rerouteCollide, IsoWindow.class);
        if (isoWindow != null && isoWindow.canClimbThrough(this) && b) {
            if (!this.isFacingObject(isoWindow, 0.8f)) {
                super.collideWith(rerouteCollide);
                return;
            }
            if (currentState != PathFindState.instance() && !this.bCrawling) {
                this.climbThroughWindow(isoWindow);
            }
        }
        else if (rerouteCollide instanceof IsoThumpable && ((IsoThumpable)rerouteCollide).canClimbThrough(this) && b) {
            if (currentState != PathFindState.instance() && !this.bCrawling) {
                this.climbThroughWindow((IsoThumpable)rerouteCollide);
            }
        }
        else if (!(rerouteCollide instanceof IsoDoor) || !((IsoDoor)rerouteCollide).isHoppable()) {
            if (rerouteCollide != null && rerouteCollide.getThumpableFor(this) != null && b) {
                final boolean b2 = (this.isCurrentState(PathFindState.instance()) || this.isCurrentState(WalkTowardState.instance()) || this.isCurrentState(WalkTowardNetworkState.instance())) && this.getPathFindBehavior2().isGoalSound();
                if (SandboxOptions.instance.Lore.ThumpNoChasing.getValue() || this.target != null || b2) {
                    if (rerouteCollide instanceof IsoThumpable && !SandboxOptions.instance.Lore.ThumpOnConstruction.getValue()) {
                        return;
                    }
                    Thumpable thumpable = rerouteCollide;
                    if (rerouteCollide instanceof IsoWindow && rerouteCollide.getThumpableFor(this) != null && rerouteCollide.isDestroyed()) {
                        thumpable = rerouteCollide.getThumpableFor(this);
                    }
                    this.setThumpTarget(thumpable);
                }
                else {
                    this.setVariable("bPathfind", false);
                    this.setVariable("bMoving", false);
                }
                this.setPath2(null);
            }
        }
        if (!this.bCrawling && IsoWindowFrame.isWindowFrame(rerouteCollide) && b && currentState != PathFindState.instance()) {
            this.climbThroughWindowFrame(rerouteCollide);
        }
        super.collideWith(rerouteCollide);
    }
    
    @Override
    public float Hit(final HandWeapon handWeapon, final IsoGameCharacter isoGameCharacter, final float n, final boolean b, final float n2, final boolean b2) {
        if (Core.bTutorial && this.ImmortalTutorialZombie) {
            return 0.0f;
        }
        BodyPartType bodyPartType = BodyPartType.FromIndex(Rand.Next(BodyPartType.ToIndex(BodyPartType.Torso_Upper), BodyPartType.ToIndex(BodyPartType.Torso_Lower) + 1));
        if (Rand.NextBool(7)) {
            bodyPartType = BodyPartType.Head;
        }
        if (isoGameCharacter.isCriticalHit() && Rand.NextBool(3)) {
            bodyPartType = BodyPartType.Head;
        }
        LuaEventManager.triggerEvent("OnHitZombie", this, isoGameCharacter, bodyPartType, handWeapon);
        final float hit = super.Hit(handWeapon, isoGameCharacter, n, b, n2, b2);
        if (GameServer.bServer && !this.isRemoteZombie()) {
            this.addAggro(isoGameCharacter, hit);
        }
        this.TimeSinceSeenFlesh = 0.0f;
        if (!this.isDead() && !this.isOnFloor() && !b && handWeapon != null && handWeapon.getScriptItem().getCategories().contains("Blade") && isoGameCharacter instanceof IsoPlayer && this.DistToProper(isoGameCharacter) <= 0.9f && (this.isCurrentState(AttackState.instance()) || this.isCurrentState(AttackNetworkState.instance()) || this.isCurrentState(LungeState.instance()) || this.isCurrentState(LungeNetworkState.instance()))) {
            this.setHitForce(0.5f);
            this.changeState(StaggerBackState.instance());
        }
        if (GameServer.bServer || (GameClient.bClient && this.isDead())) {
            this.lastPlayerHit = isoGameCharacter.getOnlineID();
        }
        return hit;
    }
    
    public void onMouseLeftClick() {
        if (IsoPlayer.getInstance() == null || IsoPlayer.getInstance().isAiming()) {
            return;
        }
        if (IsoPlayer.getInstance().IsAttackRange(this.getX(), this.getY(), this.getZ())) {
            final Vector2 vector3;
            final Vector2 vector2 = vector3 = new Vector2(this.getX(), this.getY());
            vector3.x -= IsoPlayer.getInstance().getX();
            final Vector2 vector4 = vector2;
            vector4.y -= IsoPlayer.getInstance().getY();
            vector2.normalize();
            IsoPlayer.getInstance().DirectionFromVector(vector2);
            IsoPlayer.getInstance().AttemptAttack();
        }
    }
    
    private void renderAtlasTexture(final float n, final float n2, final float n3) {
        if (this.atlasTex == null) {
            return;
        }
        if (IsoSprite.globalOffsetX == -1.0f) {
            IsoSprite.globalOffsetX = -IsoCamera.frameState.OffX;
            IsoSprite.globalOffsetY = -IsoCamera.frameState.OffY;
        }
        final float xToScreen = IsoUtils.XToScreen(n, n2, n3, 0);
        final float yToScreen = IsoUtils.YToScreen(n, n2, n3, 0);
        this.sx = xToScreen;
        this.sy = yToScreen;
        final float n4 = this.sx + IsoSprite.globalOffsetX;
        final float n5 = this.sy + IsoSprite.globalOffsetY;
        final ColorInfo set = IsoZombie.inf.set(1.0f, 1.0f, 1.0f, 1.0f);
        if (PerformanceSettings.LightingFrameSkip < 3 && this.getCurrentSquare() != null) {
            this.getCurrentSquare().interpolateLight(set, n - this.getCurrentSquare().getX(), n2 - this.getCurrentSquare().getY());
        }
        this.atlasTex.render((float)((int)n4 - this.atlasTex.getWidth() / 2), (float)((int)n5 - this.atlasTex.getHeight() / 2), (float)this.atlasTex.getWidth(), (float)this.atlasTex.getHeight(), set.r, set.g, set.b, set.a, null);
    }
    
    @Override
    public void render(final float n, final float n2, final float n3, final ColorInfo colorInfo, final boolean b, final boolean b2, final Shader shader) {
        if (this.getCurrentState() == FakeDeadZombieState.instance()) {
            if (this.bDressInRandomOutfit) {
                ModelManager.instance.dressInRandomOutfit(this);
            }
            if (this.atlasTex == null) {
                this.atlasTex = DeadBodyAtlas.instance.getBodyTexture(this);
                DeadBodyAtlas.instance.render();
            }
            if (this.atlasTex != null) {
                this.renderAtlasTexture(n, n2, n3);
            }
            return;
        }
        if (this.atlasTex != null) {
            this.atlasTex = null;
        }
        if (IsoCamera.CamCharacter != IsoPlayer.getInstance()) {
            this.setAlphaAndTarget(1.0f);
        }
        super.render(n, n2, n3, colorInfo, b, b2, shader);
    }
    
    @Override
    public void renderlast() {
        super.renderlast();
        if (DebugOptions.instance.ZombieRenderCanCrawlUnderVehicle.getValue() && this.isCanCrawlUnderVehicle()) {
            this.renderTextureOverHead("media/ui/FavoriteStar.png");
        }
        if (DebugOptions.instance.ZombieRenderMemory.getValue()) {
            String s;
            if (this.target == null) {
                s = "media/ui/Moodles/Moodle_Icon_Bored.png";
            }
            else if (this.BonusSpotTime == 0.0f) {
                s = "media/ui/Moodles/Moodle_Icon_Angry.png";
            }
            else {
                s = "media/ui/Moodles/Moodle_Icon_Zombie.png";
            }
            this.renderTextureOverHead(s);
            final int n = (int)IsoUtils.XToScreenExact(this.x, this.y, this.z, 0);
            final int n2 = (int)IsoUtils.YToScreenExact(this.x, this.y, this.z, 0);
            final int lineHeight = TextManager.instance.getFontFromEnum(UIFont.Small).getLineHeight();
            final int n3;
            TextManager.instance.DrawString((double)n, (double)(n3 = n2 + lineHeight), invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.AllowRepathDelay));
            final int n4;
            TextManager.instance.DrawString((double)n, (double)(n4 = n3 + lineHeight), invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.BonusSpotTime));
            TextManager.instance.DrawString((double)n, (double)(n4 + lineHeight), invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.TimeSinceSeenFlesh));
        }
    }
    
    @Override
    protected boolean renderTextureInsteadOfModel(final float n, final float n2) {
        final boolean b = this.isCurrentState(WalkTowardState.instance()) || this.isCurrentState(PathFindState.instance());
        final String s = "zombie";
        final String s2 = b ? "walktoward" : "idle";
        final int n3 = 4;
        final int n4 = (int)(this.m_characterTextureAnimTime / this.m_characterTextureAnimDuration * n3);
        final Texture bodyTexture = DeadBodyAtlas.instance.getBodyTexture(this.isFemale(), s, s2, this.getDir(), n4, (b ? 0.67f : 1.0f) * (n4 / (float)n3));
        if (bodyTexture != null && bodyTexture.isReady()) {
            final float n5 = (float)Core.TileScale;
            bodyTexture.render(IsoUtils.XToScreen(n, n2, this.getZ(), 0) - IsoCamera.getOffX() - (this.offsetX + 1.0f * n5) - bodyTexture.getWidthOrig() / 2, IsoUtils.YToScreen(n, n2, this.getZ(), 0) - IsoCamera.getOffY() - (this.offsetY + -89.0f * n5) - bodyTexture.getHeightOrig() - 64.0f * n5, (float)bodyTexture.getWidth(), (float)bodyTexture.getHeight(), 0.0f, 0.0f, 0.0f, this.getAlpha(IsoCamera.frameState.playerIndex), null);
        }
        if (DebugOptions.instance.Character.Debug.Render.Angle.getValue()) {
            IsoZombie.tempo.set(this.dir.ToVector());
            this.drawDirectionLine(IsoZombie.tempo, 1.2f, 0.0f, 1.0f, 0.0f);
        }
        return true;
    }
    
    private void renderTextureOverHead(final String s) {
        final float x = this.x;
        final float y = this.y;
        final float xToScreen = IsoUtils.XToScreen(x, y, this.getZ(), 0);
        final float yToScreen = IsoUtils.YToScreen(x, y, this.getZ(), 0);
        final float n = xToScreen - IsoCamera.getOffX() - this.offsetX;
        final float n2 = yToScreen - IsoCamera.getOffY() - this.offsetY - 128 / (2 / Core.TileScale);
        final Texture sharedTexture = Texture.getSharedTexture(s);
        final float max = Math.max(Core.getInstance().getZoom(IsoCamera.frameState.playerIndex), 1.0f);
        final int n3 = (int)(sharedTexture.getWidth() * max);
        final int n4 = (int)(sharedTexture.getHeight() * max);
        sharedTexture.render((float)((int)n - n3 / 2), (float)((int)n2 - n4), (float)n3, (float)n4);
    }
    
    protected void updateAlpha(final int n, final float n2, final float n3) {
        if (this.isFakeDead()) {
            this.setAlphaAndTarget(1.0f);
            return;
        }
        super.updateAlpha(n, n2, n3);
    }
    
    public void RespondToSound() {
        if (this.Ghost) {
            return;
        }
        if (this.isUseless()) {
            return;
        }
        if (GameServer.bServer) {
            return;
        }
        if (GameClient.bClient && this.isRemoteZombie()) {
            return;
        }
        if ((this.getCurrentState() == PathFindState.instance() || this.getCurrentState() == WalkTowardState.instance()) && this.getPathFindBehavior2().isGoalSound() && (int)this.z == this.getPathTargetZ() && this.bSoundSourceRepeating && this.DistToSquared((float)this.getPathTargetX(), (float)this.getPathTargetY()) < 25.0f && LosUtil.lineClear(this.getCell(), (int)this.x, (int)this.y, (int)this.z, this.getPathTargetX(), this.getPathTargetY(), (int)this.z, false) != LosUtil.TestResults.Blocked) {
            this.setVariable("bPathfind", false);
            this.setVariable("bMoving", false);
            this.setPath2(null);
        }
        if (this.soundReactDelay > 0.0f) {
            this.soundReactDelay -= GameTime.getInstance().getMultiplier() / 1.6f;
            if (this.soundReactDelay < 0.0f) {
                this.soundReactDelay = 0.0f;
            }
            if (this.soundReactDelay > 0.0f) {
                return;
            }
        }
        float attract = 0.0f;
        Object soundSourceTarget = null;
        WorldSoundManager.WorldSound worldSound = WorldSoundManager.instance.getSoundZomb(this);
        final float soundAttract = WorldSoundManager.instance.getSoundAttract(worldSound, this);
        if (soundAttract <= 0.0f) {
            worldSound = null;
        }
        if (worldSound != null) {
            attract = soundAttract;
            soundSourceTarget = worldSound.source;
            this.soundAttract = attract;
            this.soundAttractTimeout = 60.0f;
        }
        else if (this.soundAttractTimeout > 0.0f) {
            this.soundAttractTimeout -= GameTime.getInstance().getMultiplier() / 1.6f;
            if (this.soundAttractTimeout < 0.0f) {
                this.soundAttractTimeout = 0.0f;
            }
        }
        final WorldSoundManager.ResultBiggestSound biggestSoundZomb = WorldSoundManager.instance.getBiggestSoundZomb((int)this.getX(), (int)this.getY(), (int)this.getZ(), true, this);
        if (biggestSoundZomb.sound != null && (this.soundAttractTimeout == 0.0f || this.soundAttract * 2.0f < biggestSoundZomb.attract)) {
            worldSound = biggestSoundZomb.sound;
            attract = biggestSoundZomb.attract;
            soundSourceTarget = worldSound.source;
        }
        if (worldSound != null && worldSound.bRepeating && worldSound.z == (int)this.z && this.DistToSquared((float)worldSound.x, (float)worldSound.y) < 25.0f && LosUtil.lineClear(this.getCell(), (int)this.x, (int)this.y, (int)this.z, worldSound.x, worldSound.y, (int)this.z, false) != LosUtil.TestResults.Blocked) {
            worldSound = null;
        }
        if (worldSound != null) {
            this.soundAttract = attract;
            this.soundSourceTarget = soundSourceTarget;
            this.soundReactDelay = (float)Rand.Next(0, 16);
            this.delayedSound.x = worldSound.x;
            this.delayedSound.y = worldSound.y;
            this.delayedSound.z = worldSound.z;
            this.bSoundSourceRepeating = worldSound.bRepeating;
        }
        if (this.delayedSound.x != -1 && this.soundReactDelay == 0.0f) {
            final int x = this.delayedSound.x;
            final int y = this.delayedSound.y;
            final int z = this.delayedSound.z;
            this.delayedSound.x = -1;
            final float n = IsoUtils.DistanceManhatten(this.getX(), this.getY(), (float)x, (float)y) / 2.5f;
            final int n2 = x + Rand.Next((int)(-n), (int)n);
            final int n3 = y + Rand.Next((int)(-n), (int)n);
            if ((this.getCurrentState() == PathFindState.instance() || this.getCurrentState() == WalkTowardState.instance()) && (this.getPathFindBehavior2().isGoalLocation() || this.getPathFindBehavior2().isGoalSound())) {
                if (!IsoUtils.isSimilarDirection(this, (float)n2, (float)n3, this.getPathFindBehavior2().getTargetX(), this.getPathFindBehavior2().getTargetY(), 0.5f)) {
                    this.setTurnAlertedValues(n2, n3);
                    this.pathToSound(n2, n3, z);
                    this.setLastHeardSound(this.getPathTargetX(), this.getPathTargetY(), this.getPathTargetZ());
                    this.AllowRepathDelay = 120.0f;
                    this.timeSinceRespondToSound = 0.0f;
                }
                return;
            }
            if (this.timeSinceRespondToSound < 60.0f) {
                return;
            }
            if (!IsoUtils.isSimilarDirection(this, (float)n2, (float)n3, this.x + this.getForwardDirection().x, this.y + this.getForwardDirection().y, 0.5f)) {
                this.setTurnAlertedValues(n2, n3);
            }
            this.pathToSound(n2, n3, z);
            this.setLastHeardSound(this.getPathTargetX(), this.getPathTargetY(), this.getPathTargetZ());
            this.AllowRepathDelay = 120.0f;
            this.timeSinceRespondToSound = 0.0f;
        }
    }
    
    public void setTurnAlertedValues(final int n, final int n2) {
        final Vector2 vector2 = new Vector2(this.getX() - (n + 0.5f), this.getY() - (n2 + 0.5f));
        final float directionNeg = vector2.getDirectionNeg();
        float n3;
        if (directionNeg < 0.0f) {
            n3 = Math.abs(directionNeg);
        }
        else {
            n3 = new Float(6.283185307179586 - directionNeg);
        }
        double doubleValue = new Double(Math.toDegrees(n3));
        final Vector2 vector3 = new Vector2(IsoDirections.reverse(this.getDir()).ToVector().x, IsoDirections.reverse(this.getDir()).ToVector().y);
        vector3.normalize();
        final float directionNeg2 = vector3.getDirectionNeg();
        float abs;
        if (directionNeg2 < 0.0f) {
            abs = Math.abs(directionNeg2);
        }
        else {
            abs = 6.2831855f - directionNeg2;
        }
        double degrees = Math.toDegrees(abs);
        if ((int)degrees == 360) {
            degrees = 0.0;
        }
        if ((int)doubleValue == 360) {
            doubleValue = 0.0;
        }
        String s = "0";
        if (doubleValue > degrees) {
            final int n4 = (int)(doubleValue - degrees);
            if (n4 > 350 || n4 <= 35) {
                s = "45R";
            }
            if (n4 > 35 && n4 <= 80) {
                s = "90R";
            }
            if (n4 > 80 && n4 <= 125) {
                s = "135R";
            }
            if (n4 > 125 && n4 <= 170) {
                s = "180R";
            }
            if (n4 > 170 && n4 < 215) {
                s = "180L";
            }
            if (n4 >= 215 && n4 < 260) {
                s = "135L";
            }
            if (n4 >= 260 && n4 < 305) {
                s = "90L";
            }
            if (n4 >= 305 && n4 < 350) {
                s = "45L";
            }
        }
        else {
            final int n5 = (int)(degrees - doubleValue);
            if (n5 > 10 && n5 <= 55) {
                s = "45L";
            }
            if (n5 > 55 && n5 <= 100) {
                s = "90L";
            }
            if (n5 > 100 && n5 <= 145) {
                s = "135L";
            }
            if (n5 > 145 && n5 <= 190) {
                s = "180L";
            }
            if (n5 > 190 && n5 < 235) {
                s = "180R";
            }
            if (n5 >= 235 && n5 < 280) {
                s = "135R";
            }
            if (n5 >= 280 && n5 < 325) {
                s = "90R";
            }
            if (n5 >= 325 || n5 < 10) {
                s = "45R";
            }
        }
        this.setVariable("turnalertedvalue", s);
        ZombieTurnAlerted.instance().setParams(this, vector2.set(n + 0.5f - this.x, n2 + 0.5f - this.y).getDirection());
        this.alerted = true;
        this.networkAI.extraUpdate();
    }
    
    public void clearAggroList() {
        try {
            Arrays.fill(this.aggroList, null);
        }
        catch (Exception ex) {}
    }
    
    private void processAggroList() {
        try {
            for (int i = 0; i < this.aggroList.length; ++i) {
                if (this.aggroList[i] != null && this.aggroList[i].getAggro() <= 0.0f) {
                    this.aggroList[i] = null;
                    return;
                }
            }
        }
        catch (Exception ex) {}
    }
    
    public void addAggro(final IsoMovingObject isoMovingObject, final float n) {
        try {
            if (this.aggroList[0] == null) {
                this.aggroList[0] = new Aggro(isoMovingObject, n);
                return;
            }
            for (int i = 0; i < this.aggroList.length; ++i) {
                if (this.aggroList[i] != null && this.aggroList[i].obj == isoMovingObject) {
                    this.aggroList[i].addDamage(n);
                    return;
                }
            }
            for (int j = 0; j < this.aggroList.length; ++j) {
                if (this.aggroList[j] == null) {
                    this.aggroList[j] = new Aggro(isoMovingObject, n);
                    return;
                }
            }
        }
        catch (Exception ex) {}
    }
    
    public boolean isLeadAggro(final IsoMovingObject isoMovingObject) {
        try {
            if (this.aggroList[0] == null) {
                return false;
            }
            this.processAggroList();
            if (this.aggroList[0] == null) {
                return false;
            }
            IsoMovingObject isoMovingObject2 = this.aggroList[0].obj;
            float n = this.aggroList[0].getAggro();
            for (int i = 1; i < this.aggroList.length; ++i) {
                if (this.aggroList[i] != null) {
                    if (n >= 1.0f && this.aggroList[i].getAggro() >= 1.0f) {
                        return false;
                    }
                    if (this.aggroList[i] != null && n < this.aggroList[i].getAggro()) {
                        isoMovingObject2 = this.aggroList[i].obj;
                        n = this.aggroList[i].getAggro();
                    }
                }
            }
            return isoMovingObject == isoMovingObject2 && n == 1.0f;
        }
        catch (Exception ex) {
            return false;
        }
    }
    
    @Override
    public void spotted(final IsoMovingObject isoMovingObject, final boolean b) {
        if (GameClient.bClient && this.isRemoteZombie()) {
            if (this.getTarget() != null) {
                this.vectorToTarget.x = this.getTarget().getX();
                this.vectorToTarget.y = this.getTarget().getY();
                final Vector2 vectorToTarget = this.vectorToTarget;
                vectorToTarget.x -= this.getX();
                final Vector2 vectorToTarget2 = this.vectorToTarget;
                vectorToTarget2.y -= this.getY();
            }
            return;
        }
        if (this.getCurrentSquare() == null) {
            return;
        }
        if (isoMovingObject.getCurrentSquare() == null) {
            return;
        }
        if (this.getCurrentSquare().getProperties().Is(IsoFlagType.smoke) || this.isUseless()) {
            this.setTarget(null);
            this.spottedLast = null;
            return;
        }
        if (isoMovingObject instanceof IsoPlayer && ((IsoPlayer)isoMovingObject).isGhostMode()) {
            return;
        }
        final IsoGameCharacter isoGameCharacter = Type.tryCastTo(isoMovingObject, IsoGameCharacter.class);
        if (isoGameCharacter == null || isoGameCharacter.isDead()) {
            return;
        }
        if (this.getCurrentSquare() == null) {
            this.ensureOnTile();
        }
        if (isoMovingObject.getCurrentSquare() == null) {
            isoMovingObject.ensureOnTile();
        }
        float n = 200.0f;
        final int n2 = (isoMovingObject instanceof IsoPlayer && !GameServer.bServer) ? ((IsoPlayer)isoMovingObject).PlayerIndex : 0;
        float n3 = (isoMovingObject.getCurrentSquare().lighting[n2].lightInfo().r + isoMovingObject.getCurrentSquare().lighting[n2].lightInfo().g + isoMovingObject.getCurrentSquare().lighting[n2].lightInfo().b) / 3.0f;
        float ambientForPlayer = RenderSettings.getInstance().getAmbientForPlayer(n2);
        final float n4 = (this.getCurrentSquare().lighting[n2].lightInfo().r + this.getCurrentSquare().lighting[n2].lightInfo().g + this.getCurrentSquare().lighting[n2].lightInfo().b) / 3.0f;
        float n5 = n4 * n4 * n4;
        if (n3 > 1.0f) {
            n3 = 1.0f;
        }
        if (n3 < 0.0f) {
            n3 = 0.0f;
        }
        if (n5 > 1.0f) {
            n5 = 1.0f;
        }
        if (n5 < 0.0f) {
            n5 = 0.0f;
        }
        final float n6 = 1.0f - (n3 - n5);
        if (n3 < 0.2f) {
            n3 = 0.2f;
        }
        if (ambientForPlayer < 0.2f) {
            ambientForPlayer = 0.2f;
        }
        if (isoMovingObject.getCurrentSquare().getRoom() != this.getCurrentSquare().getRoom()) {
            n = 50.0f;
            if ((isoMovingObject.getCurrentSquare().getRoom() != null && this.getCurrentSquare().getRoom() == null) || (isoMovingObject.getCurrentSquare().getRoom() == null && this.getCurrentSquare().getRoom() != null)) {
                n = 20.0f;
                if (isoGameCharacter.isAiming() || isoGameCharacter.isSneaking()) {
                    if (n3 < 0.4f) {
                        n = 0.0f;
                    }
                    else {
                        n = 10.0f;
                    }
                }
                else if (isoMovingObject.getMovementLastFrame().getLength() <= 0.04f && n3 < 0.4f) {
                    n = 10.0f;
                }
            }
        }
        IsoZombie.tempo.x = isoMovingObject.getX();
        IsoZombie.tempo.y = isoMovingObject.getY();
        final Vector2 tempo = IsoZombie.tempo;
        tempo.x -= this.getX();
        final Vector2 tempo2 = IsoZombie.tempo;
        tempo2.y -= this.getY();
        if (isoMovingObject.getCurrentSquare().getZ() != this.current.getZ()) {
            int n7 = Math.abs(isoMovingObject.getCurrentSquare().getZ() - this.current.getZ()) * 5;
            ++n7;
            n /= n7;
        }
        float n8 = GameTime.getInstance().getViewDist();
        if (IsoZombie.tempo.getLength() > n8) {
            return;
        }
        if (GameServer.bServer) {
            this.bIndoorZombie = false;
        }
        if (IsoZombie.tempo.getLength() < n8) {
            n8 = IsoZombie.tempo.getLength();
        }
        float viewDistMax = n8 * 1.1f;
        if (viewDistMax > GameTime.getInstance().getViewDistMax()) {
            viewDistMax = GameTime.getInstance().getViewDistMax();
        }
        IsoZombie.tempo.normalize();
        final float dot = this.getLookVector(IsoZombie.tempo2).dot(IsoZombie.tempo);
        if (this.DistTo(isoMovingObject) > 20.0f) {
            n -= 10000.0f;
        }
        if (viewDistMax > 0.5) {
            if (dot < -0.4f) {
                n = 0.0f;
            }
            else if (dot < -0.2f) {
                n /= 8.0f;
            }
            else if (dot < -0.0f) {
                n /= 4.0f;
            }
            else if (dot < 0.2f) {
                n /= 2.0f;
            }
            else if (dot <= 0.4f) {
                n *= 2.0f;
            }
            else if (dot > 0.4f) {
                n *= 8.0f;
            }
            else if (dot > 0.6f) {
                n *= 16.0f;
            }
            else if (dot > 0.8f) {
                n *= 32.0f;
            }
        }
        if (n > 0.0f && this.target instanceof IsoPlayer) {
            final IsoPlayer isoPlayer = (IsoPlayer)this.target;
            if (!GameServer.bServer && isoPlayer.RemoteID == -1 && this.current.isCanSee(isoPlayer.PlayerIndex)) {
                ((IsoPlayer)this.target).targetedByZombie = true;
                ((IsoPlayer)this.target).lastTargeted = 0.0f;
            }
        }
        float n9 = n * n6;
        final int n10 = (int)isoMovingObject.getZ() - (int)this.getZ();
        if (n10 >= 1) {
            n9 /= n10 * 3;
        }
        final float clamp = PZMath.clamp(viewDistMax / GameTime.getInstance().getViewDist(), 0.0f, 1.0f);
        float n11 = n9 * (1.0f - clamp) * (1.0f - clamp) * (1.0f - clamp) * (1.0f + (1.0f - PZMath.clamp(viewDistMax / 10.0f, 0.0f, 1.0f)) * 10.0f);
        final float length = isoMovingObject.getMovementLastFrame().getLength();
        if (length == 0.0f && n3 <= 0.2f) {}
        if (isoGameCharacter == null) {
            return;
        }
        if (isoGameCharacter.getTorchStrength() > 0.0f) {
            n11 *= 3.0f;
        }
        if (length < 0.01f) {
            n11 *= 0.5f;
        }
        else if (isoGameCharacter.isSneaking()) {
            n11 *= 0.4f;
        }
        else if (isoGameCharacter.isAiming()) {
            n11 *= 0.75f;
        }
        else if (length < 0.06f) {
            n11 *= 0.8f;
        }
        else if (length >= 0.06f) {
            n11 *= 2.4f;
        }
        if (this.eatBodyTarget != null) {
            n11 *= 0.6f;
        }
        if (viewDistMax < 5.0f && ((!isoGameCharacter.isRunning() && !isoGameCharacter.isSneaking() && !isoGameCharacter.isAiming()) || isoGameCharacter.isRunning())) {
            n11 *= 3.0f;
        }
        if (this.spottedLast == isoMovingObject && this.TimeSinceSeenFlesh < 120.0f) {
            n11 = 1000.0f;
        }
        final float n12 = n11 * isoGameCharacter.getSneakSpotMod() * ambientForPlayer;
        if (this.target != isoMovingObject && this.target != null && IsoUtils.DistanceManhatten(this.getX(), this.getY(), isoMovingObject.getX(), isoMovingObject.getY()) > IsoUtils.DistanceManhatten(this.getX(), this.getY(), this.target.getX(), this.target.getY())) {
            return;
        }
        float n13 = n12 * 0.3f;
        if (b) {
            n13 = 1000000.0f;
        }
        if (this.BonusSpotTime > 0.0f) {
            n13 = 1000000.0f;
        }
        float n14 = n13 * 1.2f;
        if (SandboxOptions.instance.Lore.Sight.getValue() == 1) {
            n14 *= 2.5f;
        }
        if (SandboxOptions.instance.Lore.Sight.getValue() == 3) {
            n14 *= 0.45f;
        }
        if (this.inactive) {
            n14 *= 0.25f;
        }
        float n15 = n14 * 0.25f;
        if (isoMovingObject instanceof IsoPlayer && ((IsoPlayer)isoMovingObject).Traits.Inconspicuous.isSet()) {
            n15 *= 0.5f;
        }
        if (isoMovingObject instanceof IsoPlayer && ((IsoPlayer)isoMovingObject).Traits.Conspicuous.isSet()) {
            n15 *= 2.0f;
        }
        float n16 = n15 * 1.6f;
        IsoGridSquare isoGridSquare = null;
        if (this.getCurrentSquare() != isoMovingObject.getCurrentSquare() && isoMovingObject instanceof IsoPlayer && ((IsoPlayer)isoMovingObject).isSneaking()) {
            IsoGridSquare isoGridSquare2;
            if (Math.abs(this.getCurrentSquare().getX() - isoMovingObject.getCurrentSquare().getX()) > Math.abs(this.getCurrentSquare().getY() - isoMovingObject.getCurrentSquare().getY())) {
                if (this.getCurrentSquare().getX() - isoMovingObject.getCurrentSquare().getX() > 0) {
                    isoGridSquare2 = isoMovingObject.getCurrentSquare().nav[IsoDirections.E.index()];
                }
                else {
                    isoGridSquare2 = isoMovingObject.getCurrentSquare();
                    isoGridSquare = isoMovingObject.getCurrentSquare().nav[IsoDirections.W.index()];
                }
            }
            else if (this.getCurrentSquare().getY() - isoMovingObject.getCurrentSquare().getY() > 0) {
                isoGridSquare2 = isoMovingObject.getCurrentSquare().nav[IsoDirections.S.index()];
            }
            else {
                isoGridSquare2 = isoMovingObject.getCurrentSquare();
                isoGridSquare = isoMovingObject.getCurrentSquare().nav[IsoDirections.N.index()];
            }
            if (isoGridSquare2 != null && isoMovingObject instanceof IsoGameCharacter) {
                float n17 = ((IsoGameCharacter)isoMovingObject).checkIsNearWall();
                if (n17 == 1.0f && isoGridSquare != null) {
                    n17 = isoGridSquare.getGridSneakModifier(true);
                }
                if (n17 > 1.0f) {
                    final float distTo = isoMovingObject.DistTo(isoGridSquare2.x, isoGridSquare2.y);
                    if (distTo > 1.0f) {
                        n17 /= distTo;
                    }
                    n16 /= n17;
                }
            }
        }
        final float a = (float)Math.floor(n16);
        boolean b2 = false;
        final float n18 = (float)(1.0 - Math.pow(1.0f - Math.min(1.0f, Math.max(0.0f, Math.min(a, 400.0f) / 400.0f)), GameTime.instance.getMultiplier())) * 100.0f;
        if (Rand.Next(10000) / 100.0f < n18) {
            b2 = true;
        }
        if ((GameClient.bClient || GameServer.bServer) && !NetworkZombieManager.canSpotted(this) && isoMovingObject != this.target) {
            return;
        }
        if (!b2) {
            if (n18 > 20.0f && isoMovingObject instanceof IsoPlayer && viewDistMax < 15.0f) {
                ((IsoPlayer)isoMovingObject).bCouldBeSeenThisFrame = true;
            }
            if (!((IsoPlayer)isoMovingObject).isbCouldBeSeenThisFrame() && !((IsoPlayer)isoMovingObject).isbSeenThisFrame() && ((IsoPlayer)isoMovingObject).isSneaking() && ((IsoPlayer)isoMovingObject).isJustMoved() && Rand.Next((int)(1100.0f * GameTime.instance.getInvMultiplier())) == 0) {
                if (GameServer.bServer) {
                    GameServer.addXp((IsoPlayer)isoMovingObject, PerkFactory.Perks.Sneak, 1);
                }
                else {
                    ((IsoPlayer)isoMovingObject).getXp().AddXP(PerkFactory.Perks.Sneak, 1.0f);
                }
            }
            if (!((IsoPlayer)isoMovingObject).isbCouldBeSeenThisFrame() && !((IsoPlayer)isoMovingObject).isbSeenThisFrame() && ((IsoPlayer)isoMovingObject).isSneaking() && ((IsoPlayer)isoMovingObject).isJustMoved() && Rand.Next((int)(1100.0f * GameTime.instance.getInvMultiplier())) == 0) {
                if (GameServer.bServer) {
                    GameServer.addXp((IsoPlayer)isoMovingObject, PerkFactory.Perks.Lightfoot, 1);
                }
                else {
                    ((IsoPlayer)isoMovingObject).getXp().AddXP(PerkFactory.Perks.Lightfoot, 1.0f);
                }
            }
            return;
        }
        if (isoMovingObject instanceof IsoPlayer) {
            ((IsoPlayer)isoMovingObject).setbSeenThisFrame(true);
        }
        if (!b) {
            this.BonusSpotTime = 120.0f;
        }
        this.LastTargetSeenX = (int)isoMovingObject.getX();
        this.LastTargetSeenY = (int)isoMovingObject.getY();
        this.LastTargetSeenZ = (int)isoMovingObject.getZ();
        if (this.stateMachine.getCurrent() == StaggerBackState.instance()) {
            return;
        }
        if (this.target != isoMovingObject) {
            this.targetSeenTime = 0.0f;
            if (GameServer.bServer && !this.isRemoteZombie()) {
                this.addAggro(isoMovingObject, 1.0f);
            }
        }
        this.setTarget(isoMovingObject);
        this.vectorToTarget.x = isoMovingObject.getX();
        this.vectorToTarget.y = isoMovingObject.getY();
        final Vector2 vectorToTarget3 = this.vectorToTarget;
        vectorToTarget3.x -= this.getX();
        final Vector2 vectorToTarget4 = this.vectorToTarget;
        vectorToTarget4.y -= this.getY();
        final float length2 = this.vectorToTarget.getLength();
        if (!b) {
            this.TimeSinceSeenFlesh = 0.0f;
            this.targetSeenTime += GameTime.getInstance().getRealworldSecondsSinceLastUpdate();
        }
        if (this.target == this.spottedLast && this.getCurrentState() == LungeState.instance() && this.LungeTimer > 0.0f) {
            return;
        }
        if (this.target == this.spottedLast && this.getCurrentState() == AttackVehicleState.instance()) {
            return;
        }
        if ((int)this.getZ() == (int)this.target.getZ() && (length2 <= 3.5f || (this.target instanceof IsoGameCharacter && ((IsoGameCharacter)this.target).getVehicle() != null && length2 <= 4.0f)) && this.getStateEventDelayTimer() <= 0.0f && !PolygonalMap2.instance.lineClearCollide(this.getX(), this.getY(), isoMovingObject.x, isoMovingObject.y, (int)this.getZ(), isoMovingObject)) {
            this.setTarget(isoMovingObject);
            if (this.getCurrentState() == LungeState.instance()) {
                return;
            }
        }
        this.spottedLast = isoMovingObject;
        if (!this.Ghost && !this.getCurrentSquare().getProperties().Is(IsoFlagType.smoke)) {
            this.setTarget(isoMovingObject);
            if (this.AllowRepathDelay > 0.0f) {
                return;
            }
            if (this.target instanceof IsoGameCharacter && ((IsoGameCharacter)this.target).getVehicle() != null) {
                if ((this.getCurrentState() == PathFindState.instance() || this.getCurrentState() == WalkTowardState.instance()) && this.getPathFindBehavior2().getTargetChar() == this.target) {
                    return;
                }
                if (this.getCurrentState() == AttackVehicleState.instance()) {
                    return;
                }
                final BaseVehicle vehicle = ((IsoGameCharacter)this.target).getVehicle();
                if (Math.abs(vehicle.getCurrentSpeedKmHour()) > 0.1f && this.DistToSquared(vehicle) <= 16.0f) {
                    return;
                }
                this.pathToCharacter((IsoGameCharacter)this.target);
                this.AllowRepathDelay = 10.0f;
            }
            else {
                this.pathToCharacter(isoGameCharacter);
                if (Rand.Next(5) == 0) {
                    this.spotSoundDelay = 200;
                }
                this.AllowRepathDelay = 480.0f;
            }
        }
    }
    
    @Override
    public void Move(final Vector2 vector2) {
        if (GameClient.bClient && this.authOwner == null) {
            return;
        }
        this.nx += vector2.x * GameTime.instance.getMultiplier();
        this.ny += vector2.y * GameTime.instance.getMultiplier();
        this.movex = vector2.x;
        this.movey = vector2.y;
    }
    
    @Override
    public void MoveUnmodded(final Vector2 vector2) {
        if (this.speedType == 1 && (this.isCurrentState(LungeState.instance()) || this.isCurrentState(LungeNetworkState.instance()) || this.isCurrentState(AttackState.instance()) || this.isCurrentState(AttackNetworkState.instance()) || this.isCurrentState(StaggerBackState.instance()) || this.isCurrentState(ZombieHitReactionState.instance())) && this.target instanceof IsoGameCharacter) {
            final float n = this.target.nx - this.x;
            final float n2 = this.target.ny - this.y;
            final float max = Math.max(0.0f, (float)Math.sqrt(n * n + n2 * n2) - (this.getWidth() + this.target.getWidth() - 0.1f));
            if (vector2.getLength() > max) {
                vector2.setLength(max);
            }
        }
        if (this.isRemoteZombie()) {
            final float distanceTo = IsoUtils.DistanceTo(this.realx, this.realy, this.networkAI.targetX, this.networkAI.targetY);
            if (distanceTo > 1.0f) {
                final Vector2 vector3 = new Vector2(this.realx - this.x, this.realy - this.y);
                vector3.normalize();
                final float n3 = 0.5f + IsoUtils.smoothstep(0.5f, 1.5f, IsoUtils.DistanceTo(this.x, this.y, this.networkAI.targetX, this.networkAI.targetY) / distanceTo);
                final float length = vector2.getLength();
                vector2.normalize();
                PZMath.lerp(vector2, vector2, vector3, 0.5f);
                vector2.setLength(length * n3);
            }
        }
        super.MoveUnmodded(vector2);
    }
    
    public boolean canBeDeletedUnnoticed(final float n) {
        if (!GameClient.bClient) {
            return false;
        }
        float n2 = Float.POSITIVE_INFINITY;
        final ArrayList<IsoPlayer> players = GameClient.instance.getPlayers();
        for (int i = 0; i < players.size(); ++i) {
            final IsoPlayer isoPlayer = players.get(i);
            if (isoPlayer.getDotWithForwardDirection(this.getX(), this.getY()) > -(LightingJNI.calculateVisionCone(isoPlayer) + 0.2f)) {
                return false;
            }
            final float distanceToSquared = IsoUtils.DistanceToSquared(this.x, this.y, isoPlayer.x, isoPlayer.y);
            if (distanceToSquared < n2) {
                n2 = distanceToSquared;
            }
        }
        return n2 > n * n;
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
        if (!GameServer.bServer && !this.getFMODParameters().parameterList.contains(this.parameterCharacterMovementSpeed)) {
            this.getFMODParameters().add(this.parameterCharacterMovementSpeed);
            this.getFMODParameters().add(this.parameterFootstepMaterial);
            this.getFMODParameters().add(this.parameterFootstepMaterial2);
            this.getFMODParameters().add(this.parameterShoeType);
        }
        this.parameterCharacterMovementSpeed.setMovementType(movementType);
        this.DoFootstepSound(n);
    }
    
    @Override
    public void DoFootstepSound(final float footstepVolume) {
        if (GameServer.bServer) {
            return;
        }
        if (footstepVolume <= 0.0f) {
            return;
        }
        if (this.getCurrentSquare() == null) {
            return;
        }
        if (GameClient.bClient && this.authOwner == null) {
            if (this.def != null && this.sprite != null && this.sprite.CurrentAnim != null && (this.sprite.CurrentAnim.name.contains("Run") || this.sprite.CurrentAnim.name.contains("Walk"))) {
                final int stepFrameLast = (int)this.def.Frame;
                boolean b;
                if (stepFrameLast >= 0 && stepFrameLast < 5) {
                    b = (this.stepFrameLast < 0 || this.stepFrameLast > 5);
                }
                else {
                    b = (this.stepFrameLast < 5);
                }
                if (b) {
                    for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
                        final IsoPlayer isoPlayer = IsoPlayer.players[i];
                        if (isoPlayer != null && isoPlayer.DistToSquared(this) < 225.0f) {
                            ZombieFootstepManager.instance.addCharacter(this);
                            break;
                        }
                    }
                }
                this.stepFrameLast = stepFrameLast;
            }
            else {
                this.stepFrameLast = -1;
            }
            return;
        }
        if (SoundManager.instance.isListenerInRange(this.getX(), this.getY(), 15.0f)) {
            this.footstepVolume = footstepVolume;
            ZombieFootstepManager.instance.addCharacter(this);
        }
    }
    
    @Override
    public void preupdate() {
        if (GameServer.bServer && this.thumpSent) {
            this.thumpFlag = 0;
            this.thumpSent = false;
            this.mpIdleSound = false;
        }
        this.FollowCount = 0;
        if (GameClient.bClient) {
            this.networkAI.updateHitVehicle();
            if (!this.isLocal()) {
                this.networkAI.preupdate();
            }
            else if (this.isKnockedDown() && !this.isOnFloor()) {
                final HitReactionNetworkAI hitReactionNetworkAI = this.getHitReactionNetworkAI();
                if (hitReactionNetworkAI.isSetup() && !hitReactionNetworkAI.isStarted()) {
                    hitReactionNetworkAI.start();
                    if (Core.bDebug) {
                        DebugLog.log(DebugType.Multiplayer, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, hitReactionNetworkAI.getDescription()));
                    }
                }
            }
        }
        super.preupdate();
    }
    
    @Override
    public void postupdate() {
        s_performance.postUpdate.invokeAndMeasure(this, IsoZombie::postUpdateInternal);
    }
    
    private void postUpdateInternal() {
        if (this.target instanceof IsoPlayer) {
            final Stats stats = ((IsoPlayer)this.target).getStats();
            ++stats.NumChasingZombies;
        }
        super.postupdate();
        if (this.current == null && (!GameClient.bClient || this.authOwner != null)) {
            this.removeFromWorld();
            this.removeFromSquare();
        }
        if (!GameServer.bServer) {
            final IsoPlayer reanimatedPlayer = this.getReanimatedPlayer();
            if (reanimatedPlayer != null) {
                reanimatedPlayer.setX(this.getX());
                reanimatedPlayer.setY(this.getY());
                reanimatedPlayer.setZ(this.getZ());
                reanimatedPlayer.setDir(this.getDir());
                reanimatedPlayer.setForwardDirection(this.getForwardDirection());
                final AnimationPlayer animationPlayer = this.getAnimationPlayer();
                final AnimationPlayer animationPlayer2 = reanimatedPlayer.getAnimationPlayer();
                if (animationPlayer != null && animationPlayer.isReady() && animationPlayer2 != null && animationPlayer2.isReady()) {
                    animationPlayer2.setTargetAngle(animationPlayer.getAngle());
                    animationPlayer2.setAngleToTarget();
                }
                reanimatedPlayer.setCurrent(this.getCell().getGridSquare((int)reanimatedPlayer.x, (int)reanimatedPlayer.y, (int)reanimatedPlayer.z));
                reanimatedPlayer.updateLightInfo();
                if (reanimatedPlayer.soundListener != null) {
                    reanimatedPlayer.soundListener.setPos(reanimatedPlayer.getX(), reanimatedPlayer.getY(), reanimatedPlayer.getZ());
                    reanimatedPlayer.soundListener.tick();
                }
                final IsoPlayer instance = IsoPlayer.getInstance();
                IsoPlayer.setInstance(reanimatedPlayer);
                reanimatedPlayer.updateLOS();
                IsoPlayer.setInstance(instance);
                if (GameClient.bClient && this.authOwner == null && this.networkUpdate.Check()) {
                    GameClient.instance.sendPlayer(reanimatedPlayer);
                }
                reanimatedPlayer.dirtyRecalcGridStackTime = 2.0f;
            }
        }
        if (this.targetSeenTime > 0.0f && !this.isTargetVisible()) {
            this.targetSeenTime = 0.0f;
        }
    }
    
    @Override
    public boolean isSolidForSeparate() {
        return this.getCurrentState() != FakeDeadZombieState.instance() && this.getCurrentState() != ZombieFallDownState.instance() && this.getCurrentState() != ZombieOnGroundState.instance() && this.getCurrentState() != ZombieGetUpState.instance() && (this.getCurrentState() != ZombieHitReactionState.instance() || this.speedType == 1) && !this.isSitAgainstWall() && super.isSolidForSeparate();
    }
    
    @Override
    public boolean isPushableForSeparate() {
        return this.getCurrentState() != ThumpState.instance() && this.getCurrentState() != AttackState.instance() && this.getCurrentState() != AttackVehicleState.instance() && this.getCurrentState() != ZombieEatBodyState.instance() && this.getCurrentState() != ZombieFaceTargetState.instance() && !this.isSitAgainstWall() && super.isPushableForSeparate();
    }
    
    @Override
    public boolean isPushedByForSeparate(final IsoMovingObject isoMovingObject) {
        return (!(isoMovingObject instanceof IsoZombie) || ((IsoZombie)isoMovingObject).getCurrentState() != ZombieHitReactionState.instance() || ((IsoZombie)isoMovingObject).collideWhileHit) && (this.getCurrentState() != ZombieHitReactionState.instance() || this.collideWhileHit) && (!GameClient.bClient || !(isoMovingObject instanceof IsoZombie) || NetworkZombieSimulator.getInstance().isZombieSimulated(this.getOnlineID())) && super.isPushedByForSeparate(isoMovingObject);
    }
    
    @Override
    public void update() {
        s_performance.update.invokeAndMeasure(this, IsoZombie::updateInternal);
    }
    
    private void updateInternal() {
        if (GameClient.bClient && !this.isRemoteZombie()) {
            ZombieCountOptimiser.incrementZombie(this);
            MPStatistics.clientZombieUpdated();
        }
        else if (GameServer.bServer) {
            MPStatistics.serverZombieUpdated();
        }
        if (SandboxOptions.instance.Lore.ActiveOnly.getValue() > 1) {
            if ((SandboxOptions.instance.Lore.ActiveOnly.getValue() == 2 && (GameTime.instance.getHour() >= 20 || GameTime.instance.getHour() <= 8)) || (SandboxOptions.instance.Lore.ActiveOnly.getValue() == 3 && GameTime.instance.getHour() > 8 && GameTime.instance.getHour() < 20)) {
                this.makeInactive(false);
            }
            else {
                this.makeInactive(true);
            }
        }
        this.updateVocalProperties();
        if (this.bCrawling) {
            if (this.actionContext.getGroup() != ActionGroup.getActionGroup("zombie-crawler")) {
                this.advancedAnimator.OnAnimDataChanged(false);
                this.initializeStates();
                this.actionContext.setGroup(ActionGroup.getActionGroup("zombie-crawler"));
            }
        }
        else if (this.actionContext.getGroup() != ActionGroup.getActionGroup("zombie")) {
            this.advancedAnimator.OnAnimDataChanged(false);
            this.initializeStates();
            this.actionContext.setGroup(ActionGroup.getActionGroup("zombie"));
        }
        if (this.getThumpTimer() > 0) {
            --this.thumpTimer;
        }
        final BaseVehicle nearVehicle = this.getNearVehicle();
        if (nearVehicle != null && this.target == null && nearVehicle.hasLightbar() && nearVehicle.lightbarSirenMode.get() > 0) {
            final VehiclePart useablePart = nearVehicle.getUseablePart(this, false);
            if (useablePart != null && useablePart.getSquare().DistTo(this) < 0.7f) {
                this.setThumpTarget(nearVehicle);
            }
        }
        this.doDeferredMovement();
        this.updateEmitter();
        if (this.spotSoundDelay > 0) {
            --this.spotSoundDelay;
        }
        if (GameClient.bClient && this.authOwner == null) {
            if (this.lastRemoteUpdate > 800 && (this.legsSprite.CurrentAnim.name.equals("ZombieDeath") || this.legsSprite.CurrentAnim.name.equals("ZombieStaggerBack") || this.legsSprite.CurrentAnim.name.equals("ZombieGetUp"))) {
                DebugLog.log(DebugType.Zombie, invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, this.OnlineID));
                VirtualZombieManager.instance.removeZombieFromWorld(this);
                return;
            }
            if (GameClient.bFastForward) {
                VirtualZombieManager.instance.removeZombieFromWorld(this);
                return;
            }
        }
        if (GameClient.bClient && this.authOwner == null && this.lastRemoteUpdate < 2000 && this.lastRemoteUpdate + 1000 / PerformanceSettings.getLockFPS() > 2000) {
            DebugLog.log(DebugType.Zombie, invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, this.OnlineID));
        }
        this.lastRemoteUpdate += (short)(1000 / PerformanceSettings.getLockFPS());
        if (GameClient.bClient && this.authOwner == null && (!this.bRemote || this.lastRemoteUpdate > 5000)) {
            DebugLog.log(DebugType.Zombie, invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, this.OnlineID));
            DebugLog.log(invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, this.OnlineID));
            VirtualZombieManager.instance.removeZombieFromWorld(this);
            return;
        }
        this.sprite = this.legsSprite;
        if (this.sprite == null) {
            return;
        }
        this.updateCharacterTextureAnimTime();
        if (GameServer.bServer && this.bIndoorZombie) {
            super.update();
            if (GameServer.bServer && GameServer.doSendZombies()) {
                GameServer.sendZombie(this);
            }
            return;
        }
        this.BonusSpotTime = PZMath.clamp(this.BonusSpotTime - GameTime.instance.getMultiplier(), 0.0f, Float.MAX_VALUE);
        this.TimeSinceSeenFlesh = PZMath.clamp(this.TimeSinceSeenFlesh + GameTime.instance.getMultiplier(), 0.0f, Float.MAX_VALUE);
        if (this.getStateMachine().getCurrent() == ClimbThroughWindowState.instance() || this.getStateMachine().getCurrent() == ClimbOverFenceState.instance() || this.getStateMachine().getCurrent() == CrawlingZombieTurnState.instance() || this.getStateMachine().getCurrent() == ZombieHitReactionState.instance() || this.getStateMachine().getCurrent() == ZombieFallDownState.instance()) {
            super.update();
            if (GameServer.bServer && GameServer.doSendZombies()) {
                GameServer.sendZombie(this);
            }
            return;
        }
        this.setCollidable(true);
        LuaEventManager.triggerEvent("OnZombieUpdate", this);
        if (Core.bLastStand && this.getStateMachine().getCurrent() != ThumpState.instance() && this.getStateMachine().getCurrent() != AttackState.instance() && this.TimeSinceSeenFlesh > 120.0f && Rand.Next(36000) == 0) {
            IsoGameCharacter isoGameCharacter = null;
            float distTo = 1000000.0f;
            for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
                if (IsoPlayer.players[i] != null && IsoPlayer.players[i].DistTo(this) < distTo && !IsoPlayer.players[i].isDead()) {
                    distTo = IsoPlayer.players[i].DistTo(this);
                    isoGameCharacter = IsoPlayer.players[i];
                }
            }
            if (isoGameCharacter != null) {
                this.AllowRepathDelay = -1.0f;
                this.pathToCharacter(isoGameCharacter);
            }
            return;
        }
        if (GameServer.bServer) {
            this.vehicle4testCollision = null;
        }
        else if (GameClient.bClient) {
            if (this.vehicle4testCollision != null && this.vehicle4testCollision.updateHitByVehicle(this)) {
                super.update();
                this.vehicle4testCollision = null;
                return;
            }
        }
        else {
            if (this.Health > 0.0f && this.vehicle4testCollision != null && this.testCollideWithVehicles(this.vehicle4testCollision)) {
                this.vehicle4testCollision = null;
                return;
            }
            if (this.Health > 0.0f && this.vehicle4testCollision != null && this.isCollidedWithVehicle()) {
                this.vehicle4testCollision.hitCharacter(this);
                super.update();
                return;
            }
        }
        this.vehicle4testCollision = null;
        if (this.BonusSpotTime > 0.0f && this.spottedLast != null && !((IsoGameCharacter)this.spottedLast).isDead()) {
            this.spotted(this.spottedLast, true);
        }
        if (GameServer.bServer && this.getStateMachine().getCurrent() == BurntToDeath.instance()) {
            DebugLog.log(DebugType.Zombie, invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, this.OnlineID));
        }
        super.update();
        if (VirtualZombieManager.instance.isReused(this)) {
            DebugLog.log(DebugType.Zombie, invokedynamic(makeConcatWithConstants:(Lzombie/characters/IsoZombie;)Ljava/lang/String;, this));
            return;
        }
        if (GameServer.bServer && (GameServer.doSendZombies() || this.getStateMachine().getCurrent() == StaggerBackState.instance() || this.getStateMachine().getCurrent() == BurntToDeath.instance())) {
            GameServer.sendZombie(this);
        }
        if (this.getStateMachine().getCurrent() == ClimbThroughWindowState.instance() || this.getStateMachine().getCurrent() == ClimbOverFenceState.instance() || this.getStateMachine().getCurrent() == CrawlingZombieTurnState.instance()) {
            return;
        }
        this.ensureOnTile();
        final State current = this.stateMachine.getCurrent();
        if (current == StaggerBackState.instance() || current == BurntToDeath.instance() || current == FakeDeadZombieState.instance() || current == ZombieFallDownState.instance() || current == ZombieOnGroundState.instance() || current == ZombieHitReactionState.instance() || current == ZombieGetUpState.instance()) {
            return;
        }
        if (GameServer.bServer && this.OnlineID == -1) {
            this.OnlineID = ServerMap.instance.getUniqueZombieId();
        }
        else if (current == PathFindState.instance() && this.finder.progress == AStarPathFinder.PathFindProgress.notyetfound) {
            if (this.bCrawling) {
                this.PlayAnim("ZombieCrawl");
                this.def.AnimFrameIncrease = 0.0f;
            }
            else {
                this.PlayAnim("ZombieIdle");
                this.def.AnimFrameIncrease = 0.08f + Rand.Next(1000) / 8000.0f;
                final IsoSpriteInstance def = this.def;
                def.AnimFrameIncrease *= 0.5f;
            }
        }
        else if (current != AttackState.instance() && current != AttackVehicleState.instance() && (this.nx != this.x || this.ny != this.y)) {
            if (this.walkVariantUse == null || (current != LungeState.instance() && current != LungeNetworkState.instance())) {
                this.walkVariantUse = this.walkVariant;
            }
            if (this.bCrawling) {
                this.walkVariantUse = "ZombieCrawl";
            }
            if (current != ZombieIdleState.instance() && current != StaggerBackState.instance() && current != ThumpState.instance() && current != FakeDeadZombieState.instance()) {
                if (this.bRunning) {
                    this.PlayAnim("Run");
                    this.def.setFrameSpeedPerFrame(0.33f);
                }
                else {
                    this.PlayAnim(this.walkVariantUse);
                    this.def.setFrameSpeedPerFrame(0.26f);
                    final IsoSpriteInstance def2 = this.def;
                    def2.AnimFrameIncrease *= this.speedMod;
                }
                this.setShootable(true);
            }
        }
        this.shootable = true;
        this.solid = true;
        this.tryThump(null);
        this.damageSheetRope();
        this.AllowRepathDelay = PZMath.clamp(this.AllowRepathDelay - GameTime.instance.getMultiplier(), 0.0f, Float.MAX_VALUE);
        if (this.TimeSinceSeenFlesh > this.getSandboxMemoryDuration() && this.target != null) {
            this.setTarget(null);
        }
        if (this.target instanceof IsoGameCharacter && ((IsoGameCharacter)this.target).ReanimatedCorpse != null) {
            this.setTarget(null);
        }
        if (this.target != null) {
            this.vectorToTarget.x = this.target.getX();
            this.vectorToTarget.y = this.target.getY();
            final Vector2 vectorToTarget = this.vectorToTarget;
            vectorToTarget.x -= this.getX();
            final Vector2 vectorToTarget2 = this.vectorToTarget;
            vectorToTarget2.y -= this.getY();
            this.updateZombieTripping();
        }
        if (IsoPlayer.getInstance() != null) {
            this.nextIdleSound -= GameTime.getInstance().getMultiplier() / 1.6f;
            if (this.nextIdleSound < 0.0f && (this.getCurrentState() == WalkTowardState.instance() || this.getCurrentState() == PathFindState.instance())) {
                this.nextIdleSound = (float)Rand.Next(300, 600);
                if (SoundManager.instance.isListenerInRange(this.getX(), this.getY(), 20.0f) && !this.emitter.isPlaying(this.isFemale() ? "FemaleZombieIdle" : "MaleZombieIdle")) {
                    ZombieVocalsManager.instance.addCharacter(this);
                }
            }
        }
        if (GameServer.bServer && (this.getCurrentState() == WalkTowardState.instance() || this.getCurrentState() == PathFindState.instance()) && Rand.Next(Rand.AdjustForFramerate(360)) == 0) {
            this.mpIdleSound = true;
        }
        if (this.getCurrentState() != PathFindState.instance() && this.getCurrentState() != WalkTowardState.instance() && this.getCurrentState() != ClimbThroughWindowState.instance()) {
            this.setLastHeardSound(-1, -1, -1);
        }
        if (this.TimeSinceSeenFlesh > 240.0f && this.timeSinceRespondToSound > 5.0f) {
            this.RespondToSound();
        }
        this.timeSinceRespondToSound += GameTime.getInstance().getMultiplier() / 1.6f;
        this.separate();
        this.updateSearchForCorpse();
        if (this.TimeSinceSeenFlesh > 2000.0f && this.timeSinceRespondToSound > 2000.0f) {
            ZombieGroupManager.instance.update(this);
        }
    }
    
    @Override
    protected void calculateStats() {
    }
    
    private void updateZombieTripping() {
        if (this.speedType == 1 && StringUtils.isNullOrEmpty(this.getBumpType()) && this.target != null && Rand.NextBool(Rand.AdjustForFramerate(750))) {
            this.setBumpType("trippingFromSprint");
        }
    }
    
    private void updateVocalProperties() {
        if (GameServer.bServer) {
            return;
        }
        final boolean listenerInRange = SoundManager.instance.isListenerInRange(this.getX(), this.getY(), 20.0f);
        if (this.vocalEvent == 0L && !this.isDead() && !this.isFakeDead() && listenerInRange) {
            final String s = this.isFemale() ? "FemaleZombieCombined" : "MaleZombieCombined";
            if (!this.getFMODParameters().parameterList.contains(this.parameterZombieState)) {
                this.parameterZombieState.update();
                this.getFMODParameters().add(this.parameterZombieState);
                this.parameterCharacterInside.update();
                this.getFMODParameters().add(this.parameterCharacterInside);
                this.parameterPlayerDistance.update();
                this.getFMODParameters().add(this.parameterPlayerDistance);
            }
            this.vocalEvent = this.getEmitter().playVocals(s);
        }
        if (this.vocalEvent != 0L && !this.isDead() && this.isFakeDead() && this.getEmitter().isPlaying(this.vocalEvent)) {
            this.getEmitter().stopSound(this.vocalEvent);
            this.vocalEvent = 0L;
        }
    }
    
    public void setVehicleHitLocation(final BaseVehicle baseVehicle) {
        if (!this.getFMODParameters().parameterList.contains(this.parameterVehicleHitLocation)) {
            this.getFMODParameters().add(this.parameterVehicleHitLocation);
        }
        this.parameterVehicleHitLocation.setLocation(ParameterVehicleHitLocation.calculateLocation(baseVehicle, this.getX(), this.getY(), this.getZ()));
    }
    
    private void updateSearchForCorpse() {
        if (this.bCrawling || this.target != null || this.eatBodyTarget != null) {
            this.checkForCorpseTimer = 10000.0f;
            this.bodyToEat = null;
            return;
        }
        if (this.bodyToEat != null) {
            if (this.bodyToEat.getStaticMovingObjectIndex() == -1) {
                this.bodyToEat = null;
            }
            else if (!this.isEatingOther(this.bodyToEat) && this.bodyToEat.getEatingZombies().size() >= 3) {
                this.bodyToEat = null;
            }
        }
        if (this.bodyToEat == null) {
            this.checkForCorpseTimer -= GameTime.getInstance().getMultiplier() / 1.6f;
            if (this.checkForCorpseTimer <= 0.0f) {
                this.checkForCorpseTimer = 10000.0f;
                IsoZombie.tempBodies.clear();
                for (int i = -10; i < 10; ++i) {
                    for (int j = -10; j < 10; ++j) {
                        final IsoGridSquare gridSquare = this.getCell().getGridSquare(this.getX() + i, this.getY() + j, this.getZ());
                        if (gridSquare != null) {
                            final IsoDeadBody deadBody = gridSquare.getDeadBody();
                            if (deadBody != null && !deadBody.isSkeleton() && !deadBody.isZombie()) {
                                if (deadBody.getEatingZombies().size() < 3) {
                                    if (!PolygonalMap2.instance.lineClearCollide(this.getX(), this.getY(), deadBody.x, deadBody.y, (int)this.getZ(), null, false, true)) {
                                        IsoZombie.tempBodies.add(deadBody);
                                    }
                                }
                            }
                        }
                    }
                }
                if (!IsoZombie.tempBodies.isEmpty()) {
                    this.bodyToEat = PZArrayUtil.pickRandom(IsoZombie.tempBodies);
                    IsoZombie.tempBodies.clear();
                }
            }
        }
        if (this.bodyToEat == null || !this.isCurrentState(ZombieIdleState.instance())) {
            return;
        }
        if (this.DistToSquared(this.bodyToEat) > 1.0f) {
            final Vector2 set = IsoZombie.tempo.set(this.x - this.bodyToEat.x, this.y - this.bodyToEat.y);
            set.setLength(0.5f);
            this.pathToLocationF(this.bodyToEat.getX() + set.x, this.bodyToEat.getY() + set.y, this.bodyToEat.getZ());
        }
    }
    
    private void damageSheetRope() {
        if (Rand.Next(30) == 0 && this.current != null && (this.current.Is(IsoFlagType.climbSheetN) || this.current.Is(IsoFlagType.climbSheetE) || this.current.Is(IsoFlagType.climbSheetS) || this.current.Is(IsoFlagType.climbSheetW))) {
            final IsoObject sheetRope = this.current.getSheetRope();
            if (sheetRope != null) {
                final IsoObject isoObject = sheetRope;
                isoObject.sheetRopeHealth -= Rand.Next(5, 15);
                if (sheetRope.sheetRopeHealth < 40.0f) {
                    this.current.damageSpriteSheetRopeFromBottom(null, this.current.Is(IsoFlagType.climbSheetN) || this.current.Is(IsoFlagType.climbSheetS));
                    this.current.RecalcProperties();
                }
                if (sheetRope.sheetRopeHealth <= 0.0f) {
                    this.current.removeSheetRopeFromBottom(null, this.current.Is(IsoFlagType.climbSheetN) || this.current.Is(IsoFlagType.climbSheetS));
                }
            }
        }
    }
    
    public void getZombieWalkTowardSpeed(final float n, final float length, final Vector2 vector2) {
        float n2 = length / 24.0f;
        if (n2 < 1.0f) {
            n2 = 1.0f;
        }
        if (n2 > 1.3f) {
            n2 = 1.3f;
        }
        vector2.setLength((n * this.getSpeedMod() + 0.006f) * n2);
        if ((SandboxOptions.instance.Lore.Speed.getValue() == 1 && !this.inactive) || this.speedType == 1) {
            vector2.setLength(0.08f);
            this.bRunning = true;
        }
        if (vector2.getLength() > length) {
            vector2.setLength(length);
        }
    }
    
    public void getZombieLungeSpeed() {
        this.bRunning = ((SandboxOptions.instance.Lore.Speed.getValue() == 1 && !this.inactive) || this.speedType == 1);
    }
    
    public boolean tryThump(final IsoGridSquare isoGridSquare) {
        if (this.Ghost) {
            return false;
        }
        if (this.bCrawling) {
            return false;
        }
        if (!this.isCurrentState(PathFindState.instance()) && !this.isCurrentState(LungeState.instance()) && !this.isCurrentState(LungeNetworkState.instance()) && !this.isCurrentState(WalkTowardState.instance()) && !this.isCurrentState(WalkTowardNetworkState.instance())) {
            return false;
        }
        IsoGridSquare feelerTile;
        if (isoGridSquare != null) {
            feelerTile = isoGridSquare;
        }
        else {
            feelerTile = this.getFeelerTile(this.getFeelersize());
        }
        if (feelerTile == null || this.current == null) {
            return false;
        }
        IsoObject testCollideSpecialObjects = this.current.testCollideSpecialObjects(feelerTile);
        final IsoDoor isoDoor = Type.tryCastTo(testCollideSpecialObjects, IsoDoor.class);
        final IsoThumpable isoThumpable = Type.tryCastTo(testCollideSpecialObjects, IsoThumpable.class);
        final IsoWindow isoWindow = Type.tryCastTo(testCollideSpecialObjects, IsoWindow.class);
        if (isoWindow != null && isoWindow.canClimbThrough(this)) {
            if (!this.isFacingObject(isoWindow, 0.8f)) {
                return false;
            }
            this.climbThroughWindow(isoWindow);
            return true;
        }
        else {
            if (isoThumpable != null && isoThumpable.canClimbThrough(this)) {
                this.climbThroughWindow(isoThumpable);
                return true;
            }
            if ((isoThumpable != null && isoThumpable.getThumpableFor(this) != null) || (isoWindow != null && isoWindow.getThumpableFor(this) != null) || (isoDoor != null && isoDoor.getThumpableFor(this) != null)) {
                final int n = feelerTile.getX() - this.current.getX();
                final int n2 = feelerTile.getY() - this.current.getY();
                IsoDirections isoDirections = IsoDirections.N;
                if (n < 0 && Math.abs(n) > Math.abs(n2)) {
                    isoDirections = IsoDirections.S;
                }
                if (n < 0 && Math.abs(n) <= Math.abs(n2)) {
                    isoDirections = IsoDirections.SW;
                }
                if (n > 0 && Math.abs(n) > Math.abs(n2)) {
                    isoDirections = IsoDirections.W;
                }
                if (n > 0 && Math.abs(n) <= Math.abs(n2)) {
                    isoDirections = IsoDirections.SE;
                }
                if (n2 < 0 && Math.abs(n) < Math.abs(n2)) {
                    isoDirections = IsoDirections.N;
                }
                if (n2 < 0 && Math.abs(n) >= Math.abs(n2)) {
                    isoDirections = IsoDirections.NW;
                }
                if (n2 > 0 && Math.abs(n) < Math.abs(n2)) {
                    isoDirections = IsoDirections.E;
                }
                if (n2 > 0 && Math.abs(n) >= Math.abs(n2)) {
                    isoDirections = IsoDirections.NE;
                }
                if (this.getDir() == isoDirections) {
                    final boolean b = this.getPathFindBehavior2().isGoalSound() && (this.isCurrentState(PathFindState.instance()) || this.isCurrentState(WalkTowardState.instance()) || this.isCurrentState(WalkTowardNetworkState.instance()));
                    if (SandboxOptions.instance.Lore.ThumpNoChasing.getValue() || this.target != null || b) {
                        if (isoWindow != null && isoWindow.getThumpableFor(this) != null) {
                            testCollideSpecialObjects = (IsoObject)isoWindow.getThumpableFor(this);
                        }
                        this.setThumpTarget(testCollideSpecialObjects);
                        this.setPath2(null);
                    }
                }
                return true;
            }
            if (testCollideSpecialObjects != null && IsoWindowFrame.isWindowFrame(testCollideSpecialObjects)) {
                this.climbThroughWindowFrame(testCollideSpecialObjects);
                return true;
            }
            return false;
        }
    }
    
    public void Wander() {
        GameServer.sendZombie(this);
        this.changeState(ZombieIdleState.instance());
    }
    
    public void DoZombieInventory() {
        this.DoZombieInventory(false);
    }
    
    public void DoCorpseInventory() {
        this.DoZombieInventory(true);
    }
    
    private void DoZombieInventory(final boolean b) {
        if (this.isReanimatedPlayer() || this.wasFakeDead()) {
            return;
        }
        if (GameServer.bServer && !b) {
            return;
        }
        this.getInventory().removeAllItems();
        this.getInventory().setSourceGrid(this.getCurrentSquare());
        this.wornItems.setFromItemVisuals(this.itemVisuals);
        this.wornItems.addItemsToItemContainer(this.getInventory());
        for (int i = 0; i < this.attachedItems.size(); ++i) {
            final InventoryItem item = this.attachedItems.get(i).getItem();
            if (!this.getInventory().contains(item)) {
                item.setContainer(this.getInventory());
                this.getInventory().getItems().add(item);
            }
        }
        final IsoBuilding currentBuilding = this.getCurrentBuilding();
        if (currentBuilding != null && currentBuilding.getDef() != null && currentBuilding.getDef().getKeyId() != -1 && Rand.Next(4) == 0) {
            this.inventory.AddItem(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Rand.Next(5) + 1)).setKeyId(currentBuilding.getDef().getKeyId());
        }
        if (this.itemsToSpawnAtDeath != null && !this.itemsToSpawnAtDeath.isEmpty()) {
            for (int j = 0; j < this.itemsToSpawnAtDeath.size(); ++j) {
                this.inventory.AddItem(this.itemsToSpawnAtDeath.get(j));
            }
            this.itemsToSpawnAtDeath.clear();
        }
        if (Core.bDebug) {
            DebugLog.log(DebugType.Death, String.format("DoZombieInventory %s(%d): %s", this.getClass().getSimpleName(), this.getOnlineID(), this.getInventory().getItems().size()));
        }
    }
    
    public void DoZombieStats() {
        if (SandboxOptions.instance.Lore.Cognition.getValue() == 1) {
            this.cognition = 1;
        }
        if (SandboxOptions.instance.Lore.Cognition.getValue() == 4) {
            this.cognition = Rand.Next(0, 2);
        }
        if (this.strength == -1 && SandboxOptions.instance.Lore.Strength.getValue() == 1) {
            this.strength = 5;
        }
        if (this.strength == -1 && SandboxOptions.instance.Lore.Strength.getValue() == 2) {
            this.strength = 3;
        }
        if (this.strength == -1 && SandboxOptions.instance.Lore.Strength.getValue() == 3) {
            this.strength = 1;
        }
        if (this.strength == -1 && SandboxOptions.instance.Lore.Strength.getValue() == 4) {
            this.strength = Rand.Next(1, 5);
        }
        if (this.speedType == -1) {
            this.speedType = SandboxOptions.instance.Lore.Speed.getValue();
            if (this.speedType == 4) {
                this.speedType = Rand.Next(2);
            }
            if (this.inactive) {
                this.speedType = 3;
            }
        }
        if (this.bCrawling) {
            this.speedMod = 0.3f;
            this.speedMod += Rand.Next(1500) / 10000.0f;
            final IsoSpriteInstance def = this.def;
            def.AnimFrameIncrease *= 0.8f;
        }
        else if (SandboxOptions.instance.Lore.Speed.getValue() == 3 || this.speedType == 3 || Rand.Next(3) != 0) {
            this.speedMod = 0.55f;
            this.speedMod += Rand.Next(1500) / 10000.0f;
            this.walkVariant = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.walkVariant);
            this.def.setFrameSpeedPerFrame(0.24f);
            final IsoSpriteInstance def2 = this.def;
            def2.AnimFrameIncrease *= this.speedMod;
        }
        else if (SandboxOptions.instance.Lore.Speed.getValue() != 3 || this.speedType != 3) {
            this.bLunger = true;
            this.speedMod = 0.85f;
            this.walkVariant = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.walkVariant);
            this.speedMod += Rand.Next(1500) / 10000.0f;
            this.def.setFrameSpeedPerFrame(0.24f);
            final IsoSpriteInstance def3 = this.def;
            def3.AnimFrameIncrease *= this.speedMod;
        }
        this.walkType = Integer.toString(Rand.Next(5) + 1);
        if (this.speedType == 1) {
            this.setTurnDelta(1.0f);
            this.walkType = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.walkType);
        }
        if (this.speedType == 3) {
            this.walkType = Integer.toString(Rand.Next(3) + 1);
            this.walkType = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.walkType);
        }
        this.initCanCrawlUnderVehicle();
    }
    
    public void setWalkType(final String walkType) {
        this.walkType = walkType;
    }
    
    public void DoZombieSpeeds(final float speedMod) {
        if (this.bCrawling) {
            this.speedMod = speedMod;
            final IsoSpriteInstance def = this.def;
            def.AnimFrameIncrease *= 0.8f;
        }
        else if (Rand.Next(3) != 0 || SandboxOptions.instance.Lore.Speed.getValue() == 3) {
            this.speedMod = speedMod;
            this.speedMod += Rand.Next(1500) / 10000.0f;
            this.walkVariant = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.walkVariant);
            this.def.setFrameSpeedPerFrame(0.24f);
            final IsoSpriteInstance def2 = this.def;
            def2.AnimFrameIncrease *= this.speedMod;
        }
        else if (SandboxOptions.instance.Lore.Speed.getValue() != 3) {
            this.bLunger = true;
            this.speedMod = speedMod;
            this.walkVariant = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.walkVariant);
            this.def.setFrameSpeedPerFrame(0.24f);
            final IsoSpriteInstance def3 = this.def;
            def3.AnimFrameIncrease *= this.speedMod;
        }
    }
    
    public boolean isFakeDead() {
        return this.bFakeDead;
    }
    
    public void setFakeDead(final boolean bFakeDead) {
        if (bFakeDead && Rand.Next(2) == 0) {
            this.setCrawlerType(2);
        }
        this.bFakeDead = bFakeDead;
    }
    
    public boolean isForceFakeDead() {
        return this.bForceFakeDead;
    }
    
    public void setForceFakeDead(final boolean bForceFakeDead) {
        this.bForceFakeDead = bForceFakeDead;
    }
    
    public void HitSilence(final HandWeapon handWeapon, final IsoZombie target, final float n, final boolean b, final float n2) {
        super.HitSilence(handWeapon, target, b, n2);
        this.setTarget(target);
        if (this.Health <= 0.0f && !this.isOnDeathDone()) {
            this.DoZombieInventory();
            this.setOnDeathDone(true);
        }
        this.TimeSinceSeenFlesh = 0.0f;
    }
    
    @Override
    protected void DoDeathSilence(final HandWeapon handWeapon, final IsoGameCharacter isoGameCharacter) {
        if (this.Health <= 0.0f && !this.isOnDeathDone()) {
            this.DoZombieInventory();
            this.setOnDeathDone(true);
        }
        super.DoDeathSilence(handWeapon, isoGameCharacter);
    }
    
    public float Hit(final BaseVehicle baseVehicle, final float n, final boolean b, final Vector2 hitDir) {
        float health = 0.0f;
        this.AttackedBy = baseVehicle.getDriver();
        this.setHitDir(hitDir);
        this.setHitForce(n * 0.15f);
        final int n2 = (int)(n * 6.0f);
        this.setTarget(baseVehicle.getCharacter(0));
        if (this.bStaggerBack || this.isOnFloor() || this.getCurrentState() == ZombieGetUpState.instance() || this.getCurrentState() == ZombieOnGroundState.instance()) {
            if (this.isFakeDead()) {
                this.setFakeDead(false);
            }
            this.setHitReaction("Floor");
            health = n / 5.0f;
            if (!GameServer.bServer && !GameClient.bClient) {
                this.Health -= health;
                if (this.isDead()) {
                    this.Kill(baseVehicle.getDriver());
                }
            }
        }
        else {
            boolean staggerBack = this.isStaggerBack();
            boolean knockedDown = this.isKnockedDown();
            boolean becomeCrawler = this.isBecomeCrawler();
            if (b) {
                this.setHitFromBehind(true);
                if (Rand.Next(100) <= n2) {
                    if (Rand.Next(5) == 0) {
                        becomeCrawler = true;
                    }
                    staggerBack = true;
                    knockedDown = true;
                }
                else {
                    staggerBack = true;
                }
            }
            else if (n < 3.0f) {
                if (Rand.Next(100) <= n2) {
                    if (Rand.Next(8) == 0) {
                        becomeCrawler = true;
                    }
                    staggerBack = true;
                    knockedDown = true;
                }
                else {
                    staggerBack = true;
                }
            }
            else if (n < 10.0f) {
                if (Rand.Next(8) == 0) {
                    becomeCrawler = true;
                }
                staggerBack = true;
                knockedDown = true;
            }
            else {
                health = this.getHealth();
                if (!GameServer.bServer && !GameClient.bClient) {
                    this.Health -= health;
                    this.Kill(baseVehicle.getDriver());
                }
            }
            if (DebugOptions.instance.MultiplayerZombieCrawler.getValue()) {
                becomeCrawler = true;
            }
            if (!GameServer.bServer) {
                this.setStaggerBack(staggerBack);
                this.setKnockedDown(knockedDown);
                this.setBecomeCrawler(becomeCrawler);
            }
        }
        if (!GameServer.bServer && !GameClient.bClient) {
            this.addBlood(n);
        }
        return health;
    }
    
    @Override
    public void addBlood(final float n) {
        if (Rand.Next(10) > n) {
            return;
        }
        final float n2 = 0.6f;
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
        }
        if (SandboxOptions.instance.BloodLevel.getValue() > 1) {
            this.playBloodSplatterSound();
            new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + n2, this.getHitDir().x * 1.5f, this.getHitDir().y * 1.5f);
            IsoZombie.tempo.x = this.getHitDir().x;
            IsoZombie.tempo.y = this.getHitDir().y;
            int n3 = 3;
            int n4 = 0;
            int n5 = 1;
            switch (SandboxOptions.instance.BloodLevel.getValue()) {
                case 1: {
                    n5 = 0;
                    break;
                }
                case 2: {
                    n5 = 1;
                    n3 = 5;
                    n4 = 2;
                    break;
                }
                case 4: {
                    n5 = 3;
                    n3 = 2;
                    break;
                }
                case 5: {
                    n5 = 10;
                    n3 = 0;
                    break;
                }
            }
            for (int j = 0; j < n5; ++j) {
                if (Rand.Next(this.isCloseKilled() ? 8 : n3) == 0) {
                    new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + n2, this.getHitDir().x * 1.5f, this.getHitDir().y * 1.5f);
                }
                if (Rand.Next(this.isCloseKilled() ? 8 : n3) == 0) {
                    new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + n2, this.getHitDir().x * 1.8f, this.getHitDir().y * 1.8f);
                }
                if (Rand.Next(this.isCloseKilled() ? 8 : n3) == 0) {
                    new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + n2, this.getHitDir().x * 1.9f, this.getHitDir().y * 1.9f);
                }
                if (Rand.Next(this.isCloseKilled() ? 4 : n4) == 0) {
                    new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + n2, this.getHitDir().x * 3.9f, this.getHitDir().y * 3.9f);
                }
                if (Rand.Next(this.isCloseKilled() ? 4 : n4) == 0) {
                    new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + n2, this.getHitDir().x * 3.8f, this.getHitDir().y * 3.8f);
                }
                if (Rand.Next(this.isCloseKilled() ? 9 : 6) == 0) {
                    new IsoZombieGiblets(IsoZombieGiblets.GibletType.Eye, this.getCell(), this.getX(), this.getY(), this.getZ() + n2, this.getHitDir().x * 0.8f, this.getHitDir().y * 0.8f);
                }
            }
        }
    }
    
    private void processHitDirection(final HandWeapon handWeapon, final IsoGameCharacter isoGameCharacter) {
        String variableString = isoGameCharacter.getVariableString("ZombieHitReaction");
        if ("Shot".equals(variableString)) {
            variableString = "ShotBelly";
            isoGameCharacter.setCriticalHit(Rand.Next(100) < ((IsoPlayer)isoGameCharacter).calculateCritChance(this));
            final Vector2 forwardDirection = isoGameCharacter.getForwardDirection();
            final Vector2 hitAngle = this.getHitAngle();
            double n = Math.acos(forwardDirection.x * hitAngle.x + forwardDirection.y * hitAngle.y) * ((forwardDirection.x * hitAngle.y - forwardDirection.y * hitAngle.x >= 0.0) ? 1.0 : -1.0);
            if (n < 0.0) {
                n += 6.283185307179586;
            }
            String s = "";
            if (Math.toDegrees(n) < 45.0) {
                this.setHitFromBehind(true);
                s = "S";
                final int next = Rand.Next(9);
                if (next > 6) {
                    s = "L";
                }
                if (next > 4) {
                    s = "R";
                }
            }
            if (Math.toDegrees(n) > 45.0 && Math.toDegrees(n) < 90.0) {
                this.setHitFromBehind(true);
                if (Rand.Next(4) == 0) {
                    s = "S";
                }
                else {
                    s = "R";
                }
            }
            if (Math.toDegrees(n) > 90.0 && Math.toDegrees(n) < 135.0) {
                s = "R";
            }
            if (Math.toDegrees(n) > 135.0 && Math.toDegrees(n) < 180.0) {
                if (Rand.Next(4) == 0) {
                    s = "N";
                }
                else {
                    s = "R";
                }
            }
            if (Math.toDegrees(n) > 180.0 && Math.toDegrees(n) < 225.0) {
                s = "N";
                final int next2 = Rand.Next(9);
                if (next2 > 6) {
                    s = "L";
                }
                if (next2 > 4) {
                    s = "R";
                }
            }
            if (Math.toDegrees(n) > 225.0 && Math.toDegrees(n) < 270.0) {
                if (Rand.Next(4) == 0) {
                    s = "N";
                }
                else {
                    s = "L";
                }
            }
            if (Math.toDegrees(n) > 270.0 && Math.toDegrees(n) < 315.0) {
                this.setHitFromBehind(true);
                s = "L";
            }
            if (Math.toDegrees(n) > 315.0) {
                if (Rand.Next(4) == 0) {
                    s = "S";
                }
                else {
                    s = "L";
                }
            }
            if ("N".equals(s)) {
                if (this.isHitFromBehind()) {
                    variableString = "ShotBellyStep";
                }
                else {
                    switch (Rand.Next(2)) {
                        case 0: {
                            variableString = "ShotBelly";
                            break;
                        }
                        case 1: {
                            variableString = "ShotBellyStep";
                            break;
                        }
                    }
                }
            }
            if ("S".equals(s)) {
                variableString = "ShotBellyStep";
            }
            if ("L".equals(s) || "R".equals(s)) {
                if (this.isHitFromBehind()) {
                    switch (Rand.Next(3)) {
                        case 0: {
                            variableString = "ShotChest";
                            break;
                        }
                        case 1: {
                            variableString = "ShotLeg";
                            break;
                        }
                        case 2: {
                            variableString = "ShotShoulderStep";
                            break;
                        }
                    }
                }
                else {
                    switch (Rand.Next(5)) {
                        case 0: {
                            variableString = "ShotChest";
                            break;
                        }
                        case 1: {
                            variableString = "ShotChestStep";
                            break;
                        }
                        case 2: {
                            variableString = "ShotLeg";
                            break;
                        }
                        case 3: {
                            variableString = "ShotShoulder";
                            break;
                        }
                        case 4: {
                            variableString = "ShotShoulderStep";
                            break;
                        }
                    }
                }
                variableString = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, variableString, s);
            }
            if (isoGameCharacter.isCriticalHit()) {
                if ("S".equals(s)) {
                    variableString = "ShotHeadFwd";
                }
                if ("N".equals(s)) {
                    variableString = "ShotHeadBwd";
                }
                if (("L".equals(s) || "R".equals(s)) && Rand.Next(4) == 0) {
                    variableString = "ShotHeadBwd";
                }
            }
            if (variableString.contains("Head")) {
                this.addBlood(BloodBodyPartType.Head, false, true, true);
            }
            else if (variableString.contains("Chest")) {
                this.addBlood(BloodBodyPartType.Torso_Upper, !this.isCriticalHit(), this.isCriticalHit(), true);
            }
            else if (variableString.contains("Belly")) {
                this.addBlood(BloodBodyPartType.Torso_Lower, !this.isCriticalHit(), this.isCriticalHit(), true);
            }
            else if (variableString.contains("Leg")) {
                final boolean b = Rand.Next(2) == 0;
                if ("L".equals(s)) {
                    this.addBlood(b ? BloodBodyPartType.LowerLeg_L : BloodBodyPartType.UpperLeg_L, !this.isCriticalHit(), this.isCriticalHit(), true);
                }
                else {
                    this.addBlood(b ? BloodBodyPartType.LowerLeg_R : BloodBodyPartType.UpperLeg_R, !this.isCriticalHit(), this.isCriticalHit(), true);
                }
            }
            else if (variableString.contains("Shoulder")) {
                final boolean b2 = Rand.Next(2) == 0;
                if ("L".equals(s)) {
                    this.addBlood(b2 ? BloodBodyPartType.ForeArm_L : BloodBodyPartType.UpperArm_L, !this.isCriticalHit(), this.isCriticalHit(), true);
                }
                else {
                    this.addBlood(b2 ? BloodBodyPartType.ForeArm_R : BloodBodyPartType.UpperArm_R, !this.isCriticalHit(), this.isCriticalHit(), true);
                }
            }
        }
        else if (handWeapon.getCategories().contains("Blunt")) {
            this.addBlood(BloodBodyPartType.FromIndex(Rand.Next(BloodBodyPartType.UpperArm_L.index(), BloodBodyPartType.Groin.index())), false, false, true);
        }
        else if (!handWeapon.getCategories().contains("Unarmed")) {
            this.addBlood(BloodBodyPartType.FromIndex(Rand.Next(BloodBodyPartType.UpperArm_L.index(), BloodBodyPartType.Groin.index())), false, true, true);
        }
        if ("ShotHeadFwd".equals(variableString) && Rand.Next(2) == 0) {
            variableString = "ShotHeadFwd02";
        }
        if (this.getEatBodyTarget() != null) {
            if (this.getVariableBoolean("onknees")) {
                variableString = "OnKnees";
            }
            else {
                variableString = "Eating";
            }
        }
        if ("Floor".equalsIgnoreCase(variableString) && this.isCurrentState(ZombieGetUpState.instance()) && this.isFallOnFront()) {
            variableString = "GettingUpFront";
        }
        if (variableString != null && !"".equals(variableString)) {
            this.setHitReaction(variableString);
        }
        else {
            this.setStaggerBack(true);
            this.setHitReaction("");
            if ("LEFT".equals(this.getPlayerAttackPosition()) || "RIGHT".equals(this.getPlayerAttackPosition())) {
                isoGameCharacter.setCriticalHit(false);
            }
        }
    }
    
    @Override
    public void hitConsequences(final HandWeapon handWeapon, final IsoGameCharacter target, final boolean b, final float n, final boolean b2) {
        if (this.isOnlyJawStab() && !this.isCloseKilled()) {
            return;
        }
        super.hitConsequences(handWeapon, target, b, n, b2);
        if (DebugLog.isEnabled(DebugType.Combat)) {
            DebugLog.Combat.debugln(invokedynamic(makeConcatWithConstants:(SF)Ljava/lang/String;, this.OnlineID, n));
        }
        this.actionContext.reportEvent("wasHit");
        if (!b2) {
            this.processHitDirection(handWeapon, target);
        }
        if (!GameClient.bClient || this.target == null || target == this.target || IsoUtils.DistanceToSquared(this.x, this.y, this.target.x, this.target.y) >= 10.0f) {
            this.setTarget(target);
        }
        if ((!GameServer.bServer && !GameClient.bClient) || (GameClient.bClient && target instanceof IsoPlayer && ((IsoPlayer)target).isLocalPlayer() && !this.isRemoteZombie())) {
            this.setKnockedDown(target.isCriticalHit() || this.isOnFloor() || this.isAlwaysKnockedDown());
        }
        this.checkClimbOverFenceHit();
        this.checkClimbThroughWindowHit();
        if (this.shouldBecomeCrawler(target)) {
            this.setBecomeCrawler(true);
        }
    }
    
    @Override
    public void playHurtSound() {
    }
    
    private void checkClimbOverFenceHit() {
        if (this.isOnFloor()) {
            return;
        }
        if (!this.isCurrentState(ClimbOverFenceState.instance()) || !this.getVariableBoolean("ClimbFenceStarted") || this.isVariable("ClimbFenceOutcome", "fall") || this.getVariableBoolean("ClimbFenceFlopped")) {
            return;
        }
        final HashMap<Object, Object> hashMap = this.StateMachineParams.get(ClimbOverFenceState.instance());
        this.climbFenceWindowHit((int)hashMap.get(3), (int)hashMap.get(4));
    }
    
    private void checkClimbThroughWindowHit() {
        if (this.isOnFloor()) {
            return;
        }
        if (!this.isCurrentState(ClimbThroughWindowState.instance()) || !this.getVariableBoolean("ClimbWindowStarted") || this.isVariable("ClimbWindowOutcome", "fall") || this.getVariableBoolean("ClimbWindowFlopped")) {
            return;
        }
        final HashMap<Object, Object> hashMap = this.StateMachineParams.get(ClimbThroughWindowState.instance());
        this.climbFenceWindowHit((int)hashMap.get(12), (int)hashMap.get(13));
    }
    
    private void climbFenceWindowHit(final int n, final int n2) {
        if (this.getDir() == IsoDirections.W) {
            this.setX(n + 0.9f);
            this.setLx(this.getX());
        }
        else if (this.getDir() == IsoDirections.E) {
            this.setX(n + 0.1f);
            this.setLx(this.getX());
        }
        else if (this.getDir() == IsoDirections.N) {
            this.setY(n2 + 0.9f);
            this.setLy(this.getY());
        }
        else if (this.getDir() == IsoDirections.S) {
            this.setY(n2 + 0.1f);
            this.setLy(this.getY());
        }
        this.setStaggerBack(false);
        this.setKnockedDown(true);
        this.setOnFloor(true);
        this.setFallOnFront(true);
        this.setHitReaction("FenceWindow");
    }
    
    private boolean shouldBecomeCrawler(final IsoGameCharacter isoGameCharacter) {
        if (DebugOptions.instance.MultiplayerZombieCrawler.getValue()) {
            return true;
        }
        if (this.isBecomeCrawler()) {
            return true;
        }
        if (this.isCrawling()) {
            return false;
        }
        if (Core.bLastStand) {
            return false;
        }
        if (this.isDead()) {
            return false;
        }
        if (this.isCloseKilled()) {
            return false;
        }
        final IsoPlayer isoPlayer = Type.tryCastTo(isoGameCharacter, IsoPlayer.class);
        if (isoPlayer != null && !isoPlayer.isAimAtFloor() && isoPlayer.bDoShove) {
            return false;
        }
        int n = 30;
        if (isoPlayer != null && isoPlayer.isAimAtFloor() && isoPlayer.bDoShove) {
            if (this.isHitLegsWhileOnFloor()) {
                n = 7;
            }
            else {
                n = 15;
            }
        }
        return Rand.NextBool(n);
    }
    
    @Override
    public void removeFromWorld() {
        this.getEmitter().stopOrTriggerSoundByName("BurningFlesh");
        this.clearAggroList();
        VirtualZombieManager.instance.RemoveZombie(this);
        this.setPath2(null);
        PolygonalMap2.instance.cancelRequest(this);
        if (this.getFinder().progress != AStarPathFinder.PathFindProgress.notrunning && this.getFinder().progress != AStarPathFinder.PathFindProgress.found) {
            this.getFinder().progress = AStarPathFinder.PathFindProgress.notrunning;
        }
        if (this.group != null) {
            this.group.remove(this);
            this.group = null;
        }
        if (GameServer.bServer && this.OnlineID != -1) {
            ServerMap.instance.ZombieMap.remove(this.OnlineID);
            this.OnlineID = -1;
        }
        if (GameClient.bClient) {
            GameClient.instance.removeZombieFromCache(this);
        }
        this.getCell().getZombieList().remove(this);
        if (GameServer.bServer) {
            if (this.authOwner != null || this.authOwnerPlayer != null) {
                NetworkZombieManager.getInstance().moveZombie(this, null, null);
            }
            this.zombiePacketUpdated = false;
        }
        super.removeFromWorld();
    }
    
    public void resetForReuse() {
        this.setCrawler(false);
        this.initializeStates();
        this.actionContext.setGroup(ActionGroup.getActionGroup("zombie"));
        this.advancedAnimator.OnAnimDataChanged(false);
        this.setStateMachineLocked(false);
        this.setDefaultState();
        if (this.vocalEvent != 0L) {
            this.getEmitter().stopSound(this.vocalEvent);
            this.vocalEvent = 0L;
        }
        this.parameterZombieState.setState(ParameterZombieState.State.Idle);
        this.setSceneCulled(true);
        this.releaseAnimationPlayer();
        Arrays.fill(this.IsVisibleToPlayer, false);
        this.setCurrent(null);
        this.setLast(null);
        this.setOnFloor(false);
        this.setCanWalk(true);
        this.setFallOnFront(false);
        this.setHitTime(0);
        this.strength = -1;
        this.setImmortalTutorialZombie(false);
        this.setOnlyJawStab(false);
        this.setAlwaysKnockedDown(false);
        this.setForceEatingAnimation(false);
        this.setNoTeeth(false);
        this.cognition = -1;
        this.speedType = -1;
        this.bodyToEat = null;
        this.checkForCorpseTimer = 10000.0f;
        this.clearAttachedItems();
        this.setEatBodyTarget(this.target = null, false);
        this.setSkeleton(false);
        this.setReanimatedPlayer(false);
        this.setBecomeCrawler(false);
        this.setWasFakeDead(false);
        this.setKnifeDeath(false);
        this.setJawStabAttach(false);
        this.setReanimate(false);
        this.DoZombieStats();
        this.alerted = false;
        this.BonusSpotTime = 0.0f;
        this.TimeSinceSeenFlesh = 100000.0f;
        this.soundReactDelay = 0.0f;
        final Location delayedSound = this.delayedSound;
        final Location delayedSound2 = this.delayedSound;
        final Location delayedSound3 = this.delayedSound;
        final int x = -1;
        delayedSound3.z = x;
        delayedSound2.y = x;
        delayedSound.x = x;
        this.bSoundSourceRepeating = false;
        this.soundSourceTarget = null;
        this.soundAttract = 0.0f;
        this.soundAttractTimeout = 0.0f;
        if (SandboxOptions.instance.Lore.Toughness.getValue() == 1) {
            this.setHealth(3.5f + Rand.Next(0.0f, 0.3f));
        }
        if (SandboxOptions.instance.Lore.Toughness.getValue() == 2) {
            this.setHealth(1.8f + Rand.Next(0.0f, 0.3f));
        }
        if (SandboxOptions.instance.Lore.Toughness.getValue() == 3) {
            this.setHealth(0.5f + Rand.Next(0.0f, 0.3f));
        }
        if (SandboxOptions.instance.Lore.Toughness.getValue() == 4) {
            this.setHealth(Rand.Next(0.5f, 3.5f) + Rand.Next(0.0f, 0.3f));
        }
        this.setCollidable(true);
        this.setShootable(true);
        if (this.isOnFire()) {
            IsoFireManager.RemoveBurningCharacter(this);
            this.setOnFire(false);
        }
        if (this.AttachedAnimSprite != null) {
            this.AttachedAnimSprite.clear();
        }
        this.OnlineID = -1;
        this.bIndoorZombie = false;
        this.setVehicle4TestCollision(null);
        this.clearItemsToSpawnAtDeath();
        this.m_persistentOutfitId = 0;
        this.m_bPersistentOutfitInit = false;
        this.sharedDesc = null;
    }
    
    public boolean wasFakeDead() {
        return this.bWasFakeDead;
    }
    
    public void setWasFakeDead(final boolean bWasFakeDead) {
        this.bWasFakeDead = bWasFakeDead;
    }
    
    public void setCrawler(final boolean bCrawling) {
        this.bCrawling = bCrawling;
    }
    
    public boolean isBecomeCrawler() {
        return this.bBecomeCrawler;
    }
    
    public void setBecomeCrawler(final boolean bBecomeCrawler) {
        this.bBecomeCrawler = bBecomeCrawler;
    }
    
    public boolean isReanimate() {
        return this.bReanimate;
    }
    
    public void setReanimate(final boolean bReanimate) {
        this.bReanimate = bReanimate;
    }
    
    public boolean isReanimatedPlayer() {
        return this.bReanimatedPlayer;
    }
    
    public void setReanimatedPlayer(final boolean bReanimatedPlayer) {
        this.bReanimatedPlayer = bReanimatedPlayer;
    }
    
    public IsoPlayer getReanimatedPlayer() {
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            final IsoPlayer isoPlayer = IsoPlayer.players[i];
            if (isoPlayer != null && isoPlayer.ReanimatedCorpse == this) {
                return isoPlayer;
            }
        }
        return null;
    }
    
    public void setFemaleEtc(final boolean b) {
        this.setFemale(b);
        if (this.getDescriptor() != null) {
            this.getDescriptor().setFemale(b);
        }
        this.SpriteName = (b ? "KateZ" : "BobZ");
        this.hurtSound = (b ? "FemaleZombieHurt" : "MaleZombieHurt");
        if (this.vocalEvent != 0L && !this.getEmitter().isPlaying(b ? "FemaleZombieCombined" : "MaleZombieCombined")) {
            this.getEmitter().stopSound(this.vocalEvent);
            this.vocalEvent = 0L;
        }
    }
    
    public void addRandomBloodDirtHolesEtc() {
        this.addBlood(null, false, true, false);
        this.addDirt(null, OutfitRNG.Next(5, 10), false);
        this.addRandomVisualDamages();
        this.addRandomVisualBandages();
        final int max = Math.max(8 - (int)IsoWorld.instance.getWorldAgeDays() / 30, 0);
        for (int i = 0; i < 5; ++i) {
            if (OutfitRNG.NextBool(max)) {
                this.addBlood(null, false, true, false);
                this.addDirt(null, null, false);
            }
        }
        for (int j = 0; j < 8; ++j) {
            if (OutfitRNG.NextBool(max)) {
                final BloodBodyPartType fromIndex = BloodBodyPartType.FromIndex(OutfitRNG.Next(0, BloodBodyPartType.MAX.index()));
                this.addHole(fromIndex);
                this.addBlood(fromIndex, true, false, false);
            }
        }
    }
    
    public void useDescriptor(final SharedDescriptors.Descriptor sharedDesc) {
        this.getHumanVisual().clear();
        this.itemVisuals.clear();
        this.m_persistentOutfitId = ((sharedDesc == null) ? 0 : sharedDesc.getPersistentOutfitID());
        this.m_bPersistentOutfitInit = true;
        this.sharedDesc = sharedDesc;
        if (sharedDesc == null) {
            return;
        }
        this.setFemaleEtc(sharedDesc.isFemale());
        this.getHumanVisual().copyFrom(sharedDesc.getHumanVisual());
        this.getWornItems().setFromItemVisuals(sharedDesc.itemVisuals);
        this.onWornItemsChanged();
    }
    
    public SharedDescriptors.Descriptor getSharedDescriptor() {
        return this.sharedDesc;
    }
    
    public int getSharedDescriptorID() {
        return this.getPersistentOutfitID();
    }
    
    public int getScreenProperX(final int n) {
        return (int)(IsoUtils.XToScreen(this.x, this.y, this.z, 0) - IsoCamera.cameras[n].getOffX());
    }
    
    public int getScreenProperY(final int n) {
        return (int)(IsoUtils.YToScreen(this.x, this.y, this.z, 0) - IsoCamera.cameras[n].getOffY());
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
        this.getItemVisuals(this.itemVisuals);
        return this.itemVisuals;
    }
    
    @Override
    public void getItemVisuals(final ItemVisuals itemVisuals) {
        if (this.isUsingWornItems()) {
            this.getWornItems().getItemVisuals(itemVisuals);
        }
        else if (itemVisuals != this.itemVisuals) {
            itemVisuals.clear();
            itemVisuals.addAll(this.itemVisuals);
        }
    }
    
    @Override
    public boolean isUsingWornItems() {
        return this.isOnKillDone() || this.isOnDeathDone() || this.isReanimatedPlayer() || this.wasFakeDead();
    }
    
    public void setAsSurvivor() {
        String s = "Survivalist";
        switch (Rand.Next(3)) {
            case 1: {
                s = "Survivalist02";
                break;
            }
            case 2: {
                s = "Survivalist03";
                break;
            }
        }
        this.dressInPersistentOutfit(s);
    }
    
    @Override
    public void dressInRandomOutfit() {
        ZombiesZoneDefinition.dressInRandomOutfit(this);
    }
    
    @Override
    public void dressInNamedOutfit(final String pendingOutfitName) {
        this.wornItems.clear();
        this.getHumanVisual().clear();
        this.itemVisuals.clear();
        final Outfit outfit = this.isFemale() ? OutfitManager.instance.FindFemaleOutfit(pendingOutfitName) : OutfitManager.instance.FindMaleOutfit(pendingOutfitName);
        if (outfit == null) {
            return;
        }
        if (outfit.isEmpty()) {
            outfit.loadItems();
            this.pendingOutfitName = pendingOutfitName;
            return;
        }
        this.getHumanVisual().dressInNamedOutfit(pendingOutfitName, this.itemVisuals);
        this.getHumanVisual().synchWithOutfit(this.getHumanVisual().getOutfit());
        UnderwearDefinition.addRandomUnderwear(this);
        this.onWornItemsChanged();
    }
    
    @Override
    public void dressInPersistentOutfitID(final int persistentOutfitId) {
        this.getHumanVisual().clear();
        this.itemVisuals.clear();
        this.m_persistentOutfitId = persistentOutfitId;
        this.m_bPersistentOutfitInit = true;
        if (persistentOutfitId == 0) {
            return;
        }
        this.bDressInRandomOutfit = false;
        PersistentOutfits.instance.dressInOutfit(this, persistentOutfitId);
        this.onWornItemsChanged();
    }
    
    @Override
    public void dressInClothingItem(final String s) {
        this.wornItems.clear();
        this.getHumanVisual().dressInClothingItem(s, this.itemVisuals);
        this.onWornItemsChanged();
    }
    
    @Override
    public void onWornItemsChanged() {
        this.parameterShoeType.setShoeType(null);
    }
    
    @Override
    public void clothingItemChanged(final String s) {
        super.clothingItemChanged(s);
        if (!StringUtils.isNullOrWhitespace(this.pendingOutfitName)) {
            final Outfit outfit = this.isFemale() ? OutfitManager.instance.FindFemaleOutfit(this.pendingOutfitName) : OutfitManager.instance.FindMaleOutfit(this.pendingOutfitName);
            if (outfit != null && !outfit.isEmpty()) {
                this.dressInNamedOutfit(this.pendingOutfitName);
                this.pendingOutfitName = null;
                this.resetModelNextFrame();
            }
        }
    }
    
    public boolean WanderFromWindow() {
        if (this.getCurrentSquare() == null) {
            return false;
        }
        final FloodFill floodFill = IsoZombie.floodFill;
        floodFill.calculate(this, this.getCurrentSquare());
        final IsoGridSquare choose = floodFill.choose();
        floodFill.reset();
        if (choose != null) {
            this.pathToLocation(choose.getX(), choose.getY(), choose.getZ());
            return true;
        }
        return false;
    }
    
    public boolean isUseless() {
        return this.useless;
    }
    
    public void setUseless(final boolean useless) {
        this.useless = useless;
    }
    
    public void setImmortalTutorialZombie(final boolean immortalTutorialZombie) {
        this.ImmortalTutorialZombie = immortalTutorialZombie;
    }
    
    public boolean isTargetInCone(final float n, final float n2) {
        if (this.target == null) {
            return false;
        }
        IsoZombie.tempo.set(this.target.getX() - this.getX(), this.target.getY() - this.getY());
        final float length = IsoZombie.tempo.getLength();
        if (length == 0.0f) {
            return true;
        }
        if (length > n) {
            return false;
        }
        IsoZombie.tempo.normalize();
        this.getVectorFromDirection(IsoZombie.tempo2);
        return IsoZombie.tempo.dot(IsoZombie.tempo2) >= n2;
    }
    
    public boolean testCollideWithVehicles(final BaseVehicle baseVehicle) {
        if (this.Health <= 0.0f) {
            return false;
        }
        if (!this.isProne()) {
            if (baseVehicle.shouldCollideWithCharacters()) {
                final Vector2 vector2 = BaseVehicle.TL_vector2_pool.get().alloc();
                if (baseVehicle.testCollisionWithCharacter(this, 0.3f, vector2) != null) {
                    BaseVehicle.TL_vector2_pool.get().release(vector2);
                    baseVehicle.hitCharacter(this);
                    super.update();
                    return true;
                }
                BaseVehicle.TL_vector2_pool.get().release(vector2);
            }
            return false;
        }
        if (baseVehicle.getDriver() == null) {
            return false;
        }
        if ((baseVehicle.isEngineRunning() ? baseVehicle.testCollisionWithProneCharacter(this, true) : 0) > 0) {
            if (!this.emitter.isPlaying(this.getHurtSound())) {
                this.playHurtSound();
            }
            this.AttackedBy = baseVehicle.getDriver();
            baseVehicle.hitCharacter(this);
            if (!GameServer.bServer && !GameClient.bClient && this.isDead()) {
                this.Kill(baseVehicle.getDriver());
            }
            super.update();
            return true;
        }
        return false;
    }
    
    public boolean isCrawling() {
        return this.bCrawling;
    }
    
    public boolean isCanCrawlUnderVehicle() {
        return this.bCanCrawlUnderVehicle;
    }
    
    public void setCanCrawlUnderVehicle(final boolean bCanCrawlUnderVehicle) {
        this.bCanCrawlUnderVehicle = bCanCrawlUnderVehicle;
    }
    
    public boolean isCanWalk() {
        return this.bCanWalk;
    }
    
    public void setCanWalk(final boolean bCanWalk) {
        this.bCanWalk = bCanWalk;
    }
    
    public void initCanCrawlUnderVehicle() {
        int n = 100;
        switch (SandboxOptions.instance.Lore.CrawlUnderVehicle.getValue()) {
            case 1: {
                n = 0;
                break;
            }
            case 2: {
                n = 5;
                break;
            }
            case 3: {
                n = 10;
                break;
            }
            case 4: {
                n = 25;
                break;
            }
            case 5: {
                n = 50;
                break;
            }
            case 6: {
                n = 75;
                break;
            }
            case 7: {
                n = 100;
                break;
            }
        }
        this.setCanCrawlUnderVehicle(Rand.Next(100) < n);
    }
    
    public boolean shouldGetUpFromCrawl() {
        if (this.isCurrentState(ZombieGetUpFromCrawlState.instance())) {
            return true;
        }
        if (this.isCurrentState(ZombieGetUpState.instance())) {
            return this.stateMachine.getPrevious() == ZombieGetUpFromCrawlState.instance();
        }
        if (!this.isCrawling()) {
            return false;
        }
        if (!this.isCanWalk()) {
            return false;
        }
        if (this.isCurrentState(PathFindState.instance())) {
            return (this.stateMachine.getPrevious() != ZombieGetDownState.instance() || !ZombieGetDownState.instance().isNearStartXY(this)) && this.getPathFindBehavior2().shouldGetUpFromCrawl();
        }
        if (this.isCurrentState(WalkTowardState.instance())) {
            final float targetX = this.getPathFindBehavior2().getTargetX();
            final float targetY = this.getPathFindBehavior2().getTargetY();
            if (this.DistToSquared(targetX, targetY) > 0.010000001f && PolygonalMap2.instance.lineClearCollide(this.x, this.y, targetX, targetY, (int)this.z, null)) {
                return false;
            }
        }
        return !this.isCurrentState(ZombieGetDownState.instance()) && PolygonalMap2.instance.canStandAt(this.x, this.y, (int)this.z, null, false, true);
    }
    
    public void toggleCrawling() {
        final boolean bCanCrawlUnderVehicle = this.bCanCrawlUnderVehicle;
        if (this.bCrawling) {
            this.setCrawler(false);
            this.setKnockedDown(false);
            this.setStaggerBack(false);
            this.setFallOnFront(false);
            this.setOnFloor(false);
            this.DoZombieStats();
        }
        else {
            this.setCrawler(true);
            this.setOnFloor(true);
            this.DoZombieStats();
            this.walkVariant = "ZombieWalk";
        }
        this.bCanCrawlUnderVehicle = bCanCrawlUnderVehicle;
    }
    
    public void knockDown(final boolean b) {
        this.setKnockedDown(true);
        this.setStaggerBack(true);
        this.setHitReaction("");
        this.setPlayerAttackPosition(b ? "BEHIND" : null);
        this.setHitForce(1.0f);
        this.reportEvent("wasHit");
    }
    
    public void addItemToSpawnAtDeath(final InventoryItem e) {
        if (this.itemsToSpawnAtDeath == null) {
            this.itemsToSpawnAtDeath = new ArrayList<InventoryItem>();
        }
        this.itemsToSpawnAtDeath.add(e);
    }
    
    public void clearItemsToSpawnAtDeath() {
        if (this.itemsToSpawnAtDeath != null) {
            this.itemsToSpawnAtDeath.clear();
        }
    }
    
    public IsoMovingObject getEatBodyTarget() {
        return this.eatBodyTarget;
    }
    
    public float getEatSpeed() {
        return this.eatSpeed;
    }
    
    public void setEatBodyTarget(final IsoMovingObject isoMovingObject, final boolean b) {
        this.setEatBodyTarget(isoMovingObject, b, Rand.Next(0.8f, 1.2f) * GameTime.getAnimSpeedFix());
    }
    
    public void setEatBodyTarget(final IsoMovingObject eatBodyTarget, final boolean b, final float eatSpeed) {
        if (eatBodyTarget == this.eatBodyTarget) {
            return;
        }
        if (!b && eatBodyTarget != null && eatBodyTarget.getEatingZombies().size() >= 3) {
            return;
        }
        if (this.eatBodyTarget != null) {
            this.eatBodyTarget.getEatingZombies().remove(this);
        }
        if ((this.eatBodyTarget = eatBodyTarget) == null) {
            return;
        }
        this.eatBodyTarget.getEatingZombies().add(this);
        this.eatSpeed = eatSpeed;
    }
    
    private void updateEatBodyTarget() {
        if (this.bodyToEat != null && this.isCurrentState(ZombieIdleState.instance()) && this.DistToSquared(this.bodyToEat) <= 1.0f && (int)this.getZ() == (int)this.bodyToEat.getZ()) {
            this.setEatBodyTarget(this.bodyToEat, false);
            this.bodyToEat = null;
        }
        if (this.eatBodyTarget == null) {
            return;
        }
        if (this.eatBodyTarget instanceof IsoDeadBody && this.eatBodyTarget.getStaticMovingObjectIndex() == -1) {
            this.setEatBodyTarget(null, false);
        }
        if (this.target != null && !this.target.isOnFloor() && this.target != this.eatBodyTarget) {
            this.setEatBodyTarget(null, false);
        }
        final IsoPlayer isoPlayer = Type.tryCastTo(this.eatBodyTarget, IsoPlayer.class);
        if (isoPlayer != null && isoPlayer.ReanimatedCorpse != null) {
            this.setEatBodyTarget(null, false);
        }
        if (isoPlayer != null && isoPlayer.isAlive() && !isoPlayer.isOnFloor() && !isoPlayer.isCurrentState(PlayerHitReactionState.instance())) {
            this.setEatBodyTarget(null, false);
        }
        if (!this.isCurrentState(ZombieEatBodyState.instance()) && this.eatBodyTarget != null && this.DistToSquared(this.eatBodyTarget) > 1.0f) {
            this.setEatBodyTarget(null, false);
        }
        if (this.eatBodyTarget != null && this.eatBodyTarget.getSquare() != null && this.current != null && this.current.isSomethingTo(this.eatBodyTarget.getSquare())) {
            this.setEatBodyTarget(null, false);
        }
    }
    
    private void updateCharacterTextureAnimTime() {
        this.m_characterTextureAnimDuration = ((this.isCurrentState(WalkTowardState.instance()) || this.isCurrentState(PathFindState.instance())) ? 0.67f : 2.0f);
        this.m_characterTextureAnimTime += GameTime.getInstance().getTimeDelta();
        if (this.m_characterTextureAnimTime > this.m_characterTextureAnimDuration) {
            this.m_characterTextureAnimTime %= this.m_characterTextureAnimDuration;
        }
    }
    
    public Vector2 getHitAngle() {
        return this.hitAngle;
    }
    
    public void setHitAngle(final Vector2 vector2) {
        if (vector2 == null) {
            return;
        }
        this.hitAngle.set(vector2);
    }
    
    public int getCrawlerType() {
        return this.crawlerType;
    }
    
    public void setCrawlerType(final int crawlerType) {
        this.crawlerType = crawlerType;
    }
    
    public void addRandomVisualBandages() {
        if ("Tutorial".equals(Core.getInstance().getGameMode())) {
            return;
        }
        for (int i = 0; i < 5; ++i) {
            if (OutfitRNG.Next(10) == 0) {
                this.addBodyVisualFromItemType(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, BodyPartType.getRandom().getBandageModel()));
            }
        }
    }
    
    public void addVisualBandage(final BodyPartType bodyPartType, final boolean b) {
        this.addBodyVisualFromItemType(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, bodyPartType.getBandageModel(), b ? "_Blood" : ""));
    }
    
    public void addRandomVisualDamages() {
        for (int i = 0; i < 5; ++i) {
            if (OutfitRNG.Next(5) == 0) {
                this.addBodyVisualFromItemType(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (String)OutfitRNG.pickRandom(ScriptManager.instance.getZedDmgMap())));
            }
        }
    }
    
    public String getPlayerAttackPosition() {
        return this.playerAttackPosition;
    }
    
    public void setPlayerAttackPosition(final String playerAttackPosition) {
        this.playerAttackPosition = playerAttackPosition;
    }
    
    public boolean isSitAgainstWall() {
        return this.sitAgainstWall;
    }
    
    public void setSitAgainstWall(final boolean sitAgainstWall) {
        this.sitAgainstWall = sitAgainstWall;
        this.networkAI.extraUpdate();
    }
    
    @Override
    public boolean isSkeleton() {
        if (Core.bDebug && DebugOptions.instance.ModelSkeleton.getValue()) {
            this.getHumanVisual().setSkinTextureIndex(2);
            return true;
        }
        return this.isSkeleton;
    }
    
    @Override
    public boolean isZombie() {
        return true;
    }
    
    public void setSkeleton(final boolean isSkeleton) {
        this.isSkeleton = isSkeleton;
        if (isSkeleton) {
            this.getHumanVisual().setHairModel("");
            this.getHumanVisual().setBeardModel("");
            ModelManager.instance.Reset(this);
        }
    }
    
    public int getHitTime() {
        return this.hitTime;
    }
    
    public void setHitTime(final int hitTime) {
        this.hitTime = hitTime;
    }
    
    public int getThumpTimer() {
        return this.thumpTimer;
    }
    
    public void setThumpTimer(final int thumpTimer) {
        this.thumpTimer = thumpTimer;
    }
    
    public IsoMovingObject getTarget() {
        return this.target;
    }
    
    public void setTargetSeenTime(final float targetSeenTime) {
        this.targetSeenTime = targetSeenTime;
    }
    
    public float getTargetSeenTime() {
        return this.targetSeenTime;
    }
    
    public boolean isTargetVisible() {
        final IsoPlayer isoPlayer = Type.tryCastTo(this.target, IsoPlayer.class);
        return isoPlayer != null && this.getCurrentSquare() != null && (GameServer.bServer ? ServerLOS.instance.isCouldSee(isoPlayer, this.getCurrentSquare()) : this.getCurrentSquare().isCouldSee(isoPlayer.getPlayerNum()));
    }
    
    @Override
    public float getTurnDelta() {
        return this.m_turnDeltaNormal;
    }
    
    @Override
    public boolean isAttacking() {
        return this.isZombieAttacking();
    }
    
    @Override
    public boolean isZombieAttacking() {
        final State currentState = this.getCurrentState();
        return currentState != null && currentState.isAttacking(this);
    }
    
    @Override
    public boolean isZombieAttacking(final IsoMovingObject isoMovingObject) {
        if (GameClient.bClient && this.authOwner == null) {
            return this.legsSprite != null && this.legsSprite.CurrentAnim != null && "ZombieBite".equals(this.legsSprite.CurrentAnim.name);
        }
        return isoMovingObject == this.target && this.isCurrentState(AttackState.instance());
    }
    
    public int getHitHeadWhileOnFloor() {
        return this.hitHeadWhileOnFloor;
    }
    
    public String getRealState() {
        return this.realState.toString();
    }
    
    public void setHitHeadWhileOnFloor(final int hitHeadWhileOnFloor) {
        this.hitHeadWhileOnFloor = hitHeadWhileOnFloor;
        this.networkAI.extraUpdate();
    }
    
    public boolean isHitLegsWhileOnFloor() {
        return this.hitLegsWhileOnFloor;
    }
    
    public void setHitLegsWhileOnFloor(final boolean hitLegsWhileOnFloor) {
        this.hitLegsWhileOnFloor = hitLegsWhileOnFloor;
    }
    
    public void makeInactive(final boolean b) {
        if (b == this.inactive) {
            return;
        }
        if (b) {
            this.walkType = Integer.toString(Rand.Next(3) + 1);
            this.walkType = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.walkType);
            this.bRunning = false;
            this.inactive = true;
            this.speedType = 3;
        }
        else {
            this.speedType = -1;
            this.inactive = false;
            this.DoZombieStats();
        }
    }
    
    public float getFootstepVolume() {
        return this.footstepVolume;
    }
    
    public boolean isFacingTarget() {
        if (this.target == null) {
            return false;
        }
        if (GameClient.bClient && !this.isLocal() && this.isBumped()) {
            return false;
        }
        IsoZombie.tempo.set(this.target.x - this.x, this.target.y - this.y).normalize();
        if (IsoZombie.tempo.getLength() == 0.0f) {
            return true;
        }
        this.getLookVector(IsoZombie.tempo2);
        return Vector2.dot(IsoZombie.tempo.x, IsoZombie.tempo.y, IsoZombie.tempo2.x, IsoZombie.tempo2.y) >= 0.8;
    }
    
    public boolean isTargetLocationKnown() {
        return this.target != null && (this.BonusSpotTime > 0.0f || this.TimeSinceSeenFlesh < 1.0f);
    }
    
    protected int getSandboxMemoryDuration() {
        final int value = SandboxOptions.instance.Lore.Memory.getValue();
        int n = 160;
        if (this.inactive) {
            n = 5;
        }
        else if (value == 1) {
            n = 250;
        }
        else if (value == 3) {
            n = 100;
        }
        else if (value == 4) {
            n = 5;
        }
        return n * 5;
    }
    
    public boolean shouldDoFenceLunge() {
        if (!SandboxOptions.instance.Lore.ZombiesFenceLunge.getValue()) {
            return false;
        }
        if (Rand.NextBool(3)) {
            return false;
        }
        final IsoGameCharacter isoGameCharacter = Type.tryCastTo(this.target, IsoGameCharacter.class);
        return isoGameCharacter != null && (int)isoGameCharacter.getZ() == (int)this.getZ() && isoGameCharacter.getVehicle() == null && this.DistTo(isoGameCharacter) < 3.9;
    }
    
    @Override
    public boolean isProne() {
        return this.isOnFloor() && (this.bCrawling || this.isCurrentState(ZombieOnGroundState.instance()) || this.isCurrentState(FakeDeadZombieState.instance()));
    }
    
    public void setTarget(final IsoMovingObject target) {
        if (this.target == target) {
            return;
        }
        this.target = target;
        this.networkAI.extraUpdate();
    }
    
    public boolean isAlwaysKnockedDown() {
        return this.alwaysKnockedDown;
    }
    
    public void setAlwaysKnockedDown(final boolean alwaysKnockedDown) {
        this.alwaysKnockedDown = alwaysKnockedDown;
    }
    
    public void setDressInRandomOutfit(final boolean bDressInRandomOutfit) {
        this.bDressInRandomOutfit = bDressInRandomOutfit;
    }
    
    public void setBodyToEat(final IsoDeadBody bodyToEat) {
        this.bodyToEat = bodyToEat;
    }
    
    public boolean isForceEatingAnimation() {
        return this.forceEatingAnimation;
    }
    
    public void setForceEatingAnimation(final boolean forceEatingAnimation) {
        this.forceEatingAnimation = forceEatingAnimation;
    }
    
    public boolean isOnlyJawStab() {
        return this.onlyJawStab;
    }
    
    public void setOnlyJawStab(final boolean onlyJawStab) {
        this.onlyJawStab = onlyJawStab;
    }
    
    public boolean isNoTeeth() {
        return this.noTeeth;
    }
    
    public void setNoTeeth(final boolean noTeeth) {
        this.noTeeth = noTeeth;
    }
    
    public void setThumpFlag(final int thumpFlag) {
        if (this.thumpFlag == thumpFlag) {
            return;
        }
        this.thumpFlag = thumpFlag;
        this.networkAI.extraUpdate();
    }
    
    public void setThumpCondition(final float n) {
        this.thumpCondition = PZMath.clamp_01(n);
    }
    
    public void setThumpCondition(int clamp, final int n) {
        if (n <= 0) {
            this.thumpCondition = 0.0f;
            return;
        }
        clamp = PZMath.clamp(clamp, 0, n);
        this.thumpCondition = clamp / (float)n;
    }
    
    public float getThumpCondition() {
        return this.thumpCondition;
    }
    
    public boolean isStaggerBack() {
        return this.bStaggerBack;
    }
    
    public void setStaggerBack(final boolean bStaggerBack) {
        this.bStaggerBack = bStaggerBack;
    }
    
    public boolean isKnifeDeath() {
        return this.bKnifeDeath;
    }
    
    public void setKnifeDeath(final boolean bKnifeDeath) {
        this.bKnifeDeath = bKnifeDeath;
    }
    
    public boolean isJawStabAttach() {
        return this.bJawStabAttach;
    }
    
    public void setJawStabAttach(final boolean bJawStabAttach) {
        this.bJawStabAttach = bJawStabAttach;
    }
    
    @Override
    public void writeInventory(final ByteBuffer byteBuffer) {
        GameWindow.WriteString(byteBuffer, this.isFemale() ? "inventoryfemale" : "inventorymale");
        if (this.getInventory() != null) {
            byteBuffer.put((byte)1);
            try {
                int id = -1;
                for (final InventoryItem inventoryItem : this.getInventory().getItems()) {
                    if (PersistentOutfits.instance.isHatFallen(this.getPersistentOutfitID()) && inventoryItem.getScriptItem() != null && inventoryItem.getScriptItem().getChanceToFall() > 0) {
                        id = inventoryItem.id;
                    }
                }
                if (id != -1) {
                    this.getInventory().removeItemWithID(id);
                }
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
                        if (PersistentOutfits.instance.isHatFallen(this.getPersistentOutfitID()) && value.getItem().getScriptItem() != null && value.getItem().getScriptItem().getChanceToFall() > 0) {
                            GameWindow.WriteStringUTF(byteBuffer, "");
                            byteBuffer.putShort((short)(-1));
                        }
                        else {
                            GameWindow.WriteStringUTF(byteBuffer, value.getLocation());
                            byteBuffer.putShort((short)save.indexOf(value.getItem()));
                        }
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
                        GameWindow.WriteStringUTF(byteBuffer, value2.getLocation());
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
    
    public void Kill(final IsoGameCharacter isoGameCharacter, final boolean b) {
        if (this.isOnKillDone()) {
            return;
        }
        super.Kill(isoGameCharacter);
        if (this.shouldDoInventory()) {
            this.DoZombieInventory();
        }
        LuaEventManager.triggerEvent("OnZombieDead", this);
        if (isoGameCharacter == null) {
            this.DoDeath(null, null, b);
        }
        else if (isoGameCharacter.getPrimaryHandItem() instanceof HandWeapon) {
            this.DoDeath((HandWeapon)isoGameCharacter.getPrimaryHandItem(), isoGameCharacter, b);
        }
        else {
            this.DoDeath(this.getUseHandWeapon(), isoGameCharacter, b);
        }
    }
    
    @Override
    public void Kill(final IsoGameCharacter isoGameCharacter) {
        this.Kill(isoGameCharacter, true);
    }
    
    @Override
    public boolean shouldDoInventory() {
        return !GameClient.bClient || (this.getAttackedBy() instanceof IsoPlayer && ((IsoPlayer)this.getAttackedBy()).isLocalPlayer()) || (this.getAttackedBy() == IsoWorld.instance.CurrentCell.getFakeZombieForHit() && this.wasLocal());
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
        if (GameClient.bClient && this.shouldDoInventory()) {
            GameClient.sendZombieDeath(this);
        }
        final IsoDeadBody isoDeadBody = new IsoDeadBody(this);
        if (this.isFakeDead()) {
            isoDeadBody.setCrawling(true);
        }
    }
    
    @Override
    public HitReactionNetworkAI getHitReactionNetworkAI() {
        return this.networkAI.hitReaction;
    }
    
    @Override
    public NetworkCharacterAI getNetworkCharacterAI() {
        return this.networkAI;
    }
    
    public boolean wasLocal() {
        return this.getNetworkCharacterAI() == null || this.getNetworkCharacterAI().wasLocal();
    }
    
    @Override
    public boolean isLocal() {
        return super.isLocal() || !this.isRemoteZombie();
    }
    
    @Override
    public boolean isVehicleCollisionActive(final BaseVehicle baseVehicle) {
        return super.isVehicleCollisionActive(baseVehicle) && !this.isCurrentState(ZombieFallDownState.instance()) && !this.isCurrentState(ZombieFallingState.instance());
    }
    
    public void applyDamageFromVehicle(final float n, final float n2) {
        this.addBlood(n);
        this.Health -= n2;
        if (this.Health < 0.0f) {
            this.Health = 0.0f;
        }
    }
    
    @Override
    public float Hit(final BaseVehicle baseVehicle, final float n, final boolean b, final float n2, final float n3) {
        final float hit = this.Hit(baseVehicle, n, b, this.getHitDir().set(n2, n3));
        this.applyDamageFromVehicle(n, hit);
        super.Hit(baseVehicle, n, b, n2, n3);
        return hit;
    }
    
    @Override
    public Float calcHitDir(final IsoGameCharacter isoGameCharacter, final HandWeapon handWeapon, final Vector2 vector2) {
        Float n = super.calcHitDir(isoGameCharacter, handWeapon, vector2);
        if (this.getAnimationPlayer() != null) {
            n = this.getAnimAngleRadians();
        }
        return n;
    }
    
    static {
        m_sharedSkeleRepo = new SharedSkeleAnimationRepository();
        IsoZombie.AttackAnimTimeMax = 50;
        tempBodies = new ArrayList<IsoDeadBody>();
        floodFill = new FloodFill();
    }
    
    private static class Aggro
    {
        IsoMovingObject obj;
        float damage;
        long lastDamage;
        
        public Aggro(final IsoMovingObject obj, final float damage) {
            this.obj = obj;
            this.damage = damage;
            this.lastDamage = System.currentTimeMillis();
        }
        
        public void addDamage(final float n) {
            this.damage += n;
            this.lastDamage = System.currentTimeMillis();
        }
        
        public float getAggro() {
            return Math.min(1.0f, Math.max(0.0f, Math.min(1.0f, Math.max(0.0f, (10000.0f - (System.currentTimeMillis() - this.lastDamage)) / 5000.0f)) * this.damage * 0.5f));
        }
    }
    
    public enum ZombieSound
    {
        Burned(10), 
        DeadCloseKilled(10), 
        DeadNotCloseKilled(10), 
        Hurt(10), 
        Idle(15), 
        Lunge(40), 
        MAX(-1);
        
        private int radius;
        private static final ZombieSound[] values;
        
        private ZombieSound(final int radius) {
            this.radius = radius;
        }
        
        public int radius() {
            return this.radius;
        }
        
        public static ZombieSound fromIndex(final int n) {
            return (n >= 0 && n < ZombieSound.values.length) ? ZombieSound.values[n] : ZombieSound.MAX;
        }
        
        private static /* synthetic */ ZombieSound[] $values() {
            return new ZombieSound[] { ZombieSound.Burned, ZombieSound.DeadCloseKilled, ZombieSound.DeadNotCloseKilled, ZombieSound.Hurt, ZombieSound.Idle, ZombieSound.Lunge, ZombieSound.MAX };
        }
        
        static {
            $VALUES = $values();
            values = values();
        }
    }
    
    private static final class FloodFill
    {
        private IsoGridSquare start;
        private final int FLOOD_SIZE = 11;
        private final BooleanGrid visited;
        private final Stack<IsoGridSquare> stack;
        private IsoBuilding building;
        private Mover mover;
        private final ArrayList<IsoGridSquare> choices;
        
        private FloodFill() {
            this.start = null;
            this.visited = new BooleanGrid(11, 11);
            this.stack = new Stack<IsoGridSquare>();
            this.building = null;
            this.mover = null;
            this.choices = new ArrayList<IsoGridSquare>(121);
        }
        
        void calculate(final Mover mover, IsoGridSquare pop) {
            this.start = pop;
            this.mover = mover;
            if (this.start.getRoom() != null) {
                this.building = this.start.getRoom().getBuilding();
            }
            this.push(this.start.getX(), this.start.getY());
            while ((pop = this.pop()) != null) {
                int x;
                int y;
                for (x = pop.getX(), y = pop.getY(); this.shouldVisit(x, y, x, y - 1); --y) {}
                int n2;
                int n = n2 = 0;
                do {
                    this.visited.setValue(this.gridX(x), this.gridY(y), true);
                    final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(x, y, this.start.getZ());
                    if (gridSquare != null) {
                        this.choices.add(gridSquare);
                    }
                    if (n2 == 0 && this.shouldVisit(x, y, x - 1, y)) {
                        this.push(x - 1, y);
                        n2 = 1;
                    }
                    else if (n2 != 0 && !this.shouldVisit(x, y, x - 1, y)) {
                        n2 = 0;
                    }
                    else if (n2 != 0 && !this.shouldVisit(x - 1, y, x - 1, y - 1)) {
                        this.push(x - 1, y);
                    }
                    if (n == 0 && this.shouldVisit(x, y, x + 1, y)) {
                        this.push(x + 1, y);
                        n = 1;
                    }
                    else if (n != 0 && !this.shouldVisit(x, y, x + 1, y)) {
                        n = 0;
                    }
                    else if (n != 0 && !this.shouldVisit(x + 1, y, x + 1, y - 1)) {
                        this.push(x + 1, y);
                    }
                    ++y;
                } while (this.shouldVisit(x, y - 1, x, y));
            }
        }
        
        boolean shouldVisit(final int n, final int n2, final int n3, final int n4) {
            if (this.gridX(n3) >= 11 || this.gridX(n3) < 0) {
                return false;
            }
            if (this.gridY(n4) >= 11 || this.gridY(n4) < 0) {
                return false;
            }
            if (this.visited.getValue(this.gridX(n3), this.gridY(n4))) {
                return false;
            }
            final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n3, n4, this.start.getZ());
            return gridSquare != null && !gridSquare.Has(IsoObjectType.stairsBN) && !gridSquare.Has(IsoObjectType.stairsMN) && !gridSquare.Has(IsoObjectType.stairsTN) && !gridSquare.Has(IsoObjectType.stairsBW) && !gridSquare.Has(IsoObjectType.stairsMW) && !gridSquare.Has(IsoObjectType.stairsTW) && (gridSquare.getRoom() == null || this.building != null) && (gridSquare.getRoom() != null || this.building == null) && !IsoWorld.instance.CurrentCell.blocked(this.mover, n3, n4, this.start.getZ(), n, n2, this.start.getZ());
        }
        
        void push(final int n, final int n2) {
            this.stack.push(IsoWorld.instance.CurrentCell.getGridSquare(n, n2, this.start.getZ()));
        }
        
        IsoGridSquare pop() {
            return this.stack.isEmpty() ? null : this.stack.pop();
        }
        
        int gridX(final int n) {
            return n - (this.start.getX() - 5);
        }
        
        int gridY(final int n) {
            return n - (this.start.getY() - 5);
        }
        
        int gridX(final IsoGridSquare isoGridSquare) {
            return isoGridSquare.getX() - (this.start.getX() - 5);
        }
        
        int gridY(final IsoGridSquare isoGridSquare) {
            return isoGridSquare.getY() - (this.start.getY() - 5);
        }
        
        IsoGridSquare choose() {
            if (this.choices.isEmpty()) {
                return null;
            }
            return this.choices.get(Rand.Next(this.choices.size()));
        }
        
        void reset() {
            this.building = null;
            this.choices.clear();
            this.stack.clear();
            this.visited.clear();
        }
    }
    
    private static class s_performance
    {
        static final PerformanceProfileProbe update;
        static final PerformanceProfileProbe postUpdate;
        
        static {
            update = new PerformanceProfileProbe("IsoZombie.update");
            postUpdate = new PerformanceProfileProbe("IsoZombie.postUpdate");
        }
    }
}

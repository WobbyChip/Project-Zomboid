// 
// Decompiled by Procyon v0.5.36
// 

package zombie.vehicles;

import zombie.core.skinnedmodel.model.VehicleSubModelInstance;
import zombie.scripting.objects.ModelScript;
import java.util.ArrayDeque;
import zombie.popman.ObjectPool;
import java.util.function.Supplier;
import zombie.debug.DebugType;
import se.krka.kahlua.j2se.KahluaTableImpl;
import fmod.fmod.FMOD_STUDIO_PARAMETER_DESCRIPTION;
import java.util.BitSet;
import zombie.audio.GameSoundClip;
import zombie.scripting.objects.ModelAttachment;
import zombie.core.skinnedmodel.model.Model;
import zombie.util.Pool;
import zombie.inventory.types.HandWeapon;
import zombie.characters.BodyDamage.BodyPart;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.input.GameKeyboard;
import zombie.iso.objects.RainManager;
import zombie.inventory.ItemPickerJava;
import zombie.inventory.types.Key;
import zombie.inventory.types.InventoryContainer;
import zombie.Lua.LuaManager;
import zombie.inventory.types.DrainableComboItem;
import zombie.radio.devices.WaveSignalDevice;
import zombie.radio.ZomboidRadio;
import fmod.fmod.FMODSoundEmitter;
import zombie.audio.DummySoundEmitter;
import zombie.ui.UIManager;
import zombie.ui.TextManager;
import zombie.debug.LineDrawer;
import zombie.core.SpriteRenderer;
import zombie.iso.IsoCamera;
import zombie.core.opengl.Shader;
import java.util.Arrays;
import zombie.debug.DebugOptions;
import zombie.core.skinnedmodel.model.VehicleModelInstance;
import fmod.fmod.FMODManager;
import zombie.ai.states.ZombieFallDownState;
import zombie.ai.states.StaggerBackState;
import zombie.SoundManager;
import org.joml.Matrix4fc;
import org.joml.Vector2f;
import se.krka.kahlua.vm.KahluaTable;
import zombie.core.skinnedmodel.animation.AnimationMultiTrack;
import zombie.iso.IsoChunkMap;
import zombie.network.PassengerMap;
import zombie.network.ClientServerMap;
import zombie.util.list.PZArrayUtil;
import zombie.iso.weather.ClimateManager;
import zombie.core.Core;
import org.joml.Matrix3fc;
import zombie.WorldSoundManager;
import zombie.GameTime;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.RenderEffectType;
import zombie.iso.objects.IsoTree;
import zombie.iso.objects.IsoDeadBody;
import zombie.util.Type;
import zombie.iso.objects.IsoWorldInventoryObject;
import java.io.IOException;
import java.util.Map;
import zombie.GameWindow;
import zombie.core.math.PZMath;
import java.nio.ByteBuffer;
import zombie.Lua.LuaEventManager;
import zombie.iso.IsoUtils;
import zombie.characters.IsoPlayer;
import org.joml.Quaternionfc;
import zombie.core.skinnedmodel.animation.AnimationTrack;
import zombie.core.skinnedmodel.model.SkinningData;
import zombie.core.skinnedmodel.advancedanimation.AnimBoneWeight;
import java.util.List;
import zombie.core.physics.Bullet;
import java.util.Collection;
import zombie.characters.IsoGameCharacter;
import zombie.iso.IsoWorld;
import zombie.core.Color;
import zombie.core.Translator;
import zombie.inventory.InventoryItemFactory;
import zombie.core.network.ByteBufferWriter;
import zombie.inventory.CompressIdenticalItems;
import zombie.network.PacketTypes;
import zombie.core.raknet.UdpConnection;
import zombie.iso.IsoGridSquare;
import zombie.inventory.ItemContainer;
import zombie.characters.IsoZombie;
import zombie.core.skinnedmodel.ModelManager;
import zombie.SandboxOptions;
import zombie.core.physics.WorldSimulation;
import org.joml.Vector3fc;
import zombie.network.ServerOptions;
import zombie.network.GameServer;
import zombie.util.StringUtils;
import zombie.core.textures.TextureID;
import zombie.SystemDisabler;
import java.util.Iterator;
import zombie.scripting.ScriptManager;
import zombie.debug.DebugLog;
import zombie.audio.FMODParameter;
import zombie.core.Rand;
import zombie.network.GameClient;
import zombie.iso.IsoCell;
import zombie.iso.Vector2;
import zombie.audio.FMODParameterList;
import zombie.audio.parameters.ParameterVehicleTireMissing;
import zombie.audio.parameters.ParameterVehicleSteer;
import zombie.audio.parameters.ParameterVehicleSpeed;
import zombie.audio.parameters.ParameterVehicleSkid;
import zombie.audio.parameters.ParameterVehicleRPM;
import zombie.audio.parameters.ParameterVehicleRoadMaterial;
import zombie.audio.parameters.ParameterVehicleLoad;
import zombie.audio.parameters.ParameterVehicleGear;
import zombie.audio.parameters.ParameterVehicleEngineCondition;
import zombie.audio.parameters.ParameterVehicleBrake;
import zombie.core.skinnedmodel.animation.AnimationPlayer;
import org.joml.Matrix4f;
import zombie.iso.IsoObject;
import zombie.inventory.InventoryItem;
import java.util.HashMap;
import zombie.core.physics.CarController;
import zombie.scripting.objects.VehicleScript;
import zombie.iso.IsoLightSource;
import zombie.audio.BaseSoundEmitter;
import zombie.core.physics.Transform;
import org.joml.Quaternionf;
import zombie.core.utils.UpdateLimit;
import zombie.iso.IsoChunk;
import org.joml.Vector4f;
import java.util.ArrayList;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import org.joml.Vector3f;
import fmod.fmod.IFMODParameterUpdater;
import zombie.iso.objects.interfaces.Thumpable;
import zombie.iso.IsoMovingObject;

public final class BaseVehicle extends IsoMovingObject implements Thumpable, IFMODParameterUpdater
{
    private long lastCrashTime;
    public static final float RADIUS = 0.3f;
    public static final int FADE_DISTANCE = 15;
    public static final int RANDOMIZE_CONTAINER_CHANCE = 100;
    public static final byte authorizationOnServer = 0;
    public static final byte authorizationSimulation = 1;
    public static final byte authorizationServerSimulation = 2;
    public static final byte authorizationOwner = 3;
    public static final byte authorizationServerOwner = 4;
    private static final Vector3f _UNIT_Y;
    private static final PolygonalMap2.VehiclePoly tempPoly;
    public static final boolean YURI_FORCE_FIELD = false;
    public static boolean RENDER_TO_TEXTURE;
    public static float CENTER_OF_MASS_MAGIC;
    private static final float[] wheelParams;
    private static final float[] physicsParams;
    static final byte POSITION_ORIENTATION_PACKET_SIZE = 102;
    public static Texture vehicleShadow;
    public int justBreakConstraintTimer;
    public BaseVehicle wasTowedBy;
    protected static final ColorInfo inf;
    private static final float[] lowRiderParam;
    private final VehicleImpulse impulseFromServer;
    private final VehicleImpulse[] impulseFromSquishedZombie;
    private final ArrayList<VehicleImpulse> impulseFromHitZombie;
    private final int netPlayerTimeoutMax = 30;
    private final Vector4f tempVector4f;
    public final ArrayList<ModelInfo> models;
    public IsoChunk chunk;
    public boolean polyDirty;
    private boolean polyGarageCheck;
    private float radiusReductionInGarage;
    public short VehicleID;
    public int sqlID;
    public boolean serverRemovedFromWorld;
    public boolean trace;
    public VehicleInterpolation interpolation;
    public boolean waitFullUpdate;
    public float throttle;
    public double engineSpeed;
    public TransmissionNumber transmissionNumber;
    public final UpdateLimit transmissionChangeTime;
    public boolean hasExtendOffset;
    public boolean hasExtendOffsetExiting;
    public float savedPhysicsZ;
    public final Quaternionf savedRot;
    public final Transform jniTransform;
    public float jniSpeed;
    public boolean jniIsCollide;
    public final Vector3f jniLinearVelocity;
    public final Vector3f netLinearVelocity;
    public byte netPlayerAuthorization;
    public short netPlayerId;
    public int netPlayerTimeout;
    public int authSimulationHash;
    public long authSimulationTime;
    public int frontEndDurability;
    public int rearEndDurability;
    public float rust;
    public float colorHue;
    public float colorSaturation;
    public float colorValue;
    public int currentFrontEndDurability;
    public int currentRearEndDurability;
    public float collideX;
    public float collideY;
    public final PolygonalMap2.VehiclePoly shadowCoord;
    public engineStateTypes engineState;
    public long engineLastUpdateStateTime;
    public static final int MAX_WHEELS = 4;
    public static final int PHYSICS_PARAM_COUNT = 27;
    public final WheelInfo[] wheelInfo;
    public boolean skidding;
    public long skidSound;
    public long ramSound;
    public long ramSoundTime;
    private VehicleEngineRPM vehicleEngineRPM;
    public final long[] new_EngineSoundId;
    private long combinedEngineSound;
    public int engineSoundIndex;
    public BaseSoundEmitter hornemitter;
    public float startTime;
    public boolean headlightsOn;
    public boolean stoplightsOn;
    public boolean windowLightsOn;
    public boolean soundHornOn;
    public boolean soundBackMoveOn;
    public final LightbarLightsMode lightbarLightsMode;
    public final LightbarSirenMode lightbarSirenMode;
    private final IsoLightSource leftLight1;
    private final IsoLightSource leftLight2;
    private final IsoLightSource rightLight1;
    private final IsoLightSource rightLight2;
    private int leftLightIndex;
    private int rightLightIndex;
    public final ServerVehicleState[] connectionState;
    protected Passenger[] passengers;
    protected String scriptName;
    protected VehicleScript script;
    protected final ArrayList<VehiclePart> parts;
    protected VehiclePart battery;
    protected int engineQuality;
    protected int engineLoudness;
    protected int enginePower;
    protected long engineCheckTime;
    protected final ArrayList<VehiclePart> lights;
    protected boolean createdModel;
    protected final Vector3f lastLinearVelocity;
    protected int skinIndex;
    protected CarController physics;
    protected boolean bCreated;
    protected final PolygonalMap2.VehiclePoly poly;
    protected final PolygonalMap2.VehiclePoly polyPlusRadius;
    protected boolean bDoDamageOverlay;
    protected boolean loaded;
    protected short updateFlags;
    protected long updateLockTimeout;
    final UpdateLimit limitPhysicSend;
    final UpdateLimit limitPhysicValid;
    public boolean addedToWorld;
    boolean removedFromWorld;
    private float polyPlusRadiusMinX;
    private float polyPlusRadiusMinY;
    private float polyPlusRadiusMaxX;
    private float polyPlusRadiusMaxY;
    private float maxSpeed;
    private boolean keyIsOnDoor;
    private boolean hotwired;
    private boolean hotwiredBroken;
    private boolean keysInIgnition;
    private long soundHorn;
    private long soundScrapePastPlant;
    private long soundBackMoveSignal;
    public long soundSirenSignal;
    private final HashMap<String, String> choosenParts;
    private String type;
    private String respawnZone;
    private float mass;
    private float initialMass;
    private float brakingForce;
    private float baseQuality;
    private float currentSteering;
    private boolean isBraking;
    private int mechanicalID;
    private boolean needPartsUpdate;
    private boolean alarmed;
    private int alarmTime;
    private float alarmAccumulator;
    private double sirenStartTime;
    private boolean mechanicUIOpen;
    private boolean isGoodCar;
    private InventoryItem currentKey;
    private boolean doColor;
    private float brekingSlowFactor;
    private final ArrayList<IsoObject> brekingObjectsList;
    private final UpdateLimit limitUpdate;
    public byte keySpawned;
    public final Matrix4f vehicleTransform;
    public final Matrix4f renderTransform;
    private final Matrix4f tempMatrix4fLWJGL_1;
    private final Quaternionf tempQuat4f;
    private final Transform tempTransform;
    private final Transform tempTransform2;
    private final Transform tempTransform3;
    private BaseSoundEmitter emitter;
    private float brakeBetweenUpdatesSpeed;
    public long physicActiveCheck;
    private long constraintChangedTime;
    private AnimationPlayer m_animPlayer;
    public String specificDistributionId;
    private boolean bAddThumpWorldSound;
    private final SurroundVehicle m_surroundVehicle;
    private boolean regulator;
    private float regulatorSpeed;
    private static final HashMap<String, Integer> s_PartToMaskMap;
    private static final Byte BYTE_ZERO;
    private final HashMap<String, Byte> bloodIntensity;
    private boolean OptionBloodDecals;
    private long createPhysicsTime;
    private BaseVehicle vehicleTowing;
    private BaseVehicle vehicleTowedBy;
    public int constraintTowing;
    private int vehicleTowingID;
    private int vehicleTowedByID;
    private String towAttachmentSelf;
    private String towAttachmentOther;
    private float towConstraintZOffset;
    private final ParameterVehicleBrake parameterVehicleBrake;
    private final ParameterVehicleEngineCondition parameterVehicleEngineCondition;
    private final ParameterVehicleGear parameterVehicleGear;
    private final ParameterVehicleLoad parameterVehicleLoad;
    private final ParameterVehicleRoadMaterial parameterVehicleRoadMaterial;
    private final ParameterVehicleRPM parameterVehicleRPM;
    private final ParameterVehicleSkid parameterVehicleSkid;
    private final ParameterVehicleSpeed parameterVehicleSpeed;
    private final ParameterVehicleSteer parameterVehicleSteer;
    private final ParameterVehicleTireMissing parameterVehicleTireMissing;
    private final FMODParameterList fmodParameters;
    public static final ThreadLocal<Vector2ObjectPool> TL_vector2_pool;
    public static final ThreadLocal<Vector2fObjectPool> TL_vector2f_pool;
    public static final ThreadLocal<Vector3fObjectPool> TL_vector3f_pool;
    public static final ThreadLocal<Matrix4fObjectPool> TL_matrix4f_pool;
    public static final ThreadLocal<QuaternionfObjectPool> TL_quaternionf_pool;
    public static final float PHYSICS_Z_SCALE = 0.82f;
    public static float PLUS_RADIUS;
    private int zombiesHits;
    private long zombieHitTimestamp;
    public static final int MASK1_FRONT = 0;
    public static final int MASK1_REAR = 4;
    public static final int MASK1_DOOR_RIGHT_FRONT = 8;
    public static final int MASK1_DOOR_RIGHT_REAR = 12;
    public static final int MASK1_DOOR_LEFT_FRONT = 1;
    public static final int MASK1_DOOR_LEFT_REAR = 5;
    public static final int MASK1_WINDOW_RIGHT_FRONT = 9;
    public static final int MASK1_WINDOW_RIGHT_REAR = 13;
    public static final int MASK1_WINDOW_LEFT_FRONT = 2;
    public static final int MASK1_WINDOW_LEFT_REAR = 6;
    public static final int MASK1_WINDOW_FRONT = 10;
    public static final int MASK1_WINDOW_REAR = 14;
    public static final int MASK1_GUARD_RIGHT_FRONT = 3;
    public static final int MASK1_GUARD_RIGHT_REAR = 7;
    public static final int MASK1_GUARD_LEFT_FRONT = 11;
    public static final int MASK1_GUARD_LEFT_REAR = 15;
    public static final int MASK2_ROOF = 0;
    public static final int MASK2_LIGHT_RIGHT_FRONT = 4;
    public static final int MASK2_LIGHT_LEFT_FRONT = 8;
    public static final int MASK2_LIGHT_RIGHT_REAR = 12;
    public static final int MASK2_LIGHT_LEFT_REAR = 1;
    public static final int MASK2_BRAKE_RIGHT = 5;
    public static final int MASK2_BRAKE_LEFT = 9;
    public static final int MASK2_LIGHTBAR_RIGHT = 13;
    public static final int MASK2_LIGHTBAR_LEFT = 2;
    public static final int MASK2_HOOD = 6;
    public static final int MASK2_BOOT = 10;
    public float forcedFriction;
    protected final HitVars hitVars;
    
    public int getSqlId() {
        return this.sqlID;
    }
    
    public static Vector2 allocVector2() {
        return BaseVehicle.TL_vector2_pool.get().alloc();
    }
    
    public static void releaseVector2(final Vector2 vector2) {
        BaseVehicle.TL_vector2_pool.get().release(vector2);
    }
    
    private static Vector3f allocVector3f() {
        return BaseVehicle.TL_vector3f_pool.get().alloc();
    }
    
    private static void releaseVector3f(final Vector3f vector3f) {
        BaseVehicle.TL_vector3f_pool.get().release(vector3f);
    }
    
    public BaseVehicle(final IsoCell isoCell) {
        super(isoCell, false);
        this.lastCrashTime = 0L;
        this.justBreakConstraintTimer = 0;
        this.wasTowedBy = null;
        this.impulseFromServer = new VehicleImpulse();
        this.impulseFromSquishedZombie = new VehicleImpulse[4];
        this.impulseFromHitZombie = new ArrayList<VehicleImpulse>();
        this.tempVector4f = new Vector4f();
        this.models = new ArrayList<ModelInfo>();
        this.polyDirty = true;
        this.polyGarageCheck = true;
        this.radiusReductionInGarage = 0.0f;
        this.VehicleID = -1;
        this.sqlID = -1;
        this.serverRemovedFromWorld = false;
        this.trace = false;
        this.interpolation = null;
        this.throttle = 0.0f;
        this.transmissionChangeTime = new UpdateLimit(1000L);
        this.hasExtendOffset = true;
        this.hasExtendOffsetExiting = false;
        this.savedPhysicsZ = Float.NaN;
        this.savedRot = new Quaternionf();
        this.jniTransform = new Transform();
        this.jniLinearVelocity = new Vector3f();
        this.netLinearVelocity = new Vector3f();
        this.netPlayerAuthorization = 0;
        this.netPlayerId = 0;
        this.netPlayerTimeout = 0;
        this.authSimulationHash = 0;
        this.authSimulationTime = 0L;
        this.frontEndDurability = 100;
        this.rearEndDurability = 100;
        this.rust = 0.0f;
        this.colorHue = 0.0f;
        this.colorSaturation = 0.0f;
        this.colorValue = 0.0f;
        this.currentFrontEndDurability = 100;
        this.currentRearEndDurability = 100;
        this.collideX = -1.0f;
        this.collideY = -1.0f;
        this.shadowCoord = new PolygonalMap2.VehiclePoly();
        this.engineState = engineStateTypes.Idle;
        this.wheelInfo = new WheelInfo[4];
        this.skidding = false;
        this.vehicleEngineRPM = null;
        this.new_EngineSoundId = new long[8];
        this.combinedEngineSound = 0L;
        this.engineSoundIndex = 0;
        this.hornemitter = null;
        this.startTime = 0.0f;
        this.headlightsOn = false;
        this.stoplightsOn = false;
        this.windowLightsOn = false;
        this.soundHornOn = false;
        this.soundBackMoveOn = false;
        this.lightbarLightsMode = new LightbarLightsMode();
        this.lightbarSirenMode = new LightbarSirenMode();
        this.leftLight1 = new IsoLightSource(0, 0, 0, 1.0f, 0.0f, 0.0f, 8);
        this.leftLight2 = new IsoLightSource(0, 0, 0, 1.0f, 0.0f, 0.0f, 8);
        this.rightLight1 = new IsoLightSource(0, 0, 0, 0.0f, 0.0f, 1.0f, 8);
        this.rightLight2 = new IsoLightSource(0, 0, 0, 0.0f, 0.0f, 1.0f, 8);
        this.leftLightIndex = -1;
        this.rightLightIndex = -1;
        this.connectionState = new ServerVehicleState[512];
        this.passengers = new Passenger[1];
        this.parts = new ArrayList<VehiclePart>();
        this.lights = new ArrayList<VehiclePart>();
        this.createdModel = false;
        this.lastLinearVelocity = new Vector3f();
        this.skinIndex = -1;
        this.poly = new PolygonalMap2.VehiclePoly();
        this.polyPlusRadius = new PolygonalMap2.VehiclePoly();
        this.bDoDamageOverlay = false;
        this.loaded = false;
        this.updateLockTimeout = 0L;
        this.limitPhysicSend = new UpdateLimit(100L);
        this.limitPhysicValid = new UpdateLimit(1000L);
        this.addedToWorld = false;
        this.removedFromWorld = false;
        this.polyPlusRadiusMinX = -123.0f;
        this.keyIsOnDoor = false;
        this.hotwired = false;
        this.hotwiredBroken = false;
        this.keysInIgnition = false;
        this.soundHorn = -1L;
        this.soundScrapePastPlant = -1L;
        this.soundBackMoveSignal = -1L;
        this.soundSirenSignal = -1L;
        this.choosenParts = new HashMap<String, String>();
        this.type = "";
        this.mass = 0.0f;
        this.initialMass = 0.0f;
        this.brakingForce = 0.0f;
        this.baseQuality = 0.0f;
        this.currentSteering = 0.0f;
        this.isBraking = false;
        this.mechanicalID = 0;
        this.needPartsUpdate = false;
        this.alarmed = false;
        this.alarmTime = -1;
        this.sirenStartTime = 0.0;
        this.mechanicUIOpen = false;
        this.isGoodCar = false;
        this.currentKey = null;
        this.doColor = true;
        this.brekingSlowFactor = 0.0f;
        this.brekingObjectsList = new ArrayList<IsoObject>();
        this.limitUpdate = new UpdateLimit(333L);
        this.keySpawned = 0;
        this.vehicleTransform = new Matrix4f();
        this.renderTransform = new Matrix4f();
        this.tempMatrix4fLWJGL_1 = new Matrix4f();
        this.tempQuat4f = new Quaternionf();
        this.tempTransform = new Transform();
        this.tempTransform2 = new Transform();
        this.tempTransform3 = new Transform();
        this.brakeBetweenUpdatesSpeed = 0.0f;
        this.physicActiveCheck = -1L;
        this.constraintChangedTime = -1L;
        this.m_animPlayer = null;
        this.specificDistributionId = null;
        this.bAddThumpWorldSound = false;
        this.m_surroundVehicle = new SurroundVehicle(this);
        this.regulator = false;
        this.regulatorSpeed = 0.0f;
        this.bloodIntensity = new HashMap<String, Byte>();
        this.OptionBloodDecals = false;
        this.createPhysicsTime = -1L;
        this.vehicleTowing = null;
        this.vehicleTowedBy = null;
        this.constraintTowing = -1;
        this.vehicleTowingID = -1;
        this.vehicleTowedByID = -1;
        this.towAttachmentSelf = null;
        this.towAttachmentOther = null;
        this.towConstraintZOffset = 0.0f;
        this.parameterVehicleBrake = new ParameterVehicleBrake(this);
        this.parameterVehicleEngineCondition = new ParameterVehicleEngineCondition(this);
        this.parameterVehicleGear = new ParameterVehicleGear(this);
        this.parameterVehicleLoad = new ParameterVehicleLoad(this);
        this.parameterVehicleRoadMaterial = new ParameterVehicleRoadMaterial(this);
        this.parameterVehicleRPM = new ParameterVehicleRPM(this);
        this.parameterVehicleSkid = new ParameterVehicleSkid(this);
        this.parameterVehicleSpeed = new ParameterVehicleSpeed(this);
        this.parameterVehicleSteer = new ParameterVehicleSteer(this);
        this.parameterVehicleTireMissing = new ParameterVehicleTireMissing(this);
        this.fmodParameters = new FMODParameterList();
        this.zombiesHits = 0;
        this.zombieHitTimestamp = 0L;
        this.forcedFriction = -1.0f;
        this.hitVars = new HitVars();
        this.setCollidable(false);
        this.respawnZone = new String("");
        this.scriptName = "Base.PickUpTruck";
        this.passengers[0] = new Passenger();
        this.waitFullUpdate = false;
        this.savedRot.w = 1.0f;
        for (int i = 0; i < this.wheelInfo.length; ++i) {
            this.wheelInfo[i] = new WheelInfo();
        }
        if (GameClient.bClient) {
            this.interpolation = new VehicleInterpolation(VehicleManager.physicsDelay);
        }
        this.setKeyId(Rand.Next(100000000));
        this.engineSpeed = 0.0;
        this.transmissionNumber = TransmissionNumber.N;
        this.rust = (float)Rand.Next(0, 2);
        this.jniIsCollide = false;
        for (int j = 0; j < 4; ++j) {
            BaseVehicle.lowRiderParam[j] = 0.0f;
        }
        this.fmodParameters.add(this.parameterVehicleBrake);
        this.fmodParameters.add(this.parameterVehicleEngineCondition);
        this.fmodParameters.add(this.parameterVehicleGear);
        this.fmodParameters.add(this.parameterVehicleLoad);
        this.fmodParameters.add(this.parameterVehicleRPM);
        this.fmodParameters.add(this.parameterVehicleRoadMaterial);
        this.fmodParameters.add(this.parameterVehicleSkid);
        this.fmodParameters.add(this.parameterVehicleSpeed);
        this.fmodParameters.add(this.parameterVehicleSteer);
        this.fmodParameters.add(this.parameterVehicleTireMissing);
    }
    
    public static void LoadAllVehicleTextures() {
        DebugLog.General.println("BaseVehicle.LoadAllVehicleTextures...");
        final Iterator<VehicleScript> iterator = ScriptManager.instance.getAllVehicleScripts().iterator();
        while (iterator.hasNext()) {
            LoadVehicleTextures(iterator.next());
        }
    }
    
    public static void LoadVehicleTextures(final VehicleScript vehicleScript) {
        if (SystemDisabler.doVehiclesWithoutTextures) {
            final VehicleScript.Skin skin = vehicleScript.getSkin(0);
            skin.textureData = LoadVehicleTexture(skin.texture);
            skin.textureDataMask = LoadVehicleTexture("vehicles_placeholder_mask");
            skin.textureDataDamage1Overlay = LoadVehicleTexture("vehicles_placeholder_damage1overlay");
            skin.textureDataDamage1Shell = LoadVehicleTexture("vehicles_placeholder_damage1shell");
            skin.textureDataDamage2Overlay = LoadVehicleTexture("vehicles_placeholder_damage2overlay");
            skin.textureDataDamage2Shell = LoadVehicleTexture("vehicles_placeholder_damage2shell");
            skin.textureDataLights = LoadVehicleTexture("vehicles_placeholder_lights");
            skin.textureDataRust = LoadVehicleTexture("vehicles_placeholder_rust");
        }
        else {
            for (int i = 0; i < vehicleScript.getSkinCount(); ++i) {
                final VehicleScript.Skin skin2 = vehicleScript.getSkin(i);
                skin2.copyMissingFrom(vehicleScript.getTextures());
                LoadVehicleTextures(skin2);
            }
        }
    }
    
    private static void LoadVehicleTextures(final VehicleScript.Skin skin) {
        skin.textureData = LoadVehicleTexture(skin.texture);
        if (skin.textureMask != null) {
            skin.textureDataMask = LoadVehicleTexture(skin.textureMask, 0);
        }
        skin.textureDataDamage1Overlay = LoadVehicleTexture(skin.textureDamage1Overlay);
        skin.textureDataDamage1Shell = LoadVehicleTexture(skin.textureDamage1Shell);
        skin.textureDataDamage2Overlay = LoadVehicleTexture(skin.textureDamage2Overlay);
        skin.textureDataDamage2Shell = LoadVehicleTexture(skin.textureDamage2Shell);
        skin.textureDataLights = LoadVehicleTexture(skin.textureLights);
        skin.textureDataRust = LoadVehicleTexture(skin.textureRust);
        skin.textureDataShadow = LoadVehicleTexture(skin.textureShadow);
    }
    
    public static Texture LoadVehicleTexture(final String s) {
        return LoadVehicleTexture(s, 0x0 | (TextureID.bUseCompression ? 4 : 0));
    }
    
    public static Texture LoadVehicleTexture(final String s, final int n) {
        if (StringUtils.isNullOrWhitespace(s)) {
            return null;
        }
        return Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s), n);
    }
    
    public void setNetPlayerAuthorization(final byte netPlayerAuthorization) {
        this.netPlayerAuthorization = netPlayerAuthorization;
    }
    
    public static float getFakeSpeedModifier() {
        if (GameClient.bClient || GameServer.bServer) {
            return 120.0f / Math.min((float)ServerOptions.instance.SpeedLimit.getValue(), 120.0f);
        }
        return 1.0f;
    }
    
    public boolean isLocalPhysicSim() {
        if (GameServer.bServer) {
            return this.netPlayerAuthorization == 0;
        }
        return this.netPlayerAuthorization == 1 || this.netPlayerAuthorization == 3;
    }
    
    public void addImpulse(final Vector3f vector3f, final Vector3f vector3f2) {
        if (!this.impulseFromServer.enable) {
            this.impulseFromServer.enable = true;
            this.impulseFromServer.impulse.set((Vector3fc)vector3f);
            this.impulseFromServer.rel_pos.set((Vector3fc)vector3f2);
        }
        else if (this.impulseFromServer.impulse.length() < vector3f.length()) {
            this.impulseFromServer.impulse.set((Vector3fc)vector3f);
            this.impulseFromServer.rel_pos.set((Vector3fc)vector3f2);
            this.impulseFromServer.enable = false;
            this.impulseFromServer.release();
        }
    }
    
    public double getEngineSpeed() {
        return this.engineSpeed;
    }
    
    public String getTransmissionNumberLetter() {
        return this.transmissionNumber.getString();
    }
    
    public int getTransmissionNumber() {
        return this.transmissionNumber.getIndex();
    }
    
    public void setClientForce(final float clientForce) {
        this.physics.clientForce = clientForce;
    }
    
    public float getClientForce() {
        return this.physics.clientForce;
    }
    
    private void doVehicleColor() {
        if (!this.isDoColor()) {
            this.colorSaturation = 0.1f;
            this.colorValue = 0.9f;
            return;
        }
        this.colorHue = Rand.Next(0.0f, 0.0f);
        this.colorSaturation = 0.5f;
        this.colorValue = Rand.Next(0.3f, 0.6f);
        final int next = Rand.Next(100);
        if (next < 20) {
            this.colorHue = Rand.Next(0.0f, 0.03f);
            this.colorSaturation = Rand.Next(0.85f, 1.0f);
            this.colorValue = Rand.Next(0.55f, 0.85f);
        }
        else if (next < 32) {
            this.colorHue = Rand.Next(0.55f, 0.61f);
            this.colorSaturation = Rand.Next(0.85f, 1.0f);
            this.colorValue = Rand.Next(0.65f, 0.75f);
        }
        else if (next < 67) {
            this.colorHue = 0.15f;
            this.colorSaturation = Rand.Next(0.0f, 0.1f);
            this.colorValue = Rand.Next(0.7f, 0.8f);
        }
        else if (next < 89) {
            this.colorHue = Rand.Next(0.0f, 1.0f);
            this.colorSaturation = Rand.Next(0.0f, 0.1f);
            this.colorValue = Rand.Next(0.1f, 0.25f);
        }
        else {
            this.colorHue = Rand.Next(0.0f, 1.0f);
            this.colorSaturation = Rand.Next(0.6f, 0.75f);
            this.colorValue = Rand.Next(0.3f, 0.7f);
        }
        if (this.getScript() != null) {
            if (this.getScript().getForcedHue() > -1.0f) {
                this.colorHue = this.getScript().getForcedHue();
            }
            if (this.getScript().getForcedSat() > -1.0f) {
                this.colorSaturation = this.getScript().getForcedSat();
            }
            if (this.getScript().getForcedVal() > -1.0f) {
                this.colorValue = this.getScript().getForcedVal();
            }
        }
    }
    
    @Override
    public String getObjectName() {
        return "Vehicle";
    }
    
    public boolean Serialize() {
        return true;
    }
    
    public void createPhysics() {
        if (!GameClient.bClient && this.VehicleID == -1) {
            this.VehicleID = VehicleIDMap.instance.allocateID();
            if (GameServer.bServer) {
                VehicleManager.instance.registerVehicle(this);
            }
            else {
                VehicleIDMap.instance.put(this.VehicleID, this);
            }
        }
        if (this.script == null) {
            this.setScript(this.scriptName);
        }
        if (this.script == null) {
            return;
        }
        if (this.skinIndex == -1) {
            this.setSkinIndex(Rand.Next(this.getSkinCount()));
        }
        WorldSimulation.instance.create();
        this.jniTransform.origin.set(this.getX() - WorldSimulation.instance.offsetX, Float.isNaN(this.savedPhysicsZ) ? this.getZ() : this.savedPhysicsZ, this.getY() - WorldSimulation.instance.offsetY);
        this.physics = new CarController(this);
        this.savedPhysicsZ = Float.NaN;
        this.createPhysicsTime = System.currentTimeMillis();
        if (!this.bCreated) {
            this.bCreated = true;
            int n = 30;
            if (SandboxOptions.getInstance().RecentlySurvivorVehicles.getValue() == 1) {
                n = 10;
            }
            if (SandboxOptions.getInstance().RecentlySurvivorVehicles.getValue() == 3) {
                n = 50;
            }
            if (Rand.Next(100) < n) {
                this.setGoodCar(true);
            }
        }
        this.createParts();
        this.initParts();
        if (!this.createdModel) {
            ModelManager.instance.addVehicle(this);
            this.createdModel = true;
        }
        this.updateTransform();
        this.lights.clear();
        for (int i = 0; i < this.parts.size(); ++i) {
            final VehiclePart e = this.parts.get(i);
            if (e.getLight() != null) {
                this.lights.add(e);
            }
        }
        this.setMaxSpeed(this.getScript().maxSpeed);
        this.setInitialMass(this.getScript().getMass());
        if (!this.getCell().getVehicles().contains(this) && !this.getCell().addVehicles.contains(this)) {
            this.getCell().addVehicles.add(this);
        }
        this.square = this.getCell().getGridSquare(this.x, this.y, this.z);
        this.randomizeContainers();
        if (this.engineState == engineStateTypes.Running) {
            this.engineDoRunning();
        }
        this.updateTotalMass();
        this.bDoDamageOverlay = true;
        this.updatePartStats();
        this.mechanicalID = Rand.Next(100000);
    }
    
    public int getKeyId() {
        return this.keyId;
    }
    
    public boolean getKeySpawned() {
        return this.keySpawned != 0;
    }
    
    public void putKeyToZombie(final IsoZombie isoZombie) {
        isoZombie.getInventory().AddItem(this.createVehicleKey());
    }
    
    public void putKeyToContainer(final ItemContainer itemContainer, final IsoGridSquare isoGridSquare, final IsoObject isoObject) {
        final InventoryItem vehicleKey = this.createVehicleKey();
        itemContainer.AddItem(vehicleKey);
        if (GameServer.bServer) {
            for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
                if (udpConnection.RelevantTo((float)isoObject.square.x, (float)isoObject.square.y)) {
                    final ByteBufferWriter startPacket = udpConnection.startPacket();
                    PacketTypes.PacketType.AddInventoryItemToContainer.doPacket(startPacket);
                    startPacket.putShort((short)2);
                    startPacket.putInt((int)isoObject.getX());
                    startPacket.putInt((int)isoObject.getY());
                    startPacket.putInt((int)isoObject.getZ());
                    startPacket.putByte((byte)isoGridSquare.getObjects().indexOf(isoObject));
                    startPacket.putByte((byte)isoObject.getContainerIndex(itemContainer));
                    try {
                        CompressIdenticalItems.save(startPacket.bb, vehicleKey);
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    PacketTypes.PacketType.AddInventoryItemToContainer.send(udpConnection);
                }
            }
        }
    }
    
    public void putKeyToWorld(final IsoGridSquare isoGridSquare) {
        isoGridSquare.AddWorldInventoryItem(this.createVehicleKey(), 0.0f, 0.0f, 0.0f);
    }
    
    public void addKeyToWorld() {
        if (this.haveOneDoorUnlocked() && Rand.Next(100) < 30) {
            if (Rand.Next(5) == 0) {
                this.keyIsOnDoor = true;
                this.currentKey = this.createVehicleKey();
            }
            else {
                this.addKeyToGloveBox();
            }
            return;
        }
        if (this.haveOneDoorUnlocked() && Rand.Next(100) < 30) {
            this.keysInIgnition = true;
            this.currentKey = this.createVehicleKey();
            return;
        }
        if (Rand.Next(100) < 50) {
            final IsoGridSquare gridSquare = this.getCell().getGridSquare(this.x, this.y, this.z);
            if (gridSquare != null) {
                this.addKeyToSquare(gridSquare);
            }
        }
    }
    
    public void addKeyToGloveBox() {
        if (this.keySpawned != 0) {
            return;
        }
        if (this.getPartById("GloveBox") != null) {
            this.getPartById("GloveBox").container.addItem(this.createVehicleKey());
            this.keySpawned = 1;
        }
    }
    
    public InventoryItem createVehicleKey() {
        final InventoryItem createItem = InventoryItemFactory.CreateItem("CarKey");
        createItem.setKeyId(this.getKeyId());
        createItem.setName(Translator.getText("IGUI_CarKey", Translator.getText(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getScript().getName()))));
        createItem.setColor(Color.HSBtoRGB(this.colorHue, this.colorSaturation * 0.5f, this.colorValue));
        createItem.setCustomColor(true);
        return createItem;
    }
    
    public boolean addKeyToSquare(final IsoGridSquare isoGridSquare) {
        boolean b = false;
        for (int i = 0; i < 3; ++i) {
            for (int j = isoGridSquare.getX() - 10; j < isoGridSquare.getX() + 10; ++j) {
                for (int k = isoGridSquare.getY() - 10; k < isoGridSquare.getY() + 10; ++k) {
                    final IsoGridSquare gridSquare = IsoWorld.instance.getCell().getGridSquare(j, k, i);
                    if (gridSquare != null) {
                        for (int l = 0; l < gridSquare.getObjects().size(); ++l) {
                            final IsoObject isoObject = gridSquare.getObjects().get(l);
                            if (isoObject.container != null && (isoObject.container.type.equals("counter") || isoObject.container.type.equals("officedrawers") || isoObject.container.type.equals("shelves") || isoObject.container.type.equals("desk"))) {
                                this.putKeyToContainer(isoObject.container, gridSquare, isoObject);
                                b = true;
                                break;
                            }
                        }
                        for (int n = 0; n < gridSquare.getMovingObjects().size(); ++n) {
                            if (gridSquare.getMovingObjects().get(n) instanceof IsoZombie) {
                                ((IsoZombie)gridSquare.getMovingObjects().get(n)).addItemToSpawnAtDeath(this.createVehicleKey());
                                b = true;
                                break;
                            }
                        }
                    }
                    if (b) {
                        break;
                    }
                }
                if (b) {
                    break;
                }
            }
            if (b) {
                break;
            }
        }
        if (Rand.Next(10) < 6) {
            while (!b) {
                final IsoGridSquare gridSquare2 = IsoWorld.instance.getCell().getGridSquare(isoGridSquare.getX() - 10 + Rand.Next(20), isoGridSquare.getY() - 10 + Rand.Next(20), this.z);
                if (gridSquare2 != null && !gridSquare2.isSolid() && !gridSquare2.isSolidTrans() && !gridSquare2.HasTree()) {
                    this.putKeyToWorld(gridSquare2);
                    b = true;
                    break;
                }
            }
        }
        return b;
    }
    
    public void toggleLockedDoor(final VehiclePart vehiclePart, final IsoGameCharacter isoGameCharacter, final boolean b) {
        if (b) {
            if (!this.canLockDoor(vehiclePart, isoGameCharacter)) {
                return;
            }
            vehiclePart.getDoor().setLocked(true);
        }
        else {
            if (!this.canUnlockDoor(vehiclePart, isoGameCharacter)) {
                return;
            }
            vehiclePart.getDoor().setLocked(false);
        }
    }
    
    public boolean canLockDoor(final VehiclePart vehiclePart, final IsoGameCharacter isoGameCharacter) {
        if (vehiclePart == null) {
            return false;
        }
        if (isoGameCharacter == null) {
            return false;
        }
        final VehicleDoor door = vehiclePart.getDoor();
        if (door == null) {
            return false;
        }
        if (door.lockBroken) {
            return false;
        }
        if (door.locked) {
            return false;
        }
        if (this.getSeat(isoGameCharacter) != -1) {
            return true;
        }
        if (isoGameCharacter.getInventory().haveThisKeyId(this.getKeyId()) != null) {
            return true;
        }
        final VehiclePart childWindow = vehiclePart.getChildWindow();
        if (childWindow != null && childWindow.getInventoryItem() == null) {
            return true;
        }
        final VehicleWindow vehicleWindow = (childWindow == null) ? null : childWindow.getWindow();
        return vehicleWindow != null && (vehicleWindow.isOpen() || vehicleWindow.isDestroyed());
    }
    
    public boolean canUnlockDoor(final VehiclePart vehiclePart, final IsoGameCharacter isoGameCharacter) {
        if (vehiclePart == null) {
            return false;
        }
        if (isoGameCharacter == null) {
            return false;
        }
        final VehicleDoor door = vehiclePart.getDoor();
        if (door == null) {
            return false;
        }
        if (door.lockBroken) {
            return false;
        }
        if (!door.locked) {
            return false;
        }
        if (this.getSeat(isoGameCharacter) != -1) {
            return true;
        }
        if (isoGameCharacter.getInventory().haveThisKeyId(this.getKeyId()) != null) {
            return true;
        }
        final VehiclePart childWindow = vehiclePart.getChildWindow();
        if (childWindow != null && childWindow.getInventoryItem() == null) {
            return true;
        }
        final VehicleWindow vehicleWindow = (childWindow == null) ? null : childWindow.getWindow();
        return vehicleWindow != null && (vehicleWindow.isOpen() || vehicleWindow.isDestroyed());
    }
    
    private void initParts() {
        for (int i = 0; i < this.parts.size(); ++i) {
            final VehiclePart vehiclePart = this.parts.get(i);
            final String luaFunction = vehiclePart.getLuaFunction("init");
            if (luaFunction != null) {
                this.callLuaVoid(luaFunction, this, vehiclePart);
            }
        }
    }
    
    public void setGeneralPartCondition(final float n, final float n2) {
        for (int i = 0; i < this.parts.size(); ++i) {
            this.parts.get(i).setGeneralCondition(null, n, n2);
        }
    }
    
    private void createParts() {
        for (int i = 0; i < this.parts.size(); ++i) {
            final VehiclePart vehiclePart = this.parts.get(i);
            final ArrayList<String> itemType = vehiclePart.getItemType();
            if (vehiclePart.bCreated && itemType != null && !itemType.isEmpty() && vehiclePart.getInventoryItem() == null && vehiclePart.getTable("install") == null) {
                vehiclePart.bCreated = false;
            }
            else if ((itemType == null || itemType.isEmpty()) && vehiclePart.getInventoryItem() != null) {
                vehiclePart.item = null;
            }
            if (!vehiclePart.bCreated) {
                vehiclePart.bCreated = true;
                final String luaFunction = vehiclePart.getLuaFunction("create");
                if (luaFunction == null) {
                    vehiclePart.setRandomCondition(null);
                }
                else {
                    this.callLuaVoid(luaFunction, this, vehiclePart);
                    if (vehiclePart.getCondition() == -1) {
                        vehiclePart.setRandomCondition(null);
                    }
                }
            }
        }
        if (this.hasLightbar() && this.getScript().rightSirenCol != null && this.getScript().leftSirenCol != null) {
            final IsoLightSource leftLight1 = this.leftLight1;
            final IsoLightSource leftLight2 = this.leftLight2;
            final float r = this.getScript().leftSirenCol.r;
            leftLight2.r = r;
            leftLight1.r = r;
            final IsoLightSource leftLight3 = this.leftLight1;
            final IsoLightSource leftLight4 = this.leftLight2;
            final float g = this.getScript().leftSirenCol.g;
            leftLight4.g = g;
            leftLight3.g = g;
            final IsoLightSource leftLight5 = this.leftLight1;
            final IsoLightSource leftLight6 = this.leftLight2;
            final float b = this.getScript().leftSirenCol.b;
            leftLight6.b = b;
            leftLight5.b = b;
            final IsoLightSource rightLight1 = this.rightLight1;
            final IsoLightSource rightLight2 = this.rightLight2;
            final float r2 = this.getScript().rightSirenCol.r;
            rightLight2.r = r2;
            rightLight1.r = r2;
            final IsoLightSource rightLight3 = this.rightLight1;
            final IsoLightSource rightLight4 = this.rightLight2;
            final float g2 = this.getScript().rightSirenCol.g;
            rightLight4.g = g2;
            rightLight3.g = g2;
            final IsoLightSource rightLight5 = this.rightLight1;
            final IsoLightSource rightLight6 = this.rightLight2;
            final float b2 = this.getScript().rightSirenCol.b;
            rightLight6.b = b2;
            rightLight5.b = b2;
        }
    }
    
    public CarController getController() {
        return this.physics;
    }
    
    public SurroundVehicle getSurroundVehicle() {
        return this.m_surroundVehicle;
    }
    
    public int getSkinCount() {
        return this.script.getSkinCount();
    }
    
    public int getSkinIndex() {
        return this.skinIndex;
    }
    
    public void setSkinIndex(final int skinIndex) {
        if (skinIndex < 0 || skinIndex > this.getSkinCount()) {
            return;
        }
        this.skinIndex = skinIndex;
    }
    
    public Texture getShadowTexture() {
        if (this.getScript() != null) {
            VehicleScript.Skin skin = this.getScript().getTextures();
            if (this.getSkinIndex() >= 0 && this.getSkinIndex() < this.getScript().getSkinCount()) {
                skin = this.getScript().getSkin(this.getSkinIndex());
            }
            if (skin.textureDataShadow != null) {
                return skin.textureDataShadow;
            }
        }
        if (BaseVehicle.vehicleShadow == null) {
            BaseVehicle.vehicleShadow = Texture.getSharedTexture("media/vehicleShadow.png", 0x0 | (TextureID.bUseCompression ? 4 : 0));
        }
        return BaseVehicle.vehicleShadow;
    }
    
    public VehicleScript getScript() {
        return this.script;
    }
    
    public void setScript(final String scriptName) {
        if (StringUtils.isNullOrWhitespace(scriptName)) {
            return;
        }
        this.scriptName = scriptName;
        final boolean b = this.script != null;
        this.script = ScriptManager.instance.getVehicle(this.scriptName);
        if (this.script == null) {
            final ArrayList<VehicleScript> allVehicleScripts = ScriptManager.instance.getAllVehicleScripts();
            if (!allVehicleScripts.isEmpty()) {
                final ArrayList<VehicleScript> list = new ArrayList<VehicleScript>();
                for (int i = 0; i < allVehicleScripts.size(); ++i) {
                    final VehicleScript e = allVehicleScripts.get(i);
                    if (e.getWheelCount() == 0) {
                        list.add(e);
                        allVehicleScripts.remove(i--);
                    }
                }
                if (((this.loaded && this.parts.isEmpty()) || this.scriptName.contains("Burnt")) && !list.isEmpty()) {
                    this.script = list.get(Rand.Next(list.size()));
                }
                else if (!allVehicleScripts.isEmpty()) {
                    this.script = allVehicleScripts.get(Rand.Next(allVehicleScripts.size()));
                }
                if (this.script != null) {
                    this.scriptName = this.script.getFullName();
                }
            }
        }
        this.battery = null;
        this.models.clear();
        if (this.script != null) {
            this.scriptName = this.script.getFullName();
            final Passenger[] passengers = this.passengers;
            this.passengers = new Passenger[this.script.getPassengerCount()];
            for (int j = 0; j < this.passengers.length; ++j) {
                if (j < passengers.length) {
                    this.passengers[j] = passengers[j];
                }
                else {
                    this.passengers[j] = new Passenger();
                }
            }
            final ArrayList<VehiclePart> list2 = new ArrayList<VehiclePart>();
            list2.addAll(this.parts);
            this.parts.clear();
            for (int k = 0; k < this.script.getPartCount(); ++k) {
                final VehicleScript.Part part = this.script.getPart(k);
                VehiclePart vehiclePart = null;
                for (int l = 0; l < list2.size(); ++l) {
                    final VehiclePart vehiclePart2 = list2.get(l);
                    if (vehiclePart2.getScriptPart() != null && part.id.equals(vehiclePart2.getScriptPart().id)) {
                        vehiclePart = vehiclePart2;
                        break;
                    }
                    if (vehiclePart2.partId != null && part.id.equals(vehiclePart2.partId)) {
                        vehiclePart = vehiclePart2;
                        break;
                    }
                }
                if (vehiclePart == null) {
                    vehiclePart = new VehiclePart(this);
                }
                vehiclePart.setScriptPart(part);
                vehiclePart.category = part.category;
                vehiclePart.specificItem = part.specificItem;
                if (part.container == null || part.container.contentType != null) {
                    vehiclePart.setItemContainer(null);
                }
                else {
                    if (vehiclePart.getItemContainer() == null) {
                        final ItemContainer itemContainer = new ItemContainer(part.id, null, this);
                        vehiclePart.setItemContainer(itemContainer);
                        itemContainer.ID = 0;
                    }
                    vehiclePart.getItemContainer().Capacity = part.container.capacity;
                }
                if (part.door == null) {
                    vehiclePart.door = null;
                }
                else if (vehiclePart.door == null) {
                    (vehiclePart.door = new VehicleDoor(vehiclePart)).init(part.door);
                }
                if (part.window == null) {
                    vehiclePart.window = null;
                }
                else if (vehiclePart.window == null) {
                    (vehiclePart.window = new VehicleWindow(vehiclePart)).init(part.window);
                }
                else {
                    vehiclePart.window.openable = part.window.openable;
                }
                vehiclePart.parent = null;
                if (vehiclePart.children != null) {
                    vehiclePart.children.clear();
                }
                this.parts.add(vehiclePart);
                if ("Battery".equals(vehiclePart.getId())) {
                    this.battery = vehiclePart;
                }
            }
            for (int index = 0; index < this.script.getPartCount(); ++index) {
                final VehiclePart vehiclePart3 = this.parts.get(index);
                final VehicleScript.Part scriptPart = vehiclePart3.getScriptPart();
                if (scriptPart.parent != null) {
                    vehiclePart3.parent = this.getPartById(scriptPart.parent);
                    if (vehiclePart3.parent != null) {
                        vehiclePart3.parent.addChild(vehiclePart3);
                    }
                }
            }
            if (!b && !this.loaded) {
                final int n = 99999;
                this.rearEndDurability = n;
                this.frontEndDurability = n;
            }
            this.frontEndDurability = Math.min(this.frontEndDurability, this.script.getFrontEndHealth());
            this.rearEndDurability = Math.min(this.rearEndDurability, this.script.getRearEndHealth());
            this.currentFrontEndDurability = this.frontEndDurability;
            this.currentRearEndDurability = this.rearEndDurability;
            for (int index2 = 0; index2 < this.script.getPartCount(); ++index2) {
                final VehiclePart vehiclePart4 = this.parts.get(index2);
                vehiclePart4.setInventoryItem(vehiclePart4.item);
            }
        }
        if (!this.loaded || (this.colorHue == 0.0f && this.colorSaturation == 0.0f && this.colorValue == 0.0f)) {
            this.doVehicleColor();
        }
        this.m_surroundVehicle.reset();
    }
    
    public String getScriptName() {
        return this.scriptName;
    }
    
    public void setScriptName(final String scriptName) {
        assert !(!scriptName.contains("."));
        this.scriptName = scriptName;
    }
    
    public void setScript() {
        this.setScript(this.scriptName);
    }
    
    public void scriptReloaded() {
        this.tempTransform2.setIdentity();
        if (this.physics != null) {
            this.getWorldTransform(this.tempTransform2);
            this.tempTransform2.basis.getUnnormalizedRotation(this.savedRot);
            this.breakConstraint(false, false);
            Bullet.removeVehicle(this.VehicleID);
            this.physics = null;
        }
        if (this.createdModel) {
            ModelManager.instance.Remove(this);
            this.createdModel = false;
        }
        this.vehicleEngineRPM = null;
        for (int i = 0; i < this.parts.size(); ++i) {
            final VehiclePart vehiclePart = this.parts.get(i);
            vehiclePart.setInventoryItem(null);
            vehiclePart.bCreated = false;
        }
        this.setScript(this.scriptName);
        this.createPhysics();
        if (this.script != null) {
            for (int j = 0; j < this.passengers.length; ++j) {
                final Passenger passenger = this.passengers[j];
                if (passenger != null && passenger.character != null) {
                    final VehicleScript.Position passengerPosition = this.getPassengerPosition(j, "inside");
                    if (passengerPosition != null) {
                        passenger.offset.set((Vector3fc)passengerPosition.offset);
                    }
                }
            }
        }
        this.polyDirty = true;
        if (this.isEngineRunning()) {
            this.engineDoShuttingDown();
            this.engineState = engineStateTypes.Idle;
        }
        if (this.addedToWorld) {
            PolygonalMap2.instance.removeVehicleFromWorld(this);
            PolygonalMap2.instance.addVehicleToWorld(this);
        }
    }
    
    public String getSkin() {
        if (this.script == null || this.script.getSkinCount() == 0) {
            return "BOGUS";
        }
        if (this.skinIndex < 0 || this.skinIndex >= this.script.getSkinCount()) {
            this.skinIndex = Rand.Next(this.script.getSkinCount());
        }
        return this.script.getSkin(this.skinIndex).texture;
    }
    
    protected ModelInfo setModelVisible(final VehiclePart part, final VehicleScript.Model scriptModel, final boolean b) {
        int i = 0;
        while (i < this.models.size()) {
            final ModelInfo modelInfo = this.models.get(i);
            if (modelInfo.part == part && modelInfo.scriptModel == scriptModel) {
                if (b) {
                    return modelInfo;
                }
                if (modelInfo.m_animPlayer != null) {
                    modelInfo.m_animPlayer.reset();
                    modelInfo.m_animPlayer = null;
                }
                this.models.remove(i);
                if (this.createdModel) {
                    ModelManager.instance.Remove(this);
                    ModelManager.instance.addVehicle(this);
                }
                part.updateFlags |= 0x40;
                this.updateFlags |= 0x40;
                return null;
            }
            else {
                ++i;
            }
        }
        if (b) {
            final ModelInfo e = new ModelInfo();
            e.part = part;
            e.scriptModel = scriptModel;
            e.modelScript = ScriptManager.instance.getModelScript(scriptModel.file);
            e.wheelIndex = part.getWheelIndex();
            this.models.add(e);
            if (this.createdModel) {
                ModelManager.instance.Remove(this);
                ModelManager.instance.addVehicle(this);
            }
            part.updateFlags |= 0x40;
            this.updateFlags |= 0x40;
            return e;
        }
        return null;
    }
    
    protected ModelInfo getModelInfoForPart(final VehiclePart vehiclePart) {
        for (int i = 0; i < this.models.size(); ++i) {
            final ModelInfo modelInfo = this.models.get(i);
            if (modelInfo.part == vehiclePart) {
                return modelInfo;
            }
        }
        return null;
    }
    
    protected VehicleScript.Passenger getScriptPassenger(final int n) {
        if (this.getScript() == null) {
            return null;
        }
        if (n < 0 || n >= this.getScript().getPassengerCount()) {
            return null;
        }
        return this.getScript().getPassenger(n);
    }
    
    public int getMaxPassengers() {
        return this.passengers.length;
    }
    
    public boolean setPassenger(final int n, final IsoGameCharacter character, final Vector3f vector3f) {
        if (n >= 0 && n < this.passengers.length) {
            if (n == 0) {
                this.setNeedPartsUpdate(true);
            }
            this.passengers[n].character = character;
            this.passengers[n].offset.set((Vector3fc)vector3f);
            return true;
        }
        return false;
    }
    
    public boolean clearPassenger(final int n) {
        if (n >= 0 && n < this.passengers.length) {
            this.passengers[n].character = null;
            this.passengers[n].offset.set(0.0f, 0.0f, 0.0f);
            return true;
        }
        return false;
    }
    
    public Passenger getPassenger(final int n) {
        if (n >= 0 && n < this.passengers.length) {
            return this.passengers[n];
        }
        return null;
    }
    
    public IsoGameCharacter getCharacter(final int n) {
        final Passenger passenger = this.getPassenger(n);
        if (passenger != null) {
            return passenger.character;
        }
        return null;
    }
    
    public int getSeat(final IsoGameCharacter isoGameCharacter) {
        for (int i = 0; i < this.getMaxPassengers(); ++i) {
            if (this.getCharacter(i) == isoGameCharacter) {
                return i;
            }
        }
        return -1;
    }
    
    public boolean isDriver(final IsoGameCharacter isoGameCharacter) {
        return this.getSeat(isoGameCharacter) == 0;
    }
    
    public Vector3f getWorldPos(final Vector3f vector3f, final Vector3f vector3f2, final VehicleScript vehicleScript) {
        return this.getWorldPos(vector3f.x, vector3f.y, vector3f.z, vector3f2, vehicleScript);
    }
    
    public Vector3f getWorldPos(final float n, final float n2, final float n3, final Vector3f vector3f, final VehicleScript vehicleScript) {
        final Transform worldTransform = this.getWorldTransform(this.tempTransform);
        worldTransform.origin.set(0.0f, 0.0f, 0.0f);
        vector3f.set(n, n2, n3);
        worldTransform.transform(vector3f);
        vector3f.set(this.jniTransform.origin.x + WorldSimulation.instance.offsetX + vector3f.x, this.jniTransform.origin.z + WorldSimulation.instance.offsetY + vector3f.z, this.jniTransform.origin.y / 2.46f + vector3f.y);
        return vector3f;
    }
    
    public Vector3f getWorldPos(final Vector3f vector3f, final Vector3f vector3f2) {
        return this.getWorldPos(vector3f.x, vector3f.y, vector3f.z, vector3f2, this.getScript());
    }
    
    public Vector3f getWorldPos(final float n, final float n2, final float n3, final Vector3f vector3f) {
        return this.getWorldPos(n, n2, n3, vector3f, this.getScript());
    }
    
    public Vector3f getLocalPos(final Vector3f vector3f, final Vector3f vector3f2) {
        return this.getLocalPos(vector3f.x, vector3f.y, vector3f.z, vector3f2);
    }
    
    public Vector3f getLocalPos(final float n, final float n2, final float n3, final Vector3f vector3f) {
        final Transform worldTransform = this.getWorldTransform(this.tempTransform);
        worldTransform.inverse();
        vector3f.set(n - WorldSimulation.instance.offsetX, 0.0f, n2 - WorldSimulation.instance.offsetY);
        worldTransform.transform(vector3f);
        return vector3f;
    }
    
    public Vector3f getPassengerLocalPos(final int n, final Vector3f vector3f) {
        final Passenger passenger = this.getPassenger(n);
        if (passenger == null) {
            return null;
        }
        return vector3f.set((Vector3fc)this.script.getModel().getOffset()).add((Vector3fc)passenger.offset);
    }
    
    public Vector3f getPassengerWorldPos(final int n, final Vector3f vector3f) {
        final Passenger passenger = this.getPassenger(n);
        if (passenger == null) {
            return null;
        }
        return this.getPassengerPositionWorldPos(passenger.offset.x, passenger.offset.y, passenger.offset.z, vector3f);
    }
    
    public Vector3f getPassengerPositionWorldPos(final VehicleScript.Position position, final Vector3f vector3f) {
        return this.getPassengerPositionWorldPos(position.offset.x, position.offset.y, position.offset.z, vector3f);
    }
    
    public Vector3f getPassengerPositionWorldPos(final float n, final float n2, final float n3, final Vector3f vector3f) {
        vector3f.set((Vector3fc)this.script.getModel().offset);
        vector3f.add(n, n2, n3);
        this.getWorldPos(vector3f.x, vector3f.y, vector3f.z, vector3f);
        vector3f.z = (float)(int)this.getZ();
        return vector3f;
    }
    
    public VehicleScript.Anim getPassengerAnim(final int n, final String s) {
        final VehicleScript.Passenger scriptPassenger = this.getScriptPassenger(n);
        if (scriptPassenger == null) {
            return null;
        }
        for (int i = 0; i < scriptPassenger.anims.size(); ++i) {
            final VehicleScript.Anim anim = scriptPassenger.anims.get(i);
            if (s.equals(anim.id)) {
                return anim;
            }
        }
        return null;
    }
    
    public VehicleScript.Position getPassengerPosition(final int n, final String s) {
        final VehicleScript.Passenger scriptPassenger = this.getScriptPassenger(n);
        if (scriptPassenger == null) {
            return null;
        }
        return scriptPassenger.getPositionById(s);
    }
    
    public VehiclePart getPassengerDoor(final int n) {
        final VehicleScript.Passenger scriptPassenger = this.getScriptPassenger(n);
        if (scriptPassenger == null) {
            return null;
        }
        return this.getPartById(scriptPassenger.door);
    }
    
    public VehiclePart getPassengerDoor2(final int n) {
        final VehicleScript.Passenger scriptPassenger = this.getScriptPassenger(n);
        if (scriptPassenger == null) {
            return null;
        }
        return this.getPartById(scriptPassenger.door2);
    }
    
    public boolean isPositionOnLeftOrRight(float x, final float n) {
        final Vector3f vector3f = BaseVehicle.TL_vector3f_pool.get().alloc();
        this.getLocalPos(x, n, 0.0f, vector3f);
        x = vector3f.x;
        BaseVehicle.TL_vector3f_pool.get().release(vector3f);
        final Vector3f extents = this.script.getExtents();
        final Vector3f centerOfMassOffset = this.script.getCenterOfMassOffset();
        final float n2 = centerOfMassOffset.x - extents.x / 2.0f;
        final float n3 = centerOfMassOffset.x + extents.x / 2.0f;
        return x < n2 * 0.98f || x > n3 * 0.98f;
    }
    
    public boolean haveOneDoorUnlocked() {
        for (int i = 0; i < this.getPartCount(); ++i) {
            final VehiclePart partByIndex = this.getPartByIndex(i);
            if (partByIndex.getDoor() != null && (partByIndex.getId().contains("Left") || partByIndex.getId().contains("Right")) && (!partByIndex.getDoor().isLocked() || partByIndex.getDoor().isOpen())) {
                return true;
            }
        }
        return false;
    }
    
    public String getPassengerArea(final int n) {
        final VehicleScript.Passenger scriptPassenger = this.getScriptPassenger(n);
        if (scriptPassenger == null) {
            return null;
        }
        return scriptPassenger.area;
    }
    
    public void playPassengerAnim(final int n, final String s) {
        this.playPassengerAnim(n, s, this.getCharacter(n));
    }
    
    public void playPassengerAnim(final int n, final String s, final IsoGameCharacter isoGameCharacter) {
        if (isoGameCharacter == null) {
            return;
        }
        final VehicleScript.Anim passengerAnim = this.getPassengerAnim(n, s);
        if (passengerAnim == null) {
            return;
        }
        this.playCharacterAnim(isoGameCharacter, passengerAnim, true);
    }
    
    public void playPassengerSound(final int n, final String s) {
        final VehicleScript.Anim passengerAnim = this.getPassengerAnim(n, s);
        if (passengerAnim == null || passengerAnim.sound == null) {
            return;
        }
        this.playSound(passengerAnim.sound);
    }
    
    public void playPartAnim(final VehiclePart o, final String s) {
        if (!this.parts.contains(o)) {
            return;
        }
        final VehicleScript.Anim animById = o.getAnimById(s);
        if (animById == null || StringUtils.isNullOrWhitespace(animById.anim)) {
            return;
        }
        final ModelInfo modelInfoForPart = this.getModelInfoForPart(o);
        if (modelInfoForPart == null) {
            return;
        }
        final AnimationPlayer animationPlayer = modelInfoForPart.getAnimationPlayer();
        if (animationPlayer == null || !animationPlayer.isReady()) {
            return;
        }
        if (animationPlayer.getMultiTrack().getIndexOfTrack(modelInfoForPart.m_track) != -1) {
            animationPlayer.getMultiTrack().removeTrack(modelInfoForPart.m_track);
        }
        modelInfoForPart.m_track = null;
        final SkinningData skinningData = animationPlayer.getSkinningData();
        if (skinningData != null && !skinningData.AnimationClips.containsKey(animById.anim)) {
            return;
        }
        final AnimationTrack play = animationPlayer.play(animById.anim, animById.bLoop);
        if ((modelInfoForPart.m_track = play) != null) {
            play.setLayerIdx(0);
            play.BlendDelta = 1.0f;
            play.SpeedDelta = animById.rate;
            play.IsPlaying = animById.bAnimate;
            play.reverse = animById.bReverse;
            if (!modelInfoForPart.modelScript.boneWeights.isEmpty()) {
                play.setBoneWeights(modelInfoForPart.modelScript.boneWeights);
                play.initBoneWeights(skinningData);
            }
            if (o.getWindow() != null) {
                play.setCurrentTimeValue(play.getDuration() * o.getWindow().getOpenDelta());
            }
        }
    }
    
    public void playActorAnim(final VehiclePart o, final String s, final IsoGameCharacter isoGameCharacter) {
        if (isoGameCharacter == null) {
            return;
        }
        if (!this.parts.contains(o)) {
            return;
        }
        final VehicleScript.Anim animById = o.getAnimById(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        if (animById == null) {
            return;
        }
        this.playCharacterAnim(isoGameCharacter, animById, !"EngineDoor".equals(o.getId()));
    }
    
    private void playCharacterAnim(final IsoGameCharacter isoGameCharacter, final VehicleScript.Anim anim, final boolean b) {
        isoGameCharacter.PlayAnimUnlooped(anim.anim);
        isoGameCharacter.getSpriteDef().setFrameSpeedPerFrame(anim.rate);
        isoGameCharacter.getLegsSprite().Animate = true;
        final Vector3f forwardVector = this.getForwardVector(BaseVehicle.TL_vector3f_pool.get().alloc());
        if (anim.angle.lengthSquared() != 0.0f) {
            final Matrix4f matrix4f = BaseVehicle.TL_matrix4f_pool.get().alloc();
            matrix4f.rotationXYZ((float)Math.toRadians(anim.angle.x), (float)Math.toRadians(anim.angle.y), (float)Math.toRadians(anim.angle.z));
            forwardVector.rotate((Quaternionfc)matrix4f.getNormalizedRotation(this.tempQuat4f));
            BaseVehicle.TL_matrix4f_pool.get().release(matrix4f);
        }
        final Vector2 vector2 = BaseVehicle.TL_vector2_pool.get().alloc();
        vector2.set(forwardVector.x, forwardVector.z);
        isoGameCharacter.DirectionFromVector(vector2);
        BaseVehicle.TL_vector2_pool.get().release(vector2);
        isoGameCharacter.setForwardDirection(forwardVector.x, forwardVector.z);
        if (isoGameCharacter.getAnimationPlayer() != null) {
            isoGameCharacter.getAnimationPlayer().setTargetAngle(isoGameCharacter.getForwardDirection().getDirection());
            if (b) {
                isoGameCharacter.getAnimationPlayer().setAngleToTarget();
            }
        }
        BaseVehicle.TL_vector3f_pool.get().release(forwardVector);
    }
    
    public void playPartSound(final VehiclePart o, final String s) {
        if (!this.parts.contains(o)) {
            return;
        }
        final VehicleScript.Anim animById = o.getAnimById(s);
        if (animById == null || animById.sound == null) {
            return;
        }
        this.playSound(animById.sound);
    }
    
    public void setCharacterPosition(final IsoGameCharacter isoGameCharacter, final int n, final String s) {
        final VehicleScript.Passenger scriptPassenger = this.getScriptPassenger(n);
        if (scriptPassenger == null) {
            return;
        }
        final VehicleScript.Position positionById = scriptPassenger.getPositionById(s);
        if (positionById == null) {
            return;
        }
        if (this.getCharacter(n) == isoGameCharacter) {
            this.passengers[n].offset.set((Vector3fc)positionById.offset);
        }
        else {
            final Vector3f vector3f = BaseVehicle.TL_vector3f_pool.get().alloc();
            if (positionById.area == null) {
                this.getPassengerPositionWorldPos(positionById, vector3f);
            }
            else {
                final VehicleScript.Area areaById = this.script.getAreaById(positionById.area);
                final Vector2 vector2 = BaseVehicle.TL_vector2_pool.get().alloc();
                final Vector2 areaPositionWorld4PlayerInteract = this.areaPositionWorld4PlayerInteract(areaById, vector2);
                vector3f.x = areaPositionWorld4PlayerInteract.x;
                vector3f.y = areaPositionWorld4PlayerInteract.y;
                vector3f.z = 0.0f;
                BaseVehicle.TL_vector2_pool.get().release(vector2);
            }
            isoGameCharacter.setX(vector3f.x);
            isoGameCharacter.setY(vector3f.y);
            isoGameCharacter.setZ(0.0f);
            BaseVehicle.TL_vector3f_pool.get().release(vector3f);
        }
        if (isoGameCharacter instanceof IsoPlayer && ((IsoPlayer)isoGameCharacter).isLocalPlayer()) {
            ((IsoPlayer)isoGameCharacter).dirtyRecalcGridStackTime = 10.0f;
        }
    }
    
    public void transmitCharacterPosition(final int n, final String s) {
        if (GameClient.bClient) {
            VehicleManager.instance.sendPassengerPosition(this, n, s);
        }
    }
    
    public void setCharacterPositionToAnim(final IsoGameCharacter isoGameCharacter, final int n, final String s) {
        final VehicleScript.Anim passengerAnim = this.getPassengerAnim(n, s);
        if (passengerAnim == null) {
            return;
        }
        if (this.getCharacter(n) == isoGameCharacter) {
            this.passengers[n].offset.set((Vector3fc)passengerAnim.offset);
        }
        else {
            final Vector3f worldPos = this.getWorldPos(passengerAnim.offset, BaseVehicle.TL_vector3f_pool.get().alloc());
            isoGameCharacter.setX(worldPos.x);
            isoGameCharacter.setY(worldPos.y);
            isoGameCharacter.setZ(0.0f);
            BaseVehicle.TL_vector3f_pool.get().release(worldPos);
        }
    }
    
    public int getPassengerSwitchSeatCount(final int n) {
        final VehicleScript.Passenger scriptPassenger = this.getScriptPassenger(n);
        if (scriptPassenger == null) {
            return -1;
        }
        return scriptPassenger.switchSeats.size();
    }
    
    public VehicleScript.Passenger.SwitchSeat getPassengerSwitchSeat(final int n, final int index) {
        final VehicleScript.Passenger scriptPassenger = this.getScriptPassenger(n);
        if (scriptPassenger == null) {
            return null;
        }
        if (index < 0 || index >= scriptPassenger.switchSeats.size()) {
            return null;
        }
        return scriptPassenger.switchSeats.get(index);
    }
    
    private VehicleScript.Passenger.SwitchSeat getSwitchSeat(final int n, final int n2) {
        final VehicleScript.Passenger scriptPassenger = this.getScriptPassenger(n);
        if (scriptPassenger == null) {
            return null;
        }
        for (int i = 0; i < scriptPassenger.switchSeats.size(); ++i) {
            final VehicleScript.Passenger.SwitchSeat switchSeat = scriptPassenger.switchSeats.get(i);
            if (switchSeat.seat == n2 && this.getPartForSeatContainer(n2) != null && this.getPartForSeatContainer(n2).getInventoryItem() != null) {
                return switchSeat;
            }
        }
        return null;
    }
    
    public String getSwitchSeatAnimName(final int n, final int n2) {
        final VehicleScript.Passenger.SwitchSeat switchSeat = this.getSwitchSeat(n, n2);
        if (switchSeat == null) {
            return null;
        }
        return switchSeat.anim;
    }
    
    public float getSwitchSeatAnimRate(final int n, final int n2) {
        final VehicleScript.Passenger.SwitchSeat switchSeat = this.getSwitchSeat(n, n2);
        if (switchSeat == null) {
            return 0.0f;
        }
        return switchSeat.rate;
    }
    
    public String getSwitchSeatSound(final int n, final int n2) {
        final VehicleScript.Passenger.SwitchSeat switchSeat = this.getSwitchSeat(n, n2);
        if (switchSeat == null) {
            return null;
        }
        return switchSeat.sound;
    }
    
    public boolean canSwitchSeat(final int n, final int n2) {
        return this.getSwitchSeat(n, n2) != null;
    }
    
    public void switchSeat(final IsoGameCharacter isoGameCharacter, final int n) {
        final int seat = this.getSeat(isoGameCharacter);
        if (seat == -1) {
            return;
        }
        this.clearPassenger(seat);
        final VehicleScript.Position passengerPosition = this.getPassengerPosition(n, "inside");
        if (passengerPosition == null) {
            final Vector3f vector3f = BaseVehicle.TL_vector3f_pool.get().alloc();
            vector3f.set(0.0f, 0.0f, 0.0f);
            this.setPassenger(n, isoGameCharacter, vector3f);
            BaseVehicle.TL_vector3f_pool.get().release(vector3f);
        }
        else {
            this.setPassenger(n, isoGameCharacter, passengerPosition.offset);
        }
        VehicleManager.instance.sendSwichSeat(this, n, isoGameCharacter);
    }
    
    public void switchSeatRSync(final IsoGameCharacter isoGameCharacter, final int n) {
        final int seat = this.getSeat(isoGameCharacter);
        if (seat == -1) {
            return;
        }
        this.clearPassenger(seat);
        final VehicleScript.Position passengerPosition = this.getPassengerPosition(n, "inside");
        if (passengerPosition == null) {
            final Vector3f vector3f = BaseVehicle.TL_vector3f_pool.get().alloc();
            vector3f.set(0.0f, 0.0f, 0.0f);
            this.setPassenger(n, isoGameCharacter, vector3f);
            BaseVehicle.TL_vector3f_pool.get().release(vector3f);
        }
        else {
            this.setPassenger(n, isoGameCharacter, passengerPosition.offset);
        }
    }
    
    public void playSwitchSeatAnim(final int n, final int n2) {
        final IsoGameCharacter character = this.getCharacter(n);
        if (character == null) {
            return;
        }
        final VehicleScript.Passenger.SwitchSeat switchSeat = this.getSwitchSeat(n, n2);
        if (switchSeat == null) {
            return;
        }
        character.PlayAnimUnlooped(switchSeat.anim);
        character.getSpriteDef().setFrameSpeedPerFrame(switchSeat.rate);
        character.getLegsSprite().Animate = true;
    }
    
    public boolean isSeatOccupied(final int n) {
        final VehiclePart partForSeatContainer = this.getPartForSeatContainer(n);
        return (partForSeatContainer != null && partForSeatContainer.getItemContainer() != null && !partForSeatContainer.getItemContainer().getItems().isEmpty()) || this.getCharacter(n) != null;
    }
    
    public boolean isSeatInstalled(final int n) {
        final VehiclePart partForSeatContainer = this.getPartForSeatContainer(n);
        return partForSeatContainer != null && partForSeatContainer.getInventoryItem() != null;
    }
    
    public int getBestSeat(final IsoGameCharacter isoGameCharacter) {
        if ((int)this.getZ() != (int)isoGameCharacter.getZ()) {
            return -1;
        }
        if (isoGameCharacter.DistTo(this) > 5.0f) {
            return -1;
        }
        final VehicleScript script = this.getScript();
        if (script == null) {
            return -1;
        }
        final Vector3f vector3f = BaseVehicle.TL_vector3f_pool.get().alloc();
        final Vector3f vector3f2 = BaseVehicle.TL_vector3f_pool.get().alloc();
        final Vector2 vector2 = BaseVehicle.TL_vector2_pool.get().alloc();
        for (int i = 0; i < script.getPassengerCount(); ++i) {
            if (!this.isEnterBlocked(isoGameCharacter, i)) {
                if (!this.isSeatOccupied(i)) {
                    final VehicleScript.Position passengerPosition = this.getPassengerPosition(i, "outside");
                    if (passengerPosition != null) {
                        this.getPassengerPositionWorldPos(passengerPosition, vector3f);
                        final float x = vector3f.x;
                        final float y = vector3f.y;
                        this.getPassengerPositionWorldPos(0.0f, passengerPosition.offset.y, passengerPosition.offset.z, vector3f2);
                        vector2.set(vector3f2.x - isoGameCharacter.getX(), vector3f2.y - isoGameCharacter.getY());
                        vector2.normalize();
                        if (vector2.dot(isoGameCharacter.getForwardDirection()) > 0.5f && IsoUtils.DistanceTo(isoGameCharacter.getX(), isoGameCharacter.getY(), x, y) < 1.0f) {
                            BaseVehicle.TL_vector3f_pool.get().release(vector3f);
                            BaseVehicle.TL_vector3f_pool.get().release(vector3f2);
                            BaseVehicle.TL_vector2_pool.get().release(vector2);
                            return i;
                        }
                    }
                    final VehicleScript.Position passengerPosition2 = this.getPassengerPosition(i, "outside2");
                    if (passengerPosition2 != null) {
                        this.getPassengerPositionWorldPos(passengerPosition2, vector3f);
                        final float x2 = vector3f.x;
                        final float y2 = vector3f.y;
                        this.getPassengerPositionWorldPos(0.0f, passengerPosition2.offset.y, passengerPosition2.offset.z, vector3f2);
                        vector2.set(vector3f2.x - isoGameCharacter.getX(), vector3f2.y - isoGameCharacter.getY());
                        vector2.normalize();
                        if (vector2.dot(isoGameCharacter.getForwardDirection()) > 0.5f && IsoUtils.DistanceTo(isoGameCharacter.getX(), isoGameCharacter.getY(), x2, y2) < 1.0f) {
                            BaseVehicle.TL_vector3f_pool.get().release(vector3f);
                            BaseVehicle.TL_vector3f_pool.get().release(vector3f2);
                            BaseVehicle.TL_vector2_pool.get().release(vector2);
                            return i;
                        }
                    }
                }
            }
        }
        BaseVehicle.TL_vector3f_pool.get().release(vector3f);
        BaseVehicle.TL_vector3f_pool.get().release(vector3f2);
        BaseVehicle.TL_vector2_pool.get().release(vector2);
        return -1;
    }
    
    public void updateHasExtendOffsetForExit(final IsoGameCharacter isoGameCharacter) {
        this.hasExtendOffsetExiting = true;
        this.updateHasExtendOffset(isoGameCharacter);
        this.getPoly();
    }
    
    public void updateHasExtendOffsetForExitEnd(final IsoGameCharacter isoGameCharacter) {
        this.hasExtendOffsetExiting = false;
        this.updateHasExtendOffset(isoGameCharacter);
        this.getPoly();
    }
    
    public void updateHasExtendOffset(final IsoGameCharacter isoGameCharacter) {
        this.hasExtendOffset = false;
        this.hasExtendOffsetExiting = false;
    }
    
    public VehiclePart getUseablePart(final IsoGameCharacter isoGameCharacter) {
        return this.getUseablePart(isoGameCharacter, true);
    }
    
    public VehiclePart getUseablePart(final IsoGameCharacter isoGameCharacter, final boolean b) {
        if (isoGameCharacter.getVehicle() != null) {
            return null;
        }
        if ((int)this.getZ() != (int)isoGameCharacter.getZ()) {
            return null;
        }
        if (isoGameCharacter.DistTo(this) > 6.0f) {
            return null;
        }
        final VehicleScript script = this.getScript();
        if (script == null) {
            return null;
        }
        final Vector3f extents = script.getExtents();
        final Vector3f centerOfMassOffset = script.getCenterOfMassOffset();
        final float n = centerOfMassOffset.z - extents.z / 2.0f;
        final float n2 = centerOfMassOffset.z + extents.z / 2.0f;
        final Vector2 vector2 = BaseVehicle.TL_vector2_pool.get().alloc();
        final Vector3f vector3f = BaseVehicle.TL_vector3f_pool.get().alloc();
        for (int i = 0; i < this.parts.size(); ++i) {
            final VehiclePart vehiclePart = this.parts.get(i);
            if (vehiclePart.getArea() != null) {
                if (this.isInArea(vehiclePart.getArea(), isoGameCharacter)) {
                    final String luaFunction = vehiclePart.getLuaFunction("use");
                    if (luaFunction != null) {
                        if (!luaFunction.equals("")) {
                            final VehicleScript.Area areaById = script.getAreaById(vehiclePart.getArea());
                            if (areaById != null) {
                                final Vector2 areaPositionLocal = this.areaPositionLocal(areaById, vector2);
                                if (areaPositionLocal != null) {
                                    float x = 0.0f;
                                    final float n3 = 0.0f;
                                    float y = 0.0f;
                                    if (areaPositionLocal.y >= n2 || areaPositionLocal.y <= n) {
                                        x = areaPositionLocal.x;
                                    }
                                    else {
                                        y = areaPositionLocal.y;
                                    }
                                    if (!b) {
                                        return vehiclePart;
                                    }
                                    this.getWorldPos(x, n3, y, vector3f);
                                    final Vector2 vector3 = vector2;
                                    vector3.set(vector3f.x - isoGameCharacter.getX(), vector3f.y - isoGameCharacter.getY());
                                    vector3.normalize();
                                    if (vector3.dot(isoGameCharacter.getForwardDirection()) > 0.5f && !PolygonalMap2.instance.lineClearCollide(isoGameCharacter.x, isoGameCharacter.y, vector3f.x, vector3f.y, (int)isoGameCharacter.z, this, false, true)) {
                                        BaseVehicle.TL_vector2_pool.get().release(vector2);
                                        BaseVehicle.TL_vector3f_pool.get().release(vector3f);
                                        return vehiclePart;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        BaseVehicle.TL_vector2_pool.get().release(vector2);
        BaseVehicle.TL_vector3f_pool.get().release(vector3f);
        return null;
    }
    
    public VehiclePart getClosestWindow(final IsoGameCharacter isoGameCharacter) {
        if ((int)this.getZ() != (int)isoGameCharacter.getZ()) {
            return null;
        }
        if (isoGameCharacter.DistTo(this) > 5.0f) {
            return null;
        }
        final Vector3f extents = this.script.getExtents();
        final Vector3f centerOfMassOffset = this.script.getCenterOfMassOffset();
        final float n = centerOfMassOffset.z - extents.z / 2.0f;
        final float n2 = centerOfMassOffset.z + extents.z / 2.0f;
        final Vector2 vector2 = BaseVehicle.TL_vector2_pool.get().alloc();
        final Vector3f vector3f = BaseVehicle.TL_vector3f_pool.get().alloc();
        for (int i = 0; i < this.parts.size(); ++i) {
            final VehiclePart vehiclePart = this.parts.get(i);
            if (vehiclePart.getWindow() != null) {
                if (vehiclePart.getArea() != null) {
                    if (this.isInArea(vehiclePart.getArea(), isoGameCharacter)) {
                        final VehicleScript.Area areaById = this.script.getAreaById(vehiclePart.getArea());
                        if (areaById.y >= n2 || areaById.y <= n) {
                            vector3f.set(areaById.x, 0.0f, 0.0f);
                        }
                        else {
                            vector3f.set(0.0f, 0.0f, areaById.y);
                        }
                        this.getWorldPos(vector3f, vector3f);
                        final Vector2 vector3 = vector2;
                        vector3.set(vector3f.x - isoGameCharacter.getX(), vector3f.y - isoGameCharacter.getY());
                        vector3.normalize();
                        if (vector3.dot(isoGameCharacter.getForwardDirection()) > 0.5f) {
                            BaseVehicle.TL_vector2_pool.get().release(vector2);
                            BaseVehicle.TL_vector3f_pool.get().release(vector3f);
                            return vehiclePart;
                        }
                        break;
                    }
                }
            }
        }
        BaseVehicle.TL_vector2_pool.get().release(vector2);
        BaseVehicle.TL_vector3f_pool.get().release(vector3f);
        return null;
    }
    
    public void getFacingPosition(final IsoGameCharacter isoGameCharacter, final Vector2 vector2) {
        final Vector3f localPos = this.getLocalPos(isoGameCharacter.getX(), isoGameCharacter.getY(), isoGameCharacter.getZ(), BaseVehicle.TL_vector3f_pool.get().alloc());
        final Vector3f extents = this.script.getExtents();
        final Vector3f centerOfMassOffset = this.script.getCenterOfMassOffset();
        final float n = centerOfMassOffset.x - extents.x / 2.0f;
        final float n2 = centerOfMassOffset.x + extents.x / 2.0f;
        final float n3 = centerOfMassOffset.z - extents.z / 2.0f;
        final float n4 = centerOfMassOffset.z + extents.z / 2.0f;
        float n5 = 0.0f;
        float n6 = 0.0f;
        if (localPos.x <= 0.0f && localPos.z >= n3 && localPos.z <= n4) {
            n6 = localPos.z;
        }
        else if (localPos.x > 0.0f && localPos.z >= n3 && localPos.z <= n4) {
            n6 = localPos.z;
        }
        else if (localPos.z <= 0.0f && localPos.x >= n && localPos.x <= n2) {
            n5 = localPos.x;
        }
        else if (localPos.z > 0.0f && localPos.x >= n && localPos.x <= n2) {
            n5 = localPos.x;
        }
        this.getWorldPos(n5, 0.0f, n6, localPos);
        vector2.set(localPos.x, localPos.y);
        BaseVehicle.TL_vector3f_pool.get().release(localPos);
    }
    
    public boolean enter(final int n, final IsoGameCharacter isoGameCharacter, final Vector3f vector3f) {
        if (!GameClient.bClient) {
            VehiclesDB2.instance.updateVehicleAndTrailer(this);
        }
        if (isoGameCharacter == null) {
            return false;
        }
        if (isoGameCharacter.getVehicle() != null && !isoGameCharacter.getVehicle().exit(isoGameCharacter)) {
            return false;
        }
        if (this.setPassenger(n, isoGameCharacter, vector3f)) {
            isoGameCharacter.setVehicle(this);
            isoGameCharacter.setCollidable(false);
            if (GameClient.bClient) {
                VehicleManager.instance.sendEnter(this, n, isoGameCharacter);
            }
            if (isoGameCharacter instanceof IsoPlayer && ((IsoPlayer)isoGameCharacter).isLocalPlayer()) {
                ((IsoPlayer)isoGameCharacter).dirtyRecalcGridStackTime = 10.0f;
            }
            return true;
        }
        return false;
    }
    
    public boolean enter(final int n, final IsoGameCharacter isoGameCharacter) {
        if (this.getPartForSeatContainer(n) == null || this.getPartForSeatContainer(n).getInventoryItem() == null) {
            return false;
        }
        final VehicleScript.Position passengerPosition = this.getPassengerPosition(n, "outside");
        return passengerPosition != null && this.enter(n, isoGameCharacter, passengerPosition.offset);
    }
    
    public boolean enterRSync(final int n, final IsoGameCharacter isoGameCharacter, final BaseVehicle vehicle) {
        if (isoGameCharacter == null) {
            return false;
        }
        final VehicleScript.Position passengerPosition = this.getPassengerPosition(n, "inside");
        if (passengerPosition == null) {
            return false;
        }
        if (this.setPassenger(n, isoGameCharacter, passengerPosition.offset)) {
            isoGameCharacter.setVehicle(vehicle);
            isoGameCharacter.setCollidable(false);
            if (GameClient.bClient) {
                LuaEventManager.triggerEvent("OnContainerUpdate");
            }
            return true;
        }
        return false;
    }
    
    public boolean exit(final IsoGameCharacter isoGameCharacter) {
        if (!GameClient.bClient) {
            VehiclesDB2.instance.updateVehicleAndTrailer(this);
        }
        if (isoGameCharacter == null) {
            return false;
        }
        final int seat = this.getSeat(isoGameCharacter);
        if (seat == -1) {
            return false;
        }
        if (this.clearPassenger(seat)) {
            this.enginePower = (int)this.getScript().getEngineForce();
            isoGameCharacter.setVehicle(null);
            isoGameCharacter.savedVehicleSeat = -1;
            isoGameCharacter.setCollidable(true);
            if (GameClient.bClient) {
                VehicleManager.instance.sendExit(this, isoGameCharacter);
            }
            if (this.getDriver() == null && this.soundHornOn) {
                this.onHornStop();
            }
            this.polyGarageCheck = true;
            return this.polyDirty = true;
        }
        return false;
    }
    
    public boolean exitRSync(final IsoGameCharacter isoGameCharacter) {
        if (isoGameCharacter == null) {
            return false;
        }
        final int seat = this.getSeat(isoGameCharacter);
        if (seat == -1) {
            return false;
        }
        if (this.clearPassenger(seat)) {
            isoGameCharacter.setVehicle(null);
            isoGameCharacter.setCollidable(true);
            if (GameClient.bClient) {
                LuaEventManager.triggerEvent("OnContainerUpdate");
            }
            return true;
        }
        return false;
    }
    
    public boolean hasRoof(final int n) {
        final VehicleScript.Passenger scriptPassenger = this.getScriptPassenger(n);
        return scriptPassenger != null && scriptPassenger.hasRoof;
    }
    
    public boolean showPassenger(final int n) {
        final VehicleScript.Passenger scriptPassenger = this.getScriptPassenger(n);
        return scriptPassenger != null && scriptPassenger.showPassenger;
    }
    
    public boolean showPassenger(final IsoGameCharacter isoGameCharacter) {
        return this.showPassenger(this.getSeat(isoGameCharacter));
    }
    
    @Override
    public void save(final ByteBuffer byteBuffer, final boolean b) throws IOException {
        if (this.square != null) {
            final float n = 5.0E-4f;
            this.x = PZMath.clamp(this.x, this.square.x + n, this.square.x + 1 - n);
            this.y = PZMath.clamp(this.y, this.square.y + n, this.square.y + 1 - n);
        }
        super.save(byteBuffer, b);
        final Quaternionf savedRot = this.savedRot;
        final Transform worldTransform = this.getWorldTransform(this.tempTransform);
        byteBuffer.putFloat(worldTransform.origin.y);
        worldTransform.getRotation(savedRot);
        byteBuffer.putFloat(savedRot.x);
        byteBuffer.putFloat(savedRot.y);
        byteBuffer.putFloat(savedRot.z);
        byteBuffer.putFloat(savedRot.w);
        GameWindow.WriteStringUTF(byteBuffer, this.scriptName);
        byteBuffer.putInt(this.skinIndex);
        byteBuffer.put((byte)(this.isEngineRunning() ? 1 : 0));
        byteBuffer.putInt(this.frontEndDurability);
        byteBuffer.putInt(this.rearEndDurability);
        byteBuffer.putInt(this.currentFrontEndDurability);
        byteBuffer.putInt(this.currentRearEndDurability);
        byteBuffer.putInt(this.engineLoudness);
        byteBuffer.putInt(this.engineQuality);
        byteBuffer.putInt(this.keyId);
        byteBuffer.put(this.keySpawned);
        byteBuffer.put((byte)(this.headlightsOn ? 1 : 0));
        byteBuffer.put((byte)(this.bCreated ? 1 : 0));
        byteBuffer.put((byte)(this.soundHornOn ? 1 : 0));
        byteBuffer.put((byte)(this.soundBackMoveOn ? 1 : 0));
        byteBuffer.put((byte)this.lightbarLightsMode.get());
        byteBuffer.put((byte)this.lightbarSirenMode.get());
        byteBuffer.putShort((short)this.parts.size());
        for (int i = 0; i < this.parts.size(); ++i) {
            this.parts.get(i).save(byteBuffer);
        }
        byteBuffer.put((byte)(this.keyIsOnDoor ? 1 : 0));
        byteBuffer.put((byte)(this.hotwired ? 1 : 0));
        byteBuffer.put((byte)(this.hotwiredBroken ? 1 : 0));
        byteBuffer.put((byte)(this.keysInIgnition ? 1 : 0));
        byteBuffer.putFloat(this.rust);
        byteBuffer.putFloat(this.colorHue);
        byteBuffer.putFloat(this.colorSaturation);
        byteBuffer.putFloat(this.colorValue);
        byteBuffer.putInt(this.enginePower);
        byteBuffer.putShort(this.VehicleID);
        GameWindow.WriteString(byteBuffer, null);
        byteBuffer.putInt(this.mechanicalID);
        byteBuffer.put((byte)(this.alarmed ? 1 : 0));
        byteBuffer.putDouble(this.sirenStartTime);
        if (this.getCurrentKey() != null) {
            byteBuffer.put((byte)1);
            this.getCurrentKey().saveWithSize(byteBuffer, false);
        }
        else {
            byteBuffer.put((byte)0);
        }
        byteBuffer.put((byte)this.bloodIntensity.size());
        for (final Map.Entry<String, Byte> entry : this.bloodIntensity.entrySet()) {
            GameWindow.WriteStringUTF(byteBuffer, entry.getKey());
            byteBuffer.put(entry.getValue());
        }
        if (this.vehicleTowingID != -1) {
            byteBuffer.put((byte)1);
            byteBuffer.putInt(this.vehicleTowingID);
            GameWindow.WriteStringUTF(byteBuffer, this.towAttachmentSelf);
            GameWindow.WriteStringUTF(byteBuffer, this.towAttachmentOther);
            byteBuffer.putFloat(this.towConstraintZOffset);
        }
        else {
            byteBuffer.put((byte)0);
        }
    }
    
    @Override
    public void load(final ByteBuffer byteBuffer, final int n, final boolean b) throws IOException {
        super.load(byteBuffer, n, b);
        if (this.z < 0.0f) {
            this.z = 0.0f;
        }
        if (n >= 173) {
            this.savedPhysicsZ = PZMath.clamp(byteBuffer.getFloat(), 0.0f, (int)this.z + 2.4477f);
        }
        this.savedRot.set(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat());
        this.jniTransform.origin.set(this.getX() - WorldSimulation.instance.offsetX, Float.isNaN(this.savedPhysicsZ) ? this.z : this.savedPhysicsZ, this.getY() - WorldSimulation.instance.offsetY);
        this.jniTransform.setRotation(this.savedRot);
        this.scriptName = GameWindow.ReadStringUTF(byteBuffer);
        this.skinIndex = byteBuffer.getInt();
        if (byteBuffer.get() == 1) {
            this.engineState = engineStateTypes.Running;
        }
        this.frontEndDurability = byteBuffer.getInt();
        this.rearEndDurability = byteBuffer.getInt();
        this.currentFrontEndDurability = byteBuffer.getInt();
        this.currentRearEndDurability = byteBuffer.getInt();
        this.engineLoudness = byteBuffer.getInt();
        this.engineQuality = byteBuffer.getInt();
        this.engineQuality = PZMath.clamp(this.engineQuality, 0, 100);
        this.keyId = byteBuffer.getInt();
        this.keySpawned = byteBuffer.get();
        this.headlightsOn = (byteBuffer.get() == 1);
        this.bCreated = (byteBuffer.get() == 1);
        this.soundHornOn = (byteBuffer.get() == 1);
        this.soundBackMoveOn = (byteBuffer.get() == 1);
        this.lightbarLightsMode.set(byteBuffer.get());
        this.lightbarSirenMode.set(byteBuffer.get());
        for (short short1 = byteBuffer.getShort(), n2 = 0; n2 < short1; ++n2) {
            final VehiclePart e = new VehiclePart(this);
            e.load(byteBuffer, n);
            this.parts.add(e);
        }
        if (n >= 112) {
            this.keyIsOnDoor = (byteBuffer.get() == 1);
            this.hotwired = (byteBuffer.get() == 1);
            this.hotwiredBroken = (byteBuffer.get() == 1);
            this.keysInIgnition = (byteBuffer.get() == 1);
        }
        if (n >= 116) {
            this.rust = byteBuffer.getFloat();
            this.colorHue = byteBuffer.getFloat();
            this.colorSaturation = byteBuffer.getFloat();
            this.colorValue = byteBuffer.getFloat();
        }
        if (n >= 117) {
            this.enginePower = byteBuffer.getInt();
        }
        if (n >= 120) {
            byteBuffer.getShort();
        }
        if (n >= 122) {
            GameWindow.ReadString(byteBuffer);
            this.mechanicalID = byteBuffer.getInt();
        }
        if (n >= 124) {
            this.alarmed = (byteBuffer.get() == 1);
        }
        if (n >= 129) {
            this.sirenStartTime = byteBuffer.getDouble();
        }
        if (n >= 133 && byteBuffer.get() == 1) {
            InventoryItem loadItem = null;
            try {
                loadItem = InventoryItem.loadItem(byteBuffer, n);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            if (loadItem != null) {
                this.setCurrentKey(loadItem);
            }
        }
        if (n >= 165) {
            for (byte value = byteBuffer.get(), b2 = 0; b2 < value; ++b2) {
                this.bloodIntensity.put(GameWindow.ReadStringUTF(byteBuffer), byteBuffer.get());
            }
        }
        if (n >= 174) {
            if (byteBuffer.get() == 1) {
                this.vehicleTowingID = byteBuffer.getInt();
                this.towAttachmentSelf = GameWindow.ReadStringUTF(byteBuffer);
                this.towAttachmentOther = GameWindow.ReadStringUTF(byteBuffer);
                this.towConstraintZOffset = byteBuffer.getFloat();
            }
        }
        else if (n >= 172) {
            this.vehicleTowingID = byteBuffer.getInt();
        }
        this.loaded = true;
    }
    
    public void softReset() {
        this.keySpawned = 0;
        this.keyIsOnDoor = false;
        this.keysInIgnition = false;
        this.currentKey = null;
        this.engineState = engineStateTypes.Idle;
        this.randomizeContainers();
    }
    
    public void trySpawnKey() {
        if (GameClient.bClient) {
            return;
        }
        if (this.script == null || this.script.getPartById("Engine") == null) {
            return;
        }
        if (this.keySpawned == 1) {
            return;
        }
        if (SandboxOptions.getInstance().VehicleEasyUse.getValue()) {
            this.addKeyToGloveBox();
            return;
        }
        final VehicleType typeFromName = VehicleType.getTypeFromName(this.getVehicleType());
        if (Rand.Next(100) <= ((typeFromName == null) ? 70 : typeFromName.getChanceToSpawnKey())) {
            this.addKeyToWorld();
        }
        this.keySpawned = 1;
    }
    
    public boolean shouldCollideWithCharacters() {
        if (this.vehicleTowedBy != null) {
            return this.vehicleTowedBy.shouldCollideWithCharacters();
        }
        final float speed2D = this.getSpeed2D();
        return this.isEngineRunning() ? (speed2D > 0.05f) : (speed2D > 1.0f);
    }
    
    public boolean shouldCollideWithObjects() {
        if (this.vehicleTowedBy != null) {
            return this.vehicleTowedBy.shouldCollideWithObjects();
        }
        return this.isEngineRunning();
    }
    
    public void brekingObjects() {
        final boolean shouldCollideWithCharacters = this.shouldCollideWithCharacters();
        final boolean shouldCollideWithObjects = this.shouldCollideWithObjects();
        if (!shouldCollideWithCharacters && !shouldCollideWithObjects) {
            return;
        }
        final Vector3f extents = this.script.getExtents();
        final Vector2 vector2 = BaseVehicle.TL_vector2_pool.get().alloc();
        for (int n = (int)Math.ceil(Math.max(extents.x / 2.0f, extents.z / 2.0f) + 0.3f + 1.0f), i = -n; i < n; ++i) {
            for (int j = -n; j < n; ++j) {
                final IsoGridSquare gridSquare = this.getCell().getGridSquare(this.x + j, this.y + i, this.z);
                if (gridSquare != null) {
                    if (shouldCollideWithObjects) {
                        for (int k = 0; k < gridSquare.getObjects().size(); ++k) {
                            final IsoObject isoObject = gridSquare.getObjects().get(k);
                            if (!(isoObject instanceof IsoWorldInventoryObject)) {
                                Vector2 vector3 = null;
                                if (!this.brekingObjectsList.contains(isoObject) && isoObject != null) {
                                    if (isoObject.getProperties() != null) {
                                        if (isoObject.getProperties().Is("CarSlowFactor")) {
                                            vector3 = this.testCollisionWithObject(isoObject, 0.3f, vector2);
                                        }
                                        if (vector3 != null) {
                                            this.brekingObjectsList.add(isoObject);
                                            if (!GameClient.bClient) {
                                                isoObject.Collision(vector3, this);
                                            }
                                        }
                                        if (isoObject.getProperties().Is("HitByCar")) {
                                            vector3 = this.testCollisionWithObject(isoObject, 0.3f, vector2);
                                        }
                                        if (vector3 != null && !GameClient.bClient) {
                                            isoObject.Collision(vector3, this);
                                        }
                                        this.checkCollisionWithPlant(gridSquare, isoObject, vector2);
                                    }
                                }
                            }
                        }
                    }
                    if (shouldCollideWithCharacters) {
                        for (int l = 0; l < gridSquare.getMovingObjects().size(); ++l) {
                            final IsoMovingObject isoMovingObject = gridSquare.getMovingObjects().get(l);
                            final IsoZombie isoZombie = Type.tryCastTo(isoMovingObject, IsoZombie.class);
                            if (isoZombie != null) {
                                if (isoZombie.isProne()) {
                                    this.testCollisionWithProneCharacter(isoZombie, false);
                                }
                                isoZombie.setVehicle4TestCollision(this);
                            }
                            if (isoMovingObject instanceof IsoPlayer && isoMovingObject != this.getDriver()) {
                                ((IsoPlayer)isoMovingObject).setVehicle4TestCollision(this);
                            }
                        }
                    }
                    if (shouldCollideWithObjects) {
                        for (int index = 0; index < gridSquare.getStaticMovingObjects().size(); ++index) {
                            final IsoDeadBody isoDeadBody = Type.tryCastTo(gridSquare.getStaticMovingObjects().get(index), IsoDeadBody.class);
                            if (isoDeadBody != null) {
                                this.testCollisionWithCorpse(isoDeadBody, true);
                            }
                        }
                    }
                }
            }
        }
        float getVehicleSlowFactor = -999.0f;
        for (int index2 = 0; index2 < this.brekingObjectsList.size(); ++index2) {
            final IsoObject o = this.brekingObjectsList.get(index2);
            if (this.testCollisionWithObject(o, 1.0f, vector2) == null || !o.getSquare().getObjects().contains(o)) {
                this.brekingObjectsList.remove(o);
                o.UnCollision(this);
            }
            else if (getVehicleSlowFactor < o.GetVehicleSlowFactor(this)) {
                getVehicleSlowFactor = o.GetVehicleSlowFactor(this);
            }
        }
        if (getVehicleSlowFactor != -999.0f) {
            this.brekingSlowFactor = PZMath.clamp(getVehicleSlowFactor, 0.0f, 34.0f);
        }
        else {
            this.brekingSlowFactor = 0.0f;
        }
        BaseVehicle.TL_vector2_pool.get().release(vector2);
    }
    
    private void updateVelocityMultiplier() {
        if (this.physics == null || this.getScript() == null) {
            return;
        }
        final Vector3f linearVelocity = this.getLinearVelocity(BaseVehicle.TL_vector3f_pool.get().alloc());
        linearVelocity.y = 0.0f;
        final float length = linearVelocity.length();
        float n = 100000.0f;
        float n2 = 1.0f;
        if (this.getScript().getWheelCount() > 0) {
            if (length > 0.0f && length > 34.0f - this.brekingSlowFactor) {
                n = 34.0f - this.brekingSlowFactor;
                n2 = (34.0f - this.brekingSlowFactor) / length;
            }
        }
        else if (this.getVehicleTowedBy() == null) {
            n = 0.0f;
            n2 = 0.1f;
        }
        Bullet.setVehicleVelocityMultiplier(this.VehicleID, n, n2);
        BaseVehicle.TL_vector3f_pool.get().release(linearVelocity);
    }
    
    private void playScrapePastPlantSound(final IsoGridSquare isoGridSquare) {
        if (this.emitter != null && !this.emitter.isPlaying(this.soundScrapePastPlant)) {
            this.emitter.setPos(isoGridSquare.x + 0.5f, isoGridSquare.y + 0.5f, (float)isoGridSquare.z);
            this.soundScrapePastPlant = this.emitter.playSoundImpl("VehicleScrapePastPlant", isoGridSquare);
        }
    }
    
    private void checkCollisionWithPlant(final IsoGridSquare isoGridSquare, final IsoObject isoObject, final Vector2 vector2) {
        final IsoTree isoTree = Type.tryCastTo(isoObject, IsoTree.class);
        if (isoTree == null && !isoObject.getProperties().Is("Bush")) {
            return;
        }
        final float abs = Math.abs(this.getCurrentSpeedKmHour());
        if (abs <= 1.0f) {
            return;
        }
        final Vector2 testCollisionWithObject = this.testCollisionWithObject(isoObject, 0.3f, vector2);
        if (testCollisionWithObject == null) {
            return;
        }
        if (isoTree != null && isoTree.getSize() == 1) {
            this.ApplyImpulse4Break(isoObject, 0.025f);
            this.playScrapePastPlantSound(isoGridSquare);
            return;
        }
        if (this.isPositionOnLeftOrRight(testCollisionWithObject.x, testCollisionWithObject.y)) {
            this.ApplyImpulse4Break(isoObject, 0.025f);
            this.playScrapePastPlantSound(isoGridSquare);
            return;
        }
        if (abs < 10.0f) {
            this.ApplyImpulse4Break(isoObject, 0.025f);
            this.playScrapePastPlantSound(isoGridSquare);
            return;
        }
        this.ApplyImpulse4Break(isoObject, 0.1f);
        this.playScrapePastPlantSound(isoGridSquare);
    }
    
    public void damageObjects(final float n) {
        if (!this.isEngineRunning()) {
            return;
        }
        final Vector3f extents = this.script.getExtents();
        final Vector2 vector2 = BaseVehicle.TL_vector2_pool.get().alloc();
        for (int n2 = (int)Math.ceil(Math.max(extents.x / 2.0f, extents.z / 2.0f) + 0.3f + 1.0f), i = -n2; i < n2; ++i) {
            for (int j = -n2; j < n2; ++j) {
                final IsoGridSquare gridSquare = this.getCell().getGridSquare(this.x + j, this.y + i, this.z);
                if (gridSquare != null) {
                    for (int k = 0; k < gridSquare.getObjects().size(); ++k) {
                        final IsoObject isoObject = gridSquare.getObjects().get(k);
                        Vector2 vector3 = null;
                        if (isoObject instanceof IsoTree) {
                            vector3 = this.testCollisionWithObject(isoObject, 2.0f, vector2);
                            if (vector3 != null) {
                                isoObject.setRenderEffect(RenderEffectType.Hit_Tree_Shudder);
                            }
                        }
                        if (vector3 == null && isoObject instanceof IsoWindow) {
                            vector3 = this.testCollisionWithObject(isoObject, 1.0f, vector2);
                        }
                        if (vector3 == null && isoObject.sprite != null && (isoObject.sprite.getProperties().Is("HitByCar") || isoObject.sprite.getProperties().Is("CarSlowFactor"))) {
                            vector3 = this.testCollisionWithObject(isoObject, 1.0f, vector2);
                        }
                        if (vector3 == null) {
                            final IsoGridSquare gridSquare2 = this.getCell().getGridSquare(this.x + j, this.y + i, 1.0);
                            if (gridSquare2 != null && gridSquare2.getHasTypes().isSet(IsoObjectType.lightswitch)) {
                                vector3 = this.testCollisionWithObject(isoObject, 1.0f, vector2);
                            }
                        }
                        if (vector3 == null) {
                            final IsoGridSquare gridSquare3 = this.getCell().getGridSquare(this.x + j, this.y + i, 0.0);
                            if (gridSquare3 != null && gridSquare3.getHasTypes().isSet(IsoObjectType.lightswitch)) {
                                vector3 = this.testCollisionWithObject(isoObject, 1.0f, vector2);
                            }
                        }
                        if (vector3 != null) {
                            isoObject.Hit(vector3, this, n);
                        }
                    }
                }
            }
        }
        BaseVehicle.TL_vector2_pool.get().release(vector2);
    }
    
    @Override
    public void update() {
        if (this.removedFromWorld) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, this.VehicleID));
            return;
        }
        if (!this.getCell().vehicles.contains(this)) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(FFLzombie/vehicles/BaseVehicle;)Ljava/lang/String;, this.x, this.y, this));
            this.getCell().getRemoveList().add(this);
            return;
        }
        if (this.chunk == null) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(FFS)Ljava/lang/String;, this.x, this.y, this.VehicleID));
        }
        else if (!this.chunk.vehicles.contains(this)) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(FFS)Ljava/lang/String;, this.x, this.y, this.VehicleID));
            if (GameClient.bClient) {
                VehicleManager.instance.sendReqestGetPosition(this.VehicleID);
            }
        }
        else if (!GameServer.bServer && this.chunk.refs.isEmpty()) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, this.VehicleID));
            this.removeFromWorld();
            return;
        }
        super.update();
        if (GameClient.bClient && (this.netPlayerAuthorization == 1 || this.netPlayerAuthorization == 3) && GameClient.connection != null) {
            this.updatePhysicsNetwork();
        }
        if (this.getVehicleTowing() != null && this.getDriver() != null && this.getVehicleTowing().getPartCount() == 0) {}
        if (this.getVehicleTowedBy() != null && this.getDriver() != null && this.getVehicleTowedBy().getPartCount() == 0) {}
        if (this.physics != null && this.vehicleTowingID != -1 && this.vehicleTowing == null) {
            this.tryReconnectToTowedVehicle();
        }
        boolean b = false;
        boolean b2 = false;
        if (this.getVehicleTowedBy() != null && this.getVehicleTowedBy().getController() != null) {
            b = (this.getVehicleTowedBy() != null && this.getVehicleTowedBy().getController().isEnable);
            b2 = (this.getVehicleTowing() != null && this.getVehicleTowing().getDriver() != null);
        }
        if (this.physics != null) {
            final boolean b3 = this.getDriver() != null || b || b2;
            final long currentTimeMillis = System.currentTimeMillis();
            if (this.constraintChangedTime != -1L) {
                if (this.constraintChangedTime + 3500L < currentTimeMillis) {
                    this.constraintChangedTime = -1L;
                    if (!b3 && this.physicActiveCheck < currentTimeMillis) {
                        this.setPhysicsActive(false);
                    }
                }
            }
            else {
                if (this.physicActiveCheck != -1L && (b3 || !this.physics.isEnable)) {
                    this.physicActiveCheck = -1L;
                }
                if (!b3 && this.physics.isEnable && this.physicActiveCheck != -1L && this.physicActiveCheck < currentTimeMillis) {
                    this.physicActiveCheck = -1L;
                    this.setPhysicsActive(false);
                }
            }
            if (this.getVehicleTowedBy() != null && this.getScript().getWheelCount() > 0) {
                this.physics.updateTrailer();
            }
            else if (this.getDriver() == null) {
                this.physics.checkShouldBeActive();
            }
            this.doAlarm();
            final VehicleImpulse impulseFromServer = this.impulseFromServer;
            if (impulseFromServer != null && impulseFromServer.enable) {
                impulseFromServer.enable = false;
                final float n = 1.0f;
                Bullet.applyCentralForceToVehicle(this.VehicleID, impulseFromServer.impulse.x * n, impulseFromServer.impulse.y * n, impulseFromServer.impulse.z * n);
                final Vector3f cross = impulseFromServer.rel_pos.cross((Vector3fc)impulseFromServer.impulse, (Vector3f)BaseVehicle.TL_vector3f_pool.get().alloc());
                Bullet.applyTorqueToVehicle(this.VehicleID, cross.x * n, cross.y * n, cross.z * n);
                BaseVehicle.TL_vector3f_pool.get().release(cross);
            }
            this.applyImpulseFromHitZombies();
            this.applyImpulseFromProneCharacters();
            if (System.currentTimeMillis() - this.engineCheckTime > 1000 && !GameClient.bClient) {
                this.engineCheckTime = System.currentTimeMillis();
                if (!GameClient.bClient) {
                    if (this.engineState != engineStateTypes.Idle) {
                        int b4 = (int)((int)(this.engineLoudness * this.engineSpeed / 2500.0) * (1.0 + Math.min(this.getEngineSpeed(), 2000.0) / 4000.0));
                        int n2 = 120;
                        if (GameServer.bServer) {
                            n2 *= (int)ServerOptions.getInstance().CarEngineAttractionModifier.getValue();
                            b4 *= (int)ServerOptions.getInstance().CarEngineAttractionModifier.getValue();
                        }
                        if (Rand.Next((int)(n2 * GameTime.instance.getInvMultiplier())) == 0) {
                            WorldSoundManager.instance.addSoundRepeating(this, (int)this.getX(), (int)this.getY(), (int)this.getZ(), Math.max(8, b4), b4 / 40, false);
                        }
                        if (Rand.Next((int)((n2 - 85) * GameTime.instance.getInvMultiplier())) == 0) {
                            WorldSoundManager.instance.addSoundRepeating(this, (int)this.getX(), (int)this.getY(), (int)this.getZ(), Math.max(8, b4 / 2), b4 / 40, false);
                        }
                        if (Rand.Next((int)((n2 - 110) * GameTime.instance.getInvMultiplier())) == 0) {
                            WorldSoundManager.instance.addSoundRepeating(this, (int)this.getX(), (int)this.getY(), (int)this.getZ(), Math.max(8, b4 / 4), b4 / 40, false);
                        }
                        WorldSoundManager.instance.addSoundRepeating(this, (int)this.getX(), (int)this.getY(), (int)this.getZ(), Math.max(8, b4 / 6), b4 / 40, false);
                    }
                    if (this.lightbarSirenMode.isEnable() && this.getBatteryCharge() > 0.0f) {
                        WorldSoundManager.instance.addSoundRepeating(this, (int)this.getX(), (int)this.getY(), (int)this.getZ(), 100, 60, false);
                    }
                }
                if (this.engineState == engineStateTypes.Running && !this.isEngineWorking()) {
                    this.shutOff();
                }
                if (this.engineState == engineStateTypes.Running && this.getPartById("Engine").getCondition() < 50 && Rand.Next(this.getPartById("Engine").getCondition() * 12) == 0) {
                    this.shutOff();
                }
                if (this.engineState == engineStateTypes.Starting) {
                    this.updateEngineStarting();
                }
                if (this.engineState == engineStateTypes.RetryingStarting && System.currentTimeMillis() - this.engineLastUpdateStateTime > 10L) {
                    this.engineDoStarting();
                }
                if (this.engineState == engineStateTypes.StartingSuccess && System.currentTimeMillis() - this.engineLastUpdateStateTime > 500L) {
                    this.engineDoRunning();
                }
                if (this.engineState == engineStateTypes.StartingFailed && System.currentTimeMillis() - this.engineLastUpdateStateTime > 500L) {
                    this.engineDoIdle();
                }
                if (this.engineState == engineStateTypes.StartingFailedNoPower && System.currentTimeMillis() - this.engineLastUpdateStateTime > 500L) {
                    this.engineDoIdle();
                }
                if (this.engineState == engineStateTypes.Stalling && System.currentTimeMillis() - this.engineLastUpdateStateTime > 3000L) {
                    this.engineDoIdle();
                }
                if (this.engineState == engineStateTypes.ShutingDown && System.currentTimeMillis() - this.engineLastUpdateStateTime > 2000L) {
                    this.engineDoIdle();
                }
            }
            if (this.getDriver() == null && !b) {
                this.getController().park();
            }
            this.setX(this.jniTransform.origin.x + WorldSimulation.instance.offsetX);
            this.setY(this.jniTransform.origin.z + WorldSimulation.instance.offsetY);
            this.setZ(0.0f);
            if (this.getCell().getGridSquare(this.x, this.y, this.z) == null && !this.chunk.refs.isEmpty()) {
                final float n3 = 5.0E-4f;
                final int n4 = this.chunk.wx * 10;
                final int n5 = this.chunk.wy * 10;
                final int n6 = n4 + 10;
                final int n7 = n5 + 10;
                final float x = this.x;
                final float y = this.y;
                this.x = Math.max(this.x, n4 + n3);
                this.x = Math.min(this.x, n6 - n3);
                this.y = Math.max(this.y, n5 + n3);
                this.y = Math.min(this.y, n7 - n3);
                this.z = 0.2f;
                final Transform tempTransform = this.tempTransform;
                final Transform tempTransform2 = this.tempTransform2;
                this.getWorldTransform(tempTransform);
                tempTransform2.basis.set((Matrix3fc)tempTransform.basis);
                tempTransform2.origin.set(this.x - WorldSimulation.instance.offsetX, this.z, this.y - WorldSimulation.instance.offsetY);
                this.setWorldTransform(tempTransform2);
                this.current = this.getCell().getGridSquare(this.x, this.y, this.z);
            }
            if (this.current != null && this.current.chunk != null) {
                if (this.current.getChunk() != this.chunk) {
                    assert this.chunk.vehicles.contains(this);
                    this.chunk.vehicles.remove(this);
                    this.chunk = this.current.getChunk();
                    if (!GameServer.bServer && this.chunk.refs.isEmpty()) {
                        DebugLog.log(invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, this.VehicleID));
                    }
                    assert !this.chunk.vehicles.contains(this);
                    this.chunk.vehicles.add(this);
                    IsoChunk.addFromCheckedVehicles(this);
                }
            }
            this.updateTransform();
            if (this.jniIsCollide) {
                this.jniIsCollide = false;
                final Vector3f vector3f = BaseVehicle.TL_vector3f_pool.get().alloc();
                if (GameServer.bServer) {
                    vector3f.set((Vector3fc)this.netLinearVelocity);
                }
                else {
                    vector3f.set((Vector3fc)this.jniLinearVelocity);
                }
                vector3f.negate();
                vector3f.add((Vector3fc)this.lastLinearVelocity);
                vector3f.y = 0.0f;
                float abs = Math.abs(vector3f.length());
                if (abs > 2.0f) {
                    if (this.lastLinearVelocity.length() < 6.0f) {
                        abs /= 3.0f;
                    }
                    this.jniTransform.getRotation(this.tempQuat4f);
                    this.tempQuat4f.invert(this.tempQuat4f);
                    if (this.lastLinearVelocity.rotate((Quaternionfc)this.tempQuat4f).z < 0.0f) {
                        abs *= -1.0f;
                    }
                    if (Core.bDebug) {
                        DebugLog.log(invokedynamic(makeConcatWithConstants:(SFLorg/joml/Vector3f;FF)Ljava/lang/String;, this.VehicleID, this.lastLinearVelocity.length(), vector3f, abs, this.netLinearVelocity.length()));
                    }
                    final Vector3f forwardVector = this.getForwardVector(BaseVehicle.TL_vector3f_pool.get().alloc());
                    final float dot = vector3f.normalize().dot((Vector3fc)forwardVector);
                    BaseVehicle.TL_vector3f_pool.get().release(forwardVector);
                    this.crash(Math.abs(abs * 3.0f), dot > 0.0f);
                    this.damageObjects(Math.abs(abs) * 30.0f);
                }
                BaseVehicle.TL_vector3f_pool.get().release(vector3f);
            }
            if (GameServer.bServer) {
                this.lastLinearVelocity.set((Vector3fc)this.netLinearVelocity);
            }
            else {
                this.lastLinearVelocity.set((Vector3fc)this.jniLinearVelocity);
            }
        }
        if (this.soundHornOn && this.hornemitter != null) {
            this.hornemitter.setPos(this.getX(), this.getY(), this.getZ());
        }
        for (int i = 0; i < this.impulseFromSquishedZombie.length; ++i) {
            final VehicleImpulse vehicleImpulse = this.impulseFromSquishedZombie[i];
            if (vehicleImpulse != null) {
                vehicleImpulse.enable = false;
            }
        }
        this.updateSounds();
        this.brekingObjects();
        if (this.bAddThumpWorldSound) {
            this.bAddThumpWorldSound = false;
            WorldSoundManager.instance.addSound(this, (int)this.x, (int)this.y, (int)this.z, 20, 20, true);
        }
        if (this.script.getLightbar().enable && this.lightbarLightsMode.isEnable() && this.getBatteryCharge() > 0.0f) {
            this.lightbarLightsMode.update();
        }
        this.updateWorldLights();
        for (int j = 0; j < IsoPlayer.numPlayers; ++j) {
            if (this.current == null || !this.current.lighting[j].bCanSee()) {
                this.setTargetAlpha(j, 0.0f);
            }
            final IsoPlayer isoPlayer = IsoPlayer.players[j];
            if (isoPlayer != null && this.DistToSquared(isoPlayer) < 225.0f) {
                this.setTargetAlpha(j, 1.0f);
            }
        }
        for (int k = 0; k < this.getScript().getPassengerCount(); ++k) {
            if (this.getCharacter(k) != null) {
                final Vector3f passengerWorldPos = this.getPassengerWorldPos(k, BaseVehicle.TL_vector3f_pool.get().alloc());
                this.getCharacter(k).setX(passengerWorldPos.x);
                this.getCharacter(k).setY(passengerWorldPos.y);
                this.getCharacter(k).setZ(passengerWorldPos.z * 1.0f);
                BaseVehicle.TL_vector3f_pool.get().release(passengerWorldPos);
            }
        }
        if (this.needPartsUpdate() || this.isMechanicUIOpen()) {
            this.updateParts();
        }
        else {
            this.drainBatteryUpdateHack();
        }
        if (this.engineState == engineStateTypes.Running || b) {
            this.updateBulletStats();
        }
        if (this.bDoDamageOverlay) {
            this.bDoDamageOverlay = false;
            this.doDamageOverlay();
        }
        if (GameClient.bClient) {
            this.checkPhysicsValidWithServer();
        }
        final VehiclePart partById = this.getPartById("GasTank");
        if (partById != null && partById.getContainerContentAmount() > partById.getContainerCapacity()) {
            partById.setContainerContentAmount((float)partById.getContainerCapacity());
        }
        boolean b5 = false;
        for (int l = 0; l < this.getMaxPassengers(); ++l) {
            if (this.getPassenger(l).character != null) {
                b5 = true;
                break;
            }
        }
        if (b5) {
            this.m_surroundVehicle.update();
        }
        if (this.physics != null) {
            Bullet.setVehicleMass(this.VehicleID, this.getFudgedMass());
        }
        this.updateVelocityMultiplier();
    }
    
    private void updateEngineStarting() {
        if (this.getBatteryCharge() <= 0.1f) {
            this.engineDoStartingFailedNoPower();
            return;
        }
        final VehiclePart partById = this.getPartById("GasTank");
        if (partById != null && partById.getContainerContentAmount() <= 0.0f) {
            this.engineDoStartingFailed();
            return;
        }
        int min = 0;
        final float airTemperatureForSquare = ClimateManager.getInstance().getAirTemperatureForSquare(this.getSquare());
        if (this.engineQuality < 65 && airTemperatureForSquare <= 2.0f) {
            min = Math.min((2 - (int)airTemperatureForSquare) * 2, 30);
        }
        if (!SandboxOptions.instance.VehicleEasyUse.getValue() && this.engineQuality < 100 && Rand.Next(this.engineQuality + 50 - min) <= 30) {
            this.engineDoStartingFailed();
            return;
        }
        if (Rand.Next(this.engineQuality) != 0) {
            this.engineDoStartingSuccess();
        }
        else {
            this.engineDoRetryingStarting();
        }
    }
    
    private void applyImpulseFromHitZombies() {
        if (this.impulseFromHitZombie.isEmpty()) {
            return;
        }
        final Vector3f set = BaseVehicle.TL_vector3f_pool.get().alloc().set(0.0f, 0.0f, 0.0f);
        final Vector3f set2 = BaseVehicle.TL_vector3f_pool.get().alloc().set(0.0f, 0.0f, 0.0f);
        final Vector3f set3 = BaseVehicle.TL_vector3f_pool.get().alloc().set(0.0f, 0.0f, 0.0f);
        for (int size = this.impulseFromHitZombie.size(), i = 0; i < size; ++i) {
            final VehicleImpulse vehicleImpulse = this.impulseFromHitZombie.get(i);
            set.add((Vector3fc)vehicleImpulse.impulse);
            set2.add((Vector3fc)vehicleImpulse.rel_pos.cross((Vector3fc)vehicleImpulse.impulse, set3));
            vehicleImpulse.release();
            vehicleImpulse.enable = false;
        }
        this.impulseFromHitZombie.clear();
        final float n = 7.0f * this.getFudgedMass();
        if (set.lengthSquared() > n * n) {
            set.mul(n / set.length());
        }
        final float n2 = 30.0f;
        Bullet.applyCentralForceToVehicle(this.VehicleID, set.x * n2, set.y * n2, set.z * n2);
        Bullet.applyTorqueToVehicle(this.VehicleID, set2.x * n2, set2.y * n2, set2.z * n2);
        if (GameServer.bServer) {}
        BaseVehicle.TL_vector3f_pool.get().release(set);
        BaseVehicle.TL_vector3f_pool.get().release(set2);
        BaseVehicle.TL_vector3f_pool.get().release(set3);
    }
    
    private void applyImpulseFromProneCharacters() {
        if (!PZArrayUtil.contains(this.impulseFromSquishedZombie, vehicleImpulse -> vehicleImpulse != null && vehicleImpulse.enable)) {
            return;
        }
        final Vector3f set = BaseVehicle.TL_vector3f_pool.get().alloc().set(0.0f, 0.0f, 0.0f);
        final Vector3f set2 = BaseVehicle.TL_vector3f_pool.get().alloc().set(0.0f, 0.0f, 0.0f);
        final Vector3f vector3f = BaseVehicle.TL_vector3f_pool.get().alloc();
        for (int i = 0; i < this.impulseFromSquishedZombie.length; ++i) {
            final VehicleImpulse vehicleImpulse2 = this.impulseFromSquishedZombie[i];
            if (vehicleImpulse2 != null && vehicleImpulse2.enable) {
                set.add((Vector3fc)vehicleImpulse2.impulse);
                set2.add((Vector3fc)vehicleImpulse2.rel_pos.cross((Vector3fc)vehicleImpulse2.impulse, vector3f));
                vehicleImpulse2.enable = false;
                vehicleImpulse2.release();
            }
        }
        if (set.lengthSquared() > 0.0f) {
            final float n = this.getFudgedMass() * 0.15f;
            if (set.lengthSquared() > n * n) {
                set.mul(n / set.length());
            }
            final float n2 = 30.0f;
            Bullet.applyCentralForceToVehicle(this.VehicleID, set.x * n2, set.y * n2, set.z * n2);
            Bullet.applyTorqueToVehicle(this.VehicleID, set2.x * n2, set2.y * n2, set2.z * n2);
        }
        BaseVehicle.TL_vector3f_pool.get().release(set);
        BaseVehicle.TL_vector3f_pool.get().release(set2);
        BaseVehicle.TL_vector3f_pool.get().release(vector3f);
    }
    
    public float getFudgedMass() {
        if (this.getScriptName().contains("Trailer")) {
            return this.getMass();
        }
        final BaseVehicle vehicleTowedBy = this.getVehicleTowedBy();
        if (vehicleTowedBy != null && vehicleTowedBy.getDriver() != null && vehicleTowedBy.isEngineRunning()) {
            float a = Math.max(250.0f, vehicleTowedBy.getMass() / 3.7f);
            if (this.getScript().getWheelCount() == 0) {
                a = Math.min(a, 200.0f);
            }
            return a;
        }
        return this.getMass();
    }
    
    private boolean isNullChunk(final int n, final int n2) {
        return IsoWorld.instance.getMetaGrid().isValidChunk(n, n2) && ((GameClient.bClient && !ClientServerMap.isChunkLoaded(n, n2)) || (GameClient.bClient && !PassengerMap.isChunkLoaded(this, n, n2)) || this.getCell().getChunk(n, n2) == null);
    }
    
    public boolean isInvalidChunkAround() {
        final Vector3f linearVelocity = this.getLinearVelocity(BaseVehicle.TL_vector3f_pool.get().alloc());
        final float abs = Math.abs(linearVelocity.x);
        final float abs2 = Math.abs(linearVelocity.z);
        final boolean b = linearVelocity.x < 0.0f && abs > abs2;
        final boolean b2 = linearVelocity.x > 0.0f && abs > abs2;
        final boolean b3 = linearVelocity.z < 0.0f && abs2 > abs;
        final boolean b4 = linearVelocity.z > 0.0f && abs2 > abs;
        BaseVehicle.TL_vector3f_pool.get().release(linearVelocity);
        return this.isInvalidChunkAround(b, b2, b3, b4);
    }
    
    public boolean isInvalidChunkAhead() {
        final Vector3f forwardVector = this.getForwardVector(BaseVehicle.TL_vector3f_pool.get().alloc());
        return this.isInvalidChunkAround(forwardVector.x < -0.5f, forwardVector.x > 0.5f, forwardVector.z < -0.5f, forwardVector.z > 0.5f);
    }
    
    public boolean isInvalidChunkBehind() {
        final Vector3f forwardVector = this.getForwardVector(BaseVehicle.TL_vector3f_pool.get().alloc());
        return this.isInvalidChunkAround(forwardVector.x > 0.5f, forwardVector.x < -0.5f, forwardVector.z > 0.5f, forwardVector.z < -0.5f);
    }
    
    public boolean isInvalidChunkAround(final boolean b, final boolean b2, final boolean b3, final boolean b4) {
        if (IsoChunkMap.ChunkGridWidth > 7) {
            if (b2 && (this.isNullChunk(this.chunk.wx + 1, this.chunk.wy) || this.isNullChunk(this.chunk.wx + 2, this.chunk.wy))) {
                return true;
            }
            if (b && (this.isNullChunk(this.chunk.wx - 1, this.chunk.wy) || this.isNullChunk(this.chunk.wx - 2, this.chunk.wy))) {
                return true;
            }
            if (b4 && (this.isNullChunk(this.chunk.wx, this.chunk.wy + 1) || this.isNullChunk(this.chunk.wx, this.chunk.wy + 2))) {
                return true;
            }
            if (b3 && (this.isNullChunk(this.chunk.wx, this.chunk.wy - 1) || this.isNullChunk(this.chunk.wx, this.chunk.wy - 2))) {
                return true;
            }
        }
        else {
            if (IsoChunkMap.ChunkGridWidth <= 4) {
                return false;
            }
            if (b2 && this.isNullChunk(this.chunk.wx + 1, this.chunk.wy)) {
                return true;
            }
            if (b && this.isNullChunk(this.chunk.wx - 1, this.chunk.wy)) {
                return true;
            }
            if (b4 && this.isNullChunk(this.chunk.wx, this.chunk.wy + 1)) {
                return true;
            }
            if (b3 && this.isNullChunk(this.chunk.wx, this.chunk.wy - 1)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void postupdate() {
        this.current = this.getCell().getGridSquare((int)this.x, (int)this.y, 0);
        if (this.current == null) {
            for (int i = (int)this.z; i >= 0; --i) {
                this.current = this.getCell().getGridSquare((int)this.x, (int)this.y, i);
                if (this.current != null) {
                    break;
                }
            }
        }
        if (this.movingSq != null) {
            this.movingSq.getMovingObjects().remove(this);
            this.movingSq = null;
        }
        if (this.current != null && !this.current.getMovingObjects().contains(this)) {
            this.current.getMovingObjects().add(this);
            this.movingSq = this.current;
        }
        this.square = this.current;
        if (this.sprite.hasActiveModel()) {
            this.updateAnimationPlayer(this.getAnimationPlayer(), null);
            for (int j = 0; j < this.models.size(); ++j) {
                final ModelInfo modelInfo = this.models.get(j);
                this.updateAnimationPlayer(modelInfo.getAnimationPlayer(), modelInfo.part);
            }
        }
    }
    
    protected void updateAnimationPlayer(final AnimationPlayer animationPlayer, final VehiclePart vehiclePart) {
        if (animationPlayer == null || !animationPlayer.isReady()) {
            return;
        }
        final AnimationMultiTrack multiTrack = animationPlayer.getMultiTrack();
        animationPlayer.Update(0.016666668f * 0.8f * GameTime.instance.getUnmoddedMultiplier());
        for (int i = 0; i < multiTrack.getTrackCount(); ++i) {
            final AnimationTrack animationTrack = multiTrack.getTracks().get(i);
            if (animationTrack.IsPlaying && animationTrack.isFinished()) {
                multiTrack.removeTrackAt(i);
                --i;
            }
        }
        if (vehiclePart == null) {
            return;
        }
        final ModelInfo modelInfoForPart = this.getModelInfoForPart(vehiclePart);
        if (modelInfoForPart.m_track != null && multiTrack.getIndexOfTrack(modelInfoForPart.m_track) == -1) {
            modelInfoForPart.m_track = null;
        }
        if (modelInfoForPart.m_track != null) {
            final VehicleWindow window = vehiclePart.getWindow();
            if (window != null) {
                final AnimationTrack track = modelInfoForPart.m_track;
                track.setCurrentTimeValue(track.getDuration() * window.getOpenDelta());
            }
            return;
        }
        final VehicleDoor door = vehiclePart.getDoor();
        if (door != null) {
            this.playPartAnim(vehiclePart, door.isOpen() ? "Opened" : "Closed");
        }
        if (vehiclePart.getWindow() != null) {
            this.playPartAnim(vehiclePart, "ClosedToOpen");
        }
    }
    
    public void saveChange(final String s, final KahluaTable kahluaTable, final ByteBuffer byteBuffer) {
        super.saveChange(s, kahluaTable, byteBuffer);
    }
    
    public void loadChange(final String s, final ByteBuffer byteBuffer) {
        super.loadChange(s, byteBuffer);
    }
    
    public void authorizationClientForecast(final boolean b) {
        if (b && this.getDriver() == null) {
            this.setNetPlayerAuthorization((byte)1);
        }
    }
    
    public void authorizationServerUpdate() {
        if (this.netPlayerAuthorization == 1 && this.netPlayerTimeout-- < 1) {
            this.setNetPlayerAuthorization((byte)0);
            this.netPlayerId = -1;
            this.netPlayerTimeout = 0;
        }
    }
    
    public void authorizationServerCollide(final short netPlayerId, final boolean b) {
        if (this.netPlayerAuthorization == 3) {
            return;
        }
        if (b) {
            this.setNetPlayerAuthorization((byte)1);
            this.netPlayerId = netPlayerId;
            this.netPlayerTimeout = 30;
        }
        else {
            this.setNetPlayerAuthorization((byte)0);
            this.netPlayerId = -1;
            this.netPlayerTimeout = 0;
        }
    }
    
    public void authorizationServerOnSeat() {
        if (this.getDriver() != null) {
            if (this.getVehicleTowedBy() != null) {
                if (this.getVehicleTowedBy().getDriver() != null) {
                    this.setNetPlayerAuthorization((byte)3);
                    this.netPlayerId = this.getVehicleTowedBy().getDriver().getOnlineID();
                    this.netPlayerTimeout = 30;
                }
                else {
                    this.setNetPlayerAuthorization((byte)0);
                    this.netPlayerId = -1;
                }
            }
            else {
                this.setNetPlayerAuthorization((byte)3);
                this.netPlayerId = ((IsoPlayer)this.getDriver()).OnlineID;
                this.netPlayerTimeout = 30;
            }
        }
        else {
            this.setNetPlayerAuthorization((byte)0);
            this.netPlayerId = -1;
        }
    }
    
    public boolean authorizationServerOnOwnerData(final UdpConnection udpConnection) {
        boolean b = false;
        if (this.netPlayerAuthorization == 0) {
            return false;
        }
        for (int i = 0; i < udpConnection.players.length; ++i) {
            if (udpConnection.players[i] != null && udpConnection.players[i].OnlineID == this.netPlayerId) {
                b = true;
                break;
            }
        }
        if (this.getDriver() != null) {
            this.netPlayerTimeout = 30;
        }
        return b;
    }
    
    public void netPlayerServerSendAuthorisation(final ByteBuffer byteBuffer) {
        byteBuffer.put(this.netPlayerAuthorization);
        byteBuffer.putShort(this.netPlayerId);
    }
    
    public void netPlayerFromServerUpdate(final byte b, final short n) {
        if (IsoPlayer.getPlayerIndex() < 0 || IsoPlayer.players[IsoPlayer.getPlayerIndex()] == null || (b == this.netPlayerAuthorization && this.netPlayerId == n)) {
            return;
        }
        if (b == 3) {
            if (n == IsoPlayer.players[IsoPlayer.getPlayerIndex()].OnlineID) {
                this.setNetPlayerAuthorization((byte)3);
                this.netPlayerId = n;
                Bullet.setVehicleStatic(this.VehicleID, false);
                return;
            }
            if (this.netPlayerAuthorization == 4) {
                return;
            }
            this.setNetPlayerAuthorization((byte)4);
            this.netPlayerId = n;
            Bullet.setVehicleStatic(this.VehicleID, true);
        }
        else {
            if (b != 1) {
                this.setNetPlayerAuthorization((byte)0);
                this.netPlayerId = -1;
                Bullet.setVehicleStatic(this.VehicleID, false);
                return;
            }
            if (n == IsoPlayer.players[IsoPlayer.getPlayerIndex()].OnlineID) {
                this.setNetPlayerAuthorization((byte)1);
                this.netPlayerId = n;
                Bullet.setVehicleStatic(this.VehicleID, false);
                return;
            }
            if (this.netPlayerAuthorization == 2) {
                return;
            }
            this.setNetPlayerAuthorization((byte)2);
            this.netPlayerId = n;
            Bullet.setVehicleStatic(this.VehicleID, true);
        }
    }
    
    public Transform getWorldTransform(final Transform transform) {
        transform.set(this.jniTransform);
        return transform;
    }
    
    public void setWorldTransform(final Transform transform) {
        this.jniTransform.set(transform);
        final Quaternionf tempQuat4f = this.tempQuat4f;
        transform.getRotation(tempQuat4f);
        Bullet.teleportVehicle(this.VehicleID, transform.origin.x + WorldSimulation.instance.offsetX, transform.origin.z + WorldSimulation.instance.offsetY, transform.origin.y, tempQuat4f.x, tempQuat4f.y, tempQuat4f.z, tempQuat4f.w);
    }
    
    public void flipUpright() {
        final Transform tempTransform = this.tempTransform;
        tempTransform.set(this.jniTransform);
        final Quaternionf tempQuat4f = this.tempQuat4f;
        tempQuat4f.setAngleAxis(0.0f, BaseVehicle._UNIT_Y.x, BaseVehicle._UNIT_Y.y, BaseVehicle._UNIT_Y.z);
        tempTransform.setRotation(tempQuat4f);
        this.setWorldTransform(tempTransform);
    }
    
    public void setAngles(final float n, final float n2, final float n3) {
        if ((int)n == (int)this.getAngleX() && (int)n2 == (int)this.getAngleY() && n3 == (int)this.getAngleZ()) {
            return;
        }
        this.polyDirty = true;
        this.tempQuat4f.rotationXYZ(n * 0.017453292f, n2 * 0.017453292f, n3 * 0.017453292f);
        this.tempTransform.set(this.jniTransform);
        this.tempTransform.setRotation(this.tempQuat4f);
        this.setWorldTransform(this.tempTransform);
    }
    
    public float getAngleX() {
        final Vector3f vector3f = BaseVehicle.TL_vector3f_pool.get().alloc();
        this.jniTransform.getRotation(this.tempQuat4f).getEulerAnglesXYZ(vector3f);
        final float n = vector3f.x * 57.295776f;
        BaseVehicle.TL_vector3f_pool.get().release(vector3f);
        return n;
    }
    
    public float getAngleY() {
        final Vector3f vector3f = BaseVehicle.TL_vector3f_pool.get().alloc();
        this.jniTransform.getRotation(this.tempQuat4f).getEulerAnglesXYZ(vector3f);
        final float n = vector3f.y * 57.295776f;
        BaseVehicle.TL_vector3f_pool.get().release(vector3f);
        return n;
    }
    
    public float getAngleZ() {
        final Vector3f vector3f = BaseVehicle.TL_vector3f_pool.get().alloc();
        this.jniTransform.getRotation(this.tempQuat4f).getEulerAnglesXYZ(vector3f);
        final float n = vector3f.z * 57.295776f;
        BaseVehicle.TL_vector3f_pool.get().release(vector3f);
        return n;
    }
    
    public void setDebugZ(final float n) {
        this.tempTransform.set(this.jniTransform);
        this.tempTransform.origin.y = PZMath.clamp(n, 0.0f, 1.0f) * 3.0f * 0.82f;
        this.setWorldTransform(this.tempTransform);
    }
    
    public void setPhysicsActive(final boolean isEnable) {
        if (this.physics == null || isEnable == this.physics.isEnable) {
            return;
        }
        this.physics.isEnable = isEnable;
        Bullet.setVehicleActive(this.VehicleID, isEnable);
        if (isEnable) {
            this.physicActiveCheck = System.currentTimeMillis() + 3000L;
        }
    }
    
    public float getDebugZ() {
        return this.jniTransform.origin.y / 2.46f;
    }
    
    public PolygonalMap2.VehiclePoly getPoly() {
        if (this.polyDirty) {
            if (this.polyGarageCheck && this.square != null) {
                if (this.square.getRoom() != null && this.square.getRoom().RoomDef != null && this.square.getRoom().RoomDef.contains("garagestorage")) {
                    this.radiusReductionInGarage = -0.3f;
                }
                else {
                    this.radiusReductionInGarage = 0.0f;
                }
                this.polyGarageCheck = false;
            }
            this.poly.init(this, 0.0f);
            this.polyPlusRadius.init(this, BaseVehicle.PLUS_RADIUS + this.radiusReductionInGarage);
            this.polyDirty = false;
            this.polyPlusRadiusMinX = -123.0f;
            this.initShadowPoly();
        }
        return this.poly;
    }
    
    public PolygonalMap2.VehiclePoly getPolyPlusRadius() {
        if (this.polyDirty) {
            if (this.polyGarageCheck && this.square != null) {
                if (this.square.getRoom() != null && this.square.getRoom().RoomDef != null && this.square.getRoom().RoomDef.contains("garagestorage")) {
                    this.radiusReductionInGarage = -0.3f;
                }
                else {
                    this.radiusReductionInGarage = 0.0f;
                }
                this.polyGarageCheck = false;
            }
            this.poly.init(this, 0.0f);
            this.polyPlusRadius.init(this, BaseVehicle.PLUS_RADIUS + this.radiusReductionInGarage);
            this.polyDirty = false;
            this.polyPlusRadiusMinX = -123.0f;
            this.initShadowPoly();
        }
        return this.polyPlusRadius;
    }
    
    private void initShadowPoly() {
        this.getWorldTransform(this.tempTransform);
        final Quaternionf rotation = this.tempTransform.getRotation(this.tempQuat4f);
        final Vector2f shadowExtents = this.script.getShadowExtents();
        final Vector2f shadowOffset = this.script.getShadowOffset();
        final float n = shadowExtents.x / 2.0f;
        final float n2 = shadowExtents.y / 2.0f;
        final Vector3f vector3f = BaseVehicle.TL_vector3f_pool.get().alloc();
        if (rotation.x < 0.0f) {
            this.getWorldPos(shadowOffset.x - n, 0.0f, shadowOffset.y + n2, vector3f);
            this.shadowCoord.x1 = vector3f.x;
            this.shadowCoord.y1 = vector3f.y;
            this.getWorldPos(shadowOffset.x + n, 0.0f, shadowOffset.y + n2, vector3f);
            this.shadowCoord.x2 = vector3f.x;
            this.shadowCoord.y2 = vector3f.y;
            this.getWorldPos(shadowOffset.x + n, 0.0f, shadowOffset.y - n2, vector3f);
            this.shadowCoord.x3 = vector3f.x;
            this.shadowCoord.y3 = vector3f.y;
            this.getWorldPos(shadowOffset.x - n, 0.0f, shadowOffset.y - n2, vector3f);
            this.shadowCoord.x4 = vector3f.x;
            this.shadowCoord.y4 = vector3f.y;
        }
        else {
            this.getWorldPos(shadowOffset.x - n, 0.0f, shadowOffset.y + n2, vector3f);
            this.shadowCoord.x1 = vector3f.x;
            this.shadowCoord.y1 = vector3f.y;
            this.getWorldPos(shadowOffset.x + n, 0.0f, shadowOffset.y + n2, vector3f);
            this.shadowCoord.x2 = vector3f.x;
            this.shadowCoord.y2 = vector3f.y;
            this.getWorldPos(shadowOffset.x + n, 0.0f, shadowOffset.y - n2, vector3f);
            this.shadowCoord.x3 = vector3f.x;
            this.shadowCoord.y3 = vector3f.y;
            this.getWorldPos(shadowOffset.x - n, 0.0f, shadowOffset.y - n2, vector3f);
            this.shadowCoord.x4 = vector3f.x;
            this.shadowCoord.y4 = vector3f.y;
        }
        BaseVehicle.TL_vector3f_pool.get().release(vector3f);
    }
    
    private void initPolyPlusRadiusBounds() {
        if (this.polyPlusRadiusMinX != -123.0f) {
            return;
        }
        final PolygonalMap2.VehiclePoly polyPlusRadius = this.getPolyPlusRadius();
        final Vector3f vector3f = BaseVehicle.TL_vector3f_pool.get().alloc();
        final Vector3f localPos = this.getLocalPos(polyPlusRadius.x1, polyPlusRadius.y1, polyPlusRadius.z, vector3f);
        final float n = (int)(localPos.x * 100.0f) / 100.0f;
        final float n2 = (int)(localPos.z * 100.0f) / 100.0f;
        final Vector3f localPos2 = this.getLocalPos(polyPlusRadius.x2, polyPlusRadius.y2, polyPlusRadius.z, vector3f);
        final float n3 = (int)(localPos2.x * 100.0f) / 100.0f;
        final float n4 = (int)(localPos2.z * 100.0f) / 100.0f;
        final Vector3f localPos3 = this.getLocalPos(polyPlusRadius.x3, polyPlusRadius.y3, polyPlusRadius.z, vector3f);
        final float n5 = (int)(localPos3.x * 100.0f) / 100.0f;
        final float n6 = (int)(localPos3.z * 100.0f) / 100.0f;
        final Vector3f localPos4 = this.getLocalPos(polyPlusRadius.x4, polyPlusRadius.y4, polyPlusRadius.z, vector3f);
        final float n7 = (int)(localPos4.x * 100.0f) / 100.0f;
        final float n8 = (int)(localPos4.z * 100.0f) / 100.0f;
        this.polyPlusRadiusMinX = Math.min(n, Math.min(n3, Math.min(n5, n7)));
        this.polyPlusRadiusMaxX = Math.max(n, Math.max(n3, Math.max(n5, n7)));
        this.polyPlusRadiusMinY = Math.min(n2, Math.min(n4, Math.min(n6, n8)));
        this.polyPlusRadiusMaxY = Math.max(n2, Math.max(n4, Math.max(n6, n8)));
        BaseVehicle.TL_vector3f_pool.get().release(vector3f);
    }
    
    public Vector3f getForwardVector(final Vector3f vector3f) {
        return this.jniTransform.basis.getColumn(2, vector3f);
    }
    
    public Vector3f getUpVector(final Vector3f vector3f) {
        return this.jniTransform.basis.getColumn(1, vector3f);
    }
    
    public float getUpVectorDot() {
        final Vector3f upVector = this.getUpVector(BaseVehicle.TL_vector3f_pool.get().alloc());
        final float dot = upVector.dot((Vector3fc)BaseVehicle._UNIT_Y);
        BaseVehicle.TL_vector3f_pool.get().release(upVector);
        return dot;
    }
    
    public float getCurrentSpeedKmHour() {
        return this.jniSpeed;
    }
    
    public Vector3f getLinearVelocity(final Vector3f vector3f) {
        if (GameServer.bServer) {
            vector3f.set((Vector3fc)this.netLinearVelocity);
            return vector3f;
        }
        vector3f.set((Vector3fc)this.jniLinearVelocity);
        return vector3f;
    }
    
    public float getSpeed2D() {
        final float n = GameServer.bServer ? this.netLinearVelocity.x : this.jniLinearVelocity.x;
        final float n2 = GameServer.bServer ? this.netLinearVelocity.z : this.jniLinearVelocity.z;
        return (float)Math.sqrt(n * n + n2 * n2);
    }
    
    public boolean isAtRest() {
        if (this.physics == null) {
            return true;
        }
        final float a = GameServer.bServer ? this.netLinearVelocity.y : this.jniLinearVelocity.y;
        return Math.abs(this.physics.EngineForce) < 0.01f && this.getSpeed2D() < 0.02f && Math.abs(a) < 0.5f;
    }
    
    protected void updateTransform() {
        if (this.sprite.modelSlot == null) {
            return;
        }
        final float modelScale = this.getScript().getModelScale();
        float scale = 1.0f;
        if (this.sprite.modelSlot != null && this.sprite.modelSlot.model.scale != 1.0f) {
            scale = this.sprite.modelSlot.model.scale;
        }
        final Transform worldTransform = this.getWorldTransform(this.tempTransform);
        final Quaternionf quaternionf = BaseVehicle.TL_quaternionf_pool.get().alloc();
        final Quaternionf quaternionf2 = BaseVehicle.TL_quaternionf_pool.get().alloc();
        final Matrix4f matrix4f = BaseVehicle.TL_matrix4f_pool.get().alloc();
        worldTransform.getRotation(quaternionf);
        final Quaternionf quaternionf3 = quaternionf;
        quaternionf3.y *= -1.0f;
        final Quaternionf quaternionf4 = quaternionf;
        quaternionf4.z *= -1.0f;
        final Matrix4f value = quaternionf.get(matrix4f);
        float n = 1.0f;
        if (this.sprite.modelSlot.model.m_modelScript != null) {
            n = (this.sprite.modelSlot.model.m_modelScript.invertX ? -1.0f : 1.0f);
        }
        final Vector3f offset = this.script.getModel().getOffset();
        final Vector3f rotate = this.getScript().getModel().getRotate();
        quaternionf2.rotationXYZ(rotate.x * 0.017453292f, rotate.y * 0.017453292f, rotate.z * 0.017453292f);
        this.renderTransform.translationRotateScale(offset.x * -1.0f, offset.y, offset.z, quaternionf2.x, quaternionf2.y, quaternionf2.z, quaternionf2.w, modelScale * scale * n, modelScale * scale, modelScale * scale);
        value.mul((Matrix4fc)this.renderTransform, this.renderTransform);
        this.vehicleTransform.translationRotateScale(offset.x * -1.0f, offset.y, offset.z, 0.0f, 0.0f, 0.0f, 1.0f, modelScale);
        value.mul((Matrix4fc)this.vehicleTransform, this.vehicleTransform);
        for (int i = 0; i < this.models.size(); ++i) {
            final ModelInfo modelInfo = this.models.get(i);
            final VehicleScript.Model scriptModel = modelInfo.scriptModel;
            final Vector3f offset2 = scriptModel.getOffset();
            final Vector3f rotate2 = scriptModel.getRotate();
            final float scale2 = scriptModel.scale;
            float scale3 = 1.0f;
            float n2 = 1.0f;
            if (modelInfo.modelScript != null) {
                scale3 = modelInfo.modelScript.scale;
                n2 = (modelInfo.modelScript.invertX ? -1.0f : 1.0f);
            }
            quaternionf2.rotationXYZ(rotate2.x * 0.017453292f, rotate2.y * 0.017453292f, rotate2.z * 0.017453292f);
            if (modelInfo.wheelIndex == -1) {
                modelInfo.renderTransform.translationRotateScale(offset2.x * -1.0f, offset2.y, offset2.z, quaternionf2.x, quaternionf2.y, quaternionf2.z, quaternionf2.w, scale2 * scale3 * n2, scale2 * scale3, scale2 * scale3);
                this.vehicleTransform.mul((Matrix4fc)modelInfo.renderTransform, modelInfo.renderTransform);
            }
            else {
                final WheelInfo wheelInfo = this.wheelInfo[modelInfo.wheelIndex];
                final float steering = wheelInfo.steering;
                final float rotation = wheelInfo.rotation;
                final VehicleScript.Wheel wheel = this.getScript().getWheel(modelInfo.wheelIndex);
                final VehicleImpulse vehicleImpulse = (modelInfo.wheelIndex < this.impulseFromSquishedZombie.length) ? this.impulseFromSquishedZombie[modelInfo.wheelIndex] : null;
                final float n3 = (vehicleImpulse != null && vehicleImpulse.enable) ? 0.05f : 0.0f;
                final Matrix4f matrix4f2 = matrix4f;
                if (wheelInfo.suspensionLength == 0.0f) {
                    matrix4f2.translation(wheel.offset.x / modelScale * -1.0f, wheel.offset.y / modelScale, wheel.offset.z / modelScale);
                }
                else {
                    matrix4f2.translation(wheel.offset.x / modelScale * -1.0f, (wheel.offset.y + this.script.getSuspensionRestLength() - wheelInfo.suspensionLength) / modelScale + n3 * 0.5f, wheel.offset.z / modelScale);
                }
                modelInfo.renderTransform.identity();
                modelInfo.renderTransform.mul((Matrix4fc)matrix4f2);
                modelInfo.renderTransform.rotateY(steering * -1.0f);
                modelInfo.renderTransform.rotateX(rotation);
                matrix4f.translationRotateScale(offset2.x * -1.0f, offset2.y, offset2.z, quaternionf2.x, quaternionf2.y, quaternionf2.z, quaternionf2.w, scale2 * scale3 * n2, scale2 * scale3, scale2 * scale3);
                modelInfo.renderTransform.mul((Matrix4fc)matrix4f);
                this.vehicleTransform.mul((Matrix4fc)modelInfo.renderTransform, modelInfo.renderTransform);
            }
        }
        BaseVehicle.TL_matrix4f_pool.get().release(matrix4f);
        BaseVehicle.TL_quaternionf_pool.get().release(quaternionf);
        BaseVehicle.TL_quaternionf_pool.get().release(quaternionf2);
    }
    
    public void serverUpdateSimulatorState() {
        if (Math.abs(this.physics.clientForce) > 0.01f && !this.physics.isEnable) {
            Bullet.setVehicleActive(this.VehicleID, true);
            this.physics.isEnable = true;
        }
        if (this.physics.isEnable && Math.abs(this.physics.clientForce) < 0.01f && this.isAtRest()) {
            Bullet.setVehicleActive(this.VehicleID, false);
            this.physics.isEnable = false;
        }
    }
    
    public void updatePhysics() {
        this.physics.update();
    }
    
    public void updatePhysicsNetwork() {
        if (this.limitPhysicSend.Check()) {
            VehicleManager.instance.sendPhysic(this);
        }
    }
    
    public void checkPhysicsValidWithServer() {
        if (this.limitPhysicValid.Check()) {
            final float[] physicsParams = BaseVehicle.physicsParams;
            final float n = 0.05f;
            if (Bullet.getOwnVehiclePhysics(this.VehicleID, physicsParams) == 0 && (Math.abs(physicsParams[0] - this.x) > n || Math.abs(physicsParams[1] - this.y) > n)) {
                VehicleManager.instance.sendReqestGetPosition(this.VehicleID);
            }
        }
    }
    
    public void updateControls() {
        if (this.getController() == null) {
            return;
        }
        if (!this.isOperational()) {
            return;
        }
        final IsoPlayer isoPlayer = Type.tryCastTo(this.getDriver(), IsoPlayer.class);
        if (isoPlayer != null && isoPlayer.isBlockMovement()) {
            return;
        }
        this.getController().updateControls();
    }
    
    public boolean isKeyboardControlled() {
        final IsoGameCharacter character = this.getCharacter(0);
        return character != null && character == IsoPlayer.players[0] && this.getVehicleTowedBy() == null;
    }
    
    public int getJoypad() {
        final IsoGameCharacter character = this.getCharacter(0);
        if (character != null && character instanceof IsoPlayer) {
            return ((IsoPlayer)character).JoypadBind;
        }
        return -1;
    }
    
    public void Damage(final float n) {
        this.crash(n, true);
    }
    
    public void HitByVehicle(final BaseVehicle baseVehicle, final float n) {
        this.crash(n, true);
    }
    
    public void crash(float n, final boolean b) {
        if (GameClient.bClient && GameTime.getInstance().getCalender().getTimeInMillis() - this.lastCrashTime > 5000L) {
            this.lastCrashTime = GameTime.getInstance().getCalender().getTimeInMillis();
            SoundManager.instance.PlayWorldSound("VehicleCrash", this.square, 1.0f, 20.0f, 1.0f, true);
            GameClient.instance.sendClientCommandV(null, "vehicle", "crash", "vehicle", this.getId(), "amount", n, "front", b);
            return;
        }
        float n2 = 1.3f;
        final float a = n;
        switch (SandboxOptions.instance.CarDamageOnImpact.getValue()) {
            case 1: {
                n2 = 1.9f;
                break;
            }
            case 2: {
                n2 = 1.6f;
                break;
            }
            case 4: {
                n2 = 1.1f;
                break;
            }
            case 5: {
                n2 = 0.9f;
                break;
            }
        }
        n = Math.abs(n) / n2;
        if (b) {
            this.addDamageFront((int)n);
        }
        else {
            this.addDamageRear((int)Math.abs(n / n2));
        }
        this.damagePlayers(Math.abs(a));
        if (a < 5.0f) {
            SoundManager.instance.PlayWorldSound("VehicleCrash1", this.square, 1.0f, 20.0f, 1.0f, true);
        }
        else if (a < 30.0f) {
            SoundManager.instance.PlayWorldSound("VehicleCrash2", this.square, 1.0f, 20.0f, 1.0f, true);
        }
        else {
            SoundManager.instance.PlayWorldSound("VehicleCrash", this.square, 1.0f, 20.0f, 1.0f, true);
        }
    }
    
    public void addDamageFrontHitAChr(final int n) {
        if (n < 4 && Rand.NextBool(7)) {
            return;
        }
        final VehiclePart partById = this.getPartById("EngineDoor");
        if (partById != null && partById.getInventoryItem() != null) {
            partById.damage(Rand.Next(Math.max(1, n - 10), n + 3));
        }
        if (partById != null && partById.getCondition() <= 0 && Rand.NextBool(5)) {
            final VehiclePart partById2 = this.getPartById("Engine");
            if (partById2 != null) {
                partById2.damage(Rand.Next(1, 3));
            }
        }
        if (n > 12) {
            final VehiclePart partById3 = this.getPartById("Windshield");
            if (partById3 != null && partById3.getInventoryItem() != null) {
                partById3.damage(Rand.Next(Math.max(1, n - 10), n + 3));
            }
        }
        if (Rand.Next(5) < n) {
            VehiclePart vehiclePart;
            if (Rand.NextBool(2)) {
                vehiclePart = this.getPartById("TireFrontLeft");
            }
            else {
                vehiclePart = this.getPartById("TireFrontRight");
            }
            if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
                vehiclePart.damage(Rand.Next(1, 3));
            }
        }
        if (Rand.Next(7) < n) {
            this.damageHeadlight("HeadlightLeft", Rand.Next(1, 4));
        }
        if (Rand.Next(7) < n) {
            this.damageHeadlight("HeadlightRight", Rand.Next(1, 4));
        }
        this.setBloodIntensity("Front", this.getBloodIntensity("Front") + 0.01f);
    }
    
    public void addDamageRearHitAChr(final int n) {
        if (n < 4 && Rand.NextBool(7)) {
            return;
        }
        final VehiclePart partById = this.getPartById("TruckBed");
        if (partById != null && partById.getInventoryItem() != null) {
            partById.setCondition(partById.getCondition() - Rand.Next(Math.max(1, n - 10), n + 3));
            partById.doInventoryItemStats(partById.getInventoryItem(), 0);
            this.transmitPartCondition(partById);
        }
        final VehiclePart partById2 = this.getPartById("DoorRear");
        if (partById2 != null && partById2.getInventoryItem() != null) {
            partById2.damage(Rand.Next(Math.max(1, n - 10), n + 3));
        }
        final VehiclePart partById3 = this.getPartById("TrunkDoor");
        if (partById3 != null && partById3.getInventoryItem() != null) {
            partById3.damage(Rand.Next(Math.max(1, n - 10), n + 3));
        }
        if (n > 12) {
            final VehiclePart partById4 = this.getPartById("WindshieldRear");
            if (partById4 != null && partById4.getInventoryItem() != null) {
                partById4.damage(n);
            }
        }
        if (Rand.Next(5) < n) {
            VehiclePart vehiclePart;
            if (Rand.NextBool(2)) {
                vehiclePart = this.getPartById("TireRearLeft");
            }
            else {
                vehiclePart = this.getPartById("TireRearRight");
            }
            if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
                vehiclePart.damage(Rand.Next(1, 3));
            }
        }
        if (Rand.Next(7) < n) {
            this.damageHeadlight("HeadlightRearLeft", Rand.Next(1, 4));
        }
        if (Rand.Next(7) < n) {
            this.damageHeadlight("HeadlightRearRight", Rand.Next(1, 4));
        }
        if (Rand.Next(6) < n) {
            final VehiclePart partById5 = this.getPartById("GasTank");
            if (partById5 != null && partById5.getInventoryItem() != null) {
                partById5.damage(Rand.Next(1, 3));
            }
        }
        this.setBloodIntensity("Rear", this.getBloodIntensity("Rear") + 0.01f);
    }
    
    private void addDamageFront(final int n) {
        this.currentFrontEndDurability -= n;
        final VehiclePart partById = this.getPartById("EngineDoor");
        if (partById != null && partById.getInventoryItem() != null) {
            partById.damage(Rand.Next(Math.max(1, n - 5), n + 5));
        }
        if (partById == null || partById.getInventoryItem() == null || partById.getCondition() < 25) {
            final VehiclePart partById2 = this.getPartById("Engine");
            if (partById2 != null) {
                partById2.damage(Rand.Next(Math.max(1, n - 3), n + 3));
            }
        }
        final VehiclePart partById3 = this.getPartById("Windshield");
        if (partById3 != null && partById3.getInventoryItem() != null) {
            partById3.damage(Rand.Next(Math.max(1, n - 5), n + 5));
        }
        if (Rand.Next(4) == 0) {
            final VehiclePart partById4 = this.getPartById("DoorFrontLeft");
            if (partById4 != null && partById4.getInventoryItem() != null) {
                partById4.damage(Rand.Next(Math.max(1, n - 5), n + 5));
            }
            final VehiclePart partById5 = this.getPartById("WindowFrontLeft");
            if (partById5 != null && partById5.getInventoryItem() != null) {
                partById5.damage(Rand.Next(Math.max(1, n - 5), n + 5));
            }
        }
        if (Rand.Next(4) == 0) {
            final VehiclePart partById6 = this.getPartById("DoorFrontRight");
            if (partById6 != null && partById6.getInventoryItem() != null) {
                partById6.damage(Rand.Next(Math.max(1, n - 5), n + 5));
            }
            final VehiclePart partById7 = this.getPartById("WindowFrontRight");
            if (partById7 != null && partById7.getInventoryItem() != null) {
                partById7.damage(Rand.Next(Math.max(1, n - 5), n + 5));
            }
        }
        if (Rand.Next(20) < n) {
            this.damageHeadlight("HeadlightLeft", n);
        }
        if (Rand.Next(20) < n) {
            this.damageHeadlight("HeadlightRight", n);
        }
    }
    
    private void addDamageRear(final int n) {
        this.currentRearEndDurability -= n;
        final VehiclePart partById = this.getPartById("TruckBed");
        if (partById != null && partById.getInventoryItem() != null) {
            partById.setCondition(partById.getCondition() - Rand.Next(Math.max(1, n - 5), n + 5));
            partById.doInventoryItemStats(partById.getInventoryItem(), 0);
            this.transmitPartCondition(partById);
        }
        final VehiclePart partById2 = this.getPartById("DoorRear");
        if (partById2 != null && partById2.getInventoryItem() != null) {
            partById2.damage(Rand.Next(Math.max(1, n - 5), n + 5));
        }
        final VehiclePart partById3 = this.getPartById("TrunkDoor");
        if (partById3 != null && partById3.getInventoryItem() != null) {
            partById3.damage(Rand.Next(Math.max(1, n - 5), n + 5));
        }
        final VehiclePart partById4 = this.getPartById("WindshieldRear");
        if (partById4 != null && partById4.getInventoryItem() != null) {
            partById4.damage(n);
        }
        if (Rand.Next(4) == 0) {
            final VehiclePart partById5 = this.getPartById("DoorRearLeft");
            if (partById5 != null && partById5.getInventoryItem() != null) {
                partById5.damage(Rand.Next(Math.max(1, n - 5), n + 5));
            }
            final VehiclePart partById6 = this.getPartById("WindowRearLeft");
            if (partById6 != null && partById6.getInventoryItem() != null) {
                partById6.damage(Rand.Next(Math.max(1, n - 5), n + 5));
            }
        }
        if (Rand.Next(4) == 0) {
            final VehiclePart partById7 = this.getPartById("DoorRearRight");
            if (partById7 != null && partById7.getInventoryItem() != null) {
                partById7.damage(Rand.Next(Math.max(1, n - 5), n + 5));
            }
            final VehiclePart partById8 = this.getPartById("WindowRearRight");
            if (partById8 != null && partById8.getInventoryItem() != null) {
                partById8.damage(Rand.Next(Math.max(1, n - 5), n + 5));
            }
        }
        if (Rand.Next(20) < n) {
            this.damageHeadlight("HeadlightRearLeft", n);
        }
        if (Rand.Next(20) < n) {
            this.damageHeadlight("HeadlightRearRight", n);
        }
        if (Rand.Next(20) < n) {
            final VehiclePart partById9 = this.getPartById("Muffler");
            if (partById9 != null && partById9.getInventoryItem() != null) {
                partById9.damage(Rand.Next(Math.max(1, n - 5), n + 5));
            }
        }
    }
    
    private void damageHeadlight(final String s, final int n) {
        final VehiclePart partById = this.getPartById(s);
        if (partById != null && partById.getInventoryItem() != null) {
            partById.damage(n);
            if (partById.getCondition() <= 0) {
                partById.setInventoryItem(null);
                this.transmitPartItem(partById);
            }
        }
    }
    
    private float clamp(float n, final float n2, final float n3) {
        if (n < n2) {
            n = n2;
        }
        if (n > n3) {
            n = n3;
        }
        return n;
    }
    
    public boolean isCharacterAdjacentTo(final IsoGameCharacter isoGameCharacter) {
        if ((int)isoGameCharacter.z != (int)this.z) {
            return false;
        }
        final Transform worldTransform = this.getWorldTransform(this.tempTransform);
        worldTransform.inverse();
        final Vector3f vector3f = BaseVehicle.TL_vector3f_pool.get().alloc();
        vector3f.set(isoGameCharacter.x - WorldSimulation.instance.offsetX, 0.0f, isoGameCharacter.y - WorldSimulation.instance.offsetY);
        worldTransform.transform(vector3f);
        final Vector3f extents = this.script.getExtents();
        final Vector3f centerOfMassOffset = this.script.getCenterOfMassOffset();
        final float n = centerOfMassOffset.x - extents.x / 2.0f;
        final float n2 = centerOfMassOffset.x + extents.x / 2.0f;
        final float n3 = centerOfMassOffset.z - extents.z / 2.0f;
        final float n4 = centerOfMassOffset.z + extents.z / 2.0f;
        if (vector3f.x >= n - 0.5f && vector3f.x < n2 + 0.5f && vector3f.z >= n3 - 0.5f && vector3f.z < n4 + 0.5f) {
            BaseVehicle.TL_vector3f_pool.get().release(vector3f);
            return true;
        }
        BaseVehicle.TL_vector3f_pool.get().release(vector3f);
        return false;
    }
    
    public Vector2 testCollisionWithCharacter(final IsoGameCharacter isoGameCharacter, final float n, final Vector2 vector2) {
        if (this.physics == null) {
            return null;
        }
        final Vector3f extents = this.script.getExtents();
        final Vector3f centerOfMassOffset = this.script.getCenterOfMassOffset();
        if (this.DistToProper(isoGameCharacter) > Math.max(extents.x / 2.0f, extents.z / 2.0f) + n + 1.0f) {
            return null;
        }
        final Vector3f vector3f = BaseVehicle.TL_vector3f_pool.get().alloc();
        this.getLocalPos(isoGameCharacter.nx, isoGameCharacter.ny, 0.0f, vector3f);
        final float n2 = centerOfMassOffset.x - extents.x / 2.0f;
        final float n3 = centerOfMassOffset.x + extents.x / 2.0f;
        final float n4 = centerOfMassOffset.z - extents.z / 2.0f;
        final float n5 = centerOfMassOffset.z + extents.z / 2.0f;
        if (vector3f.x > n2 && vector3f.x < n3 && vector3f.z > n4 && vector3f.z < n5) {
            final float n6 = vector3f.x - n2;
            final float n7 = n3 - vector3f.x;
            final float n8 = vector3f.z - n4;
            final float n9 = n5 - vector3f.z;
            final Vector3f vector3f2 = BaseVehicle.TL_vector3f_pool.get().alloc();
            if (n6 < n7 && n6 < n8 && n6 < n9) {
                vector3f2.set(n2 - n - 0.015f, 0.0f, vector3f.z);
            }
            else if (n7 < n6 && n7 < n8 && n7 < n9) {
                vector3f2.set(n3 + n + 0.015f, 0.0f, vector3f.z);
            }
            else if (n8 < n6 && n8 < n7 && n8 < n9) {
                vector3f2.set(vector3f.x, 0.0f, n4 - n - 0.015f);
            }
            else if (n9 < n6 && n9 < n7 && n9 < n8) {
                vector3f2.set(vector3f.x, 0.0f, n5 + n + 0.015f);
            }
            BaseVehicle.TL_vector3f_pool.get().release(vector3f);
            final Transform worldTransform = this.getWorldTransform(this.tempTransform);
            worldTransform.origin.set(0.0f, 0.0f, 0.0f);
            worldTransform.transform(vector3f2);
            final Vector3f vector3f3 = vector3f2;
            vector3f3.x += this.getX();
            final Vector3f vector3f4 = vector3f2;
            vector3f4.z += this.getY();
            this.collideX = vector3f2.x;
            this.collideY = vector3f2.z;
            vector2.set(vector3f2.x, vector3f2.z);
            BaseVehicle.TL_vector3f_pool.get().release(vector3f2);
            return vector2;
        }
        final float clamp = this.clamp(vector3f.x, n2, n3);
        final float clamp2 = this.clamp(vector3f.z, n4, n5);
        final float n10 = vector3f.x - clamp;
        final float n11 = vector3f.z - clamp2;
        BaseVehicle.TL_vector3f_pool.get().release(vector3f);
        if (n10 * n10 + n11 * n11 >= n * n) {
            return null;
        }
        if (n10 == 0.0f && n11 == 0.0f) {
            return vector2.set(-1.0f, -1.0f);
        }
        final Vector3f vector3f5 = BaseVehicle.TL_vector3f_pool.get().alloc();
        vector3f5.set(n10, 0.0f, n11);
        vector3f5.normalize();
        vector3f5.mul(n + 0.015f);
        final Vector3f vector3f6 = vector3f5;
        vector3f6.x += clamp;
        final Vector3f vector3f7 = vector3f5;
        vector3f7.z += clamp2;
        final Transform worldTransform2 = this.getWorldTransform(this.tempTransform);
        worldTransform2.origin.set(0.0f, 0.0f, 0.0f);
        worldTransform2.transform(vector3f5);
        final Vector3f vector3f8 = vector3f5;
        vector3f8.x += this.getX();
        final Vector3f vector3f9 = vector3f5;
        vector3f9.z += this.getY();
        this.collideX = vector3f5.x;
        this.collideY = vector3f5.z;
        vector2.set(vector3f5.x, vector3f5.z);
        BaseVehicle.TL_vector3f_pool.get().release(vector3f5);
        return vector2;
    }
    
    public int testCollisionWithProneCharacter(final IsoGameCharacter isoGameCharacter, final boolean b) {
        final Vector2 animVector = isoGameCharacter.getAnimVector(BaseVehicle.TL_vector2_pool.get().alloc());
        final int testCollisionWithProneCharacter = this.testCollisionWithProneCharacter(isoGameCharacter, animVector.x, animVector.y, b);
        BaseVehicle.TL_vector2_pool.get().release(animVector);
        return testCollisionWithProneCharacter;
    }
    
    public int testCollisionWithCorpse(final IsoDeadBody isoDeadBody, final boolean b) {
        return this.testCollisionWithProneCharacter(isoDeadBody, (float)Math.cos(isoDeadBody.getAngle()), (float)Math.sin(isoDeadBody.getAngle()), b);
    }
    
    public int testCollisionWithProneCharacter(final IsoMovingObject isoMovingObject, final float n, final float n2, boolean b) {
        if (this.physics == null) {
            return 0;
        }
        if (GameServer.bServer) {
            return 0;
        }
        final Vector3f extents = this.script.getExtents();
        if (this.DistToProper(isoMovingObject) > Math.max(extents.x / 2.0f, extents.z / 2.0f) + 0.3f + 1.0f) {
            return 0;
        }
        if (Math.abs(this.jniSpeed) < 3.0f) {
            return 0;
        }
        final float n3 = isoMovingObject.x + n * 0.65f;
        final float n4 = isoMovingObject.y + n2 * 0.65f;
        final float n5 = isoMovingObject.x - n * 0.65f;
        final float n6 = isoMovingObject.y - n2 * 0.65f;
        int n7 = 0;
        final Vector3f vector3f = BaseVehicle.TL_vector3f_pool.get().alloc();
        final Vector3f vector3f2 = BaseVehicle.TL_vector3f_pool.get().alloc();
        for (int i = 0; i < this.script.getWheelCount(); ++i) {
            final VehicleScript.Wheel wheel = this.script.getWheel(i);
            boolean b2 = true;
            int j = 0;
            while (j < this.models.size()) {
                if (this.models.get(j).wheelIndex != i) {
                    ++j;
                }
                else {
                    this.getWorldPos(wheel.offset.x, wheel.offset.y - this.wheelInfo[i].suspensionLength, wheel.offset.z, vector3f);
                    if (vector3f.z > this.script.getWheel(i).radius + 0.05f) {
                        b2 = false;
                        break;
                    }
                    break;
                }
            }
            if (b2) {
                this.getWorldPos(wheel.offset.x, wheel.offset.y, wheel.offset.z, vector3f2);
                final float n8 = n5;
                final float n9 = n6;
                final float n10 = n3;
                final float n11 = n4;
                final double n12 = ((vector3f2.x - n8) * (n10 - n8) + (vector3f2.y - n9) * (n11 - n9)) / (Math.pow(n10 - n8, 2.0) + Math.pow(n11 - n9, 2.0));
                float n13;
                float n14;
                if (n12 <= 0.0) {
                    n13 = n8;
                    n14 = n9;
                }
                else if (n12 >= 1.0) {
                    n13 = n10;
                    n14 = n11;
                }
                else {
                    n13 = n8 + (n10 - n8) * (float)n12;
                    n14 = n9 + (n11 - n9) * (float)n12;
                }
                if (IsoUtils.DistanceToSquared(vector3f2.x, vector3f2.y, n13, n14) <= wheel.radius * wheel.radius) {
                    if (b && Math.abs(this.jniSpeed) > 10.0f) {
                        if (GameServer.bServer && isoMovingObject instanceof IsoZombie) {
                            ((IsoZombie)isoMovingObject).setThumpFlag(1);
                        }
                        else {
                            SoundManager.instance.PlayWorldSound("VehicleRunOverBody", isoMovingObject.getCurrentSquare(), 0.0f, 20.0f, 0.9f, true);
                        }
                        b = false;
                    }
                    if (i < this.impulseFromSquishedZombie.length) {
                        if (this.impulseFromSquishedZombie[i] == null) {
                            this.impulseFromSquishedZombie[i] = new VehicleImpulse();
                        }
                        this.impulseFromSquishedZombie[i].impulse.set(0.0f, 1.0f, 0.0f);
                        this.impulseFromSquishedZombie[i].impulse.mul(0.065f * this.getFudgedMass() * (Math.max(Math.abs(this.jniSpeed), 20.0f) / 20.0f));
                        this.impulseFromSquishedZombie[i].rel_pos.set(vector3f2.x - this.x, 0.0f, vector3f2.y - this.y);
                        this.impulseFromSquishedZombie[i].enable = true;
                        ++n7;
                    }
                }
            }
        }
        BaseVehicle.TL_vector3f_pool.get().release(vector3f);
        BaseVehicle.TL_vector3f_pool.get().release(vector3f2);
        return n7;
    }
    
    public Vector2 testCollisionWithObject(final IsoObject isoObject, final float n, final Vector2 vector2) {
        if (this.physics == null) {
            return null;
        }
        if (isoObject.square == null) {
            return null;
        }
        final float objectX = this.getObjectX(isoObject);
        final float objectY = this.getObjectY(isoObject);
        final Vector3f extents = this.script.getExtents();
        final Vector3f centerOfMassOffset = this.script.getCenterOfMassOffset();
        final float n2 = Math.max(extents.x / 2.0f, extents.z / 2.0f) + n + 1.0f;
        if (this.DistToSquared(objectX, objectY) > n2 * n2) {
            return null;
        }
        final Vector3f vector3f = BaseVehicle.TL_vector3f_pool.get().alloc();
        this.getLocalPos(objectX, objectY, 0.0f, vector3f);
        final float n3 = centerOfMassOffset.x - extents.x / 2.0f;
        final float n4 = centerOfMassOffset.x + extents.x / 2.0f;
        final float n5 = centerOfMassOffset.z - extents.z / 2.0f;
        final float n6 = centerOfMassOffset.z + extents.z / 2.0f;
        if (vector3f.x > n3 && vector3f.x < n4 && vector3f.z > n5 && vector3f.z < n6) {
            final float n7 = vector3f.x - n3;
            final float n8 = n4 - vector3f.x;
            final float n9 = vector3f.z - n5;
            final float n10 = n6 - vector3f.z;
            final Vector3f vector3f2 = BaseVehicle.TL_vector3f_pool.get().alloc();
            if (n7 < n8 && n7 < n9 && n7 < n10) {
                vector3f2.set(n3 - n - 0.015f, 0.0f, vector3f.z);
            }
            else if (n8 < n7 && n8 < n9 && n8 < n10) {
                vector3f2.set(n4 + n + 0.015f, 0.0f, vector3f.z);
            }
            else if (n9 < n7 && n9 < n8 && n9 < n10) {
                vector3f2.set(vector3f.x, 0.0f, n5 - n - 0.015f);
            }
            else if (n10 < n7 && n10 < n8 && n10 < n9) {
                vector3f2.set(vector3f.x, 0.0f, n6 + n + 0.015f);
            }
            BaseVehicle.TL_vector3f_pool.get().release(vector3f);
            final Transform worldTransform = this.getWorldTransform(this.tempTransform);
            worldTransform.origin.set(0.0f, 0.0f, 0.0f);
            worldTransform.transform(vector3f2);
            final Vector3f vector3f3 = vector3f2;
            vector3f3.x += this.getX();
            final Vector3f vector3f4 = vector3f2;
            vector3f4.z += this.getY();
            this.collideX = vector3f2.x;
            this.collideY = vector3f2.z;
            vector2.set(vector3f2.x, vector3f2.z);
            BaseVehicle.TL_vector3f_pool.get().release(vector3f2);
            return vector2;
        }
        final float clamp = this.clamp(vector3f.x, n3, n4);
        final float clamp2 = this.clamp(vector3f.z, n5, n6);
        final float n11 = vector3f.x - clamp;
        final float n12 = vector3f.z - clamp2;
        BaseVehicle.TL_vector3f_pool.get().release(vector3f);
        if (n11 * n11 + n12 * n12 >= n * n) {
            return null;
        }
        if (n11 == 0.0f && n12 == 0.0f) {
            return vector2.set(-1.0f, -1.0f);
        }
        final Vector3f vector3f5 = BaseVehicle.TL_vector3f_pool.get().alloc();
        vector3f5.set(n11, 0.0f, n12);
        vector3f5.normalize();
        vector3f5.mul(n + 0.015f);
        final Vector3f vector3f6 = vector3f5;
        vector3f6.x += clamp;
        final Vector3f vector3f7 = vector3f5;
        vector3f7.z += clamp2;
        final Transform worldTransform2 = this.getWorldTransform(this.tempTransform);
        worldTransform2.origin.set(0.0f, 0.0f, 0.0f);
        worldTransform2.transform(vector3f5);
        final Vector3f vector3f8 = vector3f5;
        vector3f8.x += this.getX();
        final Vector3f vector3f9 = vector3f5;
        vector3f9.z += this.getY();
        this.collideX = vector3f5.x;
        this.collideY = vector3f5.z;
        vector2.set(vector3f5.x, vector3f5.z);
        BaseVehicle.TL_vector3f_pool.get().release(vector3f5);
        return vector2;
    }
    
    public boolean testCollisionWithVehicle(final BaseVehicle baseVehicle) {
        VehicleScript vehicleScript = this.script;
        if (vehicleScript == null) {
            vehicleScript = ScriptManager.instance.getVehicle(this.scriptName);
        }
        VehicleScript vehicleScript2 = baseVehicle.script;
        if (vehicleScript2 == null) {
            vehicleScript2 = ScriptManager.instance.getVehicle(baseVehicle.scriptName);
        }
        if (vehicleScript == null || vehicleScript2 == null) {
            return false;
        }
        final Vector2[] testVecs1 = L_testCollisionWithVehicle.testVecs1;
        final Vector2[] testVecs2 = L_testCollisionWithVehicle.testVecs2;
        if (testVecs1[0] == null) {
            for (int i = 0; i < testVecs1.length; ++i) {
                testVecs1[i] = new Vector2();
                testVecs2[i] = new Vector2();
            }
        }
        final Vector3f extents = vehicleScript.getExtents();
        final Vector3f centerOfMassOffset = vehicleScript.getCenterOfMassOffset();
        final Vector3f extents2 = vehicleScript2.getExtents();
        final Vector3f centerOfMassOffset2 = vehicleScript2.getCenterOfMassOffset();
        final Vector3f worldPos = L_testCollisionWithVehicle.worldPos;
        final float n = 0.5f;
        this.getWorldPos(centerOfMassOffset.x + extents.x * n, 0.0f, centerOfMassOffset.z + extents.z * n, worldPos, vehicleScript);
        testVecs1[0].set(worldPos.x, worldPos.y);
        this.getWorldPos(centerOfMassOffset.x - extents.x * n, 0.0f, centerOfMassOffset.z + extents.z * n, worldPos, vehicleScript);
        testVecs1[1].set(worldPos.x, worldPos.y);
        this.getWorldPos(centerOfMassOffset.x - extents.x * n, 0.0f, centerOfMassOffset.z - extents.z * n, worldPos, vehicleScript);
        testVecs1[2].set(worldPos.x, worldPos.y);
        this.getWorldPos(centerOfMassOffset.x + extents.x * n, 0.0f, centerOfMassOffset.z - extents.z * n, worldPos, vehicleScript);
        testVecs1[3].set(worldPos.x, worldPos.y);
        baseVehicle.getWorldPos(centerOfMassOffset2.x + extents2.x * n, 0.0f, centerOfMassOffset2.z + extents2.z * n, worldPos, vehicleScript2);
        testVecs2[0].set(worldPos.x, worldPos.y);
        baseVehicle.getWorldPos(centerOfMassOffset2.x - extents2.x * n, 0.0f, centerOfMassOffset2.z + extents2.z * n, worldPos, vehicleScript2);
        testVecs2[1].set(worldPos.x, worldPos.y);
        baseVehicle.getWorldPos(centerOfMassOffset2.x - extents2.x * n, 0.0f, centerOfMassOffset2.z - extents2.z * n, worldPos, vehicleScript2);
        testVecs2[2].set(worldPos.x, worldPos.y);
        baseVehicle.getWorldPos(centerOfMassOffset2.x + extents2.x * n, 0.0f, centerOfMassOffset2.z - extents2.z * n, worldPos, vehicleScript2);
        testVecs2[3].set(worldPos.x, worldPos.y);
        return QuadranglesIntersection.IsQuadranglesAreIntersected(testVecs1, testVecs2);
    }
    
    protected float getObjectX(final IsoObject isoObject) {
        if (isoObject instanceof IsoMovingObject) {
            return isoObject.getX();
        }
        return isoObject.getSquare().getX() + 0.5f;
    }
    
    protected float getObjectY(final IsoObject isoObject) {
        if (isoObject instanceof IsoMovingObject) {
            return isoObject.getY();
        }
        return isoObject.getSquare().getY() + 0.5f;
    }
    
    public void ApplyImpulse(final IsoObject isoObject, final float n) {
        final float objectX = this.getObjectX(isoObject);
        final float objectY = this.getObjectY(isoObject);
        final VehicleImpulse alloc = VehicleImpulse.alloc();
        alloc.impulse.set(this.x - objectX, 0.0f, this.y - objectY);
        alloc.impulse.normalize();
        alloc.impulse.mul(n);
        alloc.rel_pos.set(objectX - this.x, 0.0f, objectY - this.y);
        this.impulseFromHitZombie.add(alloc);
    }
    
    public void ApplyImpulse4Break(final IsoObject isoObject, final float n) {
        final float objectX = this.getObjectX(isoObject);
        final float objectY = this.getObjectY(isoObject);
        final VehicleImpulse alloc = VehicleImpulse.alloc();
        this.getLinearVelocity(alloc.impulse);
        alloc.impulse.mul(-n * this.getFudgedMass());
        alloc.rel_pos.set(objectX - this.x, 0.0f, objectY - this.y);
        this.impulseFromHitZombie.add(alloc);
    }
    
    public void hitCharacter(final IsoZombie isoZombie) {
        final IsoPlayer isoPlayer = Type.tryCastTo(isoZombie, IsoPlayer.class);
        final IsoZombie isoZombie2 = Type.tryCastTo(isoZombie, IsoZombie.class);
        if (isoZombie.getCurrentState() == StaggerBackState.instance() || isoZombie.getCurrentState() == ZombieFallDownState.instance()) {
            return;
        }
        if (Math.abs(isoZombie.x - this.x) < 0.01f || Math.abs(isoZombie.y - this.y) < 0.01f) {
            return;
        }
        final float b = 15.0f;
        final Vector3f linearVelocity = this.getLinearVelocity(BaseVehicle.TL_vector3f_pool.get().alloc());
        linearVelocity.y = 0.0f;
        final float min = Math.min(linearVelocity.length(), b);
        if (min < 0.05f) {
            BaseVehicle.TL_vector3f_pool.get().release(linearVelocity);
            return;
        }
        final Vector3f vector3f = BaseVehicle.TL_vector3f_pool.get().alloc();
        vector3f.set(this.x - isoZombie.x, 0.0f, this.y - isoZombie.y);
        vector3f.normalize();
        linearVelocity.normalize();
        final float dot = linearVelocity.dot((Vector3fc)vector3f);
        BaseVehicle.TL_vector3f_pool.get().release(linearVelocity);
        if (dot < 0.0f && !GameServer.bServer) {
            this.ApplyImpulse(isoZombie, this.getFudgedMass() * 7.0f * min / b * Math.abs(dot));
        }
        vector3f.normalize();
        vector3f.mul(3.0f * min / b);
        final Vector2 vector2 = BaseVehicle.TL_vector2_pool.get().alloc();
        final float n = min + this.physics.clientForce / this.getFudgedMass();
        if (isoPlayer != null) {
            isoPlayer.setVehicleHitLocation(this);
        }
        else if (isoZombie2 != null) {
            isoZombie2.setVehicleHitLocation(this);
        }
        final BaseSoundEmitter freeEmitter = IsoWorld.instance.getFreeEmitter(isoZombie.x, isoZombie.y, isoZombie.z);
        freeEmitter.setParameterValue(freeEmitter.playSound("VehicleHitCharacter"), FMODManager.instance.getParameterDescription("VehicleSpeed"), this.getCurrentSpeedKmHour());
        isoZombie.Hit(this, n, dot > 0.0f, vector2.set(-vector3f.x, -vector3f.z));
        BaseVehicle.TL_vector2_pool.get().release(vector2);
        BaseVehicle.TL_vector3f_pool.get().release(vector3f);
        final long currentTimeMillis = System.currentTimeMillis();
        this.zombiesHits = Math.max(this.zombiesHits - (int)((currentTimeMillis - this.zombieHitTimestamp) / 1000L), 0);
        if (currentTimeMillis - this.zombieHitTimestamp > 700L) {
            this.zombieHitTimestamp = currentTimeMillis;
            ++this.zombiesHits;
            this.zombiesHits = Math.min(this.zombiesHits, 20);
        }
        if (min >= 5.0f || this.zombiesHits > 10) {
            final float n2 = this.getCurrentSpeedKmHour() / 5.0f;
            final Vector3f vector3f2 = BaseVehicle.TL_vector3f_pool.get().alloc();
            this.getLocalPos(isoZombie.x, isoZombie.y, isoZombie.z, vector3f2);
            if (vector3f2.z > 0.0f) {
                this.addDamageFrontHitAChr(this.caclulateDamageWithBodies(true));
            }
            else {
                this.addDamageRearHitAChr(this.caclulateDamageWithBodies(false));
            }
            BaseVehicle.TL_vector3f_pool.get().release(vector3f2);
        }
    }
    
    private int caclulateDamageWithBodies(final boolean b) {
        final boolean b2 = this.getCurrentSpeedKmHour() > 0.0f;
        final float n = Math.abs(this.getCurrentSpeedKmHour()) / 160.0f;
        float n2 = 60.0f * PZMath.clamp(n * n, 0.0f, 1.0f);
        float max = PZMath.max(1.0f, this.zombiesHits / 3.0f);
        if (!b && !b2) {
            max = 1.0f;
        }
        if (this.zombiesHits > 10 && n2 < Math.abs(this.getCurrentSpeedKmHour()) / 5.0f) {
            n2 = Math.abs(this.getCurrentSpeedKmHour()) / 5.0f;
        }
        return (int)(max * n2);
    }
    
    public int calculateDamageWithCharacter(final IsoGameCharacter isoGameCharacter) {
        final Vector3f vector3f = BaseVehicle.TL_vector3f_pool.get().alloc();
        this.getLocalPos(isoGameCharacter.x, isoGameCharacter.y, isoGameCharacter.z, vector3f);
        int caclulateDamageWithBodies;
        if (vector3f.z > 0.0f) {
            caclulateDamageWithBodies = this.caclulateDamageWithBodies(true);
        }
        else {
            caclulateDamageWithBodies = -1 * this.caclulateDamageWithBodies(false);
        }
        BaseVehicle.TL_vector3f_pool.get().release(vector3f);
        return caclulateDamageWithBodies;
    }
    
    public boolean blocked(final int n, final int n2, final int n3) {
        if (this.removedFromWorld || this.current == null || this.getController() == null) {
            return false;
        }
        if (this.getController() == null) {
            return false;
        }
        if (n3 != (int)this.getZ()) {
            return false;
        }
        if (IsoUtils.DistanceTo2D(n + 0.5f, n2 + 0.5f, this.x, this.y) > 5.0f) {
            return false;
        }
        final float n4 = 0.3f;
        final Transform tempTransform3 = this.tempTransform3;
        this.getWorldTransform(tempTransform3);
        tempTransform3.inverse();
        final Vector3f vector3f = BaseVehicle.TL_vector3f_pool.get().alloc();
        vector3f.set(n + 0.5f - WorldSimulation.instance.offsetX, 0.0f, n2 + 0.5f - WorldSimulation.instance.offsetY);
        tempTransform3.transform(vector3f);
        final Vector3f extents = this.script.getExtents();
        final Vector3f centerOfMassOffset = this.script.getCenterOfMassOffset();
        final float clamp = this.clamp(vector3f.x, centerOfMassOffset.x - extents.x / 2.0f, centerOfMassOffset.x + extents.x / 2.0f);
        final float clamp2 = this.clamp(vector3f.z, centerOfMassOffset.z - extents.z / 2.0f, centerOfMassOffset.z + extents.z / 2.0f);
        final float n5 = vector3f.x - clamp;
        final float n6 = vector3f.z - clamp2;
        BaseVehicle.TL_vector3f_pool.get().release(vector3f);
        return n5 * n5 + n6 * n6 < n4 * n4;
    }
    
    public boolean isIntersectingSquare(final int n, final int n2, final int n3) {
        if (n3 != (int)this.getZ()) {
            return false;
        }
        if (this.removedFromWorld || this.current == null || this.getController() == null) {
            return false;
        }
        final PolygonalMap2.VehiclePoly tempPoly = BaseVehicle.tempPoly;
        final PolygonalMap2.VehiclePoly tempPoly2 = BaseVehicle.tempPoly;
        final float n4 = (float)n;
        tempPoly2.x4 = n4;
        tempPoly.x1 = n4;
        final PolygonalMap2.VehiclePoly tempPoly3 = BaseVehicle.tempPoly;
        final PolygonalMap2.VehiclePoly tempPoly4 = BaseVehicle.tempPoly;
        final float n5 = (float)n2;
        tempPoly4.y2 = n5;
        tempPoly3.y1 = n5;
        final PolygonalMap2.VehiclePoly tempPoly5 = BaseVehicle.tempPoly;
        final PolygonalMap2.VehiclePoly tempPoly6 = BaseVehicle.tempPoly;
        final float n6 = (float)(n + 1);
        tempPoly6.x3 = n6;
        tempPoly5.x2 = n6;
        final PolygonalMap2.VehiclePoly tempPoly7 = BaseVehicle.tempPoly;
        final PolygonalMap2.VehiclePoly tempPoly8 = BaseVehicle.tempPoly;
        final float n7 = (float)(n2 + 1);
        tempPoly8.y4 = n7;
        tempPoly7.y3 = n7;
        return PolyPolyIntersect.intersects(BaseVehicle.tempPoly, this.getPoly());
    }
    
    public boolean isIntersectingSquareWithShadow(final int n, final int n2, final int n3) {
        if (n3 != (int)this.getZ()) {
            return false;
        }
        if (this.removedFromWorld || this.current == null || this.getController() == null) {
            return false;
        }
        final PolygonalMap2.VehiclePoly tempPoly = BaseVehicle.tempPoly;
        final PolygonalMap2.VehiclePoly tempPoly2 = BaseVehicle.tempPoly;
        final float n4 = (float)n;
        tempPoly2.x4 = n4;
        tempPoly.x1 = n4;
        final PolygonalMap2.VehiclePoly tempPoly3 = BaseVehicle.tempPoly;
        final PolygonalMap2.VehiclePoly tempPoly4 = BaseVehicle.tempPoly;
        final float n5 = (float)n2;
        tempPoly4.y2 = n5;
        tempPoly3.y1 = n5;
        final PolygonalMap2.VehiclePoly tempPoly5 = BaseVehicle.tempPoly;
        final PolygonalMap2.VehiclePoly tempPoly6 = BaseVehicle.tempPoly;
        final float n6 = (float)(n + 1);
        tempPoly6.x3 = n6;
        tempPoly5.x2 = n6;
        final PolygonalMap2.VehiclePoly tempPoly7 = BaseVehicle.tempPoly;
        final PolygonalMap2.VehiclePoly tempPoly8 = BaseVehicle.tempPoly;
        final float n7 = (float)(n2 + 1);
        tempPoly8.y4 = n7;
        tempPoly7.y3 = n7;
        return PolyPolyIntersect.intersects(BaseVehicle.tempPoly, this.shadowCoord);
    }
    
    public boolean circleIntersects(final float n, final float n2, final float n3, final float n4) {
        if (this.getController() == null) {
            return false;
        }
        if ((int)n3 != (int)this.getZ()) {
            return false;
        }
        if (IsoUtils.DistanceTo2D(n, n2, this.x, this.y) > 5.0f) {
            return false;
        }
        final Vector3f extents = this.script.getExtents();
        final Vector3f centerOfMassOffset = this.script.getCenterOfMassOffset();
        final Vector3f vector3f = BaseVehicle.TL_vector3f_pool.get().alloc();
        this.getLocalPos(n, n2, n3, vector3f);
        final float n5 = centerOfMassOffset.x - extents.x / 2.0f;
        final float n6 = centerOfMassOffset.x + extents.x / 2.0f;
        final float n7 = centerOfMassOffset.z - extents.z / 2.0f;
        final float n8 = centerOfMassOffset.z + extents.z / 2.0f;
        if (vector3f.x > n5 && vector3f.x < n6 && vector3f.z > n7 && vector3f.z < n8) {
            return true;
        }
        final float clamp = this.clamp(vector3f.x, n5, n6);
        final float clamp2 = this.clamp(vector3f.z, n7, n8);
        final float n9 = vector3f.x - clamp;
        final float n10 = vector3f.z - clamp2;
        BaseVehicle.TL_vector3f_pool.get().release(vector3f);
        return n9 * n9 + n10 * n10 < n4 * n4;
    }
    
    public void updateLights() {
        final VehicleModelInstance vehicleModelInstance = (VehicleModelInstance)this.sprite.modelSlot.model;
        vehicleModelInstance.textureRustA = this.rust;
        if (this.script.getWheelCount() == 0) {
            vehicleModelInstance.textureRustA = 0.0f;
        }
        vehicleModelInstance.painColor.x = this.colorHue;
        vehicleModelInstance.painColor.y = this.colorSaturation;
        vehicleModelInstance.painColor.z = this.colorValue;
        boolean b = false;
        boolean b2 = false;
        boolean b3 = false;
        boolean b4 = false;
        boolean b5 = false;
        boolean b6 = false;
        boolean b7 = false;
        boolean b8 = false;
        if (this.windowLightsOn) {
            final VehiclePart partById = this.getPartById("Windshield");
            b = (partById != null && partById.getInventoryItem() != null);
            final VehiclePart partById2 = this.getPartById("WindshieldRear");
            b2 = (partById2 != null && partById2.getInventoryItem() != null);
            final VehiclePart partById3 = this.getPartById("WindowFrontLeft");
            b3 = (partById3 != null && partById3.getInventoryItem() != null);
            final VehiclePart partById4 = this.getPartById("WindowMiddleLeft");
            b4 = (partById4 != null && partById4.getInventoryItem() != null);
            final VehiclePart partById5 = this.getPartById("WindowRearLeft");
            b5 = (partById5 != null && partById5.getInventoryItem() != null);
            final VehiclePart partById6 = this.getPartById("WindowFrontRight");
            b6 = (partById6 != null && partById6.getInventoryItem() != null);
            final VehiclePart partById7 = this.getPartById("WindowMiddleRight");
            b7 = (partById7 != null && partById7.getInventoryItem() != null);
            final VehiclePart partById8 = this.getPartById("WindowRearRight");
            b8 = (partById8 != null && partById8.getInventoryItem() != null);
        }
        vehicleModelInstance.textureLightsEnables1[10] = (b ? 1.0f : 0.0f);
        vehicleModelInstance.textureLightsEnables1[14] = (b2 ? 1.0f : 0.0f);
        vehicleModelInstance.textureLightsEnables1[2] = (b3 ? 1.0f : 0.0f);
        vehicleModelInstance.textureLightsEnables1[6] = ((b4 | b5) ? 1.0f : 0.0f);
        vehicleModelInstance.textureLightsEnables1[9] = (b6 ? 1.0f : 0.0f);
        vehicleModelInstance.textureLightsEnables1[13] = ((b7 | b8) ? 1.0f : 0.0f);
        boolean b9 = false;
        boolean b10 = false;
        boolean b11 = false;
        boolean b12 = false;
        if (this.headlightsOn && this.getBatteryCharge() > 0.0f) {
            final VehiclePart partById9 = this.getPartById("HeadlightLeft");
            if (partById9 != null && partById9.getInventoryItem() != null) {
                b9 = true;
            }
            final VehiclePart partById10 = this.getPartById("HeadlightRight");
            if (partById10 != null && partById10.getInventoryItem() != null) {
                b10 = true;
            }
            final VehiclePart partById11 = this.getPartById("HeadlightRearLeft");
            if (partById11 != null && partById11.getInventoryItem() != null) {
                b12 = true;
            }
            final VehiclePart partById12 = this.getPartById("HeadlightRearRight");
            if (partById12 != null && partById12.getInventoryItem() != null) {
                b11 = true;
            }
        }
        vehicleModelInstance.textureLightsEnables2[4] = (b10 ? 1.0f : 0.0f);
        vehicleModelInstance.textureLightsEnables2[8] = (b9 ? 1.0f : 0.0f);
        vehicleModelInstance.textureLightsEnables2[12] = (b11 ? 1.0f : 0.0f);
        vehicleModelInstance.textureLightsEnables2[1] = (b12 ? 1.0f : 0.0f);
        boolean b13 = this.stoplightsOn && this.getBatteryCharge() > 0.0f;
        if (this.scriptName.contains("Trailer") && this.vehicleTowedBy != null && this.vehicleTowedBy.stoplightsOn && this.vehicleTowedBy.getBatteryCharge() > 0.0f) {
            b13 = true;
        }
        if (b13) {
            vehicleModelInstance.textureLightsEnables2[5] = 1.0f;
            vehicleModelInstance.textureLightsEnables2[9] = 1.0f;
        }
        else {
            vehicleModelInstance.textureLightsEnables2[5] = 0.0f;
            vehicleModelInstance.textureLightsEnables2[9] = 0.0f;
        }
        if (this.script.getLightbar().enable) {
            if (this.lightbarLightsMode.isEnable() && this.getBatteryCharge() > 0.0f) {
                switch (this.lightbarLightsMode.getLightTexIndex()) {
                    case 0: {
                        vehicleModelInstance.textureLightsEnables2[13] = 0.0f;
                        vehicleModelInstance.textureLightsEnables2[2] = 0.0f;
                        break;
                    }
                    case 1: {
                        vehicleModelInstance.textureLightsEnables2[13] = 0.0f;
                        vehicleModelInstance.textureLightsEnables2[2] = 1.0f;
                        break;
                    }
                    case 2: {
                        vehicleModelInstance.textureLightsEnables2[13] = 1.0f;
                        vehicleModelInstance.textureLightsEnables2[2] = 0.0f;
                        break;
                    }
                    default: {
                        vehicleModelInstance.textureLightsEnables2[13] = 0.0f;
                        vehicleModelInstance.textureLightsEnables2[2] = 0.0f;
                        break;
                    }
                }
            }
            else {
                vehicleModelInstance.textureLightsEnables2[13] = 0.0f;
                vehicleModelInstance.textureLightsEnables2[2] = 0.0f;
            }
        }
        if (DebugOptions.instance.VehicleCycleColor.getValue()) {
            final float n = (float)(System.currentTimeMillis() % 2000L);
            final float n2 = (float)(System.currentTimeMillis() % 7000L);
            final float n3 = (float)(System.currentTimeMillis() % 11000L);
            vehicleModelInstance.painColor.x = n / 2000.0f;
            vehicleModelInstance.painColor.y = n2 / 7000.0f;
            vehicleModelInstance.painColor.z = n3 / 11000.0f;
        }
        if (DebugOptions.instance.VehicleRenderBlood0.getValue()) {
            Arrays.fill(vehicleModelInstance.matrixBlood1Enables1, 0.0f);
            Arrays.fill(vehicleModelInstance.matrixBlood1Enables2, 0.0f);
            Arrays.fill(vehicleModelInstance.matrixBlood2Enables1, 0.0f);
            Arrays.fill(vehicleModelInstance.matrixBlood2Enables2, 0.0f);
        }
        if (DebugOptions.instance.VehicleRenderBlood50.getValue()) {
            Arrays.fill(vehicleModelInstance.matrixBlood1Enables1, 0.5f);
            Arrays.fill(vehicleModelInstance.matrixBlood1Enables2, 0.5f);
            Arrays.fill(vehicleModelInstance.matrixBlood2Enables1, 1.0f);
            Arrays.fill(vehicleModelInstance.matrixBlood2Enables2, 1.0f);
        }
        if (DebugOptions.instance.VehicleRenderBlood100.getValue()) {
            Arrays.fill(vehicleModelInstance.matrixBlood1Enables1, 1.0f);
            Arrays.fill(vehicleModelInstance.matrixBlood1Enables2, 1.0f);
            Arrays.fill(vehicleModelInstance.matrixBlood2Enables1, 1.0f);
            Arrays.fill(vehicleModelInstance.matrixBlood2Enables2, 1.0f);
        }
        if (DebugOptions.instance.VehicleRenderDamage0.getValue()) {
            Arrays.fill(vehicleModelInstance.textureDamage1Enables1, 0.0f);
            Arrays.fill(vehicleModelInstance.textureDamage1Enables2, 0.0f);
            Arrays.fill(vehicleModelInstance.textureDamage2Enables1, 0.0f);
            Arrays.fill(vehicleModelInstance.textureDamage2Enables2, 0.0f);
        }
        if (DebugOptions.instance.VehicleRenderDamage1.getValue()) {
            Arrays.fill(vehicleModelInstance.textureDamage1Enables1, 1.0f);
            Arrays.fill(vehicleModelInstance.textureDamage1Enables2, 1.0f);
            Arrays.fill(vehicleModelInstance.textureDamage2Enables1, 0.0f);
            Arrays.fill(vehicleModelInstance.textureDamage2Enables2, 0.0f);
        }
        if (DebugOptions.instance.VehicleRenderDamage2.getValue()) {
            Arrays.fill(vehicleModelInstance.textureDamage1Enables1, 0.0f);
            Arrays.fill(vehicleModelInstance.textureDamage1Enables2, 0.0f);
            Arrays.fill(vehicleModelInstance.textureDamage2Enables1, 1.0f);
            Arrays.fill(vehicleModelInstance.textureDamage2Enables2, 1.0f);
        }
        if (DebugOptions.instance.VehicleRenderRust0.getValue()) {
            vehicleModelInstance.textureRustA = 0.0f;
        }
        if (DebugOptions.instance.VehicleRenderRust50.getValue()) {
            vehicleModelInstance.textureRustA = 0.5f;
        }
        if (DebugOptions.instance.VehicleRenderRust100.getValue()) {
            vehicleModelInstance.textureRustA = 1.0f;
        }
        vehicleModelInstance.refBody = 0.3f;
        vehicleModelInstance.refWindows = 0.4f;
        if (this.rust > 0.8f) {
            vehicleModelInstance.refBody = 0.1f;
            vehicleModelInstance.refWindows = 0.2f;
        }
    }
    
    private void updateWorldLights() {
        if (!this.script.getLightbar().enable) {
            this.removeWorldLights();
            return;
        }
        if (!this.lightbarLightsMode.isEnable() || this.getBatteryCharge() <= 0.0f) {
            this.removeWorldLights();
            return;
        }
        if (this.lightbarLightsMode.getLightTexIndex() == 0) {
            this.removeWorldLights();
            return;
        }
        final IsoLightSource leftLight1 = this.leftLight1;
        final IsoLightSource leftLight2 = this.leftLight2;
        final IsoLightSource rightLight1 = this.rightLight1;
        final IsoLightSource rightLight2 = this.rightLight2;
        final int n = 8;
        rightLight2.radius = n;
        rightLight1.radius = n;
        leftLight2.radius = n;
        leftLight1.radius = n;
        if (this.lightbarLightsMode.getLightTexIndex() == 1) {
            final Vector3f worldPos = this.getWorldPos(0.4f, 0.0f, 0.0f, BaseVehicle.TL_vector3f_pool.get().alloc());
            final int x = (int)worldPos.x;
            final int y = (int)worldPos.y;
            final int z = (int)(this.getZ() + 1.0f);
            BaseVehicle.TL_vector3f_pool.get().release(worldPos);
            final int leftLightIndex = this.leftLightIndex;
            if (leftLightIndex == 1 && this.leftLight1.x == x && this.leftLight1.y == y && this.leftLight1.z == z) {
                return;
            }
            if (leftLightIndex == 2 && this.leftLight2.x == x && this.leftLight2.y == y && this.leftLight2.z == z) {
                return;
            }
            this.removeWorldLights();
            IsoLightSource isoLightSource;
            if (leftLightIndex == 1) {
                isoLightSource = this.leftLight2;
                this.leftLightIndex = 2;
            }
            else {
                isoLightSource = this.leftLight1;
                this.leftLightIndex = 1;
            }
            isoLightSource.life = -1;
            isoLightSource.x = x;
            isoLightSource.y = y;
            isoLightSource.z = z;
            IsoWorld.instance.CurrentCell.addLamppost(isoLightSource);
        }
        else {
            final Vector3f worldPos2 = this.getWorldPos(-0.4f, 0.0f, 0.0f, BaseVehicle.TL_vector3f_pool.get().alloc());
            final int x2 = (int)worldPos2.x;
            final int y2 = (int)worldPos2.y;
            final int z2 = (int)(this.getZ() + 1.0f);
            BaseVehicle.TL_vector3f_pool.get().release(worldPos2);
            final int rightLightIndex = this.rightLightIndex;
            if (rightLightIndex == 1 && this.rightLight1.x == x2 && this.rightLight1.y == y2 && this.rightLight1.z == z2) {
                return;
            }
            if (rightLightIndex == 2 && this.rightLight2.x == x2 && this.rightLight2.y == y2 && this.rightLight2.z == z2) {
                return;
            }
            this.removeWorldLights();
            IsoLightSource isoLightSource2;
            if (rightLightIndex == 1) {
                isoLightSource2 = this.rightLight2;
                this.rightLightIndex = 2;
            }
            else {
                isoLightSource2 = this.rightLight1;
                this.rightLightIndex = 1;
            }
            isoLightSource2.life = -1;
            isoLightSource2.x = x2;
            isoLightSource2.y = y2;
            isoLightSource2.z = z2;
            IsoWorld.instance.CurrentCell.addLamppost(isoLightSource2);
        }
    }
    
    public void fixLightbarModelLighting(final IsoLightSource isoLightSource, final Vector3f vector3f) {
        if (isoLightSource == this.leftLight1 || isoLightSource == this.leftLight2) {
            vector3f.set(1.0f, 0.0f, 0.0f);
        }
        else if (isoLightSource == this.rightLight1 || isoLightSource == this.rightLight2) {
            vector3f.set(-1.0f, 0.0f, 0.0f);
        }
    }
    
    private void removeWorldLights() {
        if (this.leftLightIndex == 1) {
            IsoWorld.instance.CurrentCell.removeLamppost(this.leftLight1);
            this.leftLightIndex = -1;
        }
        if (this.leftLightIndex == 2) {
            IsoWorld.instance.CurrentCell.removeLamppost(this.leftLight2);
            this.leftLightIndex = -1;
        }
        if (this.rightLightIndex == 1) {
            IsoWorld.instance.CurrentCell.removeLamppost(this.rightLight1);
            this.rightLightIndex = -1;
        }
        if (this.rightLightIndex == 2) {
            IsoWorld.instance.CurrentCell.removeLamppost(this.rightLight2);
            this.rightLightIndex = -1;
        }
    }
    
    public void doDamageOverlay() {
        if (this.sprite.modelSlot == null) {
            return;
        }
        this.doDoorDamage();
        this.doWindowDamage();
        this.doOtherBodyWorkDamage();
        this.doBloodOverlay();
    }
    
    private void checkDamage(final VehiclePart vehiclePart, final int n, boolean b) {
        if (b && vehiclePart != null && vehiclePart.getId().startsWith("Window") && vehiclePart.getScriptModelById("Default") != null) {
            b = false;
        }
        final VehicleModelInstance vehicleModelInstance = (VehicleModelInstance)this.sprite.modelSlot.model;
        try {
            vehicleModelInstance.textureDamage1Enables1[n] = 0.0f;
            vehicleModelInstance.textureDamage2Enables1[n] = 0.0f;
            vehicleModelInstance.textureUninstall1[n] = 0.0f;
            if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
                if (vehiclePart.getInventoryItem().getCondition() < 60 && vehiclePart.getInventoryItem().getCondition() >= 40) {
                    vehicleModelInstance.textureDamage1Enables1[n] = 1.0f;
                }
                if (vehiclePart.getInventoryItem().getCondition() < 40) {
                    vehicleModelInstance.textureDamage2Enables1[n] = 1.0f;
                }
                if (vehiclePart.window != null && vehiclePart.window.isOpen() && b) {
                    vehicleModelInstance.textureUninstall1[n] = 1.0f;
                }
            }
            else if (vehiclePart != null && b) {
                vehicleModelInstance.textureUninstall1[n] = 1.0f;
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void checkDamage2(final VehiclePart vehiclePart, final int n, final boolean b) {
        final VehicleModelInstance vehicleModelInstance = (VehicleModelInstance)this.sprite.modelSlot.model;
        try {
            vehicleModelInstance.textureDamage1Enables2[n] = 0.0f;
            vehicleModelInstance.textureDamage2Enables2[n] = 0.0f;
            vehicleModelInstance.textureUninstall2[n] = 0.0f;
            if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
                if (vehiclePart.getInventoryItem().getCondition() < 60 && vehiclePart.getInventoryItem().getCondition() >= 40) {
                    vehicleModelInstance.textureDamage1Enables2[n] = 1.0f;
                }
                if (vehiclePart.getInventoryItem().getCondition() < 40) {
                    vehicleModelInstance.textureDamage2Enables2[n] = 1.0f;
                }
                if (vehiclePart.window != null && vehiclePart.window.isOpen() && b) {
                    vehicleModelInstance.textureUninstall2[n] = 1.0f;
                }
            }
            else if (vehiclePart != null && b) {
                vehicleModelInstance.textureUninstall2[n] = 1.0f;
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void checkUninstall2(final VehiclePart vehiclePart, final int n) {
        final VehicleModelInstance vehicleModelInstance = (VehicleModelInstance)this.sprite.modelSlot.model;
        try {
            vehicleModelInstance.textureUninstall2[n] = 0.0f;
            if (vehiclePart != null && vehiclePart.getInventoryItem() == null) {
                vehicleModelInstance.textureUninstall2[n] = 1.0f;
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void doOtherBodyWorkDamage() {
        this.checkDamage(this.getPartById("EngineDoor"), 0, false);
        this.checkDamage(this.getPartById("EngineDoor"), 3, false);
        this.checkDamage(this.getPartById("EngineDoor"), 11, false);
        this.checkDamage2(this.getPartById("EngineDoor"), 6, true);
        this.checkDamage(this.getPartById("TruckBed"), 4, false);
        this.checkDamage(this.getPartById("TruckBed"), 7, false);
        this.checkDamage(this.getPartById("TruckBed"), 15, false);
        final VehiclePart partById = this.getPartById("TrunkDoor");
        if (partById != null) {
            this.checkDamage2(partById, 10, true);
            if (partById.scriptPart.hasLightsRear) {
                this.checkUninstall2(partById, 12);
                this.checkUninstall2(partById, 1);
                this.checkUninstall2(partById, 5);
                this.checkUninstall2(partById, 9);
            }
        }
        else {
            final VehiclePart partById2 = this.getPartById("DoorRear");
            if (partById2 != null) {
                this.checkDamage2(partById2, 10, true);
                if (partById2.scriptPart.hasLightsRear) {
                    this.checkUninstall2(partById2, 12);
                    this.checkUninstall2(partById2, 1);
                    this.checkUninstall2(partById2, 5);
                    this.checkUninstall2(partById2, 9);
                }
            }
        }
    }
    
    private void doWindowDamage() {
        this.checkDamage(this.getPartById("WindowFrontLeft"), 2, true);
        this.checkDamage(this.getPartById("WindowFrontRight"), 9, true);
        final VehiclePart partById = this.getPartById("WindowRearLeft");
        if (partById != null) {
            this.checkDamage(partById, 6, true);
        }
        else {
            final VehiclePart partById2 = this.getPartById("WindowMiddleLeft");
            if (partById2 != null) {
                this.checkDamage(partById2, 6, true);
            }
        }
        final VehiclePart partById3 = this.getPartById("WindowRearRight");
        if (partById3 != null) {
            this.checkDamage(partById3, 13, true);
        }
        else {
            final VehiclePart partById4 = this.getPartById("WindowMiddleRight");
            if (partById4 != null) {
                this.checkDamage(partById4, 13, true);
            }
        }
        this.checkDamage(this.getPartById("Windshield"), 10, true);
        this.checkDamage(this.getPartById("WindshieldRear"), 14, true);
    }
    
    private void doDoorDamage() {
        this.checkDamage(this.getPartById("DoorFrontLeft"), 1, true);
        this.checkDamage(this.getPartById("DoorFrontRight"), 8, true);
        final VehiclePart partById = this.getPartById("DoorRearLeft");
        if (partById != null) {
            this.checkDamage(partById, 5, true);
        }
        else {
            final VehiclePart partById2 = this.getPartById("DoorMiddleLeft");
            if (partById2 != null) {
                this.checkDamage(partById2, 5, true);
            }
        }
        final VehiclePart partById3 = this.getPartById("DoorRearRight");
        if (partById3 != null) {
            this.checkDamage(partById3, 12, true);
        }
        else {
            final VehiclePart partById4 = this.getPartById("DoorMiddleRight");
            if (partById4 != null) {
                this.checkDamage(partById4, 12, true);
            }
        }
    }
    
    public float getBloodIntensity(final String key) {
        return (this.bloodIntensity.getOrDefault(key, BaseVehicle.BYTE_ZERO) & 0xFF) / 100.0f;
    }
    
    public void setBloodIntensity(final String key, final float n) {
        final byte b = (byte)(PZMath.clamp(n, 0.0f, 1.0f) * 100.0f);
        if (this.bloodIntensity.containsKey(key) && b == this.bloodIntensity.get(key)) {
            return;
        }
        this.bloodIntensity.put(key, b);
        this.doBloodOverlay();
        this.transmitBlood();
    }
    
    public void transmitBlood() {
        if (!GameServer.bServer) {
            return;
        }
        this.updateFlags |= 0x1000;
    }
    
    public void doBloodOverlay() {
        if (this.sprite.modelSlot == null) {
            return;
        }
        final VehicleModelInstance vehicleModelInstance = (VehicleModelInstance)this.sprite.modelSlot.model;
        Arrays.fill(vehicleModelInstance.matrixBlood1Enables1, 0.0f);
        Arrays.fill(vehicleModelInstance.matrixBlood1Enables2, 0.0f);
        Arrays.fill(vehicleModelInstance.matrixBlood2Enables1, 0.0f);
        Arrays.fill(vehicleModelInstance.matrixBlood2Enables2, 0.0f);
        if (Core.getInstance().getOptionBloodDecals() == 0) {
            return;
        }
        this.doBloodOverlayFront(vehicleModelInstance.matrixBlood1Enables1, vehicleModelInstance.matrixBlood1Enables2, this.getBloodIntensity("Front"));
        this.doBloodOverlayRear(vehicleModelInstance.matrixBlood1Enables1, vehicleModelInstance.matrixBlood1Enables2, this.getBloodIntensity("Rear"));
        this.doBloodOverlayLeft(vehicleModelInstance.matrixBlood1Enables1, vehicleModelInstance.matrixBlood1Enables2, this.getBloodIntensity("Left"));
        this.doBloodOverlayRight(vehicleModelInstance.matrixBlood1Enables1, vehicleModelInstance.matrixBlood1Enables2, this.getBloodIntensity("Right"));
        for (final Map.Entry<String, Byte> entry : this.bloodIntensity.entrySet()) {
            final Integer n = BaseVehicle.s_PartToMaskMap.get(entry.getKey());
            if (n != null) {
                vehicleModelInstance.matrixBlood1Enables1[n] = (entry.getValue() & 0xFF) / 100.0f;
            }
        }
        this.doBloodOverlayAux(vehicleModelInstance.matrixBlood2Enables1, vehicleModelInstance.matrixBlood2Enables2, 1.0f);
    }
    
    private void doBloodOverlayAux(final float[] array, final float[] array2, final float n) {
        array[0] = n;
        array2[4] = (array2[6] = n);
        array[4] = (array2[8] = n);
        array[15] = (array[7] = n);
        array2[12] = (array2[10] = n);
        array2[5] = (array2[1] = n);
        array[3] = (array2[9] = n);
        array[12] = (array[8] = n);
        array[1] = (array[11] = n);
        array2[0] = (array[5] = n);
        array[14] = (array[10] = n);
        array[13] = (array[9] = n);
        array[6] = (array[2] = n);
    }
    
    private void doBloodOverlayFront(final float[] array, final float[] array2, final float n) {
        array[0] = n;
        array2[4] = (array2[6] = n);
        array[10] = (array2[8] = n);
    }
    
    private void doBloodOverlayRear(final float[] array, final float[] array2, final float n) {
        array[4] = n;
        array2[12] = (array2[10] = n);
        array2[5] = (array2[1] = n);
        array[14] = (array2[9] = n);
    }
    
    private void doBloodOverlayLeft(final float[] array, final float[] array2, final float n) {
        array[1] = (array[11] = n);
        array[15] = (array[5] = n);
        array[6] = (array[2] = n);
    }
    
    private void doBloodOverlayRight(final float[] array, final float[] array2, final float n) {
        array[8] = (array[3] = n);
        array[7] = (array[12] = n);
        array[13] = (array[9] = n);
    }
    
    public void render(final float n, final float n2, final float n3, final ColorInfo colorInfo, final boolean b, final boolean b2, final Shader shader) {
        if (this.script == null) {
            return;
        }
        if (this.physics != null) {
            this.physics.debug();
        }
        final int playerIndex = IsoCamera.frameState.playerIndex;
        final boolean b3 = IsoCamera.CamCharacter != null && IsoCamera.CamCharacter.getVehicle() == this;
        if (!b3 && !this.square.lighting[playerIndex].bSeen()) {
            return;
        }
        if (b3 || this.square.lighting[playerIndex].bCouldSee()) {
            this.setTargetAlpha(playerIndex, 1.0f);
        }
        else {
            this.setTargetAlpha(playerIndex, 0.0f);
        }
        if (this.sprite.hasActiveModel()) {
            this.updateLights();
            final boolean optionBloodDecals = Core.getInstance().getOptionBloodDecals() != 0;
            if (this.OptionBloodDecals != optionBloodDecals) {
                this.OptionBloodDecals = optionBloodDecals;
                this.doBloodOverlay();
            }
            colorInfo.a = this.getAlpha(playerIndex);
            BaseVehicle.inf.a = colorInfo.a;
            BaseVehicle.inf.r = colorInfo.r;
            BaseVehicle.inf.g = colorInfo.g;
            BaseVehicle.inf.b = colorInfo.b;
            this.sprite.renderVehicle(this.def, this, n, n2, 0.0f, 0.0f, 0.0f, BaseVehicle.inf, true);
        }
        this.updateAlpha(playerIndex);
        if (Core.bDebug && DebugOptions.instance.VehicleRenderArea.getValue()) {
            this.renderAreas();
        }
        if (Core.bDebug && DebugOptions.instance.VehicleRenderAttackPositions.getValue()) {
            this.m_surroundVehicle.render();
        }
        if (Core.bDebug && DebugOptions.instance.VehicleRenderExit.getValue()) {
            this.renderExits();
        }
        if (Core.bDebug && DebugOptions.instance.VehicleRenderIntersectedSquares.getValue()) {
            this.renderIntersectedSquares();
        }
        if (Core.bDebug && DebugOptions.instance.VehicleRenderAuthorizations.getValue()) {
            this.renderAuthorizations();
        }
        if (DebugOptions.instance.VehicleRenderTrailerPositions.getValue()) {
            this.renderTrailerPositions();
        }
        this.renderUsableArea();
    }
    
    @Override
    public void renderlast() {
        final int playerIndex = IsoCamera.frameState.playerIndex;
        for (int i = 0; i < this.parts.size(); ++i) {
            final VehiclePart vehiclePart = this.parts.get(i);
            if (vehiclePart.chatElement != null) {
                if (vehiclePart.chatElement.getHasChatToDisplay()) {
                    if (vehiclePart.getDeviceData() != null && !vehiclePart.getDeviceData().getIsTurnedOn()) {
                        vehiclePart.chatElement.clear(playerIndex);
                    }
                    else {
                        vehiclePart.chatElement.renderBatched(playerIndex, (int)((IsoUtils.XToScreen(this.getX(), this.getY(), this.getZ(), 0) - IsoCamera.getOffX() - this.offsetX + 32 * Core.TileScale) / Core.getInstance().getZoom(playerIndex)), (int)((IsoUtils.YToScreen(this.getX(), this.getY(), this.getZ(), 0) - IsoCamera.getOffY() - this.offsetY + 20 * Core.TileScale) / Core.getInstance().getZoom(playerIndex)));
                    }
                }
            }
        }
    }
    
    public void renderShadow() {
        if (this.physics == null) {
            return;
        }
        if (this.script == null) {
            return;
        }
        final int playerIndex = IsoCamera.frameState.playerIndex;
        if (!this.square.lighting[playerIndex].bSeen()) {
            return;
        }
        if (this.square.lighting[playerIndex].bCouldSee()) {
            this.setTargetAlpha(playerIndex, 1.0f);
        }
        else {
            this.setTargetAlpha(playerIndex, 0.0f);
        }
        final Texture shadowTexture = this.getShadowTexture();
        if (shadowTexture != null && this.getCurrentSquare() != null) {
            final float n = 0.6f * this.getAlpha(playerIndex);
            final ColorInfo lightInfo = this.getCurrentSquare().lighting[playerIndex].lightInfo();
            final float n2 = n * ((lightInfo.r + lightInfo.g + lightInfo.b) / 3.0f);
            if (this.polyDirty) {
                this.getPoly();
            }
            SpriteRenderer.instance.renderPoly(shadowTexture, (float)(int)IsoUtils.XToScreenExact(this.shadowCoord.x2, this.shadowCoord.y2, 0.0f, 0), (float)(int)IsoUtils.YToScreenExact(this.shadowCoord.x2, this.shadowCoord.y2, 0.0f, 0), (float)(int)IsoUtils.XToScreenExact(this.shadowCoord.x1, this.shadowCoord.y1, 0.0f, 0), (float)(int)IsoUtils.YToScreenExact(this.shadowCoord.x1, this.shadowCoord.y1, 0.0f, 0), (float)(int)IsoUtils.XToScreenExact(this.shadowCoord.x4, this.shadowCoord.y4, 0.0f, 0), (float)(int)IsoUtils.YToScreenExact(this.shadowCoord.x4, this.shadowCoord.y4, 0.0f, 0), (float)(int)IsoUtils.XToScreenExact(this.shadowCoord.x3, this.shadowCoord.y3, 0.0f, 0), (float)(int)IsoUtils.YToScreenExact(this.shadowCoord.x3, this.shadowCoord.y3, 0.0f, 0), 1.0f, 1.0f, 1.0f, 0.8f * n2);
        }
    }
    
    public boolean isEnterBlocked(final IsoGameCharacter isoGameCharacter, final int n) {
        return this.isExitBlocked(n);
    }
    
    public boolean isExitBlocked(final int n) {
        final VehicleScript.Position passengerPosition = this.getPassengerPosition(n, "inside");
        final VehicleScript.Position passengerPosition2 = this.getPassengerPosition(n, "outside");
        if (passengerPosition == null || passengerPosition2 == null) {
            return true;
        }
        final Vector3f passengerPositionWorldPos = this.getPassengerPositionWorldPos(passengerPosition2, BaseVehicle.TL_vector3f_pool.get().alloc());
        if (passengerPosition2.area != null) {
            final Vector2 vector2 = BaseVehicle.TL_vector2_pool.get().alloc();
            final Vector2 areaPositionWorld4PlayerInteract = this.areaPositionWorld4PlayerInteract(this.script.getAreaById(passengerPosition2.area), vector2);
            if (areaPositionWorld4PlayerInteract != null) {
                passengerPositionWorldPos.x = areaPositionWorld4PlayerInteract.x;
                passengerPositionWorldPos.y = areaPositionWorld4PlayerInteract.y;
            }
            BaseVehicle.TL_vector2_pool.get().release(vector2);
        }
        passengerPositionWorldPos.z = 0.0f;
        final Vector3f passengerPositionWorldPos2 = this.getPassengerPositionWorldPos(passengerPosition, BaseVehicle.TL_vector3f_pool.get().alloc());
        final boolean lineClearCollide = PolygonalMap2.instance.lineClearCollide(passengerPositionWorldPos2.x, passengerPositionWorldPos2.y, passengerPositionWorldPos.x, passengerPositionWorldPos.y, (int)this.z, this, false, false);
        BaseVehicle.TL_vector3f_pool.get().release(passengerPositionWorldPos);
        BaseVehicle.TL_vector3f_pool.get().release(passengerPositionWorldPos2);
        return lineClearCollide;
    }
    
    public boolean isPassengerUseDoor2(final IsoGameCharacter isoGameCharacter, final int n) {
        final VehicleScript.Position passengerPosition = this.getPassengerPosition(n, "outside2");
        if (passengerPosition != null) {
            final Vector3f passengerPositionWorldPos = this.getPassengerPositionWorldPos(passengerPosition, BaseVehicle.TL_vector3f_pool.get().alloc());
            passengerPositionWorldPos.sub(isoGameCharacter.x, isoGameCharacter.y, isoGameCharacter.z);
            final float length = passengerPositionWorldPos.length();
            BaseVehicle.TL_vector3f_pool.get().release(passengerPositionWorldPos);
            if (length < 2.0f) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isEnterBlocked2(final IsoGameCharacter isoGameCharacter, final int n) {
        return this.isExitBlocked2(n);
    }
    
    public boolean isExitBlocked2(final int n) {
        final VehicleScript.Position passengerPosition = this.getPassengerPosition(n, "inside");
        final VehicleScript.Position passengerPosition2 = this.getPassengerPosition(n, "outside2");
        if (passengerPosition == null || passengerPosition2 == null) {
            return true;
        }
        final Vector3f passengerPositionWorldPos = this.getPassengerPositionWorldPos(passengerPosition2, BaseVehicle.TL_vector3f_pool.get().alloc());
        passengerPositionWorldPos.z = 0.0f;
        final Vector3f passengerPositionWorldPos2 = this.getPassengerPositionWorldPos(passengerPosition, BaseVehicle.TL_vector3f_pool.get().alloc());
        final boolean lineClearCollide = PolygonalMap2.instance.lineClearCollide(passengerPositionWorldPos2.x, passengerPositionWorldPos2.y, passengerPositionWorldPos.x, passengerPositionWorldPos.y, (int)this.z, this, false, false);
        BaseVehicle.TL_vector3f_pool.get().release(passengerPositionWorldPos);
        BaseVehicle.TL_vector3f_pool.get().release(passengerPositionWorldPos2);
        return lineClearCollide;
    }
    
    private void renderExits() {
        final int tileScale = Core.TileScale;
        final Vector3f vector3f = BaseVehicle.TL_vector3f_pool.get().alloc();
        final Vector3f vector3f2 = BaseVehicle.TL_vector3f_pool.get().alloc();
        for (int i = 0; i < this.getMaxPassengers(); ++i) {
            final VehicleScript.Position passengerPosition = this.getPassengerPosition(i, "inside");
            final VehicleScript.Position passengerPosition2 = this.getPassengerPosition(i, "outside");
            if (passengerPosition != null) {
                if (passengerPosition2 != null) {
                    final float n = 0.3f;
                    this.getPassengerPositionWorldPos(passengerPosition2, vector3f);
                    this.getPassengerPositionWorldPos(passengerPosition, vector3f2);
                    final int n2 = (int)Math.floor(vector3f.x - n);
                    final int n3 = (int)Math.floor(vector3f.x + n);
                    final int n4 = (int)Math.floor(vector3f.y - n);
                    for (int n5 = (int)Math.floor(vector3f.y + n), j = n4; j <= n5; ++j) {
                        for (int k = n2; k <= n3; ++k) {
                            final int n6 = (int)IsoUtils.XToScreenExact((float)k, (float)(j + 1), (float)(int)this.z, 0);
                            final int n7 = (int)IsoUtils.YToScreenExact((float)k, (float)(j + 1), (float)(int)this.z, 0);
                            SpriteRenderer.instance.renderPoly((float)n6, (float)n7, (float)(n6 + 32 * tileScale), (float)(n7 - 16 * tileScale), (float)(n6 + 64 * tileScale), (float)n7, (float)(n6 + 32 * tileScale), (float)(n7 + 16 * tileScale), 1.0f, 1.0f, 1.0f, 0.5f);
                        }
                    }
                    final float n8 = 1.0f;
                    float n9 = 1.0f;
                    float n10 = 1.0f;
                    if (this.isExitBlocked(i)) {
                        n10 = (n9 = 0.0f);
                    }
                    this.getController().drawCircle(vector3f2.x, vector3f2.y, n, 0.0f, 0.0f, 1.0f, 1.0f);
                    this.getController().drawCircle(vector3f.x, vector3f.y, n, n8, n9, n10, 1.0f);
                }
            }
        }
        BaseVehicle.TL_vector3f_pool.get().release(vector3f);
        BaseVehicle.TL_vector3f_pool.get().release(vector3f2);
    }
    
    private Vector2 areaPositionLocal(final VehicleScript.Area area) {
        return this.areaPositionLocal(area, new Vector2());
    }
    
    private Vector2 areaPositionLocal(final VehicleScript.Area area, final Vector2 vector2) {
        final Vector2 areaPositionWorld = this.areaPositionWorld(area, vector2);
        final Vector3f vector3f = BaseVehicle.TL_vector3f_pool.get().alloc();
        this.getLocalPos(areaPositionWorld.x, areaPositionWorld.y, 0.0f, vector3f);
        areaPositionWorld.set(vector3f.x, vector3f.z);
        BaseVehicle.TL_vector3f_pool.get().release(vector3f);
        return areaPositionWorld;
    }
    
    public Vector2 areaPositionWorld(final VehicleScript.Area area) {
        return this.areaPositionWorld(area, new Vector2());
    }
    
    public Vector2 areaPositionWorld(final VehicleScript.Area area, final Vector2 vector2) {
        if (area == null) {
            return null;
        }
        final Vector3f worldPos = this.getWorldPos(area.x, 0.0f, area.y, BaseVehicle.TL_vector3f_pool.get().alloc());
        vector2.set(worldPos.x, worldPos.y);
        BaseVehicle.TL_vector3f_pool.get().release(worldPos);
        return vector2;
    }
    
    public Vector2 areaPositionWorld4PlayerInteract(final VehicleScript.Area area) {
        return this.areaPositionWorld4PlayerInteract(area, new Vector2());
    }
    
    public Vector2 areaPositionWorld4PlayerInteract(final VehicleScript.Area area, final Vector2 vector2) {
        final Vector3f extents = this.script.getExtents();
        final Vector3f centerOfMassOffset = this.script.getCenterOfMassOffset();
        final Vector2 areaPositionWorld = this.areaPositionWorld(area, vector2);
        final Vector3f localPos = this.getLocalPos(areaPositionWorld.x, areaPositionWorld.y, 0.0f, BaseVehicle.TL_vector3f_pool.get().alloc());
        if (area.x > centerOfMassOffset.x + extents.x / 2.0f || area.x < centerOfMassOffset.x - extents.x / 2.0f) {
            if (area.x > 0.0f) {
                final Vector3f vector3f = localPos;
                vector3f.x -= area.w * 0.3f;
            }
            else {
                final Vector3f vector3f2 = localPos;
                vector3f2.x += area.w * 0.3f;
            }
        }
        else if (area.y > 0.0f) {
            final Vector3f vector3f3 = localPos;
            vector3f3.z -= area.h * 0.3f;
        }
        else {
            final Vector3f vector3f4 = localPos;
            vector3f4.z += area.h * 0.3f;
        }
        this.getWorldPos(localPos, localPos);
        vector2.set(localPos.x, localPos.y);
        BaseVehicle.TL_vector3f_pool.get().release(localPos);
        return vector2;
    }
    
    private void renderAreas() {
        if (this.getScript() == null) {
            return;
        }
        final Vector3f forwardVector = this.getForwardVector(BaseVehicle.TL_vector3f_pool.get().alloc());
        final Vector2 vector2 = BaseVehicle.TL_vector2_pool.get().alloc();
        for (int i = 0; i < this.parts.size(); ++i) {
            final VehiclePart vehiclePart = this.parts.get(i);
            if (vehiclePart.getArea() != null) {
                final VehicleScript.Area areaById = this.getScript().getAreaById(vehiclePart.getArea());
                if (areaById != null) {
                    final Vector2 areaPositionWorld = this.areaPositionWorld(areaById, vector2);
                    if (areaPositionWorld != null) {
                        final boolean inArea = this.isInArea(areaById.id, IsoPlayer.getInstance());
                        this.getController().drawRect(forwardVector, areaPositionWorld.x - WorldSimulation.instance.offsetX, areaPositionWorld.y - WorldSimulation.instance.offsetY, areaById.w, areaById.h / 2.0f, inArea ? 0.0f : 0.65f, inArea ? 1.0f : 0.65f, inArea ? 1.0f : 0.65f);
                        final Vector2 areaPositionWorld4PlayerInteract = this.areaPositionWorld4PlayerInteract(areaById, vector2);
                        this.getController().drawRect(forwardVector, areaPositionWorld4PlayerInteract.x - WorldSimulation.instance.offsetX, areaPositionWorld4PlayerInteract.y - WorldSimulation.instance.offsetY, 0.1f, 0.1f, 1.0f, 0.0f, 0.0f);
                    }
                }
            }
        }
        BaseVehicle.TL_vector3f_pool.get().release(forwardVector);
        BaseVehicle.TL_vector2_pool.get().release(vector2);
        LineDrawer.drawLine(IsoUtils.XToScreenExact(this.poly.x1, this.poly.y1, 0.0f, 0), IsoUtils.YToScreenExact(this.poly.x1, this.poly.y1, 0.0f, 0), IsoUtils.XToScreenExact(this.poly.x2, this.poly.y2, 0.0f, 0), IsoUtils.YToScreenExact(this.poly.x2, this.poly.y2, 0.0f, 0), 1.0f, 0.5f, 0.5f, 1.0f, 0);
        LineDrawer.drawLine(IsoUtils.XToScreenExact(this.poly.x2, this.poly.y2, 0.0f, 0), IsoUtils.YToScreenExact(this.poly.x2, this.poly.y2, 0.0f, 0), IsoUtils.XToScreenExact(this.poly.x3, this.poly.y3, 0.0f, 0), IsoUtils.YToScreenExact(this.poly.x3, this.poly.y3, 0.0f, 0), 1.0f, 0.5f, 0.5f, 1.0f, 0);
        LineDrawer.drawLine(IsoUtils.XToScreenExact(this.poly.x3, this.poly.y3, 0.0f, 0), IsoUtils.YToScreenExact(this.poly.x3, this.poly.y3, 0.0f, 0), IsoUtils.XToScreenExact(this.poly.x4, this.poly.y4, 0.0f, 0), IsoUtils.YToScreenExact(this.poly.x4, this.poly.y4, 0.0f, 0), 1.0f, 0.5f, 0.5f, 1.0f, 0);
        LineDrawer.drawLine(IsoUtils.XToScreenExact(this.poly.x4, this.poly.y4, 0.0f, 0), IsoUtils.YToScreenExact(this.poly.x4, this.poly.y4, 0.0f, 0), IsoUtils.XToScreenExact(this.poly.x1, this.poly.y1, 0.0f, 0), IsoUtils.YToScreenExact(this.poly.x1, this.poly.y1, 0.0f, 0), 1.0f, 0.5f, 0.5f, 1.0f, 0);
        LineDrawer.drawLine(IsoUtils.XToScreenExact(this.shadowCoord.x1, this.shadowCoord.y1, 0.0f, 0), IsoUtils.YToScreenExact(this.shadowCoord.x1, this.shadowCoord.y1, 0.0f, 0), IsoUtils.XToScreenExact(this.shadowCoord.x2, this.shadowCoord.y2, 0.0f, 0), IsoUtils.YToScreenExact(this.shadowCoord.x2, this.shadowCoord.y2, 0.0f, 0), 0.5f, 1.0f, 0.5f, 1.0f, 0);
        LineDrawer.drawLine(IsoUtils.XToScreenExact(this.shadowCoord.x2, this.shadowCoord.y2, 0.0f, 0), IsoUtils.YToScreenExact(this.shadowCoord.x2, this.shadowCoord.y2, 0.0f, 0), IsoUtils.XToScreenExact(this.shadowCoord.x3, this.shadowCoord.y3, 0.0f, 0), IsoUtils.YToScreenExact(this.shadowCoord.x3, this.shadowCoord.y3, 0.0f, 0), 0.5f, 1.0f, 0.5f, 1.0f, 0);
        LineDrawer.drawLine(IsoUtils.XToScreenExact(this.shadowCoord.x3, this.shadowCoord.y3, 0.0f, 0), IsoUtils.YToScreenExact(this.shadowCoord.x3, this.shadowCoord.y3, 0.0f, 0), IsoUtils.XToScreenExact(this.shadowCoord.x4, this.shadowCoord.y4, 0.0f, 0), IsoUtils.YToScreenExact(this.shadowCoord.x4, this.shadowCoord.y4, 0.0f, 0), 0.5f, 1.0f, 0.5f, 1.0f, 0);
        LineDrawer.drawLine(IsoUtils.XToScreenExact(this.shadowCoord.x4, this.shadowCoord.y4, 0.0f, 0), IsoUtils.YToScreenExact(this.shadowCoord.x4, this.shadowCoord.y4, 0.0f, 0), IsoUtils.XToScreenExact(this.shadowCoord.x1, this.shadowCoord.y1, 0.0f, 0), IsoUtils.YToScreenExact(this.shadowCoord.x1, this.shadowCoord.y1, 0.0f, 0), 0.5f, 1.0f, 0.5f, 1.0f, 0);
    }
    
    private void renderAuthorizations() {
        float n = 0.3f;
        float n2 = 0.3f;
        float n3 = 0.3f;
        if (this.netPlayerAuthorization == 0) {
            n = 1.0f;
        }
        if (this.netPlayerAuthorization == 1) {
            n3 = 1.0f;
        }
        if (this.netPlayerAuthorization == 3) {
            n2 = 1.0f;
        }
        if (this.netPlayerAuthorization == 4) {
            n2 = 1.0f;
            n = 1.0f;
        }
        if (this.netPlayerAuthorization == 2) {
            n3 = 1.0f;
            n = 1.0f;
        }
        LineDrawer.drawLine(IsoUtils.XToScreenExact(this.poly.x1, this.poly.y1, 0.0f, 0), IsoUtils.YToScreenExact(this.poly.x1, this.poly.y1, 0.0f, 0), IsoUtils.XToScreenExact(this.poly.x2, this.poly.y2, 0.0f, 0), IsoUtils.YToScreenExact(this.poly.x2, this.poly.y2, 0.0f, 0), n, n2, n3, 1.0f, 0);
        LineDrawer.drawLine(IsoUtils.XToScreenExact(this.poly.x2, this.poly.y2, 0.0f, 0), IsoUtils.YToScreenExact(this.poly.x2, this.poly.y2, 0.0f, 0), IsoUtils.XToScreenExact(this.poly.x3, this.poly.y3, 0.0f, 0), IsoUtils.YToScreenExact(this.poly.x3, this.poly.y3, 0.0f, 0), n, n2, n3, 1.0f, 0);
        LineDrawer.drawLine(IsoUtils.XToScreenExact(this.poly.x3, this.poly.y3, 0.0f, 0), IsoUtils.YToScreenExact(this.poly.x3, this.poly.y3, 0.0f, 0), IsoUtils.XToScreenExact(this.poly.x4, this.poly.y4, 0.0f, 0), IsoUtils.YToScreenExact(this.poly.x4, this.poly.y4, 0.0f, 0), n, n2, n3, 1.0f, 0);
        LineDrawer.drawLine(IsoUtils.XToScreenExact(this.poly.x4, this.poly.y4, 0.0f, 0), IsoUtils.YToScreenExact(this.poly.x4, this.poly.y4, 0.0f, 0), IsoUtils.XToScreenExact(this.poly.x1, this.poly.y1, 0.0f, 0), IsoUtils.YToScreenExact(this.poly.x1, this.poly.y1, 0.0f, 0), n, n2, n3, 1.0f, 0);
        TextManager.instance.DrawString((double)IsoUtils.XToScreenExact(this.x, this.y, 0.0f, 0), (double)IsoUtils.YToScreenExact(this.x, this.y, 0.0f, 0), invokedynamic(makeConcatWithConstants:(SBSI)Ljava/lang/String;, this.VehicleID, this.netPlayerAuthorization, this.netPlayerId, this.netPlayerTimeout));
    }
    
    private void renderUsableArea() {
        if (this.getScript() == null || !UIManager.VisibleAllUI) {
            return;
        }
        final VehiclePart useablePart = this.getUseablePart(IsoPlayer.getInstance());
        if (useablePart == null) {
            return;
        }
        final VehicleScript.Area areaById = this.getScript().getAreaById(useablePart.getArea());
        if (areaById == null) {
            return;
        }
        final Vector2 vector2 = BaseVehicle.TL_vector2_pool.get().alloc();
        final Vector2 areaPositionWorld = this.areaPositionWorld(areaById, vector2);
        if (areaPositionWorld == null) {
            BaseVehicle.TL_vector2_pool.get().release(vector2);
            return;
        }
        final Vector3f forwardVector = this.getForwardVector(BaseVehicle.TL_vector3f_pool.get().alloc());
        final float n = 0.0f;
        final float n2 = 1.0f;
        final float n3 = 0.0f;
        this.getController().drawRect(forwardVector, areaPositionWorld.x - WorldSimulation.instance.offsetX, areaPositionWorld.y - WorldSimulation.instance.offsetY, areaById.w, areaById.h / 2.0f, n, n2, n3);
        final Vector3f vector3f = forwardVector;
        vector3f.x *= areaById.h / this.script.getModelScale();
        final Vector3f vector3f2 = forwardVector;
        vector3f2.z *= areaById.h / this.script.getModelScale();
        if (useablePart.getDoor() != null && (useablePart.getId().contains("Left") || useablePart.getId().contains("Right"))) {
            if (useablePart.getId().contains("Front")) {
                this.getController().drawRect(forwardVector, areaPositionWorld.x - WorldSimulation.instance.offsetX + forwardVector.x * areaById.h / 2.0f, areaPositionWorld.y - WorldSimulation.instance.offsetY + forwardVector.z * areaById.h / 2.0f, areaById.w, areaById.h / 8.0f, n, n2, n3);
            }
            else if (useablePart.getId().contains("Rear")) {
                this.getController().drawRect(forwardVector, areaPositionWorld.x - WorldSimulation.instance.offsetX - forwardVector.x * areaById.h / 2.0f, areaPositionWorld.y - WorldSimulation.instance.offsetY - forwardVector.z * areaById.h / 2.0f, areaById.w, areaById.h / 8.0f, n, n2, n3);
            }
        }
        BaseVehicle.TL_vector2_pool.get().release(vector2);
        BaseVehicle.TL_vector3f_pool.get().release(forwardVector);
    }
    
    private void renderIntersectedSquares() {
        final PolygonalMap2.VehiclePoly poly = this.getPoly();
        final float min = Math.min(poly.x1, Math.min(poly.x2, Math.min(poly.x3, poly.x4)));
        final float min2 = Math.min(poly.y1, Math.min(poly.y2, Math.min(poly.y3, poly.y4)));
        final float max = Math.max(poly.x1, Math.max(poly.x2, Math.max(poly.x3, poly.x4)));
        final float max2 = Math.max(poly.y1, Math.max(poly.y2, Math.max(poly.y3, poly.y4)));
        for (int i = (int)min2; i < (int)Math.ceil(max2); ++i) {
            for (int j = (int)min; j < (int)Math.ceil(max); ++j) {
                if (this.isIntersectingSquare(j, i, (int)this.z)) {
                    LineDrawer.addLine((float)j, (float)i, (float)(int)this.z, (float)(j + 1), (float)(i + 1), (float)(int)this.z, 1.0f, 1.0f, 1.0f, null, false);
                }
            }
        }
    }
    
    private void renderTrailerPositions() {
        if (this.script == null || this.physics == null) {
            return;
        }
        final Vector3f vector3f = BaseVehicle.TL_vector3f_pool.get().alloc();
        final Vector3f vector3f2 = BaseVehicle.TL_vector3f_pool.get().alloc();
        final Vector3f towingWorldPos = this.getTowingWorldPos("trailer", vector3f2);
        if (towingWorldPos != null) {
            this.physics.drawCircle(towingWorldPos.x, towingWorldPos.y, 0.3f, 1.0f, 1.0f, 1.0f, 1.0f);
        }
        final Vector3f playerTrailerLocalPos = this.getPlayerTrailerLocalPos("trailer", false, vector3f);
        if (playerTrailerLocalPos != null) {
            this.getWorldPos(playerTrailerLocalPos, playerTrailerLocalPos);
            final boolean lineClearCollide = PolygonalMap2.instance.lineClearCollide(vector3f2.x, vector3f2.y, playerTrailerLocalPos.x, playerTrailerLocalPos.y, (int)this.z, this, false, false);
            this.physics.drawCircle(playerTrailerLocalPos.x, playerTrailerLocalPos.y, 0.3f, 1.0f, 1.0f, 1.0f, 1.0f);
            if (lineClearCollide) {
                LineDrawer.addLine(playerTrailerLocalPos.x, playerTrailerLocalPos.y, 0.0f, vector3f2.x, vector3f2.y, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f);
            }
        }
        final Vector3f playerTrailerLocalPos2 = this.getPlayerTrailerLocalPos("trailer", true, vector3f);
        if (playerTrailerLocalPos2 != null) {
            this.getWorldPos(playerTrailerLocalPos2, playerTrailerLocalPos2);
            final boolean lineClearCollide2 = PolygonalMap2.instance.lineClearCollide(vector3f2.x, vector3f2.y, playerTrailerLocalPos2.x, playerTrailerLocalPos2.y, (int)this.z, this, false, false);
            this.physics.drawCircle(playerTrailerLocalPos2.x, playerTrailerLocalPos2.y, 0.3f, 1.0f, lineClearCollide2 ? 0.0f : 1.0f, lineClearCollide2 ? 0.0f : 1.0f, 1.0f);
            if (lineClearCollide2) {
                LineDrawer.addLine(playerTrailerLocalPos2.x, playerTrailerLocalPos2.y, 0.0f, vector3f2.x, vector3f2.y, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f);
            }
        }
        BaseVehicle.TL_vector3f_pool.get().release(vector3f);
        BaseVehicle.TL_vector3f_pool.get().release(vector3f2);
    }
    
    public void getWheelForwardVector(final int n, final Vector3f vector3f) {
        final WheelInfo wheelInfo = this.wheelInfo[n];
        final Matrix4f matrix4f = BaseVehicle.TL_matrix4f_pool.get().alloc();
        matrix4f.rotationY(wheelInfo.steering);
        final Matrix4f matrix = this.jniTransform.getMatrix(BaseVehicle.TL_matrix4f_pool.get().alloc());
        matrix.setTranslation(0.0f, 0.0f, 0.0f);
        matrix4f.mul((Matrix4fc)matrix, matrix4f);
        BaseVehicle.TL_matrix4f_pool.get().release(matrix);
        BaseVehicle.TL_matrix4f_pool.get().release(matrix4f);
        matrix4f.getColumn(2, this.tempVector4f);
        vector3f.set(this.tempVector4f.x, 0.0f, this.tempVector4f.z);
    }
    
    public void tryStartEngine(final boolean b) {
        if (this.getDriver() != null && this.getDriver() instanceof IsoPlayer && ((IsoPlayer)this.getDriver()).isBlockMovement()) {
            return;
        }
        if (this.engineState != engineStateTypes.Idle) {
            return;
        }
        if ((Core.bDebug && DebugOptions.instance.CheatVehicleStartWithoutKey.getValue()) || SandboxOptions.instance.VehicleEasyUse.getValue() || this.isKeysInIgnition() || b || this.getDriver().getInventory().haveThisKeyId(this.getKeyId()) != null || this.isHotwired()) {
            this.engineDoStarting();
        }
        else if (GameServer.bServer) {
            this.getDriver().sendObjectChange("vehicleNoKey");
        }
        else {
            this.getDriver().SayDebug(" [img=media/ui/CarKey_none.png]");
        }
    }
    
    public void tryStartEngine() {
        this.tryStartEngine(false);
    }
    
    public void engineDoIdle() {
        this.engineState = engineStateTypes.Idle;
        this.engineLastUpdateStateTime = System.currentTimeMillis();
        this.transmitEngine();
    }
    
    public void engineDoStarting() {
        this.engineState = engineStateTypes.Starting;
        this.engineLastUpdateStateTime = System.currentTimeMillis();
        this.transmitEngine();
        this.setKeysInIgnition(true);
    }
    
    public boolean isStarting() {
        return this.engineState == engineStateTypes.Starting || this.engineState == engineStateTypes.StartingFailed || this.engineState == engineStateTypes.StartingSuccess || this.engineState == engineStateTypes.StartingFailedNoPower;
    }
    
    private String getEngineSound() {
        if (this.getScript() != null && this.getScript().getSounds().engine != null) {
            return this.getScript().getSounds().engine;
        }
        return "VehicleEngineDefault";
    }
    
    private String getEngineStartSound() {
        if (this.getScript() != null && this.getScript().getSounds().engineStart != null) {
            return this.getScript().getSounds().engineStart;
        }
        return "VehicleStarted";
    }
    
    private String getEngineTurnOffSound() {
        if (this.getScript() != null && this.getScript().getSounds().engineTurnOff != null) {
            return this.getScript().getSounds().engineTurnOff;
        }
        return "VehicleTurnedOff";
    }
    
    private String getIgnitionFailSound() {
        if (this.getScript() != null && this.getScript().getSounds().ignitionFail != null) {
            return this.getScript().getSounds().ignitionFail;
        }
        return "VehicleFailingToStart";
    }
    
    private String getIgnitionFailNoPowerSound() {
        if (this.getScript() != null && this.getScript().getSounds().ignitionFailNoPower != null) {
            return this.getScript().getSounds().ignitionFailNoPower;
        }
        return "VehicleFailingToStartNoPower";
    }
    
    public void engineDoRetryingStarting() {
        this.getEmitter().stopSoundByName(this.getIgnitionFailSound());
        this.getEmitter().playSoundImpl(this.getIgnitionFailSound(), (IsoObject)null);
        this.engineState = engineStateTypes.RetryingStarting;
        this.engineLastUpdateStateTime = System.currentTimeMillis();
        this.transmitEngine();
    }
    
    public void engineDoStartingSuccess() {
        this.getEmitter().stopSoundByName(this.getIgnitionFailSound());
        this.engineState = engineStateTypes.StartingSuccess;
        this.engineLastUpdateStateTime = System.currentTimeMillis();
        if (this.getEngineStartSound().equals(this.getEngineSound())) {
            if (!this.getEmitter().isPlaying(this.combinedEngineSound)) {
                this.combinedEngineSound = this.emitter.playSoundImpl(this.getEngineSound(), (IsoObject)null);
            }
        }
        else {
            this.getEmitter().playSoundImpl(this.getEngineStartSound(), (IsoObject)null);
        }
        this.transmitEngine();
        this.setKeysInIgnition(true);
    }
    
    public void engineDoStartingFailed() {
        this.getEmitter().stopSoundByName(this.getIgnitionFailSound());
        this.getEmitter().playSoundImpl(this.getIgnitionFailSound(), (IsoObject)null);
        this.stopEngineSounds();
        this.engineState = engineStateTypes.StartingFailed;
        this.engineLastUpdateStateTime = System.currentTimeMillis();
        this.transmitEngine();
    }
    
    public void engineDoStartingFailedNoPower() {
        this.getEmitter().stopSoundByName(this.getIgnitionFailNoPowerSound());
        this.getEmitter().playSoundImpl(this.getIgnitionFailNoPowerSound(), (IsoObject)null);
        this.stopEngineSounds();
        this.engineState = engineStateTypes.StartingFailedNoPower;
        this.engineLastUpdateStateTime = System.currentTimeMillis();
        this.transmitEngine();
    }
    
    public void engineDoRunning() {
        this.setNeedPartsUpdate(true);
        this.engineState = engineStateTypes.Running;
        this.engineLastUpdateStateTime = System.currentTimeMillis();
        this.transmitEngine();
    }
    
    public void engineDoStalling() {
        this.getEmitter().playSoundImpl("VehicleRunningOutOfGas", (IsoObject)null);
        this.engineState = engineStateTypes.Stalling;
        this.engineLastUpdateStateTime = System.currentTimeMillis();
        this.stopEngineSounds();
        this.engineSoundIndex = 0;
        this.transmitEngine();
        if (!Core.getInstance().getOptionLeaveKeyInIgnition()) {
            this.setKeysInIgnition(false);
        }
    }
    
    public void engineDoShuttingDown() {
        if (!this.getEngineTurnOffSound().equals(this.getEngineSound())) {
            this.getEmitter().playSoundImpl(this.getEngineTurnOffSound(), (IsoObject)null);
        }
        this.stopEngineSounds();
        this.engineSoundIndex = 0;
        this.engineState = engineStateTypes.ShutingDown;
        this.engineLastUpdateStateTime = System.currentTimeMillis();
        this.transmitEngine();
        if (!Core.getInstance().getOptionLeaveKeyInIgnition()) {
            this.setKeysInIgnition(false);
        }
        final VehiclePart heater = this.getHeater();
        if (heater != null) {
            heater.getModData().rawset((Object)"active", (Object)false);
        }
    }
    
    public void shutOff() {
        if (this.getPartById("GasTank").getContainerContentAmount() == 0.0f) {
            this.engineDoStalling();
            return;
        }
        this.engineDoShuttingDown();
    }
    
    public void resumeRunningAfterLoad() {
        if (GameClient.bClient) {
            if (this.getDriver() == null) {
                return;
            }
            GameClient.instance.sendClientCommandV((IsoPlayer)this.getDriver(), "vehicle", "startEngine", "haveKey", (this.getDriver().getInventory().haveThisKeyId(this.getKeyId()) != null) ? Boolean.TRUE : Boolean.FALSE);
        }
        else {
            if (!this.isEngineWorking()) {
                return;
            }
            this.getEmitter();
            this.engineDoStartingSuccess();
        }
    }
    
    public boolean isEngineStarted() {
        return this.engineState == engineStateTypes.Starting || this.engineState == engineStateTypes.StartingFailed || this.engineState == engineStateTypes.StartingSuccess || this.engineState == engineStateTypes.RetryingStarting;
    }
    
    public boolean isEngineRunning() {
        return this.engineState == engineStateTypes.Running;
    }
    
    public boolean isEngineWorking() {
        for (int i = 0; i < this.parts.size(); ++i) {
            final VehiclePart vehiclePart = this.parts.get(i);
            final String luaFunction = vehiclePart.getLuaFunction("checkEngine");
            if (luaFunction != null) {
                if (!Boolean.TRUE.equals(this.callLuaBoolean(luaFunction, this, vehiclePart))) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean isOperational() {
        for (int i = 0; i < this.parts.size(); ++i) {
            final VehiclePart vehiclePart = this.parts.get(i);
            final String luaFunction = vehiclePart.getLuaFunction("checkOperate");
            if (luaFunction != null) {
                if (!Boolean.TRUE.equals(this.callLuaBoolean(luaFunction, this, vehiclePart))) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean isDriveable() {
        return this.isEngineWorking() && this.isOperational();
    }
    
    public BaseSoundEmitter getEmitter() {
        if (this.emitter == null) {
            if (Core.SoundDisabled || GameServer.bServer) {
                this.emitter = new DummySoundEmitter();
            }
            else {
                final FMODSoundEmitter emitter = new FMODSoundEmitter();
                emitter.parameterUpdater = (IFMODParameterUpdater)this;
                this.emitter = (BaseSoundEmitter)emitter;
            }
        }
        return this.emitter;
    }
    
    public long playSoundImpl(final String s, final IsoObject isoObject) {
        return this.getEmitter().playSoundImpl(s, isoObject);
    }
    
    public int stopSound(final long n) {
        return this.getEmitter().stopSound(n);
    }
    
    public void playSound(final String s) {
        this.getEmitter().playSound(s);
    }
    
    public void updateSounds() {
        if (!GameServer.bServer) {
            if (this.getBatteryCharge() > 0.0f) {
                if (this.lightbarSirenMode.isEnable() && this.soundSirenSignal == -1L) {
                    this.setLightbarSirenMode(this.lightbarSirenMode.get());
                }
            }
            else if (this.soundSirenSignal != -1L) {
                this.getEmitter().stopSound(this.soundSirenSignal);
                this.soundSirenSignal = -1L;
            }
        }
        IsoPlayer isoPlayer = null;
        float n = Float.MAX_VALUE;
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            final IsoPlayer isoPlayer2 = IsoPlayer.players[i];
            if (isoPlayer2 != null && isoPlayer2.getCurrentSquare() != null) {
                float distanceToSquared = IsoUtils.DistanceToSquared(isoPlayer2.getX(), isoPlayer2.getY(), this.x, this.y);
                if (isoPlayer2.Traits.HardOfHearing.isSet()) {
                    distanceToSquared *= 4.5f;
                }
                if (isoPlayer2.Traits.Deaf.isSet()) {
                    distanceToSquared = Float.MAX_VALUE;
                }
                if (distanceToSquared < n) {
                    isoPlayer = isoPlayer2;
                    n = distanceToSquared;
                }
            }
        }
        if (isoPlayer == null) {
            if (this.emitter != null) {
                this.emitter.setPos(this.x, this.y, this.z);
                if (!this.emitter.isEmpty()) {
                    this.emitter.tick();
                }
            }
            return;
        }
        if (!GameServer.bServer) {
            if (!this.getEmitter().isPlaying("VehicleAmbiance")) {
                this.emitter.playAmbientLoopedImpl("VehicleAmbiance");
            }
            final float n2 = n;
            if (n2 > 1200.0f) {
                this.stopEngineSounds();
                if (this.emitter != null && !this.emitter.isEmpty()) {
                    this.emitter.setPos(this.x, this.y, this.z);
                    this.emitter.tick();
                }
                return;
            }
            for (int j = 0; j < this.new_EngineSoundId.length; ++j) {
                if (this.new_EngineSoundId[j] != 0L) {
                    this.getEmitter().setVolume(this.new_EngineSoundId[j], 1.0f - n2 / 1200.0f);
                }
            }
        }
        this.startTime -= GameTime.instance.getMultiplier();
        if (this.getController() == null) {
            return;
        }
        if (GameServer.bServer) {
            return;
        }
        if (this.emitter == null) {
            if (this.engineState != engineStateTypes.Running) {
                return;
            }
            this.getEmitter();
        }
        final boolean anyListenerInside = this.isAnyListenerInside();
        Math.abs(this.getCurrentSpeedKmHour());
        if (this.startTime <= 0.0f && this.engineState == engineStateTypes.Running && !this.getEmitter().isPlaying(this.combinedEngineSound)) {
            this.combinedEngineSound = this.emitter.playSoundImpl(this.getEngineSound(), (IsoObject)null);
            if (this.getEngineSound().equals(this.getEngineStartSound())) {
                this.emitter.setTimelinePosition(this.combinedEngineSound, "idle");
            }
        }
        boolean skidding = false;
        if (!GameClient.bClient || this.isLocalPhysicSim()) {
            for (int k = 0; k < this.script.getWheelCount(); ++k) {
                if (this.wheelInfo[k].skidInfo < 0.15f) {
                    skidding = true;
                    break;
                }
            }
        }
        if (this.getDriver() == null) {
            skidding = false;
        }
        if (skidding != this.skidding) {
            if (skidding) {
                this.skidSound = this.getEmitter().playSoundImpl("VehicleSkid", (IsoObject)null);
            }
            else if (this.skidSound != 0L) {
                this.emitter.stopSound(this.skidSound);
                this.skidSound = 0L;
            }
            this.skidding = skidding;
        }
        if (this.soundBackMoveSignal != -1L && this.emitter != null) {
            this.emitter.set3D(this.soundBackMoveSignal, !anyListenerInside);
        }
        if (this.soundHorn != -1L && this.emitter != null) {
            this.emitter.set3D(this.soundHorn, !anyListenerInside);
        }
        if (this.soundSirenSignal != -1L && this.emitter != null) {
            this.emitter.set3D(this.soundSirenSignal, !anyListenerInside);
        }
        if (this.emitter != null && (this.engineState != engineStateTypes.Idle || !this.emitter.isEmpty())) {
            this.getFMODParameters().update();
            this.emitter.setPos(this.x, this.y, this.z);
            this.emitter.tick();
        }
    }
    
    private boolean updatePart(final VehiclePart vehiclePart) {
        vehiclePart.updateSignalDevice();
        if (vehiclePart.getLight() != null && vehiclePart.getId().contains("Headlight")) {
            vehiclePart.setLightActive(this.getHeadlightsOn() && vehiclePart.getInventoryItem() != null && this.getBatteryCharge() > 0.0f);
        }
        final String luaFunction = vehiclePart.getLuaFunction("update");
        if (luaFunction == null) {
            return false;
        }
        final float lastUpdated = (float)GameTime.getInstance().getWorldAgeHours();
        if (vehiclePart.getLastUpdated() < 0.0f) {
            vehiclePart.setLastUpdated(lastUpdated);
        }
        else if (vehiclePart.getLastUpdated() > lastUpdated) {
            vehiclePart.setLastUpdated(lastUpdated);
        }
        final float n = lastUpdated - vehiclePart.getLastUpdated();
        if ((int)(n * 60.0f) > 0) {
            vehiclePart.setLastUpdated(lastUpdated);
            this.callLuaVoid(luaFunction, this, vehiclePart, (double)(n * 60.0f));
            return true;
        }
        return false;
    }
    
    public void updateParts() {
        if (GameClient.bClient) {
            for (int i = 0; i < this.getPartCount(); ++i) {
                this.getPartByIndex(i).updateSignalDevice();
            }
            return;
        }
        int n = 0;
        for (int j = 0; j < this.getPartCount(); ++j) {
            if (this.updatePart(this.getPartByIndex(j)) && n == 0) {
                n = 1;
            }
            if (j == this.getPartCount() - 1 && n != 0) {
                this.brakeBetweenUpdatesSpeed = 0.0f;
            }
        }
    }
    
    public void drainBatteryUpdateHack() {
        if (this.isEngineRunning()) {
            return;
        }
        for (int i = 0; i < this.parts.size(); ++i) {
            final VehiclePart vehiclePart = this.parts.get(i);
            if (vehiclePart.getDeviceData() != null && vehiclePart.getDeviceData().getIsTurnedOn()) {
                this.updatePart(vehiclePart);
            }
            else if (vehiclePart.getLight() != null && vehiclePart.getLight().getActive()) {
                this.updatePart(vehiclePart);
            }
        }
        if (this.hasLightbar() && (this.lightbarLightsMode.isEnable() || this.lightbarSirenMode.isEnable()) && this.getBattery() != null) {
            this.updatePart(this.getBattery());
        }
    }
    
    public boolean getHeadlightsOn() {
        return this.headlightsOn;
    }
    
    public void setHeadlightsOn(final boolean headlightsOn) {
        if (this.headlightsOn == headlightsOn) {
            return;
        }
        this.headlightsOn = headlightsOn;
        if (GameServer.bServer) {
            this.updateFlags |= 0x8;
        }
    }
    
    public boolean getWindowLightsOn() {
        return this.windowLightsOn;
    }
    
    public void setWindowLightsOn(final boolean windowLightsOn) {
        this.windowLightsOn = windowLightsOn;
    }
    
    public boolean getHeadlightCanEmmitLight() {
        if (this.getBatteryCharge() <= 0.0f) {
            return false;
        }
        final VehiclePart partById = this.getPartById("HeadlightLeft");
        if (partById != null && partById.getInventoryItem() != null) {
            return true;
        }
        final VehiclePart partById2 = this.getPartById("HeadlightRight");
        return partById2 != null && partById2.getInventoryItem() != null;
    }
    
    public boolean getStoplightsOn() {
        return this.stoplightsOn;
    }
    
    public void setStoplightsOn(final boolean stoplightsOn) {
        if (this.stoplightsOn == stoplightsOn) {
            return;
        }
        this.stoplightsOn = stoplightsOn;
        if (GameServer.bServer) {
            this.updateFlags |= 0x8;
        }
    }
    
    public boolean hasHeadlights() {
        return this.getLightCount() > 0;
    }
    
    public void addToWorld() {
        if (this.addedToWorld) {
            DebugLog.General.error(invokedynamic(makeConcatWithConstants:(Lzombie/vehicles/BaseVehicle;S)Ljava/lang/String;, this, this.VehicleID));
            return;
        }
        VehiclesDB2.instance.setVehicleLoaded(this);
        this.addedToWorld = true;
        this.removedFromWorld = false;
        super.addToWorld();
        this.createPhysics();
        for (int i = 0; i < this.parts.size(); ++i) {
            final VehiclePart vehiclePart = this.parts.get(i);
            if (vehiclePart.getItemContainer() != null) {
                vehiclePart.getItemContainer().addItemsToProcessItems();
            }
            if (vehiclePart.getDeviceData() != null && !GameServer.bServer) {
                ZomboidRadio.getInstance().RegisterDevice(vehiclePart);
            }
        }
        if (this.lightbarSirenMode.isEnable()) {
            this.setLightbarSirenMode(this.lightbarSirenMode.get());
            if (this.sirenStartTime <= 0.0) {
                this.sirenStartTime = GameTime.instance.getWorldAgeHours();
            }
        }
        if (this.chunk != null && this.chunk.jobType != IsoChunk.JobType.SoftReset) {
            PolygonalMap2.instance.addVehicleToWorld(this);
        }
        if (this.engineState != engineStateTypes.Idle) {
            this.engineSpeed = ((this.getScript() == null) ? 1000.0 : this.getScript().getEngineIdleSpeed());
        }
        if (this.chunk != null && this.chunk.jobType != IsoChunk.JobType.SoftReset) {
            this.trySpawnKey();
        }
        if (this.emitter != null) {
            SoundManager.instance.registerEmitter(this.emitter);
        }
    }
    
    @Override
    public void removeFromWorld() {
        this.breakConstraint(false, false);
        VehiclesDB2.instance.setVehicleUnloaded(this);
        for (int i = 0; i < this.passengers.length; ++i) {
            if (this.getPassenger(i).character != null) {
                for (int j = 0; j < 4; ++j) {
                    if (this.getPassenger(i).character == IsoPlayer.players[j]) {
                        return;
                    }
                }
            }
        }
        IsoChunk.removeFromCheckedVehicles(this);
        if (this.trace) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Lzombie/vehicles/BaseVehicle;S)Ljava/lang/String;, this, this.VehicleID));
        }
        if (this.removedFromWorld) {
            return;
        }
        if (!this.addedToWorld) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Lzombie/vehicles/BaseVehicle;S)Ljava/lang/String;, this, this.VehicleID));
        }
        this.removedFromWorld = true;
        this.addedToWorld = false;
        for (int k = 0; k < this.parts.size(); ++k) {
            final VehiclePart vehiclePart = this.parts.get(k);
            if (vehiclePart.getItemContainer() != null) {
                vehiclePart.getItemContainer().removeItemsFromProcessItems();
            }
            if (vehiclePart.getDeviceData() != null && !GameServer.bServer) {
                ZomboidRadio.getInstance().UnRegisterDevice(vehiclePart);
            }
        }
        if (this.emitter != null) {
            this.emitter.stopAll();
            SoundManager.instance.unregisterEmitter(this.emitter);
            this.emitter = null;
        }
        if (this.hornemitter != null && this.soundHorn != -1L) {
            this.hornemitter.stopAll();
            this.soundHorn = -1L;
        }
        if (this.createdModel) {
            ModelManager.instance.Remove(this);
            this.createdModel = false;
        }
        this.releaseAnimationPlayers();
        if (this.getController() != null) {
            Bullet.removeVehicle(this.VehicleID);
            this.physics = null;
        }
        if (GameServer.bServer || GameClient.bClient) {
            VehicleManager.instance.removeFromWorld(this);
        }
        else if (this.VehicleID != -1) {
            VehicleIDMap.instance.remove(this.VehicleID);
        }
        IsoWorld.instance.CurrentCell.addVehicles.remove(this);
        IsoWorld.instance.CurrentCell.vehicles.remove(this);
        PolygonalMap2.instance.removeVehicleFromWorld(this);
        if (GameClient.bClient) {
            this.chunk.vehicles.remove(this);
        }
        this.m_surroundVehicle.reset();
        this.removeWorldLights();
        super.removeFromWorld();
    }
    
    public void permanentlyRemove() {
        for (int i = 0; i < this.getMaxPassengers(); ++i) {
            final IsoGameCharacter character = this.getCharacter(i);
            if (character != null) {
                if (GameServer.bServer) {
                    character.sendObjectChange("exitVehicle");
                }
                this.exit(character);
            }
        }
        this.breakConstraint(true, false);
        this.removeFromWorld();
        this.removeFromSquare();
        if (this.chunk != null) {
            this.chunk.vehicles.remove(this);
        }
        VehiclesDB2.instance.removeVehicle(this);
    }
    
    public VehiclePart getBattery() {
        return this.battery;
    }
    
    public void setEngineFeature(final int n, final int n2, final int enginePower) {
        this.engineQuality = PZMath.clamp(n, 0, 100);
        this.engineLoudness = (int)(n2 / 2.7f);
        this.enginePower = enginePower;
    }
    
    public int getEngineQuality() {
        return this.engineQuality;
    }
    
    public int getEngineLoudness() {
        return this.engineLoudness;
    }
    
    public int getEnginePower() {
        return this.enginePower;
    }
    
    public float getBatteryCharge() {
        final VehiclePart battery = this.getBattery();
        if (battery != null && battery.getInventoryItem() instanceof DrainableComboItem) {
            return ((DrainableComboItem)battery.getInventoryItem()).getUsedDelta();
        }
        return 0.0f;
    }
    
    public int getPartCount() {
        return this.parts.size();
    }
    
    public VehiclePart getPartByIndex(final int index) {
        if (index < 0 || index >= this.parts.size()) {
            return null;
        }
        return this.parts.get(index);
    }
    
    public VehiclePart getPartById(final String s) {
        if (s == null) {
            return null;
        }
        for (int i = 0; i < this.parts.size(); ++i) {
            final VehiclePart vehiclePart = this.parts.get(i);
            final VehicleScript.Part scriptPart = vehiclePart.getScriptPart();
            if (scriptPart != null && s.equals(scriptPart.id)) {
                return vehiclePart;
            }
        }
        return null;
    }
    
    public int getNumberOfPartsWithContainers() {
        if (this.getScript() == null) {
            return 0;
        }
        int n = 0;
        for (int i = 0; i < this.getScript().getPartCount(); ++i) {
            if (this.getScript().getPart(i).container != null) {
                ++n;
            }
        }
        return n;
    }
    
    public VehiclePart getPartForSeatContainer(final int n) {
        if (this.getScript() == null || n < 0 || n >= this.getMaxPassengers()) {
            return null;
        }
        for (int i = 0; i < this.getPartCount(); ++i) {
            final VehiclePart partByIndex = this.getPartByIndex(i);
            if (partByIndex.getContainerSeatNumber() == n) {
                return partByIndex;
            }
        }
        return null;
    }
    
    public void transmitPartCondition(final VehiclePart o) {
        if (!GameServer.bServer) {
            return;
        }
        if (!this.parts.contains(o)) {
            return;
        }
        o.updateFlags |= 0x800;
        this.updateFlags |= 0x800;
    }
    
    public void transmitPartItem(final VehiclePart o) {
        if (!GameServer.bServer) {
            return;
        }
        if (!this.parts.contains(o)) {
            return;
        }
        o.updateFlags |= 0x80;
        this.updateFlags |= 0x80;
    }
    
    public void transmitPartModData(final VehiclePart o) {
        if (!GameServer.bServer) {
            return;
        }
        if (!this.parts.contains(o)) {
            return;
        }
        o.updateFlags |= 0x10;
        this.updateFlags |= 0x10;
    }
    
    public void transmitPartUsedDelta(final VehiclePart o) {
        if (!GameServer.bServer) {
            return;
        }
        if (!this.parts.contains(o)) {
            return;
        }
        if (!(o.getInventoryItem() instanceof DrainableComboItem)) {
            return;
        }
        o.updateFlags |= 0x20;
        this.updateFlags |= 0x20;
    }
    
    public void transmitPartDoor(final VehiclePart o) {
        if (!GameServer.bServer) {
            return;
        }
        if (!this.parts.contains(o)) {
            return;
        }
        if (o.getDoor() == null) {
            return;
        }
        o.updateFlags |= 0x200;
        this.updateFlags |= 0x200;
    }
    
    public void transmitPartWindow(final VehiclePart o) {
        if (!GameServer.bServer) {
            return;
        }
        if (!this.parts.contains(o)) {
            return;
        }
        if (o.getWindow() == null) {
            return;
        }
        o.updateFlags |= 0x100;
        this.updateFlags |= 0x100;
    }
    
    public int getLightCount() {
        return this.lights.size();
    }
    
    public VehiclePart getLightByIndex(final int index) {
        if (index < 0 || index >= this.lights.size()) {
            return null;
        }
        return this.lights.get(index);
    }
    
    public String getZone() {
        return this.respawnZone;
    }
    
    public void setZone(final String respawnZone) {
        this.respawnZone = respawnZone;
    }
    
    public boolean isInArea(final String s, final IsoGameCharacter isoGameCharacter) {
        if (s == null || this.getScript() == null) {
            return false;
        }
        final VehicleScript.Area areaById = this.getScript().getAreaById(s);
        if (areaById == null) {
            return false;
        }
        final Vector2 vector2 = BaseVehicle.TL_vector2_pool.get().alloc();
        final Vector2 areaPositionLocal = this.areaPositionLocal(areaById, vector2);
        if (areaPositionLocal == null) {
            BaseVehicle.TL_vector2_pool.get().release(vector2);
            return false;
        }
        final Vector3f vector3f = BaseVehicle.TL_vector3f_pool.get().alloc();
        this.getLocalPos(isoGameCharacter.x, isoGameCharacter.y, this.z, vector3f);
        final float n = areaPositionLocal.x - areaById.w / 2.0f;
        final float n2 = areaPositionLocal.y - areaById.h / 2.0f;
        final float n3 = areaPositionLocal.x + areaById.w / 2.0f;
        final float n4 = areaPositionLocal.y + areaById.h / 2.0f;
        BaseVehicle.TL_vector2_pool.get().release(vector2);
        final boolean b = vector3f.x >= n && vector3f.x < n3 && vector3f.z >= n2 && vector3f.z < n4;
        BaseVehicle.TL_vector3f_pool.get().release(vector3f);
        return b;
    }
    
    public float getAreaDist(final String s, final IsoGameCharacter isoGameCharacter) {
        if (s == null || this.getScript() == null) {
            return 999.0f;
        }
        final VehicleScript.Area areaById = this.getScript().getAreaById(s);
        if (areaById != null) {
            final Vector3f localPos = this.getLocalPos(isoGameCharacter.x, isoGameCharacter.y, this.z, BaseVehicle.TL_vector3f_pool.get().alloc());
            final float abs = Math.abs(areaById.x - areaById.w / 2.0f);
            final float abs2 = Math.abs(areaById.y - areaById.h / 2.0f);
            Math.abs(areaById.x + areaById.w / 2.0f);
            Math.abs(areaById.y + areaById.h / 2.0f);
            final float n = Math.abs(localPos.x + abs) + Math.abs(localPos.z + abs2);
            BaseVehicle.TL_vector3f_pool.get().release(localPos);
            return n;
        }
        return 999.0f;
    }
    
    public Vector2 getAreaCenter(final String s) {
        return this.getAreaCenter(s, new Vector2());
    }
    
    public Vector2 getAreaCenter(final String s, final Vector2 vector2) {
        if (s == null || this.getScript() == null) {
            return null;
        }
        final VehicleScript.Area areaById = this.getScript().getAreaById(s);
        if (areaById == null) {
            return null;
        }
        return this.areaPositionWorld(areaById, vector2);
    }
    
    public boolean isInBounds(final float n, final float n2) {
        return this.getPoly().containsPoint(n, n2);
    }
    
    public boolean canAccessContainer(final int n, final IsoGameCharacter isoGameCharacter) {
        final VehiclePart partByIndex = this.getPartByIndex(n);
        if (partByIndex == null) {
            return false;
        }
        final VehicleScript.Part scriptPart = partByIndex.getScriptPart();
        return scriptPart != null && scriptPart.container != null && (partByIndex.getItemType() == null || partByIndex.getInventoryItem() != null || scriptPart.container.capacity != 0) && (scriptPart.container.luaTest == null || scriptPart.container.luaTest.isEmpty() || Boolean.TRUE.equals(this.callLuaBoolean(scriptPart.container.luaTest, this, partByIndex, isoGameCharacter)));
    }
    
    public boolean canInstallPart(final IsoGameCharacter isoGameCharacter, final VehiclePart o) {
        if (!this.parts.contains(o)) {
            return false;
        }
        final KahluaTable table = o.getTable("install");
        return table != null && table.rawget((Object)"test") instanceof String && Boolean.TRUE.equals(this.callLuaBoolean((String)table.rawget((Object)"test"), this, o, isoGameCharacter));
    }
    
    public boolean canUninstallPart(final IsoGameCharacter isoGameCharacter, final VehiclePart o) {
        if (!this.parts.contains(o)) {
            return false;
        }
        final KahluaTable table = o.getTable("uninstall");
        return table != null && table.rawget((Object)"test") instanceof String && Boolean.TRUE.equals(this.callLuaBoolean((String)table.rawget((Object)"test"), this, o, isoGameCharacter));
    }
    
    private void callLuaVoid(final String s, final Object o, final Object o2) {
        final Object functionObject = LuaManager.getFunctionObject(s);
        if (functionObject == null) {
            return;
        }
        LuaManager.caller.protectedCallVoid(LuaManager.thread, functionObject, o, o2);
    }
    
    private void callLuaVoid(final String s, final Object o, final Object o2, final Object o3) {
        final Object functionObject = LuaManager.getFunctionObject(s);
        if (functionObject == null) {
            return;
        }
        LuaManager.caller.protectedCallVoid(LuaManager.thread, functionObject, o, o2, o3);
    }
    
    private Boolean callLuaBoolean(final String s, final Object o, final Object o2) {
        final Object functionObject = LuaManager.getFunctionObject(s);
        if (functionObject == null) {
            return null;
        }
        return LuaManager.caller.protectedCallBoolean(LuaManager.thread, functionObject, o, o2);
    }
    
    private Boolean callLuaBoolean(final String s, final Object o, final Object o2, final Object o3) {
        final Object functionObject = LuaManager.getFunctionObject(s);
        if (functionObject == null) {
            return null;
        }
        return LuaManager.caller.protectedCallBoolean(LuaManager.thread, functionObject, o, o2, o3);
    }
    
    public short getId() {
        return this.VehicleID;
    }
    
    public void setTireInflation(final int n, final float n2) {
    }
    
    public void setTireRemoved(final int n, final boolean b) {
        Bullet.setTireRemoved(this.VehicleID, n, b);
    }
    
    public Vector3f chooseBestAttackPosition(final IsoGameCharacter isoGameCharacter, final IsoGameCharacter isoGameCharacter2, final Vector3f vector3f) {
        final Vector2f vector2f = BaseVehicle.TL_vector2f_pool.get().alloc();
        final Vector2f positionForZombie = isoGameCharacter.getVehicle().getSurroundVehicle().getPositionForZombie((IsoZombie)isoGameCharacter2, vector2f);
        final float x = vector2f.x;
        final float y = vector2f.y;
        BaseVehicle.TL_vector2f_pool.get().release(vector2f);
        if (positionForZombie != null) {
            return vector3f.set(x, y, this.z);
        }
        return null;
    }
    
    public MinMaxPosition getMinMaxPosition() {
        final MinMaxPosition minMaxPosition = new MinMaxPosition();
        final float x = this.getX();
        final float y = this.getY();
        final Vector3f extents = this.getScript().getExtents();
        final float x2 = extents.x;
        final float z = extents.z;
        switch (this.getDir()) {
            case E:
            case W: {
                minMaxPosition.minX = x - x2 / 2.0f;
                minMaxPosition.maxX = x + x2 / 2.0f;
                minMaxPosition.minY = y - z / 2.0f;
                minMaxPosition.maxY = y + z / 2.0f;
                break;
            }
            case N:
            case S: {
                minMaxPosition.minX = x - z / 2.0f;
                minMaxPosition.maxX = x + z / 2.0f;
                minMaxPosition.minY = y - x2 / 2.0f;
                minMaxPosition.maxY = y + x2 / 2.0f;
                break;
            }
            default: {
                return null;
            }
        }
        return minMaxPosition;
    }
    
    public String getVehicleType() {
        return this.type;
    }
    
    public void setVehicleType(final String type) {
        this.type = type;
    }
    
    public float getMaxSpeed() {
        return this.maxSpeed;
    }
    
    public void setMaxSpeed(final float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }
    
    public void lockServerUpdate(final long n) {
        this.updateLockTimeout = System.currentTimeMillis() + n;
    }
    
    public void changeTransmission(final TransmissionNumber transmissionNumber) {
        this.transmissionNumber = transmissionNumber;
    }
    
    public void tryHotwire(final int n) {
        final int n2 = Math.min(Math.max(100 - this.getEngineQuality(), 5), 50) + n * 4;
        boolean b = false;
        String s;
        if (Rand.Next(100) <= n2) {
            this.setHotwired(true);
            b = true;
            s = "VehicleHotwireSuccess";
        }
        else if (Rand.Next(100) <= 10 - n) {
            this.setHotwiredBroken(true);
            b = true;
            s = "VehicleHotwireFail";
        }
        else {
            s = "VehicleHotwireFail";
        }
        if (s != null) {
            if (GameServer.bServer) {
                LuaManager.GlobalObject.playServerSound(s, this.square);
            }
            else if (this.getDriver() != null) {
                this.getDriver().getEmitter().playSound(s);
            }
        }
        if (b && GameServer.bServer) {
            this.updateFlags |= 0x1000;
        }
    }
    
    public void cheatHotwire(final boolean hotwired, final boolean hotwiredBroken) {
        if (hotwired != this.hotwired || hotwiredBroken != this.hotwiredBroken) {
            this.hotwired = hotwired;
            this.hotwiredBroken = hotwiredBroken;
            if (GameServer.bServer) {
                this.updateFlags |= 0x1000;
            }
        }
    }
    
    public boolean isKeyIsOnDoor() {
        return this.keyIsOnDoor;
    }
    
    public void setKeyIsOnDoor(final boolean keyIsOnDoor) {
        this.keyIsOnDoor = keyIsOnDoor;
    }
    
    public boolean isHotwired() {
        return this.hotwired;
    }
    
    public void setHotwired(final boolean hotwired) {
        this.hotwired = hotwired;
    }
    
    public boolean isHotwiredBroken() {
        return this.hotwiredBroken;
    }
    
    public void setHotwiredBroken(final boolean hotwiredBroken) {
        this.hotwiredBroken = hotwiredBroken;
    }
    
    public IsoGameCharacter getDriver() {
        final Passenger passenger = this.getPassenger(0);
        return (passenger == null) ? null : passenger.character;
    }
    
    public boolean isKeysInIgnition() {
        return this.keysInIgnition;
    }
    
    public void setKeysInIgnition(final boolean b) {
        final IsoGameCharacter driver = this.getDriver();
        if (driver == null) {
            return;
        }
        this.setAlarmed(false);
        if (GameClient.bClient && (!(driver instanceof IsoPlayer) || !((IsoPlayer)driver).isLocalPlayer())) {
            return;
        }
        if (!this.isHotwired()) {
            if (!GameServer.bServer && b && !this.keysInIgnition) {
                final InventoryItem haveThisKeyId = this.getDriver().getInventory().haveThisKeyId(this.getKeyId());
                if (haveThisKeyId != null) {
                    this.setCurrentKey(haveThisKeyId);
                    final InventoryItem containingItem = haveThisKeyId.getContainer().getContainingItem();
                    if (containingItem instanceof InventoryContainer && "KeyRing".equals(containingItem.getType())) {
                        haveThisKeyId.getModData().rawset((Object)"keyRing", (Object)(double)containingItem.getID());
                    }
                    else if (haveThisKeyId.hasModData()) {
                        haveThisKeyId.getModData().rawset((Object)"keyRing", (Object)null);
                    }
                    haveThisKeyId.getContainer().DoRemoveItem(haveThisKeyId);
                    this.keysInIgnition = b;
                    if (GameClient.bClient) {
                        GameClient.instance.sendClientCommandV((IsoPlayer)this.getDriver(), "vehicle", "putKeyInIgnition", "key", haveThisKeyId);
                    }
                }
            }
            if (!b && this.keysInIgnition && !GameServer.bServer) {
                if (this.currentKey == null) {
                    this.currentKey = this.createVehicleKey();
                }
                final InventoryItem currentKey = this.getCurrentKey();
                ItemContainer itemContainer = this.getDriver().getInventory();
                if (currentKey.hasModData() && currentKey.getModData().rawget((Object)"keyRing") instanceof Double) {
                    final InventoryItem itemWithID = itemContainer.getItemWithID(((Double)currentKey.getModData().rawget((Object)"keyRing")).intValue());
                    if (itemWithID instanceof InventoryContainer && "KeyRing".equals(itemWithID.getType())) {
                        itemContainer = ((InventoryContainer)itemWithID).getInventory();
                    }
                    currentKey.getModData().rawset((Object)"keyRing", (Object)null);
                }
                itemContainer.addItem(currentKey);
                this.setCurrentKey(null);
                this.keysInIgnition = b;
                if (GameClient.bClient) {
                    GameClient.instance.sendClientCommand((IsoPlayer)this.getDriver(), "vehicle", "removeKeyFromIgnition", null);
                }
            }
        }
    }
    
    public void putKeyInIgnition(final InventoryItem currentKey) {
        if (!GameServer.bServer) {
            return;
        }
        if (!(currentKey instanceof Key)) {
            return;
        }
        if (this.keysInIgnition) {
            return;
        }
        this.keysInIgnition = true;
        this.keyIsOnDoor = false;
        this.currentKey = currentKey;
        this.updateFlags |= 0x1000;
    }
    
    public void removeKeyFromIgnition() {
        if (!GameServer.bServer) {
            return;
        }
        if (!this.keysInIgnition) {
            return;
        }
        this.keysInIgnition = false;
        this.currentKey = null;
        this.updateFlags |= 0x1000;
    }
    
    public void putKeyOnDoor(final InventoryItem currentKey) {
        if (!GameServer.bServer) {
            return;
        }
        if (!(currentKey instanceof Key)) {
            return;
        }
        if (this.keyIsOnDoor) {
            return;
        }
        this.keyIsOnDoor = true;
        this.keysInIgnition = false;
        this.currentKey = currentKey;
        this.updateFlags |= 0x1000;
    }
    
    public void removeKeyFromDoor() {
        if (!GameServer.bServer) {
            return;
        }
        if (!this.keyIsOnDoor) {
            return;
        }
        this.keyIsOnDoor = false;
        this.currentKey = null;
        this.updateFlags |= 0x1000;
    }
    
    public void syncKeyInIgnition(final boolean keysInIgnition, final boolean keyIsOnDoor, final InventoryItem currentKey) {
        if (!GameClient.bClient) {
            return;
        }
        if (this.getDriver() instanceof IsoPlayer && ((IsoPlayer)this.getDriver()).isLocalPlayer()) {
            return;
        }
        this.keysInIgnition = keysInIgnition;
        this.keyIsOnDoor = keyIsOnDoor;
        this.currentKey = currentKey;
    }
    
    private void randomizeContainers() {
        if (GameClient.bClient) {
            return;
        }
        boolean b = true;
        final String substring = this.getScriptName().substring(this.getScriptName().indexOf(46) + 1);
        ItemPickerJava.VehicleDistribution vehicleDistribution = (ItemPickerJava.VehicleDistribution)ItemPickerJava.VehicleDistributions.get(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, substring, this.getSkinIndex()));
        if (vehicleDistribution != null) {
            b = false;
        }
        else {
            vehicleDistribution = (ItemPickerJava.VehicleDistribution)ItemPickerJava.VehicleDistributions.get((Object)substring);
        }
        if (vehicleDistribution == null) {
            for (int i = 0; i < this.parts.size(); ++i) {
                if (this.parts.get(i).getItemContainer() != null) {
                    DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, substring));
                    return;
                }
            }
            return;
        }
        ItemPickerJava.ItemPickerRoom normal;
        if (b && Rand.Next(100) <= 8 && !vehicleDistribution.Specific.isEmpty()) {
            normal = PZArrayUtil.pickRandom(vehicleDistribution.Specific);
        }
        else {
            normal = vehicleDistribution.Normal;
        }
        if (!StringUtils.isNullOrWhitespace(this.specificDistributionId)) {
            for (int j = 0; j < vehicleDistribution.Specific.size(); ++j) {
                final ItemPickerJava.ItemPickerRoom itemPickerRoom = vehicleDistribution.Specific.get(j);
                if (this.specificDistributionId.equals(itemPickerRoom.specificId)) {
                    normal = itemPickerRoom;
                    break;
                }
            }
        }
        for (int k = 0; k < this.parts.size(); ++k) {
            final VehiclePart vehiclePart = this.parts.get(k);
            if (vehiclePart.getItemContainer() != null) {
                if (!vehiclePart.getItemContainer().bExplored) {
                    vehiclePart.getItemContainer().clear();
                    if (Rand.Next(100) <= 100) {
                        this.randomizeContainer(vehiclePart, normal);
                    }
                    vehiclePart.getItemContainer().setExplored(true);
                }
            }
        }
    }
    
    private void randomizeContainer(final VehiclePart vehiclePart, final ItemPickerJava.ItemPickerRoom itemPickerRoom) {
        if (GameClient.bClient) {
            return;
        }
        if (itemPickerRoom == null) {
            return;
        }
        if (!vehiclePart.getId().contains("Seat") && !itemPickerRoom.Containers.containsKey((Object)vehiclePart.getId())) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, vehiclePart.getId(), this.getScriptName().replaceFirst("Base.", "")));
        }
        ItemPickerJava.fillContainerType(itemPickerRoom, vehiclePart.getItemContainer(), "", null);
        if (!GameServer.bServer || !vehiclePart.getItemContainer().getItems().isEmpty()) {}
    }
    
    public boolean hasHorn() {
        return this.script.getSounds().hornEnable;
    }
    
    public boolean hasLightbar() {
        final VehiclePart partById = this.getPartById("lightbar");
        return partById != null && partById.getCondition() > 0;
    }
    
    public void onHornStart() {
        this.soundHornOn = true;
        if (GameServer.bServer) {
            this.updateFlags |= 0x400;
            if (this.script.getSounds().hornEnable) {
                WorldSoundManager.instance.addSound(this, (int)this.getX(), (int)this.getY(), (int)this.getZ(), 150, 150, false);
            }
            return;
        }
        if (this.soundHorn != -1L) {
            this.hornemitter.stopSound(this.soundHorn);
        }
        if (this.script.getSounds().hornEnable) {
            this.hornemitter = IsoWorld.instance.getFreeEmitter(this.getX(), this.getY(), (float)(int)this.getZ());
            this.soundHorn = this.hornemitter.playSoundLoopedImpl(this.script.getSounds().horn);
            this.hornemitter.set3D(this.soundHorn, !this.isAnyListenerInside());
            this.hornemitter.setVolume(this.soundHorn, 1.0f);
            this.hornemitter.setPitch(this.soundHorn, 1.0f);
            if (!GameClient.bClient) {
                WorldSoundManager.instance.addSound(this, (int)this.getX(), (int)this.getY(), (int)this.getZ(), 150, 150, false);
            }
        }
    }
    
    public void onHornStop() {
        this.soundHornOn = false;
        if (GameServer.bServer) {
            this.updateFlags |= 0x400;
            return;
        }
        if (this.script.getSounds().hornEnable && this.soundHorn != -1L) {
            this.hornemitter.stopSound(this.soundHorn);
            this.soundHorn = -1L;
        }
    }
    
    public boolean hasBackSignal() {
        return this.script != null && this.script.getSounds().backSignalEnable;
    }
    
    public boolean isBackSignalEmitting() {
        return this.soundBackMoveSignal != -1L;
    }
    
    public void onBackMoveSignalStart() {
        this.soundBackMoveOn = true;
        if (GameServer.bServer) {
            this.updateFlags |= 0x400;
            return;
        }
        if (this.soundBackMoveSignal != -1L) {
            this.emitter.stopSound(this.soundBackMoveSignal);
        }
        if (this.script.getSounds().backSignalEnable) {
            this.soundBackMoveSignal = this.emitter.playSoundLoopedImpl(this.script.getSounds().backSignal);
            this.emitter.set3D(this.soundBackMoveSignal, !this.isAnyListenerInside());
        }
    }
    
    public void onBackMoveSignalStop() {
        this.soundBackMoveOn = false;
        if (GameServer.bServer) {
            this.updateFlags |= 0x400;
            return;
        }
        if (this.script.getSounds().backSignalEnable && this.soundBackMoveSignal != -1L) {
            this.emitter.stopSound(this.soundBackMoveSignal);
            this.soundBackMoveSignal = -1L;
        }
    }
    
    public int getLightbarLightsMode() {
        return this.lightbarLightsMode.get();
    }
    
    public void setLightbarLightsMode(final int n) {
        this.lightbarLightsMode.set(n);
        if (GameServer.bServer) {
            this.updateFlags |= 0x400;
        }
    }
    
    public int getLightbarSirenMode() {
        return this.lightbarSirenMode.get();
    }
    
    public void setLightbarSirenMode(final int n) {
        if (this.soundSirenSignal != -1L) {
            this.getEmitter().stopSound(this.soundSirenSignal);
            this.soundSirenSignal = -1L;
        }
        this.lightbarSirenMode.set(n);
        if (GameServer.bServer) {
            this.updateFlags |= 0x400;
            return;
        }
        if (this.lightbarSirenMode.isEnable() && this.getBatteryCharge() > 0.0f) {
            this.soundSirenSignal = this.getEmitter().playSoundLoopedImpl(this.lightbarSirenMode.getSoundName(this.script.getLightbar()));
            this.getEmitter().set3D(this.soundSirenSignal, !this.isAnyListenerInside());
        }
    }
    
    public HashMap<String, String> getChoosenParts() {
        return this.choosenParts;
    }
    
    public float getMass() {
        return this.mass;
    }
    
    public void setMass(final float mass) {
        this.mass = mass;
    }
    
    public float getInitialMass() {
        return this.initialMass;
    }
    
    public void setInitialMass(final float initialMass) {
        this.initialMass = initialMass;
    }
    
    public void updateTotalMass() {
        float n = 0.0f;
        for (int i = 0; i < this.parts.size(); ++i) {
            final VehiclePart vehiclePart = this.parts.get(i);
            if (vehiclePart.getItemContainer() != null) {
                n += vehiclePart.getItemContainer().getCapacityWeight();
            }
            if (vehiclePart.getInventoryItem() != null) {
                n += vehiclePart.getInventoryItem().getWeight();
            }
        }
        this.setMass((float)Math.round(this.getInitialMass() + n));
        if (this.physics != null) {
            Bullet.setVehicleMass(this.VehicleID, this.getMass());
        }
    }
    
    public float getBrakingForce() {
        return this.brakingForce;
    }
    
    public void setBrakingForce(final float brakingForce) {
        this.brakingForce = brakingForce;
    }
    
    public float getBaseQuality() {
        return this.baseQuality;
    }
    
    public void setBaseQuality(final float baseQuality) {
        this.baseQuality = baseQuality;
    }
    
    public float getCurrentSteering() {
        return this.currentSteering;
    }
    
    public void setCurrentSteering(final float currentSteering) {
        this.currentSteering = currentSteering;
    }
    
    public boolean isDoingOffroad() {
        if (this.getCurrentSquare() == null) {
            return false;
        }
        final IsoObject floor = this.getCurrentSquare().getFloor();
        if (floor == null || floor.getSprite() == null) {
            return false;
        }
        final String name = floor.getSprite().getName();
        return name != null && (!name.contains("carpentry_02") && !name.contains("blends_street") && !name.contains("floors_exterior_street"));
    }
    
    public boolean isBraking() {
        return this.isBraking;
    }
    
    public void setBraking(final boolean isBraking) {
        this.isBraking = isBraking;
        if (isBraking && this.brakeBetweenUpdatesSpeed == 0.0f) {
            this.brakeBetweenUpdatesSpeed = Math.abs(this.getCurrentSpeedKmHour());
        }
    }
    
    public void updatePartStats() {
        this.setBrakingForce(0.0f);
        this.engineLoudness = (int)(this.getScript().getEngineLoudness() * SandboxOptions.instance.ZombieAttractionMultiplier.getValue() / 2.0);
        boolean b = false;
        for (int i = 0; i < this.getPartCount(); ++i) {
            final VehiclePart partByIndex = this.getPartByIndex(i);
            if (partByIndex.getInventoryItem() != null) {
                if (partByIndex.getInventoryItem().getBrakeForce() > 0.0f) {
                    final float numberByCondition = VehiclePart.getNumberByCondition(partByIndex.getInventoryItem().getBrakeForce(), (float)partByIndex.getInventoryItem().getCondition(), 5.0f);
                    this.setBrakingForce(this.getBrakingForce() + (numberByCondition + numberByCondition / 50.0f * partByIndex.getMechanicSkillInstaller()));
                }
                if (partByIndex.getInventoryItem().getWheelFriction() > 0.0f) {
                    partByIndex.setWheelFriction(0.0f);
                    partByIndex.setWheelFriction(Math.min(2.3f, VehiclePart.getNumberByCondition(partByIndex.getInventoryItem().getWheelFriction(), (float)partByIndex.getInventoryItem().getCondition(), 0.2f) + 0.1f * partByIndex.getMechanicSkillInstaller()));
                }
                if (partByIndex.getInventoryItem().getSuspensionCompression() > 0.0f) {
                    partByIndex.setSuspensionCompression(VehiclePart.getNumberByCondition(partByIndex.getInventoryItem().getSuspensionCompression(), (float)partByIndex.getInventoryItem().getCondition(), 0.6f));
                    partByIndex.setSuspensionDamping(VehiclePart.getNumberByCondition(partByIndex.getInventoryItem().getSuspensionDamping(), (float)partByIndex.getInventoryItem().getCondition(), 0.6f));
                }
                if (partByIndex.getInventoryItem().getEngineLoudness() > 0.0f) {
                    partByIndex.setEngineLoudness(VehiclePart.getNumberByCondition(partByIndex.getInventoryItem().getEngineLoudness(), (float)partByIndex.getInventoryItem().getCondition(), 10.0f));
                    this.engineLoudness *= (int)(1.0f + (100.0f - partByIndex.getEngineLoudness()) / 100.0f);
                    b = true;
                }
            }
        }
        if (!b) {
            this.engineLoudness *= 2;
        }
    }
    
    public void transmitEngine() {
        if (!GameServer.bServer) {
            return;
        }
        this.updateFlags |= 0x4;
    }
    
    public void setRust(final float n) {
        this.rust = PZMath.clamp(n, 0.0f, 1.0f);
    }
    
    public float getRust() {
        return this.rust;
    }
    
    public void transmitRust() {
        if (!GameServer.bServer) {
            return;
        }
        this.updateFlags |= 0x1000;
    }
    
    public void updateBulletStats() {
        if (this.getScriptName().contains("Burnt") || !WorldSimulation.instance.created) {
            return;
        }
        final float[] wheelParams = BaseVehicle.wheelParams;
        final double n = 2.4;
        int n2 = 5;
        double n3;
        float n4;
        if (this.isInForest() && this.isDoingOffroad() && Math.abs(this.getCurrentSpeedKmHour()) > 1.0f) {
            n3 = Rand.Next(0.08f, 0.18f);
            n4 = 0.7f;
            n2 = 3;
        }
        else if (this.isDoingOffroad() && Math.abs(this.getCurrentSpeedKmHour()) > 1.0f) {
            n3 = Rand.Next(0.05f, 0.15f);
            n4 = 0.7f;
        }
        else {
            if (Math.abs(this.getCurrentSpeedKmHour()) > 1.0f && Rand.Next(100) < 10) {
                n3 = Rand.Next(0.05f, 0.15f);
            }
            else {
                n3 = 0.0;
            }
            n4 = 1.0f;
        }
        if (RainManager.isRaining()) {
            n4 -= 0.3f;
        }
        final Vector3f vector3f = BaseVehicle.TL_vector3f_pool.get().alloc();
        for (int i = 0; i < this.script.getWheelCount(); ++i) {
            this.updateBulletStatsWheel(i, wheelParams, vector3f, n4, n2, n, n3);
        }
        BaseVehicle.TL_vector3f_pool.get().release(vector3f);
        if (SystemDisabler.getdoVehicleLowRider() && this.isKeyboardControlled()) {
            final float n5 = 1.6f;
            final float n6 = 1.0f;
            if (GameKeyboard.isKeyDown(79)) {
                final float[] lowRiderParam = BaseVehicle.lowRiderParam;
                final int n7 = 0;
                lowRiderParam[n7] += (n5 - BaseVehicle.lowRiderParam[0]) * n6;
            }
            else {
                final float[] lowRiderParam2 = BaseVehicle.lowRiderParam;
                final int n8 = 0;
                lowRiderParam2[n8] += (0.0f - BaseVehicle.lowRiderParam[0]) * 0.05f;
            }
            if (GameKeyboard.isKeyDown(80)) {
                final float[] lowRiderParam3 = BaseVehicle.lowRiderParam;
                final int n9 = 1;
                lowRiderParam3[n9] += (n5 - BaseVehicle.lowRiderParam[1]) * n6;
            }
            else {
                final float[] lowRiderParam4 = BaseVehicle.lowRiderParam;
                final int n10 = 1;
                lowRiderParam4[n10] += (0.0f - BaseVehicle.lowRiderParam[1]) * 0.05f;
            }
            if (GameKeyboard.isKeyDown(75)) {
                final float[] lowRiderParam5 = BaseVehicle.lowRiderParam;
                final int n11 = 2;
                lowRiderParam5[n11] += (n5 - BaseVehicle.lowRiderParam[2]) * n6;
            }
            else {
                final float[] lowRiderParam6 = BaseVehicle.lowRiderParam;
                final int n12 = 2;
                lowRiderParam6[n12] += (0.0f - BaseVehicle.lowRiderParam[2]) * 0.05f;
            }
            if (GameKeyboard.isKeyDown(76)) {
                final float[] lowRiderParam7 = BaseVehicle.lowRiderParam;
                final int n13 = 3;
                lowRiderParam7[n13] += (n5 - BaseVehicle.lowRiderParam[3]) * n6;
            }
            else {
                final float[] lowRiderParam8 = BaseVehicle.lowRiderParam;
                final int n14 = 3;
                lowRiderParam8[n14] += (0.0f - BaseVehicle.lowRiderParam[3]) * 0.05f;
            }
            wheelParams[23] = BaseVehicle.lowRiderParam[0];
            wheelParams[22] = BaseVehicle.lowRiderParam[1];
            wheelParams[21] = BaseVehicle.lowRiderParam[2];
            wheelParams[20] = BaseVehicle.lowRiderParam[3];
        }
        Bullet.setVehicleParams(this.VehicleID, wheelParams);
    }
    
    private void updateBulletStatsWheel(final int n, final float[] array, final Vector3f vector3f, final float n2, final int n3, final double n4, final double n5) {
        final int n6 = n * 6;
        final VehicleScript.Wheel wheel = this.script.getWheel(n);
        final Vector3f worldPos = this.getWorldPos(wheel.offset.x, wheel.offset.y, wheel.offset.z, vector3f);
        final VehiclePart partById = this.getPartById(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, wheel.getId()));
        final VehiclePart partById2 = this.getPartById(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, wheel.getId()));
        if (partById != null && partById.getInventoryItem() != null) {
            array[n6 + 0] = 1.0f;
            array[n6 + 1] = Math.min(partById.getContainerContentAmount() / (partById.getContainerCapacity() - 10), 1.0f);
            array[n6 + 2] = n2 * partById.getWheelFriction();
            if (partById2 != null && partById2.getInventoryItem() != null) {
                array[n6 + 3] = partById2.getSuspensionDamping();
                array[n6 + 4] = partById2.getSuspensionCompression();
            }
            else {
                array[n6 + 4] = (array[n6 + 3] = 0.1f);
            }
            if (Rand.Next(n3) == 0) {
                array[n6 + 5] = (float)(Math.sin(n4 * worldPos.x()) * Math.sin(n4 * worldPos.y()) * n5);
            }
            else {
                array[n6 + 5] = 0.0f;
            }
        }
        else {
            array[n6 + 0] = 0.0f;
            array[n6 + 1] = 30.0f;
            array[n6 + 2] = 0.0f;
            array[n6 + 3] = 2.88f;
            array[n6 + 4] = 3.83f;
            if (Rand.Next(n3) == 0) {
                array[n6 + 5] = (float)(Math.sin(n4 * worldPos.x()) * Math.sin(n4 * worldPos.y()) * n5);
            }
            else {
                array[n6 + 5] = 0.0f;
            }
        }
        if (this.forcedFriction > -1.0f) {
            array[n6 + 2] = this.forcedFriction;
        }
    }
    
    public void setActiveInBullet(final boolean b) {
        if (!b && this.isEngineRunning()) {
            return;
        }
    }
    
    public boolean areAllDoorsLocked() {
        for (int i = 0; i < this.getMaxPassengers(); ++i) {
            final VehiclePart passengerDoor = this.getPassengerDoor(i);
            if (passengerDoor != null && passengerDoor.getDoor() != null && !passengerDoor.getDoor().isLocked()) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isAnyDoorLocked() {
        for (int i = 0; i < this.getMaxPassengers(); ++i) {
            final VehiclePart passengerDoor = this.getPassengerDoor(i);
            if (passengerDoor != null && passengerDoor.getDoor() != null && passengerDoor.getDoor().isLocked()) {
                return true;
            }
        }
        return false;
    }
    
    public float getRemainingFuelPercentage() {
        final VehiclePart partById = this.getPartById("GasTank");
        if (partById == null) {
            return 0.0f;
        }
        return partById.getContainerContentAmount() / partById.getContainerCapacity() * 100.0f;
    }
    
    public int getMechanicalID() {
        return this.mechanicalID;
    }
    
    public void setMechanicalID(final int mechanicalID) {
        this.mechanicalID = mechanicalID;
    }
    
    public boolean needPartsUpdate() {
        return this.needPartsUpdate;
    }
    
    public void setNeedPartsUpdate(final boolean needPartsUpdate) {
        this.needPartsUpdate = needPartsUpdate;
    }
    
    public VehiclePart getHeater() {
        return this.getPartById("Heater");
    }
    
    public int windowsOpen() {
        int n = 0;
        for (int i = 0; i < this.getPartCount(); ++i) {
            final VehiclePart partByIndex = this.getPartByIndex(i);
            if (partByIndex.window != null && partByIndex.window.open) {
                ++n;
            }
        }
        return n;
    }
    
    public boolean isAlarmed() {
        return this.alarmed;
    }
    
    public void setAlarmed(final boolean alarmed) {
        this.alarmed = alarmed;
    }
    
    public void triggerAlarm() {
        if (!this.alarmed) {
            return;
        }
        this.alarmed = false;
        this.alarmTime = Rand.Next(1500, 3000);
        this.alarmAccumulator = 0.0f;
    }
    
    private void doAlarm() {
        if (this.alarmTime > 0) {
            if (this.getBatteryCharge() <= 0.0f) {
                if (this.soundHornOn) {
                    this.onHornStop();
                }
                this.alarmTime = -1;
                return;
            }
            this.alarmAccumulator += GameTime.instance.getMultiplier() / 1.6f;
            if (this.alarmAccumulator >= this.alarmTime) {
                this.onHornStop();
                this.setHeadlightsOn(false);
                this.alarmTime = -1;
                return;
            }
            final int n = (int)this.alarmAccumulator / 20;
            if (!this.soundHornOn && n % 2 == 0) {
                this.onHornStart();
                this.setHeadlightsOn(true);
            }
            if (this.soundHornOn && n % 2 == 1) {
                this.onHornStop();
                this.setHeadlightsOn(false);
            }
        }
    }
    
    public boolean isMechanicUIOpen() {
        return this.mechanicUIOpen;
    }
    
    public void setMechanicUIOpen(final boolean mechanicUIOpen) {
        this.mechanicUIOpen = mechanicUIOpen;
    }
    
    public void damagePlayers(final float n) {
        if (!SandboxOptions.instance.PlayerDamageFromCrash.getValue()) {
            return;
        }
        if (GameClient.bClient) {
            return;
        }
        for (int i = 0; i < this.passengers.length; ++i) {
            if (this.getPassenger(i).character != null) {
                final IsoGameCharacter character = this.getPassenger(i).character;
                if (GameServer.bServer && character instanceof IsoPlayer) {
                    GameServer.sendPlayerDamagedByCarCrash((IsoPlayer)character, n);
                }
                else {
                    this.addRandomDamageFromCrash(character, n);
                }
            }
        }
    }
    
    public void addRandomDamageFromCrash(final IsoGameCharacter isoGameCharacter, final float n) {
        int n2 = 1;
        if (n > 40.0f) {
            n2 = Rand.Next(1, 3);
        }
        if (n > 70.0f) {
            n2 = Rand.Next(2, 4);
        }
        int n3 = 0;
        for (int i = 0; i < isoGameCharacter.getVehicle().getPartCount(); ++i) {
            final VehiclePart partByIndex = isoGameCharacter.getVehicle().getPartByIndex(i);
            if (partByIndex.window != null && partByIndex.getCondition() < 15) {
                ++n3;
            }
        }
        for (int j = 0; j < n2; ++j) {
            final BodyPart bodyPart = isoGameCharacter.getBodyDamage().getBodyPart(BodyPartType.FromIndex(Rand.Next(BodyPartType.ToIndex(BodyPartType.Hand_L), BodyPartType.ToIndex(BodyPartType.MAX))));
            float max = Math.max(Rand.Next(n - 15.0f, n), 5.0f);
            if (isoGameCharacter.Traits.FastHealer.isSet()) {
                max *= (float)0.8;
            }
            else if (isoGameCharacter.Traits.SlowHealer.isSet()) {
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
            final float n4 = (float)(max * this.getScript().getPlayerDamageProtection() * 0.9);
            bodyPart.AddDamage(n4);
            if (n4 > 40.0f && Rand.Next(12) == 0) {
                bodyPart.generateDeepWound();
            }
            else if (n4 > 50.0f && Rand.Next(10) == 0 && SandboxOptions.instance.BoneFracture.getValue()) {
                if (bodyPart.getType() == BodyPartType.Neck || bodyPart.getType() == BodyPartType.Groin) {
                    bodyPart.generateDeepWound();
                }
                else {
                    bodyPart.setFractureTime(Rand.Next(Rand.Next(10.0f, n4 + 10.0f), Rand.Next(n4 + 20.0f, n4 + 30.0f)));
                }
            }
            if (n4 > 30.0f && Rand.Next(12 - n3) == 0) {
                final BodyPart setScratchedWindow = isoGameCharacter.getBodyDamage().setScratchedWindow();
                if (Rand.Next(5) == 0) {
                    setScratchedWindow.generateDeepWound();
                    setScratchedWindow.setHaveGlass(true);
                }
            }
        }
    }
    
    public void hitVehicle(final IsoGameCharacter isoGameCharacter, HandWeapon handWeapon) {
        if (handWeapon == null) {
            handWeapon = (HandWeapon)InventoryItemFactory.CreateItem("Base.BareHands");
        }
        float n = (float)handWeapon.getDoorDamage();
        if (isoGameCharacter.isCriticalHit()) {
            n *= 10.0f;
        }
        final VehiclePart nearestBodyworkPart = this.getNearestBodyworkPart(isoGameCharacter);
        if (nearestBodyworkPart != null) {
            VehicleWindow vehicleWindow = nearestBodyworkPart.getWindow();
            for (int i = 0; i < nearestBodyworkPart.getChildCount(); ++i) {
                final VehiclePart child = nearestBodyworkPart.getChild(i);
                if (child.getWindow() != null) {
                    vehicleWindow = child.getWindow();
                    break;
                }
            }
            if (vehicleWindow != null && vehicleWindow.getHealth() > 0) {
                vehicleWindow.damage((int)n);
                this.transmitPartWindow(nearestBodyworkPart);
                if (vehicleWindow.getHealth() == 0) {
                    VehicleManager.sendSoundFromServer(this, (byte)1);
                }
            }
            else {
                nearestBodyworkPart.setCondition(nearestBodyworkPart.getCondition() - (int)n);
                this.transmitPartItem(nearestBodyworkPart);
            }
            final VehiclePart vehiclePart = nearestBodyworkPart;
            vehiclePart.updateFlags |= 0x800;
            this.updateFlags |= 0x800;
        }
        else {
            final Vector3f vector3f = BaseVehicle.TL_vector3f_pool.get().alloc();
            this.getLocalPos(isoGameCharacter.getX(), isoGameCharacter.getY(), isoGameCharacter.getZ(), vector3f);
            final boolean b = vector3f.x > 0.0f;
            BaseVehicle.TL_vector3f_pool.get().release(vector3f);
            if (b) {
                this.addDamageFront((int)n);
            }
            else {
                this.addDamageRear((int)n);
            }
            this.updateFlags |= 0x800;
        }
    }
    
    public boolean isTrunkLocked() {
        VehiclePart vehiclePart = this.getPartById("TrunkDoor");
        if (vehiclePart == null) {
            vehiclePart = this.getPartById("DoorRear");
        }
        return vehiclePart != null && vehiclePart.getDoor() != null && vehiclePart.getInventoryItem() != null && vehiclePart.getDoor().isLocked();
    }
    
    public void setTrunkLocked(final boolean locked) {
        VehiclePart vehiclePart = this.getPartById("TrunkDoor");
        if (vehiclePart == null) {
            vehiclePart = this.getPartById("DoorRear");
        }
        if (vehiclePart != null && vehiclePart.getDoor() != null && vehiclePart.getInventoryItem() != null) {
            vehiclePart.getDoor().setLocked(locked);
            if (GameServer.bServer) {
                this.transmitPartDoor(vehiclePart);
            }
        }
    }
    
    public VehiclePart getNearestBodyworkPart(final IsoGameCharacter isoGameCharacter) {
        for (int i = 0; i < this.getPartCount(); ++i) {
            final VehiclePart partByIndex = this.getPartByIndex(i);
            if (("door".equals(partByIndex.getCategory()) || "bodywork".equals(partByIndex.getCategory())) && this.isInArea(partByIndex.getArea(), isoGameCharacter) && partByIndex.getCondition() > 0) {
                return partByIndex;
            }
        }
        return null;
    }
    
    public double getSirenStartTime() {
        return this.sirenStartTime;
    }
    
    public void setSirenStartTime(final double sirenStartTime) {
        this.sirenStartTime = sirenStartTime;
    }
    
    public boolean sirenShutoffTimeExpired() {
        final double value = SandboxOptions.instance.SirenShutoffHours.getValue();
        if (value <= 0.0) {
            return false;
        }
        final double worldAgeHours = GameTime.instance.getWorldAgeHours();
        if (this.sirenStartTime > worldAgeHours) {
            this.sirenStartTime = worldAgeHours;
        }
        return this.sirenStartTime + value < worldAgeHours;
    }
    
    public void repair() {
        for (int i = 0; i < this.getPartCount(); ++i) {
            this.getPartByIndex(i).repair();
        }
        this.rust = 0.0f;
        this.transmitRust();
        this.bloodIntensity.clear();
        this.transmitBlood();
        this.doBloodOverlay();
    }
    
    public boolean isAnyListenerInside() {
        for (int i = 0; i < this.getMaxPassengers(); ++i) {
            final IsoGameCharacter character = this.getCharacter(i);
            if (character instanceof IsoPlayer && ((IsoPlayer)character).isLocalPlayer() && !character.Traits.Deaf.isSet()) {
                return true;
            }
        }
        return false;
    }
    
    public boolean couldCrawlerAttackPassenger(final IsoGameCharacter isoGameCharacter) {
        return this.getSeat(isoGameCharacter) == -1 && false;
    }
    
    public boolean isGoodCar() {
        return this.isGoodCar;
    }
    
    public void setGoodCar(final boolean isGoodCar) {
        this.isGoodCar = isGoodCar;
    }
    
    public InventoryItem getCurrentKey() {
        return this.currentKey;
    }
    
    public void setCurrentKey(final InventoryItem currentKey) {
        this.currentKey = currentKey;
    }
    
    public boolean isInForest() {
        return this.getSquare() != null && this.getSquare().getZone() != null && ("Forest".equals(this.getSquare().getZone().getType()) || "DeepForest".equals(this.getSquare().getZone().getType()) || "FarmLand".equals(this.getSquare().getZone().getType()));
    }
    
    public float getOffroadEfficiency() {
        if (this.isInForest()) {
            return this.script.getOffroadEfficiency() * 1.5f;
        }
        return this.script.getOffroadEfficiency() * 2.0f;
    }
    
    public void doChrHitImpulse(final IsoObject isoObject) {
        final float b = 22.0f;
        final Vector3f linearVelocity = this.getLinearVelocity(BaseVehicle.TL_vector3f_pool.get().alloc());
        linearVelocity.y = 0.0f;
        final Vector3f vector3f = BaseVehicle.TL_vector3f_pool.get().alloc();
        vector3f.set(this.x - isoObject.getX(), 0.0f, this.z - isoObject.getY());
        vector3f.normalize();
        linearVelocity.mul((Vector3fc)vector3f);
        BaseVehicle.TL_vector3f_pool.get().release(vector3f);
        final float min = Math.min(linearVelocity.length(), b);
        if (min < 0.05f) {
            BaseVehicle.TL_vector3f_pool.get().release(linearVelocity);
            return;
        }
        if (GameServer.bServer) {
            if (isoObject instanceof IsoZombie) {
                ((IsoZombie)isoObject).setThumpFlag(1);
            }
        }
        else {
            SoundManager.instance.PlayWorldSound("ZombieThumpGeneric", isoObject.square, 0.0f, 20.0f, 0.9f, true);
        }
        final Vector3f vector3f2 = BaseVehicle.TL_vector3f_pool.get().alloc();
        vector3f2.set(this.x - isoObject.getX(), 0.0f, this.y - isoObject.getY());
        vector3f2.normalize();
        linearVelocity.normalize();
        final float dot = linearVelocity.dot((Vector3fc)vector3f2);
        BaseVehicle.TL_vector3f_pool.get().release(linearVelocity);
        BaseVehicle.TL_vector3f_pool.get().release(vector3f2);
        this.ApplyImpulse(isoObject, this.getFudgedMass() * 3.0f * min / b * Math.abs(dot));
    }
    
    public boolean isDoColor() {
        return this.doColor;
    }
    
    public void setDoColor(final boolean doColor) {
        this.doColor = doColor;
    }
    
    public float getBrakeSpeedBetweenUpdate() {
        return this.brakeBetweenUpdatesSpeed;
    }
    
    @Override
    public IsoGridSquare getSquare() {
        return this.getCell().getGridSquare(this.x, this.y, this.z);
    }
    
    public void setColor(final float colorValue, final float colorSaturation, final float colorHue) {
        this.colorValue = colorValue;
        this.colorSaturation = colorSaturation;
        this.colorHue = colorHue;
    }
    
    public void setColorHSV(final float colorHue, final float colorSaturation, final float colorValue) {
        this.colorHue = colorHue;
        this.colorSaturation = colorSaturation;
        this.colorValue = colorValue;
    }
    
    public float getColorHue() {
        return this.colorHue;
    }
    
    public float getColorSaturation() {
        return this.colorSaturation;
    }
    
    public float getColorValue() {
        return this.colorValue;
    }
    
    public boolean isRemovedFromWorld() {
        return this.removedFromWorld;
    }
    
    public float getInsideTemperature() {
        final VehiclePart partById = this.getPartById("PassengerCompartment");
        float n = 0.0f;
        if (partById != null && partById.getModData() != null) {
            if (partById.getModData().rawget((Object)"temperature") != null) {
                n += ((Double)partById.getModData().rawget((Object)"temperature")).floatValue();
            }
            if (partById.getModData().rawget((Object)"windowtemperature") != null) {
                n += ((Double)partById.getModData().rawget((Object)"windowtemperature")).floatValue();
            }
        }
        return n;
    }
    
    public AnimationPlayer getAnimationPlayer() {
        final Model loadedModel = ModelManager.instance.getLoadedModel(this.getScript().getModel().file);
        if (loadedModel == null || loadedModel.bStatic) {
            return null;
        }
        if (this.m_animPlayer != null && this.m_animPlayer.getModel() != loadedModel) {
            this.m_animPlayer = Pool.tryRelease(this.m_animPlayer);
        }
        if (this.m_animPlayer == null) {
            this.m_animPlayer = AnimationPlayer.alloc(loadedModel);
        }
        return this.m_animPlayer;
    }
    
    public void releaseAnimationPlayers() {
        this.m_animPlayer = Pool.tryRelease(this.m_animPlayer);
        PZArrayUtil.forEach(this.models, ModelInfo::releaseAnimationPlayer);
    }
    
    public void setAddThumpWorldSound(final boolean bAddThumpWorldSound) {
        this.bAddThumpWorldSound = bAddThumpWorldSound;
    }
    
    @Override
    public void Thump(final IsoMovingObject isoMovingObject) {
        final VehiclePart partById = this.getPartById("lightbar");
        if (partById == null) {
            return;
        }
        if (partById.getCondition() <= 0) {
            isoMovingObject.setThumpTarget(null);
        }
        final VehiclePart useablePart = this.getUseablePart((IsoGameCharacter)isoMovingObject);
        if (useablePart != null) {
            useablePart.setCondition(useablePart.getCondition() - Rand.Next(1, 5));
        }
        partById.setCondition(partById.getCondition() - Rand.Next(1, 5));
    }
    
    @Override
    public void WeaponHit(final IsoGameCharacter isoGameCharacter, final HandWeapon handWeapon) {
    }
    
    @Override
    public Thumpable getThumpableFor(final IsoGameCharacter isoGameCharacter) {
        return null;
    }
    
    @Override
    public float getThumpCondition() {
        return 1.0f;
    }
    
    public boolean isRegulator() {
        return this.regulator;
    }
    
    public void setRegulator(final boolean regulator) {
        this.regulator = regulator;
    }
    
    public float getRegulatorSpeed() {
        return this.regulatorSpeed;
    }
    
    public void setRegulatorSpeed(final float regulatorSpeed) {
        this.regulatorSpeed = regulatorSpeed;
    }
    
    public void setVehicleTowing(final BaseVehicle vehicleTowing, final String towAttachmentSelf, final String towAttachmentOther, final float towConstraintZOffset) {
        this.vehicleTowing = vehicleTowing;
        this.vehicleTowingID = ((this.vehicleTowing == null) ? -1 : this.vehicleTowing.getSqlId());
        this.towAttachmentSelf = towAttachmentSelf;
        this.towAttachmentOther = towAttachmentOther;
        this.towConstraintZOffset = towConstraintZOffset;
    }
    
    public void setVehicleTowedBy(final BaseVehicle vehicleTowedBy, final String towAttachmentOther, final String towAttachmentSelf, final float towConstraintZOffset) {
        this.vehicleTowedBy = vehicleTowedBy;
        this.vehicleTowedByID = ((this.vehicleTowedBy == null) ? -1 : this.vehicleTowedBy.getSqlId());
        this.towAttachmentSelf = towAttachmentSelf;
        this.towAttachmentOther = towAttachmentOther;
        this.towConstraintZOffset = towConstraintZOffset;
    }
    
    public BaseVehicle getVehicleTowing() {
        return this.vehicleTowing;
    }
    
    public BaseVehicle getVehicleTowedBy() {
        return this.vehicleTowedBy;
    }
    
    public boolean attachmentExist(final String s) {
        final VehicleScript script = this.getScript();
        return script != null && script.getAttachmentById(s) != null;
    }
    
    public Vector3f getAttachmentLocalPos(final String s, final Vector3f vector3f) {
        final VehicleScript script = this.getScript();
        if (script == null) {
            return null;
        }
        final ModelAttachment attachmentById = script.getAttachmentById(s);
        if (attachmentById == null) {
            return null;
        }
        vector3f.set((Vector3fc)attachmentById.getOffset());
        if (script.getModel() == null) {
            return vector3f;
        }
        return vector3f.add((Vector3fc)script.getModel().getOffset());
    }
    
    public Vector3f getAttachmentWorldPos(final String s, Vector3f attachmentLocalPos) {
        attachmentLocalPos = this.getAttachmentLocalPos(s, attachmentLocalPos);
        return (attachmentLocalPos == null) ? null : this.getWorldPos(attachmentLocalPos, attachmentLocalPos);
    }
    
    public Vector3f getTowingLocalPos(final String s, final Vector3f vector3f) {
        return this.getAttachmentLocalPos(s, vector3f);
    }
    
    public Vector3f getTowedByLocalPos(final String s, final Vector3f vector3f) {
        return this.getAttachmentLocalPos(s, vector3f);
    }
    
    public Vector3f getTowingWorldPos(final String s, Vector3f towingLocalPos) {
        towingLocalPos = this.getTowingLocalPos(s, towingLocalPos);
        return (towingLocalPos == null) ? null : this.getWorldPos(towingLocalPos, towingLocalPos);
    }
    
    public Vector3f getTowedByWorldPos(final String s, Vector3f towedByLocalPos) {
        towedByLocalPos = this.getTowedByLocalPos(s, towedByLocalPos);
        return (towedByLocalPos == null) ? null : this.getWorldPos(towedByLocalPos, towedByLocalPos);
    }
    
    public Vector3f getPlayerTrailerLocalPos(final String s, final boolean b, final Vector3f vector3f) {
        final ModelAttachment attachmentById = this.getScript().getAttachmentById(s);
        if (attachmentById == null) {
            return null;
        }
        final Vector3f extents = this.getScript().getExtents();
        final Vector3f centerOfMassOffset = this.getScript().getCenterOfMassOffset();
        float n = centerOfMassOffset.x + extents.x / 2.0f + 0.3f + 0.05f;
        if (!b) {
            n *= -1.0f;
        }
        if (attachmentById.getOffset().z > 0.0f) {
            return vector3f.set(n, 0.0f, centerOfMassOffset.z + extents.z / 2.0f + 0.3f + 0.05f);
        }
        return vector3f.set(n, 0.0f, centerOfMassOffset.z - (extents.z / 2.0f + 0.3f + 0.05f));
    }
    
    public Vector3f getPlayerTrailerWorldPos(final String s, final boolean b, Vector3f playerTrailerLocalPos) {
        playerTrailerLocalPos = this.getPlayerTrailerLocalPos(s, b, playerTrailerLocalPos);
        if (playerTrailerLocalPos == null) {
            return null;
        }
        this.getWorldPos(playerTrailerLocalPos, playerTrailerLocalPos);
        playerTrailerLocalPos.z = (float)(int)this.z;
        final Vector3f towingWorldPos = this.getTowingWorldPos(s, BaseVehicle.TL_vector3f_pool.get().alloc());
        final boolean lineClearCollide = PolygonalMap2.instance.lineClearCollide(playerTrailerLocalPos.x, playerTrailerLocalPos.y, towingWorldPos.x, towingWorldPos.y, (int)this.z, this, false, false);
        BaseVehicle.TL_vector3f_pool.get().release(towingWorldPos);
        if (lineClearCollide) {
            return null;
        }
        return playerTrailerLocalPos;
    }
    
    public void addHingeConstraint(final BaseVehicle baseVehicle, final String s, final String s2) {
        this.breakConstraint(true, false);
        baseVehicle.breakConstraint(true, false);
        final Vector3fObjectPool vector3fObjectPool = BaseVehicle.TL_vector3f_pool.get();
        final Vector3f towingLocalPos = this.getTowingLocalPos(s, vector3fObjectPool.alloc());
        final Vector3f towedByLocalPos = baseVehicle.getTowedByLocalPos(s2, vector3fObjectPool.alloc());
        if (towingLocalPos == null || towedByLocalPos == null) {
            if (towingLocalPos != null) {
                vector3fObjectPool.release(towingLocalPos);
            }
            if (towedByLocalPos != null) {
                vector3fObjectPool.release(towedByLocalPos);
            }
            return;
        }
        this.constraintTowing = Bullet.addHingeConstraint(this.VehicleID, baseVehicle.VehicleID, towingLocalPos.x, towingLocalPos.y, towingLocalPos.z, towedByLocalPos.x, towedByLocalPos.y, towedByLocalPos.z);
        baseVehicle.constraintTowing = this.constraintTowing;
        this.setVehicleTowing(baseVehicle, s, s2, 0.0f);
        baseVehicle.setVehicleTowedBy(this, s, s2, 0.0f);
        vector3fObjectPool.release(towingLocalPos);
        vector3fObjectPool.release(towedByLocalPos);
        this.constraintChanged();
        baseVehicle.constraintChanged();
    }
    
    private void drawTowingRope() {
        final BaseVehicle vehicleTowing = this.getVehicleTowing();
        if (vehicleTowing == null) {
            return;
        }
        final Vector3fObjectPool vector3fObjectPool = BaseVehicle.TL_vector3f_pool.get();
        this.getAttachmentWorldPos("trailer", vector3fObjectPool.alloc());
        final Vector3f attachmentWorldPos = this.getAttachmentWorldPos("trailerfront", vector3fObjectPool.alloc());
        final ModelAttachment attachmentById = this.script.getAttachmentById("trailerfront");
        if (attachmentById != null) {
            attachmentWorldPos.set((Vector3fc)attachmentById.getOffset());
        }
        final Vector2 vector2 = new Vector2();
        vector2.x = vehicleTowing.x;
        vector2.y = vehicleTowing.y;
        final Vector2 vector3 = vector2;
        vector3.x -= this.x;
        final Vector2 vector4 = vector2;
        vector4.y -= this.y;
        vector2.setLength(2.0f);
        this.drawDirectionLine(vector2, vector2.getLength(), 1.0f, 0.5f, 0.5f);
    }
    
    public void drawDirectionLine(final Vector2 vector2, final float n, final float n2, final float n3, final float n4) {
        final float n5 = this.x + vector2.x * n;
        final float n6 = this.y + vector2.y * n;
        LineDrawer.drawLine(IsoUtils.XToScreenExact(this.x, this.y, this.z, 0), IsoUtils.YToScreenExact(this.x, this.y, this.z, 0), IsoUtils.XToScreenExact(n5, n6, this.z, 0), IsoUtils.YToScreenExact(n5, n6, this.z, 0), n2, n3, n4, 0.5f, 1);
    }
    
    public void updateConstraint(final BaseVehicle baseVehicle) {
        if (this.getScriptName().contains("Trailer") || baseVehicle.getScriptName().contains("Trailer")) {
            return;
        }
        final ModelAttachment attachmentById = this.script.getAttachmentById(this.towAttachmentSelf);
        if (attachmentById == null || !attachmentById.isUpdateConstraint()) {
            return;
        }
        final Vector3f forwardVector = this.getForwardVector(BaseVehicle.TL_vector3f_pool.get().alloc());
        final Vector3f linearVelocity = this.getLinearVelocity(BaseVehicle.TL_vector3f_pool.get().alloc());
        float dot = linearVelocity.dot((Vector3fc)forwardVector);
        if (linearVelocity.lengthSquared() < 0.25f) {
            dot = 0.0f;
        }
        final boolean b = dot > 0.0f;
        final boolean b2 = dot < 0.0f;
        float f = this.towConstraintZOffset;
        final float n = GameTime.getInstance().getMultiplier() / 0.8f;
        if (attachmentById.getZOffset() > 0.0f) {
            if (((b && this.isBraking) || this.getController().EngineForce < 0.0f) && f < 1.0f) {
                f = Math.min(1.0f, f + 0.015f * n);
            }
            else if (((b2 && this.isBraking) || this.getController().EngineForce > 0.0f) && f > 0.1f) {
                f = Math.max(0.1f, f - 0.01f * n);
            }
        }
        else if (attachmentById.getZOffset() < 0.0f) {
            if (((b && this.isBraking) || this.getController().EngineForce < 0.0f) && f < 0.1f) {
                f = Math.min(0.1f, f + 0.015f * n);
            }
            else if (((b2 && this.isBraking) || this.getController().EngineForce > 0.0f) && f > -1.0f) {
                f = Math.max(-1.0f, f - 0.01f * n);
            }
        }
        if (f != this.towConstraintZOffset) {
            this.addPointConstraint(baseVehicle, this.towAttachmentSelf, baseVehicle.towAttachmentSelf, f, true);
        }
        BaseVehicle.TL_vector3f_pool.get().release(forwardVector);
        BaseVehicle.TL_vector3f_pool.get().release(linearVelocity);
    }
    
    public void addPointConstraint(final BaseVehicle baseVehicle, final String s, final String s2) {
        this.addPointConstraint(baseVehicle, s, s2, null, false);
    }
    
    public void addPointConstraint(final BaseVehicle baseVehicle, final String s, final String s2, final Float n, final Boolean b) {
        this.breakConstraint(true, true);
        baseVehicle.breakConstraint(true, true);
        final Vector3fObjectPool vector3fObjectPool = BaseVehicle.TL_vector3f_pool.get();
        final Vector3f towingLocalPos = this.getTowingLocalPos(s, vector3fObjectPool.alloc());
        final Vector3f towedByLocalPos = baseVehicle.getTowedByLocalPos(s2, vector3fObjectPool.alloc());
        if (towingLocalPos == null || towedByLocalPos == null) {
            if (towingLocalPos != null) {
                vector3fObjectPool.release(towingLocalPos);
            }
            if (towedByLocalPos != null) {
                vector3fObjectPool.release(towedByLocalPos);
            }
            return;
        }
        final ModelAttachment attachmentById = this.script.getAttachmentById(s);
        float n2 = 0.0f;
        final float n3 = 0.0f;
        if (attachmentById != null && attachmentById.getZOffset() != 0.0f) {
            n2 = attachmentById.getZOffset();
        }
        if (n != null) {
            n2 = n;
        }
        if (this.getScriptName().contains("Trailer") || baseVehicle.getScriptName().contains("Trailer")) {
            n2 = 0.0f;
        }
        this.constraintTowing = Bullet.addPointConstraint(this.VehicleID, baseVehicle.VehicleID, towingLocalPos.x, towingLocalPos.y, towingLocalPos.z + n2, towedByLocalPos.x, towedByLocalPos.y, towedByLocalPos.z + n3);
        baseVehicle.constraintTowing = this.constraintTowing;
        this.setVehicleTowing(baseVehicle, s, s2, n2);
        baseVehicle.setVehicleTowedBy(this, s, s2, n3);
        vector3fObjectPool.release(towingLocalPos);
        vector3fObjectPool.release(towedByLocalPos);
        this.constraintChanged();
        baseVehicle.constraintChanged();
        if (GameClient.bClient && !b) {
            VehicleManager.instance.sendTowing(this, baseVehicle, s, s2, n);
        }
    }
    
    public void constraintChanged() {
        final long currentTimeMillis = System.currentTimeMillis();
        this.setPhysicsActive(true);
        this.constraintChangedTime = currentTimeMillis;
    }
    
    public void breakConstraint(final boolean b, final boolean b2) {
        if (this.constraintTowing == -1) {
            return;
        }
        Bullet.removeConstraint(this.constraintTowing);
        this.constraintTowing = -1;
        this.constraintChanged();
        if (GameClient.bClient && !b2) {
            VehicleManager.instance.sendDetachTowing(this.vehicleTowing, this.vehicleTowedBy);
        }
        if (this.vehicleTowing != null) {
            this.vehicleTowing.constraintChanged();
            this.vehicleTowing.vehicleTowedBy = null;
            this.vehicleTowing.constraintTowing = -1;
            if (b) {
                this.vehicleTowingID = -1;
                this.vehicleTowing.vehicleTowedByID = -1;
            }
            this.vehicleTowing = null;
        }
        if (this.vehicleTowedBy != null) {
            this.vehicleTowedBy.constraintChanged();
            this.vehicleTowedBy.vehicleTowing = null;
            this.vehicleTowedBy.constraintTowing = -1;
            if (b) {
                this.vehicleTowedBy.vehicleTowingID = -1;
                this.vehicleTowedByID = -1;
            }
            this.vehicleTowedBy = null;
        }
    }
    
    public boolean canAttachTrailer(final BaseVehicle baseVehicle, final String o, final String o2) {
        if (this == baseVehicle || this.physics == null || this.constraintTowing != -1) {
            return false;
        }
        if (baseVehicle == null || baseVehicle.physics == null || baseVehicle.constraintTowing != -1) {
            return false;
        }
        if (!GameClient.bClient) {
            if (Math.abs(GameServer.bServer ? this.netLinearVelocity.y : this.jniLinearVelocity.y) > 0.2f) {
                return false;
            }
            if (Math.abs(GameServer.bServer ? baseVehicle.netLinearVelocity.y : baseVehicle.jniLinearVelocity.y) > 0.2f) {
                return false;
            }
        }
        final long currentTimeMillis = System.currentTimeMillis();
        if (this.createPhysicsTime + 1000L > currentTimeMillis) {
            return false;
        }
        if (baseVehicle.createPhysicsTime + 1000L > currentTimeMillis) {
            return false;
        }
        final Vector3fObjectPool vector3fObjectPool = BaseVehicle.TL_vector3f_pool.get();
        final Vector3f towingWorldPos = this.getTowingWorldPos(o, vector3fObjectPool.alloc());
        final Vector3f towedByWorldPos = baseVehicle.getTowedByWorldPos(o2, vector3fObjectPool.alloc());
        if (towingWorldPos == null || towedByWorldPos == null) {
            return false;
        }
        final float distanceToSquared = IsoUtils.DistanceToSquared(towingWorldPos.x, towingWorldPos.y, towingWorldPos.z, towedByWorldPos.x, towedByWorldPos.y, towedByWorldPos.z);
        vector3fObjectPool.release(towingWorldPos);
        vector3fObjectPool.release(towedByWorldPos);
        final ModelAttachment attachmentById = this.script.getAttachmentById(o);
        final ModelAttachment attachmentById2 = baseVehicle.script.getAttachmentById(o2);
        return (attachmentById == null || attachmentById.getCanAttach() == null || attachmentById.getCanAttach().contains(o2)) && (attachmentById2 == null || attachmentById2.getCanAttach() == null || attachmentById2.getCanAttach().contains(o)) && distanceToSquared < 2.0f;
    }
    
    private void tryReconnectToTowedVehicle() {
        if (GameClient.bClient) {
            return;
        }
        if (this.vehicleTowing != null) {
            return;
        }
        if (this.vehicleTowingID == -1) {
            return;
        }
        BaseVehicle baseVehicle = null;
        final ArrayList<BaseVehicle> vehicles = IsoWorld.instance.CurrentCell.getVehicles();
        for (int i = 0; i < vehicles.size(); ++i) {
            final BaseVehicle baseVehicle2 = vehicles.get(i);
            if (baseVehicle2.getSqlId() == this.vehicleTowingID) {
                baseVehicle = baseVehicle2;
                break;
            }
        }
        if (baseVehicle == null) {
            return;
        }
        if (!this.canAttachTrailer(baseVehicle, this.towAttachmentSelf, this.towAttachmentOther)) {
            return;
        }
        this.addPointConstraint(baseVehicle, this.towAttachmentSelf, this.towAttachmentOther, this.towConstraintZOffset, false);
    }
    
    public void positionTrailer(final BaseVehicle baseVehicle) {
        if (baseVehicle == null) {
            return;
        }
        final Vector3fObjectPool vector3fObjectPool = BaseVehicle.TL_vector3f_pool.get();
        final Vector3f towingWorldPos = this.getTowingWorldPos("trailer", vector3fObjectPool.alloc());
        final Vector3f towedByWorldPos = baseVehicle.getTowedByWorldPos("trailer", vector3fObjectPool.alloc());
        if (towingWorldPos == null || towedByWorldPos == null) {
            return;
        }
        towedByWorldPos.sub(baseVehicle.x, baseVehicle.y, baseVehicle.z);
        towingWorldPos.sub((Vector3fc)towedByWorldPos);
        final Transform worldTransform = baseVehicle.getWorldTransform(this.tempTransform);
        worldTransform.origin.set(towingWorldPos.x - WorldSimulation.instance.offsetX, baseVehicle.jniTransform.origin.y, towingWorldPos.y - WorldSimulation.instance.offsetY);
        baseVehicle.setWorldTransform(worldTransform);
        baseVehicle.setX(towingWorldPos.x);
        baseVehicle.setLx(towingWorldPos.x);
        baseVehicle.setY(towingWorldPos.y);
        baseVehicle.setLy(towingWorldPos.y);
        baseVehicle.setCurrent(this.getCell().getGridSquare(towingWorldPos.x, towingWorldPos.y, 0.0));
        this.addPointConstraint(baseVehicle, "trailer", "trailer");
        vector3fObjectPool.release(towingWorldPos);
        vector3fObjectPool.release(towedByWorldPos);
    }
    
    public String getTowAttachmentSelf() {
        return this.towAttachmentSelf;
    }
    
    public String getTowAttachmentOther() {
        return this.towAttachmentOther;
    }
    
    public VehicleEngineRPM getVehicleEngineRPM() {
        if (this.vehicleEngineRPM == null) {
            this.vehicleEngineRPM = ScriptManager.instance.getVehicleEngineRPM(this.getScript().getEngineRPMType());
            if (this.vehicleEngineRPM == null) {
                DebugLog.General.warn("unknown vehicleEngineRPM \"%s\"", this.getScript().getEngineRPMType());
                this.vehicleEngineRPM = new VehicleEngineRPM();
            }
        }
        return this.vehicleEngineRPM;
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
    
    private void stopEngineSounds() {
        if (this.emitter == null) {
            return;
        }
        for (int i = 0; i < this.new_EngineSoundId.length; ++i) {
            if (this.new_EngineSoundId[i] != 0L) {
                this.getEmitter().stopSound(this.new_EngineSoundId[i]);
                this.new_EngineSoundId[i] = 0L;
            }
        }
        if (this.combinedEngineSound != 0L) {
            if (this.getEmitter().hasSustainPoints(this.combinedEngineSound)) {
                this.getEmitter().triggerCue(this.combinedEngineSound);
            }
            else {
                this.getEmitter().stopSound(this.combinedEngineSound);
            }
            this.combinedEngineSound = 0L;
        }
    }
    
    public void debugSetStatic(final boolean b) {
        Bullet.setVehicleStatic(this.getId(), b);
    }
    
    public BaseVehicle setSmashed(final String s) {
        return this.setSmashed(s, false);
    }
    
    public BaseVehicle setSmashed(final String s, final boolean b) {
        String rawgetStr = null;
        Integer n = null;
        final KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)LuaManager.env.rawget((Object)"SmashedCarDefinitions");
        if (kahluaTableImpl != null) {
            final KahluaTableImpl kahluaTableImpl2 = (KahluaTableImpl)kahluaTableImpl.rawget((Object)"cars");
            if (kahluaTableImpl2 != null) {
                final KahluaTableImpl kahluaTableImpl3 = (KahluaTableImpl)kahluaTableImpl2.rawget((Object)this.getScriptName());
                if (kahluaTableImpl3 != null) {
                    rawgetStr = kahluaTableImpl3.rawgetStr((Object)s.toLowerCase());
                    n = kahluaTableImpl3.rawgetInt((Object)"skin");
                    if (n == -1) {
                        n = this.getSkinIndex();
                    }
                }
            }
        }
        if (rawgetStr != null) {
            this.removeFromWorld();
            this.permanentlyRemove();
            final BaseVehicle e = new BaseVehicle(IsoWorld.instance.CurrentCell);
            e.setScriptName(rawgetStr);
            e.setScript();
            e.setSkinIndex(n);
            e.setX(this.x);
            e.setY(this.y);
            e.setZ(this.z);
            e.setDir(this.getDir());
            e.savedRot.set((Quaternionfc)this.savedRot);
            if (b) {
                e.savedRot.rotationXYZ(0.0f, this.getAngleY() * 0.017453292f, 3.1415927f);
            }
            e.jniTransform.setRotation(e.savedRot);
            if (IsoChunk.doSpawnedVehiclesInInvalidPosition(e)) {
                e.setSquare(this.square);
                e.square.chunk.vehicles.add(e);
                e.chunk = e.square.chunk;
                e.addToWorld();
                VehiclesDB2.instance.addVehicle(e);
            }
            e.setGeneralPartCondition(0.5f, 60.0f);
            final VehiclePart partById = e.getPartById("Engine");
            if (partById != null) {
                partById.setCondition(0);
            }
            e.engineQuality = 0;
            return e;
        }
        return this;
    }
    
    public boolean isCollided(final IsoGameCharacter isoGameCharacter) {
        if (GameClient.bClient && this.getDriver() != null && !this.getDriver().isLocal()) {
            return true;
        }
        final Vector2 testCollisionWithCharacter = this.testCollisionWithCharacter(isoGameCharacter, 0.20000002f, this.hitVars.collision);
        return testCollisionWithCharacter != null && testCollisionWithCharacter.x != -1.0f;
    }
    
    public HitVars checkCollision(final IsoGameCharacter isoGameCharacter) {
        if (!isoGameCharacter.isProne()) {
            this.hitVars.calc(isoGameCharacter, this);
            this.hitCharacter(isoGameCharacter, this.hitVars);
            return this.hitVars;
        }
        if (this.testCollisionWithProneCharacter(isoGameCharacter, true) > 0) {
            this.hitVars.calc(isoGameCharacter, this);
            this.hitCharacter(isoGameCharacter, this.hitVars);
            return this.hitVars;
        }
        return null;
    }
    
    public boolean updateHitByVehicle(final IsoGameCharacter isoGameCharacter) {
        if (isoGameCharacter.isVehicleCollisionActive(this) && (this.isCollided(isoGameCharacter) || isoGameCharacter.isCollidedWithVehicle()) && this.physics != null) {
            final HitVars checkCollision = this.checkCollision(isoGameCharacter);
            if (checkCollision != null) {
                isoGameCharacter.doHitByVehicle(this, checkCollision);
                return true;
            }
        }
        return false;
    }
    
    public void hitCharacter(final IsoGameCharacter isoGameCharacter, final HitVars hitVars) {
        if (hitVars.dot < 0.0f && !GameServer.bServer) {
            this.ApplyImpulse(isoGameCharacter, hitVars.vehicleImpulse);
        }
        final long currentTimeMillis = System.currentTimeMillis();
        this.zombiesHits = Math.max(this.zombiesHits - (int)((currentTimeMillis - this.zombieHitTimestamp) / 1000L), 0);
        if (currentTimeMillis - this.zombieHitTimestamp > 700L) {
            this.zombieHitTimestamp = currentTimeMillis;
            ++this.zombiesHits;
            this.zombiesHits = Math.min(this.zombiesHits, 20);
        }
        if (isoGameCharacter instanceof IsoPlayer) {
            ((IsoPlayer)isoGameCharacter).setVehicleHitLocation(this);
        }
        else if (isoGameCharacter instanceof IsoZombie) {
            ((IsoZombie)isoGameCharacter).setVehicleHitLocation(this);
        }
        if (hitVars.vehicleSpeed >= 5.0f || this.zombiesHits > 10) {
            hitVars.vehicleSpeed = this.getCurrentSpeedKmHour() / 5.0f;
            final Vector3f vector3f = BaseVehicle.TL_vector3f_pool.get().alloc();
            this.getLocalPos(isoGameCharacter.x, isoGameCharacter.y, isoGameCharacter.z, vector3f);
            if (vector3f.z > 0.0f) {
                final int caclulateDamageWithBodies = this.caclulateDamageWithBodies(true);
                if (!GameClient.bClient) {
                    this.addDamageFrontHitAChr(caclulateDamageWithBodies);
                }
                if (Core.bDebug) {
                    DebugLog.log(DebugType.Damage, invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, this.zombiesHits, caclulateDamageWithBodies));
                }
                hitVars.vehicleDamage = caclulateDamageWithBodies;
                hitVars.isVehicleHitFromFront = true;
            }
            else {
                final int caclulateDamageWithBodies2 = this.caclulateDamageWithBodies(false);
                if (!GameClient.bClient) {
                    this.addDamageRearHitAChr(caclulateDamageWithBodies2);
                }
                if (Core.bDebug) {
                    DebugLog.log(DebugType.Damage, invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, this.zombiesHits, caclulateDamageWithBodies2));
                }
                hitVars.vehicleDamage = caclulateDamageWithBodies2;
                hitVars.isVehicleHitFromFront = false;
            }
            BaseVehicle.TL_vector3f_pool.get().release(vector3f);
        }
    }
    
    static {
        _UNIT_Y = new Vector3f(0.0f, 1.0f, 0.0f);
        tempPoly = new PolygonalMap2.VehiclePoly();
        BaseVehicle.RENDER_TO_TEXTURE = false;
        BaseVehicle.CENTER_OF_MASS_MAGIC = 0.7f;
        wheelParams = new float[24];
        physicsParams = new float[27];
        BaseVehicle.vehicleShadow = null;
        inf = new ColorInfo();
        lowRiderParam = new float[4];
        s_PartToMaskMap = new HashMap<String, Integer>();
        BYTE_ZERO = 0;
        TL_vector2_pool = ThreadLocal.withInitial((Supplier<? extends Vector2ObjectPool>)Vector2ObjectPool::new);
        TL_vector2f_pool = ThreadLocal.withInitial((Supplier<? extends Vector2fObjectPool>)Vector2fObjectPool::new);
        TL_vector3f_pool = ThreadLocal.withInitial((Supplier<? extends Vector3fObjectPool>)Vector3fObjectPool::new);
        TL_matrix4f_pool = ThreadLocal.withInitial((Supplier<? extends Matrix4fObjectPool>)Matrix4fObjectPool::new);
        TL_quaternionf_pool = ThreadLocal.withInitial((Supplier<? extends QuaternionfObjectPool>)QuaternionfObjectPool::new);
        BaseVehicle.PLUS_RADIUS = 0.15f;
    }
    
    public static final class Vector2ObjectPool extends ObjectPool<Vector2>
    {
        int allocated;
        
        Vector2ObjectPool() {
            super(Vector2::new);
            this.allocated = 0;
        }
        
        @Override
        protected Vector2 makeObject() {
            ++this.allocated;
            return super.makeObject();
        }
    }
    
    public static final class Vector2fObjectPool extends ObjectPool<Vector2f>
    {
        int allocated;
        
        Vector2fObjectPool() {
            super(Vector2f::new);
            this.allocated = 0;
        }
        
        @Override
        protected Vector2f makeObject() {
            ++this.allocated;
            return super.makeObject();
        }
    }
    
    public static final class Vector3fObjectPool extends ObjectPool<Vector3f>
    {
        int allocated;
        
        Vector3fObjectPool() {
            super(Vector3f::new);
            this.allocated = 0;
        }
        
        @Override
        protected Vector3f makeObject() {
            ++this.allocated;
            return super.makeObject();
        }
    }
    
    public static final class Matrix4fObjectPool extends ObjectPool<Matrix4f>
    {
        int allocated;
        
        Matrix4fObjectPool() {
            super(Matrix4f::new);
            this.allocated = 0;
        }
        
        @Override
        protected Matrix4f makeObject() {
            ++this.allocated;
            return super.makeObject();
        }
    }
    
    public static final class QuaternionfObjectPool extends ObjectPool<Quaternionf>
    {
        int allocated;
        
        QuaternionfObjectPool() {
            super(Quaternionf::new);
            this.allocated = 0;
        }
        
        @Override
        protected Quaternionf makeObject() {
            ++this.allocated;
            return super.makeObject();
        }
    }
    
    private static final class L_testCollisionWithVehicle
    {
        static final Vector2[] testVecs1;
        static final Vector2[] testVecs2;
        static final Vector3f worldPos;
        
        static {
            testVecs1 = new Vector2[4];
            testVecs2 = new Vector2[4];
            worldPos = new Vector3f();
        }
    }
    
    public enum engineStateTypes
    {
        Idle, 
        Starting, 
        RetryingStarting, 
        StartingSuccess, 
        StartingFailed, 
        Running, 
        Stalling, 
        ShutingDown, 
        StartingFailedNoPower;
        
        public static final engineStateTypes[] Values;
        
        private static /* synthetic */ engineStateTypes[] $values() {
            return new engineStateTypes[] { engineStateTypes.Idle, engineStateTypes.Starting, engineStateTypes.RetryingStarting, engineStateTypes.StartingSuccess, engineStateTypes.StartingFailed, engineStateTypes.Running, engineStateTypes.Stalling, engineStateTypes.ShutingDown, engineStateTypes.StartingFailedNoPower };
        }
        
        static {
            $VALUES = $values();
            Values = values();
        }
    }
    
    private static final class VehicleImpulse
    {
        static final ArrayDeque<VehicleImpulse> pool;
        final Vector3f impulse;
        final Vector3f rel_pos;
        boolean enable;
        
        private VehicleImpulse() {
            this.impulse = new Vector3f();
            this.rel_pos = new Vector3f();
            this.enable = false;
        }
        
        static VehicleImpulse alloc() {
            return VehicleImpulse.pool.isEmpty() ? new VehicleImpulse() : VehicleImpulse.pool.pop();
        }
        
        void release() {
            VehicleImpulse.pool.push(this);
        }
        
        static {
            pool = new ArrayDeque<VehicleImpulse>();
        }
    }
    
    public static final class ModelInfo
    {
        public VehiclePart part;
        public VehicleScript.Model scriptModel;
        public ModelScript modelScript;
        public int wheelIndex;
        public final Matrix4f renderTransform;
        public VehicleSubModelInstance modelInstance;
        public AnimationPlayer m_animPlayer;
        public AnimationTrack m_track;
        
        public ModelInfo() {
            this.renderTransform = new Matrix4f();
        }
        
        public AnimationPlayer getAnimationPlayer() {
            if (this.part != null && this.part.getParent() != null) {
                final ModelInfo modelInfoForPart = this.part.getVehicle().getModelInfoForPart(this.part.getParent());
                if (modelInfoForPart != null) {
                    return modelInfoForPart.getAnimationPlayer();
                }
            }
            final Model loadedModel = ModelManager.instance.getLoadedModel(this.scriptModel.file);
            if (loadedModel == null || loadedModel.bStatic) {
                return null;
            }
            if (this.m_animPlayer != null && this.m_animPlayer.getModel() != loadedModel) {
                this.m_animPlayer = Pool.tryRelease(this.m_animPlayer);
            }
            if (this.m_animPlayer == null) {
                this.m_animPlayer = AnimationPlayer.alloc(loadedModel);
            }
            return this.m_animPlayer;
        }
        
        public void releaseAnimationPlayer() {
            this.m_animPlayer = Pool.tryRelease(this.m_animPlayer);
        }
    }
    
    public static final class WheelInfo
    {
        public float steering;
        public float rotation;
        public float skidInfo;
        public float suspensionLength;
    }
    
    protected static class UpdateFlags
    {
        public static final short Full = 1;
        public static final short PositionOrientation = 2;
        public static final short Engine = 4;
        public static final short Lights = 8;
        public static final short PartModData = 16;
        public static final short PartUsedDelta = 32;
        public static final short PartModels = 64;
        public static final short PartItem = 128;
        public static final short PartWindow = 256;
        public static final short PartDoor = 512;
        public static final short Sounds = 1024;
        public static final short PartCondition = 2048;
        public static final short UpdateCarProperties = 4096;
        public static final short EngineSound = 8192;
        public static final short Authorization = 16384;
        public static final short AllPartFlags = 19440;
    }
    
    public static final class ServerVehicleState
    {
        private static final float delta = 0.01f;
        public float x;
        public float y;
        public float z;
        public Quaternionf orient;
        public short flags;
        public byte netPlayerAuthorization;
        public int netPlayerId;
        
        public ServerVehicleState() {
            this.x = -1.0f;
            this.orient = new Quaternionf();
            this.netPlayerAuthorization = 0;
            this.netPlayerId = 0;
            this.flags = 0;
        }
        
        public void setAuthorization(final BaseVehicle baseVehicle) {
            this.netPlayerAuthorization = baseVehicle.netPlayerAuthorization;
            this.netPlayerId = baseVehicle.netPlayerId;
        }
        
        public boolean shouldSend(final BaseVehicle baseVehicle) {
            if (baseVehicle.getController() == null) {
                return false;
            }
            if (baseVehicle.updateLockTimeout > System.currentTimeMillis()) {
                return false;
            }
            this.flags &= 0x1;
            if (Math.abs(this.x - baseVehicle.x) > 0.01f || Math.abs(this.y - baseVehicle.y) > 0.01f || Math.abs(this.z - baseVehicle.jniTransform.origin.y) > 0.01f || Math.abs(this.orient.x - baseVehicle.savedRot.x) > 0.01f || Math.abs(this.orient.y - baseVehicle.savedRot.y) > 0.01f || Math.abs(this.orient.z - baseVehicle.savedRot.z) > 0.01f || Math.abs(this.orient.w - baseVehicle.savedRot.w) > 0.01f) {
                this.flags |= 0x2;
            }
            if (this.netPlayerAuthorization != baseVehicle.netPlayerAuthorization || this.netPlayerId != baseVehicle.netPlayerId) {
                this.flags |= 0x4000;
            }
            this.flags |= baseVehicle.updateFlags;
            return this.flags != 0;
        }
    }
    
    public static final class MinMaxPosition
    {
        public float minX;
        public float maxX;
        public float minY;
        public float maxY;
    }
    
    public static final class Passenger
    {
        public IsoGameCharacter character;
        final Vector3f offset;
        
        public Passenger() {
            this.offset = new Vector3f();
        }
    }
    
    public static class HitVars
    {
        private static final float speedCap = 10.0f;
        private final Vector3f velocity;
        private final Vector2 collision;
        private float dot;
        protected float vehicleImpulse;
        protected float vehicleSpeed;
        public final Vector3f targetImpulse;
        public boolean isVehicleHitFromFront;
        public boolean isTargetHitFromBehind;
        public int vehicleDamage;
        public float hitSpeed;
        
        public HitVars() {
            this.velocity = new Vector3f();
            this.collision = new Vector2();
            this.targetImpulse = new Vector3f();
        }
        
        public void calc(final IsoGameCharacter isoGameCharacter, final BaseVehicle baseVehicle) {
            baseVehicle.getLinearVelocity(this.velocity);
            this.velocity.y = 0.0f;
            if (isoGameCharacter instanceof IsoZombie) {
                this.vehicleSpeed = Math.min(this.velocity.length(), 10.0f);
                this.hitSpeed = this.vehicleSpeed + baseVehicle.getClientForce() / baseVehicle.getFudgedMass();
            }
            else {
                this.vehicleSpeed = (float)Math.sqrt(this.velocity.x * this.velocity.x + this.velocity.z * this.velocity.z);
                if (isoGameCharacter.isOnFloor()) {
                    this.hitSpeed = Math.max(this.vehicleSpeed * 6.0f, 5.0f);
                }
                else {
                    this.hitSpeed = Math.max(this.vehicleSpeed * 2.0f, 5.0f);
                }
            }
            this.targetImpulse.set(baseVehicle.x - isoGameCharacter.x, 0.0f, baseVehicle.y - isoGameCharacter.y);
            this.targetImpulse.normalize();
            this.velocity.normalize();
            this.dot = this.velocity.dot((Vector3fc)this.targetImpulse);
            this.targetImpulse.normalize();
            this.targetImpulse.mul(3.0f * this.vehicleSpeed / 10.0f);
            this.targetImpulse.set(this.targetImpulse.x, this.targetImpulse.y, this.targetImpulse.z);
            this.vehicleImpulse = baseVehicle.getFudgedMass() * 7.0f * this.vehicleSpeed / 10.0f * Math.abs(this.dot);
            this.isTargetHitFromBehind = "BEHIND".equals(isoGameCharacter.testDotSide(baseVehicle));
        }
    }
}

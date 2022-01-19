// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import zombie.core.opengl.RenderThread;
import zombie.core.SpriteRenderer;
import org.lwjgl.opengl.ARBShaderObjects;
import zombie.core.opengl.ShaderProgram;
import java.util.HashMap;
import java.util.Collection;
import zombie.ai.states.ThumpState;
import zombie.iso.sprite.IsoSpriteGrid;
import zombie.iso.objects.RenderEffectType;
import zombie.inventory.ItemPickerJava;
import zombie.core.logger.ExceptionLogger;
import zombie.spnetwork.SinglePlayerServer;
import zombie.network.PacketTypes;
import zombie.SystemDisabler;
import zombie.audio.FMODParameter;
import zombie.audio.parameters.ParameterCurrentZone;
import fmod.fmod.FMODSoundEmitter;
import zombie.iso.areas.isoregion.IsoRegions;
import zombie.iso.sprite.IsoDirectionFrame;
import zombie.iso.sprite.shapers.WallShaperN;
import zombie.iso.sprite.shapers.WallShaperWhole;
import zombie.iso.sprite.shapers.WallShaperW;
import zombie.iso.objects.IsoWindowFrame;
import zombie.core.opengl.RenderSettings;
import zombie.debug.DebugOptions;
import zombie.iso.sprite.shapers.WallShaper;
import zombie.util.Type;
import zombie.iso.sprite.shapers.FloorShaper;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;
import zombie.IndieGL;
import zombie.core.Color;
import zombie.core.opengl.Shader;
import zombie.util.StringUtils;
import zombie.inventory.InventoryItemFactory;
import zombie.SandboxOptions;
import zombie.util.list.PZArrayList;
import zombie.Lua.LuaEventManager;
import zombie.ui.ObjectTooltip;
import zombie.characters.IsoLivingCharacter;
import zombie.inventory.InventoryItem;
import zombie.core.math.PZMath;
import fmod.fmod.FMODManager;
import zombie.core.properties.PropertyContainer;
import zombie.core.Rand;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.WorldSoundManager;
import zombie.inventory.types.HandWeapon;
import zombie.SoundManager;
import zombie.characters.IsoGameCharacter;
import zombie.util.io.BitHeaderWrite;
import zombie.util.io.BitHeaderRead;
import zombie.network.ServerOptions;
import zombie.network.GameServer;
import zombie.world.WorldDictionary;
import zombie.GameTime;
import zombie.core.utils.Bits;
import zombie.debug.DebugLog;
import zombie.GameWindow;
import zombie.util.io.BitHeader;
import zombie.Lua.LuaManager;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.io.DataInputStream;
import zombie.network.GameClient;
import zombie.vehicles.BaseVehicle;
import zombie.iso.objects.IsoFire;
import zombie.iso.objects.IsoMolotovCocktail;
import zombie.iso.objects.IsoZombieGiblets;
import zombie.iso.objects.IsoLightSwitch;
import zombie.iso.objects.IsoTree;
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.BSFurnace;
import zombie.iso.objects.IsoMannequin;
import zombie.iso.objects.IsoCompost;
import zombie.iso.objects.IsoGenerator;
import zombie.iso.objects.IsoCarBatteryCharger;
import zombie.iso.objects.IsoBrokenGlass;
import zombie.iso.objects.IsoTrap;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoStove;
import zombie.iso.objects.IsoFireplace;
import zombie.iso.objects.IsoClothingWasher;
import zombie.iso.objects.IsoClothingDryer;
import zombie.iso.objects.IsoBarbecue;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoTelevision;
import zombie.iso.objects.IsoRadio;
import zombie.iso.objects.IsoCurtain;
import zombie.iso.objects.IsoJukebox;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.iso.objects.IsoWheelieBin;
import zombie.characters.IsoZombie;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoPlayer;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.core.Core;
import java.util.Map;
import zombie.iso.objects.ObjectRenderEffects;
import se.krka.kahlua.vm.KahluaTable;
import zombie.iso.sprite.IsoSprite;
import zombie.inventory.ItemContainer;
import zombie.iso.sprite.IsoSpriteInstance;
import java.util.ArrayList;
import zombie.audio.BaseSoundEmitter;
import zombie.core.textures.ColorInfo;
import zombie.iso.objects.interfaces.Thumpable;
import java.io.Serializable;

public class IsoObject implements Serializable, Thumpable
{
    public static final byte OBF_Highlighted = 1;
    public static final byte OBF_HighlightRenderOnce = 2;
    public static final byte OBF_Blink = 4;
    public static final int MAX_WALL_SPLATS = 32;
    private static final String PropMoveWithWind = "MoveWithWind";
    public static IsoObject lastRendered;
    public static IsoObject lastRenderedRendered;
    private static final ColorInfo stCol;
    public static float rmod;
    public static float gmod;
    public static float bmod;
    public static boolean LowLightingQualityHack;
    private static int DefaultCondition;
    private static final ColorInfo stCol2;
    private static final ColorInfo colFxMask;
    public byte highlightFlags;
    public int keyId;
    public BaseSoundEmitter emitter;
    public float sheetRopeHealth;
    public boolean sheetRope;
    public boolean bNeverDoneAlpha;
    public boolean bAlphaForced;
    public ArrayList<IsoSpriteInstance> AttachedAnimSprite;
    public ArrayList<IsoWallBloodSplat> wallBloodSplats;
    public ItemContainer container;
    public IsoDirections dir;
    public short Damage;
    public float partialThumpDmg;
    public boolean NoPicking;
    public float offsetX;
    public float offsetY;
    public boolean OutlineOnMouseover;
    public IsoObject rerouteMask;
    public IsoSprite sprite;
    public IsoSprite overlaySprite;
    public ColorInfo overlaySpriteColor;
    public IsoGridSquare square;
    private final float[] alpha;
    private final float[] targetAlpha;
    public IsoObject rerouteCollide;
    public KahluaTable table;
    public String name;
    public float tintr;
    public float tintg;
    public float tintb;
    public String spriteName;
    public float sx;
    public float sy;
    public boolean doNotSync;
    protected ObjectRenderEffects windRenderEffects;
    protected ObjectRenderEffects objectRenderEffects;
    protected IsoObject externalWaterSource;
    protected boolean usesExternalWaterSource;
    ArrayList<IsoObject> Children;
    String tile;
    private boolean specialTooltip;
    private ColorInfo highlightColor;
    private ArrayList<ItemContainer> secondaryContainers;
    private ColorInfo customColor;
    private float renderYOffset;
    protected byte isOutlineHighlight;
    protected byte isOutlineHlAttached;
    protected byte isOutlineHlBlink;
    protected final int[] outlineHighlightCol;
    private float outlineThickness;
    protected boolean bMovedThumpable;
    private static Map<Byte, IsoObjectFactory> byteToObjectMap;
    private static Map<Integer, IsoObjectFactory> hashCodeToObjectMap;
    private static Map<String, IsoObjectFactory> nameToObjectMap;
    private static IsoObjectFactory factoryIsoObject;
    private static IsoObjectFactory factoryVehicle;
    
    public IsoObject(final IsoCell isoCell) {
        this();
    }
    
    public IsoObject() {
        this.keyId = -1;
        this.sheetRopeHealth = 100.0f;
        this.sheetRope = false;
        this.bNeverDoneAlpha = true;
        this.bAlphaForced = false;
        this.container = null;
        this.dir = IsoDirections.N;
        this.Damage = 100;
        this.partialThumpDmg = 0.0f;
        this.NoPicking = false;
        this.offsetX = (float)(32 * Core.TileScale);
        this.offsetY = (float)(96 * Core.TileScale);
        this.OutlineOnMouseover = false;
        this.rerouteMask = null;
        this.sprite = null;
        this.overlaySprite = null;
        this.overlaySpriteColor = null;
        this.alpha = new float[4];
        this.targetAlpha = new float[4];
        this.rerouteCollide = null;
        this.table = null;
        this.name = null;
        this.tintr = 1.0f;
        this.tintg = 1.0f;
        this.tintb = 1.0f;
        this.spriteName = null;
        this.doNotSync = false;
        this.externalWaterSource = null;
        this.usesExternalWaterSource = false;
        this.specialTooltip = false;
        this.highlightColor = new ColorInfo(0.9f, 1.0f, 0.0f, 1.0f);
        this.customColor = null;
        this.renderYOffset = 0.0f;
        this.isOutlineHighlight = 0;
        this.isOutlineHlAttached = 0;
        this.isOutlineHlBlink = 0;
        this.outlineHighlightCol = new int[4];
        this.outlineThickness = 0.15f;
        this.bMovedThumpable = false;
        for (int i = 0; i < 4; ++i) {
            this.setAlphaAndTarget(i, 1.0f);
            this.outlineHighlightCol[i] = -1;
        }
    }
    
    public IsoObject(final IsoCell isoCell, final IsoGridSquare square, final IsoSprite sprite) {
        this();
        this.sprite = sprite;
        this.square = square;
    }
    
    public IsoObject(final IsoCell isoCell, final IsoGridSquare square, final String tile) {
        this();
        this.sprite = IsoSpriteManager.instance.getSprite(tile);
        this.square = square;
        this.tile = tile;
    }
    
    public IsoObject(final IsoGridSquare square, final String s, final String name) {
        this();
        this.sprite = IsoSpriteManager.instance.getSprite(s);
        this.square = square;
        this.tile = s;
        this.spriteName = s;
        this.name = name;
    }
    
    public IsoObject(final IsoGridSquare square, final String s, final String name, final boolean b) {
        this();
        if (b) {
            (this.sprite = IsoSprite.CreateSprite(IsoSpriteManager.instance)).LoadFramesNoDirPageSimple(s);
        }
        else {
            this.sprite = IsoSpriteManager.instance.NamedMap.get(s);
        }
        this.tile = s;
        this.square = square;
        this.name = name;
    }
    
    public IsoObject(final IsoGridSquare square, final String s, final boolean b) {
        this();
        if (b) {
            (this.sprite = IsoSprite.CreateSprite(IsoSpriteManager.instance)).LoadFramesNoDirPageSimple(s);
        }
        else {
            this.sprite = IsoSpriteManager.instance.NamedMap.get(s);
        }
        this.tile = s;
        this.square = square;
    }
    
    public IsoObject(final IsoGridSquare square, final String s) {
        this();
        (this.sprite = IsoSprite.CreateSprite(IsoSpriteManager.instance)).LoadFramesNoDirPageSimple(s);
        this.square = square;
    }
    
    public static IsoObject getNew(final IsoGridSquare square, final String tile, final String name, final boolean b) {
        IsoObject isoObject = null;
        synchronized (CellLoader.isoObjectCache) {
            if (CellLoader.isoObjectCache.isEmpty()) {
                isoObject = new IsoObject(square, tile, name, b);
            }
            else {
                isoObject = CellLoader.isoObjectCache.pop();
                isoObject.reset();
                isoObject.tile = tile;
            }
        }
        if (b) {
            (isoObject.sprite = IsoSprite.CreateSprite(IsoSpriteManager.instance)).LoadFramesNoDirPageSimple(isoObject.tile);
        }
        else {
            isoObject.sprite = IsoSpriteManager.instance.NamedMap.get(isoObject.tile);
        }
        isoObject.square = square;
        isoObject.name = name;
        return isoObject;
    }
    
    public static IsoObject getLastRendered() {
        return IsoObject.lastRendered;
    }
    
    public static void setLastRendered(final IsoObject lastRendered) {
        IsoObject.lastRendered = lastRendered;
    }
    
    public static IsoObject getLastRenderedRendered() {
        return IsoObject.lastRenderedRendered;
    }
    
    public static void setLastRenderedRendered(final IsoObject lastRenderedRendered) {
        IsoObject.lastRenderedRendered = lastRenderedRendered;
    }
    
    public static void setDefaultCondition(final int defaultCondition) {
        IsoObject.DefaultCondition = defaultCondition;
    }
    
    public static IsoObject getNew() {
        synchronized (CellLoader.isoObjectCache) {
            if (CellLoader.isoObjectCache.isEmpty()) {
                return new IsoObject();
            }
            return CellLoader.isoObjectCache.pop();
        }
    }
    
    private static IsoObjectFactory addIsoObjectFactory(final IsoObjectFactory isoObjectFactory) {
        if (IsoObject.byteToObjectMap.containsKey(isoObjectFactory.classID)) {
            throw new RuntimeException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, isoObjectFactory.objectName));
        }
        IsoObject.byteToObjectMap.put(isoObjectFactory.classID, isoObjectFactory);
        if (IsoObject.hashCodeToObjectMap.containsKey(isoObjectFactory.hashCode)) {
            throw new RuntimeException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, isoObjectFactory.objectName));
        }
        IsoObject.hashCodeToObjectMap.put(isoObjectFactory.hashCode, isoObjectFactory);
        if (IsoObject.nameToObjectMap.containsKey(isoObjectFactory.objectName)) {
            throw new RuntimeException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, isoObjectFactory.objectName));
        }
        IsoObject.nameToObjectMap.put(isoObjectFactory.objectName, isoObjectFactory);
        return isoObjectFactory;
    }
    
    public static IsoObjectFactory getFactoryVehicle() {
        return IsoObject.factoryVehicle;
    }
    
    private static void initFactory() {
        IsoObject.factoryIsoObject = addIsoObjectFactory(new IsoObjectFactory(0, "IsoObject") {
            @Override
            protected IsoObject InstantiateObject(final IsoCell isoCell) {
                final IsoObject new1 = IsoObject.getNew();
                new1.sx = 0.0f;
                return new1;
            }
        });
        addIsoObjectFactory(new IsoObjectFactory(1, "Player") {
            @Override
            protected IsoObject InstantiateObject(final IsoCell isoCell) {
                return new IsoPlayer(isoCell);
            }
        });
        addIsoObjectFactory(new IsoObjectFactory(2, "Survivor") {
            @Override
            protected IsoObject InstantiateObject(final IsoCell isoCell) {
                return new IsoSurvivor(isoCell);
            }
        });
        addIsoObjectFactory(new IsoObjectFactory(3, "Zombie") {
            @Override
            protected IsoObject InstantiateObject(final IsoCell isoCell) {
                return new IsoZombie(isoCell);
            }
        });
        addIsoObjectFactory(new IsoObjectFactory(4, "Pushable") {
            @Override
            protected IsoObject InstantiateObject(final IsoCell isoCell) {
                return new IsoPushableObject(isoCell);
            }
        });
        addIsoObjectFactory(new IsoObjectFactory(5, "WheelieBin") {
            @Override
            protected IsoObject InstantiateObject(final IsoCell isoCell) {
                return new IsoWheelieBin(isoCell);
            }
        });
        addIsoObjectFactory(new IsoObjectFactory(6, "WorldInventoryItem") {
            @Override
            protected IsoObject InstantiateObject(final IsoCell isoCell) {
                return new IsoWorldInventoryObject(isoCell);
            }
        });
        addIsoObjectFactory(new IsoObjectFactory(7, "Jukebox") {
            @Override
            protected IsoObject InstantiateObject(final IsoCell isoCell) {
                return new IsoJukebox(isoCell);
            }
        });
        addIsoObjectFactory(new IsoObjectFactory(8, "Curtain") {
            @Override
            protected IsoObject InstantiateObject(final IsoCell isoCell) {
                return new IsoCurtain(isoCell);
            }
        });
        addIsoObjectFactory(new IsoObjectFactory(9, "Radio") {
            @Override
            protected IsoObject InstantiateObject(final IsoCell isoCell) {
                return new IsoRadio(isoCell);
            }
        });
        addIsoObjectFactory(new IsoObjectFactory(10, "Television") {
            @Override
            protected IsoObject InstantiateObject(final IsoCell isoCell) {
                return new IsoTelevision(isoCell);
            }
        });
        addIsoObjectFactory(new IsoObjectFactory(11, "DeadBody") {
            @Override
            protected IsoObject InstantiateObject(final IsoCell isoCell) {
                return new IsoDeadBody(isoCell);
            }
        });
        addIsoObjectFactory(new IsoObjectFactory(12, "Barbecue") {
            @Override
            protected IsoObject InstantiateObject(final IsoCell isoCell) {
                return new IsoBarbecue(isoCell);
            }
        });
        addIsoObjectFactory(new IsoObjectFactory(13, "ClothingDryer") {
            @Override
            protected IsoObject InstantiateObject(final IsoCell isoCell) {
                return new IsoClothingDryer(isoCell);
            }
        });
        addIsoObjectFactory(new IsoObjectFactory(14, "ClothingWasher") {
            @Override
            protected IsoObject InstantiateObject(final IsoCell isoCell) {
                return new IsoClothingWasher(isoCell);
            }
        });
        addIsoObjectFactory(new IsoObjectFactory(15, "Fireplace") {
            @Override
            protected IsoObject InstantiateObject(final IsoCell isoCell) {
                return new IsoFireplace(isoCell);
            }
        });
        addIsoObjectFactory(new IsoObjectFactory(16, "Stove") {
            @Override
            protected IsoObject InstantiateObject(final IsoCell isoCell) {
                return new IsoStove(isoCell);
            }
        });
        addIsoObjectFactory(new IsoObjectFactory(17, "Door") {
            @Override
            protected IsoObject InstantiateObject(final IsoCell isoCell) {
                return new IsoDoor(isoCell);
            }
        });
        addIsoObjectFactory(new IsoObjectFactory(18, "Thumpable") {
            @Override
            protected IsoObject InstantiateObject(final IsoCell isoCell) {
                return new IsoThumpable(isoCell);
            }
        });
        addIsoObjectFactory(new IsoObjectFactory(19, "IsoTrap") {
            @Override
            protected IsoObject InstantiateObject(final IsoCell isoCell) {
                return new IsoTrap(isoCell);
            }
        });
        addIsoObjectFactory(new IsoObjectFactory(20, "IsoBrokenGlass") {
            @Override
            protected IsoObject InstantiateObject(final IsoCell isoCell) {
                return new IsoBrokenGlass(isoCell);
            }
        });
        addIsoObjectFactory(new IsoObjectFactory(21, "IsoCarBatteryCharger") {
            @Override
            protected IsoObject InstantiateObject(final IsoCell isoCell) {
                return new IsoCarBatteryCharger(isoCell);
            }
        });
        addIsoObjectFactory(new IsoObjectFactory(22, "IsoGenerator") {
            @Override
            protected IsoObject InstantiateObject(final IsoCell isoCell) {
                return new IsoGenerator(isoCell);
            }
        });
        addIsoObjectFactory(new IsoObjectFactory(23, "IsoCompost") {
            @Override
            protected IsoObject InstantiateObject(final IsoCell isoCell) {
                return new IsoCompost(isoCell);
            }
        });
        addIsoObjectFactory(new IsoObjectFactory(24, "Mannequin") {
            @Override
            protected IsoObject InstantiateObject(final IsoCell isoCell) {
                return new IsoMannequin(isoCell);
            }
        });
        addIsoObjectFactory(new IsoObjectFactory(25, "StoneFurnace") {
            @Override
            protected IsoObject InstantiateObject(final IsoCell isoCell) {
                return new BSFurnace(isoCell);
            }
        });
        addIsoObjectFactory(new IsoObjectFactory(26, "Window") {
            @Override
            protected IsoObject InstantiateObject(final IsoCell isoCell) {
                return new IsoWindow(isoCell);
            }
        });
        addIsoObjectFactory(new IsoObjectFactory(27, "Barricade") {
            @Override
            protected IsoObject InstantiateObject(final IsoCell isoCell) {
                return new IsoBarricade(isoCell);
            }
        });
        addIsoObjectFactory(new IsoObjectFactory(28, "Tree") {
            @Override
            protected IsoObject InstantiateObject(final IsoCell isoCell) {
                return IsoTree.getNew();
            }
        });
        addIsoObjectFactory(new IsoObjectFactory(29, "LightSwitch") {
            @Override
            protected IsoObject InstantiateObject(final IsoCell isoCell) {
                return new IsoLightSwitch(isoCell);
            }
        });
        addIsoObjectFactory(new IsoObjectFactory(30, "ZombieGiblets") {
            @Override
            protected IsoObject InstantiateObject(final IsoCell isoCell) {
                return new IsoZombieGiblets(isoCell);
            }
        });
        addIsoObjectFactory(new IsoObjectFactory(31, "MolotovCocktail") {
            @Override
            protected IsoObject InstantiateObject(final IsoCell isoCell) {
                return new IsoMolotovCocktail(isoCell);
            }
        });
        addIsoObjectFactory(new IsoObjectFactory(32, "Fire") {
            @Override
            protected IsoObject InstantiateObject(final IsoCell isoCell) {
                return new IsoFire(isoCell);
            }
        });
        IsoObject.factoryVehicle = addIsoObjectFactory(new IsoObjectFactory(33, "Vehicle") {
            @Override
            protected IsoObject InstantiateObject(final IsoCell isoCell) {
                return new BaseVehicle(isoCell);
            }
        });
    }
    
    public static byte factoryGetClassID(final String s) {
        final IsoObjectFactory isoObjectFactory = IsoObject.hashCodeToObjectMap.get(s.hashCode());
        if (isoObjectFactory != null) {
            return isoObjectFactory.classID;
        }
        return IsoObject.factoryIsoObject.classID;
    }
    
    public static IsoObject factoryFromFileInput(final IsoCell isoCell, final byte b) {
        final IsoObjectFactory isoObjectFactory = IsoObject.byteToObjectMap.get(b);
        if (isoObjectFactory != null && (!isoObjectFactory.objectName.equals("Vehicle") || !GameClient.bClient)) {
            return isoObjectFactory.InstantiateObject(isoCell);
        }
        if (isoObjectFactory == null && Core.bDebug) {
            throw new RuntimeException(invokedynamic(makeConcatWithConstants:(B)Ljava/lang/String;, b));
        }
        return new IsoObject(isoCell);
    }
    
    @Deprecated
    public static IsoObject factoryFromFileInput_OLD(final IsoCell isoCell, final int n) {
        if (n == "IsoObject".hashCode()) {
            final IsoObject new1 = getNew();
            new1.sx = 0.0f;
            return new1;
        }
        if (n == "Player".hashCode()) {
            return new IsoPlayer(isoCell);
        }
        if (n == "Survivor".hashCode()) {
            return new IsoSurvivor(isoCell);
        }
        if (n == "Zombie".hashCode()) {
            return new IsoZombie(isoCell);
        }
        if (n == "Pushable".hashCode()) {
            return new IsoPushableObject(isoCell);
        }
        if (n == "WheelieBin".hashCode()) {
            return new IsoWheelieBin(isoCell);
        }
        if (n == "WorldInventoryItem".hashCode()) {
            return new IsoWorldInventoryObject(isoCell);
        }
        if (n == "Jukebox".hashCode()) {
            return new IsoJukebox(isoCell);
        }
        if (n == "Curtain".hashCode()) {
            return new IsoCurtain(isoCell);
        }
        if (n == "Radio".hashCode()) {
            return new IsoRadio(isoCell);
        }
        if (n == "Television".hashCode()) {
            return new IsoTelevision(isoCell);
        }
        if (n == "DeadBody".hashCode()) {
            return new IsoDeadBody(isoCell);
        }
        if (n == "Barbecue".hashCode()) {
            return new IsoBarbecue(isoCell);
        }
        if (n == "ClothingDryer".hashCode()) {
            return new IsoClothingDryer(isoCell);
        }
        if (n == "ClothingWasher".hashCode()) {
            return new IsoClothingWasher(isoCell);
        }
        if (n == "Fireplace".hashCode()) {
            return new IsoFireplace(isoCell);
        }
        if (n == "Stove".hashCode()) {
            return new IsoStove(isoCell);
        }
        if (n == "Door".hashCode()) {
            return new IsoDoor(isoCell);
        }
        if (n == "Thumpable".hashCode()) {
            return new IsoThumpable(isoCell);
        }
        if (n == "IsoTrap".hashCode()) {
            return new IsoTrap(isoCell);
        }
        if (n == "IsoBrokenGlass".hashCode()) {
            return new IsoBrokenGlass(isoCell);
        }
        if (n == "IsoCarBatteryCharger".hashCode()) {
            return new IsoCarBatteryCharger(isoCell);
        }
        if (n == "IsoGenerator".hashCode()) {
            return new IsoGenerator(isoCell);
        }
        if (n == "IsoCompost".hashCode()) {
            return new IsoCompost(isoCell);
        }
        if (n == "Mannequin".hashCode()) {
            return new IsoMannequin(isoCell);
        }
        if (n == "StoneFurnace".hashCode()) {
            return new BSFurnace(isoCell);
        }
        if (n == "Window".hashCode()) {
            return new IsoWindow(isoCell);
        }
        if (n == "Barricade".hashCode()) {
            return new IsoBarricade(isoCell);
        }
        if (n == "Tree".hashCode()) {
            return IsoTree.getNew();
        }
        if (n == "LightSwitch".hashCode()) {
            return new IsoLightSwitch(isoCell);
        }
        if (n == "ZombieGiblets".hashCode()) {
            return new IsoZombieGiblets(isoCell);
        }
        if (n == "MolotovCocktail".hashCode()) {
            return new IsoMolotovCocktail(isoCell);
        }
        if (n == "Fire".hashCode()) {
            return new IsoFire(isoCell);
        }
        if (n == "Vehicle".hashCode() && !GameClient.bClient) {
            return new BaseVehicle(isoCell);
        }
        return new IsoObject(isoCell);
    }
    
    @Deprecated
    public static Class factoryClassFromFileInput(final IsoCell isoCell, final int n) {
        if (n == "IsoObject".hashCode()) {
            return IsoObject.class;
        }
        if (n == "Player".hashCode()) {
            return IsoPlayer.class;
        }
        if (n == "Survivor".hashCode()) {
            return IsoSurvivor.class;
        }
        if (n == "Zombie".hashCode()) {
            return IsoZombie.class;
        }
        if (n == "Pushable".hashCode()) {
            return IsoPushableObject.class;
        }
        if (n == "WheelieBin".hashCode()) {
            return IsoWheelieBin.class;
        }
        if (n == "WorldInventoryItem".hashCode()) {
            return IsoWorldInventoryObject.class;
        }
        if (n == "Jukebox".hashCode()) {
            return IsoJukebox.class;
        }
        if (n == "Curtain".hashCode()) {
            return IsoCurtain.class;
        }
        if (n == "Radio".hashCode()) {
            return IsoRadio.class;
        }
        if (n == "Television".hashCode()) {
            return IsoTelevision.class;
        }
        if (n == "DeadBody".hashCode()) {
            return IsoDeadBody.class;
        }
        if (n == "Barbecue".hashCode()) {
            return IsoBarbecue.class;
        }
        if (n == "ClothingDryer".hashCode()) {
            return IsoClothingDryer.class;
        }
        if (n == "ClothingWasher".hashCode()) {
            return IsoClothingWasher.class;
        }
        if (n == "Fireplace".hashCode()) {
            return IsoFireplace.class;
        }
        if (n == "Stove".hashCode()) {
            return IsoStove.class;
        }
        if (n == "Mannequin".hashCode()) {
            return IsoMannequin.class;
        }
        if (n == "Door".hashCode()) {
            return IsoDoor.class;
        }
        if (n == "Thumpable".hashCode()) {
            return IsoThumpable.class;
        }
        if (n == "Window".hashCode()) {
            return IsoWindow.class;
        }
        if (n == "Barricade".hashCode()) {
            return IsoBarricade.class;
        }
        if (n == "Tree".hashCode()) {
            return IsoTree.class;
        }
        if (n == "LightSwitch".hashCode()) {
            return IsoLightSwitch.class;
        }
        if (n == "ZombieGiblets".hashCode()) {
            return IsoZombieGiblets.class;
        }
        if (n == "MolotovCocktail".hashCode()) {
            return IsoMolotovCocktail.class;
        }
        if (n == "Vehicle".hashCode()) {
            return BaseVehicle.class;
        }
        return IsoObject.class;
    }
    
    @Deprecated
    static IsoObject factoryFromFileInput(final IsoCell isoCell, final DataInputStream dataInputStream) throws IOException {
        if (!dataInputStream.readBoolean()) {
            return null;
        }
        return factoryFromFileInput(isoCell, dataInputStream.readByte());
    }
    
    public static IsoObject factoryFromFileInput(final IsoCell isoCell, final ByteBuffer byteBuffer) {
        if (byteBuffer.get() == 0) {
            return null;
        }
        return factoryFromFileInput(isoCell, byteBuffer.get());
    }
    
    public void syncIsoObject(final boolean b, final byte b2, final UdpConnection udpConnection, final ByteBuffer byteBuffer) {
    }
    
    public void syncIsoObjectSend(final ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putInt(this.square.getX());
        byteBufferWriter.putInt(this.square.getY());
        byteBufferWriter.putInt(this.square.getZ());
        byteBufferWriter.putByte((byte)this.square.getObjects().indexOf(this));
        byteBufferWriter.putByte((byte)0);
        byteBufferWriter.putByte((byte)0);
    }
    
    public String getTextureName() {
        if (this.sprite == null) {
            return null;
        }
        return this.sprite.name;
    }
    
    public boolean Serialize() {
        return true;
    }
    
    public KahluaTable getModData() {
        if (this.table == null) {
            this.table = LuaManager.platform.newTable();
        }
        return this.table;
    }
    
    public boolean hasModData() {
        return this.table != null && !this.table.isEmpty();
    }
    
    public IsoGridSquare getSquare() {
        return this.square;
    }
    
    public void setSquare(final IsoGridSquare square) {
        this.square = square;
    }
    
    public void update() {
        this.checkHaveElectricity();
    }
    
    public void renderlast() {
    }
    
    public void DirtySlice() {
    }
    
    public String getObjectName() {
        if (this.name != null) {
            return this.name;
        }
        if (this.sprite != null && this.sprite.getParentObjectName() != null) {
            return this.sprite.getParentObjectName();
        }
        return "IsoObject";
    }
    
    public final void load(final ByteBuffer byteBuffer, final int n) throws IOException {
        this.load(byteBuffer, n, false);
    }
    
    public void load(final ByteBuffer byteBuffer, final int n, final boolean b) throws IOException {
        final int fix2x = IsoChunk.Fix2x(this.square, byteBuffer.getInt());
        this.sprite = IsoSprite.getSprite(IsoSpriteManager.instance, fix2x);
        if (fix2x == -1) {
            this.sprite = IsoSpriteManager.instance.getSprite("");
            assert this.sprite != null;
            assert this.sprite.ID == -1;
        }
        final BitHeaderRead allocRead = BitHeader.allocRead(BitHeader.HeaderSize.Byte, byteBuffer);
        if (!allocRead.equals(0)) {
            if (allocRead.hasFlags(1)) {
                int n2;
                if (allocRead.hasFlags(2)) {
                    n2 = 1;
                }
                else {
                    n2 = (byteBuffer.get() & 0xFF);
                }
                if (b) {
                    DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, GameWindow.ReadStringUTF(byteBuffer), n2));
                }
                for (int i = 0; i < n2; ++i) {
                    if (this.AttachedAnimSprite == null) {
                        this.AttachedAnimSprite = new ArrayList<IsoSpriteInstance>();
                    }
                    final IsoSprite sprite = IsoSprite.getSprite(IsoSpriteManager.instance, byteBuffer.getInt());
                    IsoSpriteInstance instance = null;
                    if (sprite != null) {
                        instance = sprite.newInstance();
                    }
                    else if (Core.bDebug) {
                        DebugLog.General.warn((Object)"discarding attached sprite because it has no tile properties");
                    }
                    final byte value = byteBuffer.get();
                    boolean b2 = false;
                    boolean b3 = false;
                    if ((value & 0x2) != 0x0) {
                        b2 = true;
                    }
                    if ((value & 0x4) != 0x0 && instance != null) {
                        instance.Flip = true;
                    }
                    if ((value & 0x8) != 0x0 && instance != null) {
                        instance.bCopyTargetAlpha = true;
                    }
                    if ((value & 0x10) != 0x0) {
                        b3 = true;
                        if (instance != null) {
                            instance.bMultiplyObjectAlpha = true;
                        }
                    }
                    if (b2) {
                        final float float1 = byteBuffer.getFloat();
                        final float float2 = byteBuffer.getFloat();
                        final float float3 = byteBuffer.getFloat();
                        final float unpackByteToFloatUnit = Bits.unpackByteToFloatUnit(byteBuffer.get());
                        final float unpackByteToFloatUnit2 = Bits.unpackByteToFloatUnit(byteBuffer.get());
                        final float unpackByteToFloatUnit3 = Bits.unpackByteToFloatUnit(byteBuffer.get());
                        if (instance != null) {
                            instance.offX = float1;
                            instance.offY = float2;
                            instance.offZ = float3;
                            instance.tintr = unpackByteToFloatUnit;
                            instance.tintg = unpackByteToFloatUnit2;
                            instance.tintb = unpackByteToFloatUnit3;
                        }
                    }
                    else if (instance != null) {
                        instance.offX = 0.0f;
                        instance.offY = 0.0f;
                        instance.offZ = 0.0f;
                        instance.tintr = 1.0f;
                        instance.tintg = 1.0f;
                        instance.tintb = 1.0f;
                        instance.alpha = 1.0f;
                        instance.targetAlpha = 1.0f;
                    }
                    if (b3) {
                        final float float4 = byteBuffer.getFloat();
                        if (instance != null) {
                            instance.alpha = float4;
                        }
                    }
                    if (sprite != null) {
                        if (sprite.name != null && sprite.name.startsWith("overlay_blood_")) {
                            final IsoWallBloodSplat e = new IsoWallBloodSplat((float)GameTime.getInstance().getWorldAgeHours(), sprite);
                            if (this.wallBloodSplats == null) {
                                this.wallBloodSplats = new ArrayList<IsoWallBloodSplat>();
                            }
                            this.wallBloodSplats.add(e);
                        }
                        else {
                            this.AttachedAnimSprite.add(instance);
                        }
                    }
                }
            }
            if (allocRead.hasFlags(4)) {
                if (b) {
                    DebugLog.log(GameWindow.ReadStringUTF(byteBuffer));
                }
                final byte value2 = byteBuffer.get();
                if ((value2 & 0x2) != 0x0) {
                    this.name = "Grass";
                }
                else if ((value2 & 0x4) != 0x0) {
                    this.name = WorldDictionary.getObjectNameFromID(byteBuffer.get());
                }
                else if ((value2 & 0x8) != 0x0) {
                    this.name = GameWindow.ReadString(byteBuffer);
                }
                if ((value2 & 0x10) != 0x0) {
                    this.spriteName = WorldDictionary.getSpriteNameFromID(byteBuffer.getInt());
                }
                else if ((value2 & 0x20) != 0x0) {
                    this.spriteName = GameWindow.ReadString(byteBuffer);
                }
            }
            if (allocRead.hasFlags(8)) {
                this.customColor = new ColorInfo(Bits.unpackByteToFloatUnit(byteBuffer.get()), Bits.unpackByteToFloatUnit(byteBuffer.get()), Bits.unpackByteToFloatUnit(byteBuffer.get()), 1.0f);
            }
            this.doNotSync = allocRead.hasFlags(16);
            this.setOutlineOnMouseover(allocRead.hasFlags(32));
            if (allocRead.hasFlags(64)) {
                final BitHeaderRead allocRead2 = BitHeader.allocRead(BitHeader.HeaderSize.Short, byteBuffer);
                if (allocRead2.hasFlags(1)) {
                    final byte value3 = byteBuffer.get();
                    if (value3 > 0) {
                        if (this.wallBloodSplats == null) {
                            this.wallBloodSplats = new ArrayList<IsoWallBloodSplat>();
                        }
                        int value4 = 0;
                        if (GameClient.bClient || GameServer.bServer) {
                            value4 = ServerOptions.getInstance().BloodSplatLifespanDays.getValue();
                        }
                        final float worldAge = (float)GameTime.getInstance().getWorldAgeHours();
                        for (byte b4 = 0; b4 < value3; ++b4) {
                            final IsoWallBloodSplat e2 = new IsoWallBloodSplat();
                            e2.load(byteBuffer, n);
                            if (e2.worldAge > worldAge) {
                                e2.worldAge = worldAge;
                            }
                            if (value4 <= 0 || worldAge - e2.worldAge < value4 * 24) {
                                this.wallBloodSplats.add(e2);
                            }
                        }
                    }
                }
                if (allocRead2.hasFlags(2)) {
                    if (b) {
                        DebugLog.log(GameWindow.ReadStringUTF(byteBuffer));
                    }
                    for (byte value5 = byteBuffer.get(), b5 = 0; b5 < value5; ++b5) {
                        try {
                            final ItemContainer container = new ItemContainer();
                            container.ID = 0;
                            container.parent = this;
                            container.parent.square = this.square;
                            container.SourceGrid = this.square;
                            container.load(byteBuffer, n);
                            if (b5 == 0) {
                                if (this instanceof IsoDeadBody) {
                                    container.Capacity = 8;
                                }
                                this.container = container;
                            }
                            else {
                                this.addSecondaryContainer(container);
                            }
                        }
                        catch (Exception cause) {
                            if (this.container != null) {
                                DebugLog.log(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.container.ID));
                            }
                            throw new RuntimeException(cause);
                        }
                    }
                }
                if (allocRead2.hasFlags(4)) {
                    if (this.table == null) {
                        this.table = LuaManager.platform.newTable();
                    }
                    this.table.load(byteBuffer, n);
                }
                this.setSpecialTooltip(allocRead2.hasFlags(8));
                if (allocRead2.hasFlags(16)) {
                    this.keyId = byteBuffer.getInt();
                }
                this.usesExternalWaterSource = allocRead2.hasFlags(32);
                if (allocRead2.hasFlags(64)) {
                    this.sheetRope = true;
                    this.sheetRopeHealth = byteBuffer.getFloat();
                }
                else {
                    this.sheetRope = false;
                }
                if (allocRead2.hasFlags(128)) {
                    this.renderYOffset = byteBuffer.getFloat();
                }
                if (allocRead2.hasFlags(256)) {
                    String name;
                    if (allocRead2.hasFlags(512)) {
                        name = GameWindow.ReadString(byteBuffer);
                    }
                    else {
                        name = WorldDictionary.getSpriteNameFromID(byteBuffer.getInt());
                    }
                    if (name != null && !name.isEmpty()) {
                        this.overlaySprite = IsoSpriteManager.instance.getSprite(name);
                        this.overlaySprite.name = name;
                    }
                }
                if (allocRead2.hasFlags(1024)) {
                    final float unpackByteToFloatUnit4 = Bits.unpackByteToFloatUnit(byteBuffer.get());
                    final float unpackByteToFloatUnit5 = Bits.unpackByteToFloatUnit(byteBuffer.get());
                    final float unpackByteToFloatUnit6 = Bits.unpackByteToFloatUnit(byteBuffer.get());
                    final float unpackByteToFloatUnit7 = Bits.unpackByteToFloatUnit(byteBuffer.get());
                    if (this.overlaySprite != null) {
                        this.setOverlaySpriteColor(unpackByteToFloatUnit4, unpackByteToFloatUnit5, unpackByteToFloatUnit6, unpackByteToFloatUnit7);
                    }
                }
                this.setMovedThumpable(allocRead2.hasFlags(2048));
                allocRead2.release();
            }
        }
        allocRead.release();
        if (this.sprite == null) {
            (this.sprite = IsoSprite.CreateSprite(IsoSpriteManager.instance)).LoadFramesNoDirPageSimple(this.spriteName);
        }
    }
    
    public final void save(final ByteBuffer byteBuffer) throws IOException {
        this.save(byteBuffer, false);
    }
    
    public void save(final ByteBuffer byteBuffer, final boolean b) throws IOException {
        byteBuffer.put((byte)(this.Serialize() ? 1 : 0));
        if (!this.Serialize()) {
            return;
        }
        byteBuffer.put(factoryGetClassID(this.getObjectName()));
        byteBuffer.putInt((this.sprite == null) ? -1 : this.sprite.ID);
        final BitHeaderWrite allocWrite = BitHeader.allocWrite(BitHeader.HeaderSize.Byte, byteBuffer);
        if (this.AttachedAnimSprite != null) {
            allocWrite.addFlags(1);
            if (this.AttachedAnimSprite.size() == 1) {
                allocWrite.addFlags(2);
            }
            final int n = (this.AttachedAnimSprite.size() > 255) ? 255 : this.AttachedAnimSprite.size();
            if (n != 1) {
                byteBuffer.put((byte)n);
            }
            if (b) {
                GameWindow.WriteString(byteBuffer, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n));
            }
            for (int i = 0; i < n; ++i) {
                final IsoSpriteInstance isoSpriteInstance = this.AttachedAnimSprite.get(i);
                byteBuffer.putInt(isoSpriteInstance.getID());
                byte b2 = 0;
                boolean b3 = false;
                if (isoSpriteInstance.offX != 0.0f || isoSpriteInstance.offY != 0.0f || isoSpriteInstance.offZ != 0.0f || isoSpriteInstance.tintr != 1.0f || isoSpriteInstance.tintg != 1.0f || isoSpriteInstance.tintb != 1.0f) {
                    b2 |= 0x2;
                    b3 = true;
                }
                if (isoSpriteInstance.Flip) {
                    b2 |= 0x4;
                }
                if (isoSpriteInstance.bCopyTargetAlpha) {
                    b2 |= 0x8;
                }
                if (isoSpriteInstance.bMultiplyObjectAlpha) {
                    b2 |= 0x10;
                }
                byteBuffer.put(b2);
                if (b3) {
                    byteBuffer.putFloat(isoSpriteInstance.offX);
                    byteBuffer.putFloat(isoSpriteInstance.offY);
                    byteBuffer.putFloat(isoSpriteInstance.offZ);
                    byteBuffer.put(Bits.packFloatUnitToByte(isoSpriteInstance.tintr));
                    byteBuffer.put(Bits.packFloatUnitToByte(isoSpriteInstance.tintg));
                    byteBuffer.put(Bits.packFloatUnitToByte(isoSpriteInstance.tintb));
                }
                if (isoSpriteInstance.bMultiplyObjectAlpha) {
                    byteBuffer.putFloat(isoSpriteInstance.alpha);
                }
            }
        }
        if (this.name != null || this.spriteName != null) {
            allocWrite.addFlags(4);
            if (b) {
                GameWindow.WriteString(byteBuffer, "Writing name");
            }
            byte b4 = 0;
            byte idForObjectName = -1;
            int idForSpriteName = -1;
            if (this.name != null) {
                if (this.name.equals("Grass")) {
                    b4 |= 0x2;
                }
                else {
                    idForObjectName = WorldDictionary.getIdForObjectName(this.name);
                    if (idForObjectName >= 0) {
                        b4 |= 0x4;
                    }
                    else {
                        b4 |= 0x8;
                    }
                }
            }
            if (this.spriteName != null) {
                idForSpriteName = WorldDictionary.getIdForSpriteName(this.spriteName);
                if (idForSpriteName >= 0) {
                    b4 |= 0x10;
                }
                else {
                    b4 |= 0x20;
                }
            }
            byteBuffer.put(b4);
            if (this.name != null && !this.name.equals("Grass")) {
                if (idForObjectName >= 0) {
                    byteBuffer.put(idForObjectName);
                }
                else {
                    GameWindow.WriteString(byteBuffer, this.name);
                }
            }
            if (this.spriteName != null) {
                if (idForSpriteName >= 0) {
                    byteBuffer.putInt(idForSpriteName);
                }
                else {
                    GameWindow.WriteString(byteBuffer, this.spriteName);
                }
            }
        }
        if (this.customColor != null) {
            allocWrite.addFlags(8);
            byteBuffer.put(Bits.packFloatUnitToByte(this.customColor.r));
            byteBuffer.put(Bits.packFloatUnitToByte(this.customColor.g));
            byteBuffer.put(Bits.packFloatUnitToByte(this.customColor.b));
        }
        if (this.doNotSync) {
            allocWrite.addFlags(16);
        }
        if (this.isOutlineOnMouseover()) {
            allocWrite.addFlags(32);
        }
        final BitHeaderWrite allocWrite2 = BitHeader.allocWrite(BitHeader.HeaderSize.Short, byteBuffer);
        if (this.wallBloodSplats != null) {
            allocWrite2.addFlags(1);
            final int min = Math.min(this.wallBloodSplats.size(), 32);
            final int n2 = this.wallBloodSplats.size() - min;
            byteBuffer.put((byte)min);
            for (int j = n2; j < this.wallBloodSplats.size(); ++j) {
                this.wallBloodSplats.get(j).save(byteBuffer);
            }
        }
        if (this.getContainerCount() > 0) {
            allocWrite2.addFlags(2);
            if (b) {
                GameWindow.WriteString(byteBuffer, "Writing container");
            }
            byteBuffer.put((byte)this.getContainerCount());
            for (int k = 0; k < this.getContainerCount(); ++k) {
                this.getContainerByIndex(k).save(byteBuffer);
            }
        }
        if (this.table != null && !this.table.isEmpty()) {
            allocWrite2.addFlags(4);
            this.table.save(byteBuffer);
        }
        if (this.haveSpecialTooltip()) {
            allocWrite2.addFlags(8);
        }
        if (this.getKeyId() != -1) {
            allocWrite2.addFlags(16);
            byteBuffer.putInt(this.getKeyId());
        }
        if (this.usesExternalWaterSource) {
            allocWrite2.addFlags(32);
        }
        if (this.sheetRope) {
            allocWrite2.addFlags(64);
            byteBuffer.putFloat(this.sheetRopeHealth);
        }
        if (this.renderYOffset != 0.0f) {
            allocWrite2.addFlags(128);
            byteBuffer.putFloat(this.renderYOffset);
        }
        if (this.getOverlaySprite() != null) {
            allocWrite2.addFlags(256);
            final int idForSpriteName2 = WorldDictionary.getIdForSpriteName(this.getOverlaySprite().name);
            if (idForSpriteName2 < 0) {
                allocWrite2.addFlags(512);
                GameWindow.WriteString(byteBuffer, this.getOverlaySprite().name);
            }
            else {
                byteBuffer.putInt(idForSpriteName2);
            }
            if (this.getOverlaySpriteColor() != null) {
                allocWrite2.addFlags(1024);
                byteBuffer.put(Bits.packFloatUnitToByte(this.getOverlaySpriteColor().r));
                byteBuffer.put(Bits.packFloatUnitToByte(this.getOverlaySpriteColor().g));
                byteBuffer.put(Bits.packFloatUnitToByte(this.getOverlaySpriteColor().b));
                byteBuffer.put(Bits.packFloatUnitToByte(this.getOverlaySpriteColor().a));
            }
        }
        if (this.isMovedThumpable()) {
            allocWrite2.addFlags(2048);
        }
        if (!allocWrite2.equals(0)) {
            allocWrite.addFlags(64);
            allocWrite2.write();
        }
        else {
            byteBuffer.position(allocWrite2.getStartPosition());
        }
        allocWrite.write();
        allocWrite.release();
        allocWrite2.release();
    }
    
    public void saveState(final ByteBuffer byteBuffer) throws IOException {
    }
    
    public void loadState(final ByteBuffer byteBuffer) throws IOException {
    }
    
    public void softReset() {
        if (this.container != null) {
            this.container.Items.clear();
            this.setOverlaySprite(null, -1.0f, -1.0f, -1.0f, -1.0f, this.container.bExplored = false);
        }
        if (this.AttachedAnimSprite != null && !this.AttachedAnimSprite.isEmpty()) {
            for (int i = 0; i < this.AttachedAnimSprite.size(); ++i) {
                final IsoSprite parentSprite = this.AttachedAnimSprite.get(i).parentSprite;
                if (parentSprite.name != null && parentSprite.name.contains("blood")) {
                    this.AttachedAnimSprite.remove(i);
                    --i;
                }
            }
        }
    }
    
    public void AttackObject(final IsoGameCharacter isoGameCharacter) {
        this.Damage -= 10;
        SoundManager.instance.PlaySound(((HandWeapon)isoGameCharacter.getPrimaryHandItem()).getDoorHitSound(), false, 2.0f);
        WorldSoundManager.instance.addSound(isoGameCharacter, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, false, 0.0f, 15.0f);
        if (this.Damage <= 0) {
            this.square.getObjects().remove(this);
            this.square.RecalcAllWithNeighbours(true);
            if (this.getType() == IsoObjectType.stairsBN || this.getType() == IsoObjectType.stairsMN || this.getType() == IsoObjectType.stairsTN || this.getType() == IsoObjectType.stairsBW || this.getType() == IsoObjectType.stairsMW || this.getType() == IsoObjectType.stairsTW) {
                this.square.RemoveAllWith(IsoFlagType.attachtostairs);
            }
            for (int n = 1, i = 0; i < n; ++i) {
                this.square.AddWorldInventoryItem("Base.Plank", Rand.Next(-1.0f, 1.0f), Rand.Next(-1.0f, 1.0f), 0.0f).setUses(1);
            }
        }
    }
    
    public void onMouseRightClick(final int n, final int n2) {
    }
    
    public void onMouseRightReleased() {
    }
    
    public void Hit(final Vector2 vector2, final IsoObject isoObject, final float n) {
        if (isoObject instanceof BaseVehicle) {
            this.HitByVehicle((BaseVehicle)isoObject, n);
            if (this.Damage <= 0 && BrokenFences.getInstance().isBreakableObject(this)) {
                final PropertyContainer properties = this.getProperties();
                IsoDirections isoDirections;
                if (properties.Is(IsoFlagType.collideN) && properties.Is(IsoFlagType.collideW)) {
                    isoDirections = ((isoObject.getY() >= this.getY()) ? IsoDirections.N : IsoDirections.S);
                }
                else if (properties.Is(IsoFlagType.collideN)) {
                    isoDirections = ((isoObject.getY() >= this.getY()) ? IsoDirections.N : IsoDirections.S);
                }
                else {
                    isoDirections = ((isoObject.getX() >= this.getX()) ? IsoDirections.W : IsoDirections.E);
                }
                BrokenFences.getInstance().destroyFence(this, isoDirections);
            }
        }
    }
    
    public void Damage(final float n) {
        this.Damage -= (short)(n * 0.1);
    }
    
    public void HitByVehicle(final BaseVehicle baseVehicle, final float n) {
        final short damage = this.Damage;
        this.Damage -= (short)(n * 0.1);
        final BaseSoundEmitter freeEmitter = IsoWorld.instance.getFreeEmitter(this.square.x + 0.5f, this.square.y + 0.5f, (float)this.square.z);
        freeEmitter.setParameterValue(freeEmitter.playSound("VehicleHitObject"), FMODManager.instance.getParameterDescription("VehicleSpeed"), baseVehicle.getCurrentSpeedKmHour());
        WorldSoundManager.instance.addSound(null, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, true, 4.0f, 15.0f);
        if (this.getProperties().Is("HitByCar") && this.getSprite().getProperties().Val("DamagedSprite") != null && !this.getSprite().getProperties().Val("DamagedSprite").equals("") && this.Damage <= 90 && damage > 90) {
            this.setSprite(IsoSpriteManager.instance.getSprite(this.getSprite().getProperties().Val("DamagedSprite")));
            if (this.getSprite().getProperties().Is("StopCar")) {
                this.getSprite().setType(IsoObjectType.isMoveAbleObject);
            }
            else {
                this.getSprite().setType(IsoObjectType.MAX);
            }
            if (this instanceof IsoThumpable) {
                ((IsoThumpable)this).setBlockAllTheSquare(false);
            }
            if (GameServer.bServer) {
                this.transmitUpdatedSpriteToClients();
            }
            this.getSquare().RecalcProperties();
            this.Damage = 50;
        }
        if (this.Damage <= 40 && this.getProperties().Is("HitByCar")) {
            if (!BrokenFences.getInstance().isBreakableObject(this)) {
                this.getSquare().transmitRemoveItemFromSquare(this);
            }
        }
    }
    
    public void Collision(final Vector2 vector2, final IsoObject isoObject) {
        if (isoObject instanceof BaseVehicle) {
            if (this.getProperties().Is("CarSlowFactor")) {
                final int int1 = Integer.parseInt(this.getProperties().Val("CarSlowFactor"));
                final BaseVehicle baseVehicle = (BaseVehicle)isoObject;
                baseVehicle.ApplyImpulse(this, Math.abs(baseVehicle.getFudgedMass() * baseVehicle.getCurrentSpeedKmHour() * int1 / 100.0f));
            }
            if (this.getProperties().Is("HitByCar")) {
                final BaseVehicle baseVehicle2 = (BaseVehicle)isoObject;
                String val = this.getSprite().getProperties().Val("MinimumCarSpeedDmg");
                if (val == null) {
                    val = "150";
                }
                if (Math.abs(baseVehicle2.getCurrentSpeedKmHour()) > Integer.parseInt(val)) {
                    this.HitByVehicle(baseVehicle2, Math.abs(baseVehicle2.getFudgedMass() * baseVehicle2.getCurrentSpeedKmHour()) / 300.0f);
                    if (this.Damage <= 0 && BrokenFences.getInstance().isBreakableObject(this)) {
                        final PropertyContainer properties = this.getProperties();
                        IsoDirections isoDirections;
                        if (properties.Is(IsoFlagType.collideN) && properties.Is(IsoFlagType.collideW)) {
                            isoDirections = ((baseVehicle2.getY() >= this.getY()) ? IsoDirections.N : IsoDirections.S);
                        }
                        else if (properties.Is(IsoFlagType.collideN)) {
                            isoDirections = ((baseVehicle2.getY() >= this.getY()) ? IsoDirections.N : IsoDirections.S);
                        }
                        else {
                            isoDirections = ((baseVehicle2.getX() >= this.getX()) ? IsoDirections.W : IsoDirections.E);
                        }
                        BrokenFences.getInstance().destroyFence(this, isoDirections);
                    }
                }
                else if (!this.square.getProperties().Is(IsoFlagType.collideN) && !this.square.getProperties().Is(IsoFlagType.collideW)) {
                    baseVehicle2.ApplyImpulse(this, Math.abs(baseVehicle2.getFudgedMass() * baseVehicle2.getCurrentSpeedKmHour() * 10.0f / 200.0f));
                    if (baseVehicle2.getCurrentSpeedKmHour() > 3.0f) {
                        baseVehicle2.ApplyImpulse(this, Math.abs(baseVehicle2.getFudgedMass() * baseVehicle2.getCurrentSpeedKmHour() * 10.0f / 150.0f));
                    }
                    baseVehicle2.jniSpeed = 0.0f;
                }
            }
        }
    }
    
    public void UnCollision(final IsoObject isoObject) {
    }
    
    public float GetVehicleSlowFactor(final BaseVehicle baseVehicle) {
        if (this.getProperties().Is("CarSlowFactor")) {
            return 33.0f - (10 - Integer.parseInt(this.getProperties().Val("CarSlowFactor")));
        }
        return 0.0f;
    }
    
    public IsoObject getRerouteCollide() {
        return this.rerouteCollide;
    }
    
    public void setRerouteCollide(final IsoObject rerouteCollide) {
        this.rerouteCollide = rerouteCollide;
    }
    
    public KahluaTable getTable() {
        return this.table;
    }
    
    public void setTable(final KahluaTable table) {
        this.table = table;
    }
    
    public void setAlpha(final float n) {
        this.setAlpha(IsoPlayer.getPlayerIndex(), n);
    }
    
    public void setAlpha(final int n, final float n2) {
        this.alpha[n] = PZMath.clamp(n2, 0.0f, 1.0f);
    }
    
    public void setAlphaToTarget(final int n) {
        this.setAlpha(n, this.getTargetAlpha(n));
    }
    
    public void setAlphaAndTarget(final float n) {
        this.setAlphaAndTarget(IsoPlayer.getPlayerIndex(), n);
    }
    
    public void setAlphaAndTarget(final int n, final float n2) {
        this.setAlpha(n, n2);
        this.setTargetAlpha(n, n2);
    }
    
    public float getAlpha() {
        return this.getAlpha(IsoPlayer.getPlayerIndex());
    }
    
    public float getAlpha(final int n) {
        return this.alpha[n];
    }
    
    public ArrayList<IsoSpriteInstance> getAttachedAnimSprite() {
        return this.AttachedAnimSprite;
    }
    
    public void setAttachedAnimSprite(final ArrayList<IsoSpriteInstance> attachedAnimSprite) {
        this.AttachedAnimSprite = attachedAnimSprite;
    }
    
    public IsoCell getCell() {
        return IsoWorld.instance.CurrentCell;
    }
    
    public ArrayList<IsoSpriteInstance> getChildSprites() {
        return this.AttachedAnimSprite;
    }
    
    public void setChildSprites(final ArrayList<IsoSpriteInstance> attachedAnimSprite) {
        this.AttachedAnimSprite = attachedAnimSprite;
    }
    
    public void clearAttachedAnimSprite() {
        if (this.AttachedAnimSprite == null) {
            return;
        }
        for (int i = 0; i < this.AttachedAnimSprite.size(); ++i) {
            IsoSpriteInstance.add(this.AttachedAnimSprite.get(i));
        }
        this.AttachedAnimSprite.clear();
    }
    
    public ItemContainer getContainer() {
        return this.container;
    }
    
    public void setContainer(final ItemContainer container) {
        container.parent = this;
        this.container = container;
    }
    
    public IsoDirections getDir() {
        return this.dir;
    }
    
    public void setDir(final IsoDirections dir) {
        this.dir = dir;
    }
    
    public void setDir(final int n) {
        this.dir = IsoDirections.fromIndex(n);
    }
    
    public short getDamage() {
        return this.Damage;
    }
    
    public void setDamage(final short damage) {
        this.Damage = damage;
    }
    
    public boolean isNoPicking() {
        return this.NoPicking;
    }
    
    public void setNoPicking(final boolean noPicking) {
        this.NoPicking = noPicking;
    }
    
    public boolean isOutlineOnMouseover() {
        return this.OutlineOnMouseover;
    }
    
    public void setOutlineOnMouseover(final boolean outlineOnMouseover) {
        this.OutlineOnMouseover = outlineOnMouseover;
    }
    
    public IsoObject getRerouteMask() {
        return this.rerouteMask;
    }
    
    public void setRerouteMask(final IsoObject rerouteMask) {
        this.rerouteMask = rerouteMask;
    }
    
    public IsoSprite getSprite() {
        return this.sprite;
    }
    
    public void setSprite(final IsoSprite sprite) {
        this.sprite = sprite;
        this.windRenderEffects = null;
        this.checkMoveWithWind();
    }
    
    public void setSprite(final String s) {
        (this.sprite = IsoSprite.CreateSprite(IsoSpriteManager.instance)).LoadFramesNoDirPageSimple(s);
        this.tile = s;
        this.spriteName = s;
        this.windRenderEffects = null;
        this.checkMoveWithWind();
    }
    
    public void setSpriteFromName(final String s) {
        this.sprite = IsoSpriteManager.instance.getSprite(s);
        this.windRenderEffects = null;
        this.checkMoveWithWind();
    }
    
    public float getTargetAlpha() {
        return this.getTargetAlpha(IsoPlayer.getPlayerIndex());
    }
    
    public void setTargetAlpha(final float n) {
        this.setTargetAlpha(IsoPlayer.getPlayerIndex(), n);
    }
    
    public void setTargetAlpha(final int n, final float n2) {
        this.targetAlpha[n] = PZMath.clamp(n2, 0.0f, 1.0f);
    }
    
    public float getTargetAlpha(final int n) {
        return this.targetAlpha[n];
    }
    
    public boolean isAlphaAndTargetZero() {
        return this.isAlphaAndTargetZero(IsoPlayer.getPlayerIndex());
    }
    
    public boolean isAlphaAndTargetZero(final int n) {
        return this.isAlphaZero(n) && this.isTargetAlphaZero(n);
    }
    
    public boolean isAlphaZero() {
        return this.isAlphaZero(IsoPlayer.getPlayerIndex());
    }
    
    public boolean isAlphaZero(final int n) {
        return this.alpha[n] <= 0.001f;
    }
    
    public boolean isTargetAlphaZero(final int n) {
        return this.targetAlpha[n] <= 0.001f;
    }
    
    public IsoObjectType getType() {
        if (this.sprite == null) {
            return IsoObjectType.MAX;
        }
        return this.sprite.getType();
    }
    
    public void setType(final IsoObjectType type) {
        if (this.sprite != null) {
            this.sprite.setType(type);
        }
    }
    
    public void addChild(final IsoObject e) {
        if (this.Children == null) {
            this.Children = new ArrayList<IsoObject>(4);
        }
        this.Children.add(e);
    }
    
    public void debugPrintout() {
        System.out.println(this.getClass().toString());
        System.out.println(this.getObjectName());
    }
    
    protected void checkMoveWithWind() {
        this.checkMoveWithWind(this.sprite != null && this.sprite.isBush);
    }
    
    protected void checkMoveWithWind(final boolean b) {
        if (GameServer.bServer) {
            return;
        }
        if (this.sprite != null && this.windRenderEffects == null && this.sprite.moveWithWind) {
            if (this.getSquare() != null) {
                final IsoGridSquare gridSquare = this.getCell().getGridSquare(this.getSquare().x - 1, this.getSquare().y, this.getSquare().z);
                if (gridSquare != null) {
                    final IsoGridSquare gridSquare2 = this.getCell().getGridSquare(gridSquare.x, gridSquare.y + 1, gridSquare.z);
                    if (gridSquare2 != null && !gridSquare2.isExteriorCache && gridSquare2.getWall(true) != null) {
                        this.windRenderEffects = null;
                        return;
                    }
                }
                final IsoGridSquare gridSquare3 = this.getCell().getGridSquare(this.getSquare().x, this.getSquare().y - 1, this.getSquare().z);
                if (gridSquare3 != null) {
                    final IsoGridSquare gridSquare4 = this.getCell().getGridSquare(gridSquare3.x + 1, gridSquare3.y, gridSquare3.z);
                    if (gridSquare4 != null && !gridSquare4.isExteriorCache && gridSquare4.getWall(false) != null) {
                        this.windRenderEffects = null;
                        return;
                    }
                }
            }
            this.windRenderEffects = ObjectRenderEffects.getNextWindEffect(this.sprite.windType, b);
            return;
        }
        if (this.windRenderEffects != null && (this.sprite == null || !this.sprite.moveWithWind)) {
            this.windRenderEffects = null;
        }
    }
    
    public void reset() {
        this.tintr = 1.0f;
        this.tintg = 1.0f;
        this.tintb = 1.0f;
        this.name = null;
        this.table = null;
        this.rerouteCollide = null;
        if (this.AttachedAnimSprite != null) {
            for (int i = 0; i < this.AttachedAnimSprite.size(); ++i) {
                IsoSpriteInstance.add(this.AttachedAnimSprite.get(i));
            }
            this.AttachedAnimSprite.clear();
        }
        if (this.wallBloodSplats != null) {
            this.wallBloodSplats.clear();
        }
        this.overlaySprite = null;
        this.overlaySpriteColor = null;
        this.customColor = null;
        if (this.container != null) {
            this.container.Items.clear();
            this.container.IncludingObsoleteItems.clear();
            this.container.setParent(null);
            this.container.setSourceGrid(null);
            this.container.vehiclePart = null;
        }
        this.container = null;
        this.dir = IsoDirections.N;
        this.Damage = 100;
        this.partialThumpDmg = 0.0f;
        this.NoPicking = false;
        this.offsetX = (float)(32 * Core.TileScale);
        this.offsetY = (float)(96 * Core.TileScale);
        this.OutlineOnMouseover = false;
        this.rerouteMask = null;
        this.sprite = null;
        this.square = null;
        for (int j = 0; j < 4; ++j) {
            this.setAlphaAndTarget(j, 1.0f);
        }
        this.bNeverDoneAlpha = true;
        this.bAlphaForced = false;
        this.highlightFlags = 0;
        this.tile = null;
        this.spriteName = null;
        this.specialTooltip = false;
        this.usesExternalWaterSource = false;
        this.externalWaterSource = null;
        if (this.secondaryContainers != null) {
            for (int k = 0; k < this.secondaryContainers.size(); ++k) {
                final ItemContainer itemContainer = this.secondaryContainers.get(k);
                itemContainer.Items.clear();
                itemContainer.IncludingObsoleteItems.clear();
                itemContainer.setParent(null);
                itemContainer.setSourceGrid(null);
                itemContainer.vehiclePart = null;
            }
            this.secondaryContainers.clear();
        }
        this.renderYOffset = 0.0f;
        this.sx = 0.0f;
        this.windRenderEffects = null;
        this.objectRenderEffects = null;
        this.sheetRope = false;
        this.sheetRopeHealth = 100.0f;
        this.bMovedThumpable = false;
    }
    
    public long customHashCode() {
        if (this.doNotSync) {
            return 0L;
        }
        try {
            long n = 1L;
            if (this.getObjectName() != null) {
                n = n * 3L + this.getObjectName().hashCode();
            }
            if (this.name != null) {
                n = n * 2L + this.name.hashCode();
            }
            if (this.container != null) {
                n = n + 1L + this.container.Items.size();
                for (int i = 0; i < this.container.Items.size(); ++i) {
                    n += this.container.Items.get(i).getModule().hashCode() + this.container.Items.get(i).getType().hashCode() + this.container.Items.get(i).id;
                }
            }
            return n + this.square.getObjects().indexOf(this);
        }
        catch (Throwable t) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, t.getMessage()));
            return 0L;
        }
    }
    
    public void SetName(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getSpriteName() {
        return this.spriteName;
    }
    
    public String getTile() {
        return this.tile;
    }
    
    public boolean isCharacter() {
        return this instanceof IsoLivingCharacter;
    }
    
    public boolean isZombie() {
        return false;
    }
    
    public String getScriptName() {
        return "none";
    }
    
    public void AttachAnim(final String s, final String s2, final int n, final float animFrameIncrease, final int n2, final int n3, final boolean loop, final int n4, final boolean deleteWhenFinished, final float n5, final ColorInfo colorInfo) {
        if (this.AttachedAnimSprite == null) {
            this.AttachedAnimSprite = new ArrayList<IsoSpriteInstance>(4);
        }
        final IsoSprite createSpriteUsingCache = IsoSprite.CreateSpriteUsingCache(s, s2, n);
        createSpriteUsingCache.TintMod.set(colorInfo);
        createSpriteUsingCache.soffX = (short)(-n2);
        createSpriteUsingCache.soffY = (short)(-n3);
        createSpriteUsingCache.Animate = true;
        createSpriteUsingCache.Loop = loop;
        createSpriteUsingCache.DeleteWhenFinished = deleteWhenFinished;
        createSpriteUsingCache.PlayAnim(s2);
        final IsoSpriteInstance def = createSpriteUsingCache.def;
        def.AnimFrameIncrease = animFrameIncrease;
        def.Frame = 0.0f;
        this.AttachedAnimSprite.add(def);
    }
    
    public void AttachExistingAnim(final IsoSprite isoSprite, final int i, final int j, final boolean loop, final int n, final boolean deleteWhenFinished, final float n2, final ColorInfo colorInfo) {
        if (this.AttachedAnimSprite == null) {
            this.AttachedAnimSprite = new ArrayList<IsoSpriteInstance>(4);
        }
        isoSprite.TintMod.r = colorInfo.r;
        isoSprite.TintMod.g = colorInfo.g;
        isoSprite.TintMod.b = colorInfo.b;
        isoSprite.TintMod.a = colorInfo.a;
        final Integer value = i;
        final Integer value2 = j;
        isoSprite.soffX = (short)(Object)(-value);
        isoSprite.soffY = (short)(Object)(-value2);
        isoSprite.Animate = true;
        isoSprite.Loop = loop;
        isoSprite.DeleteWhenFinished = deleteWhenFinished;
        this.AttachedAnimSprite.add(IsoSpriteInstance.get(isoSprite));
    }
    
    public void AttachExistingAnim(final IsoSprite isoSprite, final int n, final int n2, final boolean b, final int n3, final boolean b2, final float n4) {
        this.AttachExistingAnim(isoSprite, n, n2, b, n3, b2, n4, new ColorInfo());
    }
    
    public void DoTooltip(final ObjectTooltip objectTooltip) {
    }
    
    public void DoSpecialTooltip(final ObjectTooltip objectTooltip, final IsoGridSquare isoGridSquare) {
        if (this.haveSpecialTooltip()) {
            objectTooltip.setHeight(0.0);
            LuaEventManager.triggerEvent("DoSpecialTooltip", objectTooltip, isoGridSquare);
            if (objectTooltip.getHeight() == 0.0) {
                objectTooltip.hide();
            }
        }
    }
    
    public ItemContainer getItemContainer() {
        return this.container;
    }
    
    public float getOffsetX() {
        return this.offsetX;
    }
    
    public void setOffsetX(final float offsetX) {
        this.offsetX = offsetX;
    }
    
    public float getOffsetY() {
        return this.offsetY;
    }
    
    public void setOffsetY(final float offsetY) {
        this.offsetY = offsetY;
    }
    
    public IsoObject getRerouteMaskObject() {
        return this.rerouteMask;
    }
    
    public boolean HasTooltip() {
        return false;
    }
    
    public boolean getUsesExternalWaterSource() {
        return this.usesExternalWaterSource;
    }
    
    public void setUsesExternalWaterSource(final boolean usesExternalWaterSource) {
        this.usesExternalWaterSource = usesExternalWaterSource;
    }
    
    public boolean hasExternalWaterSource() {
        return this.externalWaterSource != null;
    }
    
    public void doFindExternalWaterSource() {
        this.externalWaterSource = FindExternalWaterSource(this.getSquare());
    }
    
    public static IsoObject FindExternalWaterSource(final IsoGridSquare isoGridSquare) {
        if (isoGridSquare == null) {
            return null;
        }
        return FindExternalWaterSource(isoGridSquare.getX(), isoGridSquare.getY(), isoGridSquare.getZ());
    }
    
    public static IsoObject FindExternalWaterSource(final int n, final int n2, final int n3) {
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n, n2, n3 + 1);
        IsoObject isoObject = null;
        final IsoObject findWaterSourceOnSquare = FindWaterSourceOnSquare(gridSquare);
        if (findWaterSourceOnSquare != null) {
            if (findWaterSourceOnSquare.hasWater()) {
                return findWaterSourceOnSquare;
            }
            isoObject = findWaterSourceOnSquare;
        }
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                if (j != 0 || i != 0) {
                    final IsoObject findWaterSourceOnSquare2 = FindWaterSourceOnSquare(IsoWorld.instance.CurrentCell.getGridSquare(n + j, n2 + i, n3 + 1));
                    if (findWaterSourceOnSquare2 != null) {
                        if (findWaterSourceOnSquare2.hasWater()) {
                            return findWaterSourceOnSquare2;
                        }
                        if (isoObject == null) {
                            isoObject = findWaterSourceOnSquare2;
                        }
                    }
                }
            }
        }
        return isoObject;
    }
    
    public static IsoObject FindWaterSourceOnSquare(final IsoGridSquare isoGridSquare) {
        if (isoGridSquare == null) {
            return null;
        }
        final PZArrayList<IsoObject> objects = isoGridSquare.getObjects();
        for (int i = 0; i < objects.size(); ++i) {
            final IsoObject isoObject = objects.get(i);
            if (isoObject instanceof IsoThumpable) {
                if (isoObject.getSprite() == null || !isoObject.getSprite().solidfloor) {
                    if (!isoObject.getUsesExternalWaterSource() && isoObject.getWaterMax() > 0) {
                        return isoObject;
                    }
                }
            }
        }
        return null;
    }
    
    public int getPipedFuelAmount() {
        if (this.sprite == null) {
            return 0;
        }
        double doubleValue = 0.0;
        if (this.hasModData() && !this.getModData().isEmpty()) {
            final Object rawget = this.getModData().rawget((Object)"fuelAmount");
            if (rawget != null) {
                doubleValue = (double)rawget;
            }
        }
        if (this.sprite.getProperties().Is("fuelAmount")) {
            if (SandboxOptions.instance.FuelStationGas.getValue() == 7) {
                return 1000;
            }
            if (doubleValue == 0.0 && ((SandboxOptions.getInstance().AllowExteriorGenerator.getValue() && this.getSquare().haveElectricity()) || GameTime.getInstance().getNightsSurvived() < SandboxOptions.instance.getElecShutModifier())) {
                float n = 0.8f;
                float n2 = 1.0f;
                switch (SandboxOptions.getInstance().FuelStationGas.getValue()) {
                    case 1: {
                        n2 = (n = 0.0f);
                        break;
                    }
                    case 2: {
                        n = 0.2f;
                        n2 = 0.4f;
                        break;
                    }
                    case 3: {
                        n = 0.3f;
                        n2 = 0.5f;
                        break;
                    }
                    case 4: {
                        n = 0.5f;
                        n2 = 0.7f;
                        break;
                    }
                    case 5: {
                        n = 0.7f;
                        n2 = 0.8f;
                        break;
                    }
                    case 6: {
                        n = 0.8f;
                        n2 = 0.9f;
                        break;
                    }
                    case 7: {
                        n2 = (n = 1.0f);
                        break;
                    }
                }
                final double d = (int)Rand.Next(Integer.parseInt(this.sprite.getProperties().Val("fuelAmount")) * n, Integer.parseInt(this.sprite.getProperties().Val("fuelAmount")) * n2);
                this.getModData().rawset((Object)"fuelAmount", (Object)d);
                this.transmitModData();
                return (int)d;
            }
        }
        return (int)doubleValue;
    }
    
    public void setPipedFuelAmount(int max) {
        max = Math.max(0, max);
        final int pipedFuelAmount = this.getPipedFuelAmount();
        if (max != pipedFuelAmount) {
            if (max == 0 && pipedFuelAmount != 0) {
                max = -1;
            }
            this.getModData().rawset((Object)"fuelAmount", (Object)(double)max);
            this.transmitModData();
        }
    }
    
    private boolean isWaterInfinite() {
        return this.sprite != null && this.square != null && this.square.getRoom() != null && this.sprite.getProperties().Is(IsoFlagType.waterPiped) && GameTime.getInstance().getNightsSurvived() < SandboxOptions.instance.getWaterShutModifier() && (!this.hasModData() || !(this.getModData().rawget((Object)"canBeWaterPiped") instanceof Boolean) || !(boolean)this.getModData().rawget((Object)"canBeWaterPiped"));
    }
    
    private IsoObject checkExternalWaterSource() {
        if (!this.usesExternalWaterSource) {
            return null;
        }
        if (this.externalWaterSource == null || !this.externalWaterSource.hasWater()) {
            this.doFindExternalWaterSource();
        }
        return this.externalWaterSource;
    }
    
    public int getWaterAmount() {
        if (this.sprite == null) {
            return 0;
        }
        if (this.usesExternalWaterSource) {
            if (this.isWaterInfinite()) {
                return 10000;
            }
            final IsoObject checkExternalWaterSource = this.checkExternalWaterSource();
            if (checkExternalWaterSource == null) {
                return 0;
            }
            return checkExternalWaterSource.getWaterAmount();
        }
        else {
            if (this.isWaterInfinite()) {
                return 10000;
            }
            if (this.hasModData() && !this.getModData().isEmpty()) {
                final Object rawget = this.getModData().rawget((Object)"waterAmount");
                if (rawget != null) {
                    if (rawget instanceof Double) {
                        return (int)Math.max(0.0, (double)rawget);
                    }
                    if (rawget instanceof String) {
                        return Math.max(0, Integer.parseInt((String)rawget));
                    }
                    return 0;
                }
            }
            if (this.square != null && !this.square.getProperties().Is(IsoFlagType.water) && this.sprite != null && this.sprite.getProperties().Is(IsoFlagType.solidfloor) && this.square.getPuddlesInGround() > 0.09f) {
                return (int)(this.square.getPuddlesInGround() * 10.0f);
            }
            if (!this.sprite.Properties.Is("waterAmount")) {
                return 0;
            }
            return Integer.parseInt(this.sprite.getProperties().Val("waterAmount"));
        }
    }
    
    public void setWaterAmount(int max) {
        if (!this.usesExternalWaterSource) {
            max = Math.max(0, max);
            final int waterAmount = this.getWaterAmount();
            if (max != waterAmount) {
                int n = 1;
                if (this.hasModData() && !this.getModData().isEmpty()) {
                    n = ((this.getModData().rawget((Object)"waterAmount") == null) ? 1 : 0);
                }
                if (n != 0) {
                    this.getModData().rawset((Object)"waterMax", (Object)(double)waterAmount);
                }
                this.getModData().rawset((Object)"waterAmount", (Object)(double)max);
                if (max <= 0) {
                    this.setTaintedWater(false);
                }
                LuaEventManager.triggerEvent("OnWaterAmountChange", this, waterAmount);
            }
            return;
        }
        if (this.isWaterInfinite()) {
            return;
        }
        final IsoObject checkExternalWaterSource = this.checkExternalWaterSource();
        if (checkExternalWaterSource != null) {
            checkExternalWaterSource.setWaterAmount(max);
        }
    }
    
    public int getWaterMax() {
        if (this.sprite == null) {
            return 0;
        }
        if (this.usesExternalWaterSource) {
            if (this.isWaterInfinite()) {
                return 10000;
            }
            final IsoObject checkExternalWaterSource = this.checkExternalWaterSource();
            if (checkExternalWaterSource != null) {
                return checkExternalWaterSource.getWaterMax();
            }
            return 0;
        }
        else {
            if (this.isWaterInfinite()) {
                return 10000;
            }
            if (this.hasModData() && !this.getModData().isEmpty()) {
                final Object rawget = this.getModData().rawget((Object)"waterMax");
                if (rawget != null) {
                    if (rawget instanceof Double) {
                        return (int)Math.max(0.0, (double)rawget);
                    }
                    if (rawget instanceof String) {
                        return Math.max(0, Integer.parseInt((String)rawget));
                    }
                    return 0;
                }
            }
            if (this.square != null && !this.square.getProperties().Is(IsoFlagType.water) && this.sprite != null && this.sprite.getProperties().Is(IsoFlagType.solidfloor) && this.square.getPuddlesInGround() > 0.09f) {
                return (int)(this.square.getPuddlesInGround() * 10.0f);
            }
            if (this.sprite.Properties.Is("waterMaxAmount")) {
                return Integer.parseInt(this.sprite.getProperties().Val("waterMaxAmount"));
            }
            if (this.sprite.Properties.Is("waterAmount")) {
                return Integer.parseInt(this.sprite.getProperties().Val("waterAmount"));
            }
            return 0;
        }
    }
    
    public int useWater(final int n) {
        if (this.sprite == null) {
            return 0;
        }
        final int waterAmount = this.getWaterAmount();
        int n2;
        if (waterAmount >= n) {
            n2 = n;
        }
        else {
            n2 = waterAmount;
        }
        if (this.square != null && this.sprite != null && this.sprite.getProperties().Is(IsoFlagType.solidfloor) && this.square.getPuddlesInGround() > 0.09f) {
            return n2;
        }
        if (!this.usesExternalWaterSource) {
            if (this.sprite.getProperties().Is(IsoFlagType.water)) {
                return n2;
            }
            if (this.isWaterInfinite()) {
                return n2;
            }
        }
        this.setWaterAmount(waterAmount - n2);
        return n2;
    }
    
    public boolean hasWater() {
        return (this.square != null && this.sprite != null && this.sprite.getProperties().Is(IsoFlagType.solidfloor) && this.square.getPuddlesInGround() > 0.09f) || this.getWaterAmount() > 0;
    }
    
    public boolean isTaintedWater() {
        if (this.square != null && this.sprite != null && this.sprite.getProperties().Is(IsoFlagType.solidfloor) && this.square.getPuddlesInGround() > 0.09f) {
            return true;
        }
        if (this.hasModData()) {
            final Object rawget = this.getModData().rawget((Object)"taintedWater");
            if (rawget instanceof Boolean) {
                return (boolean)rawget;
            }
        }
        return this.sprite != null && this.sprite.getProperties().Is(IsoFlagType.taintedWater);
    }
    
    public void setTaintedWater(final boolean b) {
        this.getModData().rawset((Object)"taintedWater", (Object)b);
    }
    
    public InventoryItem replaceItem(final InventoryItem inventoryItem) {
        String s = null;
        InventoryItem addItem = null;
        if (inventoryItem != null && inventoryItem != null) {
            final String replaceOnUseOn = inventoryItem.getReplaceOnUseOn();
            if (replaceOnUseOn.split("-")[0].trim().contains(this.getObjectName())) {
                final String s2;
                s = (s2 = replaceOnUseOn.split("-")[1]);
                if (!s.contains(".")) {
                    s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, inventoryItem.getModule(), s2);
                }
            }
            else if (replaceOnUseOn.split("-")[0].trim().contains("WaterSource")) {
                final String s3;
                s = (s3 = replaceOnUseOn.split("-")[1]);
                if (!s.contains(".")) {
                    s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, inventoryItem.getModule(), s3);
                }
            }
            else {
                s = null;
            }
        }
        if (s != null && inventoryItem != null) {
            addItem = inventoryItem.getContainer().AddItem(InventoryItemFactory.CreateItem(s));
            if (inventoryItem.getContainer().getParent() instanceof IsoGameCharacter) {
                final IsoGameCharacter isoGameCharacter = (IsoGameCharacter)inventoryItem.getContainer().getParent();
                if (isoGameCharacter.getPrimaryHandItem() == inventoryItem) {
                    isoGameCharacter.setPrimaryHandItem(addItem);
                }
                if (isoGameCharacter.getSecondaryHandItem() == inventoryItem) {
                    isoGameCharacter.setSecondaryHandItem(addItem);
                }
            }
            inventoryItem.getContainer().Remove(inventoryItem);
        }
        return addItem;
    }
    
    public void useItemOn(final InventoryItem o) {
        String s = null;
        if (o != null && o != null) {
            final String replaceOnUseOn = o.getReplaceOnUseOn();
            if (replaceOnUseOn.split("-")[0].trim().contains(this.getObjectName())) {
                final String s2;
                s = (s2 = replaceOnUseOn.split("-")[1]);
                if (!s.contains(".")) {
                    s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, o.getModule(), s2);
                }
            }
            else if (replaceOnUseOn.split("-")[0].trim().contains("WaterSource")) {
                final String s3;
                s = (s3 = replaceOnUseOn.split("-")[1]);
                if (!s.contains(".")) {
                    s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, o.getModule(), s3);
                }
                this.useWater(10);
            }
            else {
                s = null;
            }
        }
        if (s != null && o != null) {
            o.getContainer().AddItem(InventoryItemFactory.CreateItem(s));
            o.setUses(o.getUses() - 1);
            if (o.getUses() <= 0 && o.getContainer() != null) {
                o.getContainer().Items.remove(o);
            }
        }
    }
    
    public float getX() {
        return (float)this.square.getX();
    }
    
    public float getY() {
        return (float)this.square.getY();
    }
    
    public float getZ() {
        return (float)this.square.getZ();
    }
    
    public boolean onMouseLeftClick(final int n, final int n2) {
        return false;
    }
    
    public PropertyContainer getProperties() {
        if (this.sprite == null) {
            return null;
        }
        return this.sprite.getProperties();
    }
    
    public void RemoveAttachedAnims() {
        if (this.AttachedAnimSprite == null) {
            return;
        }
        for (int i = 0; i < this.AttachedAnimSprite.size(); ++i) {
            this.AttachedAnimSprite.get(i).Dispose();
        }
        this.AttachedAnimSprite.clear();
    }
    
    public void RemoveAttachedAnim(final int n) {
        if (this.AttachedAnimSprite == null) {
            return;
        }
        if (n < 0 || n >= this.AttachedAnimSprite.size()) {
            return;
        }
        this.AttachedAnimSprite.get(n).Dispose();
        this.AttachedAnimSprite.remove(n);
    }
    
    public Vector2 getFacingPosition(final Vector2 vector2) {
        if (this.square == null) {
            return vector2.set(0.0f, 0.0f);
        }
        final PropertyContainer properties = this.getProperties();
        if (properties != null) {
            if (this.getType() == IsoObjectType.wall) {
                if (properties.Is(IsoFlagType.collideN) && properties.Is(IsoFlagType.collideW)) {
                    return vector2.set(this.getX(), this.getY());
                }
                if (properties.Is(IsoFlagType.collideN)) {
                    return vector2.set(this.getX() + 0.5f, this.getY());
                }
                if (properties.Is(IsoFlagType.collideW)) {
                    return vector2.set(this.getX(), this.getY() + 0.5f);
                }
                if (properties.Is(IsoFlagType.DoorWallN)) {
                    return vector2.set(this.getX() + 0.5f, this.getY());
                }
                if (properties.Is(IsoFlagType.DoorWallW)) {
                    return vector2.set(this.getX(), this.getY() + 0.5f);
                }
            }
            else {
                if (properties.Is(IsoFlagType.attachedN)) {
                    return vector2.set(this.getX() + 0.5f, this.getY());
                }
                if (properties.Is(IsoFlagType.attachedS)) {
                    return vector2.set(this.getX() + 0.5f, this.getY() + 1.0f);
                }
                if (properties.Is(IsoFlagType.attachedW)) {
                    return vector2.set(this.getX(), this.getY() + 0.5f);
                }
                if (properties.Is(IsoFlagType.attachedE)) {
                    return vector2.set(this.getX() + 1.0f, this.getY() + 0.5f);
                }
            }
        }
        return vector2.set(this.getX() + 0.5f, this.getY() + 0.5f);
    }
    
    public Vector2 getFacingPositionAlt(final Vector2 vector2) {
        return this.getFacingPosition(vector2);
    }
    
    public float getRenderYOffset() {
        return this.renderYOffset;
    }
    
    public void setRenderYOffset(final float renderYOffset) {
        this.renderYOffset = renderYOffset;
        this.sx = 0.0f;
    }
    
    public boolean isTableSurface() {
        final PropertyContainer properties = this.getProperties();
        return properties != null && properties.isTable();
    }
    
    public boolean isTableTopObject() {
        final PropertyContainer properties = this.getProperties();
        return properties != null && properties.isTableTop();
    }
    
    public boolean getIsSurfaceNormalOffset() {
        final PropertyContainer properties = this.getProperties();
        return properties != null && properties.isSurfaceOffset();
    }
    
    public float getSurfaceNormalOffset() {
        float n = 0.0f;
        final PropertyContainer properties = this.getProperties();
        if (properties.isSurfaceOffset()) {
            n = (float)properties.getSurface();
        }
        return n;
    }
    
    public float getSurfaceOffsetNoTable() {
        float n = 0.0f;
        int int1 = 0;
        final PropertyContainer properties = this.getProperties();
        if (properties != null) {
            n = (float)properties.getSurface();
            if (!StringUtils.isNullOrEmpty(properties.Val("ItemHeight"))) {
                int1 = Integer.parseInt(properties.Val("ItemHeight"));
            }
        }
        return n + this.getRenderYOffset() + int1;
    }
    
    public float getSurfaceOffset() {
        float n = 0.0f;
        if (this.isTableSurface()) {
            final PropertyContainer properties = this.getProperties();
            if (properties != null) {
                n = (float)properties.getSurface();
            }
        }
        return n;
    }
    
    public boolean isStairsNorth() {
        return this.getType() == IsoObjectType.stairsTN || this.getType() == IsoObjectType.stairsMN || this.getType() == IsoObjectType.stairsBN;
    }
    
    public boolean isStairsWest() {
        return this.getType() == IsoObjectType.stairsTW || this.getType() == IsoObjectType.stairsMW || this.getType() == IsoObjectType.stairsBW;
    }
    
    public boolean isStairsObject() {
        return this.isStairsNorth() || this.isStairsWest();
    }
    
    public boolean isHoppable() {
        return this.sprite != null && (this.sprite.getProperties().Is(IsoFlagType.HoppableN) || this.sprite.getProperties().Is(IsoFlagType.HoppableW));
    }
    
    public boolean isNorthHoppable() {
        return this.sprite != null && this.isHoppable() && this.sprite.getProperties().Is(IsoFlagType.HoppableN);
    }
    
    public boolean haveSheetRope() {
        return IsoWindow.isTopOfSheetRopeHere(this.square, this.isNorthHoppable());
    }
    
    public int countAddSheetRope() {
        return IsoWindow.countAddSheetRope(this.square, this.isNorthHoppable());
    }
    
    public boolean canAddSheetRope() {
        return IsoWindow.canAddSheetRope(this.square, this.isNorthHoppable());
    }
    
    public boolean addSheetRope(final IsoPlayer isoPlayer, final String s) {
        return this.canAddSheetRope() && IsoWindow.addSheetRope(isoPlayer, this.square, this.isNorthHoppable(), s);
    }
    
    public boolean removeSheetRope(final IsoPlayer isoPlayer) {
        return this.haveSheetRope() && IsoWindow.removeSheetRope(isoPlayer, this.square, this.isNorthHoppable());
    }
    
    public void render(final float n, final float n2, final float n3, final ColorInfo colorInfo, final boolean b, final boolean b2, final Shader shader) {
        if (this.isSpriteInvisible()) {
            return;
        }
        this.prepareToRender(colorInfo);
        final int playerIndex = IsoCamera.frameState.playerIndex;
        if (this.shouldDrawMainSprite()) {
            this.sprite.render(this, n, n2, n3, this.dir, this.offsetX, this.offsetY + this.renderYOffset * Core.TileScale, IsoObject.stCol, !this.isBlink());
            if (this.isOutlineHighlight(playerIndex) && !this.isOutlineHlAttached(playerIndex) && OutlineShader.instance.StartShader()) {
                final int n4 = this.outlineHighlightCol[playerIndex];
                OutlineShader.instance.setOutlineColor(Color.getRedChannelFromABGR(n4), Color.getGreenChannelFromABGR(n4), Color.getBlueChannelFromABGR(n4), this.isOutlineHlBlink(playerIndex) ? Core.blinkAlpha : 1.0f);
                final Texture textureForCurrentFrame = this.sprite.getTextureForCurrentFrame(this.dir);
                if (textureForCurrentFrame != null) {
                    OutlineShader.instance.setStepSize(this.outlineThickness, textureForCurrentFrame.getWidth(), textureForCurrentFrame.getHeight());
                }
                this.sprite.render(this, n, n2, n3, this.dir, this.offsetX, this.offsetY + this.renderYOffset * Core.TileScale, IsoObject.stCol, !this.isBlink());
                IndieGL.EndShader();
            }
        }
        this.renderAttachedAndOverlaySpritesInternal(n, n2, n3, colorInfo, b, b2, shader, null);
        if (this.isOutlineHighlight(playerIndex) && this.isOutlineHlAttached(playerIndex) && OutlineShader.instance.StartShader()) {
            final int n5 = this.outlineHighlightCol[playerIndex];
            OutlineShader.instance.setOutlineColor(Color.getRedChannelFromABGR(n5), Color.getGreenChannelFromABGR(n5), Color.getBlueChannelFromABGR(n5), this.isOutlineHlBlink(playerIndex) ? Core.blinkAlpha : 1.0f);
            final Texture textureForCurrentFrame2 = this.sprite.getTextureForCurrentFrame(this.dir);
            if (textureForCurrentFrame2 != null) {
                OutlineShader.instance.setStepSize(this.outlineThickness, textureForCurrentFrame2.getWidth(), textureForCurrentFrame2.getHeight());
            }
            if (this.shouldDrawMainSprite()) {
                this.sprite.render(this, n, n2, n3, this.dir, this.offsetX, this.offsetY + this.renderYOffset * Core.TileScale, IsoObject.stCol, !this.isBlink());
            }
            this.renderAttachedAndOverlaySpritesInternal(n, n2, n3, colorInfo, b, b2, shader, null);
            IndieGL.EndShader();
        }
        if (!this.bAlphaForced && this.isUpdateAlphaDuringRender()) {
            this.updateAlpha(playerIndex);
        }
    }
    
    public void renderFloorTile(final float n, final float n2, final float n3, final ColorInfo colorInfo, final boolean b, final boolean b2, final Shader shader, final Consumer<TextureDraw> consumer, final Consumer<TextureDraw> consumer2) {
        if (this.isSpriteInvisible()) {
            return;
        }
        this.prepareToRender(colorInfo);
        final FloorShaper floorShaper = Type.tryCastTo(consumer, FloorShaper.class);
        final FloorShaper floorShaper2 = Type.tryCastTo(consumer2, FloorShaper.class);
        if ((floorShaper != null || floorShaper2 != null) && this.isHighlighted() && this.getHighlightColor() != null) {
            final ColorInfo highlightColor = this.getHighlightColor();
            final int colorToABGR = Color.colorToABGR(highlightColor.r, highlightColor.g, highlightColor.b, highlightColor.a * (this.isBlink() ? Core.blinkAlpha : 1.0f));
            if (floorShaper != null) {
                floorShaper.setTintColor(colorToABGR);
            }
            if (floorShaper2 != null) {
                floorShaper2.setTintColor(colorToABGR);
            }
        }
        if (this.shouldDrawMainSprite()) {
            IndieGL.shaderSetValue(shader, "floorLayer", 0);
            this.sprite.render(this, n, n2, n3, this.dir, this.offsetX, this.offsetY + this.renderYOffset * Core.TileScale, IsoObject.stCol, !this.isBlink(), consumer);
        }
        this.renderAttachedAndOverlaySpritesInternal(n, n2, n3, colorInfo, b, b2, shader, consumer2);
        if (floorShaper != null) {
            floorShaper.setTintColor(0);
        }
        if (floorShaper2 != null) {
            floorShaper2.setTintColor(0);
        }
    }
    
    public void renderWallTile(final float n, final float n2, final float n3, final ColorInfo colorInfo, final boolean b, final boolean b2, final Shader shader, final Consumer<TextureDraw> consumer) {
        if (this.isSpriteInvisible()) {
            return;
        }
        this.renderWallTileOnly(n, n2, n3, colorInfo, shader, consumer);
        this.renderAttachedAndOverlaySpritesInternal(n, n2, n3, colorInfo, b, b2, shader, consumer);
        final int playerIndex = IsoCamera.frameState.playerIndex;
        if (this.isOutlineHighlight(playerIndex) && !this.isOutlineHlAttached(playerIndex) && OutlineShader.instance.StartShader()) {
            final int n4 = this.outlineHighlightCol[playerIndex];
            OutlineShader.instance.setOutlineColor(Color.getRedChannelFromABGR(n4), Color.getGreenChannelFromABGR(n4), Color.getBlueChannelFromABGR(n4), this.isOutlineHlBlink(playerIndex) ? Core.blinkAlpha : 1.0f);
            final Texture textureForCurrentFrame = this.sprite.getTextureForCurrentFrame(this.dir);
            if (textureForCurrentFrame != null) {
                OutlineShader.instance.setStepSize(this.outlineThickness, textureForCurrentFrame.getWidth(), textureForCurrentFrame.getHeight());
            }
            this.sprite.render(this, n, n2, n3, this.dir, this.offsetX, this.offsetY + this.renderYOffset * Core.TileScale, IsoObject.stCol, !this.isBlink());
            IndieGL.EndShader();
        }
    }
    
    public void renderWallTileOnly(final float n, final float n2, final float n3, final ColorInfo colorInfo, final Shader shader, final Consumer<TextureDraw> consumer) {
        if (this.isSpriteInvisible()) {
            return;
        }
        this.prepareToRender(colorInfo);
        final WallShaper wallShaper = Type.tryCastTo(consumer, WallShaper.class);
        if (wallShaper != null && this.isHighlighted() && this.getHighlightColor() != null) {
            final ColorInfo highlightColor = this.getHighlightColor();
            wallShaper.setTintColor(Color.colorToABGR(highlightColor.r, highlightColor.g, highlightColor.b, highlightColor.a * (this.isBlink() ? Core.blinkAlpha : 1.0f)));
        }
        if (this.shouldDrawMainSprite()) {
            IndieGL.pushShader(shader);
            this.sprite.render(this, n, n2, n3, this.dir, this.offsetX, this.offsetY + this.renderYOffset * Core.TileScale, IsoObject.stCol, !this.isBlink(), consumer);
            IndieGL.popShader(shader);
        }
        if (wallShaper != null) {
            wallShaper.setTintColor(0);
        }
    }
    
    private boolean shouldDrawMainSprite() {
        return this.sprite != null && DebugOptions.instance.Terrain.RenderTiles.RenderSprites.getValue();
    }
    
    public void renderAttachedAndOverlaySprites(final float n, final float n2, final float n3, final ColorInfo colorInfo, final boolean b, final boolean b2, final Shader shader, final Consumer<TextureDraw> consumer) {
        if (this.isSpriteInvisible()) {
            return;
        }
        this.renderAttachedAndOverlaySpritesInternal(n, n2, n3, colorInfo, b, b2, shader, consumer);
    }
    
    private void renderAttachedAndOverlaySpritesInternal(final float n, final float n2, final float n3, ColorInfo stCol, final boolean b, final boolean b2, final Shader shader, final Consumer<TextureDraw> consumer) {
        if (this.isHighlighted()) {
            stCol = IsoObject.stCol;
        }
        this.renderOverlaySprites(n, n2, n3, stCol);
        if (b) {
            this.renderAttachedSprites(n, n2, n3, stCol, b2, shader, consumer);
        }
    }
    
    private void prepareToRender(final ColorInfo colorInfo) {
        IsoObject.stCol.set(colorInfo);
        if (this.isHighlighted()) {
            IsoObject.stCol.set(this.getHighlightColor());
            if (this.isBlink()) {
                IsoObject.stCol.a = Core.blinkAlpha;
            }
            else {
                IsoObject.stCol.a = 1.0f;
            }
            IsoObject.stCol.r = colorInfo.r * (1.0f - IsoObject.stCol.a) + this.getHighlightColor().r * IsoObject.stCol.a;
            IsoObject.stCol.g = colorInfo.g * (1.0f - IsoObject.stCol.a) + this.getHighlightColor().g * IsoObject.stCol.a;
            IsoObject.stCol.b = colorInfo.b * (1.0f - IsoObject.stCol.a) + this.getHighlightColor().b * IsoObject.stCol.a;
            IsoObject.stCol.a = colorInfo.a;
        }
        if (this.customColor != null) {
            final float n = (this.square != null) ? this.square.getDarkMulti(IsoPlayer.getPlayerIndex()) : 1.0f;
            IsoObject.stCol.r = this.customColor.r * n;
            IsoObject.stCol.g = this.customColor.g * n;
            IsoObject.stCol.b = this.customColor.b * n;
        }
        if (this.sprite != null && this.sprite.forceAmbient) {
            final float n2 = IsoObject.rmod * this.tintr;
            final float n3 = IsoObject.gmod * this.tintg;
            final float n4 = IsoObject.bmod * this.tintb;
            if (!this.isHighlighted()) {
                IsoObject.stCol.r = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * n2;
                IsoObject.stCol.g = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * n3;
                IsoObject.stCol.b = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * n4;
            }
        }
        final int playerIndex = IsoPlayer.getPlayerIndex();
        float camCharacterX = IsoCamera.frameState.CamCharacterX;
        float camCharacterY = IsoCamera.frameState.CamCharacterY;
        final float camCharacterZ = IsoCamera.frameState.CamCharacterZ;
        if (IsoWorld.instance.CurrentCell.IsPlayerWindowPeeking(playerIndex)) {
            final IsoDirections fromAngle = IsoDirections.fromAngle(IsoPlayer.players[playerIndex].getForwardDirection());
            if (fromAngle == IsoDirections.N || fromAngle == IsoDirections.NW) {
                --camCharacterY;
            }
            if (fromAngle == IsoDirections.W || fromAngle == IsoDirections.NW) {
                --camCharacterX;
            }
        }
        if (this == IsoCamera.CamCharacter) {
            this.setAlphaAndTarget(playerIndex, 1.0f);
        }
        IsoObject.lastRenderedRendered = IsoObject.lastRendered;
        IsoObject.lastRendered = this;
        if (this.sprite != null && !(this instanceof IsoPhysicsObject) && IsoCamera.CamCharacter != null) {
            boolean b = this instanceof IsoWindow || this.sprite.getType() == IsoObjectType.doorW || this.sprite.getType() == IsoObjectType.doorN;
            if (this.sprite.getProperties().Is("GarageDoor")) {
                b = false;
            }
            if (!b && (this.square.getX() > camCharacterX || this.square.getY() > camCharacterY) && (int)camCharacterZ <= this.square.getZ()) {
                boolean b2 = false;
                float n5 = 0.2f;
                int n6 = ((this.sprite.cutW || this.sprite.getProperties().Is(IsoFlagType.doorW)) && this.square.getX() > camCharacterX) ? 1 : 0;
                final boolean b3 = (this.sprite.cutN || this.sprite.getProperties().Is(IsoFlagType.doorN)) && this.square.getY() > camCharacterY;
                if (n6 != 0 && this.square.getProperties().Is(IsoFlagType.WallSE) && this.square.getY() <= camCharacterY) {
                    n6 = 0;
                }
                if (n6 != 0 || b3) {
                    b2 = true;
                }
                else if ((this.getType() == IsoObjectType.WestRoofB || this.getType() == IsoObjectType.WestRoofM || this.getType() == IsoObjectType.WestRoofT) && (int)camCharacterZ == this.square.getZ() && this.square.getBuilding() == null && IsoWorld.instance.CurrentCell.CanBuildingSquareOccludePlayer(this.square, playerIndex)) {
                    b2 = true;
                    n5 = 0.05f;
                }
                if (this.sprite.getProperties().Is(IsoFlagType.halfheight)) {
                    b2 = false;
                }
                if (b2) {
                    if (b3 && this.sprite.getProperties().Is(IsoFlagType.HoppableN)) {
                        n5 = 0.25f;
                    }
                    if (n6 != 0 && this.sprite.getProperties().Is(IsoFlagType.HoppableW)) {
                        n5 = 0.25f;
                    }
                    if (this.bAlphaForced) {
                        if (this.getTargetAlpha(playerIndex) == 1.0f) {
                            this.setAlphaAndTarget(playerIndex, 0.99f);
                        }
                    }
                    else {
                        this.setTargetAlpha(playerIndex, n5);
                    }
                    IsoObject.LowLightingQualityHack = true;
                    this.NoPicking = (this.rerouteMask == null && !(this instanceof IsoThumpable) && !IsoWindowFrame.isWindowFrame(this) && !this.sprite.getProperties().Is(IsoFlagType.doorN) && !this.sprite.getProperties().Is(IsoFlagType.doorW));
                }
                else {
                    this.NoPicking = false;
                }
            }
            else {
                this.NoPicking = false;
            }
        }
        if (this == IsoCamera.CamCharacter) {
            this.setTargetAlpha(playerIndex, 1.0f);
        }
    }
    
    protected float getAlphaUpdateRateDiv() {
        return 14.0f;
    }
    
    protected float getAlphaUpdateRateMul() {
        float n = 0.25f;
        if (this.square != null && this.square.room != null) {
            n *= 2.0f;
        }
        return n;
    }
    
    protected boolean isUpdateAlphaEnabled() {
        return true;
    }
    
    protected boolean isUpdateAlphaDuringRender() {
        return true;
    }
    
    protected final void updateAlpha() {
        if (GameServer.bServer) {
            return;
        }
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            if (IsoPlayer.players[i] != null) {
                this.updateAlpha(i);
            }
        }
    }
    
    protected final void updateAlpha(final int n) {
        if (GameServer.bServer) {
            return;
        }
        this.updateAlpha(n, this.getAlphaUpdateRateMul(), this.getAlphaUpdateRateDiv());
    }
    
    protected void updateAlpha(final int alphaToTarget, float n, float n2) {
        if (!this.isUpdateAlphaEnabled()) {
            return;
        }
        if (!DebugOptions.instance.Character.Debug.UpdateAlpha.getValue()) {
            this.setAlphaToTarget(alphaToTarget);
            return;
        }
        if (this.bNeverDoneAlpha) {
            this.setAlpha(0.0f);
            this.bNeverDoneAlpha = false;
        }
        if (DebugOptions.instance.Character.Debug.UpdateAlphaEighthSpeed.getValue()) {
            n /= 8.0f;
            n2 *= 8.0f;
        }
        final float n3 = GameTime.getInstance().getMultiplier() * 0.28f;
        float alpha = this.getAlpha(alphaToTarget);
        final float n4 = this.targetAlpha[alphaToTarget];
        if (alpha < n4) {
            alpha += n3 * n;
            if (alpha > n4) {
                alpha = n4;
            }
        }
        else if (alpha > n4) {
            alpha -= n3 / n2;
            if (alpha < n4) {
                alpha = n4;
            }
        }
        this.setAlpha(alphaToTarget, alpha);
    }
    
    private void renderOverlaySprites(final float n, final float n2, final float n3, final ColorInfo colorInfo) {
        if (this.getOverlaySprite() == null || !DebugOptions.instance.Terrain.RenderTiles.OverlaySprites.getValue()) {
            return;
        }
        final ColorInfo stCol2 = IsoObject.stCol2;
        stCol2.set(colorInfo);
        if (this.overlaySpriteColor != null) {
            stCol2.set(this.overlaySpriteColor);
        }
        if (stCol2.a != 1.0f && this.overlaySprite.def != null && this.overlaySprite.def.bCopyTargetAlpha) {
            final int playerIndex = IsoPlayer.getPlayerIndex();
            final float n4 = this.alpha[playerIndex];
            final float[] alpha = this.alpha;
            final int n5 = playerIndex;
            alpha[n5] *= stCol2.a;
            this.getOverlaySprite().render(this, n, n2, n3, this.dir, this.offsetX, this.offsetY + this.renderYOffset * Core.TileScale, stCol2, true);
            this.alpha[playerIndex] = n4;
        }
        else {
            this.getOverlaySprite().render(this, n, n2, n3, this.dir, this.offsetX, this.offsetY + this.renderYOffset * Core.TileScale, stCol2, true);
        }
    }
    
    private void renderAttachedSprites(final float n, final float n2, final float n3, final ColorInfo colorInfo, final boolean b, final Shader shader, final Consumer<TextureDraw> consumer) {
        if (this.AttachedAnimSprite != null && DebugOptions.instance.Terrain.RenderTiles.AttachedAnimSprites.getValue()) {
            for (int size = this.AttachedAnimSprite.size(), i = 0; i < size; ++i) {
                final IsoSpriteInstance isoSpriteInstance = this.AttachedAnimSprite.get(i);
                if (!b || !isoSpriteInstance.parentSprite.Properties.Is(IsoFlagType.NoWallLighting)) {
                    final float a = colorInfo.a;
                    IndieGL.shaderSetValue(shader, "floorLayer", 1);
                    colorInfo.a = isoSpriteInstance.alpha;
                    WallShaper wallShaper;
                    if ((wallShaper = (WallShaper)consumer) == WallShaperW.instance) {
                        if (isoSpriteInstance.parentSprite.getProperties().Is(IsoFlagType.attachedN)) {
                            final Texture textureForCurrentFrame = isoSpriteInstance.parentSprite.getTextureForCurrentFrame(this.dir);
                            if (textureForCurrentFrame != null && textureForCurrentFrame.getWidth() < 32 * Core.TileScale) {
                                continue;
                            }
                        }
                        if (isoSpriteInstance.parentSprite.getProperties().Is(IsoFlagType.attachedW)) {
                            wallShaper = WallShaperWhole.instance;
                        }
                    }
                    else if (consumer == WallShaperN.instance) {
                        if (isoSpriteInstance.parentSprite.getProperties().Is(IsoFlagType.attachedW)) {
                            continue;
                        }
                        if (isoSpriteInstance.parentSprite.getProperties().Is(IsoFlagType.attachedN)) {
                            wallShaper = WallShaperWhole.instance;
                        }
                    }
                    isoSpriteInstance.parentSprite.render(isoSpriteInstance, this, n, n2, n3, this.dir, this.offsetX, this.offsetY + this.renderYOffset * Core.TileScale, colorInfo, true, wallShaper);
                    colorInfo.a = a;
                    isoSpriteInstance.update();
                }
            }
        }
        if (this.Children != null && DebugOptions.instance.Terrain.RenderTiles.AttachedChildren.getValue()) {
            for (int size2 = this.Children.size(), j = 0; j < size2; ++j) {
                final IsoObject isoObject = this.Children.get(j);
                if (isoObject instanceof IsoMovingObject) {
                    IndieGL.shaderSetValue(shader, "floorLayer", 1);
                    isoObject.render(((IsoMovingObject)isoObject).x, ((IsoMovingObject)isoObject).y, ((IsoMovingObject)isoObject).z, colorInfo, true, false, null);
                }
            }
        }
        if (this.wallBloodSplats != null && DebugOptions.instance.Terrain.RenderTiles.AttachedWallBloodSplats.getValue()) {
            if (Core.OptionBloodDecals == 0) {
                return;
            }
            IndieGL.shaderSetValue(shader, "floorLayer", 0);
            for (int k = 0; k < this.wallBloodSplats.size(); ++k) {
                this.wallBloodSplats.get(k).render(n, n2, n3, colorInfo);
            }
        }
    }
    
    public boolean isSpriteInvisible() {
        return this.sprite != null && this.sprite.getProperties().Is(IsoFlagType.invisible);
    }
    
    public void renderFxMask(final float n, final float n2, final float n3, final boolean b) {
        if (this.sprite != null) {
            if (this.getType() == IsoObjectType.wall) {}
            this.sprite.render(this, n, n2, n3, this.dir, this.offsetX, this.offsetY + this.renderYOffset * Core.TileScale, IsoObject.colFxMask, false);
        }
        if (this.getOverlaySprite() != null) {
            this.getOverlaySprite().render(this, n, n2, n3, this.dir, this.offsetX, this.offsetY + this.renderYOffset * Core.TileScale, IsoObject.colFxMask, false);
        }
        if (b) {
            if (this.AttachedAnimSprite != null) {
                for (int size = this.AttachedAnimSprite.size(), i = 0; i < size; ++i) {
                    this.AttachedAnimSprite.get(i).render(this, n, n2, n3, this.dir, this.offsetX, this.offsetY + this.renderYOffset * Core.TileScale, IsoObject.colFxMask);
                }
            }
            if (this.Children != null) {
                for (int size2 = this.Children.size(), j = 0; j < size2; ++j) {
                    final IsoObject isoObject = this.Children.get(j);
                    if (isoObject instanceof IsoMovingObject) {
                        isoObject.render(((IsoMovingObject)isoObject).x, ((IsoMovingObject)isoObject).y, ((IsoMovingObject)isoObject).z, IsoObject.colFxMask, b, false, null);
                    }
                }
            }
            if (this.wallBloodSplats != null) {
                if (Core.OptionBloodDecals == 0) {
                    return;
                }
                for (int k = 0; k < this.wallBloodSplats.size(); ++k) {
                    this.wallBloodSplats.get(k).render(n, n2, n3, IsoObject.colFxMask);
                }
            }
        }
    }
    
    public void renderObjectPicker(final float n, final float n2, final float n3, final ColorInfo colorInfo) {
        if (this.sprite == null) {
            return;
        }
        if (this.sprite.getProperties().Is(IsoFlagType.invisible)) {
            return;
        }
        this.sprite.renderObjectPicker(this.sprite.def, this, this.dir);
    }
    
    public boolean TestPathfindCollide(final IsoMovingObject isoMovingObject, final IsoGridSquare isoGridSquare, final IsoGridSquare isoGridSquare2) {
        return false;
    }
    
    public boolean TestCollide(final IsoMovingObject isoMovingObject, final IsoGridSquare isoGridSquare, final IsoGridSquare isoGridSquare2) {
        return false;
    }
    
    public VisionResult TestVision(final IsoGridSquare isoGridSquare, final IsoGridSquare isoGridSquare2) {
        return VisionResult.Unblocked;
    }
    
    Texture getCurrentFrameTex() {
        if (this.sprite == null) {
            return null;
        }
        if (this.sprite.CurrentAnim == null) {
            return null;
        }
        if (this.sprite.CurrentAnim.Frames.size() <= this.sprite.def.Frame) {
            return null;
        }
        return this.sprite.CurrentAnim.Frames.get((int)this.sprite.def.Frame).getTexture(this.dir);
    }
    
    public boolean isMaskClicked(final int n, final int n2) {
        return this.sprite != null && this.sprite.isMaskClicked(this.dir, n, n2);
    }
    
    public boolean isMaskClicked(final int n, final int n2, final boolean b) {
        return this.sprite != null && ((this.overlaySprite != null && this.overlaySprite.isMaskClicked(this.dir, n, n2, b)) || this.sprite.isMaskClicked(this.dir, n, n2, b));
    }
    
    public float getMaskClickedY(final int n, final int n2, final boolean b) {
        if (this.sprite == null) {
            return 10000.0f;
        }
        return this.sprite.getMaskClickedY(this.dir, n, n2, b);
    }
    
    public ColorInfo getCustomColor() {
        return this.customColor;
    }
    
    public void setCustomColor(final ColorInfo customColor) {
        this.customColor = customColor;
    }
    
    public void setCustomColor(final float n, final float n2, final float n3, final float n4) {
        this.customColor = new ColorInfo(n, n2, n3, n4);
    }
    
    public void loadFromRemoteBuffer(final ByteBuffer byteBuffer) {
        this.loadFromRemoteBuffer(byteBuffer, true);
    }
    
    public void loadFromRemoteBuffer(final ByteBuffer byteBuffer, final boolean b) {
        try {
            this.load(byteBuffer, 186);
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
        if (this instanceof IsoWorldInventoryObject && ((IsoWorldInventoryObject)this).getItem() == null) {
            DebugLog.log("loadFromRemoteBuffer() failed due to an unknown item type");
            return;
        }
        final int int1 = byteBuffer.getInt();
        final int int2 = byteBuffer.getInt();
        final int int3 = byteBuffer.getInt();
        final int int4 = byteBuffer.getInt();
        final boolean b2 = byteBuffer.get() != 0;
        final boolean b3 = byteBuffer.get() != 0;
        IsoWorld.instance.CurrentCell.EnsureSurroundNotNull(int1, int2, int3);
        this.square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
        if (this.square == null) {
            return;
        }
        if (GameServer.bServer && !(this instanceof IsoWorldInventoryObject)) {
            IsoRegions.setPreviousFlags(this.square);
        }
        if (b2) {
            this.square.getSpecialObjects().add(this);
        }
        if (b3 && this instanceof IsoWorldInventoryObject) {
            this.square.getWorldObjects().add((IsoWorldInventoryObject)this);
            this.square.chunk.recalcHashCodeObjects();
        }
        if (b) {
            if (int4 != -1 && int4 >= 0 && int4 <= this.square.getObjects().size()) {
                this.square.getObjects().add(int4, this);
            }
            else {
                this.square.getObjects().add(this);
            }
        }
        for (int i = 0; i < this.getContainerCount(); ++i) {
            final ItemContainer containerByIndex = this.getContainerByIndex(i);
            containerByIndex.parent = this;
            containerByIndex.parent.square = this.square;
            containerByIndex.SourceGrid = this.square;
        }
        for (int j = -1; j <= 1; ++j) {
            for (int k = -1; k <= 1; ++k) {
                final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(j + int1, k + int2, int3);
                if (gridSquare != null) {
                    gridSquare.RecalcAllWithNeighbours(true);
                }
            }
        }
    }
    
    public void addToWorld() {
        for (int i = 0; i < this.getContainerCount(); ++i) {
            this.getContainerByIndex(i).addItemsToProcessItems();
        }
        if (GameServer.bServer) {
            return;
        }
        String s = null;
        final ItemContainer containerByEitherType = this.getContainerByEitherType("fridge", "freezer");
        if (containerByEitherType != null && containerByEitherType.isPowered()) {
            s = "FridgeHum";
            IsoWorld.instance.getCell().addToProcessIsoObject(this);
        }
        else if (this.sprite != null && this.sprite.getProperties().Is(IsoFlagType.waterPiped) && this.getWaterAmount() > 0.0f && Rand.Next(15) == 0) {
            s = "WaterDrip";
        }
        else if (this instanceof IsoWindow && Rand.Next(15) == 0) {
            s = "WindowWind";
        }
        else if (this instanceof IsoWindow && Rand.Next(15) == 0) {
            s = "WindowRattle";
        }
        else if (this instanceof IsoDoor && Rand.Next(15) == 0) {
            s = "WoodDoorCreaks";
        }
        else if (this instanceof IsoTree && Rand.Next(40) == 0) {
            s = "TreeAmbiance";
        }
        if (s != null) {
            this.emitter = IsoWorld.instance.getFreeEmitter(this.getX() + 0.5f, this.getY() + 0.5f, (float)(int)this.getZ());
            IsoWorld.instance.setEmitterOwner(this.emitter, this);
            this.emitter.randomStart();
            this.emitter.playAmbientLoopedImpl(s);
        }
        if (this instanceof IsoTree && this.emitter != null) {
            if (this.emitter instanceof FMODSoundEmitter) {
                ((FMODSoundEmitter)this.emitter).addParameter((FMODParameter)new ParameterCurrentZone(this));
            }
            this.emitter.playAmbientLoopedImpl("BirdInTree");
        }
        this.checkMoveWithWind();
    }
    
    public void removeFromWorld() {
        final IsoCell cell = this.getCell();
        cell.addToProcessIsoObjectRemove(this);
        cell.getStaticUpdaterObjectList().remove(this);
        for (int i = 0; i < this.getContainerCount(); ++i) {
            this.getContainerByIndex(i).removeItemsFromProcessItems();
        }
        if (this.emitter != null) {
            this.emitter.stopAll();
            this.emitter = null;
        }
    }
    
    public void reuseGridSquare() {
    }
    
    public void removeFromSquare() {
        if (this.square != null) {
            this.square.getObjects().remove(this);
            this.square.getSpecialObjects().remove(this);
        }
    }
    
    public void transmitCustomColor() {
        if (GameClient.bClient && this.getCustomColor() != null) {
            GameClient.instance.sendCustomColor(this);
        }
    }
    
    public void transmitCompleteItemToClients() {
        if (GameServer.bServer) {
            if (GameServer.udpEngine == null) {
                return;
            }
            if (SystemDisabler.doWorldSyncEnable) {
                for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                    final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
                    if (udpConnection.RelevantTo((float)this.square.x, (float)this.square.y)) {
                        GameServer.SyncObjectChunkHashes(this.square.chunk, udpConnection);
                    }
                }
                return;
            }
            for (int j = 0; j < GameServer.udpEngine.connections.size(); ++j) {
                final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(j);
                if (udpConnection2 != null) {
                    if (this.square != null) {
                        if (udpConnection2.RelevantTo((float)this.square.x, (float)this.square.y)) {
                            final ByteBufferWriter startPacket = udpConnection2.startPacket();
                            PacketTypes.PacketType.AddItemToMap.doPacket(startPacket);
                            this.writeToRemoteBuffer(startPacket);
                            PacketTypes.PacketType.AddItemToMap.send(udpConnection2);
                        }
                    }
                }
            }
        }
    }
    
    public void transmitUpdatedSpriteToClients(final UdpConnection udpConnection) {
        if (GameServer.bServer) {
            for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
                if (udpConnection2 != null) {
                    if (this.square != null) {
                        if ((udpConnection == null || udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) && udpConnection2.RelevantTo((float)this.square.x, (float)this.square.y)) {
                            final ByteBufferWriter startPacket = udpConnection2.startPacket();
                            PacketTypes.PacketType.UpdateItemSprite.doPacket(startPacket);
                            startPacket.putInt(this.getSprite().ID);
                            GameWindow.WriteStringUTF(startPacket.bb, this.spriteName);
                            startPacket.putInt(this.getSquare().getX());
                            startPacket.putInt(this.getSquare().getY());
                            startPacket.putInt(this.getSquare().getZ());
                            startPacket.putInt(this.getSquare().getObjects().indexOf(this));
                            if (this.AttachedAnimSprite != null) {
                                startPacket.putByte((byte)this.AttachedAnimSprite.size());
                                for (int j = 0; j < this.AttachedAnimSprite.size(); ++j) {
                                    startPacket.putInt(this.AttachedAnimSprite.get(j).parentSprite.ID);
                                }
                            }
                            else {
                                startPacket.putByte((byte)0);
                            }
                            PacketTypes.PacketType.UpdateItemSprite.send(udpConnection2);
                        }
                    }
                }
            }
        }
    }
    
    public void transmitUpdatedSpriteToClients() {
        this.transmitUpdatedSpriteToClients(null);
    }
    
    public void transmitUpdatedSprite() {
        if (GameClient.bClient) {
            this.transmitUpdatedSpriteToServer();
        }
        if (GameServer.bServer) {
            this.transmitUpdatedSpriteToClients();
        }
    }
    
    public void sendObjectChange(final String s) {
        if (GameServer.bServer) {
            GameServer.sendObjectChange(this, s, null);
        }
        else if (GameClient.bClient) {
            DebugLog.log("sendObjectChange() can only be called on the server");
        }
        else {
            SinglePlayerServer.sendObjectChange(this, s, null);
        }
    }
    
    public void sendObjectChange(final String s, final KahluaTable kahluaTable) {
        if (GameServer.bServer) {
            GameServer.sendObjectChange(this, s, kahluaTable);
        }
        else if (GameClient.bClient) {
            DebugLog.log("sendObjectChange() can only be called on the server");
        }
        else {
            SinglePlayerServer.sendObjectChange(this, s, kahluaTable);
        }
    }
    
    public void sendObjectChange(final String s, final Object... array) {
        if (GameServer.bServer) {
            GameServer.sendObjectChange(this, s, array);
        }
        else if (GameClient.bClient) {
            DebugLog.log("sendObjectChange() can only be called on the server");
        }
        else {
            SinglePlayerServer.sendObjectChange(this, s, array);
        }
    }
    
    public void saveChange(final String s, final KahluaTable kahluaTable, final ByteBuffer byteBuffer) {
        if ("containers".equals(s)) {
            byteBuffer.put((byte)this.getContainerCount());
            for (int i = 0; i < this.getContainerCount(); ++i) {
                final ItemContainer containerByIndex = this.getContainerByIndex(i);
                try {
                    containerByIndex.save(byteBuffer);
                }
                catch (Throwable t) {
                    ExceptionLogger.logException(t);
                }
            }
        }
        else if ("container.customTemperature".equals(s)) {
            if (this.getContainer() != null) {
                byteBuffer.putFloat(this.getContainer().getCustomTemperature());
            }
            else {
                byteBuffer.putFloat(0.0f);
            }
        }
        else if ("name".equals(s)) {
            GameWindow.WriteStringUTF(byteBuffer, this.getName());
        }
        else if ("replaceWith".equals(s)) {
            if (kahluaTable != null && kahluaTable.rawget((Object)"object") instanceof IsoObject) {
                final IsoObject isoObject = (IsoObject)kahluaTable.rawget((Object)"object");
                try {
                    isoObject.save(byteBuffer);
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        else if ("usesExternalWaterSource".equals(s)) {
            byteBuffer.put((byte)((kahluaTable != null && Boolean.TRUE.equals(kahluaTable.rawget((Object)"value"))) ? 1 : 0));
        }
        else if ("sprite".equals(s)) {
            if (this.sprite == null) {
                byteBuffer.putInt(0);
            }
            else {
                byteBuffer.putInt(this.sprite.ID);
                GameWindow.WriteStringUTF(byteBuffer, this.spriteName);
            }
        }
    }
    
    public void loadChange(final String anObject, final ByteBuffer byteBuffer) {
        if ("containers".equals(anObject)) {
            for (int i = 0; i < this.getContainerCount(); ++i) {
                final ItemContainer containerByIndex = this.getContainerByIndex(i);
                containerByIndex.removeItemsFromProcessItems();
                containerByIndex.removeAllItems();
            }
            this.removeAllContainers();
            for (byte value = byteBuffer.get(), b = 0; b < value; ++b) {
                final ItemContainer container = new ItemContainer();
                container.ID = 0;
                container.parent = this;
                container.SourceGrid = this.square;
                try {
                    container.load(byteBuffer, 186);
                    if (b == 0) {
                        if (this instanceof IsoDeadBody) {
                            container.Capacity = 8;
                        }
                        this.container = container;
                    }
                    else {
                        this.addSecondaryContainer(container);
                    }
                }
                catch (Throwable t) {
                    ExceptionLogger.logException(t);
                }
            }
        }
        else if ("container.customTemperature".equals(anObject)) {
            final float float1 = byteBuffer.getFloat();
            if (this.getContainer() != null) {
                this.getContainer().setCustomTemperature(float1);
            }
        }
        else if ("name".equals(anObject)) {
            this.setName(GameWindow.ReadStringUTF(byteBuffer));
        }
        else if ("replaceWith".equals(anObject)) {
            try {
                final int objectIndex = this.getObjectIndex();
                if (objectIndex >= 0) {
                    final IsoObject factoryFromFileInput = factoryFromFileInput(this.getCell(), byteBuffer);
                    factoryFromFileInput.load(byteBuffer, 186);
                    factoryFromFileInput.setSquare(this.square);
                    this.square.getObjects().set(objectIndex, factoryFromFileInput);
                    this.square.getSpecialObjects().remove(this);
                    this.square.RecalcAllWithNeighbours(true);
                    if (this.getContainerCount() > 0) {
                        for (int j = 0; j < this.getContainerCount(); ++j) {
                            this.getContainerByIndex(j).removeItemsFromProcessItems();
                        }
                        LuaEventManager.triggerEvent("OnContainerUpdate");
                    }
                }
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        else if ("usesExternalWaterSource".equals(anObject)) {
            this.usesExternalWaterSource = (byteBuffer.get() == 1);
        }
        else if ("sprite".equals(anObject)) {
            final int int1 = byteBuffer.getInt();
            if (int1 == 0) {
                this.sprite = null;
                this.spriteName = null;
                this.tile = null;
            }
            else {
                this.spriteName = GameWindow.ReadString(byteBuffer);
                this.sprite = IsoSprite.getSprite(IsoSpriteManager.instance, int1);
                if (this.sprite == null) {
                    (this.sprite = IsoSprite.CreateSprite(IsoSpriteManager.instance)).LoadFramesNoDirPageSimple(this.spriteName);
                }
            }
        }
        else if ("emptyTrash".equals(anObject)) {
            this.getContainer().clear();
            if (this.getOverlaySprite() != null) {
                ItemPickerJava.updateOverlaySprite(this);
            }
        }
        this.checkMoveWithWind();
    }
    
    public void transmitUpdatedSpriteToServer() {
        if (GameClient.bClient) {
            final ByteBufferWriter startPacket = GameClient.connection.startPacket();
            PacketTypes.PacketType.UpdateItemSprite.doPacket(startPacket);
            startPacket.putInt(this.getSprite().ID);
            GameWindow.WriteStringUTF(startPacket.bb, this.spriteName);
            startPacket.putInt(this.getSquare().getX());
            startPacket.putInt(this.getSquare().getY());
            startPacket.putInt(this.getSquare().getZ());
            startPacket.putInt(this.getSquare().getObjects().indexOf(this));
            if (this.AttachedAnimSprite != null) {
                startPacket.putByte((byte)this.AttachedAnimSprite.size());
                for (int i = 0; i < this.AttachedAnimSprite.size(); ++i) {
                    startPacket.putInt(this.AttachedAnimSprite.get(i).parentSprite.ID);
                }
            }
            else {
                startPacket.putByte((byte)0);
            }
            PacketTypes.PacketType.UpdateItemSprite.send(GameClient.connection);
        }
    }
    
    public void transmitCompleteItemToServer() {
        if (GameClient.bClient) {
            final ByteBufferWriter startPacket = GameClient.connection.startPacket();
            PacketTypes.PacketType.AddItemToMap.doPacket(startPacket);
            this.writeToRemoteBuffer(startPacket);
            PacketTypes.PacketType.AddItemToMap.send(GameClient.connection);
        }
    }
    
    public void transmitModData() {
        if (this.square == null) {
            return;
        }
        if (GameClient.bClient) {
            final ByteBufferWriter startPacket = GameClient.connection.startPacket();
            PacketTypes.PacketType.ObjectModData.doPacket(startPacket);
            startPacket.putInt(this.getSquare().getX());
            startPacket.putInt(this.getSquare().getY());
            startPacket.putInt(this.getSquare().getZ());
            startPacket.putInt(this.getSquare().getObjects().indexOf(this));
            if (this.getModData().isEmpty()) {
                startPacket.putByte((byte)0);
            }
            else {
                startPacket.putByte((byte)1);
                try {
                    this.getModData().save(startPacket.bb);
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            PacketTypes.PacketType.ObjectModData.send(GameClient.connection);
        }
        else if (GameServer.bServer) {
            GameServer.sendObjectModData(this);
        }
    }
    
    public void writeToRemoteBuffer(final ByteBufferWriter byteBufferWriter) {
        try {
            this.save(byteBufferWriter.bb);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        byteBufferWriter.putInt(this.square.getX());
        byteBufferWriter.putInt(this.square.getY());
        byteBufferWriter.putInt(this.square.getZ());
        byteBufferWriter.putInt(this.getObjectIndex());
        byteBufferWriter.putBoolean(this.square.getSpecialObjects().contains(this));
        byteBufferWriter.putBoolean(this.square.getWorldObjects().contains(this));
    }
    
    public int getObjectIndex() {
        if (this.square == null) {
            return -1;
        }
        return this.square.getObjects().indexOf(this);
    }
    
    public int getMovingObjectIndex() {
        if (this.square == null) {
            return -1;
        }
        return this.square.getMovingObjects().indexOf(this);
    }
    
    public int getSpecialObjectIndex() {
        if (this.square == null) {
            return -1;
        }
        return this.square.getSpecialObjects().indexOf(this);
    }
    
    public int getStaticMovingObjectIndex() {
        if (this.square == null) {
            return -1;
        }
        return this.square.getStaticMovingObjects().indexOf(this);
    }
    
    public int getWorldObjectIndex() {
        if (this.square == null) {
            return -1;
        }
        return this.square.getWorldObjects().indexOf(this);
    }
    
    public IsoSprite getOverlaySprite() {
        return this.overlaySprite;
    }
    
    public void setOverlaySprite(final String s) {
        this.setOverlaySprite(s, -1.0f, -1.0f, -1.0f, -1.0f, true);
    }
    
    public void setOverlaySprite(final String s, final boolean b) {
        this.setOverlaySprite(s, -1.0f, -1.0f, -1.0f, -1.0f, b);
    }
    
    public void setOverlaySpriteColor(final float n, final float n2, final float n3, final float n4) {
        this.overlaySpriteColor = new ColorInfo(n, n2, n3, n4);
    }
    
    public ColorInfo getOverlaySpriteColor() {
        return this.overlaySpriteColor;
    }
    
    public void setOverlaySprite(final String s, final float n, final float n2, final float n3, final float n4) {
        this.setOverlaySprite(s, n, n2, n3, n4, true);
    }
    
    public boolean setOverlaySprite(String name, final float n, final float n2, final float n3, final float n4, final boolean b) {
        if (StringUtils.isNullOrWhitespace(name)) {
            if (this.overlaySprite == null) {
                return false;
            }
            this.overlaySprite = null;
            name = "";
        }
        else {
            boolean b2;
            if (n > -1.0f) {
                b2 = (this.overlaySpriteColor != null && this.overlaySpriteColor.r == n && this.overlaySpriteColor.g == n2 && this.overlaySpriteColor.b == n3 && this.overlaySpriteColor.a == n4);
            }
            else {
                b2 = (this.overlaySpriteColor == null);
            }
            if (this.overlaySprite != null && name.equals(this.overlaySprite.name) && b2) {
                return false;
            }
            this.overlaySprite = IsoSpriteManager.instance.getSprite(name);
            this.overlaySprite.name = name;
        }
        if (n > -1.0f) {
            this.overlaySpriteColor = new ColorInfo(n, n2, n3, n4);
        }
        else {
            this.overlaySpriteColor = null;
        }
        if (!b) {
            return true;
        }
        if (GameServer.bServer) {
            GameServer.updateOverlayForClients(this, name, n, n2, n3, n4, null);
        }
        else if (GameClient.bClient) {
            final ByteBufferWriter startPacket = GameClient.connection.startPacket();
            PacketTypes.PacketType.UpdateOverlaySprite.doPacket(startPacket);
            GameWindow.WriteStringUTF(startPacket.bb, name);
            startPacket.putInt(this.getSquare().getX());
            startPacket.putInt(this.getSquare().getY());
            startPacket.putInt(this.getSquare().getZ());
            startPacket.putFloat(n);
            startPacket.putFloat(n2);
            startPacket.putFloat(n3);
            startPacket.putFloat(n4);
            startPacket.putInt(this.getSquare().getObjects().indexOf(this));
            PacketTypes.PacketType.UpdateOverlaySprite.send(GameClient.connection);
        }
        return true;
    }
    
    public boolean haveSpecialTooltip() {
        return this.specialTooltip;
    }
    
    public void setSpecialTooltip(final boolean specialTooltip) {
        this.specialTooltip = specialTooltip;
    }
    
    public int getKeyId() {
        return this.keyId;
    }
    
    public void setKeyId(final int keyId) {
        this.keyId = keyId;
    }
    
    public boolean isHighlighted() {
        return (this.highlightFlags & 0x1) != 0x0;
    }
    
    public void setHighlighted(final boolean b) {
        this.setHighlighted(b, true);
    }
    
    public void setHighlighted(final boolean b, final boolean b2) {
        if (b) {
            this.highlightFlags |= 0x1;
        }
        else {
            this.highlightFlags &= 0xFFFFFFFE;
        }
        if (b2) {
            this.highlightFlags |= 0x2;
        }
        else {
            this.highlightFlags &= 0xFFFFFFFD;
        }
    }
    
    public ColorInfo getHighlightColor() {
        return this.highlightColor;
    }
    
    public void setHighlightColor(final ColorInfo colorInfo) {
        this.highlightColor.set(colorInfo);
    }
    
    public void setHighlightColor(final float n, final float n2, final float n3, final float n4) {
        if (this.highlightColor == null) {
            this.highlightColor = new ColorInfo(n, n2, n3, n4);
        }
        else {
            this.highlightColor.set(n, n2, n3, n4);
        }
    }
    
    public boolean isBlink() {
        return (this.highlightFlags & 0x4) != 0x0;
    }
    
    public void setBlink(final boolean b) {
        if (b) {
            this.highlightFlags |= 0x4;
        }
        else {
            this.highlightFlags &= 0xFFFFFFFB;
        }
    }
    
    public void checkHaveElectricity() {
        if (GameServer.bServer) {
            return;
        }
        final ItemContainer containerByEitherType = this.getContainerByEitherType("fridge", "freezer");
        if (containerByEitherType != null) {
            if (containerByEitherType.isPowered()) {
                if (this.emitter == null) {
                    (this.emitter = IsoWorld.instance.getFreeEmitter(this.getX() + 0.5f, this.getY() + 0.5f, (float)(int)this.getZ())).playAmbientLoopedImpl("FridgeHum");
                    IsoWorld.instance.getCell().addToProcessIsoObject(this);
                }
            }
            else if (this.emitter != null) {
                this.emitter.stopAll();
                this.emitter = null;
            }
        }
    }
    
    public int getContainerCount() {
        return ((this.container != null) ? 1 : 0) + ((this.secondaryContainers == null) ? 0 : this.secondaryContainers.size());
    }
    
    public ItemContainer getContainerByIndex(final int index) {
        if (this.container != null) {
            if (index == 0) {
                return this.container;
            }
            if (this.secondaryContainers == null) {
                return null;
            }
            if (index < 1 || index > this.secondaryContainers.size()) {
                return null;
            }
            return this.secondaryContainers.get(index - 1);
        }
        else {
            if (this.secondaryContainers == null) {
                return null;
            }
            if (index < 0 || index >= this.secondaryContainers.size()) {
                return null;
            }
            return this.secondaryContainers.get(index);
        }
    }
    
    public ItemContainer getContainerByType(final String anObject) {
        for (int i = 0; i < this.getContainerCount(); ++i) {
            final ItemContainer containerByIndex = this.getContainerByIndex(i);
            if (containerByIndex.getType().equals(anObject)) {
                return containerByIndex;
            }
        }
        return null;
    }
    
    public ItemContainer getContainerByEitherType(final String anObject, final String anObject2) {
        for (int i = 0; i < this.getContainerCount(); ++i) {
            final ItemContainer containerByIndex = this.getContainerByIndex(i);
            if (containerByIndex.getType().equals(anObject) || containerByIndex.getType().equals(anObject2)) {
                return containerByIndex;
            }
        }
        return null;
    }
    
    public void addSecondaryContainer(final ItemContainer e) {
        if (this.secondaryContainers == null) {
            this.secondaryContainers = new ArrayList<ItemContainer>();
        }
        this.secondaryContainers.add(e);
        e.parent = this;
    }
    
    public int getContainerIndex(final ItemContainer itemContainer) {
        if (itemContainer == this.container) {
            return 0;
        }
        if (this.secondaryContainers == null) {
            return -1;
        }
        for (int i = 0; i < this.secondaryContainers.size(); ++i) {
            if (this.secondaryContainers.get(i) == itemContainer) {
                return ((this.container != null) ? 1 : 0) + i;
            }
        }
        return -1;
    }
    
    public void removeAllContainers() {
        this.container = null;
        if (this.secondaryContainers != null) {
            this.secondaryContainers.clear();
        }
    }
    
    public void createContainersFromSpriteProperties() {
        if (this.sprite == null) {
            return;
        }
        if (this.container != null) {
            return;
        }
        if (this.sprite.getProperties().Is(IsoFlagType.container) && this.container == null) {
            this.container = new ItemContainer(this.sprite.getProperties().Val("container"), this.square, this);
            this.container.parent = this;
            this.OutlineOnMouseover = true;
            if (this.sprite.getProperties().Is("ContainerCapacity")) {
                this.container.Capacity = Integer.parseInt(this.sprite.getProperties().Val("ContainerCapacity"));
            }
            if (this.sprite.getProperties().Is("ContainerPosition")) {
                this.container.setContainerPosition(this.sprite.getProperties().Val("ContainerPosition"));
            }
        }
        if (this.getSprite().getProperties().Is("Freezer")) {
            final ItemContainer container = new ItemContainer("freezer", this.square, this);
            if (this.getSprite().getProperties().Is("FreezerCapacity")) {
                container.Capacity = Integer.parseInt(this.sprite.getProperties().Val("FreezerCapacity"));
            }
            else {
                container.Capacity = 15;
            }
            if (this.container == null) {
                this.container = container;
                this.container.parent = this;
            }
            else {
                this.addSecondaryContainer(container);
            }
            if (this.sprite.getProperties().Is("FreezerPosition")) {
                container.setFreezerPosition(this.sprite.getProperties().Val("FreezerPosition"));
            }
        }
    }
    
    public boolean isItemAllowedInContainer(final ItemContainer itemContainer, final InventoryItem inventoryItem) {
        return true;
    }
    
    public boolean isRemoveItemAllowedFromContainer(final ItemContainer itemContainer, final InventoryItem inventoryItem) {
        return true;
    }
    
    public void cleanWallBlood() {
        this.square.removeBlood(false, true);
    }
    
    public ObjectRenderEffects getWindRenderEffects() {
        return this.windRenderEffects;
    }
    
    public ObjectRenderEffects getObjectRenderEffects() {
        return this.objectRenderEffects;
    }
    
    public void setRenderEffect(final RenderEffectType renderEffectType) {
        this.setRenderEffect(renderEffectType, false);
    }
    
    public IsoObject getRenderEffectMaster() {
        return this;
    }
    
    public void setRenderEffect(final RenderEffectType renderEffectType, final boolean b) {
        if (!GameServer.bServer) {
            final IsoObject renderEffectMaster = this.getRenderEffectMaster();
            if (renderEffectMaster.objectRenderEffects == null || b) {
                renderEffectMaster.objectRenderEffects = ObjectRenderEffects.getNew(this, renderEffectType, b);
            }
        }
    }
    
    public void removeRenderEffect(final ObjectRenderEffects objectRenderEffects) {
        final IsoObject renderEffectMaster = this.getRenderEffectMaster();
        if (renderEffectMaster.objectRenderEffects != null && renderEffectMaster.objectRenderEffects == objectRenderEffects) {
            renderEffectMaster.objectRenderEffects = null;
        }
    }
    
    public ObjectRenderEffects getObjectRenderEffectsToApply() {
        final IsoObject renderEffectMaster = this.getRenderEffectMaster();
        if (renderEffectMaster.objectRenderEffects != null) {
            return renderEffectMaster.objectRenderEffects;
        }
        if (Core.getInstance().getOptionDoWindSpriteEffects() && renderEffectMaster.windRenderEffects != null) {
            return renderEffectMaster.windRenderEffects;
        }
        return null;
    }
    
    public void destroyFence(final IsoDirections isoDirections) {
        BrokenFences.getInstance().destroyFence(this, isoDirections);
    }
    
    public void getSpriteGridObjects(final ArrayList<IsoObject> list) {
        list.clear();
        final IsoSprite sprite = this.getSprite();
        if (sprite == null) {
            return;
        }
        final IsoSpriteGrid spriteGrid = sprite.getSpriteGrid();
        if (spriteGrid == null) {
            return;
        }
        final int spriteGridPosX = spriteGrid.getSpriteGridPosX(sprite);
        final int spriteGridPosY = spriteGrid.getSpriteGridPosY(sprite);
        final int x = this.getSquare().getX();
        final int y = this.getSquare().getY();
        final int z = this.getSquare().getZ();
        for (int i = y - spriteGridPosY; i < y - spriteGridPosY + spriteGrid.getHeight(); ++i) {
            for (int j = x - spriteGridPosX; j < x - spriteGridPosX + spriteGrid.getWidth(); ++j) {
                final IsoGridSquare gridSquare = this.getCell().getGridSquare(j, i, z);
                if (gridSquare != null) {
                    for (int k = 0; k < gridSquare.getObjects().size(); ++k) {
                        final IsoObject e = gridSquare.getObjects().get(k);
                        if (e.getSprite() != null && e.getSprite().getSpriteGrid() == spriteGrid) {
                            list.add(e);
                        }
                    }
                }
            }
        }
    }
    
    public final int getOutlineHighlightCol() {
        return this.outlineHighlightCol[0];
    }
    
    public final void setOutlineHighlightCol(final ColorInfo colorInfo) {
        if (colorInfo == null) {
            return;
        }
        for (int i = 0; i < this.outlineHighlightCol.length; ++i) {
            this.outlineHighlightCol[i] = Color.colorToABGR(colorInfo.r, colorInfo.g, colorInfo.b, colorInfo.a);
        }
    }
    
    public final int getOutlineHighlightCol(final int n) {
        return this.outlineHighlightCol[n];
    }
    
    public final void setOutlineHighlightCol(final int n, final ColorInfo colorInfo) {
        if (colorInfo == null) {
            return;
        }
        this.outlineHighlightCol[n] = Color.colorToABGR(colorInfo.r, colorInfo.g, colorInfo.b, colorInfo.a);
    }
    
    public final void setOutlineHighlightCol(final float n, final float n2, final float n3, final float n4) {
        for (int i = 0; i < this.outlineHighlightCol.length; ++i) {
            this.outlineHighlightCol[i] = Color.colorToABGR(n, n2, n3, n4);
        }
    }
    
    public final void setOutlineHighlightCol(final int n, final float n2, final float n3, final float n4, final float n5) {
        this.outlineHighlightCol[n] = Color.colorToABGR(n2, n3, n4, n5);
    }
    
    public final boolean isOutlineHighlight() {
        return this.isOutlineHighlight != 0;
    }
    
    public final boolean isOutlineHighlight(final int n) {
        return (this.isOutlineHighlight & 1 << n) != 0x0;
    }
    
    public final void setOutlineHighlight(final boolean b) {
        this.isOutlineHighlight = (byte)(b ? -1 : 0);
    }
    
    public final void setOutlineHighlight(final int n, final boolean b) {
        if (b) {
            this.isOutlineHighlight |= (byte)(1 << n);
        }
        else {
            this.isOutlineHighlight &= (byte)~(1 << n);
        }
    }
    
    public final boolean isOutlineHlAttached() {
        return this.isOutlineHlAttached != 0;
    }
    
    public final boolean isOutlineHlAttached(final int n) {
        return (this.isOutlineHlAttached & 1 << n) != 0x0;
    }
    
    public void setOutlineHlAttached(final boolean b) {
        this.isOutlineHlAttached = (byte)(b ? -1 : 0);
    }
    
    public final void setOutlineHlAttached(final int n, final boolean b) {
        if (b) {
            this.isOutlineHlAttached |= (byte)(1 << n);
        }
        else {
            this.isOutlineHlAttached &= (byte)~(1 << n);
        }
    }
    
    public boolean isOutlineHlBlink() {
        return this.isOutlineHlBlink != 0;
    }
    
    public final boolean isOutlineHlBlink(final int n) {
        return (this.isOutlineHlBlink & 1 << n) != 0x0;
    }
    
    public void setOutlineHlBlink(final boolean b) {
        this.isOutlineHlBlink = (byte)(b ? -1 : 0);
    }
    
    public final void setOutlineHlBlink(final int n, final boolean b) {
        if (b) {
            this.isOutlineHlBlink |= (byte)(1 << n);
        }
        else {
            this.isOutlineHlBlink &= (byte)~(1 << n);
        }
    }
    
    public void unsetOutlineHighlight() {
        this.isOutlineHighlight = 0;
        this.isOutlineHlBlink = 0;
        this.isOutlineHlAttached = 0;
    }
    
    public float getOutlineThickness() {
        return this.outlineThickness;
    }
    
    public void setOutlineThickness(final float outlineThickness) {
        this.outlineThickness = outlineThickness;
    }
    
    protected void addItemsFromProperties() {
        final PropertyContainer properties = this.getProperties();
        if (properties == null) {
            return;
        }
        final String val = properties.Val("Material");
        final String val2 = properties.Val("Material2");
        final String val3 = properties.Val("Material3");
        if ("Wood".equals(val) || "Wood".equals(val2) || "Wood".equals(val3)) {
            this.square.AddWorldInventoryItem(InventoryItemFactory.CreateItem("Base.Plank"), Rand.Next(0.0f, 0.5f), Rand.Next(0.0f, 0.5f), 0.0f);
            if (Rand.NextBool(5)) {
                this.square.AddWorldInventoryItem(InventoryItemFactory.CreateItem("Base.Plank"), Rand.Next(0.0f, 0.5f), Rand.Next(0.0f, 0.5f), 0.0f);
            }
        }
        if (("MetalBars".equals(val) || "MetalBars".equals(val2) || "MetalBars".equals(val3)) && Rand.NextBool(2)) {
            this.square.AddWorldInventoryItem(InventoryItemFactory.CreateItem("Base.MetalBar"), Rand.Next(0.0f, 0.5f), Rand.Next(0.0f, 0.5f), 0.0f);
        }
        if (("MetalPlates".equals(val) || "MetalPlates".equals(val2) || "MetalPlates".equals(val3)) && Rand.NextBool(2)) {
            this.square.AddWorldInventoryItem(InventoryItemFactory.CreateItem("Base.SheetMetal"), Rand.Next(0.0f, 0.5f), Rand.Next(0.0f, 0.5f), 0.0f);
        }
        if (("MetalPipe".equals(val) || "MetalPipe".equals(val2) || "MetalPipe".equals(val3)) && Rand.NextBool(2)) {
            this.square.AddWorldInventoryItem(InventoryItemFactory.CreateItem("Base.MetalPipe"), Rand.Next(0.0f, 0.5f), Rand.Next(0.0f, 0.5f), 0.0f);
        }
        if (("MetalWire".equals(val) || "MetalWire".equals(val2) || "MetalWire".equals(val3)) && Rand.NextBool(3)) {
            this.square.AddWorldInventoryItem(InventoryItemFactory.CreateItem("Base.Wire"), Rand.Next(0.0f, 0.5f), Rand.Next(0.0f, 0.5f), 0.0f);
        }
        if (("Nails".equals(val) || "Nails".equals(val2) || "Nails".equals(val3)) && Rand.NextBool(2)) {
            this.square.AddWorldInventoryItem(InventoryItemFactory.CreateItem("Base.Nails"), Rand.Next(0.0f, 0.5f), Rand.Next(0.0f, 0.5f), 0.0f);
        }
        if (("Screws".equals(val) || "Screws".equals(val2) || "Screws".equals(val3)) && Rand.NextBool(2)) {
            this.square.AddWorldInventoryItem(InventoryItemFactory.CreateItem("Base.Screws"), Rand.Next(0.0f, 0.5f), Rand.Next(0.0f, 0.5f), 0.0f);
        }
    }
    
    @Override
    public boolean isDestroyed() {
        return this.Damage <= 0;
    }
    
    @Override
    public void Thump(final IsoMovingObject isoMovingObject) {
        final IsoGameCharacter isoGameCharacter = Type.tryCastTo(isoMovingObject, IsoGameCharacter.class);
        if (isoGameCharacter != null) {
            final Thumpable thumpable = this.getThumpableFor(isoGameCharacter);
            if (thumpable == null) {
                return;
            }
            if (thumpable != this) {
                thumpable.Thump(isoMovingObject);
                return;
            }
        }
        final boolean breakableObject = BrokenFences.getInstance().isBreakableObject(this);
        final int n = 8;
        if (isoMovingObject instanceof IsoZombie) {
            int size = isoMovingObject.getCurrentSquare().getMovingObjects().size();
            if (isoMovingObject.getCurrentSquare().getW() != null) {
                size += isoMovingObject.getCurrentSquare().getW().getMovingObjects().size();
            }
            if (isoMovingObject.getCurrentSquare().getE() != null) {
                size += isoMovingObject.getCurrentSquare().getE().getMovingObjects().size();
            }
            if (isoMovingObject.getCurrentSquare().getS() != null) {
                size += isoMovingObject.getCurrentSquare().getS().getMovingObjects().size();
            }
            if (isoMovingObject.getCurrentSquare().getN() != null) {
                size += isoMovingObject.getCurrentSquare().getN().getMovingObjects().size();
            }
            final int n2 = n;
            if (size >= n2) {
                this.Damage -= (short)(1 * ThumpState.getFastForwardDamageMultiplier());
            }
            else {
                this.partialThumpDmg += size / (float)n2 * ThumpState.getFastForwardDamageMultiplier();
                if ((int)this.partialThumpDmg > 0) {
                    final int n3 = (int)this.partialThumpDmg;
                    this.Damage -= (short)n3;
                    this.partialThumpDmg -= n3;
                }
            }
            WorldSoundManager.instance.addSound(isoMovingObject, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, true, 4.0f, 15.0f);
        }
        if (this.Damage <= 0) {
            final String s = "BreakObject";
            if (isoGameCharacter != null) {
                isoGameCharacter.getEmitter().playSound(s, this);
            }
            if (GameServer.bServer) {
                GameServer.PlayWorldSoundServer(s, false, isoMovingObject.getCurrentSquare(), 0.2f, 20.0f, 1.1f, true);
            }
            WorldSoundManager.instance.addSound(null, this.square.getX(), this.square.getY(), this.square.getZ(), 10, 20, true, 4.0f, 15.0f);
            isoMovingObject.setThumpTarget(null);
            if (breakableObject) {
                final PropertyContainer properties = this.getProperties();
                IsoDirections isoDirections;
                if (properties.Is(IsoFlagType.collideN) && properties.Is(IsoFlagType.collideW)) {
                    isoDirections = ((isoMovingObject.getY() >= this.getY()) ? IsoDirections.N : IsoDirections.S);
                }
                else if (properties.Is(IsoFlagType.collideN)) {
                    isoDirections = ((isoMovingObject.getY() >= this.getY()) ? IsoDirections.N : IsoDirections.S);
                }
                else {
                    isoDirections = ((isoMovingObject.getX() >= this.getX()) ? IsoDirections.W : IsoDirections.E);
                }
                BrokenFences.getInstance().destroyFence(this, isoDirections);
                return;
            }
            final ArrayList<InventoryItem> list = new ArrayList<InventoryItem>();
            for (int i = 0; i < this.getContainerCount(); ++i) {
                final ItemContainer containerByIndex = this.getContainerByIndex(i);
                list.clear();
                list.addAll(containerByIndex.getItems());
                containerByIndex.removeItemsFromProcessItems();
                containerByIndex.removeAllItems();
                for (int j = 0; j < list.size(); ++j) {
                    this.getSquare().AddWorldInventoryItem(list.get(j), 0.0f, 0.0f, 0.0f);
                }
            }
            this.square.transmitRemoveItemFromSquare(this);
        }
    }
    
    public void setMovedThumpable(final boolean bMovedThumpable) {
        this.bMovedThumpable = bMovedThumpable;
    }
    
    public boolean isMovedThumpable() {
        return this.bMovedThumpable;
    }
    
    @Override
    public void WeaponHit(final IsoGameCharacter isoGameCharacter, final HandWeapon handWeapon) {
    }
    
    @Override
    public Thumpable getThumpableFor(final IsoGameCharacter isoGameCharacter) {
        if (this.isDestroyed()) {
            return null;
        }
        if (this.isMovedThumpable()) {
            return this;
        }
        if (BrokenFences.getInstance().isBreakableObject(this)) {
            final IsoZombie isoZombie = Type.tryCastTo(isoGameCharacter, IsoZombie.class);
            return (isoZombie != null && isoZombie.isCrawling()) ? this : null;
        }
        return null;
    }
    
    public boolean isExistInTheWorld() {
        return this.square.getMovingObjects().contains(this);
    }
    
    @Override
    public float getThumpCondition() {
        return PZMath.clamp(this.getDamage(), 0, 100) / 100.0f;
    }
    
    static {
        IsoObject.lastRendered = null;
        IsoObject.lastRenderedRendered = null;
        stCol = new ColorInfo();
        IsoObject.LowLightingQualityHack = false;
        IsoObject.DefaultCondition = 0;
        stCol2 = new ColorInfo();
        colFxMask = new ColorInfo(1.0f, 1.0f, 1.0f, 1.0f);
        IsoObject.byteToObjectMap = new HashMap<Byte, IsoObjectFactory>();
        IsoObject.hashCodeToObjectMap = new HashMap<Integer, IsoObjectFactory>();
        IsoObject.nameToObjectMap = new HashMap<String, IsoObjectFactory>();
        initFactory();
    }
    
    public static class IsoObjectFactory
    {
        private final byte classID;
        private final String objectName;
        private final int hashCode;
        
        public IsoObjectFactory(final byte classID, final String objectName) {
            this.classID = classID;
            this.objectName = objectName;
            this.hashCode = objectName.hashCode();
        }
        
        protected IsoObject InstantiateObject(final IsoCell isoCell) {
            return new IsoObject(isoCell);
        }
        
        public byte getClassID() {
            return this.classID;
        }
        
        public String getObjectName() {
            return this.objectName;
        }
    }
    
    public enum VisionResult
    {
        NoEffect, 
        Blocked, 
        Unblocked;
        
        private static /* synthetic */ VisionResult[] $values() {
            return new VisionResult[] { VisionResult.NoEffect, VisionResult.Blocked, VisionResult.Unblocked };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    public static class OutlineShader
    {
        public static final OutlineShader instance;
        private ShaderProgram shaderProgram;
        private int stepSize;
        private int outlineColor;
        
        public void initShader() {
            this.shaderProgram = ShaderProgram.createShaderProgram("outline", false, true);
            if (this.shaderProgram.isCompiled()) {
                this.stepSize = ARBShaderObjects.glGetUniformLocationARB(this.shaderProgram.getShaderID(), (CharSequence)"stepSize");
                this.outlineColor = ARBShaderObjects.glGetUniformLocationARB(this.shaderProgram.getShaderID(), (CharSequence)"outlineColor");
                ARBShaderObjects.glUseProgramObjectARB(this.shaderProgram.getShaderID());
                ARBShaderObjects.glUniform2fARB(this.stepSize, 0.001f, 0.001f);
                ARBShaderObjects.glUseProgramObjectARB(0);
            }
        }
        
        public void setOutlineColor(final float n, final float n2, final float n3, final float n4) {
            SpriteRenderer.instance.ShaderUpdate4f(this.shaderProgram.getShaderID(), this.outlineColor, n, n2, n3, n4);
        }
        
        public void setStepSize(final float n, final int n2, final int n3) {
            SpriteRenderer.instance.ShaderUpdate2f(this.shaderProgram.getShaderID(), this.stepSize, n / n2, n / n3);
        }
        
        public boolean StartShader() {
            if (this.shaderProgram == null) {
                RenderThread.invokeOnRenderContext(this::initShader);
            }
            if (this.shaderProgram.isCompiled()) {
                IndieGL.StartShader(this.shaderProgram.getShaderID(), 0);
                return true;
            }
            return false;
        }
        
        static {
            instance = new OutlineShader();
        }
    }
}

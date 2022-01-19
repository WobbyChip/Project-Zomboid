// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.objects;

import zombie.characters.WornItems.WornItem;
import zombie.characters.AttachedItems.AttachedItem;
import zombie.debug.LogSeverity;
import zombie.vehicles.BaseVehicle;
import zombie.audio.parameters.ParameterZombieState;
import zombie.SoundManager;
import zombie.SharedDescriptors;
import java.util.Arrays;
import zombie.iso.Vector2;
import zombie.network.ServerMap;
import zombie.core.math.PZMath;
import zombie.network.ServerOptions;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.core.SpriteRenderer;
import zombie.core.Colors;
import zombie.input.Mouse;
import org.joml.Vector3fc;
import zombie.iso.IsoObjectPicker;
import zombie.debug.LineDrawer;
import zombie.debug.DebugOptions;
import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;
import zombie.core.PerformanceSettings;
import zombie.iso.IsoUtils;
import zombie.core.skinnedmodel.DeadBodyAtlas;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.opengl.Shader;
import zombie.ui.UIFont;
import zombie.ui.TextManager;
import zombie.iso.IsoCamera;
import java.io.IOException;
import zombie.network.ServerGUI;
import zombie.GameWindow;
import zombie.iso.sprite.IsoSprite;
import java.nio.ByteBuffer;
import zombie.iso.IsoObject;
import zombie.inventory.InventoryItemFactory;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.characters.AttachedItems.AttachedLocations;
import zombie.characters.WornItems.BodyLocations;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.FliesSound;
import zombie.core.Rand;
import zombie.GameTime;
import zombie.Lua.LuaEventManager;
import zombie.SandboxOptions;
import zombie.inventory.ItemContainer;
import zombie.debug.DebugType;
import zombie.core.Core;
import zombie.iso.IsoWorld;
import zombie.characters.IsoSurvivor;
import zombie.debug.DebugLog;
import zombie.network.GameServer;
import zombie.network.GameClient;
import zombie.util.Type;
import zombie.characters.IsoGameCharacter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import org.joml.Vector3f;
import zombie.core.physics.Transform;
import org.joml.Quaternionf;
import zombie.core.textures.Texture;
import zombie.core.textures.ColorInfo;
import zombie.characters.IsoZombie;
import zombie.inventory.InventoryItem;
import zombie.characters.IsoPlayer;
import zombie.characters.AttachedItems.AttachedItems;
import zombie.characters.WornItems.WornItems;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.characters.SurvivorDesc;
import zombie.core.Color;
import zombie.core.skinnedmodel.visual.IHumanVisual;
import zombie.characters.Talker;
import zombie.iso.IsoMovingObject;

public final class IsoDeadBody extends IsoMovingObject implements Talker, IHumanVisual
{
    public static final int MAX_ROT_STAGES = 3;
    private boolean bFemale;
    private boolean wasZombie;
    private boolean bFakeDead;
    private boolean bCrawling;
    private Color SpeakColor;
    private float SpeakTime;
    private int m_persistentOutfitID;
    private SurvivorDesc desc;
    private final HumanVisual humanVisual;
    private WornItems wornItems;
    private AttachedItems attachedItems;
    private float deathTime;
    private long realWorldDeathTime;
    private float reanimateTime;
    private IsoPlayer player;
    private boolean fallOnFront;
    private boolean wasSkeleton;
    private InventoryItem primaryHandItem;
    private InventoryItem secondaryHandItem;
    private float m_angle;
    private int m_zombieRotStageAtDeath;
    private short onlineID;
    private static final ThreadLocal<IsoZombie> tempZombie;
    private static ColorInfo inf;
    public Texture atlasTex;
    private static Texture DropShadow;
    private static final float HIT_TEST_WIDTH = 0.3f;
    private static final float HIT_TEST_HEIGHT = 0.9f;
    private static final Quaternionf _rotation;
    private static final Transform _transform;
    private static final Vector3f _UNIT_Z;
    private static final Vector3f _tempVec3f_1;
    private static final Vector3f _tempVec3f_2;
    private float burnTimer;
    public boolean Speaking;
    public String sayLine;
    private static ArrayList<IsoDeadBody> AllBodies;
    private static final ConcurrentHashMap<Short, IsoDeadBody> ClientBodies;
    
    public static boolean isDead(final short s) {
        return IsoDeadBody.ClientBodies.containsKey(s);
    }
    
    @Override
    public String getObjectName() {
        return "DeadBody";
    }
    
    public IsoDeadBody(final IsoGameCharacter isoGameCharacter) {
        this(isoGameCharacter, false);
    }
    
    public IsoDeadBody(final IsoGameCharacter o, final boolean b) {
        super(o.getCell(), false);
        this.bFemale = false;
        this.wasZombie = false;
        this.bFakeDead = false;
        this.bCrawling = false;
        this.SpeakTime = 0.0f;
        this.humanVisual = new HumanVisual(this);
        this.deathTime = -1.0f;
        this.reanimateTime = -1.0f;
        this.fallOnFront = false;
        this.wasSkeleton = false;
        this.primaryHandItem = null;
        this.secondaryHandItem = null;
        this.m_zombieRotStageAtDeath = 1;
        this.onlineID = -1;
        this.burnTimer = 0.0f;
        this.Speaking = false;
        this.sayLine = "";
        final IsoZombie isoZombie = Type.tryCastTo(o, IsoZombie.class);
        this.setFallOnFront(o.isFallOnFront());
        if (!GameClient.bClient && !GameServer.bServer && isoZombie != null && isoZombie.bCrawling) {
            if (!isoZombie.isReanimate()) {
                this.setFallOnFront(true);
            }
            this.bCrawling = true;
        }
        final IsoGridSquare currentSquare = o.getCurrentSquare();
        if (currentSquare == null) {
            return;
        }
        if (o.getZ() < 0.0f) {
            DebugLog.General.error("invalid z-coordinate %d,%d,%d", o.x, o.y, o.z);
            o.setZ(0.0f);
        }
        this.square = currentSquare;
        this.current = currentSquare;
        if (o instanceof IsoPlayer) {
            ((IsoPlayer)o).removeSaveFile();
        }
        currentSquare.getStaticMovingObjects().add(this);
        if (o instanceof IsoSurvivor) {
            final IsoWorld instance = IsoWorld.instance;
            instance.TotalSurvivorNights += ((IsoSurvivor)o).nightsSurvived;
            final IsoWorld instance2 = IsoWorld.instance;
            ++instance2.TotalSurvivorsDead;
            if (IsoWorld.instance.SurvivorSurvivalRecord < ((IsoSurvivor)o).nightsSurvived) {
                IsoWorld.instance.SurvivorSurvivalRecord = ((IsoSurvivor)o).nightsSurvived;
            }
        }
        this.bFemale = o.isFemale();
        this.wasZombie = (isoZombie != null);
        if (this.wasZombie) {
            this.bFakeDead = isoZombie.isFakeDead();
            this.wasSkeleton = isoZombie.isSkeleton();
        }
        this.dir = o.dir;
        this.m_angle = o.getAnimAngleRadians();
        this.Collidable = false;
        this.x = o.getX();
        this.y = o.getY();
        this.z = o.getZ();
        this.nx = this.x;
        this.ny = this.y;
        this.offsetX = o.offsetX;
        this.offsetY = o.offsetY;
        this.solid = false;
        this.shootable = false;
        this.onlineID = o.getOnlineID();
        this.OutlineOnMouseover = true;
        this.setContainer(o.getInventory());
        this.setWornItems(o.getWornItems());
        this.setAttachedItems(o.getAttachedItems());
        if (o instanceof IHumanVisual) {
            this.getHumanVisual().copyFrom(((IHumanVisual)o).getHumanVisual());
        }
        if (Core.bDebug) {
            DebugLog.log(DebugType.Death, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getDescription()));
        }
        o.setInventory(new ItemContainer());
        o.clearWornItems();
        o.clearAttachedItems();
        this.m_zombieRotStageAtDeath = this.getHumanVisual().zombieRotStage;
        if (!this.container.bExplored) {
            this.container.setExplored(o instanceof IsoPlayer || (o instanceof IsoZombie && ((IsoZombie)o).isReanimatedPlayer()));
        }
        final boolean onFire = o.isOnFire();
        if (o instanceof IsoZombie) {
            this.m_persistentOutfitID = o.getPersistentOutfitID();
            if (!b) {
                if (GameServer.bServer) {
                    GameServer.sendKillZombie((IsoZombie)o);
                }
                else {
                    for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
                        final IsoPlayer isoPlayer = IsoPlayer.players[i];
                        if (isoPlayer != null && isoPlayer.ReanimatedCorpse == o) {
                            isoPlayer.ReanimatedCorpse = null;
                            isoPlayer.ReanimatedCorpseID = -1;
                        }
                    }
                    if (!GameClient.bClient && o.emitter != null) {
                        o.emitter.tick();
                    }
                }
            }
        }
        else {
            if (o instanceof IsoSurvivor) {
                this.getCell().getSurvivorList().remove(o);
            }
            this.desc = new SurvivorDesc(o.getDescriptor());
            if (o instanceof IsoPlayer) {
                if (GameServer.bServer) {
                    this.player = (IsoPlayer)o;
                }
                else if (!GameClient.bClient && ((IsoPlayer)o).isLocalPlayer()) {
                    this.player = (IsoPlayer)o;
                }
            }
        }
        o.removeFromWorld();
        o.removeFromSquare();
        this.sayLine = o.getSayLine();
        this.SpeakColor = o.getSpeakColour();
        this.SpeakTime = o.getSpeakTime();
        this.Speaking = o.isSpeaking();
        if (onFire) {
            if (!GameClient.bClient && SandboxOptions.instance.FireSpread.getValue()) {
                IsoFireManager.StartFire(this.getCell(), this.getSquare(), true, 100, 500);
            }
            this.container.setExplored(true);
        }
        if (!b && !GameServer.bServer) {
            LuaEventManager.triggerEvent("OnContainerUpdate", this);
        }
        if (o instanceof IsoPlayer) {
            ((IsoPlayer)o).bDeathFinished = true;
        }
        this.deathTime = (float)GameTime.getInstance().getWorldAgeHours();
        this.realWorldDeathTime = System.currentTimeMillis();
        this.setEatingZombies(o.getEatingZombies());
        if (!this.wasZombie) {
            final ArrayList<IsoZombie> list = new ArrayList<IsoZombie>();
            for (int j = -2; j < 2; ++j) {
                for (int k = -2; k < 2; ++k) {
                    final IsoGridSquare gridSquare = currentSquare.getCell().getGridSquare(currentSquare.x + j, currentSquare.y + k, currentSquare.z);
                    if (gridSquare != null) {
                        for (int l = 0; l < gridSquare.getMovingObjects().size(); ++l) {
                            if (gridSquare.getMovingObjects().get(l) instanceof IsoZombie) {
                                list.add((IsoZombie)gridSquare.getMovingObjects().get(l));
                            }
                        }
                    }
                }
            }
            for (int n = 0; n < list.size(); ++n) {
                list.get(n).pathToLocationF(this.getX() + Rand.Next(-0.3f, 0.3f), this.getY() + Rand.Next(-0.3f, 0.3f), this.getZ());
                list.get(n).bodyToEat = this;
            }
        }
        if (!GameClient.bClient) {
            int index = 0;
            for (int index2 = 0; index2 < IsoDeadBody.AllBodies.size() && IsoDeadBody.AllBodies.get(index2).deathTime < this.deathTime; ++index2) {
                ++index;
            }
            IsoDeadBody.AllBodies.add(index, this);
        }
        else if (this.wasZombie) {
            IsoDeadBody.ClientBodies.put(this.onlineID, this);
        }
        if (!GameServer.bServer) {
            FliesSound.instance.corpseAdded((int)this.getX(), (int)this.getY(), (int)this.getZ());
        }
    }
    
    public IsoDeadBody(final IsoCell isoCell) {
        super(isoCell, false);
        this.bFemale = false;
        this.wasZombie = false;
        this.bFakeDead = false;
        this.bCrawling = false;
        this.SpeakTime = 0.0f;
        this.humanVisual = new HumanVisual(this);
        this.deathTime = -1.0f;
        this.reanimateTime = -1.0f;
        this.fallOnFront = false;
        this.wasSkeleton = false;
        this.primaryHandItem = null;
        this.secondaryHandItem = null;
        this.m_zombieRotStageAtDeath = 1;
        this.onlineID = -1;
        this.burnTimer = 0.0f;
        this.Speaking = false;
        this.sayLine = "";
        this.SpeakColor = Color.white;
        this.solid = false;
        this.shootable = false;
        this.wornItems = new WornItems(BodyLocations.getGroup("Human"));
        this.attachedItems = new AttachedItems(AttachedLocations.getGroup("Human"));
    }
    
    @Override
    public HumanVisual getHumanVisual() {
        return this.humanVisual;
    }
    
    @Override
    public void getItemVisuals(final ItemVisuals itemVisuals) {
        this.wornItems.getItemVisuals(itemVisuals);
    }
    
    @Override
    public boolean isFemale() {
        return this.bFemale;
    }
    
    @Override
    public boolean isZombie() {
        return this.wasZombie;
    }
    
    public boolean isCrawling() {
        return this.bCrawling;
    }
    
    public void setCrawling(final boolean bCrawling) {
        this.bCrawling = bCrawling;
    }
    
    public boolean isFakeDead() {
        return this.bFakeDead;
    }
    
    public void setFakeDead(final boolean bFakeDead) {
        this.bFakeDead = bFakeDead;
    }
    
    public short getOnlineID() {
        return this.onlineID;
    }
    
    @Override
    public boolean isSkeleton() {
        return this.wasSkeleton;
    }
    
    public void setWornItems(final WornItems wornItems) {
        this.wornItems = new WornItems(wornItems);
    }
    
    public WornItems getWornItems() {
        return this.wornItems;
    }
    
    public void setAttachedItems(final AttachedItems attachedItems) {
        this.attachedItems = new AttachedItems(attachedItems);
        for (int i = 0; i < this.attachedItems.size(); ++i) {
            final InventoryItem item = this.attachedItems.get(i).getItem();
            if (!this.container.contains(item) && !GameClient.bClient && !GameServer.bServer) {
                item.setContainer(this.container);
                this.container.getItems().add(item);
            }
        }
    }
    
    public AttachedItems getAttachedItems() {
        return this.attachedItems;
    }
    
    public InventoryItem getItem() {
        final InventoryItem createItem = InventoryItemFactory.CreateItem("Base.CorpseMale");
        createItem.storeInByteData(this);
        return createItem;
    }
    
    private IsoSprite loadSprite(final ByteBuffer byteBuffer) {
        GameWindow.ReadString(byteBuffer);
        byteBuffer.getFloat();
        byteBuffer.getFloat();
        byteBuffer.getFloat();
        byteBuffer.getFloat();
        return null;
    }
    
    @Override
    public void load(final ByteBuffer byteBuffer, final int n, final boolean b) throws IOException {
        super.load(byteBuffer, n, b);
        this.bFemale = (byteBuffer.get() == 1);
        this.wasZombie = (byteBuffer.get() == 1);
        final boolean b2 = byteBuffer.get() == 1;
        if (n >= 171) {
            this.m_persistentOutfitID = byteBuffer.getInt();
        }
        if (b2 && n < 171) {
            byteBuffer.getShort();
        }
        if (byteBuffer.get() == 1) {
            (this.desc = new SurvivorDesc(true)).load(byteBuffer, n, null);
        }
        this.humanVisual.load(byteBuffer, n);
        if (byteBuffer.get() == 1) {
            final int int1 = byteBuffer.getInt();
            try {
                this.setContainer(new ItemContainer());
                this.container.ID = int1;
                final ArrayList<InventoryItem> load = this.container.load(byteBuffer, n);
                for (byte value = byteBuffer.get(), b3 = 0; b3 < value; ++b3) {
                    final String readString = GameWindow.ReadString(byteBuffer);
                    final short short1 = byteBuffer.getShort();
                    if (short1 >= 0 && short1 < load.size() && this.wornItems.getBodyLocationGroup().getLocation(readString) != null) {
                        this.wornItems.setItem(readString, load.get(short1));
                    }
                }
                for (byte value2 = byteBuffer.get(), b4 = 0; b4 < value2; ++b4) {
                    final String readString2 = GameWindow.ReadString(byteBuffer);
                    final short short2 = byteBuffer.getShort();
                    if (short2 >= 0 && short2 < load.size() && this.attachedItems.getGroup().getLocation(readString2) != null) {
                        this.attachedItems.setItem(readString2, load.get(short2));
                    }
                }
            }
            catch (Exception ex) {
                if (this.container != null) {
                    DebugLog.log(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.container.ID));
                }
            }
        }
        this.deathTime = byteBuffer.getFloat();
        this.reanimateTime = byteBuffer.getFloat();
        this.fallOnFront = (byteBuffer.get() == 1);
        if (b2 && (GameClient.bClient || (GameServer.bServer && ServerGUI.isCreated()))) {
            this.checkClothing(null);
        }
        this.wasSkeleton = (byteBuffer.get() == 1);
        if (n >= 159) {
            this.m_angle = byteBuffer.getFloat();
        }
        else {
            this.m_angle = this.dir.toAngle();
        }
        if (n >= 166) {
            this.m_zombieRotStageAtDeath = (byteBuffer.get() & 0xFF);
        }
        if (n >= 168) {
            this.bCrawling = (byteBuffer.get() == 1);
            this.bFakeDead = (byteBuffer.get() == 1);
        }
    }
    
    @Override
    public void save(final ByteBuffer byteBuffer, final boolean b) throws IOException {
        super.save(byteBuffer, b);
        byteBuffer.put((byte)(this.bFemale ? 1 : 0));
        byteBuffer.put((byte)(this.wasZombie ? 1 : 0));
        if (GameServer.bServer || GameClient.bClient) {
            byteBuffer.put((byte)1);
        }
        else {
            byteBuffer.put((byte)0);
        }
        byteBuffer.putInt(this.m_persistentOutfitID);
        if (this.desc != null) {
            byteBuffer.put((byte)1);
            this.desc.save(byteBuffer);
        }
        else {
            byteBuffer.put((byte)0);
        }
        this.humanVisual.save(byteBuffer);
        if (this.container != null) {
            byteBuffer.put((byte)1);
            byteBuffer.putInt(this.container.ID);
            this.container.save(byteBuffer);
            if (this.wornItems.size() > 127) {
                throw new RuntimeException("too many worn items");
            }
            byteBuffer.put((byte)this.wornItems.size());
            final ArrayList list;
            this.wornItems.forEach(wornItem -> {
                GameWindow.WriteString(byteBuffer, wornItem.getLocation());
                byteBuffer.putShort((short)list.indexOf(wornItem.getItem()));
                return;
            });
            if (this.attachedItems.size() > 127) {
                throw new RuntimeException("too many attached items");
            }
            byteBuffer.put((byte)this.attachedItems.size());
            final ArrayList list2;
            this.attachedItems.forEach(attachedItem -> {
                GameWindow.WriteString(byteBuffer, attachedItem.getLocation());
                byteBuffer.putShort((short)list2.indexOf(attachedItem.getItem()));
                return;
            });
        }
        else {
            byteBuffer.put((byte)0);
        }
        byteBuffer.putFloat(this.deathTime);
        byteBuffer.putFloat(this.reanimateTime);
        byteBuffer.put((byte)(this.fallOnFront ? 1 : 0));
        byteBuffer.put((byte)(this.isSkeleton() ? 1 : 0));
        byteBuffer.putFloat(this.m_angle);
        byteBuffer.put((byte)this.m_zombieRotStageAtDeath);
        byteBuffer.put((byte)(this.bCrawling ? 1 : 0));
        byteBuffer.put((byte)(this.bFakeDead ? 1 : 0));
    }
    
    @Override
    public void softReset() {
        this.square.RemoveTileObject(this);
    }
    
    @Override
    public void renderlast() {
        if (this.Speaking) {
            final float sx = this.sx;
            final float sy = this.sy;
            final float n = sx - IsoCamera.getOffX();
            final float n2 = sy - IsoCamera.getOffY();
            final float n3 = n + 8.0f;
            final float n4 = n2 + 32.0f;
            if (this.sayLine != null) {
                TextManager.instance.DrawStringCentre(UIFont.Medium, n3, n4, this.sayLine, this.SpeakColor.r, this.SpeakColor.g, this.SpeakColor.b, this.SpeakColor.a);
            }
        }
    }
    
    @Override
    public void render(final float f, final float f2, final float n, ColorInfo inf, final boolean b, final boolean b2, final Shader shader) {
        this.offsetX = 0.0f;
        this.offsetY = 0.0f;
        final boolean highlighted = this.isHighlighted();
        if (ModelManager.instance.bDebugEnableModels && ModelManager.instance.isCreated()) {
            if (this.atlasTex == null) {
                this.atlasTex = DeadBodyAtlas.instance.getBodyTexture(this);
                DeadBodyAtlas.instance.render();
            }
            if (this.atlasTex != null) {
                if (IsoSprite.globalOffsetX == -1.0f) {
                    IsoSprite.globalOffsetX = -IsoCamera.frameState.OffX;
                    IsoSprite.globalOffsetY = -IsoCamera.frameState.OffY;
                }
                final float xToScreen = IsoUtils.XToScreen(f, f2, n, 0);
                final float yToScreen = IsoUtils.YToScreen(f, f2, n, 0);
                this.sx = xToScreen;
                this.sy = yToScreen;
                final float sx = this.sx + IsoSprite.globalOffsetX;
                final float sy = this.sy + IsoSprite.globalOffsetY;
                if (Core.TileScale == 1) {}
                if (highlighted) {
                    IsoDeadBody.inf.r = this.getHighlightColor().r;
                    IsoDeadBody.inf.g = this.getHighlightColor().g;
                    IsoDeadBody.inf.b = this.getHighlightColor().b;
                    IsoDeadBody.inf.a = this.getHighlightColor().a;
                }
                else {
                    IsoDeadBody.inf.r = inf.r;
                    IsoDeadBody.inf.g = inf.g;
                    IsoDeadBody.inf.b = inf.b;
                    IsoDeadBody.inf.a = inf.a;
                }
                inf = IsoDeadBody.inf;
                if (!highlighted && PerformanceSettings.LightingFrameSkip < 3 && this.getCurrentSquare() != null) {
                    this.getCurrentSquare().interpolateLight(inf, f - this.getCurrentSquare().getX(), f2 - this.getCurrentSquare().getY());
                }
                if (GameServer.bServer && ServerGUI.isCreated()) {
                    IsoDeadBody.inf.set(1.0f, 1.0f, 1.0f, 1.0f);
                }
                this.atlasTex.render((float)((int)sx - this.atlasTex.getWidth() / 2), (float)((int)sy - this.atlasTex.getHeight() / 2), (float)this.atlasTex.getWidth(), (float)this.atlasTex.getHeight(), inf.r, inf.g, inf.b, inf.a, null);
                if (Core.bDebug && DebugOptions.instance.DeadBodyAtlasRender.getValue()) {
                    LineDrawer.DrawIsoLine(f - 0.5f, f2, n, f + 0.5f, f2, n, 1.0f, 1.0f, 1.0f, 0.25f, 1);
                    LineDrawer.DrawIsoLine(f, f2 - 0.5f, n, f, f2 + 0.5f, n, 1.0f, 1.0f, 1.0f, 0.25f, 1);
                }
                this.sx = sx;
                this.sy = sy;
                if (IsoObjectPicker.Instance.wasDirty) {
                    this.renderObjectPicker(this.getX(), this.getY(), this.getZ(), inf);
                }
            }
        }
        if (Core.bDebug && DebugOptions.instance.DeadBodyAtlasRender.getValue()) {
            IsoDeadBody._rotation.setAngleAxis(this.m_angle + 1.5707963267948966, 0.0, 0.0, 1.0);
            IsoDeadBody._transform.setRotation(IsoDeadBody._rotation);
            IsoDeadBody._transform.origin.set(this.x, this.y, this.z);
            final Vector3f tempVec3f_1 = IsoDeadBody._tempVec3f_1;
            IsoDeadBody._transform.basis.getColumn(1, tempVec3f_1);
            final Vector3f tempVec3f_2 = IsoDeadBody._tempVec3f_2;
            tempVec3f_1.cross((Vector3fc)IsoDeadBody._UNIT_Z, tempVec3f_2);
            final float n2 = 0.3f;
            final float n3 = 0.9f;
            final Vector3f vector3f = tempVec3f_1;
            vector3f.x *= n3;
            final Vector3f vector3f2 = tempVec3f_1;
            vector3f2.y *= n3;
            final Vector3f vector3f3 = tempVec3f_2;
            vector3f3.x *= n2;
            final Vector3f vector3f4 = tempVec3f_2;
            vector3f4.y *= n2;
            final float n4 = f + tempVec3f_1.x;
            final float n5 = f2 + tempVec3f_1.y;
            final float n6 = f - tempVec3f_1.x;
            final float n7 = f2 - tempVec3f_1.y;
            final float n8 = n4 - tempVec3f_2.x;
            final float n9 = n4 + tempVec3f_2.x;
            final float n10 = n6 - tempVec3f_2.x;
            final float n11 = n6 + tempVec3f_2.x;
            final float n12 = n7 - tempVec3f_2.y;
            final float n13 = n7 + tempVec3f_2.y;
            final float n14 = n5 - tempVec3f_2.y;
            final float n15 = n5 + tempVec3f_2.y;
            float n16 = 1.0f;
            final float n17 = 1.0f;
            float n18 = 1.0f;
            if (this.isMouseOver((float)Mouse.getX(), (float)Mouse.getY())) {
                n18 = (n16 = 0.0f);
            }
            LineDrawer.addLine(n8, n14, 0.0f, n9, n15, 0.0f, n16, n17, n18, null, true);
            LineDrawer.addLine(n8, n14, 0.0f, n10, n12, 0.0f, n16, n17, n18, null, true);
            LineDrawer.addLine(n9, n15, 0.0f, n11, n13, 0.0f, n16, n17, n18, null, true);
            LineDrawer.addLine(n10, n12, 0.0f, n11, n13, 0.0f, n16, n17, n18, null, true);
        }
        if (this.isFakeDead() && DebugOptions.instance.ZombieRenderFakeDead.getValue()) {
            TextManager.instance.DrawStringCentre(UIFont.Medium, IsoUtils.XToScreen(f, f2, n, 0) + IsoSprite.globalOffsetX, IsoUtils.YToScreen(f, f2, n, 0) + IsoSprite.globalOffsetY - 16 * Core.TileScale, String.format("FakeDead %.2f", Math.max(this.getFakeDeadWakeupHours() - (float)GameTime.getInstance().getWorldAgeHours(), 0.0f)), 1.0, 1.0, 1.0, 1.0);
        }
        if (Core.bDebug && (DebugOptions.instance.MultiplayerShowZombieStatus.getValue() || DebugOptions.instance.MultiplayerShowPlayerStatus.getValue() || DebugOptions.instance.MultiplayerShowZombieOwner.getValue()) && this.onlineID != -1) {
            final Color yellow = Colors.Yellow;
            final float xToScreenExact = IsoUtils.XToScreenExact(f + 0.4f, f2 + 0.4f, n, 0);
            final float yToScreenExact = IsoUtils.YToScreenExact(f + 0.4f, f2 - 1.4f, n, 0);
            TextManager.instance.DrawStringCentre(UIFont.DebugConsole, xToScreenExact, yToScreenExact, String.valueOf(this.onlineID), yellow.r, yellow.g, yellow.b, yellow.a);
            TextManager.instance.DrawStringCentre(UIFont.DebugConsole, xToScreenExact, yToScreenExact + 10.0f, String.format("x=%09.3f", f), yellow.r, yellow.g, yellow.b, yellow.a);
            TextManager.instance.DrawStringCentre(UIFont.DebugConsole, xToScreenExact, yToScreenExact + 20.0f, String.format("y=%09.3f", f2), yellow.r, yellow.g, yellow.b, yellow.a);
            TextManager.instance.DrawStringCentre(UIFont.DebugConsole, xToScreenExact, yToScreenExact + 30.0f, String.format("z=%d", (byte)n), yellow.r, yellow.g, yellow.b, yellow.a);
        }
    }
    
    public void renderShadow() {
        IsoDeadBody._rotation.setAngleAxis(this.m_angle + 1.5707963267948966, 0.0, 0.0, 1.0);
        IsoDeadBody._transform.setRotation(IsoDeadBody._rotation);
        IsoDeadBody._transform.origin.set(this.x, this.y, this.z);
        final Vector3f tempVec3f_1 = IsoDeadBody._tempVec3f_1;
        IsoDeadBody._transform.basis.getColumn(1, tempVec3f_1);
        final float n = 0.45f;
        final float n2 = 1.4f;
        final float n3 = 1.125f;
        final int playerIndex = IsoCamera.frameState.playerIndex;
        renderShadow(this.x, this.y, this.z, tempVec3f_1, n, n2, n3, this.square.lighting[playerIndex].lightInfo(), this.getAlpha(playerIndex));
    }
    
    public static void renderShadow(final float n, final float n2, final float n3, final Vector3f vector3f, float max, float max2, float max3, final ColorInfo colorInfo, final float n4) {
        final float n5 = n4 * ((colorInfo.r + colorInfo.g + colorInfo.b) / 3.0f) * 0.66f;
        vector3f.normalize();
        final Vector3f tempVec3f_2 = IsoDeadBody._tempVec3f_2;
        vector3f.cross((Vector3fc)IsoDeadBody._UNIT_Z, tempVec3f_2);
        max = Math.max(0.65f, max);
        max2 = Math.max(max2, 0.65f);
        max3 = Math.max(max3, 0.65f);
        final Vector3f vector3f2 = tempVec3f_2;
        vector3f2.x *= max;
        final Vector3f vector3f3 = tempVec3f_2;
        vector3f3.y *= max;
        final float n6 = n + vector3f.x * max2;
        final float n7 = n2 + vector3f.y * max2;
        final float n8 = n - vector3f.x * max3;
        final float n9 = n2 - vector3f.y * max3;
        final float n10 = n6 - tempVec3f_2.x;
        final float n11 = n6 + tempVec3f_2.x;
        final float n12 = n8 - tempVec3f_2.x;
        final float n13 = n8 + tempVec3f_2.x;
        final float n14 = n9 - tempVec3f_2.y;
        final float n15 = n9 + tempVec3f_2.y;
        final float n16 = n7 - tempVec3f_2.y;
        final float n17 = n7 + tempVec3f_2.y;
        final float xToScreenExact = IsoUtils.XToScreenExact(n10, n16, n3, 0);
        final float yToScreenExact = IsoUtils.YToScreenExact(n10, n16, n3, 0);
        final float xToScreenExact2 = IsoUtils.XToScreenExact(n11, n17, n3, 0);
        final float yToScreenExact2 = IsoUtils.YToScreenExact(n11, n17, n3, 0);
        final float xToScreenExact3 = IsoUtils.XToScreenExact(n13, n15, n3, 0);
        final float yToScreenExact3 = IsoUtils.YToScreenExact(n13, n15, n3, 0);
        final float xToScreenExact4 = IsoUtils.XToScreenExact(n12, n14, n3, 0);
        final float yToScreenExact4 = IsoUtils.YToScreenExact(n12, n14, n3, 0);
        if (IsoDeadBody.DropShadow == null) {
            IsoDeadBody.DropShadow = Texture.getSharedTexture("media/textures/NewShadow.png");
        }
        SpriteRenderer.instance.renderPoly(IsoDeadBody.DropShadow, xToScreenExact, yToScreenExact, xToScreenExact2, yToScreenExact2, xToScreenExact3, yToScreenExact3, xToScreenExact4, yToScreenExact4, 0.0f, 0.0f, 0.0f, n5);
        if (DebugOptions.instance.IsoSprite.DropShadowEdges.getValue()) {
            LineDrawer.addLine(n10, n16, n3, n11, n17, n3, 1, 1, 1, null);
            LineDrawer.addLine(n11, n17, n3, n13, n15, n3, 1, 1, 1, null);
            LineDrawer.addLine(n13, n15, n3, n12, n14, n3, 1, 1, 1, null);
            LineDrawer.addLine(n12, n14, n3, n10, n16, n3, 1, 1, 1, null);
        }
    }
    
    @Override
    public void renderObjectPicker(final float n, final float n2, final float n3, final ColorInfo colorInfo) {
        if (this.atlasTex == null) {
            return;
        }
        IsoObjectPicker.Instance.Add((int)(this.sx - this.atlasTex.getWidth() / 2), (int)(this.sy - this.atlasTex.getHeight() / 2), this.atlasTex.getWidthOrig(), this.atlasTex.getHeightOrig(), this.square, this, false, 1.0f, 1.0f);
    }
    
    public boolean isMouseOver(final float n, final float n2) {
        IsoDeadBody._rotation.setAngleAxis(this.m_angle + 1.5707963267948966, 0.0, 0.0, 1.0);
        IsoDeadBody._transform.setRotation(IsoDeadBody._rotation);
        IsoDeadBody._transform.origin.set(this.x, this.y, this.z);
        IsoDeadBody._transform.inverse();
        final Vector3f set = IsoDeadBody._tempVec3f_1.set(IsoUtils.XToIso(n, n2, this.z), IsoUtils.YToIso(n, n2, this.z), this.z);
        IsoDeadBody._transform.transform(set);
        return set.x >= -0.3f && set.y >= -0.9f && set.x < 0.3f && set.y < 0.9f;
    }
    
    public void Burn() {
        if (GameClient.bClient) {
            return;
        }
        if (this.getSquare() != null && this.getSquare().getProperties().Is(IsoFlagType.burning)) {
            this.burnTimer += GameTime.instance.getMultipliedSecondsSinceLastUpdate();
            if (this.burnTimer >= 10.0f) {
                boolean b = true;
                for (int i = 0; i < this.getSquare().getObjects().size(); ++i) {
                    final IsoObject isoObject = this.getSquare().getObjects().get(i);
                    if (isoObject.getName() != null && "burnedCorpse".equals(isoObject.getName())) {
                        b = false;
                        break;
                    }
                }
                if (b) {
                    final IsoObject isoObject2 = new IsoObject(this.getSquare(), invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Rand.Next(1, 3)), "burnedCorpse");
                    this.getSquare().getObjects().add(isoObject2);
                    isoObject2.transmitCompleteItemToClients();
                }
                if (GameServer.bServer) {
                    GameServer.sendRemoveCorpseFromMap(this);
                }
                this.getSquare().removeCorpse(this, true);
            }
        }
    }
    
    @Override
    public void setContainer(final ItemContainer container) {
        super.setContainer(container);
        container.type = (this.bFemale ? "inventoryfemale" : "inventorymale");
        container.Capacity = 8;
        container.SourceGrid = this.square;
    }
    
    public void checkClothing(final InventoryItem inventoryItem) {
        for (int i = 0; i < this.wornItems.size(); ++i) {
            final InventoryItem itemByIndex = this.wornItems.getItemByIndex(i);
            if (this.container == null || this.container.getItems().indexOf(itemByIndex) == -1) {
                this.wornItems.remove(itemByIndex);
                this.atlasTex = null;
                --i;
            }
        }
        if (inventoryItem == this.getPrimaryHandItem()) {
            this.setPrimaryHandItem(null);
            this.atlasTex = null;
        }
        if (inventoryItem == this.getSecondaryHandItem()) {
            this.setSecondaryHandItem(null);
            this.atlasTex = null;
        }
        for (int j = 0; j < this.attachedItems.size(); ++j) {
            final InventoryItem itemByIndex2 = this.attachedItems.getItemByIndex(j);
            if (this.container == null || this.container.getItems().indexOf(itemByIndex2) == -1) {
                this.attachedItems.remove(itemByIndex2);
                this.atlasTex = null;
                --j;
            }
        }
    }
    
    @Override
    public boolean IsSpeaking() {
        return this.Speaking;
    }
    
    @Override
    public void Say(final String sayLine) {
        this.SpeakTime = (float)(sayLine.length() * 4);
        if (this.SpeakTime < 60.0f) {
            this.SpeakTime = 60.0f;
        }
        this.sayLine = sayLine;
        this.Speaking = true;
    }
    
    @Override
    public String getSayLine() {
        return this.sayLine;
    }
    
    @Override
    public String getTalkerType() {
        return "Talker";
    }
    
    @Override
    public void addToWorld() {
        super.addToWorld();
        if (!GameServer.bServer) {
            FliesSound.instance.corpseAdded((int)this.getX(), (int)this.getY(), (int)this.getZ());
        }
        if (GameClient.bClient) {
            return;
        }
        if (this.reanimateTime > 0.0f) {
            this.getCell().addToStaticUpdaterObjectList(this);
            if (Core.bDebug) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(FLzombie/iso/objects/IsoDeadBody;)Ljava/lang/String;, this.reanimateTime, this));
            }
        }
        final float n = (float)GameTime.getInstance().getWorldAgeHours();
        if (this.deathTime < 0.0f) {
            this.deathTime = n;
        }
        if (this.deathTime > n) {
            this.deathTime = n;
        }
        int index = 0;
        for (int index2 = 0; index2 < IsoDeadBody.AllBodies.size() && IsoDeadBody.AllBodies.get(index2).deathTime < this.deathTime; ++index2) {
            ++index;
        }
        IsoDeadBody.AllBodies.add(index, this);
    }
    
    @Override
    public void removeFromWorld() {
        if (!GameServer.bServer) {
            FliesSound.instance.corpseRemoved((int)this.getX(), (int)this.getY(), (int)this.getZ());
        }
        if (!GameClient.bClient) {
            IsoDeadBody.AllBodies.remove(this);
        }
        super.removeFromWorld();
    }
    
    public static void updateBodies() {
        if (GameClient.bClient) {
            IsoDeadBody.ClientBodies.values().removeIf(isoDeadBody -> (float)GameTime.getInstance().getWorldAgeHours() - 0.1f > isoDeadBody.deathTime);
            return;
        }
        if (Core.bDebug) {}
        final boolean b = false;
        final float n = (float)SandboxOptions.instance.HoursForCorpseRemoval.getValue();
        if (n <= 0.0f) {
            return;
        }
        final float n2 = n / 3.0f;
        final float deathTime = (float)GameTime.getInstance().getWorldAgeHours();
        for (int i = 0; i < IsoDeadBody.AllBodies.size(); ++i) {
            final IsoDeadBody isoDeadBody2 = IsoDeadBody.AllBodies.get(i);
            if (isoDeadBody2.deathTime > deathTime) {
                isoDeadBody2.deathTime = deathTime;
                isoDeadBody2.getHumanVisual().zombieRotStage = isoDeadBody2.m_zombieRotStageAtDeath;
            }
            if (!isoDeadBody2.updateFakeDead()) {
                if (ServerOptions.instance.RemovePlayerCorpsesOnCorpseRemoval.getValue() || isoDeadBody2.wasZombie) {
                    final int zombieRotStage = isoDeadBody2.getHumanVisual().zombieRotStage;
                    isoDeadBody2.updateRotting(deathTime, n2, b);
                    final int zombieRotStage2 = isoDeadBody2.getHumanVisual().zombieRotStage;
                    final float f = deathTime - isoDeadBody2.deathTime;
                    if (f >= n + (isoDeadBody2.isSkeleton() ? n2 : 0.0f)) {
                        if (b) {
                            DebugLog.General.debugln("%s REMOVE %d -> %d age=%.2f stages=%d", isoDeadBody2, zombieRotStage, zombieRotStage2, f, (int)(f / n2));
                        }
                        if (GameServer.bServer) {
                            GameServer.sendRemoveCorpseFromMap(isoDeadBody2);
                        }
                        isoDeadBody2.removeFromWorld();
                        isoDeadBody2.removeFromSquare();
                        --i;
                    }
                }
            }
        }
    }
    
    private void updateRotting(final float n, final float n2, final boolean b) {
        if (this.isSkeleton()) {
            return;
        }
        final float n3 = n - this.deathTime;
        final int n4 = (int)(n3 / n2);
        int clamp = this.m_zombieRotStageAtDeath + n4;
        if (n4 < 3) {
            clamp = PZMath.clamp(clamp, 1, 3);
        }
        if (clamp <= 3 && clamp != this.getHumanVisual().zombieRotStage) {
            if (b) {
                DebugLog.General.debugln("%s zombieRotStage %d -> %d age=%.2f stages=%d", this, this.getHumanVisual().zombieRotStage, clamp, n3, n4);
            }
            this.getHumanVisual().zombieRotStage = clamp;
            this.atlasTex = null;
            if (GameServer.bServer) {
                GameServer.sendRemoveCorpseFromMap(this);
                GameServer.sendCorpse(this);
            }
            return;
        }
        if (n4 == 3 && Rand.NextBool(7)) {
            if (b) {
                DebugLog.General.debugln("%s zombieRotStage %d -> x age=%.2f stages=%d", this, this.getHumanVisual().zombieRotStage, n3, n4);
            }
            this.getHumanVisual().setBeardModel("");
            this.getHumanVisual().setHairModel("");
            this.getHumanVisual().setSkinTextureIndex(Rand.Next(1, 3));
            this.wasSkeleton = true;
            this.getWornItems().clear();
            this.getAttachedItems().clear();
            this.getContainer().clear();
            this.atlasTex = null;
            if (GameServer.bServer) {
                GameServer.sendRemoveCorpseFromMap(this);
                GameServer.sendCorpse(this);
            }
        }
    }
    
    private boolean updateFakeDead() {
        if (!this.isFakeDead()) {
            return false;
        }
        if (this.isSkeleton()) {
            return false;
        }
        if (this.getFakeDeadWakeupHours() > GameTime.getInstance().getWorldAgeHours()) {
            return false;
        }
        if (!this.isPlayerNearby()) {
            return false;
        }
        this.reanimateNow();
        return true;
    }
    
    private float getFakeDeadWakeupHours() {
        return this.deathTime + 0.5f;
    }
    
    private boolean isPlayerNearby() {
        if (!GameServer.bServer) {
            final IsoGridSquare square = this.getSquare();
            for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
                if (this.isPlayerNearby(IsoPlayer.players[i], square != null && square.isCanSee(i))) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean isPlayerNearby(final IsoPlayer isoPlayer, final boolean b) {
        if (!b) {
            return false;
        }
        if (isoPlayer == null || isoPlayer.isDead()) {
            return false;
        }
        if (isoPlayer.isGhostMode() || isoPlayer.isInvisible()) {
            return false;
        }
        if (isoPlayer.getVehicle() != null) {
            return false;
        }
        final float distToSquared = isoPlayer.DistToSquared(this);
        return distToSquared >= 4.0f && distToSquared <= 16.0f;
    }
    
    public void setReanimateTime(final float reanimateTime) {
        this.reanimateTime = reanimateTime;
        if (GameClient.bClient) {
            return;
        }
        final ArrayList<IsoObject> staticUpdaterObjectList = IsoWorld.instance.CurrentCell.getStaticUpdaterObjectList();
        if (this.reanimateTime > 0.0f && !staticUpdaterObjectList.contains(this)) {
            staticUpdaterObjectList.add(this);
        }
        else if (this.reanimateTime <= 0.0f && staticUpdaterObjectList.contains(this)) {
            staticUpdaterObjectList.remove(this);
        }
    }
    
    private float getReanimateDelay() {
        float n = 0.0f;
        float n2 = 0.0f;
        switch (SandboxOptions.instance.Lore.Reanimate.getValue()) {
            case 2: {
                n2 = 0.5f;
                break;
            }
            case 3: {
                n2 = 0.016666668f;
                break;
            }
            case 4: {
                n2 = 12.0f;
                break;
            }
            case 5: {
                n = 48.0f;
                n2 = 72.0f;
                break;
            }
            case 6: {
                n = 168.0f;
                n2 = 336.0f;
                break;
            }
        }
        if (Core.bTutorial) {
            n2 = 0.25f;
        }
        if (n == n2) {
            return n;
        }
        return Rand.Next(n, n2);
    }
    
    public void reanimateLater() {
        this.setReanimateTime((float)GameTime.getInstance().getWorldAgeHours() + this.getReanimateDelay());
    }
    
    public void reanimateNow() {
        this.setReanimateTime((float)GameTime.getInstance().getWorldAgeHours());
        this.realWorldDeathTime = 0L;
    }
    
    @Override
    public void update() {
        if (this.current == null) {
            this.current = IsoWorld.instance.CurrentCell.getGridSquare(this.x, this.y, this.z);
        }
        if (GameClient.bClient) {
            return;
        }
        if (this.reanimateTime > 0.0f) {
            if (System.currentTimeMillis() - this.realWorldDeathTime < 10000L) {
                return;
            }
            if (this.reanimateTime <= (float)GameTime.getInstance().getWorldAgeHours()) {
                this.reanimate();
            }
        }
    }
    
    public void reanimate() {
        short uniqueZombieId = -1;
        if (GameServer.bServer) {
            uniqueZombieId = ServerMap.instance.getUniqueZombieId();
            if (uniqueZombieId == -1) {
                return;
            }
        }
        final SurvivorDesc survivorDesc = new SurvivorDesc();
        survivorDesc.setFemale(this.isFemale());
        final IsoZombie reanimatedCorpse = new IsoZombie(IsoWorld.instance.CurrentCell, survivorDesc, -1);
        reanimatedCorpse.setPersistentOutfitID(this.m_persistentOutfitID);
        if (this.container == null) {
            this.container = new ItemContainer();
        }
        reanimatedCorpse.setInventory(this.container);
        this.container = null;
        reanimatedCorpse.getHumanVisual().copyFrom(this.getHumanVisual());
        reanimatedCorpse.getWornItems().copyFrom(this.wornItems);
        this.wornItems.clear();
        reanimatedCorpse.getAttachedItems().copyFrom(this.attachedItems);
        this.attachedItems.clear();
        reanimatedCorpse.setX(this.getX());
        reanimatedCorpse.setY(this.getY());
        reanimatedCorpse.setZ(this.getZ());
        reanimatedCorpse.setCurrent(this.getCurrentSquare());
        reanimatedCorpse.setMovingSquareNow();
        reanimatedCorpse.setDir(this.dir);
        reanimatedCorpse.getAnimationPlayer().setTargetAngle(this.m_angle);
        reanimatedCorpse.getAnimationPlayer().setAngleToTarget();
        reanimatedCorpse.setForwardDirection(Vector2.fromLengthDirection(1.0f, this.m_angle));
        reanimatedCorpse.setAlphaAndTarget(1.0f);
        Arrays.fill(reanimatedCorpse.IsVisibleToPlayer, true);
        reanimatedCorpse.setOnFloor(true);
        reanimatedCorpse.setCrawler(this.bCrawling);
        reanimatedCorpse.setCanWalk(!this.bCrawling);
        reanimatedCorpse.walkVariant = "ZombieWalk";
        reanimatedCorpse.DoZombieStats();
        reanimatedCorpse.setFallOnFront(this.isFallOnFront());
        if (SandboxOptions.instance.Lore.Toughness.getValue() == 1) {
            reanimatedCorpse.setHealth(3.5f + Rand.Next(0.0f, 0.3f));
        }
        if (SandboxOptions.instance.Lore.Toughness.getValue() == 2) {
            reanimatedCorpse.setHealth(1.8f + Rand.Next(0.0f, 0.3f));
        }
        if (SandboxOptions.instance.Lore.Toughness.getValue() == 3) {
            reanimatedCorpse.setHealth(0.5f + Rand.Next(0.0f, 0.3f));
        }
        if (GameServer.bServer) {
            reanimatedCorpse.OnlineID = uniqueZombieId;
            ServerMap.instance.ZombieMap.put(reanimatedCorpse.OnlineID, reanimatedCorpse);
        }
        if (this.isFakeDead()) {
            reanimatedCorpse.setWasFakeDead(true);
        }
        else {
            reanimatedCorpse.setReanimatedPlayer(true);
            reanimatedCorpse.getDescriptor().setID(0);
            SharedDescriptors.createPlayerZombieDescriptor(reanimatedCorpse);
        }
        reanimatedCorpse.setReanimate(this.bCrawling);
        if (!IsoWorld.instance.CurrentCell.getZombieList().contains(reanimatedCorpse)) {
            IsoWorld.instance.CurrentCell.getZombieList().add(reanimatedCorpse);
        }
        if (!IsoWorld.instance.CurrentCell.getObjectList().contains(reanimatedCorpse) && !IsoWorld.instance.CurrentCell.getAddList().contains(reanimatedCorpse)) {
            IsoWorld.instance.CurrentCell.getAddList().add(reanimatedCorpse);
        }
        if (GameServer.bServer) {
            if (this.player != null) {
                this.player.ReanimatedCorpse = reanimatedCorpse;
                this.player.ReanimatedCorpseID = reanimatedCorpse.OnlineID;
            }
            GameServer.sendRemoveCorpseFromMap(this);
        }
        this.removeFromWorld();
        this.removeFromSquare();
        LuaEventManager.triggerEvent("OnContainerUpdate");
        reanimatedCorpse.setReanimateTimer(0.0f);
        reanimatedCorpse.onWornItemsChanged();
        if (this.player != null) {
            if (GameServer.bServer) {
                GameServer.sendReanimatedZombieID(this.player, reanimatedCorpse);
            }
            else if (!GameClient.bClient && this.player.isLocalPlayer()) {
                this.player.ReanimatedCorpse = reanimatedCorpse;
            }
            this.player.setLeaveBodyTimedown(3601.0f);
        }
        reanimatedCorpse.actionContext.update();
        final float fpsMultiplier = GameTime.getInstance().FPSMultiplier;
        GameTime.getInstance().FPSMultiplier = 100.0f;
        try {
            reanimatedCorpse.advancedAnimator.update();
        }
        finally {
            GameTime.getInstance().FPSMultiplier = fpsMultiplier;
        }
        if (this.isFakeDead() && SoundManager.instance.isListenerInRange(this.x, this.y, 20.0f) && !GameServer.bServer) {
            reanimatedCorpse.parameterZombieState.setState(ParameterZombieState.State.Reanimate);
        }
        if (Core.bDebug) {
            DebugLog.log(DebugType.Death, String.format("Corpse reanimate: Corpse(%d) Zombie(%d): items=%d", this.getOnlineID(), reanimatedCorpse.getOnlineID(), reanimatedCorpse.getInventory().getItems().size()));
        }
    }
    
    public static void Reset() {
        IsoDeadBody.AllBodies.clear();
    }
    
    @Override
    public void Collision(final Vector2 vector2, final IsoObject isoObject) {
        if (isoObject instanceof BaseVehicle) {
            final BaseVehicle baseVehicle = (BaseVehicle)isoObject;
            final float b = 15.0f;
            final Vector3f vector3f = BaseVehicle.TL_vector3f_pool.get().alloc();
            final Vector3f vector3f2 = BaseVehicle.TL_vector3f_pool.get().alloc();
            baseVehicle.getLinearVelocity(vector3f);
            vector3f.y = 0.0f;
            vector3f2.set(baseVehicle.x - this.x, 0.0f, baseVehicle.z - this.z);
            vector3f2.normalize();
            vector3f.mul((Vector3fc)vector3f2);
            BaseVehicle.TL_vector3f_pool.get().release(vector3f2);
            final float length = vector3f.length();
            BaseVehicle.TL_vector3f_pool.get().release(vector3f);
            if (Math.min(length, b) < 0.05f) {
                return;
            }
            if (Math.abs(baseVehicle.getCurrentSpeedKmHour()) > 20.0f) {
                baseVehicle.doChrHitImpulse(this);
            }
        }
    }
    
    public boolean isFallOnFront() {
        return this.fallOnFront;
    }
    
    public void setFallOnFront(final boolean fallOnFront) {
        this.fallOnFront = fallOnFront;
    }
    
    public InventoryItem getPrimaryHandItem() {
        return this.primaryHandItem;
    }
    
    public void setPrimaryHandItem(final InventoryItem primaryHandItem) {
        this.primaryHandItem = primaryHandItem;
        this.updateContainerWithHandItems();
    }
    
    private void updateContainerWithHandItems() {
        if (this.getContainer() != null) {
            if (this.getPrimaryHandItem() != null) {
                this.getContainer().AddItem(this.getPrimaryHandItem());
            }
            if (this.getSecondaryHandItem() != null) {
                this.getContainer().AddItem(this.getSecondaryHandItem());
            }
        }
    }
    
    public InventoryItem getSecondaryHandItem() {
        return this.secondaryHandItem;
    }
    
    public void setSecondaryHandItem(final InventoryItem secondaryHandItem) {
        this.secondaryHandItem = secondaryHandItem;
        this.updateContainerWithHandItems();
    }
    
    public float getAngle() {
        return this.m_angle;
    }
    
    public String getOutfitName() {
        if (this.getHumanVisual().getOutfit() != null) {
            return this.getHumanVisual().getOutfit().m_Name;
        }
        return null;
    }
    
    private String getDescription() {
        return String.format("Corpse: id=%d bFakeDead=%b bCrawling=%b isFallOnFront=%b (x=%f,y=%f,z=%f;a=%f) outfit=%d", this.onlineID, this.bFakeDead, this.bCrawling, this.fallOnFront, this.x, this.y, this.z, this.m_angle, this.m_persistentOutfitID);
    }
    
    public String readInventory(final ByteBuffer byteBuffer) {
        final String readString = GameWindow.ReadString(byteBuffer);
        if (this.getContainer() == null || this.getWornItems() == null || this.getAttachedItems() == null) {
            return readString;
        }
        this.getContainer().clear();
        this.getWornItems().clear();
        this.getAttachedItems().clear();
        if (byteBuffer.get() == 1) {
            try {
                final ArrayList<InventoryItem> load = this.getContainer().load(byteBuffer, IsoWorld.getWorldVersion());
                this.getContainer().Capacity = 8;
                for (byte value = byteBuffer.get(), b = 0; b < value; ++b) {
                    final String readStringUTF = GameWindow.ReadStringUTF(byteBuffer);
                    final short short1 = byteBuffer.getShort();
                    if (short1 >= 0 && short1 < load.size() && this.getWornItems().getBodyLocationGroup().getLocation(readStringUTF) != null) {
                        this.getWornItems().setItem(readStringUTF, load.get(short1));
                    }
                }
                for (byte value2 = byteBuffer.get(), b2 = 0; b2 < value2; ++b2) {
                    final String readStringUTF2 = GameWindow.ReadStringUTF(byteBuffer);
                    final short short2 = byteBuffer.getShort();
                    if (short2 >= 0 && short2 < load.size() && this.getAttachedItems().getGroup().getLocation(readStringUTF2) != null) {
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
    
    static {
        tempZombie = new ThreadLocal<IsoZombie>() {
            public IsoZombie initialValue() {
                return new IsoZombie(null);
            }
        };
        IsoDeadBody.inf = new ColorInfo();
        IsoDeadBody.DropShadow = null;
        _rotation = new Quaternionf();
        _transform = new Transform();
        _UNIT_Z = new Vector3f(0.0f, 0.0f, 1.0f);
        _tempVec3f_1 = new Vector3f();
        _tempVec3f_2 = new Vector3f();
        IsoDeadBody.AllBodies = new ArrayList<IsoDeadBody>(256);
        ClientBodies = new ConcurrentHashMap<Short, IsoDeadBody>();
    }
}

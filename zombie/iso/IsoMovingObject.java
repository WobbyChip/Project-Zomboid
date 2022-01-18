// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import fmod.fmod.Audio;
import zombie.SoundManager;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.Collection;
import zombie.network.ServerMap;
import zombie.util.StringUtils;
import zombie.iso.objects.IsoThumpable;
import zombie.ai.states.CrawlingZombieTurnState;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.ai.states.AttackState;
import zombie.vehicles.PathFindBehavior2;
import zombie.iso.objects.IsoMolotovCocktail;
import zombie.ai.states.StaggerBackState;
import zombie.ai.states.ZombieFallDownState;
import zombie.debug.LineDrawer;
import zombie.debug.DebugOptions;
import zombie.core.Core;
import zombie.core.skinnedmodel.model.Model;
import zombie.characters.BodyDamage.BodyPart;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.characters.skills.PerkFactory;
import zombie.network.ServerOptions;
import zombie.network.GameClient;
import zombie.inventory.types.WeaponType;
import zombie.CollisionManager;
import zombie.vehicles.BaseVehicle;
import zombie.iso.objects.IsoZombieGiblets;
import zombie.characters.IsoSurvivor;
import org.joml.Vector2f;
import zombie.ai.states.CollideWithWallState;
import zombie.util.Type;
import zombie.vehicles.PolygonalMap2;
import zombie.popman.ZombiePopulationManager;
import zombie.ai.states.WalkTowardState;
import zombie.ai.states.PathFindState;
import zombie.ai.states.ZombieIdleState;
import zombie.core.Rand;
import zombie.network.GameServer;
import zombie.iso.objects.RenderEffectType;
import zombie.iso.objects.IsoTree;
import zombie.MovingObjectUpdateScheduler;
import java.io.IOException;
import zombie.Lua.LuaManager;
import java.nio.ByteBuffer;
import zombie.inventory.types.HandWeapon;
import zombie.debug.DebugLog;
import zombie.ai.State;
import zombie.GameTime;
import zombie.ai.states.ClimbThroughWindowState;
import zombie.ai.states.ClimbOverFenceState;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.Lua.LuaEventManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.iso.areas.isoregion.regions.IWorldRegion;
import zombie.iso.areas.IsoRoom;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import java.util.ArrayList;
import zombie.core.textures.ColorInfo;
import zombie.characters.IsoZombie;
import zombie.iso.objects.interfaces.Thumpable;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.ai.astar.Mover;

public class IsoMovingObject extends IsoObject implements Mover
{
    public static TreeSoundManager treeSoundMgr;
    public static final int MAX_ZOMBIES_EATING = 3;
    private static int IDCount;
    private static final Vector2 tempo;
    public boolean noDamage;
    public IsoGridSquare last;
    public float lx;
    public float ly;
    public float lz;
    public float nx;
    public float ny;
    public float x;
    public float y;
    public float z;
    public IsoSpriteInstance def;
    protected IsoGridSquare current;
    protected Vector2 hitDir;
    protected int ID;
    protected IsoGridSquare movingSq;
    protected boolean solid;
    protected float width;
    protected boolean shootable;
    protected boolean Collidable;
    protected float scriptnx;
    protected float scriptny;
    protected String ScriptModule;
    protected Vector2 movementLastFrame;
    protected float weight;
    boolean bOnFloor;
    private boolean closeKilled;
    private String collideType;
    private float lastCollideTime;
    private int TimeSinceZombieAttack;
    private boolean collidedE;
    private boolean collidedN;
    private IsoObject CollidedObject;
    private boolean collidedS;
    private boolean collidedThisFrame;
    private boolean collidedW;
    private boolean CollidedWithDoor;
    private boolean collidedWithVehicle;
    private boolean destroyed;
    private boolean firstUpdate;
    private float impulsex;
    private float impulsey;
    private float limpulsex;
    private float limpulsey;
    private float hitForce;
    private float hitFromAngle;
    private int PathFindIndex;
    private float StateEventDelayTimer;
    private Thumpable thumpTarget;
    private boolean bAltCollide;
    private IsoZombie lastTargettedBy;
    private float feelersize;
    public final boolean[] bOutline;
    public final ColorInfo[] outlineColor;
    private final ArrayList<IsoZombie> eatingZombies;
    private boolean zombiesDontAttack;
    
    public IsoMovingObject(final IsoCell isoCell) {
        this.noDamage = false;
        this.last = null;
        this.def = null;
        this.current = null;
        this.hitDir = new Vector2();
        this.ID = 0;
        this.movingSq = null;
        this.solid = true;
        this.width = 0.24f;
        this.shootable = true;
        this.Collidable = true;
        this.scriptnx = 0.0f;
        this.scriptny = 0.0f;
        this.ScriptModule = "none";
        this.movementLastFrame = new Vector2();
        this.weight = 1.0f;
        this.bOnFloor = false;
        this.closeKilled = false;
        this.collideType = null;
        this.lastCollideTime = 0.0f;
        this.TimeSinceZombieAttack = 1000000;
        this.collidedE = false;
        this.collidedN = false;
        this.CollidedObject = null;
        this.collidedS = false;
        this.collidedThisFrame = false;
        this.collidedW = false;
        this.CollidedWithDoor = false;
        this.collidedWithVehicle = false;
        this.destroyed = false;
        this.firstUpdate = true;
        this.impulsex = 0.0f;
        this.impulsey = 0.0f;
        this.limpulsex = 0.0f;
        this.limpulsey = 0.0f;
        this.hitForce = 0.0f;
        this.PathFindIndex = -1;
        this.StateEventDelayTimer = 0.0f;
        this.thumpTarget = null;
        this.bAltCollide = false;
        this.lastTargettedBy = null;
        this.feelersize = 0.5f;
        this.bOutline = new boolean[4];
        this.outlineColor = new ColorInfo[4];
        this.eatingZombies = new ArrayList<IsoZombie>();
        this.zombiesDontAttack = false;
        this.sprite = IsoSprite.CreateSprite(IsoSpriteManager.instance);
        if (isoCell == null) {
            return;
        }
        this.ID = IsoMovingObject.IDCount++;
        if (this.getCell().isSafeToAdd()) {
            this.getCell().getObjectList().add(this);
        }
        else {
            this.getCell().getAddList().add(this);
        }
    }
    
    public IsoMovingObject(final IsoCell isoCell, final boolean b) {
        this.noDamage = false;
        this.last = null;
        this.def = null;
        this.current = null;
        this.hitDir = new Vector2();
        this.ID = 0;
        this.movingSq = null;
        this.solid = true;
        this.width = 0.24f;
        this.shootable = true;
        this.Collidable = true;
        this.scriptnx = 0.0f;
        this.scriptny = 0.0f;
        this.ScriptModule = "none";
        this.movementLastFrame = new Vector2();
        this.weight = 1.0f;
        this.bOnFloor = false;
        this.closeKilled = false;
        this.collideType = null;
        this.lastCollideTime = 0.0f;
        this.TimeSinceZombieAttack = 1000000;
        this.collidedE = false;
        this.collidedN = false;
        this.CollidedObject = null;
        this.collidedS = false;
        this.collidedThisFrame = false;
        this.collidedW = false;
        this.CollidedWithDoor = false;
        this.collidedWithVehicle = false;
        this.destroyed = false;
        this.firstUpdate = true;
        this.impulsex = 0.0f;
        this.impulsey = 0.0f;
        this.limpulsex = 0.0f;
        this.limpulsey = 0.0f;
        this.hitForce = 0.0f;
        this.PathFindIndex = -1;
        this.StateEventDelayTimer = 0.0f;
        this.thumpTarget = null;
        this.bAltCollide = false;
        this.lastTargettedBy = null;
        this.feelersize = 0.5f;
        this.bOutline = new boolean[4];
        this.outlineColor = new ColorInfo[4];
        this.eatingZombies = new ArrayList<IsoZombie>();
        this.zombiesDontAttack = false;
        this.ID = IsoMovingObject.IDCount++;
        this.sprite = IsoSprite.CreateSprite(IsoSpriteManager.instance);
        if (b) {
            if (this.getCell().isSafeToAdd()) {
                this.getCell().getObjectList().add(this);
            }
            else {
                this.getCell().getAddList().add(this);
            }
        }
    }
    
    public IsoMovingObject(final IsoCell isoCell, final IsoGridSquare isoGridSquare, final IsoSprite sprite, final boolean b) {
        this.noDamage = false;
        this.last = null;
        this.def = null;
        this.current = null;
        this.hitDir = new Vector2();
        this.ID = 0;
        this.movingSq = null;
        this.solid = true;
        this.width = 0.24f;
        this.shootable = true;
        this.Collidable = true;
        this.scriptnx = 0.0f;
        this.scriptny = 0.0f;
        this.ScriptModule = "none";
        this.movementLastFrame = new Vector2();
        this.weight = 1.0f;
        this.bOnFloor = false;
        this.closeKilled = false;
        this.collideType = null;
        this.lastCollideTime = 0.0f;
        this.TimeSinceZombieAttack = 1000000;
        this.collidedE = false;
        this.collidedN = false;
        this.CollidedObject = null;
        this.collidedS = false;
        this.collidedThisFrame = false;
        this.collidedW = false;
        this.CollidedWithDoor = false;
        this.collidedWithVehicle = false;
        this.destroyed = false;
        this.firstUpdate = true;
        this.impulsex = 0.0f;
        this.impulsey = 0.0f;
        this.limpulsex = 0.0f;
        this.limpulsey = 0.0f;
        this.hitForce = 0.0f;
        this.PathFindIndex = -1;
        this.StateEventDelayTimer = 0.0f;
        this.thumpTarget = null;
        this.bAltCollide = false;
        this.lastTargettedBy = null;
        this.feelersize = 0.5f;
        this.bOutline = new boolean[4];
        this.outlineColor = new ColorInfo[4];
        this.eatingZombies = new ArrayList<IsoZombie>();
        this.zombiesDontAttack = false;
        this.ID = IsoMovingObject.IDCount++;
        this.sprite = sprite;
        if (b) {
            if (this.getCell().isSafeToAdd()) {
                this.getCell().getObjectList().add(this);
            }
            else {
                this.getCell().getAddList().add(this);
            }
        }
    }
    
    public IsoMovingObject() {
        this.noDamage = false;
        this.last = null;
        this.def = null;
        this.current = null;
        this.hitDir = new Vector2();
        this.ID = 0;
        this.movingSq = null;
        this.solid = true;
        this.width = 0.24f;
        this.shootable = true;
        this.Collidable = true;
        this.scriptnx = 0.0f;
        this.scriptny = 0.0f;
        this.ScriptModule = "none";
        this.movementLastFrame = new Vector2();
        this.weight = 1.0f;
        this.bOnFloor = false;
        this.closeKilled = false;
        this.collideType = null;
        this.lastCollideTime = 0.0f;
        this.TimeSinceZombieAttack = 1000000;
        this.collidedE = false;
        this.collidedN = false;
        this.CollidedObject = null;
        this.collidedS = false;
        this.collidedThisFrame = false;
        this.collidedW = false;
        this.CollidedWithDoor = false;
        this.collidedWithVehicle = false;
        this.destroyed = false;
        this.firstUpdate = true;
        this.impulsex = 0.0f;
        this.impulsey = 0.0f;
        this.limpulsex = 0.0f;
        this.limpulsey = 0.0f;
        this.hitForce = 0.0f;
        this.PathFindIndex = -1;
        this.StateEventDelayTimer = 0.0f;
        this.thumpTarget = null;
        this.bAltCollide = false;
        this.lastTargettedBy = null;
        this.feelersize = 0.5f;
        this.bOutline = new boolean[4];
        this.outlineColor = new ColorInfo[4];
        this.eatingZombies = new ArrayList<IsoZombie>();
        this.zombiesDontAttack = false;
        this.ID = IsoMovingObject.IDCount++;
        this.getCell().getAddList().add(this);
    }
    
    public static int getIDCount() {
        return IsoMovingObject.IDCount;
    }
    
    public static void setIDCount(final int idCount) {
        IsoMovingObject.IDCount = idCount;
    }
    
    public IsoBuilding getBuilding() {
        if (this.current == null) {
            return null;
        }
        final IsoRoom room = this.current.getRoom();
        if (room == null) {
            return null;
        }
        return room.building;
    }
    
    public IWorldRegion getMasterRegion() {
        if (this.current != null) {
            return this.current.getIsoWorldRegion();
        }
        return null;
    }
    
    public float getWeight() {
        return this.weight;
    }
    
    public void setWeight(final float weight) {
        this.weight = weight;
    }
    
    public float getWeight(final float n, final float n2) {
        return this.weight;
    }
    
    @Override
    public void onMouseRightClick(final int n, final int n2) {
        if (this.square.getZ() == (int)IsoPlayer.getInstance().getZ() && this.DistToProper(IsoPlayer.getInstance()) <= 2.0f) {
            IsoPlayer.getInstance().setDragObject(this);
        }
    }
    
    @Override
    public String getObjectName() {
        return "IsoMovingObject";
    }
    
    @Override
    public void onMouseRightReleased() {
    }
    
    public void collideWith(final IsoObject isoObject) {
        if (this instanceof IsoGameCharacter && isoObject instanceof IsoGameCharacter) {
            LuaEventManager.triggerEvent("OnCharacterCollide", this, isoObject);
        }
        else {
            LuaEventManager.triggerEvent("OnObjectCollide", this, isoObject);
        }
    }
    
    public void doStairs() {
        if (this.current == null) {
            return;
        }
        if (this.last == null) {
            return;
        }
        if (this instanceof IsoPhysicsObject) {
            return;
        }
        IsoGridSquare current = this.current;
        if (current.z > 0 && (current.Has(IsoObjectType.stairsTN) || current.Has(IsoObjectType.stairsTW)) && this.z - (int)this.z < 0.1f) {
            final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(current.x, current.y, current.z - 1);
            if (gridSquare != null && (gridSquare.Has(IsoObjectType.stairsTN) || gridSquare.Has(IsoObjectType.stairsTW))) {
                current = gridSquare;
            }
        }
        if (this instanceof IsoGameCharacter && (this.last.Has(IsoObjectType.stairsTN) || this.last.Has(IsoObjectType.stairsTW))) {
            this.z = (float)Math.round(this.z);
        }
        float n = this.z;
        if (current.HasStairs()) {
            n = current.getApparentZ(this.x - current.getX(), this.y - current.getY());
        }
        if (this instanceof IsoGameCharacter) {
            final State currentState = ((IsoGameCharacter)this).getCurrentState();
            if (currentState == ClimbOverFenceState.instance() || currentState == ClimbThroughWindowState.instance()) {
                if (current.HasStairs() && this.z > n) {
                    this.z = Math.max(n, this.z - 0.075f * GameTime.getInstance().getMultiplier());
                }
                return;
            }
        }
        if (Math.abs(n - this.z) < 0.95f) {
            this.z = n;
        }
    }
    
    @Override
    public int getID() {
        return this.ID;
    }
    
    public void setID(final int id) {
        this.ID = id;
    }
    
    @Override
    public int getPathFindIndex() {
        return this.PathFindIndex;
    }
    
    public void setPathFindIndex(final int pathFindIndex) {
        this.PathFindIndex = pathFindIndex;
    }
    
    public float getScreenX() {
        return IsoUtils.XToScreen(this.x, this.y, this.z, 0);
    }
    
    public float getScreenY() {
        return IsoUtils.YToScreen(this.x, this.y, this.z, 0);
    }
    
    public Thumpable getThumpTarget() {
        return this.thumpTarget;
    }
    
    public void setThumpTarget(final Thumpable thumpTarget) {
        this.thumpTarget = thumpTarget;
    }
    
    public Vector2 getVectorFromDirection(final Vector2 vector2) {
        return getVectorFromDirection(vector2, this.dir);
    }
    
    public static Vector2 getVectorFromDirection(Vector2 vector2, final IsoDirections isoDirections) {
        if (vector2 == null) {
            DebugLog.General.warn((Object)"Supplied vector2 is null. Cannot be processed. Using fail-safe fallback.");
            vector2 = new Vector2();
        }
        vector2.x = 0.0f;
        vector2.y = 0.0f;
        switch (isoDirections) {
            case S: {
                vector2.x = 0.0f;
                vector2.y = 1.0f;
                break;
            }
            case N: {
                vector2.x = 0.0f;
                vector2.y = -1.0f;
                break;
            }
            case E: {
                vector2.x = 1.0f;
                vector2.y = 0.0f;
                break;
            }
            case W: {
                vector2.x = -1.0f;
                vector2.y = 0.0f;
                break;
            }
            case NW: {
                vector2.x = -1.0f;
                vector2.y = -1.0f;
                break;
            }
            case NE: {
                vector2.x = 1.0f;
                vector2.y = -1.0f;
                break;
            }
            case SW: {
                vector2.x = -1.0f;
                vector2.y = 1.0f;
                break;
            }
            case SE: {
                vector2.x = 1.0f;
                vector2.y = 1.0f;
                break;
            }
        }
        vector2.normalize();
        return vector2;
    }
    
    public Vector3 getPosition(final Vector3 vector3) {
        vector3.set(this.getX(), this.getY(), this.getZ());
        return vector3;
    }
    
    @Override
    public float getX() {
        return this.x;
    }
    
    public void setX(final float scriptnx) {
        this.x = scriptnx;
        this.nx = scriptnx;
        this.scriptnx = scriptnx;
    }
    
    @Override
    public float getY() {
        return this.y;
    }
    
    public void setY(final float scriptny) {
        this.y = scriptny;
        this.ny = scriptny;
        this.scriptny = scriptny;
    }
    
    @Override
    public float getZ() {
        return this.z;
    }
    
    public void setZ(final float n) {
        this.z = n;
        this.lz = n;
    }
    
    @Override
    public IsoGridSquare getSquare() {
        if (this.current != null) {
            return this.current;
        }
        return this.square;
    }
    
    public IsoBuilding getCurrentBuilding() {
        if (this.current == null) {
            return null;
        }
        if (this.current.getRoom() == null) {
            return null;
        }
        return this.current.getRoom().building;
    }
    
    public float Hit(final HandWeapon handWeapon, final IsoGameCharacter isoGameCharacter, final float n, final boolean b, final float n2) {
        return 0.0f;
    }
    
    public void Move(final Vector2 vector2) {
        this.nx += vector2.x * GameTime.instance.getMultiplier();
        this.ny += vector2.y * GameTime.instance.getMultiplier();
        if (this instanceof IsoPlayer) {
            this.current = IsoWorld.instance.CurrentCell.getGridSquare(this.x, this.y, (int)this.z);
        }
    }
    
    public void MoveUnmodded(final Vector2 vector2) {
        this.nx += vector2.x;
        this.ny += vector2.y;
        if (this instanceof IsoPlayer) {
            this.current = IsoWorld.instance.CurrentCell.getGridSquare(this.x, this.y, (int)this.z);
        }
    }
    
    @Override
    public boolean isCharacter() {
        return this instanceof IsoGameCharacter;
    }
    
    public float DistTo(final int n, final int n2) {
        return IsoUtils.DistanceManhatten((float)n, (float)n2, this.x, this.y);
    }
    
    public float DistTo(final IsoMovingObject isoMovingObject) {
        return IsoUtils.DistanceManhatten(this.x, this.y, isoMovingObject.x, isoMovingObject.y);
    }
    
    public float DistToProper(final IsoObject isoObject) {
        return IsoUtils.DistanceTo(this.x, this.y, isoObject.getX(), isoObject.getY());
    }
    
    public float DistToSquared(final IsoMovingObject isoMovingObject) {
        return IsoUtils.DistanceToSquared(this.x, this.y, isoMovingObject.x, isoMovingObject.y);
    }
    
    public float DistToSquared(final float n, final float n2) {
        return IsoUtils.DistanceToSquared(n, n2, this.x, this.y);
    }
    
    @Override
    public void load(final ByteBuffer byteBuffer, final int n, final boolean b) throws IOException {
        byteBuffer.getFloat();
        byteBuffer.getFloat();
        final float n2 = byteBuffer.getFloat() + IsoWorld.saveoffsetx * 300;
        this.scriptnx = n2;
        this.nx = n2;
        this.lx = n2;
        this.x = n2;
        final float n3 = byteBuffer.getFloat() + IsoWorld.saveoffsety * 300;
        this.scriptny = n3;
        this.ny = n3;
        this.ly = n3;
        this.y = n3;
        final float float1 = byteBuffer.getFloat();
        this.lz = float1;
        this.z = float1;
        this.dir = IsoDirections.fromIndex(byteBuffer.getInt());
        if (byteBuffer.get() != 0) {
            if (this.table == null) {
                this.table = LuaManager.platform.newTable();
            }
            this.table.load(byteBuffer, n);
        }
    }
    
    @Override
    public void save(final ByteBuffer byteBuffer, final boolean b) throws IOException {
        byteBuffer.put((byte)(this.Serialize() ? 1 : 0));
        byteBuffer.put(IsoObject.factoryGetClassID(this.getObjectName()));
        byteBuffer.putFloat(this.offsetX);
        byteBuffer.putFloat(this.offsetY);
        byteBuffer.putFloat(this.x);
        byteBuffer.putFloat(this.y);
        byteBuffer.putFloat(this.z);
        byteBuffer.putInt(this.dir.index());
        if (this.table != null && !this.table.isEmpty()) {
            byteBuffer.put((byte)1);
            this.table.save(byteBuffer);
        }
        else {
            byteBuffer.put((byte)0);
        }
    }
    
    @Override
    public void removeFromWorld() {
        final IsoCell cell = this.getCell();
        if (cell.isSafeToAdd()) {
            cell.getObjectList().remove(this);
            cell.getRemoveList().remove(this);
        }
        else {
            cell.getRemoveList().add(this);
        }
        cell.getAddList().remove(this);
        MovingObjectUpdateScheduler.instance.removeObject(this);
        super.removeFromWorld();
    }
    
    @Override
    public void removeFromSquare() {
        if (this.current != null) {
            this.current.getMovingObjects().remove(this);
        }
        if (this.last != null) {
            this.last.getMovingObjects().remove(this);
        }
        if (this.movingSq != null) {
            this.movingSq.getMovingObjects().remove(this);
        }
        final IsoGridSquare current = null;
        this.movingSq = current;
        this.last = current;
        this.current = current;
        if (this.square != null) {
            this.square.getStaticMovingObjects().remove(this);
        }
        super.removeFromSquare();
    }
    
    public IsoGridSquare getFuturWalkedSquare() {
        if (this.current != null) {
            final IsoGridSquare feelerTile = this.getFeelerTile(this.feelersize);
            if (feelerTile != null && feelerTile != this.current) {
                return feelerTile;
            }
        }
        return null;
    }
    
    public float getGlobalMovementMod() {
        return this.getGlobalMovementMod(true);
    }
    
    public float getGlobalMovementMod(final boolean b) {
        if (this.current != null && this.z - (int)this.z < 0.5f) {
            if (this.current.Has(IsoObjectType.tree) || (this.current.getProperties() != null && this.current.getProperties().Is("Bush"))) {
                if (b) {
                    this.doTreeNoises();
                }
                for (int i = 1; i < this.current.getObjects().size(); ++i) {
                    final IsoObject isoObject = this.current.getObjects().get(i);
                    if (isoObject instanceof IsoTree) {
                        isoObject.setRenderEffect(RenderEffectType.Vegetation_Rustle);
                    }
                    else if (isoObject.getProperties() != null && isoObject.getProperties().Is("Bush")) {
                        isoObject.setRenderEffect(RenderEffectType.Vegetation_Rustle);
                    }
                }
            }
            final IsoGridSquare feelerTile = this.getFeelerTile(this.feelersize);
            if (feelerTile != null && feelerTile != this.current && (feelerTile.Has(IsoObjectType.tree) || (feelerTile.getProperties() != null && feelerTile.getProperties().Is("Bush")))) {
                if (b) {
                    this.doTreeNoises();
                }
                for (int j = 1; j < feelerTile.getObjects().size(); ++j) {
                    final IsoObject isoObject2 = feelerTile.getObjects().get(j);
                    if (isoObject2 instanceof IsoTree) {
                        isoObject2.setRenderEffect(RenderEffectType.Vegetation_Rustle);
                    }
                    else if (isoObject2.getSprite() != null && isoObject2.getProperties().Is("Bush")) {
                        isoObject2.setRenderEffect(RenderEffectType.Vegetation_Rustle);
                    }
                }
            }
        }
        if (this.current != null && this.current.HasStairs()) {
            return 0.75f;
        }
        return 1.0f;
    }
    
    private void doTreeNoises() {
        if (GameServer.bServer) {
            return;
        }
        if (this instanceof IsoPhysicsObject) {
            return;
        }
        if (this.current == null) {
            return;
        }
        if (Rand.Next(Rand.AdjustForFramerate(50)) != 0) {
            return;
        }
        IsoMovingObject.treeSoundMgr.addSquare(this.current);
    }
    
    public void postupdate() {
        this.slideAwayFromWalls();
        if (this instanceof IsoZombie && GameServer.bServer && ((IsoZombie)this).getStateMachine().getCurrent() != ZombieIdleState.instance()) {}
        if (this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
            IsoPlayer.setInstance((IsoPlayer)this);
            IsoCamera.CamCharacter = (IsoPlayer)this;
        }
        this.ensureOnTile();
        if (this.lastTargettedBy != null && this.lastTargettedBy.isDead()) {
            this.lastTargettedBy = null;
        }
        if (this.lastTargettedBy != null && this.TimeSinceZombieAttack > 120) {
            this.lastTargettedBy = null;
        }
        ++this.TimeSinceZombieAttack;
        if (this instanceof IsoPlayer) {
            ((IsoPlayer)this).setLastCollidedW(this.collidedW);
            ((IsoPlayer)this).setLastCollidedN(this.collidedN);
            final IsoPlayer isoPlayer = (IsoPlayer)this;
        }
        if (this.destroyed) {
            return;
        }
        this.collidedThisFrame = false;
        this.collidedN = false;
        this.collidedS = false;
        this.collidedW = false;
        this.collidedE = false;
        this.CollidedWithDoor = false;
        this.last = this.current;
        this.CollidedObject = null;
        this.nx += this.impulsex;
        this.ny += this.impulsey;
        if (this.nx < 0.0f) {
            this.nx = 0.0f;
        }
        if (this.ny < 0.0f) {
            this.ny = 0.0f;
        }
        IsoMovingObject.tempo.set(this.nx - this.x, this.ny - this.y);
        if (IsoMovingObject.tempo.getLength() > 1.0f) {
            IsoMovingObject.tempo.normalize();
            this.nx = this.x + IsoMovingObject.tempo.getX();
            this.ny = this.y + IsoMovingObject.tempo.getY();
        }
        this.impulsex = 0.0f;
        this.impulsey = 0.0f;
        if (this instanceof IsoZombie && (int)this.z == 0 && this.getCurrentBuilding() == null && !this.isInLoadedArea((int)this.nx, (int)this.ny) && (((IsoZombie)this).getCurrentState() == PathFindState.instance() || ((IsoZombie)this).getCurrentState() == WalkTowardState.instance())) {
            ZombiePopulationManager.instance.virtualizeZombie((IsoZombie)this);
            return;
        }
        final float nx = this.nx;
        final float ny = this.ny;
        this.collidedWithVehicle = false;
        if (this instanceof IsoGameCharacter && !this.isOnFloor() && ((IsoGameCharacter)this).getVehicle() == null && this.isCollidable() && (!(this instanceof IsoPlayer) || !((IsoPlayer)this).isNoClip())) {
            final Vector2f resolveCollision = PolygonalMap2.instance.resolveCollision((IsoGameCharacter)this, this.nx, this.ny, L_postUpdate.vector2f);
            if (resolveCollision.x != this.nx || resolveCollision.y != this.ny) {
                this.nx = resolveCollision.x;
                this.ny = resolveCollision.y;
                this.collidedWithVehicle = true;
            }
        }
        final float nx2 = this.nx;
        final float ny2 = this.ny;
        boolean b = false;
        if (this.Collidable) {
            if (this.bAltCollide) {
                this.DoCollide(2);
            }
            else {
                this.DoCollide(1);
            }
            if (this.collidedN || this.collidedS) {
                this.ny = this.ly;
                this.DoCollideNorS();
            }
            if (this.collidedW || this.collidedE) {
                this.nx = this.lx;
                this.DoCollideWorE();
            }
            if (this.bAltCollide) {
                this.DoCollide(1);
            }
            else {
                this.DoCollide(2);
            }
            this.bAltCollide = !this.bAltCollide;
            if (this.collidedN || this.collidedS) {
                this.ny = this.ly;
                this.DoCollideNorS();
                b = true;
            }
            if (this.collidedW || this.collidedE) {
                this.nx = this.lx;
                this.DoCollideWorE();
                b = true;
            }
            final float n = Math.abs(this.nx - this.lx) + Math.abs(this.ny - this.ly);
            final float nx3 = this.nx;
            final float ny3 = this.ny;
            this.nx = nx2;
            this.ny = ny2;
            if (this.Collidable && b) {
                if (this.bAltCollide) {
                    this.DoCollide(2);
                }
                else {
                    this.DoCollide(1);
                }
                if (this.collidedN || this.collidedS) {
                    this.ny = this.ly;
                    this.DoCollideNorS();
                }
                if (this.collidedW || this.collidedE) {
                    this.nx = this.lx;
                    this.DoCollideWorE();
                }
                if (this.bAltCollide) {
                    this.DoCollide(1);
                }
                else {
                    this.DoCollide(2);
                }
                if (this.collidedN || this.collidedS) {
                    this.ny = this.ly;
                    this.DoCollideNorS();
                }
                if (this.collidedW || this.collidedE) {
                    this.nx = this.lx;
                    this.DoCollideWorE();
                }
                if (Math.abs(this.nx - this.lx) + Math.abs(this.ny - this.ly) < n) {
                    this.nx = nx3;
                    this.ny = ny3;
                }
            }
        }
        if (this.collidedThisFrame) {
            this.current = this.last;
        }
        this.checkHitWall();
        final IsoPlayer isoPlayer2 = Type.tryCastTo(this, IsoPlayer.class);
        if (isoPlayer2 != null && !isoPlayer2.isCurrentState(CollideWithWallState.instance()) && !this.collidedN && !this.collidedS && !this.collidedW && !this.collidedE) {
            this.setCollideType(null);
        }
        float n2 = this.nx - this.x;
        float n3 = this.ny - this.y;
        final float n4 = (Math.abs(n2) > 0.0f || Math.abs(n3) > 0.0f) ? this.getGlobalMovementMod() : 0.0f;
        if (Math.abs(n2) > 0.01f || Math.abs(n3) > 0.01f) {
            n2 *= n4;
            n3 *= n4;
        }
        this.x += n2;
        this.y += n3;
        this.doStairs();
        this.current = this.getCell().getGridSquare((int)this.x, (int)this.y, (int)this.z);
        if (this.current == null) {
            for (int i = (int)this.z; i >= 0; --i) {
                this.current = this.getCell().getGridSquare((int)this.x, (int)this.y, i);
                if (this.current != null) {
                    break;
                }
            }
            if (this.current == null && this.last != null) {
                this.current = this.last;
                final float x = this.current.getX() + 0.5f;
                this.scriptnx = x;
                this.nx = x;
                this.x = x;
                final float y = this.current.getY() + 0.5f;
                this.scriptny = y;
                this.ny = y;
                this.y = y;
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
        this.ensureOnTile();
        this.square = this.current;
        this.scriptnx = this.nx;
        this.scriptny = this.ny;
        this.firstUpdate = false;
    }
    
    public void ensureOnTile() {
        if (this.current == null) {
            if (!(this instanceof IsoPlayer)) {
                if (this instanceof IsoSurvivor) {
                    IsoWorld.instance.CurrentCell.Remove(this);
                    IsoWorld.instance.CurrentCell.getSurvivorList().remove(this);
                }
                return;
            }
            boolean b = true;
            if (this.last != null && (this.last.Has(IsoObjectType.stairsTN) || this.last.Has(IsoObjectType.stairsTW))) {
                this.current = this.getCell().getGridSquare((int)this.x, (int)this.y, (int)this.z + 1);
                b = false;
            }
            if (this.current == null) {
                this.current = this.getCell().getGridSquare((int)this.x, (int)this.y, (int)this.z);
                return;
            }
            if (b) {
                final float x = this.current.getX() + 0.5f;
                this.scriptnx = x;
                this.nx = x;
                this.x = x;
                final float y = this.current.getY() + 0.5f;
                this.scriptny = y;
                this.ny = y;
                this.y = y;
            }
            this.z = (float)this.current.getZ();
        }
    }
    
    public void preupdate() {
        this.nx = this.x;
        this.ny = this.y;
    }
    
    @Override
    public void renderlast() {
        this.bOutline[IsoCamera.frameState.playerIndex] = false;
    }
    
    public void spotted(final IsoMovingObject isoMovingObject, final boolean b) {
    }
    
    @Override
    public void update() {
        if (this.def == null) {
            this.def = IsoSpriteInstance.get(this.sprite);
        }
        this.movementLastFrame.x = this.x - this.lx;
        this.movementLastFrame.y = this.y - this.ly;
        this.lx = this.x;
        this.ly = this.y;
        this.lz = this.z;
        this.square = this.current;
        if (this.sprite != null) {
            this.sprite.update(this.def);
        }
        this.StateEventDelayTimer -= GameTime.instance.getMultiplier();
    }
    
    private void Collided() {
        this.collidedThisFrame = true;
    }
    
    public int compareToY(final IsoMovingObject isoMovingObject) {
        if (this.sprite == null && isoMovingObject.sprite == null) {
            return 0;
        }
        if (this.sprite != null && isoMovingObject.sprite == null) {
            return -1;
        }
        if (this.sprite == null) {
            return 1;
        }
        final float yToScreen = IsoUtils.YToScreen(this.x, this.y, this.z, 0);
        final float yToScreen2 = IsoUtils.YToScreen(isoMovingObject.x, isoMovingObject.y, isoMovingObject.z, 0);
        if (yToScreen > (double)yToScreen2) {
            return 1;
        }
        if (yToScreen < (double)yToScreen2) {
            return -1;
        }
        return 0;
    }
    
    public float distToNearestCamCharacter() {
        float min = Float.MAX_VALUE;
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            final IsoPlayer isoPlayer = IsoPlayer.players[i];
            if (isoPlayer != null) {
                min = Math.min(min, this.DistTo(isoPlayer));
            }
        }
        return min;
    }
    
    public boolean isSolidForSeparate() {
        return !(this instanceof IsoZombieGiblets) && this.current != null && this.solid && !this.isOnFloor();
    }
    
    public boolean isPushableForSeparate() {
        return true;
    }
    
    public boolean isPushedByForSeparate(final IsoMovingObject isoMovingObject) {
        return true;
    }
    
    public void separate() {
        if (!this.isSolidForSeparate()) {
            return;
        }
        if (!this.isPushableForSeparate()) {
            return;
        }
        final IsoGameCharacter isoGameCharacter = Type.tryCastTo(this, IsoGameCharacter.class);
        final IsoPlayer isoPlayer = Type.tryCastTo(this, IsoPlayer.class);
        if (this.z < 0.0f) {
            this.z = 0.0f;
        }
        for (int i = 0; i <= 8; ++i) {
            final IsoGridSquare isoGridSquare = (i == 8) ? this.current : this.current.nav[i];
            if (isoGridSquare != null) {
                if (!isoGridSquare.getMovingObjects().isEmpty()) {
                    if (isoGridSquare == this.current || !this.current.isBlockedTo(isoGridSquare)) {
                        for (int j = 0; j < isoGridSquare.getMovingObjects().size(); ++j) {
                            final IsoMovingObject isoMovingObject = isoGridSquare.getMovingObjects().get(j);
                            if (isoMovingObject != this) {
                                if (isoMovingObject.isSolidForSeparate()) {
                                    if (Math.abs(this.z - isoMovingObject.z) <= 0.3f) {
                                        final IsoGameCharacter bumpedChr = Type.tryCastTo(isoMovingObject, IsoGameCharacter.class);
                                        final IsoPlayer isoPlayer2 = Type.tryCastTo(isoMovingObject, IsoPlayer.class);
                                        final float n = this.width + isoMovingObject.width;
                                        final Vector2 tempo = IsoMovingObject.tempo;
                                        tempo.x = this.nx - isoMovingObject.nx;
                                        tempo.y = this.ny - isoMovingObject.ny;
                                        final float length = tempo.getLength();
                                        if (isoGameCharacter == null || (bumpedChr == null && !(isoMovingObject instanceof BaseVehicle))) {
                                            if (length < n) {
                                                CollisionManager.instance.AddContact(this, isoMovingObject);
                                            }
                                            return;
                                        }
                                        if (bumpedChr != null) {
                                            if (length < n + 16.0f && isoPlayer != null && isoPlayer.getBumpedChr() != isoMovingObject && isoPlayer.getBeenSprintingFor() >= 70.0f && WeaponType.getWeaponType(isoPlayer) == WeaponType.spear) {
                                                isoPlayer.reportEvent("ChargeSpearConnect");
                                                isoPlayer.setAttackType("charge");
                                                isoPlayer.setVariable("StartedAttackWhileSprinting", isoPlayer.attackStarted = true);
                                                return;
                                            }
                                            if (length < n) {
                                                boolean b = false;
                                                if (isoPlayer != null && isoPlayer.getVariableFloat("WalkSpeed", 0.0f) > 0.2f && isoPlayer.runningTime > 0.5f && isoPlayer.getBumpedChr() != isoMovingObject) {
                                                    b = true;
                                                }
                                                if (GameClient.bClient && isoPlayer != null && bumpedChr instanceof IsoPlayer && !ServerOptions.getInstance().PlayerBumpPlayer.getValue()) {
                                                    b = false;
                                                }
                                                if (b && !"charge".equals(isoPlayer.getAttackType())) {
                                                    final boolean b2 = !this.isOnFloor() && (isoGameCharacter.getBumpedChr() != null || (System.currentTimeMillis() - isoPlayer.getLastBump()) / 100L < 15L || isoPlayer.isSprinting()) && (isoPlayer2 == null || !isoPlayer2.isNPC());
                                                    if (b2) {
                                                        final IsoGameCharacter isoGameCharacter2 = isoGameCharacter;
                                                        ++isoGameCharacter2.bumpNbr;
                                                        int b3 = 10 - isoGameCharacter.bumpNbr * 3 + isoGameCharacter.getPerkLevel(PerkFactory.Perks.Fitness) + isoGameCharacter.getPerkLevel(PerkFactory.Perks.Strength);
                                                        if (isoGameCharacter.Traits.Clumsy.isSet()) {
                                                            b3 -= 5;
                                                        }
                                                        if (isoGameCharacter.Traits.Graceful.isSet()) {
                                                            b3 += 5;
                                                        }
                                                        if (isoGameCharacter.Traits.VeryUnderweight.isSet()) {
                                                            b3 -= 8;
                                                        }
                                                        if (isoGameCharacter.Traits.Underweight.isSet()) {
                                                            b3 -= 4;
                                                        }
                                                        if (isoGameCharacter.Traits.Obese.isSet()) {
                                                            b3 -= 8;
                                                        }
                                                        if (isoGameCharacter.Traits.Overweight.isSet()) {
                                                            b3 -= 4;
                                                        }
                                                        final BodyPart bodyPart = isoGameCharacter.getBodyDamage().getBodyPart(BodyPartType.Torso_Lower);
                                                        if (bodyPart.getAdditionalPain(true) > 20.0f) {
                                                            b3 -= (int)((bodyPart.getAdditionalPain(true) - 20.0f) / 20.0f);
                                                        }
                                                        if (Rand.Next(Math.max(1, Math.min(80, b3))) == 0 || isoGameCharacter.isSprinting()) {
                                                            isoGameCharacter.setVariable("BumpDone", false);
                                                            isoGameCharacter.setBumpFall(true);
                                                            isoGameCharacter.setVariable("TripObstacleType", "zombie");
                                                        }
                                                    }
                                                    else {
                                                        isoGameCharacter.bumpNbr = 0;
                                                    }
                                                    isoGameCharacter.setLastBump(System.currentTimeMillis());
                                                    isoGameCharacter.setBumpedChr(bumpedChr);
                                                    isoGameCharacter.setBumpType(this.getBumpedType(bumpedChr));
                                                    final boolean behind = isoGameCharacter.isBehind(bumpedChr);
                                                    String bumpType = isoGameCharacter.getBumpType();
                                                    if (behind) {
                                                        if (bumpType.equals("left")) {
                                                            bumpType = "right";
                                                        }
                                                        else {
                                                            bumpType = "left";
                                                        }
                                                    }
                                                    bumpedChr.setBumpType(bumpType);
                                                    bumpedChr.setHitFromBehind(behind);
                                                    if (b2 | GameClient.bClient) {
                                                        isoGameCharacter.actionContext.reportEvent("wasBumped");
                                                    }
                                                }
                                                if (GameServer.bServer || this.distToNearestCamCharacter() < 60.0f) {
                                                    if (this.isPushedByForSeparate(isoMovingObject)) {
                                                        tempo.setLength((length - n) / 8.0f);
                                                        this.nx -= tempo.x;
                                                        this.ny -= tempo.y;
                                                    }
                                                    this.collideWith(isoMovingObject);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public String getBumpedType(final IsoGameCharacter isoGameCharacter) {
        final float n = this.x - isoGameCharacter.x;
        final float n2 = this.y - isoGameCharacter.y;
        String s = "left";
        if (this.dir == IsoDirections.S || this.dir == IsoDirections.SE || this.dir == IsoDirections.SW) {
            if (n < 0.0f) {
                s = "left";
            }
            else {
                s = "right";
            }
        }
        if (this.dir == IsoDirections.N || this.dir == IsoDirections.NE || this.dir == IsoDirections.NW) {
            if (n > 0.0f) {
                s = "left";
            }
            else {
                s = "right";
            }
        }
        if (this.dir == IsoDirections.E) {
            if (n2 > 0.0f) {
                s = "left";
            }
            else {
                s = "right";
            }
        }
        if (this.dir == IsoDirections.W) {
            if (n2 < 0.0f) {
                s = "left";
            }
            else {
                s = "right";
            }
        }
        return s;
    }
    
    private void slideAwayFromWalls() {
        if (this.current == null) {
            return;
        }
        final IsoZombie isoZombie = Type.tryCastTo(this, IsoZombie.class);
        if (isoZombie == null || (!this.isOnFloor() && !isoZombie.isKnockedDown())) {
            return;
        }
        if (isoZombie.isCrawling() && (isoZombie.getPath2() != null || isoZombie.isMoving())) {
            return;
        }
        if (isoZombie.isCurrentState(ClimbOverFenceState.instance()) || isoZombie.isCurrentState(ClimbThroughWindowState.instance())) {
            return;
        }
        if (!isoZombie.hasAnimationPlayer() || !isoZombie.getAnimationPlayer().isReady()) {
            return;
        }
        final Vector3 vector3 = L_slideAwayFromWalls.vector3;
        Model.BoneToWorldCoords(isoZombie, isoZombie.getAnimationPlayer().getSkinningBoneIndex("Bip01_Head", -1), vector3);
        if (Core.bDebug && DebugOptions.instance.CollideWithObstaclesRenderRadius.getValue()) {
            LineDrawer.DrawIsoCircle(vector3.x, vector3.y, this.z, 0.3f, 16, 1.0f, 1.0f, 0.0f, 1.0f);
        }
        final Vector2 set = L_slideAwayFromWalls.vector2.set(vector3.x - this.x, vector3.y - this.y);
        set.normalize();
        final Vector3 vector4 = vector3;
        vector4.x += set.x * 0.3f;
        final Vector3 vector5 = vector3;
        vector5.y += set.y * 0.3f;
        if (isoZombie.isKnockedDown() && (isoZombie.isCurrentState(ZombieFallDownState.instance()) || isoZombie.isCurrentState(StaggerBackState.instance()))) {
            final Vector2f resolveCollision = PolygonalMap2.instance.resolveCollision(isoZombie, vector3.x, vector3.y, L_slideAwayFromWalls.vector2f);
            if (resolveCollision.x != vector3.x || resolveCollision.y != vector3.y) {
                final float n = GameTime.getInstance().getMultiplier() / 5.0f;
                this.nx += (resolveCollision.x - vector3.x) * n;
                this.ny += (resolveCollision.y - vector3.y) * n;
                return;
            }
        }
        if ((int)vector3.x == this.current.x && (int)vector3.y == this.current.y) {
            return;
        }
        final IsoGridSquare gridSquare = this.getCell().getGridSquare((int)vector3.x, (int)vector3.y, (int)this.z);
        if (gridSquare == null) {
            return;
        }
        if (!this.current.testCollideAdjacent(this, gridSquare.x - this.current.x, gridSquare.y - this.current.y, 0)) {
            return;
        }
        final float n2 = GameTime.getInstance().getMultiplier() / 5.0f;
        if (gridSquare.x < this.current.x) {
            this.nx += (this.current.x - vector3.x) * n2;
        }
        else if (gridSquare.x > this.current.x) {
            this.nx += (gridSquare.x - vector3.x) * n2;
        }
        if (gridSquare.y < this.current.y) {
            this.ny += (this.current.y - vector3.y) * n2;
        }
        else if (gridSquare.y > this.current.y) {
            this.ny += (gridSquare.y - vector3.y) * n2;
        }
    }
    
    private boolean DoCollide(final int n) {
        final IsoGameCharacter isoGameCharacter = Type.tryCastTo(this, IsoGameCharacter.class);
        this.current = this.getCell().getGridSquare((int)this.nx, (int)this.ny, (int)this.z);
        if (this instanceof IsoMolotovCocktail) {
            for (int i = (int)this.z; i > 0; --i) {
                for (int j = -1; j <= 1; ++j) {
                    for (int k = -1; k <= 1; ++k) {
                        final IsoGridSquare newGridSquare = this.getCell().createNewGridSquare((int)this.nx + k, (int)this.ny + j, i, false);
                        if (newGridSquare != null) {
                            newGridSquare.RecalcAllWithNeighbours(true);
                        }
                    }
                }
            }
        }
        if (this.current != null) {
            if (!this.current.TreatAsSolidFloor()) {
                this.current = this.getCell().getGridSquare((int)this.nx, (int)this.ny, (int)this.z);
            }
            if (this.current == null) {
                return false;
            }
            this.current = this.getCell().getGridSquare((int)this.nx, (int)this.ny, (int)this.z);
        }
        if (this.current != this.last && this.last != null && this.current != null) {
            if (isoGameCharacter != null && isoGameCharacter.getCurrentState() != null && isoGameCharacter.getCurrentState().isIgnoreCollide(isoGameCharacter, this.last.x, this.last.y, this.last.z, this.current.x, this.current.y, this.current.z)) {
                return false;
            }
            if (this == IsoCamera.CamCharacter) {
                IsoWorld.instance.CurrentCell.lightUpdateCount = 10;
            }
            final int n2 = this.current.getX() - this.last.getX();
            final int n3 = this.current.getY() - this.last.getY();
            final int n4 = this.current.getZ() - this.last.getZ();
            boolean b = false;
            if (this.last.testCollideAdjacent(this, n2, n3, n4) || this.current == null) {
                b = true;
            }
            if (b) {
                if (this.last.getX() < this.current.getX()) {
                    this.collidedE = true;
                }
                if (this.last.getX() > this.current.getX()) {
                    this.collidedW = true;
                }
                if (this.last.getY() < this.current.getY()) {
                    this.collidedS = true;
                }
                if (this.last.getY() > this.current.getY()) {
                    this.collidedN = true;
                }
                this.current = this.last;
                this.checkBreakHoppable();
                this.checkHitHoppable();
                if (n == 2) {
                    if ((this.collidedS || this.collidedN) && (this.collidedE || this.collidedW)) {
                        this.collidedS = false;
                        this.collidedN = false;
                    }
                }
                else if (n == 1 && (this.collidedS || this.collidedN) && (this.collidedE || this.collidedW)) {
                    this.collidedW = false;
                    this.collidedE = false;
                }
                this.Collided();
                return true;
            }
        }
        else if (this.nx != this.lx || this.ny != this.ly) {
            if (this instanceof IsoZombie && Core.GameMode.equals("Tutorial")) {
                return true;
            }
            if (this.current == null) {
                if (this.nx < this.lx) {
                    this.collidedW = true;
                }
                if (this.nx > this.lx) {
                    this.collidedE = true;
                }
                if (this.ny < this.ly) {
                    this.collidedN = true;
                }
                if (this.ny > this.ly) {
                    this.collidedS = true;
                }
                this.nx = this.lx;
                this.ny = this.ly;
                this.current = this.last;
                this.Collided();
                return true;
            }
            if (isoGameCharacter != null && isoGameCharacter.getPath2() != null) {
                final PathFindBehavior2 pathFindBehavior2 = isoGameCharacter.getPathFindBehavior2();
                if ((int)pathFindBehavior2.getTargetX() == (int)this.x && (int)pathFindBehavior2.getTargetY() == (int)this.y && (int)pathFindBehavior2.getTargetZ() == (int)this.z) {
                    return false;
                }
            }
            IsoGridSquare isoGridSquare = this.getFeelerTile(this.feelersize);
            if (isoGameCharacter != null) {
                if (isoGameCharacter.isClimbing()) {
                    isoGridSquare = this.current;
                }
                if (isoGridSquare != null && isoGridSquare != this.current && isoGameCharacter.getPath2() != null && !isoGameCharacter.getPath2().crossesSquare(isoGridSquare.x, isoGridSquare.y, isoGridSquare.z)) {
                    isoGridSquare = this.current;
                }
            }
            if (isoGridSquare != null && isoGridSquare != this.current && this.current != null) {
                if (isoGameCharacter != null && isoGameCharacter.getCurrentState() != null && isoGameCharacter.getCurrentState().isIgnoreCollide(isoGameCharacter, this.current.x, this.current.y, this.current.z, isoGridSquare.x, isoGridSquare.y, isoGridSquare.z)) {
                    return false;
                }
                if (this.current.testCollideAdjacent(this, isoGridSquare.getX() - this.current.getX(), isoGridSquare.getY() - this.current.getY(), isoGridSquare.getZ() - this.current.getZ())) {
                    if (this.last != null) {
                        if (this.current.getX() < isoGridSquare.getX()) {
                            this.collidedE = true;
                        }
                        if (this.current.getX() > isoGridSquare.getX()) {
                            this.collidedW = true;
                        }
                        if (this.current.getY() < isoGridSquare.getY()) {
                            this.collidedS = true;
                        }
                        if (this.current.getY() > isoGridSquare.getY()) {
                            this.collidedN = true;
                        }
                        this.checkBreakHoppable();
                        this.checkHitHoppable();
                        if (n == 2 && (this.collidedS || this.collidedN) && (this.collidedE || this.collidedW)) {
                            this.collidedS = false;
                            this.collidedN = false;
                        }
                        if (n == 1 && (this.collidedS || this.collidedN) && (this.collidedE || this.collidedW)) {
                            this.collidedW = false;
                            this.collidedE = false;
                        }
                    }
                    this.Collided();
                    return true;
                }
            }
        }
        return false;
    }
    
    private void checkHitHoppable() {
        final IsoZombie isoZombie = Type.tryCastTo(this, IsoZombie.class);
        if (isoZombie == null || isoZombie.bCrawling) {
            return;
        }
        if (isoZombie.isCurrentState(AttackState.instance()) || isoZombie.isCurrentState(StaggerBackState.instance()) || isoZombie.isCurrentState(ClimbOverFenceState.instance()) || isoZombie.isCurrentState(ClimbThroughWindowState.instance())) {
            return;
        }
        if (this.collidedW && !this.collidedN && !this.collidedS && this.last.Is(IsoFlagType.HoppableW)) {
            isoZombie.climbOverFence(IsoDirections.W);
        }
        if (this.collidedN && !this.collidedE && !this.collidedW && this.last.Is(IsoFlagType.HoppableN)) {
            isoZombie.climbOverFence(IsoDirections.N);
        }
        if (this.collidedS && !this.collidedE && !this.collidedW) {
            final IsoGridSquare isoGridSquare = this.last.nav[IsoDirections.S.index()];
            if (isoGridSquare != null && isoGridSquare.Is(IsoFlagType.HoppableN)) {
                isoZombie.climbOverFence(IsoDirections.S);
            }
        }
        if (this.collidedE && !this.collidedN && !this.collidedS) {
            final IsoGridSquare isoGridSquare2 = this.last.nav[IsoDirections.E.index()];
            if (isoGridSquare2 != null && isoGridSquare2.Is(IsoFlagType.HoppableW)) {
                isoZombie.climbOverFence(IsoDirections.E);
            }
        }
    }
    
    private void checkBreakHoppable() {
        final IsoZombie isoZombie = Type.tryCastTo(this, IsoZombie.class);
        if (isoZombie == null || !isoZombie.bCrawling) {
            return;
        }
        if (isoZombie.isCurrentState(AttackState.instance()) || isoZombie.isCurrentState(StaggerBackState.instance()) || isoZombie.isCurrentState(CrawlingZombieTurnState.instance())) {
            return;
        }
        IsoDirections isoDirections = IsoDirections.Max;
        if (this.collidedW && !this.collidedN && !this.collidedS) {
            isoDirections = IsoDirections.W;
        }
        if (this.collidedN && !this.collidedE && !this.collidedW) {
            isoDirections = IsoDirections.N;
        }
        if (this.collidedS && !this.collidedE && !this.collidedW) {
            isoDirections = IsoDirections.S;
        }
        if (this.collidedE && !this.collidedN && !this.collidedS) {
            isoDirections = IsoDirections.E;
        }
        if (isoDirections == IsoDirections.Max) {
            return;
        }
        final IsoObject hoppableTo = this.last.getHoppableTo(this.last.getAdjacentSquare(isoDirections));
        final IsoThumpable thumpTarget = Type.tryCastTo(hoppableTo, IsoThumpable.class);
        if (thumpTarget != null && !thumpTarget.isThumpable()) {
            isoZombie.setThumpTarget(thumpTarget);
        }
        else if (hoppableTo != null && hoppableTo.getThumpableFor(isoZombie) != null) {
            isoZombie.setThumpTarget(hoppableTo);
        }
    }
    
    private void checkHitWall() {
        if (!this.collidedN && !this.collidedS && !this.collidedE && !this.collidedW) {
            return;
        }
        if (this.current == null) {
            return;
        }
        final IsoPlayer isoPlayer = Type.tryCastTo(this, IsoPlayer.class);
        if (isoPlayer == null) {
            return;
        }
        if (!StringUtils.isNullOrEmpty(this.getCollideType())) {
            return;
        }
        boolean b = false;
        final int wallType = this.current.getWallType();
        if ((wallType & 0x1) != 0x0 && this.collidedN && this.getDir() == IsoDirections.N) {
            b = true;
        }
        if ((wallType & 0x2) != 0x0 && this.collidedS && this.getDir() == IsoDirections.S) {
            b = true;
        }
        if ((wallType & 0x4) != 0x0 && this.collidedW && this.getDir() == IsoDirections.W) {
            b = true;
        }
        if ((wallType & 0x8) != 0x0 && this.collidedE && this.getDir() == IsoDirections.E) {
            b = true;
        }
        if (this.checkVaultOver()) {
            b = false;
        }
        if (b && isoPlayer.isSprinting() && isoPlayer.isLocalPlayer()) {
            this.setCollideType("wall");
            isoPlayer.getActionContext().reportEvent("collideWithWall");
            this.lastCollideTime = 70.0f;
        }
    }
    
    private boolean checkVaultOver() {
        final IsoPlayer isoPlayer = (IsoPlayer)this;
        if (isoPlayer.isCurrentState(ClimbOverFenceState.instance()) || isoPlayer.isIgnoreAutoVault()) {
            return false;
        }
        if (!isoPlayer.IsRunning() && !isoPlayer.isSprinting() && isoPlayer.isLocalPlayer()) {
            return false;
        }
        IsoDirections isoDirections = this.getDir();
        final IsoGridSquare adjacentSquare = this.current.getAdjacentSquare(IsoDirections.SE);
        if (isoDirections == IsoDirections.SE && adjacentSquare != null && adjacentSquare.Is(IsoFlagType.HoppableN) && adjacentSquare.Is(IsoFlagType.HoppableW)) {
            return false;
        }
        IsoGridSquare isoGridSquare = this.current;
        if (this.collidedS) {
            isoGridSquare = this.current.getAdjacentSquare(IsoDirections.S);
        }
        else if (this.collidedE) {
            isoGridSquare = this.current.getAdjacentSquare(IsoDirections.E);
        }
        if (isoGridSquare == null) {
            return false;
        }
        boolean b = false;
        if (this.current.getProperties().Is(IsoFlagType.HoppableN) && this.collidedN && !this.collidedW && !this.collidedE && (isoDirections == IsoDirections.NW || isoDirections == IsoDirections.N || isoDirections == IsoDirections.NE)) {
            isoDirections = IsoDirections.N;
            b = true;
        }
        if (isoGridSquare.getProperties().Is(IsoFlagType.HoppableN) && this.collidedS && !this.collidedW && !this.collidedE && (isoDirections == IsoDirections.SW || isoDirections == IsoDirections.S || isoDirections == IsoDirections.SE)) {
            isoDirections = IsoDirections.S;
            b = true;
        }
        if (this.current.getProperties().Is(IsoFlagType.HoppableW) && this.collidedW && !this.collidedN && !this.collidedS && (isoDirections == IsoDirections.NW || isoDirections == IsoDirections.W || isoDirections == IsoDirections.SW)) {
            isoDirections = IsoDirections.W;
            b = true;
        }
        if (isoGridSquare.getProperties().Is(IsoFlagType.HoppableW) && this.collidedE && !this.collidedN && !this.collidedS && (isoDirections == IsoDirections.NE || isoDirections == IsoDirections.E || isoDirections == IsoDirections.SE)) {
            isoDirections = IsoDirections.E;
            b = true;
        }
        if (b && isoPlayer.isSafeToClimbOver(isoDirections)) {
            ClimbOverFenceState.instance().setParams(isoPlayer, isoDirections);
            isoPlayer.getActionContext().reportEvent("EventClimbFence");
            return true;
        }
        return false;
    }
    
    public void setMovingSquareNow() {
        if (this.movingSq != null) {
            this.movingSq.getMovingObjects().remove(this);
            this.movingSq = null;
        }
        if (this.current != null && !this.current.getMovingObjects().contains(this)) {
            this.current.getMovingObjects().add(this);
            this.movingSq = this.current;
        }
    }
    
    public IsoGridSquare getFeelerTile(final float length) {
        final Vector2 tempo = IsoMovingObject.tempo;
        tempo.x = this.nx - this.lx;
        tempo.y = this.ny - this.ly;
        tempo.setLength(length);
        return this.getCell().getGridSquare((int)(this.x + tempo.x), (int)(this.y + tempo.y), (int)this.z);
    }
    
    public void DoCollideNorS() {
        this.ny = this.ly;
    }
    
    public void DoCollideWorE() {
        this.nx = this.lx;
    }
    
    public int getTimeSinceZombieAttack() {
        return this.TimeSinceZombieAttack;
    }
    
    public void setTimeSinceZombieAttack(final int timeSinceZombieAttack) {
        this.TimeSinceZombieAttack = timeSinceZombieAttack;
    }
    
    public boolean isCollidedE() {
        return this.collidedE;
    }
    
    public void setCollidedE(final boolean collidedE) {
        this.collidedE = collidedE;
    }
    
    public boolean isCollidedN() {
        return this.collidedN;
    }
    
    public void setCollidedN(final boolean collidedN) {
        this.collidedN = collidedN;
    }
    
    public IsoObject getCollidedObject() {
        return this.CollidedObject;
    }
    
    public void setCollidedObject(final IsoObject collidedObject) {
        this.CollidedObject = collidedObject;
    }
    
    public boolean isCollidedS() {
        return this.collidedS;
    }
    
    public void setCollidedS(final boolean collidedS) {
        this.collidedS = collidedS;
    }
    
    public boolean isCollidedThisFrame() {
        return this.collidedThisFrame;
    }
    
    public void setCollidedThisFrame(final boolean collidedThisFrame) {
        this.collidedThisFrame = collidedThisFrame;
    }
    
    public boolean isCollidedW() {
        return this.collidedW;
    }
    
    public void setCollidedW(final boolean collidedW) {
        this.collidedW = collidedW;
    }
    
    public boolean isCollidedWithDoor() {
        return this.CollidedWithDoor;
    }
    
    public void setCollidedWithDoor(final boolean collidedWithDoor) {
        this.CollidedWithDoor = collidedWithDoor;
    }
    
    public boolean isCollidedWithVehicle() {
        return this.collidedWithVehicle;
    }
    
    public IsoGridSquare getCurrentSquare() {
        return this.current;
    }
    
    public IsoMetaGrid.Zone getCurrentZone() {
        if (this.current != null) {
            return this.current.getZone();
        }
        return null;
    }
    
    public void setCurrent(final IsoGridSquare current) {
        this.current = current;
    }
    
    @Override
    public boolean isDestroyed() {
        return this.destroyed;
    }
    
    public void setDestroyed(final boolean destroyed) {
        this.destroyed = destroyed;
    }
    
    public boolean isFirstUpdate() {
        return this.firstUpdate;
    }
    
    public void setFirstUpdate(final boolean firstUpdate) {
        this.firstUpdate = firstUpdate;
    }
    
    public Vector2 getHitDir() {
        return this.hitDir;
    }
    
    public void setHitDir(final Vector2 vector2) {
        this.hitDir.set(vector2);
    }
    
    public float getImpulsex() {
        return this.impulsex;
    }
    
    public void setImpulsex(final float impulsex) {
        this.impulsex = impulsex;
    }
    
    public float getImpulsey() {
        return this.impulsey;
    }
    
    public void setImpulsey(final float impulsey) {
        this.impulsey = impulsey;
    }
    
    public float getLimpulsex() {
        return this.limpulsex;
    }
    
    public void setLimpulsex(final float limpulsex) {
        this.limpulsex = limpulsex;
    }
    
    public float getLimpulsey() {
        return this.limpulsey;
    }
    
    public void setLimpulsey(final float limpulsey) {
        this.limpulsey = limpulsey;
    }
    
    public float getHitForce() {
        return this.hitForce;
    }
    
    public void setHitForce(final float hitForce) {
        this.hitForce = hitForce;
    }
    
    public float getHitFromAngle() {
        return this.hitFromAngle;
    }
    
    public void setHitFromAngle(final float hitFromAngle) {
        this.hitFromAngle = hitFromAngle;
    }
    
    public IsoGridSquare getLastSquare() {
        return this.last;
    }
    
    public void setLast(final IsoGridSquare last) {
        this.last = last;
    }
    
    public float getLx() {
        return this.lx;
    }
    
    public void setLx(final float lx) {
        this.lx = lx;
    }
    
    public float getLy() {
        return this.ly;
    }
    
    public void setLy(final float ly) {
        this.ly = ly;
    }
    
    public float getLz() {
        return this.lz;
    }
    
    public void setLz(final float lz) {
        this.lz = lz;
    }
    
    public float getNx() {
        return this.nx;
    }
    
    public void setNx(final float nx) {
        this.nx = nx;
    }
    
    public float getNy() {
        return this.ny;
    }
    
    public void setNy(final float ny) {
        this.ny = ny;
    }
    
    public boolean getNoDamage() {
        return this.noDamage;
    }
    
    public void setNoDamage(final boolean noDamage) {
        this.noDamage = noDamage;
    }
    
    public boolean isSolid() {
        return this.solid;
    }
    
    public void setSolid(final boolean solid) {
        this.solid = solid;
    }
    
    public float getStateEventDelayTimer() {
        return this.StateEventDelayTimer;
    }
    
    public void setStateEventDelayTimer(final float stateEventDelayTimer) {
        this.StateEventDelayTimer = stateEventDelayTimer;
    }
    
    public float getWidth() {
        return this.width;
    }
    
    public void setWidth(final float width) {
        this.width = width;
    }
    
    public boolean isbAltCollide() {
        return this.bAltCollide;
    }
    
    public void setbAltCollide(final boolean bAltCollide) {
        this.bAltCollide = bAltCollide;
    }
    
    public boolean isShootable() {
        return this.shootable;
    }
    
    public void setShootable(final boolean shootable) {
        this.shootable = shootable;
    }
    
    public IsoZombie getLastTargettedBy() {
        return this.lastTargettedBy;
    }
    
    public void setLastTargettedBy(final IsoZombie lastTargettedBy) {
        this.lastTargettedBy = lastTargettedBy;
    }
    
    public boolean isCollidable() {
        return this.Collidable;
    }
    
    public void setCollidable(final boolean collidable) {
        this.Collidable = collidable;
    }
    
    public float getScriptnx() {
        return this.scriptnx;
    }
    
    public void setScriptnx(final float scriptnx) {
        this.scriptnx = scriptnx;
    }
    
    public float getScriptny() {
        return this.scriptny;
    }
    
    public void setScriptny(final float scriptny) {
        this.scriptny = scriptny;
    }
    
    public String getScriptModule() {
        return this.ScriptModule;
    }
    
    public void setScriptModule(final String scriptModule) {
        this.ScriptModule = scriptModule;
    }
    
    public Vector2 getMovementLastFrame() {
        return this.movementLastFrame;
    }
    
    public void setMovementLastFrame(final Vector2 movementLastFrame) {
        this.movementLastFrame = movementLastFrame;
    }
    
    public float getFeelersize() {
        return this.feelersize;
    }
    
    public void setFeelersize(final float feelersize) {
        this.feelersize = feelersize;
    }
    
    public byte canHaveMultipleHits() {
        byte b = 0;
        final ArrayList<IsoMovingObject> objectList = IsoWorld.instance.CurrentCell.getObjectList();
        for (int i = 0; i < objectList.size(); ++i) {
            final IsoPlayer isoPlayer = Type.tryCastTo(objectList.get(i), IsoPlayer.class);
            if (isoPlayer != null) {
                HandWeapon bareHands = Type.tryCastTo(isoPlayer.getPrimaryHandItem(), HandWeapon.class);
                if (bareHands == null || isoPlayer.bDoShove || isoPlayer.isForceShove()) {
                    bareHands = isoPlayer.bareHands;
                }
                final float distanceTo = IsoUtils.DistanceTo(isoPlayer.x, isoPlayer.y, this.x, this.y);
                if (distanceTo <= bareHands.getMaxRange() * bareHands.getRangeMod(isoPlayer) + 2.0f) {
                    final float dotWithForwardDirection = isoPlayer.getDotWithForwardDirection(this.x, this.y);
                    if (distanceTo <= 2.5 || dotWithForwardDirection >= 0.1f) {
                        final LosUtil.TestResults lineClear = LosUtil.lineClear(isoPlayer.getCell(), (int)isoPlayer.getX(), (int)isoPlayer.getY(), (int)isoPlayer.getZ(), (int)this.getX(), (int)this.getY(), (int)this.getZ(), false);
                        if (lineClear != LosUtil.TestResults.Blocked) {
                            if (lineClear != LosUtil.TestResults.ClearThroughClosedDoor) {
                                ++b;
                                if (b >= 2) {
                                    return b;
                                }
                            }
                        }
                    }
                }
            }
        }
        return b;
    }
    
    public boolean isOnFloor() {
        return this.bOnFloor;
    }
    
    public void setOnFloor(final boolean bOnFloor) {
        this.bOnFloor = bOnFloor;
    }
    
    public void Despawn() {
    }
    
    public boolean isCloseKilled() {
        return this.closeKilled;
    }
    
    public void setCloseKilled(final boolean closeKilled) {
        this.closeKilled = closeKilled;
    }
    
    @Override
    public Vector2 getFacingPosition(final Vector2 vector2) {
        vector2.set(this.getX(), this.getY());
        return vector2;
    }
    
    private boolean isInLoadedArea(final int n, final int n2) {
        if (GameServer.bServer) {
            for (int i = 0; i < ServerMap.instance.LoadedCells.size(); ++i) {
                final ServerMap.ServerCell serverCell = ServerMap.instance.LoadedCells.get(i);
                if (n >= serverCell.WX * 50 && n < (serverCell.WX + 1) * 50 && n2 >= serverCell.WY * 50 && n2 < (serverCell.WY + 1) * 50) {
                    return true;
                }
            }
        }
        else {
            for (int j = 0; j < IsoPlayer.numPlayers; ++j) {
                final IsoChunkMap isoChunkMap = IsoWorld.instance.CurrentCell.ChunkMap[j];
                if (!isoChunkMap.ignore) {
                    if (n >= isoChunkMap.getWorldXMinTiles() && n < isoChunkMap.getWorldXMaxTiles() && n2 >= isoChunkMap.getWorldYMinTiles() && n2 < isoChunkMap.getWorldYMaxTiles()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public boolean isCollided() {
        return !StringUtils.isNullOrWhitespace(this.getCollideType());
    }
    
    public String getCollideType() {
        return this.collideType;
    }
    
    public void setCollideType(final String collideType) {
        this.collideType = collideType;
    }
    
    public float getLastCollideTime() {
        return this.lastCollideTime;
    }
    
    public void setLastCollideTime(final float lastCollideTime) {
        this.lastCollideTime = lastCollideTime;
    }
    
    public ArrayList<IsoZombie> getEatingZombies() {
        return this.eatingZombies;
    }
    
    public void setEatingZombies(final ArrayList<IsoZombie> c) {
        this.eatingZombies.clear();
        this.eatingZombies.addAll(c);
    }
    
    public boolean isEatingOther(final IsoMovingObject isoMovingObject) {
        return isoMovingObject != null && isoMovingObject.eatingZombies.contains(this);
    }
    
    public float getDistanceSq(final IsoMovingObject isoMovingObject) {
        final float n = this.x - isoMovingObject.x;
        final float n2 = this.y - isoMovingObject.y;
        return n * n + n2 * n2;
    }
    
    public void setZombiesDontAttack(final boolean zombiesDontAttack) {
        this.zombiesDontAttack = zombiesDontAttack;
    }
    
    public boolean isZombiesDontAttack() {
        return this.zombiesDontAttack;
    }
    
    static {
        IsoMovingObject.treeSoundMgr = new TreeSoundManager();
        IsoMovingObject.IDCount = 0;
        tempo = new Vector2();
    }
    
    private static final class L_slideAwayFromWalls
    {
        static final Vector2f vector2f;
        static final Vector2 vector2;
        static final Vector3 vector3;
        
        static {
            vector2f = new Vector2f();
            vector2 = new Vector2();
            vector3 = new Vector3();
        }
    }
    
    public static class TreeSoundManager
    {
        private ArrayList<IsoGridSquare> squares;
        private long[] soundTime;
        private Comparator<IsoGridSquare> comp;
        
        public TreeSoundManager() {
            this.squares = new ArrayList<IsoGridSquare>();
            this.soundTime = new long[6];
            final float n;
            final float n2;
            this.comp = ((isoGridSquare, isoGridSquare2) -> {
                this.getClosestListener(isoGridSquare.x + 0.5f, isoGridSquare.y + 0.5f, (float)isoGridSquare.z);
                this.getClosestListener(isoGridSquare2.x + 0.5f, isoGridSquare2.y + 0.5f, (float)isoGridSquare2.z);
                if (n > n2) {
                    return 1;
                }
                else if (n < n2) {
                    return -1;
                }
                else {
                    return 0;
                }
            });
        }
        
        public void addSquare(final IsoGridSquare isoGridSquare) {
            if (!this.squares.contains(isoGridSquare)) {
                this.squares.add(isoGridSquare);
            }
        }
        
        public void update() {
            if (this.squares.isEmpty()) {
                return;
            }
            Collections.sort(this.squares, this.comp);
            final long currentTimeMillis = System.currentTimeMillis();
            for (int i = 0; i < this.soundTime.length; ++i) {
                if (i >= this.squares.size()) {
                    break;
                }
                final IsoGridSquare isoGridSquare = this.squares.get(i);
                if (this.getClosestListener(isoGridSquare.x + 0.5f, isoGridSquare.y + 0.5f, (float)isoGridSquare.z) <= 20.0f) {
                    final int freeSoundSlot = this.getFreeSoundSlot(currentTimeMillis);
                    if (freeSoundSlot == -1) {
                        break;
                    }
                    Audio playWorldSoundImpl = null;
                    final float n = 0.05f;
                    final float n2 = 16.0f;
                    final float n3 = 0.29999998f;
                    if (GameClient.bClient) {
                        playWorldSoundImpl = SoundManager.instance.PlayWorldSoundImpl("Bushes", false, isoGridSquare.getX(), isoGridSquare.getY(), isoGridSquare.getZ(), n, n2, n3, false);
                    }
                    else if (IsoWorld.instance.getFreeEmitter(isoGridSquare.x + 0.5f, isoGridSquare.y + 0.5f, (float)isoGridSquare.z).playSound("Bushes") != 0L) {
                        this.soundTime[freeSoundSlot] = currentTimeMillis;
                    }
                    if (playWorldSoundImpl != null) {
                        this.soundTime[freeSoundSlot] = currentTimeMillis;
                    }
                }
            }
            this.squares.clear();
        }
        
        private float getClosestListener(final float n, final float n2, final float n3) {
            float n4 = Float.MAX_VALUE;
            for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
                final IsoPlayer isoPlayer = IsoPlayer.players[i];
                if (isoPlayer != null && isoPlayer.getCurrentSquare() != null) {
                    float distanceTo = IsoUtils.DistanceTo(isoPlayer.getX(), isoPlayer.getY(), isoPlayer.getZ() * 3.0f, n, n2, n3 * 3.0f);
                    if (isoPlayer.Traits.HardOfHearing.isSet()) {
                        distanceTo *= 4.5f;
                    }
                    if (distanceTo < n4) {
                        n4 = distanceTo;
                    }
                }
            }
            return n4;
        }
        
        private int getFreeSoundSlot(final long n) {
            long n2 = Long.MAX_VALUE;
            int n3 = -1;
            for (int i = 0; i < this.soundTime.length; ++i) {
                if (this.soundTime[i] < n2) {
                    n2 = this.soundTime[i];
                    n3 = i;
                }
            }
            if (n - n2 < 1000L) {
                return -1;
            }
            return n3;
        }
    }
    
    private static final class L_postUpdate
    {
        static final Vector2f vector2f;
        
        static {
            vector2f = new Vector2f();
        }
    }
}

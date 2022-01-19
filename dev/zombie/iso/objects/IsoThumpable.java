// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.objects;

import zombie.characters.BaseCharacterSoundEmitter;
import zombie.iso.SpriteDetails.IsoObjectType;
import java.util.Iterator;
import zombie.network.ServerMap;
import zombie.network.PacketTypes;
import zombie.core.network.ByteBufferWriter;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.DrainableComboItem;
import zombie.core.raknet.UdpConnection;
import zombie.GameTime;
import zombie.iso.LosUtil;
import zombie.core.Translator;
import zombie.iso.IsoWorld;
import zombie.Lua.LuaEventManager;
import zombie.audio.parameters.ParameterMeleeHitSurface;
import zombie.util.Type;
import zombie.inventory.types.HandWeapon;
import zombie.core.math.PZMath;
import zombie.core.properties.PropertyContainer;
import zombie.iso.IsoDirections;
import zombie.network.GameServer;
import zombie.WorldSoundManager;
import zombie.ai.states.ThumpState;
import zombie.characters.IsoZombie;
import zombie.iso.BrokenFences;
import zombie.SandboxOptions;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.iso.IsoMovingObject;
import zombie.util.io.BitHeaderWrite;
import java.io.IOException;
import zombie.util.io.BitHeaderRead;
import zombie.network.GameClient;
import zombie.SystemDisabler;
import zombie.characters.IsoGameCharacter;
import zombie.inventory.InventoryItem;
import zombie.GameWindow;
import zombie.world.WorldDictionary;
import zombie.util.io.BitHeader;
import java.nio.ByteBuffer;
import zombie.iso.IsoGridSquare;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.iso.IsoCell;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.inventory.ItemContainer;
import zombie.Lua.LuaManager;
import zombie.iso.Vector2;
import zombie.iso.IsoLightSource;
import zombie.iso.sprite.IsoSprite;
import se.krka.kahlua.vm.KahluaTable;
import zombie.iso.objects.interfaces.Thumpable;
import zombie.iso.objects.interfaces.BarricadeAble;
import zombie.iso.IsoObject;

public class IsoThumpable extends IsoObject implements BarricadeAble, Thumpable
{
    private KahluaTable table;
    private KahluaTable modData;
    public Boolean isDoor;
    public Boolean isDoorFrame;
    public String breakSound;
    private boolean isCorner;
    private boolean isFloor;
    private boolean blockAllTheSquare;
    public boolean Locked;
    public int MaxHealth;
    public int Health;
    public int PushedMaxStrength;
    public int PushedStrength;
    IsoSprite closedSprite;
    public boolean north;
    private int thumpDmg;
    private float crossSpeed;
    public boolean open;
    IsoSprite openSprite;
    private boolean destroyed;
    private boolean canBarricade;
    public boolean canPassThrough;
    private boolean isStairs;
    private boolean isContainer;
    private boolean dismantable;
    private boolean canBePlastered;
    private boolean paintable;
    private boolean isThumpable;
    private boolean isHoppable;
    private int lightSourceRadius;
    private int lightSourceLife;
    private int lightSourceXOffset;
    private int lightSourceYOffset;
    private boolean lightSourceOn;
    private IsoLightSource lightSource;
    private String lightSourceFuel;
    private float lifeLeft;
    private float lifeDelta;
    private boolean haveFuel;
    private float updateAccumulator;
    private float lastUpdateHours;
    public int keyId;
    private boolean lockedByKey;
    public boolean lockedByPadlock;
    private boolean canBeLockByPadlock;
    public int lockedByCode;
    public int OldNumPlanks;
    public String thumpSound;
    public static final Vector2 tempo;
    
    @Override
    public KahluaTable getModData() {
        if (this.modData == null) {
            this.modData = LuaManager.platform.newTable();
        }
        return this.modData;
    }
    
    public void setModData(final KahluaTable modData) {
        this.modData = modData;
    }
    
    @Override
    public boolean hasModData() {
        return this.modData != null && !this.modData.isEmpty();
    }
    
    public boolean isCanPassThrough() {
        return this.canPassThrough;
    }
    
    public void setCanPassThrough(final boolean canPassThrough) {
        this.canPassThrough = canPassThrough;
    }
    
    public boolean isBlockAllTheSquare() {
        return this.blockAllTheSquare;
    }
    
    public void setBlockAllTheSquare(final boolean blockAllTheSquare) {
        this.blockAllTheSquare = blockAllTheSquare;
    }
    
    public void setIsDismantable(final boolean dismantable) {
        this.dismantable = dismantable;
    }
    
    public boolean isDismantable() {
        return this.dismantable;
    }
    
    public float getCrossSpeed() {
        return this.crossSpeed;
    }
    
    public void setCrossSpeed(final float crossSpeed) {
        this.crossSpeed = crossSpeed;
    }
    
    public void setIsFloor(final boolean isFloor) {
        this.isFloor = isFloor;
    }
    
    public boolean isCorner() {
        return this.isCorner;
    }
    
    public boolean isFloor() {
        return this.isFloor;
    }
    
    public void setIsContainer(final boolean isContainer) {
        this.isContainer = isContainer;
        if (isContainer) {
            this.container = new ItemContainer("crate", this.square, this);
            if (this.sprite.getProperties().Is("ContainerCapacity")) {
                this.container.Capacity = Integer.parseInt(this.sprite.getProperties().Val("ContainerCapacity"));
            }
            this.container.setExplored(true);
        }
    }
    
    public void setIsStairs(final boolean isStairs) {
        this.isStairs = isStairs;
    }
    
    public boolean isStairs() {
        return this.isStairs;
    }
    
    public boolean isWindow() {
        return this.sprite != null && (this.sprite.getProperties().Is(IsoFlagType.WindowN) || this.sprite.getProperties().Is(IsoFlagType.WindowW));
    }
    
    @Override
    public String getObjectName() {
        return "Thumpable";
    }
    
    public IsoThumpable(final IsoCell isoCell) {
        super(isoCell);
        this.isDoor = false;
        this.isDoorFrame = false;
        this.breakSound = "BreakObject";
        this.isCorner = false;
        this.isFloor = false;
        this.blockAllTheSquare = false;
        this.Locked = false;
        this.MaxHealth = 500;
        this.Health = 500;
        this.PushedMaxStrength = 0;
        this.PushedStrength = 0;
        this.north = false;
        this.thumpDmg = 8;
        this.crossSpeed = 1.0f;
        this.open = false;
        this.destroyed = false;
        this.canBarricade = false;
        this.canPassThrough = false;
        this.isStairs = false;
        this.isContainer = false;
        this.dismantable = false;
        this.canBePlastered = false;
        this.paintable = false;
        this.isThumpable = true;
        this.isHoppable = false;
        this.lightSourceRadius = -1;
        this.lightSourceLife = -1;
        this.lightSourceXOffset = 0;
        this.lightSourceYOffset = 0;
        this.lightSourceOn = false;
        this.lightSource = null;
        this.lightSourceFuel = null;
        this.lifeLeft = -1.0f;
        this.lifeDelta = 0.0f;
        this.haveFuel = false;
        this.updateAccumulator = 0.0f;
        this.lastUpdateHours = -1.0f;
        this.keyId = -1;
        this.lockedByKey = false;
        this.lockedByPadlock = false;
        this.canBeLockByPadlock = false;
        this.lockedByCode = 0;
        this.OldNumPlanks = 0;
        this.thumpSound = "ZombieThumpGeneric";
    }
    
    public void setCorner(final boolean isCorner) {
        this.isCorner = isCorner;
    }
    
    public void setCanBarricade(final boolean canBarricade) {
        this.canBarricade = canBarricade;
    }
    
    public boolean getCanBarricade() {
        return this.canBarricade;
    }
    
    public void setHealth(final int health) {
        this.Health = health;
    }
    
    public int getHealth() {
        return this.Health;
    }
    
    public void setMaxHealth(final int maxHealth) {
        this.MaxHealth = maxHealth;
    }
    
    public int getMaxHealth() {
        return this.MaxHealth;
    }
    
    public void setThumpDmg(final Integer n) {
        this.thumpDmg = n;
    }
    
    public int getThumpDmg() {
        return this.thumpDmg;
    }
    
    public void setBreakSound(final String breakSound) {
        this.breakSound = breakSound;
    }
    
    public String getBreakSound() {
        return this.breakSound;
    }
    
    public boolean isDoor() {
        return this.isDoor;
    }
    
    @Override
    public boolean getNorth() {
        return this.north;
    }
    
    @Override
    public Vector2 getFacingPosition(final Vector2 vector2) {
        if (this.square == null) {
            return vector2.set(0.0f, 0.0f);
        }
        if (!this.isDoor && !this.isDoorFrame && !this.isWindow() && !this.isHoppable && (this.getProperties() == null || (!this.getProperties().Is(IsoFlagType.collideN) && !this.getProperties().Is(IsoFlagType.collideW)))) {
            return vector2.set(this.getX() + 0.5f, this.getY() + 0.5f);
        }
        if (this.north) {
            return vector2.set(this.getX() + 0.5f, this.getY());
        }
        return vector2.set(this.getX(), this.getY() + 0.5f);
    }
    
    public boolean isDoorFrame() {
        return this.isDoorFrame;
    }
    
    public void setIsDoor(final boolean b) {
        this.isDoor = b;
    }
    
    public void setIsDoorFrame(final boolean b) {
        this.isDoorFrame = b;
    }
    
    @Override
    public void setSprite(final String s) {
        this.closedSprite = IsoSpriteManager.instance.getSprite(s);
        this.sprite = this.closedSprite;
    }
    
    @Override
    public void setSpriteFromName(final String s) {
        this.sprite = IsoSpriteManager.instance.getSprite(s);
    }
    
    public void setClosedSprite(final IsoSprite closedSprite) {
        this.closedSprite = closedSprite;
        this.sprite = this.closedSprite;
    }
    
    public void setOpenSprite(final IsoSprite openSprite) {
        this.openSprite = openSprite;
    }
    
    public IsoThumpable(final IsoCell isoCell, final IsoGridSquare square, final String s, final String s2, final boolean north, final KahluaTable table) {
        this.isDoor = false;
        this.isDoorFrame = false;
        this.breakSound = "BreakObject";
        this.isCorner = false;
        this.isFloor = false;
        this.blockAllTheSquare = false;
        this.Locked = false;
        this.MaxHealth = 500;
        this.Health = 500;
        this.PushedMaxStrength = 0;
        this.PushedStrength = 0;
        this.north = false;
        this.thumpDmg = 8;
        this.crossSpeed = 1.0f;
        this.open = false;
        this.destroyed = false;
        this.canBarricade = false;
        this.canPassThrough = false;
        this.isStairs = false;
        this.isContainer = false;
        this.dismantable = false;
        this.canBePlastered = false;
        this.paintable = false;
        this.isThumpable = true;
        this.isHoppable = false;
        this.lightSourceRadius = -1;
        this.lightSourceLife = -1;
        this.lightSourceXOffset = 0;
        this.lightSourceYOffset = 0;
        this.lightSourceOn = false;
        this.lightSource = null;
        this.lightSourceFuel = null;
        this.lifeLeft = -1.0f;
        this.lifeDelta = 0.0f;
        this.haveFuel = false;
        this.updateAccumulator = 0.0f;
        this.lastUpdateHours = -1.0f;
        this.keyId = -1;
        this.lockedByKey = false;
        this.lockedByPadlock = false;
        this.canBeLockByPadlock = false;
        this.lockedByCode = 0;
        this.OldNumPlanks = 0;
        this.thumpSound = "ZombieThumpGeneric";
        this.OutlineOnMouseover = true;
        final int n = 2500;
        this.PushedStrength = n;
        this.PushedMaxStrength = n;
        this.openSprite = IsoSpriteManager.instance.getSprite(s2);
        this.closedSprite = IsoSpriteManager.instance.getSprite(s);
        this.table = table;
        this.sprite = this.closedSprite;
        this.square = square;
        this.north = north;
    }
    
    public IsoThumpable(final IsoCell isoCell, final IsoGridSquare square, final String s, final boolean north, final KahluaTable table) {
        this.isDoor = false;
        this.isDoorFrame = false;
        this.breakSound = "BreakObject";
        this.isCorner = false;
        this.isFloor = false;
        this.blockAllTheSquare = false;
        this.Locked = false;
        this.MaxHealth = 500;
        this.Health = 500;
        this.PushedMaxStrength = 0;
        this.PushedStrength = 0;
        this.north = false;
        this.thumpDmg = 8;
        this.crossSpeed = 1.0f;
        this.open = false;
        this.destroyed = false;
        this.canBarricade = false;
        this.canPassThrough = false;
        this.isStairs = false;
        this.isContainer = false;
        this.dismantable = false;
        this.canBePlastered = false;
        this.paintable = false;
        this.isThumpable = true;
        this.isHoppable = false;
        this.lightSourceRadius = -1;
        this.lightSourceLife = -1;
        this.lightSourceXOffset = 0;
        this.lightSourceYOffset = 0;
        this.lightSourceOn = false;
        this.lightSource = null;
        this.lightSourceFuel = null;
        this.lifeLeft = -1.0f;
        this.lifeDelta = 0.0f;
        this.haveFuel = false;
        this.updateAccumulator = 0.0f;
        this.lastUpdateHours = -1.0f;
        this.keyId = -1;
        this.lockedByKey = false;
        this.lockedByPadlock = false;
        this.canBeLockByPadlock = false;
        this.lockedByCode = 0;
        this.OldNumPlanks = 0;
        this.thumpSound = "ZombieThumpGeneric";
        this.OutlineOnMouseover = true;
        final int n = 2500;
        this.PushedStrength = n;
        this.PushedMaxStrength = n;
        this.closedSprite = IsoSpriteManager.instance.getSprite(s);
        this.table = table;
        this.sprite = this.closedSprite;
        this.square = square;
        this.north = north;
    }
    
    @Override
    public void load(final ByteBuffer byteBuffer, final int n, final boolean b) throws IOException {
        super.load(byteBuffer, n, b);
        final BitHeaderRead allocRead = BitHeader.allocRead(BitHeader.HeaderSize.Long, byteBuffer);
        this.OutlineOnMouseover = true;
        final int n2 = 2500;
        this.PushedStrength = n2;
        this.PushedMaxStrength = n2;
        if (!allocRead.equals(0)) {
            this.open = allocRead.hasFlags(1);
            this.Locked = allocRead.hasFlags(2);
            this.north = allocRead.hasFlags(4);
            if (allocRead.hasFlags(8)) {
                this.MaxHealth = byteBuffer.getInt();
            }
            if (allocRead.hasFlags(16)) {
                this.Health = byteBuffer.getInt();
            }
            else {
                this.Health = this.MaxHealth;
            }
            if (allocRead.hasFlags(32)) {
                this.closedSprite = IsoSprite.getSprite(IsoSpriteManager.instance, byteBuffer.getInt());
            }
            if (allocRead.hasFlags(64)) {
                this.openSprite = IsoSprite.getSprite(IsoSpriteManager.instance, byteBuffer.getInt());
            }
            if (allocRead.hasFlags(128)) {
                this.thumpDmg = byteBuffer.getInt();
            }
            this.isDoor = allocRead.hasFlags(512);
            this.isDoorFrame = allocRead.hasFlags(1024);
            this.isCorner = allocRead.hasFlags(2048);
            this.isStairs = allocRead.hasFlags(4096);
            this.isContainer = allocRead.hasFlags(8192);
            this.isFloor = allocRead.hasFlags(16384);
            this.canBarricade = allocRead.hasFlags(32768);
            this.canPassThrough = allocRead.hasFlags(65536);
            this.dismantable = allocRead.hasFlags(131072);
            this.canBePlastered = allocRead.hasFlags(262144);
            this.paintable = allocRead.hasFlags(524288);
            if (allocRead.hasFlags(1048576)) {
                this.crossSpeed = byteBuffer.getFloat();
            }
            if (allocRead.hasFlags(2097152)) {
                if (this.table == null) {
                    this.table = LuaManager.platform.newTable();
                }
                this.table.load(byteBuffer, n);
            }
            if (allocRead.hasFlags(4194304)) {
                if (this.modData == null) {
                    this.modData = LuaManager.platform.newTable();
                }
                this.modData.load(byteBuffer, n);
            }
            this.blockAllTheSquare = allocRead.hasFlags(8388608);
            this.isThumpable = allocRead.hasFlags(16777216);
            this.isHoppable = allocRead.hasFlags(33554432);
            if (allocRead.hasFlags(67108864)) {
                this.setLightSourceLife(byteBuffer.getInt());
            }
            if (allocRead.hasFlags(134217728)) {
                this.setLightSourceRadius(byteBuffer.getInt());
            }
            if (allocRead.hasFlags(268435456)) {
                this.setLightSourceXOffset(byteBuffer.getInt());
            }
            if (allocRead.hasFlags(536870912)) {
                this.setLightSourceYOffset(byteBuffer.getInt());
            }
            if (allocRead.hasFlags(1073741824)) {
                this.setLightSourceFuel(WorldDictionary.getItemTypeFromID(byteBuffer.getShort()));
            }
            if (allocRead.hasFlags(2147483648L)) {
                this.setLifeDelta(byteBuffer.getFloat());
            }
            if (allocRead.hasFlags(4294967296L)) {
                this.setLifeLeft(byteBuffer.getFloat());
            }
            if (allocRead.hasFlags(8589934592L)) {
                this.keyId = byteBuffer.getInt();
            }
            this.lockedByKey = allocRead.hasFlags(17179869184L);
            this.lockedByPadlock = allocRead.hasFlags(34359738368L);
            this.canBeLockByPadlock = allocRead.hasFlags(68719476736L);
            if (allocRead.hasFlags(137438953472L)) {
                this.lockedByCode = byteBuffer.getInt();
            }
            if (allocRead.hasFlags(274877906944L)) {
                this.thumpSound = GameWindow.ReadString(byteBuffer);
                if ("thumpa2".equals(this.thumpSound)) {
                    this.thumpSound = "ZombieThumpGeneric";
                }
                if ("metalthump".equals(this.thumpSound)) {
                    this.thumpSound = "ZombieThumpMetal";
                }
            }
            if (allocRead.hasFlags(549755813888L)) {
                this.lastUpdateHours = byteBuffer.getFloat();
            }
            if (n >= 183) {
                if (allocRead.hasFlags(1099511627776L)) {
                    this.haveFuel = true;
                }
                if (allocRead.hasFlags(2199023255552L)) {
                    this.lightSourceOn = true;
                }
            }
        }
        allocRead.release();
        if (this.getLightSourceFuel() != null) {
            final boolean lightSourceOn = this.isLightSourceOn();
            this.createLightSource(this.getLightSourceRadius(), this.getLightSourceXOffset(), this.getLightSourceYOffset(), 0, this.getLightSourceLife(), this.getLightSourceFuel(), null, null);
            if (this.lightSource != null) {
                this.getLightSource().setActive(lightSourceOn);
            }
            this.setLightSourceOn(lightSourceOn);
        }
        if (SystemDisabler.doObjectStateSyncEnable && GameClient.bClient) {
            GameClient.instance.objectSyncReq.putRequestLoad(this.square);
        }
    }
    
    @Override
    public void save(final ByteBuffer byteBuffer, final boolean b) throws IOException {
        super.save(byteBuffer, b);
        final BitHeaderWrite allocWrite = BitHeader.allocWrite(BitHeader.HeaderSize.Long, byteBuffer);
        if (this.open) {
            allocWrite.addFlags(1);
        }
        if (this.Locked) {
            allocWrite.addFlags(2);
        }
        if (this.north) {
            allocWrite.addFlags(4);
        }
        if (this.MaxHealth != 500) {
            allocWrite.addFlags(8);
            byteBuffer.putInt(this.MaxHealth);
        }
        if (this.Health != this.MaxHealth) {
            allocWrite.addFlags(16);
            byteBuffer.putInt(this.Health);
        }
        if (this.closedSprite != null) {
            allocWrite.addFlags(32);
            byteBuffer.putInt(this.closedSprite.ID);
        }
        if (this.openSprite != null) {
            allocWrite.addFlags(64);
            byteBuffer.putInt(this.openSprite.ID);
        }
        if (this.thumpDmg != 8) {
            allocWrite.addFlags(128);
            byteBuffer.putInt(this.thumpDmg);
        }
        if (this.isDoor) {
            allocWrite.addFlags(512);
        }
        if (this.isDoorFrame) {
            allocWrite.addFlags(1024);
        }
        if (this.isCorner) {
            allocWrite.addFlags(2048);
        }
        if (this.isStairs) {
            allocWrite.addFlags(4096);
        }
        if (this.isContainer) {
            allocWrite.addFlags(8192);
        }
        if (this.isFloor) {
            allocWrite.addFlags(16384);
        }
        if (this.canBarricade) {
            allocWrite.addFlags(32768);
        }
        if (this.canPassThrough) {
            allocWrite.addFlags(65536);
        }
        if (this.dismantable) {
            allocWrite.addFlags(131072);
        }
        if (this.canBePlastered) {
            allocWrite.addFlags(262144);
        }
        if (this.paintable) {
            allocWrite.addFlags(524288);
        }
        if (this.crossSpeed != 1.0f) {
            allocWrite.addFlags(1048576);
            byteBuffer.putFloat(this.crossSpeed);
        }
        if (this.table != null && !this.table.isEmpty()) {
            allocWrite.addFlags(2097152);
            this.table.save(byteBuffer);
        }
        if (this.modData != null && !this.modData.isEmpty()) {
            allocWrite.addFlags(4194304);
            this.modData.save(byteBuffer);
        }
        if (this.blockAllTheSquare) {
            allocWrite.addFlags(8388608);
        }
        if (this.isThumpable) {
            allocWrite.addFlags(16777216);
        }
        if (this.isHoppable) {
            allocWrite.addFlags(33554432);
        }
        if (this.getLightSourceLife() != -1) {
            allocWrite.addFlags(67108864);
            byteBuffer.putInt(this.getLightSourceLife());
        }
        if (this.getLightSourceRadius() != -1) {
            allocWrite.addFlags(134217728);
            byteBuffer.putInt(this.getLightSourceRadius());
        }
        if (this.getLightSourceXOffset() != 0) {
            allocWrite.addFlags(268435456);
            byteBuffer.putInt(this.getLightSourceXOffset());
        }
        if (this.getLightSourceYOffset() != 0) {
            allocWrite.addFlags(536870912);
            byteBuffer.putInt(this.getLightSourceYOffset());
        }
        if (this.getLightSourceFuel() != null) {
            allocWrite.addFlags(1073741824);
            byteBuffer.putShort(WorldDictionary.getItemRegistryID(this.getLightSourceFuel()));
        }
        if (this.getLifeDelta() != 0.0f) {
            allocWrite.addFlags(2147483648L);
            byteBuffer.putFloat(this.getLifeDelta());
        }
        if (this.getLifeLeft() != -1.0f) {
            allocWrite.addFlags(4294967296L);
            byteBuffer.putFloat(this.getLifeLeft());
        }
        if (this.keyId != -1) {
            allocWrite.addFlags(8589934592L);
            byteBuffer.putInt(this.keyId);
        }
        if (this.isLockedByKey()) {
            allocWrite.addFlags(17179869184L);
        }
        if (this.isLockedByPadlock()) {
            allocWrite.addFlags(34359738368L);
        }
        if (this.canBeLockByPadlock()) {
            allocWrite.addFlags(68719476736L);
        }
        if (this.getLockedByCode() != 0) {
            allocWrite.addFlags(137438953472L);
            byteBuffer.putInt(this.getLockedByCode());
        }
        if (!this.thumpSound.equals("ZombieThumbGeneric")) {
            allocWrite.addFlags(274877906944L);
            GameWindow.WriteString(byteBuffer, this.thumpSound);
        }
        if (this.lastUpdateHours != -1.0f) {
            allocWrite.addFlags(549755813888L);
            byteBuffer.putFloat(this.lastUpdateHours);
        }
        if (this.haveFuel) {
            allocWrite.addFlags(1099511627776L);
        }
        if (this.lightSourceOn) {
            allocWrite.addFlags(2199023255552L);
        }
        allocWrite.write();
        allocWrite.release();
    }
    
    @Override
    public boolean isDestroyed() {
        return this.destroyed;
    }
    
    public boolean IsOpen() {
        return this.open;
    }
    
    public boolean IsStrengthenedByPushedItems() {
        return false;
    }
    
    @Override
    public boolean onMouseLeftClick(final int n, final int n2) {
        return false;
    }
    
    @Override
    public boolean TestPathfindCollide(final IsoMovingObject isoMovingObject, final IsoGridSquare isoGridSquare, final IsoGridSquare isoGridSquare2) {
        final boolean north = this.north;
        if (isoMovingObject instanceof IsoSurvivor && ((IsoSurvivor)isoMovingObject).getInventory().contains("Hammer")) {
            return false;
        }
        if (this.open) {
            return false;
        }
        if (isoGridSquare == this.square) {
            if (north && isoGridSquare2.getY() < isoGridSquare.getY()) {
                return true;
            }
            if (!north && isoGridSquare2.getX() < isoGridSquare.getX()) {
                return true;
            }
        }
        else {
            if (north && isoGridSquare2.getY() > isoGridSquare.getY()) {
                return true;
            }
            if (!north && isoGridSquare2.getX() > isoGridSquare.getX()) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean TestCollide(final IsoMovingObject isoMovingObject, final IsoGridSquare isoGridSquare, final IsoGridSquare isoGridSquare2) {
        if (isoMovingObject instanceof IsoPlayer && ((IsoPlayer)isoMovingObject).isNoClip()) {
            return false;
        }
        final boolean north = this.north;
        if (this.open) {
            return false;
        }
        if (!this.blockAllTheSquare) {
            if (isoGridSquare == this.square) {
                if (north && isoGridSquare2.getY() < isoGridSquare.getY()) {
                    if (isoMovingObject != null) {
                        isoMovingObject.collideWith(this);
                    }
                    if (!this.canPassThrough && !this.isStairs && !this.isCorner) {
                        return true;
                    }
                }
                if (!north && isoGridSquare2.getX() < isoGridSquare.getX()) {
                    if (isoMovingObject != null) {
                        isoMovingObject.collideWith(this);
                    }
                    if (!this.canPassThrough && !this.isStairs && !this.isCorner) {
                        return true;
                    }
                }
            }
            else {
                if (north && isoGridSquare2.getY() > isoGridSquare.getY()) {
                    if (isoMovingObject != null) {
                        isoMovingObject.collideWith(this);
                    }
                    if (!this.canPassThrough && !this.isStairs && !this.isCorner) {
                        return true;
                    }
                }
                if (!north && isoGridSquare2.getX() > isoGridSquare.getX()) {
                    if (isoMovingObject != null) {
                        isoMovingObject.collideWith(this);
                    }
                    if (!this.canPassThrough && !this.isStairs && !this.isCorner) {
                        return true;
                    }
                }
            }
            if (this.isCorner) {
                if (isoGridSquare2.getY() < isoGridSquare.getY() && isoGridSquare2.getX() < isoGridSquare.getX()) {
                    if (isoMovingObject != null) {
                        isoMovingObject.collideWith(this);
                    }
                    if (!this.canPassThrough) {
                        return true;
                    }
                }
                if (isoGridSquare2.getY() > isoGridSquare.getY() && isoGridSquare2.getX() > isoGridSquare.getX()) {
                    if (isoMovingObject != null) {
                        isoMovingObject.collideWith(this);
                    }
                    if (!this.canPassThrough) {
                        return true;
                    }
                }
            }
            return false;
        }
        if (isoGridSquare != this.square) {
            if (isoMovingObject != null) {
                isoMovingObject.collideWith(this);
            }
            return true;
        }
        return false;
    }
    
    @Override
    public VisionResult TestVision(final IsoGridSquare isoGridSquare, final IsoGridSquare isoGridSquare2) {
        if (this.canPassThrough) {
            return VisionResult.NoEffect;
        }
        boolean north = this.north;
        if (this.open) {
            north = !north;
        }
        if (isoGridSquare2.getZ() != isoGridSquare.getZ()) {
            return VisionResult.NoEffect;
        }
        final boolean b = this.sprite != null && this.sprite.getProperties().Is("doorTrans");
        if (isoGridSquare == this.square) {
            if (north && isoGridSquare2.getY() < isoGridSquare.getY()) {
                if (b) {
                    return VisionResult.Unblocked;
                }
                if (this.isWindow()) {
                    return VisionResult.Unblocked;
                }
                return VisionResult.Blocked;
            }
            else if (!north && isoGridSquare2.getX() < isoGridSquare.getX()) {
                if (b) {
                    return VisionResult.Unblocked;
                }
                if (this.isWindow()) {
                    return VisionResult.Unblocked;
                }
                return VisionResult.Blocked;
            }
        }
        else if (north && isoGridSquare2.getY() > isoGridSquare.getY()) {
            if (b) {
                return VisionResult.Unblocked;
            }
            if (this.isWindow()) {
                return VisionResult.Unblocked;
            }
            return VisionResult.Blocked;
        }
        else if (!north && isoGridSquare2.getX() > isoGridSquare.getX()) {
            if (b) {
                return VisionResult.Unblocked;
            }
            if (this.isWindow()) {
                return VisionResult.Unblocked;
            }
            return VisionResult.Blocked;
        }
        return VisionResult.NoEffect;
    }
    
    @Override
    public void Thump(final IsoMovingObject isoMovingObject) {
        if (!SandboxOptions.instance.Lore.ThumpOnConstruction.getValue()) {
            return;
        }
        if (isoMovingObject instanceof IsoGameCharacter) {
            final Thumpable thumpable = this.getThumpableFor((IsoGameCharacter)isoMovingObject);
            if (thumpable == null) {
                return;
            }
            if (thumpable != this) {
                thumpable.Thump(isoMovingObject);
                return;
            }
        }
        final boolean breakableObject = BrokenFences.getInstance().isBreakableObject(this);
        if (isoMovingObject instanceof IsoZombie) {
            if (((IsoZombie)isoMovingObject).cognition == 1 && this.isDoor() && !this.IsOpen() && !this.isLocked()) {
                this.ToggleDoor((IsoGameCharacter)isoMovingObject);
                return;
            }
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
            final int thumpDmg = this.thumpDmg;
            if (size >= thumpDmg) {
                this.Health -= 1 * ThumpState.getFastForwardDamageMultiplier();
            }
            else {
                this.partialThumpDmg += size / (float)thumpDmg * ThumpState.getFastForwardDamageMultiplier();
                if ((int)this.partialThumpDmg > 0) {
                    final int n = (int)this.partialThumpDmg;
                    this.Health -= n;
                    this.partialThumpDmg -= n;
                }
            }
            WorldSoundManager.instance.addSound(isoMovingObject, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, true, 4.0f, 15.0f);
            if (this.isDoor()) {
                this.setRenderEffect(RenderEffectType.Hit_Door, true);
            }
        }
        if (this.Health <= 0) {
            ((IsoGameCharacter)isoMovingObject).getEmitter().playSound(this.breakSound, this);
            if (GameServer.bServer) {
                GameServer.PlayWorldSoundServer(this.breakSound, false, isoMovingObject.getCurrentSquare(), 0.2f, 20.0f, 1.1f, true);
            }
            WorldSoundManager.instance.addSound(null, this.square.getX(), this.square.getY(), this.square.getZ(), 10, 20, true, 4.0f, 15.0f);
            isoMovingObject.setThumpTarget(null);
            if (IsoDoor.destroyDoubleDoor(this)) {
                return;
            }
            if (IsoDoor.destroyGarageDoor(this)) {
                return;
            }
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
            this.destroy();
        }
    }
    
    @Override
    public Thumpable getThumpableFor(final IsoGameCharacter isoGameCharacter) {
        if (this.isDoor() || this.isWindow()) {
            final IsoBarricade barricadeForCharacter = this.getBarricadeForCharacter(isoGameCharacter);
            if (barricadeForCharacter != null) {
                return barricadeForCharacter;
            }
            final IsoBarricade barricadeOppositeCharacter = this.getBarricadeOppositeCharacter(isoGameCharacter);
            if (barricadeOppositeCharacter != null) {
                return barricadeOppositeCharacter;
            }
        }
        int isThumpable = this.isThumpable ? 1 : 0;
        final boolean b = isoGameCharacter instanceof IsoZombie && ((IsoZombie)isoGameCharacter).isCrawling();
        if (isThumpable == 0 && b && BrokenFences.getInstance().isBreakableObject(this)) {
            isThumpable = 1;
        }
        if (isThumpable == 0 && b && this.isHoppable()) {
            isThumpable = 1;
        }
        if (isThumpable == 0 || this.isDestroyed()) {
            return null;
        }
        if ((this.isDoor() && this.IsOpen()) || this.isWindow()) {
            return null;
        }
        if (!b && this.isHoppable()) {
            return null;
        }
        return this;
    }
    
    @Override
    public float getThumpCondition() {
        return PZMath.clamp(this.Health, 0, this.MaxHealth) / (float)this.MaxHealth;
    }
    
    @Override
    public void WeaponHit(final IsoGameCharacter isoGameCharacter, final HandWeapon handWeapon) {
        final IsoPlayer isoPlayer = Type.tryCastTo(isoGameCharacter, IsoPlayer.class);
        if (GameClient.bClient) {
            if (isoPlayer != null) {
                GameClient.instance.sendWeaponHit(isoPlayer, handWeapon, this);
            }
            if (this.isDoor()) {
                this.setRenderEffect(RenderEffectType.Hit_Door, true);
            }
            return;
        }
        final Thumpable thumpable = this.getThumpableFor(isoGameCharacter);
        if (thumpable == null) {
            return;
        }
        if (thumpable instanceof IsoBarricade) {
            ((IsoBarricade)thumpable).WeaponHit(isoGameCharacter, handWeapon);
            return;
        }
        this.Damage(handWeapon.getDoorDamage());
        if (handWeapon.getDoorHitSound() != null) {
            if (isoPlayer != null) {
                final String soundPrefix = this.getSoundPrefix();
                switch (soundPrefix) {
                    case "GarageDoor":
                    case "MetalDoor":
                    case "MetalGate":
                    case "PrisonMetalDoor": {
                        isoPlayer.setMeleeHitSurface(ParameterMeleeHitSurface.Material.Metal);
                        break;
                    }
                    case "SlidingGlassDoor": {
                        isoPlayer.setMeleeHitSurface(ParameterMeleeHitSurface.Material.Glass);
                        break;
                    }
                    default: {
                        isoPlayer.setMeleeHitSurface(ParameterMeleeHitSurface.Material.Wood);
                        break;
                    }
                }
            }
            isoGameCharacter.getEmitter().playSound(handWeapon.getDoorHitSound(), this);
            if (GameServer.bServer) {
                GameServer.PlayWorldSoundServer(handWeapon.getDoorHitSound(), false, this.getSquare(), 0.2f, 20.0f, 1.0f, false);
            }
        }
        WorldSoundManager.instance.addSound(isoGameCharacter, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, false, 0.0f, 15.0f);
        if (this.isDoor()) {
            this.setRenderEffect(RenderEffectType.Hit_Door, true);
        }
        if ((!this.IsStrengthenedByPushedItems() && this.Health <= 0) || (this.IsStrengthenedByPushedItems() && this.Health <= -this.PushedMaxStrength)) {
            isoGameCharacter.getEmitter().playSound(this.breakSound, this);
            WorldSoundManager.instance.addSound(isoGameCharacter, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, false, 0.0f, 15.0f);
            if (GameClient.bClient) {
                GameClient.instance.sendClientCommandV(null, "object", "OnDestroyIsoThumpable", "x", (int)this.getX(), "y", (int)this.getY(), "z", (int)this.getZ(), "index", this.getObjectIndex());
            }
            LuaEventManager.triggerEvent("OnDestroyIsoThumpable", this, null);
            if (IsoDoor.destroyDoubleDoor(this)) {
                return;
            }
            if (IsoDoor.destroyGarageDoor(this)) {
                return;
            }
            this.destroyed = true;
            if (this.getObjectIndex() != -1) {
                this.square.transmitRemoveItemFromSquare(this);
            }
        }
    }
    
    public IsoGridSquare getOtherSideOfDoor(final IsoGameCharacter isoGameCharacter) {
        if (this.north) {
            if (isoGameCharacter.getCurrentSquare().getRoom() == this.square.getRoom()) {
                return IsoWorld.instance.CurrentCell.getGridSquare(this.square.getX(), this.square.getY() - 1, this.square.getZ());
            }
            return IsoWorld.instance.CurrentCell.getGridSquare(this.square.getX(), this.square.getY(), this.square.getZ());
        }
        else {
            if (isoGameCharacter.getCurrentSquare().getRoom() == this.square.getRoom()) {
                return IsoWorld.instance.CurrentCell.getGridSquare(this.square.getX() - 1, this.square.getY(), this.square.getZ());
            }
            return IsoWorld.instance.CurrentCell.getGridSquare(this.square.getX(), this.square.getY(), this.square.getZ());
        }
    }
    
    public void ToggleDoorActual(final IsoGameCharacter isoGameCharacter) {
        if (this.isBarricaded()) {
            if (isoGameCharacter != null) {
                this.playDoorSound(isoGameCharacter.getEmitter(), "Blocked");
                isoGameCharacter.setHaloNote(Translator.getText("IGUI_PlayerText_DoorBarricaded"), 255, 255, 255, 256.0f);
                this.setRenderEffect(RenderEffectType.Hit_Door, true);
            }
            return;
        }
        if (this.isLockedByKey() && isoGameCharacter instanceof IsoPlayer && isoGameCharacter.getCurrentSquare().Is(IsoFlagType.exterior) && isoGameCharacter.getInventory().haveThisKeyId(this.getKeyId()) == null) {
            this.playDoorSound(isoGameCharacter.getEmitter(), "Locked");
            this.setRenderEffect(RenderEffectType.Hit_Door, true);
            return;
        }
        if (this.isLockedByKey() && isoGameCharacter instanceof IsoPlayer && isoGameCharacter.getInventory().haveThisKeyId(this.getKeyId()) != null) {
            this.playDoorSound(isoGameCharacter.getEmitter(), "Unlock");
            this.setIsLocked(false);
            this.setLockedByKey(false);
        }
        this.DirtySlice();
        this.square.InvalidateSpecialObjectPaths();
        if (this.Locked && isoGameCharacter instanceof IsoPlayer && isoGameCharacter.getCurrentSquare().Is(IsoFlagType.exterior) && !this.open) {
            this.playDoorSound(isoGameCharacter.getEmitter(), "Locked");
            this.setRenderEffect(RenderEffectType.Hit_Door, true);
            return;
        }
        if (isoGameCharacter instanceof IsoPlayer) {}
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            LosUtil.cachecleared[i] = true;
        }
        IsoGridSquare.setRecalcLightTime(-1);
        GameTime.instance.lightSourceUpdate = 100.0f;
        if (this.getSprite().getProperties().Is("DoubleDoor")) {
            if (IsoDoor.isDoubleDoorObstructed(this)) {
                if (isoGameCharacter != null) {
                    this.playDoorSound(isoGameCharacter.getEmitter(), "Blocked");
                    isoGameCharacter.setHaloNote(Translator.getText("IGUI_PlayerText_DoorBlocked"), 255, 255, 255, 256.0f);
                }
                return;
            }
            final boolean open = this.open;
            IsoDoor.toggleDoubleDoor(this, true);
            if (open != this.open) {
                this.playDoorSound(isoGameCharacter.getEmitter(), this.open ? "Open" : "Close");
            }
        }
        else {
            if (this.isObstructed()) {
                if (isoGameCharacter != null) {
                    this.playDoorSound(isoGameCharacter.getEmitter(), "Blocked");
                    isoGameCharacter.setHaloNote(Translator.getText("IGUI_PlayerText_DoorBlocked"), 255, 255, 255, 256.0f);
                }
                return;
            }
            this.sprite = this.closedSprite;
            this.open = !this.open;
            this.setLockedByKey(false);
            if (this.open) {
                this.playDoorSound(isoGameCharacter.getEmitter(), "Open");
                this.sprite = this.openSprite;
            }
            else {
                this.playDoorSound(isoGameCharacter.getEmitter(), "Close");
            }
            this.square.RecalcProperties();
            this.syncIsoObject(false, (byte)(this.open ? 1 : 0), null, null);
            LuaEventManager.triggerEvent("OnContainerUpdate");
        }
    }
    
    public void ToggleDoor(final IsoGameCharacter isoGameCharacter) {
        this.ToggleDoorActual(isoGameCharacter);
    }
    
    public void ToggleDoorSilent() {
        if (this.isBarricaded()) {
            return;
        }
        this.square.InvalidateSpecialObjectPaths();
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            LosUtil.cachecleared[i] = true;
        }
        IsoGridSquare.setRecalcLightTime(-1);
        this.open = !this.open;
        this.sprite = this.closedSprite;
        if (this.open) {
            this.sprite = this.openSprite;
        }
    }
    
    public boolean isObstructed() {
        return IsoDoor.isDoorObstructed(this);
    }
    
    @Override
    public boolean haveSheetRope() {
        return IsoWindow.isTopOfSheetRopeHere(this.square, this.north);
    }
    
    @Override
    public int countAddSheetRope() {
        if (this.isHoppable() || this.isWindow()) {
            return IsoWindow.countAddSheetRope(this.square, this.north);
        }
        return 0;
    }
    
    @Override
    public boolean canAddSheetRope() {
        return (this.isHoppable() || this.isWindow()) && IsoWindow.canAddSheetRope(this.square, this.north);
    }
    
    @Override
    public boolean addSheetRope(final IsoPlayer isoPlayer, final String s) {
        return this.canAddSheetRope() && IsoWindow.addSheetRope(isoPlayer, this.square, this.north, s);
    }
    
    @Override
    public boolean removeSheetRope(final IsoPlayer isoPlayer) {
        return this.haveSheetRope() && IsoWindow.removeSheetRope(isoPlayer, this.square, this.north);
    }
    
    public void createLightSource(final int lightSourceRadius, final int lightSourceXOffset, final int lightSourceYOffset, final int n, final int n2, final String lightSourceFuel, final InventoryItem inventoryItem, final IsoGameCharacter isoGameCharacter) {
        this.setLightSourceXOffset(lightSourceXOffset);
        this.setLightSourceYOffset(lightSourceYOffset);
        this.setLightSourceRadius(lightSourceRadius);
        this.setLightSourceFuel(lightSourceFuel);
        if (inventoryItem != null) {
            if (inventoryItem instanceof DrainableComboItem) {
                this.setLifeLeft(((DrainableComboItem)inventoryItem).getUsedDelta());
                this.setLifeDelta(((DrainableComboItem)inventoryItem).getUseDelta());
                this.setHaveFuel(!"Base.Torch".equals(inventoryItem.getFullType()) || ((DrainableComboItem)inventoryItem).getUsedDelta() > 0.0f);
            }
            else {
                this.setLifeLeft(1.0f);
                this.setHaveFuel(true);
            }
            isoGameCharacter.removeFromHands(inventoryItem);
            final IsoWorldInventoryObject worldItem = inventoryItem.getWorldItem();
            if (worldItem != null) {
                if (worldItem.getSquare() != null) {
                    worldItem.getSquare().transmitRemoveItemFromSquare(worldItem);
                    LuaEventManager.triggerEvent("OnContainerUpdate");
                }
            }
            else if (inventoryItem.getContainer() != null) {
                inventoryItem.getContainer().Remove(inventoryItem);
            }
        }
        this.setLightSourceOn(this.haveFuel);
        if (this.lightSource != null) {
            this.lightSource.setActive(this.isLightSourceOn());
        }
    }
    
    public InventoryItem insertNewFuel(final InventoryItem inventoryItem, final IsoGameCharacter isoGameCharacter) {
        if (inventoryItem != null) {
            final InventoryItem removeCurrentFuel = this.removeCurrentFuel(isoGameCharacter);
            if (isoGameCharacter != null) {
                isoGameCharacter.removeFromHands(inventoryItem);
                isoGameCharacter.getInventory().Remove(inventoryItem);
            }
            if (inventoryItem instanceof DrainableComboItem) {
                this.setLifeLeft(((DrainableComboItem)inventoryItem).getUsedDelta());
                this.setLifeDelta(((DrainableComboItem)inventoryItem).getUseDelta());
            }
            else {
                this.setLifeLeft(1.0f);
            }
            this.setHaveFuel(true);
            this.toggleLightSource(true);
            return removeCurrentFuel;
        }
        return null;
    }
    
    public InventoryItem removeCurrentFuel(final IsoGameCharacter isoGameCharacter) {
        if (this.haveFuel()) {
            final InventoryItem createItem = InventoryItemFactory.CreateItem(this.getLightSourceFuel());
            if (createItem instanceof DrainableComboItem) {
                ((DrainableComboItem)createItem).setUsedDelta(this.getLifeLeft());
            }
            if (isoGameCharacter != null) {
                isoGameCharacter.getInventory().AddItem(createItem);
            }
            this.setLifeLeft(0.0f);
            this.setLifeDelta(-1.0f);
            this.toggleLightSource(false);
            this.setHaveFuel(false);
            return createItem;
        }
        return null;
    }
    
    private int calcLightSourceX() {
        int n = (int)this.getX();
        final int n2 = (int)this.getY();
        if (this.lightSourceXOffset != 0) {
            for (int i = 1; i <= Math.abs(this.lightSourceXOffset); ++i) {
                final int n3 = (this.lightSourceXOffset > 0) ? 1 : -1;
                final LosUtil.TestResults lineClear = LosUtil.lineClear(this.getCell(), (int)this.getX(), (int)this.getY(), (int)this.getZ(), n + n3, n2, (int)this.getZ(), false);
                if (lineClear == LosUtil.TestResults.Blocked) {
                    break;
                }
                if (lineClear == LosUtil.TestResults.ClearThroughWindow) {
                    break;
                }
                n += n3;
            }
        }
        return n;
    }
    
    private int calcLightSourceY() {
        final int n = (int)this.getX();
        int n2 = (int)this.getY();
        if (this.lightSourceYOffset != 0) {
            for (int i = 1; i <= Math.abs(this.lightSourceYOffset); ++i) {
                final int n3 = (this.lightSourceYOffset > 0) ? 1 : -1;
                final LosUtil.TestResults lineClear = LosUtil.lineClear(this.getCell(), (int)this.getX(), (int)this.getY(), (int)this.getZ(), n, n2 + n3, (int)this.getZ(), false);
                if (lineClear == LosUtil.TestResults.Blocked) {
                    break;
                }
                if (lineClear == LosUtil.TestResults.ClearThroughWindow) {
                    break;
                }
                n2 += n3;
            }
        }
        return n2;
    }
    
    @Override
    public void update() {
        if (this.getObjectIndex() == -1) {
            return;
        }
        if (!GameServer.bServer) {
            if (this.lightSource != null && !this.lightSource.isInBounds()) {
                this.lightSource = null;
            }
            if (this.lightSourceFuel != null && !this.lightSourceFuel.isEmpty() && this.lightSource == null && this.square != null) {
                final int n = 0;
                final int calcLightSourceX = this.calcLightSourceX();
                final int calcLightSourceY = this.calcLightSourceY();
                if (IsoWorld.instance.CurrentCell.isInChunkMap(calcLightSourceX, calcLightSourceY)) {
                    final int lightSourceLife = this.getLightSourceLife();
                    this.setLightSource(new IsoLightSource(calcLightSourceX, calcLightSourceY, (int)this.getZ() + n, 1.0f, 1.0f, 1.0f, this.lightSourceRadius, (lightSourceLife > 0) ? lightSourceLife : -1));
                    this.lightSource.setActive(this.isLightSourceOn());
                    IsoWorld.instance.getCell().getLamppostPositions().add(this.getLightSource());
                }
            }
            if (this.lightSource != null && this.lightSource.isActive()) {
                final int n2 = 0;
                final int calcLightSourceX2 = this.calcLightSourceX();
                final int calcLightSourceY2 = this.calcLightSourceY();
                if (calcLightSourceX2 != this.lightSource.x || calcLightSourceY2 != this.lightSource.y) {
                    this.getCell().removeLamppost(this.lightSource);
                    final int lightSourceLife2 = this.getLightSourceLife();
                    this.setLightSource(new IsoLightSource(calcLightSourceX2, calcLightSourceY2, (int)this.getZ() + n2, 1.0f, 1.0f, 1.0f, this.lightSourceRadius, (lightSourceLife2 > 0) ? lightSourceLife2 : -1));
                    this.lightSource.setActive(this.isLightSourceOn());
                    IsoWorld.instance.getCell().getLamppostPositions().add(this.getLightSource());
                }
            }
        }
        if (this.getLifeLeft() > -1.0f) {
            final float lastUpdateHours = (float)GameTime.getInstance().getWorldAgeHours();
            if (this.lastUpdateHours == -1.0f) {
                this.lastUpdateHours = lastUpdateHours;
            }
            else if (this.lastUpdateHours > lastUpdateHours) {
                this.lastUpdateHours = lastUpdateHours;
            }
            final float n3 = lastUpdateHours - this.lastUpdateHours;
            this.lastUpdateHours = lastUpdateHours;
            if (this.isLightSourceOn()) {
                this.updateAccumulator += n3;
                final int n4 = (int)Math.floor(this.updateAccumulator / 0.004166667f);
                if (n4 > 0) {
                    this.updateAccumulator -= 0.004166667f * n4;
                    this.setLifeLeft(this.getLifeLeft() - this.getLifeDelta() * n4);
                    if (this.getLifeLeft() <= 0.0f) {
                        this.setLifeLeft(0.0f);
                        this.toggleLightSource(false);
                    }
                }
            }
            else {
                this.updateAccumulator = 0.0f;
            }
        }
        this.checkHaveElectricity();
    }
    
    void Damage(final int n) {
        if (!this.isThumpable()) {
            return;
        }
        this.DirtySlice();
        this.Health -= n;
    }
    
    public void destroy() {
        if (this.destroyed) {
            return;
        }
        if (this.getObjectIndex() == -1) {
            return;
        }
        if (GameClient.bClient) {
            GameClient.instance.sendClientCommandV(null, "object", "OnDestroyIsoThumpable", "x", this.square.getX(), "y", this.square.getY(), "z", this.square.getZ(), "index", this.getObjectIndex());
        }
        LuaEventManager.triggerEvent("OnDestroyIsoThumpable", this, null);
        this.Health = 0;
        this.destroyed = true;
        if (this.getObjectIndex() != -1) {
            this.square.transmitRemoveItemFromSquare(this);
        }
    }
    
    @Override
    public IsoBarricade getBarricadeOnSameSquare() {
        return IsoBarricade.GetBarricadeOnSquare(this.square, this.north ? IsoDirections.N : IsoDirections.W);
    }
    
    @Override
    public IsoBarricade getBarricadeOnOppositeSquare() {
        return IsoBarricade.GetBarricadeOnSquare(this.getOppositeSquare(), this.north ? IsoDirections.S : IsoDirections.E);
    }
    
    @Override
    public boolean isBarricaded() {
        IsoBarricade isoBarricade = this.getBarricadeOnSameSquare();
        if (isoBarricade == null) {
            isoBarricade = this.getBarricadeOnOppositeSquare();
        }
        return isoBarricade != null;
    }
    
    @Override
    public boolean isBarricadeAllowed() {
        return this.canBarricade;
    }
    
    @Override
    public IsoBarricade getBarricadeForCharacter(final IsoGameCharacter isoGameCharacter) {
        return IsoBarricade.GetBarricadeForCharacter(this, isoGameCharacter);
    }
    
    @Override
    public IsoBarricade getBarricadeOppositeCharacter(final IsoGameCharacter isoGameCharacter) {
        return IsoBarricade.GetBarricadeOppositeCharacter(this, isoGameCharacter);
    }
    
    public void setIsDoor(final Boolean isDoor) {
        this.isDoor = isDoor;
    }
    
    @Override
    public KahluaTable getTable() {
        return this.table;
    }
    
    @Override
    public void setTable(final KahluaTable table) {
        this.table = table;
    }
    
    public boolean canBePlastered() {
        return this.canBePlastered;
    }
    
    public void setCanBePlastered(final boolean canBePlastered) {
        this.canBePlastered = canBePlastered;
    }
    
    public boolean isPaintable() {
        return this.paintable;
    }
    
    public void setPaintable(final boolean paintable) {
        this.paintable = paintable;
    }
    
    public boolean isLocked() {
        return this.Locked;
    }
    
    public void setIsLocked(final boolean locked) {
        this.Locked = locked;
    }
    
    public boolean isThumpable() {
        return this.isBarricaded() || this.isThumpable;
    }
    
    public void setIsThumpable(final boolean isThumpable) {
        this.isThumpable = isThumpable;
    }
    
    public void setIsHoppable(final boolean hoppable) {
        this.setHoppable(hoppable);
    }
    
    public IsoSprite getOpenSprite() {
        return this.openSprite;
    }
    
    @Override
    public boolean isHoppable() {
        if (this.isDoor() && !this.IsOpen() && this.closedSprite != null) {
            final PropertyContainer properties = this.closedSprite.getProperties();
            return properties.Is(IsoFlagType.HoppableN) || properties.Is(IsoFlagType.HoppableW);
        }
        return (this.sprite != null && (this.sprite.getProperties().Is(IsoFlagType.HoppableN) || this.sprite.getProperties().Is(IsoFlagType.HoppableW))) || this.isHoppable;
    }
    
    public void setHoppable(final boolean isHoppable) {
        this.isHoppable = isHoppable;
    }
    
    public int getLightSourceRadius() {
        return this.lightSourceRadius;
    }
    
    public void setLightSourceRadius(final int lightSourceRadius) {
        this.lightSourceRadius = lightSourceRadius;
    }
    
    public int getLightSourceXOffset() {
        return this.lightSourceXOffset;
    }
    
    public void setLightSourceXOffset(final int lightSourceXOffset) {
        this.lightSourceXOffset = lightSourceXOffset;
    }
    
    public int getLightSourceYOffset() {
        return this.lightSourceYOffset;
    }
    
    public void setLightSourceYOffset(final int lightSourceYOffset) {
        this.lightSourceYOffset = lightSourceYOffset;
    }
    
    public int getLightSourceLife() {
        return this.lightSourceLife;
    }
    
    public void setLightSourceLife(final int lightSourceLife) {
        this.lightSourceLife = lightSourceLife;
    }
    
    public boolean isLightSourceOn() {
        return this.lightSourceOn;
    }
    
    public void setLightSourceOn(final boolean lightSourceOn) {
        this.lightSourceOn = lightSourceOn;
    }
    
    public IsoLightSource getLightSource() {
        return this.lightSource;
    }
    
    public void setLightSource(final IsoLightSource lightSource) {
        this.lightSource = lightSource;
    }
    
    public void toggleLightSource(final boolean b) {
        this.setLightSourceOn(b);
        if (this.lightSource == null) {
            return;
        }
        this.getLightSource().setActive(b);
        IsoGridSquare.setRecalcLightTime(-1);
        GameTime.instance.lightSourceUpdate = 100.0f;
    }
    
    public String getLightSourceFuel() {
        return this.lightSourceFuel;
    }
    
    public void setLightSourceFuel(String lightSourceFuel) {
        if (lightSourceFuel != null && lightSourceFuel.isEmpty()) {
            lightSourceFuel = null;
        }
        this.lightSourceFuel = lightSourceFuel;
    }
    
    public float getLifeLeft() {
        return this.lifeLeft;
    }
    
    public void setLifeLeft(final float lifeLeft) {
        this.lifeLeft = lifeLeft;
    }
    
    public float getLifeDelta() {
        return this.lifeDelta;
    }
    
    public void setLifeDelta(final float lifeDelta) {
        this.lifeDelta = lifeDelta;
    }
    
    public boolean haveFuel() {
        return this.haveFuel;
    }
    
    public void setHaveFuel(final boolean haveFuel) {
        this.haveFuel = haveFuel;
    }
    
    @Override
    public void syncIsoObjectSend(final ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putInt(this.square.getX());
        byteBufferWriter.putInt(this.square.getY());
        byteBufferWriter.putInt(this.square.getZ());
        byteBufferWriter.putByte((byte)this.square.getObjects().indexOf(this));
        byteBufferWriter.putByte((byte)1);
        if (this.getSprite() != null && this.getSprite().getProperties().Is("DoubleDoor")) {
            byteBufferWriter.putByte((byte)5);
            return;
        }
        if (this.open) {
            byteBufferWriter.putByte((byte)1);
        }
        else if (this.lockedByKey) {
            byteBufferWriter.putByte((byte)3);
        }
        else {
            byteBufferWriter.putByte((byte)4);
        }
    }
    
    @Override
    public void syncIsoObject(final boolean b, final byte b2, final UdpConnection udpConnection, final ByteBuffer byteBuffer) {
        if (this.square == null) {
            System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()));
            return;
        }
        if (this.getObjectIndex() == -1) {
            System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;III)Ljava/lang/String;, this.getClass().getSimpleName(), this.square.getX(), this.square.getY(), this.square.getZ()));
            return;
        }
        if (!this.isDoor()) {
            return;
        }
        short short1 = -1;
        if ((GameServer.bServer || GameClient.bClient) && byteBuffer != null) {
            short1 = byteBuffer.getShort();
        }
        if (GameClient.bClient && !b) {
            final short onlineID = IsoPlayer.getInstance().getOnlineID();
            final ByteBufferWriter startPacket = GameClient.connection.startPacket();
            PacketTypes.PacketType.SyncIsoObject.doPacket(startPacket);
            this.syncIsoObjectSend(startPacket);
            startPacket.putShort(onlineID);
            PacketTypes.PacketType.SyncIsoObject.send(GameClient.connection);
        }
        else if (GameServer.bServer && !b) {
            for (final UdpConnection udpConnection2 : GameServer.udpEngine.connections) {
                final ByteBufferWriter startPacket2 = udpConnection2.startPacket();
                PacketTypes.PacketType.SyncIsoObject.doPacket(startPacket2);
                this.syncIsoObjectSend(startPacket2);
                startPacket2.putShort(short1);
                PacketTypes.PacketType.SyncIsoObject.send(udpConnection2);
            }
        }
        else if (b) {
            if (GameClient.bClient && short1 != -1) {
                final IsoPlayer isoPlayer = GameClient.IDToPlayerMap.get(short1);
                if (isoPlayer != null) {
                    isoPlayer.networkAI.setNoCollision(1000L);
                }
            }
            if (b2 == 1) {
                this.open = true;
                this.sprite = this.openSprite;
                this.Locked = false;
            }
            else if (b2 == 0) {
                this.open = false;
                this.sprite = this.closedSprite;
            }
            else if (b2 == 3) {
                this.Locked = true;
                this.lockedByKey = true;
                this.open = false;
                this.sprite = this.closedSprite;
            }
            else if (b2 == 4) {
                this.Locked = false;
                this.lockedByKey = false;
                this.open = false;
                this.sprite = this.closedSprite;
            }
            if (GameServer.bServer) {
                for (final UdpConnection udpConnection3 : GameServer.udpEngine.connections) {
                    if (udpConnection != null && udpConnection3.getConnectedGUID() != udpConnection.getConnectedGUID()) {
                        final ByteBufferWriter startPacket3 = udpConnection3.startPacket();
                        PacketTypes.PacketType.SyncIsoObject.doPacket(startPacket3);
                        this.syncIsoObjectSend(startPacket3);
                        startPacket3.putShort(short1);
                        PacketTypes.PacketType.SyncIsoObject.send(udpConnection3);
                    }
                }
            }
            if (b2 == 5) {
                IsoDoor.toggleDoubleDoor(this, false);
                if (GameServer.bServer) {
                    ServerMap.instance.physicsCheck(this.square.getX(), this.square.getY());
                }
            }
            this.square.InvalidateSpecialObjectPaths();
            this.square.RecalcProperties();
            this.square.RecalcAllWithNeighbours(true);
            for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
                LosUtil.cachecleared[i] = true;
            }
            IsoGridSquare.setRecalcLightTime(-1);
            GameTime.instance.lightSourceUpdate = 100.0f;
            LuaEventManager.triggerEvent("OnContainerUpdate");
        }
    }
    
    @Override
    public void addToWorld() {
        super.addToWorld();
        this.getCell().addToProcessIsoObject(this);
    }
    
    @Override
    public void removeFromWorld() {
        if (this.lightSource != null) {
            IsoWorld.instance.CurrentCell.removeLamppost(this.lightSource);
        }
        super.removeFromWorld();
    }
    
    @Override
    public void saveChange(final String s, final KahluaTable kahluaTable, final ByteBuffer byteBuffer) {
        super.saveChange(s, kahluaTable, byteBuffer);
        if ("lightSource".equals(s)) {
            byteBuffer.put((byte)(this.lightSourceOn ? 1 : 0));
            byteBuffer.put((byte)(this.haveFuel ? 1 : 0));
            byteBuffer.putFloat(this.lifeLeft);
            byteBuffer.putFloat(this.lifeDelta);
        }
        else if ("paintable".equals(s)) {
            byteBuffer.put((byte)(this.isPaintable() ? 1 : 0));
        }
    }
    
    @Override
    public void loadChange(final String s, final ByteBuffer byteBuffer) {
        super.loadChange(s, byteBuffer);
        if ("lightSource".equals(s)) {
            final boolean b = byteBuffer.get() == 1;
            this.haveFuel = (byteBuffer.get() == 1);
            this.lifeLeft = byteBuffer.getFloat();
            this.lifeDelta = byteBuffer.getFloat();
            if (b != this.lightSourceOn) {
                this.toggleLightSource(b);
            }
        }
        else if ("paintable".equals(s)) {
            this.setPaintable(byteBuffer.get() == 1);
        }
    }
    
    public IsoCurtain HasCurtains() {
        final IsoGridSquare oppositeSquare = this.getOppositeSquare();
        if (oppositeSquare != null) {
            final IsoCurtain curtain = oppositeSquare.getCurtain(this.getNorth() ? IsoObjectType.curtainS : IsoObjectType.curtainE);
            if (curtain != null) {
                return curtain;
            }
        }
        return this.getSquare().getCurtain(this.getNorth() ? IsoObjectType.curtainN : IsoObjectType.curtainW);
    }
    
    public IsoGridSquare getInsideSquare() {
        if (this.north) {
            return this.square.getCell().getGridSquare(this.square.getX(), this.square.getY() - 1, this.square.getZ());
        }
        return this.square.getCell().getGridSquare(this.square.getX() - 1, this.square.getY(), this.square.getZ());
    }
    
    @Override
    public IsoGridSquare getOppositeSquare() {
        return this.getInsideSquare();
    }
    
    public boolean isAdjacentToSquare(final IsoGridSquare isoGridSquare) {
        final IsoGridSquare square = this.getSquare();
        if (square == null || isoGridSquare == null) {
            return false;
        }
        final int n = square.x - isoGridSquare.x;
        final int n2 = square.y - isoGridSquare.y;
        int x = square.x;
        int x2 = square.x;
        int y = square.y;
        int y2 = square.y;
        IsoGridSquare isoGridSquare2 = square;
        switch (this.getSpriteEdge(false)) {
            case N: {
                --x;
                ++x2;
                --y;
                if (n2 == 1) {
                    isoGridSquare2 = square.getAdjacentSquare(IsoDirections.N);
                    break;
                }
                break;
            }
            case S: {
                --x;
                ++x2;
                ++y2;
                if (n2 == -1) {
                    isoGridSquare2 = square.getAdjacentSquare(IsoDirections.S);
                    break;
                }
                break;
            }
            case W: {
                --y;
                ++y2;
                --x;
                if (n == 1) {
                    isoGridSquare2 = square.getAdjacentSquare(IsoDirections.W);
                    break;
                }
                break;
            }
            case E: {
                --y;
                ++y2;
                ++x2;
                if (n == -1) {
                    isoGridSquare2 = square.getAdjacentSquare(IsoDirections.E);
                    break;
                }
                break;
            }
            default: {
                return false;
            }
        }
        return isoGridSquare.x >= x && isoGridSquare.x <= x2 && isoGridSquare.y >= y && isoGridSquare.y <= y2 && !isoGridSquare2.isSomethingTo(isoGridSquare);
    }
    
    public IsoGridSquare getAddSheetSquare(final IsoGameCharacter isoGameCharacter) {
        if (isoGameCharacter == null || isoGameCharacter.getCurrentSquare() == null) {
            return null;
        }
        final IsoGridSquare currentSquare = isoGameCharacter.getCurrentSquare();
        final IsoGridSquare square = this.getSquare();
        if (this.north) {
            if (currentSquare.getY() < square.getY()) {
                return this.getCell().getGridSquare(square.x, square.y - 1, square.z);
            }
            return square;
        }
        else {
            if (currentSquare.getX() < square.getX()) {
                return this.getCell().getGridSquare(square.x - 1, square.y, square.z);
            }
            return square;
        }
    }
    
    public void addSheet(final IsoGameCharacter isoGameCharacter) {
        IsoGridSquare isoGridSquare = this.getIndoorSquare();
        IsoObjectType isoObjectType;
        if (this.north) {
            isoObjectType = IsoObjectType.curtainN;
            if (isoGridSquare != this.square) {
                isoObjectType = IsoObjectType.curtainS;
            }
        }
        else {
            isoObjectType = IsoObjectType.curtainW;
            if (isoGridSquare != this.square) {
                isoObjectType = IsoObjectType.curtainE;
            }
        }
        if (isoGameCharacter != null) {
            if (this.north) {
                if (isoGameCharacter.getY() < this.getY()) {
                    isoGridSquare = this.getCell().getGridSquare(this.getX(), this.getY() - 1.0f, this.getZ());
                    isoObjectType = IsoObjectType.curtainS;
                }
                else {
                    isoGridSquare = this.getSquare();
                    isoObjectType = IsoObjectType.curtainN;
                }
            }
            else if (isoGameCharacter.getX() < this.getX()) {
                isoGridSquare = this.getCell().getGridSquare(this.getX() - 1.0f, this.getY(), this.getZ());
                isoObjectType = IsoObjectType.curtainE;
            }
            else {
                isoGridSquare = this.getSquare();
                isoObjectType = IsoObjectType.curtainW;
            }
        }
        if (isoGridSquare == null) {
            return;
        }
        if (isoGridSquare.getCurtain(isoObjectType) != null) {
            return;
        }
        if (isoGridSquare != null) {
            int n = 16;
            if (isoObjectType == IsoObjectType.curtainE) {
                ++n;
            }
            if (isoObjectType == IsoObjectType.curtainS) {
                n += 3;
            }
            if (isoObjectType == IsoObjectType.curtainN) {
                n += 2;
            }
            n += 4;
            final IsoCurtain isoCurtain = new IsoCurtain(this.getCell(), isoGridSquare, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n), this.north);
            isoGridSquare.AddSpecialTileObject(isoCurtain);
            if (GameServer.bServer) {
                isoCurtain.transmitCompleteItemToClients();
                isoGameCharacter.sendObjectChange("removeOneOf", new Object[] { "type", "Sheet" });
            }
            else {
                isoGameCharacter.getInventory().RemoveOneOf("Sheet");
            }
        }
    }
    
    public IsoGridSquare getIndoorSquare() {
        if (this.square.getRoom() != null) {
            return this.square;
        }
        IsoGridSquare isoGridSquare;
        if (this.north) {
            isoGridSquare = IsoWorld.instance.CurrentCell.getGridSquare(this.square.getX(), this.square.getY() - 1, this.square.getZ());
        }
        else {
            isoGridSquare = IsoWorld.instance.CurrentCell.getGridSquare(this.square.getX() - 1, this.square.getY(), this.square.getZ());
        }
        if (isoGridSquare == null || isoGridSquare.getFloor() == null) {
            return this.square;
        }
        if (isoGridSquare.getRoom() != null) {
            return isoGridSquare;
        }
        if (this.square.getFloor() == null) {
            return isoGridSquare;
        }
        final String name = isoGridSquare.getFloor().getSprite().getName();
        if (name != null && name.startsWith("carpentry_02_")) {
            return isoGridSquare;
        }
        return this.square;
    }
    
    @Override
    public int getKeyId() {
        return this.keyId;
    }
    
    public void setKeyId(final int n, final boolean b) {
        if (b && this.keyId != n && GameClient.bClient) {
            this.keyId = n;
            this.syncIsoThumpable();
        }
        else {
            this.keyId = n;
        }
    }
    
    @Override
    public void setKeyId(final int n) {
        this.setKeyId(n, true);
    }
    
    public boolean isLockedByKey() {
        return this.lockedByKey;
    }
    
    public void setLockedByKey(final boolean lockedByKey) {
        final boolean b = lockedByKey != this.lockedByKey;
        this.setIsLocked(this.lockedByKey = lockedByKey);
        if (!GameServer.bServer && b) {
            if (lockedByKey) {
                this.syncIsoObject(false, (byte)3, null, null);
            }
            else {
                this.syncIsoObject(false, (byte)4, null, null);
            }
        }
    }
    
    public boolean isLockedByPadlock() {
        return this.lockedByPadlock;
    }
    
    public void syncIsoThumpable() {
        final ByteBufferWriter startPacket = GameClient.connection.startPacket();
        PacketTypes.PacketType.SyncThumpable.doPacket(startPacket);
        startPacket.putInt(this.square.getX());
        startPacket.putInt(this.square.getY());
        startPacket.putInt(this.square.getZ());
        final byte b = (byte)this.square.getObjects().indexOf(this);
        if (b == -1) {
            System.out.println(invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, this.square.getX(), this.square.getY(), this.square.getZ()));
            GameClient.connection.cancelPacket();
            return;
        }
        startPacket.putByte(b);
        startPacket.putInt(this.getLockedByCode());
        startPacket.putByte((byte)(this.lockedByPadlock ? 1 : 0));
        startPacket.putInt(this.getKeyId());
        PacketTypes.PacketType.SyncThumpable.send(GameClient.connection);
    }
    
    public void setLockedByPadlock(final boolean b) {
        if (this.lockedByPadlock != b && GameClient.bClient) {
            this.lockedByPadlock = b;
            this.syncIsoThumpable();
        }
        else {
            this.lockedByPadlock = b;
        }
    }
    
    public boolean canBeLockByPadlock() {
        return this.canBeLockByPadlock;
    }
    
    public void setCanBeLockByPadlock(final boolean canBeLockByPadlock) {
        this.canBeLockByPadlock = canBeLockByPadlock;
    }
    
    public int getLockedByCode() {
        return this.lockedByCode;
    }
    
    public void setLockedByCode(final int n) {
        if (this.lockedByCode != n && GameClient.bClient) {
            this.lockedByCode = n;
            this.syncIsoThumpable();
        }
        else {
            this.lockedByCode = n;
        }
    }
    
    public boolean isLockedToCharacter(final IsoGameCharacter isoGameCharacter) {
        return (!GameClient.bClient || !(isoGameCharacter instanceof IsoPlayer) || ((IsoPlayer)isoGameCharacter).accessLevel.equals("")) && (this.getLockedByCode() > 0 || (this.isLockedByPadlock() && (isoGameCharacter.getInventory() == null || isoGameCharacter.getInventory().haveThisKeyId(this.getKeyId()) == null)));
    }
    
    public boolean canClimbOver(final IsoGameCharacter isoGameCharacter) {
        return this.square != null && this.isHoppable() && (isoGameCharacter == null || IsoWindow.canClimbThroughHelper(isoGameCharacter, this.getSquare(), this.getOppositeSquare(), this.north));
    }
    
    public boolean canClimbThrough(final IsoGameCharacter isoGameCharacter) {
        return this.square != null && this.isWindow() && !this.isBarricaded() && (isoGameCharacter == null || IsoWindow.canClimbThroughHelper(isoGameCharacter, this.getSquare(), this.getOppositeSquare(), this.north));
    }
    
    public String getThumpSound() {
        return this.thumpSound;
    }
    
    public void setThumpSound(final String thumpSound) {
        this.thumpSound = thumpSound;
    }
    
    @Override
    public IsoObject getRenderEffectMaster() {
        final int doubleDoorIndex = IsoDoor.getDoubleDoorIndex(this);
        if (doubleDoorIndex != -1) {
            IsoObject isoObject = null;
            if (doubleDoorIndex == 2) {
                isoObject = IsoDoor.getDoubleDoorObject(this, 1);
            }
            else if (doubleDoorIndex == 3) {
                isoObject = IsoDoor.getDoubleDoorObject(this, 4);
            }
            if (isoObject != null) {
                return isoObject;
            }
        }
        else {
            final IsoObject garageDoorFirst = IsoDoor.getGarageDoorFirst(this);
            if (garageDoorFirst != null) {
                return garageDoorFirst;
            }
        }
        return this;
    }
    
    public IsoDirections getSpriteEdge(final boolean b) {
        if (!this.isDoor() && !this.isWindow()) {
            return null;
        }
        if (!this.open || b) {
            return this.north ? IsoDirections.N : IsoDirections.W;
        }
        final PropertyContainer properties = this.getProperties();
        if (properties != null && properties.Is(IsoFlagType.attachedE)) {
            return IsoDirections.E;
        }
        if (properties != null && properties.Is(IsoFlagType.attachedS)) {
            return IsoDirections.S;
        }
        return this.north ? IsoDirections.W : IsoDirections.N;
    }
    
    private String getSoundPrefix() {
        if (this.closedSprite == null) {
            return "WoodDoor";
        }
        final PropertyContainer properties = this.closedSprite.getProperties();
        if (properties.Is("DoorSound")) {
            return properties.Val("DoorSound");
        }
        return "WoodDoor";
    }
    
    private void playDoorSound(final BaseCharacterSoundEmitter baseCharacterSoundEmitter, final String s) {
        baseCharacterSoundEmitter.playSound(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getSoundPrefix(), s), (IsoObject)this);
    }
    
    static {
        tempo = new Vector2();
    }
}

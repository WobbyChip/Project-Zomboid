// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.objects;

import zombie.inventory.InventoryItem;
import zombie.network.ServerOptions;
import zombie.iso.areas.SafeHouse;
import zombie.vehicles.BaseVehicle;
import zombie.iso.Vector2;
import zombie.core.properties.PropertyContainer;
import zombie.core.Core;
import java.util.Iterator;
import zombie.network.PacketTypes;
import zombie.core.network.ByteBufferWriter;
import zombie.Lua.LuaEventManager;
import zombie.vehicles.PolygonalMap2;
import zombie.core.raknet.UdpConnection;
import zombie.iso.IsoDirections;
import java.io.IOException;
import zombie.SystemDisabler;
import java.nio.ByteBuffer;
import zombie.core.math.PZMath;
import zombie.ai.states.ThumpState;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.SandboxOptions;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.RoomDef;
import zombie.AmbientStreamManager;
import zombie.SoundManager;
import zombie.WorldSoundManager;
import zombie.network.GameServer;
import zombie.iso.IsoMovingObject;
import zombie.audio.parameters.ParameterMeleeHitSurface;
import zombie.characters.IsoLivingCharacter;
import zombie.network.GameClient;
import zombie.util.Type;
import zombie.characters.IsoPlayer;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoCell;
import zombie.characters.IsoGameCharacter;
import zombie.iso.IsoWorld;
import zombie.iso.IsoGridSquare;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.objects.interfaces.Thumpable;
import zombie.iso.objects.interfaces.BarricadeAble;
import zombie.iso.IsoObject;

public class IsoWindow extends IsoObject implements BarricadeAble, Thumpable
{
    public int Health;
    public int MaxHealth;
    public WindowType type;
    IsoSprite closedSprite;
    IsoSprite smashedSprite;
    public boolean north;
    public boolean Locked;
    public boolean PermaLocked;
    public boolean open;
    IsoSprite openSprite;
    private boolean destroyed;
    private boolean glassRemoved;
    private IsoSprite glassRemovedSprite;
    public int OldNumPlanks;
    
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
    
    public IsoGridSquare getIndoorSquare() {
        if (this.square.getRoom() != null) {
            return this.square;
        }
        if (this.north) {
            final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(this.square.getX(), this.square.getY() - 1, this.square.getZ());
            if (gridSquare != null && gridSquare.getRoom() != null) {
                return gridSquare;
            }
        }
        else {
            final IsoGridSquare gridSquare2 = IsoWorld.instance.CurrentCell.getGridSquare(this.square.getX() - 1, this.square.getY(), this.square.getZ());
            if (gridSquare2 != null && gridSquare2.getRoom() != null) {
                return gridSquare2;
            }
        }
        return null;
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
    
    @Override
    public void AttackObject(final IsoGameCharacter isoGameCharacter) {
        super.AttackObject(isoGameCharacter);
        final IsoObject wall = this.square.getWall(this.north);
        if (wall != null) {
            wall.AttackObject(isoGameCharacter);
        }
    }
    
    public IsoGridSquare getInsideSquare() {
        if (this.square == null) {
            return null;
        }
        if (this.north) {
            return this.getCell().getGridSquare(this.square.getX(), this.square.getY() - 1, this.square.getZ());
        }
        return this.getCell().getGridSquare(this.square.getX() - 1, this.square.getY(), this.square.getZ());
    }
    
    @Override
    public IsoGridSquare getOppositeSquare() {
        return this.getInsideSquare();
    }
    
    public IsoWindow(final IsoCell isoCell) {
        super(isoCell);
        this.Health = 75;
        this.MaxHealth = 75;
        this.type = WindowType.SinglePane;
        this.north = false;
        this.Locked = false;
        this.PermaLocked = false;
        this.open = false;
        this.destroyed = false;
        this.glassRemoved = false;
    }
    
    @Override
    public String getObjectName() {
        return "Window";
    }
    
    @Override
    public void WeaponHit(final IsoGameCharacter isoGameCharacter, final HandWeapon handWeapon) {
        final IsoPlayer isoPlayer = Type.tryCastTo(isoGameCharacter, IsoPlayer.class);
        if (GameClient.bClient) {
            if (isoPlayer != null) {
                GameClient.instance.sendWeaponHit(isoPlayer, handWeapon, this);
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
        if (handWeapon == ((IsoLivingCharacter)isoGameCharacter).bareHands) {
            if (isoPlayer != null) {
                isoPlayer.setMeleeHitSurface(ParameterMeleeHitSurface.Material.Glass);
                isoPlayer.getEmitter().playSound(handWeapon.getDoorHitSound(), this);
            }
            return;
        }
        if (handWeapon != null) {
            this.Damage((float)(handWeapon.getDoorDamage() * 5), isoGameCharacter);
        }
        else {
            this.Damage(100.0f, isoGameCharacter);
        }
        this.DirtySlice();
        if (handWeapon != null && handWeapon.getDoorHitSound() != null) {
            if (isoPlayer != null) {
                isoPlayer.setMeleeHitSurface(ParameterMeleeHitSurface.Material.Glass);
            }
            isoGameCharacter.getEmitter().playSound(handWeapon.getDoorHitSound(), this);
            if (GameServer.bServer) {
                GameServer.PlayWorldSoundServer(handWeapon.getDoorHitSound(), false, this.getSquare(), 1.0f, 20.0f, 2.0f, false);
            }
        }
        WorldSoundManager.instance.addSound(isoGameCharacter, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, false, 0.0f, 15.0f);
        if (!this.isDestroyed() && this.Health <= 0) {
            this.smashWindow();
            this.addBrokenGlass(isoGameCharacter);
        }
    }
    
    public void smashWindow(final boolean b, final boolean b2) {
        if (this.destroyed) {
            return;
        }
        if (GameClient.bClient && !b) {
            GameClient.instance.smashWindow(this, 1);
        }
        if (!b) {
            if (GameServer.bServer) {
                GameServer.PlayWorldSoundServer("SmashWindow", false, this.square, 0.2f, 20.0f, 1.1f, true);
            }
            else {
                SoundManager.instance.PlayWorldSound("SmashWindow", this.square, 0.2f, 20.0f, 1.0f, true);
            }
            WorldSoundManager.instance.addSound(null, this.square.getX(), this.square.getY(), this.square.getZ(), 10, 20, true, 4.0f, 15.0f);
        }
        this.destroyed = true;
        this.sprite = this.smashedSprite;
        if (b2) {
            this.handleAlarm();
        }
        if (GameServer.bServer && !b) {
            GameServer.smashWindow(this, 1);
        }
        this.square.InvalidateSpecialObjectPaths();
    }
    
    public void smashWindow(final boolean b) {
        this.smashWindow(b, true);
    }
    
    public void smashWindow() {
        this.smashWindow(false, true);
    }
    
    public void addBrokenGlass(final IsoMovingObject isoMovingObject) {
        if (isoMovingObject == null) {
            return;
        }
        if (this.getSquare() == null) {
            return;
        }
        if (this.getNorth()) {
            this.addBrokenGlass(isoMovingObject.getY() >= this.getSquare().getY());
        }
        else {
            this.addBrokenGlass(isoMovingObject.getX() >= this.getSquare().getX());
        }
    }
    
    public void addBrokenGlass(final boolean b) {
        final IsoGridSquare isoGridSquare = b ? this.getOppositeSquare() : this.getSquare();
        if (isoGridSquare != null) {
            isoGridSquare.addBrokenGlass();
        }
    }
    
    private void handleAlarm() {
        if (GameClient.bClient) {
            return;
        }
        final IsoGridSquare indoorSquare = this.getIndoorSquare();
        if (indoorSquare == null) {
            return;
        }
        final RoomDef def = indoorSquare.getRoom().def;
        if (def.building.bAlarmed && !GameClient.bClient) {
            AmbientStreamManager.instance.doAlarm(def);
        }
    }
    
    public IsoWindow(final IsoCell isoCell, final IsoGridSquare square, final IsoSprite closedSprite, final boolean north) {
        this.Health = 75;
        this.MaxHealth = 75;
        this.type = WindowType.SinglePane;
        this.north = false;
        this.Locked = false;
        this.PermaLocked = false;
        this.open = false;
        this.destroyed = false;
        this.glassRemoved = false;
        closedSprite.getProperties().UnSet(IsoFlagType.cutN);
        closedSprite.getProperties().UnSet(IsoFlagType.cutW);
        int int1 = 0;
        if (closedSprite.getProperties().Is("OpenTileOffset")) {
            int1 = Integer.parseInt(closedSprite.getProperties().Val("OpenTileOffset"));
        }
        int int2 = 0;
        this.PermaLocked = closedSprite.getProperties().Is("WindowLocked");
        if (closedSprite.getProperties().Is("SmashedTileOffset")) {
            int2 = Integer.parseInt(closedSprite.getProperties().Val("SmashedTileOffset"));
        }
        this.closedSprite = closedSprite;
        if (north) {
            this.closedSprite.getProperties().Set(IsoFlagType.cutN);
            this.closedSprite.getProperties().Set(IsoFlagType.windowN);
        }
        else {
            this.closedSprite.getProperties().Set(IsoFlagType.cutW);
            this.closedSprite.getProperties().Set(IsoFlagType.windowW);
        }
        this.openSprite = IsoSprite.getSprite(IsoSpriteManager.instance, closedSprite, int1);
        this.smashedSprite = IsoSprite.getSprite(IsoSpriteManager.instance, closedSprite, int2);
        if (this.closedSprite.getProperties().Is("GlassRemovedOffset")) {
            this.glassRemovedSprite = IsoSprite.getSprite(IsoSpriteManager.instance, this.closedSprite, Integer.parseInt(this.closedSprite.getProperties().Val("GlassRemovedOffset")));
        }
        else {
            this.glassRemovedSprite = this.smashedSprite;
        }
        if (this.smashedSprite != this.closedSprite && this.smashedSprite != null) {
            this.smashedSprite.AddProperties(this.closedSprite);
            this.smashedSprite.setType(this.closedSprite.getType());
        }
        if (this.openSprite != this.closedSprite && this.openSprite != null) {
            this.openSprite.AddProperties(this.closedSprite);
            this.openSprite.setType(this.closedSprite.getType());
        }
        if (this.glassRemovedSprite != this.closedSprite && this.glassRemovedSprite != null) {
            this.glassRemovedSprite.AddProperties(this.closedSprite);
            this.glassRemovedSprite.setType(this.closedSprite.getType());
        }
        this.sprite = this.closedSprite;
        final IsoObject wall = square.getWall(north);
        if (wall != null) {
            wall.rerouteCollide = this;
        }
        this.square = square;
        this.north = north;
        switch (this.type) {
            case SinglePane: {
                final int n = 50;
                this.Health = n;
                this.MaxHealth = n;
                break;
            }
            case DoublePane: {
                final int n2 = 150;
                this.Health = n2;
                this.MaxHealth = n2;
                break;
            }
        }
        int n3 = 69;
        if (SandboxOptions.instance.LockedHouses.getValue() == 1) {
            n3 = -1;
        }
        else if (SandboxOptions.instance.LockedHouses.getValue() == 2) {
            n3 = 5;
        }
        else if (SandboxOptions.instance.LockedHouses.getValue() == 3) {
            n3 = 10;
        }
        else if (SandboxOptions.instance.LockedHouses.getValue() == 4) {
            n3 = 50;
        }
        else if (SandboxOptions.instance.LockedHouses.getValue() == 5) {
            n3 = 60;
        }
        else if (SandboxOptions.instance.LockedHouses.getValue() == 6) {
            n3 = 70;
        }
        if (n3 > -1) {
            this.Locked = (Rand.Next(100) < n3);
        }
    }
    
    @Override
    public boolean isDestroyed() {
        return this.destroyed;
    }
    
    public boolean IsOpen() {
        return this.open;
    }
    
    @Override
    public boolean onMouseLeftClick(final int n, final int n2) {
        return false;
    }
    
    @Override
    public boolean TestCollide(final IsoMovingObject isoMovingObject, final IsoGridSquare isoGridSquare, final IsoGridSquare isoGridSquare2) {
        if (isoGridSquare == this.square) {
            if (this.north && isoGridSquare2.getY() < isoGridSquare.getY()) {
                if (isoMovingObject != null) {
                    isoMovingObject.collideWith(this);
                }
                return true;
            }
            if (!this.north && isoGridSquare2.getX() < isoGridSquare.getX()) {
                if (isoMovingObject != null) {
                    isoMovingObject.collideWith(this);
                }
                return true;
            }
        }
        else {
            if (this.north && isoGridSquare2.getY() > isoGridSquare.getY()) {
                if (isoMovingObject != null) {
                    isoMovingObject.collideWith(this);
                }
                return true;
            }
            if (!this.north && isoGridSquare2.getX() > isoGridSquare.getX()) {
                if (isoMovingObject != null) {
                    isoMovingObject.collideWith(this);
                }
                return true;
            }
        }
        return false;
    }
    
    @Override
    public VisionResult TestVision(final IsoGridSquare isoGridSquare, final IsoGridSquare isoGridSquare2) {
        if (isoGridSquare2.getZ() != isoGridSquare.getZ()) {
            return VisionResult.NoEffect;
        }
        if (isoGridSquare == this.square) {
            if (this.north && isoGridSquare2.getY() < isoGridSquare.getY()) {
                return VisionResult.Unblocked;
            }
            if (!this.north && isoGridSquare2.getX() < isoGridSquare.getX()) {
                return VisionResult.Unblocked;
            }
        }
        else {
            if (this.north && isoGridSquare2.getY() > isoGridSquare.getY()) {
                return VisionResult.Unblocked;
            }
            if (!this.north && isoGridSquare2.getX() > isoGridSquare.getX()) {
                return VisionResult.Unblocked;
            }
        }
        return VisionResult.NoEffect;
    }
    
    @Override
    public void Thump(final IsoMovingObject isoMovingObject) {
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
        if (isoMovingObject instanceof IsoZombie) {
            if (((IsoZombie)isoMovingObject).cognition == 1 && !this.canClimbThrough((IsoGameCharacter)isoMovingObject) && !this.isInvincible() && (!this.Locked || (isoMovingObject.getCurrentSquare() != null && !isoMovingObject.getCurrentSquare().Is(IsoFlagType.exterior)))) {
                this.ToggleWindow((IsoGameCharacter)isoMovingObject);
                if (this.canClimbThrough((IsoGameCharacter)isoMovingObject)) {
                    return;
                }
            }
            final int fastForwardDamageMultiplier = ThumpState.getFastForwardDamageMultiplier();
            this.DirtySlice();
            this.Damage((float)(((IsoZombie)isoMovingObject).strength * fastForwardDamageMultiplier), isoMovingObject);
            WorldSoundManager.instance.addSound(isoMovingObject, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, true, 4.0f, 15.0f);
        }
        if (!this.isDestroyed() && this.Health <= 0) {
            if (this.getSquare().getBuilding() != null) {
                this.getSquare().getBuilding().forceAwake();
            }
            if (GameServer.bServer) {
                GameServer.smashWindow(this, 1);
                GameServer.PlayWorldSoundServer("SmashWindow", false, isoMovingObject.getCurrentSquare(), 0.2f, 20.0f, 1.1f, true);
            }
            ((IsoGameCharacter)isoMovingObject).getEmitter().playSound("SmashWindow", this);
            WorldSoundManager.instance.addSound(null, this.square.getX(), this.square.getY(), this.square.getZ(), 10, 20, true, 4.0f, 15.0f);
            isoMovingObject.setThumpTarget(null);
            this.destroyed = true;
            this.sprite = this.smashedSprite;
            this.square.InvalidateSpecialObjectPaths();
            this.addBrokenGlass(isoMovingObject);
            if (isoMovingObject instanceof IsoZombie && this.getThumpableFor((IsoGameCharacter)isoMovingObject) != null) {
                isoMovingObject.setThumpTarget(this.getThumpableFor((IsoGameCharacter)isoMovingObject));
            }
        }
    }
    
    @Override
    public Thumpable getThumpableFor(final IsoGameCharacter isoGameCharacter) {
        final IsoBarricade barricadeForCharacter = this.getBarricadeForCharacter(isoGameCharacter);
        if (barricadeForCharacter != null) {
            return barricadeForCharacter;
        }
        if (!this.isDestroyed() && !this.IsOpen()) {
            return this;
        }
        final IsoBarricade barricadeOppositeCharacter = this.getBarricadeOppositeCharacter(isoGameCharacter);
        if (barricadeOppositeCharacter != null) {
            return barricadeOppositeCharacter;
        }
        return null;
    }
    
    @Override
    public float getThumpCondition() {
        return PZMath.clamp(this.Health, 0, this.MaxHealth) / (float)this.MaxHealth;
    }
    
    @Override
    public void load(final ByteBuffer byteBuffer, final int n, final boolean b) throws IOException {
        super.load(byteBuffer, n, b);
        this.open = (byteBuffer.get() == 1);
        this.north = (byteBuffer.get() == 1);
        if (n >= 87) {
            this.Health = byteBuffer.getInt();
        }
        else {
            final int int1 = byteBuffer.getInt();
            this.Health = byteBuffer.getInt();
            final int int2 = byteBuffer.getInt();
            if (n >= 49) {
                byteBuffer.getShort();
            }
            else {
                Math.max(int2, int1 * 1000);
            }
            this.OldNumPlanks = int1;
        }
        this.Locked = (byteBuffer.get() == 1);
        this.PermaLocked = (byteBuffer.get() == 1);
        this.destroyed = (byteBuffer.get() == 1);
        if (n >= 64) {
            this.glassRemoved = (byteBuffer.get() == 1);
            if (byteBuffer.get() == 1) {
                this.openSprite = IsoSprite.getSprite(IsoSpriteManager.instance, byteBuffer.getInt());
            }
            if (byteBuffer.get() == 1) {
                this.closedSprite = IsoSprite.getSprite(IsoSpriteManager.instance, byteBuffer.getInt());
            }
            if (byteBuffer.get() == 1) {
                this.smashedSprite = IsoSprite.getSprite(IsoSpriteManager.instance, byteBuffer.getInt());
            }
            if (byteBuffer.get() == 1) {
                this.glassRemovedSprite = IsoSprite.getSprite(IsoSpriteManager.instance, byteBuffer.getInt());
            }
        }
        else {
            if (byteBuffer.getInt() == 1) {
                this.openSprite = IsoSprite.getSprite(IsoSpriteManager.instance, byteBuffer.getInt());
            }
            if (byteBuffer.getInt() == 1) {
                this.closedSprite = IsoSprite.getSprite(IsoSpriteManager.instance, byteBuffer.getInt());
            }
            if (byteBuffer.getInt() == 1) {
                this.smashedSprite = IsoSprite.getSprite(IsoSpriteManager.instance, byteBuffer.getInt());
            }
            if (this.closedSprite != null) {
                if (this.destroyed && this.closedSprite.getProperties().Is("SmashedTileOffset")) {
                    this.closedSprite = IsoSprite.getSprite(IsoSpriteManager.instance, this.closedSprite, -Integer.parseInt(this.closedSprite.getProperties().Val("SmashedTileOffset")));
                }
                if (this.closedSprite.getProperties().Is("GlassRemovedOffset")) {
                    this.glassRemovedSprite = IsoSprite.getSprite(IsoSpriteManager.instance, this.closedSprite, Integer.parseInt(this.closedSprite.getProperties().Val("GlassRemovedOffset")));
                }
            }
            if (this.glassRemovedSprite == null) {
                this.glassRemovedSprite = ((this.smashedSprite != null) ? this.smashedSprite : this.closedSprite);
            }
        }
        this.MaxHealth = byteBuffer.getInt();
        if (this.closedSprite != null) {
            if (this.north) {
                this.closedSprite.getProperties().Set(IsoFlagType.cutN);
                this.closedSprite.getProperties().Set(IsoFlagType.windowN);
            }
            else {
                this.closedSprite.getProperties().Set(IsoFlagType.cutW);
                this.closedSprite.getProperties().Set(IsoFlagType.windowW);
            }
            if (this.smashedSprite != this.closedSprite && this.smashedSprite != null) {
                this.smashedSprite.AddProperties(this.closedSprite);
                this.smashedSprite.setType(this.closedSprite.getType());
            }
            if (this.openSprite != this.closedSprite && this.openSprite != null) {
                this.openSprite.AddProperties(this.closedSprite);
                this.openSprite.setType(this.closedSprite.getType());
            }
            if (this.glassRemovedSprite != this.closedSprite && this.glassRemovedSprite != null) {
                this.glassRemovedSprite.AddProperties(this.closedSprite);
                this.glassRemovedSprite.setType(this.closedSprite.getType());
            }
        }
        if (SystemDisabler.doObjectStateSyncEnable && GameClient.bClient) {
            GameClient.instance.objectSyncReq.putRequestLoad(this.square);
        }
    }
    
    @Override
    public void addToWorld() {
        super.addToWorld();
        this.getCell().addToWindowList(this);
    }
    
    @Override
    public void removeFromWorld() {
        super.removeFromWorld();
        this.getCell().removeFromWindowList(this);
    }
    
    @Override
    public void save(final ByteBuffer byteBuffer, final boolean b) throws IOException {
        super.save(byteBuffer, b);
        byteBuffer.put((byte)(this.open ? 1 : 0));
        byteBuffer.put((byte)(this.north ? 1 : 0));
        byteBuffer.putInt(this.Health);
        byteBuffer.put((byte)(this.Locked ? 1 : 0));
        byteBuffer.put((byte)(this.PermaLocked ? 1 : 0));
        byteBuffer.put((byte)(this.destroyed ? 1 : 0));
        byteBuffer.put((byte)(this.glassRemoved ? 1 : 0));
        if (this.openSprite != null) {
            byteBuffer.put((byte)1);
            byteBuffer.putInt(this.openSprite.ID);
        }
        else {
            byteBuffer.put((byte)0);
        }
        if (this.closedSprite != null) {
            byteBuffer.put((byte)1);
            byteBuffer.putInt(this.closedSprite.ID);
        }
        else {
            byteBuffer.put((byte)0);
        }
        if (this.smashedSprite != null) {
            byteBuffer.put((byte)1);
            byteBuffer.putInt(this.smashedSprite.ID);
        }
        else {
            byteBuffer.put((byte)0);
        }
        if (this.glassRemovedSprite != null) {
            byteBuffer.put((byte)1);
            byteBuffer.putInt(this.glassRemovedSprite.ID);
        }
        else {
            byteBuffer.put((byte)0);
        }
        byteBuffer.putInt(this.MaxHealth);
    }
    
    @Override
    public void saveState(final ByteBuffer byteBuffer) throws IOException {
        byteBuffer.put((byte)(this.Locked ? 1 : 0));
    }
    
    @Override
    public void loadState(final ByteBuffer byteBuffer) throws IOException {
        final boolean locked = byteBuffer.get() == 1;
        if (locked != this.Locked) {
            this.Locked = locked;
        }
    }
    
    public void openCloseCurtain(final IsoGameCharacter isoGameCharacter) {
        if (isoGameCharacter == IsoPlayer.getInstance()) {
            final IsoGridSquare isoGridSquare = null;
            final IsoDirections n = IsoDirections.N;
            IsoGridSquare isoGridSquare3;
            IsoGridSquare isoGridSquare4;
            if (this.north) {
                IsoGridSquare isoGridSquare2 = this.square;
                final IsoDirections n2 = IsoDirections.N;
                if (isoGridSquare2.getRoom() == null) {
                    isoGridSquare2 = this.getCell().getGridSquare(isoGridSquare2.getX(), isoGridSquare2.getY() - 1, isoGridSquare2.getZ());
                    final IsoDirections s = IsoDirections.S;
                }
                isoGridSquare3 = isoGridSquare;
                isoGridSquare4 = isoGridSquare2;
            }
            else {
                IsoGridSquare isoGridSquare5 = this.square;
                final IsoDirections w = IsoDirections.W;
                if (isoGridSquare5.getRoom() == null) {
                    isoGridSquare5 = this.getCell().getGridSquare(isoGridSquare5.getX() - 1, isoGridSquare5.getY(), isoGridSquare5.getZ());
                    final IsoDirections e = IsoDirections.E;
                }
                isoGridSquare3 = isoGridSquare;
                isoGridSquare4 = isoGridSquare5;
            }
            if (isoGridSquare4 != null) {
                for (int i = 0; i < isoGridSquare4.getSpecialObjects().size(); ++i) {
                    if (isoGridSquare4.getSpecialObjects().get(i) instanceof IsoCurtain) {
                        ((IsoCurtain)isoGridSquare4.getSpecialObjects().get(i)).ToggleDoorSilent();
                        return;
                    }
                }
            }
            if (isoGridSquare3 != null) {
                for (int j = 0; j < isoGridSquare3.getSpecialObjects().size(); ++j) {
                    if (isoGridSquare3.getSpecialObjects().get(j) instanceof IsoCurtain) {
                        ((IsoCurtain)isoGridSquare3.getSpecialObjects().get(j)).ToggleDoorSilent();
                        return;
                    }
                }
            }
        }
    }
    
    public void removeSheet(final IsoGameCharacter isoGameCharacter) {
        final IsoDirections n = IsoDirections.N;
        IsoGridSquare isoGridSquare2;
        if (this.north) {
            IsoGridSquare isoGridSquare = this.square;
            final IsoDirections n2 = IsoDirections.N;
            if (isoGridSquare.getRoom() == null) {
                isoGridSquare = this.getCell().getGridSquare(isoGridSquare.getX(), isoGridSquare.getY() - 1, isoGridSquare.getZ());
                final IsoDirections s = IsoDirections.S;
            }
            isoGridSquare2 = isoGridSquare;
        }
        else {
            IsoGridSquare isoGridSquare3 = this.square;
            final IsoDirections w = IsoDirections.W;
            if (isoGridSquare3.getRoom() == null) {
                isoGridSquare3 = this.getCell().getGridSquare(isoGridSquare3.getX() - 1, isoGridSquare3.getY(), isoGridSquare3.getZ());
                final IsoDirections e = IsoDirections.E;
            }
            isoGridSquare2 = isoGridSquare3;
        }
        int i = 0;
        while (i < isoGridSquare2.getSpecialObjects().size()) {
            final IsoObject isoObject = isoGridSquare2.getSpecialObjects().get(i);
            if (isoObject instanceof IsoCurtain) {
                isoGridSquare2.transmitRemoveItemFromSquare(isoObject);
                if (isoGameCharacter == null) {
                    break;
                }
                if (GameServer.bServer) {
                    isoGameCharacter.sendObjectChange("addItemOfType", new Object[] { "type", isoObject.getName() });
                    break;
                }
                isoGameCharacter.getInventory().AddItem(isoObject.getName());
                break;
            }
            else {
                ++i;
            }
        }
    }
    
    public void addSheet(final IsoGameCharacter isoGameCharacter) {
        IsoObjectType isoObjectType;
        IsoGridSquare isoGridSquare2;
        if (this.north) {
            IsoGridSquare isoGridSquare = this.square;
            isoObjectType = IsoObjectType.curtainN;
            if (isoGameCharacter != null) {
                if (isoGameCharacter.getY() < this.getY()) {
                    isoGridSquare = this.getCell().getGridSquare(isoGridSquare.getX(), isoGridSquare.getY() - 1, isoGridSquare.getZ());
                    isoObjectType = IsoObjectType.curtainS;
                }
            }
            else if (isoGridSquare.getRoom() == null) {
                isoGridSquare = this.getCell().getGridSquare(isoGridSquare.getX(), isoGridSquare.getY() - 1, isoGridSquare.getZ());
                isoObjectType = IsoObjectType.curtainS;
            }
            isoGridSquare2 = isoGridSquare;
        }
        else {
            IsoGridSquare isoGridSquare3 = this.square;
            isoObjectType = IsoObjectType.curtainW;
            if (isoGameCharacter != null) {
                if (isoGameCharacter.getX() < this.getX()) {
                    isoGridSquare3 = this.getCell().getGridSquare(isoGridSquare3.getX() - 1, isoGridSquare3.getY(), isoGridSquare3.getZ());
                    isoObjectType = IsoObjectType.curtainE;
                }
            }
            else if (isoGridSquare3.getRoom() == null) {
                isoGridSquare3 = this.getCell().getGridSquare(isoGridSquare3.getX() - 1, isoGridSquare3.getY(), isoGridSquare3.getZ());
                isoObjectType = IsoObjectType.curtainE;
            }
            isoGridSquare2 = isoGridSquare3;
        }
        if (isoGridSquare2.getCurtain(isoObjectType) != null) {
            return;
        }
        if (isoGridSquare2 != null) {
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
            final IsoCurtain isoCurtain = new IsoCurtain(this.getCell(), isoGridSquare2, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n), this.north);
            isoGridSquare2.AddSpecialTileObject(isoCurtain);
            if (!isoCurtain.open) {
                isoCurtain.ToggleDoorSilent();
            }
            if (GameServer.bServer) {
                isoCurtain.transmitCompleteItemToClients();
                if (isoGameCharacter != null) {
                    isoGameCharacter.sendObjectChange("removeOneOf", new Object[] { "type", "Sheet" });
                }
            }
            else if (isoGameCharacter != null) {
                isoGameCharacter.getInventory().RemoveOneOf("Sheet");
            }
        }
    }
    
    public void ToggleWindow(final IsoGameCharacter isoGameCharacter) {
        this.DirtySlice();
        IsoGridSquare.setRecalcLightTime(-1);
        if (this.PermaLocked) {
            return;
        }
        if (this.destroyed) {
            return;
        }
        if (isoGameCharacter != null && this.getBarricadeForCharacter(isoGameCharacter) != null) {
            return;
        }
        this.Locked = false;
        this.open = !this.open;
        this.sprite = this.closedSprite;
        this.square.InvalidateSpecialObjectPaths();
        if (this.open) {
            if (!(isoGameCharacter instanceof IsoZombie) || SandboxOptions.getInstance().Lore.TriggerHouseAlarm.getValue()) {
                this.handleAlarm();
            }
            this.sprite = this.openSprite;
        }
        this.square.RecalcProperties();
        this.syncIsoObject(false, (byte)(this.open ? 1 : 0), null, null);
        PolygonalMap2.instance.squareChanged(this.square);
        LuaEventManager.triggerEvent("OnContainerUpdate");
    }
    
    @Override
    public void syncIsoObjectSend(final ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putInt(this.square.getX());
        byteBufferWriter.putInt(this.square.getY());
        byteBufferWriter.putInt(this.square.getZ());
        byteBufferWriter.putByte((byte)this.square.getObjects().indexOf(this));
        byteBufferWriter.putByte((byte)1);
        byteBufferWriter.putByte((byte)(this.open ? 1 : 0));
        byteBufferWriter.putByte((byte)(this.destroyed ? 1 : 0));
        byteBufferWriter.putByte((byte)(this.Locked ? 1 : 0));
        byteBufferWriter.putByte((byte)(this.PermaLocked ? 1 : 0));
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
        if (GameClient.bClient && !b) {
            final ByteBufferWriter startPacket = GameClient.connection.startPacket();
            PacketTypes.PacketType.SyncIsoObject.doPacket(startPacket);
            this.syncIsoObjectSend(startPacket);
            PacketTypes.PacketType.SyncIsoObject.send(GameClient.connection);
        }
        else if (GameServer.bServer && !b) {
            for (final UdpConnection udpConnection2 : GameServer.udpEngine.connections) {
                final ByteBufferWriter startPacket2 = udpConnection2.startPacket();
                PacketTypes.PacketType.SyncIsoObject.doPacket(startPacket2);
                this.syncIsoObjectSend(startPacket2);
                PacketTypes.PacketType.SyncIsoObject.send(udpConnection2);
            }
        }
        else if (b) {
            final boolean b3 = byteBuffer.get() == 1;
            final boolean locked = byteBuffer.get() == 1;
            final boolean permaLocked = byteBuffer.get() == 1;
            if (b2 == 1) {
                this.open = true;
                this.sprite = this.openSprite;
            }
            else if (b2 == 0) {
                this.open = false;
                this.sprite = this.closedSprite;
            }
            if (b3) {
                this.destroyed = true;
                this.sprite = this.smashedSprite;
            }
            this.Locked = locked;
            this.PermaLocked = permaLocked;
            if (GameServer.bServer) {
                for (final UdpConnection udpConnection3 : GameServer.udpEngine.connections) {
                    if (udpConnection != null && udpConnection3.getConnectedGUID() != udpConnection.getConnectedGUID()) {
                        final ByteBufferWriter startPacket3 = udpConnection3.startPacket();
                        PacketTypes.PacketType.SyncIsoObject.doPacket(startPacket3);
                        this.syncIsoObjectSend(startPacket3);
                        PacketTypes.PacketType.SyncIsoObject.send(udpConnection3);
                    }
                }
            }
            this.square.RecalcProperties();
            LuaEventManager.triggerEvent("OnContainerUpdate");
        }
    }
    
    public static boolean isTopOfSheetRopeHere(final IsoGridSquare isoGridSquare) {
        return isoGridSquare != null && (isoGridSquare.Is(IsoFlagType.climbSheetTopN) || isoGridSquare.Is(IsoFlagType.climbSheetTopS) || isoGridSquare.Is(IsoFlagType.climbSheetTopW) || isoGridSquare.Is(IsoFlagType.climbSheetTopE));
    }
    
    public static boolean isTopOfSheetRopeHere(final IsoGridSquare isoGridSquare, final boolean b) {
        if (isoGridSquare == null) {
            return false;
        }
        if (b) {
            if (isoGridSquare.Is(IsoFlagType.climbSheetTopN)) {
                return true;
            }
            if (isoGridSquare.nav[IsoDirections.N.index()] != null && isoGridSquare.nav[IsoDirections.N.index()].Is(IsoFlagType.climbSheetTopS)) {
                return true;
            }
        }
        else {
            if (isoGridSquare.Is(IsoFlagType.climbSheetTopW)) {
                return true;
            }
            if (isoGridSquare.nav[IsoDirections.W.index()] != null && isoGridSquare.nav[IsoDirections.W.index()].Is(IsoFlagType.climbSheetTopE)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean haveSheetRope() {
        return isTopOfSheetRopeHere(this.square, this.north);
    }
    
    public static boolean isSheetRopeHere(final IsoGridSquare isoGridSquare) {
        return isoGridSquare != null && (isoGridSquare.Is(IsoFlagType.climbSheetTopW) || isoGridSquare.Is(IsoFlagType.climbSheetTopN) || isoGridSquare.Is(IsoFlagType.climbSheetTopE) || isoGridSquare.Is(IsoFlagType.climbSheetTopS) || isoGridSquare.Is(IsoFlagType.climbSheetW) || isoGridSquare.Is(IsoFlagType.climbSheetN) || isoGridSquare.Is(IsoFlagType.climbSheetE) || isoGridSquare.Is(IsoFlagType.climbSheetS));
    }
    
    public static boolean canClimbHere(final IsoGridSquare isoGridSquare) {
        return isoGridSquare != null && !isoGridSquare.getProperties().Is(IsoFlagType.solid) && !isoGridSquare.Has(IsoObjectType.stairsBN) && !isoGridSquare.Has(IsoObjectType.stairsMN) && !isoGridSquare.Has(IsoObjectType.stairsTN) && !isoGridSquare.Has(IsoObjectType.stairsBW) && !isoGridSquare.Has(IsoObjectType.stairsMW) && !isoGridSquare.Has(IsoObjectType.stairsTW);
    }
    
    public static int countAddSheetRope(IsoGridSquare orCreateGridSquare, final boolean b) {
        if (isTopOfSheetRopeHere(orCreateGridSquare, b)) {
            return 0;
        }
        final IsoCell currentCell = IsoWorld.instance.CurrentCell;
        if (orCreateGridSquare.TreatAsSolidFloor()) {
            if (b) {
                final IsoGridSquare orCreateGridSquare2 = currentCell.getOrCreateGridSquare(orCreateGridSquare.getX(), orCreateGridSquare.getY() - 1, orCreateGridSquare.getZ());
                if (orCreateGridSquare2 == null || orCreateGridSquare2.TreatAsSolidFloor() || isSheetRopeHere(orCreateGridSquare2) || !canClimbHere(orCreateGridSquare2)) {
                    return 0;
                }
                orCreateGridSquare = orCreateGridSquare2;
            }
            else {
                final IsoGridSquare orCreateGridSquare3 = currentCell.getOrCreateGridSquare(orCreateGridSquare.getX() - 1, orCreateGridSquare.getY(), orCreateGridSquare.getZ());
                if (orCreateGridSquare3 == null || orCreateGridSquare3.TreatAsSolidFloor() || isSheetRopeHere(orCreateGridSquare3) || !canClimbHere(orCreateGridSquare3)) {
                    return 0;
                }
                orCreateGridSquare = orCreateGridSquare3;
            }
        }
        for (int n = 1; orCreateGridSquare != null; orCreateGridSquare = currentCell.getOrCreateGridSquare(orCreateGridSquare.getX(), orCreateGridSquare.getY(), orCreateGridSquare.getZ() - 1), ++n) {
            if (!canClimbHere(orCreateGridSquare)) {
                return 0;
            }
            if (orCreateGridSquare.TreatAsSolidFloor()) {
                return n;
            }
            if (orCreateGridSquare.getZ() == 0) {
                return n;
            }
        }
        return 0;
    }
    
    @Override
    public int countAddSheetRope() {
        return countAddSheetRope(this.square, this.north);
    }
    
    public static boolean canAddSheetRope(final IsoGridSquare isoGridSquare, final boolean b) {
        return countAddSheetRope(isoGridSquare, b) != 0;
    }
    
    @Override
    public boolean canAddSheetRope() {
        return this.canClimbThrough(null) && canAddSheetRope(this.square, this.north);
    }
    
    @Override
    public boolean addSheetRope(final IsoPlayer isoPlayer, final String s) {
        return this.canAddSheetRope() && addSheetRope(isoPlayer, this.square, this.north, s);
    }
    
    public static boolean addSheetRope(final IsoPlayer isoPlayer, IsoGridSquare orCreateGridSquare, final boolean b, final String name) {
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        if (b) {
            n3 = 1;
        }
        boolean b2 = false;
        boolean b3 = false;
        IsoGridSquare gridSquare = null;
        IsoGridSquare gridSquare2 = null;
        final IsoCell currentCell = IsoWorld.instance.CurrentCell;
        if (orCreateGridSquare.TreatAsSolidFloor()) {
            if (!b) {
                gridSquare = currentCell.getGridSquare(orCreateGridSquare.getX() - 1, orCreateGridSquare.getY(), orCreateGridSquare.getZ());
                if (gridSquare != null) {
                    b3 = true;
                    n3 = 3;
                }
            }
            else {
                gridSquare2 = currentCell.getGridSquare(orCreateGridSquare.getX(), orCreateGridSquare.getY() - 1, orCreateGridSquare.getZ());
                if (gridSquare2 != null) {
                    b2 = true;
                    n3 = 4;
                }
            }
        }
        if (orCreateGridSquare.getProperties().Is(IsoFlagType.solidfloor)) {}
        while (orCreateGridSquare != null && (GameServer.bServer || isoPlayer.getInventory().contains(name))) {
            String s = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n3);
            if (n2 > 0) {
                if (b3) {
                    s = "crafted_01_10";
                }
                else if (b2) {
                    s = "crafted_01_13";
                }
                else {
                    s = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n3 + 8);
                }
            }
            final IsoObject isoObject = new IsoObject(currentCell, orCreateGridSquare, s);
            isoObject.setName(name);
            isoObject.sheetRope = true;
            orCreateGridSquare.getObjects().add(isoObject);
            isoObject.transmitCompleteItemToClients();
            orCreateGridSquare.haveSheetRope = true;
            if (b2 && n2 == 0) {
                orCreateGridSquare = gridSquare2;
                final IsoObject isoObject2 = new IsoObject(currentCell, orCreateGridSquare, "crafted_01_5");
                isoObject2.setName(name);
                isoObject2.sheetRope = true;
                orCreateGridSquare.getObjects().add(isoObject2);
                isoObject2.transmitCompleteItemToClients();
            }
            if (b3 && n2 == 0) {
                orCreateGridSquare = gridSquare;
                final IsoObject isoObject3 = new IsoObject(currentCell, orCreateGridSquare, "crafted_01_2");
                isoObject3.setName(name);
                isoObject3.sheetRope = true;
                orCreateGridSquare.getObjects().add(isoObject3);
                isoObject3.transmitCompleteItemToClients();
            }
            orCreateGridSquare.RecalcProperties();
            orCreateGridSquare.getProperties().UnSet(IsoFlagType.solidtrans);
            if (GameServer.bServer) {
                if (n2 == 0) {
                    isoPlayer.sendObjectChange("removeOneOf", new Object[] { "type", "Nails" });
                }
                isoPlayer.sendObjectChange("removeOneOf", new Object[] { "type", name });
            }
            else {
                if (n2 == 0) {
                    isoPlayer.getInventory().RemoveOneOf("Nails");
                }
                isoPlayer.getInventory().RemoveOneOf(name);
            }
            ++n2;
            if (n != 0) {
                break;
            }
            orCreateGridSquare = currentCell.getOrCreateGridSquare(orCreateGridSquare.getX(), orCreateGridSquare.getY(), orCreateGridSquare.getZ() - 1);
            if (orCreateGridSquare == null || !orCreateGridSquare.TreatAsSolidFloor()) {
                continue;
            }
            n = 1;
        }
        return true;
    }
    
    @Override
    public boolean removeSheetRope(final IsoPlayer isoPlayer) {
        return this.haveSheetRope() && removeSheetRope(isoPlayer, this.square, this.north);
    }
    
    public static boolean removeSheetRope(final IsoPlayer isoPlayer, final IsoGridSquare isoGridSquare, final boolean b) {
        if (isoGridSquare == null) {
            return false;
        }
        IsoGridSquare gridSquare = isoGridSquare;
        gridSquare.haveSheetRope = false;
        IsoFlagType isoFlagType;
        IsoFlagType isoFlagType2;
        if (b) {
            if (isoGridSquare.Is(IsoFlagType.climbSheetTopN)) {
                isoFlagType = IsoFlagType.climbSheetTopN;
                isoFlagType2 = IsoFlagType.climbSheetN;
            }
            else {
                if (isoGridSquare.nav[IsoDirections.N.index()] == null || !isoGridSquare.nav[IsoDirections.N.index()].Is(IsoFlagType.climbSheetTopS)) {
                    return false;
                }
                isoFlagType = IsoFlagType.climbSheetTopS;
                isoFlagType2 = IsoFlagType.climbSheetS;
                final String anObject = "crafted_01_4";
                for (int i = 0; i < gridSquare.getObjects().size(); ++i) {
                    final IsoObject isoObject = gridSquare.getObjects().get(i);
                    if (isoObject.sprite != null && isoObject.sprite.getName() != null && isoObject.sprite.getName().equals(anObject)) {
                        gridSquare.transmitRemoveItemFromSquare(isoObject);
                        break;
                    }
                }
                gridSquare = isoGridSquare.nav[IsoDirections.N.index()];
            }
        }
        else if (isoGridSquare.Is(IsoFlagType.climbSheetTopW)) {
            isoFlagType = IsoFlagType.climbSheetTopW;
            isoFlagType2 = IsoFlagType.climbSheetW;
        }
        else {
            if (isoGridSquare.nav[IsoDirections.W.index()] == null || !isoGridSquare.nav[IsoDirections.W.index()].Is(IsoFlagType.climbSheetTopE)) {
                return false;
            }
            isoFlagType = IsoFlagType.climbSheetTopE;
            isoFlagType2 = IsoFlagType.climbSheetE;
            final String anObject2 = "crafted_01_3";
            for (int j = 0; j < gridSquare.getObjects().size(); ++j) {
                final IsoObject isoObject2 = gridSquare.getObjects().get(j);
                if (isoObject2.sprite != null && isoObject2.sprite.getName() != null && isoObject2.sprite.getName().equals(anObject2)) {
                    gridSquare.transmitRemoveItemFromSquare(isoObject2);
                    break;
                }
            }
            gridSquare = isoGridSquare.nav[IsoDirections.W.index()];
        }
        while (gridSquare != null) {
            boolean b2 = false;
            for (int k = 0; k < gridSquare.getObjects().size(); ++k) {
                final IsoObject isoObject3 = gridSquare.getObjects().get(k);
                if (isoObject3.getProperties() != null && (isoObject3.getProperties().Is(isoFlagType) || isoObject3.getProperties().Is(isoFlagType2))) {
                    gridSquare.transmitRemoveItemFromSquare(isoObject3);
                    if (GameServer.bServer) {
                        if (isoPlayer != null) {
                            isoPlayer.sendObjectChange("addItemOfType", new Object[] { "type", isoObject3.getName() });
                        }
                    }
                    else if (isoPlayer != null) {
                        isoPlayer.getInventory().AddItem(isoObject3.getName());
                    }
                    b2 = true;
                    break;
                }
            }
            if (!b2) {
                break;
            }
            if (gridSquare.getZ() == 0) {
                break;
            }
            gridSquare = gridSquare.getCell().getGridSquare(gridSquare.getX(), gridSquare.getY(), gridSquare.getZ() - 1);
        }
        return true;
    }
    
    @Override
    public void Damage(final float n) {
        this.Damage(n, false);
    }
    
    public void Damage(final float n, final boolean b) {
        if (this.isInvincible() || "Tutorial".equals(Core.GameMode)) {
            return;
        }
        this.DirtySlice();
        this.Health -= (int)n;
        if (this.Health < 0) {
            this.Health = 0;
        }
        if (!this.isDestroyed() && this.Health == 0) {
            this.smashWindow(false, !b || SandboxOptions.getInstance().Lore.TriggerHouseAlarm.getValue());
            if (this.getSquare().getBuilding() != null) {
                this.getSquare().getBuilding().forceAwake();
            }
        }
    }
    
    public void Damage(final float n, final IsoMovingObject isoMovingObject) {
        if (this.isInvincible() || "Tutorial".equals(Core.GameMode)) {
            return;
        }
        this.Health -= (int)n;
        if (this.Health < 0) {
            this.Health = 0;
        }
        if (!this.isDestroyed() && this.Health == 0) {
            this.smashWindow(false, !(isoMovingObject instanceof IsoZombie) || SandboxOptions.getInstance().Lore.TriggerHouseAlarm.getValue());
            this.addBrokenGlass(isoMovingObject);
        }
    }
    
    public boolean isLocked() {
        return this.Locked;
    }
    
    public boolean isSmashed() {
        return this.destroyed;
    }
    
    public boolean isInvincible() {
        if (this.square == null || !this.square.Is(IsoFlagType.makeWindowInvincible)) {
            return false;
        }
        final int objectIndex = this.getObjectIndex();
        if (objectIndex != -1) {
            final IsoObject[] array = this.square.getObjects().getElements();
            for (int size = this.square.getObjects().size(), i = 0; i < size; ++i) {
                if (i != objectIndex) {
                    final PropertyContainer properties = array[i].getProperties();
                    if (properties != null && properties.Is(this.getNorth() ? IsoFlagType.cutN : IsoFlagType.cutW) && properties.Is(IsoFlagType.makeWindowInvincible)) {
                        return true;
                    }
                }
            }
        }
        return this.sprite != null && this.sprite.getProperties().Is(IsoFlagType.makeWindowInvincible);
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
        return true;
    }
    
    @Override
    public IsoBarricade getBarricadeForCharacter(final IsoGameCharacter isoGameCharacter) {
        return IsoBarricade.GetBarricadeForCharacter(this, isoGameCharacter);
    }
    
    @Override
    public IsoBarricade getBarricadeOppositeCharacter(final IsoGameCharacter isoGameCharacter) {
        return IsoBarricade.GetBarricadeOppositeCharacter(this, isoGameCharacter);
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
        if (this.north) {
            return vector2.set(this.getX() + 0.5f, this.getY());
        }
        return vector2.set(this.getX(), this.getY() + 0.5f);
    }
    
    public void setIsLocked(final boolean locked) {
        this.Locked = locked;
    }
    
    public IsoSprite getOpenSprite() {
        return this.openSprite;
    }
    
    public void setOpenSprite(final IsoSprite openSprite) {
        this.openSprite = openSprite;
    }
    
    public void setSmashed(final boolean b) {
        if (b) {
            this.destroyed = true;
            this.sprite = this.smashedSprite;
        }
        else {
            this.destroyed = false;
            this.sprite = (this.open ? this.openSprite : this.closedSprite);
            this.Health = this.MaxHealth;
        }
        this.glassRemoved = false;
    }
    
    public IsoSprite getSmashedSprite() {
        return this.smashedSprite;
    }
    
    public void setSmashedSprite(final IsoSprite smashedSprite) {
        this.smashedSprite = smashedSprite;
    }
    
    public void setPermaLocked(final Boolean b) {
        this.PermaLocked = b;
    }
    
    public boolean isPermaLocked() {
        return this.PermaLocked;
    }
    
    public static boolean canClimbThroughHelper(final IsoGameCharacter isoGameCharacter, final IsoGridSquare isoGridSquare, final IsoGridSquare isoGridSquare2, final boolean b) {
        IsoGridSquare isoGridSquare3 = isoGridSquare;
        float n = 0.5f;
        float n2 = 0.5f;
        if (b) {
            if (isoGameCharacter.getY() >= isoGridSquare.getY()) {
                isoGridSquare3 = isoGridSquare2;
                n2 = 0.7f;
            }
            else {
                n2 = 0.3f;
            }
        }
        else if (isoGameCharacter.getX() >= isoGridSquare.getX()) {
            isoGridSquare3 = isoGridSquare2;
            n = 0.7f;
        }
        else {
            n = 0.3f;
        }
        if (isoGridSquare3 == null) {
            return false;
        }
        if (isoGridSquare3.isSolid()) {
            return false;
        }
        if (isoGridSquare3.Is(IsoFlagType.water)) {
            return false;
        }
        if (!isoGameCharacter.canClimbDownSheetRope(isoGridSquare3) && !isoGridSquare3.HasStairsBelow() && !PolygonalMap2.instance.canStandAt(isoGridSquare3.x + n, isoGridSquare3.y + n2, isoGridSquare3.z, null, 19)) {
            return !isoGridSquare3.TreatAsSolidFloor();
        }
        return !GameClient.bClient || !(isoGameCharacter instanceof IsoPlayer) || SafeHouse.isSafeHouse(isoGridSquare3, ((IsoPlayer)isoGameCharacter).getUsername(), true) == null || ServerOptions.instance.SafehouseAllowTrepass.getValue();
    }
    
    public boolean canClimbThrough(final IsoGameCharacter isoGameCharacter) {
        if (this.square == null || this.isInvincible()) {
            return false;
        }
        if (this.isBarricaded()) {
            return false;
        }
        if (isoGameCharacter != null && !canClimbThroughHelper(isoGameCharacter, this.getSquare(), this.getOppositeSquare(), this.north)) {
            return false;
        }
        final IsoGameCharacter firstCharacterClosing = this.getFirstCharacterClosing();
        return (firstCharacterClosing == null || !firstCharacterClosing.isVariable("CloseWindowOutcome", "success")) && (this.Health <= 0 || this.destroyed || this.open);
    }
    
    public IsoGameCharacter getFirstCharacterClimbingThrough() {
        final IsoGameCharacter firstCharacterClimbingThrough = this.getFirstCharacterClimbingThrough(this.getSquare());
        if (firstCharacterClimbingThrough != null) {
            return firstCharacterClimbingThrough;
        }
        return this.getFirstCharacterClimbingThrough(this.getOppositeSquare());
    }
    
    public IsoGameCharacter getFirstCharacterClimbingThrough(final IsoGridSquare isoGridSquare) {
        if (isoGridSquare == null) {
            return null;
        }
        for (int i = 0; i < isoGridSquare.getMovingObjects().size(); ++i) {
            final IsoGameCharacter isoGameCharacter = Type.tryCastTo(isoGridSquare.getMovingObjects().get(i), IsoGameCharacter.class);
            if (isoGameCharacter != null && isoGameCharacter.isClimbingThroughWindow(this)) {
                return isoGameCharacter;
            }
        }
        return null;
    }
    
    public IsoGameCharacter getFirstCharacterClosing() {
        final IsoGameCharacter firstCharacterClosing = this.getFirstCharacterClosing(this.getSquare());
        if (firstCharacterClosing != null) {
            return firstCharacterClosing;
        }
        return this.getFirstCharacterClosing(this.getOppositeSquare());
    }
    
    public IsoGameCharacter getFirstCharacterClosing(final IsoGridSquare isoGridSquare) {
        if (isoGridSquare == null) {
            return null;
        }
        for (int i = 0; i < isoGridSquare.getMovingObjects().size(); ++i) {
            final IsoGameCharacter isoGameCharacter = Type.tryCastTo(isoGridSquare.getMovingObjects().get(i), IsoGameCharacter.class);
            if (isoGameCharacter != null && isoGameCharacter.isClosingWindow(this)) {
                return isoGameCharacter;
            }
        }
        return null;
    }
    
    public boolean isGlassRemoved() {
        return this.glassRemoved;
    }
    
    public void setGlassRemoved(final boolean b) {
        if (!this.destroyed) {
            return;
        }
        if (b) {
            this.sprite = this.glassRemovedSprite;
            this.glassRemoved = true;
        }
        else {
            this.sprite = this.smashedSprite;
            this.glassRemoved = false;
        }
        if (this.getObjectIndex() != -1) {
            PolygonalMap2.instance.squareChanged(this.square);
        }
    }
    
    public void removeBrokenGlass() {
        if (GameClient.bClient) {
            GameClient.instance.smashWindow(this, 2);
        }
        else {
            this.setGlassRemoved(true);
        }
    }
    
    public IsoBarricade addBarricadesDebug(final int n, final boolean b) {
        final IsoBarricade addBarricadeToObject = IsoBarricade.AddBarricadeToObject(this, ((this.square.getRoom() == null) ? this.square : this.getOppositeSquare()) != this.square);
        if (addBarricadeToObject != null) {
            for (int i = 0; i < n; ++i) {
                if (b) {
                    addBarricadeToObject.addMetalBar(null, null);
                }
                else {
                    addBarricadeToObject.addPlank(null, null);
                }
            }
        }
        return addBarricadeToObject;
    }
    
    public void addRandomBarricades() {
        final IsoGridSquare isoGridSquare = (this.square.getRoom() == null) ? this.square : this.getOppositeSquare();
        if (this.getZ() == 0.0f && isoGridSquare != null && isoGridSquare.getRoom() == null) {
            final IsoBarricade addBarricadeToObject = IsoBarricade.AddBarricadeToObject(this, isoGridSquare != this.square);
            if (addBarricadeToObject != null) {
                for (int next = Rand.Next(1, 4), i = 0; i < next; ++i) {
                    addBarricadeToObject.addPlank(null, null);
                }
                if (GameServer.bServer) {
                    addBarricadeToObject.transmitCompleteItemToClients();
                }
            }
        }
        else {
            this.addSheet(null);
            this.HasCurtains().ToggleDoor(null);
        }
    }
    
    public enum WindowType
    {
        SinglePane, 
        DoublePane;
        
        private static /* synthetic */ WindowType[] $values() {
            return new WindowType[] { WindowType.SinglePane, WindowType.DoublePane };
        }
        
        static {
            $VALUES = $values();
        }
    }
}

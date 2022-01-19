// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.objects;

import zombie.characters.BaseCharacterSoundEmitter;
import java.util.ArrayList;
import zombie.iso.IsoChunk;
import zombie.vehicles.BaseVehicle;
import zombie.network.ServerMap;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.BuildingDef;
import java.util.Iterator;
import zombie.network.PacketTypes;
import zombie.core.network.ByteBufferWriter;
import zombie.vehicles.PolygonalMap2;
import zombie.core.raknet.UdpConnection;
import zombie.iso.weather.fx.WeatherFxMask;
import zombie.iso.LosUtil;
import zombie.core.Translator;
import zombie.debug.DebugOptions;
import zombie.iso.IsoWorld;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.util.StringUtils;
import zombie.Lua.LuaEventManager;
import zombie.audio.parameters.ParameterMeleeHitSurface;
import zombie.GameTime;
import zombie.characters.skills.PerkFactory;
import zombie.util.Type;
import zombie.characters.IsoPlayer;
import zombie.inventory.types.HandWeapon;
import zombie.core.math.PZMath;
import zombie.network.GameServer;
import zombie.WorldSoundManager;
import zombie.ai.states.ThumpState;
import zombie.characters.IsoZombie;
import zombie.characters.IsoSurvivor;
import zombie.iso.IsoMovingObject;
import java.io.IOException;
import zombie.network.GameClient;
import zombie.SystemDisabler;
import java.nio.ByteBuffer;
import zombie.core.Rand;
import zombie.SandboxOptions;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.core.properties.PropertyContainer;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.core.Core;
import zombie.iso.IsoGridSquare;
import zombie.characters.IsoGameCharacter;
import zombie.inventory.types.Key;
import zombie.iso.IsoCamera;
import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;
import zombie.iso.IsoDirections;
import zombie.core.opengl.Shader;
import zombie.core.textures.ColorInfo;
import zombie.iso.IsoCell;
import zombie.iso.Vector2;
import se.krka.kahlua.vm.KahluaTable;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.objects.interfaces.Thumpable;
import zombie.iso.objects.interfaces.BarricadeAble;
import zombie.iso.IsoObject;

public class IsoDoor extends IsoObject implements BarricadeAble, Thumpable
{
    public int Health;
    public boolean lockedByKey;
    private boolean haveKey;
    public boolean Locked;
    public int MaxHealth;
    public int PushedMaxStrength;
    public int PushedStrength;
    public DoorType type;
    IsoSprite closedSprite;
    public boolean north;
    int gid;
    public boolean open;
    IsoSprite openSprite;
    private boolean destroyed;
    private boolean bHasCurtain;
    private boolean bCurtainInside;
    private boolean bCurtainOpen;
    KahluaTable table;
    public static final Vector2 tempo;
    private IsoSprite curtainN;
    private IsoSprite curtainS;
    private IsoSprite curtainW;
    private IsoSprite curtainE;
    private IsoSprite curtainNopen;
    private IsoSprite curtainSopen;
    private IsoSprite curtainWopen;
    private IsoSprite curtainEopen;
    private static final int[] DoubleDoorNorthSpriteOffset;
    private static final int[] DoubleDoorWestSpriteOffset;
    private static final int[] DoubleDoorNorthClosedXOffset;
    private static final int[] DoubleDoorNorthOpenXOffset;
    private static final int[] DoubleDoorNorthClosedYOffset;
    private static final int[] DoubleDoorNorthOpenYOffset;
    private static final int[] DoubleDoorWestClosedXOffset;
    private static final int[] DoubleDoorWestOpenXOffset;
    private static final int[] DoubleDoorWestClosedYOffset;
    private static final int[] DoubleDoorWestOpenYOffset;
    
    public IsoDoor(final IsoCell isoCell) {
        super(isoCell);
        this.Health = 500;
        this.lockedByKey = false;
        this.haveKey = false;
        this.Locked = false;
        this.MaxHealth = 500;
        this.PushedMaxStrength = 0;
        this.PushedStrength = 0;
        this.type = DoorType.WeakWooden;
        this.north = false;
        this.gid = -1;
        this.open = false;
        this.destroyed = false;
    }
    
    @Override
    public String getObjectName() {
        return "Door";
    }
    
    @Override
    public void render(final float n, final float n2, final float n3, final ColorInfo colorInfo, final boolean b, final boolean b2, final Shader shader) {
        this.checkKeyHighlight(n, n2);
        if (!this.bHasCurtain) {
            super.render(n, n2, n3, colorInfo, b, b2, shader);
            return;
        }
        this.initCurtainSprites();
        final IsoDirections spriteEdge = this.getSpriteEdge(false);
        this.prerender(n, n2, n3, colorInfo, b, b2, spriteEdge);
        super.render(n, n2, n3, colorInfo, b, b2, shader);
        this.postrender(n, n2, n3, colorInfo, b, b2, spriteEdge);
    }
    
    @Override
    public void renderWallTile(final float n, final float n2, final float n3, final ColorInfo colorInfo, final boolean b, final boolean b2, final Shader shader, final Consumer<TextureDraw> consumer) {
        this.checkKeyHighlight(n, n2);
        if (!this.bHasCurtain) {
            super.renderWallTile(n, n2, n3, colorInfo, b, b2, shader, consumer);
            return;
        }
        this.initCurtainSprites();
        final IsoDirections spriteEdge = this.getSpriteEdge(false);
        this.prerender(n, n2, n3, colorInfo, b, b2, spriteEdge);
        super.renderWallTile(n, n2, n3, colorInfo, b, b2, shader, consumer);
        this.postrender(n, n2, n3, colorInfo, b, b2, spriteEdge);
    }
    
    private void checkKeyHighlight(final float n, final float n2) {
        final int playerIndex = IsoCamera.frameState.playerIndex;
        final IsoGameCharacter camCharacter = IsoCamera.frameState.CamCharacter;
        final Key key = Key.highlightDoor[playerIndex];
        if (key != null && n >= camCharacter.getX() - 20.0f && n2 >= camCharacter.getY() - 20.0f && n < camCharacter.getX() + 20.0f && n2 < camCharacter.getY() + 20.0f) {
            boolean seen = this.square.isSeen(playerIndex);
            if (!seen) {
                final IsoGridSquare oppositeSquare = this.getOppositeSquare();
                seen = (oppositeSquare != null && oppositeSquare.isSeen(playerIndex));
            }
            if (seen) {
                this.checkKeyId();
                if (this.getKeyId() == key.getKeyId()) {
                    this.setHighlighted(true);
                }
            }
        }
    }
    
    private void prerender(final float n, final float n2, final float n3, final ColorInfo colorInfo, final boolean b, final boolean b2, final IsoDirections isoDirections) {
        if (Core.TileScale == 1) {
            switch (isoDirections) {
                case N: {
                    this.prerender1xN(n, n2, n3, colorInfo, b, b2, null);
                    break;
                }
                case S: {
                    this.prerender1xS(n, n2, n3, colorInfo, b, b2, null);
                    break;
                }
                case W: {
                    this.prerender1xW(n, n2, n3, colorInfo, b, b2, null);
                    break;
                }
                case E: {
                    this.prerender1xE(n, n2, n3, colorInfo, b, b2, null);
                    break;
                }
            }
            return;
        }
        switch (isoDirections) {
            case N: {
                this.prerender2xN(n, n2, n3, colorInfo, b, b2, null);
                break;
            }
            case S: {
                this.prerender2xS(n, n2, n3, colorInfo, b, b2, null);
                break;
            }
            case W: {
                this.prerender2xW(n, n2, n3, colorInfo, b, b2, null);
                break;
            }
            case E: {
                this.prerender2xE(n, n2, n3, colorInfo, b, b2, null);
                break;
            }
        }
    }
    
    private void postrender(final float n, final float n2, final float n3, final ColorInfo colorInfo, final boolean b, final boolean b2, final IsoDirections isoDirections) {
        if (Core.TileScale == 1) {
            switch (isoDirections) {
                case N: {
                    this.postrender1xN(n, n2, n3, colorInfo, b, b2, null);
                    break;
                }
                case S: {
                    this.postrender1xS(n, n2, n3, colorInfo, b, b2, null);
                    break;
                }
                case W: {
                    this.postrender1xW(n, n2, n3, colorInfo, b, b2, null);
                    break;
                }
                case E: {
                    this.postrender1xE(n, n2, n3, colorInfo, b, b2, null);
                    break;
                }
            }
            return;
        }
        switch (isoDirections) {
            case N: {
                this.postrender2xN(n, n2, n3, colorInfo, b, b2, null);
                break;
            }
            case S: {
                this.postrender2xS(n, n2, n3, colorInfo, b, b2, null);
                break;
            }
            case W: {
                this.postrender2xW(n, n2, n3, colorInfo, b, b2, null);
                break;
            }
            case E: {
                this.postrender2xE(n, n2, n3, colorInfo, b, b2, null);
                break;
            }
        }
    }
    
    private void prerender1xN(final float n, final float n2, final float n3, final ColorInfo colorInfo, final boolean b, final boolean b2, final Shader shader) {
        if (this.bCurtainInside) {
            if (!this.north && this.open) {
                (this.bCurtainOpen ? this.curtainSopen : this.curtainS).render(null, n, n2 - 1.0f, n3, this.dir, this.offsetX + 3.0f, this.offsetY + (this.bCurtainOpen ? -14 : -14), colorInfo, true);
            }
        }
        else if (this.north && !this.open) {
            (this.bCurtainOpen ? this.curtainSopen : this.curtainS).render(null, n, n2 - 1.0f, n3, this.dir, this.offsetX - 1.0f - 1.0f, this.offsetY - 15.0f, colorInfo, true);
        }
    }
    
    private void postrender1xN(final float n, final float n2, final float n3, final ColorInfo colorInfo, final boolean b, final boolean b2, final Shader shader) {
        if (this.bCurtainInside) {
            if (this.north && !this.open) {
                (this.bCurtainOpen ? this.curtainNopen : this.curtainN).render(null, n, n2, n3, this.dir, this.offsetX - 10.0f - 1.0f, this.offsetY - 10.0f, colorInfo, true);
            }
        }
        else if (!this.north && this.open) {
            (this.bCurtainOpen ? this.curtainNopen : this.curtainN).render(null, n, n2, n3, this.dir, this.offsetX - 4.0f, this.offsetY + (this.bCurtainOpen ? -10 : -10), colorInfo, true);
        }
    }
    
    private void prerender1xS(final float n, final float n2, final float n3, final ColorInfo colorInfo, final boolean b, final boolean b2, final Shader shader) {
        assert !this.north && this.open;
        if (!this.bCurtainInside) {
            (this.bCurtainOpen ? this.curtainSopen : this.curtainS).render(null, n, n2, n3, this.dir, this.offsetX + (this.bCurtainOpen ? -14 : -14) / 2, this.offsetY + (this.bCurtainOpen ? -16 : -16) / 2, colorInfo, true);
        }
    }
    
    private void postrender1xS(final float n, final float n2, final float n3, final ColorInfo colorInfo, final boolean b, final boolean b2, final Shader shader) {
        assert !this.north && this.open;
        if (this.bCurtainInside) {
            (this.bCurtainOpen ? this.curtainNopen : this.curtainN).render(null, n, n2 + 1.0f, n3, this.dir, this.offsetX + (this.bCurtainOpen ? -28 : -28) / 2, this.offsetY + (this.bCurtainOpen ? -8 : -8) / 2, colorInfo, true);
        }
    }
    
    private void prerender1xW(final float n, final float n2, final float n3, final ColorInfo colorInfo, final boolean b, final boolean b2, final Shader shader) {
        if (this.bCurtainInside) {
            if (this.north && this.open) {
                (this.bCurtainOpen ? this.curtainEopen : this.curtainE).render(null, n - 1.0f, n2, n3, this.dir, this.offsetX + (this.bCurtainOpen ? -16 : -18), this.offsetY + (this.bCurtainOpen ? -14 : -15), colorInfo, true);
            }
            if (!this.north && this.open) {
                (this.bCurtainOpen ? this.curtainSopen : this.curtainS).render(null, n, n2 - 1.0f, n3, this.dir, this.offsetX + 3.0f, this.offsetY + (this.bCurtainOpen ? -14 : -14), colorInfo, true);
            }
        }
        else {
            if (this.north && !this.open) {
                (this.bCurtainOpen ? this.curtainSopen : this.curtainS).render(null, n, n2 - 1.0f, n3, this.dir, this.offsetX - 1.0f - 1.0f, this.offsetY - 15.0f, colorInfo, true);
            }
            if (!this.north && !this.open) {
                (this.bCurtainOpen ? this.curtainEopen : this.curtainE).render(null, n - 1.0f, n2, n3, this.dir, this.offsetX + (this.bCurtainOpen ? -12 : -14), this.offsetY + (this.bCurtainOpen ? -14 : -15), colorInfo, true);
            }
        }
    }
    
    private void postrender1xW(final float n, final float n2, final float n3, final ColorInfo colorInfo, final boolean b, final boolean b2, final Shader shader) {
        if (this.bCurtainInside) {
            if (this.north && !this.open) {
                (this.bCurtainOpen ? this.curtainNopen : this.curtainN).render(null, n, n2, n3, this.dir, this.offsetX - 10.0f - 1.0f, this.offsetY - 10.0f, colorInfo, true);
            }
            if (!this.north && !this.open) {
                (this.bCurtainOpen ? this.curtainWopen : this.curtainW).render(null, n, n2, n3, this.dir, this.offsetX - 2.0f - 1.0f, this.offsetY - 10.0f, colorInfo, true);
            }
        }
        else {
            if (this.north && this.open) {
                (this.bCurtainOpen ? this.curtainWopen : this.curtainW).render(null, n, n2, n3, this.dir, this.offsetX - 9.0f, this.offsetY - 10.0f, colorInfo, true);
            }
            if (!this.north && this.open) {
                (this.bCurtainOpen ? this.curtainNopen : this.curtainN).render(null, n, n2, n3, this.dir, this.offsetX - 4.0f, this.offsetY + (this.bCurtainOpen ? -10 : -10), colorInfo, true);
            }
        }
    }
    
    private void prerender1xE(final float n, final float n2, final float n3, final ColorInfo colorInfo, final boolean b, final boolean b2, final Shader shader) {
        assert this.north && this.open;
        if (!this.bCurtainInside) {
            (this.bCurtainOpen ? this.curtainEopen : this.curtainE).render(null, n, n2, n3, this.dir, this.offsetX + (this.bCurtainOpen ? -13 : -18) / 2, this.offsetY + (this.bCurtainOpen ? -15 : -18) / 2, colorInfo, true);
        }
    }
    
    private void postrender1xE(final float n, final float n2, final float n3, final ColorInfo colorInfo, final boolean b, final boolean b2, final Shader shader) {
        assert this.north && this.open;
        if (this.bCurtainInside) {
            (this.bCurtainOpen ? this.curtainWopen : this.curtainW).render(null, n + 1.0f, n2, n3, this.dir, this.offsetX + (this.bCurtainOpen ? 0 : 0), this.offsetY + (this.bCurtainOpen ? 0 : 0), colorInfo, true);
        }
    }
    
    private void prerender2xN(final float n, final float n2, final float n3, final ColorInfo colorInfo, final boolean b, final boolean b2, final Shader shader) {
        if (this.bCurtainInside) {
            if (!this.north && this.open) {
                (this.bCurtainOpen ? this.curtainSopen : this.curtainS).render(null, n, n2 - 1.0f, n3, this.dir, this.offsetX + 7.0f, this.offsetY + (this.bCurtainOpen ? -28 : -28), colorInfo, true);
            }
        }
        else if (this.north && !this.open) {
            (this.bCurtainOpen ? this.curtainSopen : this.curtainS).render(null, n, n2 - 1.0f, n3, this.dir, this.offsetX - 3.0f, this.offsetY + (this.bCurtainOpen ? -30 : -30), colorInfo, true);
        }
    }
    
    private void postrender2xN(final float n, final float n2, final float n3, final ColorInfo colorInfo, final boolean b, final boolean b2, final Shader shader) {
        if (this.bCurtainInside) {
            if (this.north && !this.open) {
                (this.bCurtainOpen ? this.curtainNopen : this.curtainN).render(null, n, n2, n3, this.dir, this.offsetX - 20.0f, this.offsetY + (this.bCurtainOpen ? -20 : -20), colorInfo, true);
            }
        }
        else if (!this.north && this.open) {
            (this.bCurtainOpen ? this.curtainNopen : this.curtainN).render(null, n, n2, n3, this.dir, this.offsetX - 8.0f, this.offsetY + (this.bCurtainOpen ? -20 : -20), colorInfo, true);
        }
    }
    
    private void prerender2xS(final float n, final float n2, final float n3, final ColorInfo colorInfo, final boolean b, final boolean b2, final Shader shader) {
        assert !this.north && this.open;
        if (!this.bCurtainInside) {
            (this.bCurtainOpen ? this.curtainSopen : this.curtainS).render(null, n, n2, n3, this.dir, this.offsetX + (this.bCurtainOpen ? -14 : -14), this.offsetY + (this.bCurtainOpen ? -16 : -16), colorInfo, true);
        }
    }
    
    private void postrender2xS(final float n, final float n2, final float n3, final ColorInfo colorInfo, final boolean b, final boolean b2, final Shader shader) {
        assert !this.north && this.open;
        if (this.bCurtainInside) {
            (this.bCurtainOpen ? this.curtainNopen : this.curtainN).render(null, n, n2 + 1.0f, n3, this.dir, this.offsetX + (this.bCurtainOpen ? -28 : -28), this.offsetY + (this.bCurtainOpen ? -8 : -8), colorInfo, true);
        }
    }
    
    private void prerender2xW(final float n, final float n2, final float n3, final ColorInfo colorInfo, final boolean b, final boolean b2, final Shader shader) {
        if (this.bCurtainInside) {
            if (this.north && this.open) {
                (this.bCurtainOpen ? this.curtainEopen : this.curtainE).render(null, n - 1.0f, n2, n3, this.dir, this.offsetX + (this.bCurtainOpen ? -32 : -37), this.offsetY + (this.bCurtainOpen ? -28 : -31), colorInfo, true);
            }
        }
        else if (!this.north && !this.open) {
            (this.bCurtainOpen ? this.curtainEopen : this.curtainE).render(null, n - 1.0f, n2, n3, this.dir, this.offsetX + (this.bCurtainOpen ? -22 : -26), this.offsetY + (this.bCurtainOpen ? -28 : -31), colorInfo, true);
        }
    }
    
    private void postrender2xW(final float n, final float n2, final float n3, final ColorInfo colorInfo, final boolean b, final boolean b2, final Shader shader) {
        if (this.bCurtainInside) {
            if (!this.north && !this.open) {
                (this.bCurtainOpen ? this.curtainWopen : this.curtainW).render(null, n, n2, n3, this.dir, this.offsetX - 5.0f, this.offsetY + (this.bCurtainOpen ? -20 : -20), colorInfo, true);
            }
        }
        else if (this.north && this.open) {
            (this.bCurtainOpen ? this.curtainWopen : this.curtainW).render(null, n, n2, n3, this.dir, this.offsetX - 19.0f, this.offsetY + (this.bCurtainOpen ? -20 : -20), colorInfo, true);
        }
    }
    
    private void prerender2xE(final float n, final float n2, final float n3, final ColorInfo colorInfo, final boolean b, final boolean b2, final Shader shader) {
        assert this.north && this.open;
        if (!this.bCurtainInside) {
            (this.bCurtainOpen ? this.curtainEopen : this.curtainE).render(null, n, n2, n3, this.dir, this.offsetX + (this.bCurtainOpen ? -13 : -18), this.offsetY + (this.bCurtainOpen ? -15 : -18), colorInfo, true);
        }
    }
    
    private void postrender2xE(final float n, final float n2, final float n3, final ColorInfo colorInfo, final boolean b, final boolean b2, final Shader shader) {
        assert this.north && this.open;
        if (this.bCurtainInside) {
            (this.bCurtainOpen ? this.curtainWopen : this.curtainW).render(null, n + 1.0f, n2, n3, this.dir, this.offsetX + (this.bCurtainOpen ? 0 : 0), this.offsetY + (this.bCurtainOpen ? 0 : 0), colorInfo, true);
        }
    }
    
    public IsoDirections getSpriteEdge(final boolean b) {
        if (!this.open || b) {
            return this.north ? IsoDirections.N : IsoDirections.W;
        }
        final PropertyContainer properties = this.getProperties();
        if (properties != null && properties.Is("GarageDoor")) {
            return this.north ? IsoDirections.N : IsoDirections.W;
        }
        if (properties != null && properties.Is(IsoFlagType.attachedE)) {
            return IsoDirections.E;
        }
        if (properties != null && properties.Is(IsoFlagType.attachedS)) {
            return IsoDirections.S;
        }
        return this.north ? IsoDirections.W : IsoDirections.N;
    }
    
    public IsoDoor(final IsoCell isoCell, final IsoGridSquare square, final IsoSprite openSprite, final boolean north) {
        this.Health = 500;
        this.lockedByKey = false;
        this.haveKey = false;
        this.Locked = false;
        this.MaxHealth = 500;
        this.PushedMaxStrength = 0;
        this.PushedStrength = 0;
        this.type = DoorType.WeakWooden;
        this.north = false;
        this.gid = -1;
        this.open = false;
        this.destroyed = false;
        this.OutlineOnMouseover = true;
        final int n = 2500;
        this.PushedStrength = n;
        this.PushedMaxStrength = n;
        this.closedSprite = openSprite;
        this.openSprite = IsoSprite.getSprite(IsoSpriteManager.instance, openSprite, 2);
        this.sprite = this.closedSprite;
        final String val = openSprite.getProperties().Val("GarageDoor");
        if (val != null) {
            if (Integer.parseInt(val) <= 3) {
                this.closedSprite = openSprite;
                this.openSprite = IsoSprite.getSprite(IsoSpriteManager.instance, openSprite, 8);
            }
            else {
                this.openSprite = openSprite;
                this.closedSprite = IsoSprite.getSprite(IsoSpriteManager.instance, openSprite, -8);
            }
        }
        this.square = square;
        this.north = north;
        switch (this.type) {
            case WeakWooden: {
                final int n2 = 500;
                this.Health = n2;
                this.MaxHealth = n2;
                break;
            }
            case StrongWooden: {
                final int n3 = 800;
                this.Health = n3;
                this.MaxHealth = n3;
                break;
            }
        }
        if (this.getSprite().getName() != null && this.getSprite().getName().contains("fences")) {
            final int n4 = 100;
            this.Health = n4;
            this.MaxHealth = n4;
        }
        int n5 = 69;
        if (SandboxOptions.instance.LockedHouses.getValue() == 1) {
            n5 = -1;
        }
        else if (SandboxOptions.instance.LockedHouses.getValue() == 2) {
            n5 = 5;
        }
        else if (SandboxOptions.instance.LockedHouses.getValue() == 3) {
            n5 = 10;
        }
        else if (SandboxOptions.instance.LockedHouses.getValue() == 4) {
            n5 = 50;
        }
        else if (SandboxOptions.instance.LockedHouses.getValue() == 5) {
            n5 = 60;
        }
        else if (SandboxOptions.instance.LockedHouses.getValue() == 6) {
            n5 = 70;
        }
        if (n5 > -1) {
            this.Locked = (Rand.Next(100) < n5);
            if (this.Locked && Rand.Next(3) == 0) {
                this.lockedByKey = true;
            }
        }
        if (this.getProperties().Is("forceLocked")) {
            this.Locked = true;
            this.lockedByKey = true;
        }
    }
    
    public IsoDoor(final IsoCell isoCell, final IsoGridSquare square, final String s, final boolean north) {
        this.Health = 500;
        this.lockedByKey = false;
        this.haveKey = false;
        this.Locked = false;
        this.MaxHealth = 500;
        this.PushedMaxStrength = 0;
        this.PushedStrength = 0;
        this.type = DoorType.WeakWooden;
        this.north = false;
        this.gid = -1;
        this.open = false;
        this.destroyed = false;
        this.OutlineOnMouseover = true;
        final int n = 2500;
        this.PushedStrength = n;
        this.PushedMaxStrength = n;
        this.closedSprite = IsoSprite.getSprite(IsoSpriteManager.instance, s, 0);
        this.openSprite = IsoSprite.getSprite(IsoSpriteManager.instance, s, 2);
        this.sprite = this.closedSprite;
        final String val = this.closedSprite.getProperties().Val("GarageDoor");
        if (val != null) {
            if (Integer.parseInt(val) <= 3) {
                this.openSprite = IsoSprite.getSprite(IsoSpriteManager.instance, s, 8);
            }
            else {
                this.openSprite = this.sprite;
                this.closedSprite = IsoSprite.getSprite(IsoSpriteManager.instance, s, -8);
            }
        }
        this.square = square;
        this.north = north;
        switch (this.type) {
            case WeakWooden: {
                final int n2 = 500;
                this.Health = n2;
                this.MaxHealth = n2;
                break;
            }
            case StrongWooden: {
                final int n3 = 800;
                this.Health = n3;
                this.MaxHealth = n3;
                break;
            }
        }
        if (this.getSprite().getName() != null && this.getSprite().getName().contains("fences")) {
            final int n4 = 100;
            this.Health = n4;
            this.MaxHealth = n4;
        }
    }
    
    public IsoDoor(final IsoCell isoCell, final IsoGridSquare square, final String s, final boolean north, final KahluaTable table) {
        this.Health = 500;
        this.lockedByKey = false;
        this.haveKey = false;
        this.Locked = false;
        this.MaxHealth = 500;
        this.PushedMaxStrength = 0;
        this.PushedStrength = 0;
        this.type = DoorType.WeakWooden;
        this.north = false;
        this.gid = -1;
        this.open = false;
        this.destroyed = false;
        this.OutlineOnMouseover = true;
        final int n = 2500;
        this.PushedStrength = n;
        this.PushedMaxStrength = n;
        this.closedSprite = IsoSprite.getSprite(IsoSpriteManager.instance, s, 0);
        this.openSprite = IsoSprite.getSprite(IsoSpriteManager.instance, s, 2);
        this.table = table;
        this.sprite = this.closedSprite;
        final String val = this.sprite.getProperties().Val("GarageDoor");
        if (val != null) {
            if (Integer.parseInt(val) <= 3) {
                this.openSprite = IsoSprite.getSprite(IsoSpriteManager.instance, s, 8);
            }
            else {
                this.openSprite = this.sprite;
                this.closedSprite = IsoSprite.getSprite(IsoSpriteManager.instance, s, -8);
            }
        }
        this.square = square;
        this.north = north;
        switch (this.type) {
            case WeakWooden: {
                final int n2 = 500;
                this.Health = n2;
                this.MaxHealth = n2;
                break;
            }
            case StrongWooden: {
                final int n3 = 800;
                this.Health = n3;
                this.MaxHealth = n3;
                break;
            }
        }
        if (this.getSprite().getName() != null && this.getSprite().getName().contains("fences")) {
            final int n4 = 100;
            this.Health = n4;
            this.MaxHealth = n4;
        }
    }
    
    @Override
    public void load(final ByteBuffer byteBuffer, final int n, final boolean b) throws IOException {
        super.load(byteBuffer, n, b);
        this.open = (byteBuffer.get() == 1);
        this.Locked = (byteBuffer.get() == 1);
        this.north = (byteBuffer.get() == 1);
        this.Health = byteBuffer.getInt();
        this.MaxHealth = byteBuffer.getInt();
        this.closedSprite = IsoSprite.getSprite(IsoSpriteManager.instance, byteBuffer.getInt());
        this.openSprite = IsoSprite.getSprite(IsoSpriteManager.instance, byteBuffer.getInt());
        this.OutlineOnMouseover = true;
        final int n2 = 2500;
        this.PushedStrength = n2;
        this.PushedMaxStrength = n2;
        if (n >= 57) {
            this.keyId = byteBuffer.getInt();
            this.lockedByKey = (byteBuffer.get() == 1);
        }
        if (n >= 80) {
            final byte value = byteBuffer.get();
            if ((value & 0x1) != 0x0) {
                this.bHasCurtain = true;
                this.bCurtainOpen = ((value & 0x2) != 0x0);
                this.bCurtainInside = ((value & 0x4) != 0x0);
            }
        }
        if (SystemDisabler.doObjectStateSyncEnable && GameClient.bClient) {
            GameClient.instance.objectSyncReq.putRequestLoad(this.square);
        }
    }
    
    @Override
    public void save(final ByteBuffer byteBuffer, final boolean b) throws IOException {
        super.save(byteBuffer, b);
        byteBuffer.put((byte)(this.open ? 1 : 0));
        byteBuffer.put((byte)(this.Locked ? 1 : 0));
        byteBuffer.put((byte)(this.north ? 1 : 0));
        byteBuffer.putInt(this.Health);
        byteBuffer.putInt(this.MaxHealth);
        byteBuffer.putInt(this.closedSprite.ID);
        byteBuffer.putInt(this.openSprite.ID);
        byteBuffer.putInt(this.getKeyId());
        byteBuffer.put((byte)(this.isLockedByKey() ? 1 : 0));
        byte b2 = 0;
        if (this.bHasCurtain) {
            b2 |= 0x1;
            if (this.bCurtainOpen) {
                b2 |= 0x2;
            }
            if (this.bCurtainInside) {
                b2 |= 0x4;
            }
        }
        byteBuffer.put(b2);
    }
    
    @Override
    public void saveState(final ByteBuffer byteBuffer) throws IOException {
        byteBuffer.put((byte)(this.open ? 1 : 0));
        byteBuffer.put((byte)(this.Locked ? 1 : 0));
        byteBuffer.put((byte)(this.lockedByKey ? 1 : 0));
    }
    
    @Override
    public void loadState(final ByteBuffer byteBuffer) throws IOException {
        final boolean open = byteBuffer.get() == 1;
        final boolean locked = byteBuffer.get() == 1;
        final boolean lockedByKey = byteBuffer.get() == 1;
        if (open != this.open) {
            this.open = open;
            this.sprite = (open ? this.openSprite : this.closedSprite);
        }
        if (locked != this.Locked) {
            this.Locked = locked;
        }
        if (lockedByKey != this.lockedByKey) {
            this.lockedByKey = lockedByKey;
        }
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
        boolean north = this.north;
        if (!this.isBarricaded()) {
            return false;
        }
        if (isoMovingObject instanceof IsoSurvivor && ((IsoSurvivor)isoMovingObject).getInventory().contains("Hammer")) {
            return false;
        }
        if (this.open) {
            north = !north;
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
        final boolean north = this.north;
        if (this.open) {
            return false;
        }
        if (isoGridSquare == this.square) {
            if (north && isoGridSquare2.getY() < isoGridSquare.getY()) {
                if (isoMovingObject != null) {
                    isoMovingObject.collideWith(this);
                }
                return true;
            }
            if (!north && isoGridSquare2.getX() < isoGridSquare.getX()) {
                if (isoMovingObject != null) {
                    isoMovingObject.collideWith(this);
                }
                return true;
            }
        }
        else {
            if (north && isoGridSquare2.getY() > isoGridSquare.getY()) {
                if (isoMovingObject != null) {
                    isoMovingObject.collideWith(this);
                }
                return true;
            }
            if (!north && isoGridSquare2.getX() > isoGridSquare.getX()) {
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
        boolean b = this.sprite != null && this.sprite.getProperties().Is("doorTrans");
        if (this.sprite != null && this.sprite.getProperties().Is("GarageDoor") && this.open) {
            b = true;
        }
        if (this.open) {
            b = true;
        }
        else if (this.bHasCurtain && !this.bCurtainOpen) {
            b = false;
        }
        boolean north = this.north;
        if (this.open) {
            north = !north;
        }
        if (isoGridSquare2.getZ() != isoGridSquare.getZ()) {
            return VisionResult.NoEffect;
        }
        if (isoGridSquare == this.square) {
            if (north && isoGridSquare2.getY() < isoGridSquare.getY()) {
                if (b) {
                    return VisionResult.Unblocked;
                }
                return VisionResult.Blocked;
            }
            else if (!north && isoGridSquare2.getX() < isoGridSquare.getX()) {
                if (b) {
                    return VisionResult.Unblocked;
                }
                return VisionResult.Blocked;
            }
        }
        else if (north && isoGridSquare2.getY() > isoGridSquare.getY()) {
            if (b) {
                return VisionResult.Unblocked;
            }
            return VisionResult.Blocked;
        }
        else if (!north && isoGridSquare2.getX() > isoGridSquare.getX()) {
            if (b) {
                return VisionResult.Unblocked;
            }
            return VisionResult.Blocked;
        }
        return VisionResult.NoEffect;
    }
    
    @Override
    public void Thump(final IsoMovingObject isoMovingObject) {
        if (this.isDestroyed()) {
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
        if (isoMovingObject instanceof IsoZombie) {
            if (((IsoZombie)isoMovingObject).cognition == 1 && !this.open && (!this.Locked || (isoMovingObject.getCurrentSquare() != null && !isoMovingObject.getCurrentSquare().Is(IsoFlagType.exterior)))) {
                this.ToggleDoor((IsoGameCharacter)isoMovingObject);
                if (this.open) {
                    return;
                }
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
            final int fastForwardDamageMultiplier = ThumpState.getFastForwardDamageMultiplier();
            final int strength = ((IsoZombie)isoMovingObject).strength;
            if (size >= 2) {
                this.DirtySlice();
                this.Damage(((IsoZombie)isoMovingObject).strength * fastForwardDamageMultiplier);
                if (SandboxOptions.instance.Lore.Strength.getValue() == 1) {
                    this.Damage(size * 2 * fastForwardDamageMultiplier);
                }
            }
            if (Core.GameMode.equals("LastStand")) {
                this.Damage(1 * fastForwardDamageMultiplier);
            }
            WorldSoundManager.instance.addSound(isoMovingObject, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, true, 4.0f, 15.0f);
            this.setRenderEffect(RenderEffectType.Hit_Door, true);
        }
        if (this.Health <= 0) {
            if (this.getSquare().getBuilding() != null) {
                this.getSquare().getBuilding().forceAwake();
            }
            this.playDoorSound(((IsoGameCharacter)isoMovingObject).getEmitter(), "Break");
            if (GameServer.bServer) {
                GameServer.PlayWorldSoundServer("BreakDoor", false, isoMovingObject.getCurrentSquare(), 0.2f, 20.0f, 1.1f, true);
            }
            WorldSoundManager.instance.addSound(null, this.square.getX(), this.square.getY(), this.square.getZ(), 10, 20, true, 4.0f, 15.0f);
            isoMovingObject.setThumpTarget(null);
            if (destroyDoubleDoor(this)) {
                return;
            }
            if (destroyGarageDoor(this)) {
                return;
            }
            this.destroy();
        }
    }
    
    @Override
    public Thumpable getThumpableFor(final IsoGameCharacter isoGameCharacter) {
        final IsoBarricade barricadeForCharacter = this.getBarricadeForCharacter(isoGameCharacter);
        if (barricadeForCharacter != null) {
            return barricadeForCharacter;
        }
        final IsoBarricade barricadeOppositeCharacter = this.getBarricadeOppositeCharacter(isoGameCharacter);
        if (barricadeOppositeCharacter != null) {
            return barricadeOppositeCharacter;
        }
        if (this.isDestroyed() || this.IsOpen()) {
            return null;
        }
        return this;
    }
    
    @Override
    public float getThumpCondition() {
        if (this.getMaxHealth() <= 0) {
            return 0.0f;
        }
        return PZMath.clamp(this.getHealth(), 0, this.getMaxHealth()) / (float)this.getMaxHealth();
    }
    
    @Override
    public void WeaponHit(final IsoGameCharacter isoGameCharacter, final HandWeapon handWeapon) {
        final IsoPlayer isoPlayer = Type.tryCastTo(isoGameCharacter, IsoPlayer.class);
        if (GameClient.bClient) {
            if (isoPlayer != null) {
                GameClient.instance.sendWeaponHit(isoPlayer, handWeapon, this);
            }
            this.setRenderEffect(RenderEffectType.Hit_Door, true);
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
        if (this.open) {
            return;
        }
        if (this.isDestroyed()) {
            return;
        }
        final int perkLevel = isoGameCharacter.getPerkLevel(PerkFactory.Perks.Strength);
        float n = 1.0f;
        if (perkLevel == 0) {
            n = 0.5f;
        }
        else if (perkLevel == 1) {
            n = 0.63f;
        }
        else if (perkLevel == 2) {
            n = 0.76f;
        }
        else if (perkLevel == 3) {
            n = 0.89f;
        }
        else if (perkLevel == 4) {
            n = 1.02f;
        }
        if (perkLevel == 6) {
            n = 1.15f;
        }
        else if (perkLevel == 7) {
            n = 1.27f;
        }
        else if (perkLevel == 8) {
            n = 1.3f;
        }
        else if (perkLevel == 9) {
            n = 1.45f;
        }
        else if (perkLevel == 10) {
            n = 1.7f;
        }
        this.Damage((int)(handWeapon.getDoorDamage() * 2.0f * n));
        this.setRenderEffect(RenderEffectType.Hit_Door, true);
        if (Rand.Next(10) == 0) {
            this.Damage((int)(handWeapon.getDoorDamage() * 6.0f * n));
        }
        final float n2 = GameTime.getInstance().getMultiplier() / 1.6f;
        switch (isoGameCharacter.getPerkLevel(PerkFactory.Perks.Fitness)) {
            case 0: {
                isoGameCharacter.exert(0.01f * n2);
                break;
            }
            case 1: {
                isoGameCharacter.exert(0.007f * n2);
                break;
            }
            case 2: {
                isoGameCharacter.exert(0.0065f * n2);
                break;
            }
            case 3: {
                isoGameCharacter.exert(0.006f * n2);
                break;
            }
            case 4: {
                isoGameCharacter.exert(0.005f * n2);
                break;
            }
            case 5: {
                isoGameCharacter.exert(0.004f * n2);
                break;
            }
            case 6: {
                isoGameCharacter.exert(0.0035f * n2);
                break;
            }
            case 7: {
                isoGameCharacter.exert(0.003f * n2);
                break;
            }
            case 8: {
                isoGameCharacter.exert(0.0025f * n2);
                break;
            }
            case 9: {
                isoGameCharacter.exert(0.002f * n2);
                break;
            }
        }
        this.DirtySlice();
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
                GameServer.PlayWorldSoundServer(handWeapon.getDoorHitSound(), false, this.getSquare(), 1.0f, 20.0f, 2.0f, false);
            }
        }
        WorldSoundManager.instance.addSound(isoGameCharacter, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, false, 0.0f, 15.0f);
        if ((!this.IsStrengthenedByPushedItems() && this.Health <= 0) || (this.IsStrengthenedByPushedItems() && this.Health <= -this.PushedMaxStrength)) {
            this.playDoorSound(isoGameCharacter.getEmitter(), "Break");
            if (GameServer.bServer) {
                GameServer.PlayWorldSoundServer("BreakDoor", false, this.getSquare(), 0.2f, 20.0f, 1.1f, true);
            }
            WorldSoundManager.instance.addSound(isoGameCharacter, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, false, 0.0f, 15.0f);
            if (destroyDoubleDoor(this)) {
                return;
            }
            if (destroyGarageDoor(this)) {
                return;
            }
            this.destroy();
            LuaEventManager.triggerEvent("OnContainerUpdate");
        }
    }
    
    public void destroy() {
        if (this.sprite != null && this.sprite.getProperties().Is("GarageDoor")) {
            this.destroyed = true;
            this.square.transmitRemoveItemFromSquare(this);
            return;
        }
        final PropertyContainer properties = this.getProperties();
        if (properties == null) {
            return;
        }
        final String val = properties.Val("Material");
        final String val2 = properties.Val("Material2");
        final String val3 = properties.Val("Material3");
        if (!StringUtils.isNullOrEmpty(val) || !StringUtils.isNullOrEmpty(val2) || !StringUtils.isNullOrEmpty(val3)) {
            this.addItemsFromProperties();
        }
        else {
            for (int n = Rand.Next(2) + 1, i = 0; i < n; ++i) {
                this.square.AddWorldInventoryItem("Base.Plank", 0.0f, 0.0f, 0.0f);
            }
        }
        final InventoryItem createItem = InventoryItemFactory.CreateItem("Base.Doorknob");
        createItem.setKeyId(this.checkKeyId());
        this.square.AddWorldInventoryItem(createItem, 0.0f, 0.0f, 0.0f);
        for (int next = Rand.Next(3), j = 0; j < next; ++j) {
            this.square.AddWorldInventoryItem("Base.Hinge", 0.0f, 0.0f, 0.0f);
        }
        if (this.bHasCurtain) {
            this.square.AddWorldInventoryItem("Base.Sheet", 0.0f, 0.0f, 0.0f);
        }
        this.destroyed = true;
        this.square.transmitRemoveItemFromSquare(this);
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
    
    public boolean isExteriorDoor(final IsoGameCharacter isoGameCharacter) {
        final IsoGridSquare square = this.getSquare();
        final IsoGridSquare oppositeSquare = this.getOppositeSquare();
        return oppositeSquare != null && ((square.Is(IsoFlagType.exterior) && oppositeSquare.getBuilding() != null && oppositeSquare.getBuilding().getDef() != null) || (square.getBuilding() != null && square.getBuilding().getDef() != null && oppositeSquare.Is(IsoFlagType.exterior)));
    }
    
    @Override
    public boolean isHoppable() {
        if (this.IsOpen()) {
            return false;
        }
        if (this.closedSprite == null) {
            return false;
        }
        final PropertyContainer properties = this.closedSprite.getProperties();
        return properties.Is(IsoFlagType.HoppableN) || properties.Is(IsoFlagType.HoppableW);
    }
    
    public boolean canClimbOver(final IsoGameCharacter isoGameCharacter) {
        return this.square != null && this.isHoppable() && (isoGameCharacter == null || IsoWindow.canClimbThroughHelper(isoGameCharacter, this.getSquare(), this.getOppositeSquare(), this.north));
    }
    
    public void ToggleDoorActual(final IsoGameCharacter isoGameCharacter) {
        if (Core.bDebug && DebugOptions.instance.CheatDoorUnlock.getValue()) {
            this.setLockedByKey(this.Locked = false);
        }
        if (this.isHoppable()) {
            this.setLockedByKey(this.Locked = false);
        }
        if (this.isBarricaded()) {
            if (isoGameCharacter != null) {
                this.playDoorSound(isoGameCharacter.getEmitter(), "Blocked");
                isoGameCharacter.setHaloNote(Translator.getText("IGUI_PlayerText_DoorBarricaded"), 255, 255, 255, 256.0f);
                this.setRenderEffect(RenderEffectType.Hit_Door, true);
            }
            return;
        }
        this.checkKeyId();
        if (this.Locked && !this.lockedByKey && this.getKeyId() != -1) {
            this.lockedByKey = true;
        }
        if (!this.open && isoGameCharacter instanceof IsoPlayer) {
            ((IsoPlayer)isoGameCharacter).TimeSinceOpenDoor = 0.0f;
        }
        this.DirtySlice();
        IsoGridSquare.RecalcLightTime = -1;
        GameTime.instance.lightSourceUpdate = 100.0f;
        this.square.InvalidateSpecialObjectPaths();
        if (this.isLockedByKey() && isoGameCharacter != null && isoGameCharacter instanceof IsoPlayer && (isoGameCharacter.getCurrentSquare().Is(IsoFlagType.exterior) || this.getProperties().Is("forceLocked")) && !this.open) {
            if (isoGameCharacter.getInventory().haveThisKeyId(this.getKeyId()) == null) {
                this.playDoorSound(isoGameCharacter.getEmitter(), "Locked");
                this.setRenderEffect(RenderEffectType.Hit_Door, true);
                return;
            }
            this.playDoorSound(isoGameCharacter.getEmitter(), "Unlock");
            this.playDoorSound(isoGameCharacter.getEmitter(), "Open");
            this.setLockedByKey(this.Locked = false);
        }
        boolean b = isoGameCharacter instanceof IsoPlayer && !isoGameCharacter.getCurrentSquare().isOutside();
        if ("Tutorial".equals(Core.getInstance().getGameMode()) && this.isLockedByKey()) {
            b = false;
        }
        if (isoGameCharacter instanceof IsoPlayer && this.getSprite().getProperties().Is("GarageDoor")) {
            if (this.getSprite().getProperties().Is("InteriorSide")) {
                b = (this.north ? (isoGameCharacter.getY() >= this.getY()) : (isoGameCharacter.getX() >= this.getX()));
            }
            else {
                b = (this.north ? (isoGameCharacter.getY() < this.getY()) : (isoGameCharacter.getX() < this.getX()));
            }
        }
        if (this.Locked && !b && !this.open) {
            this.playDoorSound(isoGameCharacter.getEmitter(), "Locked");
            this.setRenderEffect(RenderEffectType.Hit_Door, true);
            return;
        }
        if (this.getSprite().getProperties().Is("DoubleDoor")) {
            if (isDoubleDoorObstructed(this)) {
                if (isoGameCharacter != null) {
                    this.playDoorSound(isoGameCharacter.getEmitter(), "Blocked");
                    isoGameCharacter.setHaloNote(Translator.getText("IGUI_PlayerText_DoorBlocked"), 255, 255, 255, 256.0f);
                }
                return;
            }
            final boolean open = this.open;
            toggleDoubleDoor(this, true);
            if (open != this.open) {
                this.playDoorSound(isoGameCharacter.getEmitter(), this.open ? "Open" : "Close");
            }
        }
        else if (this.getSprite().getProperties().Is("GarageDoor")) {
            if (isGarageDoorObstructed(this)) {
                if (isoGameCharacter != null) {
                    this.playDoorSound(isoGameCharacter.getEmitter(), "Blocked");
                    isoGameCharacter.setHaloNote(Translator.getText("IGUI_PlayerText_DoorBlocked"), 255, 255, 255, 256.0f);
                }
                return;
            }
            final boolean open2 = this.open;
            toggleGarageDoor(this, true);
            if (open2 != this.open) {
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
            this.setLockedByKey(this.Locked = false);
            if (isoGameCharacter instanceof IsoPlayer) {
                for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
                    LosUtil.cachecleared[i] = true;
                }
                IsoGridSquare.setRecalcLightTime(-1);
            }
            this.open = !this.open;
            WeatherFxMask.forceMaskUpdateAll();
            this.sprite = this.closedSprite;
            if (this.open) {
                if (isoGameCharacter != null) {
                    this.playDoorSound(isoGameCharacter.getEmitter(), "Open");
                }
                this.sprite = this.openSprite;
            }
            else if (isoGameCharacter != null) {
                this.playDoorSound(isoGameCharacter.getEmitter(), "Close");
            }
            this.square.RecalcProperties();
            this.syncIsoObject(false, (byte)(this.open ? 1 : 0), null, null);
            PolygonalMap2.instance.squareChanged(this.square);
            LuaEventManager.triggerEvent("OnContainerUpdate");
        }
    }
    
    @Override
    public void syncIsoObjectSend(final ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putInt(this.square.getX());
        byteBufferWriter.putInt(this.square.getY());
        byteBufferWriter.putInt(this.square.getZ());
        byteBufferWriter.putByte((byte)this.square.getObjects().indexOf(this));
        byteBufferWriter.putByte((byte)1);
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
                this.lockedByKey = true;
                this.open = false;
                this.sprite = this.closedSprite;
            }
            else if (b2 == 4) {
                this.lockedByKey = false;
                this.open = false;
                this.sprite = this.closedSprite;
            }
            if (GameServer.bServer) {
                for (final UdpConnection udpConnection3 : GameServer.udpEngine.connections) {
                    if ((udpConnection != null && udpConnection3.getConnectedGUID() != udpConnection.getConnectedGUID()) || udpConnection == null) {
                        final ByteBufferWriter startPacket3 = udpConnection3.startPacket();
                        PacketTypes.PacketType.SyncIsoObject.doPacket(startPacket3);
                        this.syncIsoObjectSend(startPacket3);
                        startPacket3.putShort(short1);
                        PacketTypes.PacketType.SyncIsoObject.send(udpConnection3);
                    }
                }
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
        WeatherFxMask.forceMaskUpdateAll();
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
    
    void Damage(final int n) {
        this.DirtySlice();
        this.Health -= n;
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
        return this.getSprite() != null && !this.getSprite().getProperties().Is("DoubleDoor") && !this.getSprite().getProperties().Is("GarageDoor");
    }
    
    @Override
    public IsoBarricade getBarricadeForCharacter(final IsoGameCharacter isoGameCharacter) {
        return IsoBarricade.GetBarricadeForCharacter(this, isoGameCharacter);
    }
    
    @Override
    public IsoBarricade getBarricadeOppositeCharacter(final IsoGameCharacter isoGameCharacter) {
        return IsoBarricade.GetBarricadeOppositeCharacter(this, isoGameCharacter);
    }
    
    public boolean isLocked() {
        return this.Locked;
    }
    
    public void setLocked(final boolean locked) {
        this.Locked = locked;
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
    
    @Override
    public Vector2 getFacingPositionAlt(final Vector2 vector2) {
        if (this.square == null) {
            return vector2.set(0.0f, 0.0f);
        }
        switch (this.getSpriteEdge(false)) {
            case N: {
                return vector2.set(this.getX() + 0.5f, this.getY());
            }
            case S: {
                return vector2.set(this.getX() + 0.5f, this.getY() + 1.0f);
            }
            case W: {
                return vector2.set(this.getX(), this.getY() + 0.5f);
            }
            case E: {
                return vector2.set(this.getX() + 1.0f, this.getY() + 0.5f);
            }
            default: {
                throw new IllegalStateException();
            }
        }
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
    
    @Override
    public int getKeyId() {
        return this.keyId;
    }
    
    public void syncDoorKey() {
        final ByteBufferWriter startPacket = GameClient.connection.startPacket();
        PacketTypes.PacketType.SyncDoorKey.doPacket(startPacket);
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
        startPacket.putInt(this.getKeyId());
        PacketTypes.PacketType.SyncDoorKey.send(GameClient.connection);
    }
    
    @Override
    public void setKeyId(final int n) {
        if (this.keyId != n && GameClient.bClient) {
            this.keyId = n;
            this.syncDoorKey();
        }
        else {
            this.keyId = n;
        }
    }
    
    public boolean isLockedByKey() {
        return this.lockedByKey;
    }
    
    public void setLockedByKey(final boolean b) {
        final boolean b2 = b != this.lockedByKey;
        this.lockedByKey = b;
        this.Locked = b;
        if (!GameServer.bServer && b2) {
            if (b) {
                this.syncIsoObject(false, (byte)3, null, null);
            }
            else {
                this.syncIsoObject(false, (byte)4, null, null);
            }
        }
    }
    
    public boolean haveKey() {
        return this.haveKey;
    }
    
    public void setHaveKey(final boolean haveKey) {
        this.haveKey = haveKey;
        if (GameServer.bServer) {
            return;
        }
        if (haveKey) {
            this.syncIsoObject(false, (byte)(-1), null, null);
        }
        else {
            this.syncIsoObject(false, (byte)(-2), null, null);
        }
    }
    
    @Override
    public IsoGridSquare getOppositeSquare() {
        if (this.getNorth()) {
            return this.getCell().getGridSquare(this.getX(), this.getY() - 1.0f, this.getZ());
        }
        return this.getCell().getGridSquare(this.getX() - 1.0f, this.getY(), this.getZ());
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
    
    public int checkKeyId() {
        if (this.getKeyId() != -1) {
            return this.getKeyId();
        }
        final IsoGridSquare square = this.getSquare();
        final IsoGridSquare oppositeSquare = this.getOppositeSquare();
        if (square == null || oppositeSquare == null) {
            return -1;
        }
        final BuildingDef buildingDef = (square.getBuilding() == null) ? null : square.getBuilding().getDef();
        final BuildingDef buildingDef2 = (oppositeSquare.getBuilding() == null) ? null : oppositeSquare.getBuilding().getDef();
        if (buildingDef == null && buildingDef2 != null) {
            this.setKeyId(buildingDef2.getKeyId());
        }
        else if (buildingDef != null && buildingDef2 == null) {
            this.setKeyId(buildingDef.getKeyId());
        }
        else if (this.getProperties().Is("forceLocked") && buildingDef != null) {
            this.setKeyId(buildingDef.getKeyId());
        }
        if (this.Locked && !this.lockedByKey && this.getKeyId() != -1) {
            this.lockedByKey = true;
        }
        return this.getKeyId();
    }
    
    public void setHealth(final int health) {
        this.Health = health;
    }
    
    private void initCurtainSprites() {
        if (this.curtainN != null) {
            return;
        }
        (this.curtainW = IsoSprite.CreateSprite(IsoSpriteManager.instance)).LoadFramesNoDirPageSimple("fixtures_windows_curtains_01_16");
        this.curtainW.def.setScale(0.8f, 0.8f);
        (this.curtainWopen = IsoSprite.CreateSprite(IsoSpriteManager.instance)).LoadFramesNoDirPageSimple("fixtures_windows_curtains_01_20");
        this.curtainWopen.def.setScale(0.8f, 0.8f);
        (this.curtainE = IsoSprite.CreateSprite(IsoSpriteManager.instance)).LoadFramesNoDirPageSimple("fixtures_windows_curtains_01_17");
        this.curtainE.def.setScale(0.8f, 0.8f);
        (this.curtainEopen = IsoSprite.CreateSprite(IsoSpriteManager.instance)).LoadFramesNoDirPageSimple("fixtures_windows_curtains_01_21");
        this.curtainEopen.def.setScale(0.8f, 0.8f);
        (this.curtainN = IsoSprite.CreateSprite(IsoSpriteManager.instance)).LoadFramesNoDirPageSimple("fixtures_windows_curtains_01_18");
        this.curtainN.def.setScale(0.8f, 0.8f);
        (this.curtainNopen = IsoSprite.CreateSprite(IsoSpriteManager.instance)).LoadFramesNoDirPageSimple("fixtures_windows_curtains_01_22");
        this.curtainNopen.def.setScale(0.8f, 0.8f);
        (this.curtainS = IsoSprite.CreateSprite(IsoSpriteManager.instance)).LoadFramesNoDirPageSimple("fixtures_windows_curtains_01_19");
        this.curtainS.def.setScale(0.8f, 0.8f);
        (this.curtainSopen = IsoSprite.CreateSprite(IsoSpriteManager.instance)).LoadFramesNoDirPageSimple("fixtures_windows_curtains_01_23");
        this.curtainSopen.def.setScale(0.8f, 0.8f);
    }
    
    public IsoDoor HasCurtains() {
        return this.bHasCurtain ? this : null;
    }
    
    public boolean isCurtainOpen() {
        return this.bHasCurtain && this.bCurtainOpen;
    }
    
    public void setCurtainOpen(final boolean bCurtainOpen) {
        if (!this.bHasCurtain) {
            return;
        }
        this.bCurtainOpen = bCurtainOpen;
        if (!GameServer.bServer) {
            for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
                LosUtil.cachecleared[i] = true;
            }
            GameTime.instance.lightSourceUpdate = 100.0f;
            IsoGridSquare.setRecalcLightTime(-1);
            if (this.square != null) {
                this.square.RecalcProperties();
                this.square.RecalcAllWithNeighbours(true);
            }
        }
    }
    
    public void transmitSetCurtainOpen(final boolean b) {
        if (!this.bHasCurtain) {
            return;
        }
        if (GameServer.bServer) {
            this.sendObjectChange("setCurtainOpen", "open", b);
        }
        if (GameClient.bClient) {
            GameClient.instance.sendClientCommandV(null, "object", "openCloseCurtain", "x", this.getX(), "y", this.getY(), "z", this.getZ(), "index", this.getObjectIndex(), "open", !this.bCurtainOpen);
        }
    }
    
    public void toggleCurtain() {
        if (!this.bHasCurtain) {
            return;
        }
        if (GameClient.bClient) {
            this.transmitSetCurtainOpen(!this.isCurtainOpen());
        }
        else {
            this.setCurtainOpen(!this.isCurtainOpen());
            if (GameServer.bServer) {
                this.transmitSetCurtainOpen(this.isCurtainOpen());
            }
        }
    }
    
    public void addSheet(final IsoGameCharacter isoGameCharacter) {
        if (this.bHasCurtain || isoGameCharacter == null || isoGameCharacter.getCurrentSquare() == null) {
            return;
        }
        final IsoGridSquare currentSquare = isoGameCharacter.getCurrentSquare();
        final IsoGridSquare square = this.getSquare();
        boolean b = false;
        switch (this.getSpriteEdge(false)) {
            case N: {
                b = (this.north == currentSquare.getY() >= square.getY());
                break;
            }
            case S: {
                b = (currentSquare.getY() > square.getY());
                break;
            }
            case W: {
                b = (this.north == currentSquare.getX() < square.getX());
                break;
            }
            case E: {
                b = (currentSquare.getX() > square.getX());
                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }
        this.addSheet(b, isoGameCharacter);
    }
    
    public void addSheet(final boolean b, final IsoGameCharacter isoGameCharacter) {
        if (this.bHasCurtain) {
            return;
        }
        this.bHasCurtain = true;
        this.bCurtainInside = b;
        this.bCurtainOpen = true;
        if (GameServer.bServer) {
            this.sendObjectChange("addSheet", "inside", b);
            if (isoGameCharacter != null) {
                isoGameCharacter.sendObjectChange("removeOneOf", new Object[] { "type", "Sheet" });
            }
        }
        else if (isoGameCharacter != null) {
            isoGameCharacter.getInventory().RemoveOneOf("Sheet");
            for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
                LosUtil.cachecleared[i] = true;
            }
            GameTime.instance.lightSourceUpdate = 100.0f;
            IsoGridSquare.setRecalcLightTime(-1);
            if (this.square != null) {
                this.square.RecalcProperties();
            }
        }
    }
    
    public void removeSheet(final IsoGameCharacter isoGameCharacter) {
        if (!this.bHasCurtain) {
            return;
        }
        this.bHasCurtain = false;
        if (GameServer.bServer) {
            this.sendObjectChange("removeSheet");
            if (isoGameCharacter != null) {
                isoGameCharacter.sendObjectChange("addItemOfType", new Object[] { "type", "Base.Sheet" });
            }
        }
        else if (isoGameCharacter != null) {
            isoGameCharacter.getInventory().AddItem("Base.Sheet");
            for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
                LosUtil.cachecleared[i] = true;
            }
            GameTime.instance.lightSourceUpdate = 100.0f;
            IsoGridSquare.setRecalcLightTime(-1);
            if (this.square != null) {
                this.square.RecalcProperties();
            }
        }
    }
    
    public IsoGridSquare getAddSheetSquare(final IsoGameCharacter isoGameCharacter) {
        if (isoGameCharacter == null || isoGameCharacter.getCurrentSquare() == null) {
            return null;
        }
        final IsoGridSquare currentSquare = isoGameCharacter.getCurrentSquare();
        final IsoGridSquare square = this.getSquare();
        switch (this.getSpriteEdge(false)) {
            case N: {
                return (currentSquare.getY() >= square.getY()) ? square : this.getCell().getGridSquare(square.x, square.y - 1, square.z);
            }
            case S: {
                return (currentSquare.getY() <= square.getY()) ? square : this.getCell().getGridSquare(square.x, square.y + 1, square.z);
            }
            case W: {
                return (currentSquare.getX() >= square.getX()) ? square : this.getCell().getGridSquare(square.x - 1, square.y, square.z);
            }
            case E: {
                return (currentSquare.getX() <= square.getX()) ? square : this.getCell().getGridSquare(square.x + 1, square.y, square.z);
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    public IsoGridSquare getSheetSquare() {
        if (!this.bHasCurtain) {
            return null;
        }
        switch (this.getSpriteEdge(false)) {
            case N: {
                if (this.open) {
                    return this.bCurtainInside ? this.getCell().getGridSquare(this.getX(), this.getY() - 1.0f, this.getZ()) : this.getSquare();
                }
                return this.bCurtainInside ? this.getSquare() : this.getCell().getGridSquare(this.getX(), this.getY() - 1.0f, this.getZ());
            }
            case S: {
                return this.bCurtainInside ? this.getCell().getGridSquare(this.getX(), this.getY() + 1.0f, this.getZ()) : this.getSquare();
            }
            case W: {
                if (this.open) {
                    return this.bCurtainInside ? this.getCell().getGridSquare(this.getX() - 1.0f, this.getY(), this.getZ()) : this.getSquare();
                }
                return this.bCurtainInside ? this.getSquare() : this.getCell().getGridSquare(this.getX() - 1.0f, this.getY(), this.getZ());
            }
            case E: {
                return this.bCurtainInside ? this.getCell().getGridSquare(this.getX() + 1.0f, this.getY(), this.getZ()) : this.getSquare();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    public int getHealth() {
        return this.Health;
    }
    
    public int getMaxHealth() {
        return this.MaxHealth;
    }
    
    public boolean isFacingSheet(final IsoGameCharacter isoGameCharacter) {
        if (!this.bHasCurtain || isoGameCharacter == null || isoGameCharacter.getCurrentSquare() != this.getSheetSquare()) {
            return false;
        }
        IsoDirections isoDirections;
        if (this.bCurtainInside) {
            if (this.open) {
                if (this.north) {
                    isoDirections = IsoDirections.E;
                }
                else {
                    isoDirections = IsoDirections.S;
                }
            }
            else if (this.north) {
                isoDirections = IsoDirections.N;
            }
            else {
                isoDirections = IsoDirections.W;
            }
        }
        else if (this.open) {
            if (this.north) {
                isoDirections = IsoDirections.W;
            }
            else {
                isoDirections = IsoDirections.N;
            }
        }
        else if (this.north) {
            isoDirections = IsoDirections.S;
        }
        else {
            isoDirections = IsoDirections.E;
        }
        final IsoDirections spriteEdge = this.getSpriteEdge(false);
        if (spriteEdge == IsoDirections.E) {
            isoDirections = (this.bCurtainInside ? IsoDirections.W : IsoDirections.E);
        }
        if (spriteEdge == IsoDirections.S) {
            isoDirections = (this.bCurtainInside ? IsoDirections.N : IsoDirections.S);
        }
        return isoGameCharacter.getDir() == isoDirections || isoGameCharacter.getDir() == IsoDirections.RotLeft(isoDirections) || isoGameCharacter.getDir() == IsoDirections.RotRight(isoDirections);
    }
    
    @Override
    public void saveChange(final String anObject, final KahluaTable kahluaTable, final ByteBuffer byteBuffer) {
        if ("addSheet".equals(anObject)) {
            if (kahluaTable != null && kahluaTable.rawget((Object)"inside") instanceof Boolean) {
                byteBuffer.put((byte)(((boolean)kahluaTable.rawget((Object)"inside")) ? 1 : 0));
            }
        }
        else if (!"removeSheet".equals(anObject)) {
            if ("setCurtainOpen".equals(anObject)) {
                if (kahluaTable != null && kahluaTable.rawget((Object)"open") instanceof Boolean) {
                    byteBuffer.put((byte)(((boolean)kahluaTable.rawget((Object)"open")) ? 1 : 0));
                }
            }
            else {
                super.saveChange(anObject, kahluaTable, byteBuffer);
            }
        }
    }
    
    @Override
    public void loadChange(final String anObject, final ByteBuffer byteBuffer) {
        if ("addSheet".equals(anObject)) {
            this.addSheet(byteBuffer.get() == 1, null);
        }
        else if ("removeSheet".equals(anObject)) {
            this.removeSheet(null);
        }
        else if ("setCurtainOpen".equals(anObject)) {
            this.setCurtainOpen(byteBuffer.get() == 1);
        }
        else {
            super.loadChange(anObject, byteBuffer);
        }
    }
    
    public void addRandomBarricades() {
        final IsoGridSquare isoGridSquare = (this.square.getRoom() == null) ? this.square : this.getOppositeSquare();
        if (isoGridSquare != null && isoGridSquare.getRoom() == null) {
            final IsoBarricade addBarricadeToObject = IsoBarricade.AddBarricadeToObject(this, isoGridSquare != this.square);
            if (addBarricadeToObject != null) {
                for (int next = Rand.Next(1, 4), i = 0; i < next; ++i) {
                    addBarricadeToObject.addPlank(null, null);
                }
            }
        }
    }
    
    public boolean isObstructed() {
        return isDoorObstructed(this);
    }
    
    public static boolean isDoorObstructed(final IsoObject isoObject) {
        final IsoDoor isoDoor = (isoObject instanceof IsoDoor) ? ((IsoDoor)isoObject) : null;
        final IsoThumpable isoThumpable = (isoObject instanceof IsoThumpable) ? ((IsoThumpable)isoObject) : null;
        if (isoDoor == null && isoThumpable == null) {
            return false;
        }
        final IsoGridSquare square = isoObject.getSquare();
        if (square == null) {
            return false;
        }
        if (square.isSolid() || square.isSolidTrans() || square.Has(IsoObjectType.tree)) {
            return true;
        }
        final int n = (square.x - 1) / 10;
        final int n2 = (square.y - 1) / 10;
        final int n3 = (int)Math.ceil((square.x + 1.0f) / 10.0f);
        for (int n4 = (int)Math.ceil((square.y + 1.0f) / 10.0f), i = n2; i <= n4; ++i) {
            for (int j = n; j <= n3; ++j) {
                final IsoChunk isoChunk = GameServer.bServer ? ServerMap.instance.getChunk(j, i) : IsoWorld.instance.CurrentCell.getChunk(j, i);
                if (isoChunk != null) {
                    for (int k = 0; k < isoChunk.vehicles.size(); ++k) {
                        if (isoChunk.vehicles.get(k).isIntersectingSquareWithShadow(square.x, square.y, square.z)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public static void toggleDoubleDoor(final IsoObject isoObject, final boolean b) {
        if (getDoubleDoorIndex(isoObject) == -1) {
            return;
        }
        final IsoDoor isoDoor = (isoObject instanceof IsoDoor) ? ((IsoDoor)isoObject) : null;
        final IsoThumpable isoThumpable = (isoObject instanceof IsoThumpable) ? ((IsoThumpable)isoObject) : null;
        final boolean b2 = (isoDoor == null) ? isoThumpable.north : isoDoor.north;
        final boolean b3 = (isoDoor == null) ? isoThumpable.open : isoDoor.open;
        if (b && isoThumpable != null) {
            isoThumpable.syncIsoObject(false, (byte)(isoThumpable.open ? 1 : 0), null, null);
        }
        final IsoObject doubleDoorObject = getDoubleDoorObject(isoObject, 1);
        final IsoObject doubleDoorObject2 = getDoubleDoorObject(isoObject, 2);
        final IsoObject doubleDoorObject3 = getDoubleDoorObject(isoObject, 3);
        final IsoObject doubleDoorObject4 = getDoubleDoorObject(isoObject, 4);
        if (doubleDoorObject != null) {
            toggleDoubleDoorObject(doubleDoorObject);
        }
        if (doubleDoorObject2 != null) {
            toggleDoubleDoorObject(doubleDoorObject2);
        }
        if (doubleDoorObject3 != null) {
            toggleDoubleDoorObject(doubleDoorObject3);
        }
        if (doubleDoorObject4 != null) {
            toggleDoubleDoorObject(doubleDoorObject4);
        }
        LuaEventManager.triggerEvent("OnContainerUpdate");
    }
    
    private static void toggleDoubleDoorObject(final IsoObject isoObject) {
        final int doubleDoorIndex = getDoubleDoorIndex(isoObject);
        if (doubleDoorIndex == -1) {
            return;
        }
        final IsoDoor isoDoor = (isoObject instanceof IsoDoor) ? ((IsoDoor)isoObject) : null;
        final IsoThumpable isoThumpable = (isoObject instanceof IsoThumpable) ? ((IsoThumpable)isoObject) : null;
        final boolean b = (isoDoor == null) ? isoThumpable.north : isoDoor.north;
        final boolean b2 = (isoDoor == null) ? isoThumpable.open : isoDoor.open;
        int n = -1;
        if (isoDoor != null) {
            isoDoor.open = !b2;
            isoDoor.setLockedByKey(false);
            n = isoDoor.checkKeyId();
        }
        if (isoThumpable != null) {
            isoThumpable.open = !b2;
            isoThumpable.setLockedByKey(false);
            n = isoThumpable.getKeyId();
        }
        final IsoSprite sprite = isoObject.getSprite();
        int n2 = b ? IsoDoor.DoubleDoorNorthSpriteOffset[doubleDoorIndex - 1] : IsoDoor.DoubleDoorWestSpriteOffset[doubleDoorIndex - 1];
        if (b2) {
            n2 *= -1;
        }
        isoObject.sprite = IsoSprite.getSprite(IsoSpriteManager.instance, sprite.getName(), n2);
        isoObject.getSquare().RecalcAllWithNeighbours(true);
        if (doubleDoorIndex == 2 || doubleDoorIndex == 3) {
            final IsoGridSquare square = isoObject.getSquare();
            int[] array;
            int[] array2;
            int[] array3;
            int[] array4;
            if (b) {
                if (b2) {
                    array = IsoDoor.DoubleDoorNorthOpenXOffset;
                    array2 = IsoDoor.DoubleDoorNorthOpenYOffset;
                    array3 = IsoDoor.DoubleDoorNorthClosedXOffset;
                    array4 = IsoDoor.DoubleDoorNorthClosedYOffset;
                }
                else {
                    array = IsoDoor.DoubleDoorNorthClosedXOffset;
                    array2 = IsoDoor.DoubleDoorNorthClosedYOffset;
                    array3 = IsoDoor.DoubleDoorNorthOpenXOffset;
                    array4 = IsoDoor.DoubleDoorNorthOpenYOffset;
                }
            }
            else if (b2) {
                array = IsoDoor.DoubleDoorWestOpenXOffset;
                array2 = IsoDoor.DoubleDoorWestOpenYOffset;
                array3 = IsoDoor.DoubleDoorWestClosedXOffset;
                array4 = IsoDoor.DoubleDoorWestClosedYOffset;
            }
            else {
                array = IsoDoor.DoubleDoorWestClosedXOffset;
                array2 = IsoDoor.DoubleDoorWestClosedYOffset;
                array3 = IsoDoor.DoubleDoorWestOpenXOffset;
                array4 = IsoDoor.DoubleDoorWestOpenYOffset;
            }
            final int n3 = square.getX() - array[doubleDoorIndex - 1];
            final int n4 = square.getY() - array2[doubleDoorIndex - 1];
            final int n5 = n3 + array3[doubleDoorIndex - 1];
            final int n6 = n4 + array4[doubleDoorIndex - 1];
            square.RemoveTileObject(isoObject);
            PolygonalMap2.instance.squareChanged(square);
            final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n5, n6, square.getZ());
            if (gridSquare == null) {
                return;
            }
            if (isoThumpable != null) {
                final IsoThumpable isoThumpable2 = new IsoThumpable(gridSquare.getCell(), gridSquare, isoObject.getSprite().getName(), b, isoThumpable.getTable());
                isoThumpable2.setModData(isoThumpable.getModData());
                isoThumpable2.setCanBeLockByPadlock(isoThumpable.canBeLockByPadlock());
                isoThumpable2.setCanBePlastered(isoThumpable.canBePlastered());
                isoThumpable2.setIsHoppable(isoThumpable.isHoppable());
                isoThumpable2.setIsDismantable(isoThumpable.isDismantable());
                isoThumpable2.setName(isoThumpable.getName());
                isoThumpable2.setIsDoor(true);
                isoThumpable2.setIsThumpable(isoThumpable.isThumpable());
                isoThumpable2.setThumpDmg(isoThumpable.getThumpDmg());
                isoThumpable2.setThumpSound(isoThumpable.getThumpSound());
                isoThumpable2.open = !b2;
                isoThumpable2.keyId = n;
                gridSquare.AddSpecialObject(isoThumpable2);
            }
            else {
                final IsoDoor e = new IsoDoor(gridSquare.getCell(), gridSquare, isoObject.getSprite().getName(), b);
                e.open = !b2;
                e.keyId = n;
                gridSquare.getObjects().add(e);
                gridSquare.getSpecialObjects().add(e);
                gridSquare.RecalcProperties();
            }
            if (!GameClient.bClient) {
                gridSquare.restackSheetRope();
            }
            PolygonalMap2.instance.squareChanged(gridSquare);
        }
        else {
            PolygonalMap2.instance.squareChanged(isoObject.getSquare());
        }
    }
    
    public static int getDoubleDoorIndex(final IsoObject isoObject) {
        if (isoObject == null || isoObject.getSquare() == null) {
            return -1;
        }
        final PropertyContainer properties = isoObject.getProperties();
        if (properties == null || !properties.Is("DoubleDoor")) {
            return -1;
        }
        final int int1 = Integer.parseInt(properties.Val("DoubleDoor"));
        if (int1 < 1 || int1 > 8) {
            return -1;
        }
        final IsoDoor isoDoor = (isoObject instanceof IsoDoor) ? ((IsoDoor)isoObject) : null;
        final IsoThumpable isoThumpable = (isoObject instanceof IsoThumpable) ? ((IsoThumpable)isoObject) : null;
        if (isoDoor == null && isoThumpable == null) {
            return -1;
        }
        if (!((isoDoor == null) ? isoThumpable.open : isoDoor.open)) {
            return int1;
        }
        if (int1 >= 5) {
            return int1 - 4;
        }
        return -1;
    }
    
    public static IsoObject getDoubleDoorObject(final IsoObject isoObject, final int n) {
        final int doubleDoorIndex = getDoubleDoorIndex(isoObject);
        if (doubleDoorIndex == -1) {
            return null;
        }
        final IsoDoor isoDoor = (isoObject instanceof IsoDoor) ? ((IsoDoor)isoObject) : null;
        final IsoThumpable isoThumpable = (isoObject instanceof IsoThumpable) ? ((IsoThumpable)isoObject) : null;
        final boolean b = (isoDoor == null) ? isoThumpable.north : isoDoor.north;
        final boolean b2 = (isoDoor == null) ? isoThumpable.open : isoDoor.open;
        final IsoGridSquare square = isoObject.getSquare();
        int[] array;
        int[] array2;
        if (b) {
            if (b2) {
                array = IsoDoor.DoubleDoorNorthOpenXOffset;
                array2 = IsoDoor.DoubleDoorNorthOpenYOffset;
            }
            else {
                array = IsoDoor.DoubleDoorNorthClosedXOffset;
                array2 = IsoDoor.DoubleDoorNorthClosedYOffset;
            }
        }
        else if (b2) {
            array = IsoDoor.DoubleDoorWestOpenXOffset;
            array2 = IsoDoor.DoubleDoorWestOpenYOffset;
        }
        else {
            array = IsoDoor.DoubleDoorWestClosedXOffset;
            array2 = IsoDoor.DoubleDoorWestClosedYOffset;
        }
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(square.getX() - array[doubleDoorIndex - 1] + array[n - 1], square.getY() - array2[doubleDoorIndex - 1] + array2[n - 1], square.getZ());
        if (gridSquare == null) {
            return null;
        }
        final ArrayList<IsoObject> specialObjects = gridSquare.getSpecialObjects();
        if (isoDoor != null) {
            for (int i = 0; i < specialObjects.size(); ++i) {
                final IsoObject isoObject2 = specialObjects.get(i);
                if (isoObject2 instanceof IsoDoor && ((IsoDoor)isoObject2).north == b && getDoubleDoorIndex(isoObject2) == n) {
                    return isoObject2;
                }
            }
        }
        if (isoThumpable != null) {
            for (int j = 0; j < specialObjects.size(); ++j) {
                final IsoObject isoObject3 = specialObjects.get(j);
                if (isoObject3 instanceof IsoThumpable && ((IsoThumpable)isoObject3).north == b && getDoubleDoorIndex(isoObject3) == n) {
                    return isoObject3;
                }
            }
        }
        return null;
    }
    
    public static boolean isDoubleDoorObstructed(final IsoObject isoObject) {
        final int doubleDoorIndex = getDoubleDoorIndex(isoObject);
        if (doubleDoorIndex == -1) {
            return false;
        }
        final IsoDoor isoDoor = (isoObject instanceof IsoDoor) ? ((IsoDoor)isoObject) : null;
        final IsoThumpable isoThumpable = (isoObject instanceof IsoThumpable) ? ((IsoThumpable)isoObject) : null;
        final boolean b = (isoDoor == null) ? isoThumpable.north : isoDoor.north;
        final boolean b2 = (isoDoor == null) ? isoThumpable.open : isoDoor.open;
        final IsoGridSquare square = isoObject.getSquare();
        int[] array;
        int[] array2;
        if (b) {
            if (b2) {
                array = IsoDoor.DoubleDoorNorthOpenXOffset;
                array2 = IsoDoor.DoubleDoorNorthOpenYOffset;
            }
            else {
                array = IsoDoor.DoubleDoorNorthClosedXOffset;
                array2 = IsoDoor.DoubleDoorNorthClosedYOffset;
            }
        }
        else if (b2) {
            array = IsoDoor.DoubleDoorWestOpenXOffset;
            array2 = IsoDoor.DoubleDoorWestOpenYOffset;
        }
        else {
            array = IsoDoor.DoubleDoorWestClosedXOffset;
            array2 = IsoDoor.DoubleDoorWestClosedYOffset;
        }
        final int n = square.getX() - array[doubleDoorIndex - 1];
        final int n2 = square.getY() - array2[doubleDoorIndex - 1];
        final int n3 = n;
        final int n4 = n2 + (b ? 0 : -3);
        final int n5 = n3 + (b ? 4 : 2);
        final int n6 = n4 + (b ? 2 : 4);
        final int z = square.getZ();
        for (int i = n4; i < n6; ++i) {
            for (int j = n3; j < n5; ++j) {
                final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(j, i, z);
                if (gridSquare != null) {
                    if (gridSquare.isSolid() || gridSquare.isSolidTrans() || gridSquare.Has(IsoObjectType.tree)) {
                        return true;
                    }
                }
            }
        }
        final int n7 = (n3 - 4) / 10;
        final int n8 = (n4 - 4) / 10;
        final int n9 = (int)Math.ceil((n5 + 4) / 10);
        for (int n10 = (int)Math.ceil((n6 + 4) / 10), k = n8; k <= n10; ++k) {
            for (int l = n7; l <= n9; ++l) {
                final IsoChunk isoChunk = GameServer.bServer ? ServerMap.instance.getChunk(l, k) : IsoWorld.instance.CurrentCell.getChunk(l, k);
                if (isoChunk != null) {
                    for (int index = 0; index < isoChunk.vehicles.size(); ++index) {
                        final BaseVehicle baseVehicle = isoChunk.vehicles.get(index);
                        for (int n11 = n4; n11 < n6; ++n11) {
                            for (int n12 = n3; n12 < n5; ++n12) {
                                if (baseVehicle.isIntersectingSquare(n12, n11, z)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public static boolean destroyDoubleDoor(final IsoObject isoObject) {
        final int doubleDoorIndex = getDoubleDoorIndex(isoObject);
        if (doubleDoorIndex == -1) {
            return false;
        }
        if (doubleDoorIndex == 1 || doubleDoorIndex == 4) {
            final IsoObject doubleDoorObject = getDoubleDoorObject(isoObject, (doubleDoorIndex == 1) ? 2 : 3);
            if (doubleDoorObject instanceof IsoDoor) {
                ((IsoDoor)doubleDoorObject).destroy();
            }
            else if (doubleDoorObject instanceof IsoThumpable) {
                ((IsoThumpable)doubleDoorObject).destroy();
            }
        }
        if (isoObject instanceof IsoDoor) {
            ((IsoDoor)isoObject).destroy();
        }
        else if (isoObject instanceof IsoThumpable) {
            ((IsoThumpable)isoObject).destroy();
        }
        LuaEventManager.triggerEvent("OnContainerUpdate");
        return true;
    }
    
    public static int getGarageDoorIndex(final IsoObject isoObject) {
        if (isoObject == null || isoObject.getSquare() == null) {
            return -1;
        }
        final PropertyContainer properties = isoObject.getProperties();
        if (properties == null || !properties.Is("GarageDoor")) {
            return -1;
        }
        final int int1 = Integer.parseInt(properties.Val("GarageDoor"));
        if (int1 < 1 || int1 > 6) {
            return -1;
        }
        final IsoDoor isoDoor = (isoObject instanceof IsoDoor) ? ((IsoDoor)isoObject) : null;
        final IsoThumpable isoThumpable = (isoObject instanceof IsoThumpable) ? ((IsoThumpable)isoObject) : null;
        if (isoDoor == null && isoThumpable == null) {
            return -1;
        }
        if (!((isoDoor == null) ? isoThumpable.open : isoDoor.open)) {
            return int1;
        }
        if (int1 >= 4) {
            return int1 - 3;
        }
        return -1;
    }
    
    public static IsoObject getGarageDoorPrev(final IsoObject isoObject) {
        final int garageDoorIndex = getGarageDoorIndex(isoObject);
        if (garageDoorIndex == -1) {
            return null;
        }
        if (garageDoorIndex == 1) {
            return null;
        }
        final IsoDoor isoDoor = (isoObject instanceof IsoDoor) ? ((IsoDoor)isoObject) : null;
        final IsoThumpable isoThumpable = (isoObject instanceof IsoThumpable) ? ((IsoThumpable)isoObject) : null;
        final int n = ((isoDoor == null) ? isoThumpable.north : isoDoor.north) ? 1 : 0;
        final IsoGridSquare square = isoObject.getSquare();
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(square.x - n, square.y + ((n != 0) ? 0 : 1), square.getZ());
        if (gridSquare == null) {
            return null;
        }
        final ArrayList<IsoObject> specialObjects = gridSquare.getSpecialObjects();
        if (isoDoor != null) {
            for (int i = 0; i < specialObjects.size(); ++i) {
                final IsoObject isoObject2 = specialObjects.get(i);
                if (isoObject2 instanceof IsoDoor && (((IsoDoor)isoObject2).north ? 1 : 0) == n && getGarageDoorIndex(isoObject2) <= garageDoorIndex) {
                    return isoObject2;
                }
            }
        }
        if (isoThumpable != null) {
            for (int j = 0; j < specialObjects.size(); ++j) {
                final IsoObject isoObject3 = specialObjects.get(j);
                if (isoObject3 instanceof IsoThumpable && (((IsoThumpable)isoObject3).north ? 1 : 0) == n && getGarageDoorIndex(isoObject3) <= garageDoorIndex) {
                    return isoObject3;
                }
            }
        }
        return null;
    }
    
    public static IsoObject getGarageDoorNext(final IsoObject isoObject) {
        final int garageDoorIndex = getGarageDoorIndex(isoObject);
        if (garageDoorIndex == -1) {
            return null;
        }
        if (garageDoorIndex == 3) {
            return null;
        }
        final IsoDoor isoDoor = (isoObject instanceof IsoDoor) ? ((IsoDoor)isoObject) : null;
        final IsoThumpable isoThumpable = (isoObject instanceof IsoThumpable) ? ((IsoThumpable)isoObject) : null;
        final int n = ((isoDoor == null) ? isoThumpable.north : isoDoor.north) ? 1 : 0;
        final IsoGridSquare square = isoObject.getSquare();
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(square.x + n, square.y - ((n != 0) ? 0 : 1), square.getZ());
        if (gridSquare == null) {
            return null;
        }
        final ArrayList<IsoObject> specialObjects = gridSquare.getSpecialObjects();
        if (isoDoor != null) {
            for (int i = 0; i < specialObjects.size(); ++i) {
                final IsoObject isoObject2 = specialObjects.get(i);
                if (isoObject2 instanceof IsoDoor && (((IsoDoor)isoObject2).north ? 1 : 0) == n && getGarageDoorIndex(isoObject2) >= garageDoorIndex) {
                    return isoObject2;
                }
            }
        }
        if (isoThumpable != null) {
            for (int j = 0; j < specialObjects.size(); ++j) {
                final IsoObject isoObject3 = specialObjects.get(j);
                if (isoObject3 instanceof IsoThumpable && (((IsoThumpable)isoObject3).north ? 1 : 0) == n && getGarageDoorIndex(isoObject3) >= garageDoorIndex) {
                    return isoObject3;
                }
            }
        }
        return null;
    }
    
    public static IsoObject getGarageDoorFirst(final IsoObject isoObject) {
        final int garageDoorIndex = getGarageDoorIndex(isoObject);
        if (garageDoorIndex == -1) {
            return null;
        }
        if (garageDoorIndex == 1) {
            return isoObject;
        }
        for (IsoObject isoObject2 = getGarageDoorPrev(isoObject); isoObject2 != null; isoObject2 = getGarageDoorPrev(isoObject2)) {
            if (getGarageDoorIndex(isoObject2) == 1) {
                return isoObject2;
            }
        }
        return isoObject;
    }
    
    private static void toggleGarageDoorObject(final IsoObject isoObject) {
        if (getGarageDoorIndex(isoObject) == -1) {
            return;
        }
        final IsoDoor isoDoor = (isoObject instanceof IsoDoor) ? ((IsoDoor)isoObject) : null;
        final IsoThumpable isoThumpable = (isoObject instanceof IsoThumpable) ? ((IsoThumpable)isoObject) : null;
        final boolean b = (isoDoor == null) ? isoThumpable.open : isoDoor.open;
        if (isoDoor != null) {
            isoDoor.open = !b;
            isoDoor.setLockedByKey(false);
            isoDoor.sprite = (isoDoor.open ? isoDoor.openSprite : isoDoor.closedSprite);
        }
        if (isoThumpable != null) {
            isoThumpable.open = !b;
            isoThumpable.setLockedByKey(false);
            isoThumpable.sprite = (isoThumpable.open ? isoThumpable.openSprite : isoThumpable.closedSprite);
        }
        isoObject.getSquare().RecalcAllWithNeighbours(true);
        isoObject.syncIsoObject(false, (byte)(b ? 0 : 1), null, null);
        PolygonalMap2.instance.squareChanged(isoObject.getSquare());
    }
    
    public static void toggleGarageDoor(final IsoObject isoObject, final boolean b) {
        if (getGarageDoorIndex(isoObject) == -1) {
            return;
        }
        final IsoThumpable isoThumpable = (isoObject instanceof IsoThumpable) ? ((IsoThumpable)isoObject) : null;
        if (b && isoThumpable != null) {
            isoThumpable.syncIsoObject(false, (byte)(isoThumpable.open ? 1 : 0), null, null);
        }
        toggleGarageDoorObject(isoObject);
        for (IsoObject isoObject2 = getGarageDoorPrev(isoObject); isoObject2 != null; isoObject2 = getGarageDoorPrev(isoObject2)) {
            toggleGarageDoorObject(isoObject2);
        }
        for (IsoObject isoObject3 = getGarageDoorNext(isoObject); isoObject3 != null; isoObject3 = getGarageDoorNext(isoObject3)) {
            toggleGarageDoorObject(isoObject3);
        }
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            LosUtil.cachecleared[i] = true;
        }
        IsoGridSquare.setRecalcLightTime(-1);
        LuaEventManager.triggerEvent("OnContainerUpdate");
    }
    
    private static boolean isGarageDoorObstructed(final IsoObject isoObject) {
        if (getGarageDoorIndex(isoObject) == -1) {
            return false;
        }
        final IsoDoor isoDoor = (isoObject instanceof IsoDoor) ? ((IsoDoor)isoObject) : null;
        final IsoThumpable isoThumpable = (isoObject instanceof IsoThumpable) ? ((IsoThumpable)isoObject) : null;
        final int n = ((isoDoor == null) ? isoThumpable.north : isoDoor.north) ? 1 : 0;
        if (!((isoDoor == null) ? isoThumpable.open : isoDoor.open)) {
            return false;
        }
        int x = isoObject.square.x;
        int y = isoObject.square.y;
        int n2 = x;
        int n3 = y;
        if (n != 0) {
            for (IsoObject isoObject2 = getGarageDoorPrev(isoObject); isoObject2 != null; isoObject2 = getGarageDoorPrev(isoObject2)) {
                --x;
            }
            for (IsoObject isoObject3 = getGarageDoorNext(isoObject); isoObject3 != null; isoObject3 = getGarageDoorNext(isoObject3)) {
                ++n2;
            }
        }
        else {
            for (IsoObject isoObject4 = getGarageDoorPrev(isoObject); isoObject4 != null; isoObject4 = getGarageDoorPrev(isoObject4)) {
                ++n3;
            }
            for (IsoObject isoObject5 = getGarageDoorNext(isoObject); isoObject5 != null; isoObject5 = getGarageDoorNext(isoObject5)) {
                --y;
            }
        }
        final int n4 = (x - 4) / 10;
        final int n5 = (y - 4) / 10;
        final int n6 = (int)Math.ceil((n2 + 4) / 10);
        final int n7 = (int)Math.ceil((n3 + 4) / 10);
        final int z = isoObject.square.z;
        for (int i = n5; i <= n7; ++i) {
            for (int j = n4; j <= n6; ++j) {
                final IsoChunk isoChunk = GameServer.bServer ? ServerMap.instance.getChunk(j, i) : IsoWorld.instance.CurrentCell.getChunk(j, i);
                if (isoChunk != null) {
                    for (int k = 0; k < isoChunk.vehicles.size(); ++k) {
                        final BaseVehicle baseVehicle = isoChunk.vehicles.get(k);
                        for (int l = y; l <= n3; ++l) {
                            for (int n8 = x; n8 <= n2; ++n8) {
                                if (baseVehicle.isIntersectingSquare(n8, l, z) && baseVehicle.isIntersectingSquare(n8 - ((n != 0) ? 0 : 1), l - n, z)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public static boolean destroyGarageDoor(final IsoObject isoObject) {
        if (getGarageDoorIndex(isoObject) == -1) {
            return false;
        }
        IsoObject garageDoorPrev2;
        for (IsoObject garageDoorPrev = getGarageDoorPrev(isoObject); garageDoorPrev != null; garageDoorPrev = garageDoorPrev2) {
            garageDoorPrev2 = getGarageDoorPrev(garageDoorPrev);
            if (garageDoorPrev instanceof IsoDoor) {
                ((IsoDoor)garageDoorPrev).destroy();
            }
            else if (garageDoorPrev instanceof IsoThumpable) {
                ((IsoThumpable)garageDoorPrev).destroy();
            }
        }
        IsoObject garageDoorNext2;
        for (IsoObject garageDoorNext = getGarageDoorNext(isoObject); garageDoorNext != null; garageDoorNext = garageDoorNext2) {
            garageDoorNext2 = getGarageDoorNext(garageDoorNext);
            if (garageDoorNext instanceof IsoDoor) {
                ((IsoDoor)garageDoorNext).destroy();
            }
            else if (garageDoorNext instanceof IsoThumpable) {
                ((IsoThumpable)garageDoorNext).destroy();
            }
        }
        if (isoObject instanceof IsoDoor) {
            ((IsoDoor)isoObject).destroy();
        }
        else if (isoObject instanceof IsoThumpable) {
            ((IsoThumpable)isoObject).destroy();
        }
        LuaEventManager.triggerEvent("OnContainerUpdate");
        return true;
    }
    
    @Override
    public IsoObject getRenderEffectMaster() {
        final int doubleDoorIndex = getDoubleDoorIndex(this);
        if (doubleDoorIndex != -1) {
            IsoObject isoObject = null;
            if (doubleDoorIndex == 2) {
                isoObject = getDoubleDoorObject(this, 1);
            }
            else if (doubleDoorIndex == 3) {
                isoObject = getDoubleDoorObject(this, 4);
            }
            if (isoObject != null) {
                return isoObject;
            }
        }
        else {
            final IsoObject garageDoorFirst = getGarageDoorFirst(this);
            if (garageDoorFirst != null) {
                return garageDoorFirst;
            }
        }
        return this;
    }
    
    public String getThumpSound() {
        final String soundPrefix = this.getSoundPrefix();
        switch (soundPrefix) {
            case "GarageDoor":
            case "MetalDoor":
            case "MetalGate":
            case "PrisonMetalDoor": {
                return "ZombieThumpMetal";
            }
            case "SlidingGlassDoor": {
                return "ZombieThumpWindow";
            }
            default: {
                return "ZombieThumpGeneric";
            }
        }
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
        DoubleDoorNorthSpriteOffset = new int[] { 5, 3, 4, 4 };
        DoubleDoorWestSpriteOffset = new int[] { 4, 4, 5, 3 };
        DoubleDoorNorthClosedXOffset = new int[] { 0, 1, 2, 3 };
        DoubleDoorNorthOpenXOffset = new int[] { 0, 0, 3, 3 };
        DoubleDoorNorthClosedYOffset = new int[] { 0, 0, 0, 0 };
        DoubleDoorNorthOpenYOffset = new int[] { 0, 1, 1, 0 };
        DoubleDoorWestClosedXOffset = new int[] { 0, 0, 0, 0 };
        DoubleDoorWestOpenXOffset = new int[] { 0, 1, 1, 0 };
        DoubleDoorWestClosedYOffset = new int[] { 0, -1, -2, -3 };
        DoubleDoorWestOpenYOffset = new int[] { 0, 0, -3, -3 };
    }
    
    public enum DoorType
    {
        WeakWooden, 
        StrongWooden;
        
        private static /* synthetic */ DoorType[] $values() {
            return new DoorType[] { DoorType.WeakWooden, DoorType.StrongWooden };
        }
        
        static {
            $VALUES = $values();
        }
    }
}

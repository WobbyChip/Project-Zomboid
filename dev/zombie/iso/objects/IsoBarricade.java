// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.objects;

import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.IsoWorld;
import zombie.iso.IsoCamera;
import zombie.core.opengl.Shader;
import zombie.core.textures.ColorInfo;
import java.util.ArrayList;
import zombie.iso.objects.interfaces.BarricadeAble;
import se.krka.kahlua.vm.KahluaTable;
import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.core.math.PZMath;
import zombie.core.Core;
import zombie.SoundManager;
import zombie.audio.parameters.ParameterMeleeHitSurface;
import zombie.network.GameClient;
import zombie.util.Type;
import zombie.inventory.types.HandWeapon;
import zombie.iso.Vector2;
import zombie.WorldSoundManager;
import zombie.ai.states.ThumpState;
import zombie.characters.IsoZombie;
import zombie.iso.IsoMovingObject;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.inventory.InventoryItemFactory;
import zombie.GameTime;
import zombie.iso.LosUtil;
import zombie.characters.IsoPlayer;
import zombie.network.GameServer;
import zombie.inventory.InventoryItem;
import zombie.characters.IsoGameCharacter;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoCell;
import zombie.iso.objects.interfaces.Thumpable;
import zombie.iso.IsoObject;

public class IsoBarricade extends IsoObject implements Thumpable
{
    public static final int MAX_PLANKS = 4;
    public static final int PLANK_HEALTH = 1000;
    private int[] plankHealth;
    public static final int METAL_HEALTH = 5000;
    public static final int METAL_HEALTH_DAMAGED = 2500;
    private int metalHealth;
    public static final int METAL_BAR_HEALTH = 3000;
    private int metalBarHealth;
    
    public IsoBarricade(final IsoCell isoCell) {
        super(isoCell);
        this.plankHealth = new int[4];
    }
    
    public IsoBarricade(final IsoCell isoCell, final IsoGridSquare square, final IsoDirections dir) {
        this.plankHealth = new int[4];
        this.square = square;
        this.dir = dir;
    }
    
    @Override
    public String getObjectName() {
        return "Barricade";
    }
    
    public void addPlank(final IsoGameCharacter isoGameCharacter, final InventoryItem inventoryItem) {
        if (!this.canAddPlank()) {
            return;
        }
        int n = 1000;
        if (inventoryItem != null) {
            n = (int)(inventoryItem.getCondition() / (float)inventoryItem.getConditionMax() * 1000.0f);
        }
        if (isoGameCharacter != null) {
            n *= (int)isoGameCharacter.getBarricadeStrengthMod();
        }
        for (int i = 0; i < 4; ++i) {
            if (this.plankHealth[i] <= 0) {
                this.plankHealth[i] = n;
                break;
            }
        }
        this.chooseSprite();
        if (!GameServer.bServer) {
            for (int j = 0; j < IsoPlayer.numPlayers; ++j) {
                LosUtil.cachecleared[j] = true;
            }
            IsoGridSquare.setRecalcLightTime(-1);
            GameTime.instance.lightSourceUpdate = 100.0f;
        }
        if (this.square != null) {
            this.square.RecalcProperties();
        }
    }
    
    public InventoryItem removePlank(final IsoGameCharacter isoGameCharacter) {
        if (this.getNumPlanks() <= 0) {
            return null;
        }
        InventoryItem createItem = null;
        for (int i = 3; i >= 0; --i) {
            if (this.plankHealth[i] > 0) {
                final float min = Math.min(this.plankHealth[i] / 1000.0f, 1.0f);
                createItem = InventoryItemFactory.CreateItem("Base.Plank");
                createItem.setCondition((int)Math.max(createItem.getConditionMax() * min, 1.0f));
                this.plankHealth[i] = 0;
                break;
            }
        }
        if (this.getNumPlanks() <= 0) {
            if (this.square != null) {
                if (GameServer.bServer) {
                    this.square.transmitRemoveItemFromSquare(this);
                }
                else {
                    this.square.RemoveTileObject(this);
                }
            }
        }
        else {
            this.chooseSprite();
            if (!GameServer.bServer) {
                for (int j = 0; j < IsoPlayer.numPlayers; ++j) {
                    LosUtil.cachecleared[j] = true;
                }
                IsoGridSquare.setRecalcLightTime(-1);
                GameTime.instance.lightSourceUpdate = 100.0f;
            }
            if (this.square != null) {
                this.square.RecalcProperties();
            }
        }
        return createItem;
    }
    
    public int getNumPlanks() {
        int n = 0;
        for (int i = 0; i < 4; ++i) {
            if (this.plankHealth[i] > 0) {
                ++n;
            }
        }
        return n;
    }
    
    public boolean canAddPlank() {
        return !this.isMetal() && this.getNumPlanks() < 4 && !this.isMetalBar();
    }
    
    public void addMetalBar(final IsoGameCharacter isoGameCharacter, final InventoryItem inventoryItem) {
        if (this.getNumPlanks() > 0) {
            return;
        }
        if (this.metalHealth > 0) {
            return;
        }
        if (this.metalBarHealth > 0) {
            return;
        }
        this.metalBarHealth = 3000;
        if (inventoryItem != null) {
            this.metalBarHealth = (int)(inventoryItem.getCondition() / (float)inventoryItem.getConditionMax() * 5000.0f);
        }
        if (isoGameCharacter != null) {
            this.metalBarHealth *= (int)isoGameCharacter.getMetalBarricadeStrengthMod();
        }
        this.chooseSprite();
        if (!GameServer.bServer) {
            for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
                LosUtil.cachecleared[i] = true;
            }
            IsoGridSquare.setRecalcLightTime(-1);
            GameTime.instance.lightSourceUpdate = 100.0f;
        }
        if (this.square != null) {
            this.square.RecalcProperties();
        }
    }
    
    public InventoryItem removeMetalBar(final IsoGameCharacter isoGameCharacter) {
        if (this.metalBarHealth <= 0) {
            return null;
        }
        final float min = Math.min(this.metalBarHealth / 3000.0f, 1.0f);
        this.metalBarHealth = 0;
        final InventoryItem createItem = InventoryItemFactory.CreateItem("Base.MetalBar");
        createItem.setCondition((int)Math.max(createItem.getConditionMax() * min, 1.0f));
        if (this.square != null) {
            if (GameServer.bServer) {
                this.square.transmitRemoveItemFromSquare(this);
            }
            else {
                this.square.RemoveTileObject(this);
            }
        }
        return createItem;
    }
    
    public void addMetal(final IsoGameCharacter isoGameCharacter, final InventoryItem inventoryItem) {
        if (this.getNumPlanks() > 0) {
            return;
        }
        if (this.metalHealth > 0) {
            return;
        }
        this.metalHealth = 5000;
        if (inventoryItem != null) {
            this.metalHealth = (int)(inventoryItem.getCondition() / (float)inventoryItem.getConditionMax() * 5000.0f);
        }
        if (isoGameCharacter != null) {
            this.metalHealth *= (int)isoGameCharacter.getMetalBarricadeStrengthMod();
        }
        this.chooseSprite();
        if (!GameServer.bServer) {
            for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
                LosUtil.cachecleared[i] = true;
            }
            IsoGridSquare.setRecalcLightTime(-1);
            GameTime.instance.lightSourceUpdate = 100.0f;
        }
        if (this.square != null) {
            this.square.RecalcProperties();
        }
    }
    
    public boolean isMetalBar() {
        return this.metalBarHealth > 0;
    }
    
    public InventoryItem removeMetal(final IsoGameCharacter isoGameCharacter) {
        if (this.metalHealth <= 0) {
            return null;
        }
        final float min = Math.min(this.metalHealth / 5000.0f, 1.0f);
        this.metalHealth = 0;
        final InventoryItem createItem = InventoryItemFactory.CreateItem("Base.SheetMetal");
        createItem.setCondition((int)Math.max(createItem.getConditionMax() * min, 1.0f));
        if (this.square != null) {
            if (GameServer.bServer) {
                this.square.transmitRemoveItemFromSquare(this);
            }
            else {
                this.square.RemoveTileObject(this);
            }
        }
        return createItem;
    }
    
    public boolean isMetal() {
        return this.metalHealth > 0;
    }
    
    public boolean isBlockVision() {
        return this.isMetal() || this.getNumPlanks() > 2;
    }
    
    private void chooseSprite() {
        final IsoSpriteManager instance = IsoSpriteManager.instance;
        if (this.metalHealth > 0) {
            final int n = (this.metalHealth <= 2500) ? 2 : 0;
            final String s = "constructedobjects_01";
            switch (this.dir) {
                case W: {
                    this.sprite = instance.getSprite(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s, 24 + n));
                    break;
                }
                case N: {
                    this.sprite = instance.getSprite(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s, 25 + n));
                    break;
                }
                case E: {
                    this.sprite = instance.getSprite(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s, 28 + n));
                    break;
                }
                case S: {
                    this.sprite = instance.getSprite(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s, 29 + n));
                    break;
                }
                default: {
                    this.sprite.LoadFramesNoDirPageSimple("media/ui/missing-tile.png");
                    break;
                }
            }
            return;
        }
        if (this.metalBarHealth > 0) {
            final String s2 = "constructedobjects_01";
            switch (this.dir) {
                case W: {
                    this.sprite = instance.getSprite(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s2));
                    break;
                }
                case N: {
                    this.sprite = instance.getSprite(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s2));
                    break;
                }
                case E: {
                    this.sprite = instance.getSprite(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s2));
                    break;
                }
                case S: {
                    this.sprite = instance.getSprite(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s2));
                    break;
                }
                default: {
                    this.sprite.LoadFramesNoDirPageSimple("media/ui/missing-tile.png");
                    break;
                }
            }
            return;
        }
        final int numPlanks = this.getNumPlanks();
        if (numPlanks <= 0) {
            this.sprite = instance.getSprite("media/ui/missing-tile.png");
            return;
        }
        final String s3 = "carpentry_01";
        switch (this.dir) {
            case W: {
                this.sprite = instance.getSprite(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s3, 8 + (numPlanks - 1) * 2));
                break;
            }
            case N: {
                this.sprite = instance.getSprite(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s3, 9 + (numPlanks - 1) * 2));
                break;
            }
            case E: {
                this.sprite = instance.getSprite(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s3, 0 + (numPlanks - 1) * 2));
                break;
            }
            case S: {
                this.sprite = instance.getSprite(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s3, 1 + (numPlanks - 1) * 2));
                break;
            }
            default: {
                this.sprite.LoadFramesNoDirPageSimple("media/ui/missing-tile.png");
                break;
            }
        }
    }
    
    @Override
    public boolean isDestroyed() {
        return this.metalHealth <= 0 && this.getNumPlanks() <= 0 && this.metalBarHealth <= 0;
    }
    
    @Override
    public boolean TestCollide(final IsoMovingObject isoMovingObject, final IsoGridSquare isoGridSquare, final IsoGridSquare isoGridSquare2) {
        return false;
    }
    
    @Override
    public VisionResult TestVision(final IsoGridSquare isoGridSquare, final IsoGridSquare isoGridSquare2) {
        if (this.metalHealth <= 0 && this.getNumPlanks() <= 2) {
            return VisionResult.NoEffect;
        }
        if (isoGridSquare == this.square) {
            if (this.dir == IsoDirections.N && isoGridSquare2.getY() < isoGridSquare.getY()) {
                return VisionResult.Blocked;
            }
            if (this.dir == IsoDirections.S && isoGridSquare2.getY() > isoGridSquare.getY()) {
                return VisionResult.Blocked;
            }
            if (this.dir == IsoDirections.W && isoGridSquare2.getX() < isoGridSquare.getX()) {
                return VisionResult.Blocked;
            }
            if (this.dir == IsoDirections.E && isoGridSquare2.getX() > isoGridSquare.getX()) {
                return VisionResult.Blocked;
            }
        }
        else if (isoGridSquare2 == this.square && isoGridSquare != this.square) {
            return this.TestVision(isoGridSquare2, isoGridSquare);
        }
        return VisionResult.NoEffect;
    }
    
    @Override
    public void Thump(final IsoMovingObject isoMovingObject) {
        if (this.isDestroyed()) {
            return;
        }
        if (isoMovingObject instanceof IsoZombie) {
            final int numPlanks = this.getNumPlanks();
            final boolean b = this.metalHealth > 2500;
            this.Damage(((IsoZombie)isoMovingObject).strength * ThumpState.getFastForwardDamageMultiplier());
            if (numPlanks != this.getNumPlanks()) {
                ((IsoGameCharacter)isoMovingObject).getEmitter().playSound("BreakBarricadePlank");
                if (GameServer.bServer) {
                    GameServer.PlayWorldSoundServer("BreakBarricadePlank", false, isoMovingObject.getCurrentSquare(), 0.2f, 20.0f, 1.1f, true);
                }
            }
            if (this.isDestroyed()) {
                if (this.getSquare().getBuilding() != null) {
                    this.getSquare().getBuilding().forceAwake();
                }
                this.square.transmitRemoveItemFromSquare(this);
                if (!GameServer.bServer) {
                    this.square.RemoveTileObject(this);
                }
            }
            else if ((numPlanks != this.getNumPlanks() || (b && this.metalHealth < 2500)) && GameServer.bServer) {
                this.sendObjectChange("state");
            }
            if (!this.isDestroyed()) {
                this.setRenderEffect(RenderEffectType.Hit_Door, true);
            }
            WorldSoundManager.instance.addSound(isoMovingObject, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, true, 4.0f, 15.0f);
        }
    }
    
    @Override
    public Thumpable getThumpableFor(final IsoGameCharacter isoGameCharacter) {
        if (this.isDestroyed()) {
            return null;
        }
        return this;
    }
    
    @Override
    public Vector2 getFacingPosition(final Vector2 vector2) {
        if (this.square == null) {
            return vector2.set(0.0f, 0.0f);
        }
        if (this.dir == IsoDirections.N) {
            return vector2.set(this.getX() + 0.5f, this.getY());
        }
        if (this.dir == IsoDirections.S) {
            return vector2.set(this.getX() + 0.5f, this.getY() + 1.0f);
        }
        if (this.dir == IsoDirections.W) {
            return vector2.set(this.getX(), this.getY() + 0.5f);
        }
        if (this.dir == IsoDirections.E) {
            return vector2.set(this.getX() + 1.0f, this.getY() + 0.5f);
        }
        return vector2.set(this.getX(), this.getY() + 0.5f);
    }
    
    @Override
    public void WeaponHit(final IsoGameCharacter isoGameCharacter, final HandWeapon handWeapon) {
        if (this.isDestroyed()) {
            return;
        }
        final IsoPlayer isoPlayer = Type.tryCastTo(isoGameCharacter, IsoPlayer.class);
        if (GameClient.bClient) {
            if (isoPlayer != null) {
                GameClient.instance.sendWeaponHit(isoPlayer, handWeapon, this);
            }
            return;
        }
        final String s = (this.isMetal() || this.isMetalBar()) ? "HitBarricadeMetal" : "HitBarricadePlank";
        if (isoPlayer != null) {
            isoPlayer.setMeleeHitSurface((this.isMetal() || this.isMetalBar()) ? ParameterMeleeHitSurface.Material.Metal : ParameterMeleeHitSurface.Material.Wood);
        }
        SoundManager.instance.PlayWorldSound(s, false, this.getSquare(), 1.0f, 20.0f, 2.0f, false);
        if (GameServer.bServer) {
            GameServer.PlayWorldSoundServer(s, false, this.getSquare(), 1.0f, 20.0f, 2.0f, false);
        }
        if (handWeapon != null) {
            this.Damage(handWeapon.getDoorDamage() * 5);
        }
        else {
            this.Damage(100);
        }
        WorldSoundManager.instance.addSound(isoGameCharacter, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, false, 0.0f, 15.0f);
        if (this.isDestroyed()) {
            if (isoGameCharacter != null) {
                final String s2 = (s == "HitBarricadeMetal") ? "BreakBarricadeMetal" : "BreakBarricadePlank";
                isoGameCharacter.getEmitter().playSound(s2);
                if (GameServer.bServer) {
                    GameServer.PlayWorldSoundServer(s2, false, isoGameCharacter.getCurrentSquare(), 0.2f, 20.0f, 1.1f, true);
                }
            }
            this.square.transmitRemoveItemFromSquare(this);
            if (!GameServer.bServer) {
                this.square.RemoveTileObject(this);
            }
        }
        if (!this.isDestroyed()) {
            this.setRenderEffect(RenderEffectType.Hit_Door, true);
        }
    }
    
    public void DamageBarricade(final int n) {
        this.Damage(n);
    }
    
    public void Damage(final int n) {
        if ("Tutorial".equals(Core.GameMode)) {
            return;
        }
        if (this.metalHealth > 0) {
            this.metalHealth -= n;
            if (this.metalHealth <= 0) {
                this.metalHealth = 0;
                this.chooseSprite();
            }
            return;
        }
        if (this.metalBarHealth > 0) {
            this.metalBarHealth -= n;
            if (this.metalBarHealth <= 0) {
                this.metalBarHealth = 0;
                this.chooseSprite();
            }
            return;
        }
        int i = 3;
        while (i >= 0) {
            if (this.plankHealth[i] > 0) {
                final int[] plankHealth = this.plankHealth;
                final int n2 = i;
                plankHealth[n2] -= n;
                if (this.plankHealth[i] <= 0) {
                    this.plankHealth[i] = 0;
                    this.chooseSprite();
                    break;
                }
                break;
            }
            else {
                --i;
            }
        }
    }
    
    @Override
    public float getThumpCondition() {
        if (this.metalHealth > 0) {
            return PZMath.clamp(this.metalHealth, 0, 5000) / 5000.0f;
        }
        if (this.metalBarHealth > 0) {
            return PZMath.clamp(this.metalBarHealth, 0, 3000) / 3000.0f;
        }
        for (int i = 3; i >= 0; --i) {
            if (this.plankHealth[i] > 0) {
                return PZMath.clamp(this.plankHealth[i], 0, 1000) / 1000.0f;
            }
        }
        return 0.0f;
    }
    
    @Override
    public void load(final ByteBuffer byteBuffer, final int n, final boolean b) throws IOException {
        this.dir = IsoDirections.fromIndex(byteBuffer.get());
        for (byte value = byteBuffer.get(), b2 = 0; b2 < value; ++b2) {
            final short short1 = byteBuffer.getShort();
            if (b2 < 4) {
                this.plankHealth[b2] = short1;
            }
        }
        this.metalHealth = byteBuffer.getShort();
        if (n >= 90) {
            this.metalBarHealth = byteBuffer.getShort();
        }
        this.chooseSprite();
    }
    
    @Override
    public void save(final ByteBuffer byteBuffer, final boolean b) throws IOException {
        byteBuffer.put((byte)1);
        byteBuffer.put(IsoObject.factoryGetClassID(this.getObjectName()));
        byteBuffer.put((byte)this.dir.index());
        byteBuffer.put((byte)4);
        for (int i = 0; i < 4; ++i) {
            byteBuffer.putShort((short)this.plankHealth[i]);
        }
        byteBuffer.putShort((short)this.metalHealth);
        byteBuffer.putShort((short)this.metalBarHealth);
    }
    
    @Override
    public void saveChange(final String anObject, final KahluaTable kahluaTable, final ByteBuffer byteBuffer) {
        if ("state".equals(anObject)) {
            for (int i = 0; i < 4; ++i) {
                byteBuffer.putShort((short)this.plankHealth[i]);
            }
            byteBuffer.putShort((short)this.metalHealth);
            byteBuffer.putShort((short)this.metalBarHealth);
        }
    }
    
    @Override
    public void loadChange(final String anObject, final ByteBuffer byteBuffer) {
        if ("state".equals(anObject)) {
            for (int i = 0; i < 4; ++i) {
                this.plankHealth[i] = byteBuffer.getShort();
            }
            this.metalHealth = byteBuffer.getShort();
            this.metalBarHealth = byteBuffer.getShort();
            this.chooseSprite();
            if (this.square != null) {
                this.square.RecalcProperties();
            }
            for (int j = 0; j < IsoPlayer.numPlayers; ++j) {
                LosUtil.cachecleared[j] = true;
            }
            IsoGridSquare.setRecalcLightTime(-1);
            GameTime.instance.lightSourceUpdate = 100.0f;
        }
    }
    
    public BarricadeAble getBarricadedObject() {
        final int specialObjectIndex = this.getSpecialObjectIndex();
        if (specialObjectIndex == -1) {
            return null;
        }
        final ArrayList<IsoObject> specialObjects = this.getSquare().getSpecialObjects();
        if (this.getDir() == IsoDirections.W || this.getDir() == IsoDirections.N) {
            final boolean b = this.getDir() == IsoDirections.N;
            for (int i = specialObjectIndex - 1; i >= 0; --i) {
                final IsoObject isoObject = specialObjects.get(i);
                if (isoObject instanceof BarricadeAble && b == ((BarricadeAble)isoObject).getNorth()) {
                    return (BarricadeAble)isoObject;
                }
            }
        }
        else if (this.getDir() == IsoDirections.E || this.getDir() == IsoDirections.S) {
            final boolean b2 = this.getDir() == IsoDirections.S;
            final IsoGridSquare gridSquare = this.getCell().getGridSquare(this.getSquare().getX() + ((this.getDir() == IsoDirections.E) ? 1 : 0), this.getSquare().getY() + ((this.getDir() == IsoDirections.S) ? 1 : 0), this.getZ());
            if (gridSquare != null) {
                final ArrayList<IsoObject> specialObjects2 = gridSquare.getSpecialObjects();
                for (int j = specialObjects2.size() - 1; j >= 0; --j) {
                    final IsoObject isoObject2 = specialObjects2.get(j);
                    if (isoObject2 instanceof BarricadeAble && b2 == ((BarricadeAble)isoObject2).getNorth()) {
                        return (BarricadeAble)isoObject2;
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    public void render(final float n, final float n2, final float n3, ColorInfo lightInfo, final boolean b, final boolean b2, final Shader shader) {
        final int playerIndex = IsoCamera.frameState.playerIndex;
        final BarricadeAble barricadedObject = this.getBarricadedObject();
        if (barricadedObject != null && this.square.lighting[playerIndex].targetDarkMulti() <= barricadedObject.getSquare().lighting[playerIndex].targetDarkMulti()) {
            lightInfo = barricadedObject.getSquare().lighting[playerIndex].lightInfo();
            this.setTargetAlpha(playerIndex, ((IsoObject)barricadedObject).getTargetAlpha(playerIndex));
        }
        super.render(n, n2, n3, lightInfo, b, b2, shader);
    }
    
    public static IsoBarricade GetBarricadeOnSquare(final IsoGridSquare isoGridSquare, final IsoDirections isoDirections) {
        if (isoGridSquare == null) {
            return null;
        }
        for (int i = 0; i < isoGridSquare.getSpecialObjects().size(); ++i) {
            final IsoObject isoObject = isoGridSquare.getSpecialObjects().get(i);
            if (isoObject instanceof IsoBarricade) {
                final IsoBarricade isoBarricade = (IsoBarricade)isoObject;
                if (isoBarricade.getDir() == isoDirections) {
                    return isoBarricade;
                }
            }
        }
        return null;
    }
    
    public static IsoBarricade GetBarricadeForCharacter(final BarricadeAble barricadeAble, final IsoGameCharacter isoGameCharacter) {
        if (barricadeAble == null || barricadeAble.getSquare() == null) {
            return null;
        }
        if (isoGameCharacter != null) {
            if (barricadeAble.getNorth()) {
                if (isoGameCharacter.getY() < barricadeAble.getSquare().getY()) {
                    return GetBarricadeOnSquare(barricadeAble.getOppositeSquare(), barricadeAble.getNorth() ? IsoDirections.S : IsoDirections.E);
                }
            }
            else if (isoGameCharacter.getX() < barricadeAble.getSquare().getX()) {
                return GetBarricadeOnSquare(barricadeAble.getOppositeSquare(), barricadeAble.getNorth() ? IsoDirections.S : IsoDirections.E);
            }
        }
        return GetBarricadeOnSquare(barricadeAble.getSquare(), barricadeAble.getNorth() ? IsoDirections.N : IsoDirections.W);
    }
    
    public static IsoBarricade GetBarricadeOppositeCharacter(final BarricadeAble barricadeAble, final IsoGameCharacter isoGameCharacter) {
        if (barricadeAble == null || barricadeAble.getSquare() == null) {
            return null;
        }
        if (isoGameCharacter != null) {
            if (barricadeAble.getNorth()) {
                if (isoGameCharacter.getY() < barricadeAble.getSquare().getY()) {
                    return GetBarricadeOnSquare(barricadeAble.getSquare(), barricadeAble.getNorth() ? IsoDirections.N : IsoDirections.W);
                }
            }
            else if (isoGameCharacter.getX() < barricadeAble.getSquare().getX()) {
                return GetBarricadeOnSquare(barricadeAble.getSquare(), barricadeAble.getNorth() ? IsoDirections.N : IsoDirections.W);
            }
        }
        return GetBarricadeOnSquare(barricadeAble.getOppositeSquare(), barricadeAble.getNorth() ? IsoDirections.S : IsoDirections.E);
    }
    
    public static IsoBarricade AddBarricadeToObject(final BarricadeAble barricadeAble, final boolean b) {
        final IsoGridSquare isoGridSquare = b ? barricadeAble.getOppositeSquare() : barricadeAble.getSquare();
        IsoDirections isoDirections;
        if (barricadeAble.getNorth()) {
            isoDirections = (b ? IsoDirections.S : IsoDirections.N);
        }
        else {
            isoDirections = (b ? IsoDirections.E : IsoDirections.W);
        }
        if (isoGridSquare == null || isoDirections == null) {
            return null;
        }
        final IsoBarricade getBarricadeOnSquare = GetBarricadeOnSquare(isoGridSquare, isoDirections);
        if (getBarricadeOnSquare != null) {
            return getBarricadeOnSquare;
        }
        final IsoBarricade isoBarricade = new IsoBarricade(IsoWorld.instance.CurrentCell, isoGridSquare, isoDirections);
        int n = -1;
        for (int i = 0; i < isoGridSquare.getObjects().size(); ++i) {
            final IsoObject isoObject = isoGridSquare.getObjects().get(i);
            if (isoObject instanceof IsoCurtain) {
                final IsoCurtain isoCurtain = (IsoCurtain)isoObject;
                if (isoCurtain.getType() == IsoObjectType.curtainW && isoDirections == IsoDirections.W) {
                    n = i;
                }
                else if (isoCurtain.getType() == IsoObjectType.curtainN && isoDirections == IsoDirections.N) {
                    n = i;
                }
                else if (isoCurtain.getType() == IsoObjectType.curtainE && isoDirections == IsoDirections.E) {
                    n = i;
                }
                else if (isoCurtain.getType() == IsoObjectType.curtainS && isoDirections == IsoDirections.S) {
                    n = i;
                }
                if (n != -1) {
                    break;
                }
            }
        }
        isoGridSquare.AddSpecialObject(isoBarricade, n);
        for (int j = 0; j < IsoPlayer.numPlayers; ++j) {
            LosUtil.cachecleared[j] = true;
        }
        IsoGridSquare.setRecalcLightTime(-1);
        GameTime.instance.lightSourceUpdate = 100.0f;
        return isoBarricade;
    }
    
    public static IsoBarricade AddBarricadeToObject(final BarricadeAble barricadeAble, final IsoGameCharacter isoGameCharacter) {
        if (barricadeAble == null || barricadeAble.getSquare() == null || isoGameCharacter == null) {
            return null;
        }
        if (barricadeAble.getNorth()) {
            return AddBarricadeToObject(barricadeAble, isoGameCharacter.getY() < barricadeAble.getSquare().getY());
        }
        return AddBarricadeToObject(barricadeAble, isoGameCharacter.getX() < barricadeAble.getSquare().getX());
    }
}

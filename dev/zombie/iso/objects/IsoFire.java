// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.objects;

import se.krka.kahlua.vm.KahluaTable;
import zombie.core.textures.Texture;
import zombie.iso.sprite.IsoDirectionFrame;
import zombie.core.Core;
import zombie.core.opengl.Shader;
import zombie.core.textures.ColorInfo;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.IsoWorld;
import zombie.GameTime;
import zombie.iso.IsoMovingObject;
import zombie.SandboxOptions;
import zombie.iso.areas.SafeHouse;
import zombie.network.ServerOptions;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.Lua.LuaEventManager;
import zombie.ui.TutorialManager;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.core.Rand;
import java.io.IOException;
import zombie.iso.sprite.IsoSpriteInstance;
import java.util.ArrayList;
import java.nio.ByteBuffer;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoCell;
import zombie.iso.IsoHeatSource;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoObject;

public class IsoFire extends IsoObject
{
    public int Age;
    public int Energy;
    public int Life;
    public int LifeStage;
    public int LifeStageDuration;
    public int LifeStageTimer;
    public int MaxLife;
    public int MinLife;
    public int SpreadDelay;
    public int SpreadTimer;
    public int numFlameParticles;
    public boolean perm;
    public boolean bSmoke;
    public IsoLightSource LightSource;
    public int LightRadius;
    public float LightOscillator;
    private IsoHeatSource heatSource;
    private float accum;
    
    public IsoFire(final IsoCell isoCell) {
        super(isoCell);
        this.Age = 0;
        this.Energy = 0;
        this.MaxLife = 3000;
        this.MinLife = 800;
        this.perm = false;
        this.bSmoke = false;
        this.LightSource = null;
        this.LightRadius = 1;
        this.LightOscillator = 0.0f;
        this.accum = 0.0f;
    }
    
    public IsoFire(final IsoCell isoCell, final IsoGridSquare square) {
        super(isoCell);
        this.Age = 0;
        this.Energy = 0;
        this.MaxLife = 3000;
        this.MinLife = 800;
        this.perm = false;
        this.bSmoke = false;
        this.LightSource = null;
        this.LightRadius = 1;
        this.LightOscillator = 0.0f;
        this.accum = 0.0f;
        this.square = square;
        this.perm = true;
    }
    
    @Override
    public String getObjectName() {
        return "Fire";
    }
    
    @Override
    public void save(final ByteBuffer byteBuffer, final boolean b) throws IOException {
        final ArrayList<IsoSpriteInstance> attachedAnimSprite = this.AttachedAnimSprite;
        this.AttachedAnimSprite = null;
        super.save(byteBuffer, b);
        this.AttachedAnimSprite = attachedAnimSprite;
        this.sprite = null;
        byteBuffer.putInt(this.Life);
        byteBuffer.putInt(this.SpreadDelay);
        byteBuffer.putInt(this.LifeStage - 1);
        byteBuffer.putInt(this.LifeStageTimer);
        byteBuffer.putInt(this.LifeStageDuration);
        byteBuffer.putInt(this.Energy);
        byteBuffer.putInt(this.numFlameParticles);
        byteBuffer.putInt(this.SpreadTimer);
        byteBuffer.putInt(this.Age);
        byteBuffer.put((byte)(this.perm ? 1 : 0));
        byteBuffer.put((byte)this.LightRadius);
        byteBuffer.put((byte)(this.bSmoke ? 1 : 0));
    }
    
    @Override
    public void load(final ByteBuffer byteBuffer, final int n, final boolean b) throws IOException {
        super.load(byteBuffer, n, b);
        this.sprite = null;
        this.Life = byteBuffer.getInt();
        this.SpreadDelay = byteBuffer.getInt();
        this.LifeStage = byteBuffer.getInt();
        this.LifeStageTimer = byteBuffer.getInt();
        this.LifeStageDuration = byteBuffer.getInt();
        this.Energy = byteBuffer.getInt();
        this.numFlameParticles = byteBuffer.getInt();
        this.SpreadTimer = byteBuffer.getInt();
        this.Age = byteBuffer.getInt();
        this.perm = (byteBuffer.get() == 1);
        this.LightRadius = (byteBuffer.get() & 0xFF);
        if (n >= 89) {
            this.bSmoke = (byteBuffer.get() == 1);
        }
        if (this.perm) {
            this.AttachAnim("Fire", "01", 4, IsoFireManager.FireAnimDelay, -16, -78, true, 0, false, 0.7f, IsoFireManager.FireTintMod);
            return;
        }
        if (this.numFlameParticles == 0) {
            this.numFlameParticles = 1;
        }
        switch (this.LifeStage) {
            case -1: {
                this.LifeStage = 0;
                for (int i = 0; i < this.numFlameParticles; ++i) {
                    this.AttachAnim("Fire", "01", 4, IsoFireManager.FireAnimDelay, -16 + (-16 + Rand.Next(32)), -85 + (-16 + Rand.Next(32)), true, 0, false, 0.7f, IsoFireManager.FireTintMod);
                }
                break;
            }
            case 0: {
                this.LifeStage = 1;
                this.LifeStageTimer = this.LifeStageDuration;
                this.AttachAnim("Fire", "02", 4, IsoFireManager.FireAnimDelay, -16, -72, true, 0, false, 0.7f, IsoFireManager.FireTintMod);
                break;
            }
            case 1: {
                this.LifeStage = 2;
                this.LifeStageTimer = this.LifeStageDuration;
                this.AttachAnim("Smoke", "01", 4, IsoFireManager.SmokeAnimDelay, -9, 12, true, 0, false, 0.7f, IsoFireManager.SmokeTintMod);
                this.AttachAnim("Fire", "03", 4, IsoFireManager.FireAnimDelay, -9, -52, true, 0, false, 0.7f, IsoFireManager.FireTintMod);
                break;
            }
            case 2: {
                this.LifeStage = 3;
                this.LifeStageTimer = this.LifeStageDuration / 3;
                this.RemoveAttachedAnims();
                this.AttachAnim("Smoke", "02", 4, IsoFireManager.SmokeAnimDelay, 0, 12, true, 0, false, 0.7f, IsoFireManager.SmokeTintMod);
                this.AttachAnim("Fire", "02", 4, IsoFireManager.FireAnimDelay, -16, -72, true, 0, false, 0.7f, IsoFireManager.FireTintMod);
                break;
            }
            case 3: {
                this.LifeStage = 4;
                this.LifeStageTimer = this.LifeStageDuration / 3;
                this.RemoveAttachedAnims();
                if (this.bSmoke) {
                    this.AttachAnim("Smoke", "03", 4, IsoFireManager.SmokeAnimDelay, 0, 12, true, 0, false, 0.7f, IsoFireManager.SmokeTintMod);
                    break;
                }
                this.AttachAnim("Smoke", "03", 4, IsoFireManager.SmokeAnimDelay, 0, 12, true, 0, false, 0.7f, IsoFireManager.SmokeTintMod);
                this.AttachAnim("Fire", "01", 4, IsoFireManager.FireAnimDelay, -16, -85, true, 0, false, 0.7f, IsoFireManager.FireTintMod);
                break;
            }
            case 4: {
                this.LifeStage = 5;
                this.LifeStageTimer = this.LifeStageDuration / 3;
                this.RemoveAttachedAnims();
                this.AttachAnim("Smoke", "01", 4, IsoFireManager.SmokeAnimDelay, -9, 12, true, 0, false, 0.7f, IsoFireManager.SmokeTintMod);
                break;
            }
        }
        if (this.square != null) {
            if (this.LifeStage < 4) {
                this.square.getProperties().Set(IsoFlagType.burning);
            }
            else {
                this.square.getProperties().Set(IsoFlagType.smoke);
            }
        }
    }
    
    public IsoFire(final IsoCell isoCell, final IsoGridSquare square, final boolean b, final int energy, final int life, final boolean bSmoke) {
        this.Age = 0;
        this.Energy = 0;
        this.MaxLife = 3000;
        this.MinLife = 800;
        this.perm = false;
        this.bSmoke = false;
        this.LightSource = null;
        this.LightRadius = 1;
        this.LightOscillator = 0.0f;
        this.accum = 0.0f;
        this.square = square;
        this.DirtySlice();
        this.square.getProperties().Set(IsoFlagType.smoke);
        this.AttachAnim("Smoke", "03", 4, IsoFireManager.SmokeAnimDelay, 0, 12, true, 0, false, 0.7f, IsoFireManager.SmokeTintMod);
        this.Life = this.MinLife + Rand.Next(this.MaxLife - this.MinLife);
        if (life > 0) {
            this.Life = life;
        }
        this.LifeStage = 4;
        final int n = this.Life / 4;
        this.LifeStageDuration = n;
        this.LifeStageTimer = n;
        this.Energy = energy;
        this.bSmoke = bSmoke;
    }
    
    public IsoFire(final IsoCell isoCell, final IsoGridSquare square, final boolean b, final int energy, final int life) {
        this.Age = 0;
        this.Energy = 0;
        this.MaxLife = 3000;
        this.MinLife = 800;
        this.perm = false;
        this.bSmoke = false;
        this.LightSource = null;
        this.LightRadius = 1;
        this.LightOscillator = 0.0f;
        this.accum = 0.0f;
        this.square = square;
        this.DirtySlice();
        this.numFlameParticles = 2 + Rand.Next(2);
        for (int i = 0; i < this.numFlameParticles; ++i) {
            this.AttachAnim("Fire", "01", 4, IsoFireManager.FireAnimDelay, -16 + (-16 + Rand.Next(32)), -85 + (-16 + Rand.Next(32)), true, 0, false, 0.7f, IsoFireManager.FireTintMod);
        }
        this.Life = this.MinLife + Rand.Next(this.MaxLife - this.MinLife);
        if (life > 0) {
            this.Life = life;
        }
        if (this.square.getProperties() != null && !this.square.getProperties().Is(IsoFlagType.vegitation) && this.square.getFloor() != null) {
            this.Life -= this.square.getFloor().getSprite().firerequirement * 100;
            if (this.Life < 600) {
                this.Life = Rand.Next(300, 600);
            }
        }
        final int next = Rand.Next(this.Life - this.Life / 2);
        this.SpreadTimer = next;
        this.SpreadDelay = next;
        this.LifeStage = 0;
        final int n = this.Life / 4;
        this.LifeStageDuration = n;
        this.LifeStageTimer = n;
        if (TutorialManager.instance.Active) {
            this.LifeStageDuration *= 2;
            this.Life *= 2;
        }
        if (TutorialManager.instance.Active) {
            final int n2 = this.SpreadTimer / 4;
            this.SpreadTimer = n2;
            this.SpreadDelay = n2;
        }
        square.getProperties().Set(IsoFlagType.burning);
        this.Energy = energy;
        if (this.square.getProperties().Is(IsoFlagType.vegitation)) {
            this.Energy += 50;
        }
        LuaEventManager.triggerEvent("OnNewFire", this);
    }
    
    public IsoFire(final IsoCell isoCell, final IsoGridSquare isoGridSquare, final boolean b, final int n) {
        this(isoCell, isoGridSquare, b, n, 0);
    }
    
    public static boolean CanAddSmoke(final IsoGridSquare isoGridSquare, final boolean b) {
        return CanAddFire(isoGridSquare, b, true);
    }
    
    public static boolean CanAddFire(final IsoGridSquare isoGridSquare, final boolean b) {
        return CanAddFire(isoGridSquare, b, false);
    }
    
    public static boolean CanAddFire(final IsoGridSquare isoGridSquare, final boolean b, final boolean b2) {
        return (b2 || (!GameServer.bServer && !GameClient.bClient) || !ServerOptions.instance.NoFire.getValue()) && isoGridSquare != null && !isoGridSquare.getObjects().isEmpty() && !isoGridSquare.Is(IsoFlagType.water) && (b || !isoGridSquare.getProperties().Is(IsoFlagType.burntOut)) && !isoGridSquare.getProperties().Is(IsoFlagType.burning) && !isoGridSquare.getProperties().Is(IsoFlagType.smoke) && (b || Fire_IsSquareFlamable(isoGridSquare)) && (b2 || (!GameServer.bServer && !GameClient.bClient) || SafeHouse.getSafeHouse(isoGridSquare) == null || ServerOptions.instance.SafehouseAllowFire.getValue());
    }
    
    public static boolean Fire_IsSquareFlamable(final IsoGridSquare isoGridSquare) {
        return !isoGridSquare.getProperties().Is(IsoFlagType.unflamable);
    }
    
    @Override
    public boolean HasTooltip() {
        return false;
    }
    
    public void Spread() {
        if (GameClient.bClient) {
            return;
        }
        if (!SandboxOptions.instance.FireSpread.getValue()) {
            return;
        }
        if (this.getCell() == null) {
            return;
        }
        if (this.square == null) {
            return;
        }
        if (this.LifeStage >= 4) {
            return;
        }
        IsoGridSquare isoGridSquare = null;
        int n = Rand.Next(3) + 1;
        if (Rand.Next(50) == 0) {
            n += 15;
        }
        if (TutorialManager.instance.Active) {
            n += 15;
        }
        for (int i = 0; i < n; ++i) {
            switch (Rand.Next(13)) {
                case 0: {
                    isoGridSquare = this.getCell().getGridSquare(this.square.getX(), this.square.getY() - 1, this.square.getZ());
                    break;
                }
                case 1: {
                    isoGridSquare = this.getCell().getGridSquare(this.square.getX() + 1, this.square.getY() - 1, this.square.getZ());
                    break;
                }
                case 2: {
                    isoGridSquare = this.getCell().getGridSquare(this.square.getX() + 1, this.square.getY(), this.square.getZ());
                    break;
                }
                case 3: {
                    isoGridSquare = this.getCell().getGridSquare(this.square.getX() + 1, this.square.getY() + 1, this.square.getZ());
                    break;
                }
                case 4: {
                    isoGridSquare = this.getCell().getGridSquare(this.square.getX(), this.square.getY() + 1, this.square.getZ());
                    break;
                }
                case 5: {
                    isoGridSquare = this.getCell().getGridSquare(this.square.getX() - 1, this.square.getY() + 1, this.square.getZ());
                    break;
                }
                case 6: {
                    isoGridSquare = this.getCell().getGridSquare(this.square.getX() - 1, this.square.getY(), this.square.getZ());
                    break;
                }
                case 7: {
                    isoGridSquare = this.getCell().getGridSquare(this.square.getX() - 1, this.square.getY() - 1, this.square.getZ());
                    break;
                }
                case 8: {
                    isoGridSquare = this.getCell().getGridSquare(this.square.getX() - 1, this.square.getY() - 1, this.square.getZ() - 1);
                    break;
                }
                case 9: {
                    isoGridSquare = this.getCell().getGridSquare(this.square.getX() - 1, this.square.getY(), this.square.getZ() - 1);
                    break;
                }
                case 10: {
                    isoGridSquare = this.getCell().getGridSquare(this.square.getX(), this.square.getY() - 1, this.square.getZ() - 1);
                    break;
                }
                case 11: {
                    isoGridSquare = this.getCell().getGridSquare(this.square.getX(), this.square.getY(), this.square.getZ() - 1);
                    break;
                }
                case 12: {
                    isoGridSquare = this.getCell().getGridSquare(this.square.getX(), this.square.getY(), this.square.getZ() + 1);
                    break;
                }
            }
            if (CanAddFire(isoGridSquare, false)) {
                final int squaresEnergyRequirement = this.getSquaresEnergyRequirement(isoGridSquare);
                if (this.Energy >= squaresEnergyRequirement) {
                    this.Energy -= squaresEnergyRequirement;
                    if (GameServer.bServer) {
                        this.sendObjectChange("Energy");
                    }
                    if (RainManager.isRaining()) {
                        return;
                    }
                    IsoFireManager.StartFire(this.getCell(), isoGridSquare, false, isoGridSquare.getProperties().Is(IsoFlagType.exterior) ? this.Energy : (squaresEnergyRequirement * 2));
                }
            }
        }
    }
    
    public boolean TestCollide(final IsoMovingObject isoMovingObject, final IsoGridSquare isoGridSquare) {
        return this.square == isoGridSquare;
    }
    
    @Override
    public VisionResult TestVision(final IsoGridSquare isoGridSquare, final IsoGridSquare isoGridSquare2) {
        return VisionResult.NoEffect;
    }
    
    @Override
    public void update() {
        if (this.getObjectIndex() == -1) {
            return;
        }
        if (!GameServer.bServer) {
            IsoFireManager.updateSound(this);
        }
        if (this.LifeStage < 4) {
            this.square.getProperties().Set(IsoFlagType.burning);
        }
        else {
            this.square.getProperties().Set(IsoFlagType.smoke);
        }
        if (!this.bSmoke && this.LifeStage < 5) {
            this.square.BurnTick();
        }
        for (int size = this.AttachedAnimSprite.size(), i = 0; i < size; ++i) {
            final IsoSpriteInstance isoSpriteInstance = this.AttachedAnimSprite.get(i);
            final IsoSprite parentSprite = isoSpriteInstance.parentSprite;
            isoSpriteInstance.update();
            final float n = GameTime.instance.getMultipliedSecondsSinceLastUpdate() * 60.0f;
            final IsoSpriteInstance isoSpriteInstance2 = isoSpriteInstance;
            isoSpriteInstance2.Frame += isoSpriteInstance.AnimFrameIncrease * n;
            if ((int)isoSpriteInstance.Frame >= parentSprite.CurrentAnim.Frames.size() && parentSprite.Loop && isoSpriteInstance.Looped) {
                isoSpriteInstance.Frame = 0.0f;
            }
        }
        if (!this.bSmoke && !GameServer.bServer && this.LightSource == null) {
            this.LightSource = new IsoLightSource(this.square.getX(), this.square.getY(), this.square.getZ(), 0.61f, 0.165f, 0.0f, this.perm ? this.LightRadius : 5);
            IsoWorld.instance.CurrentCell.addLamppost(this.LightSource);
        }
        if (this.perm) {
            if (this.heatSource == null) {
                this.heatSource = new IsoHeatSource(this.square.x, this.square.y, this.square.z, this.LightRadius, 35);
                IsoWorld.instance.CurrentCell.addHeatSource(this.heatSource);
            }
            else {
                this.heatSource.setRadius(this.LightRadius);
            }
            return;
        }
        this.accum += GameTime.getInstance().getMultiplier() / 1.6f;
        while (this.accum > 1.0f) {
            --this.accum;
            ++this.Age;
            if (this.LifeStageTimer > 0) {
                --this.LifeStageTimer;
                if (this.LifeStageTimer <= 0) {
                    switch (this.LifeStage) {
                        case 0: {
                            this.LifeStage = 1;
                            this.LifeStageTimer = this.LifeStageDuration;
                            this.AttachAnim("Fire", "01", 4, IsoFireManager.FireAnimDelay, -16, -72, true, 0, false, 0.7f, IsoFireManager.FireTintMod);
                            this.square.Burn();
                            if (this.LightSource != null) {
                                this.setLightRadius(5);
                                break;
                            }
                            break;
                        }
                        case 1: {
                            this.LifeStage = 2;
                            this.LifeStageTimer = this.LifeStageDuration;
                            this.RemoveAttachedAnims();
                            this.AttachAnim("Smoke", "02", 4, IsoFireManager.SmokeAnimDelay, -9, 12, true, 0, false, 0.7f, IsoFireManager.SmokeTintMod);
                            this.AttachAnim("Fire", "02", 4, IsoFireManager.FireAnimDelay, -9, -52, true, 0, false, 0.7f, IsoFireManager.FireTintMod);
                            this.square.Burn();
                            if (this.LightSource != null) {
                                this.setLightRadius(8);
                                break;
                            }
                            break;
                        }
                        case 2: {
                            this.LifeStage = 3;
                            this.LifeStageTimer = this.LifeStageDuration / 3;
                            this.RemoveAttachedAnims();
                            this.AttachAnim("Smoke", "03", 4, IsoFireManager.SmokeAnimDelay, 0, 12, true, 0, false, 0.7f, IsoFireManager.SmokeTintMod);
                            this.AttachAnim("Fire", "03", 4, IsoFireManager.FireAnimDelay, -16, -72, true, 0, false, 0.7f, IsoFireManager.FireTintMod);
                            if (this.LightSource != null) {
                                this.setLightRadius(12);
                                break;
                            }
                            break;
                        }
                        case 3: {
                            this.LifeStage = 4;
                            this.LifeStageTimer = this.LifeStageDuration / 3;
                            this.RemoveAttachedAnims();
                            this.AttachAnim("Smoke", "02", 4, IsoFireManager.SmokeAnimDelay, 0, 12, true, 0, false, 0.7f, IsoFireManager.SmokeTintMod);
                            this.AttachAnim("Fire", "02", 4, IsoFireManager.FireAnimDelay, -16, -85, true, 0, false, 0.7f, IsoFireManager.FireTintMod);
                            if (this.LightSource != null) {
                                this.setLightRadius(8);
                                break;
                            }
                            break;
                        }
                        case 4: {
                            this.LifeStage = 5;
                            this.LifeStageTimer = this.LifeStageDuration / 3;
                            this.RemoveAttachedAnims();
                            this.AttachAnim("Smoke", "01", 4, IsoFireManager.SmokeAnimDelay, -9, 12, true, 0, false, 0.7f, IsoFireManager.SmokeTintMod);
                            if (this.LightSource != null) {
                                this.setLightRadius(1);
                                break;
                            }
                            break;
                        }
                    }
                }
            }
            if (this.Life <= 0) {
                this.extinctFire();
                break;
            }
            --this.Life;
            if (this.LifeStage > 0 && this.SpreadTimer > 0) {
                --this.SpreadTimer;
                if (this.SpreadTimer <= 0) {
                    if (this.LifeStage != 5) {
                        this.Spread();
                    }
                    this.SpreadTimer = this.SpreadDelay;
                }
            }
            if (this.Energy <= 0) {
                this.extinctFire();
                break;
            }
        }
    }
    
    @Override
    public void render(float n, float n2, final float n3, final ColorInfo colorInfo, final boolean b, final boolean b2, final Shader shader) {
        n += 0.5f;
        n2 += 0.5f;
        this.sx = 0.0f;
        this.offsetX = 0.0f;
        this.offsetY = 0.0f;
        final float n4 = (float)Core.TileScale;
        for (int i = 0; i < this.AttachedAnimSprite.size(); ++i) {
            final IsoSprite parentSprite = this.AttachedAnimSprite.get(i).parentSprite;
            if (parentSprite != null && parentSprite.CurrentAnim != null) {
                if (parentSprite.def != null) {
                    final Texture texture = parentSprite.CurrentAnim.Frames.get((int)parentSprite.def.Frame).directions[this.dir.index()];
                    if (texture != null) {
                        parentSprite.soffX = (short)(-(texture.getWidthOrig() / 2 * n4));
                        parentSprite.soffY = (short)(-(texture.getHeightOrig() * n4));
                        this.AttachedAnimSprite.get(i).setScale(n4, n4);
                    }
                }
            }
        }
        super.render(n, n2, n3, colorInfo, b, b2, shader);
        if (Core.bDebug) {}
    }
    
    public void extinctFire() {
        this.square.getProperties().UnSet(IsoFlagType.burning);
        this.square.getProperties().UnSet(IsoFlagType.smoke);
        this.RemoveAttachedAnims();
        this.square.getObjects().remove(this);
        this.square.RemoveTileObject(this);
        this.setLife(0);
        this.removeFromWorld();
    }
    
    int getSquaresEnergyRequirement(final IsoGridSquare isoGridSquare) {
        int firerequirement = 30;
        if (isoGridSquare.getProperties().Is(IsoFlagType.vegitation)) {
            firerequirement = -15;
        }
        if (!isoGridSquare.getProperties().Is(IsoFlagType.exterior)) {
            firerequirement = 40;
        }
        if (isoGridSquare.getFloor() != null && isoGridSquare.getFloor().getSprite() != null) {
            firerequirement = isoGridSquare.getFloor().getSprite().firerequirement;
        }
        if (TutorialManager.instance.Active) {
            return firerequirement / 4;
        }
        return firerequirement;
    }
    
    public void setSpreadDelay(final int spreadDelay) {
        this.SpreadDelay = spreadDelay;
    }
    
    public int getSpreadDelay() {
        return this.SpreadDelay;
    }
    
    public void setLife(final int life) {
        this.Life = life;
    }
    
    public int getLife() {
        return this.Life;
    }
    
    public int getEnergy() {
        return this.Energy;
    }
    
    public boolean isPermanent() {
        return this.perm;
    }
    
    public void setLifeStage(final int n) {
        if (!this.perm) {
            return;
        }
        this.RemoveAttachedAnims();
        switch (n) {
            case 0: {
                this.AttachAnim("Fire", "01", 4, IsoFireManager.FireAnimDelay, -16, -72, true, 0, false, 0.7f, IsoFireManager.FireTintMod);
                break;
            }
            case 1: {
                this.AttachAnim("Smoke", "02", 4, IsoFireManager.SmokeAnimDelay, -9, 12, true, 0, false, 0.7f, IsoFireManager.SmokeTintMod);
                this.AttachAnim("Fire", "02", 4, IsoFireManager.FireAnimDelay, -9, -52, true, 0, false, 0.7f, IsoFireManager.FireTintMod);
                break;
            }
            case 2: {
                this.AttachAnim("Smoke", "03", 4, IsoFireManager.SmokeAnimDelay, 0, 12, true, 0, false, 0.7f, IsoFireManager.SmokeTintMod);
                this.AttachAnim("Fire", "03", 4, IsoFireManager.FireAnimDelay, -16, -72, true, 0, false, 0.7f, IsoFireManager.FireTintMod);
                break;
            }
            case 3: {
                this.AttachAnim("Smoke", "02", 4, IsoFireManager.SmokeAnimDelay, 0, 12, true, 0, false, 0.7f, IsoFireManager.SmokeTintMod);
                this.AttachAnim("Fire", "02", 4, IsoFireManager.FireAnimDelay, -16, -85, true, 0, false, 0.7f, IsoFireManager.FireTintMod);
                break;
            }
            case 4: {
                this.AttachAnim("Smoke", "01", 4, IsoFireManager.SmokeAnimDelay, -9, 12, true, 0, false, 0.7f, IsoFireManager.SmokeTintMod);
                break;
            }
        }
    }
    
    public void setLightRadius(final int lightRadius) {
        this.LightRadius = lightRadius;
        if (this.LightSource != null && lightRadius != this.LightSource.getRadius()) {
            this.getCell().removeLamppost(this.LightSource);
            this.LightSource = new IsoLightSource(this.square.getX(), this.square.getY(), this.square.getZ(), 0.61f, 0.165f, 0.0f, this.LightRadius);
            this.getCell().getLamppostPositions().add(this.LightSource);
            IsoGridSquare.RecalcLightTime = -1;
            GameTime.instance.lightSourceUpdate = 100.0f;
        }
    }
    
    public int getLightRadius() {
        return this.LightRadius;
    }
    
    @Override
    public void addToWorld() {
        if (this.perm) {
            this.getCell().addToStaticUpdaterObjectList(this);
        }
        else {
            IsoFireManager.Add(this);
        }
    }
    
    @Override
    public void removeFromWorld() {
        if (!this.perm) {
            IsoFireManager.Remove(this);
        }
        IsoFireManager.stopSound(this);
        if (this.LightSource != null) {
            this.getCell().removeLamppost(this.LightSource);
            this.LightSource = null;
        }
        if (this.heatSource != null) {
            this.getCell().removeHeatSource(this.heatSource);
            this.heatSource = null;
        }
        super.removeFromWorld();
    }
    
    @Override
    public void saveChange(final String s, final KahluaTable kahluaTable, final ByteBuffer byteBuffer) {
        super.saveChange(s, kahluaTable, byteBuffer);
        if ("Energy".equals(s)) {
            byteBuffer.putInt(this.Energy);
        }
        else if ("lightRadius".equals(s)) {
            byteBuffer.putInt(this.getLightRadius());
        }
    }
    
    @Override
    public void loadChange(final String s, final ByteBuffer byteBuffer) {
        super.loadChange(s, byteBuffer);
        if ("Energy".equals(s)) {
            this.Energy = byteBuffer.getInt();
        }
        if ("lightRadius".equals(s)) {
            this.setLightRadius(byteBuffer.getInt());
        }
    }
}

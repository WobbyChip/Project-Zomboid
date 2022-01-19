// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.objects;

import se.krka.kahlua.vm.KahluaTable;
import zombie.core.opengl.Shader;
import zombie.core.textures.ColorInfo;
import zombie.network.GameServer;
import zombie.network.GameClient;
import zombie.iso.IsoWorld;
import zombie.GameTime;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.core.Core;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.iso.sprite.IsoSpriteManager;
import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.iso.Vector2;
import zombie.inventory.ItemContainer;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoCell;
import zombie.iso.IsoHeatSource;
import zombie.iso.IsoLightSource;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.IsoObject;

public class IsoFireplace extends IsoObject
{
    int FuelAmount;
    boolean bLit;
    boolean bSmouldering;
    protected float LastUpdateTime;
    protected float MinuteAccumulator;
    protected int MinutesSinceExtinguished;
    protected IsoSprite FuelSprite;
    protected int FuelSpriteIndex;
    protected int FireSpriteIndex;
    protected IsoLightSource LightSource;
    protected IsoHeatSource heatSource;
    private static int SMOULDER_MINUTES;
    
    public IsoFireplace(final IsoCell isoCell) {
        super(isoCell);
        this.FuelAmount = 0;
        this.bLit = false;
        this.bSmouldering = false;
        this.LastUpdateTime = -1.0f;
        this.MinuteAccumulator = 0.0f;
        this.MinutesSinceExtinguished = -1;
        this.FuelSprite = null;
        this.FuelSpriteIndex = -1;
        this.FireSpriteIndex = -1;
        this.LightSource = null;
        this.heatSource = null;
    }
    
    public IsoFireplace(final IsoCell isoCell, final IsoGridSquare isoGridSquare, final IsoSprite isoSprite) {
        super(isoCell, isoGridSquare, isoSprite);
        this.FuelAmount = 0;
        this.bLit = false;
        this.bSmouldering = false;
        this.LastUpdateTime = -1.0f;
        this.MinuteAccumulator = 0.0f;
        this.MinutesSinceExtinguished = -1;
        this.FuelSprite = null;
        this.FuelSpriteIndex = -1;
        this.FireSpriteIndex = -1;
        this.LightSource = null;
        this.heatSource = null;
        (this.container = new ItemContainer((isoSprite != null && isoSprite.getProperties().Is(IsoFlagType.container)) ? isoSprite.getProperties().Val("container") : "fireplace", isoGridSquare, this)).setExplored(true);
    }
    
    @Override
    public String getObjectName() {
        return "Fireplace";
    }
    
    @Override
    public Vector2 getFacingPosition(final Vector2 vector2) {
        if (this.square == null) {
            return vector2.set(0.0f, 0.0f);
        }
        if (this.getProperties() != null && this.getProperties().Is(IsoFlagType.collideN)) {
            return vector2.set(this.getX() + 0.5f, this.getY());
        }
        return vector2.set(this.getX(), this.getY() + 0.5f);
    }
    
    @Override
    public void load(final ByteBuffer byteBuffer, final int n, final boolean b) throws IOException {
        super.load(byteBuffer, n, b);
        this.FuelAmount = byteBuffer.getInt();
        this.bLit = (byteBuffer.get() == 1);
        this.LastUpdateTime = byteBuffer.getFloat();
        this.MinutesSinceExtinguished = byteBuffer.getInt();
    }
    
    @Override
    public void save(final ByteBuffer byteBuffer, final boolean b) throws IOException {
        super.save(byteBuffer, b);
        byteBuffer.putInt(this.FuelAmount);
        byteBuffer.put((byte)(this.bLit ? 1 : 0));
        byteBuffer.putFloat(this.LastUpdateTime);
        byteBuffer.putInt(this.MinutesSinceExtinguished);
    }
    
    public void setFuelAmount(int max) {
        max = Math.max(0, max);
        if (max != this.getFuelAmount()) {
            this.FuelAmount = max;
        }
    }
    
    public int getFuelAmount() {
        return this.FuelAmount;
    }
    
    public void addFuel(final int n) {
        this.setFuelAmount(this.getFuelAmount() + n);
    }
    
    public int useFuel(final int n) {
        final int fuelAmount = this.getFuelAmount();
        int n2;
        if (fuelAmount >= n) {
            n2 = n;
        }
        else {
            n2 = fuelAmount;
        }
        this.setFuelAmount(fuelAmount - n2);
        return n2;
    }
    
    public boolean hasFuel() {
        return this.getFuelAmount() > 0;
    }
    
    public void setLit(final boolean bLit) {
        this.bLit = bLit;
    }
    
    public boolean isLit() {
        return this.bLit;
    }
    
    public boolean isSmouldering() {
        return this.bSmouldering;
    }
    
    public void extinguish() {
        if (this.isLit()) {
            this.setLit(false);
            if (this.hasFuel()) {
                this.MinutesSinceExtinguished = 0;
            }
        }
    }
    
    public float getTemperature() {
        if (this.isLit()) {
            return 1.8f;
        }
        return 1.0f;
    }
    
    private void updateFuelSprite() {
        if (this.container != null && "woodstove".equals(this.container.getType())) {
            return;
        }
        if (this.hasFuel()) {
            if (this.FuelSprite == null) {
                (this.FuelSprite = IsoSprite.CreateSprite(IsoSpriteManager.instance)).LoadFrameExplicit("Item_Logs");
            }
            if (this.FuelSpriteIndex == -1) {
                DebugLog.log(DebugType.Fireplace, "fireplace: added fuel sprite");
                this.FuelSpriteIndex = ((this.AttachedAnimSprite != null) ? this.AttachedAnimSprite.size() : 0);
                if (this.getProperties() != null && this.getProperties().Is(IsoFlagType.collideW)) {
                    this.AttachExistingAnim(this.FuelSprite, -10 * Core.TileScale, -90 * Core.TileScale, false, 0, false, 0.0f);
                }
                else {
                    this.AttachExistingAnim(this.FuelSprite, -35 * Core.TileScale, -90 * Core.TileScale, false, 0, false, 0.0f);
                }
                if (Core.TileScale == 1) {
                    this.AttachedAnimSprite.get(this.FuelSpriteIndex).setScale(0.5f, 0.5f);
                }
            }
        }
        else if (this.FuelSpriteIndex != -1) {
            DebugLog.log(DebugType.Fireplace, "fireplace: removed fuel sprite");
            this.AttachedAnimSprite.remove(this.FuelSpriteIndex);
            if (this.FireSpriteIndex > this.FuelSpriteIndex) {
                --this.FireSpriteIndex;
            }
            this.FuelSpriteIndex = -1;
        }
    }
    
    private void updateFireSprite() {
        if (this.container != null && "woodstove".equals(this.container.getType())) {
            return;
        }
        if (this.isLit()) {
            if (this.FireSpriteIndex == -1) {
                DebugLog.log(DebugType.Fireplace, "fireplace: added fire sprite");
                this.FireSpriteIndex = ((this.AttachedAnimSprite != null) ? this.AttachedAnimSprite.size() : 0);
                if (this.getProperties() != null && this.getProperties().Is(IsoFlagType.collideW)) {
                    this.AttachAnim("Fire", "01", 4, IsoFireManager.FireAnimDelay, -11 * Core.TileScale, -84 * Core.TileScale, true, 0, false, 0.7f, IsoFireManager.FireTintMod);
                }
                else {
                    this.AttachAnim("Fire", "01", 4, IsoFireManager.FireAnimDelay, -35 * Core.TileScale, -84 * Core.TileScale, true, 0, false, 0.7f, IsoFireManager.FireTintMod);
                }
                if (Core.TileScale == 1) {
                    this.AttachedAnimSprite.get(this.FireSpriteIndex).setScale(0.5f, 0.5f);
                }
            }
        }
        else if (this.FireSpriteIndex != -1) {
            DebugLog.log(DebugType.Fireplace, "fireplace: removed fire sprite");
            this.AttachedAnimSprite.remove(this.FireSpriteIndex);
            if (this.FuelSpriteIndex > this.FireSpriteIndex) {
                --this.FuelSpriteIndex;
            }
            this.FireSpriteIndex = -1;
        }
    }
    
    private int calcLightRadius() {
        return (int)GameTime.instance.Lerp(1.0f, 8.0f, Math.min(this.getFuelAmount(), 60) / 60.0f);
    }
    
    private void updateLightSource() {
        if (this.isLit()) {
            final int calcLightRadius = this.calcLightRadius();
            if (this.LightSource != null && this.LightSource.getRadius() != calcLightRadius) {
                this.LightSource.life = 0;
                this.LightSource = null;
            }
            if (this.LightSource == null) {
                this.LightSource = new IsoLightSource(this.square.getX(), this.square.getY(), this.square.getZ(), 1.0f, 0.1f, 0.1f, calcLightRadius);
                IsoWorld.instance.CurrentCell.addLamppost(this.LightSource);
                IsoGridSquare.RecalcLightTime = -1;
                GameTime.instance.lightSourceUpdate = 100.0f;
            }
        }
        else if (this.LightSource != null) {
            IsoWorld.instance.CurrentCell.removeLamppost(this.LightSource);
            this.LightSource = null;
        }
    }
    
    private void updateHeatSource() {
        if (this.isLit()) {
            final int calcLightRadius = this.calcLightRadius();
            if (this.heatSource == null) {
                this.heatSource = new IsoHeatSource((int)this.getX(), (int)this.getY(), (int)this.getZ(), calcLightRadius, 35);
                IsoWorld.instance.CurrentCell.addHeatSource(this.heatSource);
            }
            else if (calcLightRadius != this.heatSource.getRadius()) {
                this.heatSource.setRadius(calcLightRadius);
            }
        }
        else if (this.heatSource != null) {
            IsoWorld.instance.CurrentCell.removeHeatSource(this.heatSource);
            this.heatSource = null;
        }
    }
    
    @Override
    public void update() {
        if (!GameClient.bClient) {
            final boolean hasFuel = this.hasFuel();
            final boolean lit = this.isLit();
            final int calcLightRadius = this.calcLightRadius();
            final float lastUpdateTime = (float)GameTime.getInstance().getWorldAgeHours();
            if (this.LastUpdateTime < 0.0f) {
                this.LastUpdateTime = lastUpdateTime;
            }
            else if (this.LastUpdateTime > lastUpdateTime) {
                this.LastUpdateTime = lastUpdateTime;
            }
            if (lastUpdateTime > this.LastUpdateTime) {
                this.MinuteAccumulator += (lastUpdateTime - this.LastUpdateTime) * 60.0f;
                final int a = (int)Math.floor(this.MinuteAccumulator);
                if (a > 0) {
                    if (this.isLit()) {
                        DebugLog.log(DebugType.Fireplace, invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, a, this.getFuelAmount()));
                        this.useFuel(a);
                        if (!this.hasFuel()) {
                            this.extinguish();
                        }
                    }
                    else if (this.MinutesSinceExtinguished != -1) {
                        final int min = Math.min(a, IsoFireplace.SMOULDER_MINUTES - this.MinutesSinceExtinguished);
                        DebugLog.log(DebugType.Fireplace, invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, min, this.getFuelAmount()));
                        this.MinutesSinceExtinguished += a;
                        this.useFuel(min);
                        this.bSmouldering = true;
                        if (!this.hasFuel() || this.MinutesSinceExtinguished >= IsoFireplace.SMOULDER_MINUTES) {
                            this.MinutesSinceExtinguished = -1;
                            this.bSmouldering = false;
                        }
                    }
                    this.MinuteAccumulator -= a;
                }
            }
            this.LastUpdateTime = lastUpdateTime;
            if (GameServer.bServer) {
                if (hasFuel != this.hasFuel() || lit != this.isLit() || calcLightRadius != this.calcLightRadius()) {
                    this.sendObjectChange("state");
                }
                return;
            }
        }
        this.updateFuelSprite();
        this.updateFireSprite();
        this.updateLightSource();
        this.updateHeatSource();
        if (this.AttachedAnimSprite != null && !this.AttachedAnimSprite.isEmpty()) {
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
        }
    }
    
    @Override
    public void addToWorld() {
        this.getCell().addToProcessIsoObject(this);
        this.container.addItemsToProcessItems();
    }
    
    @Override
    public void removeFromWorld() {
        if (this.LightSource != null) {
            IsoWorld.instance.CurrentCell.removeLamppost(this.LightSource);
            this.LightSource = null;
        }
        if (this.heatSource != null) {
            IsoWorld.instance.CurrentCell.removeHeatSource(this.heatSource);
            this.heatSource = null;
        }
        super.removeFromWorld();
    }
    
    @Override
    public void render(final float n, final float n2, final float n3, final ColorInfo colorInfo, final boolean b, final boolean b2, final Shader shader) {
        super.render(n, n2, n3, colorInfo, false, b2, shader);
        if (this.AttachedAnimSprite == null) {
            return;
        }
        for (int i = 0; i < this.AttachedAnimSprite.size(); ++i) {
            final IsoSpriteInstance isoSpriteInstance = this.AttachedAnimSprite.get(i);
            isoSpriteInstance.getParentSprite().render(isoSpriteInstance, this, n, n2, n3, this.dir, this.offsetX, this.offsetY, colorInfo, true);
        }
    }
    
    @Override
    public void saveChange(final String anObject, final KahluaTable kahluaTable, final ByteBuffer byteBuffer) {
        if ("state".equals(anObject)) {
            byteBuffer.putInt(this.getFuelAmount());
            byteBuffer.put((byte)(this.isLit() ? 1 : 0));
        }
    }
    
    @Override
    public void loadChange(final String anObject, final ByteBuffer byteBuffer) {
        if ("state".equals(anObject)) {
            this.setFuelAmount(byteBuffer.getInt());
            this.setLit(byteBuffer.get() == 1);
        }
    }
    
    static {
        IsoFireplace.SMOULDER_MINUTES = 10;
    }
}

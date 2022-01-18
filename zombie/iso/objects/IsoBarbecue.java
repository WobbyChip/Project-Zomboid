// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.objects;

import se.krka.kahlua.vm.KahluaTable;
import zombie.core.Core;
import zombie.core.opengl.Shader;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.core.textures.ColorInfo;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.GameTime;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.iso.IsoWorld;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.DrainableComboItem;
import zombie.inventory.InventoryItem;
import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.inventory.ItemContainer;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoCell;
import zombie.iso.IsoHeatSource;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.IsoObject;

public class IsoBarbecue extends IsoObject
{
    boolean bHasPropaneTank;
    int FuelAmount;
    boolean bLit;
    boolean bIsSmouldering;
    protected float LastUpdateTime;
    protected float MinuteAccumulator;
    protected int MinutesSinceExtinguished;
    IsoSprite normalSprite;
    IsoSprite noTankSprite;
    private IsoHeatSource heatSource;
    private long soundInstance;
    private static int SMOULDER_MINUTES;
    
    public IsoBarbecue(final IsoCell isoCell) {
        super(isoCell);
        this.bHasPropaneTank = false;
        this.FuelAmount = 0;
        this.bLit = false;
        this.bIsSmouldering = false;
        this.LastUpdateTime = -1.0f;
        this.MinuteAccumulator = 0.0f;
        this.MinutesSinceExtinguished = -1;
        this.normalSprite = null;
        this.noTankSprite = null;
        this.soundInstance = 0L;
    }
    
    public IsoBarbecue(final IsoCell isoCell, final IsoGridSquare isoGridSquare, final IsoSprite isoSprite) {
        super(isoCell, isoGridSquare, isoSprite);
        this.bHasPropaneTank = false;
        this.FuelAmount = 0;
        this.bLit = false;
        this.bIsSmouldering = false;
        this.LastUpdateTime = -1.0f;
        this.MinuteAccumulator = 0.0f;
        this.MinutesSinceExtinguished = -1;
        this.normalSprite = null;
        this.noTankSprite = null;
        this.soundInstance = 0L;
        (this.container = new ItemContainer("barbecue", isoGridSquare, this)).setExplored(true);
        if (isSpriteWithPropaneTank(this.sprite)) {
            this.bHasPropaneTank = true;
            this.FuelAmount = 1200;
            final int n = 8;
            this.normalSprite = this.sprite;
            this.noTankSprite = IsoSprite.getSprite(IsoSpriteManager.instance, this.sprite, n);
        }
        else if (isSpriteWithoutPropaneTank(this.sprite)) {
            this.normalSprite = IsoSprite.getSprite(IsoSpriteManager.instance, this.sprite, -8);
            this.noTankSprite = this.sprite;
        }
    }
    
    @Override
    public String getObjectName() {
        return "Barbecue";
    }
    
    @Override
    public void load(final ByteBuffer byteBuffer, final int n, final boolean b) throws IOException {
        super.load(byteBuffer, n, b);
        this.bHasPropaneTank = (byteBuffer.get() == 1);
        this.FuelAmount = byteBuffer.getInt();
        this.bLit = (byteBuffer.get() == 1);
        this.LastUpdateTime = byteBuffer.getFloat();
        this.MinutesSinceExtinguished = byteBuffer.getInt();
        if (byteBuffer.get() == 1) {
            this.normalSprite = IsoSprite.getSprite(IsoSpriteManager.instance, byteBuffer.getInt());
        }
        if (byteBuffer.get() == 1) {
            this.noTankSprite = IsoSprite.getSprite(IsoSpriteManager.instance, byteBuffer.getInt());
        }
    }
    
    @Override
    public void save(final ByteBuffer byteBuffer, final boolean b) throws IOException {
        super.save(byteBuffer, b);
        byteBuffer.put((byte)(this.bHasPropaneTank ? 1 : 0));
        byteBuffer.putInt(this.FuelAmount);
        byteBuffer.put((byte)(this.bLit ? 1 : 0));
        byteBuffer.putFloat(this.LastUpdateTime);
        byteBuffer.putInt(this.MinutesSinceExtinguished);
        if (this.normalSprite != null) {
            byteBuffer.put((byte)1);
            byteBuffer.putInt(this.normalSprite.ID);
        }
        else {
            byteBuffer.put((byte)0);
        }
        if (this.noTankSprite != null) {
            byteBuffer.put((byte)1);
            byteBuffer.putInt(this.noTankSprite.ID);
        }
        else {
            byteBuffer.put((byte)0);
        }
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
    
    public boolean hasPropaneTank() {
        return this.isPropaneBBQ() && this.bHasPropaneTank;
    }
    
    public boolean isPropaneBBQ() {
        return this.getSprite() != null && this.getProperties().Is("propaneTank");
    }
    
    public static boolean isSpriteWithPropaneTank(final IsoSprite isoSprite) {
        if (isoSprite == null || !isoSprite.getProperties().Is("propaneTank")) {
            return false;
        }
        final IsoSprite sprite = IsoSprite.getSprite(IsoSpriteManager.instance, isoSprite, 8);
        return sprite != null && sprite.getProperties().Is("propaneTank");
    }
    
    public static boolean isSpriteWithoutPropaneTank(final IsoSprite isoSprite) {
        if (isoSprite == null || !isoSprite.getProperties().Is("propaneTank")) {
            return false;
        }
        final IsoSprite sprite = IsoSprite.getSprite(IsoSpriteManager.instance, isoSprite, -8);
        return sprite != null && sprite.getProperties().Is("propaneTank");
    }
    
    public void setPropaneTank(final InventoryItem inventoryItem) {
        if (inventoryItem.getFullType().equals("Base.PropaneTank")) {
            this.bHasPropaneTank = true;
            this.FuelAmount = 1200;
            if (inventoryItem instanceof DrainableComboItem) {
                this.FuelAmount *= (int)((DrainableComboItem)inventoryItem).getUsedDelta();
            }
        }
    }
    
    public InventoryItem removePropaneTank() {
        if (!this.bHasPropaneTank) {
            return null;
        }
        this.bHasPropaneTank = false;
        this.bLit = false;
        final InventoryItem createItem = InventoryItemFactory.CreateItem("Base.PropaneTank");
        if (createItem instanceof DrainableComboItem) {
            ((DrainableComboItem)createItem).setUsedDelta(this.getFuelAmount() / 1200.0f);
        }
        this.FuelAmount = 0;
        return createItem;
    }
    
    public void setLit(final boolean bLit) {
        this.bLit = bLit;
    }
    
    public boolean isLit() {
        return this.bLit;
    }
    
    public boolean isSmouldering() {
        return this.bIsSmouldering;
    }
    
    public void turnOn() {
        if (!this.isLit()) {
            this.setLit(true);
        }
    }
    
    public void turnOff() {
        if (this.isLit()) {
            this.setLit(false);
        }
    }
    
    public void toggle() {
        this.setLit(!this.isLit());
    }
    
    public void extinguish() {
        if (this.isLit()) {
            this.setLit(false);
            if (this.hasFuel() && !this.isPropaneBBQ()) {
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
    
    private void updateSprite() {
        if (this.isPropaneBBQ()) {
            if (this.hasPropaneTank()) {
                this.sprite = this.normalSprite;
            }
            else {
                this.sprite = this.noTankSprite;
            }
        }
    }
    
    private void updateHeatSource() {
        if (this.isLit()) {
            if (this.heatSource == null) {
                this.heatSource = new IsoHeatSource((int)this.getX(), (int)this.getY(), (int)this.getZ(), 3, 25);
                IsoWorld.instance.CurrentCell.addHeatSource(this.heatSource);
            }
        }
        else if (this.heatSource != null) {
            IsoWorld.instance.CurrentCell.removeHeatSource(this.heatSource);
            this.heatSource = null;
        }
    }
    
    private void updateSound() {
        if (GameServer.bServer) {
            return;
        }
        if (this.isLit()) {
            if (this.emitter == null) {
                this.emitter = IsoWorld.instance.getFreeEmitter(this.getX() + 0.5f, this.getY() + 0.5f, (float)(int)this.getZ());
                IsoWorld.instance.setEmitterOwner(this.emitter, this);
            }
            final String s = this.isPropaneBBQ() ? "PropaneBarbecueRunning" : "CharcoalBarbecueRunning";
            if (!this.emitter.isPlaying(s)) {
                this.soundInstance = this.emitter.playSoundLoopedImpl(s);
            }
        }
        else if (this.emitter != null && this.soundInstance != 0L) {
            this.emitter.stopSound(this.soundInstance);
            this.emitter = null;
            this.soundInstance = 0L;
        }
    }
    
    @Override
    public void update() {
        if (!GameClient.bClient) {
            final boolean hasFuel = this.hasFuel();
            final boolean lit = this.isLit();
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
                        final int min = Math.min(a, IsoBarbecue.SMOULDER_MINUTES - this.MinutesSinceExtinguished);
                        DebugLog.log(DebugType.Fireplace, invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, min, this.getFuelAmount()));
                        this.MinutesSinceExtinguished += a;
                        this.bIsSmouldering = true;
                        this.useFuel(min);
                        if (!this.hasFuel() || this.MinutesSinceExtinguished >= IsoBarbecue.SMOULDER_MINUTES) {
                            this.MinutesSinceExtinguished = -1;
                            this.bIsSmouldering = false;
                        }
                    }
                    this.MinuteAccumulator -= a;
                }
            }
            this.LastUpdateTime = lastUpdateTime;
            if (GameServer.bServer) {
                if (hasFuel != this.hasFuel() || lit != this.isLit()) {
                    this.sendObjectChange("state");
                }
                return;
            }
        }
        this.updateSprite();
        this.updateHeatSource();
        if (this.isLit() && (this.AttachedAnimSprite == null || this.AttachedAnimSprite.isEmpty())) {
            this.AttachAnim("Smoke", "01", 4, IsoFireManager.SmokeAnimDelay, -14, 58, true, 0, false, 0.7f, new ColorInfo(0.95f, 0.95f, 0.85f, 1.0f));
            final IsoSpriteInstance isoSpriteInstance = this.AttachedAnimSprite.get(0);
            final IsoSpriteInstance isoSpriteInstance2 = this.AttachedAnimSprite.get(0);
            final float n = 0.55f;
            isoSpriteInstance2.targetAlpha = n;
            isoSpriteInstance.alpha = n;
            this.AttachedAnimSprite.get(0).bCopyTargetAlpha = false;
        }
        else if (!this.isLit() && this.AttachedAnimSprite != null && !this.AttachedAnimSprite.isEmpty()) {
            this.RemoveAttachedAnims();
        }
        if (this.AttachedAnimSprite != null && !this.AttachedAnimSprite.isEmpty()) {
            for (int size = this.AttachedAnimSprite.size(), i = 0; i < size; ++i) {
                final IsoSpriteInstance isoSpriteInstance3 = this.AttachedAnimSprite.get(i);
                final IsoSprite parentSprite = isoSpriteInstance3.parentSprite;
                isoSpriteInstance3.update();
                final float n2 = GameTime.instance.getMultipliedSecondsSinceLastUpdate() * 60.0f;
                final IsoSpriteInstance isoSpriteInstance4 = isoSpriteInstance3;
                isoSpriteInstance4.Frame += isoSpriteInstance3.AnimFrameIncrease * n2;
                if ((int)isoSpriteInstance3.Frame >= parentSprite.CurrentAnim.Frames.size() && parentSprite.Loop && isoSpriteInstance3.Looped) {
                    isoSpriteInstance3.Frame = 0.0f;
                }
            }
        }
        this.updateSound();
    }
    
    @Override
    public void setSprite(final IsoSprite isoSprite) {
        if (isSpriteWithPropaneTank(isoSprite)) {
            final int n = 8;
            this.normalSprite = isoSprite;
            this.noTankSprite = IsoSprite.getSprite(IsoSpriteManager.instance, isoSprite, n);
        }
        else if (isSpriteWithoutPropaneTank(isoSprite)) {
            this.normalSprite = IsoSprite.getSprite(IsoSpriteManager.instance, isoSprite, -8);
            this.noTankSprite = isoSprite;
        }
    }
    
    @Override
    public void addToWorld() {
        this.getCell();
        this.getCell().addToProcessIsoObject(this);
        this.container.addItemsToProcessItems();
    }
    
    @Override
    public void removeFromWorld() {
        if (this.heatSource != null) {
            IsoWorld.instance.CurrentCell.removeHeatSource(this.heatSource);
            this.heatSource = null;
        }
        super.removeFromWorld();
    }
    
    @Override
    public void render(final float n, final float n2, final float n3, final ColorInfo colorInfo, final boolean b, final boolean b2, final Shader shader) {
        if (this.AttachedAnimSprite != null) {
            final int tileScale = Core.TileScale;
            for (int i = 0; i < this.AttachedAnimSprite.size(); ++i) {
                final IsoSprite parentSprite = this.AttachedAnimSprite.get(i).parentSprite;
                parentSprite.soffX = (short)(14 * tileScale);
                parentSprite.soffY = (short)(-58 * tileScale);
                this.AttachedAnimSprite.get(i).setScale((float)tileScale, (float)tileScale);
            }
        }
        super.render(n, n2, n3, colorInfo, b, b2, shader);
    }
    
    @Override
    public void saveChange(final String anObject, final KahluaTable kahluaTable, final ByteBuffer byteBuffer) {
        if ("state".equals(anObject)) {
            byteBuffer.putInt(this.getFuelAmount());
            byteBuffer.put((byte)(this.isLit() ? 1 : 0));
            byteBuffer.put((byte)(this.hasPropaneTank() ? 1 : 0));
        }
    }
    
    @Override
    public void loadChange(final String anObject, final ByteBuffer byteBuffer) {
        if ("state".equals(anObject)) {
            this.setFuelAmount(byteBuffer.getInt());
            this.setLit(byteBuffer.get() == 1);
            this.bHasPropaneTank = (byteBuffer.get() == 1);
        }
    }
    
    static {
        IsoBarbecue.SMOULDER_MINUTES = 10;
    }
}

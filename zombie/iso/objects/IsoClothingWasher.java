// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.objects;

import zombie.inventory.ItemContainer;
import zombie.WorldSoundManager;
import zombie.iso.IsoWorld;
import zombie.network.GameServer;
import se.krka.kahlua.vm.KahluaTable;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.characterTextures.BloodClothingType;
import zombie.inventory.types.Clothing;
import zombie.inventory.InventoryItem;
import zombie.GameTime;
import zombie.network.GameClient;
import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoCell;
import zombie.characterTextures.BloodBodyPartType;
import java.util.ArrayList;
import zombie.iso.IsoObject;

public class IsoClothingWasher extends IsoObject
{
    private boolean bActivated;
    private long soundInstance;
    private float lastUpdate;
    private boolean cycleFinished;
    private float startTime;
    private float cycleLengthMinutes;
    private boolean alreadyExecuted;
    private static final ArrayList<BloodBodyPartType> coveredParts;
    
    public IsoClothingWasher(final IsoCell isoCell) {
        super(isoCell);
        this.soundInstance = -1L;
        this.lastUpdate = -1.0f;
        this.cycleFinished = false;
        this.startTime = 0.0f;
        this.cycleLengthMinutes = 90.0f;
        this.alreadyExecuted = false;
    }
    
    public IsoClothingWasher(final IsoCell isoCell, final IsoGridSquare isoGridSquare, final IsoSprite isoSprite) {
        super(isoCell, isoGridSquare, isoSprite);
        this.soundInstance = -1L;
        this.lastUpdate = -1.0f;
        this.cycleFinished = false;
        this.startTime = 0.0f;
        this.cycleLengthMinutes = 90.0f;
        this.alreadyExecuted = false;
    }
    
    @Override
    public String getObjectName() {
        return "ClothingWasher";
    }
    
    @Override
    public void load(final ByteBuffer byteBuffer, final int n, final boolean b) throws IOException {
        super.load(byteBuffer, n, b);
        this.bActivated = (byteBuffer.get() == 1);
        this.lastUpdate = byteBuffer.getFloat();
    }
    
    @Override
    public void save(final ByteBuffer byteBuffer, final boolean b) throws IOException {
        super.save(byteBuffer, b);
        byteBuffer.put((byte)(this.isActivated() ? 1 : 0));
        byteBuffer.putFloat(this.lastUpdate);
    }
    
    @Override
    public void update() {
        if (this.getObjectIndex() == -1) {
            return;
        }
        if (!this.container.isPowered()) {
            this.setActivated(false);
        }
        this.updateSound();
        this.cycleFinished();
        if (GameClient.bClient) {}
        if (this.getWaterAmount() <= 0) {
            this.setActivated(false);
        }
        if (!this.isActivated()) {
            this.lastUpdate = -1.0f;
            return;
        }
        final float lastUpdate = (float)GameTime.getInstance().getWorldAgeHours();
        if (this.lastUpdate < 0.0f) {
            this.lastUpdate = lastUpdate;
        }
        else if (this.lastUpdate > lastUpdate) {
            this.lastUpdate = lastUpdate;
        }
        final int n = (int)((lastUpdate - this.lastUpdate) * 60.0f);
        if (n < 1) {
            return;
        }
        this.lastUpdate = lastUpdate;
        this.useWater(1 * n);
        for (int i = 0; i < this.container.getItems().size(); ++i) {
            final InventoryItem inventoryItem = this.container.getItems().get(i);
            if (inventoryItem instanceof Clothing) {
                final Clothing clothing = (Clothing)inventoryItem;
                if (clothing.getBloodlevel() > 0.0f) {
                    this.removeBlood(clothing, (float)(n * 2));
                }
                if (clothing.getDirtyness() > 0.0f) {
                    this.removeDirt(clothing, (float)(n * 2));
                }
                clothing.setWetness(100.0f);
            }
        }
    }
    
    private void removeBlood(final Clothing clothing, final float n) {
        final ItemVisual visual = clothing.getVisual();
        if (visual == null) {
            return;
        }
        for (int i = 0; i < BloodBodyPartType.MAX.index(); ++i) {
            final BloodBodyPartType fromIndex = BloodBodyPartType.FromIndex(i);
            final float blood = visual.getBlood(fromIndex);
            if (blood > 0.0f) {
                visual.setBlood(fromIndex, blood - n / 100.0f);
            }
        }
        BloodClothingType.calcTotalBloodLevel(clothing);
    }
    
    private void removeDirt(final Clothing clothing, final float n) {
        final ItemVisual visual = clothing.getVisual();
        if (visual == null) {
            return;
        }
        for (int i = 0; i < BloodBodyPartType.MAX.index(); ++i) {
            final BloodBodyPartType fromIndex = BloodBodyPartType.FromIndex(i);
            final float dirt = visual.getDirt(fromIndex);
            if (dirt > 0.0f) {
                visual.setDirt(fromIndex, dirt - n / 100.0f);
            }
        }
        BloodClothingType.calcTotalDirtLevel(clothing);
    }
    
    @Override
    public void addToWorld() {
        this.getCell().addToProcessIsoObject(this);
    }
    
    @Override
    public void removeFromWorld() {
        super.removeFromWorld();
    }
    
    @Override
    public void saveChange(final String anObject, final KahluaTable kahluaTable, final ByteBuffer byteBuffer) {
        if ("state".equals(anObject)) {
            byteBuffer.put((byte)(this.isActivated() ? 1 : 0));
        }
    }
    
    @Override
    public void loadChange(final String anObject, final ByteBuffer byteBuffer) {
        if ("state".equals(anObject)) {
            this.setActivated(byteBuffer.get() == 1);
        }
    }
    
    private void updateSound() {
        if (this.isActivated()) {
            if (!GameServer.bServer) {
                if (this.emitter != null && this.emitter.isPlaying("ClothingWasherFinished")) {
                    this.emitter.stopSoundByName("ClothingWasherFinished");
                }
                if (this.soundInstance == -1L) {
                    this.emitter = IsoWorld.instance.getFreeEmitter(this.getX() + 0.5f, this.getY() + 0.5f, (float)(int)this.getZ());
                    this.soundInstance = this.emitter.playSoundLoopedImpl("ClothingWasherRunning");
                }
            }
            if (!GameClient.bClient) {
                WorldSoundManager.instance.addSound(this, this.square.x, this.square.y, this.square.z, 10, 10);
            }
        }
        else if (this.soundInstance != -1L) {
            this.emitter.stopSound(this.soundInstance);
            this.soundInstance = -1L;
            if (this.cycleFinished) {
                this.cycleFinished = false;
                this.emitter.playSoundImpl("ClothingWasherFinished", this);
            }
            else {
                this.emitter = null;
            }
        }
    }
    
    @Override
    public boolean isItemAllowedInContainer(final ItemContainer itemContainer, final InventoryItem inventoryItem) {
        return !this.isActivated();
    }
    
    @Override
    public boolean isRemoveItemAllowedFromContainer(final ItemContainer itemContainer, final InventoryItem inventoryItem) {
        return (this.container.Items.size() <= 0 || !this.isActivated()) && this.container == itemContainer;
    }
    
    private boolean cycleFinished() {
        if (this.isActivated()) {
            if (!this.alreadyExecuted) {
                this.startTime = (float)GameTime.getInstance().getWorldAgeHours();
                this.alreadyExecuted = true;
            }
            if ((int)(((float)GameTime.getInstance().getWorldAgeHours() - this.startTime) * 60.0f) < this.cycleLengthMinutes) {
                return false;
            }
            this.cycleFinished = true;
            this.setActivated(false);
        }
        return true;
    }
    
    public boolean isActivated() {
        return this.bActivated;
    }
    
    public void setActivated(final boolean bActivated) {
        this.bActivated = bActivated;
        this.alreadyExecuted = false;
    }
    
    static {
        coveredParts = new ArrayList<BloodBodyPartType>();
    }
}

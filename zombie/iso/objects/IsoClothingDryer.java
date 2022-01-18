// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.objects;

import zombie.inventory.ItemContainer;
import zombie.WorldSoundManager;
import se.krka.kahlua.vm.KahluaTable;
import zombie.iso.IsoWorld;
import zombie.inventory.InventoryItemFactory;
import zombie.network.GameServer;
import zombie.inventory.types.Clothing;
import zombie.inventory.InventoryItem;
import zombie.GameTime;
import zombie.network.GameClient;
import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoCell;
import zombie.iso.IsoObject;

public class IsoClothingDryer extends IsoObject
{
    private boolean bActivated;
    private long SoundInstance;
    private float lastUpdate;
    private boolean cycleFinished;
    private float startTime;
    private float cycleLengthMinutes;
    private boolean alreadyExecuted;
    
    public IsoClothingDryer(final IsoCell isoCell) {
        super(isoCell);
        this.SoundInstance = -1L;
        this.lastUpdate = -1.0f;
        this.cycleFinished = false;
        this.startTime = 0.0f;
        this.cycleLengthMinutes = 90.0f;
        this.alreadyExecuted = false;
    }
    
    public IsoClothingDryer(final IsoCell isoCell, final IsoGridSquare isoGridSquare, final IsoSprite isoSprite) {
        super(isoCell, isoGridSquare, isoSprite);
        this.SoundInstance = -1L;
        this.lastUpdate = -1.0f;
        this.cycleFinished = false;
        this.startTime = 0.0f;
        this.cycleLengthMinutes = 90.0f;
        this.alreadyExecuted = false;
    }
    
    @Override
    public String getObjectName() {
        return "ClothingDryer";
    }
    
    @Override
    public void load(final ByteBuffer byteBuffer, final int n, final boolean b) throws IOException {
        super.load(byteBuffer, n, b);
        this.bActivated = (byteBuffer.get() == 1);
    }
    
    @Override
    public void save(final ByteBuffer byteBuffer, final boolean b) throws IOException {
        super.save(byteBuffer, b);
        byteBuffer.put((byte)(this.isActivated() ? 1 : 0));
    }
    
    @Override
    public void update() {
        if (this.getObjectIndex() == -1) {
            return;
        }
        if (this.container == null) {
            return;
        }
        if (!this.container.isPowered()) {
            this.setActivated(false);
        }
        this.cycleFinished();
        this.updateSound();
        if (GameClient.bClient) {}
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
        for (int i = 0; i < this.container.getItems().size(); ++i) {
            final InventoryItem inventoryItem = this.container.getItems().get(i);
            if (inventoryItem instanceof Clothing) {
                final Clothing clothing = (Clothing)inventoryItem;
                final float wetness = clothing.getWetness();
                if (wetness > 0.0f) {
                    clothing.setWetness(wetness - n);
                    if (GameServer.bServer) {}
                }
            }
            if (inventoryItem.isWet() && inventoryItem.getItemWhenDry() != null) {
                inventoryItem.setWetCooldown(inventoryItem.getWetCooldown() - n * 250);
                if (inventoryItem.getWetCooldown() <= 0.0f) {
                    this.getContainer().addItem(InventoryItemFactory.CreateItem(inventoryItem.getItemWhenDry()));
                    this.getContainer().Remove(inventoryItem);
                    --i;
                    inventoryItem.setWet(false);
                    IsoWorld.instance.CurrentCell.addToProcessItemsRemove(inventoryItem);
                }
            }
        }
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
                if (this.emitter != null && this.emitter.isPlaying("ClothingDryerFinished")) {
                    this.emitter.stopSoundByName("ClothingDryerFinished");
                }
                if (this.SoundInstance == -1L) {
                    this.emitter = IsoWorld.instance.getFreeEmitter(this.getX() + 0.5f, this.getY() + 0.5f, (float)(int)this.getZ());
                    this.SoundInstance = this.emitter.playSoundLoopedImpl("ClothingDryerRunning");
                }
            }
            if (!GameClient.bClient) {
                WorldSoundManager.instance.addSound(this, this.square.x, this.square.y, this.square.z, 10, 10);
            }
        }
        else if (this.SoundInstance != -1L) {
            this.emitter.stopSound(this.SoundInstance);
            this.SoundInstance = -1L;
            if (this.cycleFinished) {
                this.cycleFinished = false;
                this.emitter.playSoundImpl("ClothingDryerFinished", this);
            }
            else {
                this.emitter = null;
            }
        }
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
    
    @Override
    public boolean isItemAllowedInContainer(final ItemContainer itemContainer, final InventoryItem inventoryItem) {
        return !this.isActivated();
    }
    
    @Override
    public boolean isRemoveItemAllowedFromContainer(final ItemContainer itemContainer, final InventoryItem inventoryItem) {
        return (this.container.Items.size() <= 0 || !this.isActivated()) && this.container == itemContainer;
    }
    
    public boolean isActivated() {
        return this.bActivated;
    }
    
    public void setActivated(final boolean bActivated) {
        this.bActivated = bActivated;
        this.alreadyExecuted = false;
    }
}

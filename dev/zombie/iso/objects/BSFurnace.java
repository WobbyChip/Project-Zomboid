// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.objects;

import zombie.core.raknet.UdpConnection;
import zombie.network.GameServer;
import zombie.iso.IsoWorld;
import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.characters.skills.PerkFactory;
import zombie.inventory.InventoryItemFactory;
import zombie.characters.IsoPlayer;
import zombie.GameTime;
import zombie.inventory.types.DrainableComboItem;
import zombie.inventory.InventoryItem;
import zombie.network.GameClient;
import zombie.inventory.ItemContainer;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoCell;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoObject;

public class BSFurnace extends IsoObject
{
    public float heat;
    public float heatDecrease;
    public float heatIncrease;
    public float fuelAmount;
    public float fuelDecrease;
    public boolean fireStarted;
    private IsoLightSource LightSource;
    public String sSprite;
    public String sLitSprite;
    
    public BSFurnace(final IsoCell isoCell) {
        super(isoCell);
        this.heat = 0.0f;
        this.heatDecrease = 0.005f;
        this.heatIncrease = 0.001f;
        this.fuelAmount = 0.0f;
        this.fuelDecrease = 0.001f;
        this.fireStarted = false;
    }
    
    public BSFurnace(final IsoCell isoCell, final IsoGridSquare square, final String sSprite, final String sLitSprite) {
        super(isoCell, square, IsoSpriteManager.instance.getSprite(sSprite));
        this.heat = 0.0f;
        this.heatDecrease = 0.005f;
        this.heatIncrease = 0.001f;
        this.fuelAmount = 0.0f;
        this.fuelDecrease = 0.001f;
        this.fireStarted = false;
        this.sSprite = sSprite;
        this.sLitSprite = sLitSprite;
        this.sprite = IsoSpriteManager.instance.getSprite(sSprite);
        this.square = square;
        (this.container = new ItemContainer()).setType("stonefurnace");
        this.container.setParent(this);
        square.AddSpecialObject(this);
    }
    
    @Override
    public void update() {
        this.updateHeat();
        if (GameClient.bClient) {
            return;
        }
        DrainableComboItem drainableComboItem = null;
        InventoryItem inventoryItem = null;
        for (int i = 0; i < this.getContainer().getItems().size(); ++i) {
            final InventoryItem inventoryItem2 = this.getContainer().getItems().get(i);
            if (inventoryItem2.getType().equals("IronIngot") && ((DrainableComboItem)inventoryItem2).getUsedDelta() < 1.0f) {
                drainableComboItem = (DrainableComboItem)inventoryItem2;
            }
            if (inventoryItem2.getMetalValue() > 0.0f) {
                if (this.getHeat() > 15.0f) {
                    if (inventoryItem2.getItemHeat() < 2.0f) {
                        inventoryItem2.setItemHeat(inventoryItem2.getItemHeat() + 0.001f * (this.getHeat() / 100.0f) * GameTime.instance.getMultiplier());
                    }
                    else {
                        inventoryItem2.setMeltingTime(inventoryItem2.getMeltingTime() + 0.1f * (this.getHeat() / 100.0f) * (1.0f + this.getMeltingSkill(inventoryItem2) * 3 / 100.0f) * GameTime.instance.getMultiplier());
                    }
                    if (inventoryItem2.getMeltingTime() == 100.0f) {
                        inventoryItem = inventoryItem2;
                    }
                }
                else {
                    inventoryItem2.setItemHeat(inventoryItem2.getItemHeat() - 0.001f * (this.getHeat() / 100.0f) * GameTime.instance.getMultiplier());
                    inventoryItem2.setMeltingTime(inventoryItem2.getMeltingTime() - 0.1f * (this.getHeat() / 100.0f) * GameTime.instance.getMultiplier());
                }
            }
        }
        if (inventoryItem != null) {
            if (inventoryItem.getWorker() != null && !inventoryItem.getWorker().isEmpty()) {
                for (int j = 0; j < IsoPlayer.numPlayers; ++j) {
                    final IsoPlayer isoPlayer = IsoPlayer.players[j];
                    if (isoPlayer != null && !isoPlayer.isDead() && inventoryItem.getWorker().equals(isoPlayer.getFullName())) {
                        break;
                    }
                }
            }
            float n = inventoryItem.getMetalValue() + (inventoryItem.getMetalValue() * (1.0f + this.getMeltingSkill(inventoryItem) * 3 / 100.0f) - inventoryItem.getMetalValue());
            if (drainableComboItem != null) {
                if (n + drainableComboItem.getUsedDelta() / drainableComboItem.getUseDelta() > 1.0f / drainableComboItem.getUseDelta()) {
                    n -= 1.0f / drainableComboItem.getUseDelta() - drainableComboItem.getUsedDelta() / drainableComboItem.getUseDelta();
                    drainableComboItem.setUsedDelta(1.0f);
                    drainableComboItem = (DrainableComboItem)InventoryItemFactory.CreateItem("Base.IronIngot");
                    drainableComboItem.setUsedDelta(0.0f);
                    this.getContainer().addItem(drainableComboItem);
                }
            }
            else {
                drainableComboItem = (DrainableComboItem)InventoryItemFactory.CreateItem("Base.IronIngot");
                drainableComboItem.setUsedDelta(0.0f);
                this.getContainer().addItem(drainableComboItem);
            }
            float n2 = 0.0f;
            while (n2 < n) {
                if (drainableComboItem.getUsedDelta() + n * drainableComboItem.getUseDelta() <= 1.0f) {
                    drainableComboItem.setUsedDelta(drainableComboItem.getUsedDelta() + n * drainableComboItem.getUseDelta());
                    n2 += n;
                }
                else {
                    n -= 1.0f / drainableComboItem.getUseDelta();
                    n2 += 1.0f / drainableComboItem.getUseDelta();
                    drainableComboItem.setUsedDelta(1.0f);
                    drainableComboItem = (DrainableComboItem)InventoryItemFactory.CreateItem("Base.IronIngot");
                    drainableComboItem.setUsedDelta(0.0f);
                    this.getContainer().addItem(drainableComboItem);
                }
            }
            this.getContainer().Remove(inventoryItem);
        }
    }
    
    private void updateHeat() {
        if (!this.isFireStarted()) {
            this.heat -= this.heatDecrease * GameTime.instance.getMultiplier();
        }
        else if (this.getFuelAmount() == 0.0f) {
            this.setFireStarted(false);
        }
        else {
            this.fuelAmount -= this.fuelDecrease * (0.2f + this.heatIncrease / 80.0f) * GameTime.instance.getMultiplier();
            if (this.getHeat() < 20.0f) {
                this.heat += this.heatIncrease * GameTime.instance.getMultiplier();
            }
            this.heat -= this.heatDecrease * 0.05f * GameTime.instance.getMultiplier();
        }
        if (this.heat < 0.0f) {
            this.heat = 0.0f;
        }
        if (this.fuelAmount < 0.0f) {
            this.fuelAmount = 0.0f;
        }
    }
    
    public int getMeltingSkill(final InventoryItem inventoryItem) {
        if (inventoryItem.getWorker() != null && !inventoryItem.getWorker().isEmpty()) {
            for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
                final IsoPlayer isoPlayer = IsoPlayer.players[i];
                if (isoPlayer != null && !isoPlayer.isDead() && inventoryItem.getWorker().equals(isoPlayer.getFullName())) {
                    return isoPlayer.getPerkLevel(PerkFactory.Perks.Melting);
                }
            }
        }
        return 0;
    }
    
    @Override
    public void load(final ByteBuffer byteBuffer, final int n, final boolean b) throws IOException {
        super.load(byteBuffer, n, b);
        this.fireStarted = (byteBuffer.get() == 1);
        this.heat = byteBuffer.getFloat();
        this.fuelAmount = byteBuffer.getFloat();
    }
    
    @Override
    public void save(final ByteBuffer byteBuffer, final boolean b) throws IOException {
        super.save(byteBuffer, b);
        byteBuffer.put((byte)(this.isFireStarted() ? 1 : 0));
        byteBuffer.putFloat(this.getHeat());
        byteBuffer.putFloat(this.getFuelAmount());
    }
    
    @Override
    public String getObjectName() {
        return "StoneFurnace";
    }
    
    public float getHeat() {
        return this.heat;
    }
    
    public void setHeat(float heat) {
        if (heat > 100.0f) {
            heat = 100.0f;
        }
        if (heat < 0.0f) {
            heat = 0.0f;
        }
        this.heat = heat;
    }
    
    public boolean isFireStarted() {
        return this.fireStarted;
    }
    
    public void updateLight() {
        if (this.fireStarted && this.LightSource == null) {
            this.LightSource = new IsoLightSource(this.square.getX(), this.square.getY(), this.square.getZ(), 0.61f, 0.165f, 0.0f, 7);
            IsoWorld.instance.CurrentCell.addLamppost(this.LightSource);
        }
        else if (this.LightSource != null) {
            IsoWorld.instance.CurrentCell.removeLamppost(this.LightSource);
            this.LightSource = null;
        }
    }
    
    public void setFireStarted(final boolean fireStarted) {
        this.fireStarted = fireStarted;
        this.updateLight();
        this.syncFurnace();
    }
    
    public void syncFurnace() {
        if (GameServer.bServer) {
            GameServer.sendFuranceChange(this, null);
        }
        else if (GameClient.bClient) {
            GameClient.sendFurnaceChange(this);
        }
    }
    
    public float getFuelAmount() {
        return this.fuelAmount;
    }
    
    public void setFuelAmount(float fuelAmount) {
        if (fuelAmount > 100.0f) {
            fuelAmount = 100.0f;
        }
        if (fuelAmount < 0.0f) {
            fuelAmount = 0.0f;
        }
        this.fuelAmount = fuelAmount;
    }
    
    public void addFuel(final float n) {
        this.setFuelAmount(this.getFuelAmount() + n);
    }
    
    @Override
    public void addToWorld() {
        IsoWorld.instance.getCell().addToProcessIsoObject(this);
    }
    
    @Override
    public void removeFromWorld() {
        if (this.emitter != null) {
            this.emitter.stopAll();
            IsoWorld.instance.returnOwnershipOfEmitter(this.emitter);
            this.emitter = null;
        }
        super.removeFromWorld();
    }
    
    public float getFuelDecrease() {
        return this.fuelDecrease;
    }
    
    public void setFuelDecrease(final float fuelDecrease) {
        this.fuelDecrease = fuelDecrease;
    }
    
    public float getHeatDecrease() {
        return this.heatDecrease;
    }
    
    public void setHeatDecrease(final float heatDecrease) {
        this.heatDecrease = heatDecrease;
    }
    
    public float getHeatIncrease() {
        return this.heatIncrease;
    }
    
    public void setHeatIncrease(final float heatIncrease) {
        this.heatIncrease = heatIncrease;
    }
}

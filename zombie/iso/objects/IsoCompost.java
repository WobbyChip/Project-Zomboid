// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.objects;

import zombie.core.math.PZMath;
import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.iso.IsoWorld;
import zombie.core.Rand;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameServer;
import zombie.inventory.types.Food;
import zombie.inventory.InventoryItem;
import zombie.SandboxOptions;
import zombie.GameTime;
import zombie.network.GameClient;
import zombie.inventory.ItemContainer;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoCell;
import zombie.iso.IsoObject;

public class IsoCompost extends IsoObject
{
    private float compost;
    private float LastUpdated;
    
    public IsoCompost(final IsoCell isoCell) {
        super(isoCell);
        this.compost = 0.0f;
        this.LastUpdated = -1.0f;
    }
    
    public IsoCompost(final IsoCell isoCell, final IsoGridSquare square) {
        super(isoCell, square, IsoSpriteManager.instance.getSprite("camping_01_19"));
        this.compost = 0.0f;
        this.LastUpdated = -1.0f;
        this.sprite = IsoSpriteManager.instance.getSprite("camping_01_19");
        this.square = square;
        (this.container = new ItemContainer()).setType("crate");
        this.container.setParent(this);
        this.container.bExplored = true;
    }
    
    @Override
    public void update() {
        if (GameClient.bClient || this.container == null) {
            return;
        }
        final float lastUpdated = (float)GameTime.getInstance().getWorldAgeHours();
        if (this.LastUpdated < 0.0f) {
            this.LastUpdated = lastUpdated;
        }
        else if (this.LastUpdated > lastUpdated) {
            this.LastUpdated = lastUpdated;
        }
        final float n = lastUpdated - this.LastUpdated;
        if (n <= 0.0f) {
            return;
        }
        this.LastUpdated = lastUpdated;
        final int compostHours = SandboxOptions.instance.getCompostHours();
        for (int i = 0; i < this.container.getItems().size(); ++i) {
            final InventoryItem inventoryItem = this.container.getItems().get(i);
            if (inventoryItem instanceof Food) {
                final Food food = (Food)inventoryItem;
                if (GameServer.bServer) {
                    food.updateAge();
                }
                if (food.isRotten()) {
                    if (this.getCompost() < 100.0f) {
                        food.setRottenTime(0.0f);
                        food.setCompostTime(food.getCompostTime() + n);
                    }
                    if (food.getCompostTime() >= compostHours) {
                        this.setCompost(this.getCompost() + Math.abs(food.getHungChange()) * 2.0f);
                        if (this.getCompost() > 100.0f) {
                            this.setCompost(100.0f);
                        }
                        if (GameServer.bServer) {
                            GameServer.sendCompost(this, null);
                            GameServer.sendRemoveItemFromContainer(this.container, inventoryItem);
                        }
                        if (Rand.Next(10) == 0) {
                            final InventoryItem addItem = this.container.AddItem("Base.Worm");
                            if (GameServer.bServer && addItem != null) {
                                GameServer.sendAddItemToContainer(this.container, addItem);
                            }
                        }
                        inventoryItem.Use();
                        IsoWorld.instance.CurrentCell.addToProcessItemsRemove(inventoryItem);
                    }
                }
            }
        }
        this.updateSprite();
    }
    
    public void updateSprite() {
        if (this.getCompost() >= 10.0f && this.sprite.getName().equals("camping_01_19")) {
            this.sprite = IsoSpriteManager.instance.getSprite("camping_01_20");
            this.transmitUpdatedSpriteToClients();
        }
        else if (this.getCompost() < 10.0f && this.sprite.getName().equals("camping_01_20")) {
            this.sprite = IsoSpriteManager.instance.getSprite("camping_01_19");
            this.transmitUpdatedSpriteToClients();
        }
    }
    
    public void syncCompost() {
        if (GameClient.bClient) {
            GameClient.sendCompost(this);
        }
    }
    
    @Override
    public void load(final ByteBuffer byteBuffer, final int n, final boolean b) throws IOException {
        super.load(byteBuffer, n, b);
        this.compost = byteBuffer.getFloat();
        if (n >= 130) {
            this.LastUpdated = byteBuffer.getFloat();
        }
    }
    
    @Override
    public void save(final ByteBuffer byteBuffer, final boolean b) throws IOException {
        super.save(byteBuffer, b);
        byteBuffer.putFloat(this.compost);
        byteBuffer.putFloat(this.LastUpdated);
    }
    
    @Override
    public String getObjectName() {
        return "IsoCompost";
    }
    
    public float getCompost() {
        return this.compost;
    }
    
    public void setCompost(final float n) {
        this.compost = PZMath.clamp(n, 0.0f, 100.0f);
    }
    
    public void remove() {
        if (this.getSquare() == null) {
            return;
        }
        this.getSquare().transmitRemoveItemFromSquare(this);
    }
    
    @Override
    public void addToWorld() {
        this.getCell().addToProcessIsoObject(this);
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.vehicles;

import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.iso.IsoGridSquare;
import zombie.WorldSoundManager;
import zombie.SoundManager;
import zombie.network.GameServer;
import zombie.inventory.InventoryItem;
import zombie.characters.IsoPlayer;
import zombie.network.GameClient;
import zombie.characters.IsoGameCharacter;
import zombie.scripting.objects.VehicleScript;

public final class VehicleWindow
{
    protected VehiclePart part;
    protected int health;
    protected boolean openable;
    protected boolean open;
    protected float openDelta;
    
    VehicleWindow(final VehiclePart part) {
        this.openDelta = 0.0f;
        this.part = part;
    }
    
    public void init(final VehicleScript.Window window) {
        this.health = 100;
        this.openable = window.openable;
        this.open = false;
    }
    
    public int getHealth() {
        return this.part.getCondition();
    }
    
    public void setHealth(int health) {
        health = Math.max(health, 0);
        health = Math.min(health, 100);
        this.health = health;
    }
    
    public boolean isDestroyed() {
        return this.getHealth() == 0;
    }
    
    public boolean isOpenable() {
        return this.openable;
    }
    
    public boolean isOpen() {
        return this.open;
    }
    
    public void setOpen(final boolean open) {
        this.open = open;
        this.part.getVehicle().bDoDamageOverlay = true;
    }
    
    public void setOpenDelta(final float openDelta) {
        this.openDelta = openDelta;
    }
    
    public float getOpenDelta() {
        return this.openDelta;
    }
    
    public boolean isHittable() {
        return !this.isDestroyed() && !this.isOpen() && (this.part.getItemType() == null || this.part.getInventoryItem() != null);
    }
    
    public void hit(final IsoGameCharacter isoGameCharacter) {
        this.damage(this.getHealth());
        this.part.setCondition(0);
    }
    
    public void damage(final int i) {
        if (i <= 0) {
            return;
        }
        if (!this.isHittable()) {
            return;
        }
        if (GameClient.bClient) {
            GameClient.instance.sendClientCommandV(null, "vehicle", "damageWindow", "vehicle", this.part.vehicle.getId(), "part", this.part.getId(), "amount", i);
            return;
        }
        if (this.part.getVehicle().isAlarmed()) {
            this.part.getVehicle().triggerAlarm();
        }
        this.part.setCondition(this.part.getCondition() - i);
        if (this.isDestroyed()) {
            if (this.part.getInventoryItem() != null) {
                this.part.setInventoryItem(null);
                this.part.getVehicle().transmitPartItem(this.part);
            }
            final IsoGridSquare square = this.part.vehicle.square;
            if (GameServer.bServer) {
                GameServer.PlayWorldSoundServer("SmashWindow", false, square, 0.2f, 20.0f, 1.1f, true);
            }
            else {
                SoundManager.instance.PlayWorldSound("SmashWindow", square, 0.2f, 20.0f, 1.0f, true);
            }
            WorldSoundManager.instance.addSound(null, square.getX(), square.getY(), square.getZ(), 10, 20, true, 4.0f, 15.0f);
        }
        this.part.getVehicle().transmitPartWindow(this.part);
    }
    
    public void save(final ByteBuffer byteBuffer) throws IOException {
        byteBuffer.put((byte)this.part.getCondition());
        byteBuffer.put((byte)(this.open ? 1 : 0));
    }
    
    public void load(final ByteBuffer byteBuffer, final int n) throws IOException {
        this.part.setCondition(byteBuffer.get());
        this.health = this.part.getCondition();
        this.open = (byteBuffer.get() == 1);
        this.openDelta = (this.open ? 1.0f : 0.0f);
    }
}

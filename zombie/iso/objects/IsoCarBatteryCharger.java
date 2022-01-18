// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.objects;

import zombie.iso.IsoWorld;
import java.util.Iterator;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.network.GameClient;
import zombie.core.raknet.UdpConnection;
import zombie.core.network.ByteBufferWriter;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.core.textures.Texture;
import zombie.core.Core;
import zombie.core.skinnedmodel.model.WorldItemModelDrawer;
import zombie.iso.IsoCamera;
import zombie.iso.sprite.IsoDirectionFrame;
import zombie.core.opengl.Shader;
import zombie.core.textures.ColorInfo;
import zombie.SandboxOptions;
import zombie.GameTime;
import zombie.inventory.types.DrainableComboItem;
import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoCell;
import zombie.iso.sprite.IsoSprite;
import zombie.inventory.InventoryItem;
import zombie.iso.IsoObject;

public class IsoCarBatteryCharger extends IsoObject
{
    protected InventoryItem item;
    protected InventoryItem battery;
    protected boolean activated;
    protected float lastUpdate;
    protected float chargeRate;
    protected IsoSprite chargerSprite;
    protected IsoSprite batterySprite;
    protected long sound;
    
    public IsoCarBatteryCharger(final IsoCell isoCell) {
        super(isoCell);
        this.lastUpdate = -1.0f;
        this.chargeRate = 0.16666667f;
        this.sound = 0L;
    }
    
    public IsoCarBatteryCharger(final InventoryItem item, final IsoCell isoCell, final IsoGridSquare isoGridSquare) {
        super(isoCell, isoGridSquare, (IsoSprite)null);
        this.lastUpdate = -1.0f;
        this.chargeRate = 0.16666667f;
        this.sound = 0L;
        if (item == null) {
            throw new NullPointerException("item is null");
        }
        this.item = item;
    }
    
    @Override
    public String getObjectName() {
        return "IsoCarBatteryCharger";
    }
    
    @Override
    public void load(final ByteBuffer byteBuffer, final int n, final boolean b) throws IOException {
        super.load(byteBuffer, n, b);
        if (byteBuffer.get() == 1) {
            try {
                this.item = InventoryItem.loadItem(byteBuffer, n);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (byteBuffer.get() == 1) {
            try {
                this.battery = InventoryItem.loadItem(byteBuffer, n);
            }
            catch (Exception ex2) {
                ex2.printStackTrace();
            }
        }
        this.activated = (byteBuffer.get() == 1);
        this.lastUpdate = byteBuffer.getFloat();
        this.chargeRate = byteBuffer.getFloat();
    }
    
    @Override
    public void save(final ByteBuffer byteBuffer, final boolean b) throws IOException {
        super.save(byteBuffer, b);
        if (this.item == null) {
            assert false;
            byteBuffer.put((byte)0);
        }
        else {
            byteBuffer.put((byte)1);
            this.item.saveWithSize(byteBuffer, false);
        }
        if (this.battery == null) {
            byteBuffer.put((byte)0);
        }
        else {
            byteBuffer.put((byte)1);
            this.battery.saveWithSize(byteBuffer, false);
        }
        byteBuffer.put((byte)(this.activated ? 1 : 0));
        byteBuffer.putFloat(this.lastUpdate);
        byteBuffer.putFloat(this.chargeRate);
    }
    
    @Override
    public void addToWorld() {
        super.addToWorld();
        this.getCell().addToProcessIsoObject(this);
    }
    
    @Override
    public void removeFromWorld() {
        this.stopChargingSound();
        super.removeFromWorld();
    }
    
    @Override
    public void update() {
        super.update();
        if (!(this.battery instanceof DrainableComboItem)) {
            this.battery = null;
        }
        if (this.battery == null) {
            this.lastUpdate = -1.0f;
            this.activated = false;
            this.stopChargingSound();
            return;
        }
        if (this.square == null || (!this.square.haveElectricity() && (GameTime.instance.NightsSurvived >= SandboxOptions.instance.getElecShutModifier() || this.square.getRoom() == null))) {
            this.activated = false;
        }
        if (!this.activated) {
            this.lastUpdate = -1.0f;
            this.stopChargingSound();
            return;
        }
        this.startChargingSound();
        final DrainableComboItem drainableComboItem = (DrainableComboItem)this.battery;
        if (drainableComboItem.getUsedDelta() >= 1.0f) {
            return;
        }
        final float lastUpdate = (float)GameTime.getInstance().getWorldAgeHours();
        if (this.lastUpdate < 0.0f) {
            this.lastUpdate = lastUpdate;
        }
        if (this.lastUpdate > lastUpdate) {
            this.lastUpdate = lastUpdate;
        }
        final float n = lastUpdate - this.lastUpdate;
        if (n > 0.0f) {
            drainableComboItem.setUsedDelta(Math.min(1.0f, drainableComboItem.getUsedDelta() + this.chargeRate * n));
            this.lastUpdate = lastUpdate;
        }
    }
    
    @Override
    public void render(final float n, final float n2, final float n3, final ColorInfo colorInfo, final boolean b, final boolean b2, final Shader shader) {
        this.chargerSprite = this.configureSprite(this.item, this.chargerSprite);
        if (this.chargerSprite.CurrentAnim == null || this.chargerSprite.CurrentAnim.Frames.isEmpty()) {
            return;
        }
        final Texture texture = this.chargerSprite.CurrentAnim.Frames.get(0).getTexture(this.dir);
        if (texture == null) {
            return;
        }
        final float n4 = texture.getWidthOrig() * this.chargerSprite.def.getScaleX() / 2.0f;
        final float n5 = texture.getHeightOrig() * this.chargerSprite.def.getScaleY() * 3.0f / 4.0f;
        final float n6 = 0.0f;
        this.offsetY = n6;
        this.offsetX = n6;
        this.setAlpha(IsoCamera.frameState.playerIndex, 1.0f);
        final float n7 = 0.5f;
        final float n8 = 0.5f;
        final float n9 = 0.0f;
        this.sx = 0.0f;
        this.item.setWorldZRotation(315);
        if (!WorldItemModelDrawer.renderMain(this.getItem(), this.getSquare(), this.getX() + n7, this.getY() + n8, this.getZ() + n9, -1.0f)) {
            this.chargerSprite.render(this, n + n7, n2 + n8, n3 + n9, this.dir, this.offsetX + n4 + 8 * Core.TileScale, this.offsetY + n5 + 4 * Core.TileScale, colorInfo, true);
        }
        if (this.battery != null) {
            this.batterySprite = this.configureSprite(this.battery, this.batterySprite);
            if (this.batterySprite != null && this.batterySprite.CurrentAnim != null && !this.batterySprite.CurrentAnim.Frames.isEmpty()) {
                this.sx = 0.0f;
                this.getBattery().setWorldZRotation(90);
                if (!WorldItemModelDrawer.renderMain(this.getBattery(), this.getSquare(), this.getX() + 0.75f, this.getY() + 0.75f, this.getZ() + n9, -1.0f)) {
                    this.batterySprite.render(this, n + n7, n2 + n8, n3 + n9, this.dir, this.offsetX + n4 - 8.0f + Core.TileScale, this.offsetY + n5 - 4 * Core.TileScale, colorInfo, true);
                }
            }
        }
    }
    
    @Override
    public void renderObjectPicker(final float n, final float n2, final float n3, final ColorInfo colorInfo) {
    }
    
    private IsoSprite configureSprite(final InventoryItem inventoryItem, IsoSprite createSprite) {
        String s = inventoryItem.getWorldTexture();
        try {
            if (Texture.getSharedTexture(s) == null) {
                s = inventoryItem.getTex().getName();
            }
        }
        catch (Exception ex) {
            s = "media/inventory/world/WItem_Sack.png";
        }
        final Texture sharedTexture = Texture.getSharedTexture(s);
        boolean b = false;
        if (createSprite == null) {
            createSprite = IsoSprite.CreateSprite(IsoSpriteManager.instance);
        }
        if (createSprite.CurrentAnim == null) {
            createSprite.LoadFramesNoDirPageSimple(s);
            createSprite.CurrentAnim.name = s;
            b = true;
        }
        else if (!s.equals(createSprite.CurrentAnim.name)) {
            createSprite.ReplaceCurrentAnimFrames(s);
            createSprite.CurrentAnim.name = s;
            b = true;
        }
        if (b) {
            if (inventoryItem.getScriptItem() == null) {
                createSprite.def.scaleAspect((float)sharedTexture.getWidthOrig(), (float)sharedTexture.getHeightOrig(), (float)(16 * Core.TileScale), (float)(16 * Core.TileScale));
            }
            else if (this.battery != null && this.battery.getScriptItem() != null) {
                final float n = this.battery.getScriptItem().ScaleWorldIcon * (Core.TileScale / 2.0f);
                createSprite.def.setScale(n, n);
            }
        }
        return createSprite;
    }
    
    @Override
    public void syncIsoObjectSend(final ByteBufferWriter byteBufferWriter) {
        final byte b = (byte)this.getObjectIndex();
        byteBufferWriter.putInt(this.square.getX());
        byteBufferWriter.putInt(this.square.getY());
        byteBufferWriter.putInt(this.square.getZ());
        byteBufferWriter.putByte(b);
        byteBufferWriter.putByte((byte)1);
        byteBufferWriter.putByte((byte)0);
        if (this.battery == null) {
            byteBufferWriter.putByte((byte)0);
        }
        else {
            byteBufferWriter.putByte((byte)1);
            try {
                this.battery.saveWithSize(byteBufferWriter.bb, false);
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        byteBufferWriter.putBoolean(this.activated);
        byteBufferWriter.putFloat(this.chargeRate);
    }
    
    @Override
    public void syncIsoObject(final boolean b, final byte b2, final UdpConnection udpConnection, final ByteBuffer byteBuffer) {
        if (GameClient.bClient && !b) {
            final ByteBufferWriter startPacket = GameClient.connection.startPacket();
            PacketTypes.PacketType.SyncIsoObject.doPacket(startPacket);
            this.syncIsoObjectSend(startPacket);
            PacketTypes.PacketType.SyncIsoObject.send(GameClient.connection);
        }
        else if (GameServer.bServer && !b) {
            for (final UdpConnection udpConnection2 : GameServer.udpEngine.connections) {
                final ByteBufferWriter startPacket2 = udpConnection2.startPacket();
                PacketTypes.PacketType.SyncIsoObject.doPacket(startPacket2);
                this.syncIsoObjectSend(startPacket2);
                PacketTypes.PacketType.SyncIsoObject.send(udpConnection2);
            }
        }
        else if (b) {
            if (byteBuffer.get() == 1) {
                try {
                    this.battery = InventoryItem.loadItem(byteBuffer, 186);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            else {
                this.battery = null;
            }
            this.activated = (byteBuffer.get() == 1);
            this.chargeRate = byteBuffer.getFloat();
            if (GameServer.bServer) {
                for (final UdpConnection udpConnection3 : GameServer.udpEngine.connections) {
                    if (udpConnection != null && udpConnection3 != udpConnection) {
                        final ByteBufferWriter startPacket3 = udpConnection3.startPacket();
                        PacketTypes.PacketType.SyncIsoObject.doPacket(startPacket3);
                        this.syncIsoObjectSend(startPacket3);
                        PacketTypes.PacketType.SyncIsoObject.send(udpConnection3);
                    }
                }
            }
        }
    }
    
    public void sync() {
        this.syncIsoObject(false, (byte)0, null, null);
    }
    
    public InventoryItem getItem() {
        return this.item;
    }
    
    public InventoryItem getBattery() {
        return this.battery;
    }
    
    public void setBattery(final InventoryItem battery) {
        if (battery != null) {
            if (!(battery instanceof DrainableComboItem)) {
                throw new IllegalArgumentException("battery isn't DrainableComboItem");
            }
            if (this.battery != null) {
                throw new IllegalStateException("battery already inserted");
            }
        }
        this.battery = battery;
    }
    
    public boolean isActivated() {
        return this.activated;
    }
    
    public void setActivated(final boolean activated) {
        this.activated = activated;
    }
    
    public float getChargeRate() {
        return this.chargeRate;
    }
    
    public void setChargeRate(final float chargeRate) {
        if (chargeRate <= 0.0f) {
            throw new IllegalArgumentException("chargeRate <= 0.0f");
        }
        this.chargeRate = chargeRate;
    }
    
    private void startChargingSound() {
        if (GameServer.bServer) {
            return;
        }
        if (this.getObjectIndex() == -1) {
            return;
        }
        if (this.sound == -1L) {
            return;
        }
        if (this.emitter == null) {
            this.emitter = IsoWorld.instance.getFreeEmitter(this.square.x + 0.5f, this.square.y + 0.5f, (float)this.square.z);
            IsoWorld.instance.takeOwnershipOfEmitter(this.emitter);
        }
        if (!this.emitter.isPlaying(this.sound)) {
            this.sound = this.emitter.playSound("CarBatteryChargerRunning");
            if (this.sound == 0L) {
                this.sound = -1L;
            }
        }
        this.emitter.tick();
    }
    
    private void stopChargingSound() {
        if (GameServer.bServer) {
            return;
        }
        if (this.emitter == null) {
            return;
        }
        this.emitter.stopOrTriggerSound(this.sound);
        this.sound = 0L;
        IsoWorld.instance.returnOwnershipOfEmitter(this.emitter);
        this.emitter = null;
    }
}

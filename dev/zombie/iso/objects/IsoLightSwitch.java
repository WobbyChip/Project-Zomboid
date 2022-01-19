// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.objects;

import zombie.iso.IsoRoomLight;
import zombie.iso.IsoChunk;
import zombie.core.Rand;
import zombie.core.network.ByteBufferWriter;
import zombie.network.PacketTypes;
import zombie.network.PacketTypesShort;
import java.util.Iterator;
import zombie.inventory.types.Moveable;
import zombie.network.GameServer;
import zombie.WorldSoundManager;
import zombie.iso.IsoWorld;
import java.io.IOException;
import zombie.SystemDisabler;
import zombie.GameWindow;
import java.nio.ByteBuffer;
import zombie.core.Color;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.DrainableComboItem;
import zombie.inventory.InventoryItem;
import zombie.characters.IsoGameCharacter;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameClient;
import zombie.iso.areas.IsoRoom;
import zombie.SandboxOptions;
import zombie.GameTime;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoCell;
import zombie.iso.IsoLightSource;
import java.util.ArrayList;
import zombie.iso.IsoObject;

public class IsoLightSwitch extends IsoObject
{
    boolean Activated;
    public final ArrayList<IsoLightSource> lights;
    public boolean lightRoom;
    public int RoomID;
    public boolean bStreetLight;
    private boolean canBeModified;
    private boolean useBattery;
    private boolean hasBattery;
    private String bulbItem;
    private float power;
    private float delta;
    private float primaryR;
    private float primaryG;
    private float primaryB;
    protected long lastMinuteStamp;
    protected int bulbBurnMinutes;
    protected int lastMin;
    protected int nextBreakUpdate;
    
    @Override
    public String getObjectName() {
        return "LightSwitch";
    }
    
    public IsoLightSwitch(final IsoCell isoCell) {
        super(isoCell);
        this.Activated = false;
        this.lights = new ArrayList<IsoLightSource>();
        this.lightRoom = false;
        this.RoomID = -1;
        this.bStreetLight = false;
        this.canBeModified = false;
        this.useBattery = false;
        this.hasBattery = false;
        this.bulbItem = "Base.LightBulb";
        this.power = 0.0f;
        this.delta = 2.5E-4f;
        this.primaryR = 1.0f;
        this.primaryG = 1.0f;
        this.primaryB = 1.0f;
        this.lastMinuteStamp = -1L;
        this.bulbBurnMinutes = -1;
        this.lastMin = 0;
        this.nextBreakUpdate = 60;
    }
    
    public IsoLightSwitch(final IsoCell isoCell, final IsoGridSquare isoGridSquare, final IsoSprite isoSprite, final int roomID) {
        super(isoCell, isoGridSquare, isoSprite);
        this.Activated = false;
        this.lights = new ArrayList<IsoLightSource>();
        this.lightRoom = false;
        this.RoomID = -1;
        this.bStreetLight = false;
        this.canBeModified = false;
        this.useBattery = false;
        this.hasBattery = false;
        this.bulbItem = "Base.LightBulb";
        this.power = 0.0f;
        this.delta = 2.5E-4f;
        this.primaryR = 1.0f;
        this.primaryG = 1.0f;
        this.primaryB = 1.0f;
        this.lastMinuteStamp = -1L;
        this.bulbBurnMinutes = -1;
        this.lastMin = 0;
        this.nextBreakUpdate = 60;
        this.RoomID = roomID;
        if (isoSprite != null && isoSprite.getProperties().Is("lightR")) {
            if (isoSprite.getProperties().Is("IsMoveAble")) {
                this.canBeModified = true;
            }
            this.primaryR = Float.parseFloat(isoSprite.getProperties().Val("lightR")) / 255.0f;
            this.primaryG = Float.parseFloat(isoSprite.getProperties().Val("lightG")) / 255.0f;
            this.primaryB = Float.parseFloat(isoSprite.getProperties().Val("lightB")) / 255.0f;
        }
        else {
            this.lightRoom = true;
        }
        this.bStreetLight = (isoSprite != null && isoSprite.getProperties().Is("streetlight"));
        final IsoRoom room = this.square.getRoom();
        if (room != null && this.lightRoom) {
            if (!isoGridSquare.haveElectricity() && GameTime.instance.NightsSurvived >= SandboxOptions.instance.getElecShutModifier()) {
                room.def.bLightsActive = false;
            }
            this.Activated = room.def.bLightsActive;
            room.lightSwitches.add(this);
        }
        else {
            this.Activated = true;
        }
    }
    
    public void addLightSourceFromSprite() {
        if (this.sprite != null && this.sprite.getProperties().Is("lightR")) {
            final float n = Float.parseFloat(this.sprite.getProperties().Val("lightR")) / 255.0f;
            final float n2 = Float.parseFloat(this.sprite.getProperties().Val("lightG")) / 255.0f;
            final float n3 = Float.parseFloat(this.sprite.getProperties().Val("lightB")) / 255.0f;
            this.Activated = false;
            this.setActive(true, true);
            int int1 = 10;
            if (this.sprite.getProperties().Is("LightRadius") && Integer.parseInt(this.sprite.getProperties().Val("LightRadius")) > 0) {
                int1 = Integer.parseInt(this.sprite.getProperties().Val("LightRadius"));
            }
            final IsoLightSource e = new IsoLightSource(this.square.getX(), this.square.getY(), this.square.getZ(), n, n2, n3, int1);
            e.bActive = this.Activated;
            e.bHydroPowered = true;
            e.switches.add(this);
            this.lights.add(e);
        }
    }
    
    public boolean getCanBeModified() {
        return this.canBeModified;
    }
    
    public float getPower() {
        return this.power;
    }
    
    public void setPower(final float power) {
        this.power = power;
    }
    
    public void setDelta(final float delta) {
        this.delta = delta;
    }
    
    public float getDelta() {
        return this.delta;
    }
    
    public void setUseBattery(final boolean useBattery) {
        this.setActive(false);
        this.useBattery = useBattery;
        if (GameClient.bClient) {
            this.syncCustomizedSettings(null);
        }
    }
    
    public boolean getUseBattery() {
        return this.useBattery;
    }
    
    public boolean getHasBattery() {
        return this.hasBattery;
    }
    
    public void setHasBatteryRaw(final boolean hasBattery) {
        this.hasBattery = hasBattery;
    }
    
    public void addBattery(final IsoGameCharacter isoGameCharacter, final InventoryItem inventoryItem) {
        if (this.canBeModified && this.useBattery && !this.hasBattery && inventoryItem != null && inventoryItem.getFullType().equals("Base.Battery")) {
            this.power = ((DrainableComboItem)inventoryItem).getUsedDelta();
            this.hasBattery = true;
            isoGameCharacter.removeFromHands(inventoryItem);
            isoGameCharacter.getInventory().Remove(inventoryItem);
            if (GameClient.bClient) {
                this.syncCustomizedSettings(null);
            }
        }
    }
    
    public DrainableComboItem removeBattery(final IsoGameCharacter isoGameCharacter) {
        if (this.canBeModified && this.useBattery && this.hasBattery) {
            final DrainableComboItem drainableComboItem = (DrainableComboItem)InventoryItemFactory.CreateItem("Base.Battery");
            if (drainableComboItem != null) {
                this.hasBattery = false;
                drainableComboItem.setUsedDelta((this.power >= 0.0f) ? this.power : 0.0f);
                this.power = 0.0f;
                this.setActive(false, false, true);
                isoGameCharacter.getInventory().AddItem(drainableComboItem);
                if (GameClient.bClient) {
                    this.syncCustomizedSettings(null);
                }
                return drainableComboItem;
            }
        }
        return null;
    }
    
    public boolean hasLightBulb() {
        return this.bulbItem != null;
    }
    
    public String getBulbItem() {
        return this.bulbItem;
    }
    
    public void setBulbItemRaw(final String bulbItem) {
        this.bulbItem = bulbItem;
    }
    
    public void addLightBulb(final IsoGameCharacter isoGameCharacter, final InventoryItem inventoryItem) {
        if (!this.hasLightBulb() && inventoryItem != null && inventoryItem.getType().startsWith("LightBulb") && this.getPrimaryLight() != null) {
            this.setPrimaryR(inventoryItem.getColorRed());
            this.setPrimaryG(inventoryItem.getColorGreen());
            this.setPrimaryB(inventoryItem.getColorBlue());
            this.bulbItem = inventoryItem.getFullType();
            isoGameCharacter.removeFromHands(inventoryItem);
            isoGameCharacter.getInventory().Remove(inventoryItem);
            if (GameClient.bClient) {
                this.syncCustomizedSettings(null);
            }
        }
    }
    
    public InventoryItem removeLightBulb(final IsoGameCharacter isoGameCharacter) {
        final IsoLightSource primaryLight = this.getPrimaryLight();
        if (primaryLight != null && this.hasLightBulb()) {
            final InventoryItem createItem = InventoryItemFactory.CreateItem(this.bulbItem);
            if (createItem != null) {
                createItem.setColorRed(this.getPrimaryR());
                createItem.setColorGreen(this.getPrimaryG());
                createItem.setColorBlue(this.getPrimaryB());
                createItem.setColor(new Color(primaryLight.r, primaryLight.g, primaryLight.b));
                this.bulbItem = null;
                isoGameCharacter.getInventory().AddItem(createItem);
                this.setActive(false, false, true);
                if (GameClient.bClient) {
                    this.syncCustomizedSettings(null);
                }
                return createItem;
            }
        }
        return null;
    }
    
    private IsoLightSource getPrimaryLight() {
        if (this.lights.size() > 0) {
            return this.lights.get(0);
        }
        return null;
    }
    
    public float getPrimaryR() {
        return (this.getPrimaryLight() != null) ? this.getPrimaryLight().r : this.primaryR;
    }
    
    public float getPrimaryG() {
        return (this.getPrimaryLight() != null) ? this.getPrimaryLight().g : this.primaryG;
    }
    
    public float getPrimaryB() {
        return (this.getPrimaryLight() != null) ? this.getPrimaryLight().b : this.primaryB;
    }
    
    public void setPrimaryR(final float n) {
        this.primaryR = n;
        if (this.getPrimaryLight() != null) {
            this.getPrimaryLight().r = n;
        }
    }
    
    public void setPrimaryG(final float n) {
        this.primaryG = n;
        if (this.getPrimaryLight() != null) {
            this.getPrimaryLight().g = n;
        }
    }
    
    public void setPrimaryB(final float n) {
        this.primaryB = n;
        if (this.getPrimaryLight() != null) {
            this.getPrimaryLight().b = n;
        }
    }
    
    @Override
    public void load(final ByteBuffer byteBuffer, final int n, final boolean b) throws IOException {
        super.load(byteBuffer, n, b);
        this.lightRoom = (byteBuffer.get() == 1);
        this.RoomID = byteBuffer.getInt();
        this.Activated = (byteBuffer.get() == 1);
        if (n >= 76) {
            this.canBeModified = (byteBuffer.get() == 1);
            if (this.canBeModified) {
                this.useBattery = (byteBuffer.get() == 1);
                this.hasBattery = (byteBuffer.get() == 1);
                if (byteBuffer.get() == 1) {
                    this.bulbItem = GameWindow.ReadString(byteBuffer);
                }
                else {
                    this.bulbItem = null;
                }
                this.power = byteBuffer.getFloat();
                this.delta = byteBuffer.getFloat();
                this.setPrimaryR(byteBuffer.getFloat());
                this.setPrimaryG(byteBuffer.getFloat());
                this.setPrimaryB(byteBuffer.getFloat());
            }
        }
        if (n >= 79) {
            this.lastMinuteStamp = byteBuffer.getLong();
            this.bulbBurnMinutes = byteBuffer.getInt();
        }
        this.bStreetLight = (this.sprite != null && this.sprite.getProperties().Is("streetlight"));
        if (this.square == null) {
            return;
        }
        final IsoRoom room = this.square.getRoom();
        if (room != null && this.lightRoom) {
            this.Activated = room.def.bLightsActive;
            room.lightSwitches.add(this);
        }
        else {
            float primaryR = 0.9f;
            float primaryG = 0.8f;
            float primaryB = 0.7f;
            if (this.sprite != null && this.sprite.getProperties().Is("lightR")) {
                if (n >= 76 && this.canBeModified) {
                    primaryR = this.primaryR;
                    primaryG = this.primaryG;
                    primaryB = this.primaryB;
                }
                else {
                    primaryR = Float.parseFloat(this.sprite.getProperties().Val("lightR")) / 255.0f;
                    primaryG = Float.parseFloat(this.sprite.getProperties().Val("lightG")) / 255.0f;
                    primaryB = Float.parseFloat(this.sprite.getProperties().Val("lightB")) / 255.0f;
                    this.primaryR = primaryR;
                    this.primaryG = primaryG;
                    this.primaryB = primaryB;
                }
            }
            int int1 = 8;
            if (this.sprite.getProperties().Is("LightRadius") && Integer.parseInt(this.sprite.getProperties().Val("LightRadius")) > 0) {
                int1 = Integer.parseInt(this.sprite.getProperties().Val("LightRadius"));
            }
            final IsoLightSource e = new IsoLightSource((int)this.getX(), (int)this.getY(), (int)this.getZ(), primaryR, primaryG, primaryB, int1);
            e.bActive = this.Activated;
            e.bWasActive = e.bActive;
            e.bHydroPowered = true;
            e.switches.add(this);
            this.lights.add(e);
        }
        if (SystemDisabler.doObjectStateSyncEnable && GameClient.bClient) {
            GameClient.instance.objectSyncReq.putRequestLoad(this.square);
        }
    }
    
    @Override
    public void save(final ByteBuffer byteBuffer, final boolean b) throws IOException {
        super.save(byteBuffer, b);
        byteBuffer.put((byte)(this.lightRoom ? 1 : 0));
        byteBuffer.putInt(this.RoomID);
        byteBuffer.put((byte)(this.Activated ? 1 : 0));
        byteBuffer.put((byte)(this.canBeModified ? 1 : 0));
        if (this.canBeModified) {
            byteBuffer.put((byte)(this.useBattery ? 1 : 0));
            byteBuffer.put((byte)(this.hasBattery ? 1 : 0));
            byteBuffer.put((byte)(this.hasLightBulb() ? 1 : 0));
            if (this.hasLightBulb()) {
                GameWindow.WriteString(byteBuffer, this.bulbItem);
            }
            byteBuffer.putFloat(this.power);
            byteBuffer.putFloat(this.delta);
            byteBuffer.putFloat(this.getPrimaryR());
            byteBuffer.putFloat(this.getPrimaryG());
            byteBuffer.putFloat(this.getPrimaryB());
        }
        byteBuffer.putLong(this.lastMinuteStamp);
        byteBuffer.putInt(this.bulbBurnMinutes);
    }
    
    @Override
    public boolean onMouseLeftClick(final int n, final int n2) {
        return false;
    }
    
    public boolean canSwitchLight() {
        if (this.bulbItem != null) {
            final boolean b = GameTime.instance.NightsSurvived < SandboxOptions.instance.getElecShutModifier();
            int n = (b ? (this.square.getRoom() != null || this.bStreetLight) : this.square.haveElectricity()) ? 1 : 0;
            if (n == 0 && this.getCell() != null) {
                for (int i = 0; i >= ((this.getZ() >= 1.0f) ? -1 : 0); --i) {
                    for (int j = -1; j < 2; ++j) {
                        for (int k = -1; k < 2; ++k) {
                            if (j != 0 || k != 0 || i != 0) {
                                final IsoGridSquare gridSquare = this.getCell().getGridSquare(this.getX() + j, this.getY() + k, this.getZ() + i);
                                if (gridSquare != null && ((b && gridSquare.getRoom() != null) || gridSquare.haveElectricity())) {
                                    n = 1;
                                    break;
                                }
                            }
                        }
                        if (n != 0) {
                            break;
                        }
                    }
                }
            }
            if ((!this.useBattery && n != 0) || (this.canBeModified && this.useBattery && this.hasBattery && this.power > 0.0f)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean setActive(final boolean b) {
        return this.setActive(b, false, false);
    }
    
    public boolean setActive(final boolean b, final boolean b2) {
        return this.setActive(b, b2, false);
    }
    
    public boolean setActive(boolean activated, final boolean b, final boolean b2) {
        if (this.bulbItem == null) {
            activated = false;
        }
        if (activated == this.Activated) {
            return this.Activated;
        }
        if (this.square.getRoom() == null && !this.canBeModified) {
            return this.Activated;
        }
        if (b2 || this.canSwitchLight()) {
            this.Activated = activated;
            if (!b) {
                IsoWorld.instance.getFreeEmitter().playSound("LightSwitch", this.square);
                if (this.Activated && (GameTime.instance.getHour() > 22 || GameTime.instance.getHour() < 5)) {
                    WorldSoundManager.instance.addSound(null, (int)this.getX(), (int)this.getY(), (int)this.getZ(), 50, 3);
                }
                this.switchLight(this.Activated);
                this.syncIsoObject(false, (byte)(this.Activated ? 1 : 0), null);
            }
        }
        return this.Activated;
    }
    
    public boolean toggle() {
        return this.setActive(!this.Activated);
    }
    
    public void switchLight(final boolean bActive) {
        if (this.lightRoom && this.square.getRoom() != null) {
            this.square.getRoom().def.bLightsActive = bActive;
            for (int i = 0; i < this.square.getRoom().lightSwitches.size(); ++i) {
                this.square.getRoom().lightSwitches.get(i).Activated = bActive;
            }
            if (GameServer.bServer) {
                GameServer.sendMetaGrid(this.square.getX() / 300, this.square.getY() / 300, this.square.getRoom().def.ID);
            }
        }
        for (int j = 0; j < this.lights.size(); ++j) {
            this.lights.get(j).bActive = bActive;
        }
        IsoGridSquare.RecalcLightTime = -1;
        GameTime.instance.lightSourceUpdate = 100.0f;
        IsoGenerator.updateGenerator(this.getSquare());
    }
    
    public void getCustomSettingsFromItem(final InventoryItem inventoryItem) {
        if (inventoryItem instanceof Moveable) {
            final Moveable moveable = (Moveable)inventoryItem;
            if (moveable.isLight()) {
                this.useBattery = moveable.isLightUseBattery();
                this.hasBattery = moveable.isLightHasBattery();
                this.bulbItem = moveable.getLightBulbItem();
                this.power = moveable.getLightPower();
                this.delta = moveable.getLightDelta();
                this.setPrimaryR(moveable.getLightR());
                this.setPrimaryG(moveable.getLightG());
                this.setPrimaryB(moveable.getLightB());
            }
        }
    }
    
    public void setCustomSettingsToItem(final InventoryItem inventoryItem) {
        if (inventoryItem instanceof Moveable) {
            final Moveable moveable = (Moveable)inventoryItem;
            moveable.setLightUseBattery(this.useBattery);
            moveable.setLightHasBattery(this.hasBattery);
            moveable.setLightBulbItem(this.bulbItem);
            moveable.setLightPower(this.power);
            moveable.setLightDelta(this.delta);
            moveable.setLightR(this.primaryR);
            moveable.setLightG(this.primaryG);
            moveable.setLightB(this.primaryB);
        }
    }
    
    public void syncCustomizedSettings(final UdpConnection udpConnection) {
        if (GameClient.bClient) {
            this.writeCustomizedSettingsPacket(GameClient.connection);
        }
        else if (GameServer.bServer) {
            for (final UdpConnection udpConnection2 : GameServer.udpEngine.connections) {
                if (udpConnection == null || udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
                    this.writeCustomizedSettingsPacket(udpConnection2);
                }
            }
        }
    }
    
    private void writeCustomizedSettingsPacket(final UdpConnection udpConnection) {
        if (udpConnection != null) {
            final ByteBufferWriter startPacket = udpConnection.startPacket();
            PacketTypesShort.doPacket((short)1200, startPacket);
            this.writeLightSwitchObjectHeader(startPacket, (byte)(this.Activated ? 1 : 0));
            startPacket.putBoolean(this.canBeModified);
            startPacket.putBoolean(this.useBattery);
            startPacket.putBoolean(this.hasBattery);
            startPacket.putByte((byte)((this.bulbItem != null) ? 1 : 0));
            if (this.bulbItem != null) {
                GameWindow.WriteString(startPacket.bb, this.bulbItem);
            }
            startPacket.putFloat(this.power);
            startPacket.putFloat(this.delta);
            startPacket.putFloat(this.primaryR);
            startPacket.putFloat(this.primaryG);
            startPacket.putFloat(this.primaryB);
            PacketTypes.PacketType.PacketTypeShort.send(udpConnection);
        }
    }
    
    private void readCustomizedSettingsPacket(final ByteBuffer byteBuffer) {
        this.Activated = (byteBuffer.get() == 1);
        this.canBeModified = (byteBuffer.get() == 1);
        this.useBattery = (byteBuffer.get() == 1);
        this.hasBattery = (byteBuffer.get() == 1);
        if (byteBuffer.get() == 1) {
            this.bulbItem = GameWindow.ReadString(byteBuffer);
        }
        else {
            this.bulbItem = null;
        }
        this.power = byteBuffer.getFloat();
        this.delta = byteBuffer.getFloat();
        this.setPrimaryR(byteBuffer.getFloat());
        this.setPrimaryG(byteBuffer.getFloat());
        this.setPrimaryB(byteBuffer.getFloat());
    }
    
    public void receiveSyncCustomizedSettings(final ByteBuffer byteBuffer, final UdpConnection udpConnection) {
        if (GameClient.bClient) {
            this.readCustomizedSettingsPacket(byteBuffer);
        }
        else if (GameServer.bServer) {
            this.readCustomizedSettingsPacket(byteBuffer);
            this.syncCustomizedSettings(udpConnection);
        }
        this.switchLight(this.Activated);
    }
    
    private void writeLightSwitchObjectHeader(final ByteBufferWriter byteBufferWriter, final byte b) {
        byteBufferWriter.putInt(this.square.getX());
        byteBufferWriter.putInt(this.square.getY());
        byteBufferWriter.putInt(this.square.getZ());
        byteBufferWriter.putByte((byte)this.square.getObjects().indexOf(this));
        byteBufferWriter.putByte(b);
    }
    
    @Override
    public void syncIsoObjectSend(final ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putInt(this.square.getX());
        byteBufferWriter.putInt(this.square.getY());
        byteBufferWriter.putInt(this.square.getZ());
        byteBufferWriter.putByte((byte)this.square.getObjects().indexOf(this));
        byteBufferWriter.putByte((byte)1);
        byteBufferWriter.putByte((byte)(this.Activated ? 1 : 0));
    }
    
    @Override
    public void syncIsoObject(final boolean b, final byte b2, final UdpConnection udpConnection, final ByteBuffer byteBuffer) {
        this.syncIsoObject(b, b2, udpConnection);
    }
    
    public void syncIsoObject(final boolean b, final byte b2, final UdpConnection udpConnection) {
        if (this.square == null) {
            System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()));
            return;
        }
        if (this.getObjectIndex() == -1) {
            System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;III)Ljava/lang/String;, this.getClass().getSimpleName(), this.square.getX(), this.square.getY(), this.square.getZ()));
            return;
        }
        if (GameClient.bClient && !b) {
            final ByteBufferWriter startPacket = GameClient.connection.startPacket();
            PacketTypes.PacketType.SyncIsoObject.doPacket(startPacket);
            this.syncIsoObjectSend(startPacket);
            PacketTypes.PacketType.SyncIsoObject.send(GameClient.connection);
        }
        else if (b) {
            if (b2 == 1) {
                this.switchLight(true);
                this.Activated = true;
            }
            else {
                this.switchLight(false);
                this.Activated = false;
            }
            if (GameServer.bServer) {
                for (final UdpConnection udpConnection2 : GameServer.udpEngine.connections) {
                    if (udpConnection != null) {
                        if (udpConnection2.getConnectedGUID() == udpConnection.getConnectedGUID()) {
                            continue;
                        }
                        final ByteBufferWriter startPacket2 = udpConnection2.startPacket();
                        PacketTypes.PacketType.SyncIsoObject.doPacket(startPacket2);
                        this.syncIsoObjectSend(startPacket2);
                        PacketTypes.PacketType.SyncIsoObject.send(udpConnection2);
                    }
                    else {
                        if (!udpConnection2.RelevantTo((float)this.square.x, (float)this.square.y)) {
                            continue;
                        }
                        final ByteBufferWriter startPacket3 = udpConnection2.startPacket();
                        PacketTypes.PacketType.SyncIsoObject.doPacket(startPacket3);
                        startPacket3.putInt(this.square.getX());
                        startPacket3.putInt(this.square.getY());
                        startPacket3.putInt(this.square.getZ());
                        final byte b3 = (byte)this.square.getObjects().indexOf(this);
                        if (b3 != -1) {
                            startPacket3.putByte(b3);
                        }
                        else {
                            startPacket3.putByte((byte)this.square.getObjects().size());
                        }
                        startPacket3.putByte((byte)1);
                        startPacket3.putByte((byte)(this.Activated ? 1 : 0));
                        PacketTypes.PacketType.SyncIsoObject.send(udpConnection2);
                    }
                }
            }
        }
    }
    
    @Override
    public void update() {
        if ((!GameServer.bServer && !GameClient.bClient) || GameServer.bServer) {
            boolean b = false;
            if (!this.Activated) {
                this.lastMinuteStamp = -1L;
            }
            if (!this.lightRoom && this.canBeModified && this.Activated) {
                if (this.lastMinuteStamp == -1L) {
                    this.lastMinuteStamp = GameTime.instance.getMinutesStamp();
                }
                if (GameTime.instance.getMinutesStamp() > this.lastMinuteStamp) {
                    if (this.bulbBurnMinutes == -1) {
                        final int bulbBurnMinutes = SandboxOptions.instance.getElecShutModifier() * 24 * 60;
                        if (this.lastMinuteStamp < bulbBurnMinutes) {
                            this.bulbBurnMinutes = (int)this.lastMinuteStamp;
                        }
                        else {
                            this.bulbBurnMinutes = bulbBurnMinutes;
                        }
                    }
                    final long n = GameTime.instance.getMinutesStamp() - this.lastMinuteStamp;
                    this.lastMinuteStamp = GameTime.instance.getMinutesStamp();
                    if (this.Activated && this.hasLightBulb()) {
                        this.bulbBurnMinutes += (int)n;
                    }
                    this.nextBreakUpdate -= (int)n;
                    if (this.nextBreakUpdate <= 0) {
                        if (this.Activated && this.hasLightBulb() && Rand.Next(0, 1000) < this.bulbBurnMinutes / 10000) {
                            this.bulbBurnMinutes = 0;
                            this.setActive(false, true, true);
                            this.bulbItem = null;
                            IsoWorld.instance.getFreeEmitter().playSound("LightbulbBurnedOut", this.square);
                            b = true;
                        }
                        this.nextBreakUpdate = 60;
                    }
                    if (this.useBattery && this.Activated && this.hasLightBulb() && this.hasBattery && this.power > 0.0f) {
                        final float n2 = this.power - this.power % 0.01f;
                        this.power -= this.delta * n;
                        if (this.power < 0.0f) {
                            this.power = 0.0f;
                        }
                        if (n == 1L || this.power < n2) {
                            b = true;
                        }
                    }
                }
                if (this.useBattery && this.Activated && (this.power <= 0.0f || !this.hasBattery)) {
                    this.power = 0.0f;
                    this.setActive(false, true, true);
                    b = true;
                }
            }
            if (this.Activated && !this.hasLightBulb()) {
                this.setActive(false, true, true);
                b = true;
            }
            if (b && GameServer.bServer) {
                this.syncCustomizedSettings(null);
            }
        }
    }
    
    public boolean isActivated() {
        return this.Activated;
    }
    
    @Override
    public void addToWorld() {
        if (!this.Activated) {
            this.lastMinuteStamp = -1L;
        }
        if (!this.lightRoom && !this.lights.isEmpty()) {
            for (int i = 0; i < this.lights.size(); ++i) {
                IsoWorld.instance.CurrentCell.getLamppostPositions().add(this.lights.get(i));
            }
        }
        if (this.getCell() != null && this.canBeModified && !this.lightRoom && ((!GameServer.bServer && !GameClient.bClient) || GameServer.bServer)) {
            this.getCell().addToStaticUpdaterObjectList(this);
        }
    }
    
    @Override
    public void removeFromWorld() {
        if (!this.lightRoom && !this.lights.isEmpty()) {
            for (int i = 0; i < this.lights.size(); ++i) {
                this.lights.get(i).setActive(false);
                IsoWorld.instance.CurrentCell.removeLamppost(this.lights.get(i));
            }
            this.lights.clear();
        }
        if (this.square != null && this.lightRoom) {
            final IsoRoom room = this.square.getRoom();
            if (room != null) {
                room.lightSwitches.remove(this);
            }
        }
        super.removeFromWorld();
    }
    
    public static void chunkLoaded(final IsoChunk isoChunk) {
        final ArrayList<IsoRoom> list = new ArrayList<IsoRoom>();
        for (int i = 0; i < 10; ++i) {
            for (int j = 0; j < 10; ++j) {
                for (int k = 0; k < 8; ++k) {
                    final IsoGridSquare gridSquare = isoChunk.getGridSquare(i, j, k);
                    if (gridSquare != null) {
                        final IsoRoom room = gridSquare.getRoom();
                        if (room != null && room.hasLightSwitches() && !list.contains(room)) {
                            list.add(room);
                        }
                    }
                }
            }
        }
        for (int l = 0; l < list.size(); ++l) {
            final IsoRoom isoRoom = list.get(l);
            isoRoom.createLights(isoRoom.def.bLightsActive);
            for (int index = 0; index < isoRoom.roomLights.size(); ++index) {
                final IsoRoomLight isoRoomLight = isoRoom.roomLights.get(index);
                if (!isoChunk.roomLights.contains(isoRoomLight)) {
                    isoChunk.roomLights.add(isoRoomLight);
                }
            }
        }
    }
    
    public ArrayList<IsoLightSource> getLights() {
        return this.lights;
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.radio.devices;

import zombie.characters.IsoGameCharacter;
import zombie.radio.media.RecordedMedia;
import java.nio.ByteBuffer;
import java.io.IOException;
import zombie.iso.IsoGridSquare;
import zombie.core.network.ByteBufferWriter;
import zombie.network.PacketTypes;
import zombie.GameWindow;
import zombie.network.PacketTypesShort;
import zombie.iso.objects.IsoWaveSignal;
import zombie.WorldSoundManager;
import zombie.network.GameClient;
import zombie.core.raknet.UdpConnection;
import zombie.iso.IsoWorld;
import zombie.network.GameServer;
import zombie.SandboxOptions;
import zombie.vehicles.VehiclePart;
import zombie.iso.objects.IsoGenerator;
import zombie.characters.IsoPlayer;
import zombie.inventory.types.Radio;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.InventoryItem;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.iso.IsoObject;
import zombie.inventory.types.DrainableComboItem;
import java.util.Iterator;
import zombie.core.Rand;
import java.util.Map;
import zombie.radio.ZomboidRadio;
import zombie.core.Color;
import zombie.radio.media.MediaData;
import java.util.ArrayList;
import zombie.audio.BaseSoundEmitter;
import zombie.GameTime;

public final class DeviceData implements Cloneable
{
    private static final float deviceSpeakerSoundMod = 0.4f;
    private static final float deviceButtonSoundVol = 0.05f;
    protected String deviceName;
    protected boolean twoWay;
    protected int transmitRange;
    protected int micRange;
    protected boolean micIsMuted;
    protected float baseVolumeRange;
    protected float deviceVolume;
    protected boolean isPortable;
    protected boolean isTelevision;
    protected boolean isHighTier;
    protected boolean isTurnedOn;
    protected int channel;
    protected int minChannelRange;
    protected int maxChannelRange;
    protected DevicePresets presets;
    protected boolean isBatteryPowered;
    protected boolean hasBattery;
    protected float powerDelta;
    protected float useDelta;
    protected int lastRecordedDistance;
    protected int headphoneType;
    protected WaveSignalDevice parent;
    protected GameTime gameTime;
    protected boolean channelChangedRecently;
    protected BaseSoundEmitter emitter;
    protected ArrayList<Long> soundIDs;
    protected short mediaIndex;
    protected byte mediaType;
    protected String mediaItem;
    protected MediaData playingMedia;
    protected boolean isPlayingMedia;
    protected int mediaLineIndex;
    protected float lineCounter;
    protected String currentMediaLine;
    protected Color currentMediaColor;
    protected boolean isStoppingMedia;
    protected float stopMediaCounter;
    protected boolean noTransmit;
    private float soundCounterStatic;
    protected long radioLoopSound;
    protected boolean doTriggerWorldSound;
    protected long lastMinuteStamp;
    protected int listenCnt;
    float nextStaticSound;
    protected float signalCounter;
    protected float soundCounter;
    float minmod;
    float maxmod;
    
    public DeviceData() {
        this(null);
    }
    
    public DeviceData(final WaveSignalDevice parent) {
        this.deviceName = "WaveSignalDevice";
        this.twoWay = false;
        this.transmitRange = 1000;
        this.micRange = 5;
        this.micIsMuted = false;
        this.baseVolumeRange = 15.0f;
        this.deviceVolume = 1.0f;
        this.isPortable = false;
        this.isTelevision = false;
        this.isHighTier = false;
        this.isTurnedOn = false;
        this.channel = 88000;
        this.minChannelRange = 200;
        this.maxChannelRange = 1000000;
        this.presets = null;
        this.isBatteryPowered = true;
        this.hasBattery = true;
        this.powerDelta = 1.0f;
        this.useDelta = 0.001f;
        this.lastRecordedDistance = -1;
        this.headphoneType = -1;
        this.parent = null;
        this.gameTime = null;
        this.channelChangedRecently = false;
        this.emitter = null;
        this.soundIDs = new ArrayList<Long>();
        this.mediaIndex = -1;
        this.mediaType = -1;
        this.mediaItem = null;
        this.playingMedia = null;
        this.isPlayingMedia = false;
        this.mediaLineIndex = 0;
        this.lineCounter = 0.0f;
        this.currentMediaLine = null;
        this.currentMediaColor = null;
        this.isStoppingMedia = false;
        this.stopMediaCounter = 0.0f;
        this.noTransmit = false;
        this.soundCounterStatic = 0.0f;
        this.radioLoopSound = 0L;
        this.doTriggerWorldSound = false;
        this.lastMinuteStamp = -1L;
        this.listenCnt = 0;
        this.nextStaticSound = 0.0f;
        this.signalCounter = 0.0f;
        this.soundCounter = 0.0f;
        this.minmod = 1.5f;
        this.maxmod = 5.0f;
        this.parent = parent;
        this.presets = new DevicePresets();
        this.gameTime = GameTime.getInstance();
    }
    
    public void generatePresets() {
        if (this.presets == null) {
            this.presets = new DevicePresets();
        }
        this.presets.clearPresets();
        if (this.isTelevision) {
            final Map<Integer, String> getChannelList = ZomboidRadio.getInstance().GetChannelList("Television");
            if (getChannelList != null) {
                for (final Map.Entry<Integer, String> entry : getChannelList.entrySet()) {
                    if (entry.getKey() >= this.minChannelRange && entry.getKey() <= this.maxChannelRange) {
                        this.presets.addPreset(entry.getValue(), entry.getKey());
                    }
                }
            }
        }
        else {
            int n = this.twoWay ? 100 : 300;
            if (this.isHighTier) {
                n = 800;
            }
            final Map<Integer, String> getChannelList2 = ZomboidRadio.getInstance().GetChannelList("Emergency");
            if (getChannelList2 != null) {
                for (final Map.Entry<Integer, String> entry2 : getChannelList2.entrySet()) {
                    if (entry2.getKey() >= this.minChannelRange && entry2.getKey() <= this.maxChannelRange && Rand.Next(1000) < n) {
                        this.presets.addPreset(entry2.getValue(), entry2.getKey());
                    }
                }
            }
            final int n2 = this.twoWay ? 100 : 800;
            final Map<Integer, String> getChannelList3 = ZomboidRadio.getInstance().GetChannelList("Radio");
            if (getChannelList3 != null) {
                for (final Map.Entry<Integer, String> entry3 : getChannelList3.entrySet()) {
                    if (entry3.getKey() >= this.minChannelRange && entry3.getKey() <= this.maxChannelRange && Rand.Next(1000) < n2) {
                        this.presets.addPreset(entry3.getValue(), entry3.getKey());
                    }
                }
            }
            if (this.twoWay) {
                final Map<Integer, String> getChannelList4 = ZomboidRadio.getInstance().GetChannelList("Amateur");
                if (getChannelList4 != null) {
                    for (final Map.Entry<Integer, String> entry4 : getChannelList4.entrySet()) {
                        if (entry4.getKey() >= this.minChannelRange && entry4.getKey() <= this.maxChannelRange && Rand.Next(1000) < n2) {
                            this.presets.addPreset(entry4.getValue(), entry4.getKey());
                        }
                    }
                }
            }
            if (this.isHighTier) {
                final Map<Integer, String> getChannelList5 = ZomboidRadio.getInstance().GetChannelList("Military");
                if (getChannelList5 != null) {
                    for (final Map.Entry<Integer, String> entry5 : getChannelList5.entrySet()) {
                        if (entry5.getKey() >= this.minChannelRange && entry5.getKey() <= this.maxChannelRange && Rand.Next(1000) < 10) {
                            this.presets.addPreset(entry5.getValue(), entry5.getKey());
                        }
                    }
                }
            }
        }
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        final DeviceData deviceData = (DeviceData)super.clone();
        deviceData.setDevicePresets((DevicePresets)this.presets.clone());
        deviceData.setParent(null);
        return deviceData;
    }
    
    public DeviceData getClone() {
        DeviceData deviceData;
        try {
            deviceData = (DeviceData)this.clone();
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            deviceData = new DeviceData();
        }
        return deviceData;
    }
    
    public WaveSignalDevice getParent() {
        return this.parent;
    }
    
    public void setParent(final WaveSignalDevice parent) {
        this.parent = parent;
    }
    
    public DevicePresets getDevicePresets() {
        return this.presets;
    }
    
    public void setDevicePresets(DevicePresets presets) {
        if (presets == null) {
            presets = new DevicePresets();
        }
        this.presets = presets;
    }
    
    public int getMinChannelRange() {
        return this.minChannelRange;
    }
    
    public void setMinChannelRange(final int n) {
        this.minChannelRange = ((n >= 200 && n <= 1000000) ? n : 200);
    }
    
    public int getMaxChannelRange() {
        return this.maxChannelRange;
    }
    
    public void setMaxChannelRange(final int n) {
        this.maxChannelRange = ((n >= 200 && n <= 1000000) ? n : 1000000);
    }
    
    public boolean getIsHighTier() {
        return this.isHighTier;
    }
    
    public void setIsHighTier(final boolean isHighTier) {
        this.isHighTier = isHighTier;
    }
    
    public boolean getIsBatteryPowered() {
        return this.isBatteryPowered;
    }
    
    public void setIsBatteryPowered(final boolean isBatteryPowered) {
        this.isBatteryPowered = isBatteryPowered;
    }
    
    public boolean getHasBattery() {
        return this.hasBattery;
    }
    
    public void setHasBattery(final boolean hasBattery) {
        this.hasBattery = hasBattery;
    }
    
    public void addBattery(final DrainableComboItem drainableComboItem) {
        if (!this.hasBattery && drainableComboItem != null && drainableComboItem.getFullType().equals("Base.Battery")) {
            final ItemContainer container = drainableComboItem.getContainer();
            if (container != null) {
                if (container.getType().equals("floor") && drainableComboItem.getWorldItem() != null && drainableComboItem.getWorldItem().getSquare() != null) {
                    drainableComboItem.getWorldItem().getSquare().transmitRemoveItemFromSquare(drainableComboItem.getWorldItem());
                    drainableComboItem.getWorldItem().getSquare().getWorldObjects().remove(drainableComboItem.getWorldItem());
                    drainableComboItem.getWorldItem().getSquare().chunk.recalcHashCodeObjects();
                    drainableComboItem.getWorldItem().getSquare().getObjects().remove(drainableComboItem.getWorldItem());
                    drainableComboItem.setWorldItem(null);
                }
                this.powerDelta = drainableComboItem.getDelta();
                container.DoRemoveItem(drainableComboItem);
                this.hasBattery = true;
                this.transmitDeviceDataState((short)2);
            }
        }
    }
    
    public InventoryItem getBattery(final ItemContainer itemContainer) {
        if (this.hasBattery) {
            final DrainableComboItem drainableComboItem = (DrainableComboItem)InventoryItemFactory.CreateItem("Base.Battery");
            drainableComboItem.setDelta(this.powerDelta);
            this.powerDelta = 0.0f;
            itemContainer.AddItem(drainableComboItem);
            this.hasBattery = false;
            this.transmitDeviceDataState((short)2);
            return drainableComboItem;
        }
        return null;
    }
    
    public void transmitBattryChange() {
        this.transmitDeviceDataState((short)2);
    }
    
    public void addHeadphones(final InventoryItem inventoryItem) {
        if (this.headphoneType < 0 && (inventoryItem.getFullType().equals("Base.Headphones") || inventoryItem.getFullType().equals("Base.Earbuds"))) {
            final ItemContainer container = inventoryItem.getContainer();
            if (container != null) {
                if (container.getType().equals("floor") && inventoryItem.getWorldItem() != null && inventoryItem.getWorldItem().getSquare() != null) {
                    inventoryItem.getWorldItem().getSquare().transmitRemoveItemFromSquare(inventoryItem.getWorldItem());
                    inventoryItem.getWorldItem().getSquare().getWorldObjects().remove(inventoryItem.getWorldItem());
                    inventoryItem.getWorldItem().getSquare().chunk.recalcHashCodeObjects();
                    inventoryItem.getWorldItem().getSquare().getObjects().remove(inventoryItem.getWorldItem());
                    inventoryItem.setWorldItem(null);
                }
                final int headphoneType = inventoryItem.getFullType().equals("Base.Headphones") ? 0 : 1;
                container.DoRemoveItem(inventoryItem);
                this.setHeadphoneType(headphoneType);
                this.transmitDeviceDataState((short)6);
            }
        }
    }
    
    public InventoryItem getHeadphones(final ItemContainer itemContainer) {
        if (this.headphoneType >= 0) {
            InventoryItem inventoryItem = null;
            if (this.headphoneType == 0) {
                inventoryItem = InventoryItemFactory.CreateItem("Base.Headphones");
            }
            else if (this.headphoneType == 1) {
                inventoryItem = InventoryItemFactory.CreateItem("Base.Earbuds");
            }
            if (inventoryItem != null) {
                itemContainer.AddItem(inventoryItem);
            }
            this.setHeadphoneType(-1);
            this.transmitDeviceDataState((short)6);
        }
        return null;
    }
    
    public int getMicRange() {
        return this.micRange;
    }
    
    public void setMicRange(final int micRange) {
        this.micRange = micRange;
    }
    
    public boolean getMicIsMuted() {
        return this.micIsMuted;
    }
    
    public void setMicIsMuted(final boolean micIsMuted) {
        this.micIsMuted = micIsMuted;
        if (this.getParent() != null && this.getParent() instanceof Radio && ((Radio)this.getParent()).getEquipParent() != null && ((Radio)this.getParent()).getEquipParent() instanceof IsoPlayer) {
            ((IsoPlayer)((Radio)this.getParent()).getEquipParent()).updateEquippedRadioFreq();
        }
    }
    
    public int getHeadphoneType() {
        return this.headphoneType;
    }
    
    public void setHeadphoneType(final int headphoneType) {
        this.headphoneType = headphoneType;
    }
    
    public float getBaseVolumeRange() {
        return this.baseVolumeRange;
    }
    
    public void setBaseVolumeRange(final float baseVolumeRange) {
        this.baseVolumeRange = baseVolumeRange;
    }
    
    public float getDeviceVolume() {
        return this.deviceVolume;
    }
    
    public void setDeviceVolume(final float n) {
        this.deviceVolume = ((n < 0.0f) ? 0.0f : ((n > 1.0f) ? 1.0f : n));
        this.transmitDeviceDataState((short)4);
    }
    
    public void setDeviceVolumeRaw(final float n) {
        this.deviceVolume = ((n < 0.0f) ? 0.0f : ((n > 1.0f) ? 1.0f : n));
    }
    
    public boolean getIsTelevision() {
        return this.isTelevision;
    }
    
    public void setIsTelevision(final boolean isTelevision) {
        this.isTelevision = isTelevision;
    }
    
    public String getDeviceName() {
        return this.deviceName;
    }
    
    public void setDeviceName(final String deviceName) {
        this.deviceName = deviceName;
    }
    
    public boolean getIsTwoWay() {
        return this.twoWay;
    }
    
    public void setIsTwoWay(final boolean twoWay) {
        this.twoWay = twoWay;
    }
    
    public int getTransmitRange() {
        return this.transmitRange;
    }
    
    public void setTransmitRange(final int n) {
        this.transmitRange = ((n > 0) ? n : 0);
    }
    
    public boolean getIsPortable() {
        return this.isPortable;
    }
    
    public void setIsPortable(final boolean isPortable) {
        this.isPortable = isPortable;
    }
    
    public boolean getIsTurnedOn() {
        return this.isTurnedOn;
    }
    
    public void setIsTurnedOn(final boolean isTurnedOn) {
        if (this.canBePoweredHere()) {
            if (!this.isBatteryPowered || this.powerDelta > 0.0f) {
                this.isTurnedOn = isTurnedOn;
            }
            else {
                this.isTurnedOn = false;
            }
            this.playSoundSend("RadioButton", false);
            this.transmitDeviceDataState((short)0);
        }
        else if (this.isTurnedOn) {
            this.playSoundSend("RadioButton", this.isTurnedOn = false);
            this.transmitDeviceDataState((short)0);
        }
        if (this.getParent() != null && this.getParent() instanceof Radio && ((Radio)this.getParent()).getEquipParent() != null && ((Radio)this.getParent()).getEquipParent() instanceof IsoPlayer) {
            ((IsoPlayer)((Radio)this.getParent()).getEquipParent()).updateEquippedRadioFreq();
        }
        IsoGenerator.updateGenerator(this.getParent().getSquare());
    }
    
    public void setTurnedOnRaw(final boolean isTurnedOn) {
        this.isTurnedOn = isTurnedOn;
        if (this.getParent() != null && this.getParent() instanceof Radio && ((Radio)this.getParent()).getEquipParent() != null && ((Radio)this.getParent()).getEquipParent() instanceof IsoPlayer) {
            ((IsoPlayer)((Radio)this.getParent()).getEquipParent()).updateEquippedRadioFreq();
        }
    }
    
    public boolean canBePoweredHere() {
        if (this.isBatteryPowered) {
            return true;
        }
        if (this.parent instanceof VehiclePart) {
            final VehiclePart vehiclePart = (VehiclePart)this.parent;
            return (vehiclePart.getItemType() == null || vehiclePart.getItemType().isEmpty() || vehiclePart.getInventoryItem() != null) && vehiclePart.hasDevicePower();
        }
        boolean b = false;
        if (GameTime.getInstance().getNightsSurvived() < SandboxOptions.instance.getElecShutModifier()) {
            b = true;
        }
        if (this.parent == null || this.parent.getSquare() == null) {
            b = false;
        }
        else if (this.parent.getSquare().haveElectricity()) {
            b = true;
        }
        else if (this.parent.getSquare().getRoom() == null) {
            b = false;
        }
        return b;
    }
    
    public void setRandomChannel() {
        if (this.presets != null && this.presets.getPresets().size() > 0) {
            this.channel = this.presets.getPresets().get(Rand.Next(0, this.presets.getPresets().size())).getFrequency();
        }
        else {
            this.channel = Rand.Next(this.minChannelRange, this.maxChannelRange);
            this.channel -= this.channel % 200;
        }
    }
    
    public int getChannel() {
        return this.channel;
    }
    
    public void setChannel(final int n) {
        this.setChannel(n, true);
    }
    
    public void setChannel(final int channel, final boolean b) {
        if (channel >= this.minChannelRange && channel <= this.maxChannelRange) {
            this.channel = channel;
            this.playSoundSend("RadioButton", false);
            if (this.isTelevision) {
                this.playSoundSend("TelevisionZap", true);
            }
            else {
                this.playSoundSend("RadioZap", true);
            }
            if (this.radioLoopSound > 0L) {
                this.emitter.stopSound(this.radioLoopSound);
                this.radioLoopSound = 0L;
            }
            this.transmitDeviceDataState((short)1);
            if (b) {
                this.TriggerPlayerListening(true);
            }
        }
    }
    
    public void setChannelRaw(final int channel) {
        this.channel = channel;
    }
    
    public float getUseDelta() {
        return this.useDelta;
    }
    
    public void setUseDelta(final float n) {
        this.useDelta = n / 60.0f;
    }
    
    public float getPower() {
        return this.powerDelta;
    }
    
    public void setPower(float powerDelta) {
        if (powerDelta > 1.0f) {
            powerDelta = 1.0f;
        }
        if (powerDelta < 0.0f) {
            powerDelta = 0.0f;
        }
        this.powerDelta = powerDelta;
    }
    
    public void TriggerPlayerListening(final boolean b) {
        if (this.isTurnedOn) {
            ZomboidRadio.getInstance().PlayerListensChannel(this.channel, true, this.isTelevision);
        }
    }
    
    public void playSoundSend(final String s, final boolean b) {
        this.playSound(s, b ? (this.deviceVolume * 0.4f) : 0.05f, true);
    }
    
    public void playSoundLocal(final String s, final boolean b) {
        this.playSound(s, b ? (this.deviceVolume * 0.4f) : 0.05f, false);
    }
    
    public void playSound(final String s, final float n, final boolean b) {
        if (GameServer.bServer) {
            return;
        }
        this.setEmitterAndPos();
        if (this.emitter != null) {
            this.emitter.setVolume(b ? this.emitter.playSound(s) : this.emitter.playSoundImpl(s, (IsoObject)null), n);
        }
    }
    
    public void cleanSoundsAndEmitter() {
        if (this.emitter != null) {
            this.emitter.stopAll();
            IsoWorld.instance.returnOwnershipOfEmitter(this.emitter);
            this.emitter = null;
            this.radioLoopSound = 0L;
        }
    }
    
    protected void setEmitterAndPos() {
        IsoObject instance = null;
        if (this.parent != null && this.parent instanceof IsoObject) {
            instance = (IsoObject)this.parent;
        }
        else if (this.parent != null && this.parent instanceof Radio) {
            instance = IsoPlayer.getInstance();
        }
        if (instance != null) {
            if (this.emitter == null) {
                this.emitter = IsoWorld.instance.getFreeEmitter(instance.getX() + 0.5f, instance.getY() + 0.5f, (float)(int)instance.getZ());
                IsoWorld.instance.takeOwnershipOfEmitter(this.emitter);
            }
            else {
                this.emitter.setPos(instance.getX() + 0.5f, instance.getY() + 0.5f, (float)(int)instance.getZ());
            }
            if (this.radioLoopSound != 0L) {
                this.emitter.setVolume(this.radioLoopSound, this.deviceVolume * 0.4f);
            }
        }
    }
    
    protected void updateEmitter() {
        if (GameServer.bServer) {
            return;
        }
        if (this.isTurnedOn) {
            this.setEmitterAndPos();
            if (this.emitter != null) {
                if (this.signalCounter > 0.0f && !this.emitter.isPlaying("RadioTalk")) {
                    if (this.radioLoopSound > 0L) {
                        this.emitter.stopSound(this.radioLoopSound);
                    }
                    this.radioLoopSound = this.emitter.playSoundImpl("RadioTalk", (IsoObject)null);
                    this.emitter.setVolume(this.radioLoopSound, this.deviceVolume * 0.4f);
                }
                final String s = this.isTelevision ? "TelevisionTestBeep" : "RadioStatic";
                if (this.radioLoopSound == 0L || (this.signalCounter <= 0.0f && !this.emitter.isPlaying(s))) {
                    if (this.radioLoopSound > 0L) {
                        this.emitter.stopSound(this.radioLoopSound);
                        if (this.isTelevision) {
                            this.playSoundLocal("TelevisionZap", true);
                        }
                        else {
                            this.playSoundLocal("RadioZap", true);
                        }
                    }
                    this.radioLoopSound = this.emitter.playSoundImpl(s, (IsoObject)null);
                    this.emitter.setVolume(this.radioLoopSound, this.deviceVolume * 0.4f);
                }
                this.emitter.tick();
            }
            return;
        }
        if (this.emitter != null && this.emitter.isPlaying("RadioButton")) {
            if (this.radioLoopSound > 0L) {
                this.emitter.stopSound(this.radioLoopSound);
            }
            this.setEmitterAndPos();
            this.emitter.tick();
            return;
        }
        this.cleanSoundsAndEmitter();
    }
    
    public BaseSoundEmitter getEmitter() {
        return this.emitter;
    }
    
    public void update(final boolean b, final boolean b2) {
        if (this.lastMinuteStamp == -1L) {
            this.lastMinuteStamp = this.gameTime.getMinutesStamp();
        }
        if (this.gameTime.getMinutesStamp() > this.lastMinuteStamp) {
            final long n = this.gameTime.getMinutesStamp() - this.lastMinuteStamp;
            this.lastMinuteStamp = this.gameTime.getMinutesStamp();
            this.listenCnt += (int)n;
            if (this.listenCnt >= 10) {
                this.listenCnt = 0;
            }
            if (!GameServer.bServer && this.isTurnedOn && b2 && (this.listenCnt == 0 || this.listenCnt == 5)) {
                this.TriggerPlayerListening(true);
            }
            if (this.isTurnedOn && this.isBatteryPowered && this.powerDelta > 0.0f) {
                final float n2 = this.powerDelta - this.powerDelta % 0.01f;
                this.setPower(this.powerDelta - this.useDelta * n);
                if (this.listenCnt == 0 || this.powerDelta == 0.0f || this.powerDelta < n2) {
                    if (b && GameServer.bServer) {
                        this.transmitDeviceDataStateServer((short)3, null);
                    }
                    else if (!b && GameClient.bClient) {
                        this.transmitDeviceDataState((short)3);
                    }
                }
            }
        }
        if (this.isTurnedOn && ((this.isBatteryPowered && this.powerDelta <= 0.0f) || !this.canBePoweredHere())) {
            this.isTurnedOn = false;
            if (b && GameServer.bServer) {
                this.transmitDeviceDataStateServer((short)0, null);
            }
            else if (!b && GameClient.bClient) {
                this.transmitDeviceDataState((short)0);
            }
        }
        this.updateMediaPlaying();
        this.updateEmitter();
        this.updateSimple();
    }
    
    public void updateSimple() {
        if (this.signalCounter >= 0.0f) {
            this.signalCounter -= 1.25f * GameTime.getInstance().getMultiplier();
        }
        if (this.soundCounter >= 0.0f) {
            this.soundCounter -= (float)(1.25 * GameTime.getInstance().getMultiplier());
        }
        if (this.signalCounter <= 0.0f && this.lastRecordedDistance >= 0) {
            this.lastRecordedDistance = -1;
        }
        this.updateStaticSounds();
        if (GameClient.bClient) {
            this.updateEmitter();
        }
        if (this.doTriggerWorldSound && this.soundCounter <= 0.0f) {
            if (this.isTurnedOn && this.deviceVolume > 0.0f && (!this.isInventoryDevice() || this.headphoneType < 0) && ((!GameClient.bClient && !GameServer.bServer) || (GameClient.bClient && this.isInventoryDevice()) || (GameServer.bServer && !this.isInventoryDevice()))) {
                IsoObject isoObject = null;
                if (this.parent != null && this.parent instanceof IsoObject) {
                    isoObject = (IsoObject)this.parent;
                }
                else if (this.parent != null && this.parent instanceof Radio) {
                    isoObject = IsoPlayer.getInstance();
                }
                else if (this.parent instanceof VehiclePart) {
                    isoObject = ((VehiclePart)this.parent).getVehicle();
                }
                if (isoObject != null) {
                    final int n = (int)(100.0f * this.deviceVolume);
                    WorldSoundManager.instance.addSoundRepeating(isoObject, (int)isoObject.getX(), (int)isoObject.getY(), (int)isoObject.getZ(), this.getDeviceSoundVolumeRange(), n, n > 50);
                }
            }
            this.doTriggerWorldSound = false;
            this.soundCounter = (float)(300 + Rand.Next(0, 300));
        }
    }
    
    private void updateStaticSounds() {
        if (!this.isTurnedOn) {
            return;
        }
        this.nextStaticSound -= GameTime.getInstance().getMultiplier();
        if (this.nextStaticSound <= 0.0f) {
            if (this.parent != null && this.signalCounter <= 0.0f && !this.isNoTransmit() && !this.isPlayingMedia()) {
                this.parent.AddDeviceText(ZomboidRadio.getInstance().getRandomBzztFzzt(), 1.0f, 1.0f, 1.0f, null, -1);
                this.doTriggerWorldSound = true;
            }
            this.setNextStaticSound();
        }
    }
    
    private void setNextStaticSound() {
        this.nextStaticSound = Rand.Next(250.0f, 1500.0f);
    }
    
    public int getDeviceVolumeRange() {
        return 5 + (int)(this.baseVolumeRange * this.deviceVolume);
    }
    
    public int getDeviceSoundVolumeRange() {
        if (this.isInventoryDevice()) {
            final Radio radio = (Radio)this.getParent();
            if (radio.getPlayer() != null && radio.getPlayer().getSquare() != null && radio.getPlayer().getSquare().getRoom() != null) {
                return 3 + (int)(this.baseVolumeRange * 0.4f * this.deviceVolume);
            }
            return 5 + (int)(this.baseVolumeRange * this.deviceVolume);
        }
        else {
            if (!this.isIsoDevice()) {
                return 5 + (int)(this.baseVolumeRange / 2.0f * this.deviceVolume);
            }
            final IsoWaveSignal isoWaveSignal = (IsoWaveSignal)this.getParent();
            if (isoWaveSignal.getSquare() != null && isoWaveSignal.getSquare().getRoom() != null) {
                return 3 + (int)(this.baseVolumeRange * 0.5f * this.deviceVolume);
            }
            return 5 + (int)(this.baseVolumeRange * 0.75f * this.deviceVolume);
        }
    }
    
    public void doReceiveSignal(final int lastRecordedDistance) {
        if (this.isTurnedOn) {
            this.lastRecordedDistance = lastRecordedDistance;
            if (this.deviceVolume > 0.0f && (this.isIsoDevice() || this.headphoneType < 0)) {
                IsoObject isoObject = null;
                if (this.parent != null && this.parent instanceof IsoObject) {
                    isoObject = (IsoObject)this.parent;
                }
                else if (this.parent != null && this.parent instanceof Radio) {
                    isoObject = IsoPlayer.getInstance();
                }
                else if (this.parent instanceof VehiclePart) {
                    isoObject = ((VehiclePart)this.parent).getVehicle();
                }
                if (isoObject != null && this.soundCounter <= 0.0f) {
                    final int n = (int)(100.0f * this.deviceVolume);
                    WorldSoundManager.instance.addSound(isoObject, (int)isoObject.getX(), (int)isoObject.getY(), (int)isoObject.getZ(), this.getDeviceSoundVolumeRange(), n, n > 50);
                    this.soundCounter = 120.0f;
                }
            }
            this.signalCounter = 300.0f;
            this.doTriggerWorldSound = true;
            this.setNextStaticSound();
        }
    }
    
    public boolean isReceivingSignal() {
        return this.signalCounter > 0.0f;
    }
    
    public int getLastRecordedDistance() {
        return this.lastRecordedDistance;
    }
    
    public boolean isIsoDevice() {
        return this.getParent() != null && this.getParent() instanceof IsoWaveSignal;
    }
    
    public boolean isInventoryDevice() {
        return this.getParent() != null && this.getParent() instanceof Radio;
    }
    
    public boolean isVehicleDevice() {
        return this.getParent() instanceof VehiclePart;
    }
    
    public void transmitPresets() {
        this.transmitDeviceDataState((short)5);
    }
    
    private void transmitDeviceDataState(final short n) {
        if (GameClient.bClient) {
            try {
                this.sendDeviceDataStatePacket(GameClient.connection, n);
            }
            catch (Exception ex) {
                System.out.print(ex.getMessage());
            }
        }
    }
    
    private void transmitDeviceDataStateServer(final short n, final UdpConnection udpConnection) {
        if (GameServer.bServer) {
            try {
                for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                    final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
                    if (udpConnection == null || udpConnection != udpConnection2) {
                        this.sendDeviceDataStatePacket(udpConnection2, n);
                    }
                }
            }
            catch (Exception ex) {
                System.out.print(ex.getMessage());
            }
        }
    }
    
    private void sendDeviceDataStatePacket(final UdpConnection udpConnection, final short n) throws IOException {
        final ByteBufferWriter startPacket = udpConnection.startPacket();
        PacketTypesShort.doPacket((short)1004, startPacket);
        boolean b = false;
        if (this.isIsoDevice()) {
            final IsoWaveSignal isoWaveSignal = (IsoWaveSignal)this.getParent();
            final IsoGridSquare square = isoWaveSignal.getSquare();
            if (square != null) {
                startPacket.putByte((byte)1);
                startPacket.putInt(square.getX());
                startPacket.putInt(square.getY());
                startPacket.putInt(square.getZ());
                startPacket.putInt(square.getObjects().indexOf(isoWaveSignal));
                b = true;
            }
        }
        else if (this.isInventoryDevice()) {
            final Radio radio = (Radio)this.getParent();
            IsoPlayer isoPlayer = null;
            if (radio.getEquipParent() != null && radio.getEquipParent() instanceof IsoPlayer) {
                isoPlayer = (IsoPlayer)radio.getEquipParent();
            }
            if (isoPlayer != null) {
                startPacket.putByte((byte)0);
                if (GameServer.bServer) {
                    startPacket.putShort((short)((isoPlayer != null) ? isoPlayer.OnlineID : -1));
                }
                else {
                    startPacket.putByte((byte)isoPlayer.PlayerIndex);
                }
                if (isoPlayer.getPrimaryHandItem() == radio) {
                    startPacket.putByte((byte)1);
                }
                else if (isoPlayer.getSecondaryHandItem() == radio) {
                    startPacket.putByte((byte)2);
                }
                else {
                    startPacket.putByte((byte)0);
                }
                b = true;
            }
        }
        else if (this.isVehicleDevice()) {
            final VehiclePart vehiclePart = (VehiclePart)this.getParent();
            startPacket.putByte((byte)2);
            startPacket.putShort(vehiclePart.getVehicle().VehicleID);
            startPacket.putShort((short)vehiclePart.getIndex());
            b = true;
        }
        if (b) {
            startPacket.putShort(n);
            switch (n) {
                case 0: {
                    startPacket.putByte((byte)(this.isTurnedOn ? 1 : 0));
                    break;
                }
                case 1: {
                    startPacket.putInt(this.channel);
                    break;
                }
                case 2: {
                    startPacket.putByte((byte)(this.hasBattery ? 1 : 0));
                    startPacket.putFloat(this.powerDelta);
                    break;
                }
                case 3: {
                    startPacket.putFloat(this.powerDelta);
                    break;
                }
                case 4: {
                    startPacket.putFloat(this.deviceVolume);
                    break;
                }
                case 5: {
                    startPacket.putInt(this.presets.getPresets().size());
                    for (final PresetEntry presetEntry : this.presets.getPresets()) {
                        GameWindow.WriteString(startPacket.bb, presetEntry.getName());
                        startPacket.putInt(presetEntry.getFrequency());
                    }
                    break;
                }
                case 6: {
                    startPacket.putInt(this.headphoneType);
                    break;
                }
                case 7: {
                    startPacket.putShort(this.mediaIndex);
                    startPacket.putByte((byte)((this.mediaItem != null) ? 1 : 0));
                    if (this.mediaItem != null) {
                        GameWindow.WriteString(startPacket.bb, this.mediaItem);
                        break;
                    }
                    break;
                }
                case 8: {
                    if (!GameServer.bServer) {
                        break;
                    }
                    startPacket.putShort(this.mediaIndex);
                    startPacket.putByte((byte)((this.mediaItem != null) ? 1 : 0));
                    if (this.mediaItem != null) {
                        GameWindow.WriteString(startPacket.bb, this.mediaItem);
                        break;
                    }
                    break;
                }
                case 10: {
                    if (GameServer.bServer) {
                        startPacket.putShort(this.mediaIndex);
                        startPacket.putInt(this.mediaLineIndex);
                        break;
                    }
                    break;
                }
            }
            PacketTypes.PacketType.PacketTypeShort.send(udpConnection);
        }
        else {
            udpConnection.cancelPacket();
        }
    }
    
    public void receiveDeviceDataStatePacket(final ByteBuffer byteBuffer, final UdpConnection udpConnection) throws IOException {
        if (!GameClient.bClient && !GameServer.bServer) {
            return;
        }
        final boolean bServer = GameServer.bServer;
        final boolean b = this.isIsoDevice() || this.isVehicleDevice();
        final short short1 = byteBuffer.getShort();
        switch (short1) {
            case 0: {
                if (bServer && b) {
                    this.setIsTurnedOn(byteBuffer.get() == 1);
                }
                else {
                    this.isTurnedOn = (byteBuffer.get() == 1);
                }
                if (bServer) {
                    this.transmitDeviceDataStateServer(short1, b ? null : udpConnection);
                    break;
                }
                break;
            }
            case 1: {
                final int int1 = byteBuffer.getInt();
                if (bServer && b) {
                    this.setChannel(int1);
                }
                else {
                    this.channel = int1;
                }
                if (bServer) {
                    this.transmitDeviceDataStateServer(short1, b ? null : udpConnection);
                    break;
                }
                break;
            }
            case 2: {
                final boolean b2 = byteBuffer.get() == 1;
                final float float1 = byteBuffer.getFloat();
                if (bServer && b) {
                    this.hasBattery = b2;
                    this.setPower(float1);
                }
                else {
                    this.hasBattery = b2;
                    this.powerDelta = float1;
                }
                if (bServer) {
                    this.transmitDeviceDataStateServer(short1, b ? null : udpConnection);
                    break;
                }
                break;
            }
            case 3: {
                final float float2 = byteBuffer.getFloat();
                if (bServer && b) {
                    this.setPower(float2);
                }
                else {
                    this.powerDelta = float2;
                }
                if (bServer) {
                    this.transmitDeviceDataStateServer(short1, b ? null : udpConnection);
                    break;
                }
                break;
            }
            case 4: {
                final float float3 = byteBuffer.getFloat();
                if (bServer && b) {
                    this.setDeviceVolume(float3);
                }
                else {
                    this.deviceVolume = float3;
                }
                if (bServer) {
                    this.transmitDeviceDataStateServer(short1, b ? null : udpConnection);
                    break;
                }
                break;
            }
            case 5: {
                for (int int2 = byteBuffer.getInt(), i = 0; i < int2; ++i) {
                    final String readString = GameWindow.ReadString(byteBuffer);
                    final int int3 = byteBuffer.getInt();
                    if (i < this.presets.getPresets().size()) {
                        final PresetEntry presetEntry = this.presets.getPresets().get(i);
                        if (!presetEntry.getName().equals(readString) || presetEntry.getFrequency() != int3) {
                            presetEntry.setName(readString);
                            presetEntry.setFrequency(int3);
                        }
                    }
                    else {
                        this.presets.addPreset(readString, int3);
                    }
                }
                if (bServer) {
                    this.transmitDeviceDataStateServer((short)5, b ? null : udpConnection);
                    break;
                }
                break;
            }
            case 6: {
                this.headphoneType = byteBuffer.getInt();
                if (bServer) {
                    this.transmitDeviceDataStateServer(short1, b ? null : udpConnection);
                    break;
                }
                break;
            }
            case 7: {
                this.mediaIndex = byteBuffer.getShort();
                if (byteBuffer.get() == 1) {
                    this.mediaItem = GameWindow.ReadString(byteBuffer);
                }
                if (bServer) {
                    this.transmitDeviceDataStateServer(short1, b ? null : udpConnection);
                    break;
                }
                break;
            }
            case 8: {
                if (GameServer.bServer) {
                    this.StartPlayMedia();
                    break;
                }
                this.mediaIndex = byteBuffer.getShort();
                if (byteBuffer.get() == 1) {
                    this.mediaItem = GameWindow.ReadString(byteBuffer);
                }
                this.isPlayingMedia = true;
                this.televisionMediaSwitch();
                break;
            }
            case 9: {
                if (GameServer.bServer) {
                    this.StopPlayMedia();
                    break;
                }
                this.isPlayingMedia = false;
                this.televisionMediaSwitch();
                break;
            }
            case 10: {
                if (!GameClient.bClient) {
                    break;
                }
                this.mediaIndex = byteBuffer.getShort();
                final int int4 = byteBuffer.getInt();
                final MediaData mediaData = this.getMediaData();
                if (mediaData != null && int4 >= 0 && int4 < mediaData.getLineCount()) {
                    final MediaData.MediaLineData line = mediaData.getLine(int4);
                    final String translatedText = line.getTranslatedText();
                    final Color color = line.getColor();
                    final RecordedMedia recordedMedia = ZomboidRadio.getInstance().getRecordedMedia();
                    String codes = null;
                    if (!recordedMedia.hasListenedLineAndAdd(line.getTextGuid())) {
                        codes = line.getCodes();
                    }
                    this.parent.AddDeviceText(translatedText, color.r, color.g, color.b, codes, 0);
                    break;
                }
                break;
            }
        }
    }
    
    public void save(final ByteBuffer byteBuffer, final boolean b) throws IOException {
        GameWindow.WriteString(byteBuffer, this.deviceName);
        byteBuffer.put((byte)(this.twoWay ? 1 : 0));
        byteBuffer.putInt(this.transmitRange);
        byteBuffer.putInt(this.micRange);
        byteBuffer.put((byte)(this.micIsMuted ? 1 : 0));
        byteBuffer.putFloat(this.baseVolumeRange);
        byteBuffer.putFloat(this.deviceVolume);
        byteBuffer.put((byte)(this.isPortable ? 1 : 0));
        byteBuffer.put((byte)(this.isTelevision ? 1 : 0));
        byteBuffer.put((byte)(this.isHighTier ? 1 : 0));
        byteBuffer.put((byte)(this.isTurnedOn ? 1 : 0));
        byteBuffer.putInt(this.channel);
        byteBuffer.putInt(this.minChannelRange);
        byteBuffer.putInt(this.maxChannelRange);
        byteBuffer.put((byte)(this.isBatteryPowered ? 1 : 0));
        byteBuffer.put((byte)(this.hasBattery ? 1 : 0));
        byteBuffer.putFloat(this.powerDelta);
        byteBuffer.putFloat(this.useDelta);
        byteBuffer.putInt(this.headphoneType);
        if (this.presets != null) {
            byteBuffer.put((byte)1);
            this.presets.save(byteBuffer, b);
        }
        else {
            byteBuffer.put((byte)0);
        }
        byteBuffer.putShort(this.mediaIndex);
        byteBuffer.put(this.mediaType);
        byteBuffer.put((byte)((this.mediaItem != null) ? 1 : 0));
        if (this.mediaItem != null) {
            GameWindow.WriteString(byteBuffer, this.mediaItem);
        }
        byteBuffer.put((byte)(this.noTransmit ? 1 : 0));
    }
    
    public void load(final ByteBuffer byteBuffer, final int n, final boolean b) throws IOException {
        if (this.presets == null) {
            this.presets = new DevicePresets();
        }
        if (n >= 69) {
            this.deviceName = GameWindow.ReadString(byteBuffer);
            this.twoWay = (byteBuffer.get() == 1);
            this.transmitRange = byteBuffer.getInt();
            this.micRange = byteBuffer.getInt();
            this.micIsMuted = (byteBuffer.get() == 1);
            this.baseVolumeRange = byteBuffer.getFloat();
            this.deviceVolume = byteBuffer.getFloat();
            this.isPortable = (byteBuffer.get() == 1);
            this.isTelevision = (byteBuffer.get() == 1);
            this.isHighTier = (byteBuffer.get() == 1);
            this.isTurnedOn = (byteBuffer.get() == 1);
            this.channel = byteBuffer.getInt();
            this.minChannelRange = byteBuffer.getInt();
            this.maxChannelRange = byteBuffer.getInt();
            this.isBatteryPowered = (byteBuffer.get() == 1);
            this.hasBattery = (byteBuffer.get() == 1);
            this.powerDelta = byteBuffer.getFloat();
            this.useDelta = byteBuffer.getFloat();
            this.headphoneType = byteBuffer.getInt();
            if (byteBuffer.get() == 1) {
                this.presets.load(byteBuffer, n, b);
            }
        }
        if (n >= 181) {
            this.mediaIndex = byteBuffer.getShort();
            this.mediaType = byteBuffer.get();
            if (byteBuffer.get() == 1) {
                this.mediaItem = GameWindow.ReadString(byteBuffer);
            }
            this.noTransmit = (byteBuffer.get() == 1);
        }
    }
    
    public boolean hasMedia() {
        return this.mediaIndex >= 0;
    }
    
    public short getMediaIndex() {
        return this.mediaIndex;
    }
    
    public void setMediaIndex(final short mediaIndex) {
        this.mediaIndex = mediaIndex;
    }
    
    public byte getMediaType() {
        return this.mediaType;
    }
    
    public void setMediaType(final byte mediaType) {
        this.mediaType = mediaType;
    }
    
    public void addMediaItem(final InventoryItem inventoryItem) {
        if (this.mediaIndex < 0 && inventoryItem.isRecordedMedia() && inventoryItem.getMediaType() == this.mediaType) {
            final ItemContainer container = inventoryItem.getContainer();
            if (container != null) {
                if (container.getType().equals("floor") && inventoryItem.getWorldItem() != null && inventoryItem.getWorldItem().getSquare() != null) {
                    inventoryItem.getWorldItem().getSquare().transmitRemoveItemFromSquare(inventoryItem.getWorldItem());
                    inventoryItem.getWorldItem().getSquare().getWorldObjects().remove(inventoryItem.getWorldItem());
                    inventoryItem.getWorldItem().getSquare().chunk.recalcHashCodeObjects();
                    inventoryItem.getWorldItem().getSquare().getObjects().remove(inventoryItem.getWorldItem());
                    inventoryItem.setWorldItem(null);
                }
                this.mediaIndex = inventoryItem.getRecordedMediaIndex();
                this.mediaItem = inventoryItem.getFullType();
                container.DoRemoveItem(inventoryItem);
                final IsoGameCharacter character = container.getCharacter();
                if (character != null && character.isEquipped(inventoryItem)) {
                    character.removeFromHands(inventoryItem);
                }
                this.transmitDeviceDataState((short)7);
            }
        }
    }
    
    public InventoryItem removeMediaItem(final ItemContainer itemContainer) {
        if (this.hasMedia()) {
            final InventoryItem createItem = InventoryItemFactory.CreateItem(this.mediaItem);
            createItem.setRecordedMediaIndex(this.mediaIndex);
            itemContainer.AddItem(createItem);
            this.mediaIndex = -1;
            this.mediaItem = null;
            if (this.isPlayingMedia()) {
                this.StopPlayMedia();
            }
            this.transmitDeviceDataState((short)7);
            return createItem;
        }
        return null;
    }
    
    public boolean isPlayingMedia() {
        return this.isPlayingMedia;
    }
    
    public void StartPlayMedia() {
        if (GameClient.bClient) {
            this.transmitDeviceDataState((short)8);
        }
        else if (!this.isPlayingMedia() && this.getIsTurnedOn() && this.hasMedia()) {
            this.playingMedia = ZomboidRadio.getInstance().getRecordedMedia().getMediaDataFromIndex(this.mediaIndex);
            if (this.playingMedia != null) {
                this.isPlayingMedia = true;
                this.mediaLineIndex = 0;
                this.prePlayingMedia();
                if (GameServer.bServer) {
                    this.transmitDeviceDataStateServer((short)8, null);
                }
            }
        }
    }
    
    private void prePlayingMedia() {
        this.lineCounter = 60.0f * this.maxmod * 0.5f;
        this.televisionMediaSwitch();
    }
    
    private void postPlayingMedia() {
        this.isStoppingMedia = true;
        this.stopMediaCounter = 60.0f * this.maxmod * 0.5f;
        this.televisionMediaSwitch();
    }
    
    private void televisionMediaSwitch() {
        if (this.mediaType == 1) {
            ZomboidRadio.getInstance().getRandomBzztFzzt();
            this.parent.AddDeviceText(ZomboidRadio.getInstance().getRandomBzztFzzt(), 0.5f, 0.5f, 0.5f, null, 0);
            this.playSoundLocal("TelevisionZap", true);
        }
    }
    
    public void StopPlayMedia() {
        if (GameClient.bClient) {
            this.transmitDeviceDataState((short)9);
        }
        else {
            this.playingMedia = null;
            this.postPlayingMedia();
            if (GameServer.bServer) {
                this.transmitDeviceDataStateServer((short)9, null);
            }
        }
    }
    
    public void updateMediaPlaying() {
        if (GameClient.bClient) {
            return;
        }
        if (this.isStoppingMedia) {
            this.stopMediaCounter -= 1.25f * GameTime.getInstance().getMultiplier();
            if (this.stopMediaCounter <= 0.0f) {
                this.isPlayingMedia = false;
                this.isStoppingMedia = false;
            }
            return;
        }
        if (this.hasMedia() && this.isPlayingMedia()) {
            if (!this.getIsTurnedOn()) {
                this.StopPlayMedia();
                return;
            }
            if (this.playingMedia != null) {
                this.lineCounter -= 1.25f * GameTime.getInstance().getMultiplier();
                if (this.lineCounter <= 0.0f) {
                    final MediaData.MediaLineData line = this.playingMedia.getLine(this.mediaLineIndex);
                    if (line != null) {
                        final String translatedText = line.getTranslatedText();
                        final Color color = line.getColor();
                        this.lineCounter = translatedText.length() / 10.0f * 60.0f;
                        if (this.lineCounter < 60.0f * this.minmod) {
                            this.lineCounter = 60.0f * this.minmod;
                        }
                        else if (this.lineCounter > 60.0f * this.maxmod) {
                            this.lineCounter = 60.0f * this.maxmod;
                        }
                        if (GameServer.bServer) {
                            this.currentMediaLine = translatedText;
                            this.currentMediaColor = color;
                            this.transmitDeviceDataStateServer((short)10, null);
                        }
                        else {
                            final RecordedMedia recordedMedia = ZomboidRadio.getInstance().getRecordedMedia();
                            String codes = null;
                            if (!recordedMedia.hasListenedLineAndAdd(line.getTextGuid())) {
                                codes = line.getCodes();
                            }
                            this.parent.AddDeviceText(translatedText, color.r, color.g, color.b, codes, 0);
                        }
                        ++this.mediaLineIndex;
                    }
                    else {
                        this.StopPlayMedia();
                    }
                }
            }
        }
    }
    
    public MediaData getMediaData() {
        if (this.mediaIndex >= 0) {
            return ZomboidRadio.getInstance().getRecordedMedia().getMediaDataFromIndex(this.mediaIndex);
        }
        return null;
    }
    
    public boolean isNoTransmit() {
        return this.noTransmit;
    }
    
    public void setNoTransmit(final boolean noTransmit) {
        this.noTransmit = noTransmit;
    }
}

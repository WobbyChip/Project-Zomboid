// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.objects;

import java.util.HashMap;
import java.util.ArrayList;
import zombie.GameWindow;
import zombie.radio.devices.PresetEntry;
import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.iso.IsoWorld;
import zombie.network.GameServer;
import zombie.iso.IsoUtils;
import zombie.characters.IsoPlayer;
import zombie.iso.IsoCamera;
import zombie.Lua.LuaEventManager;
import zombie.ui.UIFont;
import zombie.radio.ZomboidRadio;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.Radio;
import zombie.inventory.InventoryItemFactory;
import zombie.core.properties.PropertyContainer;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.network.GameClient;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoCell;
import java.util.Map;
import zombie.chat.ChatElement;
import zombie.GameTime;
import zombie.radio.devices.DeviceData;
import zombie.iso.IsoLightSource;
import zombie.characters.Talker;
import zombie.chat.ChatElementOwner;
import zombie.radio.devices.WaveSignalDevice;
import zombie.iso.IsoObject;

public class IsoWaveSignal extends IsoObject implements WaveSignalDevice, ChatElementOwner, Talker
{
    protected IsoLightSource lightSource;
    protected boolean lightWasRemoved;
    protected int lightSourceRadius;
    protected float nextLightUpdate;
    protected float lightUpdateCnt;
    protected DeviceData deviceData;
    protected boolean displayRange;
    protected boolean hasPlayerInRange;
    protected GameTime gameTime;
    protected ChatElement chatElement;
    protected String talkerType;
    protected static Map<String, DeviceData> deviceDataCache;
    
    public IsoWaveSignal(final IsoCell isoCell) {
        super(isoCell);
        this.lightSource = null;
        this.lightWasRemoved = false;
        this.lightSourceRadius = 4;
        this.nextLightUpdate = 0.0f;
        this.lightUpdateCnt = 0.0f;
        this.deviceData = null;
        this.displayRange = false;
        this.hasPlayerInRange = false;
        this.talkerType = "device";
        this.init(true);
    }
    
    public IsoWaveSignal(final IsoCell isoCell, final IsoGridSquare isoGridSquare, final IsoSprite isoSprite) {
        super(isoCell, isoGridSquare, isoSprite);
        this.lightSource = null;
        this.lightWasRemoved = false;
        this.lightSourceRadius = 4;
        this.nextLightUpdate = 0.0f;
        this.lightUpdateCnt = 0.0f;
        this.deviceData = null;
        this.displayRange = false;
        this.hasPlayerInRange = false;
        this.talkerType = "device";
        this.init(false);
    }
    
    protected void init(final boolean b) {
        this.chatElement = new ChatElement(this, 5, this.talkerType);
        this.gameTime = GameTime.getInstance();
        if (!b) {
            if (this.sprite != null && this.sprite.getProperties() != null) {
                final PropertyContainer properties = this.sprite.getProperties();
                if (properties.Is("CustomItem") && properties.Val("CustomItem") != null) {
                    this.deviceData = this.cloneDeviceDataFromItem(properties.Val("CustomItem"));
                }
            }
            if (!GameClient.bClient && this.deviceData != null) {
                this.deviceData.generatePresets();
                this.deviceData.setDeviceVolume(Rand.Next(0.1f, 1.0f));
                this.deviceData.setRandomChannel();
                if (Rand.Next(100) <= 35 && !"Tutorial".equals(Core.GameMode)) {
                    this.deviceData.setTurnedOnRaw(true);
                }
            }
        }
        if (this.deviceData == null) {
            this.deviceData = new DeviceData(this);
        }
        this.deviceData.setParent(this);
    }
    
    public DeviceData cloneDeviceDataFromItem(final String s) {
        if (s != null) {
            if (IsoWaveSignal.deviceDataCache.containsKey(s) && IsoWaveSignal.deviceDataCache.get(s) != null) {
                return IsoWaveSignal.deviceDataCache.get(s).getClone();
            }
            final InventoryItem createItem = InventoryItemFactory.CreateItem(s);
            if (createItem != null && createItem instanceof Radio) {
                final DeviceData deviceData = ((Radio)createItem).getDeviceData();
                if (deviceData != null) {
                    IsoWaveSignal.deviceDataCache.put(s, deviceData);
                    return deviceData.getClone();
                }
            }
        }
        return null;
    }
    
    public boolean hasChatToDisplay() {
        return this.chatElement.getHasChatToDisplay();
    }
    
    @Override
    public boolean HasPlayerInRange() {
        return this.hasPlayerInRange;
    }
    
    @Override
    public float getDelta() {
        if (this.deviceData != null) {
            return this.deviceData.getPower();
        }
        return 0.0f;
    }
    
    @Override
    public void setDelta(final float power) {
        if (this.deviceData != null) {
            this.deviceData.setPower(power);
        }
    }
    
    @Override
    public DeviceData getDeviceData() {
        return this.deviceData;
    }
    
    @Override
    public void setDeviceData(DeviceData deviceData) {
        if (deviceData == null) {
            deviceData = new DeviceData(this);
        }
        (this.deviceData = deviceData).setParent(this);
    }
    
    @Override
    public boolean IsSpeaking() {
        return this.chatElement.IsSpeaking();
    }
    
    @Override
    public String getTalkerType() {
        return this.chatElement.getTalkerType();
    }
    
    public void setTalkerType(final String s) {
        this.talkerType = ((s == null) ? "" : s);
        this.chatElement.setTalkerType(this.talkerType);
    }
    
    @Override
    public String getSayLine() {
        return this.chatElement.getSayLine();
    }
    
    @Override
    public void Say(final String s) {
        this.AddDeviceText(s, 1.0f, 1.0f, 1.0f, null, -1, false);
    }
    
    @Override
    public void AddDeviceText(final String s, final float n, final float n2, final float n3, final String s2, final int n4) {
        this.AddDeviceText(s, n, n2, n3, s2, n4, true);
    }
    
    public void AddDeviceText(final String s, final int n, final int n2, final int n3, final String s2, final int n4) {
        this.AddDeviceText(s, n / 255.0f, n2 / 255.0f, (float)(n3 / 255), s2, n4, true);
    }
    
    public void AddDeviceText(final String s, final int n, final int n2, final int n3, final String s2, final int n4, final boolean b) {
        this.AddDeviceText(s, n / 255.0f, n2 / 255.0f, n3 / 255.0f, s2, n4, b);
    }
    
    public void AddDeviceText(final String s, final float n, final float n2, final float n3, final String s2, final int n4, final boolean b) {
        if (this.deviceData != null && this.deviceData.getIsTurnedOn()) {
            if (!ZomboidRadio.isStaticSound(s)) {
                this.deviceData.doReceiveSignal(n4);
            }
            if (this.deviceData.getDeviceVolume() > 0.0f) {
                this.chatElement.addChatLine(s, n, n2, n3, UIFont.Medium, (float)this.deviceData.getDeviceVolumeRange(), "default", true, true, true, true, true, true);
                if (s2 != null) {
                    LuaEventManager.triggerEvent("OnDeviceText", s2, this.getX(), this.getY(), this.getZ(), s, this);
                }
            }
        }
    }
    
    @Override
    public void renderlast() {
        if (this.chatElement.getHasChatToDisplay()) {
            if (this.getDeviceData() != null && !this.getDeviceData().getIsTurnedOn()) {
                this.chatElement.clear(IsoCamera.frameState.playerIndex);
                return;
            }
            this.chatElement.renderBatched(IsoPlayer.getPlayerIndex(), (int)((IsoUtils.XToScreen(this.getX(), this.getY(), this.getZ(), 0) - IsoCamera.getOffX() - this.offsetX + 32 * Core.TileScale) / Core.getInstance().getZoom(IsoPlayer.getPlayerIndex())), (int)((IsoUtils.YToScreen(this.getX(), this.getY(), this.getZ(), 0) - IsoCamera.getOffY() - this.offsetY + 50 * Core.TileScale) / Core.getInstance().getZoom(IsoPlayer.getPlayerIndex())));
        }
    }
    
    public void renderlastold2() {
        if (this.chatElement.getHasChatToDisplay()) {
            this.chatElement.renderBatched(IsoPlayer.getPlayerIndex(), (int)((IsoUtils.XToScreen(this.getX(), this.getY(), this.getZ(), 0) - IsoCamera.getOffX() - this.offsetX + 28.0f) / Core.getInstance().getZoom(IsoPlayer.getPlayerIndex()) + IsoCamera.getScreenLeft(IsoPlayer.getPlayerIndex())), (int)((IsoUtils.YToScreen(this.getX(), this.getY(), this.getZ(), 0) - IsoCamera.getOffY() - this.offsetY + 180.0f) / Core.getInstance().getZoom(IsoPlayer.getPlayerIndex()) + IsoCamera.getScreenTop(IsoPlayer.getPlayerIndex())));
        }
    }
    
    protected boolean playerWithinBounds(final IsoPlayer isoPlayer, final float n) {
        return isoPlayer != null && (isoPlayer.getX() > this.getX() - n || isoPlayer.getX() < this.getX() + n) && (isoPlayer.getY() > this.getY() - n || isoPlayer.getY() < this.getY() + n);
    }
    
    @Override
    public void update() {
        if (this.deviceData == null) {
            return;
        }
        if ((!GameServer.bServer && !GameClient.bClient) || GameServer.bServer) {
            this.deviceData.update(true, this.hasPlayerInRange);
        }
        else {
            this.deviceData.updateSimple();
        }
        if (!GameServer.bServer) {
            this.hasPlayerInRange = false;
            if (this.deviceData.getIsTurnedOn()) {
                if (this.playerWithinBounds(IsoPlayer.getInstance(), this.deviceData.getDeviceVolumeRange() * 0.6f)) {
                    this.hasPlayerInRange = true;
                }
                this.updateLightSource();
            }
            else {
                this.removeLightSourceFromWorld();
            }
            this.chatElement.setHistoryRange(this.deviceData.getDeviceVolumeRange() * 0.6f);
            this.chatElement.update();
        }
        else {
            this.hasPlayerInRange = false;
        }
    }
    
    protected void updateLightSource() {
    }
    
    protected void removeLightSourceFromWorld() {
        if (this.lightSource != null) {
            IsoWorld.instance.CurrentCell.removeLamppost(this.lightSource);
            this.lightSource = null;
        }
    }
    
    @Override
    public void load(final ByteBuffer byteBuffer, final int n, final boolean b) throws IOException {
        super.load(byteBuffer, n, b);
        if (this.deviceData == null) {
            this.deviceData = new DeviceData(this);
        }
        if (byteBuffer.get() == 1) {
            this.deviceData.load(byteBuffer, n, true);
        }
        this.deviceData.setParent(this);
    }
    
    @Override
    public void save(final ByteBuffer byteBuffer, final boolean b) throws IOException {
        super.save(byteBuffer, b);
        if (this.deviceData != null) {
            byteBuffer.put((byte)1);
            this.deviceData.save(byteBuffer, true);
        }
        else {
            byteBuffer.put((byte)0);
        }
    }
    
    @Override
    public void addToWorld() {
        if (!GameServer.bServer) {
            ZomboidRadio.getInstance().RegisterDevice(this);
        }
        if (this.getCell() != null) {
            this.getCell().addToStaticUpdaterObjectList(this);
        }
        super.addToWorld();
    }
    
    @Override
    public void removeFromWorld() {
        if (!GameServer.bServer) {
            ZomboidRadio.getInstance().UnRegisterDevice(this);
        }
        this.removeLightSourceFromWorld();
        this.lightSource = null;
        if (this.deviceData != null) {
            this.deviceData.cleanSoundsAndEmitter();
        }
        super.removeFromWorld();
    }
    
    @Override
    public void removeFromSquare() {
        super.removeFromSquare();
        this.square = null;
    }
    
    @Override
    public void saveState(final ByteBuffer byteBuffer) throws IOException {
        if (this.deviceData == null) {
            return;
        }
        final ArrayList<PresetEntry> presets = this.deviceData.getDevicePresets().getPresets();
        byteBuffer.putInt(presets.size());
        for (int i = 0; i < presets.size(); ++i) {
            final PresetEntry presetEntry = presets.get(i);
            GameWindow.WriteString(byteBuffer, presetEntry.getName());
            byteBuffer.putInt(presetEntry.getFrequency());
        }
        byteBuffer.put((byte)(this.deviceData.getIsTurnedOn() ? 1 : 0));
        byteBuffer.putInt(this.deviceData.getChannel());
        byteBuffer.putFloat(this.deviceData.getDeviceVolume());
    }
    
    @Override
    public void loadState(final ByteBuffer byteBuffer) throws IOException {
        final ArrayList<PresetEntry> presets = this.deviceData.getDevicePresets().getPresets();
        final int int1 = byteBuffer.getInt();
        for (int i = 0; i < int1; ++i) {
            final String readString = GameWindow.ReadString(byteBuffer);
            final int int2 = byteBuffer.getInt();
            if (i < presets.size()) {
                final PresetEntry presetEntry = presets.get(i);
                presetEntry.setName(readString);
                presetEntry.setFrequency(int2);
            }
            else {
                this.deviceData.getDevicePresets().addPreset(readString, int2);
            }
        }
        while (presets.size() > int1) {
            this.deviceData.getDevicePresets().removePreset(int1);
        }
        this.deviceData.setTurnedOnRaw(byteBuffer.get() == 1);
        this.deviceData.setChannelRaw(byteBuffer.getInt());
        this.deviceData.setDeviceVolumeRaw(byteBuffer.getFloat());
    }
    
    static {
        IsoWaveSignal.deviceDataCache = new HashMap<String, DeviceData>();
    }
}

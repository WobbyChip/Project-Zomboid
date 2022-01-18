// 
// Decompiled by Procyon v0.5.36
// 

package zombie.inventory.types;

import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.iso.IsoGridSquare;
import zombie.scripting.objects.Item;
import zombie.core.properties.PropertyContainer;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.chat.ChatManager;
import zombie.chat.ChatMessage;
import zombie.characters.IsoPlayer;
import zombie.Lua.LuaEventManager;
import zombie.ui.UIFont;
import zombie.radio.ZomboidRadio;
import zombie.GameTime;
import zombie.radio.devices.DeviceData;
import zombie.radio.devices.WaveSignalDevice;
import zombie.interfaces.IUpdater;
import zombie.characters.Talker;

public final class Radio extends Moveable implements Talker, IUpdater, WaveSignalDevice
{
    protected DeviceData deviceData;
    protected GameTime gameTime;
    protected int lastMin;
    protected boolean doPowerTick;
    protected int listenCnt;
    
    public Radio(final String s, final String s2, final String s3, final String s4) {
        super(s, s2, s3, s4);
        this.deviceData = null;
        this.lastMin = 0;
        this.doPowerTick = false;
        this.listenCnt = 0;
        this.deviceData = new DeviceData(this);
        this.gameTime = GameTime.getInstance();
        this.canBeDroppedOnFloor = true;
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
    
    public void doReceiveSignal(final int n) {
        if (this.deviceData != null) {
            this.deviceData.doReceiveSignal(n);
        }
    }
    
    @Override
    public void AddDeviceText(final String s, final float n, final float n2, final float n3, final String s2, final int n4) {
        if (!ZomboidRadio.isStaticSound(s)) {
            this.doReceiveSignal(n4);
        }
        final IsoPlayer player = this.getPlayer();
        if (player != null && this.deviceData != null && this.deviceData.getDeviceVolume() > 0.0f && !player.Traits.Deaf.isSet()) {
            player.Say(s, n, n2, n3, UIFont.Medium, (float)this.deviceData.getDeviceVolumeRange(), "radio");
            if (s2 != null) {
                LuaEventManager.triggerEvent("OnDeviceText", s2, -1, -1, -1, s, this);
            }
        }
    }
    
    public void AddDeviceText(final ChatMessage chatMessage, final float n, final float n2, final float n3, final String s, final int n4) {
        if (!ZomboidRadio.isStaticSound(chatMessage.getText())) {
            this.doReceiveSignal(n4);
        }
        if (this.getPlayer() != null && this.deviceData != null && this.deviceData.getDeviceVolume() > 0.0f) {
            ChatManager.getInstance().showRadioMessage(chatMessage);
            if (s != null) {
                LuaEventManager.triggerEvent("OnDeviceText", s, -1, -1, -1, chatMessage, this);
            }
        }
    }
    
    @Override
    public boolean HasPlayerInRange() {
        return false;
    }
    
    @Override
    public boolean ReadFromWorldSprite(final String worldSprite) {
        if (worldSprite == null) {
            return false;
        }
        final IsoSprite sprite = IsoSpriteManager.instance.getSprite(worldSprite);
        if (sprite != null) {
            final PropertyContainer properties = sprite.getProperties();
            if (properties.Is("IsMoveAble")) {
                if (properties.Is("CustomItem")) {
                    this.customItem = properties.Val("CustomItem");
                }
                this.worldSprite = worldSprite;
                return true;
            }
        }
        System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (worldSprite == null) ? "null" : worldSprite));
        return false;
    }
    
    @Override
    public int getSaveType() {
        return Item.Type.Radio.ordinal();
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
    public IsoGridSquare getSquare() {
        if (this.container != null && this.container.parent != null && this.container.parent instanceof IsoPlayer) {
            return this.container.parent.getSquare();
        }
        return null;
    }
    
    @Override
    public float getX() {
        final IsoGridSquare square = this.getSquare();
        return (square == null) ? 0.0f : ((float)square.getX());
    }
    
    @Override
    public float getY() {
        final IsoGridSquare square = this.getSquare();
        return (square == null) ? 0.0f : ((float)square.getY());
    }
    
    @Override
    public float getZ() {
        final IsoGridSquare square = this.getSquare();
        return (square == null) ? 0.0f : ((float)square.getZ());
    }
    
    public IsoPlayer getPlayer() {
        if (this.container != null && this.container.parent != null && this.container.parent instanceof IsoPlayer) {
            return (IsoPlayer)this.container.parent;
        }
        return null;
    }
    
    @Override
    public void render() {
    }
    
    @Override
    public void renderlast() {
    }
    
    @Override
    public void update() {
        if (this.deviceData == null) {
            return;
        }
        if ((!GameServer.bServer && !GameClient.bClient) || GameClient.bClient) {
            if (IsoPlayer.getInstance().getEquipedRadio() == this) {
                this.deviceData.update(false, true);
            }
            else {
                this.deviceData.cleanSoundsAndEmitter();
            }
        }
    }
    
    @Override
    public boolean IsSpeaking() {
        return false;
    }
    
    @Override
    public void Say(final String s) {
    }
    
    @Override
    public String getSayLine() {
        return null;
    }
    
    @Override
    public String getTalkerType() {
        return "radio";
    }
    
    @Override
    public void save(final ByteBuffer byteBuffer, final boolean b) throws IOException {
        super.save(byteBuffer, b);
        if (this.deviceData != null) {
            byteBuffer.put((byte)1);
            this.deviceData.save(byteBuffer, b);
        }
        else {
            byteBuffer.put((byte)0);
        }
    }
    
    @Override
    public void load(final ByteBuffer byteBuffer, final int n) throws IOException {
        super.load(byteBuffer, n);
        if (this.deviceData == null) {
            this.deviceData = new DeviceData(this);
        }
        if (byteBuffer.get() == 1) {
            this.deviceData.load(byteBuffer, n, false);
        }
        this.deviceData.setParent(this);
    }
}

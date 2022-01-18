// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.objects;

import zombie.util.Type;
import java.util.Iterator;
import zombie.network.PacketTypes;
import zombie.core.network.ByteBufferWriter;
import zombie.inventory.InventoryItem;
import zombie.core.raknet.UdpConnection;
import zombie.SoundManager;
import java.io.IOException;
import zombie.network.GameClient;
import zombie.SystemDisabler;
import java.nio.ByteBuffer;
import zombie.iso.IsoWorld;
import zombie.core.Rand;
import zombie.network.GameServer;
import zombie.GameTime;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoCell;
import java.util.ArrayList;
import zombie.iso.objects.interfaces.Activatable;
import zombie.iso.IsoObject;

public class IsoStove extends IsoObject implements Activatable
{
    private static final ArrayList<IsoObject> s_tempObjects;
    boolean activated;
    long soundInstance;
    private float maxTemperature;
    private double stopTime;
    private double startTime;
    private float currentTemperature;
    private int secondsTimer;
    private boolean firstTurnOn;
    private boolean broken;
    private boolean hasMetal;
    
    public IsoStove(final IsoCell isoCell, final IsoGridSquare isoGridSquare, final IsoSprite isoSprite) {
        super(isoCell, isoGridSquare, isoSprite);
        this.activated = false;
        this.soundInstance = -1L;
        this.maxTemperature = 0.0f;
        this.currentTemperature = 0.0f;
        this.secondsTimer = -1;
        this.firstTurnOn = true;
        this.broken = false;
        this.hasMetal = false;
    }
    
    @Override
    public String getObjectName() {
        return "Stove";
    }
    
    public IsoStove(final IsoCell isoCell) {
        super(isoCell);
        this.activated = false;
        this.soundInstance = -1L;
        this.maxTemperature = 0.0f;
        this.currentTemperature = 0.0f;
        this.secondsTimer = -1;
        this.firstTurnOn = true;
        this.broken = false;
        this.hasMetal = false;
    }
    
    @Override
    public boolean Activated() {
        return this.activated;
    }
    
    @Override
    public void update() {
        if (this.Activated() && (this.container == null || !this.container.isPowered())) {
            this.setActivated(false);
            if (this.container != null) {
                this.container.addItemsToProcessItems();
            }
        }
        if (this.Activated() && this.isMicrowave() && this.stopTime > 0.0 && this.stopTime < GameTime.instance.getWorldAgeHours()) {
            this.setActivated(false);
        }
        if (GameServer.bServer && this.Activated() && this.hasMetal && Rand.Next(Rand.AdjustForFramerate(200)) == 100) {
            IsoFireManager.StartFire(this.container.SourceGrid.getCell(), this.container.SourceGrid, true, 10000);
            this.setBroken(true);
            this.activated = false;
            this.stopTime = 0.0;
            this.startTime = 0.0;
            this.secondsTimer = -1;
        }
        if (GameServer.bServer) {
            return;
        }
        if (this.Activated()) {
            if (this.stopTime > 0.0 && this.stopTime < GameTime.instance.getWorldAgeHours()) {
                if (!this.isMicrowave()) {
                    if ("stove".equals(this.container.getType())) {
                        this.getSpriteGridObjects(IsoStove.s_tempObjects);
                        if (IsoStove.s_tempObjects.isEmpty() || this == IsoStove.s_tempObjects.get(0)) {
                            IsoWorld.instance.getFreeEmitter(this.getX() + 0.5f, this.getY() + 0.5f, (float)(int)this.getZ()).playSoundImpl("StoveTimerExpired", this);
                        }
                    }
                }
                this.stopTime = 0.0;
                this.startTime = 0.0;
                this.secondsTimer = -1;
            }
            if (this.getMaxTemperature() > 0.0f && this.currentTemperature < this.getMaxTemperature()) {
                float n = (this.getMaxTemperature() - this.currentTemperature) / 700.0f;
                if (n < 0.05f) {
                    n = 0.05f;
                }
                this.currentTemperature += n * GameTime.instance.getMultiplier();
                if (this.currentTemperature > this.getMaxTemperature()) {
                    this.currentTemperature = this.getMaxTemperature();
                }
            }
            else if (this.currentTemperature > this.getMaxTemperature()) {
                this.currentTemperature -= (this.currentTemperature - this.getMaxTemperature()) / 1000.0f * GameTime.instance.getMultiplier();
                if (this.currentTemperature < 0.0f) {
                    this.currentTemperature = 0.0f;
                }
            }
        }
        else if (this.currentTemperature > 0.0f) {
            this.currentTemperature -= 0.1f * GameTime.instance.getMultiplier();
            this.currentTemperature = Math.max(this.currentTemperature, 0.0f);
        }
        if (this.container != null && this.isMicrowave()) {
            if (this.Activated()) {
                this.currentTemperature = this.getMaxTemperature();
            }
            else {
                this.currentTemperature = 0.0f;
            }
        }
    }
    
    @Override
    public void load(final ByteBuffer byteBuffer, final int n, final boolean b) throws IOException {
        super.load(byteBuffer, n, b);
        if (n >= 28) {
            this.activated = (byteBuffer.get() == 1);
        }
        if (n >= 106) {
            this.secondsTimer = byteBuffer.getInt();
            this.maxTemperature = byteBuffer.getFloat();
            this.firstTurnOn = (byteBuffer.get() == 1);
            this.broken = (byteBuffer.get() == 1);
        }
        if (SystemDisabler.doObjectStateSyncEnable && GameClient.bClient) {
            GameClient.instance.objectSyncReq.putRequestLoad(this.square);
        }
    }
    
    @Override
    public void save(final ByteBuffer byteBuffer, final boolean b) throws IOException {
        super.save(byteBuffer, b);
        byteBuffer.put((byte)(this.activated ? 1 : 0));
        byteBuffer.putInt(this.secondsTimer);
        byteBuffer.putFloat(this.maxTemperature);
        byteBuffer.put((byte)(this.firstTurnOn ? 1 : 0));
        byteBuffer.put((byte)(this.broken ? 1 : 0));
    }
    
    @Override
    public void addToWorld() {
        if (this.container == null) {
            return;
        }
        this.getCell().addToProcessIsoObject(this);
        this.container.addItemsToProcessItems();
        this.setActivated(this.activated);
    }
    
    @Override
    public void Toggle() {
        SoundManager.instance.PlayWorldSound(this.isMicrowave() ? "ToggleMicrowave" : "ToggleStove", this.getSquare(), 1.0f, 1.0f, 1.0f, false);
        this.setActivated(!this.activated);
        this.container.addItemsToProcessItems();
        IsoGenerator.updateGenerator(this.square);
        this.syncIsoObject(false, (byte)(this.activated ? 1 : 0), null, null);
        this.syncSpriteGridObjects(true, true);
    }
    
    public void sync() {
        this.syncIsoObject(false, (byte)(this.activated ? 1 : 0), null, null);
    }
    
    private void doSound() {
        if (GameServer.bServer) {
            this.hasMetal();
            return;
        }
        if (this.isMicrowave()) {
            if (this.activated) {
                if (this.soundInstance != -1L && this.emitter != null) {
                    this.emitter.stopSound(this.soundInstance);
                }
                this.emitter = IsoWorld.instance.getFreeEmitter(this.getX() + 0.5f, this.getY() + 0.5f, (float)(int)this.getZ());
                IsoWorld.instance.setEmitterOwner(this.emitter, this);
                if (this.hasMetal()) {
                    this.soundInstance = this.emitter.playSoundLoopedImpl("MicrowaveCookingMetal");
                }
                else {
                    this.soundInstance = this.emitter.playSoundLoopedImpl("MicrowaveRunning");
                }
            }
            else if (this.soundInstance != -1L) {
                if (this.emitter != null) {
                    this.emitter.stopSound(this.soundInstance);
                    this.emitter = null;
                }
                this.soundInstance = -1L;
                if (this.container != null && this.container.isPowered()) {
                    IsoWorld.instance.getFreeEmitter(this.getX() + 0.5f, this.getY() + 0.5f, (float)(int)this.getZ()).playSoundImpl("MicrowaveTimerExpired", this);
                }
            }
        }
        else if (this.getContainer() != null && "stove".equals(this.container.getType())) {
            if (this.Activated()) {
                if (this.soundInstance != -1L && this.emitter != null) {
                    this.emitter.stopSound(this.soundInstance);
                }
                this.emitter = IsoWorld.instance.getFreeEmitter(this.getX() + 0.5f, this.getY() + 0.5f, (float)(int)this.getZ());
                IsoWorld.instance.setEmitterOwner(this.emitter, this);
                this.soundInstance = this.emitter.playSoundLoopedImpl("StoveRunning");
            }
            else if (this.soundInstance != -1L) {
                if (this.emitter != null) {
                    this.emitter.stopSound(this.soundInstance);
                    this.emitter = null;
                }
                this.soundInstance = -1L;
            }
        }
    }
    
    private boolean hasMetal() {
        for (int size = this.getContainer().getItems().size(), i = 0; i < size; ++i) {
            if (this.getContainer().getItems().get(i).getMetalValue() > 0.0f) {
                return this.hasMetal = true;
            }
        }
        return this.hasMetal = false;
    }
    
    @Override
    public String getActivatableType() {
        return "stove";
    }
    
    @Override
    public void syncIsoObjectSend(final ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putInt(this.square.getX());
        byteBufferWriter.putInt(this.square.getY());
        byteBufferWriter.putInt(this.square.getZ());
        byteBufferWriter.putByte((byte)this.square.getObjects().indexOf(this));
        byteBufferWriter.putByte((byte)1);
        byteBufferWriter.putByte((byte)(this.activated ? 1 : 0));
        byteBufferWriter.putInt(this.secondsTimer);
        byteBufferWriter.putFloat(this.maxTemperature);
    }
    
    @Override
    public void syncIsoObject(final boolean b, final byte b2, final UdpConnection udpConnection, final ByteBuffer byteBuffer) {
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
            final boolean activated = b2 == 1;
            this.secondsTimer = byteBuffer.getInt();
            this.maxTemperature = byteBuffer.getFloat();
            this.setActivated(activated);
            this.container.addItemsToProcessItems();
            if (GameServer.bServer) {
                for (final UdpConnection udpConnection2 : GameServer.udpEngine.connections) {
                    if (udpConnection == null || udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
                        final ByteBufferWriter startPacket2 = udpConnection2.startPacket();
                        PacketTypes.PacketType.SyncIsoObject.doPacket(startPacket2);
                        this.syncIsoObjectSend(startPacket2);
                        PacketTypes.PacketType.SyncIsoObject.send(udpConnection2);
                    }
                }
            }
        }
    }
    
    public void setActivated(final boolean activated) {
        if (this.isBroken()) {
            return;
        }
        this.activated = activated;
        if (this.firstTurnOn && this.getMaxTemperature() == 0.0f) {
            if (this.isMicrowave() && this.secondsTimer < 0) {
                this.maxTemperature = 100.0f;
            }
            if ("stove".equals(this.getContainer().getType()) && this.secondsTimer < 0) {
                this.maxTemperature = 200.0f;
            }
        }
        if (this.firstTurnOn) {
            this.firstTurnOn = false;
        }
        if (this.activated) {
            if (this.isMicrowave() && this.secondsTimer < 0) {
                this.secondsTimer = 3600;
            }
            if (this.secondsTimer > 0) {
                this.startTime = GameTime.instance.getWorldAgeHours();
                this.stopTime = this.startTime + this.secondsTimer / 3600.0;
            }
        }
        else {
            this.stopTime = 0.0;
            this.startTime = 0.0;
            this.hasMetal = false;
        }
        this.doSound();
        this.doOverlay();
    }
    
    private void doOverlay() {
        if (this.Activated() && this.getOverlaySprite() == null) {
            final String[] split = this.getSprite().getName().split("_");
            this.setOverlaySprite(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, split[0], split[1], split[2], split[3]));
        }
        else if (!this.Activated()) {
            this.setOverlaySprite(null);
        }
    }
    
    public void setTimer(final int secondsTimer) {
        this.secondsTimer = secondsTimer;
        if (this.activated && this.secondsTimer > 0) {
            this.startTime = GameTime.instance.getWorldAgeHours();
            this.stopTime = this.startTime + this.secondsTimer / 3600.0;
        }
    }
    
    public int getTimer() {
        return this.secondsTimer;
    }
    
    public float getMaxTemperature() {
        return this.maxTemperature;
    }
    
    public void setMaxTemperature(final float maxTemperature) {
        this.maxTemperature = maxTemperature;
    }
    
    public boolean isMicrowave() {
        return this.getContainer() != null && this.getContainer().isMicrowave();
    }
    
    public int isRunningFor() {
        if (this.startTime == 0.0) {
            return 0;
        }
        return (int)((GameTime.instance.getWorldAgeHours() - this.startTime) * 3600.0);
    }
    
    public float getCurrentTemperature() {
        return this.currentTemperature + 100.0f;
    }
    
    public boolean isTemperatureChanging() {
        return this.currentTemperature != (this.activated ? this.maxTemperature : 0.0f);
    }
    
    public boolean isBroken() {
        return this.broken;
    }
    
    public void setBroken(final boolean broken) {
        this.broken = broken;
    }
    
    public void syncSpriteGridObjects(final boolean b, final boolean b2) {
        this.getSpriteGridObjects(IsoStove.s_tempObjects);
        for (int i = IsoStove.s_tempObjects.size() - 1; i >= 0; --i) {
            final IsoStove isoStove = Type.tryCastTo(IsoStove.s_tempObjects.get(i), IsoStove.class);
            if (isoStove != null) {
                if (isoStove != this) {
                    isoStove.activated = this.activated;
                    isoStove.maxTemperature = this.maxTemperature;
                    isoStove.firstTurnOn = this.firstTurnOn;
                    isoStove.secondsTimer = this.secondsTimer;
                    isoStove.startTime = this.startTime;
                    isoStove.stopTime = this.stopTime;
                    isoStove.hasMetal = this.hasMetal;
                    isoStove.doOverlay();
                    isoStove.doSound();
                    if (b) {
                        if (isoStove.container != null) {
                            isoStove.container.addItemsToProcessItems();
                        }
                        IsoGenerator.updateGenerator(isoStove.square);
                    }
                    if (b2) {
                        isoStove.sync();
                    }
                }
            }
        }
    }
    
    static {
        s_tempObjects = new ArrayList<IsoObject>();
    }
}

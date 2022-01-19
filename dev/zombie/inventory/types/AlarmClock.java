// 
// Decompiled by Procyon v0.5.36
// 

package zombie.inventory.types;

import zombie.core.network.ByteBufferWriter;
import zombie.network.PacketTypes;
import zombie.iso.IsoObject;
import zombie.iso.IsoCell;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.iso.IsoWorld;
import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.core.Translator;
import zombie.ui.ObjectTooltip;
import zombie.characters.IsoGameCharacter;
import zombie.ai.sadisticAIDirector.SleepingEvent;
import zombie.SoundManager;
import zombie.iso.IsoUtils;
import zombie.characters.IsoPlayer;
import zombie.audio.BaseSoundEmitter;
import zombie.inventory.ItemSoundManager;
import zombie.network.GameServer;
import zombie.WorldSoundManager;
import zombie.network.GameClient;
import zombie.GameTime;
import zombie.inventory.ItemContainer;
import zombie.iso.IsoGridSquare;
import zombie.core.Rand;
import zombie.scripting.objects.Item;
import zombie.inventory.ItemType;
import zombie.core.utils.OnceEvery;
import zombie.inventory.InventoryItem;

public final class AlarmClock extends InventoryItem
{
    private int alarmHour;
    private int alarmMinutes;
    private boolean alarmSet;
    private long ringSound;
    private double ringSince;
    private int forceDontRing;
    private String alarmSound;
    private int soundRadius;
    private boolean isDigital;
    public static short PacketPlayer;
    public static short PacketWorld;
    private static final OnceEvery sendEvery;
    
    public AlarmClock(final String s, final String s2, final String s3, final String s4) {
        super(s, s2, s3, s4);
        this.alarmHour = -1;
        this.alarmMinutes = -1;
        this.alarmSet = false;
        this.ringSince = -1.0;
        this.forceDontRing = -1;
        this.alarmSound = "AlarmClockLoop";
        this.soundRadius = 40;
        this.isDigital = true;
        if (s2.contains("Classic")) {
            this.isDigital = false;
        }
        this.cat = ItemType.AlarmClock;
        this.randomizeAlarm();
    }
    
    public AlarmClock(final String s, final String s2, final String s3, final Item item) {
        super(s, s2, s3, item);
        this.alarmHour = -1;
        this.alarmMinutes = -1;
        this.alarmSet = false;
        this.ringSince = -1.0;
        this.forceDontRing = -1;
        this.alarmSound = "AlarmClockLoop";
        this.soundRadius = 40;
        this.isDigital = true;
        if (s2.contains("Classic")) {
            this.isDigital = false;
        }
        this.cat = ItemType.AlarmClock;
        this.randomizeAlarm();
    }
    
    private void randomizeAlarm() {
        this.alarmHour = Rand.Next(0, 23);
        this.alarmMinutes = (int)Math.floor(Rand.Next(0, 59) / 10) * 10;
        this.alarmSet = (Rand.Next(6) == 1);
    }
    
    public IsoGridSquare getAlarmSquare() {
        IsoGridSquare isoGridSquare = null;
        final ItemContainer outermostContainer = this.getOutermostContainer();
        if (outermostContainer != null) {
            isoGridSquare = outermostContainer.getSourceGrid();
            if (isoGridSquare == null && outermostContainer.parent != null) {
                isoGridSquare = outermostContainer.parent.square;
            }
            final InventoryItem containingItem = outermostContainer.containingItem;
            if (isoGridSquare == null && containingItem != null && containingItem.getWorldItem() != null) {
                isoGridSquare = containingItem.getWorldItem().getSquare();
            }
        }
        if (isoGridSquare == null && this.getWorldItem() != null && this.getWorldItem().getWorldObjectIndex() != -1) {
            isoGridSquare = this.getWorldItem().square;
        }
        return isoGridSquare;
    }
    
    @Override
    public boolean shouldUpdateInWorld() {
        return this.alarmSet;
    }
    
    @Override
    public void update() {
        if (this.alarmSet) {
            final int n = GameTime.instance.getMinutes() / 10 * 10;
            if (!this.isRinging() && this.forceDontRing != n && this.alarmHour == GameTime.instance.getHour() && this.alarmMinutes == n) {
                this.ringSince = GameTime.getInstance().getWorldAgeHours();
            }
            if (this.isRinging()) {
                final double worldAgeHours = GameTime.getInstance().getWorldAgeHours();
                if (this.ringSince > worldAgeHours) {
                    this.ringSince = worldAgeHours;
                }
                final IsoGridSquare alarmSquare = this.getAlarmSquare();
                if (alarmSquare == null || this.ringSince + 0.5 < worldAgeHours) {
                    this.stopRinging();
                }
                else if (!GameClient.bClient && alarmSquare != null) {
                    WorldSoundManager.instance.addSoundRepeating(null, alarmSquare.getX(), alarmSquare.getY(), alarmSquare.getZ(), this.getSoundRadius(), 3, false);
                }
                if (!GameServer.bServer && this.isRinging()) {
                    ItemSoundManager.addItem(this);
                }
            }
            if (this.forceDontRing != n) {
                this.forceDontRing = -1;
            }
        }
    }
    
    @Override
    public void updateSound(final BaseSoundEmitter baseSoundEmitter) {
        assert !GameServer.bServer;
        final IsoGridSquare alarmSquare = this.getAlarmSquare();
        if (alarmSquare == null) {
            return;
        }
        baseSoundEmitter.setPos(alarmSquare.x + 0.5f, alarmSquare.y + 0.5f, (float)alarmSquare.z);
        if (!baseSoundEmitter.isPlaying(this.ringSound)) {
            if (this.alarmSound == null || "".equals(this.alarmSound)) {
                this.alarmSound = "AlarmClockLoop";
            }
            this.ringSound = baseSoundEmitter.playSoundImpl(this.alarmSound, alarmSquare);
        }
        if (GameClient.bClient && AlarmClock.sendEvery.Check() && this.isInLocalPlayerInventory()) {
            GameClient.instance.sendWorldSound(null, alarmSquare.x, alarmSquare.y, alarmSquare.z, this.getSoundRadius(), 3, false, 0.0f, 1.0f);
        }
        this.wakeUpPlayers(alarmSquare);
    }
    
    private void wakeUpPlayers(final IsoGridSquare isoGridSquare) {
        if (GameServer.bServer) {
            return;
        }
        final int soundRadius = this.getSoundRadius();
        final int max = Math.max(isoGridSquare.getZ() - 3, 0);
        final int min = Math.min(isoGridSquare.getZ() + 3, 8);
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            final IsoPlayer isoPlayer = IsoPlayer.players[i];
            if (isoPlayer != null && !isoPlayer.isDead()) {
                if (isoPlayer.getCurrentSquare() != null) {
                    if (!isoPlayer.Traits.Deaf.isSet()) {
                        final IsoGridSquare currentSquare = isoPlayer.getCurrentSquare();
                        if (currentSquare.z >= max) {
                            if (currentSquare.z < min) {
                                float distanceToSquared = IsoUtils.DistanceToSquared((float)isoGridSquare.x, (float)isoGridSquare.y, (float)currentSquare.x, (float)currentSquare.y);
                                if (isoPlayer.Traits.HardOfHearing.isSet()) {
                                    distanceToSquared *= 4.5f;
                                }
                                if (distanceToSquared <= soundRadius * soundRadius) {
                                    this.wakeUp(isoPlayer);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void wakeUp(final IsoPlayer isoPlayer) {
        if (isoPlayer.Asleep) {
            SoundManager.instance.setMusicWakeState(isoPlayer, "WakeNormal");
            SleepingEvent.instance.wakeUp(isoPlayer);
        }
    }
    
    public boolean isRinging() {
        return this.ringSince >= 0.0;
    }
    
    @Override
    public boolean finishupdate() {
        return !this.alarmSet;
    }
    
    public boolean isDigital() {
        return this.isDigital;
    }
    
    @Override
    public void DoTooltip(final ObjectTooltip objectTooltip, final ObjectTooltip.Layout layout) {
        final ObjectTooltip.LayoutItem addItem = layout.addItem();
        addItem.setLabel(Translator.getText("IGUI_CurrentTime"), 1.0f, 1.0f, 0.8f, 1.0f);
        final int i = GameTime.instance.getMinutes() / 10 * 10;
        addItem.setValue(invokedynamic(makeConcatWithConstants:(ILjava/io/Serializable;)Ljava/lang/String;, GameTime.getInstance().getHour(), (i == 0) ? "00" : Integer.valueOf(i)), 1.0f, 1.0f, 0.8f, 1.0f);
        if (this.alarmSet) {
            final ObjectTooltip.LayoutItem addItem2 = layout.addItem();
            addItem2.setLabel(Translator.getText("IGUI_AlarmIsSetFor"), 1.0f, 1.0f, 0.8f, 1.0f);
            addItem2.setValue(invokedynamic(makeConcatWithConstants:(ILjava/io/Serializable;)Ljava/lang/String;, this.alarmHour, (this.alarmMinutes == 0) ? "00" : Integer.valueOf(this.alarmMinutes)), 1.0f, 1.0f, 0.8f, 1.0f);
        }
    }
    
    @Override
    public void save(final ByteBuffer byteBuffer, final boolean b) throws IOException {
        super.save(byteBuffer, b);
        byteBuffer.putInt(this.alarmHour);
        byteBuffer.putInt(this.alarmMinutes);
        byteBuffer.put((byte)(this.alarmSet ? 1 : 0));
        byteBuffer.putFloat((float)this.ringSince);
    }
    
    @Override
    public void load(final ByteBuffer byteBuffer, final int n) throws IOException {
        super.load(byteBuffer, n);
        this.alarmHour = byteBuffer.getInt();
        this.alarmMinutes = byteBuffer.getInt();
        this.alarmSet = (byteBuffer.get() == 1);
        this.ringSince = byteBuffer.getFloat();
        this.ringSound = -1L;
    }
    
    @Override
    public int getSaveType() {
        return Item.Type.AlarmClock.ordinal();
    }
    
    @Override
    public String getCategory() {
        if (this.mainCategory != null) {
            return this.mainCategory;
        }
        return "AlarmClock";
    }
    
    public void setAlarmSet(final boolean alarmSet) {
        this.stopRinging();
        this.alarmSet = alarmSet;
        this.ringSound = -1L;
        if (alarmSet) {
            IsoWorld.instance.CurrentCell.addToProcessItems(this);
            final IsoWorldInventoryObject worldItem = this.getWorldItem();
            if (worldItem != null && worldItem.getSquare() != null) {
                final IsoCell cell = IsoWorld.instance.getCell();
                if (!cell.getProcessWorldItems().contains(worldItem)) {
                    cell.getProcessWorldItems().add(worldItem);
                }
            }
        }
        else {
            IsoWorld.instance.CurrentCell.addToProcessItemsRemove(this);
        }
    }
    
    public boolean isAlarmSet() {
        return this.alarmSet;
    }
    
    public void setHour(final int alarmHour) {
        this.alarmHour = alarmHour;
        this.forceDontRing = -1;
    }
    
    public void setMinute(final int alarmMinutes) {
        this.alarmMinutes = alarmMinutes;
        this.forceDontRing = -1;
    }
    
    public int getHour() {
        return this.alarmHour;
    }
    
    public int getMinute() {
        return this.alarmMinutes;
    }
    
    public void syncAlarmClock() {
        final IsoPlayer ownerPlayer = this.getOwnerPlayer(this.container);
        if (ownerPlayer != null) {
            this.syncAlarmClock_Player(ownerPlayer);
        }
        if (this.worldItem != null) {
            this.syncAlarmClock_World();
        }
    }
    
    private IsoPlayer getOwnerPlayer(final ItemContainer itemContainer) {
        if (itemContainer == null) {
            return null;
        }
        final IsoObject parent = itemContainer.getParent();
        if (parent instanceof IsoPlayer) {
            return (IsoPlayer)parent;
        }
        return null;
    }
    
    public void syncAlarmClock_Player(final IsoPlayer isoPlayer) {
        if (GameClient.bClient) {
            final ByteBufferWriter startPacket = GameClient.connection.startPacket();
            PacketTypes.PacketType.SyncAlarmClock.doPacket(startPacket);
            startPacket.putShort(AlarmClock.PacketPlayer);
            startPacket.putShort(isoPlayer.OnlineID);
            startPacket.putInt(this.id);
            startPacket.putByte((byte)0);
            startPacket.putInt(this.alarmHour);
            startPacket.putInt(this.alarmMinutes);
            startPacket.putByte((byte)(this.alarmSet ? 1 : 0));
            PacketTypes.PacketType.SyncAlarmClock.send(GameClient.connection);
        }
    }
    
    public void syncAlarmClock_World() {
        if (GameClient.bClient) {
            final ByteBufferWriter startPacket = GameClient.connection.startPacket();
            PacketTypes.PacketType.SyncAlarmClock.doPacket(startPacket);
            startPacket.putShort(AlarmClock.PacketWorld);
            startPacket.putInt(this.worldItem.square.getX());
            startPacket.putInt(this.worldItem.square.getY());
            startPacket.putInt(this.worldItem.square.getZ());
            startPacket.putInt(this.id);
            startPacket.putByte((byte)0);
            startPacket.putInt(this.alarmHour);
            startPacket.putInt(this.alarmMinutes);
            startPacket.putByte((byte)(this.alarmSet ? 1 : 0));
            PacketTypes.PacketType.SyncAlarmClock.send(GameClient.connection);
        }
    }
    
    public void syncStopRinging() {
        if (!GameClient.bClient) {
            return;
        }
        final ByteBufferWriter startPacket = GameClient.connection.startPacket();
        PacketTypes.PacketType.SyncAlarmClock.doPacket(startPacket);
        final IsoPlayer ownerPlayer = this.getOwnerPlayer(this.container);
        if (ownerPlayer != null) {
            startPacket.putShort(AlarmClock.PacketPlayer);
            startPacket.putShort(ownerPlayer.OnlineID);
        }
        else if (this.getWorldItem() != null) {
            startPacket.putShort(AlarmClock.PacketWorld);
            startPacket.putInt(this.worldItem.square.getX());
            startPacket.putInt(this.worldItem.square.getY());
            startPacket.putInt(this.worldItem.square.getZ());
        }
        else {
            assert false;
        }
        startPacket.putInt(this.id);
        startPacket.putByte((byte)1);
        PacketTypes.PacketType.SyncAlarmClock.send(GameClient.connection);
    }
    
    public void stopRinging() {
        if (this.ringSound != -1L) {
            this.ringSound = -1L;
            final IsoGridSquare alarmSquare = this.getAlarmSquare();
            if (alarmSquare != null) {
                IsoWorld.instance.getFreeEmitter(alarmSquare.x + 0.5f, alarmSquare.y + 0.5f, (float)alarmSquare.z).playSoundImpl("AlarmClockRingingStop", alarmSquare);
            }
        }
        ItemSoundManager.removeItem(this);
        this.ringSince = -1.0;
        this.forceDontRing = GameTime.instance.getMinutes() / 10 * 10;
    }
    
    public String getAlarmSound() {
        return this.alarmSound;
    }
    
    public void setAlarmSound(final String alarmSound) {
        this.alarmSound = alarmSound;
    }
    
    public int getSoundRadius() {
        return this.soundRadius;
    }
    
    public void setSoundRadius(final int soundRadius) {
        this.soundRadius = soundRadius;
    }
    
    static {
        AlarmClock.PacketPlayer = 1;
        AlarmClock.PacketWorld = 2;
        sendEvery = new OnceEvery(2.0f);
    }
}

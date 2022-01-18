// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network;

import zombie.GameWindow;
import zombie.characters.Moodles.MoodleType;
import zombie.characters.BodyDamage.BodyPart;
import zombie.core.raknet.UdpConnection;
import java.nio.ByteBuffer;
import zombie.core.network.ByteBufferWriter;
import zombie.characters.IsoGameCharacter;
import zombie.characters.BodyDamage.BodyDamage;
import zombie.characters.IsoPlayer;
import zombie.debug.DebugLog;
import zombie.core.Core;
import java.util.ArrayList;

public class BodyDamageSync
{
    public static final byte BD_Health = 1;
    public static final byte BD_bandaged = 2;
    public static final byte BD_bitten = 3;
    public static final byte BD_bleeding = 4;
    public static final byte BD_IsBleedingStemmed = 5;
    public static final byte BD_IsCortorised = 6;
    public static final byte BD_scratched = 7;
    public static final byte BD_stitched = 8;
    public static final byte BD_deepWounded = 9;
    public static final byte BD_IsInfected = 10;
    public static final byte BD_IsFakeInfected = 11;
    public static final byte BD_bandageLife = 12;
    public static final byte BD_scratchTime = 13;
    public static final byte BD_biteTime = 14;
    public static final byte BD_alcoholicBandage = 15;
    public static final byte BD_woundInfectionLevel = 16;
    public static final byte BD_infectedWound = 17;
    public static final byte BD_bleedingTime = 18;
    public static final byte BD_deepWoundTime = 19;
    public static final byte BD_haveGlass = 20;
    public static final byte BD_stitchTime = 21;
    public static final byte BD_alcoholLevel = 22;
    public static final byte BD_additionalPain = 23;
    public static final byte BD_bandageType = 24;
    public static final byte BD_getBandageXp = 25;
    public static final byte BD_getStitchXp = 26;
    public static final byte BD_getSplintXp = 27;
    public static final byte BD_fractureTime = 28;
    public static final byte BD_splint = 29;
    public static final byte BD_splintFactor = 30;
    public static final byte BD_haveBullet = 31;
    public static final byte BD_burnTime = 32;
    public static final byte BD_needBurnWash = 33;
    public static final byte BD_lastTimeBurnWash = 34;
    public static final byte BD_splintItem = 35;
    public static final byte BD_plantainFactor = 36;
    public static final byte BD_comfreyFactor = 37;
    public static final byte BD_garlicFactor = 38;
    public static final byte BD_cut = 39;
    public static final byte BD_cutTime = 40;
    public static final byte BD_BodyDamage = 50;
    private static final byte BD_START = 64;
    private static final byte BD_END = 65;
    private static final byte PKT_START_UPDATING = 1;
    private static final byte PKT_STOP_UPDATING = 2;
    private static final byte PKT_UPDATE = 3;
    public static BodyDamageSync instance;
    private ArrayList<Updater> updaters;
    
    public BodyDamageSync() {
        this.updaters = new ArrayList<Updater>();
    }
    
    private static void noise(final String s) {
        if (Core.bDebug || (GameServer.bServer && GameServer.bDebug)) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        }
    }
    
    public void startSendingUpdates(final short localIndex, final short remoteID) {
        if (!GameClient.bClient) {
            return;
        }
        noise(invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, remoteID));
        for (int i = 0; i < this.updaters.size(); ++i) {
            final Updater updater = this.updaters.get(i);
            if (updater.localIndex == localIndex && updater.remoteID == remoteID) {
                return;
            }
        }
        final IsoPlayer isoPlayer = IsoPlayer.players[localIndex];
        final Updater e = new Updater();
        e.localIndex = localIndex;
        e.remoteID = remoteID;
        e.bdLocal = isoPlayer.getBodyDamage();
        e.bdSent = new BodyDamage(null);
        this.updaters.add(e);
    }
    
    public void stopSendingUpdates(final short n, final short n2) {
        if (!GameClient.bClient) {
            return;
        }
        noise(invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, n2));
        for (int i = 0; i < this.updaters.size(); ++i) {
            final Updater updater = this.updaters.get(i);
            if (updater.localIndex == n && updater.remoteID == n2) {
                this.updaters.remove(i);
                return;
            }
        }
    }
    
    public void startReceivingUpdates(final short n) {
        if (!GameClient.bClient) {
            return;
        }
        noise(invokedynamic(makeConcatWithConstants:(SS)Ljava/lang/String;, n, IsoPlayer.players[0].getOnlineID()));
        final ByteBufferWriter startPacket = GameClient.connection.startPacket();
        PacketTypes.PacketType.BodyDamageUpdate.doPacket(startPacket);
        startPacket.putByte((byte)1);
        startPacket.putShort(IsoPlayer.players[0].getOnlineID());
        startPacket.putShort(n);
        PacketTypes.PacketType.BodyDamageUpdate.send(GameClient.connection);
    }
    
    public void stopReceivingUpdates(final short n) {
        if (!GameClient.bClient) {
            return;
        }
        noise(invokedynamic(makeConcatWithConstants:(SS)Ljava/lang/String;, n, IsoPlayer.players[0].getOnlineID()));
        final ByteBufferWriter startPacket = GameClient.connection.startPacket();
        PacketTypes.PacketType.BodyDamageUpdate.doPacket(startPacket);
        startPacket.putByte((byte)2);
        startPacket.putShort(IsoPlayer.players[0].getOnlineID());
        startPacket.putShort(n);
        PacketTypes.PacketType.BodyDamageUpdate.send(GameClient.connection);
    }
    
    public void update() {
        if (!GameClient.bClient) {
            return;
        }
        for (int i = 0; i < this.updaters.size(); ++i) {
            this.updaters.get(i).update();
        }
    }
    
    public void serverPacket(final ByteBuffer src) {
        final byte value = src.get();
        if (value == 1) {
            final short short1 = src.getShort();
            final short short2 = src.getShort();
            final Long n = GameServer.IDToAddressMap.get(short2);
            if (n == null) {
                return;
            }
            final UdpConnection activeConnection = GameServer.udpEngine.getActiveConnection(n);
            if (activeConnection == null) {
                return;
            }
            final ByteBufferWriter startPacket = activeConnection.startPacket();
            PacketTypes.PacketType.BodyDamageUpdate.doPacket(startPacket);
            startPacket.putByte((byte)1);
            startPacket.putShort(short1);
            startPacket.putShort(short2);
            PacketTypes.PacketType.BodyDamageUpdate.send(activeConnection);
        }
        else if (value == 2) {
            final short short3 = src.getShort();
            final short short4 = src.getShort();
            final Long n2 = GameServer.IDToAddressMap.get(short4);
            if (n2 == null) {
                return;
            }
            final UdpConnection activeConnection2 = GameServer.udpEngine.getActiveConnection(n2);
            if (activeConnection2 == null) {
                return;
            }
            final ByteBufferWriter startPacket2 = activeConnection2.startPacket();
            PacketTypes.PacketType.BodyDamageUpdate.doPacket(startPacket2);
            startPacket2.putByte((byte)2);
            startPacket2.putShort(short3);
            startPacket2.putShort(short4);
            PacketTypes.PacketType.BodyDamageUpdate.send(activeConnection2);
        }
        else {
            if (value != 3) {
                return;
            }
            final short short5 = src.getShort();
            final short short6 = src.getShort();
            final Long n3 = GameServer.IDToAddressMap.get(short6);
            if (n3 == null) {
                return;
            }
            final UdpConnection activeConnection3 = GameServer.udpEngine.getActiveConnection(n3);
            if (activeConnection3 == null) {
                return;
            }
            final ByteBufferWriter startPacket3 = activeConnection3.startPacket();
            PacketTypes.PacketType.BodyDamageUpdate.doPacket(startPacket3);
            startPacket3.putByte((byte)3);
            startPacket3.putShort(short5);
            startPacket3.putShort(short6);
            startPacket3.bb.put(src);
            PacketTypes.PacketType.BodyDamageUpdate.send(activeConnection3);
        }
    }
    
    public void clientPacket(final ByteBuffer byteBuffer) {
        final byte value = byteBuffer.get();
        if (value == 1) {
            final short short1 = byteBuffer.getShort();
            final short short2 = byteBuffer.getShort();
            for (short n = 0; n < IsoPlayer.numPlayers; ++n) {
                final IsoPlayer isoPlayer = IsoPlayer.players[n];
                noise(invokedynamic(makeConcatWithConstants:(SS)Ljava/lang/String;, short2, isoPlayer.getOnlineID()));
                if (isoPlayer != null && isoPlayer.isAlive() && isoPlayer.getOnlineID() == short2) {
                    this.startSendingUpdates(n, short1);
                    break;
                }
            }
            return;
        }
        if (value == 2) {
            final short short3 = byteBuffer.getShort();
            final short short4 = byteBuffer.getShort();
            for (short n2 = 0; n2 < IsoPlayer.numPlayers; ++n2) {
                final IsoPlayer isoPlayer2 = IsoPlayer.players[n2];
                if (isoPlayer2 != null && isoPlayer2.getOnlineID() == short4) {
                    this.stopSendingUpdates(n2, short3);
                    break;
                }
            }
            return;
        }
        if (value != 3) {
            return;
        }
        final short short5 = byteBuffer.getShort();
        byteBuffer.getShort();
        final GameClient instance = GameClient.instance;
        final IsoPlayer isoPlayer3 = GameClient.IDToPlayerMap.get(short5);
        if (isoPlayer3 == null) {
            return;
        }
        final BodyDamage bodyDamageRemote = isoPlayer3.getBodyDamageRemote();
        byte b = byteBuffer.get();
        if (b == 50) {
            bodyDamageRemote.setOverallBodyHealth(byteBuffer.getFloat());
            bodyDamageRemote.setRemotePainLevel(byteBuffer.get());
            bodyDamageRemote.IsFakeInfected = (byteBuffer.get() == 1);
            bodyDamageRemote.InfectionLevel = byteBuffer.getFloat();
            b = byteBuffer.get();
        }
        while (b == 64) {
            final BodyPart bodyPart = bodyDamageRemote.BodyParts.get(byteBuffer.get());
            for (byte b2 = byteBuffer.get(); b2 != 65; b2 = byteBuffer.get()) {
                bodyPart.sync(byteBuffer, b2);
            }
            b = byteBuffer.get();
        }
    }
    
    static {
        BodyDamageSync.instance = new BodyDamageSync();
    }
    
    public static final class Updater
    {
        static ByteBuffer bb;
        short localIndex;
        short remoteID;
        BodyDamage bdLocal;
        BodyDamage bdSent;
        boolean partStarted;
        byte partIndex;
        long sendTime;
        
        void update() {
            final long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis - this.sendTime < 500L) {
                return;
            }
            this.sendTime = currentTimeMillis;
            Updater.bb.clear();
            final int moodleLevel = this.bdLocal.getParentChar().getMoodles().getMoodleLevel(MoodleType.Pain);
            if (this.compareFloats(this.bdLocal.getOverallBodyHealth(), (float)(int)this.bdSent.getOverallBodyHealth()) || moodleLevel != this.bdSent.getRemotePainLevel() || this.bdLocal.IsFakeInfected != this.bdSent.IsFakeInfected || this.compareFloats(this.bdLocal.InfectionLevel, this.bdSent.InfectionLevel)) {
                Updater.bb.put((byte)50);
                Updater.bb.putFloat(this.bdLocal.getOverallBodyHealth());
                Updater.bb.put((byte)moodleLevel);
                Updater.bb.put((byte)(this.bdLocal.IsFakeInfected ? 1 : 0));
                Updater.bb.putFloat(this.bdLocal.InfectionLevel);
                this.bdSent.setOverallBodyHealth(this.bdLocal.getOverallBodyHealth());
                this.bdSent.setRemotePainLevel(moodleLevel);
                this.bdSent.IsFakeInfected = this.bdLocal.IsFakeInfected;
                this.bdSent.InfectionLevel = this.bdLocal.InfectionLevel;
            }
            for (int i = 0; i < this.bdLocal.BodyParts.size(); ++i) {
                this.updatePart(i);
            }
            if (Updater.bb.position() > 0) {
                Updater.bb.put((byte)65);
                final ByteBufferWriter startPacket = GameClient.connection.startPacket();
                PacketTypes.PacketType.BodyDamageUpdate.doPacket(startPacket);
                startPacket.putByte((byte)3);
                startPacket.putShort(IsoPlayer.players[this.localIndex].getOnlineID());
                startPacket.putShort(this.remoteID);
                startPacket.bb.put(Updater.bb.array(), 0, Updater.bb.position());
                PacketTypes.PacketType.BodyDamageUpdate.send(GameClient.connection);
            }
        }
        
        void updatePart(final int n) {
            final BodyPart bodyPart = this.bdLocal.BodyParts.get(n);
            final BodyPart bodyPart2 = this.bdSent.BodyParts.get(n);
            this.partStarted = false;
            this.partIndex = (byte)n;
            bodyPart.sync(bodyPart2, this);
            if (this.partStarted) {
                Updater.bb.put((byte)65);
            }
        }
        
        public void updateField(final byte b, final boolean b2) {
            if (!this.partStarted) {
                Updater.bb.put((byte)64);
                Updater.bb.put(this.partIndex);
                this.partStarted = true;
            }
            Updater.bb.put(b);
            Updater.bb.put((byte)(b2 ? 1 : 0));
        }
        
        private boolean compareFloats(final float f1, final float f2) {
            return Float.compare(f1, 0.0f) != Float.compare(f2, 0.0f) || (int)f1 != (int)f2;
        }
        
        public boolean updateField(final byte b, final float n, final float n2) {
            if (!this.compareFloats(n, n2)) {
                return false;
            }
            if (!this.partStarted) {
                Updater.bb.put((byte)64);
                Updater.bb.put(this.partIndex);
                this.partStarted = true;
            }
            Updater.bb.put(b);
            Updater.bb.putFloat(n);
            return true;
        }
        
        public void updateField(final byte b, final String s) {
            if (!this.partStarted) {
                Updater.bb.put((byte)64);
                Updater.bb.put(this.partIndex);
                this.partStarted = true;
            }
            Updater.bb.put(b);
            GameWindow.WriteStringUTF(Updater.bb, s);
        }
        
        static {
            Updater.bb = ByteBuffer.allocate(1024);
        }
    }
}

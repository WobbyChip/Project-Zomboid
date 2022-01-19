// 
// Decompiled by Procyon v0.5.36
// 

package zombie.popman;

import zombie.WorldSoundManager;
import java.util.Iterator;
import zombie.iso.IsoMetaGrid;
import zombie.core.raknet.RakVoice;
import zombie.core.Colors;
import zombie.core.Color;
import zombie.characters.IsoPlayer;
import zombie.iso.Vector2;
import java.util.Map;
import zombie.iso.IsoChunkMap;
import zombie.debug.DebugOptions;
import zombie.iso.IsoWorld;
import zombie.GameTime;
import zombie.network.GameServer;
import zombie.core.raknet.UdpConnection;
import java.util.List;
import zombie.core.network.ByteBufferWriter;
import zombie.network.PacketTypes;
import zombie.network.GameClient;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public final class MPDebugInfo
{
    public static final MPDebugInfo instance;
    private static final ConcurrentHashMap<Long, MPSoundDebugInfo> debugSounds;
    private final ArrayList<MPCell> loadedCells;
    private final ObjectPool<MPCell> cellPool;
    private final LoadedAreas loadedAreas;
    private ArrayList<MPRepopEvent> repopEvents;
    private final ObjectPool<MPRepopEvent> repopEventPool;
    private short repopEpoch;
    private long requestTime;
    private boolean requestFlag;
    private boolean requestPacketReceived;
    private final ByteBuffer byteBuffer;
    private float RESPAWN_EVERY_HOURS;
    private float REPOP_DISPLAY_HOURS;
    
    public MPDebugInfo() {
        this.loadedCells = new ArrayList<MPCell>();
        this.cellPool = new ObjectPool<MPCell>(MPCell::new);
        this.loadedAreas = new LoadedAreas(false);
        this.repopEvents = new ArrayList<MPRepopEvent>();
        this.repopEventPool = new ObjectPool<MPRepopEvent>(MPRepopEvent::new);
        this.repopEpoch = 0;
        this.requestTime = 0L;
        this.requestFlag = false;
        this.requestPacketReceived = false;
        this.byteBuffer = ByteBuffer.allocateDirect(1024);
        this.RESPAWN_EVERY_HOURS = 1.0f;
        this.REPOP_DISPLAY_HOURS = 0.5f;
    }
    
    private static native boolean n_hasData(final boolean p0);
    
    private static native void n_requestData();
    
    private static native int n_getLoadedCellsCount();
    
    private static native int n_getLoadedCellsData(final int p0, final ByteBuffer p1);
    
    private static native int n_getLoadedAreasCount();
    
    private static native int n_getLoadedAreasData(final int p0, final ByteBuffer p1);
    
    private static native int n_getRepopEventCount();
    
    private static native int n_getRepopEventData(final int p0, final ByteBuffer p1);
    
    private void requestServerInfo() {
        if (!GameClient.bClient) {
            return;
        }
        final long currentTimeMillis = System.currentTimeMillis();
        if (this.requestTime + 1000L > currentTimeMillis) {
            return;
        }
        this.requestTime = currentTimeMillis;
        final ByteBufferWriter startPacket = GameClient.connection.startPacket();
        PacketTypes.PacketType.KeepAlive.doPacket(startPacket);
        startPacket.bb.put((byte)1);
        startPacket.bb.putShort(this.repopEpoch);
        PacketTypes.PacketType.KeepAlive.send(GameClient.connection);
    }
    
    public void clientPacket(final ByteBuffer byteBuffer) {
        if (!GameClient.bClient) {
            return;
        }
        final byte value = byteBuffer.get();
        if (value == 1) {
            this.cellPool.release(this.loadedCells);
            this.loadedCells.clear();
            this.RESPAWN_EVERY_HOURS = byteBuffer.getFloat();
            for (short short1 = byteBuffer.getShort(), n = 0; n < short1; ++n) {
                final MPCell e = this.cellPool.alloc();
                e.cx = byteBuffer.getShort();
                e.cy = byteBuffer.getShort();
                e.currentPopulation = byteBuffer.getShort();
                e.desiredPopulation = byteBuffer.getShort();
                e.lastRepopTime = byteBuffer.getFloat();
                this.loadedCells.add(e);
            }
            this.loadedAreas.clear();
            for (short short2 = byteBuffer.getShort(), n2 = 0; n2 < short2; ++n2) {
                this.loadedAreas.add(byteBuffer.getShort(), byteBuffer.getShort(), byteBuffer.getShort(), byteBuffer.getShort());
            }
        }
        if (value == 2) {
            this.repopEventPool.release(this.repopEvents);
            this.repopEvents.clear();
            this.repopEpoch = byteBuffer.getShort();
            for (short short3 = byteBuffer.getShort(), n3 = 0; n3 < short3; ++n3) {
                final MPRepopEvent e2 = this.repopEventPool.alloc();
                e2.wx = byteBuffer.getShort();
                e2.wy = byteBuffer.getShort();
                e2.worldAge = byteBuffer.getFloat();
                this.repopEvents.add(e2);
            }
        }
    }
    
    public void serverPacket(final ByteBuffer byteBuffer, final UdpConnection udpConnection) {
        if (!GameServer.bServer) {
            return;
        }
        if (!udpConnection.accessLevel.equals("admin")) {
            return;
        }
        int value = byteBuffer.get();
        if (value == 1) {
            this.requestTime = System.currentTimeMillis();
            this.requestPacketReceived = true;
            final short short1 = byteBuffer.getShort();
            final ByteBufferWriter startPacket = udpConnection.startPacket();
            PacketTypes.PacketType.KeepAlive.doPacket(startPacket);
            startPacket.bb.put((byte)1);
            startPacket.bb.putFloat(this.RESPAWN_EVERY_HOURS);
            startPacket.bb.putShort((short)this.loadedCells.size());
            for (int i = 0; i < this.loadedCells.size(); ++i) {
                final MPCell mpCell = this.loadedCells.get(i);
                startPacket.bb.putShort(mpCell.cx);
                startPacket.bb.putShort(mpCell.cy);
                startPacket.bb.putShort(mpCell.currentPopulation);
                startPacket.bb.putShort(mpCell.desiredPopulation);
                startPacket.bb.putFloat(mpCell.lastRepopTime);
            }
            startPacket.bb.putShort((short)this.loadedAreas.count);
            for (int j = 0; j < this.loadedAreas.count; ++j) {
                int n = j * 4;
                startPacket.bb.putShort((short)this.loadedAreas.areas[n++]);
                startPacket.bb.putShort((short)this.loadedAreas.areas[n++]);
                startPacket.bb.putShort((short)this.loadedAreas.areas[n++]);
                startPacket.bb.putShort((short)this.loadedAreas.areas[n++]);
            }
            if (short1 != this.repopEpoch) {
                value = 2;
            }
            PacketTypes.PacketType.KeepAlive.send(udpConnection);
        }
        if (value == 2) {
            final ByteBufferWriter startPacket2 = udpConnection.startPacket();
            PacketTypes.PacketType.KeepAlive.doPacket(startPacket2);
            startPacket2.bb.put((byte)2);
            startPacket2.bb.putShort(this.repopEpoch);
            startPacket2.bb.putShort((short)this.repopEvents.size());
            for (int k = 0; k < this.repopEvents.size(); ++k) {
                final MPRepopEvent mpRepopEvent = this.repopEvents.get(k);
                startPacket2.bb.putShort((short)mpRepopEvent.wx);
                startPacket2.bb.putShort((short)mpRepopEvent.wy);
                startPacket2.bb.putFloat(mpRepopEvent.worldAge);
            }
            PacketTypes.PacketType.KeepAlive.send(udpConnection);
            return;
        }
        if (value == 3) {
            ZombiePopulationManager.instance.dbgSpawnTimeToZero(byteBuffer.getShort(), byteBuffer.getShort());
            return;
        }
        if (value == 4) {
            ZombiePopulationManager.instance.dbgClearZombies(byteBuffer.getShort(), byteBuffer.getShort());
            return;
        }
        if (value == 5) {
            ZombiePopulationManager.instance.dbgSpawnNow(byteBuffer.getShort(), byteBuffer.getShort());
        }
    }
    
    public void request() {
        if (!GameServer.bServer) {
            return;
        }
        this.requestTime = System.currentTimeMillis();
    }
    
    private void addRepopEvent(final int n, final int n2, final float n3) {
        final float n4 = (float)GameTime.getInstance().getWorldAgeHours();
        while (!this.repopEvents.isEmpty() && this.repopEvents.get(0).worldAge + this.REPOP_DISPLAY_HOURS < n4) {
            this.repopEventPool.release(this.repopEvents.remove(0));
        }
        this.repopEvents.add(this.repopEventPool.alloc().init(n, n2, n3));
        ++this.repopEpoch;
    }
    
    public void serverUpdate() {
        if (!GameServer.bServer) {
            return;
        }
        if (this.requestTime + 10000L < System.currentTimeMillis()) {
            this.requestFlag = false;
            this.requestPacketReceived = false;
            return;
        }
        if (this.requestFlag) {
            if (n_hasData(false)) {
                this.requestFlag = false;
                this.cellPool.release(this.loadedCells);
                this.loadedCells.clear();
                this.loadedAreas.clear();
                final int n_getLoadedCellsCount = n_getLoadedCellsCount();
                int i = 0;
                while (i < n_getLoadedCellsCount) {
                    this.byteBuffer.clear();
                    final int n_getLoadedCellsData = n_getLoadedCellsData(i, this.byteBuffer);
                    i += n_getLoadedCellsData;
                    for (int j = 0; j < n_getLoadedCellsData; ++j) {
                        final MPCell e = this.cellPool.alloc();
                        e.cx = this.byteBuffer.getShort();
                        e.cy = this.byteBuffer.getShort();
                        e.currentPopulation = this.byteBuffer.getShort();
                        e.desiredPopulation = this.byteBuffer.getShort();
                        e.lastRepopTime = this.byteBuffer.getFloat();
                        this.loadedCells.add(e);
                    }
                }
                final int n_getLoadedAreasCount = n_getLoadedAreasCount();
                int k = 0;
                while (k < n_getLoadedAreasCount) {
                    this.byteBuffer.clear();
                    final int n_getLoadedAreasData = n_getLoadedAreasData(k, this.byteBuffer);
                    k += n_getLoadedAreasData;
                    for (int l = 0; l < n_getLoadedAreasData; ++l) {
                        final boolean b = this.byteBuffer.get() == 0;
                        this.loadedAreas.add(this.byteBuffer.getShort(), this.byteBuffer.getShort(), this.byteBuffer.getShort(), this.byteBuffer.getShort());
                    }
                }
            }
        }
        else if (this.requestPacketReceived) {
            n_requestData();
            this.requestFlag = true;
            this.requestPacketReceived = false;
        }
        if (n_hasData(true)) {
            final int n_getRepopEventCount = n_getRepopEventCount();
            int n = 0;
            while (n < n_getRepopEventCount) {
                this.byteBuffer.clear();
                final int n_getRepopEventData = n_getRepopEventData(n, this.byteBuffer);
                n += n_getRepopEventData;
                for (int n2 = 0; n2 < n_getRepopEventData; ++n2) {
                    this.addRepopEvent(this.byteBuffer.getShort(), this.byteBuffer.getShort(), this.byteBuffer.getFloat());
                }
            }
        }
    }
    
    boolean isRespawnEnabled() {
        return !IsoWorld.getZombiesDisabled() && this.RESPAWN_EVERY_HOURS > 0.0f;
    }
    
    public void render(final ZombiePopulationRenderer zombiePopulationRenderer, final float n) {
        this.requestServerInfo();
        final float n2 = (float)GameTime.getInstance().getWorldAgeHours();
        final IsoMetaGrid metaGrid = IsoWorld.instance.MetaGrid;
        zombiePopulationRenderer.outlineRect(metaGrid.minX * 300 * 1.0f, metaGrid.minY * 300 * 1.0f, (metaGrid.maxX - metaGrid.minX + 1) * 300 * 1.0f, (metaGrid.maxY - metaGrid.minY + 1) * 300 * 1.0f, 1.0f, 1.0f, 1.0f, 0.25f);
        for (int i = 0; i < this.loadedCells.size(); ++i) {
            final MPCell mpCell = this.loadedCells.get(i);
            zombiePopulationRenderer.outlineRect((float)(mpCell.cx * 300), (float)(mpCell.cy * 300), 300.0f, 300.0f, 1.0f, 1.0f, 1.0f, 0.25f);
            if (this.isRespawnEnabled()) {
                float n3 = Math.min(n2 - mpCell.lastRepopTime, this.RESPAWN_EVERY_HOURS) / this.RESPAWN_EVERY_HOURS;
                if (mpCell.lastRepopTime > n2) {
                    n3 = 0.0f;
                }
                zombiePopulationRenderer.outlineRect((float)(mpCell.cx * 300 + 1), (float)(mpCell.cy * 300 + 1), 298.0f, 298.0f, 0.0f, 1.0f, 0.0f, n3 * n3);
            }
        }
        for (int j = 0; j < this.loadedAreas.count; ++j) {
            int n4 = j * 4;
            zombiePopulationRenderer.outlineRect((float)(this.loadedAreas.areas[n4++] * 10), (float)(this.loadedAreas.areas[n4++] * 10), (float)(this.loadedAreas.areas[n4++] * 10), (float)(this.loadedAreas.areas[n4++] * 10), 0.7f, 0.7f, 0.7f, 1.0f);
        }
        for (int k = 0; k < this.repopEvents.size(); ++k) {
            final MPRepopEvent mpRepopEvent = this.repopEvents.get(k);
            if (mpRepopEvent.worldAge + this.REPOP_DISPLAY_HOURS >= n2) {
                zombiePopulationRenderer.outlineRect((float)(mpRepopEvent.wx * 10), (float)(mpRepopEvent.wy * 10), 50.0f, 50.0f, 0.0f, 0.0f, 1.0f, Math.max(1.0f - (n2 - mpRepopEvent.worldAge) / this.REPOP_DISPLAY_HOURS, 0.1f));
            }
        }
        if (GameClient.bClient && DebugOptions.instance.MultiplayerShowPosition.getValue()) {
            final float n5 = (float)((IsoChunkMap.ChunkGridWidth / 2 + 2) * 10);
            for (final Map.Entry<Short, Vector2> entry2 : GameClient.positions.entrySet()) {
                final IsoPlayer isoPlayer = GameClient.IDToPlayerMap.get(entry2.getKey());
                Color color = Color.white;
                if (isoPlayer != null) {
                    color = isoPlayer.getSpeakColour();
                }
                final Vector2 vector2 = entry2.getValue();
                zombiePopulationRenderer.renderZombie(vector2.x, vector2.y, color.r, color.g, color.b);
                zombiePopulationRenderer.renderCircle(vector2.x, vector2.y, n5, color.r, color.g, color.b, color.a);
                zombiePopulationRenderer.renderString(vector2.x, vector2.y, (isoPlayer == null) ? String.valueOf(entry2.getKey()) : isoPlayer.getUsername(), color.r, color.g, color.b, color.a);
            }
            if (IsoPlayer.getInstance() != null) {
                final IsoPlayer instance = IsoPlayer.getInstance();
                final Color speakColour = instance.getSpeakColour();
                zombiePopulationRenderer.renderZombie(instance.x, instance.y, speakColour.r, speakColour.g, speakColour.b);
                zombiePopulationRenderer.renderCircle(instance.x, instance.y, n5, speakColour.r, speakColour.g, speakColour.b, speakColour.a);
                zombiePopulationRenderer.renderString(instance.x, instance.y, instance.getUsername(), speakColour.r, speakColour.g, speakColour.b, speakColour.a);
                final Color lightBlue = Colors.LightBlue;
                zombiePopulationRenderer.renderCircle(instance.x, instance.y, RakVoice.GetMinDistance(), lightBlue.r, lightBlue.g, lightBlue.b, lightBlue.a);
                zombiePopulationRenderer.renderCircle(instance.x, instance.y, RakVoice.GetMaxDistance(), lightBlue.r, lightBlue.g, lightBlue.b, lightBlue.a);
            }
        }
        if (n > 0.25f) {
            for (int l = 0; l < this.loadedCells.size(); ++l) {
                final MPCell mpCell2 = this.loadedCells.get(l);
                zombiePopulationRenderer.renderCellInfo(mpCell2.cx, mpCell2.cy, mpCell2.currentPopulation, mpCell2.desiredPopulation, mpCell2.lastRepopTime + this.RESPAWN_EVERY_HOURS - n2);
            }
        }
        try {
            MPDebugInfo.debugSounds.entrySet().removeIf(entry -> System.currentTimeMillis() > entry.getKey() + 1000L);
            for (final Map.Entry<Long, MPSoundDebugInfo> entry3 : MPDebugInfo.debugSounds.entrySet()) {
                Color color2 = Colors.LightBlue;
                if (entry3.getValue().sourceIsZombie) {
                    color2 = Colors.GreenYellow;
                }
                else if (entry3.getValue().bRepeating) {
                    color2 = Colors.Coral;
                }
                zombiePopulationRenderer.renderCircle((float)entry3.getValue().x, (float)entry3.getValue().y, (float)entry3.getValue().radius, color2.r, color2.g, color2.b, 1.0f - Math.max(0.0f, Math.min(1.0f, (System.currentTimeMillis() - entry3.getKey()) / 1000.0f)));
            }
        }
        catch (Exception ex) {}
    }
    
    public static void AddDebugSound(final WorldSoundManager.WorldSound worldSound) {
        try {
            MPDebugInfo.debugSounds.put(System.currentTimeMillis(), new MPSoundDebugInfo(worldSound));
        }
        catch (Exception ex) {}
    }
    
    static {
        instance = new MPDebugInfo();
        debugSounds = new ConcurrentHashMap<Long, MPSoundDebugInfo>();
    }
    
    private static class MPSoundDebugInfo
    {
        int x;
        int y;
        int radius;
        boolean bRepeating;
        boolean sourceIsZombie;
        
        MPSoundDebugInfo(final WorldSoundManager.WorldSound worldSound) {
            this.x = worldSound.x;
            this.y = worldSound.y;
            this.radius = worldSound.radius;
            this.bRepeating = worldSound.bRepeating;
            this.sourceIsZombie = worldSound.sourceIsZombie;
        }
    }
    
    private static final class MPCell
    {
        public short cx;
        public short cy;
        public short currentPopulation;
        public short desiredPopulation;
        public float lastRepopTime;
        
        MPCell init(final int n, final int n2, final int n3, final int n4, final float lastRepopTime) {
            this.cx = (short)n;
            this.cy = (short)n2;
            this.currentPopulation = (short)n3;
            this.desiredPopulation = (short)n4;
            this.lastRepopTime = lastRepopTime;
            return this;
        }
    }
    
    private static final class MPRepopEvent
    {
        public int wx;
        public int wy;
        public float worldAge;
        
        public MPRepopEvent init(final int wx, final int wy, final float worldAge) {
            this.wx = wx;
            this.wy = wy;
            this.worldAge = worldAge;
            return this;
        }
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network;

import zombie.iso.IsoUtils;
import zombie.core.logger.LoggerManager;
import java.util.Iterator;
import zombie.VirtualZombieManager;
import zombie.core.physics.WorldSimulation;
import zombie.iso.RoomDef;
import java.util.HashSet;
import zombie.characters.IsoZombie;
import java.util.HashMap;
import zombie.iso.IsoChunk;
import zombie.iso.IsoWorld;
import zombie.core.raknet.UdpConnection;
import zombie.popman.NetworkZombiePacker;
import zombie.core.Rand;
import zombie.iso.IsoGridSquare;
import zombie.core.znet.SteamUtils;
import zombie.vehicles.VehiclesDB2;
import zombie.savefile.ServerPlayerDB;
import zombie.popman.ZombiePopulationManager;
import zombie.globalObjects.SGlobalObjects;
import zombie.MapCollisionData;
import zombie.ReanimatedPlayers;
import zombie.GameTime;
import java.util.Comparator;
import java.util.List;
import java.util.Collections;
import zombie.characters.IsoPlayer;
import zombie.debug.DebugType;
import zombie.core.stash.StashSystem;
import zombie.debug.DebugLog;
import zombie.iso.Vector3;
import zombie.iso.Vector2;
import zombie.iso.IsoMetaGrid;
import java.util.ArrayList;
import zombie.core.utils.OnceEvery;

public class ServerMap
{
    public boolean bUpdateLOSThisFrame;
    public static OnceEvery LOSTick;
    public static OnceEvery TimeTick;
    public static final int CellSize = 50;
    public static final int ChunksPerCellWidth = 5;
    public long LastSaved;
    private static boolean MapLoading;
    public final ZombieIDMap ZombieMap;
    public boolean bQueuedSaveAll;
    public boolean bQueuedQuit;
    public static ServerMap instance;
    public ServerCell[] cellMap;
    public ArrayList<ServerCell> LoadedCells;
    public ArrayList<ServerCell> ReleventNow;
    int width;
    int height;
    IsoMetaGrid grid;
    ArrayList<ServerCell> ToLoad;
    static final DistToCellComparator distToCellComparator;
    private final ArrayList<ServerCell> tempCells;
    long lastTick;
    Vector2 start;
    
    public ServerMap() {
        this.bUpdateLOSThisFrame = false;
        this.LastSaved = 0L;
        this.ZombieMap = new ZombieIDMap();
        this.bQueuedSaveAll = false;
        this.bQueuedQuit = false;
        this.LoadedCells = new ArrayList<ServerCell>();
        this.ReleventNow = new ArrayList<ServerCell>();
        this.ToLoad = new ArrayList<ServerCell>();
        this.tempCells = new ArrayList<ServerCell>();
        this.lastTick = 0L;
    }
    
    public short getUniqueZombieId() {
        return this.ZombieMap.allocateID();
    }
    
    public Vector3 getStartLocation(final ServerWorldDatabase.LogonResult logonResult) {
        return new Vector3(10745, 9412, 0);
    }
    
    public void SaveAll() {
        final long nanoTime = System.nanoTime();
        for (int i = 0; i < this.LoadedCells.size(); ++i) {
            this.LoadedCells.get(i).Save();
        }
        this.grid.save();
        DebugLog.log(invokedynamic(makeConcatWithConstants:(D)Ljava/lang/String;, (System.nanoTime() - nanoTime) / 1000000.0));
    }
    
    public void QueueSaveAll() {
        this.bQueuedSaveAll = true;
    }
    
    public void QueueQuit() {
        this.bQueuedSaveAll = true;
        this.bQueuedQuit = true;
    }
    
    public int toServerCellX(int n) {
        n *= 300;
        n /= 50;
        return n;
    }
    
    public int toServerCellY(int n) {
        n *= 300;
        n /= 50;
        return n;
    }
    
    public int toWorldCellX(int n) {
        n *= 50;
        n /= 300;
        return n;
    }
    
    public int toWorldCellY(int n) {
        n *= 50;
        n /= 300;
        return n;
    }
    
    public int getMaxX() {
        int serverCellX = this.toServerCellX(this.grid.maxX + 1);
        if ((this.grid.maxX + 1) * 300 % 50 == 0) {
            --serverCellX;
        }
        return serverCellX;
    }
    
    public int getMaxY() {
        int serverCellY = this.toServerCellY(this.grid.maxY + 1);
        if ((this.grid.maxY + 1) * 300 % 50 == 0) {
            --serverCellY;
        }
        return serverCellY;
    }
    
    public int getMinX() {
        return this.toServerCellX(this.grid.minX);
    }
    
    public int getMinY() {
        return this.toServerCellY(this.grid.minY);
    }
    
    public void init(final IsoMetaGrid grid) {
        this.grid = grid;
        this.width = this.getMaxX() - this.getMinX() + 1;
        this.height = this.getMaxY() - this.getMinY() + 1;
        assert this.width * 50 >= grid.getWidth() * 300;
        assert this.height * 50 >= grid.getHeight() * 300;
        assert this.getMaxX() * 50 < (grid.getMaxX() + 1) * 300;
        assert this.getMaxY() * 50 < (grid.getMaxY() + 1) * 300;
        this.cellMap = new ServerCell[this.width * this.height];
        StashSystem.init();
    }
    
    public ServerCell getCell(final int n, final int n2) {
        if (!this.isValidCell(n, n2)) {
            return null;
        }
        return this.cellMap[n2 * this.width + n];
    }
    
    public boolean isValidCell(final int n, final int n2) {
        return n >= 0 && n2 >= 0 && n < this.width && n2 < this.height;
    }
    
    public void loadOrKeepRelevent(final int n, final int n2) {
        if (!this.isValidCell(n, n2)) {
            return;
        }
        final ServerCell cell = this.getCell(n, n2);
        if (cell == null) {
            final ServerCell e = new ServerCell();
            e.WX = n + this.getMinX();
            e.WY = n2 + this.getMinY();
            if (ServerMap.MapLoading) {
                DebugLog.log(DebugType.MapLoading, invokedynamic(makeConcatWithConstants:(IIII)Ljava/lang/String;, e.WX, e.WY, this.toWorldCellX(e.WX), this.toWorldCellX(e.WY)));
            }
            this.cellMap[n2 * this.width + n] = e;
            this.ToLoad.add(e);
            MPStatistic.getInstance().ServerMapToLoad.Added();
            this.LoadedCells.add(e);
            MPStatistic.getInstance().ServerMapLoadedCells.Added();
            this.ReleventNow.add(e);
        }
        else if (!this.ReleventNow.contains(cell)) {
            this.ReleventNow.add(cell);
        }
    }
    
    public void characterIn(final IsoPlayer isoPlayer) {
        while (this.grid == null) {
            try {
                Thread.sleep(1000L);
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        final int n = isoPlayer.OnlineChunkGridWidth / 2 * 10;
        final int n2 = (int)(Math.floor((isoPlayer.getX() - n) / 50.0f) - this.getMinX());
        final int n3 = (int)(Math.floor((isoPlayer.getX() + n) / 50.0f) - this.getMinX());
        final int n4 = (int)(Math.floor((isoPlayer.getY() - n) / 50.0f) - this.getMinY());
        for (int n5 = (int)(Math.floor((isoPlayer.getY() + n) / 50.0f) - this.getMinY()), i = n4; i <= n5; ++i) {
            for (int j = n2; j <= n3; ++j) {
                this.loadOrKeepRelevent(j, i);
            }
        }
    }
    
    public void characterIn(final int n, final int n2, final int n3) {
        while (this.grid == null) {
            try {
                Thread.sleep(1000L);
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        final int n4 = n * 10;
        final int n5 = n2 * 10;
        final int n6 = (int)(n4 / 50.0f);
        final int n7 = (int)(n5 / 50.0f);
        final int n8 = n6 - this.getMinX();
        final int n9 = n7 - this.getMinY();
        final int n10 = n8;
        final int n11 = n9;
        final int n12 = n * 10 % 50;
        final int n13 = n2 * 10 % 50;
        final int n14 = n3 / 2 * 10;
        int n15 = n10;
        int n16 = n11;
        int n17 = n10;
        int n18 = n11;
        if (n12 < n14) {
            --n15;
        }
        if (n12 > 50 - n14) {
            ++n17;
        }
        if (n13 < n14) {
            --n16;
        }
        if (n13 > 50 - n14) {
            ++n18;
        }
        for (int i = n16; i <= n18; ++i) {
            for (int j = n15; j <= n17; ++j) {
                this.loadOrKeepRelevent(j, i);
            }
        }
    }
    
    public void loadMapChunk(final int n, final int n2) {
        while (this.grid == null) {
            try {
                Thread.sleep(1000L);
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        this.loadOrKeepRelevent((int)(n / 50.0f) - this.getMinX(), (int)(n2 / 50.0f) - this.getMinY());
    }
    
    public void preupdate() {
        final long nanoTime = System.nanoTime();
        final double n = (nanoTime - this.lastTick) * 1.0E-6;
        this.lastTick = nanoTime;
        ServerMap.MapLoading = DebugType.Do(DebugType.MapLoading);
        for (int i = 0; i < this.ToLoad.size(); ++i) {
            final ServerCell o = this.ToLoad.get(i);
            if (o.bLoadingWasCancelled) {
                if (ServerMap.MapLoading) {
                    DebugLog.log(DebugType.MapLoading, invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, o.WX, o.WY));
                }
                final int n2 = o.WX - this.getMinX();
                final int n3 = o.WY - this.getMinY();
                assert this.cellMap[n2 + n3 * this.width] == o;
                this.cellMap[n2 + n3 * this.width] = null;
                this.LoadedCells.remove(o);
                this.ReleventNow.remove(o);
                ServerCell.loaded2.remove(o);
                this.ToLoad.remove(i--);
                MPStatistic.getInstance().ServerMapToLoad.Canceled();
            }
        }
        for (int j = 0; j < this.LoadedCells.size(); ++j) {
            final ServerCell o2 = this.LoadedCells.get(j);
            if (o2.bCancelLoading) {
                if (ServerMap.MapLoading) {
                    DebugLog.log(DebugType.MapLoading, invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, o2.WX, o2.WY));
                }
                final int n4 = o2.WX - this.getMinX();
                final int n5 = o2.WY - this.getMinY();
                assert this.cellMap[n4 + n5 * this.width] == o2;
                this.cellMap[n4 + n5 * this.width] = null;
                this.LoadedCells.remove(j--);
                this.ReleventNow.remove(o2);
                ServerCell.loaded2.remove(o2);
                this.ToLoad.remove(o2);
                MPStatistic.getInstance().ServerMapLoadedCells.Canceled();
            }
        }
        for (int k = 0; k < ServerCell.loaded2.size(); ++k) {
            final ServerCell serverCell = ServerCell.loaded2.get(k);
            if (serverCell.bCancelLoading) {
                if (ServerMap.MapLoading) {
                    DebugLog.log(DebugType.MapLoading, invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, serverCell.WX, serverCell.WY));
                }
                final int n6 = serverCell.WX - this.getMinX();
                final int n7 = serverCell.WY - this.getMinY();
                assert this.cellMap[n6 + n7 * this.width] == serverCell;
                this.cellMap[n6 + n7 * this.width] = null;
                this.LoadedCells.remove(serverCell);
                this.ReleventNow.remove(serverCell);
                ServerCell.loaded2.remove(serverCell);
                this.ToLoad.remove(serverCell);
                MPStatistic.getInstance().ServerMapLoaded2.Canceled();
            }
        }
        if (!this.ToLoad.isEmpty()) {
            this.tempCells.clear();
            for (int l = 0; l < this.ToLoad.size(); ++l) {
                final ServerCell e = this.ToLoad.get(l);
                if (!e.bCancelLoading) {
                    if (!e.startedLoading) {
                        this.tempCells.add(e);
                    }
                }
            }
            if (!this.tempCells.isEmpty()) {
                ServerMap.distToCellComparator.init();
                Collections.sort(this.tempCells, ServerMap.distToCellComparator);
                for (int index = 0; index < this.tempCells.size(); ++index) {
                    final ServerCell serverCell2 = this.tempCells.get(index);
                    ServerCell.chunkLoader.addJob(serverCell2);
                    serverCell2.startedLoading = true;
                }
            }
            ServerCell.chunkLoader.getLoaded(ServerCell.loaded);
            for (int index2 = 0; index2 < ServerCell.loaded.size(); ++index2) {
                final ServerCell serverCell3 = ServerCell.loaded.get(index2);
                if (!serverCell3.doingRecalc) {
                    ServerCell.chunkLoader.addRecalcJob(serverCell3);
                    serverCell3.doingRecalc = true;
                }
            }
            ServerCell.loaded.clear();
            ServerCell.chunkLoader.getRecalc(ServerCell.loaded2);
            if (!ServerCell.loaded2.isEmpty()) {
                try {
                    ServerLOS.instance.suspend();
                    for (int index3 = 0; index3 < ServerCell.loaded2.size(); ++index3) {
                        final ServerCell o3 = ServerCell.loaded2.get(index3);
                        System.nanoTime();
                        if (o3.Load2()) {
                            System.nanoTime();
                            --index3;
                            this.ToLoad.remove(o3);
                        }
                    }
                }
                finally {
                    ServerLOS.instance.resume();
                }
            }
        }
        final int value = ServerOptions.instance.SaveWorldEveryMinutes.getValue();
        if (value > 0) {
            final long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis > this.LastSaved + value * 60 * 1000) {
                this.bQueuedSaveAll = true;
                this.LastSaved = currentTimeMillis;
            }
        }
        if (this.bQueuedSaveAll) {
            this.bQueuedSaveAll = false;
            final long nanoTime2 = System.nanoTime();
            this.SaveAll();
            ServerCell.chunkLoader.saveLater(GameTime.instance);
            ReanimatedPlayers.instance.saveReanimatedPlayers();
            MapCollisionData.instance.save();
            SGlobalObjects.save();
            GameServer.UnPauseAllClients();
            System.out.println("Saving finish");
            DebugLog.log(invokedynamic(makeConcatWithConstants:(D)Ljava/lang/String;, (System.nanoTime() - nanoTime2) / 1000000.0));
        }
        if (this.bQueuedQuit) {
            PacketTypes.PacketType.ServerQuit.doPacket(GameServer.udpEngine.startPacket());
            GameServer.udpEngine.endPacketBroadcast(PacketTypes.PacketType.ServerQuit);
            try {
                Thread.sleep(5000L);
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            MapCollisionData.instance.stop();
            ZombiePopulationManager.instance.stop();
            RCONServer.shutdown();
            ServerCell.chunkLoader.quit();
            ServerWorldDatabase.instance.close();
            ServerPlayersVehicles.instance.stop();
            ServerPlayerDB.getInstance().close();
            VehiclesDB2.instance.Reset();
            GameServer.udpEngine.Shutdown();
            ServerGUI.shutdown();
            SteamUtils.shutdown();
            System.exit(0);
        }
        this.ReleventNow.clear();
        this.bUpdateLOSThisFrame = ServerMap.LOSTick.Check();
        if (ServerMap.TimeTick.Check()) {
            ServerCell.chunkLoader.saveLater(GameTime.instance);
        }
    }
    
    private IsoGridSquare getRandomSquareFromCell(int n, int n2) {
        this.loadOrKeepRelevent(n, n2);
        final int n3 = n;
        final int n4 = n2;
        if (this.getCell(n, n2) == null) {
            throw new RuntimeException("Cannot find a random square.");
        }
        n = (n + this.getMinX()) * 50;
        n2 = (n2 + this.getMinY()) * 50;
        int n5 = 100;
        IsoGridSquare gridSquare;
        do {
            gridSquare = this.getGridSquare(Rand.Next(n, n + 50), Rand.Next(n2, n2 + 50), 0);
            --n5;
            if (gridSquare == null) {
                this.loadOrKeepRelevent(n3, n4);
            }
        } while (gridSquare == null && n5 > 0);
        return gridSquare;
    }
    
    public void postupdate() {
        this.LoadedCells.size();
        int n = 0;
        try {
            for (int i = 0; i < this.LoadedCells.size(); ++i) {
                final ServerCell serverCell = this.LoadedCells.get(i);
                final boolean b = this.ReleventNow.contains(serverCell) || !this.outsidePlayerInfluence(serverCell);
                if (!serverCell.bLoaded) {
                    if (!b && !serverCell.bCancelLoading) {
                        if (ServerMap.MapLoading) {
                            DebugLog.log(DebugType.MapLoading, invokedynamic(makeConcatWithConstants:(IIZ)Ljava/lang/String;, serverCell.WX, serverCell.WY, serverCell.startedLoading));
                        }
                        if (!serverCell.startedLoading) {
                            serverCell.bLoadingWasCancelled = true;
                        }
                        serverCell.bCancelLoading = true;
                    }
                }
                else if (!b) {
                    final int n2 = serverCell.WX - this.getMinX();
                    final int n3 = serverCell.WY - this.getMinY();
                    if (n == 0) {
                        ServerLOS.instance.suspend();
                        n = 1;
                    }
                    this.cellMap[n3 * this.width + n2].Unload();
                    this.cellMap[n3 * this.width + n2] = null;
                    this.LoadedCells.remove(serverCell);
                    --i;
                }
                else {
                    serverCell.update();
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            if (n != 0) {
                ServerLOS.instance.resume();
            }
        }
        NetworkZombiePacker.getInstance().postupdate();
        ServerCell.chunkLoader.updateSaved();
    }
    
    public void physicsCheck(final int n, final int n2) {
        final ServerCell cell = this.getCell(n / 50 - this.getMinX(), n2 / 50 - this.getMinY());
        if (cell != null && cell.bLoaded) {
            cell.bPhysicsCheck = true;
        }
    }
    
    private boolean outsidePlayerInfluence(final ServerCell serverCell) {
        final int n = serverCell.WX * 50;
        final int n2 = serverCell.WY * 50;
        final int n3 = (serverCell.WX + 1) * 50;
        final int n4 = (serverCell.WY + 1) * 50;
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            if (udpConnection.RelevantTo((float)n, (float)n2)) {
                return false;
            }
            if (udpConnection.RelevantTo((float)n3, (float)n2)) {
                return false;
            }
            if (udpConnection.RelevantTo((float)n3, (float)n4)) {
                return false;
            }
            if (udpConnection.RelevantTo((float)n, (float)n4)) {
                return false;
            }
        }
        return true;
    }
    
    public void saveZoneInsidePlayerInfluence(final short n) {
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            for (int j = 0; j < udpConnection.players.length; ++j) {
                if (udpConnection.players[j] != null && udpConnection.players[j].OnlineID == n) {
                    final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(udpConnection.players[j].x, udpConnection.players[j].y, udpConnection.players[j].z);
                    if (gridSquare != null) {
                        ServerCell.chunkLoader.addSaveLoadedJob(gridSquare.chunk);
                        return;
                    }
                }
            }
        }
        ServerCell.chunkLoader.updateSaved();
    }
    
    private boolean InsideThePlayerInfluence(final ServerCell serverCell, final short n) {
        final int n2 = serverCell.WX * 50;
        final int n3 = serverCell.WY * 50;
        final int n4 = (serverCell.WX + 1) * 50;
        final int n5 = (serverCell.WY + 1) * 50;
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            for (int j = 0; j < udpConnection.players.length; ++j) {
                if (udpConnection.players[j] != null && udpConnection.players[j].OnlineID == n) {
                    return udpConnection.RelevantToPlayerIndex(j, (float)n2, (float)n3) || udpConnection.RelevantToPlayerIndex(j, (float)n4, (float)n3) || udpConnection.RelevantToPlayerIndex(j, (float)n4, (float)n5) || udpConnection.RelevantToPlayerIndex(j, (float)n2, (float)n5);
                }
            }
        }
        return false;
    }
    
    public IsoGridSquare getGridSquare(final int n, final int n2, final int n3) {
        if (!IsoWorld.instance.isValidSquare(n, n2, n3)) {
            return null;
        }
        final int n4 = n / 50;
        final int n5 = n2 / 50;
        final int n6 = n4 - this.getMinX();
        final int n7 = n5 - this.getMinY();
        final int n8 = n / 10;
        final int n9 = n2 / 10;
        final int n10 = n8 % 5;
        final int n11 = n9 % 5;
        final int n12 = n % 10;
        final int n13 = n2 % 10;
        final ServerCell cell = this.getCell(n6, n7);
        if (cell == null || !cell.bLoaded) {
            return null;
        }
        final IsoChunk isoChunk = cell.chunks[n10][n11];
        if (isoChunk == null) {
            return null;
        }
        return isoChunk.getGridSquare(n12, n13, n3);
    }
    
    public void setGridSquare(final int n, final int n2, final int n3, final IsoGridSquare isoGridSquare) {
        final int n4 = n / 50;
        final int n5 = n2 / 50;
        final int n6 = n4 - this.getMinX();
        final int n7 = n5 - this.getMinY();
        final int n8 = n / 10;
        final int n9 = n2 / 10;
        final int n10 = n8 % 5;
        final int n11 = n9 % 5;
        final int n12 = n % 10;
        final int n13 = n2 % 10;
        final ServerCell cell = this.getCell(n6, n7);
        if (cell == null) {
            return;
        }
        final IsoChunk isoChunk = cell.chunks[n10][n11];
        if (isoChunk == null) {
            return;
        }
        isoChunk.setSquare(n12, n13, n3, isoGridSquare);
    }
    
    public boolean isInLoaded(final float n, final float n2) {
        final int n3 = (int)n;
        final int n4 = (int)n2;
        final int n5 = n3 / 50;
        final int n6 = n4 / 50;
        final int n7 = n5 - this.getMinX();
        final int n8 = n6 - this.getMinY();
        return !this.ToLoad.contains(this.getCell(n7, n8)) && this.getCell(n7, n8) != null;
    }
    
    public IsoChunk getChunk(final int n, final int n2) {
        if (n < 0 || n2 < 0) {
            return null;
        }
        final int n3 = n / 5;
        final int n4 = n2 / 5;
        final int n5 = n3 - this.getMinX();
        final int n6 = n4 - this.getMinY();
        final int n7 = n % 5;
        final int n8 = n2 % 5;
        final ServerCell cell = this.getCell(n5, n6);
        if (cell == null || !cell.bLoaded) {
            return null;
        }
        return cell.chunks[n7][n8];
    }
    
    static {
        ServerMap.LOSTick = new OnceEvery(1.0f);
        ServerMap.TimeTick = new OnceEvery(600.0f);
        ServerMap.instance = new ServerMap();
        distToCellComparator = new DistToCellComparator();
    }
    
    public static class ZombieIDMap
    {
        private static int MAX_ZOMBIES;
        private HashMap<Short, IsoZombie> idToZombie2;
        private short next;
        
        ZombieIDMap() {
            this.idToZombie2 = new HashMap<Short, IsoZombie>(ZombieIDMap.MAX_ZOMBIES);
            this.next = (short)Rand.Next(32766);
        }
        
        public void put(final short s, final IsoZombie value) {
            this.idToZombie2.put(s, value);
        }
        
        public void remove(final short s) {
            this.idToZombie2.remove(s);
        }
        
        public IsoZombie get(final short s) {
            return this.idToZombie2.get(s);
        }
        
        private short allocateID() {
            final short next = this.next;
            this.next = (short)(next + 1);
            return next;
        }
        
        public int size() {
            return this.idToZombie2.size();
        }
        
        static {
            ZombieIDMap.MAX_ZOMBIES = 32767;
        }
    }
    
    public static class ServerCell
    {
        public int WX;
        public int WY;
        public boolean bLoaded;
        public boolean bPhysicsCheck;
        public final IsoChunk[][] chunks;
        private HashSet<RoomDef> UnexploredRooms;
        private static ServerChunkLoader chunkLoader;
        private static ArrayList<ServerCell> loaded;
        private boolean startedLoading;
        public boolean bCancelLoading;
        public boolean bLoadingWasCancelled;
        private static ArrayList<ServerCell> loaded2;
        private boolean doingRecalc;
        
        public ServerCell() {
            this.bLoaded = false;
            this.bPhysicsCheck = false;
            this.chunks = new IsoChunk[5][5];
            this.UnexploredRooms = new HashSet<RoomDef>();
            this.startedLoading = false;
            this.bCancelLoading = false;
            this.bLoadingWasCancelled = false;
            this.doingRecalc = false;
        }
        
        public boolean Load2() {
            ServerCell.chunkLoader.getRecalc(ServerCell.loaded2);
            for (int i = 0; i < ServerCell.loaded2.size(); ++i) {
                if (ServerCell.loaded2.get(i) == this) {
                    final long nanoTime = System.nanoTime();
                    this.RecalcAll2();
                    ServerCell.loaded2.remove(i);
                    if (ServerMap.MapLoading) {
                        DebugLog.log(DebugType.MapLoading, invokedynamic(makeConcatWithConstants:(Ljava/util/ArrayList;)Ljava/lang/String;, ServerCell.loaded2));
                    }
                    final float n = (System.nanoTime() - nanoTime) / 1000000.0f;
                    if (ServerMap.MapLoading) {
                        DebugLog.log(DebugType.MapLoading, invokedynamic(makeConcatWithConstants:(IIF)Ljava/lang/String;, this.WX, this.WY, n));
                    }
                    return true;
                }
            }
            return false;
        }
        
        public void RecalcAll2() {
            final int n = this.WX * 5 * 10;
            final int n2 = this.WY * 5 * 10;
            final int n3 = n + 50;
            final int n4 = n2 + 50;
            for (final RoomDef roomDef : this.UnexploredRooms) {
                --roomDef.IndoorZombies;
            }
            this.UnexploredRooms.clear();
            this.bLoaded = true;
            for (int i = 1; i < 8; ++i) {
                for (int j = -1; j < 51; ++j) {
                    final IsoGridSquare gridSquare = ServerMap.instance.getGridSquare(n + j, n2 - 1, i);
                    if (gridSquare != null && !gridSquare.getObjects().isEmpty()) {
                        IsoWorld.instance.CurrentCell.EnsureSurroundNotNull(gridSquare.x, gridSquare.y, i);
                    }
                    else if (j >= 0 && j < 50) {
                        final IsoGridSquare gridSquare2 = ServerMap.instance.getGridSquare(n + j, n2, i);
                        if (gridSquare2 != null && !gridSquare2.getObjects().isEmpty()) {
                            IsoWorld.instance.CurrentCell.EnsureSurroundNotNull(gridSquare2.x, gridSquare2.y, i);
                        }
                    }
                    final IsoGridSquare gridSquare3 = ServerMap.instance.getGridSquare(n + j, n2 + 50, i);
                    if (gridSquare3 != null && !gridSquare3.getObjects().isEmpty()) {
                        IsoWorld.instance.CurrentCell.EnsureSurroundNotNull(gridSquare3.x, gridSquare3.y, i);
                    }
                    else if (j >= 0 && j < 50) {
                        ServerMap.instance.getGridSquare(n + j, n2 + 50 - 1, i);
                        if (gridSquare3 != null && !gridSquare3.getObjects().isEmpty()) {
                            IsoWorld.instance.CurrentCell.EnsureSurroundNotNull(gridSquare3.x, gridSquare3.y, i);
                        }
                    }
                }
                for (int k = 0; k < 50; ++k) {
                    final IsoGridSquare gridSquare4 = ServerMap.instance.getGridSquare(n - 1, n2 + k, i);
                    if (gridSquare4 != null && !gridSquare4.getObjects().isEmpty()) {
                        IsoWorld.instance.CurrentCell.EnsureSurroundNotNull(gridSquare4.x, gridSquare4.y, i);
                    }
                    else {
                        final IsoGridSquare gridSquare5 = ServerMap.instance.getGridSquare(n, n2 + k, i);
                        if (gridSquare5 != null && !gridSquare5.getObjects().isEmpty()) {
                            IsoWorld.instance.CurrentCell.EnsureSurroundNotNull(gridSquare5.x, gridSquare5.y, i);
                        }
                    }
                    final IsoGridSquare gridSquare6 = ServerMap.instance.getGridSquare(n + 50, n2 + k, i);
                    if (gridSquare6 != null && !gridSquare6.getObjects().isEmpty()) {
                        IsoWorld.instance.CurrentCell.EnsureSurroundNotNull(gridSquare6.x, gridSquare6.y, i);
                    }
                    else {
                        final IsoGridSquare gridSquare7 = ServerMap.instance.getGridSquare(n + 50 - 1, n2 + k, i);
                        if (gridSquare7 != null && !gridSquare7.getObjects().isEmpty()) {
                            IsoWorld.instance.CurrentCell.EnsureSurroundNotNull(gridSquare7.x, gridSquare7.y, i);
                        }
                    }
                }
            }
            for (int l = 0; l < 8; ++l) {
                for (int n5 = 0; n5 < 50; ++n5) {
                    final IsoGridSquare gridSquare8 = ServerMap.instance.getGridSquare(n + n5, n2 + 0, l);
                    if (gridSquare8 != null) {
                        gridSquare8.RecalcAllWithNeighbours(true);
                    }
                    final IsoGridSquare gridSquare9 = ServerMap.instance.getGridSquare(n + n5, n4 - 1, l);
                    if (gridSquare9 != null) {
                        gridSquare9.RecalcAllWithNeighbours(true);
                    }
                }
                for (int n6 = 0; n6 < 50; ++n6) {
                    final IsoGridSquare gridSquare10 = ServerMap.instance.getGridSquare(n + 0, n2 + n6, l);
                    if (gridSquare10 != null) {
                        gridSquare10.RecalcAllWithNeighbours(true);
                    }
                    final IsoGridSquare gridSquare11 = ServerMap.instance.getGridSquare(n3 - 1, n2 + n6, l);
                    if (gridSquare11 != null) {
                        gridSquare11.RecalcAllWithNeighbours(true);
                    }
                }
            }
            final int n7 = 100;
            for (int n8 = 0; n8 < 5; ++n8) {
                for (int n9 = 0; n9 < 5; ++n9) {
                    final IsoChunk isoChunk = this.chunks[n8][n9];
                    if (isoChunk != null) {
                        isoChunk.bLoaded = true;
                        for (int n10 = 0; n10 < n7; ++n10) {
                            for (int n11 = 0; n11 <= isoChunk.maxLevel; ++n11) {
                                final IsoGridSquare isoGridSquare = isoChunk.squares[n11][n10];
                                if (isoGridSquare != null) {
                                    if (isoGridSquare.getRoom() != null && !isoGridSquare.getRoom().def.bExplored) {
                                        this.UnexploredRooms.add(isoGridSquare.getRoom().def);
                                    }
                                    isoGridSquare.propertiesDirty = true;
                                }
                            }
                        }
                    }
                }
            }
            WorldSimulation.instance.createServerCell(this);
            for (int n12 = 0; n12 < 5; ++n12) {
                for (int n13 = 0; n13 < 5; ++n13) {
                    if (this.chunks[n12][n13] != null) {
                        this.chunks[n12][n13].doLoadGridsquare();
                    }
                }
            }
            for (final RoomDef roomDef3 : this.UnexploredRooms) {
                final RoomDef roomDef2 = roomDef3;
                ++roomDef3.IndoorZombies;
                if (roomDef2.IndoorZombies == 1) {
                    try {
                        VirtualZombieManager.instance.tryAddIndoorZombies(roomDef2, false);
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            this.bLoaded = true;
        }
        
        public void Unload() {
            if (!this.bLoaded) {
                return;
            }
            if (ServerMap.MapLoading) {
                DebugLog.log(DebugType.MapLoading, invokedynamic(makeConcatWithConstants:(IIII)Ljava/lang/String;, this.WX, this.WY, ServerMap.instance.toWorldCellX(this.WX), ServerMap.instance.toWorldCellX(this.WY)));
            }
            for (int i = 0; i < 5; ++i) {
                for (int j = 0; j < 5; ++j) {
                    if (this.chunks[i][j] != null) {
                        this.chunks[i][j].removeFromWorld();
                        ServerCell.chunkLoader.addSaveUnloadedJob(this.chunks[i][j]);
                        this.chunks[i][j] = null;
                    }
                }
            }
            for (final RoomDef roomDef : this.UnexploredRooms) {
                if (roomDef.IndoorZombies == 1) {}
                final RoomDef roomDef2 = roomDef;
                --roomDef2.IndoorZombies;
            }
            WorldSimulation.instance.removeServerCell(this);
        }
        
        public void Save() {
            if (!this.bLoaded) {
                return;
            }
            for (int i = 0; i < 5; ++i) {
                for (int j = 0; j < 5; ++j) {
                    if (this.chunks[i][j] != null) {
                        try {
                            ServerCell.chunkLoader.addSaveLoadedJob(this.chunks[i][j]);
                        }
                        catch (Exception ex) {
                            ex.printStackTrace();
                            LoggerManager.getLogger("map").write(ex);
                        }
                    }
                }
            }
            ServerCell.chunkLoader.updateSaved();
        }
        
        public void update() {
            for (int i = 0; i < 5; ++i) {
                for (int j = 0; j < 5; ++j) {
                    final IsoChunk isoChunk = this.chunks[i][j];
                    if (isoChunk != null) {
                        isoChunk.update();
                    }
                }
            }
            this.bPhysicsCheck = false;
        }
        
        static {
            ServerCell.chunkLoader = new ServerChunkLoader();
            ServerCell.loaded = new ArrayList<ServerCell>();
            ServerCell.loaded2 = new ArrayList<ServerCell>();
        }
    }
    
    private static class DistToCellComparator implements Comparator<ServerCell>
    {
        private Vector2[] pos;
        private int posCount;
        
        public DistToCellComparator() {
            this.pos = new Vector2[1024];
            for (int i = 0; i < this.pos.length; ++i) {
                this.pos[i] = new Vector2();
            }
        }
        
        public void init() {
            this.posCount = 0;
            for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
                if (udpConnection.isFullyConnected()) {
                    for (int j = 0; j < 4; ++j) {
                        if (udpConnection.players[j] != null) {
                            this.pos[this.posCount].set(udpConnection.players[j].x, udpConnection.players[j].y);
                            ++this.posCount;
                        }
                    }
                }
            }
        }
        
        @Override
        public int compare(final ServerCell serverCell, final ServerCell serverCell2) {
            float min = Float.MAX_VALUE;
            float min2 = Float.MAX_VALUE;
            for (int i = 0; i < this.posCount; ++i) {
                final float x = this.pos[i].x;
                final float y = this.pos[i].y;
                min = Math.min(min, this.distToCell(x, y, serverCell));
                min2 = Math.min(min2, this.distToCell(x, y, serverCell2));
            }
            if (min < min2) {
                return -1;
            }
            if (min > min2) {
                return 1;
            }
            return 0;
        }
        
        private float distToCell(final float n, final float n2, final ServerCell serverCell) {
            final int n3 = serverCell.WX * 50;
            final int n4 = serverCell.WY * 50;
            final int n5 = n3 + 50;
            final int n6 = n4 + 50;
            float n7 = n;
            float n8 = n2;
            if (n < n3) {
                n7 = (float)n3;
            }
            else if (n > n5) {
                n7 = (float)n5;
            }
            if (n2 < n4) {
                n8 = (float)n4;
            }
            else if (n2 > n6) {
                n8 = (float)n6;
            }
            return IsoUtils.DistanceToSquared(n, n2, n7, n8);
        }
    }
}

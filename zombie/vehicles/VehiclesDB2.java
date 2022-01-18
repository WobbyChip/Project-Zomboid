// 
// Decompiled by Procyon v0.5.36
// 

package zombie.vehicles;

import zombie.util.Pool;
import zombie.util.PooledObject;
import zombie.characters.IsoPlayer;
import zombie.network.GameServer;
import zombie.network.GameClient;
import zombie.iso.IsoGridSquare;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.ArrayList;
import gnu.trove.map.hash.TIntObjectHashMap;
import zombie.util.PZSQLUtils;
import java.io.File;
import zombie.ZomboidFileSystem;
import zombie.util.ByteBufferBackedInputStream;
import zombie.iso.IsoObject;
import java.sql.SQLException;
import zombie.core.Core;
import java.sql.Connection;
import gnu.trove.set.hash.TIntHashSet;
import java.io.InputStream;
import zombie.util.ByteBufferOutputStream;
import java.nio.BufferOverflowException;
import zombie.debug.DebugLog;
import zombie.iso.IsoWorld;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import zombie.util.Type;
import zombie.core.logger.ExceptionLogger;
import zombie.iso.IsoChunk;
import zombie.popman.ZombiePopulationRenderer;
import java.io.IOException;
import zombie.iso.WorldStreamer;
import java.nio.ByteBuffer;

public final class VehiclesDB2
{
    public static final int INVALID_ID = -1;
    private static final int MIN_ID = 1;
    public static final VehiclesDB2 instance;
    private static final ThreadLocal<ByteBuffer> TL_SliceBuffer;
    private static final ThreadLocal<byte[]> TL_Bytes;
    private final MainThread m_main;
    private final WorldStreamerThread m_worldStreamer;
    
    public VehiclesDB2() {
        this.m_main = new MainThread();
        this.m_worldStreamer = new WorldStreamerThread();
    }
    
    public void init() {
        this.m_worldStreamer.m_store.init(this.m_main.m_usedIDs, this.m_main.m_seenChunks);
    }
    
    public void Reset() {
        assert WorldStreamer.instance.worldStreamer == null;
        this.updateWorldStreamer();
        for (QueueItem queueItem = this.m_main.m_queue.poll(); queueItem != null; queueItem = this.m_main.m_queue.poll()) {
            queueItem.release();
        }
        this.m_main.Reset();
        this.m_worldStreamer.Reset();
    }
    
    public void updateMain() throws IOException {
        this.m_main.update();
    }
    
    public void updateWorldStreamer() {
        this.m_worldStreamer.update();
    }
    
    public void setForceSave() {
        this.m_main.m_forceSave = true;
    }
    
    public void renderDebug(final ZombiePopulationRenderer zombiePopulationRenderer) {
    }
    
    public void setChunkSeen(final int n, final int n2) {
        this.m_main.setChunkSeen(n, n2);
    }
    
    public boolean isChunkSeen(final int n, final int n2) {
        return this.m_main.isChunkSeen(n, n2);
    }
    
    public void setVehicleLoaded(final BaseVehicle vehicleLoaded) {
        this.m_main.setVehicleLoaded(vehicleLoaded);
    }
    
    public void setVehicleUnloaded(final BaseVehicle vehicleUnloaded) {
        this.m_main.setVehicleUnloaded(vehicleUnloaded);
    }
    
    public boolean isVehicleLoaded(final BaseVehicle baseVehicle) {
        return this.m_main.m_loadedIDs.contains(baseVehicle.sqlID);
    }
    
    public void loadChunk(final IsoChunk isoChunk) throws IOException {
        this.m_worldStreamer.loadChunk(isoChunk);
    }
    
    public void unloadChunk(final IsoChunk isoChunk) {
        if (Thread.currentThread() != WorldStreamer.instance.worldStreamer) {}
        this.m_worldStreamer.unloadChunk(isoChunk);
    }
    
    public void addVehicle(final BaseVehicle baseVehicle) {
        try {
            this.m_main.addVehicle(baseVehicle);
        }
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
        }
    }
    
    public void removeVehicle(final BaseVehicle baseVehicle) {
        this.m_main.removeVehicle(baseVehicle);
    }
    
    public void updateVehicle(final BaseVehicle baseVehicle) {
        try {
            this.m_main.updateVehicle(baseVehicle);
        }
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
        }
    }
    
    public void updateVehicleAndTrailer(final BaseVehicle baseVehicle) {
        if (baseVehicle == null) {
            return;
        }
        this.updateVehicle(baseVehicle);
        final BaseVehicle vehicleTowing = baseVehicle.getVehicleTowing();
        if (vehicleTowing != null) {
            this.updateVehicle(vehicleTowing);
        }
    }
    
    public void importPlayersFromOldDB(final IImportPlayerFromOldDB importPlayerFromOldDB) {
        final SQLStore sqlStore = Type.tryCastTo(this.m_worldStreamer.m_store, SQLStore.class);
        if (sqlStore == null || sqlStore.m_conn == null) {
            return;
        }
        try {
            final ResultSet tables = sqlStore.m_conn.getMetaData().getTables(null, null, "localPlayers", null);
            try {
                if (!tables.next()) {
                    if (tables != null) {
                        tables.close();
                    }
                    return;
                }
                if (tables != null) {
                    tables.close();
                }
            }
            catch (Throwable t) {
                if (tables != null) {
                    try {
                        tables.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                }
                throw t;
            }
        }
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
            return;
        }
        final String s = "SELECT id, name, wx, wy, x, y, z, worldversion, data, isDead FROM localPlayers";
        try {
            final PreparedStatement prepareStatement = sqlStore.m_conn.prepareStatement(s);
            try {
                final ResultSet executeQuery = prepareStatement.executeQuery();
                while (executeQuery.next()) {
                    importPlayerFromOldDB.accept(executeQuery.getInt(1), executeQuery.getString(2), executeQuery.getInt(3), executeQuery.getInt(4), executeQuery.getFloat(5), executeQuery.getFloat(6), executeQuery.getFloat(7), executeQuery.getInt(8), executeQuery.getBytes(9), executeQuery.getBoolean(10));
                }
                if (prepareStatement != null) {
                    prepareStatement.close();
                }
            }
            catch (Throwable t2) {
                if (prepareStatement != null) {
                    try {
                        prepareStatement.close();
                    }
                    catch (Throwable exception2) {
                        t2.addSuppressed(exception2);
                    }
                }
                throw t2;
            }
        }
        catch (Exception ex2) {
            ExceptionLogger.logException(ex2);
        }
        try {
            final Statement statement = sqlStore.m_conn.createStatement();
            statement.executeUpdate("DROP TABLE localPlayers");
            statement.executeUpdate("DROP TABLE networkPlayers");
            sqlStore.m_conn.commit();
        }
        catch (Exception ex3) {
            ExceptionLogger.logException(ex3);
        }
    }
    
    static {
        instance = new VehiclesDB2();
        TL_SliceBuffer = ThreadLocal.withInitial(() -> ByteBuffer.allocate(32768));
        TL_Bytes = ThreadLocal.withInitial(() -> new byte[1024]);
    }
    
    private static final class VehicleBuffer
    {
        int m_id;
        int m_wx;
        int m_wy;
        float m_x;
        float m_y;
        int m_WorldVersion;
        ByteBuffer m_bb;
        
        private VehicleBuffer() {
            this.m_id = -1;
            this.m_bb = ByteBuffer.allocate(32768);
        }
        
        void set(final BaseVehicle baseVehicle) throws IOException {
            assert baseVehicle.sqlID >= 1;
            synchronized (VehiclesDB2.instance.m_main.m_usedIDs) {
                assert VehiclesDB2.instance.m_main.m_usedIDs.contains(baseVehicle.sqlID);
            }
            this.m_id = baseVehicle.sqlID;
            this.m_wx = baseVehicle.chunk.wx;
            this.m_wy = baseVehicle.chunk.wy;
            this.m_x = baseVehicle.getX();
            this.m_y = baseVehicle.getY();
            this.m_WorldVersion = IsoWorld.getWorldVersion();
            ByteBuffer allocate = VehiclesDB2.TL_SliceBuffer.get();
            allocate.clear();
            while (true) {
                try {
                    baseVehicle.save(allocate);
                }
                catch (BufferOverflowException ex) {
                    if (allocate.capacity() >= 2097152) {
                        DebugLog.General.error("the vehicle %d cannot be saved", baseVehicle.sqlID);
                        throw ex;
                    }
                    allocate = ByteBuffer.allocate(allocate.capacity() + 32768);
                    VehiclesDB2.TL_SliceBuffer.set(allocate);
                    continue;
                }
                break;
            }
            allocate.flip();
            this.setBytes(allocate);
        }
        
        void setBytes(final ByteBuffer byteBuffer) {
            byteBuffer.rewind();
            final ByteBufferOutputStream byteBufferOutputStream = new ByteBufferOutputStream(this.m_bb, true);
            byteBufferOutputStream.clear();
            final byte[] dst = VehiclesDB2.TL_Bytes.get();
            int min;
            for (int i = byteBuffer.limit(); i > 0; i -= min) {
                min = Math.min(dst.length, i);
                byteBuffer.get(dst, 0, min);
                byteBufferOutputStream.write(dst, 0, min);
            }
            byteBufferOutputStream.flip();
            this.m_bb = byteBufferOutputStream.getWrappedBuffer();
        }
        
        void setBytes(final byte[] array) {
            final ByteBufferOutputStream byteBufferOutputStream = new ByteBufferOutputStream(this.m_bb, true);
            byteBufferOutputStream.clear();
            byteBufferOutputStream.write(array);
            byteBufferOutputStream.flip();
            this.m_bb = byteBufferOutputStream.getWrappedBuffer();
        }
        
        void setBytes(final InputStream inputStream) throws IOException {
            final ByteBufferOutputStream byteBufferOutputStream = new ByteBufferOutputStream(this.m_bb, true);
            byteBufferOutputStream.clear();
            final byte[] b = VehiclesDB2.TL_Bytes.get();
            while (true) {
                final int read = inputStream.read(b);
                if (read < 1) {
                    break;
                }
                byteBufferOutputStream.write(b, 0, read);
            }
            byteBufferOutputStream.flip();
            this.m_bb = byteBufferOutputStream.getWrappedBuffer();
        }
    }
    
    private abstract static class IVehicleStore
    {
        abstract void init(final TIntHashSet p0, final TIntHashSet p1);
        
        abstract void Reset();
        
        abstract void loadChunk(final IsoChunk p0, final ThrowingBiConsumer<IsoChunk, VehicleBuffer, IOException> p1) throws IOException;
        
        abstract void updateVehicle(final VehicleBuffer p0);
        
        abstract void removeVehicle(final int p0);
    }
    
    private static final class SQLStore extends IVehicleStore
    {
        Connection m_conn;
        final VehicleBuffer m_vehicleBuffer;
        
        private SQLStore() {
            this.m_conn = null;
            this.m_vehicleBuffer = new VehicleBuffer();
        }
        
        @Override
        void init(final TIntHashSet set, final TIntHashSet set2) {
            set.clear();
            set2.clear();
            if (Core.getInstance().isNoSave()) {
                return;
            }
            this.create();
            try {
                this.initUsedIDs(set, set2);
            }
            catch (SQLException ex) {
                ExceptionLogger.logException(ex);
            }
        }
        
        @Override
        void Reset() {
            if (this.m_conn == null) {
                return;
            }
            try {
                this.m_conn.close();
            }
            catch (SQLException ex) {
                ExceptionLogger.logException(ex);
            }
            this.m_conn = null;
        }
        
        @Override
        void loadChunk(final IsoChunk isoChunk, final ThrowingBiConsumer<IsoChunk, VehicleBuffer, IOException> throwingBiConsumer) throws IOException {
            if (this.m_conn == null || isoChunk == null) {
                return;
            }
            final String s = "SELECT id, x, y, data, worldversion FROM vehicles WHERE wx=? AND wy=?";
            try {
                final PreparedStatement prepareStatement = this.m_conn.prepareStatement(s);
                try {
                    prepareStatement.setInt(1, isoChunk.wx);
                    prepareStatement.setInt(2, isoChunk.wy);
                    final ResultSet executeQuery = prepareStatement.executeQuery();
                    while (executeQuery.next()) {
                        this.m_vehicleBuffer.m_id = executeQuery.getInt(1);
                        this.m_vehicleBuffer.m_wx = isoChunk.wx;
                        this.m_vehicleBuffer.m_wy = isoChunk.wy;
                        this.m_vehicleBuffer.m_x = executeQuery.getFloat(2);
                        this.m_vehicleBuffer.m_y = executeQuery.getFloat(3);
                        this.m_vehicleBuffer.setBytes(executeQuery.getBinaryStream(4));
                        this.m_vehicleBuffer.m_WorldVersion = executeQuery.getInt(5);
                        final boolean b = this.m_vehicleBuffer.m_bb.get() != 0;
                        if (this.m_vehicleBuffer.m_bb.get() == IsoObject.getFactoryVehicle().getClassID()) {
                            if (!b) {
                                continue;
                            }
                            throwingBiConsumer.accept(isoChunk, this.m_vehicleBuffer);
                        }
                    }
                    if (prepareStatement != null) {
                        prepareStatement.close();
                    }
                }
                catch (Throwable t) {
                    if (prepareStatement != null) {
                        try {
                            prepareStatement.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                    }
                    throw t;
                }
            }
            catch (Exception ex) {
                ExceptionLogger.logException(ex);
            }
        }
        
        @Override
        void updateVehicle(final VehicleBuffer vehicleBuffer) {
            if (this.m_conn == null) {
                return;
            }
            assert vehicleBuffer.m_id >= 1;
            synchronized (VehiclesDB2.instance.m_main.m_usedIDs) {
                assert VehiclesDB2.instance.m_main.m_usedIDs.contains(vehicleBuffer.m_id);
            }
            try {
                if (this.isInDB(vehicleBuffer.m_id)) {
                    this.updateDB(vehicleBuffer);
                }
                else {
                    this.addToDB(vehicleBuffer);
                }
            }
            catch (Exception ex) {
                ExceptionLogger.logException(ex);
                this.rollback();
            }
        }
        
        boolean isInDB(final int n) throws SQLException {
            final PreparedStatement prepareStatement = this.m_conn.prepareStatement("SELECT 1 FROM vehicles WHERE id=?");
            try {
                prepareStatement.setInt(1, n);
                final boolean b = prepareStatement.executeQuery().next();
                if (prepareStatement != null) {
                    prepareStatement.close();
                }
                return b;
            }
            catch (Throwable t) {
                if (prepareStatement != null) {
                    try {
                        prepareStatement.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                }
                throw t;
            }
        }
        
        void addToDB(final VehicleBuffer vehicleBuffer) throws SQLException {
            final String s = "INSERT INTO vehicles(wx,wy,x,y,worldversion,data,id) VALUES(?,?,?,?,?,?,?)";
            try {
                final PreparedStatement prepareStatement = this.m_conn.prepareStatement(s);
                try {
                    prepareStatement.setInt(1, vehicleBuffer.m_wx);
                    prepareStatement.setInt(2, vehicleBuffer.m_wy);
                    prepareStatement.setFloat(3, vehicleBuffer.m_x);
                    prepareStatement.setFloat(4, vehicleBuffer.m_y);
                    prepareStatement.setInt(5, vehicleBuffer.m_WorldVersion);
                    final ByteBuffer bb = vehicleBuffer.m_bb;
                    bb.rewind();
                    prepareStatement.setBinaryStream(6, new ByteBufferBackedInputStream(bb), bb.remaining());
                    prepareStatement.setInt(7, vehicleBuffer.m_id);
                    prepareStatement.executeUpdate();
                    this.m_conn.commit();
                    if (prepareStatement != null) {
                        prepareStatement.close();
                    }
                }
                catch (Throwable t) {
                    if (prepareStatement != null) {
                        try {
                            prepareStatement.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                    }
                    throw t;
                }
            }
            catch (Exception ex) {
                this.rollback();
                throw ex;
            }
        }
        
        void updateDB(final VehicleBuffer vehicleBuffer) throws SQLException {
            final String s = "UPDATE vehicles SET wx = ?, wy = ?, x = ?, y = ?, worldversion = ?, data = ? WHERE id=?";
            try {
                final PreparedStatement prepareStatement = this.m_conn.prepareStatement(s);
                try {
                    prepareStatement.setInt(1, vehicleBuffer.m_wx);
                    prepareStatement.setInt(2, vehicleBuffer.m_wy);
                    prepareStatement.setFloat(3, vehicleBuffer.m_x);
                    prepareStatement.setFloat(4, vehicleBuffer.m_y);
                    prepareStatement.setInt(5, vehicleBuffer.m_WorldVersion);
                    final ByteBuffer bb = vehicleBuffer.m_bb;
                    bb.rewind();
                    prepareStatement.setBinaryStream(6, new ByteBufferBackedInputStream(bb), bb.remaining());
                    prepareStatement.setInt(7, vehicleBuffer.m_id);
                    prepareStatement.executeUpdate();
                    this.m_conn.commit();
                    if (prepareStatement != null) {
                        prepareStatement.close();
                    }
                }
                catch (Throwable t) {
                    if (prepareStatement != null) {
                        try {
                            prepareStatement.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                    }
                    throw t;
                }
            }
            catch (Exception ex) {
                this.rollback();
                throw ex;
            }
        }
        
        @Override
        void removeVehicle(final int n) {
            if (this.m_conn == null || n < 1) {
                return;
            }
            final String s = "DELETE FROM vehicles WHERE id=?";
            try {
                final PreparedStatement prepareStatement = this.m_conn.prepareStatement(s);
                try {
                    prepareStatement.setInt(1, n);
                    prepareStatement.executeUpdate();
                    this.m_conn.commit();
                    if (prepareStatement != null) {
                        prepareStatement.close();
                    }
                }
                catch (Throwable t) {
                    if (prepareStatement != null) {
                        try {
                            prepareStatement.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                    }
                    throw t;
                }
            }
            catch (Exception ex) {
                ExceptionLogger.logException(ex);
                this.rollback();
            }
        }
        
        void create() {
            final String pathname = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getGameModeCacheDir(), Core.GameSaveWorld);
            final File file = new File(pathname);
            if (!file.exists()) {
                file.mkdirs();
            }
            final File file2 = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, pathname, File.separator));
            file2.setReadable(true, false);
            file2.setExecutable(true, false);
            file2.setWritable(true, false);
            if (!file2.exists()) {
                try {
                    file2.createNewFile();
                    this.m_conn = PZSQLUtils.getConnection(file2.getAbsolutePath());
                    final Statement statement = this.m_conn.createStatement();
                    statement.executeUpdate("CREATE TABLE vehicles (id   INTEGER PRIMARY KEY NOT NULL,wx    INTEGER,wy    INTEGER,x    FLOAT,y    FLOAT,worldversion    INTEGER,data BLOB);");
                    statement.executeUpdate("CREATE INDEX ivwx ON vehicles (wx);");
                    statement.executeUpdate("CREATE INDEX ivwy ON vehicles (wy);");
                    statement.close();
                }
                catch (Exception ex) {
                    ExceptionLogger.logException(ex);
                    DebugLog.log("failed to create vehicles database");
                    System.exit(1);
                }
            }
            if (this.m_conn == null) {
                try {
                    this.m_conn = PZSQLUtils.getConnection(file2.getAbsolutePath());
                }
                catch (Exception ex4) {
                    DebugLog.log("failed to create vehicles database");
                    System.exit(1);
                }
            }
            try {
                final Statement statement2 = this.m_conn.createStatement();
                statement2.executeQuery("PRAGMA JOURNAL_MODE=TRUNCATE;");
                statement2.close();
            }
            catch (Exception ex2) {
                ExceptionLogger.logException(ex2);
                System.exit(1);
            }
            try {
                this.m_conn.setAutoCommit(false);
            }
            catch (SQLException ex3) {
                ExceptionLogger.logException(ex3);
            }
        }
        
        private String searchPathForSqliteLib(final String child) {
            for (final String parent : System.getProperty("java.library.path", "").split(File.pathSeparator)) {
                if (new File(parent, child).exists()) {
                    return parent;
                }
            }
            return "";
        }
        
        void initUsedIDs(final TIntHashSet set, final TIntHashSet set2) throws SQLException {
            final PreparedStatement prepareStatement = this.m_conn.prepareStatement("SELECT wx,wy,id FROM vehicles");
            try {
                final ResultSet executeQuery = prepareStatement.executeQuery();
                while (executeQuery.next()) {
                    set2.add(executeQuery.getInt(2) << 16 | executeQuery.getInt(1));
                    set.add(executeQuery.getInt(3));
                }
                if (prepareStatement != null) {
                    prepareStatement.close();
                }
            }
            catch (Throwable t) {
                if (prepareStatement != null) {
                    try {
                        prepareStatement.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                }
                throw t;
            }
        }
        
        private void rollback() {
            if (this.m_conn == null) {
                return;
            }
            try {
                this.m_conn.rollback();
            }
            catch (SQLException ex) {
                ExceptionLogger.logException(ex);
            }
        }
    }
    
    private static class MemoryStore extends IVehicleStore
    {
        final TIntObjectHashMap<VehicleBuffer> m_IDToVehicle;
        final TIntObjectHashMap<ArrayList<VehicleBuffer>> m_ChunkToVehicles;
        
        private MemoryStore() {
            this.m_IDToVehicle = (TIntObjectHashMap<VehicleBuffer>)new TIntObjectHashMap();
            this.m_ChunkToVehicles = (TIntObjectHashMap<ArrayList<VehicleBuffer>>)new TIntObjectHashMap();
        }
        
        @Override
        void init(final TIntHashSet set, final TIntHashSet set2) {
            set.clear();
            set2.clear();
        }
        
        @Override
        void Reset() {
            this.m_IDToVehicle.clear();
            this.m_ChunkToVehicles.clear();
        }
        
        @Override
        void loadChunk(final IsoChunk isoChunk, final ThrowingBiConsumer<IsoChunk, VehicleBuffer, IOException> throwingBiConsumer) throws IOException {
            final ArrayList list = (ArrayList)this.m_ChunkToVehicles.get(isoChunk.wy << 16 | isoChunk.wx);
            if (list == null) {
                return;
            }
            for (int i = 0; i < list.size(); ++i) {
                final VehicleBuffer vehicleBuffer = list.get(i);
                vehicleBuffer.m_bb.rewind();
                final boolean b = vehicleBuffer.m_bb.get() == 1;
                vehicleBuffer.m_bb.getInt();
                throwingBiConsumer.accept(isoChunk, vehicleBuffer);
            }
        }
        
        @Override
        void updateVehicle(final VehicleBuffer vehicleBuffer) {
            assert vehicleBuffer.m_id >= 1;
            synchronized (VehiclesDB2.instance.m_main.m_usedIDs) {
                assert VehiclesDB2.instance.m_main.m_usedIDs.contains(vehicleBuffer.m_id);
            }
            vehicleBuffer.m_bb.rewind();
            VehicleBuffer vehicleBuffer2 = (VehicleBuffer)this.m_IDToVehicle.get(vehicleBuffer.m_id);
            if (vehicleBuffer2 == null) {
                vehicleBuffer2 = new VehicleBuffer();
                vehicleBuffer2.m_id = vehicleBuffer.m_id;
                this.m_IDToVehicle.put(vehicleBuffer.m_id, (Object)vehicleBuffer2);
            }
            else {
                ((ArrayList)this.m_ChunkToVehicles.get(vehicleBuffer2.m_wy << 16 | vehicleBuffer2.m_wx)).remove(vehicleBuffer2);
            }
            vehicleBuffer2.m_wx = vehicleBuffer.m_wx;
            vehicleBuffer2.m_wy = vehicleBuffer.m_wy;
            vehicleBuffer2.m_x = vehicleBuffer.m_x;
            vehicleBuffer2.m_y = vehicleBuffer.m_y;
            vehicleBuffer2.m_WorldVersion = vehicleBuffer.m_WorldVersion;
            vehicleBuffer2.setBytes(vehicleBuffer.m_bb);
            final int n = vehicleBuffer2.m_wy << 16 | vehicleBuffer2.m_wx;
            if (this.m_ChunkToVehicles.get(n) == null) {
                this.m_ChunkToVehicles.put(n, (Object)new ArrayList());
            }
            ((ArrayList)this.m_ChunkToVehicles.get(n)).add(vehicleBuffer2);
        }
        
        @Override
        void removeVehicle(final int n) {
            final VehicleBuffer o = (VehicleBuffer)this.m_IDToVehicle.remove(n);
            if (o == null) {
                return;
            }
            ((ArrayList)this.m_ChunkToVehicles.get(o.m_wy << 16 | o.m_wx)).remove(o);
        }
    }
    
    private static final class WorldStreamerThread
    {
        final IVehicleStore m_store;
        final ConcurrentLinkedQueue<QueueItem> m_queue;
        final VehicleBuffer m_vehicleBuffer;
        
        private WorldStreamerThread() {
            this.m_store = new SQLStore();
            this.m_queue = new ConcurrentLinkedQueue<QueueItem>();
            this.m_vehicleBuffer = new VehicleBuffer();
        }
        
        void Reset() {
            this.m_store.Reset();
            assert this.m_queue.isEmpty();
            this.m_queue.clear();
        }
        
        void update() {
            for (QueueItem e = this.m_queue.poll(); e != null; e = this.m_queue.poll()) {
                try {
                    e.processWorldStreamer();
                }
                finally {
                    VehiclesDB2.instance.m_main.m_queue.add(e);
                }
            }
        }
        
        void loadChunk(final IsoChunk isoChunk) throws IOException {
            this.m_store.loadChunk(isoChunk, this::vehicleLoaded);
        }
        
        void vehicleLoaded(final IsoChunk chunk, final VehicleBuffer vehicleBuffer) throws IOException {
            assert vehicleBuffer.m_id >= 1;
            final IsoGridSquare gridSquare = chunk.getGridSquare((int)(vehicleBuffer.m_x - chunk.wx * 10), (int)(vehicleBuffer.m_y - chunk.wy * 10), 0);
            final BaseVehicle e = new BaseVehicle(IsoWorld.instance.CurrentCell);
            e.setSquare(gridSquare);
            e.setCurrent(gridSquare);
            try {
                e.load(vehicleBuffer.m_bb, vehicleBuffer.m_WorldVersion);
            }
            catch (Exception ex) {
                ExceptionLogger.logException(ex);
                DebugLog.General.error("vehicle %d is being deleted because an error occurred loading it", vehicleBuffer.m_id);
                this.m_store.removeVehicle(vehicleBuffer.m_id);
                return;
            }
            e.sqlID = vehicleBuffer.m_id;
            e.chunk = chunk;
            if (chunk.jobType == IsoChunk.JobType.SoftReset) {
                e.softReset();
            }
            chunk.vehicles.add(e);
        }
        
        void unloadChunk(final IsoChunk isoChunk) {
            for (int i = 0; i < isoChunk.vehicles.size(); ++i) {
                try {
                    this.m_vehicleBuffer.set(isoChunk.vehicles.get(i));
                    this.m_store.updateVehicle(this.m_vehicleBuffer);
                }
                catch (Exception ex) {
                    ExceptionLogger.logException(ex);
                }
            }
        }
    }
    
    private static final class MainThread
    {
        final TIntHashSet m_seenChunks;
        final TIntHashSet m_usedIDs;
        final TIntHashSet m_loadedIDs;
        boolean m_forceSave;
        final ConcurrentLinkedQueue<QueueItem> m_queue;
        
        MainThread() {
            this.m_seenChunks = new TIntHashSet();
            this.m_usedIDs = new TIntHashSet();
            this.m_loadedIDs = new TIntHashSet();
            this.m_forceSave = false;
            this.m_queue = new ConcurrentLinkedQueue<QueueItem>();
            this.m_seenChunks.setAutoCompactionFactor(0.0f);
            this.m_usedIDs.setAutoCompactionFactor(0.0f);
            this.m_loadedIDs.setAutoCompactionFactor(0.0f);
        }
        
        void Reset() {
            this.m_seenChunks.clear();
            this.m_usedIDs.clear();
            this.m_loadedIDs.clear();
            assert this.m_queue.isEmpty();
            this.m_queue.clear();
            this.m_forceSave = false;
        }
        
        void update() throws IOException {
            if (!GameClient.bClient && !GameServer.bServer && this.m_forceSave) {
                this.m_forceSave = false;
                for (int i = 0; i < 4; ++i) {
                    final IsoPlayer isoPlayer = IsoPlayer.players[i];
                    if (isoPlayer != null && isoPlayer.getVehicle() != null) {
                        if (isoPlayer.getVehicle().isEngineRunning()) {
                            this.updateVehicle(isoPlayer.getVehicle());
                            final BaseVehicle vehicleTowing = isoPlayer.getVehicle().getVehicleTowing();
                            if (vehicleTowing != null) {
                                this.updateVehicle(vehicleTowing);
                            }
                        }
                    }
                }
            }
            for (QueueItem queueItem = this.m_queue.poll(); queueItem != null; queueItem = this.m_queue.poll()) {
                try {
                    queueItem.processMain();
                }
                finally {
                    queueItem.release();
                }
            }
        }
        
        void setChunkSeen(final int n, final int n2) {
            this.m_seenChunks.add(n2 << 16 | n);
        }
        
        boolean isChunkSeen(final int n, final int n2) {
            return this.m_seenChunks.contains(n2 << 16 | n);
        }
        
        int allocateID() {
            synchronized (this.m_usedIDs) {
                for (int i = 1; i < Integer.MAX_VALUE; ++i) {
                    if (!this.m_usedIDs.contains(i)) {
                        this.m_usedIDs.add(i);
                        return i;
                    }
                }
            }
            throw new RuntimeException("ran out of unused vehicle ids");
        }
        
        void setVehicleLoaded(final BaseVehicle baseVehicle) {
            if (baseVehicle.sqlID == -1) {
                baseVehicle.sqlID = this.allocateID();
            }
            assert !this.m_loadedIDs.contains(baseVehicle.sqlID);
            this.m_loadedIDs.add(baseVehicle.sqlID);
        }
        
        void setVehicleUnloaded(final BaseVehicle baseVehicle) {
            if (baseVehicle.sqlID == -1) {
                return;
            }
            this.m_loadedIDs.remove(baseVehicle.sqlID);
        }
        
        void addVehicle(final BaseVehicle baseVehicle) throws IOException {
            if (baseVehicle.sqlID == -1) {
                baseVehicle.sqlID = this.allocateID();
            }
            final QueueAddVehicle e = QueueAddVehicle.s_pool.alloc();
            e.init(baseVehicle);
            VehiclesDB2.instance.m_worldStreamer.m_queue.add(e);
        }
        
        void removeVehicle(final BaseVehicle baseVehicle) {
            final QueueRemoveVehicle e = QueueRemoveVehicle.s_pool.alloc();
            e.init(baseVehicle);
            VehiclesDB2.instance.m_worldStreamer.m_queue.add(e);
        }
        
        void updateVehicle(final BaseVehicle baseVehicle) throws IOException {
            if (baseVehicle.sqlID == -1) {
                baseVehicle.sqlID = this.allocateID();
            }
            final QueueUpdateVehicle e = QueueUpdateVehicle.s_pool.alloc();
            e.init(baseVehicle);
            VehiclesDB2.instance.m_worldStreamer.m_queue.add(e);
        }
    }
    
    private abstract static class QueueItem extends PooledObject
    {
        abstract void processMain();
        
        abstract void processWorldStreamer();
    }
    
    private static final class QueueAddVehicle extends QueueItem
    {
        static final Pool<QueueAddVehicle> s_pool;
        final VehicleBuffer m_vehicleBuffer;
        
        private QueueAddVehicle() {
            this.m_vehicleBuffer = new VehicleBuffer();
        }
        
        void init(final BaseVehicle baseVehicle) throws IOException {
            this.m_vehicleBuffer.set(baseVehicle);
        }
        
        @Override
        void processMain() {
        }
        
        @Override
        void processWorldStreamer() {
            VehiclesDB2.instance.m_worldStreamer.m_store.updateVehicle(this.m_vehicleBuffer);
        }
        
        static {
            s_pool = new Pool<QueueAddVehicle>(QueueAddVehicle::new);
        }
    }
    
    private static class QueueRemoveVehicle extends QueueItem
    {
        static final Pool<QueueRemoveVehicle> s_pool;
        int m_id;
        
        void init(final BaseVehicle baseVehicle) {
            this.m_id = baseVehicle.sqlID;
        }
        
        @Override
        void processMain() {
        }
        
        @Override
        void processWorldStreamer() {
            VehiclesDB2.instance.m_worldStreamer.m_store.removeVehicle(this.m_id);
        }
        
        static {
            s_pool = new Pool<QueueRemoveVehicle>(QueueRemoveVehicle::new);
        }
    }
    
    private static final class QueueUpdateVehicle extends QueueItem
    {
        static final Pool<QueueUpdateVehicle> s_pool;
        final VehicleBuffer m_vehicleBuffer;
        
        private QueueUpdateVehicle() {
            this.m_vehicleBuffer = new VehicleBuffer();
        }
        
        void init(final BaseVehicle baseVehicle) throws IOException {
            this.m_vehicleBuffer.set(baseVehicle);
        }
        
        @Override
        void processMain() {
        }
        
        @Override
        void processWorldStreamer() {
            VehiclesDB2.instance.m_worldStreamer.m_store.updateVehicle(this.m_vehicleBuffer);
        }
        
        static {
            s_pool = new Pool<QueueUpdateVehicle>(QueueUpdateVehicle::new);
        }
    }
    
    @FunctionalInterface
    public interface ThrowingBiConsumer<T1, T2, E extends Exception>
    {
        void accept(final T1 p0, final T2 p1) throws E, Exception;
    }
    
    public interface IImportPlayerFromOldDB
    {
        void accept(final int p0, final String p1, final int p2, final int p3, final float p4, final float p5, final float p6, final int p7, final byte[] p8, final boolean p9);
    }
}

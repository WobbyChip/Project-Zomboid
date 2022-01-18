// 
// Decompiled by Procyon v0.5.36
// 

package zombie.savefile;

import zombie.util.ByteBufferBackedInputStream;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;
import java.io.InputStream;
import zombie.util.ByteBufferOutputStream;
import java.io.IOException;
import java.nio.BufferOverflowException;
import zombie.debug.DebugLog;
import zombie.iso.IsoChunkMap;
import java.util.ArrayList;
import zombie.iso.IsoCell;
import zombie.iso.IsoWorld;
import java.io.File;
import zombie.ZomboidFileSystem;
import zombie.characters.IsoPlayer;
import zombie.vehicles.VehiclesDB2;
import zombie.iso.WorldStreamer;
import zombie.core.logger.ExceptionLogger;
import zombie.core.Core;
import zombie.core.utils.UpdateLimit;
import java.util.concurrent.ConcurrentLinkedQueue;
import gnu.trove.set.hash.TIntHashSet;
import java.nio.ByteBuffer;

public final class PlayerDB
{
    public static final int INVALID_ID = -1;
    private static final int MIN_ID = 1;
    private static PlayerDB instance;
    private static final ThreadLocal<ByteBuffer> TL_SliceBuffer;
    private static final ThreadLocal<byte[]> TL_Bytes;
    private static boolean s_allow;
    private final IPlayerStore m_store;
    private final TIntHashSet m_usedIDs;
    private final ConcurrentLinkedQueue<PlayerData> m_toThread;
    private final ConcurrentLinkedQueue<PlayerData> m_fromThread;
    private boolean m_forceSavePlayers;
    public boolean m_canSavePlayers;
    private final UpdateLimit m_saveToDBPeriod;
    
    public static synchronized PlayerDB getInstance() {
        if (PlayerDB.instance == null && PlayerDB.s_allow) {
            PlayerDB.instance = new PlayerDB();
        }
        return PlayerDB.instance;
    }
    
    public static void setAllow(final boolean s_allow) {
        PlayerDB.s_allow = s_allow;
    }
    
    public static boolean isAllow() {
        return PlayerDB.s_allow;
    }
    
    public static boolean isAvailable() {
        return PlayerDB.instance != null;
    }
    
    public PlayerDB() {
        this.m_store = new SQLPlayerStore();
        this.m_usedIDs = new TIntHashSet();
        this.m_toThread = new ConcurrentLinkedQueue<PlayerData>();
        this.m_fromThread = new ConcurrentLinkedQueue<PlayerData>();
        this.m_canSavePlayers = false;
        this.m_saveToDBPeriod = new UpdateLimit(10000L);
        if (Core.getInstance().isNoSave()) {
            return;
        }
        this.create();
    }
    
    private void create() {
        try {
            this.m_store.init(this.m_usedIDs);
            this.m_usedIDs.add(1);
        }
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
        }
    }
    
    public void close() {
        assert WorldStreamer.instance.worldStreamer == null;
        this.updateWorldStreamer();
        assert this.m_toThread.isEmpty();
        try {
            this.m_store.Reset();
        }
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
        }
        this.m_fromThread.clear();
        PlayerDB.instance = null;
        PlayerDB.s_allow = false;
    }
    
    private int allocateID() {
        synchronized (this.m_usedIDs) {
            for (int i = 1; i < Integer.MAX_VALUE; ++i) {
                if (!this.m_usedIDs.contains(i)) {
                    this.m_usedIDs.add(i);
                    return i;
                }
            }
        }
        throw new RuntimeException("ran out of unused players.db ids");
    }
    
    private PlayerData allocPlayerData() {
        PlayerData playerData = this.m_fromThread.poll();
        if (playerData == null) {
            playerData = new PlayerData();
        }
        assert playerData.m_sqlID == -1;
        return playerData;
    }
    
    private void releasePlayerData(final PlayerData e) {
        e.m_sqlID = -1;
        this.m_fromThread.add(e);
    }
    
    public void updateMain() {
        if (this.m_canSavePlayers && (this.m_forceSavePlayers || this.m_saveToDBPeriod.Check())) {
            this.m_forceSavePlayers = false;
            this.savePlayersAsync();
            VehiclesDB2.instance.setForceSave();
        }
    }
    
    public void updateWorldStreamer() {
        for (PlayerData playerData = this.m_toThread.poll(); playerData != null; playerData = this.m_toThread.poll()) {
            try {
                this.m_store.save(playerData);
            }
            catch (Exception ex) {
                ExceptionLogger.logException(ex);
            }
            finally {
                this.releasePlayerData(playerData);
            }
        }
    }
    
    private void savePlayerAsync(final IsoPlayer isoPlayer) throws Exception {
        if (isoPlayer == null) {
            return;
        }
        if (isoPlayer.sqlID == -1) {
            isoPlayer.sqlID = this.allocateID();
        }
        final PlayerData allocPlayerData = this.allocPlayerData();
        try {
            allocPlayerData.set(isoPlayer);
            this.m_toThread.add(allocPlayerData);
        }
        catch (Exception ex) {
            this.releasePlayerData(allocPlayerData);
            throw ex;
        }
    }
    
    private void savePlayersAsync() {
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            final IsoPlayer isoPlayer = IsoPlayer.players[i];
            if (isoPlayer != null) {
                try {
                    this.savePlayerAsync(isoPlayer);
                }
                catch (Exception ex) {
                    ExceptionLogger.logException(ex);
                }
            }
        }
    }
    
    public void savePlayers() {
        if (!this.m_canSavePlayers) {
            return;
        }
        this.m_forceSavePlayers = true;
    }
    
    public void saveLocalPlayersForce() {
        this.savePlayersAsync();
        if (WorldStreamer.instance.worldStreamer == null) {
            this.updateWorldStreamer();
        }
    }
    
    public void importPlayersFromVehiclesDB() {
        final PlayerData playerData;
        VehiclesDB2.instance.importPlayersFromOldDB((p0, name, p2, p3, x, y, z, worldVersion, bytes, isDead) -> {
            this.allocPlayerData();
            playerData.m_sqlID = this.allocateID();
            playerData.m_x = x;
            playerData.m_y = y;
            playerData.m_z = z;
            playerData.m_isDead = isDead;
            playerData.m_name = name;
            playerData.m_WorldVersion = worldVersion;
            playerData.setBytes(bytes);
            try {
                this.m_store.save(playerData);
            }
            catch (Exception ex) {
                ExceptionLogger.logException(ex);
            }
            this.releasePlayerData(playerData);
        });
    }
    
    public void uploadLocalPlayers2DB() {
        this.savePlayersAsync();
        final String s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getGameModeCacheDir(), Core.GameSaveWorld);
        for (int i = 1; i < 100; ++i) {
            final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;I)Ljava/lang/String;, s, File.separator, i));
            if (file.exists()) {
                try {
                    final IsoPlayer isoPlayer = new IsoPlayer(IsoWorld.instance.CurrentCell);
                    isoPlayer.load(file.getAbsolutePath());
                    this.savePlayerAsync(isoPlayer);
                    file.delete();
                }
                catch (Exception ex) {
                    ExceptionLogger.logException(ex);
                }
            }
        }
        if (WorldStreamer.instance.worldStreamer == null) {
            this.updateWorldStreamer();
        }
    }
    
    private boolean loadPlayer(final int n, final IsoPlayer isoPlayer) {
        final PlayerData allocPlayerData = this.allocPlayerData();
        try {
            allocPlayerData.m_sqlID = n;
            if (!this.m_store.load(allocPlayerData)) {
                return false;
            }
            isoPlayer.load(allocPlayerData.m_byteBuffer, allocPlayerData.m_WorldVersion);
            if (allocPlayerData.m_isDead) {
                isoPlayer.getBodyDamage().setOverallBodyHealth(0.0f);
                isoPlayer.setHealth(0.0f);
            }
            isoPlayer.sqlID = n;
            return true;
        }
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
        }
        finally {
            this.releasePlayerData(allocPlayerData);
        }
        return false;
    }
    
    public boolean loadLocalPlayer(final int n) {
        try {
            IsoPlayer instance = IsoPlayer.getInstance();
            if (instance == null) {
                instance = new IsoPlayer(IsoCell.getInstance());
                IsoPlayer.setInstance(instance);
                IsoPlayer.players[0] = instance;
            }
            if (this.loadPlayer(n, instance)) {
                final int n2 = (int)(instance.x / 10.0f);
                final int n3 = (int)(instance.y / 10.0f);
                IsoCell.getInstance().ChunkMap[IsoPlayer.getPlayerIndex()].WorldX = n2 + IsoWorld.saveoffsetx * 30;
                IsoCell.getInstance().ChunkMap[IsoPlayer.getPlayerIndex()].WorldY = n3 + IsoWorld.saveoffsety * 30;
                return true;
            }
        }
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
        }
        return false;
    }
    
    public ArrayList<IsoPlayer> getAllLocalPlayers() {
        final ArrayList<IsoPlayer> list = new ArrayList<IsoPlayer>();
        this.m_usedIDs.forEach(n -> {
            if (n <= 1) {
                return true;
            }
            final IsoPlayer e = new IsoPlayer(IsoWorld.instance.CurrentCell);
            if (this.loadPlayer(n, e)) {
                list.add(e);
            }
            return true;
        });
        return list;
    }
    
    public boolean loadLocalPlayerInfo(final int sqlID) {
        final PlayerData allocPlayerData = this.allocPlayerData();
        try {
            allocPlayerData.m_sqlID = sqlID;
            if (this.m_store.loadEverythingExceptBytes(allocPlayerData)) {
                IsoChunkMap.WorldXA = (int)allocPlayerData.m_x;
                IsoChunkMap.WorldYA = (int)allocPlayerData.m_y;
                IsoChunkMap.WorldZA = (int)allocPlayerData.m_z;
                IsoChunkMap.WorldXA += 300 * IsoWorld.saveoffsetx;
                IsoChunkMap.WorldYA += 300 * IsoWorld.saveoffsety;
                IsoChunkMap.SWorldX[0] = (int)(allocPlayerData.m_x / 10.0f);
                IsoChunkMap.SWorldY[0] = (int)(allocPlayerData.m_y / 10.0f);
                final int[] sWorldX = IsoChunkMap.SWorldX;
                final int n = 0;
                sWorldX[n] += 30 * IsoWorld.saveoffsetx;
                final int[] sWorldY = IsoChunkMap.SWorldY;
                final int n2 = 0;
                sWorldY[n2] += 30 * IsoWorld.saveoffsety;
                return true;
            }
        }
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
        }
        finally {
            this.releasePlayerData(allocPlayerData);
        }
        return false;
    }
    
    static {
        PlayerDB.instance = null;
        TL_SliceBuffer = ThreadLocal.withInitial(() -> ByteBuffer.allocate(32768));
        TL_Bytes = ThreadLocal.withInitial(() -> new byte[1024]);
        PlayerDB.s_allow = false;
    }
    
    private static final class PlayerData
    {
        int m_sqlID;
        float m_x;
        float m_y;
        float m_z;
        boolean m_isDead;
        String m_name;
        int m_WorldVersion;
        ByteBuffer m_byteBuffer;
        
        private PlayerData() {
            this.m_sqlID = -1;
            this.m_byteBuffer = ByteBuffer.allocate(32768);
        }
        
        PlayerData set(final IsoPlayer isoPlayer) throws IOException {
            assert isoPlayer.sqlID >= 1;
            this.m_sqlID = isoPlayer.sqlID;
            this.m_x = isoPlayer.getX();
            this.m_y = isoPlayer.getY();
            this.m_z = isoPlayer.getZ();
            this.m_isDead = isoPlayer.isDead();
            this.m_name = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, isoPlayer.getDescriptor().getForename(), isoPlayer.getDescriptor().getSurname());
            this.m_WorldVersion = IsoWorld.getWorldVersion();
            ByteBuffer allocate = PlayerDB.TL_SliceBuffer.get();
            allocate.clear();
            while (true) {
                try {
                    isoPlayer.save(allocate);
                }
                catch (BufferOverflowException ex) {
                    if (allocate.capacity() >= 2097152) {
                        DebugLog.General.error("the player %s cannot be saved", isoPlayer.getUsername());
                        throw ex;
                    }
                    allocate = ByteBuffer.allocate(allocate.capacity() + 32768);
                    PlayerDB.TL_SliceBuffer.set(allocate);
                    continue;
                }
                break;
            }
            allocate.flip();
            this.setBytes(allocate);
            return this;
        }
        
        void setBytes(final ByteBuffer byteBuffer) {
            byteBuffer.rewind();
            final ByteBufferOutputStream byteBufferOutputStream = new ByteBufferOutputStream(this.m_byteBuffer, true);
            byteBufferOutputStream.clear();
            final byte[] dst = PlayerDB.TL_Bytes.get();
            int min;
            for (int i = byteBuffer.limit(); i > 0; i -= min) {
                min = Math.min(dst.length, i);
                byteBuffer.get(dst, 0, min);
                byteBufferOutputStream.write(dst, 0, min);
            }
            byteBufferOutputStream.flip();
            this.m_byteBuffer = byteBufferOutputStream.getWrappedBuffer();
        }
        
        void setBytes(final byte[] array) {
            final ByteBufferOutputStream byteBufferOutputStream = new ByteBufferOutputStream(this.m_byteBuffer, true);
            byteBufferOutputStream.clear();
            byteBufferOutputStream.write(array);
            byteBufferOutputStream.flip();
            this.m_byteBuffer = byteBufferOutputStream.getWrappedBuffer();
        }
        
        void setBytes(final InputStream inputStream) throws IOException {
            final ByteBufferOutputStream byteBufferOutputStream = new ByteBufferOutputStream(this.m_byteBuffer, true);
            byteBufferOutputStream.clear();
            final byte[] b = PlayerDB.TL_Bytes.get();
            while (true) {
                final int read = inputStream.read(b);
                if (read < 1) {
                    break;
                }
                byteBufferOutputStream.write(b, 0, read);
            }
            byteBufferOutputStream.flip();
            this.m_byteBuffer = byteBufferOutputStream.getWrappedBuffer();
        }
    }
    
    private static final class SQLPlayerStore implements IPlayerStore
    {
        Connection m_conn;
        
        private SQLPlayerStore() {
            this.m_conn = null;
        }
        
        @Override
        public void init(final TIntHashSet set) throws Exception {
            set.clear();
            if (Core.getInstance().isNoSave()) {
                return;
            }
            this.m_conn = PlayerDBHelper.create();
            this.initUsedIDs(set);
        }
        
        @Override
        public void Reset() {
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
        public void save(final PlayerData playerData) throws Exception {
            assert playerData.m_sqlID >= 1;
            if (this.m_conn == null) {
                return;
            }
            if (this.isInDB(playerData.m_sqlID)) {
                this.update(playerData);
            }
            else {
                this.add(playerData);
            }
        }
        
        @Override
        public boolean load(final PlayerData playerData) throws Exception {
            assert playerData.m_sqlID >= 1;
            if (this.m_conn == null) {
                return false;
            }
            final PreparedStatement prepareStatement = this.m_conn.prepareStatement("SELECT data,worldversion,x,y,z,isDead,name FROM localPlayers WHERE id=?");
            try {
                prepareStatement.setInt(1, playerData.m_sqlID);
                final ResultSet executeQuery = prepareStatement.executeQuery();
                if (executeQuery.next()) {
                    playerData.setBytes(executeQuery.getBinaryStream(1));
                    playerData.m_WorldVersion = executeQuery.getInt(2);
                    playerData.m_x = (float)executeQuery.getInt(3);
                    playerData.m_y = (float)executeQuery.getInt(4);
                    playerData.m_z = (float)executeQuery.getInt(5);
                    playerData.m_isDead = executeQuery.getBoolean(6);
                    playerData.m_name = executeQuery.getString(7);
                    final boolean b = true;
                    if (prepareStatement != null) {
                        prepareStatement.close();
                    }
                    return b;
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
            return false;
        }
        
        @Override
        public boolean loadEverythingExceptBytes(final PlayerData playerData) throws Exception {
            if (this.m_conn == null) {
                return false;
            }
            final PreparedStatement prepareStatement = this.m_conn.prepareStatement("SELECT worldversion,x,y,z,isDead,name FROM localPlayers WHERE id=?");
            try {
                prepareStatement.setInt(1, playerData.m_sqlID);
                final ResultSet executeQuery = prepareStatement.executeQuery();
                if (executeQuery.next()) {
                    playerData.m_WorldVersion = executeQuery.getInt(1);
                    playerData.m_x = (float)executeQuery.getInt(2);
                    playerData.m_y = (float)executeQuery.getInt(3);
                    playerData.m_z = (float)executeQuery.getInt(4);
                    playerData.m_isDead = executeQuery.getBoolean(5);
                    playerData.m_name = executeQuery.getString(6);
                    final boolean b = true;
                    if (prepareStatement != null) {
                        prepareStatement.close();
                    }
                    return b;
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
            return false;
        }
        
        void initUsedIDs(final TIntHashSet set) throws SQLException {
            final PreparedStatement prepareStatement = this.m_conn.prepareStatement("SELECT id FROM localPlayers");
            try {
                final ResultSet executeQuery = prepareStatement.executeQuery();
                while (executeQuery.next()) {
                    set.add(executeQuery.getInt(1));
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
        
        boolean isInDB(final int n) throws SQLException {
            final PreparedStatement prepareStatement = this.m_conn.prepareStatement("SELECT 1 FROM localPlayers WHERE id=?");
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
        
        void add(final PlayerData playerData) throws Exception {
            if (this.m_conn == null || playerData.m_sqlID < 1) {
                return;
            }
            final String s = "INSERT INTO localPlayers(wx,wy,x,y,z,worldversion,data,isDead,name,id) VALUES(?,?,?,?,?,?,?,?,?,?)";
            try {
                final PreparedStatement prepareStatement = this.m_conn.prepareStatement(s);
                try {
                    prepareStatement.setInt(1, (int)(playerData.m_x / 10.0f));
                    prepareStatement.setInt(2, (int)(playerData.m_y / 10.0f));
                    prepareStatement.setFloat(3, playerData.m_x);
                    prepareStatement.setFloat(4, playerData.m_y);
                    prepareStatement.setFloat(5, playerData.m_z);
                    prepareStatement.setInt(6, playerData.m_WorldVersion);
                    final ByteBuffer byteBuffer = playerData.m_byteBuffer;
                    byteBuffer.rewind();
                    prepareStatement.setBinaryStream(7, new ByteBufferBackedInputStream(byteBuffer), byteBuffer.remaining());
                    prepareStatement.setBoolean(8, playerData.m_isDead);
                    prepareStatement.setString(9, playerData.m_name);
                    prepareStatement.setInt(10, playerData.m_sqlID);
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
                PlayerDBHelper.rollback(this.m_conn);
                throw ex;
            }
        }
        
        public void update(final PlayerData playerData) throws Exception {
            if (this.m_conn == null || playerData.m_sqlID < 1) {
                return;
            }
            final String s = "UPDATE localPlayers SET wx = ?, wy = ?, x = ?, y = ?, z = ?, worldversion = ?, data = ?, isDead = ?, name = ? WHERE id=?";
            try {
                final PreparedStatement prepareStatement = this.m_conn.prepareStatement(s);
                try {
                    prepareStatement.setInt(1, (int)(playerData.m_x / 10.0f));
                    prepareStatement.setInt(2, (int)(playerData.m_y / 10.0f));
                    prepareStatement.setFloat(3, playerData.m_x);
                    prepareStatement.setFloat(4, playerData.m_y);
                    prepareStatement.setFloat(5, playerData.m_z);
                    prepareStatement.setInt(6, playerData.m_WorldVersion);
                    final ByteBuffer byteBuffer = playerData.m_byteBuffer;
                    byteBuffer.rewind();
                    prepareStatement.setBinaryStream(7, new ByteBufferBackedInputStream(byteBuffer), byteBuffer.remaining());
                    prepareStatement.setBoolean(8, playerData.m_isDead);
                    prepareStatement.setString(9, playerData.m_name);
                    prepareStatement.setInt(10, playerData.m_sqlID);
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
                PlayerDBHelper.rollback(this.m_conn);
                throw ex;
            }
        }
    }
    
    private interface IPlayerStore
    {
        void init(final TIntHashSet p0) throws Exception;
        
        void Reset() throws Exception;
        
        void save(final PlayerData p0) throws Exception;
        
        boolean load(final PlayerData p0) throws Exception;
        
        boolean loadEverythingExceptBytes(final PlayerData p0) throws Exception;
    }
}

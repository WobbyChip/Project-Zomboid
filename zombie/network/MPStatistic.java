// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network;

import zombie.core.logger.ExceptionLogger;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.io.FileNotFoundException;
import zombie.debug.DebugLog;
import java.io.File;
import zombie.ZomboidFileSystem;
import java.util.Iterator;
import zombie.core.raknet.UdpConnection;
import zombie.Lua.LuaManager;
import zombie.core.network.ByteBufferWriter;
import se.krka.kahlua.vm.KahluaTable;
import java.util.ArrayList;
import java.io.PrintStream;

public class MPStatistic
{
    public static MPStatistic instance;
    private static final boolean doPrintStatistic = false;
    private static final boolean doCSVStatistic = true;
    private static int Period;
    public TasksStatistic LoaderThreadTasks;
    public TasksStatistic RecalcThreadTasks;
    public SaveTasksStatistic SaveTasks;
    public ServerCellStatistic ServerMapToLoad;
    public ServerCellStatistic ServerMapLoadedCells;
    public ServerCellStatistic ServerMapLoaded2;
    private int countServerChunkThreadSaveNow;
    public MainThreadStatistic Main;
    public ThreadStatistic ServerLOS;
    public ThreadStatistic LoaderThread;
    public ThreadStatistic RecalcAllThread;
    public ThreadStatistic SaveThread;
    public ThreadStatistic PolyPathThread;
    public ThreadStatistic WorldReuser;
    public ThreadStatistic PlayerDownloadServer;
    public ThreadStatistic MapCollisionThread;
    public ProbeStatistic ChunkChecksum;
    public ProbeStatistic Bullet;
    public ProbeStatistic AnimationPlayerUpdate;
    public ProbeStatistic ServerMapPreupdate;
    public ProbeStatistic ServerMapPostupdate;
    public ProbeStatistic IngameStateUpdate;
    private long packetLength;
    private int countIncomePackets;
    private int countOutcomePackets;
    private int countIncomeBytes;
    private int countOutcomeBytes;
    private int maxIncomeBytesPerSecond;
    private int maxOutcomeBytesPerSecond;
    private int currentIncomeBytesPerSecond;
    private int currentOutcomeBytesPerSecond;
    private long lastCalculateBPS;
    private long lastReport;
    private long minUpdatePeriod;
    private long maxUpdatePeriod;
    private long avgUpdatePeriod;
    private long currentAvgUpdatePeriod;
    private long teleports;
    private long counter1;
    private long counter2;
    private long counter3;
    private long updatePeriods;
    private int loadCellFromDisk;
    private int saveCellToDisk;
    public static boolean clientStatisticEnable;
    private PrintStream csvStatisticFile;
    private PrintStream csvIncomePacketsFile;
    private PrintStream csvIncomeBytesFile;
    private PrintStream csvOutcomePacketsFile;
    private PrintStream csvOutcomeBytesFile;
    private PrintStream csvConnectionsFile;
    private ArrayList<Integer> csvConnections;
    private KahluaTable table;
    
    public MPStatistic() {
        this.LoaderThreadTasks = new TasksStatistic();
        this.RecalcThreadTasks = new TasksStatistic();
        this.SaveTasks = new SaveTasksStatistic();
        this.ServerMapToLoad = new ServerCellStatistic();
        this.ServerMapLoadedCells = new ServerCellStatistic();
        this.ServerMapLoaded2 = new ServerCellStatistic();
        this.countServerChunkThreadSaveNow = 0;
        this.Main = new MainThreadStatistic();
        this.ServerLOS = new ThreadStatistic();
        this.LoaderThread = new ThreadStatistic();
        this.RecalcAllThread = new ThreadStatistic();
        this.SaveThread = new ThreadStatistic();
        this.PolyPathThread = new ThreadStatistic();
        this.WorldReuser = new ThreadStatistic();
        this.PlayerDownloadServer = new ThreadStatistic();
        this.MapCollisionThread = new ThreadStatistic();
        this.ChunkChecksum = new ProbeStatistic();
        this.Bullet = new ProbeStatistic();
        this.AnimationPlayerUpdate = new ProbeStatistic();
        this.ServerMapPreupdate = new ProbeStatistic();
        this.ServerMapPostupdate = new ProbeStatistic();
        this.IngameStateUpdate = new ProbeStatistic();
        this.packetLength = 0L;
        this.countIncomePackets = 0;
        this.countOutcomePackets = 0;
        this.countIncomeBytes = 0;
        this.countOutcomeBytes = 0;
        this.maxIncomeBytesPerSecond = 0;
        this.maxOutcomeBytesPerSecond = 0;
        this.currentIncomeBytesPerSecond = 0;
        this.currentOutcomeBytesPerSecond = 0;
        this.lastCalculateBPS = 0L;
        this.lastReport = 0L;
        this.minUpdatePeriod = 9999L;
        this.maxUpdatePeriod = 0L;
        this.avgUpdatePeriod = 0L;
        this.currentAvgUpdatePeriod = 0L;
        this.teleports = 0L;
        this.counter1 = 0L;
        this.counter2 = 0L;
        this.counter3 = 0L;
        this.updatePeriods = 0L;
        this.loadCellFromDisk = 0;
        this.saveCellToDisk = 0;
        this.csvStatisticFile = null;
        this.csvIncomePacketsFile = null;
        this.csvIncomeBytesFile = null;
        this.csvOutcomePacketsFile = null;
        this.csvOutcomeBytesFile = null;
        this.csvConnectionsFile = null;
        this.csvConnections = new ArrayList<Integer>();
        this.table = null;
        if (GameServer.bServer) {
            this.openCSVStatistic();
        }
    }
    
    public static MPStatistic getInstance() {
        if (MPStatistic.instance == null) {
            MPStatistic.instance = new MPStatistic();
        }
        return MPStatistic.instance;
    }
    
    public void IncrementServerChunkThreadSaveNow() {
        ++this.countServerChunkThreadSaveNow;
    }
    
    public void teleport() {
        ++this.teleports;
    }
    
    public void count1(final long n) {
        this.counter1 += n;
    }
    
    public void count2(final long n) {
        this.counter2 += n;
    }
    
    public void count3(final long n) {
        this.counter3 += n;
    }
    
    public void write(final ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putLong(this.minUpdatePeriod);
        byteBufferWriter.putLong(this.maxUpdatePeriod);
        byteBufferWriter.putLong(this.currentAvgUpdatePeriod / this.updatePeriods);
        byteBufferWriter.putLong(this.updatePeriods / MPStatistic.Period);
        byteBufferWriter.putLong(this.teleports);
        byteBufferWriter.putLong(GameServer.udpEngine.connections.size());
        byteBufferWriter.putLong(this.counter1 / this.updatePeriods);
        byteBufferWriter.putLong(this.counter2 / this.updatePeriods);
        byteBufferWriter.putLong(this.counter3 / this.updatePeriods);
    }
    
    public void setPacketsLength(final long packetLength) {
        this.packetLength = packetLength;
    }
    
    public void addIncomePacket(final short s, final int n) {
        final PacketTypes.PacketType packetType = PacketTypes.packetTypes.get(s);
        if (packetType != null) {
            final PacketTypes.PacketType packetType2 = packetType;
            ++packetType2.incomePackets;
            ++this.countIncomePackets;
            final PacketTypes.PacketType packetType3 = packetType;
            packetType3.incomeBytes += n;
            this.countIncomeBytes += n;
            this.currentIncomeBytesPerSecond += n;
            this.calculateMaxBPS();
        }
    }
    
    public void addOutcomePacket(final short s, final int n) {
        final PacketTypes.PacketType packetType = PacketTypes.packetTypes.get(s);
        if (packetType != null) {
            final PacketTypes.PacketType packetType2 = packetType;
            ++packetType2.outcomePackets;
            ++this.countOutcomePackets;
            final PacketTypes.PacketType packetType3 = packetType;
            packetType3.outcomeBytes += n;
            this.countOutcomeBytes += n;
            this.currentOutcomeBytesPerSecond += n;
            this.calculateMaxBPS();
        }
    }
    
    void calculateMaxBPS() {
        if (System.currentTimeMillis() - this.lastCalculateBPS > 1000L) {
            this.lastCalculateBPS = System.currentTimeMillis();
            if (this.currentIncomeBytesPerSecond > this.maxIncomeBytesPerSecond) {
                this.maxIncomeBytesPerSecond = this.currentIncomeBytesPerSecond;
            }
            if (this.currentOutcomeBytesPerSecond > this.maxOutcomeBytesPerSecond) {
                this.maxOutcomeBytesPerSecond = this.currentOutcomeBytesPerSecond;
            }
            this.currentIncomeBytesPerSecond = 0;
            this.currentOutcomeBytesPerSecond = 0;
        }
    }
    
    public void IncrementLoadCellFromDisk() {
        ++this.loadCellFromDisk;
    }
    
    public void IncrementSaveCellToDisk() {
        ++this.saveCellToDisk;
    }
    
    public void process(final long n) {
        if (n > this.maxUpdatePeriod) {
            this.maxUpdatePeriod = n;
        }
        if (n < this.minUpdatePeriod) {
            this.minUpdatePeriod = n;
        }
        this.avgUpdatePeriod += (long)((n - this.avgUpdatePeriod) * 0.05f);
        this.currentAvgUpdatePeriod += n;
        ++this.updatePeriods;
        if (MPStatistic.Period == 0 || System.currentTimeMillis() - this.lastReport < MPStatistic.Period * 1000) {
            return;
        }
        this.lastReport = System.currentTimeMillis();
        this.printStatistic();
        this.printCSVStatistic();
        GameServer.sendShortStatistic();
        (this.table = LuaManager.platform.newTable()).rawset((Object)"lastReport", (Object)this.lastReport);
        this.table.rawset((Object)"period", (Object)(double)MPStatistic.Period);
        this.table.rawset((Object)"minUpdatePeriod", (Object)this.minUpdatePeriod);
        this.table.rawset((Object)"maxUpdatePeriod", (Object)this.maxUpdatePeriod);
        this.table.rawset((Object)"avgUpdatePeriod", (Object)this.avgUpdatePeriod);
        this.maxUpdatePeriod = 0L;
        this.minUpdatePeriod = 9999L;
        this.currentAvgUpdatePeriod = 0L;
        this.updatePeriods = 0L;
        this.teleports = 0L;
        this.counter1 = 0L;
        this.counter2 = 0L;
        this.counter3 = 0L;
        this.table.rawset((Object)"loadCellFromDisk", (Object)(double)this.loadCellFromDisk);
        this.table.rawset((Object)"saveCellToDisk", (Object)(double)this.saveCellToDisk);
        this.loadCellFromDisk = 0;
        this.saveCellToDisk = 0;
        this.table.rawset((Object)"usedMemory", (Object)(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
        this.table.rawset((Object)"totalMemory", (Object)Runtime.getRuntime().totalMemory());
        this.table.rawset((Object)"freeMemory", (Object)Runtime.getRuntime().freeMemory());
        this.table.rawset((Object)"countConnections", (Object)(double)GameServer.udpEngine.connections.size());
        final KahluaTable table = LuaManager.platform.newTable();
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final KahluaTable table2 = LuaManager.platform.newTable();
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            table2.rawset((Object)"ip", (Object)udpConnection.ip);
            table2.rawset((Object)"username", (Object)udpConnection.username);
            table2.rawset((Object)"accessLevel", (Object)udpConnection.accessLevel);
            final KahluaTable table3 = LuaManager.platform.newTable();
            for (int j = 0; j < udpConnection.players.length; ++j) {
                if (udpConnection.players[j] != null) {
                    final KahluaTable table4 = LuaManager.platform.newTable();
                    table4.rawset((Object)"username", (Object)udpConnection.players[j].username);
                    table4.rawset((Object)"x", (Object)(double)udpConnection.players[j].x);
                    table4.rawset((Object)"y", (Object)(double)udpConnection.players[j].y);
                    table4.rawset((Object)"z", (Object)(double)udpConnection.players[j].z);
                    table3.rawset(j, (Object)table4);
                }
            }
            table2.rawset((Object)"users", (Object)table3);
            table2.rawset((Object)"diff", (Object)(double)udpConnection.statistic.diff);
            table2.rawset((Object)"pingAVG", (Object)(double)udpConnection.statistic.pingAVG);
            table2.rawset((Object)"remotePlayersCount", (Object)(double)udpConnection.statistic.remotePlayersCount);
            table2.rawset((Object)"remotePlayersDesyncAVG", (Object)(double)udpConnection.statistic.remotePlayersDesyncAVG);
            table2.rawset((Object)"remotePlayersDesyncMax", (Object)(double)udpConnection.statistic.remotePlayersDesyncMax);
            table2.rawset((Object)"remotePlayersTeleports", (Object)(double)udpConnection.statistic.remotePlayersTeleports);
            table2.rawset((Object)"zombiesCount", (Object)(double)udpConnection.statistic.zombiesCount);
            table2.rawset((Object)"zombiesLocalOwnership", (Object)(double)udpConnection.statistic.zombiesLocalOwnership);
            table2.rawset((Object)"zombiesDesyncAVG", (Object)(double)udpConnection.statistic.zombiesDesyncAVG);
            table2.rawset((Object)"zombiesDesyncMax", (Object)(double)udpConnection.statistic.zombiesDesyncMax);
            table2.rawset((Object)"zombiesTeleports", (Object)(double)udpConnection.statistic.zombiesTeleports);
            table2.rawset((Object)"FPS", (Object)(double)udpConnection.statistic.FPS);
            table2.rawset((Object)"FPSMin", (Object)(double)udpConnection.statistic.FPSMin);
            table2.rawset((Object)"FPSAvg", (Object)(double)udpConnection.statistic.FPSAvg);
            table2.rawset((Object)"FPSMax", (Object)(double)udpConnection.statistic.FPSMax);
            final KahluaTable table5 = LuaManager.platform.newTable();
            short n2 = 0;
            for (int k = 0; k < 32; ++k) {
                table5.rawset(k, (Object)(double)udpConnection.statistic.FPSHistogramm[k]);
                if (n2 < udpConnection.statistic.FPSHistogramm[k]) {
                    n2 = udpConnection.statistic.FPSHistogramm[k];
                }
            }
            table2.rawset((Object)"FPSHistogram", (Object)table5);
            table2.rawset((Object)"FPSHistogramMax", (Object)(double)n2);
            table.rawset(i, (Object)table2);
        }
        this.table.rawset((Object)"connections", (Object)table);
        this.table.rawset((Object)"packetLength", (Object)this.packetLength);
        this.table.rawset((Object)"countIncomePackets", (Object)(double)this.countIncomePackets);
        this.table.rawset((Object)"countIncomeBytes", (Object)(double)this.countIncomeBytes);
        this.table.rawset((Object)"maxIncomeBytesPerSecound", (Object)(double)this.maxIncomeBytesPerSecond);
        final KahluaTable table6 = LuaManager.platform.newTable();
        final int n3 = -1;
        for (final PacketTypes.PacketType packetType : PacketTypes.packetTypes.values()) {
            if (packetType.incomePackets > 0) {
                final KahluaTable table7 = LuaManager.platform.newTable();
                table7.rawset((Object)"name", (Object)packetType.name());
                table7.rawset((Object)"count", (Object)(double)packetType.incomePackets);
                table7.rawset((Object)"bytes", (Object)(double)packetType.incomeBytes);
                table6.rawset(n3, (Object)table7);
            }
            packetType.incomePackets = 0;
            packetType.incomeBytes = 0;
        }
        this.table.rawset((Object)"incomePacketsTable", (Object)table6);
        this.countIncomePackets = 0;
        this.countIncomeBytes = 0;
        this.maxIncomeBytesPerSecond = 0;
        this.table.rawset((Object)"countOutcomePackets", (Object)(double)this.countOutcomePackets);
        this.table.rawset((Object)"countOutcomeBytes", (Object)(double)this.countOutcomeBytes);
        this.table.rawset((Object)"maxOutcomeBytesPerSecound", (Object)(double)this.maxOutcomeBytesPerSecond);
        final KahluaTable table8 = LuaManager.platform.newTable();
        int n4 = -1;
        for (final PacketTypes.PacketType packetType2 : PacketTypes.packetTypes.values()) {
            if (packetType2.outcomePackets > 0) {
                final KahluaTable table9 = LuaManager.platform.newTable();
                table9.rawset((Object)"name", (Object)packetType2.name());
                table9.rawset((Object)"count", (Object)(double)packetType2.outcomePackets);
                table9.rawset((Object)"bytes", (Object)(double)packetType2.outcomeBytes);
                table8.rawset(n4++, (Object)table9);
            }
            packetType2.outcomePackets = 0;
            packetType2.outcomeBytes = 0;
        }
        this.table.rawset((Object)"outcomePacketsTable", (Object)table8);
        this.countOutcomePackets = 0;
        this.countOutcomeBytes = 0;
        this.maxOutcomeBytesPerSecond = 0;
        this.LoaderThreadTasks.Clear();
        this.RecalcThreadTasks.Clear();
        this.SaveTasks.Clear();
        this.ServerMapToLoad.Clear();
        this.ServerMapLoadedCells.Clear();
        this.ServerMapLoaded2.Clear();
        this.countServerChunkThreadSaveNow = 0;
        this.Main.Clear();
        this.ServerLOS.Clear();
        this.LoaderThread.Clear();
        this.RecalcAllThread.Clear();
        this.SaveThread.Clear();
        this.PolyPathThread.Clear();
        this.WorldReuser.Clear();
        this.PlayerDownloadServer.Clear();
        this.MapCollisionThread.Clear();
        this.ChunkChecksum.Clear();
        this.Bullet.Clear();
        this.AnimationPlayerUpdate.Clear();
        this.ServerMapPreupdate.Clear();
        this.ServerMapPostupdate.Clear();
        this.IngameStateUpdate.Clear();
        GameServer.getStatisticFromClients();
        GameServer.sendStatistic();
    }
    
    private void printStatistic() {
    }
    
    public static String getStatisticDir() {
        final String cacheDirSub = ZomboidFileSystem.instance.getCacheDirSub("Statistic");
        ZomboidFileSystem.ensureFolderExists(cacheDirSub);
        return new File(cacheDirSub).getAbsolutePath();
    }
    
    private void openCSVStatistic() {
        final String statisticDir = getStatisticDir();
        try {
            final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, statisticDir, File.separator));
            try {
                this.csvStatisticFile = new PrintStream(file);
            }
            catch (FileNotFoundException ex3) {
                try {
                    this.csvStatisticFile = new PrintStream(new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, statisticDir, File.separator)));
                }
                catch (FileNotFoundException ex) {
                    DebugLog.Statistic.error((Object)"The Statistic.csv was not open");
                    ex.printStackTrace();
                }
            }
            this.csvStatisticFile.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.Main.PrintTitle("MainThread"), this.ServerLOS.PrintTitle("ServerLOS"), this.LoaderThread.PrintTitle("LoaderThread"), this.RecalcAllThread.PrintTitle("RecalcAllThread"), this.SaveThread.PrintTitle("SaveThread"), this.PolyPathThread.PrintTitle("PolyPathThread"), this.WorldReuser.PrintTitle("WorldReuser"), this.PlayerDownloadServer.PrintTitle("WorldReuser"), this.MapCollisionThread.PrintTitle("MapCollisionThread"), this.ChunkChecksum.PrintTitle("ChunkChecksum"), this.Bullet.PrintTitle("Bullet"), this.AnimationPlayerUpdate.PrintTitle("AnimationPlayerUpdate"), this.ServerMapPreupdate.PrintTitle("ServerMapPreupdate"), this.ServerMapPostupdate.PrintTitle("ServerMapPostupdate"), this.IngameStateUpdate.PrintTitle("IngameStateUpdate")));
            this.csvIncomePacketsFile = new PrintStream(new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, statisticDir, File.separator)));
            for (final PacketTypes.PacketType packetType : PacketTypes.packetTypes.values()) {
                this.csvIncomePacketsFile.print(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;S)Ljava/lang/String;, packetType.name(), packetType.getId()));
            }
            this.csvIncomePacketsFile.println();
            this.csvIncomeBytesFile = new PrintStream(new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, statisticDir, File.separator)));
            for (final PacketTypes.PacketType packetType2 : PacketTypes.packetTypes.values()) {
                this.csvIncomeBytesFile.print(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;S)Ljava/lang/String;, packetType2.name(), packetType2.getId()));
            }
            this.csvIncomeBytesFile.println();
            this.csvOutcomePacketsFile = new PrintStream(new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, statisticDir, File.separator)));
            for (final PacketTypes.PacketType packetType3 : PacketTypes.packetTypes.values()) {
                this.csvOutcomePacketsFile.print(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;S)Ljava/lang/String;, packetType3.name(), packetType3.getId()));
            }
            this.csvOutcomePacketsFile.println();
            this.csvOutcomeBytesFile = new PrintStream(new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, statisticDir, File.separator)));
            for (final PacketTypes.PacketType packetType4 : PacketTypes.packetTypes.values()) {
                this.csvOutcomeBytesFile.print(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;S)Ljava/lang/String;, packetType4.name(), packetType4.getId()));
            }
            this.csvOutcomeBytesFile.println();
            (this.csvConnectionsFile = new PrintStream(new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, statisticDir, File.separator)))).print("ip; ");
            this.csvConnectionsFile.print("username; ");
            this.csvConnectionsFile.print("accessLevel; ");
            this.csvConnectionsFile.print("players.length; ");
            this.csvConnectionsFile.print("ping; ");
            this.csvConnectionsFile.print("pingAVG; ");
            this.csvConnectionsFile.print("remotePlayersCount; ");
            this.csvConnectionsFile.print("remotePlayersDesyncAVG; ");
            this.csvConnectionsFile.print("remotePlayersDesyncMax; ");
            this.csvConnectionsFile.print("remotePlayersTeleports; ");
            this.csvConnectionsFile.print("zombiesCount; ");
            this.csvConnectionsFile.print("zombiesLocalOwnership; ");
            this.csvConnectionsFile.print("zombiesDesyncAVG; ");
            this.csvConnectionsFile.print("zombiesDesyncMax; ");
            this.csvConnectionsFile.println("zombiesTeleports; ");
            this.csvConnectionsFile.print("FPS; ");
            this.csvConnectionsFile.print("FPSMin; ");
            this.csvConnectionsFile.print("FPSAvg; ");
            this.csvConnectionsFile.print("FPSMax; ");
            for (int i = 0; i < 32; ++i) {
                this.csvConnectionsFile.print(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, i));
            }
            this.csvConnectionsFile.println();
        }
        catch (FileNotFoundException ex2) {
            ex2.printStackTrace();
            this.csvStatisticFile = null;
        }
    }
    
    private void printCSVStatistic() {
        try {
            if (this.csvStatisticFile != null) {
                this.csvStatisticFile.print(invokedynamic(makeConcatWithConstants:(J)Ljava/lang/String;, System.currentTimeMillis()));
                this.csvStatisticFile.print(invokedynamic(makeConcatWithConstants:(J)Ljava/lang/String;, this.minUpdatePeriod));
                this.csvStatisticFile.print(invokedynamic(makeConcatWithConstants:(J)Ljava/lang/String;, this.maxUpdatePeriod));
                this.csvStatisticFile.print(invokedynamic(makeConcatWithConstants:(J)Ljava/lang/String;, this.avgUpdatePeriod));
                this.csvStatisticFile.print(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.loadCellFromDisk));
                this.csvStatisticFile.print(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.saveCellToDisk));
                this.csvStatisticFile.print(this.LoaderThreadTasks.Print());
                this.csvStatisticFile.print(this.RecalcThreadTasks.Print());
                this.csvStatisticFile.print(this.SaveTasks.Print());
                this.csvStatisticFile.print(this.ServerMapToLoad.Print());
                this.csvStatisticFile.print(this.ServerMapLoadedCells.Print());
                this.csvStatisticFile.print(this.ServerMapLoaded2.Print());
                this.csvStatisticFile.print(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.countServerChunkThreadSaveNow));
                this.csvStatisticFile.print(this.Main.Print());
                this.csvStatisticFile.print(this.ServerLOS.Print());
                this.csvStatisticFile.print(this.LoaderThread.Print());
                this.csvStatisticFile.print(this.RecalcAllThread.Print());
                this.csvStatisticFile.print(this.SaveThread.Print());
                this.csvStatisticFile.print(this.PolyPathThread.Print());
                this.csvStatisticFile.print(this.WorldReuser.Print());
                this.csvStatisticFile.print(this.PlayerDownloadServer.Print());
                this.csvStatisticFile.print(this.MapCollisionThread.Print());
                this.csvStatisticFile.print(this.ChunkChecksum.Print());
                this.csvStatisticFile.print(this.Bullet.Print());
                this.csvStatisticFile.print(this.AnimationPlayerUpdate.Print());
                this.csvStatisticFile.print(this.ServerMapPreupdate.Print());
                this.csvStatisticFile.print(this.ServerMapPostupdate.Print());
                this.csvStatisticFile.print(this.IngameStateUpdate.Print());
                this.csvStatisticFile.print(invokedynamic(makeConcatWithConstants:(J)Ljava/lang/String;, Runtime.getRuntime().totalMemory()));
                this.csvStatisticFile.print(invokedynamic(makeConcatWithConstants:(J)Ljava/lang/String;, Runtime.getRuntime().freeMemory()));
                this.csvStatisticFile.print(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, GameServer.udpEngine.connections.size()));
                this.csvStatisticFile.print(invokedynamic(makeConcatWithConstants:(J)Ljava/lang/String;, this.packetLength));
                this.csvStatisticFile.print(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.countIncomePackets));
                this.csvStatisticFile.print(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.countIncomeBytes));
                this.csvStatisticFile.print(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.maxIncomeBytesPerSecond));
                this.csvStatisticFile.print(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.countOutcomePackets));
                this.csvStatisticFile.print(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.countOutcomeBytes));
                this.csvStatisticFile.println(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.maxOutcomeBytesPerSecond));
                for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                    final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
                    try {
                        if (udpConnection != null && udpConnection.username != null && !this.csvConnections.contains(udpConnection.username.hashCode())) {
                            this.csvConnections.add(udpConnection.username.hashCode());
                        }
                    }
                    catch (NullPointerException ex) {
                        ex.printStackTrace();
                        return;
                    }
                }
                for (int j = 0; j < this.csvConnections.size(); ++j) {
                    UdpConnection udpConnection2 = null;
                    for (int k = 0; k < GameServer.udpEngine.connections.size(); ++k) {
                        final UdpConnection udpConnection3 = GameServer.udpEngine.connections.get(k);
                        if (udpConnection3 != null && udpConnection3.username != null && udpConnection3.username.hashCode() == this.csvConnections.get(j)) {
                            udpConnection2 = udpConnection3;
                        }
                    }
                    if (udpConnection2 == null) {
                        for (int l = 0; l < 51; ++l) {
                            this.csvConnectionsFile.print("; ");
                        }
                        this.csvConnectionsFile.println();
                    }
                    else {
                        this.csvConnectionsFile.print(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, udpConnection2.ip));
                        this.csvConnectionsFile.print(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, udpConnection2.username));
                        this.csvConnectionsFile.print(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, udpConnection2.accessLevel));
                        this.csvConnectionsFile.print(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, udpConnection2.players.length));
                        this.csvConnectionsFile.print(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, udpConnection2.statistic.diff / 2));
                        this.csvConnectionsFile.print(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, udpConnection2.statistic.pingAVG));
                        this.csvConnectionsFile.print(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, udpConnection2.statistic.remotePlayersCount));
                        this.csvConnectionsFile.print(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, udpConnection2.statistic.remotePlayersDesyncAVG));
                        this.csvConnectionsFile.print(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, udpConnection2.statistic.remotePlayersDesyncMax));
                        this.csvConnectionsFile.print(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, udpConnection2.statistic.remotePlayersTeleports));
                        this.csvConnectionsFile.print(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, udpConnection2.statistic.zombiesCount));
                        this.csvConnectionsFile.print(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, udpConnection2.statistic.zombiesLocalOwnership));
                        this.csvConnectionsFile.print(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, udpConnection2.statistic.zombiesDesyncAVG));
                        this.csvConnectionsFile.print(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, udpConnection2.statistic.zombiesDesyncMax));
                        this.csvConnectionsFile.println(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, udpConnection2.statistic.zombiesTeleports));
                        this.csvConnectionsFile.print(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, udpConnection2.statistic.zombiesTeleports));
                        this.csvConnectionsFile.print(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, udpConnection2.statistic.FPS));
                        this.csvConnectionsFile.print(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, udpConnection2.statistic.FPSMin));
                        this.csvConnectionsFile.print(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, udpConnection2.statistic.FPSAvg));
                        this.csvConnectionsFile.println(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, udpConnection2.statistic.FPSMax));
                        for (int n = 0; n < 32; ++n) {
                            this.csvConnectionsFile.println(invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, udpConnection2.statistic.FPSHistogramm[n]));
                        }
                    }
                }
                for (final PacketTypes.PacketType packetType : PacketTypes.packetTypes.values()) {
                    this.csvIncomePacketsFile.print(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, packetType.incomePackets));
                    this.csvIncomeBytesFile.print(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, packetType.incomeBytes));
                    this.csvOutcomePacketsFile.print(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, packetType.outcomePackets));
                    this.csvOutcomeBytesFile.print(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, packetType.outcomeBytes));
                }
                this.csvIncomePacketsFile.println();
                this.csvIncomeBytesFile.println();
                this.csvOutcomePacketsFile.println();
                this.csvOutcomeBytesFile.println();
                this.csvStatisticFile.flush();
                this.csvConnectionsFile.flush();
                this.csvIncomePacketsFile.flush();
                this.csvIncomeBytesFile.flush();
                this.csvOutcomePacketsFile.flush();
                this.csvOutcomeBytesFile.flush();
            }
        }
        catch (NullPointerException ex2) {
            ex2.printStackTrace();
        }
    }
    
    public void getStatisticTable(final ByteBuffer byteBuffer) throws IOException {
        if (this.table != null) {
            this.table.save(byteBuffer);
        }
    }
    
    public void setStatisticTable(final ByteBuffer byteBuffer) throws IOException {
        if (byteBuffer.remaining() == 0) {
            return;
        }
        this.table = LuaManager.platform.newTable();
        try {
            this.table.load(byteBuffer, 186);
            this.table.rawset((Object)"lastReportTime", (Object)System.currentTimeMillis());
        }
        catch (Exception ex) {
            this.table = null;
            ExceptionLogger.logException(ex);
        }
    }
    
    public KahluaTable getStatisticTableForLua() {
        return this.table;
    }
    
    public void setPeriod(final int period) {
        MPStatistic.Period = period;
        if (this.table != null) {
            this.table.rawset((Object)"period", (Object)(double)MPStatistic.Period);
        }
    }
    
    static {
        MPStatistic.Period = 0;
        MPStatistic.clientStatisticEnable = false;
    }
    
    public class ThreadStatistic
    {
        protected boolean started;
        protected long timeStart;
        protected long timeWork;
        protected long timeMax;
        protected long timeSleep;
        protected long timeCount;
        
        public ThreadStatistic() {
            this.started = false;
            this.timeStart = 0L;
            this.timeWork = 0L;
            this.timeMax = 0L;
            this.timeSleep = 0L;
            this.timeCount = 0L;
        }
        
        public void Clear() {
            this.timeWork = 0L;
            this.timeMax = 0L;
            this.timeSleep = 0L;
            this.timeCount = 0L;
        }
        
        public String PrintTitle(final String s) {
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, s, s, s);
        }
        
        public String Print() {
            return invokedynamic(makeConcatWithConstants:(JJJJ)Ljava/lang/String;, this.timeWork, this.timeMax, this.timeSleep, this.timeCount);
        }
        
        public void Start() {
            if (this.started) {
                this.End();
            }
            if (this.timeStart != 0L) {
                this.timeSleep += System.currentTimeMillis() - this.timeStart;
            }
            this.timeStart = System.currentTimeMillis();
            ++this.timeCount;
            this.started = true;
        }
        
        public void End() {
            if (this.timeStart == 0L || !this.started) {
                return;
            }
            final long timeMax = System.currentTimeMillis() - this.timeStart;
            this.timeStart = System.currentTimeMillis();
            this.timeWork += timeMax;
            if (this.timeMax < timeMax) {
                this.timeMax = timeMax;
            }
            this.started = false;
        }
    }
    
    public class MainThreadStatistic extends ThreadStatistic
    {
        private long timeStartSleep;
        
        public MainThreadStatistic() {
            this.timeStartSleep = 0L;
        }
        
        @Override
        public void Start() {
            if (this.timeStart == 0L) {
                this.timeStart = System.currentTimeMillis();
                return;
            }
            final long timeMax = System.currentTimeMillis() - this.timeStart;
            this.timeStart = System.currentTimeMillis();
            this.timeWork += timeMax;
            if (this.timeMax < timeMax) {
                this.timeMax = timeMax;
            }
            ++this.timeCount;
        }
        
        @Override
        public void End() {
        }
        
        public void StartSleep() {
            this.timeStartSleep = System.currentTimeMillis();
        }
        
        public void EndSleep() {
            final long n = System.currentTimeMillis() - this.timeStartSleep;
            this.timeSleep += n;
            this.timeStart += n;
        }
    }
    
    public class ProbeStatistic
    {
        protected boolean started;
        protected long timeStart;
        protected long timeWork;
        protected long timeMax;
        protected long timeCount;
        
        public ProbeStatistic() {
            this.started = false;
            this.timeStart = 0L;
            this.timeWork = 0L;
            this.timeMax = 0L;
            this.timeCount = 0L;
        }
        
        public void Clear() {
            this.timeWork = 0L;
            this.timeMax = 0L;
            this.timeCount = 0L;
        }
        
        public String PrintTitle(final String s) {
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, s, s);
        }
        
        public String Print() {
            return invokedynamic(makeConcatWithConstants:(JJJ)Ljava/lang/String;, this.timeWork / 1000000L, this.timeMax / 1000000L, this.timeCount);
        }
        
        public void Start() {
            this.timeStart = System.nanoTime();
            ++this.timeCount;
            this.started = true;
        }
        
        public void End() {
            if (!this.started) {
                return;
            }
            final long timeMax = System.nanoTime() - this.timeStart;
            this.timeWork += timeMax;
            if (this.timeMax < timeMax) {
                this.timeMax = timeMax;
            }
            this.started = false;
        }
    }
    
    public class TasksStatistic
    {
        protected long added;
        protected long processed;
        
        public TasksStatistic() {
            this.added = 0L;
            this.processed = 0L;
        }
        
        public void Clear() {
            this.added = 0L;
            this.processed = 0L;
        }
        
        public String PrintTitle(final String s) {
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, s);
        }
        
        public String Print() {
            return invokedynamic(makeConcatWithConstants:(JJ)Ljava/lang/String;, this.added, this.processed);
        }
        
        public void Added() {
            ++this.added;
        }
        
        public void Processed() {
            ++this.processed;
        }
    }
    
    public class ServerCellStatistic
    {
        protected long added;
        protected long canceled;
        
        public ServerCellStatistic() {
            this.added = 0L;
            this.canceled = 0L;
        }
        
        public void Clear() {
            this.added = 0L;
            this.canceled = 0L;
        }
        
        public String PrintTitle(final String s) {
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, s);
        }
        
        public String Print() {
            return invokedynamic(makeConcatWithConstants:(JJ)Ljava/lang/String;, this.added, this.canceled);
        }
        
        public void Added() {
            ++this.added;
        }
        
        public void Added(final int n) {
            this.added += n;
        }
        
        public void Canceled() {
            ++this.canceled;
        }
    }
    
    public class SaveTasksStatistic extends TasksStatistic
    {
        private int SaveUnloadedTasksAdded;
        private int SaveLoadedTasksAdded;
        private int SaveGameTimeTasksAdded;
        private int QuitThreadTasksAdded;
        
        public SaveTasksStatistic() {
            this.SaveUnloadedTasksAdded = 0;
            this.SaveLoadedTasksAdded = 0;
            this.SaveGameTimeTasksAdded = 0;
            this.QuitThreadTasksAdded = 0;
        }
        
        @Override
        public void Clear() {
            super.Clear();
            this.SaveUnloadedTasksAdded = 0;
            this.SaveLoadedTasksAdded = 0;
            this.SaveGameTimeTasksAdded = 0;
            this.QuitThreadTasksAdded = 0;
        }
        
        @Override
        public String PrintTitle(final String s) {
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, s, s, s, s);
        }
        
        @Override
        public String Print() {
            return invokedynamic(makeConcatWithConstants:(IIIIJ)Ljava/lang/String;, this.SaveUnloadedTasksAdded, this.SaveLoadedTasksAdded, this.SaveGameTimeTasksAdded, this.QuitThreadTasksAdded, this.processed);
        }
        
        public void SaveUnloadedTasksAdded() {
            ++this.SaveUnloadedTasksAdded;
        }
        
        public void SaveLoadedTasksAdded() {
            ++this.SaveLoadedTasksAdded;
        }
        
        public void SaveGameTimeTasksAdded() {
            ++this.SaveGameTimeTasksAdded;
        }
        
        public void QuitThreadTasksAdded() {
            ++this.QuitThreadTasksAdded;
        }
    }
}

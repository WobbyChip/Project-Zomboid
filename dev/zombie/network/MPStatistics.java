// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network;

import zombie.Lua.LuaManager;
import java.text.NumberFormat;
import java.util.concurrent.TimeUnit;
import zombie.GameWindow;
import java.nio.ByteBuffer;
import zombie.debug.DebugOptions;
import zombie.characters.IsoPlayer;
import zombie.core.znet.ZNetStatistics;
import zombie.GameTime;
import java.util.Iterator;
import zombie.core.raknet.UdpConnection;
import zombie.popman.NetworkZombieManager;
import zombie.VirtualZombieManager;
import zombie.popman.NetworkZombieSimulator;
import zombie.iso.IsoWorld;
import zombie.MovingObjectUpdateScheduler;
import java.util.HashSet;
import java.util.ArrayList;
import zombie.core.utils.UpdateLimit;
import se.krka.kahlua.vm.KahluaTable;

public class MPStatistics
{
    private static final long REQUEST_TIMEOUT = 10000L;
    private static final long STATISTICS_INTERVAL = 2000L;
    private static final long PING_INTERVAL = 1000L;
    private static final long PING_PERIOD = 10000L;
    private static final KahluaTable pingTable;
    private static final KahluaTable statsTable;
    private static final UpdateLimit ulRequestTimeout;
    private static final UpdateLimit ulStatistics;
    private static final UpdateLimit ulPing;
    private static boolean serverStatisticsEnabled;
    private static int lastPing;
    private static int avgPing;
    private static int minPing;
    private static long serverMemTotal;
    private static long serverMemUsed;
    private static long serverMemFree;
    private static long serverRX;
    private static long serverTX;
    private static double serverLoss;
    private static float serverFPS;
    private static long serverNetworkingUpdates;
    private static long serverNetworkingFPS;
    private static String serverRevision;
    private static long clientMemTotal;
    private static long clientMemUsed;
    private static long clientMemFree;
    private static long clientRX;
    private static long clientTX;
    private static double clientLoss;
    private static float clientFPS;
    private static int serverZombiesTotal;
    private static int serverZombiesLoaded;
    private static int serverZombiesSimulated;
    private static int serverZombiesCulled;
    private static int serverZombiesAuthorized;
    private static int serverZombiesUnauthorized;
    private static int serverZombiesReusable;
    private static int serverZombiesUpdated;
    private static int clientZombiesTotal;
    private static int clientZombiesLoaded;
    private static int clientZombiesSimulated;
    private static int clientZombiesCulled;
    private static int clientZombiesAuthorized;
    private static int clientZombiesUnauthorized;
    private static int clientZombiesReusable;
    private static int clientZombiesUpdated;
    private static long zombieUpdates;
    private static long serverHandledMinPing;
    private static long serverHandledMaxPing;
    private static long serverHandledAvgPing;
    private static long serverHandledLastPing;
    private static long serverHandledLossPing;
    private static long serverHandledPingPeriodStart;
    private static int serverHandledPingPacketIndex;
    private static final ArrayList<Long> serverHandledPingHistory;
    private static final HashSet<Long> serverHandledLossPingHistory;
    
    private static void getClientZombieStatistics() {
        final int n = (int)Math.max(MovingObjectUpdateScheduler.instance.getFrameCounter() - MPStatistics.zombieUpdates, 1L);
        MPStatistics.clientZombiesTotal = GameClient.IDToZombieMap.values().length;
        MPStatistics.clientZombiesLoaded = IsoWorld.instance.getCell().getZombieList().size();
        MPStatistics.clientZombiesSimulated = MPStatistics.clientZombiesUpdated / n;
        MPStatistics.clientZombiesAuthorized = NetworkZombieSimulator.getInstance().getAuthorizedZombieCount();
        MPStatistics.clientZombiesUnauthorized = NetworkZombieSimulator.getInstance().getUnauthorizedZombieCount();
        MPStatistics.clientZombiesReusable = VirtualZombieManager.instance.reusableZombiesSize();
        MPStatistics.clientZombiesCulled = 0;
        MPStatistics.clientZombiesUpdated = 0;
        MPStatistics.zombieUpdates = MovingObjectUpdateScheduler.instance.getFrameCounter();
        MPStatistics.serverZombiesCulled = 0;
    }
    
    private static void getServerZombieStatistics() {
        final int n = (int)Math.max(MovingObjectUpdateScheduler.instance.getFrameCounter() - MPStatistics.zombieUpdates, 1L);
        MPStatistics.serverZombiesTotal = ServerMap.instance.ZombieMap.size();
        MPStatistics.serverZombiesLoaded = IsoWorld.instance.getCell().getZombieList().size();
        MPStatistics.serverZombiesSimulated = MPStatistics.serverZombiesUpdated / n;
        MPStatistics.serverZombiesAuthorized = 0;
        MPStatistics.serverZombiesUnauthorized = NetworkZombieManager.getInstance().getUnauthorizedZombieCount();
        MPStatistics.serverZombiesReusable = VirtualZombieManager.instance.reusableZombiesSize();
        MPStatistics.serverZombiesCulled = 0;
        MPStatistics.serverZombiesUpdated = 0;
        MPStatistics.zombieUpdates = MovingObjectUpdateScheduler.instance.getFrameCounter();
    }
    
    private static void resetStatistic() {
        if (GameClient.bClient) {
            GameClient.connection.netStatistics = null;
        }
        else if (GameServer.bServer) {
            final Iterator<UdpConnection> iterator = GameServer.udpEngine.connections.iterator();
            while (iterator.hasNext()) {
                iterator.next().netStatistics = null;
            }
        }
        MPStatistics.serverRX = 0L;
        MPStatistics.serverTX = 0L;
        MPStatistics.serverLoss = 0.0;
        MPStatistics.serverFPS = 0.0f;
        MPStatistics.serverNetworkingFPS = 0L;
        MPStatistics.serverMemFree = 0L;
        MPStatistics.serverMemTotal = 0L;
        MPStatistics.serverMemUsed = 0L;
        MPStatistics.clientRX = 0L;
        MPStatistics.clientTX = 0L;
        MPStatistics.clientLoss = 0.0;
        MPStatistics.clientFPS = 0.0f;
        MPStatistics.clientMemFree = 0L;
        MPStatistics.clientMemTotal = 0L;
        MPStatistics.clientMemUsed = 0L;
        MPStatistics.serverZombiesTotal = 0;
        MPStatistics.serverZombiesLoaded = 0;
        MPStatistics.serverZombiesSimulated = 0;
        MPStatistics.serverZombiesCulled = 0;
        MPStatistics.serverZombiesAuthorized = 0;
        MPStatistics.serverZombiesUnauthorized = 0;
        MPStatistics.serverZombiesReusable = 0;
        MPStatistics.serverZombiesUpdated = 0;
        MPStatistics.clientZombiesTotal = 0;
        MPStatistics.clientZombiesLoaded = 0;
        MPStatistics.clientZombiesSimulated = 0;
        MPStatistics.clientZombiesCulled = 0;
        MPStatistics.clientZombiesAuthorized = 0;
        MPStatistics.clientZombiesUnauthorized = 0;
        MPStatistics.clientZombiesReusable = 0;
        MPStatistics.clientZombiesUpdated = 0;
    }
    
    private static void getClientStatistics() {
        try {
            MPStatistics.clientRX = 0L;
            MPStatistics.clientTX = 0L;
            MPStatistics.clientLoss = 0.0;
            final ZNetStatistics statistics = GameClient.connection.getStatistics();
            if (statistics != null) {
                MPStatistics.clientRX = statistics.lastActualBytesReceived / 1000L;
                MPStatistics.clientTX = statistics.lastActualBytesSent / 1000L;
                MPStatistics.clientLoss = statistics.packetlossLastSecond / 1000.0;
            }
            MPStatistics.clientFPS = 60.0f / GameTime.instance.FPSMultiplier;
            MPStatistics.clientMemFree = Runtime.getRuntime().freeMemory() / 1000L / 1000L;
            MPStatistics.clientMemTotal = Runtime.getRuntime().totalMemory() / 1000L / 1000L;
            MPStatistics.clientMemUsed = MPStatistics.clientMemTotal - MPStatistics.clientMemFree;
        }
        catch (Exception ex) {}
    }
    
    private static void getServerStatistics() {
        try {
            MPStatistics.serverRX = 0L;
            MPStatistics.serverTX = 0L;
            MPStatistics.serverLoss = 0.0;
            for (final UdpConnection udpConnection : GameServer.udpEngine.connections) {
                if (udpConnection.getStatistics() != null) {
                    MPStatistics.serverRX += udpConnection.netStatistics.lastActualBytesReceived;
                    MPStatistics.serverTX += udpConnection.netStatistics.lastActualBytesSent;
                    MPStatistics.serverLoss += udpConnection.netStatistics.packetlossLastSecond;
                }
            }
            MPStatistics.serverRX /= 1000L;
            MPStatistics.serverTX /= 1000L;
            MPStatistics.serverLoss /= 1000.0;
            MPStatistics.serverFPS = 60.0f / GameTime.instance.FPSMultiplier;
            MPStatistics.serverNetworkingFPS = 1000L * MPStatistics.serverNetworkingUpdates / 2000L;
            MPStatistics.serverNetworkingUpdates = 0L;
            MPStatistics.serverMemFree = Runtime.getRuntime().freeMemory() / 1000L / 1000L;
            MPStatistics.serverMemTotal = Runtime.getRuntime().totalMemory() / 1000L / 1000L;
            MPStatistics.serverMemUsed = MPStatistics.serverMemTotal - MPStatistics.serverMemFree;
        }
        catch (Exception ex) {}
    }
    
    private static void resetPingCounters() {
        MPStatistics.lastPing = -1;
        MPStatistics.avgPing = -1;
        MPStatistics.minPing = -1;
    }
    
    private static void getPing(final UdpConnection udpConnection) {
        try {
            if (udpConnection != null) {
                MPStatistics.lastPing = udpConnection.getLastPing();
                MPStatistics.avgPing = udpConnection.getAveragePing();
                MPStatistics.minPing = udpConnection.getLowestPing();
            }
        }
        catch (Exception ex) {}
    }
    
    private static void resetServerHandledPingCounters() {
        MPStatistics.serverHandledMinPing = 0L;
        MPStatistics.serverHandledMaxPing = 0L;
        MPStatistics.serverHandledAvgPing = 0L;
        MPStatistics.serverHandledLastPing = 0L;
        MPStatistics.serverHandledLossPing = 0L;
        MPStatistics.serverHandledPingPeriodStart = 0L;
        MPStatistics.serverHandledPingPacketIndex = 0;
        MPStatistics.serverHandledPingHistory.clear();
        MPStatistics.serverHandledLossPingHistory.clear();
    }
    
    private static void getServerHandledPing() {
        final long currentTimeMillis = System.currentTimeMillis();
        if (MPStatistics.serverHandledPingPacketIndex == 10L) {
            MPStatistics.serverHandledMinPing = MPStatistics.serverHandledPingHistory.stream().mapToLong(n -> n).min().orElse(0L);
            MPStatistics.serverHandledMaxPing = MPStatistics.serverHandledPingHistory.stream().mapToLong(n2 -> n2).max().orElse(0L);
            MPStatistics.serverHandledAvgPing = (long)MPStatistics.serverHandledPingHistory.stream().mapToLong(n3 -> n3).average().orElse(0.0);
            MPStatistics.serverHandledPingHistory.clear();
            MPStatistics.serverHandledPingPacketIndex = 0;
            final int size = MPStatistics.serverHandledLossPingHistory.size();
            MPStatistics.serverHandledLossPingHistory.removeIf(n4 -> currentTimeMillis > n4 + 10000L);
            MPStatistics.serverHandledLossPing += size - MPStatistics.serverHandledLossPingHistory.size();
            MPStatistics.serverHandledPingPeriodStart = currentTimeMillis;
        }
        GameClient.sendServerPing(currentTimeMillis);
        if (MPStatistics.serverHandledLossPingHistory.size() > 1000) {
            MPStatistics.serverHandledLossPingHistory.clear();
        }
        MPStatistics.serverHandledLossPingHistory.add(currentTimeMillis);
        ++MPStatistics.serverHandledPingPacketIndex;
    }
    
    public static void countServerNetworkingFPS() {
        ++MPStatistics.serverNetworkingUpdates;
    }
    
    public static void Reset() {
        resetPingCounters();
        resetServerHandledPingCounters();
        resetStatistic();
    }
    
    public static void Update() {
        if (GameClient.bClient) {
            if (MPStatistics.ulPing.Check()) {
                if (IsoPlayer.getInstance().isShowMPInfos() || DebugOptions.instance.MultiplayerPing.getValue()) {
                    getPing(GameClient.connection);
                    if (IsoPlayer.getInstance().isShowMPInfos()) {
                        getServerHandledPing();
                    }
                    else {
                        resetServerHandledPingCounters();
                    }
                }
                else {
                    resetPingCounters();
                    resetServerHandledPingCounters();
                }
            }
            if (IsoPlayer.getInstance().isShowMPInfos()) {
                if (MPStatistics.ulStatistics.Check()) {
                    getClientStatistics();
                    getClientZombieStatistics();
                }
            }
            else {
                resetStatistic();
            }
        }
        else if (GameServer.bServer) {
            if (MPStatistics.ulRequestTimeout.Check()) {
                MPStatistics.serverStatisticsEnabled = false;
            }
            if (MPStatistics.serverStatisticsEnabled) {
                if (MPStatistics.ulStatistics.Check()) {
                    getServerStatistics();
                    getServerZombieStatistics();
                }
            }
            else {
                resetStatistic();
            }
        }
    }
    
    public static void requested() {
        MPStatistics.serverStatisticsEnabled = true;
        MPStatistics.ulRequestTimeout.Reset(10000L);
    }
    
    public static void clientZombieCulled() {
        ++MPStatistics.clientZombiesCulled;
    }
    
    public static void serverZombieCulled() {
        ++MPStatistics.serverZombiesCulled;
    }
    
    public static void clientZombieUpdated() {
        ++MPStatistics.clientZombiesUpdated;
    }
    
    public static void serverZombieUpdated() {
        ++MPStatistics.serverZombiesUpdated;
    }
    
    public static void write(final UdpConnection udpConnection, final ByteBuffer byteBuffer) {
        byteBuffer.putLong(MPStatistics.serverMemFree);
        byteBuffer.putLong(MPStatistics.serverMemTotal);
        byteBuffer.putLong(MPStatistics.serverMemUsed);
        byteBuffer.putLong(MPStatistics.serverRX);
        byteBuffer.putLong(MPStatistics.serverTX);
        byteBuffer.putDouble(MPStatistics.serverLoss);
        byteBuffer.putFloat(MPStatistics.serverFPS);
        byteBuffer.putLong(MPStatistics.serverNetworkingFPS);
        byteBuffer.putInt(MPStatistics.serverZombiesTotal);
        byteBuffer.putInt(MPStatistics.serverZombiesLoaded);
        byteBuffer.putInt(MPStatistics.serverZombiesSimulated);
        byteBuffer.putInt(MPStatistics.serverZombiesCulled);
        byteBuffer.putInt(NetworkZombieManager.getInstance().getAuthorizedZombieCount(udpConnection));
        byteBuffer.putInt(MPStatistics.serverZombiesUnauthorized);
        byteBuffer.putInt(MPStatistics.serverZombiesReusable);
        GameWindow.WriteString(byteBuffer, "");
    }
    
    public static void parse(final ByteBuffer byteBuffer) {
        final long currentTimeMillis = System.currentTimeMillis();
        final long long1 = byteBuffer.getLong();
        MPStatistics.serverMemFree = byteBuffer.getLong();
        MPStatistics.serverMemTotal = byteBuffer.getLong();
        MPStatistics.serverMemUsed = byteBuffer.getLong();
        MPStatistics.serverRX = byteBuffer.getLong();
        MPStatistics.serverTX = byteBuffer.getLong();
        MPStatistics.serverLoss = byteBuffer.getDouble();
        MPStatistics.serverFPS = byteBuffer.getFloat();
        MPStatistics.serverNetworkingFPS = byteBuffer.getLong();
        MPStatistics.serverZombiesTotal = byteBuffer.getInt();
        MPStatistics.serverZombiesLoaded = byteBuffer.getInt();
        MPStatistics.serverZombiesSimulated = byteBuffer.getInt();
        MPStatistics.serverZombiesCulled += byteBuffer.getInt();
        MPStatistics.serverZombiesAuthorized = byteBuffer.getInt();
        MPStatistics.serverZombiesUnauthorized = byteBuffer.getInt();
        MPStatistics.serverZombiesReusable = byteBuffer.getInt();
        MPStatistics.serverRevision = GameWindow.ReadString(byteBuffer);
        MPStatistics.serverHandledLossPingHistory.remove(long1);
        if (long1 >= MPStatistics.serverHandledPingPeriodStart) {
            MPStatistics.serverHandledLastPing = currentTimeMillis - long1;
            MPStatistics.serverHandledPingHistory.add(MPStatistics.serverHandledLastPing);
        }
    }
    
    public static KahluaTable getLuaPing() {
        MPStatistics.pingTable.wipe();
        if (GameClient.bClient) {
            MPStatistics.pingTable.rawset((Object)"enabled", (Object)DebugOptions.instance.MultiplayerPing.getValue());
            MPStatistics.pingTable.rawset((Object)"lastPing", (Object)String.valueOf(MPStatistics.lastPing));
            MPStatistics.pingTable.rawset((Object)"avgPing", (Object)String.valueOf(MPStatistics.avgPing));
            MPStatistics.pingTable.rawset((Object)"minPing", (Object)String.valueOf(MPStatistics.minPing));
        }
        return MPStatistics.pingTable;
    }
    
    public static KahluaTable getLuaStatistics() {
        MPStatistics.statsTable.wipe();
        if (GameClient.bClient) {
            MPStatistics.statsTable.rawset((Object)"clientTime", (Object)String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())));
            MPStatistics.statsTable.rawset((Object)"serverTime", (Object)NumberFormat.getNumberInstance().format(TimeUnit.NANOSECONDS.toSeconds(GameTime.getServerTime())));
            MPStatistics.statsTable.rawset((Object)"clientRevision", (Object)String.valueOf(MPStatistics.serverRevision));
            MPStatistics.statsTable.rawset((Object)"serverRevision", (Object)String.valueOf(""));
            MPStatistics.statsTable.rawset((Object)"clientRX", (Object)String.valueOf(MPStatistics.clientRX));
            MPStatistics.statsTable.rawset((Object)"clientTX", (Object)String.valueOf(MPStatistics.clientTX));
            MPStatistics.statsTable.rawset((Object)"clientLoss", (Object)String.valueOf((int)MPStatistics.clientLoss));
            MPStatistics.statsTable.rawset((Object)"serverRX", (Object)String.valueOf(MPStatistics.serverRX));
            MPStatistics.statsTable.rawset((Object)"serverTX", (Object)String.valueOf(MPStatistics.serverTX));
            MPStatistics.statsTable.rawset((Object)"serverLoss", (Object)String.valueOf((int)MPStatistics.serverLoss));
            MPStatistics.statsTable.rawset((Object)"serverPingLast", (Object)String.valueOf(MPStatistics.serverHandledLastPing));
            MPStatistics.statsTable.rawset((Object)"serverPingMin", (Object)String.valueOf(MPStatistics.serverHandledMinPing));
            MPStatistics.statsTable.rawset((Object)"serverPingAvg", (Object)String.valueOf(MPStatistics.serverHandledAvgPing));
            MPStatistics.statsTable.rawset((Object)"serverPingMax", (Object)String.valueOf(MPStatistics.serverHandledMaxPing));
            MPStatistics.statsTable.rawset((Object)"serverPingLoss", (Object)String.valueOf(MPStatistics.serverHandledLossPing));
            MPStatistics.statsTable.rawset((Object)"clientMemTotal", (Object)String.valueOf(MPStatistics.clientMemTotal));
            MPStatistics.statsTable.rawset((Object)"clientMemUsed", (Object)String.valueOf(MPStatistics.clientMemUsed));
            MPStatistics.statsTable.rawset((Object)"clientMemFree", (Object)String.valueOf(MPStatistics.clientMemFree));
            MPStatistics.statsTable.rawset((Object)"serverMemTotal", (Object)String.valueOf(MPStatistics.serverMemTotal));
            MPStatistics.statsTable.rawset((Object)"serverMemUsed", (Object)String.valueOf(MPStatistics.serverMemUsed));
            MPStatistics.statsTable.rawset((Object)"serverMemFree", (Object)String.valueOf(MPStatistics.serverMemFree));
            MPStatistics.statsTable.rawset((Object)"serverNetworkingFPS", (Object)String.valueOf((int)MPStatistics.serverNetworkingFPS));
            MPStatistics.statsTable.rawset((Object)"serverFPS", (Object)String.valueOf((int)MPStatistics.serverFPS));
            MPStatistics.statsTable.rawset((Object)"clientFPS", (Object)String.valueOf((int)MPStatistics.clientFPS));
            MPStatistics.statsTable.rawset((Object)"serverZombiesTotal", (Object)String.valueOf(MPStatistics.serverZombiesTotal));
            MPStatistics.statsTable.rawset((Object)"serverZombiesLoaded", (Object)String.valueOf(MPStatistics.serverZombiesLoaded));
            MPStatistics.statsTable.rawset((Object)"serverZombiesSimulated", (Object)String.valueOf(MPStatistics.serverZombiesSimulated));
            MPStatistics.statsTable.rawset((Object)"serverZombiesCulled", (Object)String.valueOf(MPStatistics.serverZombiesCulled));
            MPStatistics.statsTable.rawset((Object)"serverZombiesAuthorized", (Object)String.valueOf(MPStatistics.serverZombiesAuthorized));
            MPStatistics.statsTable.rawset((Object)"serverZombiesUnauthorized", (Object)String.valueOf(MPStatistics.serverZombiesUnauthorized));
            MPStatistics.statsTable.rawset((Object)"serverZombiesReusable", (Object)String.valueOf(MPStatistics.serverZombiesReusable));
            MPStatistics.statsTable.rawset((Object)"clientZombiesTotal", (Object)String.valueOf(MPStatistics.clientZombiesTotal));
            MPStatistics.statsTable.rawset((Object)"clientZombiesLoaded", (Object)String.valueOf(MPStatistics.clientZombiesLoaded));
            MPStatistics.statsTable.rawset((Object)"clientZombiesSimulated", (Object)String.valueOf(MPStatistics.clientZombiesSimulated));
            MPStatistics.statsTable.rawset((Object)"clientZombiesCulled", (Object)String.valueOf(MPStatistics.clientZombiesCulled));
            MPStatistics.statsTable.rawset((Object)"clientZombiesAuthorized", (Object)String.valueOf(MPStatistics.clientZombiesAuthorized));
            MPStatistics.statsTable.rawset((Object)"clientZombiesUnauthorized", (Object)String.valueOf(MPStatistics.clientZombiesUnauthorized));
            MPStatistics.statsTable.rawset((Object)"clientZombiesReusable", (Object)String.valueOf(MPStatistics.clientZombiesReusable));
        }
        return MPStatistics.statsTable;
    }
    
    static {
        pingTable = LuaManager.platform.newTable();
        statsTable = LuaManager.platform.newTable();
        ulRequestTimeout = new UpdateLimit(10000L);
        ulStatistics = new UpdateLimit(2000L);
        ulPing = new UpdateLimit(1000L);
        MPStatistics.serverStatisticsEnabled = false;
        MPStatistics.lastPing = -1;
        MPStatistics.avgPing = -1;
        MPStatistics.minPing = -1;
        MPStatistics.serverMemTotal = 0L;
        MPStatistics.serverMemUsed = 0L;
        MPStatistics.serverMemFree = 0L;
        MPStatistics.serverRX = 0L;
        MPStatistics.serverTX = 0L;
        MPStatistics.serverLoss = 0.0;
        MPStatistics.serverFPS = 0.0f;
        MPStatistics.serverNetworkingUpdates = 0L;
        MPStatistics.serverNetworkingFPS = 0L;
        MPStatistics.serverRevision = "";
        MPStatistics.clientMemTotal = 0L;
        MPStatistics.clientMemUsed = 0L;
        MPStatistics.clientMemFree = 0L;
        MPStatistics.clientRX = 0L;
        MPStatistics.clientTX = 0L;
        MPStatistics.clientLoss = 0.0;
        MPStatistics.clientFPS = 0.0f;
        MPStatistics.serverZombiesTotal = 0;
        MPStatistics.serverZombiesLoaded = 0;
        MPStatistics.serverZombiesSimulated = 0;
        MPStatistics.serverZombiesCulled = 0;
        MPStatistics.serverZombiesAuthorized = 0;
        MPStatistics.serverZombiesUnauthorized = 0;
        MPStatistics.serverZombiesReusable = 0;
        MPStatistics.serverZombiesUpdated = 0;
        MPStatistics.clientZombiesTotal = 0;
        MPStatistics.clientZombiesLoaded = 0;
        MPStatistics.clientZombiesSimulated = 0;
        MPStatistics.clientZombiesCulled = 0;
        MPStatistics.clientZombiesAuthorized = 0;
        MPStatistics.clientZombiesUnauthorized = 0;
        MPStatistics.clientZombiesReusable = 0;
        MPStatistics.clientZombiesUpdated = 0;
        MPStatistics.zombieUpdates = 0L;
        MPStatistics.serverHandledMinPing = 0L;
        MPStatistics.serverHandledMaxPing = 0L;
        MPStatistics.serverHandledAvgPing = 0L;
        MPStatistics.serverHandledLastPing = 0L;
        MPStatistics.serverHandledLossPing = 0L;
        MPStatistics.serverHandledPingPeriodStart = 0L;
        MPStatistics.serverHandledPingPacketIndex = 0;
        serverHandledPingHistory = new ArrayList<Long>();
        serverHandledLossPingHistory = new HashSet<Long>();
    }
}

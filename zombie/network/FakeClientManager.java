// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network;

import java.util.function.BiConsumer;
import zombie.core.math.PZMath;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Collection;
import java.util.Set;
import com.google.common.collect.Sets;
import java.util.ArrayDeque;
import zombie.network.packets.ZombiePacket;
import zombie.iso.IsoUtils;
import zombie.core.Colors;
import zombie.core.Color;
import java.util.ArrayList;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import zombie.core.secure.PZcrypt;
import zombie.core.raknet.RakNetPeerInterface;
import java.util.zip.CRC32;
import zombie.network.packets.SyncInjuriesPacket;
import zombie.core.network.ByteBufferWriter;
import zombie.network.packets.PlayerPacket;
import zombie.characters.NetworkCharacter;
import zombie.core.utils.UpdateLimit;
import java.net.UnknownHostException;
import zombie.core.ThreadGroups;
import java.net.InetAddress;
import zombie.iso.Vector2;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.CharBuffer;
import java.util.function.Supplier;
import java.text.SimpleDateFormat;
import zombie.core.Core;
import java.util.Iterator;
import java.util.HashSet;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.core.znet.ZNet;
import zombie.core.Rand;
import java.util.Calendar;
import org.json.JSONArray;
import zombie.iso.IsoDirections;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.nio.ByteBuffer;
import java.text.DateFormat;

public class FakeClientManager
{
    private static final int SERVER_PORT = 16261;
    private static final int CLIENT_PORT = 17500;
    private static final String CLIENT_ADDRESS = "0.0.0.0";
    private static final String versionNumber;
    private static final DateFormat logDateFormat;
    private static final ThreadLocal<StringUTF> stringUTF;
    private static int logLevel;
    private static long startTime;
    
    public static String ReadStringUTF(final ByteBuffer byteBuffer) {
        return FakeClientManager.stringUTF.get().load(byteBuffer);
    }
    
    public static void WriteStringUTF(final ByteBuffer byteBuffer, final String s) {
        FakeClientManager.stringUTF.get().save(byteBuffer, s);
    }
    
    private static void sleep(final long n) {
        try {
            Thread.sleep(n);
        }
        catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    
    private static HashMap<Integer, Movement> load(final String first) {
        final HashMap<Integer, Movement> hashMap = new HashMap<Integer, Movement>();
        try {
            final JSONObject jsonObject = new JSONObject(new String(Files.readAllBytes(Paths.get(first, new String[0]))));
            Movement.version = jsonObject.getString("version");
            final JSONObject jsonObject2 = jsonObject.getJSONObject("config");
            final JSONObject jsonObject3 = jsonObject2.getJSONObject("client");
            final JSONObject jsonObject4 = jsonObject3.getJSONObject("connection");
            if (jsonObject4.has("serverHost")) {
                Client.connectionServerHost = jsonObject4.getString("serverHost");
            }
            Client.connectionInterval = jsonObject4.getLong("interval");
            Client.connectionTimeout = jsonObject4.getLong("timeout");
            Client.connectionDelay = jsonObject4.getLong("delay");
            final JSONObject jsonObject5 = jsonObject3.getJSONObject("statistics");
            Client.statisticsPeriod = jsonObject5.getInt("period");
            Client.statisticsClientID = Math.max(jsonObject5.getInt("id"), -1);
            if (jsonObject3.has("checksum")) {
                final JSONObject jsonObject6 = jsonObject3.getJSONObject("checksum");
                Client.luaChecksum = jsonObject6.getString("lua");
                Client.scriptChecksum = jsonObject6.getString("script");
            }
            if (jsonObject2.has("zombies")) {
                final JSONObject jsonObject7 = jsonObject2.getJSONObject("zombies");
                ZombieSimulator.Behaviour behaviour = ZombieSimulator.Behaviour.Normal;
                if (jsonObject7.has("behaviour")) {
                    behaviour = ZombieSimulator.Behaviour.valueOf(jsonObject7.getString("behaviour"));
                }
                ZombieSimulator.behaviour = behaviour;
                if (jsonObject7.has("maxZombiesPerUpdate")) {
                    ZombieSimulator.maxZombiesPerUpdate = jsonObject7.getInt("maxZombiesPerUpdate");
                }
                if (jsonObject7.has("deleteZombieDistance")) {
                    final int int1 = jsonObject7.getInt("deleteZombieDistance");
                    ZombieSimulator.deleteZombieDistanceSquared = int1 * int1;
                }
                if (jsonObject7.has("forgotZombieDistance")) {
                    final int int2 = jsonObject7.getInt("forgotZombieDistance");
                    ZombieSimulator.forgotZombieDistanceSquared = int2 * int2;
                }
                if (jsonObject7.has("canSeeZombieDistance")) {
                    final int int3 = jsonObject7.getInt("canSeeZombieDistance");
                    ZombieSimulator.canSeeZombieDistanceSquared = int3 * int3;
                }
                if (jsonObject7.has("seeZombieDistance")) {
                    final int int4 = jsonObject7.getInt("seeZombieDistance");
                    ZombieSimulator.seeZombieDistanceSquared = int4 * int4;
                }
                if (jsonObject7.has("canChangeTarget")) {
                    ZombieSimulator.canChangeTarget = jsonObject7.getBoolean("canChangeTarget");
                }
            }
            final JSONObject jsonObject8 = jsonObject2.getJSONObject("player");
            Player.fps = jsonObject8.getInt("fps");
            Player.predictInterval = jsonObject8.getInt("predict");
            if (jsonObject8.has("damage")) {
                Player.damage = (float)jsonObject8.getDouble("damage");
            }
            final JSONObject jsonObject9 = jsonObject2.getJSONObject("movement");
            Movement.defaultRadius = jsonObject9.getInt("radius");
            final JSONObject jsonObject10 = jsonObject9.getJSONObject("motion");
            Movement.aimSpeed = jsonObject10.getInt("aim");
            Movement.sneakSpeed = jsonObject10.getInt("sneak");
            Movement.sneakRunSpeed = jsonObject10.getInt("sneakrun");
            Movement.walkSpeed = jsonObject10.getInt("walk");
            Movement.runSpeed = jsonObject10.getInt("run");
            Movement.sprintSpeed = jsonObject10.getInt("sprint");
            final JSONObject jsonObject11 = jsonObject10.getJSONObject("pedestrian");
            Movement.pedestrianSpeedMin = jsonObject11.getInt("min");
            Movement.pedestrianSpeedMax = jsonObject11.getInt("max");
            final JSONObject jsonObject12 = jsonObject10.getJSONObject("vehicle");
            Movement.vehicleSpeedMin = jsonObject12.getInt("min");
            Movement.vehicleSpeedMax = jsonObject12.getInt("max");
            final JSONArray jsonArray = jsonObject.getJSONArray("movements");
            for (int i = 0; i < jsonArray.length(); ++i) {
                final JSONObject jsonObject13 = jsonArray.getJSONObject(i);
                final int int5 = jsonObject13.getInt("id");
                String string = null;
                if (jsonObject13.has("description")) {
                    string = jsonObject13.getString("description");
                }
                int int6 = (int)Math.round(Math.random() * 6000.0 + 6000.0);
                int int7 = (int)Math.round(Math.random() * 6000.0 + 6000.0);
                if (jsonObject13.has("spawn")) {
                    final JSONObject jsonObject14 = jsonObject13.getJSONObject("spawn");
                    int6 = jsonObject14.getInt("x");
                    int7 = jsonObject14.getInt("y");
                }
                Movement.Motion value = (Math.random() > 0.800000011920929) ? Movement.Motion.Vehicle : Movement.Motion.Pedestrian;
                if (jsonObject13.has("motion")) {
                    value = Movement.Motion.valueOf(jsonObject13.getString("motion"));
                }
                int n = 0;
                if (jsonObject13.has("speed")) {
                    n = jsonObject13.getInt("speed");
                }
                else {
                    switch (value) {
                        case Aim: {
                            n = Movement.aimSpeed;
                            break;
                        }
                        case Sneak: {
                            n = Movement.sneakSpeed;
                            break;
                        }
                        case SneakRun: {
                            n = Movement.sneakRunSpeed;
                            break;
                        }
                        case Walk: {
                            n = Movement.walkSpeed;
                            break;
                        }
                        case Run: {
                            n = Movement.runSpeed;
                            break;
                        }
                        case Sprint: {
                            n = Movement.sprintSpeed;
                            break;
                        }
                        case Pedestrian: {
                            n = (int)Math.round(Math.random() * (Movement.pedestrianSpeedMax - Movement.pedestrianSpeedMin) + Movement.pedestrianSpeedMin);
                            break;
                        }
                        case Vehicle: {
                            n = (int)Math.round(Math.random() * (Movement.vehicleSpeedMax - Movement.vehicleSpeedMin) + Movement.vehicleSpeedMin);
                            break;
                        }
                    }
                }
                Movement.Type type = Movement.Type.Line;
                if (jsonObject13.has("type")) {
                    type = Movement.Type.valueOf(jsonObject13.getString("type"));
                }
                int n2 = Movement.defaultRadius;
                if (jsonObject13.has("radius")) {
                    n2 = jsonObject13.getInt("radius");
                }
                IsoDirections isoDirections = IsoDirections.fromIndex((int)Math.round(Math.random() * 7.0));
                if (jsonObject13.has("direction")) {
                    isoDirections = IsoDirections.valueOf(jsonObject13.getString("direction"));
                }
                boolean boolean1 = false;
                if (jsonObject13.has("ghost")) {
                    boolean1 = jsonObject13.getBoolean("ghost");
                }
                long long1 = int5 * Client.connectionInterval;
                if (jsonObject13.has("connect")) {
                    long1 = jsonObject13.getLong("connect");
                }
                long long2 = 0L;
                if (jsonObject13.has("disconnect")) {
                    long2 = jsonObject13.getLong("disconnect");
                }
                long long3 = 0L;
                if (jsonObject13.has("reconnect")) {
                    long3 = jsonObject13.getLong("reconnect");
                }
                long long4 = 0L;
                if (jsonObject13.has("teleport")) {
                    long4 = jsonObject13.getLong("teleport");
                }
                int int8 = (int)Math.round(Math.random() * 6000.0 + 6000.0);
                int int9 = (int)Math.round(Math.random() * 6000.0 + 6000.0);
                if (jsonObject13.has("destination")) {
                    final JSONObject jsonObject15 = jsonObject13.getJSONObject("destination");
                    int8 = jsonObject15.getInt("x");
                    int9 = jsonObject15.getInt("y");
                }
                HordeCreator hordeCreator = null;
                if (jsonObject13.has("createHorde")) {
                    final JSONObject jsonObject16 = jsonObject13.getJSONObject("createHorde");
                    final int int10 = jsonObject16.getInt("count");
                    final int int11 = jsonObject16.getInt("radius");
                    final long long5 = jsonObject16.getLong("interval");
                    if (long5 != 0L) {
                        hordeCreator = new HordeCreator(int11, int10, long5);
                    }
                }
                SoundMaker soundMaker = null;
                if (jsonObject13.has("makeSound")) {
                    final JSONObject jsonObject17 = jsonObject13.getJSONObject("makeSound");
                    final int int12 = jsonObject17.getInt("interval");
                    final int int13 = jsonObject17.getInt("radius");
                    final String string2 = jsonObject17.getString("message");
                    if (int12 != 0) {
                        soundMaker = new SoundMaker(int12, int13, string2);
                    }
                }
                final Movement value2 = new Movement(int5, string, int6, int7, value, n, type, n2, int8, int9, isoDirections, boolean1, long1, long2, long3, long4, hordeCreator, soundMaker);
                if (hashMap.containsKey(int5)) {
                    error(int5, String.format("Client %d already exists", value2.id));
                }
                else {
                    hashMap.put(int5, value2);
                }
            }
        }
        catch (Exception ex) {
            error(-1, "Scenarios file load failed");
            ex.printStackTrace();
        }
        finally {
            return hashMap;
        }
    }
    
    private static void error(final int i, final String s) {
        System.out.print(String.format("%5s : %s , [%2d] > %s\n", "ERROR", FakeClientManager.logDateFormat.format(Calendar.getInstance().getTime()), i, s));
    }
    
    private static void info(final int i, final String s) {
        if (FakeClientManager.logLevel >= 0) {
            System.out.print(String.format("%5s : %s , [%2d] > %s\n", "INFO", FakeClientManager.logDateFormat.format(Calendar.getInstance().getTime()), i, s));
        }
    }
    
    private static void log(final int i, final String s) {
        if (FakeClientManager.logLevel >= 1) {
            System.out.print(String.format("%5s : %s , [%2d] > %s\n", "LOG", FakeClientManager.logDateFormat.format(Calendar.getInstance().getTime()), i, s));
        }
    }
    
    private static void trace(final int i, final String s) {
        if (FakeClientManager.logLevel >= 2) {
            System.out.print(String.format("%5s : %s , [%2d] > %s\n", "TRACE", FakeClientManager.logDateFormat.format(Calendar.getInstance().getTime()), i, s));
        }
    }
    
    public static void main(final String[] array) {
        String trim = null;
        int int1 = -1;
        for (int i = 0; i < array.length; ++i) {
            if (array[i].startsWith("-scenarios=")) {
                trim = array[i].replace("-scenarios=", "").trim();
            }
            else if (array[i].startsWith("-id=")) {
                int1 = Integer.parseInt(array[i].replace("-id=", "").trim());
            }
        }
        if (trim == null || trim.isBlank()) {
            error(-1, "Invalid scenarios file name");
            System.exit(0);
        }
        Rand.init();
        System.loadLibrary("RakNet64");
        System.loadLibrary("ZNetNoSteam64");
        try {
            final String property = System.getProperty("zomboid.znetlog");
            if (property != null) {
                FakeClientManager.logLevel = Integer.parseInt(property);
                ZNet.init();
                ZNet.setLogLevel(FakeClientManager.logLevel);
            }
        }
        catch (NumberFormatException ex) {
            error(-1, "Invalid log arguments");
        }
        DebugLog.disableLog(DebugType.General);
        final HashMap<Integer, Movement> load = load(trim);
        int n;
        Network network;
        if (int1 != -1) {
            n = 17500 + int1;
            network = new Network(load.size(), n);
        }
        else {
            n = 17500;
            network = new Network(load.size(), n);
        }
        if (network.isStarted()) {
            final HashSet<Player> set = new HashSet<Player>();
            int n2 = 0;
            if (int1 != -1) {
                final Movement movement = load.get(int1);
                if (movement != null) {
                    set.add(new Player(movement, network, n2, n));
                }
                else {
                    error(int1, "Client movement not found");
                }
            }
            else {
                final Iterator<Movement> iterator = load.values().iterator();
                while (iterator.hasNext()) {
                    set.add(new Player(iterator.next(), network, n2++, n));
                }
            }
            while (!set.isEmpty()) {
                sleep(1000L);
            }
        }
    }
    
    static {
        versionNumber = Core.getInstance().getVersionNumber();
        logDateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
        stringUTF = ThreadLocal.withInitial((Supplier<? extends StringUTF>)StringUTF::new);
        FakeClientManager.logLevel = 0;
        FakeClientManager.startTime = System.currentTimeMillis();
    }
    
    private static class StringUTF
    {
        private char[] chars;
        private ByteBuffer byteBuffer;
        private CharBuffer charBuffer;
        private CharsetEncoder ce;
        private CharsetDecoder cd;
        
        private int encode(final String s) {
            if (this.chars == null || this.chars.length < s.length()) {
                this.chars = new char[(s.length() + 128 - 1) / 128 * 128];
                this.charBuffer = CharBuffer.wrap(this.chars);
            }
            s.getChars(0, s.length(), this.chars, 0);
            this.charBuffer.limit(s.length());
            this.charBuffer.position(0);
            if (this.ce == null) {
                this.ce = StandardCharsets.UTF_8.newEncoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
            }
            this.ce.reset();
            final int capacity = ((int)(s.length() * (double)this.ce.maxBytesPerChar()) + 128 - 1) / 128 * 128;
            if (this.byteBuffer == null || this.byteBuffer.capacity() < capacity) {
                this.byteBuffer = ByteBuffer.allocate(capacity);
            }
            this.byteBuffer.clear();
            this.ce.encode(this.charBuffer, this.byteBuffer, true);
            return this.byteBuffer.position();
        }
        
        private String decode(final int n) {
            if (this.cd == null) {
                this.cd = StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
            }
            this.cd.reset();
            final int n2 = (int)(n * (double)this.cd.maxCharsPerByte());
            if (this.chars == null || this.chars.length < n2) {
                this.chars = new char[(n2 + 128 - 1) / 128 * 128];
                this.charBuffer = CharBuffer.wrap(this.chars);
            }
            this.charBuffer.clear();
            this.cd.decode(this.byteBuffer, this.charBuffer, true);
            return new String(this.chars, 0, this.charBuffer.position());
        }
        
        void save(final ByteBuffer byteBuffer, final String s) {
            if (s == null || s.isEmpty()) {
                byteBuffer.putShort((short)0);
                return;
            }
            byteBuffer.putShort((short)this.encode(s));
            this.byteBuffer.flip();
            byteBuffer.put(this.byteBuffer);
        }
        
        String load(final ByteBuffer src) {
            final short short1 = src.getShort();
            if (short1 <= 0) {
                return "";
            }
            final int capacity = (short1 + 128 - 1) / 128 * 128;
            if (this.byteBuffer == null || this.byteBuffer.capacity() < capacity) {
                this.byteBuffer = ByteBuffer.allocate(capacity);
            }
            this.byteBuffer.clear();
            if (src.remaining() < short1) {
                DebugLog.General.error(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, short1, src.remaining()));
            }
            final int limit = src.limit();
            src.limit(src.position() + short1);
            this.byteBuffer.put(src);
            src.limit(limit);
            this.byteBuffer.flip();
            return this.decode(short1);
        }
    }
    
    private static class Movement
    {
        static String version;
        static int defaultRadius;
        static int aimSpeed;
        static int sneakSpeed;
        static int walkSpeed;
        static int sneakRunSpeed;
        static int runSpeed;
        static int sprintSpeed;
        static int pedestrianSpeedMin;
        static int pedestrianSpeedMax;
        static int vehicleSpeedMin;
        static int vehicleSpeedMax;
        static final float zombieLungeDistanceSquared = 100.0f;
        static final float zombieWalkSpeed = 3.0f;
        static final float zombieLungeSpeed = 6.0f;
        final int id;
        final String description;
        final Vector2 spawn;
        Motion motion;
        float speed;
        final Type type;
        final int radius;
        final IsoDirections direction;
        final Vector2 destination;
        final boolean ghost;
        final long connectDelay;
        final long disconnectDelay;
        final long reconnectDelay;
        final long teleportDelay;
        final HordeCreator hordeCreator;
        SoundMaker soundMaker;
        long timestamp;
        
        public Movement(final int id, final String description, final int n, final int n2, final Motion motion, final int n3, final Type type, final int radius, final int n4, final int n5, final IsoDirections direction, final boolean ghost, final long connectDelay, final long disconnectDelay, final long reconnectDelay, final long teleportDelay, final HordeCreator hordeCreator, final SoundMaker soundMaker) {
            this.id = id;
            this.description = description;
            this.spawn = new Vector2((float)n, (float)n2);
            this.motion = motion;
            this.speed = (float)n3;
            this.type = type;
            this.radius = radius;
            this.direction = direction;
            this.destination = new Vector2((float)n4, (float)n5);
            this.ghost = ghost;
            this.connectDelay = connectDelay;
            this.disconnectDelay = disconnectDelay;
            this.reconnectDelay = reconnectDelay;
            this.teleportDelay = teleportDelay;
            this.hordeCreator = hordeCreator;
            this.soundMaker = soundMaker;
        }
        
        public void connect(final int n) {
            final long currentTimeMillis = System.currentTimeMillis();
            if (this.disconnectDelay != 0L) {
                FakeClientManager.info(this.id, String.format("Player %3d connect in %.3fs, disconnect in %.3fs", n, (currentTimeMillis - this.timestamp) / 1000.0f, this.disconnectDelay / 1000.0f));
            }
            else {
                FakeClientManager.info(this.id, String.format("Player %3d connect in %.3fs", n, (currentTimeMillis - this.timestamp) / 1000.0f));
            }
            this.timestamp = currentTimeMillis;
        }
        
        public void disconnect(final int n) {
            final long currentTimeMillis = System.currentTimeMillis();
            if (this.reconnectDelay != 0L) {
                FakeClientManager.info(this.id, String.format("Player %3d disconnect in %.3fs, reconnect in %.3fs", n, (currentTimeMillis - this.timestamp) / 1000.0f, this.reconnectDelay / 1000.0f));
            }
            else {
                FakeClientManager.info(this.id, String.format("Player %3d disconnect in %.3fs", n, (currentTimeMillis - this.timestamp) / 1000.0f));
            }
            this.timestamp = currentTimeMillis;
        }
        
        public boolean doTeleport() {
            return this.teleportDelay != 0L;
        }
        
        public boolean doDisconnect() {
            return this.disconnectDelay != 0L;
        }
        
        public boolean checkDisconnect() {
            return System.currentTimeMillis() - this.timestamp > this.disconnectDelay;
        }
        
        public boolean doReconnect() {
            return this.reconnectDelay != 0L;
        }
        
        public boolean checkReconnect() {
            return System.currentTimeMillis() - this.timestamp > this.reconnectDelay;
        }
        
        static {
            Movement.defaultRadius = 150;
            Movement.aimSpeed = 4;
            Movement.sneakSpeed = 6;
            Movement.walkSpeed = 7;
            Movement.sneakRunSpeed = 10;
            Movement.runSpeed = 13;
            Movement.sprintSpeed = 19;
            Movement.pedestrianSpeedMin = 5;
            Movement.pedestrianSpeedMax = 20;
            Movement.vehicleSpeedMin = 40;
            Movement.vehicleSpeedMax = 80;
        }
        
        private enum Type
        {
            Stay, 
            Line, 
            Circle, 
            AIAttackZombies, 
            AIRunAwayFromZombies, 
            AIRunToAnotherPlayers, 
            AINormal;
            
            private static /* synthetic */ Type[] $values() {
                return new Type[] { Type.Stay, Type.Line, Type.Circle, Type.AIAttackZombies, Type.AIRunAwayFromZombies, Type.AIRunToAnotherPlayers, Type.AINormal };
            }
            
            static {
                $VALUES = $values();
            }
        }
        
        private enum Motion
        {
            Aim, 
            Sneak, 
            Walk, 
            SneakRun, 
            Run, 
            Sprint, 
            Pedestrian, 
            Vehicle;
            
            private static /* synthetic */ Motion[] $values() {
                return new Motion[] { Motion.Aim, Motion.Sneak, Motion.Walk, Motion.SneakRun, Motion.Run, Motion.Sprint, Motion.Pedestrian, Motion.Vehicle };
            }
            
            static {
                $VALUES = $values();
            }
        }
    }
    
    private static class Client
    {
        private static String connectionServerHost;
        private static long connectionInterval;
        private static long connectionTimeout;
        private static long connectionDelay;
        private static int statisticsClientID;
        private static int statisticsPeriod;
        private static long serverTimeShift;
        private static boolean serverTimeShiftIsSet;
        private final HashMap<Integer, Request> requests;
        private final Player player;
        private final Network network;
        private final int connectionIndex;
        private final int port;
        private long connectionGUID;
        private int requestId;
        private long stateTime;
        private State state;
        private String host;
        public static String luaChecksum;
        public static String scriptChecksum;
        
        private Client(final Player player, final Network network, final int connectionIndex, final int port) {
            this.requests = new HashMap<Integer, Request>();
            this.connectionGUID = -1L;
            this.requestId = 0;
            this.connectionIndex = connectionIndex;
            this.network = network;
            this.player = player;
            this.port = port;
            try {
                this.host = InetAddress.getByName(Client.connectionServerHost).getHostAddress();
                this.state = State.CONNECT;
                final Thread thread = new Thread(ThreadGroups.Workers, this::updateThread, this.player.username);
                thread.setDaemon(true);
                thread.start();
            }
            catch (UnknownHostException ex) {
                this.state = State.QUIT;
                ex.printStackTrace();
            }
        }
        
        private void updateThread() {
            FakeClientManager.info(this.player.movement.id, String.format("Start client (%d) %s:%d => %s:%d / \"%s\"", this.connectionIndex, "0.0.0.0", this.port, this.host, 16261, this.player.movement.description));
            FakeClientManager.sleep(this.player.movement.connectDelay);
            switch (this.player.movement.type) {
                case Circle: {
                    this.player.circleMovement();
                    break;
                }
                case Line: {
                    this.player.lineMovement();
                    break;
                }
                case AIAttackZombies: {
                    this.player.aiAttackZombiesMovement();
                    break;
                }
                case AIRunAwayFromZombies: {
                    this.player.aiRunAwayFromZombiesMovement();
                    break;
                }
                case AIRunToAnotherPlayers: {
                    this.player.aiRunToAnotherPlayersMovement();
                    break;
                }
                case AINormal: {
                    this.player.aiNormalMovement();
                    break;
                }
            }
            while (this.state != State.QUIT) {
                this.update();
                FakeClientManager.sleep(1L);
            }
            FakeClientManager.info(this.player.movement.id, String.format("Stop client (%d) %s:%d => %s:%d / \"%s\"", this.connectionIndex, "0.0.0.0", this.port, this.host, 16261, this.player.movement.description));
        }
        
        private void updateTime() {
            this.stateTime = System.currentTimeMillis();
        }
        
        private long getServerTime() {
            return Client.serverTimeShiftIsSet ? (System.nanoTime() + Client.serverTimeShift) : 0L;
        }
        
        private boolean checkConnectionTimeout() {
            return System.currentTimeMillis() - this.stateTime > Client.connectionTimeout;
        }
        
        private boolean checkConnectionDelay() {
            return System.currentTimeMillis() - this.stateTime > Client.connectionDelay;
        }
        
        private void changeState(final State state) {
            this.updateTime();
            FakeClientManager.log(this.player.movement.id, String.format("%s >> %s", this.state, state));
            if (State.RUN.equals(state)) {
                this.player.movement.connect(this.player.OnlineID);
                if (this.player.teleportLimiter == null) {
                    this.player.teleportLimiter = new UpdateLimit(this.player.movement.teleportDelay);
                }
                if (this.player.movement.id == Client.statisticsClientID) {
                    this.sendTimeSync();
                    this.sendInjuries();
                    this.sendStatisticsEnable(Client.statisticsPeriod);
                }
            }
            else if (State.DISCONNECT.equals(state) && !State.DISCONNECT.equals(this.state)) {
                this.player.movement.disconnect(this.player.OnlineID);
            }
            this.state = state;
        }
        
        private void update() {
            switch (this.state) {
                case CONNECT: {
                    this.player.movement.timestamp = System.currentTimeMillis();
                    this.network.connect(this.player.movement.id, this.host);
                    this.changeState(State.WAIT);
                    break;
                }
                case LOGIN: {
                    this.sendPlayerLogin();
                    this.changeState(State.WAIT);
                    break;
                }
                case PLAYER_CONNECT: {
                    this.sendPlayerConnect();
                    this.changeState(State.WAIT);
                    break;
                }
                case CHECKSUM: {
                    this.sendChecksum();
                    this.changeState(State.WAIT);
                    break;
                }
                case PLAYER_EXTRA_INFO: {
                    this.sendPlayerExtraInfo(this.player.movement.ghost, this.player.movement.hordeCreator != null);
                    this.sendEquip();
                    this.changeState(State.WAIT);
                    break;
                }
                case LOAD: {
                    this.requestId = 0;
                    this.requests.clear();
                    this.requestFullUpdate();
                    this.requestLargeAreaZip();
                    this.changeState(State.WAIT);
                    break;
                }
                case RUN: {
                    if (this.player.movement.doDisconnect() && this.player.movement.checkDisconnect()) {
                        this.changeState(State.DISCONNECT);
                        break;
                    }
                    this.player.run();
                    break;
                }
                case WAIT: {
                    if (this.checkConnectionTimeout()) {
                        this.changeState(State.DISCONNECT);
                        break;
                    }
                    break;
                }
                case DISCONNECT: {
                    if (this.network.isConnected()) {
                        this.player.movement.timestamp = System.currentTimeMillis();
                        this.network.disconnect(this.connectionGUID, this.player.movement.id, this.host);
                    }
                    if ((this.player.movement.doReconnect() && this.player.movement.checkReconnect()) || (!this.player.movement.doReconnect() && this.checkConnectionDelay())) {
                        this.changeState(State.CONNECT);
                        break;
                    }
                    break;
                }
            }
        }
        
        private void receive(final short s, final ByteBuffer byteBuffer) {
            final PacketTypes.PacketType packetType = PacketTypes.packetTypes.get(s);
            Network.logUserPacket(this.player.movement.id, s);
            switch (packetType) {
                case PlayerConnect: {
                    if (!this.receivePlayerConnect(byteBuffer)) {
                        break;
                    }
                    if (Client.luaChecksum.isEmpty()) {
                        this.changeState(State.PLAYER_EXTRA_INFO);
                        break;
                    }
                    this.changeState(State.CHECKSUM);
                    break;
                }
                case ConnectionDetails: {
                    this.changeState(State.LOAD);
                    break;
                }
                case ExtraInfo: {
                    if (this.receivePlayerExtraInfo(byteBuffer)) {
                        this.changeState(State.RUN);
                        break;
                    }
                    break;
                }
                case SentChunk: {
                    if (this.state != State.WAIT || !this.receiveChunkPart(byteBuffer)) {
                        break;
                    }
                    this.updateTime();
                    if (this.allChunkPartsReceived()) {
                        this.changeState(State.PLAYER_CONNECT);
                        break;
                    }
                    break;
                }
                case NotRequiredInZip: {
                    if (this.state != State.WAIT || !this.receiveNotRequired(byteBuffer)) {
                        break;
                    }
                    this.updateTime();
                    if (this.allChunkPartsReceived()) {
                        this.changeState(State.PLAYER_CONNECT);
                        break;
                    }
                    break;
                }
                case StatisticRequest: {
                    this.receiveStatistics(byteBuffer);
                    break;
                }
                case TimeSync: {
                    this.receiveTimeSync(byteBuffer);
                    break;
                }
                case SyncClock: {
                    this.receiveSyncClock(byteBuffer);
                    break;
                }
                case ZombieSimulation:
                case ZombieSimulationReliable: {
                    this.receiveZombieSimulation(byteBuffer);
                    break;
                }
                case PlayerUpdate:
                case PlayerUpdateReliable: {
                    this.player.playerManager.parsePlayer(byteBuffer);
                    break;
                }
                case PlayerTimeout: {
                    this.player.playerManager.parsePlayerTimeout(byteBuffer);
                    break;
                }
                case Kicked: {
                    this.receiveKicked(byteBuffer);
                    break;
                }
                case Checksum: {
                    this.receiveChecksum(byteBuffer);
                    break;
                }
                case KillZombie: {
                    this.receiveKillZombie(byteBuffer);
                    break;
                }
                case Teleport: {
                    this.receiveTeleport(byteBuffer);
                    break;
                }
            }
            byteBuffer.clear();
        }
        
        private void doPacket(final short n, final ByteBuffer byteBuffer) {
            byteBuffer.put((byte)(-122));
            byteBuffer.putShort(n);
        }
        
        private void putUTF(final ByteBuffer byteBuffer, final String s) {
            if (s == null) {
                byteBuffer.putShort((short)0);
            }
            else {
                final byte[] bytes = s.getBytes();
                byteBuffer.putShort((short)bytes.length);
                byteBuffer.put(bytes);
            }
        }
        
        private void putBoolean(final ByteBuffer byteBuffer, final boolean b) {
            byteBuffer.put((byte)(b ? 1 : 0));
        }
        
        private void sendPlayerLogin() {
            final ByteBuffer startPacket = this.network.startPacket();
            this.doPacket(PacketTypes.PacketType.Login.getId(), startPacket);
            this.putUTF(startPacket, this.player.username);
            this.putUTF(startPacket, this.player.username);
            this.putUTF(startPacket, FakeClientManager.versionNumber);
            this.network.endPacketImmediate(this.connectionGUID);
        }
        
        private void sendPlayerConnect() {
            final ByteBuffer startPacket = this.network.startPacket();
            this.doPacket(PacketTypes.PacketType.PlayerConnect.getId(), startPacket);
            this.writePlayerConnectData(startPacket);
            this.network.endPacketImmediate(this.connectionGUID);
        }
        
        private void writePlayerConnectData(final ByteBuffer byteBuffer) {
            byteBuffer.put((byte)0);
            byteBuffer.put((byte)13);
            byteBuffer.putFloat(this.player.x);
            byteBuffer.putFloat(this.player.y);
            byteBuffer.putFloat(this.player.z);
            byteBuffer.putInt(0);
            this.putUTF(byteBuffer, this.player.username);
            this.putUTF(byteBuffer, this.player.username);
            this.putUTF(byteBuffer, (this.player.isFemale == 0) ? "Kate" : "Male");
            byteBuffer.putInt(this.player.isFemale);
            this.putUTF(byteBuffer, "fireofficer");
            byteBuffer.putInt(0);
            byteBuffer.putInt(4);
            this.putUTF(byteBuffer, "Sprinting");
            byteBuffer.putInt(1);
            this.putUTF(byteBuffer, "Fitness");
            byteBuffer.putInt(6);
            this.putUTF(byteBuffer, "Strength");
            byteBuffer.putInt(6);
            this.putUTF(byteBuffer, "Axe");
            byteBuffer.putInt(1);
            byteBuffer.put((byte)0);
            byteBuffer.put((byte)0);
            byteBuffer.put((byte)Math.round(Math.random() * 5.0));
            byteBuffer.put((byte)0);
            byteBuffer.put((byte)0);
            byteBuffer.put((byte)0);
            byteBuffer.put((byte)0);
            byteBuffer.put((byte)this.player.clothes.size());
            for (final Player.Clothes clothes : this.player.clothes) {
                byteBuffer.put(clothes.flags);
                this.putUTF(byteBuffer, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, clothes.name));
                this.putUTF(byteBuffer, null);
                this.putUTF(byteBuffer, clothes.name);
                byteBuffer.put((byte)(-1));
                byteBuffer.put((byte)(-1));
                byteBuffer.put((byte)(-1));
                byteBuffer.put(clothes.text);
                byteBuffer.putFloat(0.0f);
                byteBuffer.put((byte)0);
                byteBuffer.put((byte)0);
                byteBuffer.put((byte)0);
                byteBuffer.put((byte)0);
                byteBuffer.put((byte)0);
                byteBuffer.put((byte)0);
            }
            this.putUTF(byteBuffer, "fake_str");
            byteBuffer.putShort((short)0);
            byteBuffer.putInt(2);
            this.putUTF(byteBuffer, "Fit");
            this.putUTF(byteBuffer, "Stout");
            byteBuffer.putFloat(0.0f);
            byteBuffer.putInt(0);
            byteBuffer.putInt(0);
            byteBuffer.putInt(4);
            this.putUTF(byteBuffer, "Sprinting");
            byteBuffer.putFloat(75.0f);
            this.putUTF(byteBuffer, "Fitness");
            byteBuffer.putFloat(67500.0f);
            this.putUTF(byteBuffer, "Strength");
            byteBuffer.putFloat(67500.0f);
            this.putUTF(byteBuffer, "Axe");
            byteBuffer.putFloat(75.0f);
            byteBuffer.putInt(4);
            this.putUTF(byteBuffer, "Sprinting");
            byteBuffer.putInt(1);
            this.putUTF(byteBuffer, "Fitness");
            byteBuffer.putInt(6);
            this.putUTF(byteBuffer, "Strength");
            byteBuffer.putInt(6);
            this.putUTF(byteBuffer, "Axe");
            byteBuffer.putInt(1);
            byteBuffer.putInt(0);
            this.putBoolean(byteBuffer, true);
            this.putUTF(byteBuffer, "fake");
            byteBuffer.putFloat(this.player.tagColor.r);
            byteBuffer.putFloat(this.player.tagColor.g);
            byteBuffer.putFloat(this.player.tagColor.b);
            byteBuffer.putInt(0);
            byteBuffer.putDouble(0.0);
            byteBuffer.putInt(0);
            this.putUTF(byteBuffer, this.player.username);
            byteBuffer.putFloat(this.player.speakColor.r);
            byteBuffer.putFloat(this.player.speakColor.g);
            byteBuffer.putFloat(this.player.speakColor.b);
            this.putBoolean(byteBuffer, true);
            this.putBoolean(byteBuffer, false);
            byteBuffer.put((byte)0);
            byteBuffer.put((byte)0);
            byteBuffer.putInt(0);
            byteBuffer.putInt(0);
        }
        
        private void sendPlayerExtraInfo(final boolean b, final boolean b2) {
            final ByteBuffer startPacket = this.network.startPacket();
            this.doPacket(PacketTypes.PacketType.ExtraInfo.getId(), startPacket);
            startPacket.putShort(this.player.OnlineID);
            this.putUTF(startPacket, b2 ? "admin" : "");
            startPacket.put((byte)0);
            startPacket.put((byte)(b ? 1 : 0));
            startPacket.put((byte)0);
            startPacket.put((byte)0);
            startPacket.put((byte)0);
            startPacket.put((byte)0);
            this.network.endPacketImmediate(this.connectionGUID);
        }
        
        private void sendEquip() {
            final ByteBuffer startPacket = this.network.startPacket();
            this.doPacket(PacketTypes.PacketType.Equip.getId(), startPacket);
            startPacket.put((byte)0);
            startPacket.put((byte)0);
            startPacket.put((byte)1);
            startPacket.putInt(16);
            startPacket.putShort(this.player.registry_id);
            startPacket.put((byte)1);
            startPacket.putInt(this.player.weapon_id);
            startPacket.put((byte)0);
            startPacket.putInt(0);
            startPacket.putInt(0);
            startPacket.put((byte)0);
            this.network.endPacketImmediate(this.connectionGUID);
        }
        
        private void sendChatMessage(final String s) {
            final ByteBuffer startPacket = this.network.startPacket();
            startPacket.putShort(this.player.OnlineID);
            startPacket.putInt(2);
            this.putUTF(startPacket, this.player.username);
            this.putUTF(startPacket, s);
            this.network.endPacketImmediate(this.connectionGUID);
        }
        
        private int getBooleanVariables() {
            int n = 0;
            if (this.player.movement.speed > 0.0f) {
                switch (this.player.movement.motion) {
                    case Aim: {
                        n |= 0x40;
                        break;
                    }
                    case Sneak: {
                        n |= 0x1;
                        break;
                    }
                    case SneakRun: {
                        n |= 0x11;
                        break;
                    }
                    case Run: {
                        n |= 0x10;
                        break;
                    }
                    case Sprint: {
                        n |= 0x20;
                        break;
                    }
                }
                n |= 0x4400;
            }
            return n;
        }
        
        private void sendPlayer(final NetworkCharacter.Transform transform, final int realt, final Vector2 vector2) {
            final PlayerPacket playerPacket = new PlayerPacket();
            playerPacket.id = this.player.OnlineID;
            playerPacket.x = transform.position.x;
            playerPacket.y = transform.position.y;
            playerPacket.z = (byte)this.player.z;
            playerPacket.direction = vector2.getDirection();
            playerPacket.usePathFinder = false;
            playerPacket.moveType = NetworkVariables.PredictionTypes.None;
            playerPacket.VehicleID = -1;
            playerPacket.VehicleSeat = -1;
            playerPacket.booleanVariables = this.getBooleanVariables();
            playerPacket.footstepSoundRadius = 0;
            playerPacket.bleedingLevel = 0;
            playerPacket.realx = this.player.x;
            playerPacket.realy = this.player.y;
            playerPacket.realz = (byte)this.player.z;
            playerPacket.realdir = (byte)IsoDirections.fromAngleActual(this.player.direction).index();
            playerPacket.realt = realt;
            playerPacket.collidePointX = -1.0f;
            playerPacket.collidePointY = -1.0f;
            final ByteBuffer startPacket = this.network.startPacket();
            this.doPacket(PacketTypes.PacketType.PlayerUpdateReliable.getId(), startPacket);
            playerPacket.write(new ByteBufferWriter(startPacket));
            this.network.endPacket(this.connectionGUID);
        }
        
        private boolean receivePlayerConnect(final ByteBuffer byteBuffer) {
            if (byteBuffer.getShort() == -1) {
                byteBuffer.get();
                this.player.OnlineID = byteBuffer.getShort();
                return true;
            }
            return false;
        }
        
        private boolean receivePlayerExtraInfo(final ByteBuffer byteBuffer) {
            return byteBuffer.getShort() == this.player.OnlineID;
        }
        
        private boolean receiveChunkPart(final ByteBuffer byteBuffer) {
            boolean b = false;
            final int int1 = byteBuffer.getInt();
            byteBuffer.getInt();
            byteBuffer.getInt();
            byteBuffer.getInt();
            byteBuffer.getInt();
            byteBuffer.getInt();
            if (this.requests.remove(int1) != null) {
                b = true;
            }
            return b;
        }
        
        private boolean receiveNotRequired(final ByteBuffer byteBuffer) {
            boolean b = false;
            for (int int1 = byteBuffer.getInt(), i = 0; i < int1; ++i) {
                final int int2 = byteBuffer.getInt();
                final boolean b2 = byteBuffer.get() == 1;
                if (this.requests.remove(int2) != null) {
                    b = true;
                }
            }
            return b;
        }
        
        private boolean allChunkPartsReceived() {
            return this.requests.size() == 0;
        }
        
        private void addChunkRequest(final int n, final int n2, final int n3, final int n4) {
            final Request value = new Request(n, n2, this.requestId);
            ++this.requestId;
            this.requests.put(value.id, value);
        }
        
        private void requestZipList() {
            final ByteBuffer startPacket = this.network.startPacket();
            this.doPacket(PacketTypes.PacketType.RequestZipList.getId(), startPacket);
            startPacket.putInt(this.requests.size());
            for (final Request request : this.requests.values()) {
                startPacket.putInt(request.id);
                startPacket.putInt(request.wx);
                startPacket.putInt(request.wy);
                startPacket.putLong(request.crc);
            }
            this.network.endPacket(this.connectionGUID);
        }
        
        private void requestLargeAreaZip() {
            final ByteBuffer startPacket = this.network.startPacket();
            this.doPacket(PacketTypes.PacketType.RequestLargeAreaZip.getId(), startPacket);
            startPacket.putInt(this.player.WorldX);
            startPacket.putInt(this.player.WorldY);
            startPacket.putInt(13);
            this.network.endPacketImmediate(this.connectionGUID);
            final int n = this.player.WorldX - 6 + 2;
            final int n2 = this.player.WorldY - 6 + 2;
            final int n3 = this.player.WorldX + 6 + 2;
            for (int n4 = this.player.WorldY + 6 + 2, i = n2; i <= n4; ++i) {
                for (int j = n; j <= n3; ++j) {
                    final Request value = new Request(j, i, this.requestId);
                    ++this.requestId;
                    this.requests.put(value.id, value);
                }
            }
            this.requestZipList();
        }
        
        private void requestFullUpdate() {
            this.doPacket(PacketTypes.PacketType.IsoRegionClientRequestFullUpdate.getId(), this.network.startPacket());
            this.network.endPacketImmediate(this.connectionGUID);
        }
        
        private void requestChunkObjectState() {
            for (final Request request : this.requests.values()) {
                final ByteBuffer startPacket = this.network.startPacket();
                this.doPacket(PacketTypes.PacketType.ChunkObjectState.getId(), startPacket);
                startPacket.putShort((short)request.wx);
                startPacket.putShort((short)request.wy);
                this.network.endPacket(this.connectionGUID);
            }
        }
        
        private void requestChunks() {
            if (!this.requests.isEmpty()) {
                this.requestZipList();
                this.requestChunkObjectState();
                this.requests.clear();
            }
        }
        
        private void sendStatisticsEnable(final int n) {
            final ByteBuffer startPacket = this.network.startPacket();
            this.doPacket(PacketTypes.PacketType.StatisticRequest.getId(), startPacket);
            startPacket.put((byte)3);
            startPacket.putInt(n);
            this.network.endPacketImmediate(this.connectionGUID);
        }
        
        private void receiveStatistics(final ByteBuffer byteBuffer) {
            FakeClientManager.info(this.player.movement.id, String.format("ServerStats: con=[%2d] fps=[%2d] tps=[%2d] upt=[%4d-%4d/%4d], c1=[%d] c2=[%d] c3=[%d]", byteBuffer.getLong(), byteBuffer.getLong(), byteBuffer.getLong(), byteBuffer.getLong(), byteBuffer.getLong(), byteBuffer.getLong(), byteBuffer.getLong(), byteBuffer.getLong(), byteBuffer.getLong()));
        }
        
        private void sendTimeSync() {
            final ByteBuffer startPacket = this.network.startPacket();
            this.doPacket(PacketTypes.PacketType.TimeSync.getId(), startPacket);
            startPacket.putLong(System.nanoTime());
            startPacket.putLong(0L);
            this.network.endPacketImmediate(this.connectionGUID);
        }
        
        private void receiveTimeSync(final ByteBuffer byteBuffer) {
            final long long1 = byteBuffer.getLong();
            final long long2 = byteBuffer.getLong();
            final long nanoTime = System.nanoTime();
            final long serverTimeShift = long2 - nanoTime + (nanoTime - long1) / 2L;
            final long serverTimeShift2 = Client.serverTimeShift;
            if (!Client.serverTimeShiftIsSet) {
                Client.serverTimeShift = serverTimeShift;
            }
            else {
                Client.serverTimeShift += (long)((serverTimeShift - Client.serverTimeShift) * 0.05f);
            }
            if (Math.abs(Client.serverTimeShift - serverTimeShift2) > 10000000L) {
                this.sendTimeSync();
            }
            else {
                Client.serverTimeShiftIsSet = true;
            }
        }
        
        private void receiveSyncClock(final ByteBuffer byteBuffer) {
            FakeClientManager.trace(this.player.movement.id, String.format("Player %3d sync clock", this.player.OnlineID));
        }
        
        private void receiveKicked(final ByteBuffer byteBuffer) {
            FakeClientManager.info(this.player.movement.id, String.format("Client kicked. Reason: %s", FakeClientManager.ReadStringUTF(byteBuffer)));
        }
        
        private void receiveChecksum(final ByteBuffer byteBuffer) {
            FakeClientManager.trace(this.player.movement.id, String.format("Player %3d receive Checksum", this.player.OnlineID));
            final short short1 = byteBuffer.getShort();
            final boolean b = byteBuffer.get() == 1;
            final boolean b2 = byteBuffer.get() == 1;
            if (short1 != 1 || !b || !b2) {
                FakeClientManager.info(this.player.movement.id, String.format("checksum lua: %b, script: %b", b, b2));
            }
            this.changeState(State.PLAYER_EXTRA_INFO);
        }
        
        private void receiveKillZombie(final ByteBuffer byteBuffer) {
            FakeClientManager.trace(this.player.movement.id, String.format("Player %3d receive KillZombie", this.player.OnlineID));
            final Zombie e = this.player.simulator.zombies.get((int)byteBuffer.getShort());
            if (e != null) {
                this.player.simulator.zombies4Delete.add(e);
            }
        }
        
        private void receiveTeleport(final ByteBuffer byteBuffer) {
            byteBuffer.get();
            final float float1 = byteBuffer.getFloat();
            final float float2 = byteBuffer.getFloat();
            byteBuffer.getFloat();
            FakeClientManager.info(this.player.movement.id, String.format("Player %3d teleport to (%d, %d)", this.player.OnlineID, (int)float1, (int)float2));
            this.player.x = float1;
            this.player.y = float2;
        }
        
        private void receiveZombieSimulation(final ByteBuffer byteBuffer) {
            this.player.simulator.clear();
            final boolean b = byteBuffer.get() == 1;
            for (short short1 = byteBuffer.getShort(), n = 0; n < short1; ++n) {
                this.player.simulator.zombies4Delete.add(this.player.simulator.zombies.get((int)byteBuffer.getShort()));
            }
            for (short short2 = byteBuffer.getShort(), n2 = 0; n2 < short2; ++n2) {
                this.player.simulator.add(byteBuffer.getShort());
            }
            this.player.simulator.receivePacket(byteBuffer);
            this.player.simulator.process();
        }
        
        private void sendInjuries() {
            final SyncInjuriesPacket syncInjuriesPacket = new SyncInjuriesPacket();
            syncInjuriesPacket.id = this.player.OnlineID;
            syncInjuriesPacket.strafeSpeed = 1.0f;
            syncInjuriesPacket.walkSpeed = 1.0f;
            syncInjuriesPacket.walkInjury = 0.0f;
            final ByteBuffer startPacket = this.network.startPacket();
            this.doPacket(PacketTypes.PacketType.SyncInjuries.getId(), startPacket);
            syncInjuriesPacket.write(new ByteBufferWriter(startPacket));
            this.network.endPacketImmediate(this.connectionGUID);
        }
        
        private void sendChecksum() {
            if (Client.luaChecksum.isEmpty()) {
                return;
            }
            FakeClientManager.trace(this.player.movement.id, String.format("Player %3d sendChecksum", this.player.OnlineID));
            final ByteBuffer startPacket = this.network.startPacket();
            this.doPacket(PacketTypes.PacketType.Checksum.getId(), startPacket);
            startPacket.putShort((short)1);
            this.putUTF(startPacket, Client.luaChecksum);
            this.putUTF(startPacket, Client.scriptChecksum);
            this.network.endPacketImmediate(this.connectionGUID);
        }
        
        public void sendCommand(final String s) {
            final ByteBuffer startPacket = this.network.startPacket();
            this.doPacket(PacketTypes.PacketType.ReceiveCommand.getId(), startPacket);
            FakeClientManager.WriteStringUTF(startPacket, s);
            this.network.endPacketImmediate(this.connectionGUID);
        }
        
        private void sendEventPacket(final short n, final int n2, final int n3, final int n4, final byte b, final String s) {
            final ByteBuffer startPacket = this.network.startPacket();
            this.doPacket(PacketTypes.PacketType.EventPacket.getId(), startPacket);
            startPacket.putShort(n);
            startPacket.putFloat((float)n2);
            startPacket.putFloat((float)n3);
            startPacket.putFloat((float)n4);
            startPacket.put(b);
            FakeClientManager.WriteStringUTF(startPacket, s);
            FakeClientManager.WriteStringUTF(startPacket, "");
            FakeClientManager.WriteStringUTF(startPacket, "");
            FakeClientManager.WriteStringUTF(startPacket, "");
            startPacket.putFloat(1.0f);
            startPacket.putFloat(1.0f);
            startPacket.putFloat(0.0f);
            startPacket.putInt(0);
            startPacket.putShort((short)0);
            this.network.endPacketImmediate(this.connectionGUID);
        }
        
        private void sendWorldSound4Player(final int n, final int n2, final int n3, final int n4, final int n5) {
            final ByteBuffer startPacket = this.network.startPacket();
            this.doPacket(PacketTypes.PacketType.WorldSound.getId(), startPacket);
            startPacket.putInt(n);
            startPacket.putInt(n2);
            startPacket.putInt(n3);
            startPacket.putInt(n4);
            startPacket.putInt(n5);
            startPacket.put((byte)0);
            startPacket.putFloat(0.0f);
            startPacket.putFloat(1.0f);
            startPacket.put((byte)0);
            this.network.endPacketImmediate(this.connectionGUID);
        }
        
        static {
            Client.connectionServerHost = "127.0.0.1";
            Client.connectionInterval = 1500L;
            Client.connectionTimeout = 10000L;
            Client.connectionDelay = 15000L;
            Client.statisticsClientID = -1;
            Client.statisticsPeriod = 1;
            Client.serverTimeShift = 0L;
            Client.serverTimeShiftIsSet = false;
            Client.luaChecksum = "";
            Client.scriptChecksum = "";
        }
        
        private enum State
        {
            CONNECT, 
            LOGIN, 
            CHECKSUM, 
            PLAYER_CONNECT, 
            PLAYER_EXTRA_INFO, 
            LOAD, 
            RUN, 
            WAIT, 
            DISCONNECT, 
            QUIT;
            
            private static /* synthetic */ State[] $values() {
                return new State[] { State.CONNECT, State.LOGIN, State.CHECKSUM, State.PLAYER_CONNECT, State.PLAYER_EXTRA_INFO, State.LOAD, State.RUN, State.WAIT, State.DISCONNECT, State.QUIT };
            }
            
            static {
                $VALUES = $values();
            }
        }
        
        private static final class Request
        {
            private final int id;
            private final int wx;
            private final int wy;
            private final long crc;
            
            private Request(final int n, final int n2, final int id) {
                this.id = id;
                this.wx = n;
                this.wy = n2;
                final CRC32 crc32 = new CRC32();
                crc32.reset();
                crc32.update(String.format("map_%d_%d.bin", n, n2).getBytes());
                this.crc = crc32.getValue();
            }
        }
    }
    
    private static class Network
    {
        private final HashMap<Integer, Client> createdClients;
        private final HashMap<Long, Client> connectedClients;
        private final ByteBuffer rb;
        private final ByteBuffer wb;
        private final RakNetPeerInterface peer;
        private final int started;
        private int connected;
        private static final HashMap<Integer, String> systemPacketTypeNames;
        private static final HashMap<Short, String> userPacketTypeNames;
        
        boolean isConnected() {
            return this.connected == 0;
        }
        
        boolean isStarted() {
            return this.started == 0;
        }
        
        private Network(final int n, final int n2) {
            this.createdClients = new HashMap<Integer, Client>();
            this.connectedClients = new HashMap<Long, Client>();
            this.rb = ByteBuffer.allocate(1000000);
            this.wb = ByteBuffer.allocate(1000000);
            this.connected = -1;
            (this.peer = new RakNetPeerInterface()).Init(false);
            this.peer.SetMaximumIncomingConnections(0);
            this.peer.SetClientPort(n2);
            this.peer.SetOccasionalPing(true);
            this.started = this.peer.Startup(n);
            if (this.started == 0) {
                final Thread thread = new Thread(ThreadGroups.Network, this::receiveThread, "PeerInterfaceReceive");
                thread.setDaemon(true);
                thread.start();
                FakeClientManager.log(-1, "Network start ok");
            }
            else {
                FakeClientManager.error(-1, String.format("Network start failed: %d", this.started));
            }
        }
        
        private void connect(final int n, final String s) {
            this.connected = this.peer.Connect(s, 16261, PZcrypt.hash("", true));
            if (this.connected == 0) {
                FakeClientManager.log(n, String.format("Client connected to %s:%d", s, 16261));
            }
            else {
                FakeClientManager.error(n, String.format("Client connection to %s:%d failed: %d", s, 16261, this.connected));
            }
        }
        
        private void disconnect(final long l, final int n, final String s) {
            if (l != 0L) {
                this.peer.disconnect(l);
                this.connected = -1;
            }
            if (this.connected == -1) {
                FakeClientManager.log(n, String.format("Client disconnected from %s:%d", s, 16261));
            }
            else {
                FakeClientManager.log(n, String.format("Client disconnection from %s:%d failed: %d", s, 16261, l));
            }
        }
        
        private ByteBuffer startPacket() {
            this.wb.clear();
            return this.wb;
        }
        
        private void cancelPacket() {
            this.wb.clear();
        }
        
        private void endPacket(final long n) {
            this.wb.flip();
            this.peer.Send(this.wb, 1, 3, (byte)0, n, false);
        }
        
        private void endPacketImmediate(final long n) {
            this.wb.flip();
            this.peer.Send(this.wb, 0, 3, (byte)0, n, false);
        }
        
        private void endPacketSuperHighUnreliable(final long n) {
            this.wb.flip();
            this.peer.Send(this.wb, 0, 1, (byte)0, n, false);
        }
        
        private void receiveThread() {
            while (true) {
                if (this.peer.Receive(this.rb)) {
                    this.decode(this.rb);
                }
                else {
                    FakeClientManager.sleep(1L);
                }
            }
        }
        
        private static void logUserPacket(final int n, final short s) {
            FakeClientManager.trace(n, String.format("## %s", Network.userPacketTypeNames.getOrDefault(s, "unknown user packet")));
        }
        
        private static void logSystemPacket(final int n, final int i) {
            FakeClientManager.trace(n, String.format("# %s", Network.systemPacketTypeNames.getOrDefault(i, "unknown system packet")));
        }
        
        private void decode(final ByteBuffer byteBuffer) {
            final int n = byteBuffer.get() & 0xFF;
            int connectionIndex = -1;
            switch (n) {
                case 17:
                case 18:
                case 23:
                case 24:
                case 32: {
                    FakeClientManager.error(-1, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n));
                    break;
                }
                case 22: {
                    connectionIndex = (byteBuffer.get() & 0xFF);
                    final Client client = this.createdClients.get(connectionIndex);
                    if (client != null) {
                        client.changeState(Client.State.DISCONNECT);
                        break;
                    }
                    break;
                }
                case 21: {
                    connectionIndex = (byteBuffer.get() & 0xFF);
                    final long guidOfPacket = this.peer.getGuidOfPacket();
                    final Client client2 = this.connectedClients.get(guidOfPacket);
                    if (client2 != null) {
                        this.connectedClients.remove(guidOfPacket);
                        client2.changeState(Client.State.DISCONNECT);
                    }
                    FakeClientManager.log(-1, String.format("Connected clients: %d (connection index %d)", this.connectedClients.size(), connectionIndex));
                    break;
                }
                case 19: {
                    connectionIndex = (byteBuffer.get() & 0xFF);
                }
                case 44:
                case 45: {
                    this.peer.getGuidOfPacket();
                    break;
                }
                case 16: {
                    connectionIndex = (byteBuffer.get() & 0xFF);
                    final long guidOfPacket2 = this.peer.getGuidOfPacket();
                    final Client value = this.createdClients.get(connectionIndex);
                    if (value != null) {
                        value.connectionGUID = guidOfPacket2;
                        this.connectedClients.put(guidOfPacket2, value);
                        value.changeState(Client.State.LOGIN);
                    }
                    FakeClientManager.log(-1, String.format("Connected clients: %d (connection index %d)", this.connectedClients.size(), connectionIndex));
                }
                case 134: {
                    final short short1 = byteBuffer.getShort();
                    final Client client3 = this.connectedClients.get(this.peer.getGuidOfPacket());
                    if (client3 != null) {
                        client3.receive(short1, byteBuffer);
                        connectionIndex = client3.connectionIndex;
                        break;
                    }
                    break;
                }
            }
            logSystemPacket(connectionIndex, n);
        }
        
        static {
            systemPacketTypeNames = new HashMap<Integer, String>();
            userPacketTypeNames = new HashMap<Short, String>();
            Network.systemPacketTypeNames.put(22, "connection lost");
            Network.systemPacketTypeNames.put(21, "disconnected");
            Network.systemPacketTypeNames.put(23, "connection banned");
            Network.systemPacketTypeNames.put(17, "connection failed");
            Network.systemPacketTypeNames.put(20, "no free connections");
            Network.systemPacketTypeNames.put(16, "connection accepted");
            Network.systemPacketTypeNames.put(18, "already connected");
            Network.systemPacketTypeNames.put(44, "voice request");
            Network.systemPacketTypeNames.put(45, "voice reply");
            Network.systemPacketTypeNames.put(25, "wrong protocol version");
            Network.systemPacketTypeNames.put(0, "connected ping");
            Network.systemPacketTypeNames.put(1, "unconnected ping");
            Network.systemPacketTypeNames.put(33, "new remote connection");
            Network.systemPacketTypeNames.put(31, "remote disconnection");
            Network.systemPacketTypeNames.put(32, "remote connection lost");
            Network.systemPacketTypeNames.put(24, "invalid password");
            Network.systemPacketTypeNames.put(19, "new connection");
            Network.systemPacketTypeNames.put(134, "user packet");
            for (final Field field : PacketTypes.class.getFields()) {
                if (field.getType().equals(Short.TYPE) && Modifier.isStatic(field.getModifiers())) {
                    try {
                        Network.userPacketTypeNames.put(field.getShort(null), field.getName());
                    }
                    catch (IllegalAccessException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
    
    private static class Player
    {
        private static final int cellSize = 50;
        private static final int spawnMinX = 3550;
        private static final int spawnMaxX = 14450;
        private static final int spawnMinY = 5050;
        private static final int spawnMaxY = 12950;
        private static final int ChunkGridWidth = 13;
        private static final int ChunksPerWidth = 10;
        private static int fps;
        private static int predictInterval;
        private static float damage;
        private final NetworkCharacter networkCharacter;
        private final UpdateLimit updateLimiter;
        private final UpdateLimit predictLimiter;
        private final UpdateLimit timeSyncLimiter;
        private final Client client;
        private final Movement movement;
        private final ArrayList<Clothes> clothes;
        private final String username;
        private final int isFemale;
        private final Color tagColor;
        private final Color speakColor;
        private UpdateLimit teleportLimiter;
        private short OnlineID;
        private float x;
        private float y;
        private final float z;
        private Vector2 direction;
        private int WorldX;
        private int WorldY;
        private float angle;
        private ZombieSimulator simulator;
        private PlayerManager playerManager;
        private boolean weapon_isBareHeads;
        private int weapon_id;
        private short registry_id;
        static float distance;
        private int lastPlayerForHello;
        
        private Player(final Movement movement, final Network network, final int i, final int n) {
            this.weapon_isBareHeads = false;
            this.weapon_id = 837602032;
            this.registry_id = 1202;
            this.lastPlayerForHello = -1;
            this.username = String.format("Client%d", movement.id);
            this.tagColor = Colors.SkyBlue;
            this.speakColor = Colors.GetRandomColor();
            this.isFemale = (int)Math.round(Math.random());
            this.OnlineID = -1;
            (this.clothes = new ArrayList<Clothes>()).add(new Clothes((byte)11, (byte)0, "Shirt_FormalWhite"));
            this.clothes.add(new Clothes((byte)13, (byte)3, "Tie_Full"));
            this.clothes.add(new Clothes((byte)11, (byte)0, "Socks_Ankle"));
            this.clothes.add(new Clothes((byte)13, (byte)0, "Trousers_Suit"));
            this.clothes.add(new Clothes((byte)13, (byte)0, "Suit_Jacket"));
            this.clothes.add(new Clothes((byte)11, (byte)0, "Shoes_Black"));
            this.clothes.add(new Clothes((byte)11, (byte)0, "Glasses_Sun"));
            this.WorldX = (int)this.x / 10;
            this.WorldY = (int)this.y / 10;
            this.movement = movement;
            this.z = 0.0f;
            this.angle = 0.0f;
            this.x = movement.spawn.x;
            this.y = movement.spawn.y;
            this.direction = movement.direction.ToVector();
            this.networkCharacter = new NetworkCharacter();
            this.simulator = new ZombieSimulator(this);
            this.playerManager = new PlayerManager(this);
            this.client = new Client(this, network, i, n);
            network.createdClients.put(i, this.client);
            this.updateLimiter = new UpdateLimit(1000 / Player.fps);
            this.predictLimiter = new UpdateLimit((long)(Player.predictInterval * 0.6f));
            this.timeSyncLimiter = new UpdateLimit(10000L);
        }
        
        private float getDistance(final float n) {
            return n / 3.6f / Player.fps;
        }
        
        private void teleportMovement() {
            final float x = this.movement.destination.x;
            final float y = this.movement.destination.y;
            FakeClientManager.info(this.movement.id, String.format("Player %3d teleport (%9.3f,%9.3f) => (%9.3f,%9.3f) / %9.3f, next in %.3fs", this.OnlineID, this.x, this.y, x, y, Math.sqrt(Math.pow(x - this.x, 2.0) + Math.pow(y - this.y, 2.0)), this.movement.teleportDelay / 1000.0f));
            this.x = x;
            this.y = y;
            this.angle = 0.0f;
            this.teleportLimiter.Reset(this.movement.teleportDelay);
        }
        
        private void lineMovement() {
            Player.distance = this.getDistance(this.movement.speed);
            this.direction.set(this.movement.destination.x - this.x, this.movement.destination.y - this.y);
            this.direction.normalize();
            float x = this.x + Player.distance * this.direction.x;
            float y = this.y + Player.distance * this.direction.y;
            if ((this.x < this.movement.destination.x && x > this.movement.destination.x) || (this.x > this.movement.destination.x && x < this.movement.destination.x) || (this.y < this.movement.destination.y && y > this.movement.destination.y) || (this.y > this.movement.destination.y && y < this.movement.destination.y)) {
                x = this.movement.destination.x;
                y = this.movement.destination.y;
            }
            this.x = x;
            this.y = y;
        }
        
        private void circleMovement() {
            this.angle = (this.angle + (float)(2.0 * Math.asin(this.getDistance(this.movement.speed) / 2.0f / this.movement.radius))) % 360.0f;
            final float x = this.movement.spawn.x + (float)(this.movement.radius * Math.sin(this.angle));
            final float y = this.movement.spawn.y + (float)(this.movement.radius * Math.cos(this.angle));
            this.x = x;
            this.y = y;
        }
        
        private Zombie getNearestZombie() {
            Zombie zombie = null;
            float n = Float.POSITIVE_INFINITY;
            for (final Zombie zombie2 : this.simulator.zombies.values()) {
                final float distanceToSquared = IsoUtils.DistanceToSquared(this.x, this.y, zombie2.x, zombie2.y);
                if (distanceToSquared < n) {
                    zombie = zombie2;
                    n = distanceToSquared;
                }
            }
            return zombie;
        }
        
        private Zombie getNearestZombie(final PlayerManager.RemotePlayer remotePlayer) {
            Zombie zombie = null;
            float n = Float.POSITIVE_INFINITY;
            for (final Zombie zombie2 : this.simulator.zombies.values()) {
                final float distanceToSquared = IsoUtils.DistanceToSquared(remotePlayer.x, remotePlayer.y, zombie2.x, zombie2.y);
                if (distanceToSquared < n) {
                    zombie = zombie2;
                    n = distanceToSquared;
                }
            }
            return zombie;
        }
        
        private PlayerManager.RemotePlayer getNearestPlayer() {
            PlayerManager.RemotePlayer remotePlayer = null;
            float n = Float.POSITIVE_INFINITY;
            synchronized (this.playerManager.players) {
                for (final PlayerManager.RemotePlayer remotePlayer2 : this.playerManager.players.values()) {
                    final float distanceToSquared = IsoUtils.DistanceToSquared(this.x, this.y, remotePlayer2.x, remotePlayer2.y);
                    if (distanceToSquared < n) {
                        remotePlayer = remotePlayer2;
                        n = distanceToSquared;
                    }
                }
            }
            return remotePlayer;
        }
        
        private void aiAttackZombiesMovement() {
            final Zombie nearestZombie = this.getNearestZombie();
            final float distance = this.getDistance(this.movement.speed);
            if (nearestZombie != null) {
                this.direction.set(nearestZombie.x - this.x, nearestZombie.y - this.y);
                this.direction.normalize();
            }
            final float x = this.x + distance * this.direction.x;
            final float y = this.y + distance * this.direction.y;
            this.x = x;
            this.y = y;
        }
        
        private void aiRunAwayFromZombiesMovement() {
            final Zombie nearestZombie = this.getNearestZombie();
            final float distance = this.getDistance(this.movement.speed);
            if (nearestZombie != null) {
                this.direction.set(this.x - nearestZombie.x, this.y - nearestZombie.y);
                this.direction.normalize();
            }
            final float x = this.x + distance * this.direction.x;
            final float y = this.y + distance * this.direction.y;
            this.x = x;
            this.y = y;
        }
        
        private void aiRunToAnotherPlayersMovement() {
            final PlayerManager.RemotePlayer nearestPlayer = this.getNearestPlayer();
            final float distance = this.getDistance(this.movement.speed);
            final float x = this.x + distance * this.direction.x;
            final float y = this.y + distance * this.direction.y;
            if (nearestPlayer != null) {
                this.direction.set(nearestPlayer.x - this.x, nearestPlayer.y - this.y);
                if (this.direction.normalize() > 2.0f) {
                    this.x = x;
                    this.y = y;
                }
                else if (this.lastPlayerForHello != nearestPlayer.OnlineID) {
                    this.lastPlayerForHello = nearestPlayer.OnlineID;
                }
            }
        }
        
        private void aiNormalMovement() {
            final float distance = this.getDistance(this.movement.speed);
            final PlayerManager.RemotePlayer nearestPlayer = this.getNearestPlayer();
            if (nearestPlayer == null) {
                this.aiRunAwayFromZombiesMovement();
                return;
            }
            final float distanceToSquared = IsoUtils.DistanceToSquared(this.x, this.y, nearestPlayer.x, nearestPlayer.y);
            if (distanceToSquared > 36.0f) {
                this.movement.speed = 13.0f;
                this.movement.motion = Movement.Motion.Run;
            }
            else {
                this.movement.speed = 4.0f;
                this.movement.motion = Movement.Motion.Walk;
            }
            Zombie nearestZombie = this.getNearestZombie();
            float distanceToSquared2 = Float.POSITIVE_INFINITY;
            if (nearestZombie != null) {
                distanceToSquared2 = IsoUtils.DistanceToSquared(this.x, this.y, nearestZombie.x, nearestZombie.y);
            }
            final Zombie nearestZombie2 = this.getNearestZombie(nearestPlayer);
            float distanceToSquared3 = Float.POSITIVE_INFINITY;
            if (nearestZombie2 != null) {
                distanceToSquared3 = IsoUtils.DistanceToSquared(nearestPlayer.x, nearestPlayer.y, nearestZombie2.x, nearestZombie2.y);
            }
            if (distanceToSquared3 < 25.0f) {
                nearestZombie = nearestZombie2;
                distanceToSquared2 = distanceToSquared3;
            }
            if (distanceToSquared > 25.0f || nearestZombie == null) {
                this.direction.set(nearestPlayer.x - this.x, nearestPlayer.y - this.y);
                if (this.direction.normalize() > 4.0f) {
                    final float x = this.x + distance * this.direction.x;
                    final float y = this.y + distance * this.direction.y;
                    this.x = x;
                    this.y = y;
                }
                else if (this.lastPlayerForHello != nearestPlayer.OnlineID) {
                    this.lastPlayerForHello = nearestPlayer.OnlineID;
                }
            }
            else if (distanceToSquared2 < 25.0f) {
                this.direction.set(nearestZombie.x - this.x, nearestZombie.y - this.y);
                this.direction.normalize();
                this.x += distance * this.direction.x;
                this.y += distance * this.direction.y;
            }
        }
        
        private void checkRequestChunks() {
            final int worldX = (int)this.x / 10;
            final int worldY = (int)this.y / 10;
            if (Math.abs(worldX - this.WorldX) >= 13 || Math.abs(worldY - this.WorldY) >= 13) {
                final int n = this.WorldX - 6;
                final int n2 = this.WorldY - 6;
                final int n3 = this.WorldX + 6;
                final int n4 = this.WorldY + 6;
                for (int i = n; i <= n3; ++i) {
                    for (int j = n2; j <= n4; ++j) {
                        this.client.addChunkRequest(i, j, i - n, j - n2);
                    }
                }
            }
            else if (worldX != this.WorldX) {
                if (worldX < this.WorldX) {
                    for (int k = -6; k <= 6; ++k) {
                        this.client.addChunkRequest(this.WorldX - 6, this.WorldY + k, 0, k + 6);
                    }
                }
                else {
                    for (int l = -6; l <= 6; ++l) {
                        this.client.addChunkRequest(this.WorldX + 6, this.WorldY + l, 12, l + 6);
                    }
                }
            }
            else if (worldY != this.WorldY) {
                if (worldY < this.WorldY) {
                    for (int n5 = -6; n5 <= 6; ++n5) {
                        this.client.addChunkRequest(this.WorldX + n5, this.WorldY - 6, n5 + 6, 0);
                    }
                }
                else {
                    for (int n6 = -6; n6 <= 6; ++n6) {
                        this.client.addChunkRequest(this.WorldX + n6, this.WorldY + 6, n6 + 6, 12);
                    }
                }
            }
            this.client.requestChunks();
            this.WorldX = worldX;
            this.WorldY = worldY;
        }
        
        private void hit() {
            FakeClientManager.info(this.movement.id, String.format("Player %3d hit", this.OnlineID));
        }
        
        private void run() {
            this.simulator.update();
            if (this.updateLimiter.Check()) {
                if (this.movement.doTeleport() && this.teleportLimiter.Check()) {
                    this.teleportMovement();
                }
                switch (this.movement.type) {
                    case Circle: {
                        this.circleMovement();
                        break;
                    }
                    case Line: {
                        this.lineMovement();
                        break;
                    }
                    case AIAttackZombies: {
                        this.aiAttackZombiesMovement();
                        break;
                    }
                    case AIRunAwayFromZombies: {
                        this.aiRunAwayFromZombiesMovement();
                        break;
                    }
                    case AIRunToAnotherPlayers: {
                        this.aiRunToAnotherPlayersMovement();
                        break;
                    }
                    case AINormal: {
                        this.aiNormalMovement();
                        break;
                    }
                }
                this.checkRequestChunks();
                if (this.predictLimiter.Check()) {
                    final int n = (int)(this.client.getServerTime() / 1000000L);
                    this.networkCharacter.checkResetPlayer(n);
                    this.client.sendPlayer(this.networkCharacter.predict(Player.predictInterval, n, this.x, this.y, this.direction.x, this.direction.y), n, this.direction);
                }
                if (this.timeSyncLimiter.Check()) {
                    this.client.sendTimeSync();
                }
                if (this.movement.hordeCreator != null && this.movement.hordeCreator.hordeCreatorLimiter.Check()) {
                    this.client.sendCommand(this.movement.hordeCreator.getCommand((int)this.x, (int)this.y, (int)this.z));
                }
                if (this.movement.soundMaker != null && this.movement.soundMaker.soundMakerLimiter.Check()) {
                    this.client.sendWorldSound4Player((int)this.x, (int)this.y, (int)this.z, this.movement.soundMaker.radius, this.movement.soundMaker.radius);
                    this.client.sendChatMessage(this.movement.soundMaker.message);
                    this.client.sendEventPacket(this.OnlineID, (int)this.x, (int)this.y, (int)this.z, (byte)4, "shout");
                }
            }
        }
        
        static {
            Player.fps = 60;
            Player.predictInterval = 1000;
            Player.damage = 1.0f;
            Player.distance = 0.0f;
        }
        
        private static class Clothes
        {
            private final byte flags;
            private final byte text;
            private final String name;
            
            Clothes(final byte flags, final byte text, final String name) {
                this.flags = flags;
                this.text = text;
                this.name = name;
            }
        }
    }
    
    private static class Zombie
    {
        public long lastUpdate;
        public float x;
        public float y;
        public float z;
        public short OnlineID;
        public boolean localOwnership;
        public ZombiePacket zombiePacket;
        public IsoDirections dir;
        public float health;
        public byte walkType;
        public float dropPositionX;
        public float dropPositionY;
        public boolean isMoving;
        
        public Zombie(final short n) {
            this.localOwnership = false;
            this.zombiePacket = null;
            this.dir = IsoDirections.N;
            this.health = 1.0f;
            this.walkType = (byte)Rand.Next(NetworkVariables.WalkType.values().length);
            this.isMoving = false;
            this.zombiePacket = new ZombiePacket();
            this.zombiePacket.id = n;
            this.OnlineID = n;
            this.localOwnership = false;
        }
    }
    
    private static class ZombieSimulator
    {
        public static Behaviour behaviour;
        public static int deleteZombieDistanceSquared;
        public static int forgotZombieDistanceSquared;
        public static int canSeeZombieDistanceSquared;
        public static int seeZombieDistanceSquared;
        private static boolean canChangeTarget;
        private static int updatePeriod;
        private static int attackPeriod;
        public static int maxZombiesPerUpdate;
        private final ByteBuffer bb;
        private UpdateLimit updateLimiter;
        private UpdateLimit attackLimiter;
        private Player player;
        private final ZombiePacket zombiePacket;
        private HashSet<Short> authoriseZombiesCurrent;
        private HashSet<Short> authoriseZombiesLast;
        private final ArrayList<Short> unknownZombies;
        private final HashMap<Integer, Zombie> zombies;
        private final ArrayDeque<Zombie> zombies4Add;
        private final ArrayDeque<Zombie> zombies4Delete;
        private final HashSet<Short> authoriseZombies;
        private final ArrayDeque<Zombie> SendQueue;
        private static Vector2 tmpDir;
        
        public ZombieSimulator(final Player player) {
            this.bb = ByteBuffer.allocate(1000000);
            this.updateLimiter = new UpdateLimit(ZombieSimulator.updatePeriod);
            this.attackLimiter = new UpdateLimit(ZombieSimulator.attackPeriod);
            this.player = null;
            this.zombiePacket = new ZombiePacket();
            this.authoriseZombiesCurrent = new HashSet<Short>();
            this.authoriseZombiesLast = new HashSet<Short>();
            this.unknownZombies = new ArrayList<Short>();
            this.zombies = new HashMap<Integer, Zombie>();
            this.zombies4Add = new ArrayDeque<Zombie>();
            this.zombies4Delete = new ArrayDeque<Zombie>();
            this.authoriseZombies = new HashSet<Short>();
            this.SendQueue = new ArrayDeque<Zombie>();
            this.player = player;
        }
        
        public void becomeLocal(final Zombie zombie) {
            zombie.localOwnership = true;
        }
        
        public void becomeRemote(final Zombie zombie) {
            zombie.localOwnership = false;
        }
        
        public void clear() {
            final HashSet<Short> authoriseZombiesCurrent = this.authoriseZombiesCurrent;
            this.authoriseZombiesCurrent = this.authoriseZombiesLast;
            (this.authoriseZombiesLast = authoriseZombiesCurrent).removeIf(n -> this.zombies.get((int)n) == null);
            this.authoriseZombiesCurrent.clear();
        }
        
        public void add(final short s) {
            this.authoriseZombiesCurrent.add(s);
        }
        
        public void receivePacket(final ByteBuffer byteBuffer) {
            for (short short1 = byteBuffer.getShort(), n = 0; n < short1; ++n) {
                this.parseZombie(byteBuffer);
            }
        }
        
        private void parseZombie(final ByteBuffer byteBuffer) {
            final ZombiePacket zombiePacket = this.zombiePacket;
            zombiePacket.parse(byteBuffer);
            Zombie e = this.zombies.get((int)zombiePacket.id);
            if (this.authoriseZombies.contains(zombiePacket.id) && e != null) {
                return;
            }
            if (e == null) {
                e = new Zombie(zombiePacket.id);
                this.zombies4Add.add(e);
                FakeClientManager.trace(this.player.movement.id, String.format("New zombie %s", e.OnlineID));
            }
            e.lastUpdate = System.currentTimeMillis();
            e.zombiePacket.copy(zombiePacket);
            e.x = zombiePacket.realX;
            e.y = zombiePacket.realY;
            e.z = zombiePacket.realZ;
        }
        
        public void process() {
            for (final Short n : Sets.difference((Set)this.authoriseZombiesCurrent, (Set)this.authoriseZombiesLast)) {
                final Zombie zombie = this.zombies.get((int)n);
                if (zombie != null) {
                    this.becomeLocal(zombie);
                }
                else {
                    if (this.unknownZombies.contains(n)) {
                        continue;
                    }
                    this.unknownZombies.add(n);
                }
            }
            final UnmodifiableIterator iterator2 = Sets.difference((Set)this.authoriseZombiesLast, (Set)this.authoriseZombiesCurrent).iterator();
            while (((Iterator)iterator2).hasNext()) {
                final Zombie zombie2 = this.zombies.get((int)((Iterator<Short>)iterator2).next());
                if (zombie2 != null) {
                    this.becomeRemote(zombie2);
                }
            }
            synchronized (this.authoriseZombies) {
                this.authoriseZombies.clear();
                this.authoriseZombies.addAll((Collection<?>)this.authoriseZombiesCurrent);
            }
        }
        
        public void send() {
            if (this.authoriseZombies.size() == 0 && this.unknownZombies.size() == 0) {
                return;
            }
            if (this.SendQueue.isEmpty()) {
                synchronized (this.authoriseZombies) {
                    final Iterator<Short> iterator = this.authoriseZombies.iterator();
                    while (iterator.hasNext()) {
                        final Zombie e = this.zombies.get((int)iterator.next());
                        if (e != null && e.OnlineID != -1) {
                            this.SendQueue.add(e);
                        }
                    }
                }
            }
            this.bb.clear();
            this.bb.putShort((short)0);
            final int size = this.unknownZombies.size();
            this.bb.putShort((short)size);
            for (int i = 0; i < this.unknownZombies.size(); ++i) {
                if (this.unknownZombies.get(i) == null) {
                    return;
                }
                this.bb.putShort(this.unknownZombies.get(i));
            }
            this.unknownZombies.clear();
            final int position = this.bb.position();
            this.bb.putShort((short)ZombieSimulator.maxZombiesPerUpdate);
            int n = 0;
            while (!this.SendQueue.isEmpty()) {
                final Zombie zombie = this.SendQueue.poll();
                if (zombie.OnlineID == -1) {
                    continue;
                }
                zombie.zombiePacket.write(this.bb);
                if (++n >= ZombieSimulator.maxZombiesPerUpdate) {
                    break;
                }
            }
            if (n < ZombieSimulator.maxZombiesPerUpdate) {
                final int position2 = this.bb.position();
                this.bb.position(position);
                this.bb.putShort((short)n);
                this.bb.position(position2);
            }
            if (n > 0 || size > 0) {
                final ByteBuffer startPacket = this.player.client.network.startPacket();
                this.player.client.doPacket(PacketTypes.PacketType.ZombieSimulation.getId(), startPacket);
                startPacket.put(this.bb.array(), 0, this.bb.position());
                this.player.client.network.endPacketSuperHighUnreliable(this.player.client.connectionGUID);
            }
        }
        
        private void simulate(final Integer n, final Zombie zombie) {
            final float distanceToSquared = IsoUtils.DistanceToSquared(this.player.x, this.player.y, zombie.x, zombie.y);
            if (distanceToSquared > ZombieSimulator.deleteZombieDistanceSquared || (!zombie.localOwnership && zombie.lastUpdate + 5000L < System.currentTimeMillis())) {
                this.zombies4Delete.add(zombie);
                return;
            }
            ZombieSimulator.tmpDir.set(-zombie.x + this.player.x, -zombie.y + this.player.y);
            if (zombie.isMoving) {
                final float n2 = 0.2f;
                zombie.x = PZMath.lerp(zombie.x, zombie.zombiePacket.x, n2);
                zombie.y = PZMath.lerp(zombie.y, zombie.zombiePacket.y, n2);
                zombie.z = 0.0f;
                zombie.dir = IsoDirections.fromAngle(ZombieSimulator.tmpDir);
            }
            if (ZombieSimulator.canChangeTarget) {
                synchronized (this.player.playerManager.players) {
                    for (final PlayerManager.RemotePlayer remotePlayer : this.player.playerManager.players.values()) {
                        if (IsoUtils.DistanceToSquared(remotePlayer.x, remotePlayer.y, zombie.x, zombie.y) < ZombieSimulator.seeZombieDistanceSquared) {
                            zombie.zombiePacket.target = remotePlayer.OnlineID;
                            break;
                        }
                    }
                }
            }
            else {
                zombie.zombiePacket.target = this.player.OnlineID;
            }
            if (ZombieSimulator.behaviour == Behaviour.Stay) {
                zombie.isMoving = false;
            }
            else if (ZombieSimulator.behaviour == Behaviour.Normal) {
                if (distanceToSquared > ZombieSimulator.forgotZombieDistanceSquared) {
                    zombie.isMoving = false;
                }
                if (distanceToSquared < ZombieSimulator.canSeeZombieDistanceSquared && (Rand.Next(100) < 1 || zombie.dir == IsoDirections.fromAngle(ZombieSimulator.tmpDir))) {
                    zombie.isMoving = true;
                }
                if (distanceToSquared < ZombieSimulator.seeZombieDistanceSquared) {
                    zombie.isMoving = true;
                }
            }
            else {
                zombie.isMoving = true;
            }
            if (zombie.isMoving) {
                final Vector2 toVector = zombie.dir.ToVector();
                float n3 = 3.0f;
                if (distanceToSquared < 100.0f) {
                    n3 = 6.0f;
                }
                final long n4 = System.currentTimeMillis() - zombie.lastUpdate;
                zombie.zombiePacket.x = zombie.x + toVector.x * n4 * 0.001f * n3;
                zombie.zombiePacket.y = zombie.y + toVector.y * n4 * 0.001f * n3;
                zombie.zombiePacket.z = (byte)zombie.z;
                zombie.zombiePacket.moveType = NetworkVariables.PredictionTypes.Moving;
            }
            else {
                zombie.zombiePacket.x = zombie.x;
                zombie.zombiePacket.y = zombie.y;
                zombie.zombiePacket.z = (byte)zombie.z;
                zombie.zombiePacket.moveType = NetworkVariables.PredictionTypes.Static;
            }
            zombie.zombiePacket.booleanVariables = 0;
            if (distanceToSquared < 100.0f) {
                final ZombiePacket zombiePacket = zombie.zombiePacket;
                zombiePacket.booleanVariables |= 0x2;
            }
            zombie.zombiePacket.timeSinceSeenFlesh = (zombie.isMoving ? 0 : 100000);
            zombie.zombiePacket.smParamTargetAngle = 0;
            zombie.zombiePacket.speedMod = 1000;
            zombie.zombiePacket.walkType = NetworkVariables.WalkType.values()[zombie.walkType];
            zombie.zombiePacket.realX = zombie.x;
            zombie.zombiePacket.realY = zombie.y;
            zombie.zombiePacket.realZ = (byte)zombie.z;
            zombie.zombiePacket.realHealth = (short)(zombie.health * 1000.0f);
            zombie.zombiePacket.realState = NetworkVariables.ZombieState.fromString(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, ZombieSimulator.behaviour.toString().toLowerCase()));
            if (zombie.isMoving) {
                zombie.zombiePacket.pfbType = 1;
                zombie.zombiePacket.pfbTarget = this.player.OnlineID;
            }
            else {
                zombie.zombiePacket.pfbType = 0;
            }
            if (distanceToSquared < 2.0f && this.attackLimiter.Check()) {
                zombie.health -= Player.damage;
                this.sendHitCharacter(zombie, Player.damage);
                if (zombie.health <= 0.0f) {
                    this.player.client.sendChatMessage("DIE!!");
                    this.zombies4Delete.add(zombie);
                }
            }
            zombie.lastUpdate = System.currentTimeMillis();
        }
        
        private void writeHitInfoToZombie(final ByteBuffer byteBuffer, final short n, final float n2, final float n3, final float n4) {
            byteBuffer.put((byte)2);
            byteBuffer.putShort(n);
            byteBuffer.put((byte)0);
            byteBuffer.putFloat(n2);
            byteBuffer.putFloat(n3);
            byteBuffer.putFloat(0.0f);
            byteBuffer.putFloat(n4);
            byteBuffer.putFloat(1.0f);
            byteBuffer.putInt(100);
        }
        
        private void sendHitCharacter(final Zombie zombie, final float n) {
            final boolean b = false;
            final ByteBuffer startPacket = this.player.client.network.startPacket();
            this.player.client.doPacket(PacketTypes.PacketType.HitCharacter.getId(), startPacket);
            startPacket.put((byte)3);
            startPacket.putShort(this.player.OnlineID);
            startPacket.putShort((short)0);
            startPacket.putFloat(this.player.x);
            startPacket.putFloat(this.player.y);
            startPacket.putFloat(this.player.z);
            startPacket.putFloat(this.player.direction.x);
            startPacket.putFloat(this.player.direction.y);
            FakeClientManager.WriteStringUTF(startPacket, "");
            FakeClientManager.WriteStringUTF(startPacket, "");
            FakeClientManager.WriteStringUTF(startPacket, "");
            startPacket.putShort((short)((this.player.weapon_isBareHeads ? 2 : 0) + (b ? 8 : 0)));
            startPacket.putFloat(1.0f);
            startPacket.putFloat(1.0f);
            startPacket.putFloat(1.0f);
            FakeClientManager.WriteStringUTF(startPacket, "default");
            startPacket.put((byte)(0x0 | (byte)(this.player.weapon_isBareHeads ? 9 : 0)));
            startPacket.put((byte)0);
            startPacket.putShort((short)0);
            startPacket.putFloat(1.0f);
            startPacket.putInt(0);
            final byte b2 = 1;
            startPacket.put(b2);
            for (byte b3 = 0; b3 < b2; ++b3) {
                this.writeHitInfoToZombie(startPacket, zombie.OnlineID, zombie.x, zombie.y, n);
            }
            startPacket.put((byte)0);
            final byte b4 = 1;
            startPacket.put(b4);
            for (byte b5 = 0; b5 < b4; ++b5) {
                this.writeHitInfoToZombie(startPacket, zombie.OnlineID, zombie.x, zombie.y, n);
            }
            if (!this.player.weapon_isBareHeads) {
                startPacket.put((byte)0);
            }
            else {
                startPacket.put((byte)1);
                startPacket.putShort(this.player.registry_id);
                startPacket.put((byte)1);
                startPacket.putInt(this.player.weapon_id);
                startPacket.put((byte)0);
                startPacket.putInt(0);
                startPacket.putInt(0);
            }
            startPacket.putShort(zombie.OnlineID);
            startPacket.putShort((short)((n >= zombie.health) ? 3 : 0));
            startPacket.putFloat(zombie.x);
            startPacket.putFloat(zombie.y);
            startPacket.putFloat(zombie.z);
            startPacket.putFloat(zombie.dir.ToVector().x);
            startPacket.putFloat(zombie.dir.ToVector().y);
            FakeClientManager.WriteStringUTF(startPacket, "");
            FakeClientManager.WriteStringUTF(startPacket, "");
            FakeClientManager.WriteStringUTF(startPacket, "");
            startPacket.putShort((short)0);
            FakeClientManager.WriteStringUTF(startPacket, "");
            FakeClientManager.WriteStringUTF(startPacket, "FRONT");
            startPacket.put((byte)0);
            startPacket.putFloat(n);
            startPacket.putFloat(1.0f);
            startPacket.putFloat(this.player.direction.x);
            startPacket.putFloat(this.player.direction.y);
            startPacket.putFloat(1.0f);
            startPacket.put((byte)0);
            if (ZombieSimulator.tmpDir.getLength() > 0.0f) {
                zombie.dropPositionX = zombie.x + ZombieSimulator.tmpDir.x / ZombieSimulator.tmpDir.getLength();
                zombie.dropPositionY = zombie.y + ZombieSimulator.tmpDir.y / ZombieSimulator.tmpDir.getLength();
            }
            else {
                zombie.dropPositionX = zombie.x;
                zombie.dropPositionY = zombie.y;
            }
            startPacket.putFloat(zombie.dropPositionX);
            startPacket.putFloat(zombie.dropPositionY);
            startPacket.put((byte)zombie.z);
            startPacket.putFloat(zombie.dir.toAngle());
            this.player.client.network.endPacketImmediate(this.player.client.connectionGUID);
        }
        
        private void sendSendDeadZombie(final Zombie zombie) {
            final ByteBuffer startPacket = this.player.client.network.startPacket();
            this.player.client.doPacket(PacketTypes.PacketType.ZombieDeath.getId(), startPacket);
            startPacket.putShort(zombie.OnlineID);
            startPacket.putFloat(zombie.x);
            startPacket.putFloat(zombie.y);
            startPacket.putFloat(zombie.z);
            startPacket.putFloat(zombie.dir.toAngle());
            startPacket.put((byte)zombie.dir.index());
            startPacket.put((byte)0);
            startPacket.put((byte)0);
            startPacket.put((byte)0);
            this.player.client.network.endPacketImmediate(this.player.client.connectionGUID);
        }
        
        public void simulateAll() {
            while (!this.zombies4Add.isEmpty()) {
                final Zombie value = this.zombies4Add.poll();
                this.zombies.put((int)value.OnlineID, value);
            }
            this.zombies.forEach(this::simulate);
            while (!this.zombies4Delete.isEmpty()) {
                this.zombies.remove((int)this.zombies4Delete.poll().OnlineID);
            }
        }
        
        public void update() {
            if (this.updateLimiter.Check()) {
                this.simulateAll();
                this.send();
            }
        }
        
        static {
            ZombieSimulator.behaviour = Behaviour.Stay;
            ZombieSimulator.deleteZombieDistanceSquared = 10000;
            ZombieSimulator.forgotZombieDistanceSquared = 225;
            ZombieSimulator.canSeeZombieDistanceSquared = 100;
            ZombieSimulator.seeZombieDistanceSquared = 25;
            ZombieSimulator.canChangeTarget = true;
            ZombieSimulator.updatePeriod = 100;
            ZombieSimulator.attackPeriod = 1000;
            ZombieSimulator.maxZombiesPerUpdate = 300;
            ZombieSimulator.tmpDir = new Vector2();
        }
        
        private enum Behaviour
        {
            Stay, 
            Normal, 
            Attack;
            
            private static /* synthetic */ Behaviour[] $values() {
                return new Behaviour[] { Behaviour.Stay, Behaviour.Normal, Behaviour.Attack };
            }
            
            static {
                $VALUES = $values();
            }
        }
    }
    
    private static class SoundMaker
    {
        private final int radius;
        private final int interval;
        private final String message;
        private final UpdateLimit soundMakerLimiter;
        
        public SoundMaker(final int interval, final int radius, final String message) {
            this.radius = radius;
            this.message = message;
            this.interval = interval;
            this.soundMakerLimiter = new UpdateLimit(interval);
        }
    }
    
    private static class HordeCreator
    {
        private final int radius;
        private final int count;
        private final long interval;
        private final UpdateLimit hordeCreatorLimiter;
        
        public HordeCreator(final int radius, final int count, final long interval) {
            this.radius = radius;
            this.count = count;
            this.interval = interval;
            this.hordeCreatorLimiter = new UpdateLimit(interval);
        }
        
        public String getCommand(final int i, final int j, final int k) {
            return String.format("/createhorde2 -x %d -y %d -z %d -count %d -radius %d -crawler false -isFallOnFront false -isFakeDead false -knockedDown false -health 1 -outfit", i, j, k, this.count, this.radius);
        }
    }
    
    private static class PlayerManager
    {
        private Player player;
        private final PlayerPacket playerPacket;
        public final HashMap<Integer, RemotePlayer> players;
        
        public PlayerManager(final Player player) {
            this.player = null;
            this.playerPacket = new PlayerPacket();
            this.players = new HashMap<Integer, RemotePlayer>();
            this.player = player;
        }
        
        private void parsePlayer(final ByteBuffer byteBuffer) {
            final PlayerPacket playerPacket = this.playerPacket;
            playerPacket.parse(byteBuffer);
            synchronized (this.players) {
                RemotePlayer value = this.players.get(playerPacket.id);
                if (value == null) {
                    value = new RemotePlayer(playerPacket.id);
                    this.players.put((int)playerPacket.id, value);
                    FakeClientManager.trace(this.player.movement.id, String.format("New player %s", value.OnlineID));
                }
                value.playerPacket.copy(playerPacket);
                value.x = playerPacket.realx;
                value.y = playerPacket.realy;
                value.z = playerPacket.realz;
            }
        }
        
        private void parsePlayerTimeout(final ByteBuffer byteBuffer) {
            final short short1 = byteBuffer.getShort();
            synchronized (this.players) {
                this.players.remove(short1);
            }
            FakeClientManager.trace(this.player.movement.id, String.format("Remove player %s", short1));
        }
        
        private class RemotePlayer
        {
            public float x;
            public float y;
            public float z;
            public short OnlineID;
            public PlayerPacket playerPacket;
            
            public RemotePlayer(final short n) {
                this.playerPacket = null;
                this.playerPacket = new PlayerPacket();
                this.playerPacket.id = n;
                this.OnlineID = n;
            }
        }
    }
}

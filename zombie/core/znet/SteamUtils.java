// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.znet;

import zombie.network.ServerWorldDatabase;
import zombie.network.CoopSlave;
import java.util.Iterator;
import zombie.Lua.LuaEventManager;
import java.util.ArrayList;
import zombie.network.GameServer;
import zombie.core.opengl.RenderThread;
import java.io.File;
import zombie.core.logger.ExceptionLogger;
import zombie.debug.DebugLog;
import java.util.List;
import java.math.BigInteger;

public class SteamUtils
{
    private static boolean m_steamEnabled;
    private static boolean m_netEnabled;
    private static final BigInteger TWO_64;
    private static final BigInteger MAX_ULONG;
    private static List<IJoinRequestCallback> m_joinRequestCallbacks;
    
    private static void loadLibrary(final String libname) {
        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, libname));
        System.loadLibrary(libname);
    }
    
    public static void init() {
        SteamUtils.m_steamEnabled = (System.getProperty("zomboid.steam") != null && System.getProperty("zomboid.steam").equals("1"));
        DebugLog.log("Loading networking libraries...");
        String s = "";
        if ("1".equals(System.getProperty("zomboid.debuglibs"))) {
            DebugLog.log("***** Loading debug versions of libraries");
            s = "d";
        }
        try {
            if (System.getProperty("os.name").contains("OS X")) {
                if (SteamUtils.m_steamEnabled) {
                    loadLibrary("steam_api");
                    loadLibrary("RakNet");
                    loadLibrary("ZNetJNI");
                }
                else {
                    loadLibrary("RakNet");
                    loadLibrary("ZNetNoSteam");
                }
            }
            else if (System.getProperty("os.name").startsWith("Win")) {
                if (System.getProperty("sun.arch.data.model").equals("64")) {
                    if (SteamUtils.m_steamEnabled) {
                        loadLibrary("steam_api64");
                        loadLibrary(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
                        loadLibrary(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
                    }
                    else {
                        loadLibrary(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
                        loadLibrary(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
                    }
                }
                else if (SteamUtils.m_steamEnabled) {
                    loadLibrary("steam_api");
                    loadLibrary(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
                    loadLibrary(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
                }
                else {
                    loadLibrary(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
                    loadLibrary(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
                }
            }
            else if (System.getProperty("sun.arch.data.model").equals("64")) {
                if (SteamUtils.m_steamEnabled) {
                    loadLibrary("steam_api");
                    loadLibrary("RakNet64");
                    loadLibrary("ZNetJNI64");
                }
                else {
                    loadLibrary("RakNet64");
                    loadLibrary("ZNetNoSteam64");
                }
            }
            else if (SteamUtils.m_steamEnabled) {
                loadLibrary("steam_api");
                loadLibrary("RakNet32");
                loadLibrary("ZNetJNI32");
            }
            else {
                loadLibrary("RakNet32");
                loadLibrary("ZNetNoSteam32");
            }
            SteamUtils.m_netEnabled = true;
        }
        catch (UnsatisfiedLinkError unsatisfiedLinkError) {
            SteamUtils.m_steamEnabled = false;
            SteamUtils.m_netEnabled = false;
            ExceptionLogger.logException(unsatisfiedLinkError);
            if (System.getProperty("os.name").startsWith("Win")) {
                DebugLog.log("One of the game's DLLs could not be loaded.");
                DebugLog.log("  Your system may be missing a DLL needed by the game's DLL.");
                DebugLog.log("  You may need to install the Microsoft Visual C++ Redistributable 2013.");
                final File file = new File("../_CommonRedist/vcredist/");
                if (file.exists()) {
                    DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, file.getAbsolutePath()));
                }
            }
        }
        final String property = System.getProperty("zomboid.znetlog");
        if (SteamUtils.m_netEnabled && property != null) {
            try {
                ZNet.setLogLevel(Integer.parseInt(property));
            }
            catch (NumberFormatException ex) {
                ExceptionLogger.logException(ex);
            }
        }
        if (!SteamUtils.m_netEnabled) {
            DebugLog.log("Failed to load networking libraries");
        }
        else {
            ZNet.init();
            synchronized (RenderThread.m_contextLock) {
                if (!SteamUtils.m_steamEnabled) {
                    DebugLog.log("SteamUtils started without Steam");
                }
                else if (n_Init(GameServer.bServer)) {
                    DebugLog.log("SteamUtils initialised successfully");
                }
                else {
                    DebugLog.log("Could not initialise SteamUtils");
                    SteamUtils.m_steamEnabled = false;
                }
            }
        }
        SteamUtils.m_joinRequestCallbacks = new ArrayList<IJoinRequestCallback>();
    }
    
    public static void shutdown() {
        if (SteamUtils.m_steamEnabled) {
            n_Shutdown();
        }
    }
    
    public static void runLoop() {
        if (SteamUtils.m_steamEnabled) {
            n_RunLoop();
        }
    }
    
    public static boolean isSteamModeEnabled() {
        return SteamUtils.m_steamEnabled;
    }
    
    public static boolean isOverlayEnabled() {
        return SteamUtils.m_steamEnabled && n_IsOverlayEnabled();
    }
    
    public static String convertSteamIDToString(final long val) {
        final BigInteger value = BigInteger.valueOf(val);
        if (value.signum() < 0) {
            value.add(SteamUtils.TWO_64);
        }
        return value.toString();
    }
    
    public static boolean isValidSteamID(final String val) {
        try {
            final BigInteger bigInteger = new BigInteger(val);
            if (bigInteger.signum() < 0 || bigInteger.compareTo(SteamUtils.MAX_ULONG) > 0) {
                return false;
            }
        }
        catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }
    
    public static long convertStringToSteamID(final String val) {
        try {
            final BigInteger bigInteger = new BigInteger(val);
            if (bigInteger.signum() < 0 || bigInteger.compareTo(SteamUtils.MAX_ULONG) > 0) {
                return -1L;
            }
            return bigInteger.longValue();
        }
        catch (NumberFormatException ex) {
            return -1L;
        }
    }
    
    public static void addJoinRequestCallback(final IJoinRequestCallback joinRequestCallback) {
        SteamUtils.m_joinRequestCallbacks.add(joinRequestCallback);
    }
    
    public static void removeJoinRequestCallback(final IJoinRequestCallback joinRequestCallback) {
        SteamUtils.m_joinRequestCallbacks.remove(joinRequestCallback);
    }
    
    private static native boolean n_Init(final boolean p0);
    
    private static native void n_Shutdown();
    
    private static native void n_RunLoop();
    
    private static native boolean n_IsOverlayEnabled();
    
    private static void joinRequestCallback(final long n, final String s) {
        DebugLog.log("Got Join Request");
        final Iterator<IJoinRequestCallback> iterator = SteamUtils.m_joinRequestCallbacks.iterator();
        while (iterator.hasNext()) {
            iterator.next().onJoinRequest(n, s);
        }
        if (s.contains("+connect ")) {
            System.setProperty("args.server.connect", s.substring(9));
            LuaEventManager.triggerEvent("OnSteamGameJoin");
        }
    }
    
    private static int clientInitiateConnectionCallback(final long n) {
        if (CoopSlave.instance != null) {
            return (CoopSlave.instance.isHost(n) || CoopSlave.instance.isInvited(n)) ? 0 : 2;
        }
        return ServerWorldDatabase.instance.authClient(n).bAuthorized ? 0 : 1;
    }
    
    private static int validateOwnerCallback(final long n, final long n2) {
        return (CoopSlave.instance == null && !ServerWorldDatabase.instance.authOwner(n, n2).bAuthorized) ? 1 : 0;
    }
    
    static {
        TWO_64 = BigInteger.ONE.shiftLeft(64);
        MAX_ULONG = new BigInteger("FFFFFFFFFFFFFFFF", 16);
    }
}

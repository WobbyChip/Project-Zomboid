// 
// Decompiled by Procyon v0.5.36
// 

package zombie.debug;

import java.io.IOException;
import zombie.ui.UIDebugConsole;
import zombie.core.Core;
import java.io.FilterOutputStream;
import zombie.core.logger.LoggerManager;
import java.io.OutputStream;
import zombie.config.ConfigOption;
import java.io.File;
import zombie.ZomboidFileSystem;
import zombie.config.ConfigFile;
import zombie.config.BooleanConfigOption;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import zombie.util.StringUtils;
import zombie.GameTime;
import java.util.concurrent.TimeUnit;
import java.text.NumberFormat;
import zombie.network.GameServer;
import zombie.core.logger.ZLogger;
import java.io.PrintStream;

public final class DebugLog
{
    private static final boolean[] m_enabledDebugTypes;
    private static boolean s_initialized;
    public static boolean printServerTime;
    private static final OutputStreamWrapper s_stdout;
    private static final OutputStreamWrapper s_stderr;
    private static final PrintStream m_originalOut;
    private static final PrintStream m_originalErr;
    private static final PrintStream GeneralErr;
    private static ZLogger s_logFileLogger;
    public static final DebugLogStream Asset;
    public static final DebugLogStream NetworkPacketDebug;
    public static final DebugLogStream NetworkFileDebug;
    public static final DebugLogStream Network;
    public static final DebugLogStream General;
    public static final DebugLogStream Lua;
    public static final DebugLogStream Mod;
    public static final DebugLogStream Sound;
    public static final DebugLogStream Zombie;
    public static final DebugLogStream Combat;
    public static final DebugLogStream Objects;
    public static final DebugLogStream Fireplace;
    public static final DebugLogStream Radio;
    public static final DebugLogStream MapLoading;
    public static final DebugLogStream Clothing;
    public static final DebugLogStream Animation;
    public static final DebugLogStream Script;
    public static final DebugLogStream Shader;
    public static final DebugLogStream Input;
    public static final DebugLogStream Recipe;
    public static final DebugLogStream ActionSystem;
    public static final DebugLogStream IsoRegion;
    public static final DebugLogStream UnitTests;
    public static final DebugLogStream FileIO;
    public static final DebugLogStream Multiplayer;
    public static final DebugLogStream Statistic;
    public static final DebugLogStream Vehicle;
    public static final int VERSION = 1;
    
    private static DebugLogStream createDebugLogStream(final DebugType debugType) {
        return new DebugLogStream(DebugLog.m_originalOut, DebugLog.m_originalOut, DebugLog.m_originalErr, new GenericDebugLogFormatter(debugType));
    }
    
    public static boolean isLogEnabled(final LogSeverity logSeverity, final DebugType debugType) {
        return logSeverity.ordinal() >= LogSeverity.Warning.ordinal() || isEnabled(debugType);
    }
    
    public static String formatString(final DebugType debugType, final LogSeverity logSeverity, final String s, final Object o, final String s2) {
        if (isLogEnabled(logSeverity, debugType)) {
            return formatStringVarArgs(debugType, logSeverity, s, o, "%s", s2);
        }
        return null;
    }
    
    public static String formatString(final DebugType debugType, final LogSeverity logSeverity, final String s, final Object o, final String s2, final Object o2) {
        if (isLogEnabled(logSeverity, debugType)) {
            return formatStringVarArgs(debugType, logSeverity, s, o, s2, o2);
        }
        return null;
    }
    
    public static String formatString(final DebugType debugType, final LogSeverity logSeverity, final String s, final Object o, final String s2, final Object o2, final Object o3) {
        if (isLogEnabled(logSeverity, debugType)) {
            return formatStringVarArgs(debugType, logSeverity, s, o, s2, o2, o3);
        }
        return null;
    }
    
    public static String formatString(final DebugType debugType, final LogSeverity logSeverity, final String s, final Object o, final String s2, final Object o2, final Object o3, final Object o4) {
        if (isLogEnabled(logSeverity, debugType)) {
            return formatStringVarArgs(debugType, logSeverity, s, o, s2, o2, o3, o4);
        }
        return null;
    }
    
    public static String formatString(final DebugType debugType, final LogSeverity logSeverity, final String s, final Object o, final String s2, final Object o2, final Object o3, final Object o4, final Object o5) {
        if (isLogEnabled(logSeverity, debugType)) {
            return formatStringVarArgs(debugType, logSeverity, s, o, s2, o2, o3, o4, o5);
        }
        return null;
    }
    
    public static String formatString(final DebugType debugType, final LogSeverity logSeverity, final String s, final Object o, final String s2, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6) {
        if (isLogEnabled(logSeverity, debugType)) {
            return formatStringVarArgs(debugType, logSeverity, s, o, s2, o2, o3, o4, o5, o6);
        }
        return null;
    }
    
    public static String formatString(final DebugType debugType, final LogSeverity logSeverity, final String s, final Object o, final String s2, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7) {
        if (isLogEnabled(logSeverity, debugType)) {
            return formatStringVarArgs(debugType, logSeverity, s, o, s2, o2, o3, o4, o5, o6, o7);
        }
        return null;
    }
    
    public static String formatString(final DebugType debugType, final LogSeverity logSeverity, final String s, final Object o, final String s2, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8) {
        if (isLogEnabled(logSeverity, debugType)) {
            return formatStringVarArgs(debugType, logSeverity, s, o, s2, o2, o3, o4, o5, o6, o7, o8);
        }
        return null;
    }
    
    public static String formatString(final DebugType debugType, final LogSeverity logSeverity, final String s, final Object o, final String s2, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8, final Object o9) {
        if (isLogEnabled(logSeverity, debugType)) {
            return formatStringVarArgs(debugType, logSeverity, s, o, s2, o2, o3, o4, o5, o6, o7, o8, o9);
        }
        return null;
    }
    
    public static String formatString(final DebugType debugType, final LogSeverity logSeverity, final String s, final Object o, final String s2, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8, final Object o9, final Object o10) {
        if (isLogEnabled(logSeverity, debugType)) {
            return formatStringVarArgs(debugType, logSeverity, s, o, s2, o2, o3, o4, o5, o6, o7, o8, o9, o10);
        }
        return null;
    }
    
    public static String formatStringVarArgs(final DebugType other, final LogSeverity logSeverity, final String s, final Object o, final String format, final Object... args) {
        if (!isLogEnabled(logSeverity, other)) {
            return null;
        }
        String value = String.valueOf(System.currentTimeMillis());
        if (GameServer.bServer || DebugLog.printServerTime || DebugType.Multiplayer.equals(other) || DebugType.Damage.equals(other) || DebugType.Death.equals(other)) {
            value = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, value, NumberFormat.getNumberInstance().format(TimeUnit.NANOSECONDS.toMillis(GameTime.getServerTime())));
        }
        final String s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/String;, s, StringUtils.leftJustify(other.toString(), 12), value, o, String.format(format, args));
        echoToLogFile(s2);
        return s2;
    }
    
    private static void echoToLogFile(final String s) {
        if (DebugLog.s_logFileLogger == null) {
            if (DebugLog.s_initialized) {
                return;
            }
            DebugLog.s_logFileLogger = new ZLogger(GameServer.bServer ? "DebugLog-server" : "DebugLog", false);
        }
        try {
            DebugLog.s_logFileLogger.writeUnsafe(s, null);
        }
        catch (Exception x) {
            DebugLog.m_originalErr.println("Exception thrown writing to log file.");
            DebugLog.m_originalErr.println(x);
            x.printStackTrace(DebugLog.m_originalErr);
        }
    }
    
    public static boolean isEnabled(final DebugType debugType) {
        return DebugLog.m_enabledDebugTypes[debugType.ordinal()];
    }
    
    public static void log(final DebugType debugType, final String s) {
        final String formatString = formatString(debugType, LogSeverity.General, "LOG  : ", "", "%s", s);
        if (formatString != null) {
            DebugLog.m_originalOut.println(formatString);
        }
    }
    
    public static void enableLog(final DebugType debugType) {
        setLogEnabled(debugType, true);
    }
    
    public static void disableLog(final DebugType debugType) {
        setLogEnabled(debugType, false);
    }
    
    public static void setLogEnabled(final DebugType debugType, final boolean b) {
        DebugLog.m_enabledDebugTypes[debugType.ordinal()] = b;
    }
    
    public static void log(final Object obj) {
        log(DebugType.General, String.valueOf(obj));
    }
    
    public static void log(final String s) {
        log(DebugType.General, s);
    }
    
    public static ArrayList<DebugType> getDebugTypes() {
        final ArrayList<DebugType> list = new ArrayList<DebugType>(Arrays.asList(DebugType.values()));
        list.sort((debugType, debugType2) -> String.CASE_INSENSITIVE_ORDER.compare(debugType.name(), debugType2.name()));
        return list;
    }
    
    public static void save() {
        final ArrayList<BooleanConfigOption> list = new ArrayList<BooleanConfigOption>();
        for (final DebugType debugType : DebugType.values()) {
            final BooleanConfigOption e = new BooleanConfigOption(debugType.name(), false);
            e.setValue(isEnabled(debugType));
            list.add(e);
        }
        new ConfigFile().write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator), 1, (ArrayList<? extends ConfigOption>)list);
    }
    
    public static void load() {
        final String s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator);
        final ConfigFile configFile = new ConfigFile();
        if (configFile.read(s)) {
            for (int i = 0; i < configFile.getOptions().size(); ++i) {
                final ConfigOption configOption = configFile.getOptions().get(i);
                try {
                    setLogEnabled(DebugType.valueOf(configOption.getName()), StringUtils.tryParseBoolean(configOption.getValueAsString()));
                }
                catch (Exception ex) {}
            }
        }
    }
    
    public static void setStdOut(final OutputStream stream) {
        DebugLog.s_stdout.setStream(stream);
    }
    
    public static void setStdErr(final OutputStream stream) {
        DebugLog.s_stderr.setStream(stream);
    }
    
    public static void init() {
        if (DebugLog.s_initialized) {
            return;
        }
        DebugLog.s_initialized = true;
        setStdOut(System.out);
        setStdErr(System.err);
        System.setOut(DebugLog.General);
        System.setErr(DebugLog.GeneralErr);
        if (!GameServer.bServer) {
            load();
        }
        DebugLog.s_logFileLogger = LoggerManager.getLogger(GameServer.bServer ? "DebugLog-server" : "DebugLog");
    }
    
    static {
        m_enabledDebugTypes = new boolean[DebugType.values().length];
        DebugLog.s_initialized = false;
        DebugLog.printServerTime = false;
        s_stdout = new OutputStreamWrapper(System.out);
        s_stderr = new OutputStreamWrapper(System.err);
        m_originalOut = new PrintStream(DebugLog.s_stdout, true);
        m_originalErr = new PrintStream(DebugLog.s_stderr, true);
        GeneralErr = new DebugLogStream(DebugLog.m_originalErr, DebugLog.m_originalErr, DebugLog.m_originalErr, new GeneralErrorDebugLogFormatter());
        Asset = createDebugLogStream(DebugType.Asset);
        NetworkPacketDebug = createDebugLogStream(DebugType.NetworkPacketDebug);
        NetworkFileDebug = createDebugLogStream(DebugType.NetworkFileDebug);
        Network = createDebugLogStream(DebugType.Network);
        General = createDebugLogStream(DebugType.General);
        Lua = createDebugLogStream(DebugType.Lua);
        Mod = createDebugLogStream(DebugType.Mod);
        Sound = createDebugLogStream(DebugType.Sound);
        Zombie = createDebugLogStream(DebugType.Zombie);
        Combat = createDebugLogStream(DebugType.Combat);
        Objects = createDebugLogStream(DebugType.Objects);
        Fireplace = createDebugLogStream(DebugType.Fireplace);
        Radio = createDebugLogStream(DebugType.Radio);
        MapLoading = createDebugLogStream(DebugType.MapLoading);
        Clothing = createDebugLogStream(DebugType.Clothing);
        Animation = createDebugLogStream(DebugType.Animation);
        Script = createDebugLogStream(DebugType.Script);
        Shader = createDebugLogStream(DebugType.Shader);
        Input = createDebugLogStream(DebugType.Input);
        Recipe = createDebugLogStream(DebugType.Recipe);
        ActionSystem = createDebugLogStream(DebugType.ActionSystem);
        IsoRegion = createDebugLogStream(DebugType.IsoRegion);
        UnitTests = createDebugLogStream(DebugType.UnitTests);
        FileIO = createDebugLogStream(DebugType.FileIO);
        Multiplayer = createDebugLogStream(DebugType.Multiplayer);
        Statistic = createDebugLogStream(DebugType.Statistic);
        Vehicle = createDebugLogStream(DebugType.Vehicle);
        enableLog(DebugType.General);
        enableLog(DebugType.Lua);
        enableLog(DebugType.Mod);
        enableLog(DebugType.Network);
        enableLog(DebugType.Multiplayer);
        enableLog(DebugType.IsoRegion);
        if (GameServer.bServer) {
            enableLog(DebugType.Damage);
            enableLog(DebugType.Statistic);
            enableLog(DebugType.Death);
        }
    }
    
    private static final class OutputStreamWrapper extends FilterOutputStream
    {
        public OutputStreamWrapper(final OutputStream out) {
            super(out);
        }
        
        @Override
        public void write(final byte[] b, final int off, final int len) throws IOException {
            this.out.write(b, off, len);
            if (Core.bDebug && UIDebugConsole.instance != null && DebugOptions.instance.UIDebugConsoleDebugLog.getValue()) {
                UIDebugConsole.instance.addOutput(b, off, len);
            }
        }
        
        public void setStream(final OutputStream out) {
            this.out = out;
        }
    }
}

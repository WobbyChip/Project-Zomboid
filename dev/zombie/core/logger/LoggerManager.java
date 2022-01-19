// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.logger;

import zombie.characters.IsoPlayer;
import zombie.ZomboidFileSystem;
import java.util.Calendar;
import java.io.File;
import zombie.debug.DebugLog;
import java.util.HashMap;

public final class LoggerManager
{
    private static boolean s_isInitialized;
    private static final HashMap<String, ZLogger> s_loggers;
    
    public static synchronized ZLogger getLogger(final String s) {
        if (!LoggerManager.s_loggers.containsKey(s)) {
            createLogger(s, false);
        }
        return LoggerManager.s_loggers.get(s);
    }
    
    public static synchronized void init() {
        if (LoggerManager.s_isInitialized) {
            return;
        }
        DebugLog.General.debugln("Initializing...");
        LoggerManager.s_isInitialized = true;
        backupOldLogFiles();
    }
    
    private static void backupOldLogFiles() {
        try {
            final String[] list = new File(getLogsDir()).list();
            if (list == null || list.length == 0) {
                return;
            }
            final Calendar logFileLastModifiedTime = getLogFileLastModifiedTime(list[0]);
            final String s = "logs_";
            String s2;
            if (logFileLastModifiedTime.get(5) < 9) {
                s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s, logFileLastModifiedTime.get(5));
            }
            else {
                s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s, logFileLastModifiedTime.get(5));
            }
            String s3;
            if (logFileLastModifiedTime.get(2) < 9) {
                s3 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s2, logFileLastModifiedTime.get(2) + 1);
            }
            else {
                s3 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s2, logFileLastModifiedTime.get(2) + 1);
            }
            final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, getLogsDir(), File.separator, s3));
            if (!file.exists()) {
                file.mkdir();
            }
            for (int i = 0; i < list.length; ++i) {
                final File file2 = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, getLogsDir(), File.separator, list[i]));
                if (file2.isFile()) {
                    file2.renameTo(new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, file.getAbsolutePath(), File.separator, file2.getName())));
                    file2.delete();
                }
            }
        }
        catch (Exception ex) {
            DebugLog.General.error((Object)"Exception thrown trying to initialize LoggerManager, trying to copy old log files.");
            DebugLog.General.error((Object)"Exception: ");
            DebugLog.General.error(ex);
            ex.printStackTrace();
        }
    }
    
    private static Calendar getLogFileLastModifiedTime(final String s) {
        final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, getLogsDir(), File.separator, s));
        final Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(file.lastModified());
        return instance;
    }
    
    public static synchronized void createLogger(final String key, final boolean b) {
        init();
        LoggerManager.s_loggers.put(key, new ZLogger(key, b));
    }
    
    public static String getLogsDir() {
        final String cacheDirSub = ZomboidFileSystem.instance.getCacheDirSub("Logs");
        ZomboidFileSystem.ensureFolderExists(cacheDirSub);
        return new File(cacheDirSub).getAbsolutePath();
    }
    
    public static String getPlayerCoords(final IsoPlayer isoPlayer) {
        return invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, (int)isoPlayer.getX(), (int)isoPlayer.getY(), (int)isoPlayer.getZ());
    }
    
    static {
        LoggerManager.s_isInitialized = false;
        s_loggers = new HashMap<String, ZLogger>();
    }
}

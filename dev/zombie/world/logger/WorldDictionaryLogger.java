// 
// Decompiled by Procyon v0.5.36
// 

package zombie.world.logger;

import java.io.IOException;
import java.io.FileWriter;
import zombie.core.Core;
import java.io.File;
import zombie.ZomboidFileSystem;
import zombie.debug.DebugLog;
import zombie.network.GameClient;
import java.util.ArrayList;

public class WorldDictionaryLogger
{
    private static final ArrayList<Log.BaseLog> _logItems;
    
    public static void reset() {
        WorldDictionaryLogger._logItems.clear();
    }
    
    public static void startLogging() {
        reset();
    }
    
    public static void log(final Log.BaseLog e) {
        if (GameClient.bClient) {
            return;
        }
        WorldDictionaryLogger._logItems.add(e);
    }
    
    public static void log(final String s) {
        log(s, true);
    }
    
    public static void log(final String s, final boolean b) {
        if (GameClient.bClient) {
            return;
        }
        if (b) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        }
        WorldDictionaryLogger._logItems.add(new Log.Comment(s));
    }
    
    public static void saveLog(final String s) throws IOException {
        if (GameClient.bClient) {
            return;
        }
        boolean b = false;
        for (int i = 0; i < WorldDictionaryLogger._logItems.size(); ++i) {
            if (!WorldDictionaryLogger._logItems.get(i).isIgnoreSaveCheck()) {
                b = true;
                break;
            }
        }
        if (!b) {
            return;
        }
        final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getGameModeCacheDir(), File.separator, Core.GameSaveWorld, File.separator));
        if (file.exists() && file.isDirectory()) {
            final File file2 = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getGameModeCacheDir(), File.separator, Core.GameSaveWorld, File.separator, s));
            try {
                final FileWriter fileWriter = new FileWriter(file2, true);
                try {
                    fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.lineSeparator()));
                    fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.lineSeparator()));
                    for (int j = 0; j < WorldDictionaryLogger._logItems.size(); ++j) {
                        WorldDictionaryLogger._logItems.get(j).saveAsText(fileWriter, "\t");
                    }
                    fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.lineSeparator()));
                    fileWriter.close();
                }
                catch (Throwable t) {
                    try {
                        fileWriter.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                    throw t;
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
                throw new IOException("Error saving WorldDictionary log.");
            }
        }
    }
    
    static {
        _logItems = new ArrayList<Log.BaseLog>();
    }
}

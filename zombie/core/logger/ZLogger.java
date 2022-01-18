// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.logger;

import zombie.debug.DebugLog;
import zombie.util.StringUtils;
import java.util.Calendar;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.io.File;

public final class ZLogger
{
    private final String name;
    private final OutputStreams outputStreams;
    private File file;
    private static final SimpleDateFormat s_fileNameSdf;
    private static final SimpleDateFormat s_logSdf;
    private static final long s_maxSizeKo = 10000L;
    
    public ZLogger(final String name, final boolean b) {
        this.outputStreams = new OutputStreams();
        this.file = null;
        this.name = name;
        try {
            this.file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, LoggerManager.getLogsDir(), File.separator, getLoggerName(name)));
            this.outputStreams.file = new PrintStream(this.file);
        }
        catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        if (b) {
            this.outputStreams.console = System.out;
        }
    }
    
    private static String getLoggerName(final String s) {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZLogger.s_fileNameSdf.format(Calendar.getInstance().getTime()), s);
    }
    
    public void write(final String s) {
        this.write(s, null);
    }
    
    public void write(final String s, final String s2) {
        try {
            this.writeUnsafe(s, s2);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public synchronized void writeUnsafe(final String s, final String str) throws Exception {
        final StringBuilder sb = new StringBuilder();
        sb.setLength(0);
        sb.append("[").append(ZLogger.s_logSdf.format(Calendar.getInstance().getTime())).append("]");
        if (!StringUtils.isNullOrEmpty(str)) {
            sb.append("[").append(str).append("]");
        }
        int length = s.length();
        if (s.lastIndexOf(10) == s.length() - 1) {
            --length;
        }
        sb.append(" ").append(s, 0, length).append(".");
        this.outputStreams.println(sb.toString());
        this.checkSizeUnsafe();
    }
    
    public synchronized void write(final Exception ex) {
        ex.printStackTrace(this.outputStreams.file);
        this.checkSize();
    }
    
    private synchronized void checkSize() {
        try {
            this.checkSizeUnsafe();
        }
        catch (Exception ex) {
            DebugLog.General.error((Object)"Exception thrown checking log file size.");
            DebugLog.General.error(ex);
            ex.printStackTrace();
        }
    }
    
    private synchronized void checkSizeUnsafe() throws Exception {
        if (this.file.length() / 1024L > 10000L) {
            this.outputStreams.file.close();
            this.file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, LoggerManager.getLogsDir(), File.separator, getLoggerName(this.name)));
            this.outputStreams.file = new PrintStream(this.file);
        }
    }
    
    static {
        s_fileNameSdf = new SimpleDateFormat("dd-MM-yy_HH-mm-ss");
        s_logSdf = new SimpleDateFormat("dd-MM-yy HH:mm:ss.SSS");
    }
    
    private static class OutputStreams
    {
        public PrintStream file;
        public PrintStream console;
        
        public void println(final String s) {
            if (this.file != null) {
                this.file.println(s);
                this.file.flush();
            }
            if (this.console != null) {
                this.console.println(s);
            }
        }
    }
}

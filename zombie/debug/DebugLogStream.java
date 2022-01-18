// 
// Decompiled by Procyon v0.5.36
// 

package zombie.debug;

import zombie.util.StringUtils;
import java.io.OutputStream;
import java.io.PrintStream;

public final class DebugLogStream extends PrintStream
{
    private final PrintStream m_wrappedStream;
    private final PrintStream m_wrappedWarnStream;
    private final PrintStream m_wrappedErrStream;
    private final IDebugLogFormatter m_formatter;
    public static final String s_prefixErr = "ERROR: ";
    public static final String s_prefixWarn = "WARN : ";
    public static final String s_prefixOut = "LOG  : ";
    public static final String s_prefixDebug = "DEBUG: ";
    
    public DebugLogStream(final PrintStream printStream, final PrintStream wrappedWarnStream, final PrintStream wrappedErrStream, final IDebugLogFormatter formatter) {
        super(printStream);
        this.m_wrappedStream = printStream;
        this.m_wrappedWarnStream = wrappedWarnStream;
        this.m_wrappedErrStream = wrappedErrStream;
        this.m_formatter = formatter;
    }
    
    private void write(final PrintStream printStream, final String s) {
        final String format = this.m_formatter.format(LogSeverity.General, "LOG  : ", "", s);
        if (format != null) {
            printStream.print(format);
        }
    }
    
    private void writeln(final PrintStream printStream, final String s) {
        this.writeln(printStream, LogSeverity.General, "LOG  : ", s);
    }
    
    private void writeln(final PrintStream printStream, final String s, final Object o) {
        this.writeln(printStream, LogSeverity.General, "LOG  : ", s, o);
    }
    
    private void writeln(final PrintStream printStream, final LogSeverity logSeverity, final String s, final String s2) {
        final String format = this.m_formatter.format(logSeverity, s, "", s2);
        if (format != null) {
            printStream.println(format);
        }
    }
    
    private void writeln(final PrintStream printStream, final LogSeverity logSeverity, final String s, final String s2, final Object o) {
        final String format = this.m_formatter.format(logSeverity, s, "", s2, o);
        if (format != null) {
            printStream.println(format);
        }
    }
    
    public static String generateCallerPrefix() {
        final StackTraceElement tryGetCallerTraceElement = tryGetCallerTraceElement(4);
        if (tryGetCallerTraceElement == null) {
            return "(UnknownStack)";
        }
        return getStackTraceElementString(tryGetCallerTraceElement, false);
    }
    
    public static StackTraceElement tryGetCallerTraceElement(final int n) {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length <= n) {
            return null;
        }
        return stackTrace[n];
    }
    
    public static String getStackTraceElementString(final StackTraceElement stackTraceElement, final boolean b) {
        if (stackTraceElement == null) {
            return "(UnknownStack)";
        }
        final String unqualifiedClassName = getUnqualifiedClassName(stackTraceElement.getClassName());
        final String methodName = stackTraceElement.getMethodName();
        final int lineNumber = stackTraceElement.getLineNumber();
        String s;
        if (stackTraceElement.isNativeMethod()) {
            s = " (Native Method)";
        }
        else if (b && lineNumber > -1) {
            s = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, lineNumber);
        }
        else {
            s = "";
        }
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, unqualifiedClassName, methodName, s);
    }
    
    public static String getTopStackTraceString(final Throwable t) {
        if (t == null) {
            return "Null Exception";
        }
        final StackTraceElement[] stackTrace = t.getStackTrace();
        if (stackTrace == null || stackTrace.length == 0) {
            return "No Stack Trace Available";
        }
        return getStackTraceElementString(stackTrace[0], true);
    }
    
    public void printStackTrace() {
        this.printStackTrace(0);
    }
    
    public void printStackTrace(final int a) {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int n = (a == 0) ? stackTrace.length : Math.min(a, stackTrace.length), i = 0; i < n; ++i) {
            this.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, stackTrace[i].toString()));
        }
    }
    
    private static String getUnqualifiedClassName(final String s) {
        String substring = s;
        final int lastIndex = s.lastIndexOf(46);
        if (lastIndex > -1 && lastIndex < s.length() - 1) {
            substring = s.substring(lastIndex + 1);
        }
        return substring;
    }
    
    public void debugln(final String s) {
        if (this.m_formatter.isLogEnabled(LogSeverity.General)) {
            this.m_wrappedStream.println(this.m_formatter.format(LogSeverity.General, "DEBUG: ", invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, StringUtils.leftJustify(generateCallerPrefix(), 36)), "%s", (Object)s));
        }
    }
    
    public void debugln(final String s, final Object o) {
        if (this.m_formatter.isLogEnabled(LogSeverity.General)) {
            this.m_wrappedStream.println(this.m_formatter.format(LogSeverity.General, "DEBUG: ", invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, StringUtils.leftJustify(generateCallerPrefix(), 36)), s, o));
        }
    }
    
    public void debugln(final String s, final Object o, final Object o2) {
        if (this.m_formatter.isLogEnabled(LogSeverity.General)) {
            this.m_wrappedStream.println(this.m_formatter.format(LogSeverity.General, "DEBUG: ", invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, StringUtils.leftJustify(generateCallerPrefix(), 36)), s, o, o2));
        }
    }
    
    public void debugln(final String s, final Object o, final Object o2, final Object o3) {
        if (this.m_formatter.isLogEnabled(LogSeverity.General)) {
            this.m_wrappedStream.println(this.m_formatter.format(LogSeverity.General, "DEBUG: ", invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, StringUtils.leftJustify(generateCallerPrefix(), 36)), s, o, o2, o3));
        }
    }
    
    public void debugln(final String s, final Object o, final Object o2, final Object o3, final Object o4) {
        if (this.m_formatter.isLogEnabled(LogSeverity.General)) {
            this.m_wrappedStream.println(this.m_formatter.format(LogSeverity.General, "DEBUG: ", invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, StringUtils.leftJustify(generateCallerPrefix(), 36)), s, o, o2, o3, o4));
        }
    }
    
    public void debugln(final String s, final Object o, final Object o2, final Object o3, final Object o4, final Object o5) {
        if (this.m_formatter.isLogEnabled(LogSeverity.General)) {
            this.m_wrappedStream.println(this.m_formatter.format(LogSeverity.General, "DEBUG: ", invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, StringUtils.leftJustify(generateCallerPrefix(), 36)), s, o, o2, o3, o4, o5));
        }
    }
    
    public void debugln(final String s, final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6) {
        if (this.m_formatter.isLogEnabled(LogSeverity.General)) {
            this.m_wrappedStream.println(this.m_formatter.format(LogSeverity.General, "DEBUG: ", invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, StringUtils.leftJustify(generateCallerPrefix(), 36)), s, o, o2, o3, o4, o5, o6));
        }
    }
    
    @Override
    public void print(final boolean b) {
        this.write(this.m_wrappedStream, b ? "true" : "false");
    }
    
    @Override
    public void print(final char c) {
        this.write(this.m_wrappedStream, String.valueOf(c));
    }
    
    @Override
    public void print(final int i) {
        this.write(this.m_wrappedStream, String.valueOf(i));
    }
    
    @Override
    public void print(final long l) {
        this.write(this.m_wrappedStream, String.valueOf(l));
    }
    
    @Override
    public void print(final float f) {
        this.write(this.m_wrappedStream, String.valueOf(f));
    }
    
    @Override
    public void print(final double d) {
        this.write(this.m_wrappedStream, String.valueOf(d));
    }
    
    @Override
    public void print(final String obj) {
        this.write(this.m_wrappedStream, String.valueOf(obj));
    }
    
    @Override
    public void print(final Object obj) {
        this.write(this.m_wrappedStream, String.valueOf(obj));
    }
    
    @Override
    public PrintStream printf(final String format, final Object... args) {
        this.write(this.m_wrappedStream, String.format(format, args));
        return this;
    }
    
    @Override
    public void println() {
        this.writeln(this.m_wrappedStream, "");
    }
    
    @Override
    public void println(final boolean b) {
        this.writeln(this.m_wrappedStream, "%s", String.valueOf(b));
    }
    
    @Override
    public void println(final char c) {
        this.writeln(this.m_wrappedStream, "%s", String.valueOf(c));
    }
    
    @Override
    public void println(final int i) {
        this.writeln(this.m_wrappedStream, "%s", String.valueOf(i));
    }
    
    @Override
    public void println(final long l) {
        this.writeln(this.m_wrappedStream, "%s", String.valueOf(l));
    }
    
    @Override
    public void println(final float f) {
        this.writeln(this.m_wrappedStream, "%s", String.valueOf(f));
    }
    
    @Override
    public void println(final double d) {
        this.writeln(this.m_wrappedStream, "%s", String.valueOf(d));
    }
    
    @Override
    public void println(final char[] data) {
        this.writeln(this.m_wrappedStream, "%s", String.valueOf(data));
    }
    
    @Override
    public void println(final String s) {
        this.writeln(this.m_wrappedStream, "%s", s);
    }
    
    @Override
    public void println(final Object o) {
        this.writeln(this.m_wrappedStream, "%s", o);
    }
    
    public void println(final String s, final Object o) {
        final String format = this.m_formatter.format(LogSeverity.General, "LOG  : ", "", s, o);
        if (format != null) {
            this.m_wrappedStream.println(format);
        }
    }
    
    public void println(final String s, final Object o, final Object o2) {
        final String format = this.m_formatter.format(LogSeverity.General, "LOG  : ", "", s, o, o2);
        if (format != null) {
            this.m_wrappedStream.println(format);
        }
    }
    
    public void println(final String s, final Object o, final Object o2, final Object o3) {
        final String format = this.m_formatter.format(LogSeverity.General, "LOG  : ", "", s, o, o2, o3);
        if (format != null) {
            this.m_wrappedStream.println(format);
        }
    }
    
    public void println(final String s, final Object o, final Object o2, final Object o3, final Object o4) {
        final String format = this.m_formatter.format(LogSeverity.General, "LOG  : ", "", s, o, o2, o3, o4);
        if (format != null) {
            this.m_wrappedStream.println(format);
        }
    }
    
    public void println(final String s, final Object o, final Object o2, final Object o3, final Object o4, final Object o5) {
        final String format = this.m_formatter.format(LogSeverity.General, "LOG  : ", "", s, o, o2, o3, o4, o5);
        if (format != null) {
            this.m_wrappedStream.println(format);
        }
    }
    
    public void println(final String s, final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6) {
        final String format = this.m_formatter.format(LogSeverity.General, "LOG  : ", "", s, o, o2, o3, o4, o5, o6);
        if (format != null) {
            this.m_wrappedStream.println(format);
        }
    }
    
    public void println(final String s, final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7) {
        final String format = this.m_formatter.format(LogSeverity.General, "LOG  : ", "", s, o, o2, o3, o4, o5, o6, o7);
        if (format != null) {
            this.m_wrappedStream.println(format);
        }
    }
    
    public void println(final String s, final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8) {
        final String format = this.m_formatter.format(LogSeverity.General, "LOG  : ", "", s, o, o2, o3, o4, o5, o6, o7, o8);
        if (format != null) {
            this.m_wrappedStream.println(format);
        }
    }
    
    public void println(final String s, final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8, final Object o9) {
        final String format = this.m_formatter.format(LogSeverity.General, "LOG  : ", "", s, o, o2, o3, o4, o5, o6, o7, o8, o9);
        if (format != null) {
            this.m_wrappedStream.println(format);
        }
    }
    
    public void error(final Object obj) {
        this.writeln(this.m_wrappedErrStream, LogSeverity.Error, "ERROR: ", invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, generateCallerPrefix(), String.valueOf(obj)));
    }
    
    public void error(final String format, final Object... args) {
        this.writeln(this.m_wrappedErrStream, LogSeverity.Error, "ERROR: ", invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, generateCallerPrefix(), String.format(format, args)));
    }
    
    public void warn(final Object obj) {
        this.writeln(this.m_wrappedWarnStream, LogSeverity.Warning, "WARN : ", invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, generateCallerPrefix(), String.valueOf(obj)));
    }
    
    public void warn(final String format, final Object... args) {
        this.writeln(this.m_wrappedWarnStream, LogSeverity.Warning, "WARN : ", invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, generateCallerPrefix(), String.format(format, args)));
    }
    
    public void printUnitTest(final String s, final boolean b, final Object... array) {
        if (!b) {
            this.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s), array);
        }
        else {
            this.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s), (Object)array);
        }
    }
    
    public void printException(final Throwable t, final String s, final LogSeverity logSeverity) {
        this.printException(t, s, generateCallerPrefix(), logSeverity);
    }
    
    public void printException(final Throwable t, final String s, final String s2, final LogSeverity obj) {
        if (t == null) {
            this.warn((Object)"Null exception passed.");
            return;
        }
        String s3 = null;
        PrintStream s4 = null;
        boolean b = false;
        switch (obj) {
            case Trace:
            case General: {
                s3 = "LOG  : ";
                s4 = this.m_wrappedStream;
                b = false;
                break;
            }
            case Warning: {
                s3 = "WARN : ";
                s4 = this.m_wrappedWarnStream;
                b = false;
                break;
            }
            default: {
                this.error("Unhandled LogSeverity: %s. Defaulted to Error.", String.valueOf(obj));
            }
            case Error: {
                s3 = "ERROR: ";
                s4 = this.m_wrappedErrStream;
                b = true;
                break;
            }
        }
        if (s != null) {
            this.writeln(s4, obj, s3, String.format("%s> Exception thrown %s at %s. Message: %s", s2, t.toString(), getTopStackTraceString(t), s));
        }
        else {
            this.writeln(s4, obj, s3, String.format("%s> Exception thrown %s at %s.", s2, t.toString(), getTopStackTraceString(t)));
        }
        if (b) {
            this.error((Object)"Stack trace:");
            t.printStackTrace(s4);
        }
    }
}

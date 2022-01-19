// 
// Decompiled by Procyon v0.5.36
// 

package zombie.debug;

class GeneralErrorDebugLogFormatter implements IDebugLogFormatter
{
    @Override
    public boolean isLogEnabled(final LogSeverity logSeverity) {
        return DebugLog.isLogEnabled(logSeverity, DebugType.General);
    }
    
    @Override
    public String format(final LogSeverity logSeverity, final String s, final String s2, final String s3) {
        return DebugLog.formatString(DebugType.General, logSeverity, "ERROR: ", s2, s3);
    }
    
    @Override
    public String format(final LogSeverity logSeverity, final String s, final String s2, final String s3, final Object o) {
        return DebugLog.formatString(DebugType.General, logSeverity, "ERROR: ", s2, s3, o);
    }
    
    @Override
    public String format(final LogSeverity logSeverity, final String s, final String s2, final String s3, final Object o, final Object o2) {
        return DebugLog.formatString(DebugType.General, logSeverity, "ERROR: ", s2, s3, o, o2);
    }
    
    @Override
    public String format(final LogSeverity logSeverity, final String s, final String s2, final String s3, final Object o, final Object o2, final Object o3) {
        return DebugLog.formatString(DebugType.General, logSeverity, "ERROR: ", s2, s3, o, o2, o3);
    }
    
    @Override
    public String format(final LogSeverity logSeverity, final String s, final String s2, final String s3, final Object o, final Object o2, final Object o3, final Object o4) {
        return DebugLog.formatString(DebugType.General, logSeverity, "ERROR: ", s2, s3, o, o2, o3, o4);
    }
    
    @Override
    public String format(final LogSeverity logSeverity, final String s, final String s2, final String s3, final Object o, final Object o2, final Object o3, final Object o4, final Object o5) {
        return DebugLog.formatString(DebugType.General, logSeverity, "ERROR: ", s2, s3, o, o2, o3, o4, o5);
    }
    
    @Override
    public String format(final LogSeverity logSeverity, final String s, final String s2, final String s3, final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6) {
        return DebugLog.formatString(DebugType.General, logSeverity, "ERROR: ", s2, s3, o, o2, o3, o4, o5, o6);
    }
    
    @Override
    public String format(final LogSeverity logSeverity, final String s, final String s2, final String s3, final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7) {
        return DebugLog.formatString(DebugType.General, logSeverity, "ERROR: ", s2, s3, o, o2, o3, o4, o5, o6, o7);
    }
    
    @Override
    public String format(final LogSeverity logSeverity, final String s, final String s2, final String s3, final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8) {
        return DebugLog.formatString(DebugType.General, logSeverity, "ERROR: ", s2, s3, o, o2, o3, o4, o5, o6, o7, o8);
    }
    
    @Override
    public String format(final LogSeverity logSeverity, final String s, final String s2, final String s3, final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8, final Object o9) {
        return DebugLog.formatString(DebugType.General, logSeverity, "ERROR: ", s2, s3, o, o2, o3, o4, o5, o6, o7, o8, o9);
    }
}

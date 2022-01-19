// 
// Decompiled by Procyon v0.5.36
// 

package zombie.debug;

public interface IDebugLogFormatter
{
    boolean isLogEnabled(final LogSeverity p0);
    
    String format(final LogSeverity p0, final String p1, final String p2, final String p3);
    
    String format(final LogSeverity p0, final String p1, final String p2, final String p3, final Object p4);
    
    String format(final LogSeverity p0, final String p1, final String p2, final String p3, final Object p4, final Object p5);
    
    String format(final LogSeverity p0, final String p1, final String p2, final String p3, final Object p4, final Object p5, final Object p6);
    
    String format(final LogSeverity p0, final String p1, final String p2, final String p3, final Object p4, final Object p5, final Object p6, final Object p7);
    
    String format(final LogSeverity p0, final String p1, final String p2, final String p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8);
    
    String format(final LogSeverity p0, final String p1, final String p2, final String p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9);
    
    String format(final LogSeverity p0, final String p1, final String p2, final String p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9, final Object p10);
    
    String format(final LogSeverity p0, final String p1, final String p2, final String p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9, final Object p10, final Object p11);
    
    String format(final LogSeverity p0, final String p1, final String p2, final String p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9, final Object p10, final Object p11, final Object p12);
}

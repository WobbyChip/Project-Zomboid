// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.znet;

public class ZNet
{
    public static native void init();
    
    public static native void setLogLevel(final int p0);
    
    private static void logPutsCallback(final String s) {
        System.out.print(invokedynamic(makeConcatWithConstants:(JLjava/lang/String;)Ljava/lang/String;, System.currentTimeMillis(), s));
    }
}

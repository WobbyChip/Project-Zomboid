// 
// Decompiled by Procyon v0.5.36
// 

package zombie.vehicles;

import zombie.debug.DebugLog;
import java.nio.ByteBuffer;

public class Clipper
{
    private long address;
    final ByteBuffer bb;
    
    public static void init() {
        String s = "";
        if ("1".equals(System.getProperty("zomboid.debuglibs.clipper"))) {
            DebugLog.log("***** Loading debug version of PZClipper");
            s = "d";
        }
        if (System.getProperty("os.name").contains("OS X")) {
            System.loadLibrary("PZClipper");
        }
        else if (System.getProperty("sun.arch.data.model").equals("64")) {
            System.loadLibrary(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        }
        else {
            System.loadLibrary(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        }
        n_init();
    }
    
    public Clipper() {
        this.bb = ByteBuffer.allocateDirect(64);
        this.newInstance();
    }
    
    private native void newInstance();
    
    public native void clear();
    
    public native void addPath(final int p0, final ByteBuffer p1, final boolean p2);
    
    public native void addLine(final float p0, final float p1, final float p2, final float p3);
    
    public native void addAABB(final float p0, final float p1, final float p2, final float p3);
    
    public void addAABBBevel(final float n, final float n2, final float n3, final float n4, final float n5) {
        this.bb.clear();
        this.bb.putFloat(n + n5);
        this.bb.putFloat(n2);
        this.bb.putFloat(n3 - n5);
        this.bb.putFloat(n2);
        this.bb.putFloat(n3);
        this.bb.putFloat(n2 + n5);
        this.bb.putFloat(n3);
        this.bb.putFloat(n4 - n5);
        this.bb.putFloat(n3 - n5);
        this.bb.putFloat(n4);
        this.bb.putFloat(n + n5);
        this.bb.putFloat(n4);
        this.bb.putFloat(n);
        this.bb.putFloat(n4 - n5);
        this.bb.putFloat(n);
        this.bb.putFloat(n2 + n5);
        this.addPath(this.bb.position() / 4 / 2, this.bb, false);
    }
    
    public native void addPolygon(final float p0, final float p1, final float p2, final float p3, final float p4, final float p5, final float p6, final float p7);
    
    public native void clipAABB(final float p0, final float p1, final float p2, final float p3);
    
    public int generatePolygons() {
        return this.generatePolygons(0.0);
    }
    
    public native int generatePolygons(final double p0);
    
    public native int getPolygon(final int p0, final ByteBuffer p1);
    
    public native int generateTriangulatePolygons(final int p0, final int p1);
    
    public native int triangulate(final int p0, final ByteBuffer p1);
    
    public static native void n_init();
    
    private static void writeToStdErr(final String x) {
        System.err.println(x);
    }
}

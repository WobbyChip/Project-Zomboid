// 
// Decompiled by Procyon v0.5.36
// 

package zombie.vehicles;

import java.nio.ByteBuffer;

public final class ClipperOffset
{
    private final long address;
    
    public ClipperOffset() {
        this.address = this.newInstance();
    }
    
    private native long newInstance();
    
    public native void clear();
    
    public native void addPath(final int p0, final ByteBuffer p1, final int p2, final int p3);
    
    public native void execute(final double p0);
    
    public native int getPolygonCount();
    
    public native int getPolygon(final int p0, final ByteBuffer p1);
    
    public enum JoinType
    {
        jtSquare, 
        jtRound, 
        jtMiter;
        
        private static /* synthetic */ JoinType[] $values() {
            return new JoinType[] { JoinType.jtSquare, JoinType.jtRound, JoinType.jtMiter };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    public enum EndType
    {
        etClosedPolygon, 
        etClosedLine, 
        etOpenButt, 
        etOpenSquare, 
        etOpenRound;
        
        private static /* synthetic */ EndType[] $values() {
            return new EndType[] { EndType.etClosedPolygon, EndType.etClosedLine, EndType.etOpenButt, EndType.etOpenSquare, EndType.etOpenRound };
        }
        
        static {
            $VALUES = $values();
        }
    }
}

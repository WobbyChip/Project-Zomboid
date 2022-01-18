// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

public class IsoDirectionSet
{
    public int set;
    
    public IsoDirectionSet() {
        this.set = 0;
    }
    
    public static IsoDirections rotate(final IsoDirections isoDirections, int n) {
        n += isoDirections.index();
        n %= 8;
        return IsoDirections.fromIndex(n);
    }
    
    public IsoDirections getNext() {
        for (int i = 0; i < 8; ++i) {
            final int n = 1 << i;
            if ((this.set & n) != 0x0) {
                this.set ^= n;
                return IsoDirections.fromIndex(i);
            }
        }
        return IsoDirections.Max;
    }
}

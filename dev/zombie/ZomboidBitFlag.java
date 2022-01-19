// 
// Decompiled by Procyon v0.5.36
// 

package zombie;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.DataOutputStream;
import zombie.iso.SpriteDetails.IsoObjectType;
import java.util.Collection;
import zombie.iso.SpriteDetails.IsoFlagType;
import java.util.EnumSet;

public final class ZomboidBitFlag
{
    final EnumSet<IsoFlagType> isoFlagTypeES;
    
    public ZomboidBitFlag(final int n) {
        this.isoFlagTypeES = EnumSet.noneOf(IsoFlagType.class);
    }
    
    public ZomboidBitFlag(final ZomboidBitFlag zomboidBitFlag) {
        this.isoFlagTypeES = EnumSet.noneOf(IsoFlagType.class);
        if (zomboidBitFlag == null) {
            return;
        }
        this.isoFlagTypeES.addAll((Collection<?>)zomboidBitFlag.isoFlagTypeES);
    }
    
    public void set(final int n, final boolean b) {
        if (n >= IsoFlagType.MAX.index()) {
            return;
        }
        if (b) {
            this.isoFlagTypeES.add(IsoFlagType.fromIndex(n));
        }
        else {
            this.isoFlagTypeES.remove(IsoFlagType.fromIndex(n));
        }
    }
    
    public void clear() {
        this.isoFlagTypeES.clear();
    }
    
    public boolean isSet(final int n) {
        return this.isoFlagTypeES.contains(IsoFlagType.fromIndex(n));
    }
    
    public boolean isSet(final IsoFlagType o) {
        return this.isoFlagTypeES.contains(o);
    }
    
    public void set(final IsoFlagType isoFlagType, final boolean b) {
        if (b) {
            this.isoFlagTypeES.add(isoFlagType);
        }
        else {
            this.isoFlagTypeES.remove(isoFlagType);
        }
    }
    
    public boolean isSet(final IsoObjectType isoObjectType) {
        return this.isSet(isoObjectType.index());
    }
    
    public void set(final IsoObjectType isoObjectType, final boolean b) {
        this.set(isoObjectType.index(), b);
    }
    
    public void Or(final ZomboidBitFlag zomboidBitFlag) {
        this.isoFlagTypeES.addAll((Collection<?>)zomboidBitFlag.isoFlagTypeES);
    }
    
    public void save(final DataOutputStream dataOutputStream) throws IOException {
    }
    
    public void load(final DataInputStream dataInputStream) throws IOException {
    }
    
    public void getFromLong(final long n) {
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package zombie.vehicles;

import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.scripting.objects.VehicleScript;

public final class VehicleDoor
{
    protected VehiclePart part;
    protected boolean open;
    protected boolean locked;
    protected boolean lockBroken;
    
    public VehicleDoor(final VehiclePart part) {
        this.part = part;
    }
    
    public void init(final VehicleScript.Door door) {
        this.open = false;
        this.locked = false;
        this.lockBroken = false;
    }
    
    public boolean isOpen() {
        return this.open;
    }
    
    public void setOpen(final boolean open) {
        this.open = open;
    }
    
    public boolean isLocked() {
        return this.locked;
    }
    
    public void setLocked(final boolean locked) {
        this.locked = locked;
    }
    
    public boolean isLockBroken() {
        return this.lockBroken;
    }
    
    public void setLockBroken(final boolean lockBroken) {
        this.lockBroken = lockBroken;
    }
    
    public void save(final ByteBuffer byteBuffer) throws IOException {
        byteBuffer.put((byte)(this.open ? 1 : 0));
        byteBuffer.put((byte)(this.locked ? 1 : 0));
        byteBuffer.put((byte)(this.lockBroken ? 1 : 0));
    }
    
    public void load(final ByteBuffer byteBuffer, final int n) throws IOException {
        this.open = (byteBuffer.get() == 1);
        this.locked = (byteBuffer.get() == 1);
        this.lockBroken = (byteBuffer.get() == 1);
    }
}

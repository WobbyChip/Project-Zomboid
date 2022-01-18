// 
// Decompiled by Procyon v0.5.36
// 

package zombie.vehicles;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.joml.Vector3f;

public final class VehicleLight
{
    public boolean active;
    public final Vector3f offset;
    public float dist;
    public float intensity;
    public float dot;
    public int focusing;
    
    public VehicleLight() {
        this.offset = new Vector3f();
        this.dist = 16.0f;
        this.intensity = 1.0f;
        this.dot = 0.96f;
        this.focusing = 0;
    }
    
    public boolean getActive() {
        return this.active;
    }
    
    public void setActive(final boolean active) {
        this.active = active;
    }
    
    @Deprecated
    public int getFocusing() {
        return this.focusing;
    }
    
    public float getIntensity() {
        return this.intensity;
    }
    
    @Deprecated
    public float getDistanization() {
        return this.dist;
    }
    
    @Deprecated
    public boolean canFocusingUp() {
        return this.focusing != 0;
    }
    
    @Deprecated
    public boolean canFocusingDown() {
        return this.focusing != 1;
    }
    
    @Deprecated
    public void setFocusingUp() {
        if (this.focusing == 0) {
            return;
        }
        if (this.focusing < 4) {
            this.focusing = 4;
        }
        else if (this.focusing < 10) {
            this.focusing = 10;
        }
        else if (this.focusing < 30) {
            this.focusing = 30;
        }
        else if (this.focusing < 100) {
            this.focusing = 100;
        }
        else {
            this.focusing = 0;
        }
    }
    
    @Deprecated
    public void setFocusingDown() {
        if (this.focusing == 1) {
            return;
        }
        if (this.focusing == 0) {
            this.focusing = 100;
        }
        else if (this.focusing > 30) {
            this.focusing = 30;
        }
        else if (this.focusing > 10) {
            this.focusing = 10;
        }
        else if (this.focusing > 4) {
            this.focusing = 4;
        }
        else {
            this.focusing = 1;
        }
    }
    
    public void save(final ByteBuffer byteBuffer) throws IOException {
        byteBuffer.put((byte)(this.active ? 1 : 0));
        byteBuffer.putFloat(this.offset.x);
        byteBuffer.putFloat(this.offset.y);
        byteBuffer.putFloat(this.intensity);
        byteBuffer.putFloat(this.dist);
        byteBuffer.putInt(this.focusing);
    }
    
    public void load(final ByteBuffer byteBuffer, final int n) throws IOException {
        this.active = (byteBuffer.get() == 1);
        if (n >= 135) {
            this.offset.x = byteBuffer.getFloat();
            this.offset.y = byteBuffer.getFloat();
            this.intensity = byteBuffer.getFloat();
            this.dist = byteBuffer.getFloat();
            this.focusing = byteBuffer.getInt();
        }
    }
}

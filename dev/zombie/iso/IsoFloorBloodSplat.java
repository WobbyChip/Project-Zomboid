// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import java.io.IOException;
import zombie.GameTime;
import java.nio.ByteBuffer;
import zombie.iso.sprite.IsoSprite;
import java.util.HashMap;

public class IsoFloorBloodSplat
{
    public static final float FADE_HOURS = 72.0f;
    public static HashMap<String, IsoSprite> SpriteMap;
    public static String[] FloorBloodTypes;
    public float x;
    public float y;
    public float z;
    public int Type;
    public float worldAge;
    public int index;
    public int fade;
    IsoChunk chunk;
    
    public IsoFloorBloodSplat() {
    }
    
    public IsoFloorBloodSplat(final float x, final float y, final float z, final int type, final float worldAge) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.Type = type;
        this.worldAge = worldAge;
    }
    
    public void save(final ByteBuffer byteBuffer) {
        int n = (int)(this.x / 10.0f * 255.0f);
        if (n < 0) {
            n = 0;
        }
        if (n > 255) {
            n = 255;
        }
        int n2 = (int)(this.y / 10.0f * 255.0f);
        if (n2 < 0) {
            n2 = 0;
        }
        if (n2 > 255) {
            n2 = 255;
        }
        int n3 = (int)(this.z / 8.0f * 255.0f);
        if (n3 < 0) {
            n3 = 0;
        }
        if (n3 > 255) {
            n3 = 255;
        }
        byteBuffer.put((byte)n);
        byteBuffer.put((byte)n2);
        byteBuffer.put((byte)n3);
        byteBuffer.put((byte)this.Type);
        byteBuffer.putFloat(this.worldAge);
        byteBuffer.put((byte)this.index);
    }
    
    public void load(final ByteBuffer byteBuffer, final int n) throws IOException {
        if (n >= 65) {
            this.x = (byteBuffer.get() & 0xFF) / 255.0f * 10.0f;
            this.y = (byteBuffer.get() & 0xFF) / 255.0f * 10.0f;
            this.z = (byteBuffer.get() & 0xFF) / 255.0f * 8.0f;
            this.Type = byteBuffer.get();
            this.worldAge = byteBuffer.getFloat();
            if (n >= 73) {
                this.index = byteBuffer.get();
            }
        }
        else {
            this.x = byteBuffer.getFloat();
            this.y = byteBuffer.getFloat();
            this.z = byteBuffer.getFloat();
            this.Type = byteBuffer.getInt();
            this.worldAge = (float)GameTime.getInstance().getWorldAgeHours();
        }
    }
    
    static {
        IsoFloorBloodSplat.SpriteMap = new HashMap<String, IsoSprite>();
        IsoFloorBloodSplat.FloorBloodTypes = new String[] { "blood_floor_small_01", "blood_floor_small_02", "blood_floor_small_03", "blood_floor_small_04", "blood_floor_small_05", "blood_floor_small_06", "blood_floor_small_07", "blood_floor_small_08", "blood_floor_med_01", "blood_floor_med_02", "blood_floor_med_03", "blood_floor_med_04", "blood_floor_med_05", "blood_floor_med_06", "blood_floor_med_07", "blood_floor_med_08", "blood_floor_large_01", "blood_floor_large_02", "blood_floor_large_03", "blood_floor_large_04", "blood_floor_large_05" };
    }
}

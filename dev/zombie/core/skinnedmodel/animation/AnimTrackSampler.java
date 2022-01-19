// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.animation;

import org.lwjgl.util.vector.Matrix4f;

public interface AnimTrackSampler
{
    float getTotalTime();
    
    boolean isLooped();
    
    void moveToTime(final float p0);
    
    float getCurrentTime();
    
    void getBoneMatrix(final int p0, final Matrix4f p1);
    
    int getNumBones();
}

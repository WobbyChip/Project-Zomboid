// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.animation.sharedskele;

import zombie.core.skinnedmodel.animation.AnimationClip;
import java.util.HashMap;

public class SharedSkeleAnimationRepository
{
    private final HashMap<AnimationClip, SharedSkeleAnimationTrack> m_tracksMap;
    
    public SharedSkeleAnimationRepository() {
        this.m_tracksMap = new HashMap<AnimationClip, SharedSkeleAnimationTrack>();
    }
    
    public SharedSkeleAnimationTrack getTrack(final AnimationClip key) {
        return this.m_tracksMap.get(key);
    }
    
    public void setTrack(final AnimationClip key, final SharedSkeleAnimationTrack value) {
        this.m_tracksMap.put(key, value);
    }
}

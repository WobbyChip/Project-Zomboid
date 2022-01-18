// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.animation;

import java.util.ArrayList;
import org.lwjgl.util.vector.Quaternion;
import java.util.List;

public final class AnimationClip
{
    public final String Name;
    public StaticAnimation staticClip;
    private final KeyframeByBoneIndexElement[] m_KeyFramesByBoneIndex;
    public float Duration;
    private final List<Keyframe> m_rootMotionKeyframes;
    private final Keyframe[] KeyframeArray;
    private static final Quaternion orientation;
    
    public AnimationClip(final float duration, final List<Keyframe> list, final String name, final boolean b) {
        this.m_rootMotionKeyframes = new ArrayList<Keyframe>();
        this.Duration = duration;
        this.KeyframeArray = list.toArray(new Keyframe[0]);
        this.Name = name;
        this.m_KeyFramesByBoneIndex = new KeyframeByBoneIndexElement[60];
        final ArrayList<Keyframe> list2 = new ArrayList<Keyframe>();
        final int n = this.KeyframeArray.length - (b ? 0 : 1);
        for (int i = 0; i < 60; ++i) {
            list2.clear();
            for (int j = 0; j < n; ++j) {
                final Keyframe e = this.KeyframeArray[j];
                if (e.Bone == i) {
                    list2.add(e);
                }
            }
            this.m_KeyFramesByBoneIndex[i] = new KeyframeByBoneIndexElement(list2);
        }
    }
    
    public Keyframe[] getBoneFramesAt(final int n) {
        return this.m_KeyFramesByBoneIndex[n].m_keyframes;
    }
    
    public int getRootMotionFrameCount() {
        return this.m_rootMotionKeyframes.size();
    }
    
    public Keyframe getRootMotionFrameAt(final int n) {
        return this.m_rootMotionKeyframes.get(n);
    }
    
    public Keyframe[] getKeyframes() {
        return this.KeyframeArray;
    }
    
    static {
        orientation = new Quaternion(-0.07107f, 0.0f, 0.0f, 0.07107f);
    }
    
    private static class KeyframeByBoneIndexElement
    {
        final Keyframe[] m_keyframes;
        
        KeyframeByBoneIndexElement(final List<Keyframe> list) {
            this.m_keyframes = list.toArray(new Keyframe[0]);
        }
    }
}

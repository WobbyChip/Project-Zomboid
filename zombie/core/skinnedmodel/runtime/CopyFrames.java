// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.runtime;

import zombie.core.skinnedmodel.animation.AnimationClip;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.animation.Keyframe;
import java.util.List;
import java.util.Iterator;
import zombie.core.math.PZMath;
import zombie.scripting.ScriptParser;

public final class CopyFrames implements IRuntimeAnimationCommand
{
    protected int m_frame;
    protected int m_FPS;
    protected String m_source;
    protected int m_sourceFrame1;
    protected int m_sourceFrame2;
    protected int m_sourceFPS;
    
    public CopyFrames() {
        this.m_FPS = 30;
        this.m_sourceFPS = 30;
    }
    
    @Override
    public void parse(final ScriptParser.Block block) {
        for (final ScriptParser.Value value : block.values) {
            final String trim = value.getKey().trim();
            final String trim2 = value.getValue().trim();
            if ("source".equalsIgnoreCase(trim)) {
                this.m_source = trim2;
            }
            else if ("frame".equalsIgnoreCase(trim)) {
                this.m_frame = PZMath.tryParseInt(trim2, 1);
            }
            else if ("sourceFrame1".equalsIgnoreCase(trim)) {
                this.m_sourceFrame1 = PZMath.tryParseInt(trim2, 1);
            }
            else {
                if (!"sourceFrame2".equalsIgnoreCase(trim)) {
                    continue;
                }
                this.m_sourceFrame2 = PZMath.tryParseInt(trim2, 1);
            }
        }
    }
    
    @Override
    public void exec(final List<Keyframe> list) {
        final AnimationClip animationClip = ModelManager.instance.getAnimationClip(this.m_source);
        for (int i = 0; i < 60; ++i) {
            final Keyframe[] boneFrames = animationClip.getBoneFramesAt(i);
            if (boneFrames.length != 0) {
                for (int j = this.m_sourceFrame1; j <= this.m_sourceFrame2; ++j) {
                    final Keyframe keyframe = boneFrames[0];
                    final Keyframe keyframe2 = new Keyframe();
                    keyframe2.Bone = keyframe.Bone;
                    keyframe2.BoneName = keyframe.BoneName;
                    keyframe2.Time = (this.m_frame - 1 + (j - this.m_sourceFrame1)) / (float)this.m_FPS;
                    keyframe2.Position = KeyframeUtil.GetKeyFramePosition(boneFrames, (j - 1) / (float)this.m_sourceFPS, animationClip.Duration);
                    keyframe2.Rotation = KeyframeUtil.GetKeyFrameRotation(boneFrames, (j - 1) / (float)this.m_sourceFPS, animationClip.Duration);
                    keyframe2.Scale = keyframe.Scale;
                    list.add(keyframe2);
                }
            }
        }
    }
}

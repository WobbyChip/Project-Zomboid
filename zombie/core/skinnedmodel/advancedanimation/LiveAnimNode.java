// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.advancedanimation;

import zombie.core.skinnedmodel.animation.BoneAxis;
import zombie.debug.DebugOptions;
import zombie.util.StringUtils;
import zombie.core.Rand;
import zombie.core.math.PZMath;
import java.util.function.Consumer;
import zombie.util.list.PZArrayUtil;
import zombie.util.Lambda;
import java.util.ArrayList;
import zombie.util.Pool;
import zombie.core.skinnedmodel.animation.AnimationTrack;
import java.util.List;
import zombie.core.skinnedmodel.animation.IAnimListener;
import zombie.util.PooledObject;

public class LiveAnimNode extends PooledObject implements IAnimListener
{
    private AnimNode m_sourceNode;
    private AnimLayer m_animLayer;
    private boolean m_active;
    private boolean m_wasActive;
    boolean m_TransitioningOut;
    private float m_Weight;
    private float m_RawWeight;
    private boolean m_isNew;
    private int m_layerIdx;
    private final TransitionIn m_transitionIn;
    final List<AnimationTrack> m_AnimationTracks;
    float m_NodeAnimTime;
    float m_PrevNodeAnimTime;
    private boolean m_blendingIn;
    private boolean m_blendingOut;
    private AnimTransition m_transitionOut;
    private static final Pool<LiveAnimNode> s_pool;
    
    protected LiveAnimNode() {
        this.m_transitionIn = new TransitionIn();
        this.m_AnimationTracks = new ArrayList<AnimationTrack>();
    }
    
    public static LiveAnimNode alloc(final AnimLayer animLayer, final AnimNode sourceNode, final int layerIdx) {
        final LiveAnimNode liveAnimNode = LiveAnimNode.s_pool.alloc();
        liveAnimNode.reset();
        liveAnimNode.m_sourceNode = sourceNode;
        liveAnimNode.m_animLayer = animLayer;
        liveAnimNode.m_layerIdx = layerIdx;
        return liveAnimNode;
    }
    
    private void reset() {
        this.m_sourceNode = null;
        this.m_animLayer = null;
        this.m_active = false;
        this.m_wasActive = false;
        this.m_TransitioningOut = false;
        this.m_Weight = 0.0f;
        this.m_RawWeight = 0.0f;
        this.m_isNew = true;
        this.m_layerIdx = -1;
        this.m_transitionIn.reset();
        this.m_AnimationTracks.clear();
        this.m_NodeAnimTime = 0.0f;
        this.m_PrevNodeAnimTime = 0.0f;
        this.m_blendingIn = false;
        this.m_blendingOut = false;
        this.m_transitionOut = null;
    }
    
    @Override
    public void onReleased() {
        this.reset();
    }
    
    public String getName() {
        return this.m_sourceNode.m_Name;
    }
    
    public boolean isTransitioningIn() {
        return this.m_transitionIn.m_active && this.m_transitionIn.m_track != null;
    }
    
    public void startTransitionIn(final LiveAnimNode liveAnimNode, final AnimTransition animTransition, final AnimationTrack animationTrack) {
        this.startTransitionIn(liveAnimNode.getSourceNode(), animTransition, animationTrack);
    }
    
    public void startTransitionIn(final AnimNode animNode, final AnimTransition data, final AnimationTrack track) {
        this.m_transitionIn.m_active = (track != null);
        this.m_transitionIn.m_transitionedFrom = animNode.m_Name;
        this.m_transitionIn.m_data = data;
        this.m_transitionIn.m_track = track;
        this.m_transitionIn.m_weight = 0.0f;
        this.m_transitionIn.m_rawWeight = 0.0f;
        this.m_transitionIn.m_blendingIn = true;
        this.m_transitionIn.m_blendingOut = false;
        this.m_transitionIn.m_time = 0.0f;
        if (this.m_transitionIn.m_track != null) {
            this.m_transitionIn.m_track.addListener(this);
        }
        this.setMainTracksPlaying(false);
    }
    
    public void setTransitionOut(final AnimTransition transitionOut) {
        this.m_transitionOut = transitionOut;
    }
    
    public void update(final float n) {
        this.m_isNew = false;
        if (this.m_active != this.m_wasActive) {
            this.m_blendingIn = this.m_active;
            this.m_blendingOut = !this.m_active;
            if (this.m_transitionIn.m_active) {
                this.m_transitionIn.m_blendingIn = this.m_active;
                this.m_transitionIn.m_blendingOut = !this.m_active;
            }
            this.m_wasActive = this.m_active;
        }
        final boolean mainAnimActive = this.isMainAnimActive();
        if (this.isTransitioningIn()) {
            this.updateTransitioningIn(n);
        }
        final boolean mainAnimActive2 = this.isMainAnimActive();
        if (mainAnimActive2) {
            if (this.m_blendingOut && this.m_sourceNode.m_StopAnimOnExit) {
                this.setMainTracksPlaying(false);
            }
            else {
                this.setMainTracksPlaying(true);
            }
        }
        else {
            this.setMainTracksPlaying(false);
        }
        if (!mainAnimActive2) {
            return;
        }
        if (!mainAnimActive && this.isLooped()) {
            PZArrayUtil.forEach(this.m_AnimationTracks, (Consumer<? super AnimationTrack>)Lambda.consumer(this.getMainInitialRewindTime(), AnimationTrack::scaledRewind));
        }
        if (this.m_blendingIn) {
            this.updateBlendingIn(n);
        }
        else if (this.m_blendingOut) {
            this.updateBlendingOut(n);
        }
        this.m_PrevNodeAnimTime = this.m_NodeAnimTime;
        this.m_NodeAnimTime += n;
        if (!this.m_transitionIn.m_active && this.m_transitionIn.m_track != null && this.m_transitionIn.m_track.BlendDelta <= 0.0f) {
            this.m_animLayer.getAnimationTrack().removeTrack(this.m_transitionIn.m_track);
            this.m_transitionIn.reset();
        }
    }
    
    private void updateTransitioningIn(final float n) {
        final float speedDelta = this.m_transitionIn.m_track.SpeedDelta;
        final float duration = this.m_transitionIn.m_track.getDuration();
        this.m_transitionIn.m_time = this.m_transitionIn.m_track.getCurrentTimeValue();
        if (this.m_transitionIn.m_time >= duration) {
            this.m_transitionIn.m_active = false;
            this.m_transitionIn.m_weight = 0.0f;
            return;
        }
        if (!this.m_transitionIn.m_blendingOut && !AnimCondition.pass(this.m_animLayer.getVariableSource(), this.m_transitionIn.m_data.m_Conditions)) {
            this.m_transitionIn.m_blendingIn = false;
            this.m_transitionIn.m_blendingOut = true;
        }
        if (this.m_transitionIn.m_time >= duration - this.getTransitionInBlendOutTime() * speedDelta) {
            this.m_transitionIn.m_blendingIn = false;
            this.m_transitionIn.m_blendingOut = true;
        }
        if (this.m_transitionIn.m_blendingIn) {
            final float n2 = this.getTransitionInBlendInTime() * speedDelta;
            final float incrementBlendTime = this.incrementBlendTime(this.m_transitionIn.m_rawWeight, n2, n * speedDelta);
            final float clamp = PZMath.clamp(incrementBlendTime / n2, 0.0f, 1.0f);
            this.m_transitionIn.m_rawWeight = clamp;
            this.m_transitionIn.m_weight = PZMath.lerpFunc_EaseOutInQuad(clamp);
            this.m_transitionIn.m_blendingIn = (incrementBlendTime < n2);
            this.m_transitionIn.m_active = (incrementBlendTime < duration);
        }
        if (this.m_transitionIn.m_blendingOut) {
            final float n3 = this.getTransitionInBlendOutTime() * speedDelta;
            final float incrementBlendTime2 = this.incrementBlendTime(1.0f - this.m_transitionIn.m_rawWeight, n3, n * speedDelta);
            final float clamp2 = PZMath.clamp(1.0f - incrementBlendTime2 / n3, 0.0f, 1.0f);
            this.m_transitionIn.m_rawWeight = clamp2;
            this.m_transitionIn.m_weight = PZMath.lerpFunc_EaseOutInQuad(clamp2);
            this.m_transitionIn.m_blendingOut = (incrementBlendTime2 < n3);
            this.m_transitionIn.m_active = this.m_transitionIn.m_blendingOut;
        }
    }
    
    public void addMainTrack(final AnimationTrack animationTrack) {
        if (!this.isLooped() && !this.m_sourceNode.m_StopAnimOnExit && this.m_sourceNode.m_EarlyTransitionOut) {
            final float blendOutTime = this.getBlendOutTime();
            if (blendOutTime > 0.0f && Float.isFinite(blendOutTime)) {
                animationTrack.earlyBlendOutTime = blendOutTime;
                animationTrack.triggerOnNonLoopedAnimFadeOutEvent = true;
            }
        }
        this.m_AnimationTracks.add(animationTrack);
    }
    
    private void setMainTracksPlaying(final boolean b) {
        Lambda.forEachFrom(PZArrayUtil::forEach, this.m_AnimationTracks, Boolean.valueOf(b), (animationTrack, b2) -> animationTrack.IsPlaying = b2);
    }
    
    private void updateBlendingIn(final float n) {
        final float blendInTime = this.getBlendInTime();
        if (blendInTime <= 0.0f) {
            this.m_Weight = 1.0f;
            this.m_RawWeight = 1.0f;
            this.m_blendingIn = false;
            return;
        }
        final float incrementBlendTime = this.incrementBlendTime(this.m_RawWeight, blendInTime, n);
        final float clamp = PZMath.clamp(incrementBlendTime / blendInTime, 0.0f, 1.0f);
        this.m_RawWeight = clamp;
        this.m_Weight = PZMath.lerpFunc_EaseOutInQuad(clamp);
        this.m_blendingIn = (incrementBlendTime < blendInTime);
    }
    
    private void updateBlendingOut(final float n) {
        final float blendOutTime = this.getBlendOutTime();
        if (blendOutTime <= 0.0f) {
            this.m_Weight = 0.0f;
            this.m_RawWeight = 0.0f;
            this.m_blendingOut = false;
            return;
        }
        final float incrementBlendTime = this.incrementBlendTime(1.0f - this.m_RawWeight, blendOutTime, n);
        final float clamp = PZMath.clamp(1.0f - incrementBlendTime / blendOutTime, 0.0f, 1.0f);
        this.m_RawWeight = clamp;
        this.m_Weight = PZMath.lerpFunc_EaseOutInQuad(clamp);
        this.m_blendingOut = (incrementBlendTime < blendOutTime);
    }
    
    private float incrementBlendTime(final float n, final float n2, final float n3) {
        return n * n2 + n3;
    }
    
    public float getTransitionInBlendInTime() {
        if (this.m_transitionIn.m_data != null && this.m_transitionIn.m_data.m_blendInTime != Float.POSITIVE_INFINITY) {
            return this.m_transitionIn.m_data.m_blendInTime;
        }
        return 0.0f;
    }
    
    public float getMainInitialRewindTime() {
        float n = 0.0f;
        if (this.m_sourceNode.m_randomAdvanceFraction > 0.0f) {
            n = Rand.Next(0.0f, this.m_sourceNode.m_randomAdvanceFraction) * this.getMaxDuration();
        }
        if (this.m_transitionIn.m_data == null) {
            return 0.0f - n;
        }
        final float transitionInBlendOutTime = this.getTransitionInBlendOutTime();
        final float syncAdjustTime = this.m_transitionIn.m_data.m_SyncAdjustTime;
        if (this.m_transitionIn.m_track != null) {
            return transitionInBlendOutTime - syncAdjustTime;
        }
        return transitionInBlendOutTime - syncAdjustTime - n;
    }
    
    private float getMaxDuration() {
        float max = 0.0f;
        for (int i = 0; i < this.m_AnimationTracks.size(); ++i) {
            max = PZMath.max(this.m_AnimationTracks.get(i).getDuration(), max);
        }
        return max;
    }
    
    public float getTransitionInBlendOutTime() {
        return this.getBlendInTime();
    }
    
    public float getBlendInTime() {
        if (this.m_transitionIn.m_data == null) {
            return this.m_sourceNode.m_BlendTime;
        }
        if (this.m_transitionIn.m_track != null && this.m_transitionIn.m_data.m_blendOutTime != Float.POSITIVE_INFINITY) {
            return this.m_transitionIn.m_data.m_blendOutTime;
        }
        if (this.m_transitionIn.m_track == null) {
            if (this.m_transitionIn.m_data.m_blendInTime != Float.POSITIVE_INFINITY) {
                return this.m_transitionIn.m_data.m_blendInTime;
            }
            if (this.m_transitionIn.m_data.m_blendOutTime != Float.POSITIVE_INFINITY) {
                return this.m_transitionIn.m_data.m_blendOutTime;
            }
        }
        return this.m_sourceNode.m_BlendTime;
    }
    
    public float getBlendOutTime() {
        if (this.m_transitionOut == null) {
            return this.m_sourceNode.getBlendOutTime();
        }
        if (!StringUtils.isNullOrWhitespace(this.m_transitionOut.m_AnimName) && this.m_transitionOut.m_blendInTime != Float.POSITIVE_INFINITY) {
            return this.m_transitionOut.m_blendInTime;
        }
        if (StringUtils.isNullOrWhitespace(this.m_transitionOut.m_AnimName)) {
            if (this.m_transitionOut.m_blendOutTime != Float.POSITIVE_INFINITY) {
                return this.m_transitionOut.m_blendOutTime;
            }
            if (this.m_transitionOut.m_blendInTime != Float.POSITIVE_INFINITY) {
                return this.m_transitionOut.m_blendInTime;
            }
        }
        return this.m_sourceNode.getBlendOutTime();
    }
    
    @Override
    public void onAnimStarted(final AnimationTrack animationTrack) {
        this.invokeAnimStartTimeEvent();
    }
    
    @Override
    public void onLoopedAnim(final AnimationTrack animationTrack) {
        if (this.m_TransitioningOut) {
            return;
        }
        this.invokeAnimEndTimeEvent();
    }
    
    @Override
    public void onNonLoopedAnimFadeOut(final AnimationTrack animationTrack) {
        if (!DebugOptions.instance.Animation.AllowEarlyTransitionOut.getValue()) {
            return;
        }
        this.invokeAnimEndTimeEvent();
        this.m_TransitioningOut = true;
    }
    
    @Override
    public void onNonLoopedAnimFinished(final AnimationTrack animationTrack) {
        if (this.m_TransitioningOut) {
            return;
        }
        this.invokeAnimEndTimeEvent();
    }
    
    @Override
    public void onTrackDestroyed(final AnimationTrack animationTrack) {
        this.m_AnimationTracks.remove(animationTrack);
        if (this.m_transitionIn.m_track == animationTrack) {
            this.m_transitionIn.m_track = null;
            this.m_transitionIn.m_active = false;
            this.m_transitionIn.m_weight = 0.0f;
            this.setMainTracksPlaying(true);
        }
    }
    
    private void invokeAnimStartTimeEvent() {
        this.invokeAnimTimeEvent(AnimEvent.AnimEventTime.Start);
    }
    
    private void invokeAnimEndTimeEvent() {
        this.invokeAnimTimeEvent(AnimEvent.AnimEventTime.End);
    }
    
    private void invokeAnimTimeEvent(final AnimEvent.AnimEventTime animEventTime) {
        final List<AnimEvent> events = this.getSourceNode().m_Events;
        for (int i = 0; i < events.size(); ++i) {
            final AnimEvent animEvent = events.get(i);
            if (animEvent.m_Time == animEventTime) {
                this.m_animLayer.invokeAnimEvent(animEvent);
            }
        }
    }
    
    public AnimNode getSourceNode() {
        return this.m_sourceNode;
    }
    
    public boolean isIdleAnimActive() {
        return this.m_active && this.m_sourceNode.isIdleAnim();
    }
    
    public boolean isActive() {
        return this.m_active;
    }
    
    public void setActive(final boolean active) {
        this.m_active = active;
    }
    
    public boolean isLooped() {
        return this.m_sourceNode.m_Looped;
    }
    
    public float getWeight() {
        return this.m_Weight;
    }
    
    public float getTransitionInWeight() {
        return this.m_transitionIn.m_weight;
    }
    
    public boolean wasActivated() {
        return this.m_active != this.m_wasActive && this.m_active;
    }
    
    public boolean wasDeactivated() {
        return this.m_active != this.m_wasActive && this.m_wasActive;
    }
    
    public boolean isNew() {
        return this.m_isNew;
    }
    
    public int getPlayingTrackCount() {
        int n = 0;
        if (this.isMainAnimActive()) {
            n += this.m_AnimationTracks.size();
        }
        if (this.isTransitioningIn()) {
            ++n;
        }
        return n;
    }
    
    public boolean isMainAnimActive() {
        return !this.isTransitioningIn() || this.m_transitionIn.m_blendingOut;
    }
    
    public AnimationTrack getPlayingTrackAt(final int n) {
        final int playingTrackCount = this.getPlayingTrackCount();
        if (n < 0 || n >= playingTrackCount) {
            throw new IndexOutOfBoundsException(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.getPlayingTrackCount()));
        }
        if (this.isTransitioningIn() && n == playingTrackCount - 1) {
            return this.m_transitionIn.m_track;
        }
        return this.m_AnimationTracks.get(n);
    }
    
    public String getTransitionFrom() {
        return this.m_transitionIn.m_transitionedFrom;
    }
    
    public void setTransitionInBlendDelta(final float blendDelta) {
        if (this.m_transitionIn.m_track != null) {
            this.m_transitionIn.m_track.BlendDelta = blendDelta;
        }
    }
    
    public AnimationTrack getTransitionInTrack() {
        return this.m_transitionIn.m_track;
    }
    
    public int getTransitionLayerIdx() {
        return (this.m_transitionIn.m_track != null) ? this.m_transitionIn.m_track.getLayerIdx() : -1;
    }
    
    public int getLayerIdx() {
        return this.m_layerIdx;
    }
    
    public int getPriority() {
        return this.m_sourceNode.getPriority();
    }
    
    public String getDeferredBoneName() {
        return this.m_sourceNode.getDeferredBoneName();
    }
    
    public BoneAxis getDeferredBoneAxis() {
        return this.m_sourceNode.getDeferredBoneAxis();
    }
    
    public List<AnimBoneWeight> getSubStateBoneWeights() {
        return this.m_sourceNode.m_SubStateBoneWeights;
    }
    
    public AnimTransition findTransitionTo(final IAnimationVariableSource animationVariableSource, final String s) {
        return this.m_sourceNode.findTransitionTo(animationVariableSource, s);
    }
    
    public float getSpeedScale(final IAnimationVariableSource animationVariableSource) {
        return this.m_sourceNode.getSpeedScale(animationVariableSource);
    }
    
    static {
        s_pool = new Pool<LiveAnimNode>(LiveAnimNode::new);
    }
    
    private static class TransitionIn
    {
        private float m_time;
        private String m_transitionedFrom;
        private boolean m_active;
        private AnimationTrack m_track;
        private AnimTransition m_data;
        private float m_weight;
        private float m_rawWeight;
        private boolean m_blendingIn;
        private boolean m_blendingOut;
        
        private void reset() {
            this.m_time = 0.0f;
            this.m_transitionedFrom = null;
            this.m_active = false;
            this.m_track = null;
            this.m_data = null;
            this.m_weight = 0.0f;
            this.m_rawWeight = 0.0f;
            this.m_blendingIn = false;
            this.m_blendingOut = false;
        }
    }
}

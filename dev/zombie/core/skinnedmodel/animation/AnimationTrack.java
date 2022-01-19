// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.animation;

import zombie.core.profiling.PerformanceProfileProbe;
import zombie.core.PerformanceSettings;
import zombie.util.lambda.Stacks;
import java.util.function.Supplier;
import zombie.debug.DebugOptions;
import java.util.Objects;
import zombie.util.StringUtils;
import zombie.core.skinnedmodel.model.SkinningData;
import zombie.core.skinnedmodel.advancedanimation.PooledAnimBoneWeightArray;
import java.util.List;
import zombie.util.Lambda;
import zombie.util.lambda.Consumers;
import java.util.Collection;
import java.util.function.Consumer;
import zombie.debug.DebugLog;
import zombie.core.skinnedmodel.HelperFunctions;
import zombie.iso.Vector2;
import org.lwjgl.util.vector.ReadableVector3f;
import zombie.core.math.PZMath;
import org.lwjgl.util.vector.ReadableVector4f;
import zombie.network.ServerGUI;
import zombie.network.GameServer;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import zombie.util.list.PZArrayUtil;
import zombie.util.Pool;
import zombie.core.skinnedmodel.model.SkinningBone;
import java.util.ArrayList;
import zombie.util.PooledFloatArrayObject;
import zombie.core.skinnedmodel.advancedanimation.AnimBoneWeight;
import zombie.util.PooledArrayObject;
import zombie.util.PooledObject;

public final class AnimationTrack extends PooledObject
{
    public boolean IsPlaying;
    protected AnimationClip CurrentClip;
    public int priority;
    private float currentTimeValue;
    private float previousTimeValue;
    public boolean SyncTrackingEnabled;
    public boolean reverse;
    private boolean bLooping;
    private final KeyframeSpan[] m_pose;
    private final KeyframeSpan m_deferredPoseSpan;
    public float SpeedDelta;
    public float BlendDelta;
    public float blendFieldWeight;
    public String name;
    public float earlyBlendOutTime;
    public boolean triggerOnNonLoopedAnimFadeOutEvent;
    private int m_layerIdx;
    private PooledArrayObject<AnimBoneWeight> m_boneWeightBindings;
    private PooledFloatArrayObject m_boneWeights;
    private final ArrayList<IAnimListener> listeners;
    private final ArrayList<IAnimListener> listenersInvoking;
    private SkinningBone m_deferredBone;
    private BoneAxis m_deferredBoneAxis;
    private boolean m_useDeferredRotation;
    private final DeferredMotionData m_deferredMotion;
    private static final Pool<AnimationTrack> s_pool;
    
    public static AnimationTrack alloc() {
        return AnimationTrack.s_pool.alloc();
    }
    
    protected AnimationTrack() {
        this.m_pose = new KeyframeSpan[60];
        this.m_deferredPoseSpan = new KeyframeSpan();
        this.listeners = new ArrayList<IAnimListener>();
        this.listenersInvoking = new ArrayList<IAnimListener>();
        this.m_deferredMotion = new DeferredMotionData();
        PZArrayUtil.arrayPopulate(this.m_pose, KeyframeSpan::new);
        this.resetInternal();
    }
    
    private AnimationTrack resetInternal() {
        this.IsPlaying = false;
        this.CurrentClip = null;
        this.priority = 0;
        this.currentTimeValue = 0.0f;
        this.previousTimeValue = 0.0f;
        this.SyncTrackingEnabled = true;
        this.reverse = false;
        this.bLooping = false;
        PZArrayUtil.forEach(this.m_pose, KeyframeSpan::clear);
        this.m_deferredPoseSpan.clear();
        this.SpeedDelta = 1.0f;
        this.BlendDelta = 0.0f;
        this.blendFieldWeight = 0.0f;
        this.name = "!Empty!";
        this.earlyBlendOutTime = 0.0f;
        this.triggerOnNonLoopedAnimFadeOutEvent = false;
        this.m_layerIdx = -1;
        Pool.tryRelease(this.m_boneWeightBindings);
        this.m_boneWeightBindings = null;
        Pool.tryRelease(this.m_boneWeights);
        this.m_boneWeights = null;
        this.listeners.clear();
        this.listenersInvoking.clear();
        this.m_deferredBone = null;
        this.m_deferredBoneAxis = BoneAxis.Y;
        this.m_useDeferredRotation = false;
        this.m_deferredMotion.reset();
        return this;
    }
    
    public void get(final int n, final Vector3f vector3f, final Quaternion quaternion, final Vector3f vector3f2) {
        this.m_pose[n].lerp(this.getCurrentTime(), vector3f, quaternion, vector3f2);
    }
    
    private Keyframe getDeferredMovementFrameAt(final int n, final float n2, final Keyframe keyframe) {
        return this.getKeyframeSpan(n, n2, this.m_deferredPoseSpan).lerp(n2, keyframe);
    }
    
    private KeyframeSpan getKeyframeSpan(final int n, final float n2, final KeyframeSpan keyframeSpan) {
        if (!keyframeSpan.isBone(n)) {
            keyframeSpan.clear();
        }
        final Keyframe[] boneFrames = this.CurrentClip.getBoneFramesAt(n);
        if (boneFrames.length == 0) {
            keyframeSpan.clear();
            return keyframeSpan;
        }
        if (keyframeSpan.containsTime(n2)) {
            return keyframeSpan;
        }
        if (n2 >= boneFrames[boneFrames.length - 1].Time) {
            keyframeSpan.fromIdx = boneFrames.length - 2;
            keyframeSpan.toIdx = boneFrames.length - 1;
            keyframeSpan.from = boneFrames[keyframeSpan.fromIdx];
            keyframeSpan.to = boneFrames[keyframeSpan.toIdx];
            return keyframeSpan;
        }
        final Keyframe to = boneFrames[0];
        if (n2 <= to.Time) {
            keyframeSpan.clear();
            keyframeSpan.toIdx = 0;
            keyframeSpan.to = to;
            return keyframeSpan;
        }
        int toIdx = 0;
        if (keyframeSpan.isSpan() && keyframeSpan.to.Time <= n2) {
            toIdx = keyframeSpan.toIdx;
        }
        keyframeSpan.clear();
        for (int i = toIdx; i < boneFrames.length - 1; ++i) {
            final Keyframe from = boneFrames[i];
            final Keyframe to2 = boneFrames[i + 1];
            if (from.Time <= n2 && n2 <= to2.Time) {
                keyframeSpan.fromIdx = i;
                keyframeSpan.toIdx = i + 1;
                keyframeSpan.from = from;
                keyframeSpan.to = to2;
                break;
            }
        }
        return keyframeSpan;
    }
    
    public void removeListener(final IAnimListener o) {
        this.listeners.remove(o);
    }
    
    public void Update(final float n) {
        try {
            this.UpdateKeyframes(n);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void UpdateKeyframes(final float f) {
        s_performance.updateKeyframes.invokeAndMeasure(this, f, AnimationTrack::updateKeyframesInternal);
    }
    
    private void updateKeyframesInternal(final float n) {
        if (this.CurrentClip == null) {
            throw new RuntimeException("AnimationPlayer.Update was called before startClip");
        }
        if (n > 0.0f) {
            this.TickCurrentTime(n);
        }
        if (!GameServer.bServer || ServerGUI.isCreated()) {
            this.updatePose();
        }
        this.updateDeferredValues();
    }
    
    private void updatePose() {
        s_performance.updatePose.invokeAndMeasure(this, AnimationTrack::updatePoseInternal);
    }
    
    private void updatePoseInternal() {
        final float currentTime = this.getCurrentTime();
        for (int i = 0; i < 60; ++i) {
            this.getKeyframeSpan(i, currentTime, this.m_pose[i]);
        }
    }
    
    private void updateDeferredValues() {
        s_performance.updateDeferredValues.invokeAndMeasure(this, AnimationTrack::updateDeferredValuesInternal);
    }
    
    private void updateDeferredValuesInternal() {
        if (this.m_deferredBone == null) {
            return;
        }
        final DeferredMotionData deferredMotion = this.m_deferredMotion;
        deferredMotion.m_deferredRotationDiff = 0.0f;
        deferredMotion.m_deferredMovementDiff.set(0.0f, 0.0f);
        deferredMotion.m_counterRotatedMovementDiff.set(0.0f, 0.0f);
        float reversibleTimeValue = this.getReversibleTimeValue(this.previousTimeValue);
        final float reversibleTimeValue2 = this.getReversibleTimeValue(this.currentTimeValue);
        if (this.isLooping() && reversibleTimeValue > reversibleTimeValue2) {
            this.appendDeferredValues(deferredMotion, reversibleTimeValue, this.getDuration());
            reversibleTimeValue = 0.0f;
        }
        this.appendDeferredValues(deferredMotion, reversibleTimeValue, reversibleTimeValue2);
    }
    
    private void appendDeferredValues(final DeferredMotionData deferredMotionData, final float n, final float n2) {
        final Keyframe deferredMovementFrame = this.getDeferredMovementFrameAt(this.m_deferredBone.Index, n, L_updateDeferredValues.prevKeyFrame);
        final Keyframe deferredMovementFrame2 = this.getDeferredMovementFrameAt(this.m_deferredBone.Index, n2, L_updateDeferredValues.keyFrame);
        if (!GameServer.bServer) {
            deferredMotionData.m_prevDeferredRotation = this.getDeferredTwistRotation(deferredMovementFrame.Rotation);
            deferredMotionData.m_targetDeferredRotationQ.set((ReadableVector4f)deferredMovementFrame2.Rotation);
            deferredMotionData.m_targetDeferredRotation = this.getDeferredTwistRotation(deferredMovementFrame2.Rotation);
            deferredMotionData.m_deferredRotationDiff += PZMath.getClosestAngle(deferredMotionData.m_prevDeferredRotation, deferredMotionData.m_targetDeferredRotation);
        }
        this.getDeferredMovement(deferredMovementFrame.Position, deferredMotionData.m_prevDeferredMovement);
        deferredMotionData.m_targetDeferredPosition.set((ReadableVector3f)deferredMovementFrame2.Position);
        this.getDeferredMovement(deferredMovementFrame2.Position, deferredMotionData.m_targetDeferredMovement);
        final Vector2 set = L_updateDeferredValues.diff.set(deferredMotionData.m_targetDeferredMovement.x - deferredMotionData.m_prevDeferredMovement.x, deferredMotionData.m_targetDeferredMovement.y - deferredMotionData.m_prevDeferredMovement.y);
        final Vector2 set2 = L_updateDeferredValues.crDiff.set(set);
        if (this.getUseDeferredRotation()) {
            final float normalize = set2.normalize();
            set2.rotate(-(deferredMotionData.m_targetDeferredRotation + 1.5707964f));
            set2.scale(-normalize);
        }
        final Vector2 deferredMovementDiff = deferredMotionData.m_deferredMovementDiff;
        deferredMovementDiff.x += set.x;
        final Vector2 deferredMovementDiff2 = deferredMotionData.m_deferredMovementDiff;
        deferredMovementDiff2.y += set.y;
        final Vector2 counterRotatedMovementDiff = deferredMotionData.m_counterRotatedMovementDiff;
        counterRotatedMovementDiff.x += set2.x;
        final Vector2 counterRotatedMovementDiff2 = deferredMotionData.m_counterRotatedMovementDiff;
        counterRotatedMovementDiff2.y += set2.y;
    }
    
    public float getDeferredTwistRotation(final Quaternion quaternion) {
        if (this.m_deferredBoneAxis == BoneAxis.Z) {
            return HelperFunctions.getRotationZ(quaternion);
        }
        if (this.m_deferredBoneAxis == BoneAxis.Y) {
            return HelperFunctions.getRotationY(quaternion);
        }
        DebugLog.Animation.error("BoneAxis unhandled: %s", String.valueOf(this.m_deferredBoneAxis));
        return 0.0f;
    }
    
    public Vector2 getDeferredMovement(final Vector3f vector3f, final Vector2 vector2) {
        if (this.m_deferredBoneAxis == BoneAxis.Y) {
            vector2.set(vector3f.x, -vector3f.z);
        }
        else {
            vector2.set(vector3f.x, vector3f.y);
        }
        return vector2;
    }
    
    public Vector3f getCurrentDeferredCounterPosition(final Vector3f vector3f) {
        this.getCurrentDeferredPosition(vector3f);
        if (this.m_deferredBoneAxis == BoneAxis.Y) {
            vector3f.set(-vector3f.x, 0.0f, vector3f.z);
        }
        else {
            vector3f.set(-vector3f.x, -vector3f.y, 0.0f);
        }
        return vector3f;
    }
    
    public float getCurrentDeferredRotation() {
        return this.m_deferredMotion.m_targetDeferredRotation;
    }
    
    public Vector3f getCurrentDeferredPosition(final Vector3f vector3f) {
        vector3f.set((ReadableVector3f)this.m_deferredMotion.m_targetDeferredPosition);
        return vector3f;
    }
    
    public int getDeferredMovementBoneIdx() {
        if (this.m_deferredBone != null) {
            return this.m_deferredBone.Index;
        }
        return -1;
    }
    
    public float getCurrentTime() {
        return this.getReversibleTimeValue(this.currentTimeValue);
    }
    
    public float getPreviousTime() {
        return this.getReversibleTimeValue(this.previousTimeValue);
    }
    
    private float getReversibleTimeValue(final float n) {
        if (this.reverse) {
            return this.getDuration() - n;
        }
        return n;
    }
    
    protected void TickCurrentTime(final float f) {
        s_performance.tickCurrentTime.invokeAndMeasure(this, f, AnimationTrack::tickCurrentTimeInternal);
    }
    
    private void tickCurrentTimeInternal(float n) {
        n *= this.SpeedDelta;
        if (!this.IsPlaying) {
            n = 0.0f;
        }
        final float duration = this.getDuration();
        this.previousTimeValue = this.currentTimeValue;
        this.currentTimeValue += n;
        if (this.bLooping) {
            if (this.previousTimeValue == 0.0f && this.currentTimeValue > 0.0f) {
                this.invokeOnAnimStartedEvent();
            }
            if (this.currentTimeValue >= duration) {
                this.invokeOnLoopedAnimEvent();
                this.currentTimeValue %= duration;
                this.invokeOnAnimStartedEvent();
            }
            return;
        }
        if (this.currentTimeValue < 0.0f) {
            this.currentTimeValue = 0.0f;
        }
        if (this.previousTimeValue == 0.0f && this.currentTimeValue > 0.0f) {
            this.invokeOnAnimStartedEvent();
        }
        if (this.triggerOnNonLoopedAnimFadeOutEvent) {
            final float n2 = duration - this.earlyBlendOutTime;
            if (this.previousTimeValue < n2 && n2 <= this.currentTimeValue) {
                this.invokeOnNonLoopedAnimFadeOutEvent();
            }
        }
        if (this.currentTimeValue > duration) {
            this.currentTimeValue = duration;
        }
        if (this.previousTimeValue < duration && this.currentTimeValue >= duration) {
            this.invokeOnLoopedAnimEvent();
            this.invokeOnNonLoopedAnimFinishedEvent();
        }
    }
    
    public float getDuration() {
        if (this.hasClip()) {
            return this.CurrentClip.Duration;
        }
        return 0.0f;
    }
    
    private void invokeListeners(final Consumer<IAnimListener> consumer) {
        if (this.listeners.isEmpty()) {
            return;
        }
        this.listenersInvoking.clear();
        this.listenersInvoking.addAll(this.listeners);
        for (int i = 0; i < this.listenersInvoking.size(); ++i) {
            consumer.accept(this.listenersInvoking.get(i));
        }
    }
    
    private <T1> void invokeListeners(final T1 t1, final Consumers.Params1.ICallback<IAnimListener, T1> callback) {
        Lambda.capture(this, t1, callback, (genericStack, animationTrack, o, callback2) -> animationTrack.invokeListeners(genericStack.consumer(o, callback2)));
    }
    
    protected void invokeOnAnimStartedEvent() {
        this.invokeListeners(this, IAnimListener::onAnimStarted);
    }
    
    protected void invokeOnLoopedAnimEvent() {
        this.invokeListeners(this, IAnimListener::onLoopedAnim);
    }
    
    protected void invokeOnNonLoopedAnimFadeOutEvent() {
        this.invokeListeners(this, IAnimListener::onNonLoopedAnimFadeOut);
    }
    
    protected void invokeOnNonLoopedAnimFinishedEvent() {
        this.invokeListeners(this, IAnimListener::onNonLoopedAnimFinished);
    }
    
    @Override
    public void onReleased() {
        if (!this.listeners.isEmpty()) {
            this.listenersInvoking.clear();
            this.listenersInvoking.addAll(this.listeners);
            for (int i = 0; i < this.listenersInvoking.size(); ++i) {
                this.listenersInvoking.get(i).onTrackDestroyed(this);
            }
            this.listeners.clear();
            this.listenersInvoking.clear();
        }
        this.reset();
    }
    
    public Vector2 getDeferredMovementDiff(final Vector2 vector2) {
        vector2.set(this.m_deferredMotion.m_counterRotatedMovementDiff);
        return vector2;
    }
    
    public float getDeferredRotationDiff() {
        return this.m_deferredMotion.m_deferredRotationDiff;
    }
    
    public float getClampedBlendDelta() {
        return PZMath.clamp(this.BlendDelta, 0.0f, 1.0f);
    }
    
    public void addListener(final IAnimListener e) {
        this.listeners.add(e);
    }
    
    public void startClip(final AnimationClip currentClip, final boolean bLooping) {
        if (currentClip == null) {
            throw new NullPointerException("Supplied clip is null.");
        }
        this.reset();
        this.IsPlaying = true;
        this.bLooping = bLooping;
        this.CurrentClip = currentClip;
    }
    
    public AnimationTrack reset() {
        return this.resetInternal();
    }
    
    public void setBoneWeights(final List<AnimBoneWeight> list) {
        this.m_boneWeightBindings = PooledAnimBoneWeightArray.toArray(list);
        this.m_boneWeights = null;
    }
    
    public void initBoneWeights(final SkinningData skinningData) {
        if (this.hasBoneMask()) {
            return;
        }
        if (this.m_boneWeightBindings == null) {
            return;
        }
        if (this.m_boneWeightBindings.isEmpty()) {
            this.m_boneWeights = PooledFloatArrayObject.alloc(0);
            return;
        }
        this.m_boneWeights = PooledFloatArrayObject.alloc(skinningData.numBones());
        PZArrayUtil.arraySet(this.m_boneWeights.array(), 0.0f);
        for (int i = 0; i < this.m_boneWeightBindings.length(); ++i) {
            this.initWeightBinding(skinningData, this.m_boneWeightBindings.get(i));
        }
    }
    
    protected void initWeightBinding(final SkinningData skinningData, final AnimBoneWeight animBoneWeight) {
        if (animBoneWeight == null || StringUtils.isNullOrEmpty(animBoneWeight.boneName)) {
            return;
        }
        final String boneName = animBoneWeight.boneName;
        final SkinningBone bone = skinningData.getBone(boneName);
        if (bone == null) {
            DebugLog.Animation.error("Bone not found: %s", boneName);
            return;
        }
        final float weight = animBoneWeight.weight;
        this.assignBoneWeight(weight, bone.Index);
        if (animBoneWeight.includeDescendants) {
            final SkinningBone obj = bone;
            Objects.requireNonNull(obj);
            Lambda.forEach((Consumer<Consumer<SkinningBone>>)obj::forEachDescendant, this, weight, (skinningBone, animationTrack, n) -> animationTrack.assignBoneWeight(n, skinningBone.Index));
        }
    }
    
    private void assignBoneWeight(final float a, final int n) {
        if (!this.hasBoneMask()) {
            throw new NullPointerException("Bone weights array not initialized.");
        }
        this.m_boneWeights.set(n, Math.max(a, this.m_boneWeights.get(n)));
    }
    
    public float getBoneWeight(final int n) {
        if (!this.hasBoneMask()) {
            return 1.0f;
        }
        if (DebugOptions.instance.Character.Debug.Animate.NoBoneMasks.getValue()) {
            return 1.0f;
        }
        return PZArrayUtil.getOrDefault(this.m_boneWeights.array(), n, 0.0f);
    }
    
    public float getDeferredBoneWeight() {
        if (this.m_deferredBone == null) {
            return 0.0f;
        }
        return this.getBoneWeight(this.m_deferredBone.Index);
    }
    
    public void setLayerIdx(final int layerIdx) {
        this.m_layerIdx = layerIdx;
    }
    
    public int getLayerIdx() {
        return this.m_layerIdx;
    }
    
    public boolean hasBoneMask() {
        return this.m_boneWeights != null;
    }
    
    public boolean isLooping() {
        return this.bLooping;
    }
    
    public void setDeferredBone(final SkinningBone deferredBone, final BoneAxis deferredBoneAxis) {
        this.m_deferredBone = deferredBone;
        this.m_deferredBoneAxis = deferredBoneAxis;
    }
    
    public void setUseDeferredRotation(final boolean useDeferredRotation) {
        this.m_useDeferredRotation = useDeferredRotation;
    }
    
    public boolean getUseDeferredRotation() {
        return this.m_useDeferredRotation;
    }
    
    public boolean isFinished() {
        return !this.bLooping && this.getDuration() > 0.0f && this.currentTimeValue >= this.getDuration();
    }
    
    public float getCurrentTimeValue() {
        return this.currentTimeValue;
    }
    
    public void setCurrentTimeValue(final float currentTimeValue) {
        this.currentTimeValue = currentTimeValue;
    }
    
    public float getPreviousTimeValue() {
        return this.previousTimeValue;
    }
    
    public void setPreviousTimeValue(final float previousTimeValue) {
        this.previousTimeValue = previousTimeValue;
    }
    
    public void rewind(final float n) {
        this.advance(-n);
    }
    
    public void scaledRewind(final float n) {
        this.scaledAdvance(-n);
    }
    
    public void scaledAdvance(final float n) {
        this.advance(n * this.SpeedDelta);
    }
    
    public void advance(final float n) {
        this.currentTimeValue = PZMath.wrap(this.currentTimeValue + n, 0.0f, this.getDuration());
        this.previousTimeValue = PZMath.wrap(this.previousTimeValue + n, 0.0f, this.getDuration());
    }
    
    public void advanceFraction(final float n) {
        this.advance(this.getDuration() * n);
    }
    
    public void moveCurrentTimeValueTo(final float n) {
        this.advance(n - this.currentTimeValue);
    }
    
    public void moveCurrentTimeValueToFraction(final float n) {
        this.moveCurrentTimeValueTo(this.getDuration() * n);
    }
    
    public float getCurrentTimeFraction() {
        if (this.hasClip()) {
            return this.currentTimeValue / this.getDuration();
        }
        return 0.0f;
    }
    
    public boolean hasClip() {
        return this.CurrentClip != null;
    }
    
    public AnimationClip getClip() {
        return this.CurrentClip;
    }
    
    public int getPriority() {
        return this.priority;
    }
    
    public static AnimationTrack createClone(final AnimationTrack animationTrack, final Supplier<AnimationTrack> supplier) {
        final AnimationTrack animationTrack2 = supplier.get();
        animationTrack2.IsPlaying = animationTrack.IsPlaying;
        animationTrack2.CurrentClip = animationTrack.CurrentClip;
        animationTrack2.priority = animationTrack.priority;
        animationTrack2.currentTimeValue = animationTrack.currentTimeValue;
        animationTrack2.previousTimeValue = animationTrack.previousTimeValue;
        animationTrack2.SyncTrackingEnabled = animationTrack.SyncTrackingEnabled;
        animationTrack2.reverse = animationTrack.reverse;
        animationTrack2.bLooping = animationTrack.bLooping;
        animationTrack2.SpeedDelta = animationTrack.SpeedDelta;
        animationTrack2.BlendDelta = animationTrack.BlendDelta;
        animationTrack2.blendFieldWeight = animationTrack.blendFieldWeight;
        animationTrack2.name = animationTrack.name;
        animationTrack2.earlyBlendOutTime = animationTrack.earlyBlendOutTime;
        animationTrack2.triggerOnNonLoopedAnimFadeOutEvent = animationTrack.triggerOnNonLoopedAnimFadeOutEvent;
        animationTrack2.m_layerIdx = animationTrack.m_layerIdx;
        animationTrack2.m_boneWeightBindings = PooledAnimBoneWeightArray.toArray(animationTrack.m_boneWeightBindings);
        animationTrack2.m_boneWeights = PooledFloatArrayObject.toArray(animationTrack.m_boneWeights);
        animationTrack2.m_deferredBone = animationTrack.m_deferredBone;
        animationTrack2.m_deferredBoneAxis = animationTrack.m_deferredBoneAxis;
        animationTrack2.m_useDeferredRotation = animationTrack.m_useDeferredRotation;
        return animationTrack2;
    }
    
    static {
        s_pool = new Pool<AnimationTrack>(AnimationTrack::new);
    }
    
    private static class L_updateDeferredValues
    {
        static final Keyframe keyFrame;
        static final Keyframe prevKeyFrame;
        static final Vector2 crDiff;
        static final Vector2 diff;
        
        static {
            keyFrame = new Keyframe(new Vector3f(), new Quaternion(), new Vector3f(1.0f, 1.0f, 1.0f));
            prevKeyFrame = new Keyframe(new Vector3f(), new Quaternion(), new Vector3f(1.0f, 1.0f, 1.0f));
            crDiff = new Vector2();
            diff = new Vector2();
        }
    }
    
    private static class l_getDeferredMovementFrameAt
    {
        static final KeyframeSpan span;
        
        static {
            span = new KeyframeSpan();
        }
    }
    
    private static class l_updatePoseInternal
    {
        static final KeyframeSpan span;
        
        static {
            span = new KeyframeSpan();
        }
    }
    
    private static class DeferredMotionData
    {
        float m_targetDeferredRotation;
        float m_prevDeferredRotation;
        final Quaternion m_targetDeferredRotationQ;
        final Vector3f m_targetDeferredPosition;
        final Vector2 m_prevDeferredMovement;
        final Vector2 m_targetDeferredMovement;
        float m_deferredRotationDiff;
        final Vector2 m_deferredMovementDiff;
        final Vector2 m_counterRotatedMovementDiff;
        
        private DeferredMotionData() {
            this.m_targetDeferredRotationQ = new Quaternion();
            this.m_targetDeferredPosition = new Vector3f();
            this.m_prevDeferredMovement = new Vector2();
            this.m_targetDeferredMovement = new Vector2();
            this.m_deferredMovementDiff = new Vector2();
            this.m_counterRotatedMovementDiff = new Vector2();
        }
        
        public void reset() {
            this.m_deferredRotationDiff = 0.0f;
            this.m_targetDeferredRotation = 0.0f;
            this.m_prevDeferredRotation = 0.0f;
            this.m_targetDeferredRotationQ.setIdentity();
            this.m_targetDeferredMovement.set(0.0f, 0.0f);
            this.m_targetDeferredPosition.set(0.0f, 0.0f, 0.0f);
            this.m_prevDeferredMovement.set(0.0f, 0.0f);
            this.m_deferredMovementDiff.set(0.0f, 0.0f);
            this.m_counterRotatedMovementDiff.set(0.0f, 0.0f);
        }
    }
    
    private static class KeyframeSpan
    {
        Keyframe from;
        Keyframe to;
        int fromIdx;
        int toIdx;
        
        private KeyframeSpan() {
            this.fromIdx = -1;
            this.toIdx = -1;
        }
        
        void clear() {
            this.from = null;
            this.to = null;
            this.fromIdx = -1;
            this.toIdx = -1;
        }
        
        Keyframe lerp(final float n, final Keyframe keyframe) {
            keyframe.setIdentity();
            if (this.from == null && this.to == null) {
                return keyframe;
            }
            if (this.to == null) {
                keyframe.set(this.from);
                return keyframe;
            }
            if (this.from == null) {
                keyframe.set(this.to);
                return keyframe;
            }
            return Keyframe.lerp(this.from, this.to, n, keyframe);
        }
        
        void lerp(final float n, final Vector3f vector3f, final Quaternion quaternion, final Vector3f vector3f2) {
            if (this.from == null && this.to == null) {
                Keyframe.setIdentity(vector3f, quaternion, vector3f2);
                return;
            }
            if (this.to == null) {
                this.from.get(vector3f, quaternion, vector3f2);
                return;
            }
            if (this.from == null) {
                this.to.get(vector3f, quaternion, vector3f2);
                return;
            }
            if (!PerformanceSettings.InterpolateAnims) {
                this.to.get(vector3f, quaternion, vector3f2);
                return;
            }
            Keyframe.lerp(this.from, this.to, n, vector3f, quaternion, vector3f2);
        }
        
        boolean isSpan() {
            return this.from != null && this.to != null;
        }
        
        boolean isPost() {
            return (this.from == null || this.to == null) && this.from != this.to;
        }
        
        boolean isEmpty() {
            return this.from == null && this.to == null;
        }
        
        boolean containsTime(final float n) {
            return this.isSpan() && this.from.Time <= n && n <= this.to.Time;
        }
        
        public boolean isBone(final int n) {
            return (this.from != null && this.from.Bone == n) || (this.to != null && this.to.Bone == n);
        }
    }
    
    private static class s_performance
    {
        static final PerformanceProfileProbe tickCurrentTime;
        static final PerformanceProfileProbe updateKeyframes;
        static final PerformanceProfileProbe updateDeferredValues;
        static final PerformanceProfileProbe updatePose;
        
        static {
            tickCurrentTime = new PerformanceProfileProbe("AnimationTrack.tickCurrentTime");
            updateKeyframes = new PerformanceProfileProbe("AnimationTrack.updateKeyframes");
            updateDeferredValues = new PerformanceProfileProbe("AnimationTrack.updateDeferredValues");
            updatePose = new PerformanceProfileProbe("AnimationTrack.updatePose");
        }
    }
}

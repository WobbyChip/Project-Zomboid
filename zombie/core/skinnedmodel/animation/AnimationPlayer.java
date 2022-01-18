// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.animation;

import org.lwjgl.util.vector.Vector4f;
import zombie.core.math.Vector3;
import zombie.core.skinnedmodel.advancedanimation.AdvancedAnimator;
import org.joml.Math;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.ReadableVector4f;
import zombie.core.skinnedmodel.HelperFunctions;
import zombie.core.skinnedmodel.model.SkinningBone;
import zombie.characters.IsoGameCharacter;
import zombie.debug.DebugOptions;
import zombie.network.MPStatistic;
import zombie.debug.DebugLog;
import zombie.GameProfiler;
import java.util.HashMap;
import java.util.Arrays;
import org.lwjgl.util.vector.Matrix;
import zombie.util.IPooledObject;
import zombie.util.StringUtils;
import java.util.function.Predicate;
import zombie.util.Lambda;
import zombie.util.list.PZArrayUtil;
import java.util.Objects;
import zombie.GameTime;
import zombie.core.math.PZMath;
import zombie.util.Pool;
import zombie.core.skinnedmodel.animation.debug.AnimationPlayerRecorder;
import java.util.List;
import java.util.ArrayList;
import zombie.iso.Vector2;
import zombie.core.skinnedmodel.animation.sharedskele.SharedSkeleAnimationTrack;
import zombie.core.skinnedmodel.animation.sharedskele.SharedSkeleAnimationRepository;
import zombie.core.skinnedmodel.model.SkinningData;
import org.lwjgl.util.vector.Matrix4f;
import zombie.core.skinnedmodel.model.Model;
import zombie.util.PooledObject;

public final class AnimationPlayer extends PooledObject
{
    private Model model;
    private final Matrix4f propTransforms;
    private boolean m_boneTransformsNeedFirstFrame;
    private TwistableBoneTransform[] m_boneTransforms;
    public Matrix4f[] modelTransforms;
    private SkinTransformData m_skinTransformData;
    private SkinTransformData m_skinTransformDataPool;
    private SkinningData m_skinningData;
    private SharedSkeleAnimationRepository m_sharedSkeleAnimationRepo;
    private SharedSkeleAnimationTrack m_currentSharedTrack;
    private AnimationClip m_currentSharedTrackClip;
    private float m_angle;
    private float m_targetAngle;
    private float m_twistAngle;
    private float m_shoulderTwistAngle;
    private float m_targetTwistAngle;
    private float m_maxTwistAngle;
    private float m_excessTwist;
    private static final float angleStepBase = 0.15f;
    public float angleStepDelta;
    public float angleTwistDelta;
    public boolean bDoBlending;
    public boolean bUpdateBones;
    private final Vector2 m_lastSetDir;
    private final ArrayList<AnimationBoneBindingPair> m_reparentedBoneBindings;
    private final List<AnimationBoneBinding> m_twistBones;
    private AnimationBoneBinding m_counterRotationBone;
    public final ArrayList<Integer> dismembered;
    private final float m_minimumValidAnimWeight = 0.001f;
    private final int m_animBlendIndexCacheSize = 32;
    private final int[] m_animBlendIndices;
    private final float[] m_animBlendWeights;
    private final int[] m_animBlendLayers;
    private final int[] m_animBlendPriorities;
    private final int m_maxLayers = 4;
    private final int[] m_layerBlendCounts;
    private final float[] m_layerWeightTotals;
    private int m_totalAnimBlendCount;
    public AnimationPlayer parentPlayer;
    private final Vector2 m_deferredMovement;
    private float m_deferredRotationWeight;
    private float m_deferredAngleDelta;
    private AnimationPlayerRecorder m_recorder;
    private static final AnimationTrack[] tempTracks;
    private static final Vector2 tempo;
    private static final Pool<AnimationPlayer> s_pool;
    private final AnimationMultiTrack m_multiTrack;
    
    private AnimationPlayer() {
        this.propTransforms = new Matrix4f();
        this.m_boneTransformsNeedFirstFrame = true;
        this.m_skinTransformData = null;
        this.m_skinTransformDataPool = null;
        this.m_sharedSkeleAnimationRepo = null;
        this.m_maxTwistAngle = PZMath.degToRad(70.0f);
        this.m_excessTwist = 0.0f;
        this.angleStepDelta = 1.0f;
        this.angleTwistDelta = 1.0f;
        this.bDoBlending = true;
        this.bUpdateBones = true;
        this.m_lastSetDir = new Vector2();
        this.m_reparentedBoneBindings = new ArrayList<AnimationBoneBindingPair>();
        this.m_twistBones = new ArrayList<AnimationBoneBinding>();
        this.m_counterRotationBone = null;
        this.dismembered = new ArrayList<Integer>();
        this.m_animBlendIndices = new int[32];
        this.m_animBlendWeights = new float[32];
        this.m_animBlendLayers = new int[32];
        this.m_animBlendPriorities = new int[32];
        this.m_layerBlendCounts = new int[4];
        this.m_layerWeightTotals = new float[4];
        this.m_totalAnimBlendCount = 0;
        this.m_deferredMovement = new Vector2();
        this.m_deferredRotationWeight = 0.0f;
        this.m_deferredAngleDelta = 0.0f;
        this.m_recorder = null;
        this.m_multiTrack = new AnimationMultiTrack();
    }
    
    public static AnimationPlayer alloc(final Model model) {
        final AnimationPlayer animationPlayer = AnimationPlayer.s_pool.alloc();
        animationPlayer.setModel(model);
        return animationPlayer;
    }
    
    public static float lerpBlendWeight(final float n, final float n2, final float n3) {
        if (PZMath.equal(n, n2, 1.0E-4f)) {
            return n2;
        }
        final float n4 = 1.0f / n3;
        final float timeDelta = GameTime.getInstance().getTimeDelta();
        final float n5 = (float)PZMath.sign(n2 - n);
        float n6 = n + n5 * n4 * timeDelta;
        if (PZMath.sign(n2 - n6) != n5) {
            n6 = n2;
        }
        return n6;
    }
    
    public void setModel(final Model model) {
        Objects.requireNonNull(model);
        if (model == this.model) {
            return;
        }
        this.model = model;
        this.initSkinningData();
    }
    
    public Model getModel() {
        return this.model;
    }
    
    private void initSkinningData() {
        if (!this.model.isReady()) {
            return;
        }
        final SkinningData skinningData = (SkinningData)this.model.Tag;
        if (skinningData == null) {
            return;
        }
        if (this.m_skinningData == skinningData) {
            return;
        }
        if (this.m_skinningData != null) {
            this.m_skinningData = null;
            this.m_multiTrack.reset();
        }
        this.m_skinningData = skinningData;
        Lambda.forEachFrom(PZArrayUtil::forEach, (List<AnimationBoneBindingPair>)this.m_reparentedBoneBindings, this.m_skinningData, AnimationBoneBindingPair::setSkinningData);
        Lambda.forEachFrom(PZArrayUtil::forEach, this.m_twistBones, this.m_skinningData, AnimationBoneBinding::setSkinningData);
        if (this.m_counterRotationBone != null) {
            this.m_counterRotationBone.setSkinningData(this.m_skinningData);
        }
        final int numBones = skinningData.numBones();
        this.modelTransforms = PZArrayUtil.newInstance(Matrix4f.class, this.modelTransforms, numBones, Matrix4f::new);
        this.m_boneTransforms = PZArrayUtil.newInstance(TwistableBoneTransform.class, this.m_boneTransforms, numBones);
        for (int i = 0; i < numBones; ++i) {
            if (this.m_boneTransforms[i] == null) {
                this.m_boneTransforms[i] = TwistableBoneTransform.alloc();
            }
            this.m_boneTransforms[i].setIdentity();
        }
        this.m_boneTransformsNeedFirstFrame = true;
    }
    
    public boolean isReady() {
        this.initSkinningData();
        return this.hasSkinningData();
    }
    
    public boolean hasSkinningData() {
        return this.m_skinningData != null;
    }
    
    public void addBoneReparent(final String s, final String s2) {
        if (PZArrayUtil.contains(this.m_reparentedBoneBindings, (Predicate<AnimationBoneBindingPair>)Lambda.predicate(s, s2, AnimationBoneBindingPair::matches))) {
            return;
        }
        final AnimationBoneBindingPair e = new AnimationBoneBindingPair(s, s2);
        e.setSkinningData(this.m_skinningData);
        this.m_reparentedBoneBindings.add(e);
    }
    
    public void setTwistBones(final String... array) {
        final ArrayList<String> boneNames = L_setTwistBones.boneNames;
        PZArrayUtil.listConvert(this.m_twistBones, (List<String>)boneNames, animationBoneBinding -> animationBoneBinding.boneName);
        if (PZArrayUtil.sequenceEqual(array, boneNames, PZArrayUtil.Comparators::equalsIgnoreCase)) {
            return;
        }
        this.m_twistBones.clear();
        final AnimationBoneBinding animationBoneBinding2;
        Lambda.forEachFrom(PZArrayUtil::forEach, array, this, (s, animationPlayer) -> {
            animationBoneBinding2 = new AnimationBoneBinding(s);
            animationBoneBinding2.setSkinningData(animationPlayer.m_skinningData);
            animationPlayer.m_twistBones.add(animationBoneBinding2);
        });
    }
    
    public void setCounterRotationBone(final String s) {
        if (this.m_counterRotationBone == null || StringUtils.equals(this.m_counterRotationBone.boneName, s)) {}
        (this.m_counterRotationBone = new AnimationBoneBinding(s)).setSkinningData(this.m_skinningData);
    }
    
    public AnimationBoneBinding getCounterRotationBone() {
        return this.m_counterRotationBone;
    }
    
    public void reset() {
        this.m_multiTrack.reset();
    }
    
    @Override
    public void onReleased() {
        this.model = null;
        this.m_skinningData = null;
        this.propTransforms.setIdentity();
        this.m_boneTransformsNeedFirstFrame = true;
        IPooledObject.tryReleaseAndBlank(this.m_boneTransforms);
        PZArrayUtil.forEach(this.modelTransforms, Matrix::setIdentity);
        this.resetSkinTransforms();
        this.setAngle(0.0f);
        this.setTargetAngle(0.0f);
        this.m_twistAngle = 0.0f;
        this.m_shoulderTwistAngle = 0.0f;
        this.m_targetTwistAngle = 0.0f;
        this.m_maxTwistAngle = PZMath.degToRad(70.0f);
        this.m_excessTwist = 0.0f;
        this.angleStepDelta = 1.0f;
        this.angleTwistDelta = 1.0f;
        this.bDoBlending = true;
        this.bUpdateBones = true;
        this.m_lastSetDir.set(0.0f, 0.0f);
        this.m_reparentedBoneBindings.clear();
        this.m_twistBones.clear();
        this.m_counterRotationBone = null;
        this.dismembered.clear();
        Arrays.fill(this.m_animBlendIndices, 0);
        Arrays.fill(this.m_animBlendWeights, 0.0f);
        Arrays.fill(this.m_animBlendLayers, 0);
        Arrays.fill(this.m_layerBlendCounts, 0);
        Arrays.fill(this.m_layerWeightTotals, 0.0f);
        this.m_totalAnimBlendCount = 0;
        this.parentPlayer = null;
        this.m_deferredMovement.set(0.0f, 0.0f);
        this.m_deferredRotationWeight = 0.0f;
        this.m_deferredAngleDelta = 0.0f;
        this.m_recorder = null;
        this.m_multiTrack.reset();
    }
    
    public SkinningData getSkinningData() {
        return this.m_skinningData;
    }
    
    public HashMap<String, Integer> getSkinningBoneIndices() {
        if (this.m_skinningData != null) {
            return this.m_skinningData.BoneIndices;
        }
        return null;
    }
    
    public int getSkinningBoneIndex(final String key, final int n) {
        final HashMap<String, Integer> skinningBoneIndices = this.getSkinningBoneIndices();
        if (skinningBoneIndices != null) {
            return skinningBoneIndices.get(key);
        }
        return n;
    }
    
    private synchronized SkinTransformData getSkinTransformData(final SkinningData skinnedTo) {
        for (SkinTransformData skinTransformData = this.m_skinTransformData; skinTransformData != null; skinTransformData = skinTransformData.m_next) {
            if (skinnedTo == skinTransformData.m_skinnedTo) {
                return skinTransformData;
            }
        }
        SkinTransformData skinTransformData2;
        if (this.m_skinTransformDataPool != null) {
            skinTransformData2 = this.m_skinTransformDataPool;
            skinTransformData2.setSkinnedTo(skinnedTo);
            skinTransformData2.dirty = true;
            this.m_skinTransformDataPool = this.m_skinTransformDataPool.m_next;
        }
        else {
            skinTransformData2 = SkinTransformData.alloc(skinnedTo);
        }
        skinTransformData2.m_next = this.m_skinTransformData;
        return this.m_skinTransformData = skinTransformData2;
    }
    
    private synchronized void resetSkinTransforms() {
        GameProfiler.getInstance().invokeAndMeasure("resetSkinTransforms", this, AnimationPlayer::resetSkinTransformsInternal);
    }
    
    private void resetSkinTransformsInternal() {
        if (this.m_skinTransformDataPool != null) {
            SkinTransformData skinTransformData;
            for (skinTransformData = this.m_skinTransformDataPool; skinTransformData.m_next != null; skinTransformData = skinTransformData.m_next) {}
            skinTransformData.m_next = this.m_skinTransformData;
        }
        else {
            this.m_skinTransformDataPool = this.m_skinTransformData;
        }
        this.m_skinTransformData = null;
    }
    
    public Matrix4f GetPropBoneMatrix(final int n) {
        this.propTransforms.load(this.modelTransforms[n]);
        return this.propTransforms;
    }
    
    private AnimationTrack startClip(final AnimationClip animationClip, final boolean b) {
        if (animationClip == null) {
            throw new NullPointerException("Supplied clip is null.");
        }
        final AnimationTrack alloc = AnimationTrack.alloc();
        alloc.startClip(animationClip, b);
        alloc.name = animationClip.Name;
        alloc.IsPlaying = true;
        this.m_multiTrack.addTrack(alloc);
        return alloc;
    }
    
    public static void releaseTracks(final List<AnimationTrack> list) {
        PZArrayUtil.forEach(list.toArray(AnimationPlayer.tempTracks), PooledObject::release);
    }
    
    public AnimationTrack play(final String key, final boolean b) {
        if (this.m_skinningData == null) {
            return null;
        }
        final AnimationClip animationClip = this.m_skinningData.AnimationClips.get(key);
        if (animationClip == null) {
            DebugLog.General.warn("Anim Clip not found: %s", key);
            return null;
        }
        return this.startClip(animationClip, b);
    }
    
    public void Update() {
        this.Update(GameTime.instance.getTimeDelta());
    }
    
    public void Update(final float f) {
        MPStatistic.getInstance().AnimationPlayerUpdate.Start();
        GameProfiler.getInstance().invokeAndMeasure("AnimationPlayer.Update", this, f, AnimationPlayer::updateInternal);
        MPStatistic.getInstance().AnimationPlayerUpdate.End();
    }
    
    private void updateInternal(final float n) {
        if (!this.isReady()) {
            return;
        }
        this.m_multiTrack.Update(n);
        if (!this.bUpdateBones) {
            this.updateAnimation_NonVisualOnly();
            return;
        }
        if (this.m_multiTrack.getTrackCount() <= 0) {
            return;
        }
        final SharedSkeleAnimationTrack determineCurrentSharedSkeleTrack = this.determineCurrentSharedSkeleTrack();
        if (determineCurrentSharedSkeleTrack != null) {
            this.updateAnimation_SharedSkeleTrack(determineCurrentSharedSkeleTrack, this.m_multiTrack.getTrackAt(0).getCurrentTime());
            return;
        }
        this.updateAnimation_StandardAnimation();
    }
    
    private SharedSkeleAnimationTrack determineCurrentSharedSkeleTrack() {
        if (this.m_sharedSkeleAnimationRepo == null) {
            return null;
        }
        if (this.bDoBlending) {
            return null;
        }
        if (!DebugOptions.instance.Animation.SharedSkeles.Enabled.getValue()) {
            return null;
        }
        if (this.m_multiTrack.getTrackCount() != 1) {
            return null;
        }
        if (!PZMath.equal(this.m_twistAngle, 0.0f, 114.59155f)) {
            return null;
        }
        if (this.parentPlayer != null) {
            return null;
        }
        final AnimationTrack track = this.m_multiTrack.getTrackAt(0);
        if (!PZMath.equal(track.blendFieldWeight, 0.0f, 0.1f)) {
            return null;
        }
        final AnimationClip clip = track.getClip();
        if (clip == this.m_currentSharedTrackClip) {
            return this.m_currentSharedTrack;
        }
        SharedSkeleAnimationTrack track2 = this.m_sharedSkeleAnimationRepo.getTrack(clip);
        if (track2 == null) {
            DebugLog.Animation.debugln("Caching SharedSkeleAnimationTrack: %s", track.name);
            track2 = new SharedSkeleAnimationTrack();
            final ModelTransformSampler alloc = ModelTransformSampler.alloc(this, track);
            try {
                track2.set(alloc, 5.0f);
            }
            finally {
                alloc.release();
            }
            this.m_sharedSkeleAnimationRepo.setTrack(clip, track2);
        }
        this.m_currentSharedTrackClip = clip;
        return this.m_currentSharedTrack = track2;
    }
    
    private void updateAnimation_NonVisualOnly() {
        this.updateMultiTrackBoneTransforms_DeferredMovementOnly();
        this.DoAngles();
        this.calculateDeferredMovement();
    }
    
    public void setSharedAnimRepo(final SharedSkeleAnimationRepository sharedSkeleAnimationRepo) {
        this.m_sharedSkeleAnimationRepo = sharedSkeleAnimationRepo;
    }
    
    private void updateAnimation_SharedSkeleTrack(final SharedSkeleAnimationTrack sharedSkeleAnimationTrack, final float n) {
        this.updateMultiTrackBoneTransforms_DeferredMovementOnly();
        this.DoAngles();
        this.calculateDeferredMovement();
        sharedSkeleAnimationTrack.moveToTime(n);
        for (int i = 0; i < this.modelTransforms.length; ++i) {
            sharedSkeleAnimationTrack.getBoneMatrix(i, this.modelTransforms[i]);
        }
        this.UpdateSkinTransforms();
    }
    
    private void updateAnimation_StandardAnimation() {
        if (this.parentPlayer == null) {
            this.updateMultiTrackBoneTransforms();
        }
        else {
            this.copyBoneTransformsFromParentPlayer();
        }
        this.DoAngles();
        this.calculateDeferredMovement();
        this.updateTwistBone();
        this.applyBoneReParenting();
        this.updateModelTransforms();
        this.UpdateSkinTransforms();
    }
    
    private void copyBoneTransformsFromParentPlayer() {
        this.m_boneTransformsNeedFirstFrame = false;
        for (int i = 0; i < this.m_boneTransforms.length; ++i) {
            this.m_boneTransforms[i].set(this.parentPlayer.m_boneTransforms[i]);
        }
    }
    
    public static float calculateAnimPlayerAngle(final Vector2 vector2) {
        return vector2.getDirection();
    }
    
    public void SetDir(final Vector2 vector2) {
        if (this.m_lastSetDir.x != vector2.x || this.m_lastSetDir.y != vector2.y) {
            this.setTargetAngle(calculateAnimPlayerAngle(vector2));
            this.m_targetTwistAngle = PZMath.getClosestAngle(this.m_angle, this.m_targetAngle);
            this.m_excessTwist = PZMath.getClosestAngle(PZMath.clamp(this.m_targetTwistAngle, -this.m_maxTwistAngle, this.m_maxTwistAngle), this.m_targetTwistAngle);
            this.m_lastSetDir.set(vector2);
        }
    }
    
    public void SetForceDir(final Vector2 vector2) {
        this.setTargetAngle(calculateAnimPlayerAngle(vector2));
        this.setAngleToTarget();
        this.m_targetTwistAngle = 0.0f;
        this.m_lastSetDir.set(vector2);
    }
    
    public void UpdateDir(final IsoGameCharacter isoGameCharacter) {
        if (isoGameCharacter != null) {
            this.SetDir(isoGameCharacter.getForwardDirection());
        }
    }
    
    public void DoAngles() {
        GameProfiler.getInstance().invokeAndMeasure("AnimationPlayer.doAngles", this, AnimationPlayer::doAnglesInternal);
    }
    
    private void doAnglesInternal() {
        final float n = 0.15f * GameTime.instance.getMultiplier();
        this.interpolateBodyAngle(n);
        this.interpolateBodyTwist(n);
        this.interpolateShoulderTwist(n);
    }
    
    private void interpolateBodyAngle(final float n) {
        final float closestAngle = PZMath.getClosestAngle(this.m_angle, this.m_targetAngle);
        if (PZMath.equal(closestAngle, 0.0f, 0.001f)) {
            this.setAngleToTarget();
            this.m_targetTwistAngle = 0.0f;
            return;
        }
        final float n2 = (float)PZMath.sign(closestAngle);
        final float n3 = n * n2 * this.angleStepDelta;
        float n4;
        if (DebugOptions.instance.Character.Debug.Animate.DeferredRotationOnly.getValue()) {
            n4 = this.m_deferredAngleDelta;
        }
        else if (this.m_deferredRotationWeight > 0.0f) {
            n4 = this.m_deferredAngleDelta;
        }
        else {
            n4 = n3;
        }
        final float n5 = (float)PZMath.sign(n4);
        final float angle = this.m_angle + n4;
        final float closestAngle2 = PZMath.getClosestAngle(angle, this.m_targetAngle);
        if (PZMath.sign(closestAngle2) == n2 || n5 != n2) {
            this.setAngle(angle);
            this.m_targetTwistAngle = closestAngle2;
        }
        else {
            this.setAngleToTarget();
            this.m_targetTwistAngle = 0.0f;
        }
    }
    
    private void interpolateBodyTwist(final float n) {
        final float wrap = PZMath.wrap(this.m_targetTwistAngle, -3.1415927f, 3.1415927f);
        final float clamp = PZMath.clamp(wrap, -this.m_maxTwistAngle, this.m_maxTwistAngle);
        this.m_excessTwist = PZMath.getClosestAngle(clamp, wrap);
        final float closestAngle = PZMath.getClosestAngle(this.m_twistAngle, clamp);
        if (PZMath.equal(closestAngle, 0.0f, 0.001f)) {
            this.m_twistAngle = clamp;
            return;
        }
        final float n2 = (float)PZMath.sign(closestAngle);
        final float twistAngle = this.m_twistAngle + n * n2 * this.angleTwistDelta;
        if (PZMath.sign(PZMath.getClosestAngle(twistAngle, clamp)) == n2) {
            this.m_twistAngle = twistAngle;
        }
        else {
            this.m_twistAngle = clamp;
        }
    }
    
    private void interpolateShoulderTwist(final float n) {
        final float wrap = PZMath.wrap(this.m_twistAngle, -3.1415927f, 3.1415927f);
        final float closestAngle = PZMath.getClosestAngle(this.m_shoulderTwistAngle, wrap);
        if (PZMath.equal(closestAngle, 0.0f, 0.001f)) {
            this.m_shoulderTwistAngle = wrap;
            return;
        }
        final float n2 = (float)PZMath.sign(closestAngle);
        final float shoulderTwistAngle = this.m_shoulderTwistAngle + n * n2 * this.angleTwistDelta * 0.55f;
        if (PZMath.sign(PZMath.getClosestAngle(shoulderTwistAngle, wrap)) == n2) {
            this.m_shoulderTwistAngle = shoulderTwistAngle;
        }
        else {
            this.m_shoulderTwistAngle = wrap;
        }
    }
    
    private void updateTwistBone() {
        GameProfiler.getInstance().invokeAndMeasure("updateTwistBone", this, AnimationPlayer::updateTwistBoneInternal);
    }
    
    private void updateTwistBoneInternal() {
        if (this.m_twistBones.isEmpty()) {
            return;
        }
        if (PZMath.equal(this.m_twistAngle, 0.0f, PZMath.degToRad(1.0f))) {
            return;
        }
        if (DebugOptions.instance.Character.Debug.Animate.NoBoneTwists.getValue()) {
            return;
        }
        final int n = this.m_twistBones.size() - 1;
        final float n2 = -this.m_shoulderTwistAngle;
        final float n3 = n2 / n;
        for (int i = 0; i < n; ++i) {
            this.applyTwistBone(this.m_twistBones.get(i).getBone(), n3);
        }
        final float closestAngle = PZMath.getClosestAngle(n2, -this.m_twistAngle);
        if (PZMath.abs(closestAngle) > 1.0E-4f) {
            this.applyTwistBone(this.m_twistBones.get(n).getBone(), closestAngle);
        }
    }
    
    private void applyTwistBone(final SkinningBone skinningBone, final float twist) {
        if (skinningBone == null) {
            return;
        }
        final int index = skinningBone.Index;
        final Matrix4f invert = Matrix4f.invert(this.getBoneModelTransform(skinningBone.Parent.Index, L_applyTwistBone.twistParentBoneTrans), L_applyTwistBone.twistParentBoneTransInv);
        if (invert == null) {
            return;
        }
        final Matrix4f boneModelTransform = this.getBoneModelTransform(index, L_applyTwistBone.twistBoneTrans);
        final Quaternion twistBoneTargetRot = L_applyTwistBone.twistBoneTargetRot;
        final Matrix4f twistRotDiffTrans = L_applyTwistBone.twistRotDiffTrans;
        twistRotDiffTrans.setIdentity();
        L_applyTwistBone.twistRotDiffTransAxis.set(0.0f, 1.0f, 0.0f);
        final float closestAngle = PZMath.getClosestAngle(this.m_boneTransforms[index].Twist, twist);
        this.m_boneTransforms[index].Twist = twist;
        twistRotDiffTrans.rotate(closestAngle, L_applyTwistBone.twistRotDiffTransAxis);
        final Matrix4f twistBoneTargetTrans = L_applyTwistBone.twistBoneTargetTrans;
        Matrix4f.mul(boneModelTransform, twistRotDiffTrans, twistBoneTargetTrans);
        HelperFunctions.getRotation(twistBoneTargetTrans, twistBoneTargetRot);
        final Quaternion twistBoneNewRot = L_applyTwistBone.twistBoneNewRot;
        twistBoneNewRot.set((ReadableVector4f)twistBoneTargetRot);
        final Vector3f position = HelperFunctions.getPosition(boneModelTransform, L_applyTwistBone.twistBonePos);
        final Vector3f twistBoneScale = L_applyTwistBone.twistBoneScale;
        twistBoneScale.set(1.0f, 1.0f, 1.0f);
        final Matrix4f twistBoneNewTrans = L_applyTwistBone.twistBoneNewTrans;
        HelperFunctions.CreateFromQuaternionPositionScale(position, twistBoneNewRot, twistBoneScale, twistBoneNewTrans);
        this.m_boneTransforms[index].mul(twistBoneNewTrans, invert);
    }
    
    public void resetBoneModelTransforms() {
        if (this.m_skinningData == null || this.modelTransforms == null) {
            return;
        }
        this.m_boneTransformsNeedFirstFrame = true;
        for (int length = this.m_boneTransforms.length, i = 0; i < length; ++i) {
            this.m_boneTransforms[i].BlendWeight = 0.0f;
            this.m_boneTransforms[i].setIdentity();
            this.modelTransforms[i].setIdentity();
        }
    }
    
    public boolean isBoneTransformsNeedFirstFrame() {
        return this.m_boneTransformsNeedFirstFrame;
    }
    
    private void updateMultiTrackBoneTransforms() {
        GameProfiler.getInstance().invokeAndMeasure("updateMultiTrackBoneTransforms", this, AnimationPlayer::updateMultiTrackBoneTransformsInternal);
    }
    
    private void updateMultiTrackBoneTransformsInternal() {
        for (int i = 0; i < this.modelTransforms.length; ++i) {
            this.modelTransforms[i].setIdentity();
        }
        this.updateLayerBlendWeightings();
        if (this.m_totalAnimBlendCount == 0) {
            return;
        }
        if (this.isRecording()) {
            this.m_recorder.logAnimWeights(this.m_multiTrack.getTracks(), this.m_animBlendIndices, this.m_animBlendWeights, this.m_deferredMovement);
        }
        for (int j = 0; j < this.m_boneTransforms.length; ++j) {
            if (!this.isBoneReparented(j)) {
                this.updateBoneAnimationTransform(j, null);
            }
        }
        this.m_boneTransformsNeedFirstFrame = false;
    }
    
    private void updateLayerBlendWeightings() {
        final List<AnimationTrack> tracks = this.m_multiTrack.getTracks();
        final int size = tracks.size();
        PZArrayUtil.arraySet(this.m_animBlendIndices, -1);
        PZArrayUtil.arraySet(this.m_animBlendWeights, 0.0f);
        PZArrayUtil.arraySet(this.m_animBlendLayers, -1);
        PZArrayUtil.arraySet(this.m_animBlendPriorities, 0);
        for (int i = 0; i < size; ++i) {
            final AnimationTrack animationTrack = tracks.get(i);
            final float blendDelta = animationTrack.BlendDelta;
            final int layerIdx = animationTrack.getLayerIdx();
            final int priority = animationTrack.getPriority();
            if (layerIdx < 0 || layerIdx >= 4) {
                DebugLog.General.error("Layer index is out of range: %d. Range: 0 - %d", layerIdx, 3);
            }
            else if (blendDelta >= 0.001f) {
                if (layerIdx <= 0 || !animationTrack.isFinished()) {
                    int n = -1;
                    for (int j = 0; j < this.m_animBlendIndices.length; ++j) {
                        if (this.m_animBlendIndices[j] == -1) {
                            n = j;
                            break;
                        }
                        if (layerIdx <= this.m_animBlendLayers[j]) {
                            if (layerIdx < this.m_animBlendLayers[j]) {
                                n = j;
                                break;
                            }
                            if (priority <= this.m_animBlendPriorities[j]) {
                                if (priority < this.m_animBlendPriorities[j]) {
                                    n = j;
                                    break;
                                }
                                if (blendDelta < this.m_animBlendWeights[j]) {
                                    n = j;
                                    break;
                                }
                            }
                        }
                    }
                    if (n < 0) {
                        DebugLog.General.error("Buffer overflow. Insufficient anim blends in cache. More than %d animations are being blended at once. Will be truncated to %d.", this.m_animBlendIndices.length, this.m_animBlendIndices.length);
                    }
                    else {
                        PZArrayUtil.insertAt(this.m_animBlendIndices, n, i);
                        PZArrayUtil.insertAt(this.m_animBlendWeights, n, blendDelta);
                        PZArrayUtil.insertAt(this.m_animBlendLayers, n, layerIdx);
                        PZArrayUtil.insertAt(this.m_animBlendPriorities, n, priority);
                    }
                }
            }
        }
        PZArrayUtil.arraySet(this.m_layerBlendCounts, 0);
        PZArrayUtil.arraySet(this.m_layerWeightTotals, 0.0f);
        this.m_totalAnimBlendCount = 0;
        for (int n2 = 0; n2 < this.m_animBlendIndices.length && this.m_animBlendIndices[n2] >= 0; ++n2) {
            final int n3 = this.m_animBlendLayers[n2];
            final float[] layerWeightTotals = this.m_layerWeightTotals;
            final int n4 = n3;
            layerWeightTotals[n4] += this.m_animBlendWeights[n2];
            final int[] layerBlendCounts = this.m_layerBlendCounts;
            final int n5 = n3;
            ++layerBlendCounts[n5];
            ++this.m_totalAnimBlendCount;
        }
        if (this.m_totalAnimBlendCount == 0) {
            return;
        }
        if (this.m_boneTransformsNeedFirstFrame) {
            final int n6 = this.m_animBlendLayers[0];
            final int n7 = this.m_layerBlendCounts[0];
            final float n8 = this.m_layerWeightTotals[0];
            if (n8 < 1.0f) {
                for (int k = 0; k < this.m_totalAnimBlendCount; ++k) {
                    if (this.m_animBlendLayers[k] != n6) {
                        break;
                    }
                    if (n8 > 0.0f) {
                        final float[] animBlendWeights = this.m_animBlendWeights;
                        final int n9 = k;
                        animBlendWeights[n9] /= n8;
                    }
                    else {
                        this.m_animBlendWeights[k] = 1.0f / n7;
                    }
                }
            }
        }
    }
    
    private void calculateDeferredMovement() {
        GameProfiler.getInstance().invokeAndMeasure("calculateDeferredMovement", this, AnimationPlayer::calculateDeferredMovementInternal);
    }
    
    private void calculateDeferredMovementInternal() {
        final List<AnimationTrack> tracks = this.m_multiTrack.getTracks();
        this.m_deferredMovement.set(0.0f, 0.0f);
        this.m_deferredAngleDelta = 0.0f;
        this.m_deferredRotationWeight = 0.0f;
        float max = 1.0f;
        for (int n = this.m_totalAnimBlendCount - 1; n >= 0 && max > 0.001f; --n) {
            final AnimationTrack animationTrack = tracks.get(this.m_animBlendIndices[n]);
            if (!animationTrack.isFinished()) {
                final float deferredBoneWeight = animationTrack.getDeferredBoneWeight();
                if (deferredBoneWeight > 0.001f) {
                    final float n2 = this.m_animBlendWeights[n] * deferredBoneWeight;
                    if (n2 > 0.001f) {
                        final float clamp = PZMath.clamp(n2, 0.0f, max);
                        max = Math.max(0.0f, max - n2);
                        Vector2.addScaled(this.m_deferredMovement, animationTrack.getDeferredMovementDiff(AnimationPlayer.tempo), clamp, this.m_deferredMovement);
                        if (animationTrack.getUseDeferredRotation()) {
                            this.m_deferredAngleDelta += animationTrack.getDeferredRotationDiff() * clamp;
                            this.m_deferredRotationWeight += clamp;
                        }
                    }
                }
            }
        }
        this.applyRotationToDeferredMovement(this.m_deferredMovement);
        final Vector2 deferredMovement = this.m_deferredMovement;
        deferredMovement.x *= AdvancedAnimator.s_MotionScale;
        final Vector2 deferredMovement2 = this.m_deferredMovement;
        deferredMovement2.y *= AdvancedAnimator.s_MotionScale;
        this.m_deferredAngleDelta *= AdvancedAnimator.s_RotationScale;
    }
    
    private void applyRotationToDeferredMovement(final Vector2 vector2) {
        final float normalize = vector2.normalize();
        vector2.rotate(this.getRenderedAngle());
        vector2.setLength(-normalize);
    }
    
    private void applyBoneReParenting() {
        GameProfiler.getInstance().invokeAndMeasure("applyBoneReParenting", this, AnimationPlayer::applyBoneReParentingInternal);
    }
    
    private void applyBoneReParentingInternal() {
        for (int i = 0; i < this.m_reparentedBoneBindings.size(); ++i) {
            final AnimationBoneBindingPair animationBoneBindingPair = this.m_reparentedBoneBindings.get(i);
            if (!animationBoneBindingPair.isValid()) {
                DebugLog.Animation.warn("Animation binding pair is not valid: %s", animationBoneBindingPair);
            }
            else {
                this.updateBoneAnimationTransform(animationBoneBindingPair.getBoneIdxA(), animationBoneBindingPair);
            }
        }
    }
    
    private void updateBoneAnimationTransform(final int n, final AnimationBoneBindingPair animationBoneBindingPair) {
        this.updateBoneAnimationTransform_Internal(n, animationBoneBindingPair);
    }
    
    private void updateBoneAnimationTransform_Internal(final int n, final AnimationBoneBindingPair animationBoneBindingPair) {
        final List<AnimationTrack> tracks = this.m_multiTrack.getTracks();
        final Vector3f pos = L_updateBoneAnimationTransform.pos;
        final Quaternion rot = L_updateBoneAnimationTransform.rot;
        final Vector3f scale = L_updateBoneAnimationTransform.scale;
        final Keyframe key = L_updateBoneAnimationTransform.key;
        final int totalAnimBlendCount = this.m_totalAnimBlendCount;
        final AnimationBoneBinding counterRotationBone = this.m_counterRotationBone;
        final boolean b = counterRotationBone != null && counterRotationBone.getBone() != null && counterRotationBone.getBone().Index == n;
        key.setIdentity();
        float blendWeight = 0.0f;
        int n2 = 1;
        float max = 1.0f;
        for (int n3 = totalAnimBlendCount - 1; n3 >= 0 && max > 0.0f && max > 0.001f; --n3) {
            final AnimationTrack animationTrack = tracks.get(this.m_animBlendIndices[n3]);
            final float boneWeight = animationTrack.getBoneWeight(n);
            if (boneWeight > 0.001f) {
                final float n4 = this.m_animBlendWeights[n3] * boneWeight;
                if (n4 > 0.001f) {
                    final float clamp = PZMath.clamp(n4, 0.0f, max);
                    max = Math.max(0.0f, max - n4);
                    this.getTrackTransform(n, animationTrack, animationBoneBindingPair, pos, rot, scale);
                    if (b && animationTrack.getUseDeferredRotation()) {
                        if (DebugOptions.instance.Character.Debug.Animate.ZeroCounterRotationBone.getValue()) {
                            final Vector3f rotAxis = L_updateBoneAnimationTransform.rotAxis;
                            final Matrix4f rotMat = L_updateBoneAnimationTransform.rotMat;
                            rotMat.setIdentity();
                            rotAxis.set(0.0f, 1.0f, 0.0f);
                            rotMat.rotate(-1.5707964f, rotAxis);
                            rotAxis.set(1.0f, 0.0f, 0.0f);
                            rotMat.rotate(-1.5707964f, rotAxis);
                            HelperFunctions.getRotation(rotMat, rot);
                        }
                        else {
                            final Vector3f toEulerAngles = HelperFunctions.ToEulerAngles(rot, L_updateBoneAnimationTransform.rotEulers);
                            HelperFunctions.ToQuaternion(toEulerAngles.x, toEulerAngles.y, 1.5707963705062866, rot);
                        }
                    }
                    if (animationTrack.getDeferredMovementBoneIdx() == n) {
                        final Vector3f currentDeferredCounterPosition = animationTrack.getCurrentDeferredCounterPosition(L_updateBoneAnimationTransform.deferredPos);
                        final Vector3f vector3f = pos;
                        vector3f.x += currentDeferredCounterPosition.x;
                        final Vector3f vector3f2 = pos;
                        vector3f2.y += currentDeferredCounterPosition.y;
                        final Vector3f vector3f3 = pos;
                        vector3f3.z += currentDeferredCounterPosition.z;
                    }
                    if (n2 != 0) {
                        Vector3.setScaled(pos, clamp, key.Position);
                        key.Rotation.set((ReadableVector4f)rot);
                        blendWeight = clamp;
                        n2 = 0;
                    }
                    else {
                        final float n5 = clamp / (clamp + blendWeight);
                        blendWeight += clamp;
                        Vector3.addScaled(key.Position, pos, clamp, key.Position);
                        PZMath.slerp(key.Rotation, key.Rotation, rot, n5);
                    }
                }
            }
        }
        if (max > 0.0f && !this.m_boneTransformsNeedFirstFrame) {
            this.m_boneTransforms[n].getPRS(pos, rot, scale);
            Vector3.addScaled(key.Position, pos, max, key.Position);
            PZMath.slerp(key.Rotation, rot, key.Rotation, blendWeight);
            PZMath.lerp(key.Scale, scale, key.Scale, blendWeight);
        }
        this.m_boneTransforms[n].set(key.Position, key.Rotation, key.Scale);
        this.m_boneTransforms[n].BlendWeight = blendWeight;
        final TwistableBoneTransform twistableBoneTransform = this.m_boneTransforms[n];
        twistableBoneTransform.Twist *= 1.0f - blendWeight;
    }
    
    private void getTrackTransform(final int n, final AnimationTrack animationTrack, final AnimationBoneBindingPair animationBoneBindingPair, final Vector3f vector3f, final Quaternion quaternion, final Vector3f vector3f2) {
        if (animationBoneBindingPair == null) {
            animationTrack.get(n, vector3f, quaternion, vector3f2);
            return;
        }
        final Matrix4f result = L_getTrackTransform.result;
        final SkinningBone boneA = animationBoneBindingPair.getBoneA();
        final Matrix4f unweightedBoneTransform = getUnweightedBoneTransform(animationTrack, boneA.Index, L_getTrackTransform.Pa);
        final SkinningBone parent = boneA.Parent;
        final SkinningBone boneB = animationBoneBindingPair.getBoneB();
        final Matrix4f invert = Matrix4f.invert(this.getBoneModelTransform(parent.Index, L_getTrackTransform.mA), L_getTrackTransform.mAinv);
        final Matrix4f boneModelTransform = this.getBoneModelTransform(boneB.Index, L_getTrackTransform.mB);
        final Matrix4f unweightedModelTransform = this.getUnweightedModelTransform(animationTrack, parent.Index, L_getTrackTransform.umA);
        final Matrix4f invert2 = Matrix4f.invert(this.getUnweightedModelTransform(animationTrack, boneB.Index, L_getTrackTransform.umB), L_getTrackTransform.umBinv);
        Matrix4f.mul(unweightedBoneTransform, unweightedModelTransform, result);
        Matrix4f.mul(result, invert2, result);
        Matrix4f.mul(result, boneModelTransform, result);
        Matrix4f.mul(result, invert, result);
        HelperFunctions.getPosition(result, vector3f);
        HelperFunctions.getRotation(result, quaternion);
        vector3f2.set(1.0f, 1.0f, 1.0f);
    }
    
    public boolean isBoneReparented(final int i) {
        return PZArrayUtil.contains(this.m_reparentedBoneBindings, (Predicate<AnimationBoneBindingPair>)Lambda.predicate(i, (animationBoneBindingPair, n) -> animationBoneBindingPair.getBoneIdxA() == n));
    }
    
    public void updateMultiTrackBoneTransforms_DeferredMovementOnly() {
        this.m_deferredMovement.set(0.0f, 0.0f);
        if (this.parentPlayer != null) {
            return;
        }
        this.updateLayerBlendWeightings();
        if (this.m_totalAnimBlendCount == 0) {
            return;
        }
        final int[] boneIndices = updateMultiTrackBoneTransforms_DeferredMovementOnly.boneIndices;
        int n = 0;
        final List<AnimationTrack> tracks = this.m_multiTrack.getTracks();
        for (int size = tracks.size(), i = 0; i < size; ++i) {
            final int deferredMovementBoneIdx = tracks.get(i).getDeferredMovementBoneIdx();
            if (deferredMovementBoneIdx != -1 && !PZArrayUtil.contains(boneIndices, n, deferredMovementBoneIdx)) {
                boneIndices[n++] = deferredMovementBoneIdx;
            }
        }
        for (int j = 0; j < n; ++j) {
            this.updateBoneAnimationTransform(boneIndices[j], null);
        }
    }
    
    public boolean isRecording() {
        return this.m_recorder != null && this.m_recorder.isRecording();
    }
    
    public void setRecorder(final AnimationPlayerRecorder recorder) {
        this.m_recorder = recorder;
    }
    
    public AnimationPlayerRecorder getRecorder() {
        return this.m_recorder;
    }
    
    public void dismember(final int i) {
        this.dismembered.add(i);
    }
    
    private void updateModelTransforms() {
        GameProfiler.getInstance().invokeAndMeasure("updateModelTransforms", this, AnimationPlayer::updateModelTransformsInternal);
    }
    
    private void updateModelTransformsInternal() {
        this.m_boneTransforms[0].getMatrix(this.modelTransforms[0]);
        for (int i = 1; i < this.modelTransforms.length; ++i) {
            final SkinningBone bone = this.m_skinningData.getBoneAt(i);
            BoneTransform.mul(this.m_boneTransforms[bone.Index], this.modelTransforms[bone.Parent.Index], this.modelTransforms[bone.Index]);
        }
    }
    
    public Matrix4f getBoneModelTransform(final int n, final Matrix4f matrix4f) {
        final Matrix4f boneTransform = L_getBoneModelTransform.boneTransform;
        matrix4f.setIdentity();
        for (SkinningBone skinningBone = this.m_skinningData.getBoneAt(n); skinningBone != null; skinningBone = skinningBone.Parent) {
            this.getBoneTransform(skinningBone.Index, boneTransform);
            Matrix4f.mul(matrix4f, boneTransform, matrix4f);
        }
        return matrix4f;
    }
    
    public Matrix4f getBoneTransform(final int n, final Matrix4f matrix4f) {
        this.m_boneTransforms[n].getMatrix(matrix4f);
        return matrix4f;
    }
    
    public Matrix4f getUnweightedModelTransform(final AnimationTrack animationTrack, final int n, final Matrix4f matrix4f) {
        final Matrix4f boneTransform = L_getUnweightedModelTransform.boneTransform;
        boneTransform.setIdentity();
        matrix4f.setIdentity();
        for (SkinningBone skinningBone = this.m_skinningData.getBoneAt(n); skinningBone != null; skinningBone = skinningBone.Parent) {
            getUnweightedBoneTransform(animationTrack, skinningBone.Index, boneTransform);
            Matrix4f.mul(matrix4f, boneTransform, matrix4f);
        }
        return matrix4f;
    }
    
    public static Matrix4f getUnweightedBoneTransform(final AnimationTrack animationTrack, final int n, final Matrix4f matrix4f) {
        final Vector3f pos = L_getUnweightedBoneTransform.pos;
        final Quaternion rot = L_getUnweightedBoneTransform.rot;
        final Vector3f scale = L_getUnweightedBoneTransform.scale;
        animationTrack.get(n, pos, rot, scale);
        HelperFunctions.CreateFromQuaternionPositionScale(pos, rot, scale, matrix4f);
        return matrix4f;
    }
    
    public void UpdateSkinTransforms() {
        this.resetSkinTransforms();
    }
    
    public Matrix4f[] getSkinTransforms(final SkinningData skinningData) {
        if (skinningData == null) {
            return this.modelTransforms;
        }
        final SkinTransformData skinTransformData = this.getSkinTransformData(skinningData);
        final Matrix4f[] transforms = skinTransformData.transforms;
        if (skinTransformData.dirty) {
            for (int i = 0; i < this.modelTransforms.length; ++i) {
                if (skinningData.BoneOffset != null && skinningData.BoneOffset.get(i) != null) {
                    Matrix4f.mul((Matrix4f)skinningData.BoneOffset.get(i), this.modelTransforms[i], transforms[i]);
                }
                else {
                    transforms[i].setIdentity();
                }
            }
            skinTransformData.dirty = false;
        }
        return transforms;
    }
    
    public void getDeferredMovement(final Vector2 vector2) {
        vector2.set(this.m_deferredMovement);
    }
    
    public float getDeferredAngleDelta() {
        return this.m_deferredAngleDelta;
    }
    
    public float getDeferredRotationWeight() {
        return this.m_deferredRotationWeight;
    }
    
    public AnimationMultiTrack getMultiTrack() {
        return this.m_multiTrack;
    }
    
    public void setRecording(final boolean recording) {
        this.m_recorder.setRecording(recording);
    }
    
    public void discardRecording() {
        if (this.m_recorder != null) {
            this.m_recorder.discardRecording();
        }
    }
    
    public float getRenderedAngle() {
        return this.m_angle + 1.5707964f;
    }
    
    public float getAngle() {
        return this.m_angle;
    }
    
    public void setAngle(final float angle) {
        this.m_angle = angle;
    }
    
    public void setAngleToTarget() {
        this.setAngle(this.getTargetAngle());
    }
    
    public void setTargetToAngle() {
        this.setTargetAngle(this.getAngle());
    }
    
    public float getTargetAngle() {
        return this.m_targetAngle;
    }
    
    public void setTargetAngle(final float targetAngle) {
        this.m_targetAngle = targetAngle;
    }
    
    public float getMaxTwistAngle() {
        return this.m_maxTwistAngle;
    }
    
    public void setMaxTwistAngle(final float maxTwistAngle) {
        this.m_maxTwistAngle = maxTwistAngle;
    }
    
    public float getExcessTwistAngle() {
        return this.m_excessTwist;
    }
    
    public float getTwistAngle() {
        return this.m_twistAngle;
    }
    
    public float getShoulderTwistAngle() {
        return this.m_shoulderTwistAngle;
    }
    
    public float getTargetTwistAngle() {
        return this.m_targetTwistAngle;
    }
    
    static {
        tempTracks = new AnimationTrack[0];
        tempo = new Vector2();
        s_pool = new Pool<AnimationPlayer>(AnimationPlayer::new);
    }
    
    private static final class updateMultiTrackBoneTransforms_DeferredMovementOnly
    {
        static int[] boneIndices;
        
        static {
            updateMultiTrackBoneTransforms_DeferredMovementOnly.boneIndices = new int[60];
        }
    }
    
    private static class SkinTransformData extends PooledObject
    {
        public Matrix4f[] transforms;
        private SkinningData m_skinnedTo;
        public boolean dirty;
        private SkinTransformData m_next;
        private static Pool<SkinTransformData> s_pool;
        
        public void setSkinnedTo(final SkinningData skinnedTo) {
            if (this.m_skinnedTo == skinnedTo) {
                return;
            }
            this.dirty = true;
            this.m_skinnedTo = skinnedTo;
            this.transforms = PZArrayUtil.newInstance(Matrix4f.class, this.transforms, skinnedTo.numBones(), Matrix4f::new);
        }
        
        public static SkinTransformData alloc(final SkinningData skinnedTo) {
            final SkinTransformData skinTransformData = SkinTransformData.s_pool.alloc();
            skinTransformData.setSkinnedTo(skinnedTo);
            skinTransformData.dirty = true;
            return skinTransformData;
        }
        
        static {
            SkinTransformData.s_pool = new Pool<SkinTransformData>(SkinTransformData::new);
        }
    }
    
    private static class L_getBoneModelTransform
    {
        static final Matrix4f boneTransform;
        
        static {
            boneTransform = new Matrix4f();
        }
    }
    
    private static final class L_getTrackTransform
    {
        static final Matrix4f Pa;
        static final Matrix4f mA;
        static final Matrix4f mB;
        static final Matrix4f umA;
        static final Matrix4f umB;
        static final Matrix4f mAinv;
        static final Matrix4f umBinv;
        static final Matrix4f result;
        
        static {
            Pa = new Matrix4f();
            mA = new Matrix4f();
            mB = new Matrix4f();
            umA = new Matrix4f();
            umB = new Matrix4f();
            mAinv = new Matrix4f();
            umBinv = new Matrix4f();
            result = new Matrix4f();
        }
    }
    
    private static final class L_updateBoneAnimationTransform
    {
        static final Quaternion rot;
        static final Vector3f pos;
        static final Vector3f scale;
        static final Keyframe key;
        static final Matrix4f boneMat;
        static final Matrix4f rotMat;
        static final Vector3f rotAxis;
        static final Quaternion crRot;
        static final Vector4f crRotAA;
        static final Matrix4f crMat;
        static final Vector3f rotEulers;
        static final Vector3f deferredPos;
        
        static {
            rot = new Quaternion();
            pos = new Vector3f();
            scale = new Vector3f();
            key = new Keyframe(new Vector3f(0.0f, 0.0f, 0.0f), new Quaternion(0.0f, 0.0f, 0.0f, 1.0f), new Vector3f(1.0f, 1.0f, 1.0f));
            boneMat = new Matrix4f();
            rotMat = new Matrix4f();
            rotAxis = new Vector3f(1.0f, 0.0f, 0.0f);
            crRot = new Quaternion();
            crRotAA = new Vector4f();
            crMat = new Matrix4f();
            rotEulers = new Vector3f();
            deferredPos = new Vector3f();
        }
    }
    
    private static class L_getUnweightedModelTransform
    {
        static final Matrix4f boneTransform;
        
        static {
            boneTransform = new Matrix4f();
        }
    }
    
    private static class L_getUnweightedBoneTransform
    {
        static final Vector3f pos;
        static final Quaternion rot;
        static final Vector3f scale;
        
        static {
            pos = new Vector3f();
            rot = new Quaternion();
            scale = new Vector3f();
        }
    }
    
    private static class L_applyTwistBone
    {
        static final Matrix4f twistParentBoneTrans;
        static final Matrix4f twistParentBoneTransInv;
        static final Matrix4f twistBoneTrans;
        static final Quaternion twistBoneRot;
        static final Quaternion twistBoneTargetRot;
        static final Matrix4f twistRotDiffTrans;
        static final Vector3f twistRotDiffTransAxis;
        static final Matrix4f twistBoneTargetTrans;
        static final Quaternion twistBoneNewRot;
        static final Vector3f twistBonePos;
        static final Vector3f twistBoneScale;
        static final Matrix4f twistBoneNewTrans;
        
        static {
            twistParentBoneTrans = new Matrix4f();
            twistParentBoneTransInv = new Matrix4f();
            twistBoneTrans = new Matrix4f();
            twistBoneRot = new Quaternion();
            twistBoneTargetRot = new Quaternion();
            twistRotDiffTrans = new Matrix4f();
            twistRotDiffTransAxis = new Vector3f(0.0f, 1.0f, 0.0f);
            twistBoneTargetTrans = new Matrix4f();
            twistBoneNewRot = new Quaternion();
            twistBonePos = new Vector3f();
            twistBoneScale = new Vector3f();
            twistBoneNewTrans = new Matrix4f();
        }
    }
    
    private static final class L_setTwistBones
    {
        static final ArrayList<String> boneNames;
        
        static {
            boneNames = new ArrayList<String>();
        }
    }
}

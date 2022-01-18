// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.animation;

import zombie.core.skinnedmodel.model.SkinningBone;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.ReadableVector4f;
import org.lwjgl.util.vector.ReadableVector3f;
import zombie.core.skinnedmodel.HelperFunctions;
import zombie.debug.DebugOptions;
import zombie.util.list.PZArrayUtil;
import zombie.util.Pool;
import org.lwjgl.util.vector.Matrix4f;
import zombie.core.skinnedmodel.model.SkinningData;
import zombie.util.PooledObject;

public class ModelTransformSampler extends PooledObject implements AnimTrackSampler
{
    private AnimationPlayer m_sourceAnimPlayer;
    private AnimationTrack m_track;
    private float m_currentTime;
    private SkinningData m_skinningData;
    private BoneTransform[] m_boneTransforms;
    private Matrix4f[] m_boneModelTransforms;
    private static final Pool<ModelTransformSampler> s_pool;
    
    public ModelTransformSampler() {
        this.m_currentTime = 0.0f;
    }
    
    private void init(final AnimationPlayer sourceAnimPlayer, final AnimationTrack animationTrack) {
        this.m_sourceAnimPlayer = sourceAnimPlayer;
        this.m_track = AnimationTrack.createClone(animationTrack, AnimationTrack::alloc);
        final SkinningData skinningData = this.m_sourceAnimPlayer.getSkinningData();
        final int numBones = skinningData.numBones();
        this.m_skinningData = skinningData;
        this.m_boneModelTransforms = PZArrayUtil.newInstance(Matrix4f.class, this.m_boneModelTransforms, numBones, Matrix4f::new);
        this.m_boneTransforms = PZArrayUtil.newInstance(BoneTransform.class, this.m_boneTransforms, numBones, BoneTransform::alloc);
    }
    
    public static ModelTransformSampler alloc(final AnimationPlayer animationPlayer, final AnimationTrack animationTrack) {
        final ModelTransformSampler modelTransformSampler = ModelTransformSampler.s_pool.alloc();
        modelTransformSampler.init(animationPlayer, animationTrack);
        return modelTransformSampler;
    }
    
    @Override
    public void onReleased() {
        this.m_sourceAnimPlayer = null;
        this.m_track = Pool.tryRelease(this.m_track);
        this.m_skinningData = null;
        this.m_boneTransforms = Pool.tryRelease(this.m_boneTransforms);
    }
    
    @Override
    public float getTotalTime() {
        return this.m_track.getDuration();
    }
    
    @Override
    public boolean isLooped() {
        return this.m_track.isLooping();
    }
    
    @Override
    public void moveToTime(final float n) {
        this.m_currentTime = n;
        this.m_track.setCurrentTimeValue(n);
        this.m_track.Update(0.0f);
        for (int i = 0; i < this.m_boneTransforms.length; ++i) {
            this.updateBoneAnimationTransform(i);
        }
    }
    
    private void updateBoneAnimationTransform(final int n) {
        final Vector3f pos = L_updateBoneAnimationTransform.pos;
        final Quaternion rot = L_updateBoneAnimationTransform.rot;
        final Vector3f scale = L_updateBoneAnimationTransform.scale;
        final Keyframe key = L_updateBoneAnimationTransform.key;
        final AnimationBoneBinding counterRotationBone = this.m_sourceAnimPlayer.getCounterRotationBone();
        final boolean b = counterRotationBone != null && counterRotationBone.getBone() != null && counterRotationBone.getBone().Index == n;
        key.setIdentity();
        final AnimationTrack track = this.m_track;
        this.getTrackTransform(n, track, pos, rot, scale);
        if (b && track.getUseDeferredRotation()) {
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
        if (track.getDeferredMovementBoneIdx() == n) {
            final Vector3f currentDeferredCounterPosition = track.getCurrentDeferredCounterPosition(L_updateBoneAnimationTransform.deferredPos);
            final Vector3f vector3f = pos;
            vector3f.x += currentDeferredCounterPosition.x;
            final Vector3f vector3f2 = pos;
            vector3f2.y += currentDeferredCounterPosition.y;
            final Vector3f vector3f3 = pos;
            vector3f3.z += currentDeferredCounterPosition.z;
        }
        key.Position.set((ReadableVector3f)pos);
        key.Rotation.set((ReadableVector4f)rot);
        key.Scale.set((ReadableVector3f)scale);
        this.m_boneTransforms[n].set(key.Position, key.Rotation, key.Scale);
    }
    
    private void getTrackTransform(final int n, final AnimationTrack animationTrack, final Vector3f vector3f, final Quaternion quaternion, final Vector3f vector3f2) {
        animationTrack.get(n, vector3f, quaternion, vector3f2);
    }
    
    @Override
    public float getCurrentTime() {
        return this.m_currentTime;
    }
    
    @Override
    public void getBoneMatrix(final int n, final Matrix4f matrix4f) {
        if (n == 0) {
            this.m_boneTransforms[0].getMatrix(this.m_boneModelTransforms[0]);
            matrix4f.load(this.m_boneModelTransforms[0]);
            return;
        }
        final SkinningBone bone = this.m_skinningData.getBoneAt(n);
        BoneTransform.mul(this.m_boneTransforms[bone.Index], this.m_boneModelTransforms[bone.Parent.Index], this.m_boneModelTransforms[bone.Index]);
        matrix4f.load(this.m_boneModelTransforms[bone.Index]);
    }
    
    @Override
    public int getNumBones() {
        return this.m_skinningData.numBones();
    }
    
    static {
        s_pool = new Pool<ModelTransformSampler>(ModelTransformSampler::new);
    }
    
    public static class L_updateBoneAnimationTransform
    {
        public static final Vector3f pos;
        public static final Quaternion rot;
        public static final Vector3f scale;
        public static final Keyframe key;
        public static final Vector3f rotAxis;
        public static final Matrix4f rotMat;
        public static final Vector3f rotEulers;
        public static final Vector3f deferredPos;
        
        static {
            pos = new Vector3f();
            rot = new Quaternion();
            scale = new Vector3f();
            key = new Keyframe(new Vector3f(0.0f, 0.0f, 0.0f), new Quaternion(0.0f, 0.0f, 0.0f, 1.0f), new Vector3f(1.0f, 1.0f, 1.0f));
            rotAxis = new Vector3f();
            rotMat = new Matrix4f();
            rotEulers = new Vector3f();
            deferredPos = new Vector3f();
        }
    }
}
